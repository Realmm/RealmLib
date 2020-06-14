package net.jamesandrew.realmlib.util.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.*;

public class Scoreboard {

    private static final int MAX_LINES = 16;

    private final org.bukkit.scoreboard.Scoreboard scoreboard;
    private final Objective objective;

    private final Map<Integer, LineExecution> executions = new HashMap<>(MAX_LINES);
    private final Map<Integer, LineExecution> originalExecutions = new HashMap<>(MAX_LINES);

    public Scoreboard(String title) {
        this(title, new ArrayList<>());
    }

    public Scoreboard(String title, LineExecution... lines) {
        this(title, Arrays.asList(lines));
    }

    public Scoreboard(String title, List<LineExecution> lines) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("dummy", title);
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        lines.forEach(this::addToExecution);
    }

    public void setTitle(String title) {
        objective.setDisplayName(title);
    }

    public void addLine(LineExecution execution) {
        if (executions.size() > MAX_LINES) throw new IndexOutOfBoundsException("You cannot add more than 16 lines.");
        addToExecution(execution);
    }

    public void addLine(String s) {
        addLine(p -> s);
    }

    public void setLine(int index, LineExecution execution) {
        executions.put(index, execution);
    }

    public void setLine(int index, String s) {
        setLine(index, p -> s);
    }

    public Scoreboard clone() {
        return new Scoreboard(objective.getDisplayName(), new ArrayList<>(executions.values()));
    }

    public org.bukkit.scoreboard.Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void update(Player p) {
        for (int x = executions.size() - 1; x >= 0; x--) {
            setLine(x, executions.get(x), p);
        }
        p.setScoreboard(getScoreboard());
    }

    public void addBlankSpace() {
        addLine(" ");
    }

    private void setLine(int index, LineExecution execution, Player p) {
        if (index < 0 || index >= MAX_LINES) throw new IndexOutOfBoundsException("The index cannot be negative or higher than 15.");
        String oldModified = originalExecutions.get(index).execute(p);
        scoreboard.resetScores(oldModified);
        String modified = getLineCoded(execution.execute(p), p);
        executions.put(index, pl -> modified);
        originalExecutions.put(index, pl -> modified);
        objective.getScore(modified).setScore(executions.size() - index - 1);
    }

    private String getLineCoded(String line, Player p) {
        StringBuilder sb = new StringBuilder(line);
        while (executions.values().stream().anyMatch(e -> e.execute(p).equalsIgnoreCase(sb.toString()))) {
            sb.append(ChatColor.RESET);
        }
        return sb.toString().substring(0, Math.min(40, sb.toString().length()));
    }

    private void addToExecution(LineExecution execution) {
        int next = executions.keySet().stream().reduce((i, ii) -> i > ii ? i : ii).orElse(-1) + 1;
        executions.put(next, execution);
        originalExecutions.put(next, execution);
    }

}
