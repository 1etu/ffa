package net.hywave.commands;

import net.hywave.BaseClass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;

public class BaseCommand {
    private final JavaPlugin plugin;
    private final Map<String, CommandData> commands = new HashMap<>();

    public BaseCommand(BaseClass plugin) {
        this.plugin = plugin;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface CommandInfo {
        String name();
        String description() default "";
        String permission() default "";
        String[] aliases() default {};
        boolean requiresPlayer() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface SubCommand {
        String name();
        String description() default "";
        String usage() default "";
        String permission() default "";
        String[] aliases() default {};
        int minArgs() default 0;
        int maxArgs() default -1;
        boolean isDefault() default false;
        boolean useCustomTabComplete() default false;
    }

    private class CommandData implements CommandExecutor, TabCompleter {
        private final Object instance;
        private final CommandInfo commandInfo;
        private final Map<String, Method> subCommands = new HashMap<>();
        private final Map<String, SubCommand> subCommandAnnotations = new HashMap<>();
        private Method defaultMethod = null;
        private SubCommand defaultSubCommand = null;

        public CommandData(Object instance, CommandInfo commandInfo) {
            this.instance = instance;
            this.commandInfo = commandInfo;
        }

        public void addSubCommand(String name, Method method, SubCommand subCommand) {
            if (subCommand.isDefault()) {
                defaultMethod = method;
                defaultSubCommand = subCommand;
            }
            subCommands.put(name.toLowerCase(), method);
            subCommandAnnotations.put(name.toLowerCase(), subCommand);
            for (String alias : subCommand.aliases()) {
                subCommands.put(alias.toLowerCase(), method);
                subCommandAnnotations.put(alias.toLowerCase(), subCommand);
            }
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!commandInfo.permission().isEmpty() && !sender.hasPermission(commandInfo.permission())) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (commandInfo.requiresPlayer() && !(sender instanceof org.bukkit.entity.Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }

            try {
                if (args.length == 0 || (defaultMethod != null && !subCommands.containsKey(args[0].toLowerCase()))) {
                    if (defaultMethod != null) {
                        defaultMethod.setAccessible(true);
                        defaultMethod.invoke(instance, sender, args);
                        return true;
                    }
                }

                String subCommandName = args[0].toLowerCase();
                Method subCommandMethod = subCommands.get(subCommandName);
                SubCommand subCommand = subCommandAnnotations.get(subCommandName);

                if (subCommandMethod == null || subCommand == null) {
                    if (defaultMethod != null) {
                        defaultMethod.setAccessible(true);
                        defaultMethod.invoke(instance, sender, args);
                        return true;
                    }
                    return false;
                }

                if (!subCommand.permission().isEmpty() && !sender.hasPermission(subCommand.permission())) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this sub-command!");
                    return true;
                }

                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                if (subArgs.length < subCommand.minArgs() || (subCommand.maxArgs() != -1 && subArgs.length > subCommand.maxArgs())) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + command.getName() + " " + subCommandName + " " + subCommand.usage());
                    return true;
                }

                subCommandMethod.setAccessible(true);
                subCommandMethod.invoke(instance, sender, args);
                return true;

            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An error occurred while executing the command!");
                e.printStackTrace();
                return true;
            }
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> completions = new ArrayList<>();

            Method currentMethod = null;
            SubCommand currentSubCommand = null;

            if (args.length > 0) {
                currentMethod = subCommands.get(args[0].toLowerCase());
                currentSubCommand = subCommandAnnotations.get(args[0].toLowerCase());
            }

            if (args.length <= 1 && (currentMethod == null || currentSubCommand == null)) {
                if (defaultMethod != null && defaultSubCommand != null && defaultSubCommand.useCustomTabComplete()) {
                    try {
                        String tabMethodName = defaultMethod.getName() + "TabComplete";
                        Method tabMethod = instance.getClass().getDeclaredMethod(tabMethodName, CommandSender.class, String[].class);
                        tabMethod.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        List<String> customCompletions = (List<String>) tabMethod.invoke(instance, sender, args);
                        if (customCompletions != null) {
                            return customCompletions;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            if (args.length == 1) {
                String partialCommand = args[0].toLowerCase();
                for (String subCmd : subCommands.keySet()) {
                    SubCommand subCommand = subCommandAnnotations.get(subCmd);
                    if (subCmd.startsWith(partialCommand) &&
                            (subCommand.permission().isEmpty() || sender.hasPermission(subCommand.permission()))) {
                        completions.add(subCmd);
                    }
                }
            }

            return completions;
        }
    }

    public void registerCommands(Object commandInstance) {
        long startTime = System.currentTimeMillis();
        Class<?> clazz = commandInstance.getClass();
        CommandInfo commandInfo = clazz.getAnnotation(CommandInfo.class);

        if (commandInfo == null) {
            throw new IllegalArgumentException("Command class must have CommandInfo annotation");
        }

        CommandData commandData = new CommandData(commandInstance, commandInfo);

        int subCommandCount = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand != null) {
                if (method.getParameterCount() != 2 ||
                        !CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]) ||
                        !String[].class.equals(method.getParameterTypes()[1])) {
                    throw new IllegalArgumentException("SubCommand method must have (CommandSender, String[]) parameters");
                }
                commandData.addSubCommand(subCommand.name(), method, subCommand);
                subCommandCount++;
                plugin.getLogger().info("Registered subcommand: " + subCommand.name() + " for " + commandInfo.name());
            }
        }

        plugin.getCommand(commandInfo.name()).setExecutor(commandData);
        plugin.getCommand(commandInfo.name()).setTabCompleter(commandData);

        long endTime = System.currentTimeMillis();
        plugin.getLogger().info("Registered command: " + commandInfo.name() + " with " + subCommandCount + " subcommands in " + (endTime - startTime) + "ms");
    }
}