import type {Executor} from '../';
import type {
    BookReadHistoryView, 
    Result, 
    UserBookShelfView, 
    UserInfoView, 
    UserLoginInput, 
    UserLoginView, 
    UserRegisterInput, 
    Void
} from '../model/static/';

export class UserController {
    
    constructor(private executor: Executor) {}
    
    readonly getUserInfo: () => Promise<
        Result<UserInfoView>
    > = async() => {
        let _uri = '/user/info';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<UserInfoView>>;
    }
    
    readonly listBookshelf: () => Promise<
        Result<ReadonlyArray<UserBookShelfView>>
    > = async() => {
        let _uri = '/user/bookshelf';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<UserBookShelfView>>>;
    }
    
    readonly listHistory: () => Promise<
        Result<ReadonlyArray<BookReadHistoryView>>
    > = async() => {
        let _uri = '/user/history';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<BookReadHistoryView>>>;
    }
    
    readonly login: (options: UserControllerOptions['login']) => Promise<
        Result<UserLoginView>
    > = async(options) => {
        let _uri = '/user/login';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<UserLoginView>>;
    }
    
    readonly register: (options: UserControllerOptions['register']) => Promise<
        Result<UserLoginView>
    > = async(options) => {
        let _uri = '/user/register';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<UserLoginView>>;
    }
    
    readonly updateBookshelf: (options: UserControllerOptions['updateBookshelf']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/user/bookshelf';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.optType;
        _uri += _separator
        _uri += 'optType='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'PUT'})) as Promise<Result<Void>>;
    }
    
    readonly updateNickName: (options: UserControllerOptions['updateNickName']) => Promise<
        Result<string>
    > = async(options) => {
        let _uri = '/user/nickName';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.nickName;
        _uri += _separator
        _uri += 'nickName='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'PUT'})) as Promise<Result<string>>;
    }
    
    readonly upload: (options: UserControllerOptions['upload']) => Promise<
        Result<string>
    > = async(options) => {
        let _uri = '/user/upload';
        const _formData = new FormData();
        const _body = options.body;
        _formData.append("file", _body.file);
        return (await this.executor({uri: _uri, method: 'POST', body: _formData})) as Promise<Result<string>>;
    }
}

export type UserControllerOptions = {
    'login': {
        readonly body: UserLoginInput
    }, 
    'register': {
        readonly body: UserRegisterInput
    }, 
    'upload': {
        readonly body: {
            readonly file: File
        }
    }, 
    'getUserInfo': {}, 
    'updateNickName': {
        readonly nickName: string
    }, 
    'listHistory': {}, 
    'listBookshelf': {}, 
    'updateBookshelf': {
        readonly optType: number, 
        readonly bookId: number
    }
}
