package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Possíveis valores para o campo parecerRevisaoMedica da entidade MamLaudoAih.
 *
 */
public enum DominioParecerRevisaoMedicaLaudoAih implements Dominio {
	
	A,
	J;
		
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aprovado";
		case J:
			return "Ajustar";
		default:
			return "";
		}
	}

}