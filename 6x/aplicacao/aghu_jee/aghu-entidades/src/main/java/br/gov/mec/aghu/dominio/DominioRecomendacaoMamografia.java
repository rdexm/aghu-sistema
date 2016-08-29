package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioRecomendacaoMamografia implements Dominio {
	
	/*1 – Complementação com Ultrasonografia
	2 – Mamografia em 1 ano
	3 – Controle Radiológico 6 meses 
	4 – Controle Radiológico 1 ano
	5 – Histopatológico
	6 – Terapêutica Específica*/
	
	COMPLEMENTACAO_COM_ULTRASSONOGRAFIA(1),	
	MAMOGRAFIA_1_ANO(2),
	CONTROLE_RADIOLOGICO_6_MESES(3),
	CONTROLE_RADIOLOGICO_1_ANO(4),
	HISTOPATOLOGICO(5),
	TERAPEUTICA_ESPECIFICA(6);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioRecomendacaoMamografia(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case COMPLEMENTACAO_COM_ULTRASSONOGRAFIA:
				return "Complementação com Ultrasonografia";
			case MAMOGRAFIA_1_ANO:
				return "Mamografia em 1 ano";	
			case CONTROLE_RADIOLOGICO_6_MESES:
				return "Controle Radiológico 6 meses";
			case CONTROLE_RADIOLOGICO_1_ANO:
				return "Controle Radiológico 1 ano";				
			case HISTOPATOLOGICO:
				return "Histopatológico";	 
			case TERAPEUTICA_ESPECIFICA:
				return "Terapêutica Específica";
			default:
				return "";
		}
	}
	
	public static DominioRecomendacaoMamografia getDominioPorCodigo(int codigo) {
		for (DominioRecomendacaoMamografia dominio : DominioRecomendacaoMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return null;		
	}
}
