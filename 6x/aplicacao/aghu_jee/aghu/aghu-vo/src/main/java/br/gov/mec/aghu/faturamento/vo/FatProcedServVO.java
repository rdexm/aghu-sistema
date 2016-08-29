package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO.TipoProcessado;



public class FatProcedServVO implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3921936170102780455L;

	private Long codTabela;

	private String descricao;

	private String fseCodigo;
	private String fcsCodigo;

	private String descServico;
	private String descClassificacao;

	private String dtCompetencia;
	
	private String servicosValidos;
	
	private StringBuilder sbServicosValidos;
	
	private String servClass;
	
	private boolean indAlteracao;

	private TipoProcessado processado;
	
	public FatProcedServVO() {}
	
	public FatProcedServVO(Long codTabela, String dtCompetencia, String servicosValidos, boolean indAlteracao, TipoProcessado processado) {
		super();
		this.codTabela = codTabela;
		this.dtCompetencia = dtCompetencia;
		this.servicosValidos = servicosValidos;
		this.indAlteracao = indAlteracao;
		this.processado = processado;
	}

	public FatProcedServVO(Long codTabela, StringBuilder sbServicosValidos, String dtCompetencia) {
		super();
		this.codTabela = codTabela;
		this.dtCompetencia = dtCompetencia;
		this.sbServicosValidos = sbServicosValidos;
	}

	public void appendSbServicosValidos(String vl){
		sbServicosValidos.append(vl);
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getFseCodigo() {
		return fseCodigo;
	}

	public void setFseCodigo(String fseCodigo) {
		this.fseCodigo = fseCodigo;
	}

	public String getFcsCodigo() {
		return fcsCodigo;
	}

	public void setFcsCodigo(String fcsCodigo) {
		this.fcsCodigo = fcsCodigo;
	}

	public String getDescServico() {
		return descServico;
	}

	public void setDescServico(String descServico) {
		this.descServico = descServico;
	}

	public String getDescClassificacao() {
		return descClassificacao;
	}

	public void setDescClassificacao(String descClassificacao) {
		this.descClassificacao = descClassificacao;
	}

	public String getDtCompetencia() {
		return dtCompetencia;
	}

	public void setDtCompetencia(String dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}

	public String getServicosValidos() {
		return servicosValidos;
	}

	public void setServicosValidos(String servicosValidos) {
		this.servicosValidos = servicosValidos;
	}

	public StringBuilder getSbServicosValidos() {
		return sbServicosValidos;
	}

	public void setSbServicosValidos(StringBuilder sbServicosValidos) {
		this.sbServicosValidos = sbServicosValidos;
	}

	public String getServClass() {
		return servClass;
	}

	public void setServClass(String servClass) {
		this.servClass = servClass;
	}

	public boolean isIndAlteracao() {
		return indAlteracao;
	}

	public void setIndAlteracao(boolean indAlteracao) {
		this.indAlteracao = indAlteracao;
	}

	public TipoProcessado getProcessado() {
		return processado;
	}

	public void setProcessado(TipoProcessado processado) {
		this.processado = processado;
	}


	public enum Fields {
		COD_TABELA("codTabela"), 
		DESCRICAO("descricao"),
		FSE_CODIGO("fseCodigo"),
		FCS_CODIGO("fcsCodigo"),
		DESC_SERVICO("descServico"),
		DESC_CLASSIFICACAO("descClassificacao"),
		DT_COMPETENCIA("dtCompetencia"),
		SERV_CLASS("servClass");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}