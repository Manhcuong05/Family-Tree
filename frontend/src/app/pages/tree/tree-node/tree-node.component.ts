import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

// =========================================================
// Color palette per branch index (auto-assigned at root level)
// =========================================================
const BRANCH_COLORS = [
  '#d35400', // Nhánh 1 — cam (họ chính)
  '#2980b9', // Nhánh 2 — xanh dương
  '#8e44ad', // Nhánh 3 — tím
  '#27ae60', // Nhánh 4 — xanh lá
  '#c0392b', // Nhánh 5 — đỏ đậm
  '#16a085', // Nhánh 6 — xanh ngọc
];

@Component({
  selector: 'app-tree-node',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="tree-branch">

      <!-- ═══════════════════════════════════════════
           COUPLE NODE (has spouse) or SINGLE NODE
           ═══════════════════════════════════════════ -->
      <div class="node-row">

        <!-- Main person card -->
        <div class="node-card"
             [class.selected]="isSelected"
             [class.deceased]="!node.isAlive"
             [style.--branch-color]="branchColor"
             (click)="onSelect()">
          <div class="avatar-wrap">
            <img [src]="getAvatarUrl(node.avatarUrl)" (error)="onImgError($event)" alt="">
            <span class="gender-dot" [class.female]="node.gioiTinh === 'NU'"></span>
          </div>
          <div class="node-info">
            <div class="node-name">{{ node.hoTen }}</div>
            <div class="node-meta">Đời {{ node.soDoi }}</div>
            @if (node.loaiQuanHe && node.loaiQuanHe !== 'RUOT') {
              <div class="rel-tag" [class]="'rel-' + node.loaiQuanHe.toLowerCase()">
                {{ relLabel(node.loaiQuanHe) }}
              </div>
            }
          </div>
          @if (node.children && node.children.length > 0) {
            <button class="toggle-btn" [class.collapsed]="isCollapsed"
                    (click)="toggleCollapse($event)"
                    [title]="isCollapsed ? 'Mở rộng' : 'Thu gọn'">
              {{ isCollapsed ? '▶' : '▼' }}
              @if (isCollapsed) {
                <span class="count-badge">{{ countDescendants(node) }}</span>
              }
            </button>
          }
        </div>

        <!-- Spouse connector + card -->
        @if (node.spouseId) {
          <div class="spouse-connector">
            <div class="connector-line"></div>
            <div class="marriage-icon" [class.divorced]="node.marriageStatus === 'LY_HON'"
                 [title]="marriageLabel(node)">
              @if (node.marriageStatus === 'LY_HON') { 🚫 } @else { ⚭ }
              @if (node.thuTuVo && node.thuTuVo > 1) {
                <sub>{{ node.thuTuVo }}</sub>
              }
            </div>
          </div>
          <div class="node-card spouse-card"
               [class.deceased]="!node.spouseIsAlive"
               (click)="onSelectSpouse()">
            <div class="avatar-wrap">
              <img [src]="getAvatarUrl(node.spouseAvatarUrl)" (error)="onImgError($event)" alt="">
              <span class="gender-dot" [class.female]="node.spouseGioiTinh === 'NU'"></span>
            </div>
            <div class="node-info">
              <div class="node-name">{{ node.spouseHoTen }}</div>
              <div class="node-meta spouse-label">
                {{ node.thuTuVo && node.thuTuVo > 1 ? 'Vợ ' + toVietnameseOrdinal(node.thuTuVo) : 'Vợ / Chồng' }}
              </div>
            </div>
          </div>
        }

      </div><!-- end .node-row -->

      <!-- Collapsed pill -->
      @if (isCollapsed && node.children?.length > 0) {
        <div class="collapsed-pill" (click)="toggleCollapse($event)"
             [style.border-color]="branchColor" [style.color]="branchColor">
          {{ countDescendants(node) }} người — Nhấn để mở
        </div>
      }

      <!-- ═══════════════════════
           CHILDREN
           ═══════════════════════ -->
      @if (node.children?.length > 0 && !isCollapsed) {
        <div class="children-connector" [style.background]="branchColor"></div>
        <div class="children-row" [class.single]="node.children.length === 1">
          @for (child of node.children; track child.id; let i = $index) {
            <div class="child-slot">
              <div class="child-drop" [style.background]="branchColor"></div>
              <app-tree-node
                [node]="child"
                [selectedNodeId]="selectedNodeId"
                [selectedSpouseId]="selectedSpouseId"
                [branchColor]="getBranchColor(i)"
                (select)="select.emit($event)"
                (selectSpouse)="selectSpouse.emit($event)">
              </app-tree-node>
            </div>
          }
        </div>
      }

    </div><!-- end .tree-branch -->
  `,
  styleUrl: './tree-node.component.scss'
})
export class TreeNodeComponent {
  @Input() node: any;
  @Input() selectedNodeId: string = '';
  @Input() selectedSpouseId: string = '';
  @Input() branchColor: string = BRANCH_COLORS[0];
  @Output() select = new EventEmitter<any>();
  @Output() selectSpouse = new EventEmitter<any>();

  isCollapsed = false;
  readonly BACKEND = 'http://localhost:8080';

  get isSelected() { return this.selectedNodeId === this.node?.id; }

  // If this is a ROOT-level node, each child gets its own branch color
  // If already inside a branch, all children inherit the same branchColor
  getBranchColor(childIndex: number): string {
    // Only diversify at the root (soDoi 1)
    if (this.node?.soDoi === 1) {
      return BRANCH_COLORS[childIndex % BRANCH_COLORS.length];
    }
    return this.branchColor;
  }

  toggleCollapse(e: Event) { e.stopPropagation(); this.isCollapsed = !this.isCollapsed; }

  countDescendants(node: any): number {
    if (!node.children?.length) return 0;
    return node.children.reduce((acc: number, c: any) => acc + 1 + this.countDescendants(c), 0);
  }

  relLabel(type: string): string {
    const map: any = { NUOI: 'Nuôi', RIENG: 'Riêng', DAU_RE: 'Dâu/Rể' };
    return map[type] || '';
  }

  marriageLabel(node: any): string {
    const statusMap: any = { DANG_KET_HON: 'Đang kết hôn', LY_HON: 'Đã ly hôn', DA_MAT: 'Đã mất' };
    const s = statusMap[node.marriageStatus] || '';
    const v = node.thuTuVo > 1 ? ` · Vợ ${this.toVietnameseOrdinal(node.thuTuVo)}` : '';
    return s + v;
  }

  toVietnameseOrdinal(n: number): string {
    const map: any = { 1: 'cả', 2: 'hai', 3: 'ba', 4: 'tư', 5: 'năm' };
    return map[n] || String(n);
  }

  getAvatarUrl(url?: string): string {
    if (!url) return 'assets/images/default-avatar.png';
    return url.startsWith('/') ? this.BACKEND + url : url;
  }

  onImgError(e: any) { e.target.src = 'assets/images/default-avatar.png'; }
  onSelect() { this.select.emit(this.node); }
  onSelectSpouse() { this.selectSpouse.emit({ ...this.node, _isSpouse: true }); }
}
