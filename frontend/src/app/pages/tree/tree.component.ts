import { Component, OnInit } from '@angular/core';
import { MemberService } from '../../core/services/member.service';

@Component({
  selector: 'app-tree',
  standalone: true,
  imports: [],
  templateUrl: './tree.component.html',
  styleUrl: './tree.component.scss'
})
export class TreeComponent implements OnInit {
  selectedMember: any = null;
  treeData: any = null;

  constructor(private memberService: MemberService) {}

  ngOnInit(): void {
    // Assuming rootId is 1 for Thủy Tổ
    this.memberService.getTree(1).subscribe({
      next: (data) => {
        this.treeData = data;
      },
      error: (err) => console.error('Error fetching tree:', err)
    });
  }

  selectNode(member: any) {
    this.selectedMember = member;
  }
}
