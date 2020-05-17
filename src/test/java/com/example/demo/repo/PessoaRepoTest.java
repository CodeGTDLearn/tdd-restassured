package com.example.demo.repo;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.filtro.FiltroPessoa;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/load-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class PessoaRepoTest {

    @Autowired
    PessoaRepoInt repo;

    @Test
    public void procura_pessoa_pelo_cpf() throws Exception {
        Optional<Pessoa> optional = repo.findByCpf("38767897100");
        assertThat(optional.isPresent()).isTrue();

        Pessoa pessoaTestada = optional.get();
        assertThat(pessoaTestada.getCodigo()).isEqualTo(3L);
        assertThat(pessoaTestada.getNome()).isEqualTo("Cauê");
        assertThat(pessoaTestada.getCpf()).isEqualTo("38767897100");
    }

    @Test
    public void not_find_pessoa_by_cpf() throws Exception {
        Optional<Pessoa> optional = repo.findByCpf("3876789710000");
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    public void find_person_by_tel_with_ddd_tel() throws Exception {
        Optional<Pessoa> optional = repo.findByTel("86", "35006330");

        assertThat(optional.isPresent()).isTrue();

        Pessoa pessoaTestada = optional.get();
        assertThat(pessoaTestada.getCodigo()).isEqualTo(3L);
        assertThat(pessoaTestada.getNome()).isEqualTo("Cauê");
        assertThat(pessoaTestada.getCpf()).isEqualTo("38767897100");
    }

    @Test
    public void not_find_person_by_tel_with_ddd_tel() throws Exception {
        Optional<Pessoa> optional = repo.findByCpf("387678971001111");
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    public void filter_using_name_fraction() throws Exception {
        FiltroPessoa p = FiltroPessoa.builder()
                .nome("a").cpf("0").ddd("86").phone("5701").build();

        List<Pessoa> pessoas = repo.multFilter(p.getNome(), p.getCpf(), p.getDdd(), p.getPhone());
        assertThat(pessoas.size()).isEqualTo(1);
    }
}
