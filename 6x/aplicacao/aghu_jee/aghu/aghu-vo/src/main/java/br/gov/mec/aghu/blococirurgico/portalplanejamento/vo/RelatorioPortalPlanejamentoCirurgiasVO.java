package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;

public class RelatorioPortalPlanejamentoCirurgiasVO {
	
	private Integer seq;
	private String local; //1
	private String especialidade; //2
	private String equipe; //3
	
	private String dataAgenda; //4
	private String dataAgendaOrdenar;
	private String nomePaciente; //5
	private String prontuario; //6
	private String procedimento; //7
	private String tempoSala; //8
	private String sala; //9 
	private String comentario; //10
	private String anotacoes; //11
	
	private DominioSituacaoAgendas indSituacao;
	
	private Integer ordem;
	
	private Integer prioridade;
	
	private Date dthrPrevInicio;
	
	private Date dthrInclusao;

	private Boolean indGeradoSistema;
	
	private Date dataCirurgia;
	
	public enum Fields {
		DATA_AGENDA_ORDENAR("dataAgendaOrdenar"),
		ORDEM("ordem"),
		IND_SITUACAO("indSituacao"),
		PRIORIDADE("prioridade"),
		DTHR_PREV_INICIO("dthrPrevInicio"),
		DTHR_INCLUSAO("dthrInclusao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}	
	
	public String getDataAgenda() {
		return dataAgenda;
	}

	public void setDataAgenda(String dataAgenda) {
		this.dataAgenda = dataAgenda;
	}

	public String getDataAgendaOrdenar() {
		return dataAgendaOrdenar;
	}

	public void setDataAgendaOrdenar(String dataAgendaOrdenar) {
		this.dataAgendaOrdenar = dataAgendaOrdenar;
	}
	
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
		
	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	
	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}
	
	public String getTempoSala() {
		return tempoSala;
	}

	public void setTempoSala(String tempoSala) {
		this.tempoSala = tempoSala;
	}
	
	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getAnotacoes() {
		return anotacoes;
	}

	public void setAnotacoes(String anotacoes) {
		this.anotacoes = anotacoes;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public DominioSituacaoAgendas getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAgendas indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	
	public Integer getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Integer prioridade) {
		this.prioridade = prioridade;
	}

	public Date getDthrPrevInicio() {
		return dthrPrevInicio;
	}

	public void setDthrPrevInicio(Date dthrPrevInicio) {
		this.dthrPrevInicio = dthrPrevInicio;
	}
	
	
	public Date getDthrInclusao() {
		return dthrInclusao;
	}

	public void setDthrInclusao(Date dthrInclusao) {
		this.dthrInclusao = dthrInclusao;
	}
	
	public Boolean getIndGeradoSistema() {
		return indGeradoSistema;
	}

	public void setIndGeradoSistema(Boolean indGeradoSistema) {
		this.indGeradoSistema = indGeradoSistema;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}
}
	
