package br.gov.mec.aghu.patrimonio.dao;

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
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmEdificacao;
import br.gov.mec.aghu.patrimonio.vo.DadosEdificacaoVO;
import br.gov.mec.aghu.patrimonio.vo.PtmEdificacaoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmEdificacaoDAO extends BaseDao<PtmEdificacao> {

	private static final String EDI1 = "EDI1.";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8112189378554363185L;
	private final String EDF = "EDF.";
	private final String CID = "CID.";
	private final String LOGR = "LOGR.";
	private final String CLG = "CLG.";
	private final String BP = "BP.";
	private final String BRO = "BRO.";
	private final String UF = "UF.";
	private final String BCL = "BCL.";
	private final String EDI = "EDI.";
	private final String BPE = "BPE.";

	/**
	 * #44799 C6
	 * @param seq
	 * @return
	 */
	public DadosEdificacaoVO obterDadosEdificacaoDAO(Integer seq){
		
		DetachedCriteria criteria = obterJOINCriteriaDadosEdificacaoDAO();
		
		criteria.add(Restrictions.eq(EDF+PtmEdificacao.Fields.SEQ.toString(), seq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(EDF+PtmEdificacao.Fields.SEQ.toString()), DadosEdificacaoVO.Fields.SEQ.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.NOME.toString()), DadosEdificacaoVO.Fields.NOME.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.DESCRICAO.toString()), DadosEdificacaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.LATITUDE.toString()), DadosEdificacaoVO.Fields.LATITUDE.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.LONGITUDE.toString()), DadosEdificacaoVO.Fields.LONGITUDE.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.NUMERO.toString()), DadosEdificacaoVO.Fields.NUMERO.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.COMPLEMENTO.toString()), DadosEdificacaoVO.Fields.COMPLEMENTO.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.SITUACAO.toString()), DadosEdificacaoVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property(BP+PtmBemPermanentes.Fields.NR_BEM.toString()), DadosEdificacaoVO.Fields.NUMERO_BEM.toString())
				.add(Projections.property(CID+AipCidades.Fields.NOME.toString()), DadosEdificacaoVO.Fields.NOME_CIDADE.toString())
				.add(Projections.property(CLG+AipCepLogradouros.Fields.CEP.toString()), DadosEdificacaoVO.Fields.CEP.toString())
				.add(Projections.property(LOGR+AipLogradouros.Fields.NOME.toString()), DadosEdificacaoVO.Fields.LOGRADOURO.toString())
				.add(Projections.property(BRO+AipBairros.Fields.DESCRICAO.toString()), DadosEdificacaoVO.Fields.BAIRRO.toString())
				.add(Projections.property(UF+AipUfs.Fields.SIGLA.toString()), DadosEdificacaoVO.Fields.SIGLA_UF.toString())
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(DadosEdificacaoVO.class));
		
		return (DadosEdificacaoVO) executeCriteriaUniqueResult(criteria);
	}
	
	public DetachedCriteria obterJOINCriteriaDadosEdificacaoDAO(){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmEdificacao.class, "EDF");
		criteria.createAlias(EDF+PtmEdificacao.Fields.LGR_SEQ.toString(), "LOGR");
		criteria.createAlias(EDF+PtmEdificacao.Fields.BPE_SEQ.toString(), "BP");
		criteria.createAlias(LOGR+AipLogradouros.Fields.CLO_LGR_CODIGO.toString(), "BCL");
		criteria.createAlias(LOGR+AipLogradouros.Fields.CIDADE.toString(), "CID");
		criteria.createAlias(LOGR+AipLogradouros.Fields.CEPS.toString(), "CLG");
		criteria.createAlias(BCL+AipBairrosCepLogradouro.Fields.BAIRRO.toString(), "BRO");
		criteria.createAlias(CID+AipCidades.Fields.UF.toString(), "UF");
		
		return criteria;
	}
	
	/**
	 * #44799 C7
	 * @param seq
	 * @return
	 */
	public List<DadosEdificacaoVO> obterListaDadosEdificacaoDAO(String nome, String descricao, AipLogradouros logradouro, DominioSituacao situacao, 
			AipCidades municipio, AipUfs uf, AipBairros bairros, Integer numeroEdificacao, String complemento, AipCepLogradouros cep,
			AipLogradouros logradouroS, PtmBemPermanentes bemPermanentes, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		
		DetachedCriteria criteria = obterJOINCriteriaDadosEdificacaoDAO();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(EDF+PtmEdificacao.Fields.SEQ.toString()), DadosEdificacaoVO.Fields.SEQ.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.NOME.toString()), DadosEdificacaoVO.Fields.NOME.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.SITUACAO.toString()), DadosEdificacaoVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.DESCRICAO.toString()), DadosEdificacaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property(LOGR+AipLogradouros.Fields.NOME.toString()), DadosEdificacaoVO.Fields.LOGRADOURO.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.NUMERO.toString()), DadosEdificacaoVO.Fields.NUMERO.toString())
				.add(Projections.property(BRO+AipBairros.Fields.DESCRICAO.toString()), DadosEdificacaoVO.Fields.BAIRRO.toString())
				.add(Projections.property(EDF+PtmEdificacao.Fields.COMPLEMENTO.toString()), DadosEdificacaoVO.Fields.COMPLEMENTO.toString())
				.add(Projections.property(CID+AipCidades.Fields.NOME.toString()), DadosEdificacaoVO.Fields.NOME_CIDADE.toString())
				.add(Projections.property(UF+AipUfs.Fields.SIGLA.toString()), DadosEdificacaoVO.Fields.SIGLA_UF.toString())
				.add(Projections.property(BP+PtmBemPermanentes.Fields.NR_BEM.toString()), DadosEdificacaoVO.Fields.NUMERO_BEM.toString())
				);
		
		criteria = obterFiltroCriteriaDadosEdificacaoDAO(criteria, nome, descricao, situacao, municipio, uf, bairros);
		criteria = obterFiltroCriteriaDadosEdificacaoDAO2(criteria, numeroEdificacao, complemento, cep, logradouroS, bemPermanentes);
		
		criteria.setResultTransformer(Transformers.aliasToBean(DadosEdificacaoVO.class));
		
		if(orderProperty == null || orderProperty.trim().isEmpty()){
			criteria.addOrder(Order.asc(EDF+PtmEdificacao.Fields.NOME.toString()));
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * #44799 C7 COUNT
	 * @param seq
	 * @return
	 */
	public Long obterListaDadosEdificacaoDAOCount(String nome, String descricao, AipLogradouros logradouro, DominioSituacao situacao, 
			AipCidades municipio, AipUfs uf, AipBairros bairros, Integer numeroEdificacao, String complemento, AipCepLogradouros cep,
			AipLogradouros logradouroS, PtmBemPermanentes bemPermanentes){
		
		DetachedCriteria criteria = obterJOINCriteriaDadosEdificacaoDAO();
		criteria = obterFiltroCriteriaDadosEdificacaoDAO(criteria, nome, descricao, situacao, municipio, uf, bairros);
		criteria = obterFiltroCriteriaDadosEdificacaoDAO2(criteria, numeroEdificacao, complemento, cep, logradouroS, bemPermanentes);
		
		return executeCriteriaCount(criteria);
	}
	
	
	
	private DetachedCriteria obterFiltroCriteriaDadosEdificacaoDAO(DetachedCriteria criteria, String nome, String descricao, DominioSituacao situacao, 
			AipCidades municipio, AipUfs uf, AipBairros bairros){
		
		if(StringUtils.isNotBlank(nome)){
			criteria.add(Restrictions.ilike(EDF+PtmEdificacao.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(EDF+PtmEdificacao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(EDF+PtmEdificacao.Fields.SITUACAO.toString(), situacao));
		}
		
		if(municipio != null && municipio.getCodigo() != null){
			criteria.add(Restrictions.eq(CID+AipCidades.Fields.CODIGO.toString(), municipio.getCodigo()));
		}
		
		if(uf != null && StringUtils.isNotBlank(uf.getSigla())){
			criteria.add(Restrictions.ilike(UF+AipUfs.Fields.SIGLA.toString(), uf.getSigla(), MatchMode.ANYWHERE));
		}
		
		if(bairros != null && bairros.getCodigo() != null){
			criteria.add(Restrictions.eq(BRO+AipBairros.Fields.CODIGO.toString(), bairros.getCodigo()));
		}
		
		return criteria;
	}
	
	private DetachedCriteria obterFiltroCriteriaDadosEdificacaoDAO2(DetachedCriteria criteria, Integer numeroEdificacao, String complemento, AipCepLogradouros cep,
			AipLogradouros logradouro, PtmBemPermanentes bemPermanentes){
		
		if(numeroEdificacao != null){
			criteria.add(Restrictions.eq(EDF+PtmEdificacao.Fields.NUMERO.toString(), numeroEdificacao));
		}
		
		if(StringUtils.isNotBlank(complemento)){
			criteria.add(Restrictions.ilike(EDF+PtmEdificacao.Fields.COMPLEMENTO.toString(), complemento, MatchMode.ANYWHERE));
		}
		
		if(cep != null && cep.getId() != null && cep.getId().getCep() != null){
			criteria.add(Restrictions.eq(CLG+AipCepLogradouros.Fields.CEP.toString(), cep.getId().getCep()));
		}
		
		if(logradouro != null && logradouro.getCodigo() != null){
			criteria.add(Restrictions.eq(LOGR+AipLogradouros.Fields.CODIGO.toString(), logradouro.getCodigo()));
		}
		
		if(bemPermanentes != null &&  bemPermanentes.getSeq() != null){
			criteria.add(Restrictions.eq(EDF+PtmEdificacao.Fields.BPESEQ.toString(), bemPermanentes.getSeq()));
		}
		
		return criteria;
	}
	
	/**
	 * #44800 C1 - SuggestionBox 1
	 * @param seq
	 * @return
	 */
	public List<PtmEdificacaoVO> obterSb1EdificacaoAtivo(String strPesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmEdificacao.class, "EDI");
		criteria.createAlias(EDI+PtmEdificacao.Fields.BPE_SEQ.toString(), "BPE", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(EDI+PtmEdificacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa)) {
			if(!CoreUtil.isNumeroInteger(strPesquisa)){
				criteria.add(Restrictions.ilike(PtmEdificacao.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
				
				criteria.setProjection(Projections.projectionList()
						.add(Projections.property(EDI+PtmEdificacao.Fields.SEQ.toString()), PtmEdificacaoVO.Fields.SEQ.toString())
						.add(Projections.property(EDI+PtmEdificacao.Fields.NOME.toString()), PtmEdificacaoVO.Fields.NOME.toString())
						.add(Projections.property(EDI+PtmEdificacao.Fields.DESCRICAO.toString()), PtmEdificacaoVO.Fields.DESCRICAO.toString())
						.add(Projections.property(BPE+PtmBemPermanentes.Fields.NR_BEM.toString()), PtmEdificacaoVO.Fields.NUMERO_BEM.toString())
						);
				
				criteria.setResultTransformer(Transformers.aliasToBean(PtmEdificacaoVO.class));
				return executeCriteria(criteria, 0, 100, PtmEdificacao.Fields.NOME.toString(), true);
			}else{
				criteria = null;
				criteria = DetachedCriteria.forClass(PtmEdificacao.class, "EDI");
				criteria.createAlias(EDI+PtmEdificacao.Fields.BPE_SEQ.toString(), "BPE2");
				criteria.add(Restrictions.eq(PtmEdificacao.Fields.SITUACAO.toString(), DominioSituacao.A));
				criteria.add(Restrictions.eq("BPE2."+PtmBemPermanentes.Fields.NR_BEM.toString(), Long.parseLong(strPesquisa)));
				if(executeCriteriaExists(criteria)){
					criteria.setProjection(Projections.projectionList()
							.add(Projections.property(EDI+PtmEdificacao.Fields.SEQ.toString()), PtmEdificacaoVO.Fields.SEQ.toString())
							.add(Projections.property(EDI+PtmEdificacao.Fields.NOME.toString()), PtmEdificacaoVO.Fields.NOME.toString())
							.add(Projections.property(EDI+PtmEdificacao.Fields.DESCRICAO.toString()), PtmEdificacaoVO.Fields.DESCRICAO.toString())
							.add(Projections.property("BPE2."+PtmBemPermanentes.Fields.NR_BEM.toString()), PtmEdificacaoVO.Fields.NUMERO_BEM.toString())
							);
					
					criteria.setResultTransformer(Transformers.aliasToBean(PtmEdificacaoVO.class));
					return executeCriteria(criteria, 0, 100, PtmEdificacao.Fields.NOME.toString(), true);	
				}else{
					criteria = null;
					criteria = DetachedCriteria.forClass(PtmEdificacao.class, "EDI1");
					criteria.createAlias(EDI1+PtmEdificacao.Fields.BPE_SEQ.toString(), "BPE3");
					criteria.add(Restrictions.eq(PtmEdificacao.Fields.SITUACAO.toString(), DominioSituacao.A));
					criteria.add(Restrictions.eq(PtmEdificacao.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
					criteria.setProjection(Projections.projectionList()
							.add(Projections.property(EDI1+PtmEdificacao.Fields.SEQ.toString()), PtmEdificacaoVO.Fields.SEQ.toString())
							.add(Projections.property(EDI1+PtmEdificacao.Fields.NOME.toString()), PtmEdificacaoVO.Fields.NOME.toString())
							.add(Projections.property(EDI1+PtmEdificacao.Fields.DESCRICAO.toString()), PtmEdificacaoVO.Fields.DESCRICAO.toString())
							.add(Projections.property("BPE3."+PtmBemPermanentes.Fields.NR_BEM.toString()), PtmEdificacaoVO.Fields.NUMERO_BEM.toString())
							);
					
					criteria.setResultTransformer(Transformers.aliasToBean(PtmEdificacaoVO.class));
					return executeCriteria(criteria, 0, 100, PtmEdificacao.Fields.NOME.toString(), true);
					
				}
			}
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(EDI+PtmEdificacao.Fields.SEQ.toString()), PtmEdificacaoVO.Fields.SEQ.toString())
				.add(Projections.property(EDI+PtmEdificacao.Fields.NOME.toString()), PtmEdificacaoVO.Fields.NOME.toString())
				.add(Projections.property(EDI+PtmEdificacao.Fields.DESCRICAO.toString()), PtmEdificacaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property(BPE+PtmBemPermanentes.Fields.NR_BEM.toString()), PtmEdificacaoVO.Fields.NUMERO_BEM.toString())
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(PtmEdificacaoVO.class));
		
		criteria.addOrder(Order.asc(PtmEdificacao.Fields.NOME.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long obterSb1EdificacaoAtivoCount(String strPesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmEdificacao.class, "EDI");
		criteria.createAlias(EDI+PtmEdificacao.Fields.BPE_SEQ.toString(), "BPE");
		
		criteria.add(Restrictions.eq(PtmEdificacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa)) {
			if(!CoreUtil.isNumeroInteger(strPesquisa)){
				criteria.add(Restrictions.ilike(PtmEdificacao.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
				return executeCriteriaCount(criteria);
			}else{
				criteria = null;
				criteria = DetachedCriteria.forClass(PtmEdificacao.class, "EDI2");
				criteria.createAlias("EDI2."+PtmEdificacao.Fields.BPE_SEQ.toString(), "BPE1");
				criteria.add(Restrictions.eq(PtmEdificacao.Fields.SITUACAO.toString(), DominioSituacao.A));
				criteria.add(Restrictions.eq("BPE1."+PtmBemPermanentes.Fields.NR_BEM.toString(), Long.parseLong(strPesquisa)));
				if(executeCriteriaExists(criteria)){
					return executeCriteriaCount(criteria);
				}else{
					criteria = null;
					criteria = DetachedCriteria.forClass(PtmEdificacao.class, "EDI3");
					criteria.createAlias("EDI3."+PtmEdificacao.Fields.BPE_SEQ.toString(), "BPE2");
					criteria.add(Restrictions.eq(PtmEdificacao.Fields.SITUACAO.toString(), DominioSituacao.A));
					criteria.add(Restrictions.eq(PtmEdificacao.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
					return executeCriteriaCount(criteria);
				}
			}
		}
		
		return executeCriteriaCount(criteria);
	}
}
