package com.giapha.api.controller;

import com.giapha.api.dto.AddRelationshipRequest;
import com.giapha.api.dto.CreateMemberRequest;
import com.giapha.api.dto.UpdateMemberRequest;
import com.giapha.api.entity.Member;
import com.giapha.api.entity.Relationship;
import com.giapha.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final com.giapha.api.repository.MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody CreateMemberRequest request) {
        Member created = memberService.createMember(request.getMember(), request.getParentId(), request.getRelationshipType());
        return ResponseEntity.ok(created);
    }

    @PostMapping("/relationship")
    public ResponseEntity<Relationship> addRelationship(@RequestBody AddRelationshipRequest request) {
        Relationship relationship = memberService.addRelationship(request.getParentId(), request.getChildId(), request.getType());
        return ResponseEntity.ok(relationship);
    }

    @GetMapping
    public ResponseEntity<List<com.giapha.api.dto.MemberListItemDto>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembersWithParent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable UUID id, @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request.getMember(), request.getParentId(), request.getRelationshipType()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tree/{rootId}")
    public ResponseEntity<List<com.giapha.api.dto.TreeNodeDto>> getFamilyTree(@PathVariable UUID rootId) {
        return ResponseEntity.ok(memberService.getFamilyTreeFlat(rootId));
    }
}
