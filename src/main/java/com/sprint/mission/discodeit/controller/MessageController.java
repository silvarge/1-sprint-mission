package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.CustomApiResponse;
import com.sprint.mission.discodeit.docs.MessageControllerDocs;
import com.sprint.mission.discodeit.dto.MessageDTO;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageControllerDocs {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CustomApiResponse<MessageDTO.idResponse> createMessage(
            @RequestPart("message") MessageDTO.request messageReqDto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        System.out.println("CreateMessage" + messageReqDto + ", " + attachments);
        return CustomApiResponse.created(messageService.create(messageReqDto, attachments));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CustomApiResponse<MessageDTO.idResponse> updateMessage(@PathVariable Long messageId,
                                                                  @RequestPart("content") String content,
                                                                  @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

        return CustomApiResponse.created(messageService.update(MessageDTO.update.builder()
                .id(messageId)
                .content(content)
                .attachments(attachments)
                .build()
        ));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public CustomApiResponse<MessageDTO.idResponse> deleteMessage(@PathVariable Long messageId) {
        return CustomApiResponse.created(messageService.delete(messageId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public CustomApiResponse<List<MessageDTO.response>> getMessagesByChannel(@RequestParam UUID channelId) {
        return CustomApiResponse.created(messageService.findAllByChannelId(channelId));
    }
}
