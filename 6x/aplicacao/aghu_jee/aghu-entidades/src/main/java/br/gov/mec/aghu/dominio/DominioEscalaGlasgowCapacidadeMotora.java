package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEscalaGlasgowCapacidadeMotora implements Dominio {

	NENHUMA(1),
	EXTENSAO_DECEREBRACAO(2),
	FLEXAO_DECORTICACAO(3),
	FLEXAO_RETIRADA(4),
	LOCALIZA_DOR(5),
	OBEDECE_COMANDOS(6)
	;
	
	private int value;
	
	private DominioEscalaGlasgowCapacidadeMotora(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NENHUMA:
			return "Nenhuma";
		case EXTENSAO_DECEREBRACAO:
			return "Extensão (decerebração)";
		case FLEXAO_DECORTICACAO:
			return "Flexão (decorticação)";
		case FLEXAO_RETIRADA:
			return "Flexão (retirada)";
		case LOCALIZA_DOR:
			return "Localiza dor";
		case OBEDECE_COMANDOS:
			return "Obedece comandos";
		default:
			return "";
		}
	}

}