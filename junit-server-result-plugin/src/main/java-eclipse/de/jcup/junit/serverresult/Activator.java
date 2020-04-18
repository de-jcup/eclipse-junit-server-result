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

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.junit.serverresult.editor.JUnitServerResultLogFileEditor;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IWorkbenchListener, LogAdapter {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.jcup.junit.serverresult"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ColorManager colorManager;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		colorManager=new ColorManager();
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.addWorkbenchListener(this);
		
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public ColorManager getColorManager() {
        return colorManager;
    }
	
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

    public void logError(String message, Exception e) {
        getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID,  message,e));
    }

    @Override
    public boolean preShutdown(IWorkbench workbench, boolean forced) {
        JUnitServerResultLogFileEditor editor = JUnitServerResultUtil.findExistingLogFileEditorOrNull();
        if (editor!=null) {
            EclipseUtil.getActivePage().closeEditor(editor, false);
        }
        return true;
    }

    @Override
    public void postShutdown(IWorkbench workbench) {
        
    }
}
