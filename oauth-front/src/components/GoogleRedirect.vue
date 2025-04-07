<template>
    <div>
        구글 로그인 진행중...
    </div>
</template>
<script>
import axios from 'axios';

export default {
    // 훅 함수, 열리자마자 실행
    created() {
        const code = new URL(window.location.href).searchParams.get("code");
        this.sendCodeToServer(code);
    },
    methods: {
        async sendCodeToServer(code) {
            const response = await axios.post("http://localhost:8080/oauth/google/login", {code});
            const token = response.data.token
            localStorage.setItem("token", token);
            window.location.href = "/";
        }
    }
}
</script>