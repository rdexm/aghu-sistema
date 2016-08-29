package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalServidorDAO;
import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioStatusTicket;
import br.gov.mec.aghu.dominio.DominioTipoTicket;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmStatusTicket;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.PtmTicketJn;
import br.gov.mec.aghu.model.PtmUserTicket;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmStatusTicketDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTecnicoItemRecebimentoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTicketDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTicketJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmUserTicketDAO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoPendenteVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.patrimonio.vo.ResponsavelAceiteTecnicoPendenteVO;
import br.gov.mec.aghu.patrimonio.vo.TicketsVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de negócio associadas à entidade PtmTicket.
 *
 */
@Stateless
public class PtmTicketRN extends BaseBusiness implements Serializable {

	private static final String STRING_2_BARRA = "\\";
	private static final String STRING_BARRA = "/";
	private static final String STATUS = "STATUS: ";
	private static final long serialVersionUID = -4992418494152489931L;
	private static final Log LOG = LogFactory.getLog(PtmTicketRN.class);
	@Inject
	private PtmStatusTicketDAO ptmStatusTicketDAO;
	@Inject
	private PtmTicketDAO ptmTicketDAO;
	@Inject
	private PtmUserTicketDAO ptmUserTicketDAO; 
	@Inject
	private PtmAreaTecAvaliacaoDAO ptmAreaTecAvaliacaoDAO;
	@Inject
	FccCentroCustosDAO fccCentroCustosDAO;
	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO;
	@Inject
	private PtmTecnicoItemRecebimentoDAO ptmTecnicoItemRecebimentoDAO; 
	@Inject
	private EmailUtil emailUtil;
	@Inject
	private PtmTicketJnDAO ptmTicketJnDAO;
	@Inject
	private AghCaixaPostalServidorDAO aghCaixaPostalServidorDAO;
	@EJB
	private PtmAreaTecAvaliacaoRN ptmAreaTecAvaliacaoRN;
	@EJB
	private AnaliseTecnicaBemPermanenteON analiseTecnicaBemPermanenteON;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private ISchedulerFacade schedulerFacade;
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public List<TicketsVO> carregarTicketsItemRecebimentoProvisorio(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<TicketsVO> listaStatusRecebimento = ptmTicketDAO.carregarTicketsItemRecebimentoProvisorio(itemRecebimento, firstResult, maxResult, orderProperty, asc);
			for (TicketsVO ticketsVO : listaStatusRecebimento) {
				if (ticketsVO.getSeq() != null){
					ticketsVO.getListaResponsaveis().addAll(ptmStatusTicketDAO.obterResponsaveisStatusDoTicket(ticketsVO.getSeq()));				
				}
			}
		 return listaStatusRecebimento;
	}
	
	public Long carregarTicketsItemRecebimentoProvisorioCount(ItemRecebimentoVO itemRecebimento) {
		return ptmTicketDAO.carregarTicketsItemRecebimentoProvisorioCount(itemRecebimento);
	}
	
	/**
	 * Cria tickets de avaliação técnica para cada item informado, associando-os ao Chefe da área técnica.
	 * (RN01, Situação 1, estória #44286)
	 * 
	 * @param caixaPostal - Informações de pendência
	 * @param itens - Itens de recebimento
	 * @param chefe - Chefe da área técnica
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public void criarTicketAvaliacaoSemTecnicoPadrao(AghCaixaPostal caixaPostal, List<PtmItemRecebProvisorios> itens, RapServidores chefe, Integer numeroTicket) throws ApplicationBusinessException {
		criarTicketAguardandoAtendimento(numeroTicket, caixaPostal, itens, chefe);
	}

	public void criarTicketAvaliacaoSemTecnicoPadrao(AghCaixaPostal caixaPostal, List<PtmItemRecebProvisorios> itens, RapServidores chefe, PtmTicket ticket) throws ApplicationBusinessException {
		criarTicketAguardandoAtendimento(caixaPostal, itens, chefe, ticket);
	}
	
	/**
	 * Método refatorado para utilização na melhoria #50613
	 * RN10 da estória #43788 - Foi adaptado para a RN11 da 44297
	 * @param caixaPostal
	 * @param itens
	 * @param chefe
	 * @param numero do Ticket
	 * @throws ApplicationBusinessException
	 */
	public void criarTicketAguardandoAtendimento(Integer numTicket, AghCaixaPostal caixaPostal, List<PtmItemRecebProvisorios> itens, RapServidores servidor) throws ApplicationBusinessException {
		PtmTicket ticket;
		for (PtmItemRecebProvisorios item : itens) {
			if(numTicket == null){
				ticket = new PtmTicket();
			}else{
				ticket = ptmTicketDAO.obterPorChavePrimaria(numTicket);
			}
			ticket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
			ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
					parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
			ticket.setDataCriacao(new Date());
			ticket.setItemRecebProvisorios(item);
			ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
			if(ticket.getSeq() != null){
				ptmTicketDAO.atualizar(ticket);
			}else{
				ptmTicketDAO.persistir(ticket);
			}
			PtmStatusTicket statusTicket = new PtmStatusTicket();
			statusTicket.setTicket(ticket);
			statusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			statusTicket.setDataCriacao(new Date());
			ptmStatusTicketDAO.persistir(statusTicket);
			PtmUserTicket userTicket = new PtmUserTicket();
			userTicket.setServidor(servidor);
			userTicket.setStatusTicket(statusTicket);
			userTicket.setCaixaPostal(caixaPostal);
			ptmUserTicketDAO.persistir(userTicket);
		}
	}
	
	/**
	 * Método refatorado para utilização na melhoria #50613
	 * RN10 da estória #43788
	 * @param caixaPostal
	 * @param itens
	 * @param chefe
	 * @throws ApplicationBusinessException
	 */
	public void criarTicketAguardandoAtendimento(AghCaixaPostal caixaPostal, List<PtmItemRecebProvisorios> itens, RapServidores servidor, PtmTicket ticket) throws ApplicationBusinessException {
		for (PtmItemRecebProvisorios item : itens) {
			ticket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
			ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
					parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
			ticket.setDataCriacao(new Date());
			ticket.setItemRecebProvisorios(item);
			ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
			ptmTicketDAO.persistir(ticket);
			PtmStatusTicket statusTicket = new PtmStatusTicket();
			statusTicket.setTicket(ticket);
			statusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			statusTicket.setDataCriacao(new Date());
			ptmStatusTicketDAO.persistir(statusTicket);
			PtmUserTicket userTicket = new PtmUserTicket();
			userTicket.setServidor(servidor);
			userTicket.setStatusTicket(statusTicket);
			userTicket.setCaixaPostal(caixaPostal);
			ptmUserTicketDAO.persistir(userTicket);
		}
	}
	
	/**
	 * Atualiza tickets de avaliação técnica para cada item informado, associando-os ao Chefe da área técnica.
	 * (RN07, estória #43449, melhoria #47225)
	 * @param caixaPostal - Informações de pendência
	 * @param itens - Itens de recebimento
	 * @param chefe - Chefe da área técnica
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarTicketAvaliacaoSemTecnicoPadrao(AghCaixaPostal caixaPostal, List<PtmTicket> itens, RapServidores chefe) throws ApplicationBusinessException {
		for (PtmTicket ticket : itens) {
			ticket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
			ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
					parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
			ticket.setDataAlteradoEm(new Date());
			ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
			ptmTicketDAO.atualizar(ticket);
			PtmStatusTicket statusTicket = new PtmStatusTicket();
			statusTicket.setTicket(ticket);
			statusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			statusTicket.setDataCriacao(new Date());
			ptmStatusTicketDAO.persistir(statusTicket);
			PtmUserTicket userTicket = new PtmUserTicket();
			userTicket.setServidor(chefe);
			userTicket.setStatusTicket(statusTicket);
			userTicket.setCaixaPostal(caixaPostal);
			ptmUserTicketDAO.persistir(userTicket);
			//estória #43449 - melhoria #47225 - RN09
			inserirJournalPtmTicket(ticket, DominioOperacoesJournal.UPD);
			List<PtmStatusTicket> statusTicketList = new ArrayList<PtmStatusTicket>();
			statusTicketList = ptmStatusTicketDAO.obterListaStatusDoTicket(ticket);
			//estória #43449 - melhoria #47225 - RN14
			excluirPendenciasAnteriores(statusTicketList, caixaPostal);
			vincularCaixaPostalServidorComCaixaPostal(caixaPostal);
		}
	}
	/**
	 * Atualiza tickets de avaliação técnica para cada item informado, associando-os ao Chefe da área técnica.
	 * (RN07, estória #43449, melhoria #47225)
	 * @param caixaPostal - Informações de pendência
	 * @param itens - Itens de recebimento
	 * @param chefe - Chefe da área técnica
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarTicketAvaliacaoSemTecnicoPadrao2(AghCaixaPostal caixaPostal, List<PtmTicket> itens, RapServidores chefe) throws ApplicationBusinessException {
		for (PtmTicket ticket : itens) {
			ticket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
			ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
					parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
			ticket.setDataAlteradoEm(new Date());
			ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
			ptmTicketDAO.atualizar(ticket);
			PtmStatusTicket statusTicket = new PtmStatusTicket();
			statusTicket.setTicket(ticket);
			statusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			statusTicket.setDataCriacao(new Date());
			ptmStatusTicketDAO.persistir(statusTicket);
			PtmUserTicket userTicket = new PtmUserTicket();
			userTicket.setServidor(chefe);
			userTicket.setStatusTicket(statusTicket);
			userTicket.setCaixaPostal(caixaPostal);
			ptmUserTicketDAO.persistir(userTicket);
			//estória #43449 - melhoria #47225 - RN09
			inserirJournalPtmTicket(ticket, DominioOperacoesJournal.UPD);
			List<PtmStatusTicket> statusTicketList = new ArrayList<PtmStatusTicket>();
			statusTicketList = ptmStatusTicketDAO.obterListaStatusDoTicket(ticket);
			//estória #43449 - melhoria #47225 - RN14
			excluirPendenciasAnteriores(statusTicketList, caixaPostal);
			vincularCaixaPostalServidorComCaixaPostal(caixaPostal);
		}
	}
	/**
	 * Atualiza tickets de avaliação técnica para cada item informado, associando-os ao Chefe da área técnica.
	 * (RN13, estória #43449, melhoria #47225)
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarTicketAvaliacaoComResponsavelTecnico(AghCaixaPostal caixaPostal, List<PtmTicket> itens, RapServidores chefe) throws ApplicationBusinessException {
		for (PtmTicket ticket : itens) {
			ticket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
			ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
					parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
			ticket.setDataAlteradoEm(new Date());
			ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
			ptmTicketDAO.atualizar(ticket);
			PtmStatusTicket statusTicket = new PtmStatusTicket();
			statusTicket.setTicket(ticket);
			statusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			statusTicket.setDataCriacao(new Date());
			ptmStatusTicketDAO.persistir(statusTicket);
			PtmUserTicket userTicket = new PtmUserTicket();
			userTicket.setServidor(chefe);
			userTicket.setStatusTicket(statusTicket);
			userTicket.setCaixaPostal(caixaPostal);
			ptmUserTicketDAO.persistir(userTicket);
			//estória #43449 - melhoria #47225 - RN09
			inserirJournalPtmTicket(ticket, DominioOperacoesJournal.UPD);
			List<PtmStatusTicket> statusTicketList = new ArrayList<PtmStatusTicket>();
			statusTicketList = ptmStatusTicketDAO.obterListaStatusDoTicket(ticket);
			//estória #43449 - melhoria #47225 - RN14
			excluirPendenciasAnteriores(statusTicketList, caixaPostal);
			vincularCaixaPostalServidorComCaixaPostal(caixaPostal);
		}
	}
	/**
	 * Cria tickets de avaliação técnica para cada item informado, associando-os ao técnico padrão da área técnica.
	 * (RN01, Situação 2, estória #44286)
	 * @param caixaPostalTecnico - Informações de pendência enviada ao técnico
	 * @param itens - Itens de recebimento
	 * @param tecnico - Técnico padrão da área técnica
	 * @throws ApplicationBusinessException 
	 */
	public void criarTicketAvaliacaoComTecnicoPadrao(AghCaixaPostal caixaPostalTecnico, List<PtmItemRecebProvisorios> itens, RapServidores tecnico)
			throws ApplicationBusinessException {
		criarTicketAguardandoAtendimento(null, caixaPostalTecnico, itens, tecnico);
	}
	/**
	 * Atualizar tickets de avaliação técnica para cada item informado, associando-os ao técnico padrão da área técnica.
	 * (RN011,  estória #47225)
	 * @param caixaPostalTecnico - Informações de pendência enviada ao técnico
	 * @param itens - Itens de recebimento
	 * @param tecnico - Técnico padrão da área técnica
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarTicketAvaliacaoComTecnicoPadrao(AghCaixaPostal caixaPostalTecnico, List<PtmTicket> itens, RapServidores tecnico)
			throws ApplicationBusinessException {
		for (PtmTicket ticket : itens) {
			ticket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
			ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
					parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
			ticket.setDataAlteradoEm(new Date());
			ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
			ptmTicketDAO.atualizar(ticket);
			PtmStatusTicket statusTicket = new PtmStatusTicket();
			statusTicket.setTicket(ticket);
			statusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
			statusTicket.setDataCriacao(new Date());
			ptmStatusTicketDAO.persistir(statusTicket);
			PtmUserTicket userTicketTecnico = new PtmUserTicket();
			userTicketTecnico.setServidor(tecnico);
			userTicketTecnico.setStatusTicket(statusTicket);
			userTicketTecnico.setCaixaPostal(caixaPostalTecnico);
			ptmUserTicketDAO.persistir(userTicketTecnico);
			//estória #43449 - melhoria #47225 - RN09
			inserirJournalPtmTicket(ticket, DominioOperacoesJournal.UPD);
			List<PtmStatusTicket> statusTicketList = new ArrayList<PtmStatusTicket>();
			statusTicketList = ptmStatusTicketDAO.obterListaStatusDoTicket(ticket);
			//estória #43449 - melhoria #47225 - RN14
			excluirPendenciasAnteriores(statusTicketList, caixaPostalTecnico);
			vincularCaixaPostalServidorComCaixaPostal(caixaPostalTecnico);
		}
	}
	/**
	 * @param caixaPostal
	 */
	private void vincularCaixaPostalServidorComCaixaPostal(
			AghCaixaPostal caixaPostal) {
		List<AghCaixaPostalServidor> caixaPostalServidorVinculadas = new ArrayList<AghCaixaPostalServidor>();
//		caixaPostalServidorVinculadas = getAghCaixaPostalServidorDAO().pesquisarAghCaixaPostalServidorVinculadasCaixaPostal(caixaPostal);
		for (AghCaixaPostalServidor aghCaixaPostalServidor : caixaPostalServidorVinculadas) {
			aghCaixaPostalServidor.setCaixaPostal(caixaPostal);
			aghCaixaPostalServidorDAO.atualizar(aghCaixaPostalServidor);
		}
	}
	/**
	 * Atualiza um ticket, encaminhando para os técnicos informados.
	 * (RN01, Situações 3 e 4, estória #44286)
	 * @param numeroTicket - Número do Ticket de Avaliação Técnica
	 * @param tecnicos - Técnicos para os quais o ticket será encaminhado
	 * @param caixasPostais - Pendências criadas para os usuários aos quais o ticket foi encaminhado
	 * @throws ApplicationBusinessException 
	 */
	public void encaminharTicketParaTecnico(Integer numeroTicket, List<RapServidores> tecnicos, List<AghCaixaPostal> caixasPostais)
			throws ApplicationBusinessException {
		PtmTicket ticket = ptmTicketDAO.obterPorChavePrimaria(numeroTicket);
		ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
		ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
				parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		ticket.setDataAlteradoEm(new Date());
		ticket.setServidorAlteracao(servidorLogadoFacade.obterServidorLogado());
		ptmTicketDAO.atualizar(ticket);
		List<PtmStatusTicket> statusTicketList = ptmStatusTicketDAO.obterListaStatusDoTicket(ticket);
		PtmStatusTicket novoStatusTicket = new PtmStatusTicket();
		novoStatusTicket.setTicket(ticket);
		novoStatusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
		novoStatusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(novoStatusTicket); 
		for (int index = 0; index < tecnicos.size(); index++) {
			RapServidores tecnico = tecnicos.get(index);
			PtmUserTicket userTicket = new PtmUserTicket();
			AghCaixaPostal caixaPostal = null;
			if (caixasPostais != null && index < caixasPostais.size()) {
				caixaPostal = caixasPostais.get(index);
			}
			userTicket.setServidor(servidorLogadoFacade.obterServidorPorChavePrimaria(tecnico.getId().getMatricula(), tecnico.getId().getVinCodigo()));
			userTicket.setStatusTicket(novoStatusTicket);
			userTicket.setCaixaPostal(caixaPostal);
			ptmUserTicketDAO.persistir(userTicket);
			excluirPendenciasAnteriores(statusTicketList, caixaPostal);
		}
	}
	private void excluirPendenciasAnteriores(List<PtmStatusTicket> statusTicketList, AghCaixaPostal caixaPostal) {
		//List<PtmStatusTicket> statusTicketList = ptmStatusTicketDAO.obterListaStatusDoTicket(ticket);
		for (int i = 0; i < statusTicketList.size(); i++) {
			// Excluindo pendências anteriores.
			if(statusTicketList.get(i).getUserTickets() != null){
				for (PtmUserTicket oldUserTicket : statusTicketList.get(i).getUserTickets()) {
					if (oldUserTicket.getCaixaPostal() != null && !oldUserTicket.getCaixaPostal().equals(caixaPostal) ) {
						AghCaixaPostalServidorId idPendencia = new AghCaixaPostalServidorId(oldUserTicket.getCaixaPostal().getSeq(), oldUserTicket.getServidor());
						aghuFacade.excluirMensagemCxPostServidor(idPendencia);
					}
				}
			}
		}
	}
	/**
	 * Atualiza um ticket que não foi encaminhado para um usuário, atualizando seu status para Expirado,
	 * e encaminhando-o centro de custo imediatamente superior ao centro de custo atual.
	 * (RN01, Situação 5, estória #44286)- Foi Atualizado para a RN11 da 44297 - não atende mais a RN01 da situação 5
	 * @param numeroTicket - Número do Ticket de Avaliação Técnica
	 * @param caixaPostal - Pendência criada para o superior
	 * @throws ApplicationBusinessException 
	 */
	public void expirarTicketAvaliacaoNaoEncaminhado(Integer numeroTicket, AghCaixaPostal caixaPostal) throws ApplicationBusinessException {
		PtmTicket ticket = ptmTicketDAO.obterPorChavePrimaria(numeroTicket);
		// Obtendo centro de custo da presidência.
		FccCentroCustos centroCustoPresidencia = centroCustoFacade.obterCentroCustoPorChavePrimaria(parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PRESIDENCIA));
		RapServidores superior = obterSuperiorCentroCusto(ticket.getItemRecebProvisorios().getAreaTecnicaAvaliacao().getFccCentroCustos(), centroCustoPresidencia);
		ticket.setStatus(DominioStatusTicket.EXPIRADO.getCodigo());
		ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
		ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
				parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		ticket.setDataAlteradoEm(new Date());
		ticket.setServidorAlteracao(servidorLogadoFacade.obterServidorLogado());
		ptmTicketDAO.atualizar(ticket);
		// Obtendo status atual do ticket antes de ser alterado.
		PtmStatusTicket oldStatusTicket = ptmStatusTicketDAO.obterStatusTicketAtual(ticket);
		PtmStatusTicket statusTicket = new PtmStatusTicket();
		statusTicket.setTicket(ticket);
		statusTicket.setStatus(DominioStatusTicket.EXPIRADO.getCodigo());
		statusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(statusTicket);
		if(superior != null && superior.getId() != null){
			for (PtmUserTicket oldUserTicket : oldStatusTicket.getUserTickets()) {
				PtmUserTicket userTicket = new PtmUserTicket();
				userTicket.setServidor(superior);
				userTicket.setStatusTicket(statusTicket);
				userTicket.setCaixaPostal(oldUserTicket.getCaixaPostal());
				ptmUserTicketDAO.persistir(userTicket);
			}
		}
		// Excluindo pendências anteriores.
		for (PtmUserTicket oldUserTicket : oldStatusTicket.getUserTickets()) {
			if (oldUserTicket.getCaixaPostal() != null && !oldUserTicket.getCaixaPostal().equals(caixaPostal)) {
				AghCaixaPostalServidorId idPendencia = new AghCaixaPostalServidorId(oldUserTicket.getCaixaPostal().getSeq(), oldUserTicket.getServidor());
				aghuFacade.excluirMensagemCxPostServidor(idPendencia);
			}
		}
		PtmStatusTicket novoStatusTicket = new PtmStatusTicket();
		novoStatusTicket.setTicket(ticket);
		novoStatusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
		novoStatusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(statusTicket);
		List<PtmItemRecebProvisorios> itens = new ArrayList<PtmItemRecebProvisorios>();
		itens.add(ticket.getItemRecebProvisorios());
		criarTicketAvaliacaoSemTecnicoPadrao(caixaPostal, itens, superior, numeroTicket); //RN11 - 44297
		ptmTicketDAO.flush();
	}
	/**
	 * Atualiza um ticket que foi encaminhado para um usuário, atualizando seu status para Expirado,
	 * e encaminhando-o centro de custo imediatamente superior ao centro de custo atual.
	 * (RN01, Situação 6, estória #44286)
	 * @param numeroTicket - Número do Ticket de Avaliação Técnica
	 * @param caixaPostal - Pendência criada para o superior
	 * @throws ApplicationBusinessException 
	 */
	public void expirarTicketAvaliacaoEncaminhado(Integer numeroTicket, AghCaixaPostal caixaPostal) throws ApplicationBusinessException {
		PtmTicket ticket = ptmTicketDAO.obterPorChavePrimaria(numeroTicket);
		// Obtendo centro de custo da presidência.
		FccCentroCustos centroCustoPresidencia = centroCustoFacade.obterCentroCustoPorChavePrimaria(
				parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PRESIDENCIA));
		RapServidores superior = obterSuperiorCentroCusto(ticket.getItemRecebProvisorios().getAreaTecnicaAvaliacao().getFccCentroCustos(), centroCustoPresidencia);
		ticket.setStatus(DominioStatusTicket.EXPIRADO.getCodigo());
		ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
		ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
				parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		ticket.setDataAlteradoEm(new Date());
		ticket.setServidorAlteracao(servidorLogadoFacade.obterServidorLogado());
		ptmTicketDAO.atualizar(ticket);
		// Obtendo status atual do ticket antes de ser alterado.
		PtmStatusTicket oldStatusTicket = ptmStatusTicketDAO.obterStatusTicketAtual(ticket);
		PtmStatusTicket statusTicket = new PtmStatusTicket();
		statusTicket.setTicket(ticket);
		statusTicket.setStatus(DominioStatusTicket.EXPIRADO.getCodigo());
		statusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(statusTicket);
		PtmUserTicket userTicket = new PtmUserTicket();
		userTicket.setServidor(superior);
		userTicket.setStatusTicket(statusTicket);
		userTicket.setCaixaPostal(caixaPostal);
		ptmUserTicketDAO.persistir(userTicket);
		// Excluindo pendências anteriores.
		for (PtmUserTicket oldUserTicket : oldStatusTicket.getUserTickets()) {
			if (oldUserTicket.getCaixaPostal() != null && !oldUserTicket.getCaixaPostal().equals(caixaPostal)) {
				AghCaixaPostalServidorId idPendencia = new AghCaixaPostalServidorId(oldUserTicket.getCaixaPostal().getSeq(), oldUserTicket.getServidor());
				aghuFacade.excluirMensagemCxPostServidor(idPendencia);
			}
		}
		PtmStatusTicket novoStatusTicket = new PtmStatusTicket();
		novoStatusTicket.setTicket(ticket);
		novoStatusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
		novoStatusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(novoStatusTicket);
		PtmUserTicket novoUserTicket = new PtmUserTicket();
		novoUserTicket.setServidor(superior);
		novoUserTicket.setStatusTicket(novoStatusTicket);
		novoUserTicket.setCaixaPostal(caixaPostal);
		ptmUserTicketDAO.persistir(novoUserTicket);
	}

	/**
	 * Obtem o superior imediato para o centro de custo informado.
	 * @param centroCusto - Centro de custo a ser verificado
	 * @param centroCustoPresidencia - Centro de custo da presidência
	 * @return Superior do centro de custo informado.
	 */
	private RapServidores obterSuperiorCentroCusto(FccCentroCustos centroCusto, FccCentroCustos centroCustoPresidencia) {
		centroCusto = fccCentroCustosDAO.obterOriginal(centroCusto);
		if (centroCusto.getCentroCusto() != null && !centroCusto.getCentroCusto().equals(centroCustoPresidencia)) {
			FccCentroCustos centroCustoSuperior = fccCentroCustosDAO.obterOriginal(centroCusto.getCentroCusto());
			if (centroCustoSuperior.getRapServidor() == null) {
				return obterSuperiorCentroCusto(centroCustoSuperior, centroCustoPresidencia);
			}
			return centroCustoSuperior.getRapServidor();
		}
		return null;
	}
	/**
	 * Atualiza um ticket, alterando seu status para Em Atendimento, e atribuindo-o a um usuário.
	 * (RN02, estória #44286)
	 * @param numeroTicket - Número do Ticket de Avaliação
	 * @param caixaPostal - Pendência criada para o usuário trabalhando no ticket
	 * @param tecnico - Técnico responsável pelo ticket
	 */
	public void assumirTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico) {
		atualizarTicketAvaliacao(numeroTicket, caixaPostal, tecnico);
	}

	/**
	 * Melhoria #50612
	 * Método refatorado para atualizar ticket.
	 * RN08 da #45707
	 */
	private void atualizarTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico) {
		PtmTicket ticket = ptmTicketDAO.obterPorChavePrimaria(numeroTicket);
		this.inserirJournalPtmTicket(ticket, DominioOperacoesJournal.UPD);
		ticket.setStatus(DominioStatusTicket.EM_ATENDIMENTO.getCodigo());
		ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
		ticket.setDataValidade(null);
		ticket.setDataAlteradoEm(new Date());
		ticket.setServidorAlteracao(servidorLogadoFacade.obterServidorLogado());
		ptmTicketDAO.atualizar(ticket);

		// Obtendo status atual do ticket antes de ser alterado.
		PtmStatusTicket oldStatusTicket = ptmStatusTicketDAO.obterStatusTicketAtual(ticket);
		
		atribuirTicket(ticket, caixaPostal, tecnico, DominioStatusTicket.EM_ATENDIMENTO.getCodigo());

		// Excluindo pendências anteriores.
		if(oldStatusTicket != null){
			for (PtmUserTicket oldUserTicket : oldStatusTicket.getUserTickets()) {
				if (oldUserTicket.getCaixaPostal() != null && !oldUserTicket.getCaixaPostal().equals(caixaPostal)) {
					AghCaixaPostalServidorId idPendencia = new AghCaixaPostalServidorId(oldUserTicket.getCaixaPostal().getSeq(), oldUserTicket.getServidor());
					aghuFacade.excluirMensagemCxPostServidor(idPendencia);
				}
			}
		}
	}

	/**
	 * 
	 * @param ticket - Número do Ticket de Avaliação
	 * @param caixaPostal - Pendência criada para o usuário trabalhando no ticket
	 * @param tecnico - Técnico responsável pelo ticket
	 */
	private void atribuirTicket(PtmTicket ticket, AghCaixaPostal caixaPostal, RapServidores tecnico, Integer codDominioStatusTicket) {
		PtmStatusTicket statusTicket = new PtmStatusTicket();
		statusTicket.setTicket(ticket);
		statusTicket.setStatus(codDominioStatusTicket);
		statusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(statusTicket);
		PtmUserTicket userTicket = new PtmUserTicket();
		userTicket.setServidor(tecnico);
		userTicket.setStatusTicket(statusTicket);
		userTicket.setCaixaPostal(caixaPostal);
		ptmUserTicketDAO.persistir(userTicket);
	}
	
	/**
	 * Cria um novo status_ticket e atribui a um usuário.
	 * (RN16, estória #43443)
	 * 
	 * @param numeroTicket
	 * @param caixaPostal
	 * @param tecnico
	 */
	public void atribuirTicketNovoResponsavel(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores novoResponsavel) {
		PtmTicket ticket = ptmTicketDAO.obterPorChavePrimaria(numeroTicket);
		PtmStatusTicket statusTicket = ptmStatusTicketDAO.obterStatusTicketAtual(ticket);
		
		atribuirTicket(ticket, caixaPostal, novoResponsavel, statusTicket.getStatus());
//		ptmUserTicketDAO.flush();
	}

	/**
	 * Atualiza um ticket, alterando seu status para Concluido.
	 * 
	 * @param numeroTicket - Número do Ticket de Avaliação
	 * @param caixaPostal - Pendência criada para o usuário trabalhando no ticket
	 * @param tecnico - Técnico responsável pelo ticket
	 */
	public void concluirTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico) {

		PtmTicket ticket = ptmTicketDAO.obterPorChavePrimaria(numeroTicket);

		ticket.setStatus(DominioStatusTicket.CONCLUIDO.getCodigo());
		ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
		ticket.setDataValidade(null);
		ticket.setDataAlteradoEm(new Date());
		ticket.setServidorAlteracao(servidorLogadoFacade.obterServidorLogado());
		ptmTicketDAO.atualizar(ticket);
		// Obtendo status atual do ticket antes de ser alterado.
		PtmStatusTicket oldStatusTicket = ptmStatusTicketDAO.obterStatusTicketAtual(ticket);
		PtmStatusTicket statusTicket = new PtmStatusTicket();
		statusTicket.setTicket(ticket);
		statusTicket.setStatus(DominioStatusTicket.CONCLUIDO.getCodigo());
		statusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(statusTicket);
		PtmUserTicket userTicket = new PtmUserTicket();
		userTicket.setServidor(tecnico);
		userTicket.setStatusTicket(statusTicket);
		userTicket.setCaixaPostal(caixaPostal);
		ptmUserTicketDAO.persistir(userTicket);
		// Excluindo pendências anteriores.
		for (PtmUserTicket oldUserTicket : oldStatusTicket.getUserTickets()) {
			if (oldUserTicket.getCaixaPostal() != null && !oldUserTicket.getCaixaPostal().equals(caixaPostal)) {
				AghCaixaPostalServidorId idPendencia = new AghCaixaPostalServidorId(oldUserTicket.getCaixaPostal().getSeq(), oldUserTicket.getServidor());
				aghuFacade.excluirMensagemCxPostServidor(idPendencia);
			}
		}
	}
	/**
	 * Atualiza um ticket, alterando seu status para Cancelado.
	 * @param numeroTicket - Número do Ticket de Avaliação
	 * @param caixaPostal - Pendência criada para o usuário trabalhando no ticket
	 * @param tecnico - Técnico responsável pelo ticket
	 */
	public void cancelarTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico) {
		PtmTicket ticket = ptmTicketDAO.obterPorChavePrimaria(numeroTicket);
		this.inserirJournalPtmTicket(ticket, DominioOperacoesJournal.UPD);
		ticket.setStatus(DominioStatusTicket.CANCELADO.getCodigo());
		ticket.setTipo(DominioTipoTicket.ANALISE_TECNICA.getCodigo());
		ticket.setDataValidade(null);
		ticket.setDataAlteradoEm(new Date());
		ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
		ptmTicketDAO.atualizar(ticket);
		// Obtendo status atual do ticket antes de ser alterado.
		PtmStatusTicket oldStatusTicket = ptmStatusTicketDAO.obterStatusTicketAtual(ticket);
		PtmStatusTicket statusTicket = new PtmStatusTicket();
		statusTicket.setTicket(ticket);
		statusTicket.setStatus(DominioStatusTicket.CANCELADO.getCodigo());
		statusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(statusTicket);
		PtmUserTicket userTicket = new PtmUserTicket();
		userTicket.setServidor(tecnico);
		userTicket.setStatusTicket(statusTicket);
		userTicket.setCaixaPostal(caixaPostal);
		ptmUserTicketDAO.persistir(userTicket);
		// Excluindo pendências anteriores.
		for (PtmUserTicket oldUserTicket : oldStatusTicket.getUserTickets()) {
			if (oldUserTicket.getCaixaPostal() != null && !oldUserTicket.getCaixaPostal().equals(caixaPostal)) {
				AghCaixaPostalServidorId idPendencia = new AghCaixaPostalServidorId(oldUserTicket.getCaixaPostal().getSeq(), oldUserTicket.getServidor());
				aghuFacade.excluirMensagemCxPostServidor(idPendencia);
			}
		}
	}
	/**
	 * Método principal que executa todas as RN's da estória 44297.
	 * RN01 da estória #44297
	 * @throws ApplicationBusinessException 
	 */
	public void enviarPendenciaEmailTicket(AghJobDetail job) throws ApplicationBusinessException {
		//RN03
		List<PtmUserTicket> listaUsersTickets = obterTicketEmAbertos();
		if (listaUsersTickets != null && !listaUsersTickets.isEmpty()) {
			//RN04
			atualizarPendenciasReenviadas(listaUsersTickets);
			//RN05
			montarEmailTicketsAbertos(listaUsersTickets, job);
			//RN06
			obterTicketsStatusAguardandoAtendimento();
		}
	}
	
	/**
	 * Obtém todos os tickets que estão em abertos.
	 * RN03 da estória #44297
	 * @return lista
	 */
	private List<PtmUserTicket> obterTicketEmAbertos() {
		List<PtmStatusTicket> listaStatusTickets = this.ptmStatusTicketDAO.pesquisarTicketPorStatusAtual();
		List<PtmUserTicket> listaUserTicket =  new ArrayList<PtmUserTicket>(); 
		if(listaStatusTickets != null && !listaStatusTickets.isEmpty()){
			for (PtmStatusTicket ptmStatusTicket : listaStatusTickets) {
				listaUserTicket.addAll(ptmStatusTicket.getUserTickets());
			}
		}
		return listaUserTicket;
	}
	
	/**
	 * Atualiza todas as pendências dos tickets em aberto.
	 * RN04 da estória #44297
	 * @param listaUserTicket
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void atualizarPendenciasReenviadas(List<PtmUserTicket> listaUserTicket) throws ApplicationBusinessException {
		for (PtmUserTicket ptmUserTicket : listaUserTicket) {
			if(ptmUserTicket.getCaixaPostal() != null){
				for (AghCaixaPostalServidor caixaPostalServidor : ptmUserTicket.getCaixaPostal().getCaixaPostalServidores()) {
					if (caixaPostalServidor.getSituacao().equals(DominioSituacaoCxtPostalServidor.E)) {
						caixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N);
						caixaPostalServidor.setDthrExcluida(null);
						caixaPostalServidor.setMotivoExcluida(null);
						
						this.aghuFacade.persistirAghCaixaPostalServidor(caixaPostalServidor);
						
						caixaPostalServidor.getCaixaPostal().setDthrFim(DateUtil.adicionaDias(new Date(),
								this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
	
						this.aghuFacade.persistirAghCaixaPostal(caixaPostalServidor.getCaixaPostal());
					}
				}
			}
		}
	}
	
	/**
	 * RN05 da estória #44297
	 * Envia e-mails para cada ticket em aberto.
	 * @param listaUserTicket
	 * @throws ApplicationBusinessException 
	 */
	private void montarEmailTicketsAbertos(List<PtmUserTicket> listaUserTicket, AghJobDetail job) throws ApplicationBusinessException {
		for (PtmUserTicket ptmUserTicket : listaUserTicket) {
			verificarRN10(ptmUserTicket, job);
		}
	}
	
	//RN10 da estória #44297
	private void verificarRN10(PtmUserTicket ptmUserTicket, AghJobDetail job) throws ApplicationBusinessException{
		AceiteTecnicoPendenteVO statusTicket = ptmTicketDAO.consultarStatusDeTicket(ptmUserTicket.getStatusTicket().getTicket().getSeq());//c7
		if(statusTicket != null){
			List<PtmUserTicket> listaUsers = ptmUserTicketDAO.obterUsuariosVinculadoAoStatusTicket(statusTicket.getSeqPtmStatusTicket());//c8
			for (PtmUserTicket ptmUser : listaUsers) {
				if(!ptmUser.getServidor().getId().getMatricula().equals(statusTicket.getMatricula())   && 
						!ptmUser.getServidor().getId().getVinCodigo().equals(statusTicket.getVinCodigo())){
					enviarEmailTickets(ptmUserTicket.getStatusTicket().getTicket(), ptmUser.getServidor());
					schedulerFacade.adicionarLog(job, "Email enviado para "+ptmUser.getServidor().getUsuario() + this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL)+" do ticket de seq "+ptmUser.getStatusTicket().getTicket().getSeq());
				}
			}
		}
	}
	/**
	 * Monta o e-mail e o envia para o destinatário.
	 * @param ticketNrRecebimento servidor
	 * @throws ApplicationBusinessException
	 */
	private void enviarEmailTickets(PtmTicket ticketNrRecebimento, RapServidores servidor) throws ApplicationBusinessException {
		String destinatario = servidor.getUsuario() + this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
		String remetente = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMAIL_PATRIMONIO);
		String assunto = super.getResourceBundleValue("ASSUNTO_EMAIL_TICKETS_ABERTOS_SERVICO_REPETICAO_NOTIFICACAO",
				ticketNrRecebimento.getItemRecebProvisorios().getSceItemRecebProvisorio().getId().getNrpSeq());
		String conteudo = super.getResourceBundleValue("CONTEUDO_UM_EMAIL_TICKETS_ABERTOS_SERVICO_REPETICAO_NOTIFICACAO");
		String email = MessageFormat.format(conteudo, ticketNrRecebimento.getItemRecebProvisorios().getSceItemRecebProvisorio().getId().getNrpSeq());
		emailUtil.enviaEmail(remetente, destinatario, null, assunto, email);
		LOG.info("Email enviado para "+destinatario+" do ticket de seq "+ticketNrRecebimento.getItemRecebProvisorios().getSceItemRecebProvisorio().getId().getNrpSeq());
	}
	/**
	 * Atualiza todos os tickets com status de aguardando atendimento e envia pendência e e-mail para usuário.
	 * RN06 da estória #44297
	 * 
	 * @throws ApplicationBusinessException 
	 */
	private void obterTicketsStatusAguardandoAtendimento() throws ApplicationBusinessException {
		List<PtmStatusTicket> listaTicketsStatusAguardandoAtend = this.ptmStatusTicketDAO.pesquisarTicketsStatusAguardandoAtendimento();
		if (listaTicketsStatusAguardandoAtend != null && !listaTicketsStatusAguardandoAtend.isEmpty()) {
			for (PtmStatusTicket ptmStatusTicket : listaTicketsStatusAguardandoAtend) {
				if (ptmStatusTicket.getTicket().getItemRecebProvisorios().getAreaTecnicaAvaliacao().getServidorCC() != null) {
					FccCentroCustos centroCustoPresidencia = centroCustoFacade.obterCentroCustoPorChavePrimaria(parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PRESIDENCIA));
					RapServidores respCcSuperior = obterSuperiorCentroCusto(ptmStatusTicket.getTicket().getItemRecebProvisorios().getAreaTecnicaAvaliacao().getFccCentroCustos(), centroCustoPresidencia);
					//RN07 - Criar pendência
					AghCaixaPostal caixaPostal = this.analiseTecnicaBemPermanenteON.adicionarPendenciaChefe(respCcSuperior, new Date());
					//executar situação 5 da RN01 da estória 44286
					expirarTicketAvaliacaoNaoEncaminhado(ptmStatusTicket.getTicket().getSeq(), caixaPostal);
					//RN08
					enviarEmailParaTicketsAtualizadoStatusAguardandoAgendamento(ptmStatusTicket);
				}
			}
		}
		
	}

	/**
	 * Envia e-mail para todos os tickets que estavam com status expirado.
	 * RN08 da estória #44297
	 * @param statusTicket
	 * @throws ApplicationBusinessException 
	 */
	private void enviarEmailParaTicketsAtualizadoStatusAguardandoAgendamento(PtmStatusTicket statusTicket) throws ApplicationBusinessException {

		PtmStatusTicket statusTicketAtualizado = this.ptmStatusTicketDAO.obterStatusTicketAtual(statusTicket.getTicket());

		if (statusTicketAtualizado != null && statusTicketAtualizado.getUserTickets() != null) {
			for (PtmUserTicket userTicket : statusTicketAtualizado.getUserTickets()) {
				enviarEmailTickets(statusTicketAtualizado.getTicket(), userTicket.getServidor());
			}
		}
	}
	
	public void atenderAceiteTecnico(Integer recebimento, Integer itemRecebimento){
		RapServidores usuarioLogado = servidorLogadoFacade.obterServidorLogado();
		
		//Obtém os tickets associados ao item recebimento
		List<PtmTicket> listaTickets = this.ptmTicketDAO.obterTicketPorItemRecebimentoPorServidor(recebimento, itemRecebimento, usuarioLogado.getId());
		
		if(listaTickets != null && !listaTickets.isEmpty()){
			for (PtmTicket ptmTicket : listaTickets) {
				PtmStatusTicket statusTicketAtual = new PtmStatusTicket();
				for (PtmStatusTicket status : ptmTicket.getPtmStatusTicket()) {
					if(statusTicketAtual.getDataCriacao() == null){
						statusTicketAtual = status;
					}else if(DateUtil.validaDataMaior(status.getDataCriacao(), statusTicketAtual.getDataCriacao())){
						statusTicketAtual = status;
					}
				}
				for (PtmUserTicket user : statusTicketAtual.getUserTickets()) {
					if(user.getServidor().equals(usuarioLogado)){
						/**
						 * Melhoria #50612. RN08 estória #45707
						 */
						this.atualizarTicketAvaliacao(ptmTicket.getSeq(), null, usuarioLogado);
						
						//RN07
						this.ptmAreaTecAvaliacaoRN.inserirJournalPtmItemRecebProvisorios(ptmTicket.getItemRecebProvisorios(), DominioOperacoesJournal.UPD);
						//RN05 da estória #45707
						ptmTicket.getItemRecebProvisorios().setStatus(DominioStatusAceiteTecnico.EM_AVALIACAO_TECNICA.getCodigo());
						ptmTicket.getItemRecebProvisorios().setDataUltimaAlteracao(new Date());
						this.ptmItemRecebProvisoriosDAO.atualizar(ptmTicket.getItemRecebProvisorios());
						break;
					}
				}
				PtmTecnicoItemRecebimento ptmTecnicoItemRecebimento = this.ptmTecnicoItemRecebimentoDAO.
						obterPorServidorItemRecebimento(usuarioLogado, recebimento, itemRecebimento);
				ptmTecnicoItemRecebimento.setIndResponsavel(DominioIndResponsavel.R);
				this.ptmTecnicoItemRecebimentoDAO.atualizar(ptmTecnicoItemRecebimento);
			}
		}
	}
	protected void inserirJournalPtmTicket(PtmTicket ptmTicket, DominioOperacoesJournal operacao){
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final PtmTicketJn ptmTicketJn = new PtmTicketJn();
		ptmTicketJn.setNomeUsuario(servidorLogado.getUsuario());
		ptmTicketJn.setOperacao(operacao);
		ptmTicketJn.setDataAlteradoEm(ptmTicket.getDataAlteradoEm());
		ptmTicketJn.setDataCriacao(ptmTicket.getDataCriacao());
		ptmTicketJn.setDataValidade(ptmTicket.getDataValidade());
		ptmTicketJn.setSceItemRecebProvisorio(ptmTicket.getItemRecebProvisorios().getSceItemRecebProvisorio());
		ptmTicketJn.setSeq(ptmTicket.getSeq());
		ptmTicketJn.setServidor(ptmTicket.getServidor());
		ptmTicketJn.setStatus(ptmTicket.getStatus());
		ptmTicketJn.setTipo(ptmTicket.getTipo());
		ptmTicketJnDAO.persistir(ptmTicketJn);
		ptmTicketJnDAO.flush();
	}
	/**
	 * #48321 - RN01
	 * @throws ApplicationBusinessException
	 */
	public void enviarNotificacaoAceitePendente(AghJobDetail job) throws ApplicationBusinessException {
		List<AceiteTecnicoPendenteVO> listaAceitePendente = ptmTicketDAO.obterAceitesPendentes();
		Map<Integer, List<AceiteTecnicoPendenteVO>> aceitesPorArea = new HashMap<Integer, List<AceiteTecnicoPendenteVO>>();
		Set<Integer> listKeys = new HashSet<Integer>();
		List<AceiteTecnicoPendenteVO> listaValores = new ArrayList<AceiteTecnicoPendenteVO>();
		if (listaAceitePendente != null && !listaAceitePendente.isEmpty()) {
			for (AceiteTecnicoPendenteVO aceiteTecnicoPendenteVO : listaAceitePendente) {
				//RN02
				listarResponsaveisPorItemRecebimento(aceiteTecnicoPendenteVO.getSeqPtmStatusTicket(), aceiteTecnicoPendenteVO);
				//RN03
				listarUsuariosPorAceiteTecnico(aceiteTecnicoPendenteVO.getSeqPtmAreaTecAvaliacao(), aceiteTecnicoPendenteVO);
				
				listKeys.add(aceiteTecnicoPendenteVO.getSeqPtmAreaTecAvaliacao());
				
				if(aceitesPorArea.containsKey(aceiteTecnicoPendenteVO.getSeqPtmAreaTecAvaliacao())){
					aceitesPorArea.get(aceiteTecnicoPendenteVO.getSeqPtmAreaTecAvaliacao()).add(aceiteTecnicoPendenteVO);
				}
				else{
					listaValores = new ArrayList<AceiteTecnicoPendenteVO>();
					listaValores.add(aceiteTecnicoPendenteVO);
					aceitesPorArea.put(aceiteTecnicoPendenteVO.getSeqPtmAreaTecAvaliacao(), listaValores);
				}
			}
		}
		agruparAceitesPorStatus(aceitesPorArea, listKeys);
	}
	private void agruparAceitesPorStatus(Map<Integer, List<AceiteTecnicoPendenteVO>> aceitesPorArea, Set<Integer> listKeys) throws ApplicationBusinessException{
		Map<Integer, List<AceiteTecnicoPendenteVO>> aceitesPorStatus = new HashMap<Integer, List<AceiteTecnicoPendenteVO>>();
		Set<Integer> listKeyStatus = new HashSet<Integer>();
		List<AceiteTecnicoPendenteVO> listaAceitesPorStatus = new ArrayList<AceiteTecnicoPendenteVO>();
		for(Integer key : listKeys){
			for (AceiteTecnicoPendenteVO vo : aceitesPorArea.get(key)) {
				listKeyStatus.add(vo.getStatus());
				
				if(aceitesPorStatus.containsKey(vo.getStatus())){
					aceitesPorStatus.get(vo.getStatus()).add(vo);
				}
				else{
					listaAceitesPorStatus = new ArrayList<AceiteTecnicoPendenteVO>();
					listaAceitesPorStatus.add(vo);
					aceitesPorStatus.put(vo.getStatus(), listaAceitesPorStatus);
				}
			}
			montarEmail(aceitesPorStatus, listKeyStatus);
		}
	}
	private void montarEmail(Map<Integer, List<AceiteTecnicoPendenteVO>> aceitesPorStatus, Set<Integer> listStatus) throws ApplicationBusinessException{
		StringBuilder builder = new StringBuilder(2000);
		StringBuilder responsaveis = new StringBuilder(240);
		AceiteTecnicoPendenteVO aceiteTecnicoPendenteVO = null;
		for(Integer key : listStatus){ 
		builder.append(StringUtils.LF+StringUtils.LF+STATUS+DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(key).getDescricao()+StringUtils.LF+StringUtils.LF);
		builder.append(this.getResourceBundleValue("LABEL_RECEB_ITEM_48321")).append(acrescentarEspacos(4));
		builder.append(this.getResourceBundleValue("LABEL_RESPONSAVEL")).append(acrescentarEspacos(78));
		builder.append(this.getResourceBundleValue("LABEL_ULT_ATUALIZACAO")+StringUtils.LF);
			
			for (AceiteTecnicoPendenteVO vo : aceitesPorStatus.get(key)) {
				aceiteTecnicoPendenteVO = vo;
				int countResponsaveis = 0;
				for(ResponsavelAceiteTecnicoPendenteVO responsavel : aceiteTecnicoPendenteVO.getListaResponsaveisItemRecebimento()){
					if (CoreUtil.igual(countResponsaveis, 0)) {
						responsaveis = new StringBuilder();
						builder.append(StringUtils.LF);
						builder.append(aceiteTecnicoPendenteVO.getReceb()).append(STRING_BARRA).append(aceiteTecnicoPendenteVO.getItem());
						builder.append(acrescentarEspacos(-1 * (aceiteTecnicoPendenteVO.getReceb().toString().length() + aceiteTecnicoPendenteVO.getItem().toString().length() - 14)));
						
						responsaveis.append(responsavel.getSeqMatricula()).append(STRING_2_BARRA).append(responsavel.getCodVinculo()+" - "+responsavel.getNomeResponsavel()+";");
						if(responsaveis.length() < 63){
							responsaveis.append(acrescentarEspacos( -1 * (responsaveis.length() - 63)));
						}
						builder.append(responsaveis.toString());
						builder.append(DateUtil.obterDataFormatada(aceiteTecnicoPendenteVO.getDataUltAtualizacao(), "dd/MM/yyyy hh:mm:ss"));

					} else {
						responsaveis = new StringBuilder();
						responsaveis.append(acrescentarEspacos(15) + responsavel.getSeqMatricula()).append(STRING_2_BARRA).append(responsavel.getCodVinculo()+" - "+responsavel.getNomeResponsavel()+";");
						builder.append(responsaveis.toString());
						if (CoreUtil.igual(countResponsaveis, aceiteTecnicoPendenteVO.getListaResponsaveisItemRecebimento().size())) {
							builder.append(StringUtils.LF);							
						}
					}
					builder.append(StringUtils.LF);
					countResponsaveis++;
				}
			}
		}
		enviarEmailAceitePendente(aceiteTecnicoPendenteVO, builder.toString());
	}
	// C2 - Consulta para saber os responsáveis por cada item de recebimento
	private void listarResponsaveisPorItemRecebimento(Integer seqStatusSeq, AceiteTecnicoPendenteVO aceiteTecnicoPendenteVO){
		if(seqStatusSeq != null){
			aceiteTecnicoPendenteVO.getListaResponsaveisItemRecebimento().addAll(
					new HashSet<ResponsavelAceiteTecnicoPendenteVO>(ptmUserTicketDAO.obterResponsaveisPorItemRecebimento(seqStatusSeq)));
		}
	}
	// C3 - Consulta responsável área técnica
	private void listarUsuariosPorAceiteTecnico(Integer seqAreaTecnica, AceiteTecnicoPendenteVO aceiteTecnicoPendenteVO){
		if(seqAreaTecnica != null){
			aceiteTecnicoPendenteVO.setResponsavelAreaTecnica(ptmAreaTecAvaliacaoDAO.obterResponsavelAreaTecnica(seqAreaTecnica));
		}
	}
	//RN 06
	private void enviarEmailAceitePendente(AceiteTecnicoPendenteVO usuario, String corpoEmail) throws ApplicationBusinessException {
		String destinatario = usuario.getResponsavelAreaTecnica().getUsuarioResponsavel() + 
				this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
		String remetente = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMAIL_PATRIMONIO);
		String assunto = super.getResourceBundleValue("ASSUNTO_EMAIL_ACEITE_TECNICO_PENDENTE", usuario.getNomeAreaTecAvaliacao());
		String conteudo = super.getResourceBundleValue("CORPO_EMAIL_ACEITE_TECNICO", usuario.getNomeAreaTecAvaliacao(), corpoEmail);
		String email = MessageFormat.format(conteudo, usuario);

		emailUtil.enviaEmail(remetente, destinatario, null, assunto, email);
		LOG.info("Email enviado para "+destinatario+" do aceite pendente de seq "+usuario.getIrpSeq());
	}	
	private String acrescentarEspacos(Integer numEspacos) {
        StringBuilder retorno = new StringBuilder("");
        for (int i=0;i<numEspacos;i++){
             retorno.append(StringUtils.SPACE);
        }
        return retorno.toString();
	}
	
	/**
	 * Método para atualizar ticket criando status relacionando ao ticket sem relacionar o usuário ao status
	 */
	public PtmStatusTicket atualizarTicketStatusSemUsuario(PtmItemRecebProvisorios irp)
			throws ApplicationBusinessException {
		/**
		 * C11 da melhoria #50614
		 */	
		PtmTicket ticket = this.ptmTicketDAO.obterTicketPorSeqItemRecebProvisorio(irp.getSeq());
		inserirJournalPtmTicket(ticket, DominioOperacoesJournal.UPD);
		ticket.setDataValidade(DateUtil.adicionaDias(new Date(),
				parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		ticket.setDataAlteradoEm(new Date());
		ticket.setServidor(servidorLogadoFacade.obterServidorLogado());
		ptmTicketDAO.atualizar(ticket);
		
		PtmStatusTicket statusTicket = new PtmStatusTicket();
		statusTicket.setTicket(ticket);
		statusTicket.setStatus(DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo());
		statusTicket.setDataCriacao(new Date());
		ptmStatusTicketDAO.persistir(statusTicket);

		return statusTicket;
	}
	
	/**
	 * Melhoria #50614 da estória #43446. Método que cria um usuário e relaciona com o status do ticket criado anteriormente.
	 */
	public void criarUsuarioRelacionadoStatus(PtmStatusTicket statusTicket, AghCaixaPostal caixaPostal, RapServidores servidor){
		PtmUserTicket userTicket = new PtmUserTicket();
		userTicket.setServidor(servidor);
		userTicket.setStatusTicket(statusTicket);
		userTicket.setCaixaPostal(caixaPostal);
		ptmUserTicketDAO.persistir(userTicket);
	}
}
