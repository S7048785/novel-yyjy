import type {Executor} from '../';
import type {Result, SseEmitter, Void} from '../model/static/';

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
    
    /**
     * SSE接口：实时推送采集进度
     */
    readonly stream: (options: CrawlerControllerOptions['stream']) => Promise<
        SseEmitter
    > = async(options) => {
        let _uri = '/admin/crawler/stream';
        let _separator = _uri.indexOf('?') === -1 ? '?' : '&';
        let _value: any = undefined;
        _value = options.bookId;
        _uri += _separator
        _uri += 'bookId='
        _uri += encodeURIComponent(_value);
        _separator = '&';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<SseEmitter>;
    }
}

export type CrawlerControllerOptions = {
    'addNovelById': {
        readonly bookId: string, 
        readonly chapterCount?: number | undefined
    }, 
    'stream': {
        readonly bookId: string
    }
}
