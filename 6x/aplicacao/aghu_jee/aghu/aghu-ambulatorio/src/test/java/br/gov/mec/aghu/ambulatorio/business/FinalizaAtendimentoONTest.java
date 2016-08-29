package br.gov.mec.aghu.ambulatorio.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;

public class FinalizaAtendimentoONTest extends AGHUBaseUnitTest<FinalizaAtendimentoON> {

	@Mock
	private MamAnamnesesDAO mockedMamAnamnesesDAO;

	@Mock
	private MamEvolucoesDAO mockedMamEvolucoesDAO;

	@Mock
	private ICascaFacade mockedCascaFacade;

	@Mock
	private FinalizaAtendimentoON mokedFinalizaAtendimentoON;

	private static final Log log = LogFactory.getLog(FinalizaAtendimentoONTest.class);

	@Test(expected = ApplicationBusinessException.class)
	public void verificarAnamneseEvolucaoNaoNulosTest() throws ApplicationBusinessException {
		MamAnamneses anamnese = new MamAnamneses();
		MamEvolucoes evolucao = new MamEvolucoes();
		AacConsultas consulta = new AacConsultas();
		consulta.setNumero(9999);
		AacGradeAgendamenConsultas gradeAgendamenConsulta = new AacGradeAgendamenConsultas();
		gradeAgendamenConsulta.setProcedimento(false);
		consulta.setGradeAgendamenConsulta(gradeAgendamenConsulta);
		this.esperarObterAnamnese(anamnese);
		this.esperarObterEvolucao(evolucao);
		systemUnderTest.verificarAnamneseEvolucao(consulta);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void verificarAnamneseEvolucaoNulosTest() throws Exception {
		MamAnamneses anamnese = null;
		MamEvolucoes evolucao = null;
		AacConsultas consulta = new AacConsultas();
		consulta.setNumero(9999);
		AacGradeAgendamenConsultas gradeAgendamenConsulta = new AacGradeAgendamenConsultas();
		gradeAgendamenConsulta.setProcedimento(true);
		consulta.setGradeAgendamenConsulta(gradeAgendamenConsulta);
		this.esperarObterAnamnese(anamnese);
		this.esperarObterEvolucao(evolucao);
		this.esperarUsuarioTemPerfil(false);

		FinalizaAtendimentoON systemUnderTestPower = PowerMockito.spy(systemUnderTest);
		PowerMockito.doReturn("").when(systemUnderTestPower, "obterLoginUsuarioLogado");		

		this.esperarObterEvolucaoQuestionarioAtivaPorNumeroConsulta();
		systemUnderTestPower.verificarAnamneseEvolucao(consulta);
		Assert.fail("Deveria ter lançado exceção CONCLUSAO_NAO_EFETUADA_1");
	}

	@Test
	public void verificarAnamneseEvolucaoTest() {
		try {
			MamAnamneses anamnese = new MamAnamneses();
			MamEvolucoes evolucao = null;
			AacConsultas consulta = new AacConsultas();
			consulta.setNumero(9999);
			AacGradeAgendamenConsultas gradeAgendamenConsulta = new AacGradeAgendamenConsultas();
			gradeAgendamenConsulta.setProcedimento(false);
			consulta.setGradeAgendamenConsulta(gradeAgendamenConsulta);
			this.esperarObterAnamnese(anamnese);
			this.esperarObterEvolucao(evolucao);
			systemUnderTest.verificarAnamneseEvolucao(consulta);
			assert (true);
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
			assert (false);
		}
	}

	private void esperarUsuarioTemPerfil(boolean retorno) {
		Mockito.when(mockedCascaFacade.usuarioTemPerfil(Mockito.anyString(), Mockito.eq("ENF05.1"))).thenReturn(retorno);
	}

	private void esperarObterEvolucaoQuestionarioAtivaPorNumeroConsulta() {
		Mockito.when(mockedMamEvolucoesDAO.obterEvolucaoQuestionarioAtivaPorNumeroConsulta(Mockito.anyInt())).thenReturn(null);
	}

	private void esperarObterAnamnese(final MamAnamneses anamnese) {
		Mockito.when(mockedMamAnamnesesDAO.obterAnamneseAtivaPorNumeroConsulta(Mockito.anyInt())).thenReturn(anamnese);
	}

	private void esperarObterEvolucao(final MamEvolucoes evolucao) {
		Mockito.when(mockedMamEvolucoesDAO.obterEvolucaoAtivaPorNumeroConsulta(Mockito.anyInt())).thenReturn(evolucao);
	}
}
