import type {Executor} from '../';
import type {LaetstNewsView, Result} from '../model/static/';

export class NewsController {
    
    constructor(private executor: Executor) {}
    
    readonly latestList: () => Promise<
        Result<ReadonlyArray<LaetstNewsView>>
    > = async() => {
        let _uri = '/news/latest';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<Result<ReadonlyArray<LaetstNewsView>>>;
    }
}

export type NewsControllerOptions = {
    'latestList': {}
}
