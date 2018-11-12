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

import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides implementation of the identifying ranges in data.
 */

public class RangeConditionStyleExpressionGenerator extends ConditionStyleExpression {
    private static final long serialVersionUID = 6106269076155338045L;

    @Override
    public Object evaluate(Map fields, Map variables, Map parameters) {
        Object value = this.getCurrentValue();
        if (value == null) {
            return null;
        } else {
            String text = value.toString();
            String patternString = "([0-9]*\\s*-\\s*[0-9]*)";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        }
    }

    @Override
    public String getClassName() {
        return Boolean.class.getName();
    }
}
