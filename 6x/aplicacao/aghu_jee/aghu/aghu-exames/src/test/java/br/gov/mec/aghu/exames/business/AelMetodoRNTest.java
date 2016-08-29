package br.gov.mec.aghu.exames.business;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.business.AelMetodoRN.AelMetodoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelMetodoDAO;
import br.gov.mec.aghu.model.AelMetodo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author amalmeida
 *
 */
public class AelMetodoRNTest extends AGHUBaseUnitTest<AelMetodoRN>{

	@Mock
	private AelMetodoDAO mockedAelMetodoDAO;

	private AelMetodo aelMetodo;
	

	@Test
	public void validaCamposAlteradosTest() {

		aelMetodo = new AelMetodo();

		RapServidoresId servidorId1 = new RapServidoresId();
		servidorId1.setMatricula(Integer.valueOf("1"));
		servidorId1.setVinCodigo(Short.valueOf("1"));

		RapServidoresId servidorId2 = new RapServidoresId();
		servidorId2.setMatricula(Integer.valueOf("2"));
		servidorId2.setVinCodigo(Short.valueOf("2"));

		RapServidores servidor1 = new RapServidores();
		servidor1.setId(servidorId1);

		RapServidores servidor2 = new RapServidores();
		servidor2.setId(servidorId2);

		final AelMetodo campoLaudoOld = new AelMetodo();
		campoLaudoOld.setCriadoEm(new Date());
		campoLaudoOld.setServidor(servidor1);

		aelMetodo.setCriadoEm(new Date());
		aelMetodo.setServidor(servidor2);


		Mockito.when(mockedAelMetodoDAO.obterOriginal(Mockito.any(AelMetodo.class))).thenReturn(campoLaudoOld);

		try {
			systemUnderTest.validaCamposAlterados(aelMetodo,campoLaudoOld);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(AelMetodoRNExceptionCode.AEL_01730 == e.getCode());
		}
		
	}
	
	@Test
	public void validaDescricaoAlteradaTest() {

		aelMetodo = new AelMetodo();

		final AelMetodo campoLaudoOld = new AelMetodo();
		campoLaudoOld.setDescricao("Teste 1");
		
		aelMetodo.setDescricao("Teste 1");

		Mockito.when(mockedAelMetodoDAO.obterOriginal(Mockito.any(AelMetodo.class))).thenReturn(campoLaudoOld);

		try {
			systemUnderTest.validaDescricaoAlterada(aelMetodo,campoLaudoOld);
		} catch (BaseException e) {
			Assert.fail();
		}
		
	}





}