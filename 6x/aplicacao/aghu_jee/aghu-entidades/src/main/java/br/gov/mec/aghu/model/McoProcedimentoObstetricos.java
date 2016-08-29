package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntity;

@Entity
@SequenceGenerator(name = "mcoObsSq1", sequenceName = "AGH.MCO_OBS_SQ1", allocationSize = 1)
@Table(name = "MCO_PROCEDIMENTO_OBSTETRICOS", schema = "AGH")
public class McoProcedimentoObstetricos implements BaseEntity,
		java.io.Serializable {

	private static final long serialVersionUID = -6223270079545747150L;
	private Short seq;
	private String descricao;
	private DominioSituacao situacao;
	private Integer codigoPHI;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer version;

	@Id
	@Column(name = "SEQ", nullable = false, length = 4)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mcoObsSq1")
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	@NotNull
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "COD", nullable = false, length = 10)
	public Integer getCodigoPHI() {
		return codigoPHI;
	}

	public void setCodigoPHI(Integer codigoPHI) {
		this.codigoPHI = codigoPHI;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false, length = 7)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, length = 3)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Version
	@Column(name = "VERSION", nullable = false, length = 9)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		SEQ("seq"), DESCRICAO("descricao"), SITUACAO("situacao"), CODIGOPHI(
				"codigoPHI"), CRIADO_EM("criadoEm"), SER_MATRICULA(
				"serMatricula"), SER_VIN_CODIGO("serVinCodigo"), VERSION(
				"version");
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
		result = prime * result
				+ ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		McoProcedimentoObstetricos other = (McoProcedimentoObstetricos) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

}
