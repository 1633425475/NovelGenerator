import request from '@/utils/request'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  tokenType: string
  userId: number
  username: string
  nickname: string
  avatarUrl: string | null
  roles: string[]
  permissions: string[]
}

export interface UserInfo {
  userId: number
  username: string
  nickname: string
  avatarUrl: string | null
  roles: string[]
  permissions: string[]
}

export const loginApi = (data: LoginRequest) => {
  return request.post<LoginResponse>('/auth/login', data)
}

export const getCurrentUserApi = () => {
  return request.get<UserInfo>('/auth/me')
}

export const logoutApi = () => {
  return request.post('/auth/logout')
}