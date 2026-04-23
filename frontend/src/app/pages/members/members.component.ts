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
        this.newMember.soDoi = parent.soDoi + 1;
      }
    } else {
      this.newMember.soDoi = 1;
    }
  }

  onSubmit() {
    if (!this.newMember.hoTen) {
      alert('Vui lòng nhập họ tên!');
      return;
    }

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
      ngayMatAmLich: ''
    };
    this.parentId = '';
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
