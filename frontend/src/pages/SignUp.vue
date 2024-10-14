<template>
    <div class="gradient-custom d-flex align-items-center justify-content-center vh-100">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-md-8 col-lg-6 col-xl-5">
                    <div class="card bg-dark text-white" style="border-radius: 1rem;">
                        <div class="card-body p-5 text-center">
                            <h2 class="fw-bold mb-2 text-uppercase">SIGN UP</h2>
                            <p class="text-white-50 mb-5">서비스 사용을 위한 회원 가입</p>
                            
                            <form @submit.prevent="submit">
                                <div class="form-outline form-white mb-4">
                                    <label class="form-label" for="email">Email address</label>
                                    <input v-model="state.form.email" type="email" id="email" class="form-control form-control-lg" required placeholder="name@example.com" />
                                </div>

                                <div class="form-outline form-white mb-4">
                                    <label class="form-label" for="password">Password</label>
                                    <input v-model="state.form.password" type="password" id="password" class="form-control form-control-lg" required placeholder="Password" />
                                </div>

                                <div class="form-outline form-white mb-4">
                                    <label class="form-label" for="name">Name</label>
                                    <input v-model="state.form.name" type="text" id="name" class="form-control form-control-lg" required placeholder="Name" />
                                </div>

                                <button class="btn btn-outline-light btn-lg px-5" type="submit">Sign Up</button>
                            </form>

                            <div class="mt-4">
                                <p class="mb-0">이미 계정이 있으신가요? <a @click="goToLogin" href="#!" class="text-white-50 fw-bold">로그인</a></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import router from '@/scripts/router';
import axios from 'axios';
import { reactive } from 'vue';

export default {
    name: "SignUp",
    setup() {
        const state = reactive({
            form: {
                email: "",
                password: "",
                name: ""
            }
        })

        const submit = async () => {
            try {
                await axios.post("/api/auth/signup", state.form).then(()=>{
                    window.alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
                    router.push({path: "/login"});
                })
            } catch (error) {
                //console.error("Signup error:", error);
                alert('회원가입에 실패했습니다. 다시 시도해 주세요.');
            }
        }

        const goToLogin = () => {
            router.push({path: "/login"});
        }

        return {state, submit, goToLogin}
    }
}
</script>

<style lang="css" scoped>
.gradient-custom {
    background: linear-gradient(to right, rgba(254, 238, 229, 1), rgba(229, 193, 197, 1));
}
</style>