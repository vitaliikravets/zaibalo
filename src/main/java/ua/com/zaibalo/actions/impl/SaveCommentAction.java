package ua.com.zaibalo.actions.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.com.zaibalo.actions.Action;
import ua.com.zaibalo.constants.ZaibaloConstants;
import ua.com.zaibalo.db.api.CommentsDAO;
import ua.com.zaibalo.db.api.PostsDAO;
import ua.com.zaibalo.helper.CharArrayWriterResponse;
import ua.com.zaibalo.helper.StringHelper;
import ua.com.zaibalo.helper.ajax.AjaxResponse;
import ua.com.zaibalo.helper.ajax.FailResponse;
import ua.com.zaibalo.helper.ajax.SuccessResponse;
import ua.com.zaibalo.model.Comment;
import ua.com.zaibalo.model.Post;
import ua.com.zaibalo.model.User;
import ua.com.zaibalo.validation.Validator;

@Component
public class SaveCommentAction implements Action{

	private static final String CONTENT_PARAM_NAME = "content";
	private static final String POST_ID_PARAM_NAME = "post_id";

	@Autowired
	private PostsDAO postsDAO;
	@Autowired
	private CommentsDAO commentsDAO;
	
	@Override
	public AjaxResponse run(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String pIDVal = (String) request.getParameter(POST_ID_PARAM_NAME);
		int postId = Integer.parseInt(pIDVal);
		
		String content = (String) request.getParameter(CONTENT_PARAM_NAME);

		Post post = postsDAO.getObjectById(postId);

		String postTitle = post.getTitle();
		
		User user = (User)request.getSession().getAttribute(ZaibaloConstants.USER_PARAM_NAME);
		
		//save comment
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setAuthorId(user.getId());
		comment.setPostId(postId);
		comment.setDate(new Date());
		//comment.setAuthorDisplayName(user.getDisplayName());
		comment.setAuthor(user);
		comment.setPostTitle(postTitle);
		comment.setRatingCount(0);
		comment.setRatingSum(0);
		
		Validator validator = new Validator();
		if(!validator.validateComment(comment)){
			return new FailResponse(StringHelper.getLocalString("error_colon") + validator.getErrors());
		}
		
		int id = commentsDAO.insert(comment);

		comment.setId(id);
		
		request.setAttribute("comment", comment);
		
		CharArrayWriterResponse customResponse  = new CharArrayWriterResponse(response);
	    request.getRequestDispatcher("/jsp/comment_wrapper.jsp").forward(request, customResponse);
	    
	    return new SuccessResponse(customResponse.getOutput());
	}

}