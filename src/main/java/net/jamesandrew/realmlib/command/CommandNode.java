package net.jamesandrew.realmlib.command;

import net.jamesandrew.commons.container.Container;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandNode implements CommandExecutionable {

    private final String node;
    private String permMessage;
    private CommandExecution commandExecution;
    private Set<SubCommand> subCommands = new LinkedHashSet<>();
    private Set<String> perms = new HashSet<>();
    private List<CommandNode> chain = new LinkedList<>();

    public CommandNode(String node, CommandExecution commandExecution) {
        this.node = node;
        this.commandExecution = commandExecution;
    }

    public String getNode() {
        return node;
    }

    void execute(CommandSender sender, String[] args) {
        commandExecution.onCommand(sender, args);
    }

    @Override
    public void setExecution(CommandExecution commandExecution) {
        this.commandExecution = commandExecution;
    }

    @Override
    public CommandExecution getExecution() {
        return commandExecution;
    }

    @Override
    public boolean isExecutable() {
        return commandExecution != null;
    }

    public boolean isChild(String s) {
        return subCommands.stream().map(SubCommand::getNode).anyMatch(n -> n.equalsIgnoreCase(s));
    }

    public SubCommand getChild(String s) {
        return subCommands.stream().filter(n -> n.getNode().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    public List<CommandNode> getParentChain() {
        return Collections.unmodifiableList(chain);
    }

    public CommandNode getParent(int index) {
        return chain.get(index);
    }

    public CommandNode getParent(String node) {
        return chain.stream().filter(n -> n.getNode().equalsIgnoreCase(node)).findFirst().orElseThrow(NullPointerException::new);
    }

    public boolean isParent(int index) {
        return getParent(index) != null;
    }

    public boolean isParent(String node) {
        return getParent(node) != null;
    }

    protected void addToParentChain(CommandNode node) {
        chain.add(node);
    }

    protected void addToParentChain(List<CommandNode> nodes) {
        nodes.forEach(this::addToParentChain);
    }

    protected void clearParentChain() {
        chain.clear();
    }

    public Set<SubCommand> getChildren() {
        return Collections.unmodifiableSet(subCommands);
    }

    public void addSubCommands(SubCommand... subCommands) {
        Optional<SubCommand> contained = Arrays.stream(subCommands).filter(s -> this.subCommands.stream().anyMatch(c -> c.getNode().equalsIgnoreCase(s.getNode()))).findFirst();
        List<String> nodes = Arrays.stream(subCommands).map(SubCommand::getNode).collect(Collectors.toList());
        boolean inSet = Container.hasDuplicate(nodes);
        boolean alreadySet = contained.isPresent() || inSet;

        StringBuilder sb = new StringBuilder();
        if (inSet) {
            Map<String, Integer> dupes = Container.getDuplicatesCounted(nodes);
            dupes.keySet().forEach(s -> sb.append("\'").append(s).append("\'").append(" (").append(dupes.get(s)).append(" times) "));
        }

        Validate.isTrue(!alreadySet, contained.isPresent() ? "Node \'" + contained.get().getNode() + "\' already registered" :
                inSet ? "Node/s already registered: " + sb.toString().trim() : "Node already registered");
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    public boolean hasChildren() {
        return subCommands.size() > 0;
    }

    public int getAmountOfChildren() {
        return getChildren().size();
    }

    public void addPermission(String s) {
        perms.add(s);
    }

    public void setPermissionMessage(String message) {
        this.permMessage = message;
    }

    protected boolean hasPermMessage() {
        return this.permMessage != null;
    }

    protected String getPermMessage() {
        return permMessage;
    }

    public void removePermission(String s) {
        perms.remove(s);
        if (hasPermMessage()) this.permMessage = null;
    }

    public boolean hasPermission(String s) {
        return perms.contains(s);
    }

    public boolean hasPermissions() {
        return !perms.isEmpty();
    }

    public Collection<? extends String> getPermissions() {
        return Collections.unmodifiableCollection(perms);
    }

}
