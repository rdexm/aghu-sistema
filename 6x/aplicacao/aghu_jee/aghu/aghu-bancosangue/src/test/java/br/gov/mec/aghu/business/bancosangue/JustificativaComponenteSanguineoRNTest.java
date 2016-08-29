package br.gov.mec.aghu.business.bancosangue;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.bancosangue.dao.AbsJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class JustificativaComponenteSanguineoRNTest extends AGHUBaseUnitTest<JustificativaComponenteSanguineoRN>{

	@Mock
	private AbsJustificativaComponenteSanguineoDAO mockedAbsJustificativaComponenteSanguineoDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void excluirJustificativaComponenteSanguineoNulosTest() throws ApplicationBusinessException {
		systemUnderTest.excluirJustificativaComponenteSanguineo(null);
	
	}

	/**
	 * Testa a execucao com passagem de parâmetros válidos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void excluirJustificativaComponenteSanguineoTest() throws ApplicationBusinessException {
		
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		systemUnderTest.excluirJustificativaComponenteSanguineo(jcs);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void preInserirJustificativaComponenteSanguineoNulosTest() throws ApplicationBusinessException {
		systemUnderTest.preInserirJustificativaComponenteSanguineo(null);

	}
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void preInserirJustificativaComponenteSanguineoMatriculaNulaTest() throws BaseException {
		
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		
		whenObterServidorLogadoNull();
		
		systemUnderTest.preInserirJustificativaComponenteSanguineo(jcs);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros válidos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void preInserirJustificativaComponenteSanguineoTest() throws BaseException {
		
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		
		whenObterServidorLogado();
		
		systemUnderTest.preInserirJustificativaComponenteSanguineo(jcs);
		
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void preAtualizarJustificativaComponenteSanguineoNulosTest() throws ApplicationBusinessException {
		
		systemUnderTest.preAtualizarJustificativaComponenteSanguineo(null);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void preAtualizarJustificativaComponenteSanguineoMatriculaNulaTest() throws BaseException {
		
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		
		whenObterServidorLogadoNull();
		
		systemUnderTest.preAtualizarJustificativaComponenteSanguineo(jcs);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros válidos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void preAtualizarJustificativaComponenteSanguineoTest() throws BaseException {
		
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		
		whenObterServidorLogado();
		
		systemUnderTest.preAtualizarJustificativaComponenteSanguineo(jcs);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void gravarJustificativaComponenteSanguineoNuloTest() throws ApplicationBusinessException {
		
		systemUnderTest.gravarJustificativaComponenteSanguineo(null);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void gravarJustificativaComponenteSanguineoSeqNuloTest() throws BaseException {
		
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
				
		whenObterServidorLogadoNull();
		
		systemUnderTest.gravarJustificativaComponenteSanguineo(jcs);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros válidos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void gravarJustificativaComponenteSanguineoComSeqTest() throws BaseException {
		
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		jcs.setSeq(123);
		
		whenObterServidorLogado();
		
		systemUnderTest.gravarJustificativaComponenteSanguineo(jcs);
		
	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }


    private void whenObterServidorLogadoNull() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(null, null)) ;
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
