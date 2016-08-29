package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamTipoReceitProcessos;

public class MamTipoReceitProcessosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTipoReceitProcessos> {
	
	private static final long serialVersionUID = 7988194222617972819L;

	public List<MamTipoReceitProcessos> listarMamTipoReceitProcessosPorCodigoReceituarioCuidados(Short vTpRecCuidados){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoReceitProcessos.class);
		criteria.add(Restrictions.ne(MamTipoReceitProcessos.Fields.TER_SEQ.toString(), vTpRecCuidados));
		criteria.add(Restrictions.eq(MamTipoReceitProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
			
		return executeCriteria(criteria);
			
	}
	
	//Consulta para a F1
	public List<MamTipoReceitProcessos> listarMamTipoReceitProcessosCodigo(Short vTpRecCuidados){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoReceitProcessos.class);
			
		criteria.add(Restrictions.ne(MamTipoReceitProcessos.Fields.TER_SEQ.toString(), vTpRecCuidados));
		criteria.add(Restrictions.eq(MamTipoReceitProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
	
			
		return executeCriteria(criteria);
			
	}
	
}
