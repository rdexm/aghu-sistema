package br.gov.mec.aghu.paciente.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioOcorrenciaPOL;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;

public class AipLogProntOnlinesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipLogProntOnlines> {

	private static final long serialVersionUID = -3021926768546972741L;


	public List<AipLogProntOnlines> listarLogsProntOnlinesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipLogProntOnlines.class);

		criteria.add(Restrictions.eq(AipLogProntOnlines.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<AipLogProntOnlines> listarLogProntOnlines(Integer pacCodigo, Date dthrInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipLogProntOnlines.class);
		criteria.add(Restrictions.eq(AipLogProntOnlines.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(AipLogProntOnlines.Fields.CRIADO_EM.toString(), dthrInicio));
		
		return executeCriteria(criteria);
	}
	
	
	public List<AipLogProntOnlines> pesquisarLogPorServidorProntuarioDatasOcorrencia(RapServidores servidor, Integer prontuario, 
																					 Date dataInicio, Date dataFim, DominioOcorrenciaPOL ocorrencia, 
																					 Integer firstResult, Integer maxResults, String orderProperty, Boolean asc){
		
		DetachedCriteria criteria = montarCriteriaPorDatasOcorrencia(servidor, prontuario, dataInicio, dataFim, ocorrencia);
		criteria.createAlias(AipLogProntOnlines.Fields.SERVIDOR.toString(), "SE", JoinType.INNER_JOIN);
		criteria.createAlias("SE."+RapServidores.Fields.PESSOA_FISICA.toString(), "SE_PF", JoinType.INNER_JOIN);
		
		criteria.addOrder(Order.asc(AipLogProntOnlines.Fields.CRIADO_EM.toString()));
		criteria.addOrder(Order.asc("PAC." + AipPacientes.Fields.PRONTUARIO.toString()));
		
		return executeCriteria(criteria,firstResult,maxResults,null,true);
	}
	
	public Long pesquisarLogPorServidorProntuarioDatasOcorrenciaCount(RapServidores servidor, Integer prontuario, Date dataInicio, Date dataFim, DominioOcorrenciaPOL ocorrencia){
		DetachedCriteria criteria = montarCriteriaPorDatasOcorrencia(servidor, prontuario, dataInicio, dataFim, ocorrencia);
		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * Monta criteria para pesquisa "pesquisarLogPorServidorProntuarioDatasOcorrencia"
	 */
	private DetachedCriteria montarCriteriaPorDatasOcorrencia(RapServidores servidor,Integer prontuario, Date dataInicio, Date dataFim, DominioOcorrenciaPOL ocorrencia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipLogProntOnlines.class, "LPOL");
		criteria.createAlias("LPOL."+AipLogProntOnlines.Fields.PACIENTE.toString(), "PAC");
		ProjectionList p = Projections.projectionList();
		
		p.add(Projections.property("LPOL." + AipLogProntOnlines.Fields.CRIADO_EM.toString()), AipLogProntOnlines.Fields.CRIADO_EM.toString());
		p.add(Projections.property("LPOL." + AipLogProntOnlines.Fields.MACHINE.toString()), AipLogProntOnlines.Fields.MACHINE.toString());
		p.add(Projections.property("LPOL." + AipLogProntOnlines.Fields.OCORRENCIA.toString()), AipLogProntOnlines.Fields.OCORRENCIA.toString());
		
		
		if(dataInicio != null && dataFim != null){
			criteria.add(Restrictions.between("LPOL." + AipLogProntOnlines.Fields.CRIADO_EM.toString(), dataInicio, dataFim));
		}
		if(prontuario != null){
			criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		if(servidor != null){
			criteria.add(Restrictions.eq("LPOL." + AipLogProntOnlines.Fields.SERVIDOR.toString(), servidor));
		}
		if(ocorrencia != null){
			criteria.add(Restrictions.eq("LPOL." + AipLogProntOnlines.Fields.OCORRENCIA.toString(), ocorrencia));
		}
		
		return criteria;
	}
	
	
}
