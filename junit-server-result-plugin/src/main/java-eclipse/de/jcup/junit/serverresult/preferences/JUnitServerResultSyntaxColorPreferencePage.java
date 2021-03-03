package de.jcup.junit.serverresult.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.junit.serverresult.editor.JunitServerResultColorConstants;

import static de.jcup.junit.serverresult.preferences.JUnitServerResultSyntaxColorPreferenceConstants.*;

public class JUnitServerResultSyntaxColorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public JUnitServerResultSyntaxColorPreferencePage() {
        setPreferenceStore(JUnitServerResultPreferences.getInstance().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        Map<JUnitServerResultSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap = new HashMap<JUnitServerResultSyntaxColorPreferenceConstants, ColorFieldEditor>();
        for (JUnitServerResultSyntaxColorPreferenceConstants colorIdentifier : JUnitServerResultSyntaxColorPreferenceConstants.values()) {
            ColorFieldEditor editor = new ColorFieldEditor(colorIdentifier.getId(), colorIdentifier.getLabelText(), parent);
            editorMap.put(colorIdentifier, editor);
            addField(editor);
        }
        Button restoreDarkThemeColorsButton = new Button(parent, SWT.PUSH);
        restoreDarkThemeColorsButton.setText("Restore Defaults for Dark Theme");
        restoreDarkThemeColorsButton.setToolTipText("Same as 'Restore Defaults' but for dark themes.\n Editor makes just a suggestion, you still have to apply or cancel the settings.");
        restoreDarkThemeColorsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                /* editor colors */
                changeColor(editorMap, COLOR_NORMAL_TEXT, JunitServerResultColorConstants.MIDDLE_GRAY);
                changeColor(editorMap, COLOR_LOG_DEBUG, new RGB(65,104,65));
                changeColor(editorMap, COLOR_LOG_INFO, new RGB(114, 159, 207));// light blue
                changeColor(editorMap, COLOR_LOG_WARN, new RGB(233, 185, 110));// light orange
                changeColor(editorMap, COLOR_LOG_ERROR, JunitServerResultColorConstants.BRIGHT_RED);

            }

            private void changeColor(Map<JUnitServerResultSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap, JUnitServerResultSyntaxColorPreferenceConstants colorId, RGB rgb) {
                editorMap.get(colorId).getColorSelector().setColorValue(rgb);
            }

        });

    }

}