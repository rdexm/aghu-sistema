package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;

public class ItensProtocoloVO implements Serializable {

	private static final long serialVersionUID = 1003025592542374568L;


	private String solicitacao;
	private String exame;

	
	public String getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}
	public String getExame() {
		return exame;
	}
	public void setExame(String exame) {
		this.exame = exame;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exame == null) ? 0 : exame.hashCode());
		result = prime * result
				+ ((solicitacao == null) ? 0 : solicitacao.hashCode());
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
		ItensProtocoloVO other = (ItensProtocoloVO) obj;
		if (exame == null) {
			if (other.exame != null) {
				return false;
			}
		} else {
			if (!exame.equals(other.exame)) {
				return false;
			}
		}
		if (solicitacao == null) {
			if (other.solicitacao != null) {
				return false;
			}
		} else {
			if (!solicitacao.equals(other.solicitacao)) {
				return false;
			}
		}
		return true;
	}
	
	
	

}
