/* ************************************************* */
/** COMENTARIO
 * 
 * @author twickert
 * 
 * ATENÇÃO: Não recolocar o campo NUMERO_AP nesse POJO
 * O NUMERO_AP é apenas gravado na tabela AEL_ANATOMO_PATOLOGICOS.NUMERO_AP
 * Dessa forma evita redundancia.
 * 
 * Exemplo de query alterada para buscar um nro AP a partir de uma solicitação
 * 
 * select lum.numero_ap 
	from AGH.AEL_ITEM_SOLICITACAO_EXAMES ise
	inner join AEL_EXAME_AP_ITEM_SOLICS lul on (lul.ISE_SOE_SEQ = ise.soe_seq and lul.ISE_SEQP = ise.seqp)
	inner join AEL_EXAME_APS lux on (lux.seq = lul.LUX_SEQ)
	inner join AEL_ANATOMO_PATOLOGICOS lum on (lum.seq = lux.lum_seq)
	where 
	ise.SOE_SEQ = 123456
 * 
 */
/* ************************************************* */

package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.dominio.DominioDocumentoPaciente;
import br.gov.mec.aghu.dominio.DominioMomentoAgendamento;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioUtilizacaoSala;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mbcCrgSq1", sequenceName="AGH.MBC_CRG_SQ1", allocationSize = 1)
@Table(name = "MBC_CIRURGIAS", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = {
		"DATA", "NRO_AGENDA" }))
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class MbcCirurgias extends BaseEntitySeq<Integer> implements java.io.Serializable {

	// TODO Implementar triggers (não foi implementado, pois o mapeamento
	// foi feito na implementação do módulo de internação)

	private static final long serialVersionUID = -3037826267323781731L;
	private Integer seq;
	private Date data;
	private Short numeroAgenda;
	private DominioNaturezaFichaAnestesia naturezaAgenda;
	private Date criadoEm;
	private Boolean contaminacao;
	private Boolean digitaNotaSala;
	private Boolean precaucaoEspecial;
	private DominioOrigemPacienteCirurgia origemPacienteCirurgia;
	private Boolean utilizaO2;
	private Boolean utilizaProAzot;
	private Boolean aplicaListaCirurgiaSegura;
	private MbcMotivoAtraso motivoAtraso;
	private MbcSalaCirurgica salaCirurgica;
	private MbcMotivoCancelamento motivoCancelamento;
	private MbcMotivoDemoraSalaRec motivoDemoraSalaRecuperacao;
	private MbcDestinoPaciente destinoPaciente;
	private Date dataPrevisaoInicio;
	private Date dataPrevisaoFim;
	private Short tempoUtilizacaoO2;
	private Short tempoUtilizacaoProAzot;
	private Date dataInicioAnestesia;
	private Date dataFimAnestesia;
	private Date dataInicioCirurgia;
	private Date dataFimCirurgia;
	private Date dataEntradaSala;
	private Date dataSaidaSala;
	private Date dataEntradaSr;
	private Date dataSaidaSr;
	private DominioDocumentoPaciente documentoPaciente;
	private Date dataDigitacaoNotaSala;
	private DominioAsa asa;
	private Date dataUltimaAtualizacaoNotaSala;
	private Short tempoPrevistoHoras;
	private Byte tempoPrevistoMinutos;
	private Boolean temDescricao;
	private MbcValorValidoCanc valorValidoCanc;
	private MbcQuestao questao;
	private String complementoCanc;
	private MbcAgendas agenda;
	private Boolean overbooking;
	private Date dataInicioOrdem;

	// Esse campo possui dominio que permite informar somente '9S', porém isso
	// significa 9º andar / ala Sul, assim não foi implementado um dominio.
	// Existe o issue no RedMine http://redmine.mec.gov.br/issues/2948 para essa
	// situação. Por isso foi mapeado String.
	private String origemIntLocal;

	private DominioMomentoAgendamento momentoAgenda;
	private DominioUtilizacaoSala utilizacaoSala;
	private MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala;
	private AelProjetoPesquisas projetoPesquisa;
	private DominioSituacaoCirurgia situacao;
	private AghEspecialidades especialidade;
	private AghUnidadesFuncionais unidadeFuncional;
	private AipPacientes paciente;
	private FccCentroCustos centroCustos;
	private RapServidores servidor;
	private AghAtendimentos atendimento;
	private FatConvenioSaudePlano convenioSaudePlano;
	private FatConvenioSaude convenioSaude;
	
	private Short sciUnfSeq;
	private Short sciSeqp;
	private Integer version;
	private Boolean indPrc;
	private Boolean atbProf = Boolean.FALSE;
	private Date dtHrAtbProf;

	private Set<MbcProfCirurgias> profCirurgias;
	private Set<MbcProcEspPorCirurgias> procEspPorCirurgias;
	private Set<MbcDescricaoCirurgica> descricoesCirurgicas;
	private Set<MbcAnestesiaCirurgias> anestesiaCirurgicas;
	private Set<MbcSolicitacaoEspExecCirg> solicitacaoEspExecCirurgia;
	private Set<MbcMatOrteseProtCirg> materialOrteseProtCirur;
	private Set<MbcEquipamentoUtilCirg> mbcEquipamentoUtilCirgs;
	private Set<PdtDescricao> pdtDescricaos;

	public MbcCirurgias() {
	}

	public MbcCirurgias(Integer seq) {
		this.seq = seq;
	}
	
	public MbcCirurgias(Integer seq, RapServidores servidor,
			AghEspecialidades especialidade, AipPacientes paciente,
			AghUnidadesFuncionais unidadeFuncional, Date data,
			Short numeroAgenda, DominioSituacaoCirurgia situacao,
			DominioNaturezaFichaAnestesia naturezaAgenda, Date criadoEm, Boolean contaminacao,
			Boolean digitaNotaSala, Boolean precaucaoEspecial,
			DominioOrigemPacienteCirurgia origemPacienteCirurgia,
			Boolean utilizaO2, Boolean utilizaProAzot, Boolean overbooking) {
		this.seq = seq;
		this.servidor = servidor;
		this.especialidade = especialidade;
		this.paciente = paciente;
		this.unidadeFuncional = unidadeFuncional;
		this.data = data;
		this.numeroAgenda = numeroAgenda;
		this.situacao = situacao;
		this.naturezaAgenda = naturezaAgenda;
		this.criadoEm = criadoEm;
		this.contaminacao = contaminacao;
		this.digitaNotaSala = digitaNotaSala;
		this.precaucaoEspecial = precaucaoEspecial;
		this.origemPacienteCirurgia = origemPacienteCirurgia;
		this.utilizaO2 = utilizaO2;
		this.utilizaProAzot = utilizaProAzot;
		this.overbooking = overbooking;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcCrgSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA", nullable = false, unique = true)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name = "NRO_AGENDA", nullable = false, precision = 4, scale = 0, unique = true)
	public Short getNumeroAgenda() {
		return this.numeroAgenda;
	}

	public void setNumeroAgenda(Short numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}

	@Column(name = "SITUACAO", nullable = false, length = 4)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoCirurgia getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoCirurgia situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "CONTAMINACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getContaminacao() {
		return this.contaminacao;
	}

	public void setContaminacao(Boolean contaminacao) {
		this.contaminacao = contaminacao;
	}
	
	
	@Column(name = "IND_PRC", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPrc() {
		return this.indPrc;
	}

	public void setIndPrc(Boolean indPrc) {
		this.indPrc = indPrc;
	}

	
	@Column(name = "IND_DIGT_NOTA_SALA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getDigitaNotaSala() {
		return this.digitaNotaSala;
	}

	public void setDigitaNotaSala(Boolean digitaNotaSala) {
		this.digitaNotaSala = digitaNotaSala;
	}

	@Column(name = "IND_PRECAUCAO_ESPECIAL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPrecaucaoEspecial() {
		return this.precaucaoEspecial;
	}

	public void setPrecaucaoEspecial(Boolean precaucaoEspecial) {
		this.precaucaoEspecial = precaucaoEspecial;
	}

	@Column(name = "ORIGEM_PAC_CIRG", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioOrigemPacienteCirurgia getOrigemPacienteCirurgia() {
		return this.origemPacienteCirurgia;
	}

	public void setOrigemPacienteCirurgia(
			DominioOrigemPacienteCirurgia origemPacienteCirurgia) {
		this.origemPacienteCirurgia = origemPacienteCirurgia;
	}

	@Column(name = "IND_UTLZ_O2", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUtilizaO2() {
		return utilizaO2;
	}

	public void setUtilizaO2(Boolean utilizaO2) {
		this.utilizaO2 = utilizaO2;
	}

	@Column(name = "IND_UTLZ_PRO_AZOT", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUtilizaProAzot() {
		return this.utilizaProAzot;
	}

	public void setUtilizaProAzot(Boolean utilizaProAzot) {
		this.utilizaProAzot = utilizaProAzot;
	}
	
	@Column(name = "IND_APL_LST_CRG_SEG", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAplicaListaCirurgiaSegura() {
		return aplicaListaCirurgiaSegura;
	}
	
	public void setAplicaListaCirurgiaSegura(Boolean aplicaListaCirurgiaSegura) {
		this.aplicaListaCirurgiaSegura = aplicaListaCirurgiaSegura;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MOA_SEQ")
	public MbcMotivoAtraso getMotivoAtraso() {
		return this.motivoAtraso;
	}

	public void setMotivoAtraso(MbcMotivoAtraso motivoAtraso) {
		this.motivoAtraso = motivoAtraso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SCI_UNF_SEQ", referencedColumnName = "UNF_SEQ"),
			@JoinColumn(name = "SCI_SEQP", referencedColumnName = "SEQP") })
	public MbcSalaCirurgica getSalaCirurgica() {
		return this.salaCirurgica;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MTC_SEQ")
	public MbcMotivoCancelamento getMotivoCancelamento() {
		return this.motivoCancelamento;
	}

	public void setMotivoCancelamento(MbcMotivoCancelamento motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSR_SEQ")
	public MbcMotivoDemoraSalaRec getMotivoDemoraSalaRecuperacao() {
		return this.motivoDemoraSalaRecuperacao;
	}

	public void setMotivoDemoraSalaRecuperacao(
			MbcMotivoDemoraSalaRec motivoDemoraSalaRecuperacao) {
		this.motivoDemoraSalaRecuperacao = motivoDemoraSalaRecuperacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DPA_SEQ")
	public MbcDestinoPaciente getDestinoPaciente() {
		return this.destinoPaciente;
	}

	public void setDestinoPaciente(MbcDestinoPaciente destinoPaciente) {
		this.destinoPaciente = destinoPaciente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_PREV_INICIO")
	public Date getDataPrevisaoInicio() {
		return this.dataPrevisaoInicio;
	}

	public void setDataPrevisaoInicio(Date dataPrevisaoInicio) {
		this.dataPrevisaoInicio = dataPrevisaoInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_PREV_FIM")
	public Date getDataPrevisaoFim() {
		return this.dataPrevisaoFim;
	}

	public void setDataPrevisaoFim(Date dataPrevisaoFim) {
		this.dataPrevisaoFim = dataPrevisaoFim;
	}

	@Column(name = "TEMPO_UTLZ_O2", precision = 4, scale = 0)
	public Short getTempoUtilizacaoO2() {
		return this.tempoUtilizacaoO2;
	}

	public void setTempoUtilizacaoO2(Short tempoUtilizacaoO2) {
		this.tempoUtilizacaoO2 = tempoUtilizacaoO2;
	}

	@Column(name = "TEMPO_UTLZ_PRO_AZOT", precision = 4, scale = 0)
	public Short getTempoUtilizacaoProAzot() {
		return this.tempoUtilizacaoProAzot;
	}

	public void setTempoUtilizacaoProAzot(Short tempoUtilizacaoProAzot) {
		this.tempoUtilizacaoProAzot = tempoUtilizacaoProAzot;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_ANEST")
	public Date getDataInicioAnestesia() {
		return this.dataInicioAnestesia;
	}

	public void setDataInicioAnestesia(Date dataInicioAnestesia) {
		this.dataInicioAnestesia = dataInicioAnestesia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM_ANEST")
	public Date getDataFimAnestesia() {
		return this.dataFimAnestesia;
	}

	public void setDataFimAnestesia(Date dataFimAnestesia) {
		this.dataFimAnestesia = dataFimAnestesia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_CIRG")
	public Date getDataInicioCirurgia() {
		return this.dataInicioCirurgia;
	}

	public void setDataInicioCirurgia(Date dataInicioCirurgia) {
		this.dataInicioCirurgia = dataInicioCirurgia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM_CIRG")
	public Date getDataFimCirurgia() {
		return this.dataFimCirurgia;
	}

	public void setDataFimCirurgia(Date dataFimCirurgia) {
		this.dataFimCirurgia = dataFimCirurgia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ENTRADA_SALA")
	public Date getDataEntradaSala() {
		return this.dataEntradaSala;
	}

	public void setDataEntradaSala(Date dataEntradaSala) {
		this.dataEntradaSala = dataEntradaSala;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_SAIDA_SALA")
	public Date getDataSaidaSala() {
		return this.dataSaidaSala;
	}

	public void setDataSaidaSala(Date dataSaidaSala) {
		this.dataSaidaSala = dataSaidaSala;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ENTRADA_SR")
	public Date getDataEntradaSr() {
		return this.dataEntradaSr;
	}

	public void setDataEntradaSr(Date dataEntradaSr) {
		this.dataEntradaSr = dataEntradaSr;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_SAIDA_SR")
	public Date getDataSaidaSr() {
		return this.dataSaidaSr;
	}

	public void setDataSaidaSr(Date dataSaidaSr) {
		this.dataSaidaSr = dataSaidaSr;
	}

	@Column(name = "DOCUMENTO_PAC", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioDocumentoPaciente getDocumentoPaciente() {
		return this.documentoPaciente;
	}

	public void setDocumentoPaciente(DominioDocumentoPaciente documentoPaciente) {
		this.documentoPaciente = documentoPaciente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_DIGIT_NOTA_SALA")
	public Date getDataDigitacaoNotaSala() {
		return this.dataDigitacaoNotaSala;
	}

	public void setDataDigitacaoNotaSala(Date dataDigitacaoNotaSala) {
		this.dataDigitacaoNotaSala = dataDigitacaoNotaSala;
	}

	@Column(name = "ASA")
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioAsa") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioAsa getAsa() {
		return this.asa;
	}

	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ULT_ATLZ_NOTA_SALA", length = 7)
	public Date getDataUltimaAtualizacaoNotaSala() {
		return this.dataUltimaAtualizacaoNotaSala;
	}

	public void setDataUltimaAtualizacaoNotaSala(
			Date dataUltimaAtualizacaoNotaSala) {
		this.dataUltimaAtualizacaoNotaSala = dataUltimaAtualizacaoNotaSala;
	}

	@Column(name = "TEMPO_PREV_HRS", precision = 3, scale = 0)
	public Short getTempoPrevistoHoras() {
		return this.tempoPrevistoHoras;
	}

	public void setTempoPrevistoHoras(Short tempoPrevistoHoras) {
		this.tempoPrevistoHoras = tempoPrevistoHoras;
	}

	@Column(name = "TEMPO_PREV_MIN", precision = 2, scale = 0)
	public Byte getTempoPrevistoMinutos() {
		return this.tempoPrevistoMinutos;
	}

	public void setTempoPrevistoMinutos(Byte tempoPrevistoMinutos) {
		this.tempoPrevistoMinutos = tempoPrevistoMinutos;
	}

	@Column(name = "IND_TEM_DESCRICAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getTemDescricao() {
		return this.temDescricao;
	}

	public void setTemDescricao(Boolean temDescricao) {
		this.temDescricao = temDescricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "VVC_QES_MTC_SEQ", referencedColumnName = "QES_MTC_SEQ"),
			@JoinColumn(name = "VVC_QES_SEQP", referencedColumnName = "QES_SEQP"),
			@JoinColumn(name = "VVC_SEQP", referencedColumnName = "SEQP") })
	public MbcValorValidoCanc getValorValidoCanc() {
		return this.valorValidoCanc;
	}

	public void setValorValidoCanc(MbcValorValidoCanc valorValidoCanc) {
		this.valorValidoCanc = valorValidoCanc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "QES_MTC_SEQ", referencedColumnName = "MTC_SEQ"),
			@JoinColumn(name = "QES_SEQP", referencedColumnName = "SEQP") })
	public MbcQuestao getQuestao() {
		return this.questao;
	}

	public void setQuestao(MbcQuestao questao) {
		this.questao = questao;
	}

	@Column(name = "COMPLEMENTO_CANC", length = 100)
	@Length(max = 100)
	public String getComplementoCanc() {
		return this.complementoCanc;
	}

	public void setComplementoCanc(String complementoCanc) {
		this.complementoCanc = complementoCanc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGD_SEQ")
	public MbcAgendas getAgenda() {
		return this.agenda;
	}

	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
	}

	@Column(name = "IND_OVERBOOKING", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getOverbooking() {
		return this.overbooking;
	}

	public void setOverbooking(Boolean overbooking) {
		this.overbooking = overbooking;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_ORDEM")
	public Date getDataInicioOrdem() {
		return this.dataInicioOrdem;
	}

	public void setDataInicioOrdem(Date dataInicioOrdem) {
		this.dataInicioOrdem = dataInicioOrdem;
	}

	@Column(name = "ORIGEM_INT_LOCAL", length = 3)
	@Length(max = 3)
	public String getOrigemIntLocal() {
		return this.origemIntLocal;
	}

	public void setOrigemIntLocal(String origemIntLocal) {
		this.origemIntLocal = origemIntLocal;
	}

	@Column(name = "MOMENTO_AGEND", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioMomentoAgendamento getMomentoAgenda() {
		return this.momentoAgenda;
	}

	public void setMomentoAgenda(DominioMomentoAgendamento momentoAgenda) {
		this.momentoAgenda = momentoAgenda;
	}

	@Column(name = "UTILIZACAO_SALA", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioUtilizacaoSala getUtilizacaoSala() {
		return this.utilizacaoSala;
	}

	public void setUtilizacaoSala(DominioUtilizacaoSala utilizacaoSala) {
		this.utilizacaoSala = utilizacaoSala;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SPE_SEQ")
	public MbcSolicitacaoCirurgiaPosEscala getSolicitacaoCirurgiaPosEscala() {
		return this.solicitacaoCirurgiaPosEscala;
	}

	public void setSolicitacaoCirurgiaPosEscala(
			MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala) {
		this.solicitacaoCirurgiaPosEscala = solicitacaoCirurgiaPosEscala;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PJQ_SEQ")
	public AelProjetoPesquisas getProjetoPesquisa() {
		return this.projetoPesquisa;
	}

	public void setProjetoPesquisa(AelProjetoPesquisas projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PAC_CODIGO", nullable = false, referencedColumnName = "CODIGO")
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "UNF_SEQ", nullable = false, referencedColumnName = "SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores sevidor) {
		this.servidor = sevidor;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ESP_SEQ", nullable = false, referencedColumnName = "SEQ")
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "CSP_CNV_CODIGO", referencedColumnName = "CNV_CODIGO", nullable = false),
			@JoinColumn(name = "CSP_SEQ", referencedColumnName = "SEQ", nullable = false) })
	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CSP_CNV_CODIGO", nullable = false, insertable = false, updatable = false)
	public FatConvenioSaude getConvenioSaude() {
		return this.convenioSaude;
	}

	public void setConvenioSaude(FatConvenioSaude fatConveniosSaude) {
		this.convenioSaude = fatConveniosSaude;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cirurgia")
	public Set<MbcProfCirurgias> getProfCirurgias() {
		return profCirurgias;
	}

	public void setProfCirurgias(Set<MbcProfCirurgias> profCirurgias) {
		this.profCirurgias = profCirurgias;
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cirurgia")
	public Set<MbcProcEspPorCirurgias> getProcEspPorCirurgias() {
		return procEspPorCirurgias;
	}

	public void setProcEspPorCirurgias(
			Set<MbcProcEspPorCirurgias> procEspPorCirurgias) {
		this.procEspPorCirurgias = procEspPorCirurgias;
	}

	public enum Fields {
		PAC_CODIGO("paciente.codigo"), 
		PACIENTE("paciente"),
		PAC_CODIGO_CODIGO("paciente.codigo"),
		DATA("data"), 
		SITUACAO("situacao"), 
		UNIDADE_FUNCIONAL("unidadeFuncional"), 
		PRONTUARIO("paciente.prontuario"), 
		UNF_SEQ("unidadeFuncional.seq"), 
		SCI_SEQP("sciSeqp"), 
		DTHR_INICIO_CIRG("dataInicioCirurgia"), 
		DTHR_FIM_CIRG("dataFimCirurgia"), 
		CSP_CNV_CODIGO("convenioSaude.codigo"), 
		NOME("paciente.nome"), 
		DATA_NASCIMENTO("paciente.dtNascimento"), 
		ORIGEM_INT_LOCAL("origemIntLocal"), 
		ORIGEM_PAC_CIRG("origemPacienteCirurgia"), 
		PRNT_ATIVO("paciente.prntAtivo"), 
		IND_PRECAUCAO_ESPECIAL("precaucaoEspecial"), 
		SEQ("seq"), VOLUMES("paciente.volumes"), 
		CRIADO_EM("criadoEm"), 
		PROF_CIRURGIAS("profCirurgias"), 
		ANEST_CIRURGIAS("anestesiaCirurgicas"),
		ATENDIMENTO_SEQ("atendimento.seq"), 
		ATENDIMENTO("atendimento"), 
		PROC_ESP_POR_CIRURGIAS("procEspPorCirurgias"), 
		PROC_CIRURGICO("procEspPorCirurgias.procedimentoCirurgico"), 
		IND_DIGT_NOTA_SALA("digitaNotaSala"), 
		CONVENIO_SAUDE("convenioSaude"), 
		CSP_SEQ("convenioSaudePlano.id.seq"),
		CONVENIO_SAUDE_PLANO("convenioSaudePlano"), 
		DESCRICOES_CIRURGIAS("descricoesCirurgicas"),
		PROJETO_PESQUISAS("projetoPesquisa"), 
		ESPECIALIDADE("especialidade"),
		ESP_SEQ("especialidade.seq"),
		SERVIDOR("servidor"), 
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		AGD_SEQ("agenda.seq"),
		MOTIVO_CANCELAMENTO("motivoCancelamento"), 
		MTC_SEQ("motivoCancelamento.seq"),
		TEM_DESCRICAO("temDescricao"),
		IND_TEM_DESCRICAO("temDescricao"),
		DIGITA_NOTA_SALA("digitaNotaSala"),
		QUESTAO("questao"),
		COMPLEMENTO_CANC("complementoCanc"), 
		SCI_UNFSEQ("salaCirurgica.id.unfSeq"), 
		SALA_CIRURGICA("salaCirurgica"),
		DTHR_PREV_INICIO("dataPrevisaoInicio"),
		NATUREZA_AGEND("naturezaAgenda"),
		DTHR_ENTRADA_SR("dataEntradaSr"),
		DTHR_SAIDA_SR("dataSaidaSr"), 
		DTHR_INICIO_ORDEM("dataInicioOrdem"),
		QES_MTC_SEQ("questao.id.mtcSeq"),
		QES_SEQP("questao.id.seqp"),
		VVC_QES_MTC_SEQ("valorValidoCanc.id.qesMtcSeq"),
		VVC_QES_SEQP("valorValidoCanc.id.qesSeqp"),
		VVC_SEQP("valorValidoCanc.id.seqp"),
		DTHR_PREVISAO_FIM("dataPrevisaoFim"),
		AGENDA("agenda"),
		NUMERO_AGENDA("numeroAgenda"),
		DPA_SEQ("destinoPaciente.seq"),
		DESTINO_PACIENTE("destinoPaciente"),
		SCI_UNF_SEQ("sciUnfSeq"),
		CENTRO_CUSTO("centroCustos"),
		APLICA_LISTA_CIRURGIA_SEGURA("aplicaListaCirurgiaSegura"),
		EQUIPAMENTOS_UTIL_CIRGS("mbcEquipamentoUtilCirgs"),
		PDT_DESCRICAO("pdtDescricaos"),
		UTILIZACAO_SALA("utilizacaoSala"),
		DATA_INICIO_ANESTESIA("dataInicioAnestesia"),
		DATA_FIM_ANESTESIA("dataFimAnestesia"),
		DTHR_ENTRADA_SALA("dataEntradaSala"),
		DTHR_SAIDA_SALA("dataSaidaSala"),
		COD_PACIENTE("paciente.codigo"),
		AGENDA_ESPPROCCIRGS("agenda.espProcCirgs"); 

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof MbcCirurgias)) {
			return false;
		}
		MbcCirurgias other = (MbcCirurgias) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public void setDescricoesCirurgicas(Set<MbcDescricaoCirurgica> descricoesCirurgicas) {
		this.descricoesCirurgicas = descricoesCirurgicas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCirurgias")
	public Set<MbcDescricaoCirurgica> getDescricoesCirurgicas() {
		return descricoesCirurgicas;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cirurgia")
	public Set<MbcAnestesiaCirurgias> getAnestesiaCirurgicas() {
		return anestesiaCirurgicas;
	}

	public void setAnestesiaCirurgicas(Set<MbcAnestesiaCirurgias> anestesiaCirurgicas) {
		this.anestesiaCirurgicas = anestesiaCirurgicas;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCirurgias")
	public Set<MbcSolicitacaoEspExecCirg> getSolicitacaoEspExecCirurgia() {
		return solicitacaoEspExecCirurgia;
	}

	public void setSolicitacaoEspExecCirurgia(
			Set<MbcSolicitacaoEspExecCirg> solicitacaoEspExecCirurgia) {
		this.solicitacaoEspExecCirurgia = solicitacaoEspExecCirurgia;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCirurgias")
	public Set<MbcMatOrteseProtCirg> getMaterialOrteseProtCirur() {
		return materialOrteseProtCirur;
	}

	public void setMaterialOrteseProtCirur(
			Set<MbcMatOrteseProtCirg> materialOrteseProtCirur) {
		this.materialOrteseProtCirur = materialOrteseProtCirur;
	}

	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "NATUREZA_AGEND", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioNaturezaFichaAnestesia getNaturezaAgenda() {
		return naturezaAgenda;
	}

	public void setNaturezaAgenda(DominioNaturezaFichaAnestesia naturezaAgenda) {
		this.naturezaAgenda = naturezaAgenda;
	}

	@Column(name = "SCI_UNF_SEQ", insertable=false, updatable=false)
	public Short getSciUnfSeq() {
		return sciUnfSeq;
	}
	
	@Column(name = "SCI_SEQP", insertable=false, updatable=false)
	public Short getSciSeqp() {
		return sciSeqp;
	}

	public void setSciUnfSeq(Short sciUnfSeq) {
		this.sciUnfSeq = sciUnfSeq;
	}

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public void setMbcEquipamentoUtilCirgs(Set<MbcEquipamentoUtilCirg> mbcEquipamentoUtilCirgs) {
		this.mbcEquipamentoUtilCirgs = mbcEquipamentoUtilCirgs;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCirurgias")
	public Set<MbcEquipamentoUtilCirg> getMbcEquipamentoUtilCirgs() {
		return mbcEquipamentoUtilCirgs;
	}

	public void setPdtDescricaos(Set<PdtDescricao> pdtDescricaos) {
		this.pdtDescricaos = pdtDescricaos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCirurgias")
	public Set<PdtDescricao> getPdtDescricaos() {
		return pdtDescricaos;
	}
	
	@Column(name = "ATB_PROF", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAtbProf() {
		return this.atbProf;
	}

	public void setAtbProf(Boolean atbProf) {
		this.atbProf = atbProf;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ATB_PROF")
	public Date getDtHrAtbProf() {
		return this.dtHrAtbProf;
	}

	public void setDtHrAtbProf(Date dtHrAtbProf) {
		this.dtHrAtbProf = dtHrAtbProf;
	}
}