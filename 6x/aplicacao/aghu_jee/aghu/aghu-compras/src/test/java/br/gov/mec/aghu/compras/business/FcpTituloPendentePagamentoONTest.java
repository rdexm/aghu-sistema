package br.gov.mec.aghu.compras.business;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.contaspagar.business.GeracaoArquivoCsvRN;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloPendenteVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FcpTituloPendentePagamentoONTest extends AGHUBaseUnitTest<FcpTituloPendentePagamentoON	> {

	@Mock
	private FcpTituloPendentePagamentoRN mockedTituloPendenteRN;
	
	@Mock
	private FcpTituloPendentePagamentoON mockedTituloPendenteON;
	
	@Mock
	private GeracaoArquivoCsvRN mockedGeracaoArquivoCsvRN;
	
	private List<TituloPendenteVO> titulosRetornoList = new ArrayList<TituloPendenteVO>();

	/**
	 * Método para teste do fluxo da pesquisa de titulo pendente.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testPesquisarTituloPendentePagamento() throws ApplicationBusinessException {
		// Datas para a validação da regra de negocio. Teóricamente essas datas
		// estão de acordo com a regra. Datas != null e Datas iniciais menores
		// que as datas finais.
		Calendar calendario = Calendar.getInstance();
		calendario.set(2014, 8, 1);
		Date dtInicialVencimentoTitulo = new Date(calendario.getTimeInMillis());

		calendario.clear();
		calendario.set(2014, 8, 15);
		Date dtFinalVencimentoTitulo = new Date(calendario.getTimeInMillis());

		calendario.clear();
		calendario.set(2014, 8, 1);
		Date dtInicialEmissaoDocumentoFiscal = new Date(
				calendario.getTimeInMillis());

		calendario.clear();
		calendario.set(2014, 8, 15);
		Date dtFinalEmissaoDocumentoFiscal = new Date(
				calendario.getTimeInMillis());
		
		// Criação das expectativas de retorno, também funciona como assert.
		Mockito.when(mockedTituloPendenteON.pesquisarTituloPendentePagamento(
				Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(ScoFornecedor.class)))
				.thenReturn(titulosRetornoList);
		
		// Objeto sendo testado chamando seu método que chama a DAO.
		this.mockedTituloPendenteON.pesquisarTituloPendentePagamento(
				dtInicialVencimentoTitulo, dtFinalVencimentoTitulo,
				dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal,
				new ScoFornecedor());
	}

	/**
	 * Método de teste do fluxo da geração de arquivo CSV.
	 * 
	 * @throws ApplicationBusinessException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void testGerarCSVTituloPendentePagamento()
			throws ApplicationBusinessException {
		// Datas para a validação da regra de negocio. Teóricamente essas datas
		// estão de acordo com a regra. Datas != null e Datas iniciais menores
		// que as datas finais.
		Calendar calendario = Calendar.getInstance();
		calendario.set(2014, 8, 1);
		Date dtInicialVencimentoTitulo = new Date(calendario.getTimeInMillis());

		calendario.clear();
		calendario.set(2014, 8, 15);
		Date dtFinalVencimentoTitulo = new Date(calendario.getTimeInMillis());

		calendario.clear();
		calendario.set(2014, 8, 1);
		Date dtInicialEmissaoDocumentoFiscal = new Date(
				calendario.getTimeInMillis());

		calendario.clear();
		calendario.set(2014, 8, 15);
		Date dtFinalEmissaoDocumentoFiscal = new Date(calendario.getTimeInMillis());

		ArquivoURINomeQtdVO arquivoURINomeQtdVO = new ArquivoURINomeQtdVO(null, "URI de teste", 10, 10);
		
		// Criação das expectativas de retorno, também funciona como assert.
		Mockito.when(mockedTituloPendenteON.gerarCSVTituloPendentePagamento(
				Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(ScoFornecedor.class)))
				.thenReturn(arquivoURINomeQtdVO);
		
		Mockito.when(mockedTituloPendenteON.pesquisarTituloPendentePagamento(
				Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(ScoFornecedor.class)))
				.thenReturn(titulosRetornoList);		
		
		
		// Objeto sendo testado chamando seu método.
		this.mockedTituloPendenteON.gerarCSVTituloPendentePagamento(
				dtInicialVencimentoTitulo, dtFinalVencimentoTitulo,
				dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal,
				new ScoFornecedor());
	}

}
