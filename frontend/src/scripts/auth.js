import axios from 'axios';
import router from './router';

// Access Token을 가져오는 함수
export function getAccessToken() {
    return localStorage.getItem('accessToken');
}

// Refresh Token을 가져오는 함수
export function getRefreshToken() {
    return localStorage.getItem('refreshToken');
}

// Token을 저장하는 함수
export function setTokens(accessToken, refreshToken) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
}

// Token을 제거하는 함수 (로그아웃 시 사용)
export function removeTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
}

// Token의 만료 여부를 확인하는 함수
export function isTokenExpired(token) {
    if (!token) return true;
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.exp * 1000 < Date.now();
    } catch (error) {
        return true;
    }
}

// Access Token 갱신 함수
export async function refreshToken() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
        throw new Error('No refresh token available');
    }

    try {
        const response = await axios.post('/api/auth/refresh', { refreshToken });
        const { accessToken, refreshToken: newRefreshToken } = response.data;
        setTokens(accessToken, newRefreshToken);
        return accessToken;
    } catch (error) {
        removeTokens();
        throw error;
    }
}

// Axios 인터셉터 설정
export function setupAxiosInterceptors() {
    axios.interceptors.request.use(
        (config) => {
            const token = getAccessToken();
            if (token) {
                config.headers['Authorization'] = 'Bearer ' + token;
            }
            return config;
        },
        (error) => {
            return Promise.reject(error);
        }
    );

    axios.interceptors.response.use(
        (response) => response,
        async (error) => {
            const originalRequest = error.config;
            if (error.response.status === 401 && !originalRequest._retry) {
                originalRequest._retry = true;
                try {
                    const accessToken = await refreshToken();
                    axios.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken;
                    return axios(originalRequest);
                } catch (refreshError) {
                    // Refresh token이 만료되었거나 유효하지 않은 경우
                    removeTokens();
                    router.push('/login');
                    return Promise.reject(refreshError);
                }
            }
            return Promise.reject(error);
        }
    );
}

export async function handleTokenRefreshOrRedirect() {
    try {
        await refreshToken();
        return true;
    } catch (error) {
        console.error('Token refresh failed:', error);
        router.push('/login');
        return false;
    }
}

export async function fetchUserInfo() {
    try {
        const response = await axios.get('/api/user');
        return response.data;
    } catch (error) {
        console.error('Failed to fetch user info:', error);
        if (error.response && error.response.status === 401) {
            await handleTokenRefreshOrRedirect();
        }
        throw error;
    }
}

export async function logout() {
    try {
        const accessToken = getAccessToken();
        const refreshToken = getRefreshToken();

        if (!accessToken || !refreshToken) {
            console.warn('No tokens found, proceeding with local logout');
            removeTokens();
            router.push('/login');
            return;
        }

        await axios.post('/api/auth/logout',
            { refreshToken }, // request body에 refreshToken 추가
            {
                headers: {
                    'Authorization': `Bearer ${accessToken}` // Authorization 헤더에 accessToken 추가
                }
            }
        );

        removeTokens();
    } catch (error) {
        console.error('Logout failed:', error);
        // 서버 측 로그아웃에 실패하더라도 클라이언트 측에서는 토큰을 제거
        removeTokens();
    } finally {
        router.push('/login');
    }
}