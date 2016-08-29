package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoDeCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.DadosItemAptoProgramacaoEntregaVO;
import br.gov.mec.aghu.estoque.vo.QuantidadesVO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Testes de {@link ProgramacaoAutomaticaRN}
 * 
 * @author luismoura
 * 
 */
@Ignore
public class ProgramacaoAutomaticaRNTest extends AGHUBaseUnitTest<ProgramacaoAutomaticaRN> {

	// ------------------------- FACADES -------------------------
	@Mock
	private IEstoqueFacade mockedEstoqueFacade;

	// ------------------------- DAOS -------------------------
	@Mock
	private ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO mockedScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;
	@Mock
	private ScoSolicitacaoProgramacaoEntregaDAO mockedScoSolicitacaoProgramacaoEntregaDAO;
	@Mock
	private ScoAutorizacaoFornDAO mockedScoAutorizacaoFornDAO;
	@Mock
	private ScoItemAutorizacaoFornDAO mockedScoItemAutorizacaoFornDAO;
	@Mock
	private ScoSolicitacaoDeCompraDAO mockedScoSolicitacaoDeCompraDAO;

	// ------------------------- ONs RNs -------------------------
	@Mock
	private ProgramacaoAutomaticaON mockedProgramacaoAutomaticaON;


	// ------------------------- INICIO DOS TESTES -------------------------

	// RN13 de #5554 - Programação automática de Parcelas AF

	@Test
	public void excluirParcelasNaoAssinadasTest01() throws Exception {
		this.testarExcluirParcelasNaoAssinadas(null);
	}

	@Test
	public void excluirParcelasNaoAssinadasTest02() throws Exception {
		this.testarExcluirParcelasNaoAssinadas(1);
	}

	@Test
	public void excluirParcelasNaoAssinadasTest03() throws Exception {
		this.testarExcluirParcelasNaoAssinadas(2);
	}

	@Test
	public void excluirParcelasNaoAssinadasTest04() throws Exception {
		this.testarExcluirParcelasNaoAssinadas(3);
	}

	@Test
	public void excluirParcelasNaoAssinadasTest05() throws Exception {
		this.testarExcluirParcelasNaoAssinadas(4);
	}

	// RN12 de #5554 - Programação automática de Parcelas AF
//		@Test
//		public void gravarParcelaTest() throws Exception {
//			//this.testarGravarParcela();
//		}

	// RN10 de #5554 - Programação automática de Parcelas AF

	@Test
	public void validarQuantidadePontoPedidoTest01() throws Exception {
		this.testarValidarQuantidadePontoPedido(null, false);
	}

	@Test
	public void validarQuantidadePontoPedidoTest03() throws Exception {
		this.testarValidarQuantidadePontoPedido(0, false);
	}

	@Test
	public void validarQuantidadePontoPedidoTest04() throws Exception {
		this.testarValidarQuantidadePontoPedido(10, true);
	}

	// RN02 de #5554 - Programação automática de Parcelas AF

//	@Test
//	public void obterQuantidadeReposicaoIntervaloParcelasTest01() throws Exception {
//		Boolean indUtilizaEspacoFisico = true;
//		Integer qtdeEspacoArmazena = 10;
//		Double quantidadeReposicaoExpected = 1d;
//		Double intervaloParcelas = 0.9d;
//
//		this.testarObterQuantidadeReposicaoIntervaloParcelas(indUtilizaEspacoFisico, qtdeEspacoArmazena, quantidadeReposicaoExpected, intervaloParcelas);
//	}
//
//	@Test
//	public void obterQuantidadeReposicaoIntervaloParcelasTest02() throws Exception {
//		Boolean indUtilizaEspacoFisico = true;
//		Integer qtdeEspacoArmazena = 0;
//		Double quantidadeReposicaoExpected = 100d;
//		Double intervaloParcelas = 10d;
//
//		this.testarObterQuantidadeReposicaoIntervaloParcelas(indUtilizaEspacoFisico, qtdeEspacoArmazena, quantidadeReposicaoExpected, intervaloParcelas);
//	}
//
//	@Test
//	public void obterQuantidadeReposicaoIntervaloParcelasTest03() throws Exception {
//		Boolean indUtilizaEspacoFisico = true;
//		Integer qtdeEspacoArmazena = null;
//		Double quantidadeReposicaoExpected = 100d;
//		Double intervaloParcelas = 10d;
//
//		this.testarObterQuantidadeReposicaoIntervaloParcelas(indUtilizaEspacoFisico, qtdeEspacoArmazena, quantidadeReposicaoExpected, intervaloParcelas);
//	}
//
//	@Test
//	public void obterQuantidadeReposicaoIntervaloParcelasTest04() throws Exception {
//		Boolean indUtilizaEspacoFisico = false;
//		Integer qtdeEspacoArmazena = 10;
//		Double quantidadeReposicaoExpected = 100d;
//		Double intervaloParcelas = 10d;
//
//		this.testarObterQuantidadeReposicaoIntervaloParcelas(indUtilizaEspacoFisico, qtdeEspacoArmazena, quantidadeReposicaoExpected, intervaloParcelas);
//	}

	// obterQuantidadeReposicaoIntervaloParcelasClassificacaoABC de #5554 - Programação automática de Parcelas AF

	@Test
	public void obterQuantidadeReposicaoIntervaloParcelasClassificacaoABCTest01() throws Exception {
		DominioClassifABC classificacaoABC = DominioClassifABC.A;
		Double quantidadeReposicaoExpected = 100d;
		Double intervaloParcelas = 10d;

		this.testarObterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(classificacaoABC, quantidadeReposicaoExpected, intervaloParcelas);
	}

	@Test
	public void obterQuantidadeReposicaoIntervaloParcelasClassificacaoABCTest02() throws Exception {
		DominioClassifABC classificacaoABC = DominioClassifABC.B;
		Double quantidadeReposicaoExpected = 200d;
		Double intervaloParcelas = 20d;

		this.testarObterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(classificacaoABC, quantidadeReposicaoExpected, intervaloParcelas);
	}

	@Test
	public void obterQuantidadeReposicaoIntervaloParcelasClassificacaoABCTest03() throws Exception {
		DominioClassifABC classificacaoABC = DominioClassifABC.C;
		Double quantidadeReposicaoExpected = 300d;
		Double intervaloParcelas = 30d;

		this.testarObterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(classificacaoABC, quantidadeReposicaoExpected, intervaloParcelas);
	}

	@Test
	public void obterQuantidadeReposicaoIntervaloParcelasClassificacaoABCTest04() throws Exception {
		this.testarObterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(null, null, null);
	}

	// RN04 de #5554 - Programação automática de Parcelas AF
	@Test
	public void obterDataPrevisaoPontoReposicaoTest01() throws Exception {
		Integer saldoInstituicao = 10;
		BigDecimal quantidadeReposicao = new BigDecimal(20);
		BigDecimal consumoDiario = new BigDecimal(2);
		Date expected = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 1);

		this.testarObterDataPrevisaoPontoReposicao(saldoInstituicao, quantidadeReposicao, consumoDiario, null, expected);
	}

	@Test
	public void obterDataPrevisaoPontoReposicaoTest02() throws Exception {
		Integer saldoInstituicao = 10;
		BigDecimal quantidadeReposicao = new BigDecimal(5);
		BigDecimal consumoDiario = new BigDecimal(2);
		Date expected = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 2);

		this.testarObterDataPrevisaoPontoReposicao(saldoInstituicao, quantidadeReposicao, consumoDiario, null, expected);
	}

	@Test
	public void obterDataPrevisaoPontoReposicaoTest03() throws Exception {
		Integer saldoInstituicao = 10;
		BigDecimal quantidadeReposicao = new BigDecimal(5);
		BigDecimal consumoDiario = new BigDecimal(2);
		Date expected = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 2);
		Object[] qtd = new Object[] { 0, 0 };

		this.testarObterDataPrevisaoPontoReposicao(saldoInstituicao, quantidadeReposicao, consumoDiario, qtd, expected);
	}

	@Test
	public void obterDataPrevisaoPontoReposicaoTest04() throws Exception {
		Integer saldoInstituicao = 10;
		BigDecimal quantidadeReposicao = new BigDecimal(5);
		BigDecimal consumoDiario = new BigDecimal(2);
		Date expected = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 2);
		Object[] qtd = new Object[] { null, null };

		this.testarObterDataPrevisaoPontoReposicao(saldoInstituicao, quantidadeReposicao, consumoDiario, qtd, expected);
	}

//	@Test
//	public void obterDataPrevisaoPontoReposicaoTest05() throws Exception {
//		Integer saldoInstituicao = 10;
//		BigDecimal quantidadeReposicao = new BigDecimal(5);
//		BigDecimal consumoDiario = new BigDecimal(2);
//		Date expected = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 3);
//		Object[] qtd = new Object[] { 2, 1 };
//
//		this.testarObterDataPrevisao(saldoInstituicao, quantidadeReposicao, consumoDiario, qtd, expected);
//	}

	private void testarObterDataPrevisao(final Integer saldoInstituicao, final BigDecimal quantidadeReposicao, final BigDecimal consumoDiario,
			final Object[] qtds, final Date expected) throws Exception {

		final DadosItemAptoProgramacaoEntregaVO dadosItemApto = new DadosItemAptoProgramacaoEntregaVO(1, 1);
		dadosItemApto.setConsumoDiario(consumoDiario);

		Mockito.when(mockedScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO.buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelas(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(qtds).thenReturn(null);
		
		Date actual = systemUnderTest.obterDataPrevisaoPontoReposicao(dadosItemApto, saldoInstituicao, quantidadeReposicao);
		Assert.assertEquals(expected, actual);
	}
	
	// RN03 de #5554 - Programação automática de Parcelas AF

//	@Test
//	public void obterDataPrimeiraParcelaTest01() throws Exception {
//		final Short prazoEntrega = (short) 2;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 1);
//		final boolean indUtilizaEspacoFisico = true;
//		final Date dataValidade = null;
//		final Integer nrDiasAntesVencimento = null;
//		final Integer diaFavoravel = null;
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), prazoEntrega.intValue());
//		final boolean entImedExpected = true;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest02() throws Exception {
//		final Short prazoEntrega = (short) 2;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = true;
//		final Date dataValidade = null;
//		final Integer nrDiasAntesVencimento = null;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		final Integer diaFavoravel = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = dataReposicao;
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest03() throws Exception {
//		final Short prazoEntrega = (short) 2;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = true;
//		final Date dataValidade = null;
//		final Integer nrDiasAntesVencimento = null;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		int day = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//		if (day == Calendar.SUNDAY) {
//			day = Calendar.SATURDAY;
//		} else {
//			day--;
//		}
//		final Integer diaFavoravel = day;
//
//		calendarPrimeiraParcela.add(Calendar.DAY_OF_YEAR, -1);
//		final Date dataDiaFavoravel = calendarPrimeiraParcela.getTime();
//
//		final Date dtExpected = dataDiaFavoravel;
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest04() throws Exception {
//		final Short prazoEntrega = (short) 2;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = true;
//		final Date dataValidade = null;
//		final Integer nrDiasAntesVencimento = null;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		int day = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//		if (day == Calendar.FRIDAY) {
//			day = Calendar.SATURDAY;
//		} else {
//			day = day - 6;
//		}
//		final Integer diaFavoravel = day;
//
//		calendarPrimeiraParcela.add(Calendar.DAY_OF_YEAR, -6);
//		final Date dataDiaFavoravel = calendarPrimeiraParcela.getTime();
//
//		final Date dtExpected = dataReposicao;
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest05() throws Exception {
//		final Short prazoEntrega = (short) 2;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = false;
//		final Date dataValidade = null;
//		final Integer nrDiasAntesVencimento = null;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		final Integer diaFavoravel = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = dataReposicao;
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest06() throws Exception {
//		final Short prazoEntrega = (short) 2;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = false;
//		final Date dataValidade = DateUtil.adicionaDias(dataReposicao, 1);
//		final Integer nrDiasAntesVencimento = null;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		final Integer diaFavoravel = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = dataReposicao;
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest07() throws Exception {
//		final Short prazoEntrega = (short) 2;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = false;
//		final Date dataValidade = DateUtil.adicionaDias(dataReposicao, -1);
//		final Integer nrDiasAntesVencimento = 9;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		final Integer diaFavoravel = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = DateUtil.adicionaDias(dataReposicao, -10);
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest08() throws Exception {
//		final Short prazoEntrega = null;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = false;
//		final Date dataValidade = DateUtil.adicionaDias(dataReposicao, -1);
//		final Integer nrDiasAntesVencimento = 9;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		final Integer diaFavoravel = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = DateUtil.adicionaDias(dataReposicao, -10);
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest09() throws Exception {
//		final Short prazoEntrega = 0;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = false;
//		final Date dataValidade = DateUtil.adicionaDias(dataReposicao, -1);
//		final Integer nrDiasAntesVencimento = 9;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		final Integer diaFavoravel = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
//
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = DateUtil.adicionaDias(dataReposicao, -10);
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}
//
//	@Test
//	public void obterDataPrimeiraParcelaTest10() throws Exception {
//		final Short prazoEntrega = 0;
//		final Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);
//		final boolean indUtilizaEspacoFisico = false;
//		final Date dataValidade = DateUtil.adicionaDias(dataReposicao, -1);
//		final Integer nrDiasAntesVencimento = 2;
//
//		Calendar calendarPrimeiraParcela = Calendar.getInstance();
//		calendarPrimeiraParcela.setTime(dataReposicao);
//		final Integer diaFavoravel = null;
//
//		final Date dataDiaFavoravel = null;
//		final Date dtExpected = DateUtil.adicionaDias(dataReposicao, -3);
//		final boolean entImedExpected = false;
//
//		this.testarObterDataPrimeiraParcela(prazoEntrega, dataReposicao, indUtilizaEspacoFisico, dataValidade, nrDiasAntesVencimento, diaFavoravel,
//				dataDiaFavoravel, dtExpected, entImedExpected);
//	}

	// RN05 de #5554 - Programação automática de Parcelas AF

	@Test
	public void obterValidadeTest01() throws Exception {
		final Date dataValidade = new Date();
		this.testarObterValidade(null, dataValidade, null);
	}

	@Test
	public void obterValidadeTest02() throws Exception {
		final Date dataValidade = new Date();
		this.testarObterValidade(false, dataValidade, null);
	}

	@Test
	public void obterValidadeTest03() throws Exception {
		final Date dataValidade = new Date();
		this.testarObterValidade(true, dataValidade, dataValidade);
	}

	// obterSaldoInstituicao - Programação automática de Parcelas AF

	@Test
	public void obterSaldoInstituicaoTest01() throws Exception {
		this.testarObterSaldoInstituicao(10, 20, 30, 60);
	}

	@Test
	public void obterSaldoInstituicaoTest02() throws Exception {
		this.testarObterSaldoInstituicao(10, 0, 0, 10);
	}

	@Test
	public void obterSaldoInstituicaoTest03() throws Exception {
		this.testarObterSaldoInstituicao(0, 20, 0, 20);
	}

	@Test
	public void obterSaldoInstituicaoTest04() throws Exception {
		this.testarObterSaldoInstituicao(0, 0, 30, 30);
	}

	@Test
	public void obterSaldoInstituicaoTest05() throws Exception {
		this.testarObterSaldoInstituicao(0, 0, 0, 0);
	}

	// RN101 de #5554 - Programação automática de Parcelas AF

	@Test
	public void validarSaldoInstituicaoTest01() throws Exception {
		this.testarValidarSaldoInstituicao(null, 100, false);
	}

	@Test
	public void validarSaldoInstituicaoTest03() throws Exception {
		this.testarValidarSaldoInstituicao(200, 100, true);
	}

	@Test
	public void validarSaldoInstituicaoTest04() throws Exception {
		this.testarValidarSaldoInstituicao(50, 100, false);
	}

	// obterQuantidadePrimeiraParcela - Programação automática de Parcelas AF

	//@Test
	public void obterQuantidadePrimeiraParcelaTest01() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(1000, 1, true, 2000, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 1010);
	}

	@Test
	public void obterQuantidadePrimeiraParcelaTest02() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(9, 1, true, 2000, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 2000);
	}

	//@Test
	public void obterQuantidadePrimeiraParcelaTest03() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(9, 1, false, 2000, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 2020);
	}

	//@Test
	public void obterQuantidadePrimeiraParcelaTest04() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(9, 4, false, 2000, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 2020);
	}

	//@Test
	public void obterQuantidadePrimeiraParcelaTest05() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(9, 1, true, null, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 2020);
	}

	//@Test
	public void obterQuantidadePrimeiraParcelaTest06() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(9, 4, true, 0, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 2020);
	}

	@Test
	public void obterQuantidadePrimeiraParcelaTest07() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(9, 4, true, 0, null, 2000);
	}

	@Test
	public void obterQuantidadePrimeiraParcelaTest08() throws Exception {
		this.testarObterQuantidadePrimeiraParcela(9, 4, true, 0, new BigDecimal[] { new BigDecimal(100), null }, 2000);
	}

	// obterQuantidadeCadaParcela - Programação automática de Parcelas AF

	@Test
	public void obterQuantidadeCadaParcelaTest01() throws Exception {
		this.testarObterQuantidadeCadaParcela(true, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 4);
	}

	//@Test
	public void obterQuantidadeCadaParcelaTest02() throws Exception {
		this.testarObterQuantidadeCadaParcela(false, new BigDecimal[] { new BigDecimal(100), BigDecimal.TEN }, 20);
	}

	@Test
	public void obterQuantidadeCadaParcelaTest03() throws Exception {
		this.testarObterQuantidadeCadaParcela(false, null, 0);
	}

	@Test
	public void obterQuantidadeCadaParcelaTest04() throws Exception {
		this.testarObterQuantidadeCadaParcela(false, new BigDecimal[] { new BigDecimal(100), null }, 0);
	}

	// obterNumeroParcelasQuantidadeUltima - Programação automática de Parcelas AF

	@Test
	public void obterNumeroParcelasQuantidadeUltimaTest01() throws Exception {
		this.testarObterNumeroParcelasQuantidadeUltima(25, 3, 3, 8, 4);
	}

	@Test
	public void obterNumeroParcelasQuantidadeUltimaTest02() throws Exception {
		this.testarObterNumeroParcelasQuantidadeUltima(26, 3, 3, 8, 5);
	}

	@Test
	public void obterNumeroParcelasQuantidadeUltimaTest03() throws Exception {
		this.testarObterNumeroParcelasQuantidadeUltima(27, 3, 3, 9, 0);
	}

	@Test
	public void obterNumeroParcelasQuantidadeUltimaTest04() throws Exception {
		this.testarObterNumeroParcelasQuantidadeUltima(28, 3, 3, 9, 4);
	}
	
	@Test
	public void obterNumeroParcelasQuantidadeUltimaTest05() throws Exception {
		this.testarObterNumeroParcelasQuantidadeUltima(28, 3, 0, 1, 0);
	}
	
	@Test
	public void obterNumeroParcelasQuantidadeUltimaTest06() throws Exception {
		this.testarObterNumeroParcelasQuantidadeUltima(3, 28, 0, 1, 3);
	}

	// gerarParcelas - Programação automática de Parcelas AF

	@Test
	public void gerarParcelasTest01() throws Exception {
		this.testarGerarParcelas(null, null);
	}

	@Test
	public void gerarParcelasTest02() throws Exception {
		this.testarGerarParcelas(null, new ArrayList<ScoItemAutorizacaoFornId>());
	}

	@Test
	public void gerarParcelasTest03() throws Exception {
		this.testarGerarParcelas(new ArrayList<Integer>(), null);
	}

	@Test
	public void gerarParcelasTest04() throws Exception {
		this.testarGerarParcelas(new ArrayList<Integer>(), new ArrayList<ScoItemAutorizacaoFornId>());
	}

	//@Test
	public void gerarParcelasTest05() throws Exception {
		List<Integer> listNumeroAFs = new ArrayList<Integer>();
		listNumeroAFs.add(1);
		this.testarGerarParcelas(listNumeroAFs, new ArrayList<ScoItemAutorizacaoFornId>());
	}

	//@Test
	public void gerarParcelasTest06() throws Exception {
		List<Integer> listNumeroAFs = new ArrayList<Integer>();
		listNumeroAFs.add(1);
		this.testarGerarParcelas(listNumeroAFs, null);
	}

	//@Test
	public void gerarParcelasTest07() throws Exception {
		List<Integer> listNumeroAFs = new ArrayList<Integer>();
		listNumeroAFs.add(1);
		List<ScoItemAutorizacaoFornId> itensAF = new ArrayList<ScoItemAutorizacaoFornId>();
		itensAF.add(new ScoItemAutorizacaoFornId());
		this.testarGerarParcelas(listNumeroAFs, itensAF);
	}

	// gerarParcelasItem - Programação automática de Parcelas AF

	//@Test
	public void gerarParcelasItemTest01() throws Exception {
		this.testarGerarParcelasItem(false, true, 3);
	}

	//@Test
	public void gerarParcelasItemTest02() throws Exception {
		this.testarGerarParcelasItem(true, false, 3);
	}

	//@Test
	public void gerarParcelasItemTest03() throws Exception {
		this.testarGerarParcelasItem(true, true, 0);
	}

	//@Test
	public void gerarParcelasItemTest04() throws Exception {
		this.testarGerarParcelasItem(true, true, null);
	}

	//@Test
	public void gerarParcelasItemTest05() throws Exception {
		this.testarGerarParcelasItem(true, true, 3);
	}

	// ------------------------- MÉTODOS AUXILIARES DE TESTES

	private void testarExcluirParcelasNaoAssinadas(final Integer teste) throws Exception {
		systemUnderTest.excluirParcelasNaoAssinadas(1, 1);
		Assert.assertTrue(true);
	}

	private void testarValidarQuantidadePontoPedido(final Integer qtdPontoPedido, final boolean expected) throws Exception {
		boolean actual = systemUnderTest.validarQuantidadePontoPedido(qtdPontoPedido);
		Assert.assertEquals(expected, actual);
	}

	@SuppressWarnings("deprecation")
	private void testarObterQuantidadeReposicaoIntervaloParcelas(final boolean indUtilizaEspacoFisico, final Integer qtdeEspacoArmazena,
			final Double quantidadeReposicaoExpected, final Double intervaloParcelasExpected) throws Exception {

		final DadosItemAptoProgramacaoEntregaVO dadosItemApto = new DadosItemAptoProgramacaoEntregaVO(1, 1);
		dadosItemApto.setIndUtilizaEspaco(indUtilizaEspacoFisico);
		dadosItemApto.setConsumoDiario(BigDecimal.TEN);
		dadosItemApto.setQtdeEspacoArmazena(qtdeEspacoArmazena);

		Mockito.when(mockedProgramacaoAutomaticaON.obterPercentualReposicaoItensEspacoFisico()).thenReturn(BigDecimal.TEN);
		
		SceAlmoxarifado sceAlmoxarifadoReferencia = new SceAlmoxarifado();
		sceAlmoxarifadoReferencia.setDiasSaldoClassA(10);

		BigDecimal[] actual = systemUnderTest.obterQuantidadeReposicaoIntervaloParcelas(dadosItemApto, DominioClassifABC.A, sceAlmoxarifadoReferencia);

		Assert.assertNotNull(actual);
		Assert.assertEquals(actual[0].doubleValue(), (double) quantidadeReposicaoExpected);
		Assert.assertEquals(actual[1].doubleValue(), (double) intervaloParcelasExpected);
	}

	private void testarObterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(final DominioClassifABC classificacaoABC, final Double quantidadeReposicaoExpected,
			final Double intervaloParcelasExpected) throws Exception {

		BigDecimal consumoDiario = BigDecimal.TEN;

		SceAlmoxarifado sceAlmoxarifadoReferencia = new SceAlmoxarifado();
		sceAlmoxarifadoReferencia.setDiasSaldoClassA(10);
		sceAlmoxarifadoReferencia.setDiasSaldoClassB(20);
		sceAlmoxarifadoReferencia.setDiasSaldoClassC(30);
		sceAlmoxarifadoReferencia.setDiasParcelaClassA(10);
		sceAlmoxarifadoReferencia.setDiasParcelaClassB(20);
		sceAlmoxarifadoReferencia.setDiasParcelaClassC(30);

		BigDecimal[] actual = systemUnderTest.obterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(consumoDiario, classificacaoABC, sceAlmoxarifadoReferencia);

		Assert.assertNotNull(actual);
		BigDecimal qtdExp = null;
		if (quantidadeReposicaoExpected != null) {
			qtdExp = new BigDecimal(quantidadeReposicaoExpected);
		}
		BigDecimal intExp = null;
		if (intervaloParcelasExpected != null) {
			intExp = new BigDecimal(intervaloParcelasExpected);
		}
		Assert.assertEquals(actual[0], qtdExp);
		Assert.assertEquals(actual[1], intExp);
	}

	private void testarObterDataPrevisaoPontoReposicao(final Integer saldoInstituicao, final BigDecimal quantidadeReposicao, final BigDecimal consumoDiario,
			final Object[] qtds, final Date expected) throws Exception {

		final DadosItemAptoProgramacaoEntregaVO dadosItemApto = new DadosItemAptoProgramacaoEntregaVO(1, 1);
		dadosItemApto.setConsumoDiario(consumoDiario);

		Mockito.when(mockedScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO.buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelas(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class)))
				.thenReturn(qtds)
				.thenReturn(null);

		Date actual = systemUnderTest.obterDataPrevisaoPontoReposicao(dadosItemApto, saldoInstituicao, quantidadeReposicao);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterDataPrimeiraParcela(final Short prazoEntrega, final Date dataReposicao, final boolean indUtilizaEspacoFisico, final Date dataValidade,
			final Integer nrDiasAntesVencimento, final Integer diaFavoravel, final Date dataDiaFavoravel, final Date dtExpected, final boolean entImedExpected)
			throws Exception {

		final DadosItemAptoProgramacaoEntregaVO dadosItemApto = new DadosItemAptoProgramacaoEntregaVO(1, 1);
		dadosItemApto.setIndUtilizaEspaco(indUtilizaEspacoFisico);
		dadosItemApto.setConsumoDiario(BigDecimal.TEN);

		Object[] obj = new Object[2];
		obj[0] = 5;
		obj[1] = 2;
		
		Mockito.when(mockedScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO.buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelas(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(obj);

		Mockito.when(systemUnderTest.obterDataPrevisaoPontoReposicao(dadosItemApto, 10, BigDecimal.TEN)).thenReturn(new Date());
		Mockito.when(mockedScoAutorizacaoFornDAO.obterPrazoEntrega(Mockito.anyInt())).thenReturn(prazoEntrega);
		Mockito.when(mockedProgramacaoAutomaticaON.obterDataDiaFavoravel(Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyInt())).thenReturn(dataDiaFavoravel);
		Mockito.when(mockedProgramacaoAutomaticaON.obterDiaFavoravel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(diaFavoravel);
		Mockito.when(mockedProgramacaoAutomaticaON.obterNumeroDiasEntregueAntesVencimento()).thenReturn(nrDiasAntesVencimento);
		
		
		Object[] actual = systemUnderTest.obterDataPrimeiraParcela(dadosItemApto, 1, BigDecimal.ONE);
		Assert.assertNotNull(actual);
		Assert.assertEquals(dtExpected, actual[0]);
		Assert.assertEquals(entImedExpected, actual[1]);
	}

	private void testarObterValidade(final Boolean indControleValidade, final Date dataValidade, final Date expected) throws Exception {

		final boolean icvalidade = indControleValidade != null ? indControleValidade.booleanValue() : false;

		Mockito.when(mockedEstoqueFacade.obterDataValidadeSceValidade(Mockito.anyInt())).thenReturn(dataValidade);
		
		Date actual = systemUnderTest.obterValidade(icvalidade, 10);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterSaldoInstituicao(final int saldoAdiantamentoAF, final int quantidadeRecParcelas, final int saldoTotal, final Integer expected)
			throws Exception {

		final DadosItemAptoProgramacaoEntregaVO dadosItemApto = new DadosItemAptoProgramacaoEntregaVO(1, 1);

		Mockito.when(mockedProgramacaoAutomaticaON.obterSaldoAdiantamentoAF(Mockito.anyInt(), Mockito.anyInt())).thenReturn(saldoAdiantamentoAF);
		Mockito.when(mockedProgramacaoAutomaticaON.obterQuantidadeItemRecebProvisorio(Mockito.anyInt())).thenReturn(quantidadeRecParcelas);
		Mockito.when(mockedProgramacaoAutomaticaON.obterSaldoTotal(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.any(Date.class))).thenReturn(saldoTotal);

		Integer actual = systemUnderTest.obterSaldoInstituicao(dadosItemApto, new Date());
		Assert.assertEquals(expected, actual);
	}

	private void testarValidarSaldoInstituicao(final Integer qtdeEstoqueMax, final Integer saldoInstituicao, final boolean expected) throws Exception {
		boolean actual = systemUnderTest.validarSaldoInstituicao(qtdeEstoqueMax, saldoInstituicao);
		Assert.assertEquals(expected, actual);
	}

	private void testarObterQuantidadePrimeiraParcela(final Integer saldoInstituicao, final Integer fatorConversaoForn, boolean indUtilizaEspaco,
			final Integer qtdeEspacoArmazena, final BigDecimal[] consumo, final Integer expected) throws Exception {

		final Date dataPrimeiraParcela = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 5);

		final BigDecimal quantidadeReposicao = new BigDecimal("2000");

		final ScoItemAutorizacaoForn scoItemAutorizacaoFornNaoAssinado = new ScoItemAutorizacaoForn();
		scoItemAutorizacaoFornNaoAssinado.setFatorConversaoForn(fatorConversaoForn);

		final DadosItemAptoProgramacaoEntregaVO dadosItemApto = new DadosItemAptoProgramacaoEntregaVO(1, 1);
		dadosItemApto.setIndUtilizaEspaco(indUtilizaEspaco);
		dadosItemApto.setConsumoDiario(new BigDecimal("2"));
		dadosItemApto.setQtdeEspacoArmazena(qtdeEspacoArmazena);

		Mockito.when(mockedProgramacaoAutomaticaON.obterFatorConversao(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyInt())).thenReturn(fatorConversaoForn);
		
		Integer actual = systemUnderTest.obterQuantidadePrimeiraParcela(dadosItemApto, dataPrimeiraParcela, quantidadeReposicao, null, null, null, null);

		Assert.assertEquals(expected, actual);
	}

	private void testarObterQuantidadeCadaParcela(boolean indUtilizaEspaco, final BigDecimal[] consumo, final Integer expected) throws Exception {

		final BigDecimal intervaloParcelas = new BigDecimal("2");

		final DadosItemAptoProgramacaoEntregaVO dadosItemApto = new DadosItemAptoProgramacaoEntregaVO(1, 1);
		dadosItemApto.setIndUtilizaEspaco(indUtilizaEspaco);
		dadosItemApto.setConsumoDiario(new BigDecimal("2"));

		Integer actual = systemUnderTest.obterQuantidadeCadaParcela(dadosItemApto, intervaloParcelas, null, null);

		Assert.assertEquals(expected, actual);
	}

	private void testarObterNumeroParcelasQuantidadeUltima(final Integer saldoNaoAssinado, final Integer quantidadePrimeiraParcela,
			final Integer quantidadeCadaParcela, final Integer expectedNumero, final Integer expectedQtdUltima) throws Exception {

		Mockito.when(mockedProgramacaoAutomaticaON.obterSaldoNaoAssinado(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyInt())).thenReturn(saldoNaoAssinado);

		QuantidadesVO actual = systemUnderTest.obterNumeroParcelasQuantidadeUltima(quantidadePrimeiraParcela, quantidadeCadaParcela, null, null, null);
		Assert.assertNotNull(actual);
		Assert.assertEquals(expectedNumero, actual.getQuantidade1());
		Assert.assertEquals(expectedQtdUltima, actual.getQuantidade2());
	}

	private void testarGerarParcelas(final List<Integer> listNumeroAFs, final List<ScoItemAutorizacaoFornId> itensAF) throws Exception {
		Mockito.when(mockedScoItemAutorizacaoFornDAO.buscarItensAutorizacao(Mockito.anyInt())).thenReturn(itensAF);
		Mockito.when(mockedProgramacaoAutomaticaON.obterDadosItemApto(Mockito.anyInt(), Mockito.anyInt())).thenReturn(new DadosItemAptoProgramacaoEntregaVO(1,2));
		
		systemUnderTest.gerarParcelas(listNumeroAFs);
		Assert.assertTrue(true);
	}

	private void testarGerarParcelasItem(final boolean validarQuantidadePontoPedido, final boolean validarSaldoInstituicao, final Integer restoUltimaParcela)
			throws Exception {

		Mockito.when(mockedScoSolicitacaoDeCompraDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(null);
		Mockito.when(mockedProgramacaoAutomaticaON.obterAlmoxarifadoReferencia()).thenReturn(new SceAlmoxarifado());
		Mockito.when(mockedProgramacaoAutomaticaON.obterDadosItemApto(Mockito.anyInt(), Mockito.anyInt())).thenReturn(new DadosItemAptoProgramacaoEntregaVO());
		Mockito.when(mockedProgramacaoAutomaticaON.obterClassificacaoABC(Mockito.any(DadosItemAptoProgramacaoEntregaVO.class))).thenReturn(DominioClassifABC.A);
		Mockito.when(mockedProgramacaoAutomaticaON.obterDadosAutorizacaoFornecedor(Mockito.any(DadosItemAptoProgramacaoEntregaVO.class))).thenReturn(new Object[] { 1, (short) 2, 3d });
		Mockito.when(mockedProgramacaoAutomaticaON.obterDataEnezimaParcela(Mockito.any(Date.class), Mockito.any(BigDecimal.class), Mockito.anyInt())).thenReturn(new Date());

		systemUnderTest.gerarParcelas(10, 10);
		Assert.assertTrue(true);
	}
}