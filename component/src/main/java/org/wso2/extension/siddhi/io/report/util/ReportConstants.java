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

import java.awt.Color;

/**
 * Constants used in siddhi-io-report extension.
 */
public class ReportConstants {
    /* configuration parameters*/
    public static final String TEMPLATE = "template";
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";
    public static final String CHART = "chart";
    public static final String SERIES = "series";
    public static final String CATEGORY = "category";
    public static final String DATASET = "dataset";
    public static final String TITLE = "title";
    public static final String PIE_CHART = "pie";
    public static final String BAR_CHART = "bar";
    public static final String LINE_CHART = "line";
    public static final String CHART_TITLE = "chart.title";
    public static final String SUBTITLE = "subtitle";
    public static final String DESCRIPTION = "description";
    public static final String OUTPUT_PATH = "outputpath";
    public static final String HEADER_IMAGE = "headerImage";
    public static final String REPORT_NAME = "report.name";
    public static final String REPORT_DYNAMIC_NAME_VALUE = "report.dynamic.name.value";
    public static final String REPORT_DYNAMIC_DATASET_VALUE = "report.dynamic.dataset.value";

    /* default values of configuration parameters*/
    public static final String DEFAULT_TEMPLATE = "dynamicTemplate.jrxml";
    public static final String DEFAULT_CHART = "table";
    public static final String DEFAULT_TITLE = "Siddhi Report";
    public static final String DEFAULT_REPORT_NAME = "SiddhiReport";
    public static final String DEFAULT_DATASET = "tableData";

    public static final int COLUMN_WIDTH = 400;
    public static final String GREY_BACKGROUND = "#616161";
    public static final Color WHITE_BACKGROUND = new Color(255, 255, 255);
    public static final Color GREY_BORDER = new Color(196, 186, 186);
    public static final Color TABLE_ODD_BACKGROUND = new Color(243, 242, 242);
    public static final int BORDER_WIDTH = 2;
    public static final int HORIZONTAL_PADDING = 20;
    public static final int VERTICAL_PADDING = 5;


    public static final String EMPTY_STRING = "";
    public static final String PDF_EXTENSION = ".pdf";

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
