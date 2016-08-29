package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterEstoqueAlmoxarifadoController extends ActionController {

	private static final String PESQUISA_ESTOQUE_ALMOXARIFADO = "estoque-pesquisaEstoqueAlmoxarifado";
	private static final long serialVersionUID = 6937282509220491432L;
	private static final Log LOG = LogFactory.getLog(ManterEstoqueAlmoxarifadoController.class);
	private static final String SALDO_ESTOQUE = "estoque-saldoEstoque";
	private static final String PESQUISAR_VALIDADE_MATERIAL = "estoque-pesquisarValidadeMaterial";
	private static final String MANTER_MATERIAL_CRUD = "estoque-manterMaterialCRUD";

	private static final String EDITAR_ESTOQUE_ALMOX_01 = "editaEstqAlmoxarifado01";
	
	private static final String EDITAR_ESTOQUE_ALMOX_02 = "editaEstqAlmoxarifado02";
	
	private static final String EDITAR_ESTOQUE_ALMOX_03 = "editaEstqAlmoxarifado03";
	
	private static final String EDITAR_ESTOQUE_ALMOX_04 = "editaEstqAlmoxarifado04";
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@Inject
	private SecurityController securityController;
	
	// Parâmetros da conversação
	private Integer seq;
	

	private SceEstoqueAlmoxarifado estoqueAlmox;	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
		if(seq!=null){
			this.estoqueAlmox = estoqueFacade.buscaSceEstoqueAlmoxarifadoPorId(this.seq);
		} else {
			this.estoqueAlmox = new SceEstoqueAlmoxarifado();
			this.estoqueAlmox.setAlmoxarifado(null);
			this.estoqueAlmox.setSolicitacaoCompra(null);
			this.estoqueAlmox.setIndSituacao(DominioSituacao.A);
			this.estoqueAlmox.setFornecedor(null);
			this.estoqueAlmox.setMaterial(null);
			this.estoqueAlmox.setIndEstqMinCalc(false);
			this.estoqueAlmox.setIndConsignado(false);
			this.estoqueAlmox.setIndPontoPedidoCalc(false);
			this.estoqueAlmox.setIndEstocavel(false);
			this.estoqueAlmox.setIndControleValidade(false);		
		}
	}
	
	public Boolean verificaPermissaoCampos(String permissao){
		return securityController.usuarioTemPermissao(permissao, "editar"); 	
	}
	
	public Boolean readOnlyEndereco(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_01)
				||verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_02)
				||(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04)
				&& estoqueAlmox.getIndSituacao().isAtivo())){ 
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean readOnlySituacao(){
		return verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04);
	}
	
	public Boolean readOnlyEstocavel(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04) && estoqueAlmox.getIndSituacao().isAtivo() ) {
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean readOnlyEstqMinCalculado(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04)
			&&(estoqueAlmox.getIndSituacao().isAtivo()
			|| estoqueAlmox.getIndEstocavel())){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean readOnlyQtdeEstqMin(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04)
			&&((estoqueAlmox.getIndSituacao().isAtivo()
			||estoqueAlmox.getIndEstocavel()) && !estoqueAlmox.getIndEstqMinCalc())){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean readOnlyQtdeEstqMax(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04)
				&&((estoqueAlmox.getIndSituacao().isAtivo()
				||estoqueAlmox.getIndEstocavel()) && !estoqueAlmox.getIndEstqMinCalc())){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean readOnlyQtPedidoCalculado(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_03)|| 
				verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04)
				&&((estoqueAlmox.getIndSituacao().isAtivo()||
				estoqueAlmox.getIndEstocavel()))&& !estoqueAlmox.getIndPontoPedidoCalc()){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean readOnlyPPedidoCalculado(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_03)||
				verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04)
				&&((estoqueAlmox.getIndSituacao().isAtivo()||estoqueAlmox.getIndEstocavel()))){
			return true;
		} else {
			return false;
		}
	}	
	
	public Boolean readOnlyTempoRep(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_03)|| 
			verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_04)&&
			(estoqueAlmox.getIndSituacao().isAtivo())){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean readOnlyValidade(){
		if(verificaPermissaoCampos(EDITAR_ESTOQUE_ALMOX_01)) {
			return true;
		} else {
			return false;
		}
	}
	
	//suggestions
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	public List<ScoMaterial>pesquisarMateriais(String param){
		return this.comprasFacade.listarScoMateriais(param, null);
	}
	
	public List<ScoFornecedor>pesquisarFornecedores(String param){
		return this.comprasFacade.obterFornecedor(param);
	}
	
	public List<ScoSolicitacaoDeCompra>pesquisarSCContrato(String param){
		return solicitacaoComprasFacade.obterScoSolicitacoesDeCompras(param);
	}

	/**
	 * Grava Estoque do Almoxarifado
	 */
	public void gravar(){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			// Grava o Estoque do Almoxarifado
			
			if(this.estoqueAlmox.getQtdeEstqMin() < 0 || this.estoqueAlmox.getQtdeEstqMax() < 0 || this.estoqueAlmox.getQtdePontoPedido() < 0) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_QTDE_MAIOR_ZERO");
			}
			
			else {
				this.estoqueBeanFacade.gravarSceEstoqueAlmoxarifado(this.estoqueAlmox, nomeMicrocomputador);
				if(this.seq ==null) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_ESTOQUE_ALMOXARIFADO");
				} else {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_ESTOQUE_ALMOXARIFADO");
				}
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Accesa ao Cadastro de Materiais
	 */
	public String cadastro(){
		return MANTER_MATERIAL_CRUD;
	}

	/**
	 * Accesa à Validade do Material
	 */
	public String manterValidadeMaterial(){
		return PESQUISAR_VALIDADE_MATERIAL;
	}

	/**
	 * Integracao com a estoria #6618: Incluir Saldo de Estoque
	 */
	public String incluirSaldoEstoque(){
		return SALDO_ESTOQUE;
	}
	
	/**
	 * Cancelar 
	 */
	public String cancelar(){
		this.seq = null;
		this.estoqueAlmox = new SceEstoqueAlmoxarifado();
		return PESQUISA_ESTOQUE_ALMOXARIFADO;

	}

	//Getters and setters

	public SceEstoqueAlmoxarifado getEstoqueAlmox() {
		return estoqueAlmox;
	}

	public void setEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox) {
		this.estoqueAlmox = estoqueAlmox;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	

}
