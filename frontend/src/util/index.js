const getSessionToken = () => localStorage.getItem('sessionToken');
const setSessionToken = userToken => localStorage.setItem('sessionToken', userToken);
export default {
    getSessionToken,
    setSessionToken
}