package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.AelExamesProvaId;

/**
 * 
 * @author lalegre
 *
 */
public class AelExamesProvaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesProva> {
	
	private static final long serialVersionUID = -7041548810584445512L;

	@Override
	protected void obterValorSequencialId(AelExamesProva elemento) {
		
		if (elemento == null || elemento.getExamesMaterialAnalise() == null || elemento.getExamesMaterialAnaliseEhProva() == null) {
			throw new IllegalArgumentException("Material de Análise do exame nao foi informado corretamente.");
		}
		
		AelExamesProvaId id = new AelExamesProvaId();
		id.setEmaExaSigla(elemento.getExamesMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExamesMaterialAnalise().getId().getManSeq());
		id.setEmaExaSiglaEhProva(elemento.getExamesMaterialAnaliseEhProva().getId().getExaSigla());
		id.setEmaManSeqEhProva(elemento.getExamesMaterialAnaliseEhProva().getId().getManSeq());
		
		elemento.setId(id);
		
	}
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesProva.class);
		return criteria;
    }
	
	/**
	 * Retorna AelExamesProva pelo Id
	 * @param id
	 * @return
	 */
	public AelExamesProva obterAelExamesProvaPorId(AelExamesProvaId id) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_MAN_SEQ.toString(), id.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_EXA_SIGLA_EH_PROVA.toString(), id.getEmaExaSiglaEhProva()));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_MAN_SEQ_EH_PROVA.toString(), id.getEmaManSeqEhProva()));

		List<AelExamesProva> examesProva = executeCriteria(criteria);
		if (examesProva.isEmpty()) {	
			return null;
		}
		
		return examesProva.get(0);
		
	}
	
	/**
	 * Retorna o número de registros cadastrados
	 * @param sigla
	 * @param siglaEhProva
	 * @param manSeq
	 * @param manSeqEhProva
	 * @return
	 */
	public Long obterCountExamesProva(String sigla, String siglaEhProva, Integer manSeq, Integer manSeqEhProva) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_EXA_SIGLA_EH_PROVA.toString(), siglaEhProva));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_MAN_SEQ_EH_PROVA.toString(), manSeqEhProva));
		
		return executeCriteriaCount(criteria);
		
	}
	
	/**
	 * Retorna lista de exames prova cadastrados para um material
	 * @param sigla
	 * @param manSeq
	 * @return
	 */
	public List<AelExamesProva> obterAelExamesProva(String sigla, Integer manSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		
		criteria.createAlias(AelExamesProva.Fields.EXAMES_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), "MAT", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelExamesProva.Fields.EXAMES_MATERIAL_ANALISE_EH_PROVA.toString(), "EMA_EPR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA_EPR.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), "EXA_EPR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA_EPR.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), "MAT_EPR", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_MAN_SEQ.toString(), manSeq));
		
		return executeCriteria(criteria);
		
	}
	
	public List<AelExamesProva> buscarProvasExameSolicitado(String sigla, Integer manSeq) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.IND_CONSISTE.toString(), true));
		criteria.add(Restrictions.eq(AelExamesProva.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
}
