package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sco_log_envio_sicon database table.
 * 
 */
@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name="SCO_LOG_ENVIO_SICON", schema="AGH")
@SequenceGenerator(name = "scoLconSq1", sequenceName = "AGH.SCO_LCON_SQ1", allocationSize = 1)
public class ScoLogEnvioSicon extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9186472712607761155L;
	private Integer seq;
	private ScoContrato contrato;
	private ScoResContrato rescisao;
	private ScoAditContrato aditivo;
	private Date dtEnvio;
	private DominioSimNao indSucesso;
	private String dsErro;
	private Integer version;
	private DominioSimNao indVlrAf;
	private byte[] arqRel;
	
	
    public ScoLogEnvioSicon() {
    }

	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoLconSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CONT_SEQ", referencedColumnName = "SEQ")	
	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECO_SEQ", referencedColumnName = "SEQ")	
	public ScoResContrato getRescisao() {
		return rescisao;
	}

	public void setRescisao(ScoResContrato rescisao) {
		this.rescisao = rescisao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "ACON_SEQ", referencedColumnName = "SEQ"),
		@JoinColumn(name = "ACON_CONT_SEQ", referencedColumnName = "CONT_SEQ") })
	public ScoAditContrato getAditivo() {
		return aditivo;
	}

	public void setAditivo(ScoAditContrato aditivo) {
		this.aditivo = aditivo;
	}

	@Column(name = "DT_ENVIO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEnvio() {
		return dtEnvio;
	}

	public void setDtEnvio(Date dtEnvio) {
		this.dtEnvio = dtEnvio;
	}
	
	@Column(name = "IND_SUCESSO")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndSucesso() {
		return indSucesso;
	}

	public void setIndSucesso(DominioSimNao indSucesso) {
		this.indSucesso = indSucesso;
	}
	
	@Column(name = "DS_ERRO", length = 255)
	public String getDsErro() {
		return dsErro;
	}

	public void setDsErro(String dsErro) {
		this.dsErro = dsErro;
	}
	
	@Column(name = "VERSION", length= 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "IND_VLR_AF")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndVlrAf() {
		return indVlrAf;
	}

	public void setIndVlrAf(DominioSimNao indVlrAf) {
		this.indVlrAf = indVlrAf;
	}

	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "ARQ_REL")
	public byte[] getArqRel() {
		return arqRel;
	}

	public void setArqRel(byte[] arqRel) {
		this.arqRel = arqRel;
	}

	/**
	 * @return Status Envio Formatado
	 */
	@Transient
	public String getStatusEnvioFormatado() {
		if (DominioSimNao.S.equals(getIndSucesso())) {
			return "Enviada com Sucesso";
		} else {
			return "Retorno de Erro no Envio";
		}
	}
	
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	public boolean equals(Object other) {

		if (!(other instanceof ScoLogEnvioSicon)) {
			return false;
		}

		ScoLogEnvioSicon castOther = (ScoLogEnvioSicon) other;

		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
		
	public enum Fields {
		SEQ("seq"), 
		CONTRATO("contrato"), 
		RESCISAO("rescisao"),
		ADITIVO_SEQ("aditivo.id.seq"),
		ADITIVO_CONT_SEQ("aditivo.id.contSeq"),
		IND_SUCESSO("indSucesso"),
		ADITIVO("aditivo");
		
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}

	}

}