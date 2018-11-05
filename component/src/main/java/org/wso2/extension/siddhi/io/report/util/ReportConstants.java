/*
 * Copyright (C) 2018 WSO2 Inc. (http://wso2.com) All Rights Reserved.

 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wso2.extension.siddhi.io.report.util;

/**
 * Constants used in siddhi-io-report extension.
 */

public class ReportConstants {
    private ReportConstants() {
    }

    /* configuration parameters*/
    public static final String TEMPLATE = "template";
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";
    public static final String CHART = "chart";
    public static final String SERIES = "series";
    public static final String CATEGORY = "category";
    public static final String DATASOURCE = "datasource";
    public static final String TITLE = "title";
    public static final String CHART_TITLE = "chart.title";
    public static final String SUBTITLE = "subtitle";
    public static final String DESCRIPTION = "description";
    public static final String URI = "report.uri";
    public static final String REPORT_NAME = "report.name";
    public static final String REPORT_DYNAMIC_VALUE = "report.dynamic.value";

    /* default values of configuration parameters*/
    public static final String DEFAULT_TEMPLATE = "dynamicTemplate.jrxml";
    public static final String DEFAULT_CHART = "table";
    public static final String DEFAULT_TITLE = "Siddhi Report";
    public static final String DEFAULT_REPORT_NAME = "SiddhiReport";
    public static final String DEFAULT_DATASOURCE = "tableData";

    public static final int COLUMN_WIDTH = 400;

    public static final String EMPTY_STRING = "";

    /**
     * Valid chart types
     */
    public enum ChartTypes {
        TABLE,
        LINE,
        BAR,
        PIE,
    }
}
