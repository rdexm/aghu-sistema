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

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mpmCduJnSeq", sequenceName="AGH.MPM_CDU_JN_SEQ", allocationSize = 1)
@Table(name = "MPM_CUIDADO_USUAIS_JN", schema = "AGH")
@Immutable
public class MpmCuidadoUsualJn extends BaseJournal {

	private static final long serialVersionUID = -5367883542096216568L;
	
	private Integer seq;
	private MpmTipoFrequenciaAprazamento mpmTipoFreqAprazamentos;
	private RapServidores rapServidores;
	private String descricao;
	private Boolean indImpMapaDietas;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Short frequencia;
	private Boolean indDigitaComplemento;
	private Boolean indCci;
	private Boolean indOutros;
	private Boolean indUsoQuimioterapia;
	private Boolean indUsoDialise;
	private Date tempo;
	private Boolean indSondaVesical;
	private Boolean indCpa;
	private Date alteradoEm;
	private RapServidores rapServidorMovimentado;

	public MpmCuidadoUsualJn() {
	}

	public MpmCuidadoUsualJn(Integer seq, RapServidores rapServidores,
			String descricao, Boolean indImpMapaDietas, Date criadoEm,
			DominioSituacao indSituacao, Boolean indDigitaComplemento,
			Boolean indCci, Boolean indOutros, Boolean indUsoQuimioterapia,
			Boolean indUsoDialise, Boolean indCpa, Date alteradoEm, RapServidores rapServidorMovimentado) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indImpMapaDietas = indImpMapaDietas;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indDigitaComplemento = indDigitaComplemento;
		this.indCci = indCci;
		this.indOutros = indOutros;
		this.indUsoQuimioterapia = indUsoQuimioterapia;
		this.indUsoDialise = indUsoDialise;
		this.indCpa = indCpa;
		this.alteradoEm = alteradoEm;
		this.rapServidorMovimentado = rapServidorMovimentado;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmCduJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", unique = true, nullable = false, precision = 6, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")
	public MpmTipoFrequenciaAprazamento getMpmTipoFreqAprazamentos() {
		return this.mpmTipoFreqAprazamentos;
	}

	public void setMpmTipoFreqAprazamentos(
			MpmTipoFrequenciaAprazamento mpmTipoFreqAprazamentos) {
		this.mpmTipoFreqAprazamentos = mpmTipoFreqAprazamentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { @JoinColumn(name = "SER_MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_IMP_MAPA_DIETAS", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImpMapaDietas() {
		return this.indImpMapaDietas;
	}

	public void setIndImpMapaDietas(Boolean indImpMapaDietas) {
		this.indImpMapaDietas = indImpMapaDietas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@Column(name = "IND_DIGITA_COMPLEMENTO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDigitaComplemento() {
		return this.indDigitaComplemento;
	}

	public void setIndDigitaComplemento(Boolean indDigitaComplemento) {
		this.indDigitaComplemento = indDigitaComplemento;
	}

	@Column(name = "IND_CCI", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCci() {
		return this.indCci;
	}

	public void setIndCci(Boolean indCci) {
		this.indCci = indCci;
	}

	@Column(name = "IND_OUTROS", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndOutros() {
		return this.indOutros;
	}

	public void setIndOutros(Boolean indOutros) {
		this.indOutros = indOutros;
	}

	@Column(name = "IND_USO_QUIMIOTERAPIA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoQuimioterapia() {
		return this.indUsoQuimioterapia;
	}

	public void setIndUsoQuimioterapia(Boolean indUsoQuimioterapia) {
		this.indUsoQuimioterapia = indUsoQuimioterapia;
	}

	@Column(name = "IND_USO_DIALISE", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoDialise() {
		return this.indUsoDialise;
	}

	public void setIndUsoDialise(Boolean indUsoDialise) {
		this.indUsoDialise = indUsoDialise;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO", length = 7)
	public Date getTempo() {
		return this.tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	@Column(name = "IND_SONDA_VESICAL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSondaVesical() {
		return this.indSondaVesical;
	}

	public void setIndSondaVesical(Boolean indSondaVesical) {
		this.indSondaVesical = indSondaVesical;
	}

	@Column(name = "IND_CPA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCpa() {
		return this.indCpa;
	}

	public void setIndCpa(Boolean indCpa) {
		this.indCpa = indCpa;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidorMovimentado() {
		return rapServidorMovimentado;
	}

	public void setRapServidorMovimentado(RapServidores rapServidorMovimentado) {
		this.rapServidorMovimentado = rapServidorMovimentado;
	}

}
