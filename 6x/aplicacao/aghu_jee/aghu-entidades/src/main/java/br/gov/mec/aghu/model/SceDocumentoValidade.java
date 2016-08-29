package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

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


/**
 * The persistent class for the sce_documento_validades database table.
 * 
 */
@Entity
@Table(name="SCE_DOCUMENTO_VALIDADES")
public class SceDocumentoValidade extends BaseEntityId<SceDocumentoValidadeID> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6833604108181026819L;
	private SceDocumentoValidadeID id;
	private Date dtAlteracao;
	private Date dtGeracao;
	private Integer quantidade;
	private RapServidores servidor;
	private RapServidores servidorAlterado;
	private Integer version;
	private SceValidade validade;
	private SceTipoMovimento tipoMovimento;

    public SceDocumentoValidade() {
    }
	
	@EmbeddedId
	public SceDocumentoValidadeID getId() {
		return this.id;
	}

	public void setId(SceDocumentoValidadeID id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ALTERACAO")
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_GERACAO")
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}
	
	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="EAL_SEQ", referencedColumnName="EAL_SEQ",  insertable = false, updatable = false),
		@JoinColumn(name="DATA", referencedColumnName="DATA",  insertable = false, updatable = false)
		})
	public SceValidade getValidade() {
		return validade;
	}
	
	public void setValidade(SceValidade validade) {
		this.validade = validade;
	}
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="TMV_SEQ", referencedColumnName="SEQ",  insertable = false, updatable = false),
		@JoinColumn(name="TMV_COMPLEMENTO", referencedColumnName="COMPLEMENTO",  insertable = false, updatable = false)
		})
	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}
	
	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		NUMERO_DOCUMENTO("id.nroDocumento"),
		TMV_COMPLEMENTO("id.tmvComplemento"),
		EAL_SEQ("id.ealSeq"),
		TMV_SEQ("id.tmvSeq"),
		DATA("id.data"),
		VALIDADE("validade");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
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
		if (!(obj instanceof SceDocumentoValidade)) {
			return false;
		}
		SceDocumentoValidade other = (SceDocumentoValidade) obj;
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