/**
 * 用户信息
 */
export interface UserAddInput {
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
    /**
     * 用户头像
     */
    readonly userPhoto?: string | undefined;
    /**
     * 用户权限
     */
    readonly role: string;
    /**
     * 用户状态;0-正常
     */
    readonly status: number;
}
