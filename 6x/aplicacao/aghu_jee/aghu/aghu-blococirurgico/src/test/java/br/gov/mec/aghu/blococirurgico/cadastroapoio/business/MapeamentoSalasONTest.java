package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MapeamentoSalasON.MapeamentoSalasONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MapeamentoSalasONTest extends AGHUBaseUnitTest<MapeamentoSalasON>{

	@Mock
	private MbcCaracteristicaSalaCirgDAO mockCaracteristicaSalaCirgDAO;
	@Mock
	private MbcCaractSalaEspDAO mockMbcCaractSalaEspDAO;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockCaracteristicaSalaCirgDAO = mockingContext.mock(MbcCaracteristicaSalaCirgDAO.class);
//		mockMbcCaractSalaEspDAO = mockingContext.mock(MbcCaractSalaEspDAO.class);
//
//		systemUnderTest = new MapeamentoSalasON(){
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 7479360924004891552L;
//
//			@Override
//			protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
//				return mockCaracteristicaSalaCirgDAO;
//			};
//
//			@Override
//			protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
//				return mockMbcCaractSalaEspDAO;
//			};
//
//		};
//	}

	/**
	 * sucesso
	 */
	@Test
	public void testValidarChecksInstOperacAntesGravar() {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		caracteristicaSalaCirg.setSituacao(DominioSituacao.A);
		caracteristicaSalaCirg.setIndDisponivel(true);
		
		try {
			systemUnderTest.validarChecksInstOperacAntesGravar(caracteristicaSalaCirg);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * MBC_01350
	 */
	@Test
	public void testValidarChecksInstOperacAntesGravar02() {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		caracteristicaSalaCirg.setSituacao(DominioSituacao.I);
		caracteristicaSalaCirg.setIndDisponivel(true);
		
		try {
			systemUnderTest.validarChecksInstOperacAntesGravar(caracteristicaSalaCirg);
			Assert.fail("Exceção não gerada: MBC_01350");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MapeamentoSalasONExceptionCode.MBC_01350, e.getCode());
		}
		
	}
	
	/**
	 * MBC_01351_1
	 */
	@Test
	public void testValidarChecksInstOperacAntesGravar03() {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		caracteristicaSalaCirg.setSituacao(DominioSituacao.I);
		caracteristicaSalaCirg.setIndDisponivel(false);
		caracteristicaSalaCirg.setSeq(Short.valueOf("1"));
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarQtdeCaractSalaEspAtivaPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(2));
//		}});
		
		try {
			systemUnderTest.validarChecksInstOperacAntesGravar(caracteristicaSalaCirg);
			Assert.fail("Exceção não gerada: MBC_01351_1");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MapeamentoSalasONExceptionCode.MBC_01351_1, e.getCode());
		}
	}
	
	/**
	 * Sucesso
	 */
	@Test
	public void testValidarConfiguracaoDuplicada() {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockCaracteristicaSalaCirgDAO).buscarCaracteristicaComMesmaConfiguracao(with(any(MbcCaracteristicaSalaCirg.class))); 
//			will(returnValue(null));
//		}});
		
		try {
			systemUnderTest.validarConfiguracaoDuplicada(caracteristicaSalaCirg);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * MBC_00021_1
	 */
	@Test
	public void testValidarConfiguracaoDuplicada02() {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		
//		mockingContext.checking(new Expectations() {{
//			MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
//			oneOf(mockCaracteristicaSalaCirgDAO).buscarCaracteristicaComMesmaConfiguracao(with(any(MbcCaracteristicaSalaCirg.class))); 
//			will(returnValue(caract));
//		}});
		
		try {
			systemUnderTest.validarConfiguracaoDuplicada(caracteristicaSalaCirg);
			Assert.fail("Exceção não gerada: MBC_00021_1");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MapeamentoSalasONExceptionCode.MBC_00021_1, e.getCode());
		}
	}
	
	/**
	 * Sucesso
	 */
	@Test
	public void testValidarPercentuais() {
		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
		caractSalaEsp.setPercentualReserva(Short.valueOf("50"));
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> listaEsp = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp esp = new MbcCaractSalaEsp();
//			esp.setPercentualReserva(Short.valueOf("50"));
//			listaEsp.add(esp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarOutrasEspecialidadesAtivasPorCaract(with(any(MbcCaractSalaEsp.class))); 
//			will(returnValue(listaEsp));
//		}});
		
		try {
			systemUnderTest.validarPercentuais(caractSalaEsp);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * MENSAGEM_ERRO_SOMA_PERCENTUAL
	 */
	@Test
	public void testValidarPercentuais01() {
		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
		caractSalaEsp.setPercentualReserva(Short.valueOf("50"));
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> listaEsp = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp esp = new MbcCaractSalaEsp();
//			esp.setPercentualReserva(Short.valueOf("51"));
//			listaEsp.add(esp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarOutrasEspecialidadesAtivasPorCaract(with(any(MbcCaractSalaEsp.class))); 
//			will(returnValue(listaEsp));
//		}});
		
		try {
			systemUnderTest.validarPercentuais(caractSalaEsp);
			Assert.fail("Exceção não gerada: MENSAGEM_ERRO_SOMA_PERCENTUAL");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MapeamentoSalasONExceptionCode.MENSAGEM_ERRO_SOMA_PERCENTUAL, e.getCode());
		}
	}
	
	/**
	 * Sucesso
	 */
	@Test
	public void testValidarSalaOperacional() {
		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
		caractSalaEsp.setIndSituacao(DominioSituacao.I);
		
		MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
		caract.setIndDisponivel(Boolean.FALSE);
		
		caractSalaEsp.setMbcCaracteristicaSalaCirg(caract);
		
		try {
			systemUnderTest.validarSalaOperacional(caractSalaEsp);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * MBC_01351
	 */
	@Test
	public void testValidarSalaOperacional01() {
		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
		caractSalaEsp.setIndSituacao(DominioSituacao.A);
		
		MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
		caract.setIndDisponivel(Boolean.FALSE);
		
		caractSalaEsp.setMbcCaracteristicaSalaCirg(caract);
		
		try {
			systemUnderTest.validarSalaOperacional(caractSalaEsp);
			Assert.fail("Exceção não gerada: MBC_01351");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MapeamentoSalasONExceptionCode.MBC_01351, e.getCode());
		}
	}
	
//	/*
//	 * MBC_00407
//	 */
//	@Test
//	public void validarSalaUrgenciasFail() {
//		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
//		caractSalaEsp.setIndSituacao(DominioSituacao.A);
//		
//		MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
//		caract.setIndUrgencia(Boolean.TRUE);
//		
//		caractSalaEsp.setMbcCaracteristicaSalaCirg(caract);
//		
//		try {
//			systemUnderTest.validarSalaUrgencias(caractSalaEsp);
//			Assert.fail("Exceção não gerada: MBC_00407");
//		} catch (ApplicationBusinessException e) {
//			Assert.assertEquals(MapeamentoSalasONExceptionCode.MBC_00407, e.getCode());
//		}
//	}
//	
//	
//	/**
//	 * Sucesso
//	 */
//	@Test
//	public void validarSalaUrgencias01() {
//		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
//		caractSalaEsp.setIndSituacao(DominioSituacao.A);
//		
//		MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
//		caract.setIndUrgencia(Boolean.FALSE);
//		
//		caractSalaEsp.setMbcCaracteristicaSalaCirg(caract);
//		
//		try {
//			systemUnderTest.validarSalaUrgencias(caractSalaEsp);
//		} catch (BaseException e) {
//			Assert.fail("Exeção gerada. " + e.getCode());
//		}
//	}
//	
//	/**
//	 * Sucesso
//	 */
//	@Test
//	public void validarSalaUrgencias02() {
//		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
//		caractSalaEsp.setIndSituacao(DominioSituacao.I);
//		
//		MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
//		caract.setIndUrgencia(Boolean.TRUE);
//		
//		caractSalaEsp.setMbcCaracteristicaSalaCirg(caract);
//		
//		try {
//			systemUnderTest.validarSalaUrgencias(caractSalaEsp);
//		} catch (BaseException e) {
//			Assert.fail("Exeção gerada. " + e.getCode());
//		}
//	}
//	
//	
//	/**
//	 * Sucesso
//	 */
//	@Test
//	public void validarSalaUrgencias03() {
//		MbcCaractSalaEsp caractSalaEsp = new MbcCaractSalaEsp();
//		caractSalaEsp.setIndSituacao(DominioSituacao.I);
//		
//		MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
//		caract.setIndUrgencia(Boolean.FALSE);
//		
//		caractSalaEsp.setMbcCaracteristicaSalaCirg(caract);
//		
//		try {
//			systemUnderTest.validarSalaUrgencias(caractSalaEsp);
//		} catch (BaseException e) {
//			Assert.fail("Exeção gerada. " + e.getCode());
//		}
//	}
	
	
}
