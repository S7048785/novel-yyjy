package com.novel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.json.JSONUtil;
import com.novel.dto.BookInfoDoc;
import com.novel.exception.BaseException;
import com.novel.result.PageResult;
import com.novel.service.SearchService;
import com.novel.user.dto.book.AllBookQueryInput;
import com.novel.user.dto.book.AllBookView;
import com.novel.user.dto.book.BookInfoSearchView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(prefix = "spring.elasticsearch", name = "enable", havingValue = "true")
@Slf4j
@Service
@RequiredArgsConstructor
public class ESServiceImpl implements SearchService {
	
	/* ---------- 常量 ---------- */
	private static final String INDEX_NAME = "book";
	private static final String FIELD_BOOK_NAME        = "bookName";
	private static final String FIELD_CATEGORY_ID      = "categoryId";
	private static final String FIELD_WORK_DIRECTION   = "workDirection";
	private static final String FIELD_BOOK_STATUS      = "bookStatus";
	private static final String FIELD_WORD_COUNT       = "wordCount";
	private static final String FIELD_LAST_UPDATE_TIME = "lastChapterUpdateTime";
	private static final String FIELD_VISIT_COUNT      = "visitCount";
	
	
	private final RestHighLevelClient client;
	@Override
	public PageResult<AllBookView> conditionSearchBooks(AllBookQueryInput condition,
	                                                    int pageNum,
	                                                    int pageSize) {
		try {
			SearchRequest request = buildSearchRequest(condition, pageNum, pageSize);
			SearchResponse response = client.search(request, RequestOptions.DEFAULT);
			return parseResponse(response, pageNum, pageSize);
		} catch (IOException e) {
			log.error("ES 查询失败, condition={}", condition, e);
			throw new BaseException("小说搜索失败，请稍后重试");
		}
	}
	
	@Override
	public List<BookInfoSearchView> searchBooksByName(String name) {
		try {
			SearchRequest request = new SearchRequest(INDEX_NAME);
			SearchSourceBuilder source = new SearchSourceBuilder()
					                             .query(QueryBuilders.matchQuery(FIELD_BOOK_NAME, name));
			request.source(source);
			// 构建高亮条件
			request.source().highlighter(
					SearchSourceBuilder.highlight()
							.field(FIELD_BOOK_NAME)
			);
			SearchResponse response = client.search(request, RequestOptions.DEFAULT);
			List<BookInfoSearchView> list = new ArrayList<>();
			for (SearchHit hit : response.getHits().getHits()) {
				// 获取高亮结果
				Map<String, HighlightField> highlightFields = hit.getHighlightFields();
				String hfName = null;
				// 判断非空
				if (CollectionUtil.isNotEmpty(highlightFields)) {
					// 获取高亮结果
					HighlightField hf = highlightFields.get(FIELD_BOOK_NAME);
					if (hf != null) {
						// 拼接所有高亮结果片段，也就是商品名称的高亮值
						StringBuilder stringBuilder = new StringBuilder();
						for (Text fragment : hf.getFragments()) {
							stringBuilder.append(fragment.string());
						}
						hfName = stringBuilder.toString();
						// 替换原始文档中的商品名称为高亮值
						log.info("高亮结果: {}", hfName);
					}
				}
				BookInfoSearchView view = JSONUtil.toBean(hit.getSourceAsString(), BookInfoSearchView.class);
				if (hfName != null) {
					view.setBookName(hfName);
				}
				list.add(view);
			}
			return list;
		} catch (IOException e) {
			log.error("ES 查询失败, name={}", name, e);
			throw new BaseException("小说搜索失败，请稍后重试");
		}
	}
	
	/* ======================== 私有方法 ======================== */
	
	private SearchRequest buildSearchRequest(AllBookQueryInput c, int pageNum, int pageSize) {
		BoolQueryBuilder bool = QueryBuilders.boolQuery();
		if (c.getBookName() != null) {
			bool.must(QueryBuilders.matchQuery(FIELD_BOOK_NAME, c.getBookName()));
		}
		if (c.getCategoryId() != null) {
			bool.must(QueryBuilders.matchQuery(FIELD_CATEGORY_ID, c.getCategoryId()));
		}
		if (c.getChannelId() != null) {
			bool.must(QueryBuilders.matchQuery(FIELD_WORK_DIRECTION, c.getChannelId()));
		}
		if (c.getOverState() != null) {
			bool.must(QueryBuilders.matchQuery(FIELD_BOOK_STATUS, c.getOverState()));
		}
		if (c.getWordCountState() != null) {
			Tuple range = convertWordCountRange(c.getWordCountState());
			bool.must(QueryBuilders.rangeQuery(FIELD_WORD_COUNT)
					          .gte(range.get(0))
					          .lte(range.get(1)));
		}
		if (c.getUpdateDay() != null) {
			long threshold = LocalDateTime.now()
					                 .minusDays(c.getUpdateDay())
					                 .atZone(ZoneId.systemDefault())
					                 .toInstant().toEpochMilli();
			bool.must(QueryBuilders.rangeQuery(FIELD_LAST_UPDATE_TIME).lt(threshold));
		}
		
		SearchSourceBuilder source = new SearchSourceBuilder()
				                             .query(bool)
				                             .from((pageNum - 1) * pageSize)
				                             .size(pageSize);
		
		/* 排序 */
		if (c.getSortState() != null) {
			switch (c.getSortState()) {
				case 0 -> source.sort(FIELD_LAST_UPDATE_TIME, SortOrder.DESC);
				case 1 -> source.sort(FIELD_WORD_COUNT, SortOrder.DESC);
				case 2 -> source.sort(FIELD_VISIT_COUNT, SortOrder.DESC);
			}
		}
		
		return new SearchRequest(INDEX_NAME).source(source);
	}
	
	private PageResult<BookInfoDoc> parseResponse(SearchResponse resp, int pageNum, int pageSize) {
		SearchHits hits = resp.getHits();
		long total = hits.getTotalHits().value;
		List<BookInfoDoc> list = new ArrayList<>();
		for (SearchHit hit : hits.getHits()) {
			BookInfoDoc view = JSONUtil.toBean(hit.getSourceAsString(), BookInfoDoc.class);
			list.add(view);
		}
		return new PageResult<>(pageNum, pageSize, total, list);
	}
	
	/* 字数范围转换 */
	private Tuple convertWordCountRange(Integer state) {
		int min = 0, max = Integer.MAX_VALUE;
		if (state == null) return new Tuple(min, max);
		return switch (state) {
			case 0 -> new Tuple(0, 300_000);
			case 1 -> new Tuple(0, 500_000);
			case 2 -> new Tuple(300_000, 500_000);
			case 3 -> new Tuple(500_000, max);
			default -> new Tuple(min, max);
		};
	}
}
