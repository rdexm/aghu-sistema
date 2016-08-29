package br.gov.mec.aghu.procedimentoterapeutico.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AfaFormaDosagemVO implements BaseBean {
			
	private static final long serialVersionUID = -5390709362184204302L;
	

	private Integer fdsSeq;
	private Integer fdoUmmSeq;
	private String ummDescricao;
			

	public Integer getFdoUmmSeq() {
		return fdoUmmSeq;
	}
	public void setFdoUmmSeq(Integer fdoUmmSeq) {
		this.fdoUmmSeq = fdoUmmSeq;
	}
	public String getUmmDescricao() {
		return ummDescricao;
	}
	public void setUmmDescricao(String ummDescricao) {
		this.ummDescricao = ummDescricao;
	}

	public Integer getFdsSeq() {
		return fdsSeq;
	}
	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}




	public enum Fields {
		
		FORMA_DOSAGEM_UMM_SEQ("fdoUmmSeq"),
		FDS_SEQ("fdsSeq"),
		UMM_DESCRICAO("ummDescricao");

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
        umHashCodeBuilder.append(this.getFdoUmmSeq());
        umHashCodeBuilder.append(this.getUmmDescricao());
        umHashCodeBuilder.append(this.getFdsSeq());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AfaFormaDosagemVO other = (AfaFormaDosagemVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getFdsSeq(), other.getFdsSeq());
        umEqualsBuilder.append(this.getUmmDescricao(), other.getUmmDescricao());
        umEqualsBuilder.append(this.getFdoUmmSeq(), other.getFdoUmmSeq());
        return umEqualsBuilder.isEquals();
	}
}
