package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author gandriotti
 * 
 */
public enum DominioSituacaoItemContaApac implements Dominio {

	A("Aberta"),
	C("Cancelada"),
	E("Encerrada"),
	P("Pendente");
	
	private final String descricao;
	
	
	private DominioSituacaoItemContaApac(String descricao) {

		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		return this.descricao;
	}
	
}
