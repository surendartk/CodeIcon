import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-my-profile',
  standalone: false,
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
})
export class MyProfileComponent implements OnInit {
  profile: any = {};
  editMode = false;
  passEditMode = false;

  editForm: any = { name: '', username: '', email: '', phone: '', address: '' };
  passForm: any = {
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
  };

  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.authService.getMyProfile().subscribe({
      next: (data) => (this.profile = data),
      error: (err) =>
        (this.errorMessage = err.error?.error || 'Cannot load profile'),
    });
  }

  enableEdit() {
    this.editMode = true;
    this.passEditMode = false;
    this.editForm = { ...this.profile };
  }

  cancelEdit() {
    this.editMode = false;
  }

  saveProfile() {
    this.authService.updateProfile(this.editForm).subscribe({
      next: (res) => {
        this.profile = res;
        this.editMode = false;
      },
      error: (err) =>
        (this.errorMessage = err.error?.error || 'Cannot update profile'),
    });
  }

  enablePasswordEdit() {
    this.passEditMode = true;
    this.editMode = false;
    this.passForm = {
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: '',
    };
    this.successMessage = '';
    this.errorMessage = '';
  }

  cancelPasswordEdit() {
    this.passEditMode = false;
    this.successMessage = '';
    this.errorMessage = '';
  }

  changePassword() {
    if (this.passForm.newPassword !== this.passForm.confirmNewPassword) {
      this.errorMessage = 'New passwords do not match';
      this.successMessage = '';
      return;
    }

    const payload = {
      currentPassword: this.passForm.currentPassword,
      newPassword: this.passForm.newPassword,
    };

    this.authService.changePassword(payload).subscribe({
      next: (res: any) => {
        this.successMessage = res.message;
        this.errorMessage = '';
        setTimeout(() => this.cancelPasswordEdit(), 1500);
      },
      error: (err: any) => {
        this.errorMessage =
          err.error?.error || err.error?.message || 'Something went wrong';
        this.successMessage = '';
      },
    });
  }
}
