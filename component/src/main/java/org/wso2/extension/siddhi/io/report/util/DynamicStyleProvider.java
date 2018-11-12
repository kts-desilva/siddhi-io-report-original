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

package org.wso2.extension.siddhi.io.report.util;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;

import java.awt.Color;

/**
 * This class provides implementation of the table styles for the reports generated.
 */

public class DynamicStyleProvider {
    public static void addStyles(DynamicReportBuilder reportBuilder) {
        Style st = new Style();
        st.setHorizontalAlign(HorizontalAlign.RIGHT);
        st.setVerticalAlign(VerticalAlign.TOP);
        st.setFont(Font.ARIAL_BIG_BOLD);
        Border border = new Border(2, Border.BORDER_STYLE_SOLID, new Color(196, 186, 186));
        st.setBorderBottom(border);

        Style tableHeaderStyle = new Style();
        tableHeaderStyle.setTransparent(false);
//        tableHeaderStyle.setBackgroundColor(new Color(97, 97, 96));
        tableHeaderStyle.setBackgroundColor(Color.decode("#616161"));
        tableHeaderStyle.setTextColor(new Color(255, 255, 255));
        tableHeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        tableHeaderStyle.setBorder(Border.THIN());
        tableHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        reportBuilder.setPrintBackgroundOnOddRows(true);

        Style tableStyle = new Style();
        tableStyle.setPaddingLeft(20);
        tableStyle.setPaddingRight(20);
        tableStyle.setPaddingTop(5);
        tableStyle.setPaddingBottom(5);

        Style oddRowStyle = new Style();
        oddRowStyle.setBackgroundColor(new Color(243, 242, 242));
        reportBuilder.setOddRowBackgroundStyle(oddRowStyle);
        reportBuilder.setDefaultStyles(null, null, null, tableStyle);
        reportBuilder.setUseFullPageWidth(true);
    }

    public static Style getColumnHeaderStyle(String className) {
        Style columnHeaderStyle = new Style();
        columnHeaderStyle.setTransparent(false);
        columnHeaderStyle.setBackgroundColor(Color.decode("#616161"));
        columnHeaderStyle.setTextColor(Color.white);
        columnHeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE);

        if (className.equals(Integer.class.getName())) {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            columnHeaderStyle.setPaddingRight(20);
        } else if (className.equals(String.class.getName())) {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
            columnHeaderStyle.setPaddingLeft(20);
            columnHeaderStyle.setPaddingRight(20);
        } else if (className.equals(Double.class.getName()) || className.equals(Long.class.getName()) || className
                .equals(Float.class.getName())) {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            columnHeaderStyle.setPaddingRight(20);
        } else {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        }
        return columnHeaderStyle;
    }
}
