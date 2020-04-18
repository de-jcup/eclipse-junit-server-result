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

import java.util.StringTokenizer;

public class PatternNameMatcher {

    public static boolean patternMatchesName(String pattern, String name) {
        if (pattern == null) {
            return true;
        }
        if (pattern.isEmpty()) {
            return true;
        }

        if (name == null) {
            return false;
        }
        if (name.isEmpty()) {
            return false;
        }
        String lowerName = name.toLowerCase();
        String lowerPattern = pattern.toLowerCase();

        if (lowerPattern.indexOf("*") != -1) {
            /* with asterisk, so split */
            String remainingName = lowerName;
            StringTokenizer tokenizer = new StringTokenizer(lowerPattern,"*");
            boolean found= true;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int index = remainingName.indexOf(token);
                if (index==-1) {
                    found=false;
                    break;
                }
                remainingName=remainingName.substring(index);
            }
            return found;

        } else if (lowerName.contains(lowerPattern)) {
            return true;
        }

        return false;
    }

}
