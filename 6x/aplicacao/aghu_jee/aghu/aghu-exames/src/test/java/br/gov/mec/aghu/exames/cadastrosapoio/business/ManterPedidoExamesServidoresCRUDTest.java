package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Before;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AelServidoresExameUnid;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author fwinck
 *
 */
public class ManterPedidoExamesServidoresCRUDTest extends AGHUBaseUnitTest<ManterPedidoExamesServidoresCRUD>{

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IRegistroColaboradorFacade mockedIRegistroColaboradorFacade;
	
	private AelServidoresExameUnid servidorExame;
	
	@Before
	public void doBeforeEachTestCase() {

		servidorExame = new AelServidoresExameUnid();

	}



}