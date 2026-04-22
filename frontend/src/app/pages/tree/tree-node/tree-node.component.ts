import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tree-node',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="tree-node-wrapper">
      <div class="node" [class.selected]="isSelected" (click)="onSelect()">
        <div class="node-name">{{ node.name }}</div>
        <div class="node-gen">Đời {{ node.soDoi }}</div>
      </div>
      
      @if (node.children && node.children.length > 0) {
        <div class="node-children">
          @for (child of node.children; track child.id) {
            <app-tree-node 
              [node]="child" 
              [selectedNodeId]="selectedNodeId"
              (select)="select.emit($event)">
            </app-tree-node>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .tree-node-wrapper {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin: 0 1rem;
    }
    .node {
      padding: 0.8rem 1.2rem;
      background: var(--color-glass);
      border: 1px solid var(--color-glass-border);
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      text-align: center;
      min-width: 120px;
      margin-bottom: 2rem;
      position: relative;
    }
    .node:hover {
      background: var(--color-primary);
      transform: translateY(-5px);
    }
    .node.selected {
      border-color: var(--color-primary);
      box-shadow: 0 0 15px var(--color-primary);
    }
    .node-name {
      font-weight: 600;
      font-size: 0.9rem;
    }
    .node-gen {
      font-size: 0.75rem;
      opacity: 0.7;
      margin-top: 0.25rem;
    }
    .node-children {
      display: flex;
      justify-content: center;
      position: relative;
      padding-top: 2rem;
    }
    /* Simple connector lines */
    .node-children::before {
      content: '';
      position: absolute;
      top: 0;
      left: 50%;
      width: 2px;
      height: 2rem;
      background: var(--color-glass-border);
    }
  `]
})
export class TreeNodeComponent {
  @Input() node: any;
  @Input() selectedNodeId: string = '';
  @Output() select = new EventEmitter<any>();

  get isSelected() {
    return this.selectedNodeId === this.node.id;
  }

  onSelect() {
    this.select.emit(this.node);
  }
}
