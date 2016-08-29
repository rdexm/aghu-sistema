package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "SCE_FORNECEDOR_MATERIAL")
public class SceFornecedorMaterial extends BaseEntityId<SceFornecedorMaterialId> implements java.io.Serializable {

	/**
	 * serialVersionUID auto-generation
	 */
	private static final long serialVersionUID = -3654037592480181773L;
	private SceFornecedorMaterialId id;
	private RapServidores servidor;
	private Date dataGeracao;
	private String registroAnvisa;
	private Long cnpjRegistroAnvisa;
	private Integer version;
	private ScoMaterial material;
	private ScoFornecedor fornecedor;

	public SceFornecedorMaterial() {
	}

	public SceFornecedorMaterial(SceFornecedorMaterialId id, RapServidores servidor, Date dataGeracao, String registroAnvisa, Long cnpjRegistroAnvisa,
			Integer version) {
		super();
		this.id = id;
		this.servidor = servidor;
		this.dataGeracao = dataGeracao;
		this.registroAnvisa = registroAnvisa;
		this.cnpjRegistroAnvisa = cnpjRegistroAnvisa;
		this.version = version;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "numero", column = @Column(name = "FRN_NUMERO", nullable = false)),
			@AttributeOverride(name = "codigo", column = @Column(name = "MAT_CODIGO", nullable = false)) })
	public SceFornecedorMaterialId getId() {
		return this.id;
	}

	public void setId(SceFornecedorMaterialId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_GERACAO", nullable = false)
	public Date getDataGeracao() {
		return this.dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	@Column(name = "REG_ANVISA", length = 20, nullable = false)
	public String getRegistroAnvisa() {
		return this.registroAnvisa;
	}

	public void setRegistroAnvisa(String registroAnvisa) {
		this.registroAnvisa = registroAnvisa;
	}

	@Column(name = "CNPJ_REG_ANVISA", precision = 14, scale = 0, nullable = false)
	public Long getCnpjRegistroAnvisa() {
		return this.cnpjRegistroAnvisa;
	}

	public void setCnpjRegistroAnvisa(Long cnpjRegistroAnvisa) {
		this.cnpjRegistroAnvisa = cnpjRegistroAnvisa;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO", updatable = false, insertable = false)
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", referencedColumnName = "NUMERO", updatable = false, insertable = false)
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		SceFornecedorMaterial other = (SceFornecedorMaterial) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {

		FRN_NUMERO("id.numero"), 
		MAT_CODIGO("id.codigo");

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
