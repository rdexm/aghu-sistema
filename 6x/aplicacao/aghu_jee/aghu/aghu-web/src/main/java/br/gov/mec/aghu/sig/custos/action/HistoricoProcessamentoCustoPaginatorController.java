package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.model.SigPassos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCustoLog;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class HistoricoProcessamentoCustoPaginatorController extends ActionController implements ActionPaginator {

	private static final String PROCESSAMENTO_MENSAL_LIST = "processamentoMensalList";

	private static final String PROCESSAMENTO_MENSAL_CRUD = "processamentoMensalCRUD";

	@Inject @Paginator
	private DynamicDataModel<SigProcessamentoCustoLog> dataModel;

	private static final long serialVersionUID = 4047015379797954845L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	private SigProcessamentoCusto processamentoCusto;
	private DominioEtapaProcessamento etapa;
	private SigPassos passo;
	private boolean resolverPendencia;
	private boolean retornarTelaCadastro;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		
		this.resolverPendencia = false;
		//Recebe o parâmetro pela url quando vier direto da central de pendência
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if(request.getParameter("seqProcessamentoCusto") != null && request.getParameter("cxtSeq") != null){
			Integer seqProcessamentoCusto = Integer.valueOf(request.getParameter("seqProcessamentoCusto"));
			this.processamentoCusto = custosSigProcessamentoFacade.obterProcessamentoCusto(seqProcessamentoCusto);
			resolverPendencia = true;
		}
		
		//Processo para pesquisar automáticamente pelo passo
		if(this.getPasso()!= null){
			this.dataModel.reiniciarPaginator();
		}
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return custosSigProcessamentoFacade.pesquisarHistoricoProcessamentoCustoCount(this.getProcessamentoCusto(), this.getEtapa(), this.getPasso());
	}

	@Override
	public List<SigProcessamentoCustoLog> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return custosSigProcessamentoFacade.pesquisarHistoricoProcessamentoCusto(firstResult, maxResult, orderProperty, asc, this.getProcessamentoCusto(),
				this.getEtapa(), this.getPasso());
	}

	public List<SigPassos> listarPassos(){
		return this.custosSigProcessamentoFacade.listarTodosPassos();
	}
	
	public String voltarPesquisa(){
		return PROCESSAMENTO_MENSAL_LIST;
	}
	
	public String voltarCadastro(){
		return PROCESSAMENTO_MENSAL_CRUD;
	}
		
	public SigProcessamentoCusto getProcessamentoCusto() {
		return processamentoCusto;
	}

	public void setProcessamentoCusto(SigProcessamentoCusto processamentoCusto) {
		this.processamentoCusto = processamentoCusto;
	}

	public DominioEtapaProcessamento getEtapa() {
		return etapa;
	}

	public void setEtapa(DominioEtapaProcessamento etapa) {
		this.etapa = etapa;
	}

	public SigPassos getPasso() {
		return passo;
	}

	public void setPasso(SigPassos passo) {
		this.passo = passo;
	}

	public boolean isResolverPendencia() {
		return resolverPendencia;
	}

	public void setResolverPendencia(boolean resolverPendencia) {
		this.resolverPendencia = resolverPendencia;
	}

	public DynamicDataModel<SigProcessamentoCustoLog> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigProcessamentoCustoLog> dataModel) {
	 this.dataModel = dataModel;
	}

	public boolean isRetornarTelaCadastro() {
		return retornarTelaCadastro;
	}

	public void setRetornarTelaCadastro(boolean retornarTelaCadastro) {
		this.retornarTelaCadastro = retornarTelaCadastro;
	}
}
