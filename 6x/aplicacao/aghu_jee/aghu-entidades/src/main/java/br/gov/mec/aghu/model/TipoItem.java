package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;


@Entity
@Table(name = "TIPO_ITEM", schema = "CONV")
public class TipoItem implements BaseEntity, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6852406435591327224L;
	
	private Short cod;
	private String dscr;
	private Integer cctCodigo;
	private Short gitCodigo;
	private Short sicCodigo;
	
	public TipoItem() {
	}

	public TipoItem(Short cod, String dscr, Integer cctCodigo, Short gitCodigo,
			Short sicCodigo) {
		this.cod = cod;
		this.dscr = dscr;
		this.cctCodigo = cctCodigo;
		this.gitCodigo = gitCodigo;
		this.sicCodigo = sicCodigo;
	}

	
	/*
	 * Getters e Setters
	 */
	
	@Id
	@Column(name = "COD", unique = true, nullable = false, precision = 2, scale = 0)
	public Short getCod() {
		return cod;
	}

	public void setCod(Short cod) {
		this.cod = cod;
	}

	@Column(name = "DSCR", nullable = false, length = 60)
	public String getDscr() {
		return dscr;
	}

	public void setDscr(String dscr) {
		this.dscr = dscr;
	}

	@Column(name = "CCT_CODIGO", precision = 6, scale = 0)
	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	@Column(name = "GIT_CODIGO", precision = 3, scale = 0)
	public Short getGitCodigo() {
		return gitCodigo;
	}

	public void setGitCodigo(Short gitCodigo) {
		this.gitCodigo = gitCodigo;
	}

	@Column(name = "SIC_CODIGO", precision = 3, scale = 0)
	public Short getSicCodigo() {
		return sicCodigo;
	}

	public void setSicCodigo(Short sicCodigo) {
		this.sicCodigo = sicCodigo;
	}
	
	
	public enum Fields {
		COD("cod"), DSCR("dscr"), CCT_CODIGO("cctCodigo"), GIT_CODIGO("gitCodigo"),
		SIC_CODIGO("sicCodigo");
		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	// ##### GeradorEqualsHashCodeMain #####
		@Override
		public int hashCode() {
			HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
			umHashCodeBuilder.append(this.getCod());
			umHashCodeBuilder.append(this.getDscr());
			umHashCodeBuilder.append(this.getCctCodigo());
			umHashCodeBuilder.append(this.getGitCodigo());
			umHashCodeBuilder.append(this.getSicCodigo());
	
			
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
			if (!(obj instanceof TipoItem)) {
				return false;
			}
			TipoItem other = (TipoItem) obj;
			EqualsBuilder umEqualsBuilder = new EqualsBuilder();
			umEqualsBuilder.append(this.getCod(), other.getCod());
			umEqualsBuilder.append(this.getDscr(), other.getDscr());
			umEqualsBuilder.append(this.getCctCodigo(), other.getCctCodigo());
			umEqualsBuilder.append(this.getGitCodigo(), other.getGitCodigo());
			umEqualsBuilder.append(this.getSicCodigo(), other.getSicCodigo());
					
			return umEqualsBuilder.isEquals();
		}

}
