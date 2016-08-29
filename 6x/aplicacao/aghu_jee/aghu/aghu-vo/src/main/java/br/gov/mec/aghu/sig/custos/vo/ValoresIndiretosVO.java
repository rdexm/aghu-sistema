package br.gov.mec.aghu.sig.custos.vo;


public class ValoresIndiretosVO implements java.io.Serializable {

	private static final long serialVersionUID = -3845213605608508206L;

	private Integer cctCodigo;
	private Integer cbjSeq;
	private Double valorIndInsumos = 0D;
	private Double valorIndPessoas = 0D;
	private Double valorIndEquipamentos = 0D;
	private Double valorIndServicos = 0D;
	

	public ValoresIndiretosVO(Object[] objects) {
	
		if (objects[0] != null) {
			this.setCctCodigo(Integer.parseInt(objects[0].toString()));
		}
		if (objects[1] != null) {
			this.setCbjSeq(Integer.parseInt(objects[1].toString()));
		}
		if (objects[2] != null) {
			this.setValorIndInsumos(Double.parseDouble(objects[2].toString()));
		}
		if (objects[3] != null) {
			this.setValorIndPessoas(Double.parseDouble(objects[3].toString()));
		}
		if (objects[4] != null) {
			this.setValorIndEquipamentos(Double.parseDouble(objects[4].toString()));
		}
		if (objects[5] != null) {
			this.setValorIndServicos(Double.parseDouble(objects[5].toString()));
		}
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Double getValorIndServicos() {
		return valorIndServicos;
	}

	public void setValorIndServicos(Double valorIndServicos) {
		this.valorIndServicos = valorIndServicos;
	}

	public Double getValorIndEquipamentos() {
		return valorIndEquipamentos;
	}

	public void setValorIndEquipamentos(Double valorIndEquipamentos) {
		this.valorIndEquipamentos = valorIndEquipamentos;
	}

	public Double getValorIndPessoas() {
		return valorIndPessoas;
	}

	public void setValorIndPessoas(Double valorIndPessoas) {
		this.valorIndPessoas = valorIndPessoas;
	}

	public Double getValorIndInsumos() {
		return valorIndInsumos;
	}

	public void setValorIndInsumos(Double valorIndInsumos) {
		this.valorIndInsumos = valorIndInsumos;
	}

	public Integer getCbjSeq() {
		return cbjSeq;
	}

	public void setCbjSeq(Integer cbjSeq) {
		this.cbjSeq = cbjSeq;
	}
}