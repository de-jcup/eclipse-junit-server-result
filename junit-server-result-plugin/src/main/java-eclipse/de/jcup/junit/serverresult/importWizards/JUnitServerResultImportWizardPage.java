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
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.eclipse.commons.ui.SWTFactory;
import de.jcup.junit.serverresult.Activator;
import de.jcup.junit.serverresult.preferences.JUnitServerResultPreferences;

public class JUnitServerResultImportWizardPage extends WizardPage implements Listener {

    // widgets
    private Text fFileNameField = null;
    private Button fBrowseForFileButton = null;
    private Button fBrowseForFolderButton;
    private Image imgDirectory;

    /**
     * This is the default constructor. It accepts the name for the tab as a
     * parameter
     *
     * @param pageName the name of the page
     */
    public JUnitServerResultImportWizardPage(String pageName) {
        super(pageName, "Import junit server resullts", null);
    }

    @Override
    public void handleEvent(Event event) {
        Widget source = event.widget;
        if (source == fBrowseForFileButton) {
            handleBrowseForFileButtonPressed();
        }
        if (source == fBrowseForFolderButton) {
            handleBrowseForFolderButtonPressed();
        }
        setPageComplete(detectPageComplete());
    }

    protected void handleBrowseForFileButtonPressed() {
        FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.OPEN | SWT.SHEET);
        dialog.setText("Import junit result file");
        dialog.setFilterExtensions(new String[] { "*.xml"} ); 
        String file = dialog.open();
        if (file != null) {
            fFileNameField.setText(file);
        }
    }
    protected void handleBrowseForFolderButtonPressed() {
        DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.OPEN | SWT.SHEET);
        dialog.setText("Import folder structure containing junit result files (TEST-*.xml)");
        String file = dialog.open();
        if (file != null) {
            fFileNameField.setText(file);
        }
    }

    @Override
    public void createControl(Composite parent) {
        imgDirectory = EclipseUtil.createImageDescriptor("/icons/folder.png", Activator.PLUGIN_ID).createImage();
        initializeDialogUnits(parent);
        Composite mainComposite = SWTFactory.createComposite(parent, 1, 2, GridData.FILL_BOTH);
        
        
        
        /* path/file selection etc. */
        Composite composite1 = SWTFactory.createComposite(mainComposite, 4, 1, GridData.FILL_HORIZONTAL);
        
        Label fFileNameLabel = new Label(composite1,SWT.NONE);
        fFileNameLabel.setText("File/Folder:");
        fFileNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        fFileNameField= new Text(composite1, SWT.BORDER);
        fFileNameField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fFileNameField.addListener(SWT.Modify,this);
        fFileNameField.setText(JUnitServerResultPreferences.getInstance().getLastImportAbsolutePath());
        
        fBrowseForFileButton = new Button(composite1,SWT.PUSH);
        fBrowseForFileButton.setText("...");
        fBrowseForFileButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
        fBrowseForFileButton.addListener(SWT.MouseUp, this);
        
        fBrowseForFolderButton = new Button(composite1,SWT.PUSH);
        fBrowseForFolderButton.setImage(imgDirectory);
        fBrowseForFolderButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
        fBrowseForFolderButton.addListener(SWT.MouseUp, this);
        
        
        
//        /* defaults */
//        Composite composite2 = SWTFactory.createComposite(mainComposite, 3, 1, GridData.HORIZONTAL_ALIGN_END);
//        
//        Button restoreDefaultsButton = new Button(composite2,SWT.PUSH);
//        restoreDefaultsButton.setText("Restore last");
//        restoreDefaultsButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
//        restoreDefaultsButton.addListener(SWT.MouseUp, this);

//        Button useAsDefaultButton = new Button(composite2,SWT.PUSH);
//        useAsDefaultButton.setText("Use as Default");
//        useAsDefaultButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
//        useAsDefaultButton.addListener(SWT.MouseUp, this);
//        
        
        
        setControl(mainComposite);
        setPageComplete(detectPageComplete());
    }

    @Override
    public Image getImage() {
        return super.getImage();
    }

    /**
     * This method is used to determine if the page can be "finished". To be
     * determined "finishable" there must be an import path.
     *
     * @return if the prerequisites of the wizard are met to allow the wizard to
     *         complete.
     */
    private boolean detectPageComplete() {
        String fileName = fFileNameField.getText().trim();
        File file = new File(fileName);
        if (!file.exists()) {
            setMessage(MessageFormat.format("File/Folder does not exist", new Object[] { fileName }), ERROR);
            return false;
        }

        if (file.isDirectory()) {
            setMessage("Finish will import TEST-*.xml files inside given directory and it's children");
        }else {
            setMessage("Finish will import given file");
        }
        return true;
    }

    /**
     * <p>
     * This method is called when the Finish button is click on the main wizard
     * dialog To import the breakpoints, we read then from the tree and add them
     * into the BreakpointManager
     * </p>
     * 
     * @return if the import operation was successful or not
     */
    public boolean finish() {
        return finish(null);
    }

    public boolean finish(final List<IMarker> selectedMarkers) {
        return true;
    }

    public Text getFileNameField() {
        return fFileNameField;
    }
    
    @Override
    public void dispose() {
        if (imgDirectory!=null) {
            imgDirectory.dispose();
        }
        super.dispose();
    }

}
