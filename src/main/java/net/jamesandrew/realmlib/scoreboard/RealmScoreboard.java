package net.jamesandrew.realmlib.scoreboard;

import net.jamesandrew.realmlib.lang.Lang;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * A wrapper for {@link Bukkit}'s {@link org.bukkit.scoreboard.Scoreboard} class
 * Allows for easier manipulation of the sidebar {@link org.bukkit.scoreboard.Scoreboard}
 */
public class RealmScoreboard {

    private final int maxLines = 15;
    private boolean toUpdate = false;

    private final org.bukkit.scoreboard.Scoreboard scoreboard;
    private Objective objective;

    private LineExecution title;

    private final Map<Integer, LineExecution> executions = new HashMap<>();
    private final Map<Integer, LineExecution> oldExecutions = new HashMap<>();
    private final Set<SetExecution> setExecutions = new HashSet<>();
    private final Set<ScoreboardTeam> teams = new HashSet<>();

    /**
     * Create a scoreboard with a certain title
     * @param title The title to set
     */
    public RealmScoreboard(String title) {
        this(title, new ArrayList<>());
    }

    /**
     * Create a scoreboard with a certain {@link LineExecution} title
     * @param p The player to execute the {@link LineExecution}
     * @param title The {@link LineExecution} title to set
     */
    public RealmScoreboard(Player p, LineExecution title) {
        this(title.execute(p));
        this.title = title;
    }

    /**
     * Create a scoreboard with a certain title and {@link LineExecution}'s
     * @param title The title to set
     * @param lines The {@link LineExecution}'s to set
     */
    public RealmScoreboard(String title, LineExecution... lines) {
        this(title, Arrays.asList(lines));
    }

    /**
     * Create a scoreboard with a certain title and a list of {@link LineExecution}'s
     * @param title The title to set
     * @param lines The {@link LineExecution}'s to set
     */
    public RealmScoreboard(String title, List<LineExecution> lines) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("obj", "dummy");
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < 16; i++) {
            Team t = scoreboard.registerNewTeam(String.valueOf(i));
            String entry = String.valueOf(ChatColor.values()[i]);
            teams.add(new ScoreboardTeam(i, t, entry));
        }

        lines.forEach(this::addToExecution);
    }

    /**
     * Sets the title of the scoreboard
     * @param title The title to set
     */
    public void setTitle(String title) {
        setTitle(p -> title);
    }

    /**
     * Sets the title of the scoreboard
     * @param title The {@link LineExecution} to set
     */
    public void setTitle(LineExecution title) {
        this.title = title;
    }

    /**
     * Add a line to the scoreboard
     * Maximum amount of lines a scoreboard can have is 15
     * @param s The content of the line to add
     */
    public void addLine(String s) {
        addLine(p -> s);
    }

    /**
     * Add a {@link LineExecution} line to the scoreboard
     * Maximum amount of lines a scoreboard can have is 15
     * @param execution The {@link LineExecution} to add
     */
    public void addLine(LineExecution execution) {
        if (getFinalSet().size() >= maxLines) throw new IndexOutOfBoundsException("You cannot add more than 15 lines.");
        addToExecution(execution);
    }

    /**
     * Adds a blank line to the scoreboard
     */
    public void addBlankLine() {
        StringBuilder sb = new StringBuilder();
        LineExecution execution = p -> "";
        while(getFinalSet().containsValue(execution)) {
            sb.append(" ");
            execution = p -> sb.toString();
        }
        addLine(execution);
    }

    /**
     * Sets a specific line to be blank
     * @param index The index of the line to modify
     */
    public void setBlankLine(int index) {
        StringBuilder sb = new StringBuilder();
        LineExecution execution = p -> "";
        if (setExecutions.stream().anyMatch(s -> {
            String st = null;

            try {
                st = s.getExecution().execute(null);
            } catch (NullPointerException ignored) {}

            if (st == null) return false;
            boolean isBlankLine = StringUtils.isBlank(st);
            return s.getIndex() == index && isBlankLine;
        })) return;

        while(getFinalSet().containsValue(execution)) {
            sb.append(" ");
            execution = p -> sb.toString();
        }
        setLine(index, execution);
    }

    /**
     * Removes specific lines from a scoreboard
     * @param index The lines index to remove
     */
    public void removeLines(int... index) {
        Arrays.stream(index).forEach(this::removeLine);
    }

    /**
     * Removes a specific line on a scoreboard
     * @param index The lines index to remove
     */
    public void removeLine(int index) {
        if (setExecutions.removeIf(e -> e.getIndex() == index)) {
            toUpdate = true;
        }
    }

    /**
     * Set a specific line on a scoreboard
     * Appends to bottom of scoreboard if the index is higher than the amount of lines
     * If, after using this method, you add more lines, this line will retain its index
     * Maximum amount of lines a scoreboard can have is 15
     * @param index The line number that you want to set
     * @param execution The {@link LineExecution} you want to set the line to
     * @param append Whether it should append to the bottom of scoreboard, if it can
     */
    public void setLine(int index, LineExecution execution, boolean append) {
        if (index > maxLines || index <= 0) throw new IndexOutOfBoundsException("Line index out of bounds (1-15), line attempted to be set at '" + index + "'");
        if (getFinalSet().size() >= maxLines) throw new IndexOutOfBoundsException("You cannot add more than 15 lines.");
        setExecutions.removeIf(e -> e.getIndex() == index);
        setExecutions.add(new SetExecution(index, execution, append));
    }

    /**
     * Set a specific line on a scoreboard
     * Appends to bottom of scoreboard if the index is higher than the amount of lines
     * If, after using this method, you add more lines, this line will retain its index
     * Maximum amount of lines a scoreboard can have is 15
     * @param index The line number that you want to set
     * @param s The content of the line that you want to set
     * @param append Whether it should append to the bottom of scoreboard, if it can
     */
    public void setLine(int index, String s, boolean append) {
        setLine(index, p -> s, append);
    }

    /**
     * Set a specific line on a scoreboard
     * Appends to bottom of scoreboard if the index is higher than the amount of lines
     * If, after using this method, you add more lines, this line will retain its index
     * Maximum amount of lines a scoreboard can have is 15
     * @param index The line number that you want to set
     * @param s The content of the line that you want to set
     */
    public void setLine(int index, String s) {
        setLine(index, p -> s, true);
    }

    /**
     * Set a specific line on a scoreboard
     * Appends to bottom of scoreboard if the index is higher than the amount of lines
     * If, after using this method, you add more lines, this line will retain its index
     * Maximum amount of lines a scoreboard can have is 15
     * @param index The line number that you want to set
     * @param execution The {@link LineExecution} you want to set the line to
     */
    public void setLine(int index, LineExecution execution) {
        setLine(index, execution, true);
    }

    /**
     * Removes the scoreboard from the {@link Player}
     * @param p The {@link Player} to remove the scoreboard from
     */
    public void remove(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    /**
     * Updates a players scoreboard with the updated values
     * This also applies the scoreboard to the player if they don't have one
     * This should be called after modifying the scoreboard in any way
     * @param p The {@link Player} to update
     */
    public void update(Player p) {
        if (executions.size() == 0 && setExecutions.size() == 0) {
            if (p.getScoreboard() != null) remove(p);
            toUpdate = false;
            return;
        }

        if (toUpdate) {
            String name = objective.getName().equals("obj") ? "obj2" : "obj";
            String criteria = objective.getCriteria().equals("dummy") ? "dummy2" : "dummy";
            Objective o = scoreboard.registerNewObjective(name, criteria);
            if (title == null) {
                o.setDisplayName(objective.getDisplayName());
            } else o.setDisplayName(Lang.color(title.execute(p)));
            o.setDisplaySlot(objective.getDisplaySlot());

            objective.unregister();
            objective = o;
            oldExecutions.clear();
        }

        //Updates players title if it can
        if (title != null) objective.setDisplayName(Lang.color(title.execute(p)));

        //Get the final sorted set and loop through it
        getFinalSet().forEach((i, e) -> {
            if (!oldExecutions.isEmpty() && oldExecutions.containsKey(i) && oldExecutions.get(i).execute(p).equals(e.execute(p)) && !toUpdate) return;
            //Find the team for this index (cached on instantiation)
            ScoreboardTeam sbTeam = teams.stream().filter(s -> s.getIndex() == i).findFirst().orElseThrow(() -> new IllegalArgumentException("No team with index " + i));
            Team team = sbTeam.getTeam();
            String entry = sbTeam.getEntry();
            team.addEntry(entry);

            String toAdd = e.execute(p);

            //Splits the line in half, sets the first half as prefix, second half as suffix, of the appropriate team
            if (toAdd.length() <= 16) {
                team.setPrefix(Lang.color(toAdd));
                team.setSuffix("");
            } else {
                if (toAdd.length() > 30) {
                    toAdd = StringUtils.substring(toAdd, 0, 30);
                }

                String sub = StringUtils.substring(toAdd, 0, 16);
                char splitCode = sub.charAt(sub.length() - 1);
                boolean codeSplit = splitCode == ChatColor.COLOR_CHAR;
                String prefix = StringUtils.substring(toAdd, 0, codeSplit ? 15 : 16);
                String lastColor = ChatColor.getLastColors(Lang.color(prefix)).equals("") ? ChatColor.RESET.toString() : ChatColor.getLastColors(Lang.color(prefix));
                String suffix = StringUtils.substring(toAdd, codeSplit ? 15 : 16, codeSplit ? 31 : 30);

                team.setPrefix(Lang.color(prefix));
                team.setSuffix(Lang.color(codeSplit ? suffix : lastColor + suffix));
            }

            //Updates the objective with the appropriate score and entry
            objective.getScore(entry).setScore(i);

            oldExecutions.put(i, e);
        });

        //Sets the players scoreboard to this scoreboard
        p.setScoreboard(scoreboard);
        toUpdate = false;
    }

    /**
     * Creates an exact copy of this {@link RealmScoreboard}
     * @return An exact copy of this {@link RealmScoreboard}
     */
    public RealmScoreboard clone() {
        RealmScoreboard scoreboard = new RealmScoreboard(objective.getDisplayName(), new ArrayList<>(executions.values()));
        scoreboard.setExecutions.addAll(setExecutions);
        return scoreboard;
    }

    private void addToExecution(LineExecution execution) {
        int next = executions.keySet().stream().reduce((i, ii) -> i > ii ? i : ii).orElse(-1) + 1;
        executions.put(next, execution);
    }

    private Map<Integer, LineExecution> getFinalSet() {
        //Get highest key value in executions map
        int highest = executions.keySet().stream().reduce((i, ii) -> i > ii ? i : ii).orElse(0);

        Map<Integer, LineExecution> reversed = new HashMap<>();
        Map<Integer, LineExecution> toSet = new HashMap<>();
        TreeMap<Integer, LineExecution> toAppend = new TreeMap<>(); //for easy sorting

        //Loops through all 'set' lines, if it can and should append to the bottom
        // of the scoreboard, it does, if it shouldn't it simply sets the line
        setExecutions.forEach(e -> {
            boolean canAppend = e.getIndex() >= highest + 1;
            if (canAppend) {
                if (e.shouldAppend()) toAppend.put(e.getIndex(), e.getExecution());
            } else {
                toSet.put(e.getIndex() - 1, e.getExecution());
            }
        });

        Map<Integer, LineExecution> pushed = new HashMap<>();

        //Loops through all 'added' lines, if it should be replaced by a 'set' line
        // then it replaces it, otherwise it inserts the 'added' line
        executions.forEach((i, e) -> pushed.put(i, toSet.getOrDefault(i, e)));

        //Reverses the pushed map insertion as scoreboards go from highest(top) -> lowest(bottom), not lowest(top) -> highest(bottom)
        if (pushed.size() > 0) {
            for (int i = highest; i >= 0; i--) {
                reversed.put(highest - i, pushed.get(i));
            }
        }

        Map<Integer, LineExecution> finalToSet = new HashMap<>();

        //If there are any 'set' lines to append to the bottom of the scoreboard, this
        // increases all lines already to go onto the scoreboard by how many it should
        // append by, to make room for the appending lines
        reversed.forEach((i, e) -> finalToSet.put(i + toAppend.size(), e));

        //Loops through the 'set' lines that should append to bottom of scoreboard
        for (int x = 0; x < toAppend.size(); x++) {
            //Puts the map in descending order, ordered by values, and gets the 'x'th value
            Optional<LineExecution> o = toAppend.descendingMap().values().stream().skip(x).findFirst();

            int next = 0;

            //Finds the next available slot in the final 'finalToSet' map
            while (finalToSet.containsKey(next)) {
                next++;
            }

            int finalNext = next;
            //Inserts the appended line to the first available slot
            o.ifPresent(l -> finalToSet.put(finalNext, l));
        }

        return finalToSet;
    }

}
