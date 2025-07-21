package ru.practicum.shareit.server.booking;

public enum State {
    // Параметр `state` необязательный и по умолчанию равен **`ALL`** (англ. «все»).
    // Также он может принимать значения **`CURRENT`** (англ. «текущие»),
    // **`PAST`** (англ. «завершённые»),
    // **`FUTURE`** (англ. «будущие»),
    // **`WAITING`** (англ. «ожидающие подтверждения»),
    // **`REJECTED`** (англ. «отклонённые»).
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
