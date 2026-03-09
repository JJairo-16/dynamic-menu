# Policies

Aquesta pàgina agrupa les polítiques del menú per evitar que la resta de pàgines avançades es converteixin en una barreja de conceptes.

En aquest context, una *policy* és una regla de comportament que modifica com actuen determinats wrappers de `DynamicMenu`.

## 1. Política de duplicats

`DynamicMenu` permet consultar i definir la política de duplicats:

```java
MenuDuplicatePolicy current = menu.getDuplicatePolicy();
menu.setDuplicatePolicy(otherPolicy);
```

Aquesta política es delega al gestor intern de snapshots però es manté exposada com a wrapper públic del menú.

## Quan importa

La política de duplicats és rellevant quan:

- afegeixes opcions que poden repetir etiqueta
- registres snapshots amb noms potencialment repetits
- vols forçar un comportament consistent davant col·lisions

## Com documentar-la

Com que els valors concrets de `MenuDuplicatePolicy` poden evolucionar, la documentació d'alt nivell hauria d'explicar **quan** s'utilitza la policy i no només enumerar constants.

Un exemple típic:

```java
menu.setDuplicatePolicy(policy);
menu.addOption("Guardar", action);
```

## 2. Política de neteja

La política de neteja es controla amb:

```java
menu.autoCleanup(true);
menu.cleanupConfig(MenuCleanupConfig.defaults());
```

I es pot inspeccionar amb:

```java
boolean enabled = menu.isAutoCleanupEnabled();
MenuCleanupConfig config = menu.getCleanupConfig();
```

A diferència de la política de duplicats, aquí la policy no solament decideix una reacció puntual, sinó també la conservació d'estat al llarg del temps.

## Quan separar polítiques de navegació o runtime

Convé documentar-ho separat quan la configuració:

- afecta molts wrappers diferents
- no forma part del flux normal d'una acció concreta
- canvia el comportament global del menú

Això és exactament el que passa amb `MenuDuplicatePolicy` i `MenuCleanupConfig`.

## Relació amb la resta de pàgines

- usa **Snapshots** per entendre les operacions sobre estat
- usa **Menu Navigation** per entendre els patrons de flux
- usa **Runtime and Option Management** per entendre mutacions
- usa **Auto Cleanup** per aprofundir en la neteja
- usa aquesta pàgina per entendre **les regles globals que condicionen aquells wrappers**

## Recomanació pràctica

Quan afegeixis nous wrappers a `DynamicMenu`, pregunta't primer si introdueixen:

- una operació nova
- un patró de navegació nou
- una policy nova

Si és una policy, acostuma a ser millor documentar-la aquí i només enllaçar-la des de la resta de pàgines.
