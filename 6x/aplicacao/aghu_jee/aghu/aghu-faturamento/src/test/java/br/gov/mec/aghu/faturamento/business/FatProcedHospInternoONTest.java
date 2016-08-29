package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.dominio.DominioTipoOperacaoConversao;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FatProcedHospInternoONTest extends AGHUBaseUnitTest<FatProcedHospInternoON>{

	@Mock
	private FatProcedHospInternosDAO mockedFatProcedHospInternosDAO;
	
	/**
	 * Testa validação do formulário do Procedimento Hospitalar Interno 
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void validarFormulario1() throws Exception {
	
		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setProcedimentoCirurgico(new MbcProcedimentoCirurgicos());
		procedHospInterno.setItemMedicacao(new MamItemMedicacao());
		procedHospInterno.setItemExame(new MamItemExame());
		systemUnderTest.validarFormulario(procedHospInterno);
	}	

	@Test(expected = ApplicationBusinessException.class)
	public void validarFormulario2() throws Exception {
	
		Mockito.when(mockedFatProcedHospInternosDAO.contaProcedimentoHospitalarInternoPorNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class))).thenReturn(1l);

		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setProcedimentoCirurgico(new MbcProcedimentoCirurgicos());
		procedHospInterno.setProcedimentoHospitalarInterno(new FatProcedHospInternos());
		procedHospInterno.setTipoNutricaoEnteral(DominioTipoNutricaoParenteral.A);
		systemUnderTest.validarFormulario(procedHospInterno);
	}	

	@Test(expected = ApplicationBusinessException.class)
	public void validarFormulario3() throws Exception {
	
		Mockito.when(mockedFatProcedHospInternosDAO.contaProcedimentoHospitalarInternoPorNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class))).thenReturn(1l);

		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setProcedimentoCirurgico(new MbcProcedimentoCirurgicos());
		procedHospInterno.setProcedimentoHospitalarInterno(new FatProcedHospInternos());
		procedHospInterno.setTipoNutricaoEnteral(DominioTipoNutricaoParenteral.N);
		systemUnderTest.validarFormulario(procedHospInterno);
	}	

	@Test(expected = ApplicationBusinessException.class)
	public void validarFormulario4() throws Exception {
	
		Mockito.when(mockedFatProcedHospInternosDAO.contaProcedimentoHospitalarInternoPorNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class))).thenReturn(1l);

		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setProcedimentoCirurgico(new MbcProcedimentoCirurgicos());
		procedHospInterno.setProcedimentoHospitalarInterno(new FatProcedHospInternos());
		procedHospInterno.setTipoNutricaoEnteral(DominioTipoNutricaoParenteral.P);
		systemUnderTest.validarFormulario(procedHospInterno);
	}	
			
	@Test(expected = ApplicationBusinessException.class)
	public void validarFormulario5() throws Exception {
	
		Mockito.when(mockedFatProcedHospInternosDAO.contaProcedimentoHospitalarInternoPorNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class))).thenReturn(1l);

		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setTipoOperConversao(DominioTipoOperacaoConversao.D);
		systemUnderTest.validarFormulario(procedHospInterno);
	}	
	
	@Test(expected = ApplicationBusinessException.class)
	public void validarFormulario6() throws Exception {
	
		Mockito.when(mockedFatProcedHospInternosDAO.contaProcedimentoHospitalarInternoPorNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class))).thenReturn(1l);

		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setFatorConversao(BigDecimal.ONE);
		systemUnderTest.validarFormulario(procedHospInterno);
	}	
			
	@Test(expected = ApplicationBusinessException.class)
	public void validarFormulario7() throws Exception {
	
		Mockito.when(mockedFatProcedHospInternosDAO.contaProcedimentoHospitalarInternoPorNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class))).thenReturn(1l);

		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setTipoOperConversao(DominioTipoOperacaoConversao.M);
		procedHospInterno.setFatorConversao(new BigDecimal(99999));
		systemUnderTest.validarFormulario(procedHospInterno);
	}	
	
	@Test
	public void validarFormulario8() throws Exception {
	
		Mockito.when(mockedFatProcedHospInternosDAO.contaProcedimentoHospitalarInternoPorNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class))).thenReturn(1l);

		FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
		procedHospInterno.setTipoOperConversao(DominioTipoOperacaoConversao.M);
		procedHospInterno.setFatorConversao(new BigDecimal("9999.999"));
		systemUnderTest.validarFormulario(procedHospInterno);
	}		
}
