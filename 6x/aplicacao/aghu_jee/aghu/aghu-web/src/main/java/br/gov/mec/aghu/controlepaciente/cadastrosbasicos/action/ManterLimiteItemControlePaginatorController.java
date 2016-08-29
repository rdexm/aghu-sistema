package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpLimiteItemControle;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterLimiteItemControlePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1837478689922276286L;
	
	private static final String MANTER_LIMITES_ITEM_CONTROLE = "manterLimitesItemControle";
	private static final String PESQUISAR_ITENS_CONTROLE = "pesquisarItensControle";

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;

	private EcpItemControle itemControle;
	
	@Inject @Paginator
	private DynamicDataModel<EcpLimiteItemControle> dataModel;
	
	private EcpLimiteItemControle selecionado;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisarLimitesItemControle(){
	 

	 

		//Executa pesquisa automática da tabela de limites
		dataModel.reiniciarPaginator();
	
	}
	
	
	@Override
	public List<EcpLimiteItemControle> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosControlePacienteFacade.listarLimitesItemControle( firstResult, maxResult, EcpLimiteItemControle.Fields.IDADE_MINIMA.toString(), 
																				 false, itemControle);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosControlePacienteFacade.listarLimitesItemControleCount(itemControle);
	}
	
	public void excluir() {
		try {
			this.cadastrosBasicosControlePacienteFacade.excluirLimiteItemControle(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_LIMITE_ITEM");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar(){
		return MANTER_LIMITES_ITEM_CONTROLE;
	}

	public String inserir(){
		return MANTER_LIMITES_ITEM_CONTROLE;
	}

	public String voltar(){
		return PESQUISAR_ITENS_CONTROLE;
	} 

	public EcpItemControle getItemControle() {
		return itemControle;
	}

	public void setItemControle(EcpItemControle itemControle) {
		this.itemControle = itemControle;
	}

	public DynamicDataModel<EcpLimiteItemControle> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EcpLimiteItemControle> dataModel) {
		this.dataModel = dataModel;
	}


	public EcpLimiteItemControle getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(EcpLimiteItemControle selecionado) {
		this.selecionado = selecionado;
	}
}