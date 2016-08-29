package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;




import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "FAT_CBOS", schema = "AGH")
@SequenceGenerator(name = "fatCbosSeq", sequenceName = "AGH.FAT_CBO_SEQ", allocationSize = 1)
@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class FatCbos extends BaseEntitySeq<Integer> implements Serializable {
	private static final long serialVersionUID = 7455604905902177491L;
	
	private String codigo;
	private String descricao;
	private Integer version;
	
	private Integer seq;
	private Date dtInicio;
	private Date dtFim;
	
	private Set<FatProcedimentoCbo> listaProcedimentoCbo = new HashSet<FatProcedimentoCbo>();

	// getters & setters
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatCbosSeq")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DT_INICIO", nullable=false)
	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Column(name = "DT_FIM")
	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	
	
	@Column(name = "CODIGO", length = 6, nullable = false)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(final String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 600, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy="cbo")
	public Set<FatProcedimentoCbo> getListaProcedimentoCbo() {
		return listaProcedimentoCbo;
	}

	public void setListaProcedimentoCbo(Set<FatProcedimentoCbo> listaProcedimentoCbo) {
		this.listaProcedimentoCbo = listaProcedimentoCbo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq)
		.toString();
	}


	public enum Fields {
		CODIGO("codigo"), 
		DESCRICAO("descricao"),
		LISTA_PROCEDIMENTO_CBO("listaProcedimentoCbo"),
		SEQ("seq"),
		DT_FIM("dtFim"),
		DT_INICIO("dtInicio")
		;

		private final String field;

		private Fields(final String field) {
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
		umHashCodeBuilder.append(this.getCodigo());
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getDtFim());
		umHashCodeBuilder.append(this.getDtInicio());
		umHashCodeBuilder.append(this.getSeq());
		umHashCodeBuilder.append(this.getVersion());
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
		if (!(obj instanceof FatCbos)) {
			return false;
		}
		FatCbos other = (FatCbos)obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCodigo(), other.getCodigo());
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getDtFim(), other.getDtFim());
		umEqualsBuilder.append(this.getDtInicio(), other.getDtInicio());
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getVersion(), this.getVersion());
		return umEqualsBuilder.isEquals();
	}

}