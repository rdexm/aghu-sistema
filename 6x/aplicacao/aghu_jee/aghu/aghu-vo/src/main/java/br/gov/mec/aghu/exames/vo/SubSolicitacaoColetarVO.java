package br.gov.mec.aghu.exames.vo;

public class SubSolicitacaoColetarVO implements Comparable<SubSolicitacaoColetarVO>{

	private String dthrProgramadaOrd;
	private String ind;
	private String descricaoUsual;
	private String ufeUnfSeq;
	private String tipoColeta;
	
	public int compareTo(SubSolicitacaoColetarVO other) {
		int result = this.getDescricaoUsual().compareTo(other.getDescricaoUsual());
        return result;
	}

	public String getDthrProgramadaOrd() {
		return dthrProgramadaOrd;
	}

	public void setDthrProgramadaOrd(String dthrProgramadaOrd) {
		this.dthrProgramadaOrd = dthrProgramadaOrd;
	}

	public String getInd() {
		return ind;
	}

	public void setInd(String ind) {
		this.ind = ind;
	}

	public String getDescricaoUsual() {
		return descricaoUsual;
	}

	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}

	public String getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(String ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public String getTipoColeta() {
		return tipoColeta;
	}

	public void setTipoColeta(String tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	
	
	
}
