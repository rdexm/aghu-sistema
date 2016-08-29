package br.gov.mec.aghu.exames.questionario.business;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.dao.AelQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelValorValidoQuestaoDAO;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelValorValidoQuestao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class QuestaoRNTest  extends AGHUBaseUnitTest<QuestaoRN>{

	@Mock
	private IAgendamentoExamesFacade mockedAgendamentoExamesFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelQuestaoDAO mockedAelQuestaoDAO;
	@Mock
	private AelValorValidoQuestaoDAO mockedAelValorValidoQuestaoDAO;
	@Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	@Before
	public void iniciar() {
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
	}
	
	@Test
	public void testInserir() {
		final AelQuestao aelQuestao = new AelQuestao();
		
		try {
			systemUnderTest.persistir(aelQuestao);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testAtualizar() {
		final AelQuestao aelQuestao = new AelQuestao();
		aelQuestao.setSeq(1);
		try {
			
			Mockito.when(mockedAelQuestaoDAO.obterOriginal(aelQuestao.getSeq())).thenReturn(aelQuestao);
			systemUnderTest.persistir(aelQuestao);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testAtualizarErroDescricao() {
		final AelQuestao aelQuestao = new AelQuestao();
		aelQuestao.setSeq(1);
		aelQuestao.setDescricao("Descrição alterada");
		final AelQuestao aelQuestaoOriginal = new AelQuestao();
		aelQuestaoOriginal.setSeq(1);
		aelQuestaoOriginal.setDescricao("Descrição");
		try {
			Mockito.when(mockedAelQuestaoDAO.obterOriginal(Mockito.anyInt())).thenReturn(aelQuestaoOriginal);
			systemUnderTest.persistir(aelQuestao);
			Assert.fail("Descrição alterada deve retornar erro");
		} catch (BaseException e) {
			mockedAelQuestaoDAO = null;
		}
	}
	
	@Test
	public void testRemover() {
		final AelQuestao aelQuestao = new AelQuestao();
		aelQuestao.setSeq(1);
		aelQuestao.setCriadoEm(new Date());
		try {
			Mockito.when(mockedAelQuestaoDAO.obterOriginal(Mockito.anyInt())).thenReturn(aelQuestao);
			
			Mockito.when(mockedAelQuestaoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aelQuestao);
			
			Mockito.when(mockedAelQuestaoDAO.contarReferenciaQuestionario(Mockito.any(AelQuestao.class))).thenReturn(0l);

			Mockito.when(mockedAelValorValidoQuestaoDAO.contarValoresValidosQuestaoPorQuestao(Mockito.anyInt())).thenReturn(0l);
			
			systemUnderTest.remover(1);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testRemoverComReferenciaValorValido() {
		final AelQuestao aelQuestao = new AelQuestao();
		aelQuestao.setSeq(1);
		aelQuestao.setCriadoEm(new Date());
		final Set<AelValorValidoQuestao> aelValorValidoQuestaos = new HashSet<AelValorValidoQuestao>(1);
		aelValorValidoQuestaos.add(new AelValorValidoQuestao());
		aelQuestao.setAelValorValidoQuestaos(aelValorValidoQuestaos);
		try {
			Mockito.when(mockedAelQuestaoDAO.obterOriginal(Mockito.anyInt())).thenReturn(aelQuestao);

			Mockito.when(mockedAelQuestaoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aelQuestao);
			
			Mockito.when(mockedAelQuestaoDAO.contarReferenciaQuestionario(Mockito.any(AelQuestao.class))).thenReturn(0l);

			Mockito.when(mockedAelValorValidoQuestaoDAO.contarValoresValidosQuestaoPorQuestao(Mockito.anyInt())).thenReturn(1l);

			systemUnderTest.remover(1);
			Assert.fail("Possui referência de valores válidos.");
		} catch (ApplicationBusinessException e) {
			mockedAelQuestaoDAO = null;
		}
	}
	
	
	@Test
	public void testRemoverComReferenciaQuestoesQuestionario() {
		final AelQuestao aelQuestao = new AelQuestao();
		aelQuestao.setSeq(1);
		aelQuestao.setCriadoEm(new Date());
		try {
			
			Mockito.when(mockedAelQuestaoDAO.obterOriginal(Mockito.anyInt())).thenReturn(aelQuestao);

			Mockito.when(mockedAelQuestaoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aelQuestao);
			
			Mockito.when(mockedAelQuestaoDAO.contarReferenciaQuestionario(Mockito.any(AelQuestao.class))).thenReturn(1l);

			Mockito.when(mockedAelValorValidoQuestaoDAO.contarValoresValidosQuestaoPorQuestao(Mockito.anyInt())).thenReturn(0l);
			
			systemUnderTest.remover(1);
			Assert.fail("Possui referência de Questões Questionário.");
		} catch (ApplicationBusinessException e) {
			mockedAelQuestaoDAO = null;
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
