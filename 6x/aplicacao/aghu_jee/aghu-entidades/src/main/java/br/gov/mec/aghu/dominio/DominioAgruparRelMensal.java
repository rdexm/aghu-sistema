package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAgruparRelMensal implements Dominio {

	GRUPO_MATERIAL,

	CODIGO_NATUREZA_SIAFI,

	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case GRUPO_MATERIAL:
			return "Grupo Material";
		case CODIGO_NATUREZA_SIAFI:
			return "Código de Natureza SIAFI";
		default:
			return this.toString();	
		}
	}
	
}
