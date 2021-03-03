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
package de.jcup.junit.serverresult.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;

public class JUnitServerResultLogFileEditor extends TextEditor implements IReusableEditor {

    public static final String EDITOR_ID = "de.jcup.junit.serverresult.editor.JUnitServerResultLogFileEditor";
    private static FileDocumentProvider fileDocumentProvider = new JunitServerResultLogFileDocumentProvider();

    public JUnitServerResultLogFileEditor() {
        setSourceViewerConfiguration(new JunitServerResultLogFileEditorViewerConfiguration());
        setDocumentProvider(fileDocumentProvider);
    }
    
    @Override
    protected void installCodeMiningProviders() {
        /* we do NOT use this - prevents an NPE because of used input */
    }
    
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
    }
    

}
