package com.sprint.mission.discodeit.async.event;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record FileUploadEvent(UUID fileId, MultipartFile file, UUID receiverId) {

}
