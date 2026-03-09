# MenuSelector

`MenuSelector` és el component responsable de **seleccionar una opció del menú**.

Això permet separar:

- la lògica del menú
- la forma d'interactuar amb l'usuari

## Signatura

```java
public interface MenuSelector {
    int getOption(List<String> options, String title);
}
```

El mètode ha de retornar un número d'opció començant a **1**.

## No cal que sempre sigui una classe amb `implements`

Fer `implements MenuSelector` és una opció vàlida, però **no s'ha d'entendre com l'única forma correcta**.

El més recomanable és escollir l'estil segons el cas:

- **classe pròpia amb `implements`** si el selector està dissenyat específicament per a aquesta llibreria o si vols reutilitzar-lo molt
- **objecte adaptador** si ja tens una utilitat existent que encaixa amb la signatura de `MenuSelector`
- **lambda** si el comportament és petit i local

## Exemple amb una classe pròpia

```java
public final class ConsoleSelector implements MenuSelector {
    @Override
    public int getOption(List<String> options, String title) {
        // implementació pròpia
        return 1;
    }
}
```

Això té sentit si el selector és una peça central del teu sistema de menús.

## Exemple amb un objecte ja existent

També pots reutilitzar una utilitat ja pensada per mostrar menús estàtics.
No cal que la classe implementi `MenuSelector` si prefereixes adaptar-la quan
construeixes el menú.

```java
DynamicMenu<String, Void> menu =
    DynamicMenu.withoutContext("Menú principal", utils.input.Menu::getOption);
```

Això és especialment útil quan ja tens una classe com aquesta:

```java
public class Menu {
    public static int getOption(List<String> options, String title) {
        // mostra opcions i retorna la selecció
    }
}
```

Aquest enfocament és perfectament vàlid i sovint millora la llegibilitat perquè no obliga a crear una classe nova només per satisfer la interfície.

## Exemple amb lambda

```java
MenuSelector selector = (options, title) -> {
    System.out.println(title);
    return 1;
};
```

És útil per proves, prototips o casos molt simples.

## Recomanació pràctica

No pensis en `MenuSelector` com una classe que sempre s'ha d'implementar, sinó com una **signatura funcional** que pots satisfer de diferents maneres.

Si tens una utilitat general reutilitzable, adaptar-la és sovint millor que forçar un `implements` només perquè sí.

## Avantatges de separar el selector

Permet utilitzar el mateix menú amb:

- consola
- GUI
- web
- tests automatitzats
