package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class ItemContaHospitalarONTest extends AGHUBaseUnitTest<ItemContaHospitalarON>{
	
	
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
    @Mock
    private FatItemContaHospitalarDAO mockedFatItemContaHospitalarDAO;
    @Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IServidorLogadoFacade servidorLogadoFacade;
    
	@Before
	public void doBefore() throws Exception {
		whenObterServidorLogado();
		
		AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("1");
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

	}

	/**
	 * Testa validaServAnest com dados nullos
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testValidaServAnestNullo() throws ApplicationBusinessException {
		Mockito.when(mockedRegistroColaboradorFacade.obterRapServidoresPorChavePrimaria(Mockito.any(RapServidoresId.class))).thenReturn(null);
		systemUnderTest.validaServAnest(3994, (short) 1, Short.parseShort("1"));
	}
	
	/**
	 * Testa validaServAnest com dados
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testValidaServAnest() throws ApplicationBusinessException {
		RapServidores rapServ = new RapServidores();
		RapPessoasFisicas pessoa = new RapPessoasFisicas();
		rapServ.setPessoaFisica(pessoa);
		Mockito.when(mockedRegistroColaboradorFacade.obterRapServidoresPorChavePrimaria(Mockito.any(RapServidoresId.class))).thenReturn(rapServ);

		List<RapPessoaTipoInformacoes> lstRapPessoaTipoInformacoes = new ArrayList<RapPessoaTipoInformacoes>();
		Mockito.when(mockedRegistroColaboradorFacade.listarRapPessoaTipoInformacoesPorPesCodigoTiiSeq(Mockito.anyInt(), Mockito.anyShort())).thenReturn(lstRapPessoaTipoInformacoes);

		try{
			systemUnderTest.validaServAnest(3994, (short) 1, Short.parseShort("1"));
			
		} catch (ApplicationBusinessException e) {
		}
	}
	
	/**
	 * Testa fatCompletaPhi
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testFatCompletaPhi() throws BaseException {
		FatItemContaHospitalar fatItem = new FatItemContaHospitalar(); 
		Mockito.when(mockedFatItemContaHospitalarDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(fatItem);
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		FatItemContaHospitalarId id = new FatItemContaHospitalarId();
		fatItem.setId(id);
		//FatContasHospitalares contaHosp = new FatContasHospitalares();
		fatItem.setContaHospitalar(null);
		FatProcedHospInternos procHospInt = new FatProcedHospInternos();
		procHospInt.setSeq(11368);
		fatItem.setProcedimentoHospitalarInterno(procHospInt);
		fatItem.setQuantidadeRealizada((short) 2);
		
		
		systemUnderTest.fatCompletaPhi(fatItem, null);
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
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
