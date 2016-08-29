package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.TransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarEfetivarTransfAutoAlmoxarifadoPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<TransferenciaAutomaticaVO> dataModel;

	private static final long serialVersionUID = 584895749147698821L;

	private static final String EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO = "estoque-efetivarTransferenciaAutoAlmoxarifado";

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@EJB
	private IComprasFacade comprasFacade;

	// Campos de filtro para pesquisa
	private Integer numero;
	private VScoClasMaterial classificacaoMaterial;
	private SceAlmoxarifado almoxarifadoOrigem;
	private SceAlmoxarifado almoxarifadoDestino;

	private TransferenciaAutomaticaVO selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private SceTransferencia getElementoFiltroPesquisa(){
		
		final SceTransferencia elementoFiltroPesquisa = new SceTransferencia();
		
		// Popula filtro de pesquisa
		elementoFiltroPesquisa.setSeq(this.numero);
		if(this.classificacaoMaterial != null){
			final ScoClassifMatNiv5 cn5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(this.classificacaoMaterial.getId().getNumero());
			elementoFiltroPesquisa.setClassifMatNiv5(cn5);
		}
		elementoFiltroPesquisa.setAlmoxarifado(this.almoxarifadoOrigem);
		elementoFiltroPesquisa.setAlmoxarifadoRecebimento(this.almoxarifadoDestino);
		return elementoFiltroPesquisa;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.pesquisarTransferenciaAutoAlmoxarifadoCount(this.getElementoFiltroPesquisa());
	}
	
	@Override
	public List<TransferenciaAutomaticaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<SceTransferencia> listaTransferencias = this.estoqueFacade.pesquisarTransferenciaAutoAlmoxarifado(firstResult, maxResult, orderProperty, false, this.getElementoFiltroPesquisa());
		List<TransferenciaAutomaticaVO> resultados = new LinkedList<TransferenciaAutomaticaVO>();
		
		if(listaTransferencias != null && !listaTransferencias.isEmpty()){
			
			for (SceTransferencia transferencia : listaTransferencias) {
				
				TransferenciaAutomaticaVO vo = new TransferenciaAutomaticaVO();
				
				vo.setSeq(transferencia.getSeq());
				vo.setAlmoxarifadoOrigem(transferencia.getAlmoxarifado());
				vo.setAlmoxarifadoDestino(transferencia.getAlmoxarifadoRecebimento());
				vo.setClassificacaoMaterial(transferencia.getClassifMatNiv5());
				// Popula a descricao da classificação do material conforme a view
				if(transferencia.getClassifMatNiv5() != null){
					final VScoClasMaterial clasMaterial = this.comprasFacade.obterVScoClasMaterialPorNumero(transferencia.getClassifMatNiv5().getNumero());
					vo.setDescricaoClassificacaoMaterial(clasMaterial != null ? clasMaterial.getId().getDescricao() : null);
				}
				vo.setDtGeracao(transferencia.getDtGeracao());
				vo.setServidor(transferencia.getServidor());
				
				resultados.add(vo);
				
			}
		}
		return resultados;
	}
	
	public void excluir()  {
		try {
			if (selecionado != null) {
				this.estoqueBeanFacade.removerTransferenciaAutoAlmoxarifado(selecionado.getSeq());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_TRANSFERENCIA",selecionado.getSeq());
			} else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_REMOVER_TRANSFERENCIA");
			}
			dataModel.reiniciarPaginator();
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);	
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String editar(){
		return EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO;
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.numero = null;
		this.classificacaoMaterial = null;
		this.almoxarifadoOrigem = null;
		this.almoxarifadoDestino = null;
	}
	
	public List<VScoClasMaterial> obterClassificacaoMaterial(String param){
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, null);
	}
	
	public List<SceAlmoxarifado> obterAlmoxarifado(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	} 

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}

	public SceAlmoxarifado getAlmoxarifadoOrigem() {
		return almoxarifadoOrigem;
	}

	public void setAlmoxarifadoOrigem(SceAlmoxarifado almoxarifadoOrigem) {
		this.almoxarifadoOrigem = almoxarifadoOrigem;
	}

	public SceAlmoxarifado getAlmoxarifadoDestino() {
		return almoxarifadoDestino;
	}

	public void setAlmoxarifadoDestino(SceAlmoxarifado almoxarifadoDestino) {
		this.almoxarifadoDestino = almoxarifadoDestino;
	}

	public DynamicDataModel<TransferenciaAutomaticaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<TransferenciaAutomaticaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public TransferenciaAutomaticaVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(TransferenciaAutomaticaVO selecionado) {
		this.selecionado = selecionado;
	}
}