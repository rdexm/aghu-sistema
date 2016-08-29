package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioEstocavelConsumoSinteticoMaterial;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioOrdenacaoConsumoSinteticoMaterial;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.ConsumoSinteticoMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioConsumoSinteticoMaterialVO;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioConsumoSinteticoMateriaisON extends BaseBusiness {

@EJB
private SceEstoqueGeralRN sceEstoqueGeralRN;

private static final Log LOG = LogFactory.getLog(RelatorioConsumoSinteticoMateriaisON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;

@Inject
private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6870971242204126954L;

	/**
	 * Pesquisa do relatório de consumo sintético de material considerando a classificação de materiais
	 * 
	 * @param cctCodigo
	 * @param almSeq
	 * @param indEstocavel
	 * @param cn5Numero
	 * @param dtCompetencia
	 * @param ordenacao
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioConsumoSinteticoMaterialVO> pesquisarRelatorioConsumoSinteticoMaterial(final Integer cctCodigo, final Short almSeq,
			final DominioEstocavelConsumoSinteticoMaterial estocavel, final Long cn5Numero, final Date dtCompetencia, final DominioOrdenacaoConsumoSinteticoMaterial ordenacao,
			ScoGrupoMaterial grupoMaterial) {

		/*
		 * INI RN1: Se foi informado como filtro de pesquisa a classificação do material, deve ser acrescentado na consulta a restrição da procedure P_DEFINE_WHERE.
		 */
		Long valorClassificacaoInicial = null;
		Long valorClassificacaoFinal = null;

		if (cn5Numero != null) {

			Long valorCodigo = 0l;

			ScoClassifMatNiv5 classifMatNiv5 = getComprasFacade().obterClassifMatNiv5PorNumero(cn5Numero);

			if (classifMatNiv5 != null) {

				if (classifMatNiv5.getCodigo().equals(0)) {
					valorCodigo = 99L;
				}

				if (classifMatNiv5.getScoClassifMatNiv4().getId().getCodigo().equals(0)) {
					valorCodigo = valorCodigo + 9900L;
				}

				if (classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getId().getCodigo().equals(0)) {
					valorCodigo = valorCodigo + 990000L;
				}

				if (classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getId().getCodigo().equals(0)) {
					valorCodigo = valorCodigo + 99000000L;
				}

				if (classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getScoClassifMatNiv1().getId().getCodigo().equals(0)) {
					valorCodigo = valorCodigo + 9900000000L;
				}

				valorClassificacaoInicial = classifMatNiv5.getNumero();
				valorClassificacaoFinal = classifMatNiv5.getNumero() + valorCodigo;

			}

		}
		// FIM RN1

		List<RelatorioConsumoSinteticoMaterialVO> listaOrigem = getSceMovimentoMaterialDAO().pesquisarRelatorioConsumoSinteticoMaterial(cctCodigo, almSeq, estocavel,
				dtCompetencia, ordenacao, valorClassificacaoInicial, valorClassificacaoFinal, grupoMaterial);
		List<RelatorioConsumoSinteticoMaterialVO> retorno = new LinkedList<RelatorioConsumoSinteticoMaterialVO>();

		if (listaOrigem != null) {
			
			Map<Integer, BigDecimal> totaisCentroCusto = new HashMap<Integer, BigDecimal>();

			for (RelatorioConsumoSinteticoMaterialVO vo : listaOrigem) {

				// Verifica a classificação do material
				boolean existeMaterialClassificado = getComprasFacade().existeMateriaisClassificacoesPorConsumoSinteticoMaterial(vo.getCodigoMaterial(), cn5Numero);

				// Caso o material pertença ao intervalo de classificação
				if (existeMaterialClassificado) {

					List<ConsumoSinteticoMaterialVO> listaConsumoSinteticoMaterial = this.getSceMovimentoMaterialDAO().pesquisarConsumoSinteticoPorMaterial(vo.getCodigoMaterial(),
							vo.getCodigoCentroCusto(), almSeq, estocavel, dtCompetencia, valorClassificacaoInicial, valorClassificacaoFinal, grupoMaterial);

					// Calcula (cálculo retirado da consulta principal) e seta a quantidade
					Integer somaQuantidade = this.calcularSomaQuantidadeConsumoSinteticoMaterial(listaConsumoSinteticoMaterial);
					vo.setQuantidade(somaQuantidade);

					// Calcula (cálculo retirado da consulta principal) e seta o valor
					BigDecimal somaValor = this.calcularSomaValorConsumoSinteticoMaterial(listaConsumoSinteticoMaterial);
					vo.setValor(somaValor);

					// Calcula custo médio ponderado. Chamada para FUNCTION CF_CUSTO_MEDIOFORMULA
					vo.setCustoMedioPonderado(this.calcularCustoMedioPonderado(vo.getQuantidade(), vo.getValor()));

					// Soma totais por centro de custo para o cálculo do percentual
					this.somarTotalPorCentroCusto(totaisCentroCusto, vo);
					

					retorno.add(vo);
				}

			}

			if(!retorno.isEmpty()){

				/*
				 * Considera a ordenação por valor quando informada pelo usuário. Está ordenação é realizada fora da consulta pois o valor foi calculado da mesma forma
				 */
				if (DominioOrdenacaoConsumoSinteticoMaterial.V.equals(ordenacao)) {
					/*
					 * Atenção para realização da ordenação por valor os atributos código e descrição do centro de custo devem ser considerados, deste modo, a ordenação da consulta
					 * original será preservada vide: ORDER BY CCT.CODIGO, CCT.DESCRICAO &p_order_by
					 */
					Collections.sort(retorno, new Comparator<RelatorioConsumoSinteticoMaterialVO>() {
						@Override
						public int compare(RelatorioConsumoSinteticoMaterialVO o1, RelatorioConsumoSinteticoMaterialVO o2) {
							return o1.compareTo(o2);
						}
					});

				}
				
				// Recalcula o percentual de cada item
				this.calcularPercentualPorCentroCusto(retorno, totaisCentroCusto);
				
			}

		}

		return retorno;

	}

	/**
	 * Calcula a quantidade do material
	 * 
	 * @param vo
	 * @return
	 */
	protected Integer calcularSomaQuantidadeConsumoSinteticoMaterial(List<ConsumoSinteticoMaterialVO> listaConsumoSinteticoMaterial) {
		Integer resultado = 0;

		for (ConsumoSinteticoMaterialVO vo : listaConsumoSinteticoMaterial) {

			Integer quantidadeN = 0;
			Integer quantidadeS = 0;

			// Não estornada
			if (Boolean.FALSE.equals((vo.getIndEstorno()))) {

				if (DominioIndOperacaoBasica.DB.equals(vo.getIndOperacaoBasica())) {
					quantidadeN = vo.getQuantidade() != null ? vo.getQuantidade() : 0;
				} else {
					quantidadeN = (vo.getQuantidade() != null ? vo.getQuantidade() : 0) * -1;
				}

			}

			// Estornada
			if (Boolean.TRUE.equals((vo.getIndEstorno()))) {

				if (DominioIndOperacaoBasica.DB.equals(vo.getIndOperacaoBasica())) {
					quantidadeS = vo.getQuantidade() != null ? vo.getQuantidade() : 0;
				} else {
					quantidadeS = (vo.getQuantidade() != null ? vo.getQuantidade() : 0) * -1;
				}

			}

			resultado += quantidadeN - quantidadeS;

		}

		return resultado;
	}

	/**
	 * Calcula o valor do material
	 * 
	 * @param vo
	 * @return
	 */
	protected BigDecimal calcularSomaValorConsumoSinteticoMaterial(List<ConsumoSinteticoMaterialVO> listaConsumoSinteticoMaterial) {

		Double soma = 0d;

		for (ConsumoSinteticoMaterialVO vo : listaConsumoSinteticoMaterial) {

			Double valorN = 0d;
			Double valorS = 0d;

			// Não estornada
			if (Boolean.FALSE.equals(vo.getIndEstorno())) {

				if (DominioIndOperacaoBasica.DB.equals(vo.getIndOperacaoBasica())) {
					valorN = vo.getValor() != null ? vo.getValor().doubleValue() : 0;
				} else {
					valorN = (vo.getValor() != null ? vo.getValor().doubleValue() : 0) * -1;
				}

			}

			// Estornada
			if (Boolean.TRUE.equals((vo.getIndEstorno()))) {

				if (DominioIndOperacaoBasica.DB.equals(vo.getIndOperacaoBasica())) {
					valorS = vo.getValor() != null ? vo.getValor().doubleValue() : 0;
				} else {
					valorS = (vo.getValor() != null ? vo.getValor().doubleValue() : 0) * -1;
				}

			} else {
				valorS = 0d;
			}

			soma += valorN - valorS;
		}

		return new BigDecimal(soma).setScale(2, BigDecimal.ROUND_HALF_EVEN);

	}

	/**
	 * ORADB FUNCTION CF_CUSTO_MEDIOFORMULA Calcula custo médio ponderado
	 * 
	 * @param quantidade
	 * @param valor
	 * @return
	 */
	protected BigDecimal calcularCustoMedioPonderado(Integer quantidade, BigDecimal valor) {
		double custoMedioPonderado = getSceEstoqueGeralRN().calcularCustoMedioPonderado(valor.doubleValue(), quantidade);
		return new BigDecimal(custoMedioPonderado).setScale(4, BigDecimal.ROUND_HALF_EVEN);
	}

	/**
	 * Incrementa o total por centro de custo
	 * @param totaisCentroCusto
	 * @param vo
	 */
	protected void somarTotalPorCentroCusto(Map<Integer, BigDecimal> totaisCentroCusto, RelatorioConsumoSinteticoMaterialVO vo) {

		Integer key = vo.getCodigoCentroCusto();
		BigDecimal valor = totaisCentroCusto.get(key) != null ? totaisCentroCusto.get(key) : BigDecimal.ZERO;
		BigDecimal soma = new BigDecimal(valor.doubleValue() + vo.getValor().doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP); 
		totaisCentroCusto.put(vo.getCodigoCentroCusto(), soma);

	}
	
	/**
	 * Calcula o percentual de cada item do relatório através do total do centro de custo
	 * @param lista
	 * @param totaisCentroCusto
	 */
	protected void calcularPercentualPorCentroCusto(List<RelatorioConsumoSinteticoMaterialVO> lista, Map<Integer, BigDecimal> totaisCentroCusto){
	
		for (RelatorioConsumoSinteticoMaterialVO vo : lista) {
			final Double valorItem = vo.getValor().doubleValue();
			Double totalCentroCusto = totaisCentroCusto.get(vo.getCodigoCentroCusto()).doubleValue();
			if (totalCentroCusto == 0){
				totalCentroCusto = new Double(1);
			}
			Double percentual = (valorItem * 100) / totalCentroCusto;
			vo.setPercentual(new BigDecimal(percentual).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		}
	}
	
	
	/**
	 * Getters para RNs e DAOs
	 */
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}

	protected SceEstoqueGeralRN getSceEstoqueGeralRN() {
		return sceEstoqueGeralRN;
	}

}
