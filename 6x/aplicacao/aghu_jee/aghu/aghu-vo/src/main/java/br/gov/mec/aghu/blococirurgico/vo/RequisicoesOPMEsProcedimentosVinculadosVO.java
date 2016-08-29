package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;

public class RequisicoesOPMEsProcedimentosVinculadosVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4694794692480514014L;
	
	private Short requisicaoSeq;
	private Integer agendaSeq;
	private Date dataRequisicao;
	private Integer requerenteMatricula;
	private Short requerenteVinCodigo;
	private String pes1Nome;
	private Integer etapaSeq;
	private String etapaDescricao;
	private Integer executorMatricula;
	private Short executorVinCodigo;
	private String pes3Nome;
	private Short agendaUnidadeSeq;
	private String unidadeFuncionalDescricao;
	private Date dataProcedimento;
	private Short especialidadeSeq;
	private String nomeEspecialidade;
	private Integer agendaPucMatricula;
	private Short agendaPucVinCodigo;
	private String pes2Nome;
	private Integer prontuario;
	private String nomePaciente;
	private DominioSituacaoRequisicao situacao;
	private Boolean indAutorizado;
	private Integer fluxoSeq;
	private Boolean habilitarBotaoOrcamento = Boolean.FALSE;
	
	private Integer executorRamal;
	private String executorEmail;
	private String sigla;
	
	public enum Fields {
		
		REQUISICAO_SEQ("requisicaoSeq"),
		AGENDA_SEQ("agendaSeq"),
		DATA_REQUISICAO("dataRequisicao"),
		REQUERENTE_MATRICULA("requerenteMatricula"),
		REQUERENTE_VIN_CODIGO("requerenteVinCodigo"),
		PES1_NOME("pes1Nome"),
		ETAPA_SEQ("etapaSeq"),
		ETAPA_DESCRICAO("etapaDescricao"),
		EXECUTOR_MATRICULA("executorMatricula"),
		EXECUTOR_VINCODIGO("executorVinCodigo"),
		PES3_NOME("pes3Nome"),
		AGENDA_UNIDADE_SEQ("agendaUnidadeSeq"),
		UNIDADE_FUNCIONAL_DESCRICAO("unidadeFuncionalDescricao"),
		DATA_PROCEDIMENTO("dataProcedimento"),
		ESPECIALIDADE_SEQ("especialidadeSeq"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		AGENDA_PUC_MATRICULA("agendaPucMatricula"),
		AGENDA_PUC_VINCODIGO("agendaPucVinCodigo"),
		PES2_NOME("pes2Nome"),
		PRONTUARIO("prontuario"),
		NOME_PACIENTE("nomePaciente"),
		SITUACAO("situacao"),
		IND_AUTORIZADO("indAutorizado"), 
		FLUXO_SEQ("fluxoSeq"),
		HABILITA_BOTAO_ORCAMENTO("habilitarBotaoOrcamento"),
		EXECUTOR_RAMAL("executorRamal"),
		SIGLA("sigla"),
		EXECUTOR_EMAIL("executorEmail");
	
		private String fields;
	
		private Fields(String fields) {
			this.fields = fields;
		}
	
		@Override
		public String toString() {
			return this.fields;
		}
	}

	
	public Short getRequisicaoSeq() {
		return requisicaoSeq;
	}
	public void setRequisicaoSeq(Short requisicaoSeq) {
		this.requisicaoSeq = requisicaoSeq;
	}
	public Integer getAgendaSeq() {
		return agendaSeq;
	}
	public void setAgendaSeq(Integer agendaSeq) {
		this.agendaSeq = agendaSeq;
	}
	
	public String getPes1Nome() {
		return pes1Nome;
	}
	public void setPes1Nome(String pes1Nome) {
		this.pes1Nome = pes1Nome;
	}
	public Integer getEtapaSeq() {
		return etapaSeq;
	}
	public void setEtapaSeq(Integer etapaSeq) {
		this.etapaSeq = etapaSeq;
	}
	public String getEtapaDescricao() {
		return etapaDescricao;
	}
	public void setEtapaDescricao(String etapaDescricao) {
		this.etapaDescricao = etapaDescricao;
	}
	public Integer getExecutorMatricula() {
		return executorMatricula;
	}
	public void setExecutorMatricula(Integer executorMatricula) {
		this.executorMatricula = executorMatricula;
	}
	public Short getExecutorVinCodigo() {
		return executorVinCodigo;
	}
	public void setExecutorVinCodigo(Short executorVinCodigo) {
		this.executorVinCodigo = executorVinCodigo;
	}
	public String getPes3Nome() {
		return pes3Nome;
	}
	public void setPes3Nome(String pes3Nome) {
		this.pes3Nome = pes3Nome;
	}
	public Short getAgendaUnidadeSeq() {
		return agendaUnidadeSeq;
	}
	public void setAgendaUnidadeSeq(Short agendaUnidadeSeq) {
		this.agendaUnidadeSeq = agendaUnidadeSeq;
	}
	public String getUnidadeFuncionalDescricao() {
		return unidadeFuncionalDescricao;
	}
	public void setUnidadeFuncionalDescricao(String unidadeFuncionalDescricao) {
		this.unidadeFuncionalDescricao = unidadeFuncionalDescricao;
	}
	public Short getEspecialidadeSeq() {
		return especialidadeSeq;
	}
	public void setEspecialidadeSeq(Short especialidadeSeq) {
		this.especialidadeSeq = especialidadeSeq;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public Integer getAgendaPucMatricula() {
		return agendaPucMatricula;
	}
	public void setAgendaPucMatricula(Integer agendaPucMatricula) {
		this.agendaPucMatricula = agendaPucMatricula;
	}
	public Short getAgendaPucVinCodigo() {
		return agendaPucVinCodigo;
	}
	public void setAgendaPucVinCodigo(Short agendaPucVinCodigo) {
		this.agendaPucVinCodigo = agendaPucVinCodigo;
	}
	public String getPes2Nome() {
		return pes2Nome;
	}
	public void setPes2Nome(String pes2Nome) {
		this.pes2Nome = pes2Nome;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Date getDataRequisicao() {
		return dataRequisicao;
	}
	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}
	public Date getDataProcedimento() {
		return dataProcedimento;
	}
	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}
	public Integer getRequerenteMatricula() {
		return requerenteMatricula;
	}
	public void setRequerenteMatricula(Integer requerenteMatricula) {
		this.requerenteMatricula = requerenteMatricula;
	}
	public Short getRequerenteVinCodigo() {
		return requerenteVinCodigo;
	}
	public void setRequerenteVinCodigo(Short requerenteVinCodigo) {
		this.requerenteVinCodigo = requerenteVinCodigo;
	}
	public DominioSituacaoRequisicao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoRequisicao situacao) {
		this.situacao = situacao;
	}
	public Boolean getIndAutorizado() {
		return indAutorizado;
	}
	public void setIndAutorizado(Boolean indAutorizado) {
		this.indAutorizado = indAutorizado;
	}
	public void setFluxoSeq(Integer fluxoSeq) {
		this.fluxoSeq = fluxoSeq;
	}
	public Integer getFluxoSeq() {
		return fluxoSeq;
	}
	public void setHabilitarBotaoOrcamento(Boolean habilitarBotaoOrcamento) {
		this.habilitarBotaoOrcamento = habilitarBotaoOrcamento;
	}
	public Boolean getHabilitarBotaoOrcamento() {
		return habilitarBotaoOrcamento;
	}
	public void setExecutorRamal(Integer executorRamal) {
		this.executorRamal = executorRamal;
	}
	public Integer getExecutorRamal() {
		return executorRamal;
	}
	public void setExecutorEmail(String executorEmail) {
		this.executorEmail = executorEmail;
	}
	public String getExecutorEmail() {
		return executorEmail;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	public String getInfoExecutor(){
		String nome = this.pes3Nome != null ? this.pes3Nome+"\n" : "\n";
		StringBuffer sb = new StringBuffer(nome);
		
		if(this.executorRamal != null){
			String ramal = "Ramal: "+this.executorRamal+"\n";
			sb.append(ramal); 
		}
		if(StringUtils.isNotEmpty(this.executorEmail)){
			String ramal = "E-mail: "+this.executorEmail+"\n";
			sb.append(ramal);
		}
		return sb.toString();
	}
	
	
	
}
