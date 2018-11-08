package com.jackiew.demo.rio.compare;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

import com.jackiew.demo.rio.vo.GlobalRIo;
import com.jackiew.demo.rio.vo.LocalRIo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RioComparator {
    private final Workbook workbook;
    private static final LocalRIo DEFAULT_LOCAL_RIO = new LocalRIo();
    private static final GlobalRIo DEFAULT_GLOBAL_RIO = new GlobalRIo();
    private final String location;
    private final FileInputStream inputFile;

    private Object getCellValue(Cell cell) {
        if (CellType.STRING.equals(cell.getCellType())) {
            return cell.getStringCellValue();
        }
        if (CellType.NUMERIC.equals(cell.getCellType())) {
            Double value = cell.getNumericCellValue();
            return value.intValue();
        }
        throw new IllegalArgumentException("not supported cell type");
    }

    public RioComparator(String location) throws IOException {
        inputFile = new FileInputStream(location);
        workbook = new XSSFWorkbook(inputFile);
        this.location = location;
    }

    public List<LocalRIo> parseLocal() throws IOException {
        List<LocalRIo> rioList = new ArrayList<LocalRIo>();


        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            LocalRIo rio = new LocalRIo();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                Object cellValue = getCellValue(cell);
                if (columnIndex == 0) {
                    rio.setPartNo(cellValue.toString());
                }
                if (columnIndex == 1) {
                    rio.setModelName(cellValue.toString());
                }
                if (columnIndex == 2) {
                    rio.setOrderType(Integer.valueOf(cellValue.toString()));
                }
                if (columnIndex == 3) {
                    rio.setRioQty(Integer.valueOf(cellValue.toString()));
                }
                if (columnIndex == 4) {
                    rio.setAllocQty(Integer.valueOf(cellValue.toString()));
                }
                if (columnIndex == 5) {
                    rio.setOffset(Integer.valueOf(cellValue.toString()));
                }
                if (columnIndex == 6) {
                    rio.setAvailQty(Integer.valueOf(cellValue.toString()));
                }
            }
            rioList.add(rio);
        }
        return rioList;
    }

    public List<GlobalRIo> parseGlobal() {
        List<GlobalRIo> rioList = new ArrayList<GlobalRIo>();
        Sheet sheet = workbook.getSheetAt(1);
        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            GlobalRIo rio = new GlobalRIo();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                Object cellValue = getCellValue(cell);
                if (columnIndex == 0) {
                    rio.setPartNo(cellValue.toString());
                }
                if (columnIndex == 1) {
                    rio.setModelName(cellValue.toString());
                }
                if (columnIndex == 2) {
                    rio.setAllocQty(Integer.valueOf(cellValue.toString()));
                }
            }
            rioList.add(rio);
        }
        return rioList;
    }

    public void compare() {
        List<LocalRIo> localRIos;
        List<GlobalRIo> globalRIos;
        try {
            localRIos = parseLocal();
            globalRIos = parseGlobal();
        } catch (IOException e) {
            throw new RuntimeException("parse error", e);
        } finally {
            try {
                inputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, LocalRIo> localRIoMap = localRIos.stream().collect(toMap(rio -> rio.getPartNo() + "" + rio.getModelName(), rio -> rio));
        Map<String, GlobalRIo> globalRIoMap = globalRIos.stream().collect(toMap(rio -> rio.getPartNo() + "" + rio.getModelName(), rio -> rio));
        Set<String> localKeySet = localRIoMap.keySet();
        Set<String> globalKeySet = globalRIoMap.keySet();
        Set<String> uniqueSet = new HashSet<>();
        uniqueSet.addAll(localKeySet);
        uniqueSet.addAll(globalKeySet);
        Map<GlobalRIo, LocalRIo> rioPairMap = new HashMap<>();
        uniqueSet.forEach(key -> rioPairMap.put(globalRIoMap.getOrDefault(key, DEFAULT_GLOBAL_RIO), localRIoMap.getOrDefault(key, DEFAULT_LOCAL_RIO)));
        Sheet resultSheet = createSheet("compare result");
        writeHeader(resultSheet.createRow(0));
        Sheet issueDataSheet = createSheet("issue data");
        writeHeader(issueDataSheet.createRow(0));

        int index = 1;
        List<Map.Entry<GlobalRIo, LocalRIo>> issueDataList = new ArrayList<>();
        for (Map.Entry<GlobalRIo, LocalRIo> entry : rioPairMap.entrySet()) {
            GlobalRIo globalRIo = entry.getKey();
            LocalRIo localRIo = entry.getValue();
            if ((globalRIo.getAllocQty() > localRIo.getRioQty() && localRIo.getAvailQty() > 0) || localRIo.getOffset() < 0) {
                issueDataList.add(entry);
            }
            Row row = resultSheet.createRow(index++);
            writeRow(globalRIo, localRIo, row);
        }
        index = 1;
        for (Map.Entry<GlobalRIo, LocalRIo> entry : issueDataList) {
            GlobalRIo globalRIo = entry.getKey();
            LocalRIo localRIo = entry.getValue();
            Row row = issueDataSheet.createRow(index++);
            writeRow(globalRIo, localRIo, row);
        }
        try (FileOutputStream out = new FileOutputStream(location)) {
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException("write result failed", e);
        }
    }

    private Sheet createSheet(String sheetName) {
        Sheet resultSheet = workbook.getSheet(sheetName);
        if (Objects.nonNull(resultSheet)) {
            int index = workbook.getSheetIndex(sheetName);
            workbook.removeSheetAt(index);
        }
        return workbook.createSheet(sheetName);
    }

    private void writeHeader(Row row) {
        row.createCell(0).setCellValue("part_no");
        row.createCell(1).setCellValue("model_name");
        row.createCell(2).setCellValue("alloc_qty(Global)");
        row.createCell(3).setCellValue("part_no");
        row.createCell(4).setCellValue("model_name");
        row.createCell(5).setCellValue("order type");
        row.createCell(6).setCellValue("rio_qty(Local)");
        row.createCell(7).setCellValue("alloc_qty");
        row.createCell(8).setCellValue("offset");
        row.createCell(9).setCellValue("avail_qty");
        row.createCell(10).setCellValue("diff");
    }

    private void writeRow(GlobalRIo globalRIo, LocalRIo localRIo, Row row) {
        Cell globalPartNo = row.createCell(0);
        globalPartNo.setCellValue(globalRIo.getPartNo());
        Cell globalModelName = row.createCell(1);
        globalModelName.setCellValue(globalRIo.getModelName());
        Cell globalAllocQty = row.createCell(2);
        globalAllocQty.setCellValue(globalRIo.getAllocQty());
        Cell localPartNo = row.createCell(3);
        localPartNo.setCellValue(localRIo.getPartNo());
        Cell localModelName = row.createCell(4);
        localModelName.setCellValue(localRIo.getModelName());
        Cell localOrderType = row.createCell(5);
        localOrderType.setCellValue(localRIo.getOrderType());
        Cell localrioQty = row.createCell(6);
        localrioQty.setCellValue(localRIo.getRioQty());
        Cell localAllocQty = row.createCell(7);
        localAllocQty.setCellValue(localRIo.getAllocQty());
        Cell localOffset = row.createCell(8);
        localOffset.setCellValue(localRIo.getOffset());
        Cell localAvailQty = row.createCell(9);
        localAvailQty.setCellValue(localRIo.getAvailQty());
        Cell diffCell = row.createCell(10);
        diffCell.setCellValue(globalRIo.getAllocQty() - localRIo.getRioQty());
    }

    public static void main(String[] args) throws IOException {
        System.out.println("please input file path, separate by whitespace\n");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String path = scanner.next();
            if ("quit".equals(path)) {
                System.exit(0);
            }
            System.out.println("process file:" + path);
            try {
                RioComparator rioComparator = new RioComparator(path);
                rioComparator.compare();
                System.out.println("done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
