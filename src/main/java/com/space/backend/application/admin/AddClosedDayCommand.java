package com.space.backend.application.admin;

import java.time.LocalDate;

public record AddClosedDayCommand(LocalDate closedDate, String reason) {}
