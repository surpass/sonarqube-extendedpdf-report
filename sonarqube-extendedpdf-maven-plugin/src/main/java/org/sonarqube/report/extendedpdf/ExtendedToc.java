/*
 * SonarQube Extended-PDF Report (Maven plugin)
 * Copyright (C) 2014 hCentive - Technology Solutions to Simplify Healthcare
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarqube.report.extendedpdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.sonar.report.pdf.Toc;
import java.awt.*;
import java.io.ByteArrayOutputStream;

/**
 * Created by Momin.Khan
 * Table Of Contents
 */

public class ExtendedToc extends Toc {
    private Document toc;
    private ByteArrayOutputStream tocOutputStream;
    private PdfPTable content;
    private PdfWriter writer;

    public ExtendedToc() {
        toc = new Document(PageSize.LETTER, 50, 50, 80, 20);
        content = new PdfPTable(2);
        Rectangle page = toc.getPageSize();
        content.setTotalWidth(page.getWidth() - toc.leftMargin() - toc.rightMargin());
        content.getDefaultCell().setUseVariableBorders(true);
        content.getDefaultCell().setBorderColorBottom(Color.WHITE);
        content.getDefaultCell().setBorderColorRight(Color.WHITE);
        content.getDefaultCell().setBorderColorLeft(Color.WHITE);
        content.getDefaultCell().setBorderColorTop(Color.WHITE);
        content.getDefaultCell().setBorderWidthBottom(2f);
    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float position, Paragraph title) {
        content.getDefaultCell().setBorderColorBottom(Color.LIGHT_GRAY);
        content.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_LEFT);
        content.getDefaultCell().setUseBorderPadding(true);
        content.addCell(new Phrase(title.getContent(), new Font(Font.HELVETICA, 11)));
        content.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
        content.addCell(new Phrase("Page " + document.getPageNumber(), new Font(Font.HELVETICA, 11)));
        content.getDefaultCell().setBorderColorBottom(Color.WHITE);
        content.getDefaultCell().setUseBorderPadding(false);
    }

    @Override
    public void onChapterEnd(PdfWriter writer, Document document, float position) {
        content.addCell("");
        content.addCell("");
    }

    @Override
    public void onSection(PdfWriter writer, Document document, float position, int depth, Paragraph title) {
        content.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_LEFT);
        switch (depth) {
            case 2:
                content.getDefaultCell().setIndent(10);
                content.addCell(new Phrase(title.getContent(), new Font(Font.HELVETICA, 10)));
                content.getDefaultCell().setIndent(0);
                content.addCell("");
                break;
            default:
                content.getDefaultCell().setIndent(20);
                content.addCell(new Phrase(title.getContent(), new Font(Font.HELVETICA, 9)));
                content.getDefaultCell().setIndent(0);
                content.addCell("");
        }
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        try {
            toc.add(content);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public Document getTocDocument() {
        return toc;
    }

    public ByteArrayOutputStream getTocOutputStream() {
        return tocOutputStream;
    }

    public void setHeader(ExtendedHeader header) {
        tocOutputStream = new ByteArrayOutputStream();
        writer = null;
        try {
            writer = PdfWriter.getInstance(toc, tocOutputStream);
            writer.setPageEvent(header);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
