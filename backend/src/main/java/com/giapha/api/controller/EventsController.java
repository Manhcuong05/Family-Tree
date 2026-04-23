package com.giapha.api.controller;

import com.giapha.api.entity.Event;
import com.giapha.api.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventsController {

    private final EventRepository eventRepository;

    @GetMapping("/{branchId}")
    public ResponseEntity<List<Event>> getEvents(@PathVariable UUID branchId) {
        return ResponseEntity.ok(eventRepository.findByBranchId(branchId));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventRepository.save(event));
    }
}
