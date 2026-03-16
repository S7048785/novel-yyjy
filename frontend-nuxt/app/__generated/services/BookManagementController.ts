import type {Executor} from '../';
import type {BookInfoDto} from '../model/dto/';
import type {
    BookPageQueryReq, 
    BookUpdateReq, 
    PageResult, 
    Result, 
    Void
} from '../model/static/';

/**
 * 后台小说管理控制器
 */
export class BookManagementController {
    
    constructor(private executor: Executor) {}
    
    readonly add: (options: BookManagementControllerOptions['add']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/admin/book';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<Void>>;
    }
    
    readonly delete: (options: BookManagementControllerOptions['delete']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/admin/book/';
        _uri += encodeURIComponent(options.id);
        return (await this.executor({uri: _uri, method: 'DELETE'})) as Promise<Result<Void>>;
    }
    
    readonly getById: (options: BookManagementControllerOptions['getById']) => Promise<
        Result<BookInfoDto['BookManagementController/BOOK_VIEW_OF_ADMIN']>
    > = async(options) => {
        let _uri = '/admin/book/';
        _uri += encodeURIComponent(options.id);
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<BookInfoDto['BookManagementController/BOOK_VIEW_OF_ADMIN']>>;
    }
    
    readonly page: (options: BookManagementControllerOptions['page']) => Promise<
        Result<PageResult<BookInfoDto['BookManagementController/BOOK_VIEW_OF_ADMIN']>>
    > = async(options) => {
        let _uri = '/admin/book/page';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.req.bookName;
        _uri += _separator
        _uri += 'bookName='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.req.authorName;
        _uri += _separator
        _uri += 'authorName='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.req.categoryId;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'categoryId='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.req.bookStatus;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'bookStatus='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.req.pageNum;
        _uri += _separator
        _uri += 'pageNum='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.req.pageSize;
        _uri += _separator
        _uri += 'pageSize='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<PageResult<BookInfoDto['BookManagementController/BOOK_VIEW_OF_ADMIN']>>>;
    }
    
    readonly update: (options: BookManagementControllerOptions['update']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/admin/book';
        return (await this.executor({uri: _uri, method: 'PUT', body: options.body})) as Promise<Result<Void>>;
    }
}

export type BookManagementControllerOptions = {
    'page': {
        readonly req: BookPageQueryReq
    }, 
    'getById': {
        readonly id: number
    }, 
    'add': {
        readonly body: BookUpdateReq
    }, 
    'update': {
        readonly body: BookUpdateReq
    }, 
    'delete': {
        readonly id: string
    }
}
