/**
 * 用户信息
 */
export interface UserLoginInput {
    /**
     * 登录名
     */
    readonly email: string;
    /**
     * 登录密码
     */
    readonly password: string;
    readonly captcha?: string | undefined;
}
