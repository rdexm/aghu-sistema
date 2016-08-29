package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class MedicamentosDialiseVO implements Serializable {

    private static final long serialVersionUID = 2361459614848370208L;

    private Integer cdcSeq;
    private Integer ccaSeq;
    private Integer phiSeq;
    private Integer matCodigo;
    private BigDecimal qtdePrevisto;
    private BigDecimal qtdeDebitado;
    private BigDecimal qtdeConsumido;
    private Integer cctCodigo;

    public MedicamentosDialiseVO(){   }
    
    public MedicamentosDialiseVO(Object[] objects) {
    	if (objects[0] != null) {
			this.setCdcSeq(Integer.valueOf(objects[0].toString()));
		}
    	if (objects[1] != null) {
			this.setCcaSeq(Integer.valueOf(objects[1].toString()));
		}
    	if (objects[2] != null) {
			this.setPhiSeq(Integer.valueOf(objects[2].toString()));
		}
    	if (objects[3] != null) {
			this.setMatCodigo(Integer.valueOf(objects[3].toString()));
		}
    	if (objects[4] != null) {
			this.setQtdePrevisto((BigDecimal)objects[4]);
		}
    	if (objects[5] != null) {
			this.setQtdeDebitado((BigDecimal)objects[5]);
		}
    	if (objects[6] != null) {
			this.setQtdeConsumido((BigDecimal)objects[6]);
		}
    	if (objects[7] != null) {
			this.setCctCodigo(Integer.valueOf(objects[7].toString()));
		}
	}

	public Integer getCdcSeq() {
        return cdcSeq;
    }

    public void setCdcSeq(Integer cdcSeq) {
        this.cdcSeq = cdcSeq;
    }

    public Integer getCcaSeq() {
        return ccaSeq;
    }

    public void setCcaSeq(Integer ccaSeq) {
        this.ccaSeq = ccaSeq;
    }

    public Integer getPhiSeq() {
        return phiSeq;
    }

    public void setPhiSeq(Integer phiSeq) {
        this.phiSeq = phiSeq;
    }

    public Integer getMatCodigo() {
        return matCodigo;
    }

    public void setMatCodigo(Integer matCodigo) {
        this.matCodigo = matCodigo;
    }

    public BigDecimal getQtdePrevisto() {
        return qtdePrevisto;
    }

    public void setQtdePrevisto(BigDecimal qtdePrevisto) {
        this.qtdePrevisto = qtdePrevisto;
    }

    public BigDecimal getQtdeDebitado() {
        return qtdeDebitado;
    }

    public void setQtdeDebitado(BigDecimal qtdeDebitado) {
        this.qtdeDebitado = qtdeDebitado;
    }

    public BigDecimal getQtdeConsumido() {
        return qtdeConsumido;
    }

    public void setQtdeConsumido(BigDecimal qtdeConsumido) {
        this.qtdeConsumido = qtdeConsumido;
    }

    public Integer getCctCodigo() {
        return cctCodigo;
    }

    public void setCctCodigo(Integer cctCodigo) {
        this.cctCodigo = cctCodigo;
    }
}
