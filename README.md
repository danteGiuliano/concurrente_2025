# Parque de Diversiones - Simulación Concurrente

## Descripción

Simulación de un parque de diversiones con múltiples atracciones trabajando concurrentemente. El sistema gestiona el flujo de visitantes, sincronización de recursos y entrega de fichas mediante mecanismos de Java concurrency.

## Requisitos

- Java 11 o superior
- Compilador Java (javac)

## Estructura del Proyecto

```
src/
├── Main.java                    # Punto de entrada
├── Simulacion.java              # Fábrica de visitantes
└── parque/
    ├── Parque.java              # Coordinador central
    ├── Visitante.java           # Agente concurrente
    ├── Molinete.java            # Control de ingreso
    ├── Reloj.java               # Gestión de horarios
    ├── Billetera.java           # Sistema de fichas
    ├── Ticketera.java           # Punto de intercambio de fichas
    ├── juegosMecanicos/
    │   ├── MontaniaRusa.java
    │   └── AutitosChocadores.java
    ├── realidadVirtual/
    │   ├── RealidadVirtual.java
    │   ├── Encargado.java
    │   ├── EquipoVR.java
    │   └── Pedido.java
    ├── carreraDeGomones/
    │   ├── CarreraGomones.java
    │   ├── SistemaGomones.java
    │   ├── StandBicicletas.java
    │   ├── TrenInterno.java
    │   ├── SistemaBolsos.java
    │   ├── Bolso.java
    │   ├── Camioneta.java
    │   ├── ControlLargada.java
    │   └── SistemaPremios.java
    ├── areaPremios/
    │   ├── AreaPremios.java
    │   ├── Encargado.java
    │   └── Premio.java
    ├── comedor/
    │   ├── Comedor.java
    │   └── Mesa.java
    └── teatro/
        ├── Teatro.java
        └── Asistente.java
```

## Compilación

```bash
cd src
javac -d ../out *.java parque/**/*.java util/*.java
```

## Ejecución

```bash
cd out
java Main
```

## Parámetros de Configuración

En `Main.java` se pueden modificar:

| Parámetro | Descripción | Valor por defecto |
|-----------|-------------|-------------------|
| `APERTURA` | Tiempo abierto (ms) | 12000 |
| `CIERRE` | Tiempo cerrado (ms) | 6000 |
| `VISITANTES` | Cantidad de visitantes | 300 |
| `MOLINETES` | Cantidad de molinetes | 3 |

## Probabilidades de Visita

Cada visitante tiene las siguientes probabilidades de elegir una atracción:

- Montaña Rusa: 20%
- Autitos Chocadores: 20%
- Comedor: 20%
- Teatro: 20%
- Realidad Virtual: 20%
- Carrera de Gomones: 25%
- Área de Premios: 10% (requiere tener fichas)

## Horarios del Parque

- **Horario de atención**: 09:00 a 18:00 (ingreso)
- **Cierre de actividades**: 19:00
- **Evacuación completa**: 23:00

## Mecanismos de Sincronización Utilizados

- **Semaphore**: Control de acceso con fairness
- **ReentrantLock**: Exclusión mutua con condiciones
- **CyclicBarrier**: Sincronización de grupos
- **Exchanger**: Intercambio de objetos entre hilos
- **BlockingQueue**: Colas thread-safe
- **AtomicInteger/AtomicLong**: Contadores thread-safe

## Troubleshooting

### El programa no compila
Verificar que Java 11+ esté instalado: `java -version`

### Hay muchos visitantes esperando indefinidamente
Verificar que el reloj esté funcionando y que las atracciones tengan capacidad disponible.

### Deadlock o bloqueos inesperados
Revisar los logs generados para identificar el punto de bloqueo.
