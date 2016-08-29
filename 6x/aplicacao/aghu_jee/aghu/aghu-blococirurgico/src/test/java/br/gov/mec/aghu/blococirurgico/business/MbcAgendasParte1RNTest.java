package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendasRN.MbcAgendasRNExceptionCode;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcAgendasParte1RNTest extends AGHUBaseUnitTest<MbcAgendasParte1RN> {

	private MbcAgendasRN mockedMbcAgendasRN;

//	@Before
//	public void doBeforeEachTestCase() {
//
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//		
//		mockedMbcAgendasRN = mockingContext.mock(MbcAgendasRN.class);
//		
//		systemUnderTest = new MbcAgendasParte1RN() {
//			
//			private static final long serialVersionUID = -7121795075476700685L;
//
//			@Override
//			protected MbcAgendasRN getMbcAgendasRN() {
//				return mockedMbcAgendasRN;
//			}
//		};
//	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarQuantidadeProcedimentoTest1() {
		MbcEspecialidadeProcCirgs espProcCirgs = new MbcEspecialidadeProcCirgs();
		
		try {
			systemUnderTest.verificarQuantidadeProcedimento(espProcCirgs, Short.valueOf("1"), new Date(), true);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00971
	 */
	@Test
	public void verificarQuantidadeProcedimentoTest2() {
		MbcEspecialidadeProcCirgs espProcCirgs = new MbcEspecialidadeProcCirgs();
		
		try {
			systemUnderTest.verificarQuantidadeProcedimento(espProcCirgs, Short.valueOf("1"), new Date(), false);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00971));
		}
	}
	
	/**
	 * MBC_00972
	 */
	@Test
	public void verificarQuantidadeProcedimentoTest3() {
		MbcEspecialidadeProcCirgs espProcCirgs = new MbcEspecialidadeProcCirgs();
		
		MbcProcedimentoCirurgicos procCirg = new MbcProcedimentoCirurgicos();
		procCirg.setIndProcMultiplo(false);
		
		espProcCirgs.setMbcProcedimentoCirurgicos(procCirg);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcAgendasRN).gerarTempoMinimoMinutos(
//							with(any(MbcProcedimentoCirurgicos.class)));
//					will(returnValue(5));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcAgendasRN).gerarTempoMinimoEditado(
//							with(any(Integer.class)));
//					will(returnValue("05:00"));
//				}
//			});
			
			systemUnderTest.verificarQuantidadeProcedimento(espProcCirgs, Short.valueOf("2"), new Date(), false);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00972));
		}
	}
	
	/**
	 * MBC_01070
	 */
	@Test
	public void verificarQuantidadeProcedimentoTest4() {
		MbcEspecialidadeProcCirgs espProcCirgs = new MbcEspecialidadeProcCirgs();
		
		MbcProcedimentoCirurgicos procCirg = new MbcProcedimentoCirurgicos();
		procCirg.setIndProcMultiplo(true);
		
		espProcCirgs.setMbcProcedimentoCirurgicos(procCirg);
		
		GregorianCalendar tempoSala = new GregorianCalendar();
		tempoSala.set(Calendar.HOUR_OF_DAY, 3);
		tempoSala.set(Calendar.MINUTE, 30);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcAgendasRN).gerarTempoMinimoMinutos(
//							with(any(MbcProcedimentoCirurgicos.class)));
//					will(returnValue(240));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcAgendasRN).gerarTempoMinimoEditado(
//							with(any(Integer.class)));
//					will(returnValue("04:00"));
//				}
//			});
			
			systemUnderTest.verificarQuantidadeProcedimento(espProcCirgs, Short.valueOf("1"), tempoSala.getTime(), false);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_01070));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarObitoPacienteTest1() {
		
		try {
			systemUnderTest.verificarObitoPaciente(new AipPacientes(), true);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_01321
	 */
	@Test
	public void verificarObitoPacienteTest2() {
		AipPacientes pac = new AipPacientes();
		pac.setDtObitoExterno(new Date());
		try {
			systemUnderTest.verificarObitoPaciente(pac, false);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_01321));
		}
	}

}