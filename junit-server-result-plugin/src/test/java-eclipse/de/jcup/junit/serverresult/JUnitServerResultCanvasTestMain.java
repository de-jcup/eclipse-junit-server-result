/*
 * Copyright 2020 Albert Tregnaghi
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

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.junit.serverresult.JUnitServerResultCanvas;
import de.jcup.junit.serverresult.JunitModel.JUnitModelStatistics;

public class JUnitServerResultCanvasTestMain {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        JUnitServerResultCanvas canvas = new JUnitServerResultCanvas(shell);
        JUnitModelStatistics statistics = new JUnitModelStatistics();
        statistics.errors=10;
        statistics.failures=5;
        statistics.testcases=300;
        statistics.skipped=3;

        shell.pack();
//        canvas.setStatistics(statistics);
        canvas.setStatistics(null);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
