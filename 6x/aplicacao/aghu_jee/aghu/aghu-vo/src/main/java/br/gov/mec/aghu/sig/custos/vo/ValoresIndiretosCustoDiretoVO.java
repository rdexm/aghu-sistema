package br.gov.mec.aghu.sig.custos.vo;

public class ValoresIndiretosCustoDiretoVO implements java.io.Serializable {

	private static final long serialVersionUID = -3845213605608508206L;

	private Integer cbjSeq;
	private Integer dirSeq;
	private Integer cctCodigo;
	private Double valorInsumos = 0D;
	private Double valorPessoas = 0D;
	private Double valorEquipamentos = 0D;
	private Double valorServicos = 0D;

	public static ValoresIndiretosCustoDiretoVO create(Object[] objects) {
		ValoresIndiretosCustoDiretoVO vo = new ValoresIndiretosCustoDiretoVO();

		if (objects[0] != null) {
			vo.setCbjSeq(Integer.parseInt(objects[0].toString()));
		}
		if (objects[1] != null) {
			vo.setDirSeq(Integer.parseInt(objects[1].toString()));
		}
		if (objects[2] != null) {
			vo.setCctCodigo(Integer.parseInt(objects[2].toString()));
		}		
		if (objects[3] != null) {
			vo.setValorInsumos(Double.parseDouble(objects[3].toString()));
		}
		if (objects[4] != null) {
			vo.setValorPessoas(Double.parseDouble(objects[4].toString()));
		}
		if (objects[5] != null) {
			vo.setValorEquipamentos(Double.parseDouble(objects[5].toString()));
		}
		if (objects[6] != null) {
			vo.setValorServicos(Double.parseDouble(objects[6].toString()));
		}
		return vo;
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

	public Integer getDirSeq() {
		return dirSeq;
	}

	public void setDirSeq(Integer dirSeq) {
		this.dirSeq = dirSeq;
	}

	public Double getValorServicos() {
		return valorServicos;
	}

	public void setValorServicos(Double valorServicos) {
		this.valorServicos = valorServicos;
	}

	public Double getValorEquipamentos() {
		return valorEquipamentos;
	}

	public void setValorEquipamentos(Double valorEquipamentos) {
		this.valorEquipamentos = valorEquipamentos;
	}

	public Double getValorPessoas() {
		return valorPessoas;
	}

	public void setValorPessoas(Double valorPessoas) {
		this.valorPessoas = valorPessoas;
	}

	public Double getValorInsumos() {
		return valorInsumos;
	}

	public void setValorInsumos(Double valorInsumos) {
		this.valorInsumos = valorInsumos;
	}
}