package br.gov.mec.aghu.prescricaomedica.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpaCadOrdCuidado;
import br.gov.mec.aghu.model.MpaUsoOrdCuidado;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmMensPrcrCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.core.commons.CoreUtil;
/**
 * 
 * @modulo prescricaomedica.cadastrosbasicos
 *
 */
public class MpmCuidadoUsualDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmCuidadoUsual> {

	private static final long serialVersionUID = -5916206149025906013L;
	
	public MpmCuidadoUsual obterCuidadoUsual(final Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCuidadoUsual.class);
		criteria.createAlias(MpmCuidadoUsual.Fields.TIPO_FREQ_APRAZAMENTOS.toString(), "TFP", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.SEQ.toString(), seq));
		return (MpmCuidadoUsual) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria obterCriteriaCuidadoUsual(Object cuidadoUsualPesquisa) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsual.class);
		
		criteria.createAlias(MpmCuidadoUsual.Fields.TIPO_FREQ_APRAZAMENTOS.toString(), "TFP", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));

		String strParametro = (String) cuidadoUsualPesquisa;
		Integer seqCuidadoUsual = null;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			seqCuidadoUsual = Integer.valueOf(strParametro);
		}
		if (seqCuidadoUsual != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.SEQ.toString(),
					seqCuidadoUsual));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						MpmCuidadoUsual.Fields.DESCRICAO.toString(),
						strParametro.toUpperCase(), MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	public List<MpmCuidadoUsual> obterListaCuidadoUsual(Object cuidadoUsualPesquisa) {
		DetachedCriteria criteria = obterCriteriaCuidadoUsual(cuidadoUsualPesquisa);
		criteria.addOrder(Order.asc(MpmCuidadoUsual.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public Long obterListaCuidadoUsualCount(Object cuidadoUsualPesquisa) {
		DetachedCriteria criteria = obterCriteriaCuidadoUsual(cuidadoUsualPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna lista com todos os cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 */
	public List<MpmCuidadoUsual> getListaCuidadosUsuaisAtivosUnidade(
			Object descricao, AghUnidadesFuncionais unidade) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsual.class);

		criteria.setFetchMode(MpmCuidadoUsual.Fields.TIPO_FREQ_APRAZAMENTOS.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));

		String strParametro = (String) descricao;
		Integer seqCuidadoUsual = null;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			seqCuidadoUsual = Integer.valueOf(strParametro);
		}
		
		if (seqCuidadoUsual != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.SEQ.toString(),
					seqCuidadoUsual));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						MpmCuidadoUsual.Fields.DESCRICAO.toString(),
						strParametro.toUpperCase(), MatchMode.ANYWHERE));
			}
		}

		if (unidade != null) {
			DetachedCriteria criteriaUnidades = criteria
					.createCriteria(MpmCuidadoUsual.Fields.UNIDADES.toString());
			criteriaUnidades.add(Restrictions.eq(
					MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(),
					unidade));
		}

		criteria.addOrder(Order
				.asc(MpmCuidadoUsual.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Retorna o número de registros de cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 * 
	 * @param descricao
	 * @param unidade
	 * @return
	 */
	public Long getListaCuidadosUsuaisAtivosUnidadeCount(Object descricao,
			AghUnidadesFuncionais unidade) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsual.class);

		criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));

		String strParametro = (String) descricao;
		Integer seqCuidadoUsual = null;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			seqCuidadoUsual = Integer.valueOf(strParametro);
		}
		if (seqCuidadoUsual != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.SEQ.toString(),
					seqCuidadoUsual));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						MpmCuidadoUsual.Fields.DESCRICAO.toString(),
						strParametro.toUpperCase(), MatchMode.ANYWHERE));
			}
		}

		if (unidade != null) {
			DetachedCriteria criteriaUnidades = criteria
					.createCriteria(MpmCuidadoUsual.Fields.UNIDADES.toString());
			criteriaUnidades.add(Restrictions.eq(
					MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(),
					unidade));
		}

		return executeCriteriaCount(criteria);
	}

	public Long pesquisarCuidadoUsualCount(Integer codigo, String descricao,
			DominioSituacao situacao, AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = montarConsultaCuidadoUsual(codigo,
				descricao, situacao, null, unidadeFuncional);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarConsultaCuidadoUsual(Integer codigo,
			String descricao, DominioSituacao situacao, Boolean indCci,
			AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsual.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.SEQ.toString(),
					codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MpmCuidadoUsual.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_SITUACAO
					.toString(), situacao));
		}

		if (indCci != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_CCI.toString(), indCci));
		}
		
		if (unidadeFuncional != null) {
			DetachedCriteria criteriaUnidades = criteria
					.createCriteria(MpmCuidadoUsual.Fields.UNIDADES.toString());
			criteriaUnidades.add(Restrictions.eq(
					MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(),
					unidadeFuncional));
		}

		return criteria;
	}

	public List<MpmCuidadoUsual> pesquisarCuidadoUsual(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao,
			AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsual.class);
		
		criteria.createAlias(MpmCuidadoUsual.Fields.TIPO_FREQ_APRAZAMENTOS.toString(), "TFP", JoinType.LEFT_OUTER_JOIN);
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.SEQ.toString(),
					codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MpmCuidadoUsual.Fields.DESCRICAO
					.toString(), descricao.toUpperCase(), MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_SITUACAO
					.toString(), situacao));
		}

		if (unidadeFuncional != null) {
			DetachedCriteria criteriaUnidades = criteria
					.createCriteria(MpmCuidadoUsual.Fields.UNIDADES.toString());
			criteriaUnidades.add(Restrictions.eq(
					MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(),
					unidadeFuncional));
		}

		criteria.addOrder(Order
				.asc(MpmCuidadoUsual.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);

	}

	public List<AghUnidadesFuncionais> pesquisaUnidadesFuncionaisPorCuidadosUsuais(MpmCuidadoUsual cuidadoUsual) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCuidadoUsualUnf.class);
		
		criteria.add(Restrictions.eq(MpmCuidadoUsualUnf.Fields.CDU_SEQ.toString(), cuidadoUsual));

		return executeCriteria(criteria);
	}

	public List<MpmMensPrcrCuidado> pesquisaCuidadosPrescritosPorCuidadosUsuais(
			MpmCuidadoUsual cuidadoUsual) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmMensPrcrCuidado.class);

		criteria.add(Restrictions.eq(MpmMensPrcrCuidado.Fields.CDU.toString(),
				cuidadoUsual));

		return executeCriteria(criteria);
	}

	public List<MpmModeloBasicoCuidado> pesquisaModeloBasicoPrescricaoPorCuidadosUsuais(
			MpmCuidadoUsual cuidadoUsual) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoCuidado.class);

		criteria.add(Restrictions.eq(MpmModeloBasicoCuidado.Fields.CDU
				.toString(), cuidadoUsual));

		return executeCriteria(criteria);
	}

	public List<MpmPrescricaoCuidado> pesquisaPrescricaoCuidadosPorCuidadosUsuais(
			MpmCuidadoUsual cuidadoUsual) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);

		criteria.add(Restrictions.eq(
				MpmPrescricaoCuidado.Fields.CDU.toString(), cuidadoUsual));

		return executeCriteria(criteria);
	}

	public List<MpaUsoOrdCuidado> pesquisaOrdemCuidadosPorCuidadosUsuais(
			MpmCuidadoUsual cuidadoUsual) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpaUsoOrdCuidado.class);

		criteria.add(Restrictions.eq(MpaUsoOrdCuidado.Fields.CDU.toString(),
				cuidadoUsual));

		return executeCriteria(criteria);
	}

	public List<MpaCadOrdCuidado> pesquisaCadastroOrdemCuidadosPorCuidadosUsuais(
			MpmCuidadoUsual cuidadoUsual) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpaCadOrdCuidado.class);

		criteria.add(Restrictions.eq(MpaCadOrdCuidado.Fields.CDU.toString(),
				cuidadoUsual));

		return executeCriteria(criteria);
	}

	public List<MptPrescricaoCuidado> pesquisaPrescricaoDeCuidadosPorCuidadosUsuais(
			MpmCuidadoUsual cuidadoUsual) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MptPrescricaoCuidado.class);

		criteria.add(Restrictions.eq(
				MptPrescricaoCuidado.Fields.CDU.toString(), cuidadoUsual));

		return executeCriteria(criteria);
	}

	/**
	 * Método que obtem o valor do campo vlrNumerico da tabela AGH_PARAMETROS de
	 * acordo com o nome informado
	 * 
	 * @param nome
	 * @return
	 */
	public BigDecimal obterValorNumericoAghParametros(String nome) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghParametros.class);

		criteria.add(Restrictions
				.eq(AghParametros.Fields.NOME.toString(), nome));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AghParametros.Fields.VLR_NUMERICO
						.toString())));

		BigDecimal vlrNumerico = (BigDecimal) this
				.executeCriteriaUniqueResult(criteria);

		return vlrNumerico;
	}

	public List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual(
			MpmCuidadoUsual cuidado) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);

		criteria.add(Restrictions.eq(MpmCuidadoUsualUnf.Fields.CDU_SEQ
				.toString(), cuidado));

		return executeCriteria(criteria);
	}
	
	public List<MpmCuidadoUsual> listarCuidadosMedicos(Integer codigo, String descricao, Boolean indCci,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarConsultaCuidadoUsual(codigo, descricao, null, indCci, null);
		criteria.addOrder(Order.asc(MpmCuidadoUsual.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarCuidadosMedicosCount(Integer codigo, String descricao, Boolean indCci) {
		DetachedCriteria criteria = montarConsultaCuidadoUsual(codigo, descricao, null, indCci, null);
		
		return executeCriteriaCount(criteria);
	}

	public List<MpmCuidadoUsual> listarCuidados(String strPesquisa) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCuidadoUsual.class);
		
		criteria.setFetchMode(MpmCuidadoUsual.Fields.TIPO_FREQ_APRAZAMENTOS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(MpmCuidadoUsual.Fields.FREQUENCIA.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(MpmCuidadoUsual.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));			
		}
		criteria.addOrder(Order.asc(MpmCuidadoUsual.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);				
	}

	public long listarCuidadosCount(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCuidadoUsual.class);
		criteria.add(Restrictions.eq(MpmCuidadoUsual.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(MpmCuidadoUsual.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));			
		}
		return executeCriteriaCount(criteria);		
	}
	
}
