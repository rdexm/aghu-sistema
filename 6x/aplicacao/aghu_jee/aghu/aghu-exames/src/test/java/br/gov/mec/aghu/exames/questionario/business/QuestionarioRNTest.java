package br.gov.mec.aghu.exames.questionario.business;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelQuestionariosDAO;
import br.gov.mec.aghu.exames.questionario.business.QuestionarioRN.QuestionarioRNExceptionCode;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class QuestionarioRNTest  extends AGHUBaseUnitTest<QuestionarioRN>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AelQuestionariosDAO mockedQuestionariosDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	@Test
	public void testVerificarDelecaoSuccess() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = cal.getTime();
		final AghParametros paramDiasDel = new AghParametros();
		paramDiasDel.setVlrNumerico(new BigDecimal(300));
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}

		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(paramDiasDel);
			systemUnderTest.executarBeforeDeleteQuestionario(dataCriadoEm);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarDelecaoError() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -12);
		final Date dataCriadoEm = cal.getTime();
		final AghParametros paramDiasDel = new AghParametros();
		paramDiasDel.setVlrNumerico(new BigDecimal(300));
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}

		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(paramDiasDel);
			systemUnderTest.executarBeforeDeleteQuestionario(dataCriadoEm);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), QuestionarioRNExceptionCode.AEL_00343);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertQuestionarioError01() {
		try {
			AelQuestionarios questionario = new AelQuestionarios();
			systemUnderTest.executarBeforeInsertQuestionario(questionario);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), QuestionarioRNExceptionCode.AEL_00353);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertQuestionarioSuccess() {
		try {
			AelQuestionarios questionario = new AelQuestionarios();
			RapServidores servidor = new RapServidores();
			questionario.setServidor(servidor);
			questionario.setServidorAlterado(servidor);
			
			try {
				whenObterServidorLogado();
			} catch (BaseException e) {
				fail();
			}

			systemUnderTest.executarBeforeInsertQuestionario(questionario);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateQuestionarioError01() {
		try {
			final AelQuestionarios questionarioNew = new AelQuestionarios();
			
			final AelQuestionarios questionarioOld = new AelQuestionarios();
			try {
				whenObterServidorLogadoNull();
			} catch (BaseException e) {
				fail();
			}

			Mockito.when(mockedQuestionariosDAO.obterOriginal(Mockito.any(AelQuestionarios.class))).thenReturn(questionarioOld);
			systemUnderTest.executarBeforeUpdateQuestionario(questionarioNew);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), QuestionarioRNExceptionCode.AEL_00353);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateQuestionarioError02() {
		try {
			RapServidores servidor = new RapServidores();
			AelQuestionarios questionarioNew = new AelQuestionarios();
			questionarioNew.setDescricao("Descrição New");
			questionarioNew.setServidor(servidor);
			questionarioNew.setServidorAlterado(servidor);
			
			final AelQuestionarios questionarioOld = new AelQuestionarios();
			questionarioOld.setDescricao("Descrição Old");

			try {
				whenObterServidorLogado();
			} catch (BaseException e) {
				fail();
			}

			Mockito.when(mockedQuestionariosDAO.obterOriginal(Mockito.any(AelQuestionarios.class))).thenReturn(questionarioOld);
			systemUnderTest.executarBeforeUpdateQuestionario(questionarioNew);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), QuestionarioRNExceptionCode.AEL_00346);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateQuestionarioSuccess() {
		try {
			RapServidores servidor = new RapServidores();
			AelQuestionarios questionarioNew = new AelQuestionarios();
			questionarioNew.setDescricao("Descrição");
			questionarioNew.setServidor(servidor);
			questionarioNew.setServidorAlterado(servidor);
			
			final AelQuestionarios questionarioOld = new AelQuestionarios();
			questionarioOld.setDescricao("Descrição");
			
			try {
				whenObterServidorLogado();
			} catch (BaseException e) {
				fail();
			}

			Mockito.when(mockedQuestionariosDAO.obterOriginal(Mockito.any(AelQuestionarios.class))).thenReturn(questionarioOld);
			systemUnderTest.executarBeforeUpdateQuestionario(questionarioNew);
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

    private void whenObterServidorLogadoNull() throws BaseException {
		RapServidores rap =  null;
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
