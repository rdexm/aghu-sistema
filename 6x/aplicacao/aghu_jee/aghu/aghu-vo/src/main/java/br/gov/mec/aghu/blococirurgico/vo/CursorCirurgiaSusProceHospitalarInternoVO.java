package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class CursorCirurgiaSusProceHospitalarInternoVO implements Serializable {

	private static final long serialVersionUID = -5779745123347124088L;
	private Integer phiSeq;

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public enum Fields {
		PHI_SEQ("phiSeq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public String toString() {
		return "CursorCirurgiaSusProceHospitalarInternoVO [phiSeq=" + phiSeq + "]";
	}
}
