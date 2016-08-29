package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEventoNotaAdicional implements Dominio {

	MCOR_RN_SL_PARTO,
	MCOR_ADMISSAO_OBS,
	MCOR_CONSULTA_OBS,
	MCOR_EX_FISICO_RN,
	MCOR_NASCIMENTO;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MCOR_RN_SL_PARTO:
			return MCOR_RN_SL_PARTO.toString();
		case MCOR_ADMISSAO_OBS:
			return MCOR_ADMISSAO_OBS.toString();
		case MCOR_CONSULTA_OBS:
			return MCOR_CONSULTA_OBS.toString();
		case MCOR_EX_FISICO_RN:
			return MCOR_EX_FISICO_RN.toString();
		case MCOR_NASCIMENTO:
			return MCOR_NASCIMENTO.toString();
		default:
			return "";
		}
	}
}
