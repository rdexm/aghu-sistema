package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_marca_modelos database table.
 * 
 */
@Entity
@Table(name="SCO_MARCA_MODELOS")
@Indexed
public class ScoMarcaModelo extends BaseEntityId<ScoMarcaModeloId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -6779209406895893819L;
    private ScoMarcaModeloId id;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer version;
	private Set<ScoMarcaModeloMaterial> scoMarcaModeloMateriais;
	private ScoMarcaComercial scoMarcaComercial;
	private Set<ScoMaterial> materiais;
	
	public enum Fields{
		
		ID("id"),
		DESCRICAO("descricao"),
		DESCRICAO_FONETICO("descricaoFonetico"),
		MARCA_COMERCIAL("scoMarcaComercial"),
		MARCA_COMERCIAL_CODIGO("scoMarcaComercial.codigo"),
		MCM_CODIGO("id.mcmCodigo"),
		SEQP("id.seqp"),
		IND_SITUACAO("indSituacao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

    public ScoMarcaModelo() {
    }

	
	@EmbeddedId 
	@DocumentId
	@FieldBridge(impl = ScoMarcaModeloIdBridge.class)
	@AttributeOverrides( {
			@AttributeOverride(name = "mcmCodigo", column = @Column(name = "MCM_CODIGO", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })	
	public ScoMarcaModeloId getId() {
		return this.id;
	}

	public void setId(ScoMarcaModeloId id) {
		this.id = id;
	}
	
	@Column(name = "descricao", nullable = false, length = 100)
	@Field(index = Index.YES, store = Store.YES)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}


	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to ScoMarcaModeloMaterial
	@OneToMany(mappedBy="scoMarcaModelo", fetch = FetchType.LAZY)
	public Set<ScoMarcaModeloMaterial> getScoMarcaModeloMateriais() {
		return this.scoMarcaModeloMateriais;
	}

	public void setScoMarcaModeloMateriais(Set<ScoMarcaModeloMaterial> scoMarcaModeloMateriais) {
		this.scoMarcaModeloMateriais = scoMarcaModeloMateriais;
	}
	

	//bi-directional many-to-one association to ScoMarcaComercial
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="MCM_CODIGO", insertable=false, updatable=false)
	public ScoMarcaComercial getScoMarcaComercial() {
		return this.scoMarcaComercial;
	}

	public void setScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) {
		this.scoMarcaComercial = scoMarcaComercial;
	}
	
	//bi-directional many-to-many association to ScoMaterial
	@ManyToMany(mappedBy="marcasModelos", fetch = FetchType.LAZY)
	public Set<ScoMaterial> getMateriais() {
		return materiais;
	}


	public void setMateriais(Set<ScoMaterial> materiais) {
		this.materiais = materiais;
	}
	
	@Transient
	@Field(index = Index.YES, store = Store.YES)
	public String getDescricaoFonetico() {
		return Fonetizador.fonetizar(descricao);
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof ScoMarcaModelo)) {
			return false;
		}
		ScoMarcaModelo other = (ScoMarcaModelo) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}