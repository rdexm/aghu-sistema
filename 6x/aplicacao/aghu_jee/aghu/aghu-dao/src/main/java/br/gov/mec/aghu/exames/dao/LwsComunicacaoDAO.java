package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioLwsCodigoResposta;
import br.gov.mec.aghu.dominio.DominioLwsOrigem;
import br.gov.mec.aghu.dominio.DominioLwsTipoComunicacao;
import br.gov.mec.aghu.dominio.DominioLwsTipoStatusTransacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaResultadoCargaInterfaceVO;
import br.gov.mec.aghu.exames.vo.PesquisaResultadoCargaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.LwsComSolicitacaoExame;
import br.gov.mec.aghu.model.LwsComunicacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.StringUtil;

public class LwsComunicacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<LwsComunicacao>{
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = 6970970109426073664L;

	/**
	 * Obtém valor sequencial de LwsComunicacao
	 * Equivale: ORADBD FUNCTION LWSC_GET_LWS_LWF_SQ1_NEXTVAL
	 * @return
	 */
	public Integer obterProximoIdComunicacao() {

		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComunicacao.class);
		criteria.setProjection(Projections.max(LwsComunicacao.Fields.ID_COMUNICACAO.toString()));

		Integer retorno = 0;

		Object maxSequencial = this.executeCriteriaUniqueResult(criteria);

		if (maxSequencial != null) {
			retorno = (Integer) maxSequencial;
		}

		return ++retorno;
	}

	/**
	 * Obtém resultado pendente do módulo GESTAM
	 * @return
	 */
	public LwsComunicacao obterRecepcaoResultadoGestaoAmostra() {
		/* 
		 * Pesquisa resultado único.
		 * Para implementar vários resultados será necessário parametrizar a quantidade máxima 
		 */
		List<LwsComunicacao> lista = this.pesquisarRecepcaoResultadoGestaoAmostra(1); // 
		return lista.isEmpty() ? null : lista.get(0);
	}

	/**
	 * Pesquisa recepção de resultados do módulo GESTAM
	 * @param setIdComunicacao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LwsComunicacao> pesquisarRecepcaoResultadoGestaoAmostra(final int maxResults) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComunicacao.class);

		criteria.add(Restrictions.eq(LwsComunicacao.Fields.ID_ORIGEM.toString(), (short)99)); // Origem GESTAM
		criteria.add(Restrictions.eq(LwsComunicacao.Fields.ID_DESTINO.toString(), (short)90)); // Destino LIS HIS
		criteria.add(Restrictions.eq(LwsComunicacao.Fields.TIPO_COMUNICACAO.toString(), DominioLwsTipoComunicacao.RECEPCAO_RESULTADOS));
		criteria.add(Restrictions.eq(LwsComunicacao.Fields.STATUS.toString(), DominioLwsTipoStatusTransacao.NAO_PROCESSADA));

		return this.executeCriteria(criteria, 0, maxResults, null);
	}

	public Long pesquisarLwsComSolicitacaoExamesCount(final PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException {
		DetachedCriteria criteria = obtercriteriaPesquisa(filtro);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa comunicações conforme o filtro
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<PesquisaResultadoCargaVO> pesquisarLwsComSolicitacaoExames(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException {

		List<PesquisaResultadoCargaVO> listaRetorno = new ArrayList<PesquisaResultadoCargaVO>();

		DetachedCriteria criteria = obtercriteriaPesquisa(filtro);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(LwsComunicacao.Fields.SEQ_COMUNICACAO.toString()))
				.add(Projections.property(LwsComunicacao.Fields.DATA_HORA.toString()))
				.add(Projections.property(LwsComunicacao.Fields.TIPO_COMUNICACAO.toString()))
				.add(Projections.property("lcom."+LwsComSolicitacaoExame.Fields.SOLICITACAO.toString()))
				.add(Projections.property(LwsComunicacao.Fields.OBSERVACAO.toString()))
				.add(Projections.property(LwsComunicacao.Fields.COD_RESPOSTA.toString())));

		List<Object[]> valores = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc); 

		if (valores != null && valores.size() > 0) {

			for (Object[] objects : valores) {

				PesquisaResultadoCargaVO vo = new PesquisaResultadoCargaVO();

				vo.setDataHora((Date)objects[1]);
				vo.setTipoComunicacao(((DominioLwsTipoComunicacao)objects[2]).getDescricao());

				if (objects[3] != null) {
					if(objects[3].toString().length()==11){
						vo.setSolicitacao(Integer.valueOf(objects[3].toString().substring(0, 8)));
						vo.setAmostra(Short.valueOf(objects[3].toString().substring(8, 11)));
					}else{
						vo.setSolicitacao(Integer.valueOf(objects[3].toString().substring(0, objects[3].toString().length())));
						vo.setAmostra((short)0);
					}
				}

				String observacao = obterObservacaoPorSeqComunicacao((Integer) objects[0], filtro);

				if (observacao != null && !observacao.isEmpty()) {

					vo.setObservacao(observacao);

				} else {

					vo.setObservacao("Mensagem Processada.");

				}

				listaRetorno.add(vo);

			}

		}


		return listaRetorno;

	}

	/**
	 * 
	 * @param filtro
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private DetachedCriteria obtercriteriaPesquisa(PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException{

		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComunicacao.class, "LWS");

		criteria.createCriteria(LwsComunicacao.Fields.LWS_COM_SOLICITACAO_EXAMEES.toString(), "lcom", DetachedCriteria.LEFT_JOIN);

		if (filtro.getDataHoraInicial() != null) {
			criteria.add(Restrictions.ge(LwsComunicacao.Fields.DATA_HORA.toString(), filtro.getDataHoraInicial()));
		}

		if (filtro.getDataHoraFinal() != null) {
			criteria.add(Restrictions.le(LwsComunicacao.Fields.DATA_HORA.toString(), filtro.getDataHoraFinal()));
		}

		if (filtro.getTipoComunicao() != null) {

			criteria.add(Restrictions.eq(LwsComunicacao.Fields.TIPO_COMUNICACAO.toString(), filtro.getTipoComunicao()));

		} else {

			criteria.add(Restrictions.in(LwsComunicacao.Fields.TIPO_COMUNICACAO.toString(), new DominioLwsTipoComunicacao[] {DominioLwsTipoComunicacao.PEDIDO_CARGA_EXAMES, DominioLwsTipoComunicacao.RECEPCAO_RESULTADOS, DominioLwsTipoComunicacao.CANCELAMENTO_PEDIDO_EXAME}));

		}
		
		if (filtro.getOrigem() != null) {
			
			
			if (filtro.getOrigem().equals(DominioLwsOrigem.AGHU)) {
				
				criteria.add(Restrictions.eq(LwsComunicacao.Fields.ID_ORIGEM.toString(), obterIdModuloLisHis()));
				
			} else {
				
				criteria.add(Restrictions.eq(LwsComunicacao.Fields.ID_ORIGEM.toString(), obterIdModuloGestaoAmostra()));
				
			}
			
		}

		if (filtro.getSolicitacao() != null) {

			criteria.add(Restrictions.ilike("lcom."+LwsComSolicitacaoExame.Fields.SOLICITACAO.toString(), StringUtil.adicionaZerosAEsquerda(filtro.getSolicitacao(), 8), MatchMode.START));

		}

		if (filtro.getAmostra() != null) {

			criteria.add(Restrictions.ilike("lcom."+LwsComSolicitacaoExame.Fields.SOLICITACAO.toString(), StringUtil.adicionaZerosAEsquerda(filtro.getAmostra(), 3), MatchMode.END));

		}
		
		if (filtro.getIndMostraComErro() != null) {
			
			if (filtro.getIndMostraComErro().equals(DominioSimNao.S)) {

				DetachedCriteria subQueryExists = DetachedCriteria.forClass(LwsComunicacao.class, "LWS2");
				subQueryExists.add(Restrictions.ne(LwsComunicacao.Fields.COD_RESPOSTA.toString(), DominioLwsCodigoResposta.A00));
				subQueryExists.add(Restrictions.eqProperty("LWS." + LwsComunicacao.Fields.ID_COMUNICACAO.toString(), "LWS2." + LwsComunicacao.Fields.SEQ_COMUNICACAO.toString()));

				subQueryExists.setProjection(Projections.property(LwsComunicacao.Fields.SEQ_COMUNICACAO.toString()));

				criteria.add(Subqueries.exists(subQueryExists));

			} else {

				DetachedCriteria subQueryExists = DetachedCriteria.forClass(LwsComunicacao.class, "LWS2");
				subQueryExists.add(Restrictions.eq(LwsComunicacao.Fields.COD_RESPOSTA.toString(), DominioLwsCodigoResposta.A00));
				subQueryExists.add(Restrictions.eqProperty("LWS." + LwsComunicacao.Fields.ID_COMUNICACAO.toString(), "LWS2." + LwsComunicacao.Fields.SEQ_COMUNICACAO.toString()));

				subQueryExists.setProjection(Projections.property(LwsComunicacao.Fields.SEQ_COMUNICACAO.toString()));

				criteria.add(Subqueries.exists(subQueryExists));

			}
			
		}

		return criteria;

	}

	/**
	 * Retorna a resposta da comunicacao
	 * @param seqComunicacao
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String obterObservacaoPorSeqComunicacao(Integer seqComunicacao, PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComunicacao.class);
		criteria.add(Restrictions.eq(LwsComunicacao.Fields.TIPO_COMUNICACAO.toString(), DominioLwsTipoComunicacao.PROPOSTA_PEDIDO_CARGA_EXAMES));
		criteria.add(Restrictions.eq(LwsComunicacao.Fields.SEQ_COMUNICACAO.toString(), seqComunicacao));
		
		if (filtro.getOrigem() != null) {
			
			
			if (filtro.getOrigem().equals(DominioLwsOrigem.AGHU)) {
				
				criteria.add(Restrictions.eq(LwsComunicacao.Fields.ID_ORIGEM.toString(), obterIdModuloLisHis()));
				
			} else {
				
				criteria.add(Restrictions.eq(LwsComunicacao.Fields.ID_ORIGEM.toString(), obterIdModuloGestaoAmostra()));
				
			}
			
		}

		List<LwsComunicacao> comunicacao = executeCriteria(criteria);

		if (comunicacao != null && !comunicacao.isEmpty()) {

			return comunicacao.get(0).getObservacao();

		}

		return null;

	}
	
	/**
	 * Atualiza para processada o status da comunicação
	 * @param idComunicacao
	 */
	public void atualizarResultadoGestaoAmostraStatusProcessada(Integer idComunicacao) {
		/*
		 * Existe uma pendência quanto a atualização da comunicação via EJB. Isso justifica o uso de SQL nativo.
		 */
		Query query = createQuery(
				"update " + LwsComunicacao.class.getName() + " set " + LwsComunicacao.Fields.STATUS.toString() 
				+ " = :status" 
				+ " where " + LwsComunicacao.Fields.ID_COMUNICACAO.toString() + " = :idComunicacao");

		query.setParameter("status", DominioLwsTipoStatusTransacao.PROCESSADA);
		query.setParameter("idComunicacao", idComunicacao);

		query.executeUpdate();

		this.flush();
	}
		
	/**
	 * Obtém o id da origem da comunicação LWS
	 * Obs. O valor padrão é 90
	 */
	public Short obterIdModuloLisHis() throws ApplicationBusinessException{
		return this.obterIdSistemasModulosLws(AghuParametrosEnum.P_LIS_HIS);
	}
	
	/**
	 * Obtém o destino da comunicação LWS
	 * Obs. O valor padrão é 99
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Short obterIdModuloGestaoAmostra() throws ApplicationBusinessException{
		return this.obterIdSistemasModulosLws(AghuParametrosEnum.P_MODULO_GESTAO_AMOSTRA_VFR);
	}
	
	/**
	 * Método reutilizado para obtenção tanto do id de origem quanto destino da comunicação LWS
	 * @param parametrosEnum
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Short obterIdSistemasModulosLws(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException{
		
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(parametrosEnum);
		return parametro.getVlrNumerico().shortValue();
		
	}
	
	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
}