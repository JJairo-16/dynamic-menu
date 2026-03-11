# Installation

Per utilitzar aquesta llibreria només cal afegir els fitxers font al teu projecte.

Versió mínima recomanada de Java:

```
Java 17+
```

Estructura de paquets recomanada:

```
menu
menu.action
menu.config
menu.editor
menu.hook
menu.model
menu.selector
menu.snapshot
menu.snapshot.context
menu.wrappers
```

`DynamicMenu` és la façana principal de la llibreria. Encara que internament hi ha classes a `menu.snapshot`, l'ús normal dels snapshots es fa a través dels wrappers de `DynamicMenu`.

Importacions habituals:

```java
import menu.DynamicMenu;
import menu.model.MenuResult;
```
