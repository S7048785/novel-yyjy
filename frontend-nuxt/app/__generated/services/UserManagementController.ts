import type {Executor} from '../';
import type {Dynamic_UserInfo} from '../model/dynamic/';
import type {
    PageResult, 
    Result, 
    UserAddInput, 
    UserPageQueryReq, 
    UserUpdateReq, 
    Void
} from '../model/static/';

export class UserManagementController {
    
    constructor(private executor: Executor) {}
    
    readonly add: (options: UserManagementControllerOptions['add']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/admin/user';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<Void>>;
    }
    
    readonly delete: (options: UserManagementControllerOptions['delete']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/admin/user/';
        _uri += encodeURIComponent(options.id);
        return (await this.executor({uri: _uri, method: 'DELETE'})) as Promise<Result<Void>>;
    }
    
    readonly getById: (options: UserManagementControllerOptions['getById']) => Promise<
        Result<Dynamic_UserInfo>
    > = async(options) => {
        let _uri = '/admin/user/';
        _uri += encodeURIComponent(options.id);
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<Dynamic_UserInfo>>;
    }
    
    readonly page: (options: UserManagementControllerOptions['page']) => Promise<
        Result<PageResult<Dynamic_UserInfo>>
    > = async(options) => {
        let _uri = '/admin/user/page';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.req.email;
        _uri += _separator
        _uri += 'email='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.req.sort;
        _uri += _separator
        _uri += 'sort='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.req.sortField;
        _uri += _separator
        _uri += 'sortField='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.req.status;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'status='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        _value = options.req.startTime;
        _uri += _separator
        _uri += 'startTime='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.req.endTime;
        _uri += _separator
        _uri += 'endTime='
        _uri += encodeURIComponent(_value);
        _separator = '&';
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
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<PageResult<Dynamic_UserInfo>>>;
    }
    
    readonly update: (options: UserManagementControllerOptions['update']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/admin/user';
        return (await this.executor({uri: _uri, method: 'PUT', body: options.body})) as Promise<Result<Void>>;
    }
    
    readonly upload: (options: UserManagementControllerOptions['upload']) => Promise<
        Result<string>
    > = async(options) => {
        let _uri = '/admin/user/upload';
        const _formData = new FormData();
        const _body = options.body;
        _formData.append("file", _body.file);
        return (await this.executor({uri: _uri, method: 'POST', body: _formData})) as Promise<Result<string>>;
    }
}

export type UserManagementControllerOptions = {
    'page': {
        readonly req: UserPageQueryReq
    }, 
    'getById': {
        readonly id: number
    }, 
    'add': {
        readonly body: UserAddInput
    }, 
    'update': {
        readonly body: UserUpdateReq
    }, 
    'delete': {
        readonly id: string
    }, 
    'upload': {
        readonly body: {
            readonly file: File
        }
    }
}
