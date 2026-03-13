package com.novel.dto.req;

import lombok.Data;

/**
 * 小说分页查询请求
 */
@Data
public class BookPageQueryReq {

    /** 小说名 */
    String bookName;

    /** 作者名 */
    String authorName;

    /** 类别ID */
    Long categoryId;

    /** 书籍状态 0-连载中 1-已完结 */
    Integer bookStatus;

    /** 当前页码 */
    int pageNum = 1;

    /** 每页记录数 */
    int pageSize = 10;
}
