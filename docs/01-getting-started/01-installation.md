# Installation

La llibreria **Dynamic Menu** es pot utilitzar de diferents maneres segons el tipus de projecte.

Versió mínima recomanada de Java:

```
Java 21+
```

---

# 1. Projecte Java simple (fitxers font)

La forma més senzilla és **copiar els fitxers font dins del teu projecte**.

Només cal afegir els paquets de la llibreria.

Estructura de paquets recomanada:

```
menu
menu.action
menu.config
menu.editor
menu.hook
menu.model
menu.selector
menu.snapshot
menu.snapshot.context
menu.wrappers
```

`DynamicMenu` és la façana principal de la llibreria. Encara que internament hi ha classes a `menu.snapshot`, l'ús normal dels snapshots es fa a través dels wrappers de `DynamicMenu`.

Importacions habituals:

```java
import menu.DynamicMenu;
import menu.model.MenuResult;
```

---

# 2. Projecte Java simple (JAR)

També pots utilitzar la llibreria com a **fitxer JAR precompilat**.

Versió actual:

```
dynamic-menu-1.4.4.jar
```

## Afegir el JAR manualment

Col·loca el JAR dins d'una carpeta del projecte, per exemple:

```
project/
 ├─ lib/
 │   └─ dynamic-menu-1.4.4.jar
 └─ src/
```

---

## Compilació (PowerShell)

```powershell
javac -cp "lib/dynamic-menu-1.4.4.jar" -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
```

Execució:

```powershell
java -cp "out;lib/dynamic-menu-1.4.4.jar" com.example.Main
```

---

## Compilació (Bash / Linux / macOS)

```bash
javac -cp "lib/dynamic-menu-1.4.4.jar" -d out $(find src -name "*.java")
```

Execució:

```bash
java -cp "out:lib/dynamic-menu-1.4.4.jar" com.example.Main
```

---

# 3. Maven

Si utilitzes **Maven**, pots declarar la dependència al `pom.xml`.

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>dynamic-menu</artifactId>
    <version>1.4.4</version>
</dependency>
```

---

# 4. Gradle

Si utilitzes **Gradle**, pots afegir la dependència així.

```groovy
dependencies {
    implementation 'com.example:dynamic-menu:1.4.4'
}
```

---

# Notes

- `DynamicMenu` actua com a **façana principal de la llibreria**
- la majoria de funcionalitats es troben sota el paquet `menu`
- la documentació completa es pot consultar a `docs/`