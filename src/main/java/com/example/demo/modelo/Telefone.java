package com.example.demo.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
//@Entity
//@Table(name = "telefone")
public class Telefone {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

//    @Column(length = 2, nullable = false)
    private String ddd;

//    @Column(length = 9, nullable = false)
    private String numero;

//    @ManyToOne
//    @JoinColumn(name = "codigo_pessoa")
//    @JsonIgnore
    private Pessoa pessoa;
}
