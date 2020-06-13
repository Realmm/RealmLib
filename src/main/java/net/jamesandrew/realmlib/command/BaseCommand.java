package net.jamesandrew.realmlib.command;

import net.jamesandrew.commons.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.*;

public class BaseCommand extends CommandNode {

    private final CommandManager manager = new CommandManager();
    private final Set<String> alias = new HashSet<>();

    public BaseCommand(String command, CommandExecution execution) {
        super(command, execution);
    }

    public BaseCommand(String command) {
        super(command, null);
    }

    public void addAlias(String alias) {
        this.alias.add(alias);
    }

    public void addAliases(String... alias) {
        this.alias.addAll(Arrays.asList(alias));
    }

    public Collection<String> getAliases() {
        return Collections.unmodifiableCollection(alias);
    }

    public boolean hasAlias() {
        return alias.size() > 0;
    }

    public boolean isAlias(String s) {
        return alias.stream().anyMatch(a -> a.equalsIgnoreCase(s));
    }

    void callAppropriateCommand(CommandSender sender, String[] args) {
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
                        String toCheck = n.hasPlaceHolder() ? ChatColor.stripColor(n.getPlaceHolder()) : n.getNode();
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
                            String toCheck = n.hasPlaceHolder() ? ChatColor.stripColor(n.getPlaceHolder()) : n.getNode();
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

        if (cmd.hasPlaceHolder()) {
            String replaced = ChatColor.stripColor(cmd.getNode().replace(cmd.getNode(), cmd.getPlaceHolder()));
            if (!replaced.equalsIgnoreCase(args[args.length - 1])) {
                cmd.clearParentChain();
                return;
            }
        } else if (!cmd.getNode().equalsIgnoreCase(args[args.length - 1])) {
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

}
