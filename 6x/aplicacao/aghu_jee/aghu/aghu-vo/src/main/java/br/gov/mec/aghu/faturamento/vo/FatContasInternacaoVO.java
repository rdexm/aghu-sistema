package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatDadosContaSemInt;


public class FatContasInternacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4991878736196232935L;

	private AinInternacao internacao;

	private FatDadosContaSemInt dadosContaSemInt;

	public FatContasInternacaoVO() {
	}

	public FatContasInternacaoVO(AinInternacao internacao, FatDadosContaSemInt dadosContaSemInt) {
		this.internacao = internacao;
		this.dadosContaSemInt = dadosContaSemInt;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public FatDadosContaSemInt getDadosContaSemInt() {
		return dadosContaSemInt;
	}

	public void setDadosContaSemInt(FatDadosContaSemInt dadosContaSemInt) {
		this.dadosContaSemInt = dadosContaSemInt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dadosContaSemInt == null) ? 0 : dadosContaSemInt.hashCode());
		result = prime * result + ((internacao == null) ? 0 : internacao.hashCode());
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
		FatContasInternacaoVO other = (FatContasInternacaoVO) obj;
		if (dadosContaSemInt == null) {
			if (other.dadosContaSemInt != null) {
				return false;
			}
		} else if (!dadosContaSemInt.equals(other.dadosContaSemInt)) {
			return false;
		}
		if (internacao == null) {
			if (other.internacao != null) {
				return false;
			}
		} else if (!internacao.equals(other.internacao)) {
			return false;
		}
		return true;
	}

}
