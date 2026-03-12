/**
 * 用户信息
 */
export interface UserRegisterInput {
    /**
     * 登录名
     */
    readonly email: string;
    /**
     * 登录密码
     */
    readonly password: string;
    /**
     * 昵称
     */
    readonly nickName: string;
    readonly captcha?: string | undefined;
}
