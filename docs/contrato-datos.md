# Contrato de datos — Reservas

Este documento define la estructura de los datos utilizados por las aplicaciones **Cliente** y **Profesional** para comunicarse mediante **Cloud Firestore**.
Ambas aplicaciones utilizan el mismo proyecto de Firebase y comparten la colección `reservas`.

## Colección

```text
reservas
```

Cada documento dentro de esta colección representa una reserva.

## Estructura de una reserva

| Campo           | Tipo   | Descripción                                         |
| --------------- | ------ | --------------------------------------------------- |
| `idCliente`     | String | Identificador del cliente que realiza la reserva.   |
| `nombreCliente` | String | Nombre del cliente.                                 |
| `servicio`      | String | Servicio seleccionado por el cliente.               |
| `profesional`   | String | Profesional asociado a la reserva.                  |
| `fecha`         | String | Fecha original de la reserva. Formato `YYYY-MM-DD`. |
| `hora`          | String | Hora original de la reserva. Formato `HH:mm`.       |
| `estado`        | String | Estado actual de la reserva.                        |
| `observacion`   | String | Observación asociada a la reserva.                  |

## Campos de reprogramación

Cuando una reserva es reprogramada, se agregan los siguientes campos:

| Campo        | Tipo   | Descripción                            |
| ------------ | ------ | -------------------------------------- |
| `fechaNueva` | String | Nueva fecha propuesta para la reserva. |
| `horaNueva`  | String | Nueva hora propuesta para la reserva.  |

Los campos `fechaNueva` y `horaNueva` solamente se utilizan cuando la reserva tiene estado `REPROGRAMADA`.

## Estados

Una reserva puede encontrarse en uno de los siguientes estados:

```text
SOLICITADA
ACEPTADA
RECHAZADA
REPROGRAMADA
```

### `SOLICITADA`

Estado inicial de una reserva creada por la aplicación Cliente.
El profesional puede:

* Aceptarla.
* Rechazarla.
* Reprogramarla.

### `ACEPTADA`

La reserva fue aceptada por el profesional.

### `RECHAZADA`

La reserva fue rechazada por el profesional.
En este estado se debe almacenar una `observacion` indicando el motivo o información correspondiente al rechazo.

### `REPROGRAMADA`

El profesional propuso una nueva fecha y hora.
La nueva información se almacena en:

```text
fechaNueva
horaNueva
```

## Ejemplo

Un documento de la colección `reservas` puede tener una estructura similar a:

```text
reservas
└── <id del documento>
    ├── idCliente: "C001"
    ├── nombreCliente: "Juan Pérez"
    ├── servicio: "Consulta general"
    ├── profesional: "Dr. Pérez"
    ├── fecha: "2026-09-25"
    ├── hora: "10:30"
    ├── estado: "SOLICITADA"
    └── observacion: ""
```

Después de una reprogramación:

```text
reservas
└── <id del documento>
    ├── idCliente: "C001"
    ├── nombreCliente: "Juan Pérez"
    ├── servicio: "Consulta general"
    ├── profesional: "Dr. Pérez"
    ├── fecha: "2026-09-25"
    ├── hora: "10:30"
    ├── estado: "REPROGRAMADA"
    ├── observacion: ""
    ├── fechaNueva: "2026-09-27"
    └── horaNueva: "11:00"
```

## Comunicación entre aplicaciones

La comunicación entre ambas aplicaciones no se realiza directamente.
El flujo es:

```text
Aplicación Cliente
       │
       │ crea/consulta reserva
       ▼
   Firestore
   colección
   reservas
       ▲
       │
       │ consulta/modifica reserva
       │
Aplicación Profesional
```

La aplicación Cliente puede crear reservas con estado `SOLICITADA`.
La aplicación Profesional consulta estas reservas y puede modificar su estado o proponer una nueva fecha y hora.
Los cambios almacenados en Firestore pueden ser detectados por la aplicación Cliente mediante listeners.
