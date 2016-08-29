package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Test;

import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioInfoComplVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterSumarioAltaProcedimentosONTest extends AGHUBaseUnitTest<ManterSumarioAltaProcedimentosON>{

	/**
	 * Deve retornar uma exceção do tipo ApplicationBusinessException.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void validarInformacoesComplementares001Test() throws ApplicationBusinessException {

		AltaSumarioInfoComplVO vo =  new AltaSumarioInfoComplVO();
		systemUnderTest.validarInformacoesComplementares(null, vo);

	}
	
	/**
	 * Não deve retornar uma exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void validarInformacoesComplementares002Test() throws ApplicationBusinessException {

		AltaSumarioInfoComplVO vo =  new AltaSumarioInfoComplVO();
		systemUnderTest.validarInformacoesComplementares("teste", vo);

	}
	
	/**
	 * Não deve retornar uma exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void validarInformacoesComplementares003Test() throws ApplicationBusinessException {

		AltaSumarioInfoComplVO vo =  new AltaSumarioInfoComplVO();
		systemUnderTest.validarInformacoesComplementares("teste", vo);

	}
	
	/**
	 * Não deve retornar uma exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void validarInformacoesComplementares004Test() throws ApplicationBusinessException {

		AltaSumarioInfoComplVO vo =  new AltaSumarioInfoComplVO();
		vo.setvAfaDescrMdto(new VAfaDescrMdto());
		systemUnderTest.validarInformacoesComplementares(null, vo);

	}
	
	/**
	 * Não deve retornar uma exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void validarInformacoesComplementares005Test() throws ApplicationBusinessException {

		AltaSumarioInfoComplVO vo =  new AltaSumarioInfoComplVO();
		vo.setDescricaoMedicamento("teste");
		vo.setvAfaDescrMdto(new VAfaDescrMdto());
		systemUnderTest.validarInformacoesComplementares("teste", vo);

	}

}
