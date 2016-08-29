package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelRetornoCartaDAO;
import br.gov.mec.aghu.exames.dao.AelRetornoCartaJnDAO;
import br.gov.mec.aghu.model.AelRetornoCarta;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RetornoCartaRNTest extends AGHUBaseUnitTest<RetornoCartaRN>{

	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelRetornoCartaDAO mockedAelRetornoCartaDAO;
	@Mock
	private AelRetornoCartaJnDAO mockedAelRetornoCartaJnDAO;	
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inciar() throws BaseException{
			whenObterServidorLogado();
    }
    
	@Test
	public void testExecutarBeforeInsertRetornoCarta() throws ApplicationBusinessException {
		final AelRetornoCarta retornoCarta = new AelRetornoCarta();
		
		try {
			systemUnderTest.executarBeforeInsertRetornoCarta(retornoCarta);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarAfterUpdateModeloCarta() throws ApplicationBusinessException {
		final RapServidoresId servidorId = new RapServidoresId(111, Short.valueOf("2"));
		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);
		final AelRetornoCarta retornoCartaOld = new AelRetornoCarta();
		retornoCartaOld.setDescricao("AAA");
		retornoCartaOld.setServidor(servidor);
		final AelRetornoCarta retornoCartaNew = new AelRetornoCarta();
		retornoCartaNew.setDescricao("BBB");			
		
		systemUnderTest.executarAfterUpdateRetornoCarta(retornoCartaNew, retornoCartaOld);
	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
