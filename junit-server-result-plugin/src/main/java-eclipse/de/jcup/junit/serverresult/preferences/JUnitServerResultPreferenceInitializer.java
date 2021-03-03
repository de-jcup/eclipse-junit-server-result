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

import static de.jcup.junit.serverresult.preferences.JUnitServerResultSyntaxColorPreferenceConstants.*;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.junit.serverresult.Activator;
import de.jcup.junit.serverresult.JUnitServerResultConstants;

import static de.jcup.junit.serverresult.editor.JunitServerResultColorConstants.*;

/**
 * Class used to initialize default preference values.
 */
public class JUnitServerResultPreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(JUnitServerResultPreferenceConstants.ENABLE_AUTOLINK_WITH_LOGFILE_ON_LOGFILE_LOCATION_CHANGES.getId(), true);
        store.setDefault(JUnitServerResultPreferenceConstants.LOGFILE_TIMESTAMP_PATTERN.getId(), JUnitServerResultConstants.DEFAULT_LOGFILE_FORMAT);
        store.setDefault(JUnitServerResultPreferenceConstants.ADDITIONAL_MILLISECONDS_SHOWN_IN_LOGFILE_EDITOR.getId(), 1000);
        
        
        JUnitServerResultPreferences preferences = JUnitServerResultPreferences.getInstance();
        
        preferences.setBooleanPreference(JUnitServerResultPreferenceConstants.COLORIZE_COMPLETE_LOG_LINE, true);

        /* +++++++++++++++++ */
        /* + Editor Colors + */
        /* +++++++++++++++++ */
        preferences.setDefaultColor(COLOR_NORMAL_TEXT, BLACK);
        preferences.setDefaultColor(COLOR_LOG_DEBUG, GREEN);
        preferences.setDefaultColor(COLOR_LOG_INFO, CADET_BLUE);
        preferences.setDefaultColor(COLOR_LOG_WARN, ORANGE);
        preferences.setDefaultColor(COLOR_LOG_ERROR, RED);
        
        

    }

}
