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

import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import ar.com.fdvs.dj.domain.chart.DJChartOptions;
import ar.com.fdvs.dj.domain.chart.builder.DJBarChartBuilder;
import ar.com.fdvs.dj.domain.chart.builder.DJLineChartBuilder;
import ar.com.fdvs.dj.domain.chart.builder.DJPieChartBuilder;
import ar.com.fdvs.dj.domain.chart.plot.DJAxisFormat;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import org.wso2.extension.siddhi.io.report.util.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.DynamicStyleProvider;

import java.awt.Color;
import java.util.List;

/**
 * This is the implementation of the chart generation for the report chart types.
 */
public class ChartGenerator {
    DJChart createPieChart(DynamicDataProvider dataProvider, String chartTitle) {
        return new DJPieChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setKey((PropertyColumn) dataProvider.getCategoryColumn())
                .addSerie(dataProvider.getSeriesColumn())
                .setCircular(true)
                .setLabelFormat("{0}{2}")
                .build();
    }

    DJChart createPieChart(DynamicDataProvider dataProvider, String chartTitle, String category, String
            series) {
        return new DJPieChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setKey((PropertyColumn) dataProvider.getCategoryColumn(category))
                .addSerie(dataProvider.getSeriesColumn(series))
                .setCircular(true)
                .setLabelFormat("{0}{2}")
                .build();
    }

    DJChart createBarChart(DynamicDataProvider dataProvider, String chartTitle) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(dataProvider.getCategoryColumn().getTitle());
        DJAxisFormat valueAxisFormat = new DJAxisFormat(dataProvider.getSeriesColumn().getTitle());

        return new DJBarChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn())
                .addSerie(dataProvider.getSeriesColumn())
                .setShowTickMarks(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DJChart createBarChart(DynamicDataProvider dataProvider, String chartTitle, String category, String series) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(dataProvider.getCategoryColumn().getTitle());
        DJAxisFormat valueAxisFormat = new DJAxisFormat(dataProvider.getSeriesColumn().getTitle());

        return new DJBarChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn(category))
                .addSerie(dataProvider.getSeriesColumn(series))
                .setShowTickMarks(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DJChart createLineChart(DynamicDataProvider dataProvider, String chartTitle) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(dataProvider.getCategoryColumn().getTitle());
        DJAxisFormat valueAxisFormat = new DJAxisFormat(dataProvider.getSeriesColumn().getTitle());

        return new DJLineChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn())
                .addSerie(dataProvider.getSeriesColumn())
                .setShowShapes(true)
                .setShowLines(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DJChart createLineChart(DynamicDataProvider dataProvider, String chartTitle, String category, String
            series) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(category);
        DJAxisFormat valueAxisFormat = new DJAxisFormat(series);

        return new DJLineChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn(category))
                .addSerie(dataProvider.getSeriesColumn(series))
                .setShowShapes(true)
                .setShowLines(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DynamicReportBuilder createTable(DynamicDataProvider dataProvider, DynamicReportBuilder reportBuilder) {
        List<AbstractColumn> tableColumns = dataProvider.getColumns();
        for (AbstractColumn column : tableColumns) {
            reportBuilder.addColumn(column);
        }
        DynamicStyleProvider.addStyles(reportBuilder);
        return reportBuilder;
    }
}
