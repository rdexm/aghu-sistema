package br.gov.mec.aghu.paciente.historico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.historico.business.IHistoricoPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class HistoricoPacienteRecuperaExcluidosPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 823873819770768851L;

	@EJB
	private IHistoricoPacienteFacade historicoPacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	private AipPacientesHist historicoPaciente = new AipPacientesHist();
	private Integer codigoPaciente;
	private Integer prontuarioPaciente;
	private String nomePaciente;
	private Boolean operacaoConcluida = false;
	
	@Inject @Paginator
	private DynamicDataModel<AipPacientesHist> dataModel;

	
	@PostConstruct
	public void iniciar() {
		this.begin(conversation);
		
		if (codigoPaciente != null || prontuarioPaciente != null
				|| nomePaciente != null) {

			if (codigoPaciente != null) {
				historicoPaciente.setCodigo(codigoPaciente);
			}

			if (prontuarioPaciente != null) {
				historicoPaciente.setProntuario(prontuarioPaciente);
			}

			if (nomePaciente != null) {
				historicoPaciente.setNome(nomePaciente);
			}

			this.pesquisar();
		}
	}
	
	public void pesquisar() {
		reiniciarPaginator();
	}
	
	/**
	 * Método para limpar campos da tela, grid e botão "Limpar"
	 */
	public void limpar() {
		reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(false);
		this.historicoPaciente = new AipPacientesHist();
	}

	/**
	 * Método chamado na tela de pesquisa quando o usuário clicar no botão
	 * recuperar da grid com a lista de Histórico de Pacientes.
	 * 
	 * @param Código
	 *            do Paciente a ser recuperado
	 */
	public void recuperar() {
		try {

			// Recupera o paciente através do seu histórico
			this.cadastroPacienteFacade.recuperarPaciente(this.codigoPaciente);
			
			reiniciarPaginator();

			// Exibir mensagem de exclusão com sucesso e fecha janela de confirmação
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_RECUPERAR_PACIENTE");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método chamado na tela de confirmação de recuperação de um registro de
	 * histórico de pacienet quando o usuário clicar no botão "Cancelar",
	 * cancelando a operação.
	 */
	public void cancelarModal() {
		this.operacaoConcluida = true;
	}

	@Override
	public Long recuperarCount() {
		return this.historicoPacienteFacade.obterHistoricoPacientesExcluidosCount(
				historicoPaciente.getCodigo(), historicoPaciente
						.getProntuario(), historicoPaciente.getNome());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AipPacientesHist> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		return this.historicoPacienteFacade.pesquisarHistoricoPacientesExcluidos(firstResult,
				maxResult, historicoPaciente.getCodigo(), historicoPaciente
						.getProntuario(), historicoPaciente.getNome());
	}
	
	private void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
	}

	public AipPacientesHist getHistoricoPaciente() {
		return historicoPaciente;
	}

	public void setHistoricoPaciente(AipPacientesHist historicoPaciente) {
		this.historicoPaciente = historicoPaciente;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Boolean getOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(Boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public DynamicDataModel<AipPacientesHist> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPacientesHist> dataModel) {
		this.dataModel = dataModel;
	}

}
