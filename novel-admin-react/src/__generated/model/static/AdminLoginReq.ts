export interface AdminLoginReq {
    readonly username: string;
    readonly password: string;
    readonly captcha: string;
}
