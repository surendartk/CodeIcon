import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: false,
  styleUrl: './register.component.css',
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  registerForm = {
    name: '',
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    address: '',
  };
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  register() {
    this.authService.register(this.registerForm).subscribe({
      next: (res: any) => {
        this.successMessage = res.message || 'Registered successfully';
        this.errorMessage = '';
      },
      error: (err: any) => {
        this.errorMessage = err.error?.error || 'Something went wrong';
        this.successMessage = '';
      },
    });
  }
}
