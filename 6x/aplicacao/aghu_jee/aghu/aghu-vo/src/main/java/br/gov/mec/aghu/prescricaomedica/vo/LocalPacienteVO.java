package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LocalPacienteVO implements Serializable {

	private static final long serialVersionUID = -8867762877253896301L;
	
	
	private Short seq;
	private String andarAlaDescricao;
	
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}
	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	public enum Fields {
		SEQ("seq"),
		ANDAR_ALA_DESCRICAO("andarAlaDescricao");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Override
    public int hashCode() {
        HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getSeq());
        umHashCodeBuilder.append(this.getAndarAlaDescricao());
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
        if (!(obj instanceof LocalPacienteVO)) {
            return false;
        }
        LocalPacienteVO other = (LocalPacienteVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getSeq(), other.getSeq());
        umEqualsBuilder.append(this.getAndarAlaDescricao(), other.getAndarAlaDescricao());
        return umEqualsBuilder.isEquals();
    }
	
}
