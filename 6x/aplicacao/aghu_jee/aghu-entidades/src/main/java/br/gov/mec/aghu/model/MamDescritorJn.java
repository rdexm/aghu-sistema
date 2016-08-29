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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MAM_DESCRITORES_JN", schema = "AGH")
@SequenceGenerator(name = "mamFlxJnSeq", sequenceName = "AGH.MAM_DCT_JN_SEQ", allocationSize = 1)
@Immutable
public class MamDescritorJn extends BaseJournal {
	private static final long serialVersionUID = -8363561076399571495L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer ordem;

	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	private Integer flxSeq;
	private Short grvSeq;

	private Boolean indDtQueixaObgt;
	private Boolean indHrQueixaObgt;

	public MamDescritorJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamFlxJnSeq")
	@Column(name = "SEQ_JN", unique = true)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	@Column(name = "DESCRICAO", nullable = false, length = 50)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM", nullable = false)
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "FLX_SEQ", nullable = false)
	public Integer getFlxSeq() {
		return flxSeq;
	}

	public void setFlxSeq(Integer flxSeq) {
		this.flxSeq = flxSeq;
	}

	@Column(name = "GRV_SEQ", nullable = false)
	public Short getGrvSeq() {
		return grvSeq;
	}

	public void setGrvSeq(Short grvSeq) {
		this.grvSeq = grvSeq;
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

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_DT_QUEIXA_OBGT", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getIndDtQueixaObgt() {
		return indDtQueixaObgt;
	}

	public void setIndDtQueixaObgt(Boolean indDtQueixaObgt) {
		this.indDtQueixaObgt = indDtQueixaObgt;
	}

	@Column(name = "IND_HR_QUEIXA_OBGT", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getIndHrQueixaObgt() {
		return indHrQueixaObgt;
	}

	public void setIndHrQueixaObgt(Boolean indHrQueixaObgt) {
		this.indHrQueixaObgt = indHrQueixaObgt;
	}

	public enum Fields {

		SEQ("seq"), //
		DESCRICAO("descricao"), //
		IND_SITUACAO("indSituacao"), //
		ORDEM("ordem"), //
		FLUXOGRAMA("fluxograma"), //
		GRAVIDADE("gravidade"), //
		IND_DT_QUEIXA_OBGT("indDtQueixaObgt"), //
		IND_HR_QUEIXA_OBGT("indHrQueixaObgt"), //
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
