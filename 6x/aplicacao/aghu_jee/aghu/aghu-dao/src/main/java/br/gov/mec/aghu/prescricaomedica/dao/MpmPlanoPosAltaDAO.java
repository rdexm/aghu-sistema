package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class MpmPlanoPosAltaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPlanoPosAlta> {

	
	private static final long serialVersionUID = -7567760803160825444L;
	
	private static final Log LOG = LogFactory.getLog(MpmPlanoPosAltaDAO.class);

	public enum PlanosPosAltaCRUDExceptionCode implements BusinessExceptionCode {
		MPM_PLANO_POS_ALTA1, ERRO_REMOVER_PLANO_POS_ALTA, ERRO_REMOVER_PLANO_POS_ALTA1,MPM_APL_PLA_FK1;
	}
	
	/**
	 * Obtém um plano pos alta pelo seu ID.
	 * 
	 * bsoliveira - 01/11/2010
	 * 
	 * @param {Integer} seq
	 * 
	 * @return {MpmPlanoPosAlta}
	 */
	public MpmPlanoPosAlta obterPlanoPosAltaPeloId(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPlanoPosAlta.class);
		criteria.add(Restrictions.eq(MpmPlanoPosAlta.Fields.SEQ.toString(), seq.shortValue()));
		MpmPlanoPosAlta retorno = (MpmPlanoPosAlta) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;

	}

	/**
	 * Método responsável pelo count de registros de planos de pos alta
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @param unidadeFuncional
	 * @return
	 */
	public Long pesquisarPlanosPosAltaCount(Integer codigoPlano, String descricaoPlano, 
			DominioSituacao situacaoPlano){

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPlanoPosAlta.class);
		if(codigoPlano != null){
			criteria.add(Restrictions.eq(MpmPlanoPosAlta.Fields.SEQ.toString(), codigoPlano.shortValue()));
		}
		if(situacaoPlano != null){
			criteria.add(Restrictions.eq(MpmPlanoPosAlta.Fields.IND_SITUACAO.toString(), situacaoPlano));
		}
		if(descricaoPlano != null && StringUtils.isNotBlank(descricaoPlano)){
			criteria.add(Restrictions.ilike(MpmPlanoPosAlta.Fields.DESCRICAO.toString(), descricaoPlano.toUpperCase(), MatchMode.ANYWHERE));
		}

		return this.executeCriteriaCount(criteria);
	}
	
	public List<MpmPlanoPosAlta> pesquisarPlanosPosAlta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoPlano,
			String descricaoPlano,
			DominioSituacao situacaoPlano){

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPlanoPosAlta.class);
		
		criteria.createAlias(MpmPlanoPosAlta.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		if(codigoPlano != null){
			criteria.add(Restrictions.eq(MpmPlanoPosAlta.Fields.SEQ.toString(), codigoPlano.shortValue()));
		}
		if(situacaoPlano != null){
			criteria.add(Restrictions.eq(MpmPlanoPosAlta.Fields.IND_SITUACAO.toString(), situacaoPlano));
		}
		if(descricaoPlano != null && StringUtils.isNotBlank(descricaoPlano)){
			criteria.add(Restrictions.ilike(MpmPlanoPosAlta.Fields.DESCRICAO.toString(), descricaoPlano.toUpperCase(), MatchMode.ANYWHERE));
		}
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	/**
	 * Método responsável pela remoção de um Plano de Pós Alta.
	 *
	 * @dbtables MPM_PLANO_POS_ALTAS delete
	 * 
	 * @param planoPosAlta
	 * @throws ApplicationBusinessException
	 */
	
	public void removerPlano(MpmPlanoPosAlta planoPosAlta)throws ApplicationBusinessException {
		try {
			this.remover(planoPosAlta);
			this.flush();
		} catch (PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(PlanosPosAltaCRUDExceptionCode.ERRO_REMOVER_PLANO_POS_ALTA1);
		}
	}

	/**
	 * Busca em MPM_PLANO_POS_ALTA.<br>
	 * Entidade: <code>MpmPlanoPosAlta</code><br>
	 * 
	 * @return
	 */
	public List<MpmPlanoPosAlta> listaPlanoPosAlta() {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPlanoPosAlta.class);
		criteria.add(Restrictions.eq(MpmPlanoPosAlta.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return this.executeCriteria(criteria);
	}
		
	/**
	 * Verificar a existência de registros de tipo item de dieta em outras entidades
	 * @param tipoDieta
	 * @param class1
	 * @param field
	 * @return
	 */
	public boolean existeItemPlanoPosAlta(MpmPlanoPosAlta planoPosAlta, Class class1, Enum field) {

		if (planoPosAlta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(),planoPosAlta));
		
		return (executeCriteriaCount(criteria) > 0);
	}	
}