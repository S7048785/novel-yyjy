package com.novel.repository;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.novel.admin.dto.user.UserInfoView;
import com.novel.admin.dto.user.UserUpdateInput;
import com.novel.constant.MessageConstant;
import com.novel.dto.req.UserPageQueryReq;
import com.novel.exception.BaseException;
import com.novel.po.user.*;
import com.novel.result.PageResult;
import com.novel.user.dto.user.BookReadHistoryView;
import com.novel.user.dto.user.UserBookShelfView;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserRepository {
	
	@Autowired
	private JSqlClient sqlClient;
	
	private final UserInfoTable userTable = UserInfoTable.$;
	
	public UserInfo getUserInfo(long userId) {
		List<UserInfo> execute = sqlClient.createQuery(userTable)
				                         .where(userTable.id().eq(Long.valueOf(userId)))
				                         .select(userTable)
				                         .execute();
		if (CollUtil.isEmpty(execute)) {
			throw new BaseException(MessageConstant.ACCOUNT_OR_PASSWORD_NOT_FOUND);
		}
		return execute.get(0);
	}
	
	public UserInfo getUserInfoByEmail(String email) {
		List<UserInfo> execute = sqlClient.createQuery(userTable)
				                         .where(userTable.email().eq(email))
				                         .select(userTable)
				                         .execute();
		if (CollUtil.isEmpty(execute)) {
			throw new BaseException(MessageConstant.ACCOUNT_OR_PASSWORD_NOT_FOUND);
		}
		return execute.get(0);
	}
	
	public void isExist(String email, String nickName) {
		List<UserInfo> execute = sqlClient.createQuery(userTable)
				                         .where(Predicate.or(
						                         userTable.email().eq(email),
						                         userTable.nickName().eq(nickName)
				                         ))
				                         .select(userTable)
				                         .execute();
		if (CollUtil.isEmpty(execute)) {
			return;
		}
		UserInfo userInfo = execute.get(0);
		if (userInfo.email().equals(email)) {
			throw new BaseException(MessageConstant.EMAIL_ALREADY_EXISTS);
		}
		if (userInfo.nickName().equals(nickName)) {
			throw new BaseException(MessageConstant.NICK_NAME_ALREADY_EXISTS);
		}
	}
	
	public UserInfo save(UserInfo produce) {
		return sqlClient.save(produce, SaveMode.INSERT_ONLY).getModifiedEntity();
	}
	
	public void updateAvatar(String url) {
		Long userId = StpUtil.getLoginIdAsLong();
		sqlClient.createUpdate(userTable)
				.set(userTable.userPhoto(), url)
				.where(userTable.id().eq(userId))
				.execute();
	}
	
	public void updateNickName(Long userId, String nickName) {
		Integer execute = sqlClient.createUpdate(userTable)
				                  .set(userTable.nickName(), nickName)
				                  .where(userTable.id().eq(userId))
				                  .execute();
		if (execute == 0) {
			throw new BaseException(MessageConstant.USER_NOT_FOUND);
		}
	}
	
	public List<BookReadHistoryView> listHistory() {
		Long userId = StpUtil.getLoginIdAsLong();
		UserReadHistoryTable table = UserReadHistoryTable.$;
		
		return sqlClient.createQuery(table)
				       .where(table.userId().eq(userId))
				       .orderBy(table.updateTime().desc())
				       .select(table.fetch(BookReadHistoryView.class))
				       .limit(20)
				       .execute();
	}
	
	public List<UserBookShelfView> listBookshelf() {
		Long userId = StpUtil.getLoginIdAsLong();
		return sqlClient.createQuery(UserBookshelfTable.$)
				       .where(UserBookshelfTable.$.userId().eq(userId))
				       .where(UserBookshelfTable.$.state().eq(0))
				       .orderBy(UserBookshelfTable.$.updateTime().desc())
				       .select(UserBookshelfTable.$.fetch(UserBookShelfView.class))
				       .limit(20)
				       .execute();
	}
	
	public void updateBookshelf(int optType, Long userId, long bookId) {
		List<Long> execute = sqlClient.createQuery(UserReadHistoryTable.$)
				                     .where(UserReadHistoryTable.$.userId().eq(userId))
				                     .where(UserReadHistoryTable.$.bookId().eq(bookId))
				                     .select(UserReadHistoryTable.$.preContentId())
				                     .execute();
		
		sqlClient.save(UserBookshelfDraft.$.produce(draft -> {
			draft.setUserId(userId)
					.setBookId(bookId)
					.setState(optType == 0 ? 0 : 1)
					.setUpdateTime(LocalDateTime.now())
					.setPreContentId(CollUtil.isEmpty(execute) ? null : execute.get(0));
		}), SaveMode.UPSERT);
	}
	
	/**
	 * 获取用户角色
	 *
	 * @param l
	 * @return
	 */
	public List<String> getUserRole(long l) {
		return sqlClient.createQuery(userTable)
				       .where(userTable.id().eq(l))
				       .select(userTable.role())
				       .execute();
	}
	
	public void updateUser(UserUpdateInput user) {
		sqlClient.update(user);
	}
	
	public void delete(Long id) {
		sqlClient.deleteById(UserInfo.class, id);
	}
	
	public PageResult<UserInfoView> page(UserPageQueryReq req) {
		PropExpression order = switch (req.getSortField()) {
			case 0 -> userTable.id();
			case 1 -> userTable.email();
			case 2 -> userTable.createTime();
			default -> null;
		};
		
		Page<UserInfoView> userInfoViewPage = sqlClient.createQuery(userTable)
				                                      .where(
						                                      Predicate.and(
								                                      userTable.id().eqIf(req.getUserId()),
								                                      userTable.email().likeIf(req.getEmail()),
								                                      userTable.status().eqIf(req.getStatus()),
								                                      userTable.createTime().betweenIf(req.getStartTime(), req.getEndTime())
						                                      )
				                                      )
				                                      .orderBy(order)
				                                      .select(userTable.fetch(UserInfoView.class))
				                                      .fetchPage(req.getPageNum() - 1, req.getPageSize());
		return new PageResult<>(req.getPageNum(), req.getPageSize(), userInfoViewPage.getTotalRowCount(), userInfoViewPage.getRows());
	}
}
