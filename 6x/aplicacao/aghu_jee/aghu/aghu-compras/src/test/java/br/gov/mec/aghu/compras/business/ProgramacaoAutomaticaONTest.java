package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.DadosItemAptoProgramacaoEntregaVO;
import br.gov.mec.aghu.estoque.vo.GrupoMaterialNumeroSolicitacaoVO;
import br.gov.mec.aghu.estoque.vo.QuantidadesVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Testes de {@link ProgramacaoAutomaticaON}
 * 
 * @author luismoura
 * 
 */
public class ProgramacaoAutomaticaONTest extends AGHUBaseUnitTest<ProgramacaoAutomaticaON> {

	// FACHADAS
	@Mock
	private IParametroFacade mockedParametroFacade = null;
	@Mock
	private IEstoqueFacade mockedEstoqueFacade = null;

	// DAOs
	@Mock
	private ScoFornecedorDAO mockedScoFornecedorDAO = null;
	@Mock
	private ScoMaterialDAO mockedScoMaterialDAO = null;
	@Mock
	private ScoItemAutorizacaoFornDAO mockedScoItemAutorizacaoFornDAO = null;
	@Mock
	private ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO mockedScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO = null;


	// INICIO DOS TESTES
	@Test
	public void obterParametro01Test() throws Exception {
		this.testarObterParametro(AghuParametrosEnum.P_NDIAS_ENTREGA_ANTES_VENCIMENTO, null, new BigDecimal(3), null);
	}

	@Test
	public void obterParametro02Test() throws Exception {
		this.testarObterParametro(AghuParametrosEnum.P_PERCENTUAL_REPOSICAO_ITENS_ESPACO_FISICO, null, new BigDecimal(10), null);
	}

	@Test
	public void obterParametro03Test() throws Exception {
		this.testarObterParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO, null, new BigDecimal(1), null);
	}

	@Test
	public void obterParametro04Test() throws Exception {
		this.testarObterParametro(AghuParametrosEnum.P_COMPETENCIA, DateUtil.obterData(2012, 9, 1), null, null);
	}

	@Test
	public void obterParametro05Test() throws Exception {
		this.testarObterParametro(AghuParametrosEnum.P_ALMOX_UNICO, null, null, "S");
	}

	@Test
	public void obterParametro06Test() throws Exception {
		this.testarObterParametro(AghuParametrosEnum.P_ALMOX_REFERENCIA, null, new BigDecimal(1), null);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void obterNumeroDiasEntregueAntesVencimentoTest01() throws Exception {
		this.testarObterNumeroDiasEntregueAntesVencimento(null, null);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void obterNumeroDiasEntregueAntesVencimentoTest02() throws Exception {
		AghParametros parametro = new AghParametros();
		this.testarObterNumeroDiasEntregueAntesVencimento(parametro, null);
	}

	@Test
	public void obterNumeroDiasEntregueAntesVencimentoTest03() throws Exception {
		Integer expected = 10;
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal(expected));
		this.testarObterNumeroDiasEntregueAntesVencimento(parametro, expected);
	}


	
	// testarObterFornecedorPadrao - Programação automática de Parcelas AF

	@Test(expected = ApplicationBusinessException.class)
	public void obterFornecedorPadraoTest01() throws Exception {
		this.testarObterFornecedorPadrao(null, null);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void obterFornecedorPadraoTest02() throws Exception {
		AghParametros parametro = new AghParametros();
		this.testarObterFornecedorPadrao(parametro, null);
	}

	@Test
	public void obterFornecedorPadraoTest03() throws Exception {
		Integer expected = 10;
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal(expected));
		this.testarObterFornecedorPadrao(parametro, expected);
	}

	
	
	// obterDiaFavoravel - Programação automática de Parcelas AF

	@Test
	public void obterDiaFavoravelTest01() throws Exception {
		Byte diaFavoravelFornecedor = null;
		DominioDiaSemanaMes diaFavoravelMaterial = null;
		this.testarObterDiaFavoravel(diaFavoravelFornecedor, diaFavoravelMaterial, null);
	}

	@Test
	public void obterDiaFavoravelTest02() throws Exception {
		Byte diaFavoravelFornecedor = (byte) 1;
		DominioDiaSemanaMes diaFavoravelMaterial = null;
		this.testarObterDiaFavoravel(diaFavoravelFornecedor, diaFavoravelMaterial, diaFavoravelFornecedor.intValue());
	}

	@Test
	public void obterDiaFavoravelTest03() throws Exception {
		Byte diaFavoravelFornecedor = null;
		DominioDiaSemanaMes diaFavoravelMaterial = DominioDiaSemanaMes.DOIS;
		this.testarObterDiaFavoravel(diaFavoravelFornecedor, diaFavoravelMaterial, diaFavoravelMaterial.getCodigo());
	}

	@Test
	public void obterDiaFavoravelTest04() throws Exception {
		Byte diaFavoravelFornecedor = (byte) 1;
		DominioDiaSemanaMes diaFavoravelMaterial = DominioDiaSemanaMes.DOIS;
		this.testarObterDiaFavoravel(diaFavoravelFornecedor, diaFavoravelMaterial, diaFavoravelFornecedor.intValue());
	}

	
	
	// obterDataDiaFavoravel - Programação automática de Parcelas AF

	@Test
	public void obterDataDiaFavoravelTest01() throws Exception {

		Date dataPrimeiraParcela = DateUtil.obterData(2014, Calendar.MAY, 20);
		Integer diaFavoravel = Calendar.SUNDAY;
		Date expected = DateUtil.obterData(2014, Calendar.MAY, 18);

		this.testarObterDataDiaFavoravel(dataPrimeiraParcela, diaFavoravel, expected);
	}

	@Test
	public void obterDataDiaFavoravelTest02() throws Exception {

		Date dataPrimeiraParcela = DateUtil.obterData(2014, Calendar.MAY, 2);
		Integer diaFavoravel = Calendar.WEDNESDAY;
		Date expected = DateUtil.obterData(2014, Calendar.APRIL, 30);

		this.testarObterDataDiaFavoravel(dataPrimeiraParcela, diaFavoravel, expected);
	}

	@Test
	public void obterDataDiaFavoravelTest03() throws Exception {

		Date dataPrimeiraParcela = DateUtil.obterData(2014, Calendar.JANUARY, 1);
		Integer diaFavoravel = Calendar.TUESDAY;
		Date expected = DateUtil.obterData(2013, Calendar.DECEMBER, 31);

		this.testarObterDataDiaFavoravel(dataPrimeiraParcela, diaFavoravel, expected);
	}

	
	
	// obterCompetenciaPadrao - Programação automática de Parcelas AF

	@Test(expected = ApplicationBusinessException.class)
	public void obterCompetenciaPadraoTest01() throws Exception {
		this.testarObterCompetenciaPadrao(null, null);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void obterCompetenciaPadraoTest02() throws Exception {
		AghParametros parametro = new AghParametros();
		this.testarObterCompetenciaPadrao(parametro, null);
	}

	@Test
	public void obterCompetenciaPadraoTest03() throws Exception {
		Date expected = DateUtil.truncaData(new Date());
		AghParametros parametro = new AghParametros();
		parametro.setVlrData(expected);
		this.testarObterCompetenciaPadrao(parametro, expected);
	}

	
	
	// obterParametroAlmoxUnico - Programação automática de Parcelas AF

	@Test(expected = ApplicationBusinessException.class)
	public void obterParametroAlmoxUnicoTest01() throws Exception {
		this.testarObterParametroAlmoxUnico(null, null);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void obterParametroAlmoxUnicoTest02() throws Exception {
		AghParametros parametro = new AghParametros();
		this.testarObterParametroAlmoxUnico(parametro, null);
	}

	@Test
	public void obterParametroAlmoxUnicoTest03() throws Exception {
		AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("S");
		this.testarObterParametroAlmoxUnico(parametro, true);
	}

	@Test
	public void obterParametroAlmoxUnicoTest04() throws Exception {
		AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("s");
		this.testarObterParametroAlmoxUnico(parametro, true);
	}

	@Test
	public void obterParametroAlmoxUnicoTest05() throws Exception {
		AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("N");
		this.testarObterParametroAlmoxUnico(parametro, false);
	}

	@Test
	public void obterParametroAlmoxUnicoTest06() throws Exception {
		AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("X");
		this.testarObterParametroAlmoxUnico(parametro, false);
	}

	
	
	// obterPercentualReposicaoItensEspacoFisico - Programação automática de Parcelas AF

	@Test(expected = ApplicationBusinessException.class)
	public void obterPercentualReposicaoItensEspacoFisicoTest01() throws Exception {
		this.testarObterPercentualReposicaoItensEspacoFisico(null, null);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void obterPercentualReposicaoItensEspacoFisicoTest02() throws Exception {
		AghParametros parametro = new AghParametros();
		this.testarObterPercentualReposicaoItensEspacoFisico(parametro, null);
	}

	@Test
	public void obterPercentualReposicaoItensEspacoFisicoTest03() throws Exception {
		BigDecimal expected = BigDecimal.TEN;
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(expected);
		this.testarObterPercentualReposicaoItensEspacoFisico(parametro, expected);
	}

	
	
	// obterAlmoxarifadoReferencia - Programação automática de Parcelas AF

	@Test(expected = ApplicationBusinessException.class)
	public void obterAlmoxarifadoReferenciaTest01() throws Exception {
		this.testarObterAlmoxarifadoReferencia(null, null);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void obterAlmoxarifadoReferenciaTest02() throws Exception {
		AghParametros parametro = new AghParametros();
		this.testarObterAlmoxarifadoReferencia(parametro, null);
	}

	@Test
	public void obterAlmoxarifadoReferenciaTest03() throws Exception {
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.TEN);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		
		SceAlmoxarifado expected = new SceAlmoxarifado();
		expected.setSeq(parametro.getVlrNumerico().shortValue());
		
		this.testarObterAlmoxarifadoReferencia(parametro, expected);
	}

	
	
	// obterSaldoNaoAssinado - Programação automática de Parcelas AF

	//@Test
	public void obterSaldoNaoAssinadoTest01() throws Exception {
		this.testarObterSaldoNaoAssinado(null, null, 0);
	}

	@Test
	public void obterSaldoNaoAssinadoTest02() throws Exception {
		this.testarObterSaldoNaoAssinado(0, 0, 0);
	}

	@Test
	public void obterSaldoNaoAssinadoTest03() throws Exception {
		this.testarObterSaldoNaoAssinado(100, 50, 50);
	}

	@Test
	public void obterSaldoNaoAssinadoTest04() throws Exception {
		this.testarObterSaldoNaoAssinado(50, 100, -50);
	}

	
	
	// obterSaldoAdiantamentoAF - Programação automática de Parcelas AF

	@Test
	public void obterSaldoAdiantamentoAFTest01() throws Exception {
		this.testarObterSaldoAdiantamentoAF(new QuantidadesVO(1000L, 500L), 500);
	}

	@Test
	public void obterSaldoAdiantamentoAFTest02() throws Exception {
		this.testarObterSaldoAdiantamentoAF(new QuantidadesVO(500L, 1000L), -500);
	}

	@Test
	public void obterSaldoAdiantamentoAFTest03() throws Exception {
		this.testarObterSaldoAdiantamentoAF(new QuantidadesVO(0L, 0L), 0);
	}

	@Test
	public void obterSaldoAdiantamentoAFTest04() throws Exception {
		this.testarObterSaldoAdiantamentoAF(new QuantidadesVO(null, null), 0);
	}

	@Test
	public void obterSaldoAdiantamentoAFTest05() throws Exception {
		this.testarObterSaldoAdiantamentoAF(null, 0);
	}

	@Test
	public void obterSaldoAdiantamentoAFTest06() throws Exception {
		this.testarObterSaldoAdiantamentoAF(new QuantidadesVO(1000L, null), 1000);
	}

	@Test
	public void obterSaldoAdiantamentoAFTest07() throws Exception {
		this.testarObterSaldoAdiantamentoAF(new QuantidadesVO(null, 1000L), -1000);
	}

	
	
	// ObterQuantidadeItemRecebProvisorio - Programação automática de Parcelas AF

	@Test
	public void obterQuantidadeItemRecebProvisorioTest01() throws Exception {
		this.testarObterQuantidadeItemRecebProvisorio(1000l, 1000);
	}

	@Test
	public void obterQuantidadeItemRecebProvisorioTest02() throws Exception {
		this.testarObterQuantidadeItemRecebProvisorio(null, 0);
	}

	
	
	// obterSaldoTotal - Programação automática de Parcelas AF

	@Test
	public void obterSaldoTotalTest01() throws Exception {
		this.testarObterSaldoTotal(new QuantidadesVO(10L, 20L), 30);
	}

	@Test
	public void obterSaldoTotalTest02() throws Exception {
		this.testarObterSaldoTotal(new QuantidadesVO(null, 20L), 20);
	}

	@Test
	public void obterSaldoTotalTest03() throws Exception {
		this.testarObterSaldoTotal(new QuantidadesVO(10L, null), 10);
	}

	@Test
	public void obterSaldoTotalTest04() throws Exception {
		this.testarObterSaldoTotal(new QuantidadesVO(null, null), 0);
	}

	@Test
	public void obterSaldoTotalTest05() throws Exception {
		this.testarObterSaldoTotal(null, 0);
	}

	
	
	// obterClassificacaoABC - Programação automática de Parcelas AF

	@Test
	public void obterClassificacaoABCTest01() throws Exception {
		this.testarObterClassificacaoABC(DominioClassifABC.A);
	}

	@Test
	public void obterClassificacaoABCTest02() throws Exception {
		this.testarObterClassificacaoABC(DominioClassifABC.B);
	}

	@Test
	public void obterClassificacaoABCTest03() throws Exception {
		this.testarObterClassificacaoABC(DominioClassifABC.C);
	}

	
	
	// obterDadosAutorizacaoFornecedor - Programação automática de Parcelas AF

	@Test
	public void obterDadosAutorizacaoFornecedorTest01() throws Exception {
		this.testarObterDadosAutorizacaoFornecedor(1, (short) 2, 3d);
	}

	@Test
	public void obterDadosAutorizacaoFornecedorTest02() throws Exception {
		this.testarObterDadosAutorizacaoFornecedor(null, null, null);
	}

	
	
	// obterFatorConversao - Programação automática de Parcelas AF

	@Test
	public void obterFatorConversaoTest01() throws Exception {
		final ScoItemAutorizacaoForn scoItemAutorizacaoForn = new ScoItemAutorizacaoForn();
		scoItemAutorizacaoForn.setFatorConversaoForn(1);
		this.testarObterFatorConversao(scoItemAutorizacaoForn, 1);
	}

	@Test
	public void obterFatorConversaoTest02() throws Exception {
		final ScoItemAutorizacaoForn scoItemAutorizacaoForn = new ScoItemAutorizacaoForn();
		scoItemAutorizacaoForn.setFatorConversaoForn(5);
		this.testarObterFatorConversao(scoItemAutorizacaoForn, 5);
	}

	@Test
	public void obterFatorConversaoTest03() throws Exception {
		this.testarObterFatorConversao(new ScoItemAutorizacaoForn(), 1);
	}

	@Test
	public void obterFatorConversaoTest04() throws Exception {
		this.testarObterFatorConversao(null, 1);
	}

	
	
	// obterDataEnezimaParcela - Programação automática de Parcelas AF

	@Test
	public void obterDataEnezimaParcelaTest01() throws Exception {
		Date dataRef = DateUtil.truncaData(new Date());
		this.testarObterDataEnezimaParcela(dataRef, 5, 1, dataRef);
	}

	@Test
	public void obterDataEnezimaParcelaTest02() throws Exception {
		Date dataRef = DateUtil.obterData(2014, 1, 1);
		Date dataExp = DateUtil.obterData(2014, 1, 6);
		this.testarObterDataEnezimaParcela(dataRef, 5, 2, dataExp);
	}

	@Test
	public void obterDataEnezimaParcelaTest03() throws Exception {
		Date dataRef = DateUtil.obterData(2014, 1, 1);
		Date dataExp = DateUtil.obterData(2014, 1, 7);
		this.testarObterDataEnezimaParcela(dataRef, 3, 3, dataExp);
	}

	
	
	// obterDadosItemApto - Programação automática de Parcelas AF

	@Test
	public void obterDadosItemAptoTest01() throws Exception {
		this.testarObterDadosItemApto(1, true, 2, 3, true, 4, true, 6, 7, 8, true, 9, true, true, true, 11);
	}

	@Test
	public void obterDadosItemAptoTest02() throws Exception {
		this.testarObterDadosItemApto(1, true, 2, 3, true, 4, false, null, 7, 8, true, 9, true, true, null, null);
	}

	@Test
	public void obterDadosItemAptoTest03() throws Exception {
		this.testarObterDadosItemApto(1, false, 2, 3, true, 4, false, null, 7, 8, true, 9, true, true, null, null);
	}

	@Test
	public void obterDadosItemAptoTest04() throws Exception {
		this.testarObterDadosItemApto(1, false, 2, 3, true, 4, false, null, 7, 8, false, 9, true, true, null, null);
	}

	@Test
	public void obterDadosItemAptoTest05() throws Exception {
		this.testarObterDadosItemApto(1, false, 2, 3, true, 4, false, null, 7, 8, false, 9, false, true, true, null);
	}

	@Test
	public void obterDadosItemAptoTest06() throws Exception {
		this.testarObterDadosItemApto(1, false, 2, 3, true, 4, false, null, 7, 8, false, 9, true, false, true, null);

	}

	@Test
	public void obterDadosItemAptoTest07() throws Exception {
		this.testarObterDadosItemApto(1, false, 2, 3, true, 4, false, null, 7, 8, false, 9, false, false, true, null);
	}

	@Test
	public void obterDadosItemAptoTest08() throws Exception {
		this.testarObterDadosItemApto(1, false, 2, 3, true, 4, false, null, 7, 8, false, 9, false, false, true, 0);
	}

	@Test
	public void obterDadosItemAptoTest09() throws Exception {
		this.testarObterDadosItemApto(1, false, 2, 3, false, 4, false, null, 7, 8, false, 9, false, false, true, 0);
	}

	
	
	// MÉTODOS AUXILIARES DE TESTES

	private void testarObterParametro(final AghuParametrosEnum nome, final Date vlrData, final BigDecimal vlrNumerico, final String vlrTexto) throws Exception {
		AghParametros param = new AghParametros(vlrData);
		param.setVlrNumerico(vlrNumerico);
		param.setVlrTexto(vlrTexto);
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(nome)).thenReturn(param);
		AghParametros result = systemUnderTest.obterParametro(nome);
		
		org.junit.Assert.assertEquals(vlrData, result.getVlrData());
		Assert.assertEquals(vlrData, result.getVlrData());
		Assert.assertEquals(vlrNumerico, result.getVlrNumerico());
		Assert.assertEquals(vlrTexto, result.getVlrTexto());
	}

	private void testarObterNumeroDiasEntregueAntesVencimento(final AghParametros parametro, final Integer expected) throws Exception {
		Mockito.when(systemUnderTest.obterParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		Integer actual = systemUnderTest.obterNumeroDiasEntregueAntesVencimento();
		Assert.assertEquals(expected, actual);
	}

	private void testarObterFornecedorPadrao(final AghParametros parametro, final Integer expected) throws Exception {
		Mockito.when(systemUnderTest.obterParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		Integer actual = systemUnderTest.obterFornecedorPadrao();
		Assert.assertEquals(expected, actual);
	}

	private void testarObterDiaFavoravel(final Byte diaFavoravelFornecedor, final DominioDiaSemanaMes diaFavoravelMaterial, final Integer expected) throws Exception {
		Mockito.when(mockedScoFornecedorDAO.buscarDiaFavoravel(Mockito.anyInt())).thenReturn(diaFavoravelFornecedor);
		Mockito.when(mockedScoMaterialDAO.buscarDiaFavoravel(Mockito.anyInt())).thenReturn(diaFavoravelMaterial);

		Integer actual = systemUnderTest.obterDiaFavoravel(10, 10);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterDataDiaFavoravel(final Date dataPrimeiraParcela, final Integer diaFavoravel, final Date expected) throws Exception {
		Calendar calendarPrimeiraParcela = Calendar.getInstance();
		calendarPrimeiraParcela.setTime(dataPrimeiraParcela);
		int diaSemanaEntrega = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);

		Date actual = systemUnderTest.obterDataDiaFavoravel(dataPrimeiraParcela, diaFavoravel, diaSemanaEntrega);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterCompetenciaPadrao(final AghParametros parametro, final Date expected) throws Exception {
		Mockito.when(systemUnderTest.obterParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		Date actual = systemUnderTest.obterCompetenciaPadrao();
		Assert.assertEquals(expected, actual);
	}

	private void testarObterParametroAlmoxUnico(final AghParametros parametro, final Boolean expected) throws Exception {
		Mockito.when(systemUnderTest.obterParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		Boolean actual = systemUnderTest.obterParametroAlmoxUnico();
		Assert.assertEquals(expected, actual);
	}

	private void testarObterPercentualReposicaoItensEspacoFisico(final AghParametros parametro, final BigDecimal expected) throws Exception {
		Mockito.when(systemUnderTest.obterParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		BigDecimal actual = systemUnderTest.obterPercentualReposicaoItensEspacoFisico();
		Assert.assertEquals(expected, actual);
	}

	private void testarObterAlmoxarifadoReferencia(final AghParametros parametro, final SceAlmoxarifado expected) throws Exception {
		Mockito.when(mockedEstoqueFacade.obterSceAlmoxarifadoPorChavePrimaria(Mockito.anyShort())).thenReturn(expected);
		
		SceAlmoxarifado actual = systemUnderTest.obterAlmoxarifadoReferencia();
		Assert.assertEquals(expected, actual);
	}

	private void testarObterSaldoAdiantamentoAF(final QuantidadesVO quantidades, final Integer expected) throws Exception {
		Mockito.when(mockedEstoqueFacade.obterQuantidadeQuantidadeDevolvidaSceItemEntrSaidSemLicitacao(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(quantidades);
		
		Integer actual = systemUnderTest.obterSaldoAdiantamentoAF(1, 1);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterQuantidadeItemRecebProvisorio(final Long quantidade, final Integer expected) throws Exception {
		Mockito.when(mockedEstoqueFacade.obterQuantidadeRecParcelasSceItemRecebProvisorio(Mockito.anyInt())).thenReturn(quantidade);
		
		Integer actual = systemUnderTest.obterQuantidadeItemRecebProvisorio(1);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterSaldoTotal(final QuantidadesVO saldos, final Integer expected) throws Exception {
		AghParametros parametroRetorno = new AghParametros();
		parametroRetorno.setVlrTexto("S");
		Mockito.when(systemUnderTest.obterParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametroRetorno);
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametroRetorno);
		
		Boolean paramUnico = systemUnderTest.obterParametroAlmoxUnico();
		Assert.assertEquals(true, paramUnico);
		
		//Mockito.when(systemUnderTest.obterParametroAlmoxUnico()).thenReturn(true);
		
		Mockito.when(mockedEstoqueFacade.obterQtdDisponivelQtdBloqueadaSceValidade(
				Mockito.anyInt(), Mockito.anyBoolean(), Mockito.any(Date.class), Mockito.anyBoolean()))
				.thenReturn(saldos);
		
		Integer actual = systemUnderTest.obterSaldoTotal(1, true, new Date());
		Assert.assertEquals(expected, actual);
	}

	private void testarObterClassificacaoABC(final DominioClassifABC expected) throws Exception {
		Date data = new Date();
		AghParametros param = new AghParametros();
		param.setVlrData(data);
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		Mockito.when(systemUnderTest.obterParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		systemUnderTest.obterCompetenciaPadrao();

		Mockito.when(mockedEstoqueFacade.obterClassificacaoABCMaterialSceEstoqueGeral(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(expected);
		
		DominioClassifABC actual = systemUnderTest.obterClassificacaoABC(new DadosItemAptoProgramacaoEntregaVO());
		
		Assert.assertEquals(expected, actual);
	}

	private void testarObterDadosAutorizacaoFornecedor(final Integer lctNumero, final Short nroComplemento, final Double valorUnitario) throws Exception {
		final ScoItemAutorizacaoForn scoItemAutorizacaoForn = new ScoItemAutorizacaoForn();
		ScoAutorizacaoForn scoAutorizacaoForn = new ScoAutorizacaoForn();
		ScoPropostaFornecedor scoPropostaFornecedor = new ScoPropostaFornecedor();
		ScoPropostaFornecedorId scoPropostaFornecedorId = new ScoPropostaFornecedorId();
		scoPropostaFornecedorId.setLctNumero(lctNumero);
		scoPropostaFornecedor.setId(scoPropostaFornecedorId);
		scoAutorizacaoForn.setPropostaFornecedor(scoPropostaFornecedor);
		scoAutorizacaoForn.setNroComplemento(nroComplemento);
		scoItemAutorizacaoForn.setAutorizacoesForn(scoAutorizacaoForn);
		scoItemAutorizacaoForn.setValorUnitario(valorUnitario);

		Mockito.when(mockedScoItemAutorizacaoFornDAO.obterPorChavePrimaria(Mockito.any(ScoItemAutorizacaoFornId.class)))
				.thenReturn(scoItemAutorizacaoForn);
		
		Object[] actual = systemUnderTest.obterDadosAutorizacaoFornecedor(new DadosItemAptoProgramacaoEntregaVO(1, 1));
		Assert.assertNotNull(actual);
		Assert.assertEquals(lctNumero, actual[0]);
		Assert.assertEquals(nroComplemento, actual[1]);
		Assert.assertEquals(valorUnitario, actual[2]);
	}

	private void testarObterFatorConversao(final ScoItemAutorizacaoForn scoItemAutorizacaoForn, final Integer expected) throws Exception {
		Mockito.when(mockedScoItemAutorizacaoFornDAO.buscarScoItemAutorizacaoForn(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyInt()))
				.thenReturn(scoItemAutorizacaoForn);
		
		Integer actual = systemUnderTest.obterFatorConversao(1, (short) 1, 1);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterDataEnezimaParcela(final Date dataPrimeiraParcela, final Integer intervaloParcelas, final Integer numeroParcela, final Date expected)
			throws Exception {
		Date actual = systemUnderTest.obterDataEnezimaParcela(dataPrimeiraParcela, new BigDecimal(intervaloParcelas), numeroParcela);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterDadosItemApto(final Integer fornecedorPadrao, final boolean matNumSolic, final Integer codigoMaterial, final Integer slcNumero,
			final boolean hasEstoqueAlmox, final Integer estoqueAlmoxSeq, final boolean indControleValidade, final Integer qtdePontoPedido,
			final Integer qtdeEstqMax, final Integer qtdeEspacoArmazena, final boolean hasFornecedor, final Integer numeroFornecedor, final boolean hasMaterial,
			final boolean hasUtilizaEspaco, final Boolean indUtilizaEspaco, final Integer tempoReposicao) throws Exception {

		final SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = new SceEstoqueAlmoxarifado();
		sceEstoqueAlmoxarifado.setSeq(estoqueAlmoxSeq);
		sceEstoqueAlmoxarifado.setQtdePontoPedido(qtdePontoPedido);
		sceEstoqueAlmoxarifado.setQtdeEstqMax(qtdeEstqMax);
		sceEstoqueAlmoxarifado.setQtdeEspacoArmazena(qtdeEspacoArmazena);
		sceEstoqueAlmoxarifado.setIndControleValidade(indControleValidade);

		if (hasFornecedor) {
			ScoFornecedor fornecedor = new ScoFornecedor();
			fornecedor.setNumero(numeroFornecedor);
			sceEstoqueAlmoxarifado.setFornecedor(fornecedor);
		}

		if (hasMaterial) {
			ScoMaterial material = new ScoMaterial();
			material.setCodigo(codigoMaterial);
			if (hasUtilizaEspaco) {
				material.setIndUtilizaEspacoFisico(indUtilizaEspaco);
			}
			sceEstoqueAlmoxarifado.setMaterial(material);
		}

		sceEstoqueAlmoxarifado.setTempoReposicao(tempoReposicao);

		sceEstoqueAlmoxarifado.setQtdePontoPedido(qtdePontoPedido);

		Mockito.when(mockedEstoqueFacade.obterSceEstoqueAlmoxarifadoPorMaterialFornecedor(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(hasEstoqueAlmox ? sceEstoqueAlmoxarifado : null);
		
		Mockito.when(mockedEstoqueFacade.obterCodigoGrupoMaterialNumeroSolicitacaoSceItemRecebProvisorio(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(matNumSolic ? new GrupoMaterialNumeroSolicitacaoVO(codigoMaterial, null, slcNumero) : null);
		
		AghParametros param = new AghParametros(new Date());
		param.setVlrTexto("S");
		param.setVlrNumerico(BigDecimal.ONE);
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)).thenReturn(param);
		AghParametros result = systemUnderTest.obterParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);

		int expected = systemUnderTest.obterFornecedorPadrao();
		
		Assert.assertEquals(result.getVlrNumerico().intValue(), expected);
			

		DadosItemAptoProgramacaoEntregaVO actual = systemUnderTest.obterDadosItemApto(1, 1);
		Assert.assertNotNull(actual);
		Assert.assertEquals(matNumSolic ? codigoMaterial : null, actual.getCodigoMaterial());
		Assert.assertEquals(matNumSolic ? slcNumero : null, actual.getSlcNumero());
		Assert.assertEquals(hasEstoqueAlmox ? estoqueAlmoxSeq : null, actual.getEstoqueAlmoxSeq());
		Assert.assertEquals(hasEstoqueAlmox && hasFornecedor ? numeroFornecedor : null, actual.getNumeroFornecedor());
		Assert.assertEquals((hasEstoqueAlmox && hasMaterial && hasUtilizaEspaco && indUtilizaEspaco != null ? indUtilizaEspaco.booleanValue() : false),
				actual.isIndUtilizaEspaco());
		Assert.assertEquals(hasEstoqueAlmox ? indControleValidade : false, actual.isIndControleValidade());

		Integer tempo = 1;
		if (hasEstoqueAlmox && sceEstoqueAlmoxarifado.getTempoReposicao() != null && sceEstoqueAlmoxarifado.getTempoReposicao().intValue() > 0) {
			tempo = sceEstoqueAlmoxarifado.getTempoReposicao();
		}
		Assert.assertEquals(tempo, actual.getTempoReposicao());

		BigDecimal consumoDiario = BigDecimal.ZERO;
		if (hasEstoqueAlmox && sceEstoqueAlmoxarifado.getQtdePontoPedido() != null) {
			consumoDiario = new BigDecimal(sceEstoqueAlmoxarifado.getQtdePontoPedido()).divide(new BigDecimal(tempoReposicao), 6, RoundingMode.HALF_EVEN);
		}
		Assert.assertEquals(consumoDiario, actual.getConsumoDiario());

		Assert.assertEquals(hasEstoqueAlmox ? qtdePontoPedido : null, actual.getQtdePontoPedido());
		Assert.assertEquals(hasEstoqueAlmox ? qtdeEstqMax : null, actual.getQtdeEstqMax());
		Assert.assertEquals(hasEstoqueAlmox ? qtdeEspacoArmazena : null, actual.getQtdeEspacoArmazena());
		Assert.assertEquals(fornecedorPadrao, actual.getFornecedorPadrao());
	}

	private void testarObterSaldoNaoAssinado(final Integer qtdSolicitada, final Integer totalAssinado, final Integer expected) throws Exception {
		Mockito.when(mockedScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO.buscarTotalScoItemAutorizacaoFornAssinado(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyInt()))
				.thenReturn(totalAssinado.longValue());
		
		ScoItemAutorizacaoForn scoItemAutorizacaoForn = null;
		if (qtdSolicitada != null) {
			scoItemAutorizacaoForn = new ScoItemAutorizacaoForn();
			scoItemAutorizacaoForn.setQtdeSolicitada(qtdSolicitada);
		}
		Mockito.when(mockedScoItemAutorizacaoFornDAO.buscarScoItemAutorizacaoForn(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyInt()))
				.thenReturn(scoItemAutorizacaoForn);
		
		Integer actual = systemUnderTest.obterSaldoNaoAssinado(1, (short) 1, 1);
		Assert.assertEquals(expected, actual);
	}
}