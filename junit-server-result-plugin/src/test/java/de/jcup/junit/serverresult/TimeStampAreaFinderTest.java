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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class TimeStampAreaFinderTest {

    private TimeStampAreaFinder finderToTest;

    @Before
    public void before() {
        finderToTest = new TimeStampAreaFinder();
    }

    @Test
    public void find_by_estimation() throws Exception {
        /* prepare */
        String text = "\n" + "  .   ____          _            __ _ _\n" + " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\\n" + "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\\n"
                + " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )\n" + "  '  |____| .__|_| |_|_| |_\\__, | / / / /\n" + " =========|_|==============|___/=/_/_/_/\n"
                + " :: Spring Boot ::        (v2.2.0.RELEASE)\n" + "\n";
        int found1 = text.length();
        text += "2020-03-18 21:12:39.858  INFO 9933 --- [           main] c.d.sechub.SecHubServerApplication       : Starting SecHubServerApplication on fv-az59 with PID 9933 (/home/runner/work/sechub/sechub/sechub-server/build/libs/sechub-server-0.0.0.jar started by runner in /home/runner/work/sechub/sechub/sechub-integrationtest)\n"
                + "2020-03-18 21:12:39.861 DEBUG 9933 --- [           main] c.d.sechub.SecHubServerApplication       : Running with Spring Boot v2.2.0.RELEASE, Spring v5.2.0.RELEASE\n"
                + "2020-03-18 21:12:39.861  INFO 9933 --- [           main] c.d.sechub.SecHubServerApplication       : The following profiles are active: integrationtest,localserver,initial_admin_static,mocked_notifications,admin_access,mocked_products,h2\n";
        int found2 = text.length();
        text += "2020-03-18 21:12:42.925  INFO 9933 --- [           main] d.s.s.c.SecHubTomcatServletWebserFactory : Set max swallow size to 6291456\n"
                + "2020-03-18 21:12:42.990  INFO 9933 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]\n"
                + "2020-03-18 21:12:42.991  INFO 9933 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.27]";
        TimeStampArea wantedArea = new TimeStampArea();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        wantedArea.beginTimeStamp = format.parse("2020-03-18 21:12:39.311");
        wantedArea.endTimeStamp = format.parse("2020-03-18 21:12:42.980");

        /* execute */
        LogFileLocation result = finderToTest.findLogFileLocationInArea(text, format, wantedArea,null);

        /* test */
        assertEquals(found1, result.begin);
        assertEquals(found2, result.end);
    }
    
    @Test
    public void when_not_found_a_not_found_text_() throws Exception {
        /* prepare */
        String text = "not containing";
        TimeStampArea wantedArea = new TimeStampArea();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        wantedArea.beginTimeStamp = format.parse("2020-03-18 21:12:39.311");
        wantedArea.endTimeStamp = format.parse("2020-03-18 21:12:42.980");

        /* execute */
        LogFileLocation result = finderToTest.findLogFileLocationInArea(text, format, wantedArea, null);

        /* test */
        assertEquals(-1, result.begin);
        assertEquals(-1, result.end);
    }

    @Test
    public void a_testsuite_with_timestamp_and_time() throws Exception {
        /* prepare */
        JUnitTestSuite suite1 = new JUnitTestSuite();
        suite1.timeStamp = "2020-04-07T11:05:21";
        suite1.timeInSeconds="0.123";


        /* execute */
        TimeStampArea area = finderToTest.findArea(suite1);

        /* test */
        assertNotNull(area.endTimeStamp);
        
        SimpleDateFormat formatWithMillis = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss:SSS");
        assertEquals("2020-04-07T11:05:21:000", formatWithMillis.format(area.beginTimeStamp));
        assertEquals("2020-04-07T11:05:21:123", formatWithMillis.format(area.endTimeStamp));

    }
    
    @Test
    public void a_testsuite_with_timestamp_and_time_and_812_additional_milliseconds_set() throws Exception {
        /* prepare */
        JUnitTestSuite suite1 = new JUnitTestSuite();
        suite1.timeStamp = "2020-04-07T11:05:21";
        suite1.timeInSeconds="0.123";

        finderToTest.setAdditionalMilliseconds(812);
        
        /* execute */
        TimeStampArea area = finderToTest.findArea(suite1);

        /* test */
        assertNotNull(area.endTimeStamp);
        
        SimpleDateFormat formatWithMillis = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss:SSS");
        assertEquals("2020-04-07T11:05:21:000", formatWithMillis.format(area.beginTimeStamp));
        assertEquals("2020-04-07T11:05:21:935", formatWithMillis.format(area.endTimeStamp));

    }

    @Test
    public void a_testsuite_with_timestamp_only() throws Exception {
        /* prepare */
        JUnitTestSuite suite1 = new JUnitTestSuite();
        suite1.timeStamp = "2020-04-07T11:05:21";

        /* execute */
        TimeStampArea area = finderToTest.findArea(suite1);

        /* test */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Date timestamp1 = format.parse(suite1.timeStamp);

        assertEquals(timestamp1, area.beginTimeStamp);
        assertEquals(null, area.endTimeStamp);

    }

    @Test
    public void a_testsuite_without_timstamp_contans_null_for_begin_and_end() throws Exception {
        /* prepare */
        JUnitTestSuite suite1 = new JUnitTestSuite();
        suite1.timeStamp = null;

        /* execute */
        TimeStampArea area = finderToTest.findArea(suite1);

        /* test */
        assertEquals(null, area.beginTimeStamp);
        assertEquals(null, area.endTimeStamp);

    }
}
