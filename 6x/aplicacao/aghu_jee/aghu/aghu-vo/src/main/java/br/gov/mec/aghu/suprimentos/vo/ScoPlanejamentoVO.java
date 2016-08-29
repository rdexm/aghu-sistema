package br.gov.mec.aghu.suprimentos.vo;

import java.util.Date;

public class ScoPlanejamentoVO {

	Integer numeroSolicitacaoCompra;
	Date dataAnaliseCompra;
	Long qtdeAprovada;
	Long qtdeReforco;
	Boolean solicitacaoAssinada;
	
	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}
	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}
	public Date getDataAnaliseCompra() {
		return dataAnaliseCompra;
	}
	public void setDataAnaliseCompra(Date dataAnaliseCompra) {
		this.dataAnaliseCompra = dataAnaliseCompra;
	}
	public Long getQtdeAprovada() {
		return qtdeAprovada;
	}
	public void setQtdeAprovada(Long qtdeAprovada) {
		this.qtdeAprovada = qtdeAprovada;
	}
	public Long getQtdeReforco() {
		return qtdeReforco;
	}
	public void setQtdeReforco(Long qtdeReforco) {
		this.qtdeReforco = qtdeReforco;
	}
	public Boolean getSolicitacaoAssinada() {
		return solicitacaoAssinada;
	}
	public void setSolicitacaoAssinada(Boolean solicitacaoAssinada) {
		this.solicitacaoAssinada = solicitacaoAssinada;
	}
	public ScoPlanejamentoVO(Integer numeroSolicitacaoCompra, Date dataAnaliseCompra, Long qtdeAprovada, Long qtdeReforco, Boolean solicitacaoAssinada) {		
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
		this.dataAnaliseCompra = dataAnaliseCompra;
		this.qtdeAprovada = qtdeAprovada;
		this.qtdeReforco = qtdeReforco;
		this.solicitacaoAssinada = solicitacaoAssinada;
	}
	public ScoPlanejamentoVO() {
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroSolicitacaoCompra == null) ? 0 : numeroSolicitacaoCompra.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoPlanejamentoVO other = (ScoPlanejamentoVO) obj;
		if (numeroSolicitacaoCompra == null) {
			if (other.numeroSolicitacaoCompra != null){
				return false;
			}
		} else if (!numeroSolicitacaoCompra.equals(other.numeroSolicitacaoCompra)){
			return false;
		}
		return true;
	}
}
