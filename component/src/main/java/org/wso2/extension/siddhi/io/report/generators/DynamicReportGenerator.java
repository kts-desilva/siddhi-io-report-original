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

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.util.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.DynamicLayoutManager;
import org.wso2.extension.siddhi.io.report.util.LayoutManager;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is the implementation of the report generation logic.
 */
public class DynamicReportGenerator extends ReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(DynamicReportGenerator.class);
    private DynamicReportBuilder reportBuilder;
    private LayoutManager layoutManager;

    public DynamicReportGenerator() {
        reportBuilder = new DynamicReportBuilder();
        layoutManager = new LayoutManager();
    }

    @Override
    public void generateReport(Object payload, Map<String, String> reportProperties) {
        LOGGER.info(reportProperties);
        DynamicDataProvider dataProvider = new DynamicDataProvider(reportProperties);
        List<Map<String, Object>> data = dataProvider.getData(payload, reportBuilder);
        LOGGER.info(data);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        Map<String, Object> parameters = setParameters(reportProperties);
        DynamicLayoutManager reportLayout = layoutManager.getLayout(reportProperties);
//        reportBuilder.setTemplateFile("//home/senuri/Projects/Jasper/JasperTestPOC/siddhi-io-report/component/src" +
//                "/main/java/org/wso2/extension/siddhi/io/report/report/template/dynamicTemplate.jrxml");
        reportBuilder.setTemplateFile(reportProperties.get(ReportConstants.TEMPLATE));
        addChartTo(reportProperties, reportBuilder, dataProvider, parameters);
        DynamicReport report = reportBuilder.build();
        JasperPrint jasperPrint = generateReportPrint(report, reportLayout, dataSource, parameters);
        exportAsPdf(jasperPrint, reportProperties.get(ReportConstants.OUTPUT_PATH));

        File f = new File(reportProperties.get(ReportConstants.OUTPUT_PATH));
        LOGGER.info("File exists : " + f.exists());
        LOGGER.info("Generated report " + reportProperties.get(ReportConstants.OUTPUT_PATH));
        LOGGER.info("File exists : " + f.exists());
    }

    private void addChartTo(Map<String, String> reportProperties, DynamicReportBuilder reportBuilder,
                            DynamicDataProvider dataProvider, Map<String, Object> parameters) {
        ChartGenerator chartGenerator = new ChartGenerator();
        String chartTitle = reportProperties.getOrDefault(ReportConstants.CHART_TITLE, "");
        String categoryName = reportProperties.getOrDefault(ReportConstants.CATEGORY, "");
        String seriesName = reportProperties.getOrDefault(ReportConstants.SERIES, "");
        DJChart chart = null;

        //no need of the default case since the chart types are validated in Siddhi app creation level.
        switch (reportProperties.get(ReportConstants.CHART).toLowerCase(Locale.ENGLISH)) {
            case "pie":
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    chart = chartGenerator.createPieChart(dataProvider, chartTitle, categoryName, seriesName);
                } else {
                    chart = chartGenerator.createPieChart(dataProvider, chartTitle);
                }
                break;
            case "bar":
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    chart = chartGenerator.createBarChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    chart = chartGenerator.createBarChart(dataProvider, chartTitle);
                }
                break;
            case "line":
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    chart = chartGenerator.createLineChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    chart = chartGenerator.createLineChart(dataProvider, chartTitle);
                }
                break;
            default:
                chartGenerator.createTable(dataProvider, reportBuilder);
                break;
        }

        if(!reportProperties.get(ReportConstants.CHART).toLowerCase(Locale.ENGLISH).equals(ReportConstants
                .DEFAULT_CHART)){
            reportBuilder.addChart(chart);
            addTable(reportBuilder, dataProvider, chartGenerator, parameters);
        }
    }

    private Map<String, Object> setParameters(Map<String, String> reportProperties) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ReportConstants.TITLE, reportProperties.get(ReportConstants.TITLE));
        parameters.put(ReportConstants.SUBTITLE, reportProperties.get(ReportConstants.SUBTITLE));
        parameters.put(ReportConstants.DESCRIPTION, reportProperties.get(ReportConstants.DESCRIPTION));
        if ((reportProperties.get(ReportConstants.HEADER) != null)) {
            parameters.put("headerImage", reportProperties.get(ReportConstants.HEADER));
        }
        return parameters;
    }

    private void addTable(DynamicReportBuilder reportBuilder, DynamicDataProvider dataProvider, ChartGenerator
            chartGenerator, Map<String, Object> parameters) {
        DynamicReportBuilder nextReportBuilder = new DynamicReportBuilder();
        nextReportBuilder = chartGenerator.createTable(dataProvider, nextReportBuilder);

        DynamicReport nextReport = nextReportBuilder.build();
        reportBuilder.addConcatenatedReport(nextReport, new ClassicLayoutManager(), ReportConstants
                .DEFAULT_DATASET, DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION);
        parameters.put(ReportConstants.DEFAULT_DATASET, dataProvider.getData());
    }
}
