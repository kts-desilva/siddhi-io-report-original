/*
 *  Copyright (C) 2018 WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wso2.extension.siddhi.io.report.generators;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.util.Map;

public class DynamicReportGeneratorTestCase {

    private static final Logger LOGGER = Logger.getLogger(DynamicReportGeneratorTestCase.class);
    private ClassLoader classLoader;

    @BeforeClass
    public void init() {
        classLoader = DynamicReportGeneratorTestCase.class.getClassLoader();
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to generate" +
            " the report. Provide a numeric series column. ")
    public void staticReportGeneratorTest1() {
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("DynamicReportGenerator TestCase 1 - Generate reports with invalid template given.");
        LOGGER.info("--------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getDyanmicReportParameters();
        DynamicReportGenerator dynamicReportGenerator = new DynamicReportGenerator();
        dynamicReportGenerator.generateReport(DummyData.STRING_DUMMY_DATA, reportProperties);
    }

}
