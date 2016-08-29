package br.gov.mec.aghu.exames.questionario.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDadoQuestionario;
import br.gov.mec.aghu.exames.dao.AelQuestoesQuestionarioDAO;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelQuestoesQuestionarioId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class QuestaoQuestionarioRNTest extends AGHUBaseUnitTest<QuestaoQuestionarioRN> {

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock	
	private AelQuestoesQuestionarioDAO mockedQuestoesQuestionariosDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void testExcluir() {
		try {
			AelQuestoesQuestionarioId aelQuestoesQuestionario = new AelQuestoesQuestionarioId(1,1);	
			Mockito.when(mockedQuestoesQuestionariosDAO.obterPorChavePrimaria(Mockito.any(AelQuestoesQuestionarioId.class))).thenReturn(new AelQuestoesQuestionario());
			systemUnderTest.excluir(aelQuestoesQuestionario);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testInserir() {
			
			final AelQuestoesQuestionario aelQuestoesQuestionario = new AelQuestoesQuestionario();
			aelQuestoesQuestionario.setQuestao(new AelQuestao(1, new RapServidores(), "Questão 1", DominioTipoDadoQuestionario.T, new Date()));
			aelQuestoesQuestionario.setAelQuestionarios(new AelQuestionarios(1, "Questionário 1", null, DominioSituacao.A, new Date(), new RapServidores()));
			aelQuestoesQuestionario.setObrigatorio(true);
			Mockito.when(mockedQuestoesQuestionariosDAO.obterOriginal(Mockito.any(AelQuestoesQuestionarioId.class))).thenReturn(null);
			try {
				whenObterServidorLogado();
				systemUnderTest.persistir(aelQuestoesQuestionario);
			} catch (BaseException e) {
				Assert.fail("Exceção gerada: " + e.getCode());
			}
	}
	
	@Test
	public void testAtualizar() throws BaseException {
		try {
			final AelQuestoesQuestionarioId aelQuestoesQuestionarioId = new AelQuestoesQuestionarioId(1,1);
			final AelQuestoesQuestionario aelQuestoesQuestionario = new AelQuestoesQuestionario();
			aelQuestoesQuestionario.setId(aelQuestoesQuestionarioId);
			aelQuestoesQuestionario.setQuestao(new AelQuestao(1, new RapServidores(), "Questão 1", DominioTipoDadoQuestionario.T, new Date()));
			aelQuestoesQuestionario.setAelQuestionarios(new AelQuestionarios(1, "Questionário 1", null, DominioSituacao.A, new Date(), new RapServidores()));
			aelQuestoesQuestionario.setObrigatorio(true);
			
			Mockito.when(mockedQuestoesQuestionariosDAO.obterOriginal(aelQuestoesQuestionarioId)).thenReturn(aelQuestoesQuestionario);
			systemUnderTest.persistir(aelQuestoesQuestionario);
			
		} catch (ApplicationBusinessException e) {
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
