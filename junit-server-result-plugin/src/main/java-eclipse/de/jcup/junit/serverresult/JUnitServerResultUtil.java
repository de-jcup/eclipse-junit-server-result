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

import java.nio.file.Path;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.junit.serverresult.editor.JUnitServerResultLogFileEditor;
import de.jcup.junit.serverresult.editor.StringEditorInput;
import de.jcup.junit.serverresult.views.JUnitServerResultView;

public class JUnitServerResultUtil {

    private static final String ECLIPSE_JDT_JUNIT_RESULT_VIEW = "org.eclipse.jdt.junit.ResultView";
    private static final String ECLIPSE_CONSOLE_VIEW = "org.eclipse.ui.console.ConsoleView";
    private static final JunitImportContext junitImportContext = new JunitImportContext();

    public static void showTestRunnerViewPartInActivePage() {
        showViewInActivePage(ECLIPSE_JDT_JUNIT_RESULT_VIEW);
    }

    public static void showConsoleViewInActivePage() {
        showViewInActivePage(ECLIPSE_CONSOLE_VIEW);
    }

    
    public static JunitImportContext getContext() {
        return junitImportContext;
    }

    private static void showViewInActivePage(String viewId) {
        EclipseUtil.safeAsyncExec(()-> {
            try {
                IWorkbenchPage page = EclipseUtil.getActivePage();
                if (page == null) {
                    return;
                }
                IViewPart view = page.findView(viewId);
                if (view == null) {
                    /* create and show the result view if it isn't created yet. */
                    page.showView(viewId, null, IWorkbenchPage.VIEW_VISIBLE);
                }
            } catch (PartInitException pie) {
                Activator.getDefault().logError("Was not able to show junit view", pie);
            }
        });
    }

    public static JUnitServerResultLogFileEditor findExistingLogFileEditorOrNull() {
        final IEditorReference[] editorRefs = EclipseUtil.getActivePage().getEditorReferences();
        for (final IEditorReference editorRef : editorRefs) {
            final IEditorPart part = editorRef.getEditor(false);
            if (part instanceof JUnitServerResultLogFileEditor) {
                return (JUnitServerResultLogFileEditor) part;
            }
        }
        return null;
    }
    
    public static JUnitServerResultView findJUnitResultViewOrNull() {
        IViewPart viewPart  = EclipseUtil.getActivePage().findView(JUnitServerResultView.ID);
        return (JUnitServerResultView)viewPart;
    }
    
    public static void setLogEditorText(Path path, final String targetText) {
        EclipseUtil.safeAsyncExec(()->{
            
            String editorId = JUnitServerResultLogFileEditor.EDITOR_ID;
            JUnitServerResultLogFileEditor logFileEditor = JUnitServerResultUtil.findExistingLogFileEditorOrNull();
            StringEditorInput input = new StringEditorInput(path, targetText);
            if (logFileEditor != null) {
                EclipseUtil.getActivePage().reuseEditor(logFileEditor, input);
            } else {
                try {
                    EclipseUtil.getActivePage().openEditor(input, editorId);
                } catch (PartInitException e) {
                   Activator.getDefault().logError("Cannot open editor", e);
                }
            }
        });
    }

}
