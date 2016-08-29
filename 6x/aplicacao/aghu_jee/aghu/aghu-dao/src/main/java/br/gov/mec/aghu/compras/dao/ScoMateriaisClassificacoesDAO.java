package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.estoque.vo.MaterialClassificacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class ScoMateriaisClassificacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMateriaisClassificacoes>{
	
	private static final long serialVersionUID = 4662927651917631836L;

	/**
	 * Pesquisa ScoMateriaisClassificacoes pela ID do material
	 * 
	 * @param scoMaterialCodigo
	 * @return
	 */
	public List<ScoMateriaisClassificacoes> pesquisarScoMateriaisClassificacoesPorMaterial(Integer scoMaterialCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class);
		criteria.add(Restrictions.eq(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString(), scoMaterialCodigo));
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa ScoMateriaisClassificacoes pela ID do material
	 * 
	 * @param scoMaterialCodigo
	 * @return
	 */
	public ScoMateriaisClassificacoes buscarPrimeiroScoMateriaisClassificacoesPorMaterial(Integer scoMaterialCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class);
		criteria.add(Restrictions.eq(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString(), scoMaterialCodigo));
		List<ScoMateriaisClassificacoes> resultado = executeCriteria(criteria);
		if (resultado != null && !resultado.isEmpty()) {
			return resultado.get(0);
		}
		return null;
	}

	public List<ScoMateriaisClassificacoes> buscarScoMateriaisClassificacoesPorMaterialEClassificacao(Integer scoMaterialCodigo, Long classIni, Long classFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class);
		criteria.add(Restrictions.eq(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString(), scoMaterialCodigo));
		criteria.add(Restrictions.between(ScoMateriaisClassificacoes.Fields.CN5.toString(), classIni, classFim));
		return executeCriteria(criteria);
	}

	/**
	 * Verifica a existência de classificação do material no relatório de consumo sintético
	 * 
	 * @param codigoMaterial
	 * @param cn5Numero
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean existeMateriaisClassificacoesPorConsumoSinteticoMaterial(Integer codigoMaterial, final Long cn5Numero) {

		if (cn5Numero == null) {
			/*
			 * Retorna verdadeiro quando parâmetro cn5Numero for nulo, pois os materiais já foram classificados anteriormente
			 */
			return true;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class);

		criteria.add(Restrictions.eq(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString(), codigoMaterial));

		Long intervalo1ResultadoDecodes = 0L;
		if (cn5Numero < 100000000000L) {
			// Realiza o decode necessário no PRIMEIRO intervalo
			Long intervalo1Decode1 = this.decodificaIntervalos(cn5Numero, 2, 9, "000000000", 9999999999L, true);
			Long intervalo1Decode2 = intervalo1Decode1 != null ? intervalo1Decode1 : this.decodificaIntervalos(cn5Numero, 4, 7, "0000000", 99999999L, true);
			Long intervalo1Decode3 = intervalo1Decode2 != null ? intervalo1Decode2 : this.decodificaIntervalos(cn5Numero, 6, 5, "00000", 999999L, true);
			Long intervalo1Decode4 = intervalo1Decode3 != null ? intervalo1Decode3 : this.decodificaIntervalos(cn5Numero, 8, 3, "000", 9999L, true);
			Long intervalo1Decode5 = intervalo1Decode4 != null ? intervalo1Decode4 : this.decodificaIntervalos(cn5Numero, 10, 1, "0", 99L, true);
			intervalo1ResultadoDecodes = intervalo1Decode5 != null ? intervalo1Decode5 : 0L;
		}
		
		Criterion criterioIntervalo1 = null;
		// Critérios necessário para o PRIMEIRO intervalo
		if(cn5Numero < intervalo1ResultadoDecodes){
			criterioIntervalo1 = Restrictions.between(ScoMateriaisClassificacoes.Fields.CN5.toString(), cn5Numero, intervalo1ResultadoDecodes);
		} else{
			criterioIntervalo1 = Restrictions.between(ScoMateriaisClassificacoes.Fields.CN5.toString(), intervalo1ResultadoDecodes, cn5Numero);
		}


		Long intervalo2ResultadoDecodes = 0L;
		if (cn5Numero > 99999999999L) {
			// Realiza o decode necessário no SEGUNDO intervalo
			Long intervalo2Decode1 = this.decodificaIntervalos(cn5Numero, 2, 10, "0000000000", 9999999999L, false);
			Long intervalo2Decode2 = intervalo2Decode1 != null ? intervalo2Decode1 : this.decodificaIntervalos(cn5Numero, 4, 8, "00000000", 99999999L, false);
			Long intervalo2Decode3 = intervalo2Decode2 != null ? intervalo2Decode2 : this.decodificaIntervalos(cn5Numero, 6, 6, "000000", 999999L, false);
			Long intervalo2Decode4 = intervalo2Decode3 != null ? intervalo2Decode3 : this.decodificaIntervalos(cn5Numero, 8, 4, "0000", 9999L, false);
			Long intervalo2Decode5 = intervalo2Decode4 != null ? intervalo2Decode4 : this.decodificaIntervalos(cn5Numero, 10, 2, "00", 99L, false);
			intervalo2ResultadoDecodes = intervalo2Decode5 != null ? intervalo2Decode5 : 0L;
		}
		
		Criterion criterioIntervalo2 = null;
		// Critérios necessário para o SEGUNDO intervalo
		if(cn5Numero < intervalo2ResultadoDecodes){
			criterioIntervalo2 = Restrictions.between(ScoMateriaisClassificacoes.Fields.CN5.toString(), cn5Numero, intervalo2ResultadoDecodes);
		} else{
			criterioIntervalo2 = Restrictions.between(ScoMateriaisClassificacoes.Fields.CN5.toString(), intervalo2ResultadoDecodes, cn5Numero);
		}

		// Considera AMBOS os intervalos de classificação
		Criterion criteriaIntervalos = Restrictions.or(criterioIntervalo1, criterioIntervalo2);
		criteria.add(criteriaIntervalos);

		return executeCriteriaCount(criteria) > 0;

	}

	/**
	 * Decodifica intervalos de classificação para verificação do relatório de consumo sintético
	 * 
	 * @param cn5Numero
	 * @param posicao
	 * @param quantidadePosicoes
	 * @param valorEsperado
	 * @param valorRetorno
	 * @return
	 */
	protected Long decodificaIntervalos(final long cn5Numero, final int posicao, final int quantidadePosicoes, final String valorEsperado, final Long valorRetorno,
			boolean isSomarClassificacao) {

		Long retorno = null;

		Long calculoCn5 = isSomarClassificacao ? (cn5Numero + 100000000000L) : cn5Numero;

		String stringCalculoCn5 = calculoCn5.toString();

		String subStringCalculoCn5 = stringCalculoCn5.substring(posicao, posicao + quantidadePosicoes);

		if (valorEsperado.equalsIgnoreCase(subStringCalculoCn5)) {
			retorno = valorRetorno;
		}

		return retorno;

	}
	
	public Boolean verificarComprasWeb(AghParametros param, ScoMaterial material) {
		if (param == null || material == null) {
			return false;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class, "MC");
		criteria.add(Restrictions.eq(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString(), material.getCodigo()));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoClassifMatNiv1.class, "CMN1");
		subQuery.setProjection(Projections.property("CMN1."+ScoClassifMatNiv1.Fields.GMT_CODIGO.toString()));
		subQuery.add(Restrictions.eq("CMN1."+ScoClassifMatNiv1.Fields.DESCRICAO.toString(), param.getVlrTexto()));
		if (isOracle()) {
			subQuery.add(Restrictions.sqlRestriction("TO_NUMBER(SUBSTR(LPAD(TO_CHAR(THIS_.CN5_NUMERO),12,'0'),1,2)) = {alias}.GMT_CODIGO"));
			subQuery.add(Restrictions.sqlRestriction("TO_NUMBER(SUBSTR(LPAD(TO_CHAR(THIS_.CN5_NUMERO),12,'0'),3,2)) = {alias}.CODIGO"));
		} else {
			subQuery.add(Restrictions.sqlRestriction("CAST(SUBSTR(LPAD(CAST(THIS_.CN5_NUMERO AS VARCHAR),12,'0'),1,2) AS SMALLINT) = {alias}.GMT_CODIGO"));
			subQuery.add(Restrictions.sqlRestriction("CAST(SUBSTR(LPAD(CAST(THIS_.CN5_NUMERO AS VARCHAR),12,'0'),3,2) AS INTEGER) = {alias}.CODIGO"));
		}
		
		criteria.add(Subqueries.exists(subQuery));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<MaterialClassificacaoVO> listarMateriasPorClassificacao(Long cn5, Integer codGrupo ){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class,"MCL");
		criteria.createAlias("MCL."+ScoMateriaisClassificacoes.Fields.MATERIAL.toString(), "MAT");
		criteria.add(Restrictions.eq("MCL."+ScoMateriaisClassificacoes.Fields.CN5.toString(), cn5));
		criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.GMT_CODIGO.toString(), codGrupo));
		criteria.setProjection(Projections.distinct(Projections.projectionList().
				add(Projections.property("MCL."+ScoMateriaisClassificacoes.Fields.CN5.toString()), MaterialClassificacaoVO.Fields.CN5.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.CODIGO.toString()), MaterialClassificacaoVO.Fields.COD_MATERIAL.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.NOME.toString()), MaterialClassificacaoVO.Fields.NOME_MATERIAL.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), MaterialClassificacaoVO.Fields.UNIDADE.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(MaterialClassificacaoVO.class));
		return executeCriteria(criteria);
		
	}
	
}