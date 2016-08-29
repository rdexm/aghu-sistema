package br.gov.mec.aghu.faturamento.vo;



public class CursorCAtosMedVO {

	private Integer indicadorDocumento;
	private String cpf;
	private Short phoSeq;
	private Integer iphSeq;
	private String cbo;
	private String equipe;
	
	private String documentoExecutor;
	private String cnesHcpa;
	private Long codProcedimento;
	private Short quantidade;
	private String competenciaUti;
	private Integer eaiSeq;
	private Integer eaiCthSeq;
	private Byte seqp;
	
	private String cgc;
	private String servClass;
	private Short amaIphPhoSeq;
	private Integer amaIphSeq;
	
	public enum Fields { 

		INDICADOR_DOCUMENTO("indicadorDocumento"), 
		CPF("cpf"),
		IPH_SEQ("iphSeq"), 
		PHO_SEQ("phoSeq"),
		CBO("cbo"), 
		EQUIPE("equipe"),
		DOCUMENTO_EXECUTOR("documentoExecutor"),
		CNES_HCPA("cnesHcpa"),
		COD_PROCEDIMENTO("codProcedimento"),
		QUANTIDADE("quantidade"), 
		COMPETENCIA_UTI("competenciaUti"), 
		EAI_CTH_SEQ("eaiCthSeq"),
		EAI_SEQ("eaiSeq"),
		SEQP("seqp"),
		CGC("cgc"), 
		AMA_IPH_SEQ("amaIphSeq"), 
		AMA_IPH_PHO_SEQ("amaIphPhoSeq")
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getIndicadorDocumento() {
		return indicadorDocumento;
	}

	public void setIndicadorDocumento(Integer indicadorDocumento) {
		this.indicadorDocumento = indicadorDocumento;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public String getCbo() {
		return cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public String getDocumentoExecutor() {
		return documentoExecutor;
	}

	public void setDocumentoExecutor(String documentoExecutor) {
		this.documentoExecutor = documentoExecutor;
	}

	public String getCnesHcpa() {
		return cnesHcpa;
	}

	public void setCnesHcpa(String cnesHcpa) {
		this.cnesHcpa = cnesHcpa;
	}

	public Long getCodProcedimento() {
		return codProcedimento;
	}

	public void setCodProcedimento(Long codProcedimento) {
		this.codProcedimento = codProcedimento;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public String getCompetenciaUti() {
		return competenciaUti;
	}

	public void setCompetenciaUti(String competenciaUti) {
		this.competenciaUti = competenciaUti;
	}

	public Integer getEaiSeq() {
		return eaiSeq;
	}

	public void setEaiSeq(Integer eaiSeq) {
		this.eaiSeq = eaiSeq;
	}

	public Integer getEaiCthSeq() {
		return eaiCthSeq;
	}

	public void setEaiCthSeq(Integer eaiCthSeq) {
		this.eaiCthSeq = eaiCthSeq;
	}

	public Byte getSeqp() {
		return seqp;
	}

	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}

	public String getCgc() {
		return cgc;
	}

	public void setCgc(String cgc) {
		this.cgc = cgc;
	}

	public Short getAmaIphPhoSeq() {
		return amaIphPhoSeq;
	}

	public void setAmaIphPhoSeq(Short amaIphPhoSeq) {
		this.amaIphPhoSeq = amaIphPhoSeq;
	}

	public Integer getAmaIphSeq() {
		return amaIphSeq;
	}

	public void setAmaIphSeq(Integer amaIphSeq) {
		this.amaIphSeq = amaIphSeq;
	}

	public String getServClass() {
		return servClass;
	}

	public void setServClass(String servClass) {
		this.servClass = servClass;
	}
}