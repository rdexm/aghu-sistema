package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndOperacaoBasica implements Dominio {

	DB, //Debito
	CR, //Credito
	NN,
	S,
	N;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DB:
			return "DB";
		case CR:
			return "CR";
		case NN:
			return "NN";
		case S:
			return "Sim";
		case N:
			return "Não";
		default:
			return "";
		}
	}
	
	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * 
	 * @return
	 */
	public boolean isSim() {
		switch (this) {
		case S:
			return Boolean.TRUE;
		case N:
			return Boolean.FALSE;
		default:
			return Boolean.FALSE;
		}
	}

}
