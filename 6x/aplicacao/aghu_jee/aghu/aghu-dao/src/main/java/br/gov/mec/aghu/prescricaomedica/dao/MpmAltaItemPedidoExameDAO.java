package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExame;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExameId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmAltaItemPedidoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaItemPedidoExame> {

	private static final long serialVersionUID = 1932314020144255608L;

	@Override
	protected void obterValorSequencialId(MpmAltaItemPedidoExame elemento) {

		if (elemento == null || elemento.getMpmAltaPedidoExames() == null || elemento.getMpmAltaSumarios() == null) {
			
			throw new IllegalArgumentException("Parametro invalido!!!");
		
		}

		MpmAltaSumarioId altaSumarioId = elemento.getMpmAltaSumarios().getId();
		AelUnfExecutaExamesId executaExamesId = elemento.getAelUnfExecutaExames().getId();
		MpmAltaItemPedidoExameId altaItemPedidoExameId = new MpmAltaItemPedidoExameId();
		altaItemPedidoExameId.setAexAsuApaAtdSeq(altaSumarioId.getApaAtdSeq());
		altaItemPedidoExameId.setAexAsuApaSeq(altaSumarioId.getApaSeq());
		altaItemPedidoExameId.setAexAsuSeqp(altaSumarioId.getSeqp());
		altaItemPedidoExameId.setUfeEmaExaSigla(executaExamesId.getEmaExaSigla());
		altaItemPedidoExameId.setUfeEmaManSeq(executaExamesId.getEmaManSeq());
		altaItemPedidoExameId.setUfeUnfSeq(executaExamesId.getUnfSeq());
		
		elemento.setId(altaItemPedidoExameId);
		
	}
	
	/**
	 * Busca MpmAltaItemPedidoExame do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmAltaItemPedidoExame> obterMpmAltaItemPedidoExame(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaItemPedidoExame.class);
		criteria.add(Restrictions.eq(MpmAltaItemPedidoExame.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaItemPedidoExame.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaItemPedidoExame.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		return executeCriteria(criteria);
		
	}

	public List<VAelExamesSolicitacao> obterNomeExames(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExamesSolicitacao.class);
		
		criteria = this.obterCriterioConsultaNomeExames(criteria, objPesquisa);
		
		criteria.addOrder(Order.asc(VAelExamesSolicitacao.Fields.DESCRICAO_USUAL_EXAME.toString()));
		return executeCriteria(criteria);
	}
	
	
	public Long obterNomeExamesCount(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExamesSolicitacao.class);
		
		criteria = this.obterCriterioConsultaNomeExames(criteria, objPesquisa);
		return this.executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria obterCriterioConsultaNomeExames(DetachedCriteria criteria, Object objPesquisa) {
		criteria.add(Restrictions.ilike(VAelExamesSolicitacao.Fields.DESCRICAO_USUAL_EXAME.toString(), 
				(String)objPesquisa, MatchMode.ANYWHERE));
		return criteria;
	}
	

	public AelUnfExecutaExames buscarAelUnfExecutaExamesPorID(Integer manSeq,
			String sigla, Integer unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelUnfExecutaExames.class);
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq.shortValue()));
		return (AelUnfExecutaExames) executeCriteriaUniqueResult(criteria);
	}
	
	public AelMateriaisAnalises buscarAelMateriaisAnalisesPorAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMateriaisAnalises.class);
		criteria.createAlias("aelExamesMaterialAnalises", "exames");
		criteria.add(Restrictions.eq("exames.id.exaSigla", aelUnfExecutaExames.getId().getEmaExaSigla()));
		criteria.add(Restrictions.eq("exames.id.manSeq", aelUnfExecutaExames.getId().getEmaManSeq()));
		
		return (AelMateriaisAnalises) executeCriteriaUniqueResult(criteria);
	}

	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(
			AelUnfExecutaExames aelUnfExecutaExames) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.add(Restrictions.eq("seq", aelUnfExecutaExames.getId().getUnfSeq().getSeq()));
		criteria.add(Restrictions.eq("indSitUnidFunc", DominioSituacao.A));
		
		return (AghUnidadesFuncionais) executeCriteriaUniqueResult(criteria);
	}

	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
		   "ae." + AelExames.Fields.DESCRICAO_USUAL.toString() + ")" +
		   " from MpmAltaItemPedidoExame aipe" +
		   " join aipe.aelUnfExecutaExames auee " +
		   " join auee.aelExamesMaterialAnalise aema " +
		   " join aema.aelExames ae" +
		   " where aipe."+ MpmAltaItemPedidoExame.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and aipe." + MpmAltaItemPedidoExame.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and aipe." + MpmAltaItemPedidoExame.Fields.ASU_SEQP.toString() + "=:asuSeqp ";
		Query query = createHibernateQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		return query.list();		
		
		
	}    	
	

}
