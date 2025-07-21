package ru.practicum.shareit.gateway.booking.dto;

import java.util.Optional;

public enum ApproveState {

    TRUE,
    FALSE;

    public static Optional<ApproveState> from(String approveStateString) {
        for (ApproveState approveState : values()) {
            if (approveState.name().equalsIgnoreCase(approveStateString))
                return Optional.of(approveState);
        }
        return Optional.empty();
    }

}
