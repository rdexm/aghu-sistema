package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConsulta;

public class ConsultasGradeVO implements Serializable {

	private static final long serialVersionUID = -2819474161276813751L;	 
	
	private Integer seqGrade;
	
	private Integer numeroConsulta;
	
	private Integer prontuario;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private DominioSituacaoConsulta situacaoConsulta;
	
	private String stcSituacaoConsulta;
	
	private Boolean exigeProntuario;
	
	private Date dataConsulta;
	
	private String descricaoCondicaoAtendimento;
	
	private String descricaoSituacaoConsulta;
	
	private String descricaoPagador;
	
	private String descricaoAutorizacao;
	
	private Short seqRetorno;
	
	private String descricaoRetorno;
	
	private Short seqMotivo;
	
	private String descricaoMotivo;
	
	private Integer codigoPaciente;
	
	private String nomePaciente;
	
	private Date alteradoEm;
	
	private String nomeAlteradoEm;
	
	private Boolean consultaExcedente;
	
	public enum Fields {
		
		ALTERADO_EM("alteradoEm"),
		CODIGO_PACIENTE("codigoPaciente"),
		CONSULTA_EXCEDENTE("consultaExcedente"),
		DATA_CONSULTA("dataConsulta"),
		DESCRICAO_AUTORIZACAO("descricaoAutorizacao"),
		DESCRICAO_CONDICAO_ATENDIMENTO("descricaoCondicaoAtendimento"),
		DESCRICAO_MOTIVO("descricaoMotivo"),
		DESCRICAO_PAGADOR("descricaoPagador"),
		DESCRICAO_RETORNO("descricaoRetorno"),
		DESCRICAO_SITUACAO_CONSULTA("descricaoSituacaoConsulta"),		
		EXIGE_PRONTUARIO("exigeProntuario"),
		NOME_ALTERADO_EM("nomeAlteradoEm"),
		NOME_PACIENTE("nomePaciente"),
		NUMERO_CONSULTA("numeroConsulta"),
		PRONTUARIO("prontuario"),
		SEQ_MOTIVO("seqMotivo"),
		SEQ_RETORNO("seqRetorno"),
		STC_SITUACAO_CONSULTA("stcSituacaoConsulta");		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public DominioSituacaoConsulta getSituacaoConsulta() {
		return situacaoConsulta;
	}

	public void setSituacaoConsulta(DominioSituacaoConsulta situacaoConsulta) {
		this.situacaoConsulta = situacaoConsulta;
	}

	public String getStcSituacaoConsulta() {
		return stcSituacaoConsulta;
	}

	public void setStcSituacaoConsulta(String stcSituacaoConsulta) {
		this.stcSituacaoConsulta = stcSituacaoConsulta;
	}

	public Boolean getExigeProntuario() {
		return exigeProntuario;
	}

	public void setExigeProntuario(Boolean exigeProntuario) {
		this.exigeProntuario = exigeProntuario;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getDescricaoCondicaoAtendimento() {
		return descricaoCondicaoAtendimento;
	}

	public void setDescricaoCondicaoAtendimento(String descricaoCondicaoAtendimento) {
		this.descricaoCondicaoAtendimento = descricaoCondicaoAtendimento;
	}

	public String getDescricaoSituacaoConsulta() {
		return descricaoSituacaoConsulta;
	}

	public void setDescricaoSituacaoConsulta(String descricaoSituacaoConsulta) {
		this.descricaoSituacaoConsulta = descricaoSituacaoConsulta;
	}

	public String getDescricaoPagador() {
		return descricaoPagador;
	}

	public void setDescricaoPagador(String descricaoPagador) {
		this.descricaoPagador = descricaoPagador;
	}

	public String getDescricaoAutorizacao() {
		return descricaoAutorizacao;
	}

	public void setDescricaoAutorizacao(String descricaoAutorizacao) {
		this.descricaoAutorizacao = descricaoAutorizacao;
	}

	public Short getSeqRetorno() {
		return seqRetorno;
	}

	public void setSeqRetorno(Short seqRetorno) {
		this.seqRetorno = seqRetorno;
	}

	public String getDescricaoRetorno() {
		return descricaoRetorno;
	}

	public void setDescricaoRetorno(String descricaoRetorno) {
		this.descricaoRetorno = descricaoRetorno;
	}

	public Short getSeqMotivo() {
		return seqMotivo;
	}

	public void setSeqMotivo(Short seqMotivo) {
		this.seqMotivo = seqMotivo;
	}

	public String getDescricaoMotivo() {
		return descricaoMotivo;
	}

	public void setDescricaoMotivo(String descricaoMotivo) {
		this.descricaoMotivo = descricaoMotivo;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public String getNomeAlteradoEm() {
		return nomeAlteradoEm;
	}

	public void setNomeAlteradoEm(String nomeAlteradoEm) {
		this.nomeAlteradoEm = nomeAlteradoEm;
	}

	public Boolean getConsultaExcedente() {
		return consultaExcedente;
	}

	public void setConsultaExcedente(Boolean consultaExcedente) {
		this.consultaExcedente = consultaExcedente;
	}

}
