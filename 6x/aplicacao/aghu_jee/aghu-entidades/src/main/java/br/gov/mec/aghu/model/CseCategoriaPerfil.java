package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "CSE_CATEGORIA_PERFIS", schema = "AGH")
public class CseCategoriaPerfil extends BaseEntityId<CseCategoriaPerfilId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 71401707153862610L;

	private CseCategoriaPerfilId id;

	private DominioSituacao indSituacao;
	private Date criadoEm;

	private CseCategoriaProfissional cseCategoriaProfissional;
	private RapServidores servidor;

	// construtores

	public CseCategoriaPerfil() {
	}

	public CseCategoriaPerfil(CseCategoriaPerfilId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "PER_NOME", column = @Column(name = "PER_NOME", nullable = false, length = 30)),
			@AttributeOverride(name = "CAG_SEQ", column = @Column(name = "CAG_SEQ", nullable = false, length = 4))})
	public CseCategoriaPerfilId getId() {
		return this.id;
	}

	public void setId(CseCategoriaPerfilId id) {
		this.id = id;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne
	@JoinColumn(name = "CAG_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public CseCategoriaProfissional getCseCategoriaProfissional() {
		return cseCategoriaProfissional;
	}

	public void setCseCategoriaProfissional(
			CseCategoriaProfissional categoriaProfissional) {
		this.cseCategoriaProfissional = categoriaProfissional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CseCategoriaPerfil)) {
			return false;
		}
		CseCategoriaPerfil castOther = (CseCategoriaPerfil) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		IND_SITUACAO("indSituacao"), CRIADO_EM("criadoEm"),

		PERFIL("id.perNome"), CSE_CATEGORIA_PROFISSIONAL(
				"cseCategoriaProfissional"), RAP_SERVIDORES("servidor"), CSE_CATEGORIA_PROFISSIONAL_SEQ("cseCategoriaProfissional.seq") ;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}