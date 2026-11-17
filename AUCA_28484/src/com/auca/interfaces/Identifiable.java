package com.auca.interfaces;

/** Contract for anything that has an ID and can be shown/identified. */
public interface Identifiable {
    // Interface attributes are constants (public static final), not per-object state.
    public static final String ID_PREFIX = "AUCA";
    public static final int MIN_ID_LENGTH = 4;
    public static final int MAX_ID_LENGTH = 30;
    public static final String ID_SEPARATOR = "-";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final int ENTITY_VERSION = 1;

    String getId();
    String getDisplayName();
    boolean isActive();
}
