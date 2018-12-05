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

import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import java.util.List;

/**
 * This interface provides the basic methods of a data provider..
 */
public interface DataProvider {

    /**
     * This method will return the abstract columns created for the dynamic report builder
     * @return the list of abstract columns created.
     */
    public List<AbstractColumn> getColumns();

    /**
     * This method will return an abstract column for a given column name
     * @param columnName the name of the abstract column
     * @return abstract column created in the dynamic report builder for the given name
     */
    public AbstractColumn getCategoryColumn(String columnName);

    /**
     * This method will return the series column created in the dynamic report builder
     * @param columnName name of the series column
     * @return the abstract series column in the dynamic report builder for the given name
     */
    public AbstractColumn getSeriesColumn(String columnName);

    /**
     * This method will return the category column created in the dynamic report builder
     * @return the abstract category column chosen from the created abstract columns
     */
    public AbstractColumn getCategoryColumn();

    /**
     * This method will returm the series column created in the dynamic report builder
     * @return the abstract series column chosen from the created abstract columns
     */
    public AbstractColumn getSeriesColumn();

}

