export interface UserUpdateReq {
    readonly id: string;
    readonly email: string;
    readonly nickName: string;
    readonly userPhoto: string;
    readonly role: string;
    readonly status: number;
}
