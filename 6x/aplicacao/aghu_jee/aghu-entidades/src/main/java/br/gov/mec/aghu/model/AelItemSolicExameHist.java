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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioIndImpressoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumarioItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoMensagemCaixaPostal;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "ael_item_solicitacao_exames", schema = "hist")
public class AelItemSolicExameHist extends BaseEntityId<AelItemSolicExameHistId> implements java.io.Serializable , IAelItemSolicitacaoExames{

	private static final long serialVersionUID = 4634424408119474877L;
	private AelItemSolicExameHistId id;
	private AelItemSolicExameHist itemSolicitacaoExame;
	private AelSolicitacaoExamesHist solicitacaoExame;
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
	private Integer numeroAp;
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
	private DominioTipoTransporteUnidade tipoTransporteUn;
	private Boolean indUsoO2Un;
	private String pacOruAccNumber;
	private AghUnidadesFuncionais unidadeFuncional;
	private AelUnfExecutaExames aelUnfExecutaExames;
	private List<AelItemSolicExameHist> itemSolicitacaoExames = new LinkedList<AelItemSolicExameHist>();
//	private List<AelAmostraItemExamesHist> aelAmostraItemExames = new LinkedList<AelAmostraItemExames>();
	private List<AelExtratoItemSolicHist> aelExtratoItemSolicitacao = new LinkedList<AelExtratoItemSolicHist>();
//	private List<AelInformacaoSolicitacaoUnidadeExecutoraHist> informacaoSolicitacaoUnidadeExecutoras = new LinkedList<AelInformacaoSolicitacaoUnidadeExecutora>();
	private AelMotivoCancelaExames aelMotivoCancelaExames;
//	private VAelExameMatAnaliseHist exameMatAnalise;
	private List<AelDocResultadoExamesHist> docResultadoExame;
	private List<AelResultadosExamesHist> resultadoExames;
	private List<AelNotaAdicional> notaAdicional;
//	private AelExamesMaterialAnaliseHist aelExameMaterialAnalise;
//	private List<AelItemHorarioAgendadoHist> itemHorarioAgendado;
	private String emaExaSigla;
	private Date dataHoraEventomaxExtratoItem;
	private Long qtdeNotaAdicional;
	private String descricaoGrupoMatAnalise;
	private Integer seqMatAnalise;
	private AelOrdExameMatAnalise aelOrdExameMatAnalise;

	public AelItemSolicExameHist() {
	}

	public AelItemSolicExameHist(AelItemSolicExameHistId id) {
		this.id = id;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public AelItemSolicExameHist(AelItemSolicExameHistId id,
			AelItemSolicExameHist itemSolicitacaoExame,
			AelSolicitacaoExamesHist solicitacaoExame,
			AelSitItemSolicitacoes situacaoItemSolicitacao,
			AelExames exame, AelMateriaisAnalises materialAnalise,
			DominioTipoColeta tipoColeta, Boolean indUsoO2,
			Boolean indGeradoAutomatico, DominioTipoTransporte tipoTransporte,
			String descRegiaoAnatomica, String descMaterialAnalise,
			Byte nroAmostras, Byte intervaloDias, Date intervaloHoras,
			Date dthrProgramada, Byte prioridadeExecucao, Date dataImpSumario,
			Boolean indImprimiuTicket, Date dthrLiberada,
			DominioTipoEmissaoSumarioItemSolicitacaoExame tipoEmissaoSumario,
			Date dthrEtiologia, Integer uieUoeSeq, Short uieSeqp,
			Boolean indCargaContador, Integer numeroAp,
			DominioIndImpressoLaudo indImpressoLaudo,
			DominioFormaRespiracao formaRespiracao, BigDecimal litrosOxigenio,
			Short percOxigenio, Short unfSeqAvisa, Boolean indPossuiImagem,
			Boolean indTicketPacImp, Boolean indInfComplImp,
			DominioTipoMensagemCaixaPostal indTipoMsgCxPostal,
			Date dthrMsgCxPostal, String complementoMotCanc,
			Integer numeroApOrigem,
			DominioTipoTransporteUnidade tipoTransporteUn, Boolean indUsoO2Un,
			String pacOruAccNumber, AghUnidadesFuncionais unidadeFuncional,
			List<AelItemSolicExameHist> itemSolicitacaoExames,
			List<AelExtratoItemSolicHist> aelExtratoItemSolicitacao,
			List<AelDocResultadoExamesHist> docResultadoExame,
			List<AelResultadosExamesHist> resultadoExames) {
		this.id = id;
		this.itemSolicitacaoExame = itemSolicitacaoExame;
		this.solicitacaoExame = solicitacaoExame;
		this.situacaoItemSolicitacao = situacaoItemSolicitacao;
		this.exame = exame;
		this.materialAnalise = materialAnalise;
		this.tipoColeta = tipoColeta;
		this.indUsoO2 = indUsoO2;
		this.indGeradoAutomatico = indGeradoAutomatico;
		this.tipoTransporte = tipoTransporte;
		this.descRegiaoAnatomica = descRegiaoAnatomica;
		this.descMaterialAnalise = descMaterialAnalise;
		this.nroAmostras = nroAmostras;
		this.intervaloDias = intervaloDias;
		this.intervaloHoras = intervaloHoras;
		this.dthrProgramada = dthrProgramada;
		this.prioridadeExecucao = prioridadeExecucao;
		this.dataImpSumario = dataImpSumario;
		this.indImprimiuTicket = indImprimiuTicket;
		this.dthrLiberada = dthrLiberada;
		this.tipoEmissaoSumario = tipoEmissaoSumario;
		this.dthrEtiologia = dthrEtiologia;
		this.uieUoeSeq = uieUoeSeq;
		this.uieSeqp = uieSeqp;
		this.indCargaContador = indCargaContador;
		this.numeroAp = numeroAp;
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
		this.unidadeFuncional = unidadeFuncional;
		this.itemSolicitacaoExames = itemSolicitacaoExames;
		this.aelExtratoItemSolicitacao = aelExtratoItemSolicitacao;
		this.docResultadoExame = docResultadoExame;
		this.resultadoExames = resultadoExames;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "soeSeq", column = @Column(name = "SOE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public AelItemSolicExameHistId getId() {
		return this.id;
	}
	
	public void setId(AelItemSolicExameHistId id) {
		this.id = id;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelExtratoItemSolicHist> getAelExtratoItemSolicitacao(){
		return this.aelExtratoItemSolicitacao;
	}
	
	public void setAelExtratoItemSolicitacao(
			List<AelExtratoItemSolicHist> aelExtratoItemSolicitacao) {
		this.aelExtratoItemSolicitacao = aelExtratoItemSolicitacao;
	}	
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelDocResultadoExamesHist> getDocResultadoExame() {
		return this.docResultadoExame;
	}
	
	public void setDocResultadoExame(List<AelDocResultadoExamesHist> docResultadoExame) {
		this.docResultadoExame = docResultadoExame;
	}

//	public void setAelExtratoItemSolicitacao(List<AelExtratoItemSolisHist> aelExtratoItemSolicitacao) {
//		this.aelExtratoItemSolicitacao = aelExtratoItemSolicitacao;
//	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOE_SEQ", nullable = false, insertable=false, updatable=false)
	public AelSolicitacaoExamesHist getSolicitacaoExame() {
		return this.solicitacaoExame;
	}
	
	public void setSolicitacaoExame(AelSolicitacaoExamesHist solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIT_CODIGO", nullable = false)
	public AelSitItemSolicitacoes getSituacaoItemSolicitacao() {
		return this.situacaoItemSolicitacao;
	}

	public void setSituacaoItemSolicitacao(AelSitItemSolicitacoes situacaoItemSolicitacao) {
		this.situacaoItemSolicitacao = situacaoItemSolicitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UFE_EMA_EXA_SIGLA", nullable = false, referencedColumnName ="SIGLA")
	public AelExames getExame() {
		return this.exame;
	}

	public void setExame(AelExames aelExames) {
		this.exame = aelExames;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UFE_EMA_MAN_SEQ", nullable = false)
	public AelMateriaisAnalises getMaterialAnalise() {
		return this.materialAnalise;
	}

	public void setMaterialAnalise(AelMateriaisAnalises aelMateriaisAnalises) {
		this.materialAnalise = aelMateriaisAnalises;
	}
	
//	@Transient
//	public AelExamesMaterialAnaliseHist getExameMaterialAnalise() {
//		return getAelUnfExecutaExames().getAelExamesMaterialAnalise();
//	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UFE_UNF_SEQ", nullable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
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

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumns({
//			@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "SIGLA", updatable = false, insertable = false),
//			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", updatable = false, insertable = false) })
//	public VAelExameMatAnaliseHist getExameMatAnalise() {
//		return exameMatAnalise;
//	}
//
//	public void setExameMatAnalise(VAelExameMatAnaliseHist exameMatAnalise) {
//		this.exameMatAnalise = exameMatAnalise;
//	}

	@Column(name = "TIPO_COLETA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoColeta getTipoColeta() {
		return this.tipoColeta;
	}

	public void setTipoColeta(DominioTipoColeta t) {
		this.tipoColeta = t;
	}

	@Column(name = "IND_USO_O2", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoO2() {
		return this.indUsoO2;
	}

	public void setIndUsoO2(Boolean q) {
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

	public void setIndGeradoAutomatico(Boolean q) {
		this.indGeradoAutomatico = q;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ICO_SEQ")
	public AelIntervaloColeta getIntervaloColeta() {
		return intervaloColeta;
	}
	
	public void setIntervaloColeta(AelIntervaloColeta intervaloColeta) {
		this.intervaloColeta = intervaloColeta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RAN_SEQ")
	public AelRegiaoAnatomica getRegiaoAnatomica() {
		return regiaoAnatomica;
	}
	
	public void setRegiaoAnatomica(AelRegiaoAnatomica regiaoAnatomica) {
		this.regiaoAnatomica = regiaoAnatomica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MOC_SEQ")
	public AelMotivoCancelaExames getAelMotivoCancelaExames() {
		return this.aelMotivoCancelaExames;
	}

	public void setAelMotivoCancelaExames(AelMotivoCancelaExames aelMotivoCancelaExames) {
		this.aelMotivoCancelaExames = aelMotivoCancelaExames;
	}
	
	@Column(name = "TIPO_TRANSPORTE", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoTransporte getTipoTransporte() {
		return this.tipoTransporte;
	}

	public void setTipoTransporte(DominioTipoTransporte t) {
		this.tipoTransporte = t;
	}

	@Column(name = "DESC_REGIAO_ANATOMICA", length = 100)
	public String getDescRegiaoAnatomica() {
		return this.descRegiaoAnatomica;
	}

	public void setDescRegiaoAnatomica(String descRegiaoAnatomica) {
		this.descRegiaoAnatomica = descRegiaoAnatomica;
	}

	@Column(name = "DESC_MATERIAL_ANALISE", length = 100)
	public String getDescMaterialAnalise() {
		return this.descMaterialAnalise;
	}

	public void setDescMaterialAnalise(String descMaterialAnalise) {
		this.descMaterialAnalise = descMaterialAnalise;
	}

	@Column(name = "NRO_AMOSTRAS", precision = 2, scale = 0)
	public Byte getNroAmostras() {
		return this.nroAmostras;
	}

	public void setNroAmostras(Byte nroAmostras) {
		this.nroAmostras = nroAmostras;
	}

	@Column(name = "INTERVALO_DIAS", precision = 2, scale = 0)
	public Byte getIntervaloDias() {
		return this.intervaloDias;
	}

	public void setIntervaloDias(Byte intervaloDias) {
		this.intervaloDias = intervaloDias;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INTERVALO_HORAS", length = 7)
	public Date getIntervaloHoras() {
		return this.intervaloHoras;
	}

	public void setIntervaloHoras(Date intervaloHoras) {
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

	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}

	@Column(name = "PRIORIDADE_EXECUCAO", precision = 2, scale = 0)
	public Byte getPrioridadeExecucao() {
		return this.prioridadeExecucao;
	}

	public void setPrioridadeExecucao(Byte prioridadeExecucao) {
		this.prioridadeExecucao = prioridadeExecucao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_IMP_SUMARIO", length = 7)
	public Date getDataImpSumario() {
		return this.dataImpSumario;
	}

	public void setDataImpSumario(Date dataImpSumario) {
		this.dataImpSumario = dataImpSumario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_EH_RESPONSABILID", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_EH_RESPONSABILI", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorResponsabilidade() {
		return servidorResponsabilidade;
	}
	
	public void setServidorResponsabilidade(RapServidores servidorResponsabilidade) {
		this.servidorResponsabilidade = servidorResponsabilidade;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ"),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP") })
	public AelItemSolicExameHist getItemSolicitacaoExame() {
		return this.itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(AelItemSolicExameHist aelItemSolicitacaoExame) {
		this.itemSolicitacaoExame = aelItemSolicitacaoExame;
	}
	
	@Column(name = "IND_IMPRIMIU_TICKET", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImprimiuTicket() {
		return this.indImprimiuTicket;
	}

	public void setIndImprimiuTicket(Boolean i) {
		this.indImprimiuTicket = i;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_LIBERADA", length = 7)
	public Date getDthrLiberada() {
		return this.dthrLiberada;
	}

	public void setDthrLiberada(Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}
	
	//
	@Column(name = "TIPO_EMISSAO_SUMARIO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoEmissaoSumarioItemSolicitacaoExame getTipoEmissaoSumario() {
		return this.tipoEmissaoSumario;
	}

	public void setTipoEmissaoSumario(DominioTipoEmissaoSumarioItemSolicitacaoExame r) {
		this.tipoEmissaoSumario = r;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EIN_TIPO")
	public MciEtiologiaInfeccao getEtiologiaInfeccao() {
		return etiologiaInfeccao;
	}
	
	public void setEtiologiaInfeccao(MciEtiologiaInfeccao etiologiaInfeccao) {
		this.etiologiaInfeccao = etiologiaInfeccao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ETIOLOGIA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ETIOLOGIA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEtiologia() {
		return servidorEtiologia;
	}

	public void setServidorEtiologia(RapServidores servidorEtiologia) {
		this.servidorEtiologia = servidorEtiologia;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ETIOLOGIA", length = 7)
	public Date getDthrEtiologia() {
		return this.dthrEtiologia;
	}

	public void setDthrEtiologia(Date dthrEtiologia) {
		this.dthrEtiologia = dthrEtiologia;
	}
	
	@Column(name = "UIE_UOE_SEQ", precision = 8, scale = 0)
	public Integer getUieUoeSeq() {
		return this.uieUoeSeq;
	}

	public void setUieUoeSeq(Integer uieUoeSeq) {
		this.uieUoeSeq = uieUoeSeq;
	}

	@Column(name = "UIE_SEQP", precision = 4, scale = 0)
	public Short getUieSeqp() {
		return this.uieSeqp;
	}

	public void setUieSeqp(Short uieSeqp) {
		this.uieSeqp = uieSeqp;
	}

	@Column(name = "IND_CARGA_CONTADOR", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCargaContador() {
		return this.indCargaContador;
	}

	public void setIndCargaContador(Boolean c) {
		this.indCargaContador = c;
	}

	@Column(name = "NUMERO_AP", precision = 8, scale = 0)
	public Integer getNumeroAp() {
		return this.numeroAp;
	}

	public void setNumeroAp(Integer numeroAp) {
		this.numeroAp = numeroAp;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRG_SEQ")
	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}
	
	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	@Column(name = "IND_IMPRESSO_LAUDO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndImpressoLaudo getIndImpressoLaudo() {
		return this.indImpressoLaudo;
	}

	public void setIndImpressoLaudo(DominioIndImpressoLaudo i) {
		this.indImpressoLaudo = i;
	}

	@Column(name = "FORMA_RESPIRACAO", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioFormaRespiracao") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioFormaRespiracao getFormaRespiracao() {
		return this.formaRespiracao;
	}

	public void setFormaRespiracao(DominioFormaRespiracao formaRespiracao) {
		this.formaRespiracao = formaRespiracao;
	}

	@Column(name = "LITROS_OXIGENIO", precision = 3, scale = 1)
	public BigDecimal getLitrosOxigenio() {
		return this.litrosOxigenio;
	}

	public void setLitrosOxigenio(BigDecimal litrosOxigenio) {
		this.litrosOxigenio = litrosOxigenio;
	}

	@Column(name = "PERC_OXIGENIO", precision = 3, scale = 0)
	public Short getPercOxigenio() {
		return this.percOxigenio;
	}

	public void setPercOxigenio(Short percOxigenio) {
		this.percOxigenio = percOxigenio;
	}
	
	//TODO qual tabela e pode criar FK?
	@Column(name = "UNF_SEQ_AVISA", precision = 4, scale = 0)
	public Short getUnfSeqAvisa() {
		return this.unfSeqAvisa;
	}

	public void setUnfSeqAvisa(Short unfSeqAvisa) {
		this.unfSeqAvisa = unfSeqAvisa;
	}
	
	@Column(name = "IND_POSSUI_IMAGEM", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPossuiImagem() {
		return this.indPossuiImagem;
	}

	public void setIndPossuiImagem(Boolean p) {
		this.indPossuiImagem = p;
	}

	@Column(name = "IND_TICKET_PAC_IMP", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTicketPacImp() {
		return this.indTicketPacImp;
	}

	public void setIndTicketPacImp(Boolean t) {
		this.indTicketPacImp = t;
	}

	@Column(name = "IND_INF_COMPL_IMP", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInfComplImp() {
		return this.indInfComplImp;
	}

	public void setIndInfComplImp(Boolean c) {
		this.indInfComplImp = c;
	}

	@Column(name = "IND_TIPO_MSG_CX_POSTAL", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoMensagemCaixaPostal getIndTipoMsgCxPostal() {
		return this.indTipoMsgCxPostal;
	}

	public void setIndTipoMsgCxPostal(DominioTipoMensagemCaixaPostal s) {
		this.indTipoMsgCxPostal = s;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_MSG_CX_POSTAL", length = 7)
	public Date getDthrMsgCxPostal() {
		return this.dthrMsgCxPostal;
	}

	public void setDthrMsgCxPostal(Date dthrMsgCxPostal) {
		this.dthrMsgCxPostal = dthrMsgCxPostal;
	}

	@Column(name = "COMPLEMENTO_MOT_CANC", length = 2000)
	public String getComplementoMotCanc() {
		return this.complementoMotCanc;
	}

	public void setComplementoMotCanc(String complementoMotCanc) {
		this.complementoMotCanc = complementoMotCanc;
	}

	@Column(name = "NUMERO_AP_ORIGEM", precision = 8, scale = 0)
	public Integer getNumeroApOrigem() {
		return this.numeroApOrigem;
	}

	public void setNumeroApOrigem(Integer numeroApOrigem) {
		this.numeroApOrigem = numeroApOrigem;
	}

	@Column(name = "TIPO_TRANSPORTE_UN", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoTransporteUnidade getTipoTransporteUn() {
		return this.tipoTransporteUn;
	}

	public void setTipoTransporteUn(DominioTipoTransporteUnidade d) {
		this.tipoTransporteUn = d;
	}

	@Column(name = "IND_USO_O2_UN", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoO2Un() {
		return this.indUsoO2Un;
	}

	public void setIndUsoO2Un(Boolean indUsoO2Un) {
		this.indUsoO2Un = indUsoO2Un;
	}

	@Column(name = "PAC_ORU_ACC_NUMBER", length = 50)
	public String getPacOruAccNumber() {
		return this.pacOruAccNumber;
	}

	public void setPacOruAccNumber(String pacOruAccNumber) {
		this.pacOruAccNumber = pacOruAccNumber;
	}
	
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemSolicitacaoExame")
	public List<AelItemSolicExameHist> getItemSolicitacaoExames() {
		return this.itemSolicitacaoExames;
	}

	public void setItemSolicitacaoExames(List<AelItemSolicExameHist> aelItemSolicitacaoExames) {
		this.itemSolicitacaoExames = aelItemSolicitacaoExames;
	}
	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelItemSolicitacaoExames")
//	public List<AelAmostraItemExamesHist> getAelAmostraItemExames() {
//		return this.aelAmostraItemExames;
//	}
//
//	public void setAelAmostraItemExames(List<AelAmostraItemExamesHist> aelAmostraItemExames) {
//		this.aelAmostraItemExames = aelAmostraItemExames;
//	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemSolicitacaoExame")
//	public List<AelInformacaoSolicitacaoUnidadeExecutoraHist> getInformacaoSolicitacaoUnidadeExecutoras() {
//		return informacaoSolicitacaoUnidadeExecutoras;
//	}
//
//	public void setInformacaoSolicitacaoUnidadeExecutoras(List<AelInformacaoSolicitacaoUnidadeExecutoraHist> informacaoSolicitacaoUnidadeExecutoras) {
//		this.informacaoSolicitacaoUnidadeExecutoras = informacaoSolicitacaoUnidadeExecutoras;
//	}
	
//	public void addInformacaoSolicitacaoUnidadeExecutora(AelInformacaoSolicitacaoUnidadeExecutoraHist item) {
//		if (item != null) {
//			item.setItemSolicitacaoExame(this);
//			this.getInformacaoSolicitacaoUnidadeExecutoras().add(item);
//		}
//	}
	
	@Override
	public boolean equals(Object other) {
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

	@Transient
	public void addItemSolicitacaoExame(AelItemSolicExameHist item) {
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
		, DESC_REGIAO_ANATOMICA("descRegiaoAnatomica")
		, IND_GERADO_AUTOMATICO("indGeradoAutomatico")
		, INTERVALO_DIAS("intervaloDias")
		, INTERVALO_HORAS("intervaloHoras")
		, USO_O2_UNIDADE("indUsoO2Un")
		, USO_O2("indUsoO2")
		, TIPO_TRANSPORTE_UNIDADE("tipoTransporteUn")
		, FORMA_RESPIRACAO("formaRespiracao")
		, DESC_MATERIAL_ANALISE("descMaterialAnalise")
		, PRIORIDADE_EXECUCAO("prioridadeExecucao")
		, SIT_CODIGO("situacaoItemSolicitacao.codigo")
		, AEL_ITEM_SOLICITACAO_EXAMES_PAI("itemSolicitacaoExame")
		, IND_TICKET_PAC_IMP("indTicketPacImp")
		, IND_INF_COMPL_IMP("indInfComplImp")
		, DOC_RESULTADO_EXAME("docResultadoExame")
		, RESULTADO_EXAME("resultadoExames")
		, NUMERO_AP("numeroAp")
		, PAC_ORU_ACC_NUMMER("pacOruAccNumber")
		, AEL_UNF_EXECUTA_EXAMES("aelUnfExecutaExames")
		, ISE_SOE_SEQ("itemSolicitacaoExame.id.soeSeq")
		, ISE_SEQP("itemSolicitacaoExame.id.seqp")
		, NOTA_ADICIONAL("notaAdicional")
		, EXTRATO_ITEM_SOLIC("aelExtratoItemSolicitacao")
		, ETIOLOGIA("etiologiaInfeccao")
		, AEL_ORD_EXAME_MAT_ANALISE("aelOrdExameMatAnalise")
		, INTERVALO_COLETAS("intervaloColeta")
		, AEL_EXAME_AP_ITEM_SOLICS("aelExameApItemSolic")
		, AEL_MOTIVO_CANCELA_EXAMES("aelMotivoCancelaExames")
		, REGIAO_ANATOMICA("regiaoAnatomica")
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

	@Transient
	public void doSetSituacaoItensDependentes(AelSitItemSolicitacoes umaSituacao) {
		if (this.getItemSolicitacaoExames() != null) {
			for (AelItemSolicExameHist itemDependente : this.getItemSolicitacaoExames()) {
				itemDependente.setSituacaoItemSolicitacao(umaSituacao);
			}
		}
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelResultadosExamesHist> getResultadoExames() {
		return resultadoExames;
	}

	public void setResultadoExames(List<AelResultadosExamesHist> resultadoExames) {
		this.resultadoExames = resultadoExames;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
	public List<AelNotaAdicional> getNotaAdicional() {
		return notaAdicional;
	}

	public void setNotaAdicional(List<AelNotaAdicional> notaAdicional) {
		this.notaAdicional = notaAdicional;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	@Column(name = "UFE_EMA_EXA_SIGLA", updatable = false, insertable = false)
	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	@Transient
	public Date getDataHoraEventomaxExtratoItem() {
		return dataHoraEventomaxExtratoItem;
	}

	public void setDataHoraEventomaxExtratoItem(Date dataHoraEventomaxExtratoItem) {
		this.dataHoraEventomaxExtratoItem = dataHoraEventomaxExtratoItem;
	}

	@Transient
	public Long getQtdeNotaAdicional() {
		return qtdeNotaAdicional;
	}

	public void setQtdeNotaAdicional(Long qtdeNotaAdicional) {
		this.qtdeNotaAdicional = qtdeNotaAdicional;
	}

	@Transient
	public String getDescricaoGrupoMatAnalise() {
		return descricaoGrupoMatAnalise;
	}

	public void setDescricaoGrupoMatAnalise(String descricaoGrupoMatAnalise) {
		this.descricaoGrupoMatAnalise = descricaoGrupoMatAnalise;
	}

	@Transient
	public Integer getSeqMatAnalise() {
		return seqMatAnalise;
	}

	public void setSeqMatAnalise(Integer seqMatAnalise) {
		this.seqMatAnalise = seqMatAnalise;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "EMA_EXA_SIGLA", updatable = false, insertable = false),
			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "EMA_MAN_SEQ", updatable = false, insertable = false) })
	public AelOrdExameMatAnalise getAelOrdExameMatAnalise() {
		return aelOrdExameMatAnalise;}
	
	public void setAelOrdExameMatAnalise(AelOrdExameMatAnalise aelOrdExameMatAnalise) {
		this.aelOrdExameMatAnalise = aelOrdExameMatAnalise;}

//	public void setAelExameMaterialAnalise(AelExamesMaterialAnaliseHist aelExameMaterialAnalise) {
//		this.aelExameMaterialAnalise = aelExameMaterialAnalise;
//	}
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumns({
//			@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "EXA_SIGLA", updatable = false, insertable = false),
//			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", updatable = false, insertable = false) })
//	public AelExamesMaterialAnaliseHist getAelExameMaterialAnalise() {
//		return aelExameMaterialAnalise;
//	}	
//	
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="itemSolicitacaoExame")
//	public List<AelItemHorarioAgendadoHist> getItemHorarioAgendado() {
//		return itemHorarioAgendado;
//	}
//
//	public void setItemHorarioAgendado(List<AelItemHorarioAgendadoHist> itemHorarioAgendado) {
//		this.itemHorarioAgendado = itemHorarioAgendado;
//	}	

}
