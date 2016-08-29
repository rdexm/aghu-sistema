package br.gov.mec.aghu.sig.custos.vo;

public class InsumoAtividadeExcedenteVO {

	private Integer cnvCodigo;
	private Integer qtPrevista;
	private Double  vlrInsumo;

	public InsumoAtividadeExcedenteVO() {
	}

	public InsumoAtividadeExcedenteVO(Object[] obj) {

		if (obj[0] != null) {
			this.setCnvCodigo((Integer) obj[0]);
		}
		if (obj[1] != null) {
			this.setQtPrevista((Integer) obj[1]);
		}
		if (obj[2] != null) {
			this.setVlrInsumo((Double) obj[2]);
		}

	}

	/*
	 * public static InsumoAtividadeExcedenteVO get(Object[] obj) {
		InsumoAtividadeExcedenteVO insumoAtividadeObjetoCustoVO = new InsumoAtividadeExcedenteVO();
		if (obj[0] != null) {
			insumoAtividadeObjetoCustoVO.setCnvCodigo((Integer) obj[0]);
		}
		if (obj[1] != null) {
			insumoAtividadeObjetoCustoVO.setQtPrevista((Integer) obj[1]);
		}
		if (obj[2] != null) {
			insumoAtividadeObjetoCustoVO.setVlrInsumo((Double) obj[2]);
		}
		return insumoAtividadeObjetoCustoVO;
	}
	 */
	
	public Integer getQtPrevista() {
		return qtPrevista;
	}

	public void setQtPrevista(Integer qtPrevista) {
		this.qtPrevista = qtPrevista;
	}

	public Integer getCnvCodigo() {
		return cnvCodigo;
	}

	public void setCnvCodigo(Integer cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}

	public Double getVlrInsumo() {
		return vlrInsumo;
	}

	public void setVlrInsumo(Double vlrInsumo) {
		this.vlrInsumo = vlrInsumo;
	}

}
