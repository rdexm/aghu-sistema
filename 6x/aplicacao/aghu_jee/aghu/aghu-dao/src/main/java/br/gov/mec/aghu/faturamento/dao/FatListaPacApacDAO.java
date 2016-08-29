package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatEspecialidadeTratamento;
import br.gov.mec.aghu.model.FatListaPacApac;
import br.gov.mec.aghu.model.FatTipoTratamentos;

public class FatListaPacApacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatListaPacApac> {

	private static final long serialVersionUID = 8908129323020547609L;

	public List<FatListaPacApac> buscarFatListaPacApac(){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatListaPacApac.class,"lpp");
		
		criteria.createAlias("lpp."+FatListaPacApac.Fields.TIPO_TRATAMENTO.toString(), "tpt");
		criteria.add(Restrictions.isNotNull("lpp."+FatListaPacApac.Fields.DT_CONFIRMADO.toString()));
		criteria.add(Restrictions.eq(FatListaPacApac.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		DetachedCriteria criteriaEspecialidade = 
				criteria.createCriteria("tpt."+FatTipoTratamentos.Fields.ESPECIALIDADE_TRATAMENTO.toString(), "etr",
				Criteria.INNER_JOIN);
		
		criteriaEspecialidade.add(Restrictions.eq(FatEspecialidadeTratamento.Fields.IND_LISTA_CANDIDATO.toString(), "S"));
		
		criteria.addOrder(Order.asc("tpt."+FatTipoTratamentos.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}	
	
	/**
	 * verificar se o paciente passou uma APAC com data de confirmação não nula no período de um ano
	 * @param pacCodigo
	 * @param dataConsulta
	 * @return
	 */
	public FatListaPacApac obterApacPacientePorAno(Integer pacCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatListaPacApac.class,"L");
		criteria.createAlias("L." + FatListaPacApac.Fields.TIPO_TRATAMENTO.toString(), "T");
		criteria.createAlias("L." + FatListaPacApac.Fields.PACICENTE.toString(), "P");
		criteria.add(Restrictions.eq("L." + FatListaPacApac.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNotNull("L." + FatListaPacApac.Fields.DT_CONFIRMADO.toString()));
		criteria.add(Restrictions.eq("L." + FatListaPacApac.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.desc("L." + FatListaPacApac.Fields.DT_CONFIRMADO.toString()));
		
		List<FatListaPacApac> lista = executeCriteria(criteria);
		if(!lista.isEmpty()){
			return lista.get(0);
		}else{
			return null;
		}
	}
	
}