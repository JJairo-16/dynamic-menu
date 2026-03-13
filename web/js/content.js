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
  closeMenu,
  escapeHtml
} from './state.js';

import { highlightCodeBlocks } from './code.js';
import {
  renderBreadcrumbs,
  renderDocNav,
  highlightActiveLink,
  openActiveSubsection,
  setCurrentPageHeadingsFromDom,
  clearSearch
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

function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function renderNotFoundMarkdown(path) {
  return `${NOT_FOUND_MARKDOWN}

\`Ruta sol·licitada: ${path}\``;
}

function renderNotFoundHtml(path) {
  return `
    <div class="not-found-page">
      <div class="not-found-card">
        <span class="not-found-eyebrow">Pàgina no trobada</span>
        <h1>No trobat</h1>
        <p>
          No s'ha trobat el document que has intentat obrir. Revisa l'enllaç o torna a una pàgina existent des del menú lateral.
        </p>
        <div class="not-found-path">${escapeHtml(path)}</div>
      </div>
    </div>
  `;
}

async function transitionContent(html, skipTransition = false) {
  if (!dom.contentCard || skipTransition) {
    dom.content.innerHTML = html;
    return;
  }

  dom.contentCard.classList.add('page-transition-out');
  await wait(120);
  dom.content.innerHTML = html;
  dom.contentCard.classList.remove('page-transition-out');
  dom.contentCard.classList.add('page-transition-in');

  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      dom.contentCard.classList.remove('page-transition-in');
    });
  });
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

function getStickyOffset() {
  const header = document.querySelector('header');
  return (header?.offsetHeight || 0) + 18;
}

export function smoothScrollToHeadingElement(element) {
  if (!element) return;

  const top = window.scrollY + element.getBoundingClientRect().top - getStickyOffset();
  window.scrollTo({ top: Math.max(0, top), behavior: 'smooth' });
}

export function navigateToCurrentHeading(slug) {
  const target = document.getElementById(slug);
  if (!target) return false;

  target.classList.add('heading-targeted');
  globalThis.setTimeout(() => {
    target.classList.remove('heading-targeted');
  }, 1200);

  smoothScrollToHeadingElement(target);
  return true;
}

export async function loadRoute(options = {}) {
  const { skipTransition = false } = options;
  const requestId = ++routeRequestId;
  const path = currentPathFromHash();
  state.currentPath = path || DEFAULT_DOC_PATH;

  let cached = pageCache.get(state.currentPath);

  if (!cached) {
    const response = await fetch(buildContentUrl(state.currentPath));
    if (requestId !== routeRequestId) return;

    const isNotFound = !response.ok;
    const markdown = isNotFound ? renderNotFoundMarkdown(state.currentPath) : await response.text();
    if (requestId !== routeRequestId) return;

    const processedMarkdown = processMarkdown(markdown, state.currentPath);
    const html = isNotFound ? renderNotFoundHtml(state.currentPath) : marked.parse(processedMarkdown);

    cached = {
      html,
      highlightedHtml: null,
      hasCode: !isNotFound && /<pre><code|<pre class=|language-/.test(html),
      isNotFound
    };
    setCachedPage(state.currentPath, cached);
  }

  await transitionContent(cached.highlightedHtml || cached.html, skipTransition);
  if (requestId !== routeRequestId) return;

  const alreadyHighlighted = !!cached.highlightedHtml;

  wrapTables();
  renderBreadcrumbs(cached.isNotFound);
  renderDocNav(cached.isNotFound);
  highlightActiveLink();
  openActiveSubsection();
  enhanceExternalLinks();
  setCurrentPageHeadingsFromDom();
  closeMenu();
  clearSearch(true);

  window.scrollTo({ top: 0, behavior: skipTransition ? 'auto' : 'smooth' });

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
