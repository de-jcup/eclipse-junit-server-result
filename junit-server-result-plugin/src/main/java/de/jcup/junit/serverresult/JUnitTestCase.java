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

public class JUnitTestCase {

    public String name;
    public String className;
    public String time;
    
    public JunitFailure failure;
    
    public JunitError error;
    public boolean skipped;
    
    
    public static abstract class JunitProblem{
    
        public String message;
        public String type;
        public String text;
        
        protected abstract String getXMLTagName();
    }
    
    public static class JunitFailure extends JunitProblem{

        @Override
        protected String getXMLTagName() {
            return "failure";
        }
        
    }
    
    public static class JunitError extends JunitProblem{

        @Override
        protected String getXMLTagName() {
            return "error";
        }
        
    }


}
