package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.EtapaPACVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.compras.vo.ModPacSolicCompraServicoVO;
import br.gov.mec.aghu.dominio.DominioObjetoDoPac;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoEtapaModPacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoEtapaModPac> {

	private static final long serialVersionUID = -594518991954904231L;

	public List<ScoEtapaModPac> listarEtapasModPac(ScoTempoAndtPac tempoLocalizacaoPac, DominioObjetoDoPac objetoPac) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		criteria.add(Restrictions.eq(
				ScoEtapaModPac.Fields.MLC_CODIGO.toString(), tempoLocalizacaoPac.getId().getMlcCodigo()));

		criteria.add(Restrictions.eq(
				ScoEtapaModPac.Fields.LCP_CODIGO.toString(), tempoLocalizacaoPac.getId().getLcpCodigo()));

		if (objetoPac != null) {
			criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), objetoPac));
		}
		
	//	criteria.addOrder(Order.asc(ScoEtapaModPac.Fields.MLC_CODIGO.toString()));
	//	criteria.addOrder(Order.asc(ScoEtapaModPac.Fields.LCP_CODIGO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	// C5 - #22068
	public Short obterNumeroDiasPac(String mlcCodigo, Boolean materialServico){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(ScoEtapaModPac.Fields.NUM_DIAS.toString()));
		criteria.setProjection(p);	
		
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.MLC_CODIGO.toString(), mlcCodigo));
		
		if(materialServico){
			criteria.add(Restrictions.or(
								Restrictions.eq(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.S), 
								Restrictions.eq(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.M))
						);
		}
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	// Para I1 - #22068
	public List<ScoEtapaModPac> obterDadosEtapaModPac(String mlcCodigo, Boolean materialServico){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.MLC_CODIGO.toString(), mlcCodigo));
		
		if(materialServico){
			criteria.add(Restrictions.or(
								Restrictions.eq(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.S), 
								Restrictions.eq(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.M))
						);
		}
		
		return executeCriteria(criteria);
		
	}
	
	// C11 - #22068
	public List<ScoEtapaModPac> obterLocaisPrevistosModPac(String mdlCodigo, Boolean material){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class, "EMP");
		criteria.createAlias("EMP." + ScoEtapaModPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.MLC_CODIGO.toString(), mdlCodigo));
		
		if(material != null){
			if(material){
				criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.M));
			} else {
				criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.S));
			}
		}
		
		return executeCriteria(criteria);
	}
	
	// C7 - #22068
	public List<ScoEtapaModPac> obterEtapaModPac(Object etapa, String modCodigo, LocalPACVO localPACVO, Boolean material){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		criteria.setProjection(Projections.projectionList()
								.add(Projections.property(ScoEtapaModPac.Fields.DESCRICAO.toString()))
								.add(Projections.property(ScoEtapaModPac.Fields.CODIGO.toString()))
								);
		
		if(etapa != null){
			if(CoreUtil.isNumeroInteger(etapa)){
				criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.CODIGO.toString(), Integer.parseInt(etapa.toString())));
			} else {
				criteria.add(Restrictions.ilike(ScoEtapaModPac.Fields.DESCRICAO.toString(), etapa.toString(), MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.MLC_CODIGO.toString(), modCodigo));
		
		if(localPACVO != null){
			criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.LCP_CODIGO.toString(), localPACVO.getCodigo()));
		}
		
		if(material != null){
			if(material){
				criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.M));
			} else {
				criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.S));
			}
		}
		
		return executeCriteria(criteria);
	}
	
	// C9 - #22068
	public List<ScoEtapaModPac> obterEtapasRelacionadasModalidade(ModPacSolicCompraServicoVO modPacSolicCompraServicoVO, Boolean material, LocalPACVO localPACVO, EtapaPACVO etapaVO){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class, "EMP");
		criteria.createAlias("EMP." + ScoEtapaModPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(modPacSolicCompraServicoVO != null){
			criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.MLC_CODIGO.toString(), modPacSolicCompraServicoVO.getCodigoModalidade()));
		}
		
		if(material != null){
			if(material){
				criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.M));
			} else {
				criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.OBJETO_PAC.toString(), DominioObjetoDoPac.S));
			}
		}
		
		if(localPACVO != null){
			criteria.add(Restrictions.eq("EMP." + ScoEtapaModPac.Fields.LCP_CODIGO.toString(), localPACVO.getCodigo()));
		}
		
		if(etapaVO != null){
			criteria.add(Restrictions.ilike("EMP." + ScoEtapaModPac.Fields.DESCRICAO.toString(), etapaVO.getDescricao(), MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc("LOC." + ScoLocalizacaoProcesso.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("EMP." + ScoEtapaModPac.Fields.NUM_DIAS.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Boolean verificarLocalPacComEtapa(ScoTemposAndtPacsId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		criteria.add(Restrictions.eq(
				ScoEtapaModPac.Fields.MLC_CODIGO.toString(), id.getMlcCodigo()));

		criteria.add(Restrictions.eq(
				ScoEtapaModPac.Fields.LCP_CODIGO.toString(), id.getLcpCodigo()));

		return this.executeCriteriaCount(criteria) > 0;		
	}
	
public List<LocalPACVO> listarEtapaModPac(Object param, String modCodigo, List<DominioObjetoDoPac> objetoPAC){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		criteria.createAlias(ScoEtapaModPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if(CoreUtil.isNumeroShort(param)){
				criteria.add(Restrictions.eq("LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("LOC."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.MLC_CODIGO.toString(), modCodigo));
		
		if(objetoPAC != null && !objetoPAC.isEmpty()){
			criteria.add(Restrictions.in(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), objetoPAC));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString())), LocalPACVO.Fields.CODIGO.toString())
				.add(Projections.property("LOC."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString()), LocalPACVO.Fields.DECRICAO.toString()));		
		
		criteria.setResultTransformer(Transformers.aliasToBean(LocalPACVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Long listarEtapaModPacCount(Object param, String modCodigo, List<DominioObjetoDoPac> objetoPAC){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		criteria.createAlias(ScoEtapaModPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if(CoreUtil.isNumeroShort(param)){
				criteria.add(Restrictions.eq("LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("LOC."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.MLC_CODIGO.toString(), modCodigo));
		
		if(objetoPAC != null && !objetoPAC.isEmpty()){
			criteria.add(Restrictions.in(ScoEtapaModPac.Fields.OBJETO_PAC.toString(), objetoPAC));
		}
		
		return executeCriteriaCountDistinct(criteria, "LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString(), true);
	}
	
    public List<EtapasRelacionadasPacVO> listarEtapasRelacionadasPACPorLicitacaoLocalizacao(String codigoModalidade, Short codigoLocalizacao, List<DominioObjetoDoPac> objetoPAC){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoEtapaModPac.class);
		
		criteria.createAlias(ScoEtapaModPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LOC");
		
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.MLC_CODIGO.toString(), codigoModalidade));
		criteria.add(Restrictions.eq(ScoEtapaModPac.Fields.LCP_CODIGO.toString(), codigoLocalizacao));
		
		criteria.addOrder(Order.asc("LOC."+ScoLocalizacaoProcesso.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(ScoEtapaModPac.Fields.NUM_DIAS.toString()));
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(ScoEtapaModPac.Fields.DESCRICAO.toString()), EtapasRelacionadasPacVO.Fields.DESCRICAO_ETAPA.toString())
		.add(Projections.property(ScoEtapaModPac.Fields.CODIGO.toString()), EtapasRelacionadasPacVO.Fields.CODIGO_ETAPA.toString());
		
		criteria.setProjection(projection);
		
		criteria.setResultTransformer(Transformers.aliasToBean(EtapasRelacionadasPacVO.class));
		
		return executeCriteria(criteria);
	}
}
