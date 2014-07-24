/*
 * Copyright (C) 2014 Dabo Ross <http://daboross.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.devtest;

import java.util.logging.Level;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DevTestPlugin extends JavaPlugin {

    private StringBuilder currentText;
    private ScriptEngine engine;
    private Bindings bindings;

    @Override
    public void onEnable() {
        engine = new ScriptEngineManager().getEngineByExtension("javascript");
        bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("skywars", getServer().getPluginManager().getPlugin("SkyWars"));
        bindings.put("server", getServer());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "Please specify an argument");
            return true;
        }
        if (currentText == null) {
            currentText = new StringBuilder();
        }
        if (args[0].startsWith("+")) {
            args[0] = args[0].substring(1);
            for (String str : args) {
                currentText.append(str);
            }
        } else {
            if (args[0].startsWith("\\+")) {
                args[0] = args[0].substring(1);
            }
            for (String str : args) {
                currentText.append(str);
            }
            Object result;
            bindings.put("player", sender);
            try {
                result = engine.eval(currentText.toString());
            } catch (ScriptException ex) {
                getLogger().log(Level.SEVERE, "Error", ex);
                return true;
            }
            bindings.put("result", result);
            currentText = new StringBuilder();
        }
        return true;
    }
}
