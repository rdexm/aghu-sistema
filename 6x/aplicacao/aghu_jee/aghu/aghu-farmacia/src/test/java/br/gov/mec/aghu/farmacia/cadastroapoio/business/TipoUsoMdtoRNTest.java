package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoJnDAO;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaTipoUsoMdtoJn;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * Classe de teste unitário para TipoUsoMdtoRN
 * 
 * @author lcmoura
 * 
 */
@Ignore
public class TipoUsoMdtoRNTest extends AGHUBaseUnitTest<TipoUsoMdtoRN> {

	private static final String NOME_PESSOA_FISICA = "PESSOA FÍSICA";
	private static final String DESCRICAO = "DESCRIÇÃO";

	private IAghuFacade mockedAghuFacade;
	private IFarmaciaFacade mockedFarmaciaFacade;
	private AfaTipoUsoMdtoDAO mockedAfaTipoUsoMdtoDAO;
	private AfaTipoUsoMdtoJnDAO mockedAfaTipoUsoMdtoJnDAO;
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade; 	

	private AfaTipoUsoMdto tipoUsoMdto;
	private AfaTipoUsoMdto tipoUsoMdtoOld;
	private AfaGrupoUsoMedicamento grupoUsoMedicamento;

//	@Before
//	public void doBeforeEachTestCase() {
////		RapPessoasFisicas pessoaFisica = new RapPessoasFisicas();
////		pessoaFisica.setNome(NOME_PESSOA_FISICA);
////		SERVIDOR_LOGADO.setPessoaFisica(pessoaFisica);
//		
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedFarmaciaFacade = mockingContext.mock(IFarmaciaFacade.class);
//		mockedAfaTipoUsoMdtoDAO = mockingContext.mock(AfaTipoUsoMdtoDAO.class);
//		mockedAfaTipoUsoMdtoJnDAO = mockingContext
//				.mock(AfaTipoUsoMdtoJnDAO.class);
//		mockedRegistroColaboradorFacade = mockingContext.mock(IRegistroColaboradorFacade.class);		
//
//		grupoUsoMedicamento = new AfaGrupoUsoMedicamento();
//		tipoUsoMdto = new AfaTipoUsoMdto();
//		tipoUsoMdtoOld = new AfaTipoUsoMdto();
//
//		grupoUsoMedicamento.setSeq(1);
//		tipoUsoMdtoOld.setSigla("A");
//		tipoUsoMdto.setSigla("A");
//
//		systemUnderTest = new TipoUsoMdtoRN() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -4734167094649751083L;
//
//			@Override
//			public IAghuFacade getAghuFacade() {
//				return mockedAghuFacade;
//			}
//
//			@Override
//			public AfaTipoUsoMdtoJnDAO getAfaTipoUsoMdtoJnDAO() {
//				return mockedAfaTipoUsoMdtoJnDAO;
//			}
//
//			@Override
//			public IFarmaciaFacade getFarmaciaFacade() {
//				return mockedFarmaciaFacade;
//			}
//
//			@Override
//			public AfaTipoUsoMdtoDAO getAfaTipoUsoMdtoDAO() {
//				return mockedAfaTipoUsoMdtoDAO;
//			}
//
//			protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
//				return mockedRegistroColaboradorFacade;
//			}
//			
//		};
//	}

	@Test
	public void preInsertTipoUsoMdtoSuccess()
			throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);

		expectServidor(1);		
		systemUnderTest.preInsertTipoUsoMdto(tipoUsoMdto);

//		Assert.assertEquals(DESCRICAO, tipoUsoMdto.getDescricao());
		Assert.assertEquals(NOME_PESSOA_FISICA, tipoUsoMdto.getRapServidores()
				.getPessoaFisica().getNome());
		Assert.assertNotNull(tipoUsoMdto.getCriadoEm());

	}

	@Test
	public void preInsertTipoUsoMdtoFailConstraint()
			throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(tipoUsoMdto));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);

		try {
			expectServidor(1);			
			systemUnderTest.preInsertTipoUsoMdto(tipoUsoMdto);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00036");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00036", e.getMessage());
		}
	}

	@Test
	public void preInsertTipoUsoMdtoFailServidor()
			throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);

		try {
			expectServidor(1);			
			systemUnderTest.preInsertTipoUsoMdto(tipoUsoMdto);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00169", e.getMessage());
		}
	}

	@Test
	public void preInsertTipoUsoMdtoFailSituacaoGrupo()
			throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.I);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);

		try {
			expectServidor(1);			
			systemUnderTest.preInsertTipoUsoMdto(tipoUsoMdto);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00170");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00170", e.getMessage());
		}
	}

	@Test
	public void preUpdateTipoUsoMdtoSuccess()
			throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);
		tipoUsoMdtoOld.setDescricao(DESCRICAO);

		expectServidor(1);		
		systemUnderTest.preUpdateTipoUsoMdto(tipoUsoMdtoOld, tipoUsoMdto);

		Assert.assertEquals(DESCRICAO, tipoUsoMdto.getDescricao());
//		Assert.assertEquals(NOME_PESSOA_FISICA, tipoUsoMdto.getRapServidores()
//				.getPessoaFisica().getNome());
	}

	@Test
	public void preUpdateTipoUsoMdtoFailSituacaoGrupo()
			throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.I);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);
		tipoUsoMdtoOld.setDescricao(DESCRICAO);

		try {
			expectServidor(1);
			systemUnderTest.preUpdateTipoUsoMdto(tipoUsoMdtoOld, tipoUsoMdto);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00170");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00170", e.getMessage());
		}
	}

	@Test
	public void preUpdateTipoUsoMdtoFailDescricao()
			throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO + " ALTERADO");

		tipoUsoMdtoOld.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdtoOld.setDescricao(DESCRICAO);

		try {
			expectServidor(1);
			systemUnderTest.preUpdateTipoUsoMdto(tipoUsoMdtoOld, tipoUsoMdto);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00171");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00171", e.getMessage());
		}
	}

	@Test
	public void preDeleteTipoUsoMdtoSuccess() throws BaseException {

//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedFarmaciaFacade).verificarDelecao(
//						with(any(Date.class)));
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		tipoUsoMdto.setCriadoEm(anteOntem.getTime());

		systemUnderTest.preDeleteTipoUsoMdto(tipoUsoMdto);

		Assert.assertEquals(tipoUsoMdto.getCriadoEm(), anteOntem.getTime());
	}

	@Test
	public void preDeleteTipoUsoMdtoFailParam() throws BaseException {

//		mockingContext.checking(new Expectations() {
//			{
//
//				oneOf(mockedFarmaciaFacade).verificarDelecao(with(any(Date.class)));
//				will(throwException(new ApplicationBusinessException(
//						FarmaciaExceptionCode.AFA_00173)));
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		tipoUsoMdto.setCriadoEm(anteOntem.getTime());

		try {
			systemUnderTest.preDeleteTipoUsoMdto(tipoUsoMdto);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00173");
		} catch (BaseException e) {
			Assert.assertEquals("AFA_00173", e.getMessage());
		}
	}

	@Test
	public void preDeleteTipoUsoMdtoFailPrazo() throws BaseException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedFarmaciaFacade).verificarDelecao(with(any(Date.class)));
//				will(throwException(new ApplicationBusinessException(
//						FarmaciaExceptionCode.AFA_00172)));
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		Calendar mesPassado = Calendar.getInstance();
		mesPassado.add(Calendar.MONTH, -1);

		tipoUsoMdto.setCriadoEm(mesPassado.getTime());

		try {
			systemUnderTest.preDeleteTipoUsoMdto(tipoUsoMdto);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00172");
		} catch (BaseException e) {
			Assert.assertEquals("AFA_00172", e.getMessage());
		}

	}

	@Test
	public void posUpdateTipoUsoMdtoSuccessSemJn() {

		final AfaTipoUsoMdtoJn tipoUsoMdtoJn = new AfaTipoUsoMdtoJn();
		// tipoUsoMdtoJn.setSeqJn(Integer.valueOf(1));
		tipoUsoMdtoJn.setOperacao(DominioOperacoesJournal.UPD);
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoJnDAO).persistir(
//						with(any(AfaTipoUsoMdtoJn.class)));
//				oneOf(mockedAfaTipoUsoMdtoJnDAO).flush();
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);

		AfaTipoUsoMdtoJn result = systemUnderTest.posUpdateTipoUsoMdto(
				tipoUsoMdto, tipoUsoMdto);
		Assert.assertNull(result);
	}

	// @Test
	// TODO: Rever teste. Não teste Regra de negócio e sim integração.
	public void posUpdateTipoUsoMdtoSuccessComJn() {

		final AfaTipoUsoMdtoJn tipoUsoMdtoJn = new AfaTipoUsoMdtoJn();
		// tipoUsoMdtoJn.setSeqJn(Integer.valueOf(1));
		tipoUsoMdtoJn.setOperacao(DominioOperacoesJournal.UPD);
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoJnDAO).persistir(
//						with(any(AfaTipoUsoMdtoJn.class)));
//				oneOf(mockedAfaTipoUsoMdtoJnDAO).flush();
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);
		tipoUsoMdto.setRapServidores(getServidor());
		tipoUsoMdtoOld.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdtoOld.setDescricao(DESCRICAO + " ALTERADO");
		tipoUsoMdtoOld.setRapServidores(getServidor());

		AfaTipoUsoMdtoJn result = systemUnderTest.posUpdateTipoUsoMdto(
				tipoUsoMdto, tipoUsoMdtoOld);
		Assert.assertEquals(tipoUsoMdtoJn, result);
	}

	// @Test
	// TODO: Rever teste. Não teste Regra de negócio e sim integração.
	public void posDeleteTipoUsoMdto() {

		final AfaTipoUsoMdtoJn tipoUsoMdtoJn = new AfaTipoUsoMdtoJn();
		// tipoUsoMdtoJn.setSeqJn(Integer.valueOf(1));
		tipoUsoMdtoJn.setOperacao(DominioOperacoesJournal.DEL);

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoJnDAO).persistir(
//						with(any(AfaTipoUsoMdtoJn.class)));
//				oneOf(mockedAfaTipoUsoMdtoJnDAO).flush();
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(String.class)));
//				will(returnValue(null));
//			}
//		});

		grupoUsoMedicamento.setIndSituacao(DominioSituacao.A);
		tipoUsoMdto.setGrupoUsoMedicamento(grupoUsoMedicamento);
		tipoUsoMdto.setDescricao(DESCRICAO);
		tipoUsoMdto.setRapServidores(getServidor());
		AfaTipoUsoMdtoJn result = systemUnderTest
				.posDeleteTipoUsoMdto(tipoUsoMdto);
		Assert.assertEquals(tipoUsoMdtoJn, result);
	}

	/**
	 * Cria uma instancia de RapServidores
	 * 
	 * @return
	 */
	private RapServidores getServidor() {
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short
				.valueOf("1"));
		servidor.setId(id);
		RapPessoasFisicas pessoaFisica = new RapPessoasFisicas();
		pessoaFisica.setNome(NOME_PESSOA_FISICA);
		servidor.setPessoaFisica(pessoaFisica);
		return servidor;
	}
	

	private void expectServidor(final Integer vinCodigo) throws ApplicationBusinessException{
//		mockingContext.checking(new Expectations() {
//			{   RapServidores rap = vinCodigo!=null?new RapServidores(new RapServidoresId(1,vinCodigo.shortValue())):null;
//				RapPessoasFisicas pf = new RapPessoasFisicas();
//				pf.setNome("PESSOA FÍSICA");
//				rap.setPessoaFisica(pf);				
//				allowing(mockedRegistroColaboradorFacade).obterServidorAtivoPorUsuario(with(any(String.class)));
//				will(returnValue(rap));
//			}
//		});				
	}	
}
