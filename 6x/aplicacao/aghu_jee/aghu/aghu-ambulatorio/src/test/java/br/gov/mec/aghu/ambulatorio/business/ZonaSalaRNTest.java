package br.gov.mec.aghu.ambulatorio.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ZonaSalaRNTest extends AGHUBaseUnitTest<ZonaSalaRN>{


	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade; 	
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
			/**
	 * Testa Trigger antes de atualizar 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = BaseException.class)
	public void zonaSalaAntesAtualizar1() throws Exception {
	
		whenObterServidorLogado();
		
		AacUnidFuncionalSalas oldZonaSala = new AacUnidFuncionalSalas();
		oldZonaSala.setIndExcluido(true);
		AacUnidFuncionalSalas newZonaSalaos = new AacUnidFuncionalSalas();
		newZonaSalaos.setIndExcluido(false);
		systemUnderTest.zonaSalaAntesAtualizar(oldZonaSala, newZonaSalaos);
	}	

	/**
	 * Testa Trigger antes de atualizar 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = BaseException.class)
	public void zonaSalaAntesAtualizar2() throws Exception {
	
		whenObterServidorLogado();
		
		AacUnidFuncionalSalas oldZonaSala = new AacUnidFuncionalSalas();
		oldZonaSala.setIndExcluido(true);
		AacUnidFuncionalSalas newZonaSalaos = new AacUnidFuncionalSalas();
		newZonaSalaos.setIndExcluido(true);
		systemUnderTest.zonaSalaAntesAtualizar(oldZonaSala, newZonaSalaos);
	}	

	/**
	 * Testa Trigger antes de atualizar 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void zonaSalaAntesAtualizar3() throws Exception {
	
		whenObterServidorLogado();
		
		AacUnidFuncionalSalas oldZonaSala = new AacUnidFuncionalSalas();
		oldZonaSala.setIndExcluido(false);
		AacUnidFuncionalSalas newZonaSalaos = new AacUnidFuncionalSalas();
		newZonaSalaos.setIndExcluido(false);
		systemUnderTest.zonaSalaAntesAtualizar(oldZonaSala, newZonaSalaos);
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