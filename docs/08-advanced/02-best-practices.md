# Best Practices

Aquesta llibreria és flexible, però hi ha algunes pràctiques que acostumen a produir codi més clar i mantenible.

## 1. Tria bé el tipus d'acció

- usa `SimpleMenuAction` quan no cal ni context ni menú
- usa `MenuAction` quan només necessites context
- usa `MenuRuntimeAction` quan realment has de modificar el menú o navegar

No facis servir `MenuRuntimeAction` per defecte si no la necessites.

## 2. Usa context per compartir estat, no per gestionar el menú

El context és molt útil per compartir dades d'aplicació entre opcions, però la navegació i l'estructura del menú haurien de continuar a `DynamicMenu`.

Bon ús del context:

- usuari actual
- serveis d'aplicació
- dades de sessió
- configuració comuna

Mal ús del context:

- guardar llistes d'opcions del menú
- substituir snapshots
- controlar manualment el bucle de `run()`

## 3. Mantén el selector separat de la lògica del menú

El selector només hauria de resoldre **quina opció s'ha triat**.

No és necessari forçar `implements MenuSelector` si ja tens una utilitat que es pot adaptar netament a la signatura requerida.

## 4. No activis `modifiableDuringRun(true)` si no cal

Per defecte, és millor construir el menú abans d'executar-lo.

Activa la modificació durant l'execució només quan realment necessitis:

- canviar títols en runtime
- afegir o treure opcions des d'una acció
- navegar amb snapshots sobre el mateix menú

## 5. Decideix bé entre snapshots i menús fills

### Prefereix snapshots si:

- el canvi d'estat és curt
- vols reutilitzar el mateix menú
- necessites navegació lleugera

### Prefereix menús fills si:

- el submenú és gran
- té vida pròpia
- vols separar millor responsabilitats

## 6. Dona noms consistents als snapshots registrats

Si fas servir `saveCurrentAs(...)` o `registerSnapshot(...)`, utilitza noms estables i fàcils d'entendre, per exemple:

- `main`
- `settings`
- `admin`
- `confirm-delete`

Això evita confusions quan el menú creix.

## 7. Fes que cada opció retorni un `MenuResult` clar

És millor que cada acció deixi explícitament què ha de passar després.

```java
return MenuResult.continueLoop();
```

és més clar que amagar la intenció dins de lògica indirecta.

## 8. Utilitza hooks per a lògica transversal

Els hooks són una bona opció per a:

- logging
- mostrar capçaleres o estat de sessió
- mètriques
- validacions lleugeres

Evita posar als hooks la lògica principal d'una opció concreta.

## 9. Considera `autoCleanup` en menús llargs o molt dinàmics

Si el menú crea molts snapshots o molts menús fills, val la pena revisar la configuració de `MenuCleanupConfig`.

## 10. Prioritza la llegibilitat del flux

Quan dubtis entre una solució molt compacta i una altra més explícita, acostuma a ser millor la més fàcil de seguir. Això és especialment cert en:

- navegació entre menús
- ús de snapshots
- accions que modifiquen runtime
