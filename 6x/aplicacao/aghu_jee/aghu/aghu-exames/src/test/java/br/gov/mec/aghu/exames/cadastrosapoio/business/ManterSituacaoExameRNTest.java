package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ManterSituacaoExameRN.ManterSituacaoExameRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterSituacaoExameRNTest extends AGHUBaseUnitTest<ManterSituacaoExameRN>{

	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private AelMatrizSituacaoDAO mockedAelMatrizSituacaoDAO;
	
	private AelSitItemSolicitacoes aelSitItemSolicitacoes;
	private AelSitItemSolicitacoes aelSitItemSolicitacoesOriginal;
	
	@Before
	public void doBeforeEachTestCase() {
		aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoes.setDescricao("teste");
		aelSitItemSolicitacoes.setCriadoEm(new Date());
		aelSitItemSolicitacoes.setServidor(new RapServidores(new RapServidoresId()));
		aelSitItemSolicitacoesOriginal = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoesOriginal.setDescricao("teste");
		aelSitItemSolicitacoesOriginal.setCriadoEm(aelSitItemSolicitacoes.getCriadoEm());
		aelSitItemSolicitacoesOriginal.setServidor(new RapServidores(new RapServidoresId()));
	}
	
	@Test
	public void testPreUpdateDescricaoAlterada() {
		try {
			aelSitItemSolicitacoesOriginal.setDescricao("testeOld");
			
			systemUnderTest.preUpdate(aelSitItemSolicitacoesOriginal, aelSitItemSolicitacoes);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para a descrição alterada
			assertEquals(e.getCode(), ManterSituacaoExameRNExceptionCode.AEL_00346);
		}
	}
	
	@Test
	public void testPreUpdateDescricaoNaoAlterada() {
		try {
			systemUnderTest.preUpdate(aelSitItemSolicitacoesOriginal, aelSitItemSolicitacoes);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testPreUpdateCriacaoAlterada() {
		try {
			Date data = new Date();
			data.setYear(0);
			aelSitItemSolicitacoesOriginal.setCriadoEm(data);
			
			systemUnderTest.preUpdate(aelSitItemSolicitacoesOriginal, aelSitItemSolicitacoes);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para a descrição alterada
			assertEquals(e.getCode(), ManterSituacaoExameRNExceptionCode.AEL_00369);
		}
	}
	
	@Test
	public void testPreUpdateCriacaoNaoAlterada() {
		try {
			systemUnderTest.preUpdate(aelSitItemSolicitacoesOriginal, aelSitItemSolicitacoes);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreUpdateServidorAlterado() {
		try {
			aelSitItemSolicitacoes.setServidor(new RapServidores(new RapServidoresId(222, Short.valueOf("10"))));
			
			systemUnderTest.preUpdate(aelSitItemSolicitacoesOriginal, aelSitItemSolicitacoes);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para a descrição alterada
			assertEquals(e.getCode(), ManterSituacaoExameRNExceptionCode.AEL_00369);
		}
	}
	
	@Test
	public void testPreUpdateServidorNaoAlterado() {
		try {
			systemUnderTest.preUpdate(aelSitItemSolicitacoesOriginal, aelSitItemSolicitacoes);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
}
