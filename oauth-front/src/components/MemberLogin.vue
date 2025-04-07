<template>
    <v-container>
        <v-row justify="center">
            <v-col md="6">
                <v-card>
                    <v-card-title class="text-h5 text-center">
                        로그인
                    </v-card-title>
                    <v-card-text>
                        <v-form>
                            <v-text-field
                                label="email"
                                v-model="email"                            
                            >
                            </v-text-field>
                            <v-text-field
                                type="password"
                                label="password"
                                v-model="password"                            
                            >
                            </v-text-field>
                            <v-btn type="button" color="primary" block @click="memberLogin()">로그인</v-btn>
                        </v-form>
                        <br>
                        <v-row>
                            <v-col cols="6" class="d-flex justify-center">
                                <img
                                    src="@/assets/google_login.png"
                                    style="max-height:40px; width:auto;"
                                    @click="googleServerLogin()"
                                />
                            </v-col>
                            <v-col cols="6" class="d-flex justify-center">
                                <img
                                    src="@/assets/kakao_login.png"
                                    style="max-height:40px; width:auto;"
                                    @click="kakaoLogin()"
                                />
                            </v-col>
                        </v-row>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            email: "",
            password: "",
            googleUrl: "https://accounts.google.com/o/oauth2/v2/auth",
            googleClientId: "461607784844-nmq27ddsk1a55u0rtbfc83vhftve0pd0.apps.googleusercontent.com",
            googleRedirectUrl: "http://localhost:3000/oauth/google/redirect",
            // openid 는 기본적으로 제공, email, profile 요청시 제공
            // 아래 3가지는 기본적으로 들어감. 하지만 명시적으로 적음.
            googleScope: "openid email profile",
            // 인가코드로 받을 것이라는 의미
            googleResponseType: "code",
            kakaoUrl: "https://kauth.kakao.com/oauth/authorize",
            kakaoClientId: "9e715e874101494bca17388e6e9c9acd",
            kakaoRedirectUrl: "http://localhost:3000/oauth/kakao/redirect",
            kakaoResponseType: "code",
            // kakaoScope: "account_email,openid"
        }
    },
    methods:{
        async memberLogin() {
            const loginData = {
                email: this.email,
                password: this.password
            }
            const response = await axios.post("http://localhost:8080/auth/login", loginData);
            const token = response.data.token;
            localStorage.setItem("token", token);
            window.location.href = "/";
        },
        googleServerLogin() {
            const auth_uri = `${this.googleUrl}?client_id=${this.googleClientId}&redirect_uri=${this.googleRedirectUrl}&response_type=code&scope=${this.googleScope}`;
            window.location.href = auth_uri;
        },
        kakaoLogin() {
            const auth_uri = `${this.kakaoUrl}?client_id=${this.kakaoClientId}&redirect_uri=${this.kakaoRedirectUrl}&response_type=code`;
            window.location.href = auth_uri;
        }

    }
}
</script>
