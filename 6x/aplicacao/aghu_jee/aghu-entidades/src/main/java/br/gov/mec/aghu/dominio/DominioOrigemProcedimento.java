package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * @author gandriotti
 *
 */
public enum DominioOrigemProcedimento implements Dominio {

	I("Internacao"),
	A("Ambulatorio"),
	;
	
	private final String descricao; 
	
	private DominioOrigemProcedimento(String descricao) {

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
