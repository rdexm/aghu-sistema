package br.gov.mec.aghu.exames.solicitacao.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.SessionContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioIndImpressoLaudo;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoDia;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExameConselhoProfsDAO;
import br.gov.mec.aghu.exames.dao.AelExamesEspecialidadeDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.exames.dao.AelServidoresExameUnidDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN.ItemSolicitacaoExameRNExceptionCode;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesEspecialidade;
import br.gov.mec.aghu.model.AelExamesEspecialidadeId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelServidoresExameUnid;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author Filipe Hoffmeister
 *
 */
public class ItemSolicitacaoExameRNTest extends AGHUBaseUnitTest<ItemSolicitacaoExameRN> {

	private AelItemSolicitacaoExames itemSolicitacaoExames;
	@Mock
	private AelExamesEspecialidadeDAO aelExamesEspecialidadeDAO;
	@Mock
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;
	@Mock
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;
	@Mock
	private IAghuFacade aghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AelServidoresExameUnidDAO aelServidoresExameUnidDAO;
	@Mock
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	@Mock
	private AelMatrizSituacaoDAO aelMatrizSituacaoDAO;
	@Mock
	private AelUnfExecutaExamesDAO mockedAelUnfExecutaExamesDAO;
	@Mock
	private IExamesFacade examesFacade;
	@Mock
	private ICascaFacade cascaFacade;
	@Mock
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	@Mock
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;
	@Mock
	private AelExameConselhoProfsDAO mockedAelExameConselhoProfsDAO;
	@Mock
	private SessionContext mockedSessionContext;

	@Before
	public void before() {
		itemSolicitacaoExames = new AelItemSolicitacaoExames();

		AelUnfExecutaExames unf = new AelUnfExecutaExames();
		unf.setIndExigeInfoClin(false);

		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class),
				Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class))).thenReturn(unf);

		Mockito.when(cascaFacade.temPermissao(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(true);

		try {
			Mockito.when(mockedSessionContext.getCallerPrincipal()).thenReturn(new Principal() {
				@Override
				public String getName() {
					return "teste";
				}
			});

			whenObterServidorLogado();
		} catch (Exception e) {
			fail();
		}
	}

	// RN06
	@Test
	public void testVerificarExigeMaterialAnaliseSuccess() {
		AelMateriaisAnalises aelMateriaisAnalises = new AelMateriaisAnalises();
		aelMateriaisAnalises.setIndExigeDescMatAnls(Boolean.TRUE);
		itemSolicitacaoExames.setMaterialAnalise(aelMateriaisAnalises);
		itemSolicitacaoExames.setDescMaterialAnalise("TEM DESCRIÇÃO");

		try {
			systemUnderTest.verificarExigeMaterialAnalise(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}

		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	public void testVerificarExigeMaterialAnaliseSuccess02() {
		try {
			systemUnderTest.verificarExigeMaterialAnalise(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	public void testVerificarExigeMaterialAnaliseError() {
		AelMateriaisAnalises aelMateriaisAnalises = new AelMateriaisAnalises();
		aelMateriaisAnalises.setIndExigeDescMatAnls(Boolean.TRUE);
		itemSolicitacaoExames.setMaterialAnalise(aelMateriaisAnalises);

		try {
			systemUnderTest.verificarExigeMaterialAnalise(itemSolicitacaoExames);
			fail("testVerificarExigeMaterialAnaliseError: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00499);
		}
	}

	@Test
	public void testVerificarExigeMaterialAnaliseError02() {
		itemSolicitacaoExames.setDescMaterialAnalise("TEM DESCRIÇÃO E NAO PRECISA, ENTAO DA ERRO");

		try {
			systemUnderTest.verificarExigeMaterialAnalise(itemSolicitacaoExames);
			fail("testVerificarExigeMaterialAnaliseError02: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00500);
		}
	}

	// RN07
	@Test
	public void testVerificarInformacaoClinicaSuccess() {
		try {
			AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
			solicitacaoExame.setInformacoesClinicas("INF CLINICAS");
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(Integer.valueOf(1));
			solicitacaoExame.setAtendimento(atendimento);
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

			systemUnderTest.verificarInformacaoClinica(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	public void testVerificarInformacaoClinicaError() {
		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		solicitacaoExame.setInformacoesClinicas(null);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(Integer.valueOf(1));
		solicitacaoExame.setAtendimento(atendimento);
		itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

		try {
			systemUnderTest.verificarInformacaoClinica(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00389);
		}
	}

	@Test
	public void testVerificarInformacaoClinicaError02() {
		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		solicitacaoExame.setInformacoesClinicas(null);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(null);
		solicitacaoExame.setAtendimento(atendimento);
		itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

		try {
			systemUnderTest.verificarInformacaoClinica(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	// RN09
	@Test
	/**
	 * Testa o sucesso quando necessita de especialidade do exame igual á do
	 * profissional
	 */
	public void testVerificarEspecialidadeExameSuccess() {
		try {
			AelSolicitacaoExames aelSolicitacaoExames = new AelSolicitacaoExames();
			aelSolicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(aelSolicitacaoExames);

			itemSolicitacaoExames.setExame(new AelExames());
			itemSolicitacaoExames.setMaterialAnalise(new AelMateriaisAnalises());
			itemSolicitacaoExames.setUnidadeFuncional(new AghUnidadesFuncionais());

			List<AelExamesEspecialidade> especialidades = new ArrayList<AelExamesEspecialidade>();
			AelExamesEspecialidade aelExamesEspecialidade = new AelExamesEspecialidade();
			AelExamesEspecialidadeId aelExamesEspecialidadeId = new AelExamesEspecialidadeId();
			aelExamesEspecialidadeId.setEspSeq(Short.valueOf("1"));
			aelExamesEspecialidade.setId(aelExamesEspecialidadeId);
			especialidades.add(aelExamesEspecialidade);
			Mockito.when(aelExamesEspecialidadeDAO.buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
					Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(especialidades);

			List<AghProfEspecialidades> profEspecialidades = new ArrayList<AghProfEspecialidades>();
			AghProfEspecialidades aghProfEspecialidades = new AghProfEspecialidades();
			AghProfEspecialidadesId aghProfEspecialidadesId = new AghProfEspecialidadesId();
			aghProfEspecialidadesId.setEspSeq(Short.valueOf("1"));
			aghProfEspecialidades.setId(aghProfEspecialidadesId);
			profEspecialidades.add(aghProfEspecialidades);
			Mockito.when(aghuFacade.listarEspecialidadesPorServidor(Mockito.any(RapServidores.class)))
					.thenReturn(profEspecialidades);

			systemUnderTest.verificarEspecialidadeExame(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa a não entrada no if que verifica se o usuário é diferente de nulo
	 */
	public void testVerificarEspecialidadeExameSuccess02() {
		try {
			AelSolicitacaoExames aelSolicitacaoExames = new AelSolicitacaoExames();
			aelSolicitacaoExames.setServidorResponsabilidade(null);
			itemSolicitacaoExames.setSolicitacaoExame(aelSolicitacaoExames);

			List<AelExamesEspecialidade> especialidades = new ArrayList<AelExamesEspecialidade>();
			AelExamesEspecialidade aelExamesEspecialidade = new AelExamesEspecialidade();
			AelExamesEspecialidadeId aelExamesEspecialidadeId = new AelExamesEspecialidadeId();
			aelExamesEspecialidadeId.setEspSeq(Short.valueOf("1"));
			aelExamesEspecialidade.setId(aelExamesEspecialidadeId);
			especialidades.add(aelExamesEspecialidade);
			Mockito.when(aelExamesEspecialidadeDAO.buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
					Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(especialidades);

			List<AghProfEspecialidades> profEspecialidades = new ArrayList<AghProfEspecialidades>();
			AghProfEspecialidades aghProfEspecialidades = new AghProfEspecialidades();
			AghProfEspecialidadesId aghProfEspecialidadesId = new AghProfEspecialidadesId();
			aghProfEspecialidadesId.setEspSeq(Short.valueOf("2"));
			aghProfEspecialidades.setId(aghProfEspecialidadesId);
			profEspecialidades.add(aghProfEspecialidades);
			Mockito.when(aghuFacade.listarEspecialidadesPorServidor(Mockito.any(RapServidores.class)))
					.thenReturn(profEspecialidades);

			systemUnderTest.verificarEspecialidadeExame(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa a não entrada no if que verifica se o convenio é sus
	 */
	public void testVerificarEspecialidadeExameSuccess03() {
		try {
			AelSolicitacaoExames aelSolicitacaoExames = new AelSolicitacaoExames();
			aelSolicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(aelSolicitacaoExames);

			List<AelExamesEspecialidade> especialidades = new ArrayList<AelExamesEspecialidade>();
			AelExamesEspecialidade aelExamesEspecialidade = new AelExamesEspecialidade();
			AelExamesEspecialidadeId aelExamesEspecialidadeId = new AelExamesEspecialidadeId();
			aelExamesEspecialidadeId.setEspSeq(Short.valueOf("1"));
			aelExamesEspecialidade.setId(aelExamesEspecialidadeId);
			especialidades.add(aelExamesEspecialidade);
			Mockito.when(aelExamesEspecialidadeDAO.buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
					Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(especialidades);

			List<AghProfEspecialidades> profEspecialidades = new ArrayList<AghProfEspecialidades>();
			AghProfEspecialidades aghProfEspecialidades = new AghProfEspecialidades();
			AghProfEspecialidadesId aghProfEspecialidadesId = new AghProfEspecialidadesId();
			aghProfEspecialidadesId.setEspSeq(Short.valueOf("2"));
			aghProfEspecialidades.setId(aghProfEspecialidadesId);
			profEspecialidades.add(aghProfEspecialidades);
			Mockito.when(aghuFacade.listarEspecialidadesPorServidor(Mockito.any(RapServidores.class)))
					.thenReturn(profEspecialidades);

			// Usando o underTest que tem o convenio diferente de sus
			systemUnderTest.verificarEspecialidadeExame(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa a o erro quando necessita de especialidade do exame igual á do
	 * profissional e é diferente
	 */
	public void testVerificarEspecialidadeExameError() {
		try {
			AelSolicitacaoExames aelSolicitacaoExames = new AelSolicitacaoExames();
			aelSolicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(aelSolicitacaoExames);

			itemSolicitacaoExames.setExame(new AelExames());
			itemSolicitacaoExames.setMaterialAnalise(new AelMateriaisAnalises());
			itemSolicitacaoExames.setUnidadeFuncional(new AghUnidadesFuncionais());

			List<AelExamesEspecialidade> especialidades = new ArrayList<AelExamesEspecialidade>();
			AelExamesEspecialidade aelExamesEspecialidade = new AelExamesEspecialidade();
			AelExamesEspecialidadeId aelExamesEspecialidadeId = new AelExamesEspecialidadeId();
			aelExamesEspecialidadeId.setEspSeq(Short.valueOf("1"));
			aelExamesEspecialidade.setId(aelExamesEspecialidadeId);
			especialidades.add(aelExamesEspecialidade);
			Mockito.when(aelExamesEspecialidadeDAO.buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
					Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(especialidades);

			List<AghProfEspecialidades> profEspecialidades = new ArrayList<AghProfEspecialidades>();
			AghProfEspecialidades aghProfEspecialidades = new AghProfEspecialidades();
			AghProfEspecialidadesId aghProfEspecialidadesId = new AghProfEspecialidadesId();
			aghProfEspecialidadesId.setEspSeq(Short.valueOf("2"));
			aghProfEspecialidades.setId(aghProfEspecialidadesId);
			profEspecialidades.add(aghProfEspecialidades);
			Mockito.when(aghuFacade.listarEspecialidadesPorServidor(Mockito.any(RapServidores.class)))
					.thenReturn(profEspecialidades);

			systemUnderTest.verificarEspecialidadeExame(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
		}
	}

	@Test
	/**
	 * Testa a não entrada no if que verifica quando a situação é ativa
	 */
	public void testVerificarSituacaoAtivaSuccess() {
		try {
			AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
			situacaoItemSolicitacao.setIndSituacao(DominioSituacao.A);
			itemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

			systemUnderTest.verificarSituacaoAtiva(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa a o erro quando a situação é inativa
	 */
	public void testVerificarSituacaoAtivaError() {
		try {
			AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
			situacaoItemSolicitacao.setIndSituacao(DominioSituacao.I);
			itemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

			systemUnderTest.verificarSituacaoAtiva(itemSolicitacaoExames);
			fail("testVerificarSituacaoAtivaError: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00420);
		}
	}

	/**
	 * Testa a excecao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testVerificarDataImpSumario_LancaExcecao() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		itemSolicitacaoExameMocked.setDataImpSumario(new Date());

		systemUnderTest.verificarDataImpSumario(itemSolicitacaoExameMocked);
	}

	/**
	 * Testa a data data_imp_sumario nula
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarDataImpSumario_DataImpSumario_Null() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		systemUnderTest.verificarDataImpSumario(itemSolicitacaoExameMocked);
		assertNull(itemSolicitacaoExameMocked.getDataImpSumario());
	}

	/**
	 * Testa a excecao
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarServidorExameUnidadeConvenioSUS_LancaExcecao_AEL_00490()
			throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		solicitacaoExame.setServidorResponsabilidade(new RapServidores());
		itemSolicitacaoExameMocked.setSolicitacaoExame(solicitacaoExame);
		itemSolicitacaoExameMocked.setExame(new AelExames());

		Mockito.when(aelServidoresExameUnidDAO.buscaListaAelServidoresExameUnid(Mockito.any(AelExames.class),
				Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class))).thenReturn(null);

		systemUnderTest.verificarServidorExameUnidadeConvenioSUS(itemSolicitacaoExameMocked);
	}

	/**
	 * Testa fluxo sem lancar ApplicationBusinessException
	 */
	@Test
	public void testVerificarServidorExameUnidadeConvenioSUS_NaoLancaExcecao_AEL_00490() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		final RapServidores rapServidores = new RapServidores();
		solicitacaoExame.setServidorResponsabilidade(rapServidores);
		itemSolicitacaoExameMocked.setSolicitacaoExame(solicitacaoExame);

		final List<AelServidoresExameUnid> lista = new LinkedList<AelServidoresExameUnid>();
		final AelServidoresExameUnid servidorExameUnidade = new AelServidoresExameUnid();
		servidorExameUnidade.setServidor(rapServidores);
		lista.add(servidorExameUnidade);

		Mockito.when(aelServidoresExameUnidDAO.buscaListaAelServidoresExameUnid(Mockito.any(AelExames.class),
				Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class))).thenReturn(lista);

		try {
			systemUnderTest.verificarServidorExameUnidadeConvenioSUS(itemSolicitacaoExameMocked);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Nao deveria ocorrer ApplicationBusinessException!");
		}

	}

	@Test
	/**
	 * Testa a permissão de uma unidade solicitante
	 */
	public void testVerificaPermissaoUnidadeSolicitanteSuccess() throws BaseException {
		try {
			setupPermissao();

			Mockito.when(aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(Mockito.any(AelExames.class),
					Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class),
					Mockito.any(AghUnidadesFuncionais.class))).thenReturn(new AelPermissaoUnidSolic());

			systemUnderTest.verificaPermissaoUnidadeSolicitante(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
	}

	@Test
	/**
	 * Testa a não permissão de uma unidade solicitante
	 */
	public void testVerificaPermissaoUnidadeSolicitanteError() throws BaseException {
		try {
			setupPermissao();

			Mockito.when(aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(Mockito.any(AelExames.class),
					Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class),
					Mockito.any(AghUnidadesFuncionais.class))).thenReturn(null);

			systemUnderTest.verificaPermissaoUnidadeSolicitante(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00426C);
		}
	}

	@Test
	/**
	 * Testa se existe dados de transporte paciente preenchido quando exige esta
	 * informação.
	 */
	public void testVerificaTransporteO2PacientSuccess() throws BaseException {
		try {
			setupPermissao();

			itemSolicitacaoExames.setIndUsoO2(Boolean.TRUE);
			itemSolicitacaoExames.setTipoTransporte(DominioTipoTransporte.A);

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndExigeTransporteO2(Boolean.TRUE);
			Mockito.when(aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(Mockito.any(AelExames.class),
					Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class),
					Mockito.any(AghUnidadesFuncionais.class))).thenReturn(aelPermissaoUnidSolic);

			systemUnderTest.verificaTransporteO2Paciente(itemSolicitacaoExames,
					aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(new AelExames(), new AelMateriaisAnalises(),
							new AghUnidadesFuncionais(), new AghUnidadesFuncionais()));
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa se passa da regra de preenchimento de dados de transporte paciente
	 * preenchido quando não exige esta informação.
	 */
	public void testVerificaTransporteO2PacientSuccess02() throws BaseException {
		try {
			setupPermissao();

			itemSolicitacaoExames.setIndUsoO2(null);
			itemSolicitacaoExames.setTipoTransporte(null);

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndExigeTransporteO2(Boolean.FALSE);
			Mockito.when(aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(Mockito.any(AelExames.class),
					Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class),
					Mockito.any(AghUnidadesFuncionais.class))).thenReturn(aelPermissaoUnidSolic);

			systemUnderTest.verificaTransporteO2Paciente(itemSolicitacaoExames,
					aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(new AelExames(), new AelMateriaisAnalises(),
							new AghUnidadesFuncionais(), new AghUnidadesFuncionais()));
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa o erro de dados de transporte quando o uso de o2 não foi preenchido
	 */
	public void testVerificaTransporteO2PacientError() throws BaseException {
		try {
			setupPermissao();

			itemSolicitacaoExames.setIndUsoO2(null);
			itemSolicitacaoExames.setTipoTransporte(DominioTipoTransporte.A);

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndExigeTransporteO2(Boolean.TRUE);
			Mockito.when(aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(Mockito.any(AelExames.class),
					Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class),
					Mockito.any(AghUnidadesFuncionais.class))).thenReturn(aelPermissaoUnidSolic);

			systemUnderTest.verificaTransporteO2Paciente(itemSolicitacaoExames,
					aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(new AelExames(), new AelMateriaisAnalises(),
							new AghUnidadesFuncionais(), new AghUnidadesFuncionais()));
			fail("testVerificaTransporteO2PacientError: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00427);
		}
	}

	private void setupPermissao() {
		itemSolicitacaoExames.setExame(new AelExames());
		itemSolicitacaoExames.setMaterialAnalise(new AelMateriaisAnalises());
		itemSolicitacaoExames.setUnidadeFuncional(new AghUnidadesFuncionais());
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setUnidadeFuncional(new AghUnidadesFuncionais());
		itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);
	}

	@Test
	/**
	 * Testa o erro de dados de transporte quando o tipo de transporte não foi
	 * preenchido
	 */
	public void testVerificaTransporteO2PacientError02() throws BaseException {
		try {
			setupPermissao();

			itemSolicitacaoExames.setIndUsoO2(Boolean.TRUE);
			itemSolicitacaoExames.setTipoTransporte(null);

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndExigeTransporteO2(Boolean.TRUE);
			Mockito.when(aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(Mockito.any(AelExames.class),
					Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class),
					Mockito.any(AghUnidadesFuncionais.class))).thenReturn(aelPermissaoUnidSolic);

			systemUnderTest.verificaTransporteO2Paciente(itemSolicitacaoExames,
					aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(new AelExames(), new AelMateriaisAnalises(),
							new AghUnidadesFuncionais(), new AghUnidadesFuncionais()));
			fail("testVerificaTransporteO2PacientError02: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00427);
		}
	}

	@Test
	/**
	 * Testa se passa da regra de data de programação colocando um índice de
	 * solicitação de sistema == TRUE (nao deve executar o resto do fluxo)
	 */
	public void testVerificaDataProgramacaoSuccess02() throws BaseException {
		try {
			setupPermissao();

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
			aelExamesMaterialAnalise.setIndSolSistema(Boolean.TRUE);
			Mockito.when(aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(),
					Mockito.anyInt())).thenReturn(aelExamesMaterialAnalise);

			itemSolicitacaoExames.setDthrProgramada(new Date());

			systemUnderTest.verificaDataProgramacao(itemSolicitacaoExames, aelPermissaoUnidSolic);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa se passa da regra de data de programação verificando se atualizou a
	 * data para a atual
	 */
	public void testVerificaDataProgramacaoSuccess03() throws BaseException {
		try {
			setupPermissao();

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
			aelExamesMaterialAnalise.setIndSolSistema(Boolean.FALSE);
			Mockito.when(aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(),
					Mockito.anyInt())).thenReturn(aelExamesMaterialAnalise);

			itemSolicitacaoExames.setDthrProgramada(null);

			systemUnderTest.verificaDataProgramacao(itemSolicitacaoExames, aelPermissaoUnidSolic);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa se passa da regra de data de programação.
	 */
	public void testVerificaDataProgramacaoSuccess04() throws BaseException {
		try {
			setupPermissao();

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();

			AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
			aelExamesMaterialAnalise.setIndSolSistema(Boolean.FALSE);
			Mockito.when(aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(),
					Mockito.anyInt())).thenReturn(aelExamesMaterialAnalise);

			AghParametros parametros = new AghParametros();
			parametros.setVlrNumerico(new BigDecimal("120"));
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametros);

			itemSolicitacaoExames.setDthrProgramada(new Date());
			AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
			solicitacaoExame.setCriadoEm(new Date());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

			systemUnderTest.verificaDataProgramacao(itemSolicitacaoExames, aelPermissaoUnidSolic);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa se passa da regra de data de programação diminuindo exatamento o
	 * mesmo que o parâmetro informa.
	 */
	public void testVerificaDataProgramacaoSuccess05() throws BaseException {
		try {
			setupPermissao();

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.S);

			AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
			aelExamesMaterialAnalise.setIndSolSistema(Boolean.FALSE);
			Mockito.when(aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(),
					Mockito.anyInt())).thenReturn(aelExamesMaterialAnalise);

			AghParametros parametros = new AghParametros();
			parametros.setVlrNumerico(new BigDecimal("120"));
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametros);

			Calendar cal = Calendar.getInstance();
			Date dataProgramada = cal.getTime();
			cal.add(Calendar.MINUTE, -120);

			itemSolicitacaoExames.setDthrProgramada(dataProgramada);
			AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
			solicitacaoExame.setCriadoEm(cal.getTime());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

			systemUnderTest.verificaDataProgramacao(itemSolicitacaoExames, aelPermissaoUnidSolic);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa o erro da regra de data de programação quando não permite programar
	 * exames
	 */
	public void testVerificaDataProgramacaoError() throws BaseException {
		try {
			setupPermissao();

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndPermiteProgramarExames(null);

			AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
			aelExamesMaterialAnalise.setIndSolSistema(Boolean.FALSE);
			Mockito.when(aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(),
					Mockito.anyInt())).thenReturn(aelExamesMaterialAnalise);

			AghParametros parametros = new AghParametros();
			parametros.setVlrNumerico(new BigDecimal("120"));
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametros);

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -121);

			itemSolicitacaoExames.setDthrProgramada(new Date());
			AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
			solicitacaoExame.setCriadoEm(cal.getTime());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

			systemUnderTest.verificaDataProgramacao(itemSolicitacaoExames, aelPermissaoUnidSolic);
			fail("testVerificaDataProgramacaoError: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00430);
		}
	}

	@Test
	/**
	 * Testa o sucesso quando permite programar exames.
	 */
	public void testVerificaDataProgramacaoSuccess06() throws BaseException {
		try {
			setupPermissao();

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.S);

			AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
			aelExamesMaterialAnalise.setIndSolSistema(Boolean.FALSE);
			Mockito.when(aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(),
					Mockito.anyInt())).thenReturn(aelExamesMaterialAnalise);

			AghParametros parametros = new AghParametros();
			parametros.setVlrNumerico(new BigDecimal("120"));
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC))
					.thenReturn(parametros);

			AghParametros parametros1 = new AghParametros();
			parametros1.setVlrNumerico(new BigDecimal("119"));
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PRAZO_MAX_SOLIC_PROGRAMADA))
					.thenReturn(parametros1);

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -120);

			itemSolicitacaoExames.setDthrProgramada(new Date());
			AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
			solicitacaoExame.setCriadoEm(cal.getTime());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

			systemUnderTest.verificaDataProgramacao(itemSolicitacaoExames, aelPermissaoUnidSolic);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);
	}

	@Test
	/**
	 * Testa o erro da regra de data de programação quando
	 */
	public void testVerificaDataProgramacaoError02() throws BaseException {
		try {
			setupPermissao();

			AelPermissaoUnidSolic aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			aelPermissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.S);

			AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
			aelExamesMaterialAnalise.setIndSolSistema(Boolean.FALSE);
			Mockito.when(aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(),
					Mockito.anyInt())).thenReturn(aelExamesMaterialAnalise);

			AghParametros parametros = new AghParametros();
			parametros.setVlrNumerico(new BigDecimal("120"));
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC))
					.thenReturn(parametros);

			AghParametros parametros1 = new AghParametros();
			parametros1.setVlrNumerico(BigDecimal.ONE);
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PRAZO_MAX_SOLIC_PROGRAMADA))
					.thenReturn(parametros1);

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -2880);

			itemSolicitacaoExames.setDthrProgramada(new Date());
			AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
			solicitacaoExame.setCriadoEm(cal.getTime());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

			systemUnderTest.verificaDataProgramacao(itemSolicitacaoExames, aelPermissaoUnidSolic);
			fail("testVerificaDataProgramacaoError: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00467);
		}
	}

	/**
	 * Testa a regra para atribuir IndCargaContador para false
	 */
	@Test
	public void testAtribuirCargaContador() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		itemSolicitacaoExameMocked.setExame(new AelExames());
		itemSolicitacaoExameMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndPertenceContador(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		systemUnderTest.atribuirCargaContador(itemSolicitacaoExameMocked);
		assertEquals(Boolean.FALSE, itemSolicitacaoExameMocked.getIndCargaContador());
	}

	/**
	 * Testa a regra para nao atribuir IndCargaContador para false
	 */
	@Test
	public void testAtribuirCargaContador_IndCargaContador_Nulo() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		itemSolicitacaoExameMocked.setExame(new AelExames());
		itemSolicitacaoExameMocked.setMaterialAnalise(new AelMateriaisAnalises());

		final AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndPertenceContador(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		systemUnderTest.atribuirCargaContador(itemSolicitacaoExameMocked);

		assertNull(itemSolicitacaoExameMocked.getIndCargaContador());
	}

	/**
	 * Testa atribuicao de valor
	 */
	@Test
	public void testVerificarIndicativoImpressoLaudoAtvSeqNaoNulo() {
		AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setAtendimentoDiverso(new AelAtendimentoDiversos());
		itemSolicitacaoExame.setSolicitacaoExame(solicitacaoExames);

		systemUnderTest.verificarIndicativoImpressoLaudo(itemSolicitacaoExame);
		assertEquals(DominioIndImpressoLaudo.N, itemSolicitacaoExame.getIndImpressoLaudo());
	}

	/**
	 * Testa o valor nulo
	 */
	@Test
	public void testVerificarIndicativoImpressoLaudoAtvSeqNulo() {
		AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		itemSolicitacaoExame.setSolicitacaoExame(solicitacaoExames);

		systemUnderTest.verificarIndicativoImpressoLaudo(itemSolicitacaoExame);
		assertNull(itemSolicitacaoExame.getIndImpressoLaudo());
	}

	/**
	 * Testa a atribuicao de valor do atributo seq
	 */
	@Test
	public void testAtribuirUnfSeqAvisa() {
		final AelItemSolicitacaoExames mockedItemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelExames exame = new AelExames();
		exame.setSigla("A");
		mockedItemSolicitacaoExame.setExame(exame);
		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setSeq(1);
		mockedItemSolicitacaoExame.setMaterialAnalise(materialAnalise);
		final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(Short.valueOf("1"));
		mockedItemSolicitacaoExame.setUnidadeFuncional(unidadeFuncional);
		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional);
		mockedItemSolicitacaoExame.setSolicitacaoExame(solicitacaoExame);

		final AelPermissaoUnidSolic permissao = new AelPermissaoUnidSolic();
		permissao.setUnfSeqAvisa(unidadeFuncional);

		Mockito.when(aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
				Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(permissao);

		systemUnderTest.atribuirUnfSeqAvisa(mockedItemSolicitacaoExame);
		assertEquals(Short.valueOf("1"), permissao.getUnfSeqAvisa().getSeq());
	}

	/**
	 * Testa o lancamento de excecao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testVerificarSituacaoExameMaterialAnaliseLancaExcecao_AEL_01007() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames entityMocked = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setIndColetavel(Boolean.TRUE);
		situacaoItemSolicitacao.setCodigo("AX");
		entityMocked.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		entityMocked.setMaterialAnalise(materialAnalise);

		systemUnderTest.verificarSituacaoExameMaterialAnaliseNaAlteracao(entityMocked);

	}

	/**
	 * Testa o lancamento de excecao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarSituacaoExameMaterialAnaliseNaoLancaExcecao_AEL_01007()
			throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setIndColetavel(Boolean.TRUE);
		situacaoItemSolicitacao.setCodigo("AC");
		itemSolicitacaoExameMocked.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		itemSolicitacaoExameMocked.setMaterialAnalise(materialAnalise);

		systemUnderTest.verificarSituacaoExameMaterialAnalise(itemSolicitacaoExameMocked);
		assertEquals(DominioSituacaoItemSolicitacaoExame.AC.toString(),
				itemSolicitacaoExameMocked.getSituacaoItemSolicitacao().getCodigo());
	}

	@Test
	/**
	 * RN 10.1 Testa o erro da regra de permissao de transição da matriz de
	 * permissões de situação de exame passando um código diferente.
	 */
	public void testVerificarPermissaoMatrizTransicaoError() throws BaseException {
		try {
			final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
			final AelSitItemSolicitacoes situacaoItemSolicitacaoOriginal = new AelSitItemSolicitacoes();
			situacaoItemSolicitacaoOriginal.setCodigo("COD");
			atdOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacaoOriginal);
			// mockingContext.checking(new Expectations() {{
			// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
			// will(returnValue(atdOriginal));
			// }});

			List<AelMatrizSituacao> lista = new ArrayList<AelMatrizSituacao>();
			AelMatrizSituacao matrizSit = new AelMatrizSituacao();

			AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
			situacaoItemSolicitacao.setCodigo("COD PARA");
			matrizSit.setSituacaoItemSolicitacaoPara(situacaoItemSolicitacao);

			Mockito.when(aelMatrizSituacaoDAO
					.pesquisarMatrizSituacaoPorSituacaoOrigem(Mockito.any(AelSitItemSolicitacoes.class)))
					.thenReturn(lista);

			AelSitItemSolicitacoes situacaoItemSolicitacao1 = new AelSitItemSolicitacoes();
			situacaoItemSolicitacao1.setCodigo("COD PARA ERRADO");
			itemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoItemSolicitacao1);

			systemUnderTest.verificarPermissaoMatrizTransicao(atdOriginal, itemSolicitacaoExames);
			fail("testVerificaDataProgramacaoError: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00477);
		}
	}

	@Test
	/**
	 * RN 10.1 Testa o sucesso da permissao de transição da matriz de permissões
	 * de situação de exame.
	 */
	public void testVerificarPermissaoMatrizTransicaoSuccess() throws BaseException {
		try {
			AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
			AelSitItemSolicitacoes situacaoItemSolic = new AelSitItemSolicitacoes();
			situacaoItemSolic.setCodigo("COD");
			atdOriginal.setSituacaoItemSolicitacao(situacaoItemSolic);

			// mockingContext.checking(new Expectations() {{
			// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
			// will(returnValue(atdOriginal));
			// }});

			final AelSitItemSolicitacoes situacaoItemSolicitacaoOriginal = new AelSitItemSolicitacoes();
			situacaoItemSolicitacaoOriginal.setCodigo("COD PARA");

			List<AelMatrizSituacao> lista = new ArrayList<AelMatrizSituacao>();
			AelMatrizSituacao matrizSit = new AelMatrizSituacao();
			AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
			situacaoItemSolicitacao.setCodigo("COD PARA");
			matrizSit.setSituacaoItemSolicitacaoPara(situacaoItemSolicitacao);
			lista.add(matrizSit);

			Mockito.when(aelMatrizSituacaoDAO
					.pesquisarMatrizSituacaoPorSituacaoOrigem(Mockito.any(AelSitItemSolicitacoes.class)))
					.thenReturn(lista);

			Mockito.when(aelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString()))
					.thenReturn(situacaoItemSolicitacaoOriginal);

			AelSitItemSolicitacoes situacaoItemSolicitacao1 = new AelSitItemSolicitacoes();
			situacaoItemSolicitacao1.setCodigo("COD PARA");
			itemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoItemSolicitacao1);

			assertTrue(systemUnderTest.verificarPermissaoMatrizTransicao(atdOriginal, itemSolicitacaoExames) != null);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
	}

	@Test
	/**
	 * RN 10.2 Testa o erro da regra de permissões de responsável, quando o
	 * servidor responsabilidade é null
	 */
	public void testVerificarPermissaoResponsavelTransicaoSituacaoError() throws BaseException {
		try {

			Mockito.when(examesFacade.validarPermissaoAlterarExameSituacao(Mockito.any(AelMatrizSituacao.class),
					Mockito.any(RapServidores.class))).thenReturn(false);

			itemSolicitacaoExames.setServidorResponsabilidade(null);
			systemUnderTest.verificarPermissaoResponsavelTransicaoSituacao(new AelMatrizSituacao(),
					itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
		}
	}

	@Test
	/****
	 * RN 10.2 Testa o sucesso da regra de permissões de responsável, quando o
	 * servidor responsabilidade é null.
	 */
	public void testVerificarPermissaoResponsavelTransicaoSuccess() throws BaseException {
		try {
			Mockito.when(examesFacade.validarPermissaoAlterarExameSituacao(Mockito.any(AelMatrizSituacao.class),
					Mockito.any(RapServidores.class))).thenReturn(true);

			itemSolicitacaoExames.setServidorResponsabilidade(null);
			systemUnderTest.verificarPermissaoResponsavelTransicaoSituacao(new AelMatrizSituacao(),
					itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não deu erro, passou (Função void)
		assertEquals(1, 1);
	}

	@Test
	/**
	 * RN 10.3 Testa o erro da regra de permissões de responsável, quando o
	 * servidor responsabilidade é diferente de null
	 */
	public void testVerificarPermissaoResponsavelTransicaoError02() throws BaseException {
		try {

			Mockito.when(examesFacade.validarPermissaoAlterarExameSituacao(Mockito.any(AelMatrizSituacao.class),
					Mockito.any(RapServidores.class))).thenReturn(false);

			itemSolicitacaoExames.setServidorResponsabilidade(new RapServidores());

			systemUnderTest.verificarPermissaoResponsavelTransicaoSituacao(new AelMatrizSituacao(),
					itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
		}
	}

	@Test
	/**
	 * RN 10.3 Testa o sucesso da regra de permissões de responsável, quando o
	 * servidor responsabilidade é diferente de null
	 */
	public void testVerificarPermissaoResponsavelTransicaoSuccess02() throws BaseException {
		try {
			Mockito.when(examesFacade.validarPermissaoAlterarExameSituacao(Mockito.any(AelMatrizSituacao.class),
					Mockito.any(RapServidores.class))).thenReturn(true);

			itemSolicitacaoExames.setServidorResponsabilidade(new RapServidores());

			systemUnderTest.verificarPermissaoResponsavelTransicaoSituacao(new AelMatrizSituacao(),
					itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}

		// Não deu erro, passou (Função void)
		assertEquals(1, 1);
	}

	/**
	 * Testa valor nulo
	 */
	@Test
	public void testSetarIndicativoUsoO2Unidade_TipoTransporte_Nulo() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		systemUnderTest.setarIndicativoUsoO2Unidade(itemSolicitacaoExameMocked);
		assertNull(itemSolicitacaoExameMocked.getTipoTransporte());
	}

	/**
	 * Testa a atribuicao de valor
	 */
	@Test
	public void testAtribuirTipoTransporte_TipoTransporte_Valido() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		itemSolicitacaoExameMocked.setTipoTransporte(DominioTipoTransporte.A);

		systemUnderTest.atribuirTipoTransporte(itemSolicitacaoExameMocked);

		assertEquals(DominioTipoTransporte.A, itemSolicitacaoExameMocked.getTipoTransporte());
	}

	@Test
	/**
	 * RN8 Testa se o método irá dar return quando o p_new_ise_seqp é != null.
	 */
	public void testVerificarConselhoProfissionalOuPermissaoSuccess() {
		try {
			AelItemSolicitacaoExamesId aelItemSolicitacaoExamesId = new AelItemSolicitacaoExamesId();
			aelItemSolicitacaoExamesId.setSeqp(Short.valueOf("1"));
			itemSolicitacaoExames.setId(aelItemSolicitacaoExamesId);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(), itemSolicitacaoExames.getServidorResponsabilidade(),
					new AghAtendimentos(), itemSolicitacaoExames.getExame(),
					itemSolicitacaoExames.getMaterialAnalise());
		} catch (BaseException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);

	}

	@Test
	/**
	 * RN8 Testa se o método irá dar return quando o servidor é = null.
	 */
	public void testVerificarConselhoProfissionalOuPermissaoServidorNulo() {
		try {
			itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			itemSolicitacaoExames.setSolicitacaoExame(new AelSolicitacaoExames());

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(), itemSolicitacaoExames.getServidorResponsabilidade(),
					itemSolicitacaoExames.getSolicitacaoExame().getAtendimento(), itemSolicitacaoExames.getExame(),
					itemSolicitacaoExames.getMaterialAnalise());
		} catch (BaseException e) {
			fail("Exeção gerada. " + e.getCode());
		}
		// Não ocorreu erro, então teste realizado com sucesso.
		assertEquals(1, 1);

	}

	@Test
	/**
	 * RN8 Testa se o método irá dar return quando a solicitação é = null. Deve
	 * estourar uma exceção: AEL-00579
	 */
	public void testVerificarConselhoProfissionalOuPermissaoSemSolicitacao() {
		try {
			itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			itemSolicitacaoExames.setSolicitacaoExame(null);

			itemSolicitacaoExames.setSolicitacaoExame(null);

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(), itemSolicitacaoExames.getServidorResponsabilidade(),
					new AghAtendimentos(), itemSolicitacaoExames.getExame(),
					itemSolicitacaoExames.getMaterialAnalise());
			fail("testVerificarConselhoProfissionalOuPermissaoSemSolicitacao: Exeção esperada não gerada. ");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00579);
		}
	}

	@Test
	/**
	 * RN8 Testa se o método irá rodar sem apresentar erro quando a Origem é A,
	 * tem permissão e existe protocolo liberado.
	 */
	public void testVerificarConselhoProfissionalOuPermissaoSuccess02() {
		try {

			itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setOrigem(DominioOrigemAtendimento.A);
			solicitacaoExames.setAtendimento(atendimento);
			solicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);
			RapServidores servidor = itemSolicitacaoExames.getSolicitacaoExame().getServidorResponsabilidade();
			servidor.setId(new RapServidoresId(1, (short) 1));

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(),
					servidor,
					itemSolicitacaoExames.getSolicitacaoExame().getAtendimento(), new AelExames(),
					itemSolicitacaoExames.getMaterialAnalise());
		} catch (BaseException e) {
		}
	}

	@Test
	/**
	 * RN8 Testa se o método irá rodar sem apresentar erro quando quando não
	 * existe permissão de consistir protocolo exames ambulatorial e o conselho
	 * profissional está ok
	 */
	public void testVerificarConselhoProfissionalOuPermissaoSemPermissaoConsistirProtocolo() {
		try {
			itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			AelExames exame = new AelExames();
			itemSolicitacaoExames.setExame(exame);
			AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setOrigem(DominioOrigemAtendimento.A);
			solicitacaoExames.setAtendimento(atendimento);
			solicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);
			RapServidores servidor = itemSolicitacaoExames.getSolicitacaoExame().getServidorResponsabilidade();
			servidor.setId(new RapServidoresId(1, (short) 1));

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(),
					servidor,
					itemSolicitacaoExames.getSolicitacaoExame().getAtendimento(), itemSolicitacaoExames.getExame(),
					itemSolicitacaoExames.getMaterialAnalise());
		} catch (BaseException e) {
		}
	}

	@Test
	/**
	 * RN8 Testa se o método irá rodar sem apresentar erro quando quando não
	 * existe permissão de consistir protocolo exames ambulatorial
	 * 
	 */
	public void testVerificarConselhoProfissionalOuPermissaoSemProtocoloError() {
		try {
			itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setOrigem(DominioOrigemAtendimento.A);
			solicitacaoExames.setAtendimento(atendimento);
			solicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);
			RapServidores servidor = itemSolicitacaoExames.getSolicitacaoExame().getServidorResponsabilidade();
			servidor.setId(new RapServidoresId(1, (short) 1));

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(),
					servidor,
					itemSolicitacaoExames.getSolicitacaoExame().getAtendimento(), new AelExames(),
					itemSolicitacaoExames.getMaterialAnalise());
			fail("testVerificarConselhoProfissionalOuPermissaoSemProtocoloError: Exeção esperada não gerada. ");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00444B);
		}

	}

	@Test
	/**
	 * RN8 Testa se o método irá rodar sem apresentar erro quando o parâmetro
	 * P_CONSISTE_POP_AMB não for 'S'
	 * 
	 */
	public void testVerificarConselhoProfissionalOuPermissaoNaoTemConsistirPopAmb() {
		try {

			itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			AelExames exame = new AelExames();
			itemSolicitacaoExames.setExame(exame);
			AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setOrigem(DominioOrigemAtendimento.A);
			solicitacaoExames.setAtendimento(atendimento);
			solicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);
			RapServidores servidor = itemSolicitacaoExames.getSolicitacaoExame().getServidorResponsabilidade();
			servidor.setId(new RapServidoresId(1, (short) 1));

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("N");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(),
					servidor,
					itemSolicitacaoExames.getSolicitacaoExame().getAtendimento(), itemSolicitacaoExames.getExame(),
					itemSolicitacaoExames.getMaterialAnalise());
		} catch (BaseException e) {
		}
	}

	/**
	 * RN8 Testa se o método irá rodar sem apresentar erro quando a Origem é
	 * diferente de A, tem permissão e existe protocolo liberado.
	 */
	public void testVerificarConselhoProfissionalOuPermissaoOrigemDiferenteA() {
		try {
			itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setOrigem(DominioOrigemAtendimento.C);
			solicitacaoExames.setAtendimento(atendimento);
			solicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(),
					itemSolicitacaoExames.getSolicitacaoExame().getServidorResponsabilidade(),
					itemSolicitacaoExames.getSolicitacaoExame().getAtendimento(), itemSolicitacaoExames.getExame(),
					itemSolicitacaoExames.getMaterialAnalise());
		} catch (BaseException e) {
			fail("Exeção gerada: " + e.getCode());
		}
		assertEquals(1, 1);
	}

	@Test
	/**
	 * RN8 Testa se o método irá apresentar o erro AEL-00444B quando não tem
	 * Permissao Protocolo Enfermagem Exames Ambulatorial.
	 */
	public void testVerificarConselhoProfissionalOuPermissaoSemPermissaoProtocoloEnfermagemExamesAmbulatorial() {
		try {
			// itemSolicitacaoExames.setId(new AelItemSolicitacaoExamesId());
			AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setOrigem(DominioOrigemAtendimento.A);
			solicitacaoExames.setAtendimento(atendimento);
			solicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);
			RapServidores servidor = itemSolicitacaoExames.getSolicitacaoExame().getServidorResponsabilidade();
			servidor.setId(new RapServidoresId(1, (short) 1));

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
					.thenReturn(parametro);

			systemUnderTest.verificarConselhoProfissionalOuPermissao(itemSolicitacaoExames.getId(),
					itemSolicitacaoExames.getSolicitacaoExame(),
					servidor,
					itemSolicitacaoExames.getSolicitacaoExame().getAtendimento(), new AelExames(),
					itemSolicitacaoExames.getMaterialAnalise());
			fail("testVerificarConselhoProfissionalOuPermissaoSemPermissaoProtocoloEnfermagemExamesAmbulatorial: Exeção esperada não gerada. ");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00444B);
		}
	}

	@Test
	/**
	 * RN8 Testa se o método irá apresentar o erro AEL-00444
	 */
	public void testVerificaConselhoProfissionalError() {
		
		try {
			AelExames exame = new AelExames();
			AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
			AghAtendimentos atendimento = new AghAtendimentos();
			solicitacaoExames.setAtendimento(atendimento);
			solicitacaoExames.setServidorResponsabilidade(new RapServidores());
			itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExames);
			
			itemSolicitacaoExames.setExame(exame);
			RapServidores servidor = itemSolicitacaoExames.getSolicitacaoExame().getServidorResponsabilidade();
			servidor.setId(new RapServidoresId(1, (short) 1));
			itemSolicitacaoExames.setServidorResponsabilidade(servidor);
						
			systemUnderTest.verificaConselhoProfissional(itemSolicitacaoExames.getExame(),
					itemSolicitacaoExames.getServidorResponsabilidade(), itemSolicitacaoExames.getMaterialAnalise());
			fail("testVerificaConselhoProfissionalError: Exeção esperada não gerada. ");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ItemSolicitacaoExameRNExceptionCode.AEL_00444B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01656
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracao_LancaExcecao_AEL_01656() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarFormaRespiracao(aelItemSolicitacaoExamesMocked);
			Assert.fail("Deveria ocorrer o ApplicationBusinessException AEL_01656");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01656B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01657
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracao_LancaExcecao_AEL_01657() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setFormaRespiracao(DominioFormaRespiracao.DOIS);
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarFormaRespiracao(aelItemSolicitacaoExamesMocked);
			Assert.fail("Deveria ocorrer o ApplicationBusinessException AEL_01657");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01657B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01658
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracao_LancaExcecao_AEL_01658() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setFormaRespiracao(DominioFormaRespiracao.TRES);
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarFormaRespiracao(aelItemSolicitacaoExamesMocked);
			Assert.fail("Deveria ocorrer o ApplicationBusinessException AEL_01658");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01658B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01816
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracao_LancaExcecao_AEL_01816() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setFormaRespiracao(DominioFormaRespiracao.UM);
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarFormaRespiracao(aelItemSolicitacaoExamesMocked);
			Assert.fail("Deveria ocorrer o ApplicationBusinessException AEL_01816");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01816B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01817
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracao_LancaExcecao_AEL_01817() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setLitrosOxigenio(BigDecimal.ONE);
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarFormaRespiracao(aelItemSolicitacaoExamesMocked);
			Assert.fail("Deveria ocorrer o ApplicationBusinessException AEL_01817");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01817B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01818
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracao_LancaExcecao_AEL_01818() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setPercOxigenio(Short.valueOf("1"));
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarFormaRespiracao(aelItemSolicitacaoExamesMocked);
			Assert.fail("Deveria ocorrer o ApplicationBusinessException AEL_01818");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01818B);
		}
	}

	/**
	 * Testa a funcao sem lancar excecoes, com sucesso
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracao_LancaExcecao_Com_Sucesso() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		systemUnderTest.verificarFormaRespiracao(aelItemSolicitacaoExamesMocked);
		assertEquals(Boolean.FALSE, aelExamesMaterialAnalise.getIndFormaRespiracao());
	}

	/**
	 * Testa o lancamento da excecao AEL_00447
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00447() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00447B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00451
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00451() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setIndGeradoAutomatico(Boolean.FALSE);

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00451B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00452
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00452() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("1"));

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00452B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00454
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00454() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("2"));

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setUnidTempoColetaAmostras(DominioUnidTempo.D);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00454B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01153
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_01153() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("2"));
		aelItemSolicitacaoExamesMocked.setIntervaloDias(Byte.valueOf("0"));

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setUnidTempoColetaAmostras(DominioUnidTempo.D);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01153B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00455
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00455() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("2"));

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setUnidTempoColetaAmostras(DominioUnidTempo.H);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00455B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_01153 no intervalo de horas
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_01153_IntevaloHorasDifNulo() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("2"));
		aelItemSolicitacaoExamesMocked.setIntervaloDias(Byte.valueOf("1"));
		aelItemSolicitacaoExamesMocked.setIntervaloHoras(DateUtil.obterDataComHoraInical(new Date()));

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setUnidTempoColetaAmostras(DominioUnidTempo.H);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_01153B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00456
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00456() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("2"));
		aelItemSolicitacaoExamesMocked.setIntervaloDias(Byte.valueOf("1"));

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setUnidTempoColetaAmostras(null);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00456B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00457
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00457() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("0"));

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setIndUsaIntervaloCadastrado(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00457B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00458
	 */
	@Test
	public void testVerificarExameMaterialAnalise_LancaExcecao_AEL_00458() {
		final AelItemSolicitacaoExames aelItemSolicitacaoExamesMocked = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExamesMocked.setExame(new AelExames());
		aelItemSolicitacaoExamesMocked.setMaterialAnalise(new AelMateriaisAnalises());
		aelItemSolicitacaoExamesMocked.setNroAmostras(Byte.valueOf("0"));
		aelItemSolicitacaoExamesMocked.setIntervaloColeta(new AelIntervaloColeta());

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setIndUsaIntervaloCadastrado(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);
		try {
			systemUnderTest.verificarExameMaterialAnalise(aelItemSolicitacaoExamesMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00458B);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00581
	 */
	@Test
	public void testVerificarAtualizacaoUsuarioErro() {
		RapServidoresId id = new RapServidoresId(111, Short.valueOf("111"));
		RapServidores servidor = new RapServidores(id);

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		solicitacaoExame.setServidor(servidor);
		solicitacaoExame.setServidorResponsabilidade(servidor);

		itemSolicitacaoExames.setSolicitacaoExame(solicitacaoExame);

		try {
			systemUnderTest.verificarAtualizacaoUsuario(itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00581);
		}
	}

	/**
	 * Testa o lancamento da excecao AEL_00542
	 */
	@Test
	public void testVerificarPossibilidadeAlteracaoErro() {
		final AelItemSolicitacaoExames itemSolicitacaoExamesOld = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOld.setTipoColeta(DominioTipoColeta.N);

		itemSolicitacaoExames.setTipoColeta(DominioTipoColeta.U);

		AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("LI");

		itemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(itemSolicitacaoExamesOld));
		// }});

		try {
			systemUnderTest.verificarPossibilidadeAlteracao(itemSolicitacaoExamesOld, itemSolicitacaoExames);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00542);
		}
	}

	/**
	 * Testa se não houve modificacao nos dados de respiracao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracaoNaAlteracao_naoModificado() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		systemUnderTest.verificarFormaRespiracaoNaAlteracao(atdOriginal, itemSolicitacaoExameMocked);
		assertEquals(Boolean.FALSE, AGHUUtil.modificados(itemSolicitacaoExameMocked.getFormaRespiracao(),
				atdOriginal.getFormaRespiracao()));
	}

	/**
	 * Testa se houve modificacao e chama o metodo verificarFormaRespiracao()
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarFormaRespiracaoNaAlteracao_foiModificado() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		itemSolicitacaoExameMocked.setFormaRespiracao(DominioFormaRespiracao.UM);
		itemSolicitacaoExameMocked.setExame(new AelExames());
		itemSolicitacaoExameMocked.setMaterialAnalise(new AelMateriaisAnalises());

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		atdOriginal.setFormaRespiracao(DominioFormaRespiracao.DOIS);
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndFormaRespiracao(Boolean.TRUE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		systemUnderTest.verificarFormaRespiracaoNaAlteracao(atdOriginal, itemSolicitacaoExameMocked);
		assertEquals(Boolean.TRUE, AGHUUtil.modificados(itemSolicitacaoExameMocked.getFormaRespiracao(),
				atdOriginal.getFormaRespiracao()));
	}

	/**
	 * RN27 Testa se não houve modificacao e nao chama excecao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarAlteracaoExames_NaoLancaExcecao_AEL_00488() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		try {
			systemUnderTest.verificarAlteracaoExames(atdOriginal, itemSolicitacaoExameMocked);
			assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Nao deveria ocorrer ApplicationBusinessException");
		}
	}

	/**
	 * RN27 Testa se houve modificacao e lanca a excecao AEL_00488
	 */
	@Test
	public void testVerificarAlteracaoExames_LancaExecao_AEL_00488() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		final AelExames exames = new AelExames();
		exames.setSigla("TIR");
		atdOriginal.setExame(exames);
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		try {
			systemUnderTest.verificarAlteracaoExames(atdOriginal, itemSolicitacaoExameMocked);
		} catch (ApplicationBusinessException e) {
			assertTrue(e.getCode() == ItemSolicitacaoExameRNExceptionCode.AEL_00488);
		}
	}

	/**
	 * RN32, RN33 e RN46 Testa se nao houve modificacao e nao insere
	 * AelInformacaoSolicitacaoUnidadeExecutora
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAtualizarInfomacoesSolicitacaoUnidadeExecutora_NaoModificado() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		systemUnderTest.atualizarInfomacoesSolicitacaoUnidadeExecutora(atdOriginal, itemSolicitacaoExameMocked);
		assertEquals(Boolean.FALSE,
				AGHUUtil.modificados(itemSolicitacaoExameMocked.getIndUsoO2Un(), atdOriginal.getIndUsoO2Un()));
	}

	/**
	 * RN32, RN33 e RN46 Testa se houve modificacao e chama o metodo de inserir
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAtualizarInfomacoesSolicitacaoUnidadeExecutora_Modificado() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		itemSolicitacaoExameMocked.setIndUsoO2Un(Boolean.TRUE);

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		systemUnderTest.atualizarInfomacoesSolicitacaoUnidadeExecutora(atdOriginal, itemSolicitacaoExameMocked);
		assertEquals(Boolean.TRUE,
				AGHUUtil.modificados(itemSolicitacaoExameMocked.getIndUsoO2Un(), atdOriginal.getIndUsoO2Un()));
	}

	/**
	 * RN34 Testa se não houve modificacao e nao chama o metodo
	 * verificarPermissoesQuandoGrupoConvenioSUS
	 */
	@Test
	public void testVerificarPermissoesQuandoGrupoConvenioSUSAlteracao_NaoModificado() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		try {
			systemUnderTest.verificarPermissoesQuandoGrupoConvenioSUSAlteracao(atdOriginal, itemSolicitacaoExameMocked);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Nao deveria ocorrer BaseException");
		}
	}

	/**
	 * RN35 Testa se não houve modificacao e nao chama o metodo
	 * verificarExameMaterialAnalise
	 */
	@Test
	public void testVerificarExamesMaterialAnalise_NaoModificado() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		// itemSolicitacaoExameMocked.setDescRegiaoAnatomica("TESTE");

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		systemUnderTest.verificarExamesMaterialAnalise(atdOriginal, itemSolicitacaoExameMocked);
		assertNull(itemSolicitacaoExameMocked.getDescRegiaoAnatomica());
	}

	/**
	 * RN35 Testa se houve modificacao e chama o metodo
	 * verificarExameMaterialAnalise
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificarExamesMaterialAnalise_Modificado() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		itemSolicitacaoExameMocked.setExame(new AelExames());
		itemSolicitacaoExameMocked.setMaterialAnalise(new AelMateriaisAnalises());
		itemSolicitacaoExameMocked.setNroAmostras(Byte.valueOf("0"));
		itemSolicitacaoExameMocked.setDescRegiaoAnatomica("TESTE");

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		// mockingContext.checking(new Expectations() {{
		// oneOf(aelItemSolicitacaoExameDAO).obterOriginal(with(any(AelItemSolicitacaoExamesId.class)));
		// will(returnValue(atdOriginal));
		// }});

		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelExamesMaterialAnalise.setIndUsaIntervaloCadastrado(Boolean.FALSE);
		Mockito.when(
				aelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(aelExamesMaterialAnalise);

		systemUnderTest.verificarExamesMaterialAnalise(atdOriginal, itemSolicitacaoExameMocked);
		assertTrue(StringUtils.isNotEmpty(itemSolicitacaoExameMocked.getDescRegiaoAnatomica()));
	}

	/**
	 * RN37 Testa se a situacao e diferente de cancelado
	 */
	@Test
	public void testVerificarSituacaoCancelado_SituacaoNaoCancelado() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();

		AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("AE");
		atdOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		Mockito.when(aelItemSolicitacaoExameDAO.obterOriginal(Mockito.any(AelItemSolicitacaoExamesId.class)))
				.thenReturn(atdOriginal);

		systemUnderTest.verificarSituacaoCancelado(itemSolicitacaoExameMocked, atdOriginal);
		assertEquals(DominioSituacaoItemSolicitacaoExame.AE.toString(),
				atdOriginal.getSituacaoItemSolicitacao().getCodigo());
	}

	/**
	 * RN37 Testa se a nova situacao e igual a anterior
	 */
	@Test
	public void testVerificarSituacaoCancelado_NovaSituacaoIgualAnterior() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("CA");
		itemSolicitacaoExameMocked.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		situacaoItemSolicitacao.setCodigo("AE");
		atdOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		Mockito.when(aelItemSolicitacaoExameDAO.obterOriginal(Mockito.any(AelItemSolicitacaoExamesId.class)))
				.thenReturn(atdOriginal);

		systemUnderTest.verificarSituacaoCancelado(itemSolicitacaoExameMocked, atdOriginal);
		assertEquals(DominioSituacaoItemSolicitacaoExame.AE.toString(),
				atdOriginal.getSituacaoItemSolicitacao().getCodigo());
	}

	/**
	 * RN37 Testa se a nova situacao e diferente da anterior<br>
	 * e limpa o motivo de cancelamento
	 */
	@Test
	public void testVerificarSituacaoCancelado_LimpaMotivoCancelamento() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("AX");
		itemSolicitacaoExameMocked.setSituacaoItemSolicitacao(situacaoItemSolicitacao);

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		atdOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		Mockito.when(aelItemSolicitacaoExameDAO.obterOriginal(Mockito.any(AelItemSolicitacaoExamesId.class)))
				.thenReturn(atdOriginal);

		systemUnderTest.verificarSituacaoCancelado(itemSolicitacaoExameMocked, atdOriginal);
		assertNull(itemSolicitacaoExameMocked.getComplementoMotCanc());
	}

	/**
	 * RN39 Testa se a situacao antiga e igual a AC
	 */
	@Test
	public void testAtualizarHorarioColetadoParaAgendado_SituacaoNaoColetado() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("AC");
		atdOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		Mockito.when(aelItemSolicitacaoExameDAO.obterOriginal(Mockito.any(AelItemSolicitacaoExamesId.class)))
				.thenReturn(atdOriginal);

		systemUnderTest.atualizarHorarioColetadoParaAgendado(itemSolicitacaoExameMocked);
		assertEquals(DominioSituacaoItemSolicitacaoExame.AC.toString(),
				atdOriginal.getSituacaoItemSolicitacao().getCodigo());
	}

	/**
	 * RN39 Testa se a situacao antiga e coletado e atualiza a situacao do
	 * horario do exame para marcado
	 */
	@Test
	public void testAtualizarHorarioColetadoParaAgendado_SituacaoColetado() {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacao = new AelSitItemSolicitacoes();
		sitItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AG.toString());
		itemSolicitacaoExameMocked.setSituacaoItemSolicitacao(sitItemSolicitacao);

		final AelItemSolicitacaoExames atdOriginal = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("CO");
		atdOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		Mockito.when(aelItemSolicitacaoExameDAO.obterOriginal(Mockito.any(AelItemSolicitacaoExamesId.class)))
				.thenReturn(atdOriginal);

		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		final List<AelItemHorarioAgendado> listItemHorarioAgendado = new ArrayList<AelItemHorarioAgendado>();
		listItemHorarioAgendado.add(itemHorarioAgendado);
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.E);
		itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);
		Mockito.when(
				aelItemHorarioAgendadoDAO.obterPorItemSolicitacaoExame(Mockito.any(AelItemSolicitacaoExames.class)))
				.thenReturn(listItemHorarioAgendado);

		systemUnderTest.atualizarHorarioColetadoParaAgendado(itemSolicitacaoExameMocked);
		assertEquals(DominioSituacaoHorario.M, itemHorarioAgendado.getHorarioExameDisp().getSituacaoHorario());
	}

	/**
	 * RN43 Testa se a situacao do codigo da entidade<br>
	 * AelItemSolicitacaoExames e diferente de LI e seta<br>
	 * DthrLiberada para nulo
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAtualizarHoraLiberacao_setDthrLiberada() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacao = new AelSitItemSolicitacoes();
		sitItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.AG.toString());
		itemSolicitacaoExameMocked.setSituacaoItemSolicitacao(sitItemSolicitacao);

		systemUnderTest.atualizarHoraLiberacao(itemSolicitacaoExameMocked);

		assertNull(itemSolicitacaoExameMocked.getDthrLiberada());
	}

	/**
	 * RN43 Testa se a situacao do codigo da entidade<br>
	 * AelItemSolicitacaoExames e igual a LI e seta valores para DthrLiberada e
	 * ServidorResponsabilidade
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	@Test
	public void testAtualizarHoraLiberacao_SituacaoItemSolicitacaoIgualLiberado() throws ApplicationBusinessException {
		final AelItemSolicitacaoExames itemSolicitacaoExameMocked = new AelItemSolicitacaoExames();
		final AelSitItemSolicitacoes sitItemSolicitacao = new AelSitItemSolicitacoes();
		sitItemSolicitacao.setCodigo(DominioSituacaoItemSolicitacaoExame.LI.toString());
		itemSolicitacaoExameMocked.setSituacaoItemSolicitacao(sitItemSolicitacao);
		systemUnderTest.atualizarHoraLiberacao(itemSolicitacaoExameMocked);

		assertTrue(itemSolicitacaoExameMocked.getDthrLiberada() != null);
	}

	/**
	 * Testa o lancamento da excecao AEL_01987
	 * 
	 */
	@Test
	public void verificarTempoAposLiberacaoExcecao01987UnidadeTempoIgualHorasTest()
			throws ApplicationBusinessException {

		final AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();

		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.valueOf(1));
		parametro.setVlrTexto("H");

		AelSitItemSolicitacoes situacaoItemSolicitacaoOriginal = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoOriginal.setCodigo("LI");

		AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("AC");

		AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		itemSolicitacaoExames.setDthrLiberada(DateUtil.adicionaDias(new Date(), -5));

		AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacaoOriginal);
		itemSolicitacaoExamesOriginal.setDthrLiberada(DateUtil.adicionaDias(new Date(), -5));

		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class),
				Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class)))
				.thenReturn(unfExecutaExames);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		Mockito.when(aghuFacade.obterListaFeriadosEntreDatas(Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<AghFeriados>());

		try {

			systemUnderTest.verificarTempoAposLiberacao(itemSolicitacaoExames, itemSolicitacaoExamesOriginal);
			fail("Deveria ter ocorrido uma exceção de código AEL_01987.");

		} catch (BaseException e) {

			assertTrue(ItemSolicitacaoExameRNExceptionCode.AEL_01987.equals(e.getCode()));

		}

	}

	/**
	 * Testa o lancamento da excecao AEL_01987
	 * 
	 */
	@Test
	public void verificarTempoAposLiberacaoExcecao01987UnidadeTempoIgualDiasTest() throws ApplicationBusinessException {

		final AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setTempoAposLiberacao(Short.valueOf("1"));
		unfExecutaExames.setUnidTempoAposLib(DominioUnidTempo.D);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.valueOf(1));
		parametro.setVlrTexto("H");

		AelSitItemSolicitacoes situacaoItemSolicitacaoOriginal = new AelSitItemSolicitacoes();
		situacaoItemSolicitacaoOriginal.setCodigo("LI");

		AelSitItemSolicitacoes situacaoItemSolicitacao = new AelSitItemSolicitacoes();
		situacaoItemSolicitacao.setCodigo("AC");

		AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
		itemSolicitacaoExames.setDthrLiberada(DateUtil.adicionaDias(new Date(), -5));

		AelItemSolicitacaoExames itemSolicitacaoExamesOriginal = new AelItemSolicitacaoExames();
		itemSolicitacaoExamesOriginal.setSituacaoItemSolicitacao(situacaoItemSolicitacaoOriginal);
		itemSolicitacaoExamesOriginal.setDthrLiberada(DateUtil.adicionaDias(new Date(), -5));

		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class),
				Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class)))
				.thenReturn(unfExecutaExames);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		Mockito.when(aghuFacade.obterListaFeriadosEntreDatas(Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<AghFeriados>());

		try {

			systemUnderTest.verificarTempoAposLiberacao(itemSolicitacaoExames, itemSolicitacaoExamesOriginal);
			fail("Deveria ter ocorrido uma exceção de código AEL_01987.");

		} catch (BaseException e) {

			assertTrue(ItemSolicitacaoExameRNExceptionCode.AEL_01987.equals(e.getCode()));

		}

	}

	/**
	 * Testa retorno de feriado.
	 * 
	 */
	@Test
	public void calcularTipoDiaManhaTest() throws ApplicationBusinessException {

		AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.M);

		Calendar dtHrProgramada = Calendar.getInstance();
		dtHrProgramada.set(2011, 3, 11, 10, 0);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.valueOf(1));
		parametro.setVlrTexto("1200");

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		DominioTipoDia atual = systemUnderTest.calcularTipoDia(feriado, dtHrProgramada.getTime());
		assertEquals(DominioTipoDia.FER, atual);

	}

	/**
	 * Testa retorno de feriado.
	 * 
	 */
	@Test
	public void calcularTipoDiaTardeTest() throws ApplicationBusinessException {

		AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.T);

		Calendar dtHrProgramada = Calendar.getInstance();
		dtHrProgramada.set(2011, 3, 11, 10, 0);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.valueOf(1));
		parametro.setVlrTexto("0900");

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		DominioTipoDia atual = systemUnderTest.calcularTipoDia(feriado, dtHrProgramada.getTime());
		assertEquals(DominioTipoDia.FER, atual);

	}

	/**
	 * Testa retorno de feriado.
	 * 
	 */
	@Test
	public void calcularTipoDiaNoiteTest() throws ApplicationBusinessException {

		AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.N);

		Calendar dtHrProgramada = Calendar.getInstance();
		dtHrProgramada.set(2011, 3, 11, 10, 0);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.valueOf(1));
		parametro.setVlrTexto("0900");

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		DominioTipoDia atual = systemUnderTest.calcularTipoDia(feriado, dtHrProgramada.getTime());
		assertEquals(DominioTipoDia.FER, atual);

	}

	/**
	 * Testa retorno de feriado.
	 * 
	 */
	@Test
	public void calcularTipoDiaNenhumTest() throws ApplicationBusinessException {

		AghFeriados feriado = new AghFeriados();

		Calendar dtHrProgramada = Calendar.getInstance();
		dtHrProgramada.set(2011, 3, 11, 10, 0);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.valueOf(1));
		parametro.setVlrTexto("12:00");

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(parametro);

		DominioTipoDia atual = systemUnderTest.calcularTipoDia(feriado, dtHrProgramada.getTime());
		assertEquals(DominioTipoDia.FER, atual);

	}

	@Test
	public void testCalcularDiasUteisEntreDatasSemFeriados() {
		Date d1 = DateUtil.obterData(2011, 4, 30);// new Date();
		Date d2 = DateUtils.addDays(d1, 2);

		List<AghFeriados> feriados = new LinkedList<AghFeriados>();
		Mockito.when(aghuFacade.obterListaFeriadosEntreDatas(Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(feriados);

		Integer dias = systemUnderTest.calcularDiasUteisEntreDatas(d1, d2);

		Assert.assertTrue(dias.intValue() == 2);
	}

	@Test
	public void testCalcularDiasUteisEntreDatasComFeriados() {
		final Date d1 = DateUtil.obterData(2011, 4, 30);
		Date d2 = DateUtils.addDays(d1, 4);

		List<AghFeriados> feriados = new LinkedList<AghFeriados>();
		feriados.add(new AghFeriados(DateUtils.addDays(d1, 2), DominioTurno.T));
		Mockito.when(aghuFacade.obterListaFeriadosEntreDatas(Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(feriados);

		Integer dias = systemUnderTest.calcularDiasUteisEntreDatas(d1, d2);

		Assert.assertTrue(dias.intValue() == 3);
	}

	@Test
	public void testCalcularDiasUteisEntreDatasComFeriadosEComFimDeSemana() {
		final Date d1 = DateUtil.obterData(2011, 4, 30);
		Date d2 = DateUtils.addDays(d1, 6);

		List<AghFeriados> feriados = new LinkedList<AghFeriados>();
		feriados.add(new AghFeriados(DateUtils.addDays(d1, 2), DominioTurno.T));
		Mockito.when(aghuFacade.obterListaFeriadosEntreDatas(Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(feriados);

		Integer dias = systemUnderTest.calcularDiasUteisEntreDatas(d1, d2);

		Assert.assertTrue(dias.intValue() == 3);
	}

	@Test
	public void testCalcularDiasUteisEntreDatas12diasCom3FeriadosE2Sabado1DomingoNaoSobrepondo() {
		final Date d1 = DateUtil.obterData(2011, 4, 30);
		Date d2 = DateUtils.addDays(d1, 12);

		List<AghFeriados> feriados = new LinkedList<AghFeriados>();
		feriados.add(new AghFeriados(DateUtils.addDays(d1, 2), DominioTurno.T));
		feriados.add(new AghFeriados(DateUtils.addDays(d1, 3), DominioTurno.T));
		feriados.add(new AghFeriados(DateUtils.addDays(d1, 4), DominioTurno.T));
		Mockito.when(aghuFacade.obterListaFeriadosEntreDatas(Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(feriados);

		Integer dias = systemUnderTest.calcularDiasUteisEntreDatas(d1, d2);

		Assert.assertTrue(dias.intValue() == 6);
	}

	@Test
	public void testCalcularDiasUteisEntreDatas12diasCom2FeriadosE2Sabados1DomingoSobrepondo() {
		final Date d1 = DateUtil.obterData(2011, 4, 30);
		Date d2 = DateUtils.addDays(d1, 12);

		List<AghFeriados> feriados = new LinkedList<AghFeriados>();
		feriados.add(new AghFeriados(DateUtils.addDays(d1, 4), DominioTurno.T));
		feriados.add(new AghFeriados(DateUtils.addDays(d1, 5), DominioTurno.T)); // Feriado
																					// e
																					// sabado
																					// sobreposicao.
		Mockito.when(aghuFacade.obterListaFeriadosEntreDatas(Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(feriados);

		Integer dias = systemUnderTest.calcularDiasUteisEntreDatas(d1, d2);

		Assert.assertTrue(dias.intValue() == 8);
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