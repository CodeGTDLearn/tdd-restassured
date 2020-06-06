-- deleta o conteudo das tabelas
DELETE FROM telefone;
DELETE FROM endereco;
DELETE FROM pessoa;

-- reinicia o ID's, apartir do 01
--SINTAX exlcusiva do HSQLDB
ALTER SEQUENCE telefone_id_seq RESTART WITH 1;
ALTER SEQUENCE endereco_id_seq RESTART WITH 1;
ALTER SEQUENCE pessoa_id_seq RESTART WITH 1;