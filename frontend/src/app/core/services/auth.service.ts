import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  private loggedIn = new BehaviorSubject<boolean>(!!localStorage.getItem('token'));
  public isLoggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        if (response && response.token) {
          localStorage.setItem('token', response.token);
          this.loggedIn.next(true);
        }
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    this.loggedIn.next(false);
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

  getRole(): string {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = token.split('.')[1];
        const decoded = JSON.parse(atob(payload));
        // The role is often in a specific claim, check the backend token builder
        // In our backend, we don't add role to claims yet. Let's fix that later.
        // For now, let's assume it's there or just check username for admin.
        if (decoded.sub === 'admin') return 'ADMIN';
        return decoded.role || 'USER';
      } catch (e) {
        return 'USER';
      }
    }
    return '';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }
}
