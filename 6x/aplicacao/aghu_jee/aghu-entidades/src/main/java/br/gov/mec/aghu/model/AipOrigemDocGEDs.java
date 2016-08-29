package br.gov.mec.aghu.model;

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
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AIP_ORIGEM_DOC_GEDS", schema = "AGH")
@SequenceGenerator(name = "AipOrigemDocGEDSSequence", sequenceName = "AGH.AIP_ODG_SQ1")
public class AipOrigemDocGEDs extends BaseEntitySeq<Integer> {

	public enum Fields {
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		ORIGEM("origem"),
		REFERENCIA("referencia"),
		SEQ("seq"),
		SER_MATRICULA_CADASTRO("servidor.id.matricula"),
		SER_VIN_CODIGO_CADASTRO("servidor.id.vinCodigo"),
		SERVIDOR("servidor");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4572107606639151413L;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private DominioOrigemDocsDigitalizados origem;
	private String referencia;
	private Integer seq;
	private RapServidores servidor;

	private Integer version;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AipOrigemDocGEDs)) {
			return false;
		}
		AipOrigemDocGEDs other = (AipOrigemDocGEDs) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	@Column(name = "ORIGEM", nullable = false, length = 60)
	@Enumerated(EnumType.STRING)
	public DominioOrigemDocsDigitalizados getOrigem() {
		return origem;
	}

	@Column(name = "REFERENCIA", nullable = false, length = 60)
	public String getReferencia() {
		return referencia;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AipOrigemDocGEDSSequence")
	@Column(name = "SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setOrigem(DominioOrigemDocsDigitalizados origem) {
		this.origem = origem;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}	

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
