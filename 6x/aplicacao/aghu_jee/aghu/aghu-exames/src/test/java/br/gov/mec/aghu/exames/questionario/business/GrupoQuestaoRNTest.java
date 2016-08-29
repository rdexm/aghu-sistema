package br.gov.mec.aghu.exames.questionario.business;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.exames.dao.AelGrupoQuestaoJnDao;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelGrupoQuestaoJn;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@PrepareForTest( BaseJournalFactory.class)
@RunWith(PowerMockRunner.class)
public class GrupoQuestaoRNTest extends AGHUBaseUnitTest<GrupoQuestaoRN>{

	@Mock
	private AelGrupoQuestaoJnDao mockedAelGrupoQuestaoJnDao;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void incio() {
    	try {
    		AelGrupoQuestaoJn journal = new AelGrupoQuestaoJn();
			whenObterServidorLogado();
			PowerMockito.mockStatic(BaseJournalFactory.class);
			PowerMockito.when(BaseJournalFactory.getBaseJournal(Mockito.any(DominioOperacoesJournal.class), Mockito.any(Class.class), Mockito.anyString()))
			.thenReturn(journal);
		} catch (BaseException e) {
			fail();
		}
    }
    
	@Test
	public void testVerificarAposAtualizacaoAtualizarJn() {
		try {
			final AelGrupoQuestao aelGrupoQuestao = new AelGrupoQuestao();
			aelGrupoQuestao.setSeq(1);
			aelGrupoQuestao.setDescricao("Teste");
			final AelGrupoQuestao aelGrupoQuestaoOld = new AelGrupoQuestao();
			aelGrupoQuestaoOld.setSeq(2);
			aelGrupoQuestao.setDescricao("Teste");
			
			systemUnderTest.executarAposAtualizar(aelGrupoQuestao, aelGrupoQuestaoOld);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exceção gerada: " + e.getMessage());
		}
	}
	
	@Test
	public void testVerificarAposAtualizacaoAtualizarJn2() {
		try {
			final AelGrupoQuestao aelGrupoQuestao = new AelGrupoQuestao();
			aelGrupoQuestao.setSeq(1);
			aelGrupoQuestao.setDescricao("Teste");
			final AelGrupoQuestao aelGrupoQuestaoOld = new AelGrupoQuestao();
			aelGrupoQuestaoOld.setSeq(1);
			aelGrupoQuestao.setDescricao("Teste2");
			
			systemUnderTest.executarAposAtualizar(aelGrupoQuestao, aelGrupoQuestaoOld);
		} catch (Exception e) {
			Assert.fail("Exceção gerada: " + e.getMessage());
		}
	}
	
	@Test
	public void testVerificarAposAtualizacaoAtualizarJn3() {
		try {
			final AelGrupoQuestao aelGrupoQuestao = new AelGrupoQuestao();
			aelGrupoQuestao.setSeq(2);
			aelGrupoQuestao.setDescricao("Teste");
			final AelGrupoQuestao aelGrupoQuestaoOld = new AelGrupoQuestao();
			aelGrupoQuestaoOld.setSeq(1);
			aelGrupoQuestao.setDescricao("Teste2");
			
			systemUnderTest.executarAposAtualizar(aelGrupoQuestao, aelGrupoQuestaoOld);
		} catch (Exception e) {
			Assert.fail("Exceção gerada: " + e.getMessage());
		}
	}
	
	@Test
	public void testVerificarAposAtualizacaoNaoAtualizarJn() {
		try {
			final AelGrupoQuestao aelGrupoQuestao = new AelGrupoQuestao();
			aelGrupoQuestao.setSeq(1);
			aelGrupoQuestao.setDescricao("Teste");
			final AelGrupoQuestao aelGrupoQuestaoOld = new AelGrupoQuestao();
			aelGrupoQuestaoOld.setSeq(1);
			aelGrupoQuestaoOld.setDescricao("Teste");
			
			systemUnderTest.executarAposAtualizar(aelGrupoQuestao, aelGrupoQuestaoOld);
		} catch (Exception e) {
			Assert.fail("Exceção gerada: " + e.getMessage());
		}
	}
	
	@Test
	public void testVerificarAposAtualizacaoNaoAtualizarJn2() {
		try {
			final AelGrupoQuestao aelGrupoQuestao = new AelGrupoQuestao();
			final AelGrupoQuestao aelGrupoQuestaoOld = new AelGrupoQuestao();
			
			systemUnderTest.executarAposAtualizar(aelGrupoQuestao, aelGrupoQuestaoOld);
		} catch (Exception e) {
			Assert.fail("Exceção gerada: " + e.getMessage());
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
