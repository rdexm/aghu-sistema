package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.model.RapServidores;

public class TipoPrescricaoFaturamentoVO {

	String tipo;
	Integer matCodigo;
	Integer pciSeq;
	Short pedSeq;
	Integer pnpSeq;
	Long pprSeq;
	Date dataInicio;
	Date dataFim;
	Integer pacCodigo;
	Integer atdSeq;
	Integer prescricaoSeq;
	RapServidores servidorValida;
	
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public Integer getPciSeq() {
		return pciSeq;
	}
	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Integer getPrescricaoSeq() {
		return prescricaoSeq;
	}
	public void setPrescricaoSeq(Integer prescricaoSeq) {
		this.prescricaoSeq = prescricaoSeq;
	}
	public RapServidores getServidorValida() {
		return servidorValida;
	}
	public void setServidorValida(RapServidores servidorValida) {
		this.servidorValida = servidorValida;
	}
	public Short getPedSeq() {
		return pedSeq;
	}
	public void setPedSeq(Short pedSeq) {
		this.pedSeq = pedSeq;
	}
	public Integer getPnpSeq() {
		return pnpSeq;
	}
	public void setPnpSeq(Integer pnpSeq) {
		this.pnpSeq = pnpSeq;
	}
	public Long getPprSeq() {
		return pprSeq;
	}
	public void setPprSeq(Long pprSeq) {
		this.pprSeq = pprSeq;
	}
	
	
}
