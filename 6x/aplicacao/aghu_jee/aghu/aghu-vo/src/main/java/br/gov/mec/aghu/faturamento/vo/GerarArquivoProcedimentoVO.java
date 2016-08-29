package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

public class GerarArquivoProcedimentoVO {
	
	private String descricaoFCF;
	
	private String descricaoFCC;

	private Long codTabela;
	
	private String descricaoIPH;
	
	private BigDecimal vlrProcedimento;
	
	private BigDecimal vlrServProfissional;
	
	private BigDecimal vlrServProfisAmbulatorio;
	
	private BigDecimal vlrSadt;
	
	private BigDecimal vlrAnestesista;
	
	private String phiDescricao;
	
	private Integer phiSeq;
	
	private String siglaExame;
	
	private String descricaoExame;

	public String getDescricaoFCF() {
		return descricaoFCF;
	}

	public void setDescricaoFCF(String descricaoFCF) {
		this.descricaoFCF = descricaoFCF;
	}

	public String getDescricaoFCC() {
		return descricaoFCC;
	}

	public void setDescricaoFCC(String descricaoFCC) {
		this.descricaoFCC = descricaoFCC;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricaoIPH() {
		return descricaoIPH;
	}

	public void setDescricaoIPH(String descricaoIPH) {
		this.descricaoIPH = descricaoIPH;
	}

	public BigDecimal getVlrProcedimento() {
		return vlrProcedimento;
	}

	public void setVlrProcedimento(BigDecimal vlrProcedimento) {
		this.vlrProcedimento = vlrProcedimento;
	}

	public BigDecimal getVlrServProfissional() {
		return vlrServProfissional;
	}

	public void setVlrServProfissional(BigDecimal vlrServProfissional) {
		this.vlrServProfissional = vlrServProfissional;
	}

	public BigDecimal getVlrServProfisAmbulatorio() {
		return vlrServProfisAmbulatorio;
	}

	public void setVlrServProfisAmbulatorio(BigDecimal vlrServProfisAmbulatorio) {
		this.vlrServProfisAmbulatorio = vlrServProfisAmbulatorio;
	}

	public BigDecimal getVlrSadt() {
		return vlrSadt;
	}

	public void setVlrSadt(BigDecimal vlrSadt) {
		this.vlrSadt = vlrSadt;
	}

	public BigDecimal getVlrAnestesista() {
		return vlrAnestesista;
	}

	public void setVlrAnestesista(BigDecimal vlrAnestesista) {
		this.vlrAnestesista = vlrAnestesista;
	}

	public String getPhiDescricao() {
		return phiDescricao;
	}

	public void setPhiDescricao(String phiDescricao) {
		this.phiDescricao = phiDescricao;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
		
	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}
	
	public String getSiglaExame() {
		return siglaExame;
	}

	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}
	
	public enum Fields {
		
		DESCRICAO_FCF("descricaoFCF"),
		DESCRICAO_FCC("descricaoFCC"),
		COD_TABELA("codTabela"),
		DESCRICAO_IPH("descricaoIPH"),
		VLR_PROCEDIMENTO("vlrProcedimento"),
		VLR_SERV_PROFISSIONAL("vlrServProfissional"),
		VLR_SERV_PROF_AMB("vlrServProfisAmbulatorio"),
		VLR_SADT("vlrSadt"),
		VLR_ANESTESIA("vlrAnestesista"),
		PHI_DESCRICAO("phiDescricao"),
		PHI_SEQ("phiSeq"),
		DESCRICAO_EXAME("descricaoExame"),
		SIGLA_EXAME("siglaExame");
		
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