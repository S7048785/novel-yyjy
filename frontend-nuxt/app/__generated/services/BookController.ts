import type {Executor} from '../';
import type {Dynamic_BookInfo} from '../model/dynamic/';
import type {
    AllBookQueryInput, 
    BookCategoryView, 
    BookChapterDto, 
    BookInfoDoc, 
    BookInfoSearchView, 
    BookInfoView, 
    BookRankView, 
    BookStateVO, 
    PageResult, 
    Result
} from '../model/static/';

export class BookController {
    
    constructor(private executor: Executor) {}
    
    readonly getBookContent: (options: BookControllerOptions['getBookContent']) => Promise<
        Result<any>
    > = async(options) => {
        let _uri = '/book/content';
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
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<any>>;
    }
    
    readonly getBookInfo: (options: BookControllerOptions['getBookInfo']) => Promise<
        Result<BookInfoView>
    > = async(options) => {
        let _uri = '/book/info/';
        _uri += encodeURIComponent(options.id);
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<BookInfoView>>;
    }
    
    readonly getBookState: (options: BookControllerOptions['getBookState']) => Promise<
        Result<BookStateVO>
    > = async(options) => {
        let _uri = '/book/state';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<BookStateVO>>;
    }
    
    readonly listAllCategory: (options: BookControllerOptions['listAllCategory']) => Promise<
        Result<PageResult<BookInfoDoc>>
    > = async(options) => {
        let _uri = '/book/all';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.params.bookName;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'bookName='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.params.channelId;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'channelId='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.params.categoryId;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'categoryId='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.params.overState;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'overState='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.params.wordCountState;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'wordCountState='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.params.updateDay;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'updateDay='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.params.sortState;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'sortState='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.pageNum;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'pageNum='
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
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<PageResult<BookInfoDoc>>>;
    }
    
    readonly listBookChapter: (options: BookControllerOptions['listBookChapter']) => Promise<
        Result<BookChapterDto>
    > = async(options) => {
        let _uri = '/book/chapter';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.id;
        _uri += _separator
        _uri += 'id='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.isAll;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'isAll='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.orderBy;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'orderBy='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<BookChapterDto>>;
    }
    
    readonly listCategory: (options: BookControllerOptions['listCategory']) => Promise<
        Result<ReadonlyArray<BookCategoryView>>
    > = async(options) => {
        let _uri = '/book/category';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.id;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'id='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<BookCategoryView>>>;
    }
    
    readonly listRandomBooks: (options: BookControllerOptions['listRandomBooks']) => Promise<
        Result<ReadonlyArray<Dynamic_BookInfo>>
    > = async(options) => {
        let _uri = '/book/random';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.categoryId;
        _uri += _separator
        _uri += 'categoryId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<Dynamic_BookInfo>>>;
    }
    
    readonly listRankBooks: (options: BookControllerOptions['listRankBooks']) => Promise<
        Result<ReadonlyArray<BookRankView>>
    > = async(options) => {
        let _uri = '/book/rank';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.rankType;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'rankType='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<BookRankView>>>;
    }
    
    readonly search: (options: BookControllerOptions['search']) => Promise<
        Result<ReadonlyArray<BookInfoSearchView>>
    > = async(options) => {
        let _uri = '/book/search';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.name;
        _uri += _separator
        _uri += 'name='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<BookInfoSearchView>>>;
    }
}

export type BookControllerOptions = {
    'listCategory': {
        readonly id?: number | undefined
    }, 
    'listAllCategory': {
        readonly params: AllBookQueryInput, 
        readonly pageNum?: number | undefined, 
        readonly pageSize?: number | undefined
    }, 
    'getBookInfo': {
        readonly id: number
    }, 
    'listBookChapter': {
        readonly id: number, 
        readonly isAll?: number | undefined, 
        readonly orderBy?: number | undefined
    }, 
    'listRandomBooks': {
        readonly categoryId: number, 
        readonly bookId: number
    }, 
    'listRankBooks': {
        readonly rankType?: number | undefined
    }, 
    'getBookContent': {
        readonly bookId: number, 
        readonly chapterId: number
    }, 
    'getBookState': {
        readonly bookId: number
    }, 
    'search': {
        readonly name: string
    }
}
