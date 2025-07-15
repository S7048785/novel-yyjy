import type {UserInfoView} from "@/type/user.ts";

export const useUserStore = defineStore('user', () => {

	const user = ref<UserInfoView | undefined>();
	const setUserInfo = ({ id, nickName, userPhoto, email }: UserInfoView) => {
		user.value = {
			id,
			nickName,
			userPhoto,
			email
		};
	}

	const logout = () => {
		user.value = undefined
		localStorage.removeItem('token')
	}

	return {
		user,
		setUserInfo,
		logout
	};
}, {
	persist: true,
});