package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaEquipeAnestesia;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.vo.RapServidoresVO;

public class MbcFichaEquipeAnestesiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaEquipeAnestesia> {

	private static final long serialVersionUID = 786666377550131666L;

	public List<MbcFichaEquipeAnestesia> pesquisarMbcFichaEquipeAnestesiasComServidorAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaEquipeAnestesia.class);
		addFichaAnestesia(seqMbcFichaAnestesia, criteria);
		
		criteria.createAlias(MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		
		criteria.addOrder(Order.desc(MbcFichaEquipeAnestesia.Fields.ANO_RESIDENCIA.toString()));
		criteria.addOrder(Order.asc(MbcFichaEquipeAnestesia.Fields.DTHR_ENTRADA.toString()));
		criteria.addOrder(Order.asc(MbcFichaEquipeAnestesia.Fields.DTHR_SAIDA.toString()));
		
		return executeCriteria(criteria);
	}

	private void addFichaAnestesia(Long seqMbcFichaAnestesia,
			DetachedCriteria criteria) {
		criteria.createAlias(MbcFichaEquipeAnestesia.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
	}
	
	public List<RapServidoresVO> pesquisarFichaAnestesiasAssociadaProfAtuaUnidCirg(Long ficSeq, Short unfSeq) {
		String aliasRap = "rap";
		String aliasFea = "fea";
		String aliasPuc = "puc";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaEquipeAnestesia.class, aliasFea);
		
		criteria.createAlias(aliasFea + ponto + MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST.toString(), aliasRap);
		criteria.createAlias(aliasRap + ponto + RapServidores.Fields.PROF_ATUA_UNID_CIRGS.toString(), aliasPuc);
		
		criteria.add(Restrictions.eq(aliasFea + ponto + MbcFichaEquipeAnestesia.Fields.MBC_FICHA_ANESTESIAS_SEQ.toString(), ficSeq));
		criteria.add(Restrictions.eq(aliasPuc + ponto + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		
		Projection proj =
				Projections.projectionList().add(Projections.property(aliasFea + ponto + MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST_MATRICULA.toString()), RapServidoresVO.Fields.MATRICULA.toString())
											.add(Projections.property(aliasFea + ponto + MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST_VIN_CODIGO.toString()), RapServidoresVO.Fields.VINCULO.toString());
											
		criteria.setProjection(Projections.distinct(proj));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		
		return executeCriteria(criteria);
	}

	public List<MbcFichaEquipeAnestesia> pesquisarMbcFichaEquipeAnestesiasByFichaAnestesia(
			Long seqMbcFichaAnestesia, Boolean executorBloqueio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaEquipeAnestesia.class);
		addFichaAnestesia(seqMbcFichaAnestesia, criteria);
		
		if(executorBloqueio != null){
			criteria.add(Restrictions.eq(MbcFichaEquipeAnestesia.Fields.EXECUTOR_BLOQUEIO.toString(), executorBloqueio));
		}
		
		return executeCriteria(criteria);
	}

	public List<MbcFichaEquipeAnestesia> pesquisarMbcFichaEquipeAnestesiasByFichaAnestesiaServidor(
			Long seqMbcFichaAnestesia, Integer matricula, Short codigoVinculo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcFichaEquipeAnestesia.class);
		addFichaAnestesia(seqMbcFichaAnestesia, criteria);

		if (matricula != null) {
			criteria.add(Restrictions.eq(
					MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST_MATRICULA
							.toString(), matricula));
		}
		if (codigoVinculo != null) {
			criteria.add(Restrictions.eq(
					MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST_VIN_CODIGO
							.toString(), codigoVinculo));
		}

		return executeCriteria(criteria);
	}
	
}
