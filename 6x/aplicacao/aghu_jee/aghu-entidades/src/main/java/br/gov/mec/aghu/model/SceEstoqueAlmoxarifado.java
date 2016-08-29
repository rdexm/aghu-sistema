package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Transient;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_estq_almoxs database table.
 * 
 */
@Entity
@Table(name = "SCE_ESTQ_ALMOXS", schema = "AGH")
@SequenceGenerator(name="SCE_ESTQ_ALMOXS_SEQ_GENERATOR", sequenceName="agh.SCE_EAL_SQ1", allocationSize = 1)
public class SceEstoqueAlmoxarifado extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7022160121611645802L;

	private Integer seq;
	
	private Date dtAlteracao;
	private Date dtDesativacao;
	private Date dtGeracao;
	private Date dtUltimaCompra;
	private Date dtUltimaCompraFf;
	private Date dtUltimoConsumo;
	
	private String endereco;
	
	private Boolean indConsignado;
	private Boolean indControleValidade;
	private Boolean indEstocavel;
	private Boolean indEstqMinCalc;
	private Boolean indPontoPedidoCalc;
	private DominioSituacao indSituacao;
	
	private Integer qtdeBloqConsumo;
	private Integer qtdeBloqEntrTransf;
	private Integer qtdeBloqueada;
	private Integer qtdeDisponivel;
	private Integer qtdeEmUso;
	private Integer qtdeEstqMax;
	private Integer qtdeEstqMin;
	private Integer qtdePontoPedido;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private Integer tempoReposicao;
	private Integer version;

	private ScoUnidadeMedida unidadeMedida;
	private RapServidores servidor;
	private RapServidores servidorAlterado;
	private RapServidores servidorDesativado;
	private SceAlmoxarifado almoxarifado;
	private ScoMaterial material;
	private ScoFornecedor fornecedor;
	private List<SceItemRms> itemRms;	
	private Set<SceValidade> validades;
	private Integer quantidade;
	private Integer qtdeBloqDispensacao;
	private Integer qtdeEspacoArmazena;
	private Boolean indEstoqueTemporario;
	
	//transient
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private String codigoUnidadeMedida;
	private String descricaoUnidadeMedida;
	private Short seqAlmoxarifado;

    public SceEstoqueAlmoxarifado() {
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList"})
	public SceEstoqueAlmoxarifado(Integer seq, Date dtAlteracao,
			Date dtDesativacao, Date dtGeracao, Date dtUltimaCompra,
			Date dtUltimaCompraFf, Date dtUltimoConsumo, String endereco,
			Boolean indConsignado, Boolean indControleValidade,
			Boolean indEstocavel, Boolean indEstqMinCalc,
			Boolean indPontoPedidoCalc, DominioSituacao indSituacao,
			Integer qtdeBloqConsumo, Integer qtdeBloqEntrTransf,
			Integer qtdeBloqueada, Integer qtdeDisponivel, Integer qtdeEmUso,
			Integer qtdeEstqMax, Integer qtdeEstqMin, Integer qtdePontoPedido,
			ScoSolicitacaoDeCompra solicitacaoCompra, Integer tempoReposicao,
			ScoUnidadeMedida unidadeMedida, Integer version,
			RapServidores servidor, RapServidores servidorAlterado,
			RapServidores servidorDesativado,
			SceAlmoxarifado almoxarifado, ScoMaterial material,
			ScoFornecedor fornecedor) {
		super();
		this.seq = seq;
		this.dtAlteracao = dtAlteracao;
		this.dtDesativacao = dtDesativacao;
		this.dtGeracao = dtGeracao;
		this.dtUltimaCompra = dtUltimaCompra;
		this.dtUltimaCompraFf = dtUltimaCompraFf;
		this.dtUltimoConsumo = dtUltimoConsumo;
		this.endereco = endereco;
		this.indConsignado = indConsignado;
		this.indControleValidade = indControleValidade;
		this.indEstocavel = indEstocavel;
		this.indEstqMinCalc = indEstqMinCalc;
		this.indPontoPedidoCalc = indPontoPedidoCalc;
		this.indSituacao = indSituacao;
		this.qtdeBloqConsumo = qtdeBloqConsumo;
		this.qtdeBloqEntrTransf = qtdeBloqEntrTransf;
		this.qtdeBloqueada = qtdeBloqueada;
		this.qtdeDisponivel = qtdeDisponivel;
		this.qtdeEmUso = qtdeEmUso;
		this.qtdeEstqMax = qtdeEstqMax;
		this.qtdeEstqMin = qtdeEstqMin;
		this.qtdePontoPedido = qtdePontoPedido;
		this.solicitacaoCompra = solicitacaoCompra;
		this.tempoReposicao = tempoReposicao;
		this.unidadeMedida = unidadeMedida;
		this.version = version;
		this.servidor = servidor;
		this.servidorAlterado = servidorAlterado;
		this.servidorDesativado = servidorDesativado;
		this.almoxarifado = almoxarifado;
		this.material = material;
		this.fornecedor = fornecedor;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="SCE_ESTQ_ALMOXS_SEQ_GENERATOR")
	@Column(name = "SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_GERACAO")
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULTIMA_COMPRA")
	public Date getDtUltimaCompra() {
		return this.dtUltimaCompra;
	}

	public void setDtUltimaCompra(Date dtUltimaCompra) {
		this.dtUltimaCompra = dtUltimaCompra;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULTIMA_COMPRA_FF")
	public Date getDtUltimaCompraFf() {
		return this.dtUltimaCompraFf;
	}

	public void setDtUltimaCompraFf(Date dtUltimaCompraFf) {
		this.dtUltimaCompraFf = dtUltimaCompraFf;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULTIMO_CONSUMO")
	public Date getDtUltimoConsumo() {
		return this.dtUltimoConsumo;
	}

	public void setDtUltimoConsumo(Date dtUltimoConsumo) {
		this.dtUltimoConsumo = dtUltimoConsumo;
	}

	@Column(name = "ENDERECO", length = 4)
	public String getEndereco() {
		return this.endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Column(name="IND_CONSIGNADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsignado() {
		return this.indConsignado;
	}

	public void setIndConsignado(Boolean indConsignado) {
		this.indConsignado = indConsignado;
	}


	@Column(name = "IND_CONTROLE_VALIDADE", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndControleValidade() {
		return this.indControleValidade;
	}

	public void setIndControleValidade(Boolean indControleValidade) {
		this.indControleValidade = indControleValidade;
	}

	@Column(name = "IND_ESTQ_TEMP", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstoqueTemporario() {
		return this.indEstoqueTemporario;
	}

	public void setIndEstoqueTemporario(Boolean indEstoqueTemporario) {
		this.indEstoqueTemporario = indEstoqueTemporario;
	}

	@Column(name = "IND_ESTOCAVEL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstocavel() {
		return this.indEstocavel;
	}

	public void setIndEstocavel(Boolean indEstocavel) {
		this.indEstocavel = indEstocavel;
	}


	@Column(name = "IND_ESTQ_MIN_CALC", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstqMinCalc() {
		return this.indEstqMinCalc;
	}

	public void setIndEstqMinCalc(Boolean indEstqMinCalc) {
		this.indEstqMinCalc = indEstqMinCalc;
	}


	@Column(name = "IND_PONTO_PEDIDO_CALC", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPontoPedidoCalc() {
		return this.indPontoPedidoCalc;
	}

	public void setIndPontoPedidoCalc(Boolean indPontoPedidoCalc) {
		this.indPontoPedidoCalc = indPontoPedidoCalc;
	}


	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}


	@Column(name = "QTDE_BLOQ_CONSUMO", precision = 7, scale = 0)
	public Integer getQtdeBloqConsumo() {
		return this.qtdeBloqConsumo;
	}

	public void setQtdeBloqConsumo(Integer qtdeBloqConsumo) {
		this.qtdeBloqConsumo = qtdeBloqConsumo;
	}

	@Column(name = "QTD_ESPACO_ARMAZENA", precision = 7, scale = 0)
	public Integer getQtdeEspacoArmazena() {
		return this.qtdeEspacoArmazena;
	}

	public void setQtdeEspacoArmazena(Integer qtdeEspacoArmazena) {
		this.qtdeEspacoArmazena = qtdeEspacoArmazena;
	}

	@Column(name = "QTDE_BLOQ_ENTR_TRANSF", precision = 7, scale = 0)
	public Integer getQtdeBloqEntrTransf() {
		return this.qtdeBloqEntrTransf;
	}

	public void setQtdeBloqEntrTransf(Integer qtdeBloqEntrTransf) {
		this.qtdeBloqEntrTransf = qtdeBloqEntrTransf;
	}


	@Column(name = "QTDE_BLOQUEADA", nullable = false, precision = 7, scale = 0)
	public Integer getQtdeBloqueada() {
		return this.qtdeBloqueada;
	}

	public void setQtdeBloqueada(Integer qtdeBloqueada) {
		this.qtdeBloqueada = qtdeBloqueada;
	}


	@Column(name = "QTDE_DISPONIVEL", nullable = false, precision = 10, scale = 0)
	public Integer getQtdeDisponivel() {
		return this.qtdeDisponivel;
	}

	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}


	@Column(name = "QTDE_EM_USO", precision = 7, scale = 0)
	public Integer getQtdeEmUso() {
		return this.qtdeEmUso;
	}

	public void setQtdeEmUso(Integer qtdeEmUso) {
		this.qtdeEmUso = qtdeEmUso;
	}


	@Column(name = "QTDE_ESTQ_MAX", precision = 7, scale = 0)
	public Integer getQtdeEstqMax() {
		return this.qtdeEstqMax;
	}

	public void setQtdeEstqMax(Integer qtdeEstqMax) {
		this.qtdeEstqMax = qtdeEstqMax;
	}


	@Column(name = "QTDE_ESTQ_MIN", precision = 7, scale = 0)
	public Integer getQtdeEstqMin() {
		return this.qtdeEstqMin;
	}

	public void setQtdeEstqMin(Integer qtdeEstqMin) {
		this.qtdeEstqMin = qtdeEstqMin;
	}


	@Column(name = "QTDE_PONTO_PEDIDO", precision = 7, scale = 0)
	public Integer getQtdePontoPedido() {
		return this.qtdePontoPedido;
	}

	public void setQtdePontoPedido(Integer qtdePontoPedido) {
		this.qtdePontoPedido = qtdePontoPedido;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_DESATIVADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_DESATIVADO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidorDesativado() {
		return servidorDesativado;
	}

	public void setServidorDesativado(RapServidores servidorDesativado) {
		this.servidorDesativado = servidorDesativado;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SLC_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	@Column(name = "TEMPO_REPOSICAO", precision = 3, scale = 0)
	public Integer getTempoReposicao() {
		return this.tempoReposicao;
	}

	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to SceAlmoxarifados
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ALM_SEQ", referencedColumnName = "SEQ",nullable=false)
	public SceAlmoxarifado getAlmoxarifado() {
		return this.almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO", nullable=false)
	public ScoMaterial getMaterial() {
		return material;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FRN_NUMERO")
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estoqueAlmoxarifado")
	public List<SceItemRms> getItemRms() {
		return itemRms;
	}
	
	public void setItemRms(List<SceItemRms> itemRms) {
		this.itemRms = itemRms;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estoqueAlmoxarifado")
	public Set<SceValidade> getValidades() {
		return validades;
	}

	public void setValidades(
			Set<SceValidade> validades) {
		this.validades = validades;
	}
	
	@Column(name = "QTDE_BLOQ_DISPENSACAO", precision = 7, scale = 0)
	public Integer getQtdeBloqDispensacao() {
		return qtdeBloqDispensacao;
	}

	public void setQtdeBloqDispensacao(Integer qtdeBloqDispensacao) {
		this.qtdeBloqDispensacao = qtdeBloqDispensacao;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		SceEstoqueAlmoxarifado other = (SceEstoqueAlmoxarifado) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	@Transient
	public Integer getQuantidade() {
		return quantidade;
	}


	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	
	@Transient
	public Integer getCodigoMaterial(){
		return this.codigoMaterial;
	}
	
	public void setCodigoMaterial(Integer codigoMaterial){
		this.codigoMaterial = codigoMaterial;
	}
	
	@Transient
	public String getNomeMaterial(){
		return this.nomeMaterial;
	}
	
	public void setNomeMaterial(String nomeMaterial){
		this.nomeMaterial = nomeMaterial;
	}
	
	
	@Transient
	public Integer getNumeroFornecedor(){
		return this.numeroFornecedor;
	}
	
	public void setNumeroFornecedor(Integer numeroFornecedor){
		this.numeroFornecedor = numeroFornecedor;
	}
	
	@Transient
	public String getRazaoSocialFornecedor(){
		return this.razaoSocialFornecedor;
	}
	
	public void setRazaoSocialFornecedor(String razaoSocialFornecedor){
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}
	
	@Transient
	public String getCodigoUnidadeMedida(){
		return this.codigoUnidadeMedida;
	}
	
	public void setCodigoUnidadeMedida(String codigoUnidadeMedida){
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}
	
	@Transient
	public String getDescricaoUnidadeMedida(){
		return this.descricaoUnidadeMedida;
	}
	
	public void setDescricaoUnidadeMedida(String descricaoUnidadeMedida){
		this.descricaoUnidadeMedida = descricaoUnidadeMedida;
	}
	
	@Transient
	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}

	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		CODIGO_MATERIAL("material.codigo"),
		QUANTIDADE_EST_MIN("qtdeEstqMin"),
		QUANTIDADE_BLOQ_ENTR_TRANSF("qtdeBloqEntrTransf"),
		DATA_ALTERACAO("dtAlteracao"),
		DATA_DESATIVACAO("dtDesativacao"),
		DATA_GERACAO("dtGeracao"),
		DATA_ULTIMA_COMPRA("dtUltimaCompra"),
		DATA_ULTIMA_COMPRA_FF("dtUltimaCompraFf"),
		DATA_ULTIMO_CONSUMO("dtUltimoConsumo"),
		ENDERECO("endereco"),
		IND_CONSIGNADO("indConsignado"),
		IND_CONTROLE_VALIDADE("indControleValidade"),
		IND_ESTOCAVEL("indEstocavel"),
		IND_ESTQ_MIN_CALC("indEstqMinCalc"),
		IND_PONTO_PEDIDO_CALC("indPontoPedidoCalc"),
		IND_SITUACAO("indSituacao"),
		QTDE_BLOQ_CONSUMO("qtdeBloqConsumo"),
		QTDE_BLOQ_ENTR_TRANSF("qtdeBloqEntrTransf"),
		QTDE_BLOQUEADA("qtdeBloqueada"),
		QTDE_DISPONIVEL("qtdeDisponivel"),
		QTDE_EM_USO("qtdeEmUso"),
		QTDE_ESTQ_MAX("qtdeEstqMax"),
		QTDE_ESTQ_MIN("qtdeEstqMin"),
		QTDE_PONTO_PEDIDO("qtdePontoPedido"),
		SOLICITACAO_COMPRA("solicitacaoCompra"),
		TEMPO_REPOSICAO("tempoReposicao"),
		UNIDADE_MEDIDA("unidadeMedida"),
		UMD_CODIGO("unidadeMedida.codigo"),
		UMD_DESCRICAO("unidadeMedida.descricao"),
		SERVIDOR("servidor"),
		SERVIDOR_ALTERADO("servidorAlterado"),
		SERVIDOR_DESATIVADO("servidorDesativado"),
		ALMOXARIFADO("almoxarifado"),
		ALMOXARIFADO_SEQ("almoxarifado.seq"),
		MATERIAL("material"),
		MATERIAL_CODIGO("material.codigo"),
		NOME_MATERIAL("material.nome"),
		GMT_CODIGO("material.grupoMaterial.codigo"),
		FORNECEDOR("fornecedor"),
		NUMERO_FORNECEDOR("fornecedor.numero"),
		NOME_FORNECEDOR("fornecedor.nomeFantasia"),
		FORNECEDOR_PACIENTE_SEQ("fornecedor.pacientes.seq"),
		ITEM_REQUISICAO("itemRms"),
		VALIDADES("validades"), 
		QUANTIDADE_DISPONIVEL("qtdeDisponivel"), 
		QUANTIDADE_BLOQUEADA("qtdeBloqueada"),
		NRO_SOLICITACAO_COMPRA("solicitacaoCompra.numero"),
		QUANTIDADE_PONTO_PEDIDO("qtdePontoPedido"),
		MATERIAL_NOME("nomeMaterial"),
		COD_MATERIAL("codigoMaterial"),
		IND_ESTOQUE_TEMPORARIO("indEstoqueTemporario"),
		CODIGO_UNIDADE_MEDIDA("codigoUnidadeMedida"),
		DESCRICAO_UNIDADE_MEDIDA("descricaoUnidadeMedida"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocialFornecedor"),
		FORNECEDOR_NUMERO("numeroFornecedor"),
		SEQ_ALMOXARIFADO("seqAlmoxarifado");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}