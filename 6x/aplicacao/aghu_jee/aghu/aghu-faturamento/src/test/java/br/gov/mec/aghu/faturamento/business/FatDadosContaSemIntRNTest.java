package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatDadosContaSemIntRNTest extends AGHUBaseUnitTest<FatDadosContaSemIntRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	final static String USUARIO = "usuario";
	private IAghuFacade mockedAghuFacade;
	
	
	@Test
	public void bruPreAtualizacaoRowTest(){
		
		final FatDadosContaSemInt original = new FatDadosContaSemInt();
		final FatDadosContaSemInt modificada = new FatDadosContaSemInt();
		final Date dtFinal = new Date();
		final Date dtInicial = DateUtil.adicionaDias(new Date(), 10);
		
		modificada.setDataFinal(dtFinal);
		modificada.setDataInicial(dtInicial);
		try {
			
			systemUnderTest.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			
			Assert.fail("Deveria ter ocorrido a excessão "+ FaturamentoExceptionCode.ERRO_DATA_FINAL_ANTERIOR_A_INICIAL_FAT_DADOS_CONTA_SEM_INTERNACAO.toString());
		} catch (BaseException e) {
		}
	}
}