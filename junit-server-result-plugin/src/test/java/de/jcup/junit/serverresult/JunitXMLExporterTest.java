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

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import de.jcup.junit.serverresult.JUnitTestCase.JunitError;

public class JunitXMLExporterTest {

    private JunitXMLExporter exporterToTest;
    private JUnitXMLImporter importer;

    @Before
    public void before() { 
        exporterToTest = new JunitXMLExporter();
        importer=new JUnitXMLImporter(null);
    }
    
    @Test
    public void exported_part_can_be_imported_and_contains_same_content() throws Exception{

        /* prepare */
        JunitModel model = new JunitModel();
        JUnitTestSuite suite1 = new JUnitTestSuite();
        suite1.name="suite-name1";
        
        suite1.errors="1";
        suite1.failures="2";
        
        JUnitTestCase testCase = new JUnitTestCase();
        testCase.name="test1";
        testCase.className="x.y.z.Classname";
        testCase.time="0.024";

        testCase.error=new JunitError();
        testCase.error.message="message1";
        testCase.error.text="text1";
        testCase.error.type="type1";
        
        suite1.testCases.add(testCase);
        suite1.systemOut.append("out1");
        suite1.systemErr.append("err1");
        
        model.testSuites.put( suite1.name,suite1);
        
        
        File targetFile = Files.createTempFile("junit_console", ".xml").toFile();
        
        /* execute */
        exporterToTest.export(model, targetFile);
        
        /* test - we expect importer work... - imported model is as we have exported before */
        JunitModel model2 = importer.importXMLFileOrFolder(targetFile);
        assertEquals(1, model2.testSuites.size());
        JUnitTestSuite suite2 = model2.testSuites.values().iterator().next();
        assertEquals("suite-name1",suite2.name);
        assertEquals("1" ,suite2.errors);
        assertEquals("2" ,suite2.failures);
        assertTrue(suite2.hasErrors());
        
        assertEquals("out1" ,suite2.systemOut.toString());
        assertEquals("err1" ,suite2.systemErr.toString());
        JUnitTestCase testCase2 = suite2.testCases.iterator().next();
        
        assertEquals("test1", testCase2.name);
        assertEquals("x.y.z.Classname", testCase2.className);
        assertEquals("0.024", testCase2.time);

        assertNotNull(testCase2.error);
        assertEquals("message1", testCase2.error.message);
        assertEquals("text1", testCase2.error.text);
        assertEquals("type1", testCase2.error.type);
        
        assertNull(testCase2.failure);
        
    }

}
