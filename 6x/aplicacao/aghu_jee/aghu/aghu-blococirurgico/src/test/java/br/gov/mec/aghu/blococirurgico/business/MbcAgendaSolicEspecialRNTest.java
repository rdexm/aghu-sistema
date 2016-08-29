package br.gov.mec.aghu.blococirurgico.business;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendaSolicEspecialRN.MbcAgendaSolicEspecialRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcAgendaSolicEspecialRNTest extends AGHUBaseUnitTest<MbcAgendaSolicEspecialRN>{

	private MbcAgendasDAO mockedAgendasDAO;

//	@Before
//	public void doBeforeEachTestCase() {
//
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedAgendasDAO = mockingContext.mock(MbcAgendasDAO.class);
//		
//		systemUnderTest = new MbcAgendaSolicEspecialRN() {
//			
//			private static final long serialVersionUID = -7121795075476700685L;
//
//			@Override
//			protected MbcAgendasDAO getMbcAgendasDAO() {
//				return mockedAgendasDAO;
//			}
//			
//		};
//
//	}

	/**
	 * SUCESSO
	 */
	@Test
	public void validarAgendaComControleEscalaCirurgicaDefinitivaTest1() {
		MbcAgendas agenda = new MbcAgendas();
		
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva((with(any(Integer.class))));
//				will(returnValue(null));
//			}
//		});

		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(agenda);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00834
	 */
	@Test
	public void validarAgendaComControleEscalaCirurgicaDefinitivaTest2() {
		MbcAgendas agenda = new MbcAgendas();
		agenda.setSeq(1);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					MbcAgendas resultado = new MbcAgendas();
//					resultado.setIndGeradoSistema(false);
//					oneOf(mockedAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva((with(any(Integer.class))));
//					will(returnValue(resultado));
//				}
//			});
			
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(agenda);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaSolicEspecialRNExceptionCode.MBC_00834));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void validarDescricaoCasoObrigatoriaTest1() {
		MbcNecessidadeCirurgica necessidade = new MbcNecessidadeCirurgica();
		necessidade.setIndExigeDescSolic(true);
		MbcAgendaSolicEspecial agendaSolicEspecial = new MbcAgendaSolicEspecial();
		agendaSolicEspecial.setMbcNecessidadeCirurgica(necessidade);
		agendaSolicEspecial.setDescricao("Teste");
		
		try {
			systemUnderTest.validarDescricaoCasoObrigatoria(agendaSolicEspecial);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00889
	 */
	@Test
	public void validarDescricaoCasoObrigatoriaTest2() {
		MbcNecessidadeCirurgica necessidade = new MbcNecessidadeCirurgica();
		necessidade.setIndExigeDescSolic(true);
		MbcAgendaSolicEspecial agendaSolicEspecial = new MbcAgendaSolicEspecial();
		agendaSolicEspecial.setMbcNecessidadeCirurgica(necessidade);
		try{
			systemUnderTest.validarDescricaoCasoObrigatoria(agendaSolicEspecial);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaSolicEspecialRNExceptionCode.MBC_00889));
		}
	}
	
}