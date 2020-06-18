package net.jamesandrew.realmlib.util.placeholder;

import java.util.Arrays;

public final class Placeholder {

    private final String main;
    private ReplacePattern pattern;

    public Placeholder(String main) {
        this.main = main;
        this.pattern = new ReplacePattern();
    }

    public Placeholder(String s, ReplacePattern pattern) {
        this.main = s;
        this.pattern = pattern;
    }

    public Placeholder setReplacePattern(ReplacePattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public Placeholder setPlaceholders(boolean appendPercent, String... placeholders) {
        pattern.setPlaceholders(appendPercent, placeholders);
        return this;
    }

    public Placeholder setPlaceholders(String... placeholders) {
        setPlaceholders(true, placeholders);
        return this;
    }

    public Placeholder setToReplace(Object... objects) {
        pattern.setToReplace(Arrays.stream(objects).map(Object::toString).toArray(String[]::new));
        return this;
    }

    public ReplacePattern getPattern() {
        return pattern;
    }

    public String[] getPlaceholders() {
        return pattern.getPlaceholders();
    }

    public String[] getToReplace() {
        return pattern.getToReplace();
    }

    public String[] getOriginalPlaceholders() {
        return pattern.getOriginalPlaceholders();
    }

    @Override
    public String toString() {
        String s = main;
        if (getPlaceholders().length != getToReplace().length) throw new IllegalArgumentException("Array lengths cannot be different");

//        Logger.debug("main: " + main);
        for (int i = 0; i < getPlaceholders().length; i++) {
//            Logger.debug("placeholder: " + getPlaceholders()[i] + " toReplace: " + getToReplace()[i]);
            s = s.replace(getPlaceholders()[i], getToReplace()[i]);
        }
        return s;
    }

}
