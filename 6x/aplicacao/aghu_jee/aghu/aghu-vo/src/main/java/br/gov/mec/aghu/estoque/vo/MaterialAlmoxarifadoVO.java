package br.gov.mec.aghu.estoque.vo;

import java.util.Date;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceE660IncLotMed;
import br.gov.mec.aghu.model.SceEstqAlmoxMvto;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.commons.BaseBean;


public class MaterialAlmoxarifadoVO implements BaseBean {
	
	private static final long serialVersionUID = -2394484523090797751L;
	
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
	private SceE660IncLotMed sceE660IncLotMed; 
	private String numLote;
	private Date dtValidade;
	private Integer codigo;
	private String codigoUndMedida;
	private String descricaoUndMedida;
	private String descricaoAlmoxarifado;
	private String observacao;
	private Integer qtdDesbloqueadaEsl;
	private Integer qtdRecebidaEsl;
	private Integer qtdRecebidaNr;
	private Integer qtdDesbloqueadaNr;
	private Integer frnNumero;
	private Integer seqLdc;
	private Integer seqEsl;
	private Integer seqNr;

	
	//transient
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private String codigoUnidadeMedida;
	private String descricaoUnidadeMedida;
	private Short seqAlmoxarifado;
	
	private List<SceEstqAlmoxMvto> sceEstqAlmoxMvtos;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getDtAlteracao() {
		return dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	public Date getDtDesativacao() {
		return dtDesativacao;
	}

	public void setDtDesativacao(Date dtDesativacao) {
		this.dtDesativacao = dtDesativacao;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public Date getDtUltimaCompra() {
		return dtUltimaCompra;
	}

	public void setDtUltimaCompra(Date dtUltimaCompra) {
		this.dtUltimaCompra = dtUltimaCompra;
	}

	public Date getDtUltimaCompraFf() {
		return dtUltimaCompraFf;
	}

	public void setDtUltimaCompraFf(Date dtUltimaCompraFf) {
		this.dtUltimaCompraFf = dtUltimaCompraFf;
	}

	public Date getDtUltimoConsumo() {
		return dtUltimoConsumo;
	}

	public void setDtUltimoConsumo(Date dtUltimoConsumo) {
		this.dtUltimoConsumo = dtUltimoConsumo;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Boolean getIndConsignado() {
		return indConsignado;
	}

	public void setIndConsignado(Boolean indConsignado) {
		this.indConsignado = indConsignado;
	}

	public Boolean getIndControleValidade() {
		return indControleValidade;
	}

	public void setIndControleValidade(Boolean indControleValidade) {
		this.indControleValidade = indControleValidade;
	}

	public Boolean getIndEstocavel() {
		return indEstocavel;
	}

	public void setIndEstocavel(Boolean indEstocavel) {
		this.indEstocavel = indEstocavel;
	}

	public Boolean getIndEstqMinCalc() {
		return indEstqMinCalc;
	}

	public void setIndEstqMinCalc(Boolean indEstqMinCalc) {
		this.indEstqMinCalc = indEstqMinCalc;
	}

	public Boolean getIndPontoPedidoCalc() {
		return indPontoPedidoCalc;
	}

	public void setIndPontoPedidoCalc(Boolean indPontoPedidoCalc) {
		this.indPontoPedidoCalc = indPontoPedidoCalc;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getQtdeBloqConsumo() {
		return qtdeBloqConsumo;
	}

	public void setQtdeBloqConsumo(Integer qtdeBloqConsumo) {
		this.qtdeBloqConsumo = qtdeBloqConsumo;
	}

	public Integer getQtdeBloqEntrTransf() {
		return qtdeBloqEntrTransf;
	}

	public void setQtdeBloqEntrTransf(Integer qtdeBloqEntrTransf) {
		this.qtdeBloqEntrTransf = qtdeBloqEntrTransf;
	}

	public Integer getQtdeBloqueada() {
		return qtdeBloqueada;
	}

	public void setQtdeBloqueada(Integer qtdeBloqueada) {
		this.qtdeBloqueada = qtdeBloqueada;
	}

	public Integer getQtdeDisponivel() {
		return qtdeDisponivel;
	}

	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}

	public Integer getQtdeEmUso() {
		return qtdeEmUso;
	}

	public void setQtdeEmUso(Integer qtdeEmUso) {
		this.qtdeEmUso = qtdeEmUso;
	}

	public Integer getQtdeEstqMax() {
		return qtdeEstqMax;
	}

	public void setQtdeEstqMax(Integer qtdeEstqMax) {
		this.qtdeEstqMax = qtdeEstqMax;
	}

	public Integer getQtdeEstqMin() {
		return qtdeEstqMin;
	}

	public void setQtdeEstqMin(Integer qtdeEstqMin) {
		this.qtdeEstqMin = qtdeEstqMin;
	}

	public Integer getQtdePontoPedido() {
		return qtdePontoPedido;
	}

	public void setQtdePontoPedido(Integer qtdePontoPedido) {
		this.qtdePontoPedido = qtdePontoPedido;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public Integer getTempoReposicao() {
		return tempoReposicao;
	}

	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	public RapServidores getServidorDesativado() {
		return servidorDesativado;
	}

	public void setServidorDesativado(RapServidores servidorDesativado) {
		this.servidorDesativado = servidorDesativado;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public List<SceItemRms> getItemRms() {
		return itemRms;
	}

	public void setItemRms(List<SceItemRms> itemRms) {
		this.itemRms = itemRms;
	}

	public Set<SceValidade> getValidades() {
		return validades;
	}

	public void setValidades(Set<SceValidade> validades) {
		this.validades = validades;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQtdeBloqDispensacao() {
		return qtdeBloqDispensacao;
	}

	public void setQtdeBloqDispensacao(Integer qtdeBloqDispensacao) {
		this.qtdeBloqDispensacao = qtdeBloqDispensacao;
	}

	public Integer getQtdeEspacoArmazena() {
		return qtdeEspacoArmazena;
	}

	public void setQtdeEspacoArmazena(Integer qtdeEspacoArmazena) {
		this.qtdeEspacoArmazena = qtdeEspacoArmazena;
	}

	public Boolean getIndEstoqueTemporario() {
		return indEstoqueTemporario;
	}

	public void setIndEstoqueTemporario(Boolean indEstoqueTemporario) {
		this.indEstoqueTemporario = indEstoqueTemporario;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}

	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

	public String getDescricaoUnidadeMedida() {
		return descricaoUnidadeMedida;
	}

	public void setDescricaoUnidadeMedida(String descricaoUnidadeMedida) {
		this.descricaoUnidadeMedida = descricaoUnidadeMedida;
	}

	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}

	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}

	public List<SceEstqAlmoxMvto> getSceEstqAlmoxMvtos() {
		return sceEstqAlmoxMvtos;
	}

	public void setSceEstqAlmoxMvtos(List<SceEstqAlmoxMvto> sceEstqAlmoxMvtos) {
		this.sceEstqAlmoxMvtos = sceEstqAlmoxMvtos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public SceE660IncLotMed getSceE660IncLotMed() {
		return sceE660IncLotMed;
	}

	public void setSceE660IncLotMed(SceE660IncLotMed sceE660IncLotMed) {
		this.sceE660IncLotMed = sceE660IncLotMed;
	}
	
	public String getNumLote() {
		return numLote;
	}

	public void setNumLote(String numLote) {
		this.numLote = numLote;
	}

	public Date getDtValidade() {
		return dtValidade;
	}

	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}	
		
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	
	public String getCodigoUndMedida() {
		return codigoUndMedida;
	}

	public void setCodigoUndMedida(String codigoUndMedida) {
		this.codigoUndMedida = codigoUndMedida;
	}

	public String getDescricaoUndMedida() {
		return descricaoUndMedida;
	}

	public void setDescricaoUndMedida(String descricaoUndMedida) {
		this.descricaoUndMedida = descricaoUndMedida;
	}

	public String getDescricaoAlmoxarifado() {
		return descricaoAlmoxarifado;
	}

	public void setDescricaoAlmoxarifado(String descricaoAlmoxarifado) {
		this.descricaoAlmoxarifado = descricaoAlmoxarifado;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getQtdDesbloqueadaEsl() {
		return qtdDesbloqueadaEsl;
	}

	public void setQtdDesbloqueadaEsl(Integer qtdDesbloqueadaEsl) {
		this.qtdDesbloqueadaEsl = qtdDesbloqueadaEsl;
	}

	public Integer getQtdRecebidaEsl() {
		return qtdRecebidaEsl;
	}

	public void setQtdRecebidaEsl(Integer qtdRecebidaEsl) {
		this.qtdRecebidaEsl = qtdRecebidaEsl;
	}

	public Integer getQtdDesbloqueadaNr() {
		return qtdDesbloqueadaNr;
	}

	public void setQtdDesbloqueadaNr(Integer qtdDesbloqueadaNr) {
		this.qtdDesbloqueadaNr = qtdDesbloqueadaNr;
	}

	public Integer getQtdRecebidaNr() {
		return qtdRecebidaNr;
	}

	public void setQtdRecebidaNr(Integer qtdRecebidaNr) {
		this.qtdRecebidaNr = qtdRecebidaNr;
	}

	public Integer getFrnNumero() {
		return frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	public Integer getSeqLdc() {
		return seqLdc;
	}

	public void setSeqLdc(Integer seqLdc) {
		this.seqLdc = seqLdc;
	}
	
	public Integer getSeqEsl() {
		return seqEsl;
	}

	public void setSeqEsl(Integer seqEsl) {
		this.seqEsl = seqEsl;
	}

	public Integer getSeqNr() {
		return seqNr;
	}

	public void setSeqNr(Integer seqNr) {
		this.seqNr = seqNr;
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
			UMD_CODIGO("codigoUndMedida"),
			UMD_DESCRICAO("descricaoUndMedida"),
			SERVIDOR("servidor"),
			SERVIDOR_ALTERADO("servidorAlterado"),
			SERVIDOR_DESATIVADO("servidorDesativado"),
			ALMOXARIFADO("almoxarifado"),
			ALMOXARIFADO_SEQ("almoxarifado.seq"),
			MATERIAL("material"),
			MATERIAL_CODIGO("codigo"),
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
			SEQ_ALMOXARIFADO("seqAlmoxarifado"),
			DESCRICAO_ALMOXARIFADO("descricaoAlmoxarifado"),
			OBSERVACAO("observacao"),
			QTD_RECEBIDA_ESL("qtdRecebidaEsl"),
			QTD_RECEBIDA_NR("qtdRecebidaNr"),
			FRN_NUMERO("frnNumero"),
			SEQ_LDC("seqLdc"),
			SEQ_ESL("seqEsl"),
			SEQ_NR("seqNr"),
			ESTQ_ALMOX_MVTOS("sceEstqAlmoxMvtos")
			;
	
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
