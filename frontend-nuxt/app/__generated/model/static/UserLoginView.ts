/**
 * 用户信息
 */
export interface UserLoginView {
    readonly id: string;
    /**
     * 昵称
     */
    readonly nickName: string;
    /**
     * 用户头像
     */
    readonly userPhoto?: string | undefined;
    /**
     * 登录名
     */
    readonly email: string;
    readonly token?: string | undefined;
}
