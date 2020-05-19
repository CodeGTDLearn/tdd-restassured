package com.example.demo.repo;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.example.demo.databuilders.PessoaBuilder.pessoaComCpfeTel;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@TestPropertySource("classpath:application.properties")
@Sql(value = "/load-dbTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-dbTest.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DataJpaTest
@RunWith(SpringRunner.class)
public class PessoaRepoTest {

    @Autowired
    private PessoaRepoInt repo;

    private Pessoa pessoa1;
    private Pessoa pessoa2;

    @Before
    public void setUp() throws Exception {
        pessoa1 = pessoaComCpfeTel().gerar();
        pessoa2 = pessoaComCpfeTel().gerar();
        repo.save(pessoa1);
    }

    @After
    public void tearDown() {
        repo.delete(pessoa1);
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
        int teste = 1;

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
        FiltroPessoaCascade filtro =
                FiltroPessoaCascade
                        .builder()
                        .nome("a")
                        .cpf("4")
                        .phone("38416516")
                        .build();

        List<Pessoa> pessoas =
                repo.findByMultiFilterCascade(
                        filtro.getNome(),
                        filtro.getCpf(),
                        filtro.getDdd(),
                        filtro.getPhone());
        assertThat(pessoas.size()).isEqualTo(1);
    }
}
