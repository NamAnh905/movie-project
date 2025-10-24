// src/app/app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TokenInterceptor } from './core/interceptors/token.interceptor';

// Standalone root + screens
import { AppComponent } from './app.component';
import { MovieDetailComponent } from './features/movies/movie-detail/movie-detail.component';
import { AuthLayoutComponent } from './features/auth/auth-layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';

@NgModule({
  declarations: [], // dùng kiến trúc standalone → để trống
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,

    // Standalone components (đưa vào imports)
    AppComponent,
    MovieDetailComponent,
    AuthLayoutComponent,
    LoginComponent,
    RegisterComponent,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
