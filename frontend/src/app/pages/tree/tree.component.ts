import { Component, OnInit } from '@angular/core';
import { MemberService } from '../../core/services/member.service';
import { TreeNodeComponent } from './tree-node/tree-node.component';

@Component({
  selector: 'app-tree',
  standalone: true,
  imports: [TreeNodeComponent],
  templateUrl: './tree.component.html',
  styleUrl: './tree.component.scss'
})
export class TreeComponent implements OnInit {
  selectedMember: any = null;
  treeData: any = null;

  constructor(private memberService: MemberService) {}

  ngOnInit(): void {
    this.loadRootAndTree();
  }

  loadRootAndTree(): void {
    this.memberService.getAllMembers().subscribe({
      next: (members) => {
        const root = members.find((m: any) => m.soDoi === 1);
        if (root) {
          this.fetchTree(root.id);
        } else if (members.length > 0) {
          this.fetchTree(members[0].id);
        }
      },
      error: (err) => console.error('Error loading members for tree:', err)
    });
  }

  fetchTree(rootId: string): void {
    this.memberService.getTree(rootId).subscribe({
      next: (data) => {
        this.treeData = this.listToTree(data);
      },
      error: (err) => console.error('Error fetching tree:', err)
    });
  }

  listToTree(list: any[]): any {
    const map: any = {};
    let node: any;
    const roots: any[] = [];
    
    for (let i = 0; i < list.length; i += 1) {
      map[list[i].id] = { ...list[i], children: [] };
    }
    
    for (let i = 0; i < list.length; i += 1) {
      node = map[list[i].id];
      if (node.pid) {
        if (map[node.pid]) {
          map[node.pid].children.push(node);
        } else {
          roots.push(node);
        }
      } else {
        roots.push(node);
      }
    }
    return roots[0];
  }


  selectNode(member: any) {
    this.selectedMember = member;
  }
}
