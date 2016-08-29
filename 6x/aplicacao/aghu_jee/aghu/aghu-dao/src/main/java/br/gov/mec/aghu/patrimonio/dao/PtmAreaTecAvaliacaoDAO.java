package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.patrimonio.vo.ResponsavelAreaTecAceiteTecnicoPendenteVO;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmAreaTecAvaliacaoDAO extends BaseDao<PtmAreaTecAvaliacao> {

	private static final String FCC = "FCC";

	private static final String ALIAS_SCC = "SCC";

	private static final String ALIAS_FCC = "FCC.";

	private static final String ALIAS_SER = "SER.";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1981095563703668348L;

	private static final String ALIAS_ATA = "ATA";
	private static final String ALIAS_ATA_EXT = "ATA."; 
	private static final String SER_PONTO = "SER.";
	private static final String ALIAS_SER_S_PONTO = "SER";
	private static final String ALIAS_PF_EXT = "PF.";
	
	/**
	 * Obtém count de {@link PtmAreaTecAvaliacao} para Suggestion Box.
	 * #43464
	 * @param parametro {@link String}
	 * @return {@link Long}
	 */
	public Long obterCountAreaTecAvaliacaoParaSuggestionBox(String parametro) {
		return executeCriteriaCount(obterCriteriaAreaTecAvaliacaoPorCodigoOuNome(parametro));
	}
	
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacao(Object servidor) {

		DetachedCriteria criteria = obterCriteriaAreaTecnicaAvaliacao(servidor);
		
		criteria.addOrder(Order.asc(PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * Obtém lista de {@link PtmAreaTecAvaliacao} para Suggestion Box.
	 * #43464
	 * @param parametro {@link String}
	 * @return {@link List} de {@link PtmAreaTecAvaliacao}
	 */
	public List<PtmAreaTecAvaliacao> obterListaAreaTecAvaliacaoParaSuggestionBox(String parametro) {
		return executeCriteria(obterCriteriaAreaTecAvaliacaoPorCodigoOuNome(parametro), 0, 100, PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), true);
	}
	
	/**
	 * Obtém criteria para consulta de {@link PtmAreaTecAvaliacao} para Suggestion Box.
	 * #43464
	 * @param parametro {@link String}
	 * @return {@link DetachedCriteria}
	 */
	private DetachedCriteria obterCriteriaAreaTecAvaliacaoPorCodigoOuNome(String parametro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class);

		if (StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SEQ.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike(PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	/**
	 * Obtém lista de {@link PtmAreaTecAvaliacao} por Código de Centro Custo, Código da Área ou Nome da Área.
	 * 
	 * @param parametro {@link String}
	 * @return {@link List} de {@link PtmAreaTecAvaliacao}
	 */
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecAvaliacaoPorCodigoNomeOuCC(String parametro) {
		return executeCriteria(obterCriteriaAreaTecAvaliacaoPorCodigoNomeOuCC(parametro), 0, 100,
				PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), true);
	}

	/**
	 * Obtém count de {@link PtmAreaTecAvaliacao} para a consulta por Código de Centro Custo, Código da Área ou Nome da Área.
	 *
	 * @param parametro {@link String}
	 * @return {@link Long}
	 */
	public Long pesquisarAreaTecAvaliacaoPorCodigoNomeOuCCCount(String parametro) {
		return executeCriteriaCount(obterCriteriaAreaTecAvaliacaoPorCodigoNomeOuCC(parametro));
	}
	
	/**
	 * Obtém criteria para consulta de {@link PtmAreaTecAvaliacao} por Código de Centro Custo, Código da Área ou Nome da Área.
	 * 
	 * @param parametro {@link String}
	 * @return {@link DetachedCriteria}
	 */
	private DetachedCriteria obterCriteriaAreaTecAvaliacaoPorCodigoNomeOuCC(String parametro) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);

		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), "SER1");
		criteria.createAlias("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");

		criteria.setFetchMode(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), FetchMode.JOIN);
		criteria.setFetchMode("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);

		if (StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), FCC);

				criteria.add(Restrictions.or(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE),
						Restrictions.eq(ALIAS_FCC + FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(parametro))));
			} else {
				criteria.add(Restrictions.ilike(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	public PtmAreaTecAvaliacao obterPorServidor(RapServidores servidor) {
		
		List<PtmAreaTecAvaliacao> lista = obterCriteriaPorServidor(servidor);
		
		if (lista != null && !lista.isEmpty()) {
			
			return lista.get(0);
		}
		
		return null;
	}
	
	public PtmAreaTecAvaliacao obterAreaTecPorServidor(RapServidores servidor) {
		List<PtmAreaTecAvaliacao> lista = obterCriteriaAreaTecPorServidor(servidor);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	private List<PtmAreaTecAvaliacao> obterCriteriaPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = getCriteriaAreaTecAtivaPorResponsavel(servidor);
		
		List<PtmAreaTecAvaliacao> lista = executeCriteria(criteria);
		return lista;
	}
	
	private List<PtmAreaTecAvaliacao> obterCriteriaAreaTecPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = getCriteriaAreaTecPorServidor(servidor);
		List<PtmAreaTecAvaliacao> lista = executeCriteria(criteria);
		return lista;
	}

	private DetachedCriteria getCriteriaAreaTecAtivaPorResponsavel(
			RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);		
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), servidor));
		return criteria;
	}
	
	private DetachedCriteria getCriteriaAreaTecPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT+ PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), FCC , JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), servidor));
		return criteria;
	}
	
	public List<PtmAreaTecAvaliacao> pesquisarCriteriaPorServidor(RapServidores servidor){
		List<PtmAreaTecAvaliacao> lista = obterCriteriaPorServidor(servidor);
		
		return lista;
	}
	
	/**
	 * Busca Área Técnica de Avaliação que estão ativas por servidor responsável
	 * 
	 * @param codigo - Código do Centro de Custo
	 * @return Lista de Área técnica de Avaliação
	 */
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecAtivaPorServidor(RapServidores servidor){
		DetachedCriteria criteria = getCriteriaAreaTecAtivaPorResponsavel(servidor);
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<PtmAreaTecAvaliacao> lista = executeCriteria(criteria);
		
		return lista;
	}

	/**
	 * Busca Área Técnica de Avaliação por código do Centro de Custo.
	 * 
	 * Usada na C1 - #44276
	 * @param codigo - Código do Centro de Custo
	 * @return Lista de Área técnica de Avaliação
	 */
	public List<PtmAreaTecAvaliacao> listarPorCodigoCentroCusto(Integer codigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CEC");
		
		criteria.add(Restrictions.eq("CEC." + FccCentroCustos.Fields.CODIGO.toString(), codigo));
		
		return executeCriteria(criteria);
	}
	
	public List<PtmAreaTecAvaliacao> listarPorCodigoCentroCusto(List<Integer> listaCodigoCC) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), FCC);
		
		criteria.add(Restrictions.in(ALIAS_FCC + FccCentroCustos.Fields.CODIGO.toString(), listaCodigoCC));
		
		return executeCriteria(criteria);
	}
	
	public Long pesquisarAreaTecnicaAvaliacaoCount(Object servidor){
		
		DetachedCriteria criteria = obterCriteriaAreaTecnicaAvaliacao(servidor);
		
		return executeCriteriaCount(criteria);
	}

	/**
	 * Método para obter a consulta do SuggestionBox para Área Técnica de Avaliação com permissão PM02.
	 * @param servidor
	 * @return
	 */
	private DetachedCriteria obterCriteriaAreaTecnicaAvaliacao(Object servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class);

		Disjunction or = Restrictions.disjunction();

		String strPesquisa = (String) servidor;
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			or.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SEQ.toString(),Integer.valueOf(strPesquisa)));
		} 
		or.add(Restrictions.ilike(PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(),strPesquisa, MatchMode.ANYWHERE));
		criteria.add(or);
		
		return criteria;
	}	
	
	private DetachedCriteria obterCriteriaOficinasAreaTecnicaAvaliacao(String nomeAreaTec, FccCentroCustos centroCusto, RapServidoresId responsavel, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), "S");
		criteria.createAlias("S." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF");
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CC");
		
		if(nomeAreaTec !=null && !nomeAreaTec.isEmpty()){
			criteria.add(Restrictions.ilike(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), nomeAreaTec, MatchMode.ANYWHERE));
		}
		if(centroCusto !=null ){
			criteria.add(Restrictions.eq("CC." + FccCentroCustos.Fields.CODIGO.toString(), centroCusto.getCodigo()));
		}
		if(responsavel !=null ){
			criteria.add(Restrictions.eq("S." + RapServidores.Fields.ID.toString(), responsavel));
		}
		if(situacao!=null){
			criteria.add(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	public List<PtmAreaTecAvaliacao> pesquisarOficinasAreaTecnicaAvaliacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String nomeAreaTec, FccCentroCustos centroCusto, RapServidoresId responsavel, DominioSituacao situacao){
		DetachedCriteria criteria = obterCriteriaOficinasAreaTecnicaAvaliacao(nomeAreaTec,centroCusto, responsavel, situacao);
		if(orderProperty == null || orderProperty.isEmpty()){
			criteria.addOrder(Order.asc(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()));
		}
		if(orderProperty != null && !orderProperty.isEmpty() && orderProperty.equals("servidorCC.pessoaFisica.nome")){
			if(asc){
				criteria.addOrder(Order.asc(ALIAS_PF_EXT + RapPessoasFisicas.Fields.NOME.toString()));
			}else{
				criteria.addOrder(Order.desc(ALIAS_PF_EXT + RapPessoasFisicas.Fields.NOME.toString()));
			}
			orderProperty = null;
		}
		
		return executeCriteria(criteria, firstResult, maxResult.intValue(), orderProperty, asc);
	}
	
	public Long pesquisarOficinasAreaTecnicaAvaliacaoCount(String nomeAreaTec, FccCentroCustos centroCusto, RapServidoresId responsavel, DominioSituacao situacao){
		DetachedCriteria criteria = obterCriteriaOficinasAreaTecnicaAvaliacao(nomeAreaTec,centroCusto, responsavel, situacao);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Consulta do SuggestionBox para Área Técnica de Avaliação com permissão PM01.
	 * @param objPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(Object objPesquisa, RapServidores servidorLogado){
		DetachedCriteria criteria = obterAreaTecnicaAvaliacaoComCentroCusto(objPesquisa ,servidorLogado);
				
		criteria.addOrder(Order.asc(PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * Consulta do Count do SuggestionBox para Área Técnica de Avaliação com permissão PM01.
	 * @param objPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(Object objPesquisa, RapServidores servidorLogado){
		DetachedCriteria criteria = obterAreaTecnicaAvaliacaoComCentroCusto(objPesquisa, servidorLogado);
		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Consulta do SuggestionBox para Área Técnica de Avaliação com permissão PM01. - Melhoria 49193
	 * @param objPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(Object objPesquisa, List<Integer> listaSeqAreaTecnica){
		DetachedCriteria criteria = obterAreaTecnicaAvaliacaoComCentroCusto(objPesquisa ,listaSeqAreaTecnica);
				
		criteria.addOrder(Order.asc(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * Consulta do Count do SuggestionBox para Área Técnica de Avaliação com permissão PM01.
	 * @param objPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(Object objPesquisa, List<Integer> listaSeqAreaTecnica){
		DetachedCriteria criteria = obterAreaTecnicaAvaliacaoComCentroCusto(objPesquisa, listaSeqAreaTecnica);
		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método para obter a consulta do SuggestionBox para Área Técnica de Avaliação com permissão PM01.
	 * @param objPesquisa
	 * @param servidorLogado
	 * @return
	 */
	private DetachedCriteria obterAreaTecnicaAvaliacaoComCentroCusto(
			Object objPesquisa, RapServidores servidorLogado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, "AVA");
		criteria.createAlias("AVA." + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), FCC, JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(ALIAS_FCC + FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(RapServidores.class, "RAP");
		subQuery.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), servidorLogado.getId().getMatricula()));
		subQuery.add(Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), servidorLogado.getId().getVinCodigo()));
		subQuery.setProjection(Projections.property(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString()));
		
		criteria.add(Subqueries.propertyEq(ALIAS_FCC + FccCentroCustos.Fields.CENTRO_CUSTO.toString(), subQuery));
		
		Disjunction or = Restrictions.disjunction();
		String strPesquisa = (String) objPesquisa;
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			or.add(Restrictions.eq(
					"AVA." + PtmAreaTecAvaliacao.Fields.SEQ.toString(),
					Integer.valueOf(strPesquisa)));
		}		
		or.add(Restrictions.ilike("AVA." + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		
		criteria.add(or);
		return criteria;
	}
	
	/**
	 * Método para obter a consulta do SuggestionBox para Área Técnica de Avaliação com permissão PM01.
	 * @param objPesquisa
	 * @param servidorLogado
	 * @return
	 */
	private DetachedCriteria obterAreaTecnicaAvaliacaoComCentroCusto(Object objPesquisa, List<Integer> listaSeqAreaTecnica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), FCC, JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(ALIAS_FCC + FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		if(listaSeqAreaTecnica!=null && !listaSeqAreaTecnica.isEmpty()){
			criteria.add(Restrictions.in(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString(), listaSeqAreaTecnica));
		}
		
		Disjunction or = Restrictions.disjunction();
		String strPesquisa = (String) objPesquisa;
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			or.add(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString(),Integer.valueOf(strPesquisa)));
		}		
		or.add(Restrictions.ilike(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		
		criteria.add(or);
		return criteria;
	}
	
	/**
	 * Método para obter a consulta do SuggestionBox para Área Técnica de Avaliação com permissão PM01.
	 * @param objPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public List<Integer> listarCodCentroCustoAbaixo(List<Integer> listCentroCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), FCC, JoinType.INNER_JOIN);
		criteria.add(Restrictions.in(ALIAS_FCC + FccCentroCustos.Fields.CC_SUPERIOR.toString(), listCentroCodigo));
		
		criteria.setProjection(Projections.property(ALIAS_FCC+FccCentroCustos.Fields.CODIGO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta do SuggestionBox para Responsável Técnico com permissão PM02.
	 * @param objPesquisa
	 * @return
	 */
	public List<RapServidores> pesquisarResponsavelTecnico(Object objPesquisa) {
		DetachedCriteria criteria = obterResponsavelTecnico(objPesquisa);

		criteria.addOrder(Order.asc("PSF."
				+ RapPessoasFisicas.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * Consulta do Count do SuggestionBox para Responsável Técnico com permissão PM02.
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarResponsavelTecnicoCount(Object objPesquisa){
		DetachedCriteria criteria = obterResponsavelTecnico(objPesquisa);
		List<RapServidores> list = executeCriteria(criteria);
		return (Long.parseLong( String.valueOf(list.size()) ));
	}
	
	/**
	 * Método para obter a consulta do SuggestionBox para Responsável Técnico com permissão PM02.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria obterResponsavelTecnico(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				RapServidores.class, "RAP");
		criteria.createAlias(
				RapServidores.Fields.PESSOA_FISICA.toString(), "PSF",
				JoinType.INNER_JOIN);
		criteria.createAlias("RAP."
				+ RapServidores.Fields.AREAS_TECNICAS_AVALIACAO_CC.toString(),
				ALIAS_ATA, JoinType.INNER_JOIN);
		
		Disjunction or = Restrictions.disjunction();
		String strPesquisa = (String) objPesquisa;
		
		if(CoreUtil.isNumeroShort(strPesquisa)){
			or.add(Restrictions.eq("RAP."
					+ RapServidores.Fields.VIN_CODIGO.toString(),
					Short.valueOf(strPesquisa)));
		}else if (CoreUtil.isNumeroInteger(strPesquisa)) {
			or.add(Restrictions.eq("RAP."
					+ RapServidores.Fields.MATRICULA.toString(),
					Integer.valueOf(strPesquisa)));
		} 
		or.add(Restrictions.ilike("PSF."
					+ RapPessoasFisicas.Fields.NOME.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		criteria.add(or);
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return criteria;
	}

	public PtmAreaTecAvaliacao obterAreaComServidorPorChavePrimaria(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR.toString(), ALIAS_SER_S_PONTO, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), ALIAS_SCC, JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SEQ.toString(), seq));
		List<PtmAreaTecAvaliacao> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	public boolean isResponsavelAreaTecnico(RapServidores servidorLogado, Integer seqAreaTecnica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SEQ.toString(), seqAreaTecnica));
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), servidorLogado));
		
			return executeCriteriaExists(criteria);
	}
	
	public PtmAreaTecAvaliacao obterAreaTecAvaliacaoComPessoaFisica(PtmAreaTecAvaliacao ptmAreaTecAvaliacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);

		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), ALIAS_SER_S_PONTO);
		criteria.createAlias(SER_PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");

		criteria.setFetchMode(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SER_PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SEQ.toString(), ptmAreaTecAvaliacao.getSeq()));
		
		return (PtmAreaTecAvaliacao) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean verificarChefeParaAreaTecnica(Integer seq, RapServidores servidorCC){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class);
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), servidorCC));
		
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * Consultar qual área técnica de avaliação a chefia anterior pertencia.
	 * C1 - #44276
	 */
	public PtmAreaTecAvaliacao obterAreaTecnicaPorCodigo(Integer codigoCentroCustos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		
		criteria.add(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString(), codigoCentroCustos));
		
		return (PtmAreaTecAvaliacao)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44276
	 * C2
	 * @param servidorLogado
	 * @return
	 */
	
	public List<Integer> seqPorChefiaSituacao(RapServidores servidorLogado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		
		criteria.setProjection(Projections.projectionList().add(Projections.property(ALIAS_ATA + "." + PtmAreaTecAvaliacao.Fields.SEQ.toString())));
		
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), servidorLogado));
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<PtmAreaTecAvaliacao> pesquisarListaAreaTecnicaSuggestionBox(String parametro, RapServidores servidor){

		DetachedCriteria criteria = obterListaAreaTecnicaSuggestionBox(
				parametro, servidor);
		
		return executeCriteria(criteria, 0, 100, PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), true);
	}
	
	public Long pesquisarListaAreaTecnicaSuggestionBoxCount(String parametro, RapServidores servidor){
		DetachedCriteria criteria = obterListaAreaTecnicaSuggestionBox(
				parametro, servidor);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterListaAreaTecnicaSuggestionBox(
			String parametro, RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class);
		if (StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SEQ.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike(PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), servidor));
		return criteria;
	}
	
	public List<PtmAreaTecAvaliacao> pesquisarListaAreaTecnicaPorResponsavel(String parametro, RapServidores servidor){
		
		return executeCriteria(obterListaAreaTecnicaPorResponsavel(parametro, servidor), 
				0, 100, PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), true);
	}
	
	public Long pesquisarListaAreaTecnicaPorResponsavelCount(String parametro, RapServidores servidor){
		 
		return executeCriteriaCount(obterListaAreaTecnicaPorResponsavel(parametro, servidor));
	}

	private DetachedCriteria obterListaAreaTecnicaPorResponsavel(
			String parametro, RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.PTM_SERV_AREA_TEC_AVALIACAO.toString(), "SATA", JoinType.INNER_JOIN);
		if (StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq("SATA." + PtmServAreaTecAvaliacao.Fields.SERVIDOR_TECNICO.toString(), servidor));
		return criteria;
	}
	
	public Long pesquisarQtdeItemRecebAssociadoAreaTecnica(Integer seqAreaTec){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.ITENS_RECEB_PROVISORIOS.toString(), "PIRP", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.in("PIRP." + PtmItemRecebProvisorios.Fields.STATUS.toString(), new Integer[]{1,2,3,4,5,9}) );
		
		criteria.add(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString(), seqAreaTec));

		criteria.setProjection(Projections.projectionList().add(Projections.property(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString())));
		
		return executeCriteriaCount(criteria);
	}
	
	//#48782 - C22
	public RapServidoresVO verificarUsuarioLogadoResponsavelPorAreaTec(Integer itemRecebimento, Integer recebimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.ITENS_RECEB_PROVISORIOS.toString(), "PIRP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString(), ALIAS_SER_S_PONTO ,JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(SER_PONTO+ RapServidores.Fields.MATRICULA.toString()).as(RapServidoresVO.Fields.MATRICULA.toString()))
				.add(Projections.property(SER_PONTO+ RapServidores.Fields.VIN_CODIGO.toString()).as(RapServidoresVO.Fields.VINCULO.toString()))
				);

		criteria.add(Restrictions.eq("PIRP." + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		criteria.add(Restrictions.eq("PIRP." + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		return (RapServidoresVO) executeCriteriaUniqueResult(criteria, false);
	}
	
	/**
	 * #48321 C3
	 * @param seqAreaTecnica
	 * @return
	 */
	public ResponsavelAreaTecAceiteTecnicoPendenteVO obterResponsavelAreaTecnica(Integer seqAreaTecnica){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);
		criteria.createAlias(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(),FCC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_FCC + FccCentroCustos.Fields.SERVIDOR.toString(),ALIAS_SER_S_PONTO, JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString()),ResponsavelAreaTecAceiteTecnicoPendenteVO.Fields.SEQ_AREA_TEC.toString())
				.add(Projections.property(ALIAS_FCC + FccCentroCustos.Fields.CODIGO.toString()),ResponsavelAreaTecAceiteTecnicoPendenteVO.Fields.COD_CENTRO_CUSTO.toString())
				.add(Projections.property(ALIAS_SER + RapServidores.Fields.MATRICULA.toString()),ResponsavelAreaTecAceiteTecnicoPendenteVO.Fields.MATRICULA_RESPONSAVEL.toString())
				.add(Projections.property(ALIAS_SER + RapServidores.Fields.VIN_CODIGO.toString()),ResponsavelAreaTecAceiteTecnicoPendenteVO.Fields.VINCULO_RESPONSAVEL.toString())
				.add(Projections.property(ALIAS_SER + RapServidores.Fields.USUARIO.toString()),ResponsavelAreaTecAceiteTecnicoPendenteVO.Fields.USUARIO_RESPONSAVEL.toString())
				);
		
		if(seqAreaTecnica != null){
			criteria.add(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString(), seqAreaTecnica));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ResponsavelAreaTecAceiteTecnicoPendenteVO.class));
		return (ResponsavelAreaTecAceiteTecnicoPendenteVO) executeCriteriaUniqueResult(criteria);
		
	}

	/**
	 * @param seqAreaTecnica
	 * @return
	 */
	public RapServidores obterResponsavelAreaTecnicaPorSeqArea(Integer seqAreaTecnica) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ALIAS_ATA);

		criteria.setProjection(Projections.property(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC.toString()));
		
		criteria.add(Restrictions.eq(ALIAS_ATA_EXT + PtmAreaTecAvaliacao.Fields.SEQ.toString(), seqAreaTecnica));

		return (RapServidores) executeCriteriaUniqueResult(criteria);
		
	}
}
