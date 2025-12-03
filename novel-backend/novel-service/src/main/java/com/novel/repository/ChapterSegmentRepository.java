package com.novel.repository;

import cn.dev33.satoken.stp.StpUtil;
import com.novel.dto.req.SegmentCommentPageQueryReq;
import com.novel.po.Fetchers;
import com.novel.po.chapterSummary.ChapterSummary;
import com.novel.po.chapterSummary.ChapterSummaryDraft;
import com.novel.po.chapterSummary.ChapterSummaryTable;
import com.novel.po.comment.*;
import com.novel.po.user.UserInfo;
import com.novel.po.user.UserInfoDraft;
import com.novel.po.user.UserInfoTable;
import com.novel.result.PageResult;
import com.novel.user.dto.comment.AddChapterComment;
import com.novel.user.dto.comment.SegmentCommentView;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ChapterSegmentRepository {
	@Lazy
	@Autowired
	private ChapterSegmentRepository self;
	@Autowired
	private JSqlClient sqlClient;
	private final ChapterSummaryTable table = ChapterSummaryTable.$;
	private final SegmentCommentTable commentTable = SegmentCommentTable.$;
	
	public List<ChapterSummary> getCommentSummaryList(long bookId, long chapterId) {
		return sqlClient.createQuery(table)
				       .where(table.bookId().eq(bookId))
				       .where(table.chapterId().eq(chapterId))
				       .select(table.fetch(Fetchers.CHAPTER_SUMMARY_FETCHER.segmentNum().commentNum()))
				       .execute();
	}
	
	@Transactional
	public SegmentCommentView addChapterSummary(String ip, String address, AddChapterComment comment) {
		long currentId = StpUtil.getLoginIdAsLong();
		
		// 查询总数
		Long count = sqlClient.createQuery(commentTable)
				         .where(commentTable.chapterId().eq(comment.getChapterId()))
				         .where(commentTable.segmentNum().eq(comment.getSegmentNum()))
				         .selectCount().execute().get(0);

// 插入一条段落评论
		SegmentComment modifiedEntity = sqlClient.save(SegmentCommentDraft.$.produce(draft -> {
			draft.setChapterId(comment.getChapterId())
					.setUserId(currentId)
					.setIp(ip)
					.setIpAddress(address)
					.setSegmentNum(comment.getSegmentNum())
					.setLevel((int) (count + 1))
					.setCreateTime(LocalDateTime.now())
					.setLikeCount(0)
					.setContent(comment.getContent());
		}), SaveMode.INSERT_ONLY).getModifiedEntity();
		
		// 更新或创建章节摘要
		self.updateOrCreateChapterSummary(comment);
		
		// 查询用户信息
		UserInfo userInfo = sqlClient.createQuery(UserInfoTable.$).where(UserInfoTable.$.id().eq(currentId)).select(UserInfoTable.$).execute().get(0);
		SegmentCommentView segmentCommentView = new SegmentCommentView(modifiedEntity);
		segmentCommentView.setNickName(userInfo.nickName());
		segmentCommentView.setAvatar(userInfo.userPhoto());
		return segmentCommentView;
	}
	
	@Transactional
	public void updateOrCreateChapterSummary(AddChapterComment comment) {
		List<ChapterSummary> summaries = sqlClient.createQuery(table)
				                                 .where(table.bookId().eq(comment.getBookId()))
				                                 .where(table.chapterId().eq(comment.getChapterId()))
				                                 .select(table)
				                                 .execute();
		
		if (summaries.isEmpty()) {
			sqlClient.save(ChapterSummaryDraft.$.produce(draft -> {
				draft.setBookId(comment.getBookId())
						.setChapterId(comment.getChapterId())
						.setSegmentNum(comment.getSegmentNum())
						.setCommentNum(1);
			}));
		} else {
			ChapterSummary summary = summaries.get(0);
			sqlClient.createUpdate(table)
					.where(table.id().eq(summary.id()))
					.set(table.commentNum(), table.commentNum().plus(1L))
					.execute();
		}
	}
	
	public PageResult<SegmentCommentView> listSegmentComment(SegmentCommentPageQueryReq query) {
		var page = sqlClient.createQuery(commentTable)
				           .where(
						           commentTable.chapterId().eq(query.getChapterId()),
						           commentTable.segmentNum().eq(query.getSegmentNum())
				           )
				           .orderBy(commentTable.level().desc())
				           .select(commentTable.fetch(SegmentCommentView.class))
				           .fetchPage(query.getPageNum() - 1, query.getPageSize());
		return new PageResult<>(query.getPageNum(), query.getPageSize(), page.getTotalRowCount(), page.getRows());
	}
	
	public void like(long commentId) {
		// 查询是否点赞
		long userId = StpUtil.getLoginIdAsLong();
		sqlClient.findById(SegmentComment.class, commentId);
		
	}
}
