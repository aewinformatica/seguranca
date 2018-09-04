CREATE TABLE IF NOT EXISTS usuario (
  codigo BIGINT(20) PRIMARY KEY  NOT NULL AUTO_INCREMENT,
  ativo BIT(1) NOT NULL,
  data_nascimento TINYBLOB NULL DEFAULT NULL,
  email VARCHAR(255) NOT NULL,
  nome VARCHAR(255) NOT NULL,
  senha VARCHAR(255) NULL DEFAULT NULL
)ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS permissao (
  codigo BIGINT(20) NOT NULL AUTO_INCREMENT,
  nome VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (codigo))
ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS grupo (
  codigo BIGINT(20) NOT NULL AUTO_INCREMENT,
  nome VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (codigo))
ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS grupo_permissao (
  codigo_grupo BIGINT(20) NOT NULL,
  codigo_permissao BIGINT(20) NOT NULL,
   PRIMARY KEY (codigo_grupo, codigo_permissao),
    FOREIGN KEY (codigo_permissao) REFERENCES permissao (codigo),
    FOREIGN KEY (codigo_grupo) REFERENCES grupo (codigo))
ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS usuario_grupo (
  codigo_usuario BIGINT(20) NOT NULL,
  codigo_grupo BIGINT(20)  NOT NULL,
  PRIMARY KEY (codigo_usuario, codigo_grupo),
    FOREIGN KEY (codigo_grupo) REFERENCES grupo (codigo),
    FOREIGN KEY (codigo_usuario) REFERENCES usuario (codigo))
ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;