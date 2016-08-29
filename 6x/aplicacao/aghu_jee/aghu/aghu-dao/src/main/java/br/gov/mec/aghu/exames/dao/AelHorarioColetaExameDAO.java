package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelExameHorarioColetaId;


public class AelHorarioColetaExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameHorarioColeta> {

	
	private static final long serialVersionUID = 4083815211012925756L;

	@Override
	public void obterValorSequencialId(AelExameHorarioColeta elemento) {
		
		if (elemento.getExamesMaterialAnalise() == null ) {
			
			throw new IllegalArgumentException("Horário de Coleta nao está associado corretamente a nenhum material de análise.");
			
		}
		
		String sigla = elemento.getExamesMaterialAnalise().getId().getExaSigla();
		Integer manSeq = elemento.getExamesMaterialAnalise().getId().getManSeq();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameHorarioColeta.class);
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.MATERIAL.toString(), manSeq));
		criteria.setProjection(Projections.max(AelExameHorarioColeta.Fields.SEQP.toString()));

		Integer seqp = (Integer) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		
		AelExameHorarioColetaId id = new AelExameHorarioColetaId(sigla, manSeq,seqp);
		
		id.setSeqp(++seqp);

		
		elemento.setId(id);
		
		
	}	
	
	public List<AelExameHorarioColeta> listaHorariosColetaExames(String sigla, Integer manSeq) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameHorarioColeta.class);
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.MATERIAL.toString(), manSeq));
		return executeCriteria(criteria);
		
	
	}
	
	
	
	
	
	
}
