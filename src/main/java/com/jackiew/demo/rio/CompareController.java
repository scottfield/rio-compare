package com.jackiew.demo.rio;

import com.jackiew.demo.rio.compare.RioComparator;
import com.jackiew.demo.rio.email.ReportMimeMessagePreparator;
import com.jackiew.demo.rio.vo.InputFile;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/compare")
public class CompareController {
    @Autowired
    private RioComparator comparator;
    @Autowired
    @Qualifier(value = "mailSender")
    private JavaMailSender sender;

    @RequestMapping(method = RequestMethod.GET)
    public String uploadPage() {
        return "index";
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String success() {
        return "success";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String compare(@RequestParam List<MultipartFile> files, @RequestParam String emails) throws IOException, MessagingException {
        if (StringUtils.isEmpty(emails)) {
            throw new IllegalArgumentException("emails:(" + emails + ") list is invalid.");
    }
        Map<String, InputFile> fileMap = new HashMap<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            if (!fileName.contains("-")) {
                throw new IllegalArgumentException("input file name should be xxxx-xxx.xlsx such as global-cn.xlsx");
            }
            String[] nameArr = fileName.split("-");
            String prefix = nameArr[0];
            String regionName = nameArr[1].toUpperCase();
            if (!fileMap.containsKey(regionName)) {
                fileMap.put(regionName, new InputFile());
            }
            InputFile input = fileMap.get(regionName);
            if ("global".equalsIgnoreCase(prefix)) {
                input.setGlobal(file);
            }
            if ("local".equalsIgnoreCase(prefix)) {
                input.setLocal(file);
            }
        }
        List<String> illegalRegion = fileMap.entrySet().stream()
                .filter(entry -> entry.getValue().getLocal() == null || entry.getValue().getGlobal() == null).map(Map.Entry::getKey).collect(toList());
        if (!illegalRegion.isEmpty()) {
            throw new IllegalArgumentException("please check your file list, these region not contain global or local input data:" + illegalRegion.toString());
        }
        Map<String, Workbook> reportMap = new HashMap<>();
        for (Map.Entry<String, InputFile> entry : fileMap.entrySet()) {
            InputFile input = entry.getValue();
            Workbook globalWorkbook = new XSSFWorkbook(input.getGlobal().getInputStream());
            Workbook localWorkbook = new XSSFWorkbook(input.getLocal().getInputStream());
            Workbook resultWorkbook = comparator.compare(globalWorkbook, localWorkbook);
            reportMap.put(entry.getKey() + ".xlsx", resultWorkbook);
        }
        sender.send(new ReportMimeMessagePreparator(emails, reportMap));
        return "redirect:/compare/success";
    }

    public static void main(String[] args) throws MessagingException {
        System.out.println("cn.xlsx".substring(0, "cn.xlsx".lastIndexOf(".")));
    }
}
