package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSismamaMama implements Dominio {
	SIM("Sim", 3), NUNCA_FORAM_EXAMINADAS("Nunca foram examinadas", 1), NAO_SABE("Não sabe", 2);

	private String descricao;
	private Integer codigo;

	DominioSismamaMama(final String descricao, final Integer codigo) {
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
