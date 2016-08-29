package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.EtapaPACVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoEtapaPac;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class ScoEtapaPacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoEtapaPac> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4793537389267147490L;

	public Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.SCO_ETP_SQ1);
	}
	
	// C4 - #22068
	public Short obterTempoPrevisto(Integer numeroLicitacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(ScoEtapaPac.Fields.TEMPO_PREVISTO.toString()));
		criteria.setProjection(p);	
		
		criteria.add(Restrictions.eq(ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));
		
		DominioSituacaoEtapaPac[] situacoes = new DominioSituacaoEtapaPac[]{DominioSituacaoEtapaPac.RZ, DominioSituacaoEtapaPac.RJ};
		criteria.add(Restrictions.not(Restrictions.in(ScoEtapaPac.Fields.SITUACAO.toString(), situacoes)));
		
		Long tempoPrevisto = (Long) executeCriteriaUniqueResult(criteria);
		
		if (tempoPrevisto != null) {
		   return tempoPrevisto.shortValue();
		} else {
			return null;
		}
	
		
	}
	
	// C3 - #22068
	public List<ScoEtapaPac> obterEtapaPacPorLicitacao(Integer numeroLicitacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class);
		criteria.add(Restrictions.eq(ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));
		return executeCriteria(criteria);
	}

	// C8 - #22968
	public List<ScoEtapaPac> obterEtapasRelacionadasPAC(Integer numeroLicitacao, LocalPACVO localPACVO, 
															RapServidoresId idServidor, DominioSituacaoEtapaPac situacao,
															EtapaPACVO etapaVO, Date dataApontamento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class, "ETP");
		criteria.createAlias("ETP." + ScoEtapaPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		criteria.createAlias("ETP." + ScoEtapaPac.Fields.SERVIDOR.toString(), "RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP." + RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.LEFT_OUTER_JOIN);
		
		
		criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));
		
		if(localPACVO != null){
			criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.LCP_CODIGO.toString(), localPACVO.getCodigo()));
		}
		
		if(idServidor != null){
			criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.SER_MATRICULA.toString(), idServidor.getMatricula()));
			criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.SER_VIN_CODIGO.toString(), idServidor.getVinCodigo()));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.SITUACAO.toString(), situacao));
		}
		
		if(etapaVO != null){
			criteria.add(Restrictions.ilike("ETP." + ScoEtapaPac.Fields.DESCRICAO_ETAPA.toString(), etapaVO.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(dataApontamento != null){
			criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.DATA_APONTAMENTO.toString(), dataApontamento));
		}
		
		criteria.addOrder(Order.asc("LOC." + ScoLocalizacaoProcesso.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("ETP." + ScoEtapaPac.Fields.DATA_APONTAMENTO.toString()));
		criteria.addOrder(Order.asc("ETP." + ScoEtapaPac.Fields.TEMPO_PREVISTO.toString()));
		
		return executeCriteria(criteria);
	}
	
	// C10 - #22068
	public List<ScoEtapaPac> obterLocaisPrevistosPAC(Integer numeroLicitacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class, "ETP");
		criteria.createAlias("ETP." + ScoEtapaPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));
		
		return executeCriteria(criteria);
	}
	
	// C6 - #22068
	public List<Object[]> obterEtapaPac(Object etapa, Integer numeroLicitacao, LocalPACVO localPACVO){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class);
		
		criteria.setProjection(Projections.projectionList()
					.add(Projections.distinct(Projections.property(ScoEtapaPac.Fields.DESCRICAO_ETAPA.toString())))
					.add(Projections.property(ScoEtapaPac.Fields.CODIGO.toString()))
					);
		
		if(!etapa.toString().isEmpty() && etapa != null){
			if(CoreUtil.isNumeroInteger(etapa)){
				criteria.add(Restrictions.eq(ScoEtapaPac.Fields.CODIGO.toString(), Integer.parseInt(etapa.toString())));
			} else {
				criteria.add(Restrictions.ilike(ScoEtapaPac.Fields.DESCRICAO_ETAPA.toString(), etapa.toString(), MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));
		
		if(localPACVO != null){
			criteria.add(Restrictions.eq(ScoEtapaPac.Fields.LCP_CODIGO.toString(), localPACVO.getCodigo()));
		}
		
		return executeCriteria(criteria);
	}
	
    public List<EtapasRelacionadasPacVO> listarEtapasRelacionadasPACPorLicitacaoLocalizacao(Integer numeroLicitacao, Short codigoLocalizacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class, "ETP");
		criteria.createAlias("ETP." + ScoEtapaPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ETP." + ScoEtapaPac.Fields.SERVIDOR.toString(), "RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP." + RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));
		criteria.add(Restrictions.eq("ETP." + ScoEtapaPac.Fields.LCP_CODIGO.toString(), codigoLocalizacao));
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("LOC."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString()), EtapasRelacionadasPacVO.Fields.DESCRICAO_LOC_PROCESSO.toString())
		.add(Projections.property("ETP."+ScoEtapaPac.Fields.DESCRICAO_ETAPA.toString()), EtapasRelacionadasPacVO.Fields.DESCRICAO_ETAPA.toString())
		.add(Projections.property("ETP."+ScoEtapaPac.Fields.SITUACAO.toString()), EtapasRelacionadasPacVO.Fields.SITUACAO.toString())
		.add(Projections.property("ETP."+ScoEtapaPac.Fields.APONTAMENTO_USUARIO.toString()), EtapasRelacionadasPacVO.Fields.APONTAMENTO_USUARIO.toString())
		.add(Projections.property("RPF."+RapPessoasFisicas.Fields.NOME.toString()), EtapasRelacionadasPacVO.Fields.NOME_USUARIO.toString())
		.add(Projections.property("ETP."+ScoEtapaPac.Fields.DATA_APONTAMENTO.toString()), EtapasRelacionadasPacVO.Fields.DATA_APONTAMENTO.toString())
		.add(Projections.property("ETP."+ScoEtapaPac.Fields.CODIGO.toString()), EtapasRelacionadasPacVO.Fields.CODIGO_ETAPA.toString());
		
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(EtapasRelacionadasPacVO.class));
		
		return executeCriteria(criteria);
	}
    
    public List<LocalPACVO> pesquisarLocaisPrevistosPAC(Object param, Integer numeroLicitacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class, "ETP");
		criteria.createAlias("ETP." + ScoEtapaPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {
			if(CoreUtil.isNumeroShort(param)){
				criteria.add(Restrictions.eq("LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("LOC."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
	
		criteria.add(Restrictions.eq("ETP."+ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));
	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString())), LocalPACVO.Fields.CODIGO.toString())
				.add(Projections.property("LOC."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString()), LocalPACVO.Fields.DECRICAO.toString()));		
		
		criteria.setResultTransformer(Transformers.aliasToBean(LocalPACVO.class));
		
		return executeCriteria(criteria);
	}
    
	public Long pesquisarLocaisPrevistosPACCount(Object param, Integer numeroLicitacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaPac.class, "ETP");
		criteria.createAlias("ETP." + ScoEtapaPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {
			if(CoreUtil.isNumeroShort(param)){
				criteria.add(Restrictions.eq("LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("LOC."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
	
		criteria.add(Restrictions.eq("ETP."+ScoEtapaPac.Fields.LCT_NUMERO.toString(), numeroLicitacao));	
		
		return executeCriteriaCountDistinct(criteria, "LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString(), true);
	}
	
}
