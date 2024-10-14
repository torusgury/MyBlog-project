<template>

    <div class="gradient-custom d-flex align-items-center justify-content-center vh-100">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-md-8 col-lg-6 col-xl-5">
                    <div class="card bg-dark text-white" style="border-radius: 1rem;">
                        <div class="card-body p-5 text-center">
                            <h2 class="fw-bold mb-2 text-uppercase">LOGIN</h2>
                            <p class="text-white-50 mb-5">서비스를 사용하려면 로그인을 해주세요!</p>

                            <form @submit.prevent="submit">
                                <div class="form-outline form-white mb-4">
                                    <label class="form-label" for="email">Email address</label>
                                    <input v-model="state.form.email" type="email" id="email"
                                        class="form-control form-control-lg" required />
                                </div>

                                <div class="form-outline form-white mb-4">
                                    <label class="form-label" for="password">Password</label>
                                    <input v-model="state.form.password" type="password" id="password"
                                        class="form-control form-control-lg" required />
                                </div>

                                <button class="btn btn-outline-light btn-lg px-5" type="submit">Login</button>
                            </form>

                            <div class="mt-4">
                                <p class="mb-0">계정이 없으신가요? <a @click="goToSignup" href="#!"
                                        class="text-white-50 fw-bold">회원가입</a></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { setTokens, setupAxiosInterceptors } from '@/scripts/auth';
import router from '@/scripts/router';
import axios from 'axios';
import { reactive } from 'vue';

export default {
    name: "Login",
    setup() {
        const state = reactive({
            form: {
                email: "",
                password: ""
            }
        });

        const submit = async () => {
            try {
                const response = await axios.post('/api/auth/login', state.form);

                const { accessToken, refreshToken } = response.data;
                setTokens(accessToken, refreshToken);
                setupAxiosInterceptors();

                // axios의 기본 헤더에 토큰 설정
                axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

                //await new Promise(resolve => setTimeout(resolve, 3000));
                router.push({path: "/"});
                window.alert("로그인되었습니다.");
            } catch (error) {
                console.log(error);
                alert('로그인에 실패했습니다. 다시 시도해 주세요.');
            }
        };

        const goToSignup = () => {
            router.push({path: "/signup"});
        };

        return {state, submit, goToSignup}
    }
}
</script>

<style lang="css" scoped>
.gradient-custom {
    background: linear-gradient(to right, rgba(106, 17, 203, 1), rgba(37, 117, 252, 1));
}
</style>