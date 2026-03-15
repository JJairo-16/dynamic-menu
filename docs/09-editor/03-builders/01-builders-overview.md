# Builders Overview

`MenuEditor` ofereix una API fluent basada en diversos builders.

Aquesta capa fluent permet definir operacions sobre un `DynamicMenu`
de manera progressiva, llegible i composable.

Els builders principals són:

- QueryBuilder
- RemoveBuilder
- ReplaceBuilder
- SortBuilder

Cada builder cobreix una responsabilitat concreta:

- consultar opcions
- eliminar coincidències
- substituir contingut o comportament
- reordenar opcions

## Ordre recomanat d'aprenentatge

Per entendre l'API fluent, és recomanable seguir aquest ordre:

1. QueryBuilder
2. RemoveBuilder
3. ReplaceBuilder
4. SortBuilder

Aquest ordre segueix la progressió natural:

```
consulta → modificació → transformació → reorganització
```

Primer s'aprèn a **inspeccionar el menú**, després a **modificar-lo**,
després a **transformar opcions**, i finalment a **reordenar-les**.

## Arquitectura interna

Els builders comparteixen una jerarquia comuna.

Això permet reutilitzar conceptes com selecció, rang, límit
i recorregut en diferents tipus d’operació.

```
AbstractChainableMenuBuilder
 ├─ AbstractRangedBuilder
 │   ├─ SortBuilder
 │   └─ AbstractSelectableRangedBuilder
 │       ├─ QueryBuilder
 │       └─ AbstractEditBuilder
 │           ├─ RemoveBuilder
 │           └─ ReplaceBuilder
```

Aquesta arquitectura permet unificar mètodes com:

- `range(...)`
- `where(...)`
- `whereLabel(...)`
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