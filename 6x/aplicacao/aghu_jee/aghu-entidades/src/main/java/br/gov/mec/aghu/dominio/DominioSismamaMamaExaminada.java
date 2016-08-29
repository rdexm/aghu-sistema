package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSismamaMamaExaminada  implements Dominio {
	DIREITA("Direita", 1), ESQUERDA("Esquerda", 2), AMBAS("Ambas", 3);

	private String descricao;
	private Integer codigo;

	DominioSismamaMamaExaminada(final String descricao, final Integer codigo){
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
