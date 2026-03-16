# Range

`Range` representa un rang d’índexs del tipus:

```text
[fromInclusive, toExclusive)
```

Això vol dir que:

- l’inici està inclòs
- el final està exclòs

Per exemple, `Range.of(1, 4)` afecta els índexs:

- `1`
- `2`
- `3`

## Obtenció

S'importa utilitzant:

```java
import menu.editor.Range;
```

## Crear un rang complet

```java
Range range = Range.all();
```

Aquest rang cobreix tot el menú.

## Crear un rang específic

```java
Range range = Range.of(2, 5);
```

Aquest rang cobreix des de l’índex `2` fins abans de l’índex `5`.

## Índexs negatius

`Range` també admet **índexs negatius**.

Quan es fa servir un valor negatiu, es compta des del final de la llista:

- `-1` és l’últim element
- `-2` és el penúltim
- `-3` és el tercer comptant des del final

Exemple:

```java
Range range = Range.of(-3, -1);
```

Si el menú té mida `10`, aquest rang equivaldrà a:

```text
[7, 9)
```

És a dir, afectarà els índexs:

```text
7 i 8
```

Els índexs negatius es resolen automàticament quan el rang es construeix dins dels builders del `MenuEditor`.

## Validacions

`Range` valida els valors en construir-se.

No es permet:

- un índex final menor que l’inicial

Exemples no vàlids:

```java
Range.of(5, 2);
```

## Comprovació manual

Pots comprovar si un índex entra dins del rang:

```java
boolean inside = range.contains(3);
```

## Ajust a la mida real

Quan un rang es vol aplicar sobre una llista concreta, es pot ajustar amb `clamp(...)`.

```java
Range requested = Range.of(2, 10);
Range effective = requested.clamp(4);
```

Si la mida real del menú és `4`, el rang efectiu passarà a cobrir només els índexs vàlids.

En aquest cas, els índexs disponibles són:

```text
0 1 2 3
```

Per tant, el rang final serà equivalent a:

```text
des de l’índex 2 fins al final de la llista
```

És a dir, afectarà només:

```text
2 i 3
```

En termes formals això s’expressa com:

```text
[2, 4)
```

on:

- `[` indica que l’inici **està inclòs**
- `)` indica que el final **no està inclòs**

Així, el rang `[2, 4)` inclou:

```text
2, 3
```

però **no inclou `4`**.

## Quan s’utilitza

`Range` apareix sobretot en operacions de `MenuEditor` com:

- `removeIf(...)`
- `replaceIf(...)`
- `replaceLabelIf(...)`
- `replaceActionIf(...)`
- `sortByLabel(...)`
- mètodes de cerca com `indexOfFirst(...)` o `indexesOf(...)`

## Exemple típic

```java
MenuEditor.replaceLabelIf(
    menu,
    (index, option) -> option.label().startsWith("Temp"),
    "Temporal",
    Range.of(0, 5)
);
```

Això només considera coincidències dins dels cinc primers elements.