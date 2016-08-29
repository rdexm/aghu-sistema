package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProfessorCrmInternacaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1029102514904793398L;
	private Boolean consisteEscala;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short espSeq;
	private Short cnvCodigo;
	private String espSigla;
	private String nome;
	private String nroRegConselho;
	private Integer capacReferencial;
	private Integer quantPacInternados;
	private String atuaCti;
	private String dtInicioEscala;
	
	
	public Boolean getConsisteEscala() {
		return consisteEscala;
	}
	public void setConsisteEscala(Boolean consisteEscala) {
		this.consisteEscala = consisteEscala;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public Short getCnvCodigo() {
		return cnvCodigo;
	}
	public void setCnvCodigo(Short cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}
	public String getEspSigla() {
		return espSigla;
	}
	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNroRegConselho() {
		return nroRegConselho;
	}
	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	public Integer getCapacReferencial() {
		return capacReferencial;
	}
	public void setCapacReferencial(Integer capacReferencial) {
		this.capacReferencial = capacReferencial;
	}
	public Integer getQuantPacInternados() {
		return quantPacInternados;
	}
	public void setQuantPacInternados(Integer quantPacInternados) {
		this.quantPacInternados = quantPacInternados;
	}
	public String getAtuaCti() {
		return atuaCti;
	}
	public void setAtuaCti(String atuaCti) {
		this.atuaCti = atuaCti;
	}
	public String getDtInicioEscala() {
		return dtInicioEscala;
	}
	public void setDtInicioEscala(String dtInicioEscala) {
		this.dtInicioEscala = dtInicioEscala;
	}
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atuaCti == null) ? 0 : atuaCti.hashCode());
		result = prime
				* result
				+ ((capacReferencial == null) ? 0 : capacReferencial.hashCode());
		result = prime * result
				+ ((cnvCodigo == null) ? 0 : cnvCodigo.hashCode());
		result = prime * result
				+ ((consisteEscala == null) ? 0 : consisteEscala.hashCode());
		result = prime * result
				+ ((dtInicioEscala == null) ? 0 : dtInicioEscala.hashCode());
		result = prime * result + ((espSeq == null) ? 0 : espSeq.hashCode());
		result = prime * result
				+ ((espSigla == null) ? 0 : espSigla.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result
				+ ((nroRegConselho == null) ? 0 : nroRegConselho.hashCode());
		result = prime
				* result
				+ ((quantPacInternados == null) ? 0 : quantPacInternados
						.hashCode());
		result = prime * result
				+ ((serMatricula == null) ? 0 : serMatricula.hashCode());
		result = prime * result
				+ ((serVinCodigo == null) ? 0 : serVinCodigo.hashCode());
		return result;
	}
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
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
		ProfessorCrmInternacaoVO other = (ProfessorCrmInternacaoVO) obj;
		if (atuaCti == null) {
			if (other.atuaCti != null) {
				return false;
			}
		} else if (!atuaCti.equals(other.atuaCti)) {
			return false;
		}
		if (capacReferencial == null) {
			if (other.capacReferencial != null) {
				return false;
			}
		} else if (!capacReferencial.equals(other.capacReferencial)) {
			return false;
		}
		if (cnvCodigo == null) {
			if (other.cnvCodigo != null) {
				return false;
			}
		} else if (!cnvCodigo.equals(other.cnvCodigo)) {
			return false;
		}
		if (consisteEscala == null) {
			if (other.consisteEscala != null) {
				return false;
			}
		} else if (!consisteEscala.equals(other.consisteEscala)) {
			return false;
		}
		if (dtInicioEscala == null) {
			if (other.dtInicioEscala != null) {
				return false;
			}
		} else if (!dtInicioEscala.equals(other.dtInicioEscala)) {
			return false;
		}
		if (espSeq == null) {
			if (other.espSeq != null) {
				return false;
			}
		} else if (!espSeq.equals(other.espSeq)) {
			return false;
		}
		if (espSigla == null) {
			if (other.espSigla != null) {
				return false;
			}
		} else if (!espSigla.equals(other.espSigla)) {
			return false;
		}
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		if (nroRegConselho == null) {
			if (other.nroRegConselho != null) {
				return false;
			}
		} else if (!nroRegConselho.equals(other.nroRegConselho)) {
			return false;
		}
		if (quantPacInternados == null) {
			if (other.quantPacInternados != null) {
				return false;
			}
		} else if (!quantPacInternados.equals(other.quantPacInternados)) {
			return false;
		}
		if (serMatricula == null) {
			if (other.serMatricula != null) {
				return false;
			}
		} else if (!serMatricula.equals(other.serMatricula)) {
			return false;
		}
		if (serVinCodigo == null) {
			if (other.serVinCodigo != null) {
				return false;
			}
		} else if (!serVinCodigo.equals(other.serVinCodigo)) {
			return false;
		}
		return true;
	}
	
	
	

}
