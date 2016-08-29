package br.gov.mec.aghu.faturamento.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ContaNaoReapresentadaCPFVO implements BaseBean {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6667289856320332250L;
	private Integer eaiCthSeq;
	private Long cpfCns;
	private Boolean indContaReapresentada;
	private Long iphCodSus;
	
	public ContaNaoReapresentadaCPFVO () {}
	
	public ContaNaoReapresentadaCPFVO(Integer eaiCthSeq, Long cpfCns,
			Boolean indContaReapresentada, Long iphCodSus) {
		super();
		this.eaiCthSeq = eaiCthSeq;
		this.cpfCns = cpfCns;
		this.indContaReapresentada = nvlContaReapresentada(indContaReapresentada);
		this.iphCodSus = iphCodSus;
	}

	public Integer getEaiCthSeq() {
		return eaiCthSeq;
	}
	public Long getCpfCns() {
		return cpfCns;
	}
	public Boolean getIndContaReapresentada() {
		return indContaReapresentada;
	}
	public Long getIphCodSus() {
		return iphCodSus;
	}
	public String getContaReapresentada() {
		return DominioSimNao.getInstance(this.getIndContaReapresentada()).getDescricao();
	}
	
	private Boolean nvlContaReapresentada(Boolean indContaReapresentada) {
		Boolean resultado = (Boolean)CoreUtil.nvl(indContaReapresentada, Boolean.FALSE);
		return resultado;
	}	
	public enum Fields {
		EAI_CTH_SEQ("eaiCthSeq"),
		CPF_CNS("cpfCns"),
		IND_CONTA_REAPRESENTADA("indContaReapresentada"),
		IPH_COD_SUS("iphCodSus"),
		;

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
    public int hashCode() {
        HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getCpfCns());
        umHashCodeBuilder.append(this.getEaiCthSeq());
        umHashCodeBuilder.append(this.getIndContaReapresentada());
        umHashCodeBuilder.append(this.getIphCodSus());
        return umHashCodeBuilder.toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ContaNaoReapresentadaCPFVO)) {
            return false;
        }
        ContaNaoReapresentadaCPFVO other = (ContaNaoReapresentadaCPFVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getCpfCns(), other.getCpfCns());
        umEqualsBuilder.append(this.getEaiCthSeq(), other.getEaiCthSeq());
        umEqualsBuilder.append(this.getIndContaReapresentada(), other.getIndContaReapresentada());
        umEqualsBuilder.append(this.getIphCodSus(), other.getIphCodSus());
        return umEqualsBuilder.isEquals();
    }

}
