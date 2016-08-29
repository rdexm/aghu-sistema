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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MAM_OBRIGATORIEDADES", schema = "AGH")
@SequenceGenerator(name = "mamObrSeq", sequenceName = "AGH.MAM_OBR_SQ1", allocationSize = 1)
public class MamObrigatoriedade extends BaseEntitySeq<Integer> implements Serializable {
	private static final long serialVersionUID = -9168818838390032169L;

	private Integer seq;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer version;

	private MamDescritor mamDescritor;
	
	private Short iceSeq;
	private MamItemSinalVital mamItemSinalVital;
	
	private MamItemMedicacao mamItemMedicacao;
	private MamItemExame mamItemExame;
	private MamItemGeral mamItemGeral;

	public MamObrigatoriedade() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamObrSeq")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DCT_SEQ", nullable = false)
	@NotNull
	public MamDescritor getMamDescritor() {
		return mamDescritor;
	}

	public void setMamDescritor(MamDescritor mamDescritor) {
		this.mamDescritor = mamDescritor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SVI_SEQ", nullable = true)
	public MamItemSinalVital getMamItemSinalVital() {
		return mamItemSinalVital;
	}

	public void setMamItemSinalVital(MamItemSinalVital mamItemSinalVital) {
		this.mamItemSinalVital = mamItemSinalVital;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDM_SEQ", nullable = true)
	public MamItemMedicacao getMamItemMedicacao() {
		return mamItemMedicacao;
	}

	public void setMamItemMedicacao(MamItemMedicacao mamItemMedicacao) {
		this.mamItemMedicacao = mamItemMedicacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMS_SEQ", nullable = true)
	public MamItemExame getMamItemExame() {
		return mamItemExame;
	}

	public void setMamItemExame(MamItemExame mamItemExame) {
		this.mamItemExame = mamItemExame;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITG_SEQ", nullable = true)
	public MamItemGeral getMamItemGeral() {
		return mamItemGeral;
	}

	public void setMamItemGeral(MamItemGeral mamItemGeral) {
		this.mamItemGeral = mamItemGeral;
	}

	@Column(name = "ICE_SEQ", nullable = true)
	public Short getIceSeq() {
		return iceSeq;
	}

	public void setIceSeq(Short iceSeq) {
		this.iceSeq = iceSeq;
	}

	public enum Fields {
		SEQ("seq"), //
		IND_SITUACAO("indSituacao"), //
		MAM_DESCRITOR("mamDescritor"), //
		MAM_ITEM_SINAL_VITAL("mamItemSinalVital"), //
		MAM_ITEM_MEDICACAO("mamItemMedicacao"), //
		MAM_ITEM_EXAME("mamItemExame"), //
		MAM_ITEM_GERAL("mamItemGeral"), //
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
