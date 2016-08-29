package br.gov.mec.aghu.transplante.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.ExtratoAlteracoesListaOrgaosVO;
import br.gov.mec.aghu.core.action.ActionController;

public class ExtratoAlteracoesListaOrgaosController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 253201932867446541L;

	private Integer transplanteSeq;
	
	private List<ExtratoAlteracoesListaOrgaosVO> extratoAlteracoesListaOrgaosVO;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	@Inject
	private ListarTransplantesOrgaoAba4PaginatorController listarTransplantesOrgaoAba4PaginatorController;	
	@Inject
	private ListarTransplantesOrgaoAba1PaginatorController listarTransplantesOrgaoAba1PaginatorController;
	@Inject
	private ListarTransplantesOrgaoController listarTransplantesOrgaoController;
	@Inject
	private ListarTransplantesOrgaoAba3PaginatorController listarTransplantesOrgaoAba3PaginatorController;
	@Inject
	private ListarTransplantesOrgaoAba2PaginatorController listarTransplantesOrgaoAba2PaginatorController;
	
	@PostConstruct
	protected void inicializar(){
		begin(conversation);
	}
	
	// consulta o extrato do tranplante para as abas implementadas
	public void consultarExtratoListaOrgaos(){
		if(listarTransplantesOrgaoController.getSelectedTab().equals(0) && listarTransplantesOrgaoAba1PaginatorController.getItemSelecionado() != null){
			this.extratoAlteracoesListaOrgaosVO = transplanteFacade.obterExtratoAlteracoesListaOrgaos(listarTransplantesOrgaoAba1PaginatorController.getItemSelecionado().getSeqTransplante());
		}else if(listarTransplantesOrgaoController.getSelectedTab().equals(1) && listarTransplantesOrgaoAba2PaginatorController.getItemSelecionado() != null){
			this.extratoAlteracoesListaOrgaosVO = transplanteFacade.obterExtratoAlteracoesListaOrgaos(listarTransplantesOrgaoAba2PaginatorController.getItemSelecionado().getSeqTransplante());
		}else if(listarTransplantesOrgaoController.getSelectedTab().equals(2) && listarTransplantesOrgaoAba3PaginatorController.getItemSelecionado() != null){
			this.extratoAlteracoesListaOrgaosVO = transplanteFacade.obterExtratoAlteracoesListaOrgaos(listarTransplantesOrgaoAba3PaginatorController.getItemSelecionado().getSeqTransplante());
		}else if(listarTransplantesOrgaoController.getSelectedTab().equals(3) && listarTransplantesOrgaoAba4PaginatorController.getItemSelecionado() != null){
			this.extratoAlteracoesListaOrgaosVO = transplanteFacade.obterExtratoAlteracoesListaOrgaos(listarTransplantesOrgaoAba4PaginatorController.getItemSelecionado().getSeqTransplante());
		}else{
			this.extratoAlteracoesListaOrgaosVO = null;
		}
	}
	
	/**
	 * Trunca descrição da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}
	
	public Integer getTransplanteSeq() {
		return transplanteSeq;
	}

	public void setTransplanteSeq(Integer transplanteSeq) {
		this.transplanteSeq = transplanteSeq;
	}

	public List<ExtratoAlteracoesListaOrgaosVO> getExtratoAlteracoesListaOrgaosVO() {
		return extratoAlteracoesListaOrgaosVO;
	}

	public void setExtratoAlteracoesListaOrgaosVO(
			List<ExtratoAlteracoesListaOrgaosVO> extratoAlteracoesListaOrgaosVO) {
		this.extratoAlteracoesListaOrgaosVO = extratoAlteracoesListaOrgaosVO;
	}

	public ListarTransplantesOrgaoController getListarTransplantesOrgaoController() {
		return listarTransplantesOrgaoController;
	}

	public void setListarTransplantesOrgaoController(ListarTransplantesOrgaoController listarTransplantesOrgaoController) {
		this.listarTransplantesOrgaoController = listarTransplantesOrgaoController;
	}

	public ListarTransplantesOrgaoAba2PaginatorController getListarTransplantesOrgaoAba2PaginatorController() {
		return listarTransplantesOrgaoAba2PaginatorController;
	}

	public void setListarTransplantesOrgaoAba2PaginatorController(
			ListarTransplantesOrgaoAba2PaginatorController listarTransplantesOrgaoAba2PaginatorController) {
		this.listarTransplantesOrgaoAba2PaginatorController = listarTransplantesOrgaoAba2PaginatorController;
	}
}
