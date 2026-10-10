package com.rental.exception;

// small helpers so the services stay short and readable
public final class Check {

    private Check() {
    }

    public static void that(boolean ok, int status, String message) {
        if (!ok) {
            throw new ApiException(status, message);
        }
    }

    public static void notBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ApiException(400, message);
        }
    }
}
