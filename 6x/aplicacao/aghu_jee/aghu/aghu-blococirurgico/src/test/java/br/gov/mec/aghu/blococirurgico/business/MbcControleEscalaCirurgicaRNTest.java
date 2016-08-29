package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.business.MbcControleEscalaCirurgicaRN.MbcControleEscalaCirurgicaRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgicaId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcControleEscalaCirurgicaRNTest extends AGHUBaseUnitTest<MbcControleEscalaCirurgicaRN> {

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private MbcControleEscalaCirurgicaDAO mockedMbcControleEscalaCirurgicaDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	
	private MbcControleEscalaCirurgica original; 

	private final static String MSG_DEVERIA_OCORRER = "Deveria ocorrer: ";
	private final static String MSG_NAO_DEVERIA_OCORRER = "Não deveria ocorrer: ";
	
	private final static Date DATA_PARA_IGUALDADE = new Date();

//	@Before
//	public void doBeforeEachTestCase() {
//
//		// contexto dos mocks, usado para criar os mocks e definir a expectativa
//		// de chamada dos métodos.
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedMbcControleEscalaCirurgicaDAO = mockingContext.mock(MbcControleEscalaCirurgicaDAO.class);
//		mockedParametroFacade = mockingContext.mock(IParametroFacade.class);
//		
//		systemUnderTest = new MbcControleEscalaCirurgicaRN() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 8885203544342879374L;
//
//			protected IAghuFacade getAghuFacade() {
//				return mockedAghuFacade;
//			}
//
//			protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
//				return mockedMbcControleEscalaCirurgicaDAO;
//			}
//
//			@Override
//			public IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			}	
//			
//
//		};
//
//		original = new MbcControleEscalaCirurgica();
//		MbcControleEscalaCirurgicaId id = new MbcControleEscalaCirurgicaId();
//		id.setDtEscala(DATA_PARA_IGUALDADE);
//		id.setUnfSeq(Short.valueOf("126"));
//		original.setId(id);
//		original.setTipoEscala(DominioTipoEscala.D);
//
//	}

	@Test
	public void verificarHorarioEscalaDefinitiva() {

		MbcControleEscalaCirurgica controleEscalaCirurgica = new MbcControleEscalaCirurgica();
		controleEscalaCirurgica.setTipoEscala(DominioTipoEscala.D);
		final String valorHora = "0700";
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					
//					AghParametros aghParametros = new AghParametros();
//					aghParametros.setVlrTexto(valorHora);
//					
//					allowing(mockedParametroFacade).buscarAghParametro(
//							with(any(AghuParametrosEnum.class)));
//					will(returnValue(aghParametros));
//					
//				}
//			});
			
			Date data = new Date();
			Calendar dt = Calendar.getInstance();
			dt.set(Calendar.HOUR_OF_DAY, Integer.valueOf(valorHora));  
			if(data.after(dt.getTime())){
				systemUnderTest.verificarHorarioEscalaDefinitiva(controleEscalaCirurgica);
			}
			
		} catch (BaseException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + MbcControleEscalaCirurgicaRNExceptionCode.MBC_00419);
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}
	}
	
	@Test
	public void verificarDadosModificados() {

	
		final MbcControleEscalaCirurgica controleEscalaCirurgica = new MbcControleEscalaCirurgica();
		MbcControleEscalaCirurgicaId id2 = new MbcControleEscalaCirurgicaId();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR,-5);
		id2.setDtEscala(c.getTime());
		id2.setUnfSeq(Short.valueOf("126"));
		controleEscalaCirurgica.setId(id2);
		
		try {

//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcControleEscalaCirurgicaDAO).obterOriginal((with(any(MbcControleEscalaCirurgica.class))));
//					will(returnValue(original)); // Teste aqui!
//				}
//			});

			systemUnderTest.verificarDadosModificados(controleEscalaCirurgica);
			Assert.fail(MSG_DEVERIA_OCORRER + MbcControleEscalaCirurgicaRNExceptionCode.MBC_00417);
			
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00417));
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}
	}
	
	@Test
	public void verificarDadosModificados2() {

		final MbcControleEscalaCirurgica controleEscalaCirurgica = new MbcControleEscalaCirurgica();
		MbcControleEscalaCirurgicaId id = new MbcControleEscalaCirurgicaId();
		id.setDtEscala(DATA_PARA_IGUALDADE);
		id.setUnfSeq(Short.valueOf("126"));
		controleEscalaCirurgica.setId(id);
		controleEscalaCirurgica.setTipoEscala(DominioTipoEscala.P);
		
		try {

//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcControleEscalaCirurgicaDAO).obterOriginal((with(any(MbcControleEscalaCirurgica.class))));
//					will(returnValue(original)); // Teste aqui!
//				}
//			});

			systemUnderTest.verificarDadosModificados(controleEscalaCirurgica);
			Assert.fail(MSG_DEVERIA_OCORRER + MbcControleEscalaCirurgicaRNExceptionCode.MBC_00418);
			
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00418));
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}
	}


}
