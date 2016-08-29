package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.vo.ConsultaProgramacaoEntregaItemVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItemAFPVO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ScoProgEntregaItemAutorizacaoFornecimentoRN extends BaseBusiness{

	private static final String _00FF00 = "#00FF00";

	private static final String FF0000 = "#FF0000";

	private static final String FFFF00 = "#FFFF00";

	private static final Log LOG = LogFactory.getLog(ScoProgEntregaItemAutorizacaoFornecimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = -7427791623456805412L;

	/**
	 * Pesquisas pelos itens de uma Autorização de Fornecimento Pedido
	 * utilizando os filtros passados. Funcionalidade implementada para estória
	 * #5564.
	 * 
	 * @param filtro
	 * @return
	 */
	public List<PesquisaItemAFPVO> pesquisarItensAFPedido(final FiltroAFPVO filtro) {
		final List<PesquisaItemAFPVO> retorno = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisarItensAFPedido(filtro);
		return retorno;
	}

	/**
	 * Grava dados de ScoProgEntregaItemAutorizacaoFornecimento.
	 * 
	 * TODO: implementar restante da triggers de update
	 * 
	 * @param progEntregaItemAutorizacaoFornecimento
	 * @throws ApplicationBusinessException
	 */
	public void persistir(final ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento)
			throws ApplicationBusinessException {
		if (progEntregaItemAutorizacaoFornecimento.getVersion() == null) {
			try {
				this.executarAntesInserir(progEntregaItemAutorizacaoFornecimento);
				this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().persistir(progEntregaItemAutorizacaoFornecimento);
			} catch (ApplicationBusinessException e) {
				progEntregaItemAutorizacaoFornecimento.setVersion(null);
				throw e;
			}
		} else {
			this.executarAntesAtualizar(progEntregaItemAutorizacaoFornecimento);
			this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(progEntregaItemAutorizacaoFornecimento);
		}
	}

	/**
	 * Remove parcelamento de entrega.
	 * 
	 * @param progEntrega Parcelamento de entrega.
	 */
	public void remover(ScoProgEntregaItemAutorizacaoFornecimento progEntrega) {
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().remover(progEntrega);
	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	/**
	 * 
	 * Estória #25272.
	 * 
	 * RN1: Se ind_assinatura modificado de ‘N’ para ‘S’, setar
	 * ser_matricula_assinatura e ser_vin_codigo_assinatura com o usuário logado
	 * e dt_assinatura com a data atual.
	 * 
	 * TODO: implementar restante da triggers de update: Demais regras de
	 * negócio deste objeto serão implementadas na estória #24579.
	 * 
	 * @ORADB SCOT_PEA_BRU
	 * @param progEntregaItemAutorizacaoFornecimento
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesAtualizar(final ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final ScoProgEntregaItemAutorizacaoFornecimento entregaItemAutorizacaoFornecimentoOriginal = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.obterOriginal(progEntregaItemAutorizacaoFornecimento.getId());
		if ((!entregaItemAutorizacaoFornecimentoOriginal.getIndAssinatura()) && progEntregaItemAutorizacaoFornecimento.getIndAssinatura()) {
				progEntregaItemAutorizacaoFornecimento.setRapServidorAssinatura(servidorLogado);
		}

	}

	/**
	 * Estória #25272.
	 * 
	 * RN1: Seta campo DT_GERACAO para a data atual do sistema.
	 * 
	 * RN2: Seta ascolunas ser_matricula e ser_vin_codigo com o usuário logado.
	 * 
	 * @ORADB SCOT_PEA_BRI
	 * @param progEntregaItemAutorizacaoFornecimento
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesInserir(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		progEntregaItemAutorizacaoFornecimento.setDtGeracao(formataData(new Date()));
		progEntregaItemAutorizacaoFornecimento.setRapServidor(servidorLogado);
	}
	
	private Date formataData(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	/**
	 * Obtem programação pendente
	 * #24898
	 * @param numeroAf
	 * @param numeroComplemento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoPendente(Integer numeroAf, Integer numeroComplemento) throws ApplicationBusinessException{
		List<ConsultaProgramacaoEntregaItemVO> listaProgramacaoPendente = new LinkedList<ConsultaProgramacaoEntregaItemVO>();
		listaProgramacaoPendente = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterProgramacaoPendente(numeroAf, numeroComplemento);
		if (listaProgramacaoPendente.size() == 0) {
			return listaProgramacaoPendente;
		}else{
			listaProgramacaoPendente = obterCurvaAbcItemLista(listaProgramacaoPendente);
			return obterRegrasParcelas(listaProgramacaoPendente);
		}


	}
	

	
	
	/**
	 * Obtem curva abc para complementar a lista de programacao entrega item
	 * #24898
	 * @param lista 
	 * @param competencia
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private List<ConsultaProgramacaoEntregaItemVO> obterCurvaAbcItemLista(
			List<ConsultaProgramacaoEntregaItemVO> lista) throws ApplicationBusinessException {
		List<ConsultaProgramacaoEntregaItemVO> listaConsultaProgramacaoEntregaItemComCurva = new LinkedList<ConsultaProgramacaoEntregaItemVO>();
		AghParametros parametroCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		
		int seq = 0;
		
		for (ConsultaProgramacaoEntregaItemVO consultaProgramacaoEntregaItemVO : lista) {
			SceEstoqueGeral estoqueGeral = new SceEstoqueGeral();
			estoqueGeral = this
					.getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.obterCurvaAbc(
							consultaProgramacaoEntregaItemVO
									.getNumeroFornecedor(),
									parametroCompetencia.getVlrData(),
							consultaProgramacaoEntregaItemVO.getAfnNumero());
			
			if(estoqueGeral != null && estoqueGeral.getClassificacaoAbc() != null){
				consultaProgramacaoEntregaItemVO.setCurvaAbc(estoqueGeral.getClassificacaoAbc());
			}
			
		// contador ja que não existe um valor sequencial para o VO, sera utilizado para consulta
		consultaProgramacaoEntregaItemVO.setSeq(seq++);
			
			listaConsultaProgramacaoEntregaItemComCurva
					.add(consultaProgramacaoEntregaItemVO);

		}
		return listaConsultaProgramacaoEntregaItemComCurva;

	}
	
	/**
	 * Obtem programação geral
	 * #24898
	 * @param numeroAf
	 * @param numeroComplemento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoGeral(Integer numeroAf, Integer numeroComplemento) throws ApplicationBusinessException{
		List<ConsultaProgramacaoEntregaItemVO> listaProgramacaoGeral = new LinkedList<ConsultaProgramacaoEntregaItemVO>();
		listaProgramacaoGeral = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterProgramacaoGeral(numeroAf, numeroComplemento);
		if (listaProgramacaoGeral.size() == 0) {
			return listaProgramacaoGeral;
		}else{
			listaProgramacaoGeral = obterCurvaAbcItemLista(listaProgramacaoGeral);
			return obterRegrasParcelas(listaProgramacaoGeral);
		}


	}
	
	/**
	 * Obtem regras de parcelamento. Efetua a validação de todas as RNs da estória #24898
	 * @param lista
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<ConsultaProgramacaoEntregaItemVO> obterRegrasParcelas(List<ConsultaProgramacaoEntregaItemVO> lista) throws ApplicationBusinessException{
		List<ConsultaProgramacaoEntregaItemVO> listaConsultaProgramacaoEntregaItem = new LinkedList<ConsultaProgramacaoEntregaItemVO>();
		AghParametros parametroDiaMesCorteProgEntregas = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIA_MES_CORTE_PROGRAMACAO_ENTREGAS);
		AghParametros parametroDiaMesProgEntregas = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIA_MES_PROGRAMACAO_ENTREGAS);
		AghParametros parametroNMesesProgEntregas= getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_N_MESES_MENOR_PROGRAMACAO_ENTREGAS);
		final Calendar dataAtual =  GregorianCalendar.getInstance();
		// RN01 P_DATA -  é composto pelo dia recuperado do parâmetro P_AGHU_DIA_MES PROGRAMACAO_ENTREGAS e pelo mês atual somado ao parâmetro P_AGHU_N_MESES_MENOR_PROGRAMACAO_ENTREGAS.
		Calendar  pData = GregorianCalendar.getInstance();
		pData.set(Calendar.getInstance().get(Calendar.YEAR), (Calendar.getInstance().get(Calendar.MONTH)+ parametroNMesesProgEntregas.getVlrNumerico().intValue()), parametroDiaMesProgEntregas.getVlrNumerico().intValue());
		//RN01_02 P_DATA é composto pelo dia recuperado do parâmetro P_AGHU_DIA_MES_PROGRAMACAO_ENTREGAS e pelo mês atual.
		Calendar  pData2 = GregorianCalendar.getInstance();
		pData.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR)+ Calendar.getInstance().get(Calendar.MONTH), parametroDiaMesProgEntregas.getVlrNumerico().intValue());
		for (ConsultaProgramacaoEntregaItemVO consultaProgramacaoEntregaItemVO : lista) {
			consultaProgramacaoEntregaItemVO = validacaoParcelasSeremLiberadas(parametroDiaMesCorteProgEntregas,dataAtual, pData, pData2, consultaProgramacaoEntregaItemVO);
			consultaProgramacaoEntregaItemVO = outrasRegras(consultaProgramacaoEntregaItemVO);
			listaConsultaProgramacaoEntregaItem.add(consultaProgramacaoEntregaItemVO);
		}
		return listaConsultaProgramacaoEntregaItem;
		
	}

	private ConsultaProgramacaoEntregaItemVO outrasRegras(ConsultaProgramacaoEntregaItemVO consultaProgramacaoEntregaItemVO) {
		//C6
		if (this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().verificarParcelasEmpenhadas(consultaProgramacaoEntregaItemVO.getIafAfnNumero(),consultaProgramacaoEntregaItemVO.getParcela(),consultaProgramacaoEntregaItemVO.getIafNumero())) {
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FF0000);
		}
		//C7
		 if (this.getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.verificarParcelasPrevisaoEntregaVencida(
						consultaProgramacaoEntregaItemVO.getIafAfnNumero(),
						consultaProgramacaoEntregaItemVO.getParcela(),
						consultaProgramacaoEntregaItemVO.getIafNumero())) {
			consultaProgramacaoEntregaItemVO.setParcelasPrevisaoEntregaVencida(true);
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FF0000);
		}
		//C8
		if (this.getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.verificarParcelasAssinadasNaoEmpenhada(
						consultaProgramacaoEntregaItemVO.getIafAfnNumero(),
						consultaProgramacaoEntregaItemVO.getParcela(),
						consultaProgramacaoEntregaItemVO.getIafNumero())) {
			consultaProgramacaoEntregaItemVO.setParcelasAssinadasNaoEmpenhada(true);
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FF0000);
		}
		
		//C9
		if (this.getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.verificarParcelasEmpenhadasNaoEntregue(
						consultaProgramacaoEntregaItemVO.getIafAfnNumero(),
						consultaProgramacaoEntregaItemVO.getParcela(),
						consultaProgramacaoEntregaItemVO.getIafNumero())) {
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FFFF00);
		}
		
		//C10
		if  (this.getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.verificarParcelasNaoEmpenhadasNaoEntregue(
						consultaProgramacaoEntregaItemVO.getIafAfnNumero(),
						consultaProgramacaoEntregaItemVO.getParcela(),
						consultaProgramacaoEntregaItemVO.getIafNumero())) {
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(_00FF00);
		}
		//C11
		if (this.getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.verificarParcelasEmpenhadasQtdeEntregueMenor(
						consultaProgramacaoEntregaItemVO.getIafAfnNumero(),
						consultaProgramacaoEntregaItemVO.getParcela(),
						consultaProgramacaoEntregaItemVO.getIafNumero())) {

			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FFFF00);
		}
		//C12
		if (this.getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.verificarParcelasEmpenhadasQtdeEntregueMenorAtrasada(
						consultaProgramacaoEntregaItemVO.getIafAfnNumero(),
						consultaProgramacaoEntregaItemVO.getParcela(),
						consultaProgramacaoEntregaItemVO.getIafNumero())) {
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FF0000);
		}
		return consultaProgramacaoEntregaItemVO;
	}

	private ConsultaProgramacaoEntregaItemVO validacaoParcelasSeremLiberadas(
			AghParametros parametroDiaMesCorteProgEntregas,
			final Calendar dataAtual, Calendar pData, Calendar pData2,
			ConsultaProgramacaoEntregaItemVO consultaProgramacaoEntregaItemVO) {
		boolean parcelasSeremLiberadasAssinatura;
		//C5
		parcelasSeremLiberadasAssinatura = false;
		parcelasSeremLiberadasAssinatura = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().verificarParcelasSeremLiberadasAssinatura(consultaProgramacaoEntregaItemVO.getAfnNumero(), consultaProgramacaoEntregaItemVO.getParcela(), consultaProgramacaoEntregaItemVO.getIafNumero());
		
		if (dataAtual.get(Calendar.DAY_OF_MONTH) > parametroDiaMesCorteProgEntregas.getVlrNumerico().intValue() && CoreUtil.isMenorDatas(consultaProgramacaoEntregaItemVO.getDtPrevEntrega(), pData.getTime()) && parcelasSeremLiberadasAssinatura ) {
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FFFF00);
		}
		if(dataAtual.get(Calendar.DAY_OF_MONTH) <=  parametroDiaMesCorteProgEntregas.getVlrNumerico().intValue() && CoreUtil.isMenorDatas(consultaProgramacaoEntregaItemVO.getDtPrevEntrega(),  pData2.getTime()) && parcelasSeremLiberadasAssinatura ) {
			consultaProgramacaoEntregaItemVO.setCorDtPrevEntrega(FFFF00);
		}
		return consultaProgramacaoEntregaItemVO;
	}
}
