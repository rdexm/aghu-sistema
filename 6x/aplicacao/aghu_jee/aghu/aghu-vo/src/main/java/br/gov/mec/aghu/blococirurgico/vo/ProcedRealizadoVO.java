package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ProcedRealizadoVO implements BaseBean {

	private static final long serialVersionUID = -3359552209246999714L;
	
	private String descricao;
	private DominioIndContaminacao contaminacao;
	private Date dthrInicioCirg;
	private String descricaoFormatada;
	
	// ID de MBC_PROC_DESCRICOES
	private Integer podDcgCrgSeq;
	private Short podDcgSeqp;
	private Integer podSeqp;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioIndContaminacao getContaminacao() {
		return contaminacao;
	}

	public void setContaminacao(DominioIndContaminacao contaminacao) {
		this.contaminacao = contaminacao;
	}

	public Date getDthrInicioCirg() {
		return dthrInicioCirg;
	}

	public void setDthrInicioCirg(Date dthrInicioCirg) {
		this.dthrInicioCirg = dthrInicioCirg;
	}

	public String getDescricaoFormatada() {
		return descricaoFormatada;
	}

	public void setDescricaoFormatada(String descricaoFormatada) {
		this.descricaoFormatada = descricaoFormatada;
	}

	public Integer getPodDcgCrgSeq() {
		return podDcgCrgSeq;
	}

	public void setPodDcgCrgSeq(Integer podDcgCrgSeq) {
		this.podDcgCrgSeq = podDcgCrgSeq;
	}

	public Short getPodDcgSeqp() {
		return podDcgSeqp;
	}

	public void setPodDcgSeqp(Short podDcgSeqp) {
		this.podDcgSeqp = podDcgSeqp;
	}

	public Integer getPodSeqp() {
		return podSeqp;
	}

	public void setPodSeqp(Integer podSeqp) {
		this.podSeqp = podSeqp;
	}

	public enum Fields {

		DESCRICAO("descricao"), 
		IND_CONTAMINACAO("contaminacao"),
		DTHR_INICIO_CIRG("dthrInicioCirg"),
		DCG_CRG_SEQ("podDcgCrgSeq"),
		DCG_SEQP("podDcgSeqp"),
		SEQP("podSeqp"),
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	@Override
    public int hashCode() {
        HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getDescricao());
        umHashCodeBuilder.append(this.getContaminacao());
        umHashCodeBuilder.append(this.getDthrInicioCirg());
        umHashCodeBuilder.append(this.getDescricaoFormatada());
        umHashCodeBuilder.append(this.getPodDcgCrgSeq());
        umHashCodeBuilder.append(this.getPodDcgSeqp());
        umHashCodeBuilder.append(this.getPodSeqp());
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
        if (!(obj instanceof ProcedRealizadoVO)) {
            return false;
        }
        ProcedRealizadoVO other = (ProcedRealizadoVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
        umEqualsBuilder.append(this.getContaminacao(), other.getContaminacao());
        umEqualsBuilder.append(this.getDthrInicioCirg(), other.getDthrInicioCirg());
        umEqualsBuilder.append(this.getDescricaoFormatada(), other.getDescricaoFormatada());
        umEqualsBuilder.append(this.getPodDcgCrgSeq(), other.getPodDcgCrgSeq());
        umEqualsBuilder.append(this.getPodDcgSeqp(), other.getPodDcgSeqp());
        umEqualsBuilder.append(this.getPodSeqp(), other.getPodSeqp());
        return umEqualsBuilder.isEquals();
    }
}