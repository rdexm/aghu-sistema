package br.gov.mec.aghu.compras.autfornecimento.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.vo.PersistenciaProgEntregaItemAfVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAFPVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ScoProgEntregaItemAutorizacaoFornecimentoON extends BaseBusiness {
	
	@EJB
	private ScoSolicitacaoProgramacaoEntregaON scoSolicitacaoProgramacaoEntregaON;
	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;
	@EJB
	private ValidacaoProgEntregaItemAfON validacaoProgEntregaItemAfON;
	
	private static final Log LOG = LogFactory.getLog(ScoProgEntregaItemAutorizacaoFornecimentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	private static final long serialVersionUID = 7276860322721573921L;
	
	public enum ScoProgEntregaItemAutorizacaoFornecimentoONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_EXCLUSAO_ITEMPROG_NAO_PERMITIDA, MENSAGEM_ERRO_PERSISTIR_ITEM_AFP;
	}
	
	class ParcelaItemAutFornecimentoVOPorDataGeracaoSolComprasComparator implements Comparator<ParcelaItemAutFornecimentoVO> {
		@Override
		public int compare(ParcelaItemAutFornecimentoVO vo1, ParcelaItemAutFornecimentoVO vo2) {
			Date dt1 = DateUtil.truncaData(new Date());
			Date dt2 = DateUtil.truncaData(new Date());
			
			if (vo1.getSolicitacaoCompra() != null && vo1.getSolicitacaoCompra().getDtSolicitacao() != null) {
				dt1 = DateUtil.truncaData(vo1.getSolicitacaoCompra().getDtSolicitacao());
			}
			
			if (vo2.getSolicitacaoCompra() != null && vo2.getSolicitacaoCompra().getDtSolicitacao() != null) {
				dt2 = DateUtil.truncaData(vo2.getSolicitacaoCompra().getDtSolicitacao());	
			}
			
			if (vo1.getSolicitacaoServico() != null && vo1.getSolicitacaoServico().getDtSolicitacao() != null) {
				dt1 = DateUtil.truncaData(vo1.getSolicitacaoServico().getDtSolicitacao());
			}
			
			if (vo2.getSolicitacaoServico() != null && vo2.getSolicitacaoServico().getDtSolicitacao() != null) {
				dt2 = DateUtil.truncaData(vo2.getSolicitacaoServico().getDtSolicitacao());
			}
			
			int compDtSolicitacao  = dt1.compareTo(dt2);
			return compDtSolicitacao;
		}
	}
	
	/**
	 * Baseado na lista de parcelas calcula a qtde programada para um item de AF.
	 * 
	 * @param listaParcelas
	 * @return Integer
	 */
	public Integer obterQtdeProgramadaProgEntregaItemAf(
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		Integer qtdeProgramada = 0;

		for (ScoProgEntregaItemAutorizacaoFornecimento parc : listaParcelas) {
			if (parc.getQtde() != null
					&& !Boolean.TRUE.equals(parc.getIndCancelada())) {
				qtdeProgramada += parc.getQtde();
			}
		}

		return qtdeProgramada;
	}

	/**
	 * Baseado na lista de parcelas calcula a qtde efetivada (já entregue) para
	 * um item de AF.
	 * 
	 * @param listaParcelas
	 * @return Integer
	 */
	public Integer obterQtdeEfetivadaProgEntregaItemAf(
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		Integer qtdeEfetivada = 0;

		for (ScoProgEntregaItemAutorizacaoFornecimento parc : listaParcelas) {
			if (parc.getQtdeEntregue() != null
					&& !Boolean.TRUE.equals(parc.getIndCancelada())) {
				qtdeEfetivada += parc.getQtdeEntregue();
			}
		}

		return qtdeEfetivada;
	}

	/**
	 * Baseado na lista de parcelas calcula o valor programado para um item de
	 * AF.
	 * 
	 * @param listaParcelas
	 * @return BigDecimal
	 */
	public BigDecimal obterValorProgramadoProgEntregaItemAf(
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		BigDecimal valorProgramado = BigDecimal.ZERO;

		for (ScoProgEntregaItemAutorizacaoFornecimento parc : listaParcelas) {
			if (parc.getValorTotal() != null
					&& !Boolean.TRUE.equals(parc.getIndCancelada())) {
				valorProgramado = valorProgramado.add(new BigDecimal(parc.getValorTotal()));
			}
		}

		return valorProgramado;
	}

	/**
	 * Baseado na lista de parcelas calcula o valor efetivado (entregue) para um
	 * item de AF.
	 * 
	 * @param listaParcelas
	 * @return BigDecimal
	 */
	public BigDecimal obterValorEfetivadoProgEntregaItemAf(
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		BigDecimal valorEfetivado = BigDecimal.ZERO;

		for (ScoProgEntregaItemAutorizacaoFornecimento parc : listaParcelas) {
			if (parc.getValorEfetivado() != null
					&& !Boolean.TRUE.equals(parc.getIndCancelada())) {
				valorEfetivado = valorEfetivado.add(new BigDecimal(parc.getValorEfetivado()));
			}
		}

		return valorEfetivado;
	}
	
	/**
	 * Remove uma programação de entrega para um item de AF
	 * @param item
	 * @throws BaseException 
	 */
	public void excluirProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento item) throws BaseException {
		ScoProgEntregaItemAutorizacaoFornecimento progEntrega = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(item.getId());
		
		if (Boolean.TRUE.equals(progEntrega.getIndAssinatura())) {
			throw new ApplicationBusinessException(ScoProgEntregaItemAutorizacaoFornecimentoONExceptionCode.MENSAGEM_EXCLUSAO_ITEMPROG_NAO_PERMITIDA);
		}
		
		getScoSolicitacaoProgramacaoEntregaON().excluirListaSolicitacaoProgramacao(item.getId());
		getScoProgEntregaItemAutorizacaoFornecimentoRN().remover(progEntrega);
	}
	
	public void liberarAssinaturaProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento item) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoProgEntregaItemAutorizacaoFornecimento progEntrega = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(item.getId());
		progEntrega.setIndPlanejamento(Boolean.TRUE);
		progEntrega.setRapServidorLibPlanej(servidorLogado);
		progEntrega.setDtLibPlanejamento(new Date());
		getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(progEntrega);
	}
	
	/**
	 * Obtem o maior número de parcela para uma programação de entrega de um item de AF 
	 * @param iafAfnNum
	 * @param iafNumero
	 * @return
	 */
	public Integer obterMaxNumeroParcela(Integer iafAfnNum, Integer iafNumero) {
		Integer max = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroParcela(iafAfnNum, iafNumero);
		
		if (max == null) {
			max = 0;
		}
		return max;
	}
		
	/**
	 * Obtem o valor total da parcela do item da AF para uma SC
	 * @param iafAfnNum
	 * @param iafNumero
	 * @param qtd
	 * @param qtdEntregue
	 * @param valorEfetivado
	 * @return Double
	 */
	public Double obterValorTotal(Integer iafAfnNum, Integer iafNumero, Integer qtd, Integer qtdEntregue, Double valorEfetivado) {
		Double valorTotal = 0.00;
		
		if (valorEfetivado == null) {
			valorEfetivado = 0.00;
		}
		
		ScoItemAutorizacaoForn itemAf = this.getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(iafAfnNum, iafNumero);
		
		if (itemAf != null) {
			valorTotal = valorEfetivado + (( (Integer)CoreUtil.nvl(qtd,0) - (Integer )CoreUtil.nvl(qtdEntregue,0)) * (Double) CoreUtil.nvl(itemAf.getValorUnitario(),0.00));
		}
		
		return valorTotal;
	}
	
	/**
	 * Persiste e retorna uma programação de entrega do item da AF
	 * @param previsaoEntrega
	 * @param valorParcelaAf
	 * @param qtdParcelaAf
	 * @param indPlanejada
	 * @param id
	 * @param tipoSolicitacao
	 * @param novoRegistro
	 * @return ScoProgEntregaItemAutorizacaoFornecimento
	 * @throws ApplicationBusinessException 
	 */
	public ScoProgEntregaItemAutorizacaoFornecimento atualizarProgEntregaItemAutorizacaoFornecimento(Date previsaoEntrega, Double valorParcelaAf, Integer qtdParcelaAf, DominioSimNao indPlanejada,
			ScoProgEntregaItemAutorizacaoFornecimentoId id,	DominioTipoSolicitacao tipoSolicitacao, Boolean novoRegistro) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoProgEntregaItemAutorizacaoFornecimento progEntrega = null;
		
		if (novoRegistro) {
			progEntrega = new ScoProgEntregaItemAutorizacaoFornecimento();
			progEntrega.setId(id);
			progEntrega.setDtGeracao(new Date());
			progEntrega.setDtPrevEntrega(previsaoEntrega);
			progEntrega.setDtEntrega(null);
			progEntrega.setQtde(qtdParcelaAf);
			progEntrega.setQtdeEntregue(0);
			progEntrega.setRapServidor(servidorLogado);
			progEntrega.setRapServidorAlteracao(null);
			progEntrega.setIndPlanejamento(Boolean.FALSE);
			progEntrega.setIndAssinatura(Boolean.FALSE);
			progEntrega.setIndEmpenhada(DominioAfEmpenhada.N);
			progEntrega.setIndEnvioFornecedor(Boolean.FALSE);
			progEntrega.setIndRecalculoAutomatico(Boolean.FALSE);
			progEntrega.setIndRecalculoManual(Boolean.FALSE);
			progEntrega.setIndImpressa(Boolean.FALSE);
			progEntrega.setDtAtualizacao(null);
			progEntrega.setDtAssinatura(null);
			progEntrega.setRapServidorAssinatura(null);
			progEntrega.setDtAlteracao(null);
			progEntrega.setDtLibPlanejamento(null);
			progEntrega.setRapServidorLibPlanej(null);
			progEntrega.setIndCancelada(Boolean.FALSE);
			progEntrega.setDtCancelamento(null);
			progEntrega.setRapServidorCancelamento(null);
			progEntrega.setScoJustificativa(null);
			progEntrega.setDtNecessidadeHcpa(null);
			progEntrega.setIndEfetivada(Boolean.FALSE);
			progEntrega.setIndEntregaImediata(Boolean.FALSE);
			progEntrega.setObservacao(null);
			progEntrega.setQtdeEntregueProv(0);
			progEntrega.setValorEfetivado(0.00);
			progEntrega.setQtdeEntregueAMais(0);
			progEntrega.setIndTramiteInterno(Boolean.FALSE);
			progEntrega.setScoAfPedido(null);
			progEntrega.setIndConversaoUnidade(false);
			progEntrega.setIndPublicado(false);
			if (tipoSolicitacao == DominioTipoSolicitacao.SS) {
				progEntrega.setQtde(1);
				progEntrega.setValorTotal(valorParcelaAf);
			} else {				
				progEntrega.setValorTotal(this.obterValorTotal(id.getIafAfnNumero(), id.getIafNumero(), qtdParcelaAf, 0, progEntrega.getValorEfetivado()));
			}
		} else {						
			progEntrega = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(id);
			progEntrega.setDtPrevEntrega(previsaoEntrega);
			progEntrega.setQtde(qtdParcelaAf);
			progEntrega.setRapServidorAlteracao(servidorLogado);
			if (indPlanejada == null || !indPlanejada.isSim()) {
				progEntrega.setIndPlanejamento(Boolean.FALSE);
				progEntrega.setDtLibPlanejamento(null);
				progEntrega.setRapServidorLibPlanej(null);
			} else {
				progEntrega.setIndPlanejamento(indPlanejada.isSim());				
				progEntrega.setDtLibPlanejamento(new Date());
				progEntrega.setRapServidorLibPlanej(servidorLogado);				
			}
			progEntrega.setDtAlteracao(new Date());
			
			if (tipoSolicitacao == DominioTipoSolicitacao.SS) {
				progEntrega.setValorTotal(valorParcelaAf);
			} else {
				progEntrega.setValorTotal(this.obterValorTotal(id.getIafAfnNumero(), id.getIafNumero(), qtdParcelaAf, 
						progEntrega.getQtdeEntregue(), progEntrega.getValorEfetivado()));
			}
		}
		
		getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(progEntrega);
		return progEntrega;
	}
		
	/**
	 * Persiste uma lista de programação de entrega para um item de AF
	 * @param listaPea
	 * @param listaPeaExclusao
	 * @param id
	 * @param previsaoEntrega
	 * @param valorParcelaAf
	 * @param qtdParcelaAf
	 * @param indPlanejada
	 * @param tipoSolicitacao
	 * @param novoRegistro
	 * @throws BaseException
	 */
	public PersistenciaProgEntregaItemAfVO persistirProgEntregaItemAutorizacaoFornecimento (List<ParcelaItemAutFornecimentoVO> listaPea, 
			List<ParcelaItemAutFornecimentoVO> listaPeaExclusao,
			ScoProgEntregaItemAutorizacaoFornecimentoId id,	Date previsaoEntrega, Double valorParcelaAf, 
			Integer qtdParcelaAf, DominioSimNao indPlanejada, 
			DominioTipoSolicitacao tipoSolicitacao, Boolean novoRegistro,
			Double valorLiberar, Integer qtdLiberar) throws BaseException {
		
		PersistenciaProgEntregaItemAfVO vo = new PersistenciaProgEntregaItemAfVO();
		
		vo.setExisteEntregaProgramada(false);
		
		// Persiste parcela se não houverem itens vinculados a ela ou se pelo
		// menos um deles não gerar novo complemento de AF.
		vo.setPersistirParcela(listaPea.isEmpty());
		
		TipoProgramacaoEntrega gerarTudo = ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega.GERAR_TUDO;
		TipoProgramacaoEntrega gerarItemPacAf = ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega.GERAR_ITEM_PAC_AF;
		
		// Se for gerar AF deve descontar do valor da parcela que ficou atrelada
		Integer descontoQtdParcelaAf = 0;
		Double descontoValorParcelaAf = 0.00;

		
		for (ParcelaItemAutFornecimentoVO pea : listaPea) {
			vo.setExisteEntregaProgramada(getValidacaoProgEntregaItemAfON().getEntregaProgramada(vo.getExisteEntregaProgramada(), pea.getGeradoAutomaticamente()));
			
			if (!vo.getPersistirParcela() && !gerarTudo.equals(pea.getTipoProgramacaoEntrega()) 
					&& !gerarItemPacAf.equals(pea.getTipoProgramacaoEntrega())) {
				vo.setPersistirParcela(true);
			}
			
			if (gerarTudo.equals(pea.getTipoProgramacaoEntrega()) || gerarItemPacAf.equals(pea.getTipoProgramacaoEntrega())) {
				if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
					descontoQtdParcelaAf += pea.getQtdeDetalhada();
				} else {
					descontoValorParcelaAf += pea.getValorDetalhado();
				}
			}
			
			if (vo.getExisteEntregaProgramada() && vo.getPersistirParcela()) {
				break;
			}
		}
		
		this.getValidacaoProgEntregaItemAfON().validarDataPrevisaoEntrega(previsaoEntrega);
		this.getValidacaoProgEntregaItemAfON().validarValorLiberado(id, valorParcelaAf, qtdParcelaAf, valorLiberar, qtdLiberar, tipoSolicitacao, vo.getExisteEntregaProgramada());	
		this.getValidacaoProgEntregaItemAfON().validarQuantidadeLimiteParcela(listaPea, qtdParcelaAf, valorParcelaAf,	0, 0.00, tipoSolicitacao, false);		
		this.getValidacaoProgEntregaItemAfON().validarNovoRegistro(novoRegistro, vo.getExisteEntregaProgramada());		
		this.getValidacaoProgEntregaItemAfON().validarQuantidadeMultipla(id, tipoSolicitacao, qtdParcelaAf);
		
		vo.setProgEntrega(null);
		
		if (vo.getPersistirParcela() || descontoQtdParcelaAf > 0 || descontoValorParcelaAf > 0) {
			vo.setProgEntrega(this.atualizarProgEntregaItemAutorizacaoFornecimento(
					previsaoEntrega, (Double)CoreUtil.nvl(valorParcelaAf, 0) - descontoValorParcelaAf,
					qtdParcelaAf - descontoQtdParcelaAf, indPlanejada, id, tipoSolicitacao,
					novoRegistro));
		}
		
		if (!vo.getExisteEntregaProgramada()) {
			this.getScoSolicitacaoProgramacaoEntregaON()
					.persistirListaSolicitacaoProgramacao(listaPea,
							tipoSolicitacao, vo.getProgEntrega(),
						previsaoEntrega, valorParcelaAf, qtdParcelaAf,
							indPlanejada, id);
			
			this.getScoSolicitacaoProgramacaoEntregaON().excluirListaSolicitacaoProgramacao(listaPeaExclusao);
		}

		return vo;
	}
	
	private Boolean verificarItemNaoEntregue(ParcelaItemAutFornecimentoVO item, DominioTipoSolicitacao tipoSolicitacao) {
		return (((tipoSolicitacao == DominioTipoSolicitacao.SC && (item.getQtdeEntregue() == null || item.getQtdeEntregue() == 0)) ||
				(tipoSolicitacao == DominioTipoSolicitacao.SS && (item.getValorEfetivado() == null || item.getValorEfetivado() == 0.00))));
	}
	
	private void reescreverPrioridades(List<ParcelaItemAutFornecimentoVO> listaOrdenaData, List<ParcelaItemAutFornecimentoVO> listaPea) {
		for (ParcelaItemAutFornecimentoVO item : listaPea) {
			Integer index = listaOrdenaData.indexOf(item);
			if (index >= 0) {
				item.setIndPrioridade(listaOrdenaData.get(index).getIndPrioridade());
			}
		}
	}
	
	private Boolean verificarJaExistePrioridade(List<ParcelaItemAutFornecimentoVO> listaOrdenaData, Integer prioridade) {
		Boolean jaExiste = Boolean.FALSE;
		for (ParcelaItemAutFornecimentoVO subItem : listaOrdenaData) {
			if (Objects.equals(subItem.getIndPrioridade(), Short.valueOf(prioridade.toString()))) {							
				jaExiste = Boolean.TRUE;
				break;
			}
		}
		return jaExiste;
	}
	
	/**
	 * Calcula a prioridade de uma programação de entrega item de AF baseado na data da solicitação, na pré-existência de prioridade e no recebimento do material 
	 * @param listaPea
	 * @param tipoSolicitacao
	 */
	public void calcularPrioridadeEntrega(List<ParcelaItemAutFornecimentoVO> listaPea, DominioTipoSolicitacao tipoSolicitacao) {
		if (listaPea != null && listaPea.size() == 1) {
			listaPea.get(0).setIndPrioridade((short)1);
		} else {
			List<ParcelaItemAutFornecimentoVO> listaOrdenaData = new ArrayList<ParcelaItemAutFornecimentoVO>();		
			
			// acrescenta quem nao foi entregue na lista
			for (ParcelaItemAutFornecimentoVO item : listaPea) {
				if (this.verificarItemNaoEntregue(item, tipoSolicitacao) && item.getIndPrioridadeAlterada().equals(Boolean.FALSE)) {
					try {
						ParcelaItemAutFornecimentoVO itemClone = (ParcelaItemAutFornecimentoVO) BeanUtils.cloneBean(item);
						itemClone.setIndPrioridade(null);
						listaOrdenaData.add(itemClone);
					} catch (IllegalAccessException
							| InstantiationException
							| InvocationTargetException
							| NoSuchMethodException e) {
						this.logError("Erro ao clonar objeto durante recalculo das prioridades da programacao de entrega..."); 
					}
				}					
			}
			
			// ordena por data somente os que nao possuem prioridade
			Collections.sort(listaOrdenaData, new ParcelaItemAutFornecimentoVOPorDataGeracaoSolComprasComparator());

			// acrescenta quem ja tinha prioridade ao final da lista ordenada
			for (ParcelaItemAutFornecimentoVO item : listaPea) {
				Integer indexOrdenado = listaOrdenaData.indexOf(item);
				if (indexOrdenado < 0) {
					listaOrdenaData.add(item);
				}
			}
			
			// computa as prioridades quando necessario na lista temporaria
			Integer prioridade = 1;
			for (ParcelaItemAutFornecimentoVO item : listaOrdenaData) {
				if (item.getIndPrioridade() == null) {
					if (!this.verificarJaExistePrioridade(listaOrdenaData, prioridade)) {
						item.setIndPrioridade(prioridade.shortValue());					
					} else {
						do  {
							prioridade++;
						} while (this.verificarJaExistePrioridade(listaOrdenaData, prioridade));
						item.setIndPrioridade(prioridade.shortValue());
					}
				}
				prioridade++;
			}
			
			this.reescreverPrioridades(listaOrdenaData, listaPea);
		}
	}
	
public void updateParcelasAFP(Integer numAf, Integer numAfp, Short prazoEntrega)  throws ApplicationBusinessException{
		
		List<ScoItemAFPVO> listRetorno = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisaProgEntregaItemAf(numAfp , numAf);
		if (listRetorno!=null && listRetorno.size()>0){
			for (ScoItemAFPVO itemVO : listRetorno){
				ScoProgEntregaItemAutorizacaoFornecimento item = new ScoProgEntregaItemAutorizacaoFornecimento();
				ScoProgEntregaItemAutorizacaoFornecimentoId itemAfpId = new ScoProgEntregaItemAutorizacaoFornecimentoId();
				itemAfpId.setIafAfnNumero(itemVO.getIafAfnNumero());
				itemAfpId.setIafNumero(itemVO.getIafNumero());
				itemAfpId.setParcela(itemVO.getNumeroParcela());
				itemAfpId.setSeq(itemVO.getSeqItemAfp());
				item = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(itemAfpId);
				
				Date sysdate = new GregorianCalendar().getTime();
				
				if (item.getIndAssinatura()!=null && item.getIndAssinatura() && item.getIndEnvioFornecedor()!=null && !item.getIndEnvioFornecedor()){
				   Date verificaData = DateUtil.adicionaDias(item.getDtPrevEntrega(),	prazoEntrega.intValue());
					
					if (verificaData.after(sysdate)){
						Date atualizaData = DateUtil.adicionaDias(sysdate,	prazoEntrega.intValue());
						item.setDtPrevEntrega(atualizaData);
						item.setIndEnvioFornecedor(true);
						try {
							this.getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(item);
						} catch (ApplicationBusinessException e) {
							this.logError(ScoProgEntregaItemAutorizacaoFornecimentoONExceptionCode.MENSAGEM_ERRO_PERSISTIR_ITEM_AFP);
						}
					}
				}
			}
		}
	}

	/**
	 * Exclui parcelas pendentes de itens relacionados a uma SC.
	 * 
	 * @param sc SC
	 * @throws BaseException 
	 */
	public void excluirParcelasPendentes(ScoSolicitacaoDeCompra sc) throws BaseException {
		assert sc != null : "SC é indefinida.";
		excluirParcelasPendentes(sc.getNumero());
	}

	/**
	 * Exclui parcelas pendentes de itens relacionados a uma SC.
	 * 
	 * @param scId ID da SC
	 * @throws BaseException 
	 */
	public void excluirParcelasPendentes(int scId) throws BaseException {
		List<ScoProgEntregaItemAutorizacaoFornecimento> parcelas = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.pesquisarParcelasPendentes(scId);
		
		for (ScoProgEntregaItemAutorizacaoFornecimento parcela : parcelas) {
			excluirProgEntregaItemAf(parcela);
		}
	}
			
	protected ScoProgEntregaItemAutorizacaoFornecimentoRN getScoProgEntregaItemAutorizacaoFornecimentoRN() {
		return scoProgEntregaItemAutorizacaoFornecimentoRN;
	}
	
	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoSolicitacaoProgramacaoEntregaON getScoSolicitacaoProgramacaoEntregaON() {
		return scoSolicitacaoProgramacaoEntregaON;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	public ValidacaoProgEntregaItemAfON getValidacaoProgEntregaItemAfON() {
		return validacaoProgEntregaItemAfON;
	}
	public void setValidacaoProgEntregaItemAfON(
			ValidacaoProgEntregaItemAfON validacaoProgEntregaItemAfON) {
		this.validacaoProgEntregaItemAfON = validacaoProgEntregaItemAfON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}