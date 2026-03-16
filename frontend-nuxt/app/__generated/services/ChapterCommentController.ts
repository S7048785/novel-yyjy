import type {Executor} from '../';
import type {Dynamic_ChapterSummary} from '../model/dynamic/';
import type {
    AddChapterComment, 
    PageResult, 
    Result, 
    SegmentCommentPageQueryReq, 
    SegmentCommentView, 
    Void
} from '../model/static/';

/**
 * 章节段落控制器
 */
export class ChapterCommentController {
    
    constructor(private executor: Executor) {}
    
    readonly addChapterSummary: (options: ChapterCommentControllerOptions['addChapterSummary']) => Promise<
        Result<SegmentCommentView>
    > = async(options) => {
        let _uri = '/chapter-comment/add';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<SegmentCommentView>>;
    }
    
    readonly delete: (options: ChapterCommentControllerOptions['delete']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/chapter-comment/delete';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.commentId;
        _uri += _separator
        _uri += 'commentId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'DELETE'})) as Promise<Result<Void>>;
    }
    
    readonly getCommentSummaryList: (options: ChapterCommentControllerOptions['getCommentSummaryList']) => Promise<
        Result<ReadonlyArray<Dynamic_ChapterSummary>>
    > = async(options) => {
        let _uri = '/chapter-comment/summary';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.chapterId;
        _uri += _separator
        _uri += 'chapterId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<Dynamic_ChapterSummary>>>;
    }
    
    readonly like: (options: ChapterCommentControllerOptions['like']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/chapter-comment/like';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.commentId;
        _uri += _separator
        _uri += 'commentId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'POST'})) as Promise<Result<Void>>;
    }
    
    readonly listSegmentComment: (options: ChapterCommentControllerOptions['listSegmentComment']) => Promise<
        Result<PageResult<SegmentCommentView>>
    > = async(options) => {
        let _uri = '/chapter-comment/list';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.query.chapterId;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'chapterId='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.query.segmentNum;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'segmentNum='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.query.pageNum;
        _uri += _separator
        _uri += 'pageNum='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.query.pageSize;
        _uri += _separator
        _uri += 'pageSize='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<PageResult<SegmentCommentView>>>;
    }
}

export type ChapterCommentControllerOptions = {
    'getCommentSummaryList': {
        readonly bookId: number, 
        readonly chapterId: number
    }, 
    'addChapterSummary': {
        readonly body: AddChapterComment
    }, 
    'listSegmentComment': {
        readonly query: SegmentCommentPageQueryReq
    }, 
    'like': {
        readonly commentId: number
    }, 
    'delete': {
        readonly commentId: number
    }
}
