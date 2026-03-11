# Menu Editor

`MenuEditor` és la classe central del paquet `menu.editor`.

És una utilitat estàtica i **no es pot instanciar**.

## Tipus d’operacions

La seva API es pot entendre en sis blocs principals:

- reemplaç per índex
- reemplaç per label exacte
- operacions condicionals
- cerca i inspecció
- ordenació
- reemplaços en lot

---

## Reemplaç per índex

Quan ja coneixes la posició exacta d’una opció, pots modificar-la directament.

### Reemplaçar només el label

```java
MenuEditor.replaceLabelAt(menu, 0, "Inici");
```

### Reemplaçar només l’acció

```java
MenuEditor.replaceActionAt(menu, 1, (ctx, runtime) -> MenuResult.repeatLoop());
```

### Reemplaçar label i acció

```java
MenuEditor.replaceAt(
    menu,
    2,
    "Configuració",
    (ctx, runtime) -> MenuResult.repeatLoop()
);
```

### Reemplaçar una opció completa

```java
MenuEditor.replaceAt(menu, 3, new MenuOption<>("Sortir", (ctx, runtime) -> MenuResult.exitMenu()));
```

---

## Reemplaç per label exacte

Quan no vols treballar per índex, pots actuar sobre coincidències exactes de label.

### Primera coincidència

```java
MenuEditor.replaceFirstLabel(menu, "Config", "Configuració");
MenuEditor.replaceFirstAction(menu, "Sortir", (ctx, runtime) -> MenuResult.exitMenu());
```

### Última coincidència

```java
MenuEditor.replaceLastLabel(menu, "Config", "Configuració");
```

### Totes les coincidències

```java
MenuEditor.replaceAllLabels(menu, "Config", "Configuració");
```

També hi ha variants per reemplaçar l’acció o l’opció completa.

---

## Eliminació condicional

Pots eliminar opcions segons una condició.

### Eliminar totes les coincidències

```java
int removed = MenuEditor.removeIf(
    menu,
    (index, option) -> option.label().startsWith("[TEMP]")
);
```

### Eliminar només la primera

```java
boolean removed = MenuEditor.removeFirstIf(
    menu,
    (index, option) -> option.label().equals("Duplicada")
);
```

### Eliminar només l’última

```java
boolean removed = MenuEditor.removeLastIf(
    menu,
    (index, option) -> option.label().equals("Duplicada")
);
```

### Eliminar per label exacte

```java
MenuEditor.removeFirstLabel(menu, "Sortir");
MenuEditor.removeLastLabel(menu, "Sortir");
MenuEditor.removeAllLabels(menu, "Sortir");
```

---

## Reemplaç condicional

Aquesta és la part més flexible de `MenuEditor`.

### Reemplaç complet amb `OptionMapper`

```java
int changed = MenuEditor.replaceIf(
    menu,
    (index, option) -> option.label().contains("config"),
    (index, option) -> new MenuOption<>(
        "Configuració",
        option.action()
    )
);
```

### Reemplaç només de labels

```java
MenuEditor.replaceLabelIf(
    menu,
    (index, option) -> index < 3,
    (index, option) -> "[" + index + "] " + option.label()
);
```

### Reemplaç només d’accions

```java
MenuEditor.replaceActionIf(
    menu,
    (index, option) -> option.label().equals("Tornar"),
    (index, option) -> (ctx, runtime) -> MenuResult.repeatLoop()
);
```

### Primera i última coincidència

```java
MenuEditor.replaceFirstIf(menu, selector, mapper);
MenuEditor.replaceLastIf(menu, selector, mapper);
```

També existeixen les variants:

- `replaceFirstLabelIf(...)`
- `replaceLastLabelIf(...)`
- `replaceFirstActionIf(...)`
- `replaceLastActionIf(...)`

---

## Ús amb `Range` i `EditConfig`

Moltes operacions condicionals tenen sobrecàrregues amb `Range` o amb `EditConfig`.

### Amb rang

```java
MenuEditor.replaceLabelIf(
    menu,
    (index, option) -> option.label().equals("Item"),
    "Element",
    Range.of(2, 8)
);
```

### Amb rang i límit

```java
MenuEditor.removeIf(
    menu,
    (index, option) -> option.label().startsWith("Tmp"),
    Range.of(0, 10),
    2
);
```

### Amb configuració completa

```java
MenuEditor.replaceActionIf(
    menu,
    (index, option) -> option.label().equals("Sortir"),
    (index, option) -> (ctx, runtime) -> MenuResult.exitMenu(),
    EditConfig.builder()
        .range(Range.of(0, 10))
        .limit(1)
        .reverse(true)
        .build()
);
```

---

## Cerca i inspecció

`MenuEditor` també inclou utilitats per consultar l’estat del menú.

### Índexs

```java
int first = MenuEditor.indexOfFirst(menu, selector);
int last = MenuEditor.indexOfLast(menu, selector);
```

### Presència i recompte

```java
boolean exists = MenuEditor.containsMatch(menu, selector);
int total = MenuEditor.countMatches(menu, selector);
```

### Llistes de coincidències

```java
List<Integer> indexes = MenuEditor.indexesOf(menu, selector);
List<MenuOption<String, Void>> matches = MenuEditor.matchingOptions(menu, selector);
```

### Helpers per label exacte

```java
int first = MenuEditor.indexOfFirstLabel(menu, "Sortir");
int last = MenuEditor.indexOfLastLabel(menu, "Sortir");
boolean exists = MenuEditor.containsLabel(menu, "Sortir");
int total = MenuEditor.countLabelMatches(menu, "Sortir");
MenuOption<String, Void> option = MenuEditor.findFirstLabel(menu, "Sortir");
```

Aquestes utilitats són especialment útils abans d’aplicar una edició.

---

## Ordenació

`MenuEditor` pot ordenar opcions per label.

### Ordenació bàsica

```java
MenuEditor.sortByLabel(menu);
```

### Ordenació dins d’un rang

```java
MenuEditor.sortByLabel(menu, Range.of(1, 6));
```

### Ordenació amb comparador

```java
MenuEditor.sortByLabel(menu, comparator);
```

### Ordenació mantenint algunes opcions al principi o al final

```java
MenuEditor.sortByLabel(
    menu,
    (index, option) -> option.label().equals("Inici"),
    (index, option) -> option.label().equals("Sortir")
);
```

En aquest cas:

- les opcions marcades pel primer selector queden al principi
- les opcions marcades pel segon selector queden al final
- la resta s’ordena pel comparador indicat o pel comparador per defecte

També hi ha una variant amb índexs fixats:

```java
MenuEditor.sortByLabelPinnedIndexes(menu, List.of(0), List.of(5));
```

---

## Reemplaços en lot

Quan ja tens un conjunt de canvis calculats, pots aplicar-los en bloc.

### Diversos labels per índex

```java
MenuEditor.replaceLabelsAt(menu, Map.of(
    0, "Inici",
    2, "Configuració"
));
```

### Diverses accions per índex

```java
MenuEditor.replaceActionsAt(menu, replacements);
```

### Diverses opcions completes per índex

```java
MenuEditor.replaceAt(menu, optionReplacements);
```

---

## Consideracions pràctiques

`MenuEditor` és molt útil quan:

- el menú ja existeix i només vols editar-ne parts
- necessites criteris més expressius que els wrappers bàsics de `DynamicMenu`
- vols escriure transformacions reutilitzables

En canvi, si el que vols és simplement afegir o treure una opció concreta, sovint continua sent més directe utilitzar l’API base de `DynamicMenu`.

## Recomanació

Fes servir:

- l’API bàsica del menú per a canvis simples i puntuals
- `MenuEditor` per a operacions declaratives, condicionals o en lot