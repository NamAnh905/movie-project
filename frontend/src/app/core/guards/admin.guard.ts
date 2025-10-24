import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    // Chưa đăng nhập -> sang login kèm returnUrl
    if (!this.auth.isLoggedIn()) {
      return this.router.createUrlTree(['/auth/login'], { queryParams: { returnUrl: state.url } });
    }
    // Đúng quyền ADMIN -> cho phép
    if (this.auth.isAdmin()) return true;

    // Có token nhưng không phải ADMIN -> về giao diện user
    return this.router.parseUrl('/');
  }
}
