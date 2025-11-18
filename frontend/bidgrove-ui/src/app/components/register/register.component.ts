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

  // Validation states
  passwordStrength: string = '';
  passwordStrengthClass: string = '';
  passwordMismatch: boolean = false;
  emailInvalid: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  // 🔹 EMAIL VALIDATION WHILE TYPING
  validateEmail() {
    const email = this.registerForm.email;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    this.emailInvalid = email.length > 0 && !emailRegex.test(email);
  }

  // 🔹 PASSWORD STRENGTH CHECK
  checkPasswordStrength() {
    const pwd = this.registerForm.password;

    if (!pwd) {
      this.passwordStrength = '';
      return;
    }

    if (pwd.length < 8) {
      this.passwordStrength = 'Weak';
      this.passwordStrengthClass = 'weak';
    } else if (pwd.match(/[A-Z]/) && pwd.match(/[0-9]/)) {
      this.passwordStrength = 'Strong';
      this.passwordStrengthClass = 'strong';
    } else {
      this.passwordStrength = 'Medium';
      this.passwordStrengthClass = 'medium';
    }
  }

  // 🔹 PASSWORD MATCH CHECK
  validatePasswordMatch() {
    this.passwordMismatch =
      this.registerForm.password !== this.registerForm.confirmPassword;
  }

  // 🔹 REGISTER CALL WITH VALIDATION BLOCK
  register() {
    this.successMessage = '';
    this.errorMessage = '';

    // ======= Front-end Validation =======

    if (
      !this.registerForm.username ||
      !this.registerForm.email ||
      !this.registerForm.password ||
      !this.registerForm.confirmPassword
    ) {
      this.errorMessage = 'Please fill all required fields.';
      return;
    }

    if (this.emailInvalid) {
      this.errorMessage = 'Enter a valid email address.';
      return;
    }

    if (this.passwordMismatch) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    if (this.registerForm.password.length < 8) {
      this.errorMessage = 'Password must be at least 8 characters long.';
      return;
    }

    // ======= API CALL AFTER VALIDATION =======
    this.authService.register(this.registerForm).subscribe({
      next: (res: any) => {
        this.successMessage = res.message || 'Registered successfully';
        this.errorMessage = '';

        // redirect after 2 sec
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err: any) => {
        this.errorMessage = err.error?.error || 'Something went wrong';
        this.successMessage = '';
      },
    });
  }
}
