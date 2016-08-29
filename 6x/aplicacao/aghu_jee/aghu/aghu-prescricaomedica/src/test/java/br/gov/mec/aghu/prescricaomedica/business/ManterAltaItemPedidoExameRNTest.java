package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaItemPedidoExameDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaItemPedidoExameRNTest extends AGHUBaseUnitTest<ManterAltaItemPedidoExameRN>{
	
	private static final Log log = LogFactory.getLog(ManterAltaItemPedidoExameRNTest.class);

	@Mock
	private ManterAltaSumarioRN mockedAltaSumarioRN;
	@Mock
	private MpmAltaItemPedidoExameDAO mockedAltaItemPedidoExameDAO;

	private void considerarInserir(
			final MpmAltaItemPedidoExame altaItemPedidoExame) {
	}
	private void considerarVerificarAltaSumarioAtivo() throws ApplicationBusinessException {
	}
	
	private void considerarObterMpmAltaItemPedidoExame() throws ApplicationBusinessException {
		Mockito.when(mockedAltaItemPedidoExameDAO.obterMpmAltaItemPedidoExame(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(new ArrayList<MpmAltaItemPedidoExame>());
	}

	@Test
	public void inserirAltaItemPedidoExameTest() {
		MpmAltaItemPedidoExame altaItemPedidoExame = new MpmAltaItemPedidoExame();
		altaItemPedidoExame.setMpmAltaSumarios(new MpmAltaSumario());
		altaItemPedidoExame.getMpmAltaSumarios().setId(new MpmAltaSumarioId());
		altaItemPedidoExame.getMpmAltaSumarios().getId().setApaSeq(1);
		altaItemPedidoExame.setAelUnfExecutaExames(new AelUnfExecutaExames());
		altaItemPedidoExame.getAelUnfExecutaExames().setId(new AelUnfExecutaExamesId());
		
		try {
			considerarObterMpmAltaItemPedidoExame();
		} catch (ApplicationBusinessException e1) {
			// TODO Auto-generated catch block
			log.error(e1.getMessage());
		}
		considerarInserir(altaItemPedidoExame);
		
		try {
			considerarVerificarAltaSumarioAtivo();
			systemUnderTest.inserirAltaItemPedidoExame(altaItemPedidoExame);
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

}
