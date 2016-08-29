package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

public class OrtesesProtesesesInternacaoVO implements java.io.Serializable {

	private static final long serialVersionUID = -1306788065227082249L;

	private Date dataUtilizacao;
	private Integer codigoCentroCusto;
	private Integer seqPhi;
	private Integer rmpSeq;
	private Integer matCodigo;
	
	public OrtesesProtesesesInternacaoVO(){	
	}

	public Date getDataUtilizacao() {
		return dataUtilizacao;
	}

	public void setDataUtilizacao(Date dataUtilizacao) {
		this.dataUtilizacao = dataUtilizacao;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public Integer getSeqPhi() {
		return seqPhi;
	}

	public void setSeqPhi(Integer seqPhi) {
		this.seqPhi = seqPhi;
	}

	public Integer getRmpSeq() {
		return rmpSeq;
	}

	public void setRmpSeq(Integer rmpSeq) {
		this.rmpSeq = rmpSeq;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public enum Fields {

		DATA_UTILIZACAO ("dataUtilizacao"),
		CODIGO_CENTRO_CUSTO("codigoCentroCusto"),
		SEQ_PHI("seqPhi"),
		RMP_SEQ("rmpSeq"),
		MAT_CODIGO("matCodigo");

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
