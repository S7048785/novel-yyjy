package com.novel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novel.dto.BookInfoDoc;
import com.novel.exception.BaseException;
import com.novel.po.Fetchers;
import com.novel.po.book.BookInfo;
import com.novel.po.book.BookInfoTable;
import com.novel.user.dto.book.BookInfoSearchView;
import com.novel.user.dto.book.BookInfoView;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.babyfish.jimmer.sql.JSqlClient;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class TestES {
	
	// 声明RestHighLevelClient
	private RestHighLevelClient client;
	
	@Autowired
	JSqlClient sqlClient;
	// 每个测试方法执行之前，初始化client
	@BeforeEach
	void setUp() {
		client = new RestHighLevelClient(RestClient.builder(
				HttpHost.create("http://192.168.229.128:9200") // 这里是es所在的虚拟机ip和端口
		));
	}
	// 每个测试方法执行后，释放资源
	@AfterEach
	void tearDown() throws Exception {
		client.close();
	}
	
	@Test
	void testGetIndex() throws IOException {
		// 创建Request对象
		GetIndexRequest items = new GetIndexRequest("book");
		// 发送GET请求
		boolean exists = client.indices().exists(items, RequestOptions.DEFAULT);
		System.out.println("exists = " + exists);
	}
	
	@Test
	void testGetDoc() throws IOException {
		// 创建request的同时指定index和文档id
		GetRequest request = new GetRequest("book", "1334318182169681920");
		// client发送get请求
		GetResponse res = client.get(request, RequestOptions.DEFAULT);
		// 获取原始文档source并转为JSON字符串
		String sourceAsString = res.getSourceAsString();
		// 再将JSON字符串转为ItemDoc对象
		//ObjectMapper mapper = new ObjectMapper()
		//		                      .registerModule(new ImmutableModule());
		//BookInfo deserializedTreeNode =
		//		mapper.readValue(sourceAsString, BookInfo.class);
		var doc = JSONUtil.toBean(sourceAsString, BookInfoDoc.class);
		System.out.println("doc = " + doc);
	}
	
	@Test
	void testBulkDoc() throws IOException {
		// 向数据库查询数据
		int pageNo = 1, pageSize = 500;
		while (pageNo == 1) {
			List<BookInfo> list = sqlClient.createQuery(BookInfoTable.$)
					                         .select(BookInfoTable.$.fetch(
							                         Fetchers.BOOK_INFO_FETCHER
									                         .authorId()
									                         .authorName()
									                         .bookName()
									                         .bookDesc()
									                         .bookStatus()
									                         .categoryId()
									                         .categoryName()
									                         .lastChapterId()
									                         .lastChapterName()
									                         .lastChapterUpdateTime()
									                         .picUrl()
									                         .score()
									                         .wordCount()
									                         .workDirection()
									                         .visitCount()
					                         ))
					                         .execute();
			//Page<Item> page = itemService.lambdaQuery()
			//		                  .eq(Item::getStatus, 1)
			//		                  .page(Page.of(pageNo, pageSize));
			//List<Item> records = page.getRecords();
			// 非空判断
			if (CollectionUtils.isEmpty(list)) {
				return;
			}
			log.info("加载第{}页数据，共{}条", pageNo, list.size());
			// 准备请求
			BulkRequest req = new BulkRequest("book");
			// 准备请求数据
			for (BookInfo record : list) {
				// 转换BookInfoDoc
				BookInfoDoc bookInfoDoc = BookInfoDoc.builder()
						.id(String.valueOf(record.id()))
						.workDirection(record.workDirection())
						.categoryId(record.categoryId())
						.categoryName(record.categoryName())
						.bookName(record.bookName())
						.bookDesc(record.bookDesc())
						.bookStatus(record.bookStatus())
						.authorId(record.authorId())
						.authorName(record.authorName())
						.lastChapterId(String.valueOf(record.lastChapterId()))
						.lastChapterName(record.lastChapterName())
						.lastChapterUpdateTime(record.lastChapterUpdateTime().atZone(ZoneId.systemDefault())
								                       .toInstant()
								                       .toEpochMilli())
						.picUrl(record.picUrl())
						.score(record.score())
						.wordCount(record.wordCount())
						.visitCount(record.visitCount())
						.build();
				// 创建新增文档的Request对象
				req.add(new IndexRequest()
						        .id(String.valueOf(record.id()))
						        .source(JSONUtil.toJsonStr(bookInfoDoc), XContentType.JSON));
			}
			// 发送请求
			BulkResponse res = client.bulk(req, RequestOptions.DEFAULT);
			// 打印结果
			log.info("批量新增文档结果: {}", res);
			//client.bulk(req, RequestOptions.DEFAULT);
			
			// 翻页
			pageNo++;
		}
	}
	
	private static final String INDEX_NAME = "book";
	
	private static final String FIELD_BOOK_NAME        = "bookName";
	@Test
	void testSearch() {
		try {
			SearchRequest request = new SearchRequest(INDEX_NAME);
			SearchSourceBuilder source = new SearchSourceBuilder()
					                             .query(QueryBuilders.matchQuery(FIELD_BOOK_NAME, "重生"));
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
		} catch (IOException e) {
			log.error("ES 查询失败, name={}", "重生", e);
			throw new BaseException("小说搜索失败，请稍后重试");
		}
	}
}
