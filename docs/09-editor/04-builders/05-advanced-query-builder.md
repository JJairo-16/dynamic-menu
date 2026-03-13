# Advanced Query Builder

`QueryBuilder<T, C>` també pot formar part d’una **cadena d’operacions declarativa**.

Aquest patró permet construir una seqüència d’operacions que **no s’executa immediatament**.

Les operacions s’apliquen només quan s’executa una operació terminal en un builder posterior.

---

# 1. Estat virtual del menú

Quan `QueryBuilder` s’utilitza dins d’una cadena:

- les operacions anteriors **no modifiquen el menú real**
- es crea **un menú temporal**
- les operacions pendents s’apliquen sobre aquesta còpia

Això permet que les consultes es resolguin sobre **l’estat virtual de la cadena**.

Exemple conceptual:

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenQuery()
    .whereAny()
    .count();
```

La consulta es resol sobre el menú **després de la simulació del remove**.

---

# 2. Encadenar noves consultes

## `thenQuery()`

Permet continuar amb una nova consulta mantenint:

- el mateix menú
- les operacions pendents
- el mateix selector
- el mateix rang

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Settings"))
    .thenQuery()
    .count();
```

Aquest mètode és útil quan es construeixen consultes complexes reutilitzant la mateixa selecció.

---

# 3. Encadenar operacions de modificació

`QueryBuilder` pot transferir el selector i el rang a altres builders.

---

## `thenRemove()`

Continua amb una operació de remove.

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenRemove()
    .whereAny()
    .execute();
```

El selector i el rang de la query es transfereixen al `RemoveBuilder`.

---

## `thenReplace()`

Continua amb una operació de replace.

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Old"))
    .thenReplace()
    .with(newOption)
    .execute();
```

La selecció inicial es reutilitza per a la substitució.

---

## `thenSort()`

Continua amb una operació d’ordenació.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenSort()
    .byLabel()
    .execute();
```

En aquest cas només es transfereix **el rang**, ja que `SortBuilder` no treballa amb selectors.

---

# 4. Quan usar el mode avançat

El mode avançat és útil quan vols:

- inspeccionar el menú abans de modificar-lo
- reutilitzar la mateixa lògica de selecció
- construir pipelines declaratius d’edició de menú
- combinar consultes i modificacions en una sola cadena

Exemple complet:

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .count();

MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenRemove()
    .whereAny()
    .execute();
```

Aquest estil permet separar clarament **consulta** i **modificació** mantenint una API fluida.