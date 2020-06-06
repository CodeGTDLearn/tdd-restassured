package com.example.demo.repo;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import jdk.nashorn.internal.ir.CallNode;
import lombok.var;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.example.demo.databuilders.ObjectMotherPessoa.pessoaComCpfeTel;
import static com.example.demo.repo.filtro.FiltroPessoaCascade.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.*;

//AUTOCONFIGURA H2, HSQL, Derby, override application-test.properties
@AutoConfigureTestDatabase(connection = HSQL)

//CONFIGURA usando o application-test.properties
//Usa o application-test.properties
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource("classpath:application-test.properties")

@Sql(value = "/preload-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

@DirtiesContext //Diz ao TestFramework p/ fechar e recriar o contexto para testes posteriores.

//Carrega JPA + MemoryDB embarcado p/ teste (Datasource substituido por MemoryDB Embarcado)
//Nao Depende de Script SQL de PreLoad,
//MAS permite Script SQL de PreLoad(preload-ScriptHSQL-dbTest.sql)
@DataJpaTest
@RunWith(SpringRunner.class) // inicia o contexto de test do Spring + roda testes + permite multiplo frameworks de test
public class RepoTest {

    @Autowired
    private PessoaRepoInt repo;

    private Pessoa pessoa1;
    private Pessoa pessoa2;

    @Before
    public void setUp() {
        pessoa1 = pessoaComCpfeTel().gerar();
        pessoa2 = pessoaComCpfeTel().gerar();
        repo.save(pessoa1);
    }

    @After
    public void tearDown() {
        //  repo.delete(pessoa1);
        repo.deleteAll();
    }

    @Test
    public void procura_pessoa_pelo_cpf() {
        Optional<Pessoa> optional = repo.findByCpf(pessoa1.getCpf());
        assertThat(optional.isPresent()).isTrue();

        Pessoa pessoaTestada = optional.get();
        assertThat(pessoaTestada.getCodigo()).isEqualTo(pessoa1.getCodigo());
        assertThat(pessoaTestada.getNome()).isEqualTo(pessoa1.getNome());
        assertThat(pessoaTestada.getCpf()).isEqualTo(pessoa1.getCpf());
    }

    @Test
    public void not_find_pessoa_by_cpf() {
        Optional<Pessoa> optional = repo.findByCpf(pessoa2.getCpf());
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    public void find_person_by_tel_with_ddd() {
        Optional<Pessoa> optional = repo.findFirstByOrderByNomeAsc();
        Pessoa pessoaSource = optional.get();

        String ddd = pessoaSource.getTelefones().get(0).getDdd();
        String numero = pessoaSource.getTelefones().get(0).getNumero();

        Optional<Pessoa> optional2 = repo.findByTelDdd(ddd, numero);
        assertThat(optional2.isPresent()).isTrue();

        Pessoa pessoaTarget = optional2.get();
        assertThat(pessoaSource.getCodigo()).isEqualTo(pessoaTarget.getCodigo());
        assertThat(pessoaSource.getNome()).isEqualTo(pessoaTarget.getNome());
        assertThat(pessoaSource.getCpf()).isEqualTo(pessoaTarget.getCpf());
    }

    @Test
    public void not_find_person_by_tel_with_ddd_tel() {
        String ddd = pessoa2.getTelefones().get(0).getDdd();
        String numero = pessoa2.getTelefones().get(0).getNumero();

        Optional<Pessoa> optional = repo.findByTelDdd(ddd, numero);

        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    public void multiFilterTest() {
        var personToTest = repo.findFirstByOrderByNomeAsc();

        final String ddd = personToTest.get().getTelefones().get(0).getDdd();
        final String nome = personToTest.get().getNome();
        final String cpf = personToTest.get().getCpf();
        final String numero = personToTest.get().getTelefones().get(0).getNumero();

        FiltroPessoaCascade multiFilter = builder()
                .nome(nome)
                .cpf(cpf)
                .ddd(ddd)
                .phone(numero)
                .build();

        List<Pessoa> pessoas =
                repo.findByMultiFilterCascade(
                        multiFilter.getNome(),
                        multiFilter.getCpf(),
                        multiFilter.getDdd(),
                        multiFilter.getPhone());

        assertThat(pessoas.size()).isEqualTo(1);
    }

    @Test
    public void findAllMockmvc() {
        List<Pessoa> list = repo.findAll();
        assertThat(list.size()).isEqualTo(list.size());
    }
}
