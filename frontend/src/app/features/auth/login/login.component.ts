import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth.service';

const USER_HOME = '/movies'; // ✅ user thường sẽ về trang này

@Component({
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatSnackBarModule
  ],
  styleUrls: ['login.component.scss'],
  templateUrl: 'login.component.html',
  host: { class: 'auth-page' }
})
export class LoginComponent {
  // Khớp với template đang dùng model.email/password
  model = { email: '', password: '' };
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private sb: MatSnackBar
  ) {}

  submit() {
    const u = (this.model.email || '').trim();
    const p = (this.model.password || '').trim();
    if (!u || !p || this.loading) return;

    this.loading = true;
    this.auth.login({ username: u, email: u, password: p }).subscribe({
      next: () => {
        const ru = this.route.snapshot.queryParamMap.get('returnUrl') || '';

        // Admin: vào /admin (ưu tiên ru nếu là nhánh admin)
        // User : theo ru nếu KHÔNG phải admin, ngược lại về USER_HOME
        let target = USER_HOME;
        if (this.auth.isAdmin()) {
          target = ru && ru.startsWith('/admin') ? ru : '/admin';
        } else {
          target = ru && !ru.startsWith('/admin') ? ru : USER_HOME;
        }

        this.sb.open('Đăng nhập thành công', 'Đóng', { duration: 1200 });
        this.router.navigateByUrl(target);
      },
      error: (e) => {
        const msg = e?.error?.message || e?.message || 'Đăng nhập thất bại';
        this.sb.open(msg, 'Đóng', { duration: 2500 });
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }
}
