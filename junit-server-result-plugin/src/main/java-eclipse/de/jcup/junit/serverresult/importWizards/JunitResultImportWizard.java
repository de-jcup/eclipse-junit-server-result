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
package de.jcup.junit.serverresult.importWizards;

import java.io.File;
import java.util.Arrays;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.jcup.junit.serverresult.preferences.JUnitServerResultPreferences;
import de.jcup.junit.serverresult.views.JUnitResultFileImportJobStarter;

public class JunitResultImportWizard extends Wizard implements IImportWizard {

    JUnitServerResultImportWizardPage mainPage;

    public JunitResultImportWizard() {
        super();
    }

    public boolean performFinish() {
        String fileName = mainPage.getFileNameField().getText();
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        File asFile = new File(fileName);
        if (! asFile.exists()) {
            return false;
        }
        JUnitResultFileImportJobStarter starter = new JUnitResultFileImportJobStarter();
        starter.startImport(Arrays.asList(asFile));
        /* mark last import */
        JUnitServerResultPreferences.getInstance().setLastImportAbsolutePath(fileName);
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("File Import Wizard"); // NON-NLS-1
        setNeedsProgressMonitor(true);
        mainPage = new JUnitServerResultImportWizardPage("Import Files"); // NON-NLS-1
    }

    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

}
