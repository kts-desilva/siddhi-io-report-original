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
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        Map<String, Object> parameters = setParameters(reportProperties);
        DynamicLayoutManager reportLayout = layoutManager.getLayout(reportProperties);
//        reportBuilder.setTemplateFile("//home/senuri/Projects/Jasper/JasperTestPOC/siddhi-io-report/component/src" +
//                "/main/java/org/wso2/extension/siddhi/io/report/report/template/dynamicTemplate.jrxml");
        reportBuilder.setTemplateFile(reportProperties.get(ReportConstants.TEMPLATE));
        if (!reportProperties.get(ReportConstants.CHART).equals("table")) {
            addChartTo(reportProperties, reportBuilder, dataProvider, parameters);
        }
        DynamicReport report = reportBuilder.build();
        JasperPrint jasperPrint = generateReportPrint(report, reportLayout, dataSource, parameters);
        exportAsPdf(jasperPrint, reportProperties.get(ReportConstants.OUTPUT_PATH));

        //LOGGER.info("replacing string : " + matcher.replaceAll(dataProvider.getDynamicReportNameValue()));
        File f = new File(reportProperties.get(ReportConstants.OUTPUT_PATH));
        LOGGER.info("File exists : " + f.exists());
        LOGGER.info("Generated report " + reportProperties.get(ReportConstants.OUTPUT_PATH));
        LOGGER.info("File exists : " + f.exists());
    }

    private void addChartTo(Map<String, String> reportProperties, DynamicReportBuilder reportBuilder,
                            DynamicDataProvider dataProvider, Map<String, Object> parameters) {
        String chartTitle = reportProperties.get(ReportConstants.CHART_TITLE);
        ChartGenerator chartGenerator = new ChartGenerator();
        String categoryName = "";
        String seriesName = "";

        if (reportProperties.get(ReportConstants.CATEGORY) != null) {
            categoryName = reportProperties.get(ReportConstants.CATEGORY);
        }
        if (reportProperties.get(ReportConstants.SERIES) != null) {
            seriesName = reportProperties.get(ReportConstants.SERIES);
        }
        //no need of the default case since the chart types are validated in Siddhi app creation level.
        switch (reportProperties.get(ReportConstants.CHART).toLowerCase(Locale.ENGLISH)) {
            case "pie":
                DJChart pieChart;
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    pieChart = chartGenerator.createPieChart(dataProvider, chartTitle, categoryName, seriesName);
                } else {
                    pieChart = chartGenerator.createPieChart(dataProvider, chartTitle);
                }
                reportBuilder.addChart(pieChart);
                addTable(reportBuilder, dataProvider, chartGenerator, parameters);
                break;
            case "bar":
                DJChart barChart;
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    barChart = chartGenerator.createBarChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    barChart = chartGenerator.createBarChart(dataProvider, chartTitle);
                }
                reportBuilder.addChart(barChart);
                addTable(reportBuilder, dataProvider, chartGenerator, parameters);
                break;
            case "line":
                DJChart lineChart;
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    lineChart = chartGenerator.createLineChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    lineChart = chartGenerator.createLineChart(dataProvider, chartTitle);
                }
                reportBuilder.addChart(lineChart);
                addTable(reportBuilder, dataProvider, chartGenerator, parameters);
                break;
            default:
                chartGenerator.createTable(dataProvider, reportBuilder);
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
                .DEFAULT_DATASOURCE, DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION);
        parameters.put(ReportConstants.DEFAULT_DATASOURCE, dataProvider.getData());
    }
}
