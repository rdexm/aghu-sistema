package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.UserTicketVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável pelo controle dos componentes, ações e valores da tela Manter Tickets de Avaliação Técnica.
 *
 */
public class ManterTicketsAvaliacaoTecnicaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8659245768697990403L;

	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject @Paginator
	private DynamicDataModel<PtmTicket> dataModel;

	private Integer numeroTicket;

	private Integer matricula;

	private Short vinculo;

	private UserTicketVO userTicketSelecionado;

	private List<UserTicketVO> userTicketsList;

	private List<UserTicketVO> itensSelecionados;
	
	private PtmTicket ticketSelecionado;

	/**
	 * Método responsável por realizar as regras de inicialização da tela.
	 */
	@PostConstruct
	protected void inicializar() {

		this.begin(conversation);

		itensSelecionados = new ArrayList<UserTicketVO>();
		atualizarGrids();
	}

	/**
	 * Atualiza as grids da tela.
	 */
	private void atualizarGrids() {

		this.dataModel.reiniciarPaginator();
		userTicketsList = patrimonioFacade.listarTodosUserTicketVO();
		itensSelecionados = new ArrayList<UserTicketVO>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.core.action.ActionPaginator#recuperarListaPaginada(java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	public List<PtmTicket> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return patrimonioFacade.listarTodosTickets(firstResult, maxResult, orderProperty, asc);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.core.action.ActionPaginator#recuperarCount()
	 */
	@Override
	public Long recuperarCount() {

		return patrimonioFacade.listarTodosTicketsCount();
	}

	/**
	 * Método que realiza a ação do botão Adicionar.
	 */
	public void adicionar() {

		try {
			List<RapServidores> tecnico = new ArrayList<RapServidores>();
			tecnico.add(new RapServidores(new RapServidoresId(matricula, vinculo)));

			patrimonioFacade.encaminharTicketParaTecnico(numeroTicket, tecnico, null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		atualizarGrids();
	}
	
	/**
	 * Método que realiza a ação do botão Aceite Técnico.
	 */
	public void aceitar() {

		for (UserTicketVO user : itensSelecionados) {
			patrimonioFacade.assumirTicketAvaliacao(user.getTicket(), null, servidorLogadoFacade.obterServidorLogado());
		}

		atualizarGrids();
	}
	
	/**
	 * Método que realiza a ação do botão Cancelar Ticket.
	 */
	public void cancelar() {

		for (UserTicketVO user : itensSelecionados) {
			patrimonioFacade.cancelarTicketAvaliacao(user.getTicket(), null, servidorLogadoFacade.obterServidorLogado());
		}
		
		atualizarGrids();
	}
	
	/**
	 * Método que realiza a ação do botão Concluir Ticket.
	 */
	public void concluir() {

		for (UserTicketVO user : itensSelecionados) {
			patrimonioFacade.concluirTicketAvaliacao(user.getTicket(), null, servidorLogadoFacade.obterServidorLogado());
		}
		
		atualizarGrids();
	}

	/**
	 * Método responsável por marcar ou desmarcar um checkbox na grid.
	 * 
	 * @param item - Item selecionado
	 */
	public void checkItem(UserTicketVO item) {

		if (itensSelecionados.contains(item)) {
			itensSelecionados.remove(item);
		} else {
			itensSelecionados.add(item);
		}

		alterarSelecaoNaListaVO();
	}
	
	/**
	 * Método responsável por alterar a lista de itens selecionados na grid.
	 */
	private void alterarSelecaoNaListaVO() {

		for (UserTicketVO vo : userTicketsList) {
			if (itensSelecionados.contains(vo)) {
				vo.setSelecionado(true);
			} else {
				vo.setSelecionado(false);
			}
		}
	}

	// GETTERS e SETTERS

	public Integer getNumeroTicket() {
		return numeroTicket;
	}

	public void setNumeroTicket(Integer numeroTicket) {
		this.numeroTicket = numeroTicket;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public UserTicketVO getUserTicketSelecionado() {
		return userTicketSelecionado;
	}

	public void setUserTicketSelecionado(UserTicketVO userTicketSelecionado) {
		this.userTicketSelecionado = userTicketSelecionado;
	}

	public List<UserTicketVO> getUserTicketsList() {
		return userTicketsList;
	}

	public void setUserTicketsList(List<UserTicketVO> userTicketsList) {
		this.userTicketsList = userTicketsList;
	}

	public DynamicDataModel<PtmTicket> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PtmTicket> dataModel) {
		this.dataModel = dataModel;
	}

	public PtmTicket getTicketSelecionado() {
		return ticketSelecionado;
	}

	public void setTicketSelecionado(PtmTicket ticketSelecionado) {
		this.ticketSelecionado = ticketSelecionado;
	}

}
