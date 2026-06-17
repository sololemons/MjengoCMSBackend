package com.storekeeperservice.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.storekeeperservice.dtos.TrackingLedgerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class PdfReportService {


    public ByteArrayInputStream generateMaterialMovementReport(List<TrackingLedgerDto> data) {
        Document document = new Document(PageSize.A4.rotate()); 
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("MATERIAL MOVEMENT LEDGER REPORT", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated: " + java.time.LocalDateTime.now()));
            document.add(Chunk.NEWLINE);

            // Table Setup (11 Columns to match your DTO)
            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2.5f, 2f, 1.5f, 1f, 1.5f, 2f, 2f, 1.5f, 2f, 2.5f});

            Stream.of("ID", "Material", "Category", "Type", "Qty", "Unit", "By", "To",  "Reason", "Date")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
                        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(header);
                    });

            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            for (TrackingLedgerDto dto : data) {
                table.addCell(new Phrase(String.valueOf(dto.getTrackingId()), rowFont));
                table.addCell(new Phrase(dto.getMaterialName(), rowFont));
                table.addCell(new Phrase(dto.getCategory(), rowFont));
                table.addCell(new Phrase(dto.getMovementType(), rowFont));
                table.addCell(new Phrase(String.valueOf(dto.getQuantity()), rowFont));
                table.addCell(new Phrase(dto.getDenomination(), rowFont));
                table.addCell(new Phrase(dto.getRecordedBy(), rowFont));
                table.addCell(new Phrase(dto.getIssuedTo() != null ? dto.getIssuedTo() : "-", rowFont));
                table.addCell(new Phrase(dto.getReasonForLoss() != null ? dto.getReasonForLoss() : "-", rowFont));
                table.addCell(new Phrase(dto.getTimestamp(), rowFont));
            }

            document.add(table);
        } catch (DocumentException ex) {
            int rowCount = data == null ? 0 : data.size();
            log.error("Failed to generate material movement report PDF. rowCount={}", rowCount, ex);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}