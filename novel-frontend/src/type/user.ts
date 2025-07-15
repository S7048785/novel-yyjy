export type UserLoginView = {
	id: string;
	nickName: string;
	userPhoto: string;
	email: string;
	token: string;
}

export type UserLoginInput = {
	email: string;
	password: string;
	captcha: string;
}

export type UserRegisterInput = {
	email: string;
	password: string;
	nickName: string;
	captcha: string;
}

export type UserInfoView = {
	id: string;
	nickName: string;
	userPhoto: string;
	email: string;
}