import {
  dom,
  SHIKI_THEME,
  COPY_BUTTON_LANGS,
  normalizeLanguage
} from './state.js';

let shikiReadyPromise = null;

export function waitForShiki() {
  if (globalThis.shikiHighlighter) {
    return Promise.resolve(globalThis.shikiHighlighter);
  }

  if (shikiReadyPromise) {
    return shikiReadyPromise;
  }

  shikiReadyPromise = import('https://esm.sh/shiki@1.29.2')
    .then(async ({ createHighlighter }) => {
      const highlighter = await createHighlighter({
        themes: ['dark-plus'],
        langs: [
          'java',
          'bash',
          'powershell',
          'xml',
          'groovy',
          'json',
          'markdown',
          'text'
        ]
      });

      globalThis.shikiHighlighter = highlighter;
      return highlighter;
    });

  return shikiReadyPromise;
}

export function canShowCopyButton(rawLang) {
  return !!rawLang && COPY_BUTTON_LANGS.has(rawLang.toLowerCase());
}

function formatLanguageLabel(rawLang) {
  if (!rawLang) return 'TEXT';

  const labels = {
    js: 'JAVASCRIPT',
    ts: 'TYPESCRIPT',
    bash: 'BASH',
    sh: 'SHELL',
    shell: 'SHELL',
    powershell: 'POWERSHELL',
    xml: 'XML',
    groovy: 'GROOVY',
    json: 'JSON',
    md: 'MARKDOWN',
    markdown: 'MARKDOWN',
    yml: 'YAML',
    yaml: 'YAML',
    txt: 'TEXT',
    plaintext: 'TEXT',
    text: 'TEXT',
    java: 'JAVA'
  };

  const normalized = rawLang.toLowerCase();
  return labels[normalized] || normalized.toUpperCase();
}

function createCodeToolbar(rawLang, code, showCopyButton) {
  const toolbar = document.createElement('div');
  toolbar.className = 'code-toolbar';

  const meta = document.createElement('div');
  meta.className = 'code-toolbar-meta';

  const lang = document.createElement('span');
  lang.className = 'code-lang';
  lang.textContent = formatLanguageLabel(rawLang);

  meta.appendChild(lang);
  toolbar.appendChild(meta);

  if (showCopyButton) {
    toolbar.appendChild(createCopyButton(code));
  }

  return toolbar;
}

export async function highlightCodeBlocks() {
  await waitForShiki();

  const highlighter = globalThis.shikiHighlighter;
  if (!highlighter) return;

  const blocks = dom.content.querySelectorAll('pre code');

  for (const block of blocks) {
    if (block.dataset.shikiDone === 'true') continue;

    block.dataset.shikiDone = 'true';

    const langClass = [...block.classList].find(className => className.startsWith('language-'));
    const rawLang = langClass ? langClass.slice(9) : 'text';
    const lang = normalizeLanguage(rawLang);
    const code = block.textContent || '';
    const showCopyButton = canShowCopyButton(rawLang);

    try {
      const html = highlighter.codeToHtml(code, {
        lang,
        theme: SHIKI_THEME
      });

      const temp = document.createElement('div');
      temp.innerHTML = html.trim();
      const shikiNode = temp.firstElementChild;
      if (!shikiNode) continue;

      const wrapper = document.createElement('div');
      wrapper.className = 'shiki-wrapper';

      const toolbar = createCodeToolbar(rawLang, code, showCopyButton);

      wrapper.appendChild(toolbar);
      wrapper.appendChild(shikiNode);
      block.parentElement.replaceWith(wrapper);
    } catch (error) {
      console.warn(`No s'ha pogut ressaltar el bloc com ${lang}.`, error);

      const pre = block.parentElement;
      if (!pre) continue;

      const wrapper = document.createElement('div');
      wrapper.className = 'shiki-wrapper';

      const toolbar = createCodeToolbar(rawLang, code, showCopyButton);
      wrapper.appendChild(toolbar);
      wrapper.appendChild(pre.cloneNode(true));
      pre.replaceWith(wrapper);
    }
  }
}

export function createCopyButton(code) {
  const button = document.createElement('button');
  button.type = 'button';
  button.className = 'code-copy-button';
  button.setAttribute('aria-label', 'Copiar codi');
  button.setAttribute('title', 'Copiar');

  button.innerHTML = `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      class="code-copy-icon"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      aria-hidden="true"
    >
      <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
      <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
    </svg>
    <span class="code-copy-label">Copiar</span>
  `;

  return button;
}

export async function handleCopyButtonClick(button) {
  const labelEl = button.querySelector('.code-copy-label');

  const wrapper = button.closest('.shiki-wrapper');
  const codeElement = wrapper?.querySelector('code');
  const code = codeElement?.textContent || '';

  try {
    await navigator.clipboard.writeText(code);
    button.classList.add('copied');
    button.setAttribute('title', 'Copiat');
    if (labelEl) labelEl.textContent = 'Copiat';

    setTimeout(() => {
      button.classList.remove('copied');
      button.setAttribute('title', 'Copiar');
      if (labelEl) labelEl.textContent = 'Copiar';
    }, 1400);
  } catch (error) {
    console.error('No s\'ha pogut copiar el codi.', error);
    button.setAttribute('title', 'Error');
    if (labelEl) labelEl.textContent = 'Error';

    setTimeout(() => {
      button.setAttribute('title', 'Copiar');
      if (labelEl) labelEl.textContent = 'Copiar';
    }, 1400);
  }
}

export function handleContentClick(event) {
  const button = event.target.closest('.code-copy-button');
  if (!button || !dom.content.contains(button)) return;

  handleCopyButtonClick(button);
}