package br.gov.mec.aghu.exames.vo;





public class RelatorioPacientesInternadosExamesRealizarDadosVO implements Comparable<RelatorioPacientesInternadosExamesRealizarDadosVO>{

	private String indUnidInternacao;
	private String unfDescricao;
	private String andarAlaDescricao;
	private String atdSeq;
	private String descMaterialAnalise;
	private String nroAmostras;
	private String descMaterialAnalise1;
	private String exaSigla;
	private String manSeq;
	private String descricaoUsual;
	private String criadoEm;
	private String seq1;
	private String descMaterialAnalise7;
	private String descMaterialAnalise2;
	private String descMaterialAnalise3;
	private String descMaterialAnalise6;
	private String descMaterialAnalise5;
	private String ranSeq;
	private String descMaterialAnalise8;
	private String soeSeq;
	private String seqp;
	private String hedDthrAgenda;
	
	private String recomendacoes;
	
	public String getIndUnidInternacao() {
		return indUnidInternacao;
	}
	public void setIndUnidInternacao(String indUnidInternacao) {
		this.indUnidInternacao = indUnidInternacao;
	}
	public String getUnfDescricao() {
		return unfDescricao;
	}
	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}
	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}
	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}
	public String getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(String atdSeq) {
		this.atdSeq = atdSeq;
	}
	public String getDescMaterialAnalise() {
		return descMaterialAnalise;
	}
	public void setDescMaterialAnalise(String descMaterialAnalise) {
		this.descMaterialAnalise = descMaterialAnalise;
	}
	public String getNroAmostras() {
		return nroAmostras;
	}
	public void setNroAmostras(String nroAmostras) {
		this.nroAmostras = nroAmostras;
	}
	public String getDescMaterialAnalise1() {
		return descMaterialAnalise1;
	}
	public void setDescMaterialAnalise1(String descMaterialAnalise1) {
		this.descMaterialAnalise1 = descMaterialAnalise1;
	}
	public String getExaSigla() {
		return exaSigla;
	}
	public void setExaSigla(String exaSigla) {
		this.exaSigla = exaSigla;
	}
	public String getManSeq() {
		return manSeq;
	}
	public void setManSeq(String manSeq) {
		this.manSeq = manSeq;
	}
	public String getDescricaoUsual() {
		return descricaoUsual;
	}
	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getSeq1() {
		return seq1;
	}
	public void setSeq1(String seq1) {
		this.seq1 = seq1;
	}
	public String getDescMaterialAnalise7() {
		return descMaterialAnalise7;
	}
	public void setDescMaterialAnalise7(String descMaterialAnalise7) {
		this.descMaterialAnalise7 = descMaterialAnalise7;
	}
	public String getDescMaterialAnalise2() {
		return descMaterialAnalise2;
	}
	public void setDescMaterialAnalise2(String descMaterialAnalise2) {
		this.descMaterialAnalise2 = descMaterialAnalise2;
	}
	public String getDescMaterialAnalise3() {
		return descMaterialAnalise3;
	}
	public void setDescMaterialAnalise3(String descMaterialAnalise3) {
		this.descMaterialAnalise3 = descMaterialAnalise3;
	}
	public String getDescMaterialAnalise6() {
		return descMaterialAnalise6;
	}
	public void setDescMaterialAnalise6(String descMaterialAnalise6) {
		this.descMaterialAnalise6 = descMaterialAnalise6;
	}
	public String getDescMaterialAnalise5() {
		return descMaterialAnalise5;
	}
	public void setDescMaterialAnalise5(String descMaterialAnalise5) {
		this.descMaterialAnalise5 = descMaterialAnalise5;
	}
	public String getRanSeq() {
		return ranSeq;
	}
	public void setRanSeq(String ranSeq) {
		this.ranSeq = ranSeq;
	}
	public String getDescMaterialAnalise8() {
		return descMaterialAnalise8;
	}
	public void setDescMaterialAnalise8(String descMaterialAnalise8) {
		this.descMaterialAnalise8 = descMaterialAnalise8;
	}
	public String getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(String soeSeq) {
		this.soeSeq = soeSeq;
	}
	public String getSeqp() {
		return seqp;
	}
	public void setSeqp(String seqp) {
		this.seqp = seqp;
	}
	public String getRecomendacoes() {
		return recomendacoes;
	}
	public void setRecomendacoes(String recomendacoes) {
		this.recomendacoes = recomendacoes;
	}
	public String getHedDthrAgenda() {
		return hedDthrAgenda;
	}
	public void setHedDthrAgenda(String hedDthrAgenda) {
		this.hedDthrAgenda = hedDthrAgenda;
	}
	
	@Override
	public int compareTo(RelatorioPacientesInternadosExamesRealizarDadosVO other) {
		// Compara leito
		int result = this.getDescMaterialAnalise().compareTo(other.getDescMaterialAnalise());
		if (result == 0) {
			if(this.getNroAmostras() != null && other.getNroAmostras() != null){
				// Compara prontuario
				result = this.getNroAmostras().compareTo(other.getNroAmostras());
				 if (result == 0) {
					 if(this.getDescMaterialAnalise2() != null && other.getDescMaterialAnalise2() != null){
						 // Compara nome do paciente
						 result = this.getDescMaterialAnalise2().compareTo(other.getDescMaterialAnalise2());
						 if (result == 0) {
							 if(this.getSeq1() != null && other.getSeq1() != null){
								// Compara solicitacao
								 result = this.getSeq1().compareTo(other.getSeq1());
							 }
						 }
					 }
				 }
			}
		}
		return result;
	}
		
}
