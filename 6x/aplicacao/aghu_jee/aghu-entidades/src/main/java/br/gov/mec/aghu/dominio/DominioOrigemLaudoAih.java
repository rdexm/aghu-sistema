package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Possíveis valores para o campo origem da entidade MamLaudoAih.
 *
 */
public enum DominioOrigemLaudoAih implements Dominio {
	
	C,
	O,
	A,
	E;
		
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Centro Obstétrico";
		case O:
			return "Outros";
		case A:
			return "Ambulatório";
		case E:
			return "Emergência";
		default:
			return "";
		}
	}

}