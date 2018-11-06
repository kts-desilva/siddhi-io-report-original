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

package org.wso2.extension.siddhi.io.report.report;

import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.ImageBanner;
import net.sf.jasperreports.engine.design.JRDesignBand;
import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * This is a class to provide customized header and footer to the report.
 */
public class DynamicLayoutManager extends ClassicLayoutManager {
    private String footerImagePath = "";
    private static final Logger LOGGER = Logger.getLogger(DynamicLayoutManager.class);

    DynamicLayoutManager() {
    }

    DynamicLayoutManager(String footerImagePath) {
        this.footerImagePath = footerImagePath;
    }

    @Override
    protected void applyBanners() {
        super.applyBanners();
        JRDesignBand pageFooter = (JRDesignBand) getDesign().getPageFooter();
        JRDesignBand pageHeader = (JRDesignBand) getDesign().getTitle();

        if (pageFooter == null) {
            pageFooter = new JRDesignBand();
            getDesign().setPageFooter(pageFooter);
        }

        if (pageHeader == null) {
            pageHeader = new JRDesignBand();
            getDesign().setPageHeader(pageHeader);
        }

        Vector<ImageBanner> bannerVector = new Vector<>();
        if (!footerImagePath.isEmpty()) {
            LOGGER.info("footer image is not empty : " + footerImagePath);
            bannerVector.add(new ImageBanner(footerImagePath, 120, 50,
                    ImageBanner.Alignment.Left));
            applyImageBannersToBand(pageFooter, bannerVector, null, false);
        }
    }
}
