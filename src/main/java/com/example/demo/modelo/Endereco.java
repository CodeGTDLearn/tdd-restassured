package com.example.demo.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
//@Entity
//@Table(name = "endereco")
public class Endereco {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    private String logradouro;
    private Integer numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

//    @ManyToOne
//    @JoinColumn(name = "codigo_pessoa")
//    @JsonIgnore
    private Pessoa pessoa;
}
