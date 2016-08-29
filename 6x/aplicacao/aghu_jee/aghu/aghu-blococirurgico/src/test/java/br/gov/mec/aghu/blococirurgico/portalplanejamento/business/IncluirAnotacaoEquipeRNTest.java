package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnotacaoDAO;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcAgendaAnotacaoId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class IncluirAnotacaoEquipeRNTest extends AGHUBaseUnitTest<IncluirAnotacaoEquipeRN> {

	@Mock
	private MbcAgendaAnotacaoDAO mockMbcAgendaAnotacaoDAO;
	@Mock
	private IRegistroColaboradorFacade mockIRegistroColaboradorFacade;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockMbcAgendaAnotacaoDAO = mockingContext.mock(MbcAgendaAnotacaoDAO.class);
//		mockIRegistroColaboradorFacade = mockingContext.mock(IRegistroColaboradorFacade.class);
//
//		systemUnderTest = new IncluirAnotacaoEquipeRN(){
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 7810403200971170267L;
//
//			protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
//				return mockIRegistroColaboradorFacade;
//			};
//			
//			protected MbcAgendaAnotacaoDAO getMbcAgendaAnotacaoDAO() {
//				return mockMbcAgendaAnotacaoDAO;
//			};
//
//			public void rnAgnpVerAltData(Date dataNova, Date dataAntiga,
//					String descricaoNova, String descricaoAntiga)
//					throws ApplicationBusinessException {};
//		};
//	}
	
	@Test
	public void testMbctAgnBri() {
		/* FR try {
			esperaObterServidorLogado();
			MbcAgendaAnotacao mbcAnotacao = instanciaMbcAnotacao();
			systemUnderTest.mbctAgnBri(mbcAnotacao);
			Assert.assertNotNull(mbcAnotacao.getCriadoEm());
			Assert.assertNotNull(mbcAnotacao.getRapServidores());
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}*/
	}
	
	@Test
	public void testMbctAgnBru() {
		/* FR try {
			esperaObterServidorLogado();
			esperarObterOriginal();
			MbcAgendaAnotacao mbcAnotacao = instanciaMbcAnotacao();
			systemUnderTest.mbctAgnBru(mbcAnotacao);
			Assert.assertNotNull(mbcAnotacao.getAlteradoEm());
			Assert.assertNotNull(mbcAnotacao.getRapServidores());
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		} */
	}


	private void esperarObterOriginal() {
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcAgendaAnotacaoDAO).obterOriginal(with(any(MbcAgendaAnotacaoId.class)));
//			will(returnValue(instanciaMbcAnotacao()));
//		}});
	}


	private MbcAgendaAnotacao instanciaMbcAnotacao() {
		MbcAgendaAnotacao mbcAnotacao = new MbcAgendaAnotacao();
		MbcAgendaAnotacaoId id = new MbcAgendaAnotacaoId();
		id.setData(new Date());
		mbcAnotacao.setId(id);
		return mbcAnotacao;
	}

	private void esperaObterServidorLogado() throws ApplicationBusinessException {
//		mockingContext.checking(new Expectations() {{
//			allowing(mockIRegistroColaboradorFacade).obterServidorPorUsuario(with(any(String.class)));
//			will(returnValue(new RapServidores()));
//		}});
	}
}
