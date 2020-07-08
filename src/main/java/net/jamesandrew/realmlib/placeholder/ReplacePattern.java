package net.jamesandrew.realmlib.placeholder;

import net.jamesandrew.commons.container.Container;

import java.util.Arrays;

public class ReplacePattern {

    private String[] placeholders;
    private String[] originalPlaceholders;
    private Object[] toReplace;

    public ReplacePattern setPlaceholders(String... placeholders) {
        if (Container.hasDuplicate(Arrays.asList(placeholders))) throw new IllegalArgumentException("Duplicate placeholders");
        this.originalPlaceholders = placeholders;
        this.placeholders = Arrays.stream(placeholders).map(s -> "%" + s + "%").toArray(String[]::new);
        return this;
    }

    public ReplacePattern setPlaceholders(boolean appendPercent, String... placeholders) {
        if (appendPercent) {
            setPlaceholders(placeholders);
        } else this.placeholders = placeholders;
        return this;
    }

    public ReplacePattern setToReplace(Object... objects) {
        this.toReplace = objects;
        return this;
    }

    public String[] getOriginalPlaceholders() {
        return originalPlaceholders;
    }

    public String[] getPlaceholders() {
        return placeholders;
    }

    public String[] getToReplace() {
        return Arrays.stream(toReplace).map(o -> o == null ? "null" : o.toString()).toArray(String[]::new);
    }

    public Object[] getObjectsToReplace() {
        return toReplace;
    }

    public boolean hasMatchingObject(String placeholder) {
        return getMatchingObject(placeholder) != null;
    }

    public Object getMatchingObject(String placeholder) {
        int count = 0;
        for (String s : placeholders) {
            if (s.equals(placeholder)) break;
            count++;
        }
        return count >= placeholder.length() ? null : toReplace[count];
    }

}
