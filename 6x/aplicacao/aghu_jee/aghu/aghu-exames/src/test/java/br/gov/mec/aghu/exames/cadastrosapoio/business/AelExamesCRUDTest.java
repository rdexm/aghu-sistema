package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelSinonimoExameDAO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lsamberg
 *
 */
public class AelExamesCRUDTest extends AGHUBaseUnitTest<ManterDadosBasicosExamesCRUD> {
	
	private static final Log log = LogFactory.getLog(AelExamesCRUDTest.class);

	@Mock
	private AelExamesDAO mockedAelExamesDAO;
	@Mock
	private AelSinonimosExamesCRUD mockedAelSinonimosExamesCRUD;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AelSinonimoExameDAO mockedAelSinonimoExameDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Before
	public void doBeforeEachTestCase() throws Exception{
		
		whenObterServidorLogado();
		
		Mockito.when(mockedAelExamesDAO.existeItem(Mockito.any(AelExames.class), Mockito.any(Class.class), Mockito.any(Enum.class))).thenReturn(true);

		Mockito.when(mockedAelSinonimoExameDAO.buscarSinonimoPrincipalPorAelExames(Mockito.any(AelExames.class))).thenReturn(new AelSinonimoExame());

		Mockito.when(mockedAelSinonimoExameDAO.pesquisarSinonimosExames(Mockito.any(AelExames.class))).thenReturn(new ArrayList<AelSinonimoExame>());

    	final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal("5"));
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
	}
	
	@Test
	public void persistirAelExamesTest() throws BaseException{
		final AelExames aelExames = new AelExames();
		final String sigla = "";
    	
		Mockito.when(mockedAelExamesDAO.pesquisarCount(Mockito.any(AelExames.class), Mockito.any(AghUnidadesFuncionais.class))).thenReturn(0l);

		Mockito.when(mockedAelExamesDAO.obterPeloId(Mockito.anyString())).thenReturn(null);

    	try {
			systemUnderTest.persistirAelExames(aelExames,sigla);
			
			Assert.assertTrue(true);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertFalse(true);
			
		}				
		
	}
	
//	@Test
//	public void persistirAelExamesUpdateTest() throws BaseException{
//		
//		final AelExames aelExames = new AelExames();
//		final String sigla = "";
//		
//    	mockingContext.checking(new Expectations() {{
//    		allowing(mockedAelExamesDAO).inserir(with(any(AelExames.class)));
//			will(returnValue(new AelExames()));
//		}});
//    	
//    	mockingContext.checking(new Expectations() {{
//    		allowing(mockedAelExamesDAO).desatachar(with(any(AelExames.class)));
//		}});
//    	
//    	mockingContext.checking(new Expectations() {{
//    		allowing(mockedAelExamesDAO).atualizar(with(any(AelExames.class)));
//			will(returnValue(new AelExames()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedAelExamesDAO).pesquisarCount(with(any(AelExames.class)));
//			will(returnValue(Integer.valueOf("0")));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedAghuConfigRN).getMatricula();
//			will(returnValue(Integer.valueOf("0")));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedAghuConfigRN).getVinculo();
//			will(returnValue(Short.valueOf("0")));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedAelSinonimosExamesCRUD).inserirAelSinonimosExames(with(any(AelSinonimoExame.class)));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedAelExamesDAO).obterPeloId(with(any(String.class)));
//			will(returnValue(new AelExames()));
//		}});
//		
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedAelSinonimosExamesCRUD).atualizarAelSinonimosExames(with(any(AelSinonimoExame.class)),with(any(Boolean.class)));
//			will(returnValue(null));
//		}});
//		
//		try {
//			aelExames.setSigla("");
//			systemUnderTest.persistirAelExames(aelExames,sigla);
//			
//			Assert.assertTrue(true);
//		} catch (BaseException e) {
//			Assert.assertFalse(false);
//		}
//	}
	
    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
