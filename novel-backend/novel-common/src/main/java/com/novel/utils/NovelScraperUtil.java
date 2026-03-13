package com.novel.utils;

import com.novel.bo.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NovelScraperUtil {
    
    private static final RestTemplate restTemplate = new RestTemplate();
    
    // --- 功能 1: 爬取小说详情 ---
    public static BookDetailResult scrapeBookDetail(String bookId) {
        String url = String.format("http://117.72.165.13:8888/book/%s.html", bookId);
        String html = restTemplate.getForObject(url, String.class);
        if (html == null) throw new RuntimeException("获取 URL 失败：" + url);
        
        Document doc = Jsoup.parse(html);
        BookDetailResult result = new BookDetailResult();
        
        // 1. 封面 URL
        Element coverImg = doc.selectFirst("img.cover");
        result.setPicUrl(coverImg != null ? coverImg.attr("src") : "");
        
        // 2. 书名
        Element titleH1 = doc.selectFirst("div.tit h1");
        result.setBookName(titleH1 != null ? titleH1.text().trim() : "");
        
        // 3. 作者
        Element authorA = doc.selectFirst("div.tit a.author");
        if (authorA != null) {
            result.setAuthorName(authorA.text().trim().replace(" 著", ""));
        }
        
        // 4. 分类 (优先从面包屑导航找，其次从列表找)
        Element navSub = doc.selectFirst("div.nav_sub");
        if (navSub != null) {
            Elements links = navSub.select("a");
            if (links.size() >= 2) {
                result.setCategoryName(links.get(1).text().trim());
            }
        }
        if (result.getCategoryName() == null || result.getCategoryName().isEmpty()) {
            Element categoryEm = doc.selectFirst("ul.list li span.item:contains(类别) em");
            if (categoryEm != null) {
                result.setCategoryName(categoryEm.text().trim());
            }
        }
        
        // 5. 简介
        Element introP = doc.selectFirst("div.intro_txt p");
        result.setBookDesc(introP != null ? introP.text().trim() : "");
        
        // 6. 章节数
        Element indexCountEm = doc.selectFirst("#bookIndexCount");
        result.setChapterCount(extractNumber(indexCountEm != null ? indexCountEm.text().replaceAll("[^\\d]", "") : "0"));
        
        // 6. 点击量 (提取数字)
        //Element visitEm = doc.selectFirst("em#cTotal");
        //result.setVisitCount(extractNumber(visitEm != null ? visitEm.text() : "0"));
        
        // 7. 评论量
        //Element commentSpan = doc.selectFirst("span#bookCommentTotal");
        //result.setCommentCount(extractNumber(commentSpan != null ? commentSpan.text() : "0"));
        
        // 8. 总字数
        //Element wordEm = doc.selectFirst("ul.list span.item:contains(总字数) em");
        //result.setWordCount(extractNumber(wordEm != null ? wordEm.text() : "0"));
        
        // 9. 最新章节
        //Element lastChapterA = doc.selectFirst("span.fl.font16 a");
        //result.setLastChapter(lastChapterA != null ? lastChapterA.text().trim() : "");
        
        return result;
    }
    
    /**
     * 爬取章节列表并获取指定数量的内容
     * @param bookId 小说id
     * @param start   起始索引
     * @param count   获取数量 (0 表示获取所有)
     * @return 章节列表
     */
    public static List<ChapterResult> scrapeChapters(String bookId, int start, int count, ProgressCallback callback) throws Exception {
        String listUrl = String.format("http://117.72.165.13:8888/book/indexList-%s.html", bookId);
        String html = restTemplate.getForObject(listUrl, String.class);
        if (html == null) throw new RuntimeException("获取 URL 失败：" + listUrl);
        
        Document doc = Jsoup.parse(html);
        Element dirList = doc.selectFirst("div.dirList");
        if (dirList == null) throw new RuntimeException("未找到章节列表容器");
        
        Elements links = dirList.select("a[href]");
        List<ChapterResult> results = new ArrayList<>();
        
        // 限制数量
        int limit = count == 0 ? links.size() : Math.min(start + count, links.size());
        
        for (int i = start, j = 0; i < limit; i++, j++) {
            Element link = links.get(i);
            String relativeHref = link.attr("href");
            // 拼接绝对 URL (处理相对路径)
            String absoluteUrl = resolveUrl(listUrl, relativeHref);
            
            try {
                ChapterResult chapterData = scrapeSingleChapter(absoluteUrl, i + 1);
                results.add(chapterData);
                // 回调进度 (每4章更新一次)
                if (j % 4 == 0) {
                    callback.onProgressUpdate(i + 1, limit, (double) i / limit * 100);
                }
	            log.info("爬取第 {} 章成功：{}", i + 1, chapterData.getBookChapter().getChapterName());
            } catch (Exception e) {
                log.error("爬取第 {} 章时出错：{}", i + 1, e.getMessage());
                // 可以选择继续或抛出异常
            }
        }
        callback.onProgressUpdate(limit, limit, 1.0);
        return results;
    }
    
    // --- 辅助方法：爬取单章内容 ---
    private static ChapterResult scrapeSingleChapter(String url, int chapterNum) {
        String html = restTemplate.getForObject(url, String.class);
        if (html == null) throw new RuntimeException("获取章节失败：" + url);
        
        Document doc = Jsoup.parse(html);
        Element readContent = doc.selectFirst("div#readcontent");
        if (readContent == null) throw new RuntimeException("未找到内容 div");
        
        ChapterResult result = new ChapterResult();
        ChapterInfo info = new ChapterInfo();
        ContentInfo contentInfo = new ContentInfo();
        
        // 1. 章节名
        Element h1 = readContent.selectFirst("h1");
        info.setChapterName(h1 != null ? h1.text().trim() : "未知章节");
        info.setChapterNum(chapterNum);
        
        // 2. 字数
        Element textInfo = readContent.selectFirst("div.textinfo");
        if (textInfo != null) {
            Element wordSpan = textInfo.selectFirst("span:contains(字数)");
            if (wordSpan != null) {
                info.setWordCount(extractNumber(wordSpan.text()));
            } else {
                info.setWordCount(0);
            }
        } else {
            info.setWordCount(0);
        }
        
        // 3. 正文内容 (核心逻辑：提取文本并包裹 <p>)
        Element readBox = doc.selectFirst("div.readBox");
        StringBuilder contentBuilder = new StringBuilder();
        
        if (readBox != null) {
            // 获取所有子节点，模拟 Python 的 descendants 逻辑
            // Jsoup 的 ownText() 只获取直接文本，我们需要遍历
            // 简单策略：获取所有 p, div, br 标签，或者提取文本行
            
            // 策略优化：Jsoup 默认会保留一定的结构。
            // 我们遍历 readBox 下的所有元素，提取文本并按行处理
            
            // 为了更精准地还原 Python 逻辑（按块分割），我们可以直接获取 textNodes 或者按标签处理
            // 这里采用一种稳健的方式：获取整个 readBox 的文本，按换行符分割，再过滤空行
            // 注意：Jsoup 的 .text() 会把所有换行压缩成一个空格。
            // 我们需要保留原始换行结构，所以最好遍历子元素。
            
            List<String> lines = new ArrayList<>();
            for (var elem : readBox.childNodes()) {
                // 获取该元素下的文本，去除首尾空白，如果内部有多个空格则压缩
                if (elem instanceof TextNode textNode) {
	                String text = textNode.text().trim();
                    lines.add(text);
                }
                
            }
            
            // 如果 children 没拿到（可能文本直接在 readBox 下），fallback 到 textNodes
            if (lines.isEmpty()) {
                String fullText = readBox.ownText().trim();
                if (!fullText.isEmpty()) {
                    // 尝试按换行分割，虽然 Jsoup 解析时可能已经丢失了一些
                    String[] split = fullText.split("\\n");
                    for (String s : split) {
                        if (!s.trim().isEmpty()) lines.add(s.trim());
                    }
                }
            }
            
            // 组装 HTML: 每一行用 <p> 包裹
            for (String line : lines) {
                contentBuilder.append("<p>").append(line).append("</p>\n");
            }
        }
        
        contentInfo.setContent(contentBuilder.toString().trim());
        
        result.setBookChapter(info);
        result.setBookContent(contentInfo);
        return result;
    }
    
    // --- 工具方法 ---
    
    // 提取字符串中的数字
    private static int extractNumber(String text) {
        if (text == null) return 0;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }
    
    // 解析相对 URL 为绝对 URL
    private static String resolveUrl(String baseUrl, String relativeUrl) throws URISyntaxException {
        if (relativeUrl.startsWith("http")) {
            return relativeUrl;
        }
        URI base = new URI(baseUrl);
        // 简单处理：如果相对路径以 / 开头，则是根路径
        if (relativeUrl.startsWith("/")) {
            return base.getScheme() + "://" + base.getHost() + ":" + base.getPort() + relativeUrl;
        }
        // 否则拼接
        String path = base.getPath();
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash > 0) {
            String basePath = path.substring(0, lastSlash + 1);
            return base.getScheme() + "://" + base.getHost() + ":" + base.getPort() + basePath + relativeUrl;
        }
        return baseUrl + "/" + relativeUrl;
    }
}
