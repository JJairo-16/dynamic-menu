import { state, dom, normalizeContentRoot, openMenu, closeMenu } from './js/state.js';
import { handleContentClick, waitForShiki } from './js/code.js';
import {
  sortDocsByPath,
  renderNav,
  handleSearchInput,
  handleNavClick
} from './js/navigation.js';
import {
  loadRoute,
  showLocalOpenMessage,
  isFileProtocol,
  canLoadDocs
} from './js/content.js';

function wireEvents() {
  globalThis.addEventListener('hashchange', loadRoute);
  dom.searchInput.addEventListener('input', handleSearchInput);
  dom.menuButton.addEventListener('click', openMenu);
  dom.closeMenuButton.addEventListener('click', closeMenu);
  dom.overlay.addEventListener('click', closeMenu);
  dom.nav.addEventListener('click', handleNavClick);
  dom.content.addEventListener('click', handleContentClick);
}

async function init() {
  const manifest = await fetch('./manifest.json').then(response => response.json());

  state.contentRoot = normalizeContentRoot(manifest.contentRoot);
  state.docs = Array.isArray(manifest.docs) ? manifest.docs.slice() : [];

  state.docByPath = new Map();
  state.docIndexByPath = new Map();

  state.docs.forEach((doc, index) => {
    state.docByPath.set(doc.path, doc);
    state.docIndexByPath.set(doc.path, index);
  });

  state.filteredDocs = state.docs;
  state.manifestLoaded = true;

  wireEvents();
  renderNav();
  await loadRoute();

  waitForShiki().catch(error => {
    console.error('No s\'ha pogut inicialitzar Shiki.', error);
  });
}

(async () => {
  if (isFileProtocol()) {
    const ok = await canLoadDocs();

    if (!ok) {
      showLocalOpenMessage();
      return;
    }
  }

  init().catch(error => {
    console.error(error);
    showLocalOpenMessage();
  });
})();