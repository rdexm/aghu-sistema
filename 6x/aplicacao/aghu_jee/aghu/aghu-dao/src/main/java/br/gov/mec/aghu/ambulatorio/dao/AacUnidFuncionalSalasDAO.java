package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUnidadeFuncionalSala;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AacUnidFuncionalSalasId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @author sneves
 *
 */
public class AacUnidFuncionalSalasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacUnidFuncionalSalas> {

	
	
	private static final long serialVersionUID = 2396224600940936986L;

	/**
	 * Obtem uma sala de unidade funcional através dos atributos da ID
	 * @param unfSeq
	 * @param sala
	 * @return instância de br.gov.mec.aghu.model.AacUnidFuncionalSalas
	 */
	public AacUnidFuncionalSalas obterUnidFuncionalSalasPeloId(Short unfSeq, Byte sala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacUnidFuncionalSalas.class);
		criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.SALA.toString(), sala));
		return (AacUnidFuncionalSalas) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem uma sala de unidade funcional através da ID
	 * @param id instância de br.gov.mec.aghu.model.AacUnidFuncionalSalasId
	 * @return instância de br.gov.mec.aghu.model.AacUnidFuncionalSalas
	 */
	public AacUnidFuncionalSalas obterUnidFuncionalSalasPeloId(AacUnidFuncionalSalasId id) {
		return obterUnidFuncionalSalasPeloId(id.getUnfSeq(),id.getSala());
	}
	
	/** Lista salas filtrando pelos parâmetros unidadeFuncional, sala, tipo e situacao
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unidadeFuncional
	 * @param sala
	 * @param tipo
	 * @param situacao
	 * @return
	 */
	public List<AacUnidFuncionalSalas> listarSalasPorUnidadeFuncionalSalaTipoSituacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short unidadeFuncional, Byte sala,
			DominioTipoUnidadeFuncionalSala tipo, DominioSituacao situacao) {
		
		DetachedCriteria criteria = montaCriteriaSalas(unidadeFuncional, sala, tipo, situacao);
		criteria.addOrder(Order.asc(AacUnidFuncionalSalas.Fields.SALA.toString()));
		criteria.addOrder(Order.asc(AacUnidFuncionalSalas.Fields.UNF_SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long listarSalasPorUnidadeFuncionalSalaTipoSituacaoCount(
			Short unidadeFuncional, Byte sala,
			DominioTipoUnidadeFuncionalSala tipo, DominioSituacao situacao) {
		
		DetachedCriteria criteria = montaCriteriaSalas(unidadeFuncional, sala, tipo, situacao);
		return executeCriteriaCount(criteria);
	}
	
	protected DetachedCriteria montaCriteriaSalas(Short unidadeFuncional, Byte sala,
			DominioTipoUnidadeFuncionalSala tipo, DominioSituacao situacao) {
		
		DetachedCriteria criteria =  DetachedCriteria.forClass(AacUnidFuncionalSalas.class);
		criteria.createAlias(AacUnidFuncionalSalas.Fields.UNIDADE_FUNCIONAL.toString(), "UNI_FUNC");
		
		criteria.createAlias(AacUnidFuncionalSalas.Fields.RAP_SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacUnidFuncionalSalas.Fields.RAP_SERVIDOR_PESSOA_FISICA.toString(), "SER_PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AacUnidFuncionalSalas.Fields.RAP_SERVIDOR_ALTERADO.toString(), "SER_ALTERADO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacUnidFuncionalSalas.Fields.RAP_SERVIDOR_ALTERADO_PESSOA_FISICA.toString(), "SER_ALTERADO_PF", JoinType.LEFT_OUTER_JOIN);
		
		
		if (unidadeFuncional != null){
			criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.UNF_SEQ.toString(), unidadeFuncional));
		}
		if (sala != null){
			criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.SALA.toString(), sala));
		}
		if (tipo != null){
			criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.TIPO.toString(), tipo));
		}
		if (situacao != null){
			criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}	
	
	/**
	 * Obtem uma lista distinta de salas existentes
	 * @param sala
	 */
	public List<AacUnidFuncionalSalas> obterListaSalasPeloNumeroSala(Object sala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacUnidFuncionalSalas.class);
		
		if (CoreUtil.isNumeroByte(sala)) {
			criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.SALA.toString(), Byte.valueOf(sala.toString())));
		}
		
		criteria.addOrder(Order.asc(AacUnidFuncionalSalas.Fields.SALA.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem a lista de salas de uma Unidade Funcional
	 * @param unidadeFuncional, param
	 */
	public List<AacUnidFuncionalSalas> obterListaSalasPorUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional, Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacUnidFuncionalSalas.class);
		
		if (CoreUtil.isNumeroByte(param)) {
			criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.SALA.toString(), Byte.valueOf(param.toString())));
		}
		criteria.add(Restrictions.eq(AacUnidFuncionalSalas.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		
		criteria.addOrder(Order.asc(AacUnidFuncionalSalas.Fields.SALA.toString()));
		return executeCriteria(criteria);
	}
}
