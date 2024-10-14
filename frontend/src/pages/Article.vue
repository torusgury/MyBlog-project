<template>
    <div class="p-5 mb-5 text-center</> bg-light">
        <h1 class="mb-3">My Blog</h1>
        <h4 class="mb-3">블로그에 오신 것을 환영합니다.</h4>
    </div>
    <div class="container mt-5" v-if="article">
        <div class="row">
            <div class="col-lg-8">
                <article>
                    <input type="hidden" id="article-id" :value="article.id">
                    <header class="mb-4">
                        <h1 class="fw-bolder mb-1">{{ article.title }}</h1>
                        <div class="text-muted fst-italic mb-2">Posted on {{ formatDate(article.createdAt) }}</div>
                    </header>
                    <section class="mb-5">
                        <p class="fs-5 mb-4">{{ article.content }}</p>
                    </section>
                    <router-link to="/" class="btn btn-primary me-2">목록보기</router-link>&nbsp;
                    <button type="button" class="btn btn-primary btn-sm" @click="editArticle">수정</button>&nbsp;
                    <button type="button" class="btn btn-secondary btn-sm" @click="deleteArticle">삭제</button>
                </article>
            </div>
        </div>
    </div>
</template>

<script>
import lib from '@/scripts/lib';
import router from '@/scripts/router';
import axios from 'axios';
import { onMounted, ref } from 'vue';

export default {
    name: "Article",
    props: {
        id: {
            type: String,
            required: true
        }
    },
    setup(props) {
        const article = ref(null);

        const fetchArticle = async () => {
            try {
                const { data } = await axios.get(`/api/articles/${props.id}`);
                article.value = data;
            } catch (error) {
                console.error("Error fetching article:", error);
            }
        };

        const deleteArticle = async () => {
            if (confirm('정말로 삭제하시겠습니까?')) {
                try {
                    await axios.delete(`/api/articles/${article.value.id}`);
                    alert('삭제가 완료되었습니다.');
                    router.push({path: "/"});
                } catch (error) {
                    console.error("Error deleting article:", error);
                    alert('삭제 중 오류가 발생했습니다.');
                }
            }
        };

        const editArticle = () => {
            router.push({path: `/articleN/${article.value.id}`});
        };

        // 컴포넌트가 마운트될 때 게시글을 가져옴
        onMounted(fetchArticle);

        return {
            article,
            deleteArticle,
            editArticle,
            formatDate: lib.formatDate
        }
    }
}
</script>

<style scoped>
</style>