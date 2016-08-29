package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemTransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;



public class EfetivarTransfAutoAlmoxarifadoController extends ActionController {


	private static final String CONSULTAR_TRANSFERENCIAS_NAO_EFETIVADAS = "estoque-consultarTransferenciasNaoEfetivadas";

	private static final Log LOG = LogFactory.getLog(EfetivarTransfAutoAlmoxarifadoController.class);

	private static final long serialVersionUID = -2637057510813289846L;

	private static final String EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO = "estoque-efetivarTransferenciaAutoAlmoxarifado";

	private static final String PESQUISAR_ESTOQUE_ALMOXARIFADO = "estoque-pesquisarEstoqueAlmoxarifado";

	private static final String MATERIAL_BLOQUEIO_DESBLOQUEIO = "estoque-materialBloqueioDesbloqueio";

	private static final String PESQUISAR_MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA = "estoque-pesquisarMaterialBloqueioDesbloqueioProblema";


	private SceTransferencia transferencia;

	private Integer seq;
	private String voltarPara;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	// Instância da lista de itens de tranferência
	private List<ItemTransferenciaAutomaticaVO> listaItensTranferencia;

	/*variáveis usadas para ou alterar excluir os itens de uma requisição*/
	private Integer ealSeq;
	private Integer trfSeq;

	// Controla o item de transferencia selecionado na listagem do XHTML
	private ItemTransferenciaAutomaticaVO itemTrsSelecionado;
	private Boolean indEfetivadoOrig;
	private String descricaoClassificacaoMaterial;
	
	/* Parâmetros impressão apósefetivar */

	@Inject
	private RelatorioTransferenciaMaterialController relatorioTransferenciaMaterialController;

	/**
	 * Material a ser inserido na lista
	 */
	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;

	/**
	 * Quantidade enviada a ser inserida na lista
	 */
	private Integer qtdeEnviada;

	/**
	 * Habilita o botão gravar
	 */
	private boolean habilitaBotao = false; 
	
	/**
	 * Campo Editável
	 */
	private VScoClasMaterial classificacaoMaterial;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
		if (this.seq != null){

			//Consulta transfrência através do id
			this.transferencia = this.estoqueFacade.obterTransferenciaPorId(this.seq);

			if (this.transferencia != null) {
				indEfetivadoOrig = transferencia.getEfetivada();

				// Resgata a lista de itens de transferencia
				
				this.listaItensTranferencia = this.estoqueFacade.pesquisarListaItensTransferenciaVOPorTransferencia(transferencia.getSeq());

				if (this.listaItensTranferencia == null || this.listaItensTranferencia.isEmpty()){
					// Inicializa a lista de itens de transferência
					this.listaItensTranferencia = new ArrayList<ItemTransferenciaAutomaticaVO>();
				}else{
					//Seleciona o primeiro item da lista de itens de transferência
					if (this.listaItensTranferencia != null && !this.listaItensTranferencia.isEmpty()){
						this.itemTrsSelecionado = this.listaItensTranferencia.get(0);
					}
				} 
				
				// Quando a transferência é efetivada a lista é recarregada de qualquer forma
				if(this.transferencia.getEfetivada()){
					this.listaItensTranferencia = this.estoqueFacade.pesquisarListaItensTransferenciaVOPorTransferencia(transferencia.getSeq());
				}

				// Popula a descricao da classificação do material conforme a view
				if (this.transferencia.getClassifMatNiv5() != null) {
					this.classificacaoMaterial = this.comprasFacade.obterVScoClasMaterialPorNumero(transferencia.getClassifMatNiv5().getNumero());	
				}
			}
		}
	
	}
	

	/**
	 * Seleciona e controla um item de resquisição de material na listagem do XHMTL
	 */
	public void selecionarItem(ItemTransferenciaAutomaticaVO itemTrs){
		// Seta uma referencia do item selecionado da lista de Itens de Tranferência
		this.itemTrsSelecionado = itemTrs;
	}

	
	/**
	 * Grava a transferencia e itens
	 */
	public void gravar() {
		
		if (this.classificacaoMaterial != null) { 
			
			final ScoClassifMatNiv5 classifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(this.classificacaoMaterial.getId().getNumero());
			transferencia.setClassifMatNiv5(classifMatNiv5);
		
		}
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {
			this.estoqueBeanFacade.atualizarTransferenciaAutoAlmoxarifados(transferencia, nomeMicrocomputador);
			this.estoqueBeanFacade.atualizarItensTransfAutoAlmoxarifados(this.listaItensTranferencia, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_TRANSF_AUTO_ALMOX");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	/**
	 * Efetiva Transferência Automática entre almoxarifados
	 */
	public String efetivar(){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			this.estoqueBeanFacade.atualizarItensTransfAutoAlmoxarifados(this.listaItensTranferencia, nomeMicrocomputador);
			this.estoqueBeanFacade.efetivarTransferenciaAutoAlmoxarifados(this.transferencia, nomeMicrocomputador);
			
			relatorioTransferenciaMaterialController.setNumTransferenciaMaterial(this.transferencia.getSeq());
			relatorioTransferenciaMaterialController.setOrigem(EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO);
			relatorioTransferenciaMaterialController.setIndImprime2Vias(Boolean.TRUE);
			relatorioTransferenciaMaterialController.impressaoDireta();
			
			this.inicio();
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EFETIVAR_TRANSF_AUTO_ALMOX");

		} catch (BaseException e) {
			//  Seta a situação original da efetivação de transferência
			this.transferencia.setEfetivada(this.indEfetivadoOrig);
			super.apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	
// Substituido por 
// serverDataTable: selection="#{efetivarTransfAutoAlmoxarifadoController.itemTrsSelecionado}"	
//	/**
//	 * Seleciona um item nota de transferência selecionado na lista
//	 * @param itemTransferencia
//	 */
//	public void selecionarItemTransferencia(ItemTransferenciaAutomaticaVO itemTransferencia){
//		this.itemTrsSelecionado = itemTransferencia;
//	}


	/**
	 * Remove um item de transferência selecionado na lista
	 * @param itemTransferencia
	 */
	public void excluir() {
		
		try {
		
			if (this.ealSeq != null && this.trfSeq != null) {
				
				String nomeMaterial = estoqueFacade.obterNomeMaterialItemTransferencia(ealSeq, trfSeq);
				this.estoqueBeanFacade.removerItemTransferenciaAutoAlmoxarifado(ealSeq, trfSeq);
				this.pesquisarListaItensTransferenciaPorTransferencia(); // "Reseta" a lista de itens de transferência da controller para correta exibição da mesma
				//this.pesquisarTransferenciaAutomaticaNaoEfetivadaDestino();
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_ITEM_TRANSFERENCIA", nomeMaterial);
		
			} else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_REMOVER_ITEM_TRANSFERENCIA");
			}
			
		} catch (BaseListException e) {
		
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);	
			}
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	/**
	 * Pesquisa/Popula lista de itens de transferência e realiza as interações necessárias na tela
	 */
	public void pesquisarListaItensTransferenciaPorTransferencia(){

		// Resgata a lista de itens de transferência
		this.listaItensTranferencia = this.estoqueFacade.pesquisarListaItensTransferenciaVOPorTransferencia(transferencia.getSeq());	
	
		if(this.listaItensTranferencia != null && !this.listaItensTranferencia.isEmpty()){
			this.itemTrsSelecionado = this.listaItensTranferencia.get(0);
		} 
	}

	public String getAlmoxarifadoDestino(){
		if(this.transferencia!=null){
			return getTransferencia().getAlmoxarifadoRecebimento().getSeqDescricao();
		}
		return null;
	}

	public String getAlmoxarifadoOrigem(){
		if(this.transferencia!=null){
			return getTransferencia().getAlmoxarifado().getSeqDescricao();
		}
		return null;
	}

	public String getGeradoEm() {
		if(transferencia != null){
			return DateUtil.obterDataFormatada(transferencia.getDtGeracao(), DateConstants.DATE_PATTERN_DDMMYYYY) + " - " + transferencia.getServidor().getPessoaFisica().getNome();
		}
		
		return null;
	}


	/**
	 * Método chamado para o botão voltar
	 */
	public String cancelar() {
		if(voltarPara != null){
			if(voltarPara.equals("CONSULTAR_TRANSFERENCIAS_NAO_EFETIVADAS")){
				return CONSULTAR_TRANSFERENCIAS_NAO_EFETIVADAS;
			}
		}
		return voltarPara;
	}

	/**
	 * Adiciona item na lista
	 */
	public void adicionar() {
		
		if (getQtdeEnviada() == null) {
			
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_QTDE_INVALIDA_ITEM_TRANSFERENCIA");
			
		} else {
			
			try {

				SceItemTransferencia itemTransferencia = new SceItemTransferencia();
				itemTransferencia.setTransferencia(getTransferencia());
				itemTransferencia.setQuantidade(getQtdeEnviada());
				itemTransferencia.setQtdeEnviada(getQtdeEnviada());
				itemTransferencia.setUnidadeMedida(getEstoqueAlmoxarifado().getUnidadeMedida());
				itemTransferencia.setEstoqueAlmoxarifado(getEstoqueAlmoxarifado());
				itemTransferencia.setEstoqueAlmoxarifadoOrigem(this.estoqueFacade.obterEstoqueAlmoxarifadoOrigem(getTransferencia().getAlmoxarifado().getSeq(), getEstoqueAlmoxarifado().getMaterial().getCodigo(), getEstoqueAlmoxarifado().getFornecedor().getNumero()));

				estoqueFacade.inserir(itemTransferencia);
				this.pesquisarListaItensTransferenciaPorTransferencia();
				
				String nomeMaterial = estoqueFacade.obterNomeMaterialItemTransferencia(itemTransferencia.getId().getEalSeq(), itemTransferencia.getId().getTrfSeq());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ADICIONAR_ITEM_TRANSFERENCIA", nomeMaterial);
			
				this.estoqueAlmoxarifado = null;
				this.qtdeEnviada = null;
				this.habilitaBotao = false;
				
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}


	public List<SceEstoqueAlmoxarifado> obterMaterial(String paramPesq) throws ApplicationBusinessException {

		AghParametros parametroFornecedor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		Integer frnNumero = null;

		if (parametroFornecedor != null) {
			frnNumero = parametroFornecedor.getVlrNumerico().intValue();
		}

		return this.estoqueFacade.pesquisarMateriaisPorTransferencia(this.transferencia.getAlmoxarifado().getSeq(), this.transferencia.getAlmoxarifadoRecebimento().getSeq(), frnNumero, paramPesq);

	}
	
	/**
	 * Obtem lista para sugestion box de classificação de material de NÍVEL 5
	 */
	public List<VScoClasMaterial> obterClassificacaoMaterial(String param){
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, null);
	}
	
	
	public String pesquisarEstoqueAlmoxarifado(){
		return PESQUISAR_ESTOQUE_ALMOXARIFADO;
	}
	
	public String desbloquearMaterial(){
		return MATERIAL_BLOQUEIO_DESBLOQUEIO;
	}
	
	public String desbloquearMaterialProblema(){
		return PESQUISAR_MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA;
	}
	
	

	public List<ItemTransferenciaAutomaticaVO> getListaItensTranferencia() {
		return listaItensTranferencia;
	}

	public void setListaItensTranferencia(
			List<ItemTransferenciaAutomaticaVO> listaItensTranferencia) {
		this.listaItensTranferencia = listaItensTranferencia;
	}

	public ItemTransferenciaAutomaticaVO getItemTrsSelecionado() {
		return itemTrsSelecionado;
	}

	public void setItemTrsSelecionado(
			ItemTransferenciaAutomaticaVO itemTrsSelecionado) {
		this.itemTrsSelecionado = itemTrsSelecionado;
	}

	public Boolean getIndEfetivadoOrig() {
		return indEfetivadoOrig;
	}

	public void setIndEfetivadoOrig(Boolean indEfetivadoOrig) {
		this.indEfetivadoOrig = indEfetivadoOrig;
	}

	public Integer getSeq() {
		return seq;
	}

	public Integer getEalSeq() {
		return ealSeq;
	}

	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}

	public Integer getTrfSeq() {
		return trfSeq;
	}

	public void setTrfSeq(Integer trfSeq) {
		this.trfSeq = trfSeq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public SceTransferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(SceTransferencia transferencia) {
		this.transferencia = transferencia;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getDescricaoClassificacaoMaterial() {
		return descricaoClassificacaoMaterial;
	}

	public void setDescricaoClassificacaoMaterial(
			String descricaoClassificacaoMaterial) {
		this.descricaoClassificacaoMaterial = descricaoClassificacaoMaterial;
	}

	public Integer getQtdeEnviada() {
		return qtdeEnviada;
	}

	public void setQtdeEnviada(Integer qtdeEnviada) {
		this.qtdeEnviada = qtdeEnviada;
	}

	public boolean isHabilitaBotao() {
		return habilitaBotao;
	}

	public void setHabilitaBotao(boolean habilitaBotao) {
		this.habilitaBotao = habilitaBotao;
	}

	public void habilitaBotaoGravar() {
		setHabilitaBotao(true);
	}

	public void desabilitaBotaoGravar() {
		setHabilitaBotao(false);
	}

	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return estoqueAlmoxarifado;
	}

	public void setEstoqueAlmoxarifado(
			SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}

	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}

}