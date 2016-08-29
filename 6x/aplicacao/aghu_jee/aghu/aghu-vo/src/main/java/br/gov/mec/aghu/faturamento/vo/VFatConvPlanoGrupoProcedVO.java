package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

import java.io.Serializable;

public class VFatConvPlanoGrupoProcedVO implements Serializable {

    private static final long serialVersionUID = -960904779309072253L;
    private Short cphCspCnvCodigo;
	private Byte cphCspSeq;
	private Short cphPhoSeq;
	private Short grcSeq;
	private String cspDescricao;
	private String cnvDescricao;
	private String phoDescricao;
	private String grcDescricao;
	private Integer iphSeq;
	private Short iphPhoSeq;
	private Integer phiSeq;						
		

	private DominioOperacoesJournal operacao;

	private FatItensProcedHospitalar fatItensProcedHospitalar;

	public VFatConvPlanoGrupoProcedVO() {
	}

	public VFatConvPlanoGrupoProcedVO(Short cphCspCnvCodigo, Byte cphCspSeq,
			Short cphPhoSeq, Short grcSeq, String cspDescricao,
			String cnvDescricao, String phoDescricao, String grcDescricao) {
		this.cphCspCnvCodigo = cphCspCnvCodigo;
		this.cphCspSeq = cphCspSeq;
		this.cphPhoSeq = cphPhoSeq;
		this.grcSeq = grcSeq;
		this.cspDescricao = cspDescricao;
		this.cnvDescricao = cnvDescricao;
		this.phoDescricao = phoDescricao;
		this.grcDescricao = grcDescricao;
	}

	public Short getCphCspCnvCodigo() {
		return cphCspCnvCodigo;
	}

	public void setCphCspCnvCodigo(Short cphCspCnvCodigo) {
		this.cphCspCnvCodigo = cphCspCnvCodigo;
	}

	public Byte getCphCspSeq() {
		return cphCspSeq;
	}

	public void setCphCspSeq(Byte cphCspSeq) {
		this.cphCspSeq = cphCspSeq;
	}

	public Short getCphPhoSeq() {
		return cphPhoSeq;
	}

	public void setCphPhoSeq(Short cphPhoSeq) {
		this.cphPhoSeq = cphPhoSeq;
	}

	public Short getGrcSeq() {
		return grcSeq;
	}

	public void setGrcSeq(Short grcSeq) {
		this.grcSeq = grcSeq;
	}

	public String getCspDescricao() {
		return cspDescricao;
	}

	public void setCspDescricao(String cspDescricao) {
		this.cspDescricao = cspDescricao;
	}

	public String getCnvDescricao() {
		return cnvDescricao;
	}

	public void setCnvDescricao(String cnvDescricao) {
		this.cnvDescricao = cnvDescricao;
	}

	public String getPhoDescricao() {
		return phoDescricao;
	}

	public void setPhoDescricao(String phoDescricao) {
		this.phoDescricao = phoDescricao;
	}

	public String getGrcDescricao() {
		return grcDescricao;
	}

	public void setGrcDescricao(String grcDescricao) {
		this.grcDescricao = grcDescricao;
	}

	public void setFatItensProcedHospitalar(
			FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
	}

	public FatItensProcedHospitalar getFatItensProcedHospitalar() {
		return fatItensProcedHospitalar;
	}
	
	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}


	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public enum Fields {
		CPH_CSP_CNV_CODIGO("cphCspCnvCodigo"),
		CPH_CSP_SEQ("cphCspSeq"),
		CPH_PHO_SEQ("cphPhoSeq"),
		GRC_SEQ("grcSeq"),
		CSP_DESCRICAO("cspDescricao"),
		CNV_DESCRICAO("cnvDescricao"),
		PHO_DESCRICAO("phoDescricao"),
		GRC_DESCRICAO("grcDescricao"),
		IPH_SEQ("iphSeq"),
		IPH_PHO_SEQ("iphPhoSeq"),
		PHI_SEQ("phiSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cnvDescricao == null) ? 0 : cnvDescricao.hashCode());
		result = prime * result
				+ ((cphCspCnvCodigo == null) ? 0 : cphCspCnvCodigo.hashCode());
		result = prime * result
				+ ((cphCspSeq == null) ? 0 : cphCspSeq.hashCode());
		result = prime * result
				+ ((cphPhoSeq == null) ? 0 : cphPhoSeq.hashCode());
		result = prime * result
				+ ((cspDescricao == null) ? 0 : cspDescricao.hashCode());
		result = prime * result
				+ ((grcDescricao == null) ? 0 : grcDescricao.hashCode());
		result = prime * result + ((grcSeq == null) ? 0 : grcSeq.hashCode());
		result = prime * result
				+ ((phoDescricao == null) ? 0 : phoDescricao.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
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
		VFatConvPlanoGrupoProcedVO other = (VFatConvPlanoGrupoProcedVO) obj;
		if (cnvDescricao == null) {
			if (other.cnvDescricao != null) {
				return false;
			}
		} else if (!cnvDescricao.equals(other.cnvDescricao)) {
			return false;
		}
		if (cphCspCnvCodigo == null) {
			if (other.cphCspCnvCodigo != null) {
				return false;
			}
		} else if (!cphCspCnvCodigo.equals(other.cphCspCnvCodigo)) {
			return false;
		}
		if (cphCspSeq == null) {
			if (other.cphCspSeq != null) {
				return false;
			}
		} else if (!cphCspSeq.equals(other.cphCspSeq)) {
			return false;
		}
		if (cphPhoSeq == null) {
			if (other.cphPhoSeq != null) {
				return false;
			}
		} else if (!cphPhoSeq.equals(other.cphPhoSeq)) {
			return false;
		}
		if (cspDescricao == null) {
			if (other.cspDescricao != null) {
				return false;
			}
		} else if (!cspDescricao.equals(other.cspDescricao)) {
			return false;
		}
		if (grcDescricao == null) {
			if (other.grcDescricao != null) {
				return false;
			}
		} else if (!grcDescricao.equals(other.grcDescricao)) {
			return false;
		}
		if (grcSeq == null) {
			if (other.grcSeq != null) {
				return false;
			}
		} else if (!grcSeq.equals(other.grcSeq)) {
			return false;
		}
		if (fatItensProcedHospitalar == null) {
			if (other.fatItensProcedHospitalar != null){
				return false;
			}
		} else if (!fatItensProcedHospitalar.equals(other.fatItensProcedHospitalar)) {
			return false;
		}
		return true;
	}	
}
