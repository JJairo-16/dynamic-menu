# Edit Config

`EditConfig` defineix **com s’ha d’aplicar una operació condicional** dins de `MenuEditor`.

No serveix per configurar un editor visual, sinó per controlar tres aspectes d’una operació:

- `range`
- `limit`
- `reverse`

## Obtenció

S'importa utilitzant:

```java
import menu.editor.EditConfig;
```

## Configuració per defecte

```java
EditConfig config = EditConfig.defaults();
```

La configuració per defecte equival conceptualment a:

- rang complet
- sense límit pràctic
- recorregut normal

## Crear configuració a partir d’un rang

```java
EditConfig config = EditConfig.of(Range.of(1, 6));
```

Això és útil quan només vols restringir la zona del menú afectada.

## Builder

Quan necessites més control, pots utilitzar el builder.

```java
EditConfig config = EditConfig.builder()
    .range(Range.of(0, 10))
    .limit(2)
    .reverse(true)
    .build();
```

## Significat de cada camp

### `range`

Defineix la part del menú on es poden buscar coincidències.

```java
config.range();
```

### `limit`

Defineix quantes coincidències com a màxim es poden afectar.

```java
config.limit();
```

Per exemple, `limit(1)` permet expressar operacions tipus “només la primera” o “només l’última”, depenent del sentit del recorregut.

### `reverse`

Indica si la cerca s’ha de fer en sentit invers.

```java
config.reverse();
```

Quan és `true`, l’operació comença des del final del rang.

## Exemple pràctic

```java
int changed = MenuEditor.replaceLabelIf(
    menu,
    (index, option) -> option.label().equals("Sortir"),
    "Tancar",
    EditConfig.builder()
        .range(Range.of(0, 8))
        .limit(1)
        .reverse(true)
        .build()
);
```

Això reemplaça només **l’última** coincidència de `"Sortir"` dins del rang indicat.

## Relació amb els helpers de conveniència

Molts mètodes de `MenuEditor` ofereixen dreceres que internament equivalen a un `EditConfig`.

Per exemple:

- `removeFirstIf(...)`
- `removeLastIf(...)`
- `replaceFirstIf(...)`
- `replaceLastIf(...)`

Conceptualment, aquests mètodes són variants comunes de:

- `limit(1)`
- recorregut normal o invers

## Quan convé usar `EditConfig`

És recomanable quan la lògica comença a necessitar:

- rangs parcials
- límits
- recorregut invers
- més claredat que amb sobrecàrregues simples