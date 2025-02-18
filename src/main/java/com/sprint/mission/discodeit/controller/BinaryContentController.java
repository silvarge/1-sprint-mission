package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.util.FileConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProfileByReferenceId(@RequestParam UUID referenceId) throws IOException {
        BinaryContentDTO.convert content = binaryContentService.findProfileByReferenceId(referenceId);

        byte[] pngBytes = FileConverter.convertToPng(content.file());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(content.mimeType() != null ? MediaType.parseMediaType(content.mimeType()) : MediaType.IMAGE_PNG);
        headers.setContentLength(pngBytes.length);
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(content.filename(), StandardCharsets.UTF_8));

        return new ResponseEntity<>(pngBytes, headers, HttpStatus.OK);
    }

    // 다중 파일은 이미지 형태로 보낼 수가 없어서 zip 파일로 보내야 할 것 같음
    @RequestMapping(value = "/message", method = RequestMethod.GET, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> getContentsByReferenceId(@RequestParam UUID referenceId) throws IOException {
        List<BinaryContentDTO.convert> content = binaryContentService.findContentsByReferenceId(referenceId);
        return FileConverter.createZipFile(content);
    }

}
