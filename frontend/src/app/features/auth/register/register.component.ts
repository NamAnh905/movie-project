import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../api/auth/auth.service';

@Component({
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatSnackBarModule
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  host: { class: 'auth-page' }
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  fullName = '';
  loading = false;

  constructor(private auth: AuthService, private sb: MatSnackBar, private router: Router) {}

  submit(): void {
    const u = this.username.trim();
    const e = this.email.trim();
    const p = this.password.trim();
    const f = this.fullName.trim();

    if (!u || !e || !p) {
      this.sb.open('Vui lòng nhập đầy đủ thông tin bắt buộc', 'Đóng', { duration: 2000 });
      return;
    }

    this.loading = true;
    this.auth.register({ username: u, email: e, password: p, fullName: f }).subscribe({
      next: () => {
        this.sb.open('Đăng ký thành công', 'Đóng', { duration: 1500 });
        this.router.navigateByUrl('/auth/login');
      },
      error: (err) => {
        this.sb.open(err?.error?.message ?? 'Không thể đăng ký', 'Đóng', { duration: 2500 });
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }
}
