import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  styleUrl: './login.component.css',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  loginForm = { login: '', password: '' };
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  login() {
    this.authService.login(this.loginForm).subscribe({
      next: () => {
        this.errorMessage = '';

        const user = this.authService.getUser();

        if (user?.role === 'ADMIN') {
          this.router.navigate(['/admin-home']);
        } else if (user?.role === 'USER') {
          this.router.navigate(['/user-home']);
        } else {
          this.errorMessage = 'Unknown user role';
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.error || 'Invalid credentials';
      },
    });
  }
}
