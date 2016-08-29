package br.gov.mec.aghu.compras.autfornecimento.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.suprimentos.vo.CalculoValorTotalAFVO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário para objeto de negócio responsável por pesquisa de versões
 * anteriores de uma AF.
 * 
 * @author mlcruz
 */
public class PesquisarVersaoAnteriorAutorizacaoFornONTest extends AGHUBaseUnitTest<PesquisarVersaoAnteriorAutorizacaoFornON>{

	@Mock
	private ScoFaseSolicitacaoDAO fslDao;
	
	@Test
	public void testCalculoValorTotalAF() {
		// Parâmetros
		final Integer numero = 1, seq = 1;
		
		// Resultado
		final List<Object[]> result = new ArrayList<Object[]>();
		
		// Item A
		Integer qtdeSolicitadaA = 12;
		Double vlrUnitarioA = 1.5;
		Double percIpiA = 10.0;
		Double percAcrescItemA = 5.0;
		Double percAcrescA = 5.0;
		Double percDescontoA = 1.0;
		Double percDescItemA = 5.0;
		Double vlrEfetivadoA = 10.0;
		DominioTipoFaseSolicitacao tipoA = DominioTipoFaseSolicitacao.C;
		Integer qtdeRecebidaA = 3;
		Double vlrBrutoA = ((qtdeSolicitadaA - qtdeRecebidaA) * vlrUnitarioA);
		Double vlrItemA = vlrBrutoA + vlrEfetivadoA;
		Double vlrDescItemA = (vlrBrutoA * (percDescItemA / 100));
		Double vlrDescA = (vlrBrutoA * (percDescontoA / 100));
		Double vlrDescCondA = vlrDescItemA + vlrDescA;
		Double vlrAcrescItemA = (vlrBrutoA * (percAcrescItemA /100));
		Double vlrAcrescA = (vlrBrutoA * (percAcrescA /100));
		Double vlrAcrescCondA = vlrAcrescItemA + vlrAcrescA;
		Double vlrIpiA = (vlrBrutoA - vlrDescCondA + vlrAcrescCondA) * (percIpiA / 100);
		
		final Object[] itemA = new Object[] { qtdeSolicitadaA, vlrUnitarioA,
				percIpiA, percAcrescItemA, percDescontoA, percAcrescA,
				percDescItemA, vlrEfetivadoA, tipoA, qtdeRecebidaA };

		result.add(itemA);
		
		// Item B
		Integer qtdeSolicitadaB = 1;
		Double vlrUnitarioB = 1.5;
		Double percIpiB = 0.0;
		Double percAcrescItemB = 0.0;
		Double percAcrescB = 0.0;
		Double percDescontoB = 0.0;
		Double percDescItemB = 0.0;
		Double vlrEfetivadoB = 10.0;
		DominioTipoFaseSolicitacao tipoB = DominioTipoFaseSolicitacao.S;
		Integer qtdeRecebidaB = 1;
		Double vlrBrutoB = vlrUnitarioB;
		Double vlrItemB = vlrBrutoB;
		Double vlrDescCondB = 0.0;
		Double vlrAcrescCondB = 0.0;
		Double vlrIpiB = 0.0;
		
		final Object[] itemB = new Object[] { qtdeSolicitadaB, vlrUnitarioB,
				percIpiB, percAcrescItemB, percDescontoB, percAcrescB,
				percDescItemB, vlrEfetivadoB, tipoB, qtdeRecebidaB };

		result.add(itemB);
		
		Mockito.when(fslDao.createCalculoValorTotalAFHql(numero, seq)).thenReturn(result);
		
		// Chamada
		CalculoValorTotalAFVO calculo = systemUnderTest.getCalculoValorTotalAF(numero, seq);		
		
		// Validação
		CalculoValorTotalAFVO expected = new CalculoValorTotalAFVO();
		expected.setValorBruto(vlrBrutoA + vlrBrutoB);
		expected.setValorIPI(vlrIpiA + vlrIpiB);
		expected.setValorAcresc(vlrAcrescCondA + vlrAcrescCondB);
		expected.setValorDesc(vlrDescCondA + vlrDescCondB);
		expected.setValorEfetivo(vlrEfetivadoA + vlrEfetivadoB);
		expected.setValorLiq((vlrItemA - vlrEfetivadoA) + (vlrItemB - vlrEfetivadoB));
		expected.setValorTotalAF(vlrItemA + vlrItemB);
		
		assertEquals(expected.getValorBruto(), calculo.getValorBruto());
		assertEquals(expected.getValorIPI(), calculo.getValorIPI());
		assertEquals(expected.getValorAcresc(), calculo.getValorAcresc());
		assertEquals(expected.getValorDesc(), calculo.getValorDesc());
		assertEquals(expected.getValorLiq(), calculo.getValorLiq());
		assertEquals(expected.getValorEfetivo(), calculo.getValorEfetivo());
		assertEquals(expected.getValorTotalAF(), calculo.getValorTotalAF());
	}
}
