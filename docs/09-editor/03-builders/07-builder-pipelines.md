# Builder Pipelines

Els builders es poden encadenar per formar **pipelines d'operacions**.

Això permet expressar diverses transformacions consecutives amb una sintaxi fluida i coherent.

Les crides `thenX()` no executen operacions immediatament.
Només afegeixen passos al pipeline.

## Idea general

Un pipeline permet:

- començar amb una selecció o configuració inicial
- continuar amb altres tipus d’operació
- executar només al final

Això fa que la intenció del codi sigui més clara.

## Exemple

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenReplace()
    .whereAny()
    .label("Temporal")
    .thenSort()
    .byLabel()
    .apply();
```

Pipeline conceptual:

```
remove → replace → sort → apply
```

## Execució terminal

L'execució real només passa quan es crida una operació terminal com:

- `execute()`
- `apply()`
- `count()`
- `exists()`

Fins a aquest moment, el pipeline només descriu què s’ha de fer.

## Herència d'estat entre passos

A més d'afegir una nova operació al pipeline, `thenX()` també pot decidir si el builder següent hereta part de l'estat fluent actual.

[...]

## Benefici principal

Aquest model és útil quan vols construir operacions més expressives, reutilitzant el context acumulat entre builders.

També ajuda a separar clarament:

- la descripció del pipeline
- la política d’herència
- l’operació terminal que realment executa els canvis

## Operation Pipeline Optimizer (OPO)

Els pipelines són gestionats per un sistema intern anomenat
**Operation Pipeline Optimizer (OPO)**.

De manera simplificada:

- optimitza el pipeline mentre el construeixes
- i fa una passada final abans d’executar-lo

Això permet evitar treball innecessari sense afectar la llegibilitat del codi.

## Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;
```