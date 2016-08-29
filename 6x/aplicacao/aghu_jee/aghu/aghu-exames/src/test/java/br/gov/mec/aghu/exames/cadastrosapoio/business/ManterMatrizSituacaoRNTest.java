package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ManterMatrizSituacaoRN.ManterMatrizSituacaoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelAutorizacaoAlteracaoSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterMatrizSituacaoRNTest extends AGHUBaseUnitTest<ManterMatrizSituacaoRN>{

	@Mock
	private AelAutorizacaoAlteracaoSituacaoDAO mockedAelAutorizacaoAlteracaoSituacaoDAO;
	@Mock
	private AelMatrizSituacaoDAO mockedAelMatrizSituacaoDAO;
	
	private AelMatrizSituacao aelMatrizSituacao;
	private AelMatrizSituacao aelMatrizSituacaoOriginal;
	
	@Before
	public void doBeforeEachTestCase() {

		aelMatrizSituacao = new AelMatrizSituacao();
		aelMatrizSituacaoOriginal = new AelMatrizSituacao();
		AelSitItemSolicitacoes situacao1 = new AelSitItemSolicitacoes();
		situacao1.setCodigo("AAA");
		situacao1.setIndSituacao(DominioSituacao.A);
		AelSitItemSolicitacoes situacao2 = new AelSitItemSolicitacoes();
		situacao2.setCodigo("BBB");
		situacao2.setIndSituacao(DominioSituacao.A);
		aelMatrizSituacao.setSituacaoItemSolicitacao(situacao1);
		aelMatrizSituacao.setSituacaoItemSolicitacaoPara(situacao2);
		aelMatrizSituacaoOriginal.setSituacaoItemSolicitacao(situacao1);
		aelMatrizSituacaoOriginal.setSituacaoItemSolicitacaoPara(situacao2);
		
	}
	
	@Test
	public void testPreInsertCodigoSituacaoInicialIgualFinal() {
		try {
			aelMatrizSituacao.setSituacaoItemSolicitacao(aelMatrizSituacao.getSituacaoItemSolicitacaoPara());
			
			systemUnderTest.preInsert(aelMatrizSituacao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção
			assertEquals(e.getCode(), ManterMatrizSituacaoRNExceptionCode.AEL_00479);
		}
	}
	
	@Test
	public void testPreInsertSituacaoInicialNaoInformada() {
		try {
			aelMatrizSituacao.setSituacaoItemSolicitacao(null);
			
			systemUnderTest.preInsert(aelMatrizSituacao);
			
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção
			//assertEquals(e.getCode(), ManterMatrizSituacaoRNExceptionCode.AEL_00480);
			fail("Exceção não gerada");
		}
	}
	
	@Test
	public void testPreInsertSituacaoFinalNaoInformada() {
		try {
			aelMatrizSituacao.setSituacaoItemSolicitacaoPara(null);
			
			systemUnderTest.preInsert(aelMatrizSituacao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção
			assertEquals(e.getCode(), ManterMatrizSituacaoRNExceptionCode.AEL_00480);
		}
	}
	
	@Test
	public void testPreInsertSituacaoInicialInativa() {
		try {
			aelMatrizSituacao.getSituacaoItemSolicitacao().setIndSituacao(DominioSituacao.I);
			
			systemUnderTest.preInsert(aelMatrizSituacao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção
			assertEquals(e.getCode(), ManterMatrizSituacaoRNExceptionCode.AEL_00481);
		}
	}
	
	@Test
	public void testPreInsertSituacaoFinalInativa() {
		try {
			aelMatrizSituacao.getSituacaoItemSolicitacaoPara().setIndSituacao(DominioSituacao.I);
			
			systemUnderTest.preInsert(aelMatrizSituacao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção
			assertEquals(e.getCode(), ManterMatrizSituacaoRNExceptionCode.AEL_00482);
		}
	}
}
