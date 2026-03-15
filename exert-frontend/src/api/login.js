import request from "@/utils/request";

export const loginAPI=(loginData)=>request.post('/auth/login',loginData)