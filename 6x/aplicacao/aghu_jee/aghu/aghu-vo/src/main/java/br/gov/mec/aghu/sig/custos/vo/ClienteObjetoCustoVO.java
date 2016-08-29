package br.gov.mec.aghu.sig.custos.vo;

public class ClienteObjetoCustoVO implements java.io.Serializable {

	private static final long serialVersionUID = -1845214605607508307L;

	private Integer cbjSeq;
	private Integer ocvSeq;
	private Integer dirSeq;

	private Boolean indTodosCct;
	private Integer ctoSeq;
	private Integer cctCodigo;
	private Double valor;

	public static ClienteObjetoCustoVO create(Object[] objects) {

		ClienteObjetoCustoVO vo = new ClienteObjetoCustoVO();
		
		if (objects[0] != null) {
			vo.setCbjSeq(Integer.parseInt(objects[0].toString())); 
		}
		
		if (objects[1] != null) {
			vo.setOcvSeq(Integer.parseInt(objects[1].toString())); 
		}
		
		if (objects[2] != null) {
			vo.setDirSeq(Integer.parseInt(objects[2].toString())); 
		}
		
		if (objects[3] != null) {
			vo.setIndTodosCct(Boolean.parseBoolean(objects[3].toString())); 
		}
		
		if (objects[4] != null) {
			vo.setCtoSeq(Integer.parseInt(objects[4].toString())); 
		}
		
		if (objects[5] != null) {
			vo.setCctCodigo(Integer.parseInt(objects[5].toString()));
		}
		
		if (objects[6] != null) {
			vo.setValor(Double.parseDouble(objects[6].toString())); 
		}
		
		return vo;
	}

	public Integer getCbjSeq() {
		return cbjSeq;
	}

	public void setCbjSeq(Integer cbjSeq) {
		this.cbjSeq = cbjSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public Integer getDirSeq() {
		return dirSeq;
	}

	public void setDirSeq(Integer dirSeq) {
		this.dirSeq = dirSeq;
	}

	public Boolean getIndTodosCct() {
		return indTodosCct;
	}

	public void setIndTodosCct(Boolean indTodosCct) {
		this.indTodosCct = indTodosCct;
	}

	public Integer getCtoSeq() {
		return ctoSeq;
	}

	public void setCtoSeq(Integer ctoSeq) {
		this.ctoSeq = ctoSeq;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

}