package net.jamesandrew.realmlib.placeholder;

import java.util.Arrays;

public final class Placeholder {

    private final String main;
    private String[] placeholders;
    private String[] toReplace;

    public Placeholder(String main) {
        this.main = main;
    }

    public Placeholder setPlaceholders(String... placeholders) {
        this.placeholders = Arrays.stream(placeholders).map(s -> "%" + s + "%").toArray(String[]::new);
        return this;
    }

    public Placeholder setToReplace(Object... objects) {
        this.toReplace = Arrays.stream(objects).map(Object::toString).toArray(String[]::new);
        return this;
    }

    @Override
    public String toString() {
        String s = main;
        if (placeholders.length != toReplace.length) throw new IllegalArgumentException("Array lengths cannot be different");
        for (int i = 0; i < placeholders.length; i++) {
            s = s.replace(placeholders[i], toReplace[i]);
        }
        return s;
    }

}
