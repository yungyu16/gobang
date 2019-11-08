import http from './util';

function postRequest(url) {
    return function (data) {
        return http.requestApi('POST', url, null, data, null);
    }
}

function getRequest(url) {
    return function (param) {
        return http.requestApi('POST', url, param, null, null);
    }
}

const account = {
    validate: postRequest("account/validate"),
    signUp: postRequest("account/sign-up"),
};

export default {
    account
}