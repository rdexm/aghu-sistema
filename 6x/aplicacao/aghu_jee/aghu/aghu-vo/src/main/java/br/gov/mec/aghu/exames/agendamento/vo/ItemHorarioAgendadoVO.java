package br.gov.mec.aghu.exames.agendamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;

/**
 * 
 * @author diego.pacheco
 *
 */

public class ItemHorarioAgendadoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6247285355445850480L;

	// Solicitação do exame
	private Integer soeSeq;
	private Short seqp;
		
	private String sigla;
	
	private String descricaoExame;
	
	private String descricaoMaterialAnalise;
	
	private String descricaoUnidade;
	
	private String descricaoSituacao;
	
	private String codigoSituacao;
	
	private String dthrAgenda;
	
	private Integer grade;
	
	private String sala;
	
	private AelItemHorarioAgendadoId itemHorarioAgendadoId;
	
	private Integer seqMaterialAnalise;
	
	private Short seqUnidade;
	
	private Integer pacCodigo;
	
	private Boolean visualizaExames;
	
	private String dataAgendamento;
	
	private String horaAgendamento;
	
	private Integer nroAmostra;
	
	private Short nroEtapa;
	
	private Boolean selecionado;
	
	// Atributo relacionados ao agendamento em grupo
	private Boolean identificadoAgendamentoExameEmGrupo;
	
	private AelItemSolicitacaoExames itemSolicitacaoExameOriginal;
	
	
	public String getDataAgendamento() {
		return dataAgendamento;
	}

	public void setDataAgendamento(String dataAgendamento) {
		this.dataAgendamento = dataAgendamento;
	}

	public String getHoraAgendamento() {
		return horaAgendamento;
	}

	public void setHoraAgendamento(String horaAgendamento) {
		this.horaAgendamento = horaAgendamento;
	}

	private AelUnfExecutaExamesId unfExecutaExamesId;

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public String getDescricaoMaterialAnalise() {
		return descricaoMaterialAnalise;
	}

	public void setDescricaoMaterialAnalise(String descricaoMaterialAnalise) {
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}

	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}

	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}

	public String getDescricaoSituacao() {
		return descricaoSituacao;
	}

	public void setDescricaoSituacao(String descricaoSituacao) {
		this.descricaoSituacao = descricaoSituacao;
	}
	
	
	public String getCodigoSituacao() {
		return codigoSituacao;
	}

	public void setCodigoSituacao(String codigoSituacao) {
		this.codigoSituacao = codigoSituacao;
	}

	public String getDthrAgenda() {
		return dthrAgenda;
	}

	public void setDthrAgenda(String dthrAgenda) {
		this.dthrAgenda = dthrAgenda;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public AelItemHorarioAgendadoId getItemHorarioAgendadoId() {
		return itemHorarioAgendadoId;
	}

	public void setItemHorarioAgendadoId(
			AelItemHorarioAgendadoId itemHorarioAgendadoId) {
		this.itemHorarioAgendadoId = itemHorarioAgendadoId;
	}

	public Integer getSeqMaterialAnalise() {
		return seqMaterialAnalise;
	}

	public void setSeqMaterialAnalise(Integer seqMaterialAnalise) {
		this.seqMaterialAnalise = seqMaterialAnalise;
	}

	public Short getSeqUnidade() {
		return seqUnidade;
	}

	public void setSeqUnidade(Short seqUnidade) {
		this.seqUnidade = seqUnidade;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public void setVisualizaExames(Boolean visualizaExames) {
		this.visualizaExames = visualizaExames;
	}

	public Boolean getVisualizaExames() {
		return visualizaExames;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setUnfExecutaExamesId(AelUnfExecutaExamesId unfExecutaExamesId) {
		this.unfExecutaExamesId = unfExecutaExamesId;
	}

	public AelUnfExecutaExamesId getUnfExecutaExamesId() {
		return unfExecutaExamesId;
	}

	public Integer getNroAmostra() {
		return nroAmostra;
	}

	public void setNroAmostra(Integer nroAmostra) {
		this.nroAmostra = nroAmostra;
	}

	public Short getNroEtapa() {
		return nroEtapa;
	}

	public void setNroEtapa(Short nroEtapa) {
		this.nroEtapa = nroEtapa;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Boolean getIdentificadoAgendamentoExameEmGrupo() {
		return identificadoAgendamentoExameEmGrupo;
	}

	public void setIdentificadoAgendamentoExameEmGrupo(
			Boolean identificadoAgendamentoExameEmGrupo) {
		this.identificadoAgendamentoExameEmGrupo = identificadoAgendamentoExameEmGrupo;
	}

	public AelItemSolicitacaoExames getItemSolicitacaoExameOriginal() {
		return itemSolicitacaoExameOriginal;
	}

	public void setItemSolicitacaoExameOriginal(
			AelItemSolicitacaoExames itemSolicitacaoExameOriginal) {
		this.itemSolicitacaoExameOriginal = itemSolicitacaoExameOriginal;
	}

}
