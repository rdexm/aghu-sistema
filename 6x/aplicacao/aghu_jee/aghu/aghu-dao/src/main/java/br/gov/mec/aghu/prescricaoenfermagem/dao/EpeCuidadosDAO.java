package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoMedicamento;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @modulo prescricaoenfermagem.cadastrosbasicos
 *
 */
public class EpeCuidadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeCuidados> {
    @Inject
    private EpeCuidadoMedicamentoDAO aEpeCuidadoMedicamentoDAO;
    @Inject
    private EpeCuidadoDiagnosticoDAO aEpeCuidadoDiagnosticoDAO;

	private static final long serialVersionUID = 5837118471078014496L;

	public List<EpeCuidados> pesquisarCuidadosAtivosPorSeqOuDescricao(
			String parametro) {
		DetachedCriteria criteria = this
				.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.add(Restrictions.eq(
				EpeCuidados.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteriaParaSeqOuDescricao(String parametro) {
		String seqOuDescricao = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpeCuidados.class);
		if (StringUtils.isNotEmpty(seqOuDescricao)) {
			Short seq = -1;
			if (CoreUtil.isNumeroShort(seqOuDescricao)){
				seq = Short.parseShort(seqOuDescricao);
			}			
			if (seq != -1) {
				criteria.add(Restrictions.eq(EpeCuidados.Fields.SEQ.toString(),
						seq));
			} else {
				criteria.add(Restrictions.ilike(
						EpeCuidados.Fields.DESCRICAO.toString(),
						seqOuDescricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	public List<EpeCuidados> pesquisarCuidadosAtivosPorGrupoDiagnosticoEtiologiaUnica(Short fdgDgnSnbGnbSeq, 
			Short fdgDgnSnbSequencia, Short fdgDgnSequencia, Short fdgFreSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidados.class);
		String aliasCuidDiagnostico = "cdg";
		String separador = ".";
		
		criteria.createAlias(EpeCuidados.Fields.CUIDADOS_DIAGNOSTICOS.toString(), aliasCuidDiagnostico);
		criteria.add(Restrictions.eq(aliasCuidDiagnostico + separador 
				+ EpeCuidadoDiagnostico.Fields.SITUACAO.toString() , DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasCuidDiagnostico + separador 
				+ EpeCuidadoDiagnostico.Fields.FDG_DGN_SNB_GNB_SEQ.toString(), fdgDgnSnbGnbSeq));
		criteria.add(Restrictions.eq(aliasCuidDiagnostico + separador 
				+ EpeCuidadoDiagnostico.Fields.FDG_DGN_SNB_SEQUENCIA.toString(), fdgDgnSnbSequencia));
		criteria.add(Restrictions.eq(aliasCuidDiagnostico + separador 
				+ EpeCuidadoDiagnostico.Fields.FDG_DGN_SEQUENCIA.toString(), fdgDgnSequencia));
		criteria.add(Restrictions.eq(aliasCuidDiagnostico + separador 
				+ EpeCuidadoDiagnostico.Fields.FDG_FRE_SEQ.toString(), fdgFreSeq));
		criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	private void criarCriteriaPesquisarEpeCuidadosPorCodigoDescricao(Short seq,
			String descricao, DetachedCriteria criteria) {
		if (seq != null) {
			criteria.add(Restrictions.eq(EpeCuidados.Fields.SEQ.toString(), seq));
		}
		
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(EpeCuidados.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
	}
	
	public Long pesquisarEpeCuidadosPorCodigoDescricaoCount(Short seq, String descricao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidados.class);
		
		criarCriteriaPesquisarEpeCuidadosPorCodigoDescricao(seq, descricao,
				criteria);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<EpeCuidados> pesquisarEpeCuidadosPorCodigoDescricao(Short seq, String descricao, int firstResult, int maxResults, String orderProperty, Boolean asc, Boolean orderBy) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidados.class);
		
		criarCriteriaPesquisarEpeCuidadosPorCodigoDescricao(seq, descricao,
				criteria);
	
		if(orderBy) {
			criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	// #4960 - Manter diagnósticos x cuidados
	// C6
	public List<EpeCuidados> pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticos(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia,
			Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.propertyNotIn(EpeCuidados.Fields.SEQ.toString(),
				subCriteriaCuidadoDiagnostico(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq)));
		criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	// #4960 - Manter diagnósticos x cuidados
	// C6 Count
	public Long pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticosCount(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia,
			Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.propertyNotIn(EpeCuidados.Fields.SEQ.toString(),
				subCriteriaCuidadoDiagnostico(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq)));
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria subCriteriaCuidadoDiagnostico(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = getEpeCuidadoDiagnosticoDAO().montarCriteriaDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq);
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(EpeCuidadoDiagnostico.Fields.CUIDADO.toString() + "." + EpeCuidados.Fields.SEQ.toString())));
		return criteria;
	}

	protected EpeCuidadoDiagnosticoDAO getEpeCuidadoDiagnosticoDAO() {
		return aEpeCuidadoDiagnosticoDAO;
	}

	public EpeCuidados obterCuidadosPrescricaoPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidados.class);
		
		criteria.add(Restrictions.eq(EpeCuidados.Fields.SEQ.toString(), seq));
		
		criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		// criteria.addOrder(Order.asc(EpeCuidados.Fields.SEQ.toString())); so tem um registro 
			
		return (EpeCuidados) executeCriteriaUniqueResult(criteria);
	}
	
	// #4961 - Manter medicamentos x cuidados
	// C3
	public List<EpeCuidados> pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentos(String parametro, Integer medMatCodigo) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.propertyNotIn(EpeCuidados.Fields.SEQ.toString(), subCriteriaCuidadoMedicamento(medMatCodigo)));
		criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	// #4961 - Manter medicamentos x cuidados
	// C3 Count
	public Long pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentosCount(String parametro, Integer medMatCodigo) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.propertyNotIn(EpeCuidados.Fields.SEQ.toString(), subCriteriaCuidadoMedicamento(medMatCodigo)));
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria subCriteriaCuidadoMedicamento(Integer medMatCodigo) {
		DetachedCriteria criteria = getEpeCuidadoMedicamentoDAO().montarCriteriaMedicamento(medMatCodigo);
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(EpeCuidadoMedicamento.Fields.CUIDADO.toString() + "." + EpeCuidados.Fields.SEQ.toString())));
		return criteria;
	}

	protected EpeCuidadoMedicamentoDAO getEpeCuidadoMedicamentoDAO() {
		return aEpeCuidadoMedicamentoDAO;
	}
	
	public List<EpeCuidados> pesquisarCuidadosdeRotinasAtivos(Short unidadeF , 
			List<Short> listCuidUnf , List<Short> listCuidUnidade) {
		
			
	//	select * from epe_cuidados where ind_situacao = 'A'
	//			and ( ind_rotina = 'S' and seq not in  (select cuf.cui_seq from epe_cuidado_unfs cuf
	//			                                              where cuf.cui_seq = seq))
	//			or (ind_rotina = 'S' and seq in  (select cuf.cui_seq from epe_cuidado_unfs cuf
	//				                                           where cuf.cui_seq = seq 
	//				                                           and cuf.unf_seq = 126))
	//			 order by descricao
			DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidados.class);
			criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_ROTINA.toString(), true));
			
			if (listCuidUnf!=null && listCuidUnf.size()>0 && listCuidUnidade!=null && listCuidUnidade.size()>0){
				criteria.add(Restrictions.or(Restrictions.not(Restrictions.in(EpeCuidados.Fields.SEQ.toString(), listCuidUnf)), 
						Restrictions.in(EpeCuidados.Fields.SEQ.toString(), listCuidUnidade)));
			}
			else if(!listCuidUnf.isEmpty()) {
				criteria.add(Restrictions.not(Restrictions.in(EpeCuidados.Fields.SEQ.toString(), listCuidUnf)));
			}	
			
			criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
			return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaEpeCuidadosPorCodigoDescricaoIndCci(Short codigo, String descricao, Boolean indCci) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidados.class);
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(EpeCuidados.Fields.SEQ.toString(), codigo));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(EpeCuidados.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (indCci != null) {
			criteria.add(Restrictions.eq(EpeCuidados.Fields.IND_CCI.toString(), indCci));
		}
		return criteria;
	}
	
	public List<EpeCuidados> listarCuidadosEnfermagem(Short codigo, String descricao, Boolean indCci,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = obterCriteriaEpeCuidadosPorCodigoDescricaoIndCci(codigo, descricao, indCci);
		
		criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarCuidadosEnfermagemCount(Short codigo, String descricao, Boolean indCci) {
		DetachedCriteria criteria = obterCriteriaEpeCuidadosPorCodigoDescricaoIndCci(codigo, descricao, indCci);
		return executeCriteriaCount(criteria);
	}
}
