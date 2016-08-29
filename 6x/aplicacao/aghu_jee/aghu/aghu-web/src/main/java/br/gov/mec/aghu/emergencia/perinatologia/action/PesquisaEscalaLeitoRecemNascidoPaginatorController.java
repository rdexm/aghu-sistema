package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.internacao.vo.LeitoVO;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascidoId;
import br.gov.mec.aghu.perinatologia.vo.EscalaLeitoRecemNascidoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class PesquisaEscalaLeitoRecemNascidoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8594905230600884751L;

	@Inject @Paginator
	private DynamicDataModel<EscalaLeitoRecemNascidoVO> dataModel;
	private final String PAGE_CADASTRO = "cadastroEscalaLeitoRecemNascido";

	@Inject
	private IEmergenciaFacade emergenciaFacade;
	private EscalaLeitoRecemNascidoVO itemSelecionado;
	private LeitoVO leito;
	private Servidor servidor;
	
	private Boolean hasPermission;
	private Boolean hasPermissionConsulta;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		verificarPermissoes();
	}
	
	private void verificarPermissoes() {
		hasPermission = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterEscalas", "executar");
		hasPermissionConsulta = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "pesquisarEscalas", "pesquisar");
		this.dataModel.setUserEditPermission(hasPermission);
	}

	/**
	 * Ações da tela.
	 */
	public List<LeitoVO> pesquisarLeitos(String param) {
		try {
			return  this.returnSGWithCount(emergenciaFacade.pesquisarLeitos((String) param),pesquisarLeitosCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<LeitoVO>();
	}
	
	public Long pesquisarLeitosCount(String param) {
		try {
			return emergenciaFacade.pesquisarLeitosCount((String) param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0l;
	}
	
	public List<Servidor> pesquisarServidores(String param) {
		try {
			return  this.returnSGWithCount(emergenciaFacade.pesquisarServidores((String) param),pesquisarServidoresCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<Servidor>();
	}
	
	public Long pesquisarServidoresCount(String param) {
		try {
			return emergenciaFacade.pesquisarServidoresCount((String) param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0l;
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {		
		servidor = null;
		leito = null;
		itemSelecionado = null;
		this.dataModel.limparPesquisa();
	}

	public String redirecionarCadastro() {
		return PAGE_CADASTRO;
	}

	public void excluir() {
		try {
			emergenciaFacade.deletarMcoEscalaLeitoRecemNascidoPorId(criarIdEntity());
			apresentarMsgNegocio(Severity.INFO, "EXCLUSAO_ESCALA_LEITO_SUCESSO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private McoEscalaLeitoRecemNascidoId criarIdEntity() {
		return new McoEscalaLeitoRecemNascidoId(itemSelecionado.getLeito(), itemSelecionado.getMatricula(), itemSelecionado.getVinculo());
	}

	@Override
	public Long recuperarCount() {
		try {
			return emergenciaFacade.pesquisarEscalaLeitoRecemNascidoCount(servidor,leito);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e); 
		}
		return 0l;
	}
	
	public IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	@Override
	public List<EscalaLeitoRecemNascidoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		try {
			return emergenciaFacade.pesquisarEscalaLeitoRecemNascido(
					firstResult, maxResults,orderProperty,
					asc,servidor,leito);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<EscalaLeitoRecemNascidoVO>();
	}

	public DynamicDataModel<EscalaLeitoRecemNascidoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EscalaLeitoRecemNascidoVO> dataModel) {
		this.dataModel = dataModel;
	}	


	public Boolean getHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(Boolean hasPermission) {
		this.hasPermission = hasPermission;
	}

	public EscalaLeitoRecemNascidoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(EscalaLeitoRecemNascidoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Boolean getHasPermissionConsulta() {
		return hasPermissionConsulta;
	}

	public void setHasPermissionConsulta(Boolean hasPermissionConsulta) {
		this.hasPermissionConsulta = hasPermissionConsulta;
	}

	public LeitoVO getLeito() {
		return leito;
	}

	public void setLeito(LeitoVO leito) {
		this.leito = leito;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

}
