package net.jamesandrew.realmlib.lang;

import net.jamesandrew.commons.logging.Logger;
import net.jamesandrew.realmlib.placeholder.ReplacePattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.*;

public class TextComponentBuilder {

    private final ComponentBuilder c;
    private final List<Text> textList = new ArrayList<>();
    private final Set<ReplacePattern> patterns = new HashSet<>();
    private ChatColor defaultColor;

    public TextComponentBuilder(Text text) {
        throw new UnsupportedOperationException("Not yet supported.");
//        this.c = new ComponentBuilder(text.getOriginal());
//        textList.add(text);
    }

    public TextComponentBuilder() {
        throw new UnsupportedOperationException("Not yet supported.");
//        this.c = new ComponentBuilder("");
    }

    public TextComponentBuilder append(Text text) {
        textList.add(text);
        Logger.debug("Adding: " + text.getReplaced() + " Color: " + text.getColor());
        return this;
    }

    public TextComponentBuilder setDefaultColor(ChatColor color) {
        this.defaultColor = color;
        return this;
    }

    public ChatColor getDefaultColor() {
        return defaultColor == null ? ChatColor.RESET : defaultColor;
    }

    public TextComponentBuilder addPlaceholder(boolean toAdd, ReplacePattern pattern) {
        if (toAdd) patterns.add(pattern);
        return this;
    }

    public BaseComponent[] build() {
        Logger.debug("\nIN TEXT LIST\n");
        textList.forEach(t -> Logger.debug(t.getReplaced()));
        Logger.debug("\nEND\n");

        //loop through, replace current texts with any placeholders, create a new reduced list
        /*
        Loop through text list
        If any in main text list has a placeholder, replace it with first text that has appropriate ReplacePattern
        First, replace all texts with what they have as placeholders (done with Text#getReplaced)
        Second, replace all texts with what the builder has as placeholders. If the builder has a placeholder
        that is a text, remove that from list
         */

        /*
        - List of all texts, each with placeholders, and the builder also has global placeholders
        - Replace all texts with their placeholders (text#getReplaced)
        - Check if a text has a global placeholder in it, after already being text#getReplaced
        - If it does, replace the text in that text object with a placeholder
         */
//        Logger.debug("Original size: " + textList.size());

        textList.forEach(t -> {
            Logger.debug("Start");
            final String[] replaced = {t.getReplaced()}; //apply replacing in text before applying it to main
            Logger.debug("replaced: " + replaced[0]);
            boolean isReplacedText = false;

            //Looping through global placeholders
            for (ReplacePattern global : patterns) {
                //Checking the text object has a global placeholder
                String[] pl = Arrays.stream(global.getPlaceholders()).filter(s -> replaced[0].contains(s)).toArray(String[]::new);
                if (pl.length <= 0) continue;
                //Calculate what should replace the text objects text, and replace it
                Object[] replace = Arrays.stream(pl).map(s -> global.hasMatchingObject(s) ? global.getMatchingObject(s) : s).toArray();
//                Object[] replace = Arrays.stream(pl).map(s -> {
//                    if (!global.hasMatchingObject(s)) return s;
//                    Object o = global.getMatchingObject(s);
//                    if (!(o instanceof Text)) return;
//                    Text text = (Text) o;
//                    text.
//
//                }).toArray();

                Logger.debug("Start1");
                Arrays.stream(pl).forEach(s -> Logger.debug("pl: " + s));
                Arrays.stream(replace).forEach(s -> Logger.debug("replace: " + s));
                Logger.debug("End1");

                ReplacePattern pattern = new ReplacePattern().setPlaceholders(false, pl).setToReplace(replace);
                t.setPlaceholder(pattern, replaced[0]);
            }
        });

//        List<Text> textReplaced = textList.stream().filter(t -> {
//            Logger.debug("Start");
//            final String[] replaced = {t.getReplaced()}; //apply replacing in text before applying it to main
//            Logger.debug("replaced: " + replaced[0]);
//            boolean isReplacedText = false;
//
//            //Looping through global placeholders
//            for (ReplacePattern global : patterns) {
//                //Checking the text object has a global placeholder
//                String[] pl = Arrays.stream(global.getPlaceholders()).filter(s -> replaced[0].contains(s)).toArray(String[]::new);
//                if (pl.length <= 0) continue;
//                //Calculate what should replace the text objects text, and replace it
//                Object[] replace = Arrays.stream(pl).map(s -> global.hasMatchingObject(s) ? global.getMatchingObject(s) : s).toArray();
//                t.setPlaceholder(new ReplacePattern().setPlaceholders(pl).setToReplace(replace), replaced[0]);
//            }
////            for (ReplacePattern p : patterns) {
////                Arrays.stream(p.getObjectsToReplace()).forEach(o -> {
////                    if (o instanceof Text) {
////                        Logger.debug("is text");
////                        Text text = (Text) o;
////                        Object[] toReplace = p.getToReplace();
////                        for (int i = 0; i < toReplace.length; i++) {
////                            toReplace[i] = text.getReplaced();
////                        }
////                        ReplacePattern pattern = new ReplacePattern().setPlaceholders(p.getOriginalPlaceholders()).setToReplace(toReplace);
////                        replaced[0] = new Placeholder(replaced[0], pattern).toString();
////                        return;
////                    }
////                    replaced[0] = new Placeholder(replaced[0], p).toString();
////                });
////
////            }
//
//            Logger.debug("finalReplaced: " + replaced[0]);
//            //replaced is replaced with all appropriate strings, now check if one of those strings was a text and remove it
//            //should return true if it was replaced, or if getPlaceholders has no placeholders
//            if (patterns.stream().map(ReplacePattern::getPlaceholders).anyMatch(a -> {
//                boolean match = Arrays.stream(a).anyMatch(s -> t.getReplaced().contains(s));
//                boolean hasPatterns = patterns.size() != 0;
//                boolean hasPlaceholder = t.hasPlaceholder();
//                return match || !hasPatterns || !hasPlaceholder;
//            })) isReplacedText = true;
////            if (patterns.size() == 0 || !t.hasPlaceholder()) isReplacedText = true;
//            Logger.debug("End: " + isReplacedText + "\n");
//            if (isReplacedText) t.setReplaced(replaced[0]);
//            return isReplacedText;
//        }).collect(Collectors.toList());

//        Logger.debug("modified size: " + textReplaced.size());

        Logger.debug("OUTPUT: ");
        textList.forEach(t -> {
            Logger.debug(t.getReplaced());
        });
        Logger.debug("END OUTPUT");

        textList.forEach(t -> {
//            c.color(getDefaultColor());
//            c.event((ClickEvent) null);
//            c.event((HoverEvent) null);

//            String replaced = t.getReplaced(); //apply replacing in text before applying it to main
//            for (ReplacePattern p : patterns) {
//                replaced = new Placeholder(replaced, p).toString();
//            }

            c.append(t.getReplaced());
            if (!Lang.isColored(t.getReplaced())) c.color(t.hasColor() ? t.getColor() : getDefaultColor());
            c.event(t.getClickEvent());
            c.event(t.getHoverEvent());
        });
        return c.create();
    }



}
