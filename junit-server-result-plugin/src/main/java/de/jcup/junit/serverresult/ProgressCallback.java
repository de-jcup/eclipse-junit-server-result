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

public interface ProgressCallback {

    public static final ProgressCallback NULL_PROGRESS = new ProgressCallback() {

        @Override
        public void beginTask(String name, int totalWork) {
            
        }

        @Override
        public void worked(int worked) {
            
        }
        
        @Override
        public boolean isCanceledRequested() {
            return false;
        }
        
        @Override
        public void cancelDone() {
            
        }

        @Override
        public void subTask(String string) {
            
        }
        
    };

    public void beginTask(String string, int nodeCount);

    public void worked(int i);

    public boolean isCanceledRequested();
    
    /**
     * Informs that cancel was recognized by callback and all operations have been canceled
     */
    public void cancelDone();

    public void subTask(String string);
    
    

}
