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
package de.jcup.junit.serverresult.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class StringEditorInput implements IStorageEditorInput {

    private String content;
    private StringBuilder name = new StringBuilder();

    public StringEditorInput(Path logfilePahth, String content) {
        name.append("truncated-");
        name.append(logfilePahth.toFile().getName());
        
        this.content = content;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public IStorage getStorage() throws CoreException {
        return new StringStorage();
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return name.toString();
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return null;
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        return null;
    }

    public class StringStorage implements IStorage {
        @Override
        public InputStream getContents() throws CoreException {
            return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public <T> T getAdapter(Class<T> adapter) {
            return null;
        }

        @Override
        public IPath getFullPath() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }
    }
}