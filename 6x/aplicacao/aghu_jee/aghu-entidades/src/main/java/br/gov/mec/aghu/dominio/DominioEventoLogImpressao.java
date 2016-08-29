package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioEventoLogImpressao implements DominioString {

	ATESTADO("ATESTADO"),
	DESBLOQUEIO_CONSULTA("DESBLOQUEIO CONSULTA"),
	DESBLOQUEIO_EX_FISICO_RN("DESBLOQUEIO EX FISICO RN"),
	DESBLOQUEIO_NAS("DESBLOQUEIO NAS"),
	DESBLOQUEIO_PRIM("DESBLOQUEIO PRIM"),
	DESBLOQUEIO_RECEM_NASCIDO("DESBLOQUEIO RECEM NASCIDO"),
	LAUDO_AIH("LAUDO_AIH"),
	MCOR_ADMISSAO_OBS("MCOR_ADMISSAO_OBS"),
	MCOR_CONSULTA_OBS("MCOR_CONSULTA_OBS"),
	MCOR_EX_FISICO_RN("MCOR_EX_FISICO_RN"),
	MCOR_NASCIMENTO("MCOR_NASCIMENTO"),
	MCOR_RN_SL_PARTO("MCOR_RN_SL_PARTO"),
	RECEITA("RECEITA"); 

	private String value; 
	
	private DominioEventoLogImpressao(String value) {
		this.value = value; 
	}

	@Override
	public String getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ATESTADO: 
			return "";
		case DESBLOQUEIO_CONSULTA: 
			return "";
		case DESBLOQUEIO_EX_FISICO_RN: 
			return "";
		case DESBLOQUEIO_NAS: 
			return "";
		case DESBLOQUEIO_PRIM: 
			return "";
		case DESBLOQUEIO_RECEM_NASCIDO: 
			return "";
		case LAUDO_AIH: 
			return "";
		case MCOR_ADMISSAO_OBS: 
			return "MCOR_ADMISSAO_OBS";
		case MCOR_CONSULTA_OBS: 
			return "MCOR_CONSULTA_OBS";
		case MCOR_EX_FISICO_RN: 
			return "MCOR_EX_FISICO_RN";
		case MCOR_NASCIMENTO: 
			return "MCOR_NASCIMENTO";
		case MCOR_RN_SL_PARTO: 
			return "";
		case RECEITA: 
			return ""; 
		default: 
			return ""; 
		}
	}
	
}
