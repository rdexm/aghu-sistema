package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioUtilizacaoSala;

public class PortalPesquisaCirurgiasAgendaEscalaVO implements Serializable {

	/**
	 * VO utilizado para carregar dados do detalhamento de agendas e escalas no portal de pesquisa de cirurgias
	 */
	private static final long serialVersionUID = 1426434886279913890L;

	private Integer agdSeq;
	private Integer crgSeq;
	private Date data;
	private Date dthrPrevInicio;
	private Date dthrPrevFim;
	private String nomePaciente;
	private Integer prontuario;
	private String prontuarioFormatado;
	private String procedimento;
	private String descricaoUnidade;
	private String especialidade;
	private String equipe;
	private String convenio;
	private Boolean escala = false;
	private Boolean planejado = false;
	private Boolean realizado = false;
	private Boolean cancelado = false;
	private Short sciSeqp; 
	private String aproveitamentoSala;
	private DominioSituacaoAgendas situacaoAgenda;
	private DominioSituacaoCirurgia situacaoCirurgia;
	private DominioNaturezaFichaAnestesia naturezaAgendamento;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Integer ordemVisualizacao;
	private DominioUtilizacaoSala utilizacaoSala;
	
	public Integer getAgdSeq() {
		return agdSeq;
	}
	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}
	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}
	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialiade) {
		this.especialidade = especialiade;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getConvenio() {
		return convenio;
	}
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	public Boolean getEscala() {
		return escala;
	}
	public void setEscala(Boolean escala) {
		this.escala = escala;
	}
	public Boolean getPlanejado() {
		return planejado;
	}
	public void setPlanejado(Boolean planejado) {
		this.planejado = planejado;
	}
	public Boolean getRealizado() {
		return realizado;
	}
	public void setRealizado(Boolean realizado) {
		this.realizado = realizado;
	}
	public Boolean getCancelado() {
		return cancelado;
	}
	public void setCancelado(Boolean cancelado) {
		this.cancelado = cancelado;
	}
	public Short getSciSeqp() {
		return sciSeqp;
	}
	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}
	public String getAproveitamentoSala() {
		return aproveitamentoSala;
	}
	public void setAproveitamentoSala(String aproveitamentoSala) {
		this.aproveitamentoSala = aproveitamentoSala;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Date getData() {
		return data;
	}
	public void setSituacaoAgenda(DominioSituacaoAgendas situacaoAgenda) {
		this.situacaoAgenda = situacaoAgenda;
	}
	public DominioSituacaoAgendas getSituacaoAgenda() {
		return situacaoAgenda;
	}
	public void setNaturezaAgendamento(DominioNaturezaFichaAnestesia naturezaAgendamento) {
		this.naturezaAgendamento = naturezaAgendamento;
	}
	public DominioNaturezaFichaAnestesia getNaturezaAgendamento() {
		return naturezaAgendamento;
	}
	public void setSituacaoCirurgia(DominioSituacaoCirurgia situacaoCirurgia) {
		this.situacaoCirurgia = situacaoCirurgia;
	}
	public DominioSituacaoCirurgia getSituacaoCirurgia() {
		return situacaoCirurgia;
	}
	
	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setDthrPrevFim(Date dthrPrevFim) {
		this.dthrPrevFim = dthrPrevFim;
	}
	public Date getDthrPrevFim() {
		return dthrPrevFim;
	}

	public void setDthrPrevInicio(Date dthrPrevInicio) {
		this.dthrPrevInicio = dthrPrevInicio;
	}
	public Date getDthrPrevInicio() {
		return dthrPrevInicio;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}
	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}
	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}

	public void setUtilizacaoSala(DominioUtilizacaoSala utilizacaoSala) {
		this.utilizacaoSala = utilizacaoSala;
	}
	public DominioUtilizacaoSala getUtilizacaoSala() {
		return utilizacaoSala;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setOrdemVisualizacao(Integer ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
	}
	public Integer getOrdemVisualizacao() {
		return ordemVisualizacao;
	}

	public enum Fields {
		AGD_SEQ("agdSeq"), 
		CRG_SEQ("crgSeq"),
		DATA("data"),
		NOME_PACIENTE("nomePaciente"),
		PRONTUARIO("prontuario"),
		PRONTUARIO_FORMATADO("prontuarioFormatado"),
		PROCEDIMENTO("procedimento"),
		DESCRICAO_UNIDADE("descricaoUnidade"),
		ESPECIALIDADE("especialidade"),
		EQUIPE("equipe"),
		CONVENIO("convenio"),
		SCI_SEQP("sciSeqp"),
		SITUACAO_AGENDA("situacaoAgenda"),
		SITUACAO_CIRURGIA("situacaoCirurgia"),
		NATUREZA_AGENDAMENTO("naturezaAgendamento"),
		UNIDADE_FUNCIONAL("descricaoUnidade"),
		DT_PREV_INICIO("dthrPrevInicio"),
		DT_PREV_FIM("dthrPrevFim"),
		PUC_SER_MATRICULA("pucSerMatricula"),
		PUC_SER_VIN_CODIGO("pucSerVinCodigo"),
		UTILIZACAO_SALA("utilizacaoSala"),
		ORDEM_VISUALIZACAO("ordemVisualizacao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}
