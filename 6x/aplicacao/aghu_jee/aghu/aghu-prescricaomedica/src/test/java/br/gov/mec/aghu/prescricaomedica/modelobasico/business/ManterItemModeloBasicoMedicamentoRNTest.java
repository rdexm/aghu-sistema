package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterItemModeloBasicoMedicamentoRNTest extends AGHUBaseUnitTest<ManterItemModeloBasicoMedicamentoRN>{
	
	@Mock
	private MpmItemModeloBasicoMedicamentoDAO mockedItemModeloBasicoMedicamentoDAO;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	
	@Test(expected=ApplicationBusinessException.class)
	public void validarFormaDosagemTest001() throws ApplicationBusinessException {
		
		final AfaFormaDosagem formaDosagens = new AfaFormaDosagem();
		formaDosagens.setIndSituacao(DominioSituacao.I);
		
		Mockito.when(mockedFarmaciaFacade.obterAfaFormaDosagem(Mockito.anyInt())).thenReturn(formaDosagens);

		systemUnderTest.validarFormaDosagem(Integer.valueOf("111111"), Integer.valueOf("111111"));
		
	}
	
}
