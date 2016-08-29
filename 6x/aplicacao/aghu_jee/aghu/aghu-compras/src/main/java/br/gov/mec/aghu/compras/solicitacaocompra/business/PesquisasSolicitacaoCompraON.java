package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.dao.ScoLogGeracaoScMatEstocavelDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.ConsumoMedioMensalVO;
import br.gov.mec.aghu.compras.vo.ProcessoGeracaoAutomaticaVO;
import br.gov.mec.aghu.compras.vo.RelatorioSolicitacaoCompraEstocavelVO;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PesquisasSolicitacaoCompraON  extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisasSolicitacaoCompraON.class);

	public enum ListarSolicitacaoEstocaveisNExceptionCode implements BusinessExceptionCode {
		DATA_INICIAL_SUPERIOR_A_FINAL, INFORME_FILTROS_PESQUISA, MENSAGEM_COLECAO_VAZIA
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ScoLogGeracaoScMatEstocavelDAO scoLogGeracaoScMatEstocavelDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5922834917785524838L;
	
	
	
	public List<RelatorioSolicitacaoCompraEstocavelVO> pesquisarSolicitacaoMaterialEstocavel(
			Date dtInicial, Date dtFinal, Integer numSolicitacao, Date dataCompetencia)
			throws ApplicationBusinessException {
		if (dtInicial != null){
			dtInicial = DateUtil.obterDataComHoraInical(dtInicial);
			
			if (dtFinal == null) {
				dtFinal = DateUtil.obterDataComHoraFinal(dtInicial);
			} else {
				dtFinal = DateUtil.obterDataComHoraFinal(dtFinal);
			}
		}
		
		validaParametros(dtInicial, dtFinal, numSolicitacao);

		List<RelatorioSolicitacaoCompraEstocavelVO> listaSolicitacaoCompraEstocavelVO = 
				getScoSolicitacoesDeComprasDAO().pesquisarSolicitacaoMaterialEstocavel(
						dtInicial, dtFinal, numSolicitacao, dataCompetencia);
		if (listaSolicitacaoCompraEstocavelVO == null || listaSolicitacaoCompraEstocavelVO.isEmpty()) {
			throw new ApplicationBusinessException(
					ListarSolicitacaoEstocaveisNExceptionCode.MENSAGEM_COLECAO_VAZIA);
		}
		
		for (RelatorioSolicitacaoCompraEstocavelVO relatorioSolicitacaoCompraEstocavelVO : listaSolicitacaoCompraEstocavelVO){
			List<ConsumoMedioMensalVO> consumoMedioMensalVO = obterConsumoMensal(relatorioSolicitacaoCompraEstocavelVO.getCodigo()); 
			relatorioSolicitacaoCompraEstocavelVO.setConsumoMensal(consumoMedioMensalVO);
			
			dadosUltimaCompraEstocavel(relatorioSolicitacaoCompraEstocavelVO);
			
			consumoMedioSazonalEstocavel(relatorioSolicitacaoCompraEstocavelVO);
		}	

		return listaSolicitacaoCompraEstocavelVO;
	}
		
	private void dadosUltimaCompraEstocavel(
			RelatorioSolicitacaoCompraEstocavelVO relatorioVO) {
		
		BigDecimal valor;
		Date dtUltimaMov;
		Date dtUltimaCompra;
		
		Integer codMaterial;
		Short almSeq;
		Short tmvSeq;
		
		if (relatorioVO.getCodigo() != null) {
			codMaterial = relatorioVO.getCodigo();
		} else {
			return;
		}
		
		if (relatorioVO.getAlmox() != null){
			almSeq = relatorioVO.getAlmox().shortValue();
		} else {
			return;
		}
		
		tmvSeq = getScoSolicitacoesDeComprasDAO().buscaTmvDoc().shortValue();
		
		valor = this.getEstoqueFacade().obterValorUltimaCompra(codMaterial, almSeq, tmvSeq);
		dtUltimaCompra = this.getEstoqueFacade().obterDtUltimaCompra(codMaterial, almSeq);
		dtUltimaMov = this.getEstoqueFacade().obterDtUltimaMovimentacao(codMaterial, almSeq, tmvSeq);

		relatorioVO.setDtUltimaMovimentacao(dtUltimaMov);
		relatorioVO.setDtUltimaCompra(dtUltimaCompra);
		relatorioVO.setValorUnitarioCompra(valor);		
	}
	
			
	public List<ConsumoMedioMensalVO> obterConsumoMensal(Integer codigoMaterial){
		SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy", new Locale("pt", "BR"));
		
		List<ConsumoMedioMensalVO> consumoMedioMensal = new ArrayList<ConsumoMedioMensalVO>();
		Date dtCompetencia;
		Integer consumoTotal = 0;

		Calendar dataAtual = GregorianCalendar.getInstance();
		dataAtual.setTime(new Date());
		dataAtual.set(Calendar.DAY_OF_MONTH, 1);
		dataAtual.set(Calendar.HOUR_OF_DAY, 0);
		dataAtual.set(Calendar.MINUTE, 0);
		dataAtual.set(Calendar.SECOND, 0);
		dataAtual.set(Calendar.MILLISECOND, 0);
		int mesAtual = 1, numMeses = 7;
				
		for (int i = numMeses; i >= mesAtual; i--){
			ConsumoMedioMensalVO consumoMes = new ConsumoMedioMensalVO(); 
			dtCompetencia = DateUtils.addMonths(dataAtual.getTime(), -i+1);		

			List<SceMovimentoMaterial> movimentoMateriais = this.getEstoqueFacade().obterConsumoMensal(codigoMaterial, dtCompetencia);

			Integer naoEstornado = 0;
			Integer estornado = 0;

			for(SceMovimentoMaterial movimentoMaterial : movimentoMateriais){
				if (!movimentoMaterial.getIndEstorno()){  // IND_ESTORNO  == 'N'
					if (movimentoMaterial.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.DB) && movimentoMaterial.getQuantidade() != null){
						naoEstornado = naoEstornado + movimentoMaterial.getQuantidade();						
					}else{
						naoEstornado = naoEstornado + (movimentoMaterial.getQuantidade() * (-1));
					}
				} 
				
				if (movimentoMaterial.getIndEstorno()){  // IND_ESTORNO  == 'S'
					if (movimentoMaterial.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.DB) && movimentoMaterial.getQuantidade() != null){
						estornado = estornado + movimentoMaterial.getQuantidade();						
					}else{
						estornado = estornado + (movimentoMaterial.getQuantidade() * (-1));
					}
				}				
		    }
		    
		    consumoMes.setConsumo(naoEstornado - estornado);
		    
		    if (i == mesAtual) {
		    	consumoMes.setMesAno(getResourceBundleValue("TITLE_MES_ATUAL"));
		    } else {
		    	consumoMes.setMesAno(sdf.format(dtCompetencia));
			    consumoTotal += consumoMes.getConsumo(); 
		    }
		    
		    consumoMedioMensal.add(consumoMes);
		}
		
		ConsumoMedioMensalVO mediaConsumo = new ConsumoMedioMensalVO(); 
		mediaConsumo.setMesAno(getResourceBundleValue("TITLE_MEDIA_SEMESTRE"));
		mediaConsumo.setConsumo(consumoTotal / (numMeses-1));
		mediaConsumo.setMedia(true);
		
		consumoMedioMensal.add(mediaConsumo);
		
		return consumoMedioMensal;		
	}
	
	private void consumoMedioSazonalEstocavel(RelatorioSolicitacaoCompraEstocavelVO relatorioVO){		
		String periodo = null;
		
		Calendar dataReferencia = GregorianCalendar.getInstance();
		dataReferencia.setTime(relatorioVO.getDataSolicitacao());	
		dataReferencia.add(Calendar.MONTH, -12);
		dataReferencia.set(Calendar.DAY_OF_MONTH, 1);
		dataReferencia.set(Calendar.HOUR_OF_DAY, 0);
		dataReferencia.set(Calendar.MINUTE, 0);
		dataReferencia.set(Calendar.SECOND, 0);
		dataReferencia.set(Calendar.MILLISECOND, 0);
		
		Date data1, data2, data3;
		data1 = dataReferencia.getTime();
		data2 = dataReferencia.getTime();
		data3 = dataReferencia.getTime();
		
		
		Integer mes = dataReferencia.get(Calendar.MONTH);
		String periodoMsg = getResourceBundleValue("CONSUMO_SAZONAL_PERIODO");

		if (mes >= 2 && mes <= 4){
			periodo = MessageFormat.format(periodoMsg, getResourceBundleValue("CONSUMO_SAZONAL_OUTONO"));	
			
			data1.setMonth(2);
			data2.setMonth(3);
			data3.setMonth(4);			
		} 
		
		if (mes >= 5 && mes <= 7){
			periodo = MessageFormat.format(periodoMsg, getResourceBundleValue("CONSUMO_SAZONAL_INVERNO"));
			
			data1.setMonth(5);
			data2.setMonth(6);
			data3.setMonth(7);						
		} 

		if (mes >= 8 && mes <= 10){
			periodo = MessageFormat.format(periodoMsg, getResourceBundleValue("CONSUMO_SAZONAL_PRIMAVERA"));
			
			data1.setMonth(8);
			data2.setMonth(9);
			data3.setMonth(10);						
		} 
		
		if (mes >= 11 || mes <= 1){
			periodo = MessageFormat.format(periodoMsg, getResourceBundleValue("CONSUMO_SAZONAL_VERAO"));
			
			data1.setMonth(11);
			data2.setMonth(0);
			data3.setMonth(1);	
			
			if (mes == 11){
				data2.setYear(data2.getYear() + 1);
				data3.setYear(data3.getYear() + 1);	
			}else{
				data1.setYear(data1.getYear() - 1);
			}			
		}
		
		relatorioVO.setPeriodo(periodo);
		
		Short almSeq;
		if (relatorioVO.getAlmox() != null){
			almSeq = relatorioVO.getAlmox().shortValue();
		} else {
			almSeq = null;
		}		
		
		Integer totalDebito = 0;
		Integer totalCredito = 0;
		Integer mediaSazonal = 0;
		totalDebito = this.getEstoqueFacade().consumoMedioSazonal(data1, data2, data3, relatorioVO.getCodigo(), almSeq, DominioIndOperacaoBasica.DB);		
		totalCredito = this.getEstoqueFacade().consumoMedioSazonal(data1, data2, data3, relatorioVO.getCodigo(), almSeq, DominioIndOperacaoBasica.CR);
		mediaSazonal = totalDebito - totalCredito;
		
		relatorioVO.setConsumoMedioSazonal(mediaSazonal);		
	}
	
	private void validaParametros(Date dtInicial, Date dtFinal,
			Integer numSolicitacao) throws ApplicationBusinessException {
		if (dtInicial != null && dtFinal != null) {
			if (dtInicial.after(dtFinal)) {
				throw new ApplicationBusinessException(
						ListarSolicitacaoEstocaveisNExceptionCode.DATA_INICIAL_SUPERIOR_A_FINAL);
			}
		}

		if (numSolicitacao == null && dtInicial == null) {
			throw new ApplicationBusinessException(
					ListarSolicitacaoEstocaveisNExceptionCode.INFORME_FILTROS_PESQUISA);
		}
	}	
	
	
	/**
	 * Retorna solicitações de compras conforme regras da tela de solicitacao de compras para liberação.
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasLiberacaoSc(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa) {
		RapServidores servidor = this.getServidorLogadoFacade().obterServidorLogado();
		return getScoSolicitacoesDeComprasDAO()
				.pesquisarSolicitacaoComprasLiberacaoSc(firstResult,
						maxResults, orderProperty, asc, filtroPesquisa,
						servidor);
	}

	/**
	 * Retorna quantidade de solicitações de compras conforme regras da tela de solicitacao de compras para liberação.
	 * @param filtroPesquisa
	 * @return Integer
	 */
	public Long countSolicitacaoComprasLiberacaoSc(
			PesqLoteSolCompVO filtroPesquisa) {				
		RapServidores servidor = this.getServidorLogadoFacade().obterServidorLogado();
			return getScoSolicitacoesDeComprasDAO().countSolicitacaoComprasLiberacaoSc(filtroPesquisa, servidor);
	}

	/**
	 * Retorna lista de solicitações de compras conforme regras de visualização do planejamento
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasPlanejamentoSc(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa,
			 Boolean pesquisarScMaterialEstocavel) {		
		RapServidores servidor = this.getServidorLogadoFacade().obterServidorLogado();
		if (pesquisarScMaterialEstocavel == Boolean.TRUE){						
			ProcessoGeracaoAutomaticaVO  ultimaGeracao = getScoLogGeracaoScMatEstocavelDAO().obterUltimoProcessoGeracao();
			
			if (ultimaGeracao == null) {
				return new ArrayList<ScoSolicitacaoDeCompra>();
			}
			
			return getScoSolicitacoesDeComprasDAO().pesquisarSCMaterialEstocavel(firstResult, maxResults, orderProperty, asc,
					ultimaGeracao.getSeqProcesso());
		}
		else {
			
			if (filtroPesquisa.getDataInicioAnaliseSolicitacaoCompra() == null && filtroPesquisa.getDataFimAnaliseSolicitacaoCompra() == null
					 && filtroPesquisa.getNumeroSolicitacaoCompra() == null){
				filtroPesquisa.setDataFimAnaliseSolicitacaoCompra(new Date());
			}
						
			filtroPesquisa
			.setFiltroVazio(verificarFiltroPlanejamentoVazio(filtroPesquisa)
					|| filtroPesquisa.getPontoParada() == this
							.getScoPontoParadaSolicitacaoDAO()
							.obterPontoParadaPorTipo(
									DominioTipoPontoParada.PL));
			
			return getScoSolicitacoesDeComprasDAO()
					.pesquisarSolicitacaoComprasPlanejamentoSc(firstResult,
							maxResults, orderProperty, asc, filtroPesquisa,
							servidor);
		}
	}

	/**
	 * Retorna quantidade de solicitações de compras conforme regras da tela de planejamento
	 * @param filtroPesquisa
	 * @return Integer
	 */
	public Long countSolicitacaoComprasPlanejamentoSc(
			PesqLoteSolCompVO filtroPesquisa,  Boolean pesquisarScMaterialEstocavel) {
		RapServidores servidor = this.getServidorLogadoFacade().obterServidorLogado();
		if (pesquisarScMaterialEstocavel == Boolean.TRUE){	
			
			ProcessoGeracaoAutomaticaVO  ultimaGeracao = getScoLogGeracaoScMatEstocavelDAO().obterUltimoProcessoGeracao();
			if (ultimaGeracao == null) {
				return 0L;
			}
			
			return getScoSolicitacoesDeComprasDAO().pesquisarSCMaterialEstocavelCount(ultimaGeracao.getSeqProcesso());
		}
		else {
			if (filtroPesquisa.getDataInicioAnaliseSolicitacaoCompra() == null && filtroPesquisa.getDataFimAnaliseSolicitacaoCompra() == null
					 && filtroPesquisa.getNumeroSolicitacaoCompra() == null){
				filtroPesquisa.setDataFimAnaliseSolicitacaoCompra(new Date());
			}
			
			filtroPesquisa
					.setFiltroVazio(verificarFiltroPlanejamentoVazio(filtroPesquisa)
							|| filtroPesquisa.getPontoParada() == this
									.getScoPontoParadaSolicitacaoDAO()
									.obterPontoParadaPorTipo(
											DominioTipoPontoParada.PL));
			return getScoSolicitacoesDeComprasDAO()
					.countSolicitacaoComprasPlanejamentoSc(filtroPesquisa, servidor);
		}
	}
	
	
	/**
	 * Verifica se o VO de filtro está vazio
	 * @param filtroPesquisa
	 * @return Boolean
	 */
	public Boolean verificarFiltroPlanejamentoVazio(
			PesqLoteSolCompVO filtroPesquisa) {
		return (filtroPesquisa.getPontoParada() == null
				&& filtroPesquisa.getNumeroSolicitacaoCompra() == null
				&& filtroPesquisa.getDataSolicitacaoCompra() == null
				&& filtroPesquisa.getDataInicioSolicitacaoCompra() == null
				&& filtroPesquisa.getDataFimSolicitacaoCompra() == null
				&& filtroPesquisa.getGrupoMaterial() == null
				&& filtroPesquisa.getMaterial() == null
				&& filtroPesquisa.getCentroCustoSolicitante() == null
				&& filtroPesquisa.getCentroCustoAplicacao() == null
				&& filtroPesquisa.getSolicitacaoPrioritaria() == null
				&& filtroPesquisa.getSolicitacaoUrgente() == null
				&& filtroPesquisa.getSolicitacaoAutorizada() == null
				&& filtroPesquisa.getSolicitacaoExcluida() == null
				&& filtroPesquisa.getSolicitacaoDevolvida() == null
				&& filtroPesquisa.getRepAutomatica() == null
				&& filtroPesquisa.getMatEstocavel() == null
				&& filtroPesquisa.getFiltroComprador() == null
				&& filtroPesquisa.getVerbaGestao() == null
				&& filtroPesquisa.getDataInicioAnaliseSolicitacaoCompra() == null
				&& filtroPesquisa.getDataFimAnaliseSolicitacaoCompra() == null
				&& filtroPesquisa.getDataInicioAutorizacaoSolicitacaoCompra() == null && filtroPesquisa
					.getDataFimAutorizacaoSolicitacaoCompra() == null);
	}
	
	

	public List<ScoSolicitacaoDeCompra> pesquisarLoteSolicitacaoCompras(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return getScoSolicitacoesDeComprasDAO()
				.pesquisarLoteSolicitacaoCompras(firstResult, maxResults,
						orderProperty, asc, filtroPesquisa, servidorLogado);
	}
	
	/**
	 * Retorna lista de solicitações de compras conforme regras para autorização da SC.
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasAutorizarSc(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return getScoSolicitacoesDeComprasDAO()
				.pesquisarSolicitacaoComprasAutorizarSc(firstResult,
						maxResults, orderProperty, asc, filtroPesquisa,
						servidorLogado);
	}

	/**
	 * Retorna quantidade de solicitações de compras conforme regras para autorização da SC.
	 * @param filtroPesquisa
	 * @return Integer
	 */
	public Long countSolicitacaoComprasAutorizarSc(
			PesqLoteSolCompVO filtroPesquisa ) {
		RapServidores servidor =this.getServidorLogadoFacade().obterServidorLogado();
		return getScoSolicitacoesDeComprasDAO()
				.countSolicitacaoComprasAutorizarSc(filtroPesquisa, servidor);
	}

	public Boolean verificarPontoParadaChefia(List<ScoSolicitacaoDeCompra> solicitacoes){		
		Boolean todasSolicitacoesPPChefia = true;
		
		for (ScoSolicitacaoDeCompra sc : solicitacoes){
			if (!DominioTipoPontoParada.CH.equals(sc.getPontoParadaProxima().getTipoPontoParada()) &&
					!DominioTipoPontoParada.GP.equals(sc.getPontoParadaProxima().getTipoPontoParada())){
				todasSolicitacoesPPChefia = false;
			}
		}
		
		return todasSolicitacoesPPChefia;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}	
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}
	
	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public ScoLogGeracaoScMatEstocavelDAO getScoLogGeracaoScMatEstocavelDAO() {
		return scoLogGeracaoScMatEstocavelDAO;
	}

	public void setScoLogGeracaoScMatEstocavelDAO(
			ScoLogGeracaoScMatEstocavelDAO scoLogGeracaoScMatEstocavelDAO) {
		this.scoLogGeracaoScMatEstocavelDAO = scoLogGeracaoScMatEstocavelDAO;
	}
	
}