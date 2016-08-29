package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioIndProcessadoRecProcCid;


public class FatTabProcCidVO implements Serializable {
	private static final long serialVersionUID = -1973417485581133519L;

	private Integer phiSeq;
	private DominioIndProcessadoRecProcCid processado;
	private String cidsValidos;
	private String cid;

	public FatTabProcCidVO() {
	}

	public FatTabProcCidVO(final Integer phiSeq, final DominioIndProcessadoRecProcCid processado) {
		this.phiSeq = phiSeq;
		this.processado = processado;
	}

	public FatTabProcCidVO(final Integer phiSeq, final DominioIndProcessadoRecProcCid processado, final String cidsValidos) {
		this.phiSeq = phiSeq;
		this.processado = processado;
		this.cidsValidos = cidsValidos;
	}
	
	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}	
	
	public Integer getPhiseq() {
		return phiSeq;
	}

	public void setPhiseq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public DominioIndProcessadoRecProcCid getProcessado() {
		return processado;
	}

	public void setProcessado(DominioIndProcessadoRecProcCid processado) {
		this.processado = processado;
	}

	public String getCidsValidos() {
		return cidsValidos;
	}

	public void setCidsValidos(String cidsValidos) {
		this.cidsValidos = cidsValidos;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCid() {
		return cid;
	}

	@Override
	public int hashCode() {
		final int prime = 32452843;
		return prime * 1 + ((phiSeq == null) ? 0 : phiSeq.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FatTabProcCidVO other = (FatTabProcCidVO) obj;

		if (phiSeq == null) {
			if (other.phiSeq != null) {
				return false;
			}
		} else if (!phiSeq.equals(other.phiSeq)) {
			return false;
		}
		return true;
	}


}
