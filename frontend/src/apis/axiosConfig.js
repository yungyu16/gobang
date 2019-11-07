import axios from 'axios';
import {Loading, Message} from 'element-ui';

const service = axios.create({
    baseURL: 'http://47.102.103.194:8093/',
    timeout: 5000
});

service.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=UTF-8';
    return config;
}, error => {
    console.log(error);
    return Promise.reject();
});

function requestApi(method, url, param, data, headers, callback) {

    const loading = Loading.service({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
    });

    service.request({
        method: method,
        url: url,
        params: param,
        data: data,
        headers: headers,
    }).then(function (response) {

        loading.close();

        if (response.status !== 200) {
            Message.error("请求错误 SC:" + response.status);
            return;
        }
        if (response.data.code === 99) {
            Message.error("请重新登录...");
            return;
        }
        if (response.data.code === 44
            || response.data.code === 55) {
            Message.error(response.data.msg);
            return;
        }
        if (callback) {
            callback(response.data.result);
        } else {
            Message.success("操作成功...");
        }
    }).catch(function (error) {

        loading.close();

        console.error(error);
        Message.error(`请求错误:${error.message}`);
    });
}

function postApi(url, data, callback) {
    requestApi('post', url, null, data, null, callback)
}

function getApi(url, param, callback) {
    requestApi('get', url, param, null, null, callback)
}

export default {
    service,
    requestApi,
    postApi,
    getApi,
};