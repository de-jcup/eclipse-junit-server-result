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

import static de.jcup.junit.serverresult.preferences.JUnitServerResultPreferenceConstants.*;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.eclipse.commons.ui.ColorUtil;
import de.jcup.junit.serverresult.Activator;

public class JUnitServerResultPreferences {

    private static JUnitServerResultPreferences INSTANCE = new JUnitServerResultPreferences();
    private IPreferenceStore store;

    private JUnitServerResultPreferences() {
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
    }

    public String getStringPreference(JUnitServerResultPreferenceConstants id) {
        String data = getPreferenceStore().getString(id.getId());
        if (data == null) {
            data = "";
        }
        return data;
    }

    public boolean getBooleanPreference(JUnitServerResultPreferenceConstants id) {
        boolean data = getPreferenceStore().getBoolean(id.getId());
        return data;
    }

    public int getIntegerPreference(JUnitServerResultPreferenceConstants id) {
        int data = getPreferenceStore().getInt(id.getId());
        return data;
    }

    public void setBooleanPreference(JUnitServerResultPreferenceConstants id, boolean value) {
        getPreferenceStore().setValue(id.getId(), value);
    }

    public IPreferenceStore getPreferenceStore() {
        return store;
    }

    public boolean getDefaultBooleanPreference(JUnitServerResultPreferenceConstants id) {
        boolean data = getPreferenceStore().getDefaultBoolean(id.getId());
        return data;
    }
    
    public boolean isColorizingFullLogLine() {
        return getBooleanPreference(COLORIZE_COMPLETE_LOG_LINE);
    }

    public RGB getColor(PreferenceIdentifiable identifiable) {
        RGB color = PreferenceConverter.getColor(getPreferenceStore(), identifiable.getId());
        return color;
    }

    /**
     * Returns color as a web color in format "#RRGGBB"
     * 
     * @param identifiable
     * @return web color string
     */
    public String getWebColor(PreferenceIdentifiable identifiable) {
        RGB color = getColor(identifiable);
        if (color == null) {
            return null;
        }
        String webColor = ColorUtil.convertToHexColor(color);
        return webColor;
    }

    public void setDefaultColor(PreferenceIdentifiable identifiable, RGB color) {
        PreferenceConverter.setDefault(getPreferenceStore(), identifiable.getId(), color);
    }

    public static JUnitServerResultPreferences getInstance() {
        return INSTANCE;
    }

    public boolean getAutoLinkLogFileWhenLogFileLocationChanges() {
        return getBooleanPreference(ENABLE_AUTOLINK_WITH_LOGFILE_ON_LOGFILE_LOCATION_CHANGES);
    }

    public int getAdditionalMillisecondsInLogFileEditor() {
        return getIntegerPreference(ADDITIONAL_MILLISECONDS_SHOWN_IN_LOGFILE_EDITOR);
    }

    public String getLogTimeDatePattern() {
        return getPreferenceStore().getString(LOGFILE_TIMESTAMP_PATTERN.getId());
    }

    public String getLastImportAbsolutePath() {
        return getPreferenceStore().getString(LAST_IMPORT_ABSOLUTE_PATH.getId());
    }

    public void setLastImportAbsolutePath(String absolutePath) {
        getPreferenceStore().setValue(LAST_IMPORT_ABSOLUTE_PATH.getId(), absolutePath);
    }

}
