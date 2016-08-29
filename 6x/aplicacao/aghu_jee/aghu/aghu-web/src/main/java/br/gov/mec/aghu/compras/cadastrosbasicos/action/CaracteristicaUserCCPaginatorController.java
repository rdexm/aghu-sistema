package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CaracteristicaUserCCPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1289998913199756967L;

	private static final String CARACTERISTICA_USER_CCCRUD = "caracteristicaUserCCCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private SecurityController securityController;	


	private ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC = new ScoCaracteristicaUsuarioCentroCusto();

	@Inject @Paginator
	private DynamicDataModel<ScoCaracteristicaUsuarioCentroCusto> dataModel;
	
	private ScoCaracteristicaUsuarioCentroCusto selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarPermissoesCompras,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();		
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setCaracteristicaUserCC(new ScoCaracteristicaUsuarioCentroCusto());		
	}

	public String inserir() {		
		return CARACTERISTICA_USER_CCCRUD;
	}

	public String editar() {		
		return CARACTERISTICA_USER_CCCRUD;
	}

	public String visualizar() {		
		return CARACTERISTICA_USER_CCCRUD;
	}
	
	public void excluir() {
		try {
			this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(selecionado);				
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CARACTERISTA_USER_CC_DELETE_SUCESSO");
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarCaracteristicaUserCCCount(this.getCaracteristicaUserCC());
	}

	@Override
	public List<ScoCaracteristicaUsuarioCentroCusto> recuperarListaPaginada(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarCaracteristicaUserCC(firstResult, maxResults,orderProperty, asc,getCaracteristicaUserCC());
	}

	
	// Metódo para Suggestion Box de Servidor
	public List<RapServidores> obterServidor(String objPesquisa) {

		try {
			return this.registroColaboradorFacade.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}
	
	/*** CENTRO DE CUSTO ***/
	public List<FccCentroCustos> listarCentroCustosSolic(String objPesquisa) {
		return this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustos(objPesquisa),listarCentroCustosSolicCount(objPesquisa));
	}

	public Long listarCentroCustosSolicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}

	/*** CARACTERISTICAS ****/
	public List<ScoCaracteristica> listarCaracteristicas(String objPesquisa) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarCaracteristicasPorCodigoOuDescricao(objPesquisa),listarCaracteristicasCount(objPesquisa));
	}

	public Long listarCaracteristicasCount(String objPesquisa) {
		return this.comprasCadastrosBasicosFacade.pesquisarCaracteristicasPorCodigoOuDescricaoCount(objPesquisa);
	}

	// ### GETs e SETs ###

	public ScoCaracteristicaUsuarioCentroCusto getCaracteristicaUserCC() {
		return caracteristicaUserCC;
	}

	public void setCaracteristicaUserCC(
			ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {
		this.caracteristicaUserCC = caracteristicaUserCC;
	} 

	public DynamicDataModel<ScoCaracteristicaUsuarioCentroCusto> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ScoCaracteristicaUsuarioCentroCusto> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoCaracteristicaUsuarioCentroCusto getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoCaracteristicaUsuarioCentroCusto selecionado) {
		this.selecionado = selecionado;
	}
}
