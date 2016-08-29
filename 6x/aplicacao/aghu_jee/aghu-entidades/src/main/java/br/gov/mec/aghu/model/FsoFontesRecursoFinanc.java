package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


/**
 * The persistent class for the fso_fontes_recurso_financ database table.
 * 
 */
@Entity
@Table(name="FSO_FONTES_RECURSO_FINANC", schema="AGH")
public class FsoFontesRecursoFinanc extends BaseEntityCodigo<Long> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2400615100042858907L;

	private Long codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer version;

	
    public FsoFontesRecursoFinanc() {
    }
    
    @Id
    @Column(name="CODIGO",length=10)
	public Long getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	@Column(name="DESCRICAO",length=60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "VERSION", length= 7)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {

		CODIGO("codigo"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FsoFontesRecursoFinanc)) {
			return false;
		}
		FsoFontesRecursoFinanc other = (FsoFontesRecursoFinanc) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
    
}