package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class BuscarProcedHospEquivalentePhiVO implements Serializable{
	private static final String NULL = "null";
	private static final long serialVersionUID = 2059611363830887749L;
	
	public static String getKeyMapBuscaProcedHospEquivalentePhi(Integer phiSeq, Short qtdRealizada, Short cnvCodigo, Byte cnvCspSeq, Short grcSus) { //NOPMD
		String p1 = phiSeq != null ? phiSeq.toString() : NULL;
		String p2 = qtdRealizada != null ? qtdRealizada.toString() : NULL;
		String p3 = cnvCodigo != null ? cnvCodigo.toString() : NULL;
		String p4 = cnvCspSeq != null ? cnvCspSeq.toString() : NULL;
		String p5 = grcSus != null ? grcSus.toString() : NULL;
		
		return p1 + "_" + p2 + "_" + p3 + "_" + p4 + "_" + p5;
	}
	
	private Short pho;
	private Integer seq;
	private Short qtd;
	private String grp;
	private Integer phiSeq;
	
	public BuscarProcedHospEquivalentePhiVO(){
		
	}
			
	
	public BuscarProcedHospEquivalentePhiVO(Short pho, Short qtd, Integer seq) {
		this.pho= pho;		
		this.qtd= qtd;
		this.seq = seq;
	}


	public Short getPho() {
		return pho;
	}


	public Integer getSeq() {
		return seq;
	}


	public Short getQtd() {
		return qtd;
	}


	public String getGrp() {
		return grp;
	}


	public void setPho(Short pho) {
		this.pho = pho;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public void setQtd(Short qtd) {
		this.qtd = qtd;
	}


	public void setGrp(String grp) {
		this.grp = grp;
	}

	@Override
	public String toString() {
		return "BuscarProcedHospEquivalentePhiVO ["
				+ (pho != null ? "pho=" + pho + ", " : "")
				+ (seq != null ? "seq=" + seq + ", " : "")
				+ (qtd != null ? "qtd=" + qtd + ", " : "")
				+ (grp != null ? "grp=" + grp : "") + "]";
	}


	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}


	public Integer getPhiSeq() {
		return phiSeq;
	}
	
}
