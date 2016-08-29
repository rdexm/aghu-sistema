package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaTitulosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroPesquisaPosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloProgramadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.estoque.dao.SceBoletimOcorrenciasDAO;
import br.gov.mec.aghu.estoque.vo.SceBoletimOcorrenciasVO;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável pelo redirecionamento das requests do contas a pagar.
 */

@Stateless
@Modulo(ModuloEnum.FINANCEIRO)
public class ContasPagarFacade extends BaseFacade implements IContasPagarFacade {

	private static final long serialVersionUID = 8932352757052748041L;

	private IServidorLogadoFacade servidorLogadoFacade;

	/** Injeção do objeto da camada de negocio do {@link FcpTitulo} */
	@EJB
	private ConsultarTituloRN consultarTituloRN;
	
	@EJB
	private RelatorioFornecedoresIrregularidadeFiscalRN relatorioFornecedoresIrregularidadeFiscalRN;
	
	@EJB
	private ConsultaPosicaoTituloON consultaPosicaoTituloON;
	
	@EJB
	private TituloON tituloON;

	@Inject
	private SceBoletimOcorrenciasDAO sceBoletimOcorrenciasDAO;
	
	@Inject
	private FcpTituloDAO fcpTituloDAO;

	@Override
	public List<TituloVO> pesquisarTitulos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroConsultaTitulosVO filtros) throws BaseException {
		return this.getConsultarTituloRN().pesquisarTitulos(firstResult, maxResult, orderProperty, asc, filtros);
	}
	
	@Override
	public Long pesquisarTitulosCount(FiltroConsultaTitulosVO filtro) throws BaseException {
		return getConsultarTituloRN().pesquisarTitulosCount(filtro);
	}

	@Override
	public List<FsoVerbaGestao> listarVerbaGestaoPorSituacaoSuggestionBox(final String strPesquisa) {
		return this.getConsultarTituloRN().listarVerbaGestaoPorSituacaoSuggestionBox(strPesquisa);
	}

	@Override
	public List<FsoNaturezaDespesa> listarNaturezaDespesaPorSituacaoSuggestionBox(final String strPesquisa) {
		return this.getConsultarTituloRN().listarNaturezaDespesaPorSituacaoSuggestionBox(strPesquisa);
	}

	@Override
	public List<FcpTipoDocumentoPagamento> listarDocumentosPorSituacaoSuggestionBox(final String strPesquisa) {
		return this.getConsultarTituloRN().listarDocumentosPorSituacaoSuggestionBox(strPesquisa);
	}

	@Override
	public Long countListarVerbaGestaoPorSituacao(final String strPesquisa) {
		return this.getConsultarTituloRN().countListarVerbaGestaoPorSituacao(strPesquisa);
	}

	@Override
	public Long countListarNaturezaDespesaPorSituacao(final String strPesquisa) {
		return this.getConsultarTituloRN().countListarNaturezaDespesaPorSituacao(strPesquisa);
	}

	@Override
	public Long countListarDocumentosPorSituacao(final String strPesquisa) {
		return this.getConsultarTituloRN().countListarDocumentosPorSituacao(strPesquisa);
	}

	@Override
	public List<TituloProgramadoVO> obterListaPagamentosProgramados(Date dataPagamento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws BaseException {

		return this.getConsultarTituloRN().obterListaPagamentosProgramados(dataPagamento, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarPagamentosProgramadosCount(Date dataPagamento) throws BaseException {
		return getConsultarTituloRN().pesquisarPagamentosProgramadosCount(dataPagamento);
	}

	/**
	 * Retorna uma instancia do {@link ServidorLogadoFacade}.
	 * 
	 * @return
	 */
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	private ConsultarTituloRN getConsultarTituloRN() {
		return this.consultarTituloRN;
	}

	@Override
	public void alterarTituloPagamento(Integer tituloSeq, Date dataPagamento, boolean validarData) throws ApplicationBusinessException {
		this.consultarTituloRN.alterarTituloPagamento(tituloSeq, dataPagamento, validarData);
	}

	@Override
	public List<String> pesquisarTributosNotaRecebimentoTitulo(Integer notaRecebimentoNumero) {
		return this.consultarTituloRN.pesquisarTributosNotaRecebimentoTitulo(notaRecebimentoNumero);
	}

	@Override
	public SceBoletimOcorrenciasVO obterDadosBoletimTitulo(Integer notaRecebimentoNumero) {
		return sceBoletimOcorrenciasDAO.obterDadosBoletimTitulo(notaRecebimentoNumero);
	}
	
	@Override
	public List<PosicaoTituloVO> pesquisarPosicaoTitulo(Integer firstResult, Integer maxResult, FiltroPesquisaPosicaoTituloVO filtro) {
		return consultaPosicaoTituloON.pesquisarPosicaoTitulo(firstResult, maxResult, filtro);
	}
	
	@Override
	public Long pesquisarPosicaoTituloCount(FiltroPesquisaPosicaoTituloVO filtro) {
		return fcpTituloDAO.pesquisarPosicaoTituloCount(filtro);
	}
	
	@Override
	public List<DatasVencimentosFornecedorVO> pesquisarFornecedoresIrregularidadeFiscal(Date dataInicial, Date dataFinal, Integer numero) throws ApplicationBusinessException {
		return this.relatorioFornecedoresIrregularidadeFiscalRN.pesquisarFornecedoresIrregularidadeFiscal(dataInicial, dataFinal, numero);
	}
	
	@Override
	public void enviarEmailFornecedoresIrregularidadeFiscal(List<DatasVencimentosFornecedorVO> listaFornecedores) throws ApplicationBusinessException {
		this.relatorioFornecedoresIrregularidadeFiscalRN.enviarEmailFornecedoresIrregularidadeFiscal(listaFornecedores);
	}
	
	@Override
	public void validarIntervaloDataGeracaoPesquisaTitulo(final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {
		this.consultaPosicaoTituloON.validarIntervaloDataGeracaoPesquisaTitulo(dataInicio, dataFim);
	}
	
	@Override
	public String colorirCampoSituacao(DominioSituacaoTitulo situacao) {
		return this.tituloON.colorirCampoSituacao(situacao);
	}
	
	@Override
	public PosicaoTituloVO obterPosicaoTituloVOPorNumero(final Integer numero) {
		return fcpTituloDAO.obterPosicaoTituloVOPorNumero(numero);
	}	
	
}