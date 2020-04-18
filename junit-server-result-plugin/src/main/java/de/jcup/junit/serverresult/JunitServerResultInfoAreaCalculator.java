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

import de.jcup.junit.serverresult.JunitModel.JUnitModelStatistics;

public class JunitServerResultInfoAreaCalculator {

    private static final int MIN_WIDTH_WHEN_AT_LEASTONE = 3;

    public class JunitServerResultInfoAreaResult {
        public final Area green_ok = new Area();
        public final Area blue_failed = new Area();
        public final Area red_error = new Area();
        public final Area white_ignored = new Area();

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName()).append(":\nred:");
            sb.append(red_error.toString());
            sb.append("\nblue:");
            sb.append(blue_failed.toString());
            sb.append("\nwhite:");
            sb.append(white_ignored.toString());
            sb.append("\ngreen:");
            sb.append(green_ok.toString());
            sb.append("\n");
            return sb.toString();
        }
    }

    public JunitServerResultInfoAreaResult calculate(Area clientArea, JUnitModelStatistics stat) {
        JunitServerResultInfoAreaResult result = new JunitServerResultInfoAreaResult();

        adoptHeightAndY(clientArea, result.blue_failed, result.green_ok, result.red_error, result.white_ignored);

        int all = stat.testcases;
        int onePercent = all / 100;
        if (onePercent==0) {
            onePercent=1;
        }

        /* red, blue, white, followed by green at the end */
        int allPixels = clientArea.width - clientArea.x;
        int pixelPerOnePercent = (int) (allPixels / 100);

        int percentRed = stat.errors / onePercent;
        int percentBlue = stat.failures / onePercent;
        int percentWhite = stat.skipped / onePercent;

        result.red_error.width = pixelPerOnePercent * percentRed;
        result.blue_failed.width = pixelPerOnePercent * percentBlue;
        result.white_ignored.width = pixelPerOnePercent * percentWhite;

        ensureMinPixels(stat.errors, result.red_error);
        ensureMinPixels(stat.failures, result.blue_failed);
        ensureMinPixels(stat.skipped, result.white_ignored);

        int otherPixelsNotGreen = result.red_error.width + result.blue_failed.width + result.white_ignored.width;

        result.red_error.x = clientArea.x;
        result.blue_failed.x = result.red_error.x + result.red_error.width;
        result.white_ignored.x = result.blue_failed.x + result.blue_failed.width;
        result.green_ok.x = result.white_ignored.x + result.white_ignored.width;

        result.green_ok.width = allPixels - otherPixelsNotGreen;
        // pixel green is just the remaining space...

        return result;
    }

    private void ensureMinPixels(int foundEntries, Area area) {
        if (foundEntries==0) {
            return;
        }
        if (area.width< MIN_WIDTH_WHEN_AT_LEASTONE) {
            area.width = MIN_WIDTH_WHEN_AT_LEASTONE;
        }
    }

    private void adoptHeightAndY(Area origin, Area... areas) {
        for (Area area : areas) {
            area.height = origin.height;
            area.y = origin.y;
        }
    }

}
