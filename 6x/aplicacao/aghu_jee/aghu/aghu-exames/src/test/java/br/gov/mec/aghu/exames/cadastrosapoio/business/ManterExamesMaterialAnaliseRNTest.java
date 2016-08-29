package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioNaturezaExame;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.dao.AelExamesMatAnaliseJnDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author fwinck
 *
 */
public class ManterExamesMaterialAnaliseRNTest extends AGHUBaseUnitTest<ManterExamesMaterialAnaliseRN>{

	@Mock
	private AelExamesMaterialAnaliseDAO mockedAelMaterialAnaliseDAO;
	@Mock
	private AelExamesMatAnaliseJnDAO mockedAelMaterialAnaliseJnDAO;
	@Mock
	private AelIntervaloColetaDAO mockedAelIntervaloColetaDAO;
	@Mock
	private AelResultadosPadraoDAO mockedAelResultadosPadraoDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	private AelExamesMaterialAnalise aelExameMaterialAnalise;
	private AelExamesMaterialAnalise aelExameMaterialAnaliseOld;
	
	@Before
	public void doBeforeEachTestCase() throws BaseException{
		
		aelExameMaterialAnalise = new AelExamesMaterialAnalise();
		aelExameMaterialAnaliseOld = new AelExamesMaterialAnalise();
		
		whenObterServidorLogado();

	}
	
	/**
	 * Método que pré-popula os objetos de teste com valores iguais.
	 */
	private void popularMaterialTeste() {
		//Exame
		AelExames exame = new AelExames();
		exame.setSigla("TESTE");
		aelExameMaterialAnalise.setAelExames(exame);
		
		AelMateriaisAnalises materiais = new AelMateriaisAnalises();
		materiais.setIndSituacao(DominioSituacao.I);
		materiais.setIndColetavel(true);
		materiais.setIndExigeDescMatAnls(true);

		
		aelExameMaterialAnalise.setCriadoEm(new Date());
		aelExameMaterialAnalise.setAelMateriaisAnalises(materiais);
		aelExameMaterialAnalise.setId(new AelExamesMaterialAnaliseId("MATEST", 1));
		aelExameMaterialAnalise.setIndCci(true);
		aelExameMaterialAnalise.setIndComedi(true);
		aelExameMaterialAnalise.setIndDependente(true);
		aelExameMaterialAnalise.setIndDietaDiferenciada(true);
		aelExameMaterialAnalise.setIndExigeRegiaoAnatomica(true);
		aelExameMaterialAnalise.setIndFormaRespiracao(true);
		aelExameMaterialAnalise.setIndGeraItemPorColetas(true);
		aelExameMaterialAnalise.setIndImpTicketPaciente(true);
		aelExameMaterialAnalise.setIndJejum(true);
		aelExameMaterialAnalise.setIndLimitaSolic(true);
		aelExameMaterialAnalise.setIndNpo(true);
		aelExameMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.S);
		aelExameMaterialAnalise.setIndPertenceContador(true);
		aelExameMaterialAnalise.setIndPreparo(true);
		aelExameMaterialAnalise.setIndSituacao(DominioSituacao.I);
		aelExameMaterialAnalise.setIndSolicInformaColetas(true);
		aelExameMaterialAnalise.setIndSolSistema(true);
		aelExameMaterialAnalise.setIndTipoTelaLiberaResu(true);
		aelExameMaterialAnalise.setIndUsaIntervaloCadastrado(true);
		aelExameMaterialAnalise.setIndVerificaMedicacao(true);
		aelExameMaterialAnalise.setAelUnfExecutaExames(new HashSet<AelUnfExecutaExames>());
		aelExameMaterialAnalise.setUnidTempoLimiteSol(DominioUnidTempo.D);
		aelExameMaterialAnalise.setUnidTempoColetaAmostras(DominioUnidTempo.H);
		aelExameMaterialAnalise.setUnidTempoLimitePeriodo(DominioUnidTempo.D);
		aelExameMaterialAnalise.setNatureza(DominioNaturezaExame.EXAME_COMPLEMENTAR);
		aelExameMaterialAnalise.setPertenceSumario(DominioSumarioExame.B);
		aelExameMaterialAnalise.setTempoDiaAmostraDefault(Byte.valueOf("1"));
		
		
//		RapPessoasFisicas pf = new RapPessoasFisicas();
//		pf.setNome("teste um");
//		
//		RapServidores servidor = new RapServidores();
//		servidor.setPessoaFisica(pf);
//		
//		aelExameMaterialAnalise.setServidor(servidor);
//		aelExameMaterialAnalise.setServidorAlterado(servidorAlterado);
		
		
		/*Old*/
		aelExameMaterialAnaliseOld.setAelExames(exame);
		aelExameMaterialAnaliseOld.setCriadoEm(new Date());
		aelExameMaterialAnaliseOld.setId(new AelExamesMaterialAnaliseId("MATEST", 1));
		aelExameMaterialAnaliseOld.setIndCci(false);
		aelExameMaterialAnaliseOld.setIndComedi(false);
		aelExameMaterialAnaliseOld.setIndDependente(false);
		aelExameMaterialAnaliseOld.setIndDietaDiferenciada(false);
		aelExameMaterialAnaliseOld.setIndExigeRegiaoAnatomica(false);
		aelExameMaterialAnaliseOld.setIndFormaRespiracao(false);
		aelExameMaterialAnaliseOld.setIndGeraItemPorColetas(false);
		aelExameMaterialAnaliseOld.setIndImpTicketPaciente(false);
		aelExameMaterialAnaliseOld.setIndJejum(false);
		aelExameMaterialAnaliseOld.setIndLimitaSolic(false);
		aelExameMaterialAnaliseOld.setIndNpo(false);
		aelExameMaterialAnaliseOld.setIndPermiteSolicAlta(DominioSimNao.N);
		aelExameMaterialAnaliseOld.setIndPertenceContador(false);
		aelExameMaterialAnaliseOld.setIndPreparo(false);
		aelExameMaterialAnaliseOld.setIndSituacao(DominioSituacao.A);
		aelExameMaterialAnaliseOld.setIndSolicInformaColetas(false);
		aelExameMaterialAnaliseOld.setIndSolSistema(false);
		aelExameMaterialAnaliseOld.setIndTipoTelaLiberaResu(false);
		aelExameMaterialAnaliseOld.setIndUsaIntervaloCadastrado(false);
		aelExameMaterialAnaliseOld.setIndVerificaMedicacao(false);
		aelExameMaterialAnaliseOld.setUnidTempoLimiteSol(DominioUnidTempo.H);
		aelExameMaterialAnaliseOld.setUnidTempoColetaAmostras(DominioUnidTempo.D);
		aelExameMaterialAnaliseOld.setUnidTempoLimitePeriodo(DominioUnidTempo.H);
		aelExameMaterialAnaliseOld.setNatureza(DominioNaturezaExame.EXAME_COMPLEMENTAR);
		aelExameMaterialAnaliseOld.setPertenceSumario(DominioSumarioExame.B);
		aelExameMaterialAnaliseOld.setTempoDiaAmostraDefault(Byte.valueOf("1"));
	}


	@Test
	public void verificarMaterialAnaliseTest(){

		try {
			popularMaterialTeste();
			systemUnderTest.verificarMaterialAnalise(this.aelExameMaterialAnalise);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			assertEquals("AEL_00362", e.getMessage());
		}
	}

	@Test
	public void verificarMaterialColetavelTeste(){
		try {
			popularMaterialTeste();
			systemUnderTest.verificarMaterialColetavel(this.aelExameMaterialAnalise);
			
		} catch (BaseException e) {
			//AEL_00363, AEL_00364
			assertEquals(e.getCode(), e.getMessage());
		}
	}
	
	@Test
	public void verificarDependentesTest(){
		try {
			popularMaterialTeste();
			systemUnderTest.verificarDependentes(this.aelExameMaterialAnalise);
			
		} catch (BaseException e) {
			assertEquals("AEL_01096", e.getMessage());
		}
	}
	
	
	@Test
	public void verificarIntervaloCadastradoTest(){

		try {
			popularMaterialTeste();
			systemUnderTest.verificarIntervaloCadastrado(this.aelExameMaterialAnalise);
			
		} catch (BaseException e) {
			assertEquals("AEL_00432", e.getMessage());
		}
	}
	
	
	@Test
	public void validaDesativacaoMaterialExame(){

		try {
			popularMaterialTeste();
			systemUnderTest.validarDesativacaoMaterialExame(this.aelExameMaterialAnalise);
		} catch (BaseException e) {
			assertEquals("AEL_00360", e.getMessage());
		}
	}
	
	
	public void verificarMaterialColetavel(){

		try {
			popularMaterialTeste();
			systemUnderTest.verificarMaterialColetavel(this.aelExameMaterialAnalise);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			//AEL_00363, AEL_00364
			assertEquals(e.getCode(), e.getMessage());
		}
	}
	
	@Test
	public void verificarDependentes(){
		try {
			popularMaterialTeste();
			systemUnderTest.verificarDependentes(this.aelExameMaterialAnalise);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			assertEquals("AEL_01096", e.getMessage());
		}
	}
	
	@Test
	public void verificarTornaNaoDependente(){
		try {
			popularMaterialTeste();
			systemUnderTest.verificarTornaNaoDependente(this.aelExameMaterialAnalise);
			//fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			assertEquals("AEL_01214", e.getMessage());
		}
	}


	@Test
	public void validaDependencias(){
		try {
			popularMaterialTeste();
			
			Mockito.when(mockedAelResultadosPadraoDAO.verificarOcorrenciaPadraoCampoPorExameMaterial(Mockito.anyString(), Mockito.anyInt()))
			.thenReturn(false);

			systemUnderTest.validarDependencias(this.aelExameMaterialAnalise);
			//fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			//AEL_00363, AEL_00364
			//assertEquals(e.getCode(), e.getMessage());
			fail("Ocorreu erro de foreign Key");
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