# Menu Editor Overview

`menu.editor` agrupa utilitats per **modificar opcions d’un `DynamicMenu` de manera declarativa**.

A diferència de l’API bàsica del menú, que exposa operacions com `addOption(...)`, `removeOption(...)` o `moveOptionToIndex(...)`, l’editor està pensat per treballar amb criteris més expressius:

- buscar opcions per condició
- reemplaçar labels
- reemplaçar accions
- reemplaçar opcions completes
- eliminar coincidències
- ordenar per label
- limitar l’operació a un rang o a un nombre màxim de coincidències

## Components del paquet

El paquet es divideix en aquestes peces:

- `MenuEditor`
- `Range`
- `EditConfig`
- `OptionSelector`
- `LabelMapper`
- `ActionMapper`
- `OptionMapper`

## Idea general

`MenuEditor` no és un editor interactiu ni una classe instanciable.

És una **utilitat estàtica** amb operacions sobre `DynamicMenu`.

Exemple simple:

```java
MenuEditor.replaceFirstLabel(menu, "Configuracio", "Configuració");
```

També es poden fer operacions condicionals:

```java
MenuEditor.removeIf(menu, (index, option) -> option.label().startsWith("[DEBUG]"));
```

## Quan convé utilitzar-lo

Aquest paquet és útil quan:

- vols modificar opcions sense reconstruir manualment tot el menú
- necessites treballar sobre la primera, l’última o totes les coincidències
- vols limitar els canvis a una part del menú
- necessites ordenar opcions mantenint algunes fixades
- vols expressar la lògica d’edició amb selectors i mappers

## Relació amb snapshots

Internament, moltes operacions de `MenuEditor` treballen a partir d’un snapshot del menú actual, reconstrueixen la llista d’opcions i després restauren l’estat.

Això fa que sigui especialment coherent amb la manera com `DynamicMenu` ja gestiona l’estat estructural.

## Corba d’aprenentatge recomanada

Per entendre bé aquesta secció, és recomanable haver llegit abans:

- `DynamicMenu`
- `MenuOption`
- `Snapshots`
- `Runtime and Option Management`

Això és important perquè `MenuEditor` no substitueix l’API principal del menú, sinó que la complementa.