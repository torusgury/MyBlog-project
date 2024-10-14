import Article from "@/pages/Article.vue";
import ArticleList from "@/pages/ArticleList.vue";
import ArticleNew from "@/pages/ArticleNew.vue";
import Login from "@/pages/Login.vue";
import SignUp from "@/pages/SignUp.vue";
import { createRouter, createWebHistory } from "vue-router";

const routes = [
    //{path:'/', component: Home},
    {path:'/login', component: Login},
    {path:'/Signup', component: SignUp},
    //{path:'/articles', component: ArticleList},
    {path:'/', component: ArticleList},
    {path:'/article/:id', component: Article, props: true},
    {path:'/articleN', component: ArticleNew},
    {path:'/articleN/:id', component: ArticleNew, props: true},
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router;