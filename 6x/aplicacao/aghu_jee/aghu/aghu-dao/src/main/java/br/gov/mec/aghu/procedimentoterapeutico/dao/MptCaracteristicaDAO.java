package br.gov.mec.aghu.procedimentoterapeutico.dao;


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.VMamMedicamentos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptCaracteristicaDAO extends BaseDao<MptCaracteristica>{

	
	private static final String CAR_PONTO = "CAR.";
	private static final long serialVersionUID = 62315854L;

	/**C2 #46468 - Retorna lista de caracteristicas ativas com Descrição,  e sigla **/
	public List<MptCaracteristica> obterCaracteristicasAtivasDescSeqSigla(){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class);
		criteria.add(Restrictions.eq(MptCaracteristica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptCaracteristica.Fields.DESCRICAO.toString()).as(MptCaracteristica.Fields.DESCRICAO.toString()))
				.add(Projections.property(MptCaracteristica.Fields.IND_SITUACAO.toString()).as(MptCaracteristica.Fields.IND_SITUACAO.toString()))
				.add(Projections.property(MptCaracteristica.Fields.SIGLA.toString()).as(MptCaracteristica.Fields.SIGLA.toString()))
				.add(Projections.property(MptCaracteristica.Fields.SEQ.toString()).as(MptCaracteristica.Fields.SEQ.toString()))
				.add(Projections.property(MptCaracteristica.Fields.CRIADO_EM.toString()).as(MptCaracteristica.Fields.CRIADO_EM.toString()))
				.add(Projections.property(MptCaracteristica.Fields.SERVIDOR.toString()).as(MptCaracteristica.Fields.SERVIDOR.toString()))
		);
		criteria.addOrder((Order.asc((MptCaracteristica.Fields.DESCRICAO.toString()))));
		criteria.setResultTransformer(Transformers.aliasToBean(MptCaracteristica.class));
		return executeCriteria(criteria);
	}
	
	/**C3 #46468 -  **/
	public List<MptCaracteristica> obterCaracteristicasPorFiltro(MptTipoSessao tipoSessaoFiltro, String descricaoFiltro, DominioSituacao situacao){
		StringBuffer car = new StringBuffer("CAR.");
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class, "CAR");
		criteria.createAlias(car.toString()+MptCaracteristica.Fields.LIST_CARACTERISTICA_TIPOS_SESSAO.toString(), "CTS", JoinType.LEFT_OUTER_JOIN);
		
		if(tipoSessaoFiltro != null){
			criteria.add(Restrictions.eq("CTS."+MptCaracteristicaTipoSessao.Fields.TPS_SEQ.toString(), tipoSessaoFiltro.getSeq()));
		}
		
		if(descricaoFiltro != null && !descricaoFiltro.isEmpty()){
			criteria.add(Restrictions.ilike(car.toString()+MptCaracteristica.Fields.DESCRICAO.toString(), descricaoFiltro, MatchMode.ANYWHERE));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(car.toString()+MptCaracteristica.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(car.toString()+MptCaracteristica.Fields.SEQ.toString()).as(MptCaracteristica.Fields.SEQ.toString())))
				.add(Projections.property(car.toString()+MptCaracteristica.Fields.DESCRICAO.toString()).as(MptCaracteristica.Fields.DESCRICAO.toString()))
				.add(Projections.property(car.toString()+MptCaracteristica.Fields.IND_SITUACAO.toString()).as(MptCaracteristica.Fields.IND_SITUACAO.toString()))
				.add(Projections.property(car.toString()+MptCaracteristica.Fields.SIGLA.toString()).as(MptCaracteristica.Fields.SIGLA.toString()))
				.add(Projections.property(car.toString()+MptCaracteristica.Fields.SEQ.toString()).as(MptCaracteristica.Fields.SEQ.toString()))
				.add(Projections.property(car.toString()+MptCaracteristica.Fields.CRIADO_EM.toString()).as(MptCaracteristica.Fields.CRIADO_EM.toString()))
				.add(Projections.property(car.toString()+MptCaracteristica.Fields.SERVIDOR.toString()).as(MptCaracteristica.Fields.SERVIDOR.toString()))
		);
		
		criteria.addOrder((Order.asc((car.toString()+MptCaracteristica.Fields.DESCRICAO.toString()))));
		criteria.setResultTransformer(Transformers.aliasToBean(MptCaracteristica.class));
		return executeCriteria(criteria);
	}
	
	/**C5 Verifica se a sigla informada já existe #46468 **/
	public Boolean verificarSigla(String sigla){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class);
		criteria.add(Restrictions.like(MptCaracteristica.Fields.SIGLA.toString(), sigla, MatchMode.EXACT));
		List<MptCaracteristica> lista = executeCriteria(criteria);
		return !lista.isEmpty();
	}
	
	/**C6 Verifica se a descrição informada já existe #46468 **/
	public Boolean verificarDescricao(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class);
		criteria.add(Restrictions.like(MptCaracteristica.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		List<MptCaracteristica> lista = executeCriteria(criteria);
		return !lista.isEmpty();
	}
	
	
	public List<MptCaracteristica> buscarCaracteristica(){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class, "CAR");
		criteria.addOrder(Order.asc("CAR."+MptCaracteristica.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	//Consulta SG
	public List<MptCaracteristica> obterListaCaracteristicaDescricao(Object objPesquisa){
		DetachedCriteria criteria = criarCriteriaCaracteristicas(objPesquisa);
		criteria.addOrder((Order.asc((MptCaracteristica.Fields.DESCRICAO.toString()))));
		return executeCriteria(criteria,0, 100, null,false);
	}
	
	public Long listarCaracteristicaCount(Object objPesquisa){
		return executeCriteriaCount(criarCriteriaCaracteristicas(objPesquisa));
	}
	
	public Long obterCaracteristicaDescricaoOuSiglaCount(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMamMedicamentos.class);
		String srtPesquisa = (String) objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.ilike(MptCaracteristica.Fields.DESCRICAO.toString(), srtPesquisa,
					MatchMode.ANYWHERE),
					Restrictions.eq(MptCaracteristica.Fields.SIGLA.toString(), srtPesquisa)));
		}
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criarCriteriaCaracteristicas(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class);
		criteria.add(Restrictions.eq(MptCaracteristica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		String srtPesquisa = (String) objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
				criteria.add(Restrictions.or(Restrictions.ilike(MptCaracteristica.Fields.DESCRICAO.toString(), srtPesquisa,
						MatchMode.ANYWHERE),
						Restrictions.eq(MptCaracteristica.Fields.SIGLA.toString(), srtPesquisa)));
		}
		return criteria;
	}
	
	public MptCaracteristica obterMptCaracteristicaPorSeq(MptCaracteristica mptCaracteristica){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class);
		criteria.add(Restrictions.eq(MptCaracteristica.Fields.SEQ.toString(), mptCaracteristica.getSeq()));
		return (MptCaracteristica) executeCriteriaUniqueResult(criteria);
	}
	//#45909
	public List<String> obterSiglaCaracteristicaPorTpsSeq(Short tpsSeq){
		DetachedCriteria criteria = montarObterSiglaCaracteristicaPorTpsSeq(tpsSeq);
		criteria.setProjection(Projections.property(CAR_PONTO+MptCaracteristica.Fields.SIGLA.toString()));
		
		return executeCriteria(criteria);
	}
	//#45909
	private DetachedCriteria montarObterSiglaCaracteristicaPorTpsSeq(Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristica.class, "CAR");
		criteria.createAlias(CAR_PONTO+MptCaracteristica.Fields.LIST_CARACTERISTICA_TIPOS_SESSAO.toString(), "CTS");
		criteria.createAlias("CTS."+MptCaracteristicaTipoSessao.Fields.TPS.toString(), "TPS");
		criteria.add(Restrictions.eq("TPS."+MptTipoSessao.Fields.SEQ.toString(), tpsSeq));
		return criteria;
	}
	
	
}
