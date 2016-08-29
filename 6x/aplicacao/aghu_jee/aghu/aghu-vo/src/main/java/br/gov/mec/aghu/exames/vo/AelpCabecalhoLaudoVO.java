package br.gov.mec.aghu.exames.vo;

import java.util.Date;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class AelpCabecalhoLaudoVO {

	private Boolean pIndImprimeNovo;
	private String pDescServico;
	private String pRegistro;
	private String pChefeServico;
	private String pConselho;
	private String pNroConselho;
	private String pDescUnidade;
	private String pChefeUnidade;
	private String pConselhoUnid;
	private String pNroConselhoUnid;
	private String leito;
	
	private String pLinhaCab1;
	private String pLinhaCab2;
	private String pLinhaCab3;
	private String pLinhaCab4;
	private String pLinhaCab5;
	private String pLinhaCab6;
	private String pLinhaCab7;
	
	/**
	 * Utilizado para controle da exibição do selo de acreditação do laboratório na geração do pdf de impressão de resultado.
	 * */
	private boolean indMostrarSeloAcreditacao = false;
	
	private boolean indLaudoPaciente = false;
	
	private String medicoSolicitante;
	
	private String convenioPaciente;
	
	private Date dataLiberacao;
	

	public Boolean getpIndImprimeNovo() {
		return pIndImprimeNovo;
	}

	public String getpDescServico() {
		return pDescServico;
	}

	public String getpRegistro() {
		return pRegistro;
	}

	public String getpChefeServico() {
		return pChefeServico;
	}

	public String getpConselho() {
		return pConselho;
	}

	public String getpNroConselho() {
		return pNroConselho;
	}

	public String getpDescUnidade() {
		return pDescUnidade;
	}

	public String getpChefeUnidade() {
		return pChefeUnidade;
	}

	public String getpConselhoUnid() {
		return pConselhoUnid;
	}

	public String getpNroConselhoUnid() {
		return pNroConselhoUnid;
	}

	public String getpLinhaCab1() {
		return pLinhaCab1;
	}

	public String getpLinhaCab2() {
		return pLinhaCab2;
	}

	public String getpLinhaCab3() {
		return pLinhaCab3;
	}

	public String getpLinhaCab4() {
		return pLinhaCab4;
	}

	public String getpLinhaCab5() {
		return pLinhaCab5;
	}

	public String getpLinhaCab6() {
		return pLinhaCab6;
	}

	public String getpLinhaCab7() {
		return pLinhaCab7;
	}

	public void setpIndImprimeNovo(Boolean pIndImprimeNovo) {
		this.pIndImprimeNovo = pIndImprimeNovo;
	}

	public void setpDescServico(String pDescServico) {
		this.pDescServico = pDescServico;
	}

	public void setpRegistro(String pRegistro) {
		this.pRegistro = pRegistro;
	}

	public void setpChefeServico(String pChefeServico) {
		this.pChefeServico = pChefeServico;
	}

	public void setpConselho(String pConselho) {
		this.pConselho = pConselho;
	}

	public void setpNroConselho(String pNroConselho) {
		this.pNroConselho = pNroConselho;
	}

	public void setpDescUnidade(String pDescUnidade) {
		this.pDescUnidade = pDescUnidade;
	}

	public void setpChefeUnidade(String pChefeUnidade) {
		this.pChefeUnidade = pChefeUnidade;
	}

	public void setpConselhoUnid(String pConselhoUnid) {
		this.pConselhoUnid = pConselhoUnid;
	}

	public void setpNroConselhoUnid(String pNroConselhoUnid) {
		this.pNroConselhoUnid = pNroConselhoUnid;
	}

	public void setpLinhaCab1(String pLinhaCab1) {
		this.pLinhaCab1 = pLinhaCab1;
	}

	public void setpLinhaCab2(String pLinhaCab2) {
		this.pLinhaCab2 = pLinhaCab2;
	}

	public void setpLinhaCab3(String pLinhaCab3) {
		this.pLinhaCab3 = pLinhaCab3;
	}

	public void setpLinhaCab4(String pLinhaCab4) {
		this.pLinhaCab4 = pLinhaCab4;
	}

	public void setpLinhaCab5(String pLinhaCab5) {
		this.pLinhaCab5 = pLinhaCab5;
	}

	public void setpLinhaCab6(String pLinhaCab6) {
		this.pLinhaCab6 = pLinhaCab6;
	}

	public void setpLinhaCab7(String pLinhaCab7) {
		this.pLinhaCab7 = pLinhaCab7;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pChefeServico == null) ? 0 : pChefeServico.hashCode());
		result = prime * result
				+ ((pChefeUnidade == null) ? 0 : pChefeUnidade.hashCode());
		result = prime * result
				+ ((pConselho == null) ? 0 : pConselho.hashCode());
		result = prime * result
				+ ((pConselhoUnid == null) ? 0 : pConselhoUnid.hashCode());
		result = prime * result
				+ ((pDescServico == null) ? 0 : pDescServico.hashCode());
		result = prime * result
				+ ((pDescUnidade == null) ? 0 : pDescUnidade.hashCode());
		result = prime * result
				+ ((pIndImprimeNovo == null) ? 0 : pIndImprimeNovo.hashCode());
		result = prime * result
				+ ((pLinhaCab1 == null) ? 0 : pLinhaCab1.hashCode());
		result = prime * result
				+ ((pLinhaCab2 == null) ? 0 : pLinhaCab2.hashCode());
		result = prime * result
				+ ((pLinhaCab3 == null) ? 0 : pLinhaCab3.hashCode());
		result = prime * result
				+ ((pLinhaCab4 == null) ? 0 : pLinhaCab4.hashCode());
		result = prime * result
				+ ((pLinhaCab5 == null) ? 0 : pLinhaCab5.hashCode());
		result = prime * result
				+ ((pLinhaCab6 == null) ? 0 : pLinhaCab6.hashCode());
		result = prime * result
				+ ((pLinhaCab7 == null) ? 0 : pLinhaCab7.hashCode());
		result = prime * result
				+ ((pNroConselho == null) ? 0 : pNroConselho.hashCode());
		result = prime
				* result
				+ ((pNroConselhoUnid == null) ? 0 : pNroConselhoUnid.hashCode());
		result = prime * result
				+ ((pRegistro == null) ? 0 : pRegistro.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AelpCabecalhoLaudoVO)) {
			return false;
		}
		AelpCabecalhoLaudoVO other = (AelpCabecalhoLaudoVO) obj;
		if (pChefeServico == null) {
			if (other.pChefeServico != null) {
				return false;
			}
		} else if (!pChefeServico.equals(other.pChefeServico)) {
			return false;
		}
		if (pChefeUnidade == null) {
			if (other.pChefeUnidade != null) {
				return false;
			}
		} else if (!pChefeUnidade.equals(other.pChefeUnidade)) {
			return false;
		}
		if (pConselho == null) {
			if (other.pConselho != null) {
				return false;
			}
		} else if (!pConselho.equals(other.pConselho)) {
			return false;
		}
		if (pConselhoUnid == null) {
			if (other.pConselhoUnid != null) {
				return false;
			}
		} else if (!pConselhoUnid.equals(other.pConselhoUnid)) {
			return false;
		}
		if (pDescServico == null) {
			if (other.pDescServico != null) {
				return false;
			}
		} else if (!pDescServico.equals(other.pDescServico)) {
			return false;
		}
		if (pDescUnidade == null) {
			if (other.pDescUnidade != null) {
				return false;
			}
		} else if (!pDescUnidade.equals(other.pDescUnidade)) {
			return false;
		}
		if (pIndImprimeNovo == null) {
			if (other.pIndImprimeNovo != null) {
				return false;
			}
		} else if (!pIndImprimeNovo.equals(other.pIndImprimeNovo)) {
			return false;
		}
		if (pLinhaCab1 == null) {
			if (other.pLinhaCab1 != null) {
				return false;
			}
		} else if (!pLinhaCab1.equals(other.pLinhaCab1)) {
			return false;
		}
		if (pLinhaCab2 == null) {
			if (other.pLinhaCab2 != null) {
				return false;
			}
		} else if (!pLinhaCab2.equals(other.pLinhaCab2)) {
			return false;
		}
		if (pLinhaCab3 == null) {
			if (other.pLinhaCab3 != null) {
				return false;
			}
		} else if (!pLinhaCab3.equals(other.pLinhaCab3)) {
			return false;
		}
		if (pLinhaCab4 == null) {
			if (other.pLinhaCab4 != null) {
				return false;
			}
		} else if (!pLinhaCab4.equals(other.pLinhaCab4)) {
			return false;
		}
		if (pLinhaCab5 == null) {
			if (other.pLinhaCab5 != null) {
				return false;
			}
		} else if (!pLinhaCab5.equals(other.pLinhaCab5)) {
			return false;
		}
		if (pLinhaCab6 == null) {
			if (other.pLinhaCab6 != null) {
				return false;
			}
		} else if (!pLinhaCab6.equals(other.pLinhaCab6)) {
			return false;
		}
		if (pLinhaCab7 == null) {
			if (other.pLinhaCab7 != null) {
				return false;
			}
		} else if (!pLinhaCab7.equals(other.pLinhaCab7)) {
			return false;
		}
		if (pNroConselho == null) {
			if (other.pNroConselho != null) {
				return false;
			}
		} else if (!pNroConselho.equals(other.pNroConselho)) {
			return false;
		}
		if (pNroConselhoUnid == null) {
			if (other.pNroConselhoUnid != null) {
				return false;
			}
		} else if (!pNroConselhoUnid.equals(other.pNroConselhoUnid)) {
			return false;
		}
		if (pRegistro == null) {
			if (other.pRegistro != null) {
				return false;
			}
		} else if (!pRegistro.equals(other.pRegistro)) {
			return false;
		}
		return true;
	}
	
	public boolean isIndMostrarSeloAcreditacao() {
		return indMostrarSeloAcreditacao;
	}

	public void setIndMostrarSeloAcreditacao(boolean indMostrarSeloAcreditacao) {
		this.indMostrarSeloAcreditacao = indMostrarSeloAcreditacao;
	}

	public boolean isIndLaudoPaciente() {
		return indLaudoPaciente;
	}

	public void setIndLaudoPaciente(boolean indLaudoPaciente) {
		this.indLaudoPaciente = indLaudoPaciente;
	}

	public String getMedicoSolicitante() {
		return medicoSolicitante;
	}

	public void setMedicoSolicitante(String medicoSolicitante) {
		this.medicoSolicitante = medicoSolicitante;
	}

	public String getConvenioPaciente() {
		return convenioPaciente;
	}

	public void setConvenioPaciente(String convenioPaciente) {
		this.convenioPaciente = convenioPaciente;
	}
	
	public Date getDataLiberacao() {
		return dataLiberacao;
	}

	public void setDataLiberacao(Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

}