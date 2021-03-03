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

/**
 * Constant definitions for plug-in preferences
 */
public enum JUnitServerResultPreferenceConstants implements PreferenceIdentifiable {

    ENABLE_AUTOLINK_WITH_LOGFILE_ON_LOGFILE_LOCATION_CHANGES("enableAutoLinkWithLogFileOnLogfileLocationChanges"),
    
    LOGFILE_TIMESTAMP_PATTERN("logFileTimeStampPattern"),
    
    LAST_IMPORT_ABSOLUTE_PATH("lastImportAbsolutePath"), 
    
    ADDITIONAL_MILLISECONDS_SHOWN_IN_LOGFILE_EDITOR("additionalMillisecondsShownInLogFileEditor"),
    
    COLORIZE_COMPLETE_LOG_LINE("colorizeCompleteLogLine"),
    ;

    private String id;

    private JUnitServerResultPreferenceConstants(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
