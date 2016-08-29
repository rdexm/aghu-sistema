package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.model.MpmDataItemSumario;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoMedicaVO;

public class MpmDataItemSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmDataItemSumario> {

	
	private static final long serialVersionUID = -3411633324814033036L;

	/**
	 * Método que pesquisa dataItemSumario filtrando pelo atendimento, atendimentoPaciente e o ituSeq (obtido no metodos da ManterSintaxeSumarioRN) 
	 * @param apaAtdSeq
	 * @param apaSeq
	 * @param ituSeq
	 * @return
	 */
	public List<MpmDataItemSumario> listarDataItemSumario(Integer apaAtdSeq, Integer apaSeq, Integer ituSeq){
		List<MpmDataItemSumario> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmDataItemSumario.class);
		
		criteria.add(Restrictions.eq(MpmDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmDataItemSumario.Fields.ID_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MpmDataItemSumario.Fields.ID_ITU_SEQ.toString(), ituSeq));
		
		criteria.addOrder(Order.asc(MpmDataItemSumario.Fields.ID_SEQP.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;		
	}
	
	
	/**
	 * Método que pesquisa dataItemSumario filtrando pelo atendimento 
	 * @param apaAtdSeq
	 * @return
	 */
	public List<MpmDataItemSumario> listarDataItemSumario(Integer apaAtdSeq){
		List<MpmDataItemSumario> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmDataItemSumario.class);
		
		criteria.add(Restrictions.eq(MpmDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.addOrder(Order.asc(MpmDataItemSumario.Fields.ID_SEQP.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;		
	}	
	
	
	
	/**
	 * Obtem a data a menor data dos DataItemSumario que possuem o mesmo atdSeq (atendimento) e apaSeq (atendimento paciente)
	 * @param atdSeq, apaSeq
	 * @return
	 */
	public Date obterMenorDataDoDataItemSumario(Integer atdSeq, Integer apaSeq){
		List<Date> lista = null;
		Date data = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmDataItemSumario.class);
		
		criteria.setProjection(Projections.property(MpmDataItemSumario.Fields.DATA.toString()));

		criteria.add(Restrictions.eq(MpmDataItemSumario.Fields.ID_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MpmDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), atdSeq));
		
		criteria.addOrder(Order.asc(MpmDataItemSumario.Fields.DATA.toString()));
		
		lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()){
			data = lista.get(0);
		}
		
		return data; 
	}
	
	/**
	 * Pesquisa DataItemSumario com itemPrescricaoSumario
	 *  
	 * @param prescricao, limitValor
	 * 
	 * @return RelSumarioPrescricaoVO
	 */
	@SuppressWarnings("unchecked")
	public List<RelSumarioPrescricaoVO> pesquisaGrupoDescricaoStatus(SumarioPrescricaoMedicaVO prescricao, boolean limitValor){
		StringBuilder hql = new StringBuilder(700);
		hql.append("select distinct "); 
		hql.append("new br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO ");
		hql.append("(dis.data, trim(itens.descricao), dis.valor, itens.tipo, ");
		hql.append("(select grp.ordemSumarioPrescricao ");
		hql.append(" from MpmItemMdtoSumario ims ");
		hql.append(" join ims.medicamento med ");
		hql.append(" join med.afaItemGrupoMedicamentos igr ");
		hql.append(" join igr.grupoMedicamento grp ");		
		hql.append("where ");
		hql.append(" ims.itemPrescricaoSumario=itens ");  
		hql.append(" and itens.tipo=:dominio ");
		hql.append(" and igr.situacao = :situacao1 ");
		hql.append(" and grp.usoSumarioPrescricao = :usoSumarioPrescricao ");
		hql.append(" and grp.situacao = :situacao2))");
		hql.append("from ");
		hql.append("MpmDataItemSumario dis join dis.itemPrescricaoSumarios itens ");
		hql.append("where dis.data between :dataInicial and :dataFinal ");		
		hql.append("and dis.id.apaAtdSeq=:atdSeq ");
		hql.append("and dis.id.apaSeq=:apaSeq ");
		hql.append("order by ");
		hql.append("itens.tipo, ");                  
		hql.append("2, 5, dis.data, dis.valor") ;   // 2=itens.descricao, 5=grp
	
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dominio", DominioTipoItemPrescricaoSumario.POSITIVO_6);		
		query.setParameter("dataInicial", prescricao.getDthrInicio());
		query.setParameter("dataFinal", prescricao.getDthrFim());		
		query.setParameter("atdSeq", prescricao.getAtdSeq());
		query.setParameter("apaSeq", prescricao.getAtdPac());
		query.setParameter("situacao1", DominioSituacao.A);
		query.setParameter("usoSumarioPrescricao", Boolean.TRUE);
		query.setParameter("situacao2", DominioSituacao.A);
		if (limitValor){
			query.setMaxResults(1);
		}		
		return query.list();
	}
	
}
