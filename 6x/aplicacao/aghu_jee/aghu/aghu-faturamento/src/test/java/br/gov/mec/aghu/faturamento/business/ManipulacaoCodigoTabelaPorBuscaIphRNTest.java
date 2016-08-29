package br.gov.mec.aghu.faturamento.business;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.faturamento.business.ManipulacaoCodigoTabelaPorBuscaIphRN.FatcBuscaProcedPrCtaVO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManipulacaoCodigoTabelaPorBuscaIphRNTest extends AGHUBaseUnitTest<ManipulacaoCodigoTabelaPorBuscaIphRN>{

	@Mock
	private FatCompatExclusItemDAO mockedFatCompatExclusItemDAO;
	@Mock
	private FatAtoMedicoAihDAO mcokedFatAtoMedicoAihDAO;
	
	@Test
	public void testObterCodigoTabelaConformeBuscaIph() {

		Long result = null;
		Integer eaiCthSeq = null;
		Short iphPhoSeq = null;
		Integer iphSeq = null;
		List<FatCompatExclusItem> listaIct = null;
		FatCompatExclusItem ict = null;
		FatItensProcedHospitalar iph = null;
		List<FatAtoMedicoAih> listaAma = null;
		FatAtoMedicoAih ama = null;
		Long codTabela = null;

		//assert
		result = systemUnderTest.obterCodigoTabelaConformeBuscaIph(
				eaiCthSeq, iphPhoSeq, iphSeq);
		Assert.assertNull(result);
		//setup
		iph = new FatItensProcedHospitalar();
		iph.setCodTabela(codTabela);
		ict = new FatCompatExclusItem();
		ict.setItemProcedHosp(iph);
		listaIct = new LinkedList<FatCompatExclusItem>();
		listaIct.add(ict);
		//assert empty listaAma
		result = systemUnderTest.obterCodigoTabelaConformeBuscaIph(
				eaiCthSeq, iphPhoSeq, iphSeq);
		Assert.assertNull(result);
		//setup
		codTabela = Long.valueOf(321l);
		iph.setCodTabela(codTabela);
		ama = new FatAtoMedicoAih();
		ama.setItemProcedimentoHospitalar(iph);
		listaAma = new LinkedList<FatAtoMedicoAih>();
		listaAma.add(ama);
		//assert empty listaAma
		result = systemUnderTest.obterCodigoTabelaConformeBuscaIph(
				eaiCthSeq, iphPhoSeq, iphSeq);
	}

	@Test
	public void testObterCodigoTabelaSuffixadaConformeBuscaIph() {

		FatcBuscaProcedPrCtaVO result = null;
		Integer eaiCthSeq = null;
		Short iphPhoSeq = null;
		Integer iphSeq = null;
		Long codTabela = null;
		Long codTabela2 = null;

		//setup
		eaiCthSeq = Integer.valueOf(123);
		iphPhoSeq = Short.valueOf((short) 21);
		iphSeq = Integer.valueOf(345);
		codTabela = Long.valueOf(9876l);
		//assert
		result = systemUnderTest.obterCodigoTabelaSuffixadaConformeBuscaIph(
				eaiCthSeq, iphPhoSeq, iphSeq, codTabela);
		Assert.assertNotNull(result);
		Assert.assertEquals(
				result.codTabela, codTabela);
		Assert.assertEquals(
				result.codTabelaSufixo,
				ManipulacaoCodigoTabelaPorBuscaIphRN.MAGIC_INT_COD_TABELA_SUFFIX_NULL_EQ_1);
		Assert.assertEquals(
				result.codTabelaMod,
				codTabela.longValue()
						* 10
						+ ManipulacaoCodigoTabelaPorBuscaIphRN.MAGIC_INT_COD_TABELA_SUFFIX_NULL_EQ_1);
		Assert.assertEquals(
				result.codTabelaModStr,
				result.codTabelaModStr.trim().length(),
				ManipulacaoCodigoTabelaPorBuscaIphRN.FatcBuscaProcedPrCtaVO.MAGIC_NUMBER_DIGIT_AMOUNT_FORMATTER_EQ_11);
						
		//setup
		codTabela2 = Long.valueOf(9876l);
		//assert
		result = systemUnderTest.obterCodigoTabelaSuffixadaConformeBuscaIph(
				eaiCthSeq, iphPhoSeq, iphSeq, codTabela);
		Assert.assertNotNull(result);
		Assert.assertEquals(
				result.codTabela, codTabela2);
	}
}
