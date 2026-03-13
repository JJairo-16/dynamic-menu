import { marked } from 'https://cdn.jsdelivr.net/npm/marked/lib/marked.esm.js';

import {
  state,
  dom,
  DEFAULT_DOC_PATH,
  NOT_FOUND_MARKDOWN,
  WARNING_TOKEN,
  WARNING_REPLACEMENT,
  buildContentUrl,
  currentPathFromHash,
  resolveDocPath,
  closeMenu
} from './state.js';

import { highlightCodeBlocks } from './code.js';
import {
  renderBreadcrumbs,
  highlightActiveLink,
  openActiveSubsection
} from './navigation.js';

const MAX_PAGE_CACHE = 8;
const pageCache = new Map();
let routeRequestId = 0;

marked.setOptions({
  gfm: true,
  breaks: false
});

function setCachedPage(path, value) {
  if (pageCache.has(path)) {
    pageCache.delete(path);
  }

  pageCache.set(path, value);

  if (pageCache.size > MAX_PAGE_CACHE) {
    const oldestKey = pageCache.keys().next().value;
    pageCache.delete(oldestKey);
  }
}

export function rewriteLinks(markdown, currentPath) {
  return markdown.replaceAll(/\[([^\]]+)\]\(([^)]+\.md)\)/g, (_, label, href) => {
    const resolved = resolveDocPath(currentPath, href);
    return `[${label}](#/${resolved})`;
  });
}

export function replaceMarkdownTokens(markdown) {
  return markdown.replaceAll(WARNING_TOKEN, WARNING_REPLACEMENT);
}

export function processMarkdown(markdown, currentPath) {
  let result = markdown;
  result = rewriteLinks(result, currentPath);
  result = replaceMarkdownTokens(result);
  return result;
}

export function enhanceExternalLinks() {
  const links = dom.content.querySelectorAll('a[href]');

  for (const link of links) {
    const href = link.getAttribute('href') || '';
    if (href.startsWith('http://') || href.startsWith('https://')) {
      link.target = '_blank';
      link.rel = 'noreferrer noopener';
    }
  }
}

export function wrapTables() {
  const tables = dom.content.querySelectorAll('table');

  for (const table of tables) {
    if (table.parentElement?.classList.contains('table-wrapper')) continue;

    const wrapper = document.createElement('div');
    wrapper.className = 'table-wrapper';
    table.parentNode.insertBefore(wrapper, table);
    wrapper.appendChild(table);
  }
}

export async function loadRoute() {
  const requestId = ++routeRequestId;
  const path = currentPathFromHash();
  state.currentPath = state.docByPath.has(path) ? path : DEFAULT_DOC_PATH;

  let cached = pageCache.get(state.currentPath);

  if (!cached) {
    const response = await fetch(buildContentUrl(state.currentPath));
    if (requestId !== routeRequestId) return;

    const markdown = response.ok ? await response.text() : NOT_FOUND_MARKDOWN;
    if (requestId !== routeRequestId) return;

    const processedMarkdown = processMarkdown(markdown, state.currentPath);
    const html = marked.parse(processedMarkdown);

    cached = {
      html,
      highlightedHtml: null,
      hasCode: /<pre><code|<pre class=|language-/.test(html)
    };
    setCachedPage(state.currentPath, cached);

  }

  dom.content.innerHTML = cached.highlightedHtml || cached.html;
  const alreadyHighlighted = !!cached.highlightedHtml;

  wrapTables();
  renderBreadcrumbs();
  highlightActiveLink();
  openActiveSubsection();
  enhanceExternalLinks();
  closeMenu();

  window.scrollTo({ top: 0, behavior: 'auto' });

  if (!alreadyHighlighted && cached.hasCode) {
    scheduleCodeHighlight(requestId);
  }
}

function scheduleCodeHighlight(requestId) {
  if (!dom.content.querySelector('pre code')) return;

  const run = () => {
    if (requestId !== routeRequestId) return;

    highlightCodeBlocks()
      .then(() => {
        if (requestId !== routeRequestId) return;

        const entry = pageCache.get(state.currentPath);
        if (entry) {
          entry.highlightedHtml = dom.content.innerHTML;
          entry.html = null;
        }
      })
      .catch(error => {
        console.error('No s\'ha pogut ressaltar el codi.', error);
      });
  };

  if ('requestIdleCallback' in globalThis) {
    globalThis.requestIdleCallback(run, { timeout: 300 });
  } else {
    setTimeout(run, 0);
  }
}

export function showLocalOpenMessage() {
  document.body.classList.add('error-mode');
  closeMenu();

  if (dom.sidebar) dom.sidebar.style.display = 'none';
  if (dom.overlay) dom.overlay.style.display = 'none';
  if (dom.menuButton) dom.menuButton.style.display = 'none';
  if (dom.searchWrapper) dom.searchWrapper.style.display = 'none';
  if (dom.breadcrumbs) dom.breadcrumbs.style.display = 'none';
  if (dom.docNav) dom.docNav.style.display = 'none';
  if (dom.nav) dom.nav.style.display = 'none';

  if (dom.layout) {
    dom.layout.classList.remove('lg:grid-cols-[300px_minmax(0,1fr)]');
    dom.layout.classList.add('lg:grid-cols-1');
  }

  if (dom.contentCard) {
    dom.contentCard.classList.add('max-w-4xl', 'mx-auto');
  }

  dom.content.innerHTML = `
    <div class="rounded-3xl border border-rose-200 bg-rose-50 p-6 text-rose-900 shadow-sm sm:p-8">
      <strong class="mb-4 block text-xl font-extrabold">
        No s'ha pogut carregar la documentació.
      </strong>

      <p class="mb-4 leading-8">
        Aquesta pàgina no es pot obrir directament fent doble clic a
        <code class="rounded-md bg-indigo-100 px-2 py-1 text-sm text-indigo-900">index.html</code>,
        perquè el navegador bloqueja la càrrega dels fitxers locals necessaris.
      </p>

      <p class="mb-3 font-bold">
        Com obrir-la correctament:
      </p>

      <ol class="mb-4 list-decimal space-y-2 pl-6">
        <li>Obre una terminal dins la carpeta del projecte.</li>
        <li>Executa:</li>
      </ol>

      <pre class="mb-6 overflow-x-auto rounded-2xl bg-slate-950 px-4 py-4 text-slate-50 shadow-lg"><code>python -m http.server 8000</code></pre>

      <p class="mb-3 leading-8">
        Després obre el navegador a:
      </p>

      <pre class="mb-6 overflow-x-auto rounded-2xl bg-slate-950 px-4 py-4 text-slate-50 shadow-lg"><code>http://localhost:8000</code></pre>

      <p class="mb-3 font-bold">
        Alternativa amb VSCode:
      </p>

      <ol class="list-decimal space-y-2 pl-6">
        <li>Obre la carpeta del projecte amb <strong>Visual Studio Code</strong>.</li>
        <li>Instal·la l'extensió <strong>Live Server</strong>.</li>
        <li>Fes clic dret a <code class="rounded-md bg-indigo-100 px-2 py-1 text-sm text-indigo-900">index.html</code> i selecciona <strong>Open with Live Server</strong>.</li>
      </ol>
    </div>
  `;
}

export function isFileProtocol() {
  return location.protocol === 'file:';
}

export async function canLoadDocs() {
  try {
    const response = await fetch('./manifest.json', { method: 'HEAD' });
    return response.ok;
  } catch {
    return false;
  }
}