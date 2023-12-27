package com.anirln.redis.data;

import java.util.Objects;

/**
 * value might be a string, list, map, integer...
 */
public class DatabaseValue {
    private final DataType dataType;
    private final Object value;

    public DatabaseValue(DataType dataType, Object value) {
        this.dataType = dataType;
        this.value = value;
    }

    public String getString() {
        assert dataType == DataType.STRING;
        return getValue();
    }

    public Integer getInteger() {
        assert dataType == DataType.INTEGER;
        return getValue();
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue() {
        return (T) value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseValue that = (DatabaseValue) o;
        return dataType == that.dataType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, value);
    }
}

