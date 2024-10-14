<template>
    <div class="p-5 mb-5 text-center</> bg-light">
        <h1 class="mb-3">My Blog</h1>
        <h4 class="mb-3">블로그에 오신 것을 환영합니다.</h4>
    </div>
    <div class="container">
        <button @click="createArticle" type="button" class="btn btn-secondary btn-sm mb-3">글 등록</button>
        <div class="row-6" v-for="(item, idx) in state.items" :key="idx">
            <div class="card">
                <div class="card-header">{{ idx + 1 }}</div>
                <div class="card-body">
                    <h5 class="card-title">{{ item.title }}</h5>
                    <p class="card-text">{{ item.content }}</p>
                    <a href="#" class="btn btn-primary" @click.prevent="submit(item.id)">보러 가기</a>
                </div>
            </div>
            <br>
        </div>
        <br />
        <button @click="logout" class="btn btn-secondary mt-3">로그아웃</button>
    </div>
</template>

<script>
import { getAccessToken, handleTokenRefreshOrRedirect, isTokenExpired, logout } from '@/scripts/auth';
import router from '@/scripts/router';
import axios from 'axios';
import { onMounted, reactive } from 'vue';

export default {
    name: "ArticleList",
    components: {
    },
    setup() {
        const state = reactive({
            items: []
        })

        //const userInfo = ref(null);

        const fetchArticles = async () => {
            try {
                const response = await axios.get('/api/articles');
                state.items = response.data;
            } catch (error) {
                console.error('Failed to fetch articles:', error);
            }
        };

        /*
        axios.get("/api/articles").then(({data})=>{
            state.items = data;
        })
        */

        const submit = (id) => {
            router.push({path: `/article/${id}`});
        }

        const createArticle = () => {
            router.push({path: `/articleN/`});
        };

        const handleLogout = async () => {
            await logout();
            router.push('/login');
        };

        onMounted(async () => {
            
            const accessToken = getAccessToken();

            if (!accessToken || isTokenExpired(accessToken)) {
                if (!await handleTokenRefreshOrRedirect()) {
                    return;
                }
                
            }
            await fetchArticles();
            //userInfo.value = await fetchUserInfo();
        });

        return {
            state,
            submit,
            createArticle,
            logout: handleLogout
        }
    }
}
</script>

<style scoped>
</style>