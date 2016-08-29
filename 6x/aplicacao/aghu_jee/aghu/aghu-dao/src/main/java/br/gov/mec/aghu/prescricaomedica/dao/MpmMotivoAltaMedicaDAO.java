package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmMotivoAltaMedicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmMotivoAltaMedica> {

	private static final long serialVersionUID = 3069306645025906984L;

	/**
	 * Obtém um motivo alta médica pelo seu ID.
	 * 
	 * bsoliveira - 01/11/2010
	 * 
	 * @param {Short} seq
	 * 
	 * @return {MpmMotivoAltaMedica}
	 */
	public MpmMotivoAltaMedica obterMotivoAltaMedicaPeloId(Short seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmMotivoAltaMedica.class);
		criteria.add(Restrictions.eq(
				MpmMotivoAltaMedica.Fields.SEQ.toString(), seq));
		MpmMotivoAltaMedica retorno = (MpmMotivoAltaMedica) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;

	}
	
	/**
	 * Retorna uma lista de motivo alta médica 
	 * 
	 * @author gfmenezes
	 * 
	 * @since 09/12/2010
	 * 
	 * @return
	 */
	public List<MpmMotivoAltaMedica> listMotivoAltaMedica() {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmMotivoAltaMedica.class);
		criteria.add(Restrictions.eq(
				MpmMotivoAltaMedica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(
				MpmMotivoAltaMedica.Fields.IND_OBITO.toString(), false));
		
		return this.executeCriteria(criteria);
	}
	
	 /** Retorna uma lista de motivo alta médica - Óbito.
	 * @author fbarra
	 * @return List<MpmMotivoAltaMedica> 
	 */
	public List<MpmMotivoAltaMedica> listMotivoAltaMedicaObito() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmMotivoAltaMedica.class);
		criteria.add(Restrictions.eq(MpmMotivoAltaMedica.Fields.IND_OBITO.toString(), true));
		
		return this.executeCriteria(criteria);
	}
	
	public Long pesquisarMotivoAltaMedicaCount(Integer codigo, String descricao,String sigla, 
			DominioSituacao situacao){

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmMotivoAltaMedica.class);
		if(codigo != null){
			criteria.add(Restrictions.eq(MpmMotivoAltaMedica.Fields.SEQ.toString(), codigo.shortValue()));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(MpmMotivoAltaMedica.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(descricao != null && StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(MpmMotivoAltaMedica.Fields.DESCRICAO.toString(), descricao.toUpperCase(), MatchMode.ANYWHERE));
		}
		
		if(sigla != null && StringUtils.isNotBlank(sigla)){
			criteria.add(Restrictions.ilike(MpmMotivoAltaMedica.Fields.SIGLA.toString(), sigla.toUpperCase(), MatchMode.ANYWHERE));
		}

		return this.executeCriteriaCount(criteria);
	}
	

	public Long pesquisarMotivoAltaMedicaSiglaCount(String sigla) {
		Long retorno = null;
		if(sigla != null && StringUtils.isNotBlank(sigla)){
				
			DetachedCriteria criteria = DetachedCriteria
					.forClass(MpmMotivoAltaMedica.class);
		
				criteria.add(Restrictions.ilike(MpmMotivoAltaMedica.Fields.SIGLA.toString(), sigla.toUpperCase(), MatchMode.ANYWHERE));
			
			retorno = this.executeCriteriaCount(criteria);
		}
		return retorno;
	}
	
	public List<MpmMotivoAltaMedica> pesquisarMotivoAltaMedica(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo,
			String descricao,
			String sigla,
			DominioSituacao situacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmMotivoAltaMedica.class);
		
		criteria.createAlias(MpmMotivoAltaMedica.Fields.SERVIDOR.toString(), "SER1", JoinType.INNER_JOIN);
		criteria.createAlias("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		if(codigo != null){
			criteria.add(Restrictions.eq(MpmMotivoAltaMedica.Fields.SEQ.toString(), codigo.shortValue()));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(MpmMotivoAltaMedica.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(descricao != null && StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(MpmMotivoAltaMedica.Fields.DESCRICAO.toString(), descricao.toUpperCase(), MatchMode.ANYWHERE));
		}
		if(sigla != null && StringUtils.isNotBlank(sigla)){
			criteria.add(Restrictions.ilike(MpmMotivoAltaMedica.Fields.SIGLA.toString(), sigla.toUpperCase(), MatchMode.ANYWHERE));
		}
		
		if (orderProperty == null) {
			orderProperty = MpmMotivoAltaMedica.Fields.DESCRICAO.toString();
		}	
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	
	/**
	 * Método responsável pela remoção de um motivo alta médica
	 *
	 * @dbtables MPM_MOTIVO_ALTA_MEDICAS delete
	 * 
	 * @param motivoAltaMedica
	 * @throws ApplicationBusinessException
	 */
	
	public void removerMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica)throws ApplicationBusinessException {
			this.remover(motivoAltaMedica);
			this.flush();
	
	}	
	
	public boolean existeItemMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica, Class class1, Enum field) {

		if (motivoAltaMedica == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(),motivoAltaMedica));
		
		return (executeCriteriaCount(criteria) > 0);
	}	

}
