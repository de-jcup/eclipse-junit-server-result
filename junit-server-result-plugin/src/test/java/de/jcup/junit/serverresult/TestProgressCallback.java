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

public class TestProgressCallback implements ProgressCallback {

    @Override
    public void worked(int i) {
        System.out.println("worked:" + i);
    }

    @Override
    public boolean isCanceledRequested() {
        return false;
    }

    @Override
    public void cancelDone() {

    }

    @Override
    public void beginTask(String string, int amount) {
        System.out.println("begin task:" + string + " [" + amount+"]");

    }

    @Override
    public void subTask(String string) {
        System.out.println("sub task:" + string);
        
    }
}
