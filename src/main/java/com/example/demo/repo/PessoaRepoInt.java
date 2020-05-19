package com.example.demo.repo;

import com.example.demo.modelo.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaRepoInt extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByCpf(String cpf);

    Optional<Pessoa> findFirstByOrderByNomeAsc();

    Optional<Pessoa> findFirstByOrderByCpfAsc();

    @Query("select p from Pessoa p join p.telefones t where " +
            "t.ddd = :ddd AND " +
            "t.numero = :number")
    Optional<Pessoa> findByTelDdd(@Param("ddd") String ddd, @Param("number") String number);

    @Query("select p from Pessoa p " +
            "join p.telefones t where " +
            "p.nome like %:nome% and " +
            "p.cpf like %:cpf% and " +
            "t.ddd like %:ddd% and " +
            "t.numero like %:numero%")
    List<Pessoa> findByMultiFilterCascade(@Param("nome") String nome,
                                          @Param("cpf") String cpf,
                                          @Param("ddd") String ddd,
                                          @Param("numero") String numero);
}
