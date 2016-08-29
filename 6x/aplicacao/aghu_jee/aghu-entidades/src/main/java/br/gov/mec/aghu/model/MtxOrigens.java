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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxOrgSq1", sequenceName="AGH.MTX_ORG_SQ1", allocationSize = 1)
@Table(name = "MTX_ORIGENS", schema = "AGH")
public class MtxOrigens extends BaseEntitySeq<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1180228598973573959L;

	private Integer seq;
	private String descricao;
	private DominioSituacao situacao;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxOrgSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 250, nullable = false)
	@Length(max = 250)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public static enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"), 
		SITUACAO("situacao"), 
		SERVIDOR("servidor"), 
		CRIADO_EM ("criadoEm");

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
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
        umHashCodeBuilder.append(this.getDescricao());
        umHashCodeBuilder.append(this.getSituacao());
        umHashCodeBuilder.append(this.getCriadoEm());
        return umHashCodeBuilder.toHashCode();
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
		MtxOrigens other = (MtxOrigens) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
        umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
        umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
        umEqualsBuilder.append(this.getCriadoEm(), other.getCriadoEm());
        return umEqualsBuilder.isEquals();
	}
}
