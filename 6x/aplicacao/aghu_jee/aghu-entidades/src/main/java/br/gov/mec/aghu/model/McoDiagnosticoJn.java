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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mcoDigJn", sequenceName="AGH.mco_dig_jn_seq", allocationSize = 1)
@Table(name = "MCO_DIAGNOSTICOS_JN", schema = "AGH")
public class McoDiagnosticoJn extends BaseJournal{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4049615130745104004L;
	
	private Integer seq;
	private Integer cidSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Boolean  indPlacar;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mcoDigJn")
	@Column(name = "SEQ_JN", unique = true, nullable = false)	
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "CID_SEQ")
	public Integer getCidSeq() {
		return cidSeq;
	}


	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@NotNull
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_PLACAR", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPlacar() {
		return this.indPlacar;
	}

	public void setIndPlacar(Boolean indPlacar) {
		this.indPlacar = indPlacar;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		CID("cidSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VINC_CODIGO("serVinCodigo"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		IND_PLACAR("indPlacar");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof McoDiagnostico)) {
			return false;
		}
		McoDiagnostico other = (McoDiagnostico) obj;
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
