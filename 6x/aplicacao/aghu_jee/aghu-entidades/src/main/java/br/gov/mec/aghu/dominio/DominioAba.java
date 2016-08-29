package br.gov.mec.aghu.dominio;

public enum DominioAba {
	LAU("LAU", "Laudo"),
	CAD("CAD", "Cadastro"),
	IND("IND", "Índice de Blocos"),
	IMG("IMG", "Imagem"),
	NOT("NOT", "Notas Adicionais"),
	CON("CON", "Liberar Laudo"),
	QUA("QUA", "Controle de Qualidade");
	
	private String valor;
	private String descricao;

	private DominioAba(String valor, String descricao) {
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
