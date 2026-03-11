# Dynamic Menu Library

Una llibreria lleugera per crear **menús interactius dinàmics en Java**.

Aquesta documentació explica com construir menús, definir accions, gestionar snapshots a través de `DynamicMenu`, treballar amb context, crear menús jeràrquics, aplicar configuracions avançades i editar opcions amb `MenuEditor`.

---

# 01 - Getting Started

Aprèn els conceptes bàsics de la llibreria.

- [01 - Installation](01-getting-started/01-installation.md)
- [02 - First Menu](01-getting-started/02-first-menu.md)
- [03 - Basic Concepts](01-getting-started/03-basic-concepts.md)

---

# 02 - Menu System

Components principals del sistema de menú.

- [01 - DynamicMenu](02-menu/01-dynamic-menu.md)
- [02 - Menu Context](02-menu/02-menu-context.md)
- [03 - Child Menus](02-menu/03-child-menus.md)

---

# 03 - Actions

Maneres de definir el comportament de les opcions del menú.

- [01 - SimpleMenuAction](03-actions/01-simple-menu-action.md)
- [02 - MenuAction](03-actions/02-menu-action.md)
- [03 - MenuRuntimeAction](03-actions/03-menu-runtime-action.md)

---

# 04 - Menu Model

Estructures de dades del sistema.

- [01 - MenuOption](04-model/01-menu-option.md)
- [02 - MenuResult](04-model/02-menu-result.md)

---

# 05 - Hooks

Permeten executar lògica abans i després de cada iteració del menú.

- [01 - MenuLoopHook](05-hooks/01-menu-loop-hook.md)
- [02 - MenuLoopState](05-hooks/02-menu-loop-state.md)

---

# 06 - Selector

Component responsable de seleccionar opcions del menú.

- [01 - MenuSelector](06-selector/01-menu-selector.md)

---

# 07 - Snapshots

Permeten capturar i restaurar l'estat estructural del menú i, opcionalment, l'estat intern del context.

- [01 - Snapshots](07-snapshots/01-snapshots.md)
- [02 - Menu Navigation](07-snapshots/02-menu-navigation.md)
- [03 - Context Rollback](07-snapshots/03-context-rollback.md)
- [04 - Composite Snapshots](07-snapshots/04-composite-snapshots.md)

---

# 08 - Advanced Topics

Funcionalitats avançades del sistema de menús.

- [01 - Runtime and Option Management](08-advanced/01-runtime-and-option-management.md)
- [02 - Best Practices](08-advanced/02-best-practices.md)
- [03 - Auto Cleanup](08-advanced/03-auto-cleanup.md)
- [04 - Policies](08-advanced/04-policies.md)

---

# 09 - Editor

Utilitats avançades per **buscar, reemplaçar, eliminar i ordenar opcions** d’un `DynamicMenu` utilitzant selectors, mappers i configuracions d’edició.

- [01 - Menu Editor Overview](09-editor/01-menu-editor-overview.md)
- [02 - Range](09-editor/02-range.md)
- [03 - Edit Config](09-editor/03-edit-config.md)
- [04 - Selectors and Mappers](09-editor/04-selectors-and-mappers.md)
- [05 - Menu Editor](09-editor/05-menu-editor.md)