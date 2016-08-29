package br.gov.mec.aghu.exames.solicitacao.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelControleNumeroUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelDocResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDependentesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesProvaDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelPacAgrpPesqExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameEnforceRN.ItemSolicitacaoExameEnforceRNExceptionCode;
import br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameEnforceRN.VariaveisInterfaceFaturamento;
import br.gov.mec.aghu.exames.solicitacao.vo.CancelarExamesAreaExecutoraVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.AelExamesProvaId;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class ItemSolicitacaoExameEnforceRNTest extends AGHUBaseUnitTest<ItemSolicitacaoExameEnforceRN> {

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private AelExtratoItemSolicitacaoDAO mockedAelExtratoItemSolicitacaoDAO;
	@Mock
	private AelItemHorarioAgendadoDAO mockedAelItemHorarioAgendadoDAO;
	@Mock
	private AelExamesDependentesDAO mockedAelExamesDependentesDAO;
	@Mock
	private AelHorarioExameDispDAO mockedAelHorarioExameDispDAO;
	@Mock
	private AelResultadoExameDAO mockedAelResultadoExameDAO;
	@Mock
	private AelDocResultadoExameDAO mockedAelDocResultadoExameDAO;
	@Mock
	private AelAgrpPesquisaXExameDAO mockedAelAgrpPesquisaXExameDAO;
	@Mock
	private AelPacAgrpPesqExameDAO mockedAelPacAgrpPesqExameDAO;
	@Mock
	private AelAmostraItemExamesDAO mockedAelAmostraItemExamesDAO;
	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private AelExamesProvaDAO mockedAelExamesProvaDAO;
	@Mock
	private AelAmostrasDAO mockedAelAmostrasDAO;
	@Mock
	private AelControleNumeroUnicoDAO mockedAelControleNumeroUnicoDAO;
	@Mock
	private IExamesFacade mockedAelAmostraItemExamesRN;
	@Mock
	private ItemSolicitacaoExameRN mockedItemSolicitacaoExameRN;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	AelItemSolicitacaoExames itemSolicitacaoExame;
	AelItemSolicitacaoExames itemSolicitacaoExameVar;

	@Before
	public void doBeforeEachTestCase() {
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId(123, Short.valueOf("1"));
		itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(id);

		itemSolicitacaoExameVar = new AelItemSolicitacaoExames();
		itemSolicitacaoExameVar.setId(id);

	}

	/**
	 * Passa situações diferentes deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoItemSolicitacaoAlteradaSituacoesDiferentesTest() throws Exception {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AC");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AE");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarSituacaoItemSolicitacaoAlterada(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertTrue(atual);

	}

	/**
	 * Passa situações iguais deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoItemSolicitacaoAlteradaSituacoesIguaisTest() throws Exception {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AC");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AC");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarSituacaoItemSolicitacaoAlterada(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Passa situações nulas deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoItemSolicitacaoAlteradaSituacoesNulasTest() throws Exception {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("1");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("1");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarSituacaoItemSolicitacaoAlterada(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Passa situação inativa de subir exceção.
	 * 
	 * @throws BaseException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarSituacaoAtivaNOKTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setIndSituacao(DominioSituacao.I);

		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString())).thenReturn(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		systemUnderTest.verificarSituacaoAtiva(itemSolicitacaoExames);

	}

	/**
	 * Passa situação ativa NÃO sube exceção.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoAtivaOKTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setIndSituacao(DominioSituacao.A);

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString())).thenReturn(sitItemSolicitacoes);

		try {

			systemUnderTest.verificarSituacaoAtiva(itemSolicitacaoExames);

		} catch (final ApplicationBusinessException e) {

			fail("Não deveria ocorrer exceção! Exception: " + e.getMessage());

		}

	}

	/**
	 * Testa que se for agendado a situação e a data do agendamento for NULA
	 * deve subir exceção de código AEL_00473.
	 */
	@Test
	public void atualizarExtratoDthrAgendaNaoEncontradaTest() {
		RapServidores servidorLogado = mockedServidorLogadoFacade.obterServidorLogado();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AG.toString());

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		try {
			whenObterServidorLogado();
		} catch (BaseException e1) {
		}

		Mockito.when(mockedAelExtratoItemSolicitacaoDAO.buscarMaiorSeqp(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(null);

		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarMenorHedDthrAgenda(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(null);

		try {

			systemUnderTest.atualizarExtrato(itemSolicitacaoExame, true);
			fail("Deveria ter ocorrido a exceção de código AEL_00473!");

		} catch (final ApplicationBusinessException e) {

			assertEquals("AEL_00473", e.getMessage());

		} catch (final BaseException e) {
			fail("Deveria ter ocorrido a exceção de código AEL_00473!");
		}

	}

	/**
	 * Testa que se for agendado a situação, não é gerada exceção quando existe
	 * hed_dthr_agenda.
	 */
	@Test
	public void atualizarExtratoDthrAgendaEncontradaTest() {

		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AG.toString());
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		// Testa se esse método não será chamado (retornando um valor não deve
		// gerar exceção)
		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarMenorHedDthrAgenda(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(new Date());

		try {

			systemUnderTest.obterDataHoraEvento(itemSolicitacaoExame);

		} catch (final ApplicationBusinessException e) {

			fail("Exceção gerada: " + e.getCode());

		}

	}

	@Test
	public void verificarSituacaoAtivaSuccessTest() {
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setIndSituacao(DominioSituacao.A);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString()))
				.thenReturn(situacaoItemSolicitacao);

		try {
			systemUnderTest.verificarSituacaoAtiva(itemSolicitacaoExame);

			// Passa no teste caso não tenha sido lançada uma exceção
			assertEquals(1, 1);
		} catch (final ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	public void verificarSituacaoAtivaErrorTest() {
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setIndSituacao(DominioSituacao.I);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString()))
				.thenReturn(situacaoItemSolicitacao);

		try {
			systemUnderTest.verificarSituacaoAtiva(itemSolicitacaoExame);
			fail("Exceção não gerada");
		} catch (final ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00420);
		}
	}

	/**
	 * Testa que se não for agendado a situação, não é gerada exceção por não
	 * existir hed_dthr_agenda.
	 */
	@Test
	public void obterDataHoraEventoSuccessTest() {

		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AE.toString());
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		// Testa se esse método não será chamado (retornando null deve gerar
		// exceção)
		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarMenorHedDthrAgenda(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(null);

		try {
			systemUnderTest.obterDataHoraEvento(itemSolicitacaoExame);

			// Passa no teste caso não tenha sido lançada uma exceção
			assertEquals(1, 1);
		} catch (final ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}

	}

	/**
	 * Testa que se existirem resultados e documentos não anulados, não é gerada
	 * exceção
	 */
	@Test
	public void verificarResultadoSuccessTest() {
		Mockito.when(mockedAelResultadoExameDAO.existeResultadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(true);

		Mockito.when(
				mockedAelDocResultadoExameDAO.existeDocumentosAnexadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(true);

		try {
			systemUnderTest.verificarResultado(itemSolicitacaoExame);

			// Passa no teste caso não tenha sido lançada uma exceção
			assertEquals(1, 1);
		} catch (final ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
	}

	/**
	 * Testa que se existirem resultados não anulados, não é gerada exceção
	 */
	@Test
	public void verificarResultadoSuccess2Test() {
		Mockito.when(mockedAelResultadoExameDAO.existeResultadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(true);

		Mockito.when(
				mockedAelDocResultadoExameDAO.existeDocumentosAnexadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(false);

		try {
			systemUnderTest.verificarResultado(itemSolicitacaoExame);

			// Passa no teste caso não tenha sido lançada uma exceção
			assertEquals(1, 1);
		} catch (final ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
	}

	/**
	 * Testa que se existirem documentos não anulados, não é gerada exceção
	 */
	@Test
	public void verificarResultadoSuccess3Test() {

		Mockito.when(mockedAelResultadoExameDAO.existeResultadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(false);

		Mockito.when(
				mockedAelDocResultadoExameDAO.existeDocumentosAnexadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(true);

		try {
			systemUnderTest.verificarResultado(itemSolicitacaoExame);

			// Passa no teste caso não tenha sido lançada uma exceção
			assertEquals(1, 1);
		} catch (final ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
	}

	/**
	 * Testa que se não existirem resultados ou documentos não anulados, é
	 * gerada exceção
	 */
	@Test
	public void verificarResultadoErrorTest() {

		Mockito.when(mockedAelResultadoExameDAO.existeResultadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(false);

		Mockito.when(
				mockedAelDocResultadoExameDAO.existeDocumentosAnexadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(false);

		try {

			systemUnderTest.verificarResultado(itemSolicitacaoExame);
			fail("Exceção não gerada");

		} catch (final ApplicationBusinessException e) {

			assertEquals(e.getCode(), ItemSolicitacaoExameEnforceRNExceptionCode.AEL_02550);

		}

	}

	/**
	 * Testa validação de data entre os intervalos
	 */
	@Test
	public void verificarDataHoraEmIntervalosSuccessTest() throws ApplicationBusinessException {
		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal(1440));

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		final Date agora = new Date();
		itemSolicitacaoExame.setDthrProgramada(agora);

		final AelItemSolicitacaoExames itemEmAnalise = new AelItemSolicitacaoExames();
		itemEmAnalise.setDthrProgramada(DateUtil.adicionaDias(agora, -2));

		try {

			systemUnderTest.verificarDataHoraEmIntervalos(itemSolicitacaoExame, itemEmAnalise);

		} catch (final ApplicationBusinessException e) {

			fail("Exceção gerada: " + e.getCode());

		}

	}

	/**
	 * Testa o erro na validação de data entre os intervalos
	 */
	@Test
	public void verificarDataHoraEmIntervalosErrorTest() throws ApplicationBusinessException {
		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal(5));

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		final Date agora = new Date();
		itemSolicitacaoExame.setDthrProgramada(agora);

		final AelItemSolicitacaoExames itemEmAnalise = new AelItemSolicitacaoExames();
		itemEmAnalise.setDthrProgramada(agora);

		try {
			systemUnderTest.verificarDataHoraEmIntervalos(itemSolicitacaoExame, itemEmAnalise);

			fail("Exceção não gerada");
		} catch (final ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00501);
		}
	}

	@Test
	/**
	 * Testa validação de data entre os intervalos
	 */
	public void testVerificarProvasExameJaSolicitadoSuccess() {
		AelExamesProvaId id = new AelExamesProvaId();
		id.setEmaExaSiglaEhProva("AAA");
		id.setEmaManSeqEhProva(111);
		AelExamesProva prova = new AelExamesProva();
		prova.setId(id);
		final List<AelExamesProva> provas = new ArrayList<AelExamesProva>();
		provas.add(prova);

		Mockito.when(mockedAelExamesProvaDAO.buscarProvasExameSolicitado(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(provas);

		AelExames exame = new AelExames();
		exame.setSigla("BBB");
		itemSolicitacaoExame.setExame(exame);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setSeq(111);
		itemSolicitacaoExame.setMaterialAnalise(materialAnalise);

		itemSolicitacaoExameVar.setExame(exame);
		itemSolicitacaoExameVar.setMaterialAnalise(materialAnalise);

		try {
			systemUnderTest.verificarProvasExameJaSolicitado(itemSolicitacaoExame, itemSolicitacaoExameVar);

			// Passa no teste caso não tenha sido lançada uma exceção
			assertEquals(1, 1);
		} catch (BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	/**
	 * Testa validação de data entre os intervalos
	 */
	public void testVerificarProvasExameJaSolicitadoSuccess2() {
		AelExamesProvaId id = new AelExamesProvaId();
		id.setEmaExaSiglaEhProva("AAA");
		id.setEmaManSeqEhProva(111);
		AelExamesProva prova = new AelExamesProva();
		prova.setId(id);
		final List<AelExamesProva> provas = new ArrayList<AelExamesProva>();
		provas.add(prova);

		Mockito.when(mockedAelExamesProvaDAO.buscarProvasExameSolicitado(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(provas);

		AelExames exame = new AelExames();
		exame.setSigla("AAA");
		itemSolicitacaoExame.setExame(exame);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setSeq(222);
		itemSolicitacaoExame.setMaterialAnalise(materialAnalise);

		itemSolicitacaoExameVar.setExame(exame);
		itemSolicitacaoExameVar.setMaterialAnalise(materialAnalise);

		try {
			systemUnderTest.verificarProvasExameJaSolicitado(itemSolicitacaoExame, itemSolicitacaoExameVar);

			// Passa no teste caso não tenha sido lançada uma exceção
			assertEquals(1, 1);
		} catch (BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	/**
	 * Testa a falha na validação de data entre os intervalos
	 */
	public void testVerificarProvasExameJaSolicitadoError() {
		AelExamesProvaId id = new AelExamesProvaId();
		id.setEmaExaSiglaEhProva("AAA");
		id.setEmaManSeqEhProva(111);
		AelExamesProva prova = new AelExamesProva();
		prova.setId(id);
		final List<AelExamesProva> provas = new ArrayList<AelExamesProva>();
		provas.add(prova);

		Mockito.when(mockedAelExamesProvaDAO.buscarProvasExameSolicitado(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(provas);

		AelExames exame = new AelExames();
		exame.setSigla("AAA");
		itemSolicitacaoExame.setExame(exame);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setSeq(111);
		itemSolicitacaoExame.setMaterialAnalise(materialAnalise);

		itemSolicitacaoExameVar.setExame(exame);
		itemSolicitacaoExameVar.setMaterialAnalise(materialAnalise);

		try {
			systemUnderTest.verificarProvasExameJaSolicitado(itemSolicitacaoExame, itemSolicitacaoExameVar);

			fail("Exceção não gerada");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00502);
		}
	}

	@Test
	/**
	 * Testa passar os dois parâmetros como nulo deve retornar falso, pois não
	 * foram alterados. (RN23)
	 */
	public void verificaAlteracaoPrioridadeFalsoNuloTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		final boolean atual = systemUnderTest.verificaAlteracaoPrioridade(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	@Test
	/**
	 * Testa passar os dois parâmetros iguais deve retornar falso, pois não
	 * foram alterados. (RN23)
	 */
	public void verificaAlteracaoPrioridadeFalsoValoresIguaisTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		itemSolicitacaoExame.setPrioridadeExecucao(Byte.valueOf("1"));
		itemSolicitacaoExameOriginal.setPrioridadeExecucao(Byte.valueOf("1"));

		final boolean atual = systemUnderTest.verificaAlteracaoPrioridade(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	@Test
	/**
	 * Testa passar os dois parâmetros diferentes sendo que um deles será nulo.
	 * Deve retornar true, pois foram alterados. (RN23)
	 */
	public void verificaAlteracaoPrioridadeVerdadeiroUmValorNuloTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		itemSolicitacaoExame.setPrioridadeExecucao(null);
		itemSolicitacaoExameOriginal.setPrioridadeExecucao(Byte.valueOf("1"));

		final boolean atual = systemUnderTest.verificaAlteracaoPrioridade(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	@Test
	/**
	 * Testa passar os dois parâmetros diferentes Deve retornar true, pois foram
	 * alterados. (RN23)
	 */
	public void verificaAlteracaoPrioridadeVerdadeiroValoresDiferentesTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		itemSolicitacaoExame.setPrioridadeExecucao(Byte.valueOf("2"));
		itemSolicitacaoExameOriginal.setPrioridadeExecucao(Byte.valueOf("1"));

		final boolean atual = systemUnderTest.verificaAlteracaoPrioridade(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros como nulo deve retornar falso, pois não
	 * foram alterados. (RN25)
	 */
	public void verificarAlteracaoAmostrasIntervalosFalsoNuloTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		final boolean atual = systemUnderTest.verificarAlteracaoAmostrasIntervalos(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros iguais deve retornar falso, pois não foram
	 * alterados. (RN25)
	 */
	public void verificarAlteracaoAmostrasIntervalosFalsoValoresIguaisTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		itemSolicitacaoExame.setNroAmostras(Byte.valueOf("1"));
		itemSolicitacaoExameOriginal.setNroAmostras(Byte.valueOf("1"));

		itemSolicitacaoExame.setIntervaloDias(Byte.valueOf("1"));
		itemSolicitacaoExameOriginal.setIntervaloDias(Byte.valueOf("1"));

		final Date date1 = new Date();
		itemSolicitacaoExame.setIntervaloHoras(date1);
		itemSolicitacaoExameOriginal.setIntervaloHoras(date1);

		final boolean atual = systemUnderTest.verificarAlteracaoAmostrasIntervalos(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros diferentes sendo que um deles será nulo.
	 * Deve retornar true, pois foram alterados. (RN25)
	 */
	public void verificarAlteracaoAmostrasIntervalosVerdadeiroUmValorNuloTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		itemSolicitacaoExame.setNroAmostras(Byte.valueOf("1"));
		itemSolicitacaoExameOriginal.setNroAmostras(Byte.valueOf("1"));

		itemSolicitacaoExame.setIntervaloDias(null);
		itemSolicitacaoExameOriginal.setIntervaloDias(Byte.valueOf("1"));

		final Date date1 = new Date();
		Date date2 = new Date();
		date2 = DateUtil.adicionaHoras(date2, 3);
		itemSolicitacaoExame.setIntervaloHoras(date1);
		itemSolicitacaoExameOriginal.setIntervaloHoras(date2);

		final boolean atual = systemUnderTest.verificarAlteracaoAmostrasIntervalos(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros diferentes Deve retornar true, pois foram
	 * alterados. (RN25)
	 */
	public void verificarAlteracaoAmostrasIntervalosVerdadeiroValoresDiferentesTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();

		itemSolicitacaoExame.setNroAmostras(Byte.valueOf("1"));
		itemSolicitacaoExameOriginal.setNroAmostras(Byte.valueOf("2"));

		itemSolicitacaoExame.setIntervaloDias(Byte.valueOf("3"));
		itemSolicitacaoExameOriginal.setIntervaloDias(Byte.valueOf("1"));

		final Date date1 = new Date();
		Date date2 = new Date();
		date2 = DateUtil.adicionaDias(date2, 3);
		itemSolicitacaoExame.setIntervaloHoras(date1);
		itemSolicitacaoExameOriginal.setIntervaloHoras(date2);

		final boolean atual = systemUnderTest.verificarAlteracaoAmostrasIntervalos(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	@Test
	/**
	 * Teste de cobertura. (RN26)
	 */
	public void atualizarItensAmostraComRestanteTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelAmostraItemExames amostraItemExames1 = new AelAmostraItemExames();
		final AelAmostraItemExamesId amostraItemExamesId1 = new AelAmostraItemExamesId();
		amostraItemExamesId1.setAmoSeqp(1);
		amostraItemExamesId1.setAmoSoeSeq(1);
		amostraItemExamesId1.setIseSeqp(Short.valueOf("1"));
		amostraItemExamesId1.setIseSoeSeq(1);
		amostraItemExames1.setId(amostraItemExamesId1);

		final List<AelAmostraItemExames> listAmostrasItemExamesRestante1 = new ArrayList<AelAmostraItemExames>();
		listAmostrasItemExamesRestante1.add(amostraItemExames1);
		itemSolicitacaoExame.setAelAmostraItemExames(listAmostrasItemExamesRestante1);

		final AelAmostraItemExames amostraItemExames2 = new AelAmostraItemExames();
		final List<AelAmostraItemExames> listAmostrasItemExamesRestante2 = new ArrayList<AelAmostraItemExames>();
		listAmostrasItemExamesRestante2.add(amostraItemExames2);
		final AelAmostras amostra = new AelAmostras();

		Mockito.when(mockedAelAmostraItemExamesDAO.buscarAelAmostraItemExamesPorAmostraPorItemSolicitacaoExame(
				Mockito.anyInt(), Mockito.anyShort(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(listAmostrasItemExamesRestante2);

		Mockito.when(mockedAelAmostrasDAO.buscarAmostrasPorId(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(amostra);

		systemUnderTest.atualizarItensAmostra(itemSolicitacaoExame);

	}

	@Test
	/**
	 * Teste de cobertura. (RN26)
	 */
	public void atualizarItensAmostraSemRestanteTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelAmostraItemExames amostraItemExames1 = new AelAmostraItemExames();
		final AelAmostraItemExamesId amostraItemExamesId1 = new AelAmostraItemExamesId();
		amostraItemExamesId1.setAmoSeqp(1);
		amostraItemExamesId1.setAmoSoeSeq(1);
		amostraItemExamesId1.setIseSeqp(Short.valueOf("1"));
		amostraItemExamesId1.setIseSoeSeq(1);
		amostraItemExames1.setId(amostraItemExamesId1);

		final List<AelAmostraItemExames> listAmostrasItemExamesRestante1 = new ArrayList<AelAmostraItemExames>();
		listAmostrasItemExamesRestante1.add(amostraItemExames1);
		itemSolicitacaoExame.setAelAmostraItemExames(listAmostrasItemExamesRestante1);

		final List<AelAmostraItemExames> listAmostrasItemExamesRestante2 = new ArrayList<AelAmostraItemExames>();
		final AelAmostras amostra = new AelAmostras();

		Mockito.when(mockedAelAmostraItemExamesDAO.buscarAelAmostraItemExamesPorAmostraPorItemSolicitacaoExame(
				Mockito.anyInt(), Mockito.anyShort(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(listAmostrasItemExamesRestante2);

		Mockito.when(mockedAelAmostrasDAO.buscarAmostrasPorId(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(amostra);

		systemUnderTest.atualizarItensAmostra(itemSolicitacaoExame);

	}

	@Test
	/**
	 * Testa se a unidade que executa o exame deve gerar número. (RN28)
	 */
	public void atualizarNumeroUnicoGeraNumeroUnicoIgualaNTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AC.toString());
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(Short.valueOf("1"));
		itemSolicitacaoExame.setUnidadeFuncional(unidadeFuncional);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		systemUnderTest.atualizarNumeroUnico(itemSolicitacaoExame);

	}

	@Test
	/**
	 * Testa de cobertura. (RN28)
	 */
	public void atualizarNumeroUnicoCodigoSituacaoIgualaAXTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AX.toString());
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(Short.valueOf("1"));
		itemSolicitacaoExame.setUnidadeFuncional(unidadeFuncional);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		systemUnderTest.atualizarNumeroUnico(itemSolicitacaoExame);

	}

	@Test
	/**
	 * Testa de cobertura. (RN28)
	 */
	public void atualizarNumeroUnicoOrigemIgualaTTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AC.toString());
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(Short.valueOf("1"));
		itemSolicitacaoExame.setUnidadeFuncional(unidadeFuncional);

		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		final AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.T);
		atendimento.setUnidadeFuncional(unidadeFuncional);
		solicitacaoExame.setAtendimento(atendimento);
		itemSolicitacaoExame.setSolicitacaoExame(solicitacaoExame);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		systemUnderTest.atualizarNumeroUnico(itemSolicitacaoExame);

	}

	@Test
	/**
	 * Teste de cobertura. (RN28)
	 */
	public void atualizarNumeroUnicoDtNumeroUnicoIgualaDthrProgramadaTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AC.toString());
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(Short.valueOf("1"));
		itemSolicitacaoExame.setUnidadeFuncional(unidadeFuncional);

		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		final AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.T);
		atendimento.setUnidadeFuncional(unidadeFuncional);
		solicitacaoExame.setAtendimento(atendimento);
		itemSolicitacaoExame.setSolicitacaoExame(solicitacaoExame);

		final List<AelAmostraItemExames> listAmostrasItemExames = new ArrayList<AelAmostraItemExames>();
		final AelAmostraItemExames amostraItemExame = new AelAmostraItemExames();
		final AelAmostras amostra = new AelAmostras();
		amostra.setNroUnico(Integer.valueOf(1));
		final Date data1 = new Date();
		amostra.setDtNumeroUnico(data1);

		amostraItemExame.setAelAmostras(amostra);
		listAmostrasItemExames.add(amostraItemExame);
		itemSolicitacaoExame.setAelAmostraItemExames(listAmostrasItemExames);

		itemSolicitacaoExame.setDthrProgramada(data1);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		systemUnderTest.atualizarNumeroUnico(itemSolicitacaoExame);

	}

	@Test
	/**
	 * Testa passar todos parâmetros como nulo deve retornar falso, pois não
	 * foram alterados. (RN29)
	 */
	public void verificarSituacaoItemSolicitacaoModificadaFalsoNuloTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarSituacaoItemSolicitacaoModificada(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros iguais deve retornar falso, pois não foram
	 * alterados. (RN29)
	 */
	public void verificarSituacaoItemSolicitacaoModificadaFalsoValoresIguaisTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AC");
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AC");
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarSituacaoItemSolicitacaoModificada(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros diferentes sendo que um deles será nulo.
	 * Deve retornar true, pois foram alterados. (RN29)
	 */
	public void verificarSituacaoItemSolicitacaoModificadaVerdadeiroUmValorNuloTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AC");
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(null);
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarSituacaoItemSolicitacaoModificada(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros diferentes Deve retornar true, pois foram
	 * alterados. (RN29)
	 */
	public void verificarSituacaoItemSolicitacaoModificadaVerdadeiroValoresDiferentesTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AC");
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("CS");
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarSituacaoItemSolicitacaoModificada(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros como nulo deve retornar falso, pois não
	 * foram alterados. (RN33)
	 */
	public void verificarCodigoSituacaoNumeroAPFalsoNuloTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarCodigoSituacaoNumeroAP(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	@Test
	/**
	 * Testa passar todos parâmetros iguais deve retornar falso, pois não foram
	 * alterados. (RN33)
	 */
	public void verificarCodigoSituacaoNumeroAPFalsoValoresIguaisTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AE");
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();

		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		// itemSolicitacaoExame.setNumeroAp(1);

		sitItemSolicitacoesOriginal.setCodigo("AE");
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);
		// itemSolicitacaoExameOriginal.setNumeroAp(1);

		final boolean atual = systemUnderTest.verificarCodigoSituacaoNumeroAP(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertFalse(atual);

	}

	/**
	 * Testa passar todos parâmetros diferentes sendo que um deles será nulo.
	 * Deve retornar true, pois foram alterados. (RN33)
	 */
	@Test
	public void verificarCodigoSituacaoNumeroAPVerdadeiroUmValorNuloTest() throws BaseException {
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AE");
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();

		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		// itemSolicitacaoExame.setNumeroAp(1);

		sitItemSolicitacoesOriginal.setCodigo(null);
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);
		// itemSolicitacaoExameOriginal.setNumeroAp(1);

		final boolean atual = systemUnderTest.verificarCodigoSituacaoNumeroAP(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	/**
	 * Testa passar todos parâmetros diferentes Deve retornar true, pois foram
	 * alterados. (RN33)
	 */
	@Test
	public void verificarCodigoSituacaoNumeroAPVerdadeiroValoresDiferentesTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AE");
		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();

		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);
		// itemSolicitacaoExame.setNumeroAp(1);

		sitItemSolicitacoesOriginal.setCodigo("AC");
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);
		// itemSolicitacaoExameOriginal.setNumeroAp(2);

		final boolean atual = systemUnderTest.verificarCodigoSituacaoNumeroAP(itemSolicitacaoExame,
				itemSolicitacaoExameOriginal);
		assertTrue(atual);

	}

	/**
	 * Testa de cobertura.
	 */
	@Test
	public void atualizarCancelamentoMocSeqIgualESitCodigoACTest() throws BaseException {
		RapServidores servidorLogado = mockedServidorLogadoFacade.obterServidorLogado();
		
		final AelExames exame = new AelExames();

		final AelMateriaisAnalises material = new AelMateriaisAnalises();

		final AelSitItemSolicitacoes situacaoItemSolicitacaoCA = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoCA.setCodigo(DominioSituacaoItemSolicitacaoExame.CA.toString());

		final AelSitItemSolicitacoes situacaoItemSolicitacaoAC = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoAC.setCodigo(DominioSituacaoItemSolicitacaoExame.AC.toString());

		final CancelarExamesAreaExecutoraVO examesDependentes = new CancelarExamesAreaExecutoraVO();
		examesDependentes.setIndCancelaAutomatico(DominioSimNao.S);

		final AelMotivoCancelaExames motivoCancelaExames = new AelMotivoCancelaExames();
		motivoCancelaExames.setSeq(Short.valueOf("1"));

		final AghParametros parametroMocCancelaAlta = new AghParametros();
		parametroMocCancelaAlta.setVlrNumerico(BigDecimal.ONE);

		final AghParametros parametroMocCancelaObito = new AghParametros();
		parametroMocCancelaObito.setVlrNumerico(BigDecimal.TEN);

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacaoCA);
		itemSolicitacaoExame.setAelMotivoCancelaExames(motivoCancelaExames);
		itemSolicitacaoExame.setExame(exame);
		itemSolicitacaoExame.setMaterialAnalise(material);

		final AelItemSolicitacaoExames itemSolicitacaoExameDependente = new AelItemSolicitacaoExames();
		itemSolicitacaoExameDependente.setSituacaoItemSolicitacao(situacaoItemSolicitacaoAC);

		final List<CancelarExamesAreaExecutoraVO> listExamesDependentes = new ArrayList<CancelarExamesAreaExecutoraVO>();
		listExamesDependentes.add(examesDependentes);

		Mockito.when(mockedAelExamesDependentesDAO.buscarPorExameMaterial(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(listExamesDependentes);

		Mockito.when(mockedAelItemSolicitacaoExameDAO.obterPorId(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(itemSolicitacaoExameDependente);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametroMocCancelaAlta);

		systemUnderTest.atualizarCancelamento(itemSolicitacaoExame, NOME_MICROCOMPUTADOR, servidorLogado, new Date());

	}

	/**
	 * Testa de cobertura.
	 */
	@Test
	public void atualizarCancelamentoMocSeqIgualESitCodigoAXTest() throws BaseException {
		RapServidores servidorLogado = mockedServidorLogadoFacade.obterServidorLogado();
		final AelExames exame = new AelExames();

		final AelMateriaisAnalises material = new AelMateriaisAnalises();

		final AelSitItemSolicitacoes situacaoItemSolicitacaoCA = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoCA.setCodigo(DominioSituacaoItemSolicitacaoExame.CA.toString());

		final AelSitItemSolicitacoes situacaoItemSolicitacaoAC = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoAC.setCodigo(DominioSituacaoItemSolicitacaoExame.AX.toString());

		final CancelarExamesAreaExecutoraVO examesDependentes = new CancelarExamesAreaExecutoraVO();
		examesDependentes.setIndCancelaAutomatico(DominioSimNao.S);

		final AelMotivoCancelaExames motivoCancelaExames = new AelMotivoCancelaExames();
		motivoCancelaExames.setSeq(Short.valueOf("1"));

		final AghParametros parametroMocCancelaAlta = new AghParametros();
		parametroMocCancelaAlta.setVlrNumerico(BigDecimal.ONE);

		final AghParametros parametroMocCancelaObito = new AghParametros();
		parametroMocCancelaObito.setVlrNumerico(BigDecimal.TEN);

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacaoCA);
		itemSolicitacaoExame.setAelMotivoCancelaExames(motivoCancelaExames);
		itemSolicitacaoExame.setExame(exame);
		itemSolicitacaoExame.setMaterialAnalise(material);

		final AelItemSolicitacaoExames itemSolicitacaoExameDependente = new AelItemSolicitacaoExames();
		itemSolicitacaoExameDependente.setSituacaoItemSolicitacao(situacaoItemSolicitacaoAC);

		final List<CancelarExamesAreaExecutoraVO> listExamesDependentes = new ArrayList<CancelarExamesAreaExecutoraVO>();
		listExamesDependentes.add(examesDependentes);

		Mockito.when(mockedAelExamesDependentesDAO.buscarPorExameMaterial(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(listExamesDependentes);

		Mockito.when(mockedAelItemSolicitacaoExameDAO.obterPorId(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(itemSolicitacaoExameDependente);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametroMocCancelaAlta);

		systemUnderTest.atualizarCancelamento(itemSolicitacaoExame, NOME_MICROCOMPUTADOR, servidorLogado, new Date());

	}

	/**
	 * Testa de cobertura.
	 */
	@Test
	public void atualizarCancelamentoMocSeqIgualESitCodigoAGTest() throws BaseException {
		RapServidores servidorLogado = mockedServidorLogadoFacade.obterServidorLogado();
		final AelExames exame = new AelExames();

		final AelMateriaisAnalises material = new AelMateriaisAnalises();

		final AelSitItemSolicitacoes situacaoItemSolicitacaoCA = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoCA.setCodigo(DominioSituacaoItemSolicitacaoExame.CA.toString());

		final AelSitItemSolicitacoes situacaoItemSolicitacaoAC = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoAC.setCodigo(DominioSituacaoItemSolicitacaoExame.AG.toString());

		final CancelarExamesAreaExecutoraVO examesDependentes = new CancelarExamesAreaExecutoraVO();
		examesDependentes.setIndCancelaAutomatico(DominioSimNao.S);

		final AelMotivoCancelaExames motivoCancelaExames = new AelMotivoCancelaExames();
		motivoCancelaExames.setSeq(Short.valueOf("1"));

		final AghParametros parametroMocCancelaAlta = new AghParametros();
		parametroMocCancelaAlta.setVlrNumerico(BigDecimal.ONE);

		final AghParametros parametroMocCancelaObito = new AghParametros();
		parametroMocCancelaObito.setVlrNumerico(BigDecimal.TEN);

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacaoCA);
		itemSolicitacaoExame.setAelMotivoCancelaExames(motivoCancelaExames);
		itemSolicitacaoExame.setExame(exame);
		itemSolicitacaoExame.setMaterialAnalise(material);

		final AelItemSolicitacaoExames itemSolicitacaoExameDependente = new AelItemSolicitacaoExames();
		itemSolicitacaoExameDependente.setSituacaoItemSolicitacao(situacaoItemSolicitacaoAC);

		final List<CancelarExamesAreaExecutoraVO> listExamesDependentes = new ArrayList<CancelarExamesAreaExecutoraVO>();
		listExamesDependentes.add(examesDependentes);

		Mockito.when(mockedAelExamesDependentesDAO.buscarPorExameMaterial(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(listExamesDependentes);

		Mockito.when(mockedAelItemSolicitacaoExameDAO.obterPorId(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(itemSolicitacaoExameDependente);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametroMocCancelaAlta);

		systemUnderTest.atualizarCancelamento(itemSolicitacaoExame, NOME_MICROCOMPUTADOR, servidorLogado, new Date());

	}

	/**
	 * Testa de cobertura.
	 */
	@Test
	public void atualizarCancelamentoMocSeqIgualESitCodigoCSTest() throws BaseException {
		RapServidores servidorLogado = mockedServidorLogadoFacade.obterServidorLogado();
		final AelExames exame = new AelExames();

		final AelMateriaisAnalises material = new AelMateriaisAnalises();

		final AelSitItemSolicitacoes situacaoItemSolicitacaoCA = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoCA.setCodigo(DominioSituacaoItemSolicitacaoExame.CA.toString());

		final AelSitItemSolicitacoes situacaoItemSolicitacaoAC = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoAC.setCodigo(DominioSituacaoItemSolicitacaoExame.CS.toString());

		final CancelarExamesAreaExecutoraVO examesDependentes = new CancelarExamesAreaExecutoraVO();
		examesDependentes.setIndCancelaAutomatico(DominioSimNao.S);

		final AelMotivoCancelaExames motivoCancelaExames = new AelMotivoCancelaExames();
		motivoCancelaExames.setSeq(Short.valueOf("1"));

		final AghParametros parametroMocCancelaAlta = new AghParametros();
		parametroMocCancelaAlta.setVlrNumerico(BigDecimal.TEN);

		final AghParametros parametroMocCancelaObito = new AghParametros();
		parametroMocCancelaObito.setVlrNumerico(BigDecimal.ONE);

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacaoCA);
		itemSolicitacaoExame.setAelMotivoCancelaExames(motivoCancelaExames);
		itemSolicitacaoExame.setExame(exame);
		itemSolicitacaoExame.setMaterialAnalise(material);

		final AelItemSolicitacaoExames itemSolicitacaoExameDependente = new AelItemSolicitacaoExames();
		itemSolicitacaoExameDependente.setSituacaoItemSolicitacao(situacaoItemSolicitacaoAC);

		final List<CancelarExamesAreaExecutoraVO> listExamesDependentes = new ArrayList<CancelarExamesAreaExecutoraVO>();
		listExamesDependentes.add(examesDependentes);

		Mockito.when(mockedAelExamesDependentesDAO.buscarPorExameMaterial(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(listExamesDependentes);

		Mockito.when(mockedAelItemSolicitacaoExameDAO.obterPorId(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(itemSolicitacaoExameDependente);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametroMocCancelaAlta);

		systemUnderTest.atualizarCancelamento(itemSolicitacaoExame, NOME_MICROCOMPUTADOR, servidorLogado, new Date());

	}

	/**
	 * Passa situações diferentes deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameAgendadoParaColetadoSituacaoAlteradaDeAGparaCOTest()
			throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("CO");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AG");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameAgendadoParaColetado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertTrue(atual);

	}

	/**
	 * Se situação passar de AG para qualquer outro valor que não seja CO então
	 * deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameAgendadoParaColetadoSituacaoAlteradaDeAGparaACTest()
			throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AC");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AG");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameAgendadoParaColetado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Passa situações nulas deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameAgendadoParaColetadoSituacoesNulasTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameAgendadoParaColetado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Testa de cobertura. Deve percorrer todo o método.
	 */
	@Test
	public void atualizarHorarioExameAgendadoParaColetadoQuantidadeIgualaZeroTest() throws BaseException {

		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(new AelItemHorarioAgendadoId());

		final List<AelItemHorarioAgendado> listItensHorarioAgendados = new ArrayList<AelItemHorarioAgendado>();
		listItensHorarioAgendados.add(itemHorarioAgendado);

		final Integer quantidadeExamesNaoColetados = Integer.valueOf("0");

		final AelHorarioExameDisp horarioExame = new AelHorarioExameDisp();

		Mockito.when(mockedAelItemHorarioAgendadoDAO
				.buscarPorItemSolicitacaoExame(Mockito.any(AelItemSolicitacaoExames.class)))
				.thenReturn(listItensHorarioAgendados);

		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarQuantidadeHorariosNaoColetados(Mockito.anyShort(),
				Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(quantidadeExamesNaoColetados);

		Mockito.when(
				mockedAelHorarioExameDispDAO.obterPorId(Mockito.anyShort(), Mockito.anyInt(), Mockito.any(Date.class)))
				.thenReturn(horarioExame);

		systemUnderTest.atualizarHorarioExameAgendadoParaAreaExecutora(itemSolicitacaoExame);

	}

	/**
	 * Testa de cobertura. Não deve entrar no if de quantidade
	 */
	@Test
	public void atualizarHorarioExameAgendadoParaColetadoQuantidadeIgualaUmTest() throws BaseException {

		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(new AelItemHorarioAgendadoId());

		final List<AelItemHorarioAgendado> listItensHorarioAgendados = new ArrayList<AelItemHorarioAgendado>();
		listItensHorarioAgendados.add(itemHorarioAgendado);

		final Integer quantidadeExamesNaoColetados = Integer.valueOf("1");

		final AelHorarioExameDisp horarioExame = new AelHorarioExameDisp();

		Mockito.when(mockedAelItemHorarioAgendadoDAO
				.buscarPorItemSolicitacaoExame(Mockito.any(AelItemSolicitacaoExames.class)))
				.thenReturn(listItensHorarioAgendados);

		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarQuantidadeHorariosNaoColetados(Mockito.anyShort(),
				Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(quantidadeExamesNaoColetados);

		Mockito.when(
				mockedAelHorarioExameDispDAO.obterPorId(Mockito.anyShort(), Mockito.anyInt(), Mockito.any(Date.class)))
				.thenReturn(horarioExame);

		systemUnderTest.atualizarHorarioExameAgendadoParaAreaExecutora(itemSolicitacaoExame);

	}

	/**
	 * Se situação passar de AG para AE então deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameAgendadoParaAreaExecutoraSituacaoAlteradaDeAGparaAETest()
			throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AE");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AG");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameAgendadoParaAreaExecutora(
				itemSolicitacaoExames, itemSolicitacaoExamesOriginal);
		assertTrue(atual);

	}

	/**
	 * Se situação passar de AG para qualquer outro valor que não seja AE então
	 * deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameAgendadoParaAreaExecutoraSituacaoAlteradaDeAGparaACTest()
			throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("AC");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AG");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameAgendadoParaAreaExecutora(
				itemSolicitacaoExames, itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Passa situações nulas deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameAgendadoParaAreaExecutoraSituacoesNulasTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameAgendadoParaAreaExecutora(
				itemSolicitacaoExames, itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Se situação passar de AG para LI então deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameParaLiberadoSituacaoAlteradaDeAGparaLITest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("LI");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("AG");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameParaLiberado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertTrue(atual);

	}

	/**
	 * Se situação passar de LI e o valor antigo for diferente de LI então deve
	 * retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameParaLiberadoSituacaoAlteradaDeLIparaLITest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo("LI");

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo("LI");

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameParaLiberado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Passa situações nulas deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAlteracaoSituacaoExameParaLiberadoSituacoesNulasTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final boolean atual = systemUnderTest.verificarAlteracaoSituacaoExameParaLiberado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);
		assertFalse(atual);

	}

	/**
	 * Não deve ocorrer uma exceção. Somente quando não encontrar nenhum
	 * registro.
	 */
	@Test
	public void verificarResultadoQuandoExistirResultadosTest() throws BaseException {

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);

		final boolean existeResultados = true;
		final boolean existeDocumentosAnexados = false;

		Mockito.when(mockedAelResultadoExameDAO.existeResultadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(existeResultados);

		Mockito.when(
				mockedAelDocResultadoExameDAO.existeDocumentosAnexadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(existeDocumentosAnexados);
		try {

			systemUnderTest.verificarResultado(itemSolicitacaoExame);

		} catch (final BaseException e) {

			fail("Não deveria ocorrer exceção. Exception: " + e.getMessage());

		}

	}

	/**
	 * Não deve ocorrer uma exceção. Somente quando não encontrar nenhum
	 * registro.
	 */
	@Test
	public void verificarResultadoQuandoExistirDocumentosAnexadosTest() throws BaseException {

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);

		final boolean existeResultados = false;
		final boolean existeDocumentosAnexados = true;

		Mockito.when(mockedAelResultadoExameDAO.existeResultadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(existeResultados);

		Mockito.when(
				mockedAelDocResultadoExameDAO.existeDocumentosAnexadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(existeDocumentosAnexados);

		try {

			systemUnderTest.verificarResultado(itemSolicitacaoExame);

		} catch (final BaseException e) {

			fail("Não deveria ocorrer exceção. Exception: " + e.getMessage());

		}

	}

	/**
	 * Não deve ocorrer uma exceção. Somente quando não encontrar nenhum
	 * registro.
	 */
	@Test
	public void verificarResultadoQuandoNaoExistirTest() throws BaseException {

		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);

		final boolean existeResultados = false;
		final boolean existeDocumentosAnexados = false;

		Mockito.when(mockedAelResultadoExameDAO.existeResultadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(existeResultados);

		Mockito.when(
				mockedAelDocResultadoExameDAO.existeDocumentosAnexadosNaoAnulados(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(existeDocumentosAnexados);

		try {

			systemUnderTest.verificarResultado(itemSolicitacaoExame);
			fail("Deveria ocorrer exceção de código AEL_02550.");

		} catch (final BaseException e) {

			assertEquals("AEL_02550", e.getMessage());

		}

	}

	/**
	 * Deve retornar tipo de amostra C.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoCTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoesCO = new AelSitItemSolicitacoes();
		sitItemSolicitacoesCO.setCodigo(DominioSituacaoItemSolicitacaoExame.CO.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesAE = new AelSitItemSolicitacoes();
		sitItemSolicitacoesAE.setCodigo(DominioSituacaoItemSolicitacaoExame.AE.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesAE);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoesCO);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertEquals(DominioSituacaoAmostra.C, atual);

	}

	/**
	 * Deve retornar tipo de amostra E.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoETest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo(DominioSituacaoItemSolicitacaoExame.LI.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(DominioSituacaoItemSolicitacaoExame.AE.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertEquals(DominioSituacaoAmostra.E, atual);

	}

	/**
	 * Deve retornar tipo de amostra G.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoGTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo(DominioSituacaoItemSolicitacaoExame.AG.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(DominioSituacaoItemSolicitacaoExame.AE.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertEquals(DominioSituacaoAmostra.G, atual);

	}

	/**
	 * Deve retornar tipo de amostra U.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoUTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo(DominioSituacaoItemSolicitacaoExame.RE.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(DominioSituacaoItemSolicitacaoExame.AE.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertEquals(DominioSituacaoAmostra.U, atual);

	}

	/**
	 * Deve retornar tipo de amostra R.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoRTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo(DominioSituacaoItemSolicitacaoExame.AE.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(DominioSituacaoItemSolicitacaoExame.AX.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertEquals(DominioSituacaoAmostra.R, atual);

	}

	/**
	 * Deve retornar tipo de amostra M.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoMTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo(DominioSituacaoItemSolicitacaoExame.EC.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(DominioSituacaoItemSolicitacaoExame.AX.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertEquals(DominioSituacaoAmostra.M, atual);

	}

	/**
	 * Deve retornar tipo de amostra A.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoATest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo(DominioSituacaoItemSolicitacaoExame.CA.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(DominioSituacaoItemSolicitacaoExame.AX.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertEquals(DominioSituacaoAmostra.A, atual);

	}

	/**
	 * Deve retornar tipo de amostra NULO.
	 */
	@Test
	public void obterNovaSituacaoAmostraTipoNuloTest() throws BaseException {

		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		sitItemSolicitacoes.setCodigo(DominioSituacaoItemSolicitacaoExame.CA.toString());

		final AelSitItemSolicitacoes sitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		sitItemSolicitacoesOriginal.setCodigo(DominioSituacaoItemSolicitacaoExame.CA.toString());

		final AelItemSolicitacaoExames itemSolicitacaoExameOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginal.setSituacaoItemSolicitacao(sitItemSolicitacoesOriginal);

		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		final DominioSituacaoAmostra atual = systemUnderTest.obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal,
				itemSolicitacaoExame);

		assertNull(atual);

	}

	/**
	 * Se servidor responsável NÃO foi alterado então deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarResponsavelModificadoServidoresIguaisTest() throws BaseException {

		final RapServidores servidor = new RapServidores(new RapServidoresId());

		final RapServidores servidorOriginal = new RapServidores(new RapServidoresId());

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setServidorResponsabilidade(servidor);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setServidorResponsabilidade(servidorOriginal);

		final boolean atual = systemUnderTest.verificarResponsavelModificado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);

		assertFalse(atual);

	}

	/**
	 * Se servidor responsável foi alterado então deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarResponsavelModificadoServidoresDiferentesTest() throws BaseException {

		final RapServidoresId servidorId = new RapServidoresId();
		servidorId.setMatricula(1);

		final RapServidoresId servidorOriginalId = new RapServidoresId();
		servidorOriginalId.setMatricula(2);

		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);

		final RapServidores servidorOriginal = new RapServidores();
		servidorOriginal.setId(servidorOriginalId);

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setServidorResponsabilidade(servidor);

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setServidorResponsabilidade(servidorOriginal);

		final boolean atual = systemUnderTest.verificarResponsavelModificado(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);

		assertTrue(atual);

	}

	/**
	 * Se a descrição do material de análise for igual então deve retornar
	 * falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarDescricaoMaterialAnaliseIgualTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		// itemSolicitacaoExames.setDescMaterialAnalise("teste");

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		// itemSolicitacaoExamesOriginal.setDescMaterialAnalise("teste");

		final boolean atual = systemUnderTest.verificarDescricaoMaterialAnalise(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);

		assertFalse(atual);

	}

	/**
	 * Se a descrição do material de análise foi alterado então deve retornar
	 * verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarDescricaoMaterialAnaliseDiferentesTest() throws BaseException {

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setDescMaterialAnalise("teste1");

		final AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setDescMaterialAnalise("teste2");

		final boolean atual = systemUnderTest.verificarDescricaoMaterialAnalise(itemSolicitacaoExames,
				itemSolicitacaoExamesOriginal);

		assertTrue(atual);

	}

	/**
	 * Se o atendimento e o projetos de pesquisa da solicitação do exame forem
	 * NULOS então deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAtendimentoEProjetoPesquisaNuloFalsoTest() throws BaseException {

		final AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);

		final boolean atual = systemUnderTest.verificarAtendimentoEProjetoPesquisaNulo(itemSolicitacaoExames);

		assertFalse(atual);

	}

	/**
	 * Se o atendimento e o projetos de pesquisa da solicitação do exame forem
	 * NULOS então deve retornar falso. Senão retorna verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarAtendimentoEProjetoPesquisaNuloVerdadeiroTest() throws BaseException {

		final AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setAtendimento(new AghAtendimentos());

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);

		final boolean atual = systemUnderTest.verificarAtendimentoEProjetoPesquisaNulo(itemSolicitacaoExames);

		assertTrue(atual);

	}

	/**
	 * Deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarCalculoProvasExameIguaisIndGeraItemPorColetasFalsoTest() throws BaseException {

		final AelExames exame = new AelExames();
		exame.setSigla("teste");

		final AelExames exameVar = new AelExames();
		exameVar.setSigla("teste");

		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();

		final AelMateriaisAnalises materialAnaliseVar = new AelMateriaisAnalises();

		final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndGeraItemPorColetas(false);
		examesMaterialAnalise.setIndDependente(false);

		final AelUnfExecutaExames unfExecutaExamesVar = new AelUnfExecutaExames();
		unfExecutaExamesVar.setAelExamesMaterialAnalise(examesMaterialAnalise);

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setMaterialAnalise(materialAnalise);
		itemSolicitacaoExames.setExame(exame);

		final AelItemSolicitacaoExames itemSolicitacaoExamesVar = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesVar.setMaterialAnalise(materialAnaliseVar);
		itemSolicitacaoExamesVar.setExame(exameVar);
		itemSolicitacaoExamesVar.setAelUnfExecutaExames(unfExecutaExamesVar);

		final boolean atual = systemUnderTest.verificarCalculoProvas(itemSolicitacaoExames, itemSolicitacaoExamesVar);

		assertTrue(atual);

	}

	/**
	 * Deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarCalculoProvasExameIguaisIndGeraItemPorColetasVerdadeiroTest() throws BaseException {

		final AelExames exame = new AelExames();
		exame.setSigla("teste");

		final AelExames exameVar = new AelExames();
		exameVar.setSigla("teste");

		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();

		final AelMateriaisAnalises materialAnaliseVar = new AelMateriaisAnalises();

		final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndGeraItemPorColetas(true);
		examesMaterialAnalise.setIndDependente(false);

		final AelUnfExecutaExames unfExecutaExamesVar = new AelUnfExecutaExames();
		unfExecutaExamesVar.setAelExamesMaterialAnalise(examesMaterialAnalise);

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setMaterialAnalise(materialAnalise);
		itemSolicitacaoExames.setExame(exame);
		itemSolicitacaoExames.setDescMaterialAnalise("teste");
		itemSolicitacaoExames.setIndGeradoAutomatico(false);

		final AelItemSolicitacaoExames itemSolicitacaoExamesVar = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesVar.setMaterialAnalise(materialAnaliseVar);
		itemSolicitacaoExamesVar.setExame(exameVar);
		itemSolicitacaoExamesVar.setAelUnfExecutaExames(unfExecutaExamesVar);
		itemSolicitacaoExamesVar.setIndGeradoAutomatico(false);
		itemSolicitacaoExamesVar.setDescMaterialAnalise("teste");

		final boolean atual = systemUnderTest.verificarCalculoProvas(itemSolicitacaoExames, itemSolicitacaoExamesVar);

		assertTrue(atual);

	}

	/**
	 * Deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarCalculoProvasExameDiferentesTest() throws BaseException {

		final AelExames exame = new AelExames();
		exame.setSigla("teste1");

		final AelExames exameVar = new AelExames();
		exameVar.setSigla("teste2");

		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();

		final AelMateriaisAnalises materialAnaliseVar = new AelMateriaisAnalises();

		final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndGeraItemPorColetas(true);
		examesMaterialAnalise.setIndDependente(false);

		final AelUnfExecutaExames unfExecutaExamesVar = new AelUnfExecutaExames();
		unfExecutaExamesVar.setAelExamesMaterialAnalise(examesMaterialAnalise);

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setMaterialAnalise(materialAnalise);
		itemSolicitacaoExames.setExame(exame);
		itemSolicitacaoExames.setDescMaterialAnalise("teste");
		itemSolicitacaoExames.setIndGeradoAutomatico(false);

		final AelItemSolicitacaoExames itemSolicitacaoExamesVar = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesVar.setMaterialAnalise(materialAnaliseVar);
		itemSolicitacaoExamesVar.setExame(exameVar);
		itemSolicitacaoExamesVar.setAelUnfExecutaExames(unfExecutaExamesVar);
		itemSolicitacaoExamesVar.setIndGeradoAutomatico(false);
		itemSolicitacaoExamesVar.setDescMaterialAnalise("teste");

		final boolean atual = systemUnderTest.verificarCalculoProvas(itemSolicitacaoExames, itemSolicitacaoExamesVar);

		assertFalse(atual);

	}

	/**
	 * Deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarCalculoProvasExameIguaisIndDependenteVerdadeiroTest() throws BaseException {

		final AelExames exame = new AelExames();
		exame.setSigla("teste");

		final AelExames exameVar = new AelExames();
		exameVar.setSigla("teste");

		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();

		final AelMateriaisAnalises materialAnaliseVar = new AelMateriaisAnalises();

		final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndGeraItemPorColetas(true);
		examesMaterialAnalise.setIndDependente(true);

		final AelUnfExecutaExames unfExecutaExamesVar = new AelUnfExecutaExames();
		unfExecutaExamesVar.setAelExamesMaterialAnalise(examesMaterialAnalise);

		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setMaterialAnalise(materialAnalise);
		itemSolicitacaoExames.setExame(exame);
		itemSolicitacaoExames.setDescMaterialAnalise("teste");
		itemSolicitacaoExames.setIndGeradoAutomatico(false);

		final AelItemSolicitacaoExames itemSolicitacaoExamesVar = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesVar.setMaterialAnalise(materialAnaliseVar);
		itemSolicitacaoExamesVar.setExame(exameVar);
		itemSolicitacaoExamesVar.setAelUnfExecutaExames(unfExecutaExamesVar);
		itemSolicitacaoExamesVar.setIndGeradoAutomatico(false);
		itemSolicitacaoExamesVar.setDescMaterialAnalise("teste");

		final boolean atual = systemUnderTest.verificarCalculoProvas(itemSolicitacaoExames, itemSolicitacaoExamesVar);

		assertFalse(atual);

	}

	/**
	 * Deve retornar verdadeiro.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoExameVerdadeiroTest() throws BaseException {

		final String codigoSituacao = "AC";

		final AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();

		final AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setUnidadeFuncional(unidade);

		final DominioOrigemAtendimento origem = DominioOrigemAtendimento.X;
		final boolean atdDiverso = true;

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		final boolean atual = systemUnderTest.verificarSituacaoExame(codigoSituacao, atendimento, origem, atdDiverso);

		assertTrue(atual);

	}

	/**
	 * Deve retornar falso.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoExameFalsoTest() throws BaseException {

		final String codigoSituacao = "AC";

		final AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();

		final AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setUnidadeFuncional(unidade);

		final DominioOrigemAtendimento origem = DominioOrigemAtendimento.N;
		final boolean atdDiverso = false;

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		final boolean atual = systemUnderTest.verificarSituacaoExame(codigoSituacao, atendimento, origem, atdDiverso);

		assertFalse(atual);

	}

	/**
	 * Deve retornar exceção de código 1094. Pois não tem dados de extrato.
	 * 
	 * @throws BaseException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void buscarDadosRealizacaoExameExtratoItemSolicitacaoNuloTest() throws BaseException {

		final AelExtratoItemSolicitacao extratoItemSolicitacao = null;
		final AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId();
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacoes);

		Mockito.when(mockedAelExtratoItemSolicitacaoDAO.buscarPorItemSolicitacaoSituacao(Mockito.anyInt(),
				Mockito.anyShort(), Mockito.anyString())).thenReturn(extratoItemSolicitacao);

		systemUnderTest.buscarDadosRealizacaoExame(itemSolicitacaoExame, null);

	}

	/**
	 * Testa o lançamento da exceção quando o atendimento corrente não possui
	 * atendimento mãe
	 */
	@Test
	public void buscarAtendimentoMaeTestSemAtendimentoMae() {
		final AghAtendimentos atendimento = new AghAtendimentos();
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();
		variaveis.setAtendimento(atendimento);

		try {
			systemUnderTest.buscarAtendimentoMae(variaveis);
		} catch (final ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameEnforceRNExceptionCode.FAT_00012);
		}
	}

	/**
	 * Testa o lançamento da exceção quando o atendimento mãe não possui dados
	 * da conta hospitalar
	 */
	@Test
	public void buscarAtendimentoMaeTestSemContaInternacaoMae() {
		final AghAtendimentos atendimentoMae = new AghAtendimentos();
		final AghAtendimentos atendimento = new AghAtendimentos();
		atendimentoMae.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setAtendimentoMae(atendimentoMae);
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();
		variaveis.setAtendimento(atendimento);

		final List<FatContasInternacao> contasInternacao = new ArrayList<FatContasInternacao>();
		Mockito.when(mockedFaturamentoFacade.obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyShort()))
				.thenReturn(contasInternacao);

		try {
			systemUnderTest.buscarAtendimentoMae(variaveis);
		} catch (final ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameEnforceRNExceptionCode.FAT_00012);
		}
	}

	/**
	 * Testa o lançamento da exceção quando a conta hospitalar possui convenio
	 * diferente do 75
	 */
	@Test
	public void verificarConvenioESituacaoTestConvenioNao75() {
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();
		variaveis.setConvenioInt(Short.valueOf("70"));
		variaveis.setConv75(Short.valueOf("75"));
		variaveis.setIndSituacao(DominioSituacaoConta.C);

		try {
			systemUnderTest.verificarConvenioESituacao(variaveis);
		} catch (final ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameEnforceRNExceptionCode.FAT_00012);
		}
	}

	/**
	 * Testa o se não é gerada exceção quando a conta hospitalar possui convenio
	 * igual a 75
	 */
	@Test
	public void verificarConvenioESituacaoTestConvenio75() throws BaseException {
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();
		variaveis.setConvenioInt(Short.valueOf("75"));
		variaveis.setConv75(Short.valueOf("75"));
		variaveis.setIndSituacao(DominioSituacaoConta.C);

		systemUnderTest.verificarConvenioESituacao(variaveis);
	}

	/**
	 * Testa se as variáveis são atualizadas de acordo com a pesquisa realizada
	 */
	@Test
	public void buscarDadosContaHospitalarTestComContaInternacao() throws BaseException {
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();

		final FatContasHospitalares contaHospitalar = new FatContasHospitalares();
		contaHospitalar.setSeq(111);
		contaHospitalar.setIndSituacao(DominioSituacaoConta.A);

		final FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setCodigo(Short.valueOf("222"));
		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		final FatConvenioSaudePlanoId id = new FatConvenioSaudePlanoId();
		id.setSeq(Byte.valueOf("33"));
		convenioSaudePlano.setId(id);
		final AinInternacao internacao = new AinInternacao();
		internacao.setConvenioSaude(convenioSaude);
		internacao.setConvenioSaudePlano(convenioSaudePlano);

		final FatContasInternacao contaInternacao = new FatContasInternacao();
		contaInternacao.setContaHospitalar(contaHospitalar);
		contaInternacao.setInternacao(internacao);

		final List<FatContasInternacao> contasInternacao = new ArrayList<FatContasInternacao>();
		contasInternacao.add(contaInternacao);

		Mockito.when(mockedFaturamentoFacade.obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyShort()))
				.thenReturn(contasInternacao);

		systemUnderTest.buscarDadosContaHospitalar(itemSolicitacaoExame, variaveis);

		assertEquals(Integer.valueOf(111), variaveis.getCthSeq());
		assertEquals(DominioSituacaoConta.A, variaveis.getIndSituacao());
		assertEquals(Short.valueOf("222"), variaveis.getConvenioInt());
		assertEquals(Byte.valueOf("33"), variaveis.getPlanoInt());
	}

	/**
	 * Testa se as variáveis são atualizadas (com valores de exames especiais)
	 * de acordo com a pesquisa realizada
	 */
	@Test
	public void buscarDadosContaHospitalarTestSemContaInternacao() throws BaseException {
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();
		final Date dataTeste = new Date();

		final List<FatContasInternacao> contasInternacao = new ArrayList<FatContasInternacao>();

		Mockito.when(mockedFaturamentoFacade.obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyShort()))
				.thenReturn(contasInternacao);

		final FatContasHospitalares contaHospitalar = new FatContasHospitalares();
		contaHospitalar.setSeq(111);
		contaHospitalar.setIndSituacao(DominioSituacaoConta.A);
		contaHospitalar.setDtAltaAdministrativa(dataTeste);

		final FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setCodigo(Short.valueOf("222"));
		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		final FatConvenioSaudePlanoId id = new FatConvenioSaudePlanoId();
		id.setSeq(Byte.valueOf("33"));
		convenioSaudePlano.setId(id);
		final AinInternacao internacao = new AinInternacao();
		internacao.setConvenioSaude(convenioSaude);
		internacao.setConvenioSaudePlano(convenioSaudePlano);

		final FatContasInternacao contaInternacao = new FatContasInternacao();
		contaInternacao.setContaHospitalar(contaHospitalar);
		contaInternacao.setInternacao(internacao);

		final List<FatContasInternacao> contasInternacao1 = new ArrayList<FatContasInternacao>();
		contasInternacao1.add(contaInternacao);

		Mockito.when(
				mockedFaturamentoFacade.obterContasHospitalaresPorInternacaoPacienteDataSolicitacaoComExamesEspeciais(
						Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyShort()))
				.thenReturn(contasInternacao1);

		systemUnderTest.buscarDadosContaHospitalar(itemSolicitacaoExame, variaveis);

		assertEquals(Integer.valueOf(111), variaveis.getCthSeq());
		assertEquals(DominioSituacaoConta.A, variaveis.getIndSituacao());
		assertEquals(Short.valueOf("222"), variaveis.getConvenioInt());
		assertEquals(Byte.valueOf("33"), variaveis.getPlanoInt());
		assertEquals(dataTeste, variaveis.getDthrSolic());
	}

	/**
	 * Testa se as variáveis são atualizadas (com valores do exame) de acordo
	 * com os dados do atendimento mãe
	 */
	@Test
	public void buscarDadosAtendimentoMaeTest() throws BaseException {
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();
		variaveis.setOrigem(DominioOrigemAtendimento.N);

		final AghAtendimentos atendimentoMae = new AghAtendimentos();
		final AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(111);
		atendimentoMae.setPaciente(paciente);
		final AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setSeq(Short.valueOf("222"));
		atendimentoMae.setEspecialidade(especialidade);
		atendimentoMae.setOrigem(DominioOrigemAtendimento.A);
		final AinInternacao internacao = new AinInternacao();
		internacao.setSeq(333);
		atendimentoMae.setInternacao(internacao);

		final AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setAtendimentoMae(atendimentoMae);
		variaveis.setAtendimento(atendimento);

		systemUnderTest.buscarDadosAtendimentoMae(variaveis);

		assertEquals(Integer.valueOf(111), variaveis.getPacCodigo());
		assertEquals(Short.valueOf("222"), variaveis.getEspSeq());
		assertEquals(Integer.valueOf(333), variaveis.getIntSeq());
	}

	/**
	 * Testa se as variáveis são atualizadas (com valores do exame) de acordo
	 * com os dados do atendimento
	 */
	// @Test
	public void buscarDadosAtendimentoGerouExameTest() throws BaseException {
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();

		final Date dataCriacao = new Date();

		final AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(555);
		final AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(111);
		atendimento.setPaciente(paciente);
		final AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setSeq(Short.valueOf("222"));
		atendimento.setEspecialidade(especialidade);
		atendimento.setOrigem(DominioOrigemAtendimento.H);
		final AinInternacao internacao = new AinInternacao();
		internacao.setSeq(333);
		atendimento.setInternacao(internacao);

		final AghAtendimentos atendimentoMae = new AghAtendimentos();
		atendimentoMae.setSeq(444);
		atendimento.setAtendimentoMae(atendimentoMae);

		final AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		solicitacao.setCriadoEm(dataCriacao);
		solicitacao.setAtendimento(atendimento);

		final FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setCodigo(Short.valueOf("666"));
		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		final FatConvenioSaudePlanoId id = new FatConvenioSaudePlanoId();
		id.setSeq(Byte.valueOf("33"));
		convenioSaudePlano.setId(id);
		convenioSaudePlano.setConvenioSaude(convenioSaude);

		final Object[] dado = new Object[2];
		dado[0] = convenioSaudePlano;
		dado[1] = solicitacao;

		final List<Object[]> dados = new ArrayList<Object[]>();
		dados.add(dado);

		Mockito.when(mockedAelItemSolicitacaoExameDAO.pesquisarDadosAtendimentoGerouExame(Mockito.anyInt()))
				.thenReturn(dados);

		systemUnderTest.buscarDadosAtendimentoGerouExame(itemSolicitacaoExame, variaveis);

		assertEquals(Integer.valueOf(555), variaveis.getAtdSeq());
		assertEquals(Integer.valueOf(111), variaveis.getPacCodigo());
		assertEquals(Short.valueOf("222"), variaveis.getEspSeq());
		assertEquals(Short.valueOf("666"), variaveis.getCspCnvCodigo());
		assertEquals(Byte.valueOf("33"), variaveis.getCspSeq());
		assertEquals(Integer.valueOf(333), variaveis.getIntSeq());
		assertEquals(dataCriacao, variaveis.getDthrSolicitacao());
		assertEquals(DominioTipoPlano.A, variaveis.getTipoAtd());
		assertEquals(DominioOrigemAtendimento.H, variaveis.getOrigem());
		assertEquals(Integer.valueOf(444), variaveis.getAtdSeqMae());
	}

	/**
	 * Testa se as variáveis são atualizadas (com null), devido a ausência de
	 * dados do atendimento
	 */
	@Test
	public void buscarDadosAtendimentoGerouExameTestSemDados() throws BaseException {
		final VariaveisInterfaceFaturamento variaveis = systemUnderTest.new VariaveisInterfaceFaturamento();

		final List<Object[]> dados = new ArrayList<Object[]>();

		Mockito.when(mockedAelItemSolicitacaoExameDAO.pesquisarDadosAtendimentoGerouExame(Mockito.anyInt()))
				.thenReturn(dados);

		systemUnderTest.buscarDadosAtendimentoGerouExame(itemSolicitacaoExame, variaveis);

		assertEquals(null, variaveis.getAtdSeq());
		assertEquals(null, variaveis.getPacCodigo());
		assertEquals(null, variaveis.getCspCnvCodigo());
		assertEquals(null, variaveis.getCspSeq());
		assertEquals(null, variaveis.getOrigem());
	}

	@Test
	public void testCalcularProgramacaoParamTipoColetaValorNullUnidFuncNull() {

		final DominioProgramacaoExecExames programacao = systemUnderTest.calcularProgramacao(null,
				new AelSolicitacaoExames());

		Assert.assertTrue(programacao.equals(DominioProgramacaoExecExames.R));
	}

	@Test
	public void testCalcularProgramacaoParamTipoColetaValorUUnidFuncNull() {

		final DominioProgramacaoExecExames programacao = systemUnderTest.calcularProgramacao(DominioTipoColeta.U,
				new AelSolicitacaoExames());

		Assert.assertTrue(programacao.equals(DominioProgramacaoExecExames.U));
	}

	@Test
	public void testCalcularProgramacaoParamTipoColetaValorNUnidFuncNull() {

		final DominioProgramacaoExecExames programacao = systemUnderTest.calcularProgramacao(DominioTipoColeta.N,
				new AelSolicitacaoExames());

		Assert.assertTrue(programacao.equals(DominioProgramacaoExecExames.R));
	}

	@Test
	public void testCalcularProgramacaoParamTipoColetaValorNUnidFuncComAutomacaoRotina() {

		Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.TRUE);

		final AelSolicitacaoExames solEx = new AelSolicitacaoExames();
		solEx.setUnidadeFuncional(new AghUnidadesFuncionais());

		final DominioProgramacaoExecExames programacao = systemUnderTest.calcularProgramacao(DominioTipoColeta.N,
				solEx);

		Assert.assertTrue(programacao.equals(DominioProgramacaoExecExames.R));
	}

	@Test
	public void testCalcularProgramacaoParamTipoColetaValorUUnidFuncComAutomacaoRotina() {

		Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.TRUE);

		final AelSolicitacaoExames solEx = new AelSolicitacaoExames();
		solEx.setUnidadeFuncional(new AghUnidadesFuncionais());

		final DominioProgramacaoExecExames programacao = systemUnderTest.calcularProgramacao(DominioTipoColeta.U,
				solEx);

		Assert.assertTrue(programacao.equals(DominioProgramacaoExecExames.U));
	}

	@Test
	public void testCalcularProgramacaoParamTipoColetaValorNUnidFuncSemAutomacaoRotina() {

		Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.FALSE);

		final AelSolicitacaoExames solEx = new AelSolicitacaoExames();
		solEx.setUnidadeFuncional(new AghUnidadesFuncionais());

		final DominioProgramacaoExecExames programacao = systemUnderTest.calcularProgramacao(DominioTipoColeta.N,
				solEx);

		Assert.assertTrue(programacao.equals(DominioProgramacaoExecExames.U));
	}

	@Test
	public void testCalcularProgramacaoParamTipoColetaValorUUnidFuncSemAutomacaoRotina() {

		Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Mockito.anyShort(),
				Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.FALSE);

		final AelSolicitacaoExames solEx = new AelSolicitacaoExames();
		solEx.setUnidadeFuncional(new AghUnidadesFuncionais());

		final DominioProgramacaoExecExames programacao = systemUnderTest.calcularProgramacao(DominioTipoColeta.U,
				solEx);

		Assert.assertTrue(programacao.equals(DominioProgramacaoExecExames.U));
	}

	@Test
	public void testCriarSeqCodBarraRedomeParamNull_Null() {
		// mock para imuno = 171;

		try {
			systemUnderTest.criarSeqCodBarraRedome(null, null);
		} catch (final BaseException e) {
			Assert.fail();
		}
	}

	@Test
	public void testCriarSeqCodBarraRedomeParamItemSolicitacaoExameSemUnidadeFunc_Null() {
		// mock para imuno = 171;

		try {
			systemUnderTest.criarSeqCodBarraRedome(new AelItemSolicitacaoExames(), null);
		} catch (final BaseException e) {
			Assert.fail();
		}
	}

	@Test
	public void testCriarSeqCodBarraRedomeParamItemSolicitacaoExameComUnidadeFunc_Null() {
		// mock para imuno = 171;

		final AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		item.setUnidadeFuncional(new AghUnidadesFuncionais());

		try {
			systemUnderTest.criarSeqCodBarraRedome(item, null);
		} catch (final BaseException e) {
			Assert.fail();
		}
	}

	@Test
	public void testCriarSeqCodBarraRedomeParamItemSolicitacaoExameComUnidadeFunc_Amostra() {
		// mock para imuno = 171;

		final AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		item.setUnidadeFuncional(new AghUnidadesFuncionais());

		try {
			systemUnderTest.criarSeqCodBarraRedome(item, new AelAmostras());
		} catch (final BaseException e) {
			Assert.fail();
		} catch (final Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testCriarSeqCodBarraRedomeParamItemSolicitacaoExameComUnidadeFunc_Amostra_UnfExecutoraIgualImuno_NAOExistePesquisaExame() {
		// mock para imuno = 171;

		final AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		final AghUnidadesFuncionais unfExecutora = new AghUnidadesFuncionais();
		item.setUnidadeFuncional(unfExecutora);

		// TODO setar unfExecutora.setSeq(Short.valueOf("171"));
		// TODO mock dos metodos de busca.
		/*
		 * AelAgrpPesquisas agrpPesquisa =
		 * getAelAgrpPesquisasDAO().obterAelAgrpPesquisasPorDescricao
		 * (AelAgrpPesquisas.REDOME);
		 * 
		 * List<AelAgrpPesquisaXExame> lista =
		 * getAelAgrpPesquisaXExameDAO().buscarAelAgrpPesquisaXExame
		 * (itemSolicitacaoExame.getExame() ,
		 * itemSolicitacaoExame.getMaterialAnalise(),
		 * itemSolicitacaoExame.getUnidadeFuncional(), agrpPesquisa);
		 */

		// Neste teste o metodo buscarAelAgrpPesquisaXExame deve retornar uma
		// lista vazia.

		try {
			systemUnderTest.criarSeqCodBarraRedome(item, new AelAmostras());
		} catch (final BaseException e) {
			Assert.fail();
		} catch (final Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testCriarSeqCodBarraRedomeParamItemSolicitacaoExameComUnidadeFunc_Amostra_UnfExecutoraIgualImuno_ExistePesquisaExame() {
		// mock para imuno = 171;

		final AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		final AghUnidadesFuncionais unfExecutora = new AghUnidadesFuncionais();
		item.setUnidadeFuncional(unfExecutora);

		// TODO setar unfExecutora.setSeq(Short.valueOf("171"));
		// TODO mock dos metodos de busca.
		/*
		 * AelAgrpPesquisas agrpPesquisa =
		 * getAelAgrpPesquisasDAO().obterAelAgrpPesquisasPorDescricao
		 * (AelAgrpPesquisas.REDOME);
		 * 
		 * List<AelAgrpPesquisaXExame> lista =
		 * getAelAgrpPesquisaXExameDAO().buscarAelAgrpPesquisaXExame
		 * (itemSolicitacaoExame.getExame() ,
		 * itemSolicitacaoExame.getMaterialAnalise(),
		 * itemSolicitacaoExame.getUnidadeFuncional(), agrpPesquisa);
		 */

		// Neste teste o metodo buscarAelAgrpPesquisaXExame deve retornar
		// valores.

		try {
			systemUnderTest.criarSeqCodBarraRedome(item, new AelAmostras());
		} catch (final BaseException e) {
			Assert.fail();
		} catch (final Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testAdicionaTempoColetaParametrosNulos() {
		final Date date = systemUnderTest.adicionaTempoColeta(null, null, null);

		Assert.assertTrue(date == null);
	}

	@Test
	public void testAdicionaTempoColetaParametroDataNulo() {
		final Date date = systemUnderTest.adicionaTempoColeta(null, DominioUnidadeMedidaTempo.D, 2d);

		Assert.assertTrue(date == null);
	}

	@Test
	public void testAdicionaTempoColetaParametroMedidaTempoNulo() {
		final Date d = new Date();

		final Date date = systemUnderTest.adicionaTempoColeta(d, null, 2d);

		Assert.assertTrue(d.equals(date));
	}

	@Test
	public void testAdicionaTempoColetaParametroTempoNulo() {
		final Date d = new Date();

		final Date date = systemUnderTest.adicionaTempoColeta(d, DominioUnidadeMedidaTempo.D, null);

		Assert.assertTrue(d.equals(date));
	}

	@Test
	public void testAdicionaTempoColetaParametroOkDataAtualDiaDois() {
		final Date date = systemUnderTest.adicionaTempoColeta(new Date(), DominioUnidadeMedidaTempo.D, 2d);

		// TODO fazer assert para mais dois dias.
		Assert.assertTrue(date != null);
	}

	@Test
	public void testAdicionaTempoColetaParametroOkDataAtualHoraDois() {
		final Date date = systemUnderTest.adicionaTempoColeta(new Date(), DominioUnidadeMedidaTempo.H, 2d);

		// TODO fazer assert para mais duas horas.
		Assert.assertTrue(date != null);
	}

	@Test
	public void testAdicionaTempoColetaParametroOkDataAtualMinutosDois() {
		final Date date = systemUnderTest.adicionaTempoColeta(new Date(), DominioUnidadeMedidaTempo.M, 2d);

		// TODO fazer assert para mais dois minutos.
		Assert.assertTrue(date != null);
	}

	@Test(expected = NullPointerException.class)
	public void testGetSituacaoAmostraParametroNulo() {

		systemUnderTest.getSituacaoAmostra(null);

	}

	@Test
	public void testGetSituacaoAmostraParametroOkSituacaoNula() {
		final DominioSituacaoAmostra dsa = systemUnderTest.getSituacaoAmostra(new AelItemSolicitacaoExames());

		Assert.assertTrue(dsa == null);
	}

	@Test
	public void testGetSituacaoAmostraParametroOkSituacaoValida_AreaExecutora() {
		final AelItemSolicitacaoExames situacao = new AelItemSolicitacaoExames();
		situacao.setSituacaoItemSolicitacao(new AelSitItemSolicitacoes("AE", "desc qualquer", DominioSituacao.A));

		final DominioSituacaoAmostra dsa = systemUnderTest.getSituacaoAmostra(situacao);

		// AE //AREA EXECUTORA returnValue = DominioSituacaoAmostra.R;
		// CS //COLETADO PELO SOLICITANTE returnValue =
		// DominioSituacaoAmostra.C;
		// AG //AGENDADO returnValue = DominioSituacaoAmostra.G;
		// AC //A COLETAR returnValue = DominioSituacaoAmostra.G;
		// } else { returnValue = DominioSituacaoAmostra.G;

		Assert.assertTrue(dsa != null && DominioSituacaoAmostra.R == dsa);
	}

	@Test
	public void testGetSituacaoAmostraParametroOkSituacaoValida_ColetadoPeloSolicitante() {
		final AelItemSolicitacaoExames situacao = new AelItemSolicitacaoExames();
		situacao.setSituacaoItemSolicitacao(new AelSitItemSolicitacoes("CS", "desc qualquer", DominioSituacao.A));

		final DominioSituacaoAmostra dsa = systemUnderTest.getSituacaoAmostra(situacao);

		// AE //AREA EXECUTORA returnValue = DominioSituacaoAmostra.R;
		// CS //COLETADO PELO SOLICITANTE returnValue =
		// DominioSituacaoAmostra.C;
		// AG //AGENDADO returnValue = DominioSituacaoAmostra.G;
		// AC //A COLETAR returnValue = DominioSituacaoAmostra.G;
		// } else { returnValue = DominioSituacaoAmostra.G;

		Assert.assertTrue(dsa != null && DominioSituacaoAmostra.C == dsa);
	}

	@Test
	public void testGetSituacaoAmostraParametroOkSituacaoValida_Agendado() {
		final AelItemSolicitacaoExames situacao = new AelItemSolicitacaoExames();
		situacao.setSituacaoItemSolicitacao(new AelSitItemSolicitacoes("AG", "desc qualquer", DominioSituacao.A));

		final DominioSituacaoAmostra dsa = systemUnderTest.getSituacaoAmostra(situacao);

		// AE //AREA EXECUTORA returnValue = DominioSituacaoAmostra.R;
		// CS //COLETADO PELO SOLICITANTE returnValue =
		// DominioSituacaoAmostra.C;
		// AG //AGENDADO returnValue = DominioSituacaoAmostra.G;
		// AC //A COLETAR returnValue = DominioSituacaoAmostra.G;
		// } else { returnValue = DominioSituacaoAmostra.G;

		Assert.assertTrue(dsa != null && DominioSituacaoAmostra.G == dsa);
	}

	@Test
	public void testGetSituacaoAmostraParametroOkSituacaoValida_Acoletar() {
		final AelItemSolicitacaoExames situacao = new AelItemSolicitacaoExames();
		situacao.setSituacaoItemSolicitacao(new AelSitItemSolicitacoes("AC", "desc qualquer", DominioSituacao.A));

		final DominioSituacaoAmostra dsa = systemUnderTest.getSituacaoAmostra(situacao);

		// AE //AREA EXECUTORA returnValue = DominioSituacaoAmostra.R;
		// CS //COLETADO PELO SOLICITANTE returnValue =
		// DominioSituacaoAmostra.C;
		// AG //AGENDADO returnValue = DominioSituacaoAmostra.G;
		// AC //A COLETAR returnValue = DominioSituacaoAmostra.G;
		// } else { returnValue = DominioSituacaoAmostra.G;

		Assert.assertTrue(dsa != null && DominioSituacaoAmostra.G == dsa);
	}

	@Test
	public void testGetSituacaoAmostraParametroOkSituacaoValida_OutroValor() {
		final AelItemSolicitacaoExames situacao = new AelItemSolicitacaoExames();
		situacao.setSituacaoItemSolicitacao(new AelSitItemSolicitacoes("AJ", "desc qualquer", DominioSituacao.A));

		final DominioSituacaoAmostra dsa = systemUnderTest.getSituacaoAmostra(situacao);

		// AE //AREA EXECUTORA returnValue = DominioSituacaoAmostra.R;
		// CS //COLETADO PELO SOLICITANTE returnValue =
		// DominioSituacaoAmostra.C;
		// AG //AGENDADO returnValue = DominioSituacaoAmostra.G;
		// AC //A COLETAR returnValue = DominioSituacaoAmostra.G;
		// } else { returnValue = DominioSituacaoAmostra.G;

		Assert.assertTrue(dsa != null && DominioSituacaoAmostra.G == dsa);
	}

	@Test
	public void testCalcularIntervaloHorasParametroNulo() {
		final Double d = systemUnderTest.calcularIntervaloHoras(null);

		Assert.assertTrue(d == null);
	}

	@Test
	public void testCalcularIntervaloHorasParametroValido() {
		final Double d = systemUnderTest.calcularIntervaloHoras(new Date());

		Assert.assertTrue(d != null);
	}

	@Test
	public void testCalcularIntervaloHorasParametroValidoHora1330() {
		final Calendar cal = Calendar.getInstance();
		cal.set(2011, 4, 29, 13, 30, 0);

		final Double d = systemUnderTest.calcularIntervaloHoras(cal.getTime());

		Assert.assertTrue(d == 13.5);
	}

	@Test
	public void testCalcularIntervaloHorasParametroValidoHora1345() {
		final Calendar cal = Calendar.getInstance();
		cal.set(2011, 4, 29, 13, 45, 0);

		final Double d = systemUnderTest.calcularIntervaloHoras(cal.getTime());

		Assert.assertTrue(d > 13.5 && d < 14); // 13.75
	}

	@Test
	public void testCalcularIntervaloHorasParametroValidoHora2215() {
		final Calendar cal = Calendar.getInstance();
		cal.set(2011, 4, 29, 22, 15, 0);

		final Double d = systemUnderTest.calcularIntervaloHoras(cal.getTime());

		Assert.assertTrue(d == 22.25);
	}

	@Test
	public void testCalcularRangeSolicitacaoParametroNulo() {
		try {
			final List<Date> lista = systemUnderTest.calcularRangeSolicitacao(null);

			Assert.assertTrue(lista != null && lista.isEmpty());
		} catch (final ApplicationBusinessException e) {
			Assert.fail();
		}
	}

	@Test
	public void testCalcularRangeSolicitacaoParametroOkDataProgramadaNula() {
		try {
			final List<Date> lista = systemUnderTest.calcularRangeSolicitacao(new AelItemSolicitacaoExames());

			Assert.assertTrue(lista != null && lista.isEmpty());
		} catch (final ApplicationBusinessException e) {
			Assert.fail();
		}
	}

	@Test
	public void testCalcularRangeSolicitacaoParametroOkDataProgramadaValida() {
		try {
			final Date dataProgramada = new Date();
			final AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
			item.setDthrProgramada(dataProgramada);
			final List<Date> lista = systemUnderTest.calcularRangeSolicitacao(item);

			Assert.assertTrue(lista != null && !lista.isEmpty());
			Assert.assertTrue(lista.size() == 2);

			final Date inicio = DateUtil.adicionaMinutos(dataProgramada, (60 * -1));
			final Date fim = DateUtil.adicionaMinutos(dataProgramada, 60);

			Assert.assertTrue(lista.get(0).equals(inicio) && lista.get(1).equals(fim));
		} catch (final ApplicationBusinessException e) {
			Assert.fail();
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