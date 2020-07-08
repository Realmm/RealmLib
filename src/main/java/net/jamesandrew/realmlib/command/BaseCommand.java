package net.jamesandrew.realmlib.command;

import net.jamesandrew.commons.container.Container;
import net.jamesandrew.commons.exception.Validator;
import net.jamesandrew.commons.logging.Logger;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.*;
import java.util.stream.Collectors;

public class BaseCommand extends BukkitCommand implements CommandNode {

    private final String node;
    private CommandExecution commandExecution;
    private Set<SubCommand> subCommands = new LinkedHashSet<>();
    private Set<String> perms = new HashSet<>();
    private List<CommandNode> chain = new LinkedList<>();

    private final Set<String> alias = new HashSet<>();

    public BaseCommand(String command, CommandExecution execution) {
        super(command);
        this.node = command;
        this.commandExecution = execution;
    }

    public BaseCommand(String command) {
        this(command, null);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        return CommandManager.get().onCommand(commandSender, this, s, args);
    }

    public void addAlias(String alias) {
        Validator.notContains(alias, new HashSet<>(this.alias));
        this.alias.add(alias);
        super.setAliases(new ArrayList<>(this.alias));
    }

    @Override
    public Command setAliases(List<String> list) {
        throw new UnsupportedOperationException("Use #addAlias or #addAliases instead");
    }

    public void addAliases(String... alias) {
        this.alias.forEach(a -> Validator.notContains(a, new HashSet<>(Arrays.asList(alias))));
        this.alias.addAll(Arrays.asList(alias));
    }

    public boolean hasAlias() {
        return alias.size() > 0;
    }

    public boolean isAlias(String s) {
        return alias.stream().anyMatch(a -> a.equalsIgnoreCase(s));
    }

    public String getAlias(String s) {
        return alias.stream().filter(a -> a.equalsIgnoreCase(s)).findFirst().orElseThrow(() -> new IllegalArgumentException("No alias"));
    }

    void callAppropriateCommand(CommandSender sender, String[] args) {
        if (getPermission() != null && !getPermission().equals("") && !sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return;
        }
        if (args.length == 0) return;
        if (!getNode().equalsIgnoreCase(args[0])) {
            if (!(hasAlias() && isAlias(args[0]))) return;
        }
        if (args.length <= 1) {
            if (!isExecutable()) return;
            addToParentChain(this);
            execute(sender, args);
            clearParentChain();
        } else {
            if (!hasChildren()) {
                if (!isExecutable()) return;
                addToParentChain(this);
                execute(sender, args);
                clearParentChain();
                return;
            }

            int arg = 1;
            String against = args[arg];

            Optional<SubCommand> node = getChildren().stream()
                    .filter(n -> {
                        if (n.hasPlaceHolderExecution()) n.runPlaceHolderExecution(sender, args);
                        String toCheck = n.hasPlaceHolder() ? ChatColor.stripColor(n.getPlaceHolder()) : n.hasAlias() && n.isAlias(against) ? n.getAlias(against) : n.getNode();
                        return toCheck.equalsIgnoreCase(against);
                    }).findFirst();

            node.ifPresent(n -> n.addToParentChain(this));

            while(node.isPresent()) {
                SubCommand sub = node.get();
                arg++;
                if (!sub.hasChildren()) break;

                if (arg >= args.length) break;

                String againstInside = args[arg];
                sub.getChildren().stream().filter(SubCommand::hasPlaceHolderExecution).forEach(c -> c.runPlaceHolderExecution(sender, args));
                Optional<SubCommand> optSub = sub.getChildren().stream()
                        .filter(n -> {
                            String toCheck = n.hasPlaceHolder() ? ChatColor.stripColor(n.getPlaceHolder()) : n.hasAlias() && n.isAlias(againstInside) ? n.getAlias(againstInside) : n.getNode();
                            return toCheck.equalsIgnoreCase(againstInside);
                        }).findFirst();
                if (optSub.isPresent()) {
                    optSub.get().addToParentChain(sub.getParentChain());
                    optSub.get().addToParentChain(sub);
                    sub.clearParentChain();
                    node = optSub;
                } else {
                    sub.addToParentChain(this);
                    break;
                }
            }

            String[] toSend = new String[arg];

            for (int i = 0; i < arg; i++) {
                toSend[i] = args[i];
            }

            clearParentChain();

            node.ifPresent(n -> runSubCommandExecution(n, sender, toSend));
        }
    }

    private void runSubCommandExecution(SubCommand cmd, CommandSender sender, String[] args) {
        if (cmd.hasPermissions()) {
            if (!cmd.getPermissions().stream().allMatch(sender::hasPermission) && !(sender instanceof ConsoleCommandSender)) {
                if (cmd.hasPermMessage()) Logger.log(cmd.getPermMessage());
                cmd.clearParentChain();
                return;
            }
        }

        if (cmd.hasPlaceHolderExecution()) cmd.runPlaceHolderExecution(sender, args);

        String against = args[args.length - 1];
        if (cmd.hasPlaceHolder()) {
            String replaced = ChatColor.stripColor(cmd.getNode().replace(cmd.getNode(), cmd.getPlaceHolder()));
            if (!replaced.equalsIgnoreCase(against)) {
                cmd.clearParentChain();
                return;
            }
        } else if (!(cmd.hasAlias() && cmd.isAlias(against) ? cmd.getAlias(against) : cmd.getNode()).equalsIgnoreCase(against)) {
            cmd.clearParentChain();
            return;
        }

        if (!cmd.isExecutable()) {
            cmd.clearParentChain();
            return;
        }
        cmd.execute(sender, args);
        cmd.clearParentChain();
    }

    public String getNode() {
        return node;
    }

    public void execute(CommandSender sender, String[] args) {
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

}
