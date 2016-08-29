package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.jdbc.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaAmostra;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameJnDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterTipoAmostraExameRNTest extends AGHUBaseUnitTest<ManterTipoAmostraExameRN>{

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelTipoAmostraExameDAO mockedAelTipoAmostraExameDAO;
	@Mock
	private AelTipoAmostraExameJnDAO mockedAelTipoAmostraExameJnDAO;
	@Mock
	private AelUnfExecutaExamesDAO mockedAelUnfExecutaExamesDAO;
	@Mock
	private BaseJournalFactory mockedBaseJournalFactory;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Before
	@SuppressWarnings("unchecked")
	public void doBeforeEachTestCase() throws BaseException {
		whenObterServidorLogado();
		
		Mockito.when(mockedAghuFacade.possuiCaracteristicaPorUnidadeEConstante(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(true);
	}
	
	@Test
	public void preInserirAelTipoAmostraExame() throws ApplicationBusinessException {
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		
		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoDiferenteTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(null);

		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(null);

		systemUnderTest.preInserirAelTipoAmostraExame(tipoAmostraExame);
	}
	
	@Test
	public void inserirAelTipoAmostraExame() throws ApplicationBusinessException {
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		
		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoDiferenteTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(null);

		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(null);

		systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
	}
	
	@Test
	public void preAtualizarAelTipoAmostraExame() throws ApplicationBusinessException {
		final AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		Mockito.when(mockedAelTipoAmostraExameDAO.obterPorChavePrimaria(Mockito.any(AelTipoAmostraExame.class)))
		.thenReturn(tipoAmostraExame);

		systemUnderTest.preAtualizarAelTipoAmostraExame(tipoAmostraExame);
	}
	
	@Test
	public void atualizarAelTipoAmostraExame() throws ApplicationBusinessException {
		final AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		Mockito.when(mockedAelTipoAmostraExameDAO.obterPorChavePrimaria(Mockito.any(AelTipoAmostraExame.class)))
		.thenReturn(tipoAmostraExame);

		systemUnderTest.atualizarAelTipoAmostraExame(tipoAmostraExame);
	}
	
	@Test
	public void preRemoverAelTipoAmostraExame() throws ApplicationBusinessException {
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		Mockito.when(mockedAelUnfExecutaExamesDAO.countUnfExecutaExameAtivaMaterialAnaliseColetavel(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(0l);

		Mockito.when(mockedAelTipoAmostraExameDAO.countListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(0l);

		systemUnderTest.preRemoverAelTipoAmostraExame(tipoAmostraExame);
		
	}
	
	
	@Test
	public void removerAelTipoAmostraExame() throws ApplicationBusinessException {
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		final Long i = 0l;
		Mockito.when(mockedAelUnfExecutaExamesDAO.countUnfExecutaExameAtivaMaterialAnaliseColetavel(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(i);

		Mockito.when(mockedAelTipoAmostraExameDAO.countListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(i);

		systemUnderTest.removerAelTipoAmostraExame(tipoAmostraExame);
	}

	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_rec_anti AEL_00392
	 */
	public void teste001() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.getRecipienteColeta().setIndSituacao(DominioSituacao.I);
		try {
			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00392);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00392, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00392);
		}
		
	}

	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_anticoag AEL_00402
	 */
	public void teste002() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.getRecipienteColeta().setIndAnticoag(DominioSimNao.S);
		tipoAmostraExame.setAnticoagulante(null);
		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00402);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00402, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00402);
		}
		
	}
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_anticoag AEL_00394
	 */
	public void teste003() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.getAnticoagulante().setIndSituacao(DominioSituacao.I);
		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00394);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00394, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00394);
		}
		
	}	
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_mat_anls AEL_00361
	 */
	public void teste004() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.getMaterialAnalise().setIndSituacao(DominioSituacao.I);
		
		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00361);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00361, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00361);
		}
		
	}
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_mat_anls AEL_00397
	 */
	public void teste005() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.getMaterialAnalise().setIndColetavel(Boolean.FALSE);
		
		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00397);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00397, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00397);
		}
		
	}
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_unid_fun AEL_00403
	 */
	public void teste006() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.getUnidadeFuncional().setIndSitUnidFunc(Boolean.FALSE);
		
		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00403);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00403, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00403);
		}
		
	}
	
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_unid_fun AEL_00404
	 */
	public void teste007() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 8425312958152513266L;

		};
		unidadeFuncional.setIndSitUnidFunc(Boolean.TRUE);
		tipoAmostraExame.setUnidadeFuncional(unidadeFuncional);
		
		try {

			Mockito.when(mockedAghuFacade.possuiCaracteristicaPorUnidadeEConstante(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
			.thenReturn(false);
			
			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00404);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00404, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00404);
		}
		
	}
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_unid_fun AEL_00406
	 */
	public void teste008() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.setResponsavelColeta(DominioResponsavelColetaExames.S);
		
		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00406);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00406, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00406);
		}
		
	}
	
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_nro_amos AEL_00837
	 */
	public void teste009() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.getExamesMaterialAnalise().setIndSolicInformaColetas(Boolean.FALSE);
		tipoAmostraExame.getExamesMaterialAnalise().setIndGeraItemPorColetas(Boolean.FALSE);
		tipoAmostraExame.getMaterialAnalise().setIndColetavel(Boolean.TRUE);
		tipoAmostraExame.setNroAmostras(null);
		
		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00837);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00837, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00837);
		}
		
	}
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_origem AEL_00839
	 */
	public void teste010() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.setOrigemAtendimento(DominioOrigemAtendimento.T);
		
		final List<AelTipoAmostraExame> list = new LinkedList<AelTipoAmostraExame>();
		list.add(new AelTipoAmostraExame());

		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoDiferenteTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(list);

		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(list);

		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00839);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00839, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00839);
		}
		
	}
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRI - aelk_tae_rn.rn_taep_ver_origem AEL_00840
	 */
	public void teste011() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		tipoAmostraExame.setOrigemAtendimento(DominioOrigemAtendimento.C);
		
		final List<AelTipoAmostraExame> list = new LinkedList<AelTipoAmostraExame>();
		list.add(new AelTipoAmostraExame());
		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoDiferenteTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(list);

		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorOrigemAtendimentoTodasOrigens(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(list);

		try {

			systemUnderTest.inserirAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00840);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00840, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00840);
		}
		
	}
	
	@Test
	/**
	 * TRIGGER AELT_TAE_BRU - aelk_tae_rn.rn_taep_ver_update AEL_00369
	 */
	public void teste012() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		final AelTipoAmostraExame tipoAmostraExameServidorAlterado = new AelTipoAmostraExame();
		RapServidores servidor = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.MAX_VALUE);
		rapServidoresId.setVinCodigo(Short.MAX_VALUE);
		servidor.setId(rapServidoresId);
		tipoAmostraExameServidorAlterado.setServidor(servidor);
		tipoAmostraExameServidorAlterado.setServidorAlterado(servidor);
		
		Mockito.when(mockedAelTipoAmostraExameDAO.obterPorChavePrimaria(Mockito.any(AelTipoAmostraExame.class)))
		.thenReturn(tipoAmostraExameServidorAlterado);
		
		try {

			systemUnderTest.atualizarAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00369);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00369, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00369);
		}
		
	}
	
	
	@Test
	/**
	 * TRIGGER AELT_TAE_ASD - aelk_tae_rn.rn_taep_ver_delecao AEL_00417
	 */
	public void teste013() throws ApplicationBusinessException {
		
		AelTipoAmostraExame tipoAmostraExame = getAelTipoAmostraExameDefault();
		
		final Long i = 1l;
		Mockito.when(mockedAelUnfExecutaExamesDAO.countUnfExecutaExameAtivaMaterialAnaliseColetavel(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(i);

		Mockito.when(mockedAelTipoAmostraExameDAO.countListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(i);

		try {

			systemUnderTest.removerAelTipoAmostraExame(tipoAmostraExame);
			Assert.fail("Deveria ter ocorrido uma "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00417);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00417, e.getCode(), 
					ManterTipoAmostraExameRN.ManterTipoAmostraExameRNExceptionCode.AEL_00417);
		}
		
	}

	
	/**
	 * Métodos que pré-populam os objetos de teste.
	 */
	
	private AelTipoAmostraExame getAelTipoAmostraExameDefault() {

		AelTipoAmostraExame tae = new AelTipoAmostraExame();

		AelMateriaisAnalises materiaisAnalise = new AelMateriaisAnalises();
		materiaisAnalise.setSeq(Integer.MIN_VALUE);
		materiaisAnalise.setIndSituacao(DominioSituacao.A);
		materiaisAnalise.setIndColetavel(Boolean.TRUE);
		materiaisAnalise.setIndExigeDescMatAnls(Boolean.TRUE);
		tae.setMaterialAnalise(materiaisAnalise);
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		AelExamesMaterialAnaliseId idExamesMaterialAnalise = new AelExamesMaterialAnaliseId();
		idExamesMaterialAnalise.setExaSigla("EXA");
		idExamesMaterialAnalise.setManSeq(Integer.MIN_VALUE);
		AelExames exames = new AelExames();
		exames.setSigla("EXA");

		examesMaterialAnalise.setAelExames(exames);
		examesMaterialAnalise.setId(idExamesMaterialAnalise);
		examesMaterialAnalise.setIndSituacao(DominioSituacao.A);
		examesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		examesMaterialAnalise.setAelMateriaisAnalises(materiaisAnalise);
		tae.setExamesMaterialAnalise(examesMaterialAnalise);
			
		AelRecipienteColeta recipienteColeta = new AelRecipienteColeta();
		recipienteColeta.setIndSituacao(DominioSituacao.A);
		recipienteColeta.setIndAnticoag(DominioSimNao.S);
		tae.setRecipienteColeta(recipienteColeta);
		
		AelAnticoagulante anticoagulante = new AelAnticoagulante();
		anticoagulante.setIndSituacao(DominioSituacao.A);
		tae.setAnticoagulante(anticoagulante);
		
		AghUnidadesFuncionais unidadesFuncional = new AghUnidadesFuncionais();
		unidadesFuncional.setIndSitUnidFunc(Boolean.TRUE);
		tae.setUnidadeFuncional(unidadesFuncional);
		
		tae.setOrigemAtendimento(DominioOrigemAtendimento.A);
		tae.setUnidadeMedidaAmostra(DominioUnidadeMedidaAmostra.FR);
		tae.setResponsavelColeta(DominioResponsavelColetaExames.C);
		tae.setIndCongela(DominioSimNao.S);
	
		tae.setNroAmostras(Short.MIN_VALUE);
		tae.setVolumeAmostra(Integer.MIN_VALUE);
		
		RapServidores servidor = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.MIN_VALUE);
		rapServidoresId.setVinCodigo(Short.MIN_VALUE);
		servidor.setId(rapServidoresId);
		tae.setServidor(servidor);
		tae.setServidorAlterado(servidor);
		
		return tae;
	}
	
	private RapServidores getRap(){
		return new RapServidores(new RapServidoresId(1,(short)1));		
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
