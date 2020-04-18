/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.junit.serverresult;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsoleStream;

import de.jcup.eclipse.commons.ui.EclipseUtil;

public class JUnitServerResultConsoleUtil {

    public static void error(String message) {
        output(message);
    }

    /**
     * Will output to {@link JUnitServerResultConsole} instance
     * 
     * @param message
     */
    public static void output(String message) {
        JUnitServerResultConsole jUnitServerResultConsole = findConsole();
        MessageConsoleStream out = jUnitServerResultConsole.newMessageStream();
        out.println(message);
    }

    public static void showConsole() {
        getConsoleManager().showConsoleView(findConsole());
    }

    private static JUnitServerResultConsole findConsole() {
        IConsoleManager conMan = getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++)
            if (existing[i] instanceof JUnitServerResultConsole) {
                return (JUnitServerResultConsole) existing[i];
            }
        // no console found, so create a new one
        JUnitServerResultConsole jUnitServerResultConsole = new JUnitServerResultConsole(EclipseUtil.createImageDescriptor("icons/junit-serverresult.png", Activator.PLUGIN_ID));
        conMan.addConsoles(new IConsole[] { jUnitServerResultConsole });

        return jUnitServerResultConsole;
    }

    private static IConsoleManager getConsoleManager() {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        return conMan;
    }

    public static void clearConsole() {
        findConsole().clearConsole();
    }

}
