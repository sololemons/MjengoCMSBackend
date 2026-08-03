package com.storekeeperservice.services;

import com.storekeeperservice.dtos.TrackingLedgerDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public ByteArrayInputStream generatePdfStream(List<TrackingLedgerDto> ledgerData) throws Exception {

        Context context = new Context();
        context.setVariable("ledgerData", ledgerData);
        context.setVariable("generatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String htmlContent = templateEngine.process("material-movement", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    public void generateAndSendPdf(String toEmail, List<TrackingLedgerDto> ledgerData) throws Exception {

        ByteArrayInputStream pdfStream = generatePdfStream(ledgerData);
        byte[] pdfBytes = pdfStream.readAllBytes();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("MjengoCMS: Material Movement Ledger Report");
        helper.setText("Please find attached the requested ledger report.", false);

        ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
        helper.addAttachment("Material_Ledger_Report.pdf", pdfAttachment);

        mailSender.send(message);
        log.info("PDF ledger report successfully sent to {}", toEmail);
    }
}