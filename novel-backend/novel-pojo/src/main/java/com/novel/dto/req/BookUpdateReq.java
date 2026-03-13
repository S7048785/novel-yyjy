package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 小说更新请求
 */
@Data
public class BookUpdateReq {

    /** 小说ID */
    String id;
    
    /** 作品方向 0-男频 1-女频 */
    int workDirection;
    
    /** 类别ID */
    @NotNull(message = "类别不能为空")
    Long categoryId;
    
    /** 小说封面地址 */
    String picUrl;
    
    /** 小说名 */
    @NotBlank(message = "小说名不能为空")
    String bookName;
    
    /** 作家名 */
    @NotBlank(message = "作者名不能为空")
    String authorName;
    
    /** 书籍描述 */
    String bookDesc;
    
    /** 书籍状态 0-连载中 1-已完结 */
    int bookStatus;
}
