package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSismamaSimNaoNaoSabe implements Dominio {
	SIM("Sim", 3), NAO("Não", 1), NAO_SABE("Não Sabe", 2);
	private String descricao;
	private Integer codigo;

	DominioSismamaSimNaoNaoSabe(final String descricao, final Integer codigo){
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
	
	public static DominioSismamaSimNaoNaoSabe getInstance(Integer value) {
		switch (value) {
		case 3:
			return SIM;
		case 1:
			return NAO;
		case 2:
			return NAO_SABE;
		default:
			return null;
		}
	}
	
}
