import axios from 'axios';
import router from '../router'
import {Toast} from 'vant';

const service = axios.create({
    timeout: 5000
});
service.interceptors
    .request
    .use(req => {
        req.headers['Content-Type'] = 'application/json;charset=UTF-8';
        return req;
    }, error => {
        return Promise.reject(error);
    });
service.interceptors
    .response
    .use(resp => resp, error => {
        let errorMsg = '请求错误';
        if (error && error.response) {
            let statusText = error.response.statusText;
            if (statusText) {
                errorMsg = statusText;
            } else {
                switch (error.response.status) {
                    case 400:
                        errorMsg = '错误请求';
                        break;
                    case 401:
                        errorMsg = '未认证,请重新登录';
                        router.push('/start');
                        break;
                    case 403:
                        errorMsg = '未授权,拒绝访问';
                        break;
                    case 500:
                        errorMsg = '服务器错误';
                        break;
                    case 501:
                        errorMsg = '网络未实现';
                        break;
                    default:
                        errorMsg = `连接错误${error.response.status}`;
                }
            }
        } else {
            errorMsg = "连接到服务器失败";
        }
        Toast(errorMsg);
        console.log('接口错误', error);
        return Promise.reject(error.message)
    });

function requestApi(method, url, param, data, headers) {
    Toast.loading({
        message: '加载中...',
        forbidClick: true,
        loadingType: 'spinner'
    });

    return service.request({
        method: method,
        url: url,
        params: param,
        data: data,
        headers: headers,
    }).finally(() => {
        Toast.clear();
    });
}

export default {
    requestApi,
};