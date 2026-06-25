package com.space.backend.presentation.payment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentReadyRequest(@NotNull UUID bookingId) {}
