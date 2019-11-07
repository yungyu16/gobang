import axios from './util';

function postRequest(url) {
    return function (param, callback) {
        axios.postApi(url, param, callback);
    }
}

function getRequest(url) {
    return function (param, callback) {
        axios.getApi(url, param, callback);
    }
}

let http = axios.service;

const downloadFile = (url, fileName = '') => {
    let eleLink = document.createElement('a');
    eleLink.download = fileName;
    eleLink.style.display = 'none';
    eleLink.href = url;
    document.body.appendChild(eleLink);
    eleLink.click();
    document.body.removeChild(eleLink);
};

const redisAdmin = {
    pageList: postRequest("redis/page-list"),
    keyDetail: postRequest("redis/key-detail"),
    historyList: postRequest("redis/history-list"),
    deleteKey: postRequest("redis/delete-key")
};
const fileInfo = {
    ifFileExist: getRequest("/file/if-exist"),
    textFile: getRequest("file/text"),
};
const accountInfo = {
    gen: postRequest("/account-info/gen"),
    create: postRequest("/account-info/create"),
    detail: postRequest("/account-info/detail"),
    changeChannel: postRequest("/account-info/change-channel"),
    changeVipStatus: postRequest("/account-info/change-vip-status"),
};
const base = {
    channels: postRequest("/base/channels"),
};
const mock = {
    list: postRequest("/mock/list"),
    detail: postRequest("/mock/detail"),
    add: postRequest("/mock/add"),
    update: postRequest("/mock/update"),
    remove: postRequest("/mock/remove"),
    switch_status: postRequest("/mock/switch-status"),
};


export const fetchData = () => {
};

export default {
    http,
    downloadFile,
    redisAdmin,
    fileInfo,
    accountInfo,
    base,
    mock,
}