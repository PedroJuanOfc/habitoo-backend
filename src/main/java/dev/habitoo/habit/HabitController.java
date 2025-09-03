package dev.habitoo.habit;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;

    public HabitController(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @GetMapping
    public List<HabitResponse> list() {
        return habitRepository.findAll()
                .stream()
                .map(h -> new HabitResponse(h.getId(), h.getName(), h.getCreatedAt()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<HabitResponse> create(@Valid @RequestBody HabitRequest body) {
        Habit h = new Habit(body.getName());
        h = habitRepository.save(h);
        HabitResponse resp = new HabitResponse(h.getId(), h.getName(), h.getCreatedAt());
        return ResponseEntity.created(URI.create("/habits/" + h.getId())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitResponse> getById(@PathVariable Long id) {
        return habitRepository.findById(id)
                .map(h -> ResponseEntity.ok(new HabitResponse(h.getId(), h.getName(), h.getCreatedAt())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}