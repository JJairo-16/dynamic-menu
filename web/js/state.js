export const DEFAULT_DOC_PATH = 'index.md';
export const NOT_FOUND_MARKDOWN = '# No trobat\n\nAquest document no existeix.';
export const ROOT_SUBSECTION = '_root';
export const HOME_SECTION = 'Home';
export const SHIKI_THEME = 'dark-plus';

export const SECTION_SORT_OPTIONS = { numeric: true, sensitivity: 'base' };
export const PATH_COLLATOR = new Intl.Collator(undefined, SECTION_SORT_OPTIONS);

export const HTML_ESCAPE_MAP = {
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;',
  "'": '&#39;'
};

export const LANGUAGE_MAP = {
  js: 'javascript',
  shell: 'bash',
  sh: 'bash',
  yml: 'yaml',
  md: 'markdown',
  plaintext: 'text',
  txt: 'text'
};

export const COPY_BUTTON_LANGS = new Set([
  'java',
  'powershell',
  'bash',
  'xml',
  'groovy'
]);

export const WARNING_TOKEN = '[!WARNING]';
export const WARNING_REPLACEMENT = '⚠️';
export const CONTENT_ROOT_FALLBACK = './content';

export const state = {
  docs: [],
  filteredDocs: [],
  currentPath: DEFAULT_DOC_PATH,
  contentRoot: 'content',
  lowerIndexReady: false,
  manifestLoaded: false,
  docByPath: new Map(),
  docIndexByPath: new Map()
};

export const dom = {
  nav: document.getElementById('nav'),
  content: document.getElementById('content'),
  breadcrumbs: document.getElementById('breadcrumbs'),
  docNav: document.getElementById('docNav'),
  searchInput: document.getElementById('searchInput'),
  searchWrapper: document.getElementById('searchWrapper'),
  sidebar: document.getElementById('sidebar'),
  overlay: document.getElementById('overlay'),
  menuButton: document.getElementById('menuButton'),
  closeMenuButton: document.getElementById('closeMenuButton'),
  layout: document.getElementById('layout'),
  contentCard: document.getElementById('contentCard')
};

export function escapeHtml(value) {
  return String(value).replaceAll(/[&<>"']/g, char => HTML_ESCAPE_MAP[char]);
}

export function normalizeContentRoot(root) {
  if (!root) return CONTENT_ROOT_FALLBACK;

  const value = String(root).trim();
  return value || CONTENT_ROOT_FALLBACK;
}

export function buildContentUrl(docPath) {
  const root = normalizeContentRoot(state.contentRoot);
  const cleanPath = String(docPath).replace(/^\/+/, '');
  return new URL(cleanPath, new URL(`${root.replace(/\/?$/, '/')}`, globalThis.location.href)).toString();
}

export function currentPathFromHash() {
  const raw = decodeURIComponent(location.hash.replace(/^#\/?/, '').trim());
  return raw || DEFAULT_DOC_PATH;
}

export function resolveDocPath(currentPath, href) {
  const currentDocUrl = buildContentUrl(currentPath);
  const resolvedUrl = new URL(href, currentDocUrl);
  const rootUrl = new URL(`${normalizeContentRoot(state.contentRoot).replace(/\/?$/, '/')}`, globalThis.location.href);

  let relativePath = resolvedUrl.pathname.slice(rootUrl.pathname.length);

  if (relativePath.startsWith('/')) {
    relativePath = relativePath.slice(1);
  }

  return relativePath;
}

export function normalizeLanguage(lang) {
  if (!lang) return 'text';

  const normalized = lang.toLowerCase();
  return LANGUAGE_MAP[normalized] || normalized;
}

export function openMenu() {
  dom.sidebar.classList.remove('-translate-x-full');
  dom.overlay.classList.remove('hidden');
}

export function closeMenu() {
  dom.sidebar.classList.add('-translate-x-full');
  dom.overlay.classList.add('hidden');
}