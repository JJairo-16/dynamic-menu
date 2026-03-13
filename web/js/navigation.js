import {
  state,
  dom,
  ROOT_SUBSECTION,
  HOME_SECTION,
  PATH_COLLATOR,
  escapeHtml
} from './state.js';

let searchDebounceTimer = null;

export function sectionLabel(section) {
  return section === HOME_SECTION
    ? 'Inici'
    : section.replace(/^\d+-/, '').replaceAll('-', ' ');
}

export function getSubsection(doc) {
  const parts = doc.path.split('/');
  return parts.length > 2 ? parts[1] : null;
}

export function sortDocsByPath(docs) {
  docs.sort((a, b) => PATH_COLLATOR.compare(a.path, b.path));
  return docs;
}

export function ensureDocSearchIndex() {
  if (state.lowerIndexReady) return;

  for (const doc of state.docs) {
    doc._searchTitle ??= String(doc.title).toLowerCase();
    doc._searchPath ??= String(doc.path).toLowerCase();
    doc._searchSection ??= String(doc.section).toLowerCase();
  }

  state.lowerIndexReady = true;
}

export function getGroups(docs) {
  const groups = new Map();

  for (const doc of docs) {
    const section = doc.section;
    const subsection = getSubsection(doc) || ROOT_SUBSECTION;

    let sectionMap = groups.get(section);
    if (!sectionMap) {
      sectionMap = new Map();
      groups.set(section, sectionMap);
    }

    let subsectionDocs = sectionMap.get(subsection);
    if (!subsectionDocs) {
      subsectionDocs = [];
      sectionMap.set(subsection, subsectionDocs);
    }

    subsectionDocs.push(doc);
  }

  return groups;
}

export function orderSections(groups) {
  const sectionOrder = [];

  if (groups.has(HOME_SECTION)) {
    sectionOrder.push(HOME_SECTION);
  }

  for (const key of [...groups.keys()].sort(PATH_COLLATOR.compare)) {
    if (key !== HOME_SECTION) {
      sectionOrder.push(key);
    }
  }

  return sectionOrder;
}

export function renderNavLink(doc, extraClass = 'px-3') {
  return `
    <a
      class="nav-link block rounded-xl ${extraClass} py-2 text-sm font-medium text-slate-700"
      href="#/${encodeURI(doc.path)}"
      data-doc-path="${escapeHtml(doc.path)}"
    >
      ${escapeHtml(doc.title)}
    </a>
  `;
}

export function renderSubsectionBlock(subsection, subsectionDocs, currentPath) {
  const hasActiveDoc = subsectionDocs.some(doc => doc.path === currentPath);
  let docsHtml = '';

  for (const subDoc of subsectionDocs) {
    docsHtml += renderNavLink(subDoc, 'px-4');
  }

  return `
    <details class="subsection group space-y-1" ${hasActiveDoc ? 'open' : ''}>
      <summary class="flex cursor-pointer list-none items-center justify-between rounded-xl px-3 py-2 text-[11px] font-bold uppercase tracking-[0.16em] text-slate-400 transition hover:bg-slate-50 hover:text-slate-600">
        <span>${escapeHtml(sectionLabel(subsection))}</span>
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-4 w-4 shrink-0 transition-transform duration-300 ease-out group-open:rotate-90"
          viewBox="0 0 20 20"
          fill="currentColor"
          aria-hidden="true"
        >
          <path fill-rule="evenodd" d="M7.21 14.77a.75.75 0 0 1 .02-1.06L10.94 10 7.23 6.29a.75.75 0 1 1 1.06-1.06l4.24 4.24a.75.75 0 0 1 0 1.06l-4.24 4.24a.75.75 0 0 1-1.08-.02Z" clip-rule="evenodd" />
        </svg>
      </summary>

      <div class="subsection-content">
        <div class="subsection-content-inner space-y-1 pl-2">
          ${docsHtml}
        </div>
      </div>
    </details>
  `;
}

export function renderSectionContent(sectionMap, currentPath) {
  const sortedDocs = [];

  for (const docs of sectionMap.values()) {
    sortedDocs.push(...docs);
  }

  sortDocsByPath(sortedDocs);

  const renderedSubsections = new Set();
  let html = '';

  for (const doc of sortedDocs) {
    const subsection = getSubsection(doc) || ROOT_SUBSECTION;

    if (subsection === ROOT_SUBSECTION) {
      html += renderNavLink(doc);
      continue;
    }

    if (renderedSubsections.has(subsection)) {
      continue;
    }

    renderedSubsections.add(subsection);

    const subsectionDocs = sortDocsByPath([...(sectionMap.get(subsection) || [])]);
    html += renderSubsectionBlock(subsection, subsectionDocs, currentPath);
  }

  return html;
}

export function renderNav() {
  const groups = getGroups(state.filteredDocs);
  const sectionOrder = orderSections(groups);
  let navHtml = '';

  for (const section of sectionOrder) {
    const sectionMap = groups.get(section);
    if (!sectionMap) continue;

    navHtml += `
      <section class="nav-section">
        <h2 class="px-2 text-xs font-extrabold uppercase tracking-[0.18em] text-slate-400">
          ${escapeHtml(sectionLabel(section))}
        </h2>

        <div class="space-y-2">
          ${renderSectionContent(sectionMap, state.currentPath)}
        </div>
      </section>
    `;
  }

  dom.nav.innerHTML = navHtml;
  syncSubsectionAnimationState();
  highlightActiveLink();
}

export function syncSubsectionAnimationState() {
  const subsections = dom.nav.querySelectorAll('details.subsection');

  for (const details of subsections) {
    const content = details.querySelector('.subsection-content');
    if (!content) continue;

    if (details.open) {
      content.style.height = 'auto';
      content.style.opacity = '1';
      content.style.transform = 'translateY(0)';
      content.style.overflow = '';
      content.style.display = 'block';
    } else {
      content.style.height = '0px';
      content.style.opacity = '0';
      content.style.transform = 'translateY(-6px)';
      content.style.overflow = 'hidden';
      content.style.display = '';
    }
  }
}

export function animateSubsection(details, open) {
  const content = details.querySelector('.subsection-content');
  if (!content) return;

  content.style.overflow = 'hidden';

  if (open) {
    details.open = true;
    content.style.display = 'block';

    const endHeight = content.scrollHeight;

    content.style.height = '0px';
    content.style.opacity = '0';
    content.style.transform = 'translateY(-6px)';

    requestAnimationFrame(() => {
      content.style.height = `${endHeight}px`;
      content.style.opacity = '1';
      content.style.transform = 'translateY(0)';
    });

    const onEnd = event => {
      if (event.propertyName !== 'height') return;
      content.style.height = 'auto';
      content.style.overflow = '';
      content.removeEventListener('transitionend', onEnd);
    };

    content.addEventListener('transitionend', onEnd);
    return;
  }

  const startHeight = content.scrollHeight;
  content.style.height = `${startHeight}px`;
  content.style.opacity = '1';
  content.style.transform = 'translateY(0)';

  requestAnimationFrame(() => {
    content.style.height = '0px';
    content.style.opacity = '0';
    content.style.transform = 'translateY(-6px)';
  });

  const onEnd = event => {
    if (event.propertyName !== 'height') return;
    details.open = false;
    content.style.display = '';
    content.style.overflow = '';
    content.removeEventListener('transitionend', onEnd);
  };

  content.addEventListener('transitionend', onEnd);
}

export function renderBreadcrumbs() {
  const current = state.docByPath.get(state.currentPath);

  if (!current) {
    dom.breadcrumbs.innerHTML = '';
    return;
  }

  dom.breadcrumbs.innerHTML = `
    <span class="font-semibold text-slate-700">${escapeHtml(current.title)}</span>
  `;
}

export function renderDocNav() {
  const index = state.docs.findIndex(doc => doc.path === state.currentPath);
  const prev = index > 0 ? state.docs[index - 1] : null;
  const next = index >= 0 && index < state.docs.length - 1 ? state.docs[index + 1] : null;

  dom.docNav.innerHTML = [prev, next]
    .map((doc, i) => {
      if (!doc) return '<div></div>';

      const label = i === 0 ? 'Anterior' : 'Següent';

      return `
        <a
          href="#/${encodeURI(doc.path)}"
          class="group rounded-2xl border border-slate-200 p-4 transition hover:border-blue-300 hover:bg-blue-50/50"
        >
          <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">${label}</p>
          <p class="mt-1 font-semibold text-slate-900 group-hover:text-blue-700">
            ${escapeHtml(doc.title)}
          </p>
        </a>
      `;
    })
    .join('');
}

export function highlightActiveLink() {
  const links = dom.nav.querySelectorAll('.nav-link');

  for (const link of links) {
    link.classList.toggle('active', link.dataset.docPath === state.currentPath);
  }
}

export function openActiveSubsection() {
  const activeLink = dom.nav.querySelector(`.nav-link[data-doc-path="${CSS.escape(state.currentPath)}"]`);
  if (!activeLink) return;

  const details = activeLink.closest('details.subsection');
  if (!details || details.open) return;

  details.open = true;

  const content = details.querySelector('.subsection-content');
  if (!content) return;

  content.style.height = 'auto';
  content.style.opacity = '1';
  content.style.transform = 'translateY(0)';
  content.style.overflow = '';
  content.style.display = 'block';
}

export function filterDocs(query) {
  if (!query) return state.docs;

  ensureDocSearchIndex();

  return state.docs.filter(doc =>
    doc._searchTitle.includes(query) ||
    doc._searchPath.includes(query) ||
    doc._searchSection.includes(query)
  );
}

export function handleSearchInput() {
  clearTimeout(searchDebounceTimer);

  searchDebounceTimer = setTimeout(() => {
    const query = dom.searchInput.value.trim().toLowerCase();
    state.filteredDocs = filterDocs(query);
    renderNav();
  }, 100);
}

export function handleNavClick(event) {
  const summary = event.target.closest('summary');
  if (!summary || !dom.nav.contains(summary)) return;

  const details = summary.parentElement;
  if (!details?.classList.contains('subsection')) return;

  event.preventDefault();
  animateSubsection(details, !details.open);
}