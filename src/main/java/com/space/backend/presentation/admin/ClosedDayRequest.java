package com.space.backend.presentation.admin;

import com.space.backend.application.admin.AddClosedDayCommand;

import java.time.LocalDate;

public record ClosedDayRequest(LocalDate closedDate, String reason) {
    public AddClosedDayCommand toCommand() {
        return new AddClosedDayCommand(closedDate, reason);
    }
}
