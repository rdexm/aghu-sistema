package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do listagem de equipes.
 * 
 * @author david.laks
 */

public class EquipePaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3558669372432203420L;
	
	private static final String REDIRECIONA_MANTER_EQUIPE = "equipeCRUD";
	
	private static final Log LOG = LogFactory.getLog(EquipePaginatorController.class);
	
	@Inject
	private IAghuFacade aghuFacade;
	
	@Inject
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private EquipeController equipeController; 

	/**
	 * Atributo utilizado para controlar a exibicao do botao "incluir equipe"
	 */
	private boolean exibirBotaoIncluirEquipe;
	
	/**
	 * Atributo referente ao campo de filtro de código da equipe na tela de
	 * pesquisa.
	 */
	private Integer codigoPesquisaEquipe;
	
	/**
	 * Atributo referente ao campo de filtro de nome da equipe na tela de
	 * pesquisa.
	 */
	private String nomePesquisaEquipe;

	/**
	 * Atributo referente ao campo de filtro de profissional responsável da
	 * equipe na tela de pesquisa.
	 */
	private RapServidoresVO responsavelPesquisaEquipe;

	/**
	 * Atributo referente ao campo de filtro de equipe ativa na tela de
	 * pesquisa.
	 */
	private DominioSituacao ativoPesquisaEquipe;

	/**
	 * Atributo referente ao campo de filtro de placar risco neonatal da equipe
	 * tela de pesquisa.
	 */
	private DominioSimNao placarRiscoNeonatalPesquisaEquipe;

	@Inject @Paginator
	private DynamicDataModel<AghEquipes> dataModel;
	
	private AghEquipes equipeSelecionada;
	
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirEquipe = true;
	}

	public void limparPesquisa() {
		this.codigoPesquisaEquipe = null;
		this.nomePesquisaEquipe = null;
		this.responsavelPesquisaEquipe = null;
		this.ativoPesquisaEquipe = null;
		this.placarRiscoNeonatalPesquisaEquipe = null;
		this.exibirBotaoIncluirEquipe = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Equipes.
	 */
	public void excluir() {
		this.dataModel.reiniciarPaginator();
		AghEquipes equipe = equipeSelecionada;
		try {
			if (equipe != null) {
				// Verifica se tem grades de agendamento do ambulatório vinculadas a equipe
				ambulatorioFacade.existeGradeAgendamentoConsultaComEquipe(equipe);
				
				this.aghuFacade.removerEquipe(equipe);
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_EQUIPE", equipe.getNome());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_EQUIPE_INVALIDA");
			}

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		}	
	}

	public void limparResponsavel() {
		this.responsavelPesquisaEquipe = null;
	}
	
	public boolean isMostrarLinkExcluirResponsavel() {
		return this.responsavelPesquisaEquipe != null;
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return aghuFacade.pesquisaEquipesCount(this.codigoPesquisaEquipe, this.nomePesquisaEquipe,
				this.responsavelPesquisaEquipe,
				this.ativoPesquisaEquipe,
				this.placarRiscoNeonatalPesquisaEquipe);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AghEquipes> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		List<AghEquipes> result = this.aghuFacade.pesquisarEquipes(firstResult,
				maxResults, orderProperty, asc, this.codigoPesquisaEquipe, this.nomePesquisaEquipe,
				this.responsavelPesquisaEquipe,
				this.ativoPesquisaEquipe,
				this.placarRiscoNeonatalPesquisaEquipe);

		if (result == null) {
			result = new ArrayList<AghEquipes>();
		}

		return result;
	}
	
	public String iniciarInclusao() {
		return REDIRECIONA_MANTER_EQUIPE;
	}
	
	public String editar() {
		equipeController.setAghEquipe(equipeSelecionada);
		return REDIRECIONA_MANTER_EQUIPE;
	}
	
	public List<RapServidoresVO> pesquisarRapServidoresVOPorCodigoDescricao(String parametro){
		return registroColaboradorFacade.pesquisarRapServidoresVOPorCodigoDescricao(parametro);
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}
	
	public boolean isExibirBotaoIncluirEquipe() {
		return exibirBotaoIncluirEquipe;
	}

	public void setExibirBotaoIncluirEquipe(boolean exibirBotaoIncluirEquipe) {
		this.exibirBotaoIncluirEquipe = exibirBotaoIncluirEquipe;
	}

	public String getNomePesquisaEquipe() {
		return nomePesquisaEquipe;
	}

	public void setNomePesquisaEquipe(String nomePesquisaEquipe) {
		this.nomePesquisaEquipe = nomePesquisaEquipe;
	}

	public RapServidoresVO getResponsavelPesquisaEquipe() {
		return responsavelPesquisaEquipe;
	}

	public void setResponsavelPesquisaEquipe(
			RapServidoresVO responsavelPesquisaEquipe) {
		this.responsavelPesquisaEquipe = responsavelPesquisaEquipe;
	}

	public DominioSituacao getAtivoPesquisaEquipe() {
		return ativoPesquisaEquipe;
	}

	public void setAtivoPesquisaEquipe(DominioSituacao ativoPesquisaEquipe) {
		this.ativoPesquisaEquipe = ativoPesquisaEquipe;
	}

	public DominioSimNao getPlacarRiscoNeonatalPesquisaEquipe() {
		return placarRiscoNeonatalPesquisaEquipe;
	}

	public void setPlacarRiscoNeonatalPesquisaEquipe(
			DominioSimNao placarRiscoNeonatalPesquisaEquipe) {
		this.placarRiscoNeonatalPesquisaEquipe = placarRiscoNeonatalPesquisaEquipe;
	}

	public Integer getCodigoPesquisaEquipe() {
		return codigoPesquisaEquipe;
	}

	public void setCodigoPesquisaEquipe(Integer codigoPesquisaEquipe) {
		this.codigoPesquisaEquipe = codigoPesquisaEquipe;
	}

	public DynamicDataModel<AghEquipes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghEquipes> dataModel) {
		this.dataModel = dataModel;
	}

	public AghEquipes getEquipeSelecionada() {
		return equipeSelecionada;
	}

	public void setEquipeSelecionada(AghEquipes equipeSelecionada) {
		this.equipeSelecionada = equipeSelecionada;
	}
	
}
