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
package de.jcup.junit.serverresult.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.junit.serverresult.ProgressCallback;

public class ProgressMonitorCallbackAdapter implements ProgressCallback{
    private IProgressMonitor monitor;
    private Runnable cancelDoneRunnable;
    
    public ProgressMonitorCallbackAdapter(IProgressMonitor monitor) {
        this(monitor,null);
    }
    
    public ProgressMonitorCallbackAdapter(IProgressMonitor monitor, Runnable cancelDonerunnable) {
        if (monitor==null) {
            monitor = new NullProgressMonitor();
        }
        this.monitor=monitor;
        this.cancelDoneRunnable=cancelDonerunnable;
    }
    
    @Override
    public void beginTask(String name, int totalWork) {
       monitor.beginTask(name, totalWork);
        
    }

    @Override
    public void worked(int i) {
        monitor.worked(i);
    }

    @Override
    public boolean isCanceledRequested() {
        return monitor.isCanceled();
    }
 
    @Override
    public void cancelDone() {
        if (cancelDoneRunnable!=null) {
            cancelDoneRunnable.run();
        }
        
    }

    @Override
    public void subTask(String name) {
        monitor.subTask(name);
    }

}
