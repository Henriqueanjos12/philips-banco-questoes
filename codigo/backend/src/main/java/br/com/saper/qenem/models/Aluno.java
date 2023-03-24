package br.com.saper.qenem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="aluno_id")
    private Long id;

    @OneToOne(targetEntity = Usuario.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(targetEntity = Prova.class, cascade = CascadeType.ALL, mappedBy = "aluno")
    private Set<Prova> provas;
}
