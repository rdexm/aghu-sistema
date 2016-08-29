package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



/**
 * Na tabela ECP_GRUPO_CONTROLES, O campo tipo podera ter os valores : M (Monitorizacao) ou BH (Balanco Hidrico).
 * 
 * @author sgralha
 * 
 */
public enum DominioTipoGrupoControle implements Dominio {
	/**
	 * Monitorizacao
	 */
	MN,

	/**
	 * Controle Hidrico - Administrado
	 */
	CA,
	/**
	 * Controle Hidrico - Eliminado
	 */
	CE;

	

	

	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MN:
			return "Monitorização";
		case CA:
			return "Controle Hídrico Administrado";
		case CE:
			return "Controle Hídrico Eliminado";
		default:
			return "";
		}
	}

	public static DominioTipoGrupoControle getInstance(String valor) {
		if ("MN".equalsIgnoreCase(valor)) {
			return DominioTipoGrupoControle.MN;
		} else if ("CE".equalsIgnoreCase(valor)) {
			return DominioTipoGrupoControle.CE;
		} else if("CA".equalsIgnoreCase(valor)){
			return DominioTipoGrupoControle.CA;
		}else{
			return null;
		}
	}
	
	
}
