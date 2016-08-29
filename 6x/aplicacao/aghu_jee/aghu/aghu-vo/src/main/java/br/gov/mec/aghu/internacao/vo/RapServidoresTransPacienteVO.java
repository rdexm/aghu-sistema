package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


/**
 * 
 * 
 * 
 * 
 */

public class RapServidoresTransPacienteVO implements BaseBean {
	/**
	 * Número da matrícula do servidor do HCPA.
	 */
	private Integer matricula;
	/**
	 * Código do vínculo que o servidor tem com o HCPA
	 */
	private Short vinCodigo;
	/**
	 * 
	 */
	private String nroRegConselho;
	/**
	 * 
	 */
	private String nome;
	
	
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public String getNroRegConselho() {
		return nroRegConselho;
	}
	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nroRegConselho == null) ? 0 : nroRegConselho.hashCode());
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
		RapServidoresTransPacienteVO other = (RapServidoresTransPacienteVO) obj;
		if (nroRegConselho == null) {
			if (other.nroRegConselho != null) {
				return false;
			}
		} else if (!nroRegConselho.equals(other.nroRegConselho)) {
			return false;
		}
		return true;
	}
	
}
