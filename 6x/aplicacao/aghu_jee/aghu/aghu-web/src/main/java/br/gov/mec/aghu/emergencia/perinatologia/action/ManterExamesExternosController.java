package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoExameExterno;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de exames externos
 * 
 * @author geancarlo
 * 
 */
public class ManterExamesExternosController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 6589546988357451478L;

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private McoExameExterno exameExterno;
	@Inject @Paginator
	private DynamicDataModel<McoExameExterno> dataModel;
	private Boolean pesquisaAtiva;
	private Boolean novaSituacaoExameExterno;
	private Boolean cadastrarExameExterno;
	private String novoExameExterno;
	private String filtroNome;
	private DominioSituacao filtroSituacao;
	private boolean cadastrarExamesExternos;
	private boolean pesquisarExamesExternos;
	private McoExameExterno selecao;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		
		this.cadastrarExamesExternos = getPermissionService().usuarioTemPermissao(
				obterLoginUsuarioLogado(), "cadastrarExamesExternos", "executar");
		
		this.pesquisarExamesExternos = getPermissionService().usuarioTemPermissao(
				obterLoginUsuarioLogado(), "pesquisarExamesExternos", "executar");
		
		setFiltroSituacao(DominioSituacao.A);
		
	}
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	/**
	 * Ação do botão de Pequisa da pagina de exames externos
	 */
	public void pesquisar() {
		setPesquisaAtiva(Boolean.TRUE);
		setCadastrarExameExterno(Boolean.TRUE);
		setNovaSituacaoExameExterno(Boolean.TRUE);
		this.getDataModel().reiniciarPaginator();
	}

	/**
	 * Ação do botão limpar da pagina de exames externos
	 */
	public void limparPesquisa() {
		setExameExterno(new McoExameExterno());
		setPesquisaAtiva(Boolean.FALSE);
		setCadastrarExameExterno(Boolean.FALSE);
		setNovoExameExterno(null);
		setFiltroNome(null);
		setFiltroSituacao(DominioSituacao.A);
		setSelecao(null);
		this.getDataModel().limparPesquisa();

	}

	/**
	 * Ação do botão ATIVAR/INATIVAR da pagina de exames externos
	 */
	public void ativarInativar() {
		try {
			if (exameExterno != null) {
				this.emergenciaFacade.ativarInativarExameExterno(exameExterno);
				this.getDataModel().reiniciarPaginator();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Ação do botão Adicionar da pagina de exames externos
	 */
	public void adicionarExameExterno()  {
		exameExterno = new McoExameExterno();
		exameExterno.setDescricao(getNovoExameExterno());
		exameExterno.setIndSituacao(DominioSituacao.getInstance(getNovaSituacaoExameExterno()));
		try {
			this.emergenciaFacade.persistirExameExterno(exameExterno);
			setNovoExameExterno(null);
			setNovaSituacaoExameExterno(Boolean.TRUE);
			this.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			setNovoExameExterno(null);
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	/**
	 * Ação do botão Excluir da pagina de listagem de exames externos
	 */
	public void excluir() {
		try {
			if (exameExterno != null) {
				this.emergenciaFacade.excluirExameExterno(exameExterno);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_EXCLUSAO_EXAME_EXTERNO_SUCESSO");
				setNovoExameExterno(null);
				this.getDataModel().reiniciarPaginator();
			}
			pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean getConvertDominioSituacaoToBoolean(DominioSituacao situacao) {
		return situacao != null ?  situacao.isAtivo() : Boolean.FALSE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<McoExameExterno> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<McoExameExterno> examesExternos = this.emergenciaFacade.pesquisarExamesExternos(firstResult, maxResult, orderProperty, asc, getFiltroNome(), getFiltroSituacao());

		return examesExternos;
	}

	@Override
	public Long recuperarCount() {
		return this.emergenciaFacade.pesquisarExamesExternosCount(getFiltroNome(), getFiltroSituacao());
	}

	public McoExameExterno getExameExterno() {
		return exameExterno;
	}

	public void setExameExterno(McoExameExterno examesExternos) {
		this.exameExterno = examesExternos;
	}

	public DynamicDataModel<McoExameExterno> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<McoExameExterno> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public Boolean getCadastrarExameExterno() {
		return cadastrarExameExterno;
	}

	public void setCadastrarExameExterno(Boolean cadastrarExameExterno) {
		this.cadastrarExameExterno = cadastrarExameExterno;
	}

	public String getNovoExameExterno() {
		return novoExameExterno;
	}

	public void setNovoExameExterno(String novoExameExterno) {
		this.novoExameExterno = novoExameExterno;
	}

	public String getFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(String filtroNome) {
		this.filtroNome = filtroNome;
	}

	public DominioSituacao getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacao filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public Boolean getNovaSituacaoExameExterno() {
		return novaSituacaoExameExterno;
	}

	public void setNovaSituacaoExameExterno(Boolean novaSituacaoExameExterno) {
		this.novaSituacaoExameExterno = novaSituacaoExameExterno;
	}

	public boolean isCadastrarExamesExternos() {
		return cadastrarExamesExternos;
	}

	public void setCadastrarExamesExternos(boolean cadastrarExamesExternos) {
		this.cadastrarExamesExternos = cadastrarExamesExternos;
	}

	public boolean isPesquisarExamesExternos() {
		return pesquisarExamesExternos;
	}

	public void setPesquisarExamesExternos(boolean pesquisarExamesExternos) {
		this.pesquisarExamesExternos = pesquisarExamesExternos;
	}

	public McoExameExterno getSelecao() {
		return selecao;
	}

	public void setSelecao(McoExameExterno selecao) {
		this.selecao = selecao;
	}
}
