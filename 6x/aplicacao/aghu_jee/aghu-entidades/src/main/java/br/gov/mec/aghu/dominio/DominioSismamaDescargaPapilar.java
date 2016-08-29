package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSismamaDescargaPapilar  implements Dominio {
	CRISTALINA("Cristalina", 1), HEMORRAGICA("Hemorrágica", 2);

	private String descricao;
	private Integer codigo;

	DominioSismamaDescargaPapilar(final String descricao, final Integer codigo){
		this.descricao = descricao;
		this.codigo = codigo;
	}
	
	@Override
	public int getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Override
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
