package br.gov.mec.aghu.compras.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioTipoCentroCusto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;

public class ScoCaracteristicaUsuarioCentroCustoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCaracteristicaUsuarioCentroCusto> {

	private static final long serialVersionUID = -1853472367136399005L;
	
	@Inject
	private ScoCaracteristicaDAO scoCaracteristicaDAO;
	
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;
	
	public List<ScoCaracteristicaUsuarioCentroCusto> pesquisarCaracteristicaUserCC(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC)	{
		
		final DetachedCriteria criteria = this.obterCriteriaBasica(caracteristicaUserCC);
		
		criteria.createAlias(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.INNER_JOIN);
		criteria.createAlias(ScoCaracteristicaUsuarioCentroCusto.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.INNER_JOIN);
		criteria.createAlias(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "CAR", JoinType.INNER_JOIN);
		criteria.addOrder(Order.asc(ScoCaracteristicaUsuarioCentroCusto.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
		
	public Long pesquisarCaracteristicaUserCCCount(final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {
		final DetachedCriteria criteria = this.obterCriteriaBasica(caracteristicaUserCC);

		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaBasica(final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "SCOCARAC");

		if (caracteristicaUserCC != null) {
			if ( caracteristicaUserCC.getSeq() != null)
			{
				criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SEQ.toString(), caracteristicaUserCC.getSeq()));
			}
			if ( caracteristicaUserCC.getCaracteristica() != null)
			{
				criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), caracteristicaUserCC.getCaracteristica()));
			}
			if ( caracteristicaUserCC.getCentroCusto() != null)
			{
				criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.CENTRO_CUSTO.toString(), caracteristicaUserCC.getCentroCusto()));
			}
			if ( caracteristicaUserCC.getTipoCcusto() != null)
			{
				criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.TIPO_CCUSTO.toString(), caracteristicaUserCC.getTipoCcusto()));
			}
			if ( caracteristicaUserCC.getServidor() != null)
			{
				criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), caracteristicaUserCC.getServidor()));
			}			
		}
		return criteria;
	}


    public ScoCaracteristicaUsuarioCentroCusto obterCaracteristicaUserCC(RapServidores servidor,Integer seq) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CAR");
        criteria.createAlias(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
        criteria.createAlias(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "CARA", JoinType.INNER_JOIN);
        criteria.createAlias(ScoCaracteristicaUsuarioCentroCusto.Fields.CENTRO_CUSTO.toString(), "CECUS", JoinType.INNER_JOIN);

        criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SEQ.toString(), seq));
        List<ScoCaracteristicaUsuarioCentroCusto> lista = executeCriteria(criteria, 0, 1, null);
        if(lista != null && !lista.isEmpty()){
            return (ScoCaracteristicaUsuarioCentroCusto) lista.get(0);
        }
        else{
            return null;
        }
    }

	public ScoCaracteristicaUsuarioCentroCusto obterCaracteristica(RapServidores servidor, DominioCaracteristicaCentroCusto carac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CAR");
		ScoCaracteristica car = this.getScoCaracteristicaDAO().obterCaracteristicaPorNome(carac.getCodigo());
		
		if (car != null) {
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), car));
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), servidor));
		} else {
			return null;
		}
		
		List<ScoCaracteristicaUsuarioCentroCusto> lista = executeCriteria(criteria, 0, 1, null);
		
		if(lista != null && !lista.isEmpty()){
			return (ScoCaracteristicaUsuarioCentroCusto) lista.get(0);
		}
		else{
			return null;
		}
	}

	public Boolean verificarAutorizacaoGppg(RapServidores servidorLogado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CAR");
		
		ScoCaracteristica car = this.getScoCaracteristicaDAO().obterCaracteristicaPorNome(DominioCaracteristicaCentroCusto.AUTORIZAR_GPPG.getCodigo());
		
		if (car != null) {
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), car));
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), servidorLogado));
		} else {
			return false;
		}
		
		return executeCriteriaCount(criteria) > 0;
	}

	public FccCentroCustos obterCcAplicacaoGeracaoGppg(RapServidores servidorLogado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CAR");
		criteria.setProjection(Projections.property(ScoCaracteristicaUsuarioCentroCusto.Fields.CCT_CODIGO.toString()));
		ScoCaracteristica car = this.getScoCaracteristicaDAO().obterCaracteristicaPorNome(DominioCaracteristicaCentroCusto.GERAR_GPPG.getCodigo());
		
		if (car != null) {
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), car));
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), servidorLogado));
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.TIPO_CCUSTO.toString(), DominioTipoCentroCusto.A));
		} else {
			return null;
		}
		
		Integer codigo =  (Integer) executeCriteriaUniqueResult(criteria);
		return this.fccCentroCustosDAO.obterCentroCusto(codigo);
	}

	public FccCentroCustos obterCcAplicacaoAlteracaoRmGppg(RapServidores servidorLogado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CAR");
		criteria.setProjection(Projections.property(ScoCaracteristicaUsuarioCentroCusto.Fields.CCT_CODIGO.toString()));
				
		ScoCaracteristica car = this.getScoCaracteristicaDAO().obterCaracteristicaPorNome(DominioCaracteristicaCentroCusto.ALTERAR_RM_GPPG.getCodigo());
		
		if (car != null) {
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), car));
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), servidorLogado));
			criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.TIPO_CCUSTO.toString(), DominioTipoCentroCusto.A));
		} else {
			return null;
		}
		
		List<Integer> centroCustosCodigo = executeCriteria(criteria);
		
		if (centroCustosCodigo != null && centroCustosCodigo.size() > 0){
			return this.fccCentroCustosDAO.obterCentroCusto(centroCustosCodigo.get(0));
		} else {
			return null;
		}
	}

	private ScoCaracteristicaDAO getScoCaracteristicaDAO() {
		return scoCaracteristicaDAO;
	}
	
	public Boolean verificarProtocoloPac(RapServidores servidorLogado) {
				DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CAR");
				
				ScoCaracteristica car = this.getScoCaracteristicaDAO().obterCaracteristicaPorNome(DominioCaracteristicaCentroCusto.PROTOCOLO_PAC.getCodigo());
				
				if (car != null) {
					criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), car));
					criteria.add(Restrictions.eq(ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR.toString(), servidorLogado));
				} else {
					return false;
				}
				
			    Long numCaracteristica = executeCriteriaCount(criteria);
			    
			    if (numCaracteristica != null && numCaracteristica > 0){
			    	return true;
			    } else {
			    	return false;
			    }
				
//				return executeCriteriaCount(criteria) > 0;
	}

}
