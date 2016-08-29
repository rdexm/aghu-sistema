/**
 * 
 */
package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.OcorrenciaRN.OcorrenciaRNExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * @author ehgsilva
 *
 */
//@Ignore
public class OcorrenciaRNTest extends AGHUBaseUnitTest<OcorrenciaRN>{

	//Daos e Facades a serem mockadas
	@Mock
	private AfaTipoOcorDispensacaoDAO mockedafaTipoOcorDispensacaoDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	
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
//		// mocking cada uma das DAOs utilizadas
//		mockedafaTipoOcorDispensacaoDAO = mockingContext.mock(AfaTipoOcorDispensacaoDAO.class);
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedRegistroColaboradorFacade = mockingContext.mock(IRegistroColaboradorFacade.class);		
//
//
//		// criação do objeto da classe a ser testada, com os devidos métodos
//		// sobrescritos.
//		systemUnderTest = new OcorrenciaRN() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -6229304556020040369L;
//
//			@Override
//			protected AfaTipoOcorDispensacaoDAO getAfaTipoOcorDispensacaoDAO() {
//				return mockedafaTipoOcorDispensacaoDAO;
//			}
//			
//			@Override
//			protected IAghuFacade getAghuFacade(){
//				return mockedAghuFacade;
//			}
//			
//			protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
//				return mockedRegistroColaboradorFacade;
//			}
//			
//		};
//
//	}
//	
//	//Expectations
//	private void esperarObterDescricaoAnterior(final String descricaoAnterior) {
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedafaTipoOcorDispensacaoDAO)
//						.obterDescricaoAnterior(
//								with(any(Short.class)));
//				will(returnValue(descricaoAnterior));
//			}
//		});
//	}
	

	
	@Test
	public void verificarAtribuirCriadoEm() {
		AfaTipoOcorDispensacao ocorrencia = new AfaTipoOcorDispensacao();
		systemUnderTest.verificarAtribuirCriadoEm(ocorrencia);
		Assert.assertNotNull(ocorrencia.getCriadoEm());
	}
	
	
	@Test
	public void verificarAtribuirServidorException() throws ApplicationBusinessException {
		//Deve chamar este método
		
		AfaTipoOcorDispensacao ocorrencia = new AfaTipoOcorDispensacao();
		try {
			Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(null);
			
			systemUnderTest.verificarAtribuirServidor(ocorrencia);
			Assert.fail("Deveria ter lançado exceção AFA_00169");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00169", e.getCode(),OcorrenciaRNExceptionCode.AFA_00169);
		}
//		mockingContext.assertIsSatisfied();
	}
	
	
	@Test
	public void verificarDiferencaDiasException() throws ApplicationBusinessException {
		AghParametros parametroDias = new AghParametros();
		BigDecimal valorNumerico = new BigDecimal(2);
		parametroDias.setVlrNumerico(valorNumerico);
		
		//Cria uma data
		Calendar calendarCriadoEm = Calendar.getInstance();  
		calendarCriadoEm.set(2011, 3, 5);  //Lembrando que o mês inicial é 0
		Date criadoEm = calendarCriadoEm.getTime();  
		try {
		systemUnderTest.verificarDiferencaDias(parametroDias, criadoEm);
		Assert.fail("Deveria ter lançado exceção AFA_00172");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00172", e.getCode(),OcorrenciaRNExceptionCode.AFA_00172);
		}
//		mockingContext.assertIsSatisfied();
	}
	
	

	@Test
	public void validarDescricaoAlterada() throws ApplicationBusinessException {
		final String descricaoAnterior = "antiga";
//		esperarObterDescricaoAnterior(descricaoAnterior);
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedafaTipoOcorDispensacaoDAO)
//						.obterDescricaoAnterior(
//								with(any(Short.class)));
//				will(returnValue(descricaoAnterior));
//			}
//		});
		
		Mockito.when(mockedafaTipoOcorDispensacaoDAO.obterDescricaoAnterior(Mockito.anyShort())).thenReturn(descricaoAnterior);
		
		final String descricaoNova = "nova";
		AfaTipoOcorDispensacao ocorrencia = new AfaTipoOcorDispensacao();
		ocorrencia.setDescricao(descricaoNova);
		try {
			systemUnderTest.validarDescricaoAlterada(ocorrencia);
			Assert.fail("Deveria ter lançado exceção MPM_00774");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser a MPM_00774", e.getCode(),OcorrenciaRNExceptionCode.MPM_00774);
		}
//		mockingContext.assertIsSatisfied();
	}
	
}
