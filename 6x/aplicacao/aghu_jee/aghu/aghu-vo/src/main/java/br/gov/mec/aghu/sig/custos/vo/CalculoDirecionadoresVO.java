package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;



public class CalculoDirecionadoresVO implements java.io.Serializable {

	private static final long serialVersionUID = -2845213605608508307L;

	private Integer cbjSeq;
	private BigDecimal percentual;
	private Integer dirSeq;
	
	private BigDecimal vlrDirInsumos;
	private BigDecimal vlrDirPessoal;
	private BigDecimal vlrDirEquipamentos;
	private BigDecimal vlrDirServicos;
	
	public static CalculoDirecionadoresVO create(Object[] objects) {
		CalculoDirecionadoresVO vo = new CalculoDirecionadoresVO();
		
		if (objects[0] != null) {
			vo.setCbjSeq(Integer.parseInt(objects[0].toString()));
		}
		
		if (objects[1] != null) {
			vo.setPercentual(new BigDecimal((objects[1]).toString()));
		}
		
		if (objects[2] != null) {
			vo.setDirSeq(Integer.parseInt(objects[2].toString()));
		}
		
		if (objects[3] != null) {
			vo.setVlrDirInsumos(new BigDecimal((objects[3].toString())));
		}
		
		if (objects[4] != null) {
			vo.setVlrDirPessoal(new BigDecimal((objects[4].toString())));
		}
		
		if (objects[5] != null) {
			vo.setVlrDirEquipamentos(new BigDecimal((objects[5].toString())));
		}

		if (objects[6] != null) {
			vo.setVlrDirServicos(new BigDecimal((objects[6].toString())));
		}
		
		return vo;
	}

	public Integer getCbjSeq() {
		return cbjSeq;
	}

	public void setCbjSeq(Integer cbjSeq) {
		this.cbjSeq = cbjSeq;
	}

	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	public Integer getDirSeq() {
		return dirSeq;
	}

	public void setDirSeq(Integer dirSeq) {
		this.dirSeq = dirSeq;
	}

	public BigDecimal getVlrDirInsumos() {
		return vlrDirInsumos;
	}

	public void setVlrDirInsumos(BigDecimal vlrDirInsumos) {
		this.vlrDirInsumos = vlrDirInsumos;
	}

	public BigDecimal getVlrDirPessoal() {
		return vlrDirPessoal;
	}

	public void setVlrDirPessoal(BigDecimal vlrDirPessoal) {
		this.vlrDirPessoal = vlrDirPessoal;
	}

	public BigDecimal getVlrDirEquipamentos() {
		return vlrDirEquipamentos;
	}

	public void setVlrDirEquipamentos(BigDecimal vlrDirEquipamentos) {
		this.vlrDirEquipamentos = vlrDirEquipamentos;
	}

	public BigDecimal getVlrDirServicos() {
		return vlrDirServicos;
	}

	public void setVlrDirServicos(BigDecimal vlrDirServicos) {
		this.vlrDirServicos = vlrDirServicos;
	}
}