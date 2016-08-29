package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio a característica associada ao microcomputador
 * 
 * @author gzapalaglio
 * 
 */
public enum DominioCaracteristicaMicrocomputador implements DominioString {
	/**
	 * Refresh Automático Exames
	 */
	REFRESH_AUTOMATICO_EXAMES("Refresh Autom AEL"),

	/**
	 * Permite Registro Atendimento Ambulatorial
	 */
	PERMITE_REGISTRO_ATENDIMENTO_AMBULATORIAL("Permite Reg Atend Amb"),
	
	/**
	 * Permite Dispensação Medicamentos Farmácia
	 */
	DISPENSADOR("Permite Disp Med Farm"),
	/**
	 * Indica que computador possui impressora Unitarizadora instalada localmente
	 */
	POSSUI_IMPRESSORA_UNITARIZADORA("Possui Imp. Unitarizadora"),
	/**
	 * Computador utilizado em ambiente UBS
	 */
	PERFIL_UBS("Micro com perfil UBS")
	;


	private String value;
	
	private DominioCaracteristicaMicrocomputador(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}	

	@Override
	public String getDescricao() {
		switch (this) {
		case REFRESH_AUTOMATICO_EXAMES:
			return "Permite Refresh Automático Exames";
		case PERMITE_REGISTRO_ATENDIMENTO_AMBULATORIAL:
			return "Permite Registro Atendimento Ambulatorial";
		case DISPENSADOR:
			return "Permite Dispensação Medicamentos Farmácia";
		case POSSUI_IMPRESSORA_UNITARIZADORA:
			return "Possui Impressora Unitarizadora";
		case PERFIL_UBS:
			return "Micro com perfil de UBS";
		default:
			return "";
		}
	}

}
