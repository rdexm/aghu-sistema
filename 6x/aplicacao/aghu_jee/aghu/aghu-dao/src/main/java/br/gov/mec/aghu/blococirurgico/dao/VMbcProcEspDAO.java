package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VMbcProcEspDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMbcProcEsp> {

	private static final long serialVersionUID = 8314239502314995398L;

	public List<VMbcProcEsp> pesquisarProcEspPorEspecialidade(Object parametro, Short espSeq, Integer maxResults ) {
		DetachedCriteria criteria = createCriteriaPesquisa(parametro, espSeq);
		criteria.addOrder(Order.asc(VMbcProcEsp.Fields.SIGLA.toString())).addOrder(Order.asc(VMbcProcEsp.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, maxResults, null, true);
	}
	
	
	public Long pesquisarProcEspPorEspecialidadeCount(Object parametro, Short espSeq) {
		DetachedCriteria criteria = createCriteriaPesquisa(parametro, espSeq);
		return executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria createCriteriaPesquisa(Object parametro, Short espSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(VMbcProcEsp.class);
		if(espSeq!=null){
			criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_ESP_SEQ.toString(), espSeq));
		}
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP_PROC.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_SINONIMO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(VMbcProcEsp.Fields.TIPO.toString(), new DominioTipoProcedimentoCirurgico[]{DominioTipoProcedimentoCirurgico.CIRURGIA,DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO}));
		
		//filtro suggestion
		if (StringUtils.isNotBlank(parametro.toString())) {
			if(CoreUtil.isNumeroInteger(parametro)){
				criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_PCI_SEQ.toString(), Integer.valueOf(parametro.toString())));
			}else{
				criteria.add(Restrictions.ilike(VMbcProcEsp.Fields.DESCRICAO.toString(), parametro.toString(),MatchMode.ANYWHERE));
			}
		}	
	   return criteria;
	}
	
	public List<VMbcProcEsp> pesquisarProcEspPorEspecialidadeProjetoPesquisa(Object parametro, Short espSeq) {
		DetachedCriteria criteria = this.createCriteriaPesquisarProcEspPorEspecialidadeProjetoPesquisa(parametro, espSeq);
		criteria.addOrder(Order.asc(VMbcProcEsp.Fields.ID_PCI_SEQ.toString())).addOrder(Order.asc(VMbcProcEsp.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria createCriteriaPesquisarProcEspPorEspecialidadeProjetoPesquisa(Object parametro, Short espSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VMbcProcEsp.class);

		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP_PROC.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_PROC.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_SINONIMO.toString(), DominioSituacao.A));

		if (espSeq != null) {
			criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_ESP_SEQ.toString(), espSeq));
		}

		if (StringUtils.isNotBlank(parametro.toString())) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_PCI_SEQ.toString(), Integer.valueOf(parametro.toString())));
			} else {
				criteria.add(Restrictions.ilike(VMbcProcEsp.Fields.DESCRICAO.toString(), parametro.toString(), MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}
	
	public List<VMbcProcEsp> pesquisarProcedimentosAgendadosPorEspecialidadeProcedimento(Short espSeq, Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMbcProcEsp.class);
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP_PROC.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_PROC.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_SINONIMO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public VMbcProcEsp obterProcedimentosAgendadosPorId(Short espSeq, Integer pciSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMbcProcEsp.class);
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.ID_PCI_SEQ.toString(), pciSeq));
		if(seqp != null){
			criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SEQP.toString(), seqp));
		}
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_ESP_PROC.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_PROC.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VMbcProcEsp.Fields.SITUACAO_SINONIMO.toString(), DominioSituacao.A));
		if(seqp != null){
			return (VMbcProcEsp) executeCriteriaUniqueResult(criteria);
		} else{
			criteria.addOrder(Order.desc(VMbcProcEsp.Fields.SEQP.toString()));
			List<VMbcProcEsp> resultadoParcial = executeCriteria(criteria);
			return resultadoParcial.isEmpty() ? null : resultadoParcial.get(0);
		}
		
	}
	
	
}
