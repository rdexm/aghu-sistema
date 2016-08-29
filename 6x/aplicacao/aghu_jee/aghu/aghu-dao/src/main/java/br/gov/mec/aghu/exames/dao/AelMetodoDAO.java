package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelMetodo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @author lalegre
 *
 */
public class AelMetodoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMetodo> {
	
	private static final long serialVersionUID = 542735640355171788L;

	/**
	 * Retorna AelMetodo original
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AelMetodo obterOriginal(AelMetodo elementoModificado) {
		
		final Short id = elementoModificado.getSeq();
		
		StringBuilder hql = new StringBuilder(100);

		hql.append("select o.").append(AelMetodo.Fields.SEQ.toString());
		hql.append(", o.").append(AelMetodo.Fields.DESCRICAO.toString());
		hql.append(", o.").append(AelMetodo.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelMetodo.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelMetodo.Fields.SERVIDOR.toString());
		
		hql.append(" from ").append(AelMetodo.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelMetodo.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		AelMetodo original = null;
		
		List<Object[]> camposList = (List<Object[]>) query.getResultList();
		
		if(camposList != null && camposList.size()>0) {
			
			Object[] campos = camposList.get(0);
			original = new AelMetodo();
			
			original.setSeq(id);
			original.setDescricao((String)campos[1]);
			original.setSituacao((DominioSituacao)campos[2]);
			original.setCriadoEm((Date)campos[3]);
			original.setServidor((RapServidores)campos[4]);
					
		}
		
		return original;
		
	}
	
	
	/**
	 * Lista Metodos pela Seq ou Descricao
	 * @param parametro
	 * @return
	 */
	public List<AelMetodo> obterMetodosPorSerDescricao(Object parametro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMetodo.class);
		final String srtPesquisa = (String) parametro;

		if (CoreUtil.isNumeroInteger(parametro)) {

			criteria.add(Restrictions.eq(AelMetodo.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));

		} else if (StringUtils.isNotEmpty(srtPesquisa)) {

			criteria.add(Restrictions.ilike(AelMetodo.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));

		}
		
		criteria.addOrder(Order.asc(AelMetodo.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
		
	}

	public List<AelMetodo> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelMetodo elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		criteria.createAlias(AelMetodo.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	private DetachedCriteria criarCriteria(AelMetodo elemento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMetodo.class);
		
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Código
    		if(elemento.getSeq() != null) {
    			criteria.add(Restrictions.eq(AelMetodo.Fields.SEQ.toString(), elemento.getSeq()));
    		}
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AelMetodo.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}
			//Situação
			if(elemento.getSituacao() != null) {
				criteria.add(Restrictions.eq(AelMetodo.Fields.SITUACAO.toString(), elemento.getSituacao()));
			}
    	}
    	return criteria;
    }
	
	public Long pesquisarCount(AelMetodo elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * @HIST AelMetodoDAO.obterMetodoAalisePorSoeSeqEDataEventoHist
	 * @param soeSeq
	 * @param seqp
	 * @param dataEvento
	 * @return
	 */
	public String obterMetodoAalisePorSoeSeqEDataEvento(final Integer soeSeq, final Short seqp, Date dataEvento) {
		String sql = 	"	select  mtd.descricao " +
						"	from   	agh.ael_metodos mtd," +
						"			agh.ael_metodo_unf_exames     mue," +
						"			agh.ael_item_solicitacao_exames   ise" +
						"	where  	ise.soe_seq		=  :iseSoeseq" +
						"	and     ise.seqp		=  :seqp" +
						"	and     mue.ufe_ema_exa_sigla	=  ise.ufe_ema_exa_sigla" +
						"	and     mue.ufe_ema_man_seq	=  ise.ufe_ema_man_seq" +
						"	and     mue.ufe_unf_seq		=  ise.ufe_unf_seq" +
						"	and     mue.dthr_inicio		<=  :dataInicio" +
						"	and     (mue.dthr_fim		is  null  or mue.dthr_fim > :dataFim)" +
						"	and     mtd.seq			=  mue.mtd_seq";
		
		Query query = this.createNativeQuery(sql);
		query.setParameter("iseSoeseq", soeSeq);
		query.setParameter("seqp", seqp);
		query.setParameter("dataInicio", dataEvento);
		query.setParameter("dataFim", dataEvento);
		
		List<Object> resultList = query.getResultList();
		
		if(resultList != null && resultList.size() > 0){
			return resultList.get(0).toString();
		}else{
			return null;
		}
	}
	
	public String obterMetodoAalisePorSoeSeqEDataEventoHist(final Integer soeSeq, final Short seqp, Date dataEvento) {
		String sql = 	"	select  mtd.descricao " +
						"	from   	agh.ael_metodos mtd," +
						"			agh.ael_metodo_unf_exames     mue," +
						"			hist.ael_item_solicitacao_exames   ise" +
						"	where  	ise.soe_seq		=  :iseSoeseq" +
						"	and     ise.seqp		=  :seqp" +
						"	and     mue.ufe_ema_exa_sigla	=  ise.ufe_ema_exa_sigla" +
						"	and     mue.ufe_ema_man_seq	=  ise.ufe_ema_man_seq" +
						"	and     mue.ufe_unf_seq		=  ise.ufe_unf_seq" +
						"	and     mue.dthr_inicio		<=  :dataInicio" +
						"	and     (mue.dthr_fim		is  null  or mue.dthr_fim > :dataFim)" +
						"	and     mtd.seq			=  mue.mtd_seq";
		
		Query query = this.createNativeQuery(sql);
		query.setParameter("iseSoeseq", soeSeq);
		query.setParameter("seqp", seqp);
		query.setParameter("dataInicio", dataEvento);
		query.setParameter("dataFim", dataEvento);
		
		List<Object> resultList = query.getResultList();
		
		if(resultList != null && resultList.size() > 0){
			return resultList.get(0).toString();
		}else{
			return null;
		}
	}
}