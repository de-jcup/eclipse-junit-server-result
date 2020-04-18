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

import java.util.SortedMap;
import java.util.TreeMap;

public class JunitModel {
    
    public SortedMap<String, JUnitTestSuite> testSuites = new TreeMap<>();
    
    public StringBuilder duplicateImports = new StringBuilder(0);

    
    public JUnitModelStatistics createStatistics() {
        JUnitModelStatistics stat = new JUnitModelStatistics();
        stat.testSuites = testSuites.size();
        
        for (JUnitTestSuite ts : testSuites.values()) {
            stat.testcases+=ts.testCases.size();
            for (JUnitTestCase tc: ts.testCases) {
                if (tc.error!=null) {
                    stat.errors++;
                    continue;
                }
                if (tc.failure!=null) {
                    stat.failures++;
                    continue;
                }
                if (tc.skipped) {
                    stat.skipped++;
                    continue;
                }
                stat.success++;
            }
        }
        return stat;
    }
    
    public static class JUnitModelStatistics{
        int testSuites;
        int testcases;
        int failures;
        int errors;
        int success;
        int skipped;
        
        public int getSkipped() {
            return skipped;
        }
        
        public int getTestcases() {
            return testcases;
        }
        public int getTestSuites() {
            return testSuites;
        }
        public int getErrors() {
            return errors;
        }
        public int getFailures() {
            return failures;
        }
        public int getSuccess() {
            return success;
        }
        
    }

   
    
}
