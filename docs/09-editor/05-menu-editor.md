# Menu Editor

`MenuEditor` és la façana pública central del paquet `menu.editor`.

És una utilitat estàtica i no es pot instanciar.

```java
private MenuEditor() {
    throw new AssertionError("No es pot instanciar MenuEditor");
}
```

La seva API pública real es divideix en aquests blocs:

- selectors de conveniència
- entrada al builder de reemplaç
- helpers estàtics d’eliminació
- entrada al builder d’eliminació
- ordenació
- consulta i cerca

---

# 1. Selectors de conveniència

`MenuEditor` ofereix dos selectors preparats.

## `alwaysFalseSelector()`

Retorna un selector que no selecciona mai cap opció.

```java
OptionSelector<String, Void> selector = MenuEditor.alwaysFalseSelector();
```

Això és útil per:

- proves
- casos on vols desactivar temporalment una operació
- composicions on necessites un selector neutre que no coincideixi amb res

## `alwaysTrueSelector()`

Retorna un selector que selecciona totes les opcions.

```java
OptionSelector<String, Void> selector = MenuEditor.alwaysTrueSelector();
```

És útil quan vols aplicar una operació global.

Exemple:

```java
int removed = MenuEditor.removeIf(menu, MenuEditor.alwaysTrueSelector());
```

---

# 2. Entrada al builder de reemplaç

`MenuEditor` no exposa helpers estàtics públics de replace del tipus “replaceFirstLabel” o “replaceAt”.

La seva API pública de reemplaç entra mitjançant `ReplaceBuilder`.

## `replace(menu)`

Inicia una operació fluïda de reemplaç.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().contains("config"))
    .label("Configuració")
    .execute();
```

## `replace(menu, selector)`

Igual que l’anterior, però ja amb una condició inicial.

```java
MenuEditor.replace(
    menu,
    (index, option) -> option.label().startsWith("Temp")
).label("Temporal").execute();
```

A partir d’aquí, tota la configuració es fa amb `ReplaceBuilder`.

---

# 3. Eliminació estàtica pública

A diferència del replace, `MenuEditor` sí que exposa una API estàtica pública completa per eliminar opcions.

## `removeIf(menu, selector)`

Elimina totes les opcions que compleixen la condició.

```java
int removed = MenuEditor.removeIf(
    menu,
    (index, option) -> option.label().startsWith("[DEBUG]")
);
```

## `removeIf(menu, selector, range)`

Elimina totes les coincidències dins d’un rang.

```java
int removed = MenuEditor.removeIf(
    menu,
    (index, option) -> option.label().startsWith("[DEBUG]"),
    Range.of(0, 5)
);
```

## `removeIf(menu, selector, range, limit)`

Elimina coincidències dins d’un rang i amb un límit màxim.

```java
int removed = MenuEditor.removeIf(
    menu,
    (index, option) -> option.label().startsWith("[DEBUG]"),
    Range.of(0, 10),
    2
);
```

## `removeIf(menu, selector, config)`

Elimina coincidències segons una configuració completa.

```java
int removed = MenuEditor.removeIf(
    menu,
    (index, option) -> option.label().startsWith("[DEBUG]"),
    EditConfig.builder()
        .range(Range.of(0, 10))
        .limit(2)
        .reverse(false)
        .build()
);
```

---

# 4. Eliminació en sentit invers

Aquests mètodes recorren el menú en sentit invers abans d’eliminar.

## `removeAllIfReverse(menu, selector)`

```java
int removed = MenuEditor.removeAllIfReverse(
    menu,
    (index, option) -> option.label().equals("Duplicada")
);
```

## `removeAllIfReverse(menu, selector, range)`

```java
int removed = MenuEditor.removeAllIfReverse(
    menu,
    (index, option) -> option.label().equals("Duplicada"),
    Range.of(0, 8)
);
```

## `removeAllIfReverse(menu, selector, range, limit)`

```java
int removed = MenuEditor.removeAllIfReverse(
    menu,
    (index, option) -> option.label().equals("Duplicada"),
    Range.of(0, 8),
    1
);
```

## `removeAllIfReverse(menu, selector, config)`

```java
int removed = MenuEditor.removeAllIfReverse(
    menu,
    (index, option) -> option.label().equals("Duplicada"),
    EditConfig.builder()
        .range(Range.of(0, 8))
        .limit(1)
        .reverse(true)
        .build()
);
```

Aquesta família és útil quan el concepte important és “recórrer des del final”.

---

# 5. Helpers estàtics d’eliminació específica

## `removeFirstIf(menu, selector)`

Elimina només la primera coincidència.

```java
boolean removed = MenuEditor.removeFirstIf(
    menu,
    (index, option) -> option.label().equals("Duplicada")
);
```

## `removeLastIf(menu, selector)`

Elimina només l’última coincidència.

```java
boolean removed = MenuEditor.removeLastIf(
    menu,
    (index, option) -> option.label().equals("Duplicada")
);
```

## `removeFirstLabel(menu, label)`

Elimina la primera opció amb un label exacte.

```java
boolean removed = MenuEditor.removeFirstLabel(menu, "Sortir");
```

## `removeLastLabel(menu, label)`

Elimina l’última opció amb un label exacte.

```java
boolean removed = MenuEditor.removeLastLabel(menu, "Sortir");
```

## `removeAllLabels(menu, label)`

Elimina totes les opcions amb un label exacte.

```java
int removed = MenuEditor.removeAllLabels(menu, "Sortir");
```

Aquests helpers són especialment útils quan no necessites definir manualment `Range`, `EditConfig` o un builder.

---

# 6. Entrada al builder d’eliminació

A més dels helpers estàtics, `MenuEditor` també exposa una API fluïda d’eliminació.

## `remove(menu)`

Inicia un `RemoveBuilder`.

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"))
    .execute();
```

## `remove(menu, selector)`

Inicia el builder amb una condició inicial.

```java
MenuEditor.remove(
    menu,
    (index, option) -> option.label().startsWith("[TEMP]")
).first().execute();
```

La documentació completa d’aquesta API està a `Fluent Builders`.

---

# 7. Ordenació

`MenuEditor` exposa diverses variants públiques de `sortByLabel(...)`.

Totes elles retornen el mateix `DynamicMenu` per facilitar un estil d’ús encadenable.

## `sortByLabel(menu)`

Ordena totes les opcions per label.

```java
MenuEditor.sortByLabel(menu);
```

## `sortByLabel(menu, comparator)`

Permet definir un comparador personalitzat.

```java
MenuEditor.sortByLabel(
    menu,
    (a, b) -> a.label().compareToIgnoreCase(b.label())
);
```

## `sortByLabel(menu, range)`

Ordena només una zona del menú.

```java
MenuEditor.sortByLabel(menu, Range.of(1, 6));
```

## `sortByLabel(menu, comparator, range)`

Combina comparador i rang.

```java
MenuEditor.sortByLabel(
    menu,
    (a, b) -> a.label().compareToIgnoreCase(b.label()),
    Range.of(1, 6)
);
```

---

# 8. Ordenació amb opcions fixades mitjançant selectors

Aquestes variants permeten marcar opcions que han de quedar al principi o al final.

## `sortByLabel(menu, firstSelector, lastSelector)`

```java
MenuEditor.sortByLabel(
    menu,
    (index, option) -> option.label().equals("Inici"),
    (index, option) -> option.label().equals("Sortir")
);
```

## `sortByLabel(menu, firstSelector, lastSelector, range)`

```java
MenuEditor.sortByLabel(
    menu,
    (index, option) -> option.label().equals("Inici"),
    (index, option) -> option.label().equals("Sortir"),
    Range.of(0, 8)
);
```

## `sortByLabel(menu, comparator, firstSelector, lastSelector)`

```java
MenuEditor.sortByLabel(
    menu,
    (a, b) -> a.label().compareToIgnoreCase(b.label()),
    (index, option) -> option.label().equals("Inici"),
    (index, option) -> option.label().equals("Sortir")
);
```

## `sortByLabel(menu, comparator, firstSelector, lastSelector, range)`

```java
MenuEditor.sortByLabel(
    menu,
    (a, b) -> a.label().compareToIgnoreCase(b.label()),
    (index, option) -> option.label().equals("Inici"),
    (index, option) -> option.label().equals("Sortir"),
    Range.of(0, 8)
);
```

Aquesta família és útil quan vols ordenar, però mantenint opcions especials ancorades.

---

# 9. Ordenació amb índexs fixats

En lloc de seleccionar opcions per condició, aquestes variants fixen posicions concretes.

## `sortByLabelPinnedIndexes(menu, firstIndexes, lastIndexes)`

```java
MenuEditor.sortByLabelPinnedIndexes(
    menu,
    List.of(0),
    List.of(5)
);
```

## `sortByLabelPinnedIndexes(menu, firstIndexes, lastIndexes, range)`

```java
MenuEditor.sortByLabelPinnedIndexes(
    menu,
    List.of(0),
    List.of(5),
    Range.of(0, 8)
);
```

## `sortByLabelPinnedIndexes(menu, comparator, firstIndexes, lastIndexes)`

```java
MenuEditor.sortByLabelPinnedIndexes(
    menu,
    (a, b) -> a.label().compareToIgnoreCase(b.label()),
    List.of(0),
    List.of(5)
);
```

## `sortByLabelPinnedIndexes(menu, comparator, firstIndexes, lastIndexes, range)`

```java
MenuEditor.sortByLabelPinnedIndexes(
    menu,
    (a, b) -> a.label().compareToIgnoreCase(b.label()),
    List.of(0),
    List.of(5),
    Range.of(0, 8)
);
```

Aquestes variants són útils quan ja coneixes exactament quines posicions no s’han de moure.

---

# 10. Helpers de consulta per selector

A més d’editar, `MenuEditor` també permet consultar el menú.

## `indexOfFirst(menu, selector)`

Retorna l’índex de la primera coincidència.

```java
int index = MenuEditor.indexOfFirst(
    menu,
    (index, option) -> option.label().startsWith("Admin")
);
```

## `indexOfFirst(menu, selector, range)`

```java
int index = MenuEditor.indexOfFirst(
    menu,
    (index, option) -> option.label().startsWith("Admin"),
    Range.of(0, 5)
);
```

## `indexOfLast(menu, selector)`

```java
int index = MenuEditor.indexOfLast(
    menu,
    (index, option) -> option.label().startsWith("Admin")
);
```

## `indexOfLast(menu, selector, range)`

```java
int index = MenuEditor.indexOfLast(
    menu,
    (index, option) -> option.label().startsWith("Admin"),
    Range.of(0, 5)
);
```

## `containsMatch(menu, selector)`

```java
boolean exists = MenuEditor.containsMatch(
    menu,
    (index, option) -> option.label().startsWith("Admin")
);
```

## `countMatches(menu, selector)`

```java
int total = MenuEditor.countMatches(
    menu,
    (index, option) -> option.label().startsWith("Admin")
);
```

## `countMatches(menu, selector, range)`

```java
int total = MenuEditor.countMatches(
    menu,
    (index, option) -> option.label().startsWith("Admin"),
    Range.of(0, 5)
);
```

## `indexesOf(menu, selector)`

```java
List<Integer> indexes = MenuEditor.indexesOf(
    menu,
    (index, option) -> option.label().startsWith("Admin")
);
```

## `indexesOf(menu, selector, range)`

```java
List<Integer> indexes = MenuEditor.indexesOf(
    menu,
    (index, option) -> option.label().startsWith("Admin"),
    Range.of(0, 5)
);
```

## `matchingOptions(menu, selector)`

Retorna totes les opcions coincidents.

```java
List<MenuOption<String, Void>> matches = MenuEditor.matchingOptions(
    menu,
    (index, option) -> option.label().startsWith("Admin")
);
```

## `matchingOptions(menu, selector, range)`

```java
List<MenuOption<String, Void>> matches = MenuEditor.matchingOptions(
    menu,
    (index, option) -> option.label().startsWith("Admin"),
    Range.of(0, 5)
);
```

---

# 11. Helpers de consulta per label exacte

Quan la cerca és per text exacte del label, hi ha dreceres específiques.

## `indexOfFirstLabel(menu, label)`

```java
int index = MenuEditor.indexOfFirstLabel(menu, "Sortir");
```

## `indexOfLastLabel(menu, label)`

```java
int index = MenuEditor.indexOfLastLabel(menu, "Sortir");
```

## `containsLabel(menu, label)`

```java
boolean exists = MenuEditor.containsLabel(menu, "Sortir");
```

## `countLabelMatches(menu, label)`

```java
int total = MenuEditor.countLabelMatches(menu, "Sortir");
```

## `findFirst(menu, selector)`

Retorna la primera opció coincident o `null`.

```java
MenuOption<String, Void> option = MenuEditor.findFirst(
    menu,
    (index, current) -> current.label().startsWith("Admin")
);
```

## `findLast(menu, selector)`

Retorna l’última opció coincident o `null`.

```java
MenuOption<String, Void> option = MenuEditor.findLast(
    menu,
    (index, current) -> current.label().startsWith("Admin")
);
```

## `findFirstLabel(menu, label)`

Retorna la primera coincidència exacta o `null`.

```java
MenuOption<String, Void> option = MenuEditor.findFirstLabel(menu, "Sortir");
```

## `findLastLabel(menu, label)`

Retorna l’última coincidència exacta o `null`.

```java
MenuOption<String, Void> option = MenuEditor.findLastLabel(menu, "Sortir");
```

---

# 12. Com aprendre a usar `MenuEditor`

La manera més pràctica d’aprendre aquesta API és:

1. començar amb `removeIf(...)`, `removeFirstIf(...)` i `removeLastIf(...)`
2. aprendre `Range` i `EditConfig`
3. passar a `remove(menu)` i `replace(menu)` amb builders
4. utilitzar els helpers de consulta abans d’editar
5. acabar amb les variants avançades d’ordenació

## Patró mental recomanat

Quan facis servir `MenuEditor`, pensa sempre en aquest esquema:

- **què** selecciones
- **on** ho apliques
- **quantes** coincidències afectes
- **en quin sentit** recorres el menú
- **què** vols retornar o transformar

Això et permet entendre tant els helpers estàtics com els builders com una mateixa família coherent.