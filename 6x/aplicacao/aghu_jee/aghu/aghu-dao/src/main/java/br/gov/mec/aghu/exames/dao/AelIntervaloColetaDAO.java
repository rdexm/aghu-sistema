package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelIntervaloColeta;

/**
 * 
 * @author lalegre
 *
 */
public class AelIntervaloColetaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelIntervaloColeta> {
	
	private static final long serialVersionUID = -6166329356758310624L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelIntervaloColeta.class);
    }
	
	/**
	 * Verifica se existe Intervalo de Coleta
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return
	 */
	public boolean existeItem(String emaExaSigla, Integer emaManSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return (executeCriteriaCount(criteria) > 0);
		
	}
	
	public AelIntervaloColeta obterPeloId(Short codigo) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.SEQ.toString(), codigo));
		return (AelIntervaloColeta) executeCriteriaUniqueResult(criteria);
	}
	
	/*public AelIntervaloColeta obterOriginal(Short seq) {
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelIntervaloColeta.Fields.SEQ.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.EXA_SIGLA.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.MAN_SEQ.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.DESCRICAO.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.NUMERO_COLETAS.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.TEMPO.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.UNIDADE_MEDIDA_TEMPO.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.VOLUME_INGERIDO.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.UNIDADE_MEDIDA_VOLUME.toString());
		hql.append(", o.").append(AelIntervaloColeta.Fields.TIPO_SUBSTANCIA.toString());
		hql.append(" from ").append(AelIntervaloColeta.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelIntervaloColeta.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", seq);
		
		
		AelIntervaloColeta retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new AelIntervaloColeta();

			retorno.setSeq( (Short) campos[0]);
			retorno.setEmaExaSigla((String) campos[1]);
			retorno.setEmaManSeq((Integer) campos[2]);
			retorno.setDescricao((String) campos[3]);
			retorno.setNroColetas((Short) campos[4]);
			retorno.setTempo((Short) campos[5]);
			retorno.setUnidMedidaTempo((DominioUnidadeMedidaTempo) campos[6]);
			retorno.setIndSituacao((DominioSituacao) campos[7]);
			retorno.setVolumeIngerido((Integer) campos[8]);
			retorno.setUnidMedidaVolume((String) campos[9]);
			retorno.setTipoSubstancia((String) campos[10]);
		}		
		
		return retorno;
	}*/
	
	public List<AelIntervaloColeta> listarPorExameMaterial(String siglaExame, Integer codigoMaterial) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.EXA_SIGLA.toString(), siglaExame));
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.MAN_SEQ.toString(), codigoMaterial));
		return executeCriteria(criteria);
	}
	
	public List<AelIntervaloColeta> listarAtivosPorExameMaterial(String siglaExame, Integer codigoMaterial) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.EXA_SIGLA.toString(), siglaExame));
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.MAN_SEQ.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(AelIntervaloColeta.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
}
