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
package de.jcup.junit.serverresult;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class TimeStampAreaFinder {

    private int additionalMillisecondsAtEnd;

    public LogFileLocation findLogFileLocationInArea(String text, DateFormat logDateFormat, TimeStampArea wantedArea, ProgressCallback callback) {
        if (callback==null) {
            callback= ProgressCallback.NULL_PROGRESS;
        }
        LogFileLocation location = new LogFileLocation();

        if (wantedArea.beginTimeStamp == null) {
            location.begin = 0;
        } else {
            location.begin = findLocation(text, wantedArea.beginTimeStamp, logDateFormat, true, callback);
        }

        if (wantedArea.endTimeStamp == null) {
            location.end = LogFileLocation.UNDEFINED;
        } else {
            String endTextToScan;
            int scanFrom = 0;
            if (location.begin != -1) {
                /* ignore former text - so speed up... */
                endTextToScan = text.substring(location.begin);
                scanFrom = location.begin;
            } else {
                endTextToScan = text;
            }
            location.end = scanFrom + findLocation(endTextToScan, wantedArea.endTimeStamp, logDateFormat, false, callback);
            if (location.end!=LogFileLocation.UNDEFINED && location.end<location.begin) {
                location.end = scanFrom + findLocation(endTextToScan, wantedArea.endTimeStamp, logDateFormat, true, callback);
            }
        }
        return location;
    }


    private int findLocation(String text, Date timeStamp, DateFormat logDateFormat, boolean forward, ProgressCallback callback) {
        /* we start to lookup for exact timestamp */
        String search = logDateFormat.format(timeStamp);
        int index = text.indexOf(search);
        if (index != -1) {
            return index;
        }
        /*
         * we will (most times) not find the exact one, but we simply try until we found
         * next one - or we make break when timeout
         */
        long timeInMillis = timeStamp.getTime();
        int loops = 0;
        int maxLoops = 3000;// 3 seconds
        while (index == -1 && loops < maxLoops) {
            if (callback.isCanceledRequested()) {
                callback.cancelDone();
                return LogFileLocation.UNDEFINED;
            }
            loops++;
            if (forward) {
                timeInMillis++;
            } else {
                timeInMillis--;
            }

            timeStamp = new Date(timeInMillis);
            search = logDateFormat.format(timeStamp);
            index = text.indexOf(search);
        }
        if (index == -1) {
            return LogFileLocation.UNDEFINED;
        }
        return index;
    }

    public TimeStampArea findArea(JUnitTestSuite suite1) {
        TimeStampArea area = new TimeStampArea();
        try {
            if (suite1.timeStamp != null) {
                area.beginTimeStamp = JUnitServerResultConstants.JUNIT_TIMESTAMP_FORMAT.parse(suite1.timeStamp);
            }
        } catch (ParseException e) {
            throw new IllegalStateException("suite timestamp " + suite1.timeStamp + " cannot be parsed");
        }
        if (suite1.timeInSeconds != null && area.beginTimeStamp != null) {
            /* add milliseconds to begin */
            double seconds = Double.parseDouble(suite1.timeInSeconds);
            double milliseconds = seconds * 1000;
            int millisecondsAsInt = (int) milliseconds;

            long millisBegin = area.beginTimeStamp.getTime();
            long millisEnd = millisBegin + millisecondsAsInt +additionalMillisecondsAtEnd;

            area.endTimeStamp = new Date(millisEnd);
        }

        return area;
    }

    public void setAdditionalMilliseconds(int additionalMillisecondsAtEnd) {
        this.additionalMillisecondsAtEnd=additionalMillisecondsAtEnd;
    }

}
