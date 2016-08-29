package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProf;

public class PdtDescricaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtDescricao> {

	private static final long serialVersionUID = 7808042636802204163L;
	
	public Integer obterQuantidadePdtDescricaoPorCirurgia(final Integer crgSeq){
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(PdtDescricao.class,"B");
		subCriteria.setProjection(Projections.property("B."+PdtDescricao.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.ne(PdtDescricao.Fields.SITUACAO.toString(), DominioSituacaoDescricao.DEF));
		
		subCriteria.add(Restrictions.eqProperty("B."+PdtDescricao.Fields.CRG_SEQ.toString(), 
											    "A."+PdtDescricao.Fields.CRG_SEQ.toString()));

		
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class,"A");
		criteria.setProjection(Projections.property("A."+PdtDescricao.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq("A."+PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Subqueries.exists(subCriteria));
		
		final List<Object> result = executeCriteria(criteria);
		return result.size();
	}
	
	public PdtDescricao obterPdtDescricaoEAtendimentoPorSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);

		criteria.createAlias(PdtDescricao.Fields.SERVIDOR.toString(), "ser");
		criteria.createAlias(PdtDescricao.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias(PdtDescricao.Fields.MBC_CIRURGIAS.toString(), "cir");
		criteria.createAlias("cir." + MbcCirurgias.Fields.ATENDIMENTO.toString(),"atd", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SEQ.toString(), seq));
		
		return (PdtDescricao) executeCriteriaUniqueResult(criteria);
	}

	public Long obterQuantidadePdtDescricaoPorCirurgiaSimples(final Integer crgSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);
		criteria.add(Restrictions.eq(PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		return executeCriteriaCount(criteria);
	}
	
	public List<PdtDescricao> listarDescricaoPorCrgSeqESituacao(Integer crgSeq, 
			DominioSituacaoDescricao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);
		criteria.add(Restrictions.eq(PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SITUACAO.toString(), situacao));
		return executeCriteria(criteria);
	}
	
	public List<PdtDescricao> listarDescricaoPorSeqCirurgiaSituacao(Integer crgSeq, 
			DominioSituacaoDescricao[] situacao) {
		DetachedCriteria criteria = getCriteriaListarDescricaoPorSeqCirurgiaSituacao(
				crgSeq, situacao);

		return executeCriteria(criteria);
	}
	
	public List<PdtDescricao> listarDescricaoPorSeqCirurgiaSituacao(Integer crgSeq, 
			DominioSituacaoDescricao[] situacao, PdtDescricao.Fields order) {
		DetachedCriteria criteria = getCriteriaListarDescricaoPorSeqCirurgiaSituacao(
				crgSeq, situacao);
		
		criteria.addOrder(Order.asc(order.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long listarDescricaoPorSeqCirurgiaSituacaoCount(Integer crgSeq, DominioSituacaoDescricao[] situacao) {
		DetachedCriteria criteria = getCriteriaListarDescricaoPorSeqCirurgiaSituacao(crgSeq, situacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaListarDescricaoPorSeqCirurgiaSituacao(
			Integer crgSeq, DominioSituacaoDescricao[] situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);
		
		criteria.add(Restrictions.eq(PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(PdtDescricao.Fields.SITUACAO.toString(), situacao));
		return criteria;
	}

	public List<PdtDescricao> listarDescricaoPorSeqCirurgia(Integer seqCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);
		criteria.add(Restrictions.eq(PdtDescricao.Fields.CRG_SEQ.toString(), seqCirurgia));
		criteria.addOrder(Order.asc(PdtDescricao.Fields.SEQ.toString())); 
		return executeCriteria(criteria);
	}
	
	public List<PdtDescricao> pesquisarDescricaoPorCirurgiaEServidor(Integer crgSeq, Integer serMatricula, Short serVinCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);
		
		criteria.add(Restrictions.eq(PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SERVIDOR_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SERVIDOR_VIN_CODIGO.toString(), serVinCodigo));
		
		criteria.addOrder(Order.desc(PdtDescricao.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}	
	
	public Integer obterPdtDescricaoPorCirurgiaEServidor(Integer crgSeq, Integer serMatricula, Short serVinCodigo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);
		
		criteria.add(Restrictions.eq(PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SERVIDOR_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SERVIDOR_VIN_CODIGO.toString(), serVinCodigo));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SITUACAO.toString(), DominioSituacaoDescricao.DEF));
		criteria.setProjection(Projections.property(PdtDescricao.Fields.SEQ.toString()));
		criteria.setProjection(Projections.max(PdtDescricao.Fields.SEQ.toString()));
		Integer result = (Integer) executeCriteriaUniqueResult(criteria);
		
		return result;
		
	}
	public Integer obterPdtSeqPorCirurgiaEServidor(Integer crgSeq, Integer serMatricula, Short serVinCodigo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class,"DPF");
		criteria.createAlias(PdtProf.Fields.PDT_DESCRICAO.toString(), "DDT");
		
		criteria.add(Restrictions.eq("DPF."+PdtProf.Fields.SERVIDOR_PRF_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq("DPF."+PdtProf.Fields.SERVIDOR_PRF_VIN_CODIGO.toString(), serVinCodigo));
		criteria.add(Restrictions.eq("DDT."+PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("DDT."+PdtDescricao.Fields.SITUACAO.toString(), DominioSituacaoDescricao.DEF));
		
		criteria.setProjection(Projections.max("DDT."+PdtDescricao.Fields.SEQ.toString()));
		
		Integer result = (Integer) executeCriteriaUniqueResult(criteria);
		
		return result;
	}
	
	public Integer obterPdtDescricaoPorCirurgiaEServidorPendente(Integer crgSeq, Integer serMatricula, Short serVinCodigo){

		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescricao.class);

		criteria.add(Restrictions.eq(PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SERVIDOR_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(PdtDescricao.Fields.SERVIDOR_VIN_CODIGO.toString(), serVinCodigo));
		criteria.add(Restrictions.or(Restrictions.eq(PdtDescricao.Fields.SITUACAO.toString(), DominioSituacaoDescricao.PEN),
				(Restrictions.eq(PdtDescricao.Fields.SITUACAO.toString(), DominioSituacaoDescricao.PRE))));

		criteria.setProjection(Projections.property(PdtDescricao.Fields.SEQ.toString()));
		criteria.setProjection(Projections.max(PdtDescricao.Fields.SEQ.toString()));

		Integer result = (Integer) executeCriteriaUniqueResult(criteria);

		return result;

	}
	
	
	public Integer obterPdtSeqPorCirurgiaEServidorPendente(Integer crgSeq, Integer serMatricula, Short serVinCodigo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class,"DPF");
		criteria.createAlias(PdtProf.Fields.PDT_DESCRICAO.toString(), "DDT");

		criteria.add(Restrictions.eq("DPF."+PdtProf.Fields.SERVIDOR_PRF_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq("DPF."+PdtProf.Fields.SERVIDOR_PRF_VIN_CODIGO.toString(), serVinCodigo));
		criteria.add(Restrictions.eq("DDT."+PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.or(Restrictions.eq("DDT."+PdtDescricao.Fields.SITUACAO.toString(), DominioSituacaoDescricao.PEN),
									(Restrictions.eq("DDT."+PdtDescricao.Fields.SITUACAO.toString(), DominioSituacaoDescricao.PRE))));

		criteria.setProjection(Projections.max("DDT."+PdtDescricao.Fields.SEQ.toString()));
		Integer result = (Integer) executeCriteriaUniqueResult(criteria);

		return result;

	}	


	
}
