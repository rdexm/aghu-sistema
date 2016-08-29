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

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "mamFlxJnSeq", sequenceName = "AGH.MAM_FLX_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_FLUXOGRAMAS_JN", schema = "AGH")
@Immutable
public class MamFluxogramaJn extends BaseJournal {
	private static final long serialVersionUID = 996843814919394225L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer ordem;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer pcrSeq;

	public MamFluxogramaJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamFlxJnSeq")
	@Column(name = "SEQ_JN", unique = true)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "SER_MATRICULA")
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", length = 50)
	@NotNull
	@Length(max = 50)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM")
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "PCR_SEQ")
	public Integer getPcrSeq() {
		return pcrSeq;
	}

	public void setPcrSeq(Integer pcrSeq) {
		this.pcrSeq = pcrSeq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public enum Fields {

		SEQ("seq"), //
		DESCRICAO("descricao"), //
		IND_SITUACAO("indSituacao"), //
		ORDEM("ordem"), //
		PCR_SEQ("pcrSeq"), //
		;

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
