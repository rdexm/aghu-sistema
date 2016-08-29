package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;


public class CirurgiaPacienteVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5486568805180953497L;
	private Date data;
	private String nomeResponsavel;
	private String descProcCirurgico;
	private Integer matricula;
	private Short vinculo;
	
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}	
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}
	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	public String getDescProcCirurgico() {
		return descProcCirurgico;
	}
	public void setDescProcCirurgico(String descProcCirurgico) {
		this.descProcCirurgico = descProcCirurgico;
	}
	
	
	
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinculo() {
		return vinculo;
	}
	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime
				* result
				+ ((descProcCirurgico == null) ? 0 : descProcCirurgico
						.hashCode());
		result = prime * result
				+ ((nomeResponsavel == null) ? 0 : nomeResponsavel.hashCode());
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
		CirurgiaPacienteVO other = (CirurgiaPacienteVO) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (descProcCirurgico == null) {
			if (other.descProcCirurgico != null) {
				return false;
			}
		} else if (!descProcCirurgico.equals(other.descProcCirurgico)) {
			return false;
		}
		if (nomeResponsavel == null) {
			if (other.nomeResponsavel != null) {
				return false;
			}
		} else if (!nomeResponsavel.equals(other.nomeResponsavel)) {
			return false;
		}
		return true;
	}
	
}
