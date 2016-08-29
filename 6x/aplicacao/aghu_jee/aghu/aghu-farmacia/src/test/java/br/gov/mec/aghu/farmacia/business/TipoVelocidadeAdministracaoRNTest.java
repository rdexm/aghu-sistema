package br.gov.mec.aghu.farmacia.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.farmacia.business.TipoVelocidadeAdministracaoRN.TipoVelocidadeAdministracaoRNExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;

public class TipoVelocidadeAdministracaoRNTest extends AGHUBaseUnitTest<TipoVelocidadeAdministracaoRN> {
	
	private static final Log log = LogFactory.getLog(TipoVelocidadeAdministracaoRNTest.class);

	@Mock
	private AfaTipoVelocAdministracoesDAO mockedAfaTipoVelocAdministracoesDAO;
	
	@Mock
	private IAghuFacade mockedAghuFacade;

	@Test
	public void verificaUsoSoroterapiaNutricaoParentalTest1() {
		try {
			final Short seqTipoVelocidadeAdmnistracao = 1;
			final Boolean indTipoUsualNpt = Boolean.TRUE;
			final Boolean indTipoUsualSoroterapia = Boolean.TRUE;

			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao = new AfaTipoVelocAdministracoes();
			tipoVelocidadeAdministracao
					.setIndTipoUsualSoroterapia(Boolean.TRUE);

			final List<AfaTipoVelocAdministracoes> tiposVelocidadeAdministacao = new ArrayList<AfaTipoVelocAdministracoes>();
			tiposVelocidadeAdministacao.add(tipoVelocidadeAdministracao);

			Mockito.when(mockedAfaTipoVelocAdministracoesDAO.cursorVerificaUsoSoroterapiaNutricaoParental(Mockito.anyShort())).thenReturn(tiposVelocidadeAdministacao);

			systemUnderTest.verificaUsoSoroterapiaNutricaoParental(
					seqTipoVelocidadeAdmnistracao,
					indTipoUsualSoroterapia, indTipoUsualNpt);
			Assert.fail("Deveria ter lançado exceção AFA_00219");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00219", e.getCode(),
					TipoVelocidadeAdministracaoRNExceptionCode.AFA_00219);
		}
	}

	@Test
	public void verificaUsoSoroterapiaNutricaoParentalTest2() {
		try {
			final Short seqTipoVelocidadeAdmnistracao = 1;
			final Boolean indTipoUsualNpt = Boolean.TRUE;
			final Boolean indTipoUsualSoroterapia = Boolean.FALSE;

			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao = new AfaTipoVelocAdministracoes();
			tipoVelocidadeAdministracao
					.setIndTipoUsualSoroterapia(Boolean.TRUE);
			tipoVelocidadeAdministracao.setIndTipoUsualNpt(Boolean.TRUE);

			final List<AfaTipoVelocAdministracoes> tiposVelocidadeAdministacao = new ArrayList<AfaTipoVelocAdministracoes>();
			tiposVelocidadeAdministacao.add(tipoVelocidadeAdministracao);

			Mockito.when(mockedAfaTipoVelocAdministracoesDAO.cursorVerificaUsoSoroterapiaNutricaoParental(Mockito.anyShort())).thenReturn(tiposVelocidadeAdministacao);
			
			systemUnderTest.verificaUsoSoroterapiaNutricaoParental(
					seqTipoVelocidadeAdmnistracao,
					indTipoUsualSoroterapia, indTipoUsualNpt);
			Assert.fail("Deveria ter lançado exceção AFA_00218");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00218", e.getCode(),
					TipoVelocidadeAdministracaoRNExceptionCode.AFA_00218);
		}
	}

	@Test
	public void verificaUsoSoroterapiaNutricaoParentalTest3() {
		try {
			final Short seqTipoVelocidadeAdmnistracao = 1;
			final Boolean indTipoUsualNpt = Boolean.FALSE;
			final Boolean indTipoUsualSoroterapia = Boolean.FALSE;

			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao = new AfaTipoVelocAdministracoes();
			tipoVelocidadeAdministracao
					.setIndTipoUsualSoroterapia(Boolean.TRUE);
			tipoVelocidadeAdministracao.setIndTipoUsualNpt(Boolean.TRUE);

			final List<AfaTipoVelocAdministracoes> tiposVelocidadeAdministacao = new ArrayList<AfaTipoVelocAdministracoes>();
			tiposVelocidadeAdministacao.add(tipoVelocidadeAdministracao);

			Mockito.when(mockedAfaTipoVelocAdministracoesDAO.cursorVerificaUsoSoroterapiaNutricaoParental(Mockito.anyShort())).thenReturn(tiposVelocidadeAdministacao);

			systemUnderTest.verificaUsoSoroterapiaNutricaoParental(
					seqTipoVelocidadeAdmnistracao,
					indTipoUsualSoroterapia, indTipoUsualNpt);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail("Ocorreu uma exceção: " + e.getMessage());
		}
	}

	@Test
	public void verificaInativoTest() {
		try {
			systemUnderTest.verificaInativo();
			Assert.fail("Deveria ter lançado exceção AFA_00217");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00217", e
					.getCode(),
					TipoVelocidadeAdministracaoRNExceptionCode.AFA_00217);
		}
	}

	@Test
	public void verificaDescricaoTest() {
		try {
			systemUnderTest.verificaDescricao();
			Assert.fail("Deveria ter lançado exceção AFA_00216");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00216", e
					.getCode(),
					TipoVelocidadeAdministracaoRNExceptionCode.AFA_00216);
		}
	}
}
