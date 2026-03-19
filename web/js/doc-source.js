import {
  buildContentUrl,
  NOT_FOUND_MARKDOWN
} from './state.js';

const MAX_DOC_SOURCE_CACHE = 16;
const docSourceCache = new Map();
const pendingDocLoads = new Map();

function touchDocSourceCache(path, entry) {
  if (docSourceCache.has(path)) {
    docSourceCache.delete(path);
  }

  docSourceCache.set(path, entry);

  if (docSourceCache.size > MAX_DOC_SOURCE_CACHE) {
    const oldestKey = docSourceCache.keys().next().value;
    docSourceCache.delete(oldestKey);
  }
}

function renderNotFoundMarkdown(path) {
  return `${NOT_FOUND_MARKDOWN}

\`Ruta sol·licitada: ${path}\``;
}

export async function getDocSource(path) {
  const cached = docSourceCache.get(path);
  if (cached) {
    touchDocSourceCache(path, cached);
    return cached;
  }

  const pending = pendingDocLoads.get(path);
  if (pending) {
    return pending;
  }

  const request = (async () => {
    try {
      const response = await fetch(buildContentUrl(path));
      const isNotFound = !response.ok;
      const markdown = isNotFound
        ? renderNotFoundMarkdown(path)
        : await response.text();

      const entry = {
        markdown,
        isNotFound,
        headings: null
      };

      touchDocSourceCache(path, entry);
      return entry;
    } finally {
      pendingDocLoads.delete(path);
    }
  })();

  pendingDocLoads.set(path, request);
  return request;
}