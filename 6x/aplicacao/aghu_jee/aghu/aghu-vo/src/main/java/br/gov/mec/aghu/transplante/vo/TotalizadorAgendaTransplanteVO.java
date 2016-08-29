package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

public class TotalizadorAgendaTransplanteVO implements Serializable {

	private static final long serialVersionUID = -3277816503208407192L;
	
	private Date data;
	private String tipoTotal;
	private Integer quantidade;
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getTipoTotal() {
		return tipoTotal;
	}
	public void setTipoTotal(String tipoTotal) {
		this.tipoTotal = tipoTotal;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	public TotalizadorAgendaTransplanteVO (Date data, String tipoTotal, Integer quantidade){
		this.data = data;
		this.tipoTotal = tipoTotal;
		this.quantidade = quantidade;
	}
}
