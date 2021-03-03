/*
 * Copyright 2021 Albert Tregnaghi
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

import static de.jcup.junit.serverresult.editor.JunitServerResultDocumentIdentifiers.*;
import static de.jcup.junit.serverresult.preferences.JUnitServerResultSyntaxColorPreferenceConstants.*;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import de.jcup.eclipse.commons.presentation.PresentationSupport;
import de.jcup.junit.serverresult.Activator;
import de.jcup.junit.serverresult.ColorManager;
import de.jcup.junit.serverresult.preferences.JUnitServerResultPreferences;

public class JunitServerResultLogFileEditorViewerConfiguration extends TextSourceViewerConfiguration {

    private JunitServerResultDefaultTextScanner scanner;
    private ColorManager colorManager;
    private TextAttribute defaultTextAttribute;

    JunitServerResultLogFileEditorViewerConfiguration() {
        colorManager=Activator.getDefault().getColorManager();
        RGB rgbColor = getPreferences().getColor(COLOR_NORMAL_TEXT);
        Color color = colorManager.getColor(rgbColor);
        this.defaultTextAttribute = new TextAttribute(color);
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        addDefaultPresentation(reconciler);

        addPresentation(reconciler, LOG_DEBUG.getId(), getPreferences().getColor(COLOR_LOG_DEBUG), SWT.NONE);
        addPresentation(reconciler, LOG_INFO.getId(), getPreferences().getColor(COLOR_LOG_INFO), SWT.NONE);
        addPresentation(reconciler, LOG_WARN.getId(), getPreferences().getColor(COLOR_LOG_WARN), SWT.BOLD);
        addPresentation(reconciler, LOG_ERROR.getId(), getPreferences().getColor(COLOR_LOG_ERROR), SWT.BOLD);

        return reconciler;
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        /* @formatter:off */
        return allIdsToStringArray( 
                IDocument.DEFAULT_CONTENT_TYPE);
        /* @formatter:on */
    }
    
    public static JUnitServerResultPreferences getPreferences() {
        return JUnitServerResultPreferences.getInstance();
    }

    private void addPresentation(PresentationReconciler reconciler, String id, RGB rgb, int style) {
        TextAttribute textAttribute = new TextAttribute(colorManager.getColor(rgb), defaultTextAttribute.getBackground(), style);
        PresentationSupport presentation = new PresentationSupport(textAttribute);
        reconciler.setDamager(presentation, id);
        reconciler.setRepairer(presentation, id);
    }

    private void addDefaultPresentation(PresentationReconciler reconciler) {
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getYamlDefaultTextScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    }

    private JunitServerResultDefaultTextScanner getYamlDefaultTextScanner() {
        if (scanner == null) {
            scanner = new JunitServerResultDefaultTextScanner(colorManager);
            updateTextScannerDefaultColorToken();
        }
        return scanner;
    }

    public void updateTextScannerDefaultColorToken() {
        if (scanner == null) {
            return;
        }
        RGB color = getPreferences().getColor(COLOR_NORMAL_TEXT);
        scanner.setDefaultReturnToken(createColorToken(color));
    }
    
    private IToken createColorToken(RGB rgb) {
        Token token = new Token(new TextAttribute(colorManager.getColor(rgb)));
        return token;
    }
}
