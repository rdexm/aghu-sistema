package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelModeloCartaJnDAO;
import br.gov.mec.aghu.exames.dao.AelModeloCartasDAO;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ModeloCartaRNTest extends AGHUBaseUnitTest<ModeloCartaRN>{

	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelModeloCartasDAO mockedAelModeloCartasDAO;
	@Mock
	private AelModeloCartaJnDAO mockedAelModeloCartaJnDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inciar() throws BaseException {
    	whenObterServidorLogado();
    }
    
	@Test
	public void testExecutarBeforeInsertModeloCarta() throws ApplicationBusinessException {
		final AelModeloCartas modeloCarta = new AelModeloCartas();
		
		try {
			systemUnderTest.executarBeforeInsertModeloCarta(modeloCarta);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarAfterUpdateModeloCarta() throws ApplicationBusinessException {
		final RapServidoresId servidorId = new RapServidoresId(111, Short.valueOf("2"));
		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);
		final AelModeloCartas modeloCartaOld = new AelModeloCartas();
		modeloCartaOld.setNome("AAA");
		modeloCartaOld.setServidor(servidor);
		final AelModeloCartas modeloCartaNew = new AelModeloCartas();
		modeloCartaNew.setNome("BBB");			
				
		systemUnderTest.executarAfterUpdateModeloCarta(modeloCartaNew, modeloCartaOld);
	}
	
	@Test
	public void testExecutarAfterDeleteModeloCarta() throws ApplicationBusinessException {
		final RapServidoresId servidorId = new RapServidoresId(111, Short.valueOf("2"));
		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);
		final AelModeloCartas modeloCartaOld = new AelModeloCartas();
		modeloCartaOld.setServidor(servidor);
				
		systemUnderTest.executarAfterDeleteModeloCarta(modeloCartaOld);
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
 