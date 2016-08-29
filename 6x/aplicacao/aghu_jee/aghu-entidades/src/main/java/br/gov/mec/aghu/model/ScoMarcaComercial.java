package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


/**
 * The persistent class for the sco_marcas_comerciais database table.
 * 
 */
@Entity
@Table(name="SCO_MARCAS_COMERCIAIS" ,schema = "AGH")
@SequenceGenerator(name="SCO_MARCAS_COMERCIAIS_CODIGO_GENERATOR", sequenceName="AGH.SCO_MCM_SQ1", allocationSize = 1)
@Indexed
public class ScoMarcaComercial extends BaseEntityCodigo<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5121245578454034038L;
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer version;
	private Set<ScoMarcaModelo> scoMarcaModelos;
	//private Set<ScoItemAutorizacaoForn> scoItensAutorizacaoForn;
	private Set<ScoNomeComercial> scoNomesComerciais;
	
	public enum Fields{
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		DESCRICAO_FONETICO("descricaoFonetico"),
		SITUACAO("indSituacao"),
		NOME_COMERCIAL("scoNomesComerciais");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

    public ScoMarcaComercial() {
    }

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 6, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SCO_MARCAS_COMERCIAIS_CODIGO_GENERATOR")
	@DocumentId
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "descricao", nullable = false, length = 60)
	@Field(index = Index.YES, store = Store.YES)
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
	
	@Transient
	@Field(index = Index.YES, store = Store.YES)
	public String getDescricaoFonetico() {
		return Fonetizador.fonetizar(descricao);
	}


	//bi-directional many-to-one association to ScoMarcaModelo
	@OneToMany(mappedBy="scoMarcaComercial", cascade=CascadeType.ALL)
	public Set<ScoMarcaModelo> getScoMarcaModelos() {
		return this.scoMarcaModelos;
	}

	public void setScoMarcaModelos(Set<ScoMarcaModelo> scoMarcaModelos) {
		this.scoMarcaModelos = scoMarcaModelos;
	}
	
	//bi-directional many-to-one association to ScoItensAutorizacaoForn
//	@OneToMany(mappedBy="scoMarcaComercial")
//	public Set<ScoItemAutorizacaoForn> getScoItensAutorizacaoForn() {
//		return this.scoItensAutorizacaoForn;
//	}
//
//	public void setScoItensAutorizacaoForn(Set<ScoItemAutorizacaoForn> scoItensAutorizacaoForn) {
//		this.scoItensAutorizacaoForn = scoItensAutorizacaoForn;
//	}
	
	//bi-directional many-to-one association to ScoNomesComerciai
	@OneToMany(mappedBy="marcaComercial", cascade=CascadeType.ALL)
	public Set<ScoNomeComercial> getScoNomesComerciais() {
		return this.scoNomesComerciais;
	}

	public void setScoNomesComerciais(Set<ScoNomeComercial> scoNomesComerciais) {
		this.scoNomesComerciais = scoNomesComerciais;
	}
		
	@Transient
	// FIXME - Remover esse método transiente do POJO, pois os mesmos devem ser evitados em POJOs.
	// Uma alternativa é colocá-lo em um VO.
	public String getCodigoEDescricao(){
		return this.codigo + " - " + this.descricao;
	}


	public void set(ScoMarcaComercial marcaComercial) {
		// TODO Auto-generated method stub
		
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
		if (!(obj instanceof ScoMarcaComercial)) {
			return false;
		}
		ScoMarcaComercial other = (ScoMarcaComercial) obj;
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