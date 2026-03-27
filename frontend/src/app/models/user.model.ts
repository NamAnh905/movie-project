export interface AdminUserResponse {
  id: number;
  username: string;
  fullName?: string;
  email?: string;
  status: string;
  role: string;
  enabled: boolean;
  createdAt?: string;
}

export interface UserResponse {
  username: string;
  fullName?: string;
  email?: string;
  status: string;
}
