import {
  Card,
  Alert,
  Form,
  Input,
  InputNumber,
  Button,
  type FormInstance,
} from "antd";
import { CloudDownloadOutlined } from "@ant-design/icons";

interface CrawlerTaskProps {
  form: FormInstance<any>;
  handleCrawl: (values: { bookId: string; chapterCount?: number }) => void;
  loading: boolean;
}
export default function CrawlerForm({
  form,
  handleCrawl,
  loading,
}: CrawlerTaskProps) {
  return (
    <Card style={{ marginBottom: 24 }}>
      <Alert
        message="使用说明"
        description="请输入要采集的小说ID，选择要采集的章节数量（可选，默认全部），然后点击开始采集。"
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
      />
      <Form
        form={form}
        layout="inline"
        onFinish={handleCrawl}
        style={{ flexWrap: "nowrap" }}
      >
        <Form.Item
          name="bookId"
          label="小说ID"
          rules={[{ required: true, message: "请输入小说ID" }]}
          style={{ marginRight: 16 }}
        >
          <Input placeholder="请输入小说ID" style={{ width: 200 }} />
        </Form.Item>
        <Form.Item
          name="chapterCount"
          label="章节数量"
          style={{ marginRight: 16 }}
        >
          <InputNumber
            min={1}
            placeholder="可选，默认全部"
            style={{ width: 150 }}
          />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            icon={<CloudDownloadOutlined />}
            loading={loading}
          >
            开始采集
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
}
