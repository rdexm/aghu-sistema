package br.gov.mec.aghu.exames.contratualizacao.business;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.contratualizacao.business.SolicitacaoContratualizacaoCommand.SolicitacaoContratualizacaoActionExceptionCode;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SolicitacaoContratualizacaoCommandTest extends AGHUBaseUnitTest<SolicitacaoContratualizacaoCommand>{

	public enum SolicitacaoContratualizacaoActionTestExceptionCode implements
		BusinessExceptionCode {
		MENSAGEM_NRO_CARTAO_SAUDE_INVALIDO;

	}
	
	private static final Log log = LogFactory.getLog(SolicitacaoContratualizacaoCommandTest.class);

	//private AghUnidadesFuncionaisDAO mockedAghUnidadesFuncionaisDAO;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	//private AghParametrosDAO mockedAghParametrosDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
	
	protected Throwable e;

	@Test
	public void definirAtendimentoSolicitacaoNullNullTest() {
		try {
			systemUnderTest.associarAtendimentoSolicitacao(null);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void definirAtendimentoSolicitacaoNullVazioTest() {
		try {
			systemUnderTest.associarAtendimentoSolicitacao(
					new HashMap<String, Object>());
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_ATENDIMENTO_NAO_LOCALIZADO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void definirAtendimentoSolicitacaoSucessoTest() {
		try {
			AghAtendimentos atd = new AghAtendimentos();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			mapa.put(SolicitacaoContratualizacaoCommand.ATENDIMENTO_AGHU, atd);
			systemUnderTest.associarAtendimentoSolicitacao(mapa);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail("Não deveria ter entrado aqui.");
		}
	}
	
	@Test
	public void buscarParametroSucessoTest() {
		final BigDecimal val = new BigDecimal("33");
		try {
			Mockito.when(mockedParametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_UNIDADE_CONTRATUALIZACAO.name())).thenReturn(val);
			short valorParam = systemUnderTest.buscarValorNumericoParametroUnidade();
			Assert.assertEquals(val.shortValue(), valorParam);
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail("Não deveria ter entrado aqui: " +  e.getLocalizedMessage());
		}
	}
	
	@Test
	public void buscarParametroNullTest() {
		try {
			Mockito.when(mockedParametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_UNIDADE_CONTRATUALIZACAO.name())).thenReturn(null);
			systemUnderTest.buscarValorNumericoParametroUnidade();
			Assert.fail("deveria ter gerado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_PARAMETRO_UNIDADE_FUNCIONAL_NAO_LOCALIZADO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

//	@Test
	public void definirUnidadeSolicitacaoSucessoTest() {
		final Short trintaETres = Short.valueOf("33");
		final AghUnidadesFuncionais unidade = new AghUnidadesFuncionais(trintaETres);
		unidade.setAtivo(true);
		final BigDecimal val = new BigDecimal("33");
		try {

			Mockito.when(mockedParametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_UNIDADE_CONTRATUALIZACAO.name())).thenReturn(val);

			Mockito.when(mockedAghuFacade.obterUnidadeFuncional(Mockito.anyShort())).thenReturn(unidade);
			systemUnderTest.associarUnidadeFuncionalSolicitacao();
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail("Não deveria ter entrado aqui.");
		}
	}
	
//	@Test
	public void definirUnidadeSolicitacaoNaoEncontradoTest() {
		final Short trintaETres = Short.valueOf("33");
		final AghUnidadesFuncionais unidade = new AghUnidadesFuncionais(trintaETres);
		unidade.setAtivo(true);
		final BigDecimal val = new BigDecimal("33");
		try {

			Mockito.when(mockedParametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_UNIDADE_CONTRATUALIZACAO.name())).thenReturn(val);

			Mockito.when(mockedAghuFacade.obterUnidadeFuncional(Mockito.anyShort())).thenReturn(null);

			systemUnderTest.associarUnidadeFuncionalSolicitacao();
			Assert.fail("deveria ter gerado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_NAO_LOCALIZADA);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void associarInformacoesClinicasOKTest() {
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		SolicitacaoExame se = new SolicitacaoExame();
		String info = "Info";
		se.setInformacoesClinicas(info);
		mapa.put(ContratualizacaoCommand.SOLICITACAO_INTEGRACAO, se);
		try {
			systemUnderTest.associarInformacoesClinicas(mapa);
			Assert.assertEquals(info, systemUnderTest.getSolicitacao().getInformacoesClinicas());
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void associarInformacoesClinicasMapaNuloTest() {
		try {
			systemUnderTest.associarInformacoesClinicas(null);
			Assert.fail("deveria ter gerado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
	}
	
	@Test
	public void associarInformacoesClinicasSemInformacoesTest() {
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		try {
			systemUnderTest.associarInformacoesClinicas(mapa);
			Assert.fail("deveria ter gerado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_SOLICITACAO_INTEGRACAO_NAO_ENCONTRADA);
		}
	}
	
	
	@Test
	public void associarConvenioSaudePlanoOkTest() {
		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		mapa.put(SolicitacaoContratualizacaoCommand.HEADER_INTEGRACAO, new Header());
		try {
			Mockito.when(mockedFaturamentoFacade.obterConvenioSaudePlanoAtivo(Mockito.anyShort(), Mockito.anyByte())).thenReturn(convenioSaudePlano);
			systemUnderTest.associarConvenioSaudePlano(mapa);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void associarConvenioSaudePlanoMapaNuloTest() {
		try {
			systemUnderTest.associarConvenioSaudePlano(null);
			Assert.fail("deveria ter gerado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
	}
	
	@Test
	public void associarConvenioSaudePlanoSemInformacoesTest() {
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		try {
			systemUnderTest.associarConvenioSaudePlano(mapa);
			Assert.fail("deveria ter gerado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_HEADER_DADOS_INTEGRACAO_NULO);
		}
	}

	
	@Test
	public void gerarExamesDependentesSucessoTest() {
		
		AelSolicitacaoExames solicitacaoExames = this.initSolicitacaoExames();
		
		try {
			systemUnderTest.gerarExamesDependentes(solicitacaoExames, null);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	private AelSolicitacaoExames initSolicitacaoExames(){
		
		AelSinonimoExame sinonimoExames = new AelSinonimoExame();
		AelExames exames = new AelExames();
		exames.getSinonimoExames().add(sinonimoExames);
		
		AelExamesMaterialAnalise materialAnalise = new AelExamesMaterialAnalise();
		materialAnalise.setAelExames(exames);
		
		AipPacientes paciente = new AipPacientes();
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setId(new AelUnfExecutaExamesId());
		unfExecutaExames.setAelExamesMaterialAnalise(materialAnalise);
		
		AghUnidadesFuncionais unidadesFuncionais = new AghUnidadesFuncionais((short)1);
		AghAtendimentos atendimento = new AghAtendimentos(1);
		atendimento.setUnidadeFuncional(unidadesFuncionais);
		atendimento.setPaciente(paciente);
		
		AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setIndUsoO2(Boolean.FALSE);
		itemSolicitacaoExames.setAelUnfExecutaExames(unfExecutaExames);
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setAtendimento(atendimento);
		solicitacaoExames.setUnidadeFuncionalAreaExecutora(unidadesFuncionais);
		solicitacaoExames.getItensSolicitacaoExame().add(itemSolicitacaoExames);
		
		return solicitacaoExames;
	}
	
}
