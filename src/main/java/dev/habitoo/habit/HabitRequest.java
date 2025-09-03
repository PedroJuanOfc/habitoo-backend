package dev.habitoo.habit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HabitRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    private String name;

    public HabitRequest() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}