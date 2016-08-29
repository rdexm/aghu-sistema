package br.gov.mec.aghu.exames.agendamento.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ItemHorarioAgendadoRNTest extends AGHUBaseUnitTest<ItemHorarioAgendadoRN>{

	private static final RapServidores SERVIDOR_LOGADO = new RapServidores(
			new RapServidoresId(1, (short) 1));
	
	@Mock
	private IExamesFacade mockedExamesFacade;
	
	@Test
	public void testatualizarHorariosDisponiveis() {
		try {
			AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
			itemHorarioAgendado.setHorarioExameDisp(new AelHorarioExameDisp());
			
			systemUnderTest.atualizarHorariosDisponiveis(itemHorarioAgendado);
						
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		} catch (NullPointerException e) {
			assert true;
		}
	}

	@Test
	public void testExecutarBeforeUpdateTipoMarcacaoExameErro1() {
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setHorarioExameDisp(null);
		try {
			systemUnderTest.atualizarHorariosDisponiveis(itemHorarioAgendado);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemHorarioAgendadoRN.ItemHorarioAgendadoRNExceptionCode.AEL_00531);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateTipoMarcacaoExameErro2() {
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.M);//Se o horario já estiver marcado.
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);		
		try {
			systemUnderTest.atualizarHorariosDisponiveis(itemHorarioAgendado);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemHorarioAgendadoRN.ItemHorarioAgendadoRNExceptionCode.AEL_00531);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateTipoMarcacaoExameSucesso() throws Exception {
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.L);//Horario na condição ideal
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);		
		try {
			systemUnderTest.atualizarHorariosDisponiveis(itemHorarioAgendado);
			Assert.assertEquals(horarioExameDisp.getSituacaoHorario(), DominioSituacaoHorario.M);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
}
