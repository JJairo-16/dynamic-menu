# Query Builder

`QueryBuilder<T, C>` permet consultar un menú sense modificar-lo.

És el builder adequat quan vols inspeccionar l’estat del menú,
comptar coincidències, recuperar opcions o obtenir-ne els índexs.

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

### `whereLabel(predicate)`

És una drecera quan només vols filtrar pel text del label.

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Exit"))
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

Aquest mètode és útil quan no vols només una llista,
sinó una col·lecció o un resultat derivat.

## Encadenament amb altres builders

`QueryBuilder` també pot actuar com a punt de partida per continuar amb altres operacions.

Això permet reutilitzar el selector i, en alguns casos, també el rang.

### `thenQuery()`

Continua la mateixa consulta amb un nou `QueryBuilder`.

```java
MenuEditor.query(menu)
    .whereAny()
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

En aquest cas només es transfereix el rang,
ja que l’ordenació no funciona a partir del selector de coincidència.