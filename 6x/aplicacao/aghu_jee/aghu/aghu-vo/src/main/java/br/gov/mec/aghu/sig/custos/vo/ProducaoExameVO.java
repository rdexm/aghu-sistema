package br.gov.mec.aghu.sig.custos.vo;

public class ProducaoExameVO {

	private Integer cctCodigo;
	private Integer phiSeq;
	private String origem;
	private Integer nroDiasProducao;
	private Integer qtdExames;

	public ProducaoExameVO() {
	}

	public static ProducaoExameVO create(Object[] object) {
		ProducaoExameVO producaoExameVO = new ProducaoExameVO();

		if (object[0] != null) {
			producaoExameVO.setCctCodigo(Integer.valueOf(object[0].toString()));
		}
		if (object[1] != null) {
			producaoExameVO.setPhiSeq(Integer.valueOf(object[1].toString()));
		}
		if (object[2] != null) {
			producaoExameVO.setOrigem(object[2].toString());
		}
		if (object[3] != null) {
			producaoExameVO.setNroDiasProducao(Integer.valueOf(object[3].toString()));
		}
		if (object[4] != null) {
			producaoExameVO.setQtdExames(Integer.valueOf(object[4].toString()));
		}

		return producaoExameVO;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Integer getNroDiasProducao() {
		return nroDiasProducao;
	}

	public void setNroDiasProducao(Integer nroDiasProducao) {
		this.nroDiasProducao = nroDiasProducao;
	}

	public Integer getQtdExames() {
		return qtdExames;
	}

	public void setQtdExames(Integer qtdExames) {
		this.qtdExames = qtdExames;
	}

}
