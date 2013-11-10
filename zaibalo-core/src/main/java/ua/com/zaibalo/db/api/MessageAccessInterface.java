package ua.com.zaibalo.db.api;

import java.util.List;

import ua.com.zaibalo.model.Message;

public interface MessageAccessInterface {
	int insert(Message message);
	List<Message> getAllUserDiscussionMessages(int discussionId, int userId);
	int getUnreadMessagesCount(int recipientId);
	void setDialogMessagesRead(int discussionId, int recipientId);
	Message getMessageById(int messageId);
}
