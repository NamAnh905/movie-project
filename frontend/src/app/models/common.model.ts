// Định nghĩa chuẩn theo class ApiResponse.java
export interface ApiResponse<T> {
  code?: number;      // Mặc định BE trả 1000
  message?: string;
  result?: T;         // Dữ liệu thực sự nằm ở đây
}

// Định nghĩa chuẩn theo class PageResponse.java
export interface PageResponse<T> {
  currentPage: number;
  totalPages: number;
  pageSize: number;
  totalElements: number;
  items: T[];
}

// Định nghĩa chuẩn theo enum Role.java
export enum Role {
  ADMIN = 'ADMIN',
  USER = 'USER'
}
