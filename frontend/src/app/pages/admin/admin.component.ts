import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="header-actions">
        <h1>Quản Trị Hệ Thống</h1>
      </div>
      
      <div class="row mt-4">
        <div class="col-md-6">
          <div class="glass-card p-4">
            <h3>Tạo Tài Khoản Quản Lý</h3>
            <p class="text-muted mb-4">Dành cho Admin để tạo tài khoản Moderator (Cai quản chi/nhánh).</p>
            
            <form (ngSubmit)="onCreateUser()" #userForm="ngForm">
              <div class="mb-3">
                <label class="form-label">Tên đăng nhập</label>
                <input type="text" name="username" class="form-input" [(ngModel)]="userData.username" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Mật khẩu</label>
                <input type="password" name="password" class="form-input" [(ngModel)]="userData.password" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Quyền hạn</label>
                <select name="role" class="form-select" [(ngModel)]="userData.role">
                  <option value="MODERATOR">Moderator (Quản lý chi)</option>
                  <option value="USER">User (Xem dữ liệu)</option>
                </select>
              </div>
              
              <div *ngIf="message" [class]="isError ? 'text-danger' : 'text-success'" class="mb-3">
                {{ message }}
              </div>
              
              <button type="submit" class="btn btn-primary" [disabled]="!userForm.valid">Tạo Tài Khoản</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .text-success { color: #28a745; }
    .text-danger { color: #dc3545; }
  `]
})
export class AdminComponent {
  userData = {
    username: '',
    password: '',
    role: 'MODERATOR'
  };
  message = '';
  isError = false;

  constructor(private authService: AuthService) {}

  onCreateUser() {
    this.authService.createManagedUser(this.userData).subscribe({
      next: () => {
        this.message = 'Tạo tài khoản thành công!';
        this.isError = false;
        this.userData = { username: '', password: '', role: 'MODERATOR' };
      },
      error: (err) => {
        this.message = 'Lỗi: ' + (err.error?.message || 'Không thể tạo tài khoản');
        this.isError = true;
      }
    });
  }
}
