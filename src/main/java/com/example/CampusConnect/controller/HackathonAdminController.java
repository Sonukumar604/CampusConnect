package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CreateHackathonDTO;
import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.service.HackathonAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hackathons")
public class HackathonAdminController {

    @Autowired
    private HackathonAdminService adminService;

    @PostMapping("/{adminId}")
    public ResponseEntity<HackathonDTO> createHackathon(
            @PathVariable Long adminId,
            @Valid @RequestBody CreateHackathonDTO dto
    ) {
        return new ResponseEntity<>(adminService.createHackathon(dto, adminId), HttpStatus.CREATED);
    }

    @PutMapping("/{hackathonId}")
    public ResponseEntity<HackathonDTO> updateHackathon(
            @PathVariable Long hackathonId,
            @Valid @RequestBody CreateHackathonDTO dto
    ) {
        return ResponseEntity.ok(adminService.updateHackathon(hackathonId, dto));
    }

    @GetMapping
    public ResponseEntity<List<HackathonDTO>> getAll() {
        return ResponseEntity.ok(adminService.getAllHackathons());
    }

    @GetMapping("/{hackathonId}")
    public ResponseEntity<HackathonDTO> getOne(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(adminService.getHackathonById(hackathonId));
    }

    @PutMapping("/{hackathonId}/toggle")
    public ResponseEntity<String> toggleStatus(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(adminService.toggleHackathonStatus(hackathonId));
    }

    @DeleteMapping("/{hackathonId}")
    public ResponseEntity<String> delete(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(adminService.deleteHackathon(hackathonId));
    }
    @GetMapping("/paged")
    public ResponseEntity<Page<HackathonDTO>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(adminService.getAllHackathonsPaged(page, size, sortBy, direction));
    }
}

