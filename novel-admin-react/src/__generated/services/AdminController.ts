import type {Executor} from '../';
import type {
    Result, 
    UserInfoView, 
    UserLoginInput, 
    UserLoginView
} from '../model/static/';

export class AdminController {
    
    constructor(private executor: Executor) {}
    
    readonly login: (options: AdminControllerOptions['login']) => Promise<
        Result<UserLoginView>
    > = async(options) => {
        let _uri = '/admin/login';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<UserLoginView>>;
    }
    
    readonly me: () => Promise<
        Result<UserInfoView>
    > = async() => {
        let _uri = '/admin/me';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<UserInfoView>>;
    }
}

export type AdminControllerOptions = {
    'login': {
        readonly body: UserLoginInput
    }, 
    'me': {}
}
