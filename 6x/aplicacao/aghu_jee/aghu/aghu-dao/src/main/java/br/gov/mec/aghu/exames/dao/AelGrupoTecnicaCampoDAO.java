package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelGrupoTecnicaCampo;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampoId;

public class AelGrupoTecnicaCampoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoTecnicaCampo> {

	
	
	private static final long serialVersionUID = 7331038867252797160L;


	@Override
	protected void obterValorSequencialId(AelGrupoTecnicaCampo elemento) {
		
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		AelGrupoTecnicaCampoId id = new AelGrupoTecnicaCampoId();
		id.setTceUfeEmaExaSigla(elemento.getGrupoTecnicaUnfExames().getId().getUfeEmaExaSigla());
		id.setTceUfeEmaManSeq(elemento.getGrupoTecnicaUnfExames().getId().getUfeEmaManSeq());
		id.setTceGrtSeq(elemento.getGrupoTecnicaUnfExames().getId().getGrtSeq());
		id.setTceUfeUnfSeq(elemento.getGrupoTecnicaUnfExames().getId().getUfeUnfSeq());
		id.setCalSeq(elemento.getCampoLaudo().getSeq());
		
		elemento.setId(id);
	}
	
	
	/**
	 * Retorna uma lista de registros<br>
	 * da tabela AEL_GRP_TECNICA_CAMPOS <br>
	 * conforme filtros de pesquisa.
	 * 
	 * @param grtSeq
	 * @param ufeEmaExaSigla
	 * @param ufeEmaManSeq
	 * @param ufeUnfSeq
	 * @return
	 */
	public List<AelGrupoTecnicaCampo> pesquisarAelGrupoTecnicaCampo(Integer grtSeq, String ufeEmaExaSigla, Short ufeUnfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoTecnicaCampo.class);
		criteria.createCriteria(AelGrupoTecnicaCampo.Fields.CAMPO_LAUDO.toString());
		
		criteria = this.obterCriteriaConsultaPesquisarAelGrupoTecnicaCampo(criteria, grtSeq, ufeEmaExaSigla, ufeUnfSeq);
		
		return this.executeCriteria(criteria);
	}
	
	
	private DetachedCriteria obterCriteriaConsultaPesquisarAelGrupoTecnicaCampo(DetachedCriteria criteria, Integer grtSeq, String ufeEmaExaSigla, Short ufeUnfSeq){
		if(grtSeq != null) {
			criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.TCE_GRT_SEQ.toString(), grtSeq));
		}
		if(StringUtils.isNotBlank(ufeEmaExaSigla)){
			criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.TCE_UFE_EMA_EXA_SIGLA.toString(), ufeEmaExaSigla));
		}
		if(ufeUnfSeq != null) {
			criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.TCE_UFE_UNF_SEQ.toString(), ufeUnfSeq));
		}
		
		return criteria;
	}
	
	
	/**
	 * Retorna uma lista de registros<br>
	 * da tabela AEL_GRP_TECNICA_CAMPOS <br>
	 * por exames do grupo exame. 
	 * 
	 * @param grtSeq
	 * @param ufeEmaExaSigla
	 * @param ufeEmaManSeq
	 * @param ufeUnfSeq
	 * @return
	 */
	public List<AelGrupoTecnicaCampo> listarAelGrupoTecnicaCampoPorExamesDoGrupo(
			Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoTecnicaCampo.class);
		
		criteria = this.obterCriterioConsulta(criteria, grtSeq, ufeEmaExaSigla, 
				ufeEmaManSeq, ufeUnfSeq);
		
		return this.executeCriteria(criteria);
	}
	
	protected DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq) {
		criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.TCE_GRT_SEQ.toString(), grtSeq));
		criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.TCE_UFE_EMA_EXA_SIGLA.toString(), ufeEmaExaSigla));
		criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.TCE_UFE_EMA_MAN_SEQ.toString(), ufeEmaManSeq));
		criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.TCE_UFE_UNF_SEQ.toString(), ufeUnfSeq));
		
		return criteria;
	}
	
	
	public AelGrupoTecnicaCampo verificarUnicoAelGrupoTecnicaCampo(Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq, Integer calSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoTecnicaCampo.class);
		
		criteria = this.obterCriterioConsulta(criteria, grtSeq, ufeEmaExaSigla, 
				ufeEmaManSeq, ufeUnfSeq);
		criteria.add(Restrictions.eq(AelGrupoTecnicaCampo.Fields.CAL_SEQ.toString(), calSeq));
		
		return (AelGrupoTecnicaCampo) executeCriteriaUniqueResult(criteria);
	}
	
}
