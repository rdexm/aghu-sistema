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
@SequenceGenerator(name = "mamObrJnSeq", sequenceName = "AGH.MAM_OBR_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_OBRIGATORIEDADES_JN", schema = "AGH")
@Immutable
public class MamObrigatoriedadeJn extends BaseJournal {
	private static final long serialVersionUID = -2938142558294986136L;

	private Integer seq;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	private Integer dctSeq;

	private Short iceSeq;
	private Integer sviSeq;

	private Integer mdmSeq;
	private Integer emsSeq;
	private Integer itgSeq;

	public MamObrigatoriedadeJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamObrJnSeq")
	@Column(name = "SEQ_JN", unique = true)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DCT_SEQ", nullable = false)
	@NotNull
	public Integer getDctSeq() {
		return dctSeq;
	}

	public void setDctSeq(Integer dctSeq) {
		this.dctSeq = dctSeq;
	}

	@Column(name = "ICE_SEQ", nullable = true)
	public Short getIceSeq() {
		return iceSeq;
	}

	public void setIceSeq(Short iceSeq) {
		this.iceSeq = iceSeq;
	}

	@Column(name = "SVI_SEQ", nullable = true)
	public Integer getSviSeq() {
		return sviSeq;
	}

	public void setSviSeq(Integer sviSeq) {
		this.sviSeq = sviSeq;
	}

	@Column(name = "MDM_SEQ", nullable = true)
	public Integer getMdmSeq() {
		return mdmSeq;
	}

	public void setMdmSeq(Integer mdmSeq) {
		this.mdmSeq = mdmSeq;
	}

	@Column(name = "EMS_SEQ", nullable = true)
	public Integer getEmsSeq() {
		return emsSeq;
	}

	public void setEmsSeq(Integer emsSeq) {
		this.emsSeq = emsSeq;
	}

	@Column(name = "ITG_SEQ", nullable = true)
	public Integer getItgSeq() {
		return itgSeq;
	}

	public void setItgSeq(Integer itgSeq) {
		this.itgSeq = itgSeq;
	}

	public enum Fields {
		SEQ("seq"), //
		IND_SITUACAO("indSituacao"), //
		DCT_SEQ("dctSeq"), //
		SVI_SEQ("sviSeq"), //
		MDM_SEQ("mdmSeq"), //
		EMS_SEQ("emsSeq"), //
		ITG_SEQ("itgSeq"), //
		ICE_SEQ("iceSeq"), //
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
