package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

@SuppressWarnings({"PMD.CyclomaticComplexity", "ucd"})
public class AtendimentosVO  implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7629433709683809562L;
	
	// atributos diretos do objeto
	private Date dataAtendimento;	
	private String nomeReduzidoEspecialidade;
	private String descricaoCentroCusto;
	private Short vinCodigoServidor;
	private Integer matriculaServidor;
	private Short vinCodigoServidor2;
	private Integer matriculaServidor2;
	private String nomeDescricaoEquipe;
	
	// atributos processados 
	private String tipoAtendimento;
	private String especialidadeServico;
	private String profissional;
	
	public AtendimentosVO() {

	}
	

	public AtendimentosVO(Date dataAtendimento,
			String nomeReduzidoEspecialidade, String descricaoCentroCusto,
			Short vinCodigoServidor, Integer matriculaServidor,
			Short vinCodigoServidor2, Integer matriculaServidor2,
			String nomeDescricaoEquipe, String tipoAtendimento,
			String especialidadeServico, String profissional) {
		this.dataAtendimento = dataAtendimento;
		this.nomeReduzidoEspecialidade = nomeReduzidoEspecialidade;
		this.descricaoCentroCusto = descricaoCentroCusto;
		this.vinCodigoServidor = vinCodigoServidor;
		this.matriculaServidor = matriculaServidor;
		this.vinCodigoServidor2 = vinCodigoServidor2;
		this.matriculaServidor2 = matriculaServidor2;
		this.nomeDescricaoEquipe = nomeDescricaoEquipe;
		this.tipoAtendimento = tipoAtendimento;
		this.especialidadeServico = especialidadeServico;
		this.profissional = profissional;
	}
	
	public AtendimentosVO(Date dataAtendimento,	String nomeDescricaoEquipe) {
		this.dataAtendimento = dataAtendimento;
		this.nomeDescricaoEquipe = nomeDescricaoEquipe;
	}
	
	public AtendimentosVO(Date dataAtendimento, Short vinCodigoServidor,
			Integer matriculaServidor) {
		super();
		this.dataAtendimento = dataAtendimento;
		this.vinCodigoServidor = vinCodigoServidor;
		this.matriculaServidor = matriculaServidor;
	}

	public enum Fields {
		
		DATA_ATENDIMENTO("dataAtendimento"),
		NOME_REDUZIDO_ESPECIALIDADE("nomeReduzidoEspecialidade"),
		DESCRICAO_CENTRO_CUSTO("descricaoCentroCusto"),
		VIN_CODIGO_SERVIDOR("vinCodigoServidor"),
		MATRICULA_SERVIDOR("matriculaServidor"),
		VIN_CODIGO_SERVIDOR_2("vinCodigoServidor2"),
		MATRICULA_SERVIDOR_2("matriculaServidor2");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public String getEspecialidadeServicoTrunc(Long size){
		if(size != null && getEspecialidadeServico() != null && getEspecialidadeServico().length() > size) {
			return getEspecialidadeServico().substring(0,size.intValue()-2) + "...";
		} else {
			return getEspecialidadeServico();
		}
	}
	
	public String getProfissionalTrunc(Long size){
		if(size != null && getProfissional() != null && getProfissional().length() > size) {
			return getProfissional().substring(0,size.intValue()-2) + "...";
		} else {
			return getProfissional();
		}
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}


	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}


	public String getNomeReduzidoEspecialidade() {
		return nomeReduzidoEspecialidade;
	}


	public void setNomeReduzidoEspecialidade(String nomeReduzidoEspecialidade) {
		this.nomeReduzidoEspecialidade = nomeReduzidoEspecialidade;
	}


	public String getDescricaoCentroCusto() {
		return descricaoCentroCusto;
	}


	public void setDescricaoCentroCusto(String descricaoCentroCusto) {
		this.descricaoCentroCusto = descricaoCentroCusto;
	}


	public Short getVinCodigoServidor() {
		return vinCodigoServidor;
	}


	public void setVinCodigoServidor(Short vinCodigoServidor) {
		this.vinCodigoServidor = vinCodigoServidor;
	}


	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}


	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}


	public Short getVinCodigoServidor2() {
		return vinCodigoServidor2;
	}


	public void setVinCodigoServidor2(Short vinCodigoServidor2) {
		this.vinCodigoServidor2 = vinCodigoServidor2;
	}


	public Integer getMatriculaServidor2() {
		return matriculaServidor2;
	}


	public void setMatriculaServidor2(Integer matriculaServidor2) {
		this.matriculaServidor2 = matriculaServidor2;
	}


	public String getNomeDescricaoEquipe() {
		return nomeDescricaoEquipe;
	}


	public void setNomeDescricaoEquipe(String nomeDescricaoEquipe) {
		this.nomeDescricaoEquipe = nomeDescricaoEquipe;
	}


	public String getTipoAtendimento() {
		return tipoAtendimento;
	}


	public void setTipoAtendimento(String tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}


	public String getEspecialidadeServico() {
		return especialidadeServico;
	}


	public void setEspecialidadeServico(String especialidadeServico) {
		this.especialidadeServico = especialidadeServico;
	}


	public String getProfissional() {
		return profissional;
	}


	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}


	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataAtendimento == null) ? 0 : dataAtendimento.hashCode());
		result = prime
				* result
				+ ((descricaoCentroCusto == null) ? 0 : descricaoCentroCusto
						.hashCode());
		result = prime
				* result
				+ ((especialidadeServico == null) ? 0 : especialidadeServico
						.hashCode());
		result = prime
				* result
				+ ((matriculaServidor == null) ? 0 : matriculaServidor
						.hashCode());
		result = prime
				* result
				+ ((matriculaServidor2 == null) ? 0 : matriculaServidor2
						.hashCode());
		result = prime
				* result
				+ ((nomeDescricaoEquipe == null) ? 0 : nomeDescricaoEquipe
						.hashCode());
		result = prime
				* result
				+ ((nomeReduzidoEspecialidade == null) ? 0
						: nomeReduzidoEspecialidade.hashCode());
		result = prime * result
				+ ((profissional == null) ? 0 : profissional.hashCode());
		result = prime * result
				+ ((tipoAtendimento == null) ? 0 : tipoAtendimento.hashCode());
		result = prime
				* result
				+ ((vinCodigoServidor == null) ? 0 : vinCodigoServidor
						.hashCode());
		result = prime
				* result
				+ ((vinCodigoServidor2 == null) ? 0 : vinCodigoServidor2
						.hashCode());
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
		AtendimentosVO other = (AtendimentosVO) obj;
		if (dataAtendimento == null) {
			if (other.dataAtendimento != null) {
				return false;
			}
		} else if (!dataAtendimento.equals(other.dataAtendimento)) {
			return false;
		}
		if (descricaoCentroCusto == null) {
			if (other.descricaoCentroCusto != null) {
				return false;
			}
		} else if (!descricaoCentroCusto.equals(other.descricaoCentroCusto)) {
			return false;
		}
		if (especialidadeServico == null) {
			if (other.especialidadeServico != null) {
				return false;
			}
		} else if (!especialidadeServico.equals(other.especialidadeServico)) {
			return false;
		}
		if (matriculaServidor == null) {
			if (other.matriculaServidor != null) {
				return false;
			}
		} else if (!matriculaServidor.equals(other.matriculaServidor)) {
			return false;
		}
		if (matriculaServidor2 == null) {
			if (other.matriculaServidor2 != null) {
				return false;
			}
		} else if (!matriculaServidor2.equals(other.matriculaServidor2)) {
			return false;
		}
		if (nomeDescricaoEquipe == null) {
			if (other.nomeDescricaoEquipe != null) {
				return false;
			}
		} else if (!nomeDescricaoEquipe.equals(other.nomeDescricaoEquipe)) {
			return false;
		}
		if (nomeReduzidoEspecialidade == null) {
			if (other.nomeReduzidoEspecialidade != null) {
				return false;
			}
		} else if (!nomeReduzidoEspecialidade
				.equals(other.nomeReduzidoEspecialidade)) {
			return false;
		}
		if (profissional == null) {
			if (other.profissional != null) {
				return false;
			}
		} else if (!profissional.equals(other.profissional)) {
			return false;
		}
		if (tipoAtendimento == null) {
			if (other.tipoAtendimento != null) {
				return false;
			}
		} else if (!tipoAtendimento.equals(other.tipoAtendimento)) {
			return false;
		}
		if (vinCodigoServidor == null) {
			if (other.vinCodigoServidor != null) {
				return false;
			}
		} else if (!vinCodigoServidor.equals(other.vinCodigoServidor)) {
			return false;
		}
		if (vinCodigoServidor2 == null) {
			if (other.vinCodigoServidor2 != null) {
				return false;
			}
		} else if (!vinCodigoServidor2.equals(other.vinCodigoServidor2)) {
			return false;
		}
		return true;
	}
}
