import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <main class="auth">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    .auth {
      min-height: 100vh;
      display: grid;
      place-items: center;
      background: #0b0c10;
      color: #e8edf2;
    }
  `]
})
export class AuthLayoutComponent {}
