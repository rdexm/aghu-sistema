package br.gov.mec.aghu.model;

// Generated 19/04/2012 13:34:06 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AghCaixaPostalServidores generated by hbm2java
 */
@Entity
@Table(name = "AGH_CAIXA_POSTAL_SERVIDORES", schema = "AGH")
public class AghCaixaPostalServidor extends BaseEntityId<AghCaixaPostalServidorId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3175148411637166876L;
	private AghCaixaPostalServidorId id;
	private Integer version;
	private Date dthrLida;
	private Date dthrExcluida;
	private String motivoExcluida;
	private DominioSituacaoCxtPostalServidor situacao;
	private Date dthrExcluirDefinitiva;
	private AghCaixaPostal caixaPostal;


	public AghCaixaPostalServidor() {
	}

	public AghCaixaPostalServidor(AghCaixaPostalServidorId id,
			DominioSituacaoCxtPostalServidor situacao) {
		this.id = id;
		this.situacao = situacao;
	}

	public AghCaixaPostalServidor(AghCaixaPostalServidorId id,
			Date dthrLida, Date dthrExcluida, String motivoExcluida,
			DominioSituacaoCxtPostalServidor situacao, Date dthrExcluirDefinitiva) {
		this.id = id;
		this.dthrLida = dthrLida;
		this.dthrExcluida = dthrExcluida;
		this.motivoExcluida = motivoExcluida;
		this.situacao = situacao;
		this.dthrExcluirDefinitiva = dthrExcluirDefinitiva;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cxtSeq", column = @Column(name = "CXT_SEQ", nullable = false)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false)) })
	public AghCaixaPostalServidorId getId() {
		return this.id;
	}

	public void setId(AghCaixaPostalServidorId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_LIDA", length = 29)
	public Date getDthrLida() {
		return this.dthrLida;
	}

	public void setDthrLida(Date dthrLida) {
		this.dthrLida = dthrLida;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_EXCLUIDA", length = 29)
	public Date getDthrExcluida() {
		return this.dthrExcluida;
	}

	public void setDthrExcluida(Date dthrExcluida) {
		this.dthrExcluida = dthrExcluida;
	}

	@Column(name = "MOTIVO_EXCLUIDA", length = 2000)
	@Length(max = 2000)
	public String getMotivoExcluida() {
		return this.motivoExcluida;
	}

	public void setMotivoExcluida(String motivoExcluida) {
		this.motivoExcluida = motivoExcluida;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoCxtPostalServidor getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoCxtPostalServidor situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_EXCLUIR_DEFINITIVA", length = 29)
	public Date getDthrExcluirDefinitiva() {
		return this.dthrExcluirDefinitiva;
	}

	public void setDthrExcluirDefinitiva(Date dthrExcluirDefinitiva) {
		this.dthrExcluirDefinitiva = dthrExcluirDefinitiva;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CXT_SEQ", insertable = false, updatable = false, nullable = false)
	public AghCaixaPostal getCaixaPostal() {
		return caixaPostal;
	}

	public void setCaixaPostal(AghCaixaPostal caixaPostal) {
		this.caixaPostal = caixaPostal;
	}
	
	public enum Fields {

		CXT_SEQ("id.cxtSeq"),
		SERVIDOR("id.servidor"),
		VERSION("version"),
		DTHR_LIDA("dthrLida"),
		DTHR_EXCLUIDA("dthrExcluida"),
		MOTIVO_EXCLUIDA("motivoExcluida"),
		SITUACAO("situacao"),
		DTHR_EXCLUIR_DEFINITIVA("dthrExcluirDefinitiva"),
		CAIXA_POSTAL("caixaPostal");		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
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
		if (!(obj instanceof AghCaixaPostalServidor)) {
			return false;
		}
		AghCaixaPostalServidor other = (AghCaixaPostalServidor) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}