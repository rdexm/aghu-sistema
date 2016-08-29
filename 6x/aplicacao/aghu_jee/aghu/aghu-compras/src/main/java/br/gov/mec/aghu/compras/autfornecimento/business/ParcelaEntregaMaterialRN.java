package br.gov.mec.aghu.compras.autfornecimento.business;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAFParcelasDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.compras.vo.ConsultarParcelasEntregaMateriaisVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class ParcelaEntregaMaterialRN extends BaseBusiness {

	private static final long serialVersionUID = 5857704722030565341L;

	public enum ParcelaEntregaMaterialRNExceptionCode implements BusinessExceptionCode {
		ERRO_CONSULTAR_PARCELAS_1
	}

	private static final Log LOG = LogFactory.getLog(ParcelaEntregaMaterialRN.class);

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	@Inject
	private ScoProgEntregaItemAFParcelasDAO scoProgEntregaItemAFParcelasDAO;

	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * RN1 #5561
	 */
	public Date buscarDataEntrega() throws ApplicationBusinessException {
		Integer diaMes = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		Integer mes = Calendar.getInstance().get(Calendar.MONTH);
		// P_AGHU_DIA_MES_CORTE_PROGRAMACAO_ENTREGAS
		AghParametros diaMesCorteProgramacaoEntrega = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_DIA_MES_CORTE_PROGRAMACAO_ENTREGAS);
		Integer dmcpe = diaMesCorteProgramacaoEntrega.getVlrNumerico().intValue();
		// P_AGHU_DIA_MES_PROGRAMACAO_ENTREGAS
		AghParametros diaMesProgramacaoEntregas = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_DIA_MES_PROGRAMACAO_ENTREGAS);
		Calendar dataEntrega = Calendar.getInstance();
		Integer diaEntrega = diaMesProgramacaoEntregas.getVlrNumerico().intValue();

		if (diaMes > dmcpe) {
			// P_AGHU_N_MESES_MAIOR_PROGRAMACAO_ENTREGAS
			AghParametros mesesMaiorProgramacaoEntregas = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_AGHU_N_MESES_MAIOR_PROGRAMACAO_ENTREGAS);
			dataEntrega.set(Calendar.DAY_OF_MONTH, diaEntrega);
			mes = mes + mesesMaiorProgramacaoEntregas.getVlrNumerico().intValue();
			dataEntrega.set(Calendar.MONTH, mes);

		} else if (diaMes < dmcpe) {
			// P_AGHU_N_MESES_MENOR_PROGRAMACAO_ENTREGAS
			AghParametros mesesMenorProgramacaoEntregas = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_AGHU_N_MESES_MENOR_PROGRAMACAO_ENTREGAS);
			dataEntrega.set(Calendar.DAY_OF_MONTH, diaEntrega);
			mes = mes + mesesMenorProgramacaoEntregas.getVlrNumerico().intValue();
			dataEntrega.set(Calendar.MONTH, mes);

			AghParametros dataInicioFimAno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIA_INIC_PROG_ENTG_FIM_ANO);
			AghParametros dataFimFimAno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIA_FINAL_PROG_ENTG_FIM_ANO);

			Calendar difa = Calendar.getInstance();
			String diaInic = String.valueOf(dataInicioFimAno.getVlrNumerico().intValue());
			difa.set(Calendar.DAY_OF_MONTH, Integer.valueOf(diaInic.substring(1, 3)));
			String dataFim = String.valueOf(dataInicioFimAno.getVlrNumerico().intValue());
			difa.set(Calendar.MONTH, Integer.valueOf(dataFim.substring(0, 2)));

			Calendar dffa = Calendar.getInstance();
			String diaInic2 = String.valueOf(dataFimFimAno.getVlrNumerico().intValue());
			dffa.set(Calendar.DAY_OF_MONTH, Integer.valueOf(diaInic2.substring(1, 3)));
			String dataFim2 = String.valueOf(dataFimFimAno.getVlrNumerico().intValue());
			dffa.set(Calendar.MONTH, Integer.valueOf(dataFim2.substring(0, 2)));

			boolean entreDatas = DateUtil.validaDataMaior(dataEntrega.getTime(), difa.getTime())
					&& DateUtil.validaDataMaior(dffa.getTime(), dataEntrega.getTime());

			if (entreDatas) {
				AghParametros mesAdicionalFinalAno = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_AGHU_N_MESES_PROG_ENTG_FIM_ANO);
				mes = mes + mesAdicionalFinalAno.getVlrNumerico().intValue();
				dataEntrega.set(Calendar.MONTH, mes);
				//DateUtil.adicionaMeses(dataEntrega.getTime(), mesAdicionalFinalAno.getVlrNumerico().intValue());
				//dataEntrega.add( Calendar.MONTH, mesAdicionalFinalAno.getVlrNumerico().intValue());  
			}
		}

		return dataEntrega.getTime();
	}

	public String verificaCorFundoPrevEntregas(Date dtPrevEntrega) throws ApplicationBusinessException{

		AghParametros diaMesProgramacaoEntregas = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_DIA_MES_PROGRAMACAO_ENTREGAS);
		AghParametros mesesMenorProgramacaoEntregas = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_N_MESES_MENOR_PROGRAMACAO_ENTREGAS);

		Date param1 = DateUtil.obterData(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
				diaMesProgramacaoEntregas.getVlrNumerico().intValue());
		Date param2 = DateUtil.adicionaDias(param1, mesesMenorProgramacaoEntregas.getVlrNumerico().intValue());

		if (dtPrevEntrega.before(param2)) {
			return "yellow";
		}
		return "white";
	}

	/**
	 * Atualiza um registro de parcela de entrega para ser considera liberada para assinatura. Utiliza o campo @{IND_PLANEJAMENTO}
	 * @param item
	 * 		Registro a ser alterado
	 * @param servidorLogado
	 * 		Objeto contendo o servidor logado no sistema
	 * @throws ApplicationBusinessException
	 */
	private void liberaParcela(ScoProgEntregaItemAutorizacaoFornecimento item, RapServidores servidorLogado) throws ApplicationBusinessException{
		item.setIndPlanejamento(Boolean.TRUE);
		item.setRapServidorLibPlanej(servidorLogado);
		item.setDtLibPlanejamento(new Date());
		getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(item);
	}

	/**
	 * Atualiza o indicador @{IND_CANCELADA}de cancelamento de um registro de parcela de entrega. Pode ser para 'S' quando a parcela é
	 * cancelada ou 'N', quando o cancelamento é revertido
	 * @param item
	 * 		Registro a ser alterado
	 * @param servidorLogado
	 * 		Objeto contendo o servidor logado no sistema
	 * @param reverter
	 * 		Quando @{code TRUE}, reverte o cancelamento da parcela de entrega; @{code FALSE}, executa o cancelamento
	 * 	da parcela
	 * @throws ApplicationBusinessException
	 */
	private void cancelarItem(ScoProgEntregaItemAutorizacaoFornecimento item, RapServidores servidorLogado, Boolean reverter) throws ApplicationBusinessException {

		if (!reverter) {
			item.setIndCancelada(Boolean.TRUE);
			item.setRapServidorAlteracao(servidorLogado);
			item.setRapServidorCancelamento(servidorLogado);
			item.setDtAlteracao(new Date());
			item.setDtCancelamento(new Date());
			getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(item);
		} else {
			item.setIndCancelada(Boolean.FALSE);
			item.setRapServidorAlteracao(servidorLogado);
			item.setRapServidorCancelamento(null);
			item.setDtCancelamento(null);
			item.setDtAlteracao(new Date());
			getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(item);
		}
	}

	public void gravar(List<ConsultarParcelasEntregaMateriaisVO> lista, ConsultarParcelasEntregaMateriaisVO selecionado) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		for (ConsultarParcelasEntregaMateriaisVO vo : lista) {
			ScoProgEntregaItemAutorizacaoFornecimento item = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.obterProgramacaoEntregaItemAFPorItemAfNumeroParcela(vo.getIafAfnNumero(), vo.getIafNumero(), vo.getParcela());

			if (item != null) {

				// verifica se parcela foi liberada para assinatura
				if ((item.getIndPlanejamento() != vo.getIndPlanejamento()) && vo.getIndPlanejamento()) {
					liberaParcela(item, servidorLogado);
				}

				// verifica se uma nova parcela foi incluíd
				if ((item.getIndRecalculoManual() != vo.getIndRecalcManual()) && vo.getIndRecalcManual()) {
					// executar C5
					Integer maxParcelaAssinada = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroParcela(vo.getIafAfnNumero(),
							vo.getIafNumero(), true);
					// executar RN03
					validarMaxParcelaAsssinada(vo.getIafAfnNumero(), vo.getIndPlanejamento(), vo.getIndCancela(), false, vo.getIafNumero(),
							maxParcelaAssinada, servidorLogado, selecionado);
					// executar RN04
					executarRN04(vo, servidorLogado);
				}

				// verifica se uma parcela foi cancelada
				if ((item.getIndCancelada() != vo.getIndCancela()) && vo.getIndCancela()) {
					cancelarItem(item, servidorLogado, Boolean.FALSE);

					// executar C7
					Integer maxParcela = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroParcela(vo.getIafAfnNumero(),
							vo.getIafNumero(), null);
					executarI2(vo, maxParcela + 1, DateUtil.adicionaDias(vo.getDataPrevEntrega(), 30), servidorLogado);
					getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
				}

				// verifica se uma parcela teve seu cancelamento revertido
				if ((item.getIndCancelada() != vo.getIndCancela()) && !vo.getIndCancela()) {

					executarD1(vo);

					cancelarItem(item, servidorLogado, Boolean.TRUE);
					getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
				}

				// verifica se foi inserida uma parcela de trâmite interno
				if ((item.getIndTramiteInterno() != vo.getIndTramiteInterno()) && vo.getIndTramiteInterno()) {
					// executar C5
					Integer maxParcelaAssinada = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroParcela(vo.getIafAfnNumero(),
							vo.getIafNumero(), true);

					// - Executar consulta C9 e atribuir o resultado ao parâmetro P_COUNT_PARCELAS
					Integer countParcelas = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroSeqParcelaItemAf(
							vo.getIafAfnNumero(), vo.getIafNumero(), maxParcelaAssinada, true);

					// - Se P_COUNT_PARCELAS>0 executar U1 senão apresentar mensagem de erro ERRO_CONSULTAR_PARCELAS_1.
					if (countParcelas != null && countParcelas > 0) {
						update1(vo.getIafAfnNumero(), vo.getIafNumero(), maxParcelaAssinada, servidorLogado, selecionado);
					} else {
						throw new ApplicationBusinessException(ParcelaEntregaMaterialRNExceptionCode.ERRO_CONSULTAR_PARCELAS_1);
					}

					// executar I3
					executarInsercaoItemAutFornecimentoTramInterno(vo, servidorLogado);
					getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
					// executar U3
					executarAtualizacaoTramInternoERecalculoAutomatico(vo, true, servidorLogado);
					// executar U4
					executarAtualizacaoTramInternoERecalculoAutomatico(vo, false, servidorLogado);

				}

				// atualiza os indicadores de entrega urgente e entrega imediata
				atualizarIndicadores(item, servidorLogado, vo);

				getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();

				getScoProgEntregaItemAutorizacaoFornecimentoDAO().desatachar(item);

			}
		}
	}

	/**
	 * Atualiza os campos @{IND_ENTREGA_IMEDIATA} e @{IND_ENTREGA_URGENTE} conforme os valores de tela
	 * @param item
	 * 		Registro a ser atualizado
	 * @param servidorLogado
	 * 		Objeto contendo o servidor logado no sistema
	 * @param vo
	 * 		VO contendo o estado atual do objeto em tela
	 * @throws ApplicationBusinessException
	 */
	private void atualizarIndicadores(ScoProgEntregaItemAutorizacaoFornecimento item, RapServidores servidorLogado, ConsultarParcelasEntregaMateriaisVO vo) throws ApplicationBusinessException {

		if ((item.getIndEntregaImediata() != vo.getIndEntregaImediata()) ||
				(item.getIndEntregaUrgente() != vo.getIndEntregaUrgente())) {
			item.setIndEntregaImediata(vo.getIndEntregaImediata());
			item.setIndEntregaUrgente(vo.getIndEntregaUrgente());
			item.setRapServidorAlteracao(servidorLogado);
			item.setDtAlteracao(new Date());
			getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(item);

		}

	}

	private void executarAtualizacaoTramInternoERecalculoAutomatico(ConsultarParcelasEntregaMateriaisVO vo, Boolean u3, RapServidores servidorLogado) {
		List<ScoProgEntregaItemAutorizacaoFornecimento> listaItens = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.consultarListaParcelasPorParcela(vo.getIafAfnNumero(), vo.getIafNumero(), u3 ? vo.getParcela() : vo.getParcela() + 1,
						false);
		for (ScoProgEntregaItemAutorizacaoFornecimento item : listaItens) {
			if (u3) {
				item.setIndTramiteInterno(false);
			} else {
				item.setIndRecalculoAutomatico(false);
			}
			item.setDtAlteracao(new Date());
			item.setRapServidorAlteracao(servidorLogado);
			item.setIndEntregaImediata(vo.getIndEntregaImediata());
			item.setIndEntregaUrgente(vo.getIndEntregaUrgente());
			getScoProgEntregaItemAutorizacaoFornecimentoDAO().merge(item);
		}
	}

	private void executarInsercaoItemAutFornecimentoTramInterno(ConsultarParcelasEntregaMateriaisVO vo, RapServidores servidorLogado) {
		ScoProgEntregaItemAutorizacaoFornecimento item = criarNovoItem(vo, servidorLogado);
		item.setIndTramiteInterno(true);
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().persistir(item);
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
	}

	private void executarD1(ConsultarParcelasEntregaMateriaisVO vo) throws ApplicationBusinessException {
		getScoProgEntregaItemAFParcelasDAO().deletarParcelasItemAF(vo.getIafAfnNumero(), vo.getIafNumero());
	}

	private void executarI2(ConsultarParcelasEntregaMateriaisVO vo, Integer maxParcela, Date dataPrevEntrega, RapServidores servidorLogado) {
		ScoProgEntregaItemAutorizacaoFornecimento item = criarNovoItem(vo, servidorLogado);
		item.getId().setParcela(maxParcela);
		item.setDtPrevEntrega(dataPrevEntrega);
		item.setValorTotal(vo.getValorTotal());
		item.setQtde(vo.getQtde());
		item.setQtdeEntregue(0);
		item.setObservacao(vo.getObservacao());

		getScoProgEntregaItemAutorizacaoFornecimentoDAO().persistir(item);
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
	}

	public void validarMaxParcelaAsssinada(Integer iafAfnNumero, Boolean indPlanejamento, Boolean indCancelada, Boolean indAssinatura,
			Integer iafNumero, Integer maxParcelaAssinada, RapServidores servidorLogado, ConsultarParcelasEntregaMateriaisVO selecionado) throws ApplicationBusinessException {
		//C6
		List<ScoProgEntregaItemAutorizacaoFornecimento> listaItens = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.pesquisarProgEntregaItemAf(iafAfnNumero, null, null, indAssinatura, iafNumero, maxParcelaAssinada);

		if (listaItens == null || listaItens.isEmpty()) {
			throw new ApplicationBusinessException(ParcelaEntregaMaterialRNExceptionCode.ERRO_CONSULTAR_PARCELAS_1);
		}

		update1(iafAfnNumero, iafNumero, maxParcelaAssinada, servidorLogado, selecionado);
	}

	private void update1(Integer iafAfnNumero, Integer iafNumero, Integer maxParcelaAssinada, RapServidores servidorLogado, ConsultarParcelasEntregaMateriaisVO selecionado) {
		List<ScoProgEntregaItemAutorizacaoFornecimento> itens = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.consultarListaParcelasPorParcela(iafAfnNumero, iafNumero, maxParcelaAssinada, true);

		for (ScoProgEntregaItemAutorizacaoFornecimento item : itens) {

			if (item.getDtPrevEntrega().after(selecionado.getDataPrevEntrega())) {
				item.setDtPrevEntrega(DateUtil.adicionaDias(item.getDtPrevEntrega(), 1));
				item.setIndEntregaImediata(selecionado.getIndEntregaImediata());
				item.setIndEntregaUrgente(selecionado.getIndEntregaUrgente());
				item.setIndCancelada(Boolean.TRUE);
				item.setRapServidorAlteracao(servidorLogado);
				item.setDtAlteracao(new Date());
				getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(item);
			}

		}
	}

	public void executarRN04(ConsultarParcelasEntregaMateriaisVO vo, RapServidores servidorLogado) {

		// insert I1
		ScoProgEntregaItemAutorizacaoFornecimento novoItem = criarNovoItem(vo, servidorLogado);
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().persistir(novoItem);
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();

		// update U2
		ScoProgEntregaItemAutorizacaoFornecimento item = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.obterProgramacaoEntregaItemAFPorItemAfNumeroParcela(vo.getIafAfnNumero(), vo.getIafNumero(), vo.getParcela());
		item.setIndRecalculoAutomatico(false);
		item.setIndEntregaImediata(vo.getIndEntregaImediata());
		item.setIndEntregaUrgente(vo.getIndEntregaUrgente());
		item.setRapServidorAlteracao(servidorLogado);
		item.setDtAlteracao(new Date());

		getScoProgEntregaItemAutorizacaoFornecimentoDAO().merge(item);
	}

	private ScoProgEntregaItemAutorizacaoFornecimento criarNovoItem(ConsultarParcelasEntregaMateriaisVO vo, RapServidores servidorLogado) {
		Integer seq = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroSeqParcelaItemAf(vo.getIafAfnNumero(),
				vo.getIafNumero(), null, false) + 1;

		Integer parcela = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroParcela(vo.getIafAfnNumero(),
				vo.getIafNumero(), null) + 1;

		ScoProgEntregaItemAutorizacaoFornecimentoId itemId = new ScoProgEntregaItemAutorizacaoFornecimentoId();
		ScoProgEntregaItemAutorizacaoFornecimento item = new ScoProgEntregaItemAutorizacaoFornecimento();

		itemId.setIafAfnNumero(vo.getIafAfnNumero());
		itemId.setIafNumero(vo.getIafNumero());
		itemId.setParcela(parcela);
		itemId.setSeq(seq);

		item.setId(itemId);
		item.setDtGeracao(new Date());
		item.setDtPrevEntrega(DateUtil.adicionaDias(vo.getDataPrevEntrega(), 1));
		item.setQtde(vo.getFatorConversao());
		item.setRapServidor(servidorLogado);
		item.setIndPlanejamento(false);
		item.setIndAssinatura(false);
		item.setIndEmpenhada(DominioAfEmpenhada.N);
		item.setIndEnvioFornecedor(false);
		item.setIndRecalculoAutomatico(false);
		item.setIndRecalculoManual(false);
		item.setValorTotal(vo.getFatorConversao() * vo.getValorUnitario());
		item.setIndImpressa(false);
		item.setIndCancelada(false);
		item.setIndEntregaImediata(false);
		item.setIndEntregaUrgente(false);
		item.setIndTramiteInterno(false);
		item.setObservacao(vo.getObservacao());

		return item;
	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	protected ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {
		return scoSolicitacaoProgramacaoEntregaDAO;
	}

	protected ScoProgEntregaItemAFParcelasDAO getScoProgEntregaItemAFParcelasDAO() {
		return scoProgEntregaItemAFParcelasDAO;
	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoRN getScoProgEntregaItemAutorizacaoFornecimentoRN() {
		return scoProgEntregaItemAutorizacaoFornecimentoRN;
	}

	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
