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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.jcup.junit.serverresult.JunitModel.JUnitModelStatistics;
import de.jcup.junit.serverresult.JunitServerResultInfoAreaCalculator.JunitServerResultInfoAreaResult;

public class JUnitServerResultCanvas extends Canvas {

    private Color red;
    private Color failure;
    private Color skipped;
    private Color success;
    
    private JunitServerResultInfoAreaCalculator calculator = new JunitServerResultInfoAreaCalculator();
    private JUnitModelStatistics statistics = new JUnitModelStatistics();

    public JUnitServerResultCanvas(Composite parent) {
        super(parent, SWT.NONE);

        /* initialize */

        addPaintListener(new ServerResultPaintListener());

    }
    
    public void setStatistics(JUnitModelStatistics stat) {
        this.statistics = stat;
        if (stat==null) {
            setToolTipText("");
        }else {
            StringBuilder sb = new StringBuilder();
            if (stat.getTestSuites() > 0) {
                sb.append("Testsuites:");
                sb.append(stat.getTestSuites());
                sb.append(", testcases:");
                sb.append(stat.getTestcases());
                sb.append("[ success:");
                sb.append(stat.getSuccess());
                sb.append(", skipped:");
                sb.append(stat.getSkipped());
                sb.append(",failures:");
                sb.append(stat.getFailures());
                sb.append(", errors:");
                sb.append(stat.getErrors());
                sb.append("]");
            }
            setToolTipText(sb.toString());
        }
        redraw();
    }
    
    public void setColorError(Color red) {
        this.red = red;
    }
    
    public void setColorSuccess(Color success) {
        this.success = success;
    }
    
    public void setColorFailure(Color failure) {
        this.failure = failure;
    }
    
    public void setColorSkipped(Color skipped) {
        this.skipped = skipped;
    }

    private class ServerResultPaintListener implements PaintListener {


        @Override
        public void paintControl(PaintEvent e) {
            GC gc = e.gc;
            
            JUnitServerResultCanvas canvas = JUnitServerResultCanvas.this;
            Rectangle clientArea = canvas.getClientArea();
            Area clientArea1 = fromRectangle(clientArea);

            if (statistics==null || statistics.testcases==0) {
                gc.setBackground(canvas.getParent().getBackground());
                gc.fillRectangle(clientArea);
                return;
            }
            JunitServerResultInfoAreaResult result = calculator.calculate(clientArea1, statistics);
            gc.setBackground(getGreenColor());
            gc.fillRectangle(toRectangle(result.green_ok));
            
            gc.setBackground(getWhiteColor());
            gc.fillRectangle(toRectangle(result.white_ignored));
            
            gc.setBackground(getBlueColor());
            gc.fillRectangle(toRectangle(result.blue_failed));
            
            gc.setBackground(getRedColor());
            gc.fillRectangle(toRectangle(result.red_error));

        }

        private Display getDisplay() {
            JUnitServerResultCanvas canvas = JUnitServerResultCanvas.this;
            return canvas.getDisplay();
        }

        private Color getRedColor() {
            if (red!=null) {
                return red;
            }
            return getDisplay().getSystemColor(SWT.COLOR_RED);
        }

        private Color getBlueColor() {
            if (failure!=null) {
                return failure;
            }
            return getDisplay().getSystemColor(SWT.COLOR_BLUE);
        }

        private Color getWhiteColor() {
            if (skipped!=null) {
                return skipped;
            }
            return getDisplay().getSystemColor(SWT.COLOR_WHITE);
        }

        private Color getGreenColor() {
            if (success!=null) {
                return success;
            }
            return getDisplay().getSystemColor(SWT.COLOR_GREEN);
        }

        private Rectangle toRectangle(Area area) {
            Rectangle r = new Rectangle(area.x, area.y, area.width, area.height);
            return r;
        }

        private Area fromRectangle(Rectangle area) {
            Area r = new Area(area.x, area.y, area.width, area.height);
            return r;
        }

    }

}
