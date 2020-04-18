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
package de.jcup.junit.serverresult.views;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.junit.serverresult.Activator;
import de.jcup.junit.serverresult.JUnitServerResultUtil;
import de.jcup.junit.serverresult.JUnitTestSuite;
import de.jcup.junit.serverresult.JUnitXMLImporter;
import de.jcup.junit.serverresult.JunitImportContext;
import de.jcup.junit.serverresult.JunitModel;

public class JUnitResultFileImportJobStarter {
    private JUnitXMLImporter importer = new JUnitXMLImporter(Activator.getDefault());
    private static Object MONITOR = new Object();
    public void startImport(List<File> files) {
        createImportJob(files).schedule();
    }

    protected Job createImportJob(List<File> files) {
        return new Job("Import JUnit server results:" + files.size()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                synchronized (MONITOR) { // prevent race conditions
                    try {
                        ProgressMonitorCallbackAdapter adapter = new ProgressMonitorCallbackAdapter(monitor);
                        JunitModel resultModel = new JunitModel();
                        for (File file : files) {
                            if (!file.exists()) {
                                continue;
                            }
                            JunitModel loadedModel = importer.importXMLFileOrFolder(file, adapter);
                            for (JUnitTestSuite suite : loadedModel.testSuites.values()) {
                                JUnitTestSuite existing = resultModel.testSuites.get(suite.name);
                                if (existing != null) {
                                    resultModel.duplicateImports.append("\n> IGNORE:").append(suite).append("\n> KEEP  :").append(existing).append('\n');
                                } else {
                                    resultModel.testSuites.put(suite.name, suite);
                                }
                            }
                            resultModel.testSuites.putAll(loadedModel.testSuites);
                            resultModel.duplicateImports.append(loadedModel.duplicateImports);

                        }
                        JunitImportContext context = JUnitServerResultUtil.getContext();
                        context.setModel(resultModel);

                        if (resultModel.duplicateImports.length() > 0) {
                            Activator.getDefault().logError("Import problem! We found following duplicate imports which were ignored:\n\n" + resultModel.duplicateImports.toString(), null);
                            EclipseUtil.safeAsyncExec(() -> MessageDialog.openWarning(EclipseUtil.getActiveWorkbenchShell(), "Import problems detected",
                                    "While importing test results, duplicates where found. Import mechanism kept only one. Maybe you got some old test results on your file system ? \n\nPlease look into error log view for details!"));
                        }

                    } catch (IOException e) {
                        Activator.getDefault().logError("Was not able to import xml file", e);
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Was not able to import xml file", e);
                    } finally {
                        monitor.done();
                    }
                    return Status.OK_STATUS;
                }
                }
               
        };
    }

}
