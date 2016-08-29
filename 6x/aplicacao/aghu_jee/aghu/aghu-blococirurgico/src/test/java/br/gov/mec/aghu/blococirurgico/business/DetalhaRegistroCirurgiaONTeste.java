package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.business.DetalhaRegistroCirurgiaON.DetalhaRegistroCirurgiaONExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.MbcCirurgias;

@Ignore
public class DetalhaRegistroCirurgiaONTeste extends AGHUBaseUnitTest<DetalhaRegistroCirurgiaON> {
	
//	@Before
//	public void doBeforeEachTestCase() {
//		systemUnderTest = new DetalhaRegistroCirurgiaON(){
//			private static final long serialVersionUID = 3171010499639021991L;
//		};
//	}
	
	@Test
	public void validarEntradaSalaMaiorInicio(){

		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setDataEntradaSala(new Date());
		cirurgia.setDataInicioCirurgia(DateUtil.adicionaDias(new Date(), -1));
		
		try {
			systemUnderTest.validarMbcCirurgias(cirurgia);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_MAIOR_INICIO)));
		}
	}
	
	@Test
	public void validarInicioMaiorFim(){

		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setDataEntradaSala(new Date());
		cirurgia.setDataInicioCirurgia(DateUtil.adicionaDias(new Date(), 1));
		cirurgia.setDataFimCirurgia(new Date());
		
		try {
			systemUnderTest.validarMbcCirurgias(cirurgia);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_INICIO_MAIOR_FIM)));
		}
	}
	
	@Test
	public void validarFimMaiorSaidaSala(){

		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setDataEntradaSala(new Date());
		cirurgia.setDataInicioCirurgia(DateUtil.adicionaDias(new Date(), 1));
		cirurgia.setDataFimCirurgia(DateUtil.adicionaDias(new Date(), 2));
		cirurgia.setDataSaidaSala(new Date());
		
		try {
			systemUnderTest.validarMbcCirurgias(cirurgia);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_FIM_MAIOR_SAIDA_SALA)));
		}
	}
	
	@Test
	public void validarSaidaSalaMaiorEntradaSalaRecuperacao(){

		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setDataEntradaSala(new Date());
		cirurgia.setDataInicioCirurgia(DateUtil.adicionaDias(new Date(), 1));
		cirurgia.setDataFimCirurgia(DateUtil.adicionaDias(new Date(), 2));
		cirurgia.setDataSaidaSala(DateUtil.adicionaDias(new Date(), 3));
		cirurgia.setDataEntradaSr(new Date());
		
		try {
			systemUnderTest.validarMbcCirurgias(cirurgia);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_SAIDA_SALA_MAIOR_ENTRADA_SALA_RECUPERACAO)));
		}
	}
	
//	@Test
//	public void validarEntradaSalaRecuperacaoMaiorSaidaSalaRecuperacao(){
//
//		MbcCirurgias cirurgia = new MbcCirurgias();
//		cirurgia.setDataEntradaSala(new Date());
//		cirurgia.setDataInicioCirurgia(DateUtil.adicionaDias(new Date(), 1));
//		cirurgia.setDataFimCirurgia(DateUtil.adicionaDias(new Date(), 2));
//		cirurgia.setDataSaidaSala(DateUtil.adicionaDias(new Date(), 3));
//		cirurgia.setDataEntradaSr(DateUtil.adicionaDias(new Date(), 4));
//		cirurgia.setDataSaidaSr(new Date());
//		
//		try {
//			systemUnderTest.validarMbcCirurgias(cirurgia);
//			Assert.fail();
//		}
//		catch (ApplicationBusinessException e) {
//			Assert.assertTrue(e.getCode().equals((DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_RECUPERACAO_MAIOR_SAIDA_SALA_RECUPERACAO)));
//		}
//	}
}
