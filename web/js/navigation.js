import {
  state,
  dom,
  ROOT_SUBSECTION,
  HOME_SECTION,
  PATH_COLLATOR,
  escapeHtml
} from './state.js';
import { getDocSource } from './doc-source.js';
import { navigateToCurrentHeading } from './content.js';

let searchDebounceTimer = null;
let searchUiReady = false;
let headingWarmupStarted = false;

const SEARCH_RESULT_LIMIT = 3;
const SEARCH_UI_BLUR_DELAY = 120;

function getActiveSearchQuery() {
  return normalizeSearchText(dom.searchInput?.value.trim() || '');
}

function isSearchActive() {
  return !!getActiveSearchQuery();
}

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

function normalizeSearchText(value) {
  return String(value || '').toLowerCase().normalize('NFD').replaceAll(/[\u0300-\u036f]/g, '');
}

function slugifyHeading(value) {
  return String(value || '')
    .toLowerCase()
    .normalize('NFD')
    .replaceAll(/[\u0300-\u036f]/g, '')
    .replaceAll(/[^\w\s-]/g, '')
    .trim()
    .replaceAll(/\s+/g, '-')
    .replaceAll(/-+/g, '-');
}

function extractHeadings(markdown) {
  const headings = [];
  const seenSlugs = new Map();
  const regex = /^(#{1,6})\s+(.+)$/gm;
  let match;

  while ((match = regex.exec(markdown))) {
    const rawText = match[2]
      .replaceAll(/`([^`]+)`/g, '$1')
      .replaceAll(/\[(.*?)\]\((.*?)\)/g, '$1')
      .replaceAll(/[*_~]/g, '')
      .trim();

    if (!rawText) continue;

    const baseSlug = slugifyHeading(rawText);
    if (!baseSlug) continue;

    const count = seenSlugs.get(baseSlug) || 0;
    seenSlugs.set(baseSlug, count + 1);

    headings.push({
      text: rawText,
      slug: count ? `${baseSlug}-${count}` : baseSlug,
      level: match[1].length,
      _searchText: normalizeSearchText(rawText)
    });
  }

  return headings;
}

export function setCurrentPageHeadingsFromDom() {
  const headings = [];
  const seenSlugs = new Set();

  for (const element of dom.content.querySelectorAll('h1, h2, h3, h4, h5, h6')) {
    const text = element.textContent?.trim();
    if (!text) continue;

    let slug = element.id || slugifyHeading(text);
    if (!slug) continue;

    if (seenSlugs.has(slug)) {
      let index = 1;
      while (seenSlugs.has(`${slug}-${index}`)) index += 1;
      slug = `${slug}-${index}`;
    }

    seenSlugs.add(slug);
    if (!element.id || element.id !== slug) {
      element.id = slug;
    }

    headings.push({
      text,
      slug,
      level: Number(element.tagName.slice(1)),
      _searchText: normalizeSearchText(text)
    });
  }

  state.headingIndex.set(state.currentPath, headings);
}

async function ensureDocHeadingIndex(doc) {
  if (!doc || state.headingIndex.has(doc.path)) {
    return state.headingIndex.get(doc.path) || [];
  }

  try {
    const source = await getDocSource(doc.path);

    if (source.isNotFound) {
      state.headingIndex.set(doc.path, []);
      return [];
    }

    source.headings ??= extractHeadings(source.markdown);
    state.headingIndex.set(doc.path, source.headings);
    return source.headings;
  } catch {
    state.headingIndex.set(doc.path, []);
    return [];
  }
}

export function ensureDocSearchIndex() {
  if (state.lowerIndexReady) return;

  for (const doc of state.docs) {
    doc._searchTitle ??= normalizeSearchText(doc.title);
    doc._searchPath ??= normalizeSearchText(doc.path);
    doc._searchSection ??= normalizeSearchText(doc.section);
  }

  state.lowerIndexReady = true;
}

export function warmHeadingIndexInBackground() {
  if (headingWarmupStarted) return;
  headingWarmupStarted = true;

  const queue = [...state.docs];

  const runNext = async deadline => {
    while (queue.length) {
      if (deadline && typeof deadline.timeRemaining === 'function' && deadline.timeRemaining() < 8) {
        break;
      }

      const doc = queue.shift();
      await ensureDocHeadingIndex(doc);
    }

    if (!queue.length) return;

    if ('requestIdleCallback' in globalThis) {
      globalThis.requestIdleCallback(runNext, { timeout: 500 });
    } else {
      setTimeout(() => runNext(), 120);
    }
  };

  if ('requestIdleCallback' in globalThis) {
    globalThis.requestIdleCallback(runNext, { timeout: 500 });
  } else {
    setTimeout(() => runNext(), 120);
  }
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
  const isActive = doc.path === state.currentPath;

  return `
    <a
      class="nav-link block rounded-xl ${extraClass} py-2 text-sm font-medium text-slate-700"
      href="#/${encodeURI(doc.path)}"
      data-doc-path="${escapeHtml(doc.path)}"
      ${isActive ? 'aria-current="page"' : ''}
    >
      ${escapeHtml(doc.title)}
    </a>
  `;
}

function renderSearchContextualLink(doc, subsection) {
  return `
    <a
      class="nav-link nav-link--search-context block rounded-xl px-3 py-2 text-sm font-medium text-slate-700"
      href="#/${encodeURI(doc.path)}"
      data-doc-path="${escapeHtml(doc.path)}"
      data-search-subsection="${escapeHtml(subsection)}"
    >
      <span class="nav-link__eyebrow">${escapeHtml(sectionLabel(subsection))}</span>
      <span class="nav-link__text">${escapeHtml(doc.title)}</span>
    </a>
  `;
}

export function renderSubsectionBlock(subsection, subsectionDocs, currentPath) {
  const hasActiveDoc = subsectionDocs.some(doc => doc.path === currentPath);
  const searching = isSearchActive();

  if (searching && subsectionDocs.length === 1 && !hasActiveDoc) {
    return renderSearchContextualLink(subsectionDocs[0], subsection);
  }

  let docsHtml = '';
  for (const subDoc of subsectionDocs) {
    docsHtml += renderNavLink(subDoc, 'px-4');
  }

  const shouldOpen = hasActiveDoc || searching;
  const resultsBadge = searching
    ? `<span class="subsection-match-count">${subsectionDocs.length}</span>`
    : '';

  return `
    <details class="subsection group space-y-1" ${shouldOpen ? 'open' : ''} data-search-open="${searching ? 'true' : 'false'}">
      <summary class="flex cursor-pointer list-none items-center justify-between rounded-xl px-3 py-2 text-[11px] font-bold uppercase tracking-[0.16em] text-slate-400 transition hover:bg-slate-50 hover:text-slate-600">
        <span class="subsection-summary-label">
          <span>${escapeHtml(sectionLabel(subsection))}</span>
          ${resultsBadge}
        </span>
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

function revealRelevantSearchResult() {
  if (!dom.sidebar || !dom.searchInput?.value.trim()) return;

  const preferred = dom.nav.querySelector(`.nav-link[data-doc-path="${CSS.escape(state.currentPath)}"]`)
    || dom.nav.querySelector('.nav-link--search-context')
    || dom.nav.querySelector('.nav-link');

  if (!preferred) return;

  const sidebarRect = dom.sidebar.getBoundingClientRect();
  const itemRect = preferred.getBoundingClientRect();
  const isAbove = itemRect.top < sidebarRect.top + 72;
  const isBelow = itemRect.bottom > sidebarRect.bottom - 40;

  if (!(isAbove || isBelow)) return;

  preferred.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
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
  state.lastNavRenderKey = buildDocsRenderKey(state.filteredDocs);
  syncSubsectionAnimationState();
  highlightActiveLink();
  revealRelevantSearchResult();
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

export function renderBreadcrumbs(isNotFound = false) {
  const current = state.docByPath.get(state.currentPath);

  if (isNotFound || !current) {
    dom.breadcrumbs.innerHTML = `
      <span class="font-semibold text-rose-700">No trobat</span>
      <span class="mx-2 text-slate-300">/</span>
      <span>${escapeHtml(state.currentPath)}</span>
    `;
    return;
  }

  dom.breadcrumbs.innerHTML = `
    <span class="font-semibold text-slate-700">${escapeHtml(current.title)}</span>
  `;
}

export function renderDocNav(isNotFound = false) {
  if (isNotFound) {
    dom.docNav.innerHTML = '';
    return;
  }

  const index = state.docIndexByPath.get(state.currentPath);
  const hasIndex = Number.isInteger(index) && index >= 0;

  const prev = hasIndex && index > 0
    ? state.docs[index - 1]
    : null;

  const next = hasIndex && index < state.docs.length - 1
    ? state.docs[index + 1]
    : null;

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

function getCurrentPageHeadingMatches(query) {
  const headings = state.headingIndex.get(state.currentPath) || [];
  if (!query) return [];

  const matches = [];

  for (const heading of headings) {
    if (!heading._searchText.includes(query)) continue;

    matches.push(heading);

    if (matches.length >= SEARCH_RESULT_LIMIT) {
      break;
    }
  }

  return matches;
}

function renderCurrentPageHeadingResults() {
  if (!state.searchUi?.panel || !state.searchUi?.message) return;

  const query = getActiveSearchQuery();
  const shouldShow = state.searchUiVisible && document.activeElement === dom.searchInput && !!query;
  const matches = shouldShow ? getCurrentPageHeadingMatches(query) : [];
  state.searchUiHeadingResults = matches;

  if (!shouldShow) {
    state.searchUiActiveIndex = -1;
    state.searchUi.panel.classList.remove('is-visible');
    state.searchUi.message.hidden = true;
    state.searchUi.results.innerHTML = '';
    return;
  }

  if (!matches.length) {
    state.searchUiActiveIndex = -1;
    state.searchUi.results.innerHTML = '';
    state.searchUi.message.hidden = false;
    state.searchUi.message.textContent = 'Cap títol d’aquesta pàgina coincideix amb la cerca.';
    state.searchUi.panel.classList.add('is-visible');
    return;
  }

  state.searchUi.message.hidden = true;
  state.searchUi.panel.classList.add('is-visible');
  state.searchUi.results.innerHTML = matches
    .map((heading, index) => `
      <button
        type="button"
        class="search-heading-result"
        data-heading-slug="${escapeHtml(heading.slug)}"
        data-heading-index="${index}"
      >
        <span class="search-heading-result__eyebrow">En aquesta pàgina</span>
        <span class="search-heading-result__text">${escapeHtml(heading.text)}</span>
      </button>
    `)
    .join('');

  if (state.searchUiActiveIndex >= matches.length) {
    state.searchUiActiveIndex = matches.length - 1;
  }

  syncActiveSearchHeadingResult();
}

function syncActiveSearchHeadingResult() {
  if (!state.searchUi?.results) return;

  const buttons = state.searchUi.results.querySelectorAll('.search-heading-result');
  let activeButton = null;

  for (const button of buttons) {
    const index = Number(button.dataset.headingIndex);
    const isActive = index === state.searchUiActiveIndex;
    button.classList.toggle('is-active', isActive);
    if (isActive) activeButton = button;
  }

  activeButton?.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
}

function ensureSearchUi() {
  if (searchUiReady || !dom.searchWrapper) return;

  const panel = document.createElement('div');
  panel.className = 'search-assist-panel';
  panel.innerHTML = `
    <p class="search-assist-message" hidden></p>
    <div class="search-assist-results"></div>
  `;

  dom.searchWrapper.appendChild(panel);
  state.searchUi = {
    panel,
    message: panel.querySelector('.search-assist-message'),
    results: panel.querySelector('.search-assist-results')
  };

  state.searchUi.results.addEventListener('mousedown', event => {
    event.preventDefault();
    const button = event.target.closest('.search-heading-result');
    if (!button) return;
    navigateToHeadingResult(button.dataset.headingSlug);
  });

  searchUiReady = true;
}

export function initializeSearchUi() {
  ensureSearchUi();
}

function hideCurrentPageHeadingResults() {
  if (!state.searchUi?.panel) return;
  state.searchUiVisible = false;
  renderCurrentPageHeadingResults();
}

export function clearSearch(resetInput = true) {
  clearTimeout(searchDebounceTimer);

  const hadQuery = !!dom.searchInput?.value.trim();
  const wasFiltered = state.filteredDocs !== state.docs;

  if (resetInput && dom.searchInput) {
    dom.searchInput.value = '';
  }

  state.filteredDocs = state.docs;
  state.searchUiActiveIndex = -1;
  state.searchUiVisible = false;
  state.lastSearchQuery = '';

  if (state.searchUi?.panel) {
    state.searchUi.panel.classList.remove('is-visible');
  }

  if (state.searchUi?.message) {
    state.searchUi.message.hidden = true;
  }

  if (state.searchUi?.results) {
    state.searchUi.results.innerHTML = '';
  }

  state.searchUiHeadingResults = [];

  if (hadQuery || wasFiltered) {
    ensureNavRendered(false);
  } else {
    highlightActiveLink();
  }
}

function navigateToHeadingResult(slug) {
  const navigated = navigateToCurrentHeading(slug);
  if (!navigated) return;

  hideCurrentPageHeadingResults();
  clearSearch(true);
}

function updateFilteredDocs(query) {
  const nextDocs = filterDocs(query);
  state.filteredDocs = nextDocs;
  state.lastSearchQuery = query;

  ensureNavRendered(false);

  if (state.filteredDocs.length) {
    state.searchUi.message.hidden = true;
  } else {
    state.searchUi.message.hidden = false;
    state.searchUi.message.textContent = 'No s’ha trobat cap pàgina amb aquesta cerca.';
    state.searchUi.panel.classList.add('is-visible');
  }

  renderCurrentPageHeadingResults();
}

export function filterDocs(query) {
  if (!query) return state.docs;

  ensureDocSearchIndex();

  return state.docs.filter(doc => {
    const headings = state.headingIndex.get(doc.path) || [];
    return doc._searchTitle.includes(query)
      || doc._searchPath.includes(query)
      || doc._searchSection.includes(query)
      || headings.some(heading => heading._searchText.includes(query));
  });
}

export function handleSearchInput() {
  ensureSearchUi();
  clearTimeout(searchDebounceTimer);

  searchDebounceTimer = setTimeout(() => {
    const query = getActiveSearchQuery();

    if (!query) {
      clearSearch(false);
      return;
    }

    if (query === state.lastSearchQuery) {
      state.searchUiVisible = true;
      renderCurrentPageHeadingResults();
      return;
    }

    state.searchUiVisible = true;
    updateFilteredDocs(query);
  }, 120);
}

export function handleSearchFocus() {
  ensureSearchUi();
  state.searchUiVisible = true;
  renderCurrentPageHeadingResults();
}

export function handleSearchBlur() {
  globalThis.setTimeout(() => {
    if (document.activeElement === dom.searchInput) return;
    hideCurrentPageHeadingResults();
  }, SEARCH_UI_BLUR_DELAY);
}

export function handleSearchKeydown(event) {
  const results = state.searchUiHeadingResults || [];
  if (!results.length) return;

  if (event.key === 'ArrowDown') {
    event.preventDefault();
    state.searchUiVisible = true;
    state.searchUiActiveIndex = Math.min(
      state.searchUiActiveIndex + 1,
      results.length - 1
    );
    syncActiveSearchHeadingResult();
    return;
  }

  if (event.key === 'ArrowUp') {
    event.preventDefault();
    state.searchUiVisible = true;
    state.searchUiActiveIndex = Math.max(state.searchUiActiveIndex - 1, 0);
    syncActiveSearchHeadingResult();
    return;
  }

  if (event.key === 'Enter' && state.searchUiActiveIndex >= 0) {
    event.preventDefault();
    const active = results[state.searchUiActiveIndex];
    if (active) {
      navigateToHeadingResult(active.slug);
    }
    return;
  }

  if (event.key === 'Escape') {
    hideCurrentPageHeadingResults();
    dom.searchInput.blur();
  }
}

export function handleNavClick(event) {
  const summary = event.target.closest('summary');
  if (summary && dom.nav.contains(summary)) {
    const details = summary.parentElement;
    if (details?.classList.contains('subsection')) {
      event.preventDefault();
      animateSubsection(details, !details.open);
      return;
    }
  }

  const link = event.target.closest('.nav-link');
  if (link) {
    clearSearch(true);
  }
}

function buildDocsRenderKey(docs) {
  if (!Array.isArray(docs) || !docs.length) {
    return '__empty__';
  }

  return docs.map(doc => doc.path).join('|');
}

function ensureNavRendered(force = false) {
  const nextKey = buildDocsRenderKey(state.filteredDocs);

  if (!force && nextKey === state.lastNavRenderKey) {
    highlightActiveLink();
    revealRelevantSearchResult();
    return false;
  }

  renderNav();
  state.lastNavRenderKey = nextKey;
  return true;
}