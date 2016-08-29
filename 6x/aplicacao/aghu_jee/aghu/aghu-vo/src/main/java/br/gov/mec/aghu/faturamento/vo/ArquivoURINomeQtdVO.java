package br.gov.mec.aghu.faturamento.vo;

import java.net.URI;

public class ArquivoURINomeQtdVO {

	private final URI uri;
	private final String nome;
	private final int qtdRegistros;
	private final int qtdArquivos;
	
	public ArquivoURINomeQtdVO(
			final URI uri,
			final String nome,
			final int qtdRegistros,
			final int qtdArquivos) {

		super();

		//algo
		this.uri = uri;
		this.nome = nome;
		this.qtdRegistros = qtdRegistros;
		this.qtdArquivos = qtdArquivos;
	}

	public URI getUri() {

		return this.uri;
	}

	public String getNome() {

		return this.nome;
	}

	public int getQtdRegistros() {

		return this.qtdRegistros;
	}

	public int getQtdArquivos() {

		return this.qtdArquivos;
	}
}
