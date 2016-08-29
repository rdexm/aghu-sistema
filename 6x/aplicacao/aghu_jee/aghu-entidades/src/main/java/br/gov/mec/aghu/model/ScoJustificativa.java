package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

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
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoJustificativa;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


/**
 * The persistent class for the sco_justificativas database table.
 * 
 */
@Entity
@SequenceGenerator(name="scoJstSq1", sequenceName="sco_jst_sq1", allocationSize = 1)
@Table(name="SCO_JUSTIFICATIVAS" , schema = "AGH")
public class ScoJustificativa extends BaseEntityCodigo<Short> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 1237194890613149890L;
    private Short codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private DominioTipoJustificativa tipo;
	private Integer version;
	private Set<ScoProgEntregaItemAutorizacaoFornecimento> scoProgrEntregaItensAfs;
    public ScoJustificativa() {
    }


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator = "scoJstSq1")
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 3, scale = 0)
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", nullable = true, length = 240)
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

	@Column(name = "TIPO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoJustificativa getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoJustificativa tipo) {
		this.tipo = tipo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
	//bi-directional many-to-one association to ScoProgEntregaItemAutorizacaoFornecimento
	@OneToMany(mappedBy="scoJustificativa")
	public Set<ScoProgEntregaItemAutorizacaoFornecimento> getScoProgrEntregaItensAfs() {
		return this.scoProgrEntregaItensAfs;
	}

	public void setScoProgrEntregaItensAfs(Set<ScoProgEntregaItemAutorizacaoFornecimento> scoProgrEntregaItensAfs) {
		this.scoProgrEntregaItensAfs = scoProgrEntregaItensAfs;
	}
	
	public enum Fields {

		CODIGO("codigo"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		TIPO("tipo"),
		SCO_PROGR_ENTREGA_ITENS_AFS("scoProgrEntregaItensAfs");

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
		return new HashCodeBuilder().append(getCodigo()).hashCode();		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof ScoJustificativa) ){
			return false;
		}
		ScoJustificativa other = (ScoJustificativa) obj;
		return new EqualsBuilder().append(this.getCodigo(), other.getCodigo()).isEquals();
				
	}
	// ##### GeradorEqualsHashCodeMain #####
	
/*	@Override
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
		if (!(obj instanceof ScoJustificativa)) {
			return false;
		}
		ScoJustificativa other = (ScoJustificativa) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}*/
	// ##### GeradorEqualsHashCodeMain #####

}