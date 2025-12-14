import { type PageData, request, type Result } from "@/utils/request.ts";
import type {
  BookChapterView,
  BookCommentInput,
  BookCommentView,
  BookSubCommentView,
  ChapterSummary,
  SegmentCommentView,
} from "@/type/book.ts";

/**
 * 获取小说章节
 * @param params
 */
export const getBookChapterAPI = (params: {
  id: string;
  isAll?: number;
  orderBy?: number;
}): Result<BookChapterView> => {
  return request({
    url: "/book/chapter",
    method: "get",
    params,
  });
};

export const getBookCommentAPI = (
  bookId: string,
  pageNo: number,
  pageSize: number
): Result<PageData<BookCommentView>> => {
  return request({
    url: "/comment/top",
    method: "get",
    params: {
      bookId,
      pageNo,
      pageSize,
    },
  });
};

export const getBookSubCommentAPI = (
  bookId: string,
  rootId: string
): Result<BookSubCommentView[]> => {
  return request({
    url: "/comment/child",
    method: "get",
    params: {
      bookId,
      rootId,
    },
  });
};

export const addBookCommentAPI = (
  data: BookCommentInput
): Result<BookCommentView> => {
  return request({
    url: "/comment/add",
    method: "post",
    data,
  });
};

/**
 * 删除评论
 * @param id 评论id
 */
export const deleteBookCommentAPI = (id: string): Result<string> => {
  return request({
    url: `/comment/${id}`,
    method: "delete",
  });
};

/**
 * 获取章节的概要列表信息
 */
export const getCommentSummaryList = (
  bookId: string,
  chapterId: string
): Result<ChapterSummary[]> => {
  return request({
    url: "/chapter-comment/summary",
    method: "get",
    params: {
      bookId,
      chapterId,
    },
  });
};

/**
 * 添加章节段落评论
 */
export const addChapterSummary = ({
  bookId,
  chapterId,
  content,
  segmentNum,
}: {
  bookId: string;
  chapterId: string;
  content: string;
  segmentNum: number;
}): Result<SegmentCommentView> => {
  return request({
    url: "/chapter-comment/add",
    method: "post",
    data: {
      bookId,
      chapterId,
      content,
      segmentNum,
    },
  });
};

export const listSegmentComment = (
  chapterId: string,
  segmentNum: number,
  pageNum: number,
  pageSize: number
): Result<PageData<SegmentCommentView>> => {
  return request({
    url: "/chapter-comment/list",
    method: "get",
    params: {
      chapterId,
      segmentNum,
      pageNum,
      pageSize,
    },
  });
};
