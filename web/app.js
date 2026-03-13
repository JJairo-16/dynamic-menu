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
  state.docs = Array.isArray(manifest.docs) ? sortDocsByPath(manifest.docs.slice()) : [];

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
