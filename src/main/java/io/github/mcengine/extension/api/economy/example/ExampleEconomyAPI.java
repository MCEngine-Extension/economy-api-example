package io.github.mcengine.extension.api.economy.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
import io.github.mcengine.api.economy.extension.api.IMCEngineEconomyAPI;

import io.github.mcengine.extension.api.economy.example.command.EconomyAPICommand;
import io.github.mcengine.extension.api.economy.example.listener.EconomyAPIListener;
import io.github.mcengine.extension.api.economy.example.tabcompleter.EconomyAPITabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Main class for the Economy API example module.
 * <p>
 * Registers the {@code /economyapiexample} command and related event listeners.
 */
public class ExampleEconomyAPI implements IMCEngineEconomyAPI {

    /**
     * Custom extension logger for this module, with contextual labeling.
     */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the Economy API example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "API", "EconomyExampleAPI");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new EconomyAPIListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /economyapiexample command
            Command economyApiExampleCommand = new Command("economyapiexample") {

                /**
                 * Handles command execution for /economyapiexample.
                 */
                private final EconomyAPICommand handler = new EconomyAPICommand();

                /**
                 * Handles tab-completion for /economyapiexample.
                 */
                private final EconomyAPITabCompleter completer = new EconomyAPITabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public java.util.List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            economyApiExampleCommand.setDescription("Economy API example command.");
            economyApiExampleCommand.setUsage("/economyapiexample");

            // Dynamically register the /economyapiexample command
            commandMap.register(plugin.getName().toLowerCase(), economyApiExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleEconomyAPI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the Economy API example module is disabled/unloaded.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-economy-api-example");
    }
}
