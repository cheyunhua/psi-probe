/*
 * Licensed under the GPL License.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.googlecode.psiprobe.tags;

import java.io.IOException;
import java.text.MessageFormat;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class VisualScoreTag extends BodyTagSupport {

    private double value;
    private double value2 = 0;
    private double minValue = 0;
    private double maxValue = 100;
    private int partialBlocks = 1;
    private int fullBlocks = 5;
    private boolean showEmptyBlocks=false;
    private boolean showA = false;
    private boolean showB = false;

    public int doAfterBody() throws JspException {

        try {
            double unitSize = (maxValue - minValue) / (fullBlocks * partialBlocks);
            double blockWidth = unitSize * partialBlocks;

            int fullRedBlockCount = (int) Math.floor(value / blockWidth);
            int partialRedBlockIndex = (int) Math.floor((value - fullRedBlockCount * blockWidth) / unitSize);
            int partialBlueBlockIndex1 = (partialRedBlockIndex > 0 ? Math.min((int) Math.floor(value2 / unitSize), partialBlocks - partialRedBlockIndex) : 0);
            int fullBlueBlockCount = Math.max(0, (int) Math.floor(value2 / blockWidth) - (partialRedBlockIndex > 0 ? 1 : 0));
            int partialBlueBlockIndex2 = (int) Math.floor((value2 - (fullBlueBlockCount * blockWidth) - (partialBlueBlockIndex1 * unitSize)) / unitSize);

            BodyContent bc = getBodyContent();
            String body = bc.getString().trim();
            JspWriter out = bc.getEnclosingWriter();


            // Beginning
            if (showA) {
                String format = "a0";
                if (fullRedBlockCount > 0 || partialRedBlockIndex > 0) {
                    format = "a1";
                } else if (partialBlueBlockIndex1 == 0 && (fullBlueBlockCount > 0 || partialBlueBlockIndex2 > 0)) {
                    format = "a2";
                }
                out.print(MessageFormat.format(body, new Object[]{format}));
            }

            // Full red blocks
            String fullRedBody = MessageFormat.format(body, new Object[]{partialBlocks + "+0"});
            for (int i = 0; i < fullRedBlockCount; i++) {
                out.print(fullRedBody);
            }

            // Mixed red/blue block (mid-block transition)
            if (partialRedBlockIndex > 0) {
                String partialBody = MessageFormat.format(body, new Object[]{partialRedBlockIndex + "+" + partialBlueBlockIndex1});
                out.print(partialBody);
            }

            // Full blue blocks
            String fullBlueBody = MessageFormat.format(body, new Object[]{"0+" + partialBlocks});
            for (int i = 0; i < fullBlueBlockCount; i++) {
                out.print(fullBlueBody);
            }

            // Partial blue block
            if (partialBlueBlockIndex2 > 0) {
                String partialBody = MessageFormat.format(body, new Object[]{"0+" + partialBlueBlockIndex2});
                out.print(partialBody);
            }

            // Empty blocks
            int emptyBlocks = showEmptyBlocks ? fullBlocks - (fullRedBlockCount + fullBlueBlockCount + (partialRedBlockIndex > 0 ? 1 : 0) + (partialBlueBlockIndex2 > 0 ? 1 : 0)) : 0;
            if (emptyBlocks > 0) {
                String emptyBody = MessageFormat.format(body, new Object[]{"0+0"});
                for (int i = 0; i < emptyBlocks; i++) {
                    out.print(emptyBody);
                }
            }

            // End
            if (showB) {
                String format = "b0";
                if (fullRedBlockCount == fullBlocks) {
                    format = "b1";
                } else if (fullRedBlockCount + (partialRedBlockIndex + partialBlueBlockIndex1 == partialBlocks ? 1 : 0) + fullBlueBlockCount == fullBlocks) {
                    format = "b2";
                }
                out.print(MessageFormat.format(body, new Object[]{format}));
            }

        } catch (IOException ioe) {
            throw new JspException("Error:IOException while writing to client" + ioe.getMessage());
        }

        return SKIP_BODY;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue2(double value2) {
        this.value2 = value2;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public int getPartialBlocks() {
        return partialBlocks;
    }

    public void setPartialBlocks(int partialBlocks) {
        this.partialBlocks = partialBlocks;
    }

    public int getFullBlocks() {
        return fullBlocks;
    }

    public void setFullBlocks(int fullBlocks) {
        this.fullBlocks = fullBlocks;
    }

    public boolean isShowEmptyBlocks() {
        return showEmptyBlocks;
    }

    public void setShowEmptyBlocks(boolean showEmptyBlocks) {
        this.showEmptyBlocks = showEmptyBlocks;
    }

    public boolean isShowA() {
        return showA;
    }

    public void setShowA(boolean showA) {
        this.showA = showA;
    }

    public boolean isShowB() {
        return showB;
    }

    public void setShowB(boolean showB) {
        this.showB = showB;
    }
}