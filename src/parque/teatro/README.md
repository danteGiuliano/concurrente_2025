┌─────────────┐        ┌──────────────┐        ┌─────────────┐
│ Visitante 1 │───────▶│  ASISTENTE 1│───────▶│   TEATRO    │
│ Visitante 2 │        │              │        │  (Lock +    │
│ Visitante 3 │        │  formarGrupo │        │ Conditions) │
│ Visitante 4 │        │              │        │             │
│ Visitante 5 │───────▶│  ✓ Grupo #1 │───────▶│ gruposFormados++│
└─────────────┘        └──────────────┘        └─────────────┘
                              │
                              │ signalAll()
                              ▼
                    ┌─────────────────────┐
                    │ Visitantes ingresan │
                    │   al teatro         │
                    └─────────────────────┘