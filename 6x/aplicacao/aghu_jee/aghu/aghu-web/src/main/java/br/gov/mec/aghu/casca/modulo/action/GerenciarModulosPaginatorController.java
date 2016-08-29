package br.gov.mec.aghu.casca.modulo.action;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.aplicacao.action.IntegracaoModulosStartUp;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.casca.permissao.action.PermissaoController;
import br.gov.mec.aghu.casca.vo.ModuloVO;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

public class GerenciarModulosPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 2158672014464370080L;
	
	private static final String PESQUISAR_PERMISSOES = "casca-pesquisarPermissoes";
	private static final String GERENCIAR_MODULOS = "casca-gerenciarModulos";

	
	private static final Log LOG = LogFactory.getLog(GerenciarModulosPaginatorController.class);
	
	private Integer idModulo;
	private Boolean situacaoModulo;
	
	@Inject @Paginator
	private DynamicDataModel<AghParametroVO> dataModel;
	
	
	@Inject
	private IntegracaoModulosStartUp integracaoModulosStartUp;
	
	private Set<String> conjuntoModulosAtivos;
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private ICascaFacade cascaFacade;
	
	private Modulo modulo;
	
	@Inject
	private PermissaoController permissaoController;
	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		conjuntoModulosAtivos = integracaoModulosStartUp.getModulosAtivos();
	}
	
	public DynamicDataModel<AghParametroVO> getDataModel() {
		return dataModel;
	}
	
	@Override
	public List<ModuloVO> recuperarListaPaginada(Integer firstResult, Integer maxResult,String orderProperty, boolean asc) {
		return cascaFacade.listarModulos(firstResult, maxResult, orderProperty,	asc, conjuntoModulosAtivos);
	}

	@Override
	public Long recuperarCount() {
		return cascaFacade.listarModulosCount();
	}
	
	public String pesquisarPermissoes() {
		permissaoController.setModulo(this.getModulo());
		permissaoController.pesquisar();
		permissaoController.setVoltarPara(GERENCIAR_MODULOS);
		return PESQUISAR_PERMISSOES;
	}
	
	
	
	public void setIdSitModulo(Integer idModulo, Boolean situacaoModulo) {
		this.idModulo = idModulo;
		this.situacaoModulo = situacaoModulo;
	}
	
	public void salvar() {
		
		String nomeModulo = cascaFacade.obterModulo(idModulo).getNome();
		if (Boolean.TRUE.equals(situacaoModulo)) {
			LOG.info("Módulo " + nomeModulo + " será ativado");
			integracaoModulosStartUp.ativarModulo(nomeModulo);
		} else {
			LOG.info("Módulo " + nomeModulo + " será desativado");
			integracaoModulosStartUp.desativarModulo(nomeModulo);
		}
		
		if (integracaoModulosStartUp.isModulosAtivosDefinidosPorBancoDeDados()) {
			cascaFacade.alterarSituacaoModulo(idModulo, situacaoModulo);
			this.dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio(Severity.WARN, "CASCA_MENSAGEM_EFEITO_SOMENTE_APOS_REINICIALIZACAO");		
		} else {
			LOG.warn("ATENÇÃO: O sistema não irá alterar o estado do módulo "
					+ nomeModulo + " no banco de dados pois a definição de ativação do mesmo foi feita via configuração na instância de servidor de aplicação.");
		}
	}

	public void cancelar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}
}