package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;

public class EspCrmVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6958641836279057053L;
	private String nroRegConselho;
	private Short espSeq;
	private Integer matricula;
	private Short vinCodigo;
	private Integer codigo;
	private String nomeMedico;
	private String nomeUsual;
	private Long cpf;
	/**
	 * @return the nroRegConselho
	 */
	public String getNroRegConselho() {
		return nroRegConselho;
	}
	/**
	 * @param nroRegConselho the nroRegConselho to set
	 */
	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	/**
	 * @return the espSeq
	 */
	public Short getEspSeq() {
		return espSeq;
	}
	/**
	 * @param espSeq the espSeq to set
	 */
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	/**
	 * @return the matricula
	 */
	public Integer getMatricula() {
		return matricula;
	}
	/**
	 * @param matricula the matricula to set
	 */
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	/**
	 * @return the vinCodigo
	 */
	public Short getVinCodigo() {
		return vinCodigo;
	}
	/**
	 * @param vinCodigo the vinCodigo to set
	 */
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	/**
	 * @return the codigo
	 */
	public Integer getCodigo() {
		return codigo;
	}
	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	/**
	 * @return the nomeMedico
	 */
	public String getNomeMedico() {
		return nomeMedico;
	}
	/**
	 * @param nomeMedico the nomeMedico to set
	 */
	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}
	
	
	public String getNomeUsual() {
		return nomeUsual;
	}
	
	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}
	
	public Long getCpf() {
		return cpf;
	}
	
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
		result = prime * result + ((espSeq == null) ? 0 : espSeq.hashCode());
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result
				+ ((nomeMedico == null) ? 0 : nomeMedico.hashCode());
		result = prime * result
				+ ((nomeUsual == null) ? 0 : nomeUsual.hashCode());
		result = prime * result
				+ ((nroRegConselho == null) ? 0 : nroRegConselho.hashCode());
		result = prime * result
				+ ((vinCodigo == null) ? 0 : vinCodigo.hashCode());
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
		EspCrmVO other = (EspCrmVO) obj;
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		if (cpf == null) {
			if (other.cpf != null) {
				return false;
			}
		} else if (!cpf.equals(other.cpf)) {
			return false;
		}
		if (espSeq == null) {
			if (other.espSeq != null) {
				return false;
			}
		} else if (!espSeq.equals(other.espSeq)) {
			return false;
		}
		if (matricula == null) {
			if (other.matricula != null) {
				return false;
			}
		} else if (!matricula.equals(other.matricula)) {
			return false;
		}
		if (nomeMedico == null) {
			if (other.nomeMedico != null) {
				return false;
			}
		} else if (!nomeMedico.equals(other.nomeMedico)) {
			return false;
		}
		if (nomeUsual == null) {
			if (other.nomeUsual != null) {
				return false;
			}
		} else if (!nomeUsual.equals(other.nomeUsual)) {
			return false;
		}
		if (nroRegConselho == null) {
			if (other.nroRegConselho != null) {
				return false;
			}
		} else if (!nroRegConselho.equals(other.nroRegConselho)) {
			return false;
		}
		if (vinCodigo == null) {
			if (other.vinCodigo != null) {
				return false;
			}
		} else if (!vinCodigo.equals(other.vinCodigo)) {
			return false;
		}
		return true;
	}
}
