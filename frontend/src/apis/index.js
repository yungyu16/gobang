import http from './util';

function postRequest(url) {
    return function (data) {
        return http.requestApi('POST', url, null, data);
    }
}

function getRequest(url) {
    return function (param) {
        return http.requestApi('GET', url, param, null);
    }
}

const user = {
    validate: getRequest("user/validate"),
    signUp: postRequest("user/sign-up"),
    signIn: postRequest("user/sign-in"),
    signOut: getRequest("user/sign-out"),
    detail: getRequest("user/detail"),
    history: getRequest("user/history"),
};

export default {
    user
}