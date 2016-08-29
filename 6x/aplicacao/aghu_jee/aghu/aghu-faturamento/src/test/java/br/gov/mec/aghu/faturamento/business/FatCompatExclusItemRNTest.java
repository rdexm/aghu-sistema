package br.gov.mec.aghu.faturamento.business;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemJnDAO;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompatExclusItemJn;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest( BaseJournalFactory.class )
public class FatCompatExclusItemRNTest extends AGHUBaseUnitTest<FatCompatExclusItemRN>{

	@Mock
	private FatCompatExclusItemJnDAO mockedFatCompatExclusItemJnDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicio() throws BaseException {
		whenObterServidorLogado();
		
		PowerMockito.mockStatic(BaseJournalFactory.class);
		PowerMockito.when(BaseJournalFactory.getBaseJournal(Mockito.any(DominioOperacoesJournal.class), Mockito.any(Class.class), Mockito.anyString()))
		.thenReturn(new FatCompatExclusItemJn());
    }
	
	/**
	 * Testa inserção em Journal com dados nulos
	 * @throws ApplicationBusinessException 
	 */
	// corvalao-agora @Test
	@Test
	public void testInserirJournalFatCompatExclusItemRNNulosTest() throws ApplicationBusinessException {
	
		FatCompatExclusItem fatCompatExclusItem = new FatCompatExclusItem();
		systemUnderTest.inserirJournalItemProcedHospitalar(fatCompatExclusItem, DominioOperacoesJournal.INS);
	}

	/**
	 * Testa inserção em Journal com dados
	 * @throws ApplicationBusinessException 
	 */
	//FIXME - corvalao-agora @Test
	@Test
	public void testInserirJournalFatCompatExclusItemRNTest() throws ApplicationBusinessException {
	
		
		FatCompatExclusItem fatCompatExclusItem = new FatCompatExclusItem();
		FatItensProcedHospitalar itemProcedHospitalar = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId id = new FatItensProcedHospitalarId((short) 11, 11);
		itemProcedHospitalar.setId(id);
		fatCompatExclusItem.setItemProcedHosp(itemProcedHospitalar);
		fatCompatExclusItem.setItemProcedHospCompatibiliza(itemProcedHospitalar);
		systemUnderTest.inserirJournalItemProcedHospitalar(fatCompatExclusItem, DominioOperacoesJournal.INS);
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
