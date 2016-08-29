package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarTransferenciaMateriaisEventualPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<SceTransferencia> dataModel;

	private static final long serialVersionUID = 584895749147698821L;

	private static final String EFETIVAR_TRANSFERENCIA_MATERIAIS_EVENTUAL = "estoque-efetivarTransferenciaMateriaisEventual";

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;


	private Integer numero;
	private SceAlmoxarifado almoxarifadoOrigem;
	private SceAlmoxarifado almoxarifadoDestino;
	private DominioSimNao efetivado;
	private DominioSimNao estornado;
	private Date dtGeracao;
	private Date dtEfetivacao;

	private SceTransferencia selecionado;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String efetivarTransferenciaMateriaisEventual(){
		return EFETIVAR_TRANSFERENCIA_MATERIAIS_EVENTUAL;
	}
	

	private SceTransferencia getElementoFiltroPesquisa(){

		final SceTransferencia elementoFiltroPesquisa = new SceTransferencia();
		elementoFiltroPesquisa.setSeq(this.numero);
		elementoFiltroPesquisa.setAlmoxarifado(this.almoxarifadoOrigem);
		elementoFiltroPesquisa.setAlmoxarifadoRecebimento(this.almoxarifadoDestino);
		if(this.efetivado !=null && this.efetivado.equals(DominioSimNao.S)){
			elementoFiltroPesquisa.setEfetivada(Boolean.TRUE);
		}else if(this.efetivado !=null && this.efetivado.equals(DominioSimNao.N)){
			elementoFiltroPesquisa.setEfetivada(Boolean.FALSE);
		}
		
		if(this.estornado!=null && this.estornado.equals(DominioSimNao.S)){
			elementoFiltroPesquisa.setEstorno(Boolean.TRUE);
		}else if(this.estornado!=null && this.estornado.equals(DominioSimNao.N)){
			elementoFiltroPesquisa.setEstorno(Boolean.FALSE);
		}
		
		elementoFiltroPesquisa.setDtEfetivacao(this.dtEfetivacao);
		elementoFiltroPesquisa.setDtGeracao(this.dtGeracao);


		return elementoFiltroPesquisa;
	}


	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.pesquisarTransferenciaMateriaisEventualCount(this.getElementoFiltroPesquisa());
	}

	@Override
	public List<SceTransferencia> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.estoqueFacade.pesquisarTransferenciaMateriaisEventual(firstResult, maxResult, orderProperty, asc, this.getElementoFiltroPesquisa());
	}
	
	public void excluir()  {
		try {
			if (selecionado != null) {
				this.estoqueBeanFacade.removerTransferenciaAutoAlmoxarifado(selecionado.getSeq());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_TRANSFERENCIA",selecionado.getSeq());
			} else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_REMOVER_TRANSFERENCIA");
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

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.numero = null;
		this.almoxarifadoOrigem = null;
		this.almoxarifadoDestino = null;
		this.efetivado = null;
		this.estornado = null;
		this.dtEfetivacao = null;
		this.dtGeracao= null;
	}

	public List<VScoClasMaterial> obterClassificacaoMaterial(Object param){
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

	public DominioSimNao getEfetivado() {
		return efetivado;
	}

	public void setEfetivado(DominioSimNao efetivado) {
		this.efetivado = efetivado;
	}

	public DominioSimNao getEstornado() {
		return estornado;
	}

	public void setEstornado(DominioSimNao estornado) {
		this.estornado = estornado;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public Date getDtEfetivacao() {
		return dtEfetivacao;
	}

	public void setDtEfetivacao(Date dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}

	public DynamicDataModel<SceTransferencia> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceTransferencia> dataModel) {
		this.dataModel = dataModel;
	}

	public SceTransferencia getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(SceTransferencia selecionado) {
		this.selecionado = selecionado;
	}
}