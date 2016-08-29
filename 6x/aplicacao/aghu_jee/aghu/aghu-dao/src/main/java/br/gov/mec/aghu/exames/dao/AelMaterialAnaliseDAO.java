package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.solicitacao.vo.MaterialVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelMaterialAnaliseDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMateriaisAnalises> {

	private static final long serialVersionUID = -8013450683089198888L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMateriaisAnalises.class);
		return criteria;
    }
	
	private DetachedCriteria criarCriteria(AelMateriaisAnalises elemento) {
    	DetachedCriteria criteria = obterCriteria();
    	
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		
    		//Código
    		if(elemento.getSeq() != null) {
    			criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.SEQ.toString(), elemento.getSeq()));
    		}
    		
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AelMateriaisAnalises.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}
			
			//Situação
			if(elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.IND_SITUACAO.toString(), elemento.getIndSituacao()));
			}
			
			//Coletável
			if(elemento.getIndColetavel() != null) {
				criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), elemento.getIndColetavel()));
			}
			
			//Não busca por data de criação
			
			//Exige Descrição
			if(elemento.getIndExigeDescMatAnls() != null) {
				criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.IND_EXIGE_DESC_MAT_ANLS.toString(), elemento.getIndExigeDescMatAnls()));
			}
			
			//Urina
			if(elemento.getIndUrina() != null) {
				criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.IND_URINA.toString(), elemento.getIndUrina()));
			}
    		
    	}
    	
    	return criteria;
    }
	
	public AelMateriaisAnalises obterPeloId(Integer codigo) {
		AelMateriaisAnalises elemento = new AelMateriaisAnalises();
		elemento.setSeq(codigo);
		DetachedCriteria criteria = criarCriteria(elemento);
		return (AelMateriaisAnalises) executeCriteriaUniqueResult(criteria);
	}
	
	/*public AelMateriaisAnalises obterOriginal(Integer codigo) {
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelMateriaisAnalises.Fields.SEQ.toString());
		hql.append(", o.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString());
		hql.append(", o.").append(AelMateriaisAnalises.Fields.IND_SITUACAO.toString());
		hql.append(", o.").append(AelMateriaisAnalises.Fields.IND_COLETAVEL.toString());
		hql.append(", o.").append(AelMateriaisAnalises.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelMateriaisAnalises.Fields.IND_EXIGE_DESC_MAT_ANLS.toString());
		hql.append(", o.").append(AelMateriaisAnalises.Fields.IND_URINA.toString());
		hql.append(" from ").append(AelMateriaisAnalises.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelMateriaisAnalises.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", codigo);
		
		
		AelMateriaisAnalises retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new AelMateriaisAnalises();

			retorno.setSeq( (Integer) campos[0]);
			retorno.setDescricao((String) campos[1]);
			retorno.setIndSituacao((DominioSituacao) campos[2]);
			retorno.setIndColetavel((Boolean) campos[3]);
			retorno.setCriadoEm((Date) campos[4]);
			retorno.setIndExigeDescMatAnls((Boolean) campos[5]);
			retorno.setIndUrina((Boolean) campos[6]);
		}		
		
		return retorno;
	}*/
	
	public List<AelMateriaisAnalises> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelMateriaisAnalises elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		
		//Métodos copiados da GenericDAO pois é necessário usar 2 critérios de ordenação
		return executeCriteriaOrdenada(criteria, firstResult, maxResult);
	}
	
	@SuppressWarnings("unchecked")
	protected List<AelMateriaisAnalises> executeCriteriaOrdenada(DetachedCriteria criteria, int firstResult, int maxResults) {

		criteria.addOrder(Order.asc(AelMateriaisAnalises.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(AelMateriaisAnalises.Fields.IND_SITUACAO.toString()));		
		
		return this.executeCriteria(criteria, firstResult, maxResults, null, false);
	}
	
	
	

	public Long pesquisarCount(AelMateriaisAnalises elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	public List<AelMateriaisAnalises> pesquisarDescricao(AelMateriaisAnalises elemento) {
		DetachedCriteria criteria = obterCriteria();
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.DESCRICAO.toString(), elemento.getDescricao()));
			}
    	}
		return executeCriteria(criteria);
	}
	
	public List<AelMateriaisAnalises> listarAelMateriaisAnalises(Object parametro) {
	    final String strPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelMateriaisAnalises.class);
	    if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AelMateriaisAnalises.Fields.DESCRICAO.toString(), strPesquisa , MatchMode.ANYWHERE ));
		}
	    criteria.addOrder(Order.asc(AelMateriaisAnalises.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AelMateriaisAnalises> listarAelMateriaisAnalisesAtivoColetavel(Object parametro) {
	    final String strPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelMateriaisAnalises.class);
	    if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AelMateriaisAnalises.Fields.DESCRICAO.toString(), strPesquisa , MatchMode.ANYWHERE ));
		}
	    criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	    criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.TRUE));
	    criteria.addOrder(Order.asc(AelMateriaisAnalises.Fields.DESCRICAO.toString()));
	    
		return executeCriteria(criteria);
	}

	public List<AelMateriaisAnalises> listarMateriaisAnalise(String strPesquisa) {
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			AelMateriaisAnalises materialAnalise = this.obterPorChavePrimaria(Integer.valueOf(strPesquisa));
			
			if (materialAnalise != null) {
				List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>(1);
				lista.add(materialAnalise);
				return lista;
			}
		}
		
		DetachedCriteria criteria = criarCriteriaListarMateriaisAnalise(strPesquisa,AelMateriaisAnalises.Fields.DESCRICAO.toString());
		return this.executeCriteria(criteria, 0, 50, null, true);
	}
	
		public Long listarMateriaisAnaliseCount(String strPesquisa) {
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			AelMateriaisAnalises materialAnalise = this.obterPorChavePrimaria(Integer.valueOf(strPesquisa));
			
			if (materialAnalise != null) {
				return 1l;
			}
		}
		
		DetachedCriteria criteria = criarCriteriaListarMateriaisAnalise(strPesquisa);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListarMateriaisAnalise(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMateriaisAnalises.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AelMateriaisAnalises.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	private DetachedCriteria criarCriteriaListarMateriaisAnalise(String strPesquisa, String order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMateriaisAnalises.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AelMateriaisAnalises.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(order));
		return criteria;
	}

	/**
	 *  Consulta para retornar amostras dado um numero de solicitacao de exame
	 *  C1 #22049
	 *  @param solicitacaoNumero numero de solicitacao de exame
	 */
	public List<MaterialVO> obterAmostrasSolicitacao(Integer solicitacaoNumero, List<Integer> numerosAmostras, final Map<AghuParametrosEnum, String> situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "soe");
		
		criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "aie");
		criteria.createAlias("ufe." + AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "ema");
		criteria.createAlias("ufe." + AelUnfExecutaExames.Fields.AEL_ITEM_CONFIG_EXAMES.toString(), "conf");
		criteria.createAlias("ema." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man");
		criteria.createAlias("ema." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa");
		
		criteria.add(Restrictions.eq("soe." + AelSolicitacaoExames.Fields.SEQ.toString(), solicitacaoNumero));
		if (numerosAmostras != null && !numerosAmostras.isEmpty()) {
			criteria.add(Restrictions.in("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), numerosAmostras));
		}
		
//		if (numerosAmostras != null && !numerosAmostras.isEmpty()) {
//			criteria.add(
//					Restrictions.or(
//							Restrictions.in("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), numerosAmostras),
//							Restrictions.isNull("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString())
//					)
//				);
//		}
		
		if (situacao != null) {
			criteria.add(Restrictions.in("ise." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao.values()));
		}
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString()), "numeroAmostra")
				.add(Projections.property("aie." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()), "solicitacaoExameSeq")
				.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString()), "iseSeq")
				.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.DESC_REGIAO_ANATOMICA.toString()), "regiaoAnatomica")
				.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()), "descricaoMaterial")
				.add(Projections.property("ema." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), "materialAnalise");
	
		criteria.setProjection(projection);
		
		criteria.addOrder(Order.asc("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MaterialVO.class));
		
		return this.executeCriteria(criteria);
	}
	
}