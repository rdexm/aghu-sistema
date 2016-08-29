package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;

@Ignore
public class ViaAdministracaoMedicamentoRNTest extends AGHUBaseUnitTest<ViaAdministracaoMedicamentoRN> {

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private IAghuFacade mockedAghuFac = null;
	@Mock
	private AfaViaAdministracaoMedicamento viaAdm;
	@Mock
	private AfaViaAdministracaoMedicamento viaAdmOld;
	@Mock
	private AfaViaAdministracao viaAdministracao;
	@Mock
	private BaseJournalFactory mockedBaseJournalFactory;
	
	private ViaAdministracaoMedicamentoRNWrapper viaRN = new ViaAdministracaoMedicamentoRNWrapper();

	public class ViaAdministracaoMedicamentoRNWrapper extends ViaAdministracaoMedicamentoRN {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 2889218246003176120L;

		@Override  
		public IAghuFacade getAghuFacade() {
			return ViaAdministracaoMedicamentoRNTest.this.mockedAghuFac;
		}
		
	}
	
//	@Before
//	public void setUp() throws Exception {
//		this.context = new Mockery();
//		this.context.setImposteriser(ClassImposteriser.INSTANCE);
//		this.mockedAghuFac = this.context.mock(IAghuFacade.class);
//		this.mockedBaseJournalFactory = this.context.mock(BaseJournalFactory.class);
//		
//		this.atribuirValoresIniciais();
//	}
	
	private void atribuirValoresIniciais() {
		viaAdm = new AfaViaAdministracaoMedicamento();
		viaAdmOld = new AfaViaAdministracaoMedicamento();
		viaAdministracao = new AfaViaAdministracao();
		viaAdministracao.setSigla("VO");
		
		AfaMedicamento medicamento = new AfaMedicamento();
		medicamento.setMatCodigo(12750);
		AfaViaAdministracaoMedicamentoId viaAdmId = new AfaViaAdministracaoMedicamentoId();
		viaAdmId.setMedMatCodigo(medicamento.getMatCodigo());
		viaAdmId.setVadSigla(viaAdministracao.getSigla());
		
		viaAdm.setMedicamento(medicamento);
		viaAdm.setId(viaAdmId);
		viaAdmOld.setMedicamento(medicamento);
		viaAdmOld.setId(viaAdmId);
		viaAdm.setViaAdministracao(viaAdministracao);
		viaAdmOld.setViaAdministracao(viaAdministracao);
	}

//	@After
//	public void tearDown() throws Exception {
//		this.context = null;
//	}
	
	@Test
	public void testBriPreInsercaoRow() {
		boolean retorno = true;
		
		//Insercao de Via de adm com valores default
		try {
			viaAdm.setDefaultBi(false);
			viaAdm.setPermiteBi(false);
			viaAdm.setSituacao(DominioSituacao.A);
			retorno = viaRN.briPreInsercaoRow(viaAdm, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(retorno);
			
		} catch (BaseException e) {
			fail("Não deve disparar exceção.");
		}
		
		//Insercao de Via de adm com situacao Inativa
		try {
			viaAdm.setSituacao(DominioSituacao.I);
			retorno = viaRN.briPreInsercaoRow(viaAdm, NOME_MICROCOMPUTADOR, new Date());
			fail("Deve disparar exceção, pois a via precisa estar Ativa para ser inserida.");
			
		} catch (BaseException e) {
			Assert.assertTrue(FarmaciaExceptionCode.AFA_00185.toString().equals(e.getMessage()));
		}
		
		//Insercao de Via de adm com Permite BI = true, mas Via associada nao permite
		try {
			viaAdm.setSituacao(DominioSituacao.A);
			viaAdm.setPermiteBi(true);
			viaAdministracao.setIndPermiteBi(false);
			retorno = viaRN.briPreInsercaoRow(viaAdm, NOME_MICROCOMPUTADOR, new Date());
			fail("Deve disparar exceção, pois a via associada nao permite BI.");
			
		} catch (BaseException e) {
			Assert.assertTrue(FarmaciaExceptionCode.AFA_01446.toString().equals(e.getMessage()));
		}
		
		//Insercao de Via de adm com Permite BI = true, e Via associada permite
		try {
			viaAdm.setSituacao(DominioSituacao.A);
			viaAdm.setPermiteBi(true);
			viaAdministracao.setIndPermiteBi(true);
			retorno = viaRN.briPreInsercaoRow(viaAdm, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(retorno);
			
		} catch (BaseException e) {
			fail("Não deve disparar exceção, pois a via associada permite BI.");
		}
		
		//Insercao de Via de adm com Permite BI = true, e Via associada permite
		try {
			viaAdm.setSituacao(DominioSituacao.A);
			viaAdm.setPermiteBi(false);
			viaAdm.setDefaultBi(true);
			retorno = viaRN.briPreInsercaoRow(viaAdm, NOME_MICROCOMPUTADOR, new Date());
			fail("Deve disparar exceção, pois só pode ser Default BI se permitir BI.");
			
		} catch (BaseException e) {
			Assert.assertTrue(FarmaciaExceptionCode.AFA_01444.toString().equals(e.getMessage()));
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void criarJournal() {
		DominioOperacoesJournal operacao = DominioOperacoesJournal.UPD;
		Date criadoEm = new Date();
		viaAdmOld.setCriadoEm(criadoEm);
		viaAdmOld.setDefaultBi(true);
		viaAdmOld.setPermiteBi(true);
		viaAdmOld.setSituacao(DominioSituacao.I);
		
//		context.checking(new Expectations() {
//			{
//				oneOf(mockedBaseJournalFactory).getBaseJournal(
//						with(any(DominioOperacoesJournal.class)),
//						with(any(Class.class)), with(any(String.class)));
//				will(returnValue(new AfaViaAdministracaoMedicamentoJN()));
//			}
//		});
		
		AfaViaAdministracaoMedicamentoJN viaJN = viaRN.criarJournal(viaAdmOld, operacao);
		
		Assert.assertEquals(Integer.valueOf(12750), viaJN.getMedMatCodigo());
		Assert.assertEquals("VO", viaJN.getVadSigla());
//		Assert.assertEquals("usuario", viaJN.getServidor().getUsuario());
		Assert.assertEquals(criadoEm, viaJN.getCriadoEm());
		Assert.assertEquals(DominioSituacao.I, viaJN.getIndSituacao());
		Assert.assertTrue(viaJN.getPermiteBI());   
		Assert.assertTrue(viaJN.getDefaultBI());     
	}
}
