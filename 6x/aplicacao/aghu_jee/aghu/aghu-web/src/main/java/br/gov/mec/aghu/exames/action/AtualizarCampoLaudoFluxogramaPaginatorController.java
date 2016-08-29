package br.gov.mec.aghu.exames.action;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AtualizarCampoLaudoFluxogramaPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = -4639524605824245511L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade; 

	//campos de pesquisa
	private AelCampoLaudo campoLaudo = new AelCampoLaudo(); 
	
	private DominioSimNao fluxo;


	@Inject @Paginator
	private DynamicDataModel<AelCampoLaudo> dataModel;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		if(this.fluxo != null) {
			this.campoLaudo.setFluxo(DominioSimNao.valueOf(this.fluxo.name()).isSim());
		} else {
			this.campoLaudo.setFluxo(null);
		}
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.campoLaudo = new AelCampoLaudo();
		this.fluxo = null;
		dataModel.limparPesquisa();
	}	
	
	public void gravar(AelCampoLaudo item) {
		try {

			if(item.getNomeSumario() != null){
				item.setNomeSumario(item.getNomeSumario().trim());
			}

			if(item.getNome() != null){
				item.setNome(item.getNome().trim());
			}
			
			this.cadastrosApoioExamesFacade.persistirCampoLaudo(item);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ATUALIZAR_CAMPO_LAUDO_FLUXOGRAMA", item.getNome());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisarCampoLaudoFluxogramaCount(this.campoLaudo);
	}

	@Override
	public List<AelCampoLaudo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisarCampoLaudoFluxograma(firstResult, maxResult, orderProperty, asc, this.campoLaudo);
	}

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public DominioSimNao getFluxo() {
		return fluxo;
	}

	public void setFluxo(DominioSimNao fluxo) {
		this.fluxo = fluxo;
	}

	public DynamicDataModel<AelCampoLaudo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelCampoLaudo> dataModel) {
		this.dataModel = dataModel;
	}
}