# Basic Concepts

El sistema de menú està format per diversos components principals.

## DynamicMenu

És el motor principal del menú.

S'encarrega de:

- executar el bucle del menú
- gestionar opcions
- gestionar snapshots
- executar hooks

## MenuOption

Representa una opció seleccionable del menú.

Cada opció té:

- una etiqueta
- una acció associada

## MenuResult

Defineix què passa després d'executar una acció.

Possibles resultats:

- continuar el bucle (`MenuResult.repeatLoop()`)
- retornar un valor (`MenuResult.returnValue(value)`)
- sortir del menú (`MenuResult.exitMenu()` → `null`)

## MenuSelector

És el component responsable de la selecció d'opcions.

Normalment s'encarrega de:

- mostrar les opcions
- llegir l'entrada de l'usuari

> Es proporcionat al crear el menú.