import type {Executor} from '../';
import type {
    BookCommentInput, 
    BookCommentView, 
    BookSubCommentView, 
    PageResult, 
    Result
} from '../model/static/';

export class CommentController {
    
    constructor(private executor: Executor) {}
    
    readonly addBookComment: (options: CommentControllerOptions['addBookComment']) => Promise<
        Result<BookCommentView>
    > = async(options) => {
        let _uri = '/comment/add';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<BookCommentView>>;
    }
    
    readonly deleteBookComment: (options: CommentControllerOptions['deleteBookComment']) => Promise<
        Result<string>
    > = async(options) => {
        let _uri = '/comment/';
        _uri += encodeURIComponent(options.id);
        return (await this.executor({uri: _uri, method: 'DELETE'})) as Promise<Result<string>>;
    }
    
    /**
     * 小说评论查询接口 顶层评论
     * @parameter {CommentControllerOptions['listBookComments']} options
     * - bookId 小说ID
     * @return 评论列表
     */
    readonly listBookComments: (options: CommentControllerOptions['listBookComments']) => Promise<
        Result<PageResult<BookCommentView>>
    > = async(options) => {
        let _uri = '/comment/top';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.pageNo;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'pageNo='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.pageSize;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'pageSize='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<PageResult<BookCommentView>>>;
    }
    
    readonly listBookComments_2: (options: CommentControllerOptions['listBookComments_2']) => Promise<
        Result<ReadonlyArray<BookSubCommentView>>
    > = async(options) => {
        let _uri = '/comment/child';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.rootId;
        _uri += _separator
        _uri += 'rootId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<BookSubCommentView>>>;
    }
}

export type CommentControllerOptions = {
    'listBookComments': {
        /**
         * 小说ID
         */
        readonly bookId: number, 
        readonly pageNo?: number | undefined, 
        readonly pageSize?: number | undefined
    }, 
    'listBookComments_2': {
        readonly bookId: number, 
        readonly rootId: number
    }, 
    'addBookComment': {
        readonly body: BookCommentInput
    }, 
    'deleteBookComment': {
        readonly id: number
    }
}
