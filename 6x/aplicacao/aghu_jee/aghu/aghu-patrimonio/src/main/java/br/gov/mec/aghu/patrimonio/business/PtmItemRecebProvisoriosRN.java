package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioStatusTicket;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnica;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmItemRecebProvisoriosJn;
import br.gov.mec.aghu.model.PtmStatusTicket;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.PtmUserTicket;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmStatusTicketDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTecnicoItemRecebimentoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTicketDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmUserTicketDAO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVOComparator;
import br.gov.mec.aghu.patrimonio.vo.FiltroAceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PtmItemRecebProvisoriosRN extends BaseBusiness implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2602933337224550351L;
	
	private static final Log LOG = LogFactory.getLog(PtmItemRecebProvisoriosRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private AnaliseTecnicaBemPermanenteON analiseTecnicaBemPermanenteON;
	
	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;
	
	@Inject
	private PtmAreaTecAvaliacaoDAO ptmAreaTecAvaliacaoDAO;
	
	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO;
	
	@Inject
	private PtmTecnicoItemRecebimentoDAO ptmTecnicoItemRecebimentoDAO;
	
	@Inject
	private PtmUserTicketDAO ptmUserTicketDAO;
	
	@Inject
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;
	
	@Inject
	private PtmStatusTicketDAO ptmStatusTicketDAO;
	
	@Inject
	private PtmTicketDAO ptmTicketDAO;
	
	@Inject
	private PtmItemRecebProvisoriosJnDAO ptmItemRecebProvisoriosJnDAO;
	
	@EJB
	private PtmTicketRN ptmTicketRN;
	
	/**
	 *  Consulta para preencher fieldset “Item de Recebimento”
	 * @param numRecebimento
	 * @param itemRecebimento
	 * @return Set<ItemRecebimentoVO>
	 */
	public List<ItemRecebimentoVO> carregarItemRecebimento(Integer numRecebimento, Integer itemRecebimento){
		
		List<ItemRecebimentoVO> parteUm =  ptmItemRecebProvisoriosDAO.carregarItemRecebimentoPrimeiraParte(numRecebimento, itemRecebimento);		
		List<ItemRecebimentoVO> parteDois =  ptmItemRecebProvisoriosDAO.carregarItemRecebimentoSegundaParte(numRecebimento, itemRecebimento);
		parteUm.addAll(parteDois);
		
		return parteUm;
	}
	
	
	/**
	 * #43464 - Recuperar lista de aceites tecnicos.
	 * @return {@link List} de {@link AceiteTecnicoParaSerRealizadoVO}
	 */
	public List<AceiteTecnicoParaSerRealizadoVO> recuperarListaPaginadaAceiteTecnico(Integer firstResult, 
			Integer maxResult, String orderProperty, boolean asc, FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		List<AceiteTecnicoParaSerRealizadoVO> lista = obterListaAceiteTecnico(filtro);
		
		Comparator<AceiteTecnicoParaSerRealizadoVO> comparador = new AceiteTecnicoParaSerRealizadoVOComparator.OrderByReceb();
		
		if (orderProperty != null && orderProperty != StringUtils.EMPTY) {
			if (orderProperty.equals(AceiteTecnicoParaSerRealizadoVO.Fields.NRO_SOLIC_COMPRAS.toString())) {
				comparador = new AceiteTecnicoParaSerRealizadoVOComparator.OrderByNroSolicCompras();
			} else if (orderProperty.equals(AceiteTecnicoParaSerRealizadoVO.Fields.CODIGO.toString())) {
				comparador = new AceiteTecnicoParaSerRealizadoVOComparator.OrderByCodigo();
			} else if (orderProperty.equals(AceiteTecnicoParaSerRealizadoVO.Fields.AREA_TEC_AVALIACAO.toString())) {
				comparador = new AceiteTecnicoParaSerRealizadoVOComparator.OrderByAreaTecAvaliacao();
			} 
		}
		Collections.sort(lista, comparador);
		if (!asc) {
			Collections.reverse(lista);
		}
		
		if (lista.size() < firstResult + maxResult) {
			return lista.subList(firstResult, lista.size());
		} else {
			return lista.subList(firstResult, firstResult + maxResult);
		}
	}

	/**
	 * #43464 - Recuperar count de aceites tecnicos.
	 * @return Contagem de elementos da lista de {@link AceiteTecnicoParaSerRealizadoVO}
	 */
	public Long recuperarCountAceiteTecnico(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		List<AceiteTecnicoParaSerRealizadoVO> lista = this.obterListaAceiteTecnico(filtro);
		
		if (lista != null && !lista.isEmpty()) {
			return Long.valueOf(lista.size());
		} else {
			return 0l;
		}
	}
	
	/**
	 * Realiza consulta dos aceites tecnicos passando o filtro selecionado. Considera RN04.
	 * @param filtro {@link FiltroAceiteTecnicoParaSerRealizadoVO}
	 * @return {@link List} de {@link AceiteTecnicoParaSerRealizadoVO}
	 */
	private List<AceiteTecnicoParaSerRealizadoVO> obterListaAceiteTecnico(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		boolean usarUnion = true;
		
		if (filtro != null && filtro.getNumeroAf() != null && (filtro.getNumeroRecebimento() == null && filtro.getStatus() == null 
				&& filtro.getAreaTecnicaAvaliacao() == null && filtro.getMaterial() == null && filtro.getResponsavelTecnico() == null)) {
			usarUnion = false;
		}
		
		List<AceiteTecnicoParaSerRealizadoVO> lista = ptmItemRecebProvisoriosDAO.obterListaAceitesTecnicos(filtro);
		
		if (usarUnion) {
			lista.addAll(ptmItemRecebProvisoriosDAO.obterListaAceitesTecnicosUnion(filtro));
		}
		
		return lista;
	}

	/**
	 * #43446 - Inclui todos os tecnicos selecionados, para todos os itens de recebimento selecionados.
	 * @param aceites - {@link List} de {@link AceiteTecnicoParaSerRealizadoVO}
	 * @param toDelete - {@link List} de {@link RapServidores} cujos tecnicos serão removidos
	 * @param toInsert - {@link List} de {@link RapServidores} cujos tecnicos setão incluidos
	 * @param pagamento - valor do pagamento parcial
	 * @param servidor - entidade de {@link RapServidores}
	 */
	public void designarTecnicoResponsavel(List<AceiteTecnicoParaSerRealizadoVO> aceites, 
			List<RapServidores> toDelete, List<RapServidores> toInsert, Integer pagamento, RapServidores servidor) 
			throws ApplicationBusinessException {
		if (aceites != null && !aceites.isEmpty()) {
			for (AceiteTecnicoParaSerRealizadoVO aceite : aceites) {
				PtmItemRecebProvisorios irp = this.ptmItemRecebProvisoriosDAO.obterPorIdItensEstoque(aceite.getRecebimento(), aceite.getItemRecebimento());
				PtmAreaTecAvaliacao ata = this.ptmAreaTecAvaliacaoDAO.obterAreaComServidorPorChavePrimaria(aceite.getAreaTecAvaliacao());
				removerTecnicos(toDelete, aceite, irp);
				adicionarNovosTecnicos(toInsert, servidor, irp, ata, aceite);
				if ((irp.getPagamentoParcial() == null && pagamento != null)
						|| (irp.getPagamentoParcial() != null && pagamento == null)
						|| (irp.getPagamentoParcial() != null && pagamento != null && !irp.getPagamentoParcial().equals(pagamento))
						|| !irp.getStatus().equals(DominioStatusAceiteTecnico.SOLICITADA_AVALIACAO_TECNICA.getCodigo())) {
					atualizarItemRecebProvisorio(pagamento, irp);
				}
			}
		}
	}

	/**
	 * #43446 - RN07 - Remover Tecnicos
	 */
	private void removerTecnicos(List<RapServidores> servidores, AceiteTecnicoParaSerRealizadoVO aceite, PtmItemRecebProvisorios irp) throws ApplicationBusinessException {
		if (servidores != null && !servidores.isEmpty()) {
			for (RapServidores servidor : servidores) {
				PtmTecnicoItemRecebimento tecnico = this.ptmTecnicoItemRecebimentoDAO.obterPorServidorItemRecebimento(
						servidor, aceite.getRecebimento(), aceite.getItemRecebimento());
				if (DominioIndResponsavel.R.equals(tecnico.getIndResponsavel())) {
					resetarItemRecebProvisorio(irp);
				}
				cancelarTickets(tecnico);
				this.ptmTecnicoItemRecebimentoDAO.remover(tecnico);
			}
		}
	}

	/**
	 * #43446 - RN08 - Atualiza o status do item de receb provisorio para RECEBIDO. 
	 */
	private void resetarItemRecebProvisorio(PtmItemRecebProvisorios irp) {
		irp.setStatus(DominioStatusAceiteTecnico.RECEBIDO.getCodigo());
		this.ptmItemRecebProvisoriosDAO.atualizar(irp);
	}

	/**
	 * #43446 - RN09 - Remove pendencia e cancela os tickets.
	 */
	private void cancelarTickets(PtmTecnicoItemRecebimento tecnico) throws ApplicationBusinessException {
		PtmUserTicket userTicket = this.ptmUserTicketDAO.obterPorUsuarioSelecionado(tecnico.getServidorTecnico());
		if (userTicket != null) {
			atualizarStatusTicket(userTicket);
			removerPendencia(userTicket, tecnico.getServidorTecnico());
		}
	}

	/**
	 * Altera o status do ticket e do status ticket para CANCELADO. 
	 */
	private void atualizarStatusTicket(PtmUserTicket userTicket) {
		PtmStatusTicket statusTicket = userTicket.getStatusTicket(); 
		if (statusTicket != null) {
			statusTicket.setStatus(DominioStatusTicket.CANCELADO.getCodigo());
			this.ptmStatusTicketDAO.atualizar(statusTicket);
			PtmTicket ticket = statusTicket.getTicket();
			if (ticket != null) {
				ticket.setStatus(DominioStatusTicket.CANCELADO.getCodigo());
				this.ptmTicketDAO.atualizar(ticket);
			}
		}
	}
	
	/**
	 * Remove a pendencia do sistema. 
	 */
	private void removerPendencia(PtmUserTicket userTicket, RapServidores usuarioSelecionado) 
			throws ApplicationBusinessException {
		AghCaixaPostal caixaPostal = userTicket.getCaixaPostal();
		if (caixaPostal != null) {
			this.centralPendenciaFacade.excluirPendenciaComUsuarioSelecionado(caixaPostal.getSeq(), usuarioSelecionado);
		}
	}
	

	/**
	 * #43446 - RN05 - Adicionar Novos Tecnicos
	 */
	private void adicionarNovosTecnicos(List<RapServidores> tecnicos, RapServidores servidor, PtmItemRecebProvisorios irp, PtmAreaTecAvaliacao ata, AceiteTecnicoParaSerRealizadoVO aceite) 
			throws ApplicationBusinessException {
		if (tecnicos != null && !tecnicos.isEmpty()) {
			PtmStatusTicket statusTicket = this.ptmTicketRN.atualizarTicketStatusSemUsuario(irp);
			for (RapServidores tecnico : tecnicos) {
				persistirTecnicoItemRecebimento(servidor, irp, tecnico);
				List<PtmAreaTecAvaliacao> areas = ptmAreaTecAvaliacaoDAO.pesquisarAreaTecAtivaPorServidor(tecnico);
				analiseTecnicaBemPermanenteON.enviarEmailsAceiteTecnico(tecnico, ata, aceite, 
						irp.getSceItemRecebProvisorio().getProgEntregaItemAf().getId().getIafAfnNumero());
				atribuirPendenciaTicket(irp, ata, tecnico, areas, statusTicket);
			}
		}
	}

	/**
	 * Verificar se técnico escolhido é chefe de área técnica de avaliação.
	 * <p>Executa C10</p>
	 * <p>Se retorno diferente de nulo - executar RN14</p>
	 * <p>Se retorno nulo - executar RN06</p>
	 * 
	 * @param irp
	 * @param ata
	 * @param tecnico
	 * @param areas
	 * @throws ApplicationBusinessException
	 */
	private void atribuirPendenciaTicket(PtmItemRecebProvisorios irp, PtmAreaTecAvaliacao ata, RapServidores tecnico, List<PtmAreaTecAvaliacao> areas, 
			PtmStatusTicket statusTicket)
			throws ApplicationBusinessException {
		if (areas != null && !areas.isEmpty()) {
			//RN14
			this.analiseTecnicaBemPermanenteON.criarPendenciaSemTecnicoPadrao(tecnico, new Date(), statusTicket);
		} else {
			//RN06
			AghCaixaPostal caixaPostal = this.analiseTecnicaBemPermanenteON.criarPendenciaTecnicoPadrao(tecnico, new Date(), ata);
			this.ptmTicketRN.criarUsuarioRelacionadoStatus(statusTicket, caixaPostal, tecnico);
		}
	}

	/**
	 * Persiste um novo tecnico item recebimento. 
	 */
	private void persistirTecnicoItemRecebimento(RapServidores servidor, PtmItemRecebProvisorios irp, RapServidores tecnico) {
		PtmTecnicoItemRecebimento tir = new PtmTecnicoItemRecebimento();
		tir.setPtmItemRecebProvisorio(irp);
		tir.setServidorTecnico(tecnico);
		tir.setServidor(servidor);
		this.ptmTecnicoItemRecebimentoDAO.persistir(tir);
	}
	
	/**
	 * #43446 - RN10 - Atualizar todos os itens de recebimento que tiveram técnicos associados.
	 * RN12 - Percentual Pagamento Parcial.
	 */
	private void atualizarItemRecebProvisorio(Integer pagamento, PtmItemRecebProvisorios irp) {
		inserirItemRecebProvisorioJn(irp, DominioOperacoesJournal.UPD);
		irp.setStatus(DominioStatusAceiteTecnico.SOLICITADA_AVALIACAO_TECNICA.getCodigo());
		irp.setPagamentoParcial(pagamento);
		irp.setServidor(servidorLogadoFacade.obterServidorLogado());
		irp.setDataUltimaAlteracao(new Date());
		this.ptmItemRecebProvisoriosDAO.atualizar(irp);
	}

	/**
	 * Registro das operacoes em PtmItemRecebProvisoriosJN
	 * @param irp - Instancia de {@link PtmItemRecebProvisorios}
	 * @param operacao (insert, update)
	 */
	protected void inserirItemRecebProvisorioJn(PtmItemRecebProvisorios irp, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		
		final PtmItemRecebProvisoriosJn irpJn = new PtmItemRecebProvisoriosJn();
		irpJn.setNomeUsuario(servidorLogado.getUsuario());
		irpJn.setOperacao(operacao);
		irpJn.setAtaSeq(irp.getAtaSeq());
		irpJn.setDataRecebimento(irp.getDataRecebimento());
		irpJn.setDataUltimaAlteracao(irp.getDataUltimaAlteracao());
		irpJn.setPagamentoParcial(irp.getPagamentoParcial());
		irpJn.setSceItemRecebProvisorio(irp.getSceItemRecebProvisorio());
		irpJn.setSeq(irp.getSeq());
		irpJn.setServidor(irp.getServidor());
		irpJn.setServidorTecPadrao(irp.getServidorTecPadrao());
		irpJn.setStatus(irp.getStatus());
		
		this.ptmItemRecebProvisoriosJnDAO.persistir(irpJn);
		this.ptmItemRecebProvisoriosJnDAO.flush();
	}
	
	public void atualizarItemRecebProvAceiteTec(PtmAvaliacaoTecnica avaliacao){
		Integer countStatusA = 0;
		Integer countStatusR = 0;
		if(avaliacao.getItemRecebProvisorio() != null){
			PtmItemRecebProvisorios pirp = ptmItemRecebProvisoriosDAO.obterOriginal(avaliacao.getItemRecebProvisorio().getSeq());
			if(pirp != null){
				List<PtmBemPermanentes> bens = this.ptmBemPermanentesDAO.verificarStatusItemReceb(pirp.getSeq());
				for(PtmBemPermanentes bemPermanente :bens){
					if(DominioAceiteTecnico.A.equals(bemPermanente.getStatusAceiteTecnico())){
						countStatusA++;
					}else if(DominioAceiteTecnico.R.equals(bemPermanente.getStatusAceiteTecnico())){
						countStatusR++;
					}
				}
				if(CoreUtil.igual(countStatusR + countStatusA, bens.size())){
					if(countStatusA == bens.size()){
						pirp.setStatus(10);
						this.ptmItemRecebProvisoriosDAO.merge(pirp);
					}else if(countStatusR == bens.size() || 
							(CoreUtil.maior(countStatusA, 0) && CoreUtil.menor(countStatusA, bens.size())) && 
							(CoreUtil.maior(countStatusR, 0) && CoreUtil.menor(countStatusR, bens.size()))){
						pirp.setStatus(9);
						this.ptmItemRecebProvisoriosDAO.merge(pirp);
					}
				}
			}
		}
	}
	
}