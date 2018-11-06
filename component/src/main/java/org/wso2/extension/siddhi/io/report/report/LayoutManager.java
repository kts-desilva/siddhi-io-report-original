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

package org.wso2.extension.siddhi.io.report.report;

import org.wso2.extension.siddhi.io.report.util.ReportConstants;

import java.util.Map;

/**
 * This class provides implementation of the report layout manager.
 */

public class LayoutManager {
    public LayoutManager() {
    }

    public DynamicLayoutManager getLayout(Map<String, String> reportProperties) {
        if (reportProperties.get(ReportConstants.FOOTER) != null) {
            return new DynamicLayoutManager(reportProperties.get(ReportConstants.FOOTER));
        }
        return new DynamicLayoutManager();
    }
}