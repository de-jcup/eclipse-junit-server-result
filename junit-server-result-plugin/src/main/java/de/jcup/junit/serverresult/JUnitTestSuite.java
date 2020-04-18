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

import java.util.ArrayList;
import java.util.List;

public class JUnitTestSuite {

    public String name;
    public String failures;
    public String errors;
    public StringBuilder systemOut = new StringBuilder();
    public StringBuilder systemErr = new StringBuilder();
    public List<JUnitTestCase> testCases= new ArrayList<>();
    public String timeStamp;
    public String timeInSeconds;
    public String skipped;
    public String location;
    
    @Override
    public String toString() {
        return "JUnitTestSuite [name=" + name + ", failures=" + failures + ", errors=" + errors + ", skipped=" + skipped + ", location=" + location+"]";
    }
    public boolean hasErrors() {
        boolean hasErrors= hasEntries(errors);
        return hasErrors;
    }
    
    public boolean hasFailures() {
        return hasEntries(failures);
    }
    
    public boolean hasSkipped() {
        return hasEntries(skipped);
    }
    
    private boolean hasEntries(String text) {
        return text != null && !"".contentEquals(text) && !"0".contentEquals(text);
    }
    
}
