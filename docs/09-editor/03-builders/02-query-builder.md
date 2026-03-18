# Query Builder

`QueryBuilder<T, C>` permet consultar un menú sense modificar-lo.

És el builder adequat quan vols inspeccionar l’estat del menú, comptar coincidències, recuperar opcions o obtenir-ne els índexs.

S'obté des de:

```java
MenuEditor.query(menu)
```

## Selecció

La selecció determina quines opcions formen part de la consulta.

### `where(selector)`

Permet definir una condició completa basada en índex i opció.

```java
MenuEditor.query(menu)
    .where((index, option) -> option.label().equals("Exit"))
    .exists();
```

### `whereIndex(predicate)`

Selecciona les opcions a partir d’una condició aplicada sobre l’índex.

```java
MenuEditor.query(menu)
    .whereIndex(i -> i < 5)
    .last();
```

### `whereLabel(predicate)`

És una drecera quan només vols filtrar pel text del label.

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Exit"))
    .exists();
```

### `whereLabelEquals(String text)`

Selecciona les opcions amb un label exactament igual.

```java
MenuEditor.query(menu)
    .whereLabelEquals("Exit")
    .exists();
```

### `whereLabelEqualsIgnoreCase(String text)`

Selecciona les opcions amb un label exactament igual, ignorant majúscules i minúscules.

```java
MenuEditor.query(menu)
    .whereLabelEqualsIgnoreCase("exit")
    .exists();
```

### `whereLabelStartsWidth(String prefix)`

Selecciona les opcions el label de les quals comença amb el prefix indicat.

```java
MenuEditor.query(menu)
    .whereLabelStartsWidth("[ADMIN]")
    .count();
```

### `whereLabelEndsWidth(String suffix)`

Selecciona les opcions el label de les quals acaba amb el sufix indicat.

```java
MenuEditor.query(menu)
    .whereLabelEndsWidth("...")
    .exists();
```

### `whereAny()`

Selecciona totes les opcions del menú.

És útil per fer consultes globals.

```java
MenuEditor.query(menu)
    .whereAny()
    .count();
```

## Rang

A més del selector, la consulta es pot limitar a una zona concreta del menú.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(2, 6)
    .count();
```

## Operacions bàsiques

Un cop definida la selecció, `QueryBuilder` permet executar consultes terminals.

### `exists()`

Indica si existeix almenys una coincidència.

```java
boolean exists = MenuEditor.query(menu)
    .whereAny()
    .exists();
```

### `count()`

Retorna quantes opcions coincideixen amb la consulta.

```java
int count = MenuEditor.query(menu)
    .whereAny()
    .count();
```

### `first()`

Retorna la primera opció coincident.

```java
MenuOption<T,C> first = MenuEditor.query(menu)
    .whereAny()
    .first();
```

### `last()`

Retorna l’última opció coincident.

```java
MenuOption<T,C> last = MenuEditor.query(menu)
    .whereAny()
    .last();
```

## Índexs de coincidència

A més de recuperar opcions, `QueryBuilder` també pot treballar amb posicions.

Això és útil quan una operació posterior necessita índexs en lloc d’objectes.

### `indexes()`

Retorna tots els índexs de les opcions coincidents.

```java
List<Integer> indexes = MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Settings"))
    .indexes();
```

### `firstIndex()`

Retorna el primer índex coincident.

```java
int index = MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Exit"))
    .firstIndex();
```

Si no hi ha coincidències retorna `-1`.

### `lastIndex()`

Retorna l’últim índex coincident.

```java
int index = MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Exit"))
    .lastIndex();
```

Si no hi ha coincidències retorna `-1`.

## Recuperació de resultats

Quan vols treballar amb les opcions trobades, pots resoldre la consulta com a llista.

### `options()`

Retorna totes les opcions coincidents.

```java
List<MenuOption<T,C>> options = MenuEditor.query(menu)
    .whereAny()
    .options();
```

### `resolve()`

És un àlies semàntic de `options()`.

```java
List<MenuOption<T,C>> options = MenuEditor.query(menu)
    .whereAny()
    .resolve();
```

S’utilitza quan es vol indicar explícitament que s’està resolent la consulta.

## Transformació de resultats

A més de recuperar la llista, la consulta es pot transformar a qualsevol estructura final.

### `collect(collector)`

Permet transformar el resultat de la consulta amb qualsevol estructura desitjada.

```java
Set<String> labels = MenuEditor.query(menu)
    .whereAny()
    .collect(list ->
        list.stream()
            .map(MenuOption::label)
            .collect(Collectors.toSet())
    );
```

Aquest mètode és útil quan no vols només una llista sinó una col·lecció o un resultat derivat.

### `toList()`

Resol la consulta i retorna les opcions coincidents com una `List`.

```java
List<MenuOption<String, Void>> options = MenuEditor.query(menu)
    .whereAny()
    .toList();
```

### `stream()`

Resol la consulta i retorna les opcions coincidents com un `Stream`.

Permet utilitzar directament l’API de Streams de Java per transformar o processar els resultats.

```java
MenuEditor.query(menu)
    .whereAny()
    .stream()
    .forEach(System.out::println);
```

## Encadenament amb altres builders

`QueryBuilder` també pot actuar com a punt de partida per continuar amb altres operacions.

Això permet reutilitzar el selector i, en alguns casos, també el rang.

## Herència predeterminada

Quan l'encadenament surt de `QueryBuilder`, el comportament per defecte és:

> `QueryBuilder` és el punt d'encadenament amb més herència predeterminada.
> Això el converteix en una bona base quan primer vols localitzar coincidències i després reutilitzar aquest context en una operació posterior.

- `thenQuery()` hereta selector i rang
- `thenRemove()` hereta selector i rang
- `thenReplace()` hereta selector i rang
- `thenSort()` hereta només el rang
- `thenShuffle()` hereta només el rang

### `thenQuery()`

Continua la mateixa consulta amb un nou `QueryBuilder`.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenQuery()
    .count();
```

Manté el selector i el rang actual.

### `thenRemove()`

Transfereix el selector i el rang al `RemoveBuilder`.

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenRemove()
    .execute();
```

### `thenReplace()`

Transfereix el selector i el rang al `ReplaceBuilder`.

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Old"))
    .thenReplace()
    .label("New")
    .execute();
```

### `thenSort()`

Continua amb `SortBuilder`.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenSort()
    .byLabel()
    .apply();
```

En aquest cas només es transfereix el rang, ja que l’ordenació no funciona a partir del selector de coincidència.

### `thenShuffle()`

Continua amb `ShuffleBuilder`.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenShuffle()
    .apply();
```

En aquest cas també només es transfereix el rang, ja que la barreja no reutilitza el selector fluent de coincidència.

## Herència explícita amb `InheritanceMode`

Quan vols controlar la transferència d'estat de forma explícita, pots usar la variant `thenX(InheritanceMode)`.

### `thenQuery(InheritanceMode)`

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenQuery(InheritanceMode.NONE)
    .whereLabel(label -> label.equals("Exit"))
    .count();
```

### `thenRemove(InheritanceMode)`

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenRemove(InheritanceMode.ALL)
    .execute();
```

### `thenReplace(InheritanceMode)`

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Old"))
    .thenReplace(InheritanceMode.SELECTION)
    .label("New")
    .execute();
```

### `thenSort(InheritanceMode)`

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenSort(InheritanceMode.RANGE)
    .byLabel()
    .apply();
```

### `thenShuffle(InheritanceMode)`

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenShuffle(InheritanceMode.RANGE)
    .apply();
```

## Modes disponibles

- `InheritanceMode.NONE`: no hereta res
- `InheritanceMode.RANGE`: hereta només el rang
- `InheritanceMode.SELECTION`: hereta selector i rang
- `InheritanceMode.ALL`: hereta tot l'estat compatible amb el builder destí

En el cas de `SortBuilder` i `ShuffleBuilder`, l'herència efectiva mai no inclou selector, de manera que habitualment s'usen `RANGE` o `ALL` com a sinònims pràctics.

### Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;
```