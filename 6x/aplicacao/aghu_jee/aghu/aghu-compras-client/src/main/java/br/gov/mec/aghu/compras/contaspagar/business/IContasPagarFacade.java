package br.gov.mec.aghu.compras.contaspagar.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaTitulosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroPesquisaPosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloProgramadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloVO;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.estoque.vo.SceBoletimOcorrenciasVO;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Interface com os métodos do {@link ContasPagarFacade}.
 *
 */
public interface IContasPagarFacade extends Serializable{
	
	

	/**
	 * Método para preencher os campos da tela de títulos.
	 * @param parametros	Mapa com os parâmetros da consulta.
	 * @return	Uma coleção com métodos de transporte de returno com o resultado da consulta.
	 */
	List<TituloVO> pesquisarTitulos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroConsultaTitulosVO filtros) throws BaseException;
	
	Long pesquisarTitulosCount(FiltroConsultaTitulosVO filtro) throws BaseException;
	
	/**
	 * Método para preencher as suggestionBox de verbas da tela .
	 * @return	Uma coleção de {@link FsoVerbaGestao}.
	 */
	public List<FsoVerbaGestao> listarVerbaGestaoPorSituacaoSuggestionBox(final String strPesquisa);
	
	/**
	 * Método para obter a quantidade de registros que a consulta para o suggestion box retorna.
	 * @return	Um númerico que representa o número de registros.
	 */
	public Long countListarVerbaGestaoPorSituacao(final String strPesquisa);
	
	/**
	 * Método para preencher as suggestionBox de despesas da tela .
	 * @return	Uma coleção de {@link FsoNaturezaDespesa}.
	 */
	public List<FsoNaturezaDespesa> listarNaturezaDespesaPorSituacaoSuggestionBox(final String strPesquisa);
	
	/**
	 * Método para obter a quantidade de registros que a consulta para o suggestion box retorna.
	 * @return	Um númerico que representa o número de registros.
	 */
	public Long countListarNaturezaDespesaPorSituacao(final String strPesquisa);
	
	/**
	 * Método para preencher as suggestionBox de despesas da tela .
	 * @return	Uma coleção de {@link FcpTipoDocumentoPagamento}.
	 */
	public List<FcpTipoDocumentoPagamento> listarDocumentosPorSituacaoSuggestionBox(final String strPesquisa);
	
	/**
	 * Método para obter a quantidade de registros que a consulta para o suggestion box retorna.
	 * @return	Um númerico que representa o número de registros.
	 */
	public Long countListarDocumentosPorSituacao(final String strPesquisa);
	
	
	public List<TituloProgramadoVO> obterListaPagamentosProgramados(Date dataPagamento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws BaseException;
	
	public Long pesquisarPagamentosProgramadosCount(Date dataPagamento) throws BaseException;
	
	/**
	 * Método responsável por atualizar a data de pagamento do título.
	 * @param tituloSeq
	 * @param dataPagamento
	 * @param validarData Indica se deve validar a data de pagamento, quando for setar uma data de pagamento.
	 * @throws ApplicationBusinessException
	 */
	public void alterarTituloPagamento(Integer tituloSeq, Date dataPagamento, boolean validarData) throws ApplicationBusinessException;
	
	/**
	 * Pesquisa tributos da nota de recebimento de um títlulo
	 * @param notaRecebimentoNumero
	 * @return
	 */
	public List<String> pesquisarTributosNotaRecebimentoTitulo(Integer notaRecebimentoNumero);
	
	/**
	 * Obtém os dados do boletim de ocorrência de um título
	 * @param notaRecebimentoNumero
	 * @return
	 */
	public SceBoletimOcorrenciasVO obterDadosBoletimTitulo(Integer notaRecebimentoNumero);
	
	/**
	 * Pesquisa movimentação de títulos
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	public List<PosicaoTituloVO> pesquisarPosicaoTitulo(Integer firstResult, Integer maxResult, final FiltroPesquisaPosicaoTituloVO filtro);

	/**
	 * Contabiliza resultados na pesquisa movimentação de títulos
	 * 
	 * @param filtro
	 * @return
	 */
	Long pesquisarPosicaoTituloCount(final FiltroPesquisaPosicaoTituloVO filtro);
	
	List<DatasVencimentosFornecedorVO> pesquisarFornecedoresIrregularidadeFiscal(Date dataInicial, Date dataFinal, Integer numero) throws ApplicationBusinessException;
	void enviarEmailFornecedoresIrregularidadeFiscal(List<DatasVencimentosFornecedorVO> listaFornecedores) throws ApplicationBusinessException;

	/**
	 * Valida o intervalo máximo da pesquisa na posição de títulos
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @throws ApplicationBusinessException
	 */
	void validarIntervaloDataGeracaoPesquisaTitulo(final Date dataInicio, final Date dataFim) throws ApplicationBusinessException;

	/**
	 * Preenche cor de fundo do campo situação
	 * 
	 * @param situacao
	 * @param tabelaTitulo
	 *            Tabela título ou tabela de pagamentos
	 * @return
	 */
	String colorirCampoSituacao(DominioSituacaoTitulo situacao);
	
	/**
	 * Obtém o VO da posição do título através do número 
	 * @param numero
	 * @return
	 */
	PosicaoTituloVO obterPosicaoTituloVOPorNumero(final Integer numero);
	
}
