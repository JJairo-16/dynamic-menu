# Builders Overview

`MenuEditor` ofereix una API fluent basada en diversos builders.

Aquesta capa fluent permet definir operacions sobre un `DynamicMenu`
de manera progressiva, llegible i composable.

Els builders principals són:

- QueryBuilder
- RemoveBuilder
- ReplaceBuilder
- SortBuilder
- ShuffleBuilder

Cada builder cobreix una responsabilitat concreta:

- consultar opcions
- eliminar coincidències
- substituir contingut o comportament
- reordenar opcions
- barrejar opcions

## Ordre recomanat d'aprenentatge

Per entendre l'API fluent, és recomanable seguir aquest ordre:

1. QueryBuilder
2. RemoveBuilder
3. ReplaceBuilder
4. SortBuilder
5. ShuffleBuilder

Aquest ordre segueix la progressió natural:

```
consulta → modificació → transformació → reorganització → variació d’ordre
```

Primer s'aprèn a **inspeccionar el menú**, després a **modificar-lo**, després a **transformar opcions**, i finalment a **reordenar-les o barrejar-les**.

## Arquitectura interna

Els builders comparteixen una jerarquia comuna.

Això permet reutilitzar conceptes com selecció, rang, límit
i recorregut en diferents tipus d’operació.

```
AbstractChainableMenuBuilder
 ├─ AbstractRangedBuilder
 │   ├─ SortBuilder
 │   ├─ ShuffleBuilder
 │   └─ AbstractSelectableRangedBuilder
 │       ├─ QueryBuilder
 │       └─ AbstractEditBuilder
 │           ├─ RemoveBuilder
 │           └─ ReplaceBuilder
```

Aquesta arquitectura permet unificar mètodes com:

- `range(...)`
- `where(...)`
- `whereIndex(...)`
- `whereLabel(...)`
- `whereLabelEquals(...)`
- `whereLabelEqualsIgnoreCase(...)`
- `whereLabelStartsWidth(...)`
- `whereLabelEndsWidth(...)`
- `whereAny()`
- `limit(...)`
- `reverse(...)`
- `first()`
- `last()`
- `all()`

## Idea general d'ús

Tots aquests builders segueixen una idea semblant:

1. seleccionar opcions
2. limitar l’àmbit d’actuació
3. definir l’operació final
4. executar-la o resoldre-la

Això fa que l’API sigui coherent entre builders diferents.

## Herència en l'encadenament

Quan un builder continua amb `thenX()`, el builder següent pot heretar una part de l'estat fluent anterior.

La política predeterminada és aquesta:

- des de `QueryBuilder`, `thenQuery()`, `thenRemove()` i `thenReplace()` hereten selector i rang
- des de `QueryBuilder`, `thenSort()` i `thenShuffle()` hereten només el rang
- des de `RemoveBuilder`, `ReplaceBuilder`, `SortBuilder` i `ShuffleBuilder`, per defecte no s'hereta res

Quan cal sobreescriure aquest comportament, es pot usar la variant `thenX(InheritanceMode)`.

Modes disponibles:

- `InheritanceMode.NONE`
- `InheritanceMode.RANGE`
- `InheritanceMode.SELECTION`
- `InheritanceMode.ALL`

Això permet distingir clarament entre:

- comportament fluent per defecte
- herència explícita demanada per l'usuari.

### Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;

```