package br.gov.mec.aghu.blococirurgico.business;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.business.AcompanhamentoCirurgiaON.AcompanhamentoCirurgiaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AcompanhamentoCirurgiaONTest extends AGHUBaseUnitTest<AcompanhamentoCirurgiaON>{

	@Mock
	private MbcCirurgiasDAO mockMbcCirurgiasDAO;
	@Mock
	private IParametroFacade mockIParametroFacade;
	@Mock
	private EmailUtil mockEmailUtil;

	@Test
	public void testValidarSeExisteCirurgiatransOperatorio() {
		try {
			final MbcCirurgias cirurgia = new MbcCirurgias();
			List<MbcCirurgias> lista =  Arrays.asList(new MbcCirurgias());
			
			
			Mockito.when(mockMbcCirurgiasDAO.pesquisarMbcCirurgiasComSalaCirurgia(Mockito.any(Date.class), Mockito.anyShort(), Mockito.anyShort(),
					Mockito.any(DominioSituacaoCirurgia.class), Mockito.anyBoolean(), Mockito.anyShort(), Mockito.anyBoolean(), 
					Mockito.anyBoolean(), Mockito.anyString())).thenReturn(lista);

			systemUnderTest.validarSeExisteCirurgiatransOperatorio(cirurgia);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((AcompanhamentoCirurgiaONExceptionCode.ERRO_EXISTE_CIRURGIA_TRANSOPERATORIO)));
		}
	}
	
	
}

	
