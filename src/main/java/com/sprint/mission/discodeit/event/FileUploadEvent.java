package com.sprint.mission.discodeit.event;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record FileUploadEvent(UUID fileId, MultipartFile file) {

}
