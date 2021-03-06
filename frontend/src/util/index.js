const getSessionToken = () => localStorage.getItem('sessionToken') || '';
const setSessionToken = userToken => localStorage.setItem('sessionToken', userToken);
const removeSessionToken = () => localStorage.removeItem('sessionToken');
const mergeObj = (x, y) => Object.assign(x, y);
const mobileShock = () => {
    try {
        if (navigator.vibrate) {
            navigator.vibrate(500);
        } else if (navigator.webkitVibrate) {
            navigator.webkitVibrate(300);
        }
    } catch (e) {

    }
};
export default {
    getSessionToken,
    setSessionToken,
    removeSessionToken,
    mergeObj,
    mobileShock
}