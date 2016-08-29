package br.gov.mec.aghu.exames.vo;




public class ClienteNfeVO {

   private String nomCli;	
   private String apeCli;
   private String documento;
   private String cliCon;
   private String codCpg;
   private Integer codEmp;
   private Integer codFil; 
   private Integer codRep; 
   private Integer ctaRed;
   private String cepCli;
   private String endCli;
   private String baiCli;
   private String intNet;
   private String emaNfe;
   private String nenCli;
   private String cplEnd;
   private String sigUfs;
   private String sitCli;
   private String tipCli;
   private String tipMer;
   private String codPai;
   private String cidCli;
   

   public ClienteNfeVO(){
	   this.setCliCon("N");
	   this.setSitCli("A");
	   this.setCodCpg("AV");
	   this.setCodEmp(1);
	   this.setCodFil(1);
	   this.setCodRep(1);
   }


   
	public String getNomCli() {
		return nomCli;
	}
	public void setNomCli(String nomCli) {
		this.nomCli = nomCli;
	}
	public String getApeCli() {
		return apeCli;
	}
	public void setApeCli(String apeCli) {
		this.apeCli = apeCli;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getCliCon() {
		return cliCon;
	}
	public void setCliCon(String cliCon) {
		this.cliCon = cliCon;
	}
	public String getCodCpg() {
		return codCpg;
	}
	public void setCodCpg(String codCpg) {
		this.codCpg = codCpg;
	}
	public Integer getCodEmp() {
		return codEmp;
	}
	public void setCodEmp(Integer codEmp) {
		this.codEmp = codEmp;
	}
	public Integer getCodFil() {
		return codFil;
	}
	public void setCodFil(Integer codFil) {
		this.codFil = codFil;
	}
	public Integer getCodRep() {
		return codRep;
	}
	public void setCodRep(Integer codRep) {
		this.codRep = codRep;
	}
	public Integer getCtaRed() {
		return ctaRed;
	}
	public void setCtaRed(Integer ctaRed) {
		this.ctaRed = ctaRed;
	}
	public String getCepCli() {
		return cepCli;
	}
	public void setCepCli(String cepCli) {
		this.cepCli = cepCli;
	}
	public String getEndCli() {
		return endCli;
	}
	public void setEndCli(String endCli) {
		this.endCli = endCli;
	}
	public String getBaiCli() {
		return baiCli;
	}
	public void setBaiCli(String baiCli) {
		this.baiCli = baiCli;
	}
	public String getIntNet() {
		return intNet;
	}
	public void setIntNet(String intNet) {
		this.intNet = intNet;
	}
	public String getEmaNfe() {
		return emaNfe;
	}
	public void setEmaNfe(String emaNfe) {
		this.emaNfe = emaNfe;
	}
	public String getNenCli() {
		return nenCli;
	}
	public void setNenCli(String nenCli) {
		this.nenCli = nenCli;
	}
	public String getCplEnd() {
		return cplEnd;
	}
	public void setCplEnd(String cplEnd) {
		this.cplEnd = cplEnd;
	}
	public String getSigUfs() {
		return sigUfs;
	}
	public void setSigUfs(String sigUfs) {
		this.sigUfs = sigUfs;
	}
	public String getSitCli() {
		return sitCli;
	}
	public void setSitCli(String sitCli) {
		this.sitCli = sitCli;
	}
	public String getTipCli() {
		return tipCli;
	}
	public void setTipCli(String tipCli) {
		this.tipCli = tipCli;
	}
	public String getTipMer() {
		return tipMer;
	}
	public void setTipMer(String tipMer) {
		this.tipMer = tipMer;
	}
	public String getCodPai() {
		return codPai;
	}
	public void setCodPai(String codPai) {
		this.codPai = codPai;
	}



	public String getCidCli() {
		return cidCli;
	}



	public void setCidCli(String cidCli) {
		this.cidCli = cidCli;
	}
	
}
