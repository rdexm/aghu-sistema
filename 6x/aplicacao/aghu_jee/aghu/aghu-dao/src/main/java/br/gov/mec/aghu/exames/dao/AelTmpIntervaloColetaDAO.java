package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelTmpIntervaloColetaId;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.RapServidores;

public class AelTmpIntervaloColetaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTmpIntervaloColeta> {
	
	private static final long serialVersionUID = -5470830655272968001L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelTmpIntervaloColeta.class);
    }
	
	public AelTmpIntervaloColeta obterPeloId(Short codigoIntervaloColeta, Short codigoTempo) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.ICO_SEQ.toString(), codigoIntervaloColeta));
		criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.SEQP.toString(), codigoTempo));
		return (AelTmpIntervaloColeta) executeCriteriaUniqueResult(criteria);
	}
	
	public AelTmpIntervaloColeta obterOriginal(Short icoSeq, Short seqp) {
		StringBuilder hql = new StringBuilder(100);
		hql.append("select o.").append(AelTmpIntervaloColeta.Fields.TEMPO.toString());
		hql.append(", o.").append(AelTmpIntervaloColeta.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelTmpIntervaloColeta.Fields.SERVIDOR.toString());
		hql.append(" from ").append(AelTmpIntervaloColeta.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelTmpIntervaloColeta.Fields.ICO_SEQ.toString()).append(" = :icoSeq ");
		hql.append(" and o.").append(AelTmpIntervaloColeta.Fields.SEQP.toString()).append(" = :seqp ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("icoSeq", icoSeq);
		query.setParameter("seqp", seqp);
		
		
		AelTmpIntervaloColeta retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new AelTmpIntervaloColeta();

			AelTmpIntervaloColetaId id = new AelTmpIntervaloColetaId(icoSeq, seqp);
			retorno.setId(id);
			retorno.setTempo((Short) campos[0]);
			retorno.setCriadoEm((Date) campos[1]);
			retorno.setServidor((RapServidores) campos[2]);
		}		
		
		return retorno;
	}
	
	public List<AelTmpIntervaloColeta> listarPorIntervaloColeta(Short codigoIntervaloColeta) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.ICO_SEQ.toString(), codigoIntervaloColeta));
		criteria.addOrder(Order.asc(AelTmpIntervaloColeta.Fields.TEMPO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de objetos <br>
	 * de Intervalo Coleta.
	 * 
	 * @param sigla
	 * @param seq
	 * @return
	 */
	public List<AelTmpIntervaloColeta> listarPesquisaIntervaloColeta(String objPesquisa, AelUnfExecutaExames exame) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.createAlias(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA.toString(), 
				AelTmpIntervaloColeta.Fields.INTERVALO_COLETA.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_SEQ.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_DSP_DESCRICAO.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_VOLUME_INGERIDO.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_UND_MED_VOLUME.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_TIPO_SUBSTANCIA.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_TEMPO.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_UND_MED_TEMPO.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_EMA_EXA_SIGLA.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_EMA_MAN_SEQ.toString()))
				.add(Projections.property(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA.toString()))
				.add(Projections.count(AelTmpIntervaloColeta.Fields.INTERVALO_NRO_COLETAS.toString()), "count_nro_coletas")
				
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_SEQ.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_DSP_DESCRICAO.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_VOLUME_INGERIDO.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_UND_MED_VOLUME.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_TIPO_SUBSTANCIA.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_TEMPO.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_UND_MED_TEMPO.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_EMA_EXA_SIGLA.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_EMA_MAN_SEQ.toString()))
				.add(Projections.groupProperty(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA.toString()))
		);
				
		//criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.INTERVALO_EMA_EXA_SIGLA.toString(), sigla));
		if(CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_SEQ.toString(), Short.valueOf(objPesquisa)));
		} else {
			criteria.add(Restrictions.ilike(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_DSP_DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.INTERVALO_UND_IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.INTERVALO_EMA_EXA_SIGLA.toString(), exame.getAelExamesMaterialAnalise().getAelExames().getSigla()));
		criteria.add(Restrictions.eq(AelTmpIntervaloColeta.Fields.INTERVALO_EMA_MAN_SEQ.toString(), exame.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq()));
		
		criteria.addOrder(Order.asc("count_nro_coletas"));
//		criteria.addOrder(Order.asc(AelTmpIntervaloColeta.Fields.INTERVALO_NRO_COLETAS.toString()));
		criteria.addOrder(Order.asc(AelTmpIntervaloColeta.Fields.INTERVALO_COLETA_DSP_DESCRICAO.toString()));
		
		final List<Object[]> result = this.executeCriteria(criteria);
		
		if(result == null || result.isEmpty()) {
			return new ArrayList<AelTmpIntervaloColeta>();
		}
		
		final List<AelTmpIntervaloColeta> intervaloColetaList = new ArrayList<AelTmpIntervaloColeta>();
		AelTmpIntervaloColeta tmpIntervaloColeta = null;
//		AelIntervaloColeta intervaloColeta = null;

		for (Object obj : result) {
//			intervaloColeta = new AelIntervaloColeta();
			tmpIntervaloColeta = new AelTmpIntervaloColeta();
			
//			intervaloColeta.setSeq((Short) ((Object[])obj)[0]);
//			intervaloColeta.setDescricao((String) ((Object[])obj)[1]);
//			intervaloColeta.setVolumeIngerido((Integer) ((Object[])obj)[2]);
//			intervaloColeta.setUnidMedidaVolume((String) ((Object[])obj)[3]);
//			intervaloColeta.setTipoSubstancia((String) ((Object[])obj)[4]);
//			intervaloColeta.setTempo((Short) ((Object[])obj)[5]);
//			intervaloColeta.setUnidMedidaTempo((DominioUnidadeMedidaTempo) ((Object[])obj)[6]);
//			intervaloColeta.setEmaExaSigla((String) ((Object[])obj)[7]);
//			intervaloColeta.setEmaManSeq((Integer) ((Object[])obj)[8]);
//			intervaloColeta.setNroColetas(((Integer) ((Object[])obj)[9]).shortValue());
			
			AelIntervaloColeta interCol = ((AelIntervaloColeta) ((Object[])obj)[9]);
			this.initialize(interCol);
			tmpIntervaloColeta.setIntervaloColeta(interCol);
			intervaloColetaList.add(tmpIntervaloColeta);
		}
		
		return intervaloColetaList;
	}
	
	@Override
	public AelTmpIntervaloColeta obterOriginal(AelTmpIntervaloColeta elemento) {
		if (elemento != null && elemento.getId() != null) {
			AelTmpIntervaloColetaId intervaloId = elemento.getId();
			return this.obterOriginal(intervaloId.getIcoSeq(), intervaloId.getSeqp());
		} else {
			return null;
		}
	}
}
