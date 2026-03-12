import type {Executor} from '../';
import type {Result, Void} from '../model/static/';

export class CrawlerController {
    
    constructor(private executor: Executor) {}
    
    readonly addNovelById: (options: CrawlerControllerOptions['addNovelById']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/crawler/';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.chapterCount;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'chapterCount='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        return (await this.executor({uri: _uri, method: 'POST'})) as Promise<Result<Void>>;
    }
}

export type CrawlerControllerOptions = {
    'addNovelById': {
        readonly bookId: string, 
        readonly chapterCount?: number | undefined
    }
}
