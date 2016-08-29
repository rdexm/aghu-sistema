package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.dominio.DominioTipoOperacaoConversao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@MappedSuperclass
public abstract class FatProcedHospInternosPai extends BaseEntitySeq<Integer> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4181902871820884494L;
	
	protected Integer seq;
	protected FatProcedHospInternos procedimentoHospitalarInterno;
	
	protected String descricao;
	private String orientacoesFaturamento;
	private DominioSituacao situacao;
	private ScoMaterial material;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private MpmProcedEspecialDiversos procedimentoEspecialDiverso;
	private String csaCodigo;
	private AbsComponenteSanguineo componenteSanguineo;
	private String pheCodigo;
	private AbsProcedHemoterapico procedHemoterapico;
	private RapServidores servidor;
	private Date criadoEm;
	private AelExames emaExaSigla;
	private Integer emaManSeq;
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private Boolean componenteIrradiado;
	private DominioTipoNutricaoParenteral tipoNutrParenteral;
	private Boolean indOrigemPresc;
	private DominioTipoOperacaoConversao tipoOperConversao;
	private BigDecimal fatorConversao;
	private Short euuSeq;
	private MbcEquipamentoCirurgico equipamentoCirurgico;
	private Integer cduSeq;
	private MpmCuidadoUsual cuidadoUsual;
	private Short cuiSeq;
	private EpeCuidados cuidado;
	private MamItemMedicacao itemMedicacao;
	private MamItemExame itemExame;
	private Integer tidSeq;
	private Boolean indUtilizaKit;
	private DominioTipoNutricaoParenteral tipoNutricaoEnteral;
	private AnuGrupoQuadroDieta grupoQuadroDieta;

	private Set<AacProcedHospEspecialidades> procedHospEspecialidade  = new HashSet<AacProcedHospEspecialidades>(
			0);
	private Set<FatItemContaHospitalar> itensContaHospitalar = new HashSet<FatItemContaHospitalar>(
			0);
	private Set<FatExcCnvGrpItemProc> excCnvGrpItensProcs = new HashSet<FatExcCnvGrpItemProc>(
			0);
	private Set<FatProcedHospInternos> procedHospInternos = new HashSet<FatProcedHospInternos>(
			0);
	private Set<FatContasHospitalares> contasHospitalares = new HashSet<FatContasHospitalares>(
			0);
	private Set<FatConvGrupoItemProced> convGrupoItensProced = new HashSet<FatConvGrupoItemProced>(
			0);
	private Set<FatContasHospitalares> contasHospitalaresRealizadas = new HashSet<FatContasHospitalares>(
			0);
	private Set<FatCandidatosApacOtorrino> candidatosApacOtorrinosSugeridos = new HashSet<FatCandidatosApacOtorrino>(
			0);
	private Set<FatCandidatosApacOtorrino> candidatosApacOtorrinos = new HashSet<FatCandidatosApacOtorrino>(
			0);
	private Set<FatResumoApacs> resumoApacs = new HashSet<FatResumoApacs>(
			0);
	private Set<FatProcedHospIntCid> procedHospIntCids = new HashSet<FatProcedHospIntCid>(
			0);
	private Set<FatAgrupItemConta> agrupItensConta = new HashSet<FatAgrupItemConta>(
			0);
	private Set<FatProcedAmbRealizado> procedAmbRealizados = new HashSet<FatProcedAmbRealizado>(
			0);
	private Set<FatItemContaApac> itensContaApacs = new HashSet<FatItemContaApac>(
			0);
	private Set<FatResumoItemApac> resumoItensApacs = new HashSet<FatResumoItemApac>(
			0);
	private Set<FatProcedTratamento> procedTratamentos = new HashSet<FatProcedTratamento>(
			0);
	private Set<FatItemCtaAtend> itemCtaAtends = new HashSet<FatItemCtaAtend>(
			0);
	private Set<FatAtendimentoApacProcHosp> atendimentoApacProcHosps = new HashSet<FatAtendimentoApacProcHosp>(
			0);
	private Set<AacConsultaProcedHospitalar> consultaProcedHospitalar  = new HashSet<AacConsultaProcedHospitalar>(
			0);
	private Set<SigDetalheProducao> detalheProducao = new HashSet<SigDetalheProducao>(0);
	private Set<SigObjetoCustoPhis> objetoCustoPhis = new HashSet<SigObjetoCustoPhis>(0);
	
	private Boolean phiRelacionadoComCid;
	private Integer version;
	
	private String tipoEtiqueta;
	
	private Set<VFatAssociacaoProcedimento> vwFatAssociacaoProcedimento = new HashSet<VFatAssociacaoProcedimento>();

	public FatProcedHospInternosPai() {
	}

	public FatProcedHospInternosPai(Integer seq, String descricao, DominioSituacao situacao) {
		this.seq = seq;
		this.descricao = descricao;
		this.situacao = situacao;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public FatProcedHospInternosPai(
			Integer seq,
			FatProcedHospInternos fatProcedHospInternos,
			String descricao,
			String orientacoesFaturamento,
			DominioSituacao situacao,
			ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedimentoEspecialDiverso,
			String csaCodigo,
			String pheCodigo,
			RapServidores servidor,
			Date criadoEm,
			AelExames emaExaSigla,
			Integer emaManSeq,
			Boolean componenteIrradiado,
			DominioTipoNutricaoParenteral tipoNutrParenteral,
			Boolean indOrigemPresc,
			DominioTipoOperacaoConversao tipoOperConversao,
			BigDecimal fatorConversao,
			Short euuSeq,
			Integer cduSeq,
			Short cuiSeq,
			MamItemMedicacao itemMedicacao,
			MamItemExame itemExame,
			Integer tidSeq,
			Boolean indUtilizaKit,
			DominioTipoNutricaoParenteral tipoNutricaoEnteral,
			Set<FatItemContaHospitalar> fatItensContaHospitalares,
			Set<FatExcCnvGrpItemProc> fatExcCnvGrpItemProcs,
			Set<FatProcedHospInternos> fatProcedHospInternoses,
			Set<FatContasHospitalares> fatContasHospitalaresesForPhiSeq,
			Set<FatConvGrupoItemProced> fatConvGrupoItemProceds,
			Set<FatContasHospitalares> fatContasHospitalaresesForPhiSeqRealizado,
			Set<FatCandidatosApacOtorrino> fatCandidatosApacOtorrinosForPhiSeqSugerido,
			Set<FatCandidatosApacOtorrino> fatCandidatosApacOtorrinosForPhiSeq,
			Set<FatResumoApacs> fatResumoApacses,
			Set<FatProcedHospIntCid> fatProcedHospIntCids,
			Set<FatAgrupItemConta> fatAgrupItemContas,
			Set<FatProcedAmbRealizado> fatProcedAmbRealizadoses,
			Set<FatItemContaApac> fatItemContaApacs,
			Set<FatResumoItemApac> fatResumoItemApacs,
			Set<FatProcedTratamento> fatProcedTratamentoses,
			Set<FatItemCtaAtend> fatItemCtaAtendses,
			Set<FatAtendimentoApacProcHosp> fatAtendimentoApacProcHosps,
			Set<VFatAssociacaoProcedimento> vFatAssociacaoProcedimento) {
		this.seq = seq;
		this.procedimentoHospitalarInterno = fatProcedHospInternos;
		this.descricao = descricao;
		this.orientacoesFaturamento = orientacoesFaturamento;
		this.situacao = situacao;
		this.material = matCodigo;
		this.procedimentoCirurgico = procedimentoCirurgico;
		this.procedimentoEspecialDiverso = procedimentoEspecialDiverso;
		this.csaCodigo = csaCodigo;
		this.pheCodigo = pheCodigo;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.emaExaSigla = emaExaSigla;
		this.emaManSeq = emaManSeq;
		this.componenteIrradiado = componenteIrradiado;
		this.tipoNutrParenteral = tipoNutrParenteral;
		this.indOrigemPresc = indOrigemPresc;
		this.tipoOperConversao = tipoOperConversao;
		this.fatorConversao = fatorConversao;
		this.euuSeq = euuSeq;
		this.cduSeq = cduSeq;
		this.cuiSeq = cuiSeq;
		this.itemMedicacao = itemMedicacao;
		this.itemExame = itemExame;
		this.tidSeq = tidSeq;
		this.indUtilizaKit = indUtilizaKit;
		this.tipoNutricaoEnteral = tipoNutricaoEnteral;
		this.itensContaHospitalar = fatItensContaHospitalares;
		this.excCnvGrpItensProcs = fatExcCnvGrpItemProcs;
		this.procedHospInternos = fatProcedHospInternoses;
		this.contasHospitalares = fatContasHospitalaresesForPhiSeq;
		this.convGrupoItensProced = fatConvGrupoItemProceds;
		this.contasHospitalaresRealizadas = fatContasHospitalaresesForPhiSeqRealizado;
		this.candidatosApacOtorrinosSugeridos = fatCandidatosApacOtorrinosForPhiSeqSugerido;
		this.candidatosApacOtorrinos = fatCandidatosApacOtorrinosForPhiSeq;
		this.resumoApacs = fatResumoApacses;
		this.procedHospIntCids = fatProcedHospIntCids;
		this.agrupItensConta = fatAgrupItemContas;
		this.procedAmbRealizados = fatProcedAmbRealizadoses;
		this.itensContaApacs = fatItemContaApacs;
		this.resumoItensApacs = fatResumoItemApacs;
		this.procedTratamentos = fatProcedTratamentoses;
		this.itemCtaAtends = fatItemCtaAtendses;
		this.atendimentoApacProcHosps = fatAtendimentoApacProcHosps;
		this.vwFatAssociacaoProcedimento = vFatAssociacaoProcedimento;
	}

	@Column(name = "SEQ", insertable = false, updatable = false, precision = 6)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ")
	public FatProcedHospInternos getProcedimentoHospitalarInterno() {
		return procedimentoHospitalarInterno;
	}

	public void setProcedimentoHospitalarInterno(
			FatProcedHospInternos procedimentoHospitalarInterno) {
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 250)
	@Length(max = 250)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORIENTACOES_FATURAMENTO", length = 2000)
	@Length(max = 2000)
	public String getOrientacoesFaturamento() {
		return this.orientacoesFaturamento;
	}

	public void setOrientacoesFaturamento(String orientacoesFaturamento) {
		this.orientacoesFaturamento = orientacoesFaturamento;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length=1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = true)	
	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return this.procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(MbcProcedimentoCirurgicos pciSeq) {
		this.procedimentoCirurgico = pciSeq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PED_SEQ", nullable = true)	
	public MpmProcedEspecialDiversos getProcedimentoEspecialDiverso() {
		return this.procedimentoEspecialDiverso;
	}

	public void setProcedimentoEspecialDiverso(MpmProcedEspecialDiversos pedSeq) {
		this.procedimentoEspecialDiverso = pedSeq;
	}
	
	@Column(name = "CSA_CODIGO", length = 2)
	@Length(max = 2)
	public String getCsaCodigo() {
		return this.csaCodigo;
	}

	public void setCsaCodigo(String csaCodigo) {
		this.csaCodigo = csaCodigo;
	}

	//Retirar o insertable = false, updatable = false quando o mapeamento csaCodigo for retirado.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CSA_CODIGO", nullable = true, insertable = false, updatable = false)	
	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}
	
	
	@Column(name = "PHE_CODIGO", length = 2)
	@Length(max = 2)
	public String getPheCodigo() {
		return this.pheCodigo;
	}

	public void setPheCodigo(String pheCodigo) {
		this.pheCodigo = pheCodigo;
	}
	
	//Retirar o insertable = false, updatable = false quando o mapeamento pheCodigo for retirado.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHE_CODIGO", nullable = true, insertable = false, updatable = false)
	public AbsProcedHemoterapico getProcedHemoterapico() {
		return procedHemoterapico;
	}

	public void setProcedHemoterapico(AbsProcedHemoterapico procedHemoterapico) {
		this.procedHemoterapico = procedHemoterapico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = true),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/*@Column(name = "EMA_EXA_SIGLA", length = 5)
	@Length(max = 5)
	public String getEmaExaSigla() {
		return this.emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.aelExame = emaExaSigla;
	}
	*/

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="EMA_EXA_SIGLA")	
	public AelExames getEmaExaSigla() {
		return this.emaExaSigla;
	}
		
	public void setEmaExaSigla(AelExames aelExame) {
		this.emaExaSigla = aelExame;
	}
	
	@Column(name = "EMA_MAN_SEQ", precision = 5, scale = 0)
	public Integer getEmaManSeq() {
		return this.emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}
	

	//Retirar o insertable = false, updatable = false quando o mapeamento csaCodigo for retirado.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "EMA_EXA_SIGLA", referencedColumnName = "EXA_SIGLA", nullable = true, insertable = false, updatable = false),
			@JoinColumn(name = "EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", nullable = true, insertable = false, updatable = false)})
	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(
			AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	@Column(name = "COMPONENTE_IRRADIADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getComponenteIrradiado() {
		return this.componenteIrradiado;
	}

	public void setComponenteIrradiado(Boolean componenteIrradiado) {
		this.componenteIrradiado = componenteIrradiado;
	}

	@Column(name = "TIPO_NUTR_PARENTERAL", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoNutricaoParenteral getTipoNutrParenteral() {
		return this.tipoNutrParenteral;
	}

	public void setTipoNutrParenteral(DominioTipoNutricaoParenteral tipoNutrParenteral) {
		this.tipoNutrParenteral = tipoNutrParenteral;
	}

	@Column(name = "IND_ORIGEM_PRESC", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndOrigemPresc() {
		return this.indOrigemPresc;
	}

	public void setIndOrigemPresc(Boolean indOrigemPresc) {
		this.indOrigemPresc = indOrigemPresc;
	}

	@Column(name = "TIPO_OPER_CONVERSAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoOperacaoConversao getTipoOperConversao() {
		return this.tipoOperConversao;
	}

	public void setTipoOperConversao(DominioTipoOperacaoConversao tipoOperConversao) {
		this.tipoOperConversao = tipoOperConversao;
	}

	@Column(name = "FATOR_CONVERSAO", precision = 7, scale = 3)
	public BigDecimal getFatorConversao() {
		return this.fatorConversao;
	}

	public void setFatorConversao(BigDecimal fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	@Column(name = "EUU_SEQ", precision = 4, scale = 0)
	public Short getEuuSeq() {
		return this.euuSeq;
	}

	public void setEuuSeq(Short euuSeq) {
		this.euuSeq = euuSeq;
	}
	
	//Retirar o insertable = false, updatable = false quando o mapeamento euuSeq for retirado.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EUU_SEQ", insertable = false, updatable = false)
	public MbcEquipamentoCirurgico getEquipamentoCirurgico() {
		return equipamentoCirurgico;
	}

	public void setEquipamentoCirurgico(MbcEquipamentoCirurgico equipamentoCirurgico) {
		this.equipamentoCirurgico = equipamentoCirurgico;
	}	

	@Column(name = "CDU_SEQ", precision = 6, scale = 0)
	public Integer getCduSeq() {
		return this.cduSeq;
	}

	public void setCduSeq(Integer cduSeq) {
		this.cduSeq = cduSeq;
	}

	
	//Retirar o insertable = false, updatable = false quando o mapeamento tiver ok.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDU_SEQ", insertable = false, updatable = false)
	public MpmCuidadoUsual getCuidadoUsual() {
		return cuidadoUsual;
	}

	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}	
	
	@Column(name = "CUI_SEQ", precision = 4, scale = 0)
	public Short getCuiSeq() {
		return this.cuiSeq;
	}

	public void setCuiSeq(Short cuiSeq) {
		this.cuiSeq = cuiSeq;
	}

	//Retirar o insertable = false, updatable = false quando o mapeamento tiver ok
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUI_SEQ", insertable = false, updatable = false)
	public EpeCuidados getCuidado() {
		return cuidado;
	}

	public void setCuidado(EpeCuidados cuidado) {
		this.cuidado = cuidado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDM_SEQ")
	public MamItemMedicacao getItemMedicacao() {
		return this.itemMedicacao;
	}

	public void setItemMedicacao(MamItemMedicacao itemMedicacao) {
		this.itemMedicacao = itemMedicacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMS_SEQ")
	public MamItemExame getItemExame() {
		return this.itemExame;
	}

	public void setItemExame(MamItemExame itemExame) {
		this.itemExame = itemExame;
	}

	@Column(name = "TID_SEQ", unique = true, precision = 5, scale = 0)
	public Integer getTidSeq() {
		return this.tidSeq;
	}

	public void setTidSeq(Integer tidSeq) {
		this.tidSeq = tidSeq;
	}

	@Column(name = "IND_UTILIZA_KIT", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUtilizaKit() {
		return this.indUtilizaKit;
	}

	public void setIndUtilizaKit(Boolean indUtilizaKit) {
		this.indUtilizaKit = indUtilizaKit;
	}

	@Column(name = "TIPO_NUTRICAO_ENTERAL", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoNutricaoParenteral getTipoNutricaoEnteral() {
		return this.tipoNutricaoEnteral;
	}

	public void setTipoNutricaoEnteral(DominioTipoNutricaoParenteral tipoNutricaoEnteral) {
		this.tipoNutricaoEnteral = tipoNutricaoEnteral;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GQD_SEQ", nullable = true)
	public AnuGrupoQuadroDieta getGrupoQuadroDieta() {
		return grupoQuadroDieta;
	}

	public void setGrupoQuadroDieta(AnuGrupoQuadroDieta grupoQuadroDieta) {
		this.grupoQuadroDieta = grupoQuadroDieta;
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatItemContaHospitalar> getItensContaHospitalar() {
		return itensContaHospitalar;
	}

	public void setItensContaHospitalar(
			Set<FatItemContaHospitalar> itensContaHospitalar) {
		this.itensContaHospitalar = itensContaHospitalar;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatExcCnvGrpItemProc> getExcCnvGrpItensProcs() {
		return excCnvGrpItensProcs;
	}

	public void setExcCnvGrpItensProcs(
			Set<FatExcCnvGrpItemProc> excCnvGrpItensProcs) {
		this.excCnvGrpItensProcs = excCnvGrpItensProcs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatProcedHospInternos> getProcedHospInternos() {
		return procedHospInternos;
	}

	public void setProcedHospInternos(Set<FatProcedHospInternos> procedHospInternos) {
		this.procedHospInternos = procedHospInternos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInternoRealizado")
	public Set<FatContasHospitalares> getContasHospitalaresRealizadas() {
		return contasHospitalaresRealizadas;
	}

	public void setContasHospitalaresRealizadas(
			Set<FatContasHospitalares> contasHospitalaresRealizadas) {
		this.contasHospitalaresRealizadas = contasHospitalaresRealizadas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatConvGrupoItemProced> getConvGrupoItensProced() {
		return convGrupoItensProced;
	}

	public void setConvGrupoItensProced(
			Set<FatConvGrupoItemProced> convGrupoItensProced) {
		this.convGrupoItensProced = convGrupoItensProced;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatContasHospitalares> getContasHospitalares() {
		return contasHospitalares;
	}

	public void setContasHospitalares(Set<FatContasHospitalares> contasHospitalares) {
		this.contasHospitalares = contasHospitalares;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarSugerido")
	public Set<FatCandidatosApacOtorrino> getCandidatosApacOtorrinosSugeridos() {
		return candidatosApacOtorrinosSugeridos;
	}

	public void setCandidatosApacOtorrinosSugeridos(
			Set<FatCandidatosApacOtorrino> candidatosApacOtorrinosSugeridos) {
		this.candidatosApacOtorrinosSugeridos = candidatosApacOtorrinosSugeridos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatCandidatosApacOtorrino> getCandidatosApacOtorrinos() {
		return candidatosApacOtorrinos;
	}

	public void setCandidatosApacOtorrinos(
			Set<FatCandidatosApacOtorrino> candidatosApacOtorrinos) {
		this.candidatosApacOtorrinos = candidatosApacOtorrinos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatResumoApacs> getResumoApacs() {
		return resumoApacs;
	}

	public void setResumoApacs(Set<FatResumoApacs> resumoApacs) {
		this.resumoApacs = resumoApacs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatProcedHospIntCid> getProcedHospIntCids() {
		return procedHospIntCids;
	}

	public void setProcedHospIntCids(Set<FatProcedHospIntCid> procedHospIntCids) {
		this.procedHospIntCids = procedHospIntCids;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatAgrupItemConta> getAgrupItensConta() {
		return agrupItensConta;
	}

	public void setAgrupItensConta(Set<FatAgrupItemConta> agrupItensConta) {
		this.agrupItensConta = agrupItensConta;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatProcedAmbRealizado> getProcedAmbRealizados() {
		return procedAmbRealizados;
	}

	public void setProcedAmbRealizados(
			Set<FatProcedAmbRealizado> procedAmbRealizados) {
		this.procedAmbRealizados = procedAmbRealizados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatItemContaApac> getItensContaApacs() {
		return itensContaApacs;
	}

	public void setItensContaApacs(Set<FatItemContaApac> itensContaApacs) {
		this.itensContaApacs = itensContaApacs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatResumoItemApac> getResumoItensApacs() {
		return resumoItensApacs;
	}

	public void setResumoItensApacs(Set<FatResumoItemApac> resumoItensApacs) {
		this.resumoItensApacs = resumoItensApacs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatProcedTratamento> getProcedTratamentos() {
		return procedTratamentos;
	}

	public void setProcedTratamentos(Set<FatProcedTratamento> procedTratamentos) {
		this.procedTratamentos = procedTratamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatItemCtaAtend> getItemCtaAtends() {
		return itemCtaAtends;
	}

	public void setItemCtaAtends(Set<FatItemCtaAtend> itemCtaAtends) {
		this.itemCtaAtends = itemCtaAtends;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<FatAtendimentoApacProcHosp> getAtendimentoApacProcHosps() {
		return atendimentoApacProcHosps;
	}

	public void setAtendimentoApacProcHosps(
			Set<FatAtendimentoApacProcHosp> atendimentoApacProcHosps) {
		this.atendimentoApacProcHosps = atendimentoApacProcHosps;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", nullable = true)
	public ScoMaterial getMaterial() {
		return this.material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@Transient
	public Boolean getPhiRelacionadoComCid() {
		return phiRelacionadoComCid;
	}

	public void setPhiRelacionadoComCid(Boolean phiRelacionadoComCid) {
		this.phiRelacionadoComCid = phiRelacionadoComCid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedHospInterno")
	public Set<AacProcedHospEspecialidades> getProcedHospEspecialidade() {
		return procedHospEspecialidade;
	}

	public void setProcedHospEspecialidade(
			Set<AacProcedHospEspecialidades> procedHospEspecialidade) {
		this.procedHospEspecialidade = procedHospEspecialidade;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedHospInterno")
	public Set<AacConsultaProcedHospitalar> getConsultaProcedHospitalar() {
		return consultaProcedHospitalar;
	}

	public void setConsultaProcedHospitalar(
			Set<AacConsultaProcedHospitalar> consultaProcedHospitalar) {
		this.consultaProcedHospitalar = consultaProcedHospitalar;
	}	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fatProcedHospInternos")
	public Set<SigDetalheProducao> getDetalheProducao() {
		return detalheProducao;
	}

	public void setDetalheProducao(Set<SigDetalheProducao> detalheProducao) {
		this.detalheProducao = detalheProducao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fatProcedHospInternos")
	public Set<SigObjetoCustoPhis> getObjetoCustoPhis() {
		return objetoCustoPhis;
	}

	public void setObjetoCustoPhis(Set<SigObjetoCustoPhis> objetoCustoPhis) {
		this.objetoCustoPhis = objetoCustoPhis;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoHospitalarInterno")
	public Set<VFatAssociacaoProcedimento> getVwFatAssociacaoProcedimento(){
		return this.vwFatAssociacaoProcedimento;
	}
	
	public void setVwFatAssociacaoProcedimento(
			Set<VFatAssociacaoProcedimento> vwFatAssociacaoProcedimento) {
		this.vwFatAssociacaoProcedimento = vwFatAssociacaoProcedimento;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "TIPO_ETIQUETA", nullable = true, length = 1)
	public String getTipoEtiqueta() {
		return tipoEtiqueta;
	}

	public void setTipoEtiqueta(String tipoEtiqueta) {
		this.tipoEtiqueta = tipoEtiqueta;
	}

	public enum Fields {
		PROCED_HOSP_INT_CIDS("procedHospIntCids"), MAT_CODIGO(
				"material.codigo"), PCI_SEQ("procedimentoCirurgico.seq"), 
				MATERIAIS("material"), PROCEDIMENTO_CIRURGICO(
				"procedimentoCirurgico"), PROCEDIMENTO_ESPECIAL_DIVERSO(
				"procedimentoEspecialDiverso"), 
				PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ("procedimentoEspecialDiverso.seq"),
				SEQ("seq"), 
				EMA_MAN_SEQ("emaManSeq"),
				EMA_EXA_SIGLA("emaExaSigla.sigla"),
				EXAME_MATERIAL_ANALISE("exameMaterialAnalise"),
				FAT_PROCEDHOSP_INTERNOS_SEQ("procedimentoHospitalarInterno.seq"),
				CSA_CODIGO("csaCodigo"),
				COMPONENTE_SANGUINEO("componenteSanguineo"),
				PHE_CODIGO("pheCodigo"),
				PROCED_HEMOTERAPICO("procedHemoterapico"),
				TIPO_NUTR_PARENTERAL("tipoNutrParenteral"), DESCRICAO("descricao"), CDU_SEQ("cduSeq"), 
				TDI_SEQ("tidSeq"), EUU_SEQ("euuSeq"), CUI_SEQ("cuiSeq"), SITUACAO("situacao"),
				FAT_PROCED_INT_CIDS("procedHospIntCids"), CONV_GRUPO_ITENS_PROCED("convGrupoItensProced"),
				TIPO_OPER_CONVERSAO("tipoOperConversao"),
				FATOR_CONVERSAO("fatorConversao"), 
				CONTAS_HOSPITALARES("contasHospitalares"), 
				CONTAS_HOSPITALARES_REALIZADAS("contasHospitalaresRealizadas"),
				ITENS_CONTA_HOSPITALAR("itensContaHospitalar"), 
				PROCEDIMENTO_HOSPITALAR_INTERNO("procedimentoHospitalarInterno"),
				IND_ORIGEM_PRESC("indOrigemPresc"),
				TIPO_NUTRICAO_ENTERAL("tipoNutricaoEnteral"),
				PHI_SEQ("procedimentoHospitalarInterno.seq"),
				PROCED_HOSP_ESPECIALIDADE("procedHospEspecialidade"),
				CONSULTA_PROCED_HOSPITALAR("consultaProcedHospitalar"),
				ITEM_MEDICACAO("itemMedicacao"),
				MDM_SEQ("itemMedicacao.seq"),
				ITEM_EXAME("itemExame"),
				EMS_SEQ("itemExame.seq"),
				EXAME_MATERIAL("emaExaSigla"),
				DETALHE_PRODUCAO("detalheProducao"),
				OBJETO_CUSTO_PHI("objetoCustoPhis"),
				EQUIPAMENTO_CIRURGICOS("equipamentoCirurgico"), 
				GRUPO_QUADRO_DIETA("grupoQuadroDieta"),
				PROCED_HOSP_INTERNOS("procedHospInternos"),
				PROCED_AMB_REALIZADOS("procedAmbRealizados"),
				PROCED_TRATAMENTOS("procedTratamentos"),
				TIPO_ETIQUETA("tipoEtiqueta"),
				V_FAT_ASSOCIACAO_PROCD_HOSP("vwFatAssociacaoProcedimento");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		if (!(obj instanceof FatProcedHospInternos)) {
			return false;
		}
		FatProcedHospInternos other = (FatProcedHospInternos) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
}
