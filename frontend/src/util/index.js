const getSessionToken = () => localStorage.getItem('sessionToken');
const setSessionToken = userToken => localStorage.setItem('sessionToken', userToken);
const removeSessionToken = () => localStorage.removeItem('sessionToken');
export default {
    getSessionToken,
    setSessionToken,
    removeSessionToken
}