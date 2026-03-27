export interface AuthRequest {
  username?: string;
  password?: string;
}

export interface RegisterRequest {
  username?: string;
  password?: string;
  email?: string;
  fullName?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface UserResponse {
  username: string;
  authorities: string[]; // VD: ['ROLE_ADMIN', 'ROLE_USER']
}
