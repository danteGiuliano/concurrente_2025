# UML - Carrera de Gomones

## Diagrama de Secuencia del Flujo

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────────┐     ┌───────────────┐     ┌────────────────┐
│  Visitante  │     │  CarreraGomones  │     │  ControlLargada│     │   Camioneta   │     │ SistemaGomones │
└──────┬──────┘     └────────┬─────────┘     └────────┬────────┘     └───────┬───────┘     └───────┬────────┘
       │                     │                       │                    │                    │
       │  participar()       │                       │                    │                    │
       │─────────────────────>│                       │                    │                    │
       │                     │                       │                    │                    │
       │                     │  usarBicicleta()      │                    │                    │
       │                     │───────────────────────>│                    │                    │
       │                     │  (o tren.viajar())     │                    │                    │
       │                     │<───────────────────────│                    │                    │
       │                     │                       │                    │                    │
       │                     │  obtenerBolso()        │                    │                    │
       │                     │─────────────────────────────────────────────>│                    │
       │                     │                       │                    │                    │
       │                     │  transportarBolso()   │                    │                    │
       │                     │─────────────────────────────────────────────>│                    │
       │                     │                       │                    │                    │
       │                     │                       │         ┌───────────│────────────────>│
       │                     │                       │         │ Viaje al destino          │
       │                     │                       │         │<──────────────────────────│
       │                     │                       │                    │                    │
       │                     │  obtenerGomon()       │                    │                    │
       │                     │───────────────────────────────────────────────────────────────>│
       │                     │                       │                    │                    │
       │                     │  esperarLargada()     │                    │                    │
       │                     │───────────────────────>│                    │                    │
       │                     │                       │                    │                    │
       │                     │                       │  +gomonesListos   │                    │
       │                     │                       │  await() si no    │                    │
       │                     │                       │  hay G gomones    │                    │
       │                     │                       │                    │                    │
       │                     │  verificarYLanzar()  │                    │                    │
       │                     │───────────────────────>│                    │                    │
       │                     │                       │  signalAll()      │                    │
       │                     │                       │<──────────────────│                    │
       │                     │                       │                    │                    │
       │                     │  Thread.sleep()       │                    │                    │
       │                     │  (descenso)           │                    │                    │
       │                     │───────────────────────>│                    │                    │
       │                     │                       │                    │                    │
       │                     │  registrarLlegada()   │                    │                    │
       │                     │───────────────────────>│                    │                    │
       │                     │                       │                    │                    │
       │                     │  retirarBolso()       │                    │                    │
       │                     │─────────────────────────────────────────────>│                    │
       │                     │                       │                    │                    │
       │                     │                       │  +bolsosEnDestino │                    │
       │                     │                       │  signalAll()      │                    │
       │                     │                       │<──────────────────│                    │
       │                     │                       │                    │                    │
       │                     │  entregarPremio()     │                    │                    │
       │                     │───────────────────────────────────────────────────────────────>│
       │                     │                       │                    │                    │
       │                     │  devolverGomon()      │                    │                    │
       │                     │───────────────────────────────────────────────────────────────>│
       │                     │                       │                    │                    │
```

## Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CARRERA DE GOMONES                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐  │
│  │ StandBicicletas │     │   TrenInterno     │     │  SistemaBolsos  │  │
│  │  (Semaphore)    │     │   (Semaphore)    │     │  (Semaphore)    │  │
│  └────────┬────────┘     └────────┬─────────┘     └────────┬────────┘  │
│           │                       │                       │              │
│           └───────────────────────┼───────────────────────┘              │
│                                   │                                          │
│                                   ▼                                          │
│                    ┌────────────────────────────┐                          │
│                    │     CarreraGomones          │                          │
│                    │     (Coordinador)           │                          │
│                    └─────────────┬──────────────┘                          │
│                                  │                                          │
│           ┌──────────────────────┼──────────────────────┐                │
│           │                      │                      │                  │
│           ▼                      ▼                      ▼                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐      │
│  │ ControlLargada  │  │  SistemaGomones  │  │ SistemaPremios       │      │
│  │ (Lock + Cond)   │  │  (Semaphore)    │  │                     │      │
│  └────────┬────────┘  └────────┬────────┘  └─────────────────────┘      │
│           │                     │                                            │
│           │                     │                                            │
│           └─────────────────────┘                                            │
│                                │                                             │
│                                ▼                                             │
│                    ┌────────────────────┐                                  │
│                    │     Camioneta       │                                  │
│                    │   (Thread)         │                                  │
│                    │  BlockingQueue     │                                  │
│                    └────────────────────┘                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                               HILOS                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌───────────┐  │
│  │ Visitante 1 │    │ Visitante 2 │    │ Visitante N │    │ Camioneta │  │
│  │   (Thread) │    │   (Thread)  │    │   (Thread)  │    │  (Thread) │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └───────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Flujo Principal

1. **Llegada al inicio**: Visitante elige bicicleta (50%) o tren (50%)
2. **Bolso**: Obtiene bolso con llave → Camioneta lo transporta al destino
3. **Gomón**: Obtiene gomón individual o doble (prioridad a individual)
4. **Largada**: Espera a que haya G gomones listos
5. **Carrera**: Desciende por el río (tiempo aleatorio)
6. **Llegada**: Se registra posición (el primero = ganador)
7. **Premio**: Si posición 1, recibe CG fichas (ambos si gomón doble)
8. **Retiro**: Recupera bolso transportado por camioneta

## Sincronización

| Recurso          | Mecanismo         | Descripción                          |
|------------------|-------------------|--------------------------------------|
| Bicicletas       | Semaphore         | Limita acceso a N bicicletas        |
| Tren             | Semaphore         | Capacidad máxima de 15 personas     |
| Bolsos           | Semaphore         | Pool de bolsos disponibles          |
| Gomones          | 2 Semaphores      | Individual + Doble                   |
| Largada          | Lock + Condition  | Espera G gomones para iniciar       |
| Camioneta        | BlockingQueue     | Hilo propio para transporteasync    |
| Llegada carrera  | AtomicInteger     | Contador thread-safe de posiciones  |
