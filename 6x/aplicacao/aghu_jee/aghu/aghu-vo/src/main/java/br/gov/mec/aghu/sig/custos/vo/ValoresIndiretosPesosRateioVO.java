package br.gov.mec.aghu.sig.custos.vo;

public class ValoresIndiretosPesosRateioVO implements java.io.Serializable {

	private static final long serialVersionUID = -3845213605608508206L;

	private Integer cctCodigo;
	private Integer cbjSeq;
	private Double peso = 0D;

	public ValoresIndiretosPesosRateioVO (Object[] objects) {
		if (objects[0] != null) {
			this.setCctCodigo(Integer.parseInt(objects[0].toString()));
		}
		if (objects[1] != null) {
			this.setCbjSeq(Integer.parseInt(objects[1].toString()));
		}
		if (objects[2] != null) {
			this.setPeso(Double.parseDouble(objects[2].toString()));
		}
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getCbjSeq() {
		return cbjSeq;
	}

	public void setCbjSeq(Integer cbjSeq) {
		this.cbjSeq = cbjSeq;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}
}