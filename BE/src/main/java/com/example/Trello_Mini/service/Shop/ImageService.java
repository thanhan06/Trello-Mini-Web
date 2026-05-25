package com.example.Trello_Mini.service.Shop;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    // Thư mục lưu file trên ổ cứng (Nằm ngay trong gốc thư mục BE)
    private final String UPLOAD_DIR = "uploads/";

    public String saveImageToFileSystem(MultipartFile file) throws IOException {
        // Tạo thư mục nếu chưa tồn tại
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Đổi tên file ngẫu nhiên để tránh trùng lặp
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        // Lưu file vật lý
        Path filePath = Paths.get(UPLOAD_DIR + newFileName);
        Files.write(filePath, file.getBytes());

        // Trả về đường dẫn để Controller lưu vào DB
        // Trình duyệt sẽ đọc qua cấu hình ResourceHandler
        return "/uploads/" + newFileName; 
    }
}
