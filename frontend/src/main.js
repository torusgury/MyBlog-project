import { getAccessToken, setupAxiosInterceptors } from '@/scripts/auth';
import axios from 'axios';
import { createApp } from 'vue';
import App from './App.vue';
import router from './scripts/router';
import store from './scripts/store';

// 앱 시작 시 토큰이 있으면 인터셉터 설정
const token = getAccessToken();
if (token) {
    setupAxiosInterceptors();
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

createApp(App).use(store).use(router).mount('#app')
