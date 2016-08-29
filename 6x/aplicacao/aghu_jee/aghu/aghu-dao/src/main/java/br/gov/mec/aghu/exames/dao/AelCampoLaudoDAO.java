package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.vo.AelExamesXAelParametroCamposLaudoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;


public class AelCampoLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCampoLaudo> {
	
	private static final long serialVersionUID = -4288135931378785757L;

	/**
	 * Obtém AelCampoLaudo por seq/id
	 * @param seq
	 * @return
	 */
	public AelCampoLaudo obterCampoLaudoPorSeq(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		criteria.createAlias(AelCampoLaudo.Fields.GRUPO_RESULTADO_CODIFICADO.toString(), "GRCODI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelCampoLaudo.Fields.GRUPO_RESULTADO_CARACTERISTICA.toString(), "GRC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SEQ.toString(), seq));
		
		return (AelCampoLaudo) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa AelCampoLaudo por tipo
	 * @param Object objPesquisa
	 * @return DetachedCriteria
	 */
	
	public List<AelCampoLaudo> pesquisarAelCampoLaudoTipo(DominioTipoCampoCampoLaudo tipo, Object pesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		
		if (tipo!=null){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tipo));
		}else{
			criteria.add(Restrictions.or(
				Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), DominioTipoCampoCampoLaudo.N),
				Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), DominioTipoCampoCampoLaudo.E))
			);	
		}
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		this.obterCriterioSuggestion(criteria, pesquisa);
		
		criteria.addOrder(Order.asc(AelCampoLaudo.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}
	
	
	public List<AelCampoLaudo> pesquisarAelCampoLaudoSB(DominioTipoCampoCampoLaudo tipo, Object pesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		
		if (tipo!=null){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tipo));
		}else{
			criteria.add(Restrictions.or(
				Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), DominioTipoCampoCampoLaudo.N),
				Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), DominioTipoCampoCampoLaudo.E))
			);	
		}
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		this.obterCriterioSuggestion(criteria, pesquisa);
		
		return executeCriteria(criteria, 0, 150, AelCampoLaudo.Fields.ORDEM.toString(), true);
	}	
	
	
	public Long pesquisarAelCampoLaudoSBCount(DominioTipoCampoCampoLaudo tipo, Object pesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		
		if (tipo!=null){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tipo));
		}else{
			criteria.add(Restrictions.or(
				Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), DominioTipoCampoCampoLaudo.N),
				Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), DominioTipoCampoCampoLaudo.E))
			);	
		}
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		this.obterCriterioSuggestion(criteria, pesquisa);
		
		return executeCriteriaCount(criteria);
	}	
	
	
	public Long countAelCampoLaudoTipo(DominioTipoCampoCampoLaudo tipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tipo));
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}	
	
	public List<AelCampoLaudo> listarCampoLaudoSuggestionBox(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		String strPesquisa = (String) param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
				
			} else {
				criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria, 0, 100, "seq", true);
	}

	
	/**
	 * Retorna ScoMaterial original
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AelCampoLaudo obterOriginal(AelCampoLaudo elementoModificado) {
		return super.obterOriginal(elementoModificado);
		
		/*final Integer id = elementoModificado.getSeq();
		
		StringBuilder hql = new StringBuilder();

		hql.append("select o.").append(AelCampoLaudo.Fields.SEQ.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.NOME.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.TIPO_CAMPO.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.FLUXO.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.GRUPO_RESULTADO_CARACTERISTICA.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.GRUPO_RESULTADO_CODIFICADO.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.ORDEM.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.CANCELA_ITEM_DEPT.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.PERTENCE_APAC.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.NOME_SUMARIO.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.DIVIDE_POR_MIL.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.PERMITE_DIGITACAO.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.PERTENCE_CONTADOR.toString());
		hql.append(", o.").append(AelCampoLaudo.Fields.PERTENCE_ABS.toString());
		
		hql.append(" from ").append(AelCampoLaudo.class.getSimpleName()).append(" o ");
		
		hql.append(" left outer join o.").append(AelCampoLaudo.Fields.GRUPO_RESULTADO_CARACTERISTICA.toString());
		hql.append(" left outer join o.").append(AelCampoLaudo.Fields.GRUPO_RESULTADO_CODIFICADO.toString());
		hql.append(" left outer join o.").append(AelCampoLaudo.Fields.SERVIDOR.toString());
		
		hql.append(" where o.").append(AelCampoLaudo.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		AelCampoLaudo original = null;
		List<Object[]> camposList = (List<Object[]>) query.getResultList();
		
		if(camposList != null && camposList.size() > 0) {
			
			Object[] campos = camposList.get(0);
			original = new AelCampoLaudo();
			
			original.setSeq(id);
			original.setNome((String)campos[1]);
			original.setTipoCampo((DominioTipoCampoCampoLaudo)campos[2]);
			original.setFluxo((Boolean)campos[3]);
			original.setSituacao((DominioSituacao)campos[4]);
			original.setGrupoResultadoCaracteristica((AelGrupoResultadoCaracteristica)campos[5]);
			original.setGrupoResultadoCodificado((AelGrupoResultadoCodificado)campos[6]);
			original.setServidor((RapServidores)campos[7]);
			original.setCriadoEm((Date)campos[8]);
			original.setOrdem((Short)campos[9]);
			original.setCancelaItemDept((Boolean)campos[10]);
			original.setPertenceApac((Boolean)campos[11]);
			original.setNomeSumario((String)campos[12]);
			original.setDividePorMil((Boolean)campos[13]);
			original.setPermiteDigitacao((Boolean)campos[14]);
			original.setPertenceContador((Boolean)campos[15]);
			original.setPertenceAbs((Boolean)campos[13]);
			
		}
		
		return original;
		*/
	}
	

	/**
	 * Obtém a criteria pra pesquisa de Campos Laudo
	 * @param campoLaudo
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria obterCriteriaPesquisarCampoLaudo(AelCampoLaudo campoLaudo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
	
		criteria.createAlias(AelCampoLaudo.Fields.GRUPO_RESULTADO_CARACTERISTICA.toString(), "grcar", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelCampoLaudo.Fields.GRUPO_RESULTADO_CODIFICADO.toString(), "grcod", JoinType.LEFT_OUTER_JOIN);
		
		if(campoLaudo.getSeq() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SEQ.toString(), campoLaudo.getSeq()));
		}
		
		if(StringUtils.isNotBlank(campoLaudo.getNome())){
			criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME.toString(), StringUtils.trim(campoLaudo.getNome()),MatchMode.ANYWHERE));
		}
		
		if(campoLaudo.getSituacao() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), campoLaudo.getSituacao()));
		}
		
		if(campoLaudo.getTipoCampo() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), campoLaudo.getTipoCampo()));
		}
		
		if(campoLaudo.getGrupoResultadoCodificado() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.GRUPO_RESULTADO_CODIFICADO.toString(), campoLaudo.getGrupoResultadoCodificado()));
		}
		
		if(campoLaudo.getGrupoResultadoCaracteristica() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.GRUPO_RESULTADO_CARACTERISTICA.toString(), campoLaudo.getGrupoResultadoCaracteristica()));
		}
		
		if(campoLaudo.getPermiteDigitacao() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.PERMITE_DIGITACAO.toString(), campoLaudo.getPermiteDigitacao()));
		}
		
		if(campoLaudo.getCancelaItemDept() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.CANCELA_ITEM_DEPT.toString(), campoLaudo.getCancelaItemDept()));
		}	
		
		if(campoLaudo.getPertenceContador() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.PERTENCE_CONTADOR.toString(), campoLaudo.getPertenceContador()));
		}
		
		if(campoLaudo.getPertenceAbs() != null ){
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.PERTENCE_ABS.toString(), campoLaudo.getPertenceAbs()));
		}
		
		if(StringUtils.isNotBlank(campoLaudo.getNomeSumario())) {
			criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME_SUMARIO.toString(), 
					StringUtils.trim(campoLaudo.getNomeSumario()),MatchMode.ANYWHERE));
		}
		
		if(campoLaudo.getOrdem() != null) {
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.ORDEM.toString(), campoLaudo.getOrdem()));
		}
		
		if(campoLaudo.getFluxo() != null) {
			criteria.add(Restrictions.eq(AelCampoLaudo.Fields.FLUXO.toString(), campoLaudo.getFluxo()));
		}

		return criteria;
	}
	
	
	/**
	 * Pesquisa/Obtém a quantidade de registros da pesquisa de Campos Laudo
	 * @param campoLaudo
	 * @return
	 */
	public Long pesquisarCampoLaudoCount(AelCampoLaudo campoLaudo) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarCampoLaudo(campoLaudo);
		return this.executeCriteriaCount(criteria);
	}
	
	public Long pesquisarCampoLaudoFluxogramaCount(AelCampoLaudo campoLaudo) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarCampoLaudo(campoLaudo);
		
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<DominioTipoCampoCampoLaudo> tiposCampos = new ArrayList<DominioTipoCampoCampoLaudo>();
		tiposCampos.add(DominioTipoCampoCampoLaudo.N);
		tiposCampos.add(DominioTipoCampoCampoLaudo.E);
		criteria.add(Restrictions.in(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tiposCampos));
		
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa de Campos Laudo
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param campoLaudo
	 * @return
	 */
	public List<AelCampoLaudo> pesquisarCampoLaudo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelCampoLaudo campoLaudo) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarCampoLaudo(campoLaudo);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public List<AelCampoLaudo> pesquisarCampoLaudoFluxograma(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelCampoLaudo campoLaudo) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarCampoLaudo(campoLaudo);
		
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<DominioTipoCampoCampoLaudo> tiposCampos = new ArrayList<DominioTipoCampoCampoLaudo>();
		tiposCampos.add(DominioTipoCampoCampoLaudo.N);
		tiposCampos.add(DominioTipoCampoCampoLaudo.E);
		criteria.add(Restrictions.in(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tiposCampos));

		criteria.addOrder(Order.asc(AelCampoLaudo.Fields.ORDEM.toString()));
		criteria.addOrder(Order.desc(AelCampoLaudo.Fields.FLUXO.toString()));
		criteria.addOrder(Order.asc(AelCampoLaudo.Fields.NOME_SUMARIO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, null, asc);
	}
	
	
	
	
	
	/**
	 * Pesquisa Campos Laudo Ativos por Tipo Campo
	 * @param tipoCampoCampoLaudo
	 * @return
	 */
	public List<AelCampoLaudo> pesquisarCampoLaudoAtivoPorTipoCampoTextoFixo(DominioTipoCampoCampoLaudo tipoCampoCampoLaudo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tipoCampoCampoLaudo));
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
		
	}
	
	
	/**
	 * Verifica se já existe um Campo Laudo Ativo e do tipo Texto Fixo
	 * @param tipoCampoCampoLaudo
	 * @return
	 */
	public boolean existeCampoLaudoAtivoTipoCampoTextoFixo() {
		return this.pesquisarCampoLaudoAtivoPorTipoCampoTextoFixo(DominioTipoCampoCampoLaudo.T).size() > 0;
		
	}
	
	/**
	 * Pesquisa Campos Laudo Ativos com Ordem repetida
	 * @param seq
	 * @param ordem
	 * @return
	 */
	public List<AelCampoLaudo> pesquisarCampoLaudoAtivoOrdemRepetida(final Integer seq, final Short ordem) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		
		criteria.add(Restrictions.ne(AelCampoLaudo.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.ORDEM.toString(), ordem));
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
		
	}
	
	
	/**
	 * Verifica a existência de alguma dependência
	 * @param elemento
	 * @param classeDependente
	 * @param fieldChaveEstrangeira
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("rawtypes")
	public final boolean existeDependencia(Object elemento, Class classeDependente, Enum fieldChaveEstrangeira) throws BaseException{

		CoreUtil.validaParametrosObrigatorios(elemento,classeDependente, fieldChaveEstrangeira);

		DetachedCriteria criteria = DetachedCriteria.forClass(classeDependente);
		criteria.add(Restrictions.eq(fieldChaveEstrangeira.toString(), elemento));
		
		return executeCriteriaCount(criteria) > 0;
		
	}
	
	
	/**
	 * Retorna o tipo campo e a <br>
	 * situacao de AelCampoLaudo
	 * 
	 * @param rpcRpaSeq
	 * @param rpcSeqp
	 * @return
	 */
	public AelCampoLaudo obterResultadoPadraoTipoCampoESituacao(Integer rpcRpaSeq, Integer rpcSeqp) {
		
		StringBuilder hql = new StringBuilder(200);
		
		hql.append("SELECT CAL.");
		hql.append(AelCampoLaudo.Fields.TIPO_CAMPO.toString());
		hql.append(",CAL.").append(AelCampoLaudo.Fields.SITUACAO.toString());
		hql.append(" FROM ");
		hql.append(AelCampoLaudo.class.getSimpleName()).append(" CAL, ");
		hql.append(AelResultadoPadraoCampo.class.getSimpleName()).append(" RPC ");
		hql.append(" WHERE ");
		hql.append(" 	 RPC.").append(AelResultadoPadraoCampo.Fields.RPA_SEQ.toString()).append(" = :pRpcRpaSeq");
		hql.append(" AND RPC.").append(AelResultadoPadraoCampo.Fields.SEQP.toString()).append(" = :pRpcSeqp");
		hql.append(" AND ").append(" (CAL.").append(AelCampoLaudo.Fields.SEQ.toString()).append(" = RPC.");
		hql.append(AelResultadoPadraoCampo.Fields.CAL_SEQ.toString());
		hql.append(" OR CAL.").append(AelCampoLaudo.Fields.SEQ.toString()).append(" = RPC.");
		hql.append(AelResultadoPadraoCampo.Fields.PCL_CAL_SEQ.toString()).append(')');
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("pRpcRpaSeq", rpcRpaSeq);
		query.setParameter("pRpcSeqp", rpcSeqp);
		
		AelCampoLaudo campoLaudo = null;
		List<Object[]> camposList = (List<Object[]>) query.getResultList();
		
		if(camposList != null && camposList.size() > 0) {
			Object[] campos = camposList.get(0);
			campoLaudo = new AelCampoLaudo();

			campoLaudo.setTipoCampo((DominioTipoCampoCampoLaudo)campos[0]);
			campoLaudo.setSituacao((DominioSituacao)campos[1]);
		}
		
		return campoLaudo;
	}
	
	/**
	 * Pesquisa Campos Laudo com Fluxo Ativo
	 * @return
	 */
	public List<AelCampoLaudo> pesquisarCampoLaudoFluxoAtivo() {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.FLUXO.toString(), Boolean.TRUE));
		
		criteria.addOrder(Order.asc(AelCampoLaudo.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
		
	}
	
	
	/**
	 * Verifica se existe dependencia 
	 * nos registros da tabela ael_campos_laudo
	 * atraves do seq da tabela AEL_GRUPO_RESULTADO_CARACTERIS
	 * 
	 * @param seq
	 * @return
	 */
	public List<AelCampoLaudo> listarCampoLaudoPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.GCA_SEQ.toString(), seq));
		return this.executeCriteria(criteria);
	}
	
	public AelCampoLaudo pesquisarCampoLaudoPorResultado(AelResultadoExame resultado){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class);
		criteria.createCriteria(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("pcl."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "cal", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ID.toString(), resultado.getId()));
		AelResultadoExame result = (AelResultadoExame)executeCriteriaUniqueResult(criteria);
		if(result==null || result.getParametroCampoLaudo() == null || result.getParametroCampoLaudo().getCampoLaudo() == null){
			return null;
		}else{
			return result.getParametroCampoLaudo().getCampoLaudo();
		}
	}

	public Long obterLaudoCount(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);

		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
				
			} else {
				criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Obtém AelCampoLaudo por nome
	 * @param Object objPesquisa
	 * @return DetachedCriteria
	 */
	
	public Boolean existeCampoLaudoNome(String nome){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		
		criteria.add(Restrictions.eq(AelCampoLaudo.Fields.NOME.toString(), nome.trim()));
		
		return executeCriteriaCount(criteria) > 0;
		
	}
	
	
	public AelCampoLaudo pesquisarCampoLaudoPorParametrocampoLaudo(AelParametroCamposLaudo pcl){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "pcl");
		
		criteria.createCriteria("pcl."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "cal", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), pcl.getId().getVelEmaExaSigla()));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), pcl.getId().getVelEmaManSeq()));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_SEQP.toString(), pcl.getId().getSeqp()));
		criteria.add(Restrictions.ne("pcl."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pcl.getId().getCalSeq()));
		
		
		List<AelParametroCamposLaudo> results = executeCriteria(criteria);
		if(results == null || results.isEmpty()) {
			return null;
		}else{
			return results.get(0).getCampoLaudo();
		}
	}

	public List<AelCampoLaudo> buscarAelCampoLaudoPorVAelCampoLaudoExme(final String valPesquisa, final String velEmaExaSigla, final Integer velEmaManSeq,
			final boolean limitarEm100) {
		if (StringUtils.isEmpty(velEmaExaSigla) || velEmaManSeq == null) {
			throw new IllegalArgumentException("Parâmetro inválido.");
		}
		final DetachedCriteria criteria = criarCriteriaAelCampoLaudoPorVAelCampoLaudoExme(valPesquisa, velEmaExaSigla, velEmaManSeq);

		criteria.addOrder(Order.asc("cal." + AelCampoLaudo.Fields.NOME.toString()));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		this.addOrderCriteriaVAelCampoLaudoExme(criteria);
		if (limitarEm100) {
			return this.executeCriteria(criteria, 0, 100, null, false);
		} else {
			return this.executeCriteria(criteria);
		}
	}

	private DetachedCriteria criarCriteriaAelCampoLaudoPorVAelCampoLaudoExme(final String valPesquisa, final String velEmaExaSigla, final Integer velEmaManSeq) {
		final DetachedCriteria criteria = this.criarCriteriaVAelCampoLaudoExme(false);

		criteria.add(Restrictions.eq("pcl." + AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), velEmaExaSigla));
		criteria.add(Restrictions.eq("pcl." + AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), velEmaManSeq));
		criteria.add(Restrictions.eq("vel." + AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));

		if (StringUtils.isNotEmpty(valPesquisa)) {
			criteria.add(Restrictions.ilike("cal." + AelCampoLaudo.Fields.NOME.toString(), valPesquisa, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	/*
	 * @ORADB v_ael_campo_laudo_exme
	 */
	private DetachedCriteria criarCriteriaVAelCampoLaudoExme(boolean addOrder) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class, "cal");
		criteria.createAlias("cal." + AelCampoLaudo.Fields.PARAMETROS_CAMPOS_LAUDO.toString(), "pcl");
		criteria.createAlias("pcl." + AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "vel");

		criteria.add(Restrictions.eq("vel." + AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));
		criteria.add(Restrictions.in("cal." + AelCampoLaudo.Fields.TIPO_CAMPO.toString(), new DominioTipoCampoCampoLaudo[] { DominioTipoCampoCampoLaudo.A,
				DominioTipoCampoCampoLaudo.N, DominioTipoCampoCampoLaudo.C }));

		if (addOrder) {
			this.addOrderCriteriaVAelCampoLaudoExme(criteria);
		}
		return criteria;
	}

	private void addOrderCriteriaVAelCampoLaudoExme(final DetachedCriteria criteria) {
		criteria.addOrder(Order.asc("cal." + AelCampoLaudo.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("cal." + AelCampoLaudo.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("cal." + AelCampoLaudo.Fields.SITUACAO.toString()));
		criteria.addOrder(Order.asc("cal." + AelCampoLaudo.Fields.GTC_SEQ.toString()));
		criteria.addOrder(Order.asc("pcl." + AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()));
		criteria.addOrder(Order.asc("pcl." + AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()));
		criteria.addOrder(Order.asc("pcl." + AelParametroCamposLaudo.Fields.VEL_SEQP.toString()));
		criteria.addOrder(Order.asc("pcl." + AelParametroCamposLaudo.Fields.CAL_SEQ.toString()));
		criteria.addOrder(Order.asc("vel." + AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString()));
		criteria.addOrder(Order.asc("vel." + AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString()));
		criteria.addOrder(Order.asc("vel." + AelVersaoLaudo.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc("vel." + AelVersaoLaudo.Fields.SITUACAO.toString()));
	}
	
	
	/**
	 * Pesquisa registros da tabela<br>
	 * AEL_CAMPOS_LAUDO atraves do filtro<br>
	 * de pesquisa da suggestion.
	 * 
	 * @param pesquisa
	 * @return
	 */
	public List<AelCampoLaudo> pesquisarAelCampoLaudoSuggestion(Object pesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		
		this.obterCriterioSuggestion(criteria, pesquisa);
		
		criteria.addOrder(Order.asc(AelCampoLaudo.Fields.NOME.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AelCampoLaudo> pesquisarCampoLaudoExameMaterial(Object param, String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class,"campoLaudoAlias");
		String strPesquisa = (String) param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
				
			} else {
				criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class,"parametro");
		subCriteria.setProjection(
				Projections.projectionList().add(Projections.property(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()))
				.add(Projections.property(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()))
				.add(Projections.property(AelParametroCamposLaudo.Fields.VEL_SEQP.toString()))
				.add(Projections.property(AelParametroCamposLaudo.Fields.SEQP.toString()))
				.add(Projections.property(AelParametroCamposLaudo.Fields.CAL_SEQ.toString())));
		subCriteria.add(Restrictions.eqProperty("parametro." + AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), 
				"campoLaudoAlias." + AelCampoLaudo.Fields.SEQ.toString()));
		
		subCriteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), emaExaSigla));
		subCriteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), emaManSeq));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.addOrder(Order.asc("campoLaudoAlias."+AelCampoLaudo.Fields.NOME.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriterioSuggestion(DetachedCriteria criteria, Object pesquisa) {
		String strPesquisa = (String)pesquisa;
		if (StringUtils.isNotBlank(strPesquisa)){
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(AelCampoLaudo.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
			}else{
				criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}	
		}
		return criteria;
	}

	public Long contarAelCampoLaudoPorVAelCampoLaudoExme(String valPesquisa, String velEmaExaSigla, Integer velEmaManSeq) {
		return this.executeCriteriaCount(criarCriteriaAelCampoLaudoPorVAelCampoLaudoExme(valPesquisa,velEmaExaSigla, velEmaManSeq));
	}
	
	public List<AelExamesXAelParametroCamposLaudoVO> obterAelCampoLaudoPorNome(String nome, String siglaExame){
		String sqlExames = String.format("(select DESCRICAO from AEL_EXAMES ex where ex.sigla = '%s') as descricao ", (siglaExame == null)? "":siglaExame);
		
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class, "CAL");
		
		criteria.createAlias("CAL."+AelCampoLaudo.Fields.PARAMETROS_CAMPOS_LAUDO.toString(), "PCL", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("CAL." +AelCampoLaudo.Fields.SEQ.toString()).as(AelCampoLaudo.Fields.SEQ.toString())))
				.add(Projections.property("CAL." +AelCampoLaudo.Fields.NOME.toString()).as(AelCampoLaudo.Fields.NOME.toString()))
				.add(Projections.sqlProjection(sqlExames, new String [] {AelExamesXAelParametroCamposLaudoVO.Fields.DESCRICAO.toString()},new Type[] { StringType.INSTANCE}))
		);
		
		
		if(StringUtils.isNotBlank(nome)){
			criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), (siglaExame == null)? "":siglaExame));
		criteria.add(Restrictions.isNull("PCL."+AelParametroCamposLaudo.Fields.TEXTO_LIVRE.toString()));
		criteria.add(Restrictions.eq("CAL."+AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
	
		criteria.addOrder(Order.asc(AelCampoLaudo.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelExamesXAelParametroCamposLaudoVO.class));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long obterAelCampoLaudoPorNomeCount(String nome, String siglaExame){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudo.class, "CAL");
		
		criteria.createAlias("CAL."+AelCampoLaudo.Fields.PARAMETROS_CAMPOS_LAUDO.toString(), "PCL", JoinType.INNER_JOIN);
		
		if(StringUtils.isNotBlank(nome)){
			criteria.add(Restrictions.ilike(AelCampoLaudo.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), (siglaExame == null)? "":siglaExame));
		criteria.add(Restrictions.isNull("PCL."+AelParametroCamposLaudo.Fields.TEXTO_LIVRE.toString()));
		criteria.add(Restrictions.eq("CAL."+AelCampoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaCountDistinct(criteria, AelCampoLaudo.Fields.NOME.toString(), true);
	}
	
}