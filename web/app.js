import {
  state,
  dom,
  normalizeContentRoot,
  openMenu,
  closeMenu,
  toggleDesktopSidebar,
  restoreDesktopSidebarState,
  isDesktopViewport
} from './js/state.js';
import { handleContentClick, waitForShiki } from './js/code.js';
import {
  sortDocsByPath,
  renderNav,
  handleSearchInput,
  handleNavClick,
  initializeSearchUi,
  handleSearchFocus,
  handleSearchBlur,
  handleSearchKeydown,
  warmHeadingIndexInBackground
} from './js/navigation.js';
import {
  loadRoute
} from './js/content.js';

function stripNumericPrefix(value) {
  return String(value || '').replace(/^\d+-/, '');
}

function prettifyLabel(value) {
  return stripNumericPrefix(value)
    .replace(/\.md$/i, '')
    .replaceAll(/[-_]+/g, ' ')
    .trim();
}

function toTitleCase(value) {
  return String(value || '')
    .split(' ')
    .filter(Boolean)
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
}

function inferTitleFromPath(path) {
  const parts = String(path || '').split('/').filter(Boolean);
  const fileName = parts.at(-1) || 'index.md';
  const baseName = fileName.toLowerCase() === 'index.md' && parts.length > 1
    ? parts.at(-2)
    : fileName;

  return toTitleCase(prettifyLabel(baseName)) || 'Sense títol';
}

function inferSectionFromPath(path) {
  const parts = String(path || '').split('/').filter(Boolean);

  if (!parts.length) {
    return 'Home';
  }

  if (parts.length === 1 && parts[0].toLowerCase() === 'index.md') {
    return 'Home';
  }

  return parts[0];
}

function hydrateDoc(doc) {
  const path = String(doc?.path || '').trim();

  if (!path) {
    return null;
  }

  return {
    ...doc,
    path,
    title: String(doc.title || '').trim() || inferTitleFromPath(path),
    section: String(doc.section || '').trim() || inferSectionFromPath(path)
  };
}

function handleViewportChange() {
  if (isDesktopViewport()) {
    closeMenu();
  }
}

function wireEvents() {
  globalThis.addEventListener('hashchange', loadRoute);
  globalThis.addEventListener('resize', handleViewportChange, { passive: true });
  dom.searchInput.addEventListener('input', handleSearchInput);
  dom.searchInput.addEventListener('focus', handleSearchFocus);
  dom.searchInput.addEventListener('blur', handleSearchBlur);
  dom.searchInput.addEventListener('keydown', handleSearchKeydown);
  dom.menuButton.addEventListener('click', openMenu);
  dom.desktopMenuButton?.addEventListener('click', toggleDesktopSidebar);
  dom.closeMenuButton.addEventListener('click', closeMenu);
  dom.overlay.addEventListener('click', closeMenu);
  dom.nav.addEventListener('click', handleNavClick);
  dom.content.addEventListener('click', handleContentClick);
}

async function init() {
  const manifest = await fetch('./manifest.json').then(response => response.json());

  state.contentRoot = normalizeContentRoot(manifest.contentRoot);
  state.docs = Array.isArray(manifest.docs)
    ? sortDocsByPath(
      manifest.docs
        .map(hydrateDoc)
        .filter(Boolean)
    )
    : [];

  state.docByPath = new Map();
  state.docIndexByPath = new Map();

  state.docs.forEach((doc, index) => {
    state.docByPath.set(doc.path, doc);
    state.docIndexByPath.set(doc.path, index);
  });

  state.filteredDocs = state.docs;
  state.manifestLoaded = true;

  restoreDesktopSidebarState();
  initializeSearchUi();
  wireEvents();
  renderNav();
  await loadRoute({ skipTransition: true });
  warmHeadingIndexInBackground();

  waitForShiki().catch(error => {
    console.error('No s\'ha pogut inicialitzar Shiki.', error);
  });
}

(async () => {
  init().catch(error => {
    console.error(error);
  });
})();
