package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import javax.inject.Inject;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class LiberarConsultasPorObitoPaginatorController extends
		ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5137939199679346284L;

	private static final Log LOG = LogFactory.getLog(LiberarConsultasPorObitoPaginatorController.class);

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject @Paginator
	private DynamicDataModel<AipPacientes> dataModel;

	// Filtro principal da pesquisa
	private AipPacientes filtro = new AipPacientes();

	private AipPacientes parametroSelecionado;

	private Integer codigoPaciente;
	
	@Override
	public List<AipPacientes> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		return this.ambulatorioFacade.listarConsultasParaLiberarObito(
				firstResult, maxResult, orderProperty, asc, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		return this.ambulatorioFacade
				.listarConsultasParaLiberarObitoCount(this.filtro);
	}

	/**
	 * Pesquisa Motivo de Pendência da Conta passando filtro
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Limpar os campos das tela e remove a grid e botão novo
	 */
	public void limpar() {
		this.filtro = new AipPacientes();
		this.dataModel.limparPesquisa();
	}

	/**
	 * Liberar Consultas por Obito
	 */
	public void liberarPorObito(Integer codPaciente) {
		this.codigoPaciente = codPaciente;
		
		try {
			ambulatorioFacade.liberarConsultaPorObito(codigoPaciente, getEnderecoRedeHostRemoto());
			
			apresentarMsgNegocio(Severity.INFO, "PROCESSO_ENCERRADO");
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		} catch (BaseException e) {
			LOG.error("Exceção caputada:", e);
			apresentarExcecaoNegocio(e);
		}
		
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * @return the dataModel
	 */
	public DynamicDataModel<AipPacientes> getDataModel() {
		return dataModel;
	}

	/**
	 * @param dataModel
	 *            the dataModel to set
	 */
	public void setDataModel(DynamicDataModel<AipPacientes> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * @return the filtro
	 */
	public AipPacientes getFiltro() {
		return filtro;
	}

	/**
	 * @param filtro
	 *            the filtro to set
	 */
	public void setFiltro(AipPacientes filtro) {
		this.filtro = filtro;
	}

	/**
	 * @return the parametroSelecionado
	 */
	public AipPacientes getParametroSelecionado() {
		return parametroSelecionado;
	}

	/**
	 * @param parametroSelecionado
	 *            the parametroSelecionado to set
	 */
	public void setParametroSelecionado(AipPacientes parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	protected Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	protected void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

}
