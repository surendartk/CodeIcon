import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-home',
  standalone: false,
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css'],
})
export class AdminHomeComponent {
  user: any;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.user = this.authService.getUser(); // ✅ Correct method
  }

  logout() {
    this.authService.logout();
  }
}
