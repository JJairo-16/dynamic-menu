# Menu Editor Overview

El paquet `menu.editor` agrupa les eines per **inspeccionar, eliminar, transformar i ordenar opcions** d’un `DynamicMenu` sense haver de reconstruir-lo manualment.

Aquest paquet està pensat per treballar a un nivell més declaratiu que l’API bàsica del menú. En lloc d’operar només per índex, permet expressar intencions com:

- “elimina totes les opcions que compleixen una condició”
- “substitueix només l’última coincidència”
- “ordena el menú, però mantén certes opcions fixades”
- “canvia només el label”
- “canvia només l’acció”
- “transforma l’opció completa”

## Peces principals del paquet

Els conceptes centrals d’aquesta secció són:

- `MenuEditor`
- `Range`
- `EditConfig`
- `OptionSelector`
- `LabelMapper`
- `ActionMapper`
- `OptionMapper`
- `RemoveBuilder`
- `ReplaceBuilder`

## Què exposa realment `MenuEditor`

La classe `MenuEditor` és la façana pública principal del paquet.

La seva API pública es pot agrupar en cinc blocs:

1. selectors de conveniència
2. entrada a l’API fluïda de reemplaç
3. entrada a l’API fluïda d’eliminació
4. helpers estàtics d’eliminació
5. utilitats d’ordenació i consulta

Això vol dir que `MenuEditor` no és només una col·lecció de mètodes d’edició destructiva, sinó també una capa de consulta i composició.

## Dos estils d’ús

La llibreria permet treballar de dues maneres complementàries.

### 1. Mètodes estàtics

Són útils per als casos directes i habituals.

```java
MenuEditor.removeFirstIf(
    menu,
    (index, option) -> option.label().equals("Duplicada")
);
```

### 2. API fluïda amb builders

És útil quan la lògica necessita més expressivitat.

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"))
    .range(0, 10)
    .limit(2)
    .reverse()
    .execute();
```

I també:

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().contains("config"))
    .label("Configuració")
    .execute();
```

## Què pots fer amb `menu.editor`

Les operacions públiques del paquet permeten:

- seleccionar opcions per condició
- eliminar la primera, l’última o totes les coincidències
- limitar una operació a un `Range`
- limitar el nombre màxim d’elements afectats
- recórrer el menú en sentit normal o invers
- substituir labels
- substituir accions
- substituir opcions completes
- ordenar per label
- fixar opcions al principi o al final durant una ordenació
- consultar índexs, opcions i coincidències

## Filosofia de disseny

L’edició es descriu com una combinació de:

- **què** s’ha de seleccionar
- **com** s’ha de transformar
- **on** s’ha d’aplicar
- **quantes** coincidències es poden afectar
- **en quin sentit** s’ha de recórrer el menú

Per això existeixen:

- `OptionSelector` per decidir quines opcions coincideixen
- `Range` per limitar la zona afectada
- `EditConfig` per agrupar `range`, `limit` i `reverse`
- `LabelMapper`, `ActionMapper` i `OptionMapper` per descriure la transformació

## Relació amb `DynamicMenu`

`MenuEditor` **complementa** `DynamicMenu`, no el substitueix.

En general:

- usa `DynamicMenu` per crear i gestionar l’estructura del menú
- usa `MenuEditor` quan vulguis aplicar canvis declaratius sobre opcions ja existents

## Quan convé utilitzar aquesta part de la llibreria

Aquest paquet és especialment útil quan:

- vols modificar un menú ja construït
- necessites treballar amb la primera o l’última coincidència
- vols expressar criteris complexos amb lambdes
- vols mantenir el codi més llegible que no pas encadenant operacions manuals
- necessites combinar selecció, rang, límit i sentit de recorregut

## Ruta de lectura recomanada

Per aprendre bé aquesta part de la llibreria, l’ordre recomanat és:

1. `Range`
2. `EditConfig`
3. `Selectors and Mappers`
4. `MenuEditor`
5. `Fluent Builders`

Aquesta progressió ajuda a entendre primer els conceptes bàsics i després la façana pública completa.