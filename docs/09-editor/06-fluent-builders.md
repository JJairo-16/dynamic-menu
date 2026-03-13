# Fluent Builders

A més dels mètodes estàtics, `MenuEditor` ofereix una API fluïda basada en dos builders públics:

- `RemoveBuilder<T, C>`
- `ReplaceBuilder<T, C>`

Aquests builders serveixen per descriure operacions d’edició de manera declarativa i llegible.

Són especialment útils quan necessites combinar:

- selectors
- rangs
- límits
- recorregut invers
- transformacions de label, acció o opció completa

---

# Builders

Els Builders es tracten de constructors que permeten modificar o transformar dades dins del flux del menu editor.

- **[Query Builder](07-builders/01-query-builder.md):**
  Permet **construir consultes sobre dades** per tal de **filtrar, buscar o obtenir informació específica** dins d’una col·lecció.

- **[Remove Builder](07-builders/02-remove-builder.md):** Elimina camps o elements d’una estructura de dades.
- **[Sort Builder](07-builders/03-sort-builder.md):** Ordena col·leccions de dades segons un o diversos camps.
- **[Replace Builder](07-builders/04-replace-builder.md):** Substitueix valors o continguts dins d’una estructura de dades.

---

# Errors habituals

## Oblidar el selector

A tots dos builders, `where(...)` és obligatori si no has passat ja el selector a `MenuEditor.remove(menu, selector)` o `MenuEditor.replace(menu, selector)`.

Aquest codi és incorrecte:

```java
MenuEditor.replace(menu)
    .label("Configuració")
    .execute();
```

Perquè no hi ha condició de selecció.

## Oblidar la transformació al replace

En `ReplaceBuilder`, a més del selector, també cal definir **què** s’ha de canviar.

Aquest codi també és incorrecte:

```java
MenuEditor.replace(menu)
    .where(MenuEditor::alwaysTrue)
    .execute();
```

Perquè no s’ha definit ni `label(...)`, ni `action(...)`, ni `option(...)`, ni `map(...)`.

## Confondre `first()` i `last()`

- `first()` busca en sentit normal i limita a una coincidència
- `last()` busca en sentit invers i limita a una coincidència

---

# Recomanació d’aprenentatge

Si un usuari nou vol dominar aquesta llibreria, el camí recomanat és:

1. aprendre primer `removeIf(...)` i `removeFirstIf(...)`
2. entendre `Range`
3. entendre `EditConfig`
4. aprendre `RemoveBuilder`
5. aprendre `ReplaceBuilder`
6. combinar-ho amb els helpers de consulta de `MenuEditor`

Amb això ja es pot utilitzar pràcticament tota la superfície pública de l’editor.

## Ordre recomanat d'aprenentatge

Per facilitar la corba d'aprenentatge de l'API fluïda, es recomana presentar els
builders en el següent ordre:

1. **QueryBuilder** – operacions de consulta (no modifiquen el menú)
2. **RemoveBuilder** – eliminació d'opcions
3. **ReplaceBuilder** – substitució d'opcions o dels seus camps
4. **SortBuilder** – reorganització i ordenació

Aquest ordre segueix una progressió natural:

```
consulta → modificació simple → modificació estructural → reorganització
```

Primer s'introdueix la lectura del menú, després les modificacions bàsiques,
posteriorment les transformacions d'opcions i finalment les operacions
d'ordenació.

Tots els builders comparteixen el mateix contracte d'encadenament mitjançant
`AbstractChainableMenuBuilder`: les crides `thenX()` **no executen cap canvi
immediatament**, sinó que acumulen operacions pendents que s'aplicaran quan es
cridi una operació terminal com `execute()` o `apply()`.
