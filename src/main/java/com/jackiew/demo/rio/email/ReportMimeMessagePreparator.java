package com.jackiew.demo.rio.email;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.awt.print.PrinterAbortException;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ReportMimeMessagePreparator implements MimeMessagePreparator {
    private final String emails;
    private final Map<String, Workbook> attachments;

    public ReportMimeMessagePreparator(String emails, Map<String, Workbook> attachments) {
        this.emails = emails;
        this.attachments = attachments;
    }

    @Override
    public void prepare(MimeMessage message) throws Exception {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("scottfield@qq.com");
        helper.setTo(emails.split(";"));
        helper.setText("please refer to the attachment for detail.");
        helper.setSubject("rio compare report");
        for (Map.Entry<String, Workbook> entry : attachments.entrySet()) {
            String reportName = entry.getKey();
            Workbook workbook = entry.getValue();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            helper.addAttachment(reportName, new ByteArrayResource(out.toByteArray()));
        }
    }
}
