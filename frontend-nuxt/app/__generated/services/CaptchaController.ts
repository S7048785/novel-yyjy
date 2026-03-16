import type {Executor} from '../';

export class CaptchaController {
    
    constructor(private executor: Executor) {}
    
    /**
     * 获取验证码图片
     * 此方法通过HTTP GET请求生成并返回一个验证码图片，同时设置响应头以避免缓存
     * 验证码文本被存储在HttpSession中，供后续验证使用
     * 
     * @parameter {CaptchaControllerOptions['getCaptcha']} options
     * - request 用于获取当前会话或创建新会话的HttpServletRequest对象
     * - response 用于设置响应头和输出流的HttpServletResponse对象
     */
    readonly getCaptcha: () => Promise<
        void
    > = async() => {
        let _uri = '/captcha';
        return (await this.executor({uri: _uri, method: 'GET'})) as Promise<void>;
    }
}

export type CaptchaControllerOptions = {
    'getCaptcha': {}
}
