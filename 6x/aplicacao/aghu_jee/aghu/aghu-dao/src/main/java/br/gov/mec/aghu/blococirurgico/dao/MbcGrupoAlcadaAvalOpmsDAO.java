package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;


public class MbcGrupoAlcadaAvalOpmsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcGrupoAlcadaAvalOpms> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7106356325073584564L;

	public List<MbcGrupoAlcadaAvalOpms> retornaGruoAlcadaAvalPorGrupoSeq(Short grupoSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoAlcadaAvalOpms.class, "GAA");
		criteria.createAlias("GAA." + MbcGrupoAlcadaAvalOpms.Fields.ALCADAS.toString(), "ALC");
		criteria.add(Restrictions.eq("GAA." + MbcGrupoAlcadaAvalOpms.Fields.SEQ.toString(), grupoSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcGrupoAlcadaAvalOpms> validaGrupoEspecialidadeConvenio(MbcGrupoAlcadaAvalOpms item){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoAlcadaAvalOpms.class);
		criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES_SEQ.toString(), item.getAghEspecialidades().getSeq()));
		criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(), item.getTipoConvenio()));
		return executeCriteria(criteria);
	}

}
