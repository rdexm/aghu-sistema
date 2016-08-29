package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoPedidoMatExpedienteDAO;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ScoPedidoMatExpedienteONTest extends AGHUBaseUnitTest<ScoPedidoMatExpedienteON>{

	@Mock
	private ScoPedidoMatExpedienteDAO mockedScoPedidoMatExpedienteDAO;
	
	@Test
	public void obterScoPedidoMatExpPorChavePrimaria() {
		
		ScoPedidoMatExpediente mat = new ScoPedidoMatExpediente();
		mat.setSeq(1);
		mat.setValorTotal(BigDecimal.ONE);
		
		Mockito.when(mockedScoPedidoMatExpedienteDAO.obterPedidoMatExpPorChavePrimaria(Mockito.anyInt())).thenReturn(mat);
		
		ScoPedidoMatExpediente ret = systemUnderTest.obterScoPedidoMatExpPorChavePrimaria(1);
		
		Assert.assertEquals(mat, ret);
	}

}
