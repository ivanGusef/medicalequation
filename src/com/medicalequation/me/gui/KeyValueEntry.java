package com.medicalequation.me.gui;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/18/13
 * Time: 11:50 PM
 * May the force be with you always.
 */
public class KeyValueEntry {
    private String key;
    private String value;

    public KeyValueEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyValueEntry that = (KeyValueEntry) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
