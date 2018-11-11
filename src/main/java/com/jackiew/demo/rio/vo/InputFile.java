package com.jackiew.demo.rio.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InputFile {
    private MultipartFile global;
    private MultipartFile local;
}
