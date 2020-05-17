package com.example.demo.repo;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.filtro.FiltroPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PessoaRepoInt extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByCpf(String cpf);

    @Query("select bean from Pessoa bean join bean.telefones tele where tele.ddd =:ddd and tele.numero = :numero")
    Optional<Pessoa> findByTel(@Param("ddd") String ddd, @Param("numero") String numero);

    @Query("select p from Pessoa p join p.telefones t where " +
            "p.nome like %:nome% and " +
            "p.cpf like %:cpf% and " +
            "t.ddd like %:ddd% and " +
            "t.numero like %:numero%")
    List<Pessoa> multFilter(@Param("nome") String nome,
                            @Param("cpf") String cpf,
                            @Param("ddd") String ddd,
                            @Param("numero") String numero);
}
