package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class ServicoAtividadeObjetoCustoVO {

	private Integer tvdSeq;
	private Integer arvSeq;
	private Integer cmtSeq;
	private Integer cctCodigo;
	private Integer afcoSeq;
	private Integer afnNumero;
	private Integer dirSeqAtividade;
	private BigDecimal pesoOc;
	private Integer srvCodigo;

	public ServicoAtividadeObjetoCustoVO(){	
	}
	
	public ServicoAtividadeObjetoCustoVO(Object[] obj) {
		if (obj[0] != null) {
			this.setTvdSeq((Integer) obj[0]);
		}

		if (obj[1] != null) {
			this.setArvSeq((Integer) obj[1]);
		}

		if (obj[2] != null) {
			this.setCmtSeq((Integer) obj[2]);
		}

		if (obj[3] != null) {
			this.setCctCodigo((Integer) obj[3]);
		}

		if (obj[4] != null) {
			this.setAfcoSeq((Integer) obj[4]);
		}
		
		if (obj[5] != null) {
			this.setAfnNumero((Integer) obj[5]);
		}
		
		if (obj[6] != null) {
			this.setSrvCodigo((Integer) obj[6]);
		}

		if (obj[7] != null) {
			this.setDirSeqAtividade((Integer) obj[7]);
		}

		if (obj[8] != null) {
			this.setPesoOc((BigDecimal) obj[8]);
		}
	}

	public ServicoAtividadeObjetoCustoVO(ServicoAtividadeObjetoCustoVO vo) {
		this.setTvdSeq(vo.getTvdSeq());
		this.setArvSeq(vo.getArvSeq());
		this.setCmtSeq(vo.getCmtSeq());
		this.setCctCodigo(vo.getCctCodigo());
		this.setAfcoSeq(vo.getAfcoSeq());
		this.setDirSeqAtividade(vo.getDirSeqAtividade());
		this.setPesoOc(vo.getPesoOc());
		this.setSrvCodigo(vo.getSrvCodigo());
	}

	public Integer getTvdSeq() {
		return tvdSeq;
	}

	public void setTvdSeq(Integer tvdSeq) {
		this.tvdSeq = tvdSeq;
	}

	public Integer getCmtSeq() {
		return cmtSeq;
	}

	public void setCmtSeq(Integer cmtSeq) {
		this.cmtSeq = cmtSeq;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getDirSeqAtividade() {
		return dirSeqAtividade;
	}

	public void setDirSeqAtividade(Integer dirSeqAtividade) {
		this.dirSeqAtividade = dirSeqAtividade;
	}

	public BigDecimal getPesoOc() {
		return pesoOc;
	}

	public void setPesoOc(BigDecimal pesoOc) {
		this.pesoOc = pesoOc;
	}

	public Integer getAfcoSeq() {
		return afcoSeq;
	}

	public void setAfcoSeq(Integer afcoSeq) {
		this.afcoSeq = afcoSeq;
	}

	public Integer getArvSeq() {
		return arvSeq;
	}

	public void setArvSeq(Integer arvSeq) {
		this.arvSeq = arvSeq;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getSrvCodigo() {
		return srvCodigo;
	}

	public void setSrvCodigo(Integer srvCodigo) {
		this.srvCodigo = srvCodigo;
	}
}
