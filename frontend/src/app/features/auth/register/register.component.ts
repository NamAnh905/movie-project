import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router'; // <— thêm RouterModule
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,            // <— THÊM DÒNG NÀY
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  host: { class: 'auth-page' }
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  loading = false;

  constructor(private auth: AuthService, private sb: MatSnackBar, private router: Router) {}

  private isValidEmail(v: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v);
  }

  submit(): void {
    const u = this.username.trim();
    const e = this.email.trim();
    const p = this.password.trim();

    if (!u || !e || !p) {
      this.sb.open('Vui lòng nhập đầy đủ tài khoản, email và mật khẩu', 'Đóng', { duration: 2000 });
      return;
    }
    if (!this.isValidEmail(e)) {
      this.sb.open('Email không hợp lệ', 'Đóng', { duration: 1800 });
      return;
    }

    this.loading = true;
    this.auth.register({ username: u, email: e, password: p }).subscribe({
      next: () => {
        this.sb.open('Đăng ký thành công', 'Đóng', { duration: 1500 });
        this.router.navigateByUrl('/login'); // đổi sang '/auth/login' nếu route của bạn là /auth/login
      },
      error: (err) => {
        this.sb.open(err?.error?.message ?? 'Không thể đăng ký', 'Đóng', { duration: 2500 });
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }
}
