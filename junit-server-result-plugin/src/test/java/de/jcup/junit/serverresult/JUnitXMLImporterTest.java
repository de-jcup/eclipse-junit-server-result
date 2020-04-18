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
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

import org.junit.Before;
import org.junit.Test;

public class JUnitXMLImporterTest {
	private JUnitXMLImporter importerToTest;

	@Before
	public void before() {
		importerToTest = new JUnitXMLImporter(new TestLogAdapter());
	}

	@Test
	public void example5_loaded_contains_error() throws IOException {
		/* prepare */
		File file = new File(TestResources.getTestResourcesFolder(), "junit-result-single-with-error-example5.xml");

		/* execute */
		JunitModel model = importerToTest.importXMLFileOrFolder(file, new TestProgressCallback());

		/* test */
		assertNotNull(model);
		SortedMap<String, JUnitTestSuite> testSuites = model.testSuites;
		assertEquals(1, testSuites.size());
		JUnitTestSuite testSuite = testSuites.values().iterator().next();
		assertTrue(testSuite.hasErrors());
	}

	@Test
	public void check_5_testsuite_single_files_are_imported_from_example4_folder() throws IOException {
		/* prepare */
		File file = new File(TestResources.getTestResourcesFolder(), "example4");

		/* execute */
		JunitModel model = importerToTest.importXMLFileOrFolder(file, new TestProgressCallback());

		/* test */
		assertNotNull(model);
		SortedMap<String, JUnitTestSuite> testSuites = model.testSuites;
		assertEquals(5, testSuites.size());

	}

	@Test
	public void resolveTestFilesFromDirectory_example3_has_2_files() {
		File file = new File(TestResources.getTestResourcesFolder(), "example3");

		/* execute */
		Set<File> files = importerToTest.resolveTestFilesFromDirectory(file);
		assertEquals(2, files.size());
	}

	@Test
	public void resolveTestFilesFromDirectory_example4_has_5_files() {
		File file = new File(TestResources.getTestResourcesFolder(), "example4");

		/* execute */
		Set<File> files = importerToTest.resolveTestFilesFromDirectory(file);
		assertEquals(5, files.size());
	}

	@Test
	public void check_2_testsuite_single_files_are_imported_from_example3_folder() throws IOException {
		/* prepare */
		File file = new File(TestResources.getTestResourcesFolder(), "example3");

		/* execute */
		JunitModel model = importerToTest.importXMLFileOrFolder(file);

		/* test */
		assertNotNull(model);
		SortedMap<String, JUnitTestSuite> testSuites = model.testSuites;
		assertEquals(2, testSuites.size());

	}

	@Test
	public void one_testsuite_is_imported_from_example1_file() throws IOException {
		/* prepare */
		File file = new File(TestResources.getTestResourcesFolder(), "junit-results-exported_example1.xml");

		/* execute */
		JunitModel model = importerToTest.importXMLFileOrFolder(file);

		/* test */
		assertNotNull(model);
		SortedMap<String, JUnitTestSuite> testSuites = model.testSuites;
		Iterator<JUnitTestSuite> x = testSuites.values().iterator();
		assertEquals(1, testSuites.size());
		JUnitTestSuite testSuite1 = x.next();
		assertEquals("1", testSuite1.failures);
		assertEquals("2", testSuite1.errors);
		assertTrue(testSuite1.hasErrors());

		assertEquals("suite1", testSuite1.name);

		assertTrue(testSuite1.systemOut.toString().equals("2020-04-07 11:04:39.719-Testdata-Systemout"));
		assertTrue(testSuite1.systemErr.toString().equals("2020-04-07 11:04:39.719-Testdata-Systemerr"));
	}

	@Test
	public void all_7_testsuites_are_imported_from_example2_file() throws Exception {
		/* prepare */
		File file = new File(TestResources.getTestResourcesFolder(), "junit-results-exported_example2.xml");

		/* execute */
		JunitModel model = importerToTest.importXMLFileOrFolder(file);

		/* test */
		assertNotNull(model);
		SortedMap<String, JUnitTestSuite> testSuites = model.testSuites;
		Iterator<JUnitTestSuite> x = testSuites.values().iterator();
		assertEquals(7, testSuites.size());
		JUnitTestSuite testSuite1 = x.next();
		JUnitTestSuite testSuite2 = x.next();
		JUnitTestSuite testSuite3 = x.next();
		JUnitTestSuite testSuite4 = x.next();
		JUnitTestSuite testSuite5 = x.next();
		JUnitTestSuite testSuite6 = x.next();
		JUnitTestSuite testSuite7 = x.next();

		assertEquals("x.y.z.adapter.nessus.NessusStateTest", testSuite1.name);
		assertEquals("x.y.z.domain.administration.user.UserAdministrationRestControllerMockTest", testSuite2.name);

		assertEquals("x.y.z.domain.notification.email.SMTPConfigStringToMapConverterTest", testSuite3.name);
		assertEquals("x.y.z.domain.notification.email.SMTPServerConfigurationTest", testSuite4.name);
		assertEquals("x.y.z.domain.notification.email.SimpleMailMessageSupportTest", testSuite5.name);
		assertEquals("x.y.z.sharedkernel.execution.SecHubExecutionExceptionTest", testSuite6.name);
		assertEquals("x.y.z.sharedkernel.util.StacktraceUtilTest", testSuite7.name);

		assertEquals("", testSuite1.systemOut.toString());
		assertTrue(testSuite2.systemOut.toString().startsWith("2020-04-07 11:04:39.719"));
		assertEquals("", testSuite3.systemOut.toString());
		assertEquals("", testSuite4.systemOut.toString());
		assertEquals("", testSuite5.systemOut.toString());
		assertEquals("", testSuite6.systemOut.toString());
		assertEquals("", testSuite7.systemOut.toString());

		assertEquals("2020-04-07T11:04:14", testSuite1.timeStamp);
		assertEquals("2020-04-07T11:04:40", testSuite2.timeStamp);
		assertEquals("2020-04-07T11:05:22", testSuite3.timeStamp);
		assertEquals("2020-04-07T11:05:21", testSuite4.timeStamp);

		assertEquals("0.0", testSuite1.timeInSeconds);
		assertEquals("0.062", testSuite2.timeInSeconds);

	}

}
