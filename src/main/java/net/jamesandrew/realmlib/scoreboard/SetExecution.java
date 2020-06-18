package net.jamesandrew.realmlib.scoreboard;

class SetExecution {

    private final int index;
    private final LineExecution execution;
    private final boolean append;

    SetExecution(int index, LineExecution execution, boolean append) {
        this.index = index;
        this.execution = execution;
        this.append = append;
    }

    int getIndex() {
        return index;
    }

    LineExecution getExecution() {
        return execution;
    }

    boolean shouldAppend() {
        return append;
    }

}
