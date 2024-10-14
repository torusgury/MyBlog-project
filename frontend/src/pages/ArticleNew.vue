<template>
    <div class="p-5 mb-5 text-center</> bg-light">
        <h1 class="mb-3">My Blog</h1>
        <h4 class="mb-3">블로그에 오신 것을 환영합니다.</h4>
    </div>
    <div class="container mt-5">
        <div class="row">
            <div class="col-lg-8">
                <article>
                    <input type="hidden" id="article-id" :value="article.id">
                    <header class="mb-4">
                        <input v-model="article.title" type="text" class="form-control" placeholder="제목" id="title">
                    </header>
                    <section class="mb-5">
                        <textarea v-model="article.content" class="form-control h-25" rows="10" placeholder="내용" id="content"></textarea>
                    </section>
                    <button v-if="id" type="button" class="btn btn-primary btn-sm" @click="editArticle">수정</button>
                    <button v-else type="button" class="btn btn-primary btn-sm" @click="createArticle">등록</button>
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
    name: "ArticleNew",
    props: {
        id: String
    },
    setup(props) {
        
        const article = ref({ title: '', content: '' });

        const fetchArticle = async () => {
            if (props.id) {
                try {
                    const { data } = await axios.get(`/api/articles/${props.id}`);
                    article.value = data;
                } catch (error) {
                    console.error("Error fetching article:", error);
                    alert('게시글을 불러오는 데 실패했습니다.');
                }
            }
        };

        const editArticle = async () => {
            try {
                await axios.put(`/api/articles/${article.value.id}`, {
                    title: article.value.title,
                    content: article.value.content
                });
                alert('수정이 완료되었습니다.');
                router.push({path: `/article/${props.id}`});
            } catch (error) {
                alert('게시글 수정에 실패했습니다.');
            }
        };

        const createArticle = async () => {
            try {
                const { data } = await axios.post('/api/articles', {
                    title: article.value.title,
                    content: article.value.content
                });
                alert('게시글이 등록되었습니다.');
                router.push({path: `/article/${data.id}`});
            } catch (error) {
                alert('게시글 등록에 실패했습니다.');
            }
        };

        // 컴포넌트가 마운트될 때 게시글을 가져옴
        onMounted(fetchArticle);

        return {
            article,
            editArticle,
            createArticle,
            formatDate: lib.formatDate
        }
    }
}
</script>

<style scoped>
</style>