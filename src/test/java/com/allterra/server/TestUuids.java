package com.allterra.server;

import java.nio.charset.StandardCharsets;

/**
 * Deterministic java.util.UUID factory for tests.
 */
public final class TestUuids {
    private TestUuids() {
    }

    public static java.util.UUID id(final long value) {
        return java.util.UUID.nameUUIDFromBytes(("id-" + value).getBytes(StandardCharsets.UTF_8));
    }
}
