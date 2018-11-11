package com.jackiew.demo.rio.compare;

import com.jackiew.demo.rio.excel.ExcelParser;
import com.jackiew.demo.rio.excel.ExcelSheetWriter;
import com.jackiew.demo.rio.vo.CompareRIo;
import com.jackiew.demo.rio.vo.GlobalRIo;
import com.jackiew.demo.rio.vo.LocalRIo;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class RioComparator {
    private static final LocalRIo DEFAULT_LOCAL_RIO = new LocalRIo();
    private static final GlobalRIo DEFAULT_GLOBAL_RIO = new GlobalRIo();

    @Qualifier(value = "globalParser")
    @Autowired
    private ExcelParser<GlobalRIo> globalRioExcelParser;

    @Qualifier(value = "localParser")
    @Autowired
    private ExcelParser<LocalRIo> localRioExcelParser;


    public RioComparator() throws IOException {

    }

    public Workbook compare(Workbook globalWorkbook, Workbook localWorkbook) throws IOException {
        Workbook outputWorkbook = new XSSFWorkbook();

        List<CompareRIo> dataList = getCompareRioList(globalWorkbook, localWorkbook);
        List<CompareRIo> issueDataList = dataList.stream().filter(CompareRIo::isIssueData).collect(toList());

        ExcelSheetWriter resultExcelSheetWriter = new ExcelSheetWriter("compare result", outputWorkbook, dataList, CompareRIo.class);
        resultExcelSheetWriter.writeSheet();
        ExcelSheetWriter issueDataExcelSheetWriter = new ExcelSheetWriter("issue data", outputWorkbook, issueDataList, CompareRIo.class);
        issueDataExcelSheetWriter.writeSheet();
        return outputWorkbook;
    }

    private List<CompareRIo> getCompareRioList(Workbook globalWorkbook, Workbook localWorkbook) {
        List<LocalRIo> localRIos = Collections.emptyList();
        List<GlobalRIo> globalRIos = Collections.emptyList();
        Sheet localSheet = localWorkbook.getSheetAt(0);
        Sheet globalSheet = globalWorkbook.getSheetAt(0);
        localRIos = localRioExcelParser.parse(localSheet);
        globalRIos = globalRioExcelParser.parse(globalSheet);
        Map<String, LocalRIo> localRIoMap = localRIos.stream().collect(toMap(rio -> rio.getPartNo() + "" + rio.getModelName(), rio -> rio));
        Map<String, GlobalRIo> globalRIoMap = globalRIos.stream().collect(toMap(rio -> rio.getGblPartNo() + "" + rio.getGblModelName(), rio -> rio));
        Set<String> localKeySet = localRIoMap.keySet();
        Set<String> globalKeySet = globalRIoMap.keySet();
        Set<String> uniqueSet = new HashSet<>();
        uniqueSet.addAll(localKeySet);
        uniqueSet.addAll(globalKeySet);
        List<CompareRIo> result = uniqueSet.stream().map(key -> {
            GlobalRIo global = globalRIoMap.getOrDefault(key, DEFAULT_GLOBAL_RIO);
            LocalRIo local = localRIoMap.getOrDefault(key, DEFAULT_LOCAL_RIO);
            CompareRIo target = new CompareRIo();
            BeanUtils.copyProperties(global, target);
            BeanUtils.copyProperties(local, target);
            return target;
        }).collect(Collectors.toList());
        return result;
    }
}
