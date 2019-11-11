import axios from 'axios';
import {Toast} from 'vant';
import util from '../util'
import router from '../router'
import config from '../config'

const service = axios.create({
    timeout: 5000,
    baseURL: `http://${config.apiHost}`
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
    .use(resp => {
        console.log(resp);
        let data = resp.data;
        let code = data.code;

        if (code === 200) {
            return data.data;
        }
        let errorMsg = '请求错误';
        let msg = data.msg;
        if (msg) {
            errorMsg = msg;
        }
        Toast(errorMsg);
        if (code === 401) {
            router.push('/sign-in');
        }
        return new Promise((l, f) => {
        });
    }, error => {
        console.log('接口错误', error);
        Toast('接口错误');
        return Promise.reject(error.message)
    });

function requestApi(method, url, param, data) {
    let headers = {
        'Authorization': util.getSessionToken()
    };
    return service.request({
        method: method,
        url: url,
        params: param,
        data: data,
        headers: headers,
    });
}

export default {
    requestApi,
};