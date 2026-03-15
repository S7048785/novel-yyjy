import type { Dynamic_UserInfo } from "@/__generated/model/dynamic";
import { api } from "@/ApiInstance";

async function updateUser(newUser: Dynamic_UserInfo) {
  await api.userManagementController.update({
    body: {
      id: newUser.id!,
      email: newUser.email!,
      nickName: newUser.nickName!,
      role: newUser.role!,
      status: newUser.status!,
      userPhoto: newUser.userPhoto!,
    },
  });
}

async function saveUser(user: Dynamic_UserInfo) {
  await api.userManagementController.add({
    body: {
      email: user.email!,
      password: user.password!,
      nickName: user.nickName!,
      role: user.role!,
      status: user.status!,
      userPhoto: user.userPhoto!,
    },
  });
}

async function queryUser({
  searchText,
  pagination,
}: {
  searchText: string;
  pagination: { pageNum: number; pageSize: number };
}) {
  // 这里可以替换成实际的 API 请求
  const res = await api.userManagementController.page({
    req: {
      email: searchText,
      pageSize: pagination.pageSize,
      pageNum: pagination.pageNum,
      sort: 0,
      startTime: "",
      endTime: "",
      sortField: "",
    },
  });
  return res.data;
}

async function deleteUser(userId: string) {
  // await api.userManagementController.delete({
  //   id: userId,
  // });
}

export { queryUser, saveUser, updateUser };
