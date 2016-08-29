package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PriorizaEntregaSolicitacaoMaterialServicoController  extends ActionController {

	private static final long serialVersionUID = -729441125582120797L;

	private static final String RECEBER_MATERIAL_SERVICO = "compras-receberMaterialServico";
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IPacFacade pacFacade;
	
	@Inject
	private RecebeMaterialServicoController recebeMaterialServicoController;

	// parametros
	private Integer numeroAf;	
	private Short numeroItemAf;
	private Integer seqProgEntrega;
	private Integer parcelaProgEntrega;
	private Integer seqRecebimento;
	private Integer itemRecebimento;
	private Integer qtdLimite;
	private Double valorLimite;
	
	
	// campos tela	
	private Integer numeroProposta;
	private Short numeroComplemento;
	private Short numeroItemLicitacao;
	private String descricaoSolicitacao;
	private List<PriorizaEntregaVO> listaPriorizacao;	
		
	
	// campos auxiliares
	private DominioTipoSolicitacao tipoSolicitacao;
	private Boolean mostraModalVoltar;
	private Boolean processoRecebimento;
	private Boolean ativo;
	private Boolean alteracoesPendentes;
	
	// URL para botão voltar
	private String voltarParaUrl;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	// botoes
			
	public void limpar() {
		this.numeroProposta = null;
		this.numeroComplemento = null;
		this.numeroItemLicitacao = null;
		this.descricaoSolicitacao = null;
		this.mostraModalVoltar = Boolean.FALSE;
		this.ativo = Boolean.FALSE;
		this.alteracoesPendentes = Boolean.FALSE;
		this.listaPriorizacao = new ArrayList<PriorizaEntregaVO>();	
	}
	
	public String gravar() {
		
		try {
			listaPriorizacao = autFornecimentoFacade.gravarPriorizacaoEntrega(processoRecebimento, tipoSolicitacao, listaPriorizacao, qtdLimite, valorLimite);
			
			if (!this.processoRecebimento) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INCLUSAO_ITEM_RECEB_PROG_ENTREGA_OK");
			}
			
			this.alteracoesPendentes = Boolean.FALSE;
			
			return voltar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	private String voltar() {
		
		if(RECEBER_MATERIAL_SERVICO.equals(voltarParaUrl)){
			recebeMaterialServicoController.setListaPriorizacao(listaPriorizacao);
		}
		
		return voltarParaUrl;
	}

	public void iniciar() {
	 

	 
	
		this.limpar();
		this.popularCamposCabecalho();
		this.verificarTelaChamadoraRecebimento();
		this.pesquisar();			
	
	}
	
	
	public void pesquisar() {
		this.ativo = Boolean.TRUE;
		this.listaPriorizacao = this.autFornecimentoFacade.pesquisarSolicitacaoProgEntregaItemAf(
				this.numeroAf, this.numeroItemAf, this.seqProgEntrega, this.parcelaProgEntrega, this.seqRecebimento, this.itemRecebimento, 
				this.qtdLimite, this.valorLimite, this.tipoSolicitacao);
	}
	
	public String verificarAtualizacaoPendentes() {
		
		if (this.listaPriorizacao.size() > 0 && this.alteracoesPendentes) {
			this.mostraModalVoltar = Boolean.TRUE;
		} else {
			this.mostraModalVoltar = Boolean.FALSE;
			return voltarParaUrl;
		}
		
		return null;
	}
	
	private void verificarTelaChamadoraRecebimento() {
		if (this.voltarParaUrl.equals("/compras/autfornecimento/receberMaterialServico.xhtml")) {				
			this.processoRecebimento = Boolean.TRUE;
		} else {
			this.processoRecebimento = Boolean.FALSE;
		}
	}
	
	private void popularCamposCabecalho() {
		ScoAutorizacaoForn af = this.autFornecimentoFacade.obterAfByNumero(this.numeroAf);
		
		if (af != null) {
			this.numeroComplemento = af.getNroComplemento();			
			this.numeroProposta = af.getPropostaFornecedor().getId().getLctNumero();			
		}
		
		ScoItemAutorizacaoForn itemAf = this.solicitacaoComprasFacade.obterDadosItensAutorizacaoFornecimento(this.numeroAf, Integer.valueOf(this.numeroItemAf));
		if (itemAf != null) {
			if (itemAf.getScoFaseSolicitacao() != null && !itemAf.getScoFaseSolicitacao().isEmpty()) {
				if (itemAf.getScoFaseSolicitacao().get(0).getTipo() == DominioTipoFaseSolicitacao.C) {
					this.tipoSolicitacao = DominioTipoSolicitacao.SC;									
				} else {
					this.tipoSolicitacao = DominioTipoSolicitacao.SS;					
				}
			}
		
			this.numeroItemLicitacao = itemAf.getItemPropostaFornecedor().getItemLicitacao().getId().getNumero();
			this.descricaoSolicitacao = this.pacFacade.obterNomeMaterialServico(itemAf.getItemPropostaFornecedor().getItemLicitacao(), true);					
		}				
	}
	
	// Getters/Setters
	
	public Boolean isSc() {
		return this.tipoSolicitacao == DominioTipoSolicitacao.SC;
	}

	public Integer getSeqProgEntrega() {
		return seqProgEntrega;
	}

	public void setSeqProgEntrega(Integer seqProgEntrega) {
		this.seqProgEntrega = seqProgEntrega;
	}

	public Integer getParcelaProgEntrega() {
		return parcelaProgEntrega;
	}

	public void setParcelaProgEntrega(Integer parcelaProgEntrega) {
		this.parcelaProgEntrega = parcelaProgEntrega;
	}

	public Integer getSeqRecebimento() {
		return seqRecebimento;
	}

	public void setSeqRecebimento(Integer seqRecebimento) {
		this.seqRecebimento = seqRecebimento;
	}

	public Integer getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(Integer itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public Integer getNumeroProposta() {
		return numeroProposta;
	}

	public void setNumeroProposta(Integer numeroProposta) {
		this.numeroProposta = numeroProposta;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}

	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}

	public List<PriorizaEntregaVO> getListaPriorizacao() {
		return listaPriorizacao;
	}

	public void setListaPriorizacao(List<PriorizaEntregaVO> listaPriorizacao) {
		this.listaPriorizacao = listaPriorizacao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}

	public void setNumeroItemLicitacao(Short numeroItemLicitacao) {
		this.numeroItemLicitacao = numeroItemLicitacao;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroItemAf() {
		return numeroItemAf;
	}

	public void setNumeroItemAf(Short numeroItemAf) {
		this.numeroItemAf = numeroItemAf;
	}

	public Boolean getMostraModalVoltar() {
		return mostraModalVoltar;
	}

	public void setMostraModalVoltar(Boolean mostraModalVoltar) {
		this.mostraModalVoltar = mostraModalVoltar;
	}

	public Integer getQtdLimite() {
		return qtdLimite;
	}

	public void setQtdLimite(Integer qtdLimite) {
		this.qtdLimite = qtdLimite;
	}

	public Double getValorLimite() {
		return valorLimite;
	}

	public void setValorLimite(Double valorLimite) {
		this.valorLimite = valorLimite;
	}

	public Boolean getProcessoRecebimento() {
		return processoRecebimento;
	}

	public void setProcessoRecebimento(Boolean processoRecebimento) {
		this.processoRecebimento = processoRecebimento;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getAlteracoesPendentes() {
		return alteracoesPendentes;
	}

	public void setAlteracoesPendentes(Boolean alteracoesPendentes) {
		this.alteracoesPendentes = alteracoesPendentes;
	}
}