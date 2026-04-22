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
  newMember: any = {
    hoTen: '',
    gioiTinh: 'NAM',
    soDoi: 1
  };
  parentId: string = '';
  relationshipType: string = 'RUOT';
  isAdding = false;

  constructor(private memberService: MemberService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  loadMembers() {
    this.memberService.getAllMembers().subscribe({
      next: (data) => {
        this.members = Array.isArray(data) ? data : data.content || [];
      },
      error: (err) => console.error('Error fetching members:', err)
    });
  }

  toggleAddForm() {
    this.isAdding = !this.isAdding;
  }

  onSubmit() {
    const payload = {
      member: this.newMember,
      parentId: this.parentId || null,
      relationshipType: this.relationshipType
    };
    this.memberService.addMember(payload).subscribe({
      next: () => {
        this.loadMembers();
        this.isAdding = false;
        this.newMember = { hoTen: '', gioiTinh: 'NAM', soDoi: 1 };
        this.parentId = '';
      },
      error: (err) => console.error('Error adding member:', err)
    });
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
