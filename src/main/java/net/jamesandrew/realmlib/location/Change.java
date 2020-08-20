package net.jamesandrew.realmlib.location;

@FunctionalInterface
public interface Change {

    void change(Changer loader);

}
