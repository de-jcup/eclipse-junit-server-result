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

import static de.jcup.junit.serverresult.DeveloperConstants.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import de.jcup.junit.serverresult.JUnitTestCase.JunitError;
import de.jcup.junit.serverresult.JUnitTestCase.JunitFailure;
import de.jcup.junit.serverresult.JUnitTestCase.JunitProblem;

/**
 * Importx Junit XML files - format details see
 * http://windyroad.com.au/dl/Open%20Source/JUnit.xsd
 * 
 * @author albert
 *
 */
public class JUnitXMLImporter {

    private LogAdapter logAdapter;

    public JUnitXMLImporter(LogAdapter logAdapter) {
        if (logAdapter == null) {
            logAdapter = LogAdapter.SYSERR_LOGADAPTER_FALLBACK;
        }
        this.logAdapter = logAdapter;
    }

    /**
     * Imports either one single testsuite xml file, a combined testsuites xml file
     * or when file is a folder scans for all test files
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public JunitModel importXMLFileOrFolder(File file) throws IOException {
        return importXMLFileOrFolder(file, null);
    }

    public JunitModel importXMLFileOrFolder(File file, ProgressCallback callback) throws IOException {
        if (callback == null) {
            callback = ProgressCallback.NULL_PROGRESS;
        }
        SAXReader reader = new SAXReader();
        JunitModel model = new JunitModel();

        if (file.isDirectory()) {
            StringBuilder failures = new StringBuilder();
            Exception lastException = null;

            callback.beginTask("resolve test files from directory:" + file.getAbsolutePath(), 1);
            Set<File> resolveTestFilesFromDirectory = resolveTestFilesFromDirectory(file);

            callback.beginTask("import resolved " + resolveTestFilesFromDirectory.size() + " test files", resolveTestFilesFromDirectory.size());

            int worked = 0;
            for (File testFile : resolveTestFilesFromDirectory) {
                try {
                    if (callback.isCanceledRequested()) {
                        callback.cancelDone();
                        return model;
                    }
                    JunitModel loadedModel = internalImportXMLFiles(testFile, callback, reader, false);
                    for (JUnitTestSuite suite : loadedModel.testSuites.values()) {
                        JUnitTestSuite existing = model.testSuites.get(suite.name);
                        if (existing!=null) {
                            model.duplicateImports.
                                append("\n>> IGNORE:").append(suite).
                                append("\n>> KEEP  :").append(existing).
                                append('\n');
                        } else {
                            model.testSuites.put(suite.name, suite);
                        }
                    }
                    callback.worked(++worked);
                } catch (Exception e) {
                    lastException = e;
                    failures.append("\nNot able to import file:" + testFile.getAbsolutePath());

                }
            }
            if (lastException != null) {
                /*
                 * after all we log problems - reduces flickering at error view when log adapter
                 * is for eclipse...
                 */
                logAdapter.logError("Importing folder " + file.getAbsolutePath() + " failed" + failures.toString(), lastException);
            }
            return model;
        } else {
            /* just one single file to import */
            return internalImportXMLFiles(file, callback, reader, true);
        }

    }

    private JunitModel internalImportXMLFiles(File file, ProgressCallback callback, SAXReader reader, boolean beginTask) throws IOException {
        JunitModel model = new JunitModel();
        if (file == null) {
            return model;
        }
        Document document;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            throw new IOException("Was not able to read junit xml file", e);
        }
        if (callback.isCanceledRequested()) {
            callback.cancelDone();
            return model;
        }
        Element testSuitesRootElement = document.getRootElement();
        String rootElementName = testSuitesRootElement.getName();
        if (rootElementName.contentEquals("testsuites")) {
            int nodeCount = testSuitesRootElement.nodeCount();
            if (beginTask) {
                callback.beginTask("import " + nodeCount + " testsuites(s) from " + file.getAbsolutePath(), nodeCount);
            } else {
                callback.subTask("import " + nodeCount + " testsuites(s) from " + file.getAbsolutePath());
            }
            int worked = 0;
            for (int i = 0, size = nodeCount; i < size; i++) {
                Node testsuite = testSuitesRootElement.node(i);
                if (beginTask) {
                    callback.worked(worked++);
                }
                handleTestSuiteNode(testsuite, model,file);
            }

            if (DEBUG) {
                System.out.println("ended import");
            }
            return model;
        } else if (rootElementName.contentEquals("testsuite")) {
            if (beginTask) {
                callback.beginTask("import testsuite from " + file.getAbsolutePath(), 1);
            } else {
                callback.subTask("import testsuite from " + file.getAbsolutePath());
            }
            handleTestSuiteNode(testSuitesRootElement, model,file);
            return model;
        }
        throw new IOException("Not able to import xml files, because root element testsuites not found!");
    }

    private void handleTestSuiteNode(Node testsuiteNode, JunitModel model, File file) {
        if (!(testsuiteNode instanceof Element)) {
            return;
        }
        String name = testsuiteNode.getName();
        if (!name.contentEquals("testsuite")) {
            return;
        }

        addTestSuite(testsuiteNode, model,file);

    }

    private JUnitTestSuite addTestSuite(Node testsuite, JunitModel model, File file) {
        Element testSuiteElement = (Element) testsuite;
        JUnitTestSuite junitTestSuite = new JUnitTestSuite();
        junitTestSuite.location=file.getAbsolutePath();
        junitTestSuite.name = safeGetAttribute(testSuiteElement, "name");
        junitTestSuite.skipped = safeGetAttribute(testSuiteElement, "skipped");
        junitTestSuite.failures = safeGetAttribute(testSuiteElement, "failures");
        junitTestSuite.errors = safeGetAttribute(testSuiteElement, "errors");
        junitTestSuite.timeStamp = safeGetAttribute(testSuiteElement, "timestamp");
        junitTestSuite.timeInSeconds = safeGetAttribute(testSuiteElement, "time");

        model.testSuites.put(junitTestSuite.name, junitTestSuite);

        List<Node> testSuiteChildren = testSuiteElement.content();

        addTestSuiteChildren(testSuiteChildren, junitTestSuite);

        if (DEBUG) {
            System.out.println("imported testsuite : " + junitTestSuite);
        }
        return junitTestSuite;
    }

    private String safeGetAttribute(Element element, String attributeName) {
        Attribute attrib = element.attribute(attributeName);
        if (attrib == null) {
            return null;
        }
        return attrib.getValue();
    }

    private void addTestSuiteChildren(List<Node> testSuiteContent, JUnitTestSuite junitTestSuite) {

        for (Node testSuiteChild : testSuiteContent) {
            if (!(testSuiteChild instanceof Element)) {
                continue;
            }
            Element element = (Element) testSuiteChild;
            String name = element.getName();
            switch (name) {
            case "testcase":
                JUnitTestCase junitTestCase = new JUnitTestCase();
                Attribute nameAttribute = element.attribute("name");
                if (nameAttribute != null) {
                    junitTestCase.name = nameAttribute.getValue();
                }
                Attribute classNameAttribute = element.attribute("classname");
                if (classNameAttribute != null) {
                    junitTestCase.className = classNameAttribute.getValue();
                }
                Attribute timeAttribute = element.attribute("time");
                if (timeAttribute != null) {
                    junitTestCase.time = timeAttribute.getValue();
                }
                junitTestSuite.testCases.add(junitTestCase);
                for (Node testCaseChild : element.content()) {
                    handleTestCaseChild(testCaseChild, junitTestCase);
                }
                break;
            case "system-out":
                String out = element.getText();
                junitTestSuite.systemOut.append(StringEscapeUtils.unescapeXml(out));
                break;
            case "system-err":
                String err = element.getText();
                junitTestSuite.systemErr.append(StringEscapeUtils.unescapeXml(err));
                break;
            default:

            }

        }

    }

    private void handleTestCaseChild(Node testCaseChild, JUnitTestCase junitTestCase) {

        if (!(testCaseChild instanceof Element)) {
            return;
        }
        Element child = (Element) testCaseChild;
        switch (child.getName()) {
        case "failure":
            junitTestCase.failure = new JunitFailure();
            buildProblem(junitTestCase.failure, child);
            break;
        case "error":
            junitTestCase.error = new JunitError();
            buildProblem(junitTestCase.error, child);
            break;
        case "skipped":
            junitTestCase.skipped = true;
            break;
        }

    }

    private void buildProblem(JunitProblem problem, Element child) {
        Attribute messageAttr = child.attribute("message");
        if (messageAttr != null) {
            problem.message = StringEscapeUtils.unescapeXml(messageAttr.getValue());
        }
        Attribute typeAttribute = child.attribute("type");
        if (typeAttribute != null) {
            problem.type = typeAttribute.getValue();
        }
        problem.text = StringEscapeUtils.unescapeXml(child.getText());
    }

    protected Set<File> resolveTestFilesFromDirectory(File file) {
        Set<File> files = new HashSet<>();
        if (!file.isDirectory()) {
            return files;
        }
        scan(file, files);

        return files;
    }

    private void scan(File folder, Set<File> files) {
        File[] folderFiles = folder.listFiles(JUNIT_TESTFILE_FILTER);

        for (File folderFile : folderFiles) {
            if (folderFile.isDirectory()) {
                scan(folderFile, files);
            } else {
                files.add(folderFile);
            }
        }
    }

    private static JunitTestFileFilter JUNIT_TESTFILE_FILTER = new JunitTestFileFilter();

    private static class JunitTestFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            String name = file.getName();
            if (name == null) {
                return false;
            }
            if (file.isDirectory()) {
                /* optimize performance */
                if (name.equals("src")) { // we ignore all content in src
                    return false;
                }
                if (name.equals(".settings")) { // we ignore settings for
                                                // eclipse
                    return false;
                }
                if (name.equals(".gradle")) { // we ignore gradle cache folder
                    return false;
                }
                if (name.equals(".git")) { // we ignore git data folder
                    return false;
                }
                if (name.equals("bin")) { // we ignore eclipse bin output folder
                    return false;
                }
                if (name.equals("classes")) { // we ignore gradle bin output
                                              // folder
                    return false;
                }
                if (name.equals("binary")) { // we ignore gradle binary parts
                                             // inside test output folder
                    return false;
                }
                return true;
            }
            /* file found */
            if (!name.startsWith("TEST-")) {
                return false;
            }
            if (!name.endsWith(".xml")) {
                return false;
            }
            return true;
        }

    }
}
