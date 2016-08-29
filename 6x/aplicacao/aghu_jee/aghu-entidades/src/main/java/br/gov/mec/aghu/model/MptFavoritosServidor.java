package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "MPT_FAVORITOS_SERVIDOR", schema = "AGH")
public class MptFavoritosServidor extends BaseEntitySeq<Long> {
	
	private static final long serialVersionUID = 3988308190385002414L;
	
	private Long seq;
	
	private Short seqTipoSessao;
	
	private Integer matriculaServidor;
	
	private Short vinCodigoServidor;

	@Id
	@Column(name = "SEQ", nullable = false, precision = 9, scale = 0)
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "TPS_SEQ", insertable=false, updatable=false)
	public Short getSeqTipoSessao() {
		return seqTipoSessao;
	}

	public void setSeqTipoSessao(Short seqTipoSessao) {
		this.seqTipoSessao = seqTipoSessao;
	}

	@Column(name = "SER_MATRICULA", insertable=false, updatable=false)
	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	@Column(name = "SER_VIN_CODIGO", insertable=false, updatable=false)
	public Short getVinCodigoServidor() {
		return vinCodigoServidor;
	}

	public void setVinCodigoServidor(Short vinCodigoServidor) {
		this.vinCodigoServidor = vinCodigoServidor;
	}
	
	public enum Fields {		
		SEQ("seq"),
		SEQ_TIPO_SESSAO("seqTipoSessao"),
		MATRICULA_SERVIDOR("matriculaServidor"),
		VIN_CODIGO_SERVIDOR("vinCodigoServidor");
		
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
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.getSeq());
		return hashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MptFavoritosServidor other = (MptFavoritosServidor) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getSeq(), other.getSeq());	
		return equalsBuilder.isEquals();
	}

}
