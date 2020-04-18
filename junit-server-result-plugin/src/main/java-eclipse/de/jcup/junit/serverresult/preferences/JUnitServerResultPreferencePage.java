/*
 * Copyright 2019 Albert Tregnaghi
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
 package de.jcup.junit.serverresult.preferences;

import java.text.SimpleDateFormat;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.junit.serverresult.Activator;


public class JUnitServerResultPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private StringFieldEditor logFileDatePatternFieldEditor;

    public JUnitServerResultPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Preferences for JUnit server result handling");
    }

    @Override
    public boolean isValid() {
        String datePattern = logFileDatePatternFieldEditor.getStringValue();
        try {
            new SimpleDateFormat(datePattern);
        }catch(RuntimeException e) {
            setErrorMessage("Log file date pattern not correct:"+datePattern+" - must be in simple date format!");
            return false;
        }
        return super.isValid();
    }
    
    /**
     * Creates the field editors. Field editors are abstractions of the common GUI
     * blocks needed to manipulate various types of preferences. Each field editor
     * knows how to save and restore itself.
     */
    public void createFieldEditors() {
        addField(new BooleanFieldEditor(JUnitServerResultPreferenceConstants.ENABLE_AUTOLINK_WITH_LOGFILE_ON_LOGFILE_LOCATION_CHANGES.getId(), "&Enable autolink with log file on drop", getFieldEditorParent()));

        logFileDatePatternFieldEditor = new StringFieldEditor(JUnitServerResultPreferenceConstants.LOGFILE_TIMESTAMP_PATTERN.getId(), "&Logfile time stamp date pattern", getFieldEditorParent());
        addField(logFileDatePatternFieldEditor);

    }

    
    public void init(IWorkbench workbench) {
    }

}