import {api} from "@/ApiInstance.ts";
import type {Dynamic_UserInfo} from "@/__generated/model/dynamic";
import type {UserPageQueryReq} from "@/__generated/model/static";

export async function fetchUsers(params: UserPageQueryReq) {
	const res = await api.userManagementController.page({req: params});
	return res.data;
}

export async function updateUser(newUser: Dynamic_UserInfo) {
	await api.userManagementController.update({
		body: {
			id: newUser.id!,
			email: newUser.email!,
			nickName: newUser.nickName!,
			role: newUser.role!,
			status: newUser.status!,
			userPhoto: newUser.userPhoto!,
		}
	})
}

export async function saveUser(user: Dynamic_UserInfo) {
	await api.userManagementController.add({
		body: {
			email: user.email!,
			password: user.password!,
			nickName: user.nickName!,
			role: user.role!,
			status: user.status!,
			userPhoto: user.userPhoto!,
		}
	})
}

export async function deleteUser(id: number) {
	await api.userManagementController.delete({id});
}
