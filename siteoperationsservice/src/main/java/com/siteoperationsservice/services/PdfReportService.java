package com.siteoperationsservice.services;

import com.siteoperationsservice.dtos.DailyLogResponseDto;
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

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void generateAndSendPdf(String toEmail, DailyLogResponseDto logDto) throws Exception {

        Context context = new Context();
        context.setVariable("dailyLog", logDto);
        String htmlContent = templateEngine.process("daily-log-report", context);

        byte[] pdfBytes;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            pdfBytes = outputStream.toByteArray();
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("MjengoCMS: Daily Site Report - " + logDto.getLogDate());
        helper.setText("Please find attached the daily operations log for " + logDto.getLogDate() + ".", false);

        ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
        helper.addAttachment("Daily_Log_" + logDto.getLogDate() + ".pdf", pdfAttachment);

        mailSender.send(message);
        log.info("PDF report successfully sent to {}", toEmail);
    }
}