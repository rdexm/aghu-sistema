package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.vo.MbcpProcedimentoCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentoDaRequisicaoVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MbcProcedimentoCirurgicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProcedimentoCirurgicos> {

	

	private static final long serialVersionUID = 4099431122315152457L;
	
	
	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionProcedimento(final AghUnidadesFuncionais unidade, final Date dtProcedimento, 
			final String filtro, final Short eprEspSeq,  final DominioSituacao situacao, final boolean indPrincipal){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class,"PPC");

		criteria.createAlias("PPC."+MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "PCI");
		criteria.createAlias("PPC."+MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "CRG");

		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.DATA.toString(), dtProcedimento));
		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), unidade));
		criteria.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), indPrincipal));
		
		if(eprEspSeq != null){
			criteria.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString(), eprEspSeq));
		}
		
		if(StringUtils.isNotEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq("PCI."+MbcProcedimentoCirurgicos.Fields.SEQ.toString(), Integer.valueOf(filtro)));
				
			} else {
				criteria.add(Restrictions.ilike("PCI."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));

			}
		}
		
		criteria.setProjection(Projections.projectionList()
									.add(Projections.distinct(Projections.property("PCI."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString())), SuggestionListaCirurgiaVO.Fields.NOME.toString())
									.add(Projections.property("PPC."+MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()), SuggestionListaCirurgiaVO.Fields.EPR_PCI_SEQ.toString())
									.add(Projections.property("PPC."+MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString()), SuggestionListaCirurgiaVO.Fields.GRC_SEQ.toString())
							  );
		
		criteria.addOrder(Order.asc(SuggestionListaCirurgiaVO.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(SuggestionListaCirurgiaVO.class));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaProcedimentoCirurgico() {
		// Metódo criado prevendo reuso.
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		return criteria;
	}
	

	protected DetachedCriteria montarCriterioBuscaProcedimento(Object objPesquisa) {
		// Metódo para ser usado na Tarefa #882 na SB de Procedimentos
		// Realizados no Leito
		/*
		 * select PCI.IND_SITUACAO DSP_IND_SITUACAO4 ,PCI.SEQ PCI_SEQ
		 * ,PCI.DESCRICAO DSP_DESCRICAO2 from MBC_PROCEDIMENTO_CIRURGICOS PCI
		 * where (( (:SYSTEM.MODE = 'ENTER-QUERY') ) OR ((:SYSTEM.MODE =
		 * 'NORMAL') and (PCI.IND_PROC_REALIZADO_LEITO = 'S' and
		 * PCI.IND_SITUACAO = 'A') )) order by PCI.DESCRICAO asc
		 */
		DetachedCriteria criteria = obterCriteriaProcedimentoCirurgico();
		String srtPesquisa = (String) objPesquisa;
		
		// criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_PROC_REALIZADO_LEITO.toString(),
		// Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(),
						Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(
						MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), srtPesquisa,
						MatchMode.ANYWHERE));
			}
		}

		
		
		return criteria;
	}
	

	/**
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<MbcProcedimentoCirurgicos> buscaProcedimentoCirurgicos(Object objPesquisa) {
		DetachedCriteria criteria = this.montarCriterioBuscaProcedimento(objPesquisa);
		// No Aghu a LOV esta ordenada pela descrição
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		return super.executeCriteria(criteria,0, 100, null,false);
	}
	
	public Long buscaProcedimentoCirurgicosCount(Object objPesquisa) {
		return super.executeCriteriaCount(montarCriterioBuscaProcedimento(objPesquisa));
	}
	
	
	public List<MbcProcedimentoCirurgicos> buscaProcedimentoCirurgicos(Object objPesquisa, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriterioBuscaProcedimento(objPesquisa);
		// No Aghu a LOV esta ordenada pela descrição
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		return super.executeCriteria(criteria,0, maxResults, null,false);
	}
	

	public List<MbcProcedimentoCirurgicos> buscaProcedimentoCirurgicosRealizadoLeito(
			Object objPesquisa) {
		// Metódo para ser usado na Tarefa #882 na SB de Procedimentos
		// Realizados no Leito
		/*
		 * select PCI.IND_SITUACAO DSP_IND_SITUACAO4 ,PCI.SEQ PCI_SEQ
		 * ,PCI.DESCRICAO DSP_DESCRICAO2 from MBC_PROCEDIMENTO_CIRURGICOS PCI
		 * where (( (:SYSTEM.MODE = 'ENTER-QUERY') ) OR ((:SYSTEM.MODE =
		 * 'NORMAL') and (PCI.IND_PROC_REALIZADO_LEITO = 'S' and
		 * PCI.IND_SITUACAO = 'A') )) order by PCI.DESCRICAO asc
		 */

		List<MbcProcedimentoCirurgicos> list;

		DetachedCriteria criteria = obterCriteriaProcedimentoCirurgico();
		String srtPesquisa = (String) objPesquisa;

		criteria.add(Restrictions.eq(
				MbcProcedimentoCirurgicos.Fields.IND_PROC_REALIZADO_LEITO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(),
						Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(
						MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), srtPesquisa,
						MatchMode.ANYWHERE));
			}
		}

		// No Aghu a LOV esta ordenada pela descrição
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));

		list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Pesquisar por codigo e descricao.
	 * 
	 * @param filtro
	 * @return
	 */
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicosPorCodigoDescricao(
			Object filtro) {
		String strPesquisa = (String) filtro;

		// Se for número pesquida por código = chave primária. Caso contrário
		// pesquisa por descrição.
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);

			_criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(),
					Integer.valueOf(strPesquisa)));

			List<MbcProcedimentoCirurgicos> list = executeCriteria(_criteria, 0, 25, null, false);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, 25, null, false);
	}
	
	

    /**
	 * Metodo que monta uma criteria para pesquisar MbcProcedimentoCirurgicos filtrando
	 *  pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaMbcProcedimentoCirurgicos(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		String strPesquisa = (String) objPesquisa;
		
						
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/** Pesquisa por codigo ou descricao (testando se o parametro é um número ou uma descricao)
	 * 
	 * @param filtro
	 * @param order
	 * @param maxResults
	 * @return
	 */
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicosPorCodigoDescricao(
			Object filtro, String order, Integer maxResults, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteriaProcedimentosCirurgicos(filtro, situacao);
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, maxResults, order, true);
	}

	public Long pesquisarProcedimentosCirurgicosPorCodigoDescricaoCount(
			Object filtro, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteriaProcedimentosCirurgicos(filtro, situacao);
		
		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria montarCriteriaProcedimentosCirurgicos(Object filtro, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		String strPesquisa = (String) filtro;
		
		if (situacao != null){
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (CoreUtil.isNumeroInteger(strPesquisa)) {

			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(),
					Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	public Long obterCountProcedimentoCirurgicoPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), descricao));
		return executeCriteriaCount(criteria);
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por MbcProcedimentoCirurgicos,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return List<MbcProcedimentoCirurgicos>
	 */
	public List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicos(Object objPesquisa){
		List<MbcProcedimentoCirurgicos> lista = null;
		DetachedCriteria criteria = montarCriteriaMbcProcedimentoCirurgicos(objPesquisa);
		
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.SEQ.toString()));
		
		lista = executeCriteria(criteria, 0, 100, null, true);
		
		return lista;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por MbcProcedimentoCirurgicos,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarMbcProcedimentoCirurgicosCount(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaMbcProcedimentoCirurgicos(objPesquisa);

		return executeCriteriaCount(criteria);
	}
	
	public List<MbcProcedimentoCirurgicos> pesquisaProcedimentoCirurgicosPorCodigoDescricaoSituacao(Object filtro, DominioSituacao situacao){
		return pesquisaProcedimentoCirurgicosPorCodigoDescricaoSituacao(filtro, situacao, MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString());
	}
	
	/**
	 * Pesquisa procedimentos cirurgicos através do código, descrição e situação
	 * @param filtro
	 * @param situacao
	 * @return
	 */
	public List<MbcProcedimentoCirurgicos> pesquisaProcedimentoCirurgicosPorCodigoDescricaoSituacao(Object filtro, DominioSituacao situacao, String order){
		DetachedCriteria criteria = this.montarCriteriaProcedimentosCirurgicos(filtro, situacao);
		criteria.addOrder(Order.asc(order));
		return executeCriteria(criteria);
	}
	
	public List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicoPorTipo(String strPesquisa, List<DominioTipoProcedimentoCirurgico> tipos){
		DetachedCriteria criteria = criarCriteriaListMbcProcedimentoCirurgicoPorTipo(strPesquisa, tipos);
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long listarMbcProcedimentoCirurgicoPorTipoCount(String strPesquisa, List<DominioTipoProcedimentoCirurgico> tipos){
		DetachedCriteria criteria = criarCriteriaListMbcProcedimentoCirurgicoPorTipo(strPesquisa, tipos);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(Integer filtroSeq, String filtroDescricao, 
			DominioSituacao filtroIndSituacao, Boolean filtroIndGeraImagensPacs, Boolean filtroIndInteresseCcih) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		criteria.createAlias(MbcProcedimentoCirurgicos.Fields.SERVIDOR.toString(), "RAP");
		
		if (filtroSeq != null) {
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(), filtroSeq));
		}
		
		if (StringUtils.isNotEmpty(filtroDescricao)) {
			criteria.add(Restrictions.ilike(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), filtroDescricao, MatchMode.ANYWHERE));
		}
		
		if (filtroIndSituacao != null) {
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), filtroIndSituacao));
		}
		
		if (filtroIndGeraImagensPacs != null) {
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_GERA_IMAGENS_PACS.toString(), filtroIndGeraImagensPacs));
		}
		
		if (filtroIndInteresseCcih != null) {
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_INTERESSE_CCIH.toString(), filtroIndInteresseCcih));
		}
		
		return criteria;
	}
	
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, Integer filtroSeq, String filtroDescricao, DominioSituacao filtroIndSituacao, Boolean filtroIndGeraImagensPacs, 
			Boolean filtroIndInteresseCcih) {
		DetachedCriteria criteria = montarCriteriaProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(filtroSeq, filtroDescricao, filtroIndSituacao, 
				filtroIndGeraImagensPacs, filtroIndInteresseCcih);
		if (asc) {
			criteria.addOrder(Order.asc(orderProperty));	
		} else {
			criteria.addOrder(Order.desc(orderProperty));
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Buscar os procedimentos realizados da Nota de sala quando não foi feita a descrição e a nota já digitada
	 * Buscar os procedimentos agendados quando não tem descrição e nem nota digitada.
	 * @param codigo Código do paciente
	 * @return
	 */
	public List<ProcedimentosPOLVO> pesquisarProcedimentosSemDescricaoPOL(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class, "pci");
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("crg." + MbcCirurgias.Fields.PAC_CODIGO.toString()),ProcedimentosPOLVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.DATA.toString()), ProcedimentosPOLVO.Fields.DATA.toString())
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ProcedimentosPOLVO.Fields.DESCRICAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SITUACAO.toString()), ProcedimentosPOLVO.Fields.SITUACAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.SEQ.toString())
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.EPR_PCI_SEQ.toString())
				.add(Projections.property("ppc." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()), ProcedimentosPOLVO.Fields.IND_RESP_PROC.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString()), ProcedimentosPOLVO.Fields.TEM_DESCRICAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), ProcedimentosPOLVO.Fields.DIGITA_NOTA_SALA.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()),ProcedimentosPOLVO.Fields.ATD_SEQ.toString());
		
		criteria.setProjection(projection);
		
		criteria.createAlias("pci." + MbcProcedimentoCirurgicos.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "ppc", Criteria.INNER_JOIN);
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "crg", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.eq("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString(), Boolean.FALSE),
				Restrictions.isNull("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString())));		
				
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.PAC_CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), 
				DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosPOLVO.class));
		
		return executeCriteria(criteria);
	}		

	public Long obterCountProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(Integer filtroSeq, String filtroDescricao, 
			DominioSituacao filtroIndSituacao, Boolean filtroIndGeraImagensPacs, Boolean filtroIndInteresseCcih) {
		DetachedCriteria criteria = montarCriteriaProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(filtroSeq, filtroDescricao, filtroIndSituacao, 
				filtroIndGeraImagensPacs, filtroIndInteresseCcih);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListMbcProcedimentoCirurgicoPorTipo(String strPesquisa, List<DominioTipoProcedimentoCirurgico> tipos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(),Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.in(MbcProcedimentoCirurgicos.Fields.TIPO.toString(), tipos));
		return criteria;
	}

	/**
	 * ORADB: PROCEDURE MBCP_POPULA_PROCED_CIRG ESTÓRIA: #24896
	 */
	public List<MbcpProcedimentoCirurgicoVO> obterCursorMbcpProcedimentoCirurgicoVO( final Integer dcgCrgSeq, 
																			 		 final DominioIndRespProc indRespProc,
																					 final DominioSituacao situacao, 
																					 final DominioSituacao indSituacao,
																					 final DominioTipoAtuacao tipoAtuacao, 
																					 final Short unfSeq){
		final StringBuilder sql = new StringBuilder(600);

		sql.append(" SELECT ")		
		   .append("   PCI.").append(MbcProcedimentoCirurgicos.Fields.DESCRICAO.name()).append(" as ").append(MbcpProcedimentoCirurgicoVO.Fields.DESCRICAO.toString())	
		   .append(" , PCI.").append(MbcProcedimentoCirurgicos.Fields.SEQ.name()).append(" as ").append(MbcpProcedimentoCirurgicoVO.Fields.SEQ.toString())
		   
		   .append(" FROM ")
		   .append("       AGH.").append(MbcProcEspPorCirurgias.class.getAnnotation(Table.class).name()).append(" PPC ")
		   .append("     , AGH.").append(MbcProcedimentoCirurgicos.class.getAnnotation(Table.class).name()).append(" PCI ")

		   .append(" WHERE 1=1 ")
		   .append("  AND PPC.").append(MbcProcEspPorCirurgias.Fields.CRG_SEQ.name()).append(" = :PRM_CRG_SEQ ")
		   .append("  AND PPC.").append(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.name()).append(" = :PRM_IND_RESP_PROC ")
		   .append("  AND PPC.").append(MbcProcEspPorCirurgias.Fields.SITUACAO.name()).append(" = :PRM_SITUACAO ")
		   .append("  AND PCI.").append(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.name()).append(" = :PRM_IND_SITUACAO ")
		   
		   .append("  AND PPC.").append(MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.name()).append(" = PCI.").append(MbcProcedimentoCirurgicos.Fields.SEQ.name())
		   
		   .append(" UNION ")
		   
		   .append(" SELECT ")		
		   .append("   PCI.").append(MbcProcedimentoCirurgicos.Fields.DESCRICAO.name()).append(" as ").append(MbcpProcedimentoCirurgicoVO.Fields.DESCRICAO.toString())	
		   .append(" , PCI.").append(MbcProcedimentoCirurgicos.Fields.SEQ.name()).append(" as ").append(MbcpProcedimentoCirurgicoVO.Fields.SEQ.toString())
		   
		   .append(" FROM ")
		   .append("       AGH.").append(MbcProcedimentoCirurgicos.class.getAnnotation(Table.class).name()).append(" PCI ")
		   .append("     , AGH.").append(MbcProcPorEquipe.class.getAnnotation(Table.class).name()).append(" PPE ")
		   .append("     , AGH.").append(MbcProfDescricoes.class.getAnnotation(Table.class).name()).append(" PFD ")

		   .append(" WHERE 1=1 ")
		   .append("  AND PFD.").append(MbcProfDescricoes.Fields.DCG_CRG_SEQ.name()).append(" = :PRM_CRG_SEQ ")
		   .append("  AND PFD.").append(MbcProfDescricoes.Fields.DCG_SEQP.name()).append(" = :PRM_DCG_SEQP ")
		   .append("  AND PFD.").append(MbcProfDescricoes.Fields.TIPO_ATUACAO.name()).append(" = :PRM_TIPO_ATUACAO ")

		   .append("  AND PPE.").append(MbcProcPorEquipe.Fields.SER_MATRICULA_PRF.name()).append(" = PFD.").append(MbcProfDescricoes.Fields.SER_MATRICULA_PROF.name())
		   .append("  AND PPE.").append(MbcProcPorEquipe.Fields.SER_VIN_CODIGO_PRF.name()).append(" = PFD.").append(MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.name())
		   
		   .append("  AND PPE.").append(MbcProcPorEquipe.Fields.UNF_SEQ.name()).append(" = :PRM_UNF_SEQ")
		   
		   .append(" AND NOT EXISTS ( SELECT EPC.").append(MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.name()).append(" FROM ")
		     			.append("       AGH.").append(MbcProcEspPorCirurgias.class.getAnnotation(Table.class).name()).append(" EPC ")
		     			.append(" WHERE 1=1 ")
		     			.append("  AND EPC.").append(MbcProcEspPorCirurgias.Fields.CRG_SEQ.name())
		     									.append(" = PFD.").append(MbcProfDescricoes.Fields.DCG_CRG_SEQ.name())
		     			.append("  AND EPC.").append(MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.name())
		     									.append(" = PPE.").append(MbcProcPorEquipe.Fields.PCI_SEQ.name())
						.append("   ) ")
		     									
		   .append("  AND PPE.").append(MbcProcPorEquipe.Fields.PCI_SEQ.name()).append(" = PCI.").append(MbcProcedimentoCirurgicos.Fields.SEQ.name())
   		   .append(" ORDER BY 1 ");
		
		
		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("PRM_CRG_SEQ", dcgCrgSeq);
		query.setParameter("PRM_IND_RESP_PROC", indRespProc.toString());
		query.setParameter("PRM_SITUACAO", situacao.toString());
		query.setParameter("PRM_IND_SITUACAO", indSituacao.toString());
		query.setShort("PRM_DCG_SEQP", Short.valueOf("1"));
		query.setParameter("PRM_TIPO_ATUACAO", tipoAtuacao.toString());
		query.setShort("PRM_UNF_SEQ", unfSeq);
		
		final List<MbcpProcedimentoCirurgicoVO> vos = query.addScalar(MbcpProcedimentoCirurgicoVO.Fields.SEQ.toString(), IntegerType.INSTANCE)
											          .addScalar(MbcpProcedimentoCirurgicoVO.Fields.DESCRICAO.toString(),StringType.INSTANCE)
											          .setResultTransformer(Transformers.aliasToBean(MbcpProcedimentoCirurgicoVO.class)).list();
		
		return vos;
	}	
	
	public MbcProcedimentoCirurgicos obterProcedimentoLancaAmbulatorio(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_LANCA_AMBULATORIO.toString(), Boolean.TRUE));
		return (MbcProcedimentoCirurgicos) executeCriteriaUniqueResult(criteria);
	}
	
	public MbcProcedimentoCirurgicos obterPorCirurgiaIndPrincipalSituacaoIndRespProcJoinComMbcProcEspPorCirurgias(Integer crgSeq, Boolean indPrincipal, DominioSituacao situacao, DominioIndRespProc indRespProc){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class, "PCI");
		criteria.createAlias(MbcProcedimentoCirurgicos.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "PPC");
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL, indPrincipal));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.SITUACAO, situacao));
		if(indRespProc != null){
			criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC, indRespProc));
		}
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ, crgSeq));
		
		return (MbcProcedimentoCirurgicos) this.executeCriteriaUniqueResult(criteria);
		
	}
	
	private DetachedCriteria obterCriteriaMbcProcedimentoCirurgicoPorTipoSituacao(
			String strPesquisa, List<DominioTipoProcedimentoCirurgico> tipos,
			DominioSituacao situacao) {
		
		DetachedCriteria criteria = criarCriteriaListMbcProcedimentoCirurgicoPorTipo(strPesquisa, tipos);
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), situacao));
		return criteria;
	}

	private DetachedCriteria obterCriteriaListarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(
			final String strPesquisa,
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, final Short especialidade) {
		
		final String aliasEPC = "epc";	//MbcEspecialidadeProcCirgs
		final String ponto = ".";
		
		DetachedCriteria criteria = obterCriteriaMbcProcedimentoCirurgicoPorTipoSituacao(strPesquisa, tipos, situacao);
		
		if(especialidade != null){
			criteria.createAlias(MbcProcedimentoCirurgicos.Fields.ESPECIALIDADES_PROCS_CIRGS.toString(), aliasEPC);
			criteria.add(Restrictions.eq(aliasEPC + ponto + MbcEspecialidadeProcCirgs.Fields.ESP_SEQ.toString(), especialidade));
		}
		
		return criteria;
	}
	
	public List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(
			final String strPesquisa, 
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, 
			final Short especialidade) {
		
		DetachedCriteria criteria = obterCriteriaListarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(strPesquisa, tipos, situacao, especialidade);
		
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long listarProcedimentoCirurgicoPorTipoSituacaoEspecialidadeCount(
			final String strPesquisa, 
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, 
			final Short especialidade) {
		
		DetachedCriteria criteria = obterCriteriaListarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(strPesquisa, tipos, situacao, especialidade);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<MbcProcedimentoCirurgicos> listarProcedimentosCirurgicosPorSeqLateralidade(Integer seq, Boolean indLadoCirurgia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_LADO_CIRURGIA.toString(), indLadoCirurgia ));

		return executeCriteria(criteria);
	}
	
	public List<ProcedimentoDaRequisicaoVO> consultarDadosProcedimentoRequisicao(final Integer ropSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.CIRURGIAS.toString(), "CRG");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "EPR");
		criteria.createAlias("EPR." + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ITENS_PROCED_HOSPITALAR.toString(), "IPH");
		criteria.createAlias("AGD." + MbcAgendas.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("AGD." + MbcAgendas.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP");
		criteria.createAlias("CSP." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CNV");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.RAP_SERVIDORES.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), ropSeq));
		
		// TODO: Fazer projecao
		
		return executeCriteria(criteria);
	}
	
}
