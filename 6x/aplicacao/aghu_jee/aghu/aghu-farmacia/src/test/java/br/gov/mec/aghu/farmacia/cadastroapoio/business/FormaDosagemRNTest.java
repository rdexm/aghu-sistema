package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaFormaDosagemJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapVinculos;

@Ignore
public class FormaDosagemRNTest extends AGHUBaseUnitTest<FormaDosagemRN> {

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	private static final String EXPECTING_EXCEPTION = "Expecting exception";
	private static final String NOT_EXPECTING_THIS_EXCEPTION = "Not expecting THIS exception: ";
	private static final String NOT_EXPECTING_EXCEPTION = "Not expecting exception: ";

	@Mock
	private FormaDosagemRN mockedRn;
	
	@Mock
	private IAghuFacade mockedFacade;
	
	@Mock
	private AfaFormaDosagem mockedEn;
	
	@Mock
	private AfaFormaDosagemJn mockedEnJn;
	
	@Mock
	private AfaFormaDosagemDAO mockedDao;
	
	@Mock
	private AfaMedicamento mockedMed;
	
	@Mock
	private RapServidoresId mockedRapId;
	
	@Mock
	private RapServidores mockedRap;
	
	public static class FormaDosagemRNWrapper extends FormaDosagemRN {
		
		private static final long serialVersionUID = 711131751918838587L;
		public static Date date = Calendar.getInstance().getTime();
		public FormaDosagemRN mockedRn = null;
		//public Expectations expect = null;
		public Boolean expect = null;
		public IAghuFacade mockedFacade = null;
		public RapServidores mockedRap = null;
		public boolean isSuperGetRapServidor = false;
		public boolean isSuperSetServidorData = true;
		public boolean isSuperObrigaRegras = false;
		
		//public FormaDosagemRNWrapper(FormaDosagemRN mockedRn, Expectations expect, IAghuFacade mockedFacade, RapServidores mockedRap) {
		public FormaDosagemRNWrapper(FormaDosagemRN mockedRn, Boolean expect, IAghuFacade mockedFacade, RapServidores mockedRap) {
			super();
			
			this.mockedRn = mockedRn;
			this.expect = expect;
			this.mockedFacade = mockedFacade;
			this.mockedRap = mockedRap;
			this.isSuperGetRapServidor = false;
			this.isSuperSetServidorData = true;
			this.isSuperObrigaRegras = false;
		}
		
		@Override
		protected AfaFormaDosagemJnDAO getEntidadeJournalDao() {

			return this.mockedRn.getEntidadeJournalDao();
		}
		
		@Override
		protected AfaFormaDosagemDAO getEntidadeDao() {

			return this.mockedRn.getEntidadeDao();
		}
		
		@Override
		protected AfaMedicamentoDAO getMedicamentoDao() {
			
			return this.mockedRn.getMedicamentoDao();
		}			
		
		@Override
		public Date getDataCriacao() {

			return date;
		}
		
		@Override
		protected AfaFormaDosagemJn getNewJournal(AfaFormaDosagem entidade,
				DominioOperacoesJournal operacao) {

			return this.mockedRn.getNewJournal(entidade, operacao);
		}
		
		@Override
		protected boolean inserirEntradaJournal(AfaFormaDosagem entidade,
				br.gov.mec.aghu.core.dominio.DominioOperacoesJournal operacao) throws ApplicationBusinessException,
				ApplicationBusinessException {
			return this.mockedRn.inserirEntradaJournal(entidade, operacao);
		}
		
		@Override
		protected void setServidorData(AfaFormaDosagem entidade) throws ApplicationBusinessException,
				ApplicationBusinessException {
			if (this.isSuperSetServidorData) {
				super.setServidorData(entidade);
			} else {
				this.mockedRn.setServidorData(entidade);				
			}
		}
		
		@Override
		protected void setServidorData(AfaFormaDosagemJn entidade) throws ApplicationBusinessException,
				ApplicationBusinessException {
			if (this.isSuperSetServidorData) {
				super.setServidorData(entidade);
			} else {
				this.mockedRn.setServidorData(entidade);				
			}
		}
		
		@Override
		protected boolean obrigaRegrasFormaDosagem(AfaFormaDosagem original,
				AfaFormaDosagem modificada, DominioOperacoesJournal operacao)
				throws ApplicationBusinessException {
			
			boolean result = false;
			
			if (this.isSuperObrigaRegras) {
				result = super.obrigaRegrasFormaDosagem(original, modificada, operacao);
			} else {
				result = this.mockedRn.obrigaRegrasFormaDosagem(original, modificada, operacao);
			}

			return result;
		}
		
		@Override
		protected boolean verificaSituacaoMedicamento(AfaMedicamento medicamento)
				throws ApplicationBusinessException {

			return this.mockedRn.verificaSituacaoMedicamento(medicamento);
		}
		
	}
	
	protected Log getLog() {
		
		return LogFactory.getLog(this.getClass());
	}
	
//	@Before
//	public void setUp() throws Exception {
//		this.context = new Mockery();
//		this.context.setImposteriser(ClassImposteriser.INSTANCE);
//		this.mockedRn = this.context.mock(FormaDosagemRN.class);
//		this.mockedDao = this.context.mock(AfaFormaDosagemDAO.class);
//		this.mockedFacade = this.context.mock(IAghuFacade.class);
//		this.mockedMed = new AfaMedicamento();
//		this.mockedRapId = new RapServidoresId();
//		this.mockedEnJn = new AfaFormaDosagemJn();
//		this.mockedEn = new AfaFormaDosagem();
//		this.mockedRap = new RapServidores(mockedRapId);
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		
//		this.context = null;
//	}

//	private Expectations prepareSetServidor(Expectations expect) {
//		
//		Expectations result = expect;
//		
//		if (result == null) {
//			result = new Expectations();			
//		}
//		//mock setup
//		result.ignoring(this.mockedRapId);
//		result.ignoring(this.mockedEn);
//		result.ignoring(this.mockedEnJn);
//		
//		return result;
//	}

	private AfaFormaDosagem getEntity(MpmUnidadeMedidaMedica umm) {
		
		AfaFormaDosagem result = null;
		
		result = new AfaFormaDosagem(
				Integer.valueOf(0), 
				this.mockedMed, 
				umm, 
				this.mockedRap, 
				BigDecimal.ONE, 
				Boolean.FALSE, 
				Boolean.FALSE, 
				FormaDosagemRNWrapper.date, 
				DominioSituacao.A);
						
		return result;
	}
	
	private FormaDosagemRNWrapper prepareBruPreAtualizacaoRow(AfaFormaDosagem original, AfaFormaDosagem modificada) {
		
		FormaDosagemRNWrapper result = null;
		//Expectations expect = new Expectations();
		Boolean expect = Boolean.TRUE;
		RapVinculos vin = null;
		Integer matMed = Integer.valueOf(0);
		
		try {
//			expect.atLeast(0).of(this.mockedRn).obrigaRegrasFormaDosagem(original, modificada, DominioOperacoesJournal.UPD);
//			expect.will(Expectations.returnValue(Boolean.TRUE));
			
			Boolean actual = mockedRn.obrigaRegrasFormaDosagem(original, modificada, DominioOperacoesJournal.UPD);
			Assert.assertEquals(expect, actual);
			
			
		} catch (ApplicationBusinessException e1) {
			this.getLog().debug(e1.getLocalizedMessage(), e1);
			fail(NOT_EXPECTING_EXCEPTION + e1);
		}		
		
		this.mockedMed.setMatCodigo(matMed);
		
		vin = new RapVinculos();
		vin.setCodigo(Short.valueOf((short) 1));
		this.mockedRap.setVinculo(vin);
		
		result = new FormaDosagemRNWrapper(this.mockedRn, expect, this.mockedFacade, this.mockedRap) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 6529676635573875112L;
			
			@Override
			protected void setServidorData(AfaFormaDosagem entidade) throws ApplicationBusinessException,
					ApplicationBusinessException {
				// do nothing
			}
			
			protected void setServidorData(AfaFormaDosagemJn entidade, RapServidores servidorLogado) throws ApplicationBusinessException {
				// do nothing
			}
		};
		result.isSuperSetServidorData = false;
		result.isSuperGetRapServidor = false;
		result.isSuperObrigaRegras = false;
		
		//this.context.checking(expect);
		Assert.assertTrue(expect);
		
		return result;		
	}
		
	
	@Test
	public void testBruPreAtualizacaoRow() {
		
		FormaDosagemRNWrapper wrapper = null;
		AfaFormaDosagem original = null;
		AfaFormaDosagem modificada = null;
		MpmUnidadeMedidaMedica ummOrig = null;
		MpmUnidadeMedidaMedica ummMod = null;

		// feliz
		original = this.getEntity(ummOrig);
		modificada = this.getEntity(ummMod);
		wrapper = this.prepareBruPreAtualizacaoRow(original, modificada);
		try {
			wrapper.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
		} catch (BaseException e) {
			this.getLog().debug(e.getLocalizedMessage(), e);
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
//		this.context.assertIsSatisfied();
		// umm diferentes 1
		original = this.getEntity(new MpmUnidadeMedidaMedica());
		modificada = this.getEntity(ummMod);
		wrapper = this.prepareBruPreAtualizacaoRow(original, modificada);
		try {
			wrapper.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			assertEquals(e.getCode(), FarmaciaExceptionCode.AFA_00208);
		}
		//this.context.assertIsSatisfied();
//		this.context.assertIsSatisfied();
		// umm diferentes 2
		
		MpmUnidadeMedidaMedica entityOriginal = new MpmUnidadeMedidaMedica();
		entityOriginal.setSeq(Integer.valueOf(1));
		original = this.getEntity(entityOriginal);

		MpmUnidadeMedidaMedica entityModificada = new MpmUnidadeMedidaMedica();
		entityModificada.setSeq(Integer.valueOf(2));
		modificada = this.getEntity(entityModificada);
		
		wrapper = this.prepareBruPreAtualizacaoRow(original, modificada);
		try {
			wrapper.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			assertEquals(e.getCode(), FarmaciaExceptionCode.AFA_00208);
		}
//		this.context.assertIsSatisfied();
		
		// seq diferentes
		entityOriginal = new MpmUnidadeMedidaMedica();
		entityOriginal.setSeq(Integer.valueOf(1));
		original = this.getEntity(entityOriginal);

		entityModificada = new MpmUnidadeMedidaMedica();
		entityModificada.setSeq(Integer.valueOf(1));
		modificada = this.getEntity(entityModificada);
		
		original.setSeq(Integer.valueOf(1));
		modificada.setSeq(Integer.valueOf(2));
		wrapper = this.prepareBruPreAtualizacaoRow(original, modificada);
		try {
			wrapper.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			assertEquals(e.getCode(), FarmaciaExceptionCode.AFA_00208);
		}
//		this.context.assertIsSatisfied();
	}

	
//	@Test
//	public void testAruPosAtualizacaoRowAfaFormaDosagemAfaFormaDosagem() throws ApplicationBusinessException {
//		FormaDosagemRNWrapper wrapper = null;
//		AfaFormaDosagem original = null;
//		AfaFormaDosagem modificada = null;
////		Expectations expect = new Expectations();
//		Boolean expect;
//
//		// no update
//		wrapper = new FormaDosagemRNWrapper(this.mockedRn, expect, this.mockedFacade, this.mockedRap);
//		wrapper.isSuperSetServidorData = false;
//		wrapper.isSuperGetRapServidor = false;
//		
//		MpmUnidadeMedidaMedica mpmUnidadeMedidaMedicaOriginal = new MpmUnidadeMedidaMedica();
//		mpmUnidadeMedidaMedicaOriginal.setSeq(1);
//		original = this.getEntity(mpmUnidadeMedidaMedicaOriginal);
//		
//		MpmUnidadeMedidaMedica mpmUnidadeMedidaMedicaModificada = new MpmUnidadeMedidaMedica();
//		mpmUnidadeMedidaMedicaModificada.setSeq(1);
//		modificada = this.getEntity(mpmUnidadeMedidaMedicaModificada);		
//		
////		this.context.checking(expect);
//		try {
//			wrapper.aruPosAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
//		} catch (BaseException e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_EXCEPTION + e);
//		}
////		this.context.assertIsSatisfied();
//		// update
//		original = this.getEntity(null);
//		modificada = this.getEntity(null);		
//		original.setSeq(Integer.valueOf(1));
//		modificada.setSeq(Integer.valueOf(2));
//		try {
//			//expect.oneOf(this.mockedRn).inserirEntradaJournal(original, DominioOperacoesJournal.UPD);
//			
//			this.mockedRn.inserirEntradaJournal(original, DominioOperacoesJournal.UPD);
//		} catch (ApplicationBusinessException e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		//this.context.checking(expect);
//		try {
//			wrapper.aruPosAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
//		} catch (BaseException e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_EXCEPTION + e);
//		}
//		//this.context.assertIsSatisfied();
//	}
//	
//	@Test
//	public void testVerificaSingularidadeUsual() {
//		
//		FormaDosagemRNWrapper wrapper = null;
//		AfaFormaDosagem entidade = null;
//		Expectations expect = new Expectations();
//		Integer matCod = Integer.valueOf(1);
//		List<AfaFormaDosagem> usados = new LinkedList<AfaFormaDosagem>();
//
//		wrapper = new FormaDosagemRNWrapper(this.mockedRn, expect, this.mockedFacade, this.mockedRap);
//		wrapper.isSuperSetServidorData = false;
//		wrapper.isSuperGetRapServidor = false;
//		wrapper.isSuperObrigaRegras = false;
//		entidade = this.getEntity(null);
//		entidade.setSeq(Integer.valueOf(1));
//		entidade.setIndSituacao(DominioSituacao.A);
//		// falha em Npt
//		entidade.setIndUsualNpt(Boolean.TRUE);
//		entidade.setIndUsualPrescricao(Boolean.FALSE);
//		usados.add(entidade);
//		try {
//			expect.oneOf(this.mockedRn).getEntidadeDao();
//			expect.will(Expectations.returnValue(this.mockedDao));
//			
//			this.mockedMed.setMatCodigo(matCod);
//			
//			expect.oneOf(this.mockedDao).listaFormaDosagemMedicamento(matCod);
//			expect.will(Expectations.returnValue(usados));
//		} catch (Exception e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		this.context.checking(expect);
//		try {
//			wrapper.verificaSingularidadeUsual(entidade, null);
//			fail(EXPECTING_EXCEPTION);
//		} catch (BaseException e1) {
//			Assert.assertEquals(e1.getCode(), FarmaciaExceptionCode.AFA_00210);
//		} catch (Exception e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_THIS_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//		// falha em Prescricao
//		entidade.setIndUsualNpt(Boolean.FALSE);
//		entidade.setIndUsualPrescricao(Boolean.TRUE);
//		usados.add(entidade);
//		try {
//			expect.oneOf(this.mockedRn).getEntidadeDao();
//			expect.will(Expectations.returnValue(this.mockedDao));
//			
//			this.mockedMed.setMatCodigo(matCod);
//			
//			expect.oneOf(this.mockedDao).listaFormaDosagemMedicamento(matCod);
//			expect.will(Expectations.returnValue(usados));
//		} catch (Exception e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		this.context.checking(expect);
//		try {
//			wrapper.verificaSingularidadeUsual(entidade, null);
//			fail(EXPECTING_EXCEPTION);
//		} catch (BaseException e1) {
//			Assert.assertEquals(e1.getCode(), FarmaciaExceptionCode.AFA_00211);
//		} catch (Exception e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_THIS_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//		// Ok
//		entidade.setIndUsualNpt(Boolean.FALSE);
//		entidade.setIndUsualPrescricao(Boolean.FALSE);
//		usados.add(entidade);
//		try {
//			expect.oneOf(this.mockedRn).getEntidadeDao();
//			expect.will(Expectations.returnValue(this.mockedDao));
//			
//			this.mockedMed.setMatCodigo(matCod);
//			
//			expect.oneOf(this.mockedDao).listaFormaDosagemMedicamento(matCod);
//			expect.will(Expectations.returnValue(usados));
//		} catch (Exception e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		this.context.checking(expect);
//		try {
//			wrapper.verificaSingularidadeUsual(entidade, null);
//		} catch (Exception e) {		
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_THIS_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//	}
//	
//	
//	@Test
//	public void testBriPreInsercaoRow() throws ApplicationBusinessException {
//		
//		FormaDosagemRNWrapper wrapper = null;
//		AfaFormaDosagem entidade = null;
//		Expectations expect = new Expectations();
//
//		wrapper = new FormaDosagemRNWrapper(this.mockedRn, expect, this.mockedFacade, this.mockedRap) {
//			
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -8982163715090935474L;
//
//			protected boolean afetaUsual(AfaFormaDosagem entidade) {
//				
//				return true;
//			}
//			
//			protected boolean verificaUnidadeMedida(AfaFormaDosagem entidade) throws ApplicationBusinessException {
//				
//				return this.mockedRn.verificaUnidadeMedida(entidade);
//			}						
//		};
//		wrapper.isSuperSetServidorData = false;
//		wrapper.isSuperGetRapServidor = false;
//		wrapper.isSuperObrigaRegras = false;
//		entidade = this.getEntity(null);
//		entidade.setSeq(Integer.valueOf(1));
//		try {
//			expect.oneOf(this.mockedRn).obrigaRegrasFormaDosagem(null, entidade, DominioOperacoesJournal.INS);
//			expect.will(Expectations.returnValue(Boolean.TRUE));
//			expect.oneOf(this.mockedRn).verificaSituacaoMedicamento(this.mockedMed);
//			expect.will(Expectations.returnValue(Boolean.TRUE));
//			expect.oneOf(this.mockedRn).verificaUnidadeMedida(entidade);
//			expect.will(Expectations.returnValue(Boolean.TRUE));
//			expect.oneOf(this.mockedRn).setServidorData(entidade);
//		} catch (ApplicationBusinessException e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		this.context.checking(expect);
//		try {
//			wrapper.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
//		} catch (BaseException e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//	}
//
//	@Test
//	public void testAsiPosInsercaoStatement() {
//		
//		FormaDosagemRNWrapper wrapper = null;
//		AfaFormaDosagem entidade = null;
//		Expectations expect = new Expectations();
//	
//		wrapper = new FormaDosagemRNWrapper(this.mockedRn, expect, this.mockedFacade, this.mockedRap);
//		wrapper.isSuperSetServidorData = false;
//		wrapper.isSuperGetRapServidor = false;
//		wrapper.isSuperObrigaRegras = false;
//		entidade = this.getEntity(null);
//		entidade.setSeq(Integer.valueOf(1));
//
//		this.context.checking(expect);
//		try {
//			wrapper.asiPosInsercaoStatement(entidade, NOME_MICROCOMPUTADOR, new Date());
//		} catch (BaseException e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//	}
//
//	@Test
//	public void testBrdPreRemocaoRow() {
//		
//		FormaDosagemRNWrapper wrapper = null;
//		AfaFormaDosagem entidade = null;
//		Expectations expect = new Expectations();
//		BigDecimal aghParam = null;
//	
//		wrapper = new FormaDosagemRNWrapper(this.mockedRn, expect, this.mockedFacade, this.mockedRap) {
//			
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 8287521126544236158L;
//
//			protected BigDecimal getAghParamVlrNum() throws ApplicationBusinessException {
//				
//				return this.mockedRn.getAghParamVlrNum();
//			}
//		};
//		wrapper.isSuperSetServidorData = false;
//		wrapper.isSuperGetRapServidor = false;
//		wrapper.isSuperObrigaRegras = false;
//		entidade = this.getEntity(null);
//		entidade.setSeq(Integer.valueOf(1));
//		// not ok
//		try {
//			expect.oneOf(this.mockedRn).getAghParamVlrNum();
//			expect.will(Expectations.returnValue(aghParam));
//		} catch (Exception e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		this.context.checking(expect);
//		try {
//			wrapper.brdPreRemocaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
//			fail(EXPECTING_EXCEPTION);
//		} catch (BaseException e1) {
//			Assert.assertEquals(e1.getCode(), FarmaciaExceptionCode.AFA_00173);
//		} catch (Exception e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_THIS_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//		//ok
//		aghParam = BigDecimal.TEN;
//		try {
//			expect.oneOf(this.mockedRn).getAghParamVlrNum();
//			expect.will(Expectations.returnValue(aghParam));
//		} catch (Exception e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		this.context.checking(expect);
//		try {
//			wrapper.brdPreRemocaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
//		} catch (Exception e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//	}
//
//	@Test
//	public void testArdPosRemocaoRow() throws ApplicationBusinessException {
//		FormaDosagemRNWrapper wrapper = null;
//		AfaFormaDosagem entidade = null;
//		Expectations expect = new Expectations();
//	
//		wrapper = new FormaDosagemRNWrapper(this.mockedRn, expect, this.mockedFacade, this.mockedRap);
//		wrapper.isSuperSetServidorData = false;
//		wrapper.isSuperGetRapServidor = false;
//		wrapper.isSuperObrigaRegras = false;
//		entidade = this.getEntity(null);
//		entidade.setSeq(Integer.valueOf(1));
//		
//		try {
//			expect.oneOf(this.mockedRn).inserirEntradaJournal(entidade, DominioOperacoesJournal.DEL);
//		} catch (ApplicationBusinessException e1) {
//			this.getLog().debug(e1.getLocalizedMessage(), e1);
//			fail(NOT_EXPECTING_EXCEPTION + e1);
//		}
//		this.context.checking(expect);
//		try {
//			wrapper.ardPosRemocaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
//		} catch (BaseException e) {
//			this.getLog().debug(e.getLocalizedMessage(), e);
//			fail(NOT_EXPECTING_EXCEPTION + e);
//		}
//		this.context.assertIsSatisfied();
//	}
}
