package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioAbrangenciaGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelGrupoRecomendacao;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelRecomendacaoExameId;

/**
 * 
 * @author lalegre
 *
 */
public class AelRecomendacaoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRecomendacaoExame> {
	
	private static final long serialVersionUID = -7269947735663203253L;

	@Override
	protected void obterValorSequencialId(AelRecomendacaoExame elemento) {
		
		if (elemento == null || elemento.getExamesMaterialAnalise() == null) {
			throw new IllegalArgumentException("Material de Análise do exame nao foi informado corretamente.");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRecomendacaoExame.class);
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.SIGLA.toString(), elemento.getExamesMaterialAnalise().getId().getExaSigla()));
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.MATERIAL.toString(), elemento.getExamesMaterialAnalise().getId().getManSeq()));
		criteria.setProjection(Projections.max(AelRecomendacaoExame.Fields.SEQP.toString()));

		Integer seqp = (Integer) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		
		AelRecomendacaoExameId id = new AelRecomendacaoExameId();
		id.setEmaExaSigla(elemento.getExamesMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExamesMaterialAnalise().getId().getManSeq());
		id.setSeqp(++seqp);
		
		elemento.setId(id);
		
	}
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRecomendacaoExame.class);
		return criteria;
    }
	
	/**
	 * Retorna AelRecomendacaoExame pelo id
	 * @param id
	 * @return
	 */
	public AelRecomendacaoExame obterAelRecomendacaoExamePorID(AelRecomendacaoExameId id) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.MATERIAL.toString(), id.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.SEQP.toString(), id.getSeqp()));
		return (AelRecomendacaoExame) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * 
	 * @param sigla
	 * @param manSeq
	 * @return
	 */
	public List<AelRecomendacaoExame> obterRecomendacoesExame(String sigla, Integer manSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.MATERIAL.toString(), manSeq));
		return executeCriteria(criteria);
		
	}
	
	/**
	 * 
	 * @param sigla
	 * @param manSeq
	 * @return
	 */
	public List<AelRecomendacaoExame> obterRecomendacoesExameResponsavelP(String sigla, Integer manSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.MATERIAL.toString(), manSeq));
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.RESPONSAVEL.toString(), DominioResponsavelGrupoRecomendacao.P));
		return executeCriteria(criteria);
		
	}
	
	/**
	 * 
	 * @param sigla
	 * @param manSeq
	 * @return
	 */
	public List<AelRecomendacaoExame> obterRecomendacoesExameResponsavelAbrangencia(String sigla, Integer manSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.MATERIAL.toString(), manSeq));
		
		criteria.add(Restrictions.eq(AelRecomendacaoExame.Fields.RESPONSAVEL.toString(), DominioResponsavelGrupoRecomendacao.P));
		List<DominioAbrangenciaGrupoRecomendacao> abr = new ArrayList<DominioAbrangenciaGrupoRecomendacao>();
		abr.add(DominioAbrangenciaGrupoRecomendacao.A);
		abr.add(DominioAbrangenciaGrupoRecomendacao.S);
		criteria.add(Restrictions.in(AelRecomendacaoExame.Fields.ABRANGENCIA.toString(), abr));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Retorna uma lista de AelRecomendacaoExame
	 * 
	 * @param sigla
	 * @param manSeq
	 * @param abrangencia
	 * @return
	 */
	public List<AelRecomendacaoExame> listarRecomendacaoExameResponsavel(
			String sigla, Integer manSeq, DominioOrigemAtendimento origem) {

		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(
				AelRecomendacaoExame.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(
				AelRecomendacaoExame.Fields.MATERIAL.toString(), manSeq));
		
		criteria.add(Restrictions.or(Restrictions.eq(
				AelRecomendacaoExame.Fields.ABRANGENCIA.toString(),
				DominioAbrangenciaGrupoRecomendacao.S),
				Restrictions.eq(
						AelRecomendacaoExame.Fields.ABRANGENCIA.toString(),
						origem)));

		return executeCriteria(criteria);

	}

}
