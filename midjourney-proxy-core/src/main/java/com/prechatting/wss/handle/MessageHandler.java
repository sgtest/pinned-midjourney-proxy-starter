package com.prechatting.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import com.prechatting.Constants;
import com.prechatting.enums.MessageType;
import com.prechatting.support.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@Slf4j
public abstract class MessageHandler {
	@Autowired
	protected TaskQueueHelper taskQueueHelper;
	@Autowired
	protected DiscordHelper discordHelper;
	@Autowired
	protected ChannelPool channelPool;

	public abstract void handle(MessageType messageType, DataObject message, DiscordChannel discordChannel);

	public abstract void handle(MessageType messageType, Message message, DiscordChannel discordChannel);

	protected String getMessageContent(DataObject message) {
		return message.hasKey("content") ? message.getString("content") : "";
	}

	protected void finishTask(Task task, DataObject message,DiscordChannel discordChannel) {
		channelPool.finish(discordChannel);
		task.setProperty(Constants.TASK_PROPERTY_MESSAGE_ID, message.getString("id"));
		task.setProperty(Constants.TASK_PROPERTY_FLAGS, message.getInt("flags", 0));
		DataArray attachments = message.getArray("attachments");
		if (!attachments.isEmpty()) {
			String imageUrl = attachments.getObject(0).getString("url");
			task.setImageUrl(replaceCdnUrl(imageUrl));
			task.setProperty(Constants.TASK_PROPERTY_MESSAGE_HASH, getMessageHash(imageUrl));
			task.success();
		} else {
			task.fail("关联图片不存在");
		}
	}

	protected void finishTask(Task task, Message message,DiscordChannel discordChannel) {
		channelPool.finish(discordChannel);
		task.setProperty(Constants.TASK_PROPERTY_MESSAGE_ID, message.getId());
		task.setProperty(Constants.TASK_PROPERTY_FLAGS, (int) message.getFlagsRaw());
		if (!message.getAttachments().isEmpty()) {
			String imageUrl = message.getAttachments().get(0).getUrl();
			task.setImageUrl(replaceCdnUrl(imageUrl));
			task.setProperty(Constants.TASK_PROPERTY_MESSAGE_HASH, getMessageHash(imageUrl));
			task.success();
		} else {
			task.fail("关联图片不存在");
		}
	}

	protected String getMessageHash(String imageUrl) {
		int hashStartIndex = imageUrl.lastIndexOf("_");
		return CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
	}

	protected String getImageUrl(DataObject message) {
		DataArray attachments = message.getArray("attachments");
		if (!attachments.isEmpty()) {
			String imageUrl = attachments.getObject(0).getString("url");
			return replaceCdnUrl(imageUrl);
		}
		return null;
	}

	protected String getImageUrl(Message message) {
		if (!message.getAttachments().isEmpty()) {
			String imageUrl = message.getAttachments().get(0).getUrl();
			return replaceCdnUrl(imageUrl);
		}
		return null;
	}

	protected String replaceCdnUrl(String imageUrl) {
		if (CharSequenceUtil.isBlank(imageUrl)) {
			return imageUrl;
		}
		String cdn = this.discordHelper.getCdn();
		if (CharSequenceUtil.startWith(imageUrl, cdn)) {
			return imageUrl;
		}
		return CharSequenceUtil.replaceFirst(imageUrl, DiscordHelper.DISCORD_CDN_URL, cdn);
	}

}
