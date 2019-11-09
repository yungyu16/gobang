const getUserToken = () => localStorage.getItem('gobangUserToken');
const setUserToken = userToken => localStorage.setItem('gobangUserToken', userToken);
export default {
    getUserToken,
    setUserToken
}