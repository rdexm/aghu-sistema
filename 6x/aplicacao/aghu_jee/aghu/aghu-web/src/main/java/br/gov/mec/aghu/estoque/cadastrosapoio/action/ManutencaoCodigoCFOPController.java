package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceCfop;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManutencaoCodigoCFOPController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4809294582601063600L;

	/**
	 * Atributo responsavel por armazenar todo o log de ações
	 */
	private static final Log LOG = LogFactory.getLog(ManutencaoCodigoCFOPController.class);
		
	/**
	 * Constante usada para redirecionar a página quando necessário
	 */
	private static final String PAGE_PESQUISAR_MANUTENCAO_CODIGO_CFOP = "pesquisarManutencaoCodigoCFOP";
	
	/**
	 * Constante usada para redirecionar a página quando necessário
	 */
	private static final String PAGE_MANTER_MANUTENCAO_CODIGO_CFOP = "manterManutencaoCodigoCFOP";
		
	/**
	 * Injeção da Facade para chamada da RN, ON e DAO
	 */
	@EJB
	private IEstoqueFacade iEstoqueFacade;
		
	/**
	 * Atributo utilizado para paginação
	 */
	@Inject @Paginator
	private DynamicDataModel<SceCfop> dataModel;
	
	/**
	 * Atributo usado para VO consulta
	 */
	private SceCfop sceCfopConsulta;
	
	/**
	 * Atributo usado para VO listagem/cadastro/edição
	 */
	private SceCfop sceCfopManutencao;
	
	/**
	 * Atributo usado para verificar se é alteração
	 */
	private Boolean alteracao;
	
	/**
	 * Atributo usado para exibir o botão novo
	 */
	private Boolean exibirBotaoNovo;
	
	/**
	 * Atributos list box (true/false)
	 */
	private DominioSimNao dominioNR;
	private DominioSimNao dominioESL;
	private DominioSimNao dominioOutros;
	
	/**
	 * Atributo usado para que funcione as permissoes do dataTable edit e remove
	 */
	@EJB
	private IPermissionService permissionService;
	
	
	/**
	 * Método executado cada vez que instanciar a página
	 */
	@PostConstruct
	protected void inicializar() {
		
		this.begin(conversation);
		
		//inicializando os objetos
		this.sceCfopConsulta = new SceCfop();
		this.sceCfopManutencao = new SceCfop();
		
		this.exibirBotaoNovo = false;
		
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "cadastarCodigoCFOP", "executar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}
		
	/**
	 * Mensagens do Sistema
	 */		
	private enum CfopMessages implements BusinessExceptionCode {
		ERRO_PESQUISA,
		CFOP_ALTERADO_COM_SUCESSO,
		CFOP_INCLUIDO_COM_SUCESSO,
		CFOP_EXCLUIDO_COM_SUCESSO;
	}
		
	/**
	 * Metodo usado no botão pesquisar
	 */
	public void pesquisar() {
		
		if (dominioNR != null) {
			sceCfopConsulta.setIndNr(dominioNR.isSim());
		
		} else {
			sceCfopConsulta.setIndNr(null);
		}
		
		if (dominioESL != null) {
		sceCfopConsulta.setIndEsl(dominioESL.isSim());
		} else {
			sceCfopConsulta.setIndEsl(null);
		}
		
		if (dominioOutros != null) {
			sceCfopConsulta.setIndOutros(dominioOutros.isSim());
		} else {
			sceCfopConsulta.setIndOutros(null);
		}
		
		this.dataModel.reiniciarPaginator();
		
		this.exibirBotaoNovo = true;
	}
		
	/**
	 * Método responsável por pesquisar a lista de sceCfopVO
	 * @return lista de sceCfopVO
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SceCfop> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<SceCfop> listaSceCfop = new ArrayList<SceCfop>();
		
		try {
			
			listaSceCfop = this.iEstoqueFacade.pesquisarListaCFOP(firstResult, maxResult, orderProperty, asc, this.sceCfopConsulta);
					
		} catch (BaseException e) {
			
			LOG.error("Exceção capturada ao recuperar lista paginada: ", e);
			apresentarMsgNegocio(Severity.INFO, CfopMessages.ERRO_PESQUISA.toString());
		}
		
		return listaSceCfop;
	}

	/**
	 * Método responsável por recuperar a quantidade de CFOP's
	 * @return quantidade de cfop's
	 */
	@Override
	public Long recuperarCount() {
		
		Long countSceCfop = null;
		
		try {
			
			countSceCfop = this.iEstoqueFacade.pesquisarListaCFOPCount(this.sceCfopConsulta);
			
		} catch (BaseException e) {
			
			LOG.error("Exceção capturada ao recuperar count da lista: ", e);
			apresentarMsgNegocio(Severity.INFO, CfopMessages.ERRO_PESQUISA.toString());
		}
		
		return countSceCfop;
	}
	
	/**
	  * Método responsável por inserir ou atualizar CFOP
	  * @return
	  * @throws ApplicationBusinessException
	  */
	public String gravar() throws BaseException {
		try {
			if (this.alteracao) {
				// Alterar CFOP
				this.iEstoqueFacade.atualizarSCFOP(this.sceCfopManutencao);
				// Sucesso
				apresentarMsgNegocio(Severity.INFO, CfopMessages.CFOP_ALTERADO_COM_SUCESSO.toString());
			} else {
				// Inserir CFOP
				this.iEstoqueFacade.inserirCFOP(this.sceCfopManutencao);
				// Sucesso
				apresentarMsgNegocio(Severity.INFO, CfopMessages.CFOP_INCLUIDO_COM_SUCESSO.toString());
			}				
		} catch (ApplicationBusinessException e) {		
			LOG.error("Exceção capturada ao inserir ou atualizar: ", e);
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}	

		return PAGE_PESQUISAR_MANUTENCAO_CODIGO_CFOP;
	}
	
	/**
	 * Método responsável por remover CFOP
	 * @param sceCfopManutencao
	 * @throws ApplicationBusinessException
	 */
	public void remover() throws BaseException {	
		try { 
			// Excluir CFOP
			this.iEstoqueFacade.excluirCFOP(this.sceCfopManutencao);	
			
			// Sucesso
			apresentarMsgNegocio(Severity.INFO, CfopMessages.CFOP_EXCLUIDO_COM_SUCESSO.toString());
			
		} catch (ApplicationBusinessException e) {		
			LOG.error("Exceção capturada ao remover: ", e);
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}			
	}
	
	/**
	 * @return Página de inserção de banco.
	 */
	public String cadastrar() {
		this.alteracao = false;
		this.sceCfopManutencao = new SceCfop();
		
		return PAGE_MANTER_MANUTENCAO_CODIGO_CFOP;
	}
	
	/**
	 * @return Página de alteração de banco.
	 */
	public String editar() {
		this.alteracao = true;
		return PAGE_MANTER_MANUTENCAO_CODIGO_CFOP;
	}
		
	/**
	 * Metodo usado para cancelar a consulta
	 * @return Página de pesquisar Manutenção Código CFOP.
	 */
	public String cancelar() {
		return PAGE_PESQUISAR_MANUTENCAO_CODIGO_CFOP;
	}
			
	/**
	 * 
	 * Método responsável por limpar os filtros da tela
	 * @return Página de pesquisar Manutenção Código CFOP
	 */
	public String limpar() {
		
		this.sceCfopConsulta = new SceCfop();
		this.sceCfopManutencao = new SceCfop();
		
		this.dataModel.limparPesquisa();
		this.exibirBotaoNovo = false;
		
		dominioNR = null;
		dominioESL = null;
		dominioOutros = null;
		
		return PAGE_PESQUISAR_MANUTENCAO_CODIGO_CFOP;
	}
	
	/**
	 * Trunca descrições grandes nas descrições das listagens
	 * 
	 * @param descricao
	 * @return
	 */
	public String truncarDescricao(String descricao) {
		int limite = 64;
		if (StringUtils.length(descricao) >= limite) {
			return StringUtils.substring(descricao, 0, limite).concat("...");
		}
		return descricao;
	}
	
	/**
	* 
	* Declaração dos Getters and Setters
	* 
	*/
	
	public IEstoqueFacade getiEstoqueFacade() {
		return iEstoqueFacade;
	}

	public void setiEstoqueFacade(IEstoqueFacade iEstoqueFacade) {
		this.iEstoqueFacade = iEstoqueFacade;
	}

	public DynamicDataModel<SceCfop> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceCfop> dataModel) {
		this.dataModel = dataModel;
	}

	public SceCfop getSceCfopConsulta() {
		return sceCfopConsulta;
	}

	public void setSceCfopConsulta(SceCfop sceCfopConsulta) {
		this.sceCfopConsulta = sceCfopConsulta;
	}

	public SceCfop getSceCfopManutencao() {
		return sceCfopManutencao;
	}

	public void setSceCfopManutencao(SceCfop sceCfopManutencao) {
		this.sceCfopManutencao = sceCfopManutencao;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public DominioSimNao getDominioNR() {
		return dominioNR;
	}

	public void setDominioNR(DominioSimNao dominioNR) {
		this.dominioNR = dominioNR;
	}

	public DominioSimNao getDominioESL() {
		return dominioESL;
	}

	public void setDominioESL(DominioSimNao dominioESL) {
		this.dominioESL = dominioESL;
	}

	public DominioSimNao getDominioOutros() {
		return dominioOutros;
	}

	public void setDominioOutros(DominioSimNao dominioOutros) {
		this.dominioOutros = dominioOutros;
	}

	public Boolean getAlteracao() {
		return alteracao;
	}

	public void setAlteracao(Boolean alteracao) {
		this.alteracao = alteracao;
	}

}