import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone:false,
  styleUrl:'./login.component.css',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm = { login: '', password: '' };
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.login(this.loginForm).subscribe({
      next: (res: any) => {
        this.errorMessage = '';
        this.router.navigate(['/user-home']);
      },
      error: (err: any) => {
        this.errorMessage = err.error?.error || 'Invalid credentials';
      }
    });
  }
}
