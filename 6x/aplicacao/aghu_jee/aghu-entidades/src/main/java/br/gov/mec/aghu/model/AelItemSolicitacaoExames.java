/**
 * @author twickert
 * 
 * ATENÇÃO: Não recolocar o campo NUMERO_AP nesse POJO
 * Deve-se usar a tabela AEL_ANATOMO_PATOLOGICOS.NUMERO_AP
 * 
 * Exemplo de query alterada:
 * 
 * select ise.* 
	from AGH.AEL_ITEM_SOLICITACAO_EXAMES ise
	inner join AEL_EXAME_AP_ITEM_SOLICS lul on (lul.ISE_SOE_SEQ = ise.soe_seq and lul.ISE_SEQP = ise.seqp)
	inner join AEL_EXAME_APS lux on (lux.seq = lul.LUX_SEQ)
	inner join AEL_ANATOMO_PATOLOGICOS lum on (lum.seq = lux.lum_seq)
	where 
	lum.NUMERO_AP = 113
	and lum.LU2_SEQ = 1
 */
package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.core.persistence.BaseEntityId;
import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioIndImpressoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumarioItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoMensagemCaixaPostal;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;

@Entity
@Table(name = "AEL_ITEM_SOLICITACAO_EXAMES", schema = "AGH")
@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("PMD.ExcessiveClassLength")
public class AelItemSolicitacaoExames extends BaseEntityId<AelItemSolicitacaoExamesId> implements java.io.Serializable, IAelItemSolicitacaoExames {

	private static final long serialVersionUID = 4634424408119474877L;
	private AelItemSolicitacaoExamesId id;
	private AelItemSolicitacaoExames itemSolicitacaoExame;
	private AelSolicitacaoExames solicitacaoExame;
	private AelSitItemSolicitacoes situacaoItemSolicitacao;
	private AelExames exame;
	private AelMateriaisAnalises materialAnalise;
	private DominioTipoColeta tipoColeta;
	private Boolean indUsoO2;
	private Boolean indGeradoAutomatico;
	private AelIntervaloColeta intervaloColeta;
	private AelRegiaoAnatomica regiaoAnatomica;
	private DominioTipoTransporte tipoTransporte;
	private String descRegiaoAnatomica;
	private String descMaterialAnalise;
	private Byte nroAmostras;
	private Byte intervaloDias;
	private Date intervaloHoras;
	private Date dthrProgramada;
	private Byte prioridadeExecucao;
	private Date dataImpSumario;
	private RapServidores servidorResponsabilidade;
	private Boolean indImprimiuTicket;
	private Date dthrLiberada;
	private DominioTipoEmissaoSumarioItemSolicitacaoExame tipoEmissaoSumario;
	private MciEtiologiaInfeccao etiologiaInfeccao;
	private RapServidores servidorEtiologia;
	private Date dthrEtiologia;
	private Integer uieUoeSeq;
	private Short uieSeqp;
	private Boolean indCargaContador;
	private MbcCirurgias cirurgia;
	private DominioIndImpressoLaudo indImpressoLaudo;
	private DominioFormaRespiracao formaRespiracao;
	private BigDecimal litrosOxigenio;
	private Short percOxigenio;
	private Short unfSeqAvisa;
	private Boolean indPossuiImagem;
	private Boolean indTicketPacImp;
	private Boolean indInfComplImp;
	private DominioTipoMensagemCaixaPostal indTipoMsgCxPostal;
	private Date dthrMsgCxPostal;
	private String complementoMotCanc;
	private Integer numeroApOrigem;
	private Integer numeroAp;
	private DominioTipoTransporteUnidade tipoTransporteUn;
	private Boolean indUsoO2Un;
	private String pacOruAccNumber;
	private AghUnidadesFuncionais unidadeFuncional;
	private AelUnfExecutaExames aelUnfExecutaExames;
	private List<AelItemEntregaExames> itemEntregaExames = new LinkedList<AelItemEntregaExames>();
	private List<AelItemSolicitacaoExames> itemSolicitacaoExames = new LinkedList<AelItemSolicitacaoExames>();
	private List<AelAmostraItemExames> aelAmostraItemExames = new LinkedList<AelAmostraItemExames>();
	private List<AelExtratoItemSolicitacao> aelExtratoItemSolicitacao = new LinkedList<AelExtratoItemSolicitacao>();
	private List<AelInformacaoSolicitacaoUnidadeExecutora> informacaoSolicitacaoUnidadeExecutoras = new LinkedList<AelInformacaoSolicitacaoUnidadeExecutora>();
	private AelMotivoCancelaExames aelMotivoCancelaExames;
	private VAelExameMatAnalise exameMatAnalise;
	private List<AelDocResultadoExame> docResultadoExame;
	private List<AelResultadoExame> resultadoExames;
	private List<AelNotaAdicional> notaAdicional;
	private AelExamesMaterialAnalise aelExameMaterialAnalise;
	private List<AelItemHorarioAgendado> itemHorarioAgendado;
	private List<AelRespostaQuestao> aelRespostasQuestoes;
	private int indexOrigem;
	private AelOrdExameMatAnalise aelOrdExameMatAnalise;
	private List<AelExameApItemSolic> aelExameApItemSolic;
	private Integer version;
	private String sitCodigo;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private boolean origemTelaSolicitacao = false;

	public AelItemSolicitacaoExames() {
	}

	public AelItemSolicitacaoExames(final AelItemSolicitacaoExamesId id) {
		this.id = id;
	}

	public AelItemSolicitacaoExames(final AelItemSolicitacaoExamesId id,
			final AelSolicitacaoExames solicitacaoExame, final AelSitItemSolicitacoes situacaoItemSolicitacao,
			final AelExames aelExames, final AelMateriaisAnalises aelMateriaisAnalises, 
			final DominioTipoColeta t, final Boolean i, final Boolean indGeradoAutomatico,
			final Boolean indImprimiuTicket) {
		this.id = id;
		this.solicitacaoExame = solicitacaoExame;
		this.situacaoItemSolicitacao = situacaoItemSolicitacao;
		this.exame = aelExames;
		this.materialAnalise = aelMateriaisAnalises;
		this.tipoColeta = t;
		this.indUsoO2 = i;
		this.indGeradoAutomatico = indGeradoAutomatico;
		this.indImprimiuTicket = indImprimiuTicket;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public AelItemSolicitacaoExames(final AelItemSolicitacaoExamesId id,
			final AelItemSolicitacaoExames aelItemSolicitacaoExame,
			final AelSolicitacaoExames solicitacaoExame, final AelSitItemSolicitacoes situacaoItemSolicitacao,
			final AelExames aelExames, final AelMateriaisAnalises aelMateriaisAnalises, final Short ufeUnfSeq,
			final DominioTipoColeta tipoColeta, final Boolean indUsoO2, final Boolean indGeradoAutomatico,
			final AelIntervaloColeta icoSeq, final AelRegiaoAnatomica ranSeq, final AelMotivoCancelaExames aelMotivoCancelaExames, final DominioTipoTransporte tpTransp,
			final String descRegiaoAnatomica, final String descMaterialAnalise,
			final Byte nroAmostras, final Byte intervaloDias, final Date intervaloHoras,
			final Date dthrProgramada, final Byte prioridadeExecucao, final Date dataImpSumario,
			final RapServidores serMatriculaEhResponsabilid,
			final Short serVinCodigoEhResponsabili, final Boolean indImprimiuTicket,
			final Date dthrLiberada, final DominioTipoEmissaoSumarioItemSolicitacaoExame tipoEmissaoSumario, final MciEtiologiaInfeccao einTipo,
			final RapServidores serMatriculaEtiologia, final Short serVinCodigoEtiologia,
			final Date dthrEtiologia, final Integer uieUoeSeq, final Short uieSeqp,
			final Boolean indCargaContador, final Integer numeroAp, final MbcCirurgias crgSeq,
			final DominioIndImpressoLaudo indImpressoLaudo, final DominioFormaRespiracao formaRespiracao,
			final BigDecimal litrosOxigenio, final Short percOxigenio, final Short unfSeqAvisa,
			final Boolean indPossuiImagem, final Boolean indTicketPacImp,
			final Boolean indInfComplImp, final DominioTipoMensagemCaixaPostal indTipoMsgCxPostal,
			final Date dthrMsgCxPostal, final String complementoMotCanc,
			final Integer numeroApOrigem, final DominioTipoTransporteUnidade tipoTransporteUn, final Boolean indUsoO2Un,
			final String pacOruAccNumber) {
		this.id = id;
		this.itemSolicitacaoExame = aelItemSolicitacaoExame;
		this.solicitacaoExame = solicitacaoExame;
		this.situacaoItemSolicitacao = situacaoItemSolicitacao;
		this.exame = aelExames;
		this.materialAnalise = aelMateriaisAnalises;
		this.tipoColeta = tipoColeta;
		this.indUsoO2 = indUsoO2;
		this.indGeradoAutomatico = indGeradoAutomatico;
		this.intervaloColeta = icoSeq;
		this.regiaoAnatomica = ranSeq;
		this.aelMotivoCancelaExames = aelMotivoCancelaExames;
		this.tipoTransporte = tpTransp;
		this.descRegiaoAnatomica = descRegiaoAnatomica;
		this.descMaterialAnalise = descMaterialAnalise;
		this.nroAmostras = nroAmostras;
		this.intervaloDias = intervaloDias;
		this.intervaloHoras = intervaloHoras;
		this.dthrProgramada = dthrProgramada;
		this.prioridadeExecucao = prioridadeExecucao;
		this.dataImpSumario = dataImpSumario;
		this.servidorResponsabilidade = serMatriculaEhResponsabilid;
		this.indImprimiuTicket = indImprimiuTicket;
		this.dthrLiberada = dthrLiberada;
		this.tipoEmissaoSumario = tipoEmissaoSumario;
		this.etiologiaInfeccao = einTipo;
		this.servidorEtiologia = serMatriculaEtiologia;
		this.dthrEtiologia = dthrEtiologia;
		this.uieUoeSeq = uieUoeSeq;
		this.uieSeqp = uieSeqp;
		this.indCargaContador = indCargaContador;
		this.numeroAp = numeroAp;
		this.cirurgia = crgSeq;
		this.indImpressoLaudo = indImpressoLaudo;
		this.formaRespiracao = formaRespiracao;
		this.litrosOxigenio = litrosOxigenio;
		this.percOxigenio = percOxigenio;
		this.unfSeqAvisa = unfSeqAvisa;
		this.indPossuiImagem = indPossuiImagem;
		this.indTicketPacImp = indTicketPacImp;
		this.indInfComplImp = indInfComplImp;
		this.indTipoMsgCxPostal = indTipoMsgCxPostal;
		this.dthrMsgCxPostal = dthrMsgCxPostal;
		this.complementoMotCanc = complementoMotCanc;
		this.numeroApOrigem = numeroApOrigem;
		this.tipoTransporteUn = tipoTransporteUn;
		this.indUsoO2Un = indUsoO2Un;
		this.pacOruAccNumber = pacOruAccNumber;
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "soeSeq", column = @Column(name = "SOE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public AelItemSolicitacaoExamesId getId() {
		return this.id;
	}
	
	public void setId(final AelItemSolicitacaoExamesId id) {
		this.id = id;
	}

	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelExtratoItemSolicitacao> getAelExtratoItemSolicitacao(){
		return this.aelExtratoItemSolicitacao;
	}
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelDocResultadoExame> getDocResultadoExame() {
		return this.docResultadoExame;
	}
	
	public void setDocResultadoExame(final List<AelDocResultadoExame> docResultadoExame) {
		this.docResultadoExame = docResultadoExame;
	}

	public void setAelExtratoItemSolicitacao(final List<AelExtratoItemSolicitacao> aelExtratoItemSolicitacao) {
		this.aelExtratoItemSolicitacao = aelExtratoItemSolicitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOE_SEQ", nullable = false, insertable=false, updatable=false)
	public AelSolicitacaoExames getSolicitacaoExame() {
		return this.solicitacaoExame;
	}
	
	public void setSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIT_CODIGO", nullable = false)
	public AelSitItemSolicitacoes getSituacaoItemSolicitacao() {
		return this.situacaoItemSolicitacao;
	}

	public void setSituacaoItemSolicitacao(final AelSitItemSolicitacoes situacaoItemSolicitacao) {
		this.situacaoItemSolicitacao = situacaoItemSolicitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UFE_EMA_EXA_SIGLA", nullable = false)
	public AelExames getExame() {
		return this.exame;
	}

	public void setExame(final AelExames aelExames) {
		this.exame = aelExames;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UFE_EMA_MAN_SEQ", nullable = false)
	public AelMateriaisAnalises getMaterialAnalise() {
		return this.materialAnalise;
	}

	public void setMaterialAnalise(final AelMateriaisAnalises aelMateriaisAnalises) {
		this.materialAnalise = aelMateriaisAnalises;
	}
	
	@Transient
	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return getAelUnfExecutaExames().getAelExamesMaterialAnalise();
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UFE_UNF_SEQ", nullable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "EMA_EXA_SIGLA", updatable = false, insertable = false),
			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "EMA_MAN_SEQ", updatable = false, insertable = false),
			@JoinColumn(name = "UFE_UNF_SEQ", referencedColumnName = "UNF_SEQ", updatable = false, insertable = false) })
	public AelUnfExecutaExames getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}

	public void setAelUnfExecutaExames(final AelUnfExecutaExames aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "SIGLA", updatable = false, insertable = false),
			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", updatable = false, insertable = false) })
	public VAelExameMatAnalise getExameMatAnalise() {
		return exameMatAnalise;
	}

	public void setExameMatAnalise(VAelExameMatAnalise exameMatAnalise) {
		this.exameMatAnalise = exameMatAnalise;
	}
	
	@Column(name = "TIPO_COLETA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoColeta getTipoColeta() {
		return this.tipoColeta;
	}

	public void setTipoColeta(final DominioTipoColeta t) {
		this.tipoColeta = t;
	}

	@Column(name = "IND_USO_O2", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoO2() {
		return this.indUsoO2;
	}

	public void setIndUsoO2(final Boolean q) {
		this.indUsoO2 = q;
	}
	
	@Transient
	public String getIndUsoO2String() {
		if(indUsoO2 != null){
			if(indUsoO2){
				return "Sim";
			}else{
				return "Não";
			}
		}
		return "";
	}

	@Column(name = "IND_GERADO_AUTOMATICO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeradoAutomatico() {
		return this.indGeradoAutomatico;
	}

	public void setIndGeradoAutomatico(final Boolean q) {
		this.indGeradoAutomatico = q;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ICO_SEQ")
	public AelIntervaloColeta getIntervaloColeta() {
		return intervaloColeta;
	}
	
	public void setIntervaloColeta(final AelIntervaloColeta intervaloColeta) {
		this.intervaloColeta = intervaloColeta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RAN_SEQ")
	public AelRegiaoAnatomica getRegiaoAnatomica() {
		return regiaoAnatomica;
	}
	
	public void setRegiaoAnatomica(final AelRegiaoAnatomica regiaoAnatomica) {
		this.regiaoAnatomica = regiaoAnatomica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MOC_SEQ")
	public AelMotivoCancelaExames getAelMotivoCancelaExames() {
		return this.aelMotivoCancelaExames;
	}

	public void setAelMotivoCancelaExames(final AelMotivoCancelaExames aelMotivoCancelaExames) {
		this.aelMotivoCancelaExames = aelMotivoCancelaExames;
	}
	
	@Column(name = "TIPO_TRANSPORTE", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoTransporte getTipoTransporte() {
		return this.tipoTransporte;
	}

	public void setTipoTransporte(final DominioTipoTransporte t) {
		this.tipoTransporte = t;
	}

	@Column(name = "DESC_REGIAO_ANATOMICA", length = 100)
	public String getDescRegiaoAnatomica() {
		return this.descRegiaoAnatomica;
	}

	public void setDescRegiaoAnatomica(final String descRegiaoAnatomica) {
		this.descRegiaoAnatomica = descRegiaoAnatomica;
	}

	@Column(name = "DESC_MATERIAL_ANALISE", length = 100)
	public String getDescMaterialAnalise() {
		return this.descMaterialAnalise;
	}

	public void setDescMaterialAnalise(final String descMaterialAnalise) {
		this.descMaterialAnalise = descMaterialAnalise;
	}

	@Column(name = "NRO_AMOSTRAS", precision = 2, scale = 0)
	public Byte getNroAmostras() {
		return this.nroAmostras;
	}

	public void setNroAmostras(final Byte nroAmostras) {
		this.nroAmostras = nroAmostras;
	}

	@Column(name = "INTERVALO_DIAS", precision = 2, scale = 0)
	public Byte getIntervaloDias() {
		return this.intervaloDias;
	}

	public void setIntervaloDias(final Byte intervaloDias) {
		this.intervaloDias = intervaloDias;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INTERVALO_HORAS", length = 7)
	public Date getIntervaloHoras() {
		return this.intervaloHoras;
	}

	public void setIntervaloHoras(final Date intervaloHoras) {
		this.intervaloHoras = intervaloHoras;
	}
	
	@Transient
	public String getIntervaloDiasHoras(){
		
		if(intervaloDias == null && intervaloHoras == null){
			return "";
		}

		final String dias = intervaloDias == null ? "" : intervaloDias + " DIA(S) ";
		final String horas = intervaloHoras == null ? "" : new SimpleDateFormat("HH:mm").format(intervaloHoras) + " HORAS";

		return dias + horas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_PROGRAMADA", length = 7)
	public Date getDthrProgramada() {
		return this.dthrProgramada;
	}

	public void setDthrProgramada(final Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}

	@Column(name = "PRIORIDADE_EXECUCAO", precision = 2, scale = 0)
	public Byte getPrioridadeExecucao() {
		return this.prioridadeExecucao;
	}

	public void setPrioridadeExecucao(final Byte prioridadeExecucao) {
		this.prioridadeExecucao = prioridadeExecucao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_IMP_SUMARIO", length = 7)
	public Date getDataImpSumario() {
		return this.dataImpSumario;
	}

	public void setDataImpSumario(final Date dataImpSumario) {
		this.dataImpSumario = dataImpSumario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_EH_RESPONSABILID", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_EH_RESPONSABILI", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorResponsabilidade() {
		return servidorResponsabilidade;
	}
	
	public void setServidorResponsabilidade(final RapServidores servidorResponsabilidade) {
		this.servidorResponsabilidade = servidorResponsabilidade;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ"),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP") })
	public AelItemSolicitacaoExames getItemSolicitacaoExame() {
		return this.itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(final AelItemSolicitacaoExames aelItemSolicitacaoExame) {
		this.itemSolicitacaoExame = aelItemSolicitacaoExame;
	}
	
	@Column(name = "IND_IMPRIMIU_TICKET", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImprimiuTicket() {
		return this.indImprimiuTicket;
	}

	public void setIndImprimiuTicket(final Boolean i) {
		this.indImprimiuTicket = i;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_LIBERADA", length = 7)
	public Date getDthrLiberada() {
		return this.dthrLiberada;
	}

	public void setDthrLiberada(final Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}
	
	//
	@Column(name = "TIPO_EMISSAO_SUMARIO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoEmissaoSumarioItemSolicitacaoExame getTipoEmissaoSumario() {
		return this.tipoEmissaoSumario;
	}

	public void setTipoEmissaoSumario(final DominioTipoEmissaoSumarioItemSolicitacaoExame r) {
		this.tipoEmissaoSumario = r;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EIN_TIPO")
	public MciEtiologiaInfeccao getEtiologiaInfeccao() {
		return etiologiaInfeccao;
	}
	
	public void setEtiologiaInfeccao(final MciEtiologiaInfeccao etiologiaInfeccao) {
		this.etiologiaInfeccao = etiologiaInfeccao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ETIOLOGIA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ETIOLOGIA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEtiologia() {
		return servidorEtiologia;
	}

	public void setServidorEtiologia(final RapServidores servidorEtiologia) {
		this.servidorEtiologia = servidorEtiologia;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ETIOLOGIA", length = 7)
	public Date getDthrEtiologia() {
		return this.dthrEtiologia;
	}

	public void setDthrEtiologia(final Date dthrEtiologia) {
		this.dthrEtiologia = dthrEtiologia;
	}
	
	// TODO Descobrir qual a tabela para associacao. E criar FK.
	@Column(name = "UIE_UOE_SEQ", precision = 8, scale = 0)
	public Integer getUieUoeSeq() {
		return this.uieUoeSeq;
	}

	public void setUieUoeSeq(final Integer uieUoeSeq) {
		this.uieUoeSeq = uieUoeSeq;
	}

	@Column(name = "UIE_SEQP", precision = 4, scale = 0)
	public Short getUieSeqp() {
		return this.uieSeqp;
	}

	public void setUieSeqp(final Short uieSeqp) {
		this.uieSeqp = uieSeqp;
	}

	@Column(name = "IND_CARGA_CONTADOR", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCargaContador() {
		return this.indCargaContador;
	}

	public void setIndCargaContador(final Boolean c) {
		this.indCargaContador = c;
	}

	@Column(name = "NUMERO_AP", precision = 8, scale = 0)
	public Integer getNumeroAp() {
		return this.numeroAp;
	}

	public void setNumeroAp(final Integer numeroAp) {
		this.numeroAp = numeroAp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRG_SEQ")
	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}
	
	public void setCirurgia(final MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}
	
	@Column(name = "IND_IMPRESSO_LAUDO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndImpressoLaudo getIndImpressoLaudo() {
		return this.indImpressoLaudo;
	}

	public void setIndImpressoLaudo(final DominioIndImpressoLaudo i) {
		this.indImpressoLaudo = i;
	}

	@Column(name = "FORMA_RESPIRACAO", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioFormaRespiracao") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioFormaRespiracao getFormaRespiracao() {
		return this.formaRespiracao;
	}

	public void setFormaRespiracao(final DominioFormaRespiracao formaRespiracao) {
		this.formaRespiracao = formaRespiracao;
	}

	@Column(name = "LITROS_OXIGENIO", precision = 3, scale = 1)
	public BigDecimal getLitrosOxigenio() {
		return this.litrosOxigenio;
	}

	public void setLitrosOxigenio(final BigDecimal litrosOxigenio) {
		this.litrosOxigenio = litrosOxigenio;
	}

	@Column(name = "PERC_OXIGENIO", precision = 3, scale = 0)
	public Short getPercOxigenio() {
		return this.percOxigenio;
	}

	public void setPercOxigenio(final Short percOxigenio) {
		this.percOxigenio = percOxigenio;
	}
	
	//TODO qual tabela e pode criar FK?
	@Column(name = "UNF_SEQ_AVISA", precision = 4, scale = 0)
	public Short getUnfSeqAvisa() {
		return this.unfSeqAvisa;
	}

	public void setUnfSeqAvisa(final Short unfSeqAvisa) {
		this.unfSeqAvisa = unfSeqAvisa;
	}
	
	@Column(name = "IND_POSSUI_IMAGEM", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPossuiImagem() {
		return this.indPossuiImagem;
	}

	public void setIndPossuiImagem(final Boolean p) {
		this.indPossuiImagem = p;
	}

	@Column(name = "IND_TICKET_PAC_IMP", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTicketPacImp() {
		return this.indTicketPacImp;
	}

	public void setIndTicketPacImp(final Boolean t) {
		this.indTicketPacImp = t;
	}

	@Column(name = "IND_INF_COMPL_IMP", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInfComplImp() {
		return this.indInfComplImp;
	}

	public void setIndInfComplImp(final Boolean c) {
		this.indInfComplImp = c;
	}

	@Column(name = "IND_TIPO_MSG_CX_POSTAL", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoMensagemCaixaPostal getIndTipoMsgCxPostal() {
		return this.indTipoMsgCxPostal;
	}

	public void setIndTipoMsgCxPostal(final DominioTipoMensagemCaixaPostal s) {
		this.indTipoMsgCxPostal = s;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_MSG_CX_POSTAL", length = 7)
	public Date getDthrMsgCxPostal() {
		return this.dthrMsgCxPostal;
	}

	public void setDthrMsgCxPostal(final Date dthrMsgCxPostal) {
		this.dthrMsgCxPostal = dthrMsgCxPostal;
	}

	@Column(name = "COMPLEMENTO_MOT_CANC", length = 2000)
	public String getComplementoMotCanc() {
		return this.complementoMotCanc;
	}

	public void setComplementoMotCanc(final String complementoMotCanc) {
		this.complementoMotCanc = complementoMotCanc;
	}

	@Column(name = "NUMERO_AP_ORIGEM", precision = 8, scale = 0)
	public Integer getNumeroApOrigem() {
		return this.numeroApOrigem;
	}

	public void setNumeroApOrigem(final Integer numeroApOrigem) {
		this.numeroApOrigem = numeroApOrigem;
	}

	@Column(name = "TIPO_TRANSPORTE_UN", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoTransporteUnidade getTipoTransporteUn() {
		return this.tipoTransporteUn;
	}

	public void setTipoTransporteUn(final DominioTipoTransporteUnidade d) {
		this.tipoTransporteUn = d;
	}

	@Column(name = "IND_USO_O2_UN", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoO2Un() {
		return this.indUsoO2Un;
	}

	public void setIndUsoO2Un(final Boolean indUsoO2Un) {
		this.indUsoO2Un = indUsoO2Un;
	}

	@Column(name = "PAC_ORU_ACC_NUMBER", length = 50)
	public String getPacOruAccNumber() {
		return this.pacOruAccNumber;
	}

	public void setPacOruAccNumber(final String pacOruAccNumber) {
		this.pacOruAccNumber = pacOruAccNumber;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "solicitacaoExames")
	public List<AelItemEntregaExames> getItemEntregaExames() {
		return itemEntregaExames;
	}

	public void setItemEntregaExames(List<AelItemEntregaExames> itemEntregaExames) {
		this.itemEntregaExames = itemEntregaExames;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemSolicitacaoExame")
	public List<AelItemSolicitacaoExames> getItemSolicitacaoExames() {
		return this.itemSolicitacaoExames;
	}

	public void setItemSolicitacaoExames(final List<AelItemSolicitacaoExames> aelItemSolicitacaoExames) {
		this.itemSolicitacaoExames = aelItemSolicitacaoExames;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelItemSolicitacaoExames")
	public List<AelAmostraItemExames> getAelAmostraItemExames() {
		return this.aelAmostraItemExames;
	}

	public void setAelAmostraItemExames(
			final List<AelAmostraItemExames> aelAmostraItemExames) {
		this.aelAmostraItemExames = aelAmostraItemExames;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemSolicitacaoExame")
	public List<AelInformacaoSolicitacaoUnidadeExecutora> getInformacaoSolicitacaoUnidadeExecutoras() {
		return informacaoSolicitacaoUnidadeExecutoras;
	}

	public void setInformacaoSolicitacaoUnidadeExecutoras(
			final List<AelInformacaoSolicitacaoUnidadeExecutora> informacaoSolicitacaoUnidadeExecutoras) {
		this.informacaoSolicitacaoUnidadeExecutoras = informacaoSolicitacaoUnidadeExecutoras;
	}
	
	public void addInformacaoSolicitacaoUnidadeExecutora(final AelInformacaoSolicitacaoUnidadeExecutora item) {
		if (item != null) {
			item.setItemSolicitacaoExame(this);
			this.getInformacaoSolicitacaoUnidadeExecutoras().add(item);
		}
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof AelItemSolicitacaoExames)) {
			return false;
		}
		
		final AelItemSolicitacaoExames castOther = (AelItemSolicitacaoExames) other;
		
		return new EqualsBuilder().append(this.getId(), castOther.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getId()).toHashCode();
	}
	
	/**
	 * Associa item depentente.<br>
	 * Adiciona um item de Exame na Solicitacao. Apenas se nao for item nulo.<br>
	 * Faz a associacao bidirecional.<br>
	 * Zera o id do item.<br>
	 * 
	 * @param item
	 */
	@Transient
	public void addItemSolicitacaoExame(final AelItemSolicitacaoExames item) {
		if (item != null) {
			item.setId(null);
			item.setItemSolicitacaoExame(this);
			this.getItemSolicitacaoExames().add(item);
		}
	}
	@PrePersist
	@PreUpdate
	protected void preInsercao() {
		if (this.getIndUsoO2() == null) {
			this.setIndUsoO2(Boolean.FALSE);
		}
		if (this.getIndGeradoAutomatico() == null) {
			this.setIndGeradoAutomatico(Boolean.FALSE);
		}
		if (this.getIndPossuiImagem() == null) {
			this.setIndPossuiImagem(Boolean.FALSE);
		}
		if (this.getIndImprimiuTicket() == null) {
			this.setIndImprimiuTicket(Boolean.FALSE);
		}
		if (this.getIndUsoO2Un() == null) {
			this.setIndUsoO2Un(Boolean.FALSE);
		}
	}

	@Transient
	public void doSetSituacaoItensDependentes(final AelSitItemSolicitacoes umaSituacao) {
		if (this.getItemSolicitacaoExames() != null) {
			for (final AelItemSolicitacaoExames itemDependente : this.getItemSolicitacaoExames()) {
				itemDependente.setSituacaoItemSolicitacao(umaSituacao);
			}
		}
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemSolicitacaoExames")
	public List<AelExameApItemSolic> getAelExameApItemSolic() {
		return aelExameApItemSolic;
	}
	public void setAelExameApItemSolic(List<AelExameApItemSolic> aelExameApItemSolic) {
		this.aelExameApItemSolic = aelExameApItemSolic;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "EMA_EXA_SIGLA", updatable = false, insertable = false),
			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "EMA_MAN_SEQ", updatable = false, insertable = false) })
	public AelOrdExameMatAnalise getAelOrdExameMatAnalise() {
		return aelOrdExameMatAnalise;
	}
	public void setAelOrdExameMatAnalise(
			AelOrdExameMatAnalise aelOrdExameMatAnalise) {
		this.aelOrdExameMatAnalise = aelOrdExameMatAnalise;
	}
	
	
		
	@Transient
	public int getIndexOrigem() {
		return indexOrigem;
	}
	public void setIndexOrigem(int indexOrigem) {
		this.indexOrigem = indexOrigem;
	}	

	public enum Fields {
		ID("id")
		, SOE_SEQ("id.soeSeq")
		, SEQP("id.seqp")
		, SOLICITACAO_EXAME("solicitacaoExame")
		, SOLICITACAO_EXAME_SEQ("solicitacaoExame.seq")
		, UFE_EMA_EXA_SIGLA("exame.sigla")
		, UFE_EMA_MAN_SEQ("materialAnalise.seq")
		, UFE_UNF_SEQ("unidadeFuncional.seq")
		, TIPO_COLETA("tipoColeta")
		, SITUACAO_ITEM_SOLICITACAO("situacaoItemSolicitacao")
		, SITUACAO_ITEM_SOLICITACAO_CODIGO("situacaoItemSolicitacao.codigo")
		, DTHR_LIBERADA("dthrLiberada")
		, AEL_AMOSTRA_ITEM_EXAMES("aelAmostraItemExames")		
		, IND_IMPRIMIU_TICKET("indImprimiuTicket")
		, AEL_EXAMES("exame")
		, AEL_EXAMES_SIGLA("exame.sigla")
		, AEL_MATERIAIS_ANALISES("materialAnalise")
		, AEL_MATERIAIS_ANALISES_SEQ("materialAnalise.seq")
		, UNIDADE_FUNCIONAL("unidadeFuncional")
		, DESC_UNIDADE_FUNCIONAL("unidadeFuncional.descricao")
		, DTHR_PROGRAMADA("dthrProgramada")
		, NRO_AMOSTRAS("nroAmostras")
		, ATENDIMENTO("solicitacaoExame.atendimento")
		, ATENDIMENTO_DIVERSO("solicitacaoExame.atendimentoDiverso")
		, UNF_SEQ_AVISA("unfSeqAvisa")
		, AEL_UNF_EXECUTA_EXAMES("aelUnfExecutaExames")
		, DESC_REGIAO_ANATOMICA("descRegiaoAnatomica")
		, REGIAO_ANATOMICA("regiaoAnatomica")
		, REGIAO_ANATOMICA_SEQ("regiaoAnatomica.seq")
		, IND_GERADO_AUTOMATICO("indGeradoAutomatico")
		, INTERVALO_DIAS("intervaloDias")
		, INTERVALO_HORAS("intervaloHoras")
		, INTERVALO_COLETA("intervaloColeta")
		, USO_O2_UNIDADE("indUsoO2Un")
		, USO_O2("indUsoO2")
		, TIPO_TRANSPORTE("tipoTransporte")
		, TIPO_TRANSPORTE_UNIDADE("tipoTransporteUn")
		, EXTRATO_ITEM_SOLIC("aelExtratoItemSolicitacao")
		, ETIOLOGIA("etiologiaInfeccao")
		, FORMA_RESPIRACAO("formaRespiracao")
		, DESC_MATERIAL_ANALISE("descMaterialAnalise")
		, PRIORIDADE_EXECUCAO("prioridadeExecucao")
		, SIT_CODIGO("situacaoItemSolicitacao.codigo")
		, AEL_ITEM_SOLICITACAO_EXAMES_PAI("itemSolicitacaoExame")
		, AEL_MOTIVO_CANCELA_EXAMES("aelMotivoCancelaExames")
		, IND_TICKET_PAC_IMP("indTicketPacImp")
		, IND_INF_COMPL_IMP("indInfComplImp")
		, SERVIDOR_RESPONSABILIDADE("servidorResponsabilidade")
		, SERVIDOR_RESPONSABILIDADE_MATRICULA("servidorResponsabilidade.id.matricula")
		, SERVIDOR_RESPONSABILIDADE_VIN_CODIGO("servidorResponsabilidade.id.vinCodigo")
		, DOC_RESULTADO_EXAME("docResultadoExame")
		, RESULTADO_EXAME("resultadoExames")
		, NOTA_ADICIONAL("notaAdicional")
		, MOC_SEQ("aelMotivoCancelaExames.seq")
		, CRG_SEQ("cirurgia.seq")
		, NUMERO_AP("numeroAp")
		, EXAME_MAT_ANALISE("exameMatAnalise")
		, AEL_EXAMES_MATERIAL_ANALISE("aelExameMaterialAnalise")	
		, PAC_ORU_ACC_NUMBER("pacOruAccNumber")
		, ISE_SOE_SEQ("itemSolicitacaoExame.id.soeSeq")
		, ISE_SEQP("itemSolicitacaoExame.id.seqp")
		, ITEM_HORARIO_AGENDADO("itemHorarioAgendado")
		, DATA_IMP_SUMARIO("dataImpSumario")
		, EIN_TIPO("etiologiaInfeccao.codigo")
		, IND_IMPRESSO_LAUDO("indImpressoLaudo")
		, AEL_ORD_EXAME_MAT_ANALISE("aelOrdExameMatAnalise")
		, AEL_EXAME_AP_ITEM_SOLICS("aelExameApItemSolic")				
		, NUMERO_AP_ORIGEM("numeroApOrigem")
		, SITCODIGO("sitCodigo")
		, UFEEMAEXASIGLA("ufeEmaExaSigla")
		, UFEEMAMANSEQ("ufeEmaManSeq")
		, ITEM_ENTREGA_EXAMES("itemEntregaExames");		
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelResultadoExame> getResultadoExames() {
		return resultadoExames;
	}
	public void setResultadoExames(final List<AelResultadoExame> resultadoExames) {
		this.resultadoExames = resultadoExames;
	}
	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelNotaAdicional> getNotaAdicional() {
		return notaAdicional;
	}
	public void setNotaAdicional(final List<AelNotaAdicional> notaAdicional) {
		this.notaAdicional = notaAdicional;
	}
	public void setAelExameMaterialAnalise(AelExamesMaterialAnalise aelExameMaterialAnalise) {
		this.aelExameMaterialAnalise = aelExameMaterialAnalise;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "EXA_SIGLA", updatable = false, insertable = false),
			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", updatable = false, insertable = false) })
	public AelExamesMaterialAnalise getAelExameMaterialAnalise() {
		return aelExameMaterialAnalise;
	}	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelItemHorarioAgendado> getItemHorarioAgendado() {
		return itemHorarioAgendado;
	}
	public void setItemHorarioAgendado(final List<AelItemHorarioAgendado> itemHorarioAgendado) {
		this.itemHorarioAgendado = itemHorarioAgendado;
	}
	public void setAelRespostasQuestoes(List<AelRespostaQuestao> aelRespostasQuestoes) {
		this.aelRespostasQuestoes = aelRespostasQuestoes;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelItemSolicitacaoExames")
	public List<AelRespostaQuestao> getAelRespostasQuestoes() {
		return aelRespostasQuestoes;
	}
	@Column(name = "SIT_CODIGO", insertable = false, updatable = false)
    public String getSitCodigo() {
          return sitCodigo;
    }
    public void setSitCodigo(String sitCodigo) {
          this.sitCodigo = sitCodigo;
    }    
    @Column(name = "UFE_EMA_EXA_SIGLA", insertable = false, updatable = false)
    public String getUfeEmaExaSigla() {
          return ufeEmaExaSigla;
    }
    public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
          this.ufeEmaExaSigla = ufeEmaExaSigla;
    }    
    @Column(name = "UFE_EMA_MAN_SEQ", insertable = false, updatable = false)
    public Integer getUfeEmaManSeq() {
          return ufeEmaManSeq;
    }
    public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
          this.ufeEmaManSeq = ufeEmaManSeq;
    }
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transient
	public boolean isOrigemTelaSolicitacao() {
		return origemTelaSolicitacao;
	}

	
	public void setOrigemTelaSolicitacao(boolean origemTelaSolicitacao) {
		this.origemTelaSolicitacao = origemTelaSolicitacao;
	}
}
