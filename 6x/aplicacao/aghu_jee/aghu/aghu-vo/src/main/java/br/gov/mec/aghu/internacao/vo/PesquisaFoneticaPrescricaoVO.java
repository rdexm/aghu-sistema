package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaFoneticaPrescricaoVO implements BaseBean{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1967679357728469659L;

	
	private String leitoId;
	
	private String alaAndar;
	
	private String descricaoQuarto;
	
	private Integer prontuario;
	
	private String nomePaciente;
	
	private Integer codigo;
	
	private String siglaEspecialidade;
	
	private String equipe;
	
	
	/**
	 * @return the leito
	 */
	public String getLeitoId() {
		return leitoId;
	}

	/**
	 * @param leito the leito to set
	 */
	public void setLeitoId(String leito) {
		this.leitoId = leito;
	}

	/**
	 * @return the alaAndar
	 */
	public String getAlaAndar() {
		return alaAndar;
	}

	/**
	 * @param alaAndar the alaAndar to set
	 */
	public void setAlaAndar(String alaAndar) {
		this.alaAndar = alaAndar;
	}

	public String getDescricaoQuarto() {
		return descricaoQuarto;
	}

	public void setDescricaoQuarto(String descricaoQuarto) {
		this.descricaoQuarto = descricaoQuarto;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	
	
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	
	
	

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result
				+ ((nomePaciente == null) ? 0 : nomePaciente.hashCode());
		result = prime * result
				+ ((prontuario == null) ? 0 : prontuario.hashCode());
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
		PesquisaFoneticaPrescricaoVO other = (PesquisaFoneticaPrescricaoVO) obj;
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		if (nomePaciente == null) {
			if (other.nomePaciente != null) {
				return false;
			}
		} else if (!nomePaciente.equals(other.nomePaciente)) {
			return false;
		}
		if (prontuario == null) {
			if (other.prontuario != null) {
				return false;
			}
		} else if (!prontuario.equals(other.prontuario)) {
			return false;
		}
		return true;
	}

	

	
	
	
		
}
