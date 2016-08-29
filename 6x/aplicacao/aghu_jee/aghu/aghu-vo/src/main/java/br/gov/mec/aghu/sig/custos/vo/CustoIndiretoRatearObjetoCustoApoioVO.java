package br.gov.mec.aghu.sig.custos.vo;

public class CustoIndiretoRatearObjetoCustoApoioVO implements java.io.Serializable{

	private static final long serialVersionUID = -2038011034405836497L;
	
	private Integer cctCodigo;
	private Integer cbjSeq;
	private Integer dirSeq;
	private Double valorIndInsumos = 0D;
	private Double valorIndPessoas = 0D;
	private Double valorIndEquipamentos = 0D;
	private Double valorIndServicos = 0D;
	
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
	
	public Double getValorIndInsumos() {
		return valorIndInsumos;
	}
	
	public void setValorIndInsumos(Double valorIndInsumos) {
		this.valorIndInsumos = valorIndInsumos;
	}
	
	public Double getValorIndPessoas() {
		return valorIndPessoas;
	}
	
	public void setValorIndPessoas(Double valorIndPessoas) {
		this.valorIndPessoas = valorIndPessoas;
	}
	
	public Double getValorIndEquipamentos() {
		return valorIndEquipamentos;
	}
	
	public void setValorIndEquipamentos(Double valorIndEquipamentos) {
		this.valorIndEquipamentos = valorIndEquipamentos;
	}
	
	public Double getValorIndServicos() {
		return valorIndServicos;
	}
	
	public void setValorIndServicos(Double valorIndServicos) {
		this.valorIndServicos = valorIndServicos;
	}
	
	public static CustoIndiretoRatearObjetoCustoApoioVO create(Object[] objects) {
		CustoIndiretoRatearObjetoCustoApoioVO vo = new CustoIndiretoRatearObjetoCustoApoioVO();
		
		if (objects[0] != null) {
			vo.setCbjSeq(Integer.parseInt(objects[0].toString()));
		}
		
		if (objects[1] != null) {
			vo.setCctCodigo(Integer.parseInt(objects[1].toString()));
		}
		
		if (objects[2] != null) {
			vo.setDirSeq(Integer.parseInt(objects[2].toString()));
		}
		
		if (objects[3] != null) {
			vo.setValorIndInsumos(Double.parseDouble(objects[3].toString()));
		}
		
		if (objects[4] != null) {
			vo.setValorIndPessoas(Double.parseDouble(objects[4].toString()));
		}
		
		if (objects[5] != null) {
			vo.setValorIndEquipamentos(Double.parseDouble(objects[5].toString()));
		}
		
		if (objects[6] != null) {
			vo.setValorIndServicos(Double.parseDouble(objects[6].toString()));
		}
		
		return vo;
	}
	
	public String getChave(){
		return this.getCctCodigo() + "-" + this.getCbjSeq() + "-" + this.getDirSeq();
	}
}
