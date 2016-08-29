package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.VRapPessoaServidorVO;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.registrocolaborador.vo.RapPessoalServidorVO;
import br.gov.mec.aghu.registrocolaborador.vo.VRapPessoaServidorCriteria;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;

/**
 * 
 * @modulo registrocolaborador
 *
 */
public class VRapPessoaServidorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VRapPessoaServidor> {

	private static final long serialVersionUID = 9058348240124688863L;

	protected VRapPessoaServidorDAO() {
	}
	
	/**
	 * Efetua as pesquisas de servidores ativos/ programados por Vínculo e matrícula; descrição do vínculo ou nome
	 * 
	 * @param pesquisa Vínculo e matrícula; descrição do vínculo ou nome
	 * @return List<RapServidores>
	 */
	public List<RapServidores> pesquisarServidoresPorCodigoDescricao(Object objetoPesquisa) {
		VRapPessoaServidorCriteria criteria = new VRapPessoaServidorCriteria();
		criteria.setFiltro(objetoPesquisa);
		criteria.setFiltroRestriction(VRapPessoaServidorCriteria.FiltroRestriction.PARTIAL);
		
		Set<DominioSituacaoVinculo> situacoes = new HashSet<DominioSituacaoVinculo>();
		situacoes.add(DominioSituacaoVinculo.A);
		situacoes.add(DominioSituacaoVinculo.P);
		criteria.setSituacoesVinculo(situacoes);
		
		criteria.setFimVinculoBase(Calendar.getInstance().getTime());
		
		return pesquisarServidores(criteria);
	}
	
	public Integer pesquisarServidoresPorCodigoDescricaoCount(Object objetoPesquisa) {
		VRapPessoaServidorCriteria criteria = new VRapPessoaServidorCriteria();
		criteria.setFiltro(objetoPesquisa);
		criteria.setFiltroRestriction(VRapPessoaServidorCriteria.FiltroRestriction.FULL);
		
		Set<DominioSituacaoVinculo> situacoes = new HashSet<DominioSituacaoVinculo>();
		situacoes.add(DominioSituacaoVinculo.A);
		situacoes.add(DominioSituacaoVinculo.P);
		criteria.setSituacoesVinculo(situacoes);
		
		criteria.setFimVinculoBase(Calendar.getInstance().getTime());
		
		return pesquisarServidoresCount(criteria);
	}
	
	public List<RapServidores> pesquisarServidores(VRapPessoaServidorCriteria criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
		
		restrictSituacaoVinculo(detached, criteria);
		
		if (criteria.getFimVinculoBase() != null) {
			detached.add(Restrictions.or(Restrictions.isNull("VRAP."
					+ VRapPessoaServidor.Fields.DT_FIM_VINCULO), Restrictions
					.ge("VRAP." + VRapPessoaServidor.Fields.DT_FIM_VINCULO,
							criteria.getFimVinculoBase())));
		}
		
		restrictFiltro(detached, criteria);
	
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString()), VRapPessoaServidor.Fields.VINDESCRICAO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		detached.setProjection(p);
		
		// order by nome asc
		detached.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.NOME.toString()));
		
		List<Object[]> resultado = executeCriteria(detached, 0, 100, null, false);
		
		return converterParaRapServidores(resultado);
	}
	
	public Integer pesquisarServidoresCount(VRapPessoaServidorCriteria criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
		
		restrictSituacaoVinculo(detached, criteria);
		
		if (criteria.getFimVinculoBase() != null) {
			detached.add(Restrictions.or(Restrictions.isNull("VRAP."
					+ VRapPessoaServidor.Fields.DT_FIM_VINCULO), Restrictions
					.ge("VRAP." + VRapPessoaServidor.Fields.DT_FIM_VINCULO,
							criteria.getFimVinculoBase())));
		}
		
		restrictFiltro(detached, criteria);
	
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString()), VRapPessoaServidor.Fields.VINDESCRICAO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		detached.setProjection(p);
		
		// order by nome asc
		detached.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.NOME.toString()));
		
		List<Object[]> resultado = executeCriteria(detached);
		
		return converterParaRapServidoresCount(resultado);
	}
	
	private void restrictFiltro(DetachedCriteria detached,
			VRapPessoaServidorCriteria criteria) {
		if (criteria.getFiltro() != null) {
			switch (criteria.getFiltroRestriction()) {
			case PARTIAL:
				restrictPartialFiltro(detached, criteria);
				break;
			case FULL:
				restrictFullFiltro(detached, criteria);
				break;
			}
		}
	}

	private void restrictPartialFiltro(DetachedCriteria detached,
			VRapPessoaServidorCriteria criteria) {
		String stParametro = (String) criteria.getFiltro();
		Integer matricula = null;
		
		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}
		
		if (matricula != null) {
			detached.add(Restrictions.eq("VRAP."
					+ VRapPessoaServidor.Fields.MATRICULA.toString(), matricula));
		} else if (StringUtils.isNotBlank(stParametro)) {
			detached.add(Restrictions.ilike("VRAP."
					+ VRapPessoaServidor.Fields.NOME.toString(), stParametro,
					MatchMode.ANYWHERE));
		}
	}
		
	private void restrictFullFiltro(DetachedCriteria detached,
			VRapPessoaServidorCriteria criteria) {
		LogicalExpression orDescricoes = null;
		LogicalExpression andVinMatricula = null;
		String strParametro = null;
		Object objetoPesquisa = criteria.getFiltro();
		if(objetoPesquisa != null) {
			strParametro = objetoPesquisa.toString();
		}
		
		if (StringUtils.isNotBlank(strParametro)){
			String[] arrayVinculoMatricula = strParametro.split(" ");
			orDescricoes = Restrictions.or(Restrictions.ilike("VRAP."
					+ VRapPessoaServidor.Fields.NOME.toString(), strParametro,
					MatchMode.ANYWHERE), Restrictions.ilike("VRAP."
					+ VRapPessoaServidor.Fields.VINDESCRICAO.toString(),
					strParametro, MatchMode.ANYWHERE));
			if (arrayVinculoMatricula.length == 2
					&& CoreUtil.isNumeroShort(arrayVinculoMatricula[0])
					&& CoreUtil.isNumeroInteger(arrayVinculoMatricula[1])){
				andVinMatricula = Restrictions.and(
						Restrictions.eq(
								"VRAP."
										+ VRapPessoaServidor.Fields.VINCODIGO
												.toString(),
								Short.valueOf(arrayVinculoMatricula[0])),
						Restrictions.eq(
								"VRAP."
										+ VRapPessoaServidor.Fields.MATRICULA
												.toString(),
								Integer.valueOf(arrayVinculoMatricula[1])));
				detached.add(Restrictions.or(andVinMatricula, orDescricoes));
			} else 	{
				detached.add(orDescricoes);
			}
			}
		}
	
	private void restrictSituacaoVinculo(DetachedCriteria detached,
			VRapPessoaServidorCriteria criteria) {
		Set<DominioSituacaoVinculo> situacoes = criteria.getSituacoesVinculo(); 
		
		if (situacoes != null) {
			String field = "VRAP." + VRapPessoaServidor.Fields.SITUACAO.toString();
		
			if (situacoes.isEmpty()) {
				detached.add(Restrictions.isNull(field));
			} else if (criteria.getSituacoesVinculo().size() == 1) {
				detached.add(Restrictions.eq(field, situacoes.iterator().next().toString()));
			} else {
				List<String> list = new ArrayList<String>();
		
				for (DominioSituacaoVinculo s: situacoes) {
					list.add(s.toString());
	}
	
				detached.add(Restrictions.in(field, list));
			}
		}
	}
	
	/**
	 * Efetua as pesquisas de servidores ativos/ programados por Vínculo e matrícula; descrição do vínculo ou nome
	 * 
	 * @param pesquisa Vínculo e matrícula; descrição do vínculo ou nome
	 * @return List<RapServidores>
	 */
	public List<RapServidores> pesquisarServidoresPorCodigoOuDescricao(Object objetoPesquisa) {
		DetachedCriteria criteria = obterCriteriapesquisarServidoresPorCodigoOuDescricao(objetoPesquisa);
		
		// order by nome asc
		criteria.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.NOME.toString()));
		
		List<Object[]> resultado = executeCriteria(criteria, 0, 100, null, false);
		
		return converterParaRapServidores(resultado);
	}
	
	public Long pesquisarServidoresPorCodigoOuDescricaoCount(Object objetoPesquisa){
		
		DetachedCriteria criteria = obterCriteriapesquisarServidoresPorCodigoOuDescricao(objetoPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria obterCriteriapesquisarServidoresPorCodigoOuDescricao(Object objetoPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
		LogicalExpression orDescricoes = null;
		
		// ind_situacao = 'A' ou 'P'
		SimpleExpression dominioA = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.A.toString());
		SimpleExpression dominioP = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.P.toString());
		criteria.add(Restrictions.or(dominioA, dominioP));
		
		// dt_fim_vinculo is null ou dt_fim_vinculo >= sysdate
		criteria.add(Restrictions.or(Restrictions.isNull("VRAP." + VRapPessoaServidor.Fields.DT_FIM_VINCULO.toString()), 
									Restrictions.ge("VRAP." + VRapPessoaServidor.Fields.DT_FIM_VINCULO, Calendar.getInstance().getTime())));
		
		String strParametro = null;
		if(objetoPesquisa != null) {
			strParametro = objetoPesquisa.toString();
		}
		
		if (StringUtils.isNotBlank(strParametro)){
			orDescricoes = Restrictions.or(Restrictions.ilike("VRAP." + VRapPessoaServidor.Fields.NOME.toString(), strParametro, MatchMode.ANYWHERE), 
					Restrictions.ilike("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			if(CoreUtil.isNumeroInteger(strParametro)){
				criteria.add(Restrictions.or(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), Integer.valueOf(strParametro)), orDescricoes));
			} else 	{
				criteria.add(orDescricoes);
			}
		}
	
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString()), VRapPessoaServidor.Fields.VINDESCRICAO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		criteria.setProjection(p);

		return criteria;
	}

	/**
	 * Recebe um array de Object e converte o resultado para uma lista de RapServidores.
	 * Utilizado internamente pelo método pesquisarServidoresPorCodigoDescricao.
	 * @param listaVRapPessoaServidores
	 * @return
	 */
	private List<RapServidores> converterParaRapServidores(List<Object[]> listaVRapPessoaServidores) {
		
		List<RapServidores> servidores = new ArrayList<RapServidores>();
		
		if(listaVRapPessoaServidores != null) {
			for(Object[] servidor : listaVRapPessoaServidores) {

				RapServidoresId id = new RapServidoresId();
				id.setVinCodigo((Short) servidor[0]);
				id.setMatricula((Integer) servidor[2]);

				RapVinculos vinculo = new RapVinculos();
				vinculo.setCodigo((Short) servidor[0]);
				vinculo.setDescricao((String) servidor[1]);
				
				RapPessoasFisicas pf = new RapPessoasFisicas();
				pf.setNome((String) servidor[3]);
				if(servidor.length > 4) {
					pf.setNomeUsual((String) servidor[4]); //#5488
				}
				
				RapServidores rapServidor = new RapServidores(id);
				rapServidor.setId(id);
				rapServidor.setVinculo(vinculo);
				rapServidor.setPessoaFisica(pf);

				servidores.add(rapServidor);
			}
		}
		return servidores;
	}
	
	private Integer converterParaRapServidoresCount(List<Object[]> listaVRapPessoaServidores) {
		
		List<RapServidores> servidores = new ArrayList<RapServidores>();
		
		if(listaVRapPessoaServidores != null) {
			for(Object[] servidor : listaVRapPessoaServidores) {

				RapServidoresId id = new RapServidoresId();
				id.setVinCodigo((Short) servidor[0]);
				id.setMatricula((Integer) servidor[2]);

				RapVinculos vinculo = new RapVinculos();
				vinculo.setCodigo((Short) servidor[0]);
				vinculo.setDescricao((String) servidor[1]);
				
				RapPessoasFisicas pf = new RapPessoasFisicas();
				pf.setNome((String) servidor[3]);
				if(servidor.length > 4) {
					pf.setNomeUsual((String) servidor[4]); //#5488
				}
				
				RapServidores rapServidor = new RapServidores(id);
				rapServidor.setId(id);
				rapServidor.setVinculo(vinculo);
				rapServidor.setPessoaFisica(pf);

				servidores.add(rapServidor);
			}
		}
		return servidores.size();
	}
	
	public String obterNomePessoaServidor(Integer cod, Integer matricula){
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class);
		
		criteria.setProjection(Projections.property(VRapPessoaServidor.Fields.NOME.toString()));
			
		criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.MATRICULA.toString(), matricula));
		
		criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.VINCODIGO.toString(), cod.shortValue()));
		
		return (String) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * Efetua as pesquisas de compradores ativos por matricula ou nome, baseado em uma das duas formas de
	 * identificacao de servidores compradores: o código da ocupação ou uma característica do servidor
	 * 
	 * @param pesquisa matricula ou nome
	 * @return List<RapServidores>
	 */
	public List<RapServidores> pesquisarServidoresCompradorAtivoPorMatriculaNome(Object objPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
		LogicalExpression orDescricoes = null;
		
		// ind_situacao = 'A' ou 'P'
		SimpleExpression dominioA = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.A.toString());
		SimpleExpression dominioP = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.P.toString());
		
		// dt_fim_vinculo >= sysdate
		SimpleExpression dataFimVinculo = Restrictions.ge("VRAP." + VRapPessoaServidor.Fields.DT_FIM_VINCULO, Calendar.getInstance().getTime()); 
		
		criteria.add(Restrictions.or(dominioA, Restrictions.or(dominioP, dataFimVinculo)));
		
		String strParametro = null;
		if(objPesquisa != null) {
			strParametro = objPesquisa.toString();
		}
		
		// pesquisa por nome ou matricula
		if (StringUtils.isNotBlank(strParametro)){
			orDescricoes = Restrictions.or(Restrictions.ilike("VRAP." + VRapPessoaServidor.Fields.NOME.toString(), strParametro, MatchMode.ANYWHERE), 
					Restrictions.ilike("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			if(CoreUtil.isNumeroInteger(strParametro)){
				criteria.add(Restrictions.or(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), Integer.valueOf(strParametro)), orDescricoes));
			} else 	{
				criteria.add(orDescricoes);
			}
		}
	
		// verifica se eh comprador pela ocupacao
		DetachedCriteria subQueryOcupacao = DetachedCriteria.forClass(AghParametros.class);
		ProjectionList projectionListSubQueryOcup = Projections.projectionList()
		.add(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString()));
		subQueryOcupacao.setProjection(projectionListSubQueryOcup);
		subQueryOcupacao.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_OCUPACAO_COMPRADOR.toString()));		
		
		// verifica se eh comprador pela caracteristica no centro de custo
		DetachedCriteria subQueryCaract = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CUCC");
		subQueryCaract.createAlias("CUCC." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "CAR", Criteria.INNER_JOIN);	
		subQueryCaract.add(Restrictions.eq("CAR."+ScoCaracteristica.Fields.CARACTERISTICA.toString(), "COMPRADOR"));
		
		ProjectionList projectionListSubQueryCarac = Projections.projectionList()
				.add(Projections.property(ScoCaracteristicaUsuarioCentroCusto.Fields.MATRICULA_SERVIDOR.toString()));
		subQueryCaract.setProjection(projectionListSubQueryCarac);
		
		// monta o 'OR' com as subqueries
		criteria.add(Restrictions.or(Subqueries.propertyIn("VRAP." + VRapPessoaServidor.Fields.OCUPACAO.toString(), subQueryOcupacao),
				Subqueries.propertyIn("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), subQueryCaract)));
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString()), VRapPessoaServidor.Fields.VINDESCRICAO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		criteria.setProjection(p);
		
		// order by nome asc
		criteria.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.NOME.toString()));
		
		List<Object[]> resultado = executeCriteria(criteria, 0, 100, null, false);
		
		return converterParaRapServidores(resultado);	
	}
	
	
	/**
	 * Efetua as pesquisas de compradores  por matricula ou nome, baseado em uma das duas formas de
	 * identificacao de servidores compradores: o código da ocupação ou uma característica do servidor
	 * 
	 * @param pesquisa matricula ou nome
	 * @return List<RapServidores>
	 */
	public List<RapServidores> pesquisarServidoresCompradorPorMatriculaNome(Object objPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
		LogicalExpression orDescricoes = null;
		
		String strParametro = null;
		if(objPesquisa != null) {
			strParametro = objPesquisa.toString();
		}
		
		// pesquisa por nome ou matricula
		if (StringUtils.isNotBlank(strParametro)){
			orDescricoes = Restrictions.or(Restrictions.ilike("VRAP." + VRapPessoaServidor.Fields.NOME.toString(), strParametro, MatchMode.ANYWHERE), 
					Restrictions.ilike("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			if(CoreUtil.isNumeroInteger(strParametro)){
				criteria.add(Restrictions.or(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), Integer.valueOf(strParametro)), orDescricoes));
			} else 	{
				criteria.add(orDescricoes);
			}
		}
	
		// verifica se eh comprador pela ocupacao
		DetachedCriteria subQueryOcupacao = DetachedCriteria.forClass(AghParametros.class);
		ProjectionList projectionListSubQueryOcup = Projections.projectionList()
		.add(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString()));
		subQueryOcupacao.setProjection(projectionListSubQueryOcup);
		subQueryOcupacao.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_OCUPACAO_COMPRADOR.toString()));		
		
		// verifica se eh comprador pela caracteristica no centro de custo
		DetachedCriteria subQueryCaract = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CUCC");
		subQueryCaract.createAlias("CUCC." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "CAR", Criteria.INNER_JOIN);	
		subQueryCaract.add(Restrictions.eq("CAR."+ScoCaracteristica.Fields.CARACTERISTICA.toString(), "COMPRADOR"));
		
		ProjectionList projectionListSubQueryCarac = Projections.projectionList()
				.add(Projections.property(ScoCaracteristicaUsuarioCentroCusto.Fields.MATRICULA_SERVIDOR.toString()));
		subQueryCaract.setProjection(projectionListSubQueryCarac);
		
		// monta o 'OR' com as subqueries
		criteria.add(Restrictions.or(Subqueries.propertyIn("VRAP." + VRapPessoaServidor.Fields.OCUPACAO.toString(), subQueryOcupacao),
				Subqueries.propertyIn("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), subQueryCaract)));
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString()), VRapPessoaServidor.Fields.VINDESCRICAO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		criteria.setProjection(p);
		
		// order by nome asc
		criteria.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.NOME.toString()));
		
		List<Object[]> resultado = executeCriteria(criteria, 0, 100, null, false);
		
		return converterParaRapServidores(resultado);	
	}


	
	public List<RapServidores> pesquisarServidoresCompradorAtivo(Integer matricula, Short vinCodigo, Integer codigoOcupacao) {

		DetachedCriteria dc = criarCriteriaPesquisarServidoresCompradorAtivo(matricula, vinCodigo, codigoOcupacao);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property(VRapPessoaServidor.Fields.VINDESCRICAO.toString()), VRapPessoaServidor.Fields.VINDESCRICAO.toString());
		p.add(Projections.property(VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property(VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		dc.setProjection(p);
		
		// order by nome asc
		dc.addOrder(Order.asc(VRapPessoaServidor.Fields.NOME.toString()));

		List<Object[]> resultado = executeCriteria(dc);
		
		return converterParaRapServidores(resultado);
	}
	
	private DetachedCriteria criarCriteriaPesquisarServidoresCompradorAtivo(Integer matricula, Short vinCodigo, Integer codigoOcupacao) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());
		
		if (matricula != null) {
			dc.add(Restrictions.eq(VRapPessoaServidor.Fields.MATRICULA.toString(), matricula));
		}
		if (vinCodigo != null) {
			dc.add(Restrictions.eq(VRapPessoaServidor.Fields.VINCODIGO.toString(), vinCodigo));
		}
		if (codigoOcupacao != null) {
			dc.add(Restrictions.eq(VRapPessoaServidor.Fields.OCUPACAO.toString(), codigoOcupacao));
		}
		
		dc.add(Restrictions.or(
				Restrictions.eq(VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.A.toString()),
				Restrictions.and(
						Restrictions.ge(VRapPessoaServidor.Fields.DT_FIM_VINCULO.toString(), Calendar.getInstance().getTime()),
						Restrictions.eq(VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.P.toString()))));

		return dc;
	}

	/**
	 * ORADB: cursor: AELC_BUSCA_MED_SOLIC.c_lul
	 * @param luxSeq
	 * @return
	 */
	public String obterNomePessoaServidorPorAelExameApItemSolics(final Long luxSeq){
		final StringBuffer hql =  new StringBuffer(370);
		
    	hql.append(" select ") 
    	   .append("     distinct(vps.id.nome) ")
    	   .append(" from ")
		   .append("     VRapPessoaServidor   vps, ")
		   .append("     AelSolicitacaoExames soe, ")
		   .append("     AelExameApItemSolic  lul ")
		   
		   .append(" where 1=1 ")
		   .append("     and lul.id.luxSeq = :prmSeq ")
		   .append("     and lul.id.iseSoeSeq = soe.seq ")
		   .append("     and soe.servidorResponsabilidade.id.matricula =  vps.id.serMatricula ")
		   .append("     and soe.servidorResponsabilidade.id.vinCodigo =  vps.id.serVinCodigo ");
    	
		final Query query = createHibernateQuery(hql.toString());
		
		query.setLong("prmSeq", luxSeq);
		
		return (String) query.uniqueResult();
	}
	
	public String obterNomePessoaServidor(Short codVinculo, Integer matricula){
		return obterNomePessoaServidor(Integer.parseInt(codVinculo.toString()), matricula);
	}
	
	/**
	 * Verifica se servidor é um comprador ativo por vinculo e matricula, baseado em uma das duas formas de
	 * identificacao de servidores compradores: o código da ocupação ou uma característica do servidor
	 * 
	 * @param pesquisa matricula ou nome
	 * @return List<RapServidores>
	 */
	public boolean isServidorCompradorAtivoPorVinculoMatricula(Short vinculo, Integer matricula) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
			
		// ind_situacao = 'A' ou 'P'
		SimpleExpression dominioA = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.A.toString());
		SimpleExpression dominioP = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SITUACAO.toString(), DominioSituacaoVinculo.P.toString());
		
		// dt_fim_vinculo >= sysdate
		SimpleExpression dataFimVinculo = Restrictions.ge("VRAP." + VRapPessoaServidor.Fields.DT_FIM_VINCULO, Calendar.getInstance().getTime()); 
		
		criteria.add(Restrictions.or(dominioA, Restrictions.or(dominioP, dataFimVinculo)));
		
		criteria.add(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString(), vinculo));
		criteria.add(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), matricula));
		
		
		// verifica se eh comprador pela ocupacao
		DetachedCriteria subQueryOcupacao = DetachedCriteria.forClass(AghParametros.class);
		ProjectionList projectionListSubQueryOcup = Projections.projectionList()
		.add(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString()));
		subQueryOcupacao.setProjection(projectionListSubQueryOcup);
		subQueryOcupacao.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_OCUPACAO_COMPRADOR.toString()));		
		
		// verifica se eh comprador pela caracteristica no centro de custo
		DetachedCriteria subQueryCaract = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CUCC");
		subQueryCaract.createAlias("CUCC." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "CAR", Criteria.INNER_JOIN);	
		subQueryCaract.add(Restrictions.eq("CAR."+ScoCaracteristica.Fields.CARACTERISTICA.toString(), "COMPRADOR"));
		
		ProjectionList projectionListSubQueryCarac = Projections.projectionList()
				.add(Projections.property(ScoCaracteristicaUsuarioCentroCusto.Fields.MATRICULA_SERVIDOR.toString()));
		subQueryCaract.setProjection(projectionListSubQueryCarac);
		
		// monta o 'OR' com as subqueries
		criteria.add(Restrictions.or(Subqueries.propertyIn("VRAP." + VRapPessoaServidor.Fields.OCUPACAO.toString(), subQueryOcupacao),
				Subqueries.propertyIn("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), subQueryCaract)));
		
		Long resultado = executeCriteriaCount(criteria);
				
		return (resultado > 0 ? true : false);
	}
	
	public List<RapServidores> pesquisarResidente(Object parametro, Short vincFunc, Integer matricula) {
		DetachedCriteria criteria = obterCriteriaResidente(parametro, vincFunc, matricula);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.VINDESCRICAO.toString()), VRapPessoaServidor.Fields.VINDESCRICAO.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property("VRAP." + VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		criteria.setProjection(p);
		
		Map<String, Boolean> mapOrdenacao = new HashMap<String, Boolean>();
		
		criteria.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString()));
		criteria.addOrder(Order.asc("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString()));
		
		List<Object[]> resultado = executeCriteria(criteria, 0, 100, mapOrdenacao);
		
		return converterParaRapServidores(resultado);
	}	
	
	public Long pesquisarResidenteCount(Object parametro, Short vincFunc, Integer matricula) {
		DetachedCriteria criteria = obterCriteriaResidente(parametro, vincFunc, matricula);
		
		return executeCriteriaCount(criteria);		
	}	
	
	private DetachedCriteria obterCriteriaResidente(Object parametro, Short vincFunc, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
		
		criteria.add(Restrictions.and(
				Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString(), vincFunc), 
				Restrictions.gt("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), matricula)));
		
		if (CoreUtil.isNumeroInteger(parametro)) {
			criteria.add(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), Integer.valueOf((String) parametro)));
		} else if (StringUtils.isNotEmpty((String) parametro)) {
			criteria.add(Restrictions.ilike("VRAP." + VRapPessoaServidor.Fields.NOME.toString(), parametro.toString(), MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	
	public String obterNomeEquipeProfissionalMonitorCirurgia(MbcProfCirurgias profCirurgias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
		criteria.setProjection(Projections.property("VRAP." + VRapPessoaServidor.Fields.NOME.toString()));
		criteria.add(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SER_MATRICULA.toString(), profCirurgias.getId().getPucSerMatricula()));
		criteria.add(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString(), profCirurgias.getId().getPucSerVinCodigo()));
		DetachedCriteria subCriteriaProfAtuaUnidCirgs = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "PUC");
		subCriteriaProfAtuaUnidCirgs.setProjection(Projections.property("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()));
		subCriteriaProfAtuaUnidCirgs.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), profCirurgias.getId().getPucSerMatricula()));
		subCriteriaProfAtuaUnidCirgs.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), profCirurgias.getId().getPucSerVinCodigo()));
		subCriteriaProfAtuaUnidCirgs.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), profCirurgias.getId().getPucUnfSeq()));
		subCriteriaProfAtuaUnidCirgs.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), profCirurgias.getId().getPucIndFuncaoProf()));
		criteria.add(Subqueries.exists(subCriteriaProfAtuaUnidCirgs));
		return (String) executeCriteriaUniqueResult(criteria);
	}

	
	/**
	 * Verifica se servidor está no mesmo centro de custo 
	 * 
	 * 
	 * @param pesquisa matricula e centro de custo aplicada 
	 * @return true or false
	 */
	public boolean isServidorVinculadoCentroCusto(Short vinculo, Integer matricula, Integer cctCodAplicada) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRAP");
			
		// ind_situacao = 'A' ou 'P'
		SimpleExpression cctCodigo = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.CCT_CODIGO.toString(), cctCodAplicada);
		SimpleExpression cctCodigoAtua = Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.CCT_CODIGO_ATUA.toString(), cctCodAplicada);
		
		criteria.add(Restrictions.or(cctCodigo, cctCodigoAtua));
		
		criteria.add(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.VINCODIGO.toString(), vinculo));
		criteria.add(Restrictions.eq("VRAP." + VRapPessoaServidor.Fields.MATRICULA.toString(), matricula));
		
		Long resultado = executeCriteriaCount(criteria);
		
				
		return (resultado > 0 ? true : false);
}
	
	public String obterChefeComprasPorMatriculaVinculo(Integer matricula, Short vinCodigo) {

		DetachedCriteria dc = criarCriteriaPesquisarServidoresMatriculaVinculo(matricula, vinCodigo);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		dc.setProjection(p);
		
		// order by nome asc
		dc.addOrder(Order.asc(VRapPessoaServidor.Fields.NOME.toString()));

		String resultado = (String) executeCriteriaUniqueResult(dc);
		
		return resultado;
	}
	
	private DetachedCriteria criarCriteriaPesquisarServidoresMatriculaVinculo(Integer matricula, Short vinCodigo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());
		
		if (matricula != null) {
			dc.add(Restrictions.eq(VRapPessoaServidor.Fields.MATRICULA.toString(), matricula));
		}
		if (vinCodigo != null) {
			dc.add(Restrictions.eq(VRapPessoaServidor.Fields.VINCODIGO.toString(), vinCodigo));
		}
		
		return dc;
	}

	/**
	 * #5799
	 * C5 - Consulta para retornar o responsável da verificação da informação (destinatário)
	 */
	public RapPessoalServidorVO obterValoresPrescricaoMedica(Integer matriculaServidorVerificado, Short vinCodigoServidorVerificado){
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class, "VPS");
		criteria.add(Restrictions.eq("VPS."+VRapPessoaServidor.Fields.SER_MATRICULA.toString(), matriculaServidorVerificado));
	    criteria.add(Restrictions.eq("VPS."+VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString(), vinCodigoServidorVerificado));
	    criteria.setProjection(Projections.projectionList().add(
	    		Projections.property("VPS." + VRapPessoaServidor.Fields.MATRICULA.toString()), RapPessoalServidorVO.Fields.SER_MATRICULA.toString())
	    		.add(Projections.property("VPS." + VRapPessoaServidor.Fields.VINCODIGO.toString()), RapPessoalServidorVO.Fields.SER_VIN_CODIGO.toString())
	    		.add(Projections.property("VPS." + VRapPessoaServidor.Fields.NOME.toString()), RapPessoalServidorVO.Fields.NOME.toString()));
	    
	    criteria.setResultTransformer(Transformers.aliasToBean(RapPessoalServidorVO.class));
	    return (RapPessoalServidorVO) executeCriteriaUniqueResult(criteria);
	    
	} 
	
	public String obterNomeUsual(Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class);
		criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.VINCODIGO.toString(), vinCodigo));
		criteria.setProjection(Projections.property(VRapPessoaServidor.Fields.NOME_USUAL.toString()));
		criteria.setProjection(Projections.property(VRapPessoaServidor.Fields.NOME.toString()));

		return (String) executeCriteriaUniqueResult(criteria);
	}

	public List<VRapPessoaServidorVO> pesquisarPessoasServidores(String pesquisa) throws ApplicationBusinessException{
		DetachedCriteria criteria = filtroPesquisarPessoasServidores(pesquisa);
		criteria.addOrder(Order.asc(VRapPessoaServidor.Fields.NOME.toString()));
		List<VRapPessoaServidorVO> listaPessoaServidor = new ArrayList<VRapPessoaServidorVO>();
		listaPessoaServidor = executeCriteria(criteria, 0, 100, null, false);
		return listaPessoaServidor;
	}
	/**
	 * #6807
	 * C4
	 * @param pesquisa
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarPessoasServidoresCount(String pesquisa) throws ApplicationBusinessException{
		DetachedCriteria criteria = filtroPesquisarPessoasServidores(pesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria filtroPesquisarPessoasServidores(String pesquisa) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(VRapPessoaServidor.Fields.VINCODIGO.toString()),VRapPessoaServidorVO.Fields.VINCULO.toString())
				.add(Projections.property(VRapPessoaServidor.Fields.MATRICULA.toString()),VRapPessoaServidorVO.Fields.MATRICULA.toString())
				.add(Projections.property(VRapPessoaServidor.Fields.NOME.toString()),VRapPessoaServidorVO.Fields.NOME.toString())
				.add(Projections.property(VRapPessoaServidor.Fields.PES_CODIGO.toString()),VRapPessoaServidorVO.Fields.PES_CODIGO.toString()));
		
		if(StringUtils.isNotBlank(pesquisa)){
			if(CoreUtil.isNumeroInteger(pesquisa)){
				criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.MATRICULA.toString(),Integer.valueOf(pesquisa)));
			}
			else{
				criteria.add(Restrictions.ilike(VRapPessoaServidor.Fields.NOME.toString(),pesquisa,MatchMode.ANYWHERE));
			}
		}

		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(
					VRapPessoaServidorVO.class.getConstructor(Short.class, Integer.class, String.class, Integer.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}

		return criteria;
	}
	
	public Object[] obterVRapPessoaServidorPorVinCodigoMatricula(Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = criarCriteriaPesquisarServidoresMatriculaVinculo(matricula, vinCodigo);
		criarProjectionVRapPessoaServidor(criteria);
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	
	public List<Object[]> pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(List<Short> vinCodigos, List<Integer> cctCodigos,  Integer matriculaVinCodigo, String nome) {
		DetachedCriteria criteria = criarCriteriaVRapPessoaServidorPorVinCodigoMatriculaCCTNome(
				vinCodigos, cctCodigos, matriculaVinCodigo, nome);
		criarProjectionVRapPessoaServidor(criteria);

		criteria.add(Restrictions.isNull(VRapPessoaServidor.Fields.DT_FIM_VINCULO.toString()));
		
		criteria.addOrder(Order.asc(VRapPessoaServidor.Fields.NOME.toString()));
		criteria.addOrder(Order.asc(VRapPessoaServidor.Fields.NOME_USUAL .toString()));
		
		List<Object[]> resultado = executeCriteria(criteria, 0, 100, null, false);
		return resultado;
	}

	private void criarProjectionVRapPessoaServidor(DetachedCriteria criteria) {
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(VRapPessoaServidor.Fields.VINCODIGO.toString()), VRapPessoaServidor.Fields.VINCODIGO.toString());
		p.add(Projections.property(VRapPessoaServidor.Fields.MATRICULA.toString()), VRapPessoaServidor.Fields.MATRICULA.toString());
		p.add(Projections.property(VRapPessoaServidor.Fields.NOME.toString()), VRapPessoaServidor.Fields.NOME.toString());
		p.add(Projections.property(VRapPessoaServidor.Fields.NOME_USUAL.toString()), VRapPessoaServidor.Fields.NOME_USUAL.toString());
		p.add(Projections.property(VRapPessoaServidor.Fields.SITUACAO.toString()), VRapPessoaServidor.Fields.SITUACAO.toString());
		criteria.setProjection(p);
	}

	private DetachedCriteria criarCriteriaVRapPessoaServidorPorVinCodigoMatriculaCCTNome(
			List<Short> vinCodigos, List<Integer> cctCodigos,
			Integer matriculaVinCodigo, String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapPessoaServidor.class);
		
		if (vinCodigos != null && !vinCodigos.isEmpty()) {
			criteria.add(Restrictions.in(VRapPessoaServidor.Fields.VINCODIGO.toString(), vinCodigos));
		}
		if (cctCodigos != null && !cctCodigos.isEmpty()) {
			criteria.add(Restrictions.in(VRapPessoaServidor.Fields.CCT_CODIGO.toString(), cctCodigos));
		}
		if (StringUtils.isNotBlank(nome)) {
			Criterion c1 = Restrictions.ilike(VRapPessoaServidor.Fields.NOME.toString(), nome, MatchMode.ANYWHERE);
			Criterion c2 = Restrictions.ilike(VRapPessoaServidor.Fields.NOME_USUAL.toString(), nome, MatchMode.ANYWHERE);			
			criteria.add(Restrictions.or(c1, c2));
		}
		if (matriculaVinCodigo != null) {
			if(CoreUtil.isNumeroShort(matriculaVinCodigo)) {
				Criterion c1 = Restrictions.eq(VRapPessoaServidor.Fields.MATRICULA.toString(), matriculaVinCodigo);
				Criterion c2 = Restrictions.eq(VRapPessoaServidor.Fields.VINCODIGO.toString(), matriculaVinCodigo.shortValue());
				criteria.add(Restrictions.or(c1, c2));	
			} else {
				criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.MATRICULA.toString(), matriculaVinCodigo));
			}
		}
		return criteria;
	}

	public Long pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(
			List<Short> vinCodigos, List<Integer> cctCodigos,
			Integer matriculaVinCodigo, String nome) {
		DetachedCriteria criteria = criarCriteriaVRapPessoaServidorPorVinCodigoMatriculaCCTNome(
				vinCodigos, cctCodigos, matriculaVinCodigo, nome);
		criteria.add(Restrictions.isNull(VRapPessoaServidor.Fields.DT_FIM_VINCULO.toString()));
		return executeCriteriaCount(criteria);
	}
	
}