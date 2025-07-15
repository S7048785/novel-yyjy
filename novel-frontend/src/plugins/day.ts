import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn'; // 如果需要中文显示
dayjs.extend(relativeTime);
dayjs.locale('zh-cn'); // 设置本地化语言（可选）