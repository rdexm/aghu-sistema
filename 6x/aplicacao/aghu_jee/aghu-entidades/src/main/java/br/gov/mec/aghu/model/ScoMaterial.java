package br.gov.mec.aghu.model;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;



import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioIndFotoSensibilidade;
import br.gov.mec.aghu.dominio.DominioIndProducaoInterna;
import br.gov.mec.aghu.dominio.DominioIndTipoUso;
import br.gov.mec.aghu.dominio.DominioSazonalidade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoResiduo;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity","PMD.ExcessiveClassLength"})
@Entity
@Table(name = "SCO_MATERIAIS", schema = "AGH")
@SequenceGenerator(name = "scoMatSq1", sequenceName = "agh.sco_mat_sq1", allocationSize = 1)
@Indexed
public class ScoMaterial extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -2978327858968545226L;
	private Integer codigo;
	private ScoUnidadeMedida unidadeMedida;
	private RapServidores servidor;
	private RapServidores servidorDesativado;
	private String descricao;
	private Date dtDigitacao;
	private DominioSituacao indSituacao;
	private Boolean estocavel;
	private DominioSimNao indGenerico;
	private DominioSimNao indMenorPreco;
	private DominioIndProducaoInterna indProducaoInterna;
	private DominioSimNao indAtuQtdeDisponivel;
	private DominioClassifyXYZ classifXyz;
	private DominioSazonalidade sazonalidade;
	private String nome;
	private String observacao;
	private Date dtAlteracao;
	private Date dtDesativacao;
	private DominioSimNao indControleValidade;
	private DominioSimNao indFaturavel;
	private DominioSimNao indEnvioProdInterna;
	private SceAlmoxarifado almLocalEstq;
	private RapServidores servidorAlteracao;
	private DominioSimNao indPadronizado;
	private DominioSimNao indCcih;
	private Boolean indControleLote;
	private DominioIndFotoSensibilidade indFotosensivel;
	private DominioIndTipoUso indTipoUso;
	private BigDecimal numero;
	private String indCadSapiens;
	private Date dtCadSapiens;
	private RapServidores servidorCadSapiens;
	private Short codRetencao;
	private String codTransacaoSapiens;
	private Integer codSiasg;
	private Integer codCatmat;
	private Long codMatAntigo;
	private String codSitTribSapiens;
	private String ncmCodigo;
	private AfaMedicamento afaMedicamento;
	private Set<SceEstoqueGeral> estoquesGerais;
	private Set<ScoSolicitacaoDeCompra> solicitacoesDeCompras;
	private ScoGrupoMaterial grupoMaterial;
	private SceAlmoxarifado almoxarifado;
	private ScoOrigemParecerTecnico origemParecerTecnico;
	private Set<ScoMarcaModelo> marcasModelos;
	private Set<SceEstoqueAlmoxarifado> estoquesAlmoxarifado;
	private Set<SceFornecedorMaterial> fornecedoresMateriais;
	private Set<FatProcedHospInternos> procedimentosHospitalaresInternos;
	private Set<ScoMaterialSicon> materiaisSicon;
	private Set<ScoMateriaisClassificacoes> materiaisClassificacoes;
	private Set<SceMovimentoMaterial> movimentosMaterial;
	private Integer codigoEsperanto;
	private Set<SceConversaoUnidadeConsumos> conversaoUnidadeConsumo;
	private Boolean indCorrosivo;
	private Boolean indInflamavel;
	private Boolean indRadioativo;
	private Boolean indReativo;
	private Boolean indToxico;
	private Boolean indUtilizaEspacoFisico;
	private DominioTipoResiduo indTipoResiduo;
	private Boolean indTermolabil;
	private Boolean indVinculado;
	private Boolean indSustentavel;
	private BigDecimal temperatura;
	private String legislacao;
	private DominioSimNao indConfaz;
	private DominioSimNao indCapCmed;
	private Integer serMatriculaJusProcRel;
	private String justificativaProcRel;
	private Date dataJusProcRel;
	private Short serVinCodigoJusProcRel;
	private Set<ScoMaterialVinculo> materialVinculo;
	private Date dataAlteracaoCap;
    private Date dataAlteracaoConfaz;
    private String justificativaCatmat;
    private Set<MpmProcedEspecialRm> mpmProcedEspecialRm;
    private Set<SceItemEntrSaidSemLicitacao> sceItemEntrSaidSemLicitacao;
    private String umdCod;
    private Date dataJusCatmat;
    private RapServidores servidorJustificativa;


	public ScoMaterial() {
	}

	public ScoMaterial(Integer codigo) {
		this.codigo = codigo;
	}
	
	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 6, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoMatSq1")
	public Integer getCodigo() {
		return this.codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "materialVinculo")
	public Set<ScoMaterialVinculo> getMaterialVinculo() {
		return materialVinculo;
	}
	
	public void setMaterialVinculo(Set<ScoMaterialVinculo> materialVinculo) {
		this.materialVinculo = materialVinculo;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_DESATIVADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_DESATIVADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorDesativado() {
		return this.servidorDesativado;
	}
	public void setServidorDesativado(RapServidores servidorDesativado) {
		this.servidorDesativado = servidorDesativado;
	}
	
	@Column(name = "DESCRICAO", length = 2000)
	public String getDescricao() {
		return this.descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_DIGITACAO", nullable = false)
	public Date getDtDigitacao() {
		return this.dtDigitacao;
	}
	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	public Set<ScoSolicitacaoDeCompra> getSolicitacoesDeCompras() {
		return solicitacoesDeCompras;
	}
	public void setSolicitacoesDeCompras(Set<ScoSolicitacaoDeCompra> solicitacoesDeCompras) {
		this.solicitacoesDeCompras = solicitacoesDeCompras;
	}
	
	@Transient
	public boolean isIndSituacaoBoolean() {
		if ("A".equals(getIndSituacao())) {
			return getIndSituacao() == DominioSituacao.A;
		} else {
			return getIndSituacao() == DominioSituacao.I;
		}
	}
	
	@Transient
	public String getDescricaoIndSituacao() {
		return getIndSituacao() == null ? null : getIndSituacao().getDescricao();
	}
	
	@Column(name = "IND_ESTOCAVEL", nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEstocavel() {
		return this.estocavel;
	}
	public void setEstocavel(Boolean estocavel) {
		this.estocavel = estocavel;
	}
	
	@Transient
	public DominioSimNao getIndEstocavel() {
		return (this.estocavel != null) ? DominioSimNao.getInstance(estocavel) : null;
	}
	
	@Column(name = "IND_GENERICO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndGenerico() {
		return this.indGenerico;
	}
	public void setIndGenerico(DominioSimNao indGenerico) {
		this.indGenerico = indGenerico;
	}
	
	@Column(name = "IND_MENOR_PRECO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndMenorPreco() {
		return this.indMenorPreco;
	}
	public void setIndMenorPreco(DominioSimNao indMenorPreco) {
		this.indMenorPreco = indMenorPreco;
	}
	
	@Column(name = "IND_PRODUCAO_INTERNA")
	@Enumerated(EnumType.STRING)
	public DominioIndProducaoInterna getIndProducaoInterna() {
		return this.indProducaoInterna;
	}
	public void setIndProducaoInterna(DominioIndProducaoInterna indProducaoInterna) {
		this.indProducaoInterna = indProducaoInterna;
	}
	
	@Column(name = "IND_ATU_QTDE_DISPONIVEL", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndAtuQtdeDisponivel() {
		return this.indAtuQtdeDisponivel;
	}
	public void setIndAtuQtdeDisponivel(DominioSimNao indAtuQtdeDisponivel) {
		this.indAtuQtdeDisponivel = indAtuQtdeDisponivel;
	}
	
	@Column(name = "CLASSIF_XYZ")
	@Enumerated(EnumType.STRING)
	public DominioClassifyXYZ getClassifXyz() {
		return this.classifXyz;
	}
	public void setClassifXyz(DominioClassifyXYZ classifXyz) {
		this.classifXyz = classifXyz;
	}
	
	@Column(name = "SAZONALIDADE")
	@Enumerated(EnumType.STRING)
	public DominioSazonalidade getSazonalidade() {
		return this.sazonalidade;
	}
	public void setSazonalidade(DominioSazonalidade sazonalidade) {
		this.sazonalidade = sazonalidade;
	}
	
	@Column(name = "NOME", nullable = false, length = 250)
	@Field(index = Index.YES, store = Store.YES)
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Transient
	@Field(index = Index.NO , store = Store.YES)
	public String getNomeOrdenacao(){
		return this.nome;
	}
	
	@Column(name = "OBSERVACAO", length = 500)
	public String getObservacao() {
		return this.observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ALTERACAO")
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}
	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_DESATIVACAO")
	public Date getDtDesativacao() {
		return this.dtDesativacao;
	}
	public void setDtDesativacao(Date dtDesativacao) {
		this.dtDesativacao = dtDesativacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GMT_CODIGO", nullable = false)
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPT_CODIGO")
	public ScoOrigemParecerTecnico getOrigemParecerTecnico() {
		return this.origemParecerTecnico;
	}
	public void setOrigemParecerTecnico(ScoOrigemParecerTecnico origemParecerTecnico) {
		this.origemParecerTecnico = origemParecerTecnico;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALM_SEQ", nullable = false)
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}
	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	
	@Column(name = "IND_CONTROLE_VALIDADE", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndControleValidade() {
		return this.indControleValidade;
	}
	public void setIndControleValidade(DominioSimNao indControleValidade) {
		this.indControleValidade = indControleValidade;
	}
	
	@Column(name = "IND_FATURAVEL", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndFaturavel() {
		return this.indFaturavel;
	}
	public void setIndFaturavel(DominioSimNao indFaturavel) {
		this.indFaturavel = indFaturavel;
	}
	
	@Column(name = "IND_ENVIO_PROD_INTERNA")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndEnvioProdInterna() {
		return this.indEnvioProdInterna;
	}
	public void setIndEnvioProdInterna(DominioSimNao indEnvioProdInterna) {
		this.indEnvioProdInterna = indEnvioProdInterna;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALM_SEQ_LOCAL_ESTQ", referencedColumnName = "SEQ")
	public SceAlmoxarifado getAlmLocalEstq() {
		return this.almLocalEstq;
	}
	public void setAlmLocalEstq(SceAlmoxarifado almLocalEstq) {
		this.almLocalEstq = almLocalEstq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERACAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlteracao() {
		return this.servidorAlteracao;
	}
	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}
	
	@Column(name = "IND_PADRONIZADO")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndPadronizado() {
		return this.indPadronizado;
	}
	public void setIndPadronizado(DominioSimNao indPadronizado) {
		this.indPadronizado = indPadronizado;
	}
	
	@Column(name = "IND_CCIH", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndCcih() {
		return this.indCcih;
	}
	public void setIndCcih(DominioSimNao indCcih) {
		this.indCcih = indCcih;
	}
	
	@Column(name = "IND_CORROSIVO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCorrosivo() {
		return this.indCorrosivo;
	}
	public void setIndCorrosivo(Boolean indCorrosivo) {
		this.indCorrosivo = indCorrosivo;
	}

	@Column(name = "IND_INFLAMAVEL")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInflamavel() {
		return this.indInflamavel;
	}
	public void setIndInflamavel(Boolean indInflamavel) {
		this.indInflamavel = indInflamavel;
	}

	@Column(name = "IND_RADIOATIVO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRadioativo() {
		return this.indRadioativo;
	}
	public void setIndRadioativo(Boolean indRadioativo) {
		this.indRadioativo = indRadioativo;
	}

	@Column(name = "IND_REATIVO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndReativo() {
		return this.indReativo;
	}
	public void setIndReativo(Boolean indReativo) {
		this.indReativo = indReativo;
	}

	@Column(name = "IND_TOXICO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndToxico() {
		return this.indToxico;
	}
	public void setIndToxico(Boolean indToxico) {
		this.indToxico = indToxico;
	}

	@Column(name = "IND_ESPACO_ARMAZENA")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUtilizaEspacoFisico() {
		return this.indUtilizaEspacoFisico;
	}

	public void setIndUtilizaEspacoFisico(Boolean indUtilizaEspacoFisico) {
		this.indUtilizaEspacoFisico = indUtilizaEspacoFisico;
	}

	@Column(name = "IND_CONTROLE_LOTE")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndControleLote() {
		return this.indControleLote;
	}
	public void setIndControleLote(Boolean indControleLote) {
		this.indControleLote = indControleLote;
	}
	
	@Column(name = "IND_FOTOSENSIVEL")
	@Enumerated(EnumType.STRING)
	public DominioIndFotoSensibilidade getIndFotosensivel() {
		return this.indFotosensivel;
	}
	public void setIndFotosensivel(DominioIndFotoSensibilidade indFotosensivel) {
		this.indFotosensivel = indFotosensivel;
	}
	
	@Column(name = "IND_TIPO_USO")
	@Enumerated(EnumType.STRING)
	public DominioIndTipoUso getIndTipoUso() {
		return this.indTipoUso;
	}
	public void setIndTipoUso(DominioIndTipoUso indTipoUso) {
		this.indTipoUso = indTipoUso;
	}
	
	@Column(name = "NUMERO", precision = 6, scale = 3)
	public BigDecimal getNumero() {
		return this.numero;
	}
	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}
	
	@Column(name = "IND_CAD_SAPIENS", length = 1)
	public String getIndCadSapiens() {
		return this.indCadSapiens;
	}
	public void setIndCadSapiens(String indCadSapiens) {
		this.indCadSapiens = indCadSapiens;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CAD_SAPIENS")
	public Date getDtCadSapiens() {
		return this.dtCadSapiens;
	}
	public void setDtCadSapiens(Date dtCadSapiens) {
		this.dtCadSapiens = dtCadSapiens;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_CAD_SAPIENS", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CAD_SAPIENS", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCadSapiens() {
		return this.servidorCadSapiens;
	}
	public void setServidorCadSapiens(RapServidores servidorCadSapiens) {
		this.servidorCadSapiens = servidorCadSapiens;
	}
	
	@Column(name = "COD_RETENCAO", precision = 4, scale = 0)
	public Short getCodRetencao() {
		return this.codRetencao;
	}
	public void setCodRetencao(Short codRetencao) {
		this.codRetencao = codRetencao;
	}
	
	@Column(name = "COD_TRANSACAO_SAPIENS", length = 5)
	public String getCodTransacaoSapiens() {
		return this.codTransacaoSapiens;
	}
	public void setCodTransacaoSapiens(String codTransacaoSapiens) {
		this.codTransacaoSapiens = codTransacaoSapiens;
	}
	
	@Column(name = "COD_SIASG", precision = 6, scale = 0)
	public Integer getCodSiasg() {
		return this.codSiasg;
	}
	public void setCodSiasg(Integer codSiasg) {
		this.codSiasg = codSiasg;
	}
	
	@Column(name = "COD_SIT_TRIB_SAPIENS", length = 3)
	public String getCodSitTribSapiens() {
		return this.codSitTribSapiens;
	}
	public void setCodSitTribSapiens(String codSitTribSapiens) {
		this.codSitTribSapiens = codSitTribSapiens;
	}
	
	@Column(name = "NCM_CODIGO", length = 10)
	public String getNcmCodigo() {
		return this.ncmCodigo;
	}
	public void setNcmCodigo(String ncmCodigo) {
		this.ncmCodigo = ncmCodigo;
	}
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "scoMaterial")
	public AfaMedicamento getAfaMedicamento() {
		return afaMedicamento;
	}
	public void setAfaMedicamento(AfaMedicamento afaMedicamento) {
		this.afaMedicamento = afaMedicamento;
	}
	
	@OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
	public Set<SceEstoqueGeral> getEstoquesGerais() {
		return estoquesGerais;
	}
	public void setEstoquesGerais(Set<SceEstoqueGeral> estoquesGerais) {
		this.estoquesGerais = estoquesGerais;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "sco_marca_modelo_materiais", joinColumns = { @JoinColumn(name = "mat_codigo") }, inverseJoinColumns = {
			@JoinColumn(name = "mom_mcm_codigo", referencedColumnName = "mcm_codigo"),
			@JoinColumn(name = "mom_seqp", referencedColumnName = "seqp") })
	public Set<ScoMarcaModelo> getMarcasModelos() {
		return marcasModelos;
	}
	
	public void setMarcasModelos(Set<ScoMarcaModelo> marcasModelos) {
		this.marcasModelos = marcasModelos;
	}
	
	@Transient
	public Boolean getIndGenericoBoolean() {
		return this.indGenerico != null
				&& this.indGenerico.equals(DominioSimNao.S);
	}
	
	@Transient
	public void setIndGenericoBoolean(Boolean indGenericoBooleano) {
		if (indGenericoBooleano == null) {
			setIndGenerico(null);
		} else if (indGenericoBooleano) {
			setIndGenerico(DominioSimNao.S);
		} else {
			setIndGenerico(DominioSimNao.N);
		}
	}
	
	@Transient
	public Boolean getIndEstocavelBoolean() {
		return Boolean.TRUE.equals(this.estocavel);
	}
	
	@Transient
	public Boolean getIndAtivo() {
		return this.indSituacao.equals(DominioSituacao.A);
	}
	
	@Transient
	public Boolean getIndAtivoSituacao() {
		if (codigo != null) {
			return this.indSituacao.equals(DominioSituacao.A);
		}
		return true;
	}
	
	@Transient
	public void setIndAtivoSituacao(Boolean ativo) {
		if (ativo) {
			setIndSituacao(DominioSituacao.A);
		} else {
			setIndSituacao(DominioSituacao.I);
		}
	}
	
	@Transient
	public Boolean getIndAtivoEstocavel() {
		if (codigo != null) {
			return this.estocavel;
		}
		return false;
	}
	
	@Transient
	public Boolean getIndAtivoPadronizado() {
		if (codigo != null) {
			return this.indPadronizado.equals(DominioSimNao.S);
		}
		return false;
	}
	
	@Transient
	public void setIndAtivoPadronizado(Boolean padronizado) {
		if (padronizado) {
			this.indPadronizado = DominioSimNao.S;
		} else {
			this.indPadronizado = DominioSimNao.N;
		}
	}
	
	@Transient
	public Boolean getIndAtivoGenerico() {
		if (codigo != null) {
			return this.indGenerico.equals(DominioSimNao.S);
		}
		return false;
	}
	
	@Transient
	public void setIndAtivoGenerico(Boolean generico) {
		if (generico) {
			this.indGenerico = DominioSimNao.S;
		} else {
			this.indGenerico = DominioSimNao.N;
		}
	}
	
	@Transient
	public Boolean getIndAtivoMenorPreco() {
		if (codigo != null) {
			return this.indMenorPreco.equals(DominioSimNao.S);
		}
		return false;
	}
	
	@Transient
	public void setIndAtivoMenorPreco(Boolean menorPreco) {
		if (menorPreco) {
			this.indMenorPreco = DominioSimNao.S;
		} else {
			this.indMenorPreco = DominioSimNao.N;
		}
	}
	
	@Transient
	public Boolean getIndAtivoQuantidadeDisponivel() {
		if (codigo != null) {
			return this.indAtuQtdeDisponivel.equals(DominioSimNao.S);
		}
		return false;
	}
	
	@Transient
	public void setIndAtivoQuantidadeDisponivel(Boolean quantidadeDisponivel) {
		if (quantidadeDisponivel) {
			this.indAtuQtdeDisponivel = DominioSimNao.S;
		} else {
			this.indAtuQtdeDisponivel = DominioSimNao.N;
		}
	}
	
	@Transient
	public Boolean getIndAtivoFaturavel() {
		if (codigo != null) {
			return this.indMenorPreco.equals(DominioSimNao.S);
		}
		return false;
	}	
	@Transient
	public void setIndAtivoFaturavel(Boolean faturavel) {
		if (faturavel) {
			this.indFaturavel = DominioSimNao.S;
		} else {
			this.indFaturavel = DominioSimNao.N;
		}
	}	
	@Transient
	public Boolean getIndAtivoControleValidade() {
		if (codigo != null) {
			return this.indControleValidade.equals(DominioSimNao.S);
		}
		return true;
	}
	
	@Transient
	public void setIndAtivoControleValidade(Boolean controleValidade) {
		if (controleValidade) {
			this.indControleValidade = DominioSimNao.S;
		} else {
			this.indControleValidade = DominioSimNao.N;
		}
	}
	
	@Transient
	public String getCodigoNome() {
		final String numero = getCodigo() == null ? "" : getCodigo() + " - ";
		final String nome = getNome() == null ? "" : getNome();
		return numero + nome;
	}
	
	@Transient
	public String getCodNome() {
		final String codigo = getCodigo() == null ? "" : getCodigo() + " - ";
		final String nome = getNome() == null ? "" : getNome();
		return codigo + nome;
	}
	
	@OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
	public Set<ScoMaterialSicon> getMateriaisSicon() {
		return materiaisSicon;
	}
	
	public void setMateriaisSicon(Set<ScoMaterialSicon> materiaisSicon) {
		this.materiaisSicon = materiaisSicon;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ScoMaterial)) {
			return false;
		}
		ScoMaterial materialObj = (ScoMaterial) obj;
		return new EqualsBuilder().append(this.codigo, materialObj.getCodigo()).isEquals();
	}
	
	@Transient
	public String getUmdCodigo() {
		if (getUnidadeMedida() != null) {
			return getUnidadeMedida().getCodigo();
		}
		return null;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	public Set<SceEstoqueAlmoxarifado> getEstoquesAlmoxarifado() {
		return estoquesAlmoxarifado;
	}
	public void setEstoquesAlmoxarifado(Set<SceEstoqueAlmoxarifado> estoquesAlmoxarifado) {
		this.estoquesAlmoxarifado = estoquesAlmoxarifado;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	public Set<FatProcedHospInternos> getProcedimentosHospitalaresInternos() {
		return procedimentosHospitalaresInternos;
	}
	public void setProcedimentosHospitalaresInternos(Set<FatProcedHospInternos> procedimentosHospitalaresInternos) {
		this.procedimentosHospitalaresInternos = procedimentosHospitalaresInternos;
	}
	
	@OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
	public Set<ScoMateriaisClassificacoes> getMateriaisClassificacoes() {
		return materiaisClassificacoes;
	}
	public void setMateriaisClassificacoes(Set<ScoMateriaisClassificacoes> scoMateriaisClassificacoes) {
		this.materiaisClassificacoes = scoMateriaisClassificacoes;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	public Set<SceMovimentoMaterial> getMovimentosMaterial() {
		return movimentosMaterial;
	}
	public void setMovimentosMaterial(Set<SceMovimentoMaterial> movimentosMaterial) {
		this.movimentosMaterial = movimentosMaterial;
	}
	
	@Column(name = "CODIGO_ESPERANTO", precision = 6, scale = 0)
	public Integer getCodigoEsperanto() {
		return codigoEsperanto;
	}

	public void setCodigoEsperanto(Integer codigoEsperanto) {
		this.codigoEsperanto = codigoEsperanto;
	}
	
	@Column(name = "cod_catmat", precision = 7, scale = 0)
	public Integer getCodCatmat() {
		return codCatmat;
	}
	public void setCodCatmat(Integer codCatmat) {
		this.codCatmat = codCatmat;
	}
	
	@Column(name = "cod_mat_antigo", precision = 10, scale = 0)
	public Long getCodMatAntigo() {
		return codMatAntigo;
	}
	public void setCodMatAntigo(Long codMatAntigo) {
		this.codMatAntigo = codMatAntigo;
	}
	
	public void obtemDataDigitacao() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		this.dtDigitacao = cal.getTime();
	}

    @Column(name = "JUSTIFICATIVA_CATMAT", length = 500)
    public String getJustificativaCatmat() {
        return justificativaCatmat;
    }
    public void setJustificativaCatmat(String justificativaCatmat) {
        this.justificativaCatmat = justificativaCatmat;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_JUS_CATMAT", nullable = true)
	public Date getDataJusCatmat() {
		return dataJusCatmat;
	}
    
	public void setDataJusCatmat(Date dataJusCatmat) {
		this.dataJusCatmat = dataJusCatmat;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_JUS_CATMAT", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_JUS_CATMAT", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorJustificativa() {
		return servidorJustificativa;
	}
	public void setServidorJustificativa(RapServidores servidorJustificativa) {
		this.servidorJustificativa = servidorJustificativa;
	}

	@Transient
	public Integer getGmtCodigo() {
		return (getGrupoMaterial() != null)  ? getGrupoMaterial().getCodigo() : null;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	public Set<SceFornecedorMaterial> getFornecedoresMateriais() {
		return fornecedoresMateriais;
	}
	public void setFornecedoresMateriais(Set<SceFornecedorMaterial> fornecedoresMateriais) {
		this.fornecedoresMateriais = fornecedoresMateriais;
	}
	
	@Transient
	public String getCodigoEDescricao() {
		return this.codigo + " - " + (this.descricao != null ? this.descricao : "");
	}
	
	@Transient
	public String getCodigoENome() {
		return this.codigo + " - " + (this.nome != null ? this.nome : "");
	}
	
	@Transient
	public String getNomeEUnidadeMedida() {
		return this.nome + " - " + this.getUnidadeMedida().getDescricao();
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	public Set<SceConversaoUnidadeConsumos> getConversaoUnidadeConsumo() {
		return conversaoUnidadeConsumo;
	}
	
	public void setConversaoUnidadeConsumo(
			Set<SceConversaoUnidadeConsumos> conversaoUnidadeConsumo) {
		this.conversaoUnidadeConsumo = conversaoUnidadeConsumo;
	}
	
	public void setIndTipoResiduo(DominioTipoResiduo indTipoResiduo) {
		this.indTipoResiduo = indTipoResiduo;
	}
	
	@Column(name = "IND_TIPO_RESIDUO")
	@Enumerated(EnumType.STRING)
	public DominioTipoResiduo getIndTipoResiduo() {
		return indTipoResiduo;
	}
	public void setIndTermolabil(Boolean indTermolabil) {
		this.indTermolabil = indTermolabil;
	}
	
	@Column(name = "IND_TERMOLABIL", nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTermolabil() {
		return indTermolabil;
	}
	public void setIndVinculado(Boolean indVinculado) {
		this.indVinculado = indVinculado;
	}
	
	@Column(name = "IND_VINCULADO", nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndVinculado() {
		return indVinculado;
	}
	public void setIndSustentavel(Boolean indSustentavel) {
		this.indSustentavel = indSustentavel;
	}
	
	@Column(name = "IND_SUSTENTAVEL", nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSustentavel() {
		return indSustentavel;
	}
	public void setTemperatura(BigDecimal temperatura) {
		this.temperatura = temperatura;
	}
	
	@Column(name = "TEMPERATURA", precision = 3, scale = 1)
	@Digits(fraction = 1, integer = 2, message="Temperatura deve ter no máximo 2 números inteiros e 1 decimal")
	public BigDecimal getTemperatura() {
		return temperatura;
	}
	public void setLegislacao(String legislacao) {
		this.legislacao = legislacao;
	}
	
	@Column(name = "LEGISLACAO", length = 100)
	public String getLegislacao() {
		return legislacao;
	}
	public void setIndConfaz(DominioSimNao indConfaz) {
		this.indConfaz = indConfaz;
	}
	
	@Column(name = "IND_CONFAZ", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndConfaz() {
		return indConfaz;
	}
	public void setIndCapCmed(DominioSimNao indCapCmed) {
		this.indCapCmed = indCapCmed;
	}
	
	@Column(name = "IND_CAP_CMED", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndCapCmed() {
		return indCapCmed;
	}
	
	@Column(name = "ser_matricula_jus_proc_rel", precision = 7, scale = 0)
	public Integer getSerMatriculaJusProcRel() {
		return serMatriculaJusProcRel;
	}
	public void setSerMatriculaJusProcRel(Integer serMatriculaJusProcRel) {
		this.serMatriculaJusProcRel = serMatriculaJusProcRel;
	}
	
	@Column(name = "justificativa_proc_rel", length = 500)
	public String getJustificativaProcRel() {
		return justificativaProcRel;
	}
	public void setJustificativaProcRel(String justificativaProcRel) {
		this.justificativaProcRel = justificativaProcRel;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_jus_proc_rel")
	public Date getDataJusProcRel() {
		return dataJusProcRel;
	}
	public void setDataJusProcRel(Date dataJusProcRel) {
		this.dataJusProcRel = dataJusProcRel;
	}
	
	@Column(name = "ser_vin_codigo_jus_proc_rel", precision = 4, scale = 0)
	public Short getSerVinCodigoJusProcRel() {
		return serVinCodigoJusProcRel;
	}
	public void setSerVinCodigoJusProcRel(Short serVinCodigoJusProcRel) {
		this.serVinCodigoJusProcRel = serVinCodigoJusProcRel;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="scoMaterial")
	public Set<SceItemEntrSaidSemLicitacao> getSceItemEntrSaidSemLicitacao() {
		return sceItemEntrSaidSemLicitacao;
	}
	
	public void setSceItemEntrSaidSemLicitacao(Set<SceItemEntrSaidSemLicitacao> sceItemEntrSaidSemLicitacao) {
		this.sceItemEntrSaidSemLicitacao = sceItemEntrSaidSemLicitacao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	public Set<MpmProcedEspecialRm> getMpmProcedEspecialRm() {
		return mpmProcedEspecialRm;
	}

	public void setMpmProcedEspecialRm(Set<MpmProcedEspecialRm> mpmProcedEspecialRm) {
		this.mpmProcedEspecialRm = mpmProcedEspecialRm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ALTERACAO_CONFAZ")
    public Date getDataAlteracaoConfaz() {
        return dataAlteracaoConfaz;
    }

    public void setDataAlteracaoConfaz(Date dataAlteracaoConfaz) {
        this.dataAlteracaoConfaz = dataAlteracaoConfaz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ALTERACAO_CAP")
    public Date getDataAlteracaoCap() {
        return dataAlteracaoCap;
    }

    public void setDataAlteracaoCap(Date dataAlteracaoCap) {
        this.dataAlteracaoCap = dataAlteracaoCap;
    }
    
    @Column(name = "UMD_CODIGO", insertable=false, updatable=false)
	public String getUmdCod() {
		return umdCod;
	}

	public void setUmdCod(String umdCod) {
		this.umdCod = umdCod;
	}
	
	public enum Fields {
		CODIGO("codigo"), NOME("nome"), UNIDADE_MEDIDA("unidadeMedida"), UNIDADE_MEDIDA_CODIGO("unidadeMedida.codigo"), SITUACAO("indSituacao"), IND_ESTOCAVEL(
				"estocavel"), ESTOQUE_GERAL("estoquesGerais"), DESCRICAO("descricao"), GRUPO_MATERIAL("grupoMaterial"), GRUPO_MATERIAL_CODIGO(
				"grupoMaterial.codigo"), NUMERO("numero"), GENERICO("indGenerico"), MENOR_PRECO("indMenorPreco"), PRODUCAO_INTERNA("indProducaoInterna"), SERVIDOR(
				"servidor"), SERVIDOR_DESATIVADO("servidorDesativado"), SERVIDOR_ALTERADO("servidorAlteracao"), ATU_QTDE_DISPONIVEL("indAtuQtdeDisponivel"), CLASSIF_XYZ(
				"classifXyz"), SAZONALIDADE("sazonalidade"), OBSERVACAO("observacao"), CONTROLE_VALIDADE("indControleValidade"), LOCAL_ESTOQUE(
				"almSeqLocalEstq"), FATURAVEL("indFaturavel"), PADRONIZADO("indPadronizado"), FOTOSENSIVEL("indFotosensivel"), TIPO_USO("indTipoUso"), SOLICITACAO_COMPRA(
				"solicitacoesDeCompras"), ORIGEM_PARECER_TECNICO("origemParecerTecnico"), ALMOXARIFADO("almoxarifado"), ALMOXARIFADO_SEQ("almoxarifado.seq"), ESTOQUE_ALMOXARIFADO(
				"estoquesAlmoxarifado"), MARCA_MODELO("marcasModelos"), DATA_DIGITACAO("dtDigitacao"), DATA_ALTERACAO("dtAlteracao"), DATA_DESATIVACAO(
				"dtDesativacao"), ENVIO_PROD_INTERNA("indEnvioProdInterna"), ALM_SEQ_LOCAL_ESTQ("almLocalEstq"), IND_CCIH("indCcih"), CONTROLE_LOTE(
				"indControleLote"), IND_CAD_SAPIENS("indCadSapiens"), DATA_CAD_SAPIENS("dtCadSapiens"), SERVIDOR_CAD_SAPIENS("servidorCadSapiens"), COD_RETENCAO(
				"codRetencao"), COD_TRANSACAO_SAPIENS("codTransacaoSapiens"), COD_SIASG("codSiasg"), COD_SIT_TRIB_SAPIENS("codSitTribSapiens"), NCM_CODIGO(
				"ncmCodigo"), MEDICAMENTO("afaMedicamento"), FORNECEDOR_MATERIAIS("fornecedoresMateriais"), MATERIAL_SICON("materiaisSicon"), MATERIAIS_CLASSIFICACOES(
				"materiaisClassificacoes"), PROCED_HOSP_INTERNO("procedimentosHospitalaresInternos"), MOVIMENTOS_MATERIAL("movimentosMaterial"), IND_MENOR_PRECO(
				"indMenorPreco"), COD_CATMAT("codCatmat"), COD_MAT_ANTIGO("codMatAntigo"), NOME_FONETICO("nomeFonetico"), NOME_ORDENACAO("nomeOrdenacao"), CONVERSAO_UNIDADE_CONSUMO("conversaoUnidadeConsumo"),
				MATERIAL_VINCULO("materialVinculo"), GMT_CODIGO("grupoMaterial.codigo"),JUSTIFICATIVA_CATMAT("justificativaCatmat"),
				IND_RADIOATIVO("indRadioativo"), IND_TOXICO("indToxico"),
				IND_TERMOLABIL("indTermolabil"), IND_CORROSIVO("indCorrosivo"), IND_INFLAMAVEL("indInflamavel"),
				MPM_PROCED_ESPECIAL_RM("mpmProcedEspecialRm"),ITEM_ESL("sceItemEntrSaidSemLicitacao"),DT_ALTERACAO_CAP("dataAlteracaoCap"),DT_ALTERACAO_CONFAZ("dataAlteracaoConfaz"),
				IND_CAP_CMED("indCapCmed"),IND_CONFAZ("indConfaz"),
				UMD_CODIGO("umdCod");
		private String fields;
		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Transient
	public String getDescricaoSplited(final Integer tam) {
		return this.getDescricaoSplited(tam, "<br />");
	}
	
	@Transient
	public String getDescricaoSplited(final Integer tam, final String spliter) {
		if (this.descricao == null) {
			return null;
		}
		if (tam == null || tam <= 0 || this.descricao.length() <= tam) {
			return this.descricao;
		}
		final StringBuffer ret = new StringBuffer(this.descricao.length());
		int i = 0;
		while ((i + tam) < this.descricao.length()) {
			final String tmp = this.descricao.substring(i, (i + tam));
			int fim = tmp.lastIndexOf(' ');
			if (fim <= 0) {
				fim = tam;
			}
			if (i > 0) {
				ret.append(spliter);
			}
			ret.append(tmp.substring(0, fim));
			i += fim;
		}
		ret.append(spliter).append(this.descricao.substring(i));
		return ret.toString();
	}

	@Override
	public String toString() {
		return "ScoMaterial [getCodigo()=" + getCodigo() + "]";
	}

	
	
}