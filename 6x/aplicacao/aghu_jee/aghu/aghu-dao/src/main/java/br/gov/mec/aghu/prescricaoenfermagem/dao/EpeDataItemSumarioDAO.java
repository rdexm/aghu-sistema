package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EpeDataItemSumario;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelSumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO;

public class EpeDataItemSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeDataItemSumario> {

	
	private static final long serialVersionUID = 7630364416499171768L;


	/**
	 * Método que pesquisa dataItemSumario filtrando pelo atendimento, atendimentoPaciente e o ituSeq (obtido no metodos da ManterSintaxeSumarioRN) 
	 * @param apaAtdSeq
	 * @param apaSeq
	 * @param isuSeq
	 * @return
	 */
	public List<EpeDataItemSumario> listarDataItemSumario(Integer apaAtdSeq, Integer apaSeq, Integer isuSeq){
		List<EpeDataItemSumario> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeDataItemSumario.class);
		
		criteria.add(Restrictions.eq(EpeDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(EpeDataItemSumario.Fields.ID_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(EpeDataItemSumario.Fields.ID_ISU_SEQ.toString(), isuSeq));
		
		criteria.addOrder(Order.asc(EpeDataItemSumario.Fields.ID_SEQP.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;		
	}
	
	
	 /** Obtem a data a menor data dos DataItemSumario que possuem o mesmo atdSeq (atendimento) e apaSeq (atendimento paciente)
	 * @param atdSeq, apaSeq
	 * @return
	 */
	public Date obterMenorDataDoDataItemSumario(Integer atdSeq, Integer apaSeq){
		List<Date> lista = null;
		Date data = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeDataItemSumario.class);
		
		criteria.setProjection(Projections.property(EpeDataItemSumario.Fields.DATA.toString()));

		criteria.add(Restrictions.eq(EpeDataItemSumario.Fields.ID_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(EpeDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), atdSeq));
		
		criteria.addOrder(Order.asc(EpeDataItemSumario.Fields.DATA.toString()));
		
		lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()){
			data = lista.get(0);
		}
		
		return data; 
	}


	/**
	 * Método que pesquisa dataItemSumario filtrando pelo atendimento 
	 * @param apaAtdSeq
	 * @return
	 */
	public List<EpeDataItemSumario> listarDataItemSumario(Integer apaAtdSeq) {
		
		List<EpeDataItemSumario> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeDataItemSumario.class);
		
		criteria.add(Restrictions.eq(EpeDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.addOrder(Order.asc(EpeDataItemSumario.Fields.ID_SEQP.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;	
	}


	/**
	 * Pesquisa DataItemSumario com itemPrescricaoSumario
	 *  
	 * @param prescricao, limitValor
	 * 
	 * @return RelSumarioPrescricaoEnfermagemVO
	 */
	@SuppressWarnings("unchecked")
	public List<RelSumarioPrescricaoEnfermagemVO> pesquisaGrupoDescricaoStatus(
			SumarioPrescricaoEnfermagemVO prescricao, boolean limitValor) {
		StringBuilder hql = new StringBuilder(400);
		hql.append("select distinct "); 
		hql.append("new br.gov.mec.aghu.prescricaoenfermagem.vo.RelSumarioPrescricaoEnfermagemVO ");
		hql.append("(dis.data, trim(itens.descricao), dis.valor, itens.tipo )");
		hql.append("from ");
		hql.append("EpeDataItemSumario dis join dis.itemPrescricaoSumario itens ");
		hql.append("where dis.data between :dataInicial and :dataFinal ");		
		hql.append("and dis.id.apaAtdSeq=:atdSeq ");
		hql.append("and dis.id.apaSeq=:apaSeq ");
		hql.append("order by ");
		hql.append("itens.tipo, ");                  
		hql.append("2, dis.data, dis.valor") ;   // 2=itens.descricao
	
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dataInicial", prescricao.getDthrInicio());
		query.setParameter("dataFinal", prescricao.getDthrFim());		
		query.setParameter("atdSeq", prescricao.getAtdSeq());
		query.setParameter("apaSeq", prescricao.getAtdPac());
		if (limitValor){
			query.setMaxResults(1);
		}		
		return query.list();
	}
}
