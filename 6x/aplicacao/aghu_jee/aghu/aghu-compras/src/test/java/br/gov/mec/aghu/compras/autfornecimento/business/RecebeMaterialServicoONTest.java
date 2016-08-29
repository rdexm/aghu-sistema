package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.RecebeMaterialServicoON.RecebimentoItemAFONExceptionCode;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentoMaterialServicoVO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RecebeMaterialServicoONTest extends AGHUBaseUnitTest<RecebeMaterialServicoON>{
	
//	/** Facade Mokeada */
	@Mock
	private IParametroFacade parametroFacade;
	@Mock
	private IEstoqueFacade estoqueFacade;
	@Mock
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	@Mock
	private ICascaFacade cascaFacade;
	
	@Test
	public void CriticarValorNotaFiscal() throws ApplicationBusinessException, ItemRecebimentoValorExcedido{
		final ScoItemAutorizacaoForn itemAf = new ScoItemAutorizacaoForn();
		itemAf.setValorUnitario(1.00);
		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("R");
		
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ENTRADA_NF_MAT_SERV)).thenReturn(parametro);
		
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CRITICA_VLR_NF)).thenReturn(parametro);
		
		Mockito.when(estoqueFacade.obterValorTotalItemNotaFiscal(1)).thenReturn(Double.valueOf(10));
		
		Mockito.when(scoItemAutorizacaoFornDAO.obterPorChavePrimaria(Mockito.any(ScoItemAutorizacaoFornId.class))).thenReturn(itemAf);
		
		SceDocumentoFiscalEntrada documentoFiscalEntrada = new SceDocumentoFiscalEntrada();
		documentoFiscalEntrada.setSeq(1);
		documentoFiscalEntrada.setValorTotalNf(new Double(10));
		
		RecebimentoMaterialServicoVO itemAF = new RecebimentoMaterialServicoVO();
		itemAF.setTipoSolicitacao(DominioTipoFaseSolicitacao.C);
		itemAF.setValorTotal(BigDecimal.ONE);
		itemAF.setQtdEntregue(1);
		itemAF.setValorUnitario(BigDecimal.ONE);
		List<RecebimentoMaterialServicoVO> listaItemAF = new ArrayList<RecebimentoMaterialServicoVO>();
		listaItemAF.add(itemAF);
		
		try {
			systemUnderTest.receberParcelaItensAF(listaItemAF, documentoFiscalEntrada, new RapServidores(), new ScoFornecedor());
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(RecebimentoItemAFONExceptionCode.MENSAGEM_CRITICA_SOMA_RECEBIMENTOS, e.getCode());
		}		
	}
	
	
	@Test 
	public void testPermitirNotaFiscalEntrada() throws BaseException, ItemRecebimentoValorExcedido{
		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("C");
		final ScoItemAutorizacaoForn itemAf = new ScoItemAutorizacaoForn();
		itemAf.setValorUnitario(1.00);
		
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ENTRADA_NF_MAT_SERV)).thenReturn(parametro);
		
		Mockito.when(scoItemAutorizacaoFornDAO.obterPorChavePrimaria(Mockito.any(ScoItemAutorizacaoFornId.class))).thenReturn(itemAf);
		
		SceDocumentoFiscalEntrada documentoFiscalEntrada = new SceDocumentoFiscalEntrada(); 
		
		try {
			systemUnderTest.receberParcelaItensAF(new ArrayList<RecebimentoMaterialServicoVO>(), documentoFiscalEntrada, new RapServidores(), new ScoFornecedor());
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(RecebimentoItemAFONExceptionCode.MENSAGEM_NAO_PERMITIDO_ENTRADA_NOTA_FISCAL, e.getCode());
		}
	}	
	
	@Test
	public void testExigeNotaFiscalRecebImediataEntrada() throws BaseException, ItemRecebimentoValorExcedido{
		final AghParametros parametroRecebImediata = new AghParametros();
		final AghParametros parametroExigeNF = new AghParametros();
		parametroRecebImediata.setVlrTexto("S");
		parametroExigeNF.setVlrTexto("N");
		final ScoItemAutorizacaoForn itemAf = new ScoItemAutorizacaoForn();
		itemAf.setValorUnitario(1.00);
		
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONF_RECEB_IMEDIATA)).thenReturn(parametroRecebImediata);

		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXIGE_NF_NO_RECEBIMENTO)).thenReturn(parametroExigeNF);
		
		Mockito.when(scoItemAutorizacaoFornDAO.obterPorChavePrimaria(Mockito.any(ScoItemAutorizacaoFornId.class))).thenReturn(itemAf);

		SceDocumentoFiscalEntrada documentoFiscalEntrada = null; 
		
		try {
			systemUnderTest.receberParcelaItensAF(new ArrayList<RecebimentoMaterialServicoVO>(), documentoFiscalEntrada, new RapServidores(), new ScoFornecedor());
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(RecebimentoItemAFONExceptionCode.MENSAGEM_OBRIGATORIO_INFORMAR_NOTA_FISCAL_ENTRADA, e.getCode());
		}		
	}	

	@Test
	public void testExigeNotaFiscalEntrada() throws BaseException, ItemRecebimentoValorExcedido{
		final AghParametros parametroRecebImediata = new AghParametros();
		final AghParametros parametroExigeNF = new AghParametros();
		parametroRecebImediata.setVlrTexto("N");
		parametroExigeNF.setVlrTexto("S");
		final ScoItemAutorizacaoForn itemAf = new ScoItemAutorizacaoForn();
		itemAf.setValorUnitario(1.00);
		
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONF_RECEB_IMEDIATA)).thenReturn(parametroRecebImediata);

		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXIGE_NF_NO_RECEBIMENTO)).thenReturn(parametroExigeNF);
		
		Mockito.when(scoItemAutorizacaoFornDAO.obterPorChavePrimaria(Mockito.any(ScoItemAutorizacaoFornId.class))).thenReturn(itemAf);

		SceDocumentoFiscalEntrada documentoFiscalEntrada = null; 
		
		try {
			systemUnderTest.receberParcelaItensAF(new ArrayList<RecebimentoMaterialServicoVO>(), documentoFiscalEntrada, new RapServidores(), new ScoFornecedor());
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(RecebimentoItemAFONExceptionCode.MENSAGEM_OBRIGATORIO_INFORMAR_NOTA_FISCAL_ENTRADA, e.getCode());
		}		
	}
}
