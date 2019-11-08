import http from './util';

function postRequest(url) {
    return function (data, ok, error) {
        let pro = http.requestApi('POST', url, null, data, null)
            .then(ok);
        if (error) {
            pro.catch(error);
        }
    }
}

function getRequest(url) {
    return function (param, ok, error) {
        let pro = http.requestApi('POST', url, param, null, null)
            .then(ok);
        if (error) {
            pro.catch(error);
        }
    }
}

const account = {
    validate: postRequest("account/validate"),
    signUp: postRequest("account/sign-up"),
};

export default {
    account
}