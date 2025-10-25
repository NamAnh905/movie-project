# 🎬 4SCinema — Movie Management System (Monorepo)

Hệ thống web đặt vé xem phim trực tuyến.
- 🌐 Frontend (Angular): [https://movie-project-two-eta.vercel.app](https://movie-project-two-eta.vercel.app)
- 🛠️ Backend (Spring Boot): [https://movie-be-hecl.onrender.com/api](https://movie-be-hecl.onrender.com/api)
- 💾 Database: MySQL (online)
- 🔐 JWT Authentication, RESTful API, Katalon Testing

## 📂 Cấu trúc
movie-project/
├─ backend/  → Spring Boot (API, JWT, MySQL)
└─ frontend/ → Angular (UI, routing, form, API calls)

## 🚀 Chạy local
### Backend
```bash
cd backend
./mvnw -DskipTests package
java -jar target/*.jar
