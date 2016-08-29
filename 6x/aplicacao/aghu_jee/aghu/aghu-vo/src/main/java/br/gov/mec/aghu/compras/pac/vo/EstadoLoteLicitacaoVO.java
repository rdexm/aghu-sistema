package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class EstadoLoteLicitacaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String loteTipo4;
	private String inicioEstadoPregao;
	private String fimEstadoPregao;
	private String estadoLoteDoPregao;
	
	
	public String getLoteTipo4() {
		return loteTipo4;
	}
	public void setLoteTipo4(String loteTipo4) {
		this.loteTipo4 = loteTipo4;
	}
	public String getInicioEstadoPregao() {
		return inicioEstadoPregao;
	}
	public void setInicioEstadoPregao(String inicioEstadoPregao) {
		this.inicioEstadoPregao = inicioEstadoPregao;
	}
	public String getFimEstadoPregao() {
		return fimEstadoPregao;
	}
	public void setFimEstadoPregao(String fimEstadoPregao) {
		this.fimEstadoPregao = fimEstadoPregao;
	}
	public String getEstadoLoteDoPregao() {
		return estadoLoteDoPregao;
	}
	public void setEstadoLoteDoPregao(String estadoLoteDoPregao) {
		this.estadoLoteDoPregao = estadoLoteDoPregao;
	}	
	
}
