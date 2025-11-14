package com.fiap.nova.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NOVA_CATEGORIA_META")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categoria_seq")
    @SequenceGenerator(name = "categoria_seq", sequenceName = "SEQ_CATEGORIA_META", allocationSize = 1)
    @Column(name = "nova_id_categoria_meta")
    private Long id;

    @NotBlank(message = "Category description is required")
    @Column(name = "nova_descricao_categoria", nullable = false, length = 50, unique = true)
    private String description;

}
