package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;


public class AghCaractUnidFuncionaisDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCaractUnidFuncionais> {

	private static final long serialVersionUID = -4068547984264740649L;

	/**
	 * DEPRECATED = Usar AghCaractUnidFuncionaisDAO.verificarCaracteristicaUnidadeFuncional pois retorna Boolean e não DominioSimNao.
	 * 
	 * Verifica se uma unidade funcional tem determinada característica, se
	 * tiver retorna 'S' senão retorna 'N'.
	 * 
	 * ORADB Function AGHC_VER_CARACT_UNF.
	 * 
	 * @return Valor 'S' ou 'N' indicando se a característica pesquisada foi
	 *         encontrada.AelNotaAdicionalRN
	 */
	@Deprecated
	public DominioSimNao verificarCaracteristicaDaUnidadeFuncional(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		DominioSimNao result = null;
		AghCaractUnidFuncionaisId id = null;
		AghCaractUnidFuncionais aghCUF = null;

		if (unfSeq != null && caracteristica != null) {
			id = new AghCaractUnidFuncionaisId(unfSeq, caracteristica);
			aghCUF = obterPorChavePrimaria(id);
		}

		result = (aghCUF == null) ? DominioSimNao.N : DominioSimNao.S;

		return result;
	}
	
	public List<AghCaractUnidFuncionais> verificarCaracteristicaDaUnidadeFuncional(
			List<Integer> atdSeqs,
			ConstanteAghCaractUnidFuncionais caracteristica) {
		
		if (atdSeqs == null || atdSeqs.isEmpty()) {
			return new LinkedList<AghCaractUnidFuncionais>();
		}
		
		String fieldFilter = "atd." + AghAtendimentos.Fields.SEQ.toString();
		String filterIN = CoreUtil.criarClausulaIN(fieldFilter, "where", atdSeqs);
		
		StringBuffer hql = new StringBuffer(100);
		
		hql.append(" select c from ").append(AghCaractUnidFuncionais.class.getSimpleName()).append(" c where c.")
		.append(AghCaractUnidFuncionais.Fields.UNIDADE_FUNCIONAL.toString())
		.append('.')
		.append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
		.append(" in ( select atd.")
			.append(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString())
			.append('.')
			.append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
			.append(" from ").append(AghAtendimentos.class.getSimpleName()).append(" atd")
			.append(filterIN)
		.append(") and c.")
		.append(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString())
		.append(" = '")
		.append(ConstanteAghCaractUnidFuncionais.PEN_INFORMATIZADA.getCodigo())
		.append("' ");
		
		final javax.persistence.Query query = createQuery(hql.toString());
		
		return query.getResultList();
	}

	/**
	 * TODO 
	 * 	2011.02.15 - gandriotti : deveria ser um join usando {@link DetachedCriteria} mas desisti.
	 * @author gandriotti
	 * @param caracteristica
	 * @return
	 */
	public List<AghUnidadesFuncionais> obterListaUnidadesFuncionaisAtivasPorCaracteristica(ConstanteAghCaractUnidFuncionais caracteristica) {
		
		List<AghUnidadesFuncionais> result = null;
		List<AghCaractUnidFuncionais> partial = null;
		DetachedCriteria criteria = null;
		AghUnidadesFuncionais unf = null;
		
		criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		criteria.add(Restrictions.eq(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), 
				caracteristica));
		partial = this.executeCriteria(criteria);
		result = new LinkedList<AghUnidadesFuncionais>(); 
		for (AghCaractUnidFuncionais caract : partial) {
			unf = caract.getUnidadeFuncional();
			if (unf.getIndSitUnidFunc().isAtivo()) {
				result.add(unf);
			}
		}
		
		return result;
	}

	public AghCaractUnidFuncionais buscarCaracteristicaPorUnidadeCaracteristica(
			Short seq,ConstanteAghCaractUnidFuncionais constanteCaract) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		criteria.createAlias(AghCaractUnidFuncionais.Fields.UNIDADE_FUNCIONAL.toString(), "ufe");
		criteria.add(Restrictions.eq(
				"ufe."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), 
				seq));
		criteria.add(Restrictions.eq(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), 
				constanteCaract));
		return (AghCaractUnidFuncionais)this.executeCriteriaUniqueResult(criteria);
	}

	public List<AghCaractUnidFuncionais> listarCaracteristicasUnidadesFuncionais(Short unfSeq,
			ConstanteAghCaractUnidFuncionais[] caracteristicas, Integer firstResult, Integer maxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);

		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), unfSeq));

		if (caracteristicas != null && caracteristicas.length > 0) {
			criteria.add(Restrictions.in(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristicas));
		}
		
		if (firstResult != null && maxResults != null) {
			return executeCriteria(criteria, firstResult, maxResults, null, false);
		} else {
			return executeCriteria(criteria);
		}
	}
	
	public List<Short> pesquisarUnfSeqsPorCaracteristicasRelatorioPrescricaoPorUnidade(
			List<ConstanteAghCaractUnidFuncionais> listaCaracteristicas) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())));
		
		criteria.add(Restrictions.in(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),listaCaracteristicas));

		return executeCriteria(criteria);
	}

	public List<ConstanteAghCaractUnidFuncionais> listarCaractUnidFuncionais(Short unfSeq,
			ConstanteAghCaractUnidFuncionais[] caracteristicas, Integer firstResult, Integer maxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString())));

		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), unfSeq));

		criteria.add(Restrictions.in(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristicas));

		if (firstResult != null && maxResults != null) {
			return executeCriteria(criteria, firstResult, maxResults, null, false);
		} else {
			return executeCriteria(criteria);
		}
	}
	
	public List<AghCaractUnidFuncionais> pesquisarCaracteristicasUnidadeFuncionalPorCaracteristica(
			List<ConstanteAghCaractUnidFuncionais> listaCaracteristicas) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCaractUnidFuncionais.class);

		criteria.add(Restrictions.in(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA
						.toString(), listaCaracteristicas));

		return executeCriteria(criteria);
	}

	/**
	 * Método para fazer busca de unidade funcional pelo seu seq e sua
	 * característica.
	 * 
	 * @param seq
	 *            da unidade funcional
	 * @param caracteristica
	 * @return lista de unidades funcionais
	 */
	public List<AghCaractUnidFuncionais> pesquisarCaracteristicaUnidadeFuncional(Short seq,
			ConstanteAghCaractUnidFuncionais caracteristica) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristica));

		return executeCriteria(criteria);
	}
	
	public boolean possuiCaracteristicaPorUnidadeEConstante(Short seqUnidadeFuncional, ConstanteAghCaractUnidFuncionais caracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		criteria.createAlias(AghCaractUnidFuncionais.Fields.UNIDADE_FUNCIONAL.toString(), "uf");		
		criteria.add(Restrictions.eq("uf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnidadeFuncional));
		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristica));

		return executeCriteriaCount(criteria) > 0;
	}
	

	public List<AghCaractUnidFuncionais> listaCaracteristicasUnidadesFuncionaisPaciente(Short unfSeq,
			ConstanteAghCaractUnidFuncionais caracteristica) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCaractUnidFuncionais.class);
		criteria.add(Restrictions.eq(
				AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA
						.toString(), caracteristica));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Verifica se a unidade funcional possui caracteristica passada por parametro.
	 * Criação deste método usando boolean pois DominioSimNao está deprecated.
	 * @param unfSeq
	 * @param caracteristica
	 * @return Boolean
	 */
	public Boolean verificarCaracteristicaUnidadeFuncional(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		Boolean result = null;
		AghCaractUnidFuncionaisId id = null;
		AghCaractUnidFuncionais aghCUF = null;

		if (unfSeq != null && caracteristica != null) {
			id = new AghCaractUnidFuncionaisId(unfSeq, caracteristica);
			aghCUF = obterPorChavePrimaria(id);
		}
		result = (aghCUF == null) ? Boolean.FALSE : Boolean.TRUE;
		return result;
	}
	
	public List<AghCaractUnidFuncionais> listarCaractUnidFuncionaisEUnidadeFuncional(String objPesquisa, ConstanteAghCaractUnidFuncionais caracteristica) {
		DetachedCriteria criteria = montarCriteriaParaListarCaractUnidFuncionaisEUnidadeFuncional(objPesquisa, caracteristica);
		criteria.addOrder(Order.asc("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		return executeCriteria(criteria);
	}

	public DetachedCriteria montarCriteriaParaListarCaractUnidFuncionaisEUnidadeFuncional(String objPesquisa, ConstanteAghCaractUnidFuncionais caracteristica) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "car");
		criteria.createAlias(AghCaractUnidFuncionais.Fields.UNIDADE_FUNCIONAL.toString(), "unf");		
		
		if (StringUtils.isNotBlank(objPesquisa)) {
			Criterion criterionDescCodigo = null;
			if (CoreUtil.isNumeroShort(objPesquisa)) {
				short codigo = Short.parseShort(objPesquisa);
				criterionDescCodigo = Restrictions.eq("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo);
			} else {
				criterionDescCodigo = Restrictions.ilike("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE);
			}
			criteria.add(criterionDescCodigo);
		}

		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristica));

		return criteria;
	}	
	
	public List<AghCaractUnidFuncionais> pesquisarCaracteristicaComAtendimentoPorPaciente(Integer pacCodigo, Date dtHrFim,
			List<DominioOrigemAtendimento> origensAtendimento,
			ConstanteAghCaractUnidFuncionais  caracteristica) {
		
		StringBuffer hql = new StringBuffer(306);
		hql.append(" select cuf from \n ")
		.append(AghCaractUnidFuncionais.class.getSimpleName()).append( " cuf, \n ")
		.append(AghAtendimentos.class.getSimpleName()).append(" atd \n where \n atd.paciente.codigo = :pacCodigo \n and cuf.id.unfSeq = atd.unidadeFuncional.seq \n");
		
		if(dtHrFim != null){
			hql.append(" and ( \n atd." + AghAtendimentos.Fields.DTHR_FIM + " >= :dtHrFim \n OR  atd.")
			.append(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO).append(" = 'S' \n ) \n");
		}
		if(origensAtendimento != null){
			hql.append(" and atd.").append(AghAtendimentos.Fields.ORIGEM).append(" IN (:origensAtendimento) \n");
		}
		if(caracteristica != null){
			hql.append(" and cuf.id.caracteristica IN (:caracteristica) \n");
		}
			
		Query q = createHibernateQuery(hql.toString());
		q.setInteger("pacCodigo", pacCodigo);
		if(dtHrFim != null){
			q.setTimestamp("dtHrFim", dtHrFim);
		}
		if(origensAtendimento != null){
			q.setParameterList("origensAtendimento", origensAtendimento);
		}
		if(caracteristica != null){
			q.setParameter("caracteristica", caracteristica);
		}
		
		return q.list();
	}
	
	public Long listarCaractUnidFuncionaisEUnidadeFuncionalCount(String objPesquisa, ConstanteAghCaractUnidFuncionais caracteristica) {
		DetachedCriteria criteria = montarCriteriaParaListarCaractUnidFuncionaisEUnidadeFuncional(objPesquisa, caracteristica);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #34722 - Consulta utilizada para verificar se determinada unidade funcional possui determinada característica associada
	 * @param unfSeq
	 * @param caracteristica
	 * @return
	 */
	public Boolean existeCaractUnidFuncionaisPorSeqCaracteristica(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristica));
		return executeCriteriaCount(criteria) > 0;
	}
	
	/**
	 *  Serviço #38488
	 *  Utilizado na estória #26325
	 *  @retun UNF_SEQ 
	 *  (Unidades funcionais com característica de unidade executora de cirurgias e Centro Obstétrico.)
	 */
	@SuppressWarnings("unchecked")
	public List<Short> pesquisarUnidFuncExecutora(){
		
		StringBuilder hql = new StringBuilder(205);
		
		hql.append("SELECT CUF.UNF_SEQ FROM AGH.")
		.append(AghCaractUnidFuncionais.class.getAnnotation(Table.class).name()).append(" CUF WHERE CUF.CARACTERISTICA = '")
		.append(ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS.getCodigo())		
		.append("' INTERSECT SELECT CUF.UNF_SEQ FROM AGH.")
		.append(AghCaractUnidFuncionais.class.getAnnotation(Table.class).name())
		.append(" CUF WHERE CUF.CARACTERISTICA = '").append(ConstanteAghCaractUnidFuncionais.CO.getCodigo()).append("' ");		
		
		Query query = this.createSQLQuery(hql.toString());
		
		List<Short> listaRetorno = new ArrayList<Short>();
		List<Object> results = query.list();
		for (Object objectResult : results) {
			listaRetorno.add(Short.valueOf(objectResult.toString()));
		}
		
		return listaRetorno;
	}
	
	public List<Short> verificaPrimeiraPrescricaoMedicaPacienteUnidFuncional(Integer atdSeq, String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "CUF");
		criteria.createAlias("CUF." + AghCaractUnidFuncionais.Fields.UNIDADE_FUNCIONAL.toString(), "UFU");
		criteria.createAlias("UFU." + AghUnidadesFuncionais.Fields.ATENDIMENTO.toString(), "ATE");
		criteria.createAlias("ATE." + AghAtendimentos.Fields.MPM_MOTIVO_INGRESSO_CTI.toString(), "MIC");
		
		criteria.setProjection(Projections.property("UFU." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		
		criteria.add(Restrictions.eqProperty("MIC." + MpmMotivoIngressoCti.Fields.UNF_SEQ.toString(),
				"ATE." + AghAtendimentos.Fields.UNF_SEQ.toString()));
		
		DetachedCriteria subExists = DetachedCriteria.forClass(AghParametros.class, "PRM");
		subExists.setProjection(Projections.property("PRM." + AghParametros.Fields.VLR_TEXTO.toString()));
		subExists.add(Restrictions.eqProperty("PRM." + AghParametros.Fields.VLR_TEXTO.toString(),
				"CUF." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()));
		subExists.add(Restrictions.eq("PRM."+ AghParametros.Fields.NOME.toString(), parametro));
		
		criteria.add(Subqueries.exists(subExists));
		
		criteria.add(Restrictions.eq("ATE."+ AghAtendimentos.Fields.SEQ.toString(),atdSeq));
		criteria.add(Restrictions.eq("ATE."+ AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		return executeCriteria(criteria);
	}

	public List<Short> pesquisarUnidadesFuncionaisPorCaracteristica(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "CUF");
		
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("CUF." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())));
		
		DetachedCriteria subExists = DetachedCriteria.forClass(AghParametros.class, "PRM");
		subExists.setProjection(Projections.property("PRM." + AghParametros.Fields.VLR_TEXTO.toString()));
		subExists.add(Restrictions.eqProperty("PRM." + AghParametros.Fields.VLR_TEXTO.toString(),
				"CUF." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()));
		subExists.add(Restrictions.eq("PRM."+ AghParametros.Fields.NOME.toString(), parametro));
		
		criteria.add(Subqueries.exists(subExists));
		
		return executeCriteria(criteria);
	}
}
