package com.novel.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.novel.dto.BookChapterDto;
import com.novel.dto.req.BookChapterAddReq;
import com.novel.dto.req.ChapterPageQueryReq;
import com.novel.exception.BaseException;
import com.novel.po.Fetchers;
import com.novel.po.Tables;
import com.novel.po.book.*;
import com.novel.po.home.HomeBookTable;
import com.novel.po.user.UserBookshelfTable;
import com.novel.po.user.UserReadHistoryDraft;
import com.novel.result.PageResult;
import com.novel.user.dto.book.*;
import com.novel.user.dto.home.HomeBookView;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.util.Tuple;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author YYJY
 */
@Repository
public class BookRepository {
	@Autowired
	private JSqlClient sqlClient;
	private final BookInfoTable bookInfo = BookInfoTable.$;
	
	public BookInfoView findBookInfoById(long bookId) {
		BookInfoView book = sqlClient.findById(BookInfoView.class, bookId);
		return book;
	}
	
	/**
	 * 查询首页小说推荐
	 *
	 * @return
	 */
	public List<HomeBookView> listHomeBooks() {
		return sqlClient.createQuery(HomeBookTable.$)
				       .select(HomeBookTable.$.fetch(
						       HomeBookView.class
				       ))
				       .execute();
	}
	
	/**
	 * 查询首页小说点击排行
	 *
	 * @return
	 */
	public List<VisitRankBookView> listBookVisitRank(int count) {
		return sqlClient.createQuery(BookInfoTable.$)
				       .select(BookInfoTable.$.fetch(
						       VisitRankBookView.class
				       ))
				       .limit(count)
				       .execute();
	}
	
	/**
	 * 查询小说新书榜
	 *
	 * @return
	 */
	public List<HomeBookRankView> listNewestRankBooks() {
		/**
		 * SELECT book_name, score, visit_count
		 * FROM book_info
		 * ORDER BY score DESC, visit_count DESC, create_time
		 * LIMIT 50;
		 */
		
		return sqlClient.createQuery(BookInfoTable.$)
				       .orderBy(BookInfoTable.$.createTime())
				       .orderBy(BookInfoTable.$.score().desc())
				       .orderBy(BookInfoTable.$.visitCount().desc())
				       .select(BookInfoTable.$.fetch(HomeBookRankView.class))
				       .limit(10)
				       .execute();
	}
	
	/**
	 * 查询最近更新小说
	 *
	 * @return
	 */
	public List<LastUpdateBookView> listRecentUpdateBook() {
		/**
		 * select category_name, book_name, last_chapter_name, author_name, last_chapter_update_time
		 * from book_info as bi
		 * order by last_chapter_update_time desc
		 * limit 20
		 */
		return sqlClient.createQuery(BookInfoTable.$)
				       .orderBy(BookInfoTable.$.lastChapterUpdateTime().desc())
				       .select(BookInfoTable.$.fetch(LastUpdateBookView.class))
				       .limit(20)
				       .execute();
	}
	
	
	public List<LastInsertBookView> listNewestAddBooks() {
		/**
		 * select category_name, book_name, author_name, create_time, rand(create_time)
		 * from book_info
		 * order by create_time desc
		 * limit 20
		 */
		return sqlClient.createQuery(BookInfoTable.$)
				       .orderBy(BookInfoTable.$.createTime().desc())
				       .select(BookInfoTable.$.fetch(LastInsertBookView.class))
				       .limit(20)
				       .execute();
	}
	
	public List<BookCategoryView> listCategory(Integer id) {
		return sqlClient.createQuery(BookCategoryTable.$)
				       .where(BookCategoryTable.$.workDirection().eqIf(id != null, id))
				       .select(BookCategoryTable.$.fetch(BookCategoryView.class))
				       .execute();
	}
	
	/**
	 * 条件查询全部小说
	 *
	 * @param params
	 * @return
	 */
	public PageResult<AllBookView> listAll(AllBookQueryInput params, int pageNum, int pageSize) {
		long wordCountMin = 0L, wordCountMax = Integer.MAX_VALUE;
		
		int wordCountState = params.getWordCountState() == null ? -1 : params.getWordCountState();
		wordCountMax = switch (wordCountState) {
			case 0 -> 300000;
			case 1 -> {
				wordCountMin = 300000;
				yield 500000;
			}
			case 2 -> {
				wordCountMin = 500000;
				yield 1000000;
			}
			case 3 -> {
				wordCountMin = 1000000;
				yield Integer.MAX_VALUE;
			}
			default -> wordCountMax;
		};
		
		Order order = null;
		switch (params.getSortState() == null ? -1 : params.getSortState()) {
			case 0 -> order = bookInfo.lastChapterUpdateTime().desc();
			case 1 -> order = bookInfo.wordCount().desc();
			case 2 -> order = bookInfo.visitCount().desc();
			default -> {
			}
		}
		
		var page = sqlClient.createQuery(bookInfo)
				           .where(bookInfo.workDirection().eqIf(params.getChannelId()))
				           .where(bookInfo.categoryId().eqIf(params.getCategoryId() != null, Long.valueOf(params.getCategoryId())))
				           .where(bookInfo.bookStatus().eqIf(params.getOverState()))
				           .where(bookInfo.wordCount().betweenIf(wordCountMin, wordCountMax))
				           .where(bookInfo.lastChapterUpdateTime().ltIf(params.getUpdateDay() != null, LocalDateTime.now().minusDays(params.getUpdateDay() == null ? 0 : params.getUpdateDay())))
				           .orderByIf(order != null, order)
				           .select(BookInfoTable.$.fetch(AllBookView.class))
				           .fetchPage(pageNum - 1, pageSize);
		return new PageResult<>(pageNum, pageSize, page.getTotalRowCount(), page.getRows());
	}
	
	
	/**
	 * 获取小说的所有章节
	 *
	 * @param id
	 * @return
	 */
	public BookChapterDto listBookChapter(long id, Integer isAll, Integer orderBy) {
		List<BookChapterView> execute = sqlClient.createQuery(BookChapterTable.$)
				                                .where(BookChapterTable.$.bookId().eq(Long.valueOf(id)))
				                                .orderBy(orderBy == 1 ? BookChapterTable.$.id().desc() : BookChapterTable.$.id().asc())
				                                .select(BookChapterTable.$.fetch(BookChapterView.class))
				                                .limit(isAll == 0 ? 20 : 5000)
				                                .execute();
		if (CollectionUtils.isEmpty(execute)) {
			throw new BaseException("该小说不存在!");
		}
		Long count = sqlClient.createQuery(BookChapterTable.$)
				             .where(BookChapterTable.$.bookId().eq(id))
				             .selectCount()
				             .execute().get(0);
		return new BookChapterDto(execute, count);
	}
	
	public List<BookInfo> listRandomBooks(long categoryId, long bookId) {
		List<BookInfo> execute = sqlClient.createQuery(bookInfo)
				                         .where(bookInfo.categoryId().eqIf(categoryId))
				                         .where(bookInfo.id().ne(bookId))
				                         .orderBy(Predicate.sql(
						                         "RAND()"
				                         ))
				                         .select(bookInfo.fetch(
						                         Fetchers.BOOK_INFO_FETCHER
								                         .bookName()
								                         .picUrl()
								                         .bookDesc()
				                         ))
				                         .limit(3)
				                         .execute();
		return execute;
	}
	
	public List<BookRankView> listRankBooks(Order order) {
		List<BookRankView> execute = sqlClient.createQuery(bookInfo)
				                             .orderBy(order)
				                             .select(bookInfo.fetch(BookRankView.class))
				                             .limit(30)
				                             .execute();
		return execute;
	}
	
	public BookChapterContentView getBookContent(long bookId, long chapterId) {
		List<BookChapterContentView> execute = sqlClient.createQuery(BookChapterTable.$)
				                                       .where(BookChapterTable.$.bookId().eq(bookId))
				                                       .where(BookChapterTable.$.id().eq(chapterId))
				                                       .select(BookChapterTable.$.fetch(BookChapterContentView.class))
				                                       .execute();
		if (CollectionUtils.isEmpty(execute)) {
			throw new BaseException("该章节不存在!");
		}
		return execute.get(0);
	}
	
	public Tuple<String, String> getChapter(long bookId, long chapterNum) {
		List<Long> nextChapterId = sqlClient.createQuery(BookChapterTable.$)
				                           .where(BookChapterTable.$.bookId().eq(bookId))
				                           .where(BookChapterTable.$.chapterNum().eq((int) (chapterNum + 1)))
				                           .select(BookChapterTable.$.id())
				                           .limit(1)
				                           .execute();
		List<Long> prevChapterId = sqlClient.createQuery(BookChapterTable.$)
				                           .where(BookChapterTable.$.bookId().eq(bookId))
				                           .where(BookChapterTable.$.chapterNum().eq((int) (chapterNum - 1)))
				                           .select(BookChapterTable.$.id())
				                           .limit(1)
				                           .execute();
		String next = String.valueOf(nextChapterId.isEmpty() ? null : nextChapterId.get(0));
		String prev = String.valueOf(prevChapterId.isEmpty() ? null : prevChapterId.get(0));
		
		return new Tuple<>(next, prev);
		
	}
	
	public void updateBookReadHistory(Long userId, long bookId, long chapterId) {
		sqlClient.save(UserReadHistoryDraft.$.produce(draft -> {
			draft.setUserId(userId)
					.setBookId(bookId)
					.setPreContentId(chapterId)
					.setUpdateTime(LocalDateTime.now());
		}), SaveMode.UPSERT);
		
	}
	
	public boolean updateBookshelf(long bookId, long userId, long chapterId) {
		
		boolean exists = isInBookshelf(userId, bookId);
		
		if (exists) {
			sqlClient.createUpdate(UserBookshelfTable.$)
					.where(UserBookshelfTable.$.bookId().eq(bookId))
					.where(UserBookshelfTable.$.userId().eq(userId))
					.set(UserBookshelfTable.$.preContentId(), chapterId)
					.execute();
		}
		
		return exists;
	}
	
	public boolean isInBookshelf(Long userId, long bookId) {
		// 是否在书架中
		return sqlClient.createQuery(UserBookshelfTable.$)
				       .where(UserBookshelfTable.$.bookId().eq(bookId))
				       .where(UserBookshelfTable.$.userId().eq(userId))
				       .where(UserBookshelfTable.$.state().eq(0))
				       .exists();
	}
	
	public List<BookInfoSearchView> search(String name) {
		return sqlClient.createQuery(BookInfoTable.$)
				       .where(Predicate.or(
						       BookInfoTable.$.bookName().like(name),
						       BookInfoTable.$.authorName().like(name)
				       ))
				       .select(BookInfoTable.$.fetch(BookInfoSearchView.class))
				       .execute();
	}
	
	public PageResult<BookChapterView> pageForAdmin(ChapterPageQueryReq req) {
		BookChapterTable table = BookChapterTable.$;
		var page = sqlClient.createQuery(table)
				                          .where(table.id().eqIf(req.getChapterId()))
				                          .where(table.chapterName().likeIf(req.getChapterName()))
				                          .where(table.book().bookName().eqIf((req.getBookName())))
				                          .where(table.createTime().between(req.getStartTime(), req.getEndTime()))
				                          .orderBy(table.id().desc())
				                          .select(table.fetch(BookChapterView.class))
				                          .fetchPage(req.getPageNum() - 1, req.getPageSize());
		return new PageResult<>(req.getPageNum(), req.getPageSize(), page.getTotalRowCount(), page.getRows());
	}
	
	@Transactional
	public void addForAdmin(BookChapterAddReq req) {
		// 查询章节数
		List<Long> execute = sqlClient.createQuery(BookChapterTable.$)
				                     .where(BookChapterTable.$.bookId().eq(req.getBookId()))
				                     .selectCount()
				                     .execute();
		Long first = CollUtil.getFirst(execute);
		var chapterDraft = BookChapterDraft.$.produce(draft -> {
			draft.setBookId(req.getBookId())
					.setChapterName(req.getChapterName())
					.setChapterNum(first == null ? 1 : (int)(long) first)
					.setVipState(0)
					.setWordCount(req.getContent().length());
		});
		BookChapter modifiedEntity = sqlClient.save(chapterDraft).getModifiedEntity();
		var contentDraft = BookContentDraft.$.produce(draft -> {
			draft.setContent(req.getContent())
					.setChapterId(modifiedEntity.id())
					.setContent(req.getContent());
		});
		sqlClient.save(contentDraft);
		
		// 更新章节数量和最新章节信息
		sqlClient.createUpdate(bookInfo)
				.where(bookInfo.id().eq(req.getBookId()))
				.set(bookInfo.lastChapterId(), BigInteger.valueOf(modifiedEntity.id()))
				.set(bookInfo.lastChapterName(), req.getChapterName())
				.set(bookInfo.lastChapterUpdateTime(), modifiedEntity.updateTime())
				.set(bookInfo.updateTime(), modifiedEntity.updateTime())
				.set(bookInfo.wordCount(), bookInfo.wordCount().plus((long) modifiedEntity.wordCount()))
				.execute();
	}
	
	public void updateForAdmin(BookChapterAddReq req) {
		
		BookInfo byId = sqlClient.findById(BookInfo.class, req.getBookId());
		int wordCount = 0;
		if (byId != null) {
			wordCount = (int)byId.wordCount() - req.getContent().length();
		}
		sqlClient.createUpdate(BookChapterTable.$)
				.where(BookChapterTable.$.id().eq(req.getBookId()))
				.set(BookChapterTable.$.chapterName(), req.getChapterName())
				.set(BookChapterTable.$.updateTime(), LocalDateTime.now())
				.set(BookChapterTable.$.content().content(), req.getContent())
				.set(BookChapterTable.$.wordCount(), BookChapterTable.$.wordCount().plus(wordCount))
				.execute();
	}
}
