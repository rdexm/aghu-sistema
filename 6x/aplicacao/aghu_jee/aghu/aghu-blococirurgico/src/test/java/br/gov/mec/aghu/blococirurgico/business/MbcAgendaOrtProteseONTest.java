package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendaOrtProteseON.MbcAgendaOrteseproteseONExceptionCode;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MbcAgendaOrtProteseONTest extends AGHUBaseUnitTest<MbcAgendaOrtProteseON>{


	/**
	 * Teste valida lista vazia e material selecionado
	 */
	@Test
	public void validarAgendaOrteseProteseAdicionadoExistenteListaVazia(){
		MbcAgendaOrtProtese agendaOrtProtese = new MbcAgendaOrtProtese();
		ScoMaterial material = new ScoMaterial();
		material.setCodigo(12);
		List<MbcAgendaOrtProtese> agendaOrtProteseList = new ArrayList<MbcAgendaOrtProtese>();
		try {
			systemUnderTest.validarAgendaOrteseproteseAdicionadoExistente(agendaOrtProteseList, agendaOrtProtese);
		} catch (BaseException e) {
			Assert.fail("Exceção nao esperada gerada: MBC_00809_1");
		}
	}	
	
	/**
	 * Teste valida lista com material diferente material selecionado
	 */
	@Test
	public void validarAgendaOrtProteseAdicionadoExistenteListaDiferenteSelecionado(){
		MbcAgendaOrtProtese AgendaOrtProtese = new MbcAgendaOrtProtese();
		ScoMaterial material1 = new ScoMaterial();
		material1.setCodigo(12);
		AgendaOrtProtese.setScoMaterial(material1);
		
		MbcAgendaOrtProtese AgendaOrtProtese2 = new MbcAgendaOrtProtese();
		ScoMaterial material2 = new ScoMaterial();
		material2.setCodigo(11);
		AgendaOrtProtese2.setScoMaterial(material2);
		
		
		List<MbcAgendaOrtProtese> AgendaOrtProteseList = new ArrayList<MbcAgendaOrtProtese>();
		AgendaOrtProteseList.add(AgendaOrtProtese2);
		try {
			systemUnderTest.validarAgendaOrteseproteseAdicionadoExistente(AgendaOrtProteseList, AgendaOrtProtese);
		} catch (BaseException e) {
			Assert.fail("Exceção nao esperada gerada: MBC_00809_1");
		}
	}	
	
	/**
	 * Teste valida lista com material igual material selecionado
	 */
	@Test
	public void validarAgendaOrtProteseAdicionadoExistenteListaIgualSelecionado(){
		MbcAgendaOrtProtese AgendaOrtProtese = new MbcAgendaOrtProtese();
		ScoMaterial material1 = new ScoMaterial();
		material1.setCodigo(12);
		AgendaOrtProtese.setScoMaterial(material1);
		
		MbcAgendaOrtProtese AgendaOrtProtese2 = new MbcAgendaOrtProtese();
		ScoMaterial material2 = new ScoMaterial();
		material2.setCodigo(12);
		AgendaOrtProtese2.setScoMaterial(material2);
		
		List<MbcAgendaOrtProtese> AgendaOrtProteseList = new ArrayList<MbcAgendaOrtProtese>();
		AgendaOrtProteseList.add(AgendaOrtProtese2);
		
		try {
			systemUnderTest.validarAgendaOrteseproteseAdicionadoExistente(AgendaOrtProteseList, AgendaOrtProtese);
			Assert.fail("Exceção esperada não gerada: MBC_00809_1");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaOrteseproteseONExceptionCode.MBC_00809_1, e.getCode());
		}
	}	
	
	
	
	/**
	 * Teste qtdade zero
	 */
	@Test
	public void validaQtdZero(){
		MbcAgendaOrtProtese agendaOrtProtese = new MbcAgendaOrtProtese();
		agendaOrtProtese.setQtde(Short.valueOf("0"));
		ScoMaterial material = new ScoMaterial();
		material.setDescricao("Descrivcao Teste");
		agendaOrtProtese.setScoMaterial(material);
		
		try {
			systemUnderTest.validarQtdeOrtProtese(agendaOrtProtese);
			Assert.fail("Exceção esperada não gerada: MBC_00809_2");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaOrteseproteseONExceptionCode.AGENDA_ORTPROT_QTDE_INVALIDA, e.getCode());
		}
	}	
	
	/**
	 * Teste qtdade vazia
	 */
	@Test
	public void validaQtdVazia(){
		MbcAgendaOrtProtese agendaOrtProtese = new MbcAgendaOrtProtese();
		ScoMaterial material = new ScoMaterial();
		material.setDescricao("Descrivcao Teste");
		agendaOrtProtese.setScoMaterial(material);
		try {
			systemUnderTest.validarQtdeOrtProtese(agendaOrtProtese);
			Assert.fail("Exceção esperada não gerada: MBC_00809_2");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaOrteseproteseONExceptionCode.AGENDA_ORTPROT_QTDE_INVALIDA, e.getCode());
		}
	}	

	
	/**
	 * Teste qtdade legal
	 */
	@Test
	public void validaQtdLegal(){
		MbcAgendaOrtProtese AgendaOrtProtese = new MbcAgendaOrtProtese();
		AgendaOrtProtese.setQtde(Short.valueOf("3"));
		
		try {
			systemUnderTest.validarQtdeOrtProtese(AgendaOrtProtese);
		} catch (BaseException e) {
			Assert.fail("Exceção não esperada foi gerada: MBC_00809_2");
		}
	}	
}
