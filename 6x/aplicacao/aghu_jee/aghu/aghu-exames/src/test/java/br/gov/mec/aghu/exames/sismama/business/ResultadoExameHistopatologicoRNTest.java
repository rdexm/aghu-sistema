package br.gov.mec.aghu.exames.sismama.business;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoResJnDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSismamaHistoCad;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.AelSismamaHistoResJn;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
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
public class ResultadoExameHistopatologicoRNTest  extends AGHUBaseUnitTest<ResultadoExameHistopatologicoRN>{

	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private AelSismamaHistoResJnDAO mockedAelSismamaHistoResJnDAO;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Before
	public void iniciar() {
		try {
			PowerMockito.mockStatic(BaseJournalFactory.class);
			PowerMockito.when(BaseJournalFactory.getBaseJournal(Mockito.any(DominioOperacoesJournal.class), Mockito.any(Class.class), Mockito.anyString()))
			.thenReturn(new AelSismamaHistoResJn());

			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
	}
	
	@Test
	public void testExecutarAntesInserir() {
		final AelSismamaHistoRes newSismamaHistoRes = new AelSismamaHistoRes();
		
		try {
			systemUnderTest.executarAntesInserir(newSismamaHistoRes);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarAposAtualizar() throws ApplicationBusinessException {
		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId(
				1, Short.valueOf("1"));
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		final AelSismamaHistoCad sismamaHistoCad = new AelSismamaHistoCad();
		sismamaHistoCad.setCodigo("C_001");
		final AelSismamaHistoRes oldSismamaHistoRes = new AelSismamaHistoRes();
		oldSismamaHistoRes.setResposta("A");
		oldSismamaHistoRes.setItemSolicitacaoExame(itemSolicitacaoExame);
		oldSismamaHistoRes.setSismamaHistoCad(sismamaHistoCad);
		final AelSismamaHistoRes newSismamaHistoRes = new AelSismamaHistoRes();
		newSismamaHistoRes.setResposta("B");

		systemUnderTest.executarAposAtualizar(oldSismamaHistoRes,
				newSismamaHistoRes);
	}
	
	@Test
	public void testExecutarAposRemover() throws ApplicationBusinessException {
		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId(
				1, Short.valueOf("1"));
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		final AelSismamaHistoCad sismamaHistoCad = new AelSismamaHistoCad();
		sismamaHistoCad.setCodigo("C_001");
		final AelSismamaHistoRes oldSismamaHistoRes = new AelSismamaHistoRes();
		oldSismamaHistoRes.setResposta("A");
		oldSismamaHistoRes.setItemSolicitacaoExame(itemSolicitacaoExame);
		oldSismamaHistoRes.setSismamaHistoCad(sismamaHistoCad);

		systemUnderTest.executarAposRemover(oldSismamaHistoRes);
	}
	
	@Test
	public void testVerificarExameSismama() {
		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames(); 
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSolicitacaoExame(solicitacaoExame);
		final List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = new ArrayList<AelItemSolicitacaoExames>();
		listaItemSolicitacaoExame.add(itemSolicitacaoExame);
		final AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		unfExecutaExame.setItemSolicitacaoExames(listaItemSolicitacaoExame);
		
		Mockito.when(mockedAelItemSolicitacaoExameDAO.pesquisarItemSolicitacaoExamePorNumeroApDescricaoEIseSeqp(Mockito.anyLong(), Mockito.anyShort(), 
				Mockito.anyString(), Mockito.anyInt())).thenReturn(listaItemSolicitacaoExame);

		try {
			systemUnderTest.verificarExameSismama(1L, Short.valueOf("1"), "SISMAMA", 2);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
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
