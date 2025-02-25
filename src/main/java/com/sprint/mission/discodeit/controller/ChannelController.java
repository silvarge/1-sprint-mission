package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.CustomApiResponse;
import com.sprint.mission.discodeit.docs.ChannelControllerDocs;
import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelControllerDocs {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public CustomApiResponse<ChannelDTO.idResponse> createPublicChannel(@RequestBody ChannelDTO.request channelReqDto) {
        return CustomApiResponse.created(channelService.createPublicChannel(channelReqDto));
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public CustomApiResponse<ChannelDTO.idResponse> createPrivateChannel(@RequestBody ChannelDTO.request channelReqDto) {
        return CustomApiResponse.created(channelService.createPrivateChannel(channelReqDto));
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    public CustomApiResponse<ChannelDTO.idResponse> updatePublicChannel(@PathVariable Long channelId, @RequestBody ChannelDTO.update updateDto) {
        return CustomApiResponse.ok(channelService.update(updateDto));
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public CustomApiResponse<ChannelDTO.idResponse> deleteChannel(@PathVariable Long channelId) {
        return CustomApiResponse.ok(channelService.delete(channelId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public CustomApiResponse<List<ChannelDTO.response>> getChannels(@RequestParam UUID userId) {
        return CustomApiResponse.ok(channelService.findAllByUserId(userId));
    }
}
