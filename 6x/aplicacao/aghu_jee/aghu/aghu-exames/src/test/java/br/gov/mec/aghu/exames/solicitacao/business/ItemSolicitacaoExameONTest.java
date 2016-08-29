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
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioDiaSemanaFeriado;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioNecessidadeInternacaoAihAssinada;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacao;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoMensagem;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExameDeptConvenioDAO;
import br.gov.mec.aghu.exames.dao.AelExamesLimitadoAtendDAO;
import br.gov.mec.aghu.exames.dao.AelExamesProvaDAO;
import br.gov.mec.aghu.exames.dao.AelExigenciaExameDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioRotinaColetasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExameDeptConvenio;
import br.gov.mec.aghu.model.AelExameDeptConvenioId;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.model.AelExamesLimitadoAtendId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.AelExamesProvaId;
import br.gov.mec.aghu.model.AelExigenciaExame;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelHorarioRotinaColetasId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTrgEncInternoId;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ItemSolicitacaoExameONTest extends AGHUBaseUnitTest<ItemSolicitacaoExameON>{
	
	@Mock
	private ItemSolicitacaoExameVO itemSolicitacaoExameVO;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private VAelSolicAtendsDAO mockedVAelSolicAtendsDAO;
	@Mock
	private AelSolicitacaoExameDAO mockedAelSolicitacaoExameDAO;
	@Mock
	private ItemSolicitacaoExameRN mockedItemSolicitacaoExameRN;
	@Mock
	private AelExamesLimitadoAtendDAO mockedExamesLimitadoAtendDAO;
	@Mock
	private ItemSolicitacaoExameEnforceRN mockedItemSolicitacaoExameEnforceRN;
	@Mock
	private AelAmostrasDAO mockedAmostrasDAO;
	@Mock
	private AelExameDeptConvenioDAO mockedExameDeptConvenioDAO;
	@Mock
	private AelExamesProvaDAO mockedExamesProvaDAO;
	@Mock
	private AelHorarioRotinaColetasDAO mockedHorarioRotinaColetasDAO;
	@Mock
	private AelPermissaoUnidSolicDAO mockedPermissaoUnidSolicDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private AelMaterialAnaliseDAO mockedAelMaterialAnaliseDAO;
	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private AelExigenciaExameDAO mockedAelExigenciaExameDAO;
	@Mock
	private AelAgrpPesquisasDAO mockedAelAgrpPesquisasDAO;
	@Mock
	private AelAgrpPesquisaXExameDAO mockedAelAgrpPesquisaXExameDAO;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private HorariosRotinaColetaFactory mockedHorariosRotinaColetaFactory;
	
	@Before
	public void before() {
		itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(new SolicitacaoExameVO());
	}
	
	@Test
	public void pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosTest() {
		
		String codigoPaciente = null;
		String nomePaciente2 = null;
		AghUnidadesFuncionais aelUnfExecutaExames = new AghUnidadesFuncionais();
		AelSolicitacaoExames aelSolicitacaoExames = null;
		AelSitItemSolicitacoes aelSitItemSolicitacoes = null;
		FatConvenioSaude fatConvenioSaude = null;
		Integer prontuario2 = null;
		
		
		List<AelItemSolicitacaoExames> itens = new ArrayList<AelItemSolicitacaoExames>();
		AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		id.setSoeSeq(1);
		id.setSeqp((short)1);
		
		item.setId(id);
		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(1);
		atendimento.setPaciente(paciente);
		
		solicitacaoExame.setAtendimento(atendimento);
		AghUnidadesFuncionais unid = new AghUnidadesFuncionais();
		unid.setSeq(Short.valueOf("1"));
		item.setUnidadeFuncional(unid);
		
		item.setSolicitacaoExame(solicitacaoExame);
		
		AelUnfExecutaExames unf = new AelUnfExecutaExames();
		AelExamesMaterialAnalise mat =  new AelExamesMaterialAnalise();
		AelExames exa = new AelExames();
		mat.setAelExames(exa);
		unf.setAelExamesMaterialAnalise(mat);
		
		item.setAelUnfExecutaExames(unf);
		
		itens.add(item);

		Mockito.when(mockedAelItemSolicitacaoExameDAO.pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(), 
				Mockito.any(AghUnidadesFuncionais.class), Mockito.any(AelSolicitacaoExames.class), Mockito.any(AelSitItemSolicitacoes.class),
				Mockito.any(FatConvenioSaude.class), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(itens);

		systemUnderTest.pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(0, 10, null, true, aelUnfExecutaExames, aelSolicitacaoExames, aelSitItemSolicitacoes, fatConvenioSaude, codigoPaciente, prontuario2, nomePaciente2);
	}
	
	/**
	 * Testa o retorno de situação de verdade.
	 */
	@Test
	public void testVerificarNecessidadeExamesJaSolicIndGeradoAutomaticoFalso() throws BaseException {
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		assertTrue(systemUnderTest.verificarNecessidadeExamesJaSolic(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Testa o retorno de situação de falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesJaSolicIndGeradoAutomaticoVerdadeiro() throws BaseException {
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		assertFalse(systemUnderTest.verificarNecessidadeExamesJaSolic(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Testa o retorno de situação de falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesJaSolicUnidUrgencia() throws BaseException {
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		assertFalse(systemUnderTest.verificarNecessidadeExamesJaSolic(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Testa o retorno de situação de falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesJaSolicUnidUrgenciaIndGeradoAutomaticoVerdadeiro() throws BaseException {
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		assertFalse(systemUnderTest.verificarNecessidadeExamesJaSolic(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Testa o retorno de situação de falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesJaSolicUnfSolicitanteNulo() throws BaseException {
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(null);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		assertFalse(systemUnderTest.verificarNecessidadeExamesJaSolic(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Não deve retornar mensagem , pois LimitaSolic = S.
	 */
	@Test
	public void testVerificarExameJaSolicitadoLimitaSolicIgualS() throws BaseException {
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		assertNull(systemUnderTest.verificarExameJaSolicitado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Não deve retornar mensagem , pois IntervaloMinTempoSolic = null.
	 */
	@Test
	public void testVerificarExameJaSolicitadoIntervaloMinTempoSolicNull() throws BaseException {
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(false);
		examesMaterialAnalise.setIntervaloMinTempoSolic(null);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		assertNull(systemUnderTest.verificarExameJaSolicitado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Não deve retornar mensagem , pois Atendimento = null.
	 */
	@Test
	public void testVerificarExameJaSolicitadoAtendimentoNull() throws BaseException {
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(false);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -6990343819743249741L;

			@Override
			public AghAtendimentos getAtendimento() {
				return null;
			}
			
		};
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(new Date());
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertNull(systemUnderTest.verificarExameJaSolicitado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Não deve retornar mensagem , pois SoeSeqMax = null.
	 */
	@Test
	public void testVerificarExameJaSolicitadoSoeSeqMaxNull() throws BaseException {
		
		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(false);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(new Date());
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedAelSolicitacaoExameDAO.buscaSolicitacaoExameSeqMax(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(null);
		
		assertNull(systemUnderTest.verificarExameJaSolicitado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Não deve retornar mensagem , pois SoeSeqMax != null.
	 */
	@Test
	public void testVerificarExameJaSolicitadoSoeSeqMaxDeveRetornarObjeto() throws BaseException {

		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(false);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(new Date());
		
		Mockito.when(mockedAelSolicitacaoExameDAO.buscaSolicitacaoExameSeqMax(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), 
				Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(1);

		Mockito.when(mockedAelSolicitacaoExameDAO.obterPeloId(Mockito.anyInt())).thenReturn(new AelSolicitacaoExames());
		
		assertTrue(systemUnderTest.verificarExameJaSolicitado(itemSolicitacaoExameVO) != null);
		
	}
	
	/**
	 * Passa atendimento nulo, deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeLimitaExamesAtendimentoNull() throws BaseException {
		
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -6918311677796230299L;

			@Override
			public AghAtendimentos getAtendimento() {
				return null;
			}
			
		};
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeLimitaExames(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem internação e gerado automatico 'S', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeLimitaExamesOrigemInternacao() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeLimitaExames(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem nascimento e gerado automatico 'S', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeLimitaExamesOrigemNascimento() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
	
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.N);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeLimitaExames(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem ambulatório e gerado automatico 'N', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeLimitaExamesOrigemAmbulatorioIndGeradoAutomaticoFalso() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeLimitaExames(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem internacao e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeLimitaExamesOrigemInternacaoIndGeradoAutomaticoFalso() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeLimitaExames(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem nascimento e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeLimitaExamesOrigemNascimentoIndGeradoAutomaticoFalso() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.N);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeLimitaExames(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Não ocorre exceção pois o grupo de convênio NÃO é SUS.
	 */
	@Test
	public void testVerificarLimitaExamesNaoSUS() throws BaseException {
					
		AinInternacao internacao = new AinInternacao();
				
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setAtendimentoSeq(Integer.valueOf(1));
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.C);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);
		
		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			
		} catch (ApplicationBusinessException e) {

			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());

		}	
		
	}
	
	/**
	 * Não ocorre exceção pois o grupo de convênio é SUS, porém
	 * item gerado automatico.
	 */
	@Test
	public void testVerificarLimitaExamesGeradoAutomaticoSim() throws BaseException {
			
		AinInternacao internacao = new AinInternacao();
				
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			
		} catch (ApplicationBusinessException e) {

			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());

		}	
		
	}
	
	/**
	 * Não ocorre exceção pois IndLimitaSolic é N.
	 */
	@Test
	public void testVerificarLimitaExamesIndLimitaSolicNao() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(false);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setAelUnfExecutaExames(unfExecutaExames);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.C);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			
		} catch (ApplicationBusinessException e) {

			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());

		}	
		
	}
	
	/**
	 * Não ocorre exceção pois DtHrProgramada é igual a DtHrLimite.
	 */
	@Test
	public void testVerificarLimitaExamesDtHrProgramadaIgualDtHrLimite() throws BaseException {
		
		final Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 6, 3, 10, 0);
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		final AelExamesLimitadoAtend examesLimitadoAtend = new AelExamesLimitadoAtend();
		examesLimitadoAtend.setDthrLimite(dthrProgramada.getTime());
		
		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class))).thenReturn(examesLimitadoAtend);
		
		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			
		} catch (ApplicationBusinessException e) {

			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());

		}	
		
	}
	
	/**
	 * Não ocorre exceção pois DtHrProgramada é menor a DtHrLimite.
	 */
	@Test
	public void testVerificarLimitaExamesDtHrProgramadaMenorDtHrLimite() throws BaseException {
		
		final Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 6, 3, 10, 0);
		
		final Calendar dthrLimite = Calendar.getInstance();
		dthrLimite.set(2011, 6, 4, 10, 0);
		
		final AelExamesLimitadoAtend examesLimitadoAtend = new AelExamesLimitadoAtend();
		examesLimitadoAtend.setDthrLimite(dthrLimite.getTime());
		
		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);
		
		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class))).thenReturn(examesLimitadoAtend);

		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			
		} catch (ApplicationBusinessException e) {

			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());

		}	
		
	}
	
	/**
	 * Não ocorre exceção pois soeSeqMax é nulo.
	 */
	@Test
	public void testVerificarLimitaExamesSoeSeqMaxNull() throws BaseException {
		
		final Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 6, 4, 10, 0);
		
		final Calendar dthrLimite = Calendar.getInstance();
		dthrLimite.set(2011, 6, 3, 10, 0);
		
		final AelExamesLimitadoAtend examesLimitadoAtend = new AelExamesLimitadoAtend();
		examesLimitadoAtend.setDthrLimite(dthrLimite.getTime());
		
		final Integer soeSeqMax = null; //Integer.valueOf(1);

		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();

		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setTempoLimiteSolic(Short.valueOf("1"));
		examesMaterialAnalise.setUnidTempoLimiteSol(DominioUnidTempo.H);
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());
		
		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class))).thenReturn(examesLimitadoAtend);

		Mockito.when(mockedItemSolicitacaoExameEnforceRN.adicionaTempoColeta(Mockito.any(Date.class), Mockito.any(DominioUnidadeMedidaTempo.class), Mockito.anyDouble()))
		.thenReturn(dthrProgramada.getTime());

		Mockito.when(mockedAelSolicitacaoExameDAO.buscaSolicitacaoExameSeqMaxSituacaoDiferenteCA(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(soeSeqMax);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			
		} catch (ApplicationBusinessException e) {

			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());

		}	
		
	}
	
	/**
	 * Testa ocorrência da exceção AEL_01549B.
	 */
	@Test
	public void testVerificarLimitaExamesExceptionAEL01549B() throws BaseException {
		
		final Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 6, 4, 10, 0);
		
		final Calendar dthrLimite = Calendar.getInstance();
		dthrLimite.set(2011, 6, 3, 10, 0);
		
		final AelExamesLimitadoAtend examesLimitadoAtend = new AelExamesLimitadoAtend();
		examesLimitadoAtend.setDthrLimite(dthrLimite.getTime());
		
		final Integer soeSeqMax = Integer.valueOf(1);
		
		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setTempoLimiteSolic(Short.valueOf("1"));
		examesMaterialAnalise.setUnidTempoLimiteSol(DominioUnidTempo.H);
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		
		UnfExecutaSinonimoExameVO unfExecutaExamesVO = new UnfExecutaSinonimoExameVO();
		unfExecutaExamesVO.setUnfExecutaExame(unfExecutaExames);
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaExamesVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class))).thenReturn(examesLimitadoAtend);

		Mockito.when(mockedItemSolicitacaoExameEnforceRN.adicionaTempoColeta(Mockito.any(Date.class), Mockito.any(DominioUnidadeMedidaTempo.class), Mockito.anyDouble()))
		.thenReturn(dthrProgramada.getTime());

		Mockito.when(mockedAelSolicitacaoExameDAO.buscaSolicitacaoExameSeqMaxSituacaoDiferenteCA(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(soeSeqMax);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			fail("Deveria ocorrer uma exceção de código AEL_01549B!");
			
		} catch (ApplicationBusinessException e) {

			assertEquals("AEL_01549B", e.getMessage());

		}	
		
	}
	
	/**
	 * Testa ocorrência da exceção AEL_01550B.
	 */
	@Test
	public void testVerificarLimitaExamesExceptionAEL01550B() throws BaseException {
		
		final Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 6, 4, 10, 0);
		
		final Calendar dthrLimite = Calendar.getInstance();
		dthrLimite.set(2011, 6, 3, 10, 0);
		
		final AelExamesLimitadoAtend examesLimitadoAtend = new AelExamesLimitadoAtend();
		examesLimitadoAtend.setDthrLimite(dthrLimite.getTime());
		
		final Integer soeSeqMax = Integer.valueOf(1);
		
		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setTempoLimiteSolic(null);
		examesMaterialAnalise.setUnidTempoLimiteSol(DominioUnidTempo.H);
		examesMaterialAnalise.setNroAmostrasSolic(Short.valueOf("1"));
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);

		AinInternacao internacao = new AinInternacao();
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidadeFuncional);
		
		UnfExecutaSinonimoExameVO unfExecutaExamesVO = new UnfExecutaSinonimoExameVO();
		unfExecutaExamesVO.setUnfExecutaExame(unfExecutaExames);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaExamesVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());
		itemSolicitacaoExameVO.setNumeroAmostra(5);
		
		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class))).thenReturn(examesLimitadoAtend);

		Mockito.when(mockedItemSolicitacaoExameEnforceRN.adicionaTempoColeta(Mockito.any(Date.class), Mockito.any(DominioUnidadeMedidaTempo.class), Mockito.anyDouble()))
		.thenReturn(dthrProgramada.getTime());

		Mockito.when(mockedAelSolicitacaoExameDAO.buscaSolicitacaoExameSeqMaxSituacaoDiferenteCA(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(soeSeqMax);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			fail("Deveria ocorrer uma exceção de código AEL_01550B!");
			
		} catch (ApplicationBusinessException e) {

			assertEquals("AEL_01550B", e.getMessage());

		}	
		
	}
	
	/**
	 * Testa de NÃO ocorrência da exceção AEL_01550B.
	 */
	@Test
	public void testVerificarLimitaExamesNotExceptionAEL01550B() throws BaseException {
		
		final Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 6, 4, 10, 0);
		
		final Calendar dthrLimite = Calendar.getInstance();
		dthrLimite.set(2011, 6, 3, 10, 0);
		
		final AelExamesLimitadoAtend examesLimitadoAtend = new AelExamesLimitadoAtend();
		examesLimitadoAtend.setDthrLimite(dthrLimite.getTime());
		
		final Integer soeSeqMax = Integer.valueOf(1);
		
		AelExames exame = new AelExames();
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setTempoLimiteSolic(null);
		examesMaterialAnalise.setUnidTempoLimiteSol(DominioUnidTempo.H);
		examesMaterialAnalise.setNroAmostrasSolic(Short.valueOf("5"));
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AinInternacao internacao = new AinInternacao();
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidadeFuncional);
		
		UnfExecutaSinonimoExameVO unfExecutaExamesVO = new UnfExecutaSinonimoExameVO();
		unfExecutaExamesVO.setUnfExecutaExame(unfExecutaExames);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaExamesVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());
		itemSolicitacaoExameVO.setNumeroAmostra(1);
		
		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class))).thenReturn(examesLimitadoAtend);

		Mockito.when(mockedItemSolicitacaoExameEnforceRN.adicionaTempoColeta(Mockito.any(Date.class), Mockito.any(DominioUnidadeMedidaTempo.class), Mockito.anyDouble()))
		.thenReturn(dthrProgramada.getTime());

		Mockito.when(mockedAelSolicitacaoExameDAO.buscaSolicitacaoExameSeqMaxSituacaoDiferenteCA(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(soeSeqMax);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			
		} catch (ApplicationBusinessException e) {

			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());

		}	
		
	}
	
	/**
	 * Testa ocorrência da exceção AEL_01551B.
	 */
	@Test
	public void testVerificarLimitaExamesExceptionAEL01551B() throws BaseException {
		
		final Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 6, 4, 10, 0);
		
		final Calendar dthrLimite = Calendar.getInstance();
		dthrLimite.set(2011, 6, 3, 10, 0);
		
		final AelExamesLimitadoAtend examesLimitadoAtend = new AelExamesLimitadoAtend();
		examesLimitadoAtend.setDthrLimite(dthrLimite.getTime());
		
		final Integer soeSeqMax = Integer.valueOf(1);
		
		final Integer countAmostras = Integer.valueOf(1);
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndLimitaSolic(true);
		examesMaterialAnalise.setIntervaloMinTempoSolic(Short.valueOf("1"));
		examesMaterialAnalise.setTempoLimiteSolic(null);
		examesMaterialAnalise.setUnidTempoLimiteSol(DominioUnidTempo.H);
		examesMaterialAnalise.setNroAmostrasSolic(null);
		examesMaterialAnalise.setNroAmostraTempo(Short.valueOf("1"));
		examesMaterialAnalise.setTempoLimitePeriodo(Short.valueOf("1"));
		
		AelExames exame = new AelExames();
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AinInternacao internacao = new AinInternacao();
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidadeFuncional);
		
		UnfExecutaSinonimoExameVO unfExecutaExamesVO = new UnfExecutaSinonimoExameVO();
		unfExecutaExamesVO.setUnfExecutaExame(unfExecutaExames);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		atendimento.setInternacao(internacao);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaExamesVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());
		itemSolicitacaoExameVO.setNumeroAmostra(1);
		
		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class))).thenReturn(examesLimitadoAtend);

		Mockito.when(mockedItemSolicitacaoExameEnforceRN.adicionaTempoColeta(Mockito.any(Date.class), Mockito.any(DominioUnidadeMedidaTempo.class), Mockito.anyDouble()))
		.thenReturn(dthrProgramada.getTime());

		Mockito.when(mockedAelSolicitacaoExameDAO.buscaSolicitacaoExameSeqMaxSituacaoDiferenteCA(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(soeSeqMax);

		Mockito.when(mockedAmostrasDAO.countAmostras(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(countAmostras);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			
			systemUnderTest.verificarLimitaExames(itemSolicitacaoExameVO);
			fail("Deveria ocorrer uma exceção de código AEL_01550B!");
			
		} catch (ApplicationBusinessException e) {

			assertEquals("AEL_01551B", e.getMessage());

		}	
		
	}	
	
	/**
	 * Passa atendimento nulo, deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaAtendimentoNull() throws BaseException {
		
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 8503785256705887739L;

			@Override
			public AghAtendimentos getAtendimento() {
				return null;
			}
			
		};
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem internação e gerado automatico 'S', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaOrigemInternacao() throws BaseException {

		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem nascimento e gerado automatico 'S', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaOrigemNascimento() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.N);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem urgência e gerado automatico 'S', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaOrigemUrgencia() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.U);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem ambulatório e gerado automatico 'N', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaOrigemAmbulatorioIndGeradoAutomaticoFalso() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem internacao e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaOrigemInternacaoIndGeradoAutomaticoFalso() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem nascimento e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaOrigemNascimentoIndGeradoAutomaticoFalso() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.N);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem urgência e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeExamesPosAltaOrigemUrgenciaIndGeradoAutomaticoFalso() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.U);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Deve retornar verdadeiro, pois a dthrLimite passada
	 * por parâmetro é maior que a data corrente do sistema.
	 */
	@Test
	public void testVerificarHorasUteisDthrLimiteMaiorDataCorrente() throws BaseException {
		
		Date dthrAlta = DateUtil.adicionaDias(new Date(), 1);
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.TEN);
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		assertTrue(systemUnderTest.verificarHorasUteis(dthrAlta));
		
	}
	
	/**
	 * Deve retornar falso, pois o valor que retorna do 
	 * parâmetro P_HORAS_POS_ALTA é zero.
	 */
	@Test
	public void testVerificarHorasUteisSaldoHorasIgualZero() throws BaseException {
		
		Date dthrAlta = DateUtil.adicionaDias(new Date(), -1);
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.ZERO);
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		assertFalse(systemUnderTest.verificarHorasUteis(dthrAlta));
		
	}
	
	/**
	 * Deve retornar falso.
	 */
	@Test
	public void testVerificarHorasUteisSaldoHorasMaiorZeroFinalDeSemana() throws BaseException {
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.ONE);
		
		final Calendar dthrAlta = Calendar.getInstance();
		dthrAlta.set(2011, 4, 28, 10, 0);
		
		final AghFeriados feriados = null;
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriados);

		assertFalse(systemUnderTest.verificarHorasUteis(dthrAlta.getTime()));
		
	}
	
	/**
	 * Deve retornar falso.
	 */
	@Test
	public void testVerificarHorasUteisSaldoHorasMaiorZeroSexta() throws BaseException {
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.ONE);
		
		final Calendar dthrAlta = Calendar.getInstance();
		dthrAlta.set(2011, 4, 27, 10, 0);
		
		final AghFeriados feriados = null;
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriados);

		assertFalse(systemUnderTest.verificarHorasUteis(dthrAlta.getTime()));
		
	}
	
	/**
	 * Deve retornar exceção AEL_01779.
	 */
	@Test
	public void testVerificarExamesPosAltaExceptionAEL01779() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.N);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			systemUnderTest.verificarExamesPosAlta(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01779.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01779", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve retornar exceção AEL_01780.
	 */
	@Test
	public void testVerificarExamesPosAltaExceptionAEL01780() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);	
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AghUnidadesFuncionais unidadeTrabalho = null; 
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(DateUtil.adicionaDias(new Date(), 10));

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			systemUnderTest.verificarExamesPosAlta(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01780.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01780", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve retornar exceção AEL_01780.
	 */
	@Test
	public void testVerificarExamesPosAltaExceptionComUnidadeDeTrabalhoAEL01780() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);		

		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(DateUtil.adicionaDias(new Date(), 10));

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			systemUnderTest.verificarExamesPosAlta(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01780.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01780", e.getMessage());
		}	
		
	}
	
	/**
	 * Passa atendimento nulo, deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeExameDesativadoAtendimentoNull() throws BaseException {
		
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -374497523175457307L;

			@Override
			public AghAtendimentos getAtendimento() {
				return null;
			}
			
		};
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem internação e gerado automatico 'S', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeExameDesativadoOrigemInternacao() throws BaseException {

		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);

		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(DominioSimNao.S);

		assertTrue(systemUnderTest.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem nascimento e gerado automatico 'S', deve retornar true.
	 */
	@Test
	public void testVerificarNecessidadeExameDesativadoOrigemNascimento() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.N);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(DominioSimNao.S);

		assertTrue(systemUnderTest.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem urgência e gerado automatico 'S', deve retornar falso.
	 */
	@Test
	public void testVerificarNecessidadeExameDesativadoOrigemUrgencia() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.U);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());

		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(DominioSimNao.S);

		assertFalse(systemUnderTest.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem ambulatório e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeExameDesativadoOrigemAmbulatorioIndGeradoAutomaticoFalso() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(DominioSimNao.S);

		assertTrue(systemUnderTest.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem internacao e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeExameDesativadoOrigemInternacaoIndGeradoAutomaticoFalso() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(DominioSimNao.S);

		assertTrue(systemUnderTest.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Passa origem nascimento e gerado automatico 'N', deve retornar verdadeiro.
	 */
	@Test
	public void testVerificarNecessidadeExameDesativadoOrigemNascimentoIndGeradoAutomaticoFalso() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.N);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(unfSolicitante);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(DominioSimNao.S);

		assertTrue(systemUnderTest.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_1634B.
	 */
	@Test
	public void testVerificarExameDesativadoExceptionAEL1634B() throws BaseException {
		
		final AelExamesLimitadoAtend examesLimitadoAtend = null;
			
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);
		
		AinInternacao internacao = new AinInternacao();
		internacao.setConvenioSaudePlano(fatConvenioSaudePlano);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		
		AelExames exame = new AelExames();
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setIndDesativaTemp(true);
		unfExecutaExames.setDthrReativaTemp(new Date());
		unfExecutaExames.setUnidadeFuncional(unidade);

		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(DateUtil.adicionaDias(new Date(), -10));
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		
		Mockito.when(mockedExamesLimitadoAtendDAO.obterPeloId(Mockito.any(AelExamesLimitadoAtendId.class)))
		.thenReturn(examesLimitadoAtend);

		try {
			systemUnderTest.verificarExameDesativado(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01634B.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01634B", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve retornar verdadeiro pois InformacoesClinicas é nulo. 
	 */
	@Test
	public void testVerificarNecessidadeInformacoesClinicasNull() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AinInternacao internacao = new AinInternacao();
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setInformacoesClinicas(null);
		solicitacaoExameVO.setUsaAntimicrobianos(DominioSimNao.S);
		solicitacaoExameVO.setIndObjetivoSolic(DominioOrigemSolicitacao.COMPARATIVO);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeInformacoesClinicas(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Deve retornar verdadeiro pois UsaAntimicrobianos é nulo. 
	 */
	@Test
	public void testVerificarNecessidadeInformacoesClinicasUsaAntimicrobianosNull() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AinInternacao internacao = new AinInternacao();
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setInformacoesClinicas("teste");
		solicitacaoExameVO.setUsaAntimicrobianos(null);
		solicitacaoExameVO.setIndObjetivoSolic(DominioOrigemSolicitacao.COMPARATIVO);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeInformacoesClinicas(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Deve retornar verdadeiro pois IndObjetivoSolic é nulo. 
	 */
	@Test
	public void testVerificarNecessidadeInformacoesClinicasIndObjetivoSolicNull() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AinInternacao internacao = new AinInternacao();
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setInformacoesClinicas("teste");
		solicitacaoExameVO.setUsaAntimicrobianos(DominioSimNao.S);
		solicitacaoExameVO.setIndObjetivoSolic(null);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertTrue(systemUnderTest.verificarNecessidadeInformacoesClinicas(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Deve retornar falso pois nenhum é nulo. 
	 */
	@Test
	public void testVerificarNecessidadeInformacoesClinicasNotNull() throws BaseException {
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AinInternacao internacao = new AinInternacao();
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setInformacoesClinicas("teste");
		solicitacaoExameVO.setUsaAntimicrobianos(DominioSimNao.S);
		solicitacaoExameVO.setIndObjetivoSolic(DominioOrigemSolicitacao.COMPARATIVO);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();		
		itemSolicitacaoExameVO.setIndGeradoAutomatico(false);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		assertFalse(systemUnderTest.verificarNecessidadeInformacoesClinicas(itemSolicitacaoExameVO));
		
	}
	
	/**
	 * Deve ocorrer a exceção MSG_EXAME_EXIGE_INFOCLI_ANTIMICRO_PRIMEXAME.
	 */
	@Test
	public void testVerificarInformacoesClinicasDeveApresentarAsTresObrigatoriedades() throws BaseException {
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setSeq(Short.valueOf("123"));
		
		AelExames exame = new AelExames();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(new AelMateriaisAnalises());
		
		final AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setUnfSolicitante(unidade);
		permissaoUnidSolic.setIndExigeAntimicrobianos(true);
		
		//List<AelPermissaoUnidSolic> listPermissaoUnidSolic = new ArrayList<AelPermissaoUnidSolic>();
		//listPermissaoUnidSolic.add(permissaoUnidSolic);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setId(new AelUnfExecutaExamesId(null, null, new AghUnidadesFuncionais()));
		unfExecutaExames.setIndExigeInfoClin(true);
		//unfExecutaExames.setPermissaoUnidSolics(listPermissaoUnidSolic);
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setIndDesativaTemp(true);
		unfExecutaExames.setDthrReativaTemp(new Date());
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setInformacoesClinicas(null);
		solicitacaoExameVO.setUsaAntimicrobianos(null);
		solicitacaoExameVO.setIndObjetivoSolic(null);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		Mockito.when(mockedPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(
				Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(permissaoUnidSolic);

		try {
			systemUnderTest.verificarInformacoesClinicas(itemSolicitacaoExameVO);
		} catch (ApplicationBusinessException e) {
		}	
	}
	
	/**
	 * Deve ocorrer a exceção MSG_EXAME_EXIGE_INFOCLI.
	 */
	@Test
	public void testVerificarInformacoesClinicasDeveSerPreenchida() throws BaseException {
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setSeq(Short.valueOf("123"));
		
		AelExames exame = new AelExames();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		examesMaterialAnalise.setAelExames(exame);
		
		AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setUnfSolicitante(unidade);
		permissaoUnidSolic.setIndExigeAntimicrobianos(true);
		
		List<AelPermissaoUnidSolic> listPermissaoUnidSolic = new ArrayList<AelPermissaoUnidSolic>();
		listPermissaoUnidSolic.add(permissaoUnidSolic);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setIndExigeInfoClin(true);
		unfExecutaExames.setPermissaoUnidSolics(listPermissaoUnidSolic);
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setIndDesativaTemp(true);
		unfExecutaExames.setDthrReativaTemp(new Date());
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setInformacoesClinicas(null);
		solicitacaoExameVO.setUsaAntimicrobianos(DominioSimNao.S);
		solicitacaoExameVO.setIndObjetivoSolic(DominioOrigemSolicitacao.COMPARATIVO);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		try {
			systemUnderTest.verificarInformacoesClinicas(itemSolicitacaoExameVO);
		} catch (ApplicationBusinessException e) {
			assertEquals("MSG_EXAME_EXIGE_INFOCLI", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção MSG_EXAME_EXIGE_ANTIMICRO.
	 */
	@Test
	public void testVerificarAntimicrobianosDeveSerPreenchida() throws BaseException {

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setSeq(Short.valueOf("123"));
		
		AelExames exame = new AelExames();
		exame.setSigla("AEL");
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(123);
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		final AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setUnfSolicitante(unidade);
		permissaoUnidSolic.setIndExigeAntimicrobianos(true);
		
		//List<AelPermissaoUnidSolic> listPermissaoUnidSolic = new ArrayList<AelPermissaoUnidSolic>();
		//listPermissaoUnidSolic.add(permissaoUnidSolic);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setIndExigeInfoClin(true);
		//unfExecutaExames.setPermissaoUnidSolics(listPermissaoUnidSolic);
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		unfExecutaExames.setIndDesativaTemp(true);
		unfExecutaExames.setDthrReativaTemp(new Date());
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setInformacoesClinicas("teste");
		solicitacaoExameVO.setUsaAntimicrobianos(null);
		solicitacaoExameVO.setIndObjetivoSolic(DominioOrigemSolicitacao.COMPARATIVO);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		Mockito.when(mockedPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPor(
				Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(permissaoUnidSolic);

		try {
			systemUnderTest.verificarInformacoesClinicas(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código MSG_EXAME_EXIGE_ANTIMICRO.");
		} catch (ApplicationBusinessException e) {
			assertEquals("MSG_EXAME_EXIGE_ANTIMICRO", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção MSG_EXAME_EXIGE_PRIMEXAME.
	 */
	@Test
	public void testVerificarPrimeiroExameDeveSerPreenchido() throws BaseException {

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setSeq(Short.valueOf("123"));
		
		AelExames exame = new AelExames();
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		examesMaterialAnalise.setAelExames(exame);
		
		AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setUnfSolicitante(unidade);
		permissaoUnidSolic.setIndExigeAntimicrobianos(true);
		
		List<AelPermissaoUnidSolic> listPermissaoUnidSolic = new ArrayList<AelPermissaoUnidSolic>();
		listPermissaoUnidSolic.add(permissaoUnidSolic);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setId(new AelUnfExecutaExamesId(null, null, new AghUnidadesFuncionais()));
		unfExecutaExames.setIndExigeInfoClin(true);
		unfExecutaExames.setPermissaoUnidSolics(listPermissaoUnidSolic);
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setIndDesativaTemp(true);
		unfExecutaExames.setDthrReativaTemp(new Date());
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setInformacoesClinicas("teste");
		solicitacaoExameVO.setUsaAntimicrobianos(DominioSimNao.S);
		solicitacaoExameVO.setIndObjetivoSolic(null);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(
				Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		try {
			systemUnderTest.verificarInformacoesClinicas(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código MSG_EXAME_EXIGE_PRIMEXAME.");
		} catch (ApplicationBusinessException e) {
			assertEquals("MSG_EXAME_EXIGE_PRIMEXAME", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_01704B.
	 */
	@Test
	public void testVerificarProvasDependentes() throws BaseException {
		
		FatConvenioSaudePlanoId convenioId = new FatConvenioSaudePlanoId();
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		final FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setId(convenioId);
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);
		
		AinInternacao internacao = new AinInternacao();
		internacao.setConvenioSaudePlano(fatConvenioSaudePlano);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		
		AelExames exame = new AelExames();
		exame.setSigla("XX");
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(Integer.valueOf(1));
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		List<ItemSolicitacaoExameVO> listItem = new ArrayList<ItemSolicitacaoExameVO>();
		listItem.add(itemSolicitacaoExameVO);
		solicitacaoExameVO.setItemSolicitacaoExameVos(listItem);
		
		AelExameDeptConvenioId exameDeptConvenioId = new AelExameDeptConvenioId();
		
		AelExameDeptConvenio exameDeptConvenio = new AelExameDeptConvenio();
		exameDeptConvenio.setId(exameDeptConvenioId);
		
		final List<AelExameDeptConvenio> listExameDeptConvenio = new ArrayList<AelExameDeptConvenio>();
		listExameDeptConvenio.add(exameDeptConvenio);
		
		AelExamesProvaId examesProvaId = new AelExamesProvaId();
		examesProvaId.setEmaExaSiglaEhProva("XX");
		examesProvaId.setEmaManSeqEhProva(Integer.valueOf(1));
		
		AelExamesProva examesProva = new AelExamesProva();
		examesProva.setId(examesProvaId);
		examesProva.setExamesMaterialAnaliseEhProva(new AelExamesMaterialAnalise());
		
		final List<AelExamesProva> listExamesProva = new ArrayList<AelExamesProva>();
		listExamesProva.add(examesProva);
		
		Mockito.when(mockedExameDeptConvenioDAO.obterDependentesAtivosPorExame(
				Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort(), Mockito.anyByte(), Mockito.anyBoolean())).thenReturn(listExameDeptConvenio);

		Mockito.when(mockedExamesProvaDAO.buscarProvasExameSolicitado(
				Mockito.anyString(), Mockito.anyInt())).thenReturn(listExamesProva);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			systemUnderTest.verificarProvasDependentes(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01704B.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01704B", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve retornar um objeto na lista.
	 */
	@Test
	public void testVerificarHorarioRotinaColetaTurnoNulo() throws BaseException {
		
		Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 5, 7, 12, 0);
		
		AelHorarioRotinaColetasId horarioRotinaColetasId = new AelHorarioRotinaColetasId();
		horarioRotinaColetasId.setDia(DominioDiaSemanaFeriado.getInstance("TER"));
		horarioRotinaColetasId.setHorario(dthrProgramada.getTime());
		
		AelHorarioRotinaColetas horarioRotinaColetas = new AelHorarioRotinaColetas();
		horarioRotinaColetas.setId(horarioRotinaColetasId);
		
		final AghFeriados feriado = null;
		final AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		final List<AelHorarioRotinaColetas> listHorarioRotinaColetasAtual = new ArrayList<AelHorarioRotinaColetas>();
		listHorarioRotinaColetasAtual.add(horarioRotinaColetas);
		
		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriado);

		Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeExecutora);

		Mockito.when(mockedHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasAtivas(Mockito.any(AghUnidadesFuncionais.class), Mockito.anyShort()))
		.thenReturn(listHorarioRotinaColetasAtual);

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());

		List<AelHorarioRotinaColetas> atual = systemUnderTest.verificarHorarioRotinaColeta(itemSolicitacaoExameVO, BigDecimal.TEN);

		assertFalse(atual.isEmpty());
		
	}
	
	/**
	 * Deve retornar um objeto na lista.
	 */
	@Test
	public void testVerificarHorarioRotinaColetaTurnoM() throws BaseException {
		
		Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 5, 7, 12, 0);
		
		Calendar dthrProgramadaRotina = Calendar.getInstance();
		dthrProgramadaRotina.set(2011, 5, 8, 11, 0);
		
		AelHorarioRotinaColetasId horarioRotinaColetasId = new AelHorarioRotinaColetasId();
		horarioRotinaColetasId.setDia(DominioDiaSemanaFeriado.getInstance("FERM"));
		horarioRotinaColetasId.setHorario(dthrProgramadaRotina.getTime());
		
		AelHorarioRotinaColetas horarioRotinaColetas = new AelHorarioRotinaColetas();
		horarioRotinaColetas.setId(horarioRotinaColetasId);
		
		final AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.M);
		
		final AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		final List<AelHorarioRotinaColetas> listHorarioRotinaColetasAtual = new ArrayList<AelHorarioRotinaColetas>();
		listHorarioRotinaColetasAtual.add(horarioRotinaColetas);
		
		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriado);

		Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeExecutora);

		Mockito.when(mockedHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasAtivas(Mockito.any(AghUnidadesFuncionais.class), Mockito.anyShort()))
		.thenReturn(listHorarioRotinaColetasAtual);

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());

		List<AelHorarioRotinaColetas> atual = systemUnderTest.verificarHorarioRotinaColeta(itemSolicitacaoExameVO, BigDecimal.TEN);
		assertFalse(atual.isEmpty());
		
	}
	
	/**
	 * Deve retornar um objeto na lista.
	 */
	@Test
	public void testVerificarHorarioRotinaColetaMesmoHorarioTurnoM() throws BaseException {
		
		Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 5, 7, 12, 0);
		
		Calendar dthrProgramadaRotina = Calendar.getInstance();
		dthrProgramadaRotina.set(2011, 5, 15, 12, 0);
		
		AelHorarioRotinaColetasId horarioRotinaColetasId = new AelHorarioRotinaColetasId();
		horarioRotinaColetasId.setDia(DominioDiaSemanaFeriado.getInstance("FERM"));
		horarioRotinaColetasId.setHorario(dthrProgramadaRotina.getTime());
		
		AelHorarioRotinaColetas horarioRotinaColetas = new AelHorarioRotinaColetas();
		horarioRotinaColetas.setId(horarioRotinaColetasId);
		
		final AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.M);
		
		final AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		final List<AelHorarioRotinaColetas> listHorarioRotinaColetasAtual = new ArrayList<AelHorarioRotinaColetas>();
		listHorarioRotinaColetasAtual.add(horarioRotinaColetas);
		
		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriado);

		Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeExecutora);

		Mockito.when(mockedHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasAtivas(Mockito.any(AghUnidadesFuncionais.class), Mockito.anyShort()))
		.thenReturn(listHorarioRotinaColetasAtual);

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());

		List<AelHorarioRotinaColetas> atual = systemUnderTest.verificarHorarioRotinaColeta(itemSolicitacaoExameVO, BigDecimal.TEN);

		assertFalse(atual.isEmpty());
		
	}
	
	/**
	 * Deve retornar um objeto na lista.
	 */
	@Test
	public void testVerificarHorarioRotinaColetaMesmoHorarioTurnoT() throws BaseException {
		
		Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 5, 7, 12, 0);
		
		Calendar dthrProgramadaRotina = Calendar.getInstance();
		dthrProgramadaRotina.set(2011, 5, 15, 12, 0);
		
		AelHorarioRotinaColetasId horarioRotinaColetasId = new AelHorarioRotinaColetasId();
		horarioRotinaColetasId.setDia(DominioDiaSemanaFeriado.getInstance("FERT"));
		horarioRotinaColetasId.setHorario(dthrProgramadaRotina.getTime());
		
		AelHorarioRotinaColetas horarioRotinaColetas = new AelHorarioRotinaColetas();
		horarioRotinaColetas.setId(horarioRotinaColetasId);
		
		final AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.T);
		
		final AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		final List<AelHorarioRotinaColetas> listHorarioRotinaColetasAtual = new ArrayList<AelHorarioRotinaColetas>();
		listHorarioRotinaColetasAtual.add(horarioRotinaColetas);
		
		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriado);

		Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeExecutora);

		Mockito.when(mockedHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasAtivas(Mockito.any(AghUnidadesFuncionais.class), Mockito.anyShort()))
		.thenReturn(listHorarioRotinaColetasAtual);

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());

		List<AelHorarioRotinaColetas> atual = systemUnderTest.verificarHorarioRotinaColeta(itemSolicitacaoExameVO, BigDecimal.TEN);

		assertFalse(atual.isEmpty());
		
	}
	
	/**
	 * Deve retornar um objeto na lista.
	 */
	@Test
	public void testVerificarHorarioRotinaColetaTurnoT() throws BaseException {
		
		Calendar dthrProgramada = Calendar.getInstance();
		dthrProgramada.set(2011, 5, 15, 12, 0);
		
		Calendar dthrProgramadaRotina = Calendar.getInstance();
		dthrProgramadaRotina.set(2011, 5, 5, 13, 0);
		
		AelHorarioRotinaColetasId horarioRotinaColetasId = new AelHorarioRotinaColetasId();
		horarioRotinaColetasId.setDia(DominioDiaSemanaFeriado.getInstance("FERT"));
		horarioRotinaColetasId.setHorario(dthrProgramadaRotina.getTime());
		
		AelHorarioRotinaColetas horarioRotinaColetas = new AelHorarioRotinaColetas();
		horarioRotinaColetas.setId(horarioRotinaColetasId);
		
		final AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.T);
		
		final AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		final List<AelHorarioRotinaColetas> listHorarioRotinaColetasAtual = new ArrayList<AelHorarioRotinaColetas>();
		listHorarioRotinaColetasAtual.add(horarioRotinaColetas);
		
		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriado);

		Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeExecutora);

		Mockito.when(mockedHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasAtivas(Mockito.any(AghUnidadesFuncionais.class), Mockito.anyShort()))
		.thenReturn(listHorarioRotinaColetasAtual);

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada.getTime());

		List<AelHorarioRotinaColetas> atual = systemUnderTest.verificarHorarioRotinaColeta(itemSolicitacaoExameVO, BigDecimal.TEN);

		assertFalse(atual.isEmpty());
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_01337B.
	 */
	@Test
	public void testVerificarProgRotinaException1337B() throws BaseException {
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		
		AelExames exame = new AelExames();
		exame.setSigla("XX");
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(Integer.valueOf(1));
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setTipoColeta(DominioTipoColeta.U);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		Calendar dthrProgramadaInicio = Calendar.getInstance();
		dthrProgramadaInicio.set(2011, 5, 5, 12, 0);
		Calendar dthrProgramadaFim = Calendar.getInstance();
		dthrProgramadaFim.set(2011, 5, 6, 12, 0);
		
		final List<Date> listDate = new ArrayList<Date>();
		listDate.add(dthrProgramadaInicio.getTime());
		listDate.add(dthrProgramadaFim.getTime());
		
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramadaInicio.getTime());
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.TEN);
		
		Mockito.when(mockedItemSolicitacaoExameEnforceRN.calcularRangeSolicitacao(Mockito.any(AelItemSolicitacaoExames.class))).thenReturn(listDate);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			systemUnderTest.verificarProgRotina(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01337B.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01337B", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_01337B.
	 */
	@Test
	public void testVerificarProgRotinaExceptionPermissao1337B() throws BaseException {
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		AelExames exame = new AelExames();
		exame.setSigla("XX");
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(Integer.valueOf(1));
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		AelItemSolicitacaoExames itemSol = new AelItemSolicitacaoExames();
		itemSol.setExame(new AelExames());
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setTipoColeta(DominioTipoColeta.N);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setItemSolicitacaoExame(itemSol);
		
		Calendar dthrProgramadaInicio = Calendar.getInstance();
		dthrProgramadaInicio.set(2011, 5, 5, 12, 0);
		Calendar dthrProgramadaFim = Calendar.getInstance();
		dthrProgramadaFim.set(2011, 5, 6, 12, 0);
		
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramadaInicio.getTime());
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.TEN);
		
		final List<Date> listDate = new ArrayList<Date>();
		listDate.add(dthrProgramadaInicio.getTime());
		listDate.add(dthrProgramadaFim.getTime());
		
		final AelPermissaoUnidSolic permissaoUnidSolic = null;
		
		Mockito.when(mockedItemSolicitacaoExameEnforceRN.calcularRangeSolicitacao(Mockito.any(AelItemSolicitacaoExames.class))).thenReturn(listDate);

		Mockito.when(mockedPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(Mockito.anyString(), 
				Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(permissaoUnidSolic);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			systemUnderTest.verificarProgRotina(itemSolicitacaoExameVO);
		} catch (ApplicationBusinessException e) {
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_01333B.
	 */
	@Test
	public void testVerificarProgRotinaException1333B() throws BaseException {
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		AelExames exame = new AelExames();
		exame.setSigla("XX");
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(Integer.valueOf(1));
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setTipoColeta(DominioTipoColeta.N);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		Calendar dthrProgramadaInicio = Calendar.getInstance();
		dthrProgramadaInicio.set(2011, 5, 5, 12, 0);
		Calendar dthrProgramadaFim = Calendar.getInstance();
		dthrProgramadaFim.set(2011, 5, 6, 12, 0);
		
		final List<Date> listDate = new ArrayList<Date>();
		listDate.add(dthrProgramadaInicio.getTime());
		listDate.add(dthrProgramadaFim.getTime());
		
		final AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.N);
		
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramadaInicio.getTime());
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.TEN);
		
		Mockito.when(mockedItemSolicitacaoExameEnforceRN.calcularRangeSolicitacao(Mockito.any(AelItemSolicitacaoExames.class))).thenReturn(listDate);

		Mockito.when(mockedPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(Mockito.anyString(), 
				Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(permissaoUnidSolic);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			systemUnderTest.verificarProgRotina(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01333B.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01333B", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_01332B.
	 */
	@Test
	public void testVerificarProgRotinaException1332B() throws BaseException {
		
		Date dthrProgramada = new Date();
		dthrProgramada = DateUtil.adicionaDias(dthrProgramada, 10);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		AelExames exame = new AelExames();
		exame.setSigla("XX");
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(Integer.valueOf(1));
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setTipoColeta(DominioTipoColeta.N);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada);
		
		Calendar dthrProgramadaInicio = Calendar.getInstance();
		dthrProgramadaInicio.set(2011, 5, 5, 12, 0);
		Calendar dthrProgramadaFim = Calendar.getInstance();
		dthrProgramadaFim.set(2011, 5, 6, 12, 0);
		
		AghUnidadesFuncionais unfSeqAvisa = new AghUnidadesFuncionais();
		unfSeqAvisa.setSeq(Short.valueOf("10"));
		
		final List<Date> listDate = new ArrayList<Date>();
		listDate.add(dthrProgramadaInicio.getTime());
		listDate.add(dthrProgramadaFim.getTime());
		
		final AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.R);
		permissaoUnidSolic.setUnfSeqAvisa(unfSeqAvisa);
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.TEN);
		
		Mockito.when(mockedItemSolicitacaoExameEnforceRN.calcularRangeSolicitacao(Mockito.any(AelItemSolicitacaoExames.class))).thenReturn(listDate);

		Mockito.when(mockedPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(Mockito.anyString(), 
				Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(permissaoUnidSolic);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		try {
			systemUnderTest.verificarProgRotina(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01332B.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01332B", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_01335B.
	 */
	@Test
	public void testVerificarProgRotinaException1335B() throws BaseException {
		
		Date dthrProgramada = new Date();
		dthrProgramada = DateUtil.adicionaDias(dthrProgramada, 10);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		AelExames exame = new AelExames();
		exame.setSigla("XX");
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(Integer.valueOf(1));
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setTipoColeta(DominioTipoColeta.N);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada);
		
		Calendar dthrProgramadaInicio = Calendar.getInstance();
		dthrProgramadaInicio.set(2011, 5, 5, 12, 0);
		Calendar dthrProgramadaFim = Calendar.getInstance();
		dthrProgramadaFim.set(2011, 5, 6, 12, 0);
		
		AghUnidadesFuncionais unfSeqAvisa = new AghUnidadesFuncionais();
		unfSeqAvisa.setSeq(Short.valueOf("10"));
		
		final List<Date> listDate = new ArrayList<Date>();
		listDate.add(dthrProgramadaInicio.getTime());
		listDate.add(dthrProgramadaFim.getTime());
		
		final AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.S);
		permissaoUnidSolic.setUnfSeqAvisa(unfSeqAvisa);
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.TEN);
		
		Mockito.when(mockedItemSolicitacaoExameEnforceRN.calcularRangeSolicitacao(Mockito.any(AelItemSolicitacaoExames.class))).thenReturn(listDate);

		Mockito.when(mockedPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(Mockito.anyString(), 
				Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(permissaoUnidSolic);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);
		
		try {
			systemUnderTest.verificarProgRotina(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01335B.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01335B", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve ocorrer a exceção AEL_01331B.
	 */
	@Test
	public void testVerificarProgRotinaException1331B() throws BaseException {
		
		Date dthrProgramada = new Date();
		dthrProgramada = DateUtil.adicionaDias(dthrProgramada, -10);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		
		AinInternacao internacao = new AinInternacao();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(Integer.valueOf(1));
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setInternacao(internacao);
		atendimento.setPaciente(paciente);
		atendimento.setLeito(new AinLeitos());
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais(); 
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setUnidadeFuncional(unidade);
		
		AelExames exame = new AelExames();
		exame.setSigla("XX");
		
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setSeq(Integer.valueOf(1));
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setAelExames(exame);
		examesMaterialAnalise.setAelMateriaisAnalises(material);
		
		AelUnfExecutaExames unfExecutaExames = new AelUnfExecutaExames();
		unfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);
		unfExecutaExames.setUnidadeFuncional(unidade);
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExames);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setTipoColeta(DominioTipoColeta.N);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setDataProgramada(dthrProgramada);
		
		Calendar dthrProgramadaInicio = Calendar.getInstance();
		dthrProgramadaInicio.set(2011, 5, 5, 12, 0);
		Calendar dthrProgramadaFim = Calendar.getInstance();
		dthrProgramadaFim.set(2011, 5, 6, 12, 0);
		
		AghUnidadesFuncionais unfSeqAvisa = new AghUnidadesFuncionais();
		unfSeqAvisa.setSeq(Short.valueOf("10"));
		
		final List<Date> listDate = new ArrayList<Date>();
		listDate.add(dthrProgramadaInicio.getTime());
		listDate.add(dthrProgramadaFim.getTime());
		
		final AelPermissaoUnidSolic permissaoUnidSolic = new AelPermissaoUnidSolic();
		permissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.R);
		permissaoUnidSolic.setUnfSeqAvisa(unfSeqAvisa);
		
		final AghParametros param = new AghParametros();
		param.setVlrNumerico(BigDecimal.TEN);
		
		final AghFeriados feriado = new AghFeriados();
		feriado.setTurno(DominioTurno.T);
		
		final AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		final List<AelHorarioRotinaColetas> listHorarioRotinaColetasAtual = new ArrayList<AelHorarioRotinaColetas>();
		
		Mockito.when(mockedItemSolicitacaoExameEnforceRN.calcularRangeSolicitacao(Mockito.any(AelItemSolicitacaoExames.class))).thenReturn(listDate);

		Mockito.when(mockedPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(Mockito.anyString(), 
				Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(permissaoUnidSolic);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
		fatConvenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
		fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

		Mockito.when(mockedItemSolicitacaoExameRN.obterFatConvenioSaudePlano(Mockito.any(SolicitacaoExameVO.class))).thenReturn(fatConvenioSaudePlano);

		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriado);

		Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeExecutora);

		Mockito.when(mockedHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasAtivas(Mockito.any(AghUnidadesFuncionais.class), Mockito.anyShort()))
		.thenReturn(listHorarioRotinaColetasAtual);

		try {
			systemUnderTest.verificarProgRotina(itemSolicitacaoExameVO);
			fail("Deveria ter ocorrido a exceção de código AEL_01331B.");
		} catch (ApplicationBusinessException e) {
			assertEquals("AEL_01331B", e.getMessage());
		}	
		
	}
	
	/**
	 * Deve filtrar os status das entidades retornando apenas as que possuem toda a estrutura ativa.
	 */
	@Test
	public void testFiltrarUnidadesFuncionaisExecutorasAtivas() {
		AelExames exameAtivo = new AelExames();
		exameAtivo.setIndSituacao(DominioSituacao.A);
		AelExames exameInativo = new AelExames();
		exameInativo.setIndSituacao(DominioSituacao.I);
		
		AelMateriaisAnalises materialAnaliseAtivo = new AelMateriaisAnalises();
		materialAnaliseAtivo.setIndSituacao(DominioSituacao.A);
		AelMateriaisAnalises materialAnaliseInativo = new AelMateriaisAnalises();
		materialAnaliseInativo.setIndSituacao(DominioSituacao.I);
		
		AelExamesMaterialAnalise examesMaterialAnaliseExameAtivoMaterialAtivo = new AelExamesMaterialAnalise();
		examesMaterialAnaliseExameAtivoMaterialAtivo.setIndSituacao(DominioSituacao.A);
		examesMaterialAnaliseExameAtivoMaterialAtivo.setAelExames(exameAtivo);
		examesMaterialAnaliseExameAtivoMaterialAtivo.setAelMateriaisAnalises(materialAnaliseAtivo);
		
		AelExamesMaterialAnalise examesMaterialAnaliseInativoExameAtivoMaterialAtivo = new AelExamesMaterialAnalise();
		examesMaterialAnaliseInativoExameAtivoMaterialAtivo.setIndSituacao(DominioSituacao.I);
		examesMaterialAnaliseInativoExameAtivoMaterialAtivo.setAelExames(exameAtivo);
		examesMaterialAnaliseInativoExameAtivoMaterialAtivo.setAelMateriaisAnalises(materialAnaliseAtivo);
		
		AelExamesMaterialAnalise examesMaterialAnaliseExameInativoMaterialAtivo = new AelExamesMaterialAnalise();
		examesMaterialAnaliseExameInativoMaterialAtivo.setIndSituacao(DominioSituacao.A);
		examesMaterialAnaliseExameInativoMaterialAtivo.setAelExames(exameInativo);
		examesMaterialAnaliseExameInativoMaterialAtivo.setAelMateriaisAnalises(materialAnaliseAtivo);
		
		AelExamesMaterialAnalise examesMaterialAnaliseExameAtivoMaterialInativo = new AelExamesMaterialAnalise();
		examesMaterialAnaliseExameAtivoMaterialInativo.setIndSituacao(DominioSituacao.A);
		examesMaterialAnaliseExameAtivoMaterialInativo.setAelExames(exameAtivo);
		examesMaterialAnaliseExameAtivoMaterialInativo.setAelMateriaisAnalises(materialAnaliseInativo);
		
		
		AelUnfExecutaExames unfExecutaExamesTudoAtivo = new AelUnfExecutaExames();
		unfExecutaExamesTudoAtivo.setIndSituacao(DominioSituacao.A);
		unfExecutaExamesTudoAtivo.setAelExamesMaterialAnalise(examesMaterialAnaliseExameAtivoMaterialAtivo);
		
		AelUnfExecutaExames unfExecutaExamesExamesMaterialAnaliseInativo = new AelUnfExecutaExames();
		unfExecutaExamesExamesMaterialAnaliseInativo.setIndSituacao(DominioSituacao.A);
		unfExecutaExamesExamesMaterialAnaliseInativo.setAelExamesMaterialAnalise(examesMaterialAnaliseInativoExameAtivoMaterialAtivo);
		
		AelUnfExecutaExames unfExecutaExamesExameInativo = new AelUnfExecutaExames();
		unfExecutaExamesExameInativo.setIndSituacao(DominioSituacao.A);
		unfExecutaExamesExameInativo.setAelExamesMaterialAnalise(examesMaterialAnaliseExameInativoMaterialAtivo);
		
		AelUnfExecutaExames unfExecutaExamesMaterialInativo = new AelUnfExecutaExames();
		unfExecutaExamesMaterialInativo.setIndSituacao(DominioSituacao.A);
		unfExecutaExamesMaterialInativo.setAelExamesMaterialAnalise(examesMaterialAnaliseExameAtivoMaterialInativo);
		
		AelUnfExecutaExames unfExecutaExamesInativo = new AelUnfExecutaExames();
		unfExecutaExamesInativo.setIndSituacao(DominioSituacao.I);
		unfExecutaExamesInativo.setAelExamesMaterialAnalise(examesMaterialAnaliseExameAtivoMaterialAtivo);
		
		List<AelUnfExecutaExames> unidadesFuncionaisExecutoras = new ArrayList<AelUnfExecutaExames>();
		unidadesFuncionaisExecutoras.add(unfExecutaExamesTudoAtivo);
		unidadesFuncionaisExecutoras.add(unfExecutaExamesExamesMaterialAnaliseInativo);
		unidadesFuncionaisExecutoras.add(unfExecutaExamesExameInativo);
		unidadesFuncionaisExecutoras.add(unfExecutaExamesMaterialInativo);
		unidadesFuncionaisExecutoras.add(unfExecutaExamesInativo);
		
		unidadesFuncionaisExecutoras = systemUnderTest.filtrarUnidadesFuncionaisExecutorasAtivas(unidadesFuncionaisExecutoras);
		
		assertEquals(1, unidadesFuncionaisExecutoras.size());
	}
	
	/**
	 * Deve atribuir a situação CS do item de acordo com as validações.
	 */
	@Test
	public void testBuscarSituacaoExamePendenteCS() {
		
		AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("CS");
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVOPai = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVOPai.setSituacaoCodigo(situacao);
		
		final AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setIndColetavel(true);
		
		Mockito.when(mockedAelMaterialAnaliseDAO.obterPeloId(Mockito.anyInt())).thenReturn(material);
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVODependente = new ItemSolicitacaoExameVO();
		
		systemUnderTest.buscarSituacaoExamePendente(1, itemSolicitacaoExameVOPai, itemSolicitacaoExameVODependente);
		
		assertEquals("CS", itemSolicitacaoExameVODependente.getSituacaoCodigo().getCodigo());
	}
	
	/**
	 * Deve atribuir a situação AC do item de acordo com as validações.
	 */
	@Test
	public void testBuscarSituacaoExamePendenteAC() {
		
		AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("AX");
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVOPai = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVOPai.setSituacaoCodigo(situacao);
		
		final AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setIndColetavel(true);
		
		Mockito.when(mockedAelMaterialAnaliseDAO.obterPeloId(Mockito.anyInt())).thenReturn(material);

		AelSitItemSolicitacoes situacao1 = new AelSitItemSolicitacoes();
		situacao1.setCodigo("AC");
		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString())).thenReturn(situacao1);

		ItemSolicitacaoExameVO itemSolicitacaoExameVODependente = new ItemSolicitacaoExameVO();
		
		systemUnderTest.buscarSituacaoExamePendente(1, itemSolicitacaoExameVOPai, itemSolicitacaoExameVODependente);
		
		assertEquals("AC", itemSolicitacaoExameVODependente.getSituacaoCodigo().getCodigo());
	}
	
	/**
	 * Deve atribuir a situação AE do item de acordo com as validações.
	 */
	@Test
	public void testBuscarSituacaoExamePendenteAE() {
		
		AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("AX");
		
		final AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setIndColetavel(false);
		
		Mockito.when(mockedAelMaterialAnaliseDAO.obterPeloId(Mockito.anyInt())).thenReturn(material);

		AelSitItemSolicitacoes situacao1 = new AelSitItemSolicitacoes();
		situacao1.setCodigo("AE");
		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString())).thenReturn(situacao1);

		ItemSolicitacaoExameVO itemSolicitacaoExameVODependente = new ItemSolicitacaoExameVO();
		
		systemUnderTest.buscarSituacaoExamePendente(1, null, itemSolicitacaoExameVODependente);
		
		assertEquals("AE", itemSolicitacaoExameVODependente.getSituacaoCodigo().getCodigo());
	}
	
	/**
	 * Valida alguns atributos do item dependente obrigatório criado.
	 */
	@Test
	public void testGerarItemSolicitacaoExameDependente() {
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		id.setEmaExaSigla("ABC");
		AelUnfExecutaExames unidadeFuncionalExecutora = new AelUnfExecutaExames();
		unidadeFuncionalExecutora.setId(id);
		
		AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("CS");
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVOPai = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVOPai.setSituacaoCodigo(situacao);
		
		testBuscarSituacaoExamePendenteCS();
		
		ItemSolicitacaoExameVO retorno = systemUnderTest.gerarItemSolicitacaoExameDependente(itemSolicitacaoExameVOPai, null, null, unidadeFuncionalExecutora, false);
		
		assertEquals(true, retorno.getIndGeradoAutomatico());
	}
	
	/**
	 * Valida o retorno quando não é encontrado exigências do exame; 
	 */
	@Test
	public void testVerificarNecessidadeInternacaoSemExigencias() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		

		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);
		
		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeInternacao(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.V, retorno);
	}
	
	/**
	 * Valida o retorno quando não é necessária internação.
	 */
	@Test
	public void testVerificarNecessidadeInternacaoNaoPedeInternacao() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setIndPedeInternacao(false);
		
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);

		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeInternacao(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.SV, retorno);
	}
	
	/**
	 * Valida o retorno quando não existe atendimento associado.
	 */
	@Test
	public void testVerificarNecessidadeInternacaoSemAtendimento() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setIndPedeInternacao(true);
		
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);

		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeInternacao(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.C, retorno);
	}
	
	/**
	 * Valida o retorno quando a origem é I.
	 */
	@Test
	public void testVerificarNecessidadeInternacaoOrigemI() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		solicitacaoExameVO.setAtendimento(atendimento);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setIndPedeInternacao(true);
		
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);
		
		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeInternacao(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.N, retorno);
	}
	
	/**
	 * Valida o retorno quando a origem não é I ou N.
	 */
	@Test
	public void testVerificarNecessidadeInternacaoOrigemNaoIN() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setIndPedeInternacao(true);
		
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);
		
		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeInternacao(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.S, retorno);
	}
	
	/**
	 * Valida o retorno quando não é encontrado exigências do exame; 
	 */
	@Test
	public void testVerificarNecessidadeAihAssinadaSemExigencias() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO();
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);

		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeAihAssinada(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.C, retorno);
	}
	
	/**
	 * Valida o retorno quando não é encontrada triagem associada.
	 */
	@Test
	public void testVerificarNecessidadeAihAssinadaSemTriagem() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);

		Mockito.when(mockedAmbulatorioFacade.obterTrgEncInternoPorConsulta(Mockito.any(AacConsultas.class))).thenReturn(null);
				
		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeAihAssinada(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.T, retorno);
	}
	
	/**
	 * Valida o retorno quando não o indPedeAihAssinada é falso.
	 */
	@Test
	public void testVerificarNecessidadeAihAssinadaSemPedirAihAssinada() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setIndPedeAihAssinada(false);
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);

		MamTrgEncInterno triagem = new MamTrgEncInterno();
		final List<MamTrgEncInterno> triagens = new ArrayList<MamTrgEncInterno>();
		triagens.add(triagem);

		MamTrgEncInterno obj = new MamTrgEncInterno();
		List<MamTrgEncInterno> lista = new ArrayList<MamTrgEncInterno>();
		lista.add(obj);
		
		Mockito.when(mockedAmbulatorioFacade.obterTrgEncInternoPorConsulta(Mockito.any(AacConsultas.class))).thenReturn(lista);
	
		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeAihAssinada(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.SV, retorno);
	}
	
	/**
	 * Valida o retorno quando não existe aih assinada.
	 */
	@Test
	public void testVerificarNecessidadeAihAssinadaSemAihAssinada() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setIndPedeAihAssinada(true);
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);

		MamTrgEncInternoId triagemId = new MamTrgEncInternoId();
		MamTrgEncInterno triagem = new MamTrgEncInterno();
		triagem.setId(triagemId);
		final List<MamTrgEncInterno> triagens = new ArrayList<MamTrgEncInterno>();
		triagens.add(triagem);

		MamTrgEncInternoId objId = new MamTrgEncInternoId();
		objId.setTrgSeq(15L);
		MamTrgEncInterno obj = new MamTrgEncInterno();
		obj.setId(objId);
		List<MamTrgEncInterno> lista = new ArrayList<MamTrgEncInterno>();
		lista.add(obj);
		
		Mockito.when(mockedAmbulatorioFacade.obterTrgEncInternoPorConsulta(Mockito.any(AacConsultas.class))).thenReturn(lista);
		
		Mockito.when(mockedAmbulatorioFacade.obterLaudoAihPorTrgSeq(Mockito.anyLong())).thenReturn(null);
		
		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeAihAssinada(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.S, retorno);
	}
	
	/**
	 * Valida o retorno quando existe aih assinada.
	 */
	@Test
	public void testVerificarNecessidadeAihAssinadaComAihAssinada() {
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setIndPedeAihAssinada(true);
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterAtivasPorUnfExecutaExamesUnidadeFuncional(Mockito.any(AelUnfExecutaExames.class), 
				Mockito.any(AghUnidadesFuncionais.class))).thenReturn(exigenciasExame);

		MamTrgEncInternoId triagemId = new MamTrgEncInternoId();
		MamTrgEncInterno triagem = new MamTrgEncInterno();
		triagem.setId(triagemId);
		final List<MamTrgEncInterno> triagens = new ArrayList<MamTrgEncInterno>();
		triagens.add(triagem);
		
		MamLaudoAih laudoAih = new MamLaudoAih();
		final List<MamLaudoAih> laudosAih = new ArrayList<MamLaudoAih>();
		laudosAih.add(laudoAih);

		MamTrgEncInternoId objId = new MamTrgEncInternoId();
		objId.setTrgSeq(15L);
		MamTrgEncInterno obj = new MamTrgEncInterno();
		obj.setId(objId);
		List<MamTrgEncInterno> lista = new ArrayList<MamTrgEncInterno>();
		lista.add(obj);
		
		Mockito.when(mockedAmbulatorioFacade.obterTrgEncInternoPorConsulta(Mockito.any(AacConsultas.class))).thenReturn(lista);

		List<MamLaudoAih> laudosAih1 = new ArrayList<MamLaudoAih>();
		laudosAih1.add(new MamLaudoAih());
		Mockito.when(mockedAmbulatorioFacade.obterLaudoAihPorTrgSeq(Mockito.anyLong())).thenReturn(laudosAih1);

		DominioNecessidadeInternacaoAihAssinada retorno = systemUnderTest.verificarNecessidadeAihAssinada(itemSolicitacaoExameVO);
		
		assertEquals(DominioNecessidadeInternacaoAihAssinada.N, retorno);
	}
	
	/**
	 * Valida o erro gerado na validação de exigência de exame.
	 */
	@Test
	public void testVerificarExigenciaExameErroAEL03409() {
		ItemSolicitacaoExameON systemUnderTest2 = new ItemSolicitacaoExameON() {
        	/**
			 * 
			 */
			private static final long serialVersionUID = 6931747478880342080L;

			@Override
        	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeInternacao(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
        		return DominioNecessidadeInternacaoAihAssinada.S;
        	}
        	
        	@Override
        	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeAihAssinada(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
        		return DominioNecessidadeInternacaoAihAssinada.S;
        	}
        	
        	@Override
        	protected AelExigenciaExameDAO getAelExigenciaExameDAO() {
        		return mockedAelExigenciaExameDAO;
        	}
        };
		
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExame);
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setConvenioSaude(convenioSaude);
		AacConsultas consulta = new AacConsultas();
		consulta.setConvenioSaudePlano(convenioSaudePlano);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setConsulta(consulta);
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(new AghUnidadesFuncionais());
		solicitacaoExameVO.setIsSus(false);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setTipoMensagem(DominioTipoMensagem.R);
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterPorUnfExecutaExames(Mockito.any(AelUnfExecutaExames.class))).thenReturn(exigenciasExame);
		
		try {
			systemUnderTest2.verificarExigenciaExame(itemSolicitacaoExameVO);
		} catch (BaseException e) {
		}
	}
	
	/**
	 * Valida o erro gerado na validação de exigência de exame.
	 */
	@Test
	public void testVerificarExigenciaExameErroAEL03408() {
		ItemSolicitacaoExameON systemUnderTest2 = new ItemSolicitacaoExameON() {
        	/**
			 * 
			 */
			private static final long serialVersionUID = 42996375773441235L;

			@Override
        	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeInternacao(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
        		return DominioNecessidadeInternacaoAihAssinada.S;
        	}
        	
        	@Override
        	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeAihAssinada(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
        		return DominioNecessidadeInternacaoAihAssinada.N;
        	}
        	
        	@Override
        	protected AelExigenciaExameDAO getAelExigenciaExameDAO() {
        		return mockedAelExigenciaExameDAO;
        	}
        };
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExame);
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setConvenioSaude(convenioSaude);
		AacConsultas consulta = new AacConsultas();
		consulta.setConvenioSaudePlano(convenioSaudePlano);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setConsulta(consulta);
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(new AghUnidadesFuncionais());
		solicitacaoExameVO.setIsSus(false);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setTipoMensagem(DominioTipoMensagem.R);
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterPorUnfExecutaExames(Mockito.any(AelUnfExecutaExames.class))).thenReturn(exigenciasExame);

		try {
			systemUnderTest2.verificarExigenciaExame(itemSolicitacaoExameVO);
		} catch (BaseException e) {
		}
	}
	
	/**
	 * Valida o erro gerado na validação de exigência de exame.
	 */
	@Test
	public void testVerificarExigenciaExameErroAEL03407() {
		ItemSolicitacaoExameON systemUnderTest2 = new ItemSolicitacaoExameON() {
        	/**
			 * 
			 */
			private static final long serialVersionUID = 382784441450779782L;

			@Override
        	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeInternacao(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
        		return DominioNecessidadeInternacaoAihAssinada.N;
        	}
        	
        	@Override
        	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeAihAssinada(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
        		return DominioNecessidadeInternacaoAihAssinada.S;
        	}
        	
        	@Override
        	protected AelExigenciaExameDAO getAelExigenciaExameDAO() {
        		return mockedAelExigenciaExameDAO;
        	}
        };
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExame);
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(new AelAtendimentoDiversos());//necessário passar atendimento para que objeto interno seja instanciado
		FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setConvenioSaude(convenioSaude);
		AacConsultas consulta = new AacConsultas();
		consulta.setConvenioSaudePlano(convenioSaudePlano);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setConsulta(consulta);
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExameVO.setAtendimento(atendimento);
		solicitacaoExameVO.setUnidadeFuncional(new AghUnidadesFuncionais());
		solicitacaoExameVO.setIsSus(false);
		
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		AelExigenciaExame exigenciaExame = new AelExigenciaExame();
		exigenciaExame.setTipoMensagem(DominioTipoMensagem.R);
		final List<AelExigenciaExame> exigenciasExame = new ArrayList<AelExigenciaExame>();
		exigenciasExame.add(exigenciaExame);
		
		Mockito.when(mockedAelExigenciaExameDAO.obterPorUnfExecutaExames(Mockito.any(AelUnfExecutaExames.class))).thenReturn(exigenciasExame);
		
		try {
			systemUnderTest2.verificarExigenciaExame(itemSolicitacaoExameVO);
		} catch (BaseException e) {
		}
	}
	
	/*
	 * #23361 INICIO DOS TESTES
	 */
	/**
	 * Se o pagador SUS
	 * Se Origem atend é AMB
	 * Se sigla do exame diferente dos parametros exame_transoper e exame_imuno_histo
	 * Se a unidade funcional executora tem característica de “Exame SISMAMA Mamo”  
	 * Se AelAgrpPesquisaXExame com código SISMAMA existe para o exame em questão
	 * Deve retornar true
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testMostrarAbaQuestionarioSismama() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);

		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);
		
		Mockito.when(mockedExamesFacade.obterOrigemIg(Mockito.any(AelSolicitacaoExames.class))).thenReturn("AMB");

		AghParametros parametro1 = new AghParametros();
		parametro1.setVlrTexto("YYY");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER)).thenReturn(parametro1);
		
		
		AghParametros parametro2 = new AghParametros();
		parametro2.setVlrTexto("XXX");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO)).thenReturn(parametro2);

		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(true);

		AelAgrpPesquisas agrpPesquisa = new AelAgrpPesquisas();
		Mockito.when(mockedAelAgrpPesquisasDAO.obterAelAgrpPesquisasPorDescricao("SISMAMA")).thenReturn(agrpPesquisa);

		List<AelAgrpPesquisaXExame> listAgrpPesquisaXExame = new ArrayList<AelAgrpPesquisaXExame>();
		listAgrpPesquisaXExame.add(new AelAgrpPesquisaXExame());
		Mockito.when(mockedAelAgrpPesquisaXExameDAO.buscarAelAgrpPesquisaXExame(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class)
				, Mockito.any(AghUnidadesFuncionais.class), Mockito.any(AelAgrpPesquisas.class), Mockito.any(DominioSituacao.class)))
		.thenReturn(listAgrpPesquisaXExame);

		//TESTE!			
		try {
			Assert.assertTrue(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/**
	 * Se o pagador não SUS
	 * Deve retornar false
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testNaoMostrarAbaQuestionarioSismamaPagadorNaoSUS() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);
		
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.TEN);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);
		
		//TESTE!			
		try {
			Assert.assertFalse(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/**
	 * Se o pagador SUS
	 * Se Origem atend NÃO é AMB
	 * Deve retornar false
	 */
	@Test
	public void testNaoMostrarAbaQuestionarioSismamaNaoAMB() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);

		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);
		
		Mockito.when(mockedExamesFacade.obterOrigemIg(Mockito.any(AelSolicitacaoExames.class))).thenReturn("INT");
		
		//TESTE!			
		try {
			Assert.assertFalse(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/**
	 * Se o pagador SUS
	 * Se Origem atend é AMB
	 * Se sigla do exame igual ao parametros exame_transoper
	 * Deve retornar false
	 */
	@Test
	public void testNaoMostrarAbaQuestionarioSismamaExameTransoper() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);
		
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);

		Mockito.when(mockedExamesFacade.obterOrigemIg(Mockito.any(AelSolicitacaoExames.class))).thenReturn("AMB");

		AghParametros parametro1 = new AghParametros();
		parametro1.setVlrTexto("H");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER)).thenReturn(parametro1);

		
		AghParametros parametro2 = new AghParametros();
		parametro2.setVlrTexto("H");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO)).thenReturn(parametro2);

		//TESTE!			
		try {
			Assert.assertFalse(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/**
	 * Se o pagador SUS
	 * Se Origem atend é AMB
	 * Se sigla do exame igual ao parametro exame_imuno_histo
	 * Deve retornar false
	 */
	@Test
	public void testNaoMostrarAbaQuestionarioSismamaExameImunoHisto() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		exameMaterialAnalise.setAelExames(new AelExames());
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);
		
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);

		Mockito.when(mockedExamesFacade.obterOrigemIg(Mockito.any(AelSolicitacaoExames.class))).thenReturn("AMB");
		
		AghParametros parametro1 = new AghParametros();
		parametro1.setVlrTexto("YYY");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER)).thenReturn(parametro1);
		
		AghParametros parametro2 = new AghParametros();
		parametro2.setVlrTexto("H");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO)).thenReturn(parametro2);
		
		//TESTE!			
		try {
			Assert.assertFalse(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/**
	 * Se o pagador SUS
	 * Se Origem atend é AMB
	 * Se sigla do exame diferente dos parametros exame_transoper e exame_imuno_histo
	 * Se a unidade funcional executora NÃO tem característica de “Exame SISMAMA Mamo”  
	 * Deve retornar false
	 */
	@Test
	public void testNaoMostrarAbaQuestionarioSismamaSemCaract() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);
		
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);
		
		Mockito.when(mockedExamesFacade.obterOrigemIg(Mockito.any(AelSolicitacaoExames.class))).thenReturn("AMB");
		
		AghParametros parametro1 = new AghParametros();
		parametro1.setVlrTexto("YYY");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER)).thenReturn(parametro1);
		
		AghParametros parametro2 = new AghParametros();
		parametro2.setVlrTexto("XXX");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO)).thenReturn(parametro2);
		
		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(false);
		
		//TESTE!			
		try {
			Assert.assertFalse(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/**
	 * Se o pagador SUS
	 * Se Origem atend é AMB
	 * Se sigla do exame diferente dos parametros exame_transoper e exame_imuno_histo
	 * Se a unidade funcional executora tem característica de “Exame SISMAMA Mamo”  
	 * Se AelAgrpPesquisaXExame com código SISMAMA NÃO existe para o exame em questão
	 * Deve retornar false
	 */
	@Test
	public void testNaoMostrarAbaQuestionarioSismamaAgrpNaoExiste() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);
		
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);
		
		Mockito.when(mockedExamesFacade.obterOrigemIg(Mockito.any(AelSolicitacaoExames.class))).thenReturn("AMB");

		AghParametros parametro1 = new AghParametros();
		parametro1.setVlrTexto("YYY");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER)).thenReturn(parametro1);
		
		
		AghParametros parametro2 = new AghParametros();
		parametro2.setVlrTexto("XXX");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO)).thenReturn(parametro2);

		
		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(true);
		
		AelAgrpPesquisas agrpPesquisa = new AelAgrpPesquisas();
		Mockito.when(mockedAelAgrpPesquisasDAO.obterAelAgrpPesquisasPorDescricao("SISMAMA")).thenReturn(agrpPesquisa);

		List<AelAgrpPesquisaXExame> listAgrpPesquisaXExame = new ArrayList<AelAgrpPesquisaXExame>();
		Mockito.when(mockedAelAgrpPesquisaXExameDAO.buscarAelAgrpPesquisaXExame(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class)
				, Mockito.any(AghUnidadesFuncionais.class), Mockito.any(AelAgrpPesquisas.class), Mockito.any(DominioSituacao.class)))
		.thenReturn(listAgrpPesquisaXExame);

		//TESTE!			
		try {
			Assert.assertFalse(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/**
	 * Se o pagador SUS
	 * Se Origem atend é AMB
	 * Se sigla do exame diferente dos parametros exame_transoper e exame_imuno_histo
	 * Se a unidade funcional executora tem característica de “Exame SISMAMA Mamo”  
	 * Se AelAgrpPesquisaXExame com código SISMAMA NÃO existe para o exame em questão (LISTA NULA)
	 * Deve retornar false
	 */
	@Test
	public void testNaoMostrarAbaQuestionarioSismamaAgrpNaoExiste2() throws ApplicationBusinessException {
		//MOCKS
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		
		ConvenioExamesLaudosVO convenioExamesLaudosVO = new ConvenioExamesLaudosVO();
		convenioExamesLaudosVO.setCodigoConvenioSaude(new Short("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExames aelExames = new AelExames();
		aelExames.setSigla("H");
		AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		exameMaterialAnalise.setAelExames(aelExames);
		unfExecutaExame.setAelExamesMaterialAnalise(exameMaterialAnalise);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		exameMaterialAnalise.setAelMateriaisAnalises(materialAnalise);
		
		AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		unidFunc.setSeq(new Short("10"));
		id.setUnfSeq(unidFunc);
		unfExecutaExame.setId(id);
		unfExecutaExame.setUnidadeFuncional(unidFunc);
		
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)).thenReturn(parametro);

		FatConvenioSaude convenio = new FatConvenioSaude();
		AacPagador pagador = new AacPagador();
		pagador.setSeq(new Short("1"));
		convenio.setPagador(pagador);
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaude(Mockito.anyShort())).thenReturn(convenio);
		
		Mockito.when(mockedExamesFacade.obterOrigemIg(Mockito.any(AelSolicitacaoExames.class))).thenReturn("AMB");

		AghParametros parametro1 = new AghParametros();
		parametro1.setVlrTexto("YYY");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER)).thenReturn(parametro1);
		
		
		AghParametros parametro2 = new AghParametros();
		parametro2.setVlrTexto("XXX");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO)).thenReturn(parametro2);
		
		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(true);
		
		AelAgrpPesquisas agrpPesquisa = new AelAgrpPesquisas();
		Mockito.when(mockedAelAgrpPesquisasDAO.obterAelAgrpPesquisasPorDescricao("SISMAMA")).thenReturn(agrpPesquisa);

		List<AelAgrpPesquisaXExame> listAgrpPesquisaXExame = null;
		Mockito.when(mockedAelAgrpPesquisaXExameDAO.buscarAelAgrpPesquisaXExame(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class)
				, Mockito.any(AghUnidadesFuncionais.class), Mockito.any(AelAgrpPesquisas.class), Mockito.any(DominioSituacao.class)))
		.thenReturn(listAgrpPesquisaXExame);
		
		//TESTE!			
		try {
			Assert.assertFalse(systemUnderTest.mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		} catch (BaseException e) {
			fail("Erro no teste unitário " + e.getMessage());
		}
	}
	
	/*
	 * #23361 FIM DOS TESTES
	 */
}
