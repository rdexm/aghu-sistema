package br.gov.mec.aghu.faturamento.vo;


/**
 * @author eschweigert
 */
public class CursorIchPhiMatVO {
	
	private Short seq;
	private Integer phiSeq;
	private Integer ipsRmpSeq;
	private Short ipsNumero;

	public enum Fields {

		SEQ("seq"),
		PHI_SEQ("phiSeq"),
		IPS_RMP_SEQ("ipsRmpSeq"),
		IPS_NUMERO("ipsNumero");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Short getSeq() {
		return seq;
	}


	public void setSeq(Short seq) {
		this.seq = seq;
	}


	public Integer getPhiSeq() {
		return phiSeq;
	}


	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}


	public Integer getIpsRmpSeq() {
		return ipsRmpSeq;
	}


	public void setIpsRmpSeq(Integer ipsRmpSeq) {
		this.ipsRmpSeq = ipsRmpSeq;
	}


	public Short getIpsNumero() {
		return ipsNumero;
	}


	public void setIpsNumero(Short ipsNumero) {
		this.ipsNumero = ipsNumero;
	}


	@Override
	public String toString() {
		return "CursorIchPhiMatVO [" + (seq != null ? "seq=" + seq + ", " : "")
				+ (phiSeq != null ? "phiSeq=" + phiSeq + ", " : "")
				+ (ipsRmpSeq != null ? "ipsRmpSeq=" + ipsRmpSeq + ", " : "")
				+ (ipsNumero != null ? "ipsNumero=" + ipsNumero : "") + "]";
	}
	
}