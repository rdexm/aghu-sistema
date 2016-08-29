package br.gov.mec.aghu.sig.custos.vo;

public class InsumoAtividadeMaterialExcedenteVO {

	private Integer cctCodigo;
	private Integer matCodigo;
	private Integer qtPrevista;
	private Integer qtDisponivel;

	public InsumoAtividadeMaterialExcedenteVO() {
	}

	public InsumoAtividadeMaterialExcedenteVO(Object[] obj) {
		
		if (obj[0] != null) {
			this.setCctCodigo((Integer) obj[0]);
		}
		if (obj[1] != null) {
			this.setMatCodigo((Integer) obj[1]);
		}
		if (obj[2] != null) {
			this.setQtDisponivel((Integer) obj[2]);
		}
		if (obj[3] != null) {
			this.setQtPrevista((Integer) obj[3]);
		}
		
	}

	public Integer getQtDisponivel() {
		return qtDisponivel;
	}

	public void setQtDisponivel(Integer qtDisponivel) {
		this.qtDisponivel = qtDisponivel;
	}

	public Integer getQtPrevista() {
		return qtPrevista;
	}

	public void setQtPrevista(Integer qtPrevista) {
		this.qtPrevista = qtPrevista;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

}
