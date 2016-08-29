package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;

public class FiltroAFPVO implements Serializable {
	private static final long serialVersionUID = 153076823409796729L;

	private Integer numeroAf;
	private Integer parcela;
	private Integer qtde;
	private Integer qtdeSolicitada;
	private Date dtPrevEntrega;
	private Date dtGeracao;
	private DominioSituacaoAutorizacaoFornecedor indSituacao;
	private Integer codigoMaterial;

	public FiltroAFPVO() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroAf == null) ? 0 : numeroAf.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FiltroAFPVO other = (FiltroAFPVO) obj;
		if (numeroAf == null) {
			if (other.getNumeroAf() != null) {
				return false;
			}
		} else if (!numeroAf.equals(other.getNumeroAf())) {
			return false;
		}
		return true;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public Integer getParcela() {
		return parcela;
	}

	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public DominioSituacaoAutorizacaoFornecedor getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAutorizacaoFornecedor indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
}
