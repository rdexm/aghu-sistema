package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoParecer implements Dominio{
	/**
	 * AN - Parecer desfavorável (PD) OU sem parecer (SP) OU Em Avaliação (EA)
	 * EA - EM Avaliação
	 * PF - PARECER FAVORAVEL
	 * PD - PARECER DESFAVORAVEL
	 * SP - Sem PARECER
	 * TD - Todos	
	 */
	AN,
	EA,
	PF,
	PD,
	SP,
	TD;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AN:
			return "Analisar";
		case EA:
			return "Em avaliação";			
		case PF:
			return "Favorárel";		
		case PD:
			return "Desfavorárel";
		case SP:
			return "Sem Parecer";	
		case TD:
			return "Todos";	
				
		default:
			return "";
		}
	}

}
