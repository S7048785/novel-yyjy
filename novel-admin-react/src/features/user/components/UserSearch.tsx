import { Button, Input, Popconfirm } from "antd";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";

interface UserSearchProps {
  handleAdd: () => void;
  searchText: string;
  handleSearch: () => void;
  setSearchText: (value: string) => void;
  selectedRowKeys: React.Key[];
}
export default function UserSearch({
  handleAdd,
  searchText,
  handleSearch,
  setSearchText,
  selectedRowKeys,
}: UserSearchProps) {
  return (
    <div className="mb-4 flex">
      <div className="space-x-4">
        <Input
          placeholder="搜索邮箱号或昵称"
          prefix={<SearchOutlined />}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          style={{ width: 250 }}
        />
        <Button onClick={handleSearch}>搜索</Button>
      </div>
      <div className="ml-auto space-x-4">
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增
        </Button>
        <Popconfirm
          title={`确定删除选中的 ${selectedRowKeys.length} 条记录吗？`}
          onConfirm={() => {}}
          disabled={selectedRowKeys.length === 0}
        >
          <Button danger disabled={selectedRowKeys.length === 0}>
            批量删除
          </Button>
        </Popconfirm>
      </div>
    </div>
  );
}
