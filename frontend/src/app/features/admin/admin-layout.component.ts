import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  selector: 'app-admin-layout',
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-layout.component.html',   // ✅ dùng file HTML
  styleUrls: ['./admin-layout.component.scss']    // (nếu có SCSS)
})
export class AdminLayoutComponent {
  constructor(private auth: AuthService, private router: Router) {}
  onLogout(): void {
    this.auth.logout();                    // xoá token
    this.router.navigateByUrl('/auth/login'); // về trang đăng nhập
  }
}
