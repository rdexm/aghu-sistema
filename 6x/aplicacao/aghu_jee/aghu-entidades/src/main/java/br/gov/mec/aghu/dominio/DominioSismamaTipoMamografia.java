package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



/**
 * Domínio que indica o tipo de mamografia realizada - SISMAMA.
 * @author rhrosa
 */
public enum DominioSismamaTipoMamografia implements Dominio {
	
	C_MAMO_DIAG,
	C_MAMO_RASTR;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C_MAMO_DIAG:
			return "Mamografia Diagnóstica";
		case C_MAMO_RASTR:
			return "Mamografia de Rastreamento";
		default:
			return "";
		}
	}
}
