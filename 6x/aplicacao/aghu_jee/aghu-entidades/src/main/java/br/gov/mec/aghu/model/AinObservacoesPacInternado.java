package br.gov.mec.aghu.model;

// Generated 09/08/2010 18:32:51 by Hibernate Tools 3.3.0.GA

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


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AinObservacoesPacInternado generated by hbm2java
 */

@Entity
@Table(name = "AIN_OBSERVACOES_PAC_INTERNADO", schema = "AGH")
public class AinObservacoesPacInternado extends BaseEntityId<AinObservacoesPacInternadoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5636850865866662398L;
	private AinObservacoesPacInternadoId id;
	private RapServidores servidor;
	private RapServidores servidorProvidencia;
	private AinInternacao internacao;
	private String recado;
	private Date criadoEm;
	private Boolean recadoProv;
	private Date dthrProvidencia;

	public AinObservacoesPacInternado() {
	}

	public AinObservacoesPacInternado(AinObservacoesPacInternadoId id,
			RapServidores servidor,
			AinInternacao internacao, String recado, Date criadoEm,
			Boolean recadoProv) {
		this.id = id;
		this.servidor = servidor;
		this.internacao = internacao;
		this.recado = recado;
		this.criadoEm = criadoEm;
		this.recadoProv = recadoProv;
	}

	public AinObservacoesPacInternado(AinObservacoesPacInternadoId id,
			RapServidores servidor,
			RapServidores servidorProvidencia,
			AinInternacao internacao, String recado, Date criadoEm,
			Boolean recadoProv, Date dthrProvidencia) {
		this.id = id;
		this.servidor = servidor;
		this.servidorProvidencia = servidorProvidencia;
		this.internacao = internacao;
		this.recado = recado;
		this.criadoEm = criadoEm;
		this.recadoProv = recadoProv;
		this.dthrProvidencia = dthrProvidencia;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "intSeq", column = @Column(name = "INT_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, precision = 3, scale = 0)) })
	public AinObservacoesPacInternadoId getId() {
		return this.id;
	}

	public void setId(AinObservacoesPacInternadoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(
			RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_PROVIDENCIA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_PROVIDENCIA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorProvidencia() {
		return this.servidorProvidencia;
	}

	public void setServidorProvidencia(RapServidores servidorProvidencia) {
		this.servidorProvidencia = servidorProvidencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INT_SEQ", nullable = false, insertable = false, updatable = false)
	public AinInternacao getInternacao() {
		return this.internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	@Column(name = "RECADO", nullable = false, length = 240)
	public String getRecado() {
		return this.recado;
	}

	public void setRecado(String recado) {
		this.recado = recado;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_RECADO_PROV", nullable = false, length=1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")    
	public Boolean getRecadoProv() {
		return this.recadoProv;
	}

	public void setRecadoProv(Boolean recadoProv) {
		this.recadoProv = recadoProv;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_PROVIDENCIA", length = 7)
	public Date getDthrProvidencia() {
		return this.dthrProvidencia;
	}

	public void setDthrProvidencia(Date dthrProvidencia) {
		this.dthrProvidencia = dthrProvidencia;
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
		if (!(obj instanceof AinObservacoesPacInternado)) {
			return false;
		}
		AinObservacoesPacInternado other = (AinObservacoesPacInternado) obj;
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

		ID("id"),
		SERVIDOR("servidor"),
		SERVIDOR_PROVIDENCIA("servidorProvidencia"),
		INTERNACAO("internacao"),
		RECADO("recado"),
		CRIADO_EM("criadoEm"),
		RECADO_PROV("recadoProv"),
		DTHR_PROVIDENCIA("dthrProvidencia");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
