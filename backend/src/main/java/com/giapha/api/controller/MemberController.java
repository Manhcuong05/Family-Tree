package com.giapha.api.controller;

import com.giapha.api.dto.AddRelationshipRequest;
import com.giapha.api.dto.CreateMemberRequest;
import com.giapha.api.dto.MemberFlatDto;
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
@CrossOrigin(origins = "*") // For development
public class MemberController {

    private final MemberService memberService;

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

    @GetMapping("/tree/{rootId}")
    public ResponseEntity<List<MemberFlatDto>> getFamilyTree(@PathVariable UUID rootId) {
        List<MemberFlatDto> tree = memberService.getFamilyTreeFlat(rootId);
        return ResponseEntity.ok(tree);
    }
}
