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

import org.eclipse.jface.viewers.StructuredViewer;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.junit.serverresult.views.JUnitServerResultView;

public class JunitImportContext {

    private JunitModel model;

    public JunitModel getModelOrNull() {
        return model;
    }

    public void setModel(JunitModel model) {
        this.model = model;

        EclipseUtil.safeAsyncExec(new Runnable() {

            @Override
            public void run() {
                final JUnitServerResultView view = JUnitServerResultUtil.findJUnitResultViewOrNull();
                final StructuredViewer viewer = view.getViewer();
                if (viewer == null) {
                    return;
                }
                if (model == null) {
                    viewer.setInput(null);
                } else {
                    viewer.setInput(model.testSuites.values());
                }
                viewer.refresh();
            }
        });

    }
}
