package com.jackiew.demo;

import com.jackiew.demo.rio.excel.ExcelParser;
import com.jackiew.demo.rio.excel.SheetExcelParser;
import com.jackiew.demo.rio.vo.GlobalRIo;
import com.jackiew.demo.rio.vo.LocalRIo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication
public class RioCompareApplication {
    public static void main(String[] args) {
        SpringApplication.run(RioCompareApplication.class);
    }

    @Bean("localParser")
    public ExcelParser<LocalRIo> localParser() {
        return new SheetExcelParser<>(LocalRIo.class);
    }

    @Bean("globalParser")
    public ExcelParser<GlobalRIo> globalParser() {
        return new SheetExcelParser<>(GlobalRIo.class);
    }

    @Bean("mailSender")
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.qq.com");
        sender.setUsername("scottfield@qq.com");
        sender.setPassword("");
        return sender;
    }

}
