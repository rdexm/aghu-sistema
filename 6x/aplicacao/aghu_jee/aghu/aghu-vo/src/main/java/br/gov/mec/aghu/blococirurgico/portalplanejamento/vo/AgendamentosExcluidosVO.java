package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AgendamentosExcluidosVO implements BaseBean {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8506093549019024328L;
	
	private Integer agendaSeq; 
	private String especialidadeSigla;
	private String especialidadeNomeEspecialidade;
	private String equipe;
	private String paciente;
	private Integer prontuario;
	private Date dtExclusao;
	private DominioSituacaoAgendas situacao;
	private DominioRegimeProcedimentoCirurgicoSus regime;
	private String vpeDescricao;
	private String unidadeFuncional;
	private List<String> agendaJustificativas;
	
	public enum Fields {
		
		AGENDA_SEQ("agendaSeq"),
		SIGLA_ESPECIALIDADE("especialidadeSigla"),
		NOME_ESPECIALIDADE("especialidadeNomeEspecialidade"),
		EQUIPE ("equipe"),
		PACIENTE ("paciente"),
		PRONTUARIO ("prontuario"),
		DT_EXCLUSAO ("dtExclusao"),
		SITUACAO ("situacao"),
		VPE_DESCRICAO ("vpeDescricao"),
		REGIME ("regime"),
		UNIDADE_FUNCIONAL ("unidadeFuncional"),
		AGENDA_JUSTIFICATIVAS ("agendaJustificativas");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public static Fields getFieldByDesc(String desc) {
		for(Fields field : Fields.values()) {
			if(field.toString().equals(desc)) {
				return field;
			}
		}
		return null;
	}

	
	public String getVpeDescricao() {
		return vpeDescricao;
	}


	public void setVpeDescricao(String vpeDescricao) {
		this.vpeDescricao = vpeDescricao;
	}


	public String getEquipe() {
		return equipe;
	}


	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}


	public DominioRegimeProcedimentoCirurgicoSus getRegime() {
		return regime;
	}


	public void setRegime(DominioRegimeProcedimentoCirurgicoSus regime) {
		this.regime = regime;
	}


	public Date getDtExclusao() {
		return dtExclusao;
	}

	
	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}
	
	public String getEspecialidadeNomeEspecialidade() {
		return especialidadeNomeEspecialidade;
	}

	public void setEspecialidadeNomeEspecialidade(
			String especialidadeNomeEspecialidade) {
		this.especialidadeNomeEspecialidade = especialidadeNomeEspecialidade;
	}

	public String getEspecialidadeSigla() {
		return especialidadeSigla;
	}

	public void setEspecialidadeSigla(String especialidadeSigla) {
		this.especialidadeSigla = especialidadeSigla;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public DominioSituacaoAgendas getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAgendas situacao) {
		this.situacao = situacao;
	}
	public String getDtExclusaoOrder() {
		if(dtExclusao != null){
			return DateUtil.dataToString(getDtExclusao(), "yyyyMMdd");
		}
		return null;
	}


	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}


	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public Integer getAgendaSeq() {
		return agendaSeq;
	}


	public void setAgendaSeq(Integer agendaSeq) {
		this.agendaSeq = agendaSeq;
	}
	
	public List<String> getAgendaJustificativas() {
		return agendaJustificativas;
	}

	public void setAgendaJustificativas(List<String> agendaJustificativas) {
		this.agendaJustificativas = agendaJustificativas;
	}
	
}
	
