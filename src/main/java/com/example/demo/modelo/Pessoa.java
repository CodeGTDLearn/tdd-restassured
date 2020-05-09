package com.example.demo.modelo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Data
//@Entity
//@Table(name = "pessoa")
public class Pessoa {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

//    @Column(length = 80, nullable = false)
    private String nome;

//    @Column(length = 11, nullable = false)
    private String cpf;

//    @OneToMany(mappedBy = "pessoa")
    private List<Endereco> enderecos;

//    @OneToMany(mappedBy = "pessoa")
    private List<Telefone> telefones;
}
