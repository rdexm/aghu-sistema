package br.gov.mec.aghu.estoque.vo;


public class RelatorioMateriaisEstocaveisGrupoCurvaAbcVO implements Comparable<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO>{

	private String abc;
	private Integer grupo;
	private String codMat;
	private String nomeMaterial;
	private String prazo;
	private String pPddo;
	private String disp;
	private String nroSC;
	private String qtdSC;
	private String dtSC;
	private String cvte;
	private String nroAF;
	private String stAF;
	private String dtPrevEntrega;
	
	@Override
	public int compareTo(RelatorioMateriaisEstocaveisGrupoCurvaAbcVO other) {
		int result = this.getGrupo().compareTo(other.getGrupo());
		if (result == 0) {
			if(this.getAbc() != null && other.getAbc() != null){
				result = this.getAbc().compareTo(other.getAbc());
				 if (result == 0) {
					 if(this.getNomeMaterial() != null && other.getNomeMaterial() != null){
						 result = this.getNomeMaterial().compareTo(other.getNomeMaterial());						 
					 }
				 }
			}
		}
		return result;
	}
	
	public String getAbc() {
		return abc;
	}
	public void setAbc(String abc) {
		this.abc = abc;
	}
	public Integer getGrupo() {
		return grupo;
	}
	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}
	public String getCodMat() {
		return codMat;
	}
	public void setCodMat(String codMat) {
		this.codMat = codMat;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public String getPrazo() {
		return prazo;
	}
	public void setPrazo(String prazo) {
		this.prazo = prazo;
	}
	public String getpPddo() {
		return pPddo;
	}
	public void setpPddo(String pPddo) {
		this.pPddo = pPddo;
	}
	public String getDisp() {
		return disp;
	}
	public void setDisp(String disp) {
		this.disp = disp;
	}
	public String getNroSC() {
		return nroSC;
	}
	public void setNroSC(String nroSC) {
		this.nroSC = nroSC;
	}
	public String getQtdSC() {
		return qtdSC;
	}
	public void setQtdSC(String qtdSC) {
		this.qtdSC = qtdSC;
	}
	public String getDtSC() {
		return dtSC;
	}
	public void setDtSC(String dtSC) {
		this.dtSC = dtSC;
	}
	public String getCvte() {
		return cvte;
	}
	public void setCvte(String cvte) {
		this.cvte = cvte;
	}
	public String getNroAF() {
		return nroAF;
	}
	public void setNroAF(String nroAF) {
		this.nroAF = nroAF;
	}
	public String getStAF() {
		return stAF;
	}
	public void setStAF(String stAF) {
		this.stAF = stAF;
	}
	public String getDtPrevEntrega() {
		return dtPrevEntrega;
	}
	public void setDtPrevEntrega(String dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}
}
