import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        if (response && response.token) {
          localStorage.setItem('token', response.token);
          // In a real app, parse JWT to get user info/roles
        }
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
  }

  createManagedUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/create-user`, userData);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getUsername(): string {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = token.split('.')[1];
        const decoded = JSON.parse(atob(payload));
        return decoded.sub || 'Người Dùng';
      } catch (e) {
        return 'Người Dùng';
      }
    }
    return '';
  }
}
