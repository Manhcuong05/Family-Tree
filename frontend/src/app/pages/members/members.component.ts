import { Component, OnInit } from '@angular/core';
import { MemberService } from '../../core/services/member.service';

import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './members.component.html',
  styleUrl: './members.component.scss'
})
export class MembersComponent implements OnInit {
  members: any[] = [];
  searchTerm: string = '';
  newMember: any = {
    hoTen: '',
    gioiTinh: 'NAM',
    soDoi: 1,
    isAlive: true,
    ngaySinhAmLich: '',
    ngayMatAmLich: ''
  };
  parentId: string = '';
  relationshipType: string = 'RUOT';
  isAdding = false;
  isEditing = false;
  editingId: string | null = null;
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  backendUrl = 'http://localhost:8080';

  constructor(private memberService: MemberService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  get filteredMembers() {
    if (!this.searchTerm) return this.members;
    const term = this.searchTerm.toLowerCase();
    return this.members.filter(m => 
      m.hoTen.toLowerCase().includes(term) || 
      (m.tieuSu && m.tieuSu.toLowerCase().includes(term))
    );
  }

  loadMembers() {
    this.memberService.getAllMembers().subscribe({
      next: (data) => {
        this.members = Array.isArray(data) ? data : (data.content || []);
      },
      error: (err) => console.error('Error fetching members:', err)
    });
  }

  toggleAddForm() {
    this.isAdding = !this.isAdding;
  }

  onParentChange() {
    if (this.parentId) {
      const parent = this.members.find(m => m.id === this.parentId);
      if (parent) {
        if (this.relationshipType === 'VO_CHONG') {
          this.newMember.soDoi = parent.soDoi;
          this.newMember.gioiTinh = parent.gioiTinh === 'NAM' ? 'NU' : 'NAM';
        } else {
          this.newMember.soDoi = parent.soDoi + 1;
        }
      }
    } else {
      this.newMember.soDoi = 1;
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => this.previewUrl = e.target.result;
      reader.readAsDataURL(file);
    }
  }

  onSubmit() {
    if (!this.newMember.hoTen) {
      alert('Vui lòng nhập họ tên!');
      return;
    }

    if (this.selectedFile) {
      this.memberService.uploadAvatar(this.selectedFile).subscribe({
        next: (res) => {
          this.newMember.avatarUrl = res.url;
          if (this.isEditing && this.editingId) {
            this.doUpdateMember(this.editingId);
          } else {
            this.saveMember();
          }
        },
        error: (err) => {
          console.error('Avatar upload failed', err);
          if (this.isEditing && this.editingId) {
            this.doUpdateMember(this.editingId);
          } else {
            this.saveMember();
          }
        }
      });
    } else {
      if (this.isEditing && this.editingId) {
        this.doUpdateMember(this.editingId);
      } else {
        this.saveMember();
      }
    }
  }

  editMember(member: any) {
    this.newMember = { ...member };
    this.isEditing = true;
    this.isAdding = true;
    this.editingId = member.id;
    this.parentId = member.parentId || '';
    this.relationshipType = member.loaiQuanHe || 'RUOT';
    this.previewUrl = member.avatarUrl ? (this.backendUrl + member.avatarUrl) : null;
  }

  doUpdateMember(id: string) {
    const payload = {
      member: { ...this.newMember },
      parentId: this.parentId || null,
      relationshipType: this.relationshipType
    };

    this.memberService.updateMember(id, payload).subscribe({
      next: () => {
        alert('Cập nhật thành công! Các thế hệ sau đã được tự động điều chỉnh.');
        this.loadMembers();
        this.isAdding = false;
        this.isEditing = false;
        this.resetForm();
      },
      error: (err) => {
        console.error('Update failed', err);
        alert('Lỗi cập nhật: ' + (err.error?.message || 'Lỗi hệ thống'));
      }
    });
  }

  saveMember() {
    const payload = {
      member: { ...this.newMember },
      parentId: this.parentId || null,
      relationshipType: this.relationshipType
    };

    this.memberService.addMember(payload).subscribe({
      next: () => {
        alert('Thêm thành viên thành công!');
        this.loadMembers();
        this.isAdding = false;
        this.resetForm();
      },
      error: (err) => {
        console.error('Error adding member:', err);
        alert('Có lỗi xảy ra: ' + (err.error?.message || 'Lỗi hệ thống'));
      }
    });
  }

  resetForm() {
    this.newMember = { 
      hoTen: '', 
      gioiTinh: 'NAM', 
      soDoi: 1, 
      isAlive: true,
      ngaySinhAmLich: '',
      ngayMatAmLich: '',
      avatarUrl: ''
    };
    this.parentId = '';
    this.isEditing = false;
    this.editingId = null;
    this.selectedFile = null;
    this.previewUrl = null;
  }

  deleteMember(id: string) {
    if (confirm('Bạn có chắc muốn xoá thành viên này?')) {
      this.memberService.deleteMember(id).subscribe({
        next: () => this.loadMembers(),
        error: (err) => console.error('Error deleting member:', err)
      });
    }
  }
}
