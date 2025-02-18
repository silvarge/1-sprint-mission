package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.enums.ContentType;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileConverter {
    public static BinaryContentDTO.request convertToBinaryContent(MultipartFile file, UUID referenceId, ContentType contentType) {
        try {
            return BinaryContentDTO.request.builder()
                    .file(file.getBytes())  // MultipartFile → byte[]
                    .referenceId(referenceId)
                    .mimeType(file.getContentType()) // MIME 타입 가져오기
                    .filename(file.getOriginalFilename()) // 파일명 가져오기
                    .contentType(contentType)
                    .build();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    // TODO: formatName도 나중에 추가로 받아야 함
    public static byte[] convertToPng(byte[] fileData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
        BufferedImage image = ImageIO.read(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    // TODO: formatName도 나중에 추가로 받아야 함
    public static ResponseEntity<byte[]> createZipFile(List<BinaryContentDTO.convert> binaryDTOList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        for (BinaryContentDTO.convert dto : binaryDTOList) {
            byte[] pngBytes = convertToPng(dto.file());

            ZipEntry zipEntry = new ZipEntry(dto.filename() + ".png");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(pngBytes);
            zipOutputStream.closeEntry();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "images.zip");

        zipOutputStream.close();

        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }
}
