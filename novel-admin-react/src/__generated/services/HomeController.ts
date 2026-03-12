import type {Executor} from '../';
import type {
    HomeBookRankView, 
    HomeBookView, 
    LastInsertBookView, 
    LastUpdateBookView, 
    Result, 
    VisitRankBookView
} from '../model/static/';

export class HomeController {
    
    constructor(private executor: Executor) {}
    
    readonly listBookVisitRank: () => Promise<
        Result<ReadonlyArray<VisitRankBookView>>
    > = async() => {
        let _uri = '/home/visit-rank';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<VisitRankBookView>>>;
    }
    
    readonly listHomeBooks: () => Promise<
        Result<ReadonlyArray<HomeBookView>>
    > = async() => {
        let _uri = '/home';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<HomeBookView>>>;
    }
    
    readonly listNewestAddBooks: () => Promise<
        Result<ReadonlyArray<LastInsertBookView>>
    > = async() => {
        let _uri = '/home/newest-add';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<LastInsertBookView>>>;
    }
    
    readonly listNewestRankBooks: () => Promise<
        Result<ReadonlyArray<HomeBookRankView>>
    > = async() => {
        let _uri = '/home/newest';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<HomeBookRankView>>>;
    }
    
    readonly listRecentUpdateBooks: () => Promise<
        Result<ReadonlyArray<LastUpdateBookView>>
    > = async() => {
        let _uri = '/home/recent-update';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<LastUpdateBookView>>>;
    }
}

export type HomeControllerOptions = {
    'listHomeBooks': {}, 
    'listBookVisitRank': {}, 
    'listNewestRankBooks': {}, 
    'listRecentUpdateBooks': {}, 
    'listNewestAddBooks': {}
}
