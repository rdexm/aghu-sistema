package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class AfaMedicamentoPrescricaoVO implements Serializable {

	

    /**
	 * 
	 */
	private static final long serialVersionUID = -8207049101534747901L;
	private Integer asuApaAtdSeq;
    private Integer asuApaSeq;
    private Short asuSeqp;
    private Integer seqp;
    private Integer medMatCodigo;
    private String descricao;

    public AfaMedicamentoPrescricaoVO() {}

   


    public Integer getAsuApaAtdSeq() {
        return this.asuApaAtdSeq;
    }




    public void setAsuApaAtdSeq(Integer asuApaAtdSeq) {
        this.asuApaAtdSeq = asuApaAtdSeq;
    }




    public Integer getMedMatCodigo() {
        return this.medMatCodigo;
    }



    public void setMedMatCodigo(Integer medMatCodigo) {
        this.medMatCodigo = medMatCodigo;
    }



    public String getDescricao() {
        return this.descricao;
    }



    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getAsuApaSeq() {
        return this.asuApaSeq;
    }

    public void setAsuApaSeq(Integer asuApaSeq) {
        this.asuApaSeq = asuApaSeq;
    }

    public Short getAsuSeqp() {
        return this.asuSeqp;
    }

    public void setAsuSeqp(Short asuSeqp) {
        this.asuSeqp = asuSeqp;
    }

    public Integer getSeqp() {
        return this.seqp;
    }

    public void setSeqp(Integer seqp) {
        this.seqp = seqp;
    }
    
    public String getUpperDescricao() {
		return StringUtils.isNotBlank(this.descricao) ? this.descricao.toUpperCase() : this.descricao;
	}


	@Override
	@SuppressWarnings("PMD.NPathComplexity")
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((this.asuApaAtdSeq == null) ? 0 : this.asuApaAtdSeq.hashCode());
	result = prime * result
		+ ((this.asuApaSeq == null) ? 0 : this.asuApaSeq.hashCode());
	result = prime * result + ((this.asuSeqp == null) ? 0 : this.asuSeqp.hashCode());
	result = prime * result
		+ ((this.descricao == null) ? 0 : this.descricao.hashCode());
	result = prime * result
		+ ((this.medMatCodigo == null) ? 0 : this.medMatCodigo.hashCode());
	result = prime * result + ((this.seqp == null) ? 0 : this.seqp.hashCode());
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
	if (!(obj instanceof AfaMedicamentoPrescricaoVO)) {
	    return false;
	}
	AfaMedicamentoPrescricaoVO other = (AfaMedicamentoPrescricaoVO) obj;
	if (this.asuApaAtdSeq == null) {
	    if (other.asuApaAtdSeq != null) {
	    	return false;
	    }
	} else if (!this.asuApaAtdSeq.equals(other.asuApaAtdSeq)) {
	    return false;
	}
	if (this.asuApaSeq == null) {
	    if (other.asuApaSeq != null) {
		return false;
	    }
	} else if (!this.asuApaSeq.equals(other.asuApaSeq)) {
	    return false;
	}
	if (this.asuSeqp == null) {
	    if (other.asuSeqp != null) {
		return false;
	    }
	} else if (!this.asuSeqp.equals(other.asuSeqp)) {
	    return false;
	}
	if (this.descricao == null) {
	    if (other.descricao != null) {
		return false;
	    }
	} else if (!this.descricao.equals(other.descricao)) {
	    return false;
	}
	if (this.medMatCodigo == null) {
	    if (other.medMatCodigo != null) {
		return false;
	    }
	} else if (!this.medMatCodigo.equals(other.medMatCodigo)) {
	    return false;
	}
	if (this.seqp == null) {
	    if (other.seqp != null) {
		return false;
	    }
	} else if (!this.seqp.equals(other.seqp)) {
	    return false;
	}
	return true;
    }

    
    



}
