package br.gov.mec.aghu.compras.parecer.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.action.FornecedorStringUtils;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.parecer.business.IParecerFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.suprimentos.action.ManterMarcaComercialController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável por manter análise técnica de item de proposta de
 * fornecedor.
 * 
 * @author mlcruz
 */

public class AnaliseTecnicaItemPropostaFornecedorController extends	ActionController {

	private static final String AVALIACAO_PROPOSTAS_PARECER_TECNICO_LIST = "avaliacaoPropostasParecerTecnicoList";

	private static final String MANTER_MARCA_COMERCIAL = "compras-manterMarcaComercial";

	private static final long serialVersionUID = -1952302811728991196L;
	
	// Parâmetros
	
	/** ID da Licitação */
	private Integer licitacaoId;
	
	/** ID do Fornecedor */
	private Integer fornecedorId;
	
	/** ID do Item da Proposta */
	private Short itemId;
	
	// Dependências
	
	@EJB
	private IParecerFacade parecerFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@Inject
	private ManterMarcaComercialController manterMarcaComercialController;
	
	private ScoMarcaComercial marcaComercialInserida;

	private ScoMarcaModelo marcaModeloInserida;
	
	// Dados
	
	/** Item da Proposta */
	private ScoItemPropostaFornecedor itemProposta;
	
	/** Solicitação de Compras */
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	
	/** Flag para alteração de marca e modelo. */
	private Boolean alteraMarcaModelo = false;
	
	private ScoMarcaComercial marcaComercialOriginal;

	private ScoMarcaModelo marcaModeloOriginal;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Carrega item e SC.
	 */
	public void carregar() {
	 
        ScoItemPropostaFornecedorId id = new ScoItemPropostaFornecedorId(
					licitacaoId, fornecedorId, itemId);
			
		itemProposta = comprasFacade.obterItemPropostaFornPorChavePrimaria(id);
		itemProposta.setPropostaFornecedor(pacFacade.obterPropostaFornecedor(itemProposta.getPropostaFornecedor().getId()));
		itemProposta.getPropostaFornecedor().setLicitacao(pacFacade.obterLicitacao(itemProposta.getPropostaFornecedor().getLicitacao().getNumero()));
			
			
		itemProposta.setMarcaComercial(comprasFacade.obterMarcaComercialPorCodigo(itemProposta.getMarcaComercial().getCodigo()));
			
		if (itemProposta.getModeloComercial() != null){
		    itemProposta.setModeloComercial(comprasFacade.buscaScoMarcaModeloPorId(itemProposta.getModeloComercial().getId().getSeqp(), itemProposta.getModeloComercial().getId().getMcmCodigo()));
		}
			
			
		setMarcaModeloOriginal();
			
		Integer scId = parecerFacade.obterScParaAnaliseTecnica(id);
		solicitacaoCompra = solicitacaoComprasFacade.obterSolicitacaoDeCompra(scId);
		solicitacaoCompra.setMaterial(comprasFacade.obterMaterialPorId(solicitacaoCompra.getMaterial().getCodigo()));
		solicitacaoCompra.getMaterial().setUnidadeMedida(comprasFacade.obterUnidadeMedidaPorId(solicitacaoCompra.getMaterial().getUnidadeMedida().getCodigo()));
		
		carregarValoresInseridos();
		
		if (marcaComercialInserida != null) {
			itemProposta.setMarcaComercial(marcaComercialInserida);
		}
		
		if (marcaModeloInserida != null) {
			itemProposta.setModeloComercial(marcaModeloInserida);
		}
	
	}
	

	
	private void carregarValoresInseridos(){
		this.marcaComercialInserida = this.manterMarcaComercialController.getMarcaComercialInserida();
		this.marcaModeloInserida = this.manterMarcaComercialController.getMarcaModeloInserida();
	}
	
	/**
	 * Obtem descrição do fornecedor.
	 * 
	 * @return Descrição
	 */
	public String getFornecedor() {
		itemProposta.getPropostaFornecedor().setFornecedor(comprasFacade.obterFornecedorPorChavePrimaria(itemProposta.getPropostaFornecedor().getFornecedor().getNumero()));
		return String.format("%s - %s", itemProposta.getPropostaFornecedor().getFornecedor().getNumero(),
				FornecedorStringUtils.format(itemProposta.getPropostaFornecedor().getFornecedor()));
	}
	
	/**
	 * Pesquisa marcas comerciais.
	 * 
	 * @param filter Filtro
	 * @return Marcas Comerciais
	 */
	public List<ScoMarcaComercial> pesquisarMarcasComerciais(String filter) {
		return comprasFacade.obterMarcas(filter);
	}
	
	/**
	 * Pesquisa modelos.
	 * 
	 * @param filter Filtro
	 * @return Modelos
	 */
	public List<ScoMarcaModelo> pesquisarModelos(String filter) {
		return comprasFacade.pesquisarMarcaModeloPorCodigoDescricao(filter, itemProposta.getMarcaComercial(), true);
	}
	
	/**
	 * Atualiza a partir da marca.
	 */
	public void refreshFromMarca() {
		if (itemProposta.getMarcaComercial() == null) {
			itemProposta.setModeloComercial(null);
		}
	}
	
	/**
	 * Atualiza a partir da flag autoriza julgamento.
	 */
	public void refreshFromAutorizaJulgamento() {
		if (!Boolean.TRUE.equals(itemProposta.getIndAutorizUsr())) {
			itemProposta.setJustifAutorizUsr(null);
		}
	}
	
	/**
	 * Obtem motivos de desclassificação.
	 * 
	 * @return Motivos
	 */
	public DominioMotivoDesclassificacaoItemProposta[] getMotivosDesclassificacao() {
		return DominioMotivoDesclassificacaoItemProposta.values();
	}
	
	/**
	 * Efetua a gravação dos dados.
	 */
	public void gravar() {
		try {
			parecerFacade.gravarAnaliseTecnica(itemProposta);

			apresentarMsgNegocio(
					Severity.INFO, "MESSAGE_PARECER_TECNICO_PROPOSTA_ALTERADO");
			
			setMarcaModeloOriginal();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Retorna a para estória #5505 – Avaliar propostas para emitir o parecer técnico.
	 */
	public String voltar() {
		return AVALIACAO_PROPOSTAS_PARECER_TECNICO_LIST;
	}
	
	
	public String adicionarMarca(){
		return MANTER_MARCA_COMERCIAL;
	}
	
	
	public String adicionarModelo(){
		return MANTER_MARCA_COMERCIAL;
	}
	
	
	
	/**
	 * Atualiza a partir da flag de alteração de marca/modelo.
	 */
	public void refreshFromAlteraMarcaModelo() {
		if (!Boolean.TRUE.equals(alteraMarcaModelo)) {
			itemProposta.setMarcaComercial(marcaComercialOriginal);
			itemProposta.setModeloComercial(marcaModeloOriginal);
		}
	}
	
	/**
	 * Atualiza marca e modelo originais.
	 */
	private void setMarcaModeloOriginal() {			
		marcaComercialOriginal = itemProposta.getMarcaComercial();
		marcaModeloOriginal = itemProposta.getModeloComercial();
	}
	
	// Getters/Setters

	public Integer getLicitacaoId() {
		return licitacaoId;
	}

	public void setLicitacaoId(Integer licitacaoId) {
		this.licitacaoId = licitacaoId;
	}

	public Integer getFornecedorId() {
		return fornecedorId;
	}

	public void setFornecedorId(Integer fornecedorId) {
		this.fornecedorId = fornecedorId;
	}

	public Short getItemId() {
		return itemId;
	}

	public void setItemId(Short itemId) {
		this.itemId = itemId;
	}

	public ScoItemPropostaFornecedor getItemProposta() {
		return itemProposta;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public Boolean getAlteraMarcaModelo() {
		return alteraMarcaModelo;
	}

	public void setAlteraMarcaModelo(Boolean alteraMarcaModelo) {
		this.alteraMarcaModelo = alteraMarcaModelo;
	}
	
	public DominioSimNao getIndAutorizUsr() {
		return itemProposta.getIndAutorizUsr() != null ? 
				DominioSimNao.getInstance(itemProposta.getIndAutorizUsr()) : null;
	}

	public void setIndAutorizUsr(DominioSimNao indAutorizUsr) {
		itemProposta.setIndAutorizUsr(indAutorizUsr != null ? indAutorizUsr.isSim() : null);
	}
}