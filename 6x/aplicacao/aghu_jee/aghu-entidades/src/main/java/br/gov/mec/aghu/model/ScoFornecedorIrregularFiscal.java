package br.gov.mec.aghu.model;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacaoEnvioEmail;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "SCO_FORN_IRREGULAR_FISCAL")
public class ScoFornecedorIrregularFiscal extends BaseEntityId<ScoFornecedorIrregularFiscalId> implements java.io.Serializable {

	/**
	 * serialVersionUID auto-generation
	 */
	private static final long serialVersionUID = -652837492480181773L;
	private ScoFornecedorIrregularFiscalId id;
	private RapServidores servidor;
	private Date data;
	private DominioSituacaoEnvioEmail situacao;
	private Integer version;
	private ScoFornecedor fornecedor;

	public ScoFornecedorIrregularFiscal() {
	}

	public ScoFornecedorIrregularFiscal(ScoFornecedorIrregularFiscalId id, RapServidores servidor, Date data, Integer version) {
		super();
		this.id = id;
		this.servidor = servidor;
		this.data = data;
		this.version = version;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "frnNumero", column = @Column(name = "FRN_NUMERO", nullable = false)),
			@AttributeOverride(name = "numero", column = @Column(name = "NUMERO", nullable = false)) })
	public ScoFornecedorIrregularFiscalId getId() {
		return this.id;
	}

	public void setId(ScoFornecedorIrregularFiscalId id) {
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
	@Column(name = "DATA", nullable = false)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name = "SITUACAO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEnvioEmail getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoEnvioEmail situacao) {
		this.situacao = situacao;
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
		ScoFornecedorIrregularFiscal other = (ScoFornecedorIrregularFiscal) obj;
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

		FRN_NUMERO("id.frnNumero"), 
		NUMERO("id.numero"),
		SITUACAO("situacao"),
		RAP_SERVIDOR("servidor");

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
