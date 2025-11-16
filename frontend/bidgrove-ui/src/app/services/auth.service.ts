import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private API_URL = environment.authApi;
  private jwtHelper = new JwtHelperService();

  private currentUserSubject = new BehaviorSubject<any>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.loadUserFromStorage();
  }

  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }

  // ---------------- REGISTER ----------------
  register(data: any) {
    return this.http.post(`${this.API_URL}/register`, data);
  }

  // ---------------- LOGIN ----------------
  login(data: any) {
    return this.http.post<any>(`${this.API_URL}/login`, data).pipe(
      tap((response) => {
        if (this.isBrowser()) {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
        }
        this.decodeAndStoreUser(response.accessToken);
      })
    );
  }

  // ---------------- REFRESH TOKEN ----------------
  refreshToken() {
    if (!this.isBrowser()) return;
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return;

    const headers = new HttpHeaders({ 'X-Refresh-Token': refreshToken });

    return this.http.post<any>(`${this.API_URL}/refresh`, {}, { headers }).pipe(
      tap((response) => {
        if (this.isBrowser())
          localStorage.setItem('accessToken', response.accessToken);
        this.decodeAndStoreUser(response.accessToken);
      })
    );
  }

  private decodeAndStoreUser(token: string) {
    const decoded = this.jwtHelper.decodeToken(token);
    const user = {
      id: decoded.userId,
      username: decoded.userName,
      email: decoded.sub,
      role: decoded.role,
    };
    this.currentUserSubject.next(user);
  }

  loadUserFromStorage() {
    if (!this.isBrowser()) return;
    const token = localStorage.getItem('accessToken');
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      this.decodeAndStoreUser(token);
    } else if (localStorage.getItem('refreshToken')) {
      this.refreshToken()?.subscribe();
    }
  }

  logout() {
    if (this.isBrowser()) {
      const token = localStorage.getItem('accessToken');
      if (token) {
        const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
        this.http.post(`${this.API_URL}/logout`, {}, { headers }).subscribe();
      }
      localStorage.clear();
    }
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    if (!this.isBrowser()) return false;
    const token = localStorage.getItem('accessToken');
    return !!token && !this.jwtHelper.isTokenExpired(token);
  }

  getRole(): string {
    return this.currentUserSubject.value?.role;
  }

  getUser() {
    return this.currentUserSubject.value;
  }

  getMyProfile() {
    const token = this.isBrowser() ? localStorage.getItem('accessToken') : null;
    return this.http.get(`${this.API_URL}/my-profile`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  updateProfile(data: any) {
    const token = this.isBrowser() ? localStorage.getItem('accessToken') : null;
    return this.http.put(`${this.API_URL}/update-profile`, data, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  changePassword(data: any) {
    const token = this.isBrowser() ? localStorage.getItem('accessToken') : null;
    return this.http.post(`${this.API_URL}/change-password`, data, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }
}
