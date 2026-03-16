import { Api } from "./__generated";

const BASE_URL = "http://localhost:9111";

// 判断是否为客户端环境
const isClient = typeof window !== "undefined";

// 导出全局变量`api`
export const api = new Api(async({uri, method, headers, body}) => {
    // 服务端渲染时 tenant 为 undefined
    const tenant = isClient ? (window as any).__tenant as string | undefined : undefined;
    const response = await fetch(`${BASE_URL}${uri}`, {
        method,
        body: body !== undefined ? JSON.stringify(body) : undefined,
        headers: {
            'content-type': 'application/json;charset=UTF-8',
            ...headers,
            ...(tenant !== undefined && tenant !== "" ? {tenant} : {})
        }
    });
    if (response.status !== 200) {
        throw response.json();
    }
    const text = await response.text();
    if (text.length === 0) {
        return null;
    }
    return JSON.parse(text);
});