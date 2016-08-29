package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioAtualizacaoSaldo;
import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.faturamento.business.SaldoUtiAtualizacaoRN.PartialVO;
import br.gov.mec.aghu.faturamento.business.SaldoUtiAtualizacaoRN.TipoCompetenciaEnum;
import br.gov.mec.aghu.faturamento.dao.FatSaltoUtiDAO;
import br.gov.mec.aghu.model.FatSaldoUti;
import br.gov.mec.aghu.model.FatSaldoUtiId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SaldoUtiAtualizacaoRNTest extends AGHUBaseUnitTest<SaldoUtiAtualizacaoRN>{

	@Mock
	FatSaltoUtiDAO mockedDao = null;
	@Mock
	SaldoUtiPersist mockedPersist = null;
	private Log log = LogFactory.getLog(this.getClass());

	private FatSaldoUti getSut(final int mes, final int ano, final int qtdLeitos) {

		FatSaldoUti result = null;
		FatSaldoUtiId id = null;

		id = new FatSaldoUtiId(Integer.valueOf(mes), Integer.valueOf(ano), DominioTipoIdadeUTI.A);
		result = new FatSaldoUti(id, Integer.valueOf(qtdLeitos), "Gustavo", new Date(0L));

		return result;
	}

	@Test
	public void testObterCapMaxUtiPorMesAno() {

		FatSaldoUti saldoUti = null;
		Integer expectedCap = 0;
		Integer returnedCap = 0;
		Integer qtdLeitos = 0;
		Integer mes = 0;
		Integer ano = 0;

		//setup
		mes = 2;
		ano = 2000;
		qtdLeitos = 13;
		saldoUti = this.getSut(mes, ano, qtdLeitos);
		expectedCap = 29 * qtdLeitos;
		//assert 2000/02/01 = 29
		returnedCap = new SaldoUtiAtualizacaoRN().obterCapMaxUti(mes, ano, qtdLeitos);
		Assert.assertEquals(expectedCap, returnedCap);
		//setup
		ano = 1999;
		saldoUti.getId().setAno(Integer.valueOf(ano));
		expectedCap = 28 * qtdLeitos;
		//assert 1999/02/01 = 28
		returnedCap = new SaldoUtiAtualizacaoRN().obterCapMaxUti(mes, ano, qtdLeitos);
		Assert.assertEquals(expectedCap, returnedCap);
	}

	private FatSaldoUti getSutForUpdate(final int ano,
			final int mes,
			final int qtdLeitos,
			final int saldo,
			final int cap) {

		FatSaldoUti sut = null;

		sut = this.getSut(mes, ano, qtdLeitos);
		sut.setSaldo(Integer.valueOf(saldo));
		sut.setCapacidade(Integer.valueOf(cap));

		return sut;
	}


	private boolean callIncDec(final DominioAtualizacaoSaldo oper,
			final SaldoUtiAtualizacaoRN objRn,
			final short ano,
			final byte mes,
			final int dias) throws BaseException {

		PartialVO result = null;
		DominioTipoIdadeUTI idadeUti = null;
		TipoCompetenciaEnum tipoCompetencia = TipoCompetenciaEnum.ALTA;

		idadeUti = DominioTipoIdadeUTI.A;

		switch (oper) {
		case I:
			result = objRn.incrementarSaldoDiariasUti(idadeUti, ano, mes, dias, tipoCompetencia, "AGHU_2K46");
			break;
		case D:
			result = objRn.decrementarSaldoDiariasUti(idadeUti, ano, mes, dias, tipoCompetencia, "AGHU_2K46");
			break;
		default:
			throw new IllegalArgumentException();
		}

		return result.retorno;
	}

	public void testIncrementarSaldoDiariasUtiCapMaiorQueSaldo() {

		FatSaldoUti sut = null;
		short ano = 0;
		byte mes = 0;
		int dias = 0;
		short qtdLeitos = 0;
		int cap = 0;
		final int saldo = 0;
		boolean result = false;

		//setup
		mes = 2;
		ano = 2000;
		qtdLeitos = 13;
		dias = 10;
		cap = dias + 1;
		sut = this.getSutForUpdate(ano, mes, qtdLeitos, saldo, cap);
		//cap = SaldoUtiAtualizacaoRN.obterCapMaxUtiPorMesAno(sut);
		//assert cap < saldo == false
		try {
			result = this.callIncDec(DominioAtualizacaoSaldo.I, systemUnderTest, ano, mes, dias);
			Assert.assertTrue(result);
			Assert.assertEquals(saldo + dias, sut.getSaldo().intValue());
		} catch (final Exception e) {
			log.error(e.getMessage());
			this.log.info(e);
			Assert.fail("Not expecting exception: " + e);
		}
	}

	public void testIncrementarSaldoDiariasUtiCapMenorQueSaldo() {

		FatSaldoUti sut = null;
		short ano = 0;
		byte mes = 0;
		int dias = 0;
		short qtdLeitos = 0;
		int cap = 0;
		final int saldo = 0;
		boolean result = false;

		//setup
		mes = 2;
		ano = 2000;
		qtdLeitos = 13;
		dias = 10;
		cap = dias - 1;
		sut = this.getSutForUpdate(ano, mes, qtdLeitos, saldo, cap);
		//cap = SaldoUtiAtualizacaoRN.obterCapMaxUtiPorMesAno(sut);
		//assert cap < saldo == false
		try {
			result = this.callIncDec(DominioAtualizacaoSaldo.I, systemUnderTest, ano, mes, dias);
			Assert.assertTrue(result);
			Assert.assertEquals(saldo + dias, sut.getSaldo().intValue());
		} catch (final Exception e) {
			log.error(e.getMessage());
			this.log.info(e);
			Assert.fail("Not expecting exception: " + e);
		}
	}

	public void testDecrementarSaldoDiariasUti() {

		FatSaldoUti sut = null;
		short ano = 0;
		byte mes = 0;
		int dias = 0;
		short qtdLeitos = 0;
		int cap = 0;
		int saldo = 0;
		boolean result = false;

		//setup
		mes = 2;
		saldo = 20;
		ano = 2000;
		qtdLeitos = 13;
		dias = 10;
		cap = dias + 1;
		sut = this.getSutForUpdate(ano, mes, qtdLeitos, saldo, cap);
		//cap = SaldoUtiAtualizacaoRN.obterCapMaxUtiPorMesAno(sut);
		//assert cap < saldo == false
		try {
			result = this.callIncDec(DominioAtualizacaoSaldo.D, systemUnderTest, ano, mes, dias);
			Assert.assertTrue(result);
			Assert.assertEquals(saldo - dias, sut.getSaldo().intValue());
		} catch (final Exception e) {
			log.error(e.getMessage());
			this.log.info(e);
			Assert.fail("Not expecting exception: " + e);
		}
	}
	

}
