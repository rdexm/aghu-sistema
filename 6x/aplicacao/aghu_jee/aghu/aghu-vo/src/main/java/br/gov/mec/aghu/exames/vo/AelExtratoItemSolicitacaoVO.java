package br.gov.mec.aghu.exames.vo;

import java.util.Date;

public class AelExtratoItemSolicitacaoVO {
	
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Short seqp;
	private Date criadoEm;
	private String situacao;
	private String descricao;
	private String servidor;
	private String servidorResponsavel;
	private String motivoCancelamento;
	private String complementoMotivoCancelamento;
	private Date dataHoraEvento;
	private String nomeConselho;
	private String nomeUsualConselho;
	private String siglaConselho;
	private String nroRegConselho;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String pessoaFisicaNome;
	private Long countSeqp;
	
	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}
	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}
	public Short getIseSeqp() {
		return iseSeqp;
	}
	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getServidor() {
		return servidor;
	}
	public void setServidor(String servidor) {
		this.servidor = servidor;
	}
	public String getServidorResponsavel() {
		return servidorResponsavel;
	}
	public void setServidorResponsavel(String servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}
	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}
	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}
	public String getComplementoMotivoCancelamento() {
		return complementoMotivoCancelamento;
	}
	public void setComplementoMotivoCancelamento(
			String complementoMotivoCancelamento) {
		this.complementoMotivoCancelamento = complementoMotivoCancelamento;
	}
	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}
	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}
	public String getNomeConselho() {
		return nomeConselho;
	}
	public void setNomeConselho(String nomeConselho) {
		this.nomeConselho = nomeConselho;
	}
	public String getNomeUsualConselho() {
		return nomeUsualConselho;
	}
	public void setNomeUsualConselho(String nomeUsualConselho) {
		this.nomeUsualConselho = nomeUsualConselho;
	}
	public String getSiglaConselho() {
		return siglaConselho;
	}
	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}
	public String getNroRegConselho() {
		return nroRegConselho;
	}
	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	
	public String getPessoaFisicaNome() {
		return pessoaFisicaNome;
	}
	public void setPessoaFisicaNome(String pessoaFisicaNome) {
		this.pessoaFisicaNome = pessoaFisicaNome;
	}
	
	public Long getCountSeqp() {
		return countSeqp;
	}
	public void setCountSeqp(Long countSeqp) {
		this.countSeqp = countSeqp;
	}
	
	public enum Fields {
		ISE_SEQP("iseSeqp"),//
		SER_VIN_CODIGO("serVinCodigo"),//
		SER_MATRICULA("serMatricula"),//
		PESSOA_FISICA_NOME("pessoaFisicaNome"),//
		DATA_HORA_EVENTO("dataHoraEvento"),//
		COUNT_SEQP("countSeqp"),//
		CRIADO_EM("criadoEm"),//
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	


	

}
