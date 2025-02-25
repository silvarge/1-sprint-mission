package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.CustomApiResponse;
import com.sprint.mission.discodeit.docs.ReadStatusControllerDocs;
import com.sprint.mission.discodeit.dto.ReadStatusDTO;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusControllerDocs {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public CustomApiResponse<ReadStatusDTO.idResponse> createReadStatus(@RequestBody ReadStatusDTO.request readStatusReqDto) {
        return CustomApiResponse.created(readStatusService.create(readStatusReqDto));
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PUT)
    public CustomApiResponse<ReadStatusDTO.idResponse> updateReadStatus(@PathVariable Long readStatusId, @RequestParam("lastReadAt") Instant lastReadAt) {
        return CustomApiResponse.ok(readStatusService.update(ReadStatusDTO.update.builder()
                .id(readStatusId).lastReadAt(lastReadAt).build()
        ));
    }

    @RequestMapping(method = RequestMethod.GET)
    public CustomApiResponse<List<ReadStatusDTO.response>> getReadStatusByUser(@RequestParam UUID userId) {
        return CustomApiResponse.ok(readStatusService.findAllByUserId(userId));
    }
}
