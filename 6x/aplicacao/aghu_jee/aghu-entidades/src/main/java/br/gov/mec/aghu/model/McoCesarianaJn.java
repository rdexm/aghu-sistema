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

import br.gov.mec.aghu.dominio.DominioContaminacaoCesariana;
import br.gov.mec.aghu.dominio.DominioHisterorrafia;
import br.gov.mec.aghu.dominio.DominioHisterotomia;
import br.gov.mec.aghu.dominio.DominioLaparotomia;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aghMcoCsrJnSeq", sequenceName="AGH.MCO_CSR_JN_SEQ", allocationSize = 1)
@Table(name = "MCO_CESARIANAS_JN", schema = "AGH")
@Immutable
public class McoCesarianaJn extends BaseJournal {

	private static final long serialVersionUID = -6367153348473912774L;
	private Short hrDuracao;
	private DominioContaminacaoCesariana contaminacao;
	private DominioLaparotomia laparotomia;
	private DominioHisterotomia histerotomia;
	private DominioHisterorrafia histerorrafia;
	private Boolean indLaqueaduraTubaria;
	private Boolean indRafiaPeritonial;
	private Boolean indLavagemCavidade;
	private Boolean indDrenos;
	private Short nasGsoSeqp;
	private Integer nasGsoPacCodigo;
	private Integer nasSeqp;
	private Date criadoEm;
	private Date dthrPrevInicio;
	private Date dthrIncisao;
	private Date dthrIndicacao;
	private Short sciUnfSeq;
	private Short sciSeqp;

	public McoCesarianaJn() {
	}

	public McoCesarianaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short nasGsoSeqp, Integer nasGsoPacCodigo,
			Integer nasSeqp) {

		this.nasGsoSeqp = nasGsoSeqp;
		this.nasGsoPacCodigo = nasGsoPacCodigo;
		this.nasSeqp = nasSeqp;
	}

	public McoCesarianaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short hrDuracao, DominioContaminacaoCesariana contaminacao,
			DominioLaparotomia laparotomia, DominioHisterotomia histerotomia, DominioHisterorrafia histerorrafia, Boolean indLaqueaduraTubaria,
			Boolean indRafiaPeritonial, Boolean indLavagemCavidade, Boolean indDrenos, Short nasGsoSeqp, Integer nasGsoPacCodigo,
			Integer nasSeqp, Date criadoEm, Date dthrPrevInicio, Date dthrIncisao, Date dthrIndicacao, Short sciUnfSeq, Short sciSeqp) {

		this.hrDuracao = hrDuracao;
		this.contaminacao = contaminacao;
		this.laparotomia = laparotomia;
		this.histerotomia = histerotomia;
		this.histerorrafia = histerorrafia;
		this.indLaqueaduraTubaria = indLaqueaduraTubaria;
		this.indRafiaPeritonial = indRafiaPeritonial;
		this.indLavagemCavidade = indLavagemCavidade;
		this.indDrenos = indDrenos;
		this.nasGsoSeqp = nasGsoSeqp;
		this.nasGsoPacCodigo = nasGsoPacCodigo;
		this.nasSeqp = nasSeqp;
		this.criadoEm = criadoEm;
		this.dthrPrevInicio = dthrPrevInicio;
		this.dthrIncisao = dthrIncisao;
		this.dthrIndicacao = dthrIndicacao;
		this.sciUnfSeq = sciUnfSeq;
		this.sciSeqp = sciSeqp;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMcoCsrJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "HR_DURACAO")
	public Short getHrDuracao() {
		return this.hrDuracao;
	}

	public void setHrDuracao(Short hrDuracao) {
		this.hrDuracao = hrDuracao;
	}

	@Column(name = "CONTAMINACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioContaminacaoCesariana getContaminacao() {
		return this.contaminacao;
	}

	public void setContaminacao(DominioContaminacaoCesariana contaminacao) {
		this.contaminacao = contaminacao;
	}

	@Column(name = "LAPAROTOMIA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioLaparotomia getLaparotomia() {
		return this.laparotomia;
	}

	public void setLaparotomia(DominioLaparotomia laparotomia) {
		this.laparotomia = laparotomia;
	}

	@Column(name = "HISTEROTOMIA", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioHisterotomia getHisterotomia() {
		return this.histerotomia;
	}

	public void setHisterotomia(DominioHisterotomia histerotomia) {
		this.histerotomia = histerotomia;
	}

	@Column(name = "HISTERORRAFIA", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioHisterorrafia getHisterorrafia() {
		return this.histerorrafia;
	}

	public void setHisterorrafia(DominioHisterorrafia histerorrafia) {
		this.histerorrafia = histerorrafia;
	}

	@Column(name = "IND_LAQUEADURA_TUBARIA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndLaqueaduraTubaria() {
		return this.indLaqueaduraTubaria;
	}

	public void setIndLaqueaduraTubaria(Boolean indLaqueaduraTubaria) {
		this.indLaqueaduraTubaria = indLaqueaduraTubaria;
	}

	@Column(name = "IND_RAFIA_PERITONIAL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRafiaPeritonial() {
		return this.indRafiaPeritonial;
	}

	public void setIndRafiaPeritonial(Boolean indRafiaPeritonial) {
		this.indRafiaPeritonial = indRafiaPeritonial;
	}

	@Column(name = "IND_LAVAGEM_CAVIDADE", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndLavagemCavidade() {
		return this.indLavagemCavidade;
	}

	public void setIndLavagemCavidade(Boolean indLavagemCavidade) {
		this.indLavagemCavidade = indLavagemCavidade;
	}

	@Column(name = "IND_DRENOS", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDrenos() {
		return this.indDrenos;
	}

	public void setIndDrenos(Boolean indDrenos) {
		this.indDrenos = indDrenos;
	}

	@Column(name = "NAS_GSO_SEQP", nullable = false)
	public Short getNasGsoSeqp() {
		return this.nasGsoSeqp;
	}

	public void setNasGsoSeqp(Short nasGsoSeqp) {
		this.nasGsoSeqp = nasGsoSeqp;
	}

	@Column(name = "NAS_GSO_PAC_CODIGO", nullable = false)
	public Integer getNasGsoPacCodigo() {
		return this.nasGsoPacCodigo;
	}

	public void setNasGsoPacCodigo(Integer nasGsoPacCodigo) {
		this.nasGsoPacCodigo = nasGsoPacCodigo;
	}

	@Column(name = "NAS_SEQP", nullable = false)
	public Integer getNasSeqp() {
		return this.nasSeqp;
	}

	public void setNasSeqp(Integer nasSeqp) {
		this.nasSeqp = nasSeqp;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_PREV_INICIO", length = 29)
	public Date getDthrPrevInicio() {
		return this.dthrPrevInicio;
	}

	public void setDthrPrevInicio(Date dthrPrevInicio) {
		this.dthrPrevInicio = dthrPrevInicio;
	}
	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_INCISAO", length = 7)
	public Date getDthrIncisao() {
		return dthrIncisao;
	}

	public void setDthrIncisao(Date dthrIncisao) {
		this.dthrIncisao = dthrIncisao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_INDICACAO", length = 7)
	public Date getDthrIndicacao() {
		return dthrIndicacao;
	}

	public void setDthrIndicacao(Date dthrIndicacao) {
		this.dthrIndicacao = dthrIndicacao;
	}
	@Column(name = "SCI_UNF_SEQ")
	public Short getSciUnfSeq() {
		return this.sciUnfSeq;
	}

	public void setSciUnfSeq(Short sciUnfSeq) {
		this.sciUnfSeq = sciUnfSeq;
	}

	@Column(name = "SCI_SEQP")
	public Short getSciSeqp() {
		return this.sciSeqp;
	}

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public enum Fields {

		HR_DURACAO("hrDuracao"),
		CONTAMINACAO("contaminacao"),
		LAPAROTOMIA("laparotomia"),
		HISTEROTOMIA("histerotomia"),
		HISTERORRAFIA("histerorrafia"),
		IND_LAQUEADURA_TUBARIA("indLaqueaduraTubaria"),
		IND_RAFIA_PERITONIAL("indRafiaPeritonial"),
		IND_LAVAGEM_CAVIDADE("indLavagemCavidade"),
		IND_DRENOS("indDrenos"),
		NAS_GSO_SEQP("nasGsoSeqp"),
		NAS_GSO_PAC_CODIGO("nasGsoPacCodigo"),
		NAS_SEQP("nasSeqp"),
		CRIADO_EM("criadoEm"),
		DTHR_PREV_INICIO("dthrPrevInicio"),
		SCI_UNF_SEQ("sciUnfSeq"),
		SCI_SEQP("sciSeqp");

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
