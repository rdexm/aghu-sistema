package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

public class MedicamentoVO implements Serializable {

	private static final long serialVersionUID = -6044547313824452028L;
	
	private Integer matCodigo;
	private String descricao;
	private Date dataHoraInicial;
	private Date dataHoraFinal;
	private String observacao;
	 
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	 
	public Date getDataHoraInicial() {
		return dataHoraInicial;
	}
	public void setDataHoraInicial(Date dataHoraInicial) {
		this.dataHoraInicial = dataHoraInicial;
	}
	public Date getDataHoraFinal() {
		return dataHoraFinal;
	}
	public void setDataHoraFinal(Date dataHoraFinal) {
		this.dataHoraFinal = dataHoraFinal;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}
