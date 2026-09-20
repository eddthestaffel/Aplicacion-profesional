# Aplicación Profesional — Sistema de Reservas

Aplicación Android correspondiente al rol **Profesional** del caso de estudio "Reserva de horas: Cliente y Profesional".
La aplicación permite a un profesional consultar y gestionar las reservas almacenadas en **Cloud Firestore**, compartiendo la información con la aplicación Cliente mediante una colección común de reservas.

## Tecnologías utilizadas
* Android Studio
* Kotlin
* Android SDK
* Firebase
* Cloud Firestore
* RecyclerView
* XML para las interfaces

## Funcionalidades

La aplicación Profesional permite:
* Consultar las reservas almacenadas en Firestore.
* Filtrar reservas por estado.
* Filtrar reservas por fecha.
* Visualizar el detalle de una reserva.
* Aceptar una reserva solicitada.
* Rechazar una reserva indicando una observación.
* Reprogramar una reserva indicando una nueva fecha y hora.
* Recibir actualizaciones de las reservas mediante listeners de Firestore.
* Mostrar mensajes de error cuando ocurre un problema de conexión o permisos.

### Estados de una reserva

Las reservas pueden tener los siguientes estados:

* `SOLICITADA`
* `ACEPTADA`
* `RECHAZADA`
* `REPROGRAMADA`

## Requisitos

Para ejecutar el proyecto se necesita:
* Android Studio.
* JDK compatible con la configuración del proyecto.
* Un dispositivo Android o emulador.
* Conexión a Internet para utilizar Firestore.
* Acceso al proyecto de Firebase utilizado por la aplicación.

## Configuración de Firebase

La aplicación utiliza **Cloud Firestore** como sistema de almacenamiento.
La aplicación Profesional y la aplicación Cliente utilizan el mismo proyecto de Firebase y la misma colección:

```text
reservas
```

La configuración de Firebase debe realizarse mediante el archivo `google-services.json` correspondiente al proyecto de Firebase.

## Ejecución

1. Clonar o descargar este repositorio.
2. Abrir el proyecto desde Android Studio.
3. Configurar Firebase con el archivo `google-services.json`.
4. Sincronizar el proyecto con Gradle.
5. Ejecutar la aplicación en un dispositivo Android o emulador.
6. Desde la pantalla principal, ingresar al listado de reservas.

Para poder instalar se requiere la depuración USB activa, solo se puede cuando el modo desarrollador está activo, si eres de xiaomi debes verificar que la opción 
instalar mediante USB esté activa.

## Gestión de reservas

Desde el listado de reservas se pueden utilizar los filtros de:

* Estado.
* Fecha.

Al seleccionar una reserva se muestra su información detallada.
Cuando una reserva se encuentra en estado `SOLICITADA`, el profesional puede:

* Aceptarla.
* Rechazarla indicando una observación.
* Reprogramarla indicando una nueva fecha y hora.

Una vez modificada la reserva, el cambio se almacena en Firestore para que pueda ser visualizado por la aplicación Cliente.

## Estructura principal

La aplicación utiliza un repositorio para centralizar las operaciones relacionadas con Firestore.

```text
com.example.profesional
├── MainActivity
├── ReservasActivity
├── DetalleReservaActivity
├── ReservaAdapter
└── ReservaRepository
```

### Componentes principales

**MainActivity**
Pantalla principal de la aplicación y acceso al listado de reservas.

**ReservasActivity**
Muestra las reservas almacenadas en Firestore y permite filtrarlas por estado y fecha.

**DetalleReservaActivity**
Muestra el detalle de una reserva y permite gestionarla cuando corresponde.

**ReservaAdapter**
Se encarga de mostrar las reservas dentro del `RecyclerView`.

**ReservaRepository**
Centraliza las operaciones de lectura y actualización de reservas en Cloud Firestore.

## Firestore
La colección utilizada por la aplicación es:

```text
reservas
```

Cada documento representa una reserva.
Los principales campos utilizados son:

```text
idCliente
nombreCliente
servicio
profesional
fecha
hora
estado
observacion
```

Para las reservas reprogramadas también se utilizan:

```text
fechaNueva
horaNueva
```

## Reglas de desarrollo

Durante el desarrollo se utilizan reglas de Firestore que permiten realizar las operaciones necesarias para probar la aplicación.
Estas reglas son únicamente para desarrollo y deben ser restringidas antes de utilizar la aplicación en un entorno de producción.

## Manejo de errores

La aplicación contempla diferentes situaciones de error, entre ellas:
* Falta de conexión a Internet.
* Campos obligatorios sin completar.
* Intentos de modificar reservas que ya no están en estado `SOLICITADA`.
* Reserva inexistente.
* Rechazo sin una observación.
* Problemas de permisos de Firestore.

En caso de no existir conexión al realizar una modificación, se muestra:

```text
Sin conexión. Intente nuevamente
```

## Proyecto

Este repositorio corresponde a la aplicación **Profesional** del caso de estudio de reservas mediante Cloud Firestore.
La aplicación Cliente se desarrolla como un proyecto independiente, pero ambas aplicaciones utilizan el mismo proyecto de Firebase y la colección `reservas`.

## Autor

Proyecto académico desarrollado para la asignatura de desarrollo de aplicaciones Android.
