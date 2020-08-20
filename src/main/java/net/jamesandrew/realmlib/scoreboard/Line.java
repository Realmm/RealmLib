package net.jamesandrew.realmlib.scoreboard;

public class Line {

    private RealmScoreboard scoreboard;
    private int index;
    private LineExecution execution;
    private boolean append;

    public Line(int index, LineExecution execution, boolean append) {
        this.index = index;
        this.execution = execution;
        this.append = append;
    }

    public Line(int index, String s, boolean append) {
        this(index, p -> s, append);
    }

    public Line(int index, String s) {
        this(index, p -> s, true);
    }

    public Line(int index, LineExecution execution) {
        this(index, execution, true);
    }

    public void setScoreboard(RealmScoreboard scoreboard) {
        if (scoreboard != null) {
            scoreboard.removeLine(index);
        }
        this.scoreboard = scoreboard;
        scoreboard.setLine(index, execution, append);
    }

    public boolean hasScoreboard() {
        return scoreboard != null;
    }

    public RealmScoreboard getScoreboard() {
        return scoreboard;
    }

    public int getIndex() {
        return index;
    }

    public LineExecution getExecution() {
        return execution;
    }

    public boolean isAppend() {
        return append;
    }
}
