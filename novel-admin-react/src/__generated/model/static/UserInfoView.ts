/**
 * 用户信息
 */
export interface UserInfoView {
    readonly id: string;
    /**
     * 登录名
     */
    readonly email: string;
    /**
     * 昵称
     */
    readonly nickName: string;
    /**
     * 用户头像
     */
    readonly userPhoto?: string | undefined;
}
