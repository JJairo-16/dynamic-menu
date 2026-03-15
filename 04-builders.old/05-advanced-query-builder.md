# Advanced Query Builder

`QueryBuilder<T, C>` també pot formar part d’una **cadena declarativa d’operacions**.

Aquest patró permet construir pipelines d’edició sobre el menú. :contentReference[oaicite:3]{index=3}

---

# 1. Estat virtual del menú

Quan s’utilitza dins d’una cadena:

- les operacions anteriors **no modifiquen el menú real**
- es crea **una còpia temporal**
- les operacions pendents s’apliquen sobre aquesta còpia

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenQuery()
    .whereAny()
    .count();
```

La consulta es resol sobre el menú **després de simular el remove**.

---

# 2. Encadenar consultes

## `thenQuery()`

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Settings"))
    .thenQuery()
    .count();
```

Aquest mètode manté:

- selector
- rang
- operacions pendents

---

# 3. Encadenar modificacions

---

## `thenRemove()`

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenRemove()
    .execute();
```

El selector i el rang es transfereixen.

---

## `thenReplace()`

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Old"))
    .thenReplace()
    .label("New")
    .execute();
```

---

## `thenSort()`

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenSort()
    .byLabel()
    .apply();
```

Només es transfereix el rang.

---

# 4. Quan utilitzar el mode avançat

Aquest mode és útil per:

- construir pipelines declaratius
- inspeccionar l’estat abans de modificar
- reutilitzar selectors
- combinar consulta i edició

Exemple:

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .count();

MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenRemove()
    .execute();
```