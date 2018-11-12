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

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.report.util.DynamicDataProvider;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticReportGeneratorTestCase {

    private static final Logger LOGGER = Logger.getLogger(StaticReportGeneratorTestCase.class);
    private ClassLoader classLoader;

    @BeforeClass
    public void init() {
        classLoader = StaticReportGeneratorTestCase.class.getClassLoader();
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class)
    public void staticReportGeneratorTest1() {
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 1 - Generate reports with invalid template given.");
        LOGGER.info("--------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getInvalidTemplateReportProperties();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator();
        staticReportGenerator.generateReport(DummyData.DUMMY_PAYLOAD, reportProperties);
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to compile " +
            "the report. ")
    public void staticReportGeneratorTest2() {
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 2 - Generate reports with invalid template given.");
        LOGGER.info("--------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getIncorrectTemplateReportProperties();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator();
        staticReportGenerator.generateReport(DummyData.DUMMY_PAYLOAD, reportProperties);
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, expectedExceptionsMessageRegExp = "Datasets are " +
            "missing in the template provided(?s) .*")
    public void staticReportGeneratorTest3() {
        LOGGER.info("-------------------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 3 - Generate reports without datasets given in the template.");
        LOGGER.info("-------------------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getTemplateWithoutDatasetsReportProperties();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator();
        staticReportGenerator.generateReport(DummyData.DUMMY_PAYLOAD, reportProperties);
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to compile " +
            "the report. ")
    public void staticReportGeneratorTest4() {
        //test with invalid report element size in the external JRXML template.
        LOGGER.info("-----------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 4 - Generate reports with invalid filler properties.");
        LOGGER.info("-----------------------------------------------------------------------------------");

        String template = classLoader.getResource("incorrectFromResultsetData.jrxml").getFile();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator();
        JasperDesign jasperDesign = staticReportGenerator.loadTemplate(template);
        JasperReport jasperReport = staticReportGenerator.compileTemplate(jasperDesign);
        Map<String, String> reportProperties = DummyData.getWithoutParametersReportProperties();
        DynamicDataProvider dynamicDataProvider = new DynamicDataProvider(reportProperties);
        List<Map<String, Object>> dataFromPayload = staticReportGenerator.getDataFromPayload(dynamicDataProvider,
                DummyData.DUMMY_PAYLOAD);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("TableDataSource", new JRMapArrayDataSource(dataFromPayload.toArray()));
        staticReportGenerator.fillReportData(jasperReport, parameters, new JREmptyDataSource());
    }

//    @Test(expectedExceptions = SiddhiAppRuntimeException.class)
//    public void staticReportGeneratorTest5() {
//        //test with invalid report element size in the external JRXML template.
//        LOGGER.info("-----------------------------------------------------------------------------------");
//        LOGGER.info("StaticReportGenerator TestCase 5 - Generate reports with invalid export properties.");
//        LOGGER.info("-----------------------------------------------------------------------------------");
//
//        String template = DummyData.class.getClassLoader().getResource("fromResultsetData.jrxml").getFile();
//        StaticReportGenerator staticReportGenerator = new StaticReportGenerator();
//        JasperDesign jasperDesign = staticReportGenerator.loadTemplate(template);
//        JasperReport jasperReport = staticReportGenerator.compileTemplate(jasperDesign);
//
//        Map<String, String> reportProperties = DummyData.getWithoutParametersReportProperties();
//
//        DynamicDataProvider dynamicDataProvider = new DynamicDataProvider(reportProperties);
//        List<Map<String, Object>> dataFromPayload = staticReportGenerator.getDataFromPayload(dynamicDataProvider,
//                DummyData.DUMMY_PAYLOAD);
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("TableDataSource", new JRMapArrayDataSource(dataFromPayload.toArray()));
//        JasperPrint jasperPrint = staticReportGenerator.fillReportData(jasperReport, parameters, new
//                JREmptyDataSource());
//        jasperPrint.setPageHeight(800);
//        staticReportGenerator.exportAsPdf(jasperPrint, "test.pdf");
//    }
}
