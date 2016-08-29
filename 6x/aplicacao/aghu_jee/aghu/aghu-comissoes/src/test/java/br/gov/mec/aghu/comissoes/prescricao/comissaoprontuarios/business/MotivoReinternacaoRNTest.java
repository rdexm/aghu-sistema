package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MotivoReinternacaoRNTest extends AGHUBaseUnitTest<MotivoReinternacaoRN> {

	private static final String NOME_PESSOA_FISICA = "PESSOA FÍSICA";
	private static final String DESCRICAO = "Descrição";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void preInsertMotivoReinternacaoSuccess() throws BaseException {

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.TRUE, Boolean.TRUE);

		whenObterServidorLogado();
		systemUnderTest.preInsertMpmMotivoReinternacao(motivoReinternacao);

		Assert.assertEquals(DESCRICAO, motivoReinternacao.getDescricao());
		Assert.assertEquals(NOME_PESSOA_FISICA, motivoReinternacao.getServidor().getPessoaFisica().getNome());
		Assert.assertNotNull(motivoReinternacao.getCriadoEm());

	}

	@Test
	public void preInsertMotivoReinternacaoFailServidor() throws BaseException {

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.TRUE, Boolean.TRUE);

		try {

			whenObterServidorLogado();
			systemUnderTest.preInsertMpmMotivoReinternacao(motivoReinternacao);
		} catch (BaseException e) {
			Assert.assertEquals("RAP_00175", e.getMessage());
		}

	}

	@Test
	public void preInsertMotivoReinternacaoFailIndOutros() throws BaseException {

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.FALSE, Boolean.TRUE);

		try {

			whenObterServidorLogado();
			systemUnderTest.preInsertMpmMotivoReinternacao(motivoReinternacao);
			Assert.fail("Deveria ter ocorrido a excessão MPM_02851");
		} catch (BaseException e) {
			Assert.assertEquals("MPM_02851", e.getMessage());
		}

	}

	@Test
	public void preUpdateMotivoReinternacaoSuccess() throws BaseException {

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.TRUE, Boolean.TRUE);

		MpmMotivoReinternacao motivoReinternacaoOld = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.TRUE, Boolean.TRUE);

		whenObterServidorLogado();
		systemUnderTest.preUpdateMpmMotivoReinternacao(motivoReinternacaoOld, motivoReinternacao);

		Assert.assertEquals(DESCRICAO, motivoReinternacao.getDescricao());
		Assert.assertEquals(NOME_PESSOA_FISICA, motivoReinternacao.getServidor().getPessoaFisica().getNome());
	}

	@Test
	public void preUpdateMotivoReinternacaoFailAtivacao() throws BaseException {

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.TRUE, Boolean.TRUE);

		MpmMotivoReinternacao motivoReinternacaoOld = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.I, Boolean.TRUE, Boolean.TRUE);

		try {
			systemUnderTest.preUpdateMpmMotivoReinternacao(motivoReinternacaoOld, motivoReinternacao);
			Assert.fail("Deveria ter ocorrido a excessão MPM_02685");
		} catch (BaseException e) {
			Assert.assertEquals("MPM_02685", e.getMessage());
		}
	}

	@Test
	public void preUpdateMotivoReinternacaoFailDescricao() throws BaseException {

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1),
				DESCRICAO + " ALTERADO", DominioSituacao.A, Boolean.TRUE, Boolean.TRUE);

		MpmMotivoReinternacao motivoReinternacaoOld = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.TRUE, Boolean.TRUE);

		try {

			whenObterServidorLogado();
			systemUnderTest.preUpdateMpmMotivoReinternacao(motivoReinternacaoOld, motivoReinternacao);

			Mockito.verify(mockedServidorLogadoFacade).obterServidorLogado();

			Assert.fail("Deveria ter ocorrido a excessão MPM_02831");
		} catch (BaseException e) {
			Assert.assertEquals("MPM_02831", e.getMessage());
		}
	}

	@Test
	public void preUpdateMotivoReinternacaoFailIndOutros() throws BaseException {

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.FALSE, Boolean.TRUE);

		MpmMotivoReinternacao motivoReinternacaoOld = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.FALSE, Boolean.FALSE);

		try {

			whenObterServidorLogado();
			systemUnderTest.preUpdateMpmMotivoReinternacao(motivoReinternacaoOld, motivoReinternacao);
			Assert.fail("Deveria ter ocorrido a excessão MPM_02851");
		} catch (BaseException e) {
			Assert.assertEquals("MPM_02851", e.getMessage());
		}
	}

	@Test
	public void preDeleteMotivoReinternacaoSuccess() throws BaseException {

		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.TEN);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(aghParametro);

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.FALSE, Boolean.FALSE);
		motivoReinternacao.setCriadoEm(anteOntem.getTime());

		systemUnderTest.preDeleteMpmMotivoReinternacao(motivoReinternacao);

		Mockito.verify(mockedParametroFacade).buscarAghParametro(Mockito.any(AghuParametrosEnum.class));

	}

	@Test
	public void preDeleteMotivoReinternacaoFailParam() throws BaseException {

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(null);

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.FALSE, Boolean.FALSE);
		motivoReinternacao.setCriadoEm(anteOntem.getTime());

		try {
			systemUnderTest.preDeleteMpmMotivoReinternacao(motivoReinternacao);
			Mockito.verify(mockedParametroFacade).buscarAghParametro(Mockito.any(AghuParametrosEnum.class));
			Assert.fail("Deveria ter ocorrido a excessão MPM_00680");
		} catch (BaseException e) {
			Assert.assertEquals("MPM_00680", e.getMessage());
		}
	}

	@Test
	public void preDeleteMotivoReinternacaoFailPrazo() throws BaseException {
		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(aghParametro);

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO,
				DominioSituacao.A, Boolean.FALSE, Boolean.FALSE);
		motivoReinternacao.setCriadoEm(anteOntem.getTime());

		try {
			systemUnderTest.preDeleteMpmMotivoReinternacao(motivoReinternacao);
			Mockito.verify(mockedParametroFacade).buscarAghParametro(Mockito.any(AghuParametrosEnum.class));
			Assert.fail("Deveria ter ocorrido a excessão MPM_00681");
		} catch (BaseException e) {
			Assert.assertEquals("MPM_00681", e.getMessage());
		}
	}

	private void whenObterServidorLogado() throws BaseException {
		RapServidores rap = new RapServidores(new RapServidoresId(1, (short) 1));
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);

	}
}
