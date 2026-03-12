import type {Executor} from '../';
import type {
    AdminLoginReq, 
    DashboardStatisticsVO, 
    Result, 
    UserInfoView, 
    UserLoginView, 
    Void
} from '../model/static/';

export class AdminController {
    
    constructor(private executor: Executor) {}
    
    readonly dashboard: () => Promise<
        Result<DashboardStatisticsVO>
    > = async() => {
        let _uri = '/admin/dashboard';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<DashboardStatisticsVO>>;
    }
    
    readonly login: (options: AdminControllerOptions['login']) => Promise<
        Result<UserLoginView>
    > = async(options) => {
        let _uri = '/admin/login';
        return (await this.executor({uri: _uri, method: 'POST', body: options.body})) as Promise<Result<UserLoginView>>;
    }
    
    readonly logout: () => Promise<
        Result<Void>
    > = async() => {
        let _uri = '/admin/logout';
        return (await this.executor({uri: _uri, method: 'POST'})) as Promise<Result<Void>>;
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
        readonly body: AdminLoginReq
    }, 
    'me': {}, 
    'logout': {}, 
    'dashboard': {}
}
