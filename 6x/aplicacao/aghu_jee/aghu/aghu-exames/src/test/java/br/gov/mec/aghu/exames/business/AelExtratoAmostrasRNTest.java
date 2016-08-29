package br.gov.mec.aghu.exames.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lucas
 *
 */
public class AelExtratoAmostrasRNTest extends AGHUBaseUnitTest<AelExtratoAmostrasRN>{

	private static final Log log = LogFactory.getLog(AelExtratoAmostrasRNTest.class);

	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Before
	public void doBeforeEachTestCase() throws BaseException{

		whenObterServidorLogado();
		
	}

	@Test
	public void beforeInsertAelExtratoAmostrasTest() {

		AelExtratoAmostras extrato = new AelExtratoAmostras();

		try {
			systemUnderTest.beforeInsertAelExtratoAmostras(extrato);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		} 

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
