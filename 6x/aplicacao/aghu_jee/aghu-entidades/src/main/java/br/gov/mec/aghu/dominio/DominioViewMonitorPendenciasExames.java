package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioViewMonitorPendenciasExames implements Dominio {
	AREA_EXECUTORA("tab1", 0), // View V_AEL_MONITOR_AE
	EXECUTANDO("tab2", 1), // View V_AEL_MONITOR_EX
	COLETADO("tab3", 2), // View V_AEL_MONITOR_CO
	EM_COLETA("tab4", 3); // View V_AEL_MONITOR_EC

	private final String value;
	private final int codigo;

	private DominioViewMonitorPendenciasExames(String value, int codigo) {
		this.value = value;
		this.codigo = codigo;
	}

	public static DominioViewMonitorPendenciasExames fromValue(String valor) {
		DominioViewMonitorPendenciasExames retorno = null;
		for (DominioViewMonitorPendenciasExames dominio : DominioViewMonitorPendenciasExames.values()) {
			if (dominio.getValue().equalsIgnoreCase(valor)) {
				retorno = dominio;
				break;
			}
		}
		return retorno;
	}
	
	public static DominioViewMonitorPendenciasExames fromValue(Integer codigo) {
		DominioViewMonitorPendenciasExames retorno = null;
		for (DominioViewMonitorPendenciasExames dominio : DominioViewMonitorPendenciasExames.values()) {
			if (dominio.getCodigo() == codigo) {
				retorno = dominio;
				break;
			}
		}
		return retorno;
	}

	@Override
	public int getCodigo() {
		return this.codigo;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AREA_EXECUTORA:
			return "Área Executora";
		case EXECUTANDO:
			return "Executando";
		case COLETADO:
			return "Coletado";
		case EM_COLETA:
			return "Em coleta";

		default:
			return "";
		}
	}

}
