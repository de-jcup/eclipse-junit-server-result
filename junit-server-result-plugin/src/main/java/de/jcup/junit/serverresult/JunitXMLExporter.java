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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.text.StringEscapeUtils;

import de.jcup.junit.serverresult.JUnitTestCase.JunitProblem;

public class JunitXMLExporter {

    public void export(JunitModel model, File targetFile) throws IOException {
        Path path = ensureTargetFile(targetFile);

        String xml = createXML(model);
        writeXML(path, xml);
    }

    public void export(JUnitTestSuite testSuite, File targetFile) throws IOException {
        Path path = ensureTargetFile(targetFile);

        String xml = createXML(testSuite);
        writeXML(path, xml);
    }

    private void writeXML(Path path, String xml) throws IOException {
        if (DEBUG) {
            System.out.println("file:" + path.toAbsolutePath().toString());
            System.out.println("xml:");
            System.out.println(xml);
        }

        try (BufferedWriter bw = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
            bw.write(xml);
        }
    }

    private Path ensureTargetFile(File targetFile) throws IOException {
        if (targetFile == null) {
            throw new IllegalStateException("no target file defined");
        }
        Path path = targetFile.toPath();
        Files.deleteIfExists(path);

        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();
        return path;
    }

    private String createXML(JUnitTestSuite oneSuiteOnly) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        sb.append("<testsuites>");
        createXMLPartForSuite(sb, oneSuiteOnly);
        sb.append("</testsuites>");
        return sb.toString();
    }

    private String createXML(JunitModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        sb.append("<testsuites>");
        for (JUnitTestSuite testSuite : model.testSuites.values()) {
            createXMLPartForSuite(sb, testSuite);
        }
        sb.append("</testsuites>");
        return sb.toString();
    }

    private void createXMLPartForSuite(StringBuilder sb, JUnitTestSuite testSuite) {
        sb.append("<testsuite name=\"");
        sb.append(testSuite.name);
        sb.append("\" tests=\"");
        sb.append(testSuite.testCases.size());
        sb.append("\" skipped=\"");
        sb.append(testSuite.skipped);
        sb.append("\" timestamp=\"");
        sb.append(testSuite.timeStamp);
        sb.append("\" time=\"");
        sb.append(testSuite.timeInSeconds);
        sb.append("\" failures=\"");
        sb.append(testSuite.failures);
        sb.append("\" errors=\"");
        sb.append(testSuite.errors);
        sb.append("\">");
        for (JUnitTestCase testCase : testSuite.testCases) {
            sb.append("<testcase name=\"");
            sb.append(testCase.name);
            sb.append("\" classname=\"");
            sb.append(testCase.className);
            sb.append("\" time=\"");
            sb.append(testCase.time);
            sb.append("\">");
            
            if (testCase.skipped) {
                sb.append("<skipped/>");
            }else {
                sb.append(createProblemXML(testCase.failure));
                sb.append(createProblemXML(testCase.error));
            }

            sb.append("</testcase>");
        }
        sb.append("<system-out><![CDATA[" + StringEscapeUtils.escapeXml10(testSuite.systemOut.toString()) + "]]></system-out>");
        sb.append("<system-err><![CDATA[" + StringEscapeUtils.escapeXml10(testSuite.systemErr.toString()) + "]]></system-err>");
        sb.append("</testsuite>");
    }

    private String createProblemXML(JunitProblem problem) {
        if (problem == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(problem.getXMLTagName());
        sb.append(" message=\"");
        sb.append(StringEscapeUtils.escapeXml10(problem.message));
        sb.append("\" type=\"");
        sb.append(problem.type);
        sb.append("\" >");
        sb.append(StringEscapeUtils.escapeXml10(problem.text));
        sb.append("</");
        sb.append(problem.getXMLTagName());
        sb.append(">");

        return sb.toString();
    }
}
