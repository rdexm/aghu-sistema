package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterGrupoCaracteristicaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3384532177640174372L;

	private static final String MANTER_GRUPO_CARACTERISTICA_CRUD = "manterGrupoCaracteristicaCRUD";
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	

	private AelGrupoResultadoCaracteristica grupoResultadoCaracteristica; 
	
	@Inject @Paginator
	private DynamicDataModel<AelGrupoResultadoCaracteristica> dataModel;
	
	private AelGrupoResultadoCaracteristica selecionado;
	
	private String voltarPara; 
	
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
		dataModel.limparPesquisa();
	}	
	
	public void excluir() {
		try {
			cadastrosApoioExamesFacade.removerGrupoResultadoCaracteristica(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_GRUPO_CARACTERISTICA", grupoResultadoCaracteristica.getDescricao());
		} catch (BaseListException ex) {
			apresentarExcecaoNegocio(ex);		
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_GRUPO_CARACTERISTICA_CRUD;
	}
	
	public String editar(){
		return MANTER_GRUPO_CARACTERISTICA_CRUD;
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosApoioExamesFacade.pesquisarGrupoResultadoCaracteristicaCount(this.grupoResultadoCaracteristica);
	}

	@Override
	public List<AelGrupoResultadoCaracteristica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosApoioExamesFacade.pesquisarGrupoResultadoCaracteristica(grupoResultadoCaracteristica, firstResult, maxResult, orderProperty, asc);
	}

	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		return this.voltarPara;
	}

	/** GET/SET **/
	public AelGrupoResultadoCaracteristica getGrupoResultadoCaracteristica() {
		return grupoResultadoCaracteristica;
	}

	public void setGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		this.grupoResultadoCaracteristica = grupoResultadoCaracteristica;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public DynamicDataModel<AelGrupoResultadoCaracteristica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AelGrupoResultadoCaracteristica> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoResultadoCaracteristica getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoResultadoCaracteristica selecionado) {
		this.selecionado = selecionado;
	}
}