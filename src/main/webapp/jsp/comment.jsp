
	<div id="comment_${comment.id}" class="comment_style">
		<div class="comment_avatar">
			<img src="/image/${comment.author.smallImgPath}" width="32" alt="${comment.author.displayName}">
		</div>
		
		<div class="comment_content">
			<c:if test="${comment.author.id != 2}">
				<div class="comment_author" onclick="location.href='<c:url value="/user/${comment.author.id}"/>';"><c:out value="${comment.author.displayName}"/></div>
			</c:if>
			<c:if test="${comment.author.id == 2}">
				<div class="comment_author"><c:out value="${comment.author.displayName}"/></div>
			</c:if>
			
			<c:if test="${sessionScope.user.id == comment.author.id || sessionScope.user.role < 2}">
				<div class="delete_link"><a href="javascript:deleteComment(${comment.id})"><img src="/img/icons/x.png"></a></div>
			</c:if>
			
			<div class="comment_context"><xe:escape text="${comment.content}" /></div>
			
			<div class="comment_rating">
				<c:if test="${sessionScope.user != null}">
					<img src="/img/icons/comment_down.png" class="rating-button" id="commentRatingDown_${comment.id}" onclick="javascript:rateComment(${comment.id}, '-1');">
				</c:if>
				<span class="rating-text">
					<zmt:message key="rating_colon"/>
					<span id="comment_rating_sum_${comment.id}" class="rating_sum">${comment.ratingSum}</span>
					(<span id="comment_rating_count_${comment.id}" class="rating_text">${comment.ratingCount}</span>)
				</span>
				<c:if test="${sessionScope.user != null}">
					<img src="/img/icons/comment_up.png" class="rating-button" id="commentRatingUp_${comment.id}" onclick="javascript:rateComment(${comment.id}, '1');">
				</c:if>
			</div>
			<div class="comment_date">
				<c:choose>
	 				<c:when test="${comment.date.time gt now.time - 86400000}">
						<fmt:formatDate type="time" timeStyle="SHORT" value="${comment.date}"/>
					</c:when> 
					<c:otherwise>
						<fmt:formatDate type="date" dateStyle="SHORT" value="${comment.date}"/>
					</c:otherwise>
				</c:choose>
			</div>
			<div style="clear:both;"></div>
		</div>
	</div>