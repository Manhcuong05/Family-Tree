import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MemberService } from '../../core/services/member.service';
import { TreeNodeComponent } from './tree-node/tree-node.component';

@Component({
  selector: 'app-tree',
  standalone: true,
  imports: [TreeNodeComponent, CommonModule],
  templateUrl: './tree.component.html',
  styleUrl: './tree.component.scss'
})
export class TreeComponent implements OnInit {
  selectedMember: any = null;
  selectedSpouseId: string = '';
  treeData: any = null;
  scale = 1;

  constructor(private memberService: MemberService) {}

  ngOnInit(): void { this.loadRootAndTree(); }

  zoom(delta: number) { this.scale = Math.min(2, Math.max(0.3, +(this.scale + delta).toFixed(1))); }
  resetZoom() { this.scale = 1; }
  onImgError(e: any) { e.target.src = 'assets/images/default-avatar.png'; }

  loadRootAndTree(): void {
    this.memberService.getAllMembers().subscribe({
      next: (members) => {
        const list = Array.isArray(members) ? members : (members.content || []);
        const root = list.find((m: any) => m.soDoi === 1) || list[0];
        if (root) this.fetchTree(root.id);
      },
      error: (err) => console.error('Error loading members:', err)
    });
  }

  fetchTree(rootId: string): void {
    this.memberService.getTree(rootId).subscribe({
      next: (data: any[]) => { this.treeData = this.listToTree(data); },
      error: (err) => console.error('Error fetching tree:', err)
    });
  }

  listToTree(list: any[]): any {
    const map: any = {};
    const roots: any[] = [];
    for (const item of list) { map[item.id] = { ...item, children: [] }; }
    for (const item of list) {
      const node = map[item.id];
      if (node.parentId && map[node.parentId]) {
        map[node.parentId].children.push(node);
      } else if (!node.parentId) {
        roots.push(node);
      }
    }
    return roots[0] || null;
  }

  selectNode(member: any) {
    this.selectedSpouseId = '';
    this.memberService.getMemberById(member.id).subscribe({
      next: (data) => this.selectedMember = { ...data, _isSpouse: false },
      error: (err) => console.error(err)
    });
  }

  selectSpouse(member: any) {
    this.selectedSpouseId = member.spouseId;
    // Build a lightweight object from the spouse data already in the tree node
    this.selectedMember = {
      hoTen: member.spouseHoTen,
      avatarUrl: member.spouseAvatarUrl,
      gioiTinh: member.spouseGioiTinh,
      isAlive: member.spouseIsAlive,
      soDoi: member.soDoi,
      _isSpouse: true,
      _spouseOf: member.hoTen
    };
  }

  get panelTitle(): string {
    if (!this.selectedMember) return '';
    return this.selectedMember._isSpouse
      ? `📜 ${this.selectedMember._spouseOf} — Vợ/Chồng`
      : '📜 Phả Ký Chi Tiết';
  }

  getAvatarUrl(url?: string): string {
    if (!url) return 'assets/images/default-avatar.png';
    return url.startsWith('/') ? 'http://localhost:8080' + url : url;
  }
}
