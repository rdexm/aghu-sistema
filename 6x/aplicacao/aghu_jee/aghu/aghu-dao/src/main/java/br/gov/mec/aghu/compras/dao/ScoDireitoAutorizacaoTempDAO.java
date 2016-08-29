package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoPontoServidor;


public class ScoDireitoAutorizacaoTempDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoDireitoAutorizacaoTemp> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7377200531885320524L;

	public List<ScoDireitoAutorizacaoTemp> pesquisarScoDireitoAutorizacaoTemp(
			ScoPontoServidor pontoServidor, 
			RapServidores    servidor,	
			Integer firstResult, Integer maxResults, String orderProperty,boolean asc) {

			DetachedCriteria criteria = montarConsulta(pontoServidor,servidor);
            return executeCriteria(criteria, firstResult, maxResults,
					orderProperty, asc);
		}
	
	public Long pesquisarScoDireitoAutorizacaoTempCount(
			ScoPontoServidor pontoServidor, 
			RapServidores    servidor ) {
  		    DetachedCriteria criteria = montarConsulta(pontoServidor,servidor);
			return executeCriteriaCount(criteria);
		}
	
	
	public List<ScoDireitoAutorizacaoTemp> listarScoDireitoAutorizacaoTemp(
			ScoPontoServidor pontoServidor, 
			RapServidores    servidor) {

			DetachedCriteria criteria = montarConsulta(pontoServidor,servidor);
			criteria.createAlias(ScoDireitoAutorizacaoTemp.Fields.FCC_CENTRO_CUSTOS.toString(), "FCC",JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(ScoDireitoAutorizacaoTemp.Fields.SCO_PONTOS_SERVIDORES.toString(), "SPS",JoinType.LEFT_OUTER_JOIN);
			
			criteria.createAlias(ScoDireitoAutorizacaoTemp.Fields.RAP_SERVIDORES.toString(), "SERV",JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PES",JoinType.LEFT_OUTER_JOIN);
			criteria.addOrder(Order.asc(ScoDireitoAutorizacaoTemp.Fields.NUMERO.toString()));
	           
            return executeCriteria(criteria);
		}
	
	public ScoDireitoAutorizacaoTemp obterDireitoAutorizacaoTemporarioPorId(ScoDireitoAutorizacaoTemp direito) {
		DetachedCriteria criteria = montarConsulta(direito.getScoPontoServidor(),direito.getServidor());
		
		criteria.add(Restrictions.eq(ScoDireitoAutorizacaoTemp.Fields.NUMERO.toString(), direito.getNumero()));
		criteria.createAlias(ScoDireitoAutorizacaoTemp.Fields.FCC_CENTRO_CUSTOS.toString(), "FCC",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoDireitoAutorizacaoTemp.Fields.SCO_PONTOS_SERVIDORES.toString(), "SPS",JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(ScoDireitoAutorizacaoTemp.Fields.RAP_SERVIDORES.toString(), "SERV",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PES",JoinType.LEFT_OUTER_JOIN);
		criteria.addOrder(Order.asc(ScoDireitoAutorizacaoTemp.Fields.NUMERO.toString()));
           
        return (ScoDireitoAutorizacaoTemp) executeCriteriaUniqueResult(criteria);
	}
		
	public boolean isDuplicadoScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutTemp)
	{
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoDireitoAutorizacaoTemp.class);

		if (scoDireitoAutTemp != null) {
			if (scoDireitoAutTemp.getNumero() != null) {
				criteria.add(Restrictions.ne(
						ScoDireitoAutorizacaoTemp.Fields.NUMERO.toString(),
						scoDireitoAutTemp.getNumero()));
			}

			if (scoDireitoAutTemp.getScoPontoServidor() != null) {
				criteria.add(Restrictions.eq(
						ScoDireitoAutorizacaoTemp.Fields.SCO_PONTOS_SERVIDORES
								.toString(), scoDireitoAutTemp
								.getScoPontoServidor()));
			}

			if (scoDireitoAutTemp.getCentroCusto() != null) {
				criteria.add(Restrictions.eq(
						ScoDireitoAutorizacaoTemp.Fields.FCC_CENTRO_CUSTOS
								.toString(), scoDireitoAutTemp.getCentroCusto()));
			}

			if (scoDireitoAutTemp.getServidor() != null) {
				criteria.add(Restrictions.eq(
						ScoDireitoAutorizacaoTemp.Fields.RAP_SERVIDORES
								.toString(), scoDireitoAutTemp.getServidor()));
			}

			if (scoDireitoAutTemp.getDtInicio() != null) {
				criteria.add(Restrictions.eq(
						ScoDireitoAutorizacaoTemp.Fields.DT_INICIO.toString(),
						scoDireitoAutTemp.getDtInicio()));
			}

			if (scoDireitoAutTemp.getDtFim() != null) {
				criteria.add(Restrictions.eq(
						ScoDireitoAutorizacaoTemp.Fields.DT_FIM.toString(),
						scoDireitoAutTemp.getDtFim()));
			}

		}
			
		ScoDireitoAutorizacaoTemp scoDireitoAutTempResult = null;
		scoDireitoAutTempResult = (ScoDireitoAutorizacaoTemp) this.executeCriteriaUniqueResult(criteria);
		
		if (scoDireitoAutTempResult != null){
			return true;
		}
		else {
			return false;
		}		
	}

	private DetachedCriteria montarConsulta(
			ScoPontoServidor pontoServidor, 
			RapServidores    servidor ) {
		
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoDireitoAutorizacaoTemp.class);

			if (pontoServidor  != null && pontoServidor.getId() != null) {
				criteria.add(Restrictions.eq(ScoDireitoAutorizacaoTemp.Fields.SCO_PONTOS_SERVIDORES.toString(), pontoServidor));
			}
			
			if (servidor  != null && servidor.getId() != null
					&& ((RapServidoresId) servidor.getId()).getVinCodigo() != null) {
				criteria.add(Restrictions.eq(ScoDireitoAutorizacaoTemp.Fields.RAP_SERVIDORES.toString(), servidor));
			}
			
			return criteria;
			
			
			
		}
	
}
