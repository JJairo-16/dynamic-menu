# Operation Pipeline Optimizer (OPO)

L’**Operation Pipeline Optimizer (OPO)** és el component responsable d’optimitzar els pipelines d’operacions abans de la seva execució.

El seu objectiu és **reduir treball innecessari** sense canviar el resultat final.

## Què fa l’OPO

L’OPO analitza la seqüència d’operacions i aplica transformacions segures per:

- eliminar operacions redundants
- evitar passos que no tenen efecte
- simplificar seqüències d’operacions compatibles
- reduir reordenacions innecessàries
- evitar càlculs repetits

En general, intenta convertir el pipeline en una forma més **eficient però equivalent**.

## Tipus d’optimització

### Optimització incremental

Durant el chaining (`thenX()`):

- es processen només operacions adjacents
- s’apliquen regles ràpides i locals
- es manté el cost molt baix

Això evita que el pipeline creixi amb operacions trivials.

### Optimització global

Abans de l’execució:

- es considera tot el pipeline
- s’apliquen regles més completes
- es poden detectar redundàncies no locals

Aquesta fase només s’aplica si és necessari.

## Què evita

L’OPO està dissenyat per evitar:

- executar operacions que no fan res
- aplicar múltiples reordenacions consecutives inútils
- repetir transformacions equivalents
- mantenir passos intermedis que es cancel·len entre si

## Exemples

### Exemple 1: eliminació de no-op

Pipeline original:

```
no-op → remove → no-op → apply
```

Després de l’OPO:

```
remove → apply
```

---

### Exemple 2: reordenacions redundants

Pipeline original:

```
sort → sort → apply
```

Després de l’OPO:

```
sort → apply
```

---

### Exemple 3: operacions cancel·lades

Pipeline original:

```
replace(A→B) → replace(A→B) → apply
```

Després de l’OPO:

```
replace(A→B) → apply
```

---

### Exemple 4: simplificació de pipeline

Pipeline original:

```
remove → no-op → sort → sort → apply
```

Després de l’OPO:

```
remove → sort → apply
```

## Notes

- L’OPO **no canvia el resultat**, només la manera d’arribar-hi
- Les optimitzacions són **conservadores i segures**
- Algunes optimitzacions avançades són **opt-in**

---

Aquest sistema permet escriure pipelines clars i expressius, mentre que l’execució es manté eficient automàticament.