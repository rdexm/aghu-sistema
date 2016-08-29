package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.MbcAgendaAnestesiaON.MbcAgendaAnestesiaONExceptionCode;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MbcAgendaAnestesiaONTest extends AGHUBaseUnitTest<MbcAgendaAnestesiaON>{

	/**
	 * sucesso
	 */
	@Test
	public void validarAnestesiaAdicionadaExistente1() {
		List<MbcAgendaAnestesia> listaAgendaAnestesias = new ArrayList<MbcAgendaAnestesia>();
		MbcAgendaAnestesia agendaAnestesia2 = new MbcAgendaAnestesia();
		MbcTipoAnestesias tipo = new MbcTipoAnestesias();
		tipo.setSeq(Short.valueOf("2"));
		agendaAnestesia2.setMbcTipoAnestesias(tipo);
		agendaAnestesia2.setMbcAgendas(new MbcAgendas());
		listaAgendaAnestesias.add(agendaAnestesia2);
		
		MbcAgendaAnestesia agendaAnestesia = new MbcAgendaAnestesia();
		agendaAnestesia.setMbcTipoAnestesias(new MbcTipoAnestesias());
		agendaAnestesia.setMbcAgendas(new MbcAgendas());
		
		try {
			systemUnderTest.validarAnestesiaAdicionadaExistente(listaAgendaAnestesias, agendaAnestesia);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
		
	}
	
	/**
	 * excecao MBC_00895
	 */
	@Test
	public void validarAnestesiaAdicionadaExistente2() {
		List<MbcAgendaAnestesia> listaAgendaAnestesias = new ArrayList<MbcAgendaAnestesia>();
		MbcAgendaAnestesia agendaAnestesia2 = new MbcAgendaAnestesia();
		MbcTipoAnestesias tipo = new MbcTipoAnestesias();
		tipo.setSeq(Short.valueOf("2"));
		agendaAnestesia2.setMbcTipoAnestesias(tipo);
		agendaAnestesia2.setMbcAgendas(new MbcAgendas());
		listaAgendaAnestesias.add(agendaAnestesia2);
		
		MbcAgendaAnestesia agendaAnestesia = new MbcAgendaAnestesia();
		agendaAnestesia.setMbcTipoAnestesias(tipo);
		agendaAnestesia.setMbcAgendas(new MbcAgendas());
		try {
			systemUnderTest.validarAnestesiaAdicionadaExistente(listaAgendaAnestesias, agendaAnestesia);
			Assert.fail("Exceção não gerada: MBC_00824_1");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaAnestesiaONExceptionCode.MBC_00824_1, e.getCode());
		}
	}
	
}
