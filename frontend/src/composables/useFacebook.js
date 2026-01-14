import { onMounted } from 'vue';

const APP_ID = '698949099689589';
const VERSION = 'v24.0';

export function useFacebook() {

    const initFacebook = () => {
        return new Promise((resolve) => {
            if (window.FB) {
                resolve(window.FB);
                return;
            }

            window.fbAsyncInit = function() {
                window.FB.init({
                    appId: APP_ID,
                    cookie: true,
                    xfbml: true,
                    version: VERSION
                });
                resolve(window.FB);
            };

            (function(d, s, id) {
                var js, fjs = d.getElementsByTagName(s)[0];
                if (d.getElementById(id)) return;
                js = d.createElement(s); js.id = id;
                js.src = "https://connect.facebook.net/en_US/sdk.js";
                fjs.parentNode.insertBefore(js, fjs);
            }(document, 'script', 'facebook-jssdk'));
        });
    };

    const login = () => {
        return new Promise((resolve, reject) => {
            if (!window.FB) {
                reject("Facebook SDK not loaded yet.");
                return;
            }

            window.FB.login((response) => {
                if (response.authResponse) {
                    window.FB.api('/me', { fields: 'id,name,email,picture' }, (userData) => {
                        resolve(userData);
                    });
                } else {
                    reject("User cancelled login or did not fully authorize.");
                }
            }, { scope: 'public_profile,email' });
        });
    };

    onMounted(() => {
        initFacebook();
    });

    return {
        login
    };
}