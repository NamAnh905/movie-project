export const environment = {
  production: false,
  baseUrl: 'http://localhost:8080',
  api: {
    auth: '/api/auth',
    movies: '/api/movies',
    genres: '/api/genres',
    cinemas: '/api/cinemas',
    showtimes: '/api/showtimes',
    files: '/api/files',      // + thêm
    uploads: '/uploads'       // + thêm (static)
  }
};
