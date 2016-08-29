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
@SequenceGenerator(name="epeCuiJnSeq", sequenceName="AGH.EPE_CUI_JN_SEQ", allocationSize = 1)
@Table(name = "EPE_CUIDADOS_JN", schema = "AGH")
@Immutable
public class EpeCuidadosJn extends BaseJournal {

	private static final long serialVersionUID = -6278483548259116568L;
	
	private Short seq;
	private String descricao;
	private Boolean indSemDiagnostico;
	private DominioSituacao indSituacao;
	private Boolean indDigitaComplemento;
	private Date criadoEm;
	private RapServidores servidor;
	private String rotina;
	private String informacoesAdicionais;
	private Short frequencia;
	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private Boolean indCci;
	private Boolean indRotina;
	private Date tempo;
	private Date alteradoEm;
	private RapServidores rapServidorMovimentado;

	public EpeCuidadosJn() {
	}

	public EpeCuidadosJn(Short seq, String descricao, Boolean indSemDiagnostico,
			DominioSituacao indSituacao, Boolean indDigitaComplemento,
			Date criadoEm, RapServidores servidor, String rotina,
			String informacoesAdicionais, Short frequencia,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			Boolean indCci, Boolean indRotina, Date tempo,
			Date alteradoEm, RapServidores rapServidorMovimentado) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSemDiagnostico = indSemDiagnostico;
		this.indSituacao = indSituacao;
		this.indDigitaComplemento = indDigitaComplemento;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.rotina = rotina;
		this.informacoesAdicionais = informacoesAdicionais;
		this.frequencia = frequencia;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		this.indCci = indCci;
		this.indRotina = indRotina;
		this.tempo = tempo;
		this.alteradoEm = alteradoEm;
		this.rapServidorMovimentado = rapServidorMovimentado;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "epeCuiJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 200)
	@Length(max = 200)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SEM_DIAGNOSTICO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSemDiagnostico() {
		return this.indSemDiagnostico;
	}

	public void setIndSemDiagnostico(Boolean indSemDiagnostico) {
		this.indSemDiagnostico = indSemDiagnostico;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_DIGITA_COMPLEMENTO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDigitaComplemento() {
		return this.indDigitaComplemento;
	}

	public void setIndDigitaComplemento(Boolean indDigitaComplemento) {
		this.indDigitaComplemento = indDigitaComplemento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "ROTINA")
	public String getRotina() {
		return this.rotina;
	}

	public void setRotina(String rotina) {
		this.rotina = rotina;
	}

	@Column(name = "INFORMACOES_ADICIONAIS", length = 2000)
	@Length(max = 2000)
	public String getInformacoesAdicionais() {
		return this.informacoesAdicionais;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return tipoFrequenciaAprazamento;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	@Column(name = "IND_CCI", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCci() {
		return this.indCci;
	}

	public void setIndCci(Boolean indCci) {
		this.indCci = indCci;
	}

	@Column(name = "IND_ROTINA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRotina() {
		return this.indRotina;
	}

	public void setIndRotina(Boolean indRotina) {
		this.indRotina = indRotina;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO")
	public Date getTempo() {
		return this.tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
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
