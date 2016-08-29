package br.gov.mec.aghu.suprimentos.vo;

import java.math.BigDecimal;
import java.util.Set;

import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class ScoItemPropostaVO {

	private Integer numeroPac;
	private Short numeroItemProposta;
	private Short numeroItemPac;
	private String descricaoItem;
	private Long qtdItemProposta;
	private ScoFornecedor fornecedorProposta;
	private ScoUnidadeMedida unidadeProposta;
	private Integer fatorConversao;
	private Boolean indNacional;
	private FcpMoeda moedaItemProposta;
	private String apresentacao;
	private BigDecimal valorUnitarioItemProposta;
	private String numeroOrcamento;
	private String observacao; 
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo modeloComercial;
	private String descricaoModelo;
	private ScoNomeComercial nomeComercial;
	private Boolean indEscolhido;
	private String criterioEscolha;
	private Boolean indDesclassificado;
	private DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao;
	private Boolean indAnalisadoParecerTecnico;
	private String parecerTecnicoMarca;
	private String codigoMaterialFornecedor;
	private Set<ScoItemAutorizacaoForn> itemAutorizacaoFornececimento;
	private DominioOperacaoBanco tipoOperacao;
	private Boolean marcadoJulgamento;
	private Boolean indAutorizUsr;
	private Boolean desabilitaCheckboxJulgamentoLote;
	private String justifAutorizUsr;
	private String nomeMaterialServico;
	private String descricaoMaterialServico;
	private String descricaoSolicitacao;
	private DominioTipoSolicitacao tipoSolicitacao;
	
	public ScoItemPropostaVO() {
		
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroItemProposta == null) ? 0 : numeroItemProposta.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoItemPropostaVO other = (ScoItemPropostaVO) obj;
		if (numeroItemProposta == null) {
			if (other.numeroItemProposta != null){
				return false;
			}
		} else if (!numeroItemProposta.equals(other.numeroItemProposta)){
			return false;
		}
		return true;
	}


	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Short getNumeroItemPac() {
		return numeroItemPac;
	}

	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
	}
	
	public Long getQtdItemProposta() {
		return qtdItemProposta;
	}

	public void setQtdItemProposta(Long qtdItemProposta) {
		this.qtdItemProposta = qtdItemProposta;
	}

	public ScoUnidadeMedida getUnidadeProposta() {
		return unidadeProposta;
	}

	public void setUnidadeProposta(ScoUnidadeMedida unidadeProposta) {
		this.unidadeProposta = unidadeProposta;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Boolean getIndNacional() {
		return indNacional;
	}

	public void setIndNacional(Boolean indNacional) {
		this.indNacional = indNacional;
	}

	public FcpMoeda getMoedaItemProposta() {
		return moedaItemProposta;
	}

	public void setMoedaItemProposta(FcpMoeda moedaItemProposta) {
		this.moedaItemProposta = moedaItemProposta;
	}

	public String getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	public BigDecimal getValorUnitarioItemProposta() {
		return valorUnitarioItemProposta;
	}

	public void setValorUnitarioItemProposta(BigDecimal valorUnitarioItemProposta) {
		this.valorUnitarioItemProposta = valorUnitarioItemProposta;
	}

	public String getNumeroOrcamento() {
		return numeroOrcamento;
	}

	public void setNumeroOrcamento(String numeroOrcamento) {
		this.numeroOrcamento = numeroOrcamento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public Boolean getIndEscolhido() {
		return indEscolhido;
	}

	public void setIndEscolhido(Boolean indEscolhido) {
		this.indEscolhido = indEscolhido;
	}

	public Boolean getIndDesclassificado() {
		return indDesclassificado;
	}

	public void setIndDesclassificado(Boolean indDesclassificado) {
		this.indDesclassificado = indDesclassificado;
	}

	public Boolean getIndAnalisadoParecerTecnico() {
		return indAnalisadoParecerTecnico;
	}

	public void setIndAnalisadoParecerTecnico(Boolean indAnalisadoParecerTecnico) {
		this.indAnalisadoParecerTecnico = indAnalisadoParecerTecnico;
	}

	public DominioOperacaoBanco getTipoOperacao() {
		return tipoOperacao;
	}

	public void setTipoOperacao(DominioOperacaoBanco tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}

	public String getCriterioEscolha() {
		return criterioEscolha;
	}

	public void setCriterioEscolha(String criterioEscolha) {
		this.criterioEscolha = criterioEscolha;
	}

	public DominioMotivoDesclassificacaoItemProposta getMotivoDesclassificacao() {
		return motivoDesclassificacao;
	}

	public void setMotivoDesclassificacao(DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao) {
		this.motivoDesclassificacao = motivoDesclassificacao;
	}

	public String getParecerTecnicoMarca() {
		return parecerTecnicoMarca;
	}

	public void setParecerTecnicoMarca(String parecerTecnicoMarca) {
		this.parecerTecnicoMarca = parecerTecnicoMarca;
	}

	public String getDescricaoItem() {
		return descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	public ScoNomeComercial getNomeComercial() {
		return nomeComercial;
	}

	public void setNomeComercial(ScoNomeComercial nomeComercial) {
		this.nomeComercial = nomeComercial;
	}

	public ScoFornecedor getFornecedorProposta() {
		return fornecedorProposta;
	}

	public void setFornecedorProposta(ScoFornecedor fornecedorProposta) {
		this.fornecedorProposta = fornecedorProposta;
	}

	public String getDescricaoModelo() {
		return descricaoModelo;
	}

	public void setDescricaoModelo(String descricaoModelo) {
		this.descricaoModelo = descricaoModelo;
	}

	public String getCodigoMaterialFornecedor() {
		return codigoMaterialFornecedor;
	}

	public void setCodigoMaterialFornecedor(String codigoMaterialFornecedor) {
		this.codigoMaterialFornecedor = codigoMaterialFornecedor;
	}

	public Short getNumeroItemProposta() {
		return numeroItemProposta;
	}

	public void setNumeroItemProposta(Short numeroItemProposta) {
		this.numeroItemProposta = numeroItemProposta;
	}

	public Set<ScoItemAutorizacaoForn> getItemAutorizacaoFornececimento() {
		return itemAutorizacaoFornececimento;
	}

	public void setItemAutorizacaoFornececimento(
			Set<ScoItemAutorizacaoForn> itemAutorizacaoFornececimento) {
		this.itemAutorizacaoFornececimento = itemAutorizacaoFornececimento;
	}

	public ScoMarcaModelo getModeloComercial() {
		return modeloComercial;
	}

	public void setModeloComercial(ScoMarcaModelo modeloComercial) {
		this.modeloComercial = modeloComercial;
	}

	public Boolean getMarcadoJulgamento() {
		return marcadoJulgamento;
	}

	public void setMarcadoJulgamento(Boolean marcadoJulgamento) {
		this.marcadoJulgamento = marcadoJulgamento;
	}

	public Boolean getIndAutorizUsr() {
		return indAutorizUsr;
	}

	public void setIndAutorizUsr(Boolean indAutorizUsr) {
		this.indAutorizUsr = indAutorizUsr;
	}

	public String getJustifAutorizUsr() {
		return justifAutorizUsr;
	}

	public void setJustifAutorizUsr(String justifAutorizUsr) {
		this.justifAutorizUsr = justifAutorizUsr;
	}

	public String getNomeMaterialServico() {
		return nomeMaterialServico;
	}

	public void setNomeMaterialServico(String nomeMaterialServico) {
		this.nomeMaterialServico = nomeMaterialServico;
	}

	public String getDescricaoMaterialServico() {
		return descricaoMaterialServico;
	}

	public void setDescricaoMaterialServico(String descricaoMaterialServico) {
		this.descricaoMaterialServico = descricaoMaterialServico;
	}

	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}

	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}

	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public Boolean getDesabilitaCheckboxJulgamentoLote() {
		return desabilitaCheckboxJulgamentoLote;
	}

	public void setDesabilitaCheckboxJulgamentoLote(
			Boolean desabilitaCheckboxJulgamentoLote) {
		this.desabilitaCheckboxJulgamentoLote = desabilitaCheckboxJulgamentoLote;
	}
}
