package br.gov.mec.aghu.model;

import java.math.BigDecimal;
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
import javax.validation.constraints.Digits;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioIndFotoSensibilidade;
import br.gov.mec.aghu.dominio.DominioIndProducaoInterna;
import br.gov.mec.aghu.dominio.DominioIndTipoUso;
import br.gov.mec.aghu.dominio.DominioSazonalidade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoResiduo;
import br.gov.mec.aghu.core.model.BaseJournal;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@SequenceGenerator(name = "SCO_MAT_JN_SEQ", sequenceName = "AGH.SCO_MAT_JN_SEQ", allocationSize = 1)
@Table(name = "SCO_MATERIAIS_JN", schema = "AGH")
public class ScoMaterialJN extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5721983015027831147L;
	private Integer codigo;
	private String umdCodigo;
	private RapServidores servidor;
	private RapServidores servidorDesativado;
	private String descricao;
	private Date dtDigitacao;
	private DominioSituacao indSituacao;
	private DominioSimNao indEstocavel;
	private DominioSimNao indGenerico;
	private DominioSimNao indMenorPreco;
	private DominioIndProducaoInterna indProducaoInterna;
	private DominioSimNao indAtuQtdeDisponivel;
	private Integer optCodigo;
	private DominioClassifyXYZ classifXyz;
	private DominioSazonalidade sazonalidade;
	private String nome;
	private String observacao;
	private Date dtAlteracao;  
	private Date dtDesativacao;
	private Byte gmtCodigo;
	private DominioSimNao indControleValidade;
	private Short almSeq;
	private DominioSimNao indFaturavel;
	private DominioSimNao indEnvioProdInterna;
	private Boolean indControleLote;
	private RapServidores servidorAlteracao;
	private Short almSeqLocalEstq;
	private DominioSimNao indPadronizado;
	private DominioSimNao indCcih;
	private DominioIndFotoSensibilidade indFotosensivel;
	private DominioIndTipoUso indTipoUso;
	private BigDecimal numero;
	private String indCadSapiens;
	private Date dtCadSapiens;
	private RapServidores servidorCadSapiens;	
	private Short codRetencao;
	private String codTransacaoSapiens;
	private Integer codSiasg;
	private String codSitTribSapiens;
	private String ncmCodigo;
	private Boolean indCorrosivo;
	private Boolean indInflamavel;
	private Boolean indRadioativo;
	private Boolean indReativo;
	private Boolean indToxico;
	private Boolean indUtilizaEspacoFisico;

	//private Integer seqJn;
	//private String jnUser;
	//private Date jnDateTime;
	//private DominioOperacoesJournal operacao;
	private Integer codNcm;
	private String nomeEstacao;
	//#26669
	private DominioTipoResiduo indTipoResiduo;
	private Boolean indTermolabil;
	private Boolean indVinculado;
	private Boolean indSustentavel;
	private BigDecimal temperatura;
	private String legislacao;
	private DominioSimNao indConfaz;	
	private DominioSimNao indCapCmed;	
	private Integer codCatmat;
	private Long codMatAntigo;
	
	
	public ScoMaterialJN() {
		super();
	}

	public void doSetPropriedades(ScoMaterial material) {
		this.codigo = material.getCodigo();
		this.umdCodigo = material.getUmdCodigo();
		this.servidor = material.getServidor();
		this.servidorDesativado = material.getServidorDesativado();
		this.descricao = material.getDescricao();
		this.dtDigitacao = material.getDtDigitacao();
		this.indSituacao = material.getIndSituacao();
		this.indEstocavel = material.getIndEstocavel();
		this.indGenerico = material.getIndGenerico();
		this.indMenorPreco = material.getIndMenorPreco();
		this.indProducaoInterna = material.getIndProducaoInterna();
		this.indAtuQtdeDisponivel = material.getIndAtuQtdeDisponivel();
		this.optCodigo = material.getOrigemParecerTecnico() == null ? null : material.getOrigemParecerTecnico().getCodigo();
		this.classifXyz = material.getClassifXyz();
		this.sazonalidade = material.getSazonalidade();
		this.nome = material.getNome();
		this.observacao = material.getObservacao();
		this.dtAlteracao = material.getDtAlteracao();
		this.dtDesativacao = material.getDtDesativacao();
		if(material.getGrupoMaterial() != null){
			
			this.gmtCodigo = material.getGrupoMaterial().getCodigo().byteValue();
		}
		this.indControleValidade = material.getIndControleValidade();
		this.almSeq = material.getAlmoxarifado() == null ? null : material.getAlmoxarifado().getSeq();
		this.indFaturavel = material.getIndFaturavel();
		this.indEnvioProdInterna = material.getIndEnvioProdInterna();
		this.indControleLote = material.getIndControleLote();
		this.servidorAlteracao = material.getServidorAlteracao();
		this.almSeqLocalEstq = material.getAlmLocalEstq() != null ? material.getAlmLocalEstq().getSeq() : null;
		this.indPadronizado = material.getIndPadronizado();
		this.indCcih = material.getIndCcih();
		this.indFotosensivel = material.getIndFotosensivel();
		this.indTipoUso = material.getIndTipoUso();
		this.numero = material.getNumero();
		this.indCadSapiens = material.getIndCadSapiens();
		this.dtCadSapiens = material.getDtCadSapiens();
		this.servidorCadSapiens = material.getServidorCadSapiens();
		this.codRetencao = material.getCodRetencao();
		this.codTransacaoSapiens = material.getCodTransacaoSapiens();
		this.codSiasg = material.getCodSiasg();
		this.codSitTribSapiens = material.getCodSitTribSapiens();
		this.ncmCodigo = material.getNcmCodigo();
		this.indTipoResiduo = material.getIndTipoResiduo();
		this.indTermolabil = material.getIndTermolabil();
		this.indVinculado = material.getIndVinculado();
		this.indSustentavel = material.getIndSustentavel();
		this.temperatura = material.getTemperatura();
		this.legislacao = material.getLegislacao();
		this.indConfaz = material.getIndConfaz();
		this.indCapCmed = material.getIndCapCmed();
		this.codCatmat = material.getCodCatmat();
		this.codMatAntigo = material.getCodMatAntigo();
		this.indCorrosivo = material.getIndCorrosivo();
		this.indInflamavel = material.getIndInflamavel();
		this.indRadioativo = material.getIndRadioativo();
		this.indReativo = material.getIndReativo();
		this.indToxico = material.getIndToxico();
		this.indUtilizaEspacoFisico = material.getIndUtilizaEspacoFisico();
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SCO_MAT_JN_SEQ")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	/*
	@Id
	@Column(name = "SEQ_JN", precision = 14, scale = 0, nullable = false)
	public Integer getSeqJn() {
		return seqJn;
	}

	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}
	@Column(name = "JN_USER")
	public String getJnUser() {
		return jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME")
	public Date getJnDateTime() {
		return jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}


	@Column(name = "JN_OPERATION", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}	
	*/
	
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 6, scale = 0)
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "UMD_CODIGO", nullable = true)
	public String getUmdCodigo() {
		return this.umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = true),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_DESATIVADO", referencedColumnName = "MATRICULA", nullable = true),
			@JoinColumn(name = "SER_VIN_CODIGO_DESATIVADO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidorDesativado() {
		return this.servidorDesativado;
	}

	public void setServidorDesativado(RapServidores servidorDesativado) {
		this.servidorDesativado = servidorDesativado;
	}

	@Column(name = "DESCRICAO")
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_DIGITACAO", nullable = true)
	public Date getDtDigitacao() {
		return this.dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	@Column(name = "IND_SITUACAO", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Column(name = "IND_ESTOCAVEL", nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndEstocavel() {
		return this.indEstocavel;
	}

	public void setIndEstocavel(DominioSimNao indEstocavel) {
		this.indEstocavel = indEstocavel;
	}

	@Column(name = "IND_GENERICO", nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndGenerico() {
		return this.indGenerico;
	}

	public void setIndGenerico(DominioSimNao indGenerico) {
		this.indGenerico = indGenerico;
	}

	@Column(name = "IND_MENOR_PRECO", nullable = true)
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

	@Column(name = "IND_ATU_QTDE_DISPONIVEL", nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndAtuQtdeDisponivel() {
		return this.indAtuQtdeDisponivel;
	}

	public void setIndAtuQtdeDisponivel(DominioSimNao indAtuQtdeDisponivel) {
		this.indAtuQtdeDisponivel = indAtuQtdeDisponivel;
	}

	@Column(name = "OPT_CODIGO", precision = 5, scale = 0)
	public Integer getOptCodigo() {
		return this.optCodigo;
	}

	public void setOptCodigo(Integer optCodigo) {
		this.optCodigo = optCodigo;
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

	@Column(name = "NOME", nullable = true)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "OBSERVACAO")
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
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

	@Column(name = "GMT_CODIGO", nullable = true, precision = 2, scale = 0)
	public Byte getGmtCodigo() {
		return this.gmtCodigo;
	}

	public void setGmtCodigo(Byte gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	@Column(name = "IND_CONTROLE_VALIDADE", nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndControleValidade() {
		return this.indControleValidade;
	}

	public void setIndControleValidade(DominioSimNao indControleValidade) {
		this.indControleValidade = indControleValidade;
	}

	@Column(name = "ALM_SEQ", nullable = true, precision = 3, scale = 0)
	public Short getAlmSeq() {
		return this.almSeq;
	}

	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}

	@Column(name = "IND_FATURAVEL", nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndFaturavel() {
		return this.indFaturavel;
	}

	public void setIndFaturavel(DominioSimNao indFaturavel) {
		this.indFaturavel = indFaturavel;
	}

	@Column(name = "IND_ENVIO_PROD_INTERNA", nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndEnvioProdInterna() {
		return this.indEnvioProdInterna;
	}

	public void setIndEnvioProdInterna(DominioSimNao indEnvioProdInterna) {
		this.indEnvioProdInterna = indEnvioProdInterna;
	}

	@Column(name = "ALM_SEQ_LOCAL_ESTQ", precision = 3, scale = 0)
	public Short getAlmSeqLocalEstq() {
		return this.almSeqLocalEstq;
	}

	public void setAlmSeqLocalEstq(Short almSeqLocalEstq) {
		this.almSeqLocalEstq = almSeqLocalEstq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
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

	@Column(name = "IND_CCIH", nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndCcih() {
		return this.indCcih;
	}

	public void setIndCcih(DominioSimNao indCcih) {
		this.indCcih = indCcih;
	}

	@Column(name = "IND_CONTROLE_LOTE", length = 1)
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
	@JoinColumns( {
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

	@Column(name = "COD_TRANSACAO_SAPIENS")
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

	@Column(name = "COD_SIT_TRIB_SAPIENS")
	public String getCodSitTribSapiens() {
		return this.codSitTribSapiens;
	}

	public void setCodSitTribSapiens(String codSitTribSapiens) {
		this.codSitTribSapiens = codSitTribSapiens;
	}

	@Column(name = "NCM_CODIGO")
	public String getNcmCodigo() {
		return this.ncmCodigo;
	}

	public void setNcmCodigo(String ncmCodigo) {
		this.ncmCodigo = ncmCodigo;
	}
	

	@Column(name = "COD_NCM", precision = 8, scale = 0)
	public Integer getCodNcm() {
		return codNcm;
	}

	public void setCodNcm(Integer codNcm) {
		this.codNcm = codNcm;
	}

	@Column(name = "NOME_ESTACAO")
	public String getNomeEstacao() {
		return nomeEstacao;
	}

	public void setNomeEstacao(String nomeEstacao) {
		this.nomeEstacao = nomeEstacao;
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
	
	@Column(name = "IND_TERMOLABIL")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTermolabil() {
		return indTermolabil;
	}

	public void setIndVinculado(Boolean indVinculado) {
		this.indVinculado = indVinculado;
	}
	
	@Column(name = "IND_VINCULADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndVinculado() {
		return indVinculado;
	}

	public void setIndSustentavel(Boolean indSustentavel) {
		this.indSustentavel = indSustentavel;
	}
	
	@Column(name = "IND_SUSTENTAVEL")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSustentavel() {
		return indSustentavel;
	}

	public void setTemperatura(BigDecimal temperatura) {
		this.temperatura = temperatura;
	}
	
	
	@Column(name = "TEMPERATURA", precision = 3, scale = 1)
	@Digits(integer=2, fraction=1, message="Temperatura deve ter no máximo 2 números inteiros e 1 decimal")
	public BigDecimal getTemperatura() {
		return temperatura;
	}
	

	public void setLegislacao(String legislacao) {
		this.legislacao = legislacao;
	}
	
	@Column(name="LEGISLACAO", length = 100)
	public String getLegislacao() {
		return legislacao;
	}
	
	@Column(name = "IND_CONFAZ")
	@Enumerated(EnumType.STRING)	
	public DominioSimNao getIndConfaz() {
		return indConfaz;
	}
	
	
	
	public void setIndCapCmed(DominioSimNao indCapCmed) {
		this.indCapCmed = indCapCmed;
	}
	
	@Column(name = "IND_CAP_CMED")
	@Enumerated(EnumType.STRING)	
	public DominioSimNao getIndCapCmed() {
		return indCapCmed;
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
	
	

	public void setIndConfaz(DominioSimNao indConfaz) {
		this.indConfaz = indConfaz;
	}



	public enum Fields {

		CODIGO("codigo"),
		UMD_CODIGO("umdCodigo"),
		SERVIDOR("servidor"),
		SERVIDOR_DESATIVADO("servidorDesativado"),
		DESCRICAO("descricao"),
		DT_DIGITACAO("dtDigitacao"),
		IND_SITUACAO("indSituacao"),
		IND_ESTOCAVEL("indEstocavel"),
		IND_GENERICO("indGenerico"),
		IND_MENOR_PRECO("indMenorPreco"),
		IND_PRODUCAO_INTERNA("indProducaoInterna"),
		IND_ATU_QTDE_DISPONIVEL("indAtuQtdeDisponivel"),
		OPT_CODIGO("optCodigo"),
		CLASSIF_XYZ("classifXyz"),
		SAZONALIDADE("sazonalidade"),
		NOME("nome"),
		OBSERVACAO("observacao"),
		DT_ALTERACAO("dtAlteracao"),
		DT_DESATIVACAO("dtDesativacao"),
		GMT_CODIGO("gmtCodigo"),
		IND_CONTROLE_VALIDADE("indControleValidade"),
		ALM_SEQ("almSeq"),
		IND_FATURAVEL("indFaturavel"),
		IND_ENVIO_PROD_INTERNA("indEnvioProdInterna"),
		IND_CONTROLE_LOTE("indControleLote"),
		SERVIDOR_ALTERACAO("servidorAlteracao"),
		ALM_SEQ_LOCAL_ESTQ("almSeqLocalEstq"),
		IND_PADRONIZADO("indPadronizado"),
		IND_CCIH("indCcih"),
		IND_FOTOSENSIVEL("indFotosensivel"),
		IND_TIPO_USO("indTipoUso"),
		NUMERO("numero"),
		IND_CAD_SAPIENS("indCadSapiens"),
		DT_CAD_SAPIENS("dtCadSapiens"),
		SERVIDOR_CAD_SAPIENS("servidorCadSapiens"),
		COD_RETENCAO("codRetencao"),
		COD_TRANSACAO_SAPIENS("codTransacaoSapiens"),
		COD_SIASG("codSiasg"),
		COD_SIT_TRIB_SAPIENS("codSitTribSapiens"),
		NCM_CODIGO("ncmCodigo"),
		OPERACAO("operacao"),
		COD_NCM("codNcm"),
		NOME_ESTACAO("nomeEstacao"),
		SEQ_JN("seqJn"),
		IND_TERMOLABIL("indTermolabil"),
		TEMPERATURA("temperatura"),		
		IND_VINCULADO("indVinculado"),
		IND_SUSTENTAVEL("indSustentavel"),	
		LEGISLACAO("legislacao"),
		IND_CONFAZ("indConfaz"),	
		IND_CAP_CMED("indCapCmed"),
		IND_TIPO_RESIDUO("indTipoResiduo"),
		COD_CATMAT("codCatmat"),
		COD_MAT_ANTIGO("codMatAntigo");

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
