# Java SOLID POO Project

## Descripción

Proyecto desarrollado en **Java** orientado a la aplicación de conceptos de **Programación Orientada a Objetos (POO)** y principios de diseño de software.

El objetivo principal del proyecto es implementar una estructura de código organizada, mantenible y escalable, aplicando buenas prácticas de ingeniería de software como **abstracción, encapsulamiento, herencia, polimorfismo y principios SOLID**.

---

## Tecnologías utilizadas

* **Java**
* Programación Orientada a Objetos (POO)
* Interfaces y clases abstractas
* Colecciones de Java (`ArrayList`, entre otras)
* Manejo de archivos para persistencia de datos
* UML para modelado del sistema
* Git / GitHub para control de versiones

---

## Conceptos aplicados

### Programación Orientada a Objetos

El proyecto implementa los principales pilares de la POO:

* **Encapsulamiento:** protección del estado interno de los objetos mediante atributos privados y métodos de acceso.
* **Abstracción:** definición de comportamientos generales mediante clases abstractas e interfaces.
* **Herencia:** reutilización de código mediante relaciones entre clases.
* **Polimorfismo:** implementación de diferentes comportamientos a través de una misma interfaz.

---

## Principios SOLID aplicados

### Single Responsibility Principle (SRP)

Cada clase posee una única responsabilidad, evitando mezclar lógica de negocio, persistencia y presentación.

### Open/Closed Principle (OCP)

El diseño permite extender funcionalidades sin modificar código existente.

### Liskov Substitution Principle (LSP)

Las clases derivadas pueden utilizarse mediante sus abstracciones sin alterar el comportamiento esperado.

### Interface Segregation Principle (ISP)

Las interfaces están diseñadas con responsabilidades específicas evitando métodos innecesarios.

### Dependency Inversion Principle (DIP)

El código depende de abstracciones y no de implementaciones concretas, facilitando modificaciones futuras.

---

## Arquitectura del proyecto

La estructura del proyecto se organiza separando responsabilidades:

```
src
│
├── modelo
│   └── Clases de entidades del sistema
│
├── servicios
│   └── Lógica de negocio
│
├── persistencia
│   └── Gestión de almacenamiento de datos
│
└── main
    └── Ejecución del programa
```

---

## Modelado UML

El proyecto fue diseñado utilizando diagramas UML para representar:

* Clases del sistema
* Relaciones entre objetos
* Herencia
* Asociaciones
* Composiciones
* Dependencias

---

## Objetivos del proyecto

* Aplicar conceptos avanzados de Programación Orientada a Objetos.
* Diseñar un sistema modular y mantenible.
* Practicar buenas prácticas utilizadas en desarrollo profesional.
* Mejorar la comprensión de arquitectura y diseño de software.

---

## Autores

**Carla Corvalán Portillo**
**Pablo Gustavo García**
**Vicente Reynaldo Ciancio**


---

## Licencia

Proyecto realizado con fines educativos y de aprendizaje.
