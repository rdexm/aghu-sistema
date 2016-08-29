package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AelCopiaResultados;
import br.gov.mec.aghu.model.AelCopiaResultadosId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.FatConvenioSaude;

/**
 * 
 * @author lalegre
 *
 */
public class AelCopiaResultadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCopiaResultados> {
	
	
	
	private static final long serialVersionUID = 4613978908185254841L;

	public AelCopiaResultados obterAelCopiaResultadoOriginal(AelCopiaResultados aelCopiaResultados) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.CNV_CODIGO.toString(), aelCopiaResultados.getId().getCnvCodigo()));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_MAN_SEQ.toString(), aelCopiaResultados.getId().getEmaManSeq()));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_EXA_SIGLA.toString(), aelCopiaResultados.getId().getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.ORIGEM_ATENDIMENTO.toString(), aelCopiaResultados.getId().getOrigemAtendimento()));
		
		return (AelCopiaResultados) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Verifica se existe item de acordo com os parametros passados
	 * @param cnvCodigo
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param origemAtendimento
	 * @return
	 */
	public boolean existeItem(Short cnvCodigo, String emaExaSigla, Integer emaManSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.ne(AelCopiaResultados.Fields.ORIGEM_ATENDIMENTO.toString(), DominioOrigemAtendimento.T));
		return (executeCriteriaCount(criteria) > 0);
		
	}
	
	/**
	 * Verifica se existe item de acordo com os parametros passados
	 * @param cnvCodigo
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param origemAtendimento
	 * @return
	 */
	public boolean existeItemOrigemAtendimentoTodos(Short cnvCodigo, String emaExaSigla, Integer emaManSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.ORIGEM_ATENDIMENTO.toString(), DominioOrigemAtendimento.T));
		return (executeCriteriaCount(criteria) > 0);
		
	}
	
	public List<AelCopiaResultados> obterAelCopiaResultados(AelCopiaResultadosId id) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.CNV_CODIGO.toString(), id.getCnvCodigo()));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_MAN_SEQ.toString(), id.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.ORIGEM_ATENDIMENTO.toString(), id.getOrigemAtendimento()));
		
		return executeCriteria(criteria);
		
	}
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelCopiaResultados.class);
    }
	
	/**
	 * Pesquisa Cópia de Resultados por Exame Material de Análise
	 * @param exameMaterialAnalise
	 * @return
	 */
	public List<AelCopiaResultados> pesquisarCopiaResultadosPorExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise) {
		return this.pesquisarCopiaResultadosPorExameMaterialAnalise(exameMaterialAnalise.getId().getExaSigla(), exameMaterialAnalise.getId().getManSeq());
	}
	
	/**
	 * Pesquisa Cópia de Resultados por Exame Material de Análise
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return
	 */
	public List<AelCopiaResultados> pesquisarCopiaResultadosPorExameMaterialAnalise(String emaExaSigla, Integer emaManSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelCopiaResultados.Fields.CONVENIO_SAUDE.toString(), "cos", DetachedCriteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelCopiaResultados.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.isNotNull("cos."+FatConvenioSaude.Fields.CODIGO.toString()));
		return executeCriteria(criteria);
		
	}
	
}
