import {
  Button,
  Drawer,
  Form,
  Input,
  message,
  Select,
  Space,
  Upload,
  type FormInstance,
  type GetProp,
  type UploadProps,
} from "antd";
const BASE_URL = import.meta.env.VITE_API_BASE_URL;
import { PlusOutlined, LoadingOutlined } from "@ant-design/icons";
import type { Dynamic_UserInfo } from "@/__generated/model/dynamic";

const roleOptions = [
  { label: "user", value: "user" },
  { label: "admin", value: "admin" },
];

const statusOptions = [
  { label: "正常", value: 0 },
  { label: "禁用", value: 1 },
];
type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];

const avatarBeforeUpload = (file: FileType) => {
  const isJpgOrPng = file.type === "image/jpeg" || file.type === "image/png";
  if (!isJpgOrPng) {
    message.error("仅支持上传 JPG/PNG 格式的图片");
  }
  const isLt1M = file.size / 1024 / 1024 < 1;
  if (!isLt1M) {
    message.error("图片大小必须小于 1MB!");
  }
  return isJpgOrPng && isLt1M;
};
interface UserDrawerProps {
  editingUser: Dynamic_UserInfo | null;
  setDrawerOpen: (open: boolean) => void;
  handleSave: () => void;
  drawerOpen: boolean;
  imageLoading: boolean;
  setImageLoading: (loading: boolean) => void;
  imageUrl: string;
  setImageUrl: (url: string) => void;
  form: FormInstance<any>;
}
export default function UserDrawer({
  editingUser,
  setDrawerOpen,
  handleSave,
  imageLoading,
  imageUrl,
  drawerOpen,
  setImageLoading,
  setImageUrl,
  form,
}: UserDrawerProps) {
  const handleAvatarChange: UploadProps["onChange"] = (info) => {
    if (info.file.status === "uploading") {
      setImageLoading(true);
      return;
    }
    if (info.file.status === "done") {
      setImageLoading(false);
      setImageUrl(info.file.response.data);
    }
  };
  const uploadButton = (
    <button style={{ border: 0, background: "none" }} type="button">
      {imageLoading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>Upload</div>
    </button>
  );

  return (
    <Drawer
      title={editingUser ? "编辑用户" : "新增用户"}
      width={480}
      onClose={() => setDrawerOpen(false)}
      open={drawerOpen}
      extra={
        <Space>
          <Button onClick={() => setDrawerOpen(false)}>取消</Button>
          <Button type="primary" onClick={handleSave}>
            保存
          </Button>
        </Space>
      }
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="nickName"
          label="昵称"
          rules={[{ required: true, message: "请输入昵称" }]}
        >
          <Input placeholder="请输入昵称" />
        </Form.Item>
        <Form.Item
          name="email"
          label="邮箱"
          rules={[
            { required: true, type: "email", message: "请输入有效的邮箱" },
          ]}
        >
          <Input placeholder="请输入邮箱" />
        </Form.Item>
        {!editingUser && (
          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: true, message: "请输入密码" }]}
          >
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
        )}
        <Form.Item name="role" label="角色" rules={[{ required: true }]}>
          <Select placeholder="请选择角色" options={roleOptions} />
        </Form.Item>
        <Form.Item name="status" label="状态" rules={[{ required: true }]}>
          <Select placeholder="请选择状态" options={statusOptions} />
        </Form.Item>
        <Form.Item name="userPhoto" label="头像" rules={[{ required: false }]}>
          <Upload
            name="file"
            listType="picture-card"
            className="avatar-uploader"
            showUploadList={false}
            action={BASE_URL + "/admin/user/upload"}
            withCredentials={true}
            beforeUpload={avatarBeforeUpload}
            onChange={handleAvatarChange}
          >
            {imageUrl ? (
              <img
                draggable={false}
                src={imageUrl}
                alt="avatar"
                style={{ width: "100%" }}
              />
            ) : (
              uploadButton
            )}
          </Upload>
        </Form.Item>
      </Form>
    </Drawer>
  );
}
