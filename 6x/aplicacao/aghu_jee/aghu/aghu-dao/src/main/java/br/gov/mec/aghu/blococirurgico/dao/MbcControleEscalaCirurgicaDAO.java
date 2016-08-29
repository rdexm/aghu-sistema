package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;

public class MbcControleEscalaCirurgicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcControleEscalaCirurgica> {

	private static final long serialVersionUID = 2998744184543872037L;

	/**
	 * pattern da data dd/MM/yyyy
	 * @return boolean
	 */
	public boolean verificaExistenciaPeviaDefinitivaPorUNFData(Short unf_seq, Date dataPesquisa,DominioTipoEscala tipo ){
		DetachedCriteria criteria = criarCriteriaObterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(
				unf_seq, dataPesquisa);

		if(tipo !=null){
			criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.TIPO_ESCALA.toString(), tipo));
		}
		
		return executeCriteriaExists(criteria);
	}

	public MbcControleEscalaCirurgica obterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(Short unfSeq, Date dataEscala){
		DetachedCriteria criteria = criarCriteriaObterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(unfSeq, dataEscala);
		
		return (MbcControleEscalaCirurgica) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria criarCriteriaObterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(Short unfSeq, Date dataEscala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcControleEscalaCirurgica.class);

		criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString(), dataEscala));
		criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		return criteria;
	}
	
	public MbcControleEscalaCirurgica obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
			Short unfSeq, Date dataAgenda, DominioTipoEscala tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcControleEscalaCirurgica.class);
		
		Date dtInicio = DateUtil.truncaData(dataAgenda);
		Date dtFim = DateUtil.obterDataComHoraFinal(dataAgenda);
		
		// Condição: TRUNC(DT_ESCALA) = TRUNC(C_DT_ESCALA)
		criteria.add(Restrictions.or(Restrictions.gt(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString(), dtInicio),
				Restrictions.eq(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString(), dtInicio)));
		criteria.add(Restrictions.or(Restrictions.lt(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString(), dtFim),
				Restrictions.eq(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString(), dtFim)));
		
		criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(tipo != null){
			criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.TIPO_ESCALA.toString(), tipo));
		}
		
		return (MbcControleEscalaCirurgica) executeCriteriaUniqueResult(criteria);
	}	
	
	/**
	 * pesquisarEscalasCirurgicas
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unidadeFunc
	 * @return
	 */
	public List<MbcControleEscalaCirurgica> pesquisarEscalasCirurgicas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,  AghUnidadesFuncionais unidadeFunc) {
		DetachedCriteria criteria = montarCriteriaLista(unidadeFunc);

		// Ordenar por data escala
		criteria.addOrder(Order.desc(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Contabiliza resultados da pesquisa paginada de escalas cirurgicas
	 * 
	 * @param unidadeFunc, dataEscala
	 * @return
	 */
	public Long pesquisarEscalasCirurgicasCount( AghUnidadesFuncionais unidadeFunc) {
		DetachedCriteria criteria = montarCriteriaLista(unidadeFunc);
		return executeCriteriaCount(criteria);
	}

	
	/**
	 * Criteria pesquisa de Escalas Cirurgicas
	 * @param unidadeFunc
	 * @param dataEscala
	 * @return
	 */
	private DetachedCriteria montarCriteriaLista( AghUnidadesFuncionais unidadeFunc) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcControleEscalaCirurgica.class);

		criteria.createAlias(MbcControleEscalaCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UND", JoinType.INNER_JOIN);
		criteria.createAlias(MbcControleEscalaCirurgica.Fields.RAP_SERVIDORES.toString(), "RAP", JoinType.INNER_JOIN);
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS", JoinType.INNER_JOIN);
	
		if (unidadeFunc != null) {
			criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), unidadeFunc));
		}
	
		return criteria;
	}
	
	/**
	 * Pesquisa controle de escala cirúrgica por unidade funcional e data da cirurgia
	 * @param unfSeq
	 * @param dataCirurgia
	 * @return
	 */
	public List<MbcControleEscalaCirurgica> pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(Short unfSeq, Date dataCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcControleEscalaCirurgica.class);
		criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));

		Object[] values = { DateUtil.truncaData(dataCirurgia)}; 
		Type[] types = { DateType.INSTANCE};
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction("trunc({alias}." + MbcControleEscalaCirurgica.Fields.DT_ESCALA.name() + ") = ?", values, types));
		} else {
			criteria.add(Restrictions.sqlRestriction("date_trunc('day',{alias}." + MbcControleEscalaCirurgica.Fields.DT_ESCALA.name() + ") = ?", values, types));
		}
		
		criteria.add(Restrictions.eq(MbcControleEscalaCirurgica.Fields.TIPO_ESCALA.toString(), DominioTipoEscala.D));
		return executeCriteria(criteria);
	}
	
	public List<MbcControleEscalaCirurgica> pesquisarEscalasCirurgicasPorUnf(AghUnidadesFuncionais unidadeFunc) {
		DetachedCriteria criteria = montarCriteriaLista(unidadeFunc);

		// Ordenar por data escala
		criteria.addOrder(Order.asc(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString()));
		
		return executeCriteria(criteria);
	}
	
	public void removerMbcControleEscalaCirurgica(MbcControleEscalaCirurgica escalaCirurgica){
		desatachar(escalaCirurgica);
		MbcControleEscalaCirurgica escala = obterPorChavePrimaria(escalaCirurgica.getId());
		remover(escala);
	}
	
	
}