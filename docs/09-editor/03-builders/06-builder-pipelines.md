# Builder Pipelines

Els builders es poden encadenar per formar **pipelines d'operacions**.

Això permet expressar diverses transformacions consecutives
amb una sintaxi fluida i coherent.

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

Fins a aquest moment, el pipeline només descriu què s’ha de fer.

## Benefici principal

Aquest model és útil quan vols construir operacions més expressives,
reutilitzant el context acumulat entre builders.