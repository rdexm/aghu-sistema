package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtProcDiagTerap;

public class PdtDescPadraoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtDescPadrao>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8616566756254376278L;

	public Long pesquisarDescricaoPadraoCount(Short especialidadeId,
			Integer procedimentoId, String titulo) {
		
		PdtDescPadrao descricaoPadrao = criarDescricaoPadrao(especialidadeId, procedimentoId, titulo);
		
		final DetachedCriteria criteria = this.criarCriteria(descricaoPadrao);
		
		return executeCriteriaCount(criteria);
	}

	public List<PdtDescPadrao> pesquisarDescricaoPadrao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short especialidadeId, Integer procedimentoId, String titulo) {
		
		PdtDescPadrao descricaoPadrao = criarDescricaoPadrao(especialidadeId, procedimentoId, titulo);
		
		final DetachedCriteria criteria = this.criarCriteria(descricaoPadrao);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	private PdtDescPadrao criarDescricaoPadrao(Short especialidadeId, Integer procedimentoId, String titulo){
		
		PdtDescPadrao descricaoPadrao = new PdtDescPadrao();
		
		if(especialidadeId!=null){
			AghEspecialidades especialidade = new AghEspecialidades();
			especialidade.setSeq(especialidadeId);
			descricaoPadrao.setAghEspecialidades(especialidade);
		}
		
		if(procedimentoId!=null){
			PdtProcDiagTerap pdtProcDiagTerap = new PdtProcDiagTerap();
			pdtProcDiagTerap.setSeq(procedimentoId);
			descricaoPadrao.setPdtProcDiagTerap(pdtProcDiagTerap);
		}
		
		if(StringUtils.isEmpty(titulo)){
			descricaoPadrao.setTitulo(null);
		}else{
			descricaoPadrao.setTitulo(titulo);
		}
		
		return descricaoPadrao;
		
	}
	
	private DetachedCriteria criarCriteria(final PdtDescPadrao  descricaoPadrao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescPadrao.class);
		criteria.createAlias(PdtDescPadrao.Fields.AGH_ESPECIALIDADES.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PdtDescPadrao.Fields.PDT_PROC_DIAG_TERAPS.toString(), "PROC_DIA_TER", JoinType.LEFT_OUTER_JOIN);
		
		if (descricaoPadrao != null) {
			if (descricaoPadrao.getId() != null) {
				if (descricaoPadrao.getId().getSeqp() != null) {
					criteria.add(Restrictions.eq(PdtDescPadrao.Fields.ID_SEQP.toString(), descricaoPadrao.getId().getSeqp()));
				}
				if (descricaoPadrao.getId().getEspSeq() != null) {
					criteria.add(Restrictions.eq(PdtDescPadrao.Fields.ID_ESP_SEQ.toString(), descricaoPadrao.getId().getEspSeq()));
				}
			}
			if (descricaoPadrao.getAghEspecialidades()!=null){
				criteria.add(Restrictions.eq(PdtDescPadrao.Fields.SEQ_AGH_ESPECIALIDADES.toString(), descricaoPadrao.getAghEspecialidades().getSeq()));
			}

			if (descricaoPadrao.getPdtProcDiagTerap()!=null){
				criteria.add(Restrictions.eq(PdtDescPadrao.Fields.SEQ_PDT_PROC_DIAG_TERAPS.toString(), descricaoPadrao.getPdtProcDiagTerap().getSeq()));
			}
			
			if (descricaoPadrao.getTitulo()!=null){
				criteria.add(Restrictions.ilike(PdtDescPadrao.Fields.TITULO.toString(), descricaoPadrao.getTitulo(), MatchMode.ANYWHERE));
			}
			
		}
		return criteria;
	}

	public PdtDescPadrao obterDescricaoPadraoById(Short seqp, Short espSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescPadrao.class);
		if (seqp != null) {
			criteria.add(Restrictions.eq(PdtDescPadrao.Fields.ID_SEQP.toString(), seqp));
		}
		if (espSeq != null) {
			criteria.add(Restrictions.eq(PdtDescPadrao.Fields.ID_ESP_SEQ.toString(), espSeq));
		}

		List<PdtDescPadrao> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return  list.get(0);
		}else{
			return null;
		}
		
	}
	
	public List<PdtDescPadrao> pesquisarDescPadraoProcedimentoCirurgicoAtivoPorEspSeqEPciSeq(Short espSeq, Integer pciSeq) {
		String aliasDpa = "dpa";
		String aliasDpt = "dpt";
		String aliasPci = "pci";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescPadrao.class, aliasDpa);

		criteria.createAlias(aliasDpa + ponto + PdtDescPadrao.Fields.PDT_PROC_DIAG_TERAPS.toString(), aliasDpt);
		criteria.createAlias(aliasDpt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), aliasPci);
		
		criteria.add(Restrictions.eq(aliasDpa + ponto + PdtDescPadrao.Fields.ID_ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq));
		
		criteria.addOrder(Order.asc(PdtDescPadrao.Fields.TITULO.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadeDescricaoPadraoProcCirurgicoAtivo() {
		String aliasPci = "pci";
		String aliasPdt = "pdt";
		String aliasDpd = "dpd";
		String aliasEsp = "esp";
		String ponto = ".";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescPadrao.class, aliasDpd);
		
		criteria.createAlias(aliasDpd + ponto + PdtDescPadrao.Fields.PDT_PROC_DIAG_TERAPS, aliasPdt, Criteria.INNER_JOIN);
		criteria.createAlias(aliasPdt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), aliasPci, Criteria.INNER_JOIN);
		criteria.createAlias(aliasDpd + ponto + PdtDescPadrao.Fields.AGH_ESPECIALIDADES.toString(), aliasEsp, Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SEQ.toString())), AghEspecialidades.Fields.SEQ.toString())
				.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidades.Fields.SIGLA.toString()));
		
		criteria.addOrder(Order.asc(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProcedimentoCirurgicos> pesquisarProcCirurgicoAtivoDescricaoPadraoPorEspSeq(Short espSeq) {
		String aliasPci = "pci";
		String aliasEsp = "esp";
		String aliasPdt = "pdt";
		String aliasDpd = "dpd";
		String ponto = ".";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescPadrao.class, aliasDpd);
		
		criteria.createAlias(aliasDpd + ponto + PdtDescPadrao.Fields.PDT_PROC_DIAG_TERAPS, aliasPdt, Criteria.INNER_JOIN);
		criteria.createAlias(aliasPdt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), aliasPci, Criteria.INNER_JOIN);
		criteria.createAlias(aliasDpd + ponto + PdtDescPadrao.Fields.AGH_ESPECIALIDADES.toString(), aliasEsp, Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasEsp + ponto + AghEspecialidades.Fields.SEQ, espSeq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.SEQ.toString())), MbcProcedimentoCirurgicos.Fields.SEQ.toString())
				.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));		
		
		criteria.addOrder(Order.asc(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MbcProcedimentoCirurgicos.class));
		
		return executeCriteria(criteria);
	}
	
	/*
	public List<MbcProcedimentoCirurgicos> pesquisarProcCirurgicoAtivoDescricaoPadraoPorDdtSeqEEspSeq(Integer ddtSeq, Short espSeq) {
		String aliasPci = "pci";
		String aliasEsp = "esp";
		String aliasPdt = "pdt";
		String aliasDpd = "dpd";
		String aliasDpc = "dpc";
		String ponto = ".";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescPadrao.class, aliasDpd);
		
		criteria.createAlias(aliasDpd + ponto + PdtDescPadrao.Fields.PDT_PROC_DIAG_TERAPS, aliasPdt, Criteria.INNER_JOIN);
		criteria.createAlias(aliasPdt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), aliasPci, Criteria.INNER_JOIN);
		criteria.createAlias(aliasPdt + ponto + PdtProcDiagTerap.Fields.PDT_PROCES, aliasDpc, Criteria.INNER_JOIN);
		criteria.createAlias(aliasDpd + ponto + PdtDescPadrao.Fields.AGH_ESPECIALIDADES.toString(), aliasEsp, Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasDpc + ponto + PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq(aliasDpd + ponto + PdtDescPadrao.Fields.ID_ESP_SEQ, espSeq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.SEQ.toString())), MbcProcedimentoCirurgicos.Fields.SEQ.toString())
				.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));		
		
		criteria.addOrder(Order.asc(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MbcProcedimentoCirurgicos.class));
		
		return executeCriteria(criteria);
	}	
	*/
	
	public Short obterMaxSeqpByEspSeq(Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescPadrao.class);
		criteria.setProjection(Projections.max(PdtDescPadrao.Fields.ID_SEQP.toString()));
		criteria.add(Restrictions.eq(PdtDescPadrao.Fields.ID_ESP_SEQ.toString(), espSeq));
		Object obj = executeCriteriaUniqueResult(criteria);
		if(obj != null){
			return (Short) obj;
		}else{
			return 0;
		}
	}
}
