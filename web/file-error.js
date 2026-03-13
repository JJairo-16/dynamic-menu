const dom = {
  sidebar: document.getElementById('sidebar'),
  overlay: document.getElementById('overlay'),
  menuButton: document.getElementById('menuButton'),
  desktopMenuButton: document.getElementById('desktopMenuButton'),
  searchWrapper: document.getElementById('searchWrapper'),
  breadcrumbs: document.getElementById('breadcrumbs'),
  docNav: document.getElementById('docNav'),
  nav: document.getElementById('nav'),
  layout: document.getElementById('layout'),
  contentCard: document.getElementById('contentCard'),
  content: document.getElementById('content')
};

function closeMenu() {
  document.body.classList.remove('menu-open');
}

function showLocalOpenMessage() {
  document.body.classList.add('error-mode');
  closeMenu();

  if (dom.sidebar) dom.sidebar.style.display = 'none';
  if (dom.overlay) dom.overlay.style.display = 'none';
  if (dom.menuButton) dom.menuButton.style.display = 'none';
  if (dom.desktopMenuButton) dom.desktopMenuButton.style.display = 'none';
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

function showLocalOpenMessageIfNeeded() {
  if (location.protocol === 'file:') {
    showLocalOpenMessage();
  }
}

showLocalOpenMessageIfNeeded();
