package com.prechatting.service;


import com.prechatting.enums.BlendDimensions;
import com.prechatting.result.Message;
import com.prechatting.support.DiscordChannel;
import eu.maxschuster.dataurl.DataUrl;

import java.util.List;

public interface DiscordService {

	Message<Void> imagine(String prompt, DiscordChannel discordConfig);

	Message<Void> upscale(String messageId, int index, String messageHash, int messageFlags, DiscordChannel discordConfig);

	Message<Void> variation(String messageId, int index, String messageHash, int messageFlags, DiscordChannel discordConfig);

	Message<Void> reroll(String messageId, String messageHash, int messageFlags, DiscordChannel discordConfig);

	Message<Void> describe(String finalFileName, DiscordChannel discordConfig);

	Message<Void> blend(List<String> finalFileNames, BlendDimensions dimensions, DiscordChannel discordConfig);

	Message<String> upload(String fileName, DataUrl dataUrl, DiscordChannel discordConfig);

	Message<String> sendImageMessage(String content, String finalFileName, DiscordChannel discordConfig);

}
