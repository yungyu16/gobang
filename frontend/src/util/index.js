const getSessionToken = () => localStorage.getItem('sessionToken') || '';
const setSessionToken = userToken => localStorage.setItem('sessionToken', userToken);
const removeSessionToken = () => localStorage.removeItem('sessionToken');
const mergeObj = (x, y) => Object.assign(x, y);
export default {
    getSessionToken,
    setSessionToken,
    removeSessionToken,
    mergeObj
}