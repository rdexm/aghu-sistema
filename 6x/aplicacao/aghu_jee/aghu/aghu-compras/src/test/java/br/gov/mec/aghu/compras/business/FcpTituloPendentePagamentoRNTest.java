package br.gov.mec.aghu.compras.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.compras.contaspagar.vo.TituloPendenteVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class FcpTituloPendentePagamentoRNTest extends AGHUBaseUnitTest<FcpTituloPendentePagamentoRN> {

	private FcpTituloPendentePagamentoRN tituloPendenteRN;
	private List<TituloPendenteVO> titulosRetorno;

	/**
	 * Inicializa as variaveis necessárias.
	 */
//	@Before
//	public void setUp() throws Exception {
//
//		// contexto dos mocks, usado para criar os mocks e definir a expectativa
//		// de chamada dos métodos.
//		this.mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//		this.titulosRetorno = new ArrayList<TituloPendenteVO>();
//		this.mockingContext.mock(FcpTituloDAO.class);
//		this.mockingContext
//				.mock(GeracaoArquivoCsvRN.class);
//		this.tituloPendenteRN = this.mockingContext
//				.mock(FcpTituloPendentePagamentoRN.class);		
//	}

	/**
	 * Método para a liberação das variaveis.
	 * 
	 * @throws Exception
	 */
//	@After
//	public void tearDown() throws Exception {
//		this.mockingContext = null;
//	}

	/**
	 * Método que testa a pesquisa de títulos pendentes seguindo o caminho ideal
	 * das datas.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testPesquisarTituloPendentePagamento()
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
		Date dtFinalEmissaoDocumentoFiscal = new Date(
				calendario.getTimeInMillis());


		// Criação das expectativas de retorno, também funciona como assert.
//		this.mockingContext.checking(new Expectations() {
//			{
//				oneOf(tituloPendenteRN).pesquisarTituloPendentePagamento(
//						with(any(Date.class)), with(any(Date.class)),
//						with(any(Date.class)), with(any(Date.class)),
//						with(any(ScoFornecedor.class)));
//				will(returnValue(titulosRetorno));
//			}
//		});

		// Objeto sendo testado chamando seu método que chama a DAO.
		this.tituloPendenteRN.pesquisarTituloPendentePagamento(
				dtInicialVencimentoTitulo, dtFinalVencimentoTitulo,
				dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal,
				new ScoFornecedor());
	}
}
