import axios from 'axios';
import {Loading, Message} from 'element-ui';
import router from '../router'

const service = axios.create({
    timeout: 5000
});

service.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=UTF-8';
    return config;
}, error => {
    return Promise.reject(error);
});
service.interceptors.response.use(
    config => {
        return config
    },
    error => {
        if (error && error.response) {
            switch (error.response.status) {
                case 400:
                    error.message = '错误请求';
                    Toast('错误请求');
                    break;
                case 401:
                    error.message = '未授权，请重新登录';
                    router.push('/signUp');
                    break;
                case 403:
                    error.message = '拒绝访问';
                    Toast('拒绝访问');
                    break;
                case 404:
                    error.message = '请求错误,未找到该资源';
                    Toast('请求错误,未找到该资源');
                    break;
                case 405:
                    error.message = '请求方法未允许';
                    Toast('请求方法未允许');
                    break;
                case 408:
                    error.message = '请求超时';
                    Toast('请求超时');
                    break;
                case 500:
                    error.message = '服务器端出错';
                    Toast('服务器端出错');
                    break;
                case 501:
                    error.message = '网络未实现';
                    Toast('网络未实现');
                    break;
                case 502:
                    error.message = '网络错误';
                    Toast('网络错误');
                    break;
                case 503:
                    error.message = '服务不可用';
                    Toast('服务不可用');
                    break;
                case 504:
                    error.message = '网络超时';
                    Toast('网络超时');
                    break;
                case 505:
                    error.message = 'http版本不支持该请求';
                    Toast('http版本不支持该请求');
                    break;
                default:
                    error.message = `连接错误${error.response.status}`;
                    Toast(`'连接错误'${error.response.status}`);
            }
        }
    });

function requestApi(method, url, param, data, headers) {
    const loading = Loading.service({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
    });

    return service
        .request({
            method: method,
            url: url,
            params: param,
            data: data,
            headers: headers,
        }).finally(() => {
            loading.close();
        });
}

export default {
    requestApi,
};