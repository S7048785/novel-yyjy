import type {Executor} from '../';
import type {
    CrawlTaskStatus, 
    Result, 
    SseEmitter, 
    Void
} from '../model/static/';

export class CrawlerController {
    
    constructor(private executor: Executor) {}
    
    readonly addNovelById: (options: CrawlerControllerOptions['addNovelById']) => Promise<
        Result<Void>
    > = async(options) => {
        let _uri = '/admin/crawler/';
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
    
    readonly getStatus: (options: CrawlerControllerOptions['getStatus']) => Promise<
        CrawlTaskStatus
    > = async(options) => {
        let _uri = '/admin/crawler/status/';
        _uri += encodeURIComponent(options.taskId);
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<CrawlTaskStatus>;
    }
    
    readonly start: (options: CrawlerControllerOptions['start']) => Promise<
        CrawlTaskStatus
    > = async(options) => {
        let _uri = '/admin/crawler/start';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.novelId;
        _uri += _separator
        _uri += 'novelId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        _value = options.chapters;
        if (_value !== undefined && _value !== null) {
            _uri += _separator
            _uri += 'chapters='
            _uri += encodeURIComponent(_value);
            _separator = '&';
        }
        return (await this.executor({uri: _uri, method: 'POST'})) as Promise<CrawlTaskStatus>;
    }
    
    readonly stream: (options: CrawlerControllerOptions['stream']) => Promise<
        SseEmitter
    > = async(options) => {
        let _uri = '/admin/crawler/stream/';
        _uri += encodeURIComponent(options.taskId);
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<SseEmitter>;
    }
}

export type CrawlerControllerOptions = {
    'addNovelById': {
        readonly bookId: string, 
        readonly chapterCount?: number | undefined
    }, 
    'start': {
        readonly novelId: string, 
        readonly chapters?: number | undefined
    }, 
    'stream': {
        readonly taskId: string
    }, 
    'getStatus': {
        readonly taskId: string
    }
}
