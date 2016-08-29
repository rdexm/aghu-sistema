package br.gov.mec.aghu.estoque.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.vo.PesquisaRequisicaoMaterialVO;
import br.gov.mec.aghu.estoque.vo.RequisicaoMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

/**
 * Classe de acesso à entidade <code>br.gov.mec.aghu.model.SceReqMateriais</code>
 * 
 * @author guilherme.finotti
 *
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class SceReqMateriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceReqMaterial> {
	
	private static final long serialVersionUID = 16021878913596813L;
	
	@Inject
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private IParametroFacade parametroFacade;
	
	@Inject
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	/**
	 * Retorna SceReqMateriais original.<br>
	 * 
	 * Devido a problema na migracao de dados/estrutura o metodo generico nao pode ser usado por enquanto.
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private SceReqMaterial obterOriginal(Integer seqReq) {
		//Integer id = elementoModificado.getSeq();
		StringBuilder hql = new StringBuilder(437);
		
		hql.append("select o.").append(SceReqMaterial.Fields.SEQ.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.SCE_REQ_MATERIAIS.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.TIPO_MOVIMENTO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.ALMOXARIFADO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.CENTRO_CUSTOS.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.GRUPO_MATERIAL.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.DATA_GERACAO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.SERVIDOR.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.IND_SITUACAO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.IND_ESTORNO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.IND_IMPRESSO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.DATA_CONFIRMACAO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.DATA_EFETIVACAO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.SERVIDOR_EFETIVADO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.DATA_ESTORNO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.SERVIDOR_ESTORNADO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.PACOTE_MATERIAL.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.DATA_CANCELAMENTO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.SERVIDOR_CANCELADO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.NOME_IMPRESSORA.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.OBSERVACAO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.RMR_PACIENTE.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.SEQ_ORIG_MATERIAL.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.IND_SIT_FATURAMENTO.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.RMR_PACIENTE_NRS.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.IND_AUTOMATICA.toString());
		hql.append(", o.").append(SceReqMaterial.Fields.ATENDIMENTO.toString());
		
		hql.append(" from ").append(SceReqMaterial.class.getSimpleName()).append(" o ");
		
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.SCE_REQ_MATERIAIS.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.TIPO_MOVIMENTO.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.ALMOXARIFADO.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.CENTRO_CUSTOS.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.GRUPO_MATERIAL.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.SERVIDOR.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.SERVIDOR_EFETIVADO.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.SERVIDOR_ESTORNADO.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.PACOTE_MATERIAL.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.SERVIDOR_CANCELADO.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.RMR_PACIENTE.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.RMR_PACIENTE_NRS.toString());
		hql.append(" left outer join o.").append(SceReqMaterial.Fields.ATENDIMENTO.toString());
		// hql.append(" left outer join o.").append(SceReqMateriais.Fields.PEDIDO_MATERIAL.toString()); 
		// TODO: A tabela SCE_PEDIDO_MATERIAIS Não foi migrada
		
		hql.append(" where o.").append(SceReqMaterial.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQueryStateless(hql.toString());
		query.setParameter("entityId", seqReq);
		
		SceReqMaterial original = null;
		@SuppressWarnings("unchecked")
		List<Object[]> camposLst = (List<Object[]>) query.list();
		
		if(camposLst != null && camposLst.size()>0) {
			
			Object[] campos = camposLst.get(0);
			
			original = new SceReqMaterial();
			
			original.setSeq(seqReq);
			original.setReqMaterial((SceReqMaterial)campos[1]);
			original.setTipoMovimento((SceTipoMovimento)campos[2]);
			original.setAlmoxarifado((SceAlmoxarifado)campos[3]);
			original.setCentroCusto((FccCentroCustos)campos[4]);
			original.setCentroCustoAplica((FccCentroCustos)campos[5]);
			original.setGrupoMaterial((ScoGrupoMaterial)campos[6]);
			original.setDtGeracao((Date)campos[7]);
			original.setServidor((RapServidores)campos[8]);
			original.setIndSituacao((DominioSituacaoRequisicaoMaterial)campos[9]);
			original.setEstorno((Boolean)campos[10]);
			original.setIndImpresso((DominioImpresso)campos[11]);
			original.setDtConfirmacao((Date)campos[12]);
			original.setServidorConfirmado((RapServidores)campos[13]);
			original.setDtEfetivacao((Date)campos[14]);
			original.setServidorEfetivado((RapServidores)campos[15]);
			original.setDtEstorno((Date)campos[16]);
			original.setServidorEstornado((RapServidores)campos[17]);
			original.setPacoteMaterial((ScePacoteMateriais)campos[18]);
			original.setDtCancelamento((Date) campos[19]);
			original.setServidorCancelado((RapServidores)campos[20]);
			original.setNomeImpressora((String)campos[21]);
			original.setObservacao((String)campos[22]);
			original.setRmrPaciente((SceRmrPaciente)campos[23]);
			original.setEslSeqOrigMat((Integer)campos[24]);
			original.setIndSitFaturamento((String)campos[25]);
			original.setRmrPacienteNrs((SceRmrPaciente)campos[26]);
			original.setAutomatica((Boolean)campos[27]);
			original.setAtendimento((AghAtendimentos)campos[28]);
	
		}

		return original;
//		return this.obterPorChavePrimaria(seqReq);
	}
	
	public SceReqMaterial buscarSceReqMateriaisPorId(Integer seq){
		if(seq == null){
			return null;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		criteria.add(Restrictions.eq(SceReqMaterial.Fields.SEQ.toString(), seq));
		return (SceReqMaterial) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Verifica se existe uma SceReqMaterial pelo código do ScoGrupoMaterial
	 * 
	 * C7 de #31584
	 * 
	 * @param gmtCodigo
	 * @return
	 */
	public boolean verificarExistenciaSceReqMateriaisPorScoGrupoMaterial(Integer gmtCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		criteria.createAlias(SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
		criteria.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), gmtCodigo));
		return super.executeCriteriaExists(criteria);
	}

	
	/**
	 * 
	 * @param filtro
	 * @return
	 */
	private DetachedCriteria basePesquisaRequisicoesMateriais(PesquisaRequisicaoMaterialVO filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		criteria.createAlias(SceReqMaterial.Fields.ALMOXARIFADO.toString(), "alm", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("alm."+SceAlmoxarifado.Fields.CCT_CODIGO.toString(), "almCC");		
		criteria.createAlias(SceReqMaterial.Fields.CENTRO_CUSTOS_REQUISICAO.toString(), "ccr", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICACAO.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), "grp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.PACOTE_MATERIAL.toString(), "pctm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR.toString(), "srv");
		criteria.createAlias("srv."+RapServidores.Fields.PESSOA_FISICA.toString(), "srvPF");
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString(), "scf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("scf."+RapServidores.Fields.PESSOA_FISICA.toString(), "scfPF", JoinType.LEFT_OUTER_JOIN);
		
		FccCentroCustos ccFipe = getComprasCadastrosBasicosFacade().obterCcAplicacaoAlteracaoRmGppg(filtro.getServidorLogado());
		Boolean possuiCaractGppg = false;
		Set<Integer> listaHierarquicaGppg = null;
		
		if (ccFipe != null) {
			listaHierarquicaGppg = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			possuiCaractGppg = (listaHierarquicaGppg != null && !listaHierarquicaGppg.isEmpty());
		}

		if (possuiCaractGppg && filtro.getCentroCustosApl() == null) {
			// Quando uma Hierarquia de Centros de Custo é informada...
			if (filtro.getCentroCustosReq() != null 
					&& filtro.getCentrosCustosRequisicaoHierarquia() != null 
					&& !filtro.getCentrosCustosRequisicaoHierarquia().isEmpty()){
				
				List<Integer> listaCentroCustosReq = new LinkedList<Integer>();
				
				// Percorre todos os Centro de Custos de Requisição através da hierarquia
				for (FccCentroCustos centroCustos : filtro.getCentrosCustosRequisicaoHierarquia() ) {
					listaCentroCustosReq.add(centroCustos.getCodigo());
				}
				
				// Acrescenta o critério de pesquisa para todos Centros de Custo de Requisição da Hierarquia
				criteria.add(
						Restrictions.or(Restrictions.in("ccr."+FccCentroCustos.Fields.CODIGO.toString(), listaCentroCustosReq), 
								Restrictions.in("cca."+FccCentroCustos.Fields.CODIGO.toString(), listaHierarquicaGppg)));
				
			} else if (filtro.getCentroCustosReq() != null){ 
				// Quando o Centro de Custo Requisição é informado individualmente a pesquisa ocorre através do mesmo
				criteria.add(
						Restrictions.or(Restrictions.eq("ccr."+FccCentroCustos.Fields.CODIGO.toString(), filtro.getCentroCustosReq().getCodigo()),
								Restrictions.in("cca."+FccCentroCustos.Fields.CODIGO.toString(), listaHierarquicaGppg)));
			}
		} else {
			// Quando uma Hierarquia de Centros de Custo é informada...
			if (filtro.getCentroCustosReq() != null 
					&& filtro.getCentrosCustosRequisicaoHierarquia() != null 
					&& !filtro.getCentrosCustosRequisicaoHierarquia().isEmpty()){
				
				List<Integer> listaCentroCustosReq = new LinkedList<Integer>();
				
				// Percorre todos os Centro de Custos de Requisição através da hierarquia
				for (FccCentroCustos centroCustos : filtro.getCentrosCustosRequisicaoHierarquia() ) {
					listaCentroCustosReq.add(centroCustos.getCodigo());
				}
				
				// Acrescenta o critério de pesquisa para todos Centros de Custo de Requisição da Hierarquia
				criteria.add(Restrictions.in("ccr."+FccCentroCustos.Fields.CODIGO.toString(), listaCentroCustosReq));
				
			} else if (filtro.getCentroCustosReq() != null){ 
				// Quando o Centro de Custo Requisição é informado individualmente a pesquisa ocorre através do mesmo
				criteria.add(Restrictions.eq("ccr."+FccCentroCustos.Fields.CODIGO.toString(), filtro.getCentroCustosReq().getCodigo()));
			}
		}
		
		this.preencheFiltros(criteria, filtro);
		
		return criteria;
	}
	
	private void preencheFiltros(DetachedCriteria criteria, PesquisaRequisicaoMaterialVO filtro) {
		if(filtro.getCentroCustosApl() != null){
			criteria.add(Restrictions.eq("cca."+FccCentroCustos.Fields.CODIGO.toString(), filtro.getCentroCustosApl().getCodigo()));
		}

		if(filtro.getGrupoMaterial() != null){
			criteria.add(Restrictions.eq("grp."+ScoGrupoMaterial.Fields.CODIGO.toString(), filtro.getGrupoMaterial().getCodigo()));
		}
		
		if(filtro.getIndAutomatica() != null) {
             if (DominioSimNao.S.equals(filtro.getIndAutomatica())) {
                     criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_AUTOMATICA.toString(), Boolean.TRUE));                                
             } else {
                     criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_AUTOMATICA.toString(), Boolean.FALSE));
             }
		}
	
		if(filtro.getNumRM() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.SEQ.toString(), filtro.getNumRM()));
		}

		if(filtro.getAlmoxarifado() != null){
			criteria.add(Restrictions.eq("alm."+SceAlmoxarifado.Fields.SEQ.toString(), filtro.getAlmoxarifado().getSeq()));
		}
	}

	/**
	 * 
	 * @param filtro
	 * @return
	 */
	public Long pesquisaRequisicoesMateriaisCount(PesquisaRequisicaoMaterialVO filtro){
		DetachedCriteria criteria = basePesquisaRequisicoesMateriais(filtro);

		criteria.add(Restrictions.ne(SceReqMaterial.Fields.SITUACAO.toString(), DominioSituacaoRequisicaoMaterial.E));
		criteria.add(Restrictions.ne(SceReqMaterial.Fields.SITUACAO.toString(), DominioSituacaoRequisicaoMaterial.A));

		if(filtro.getIndSituacao() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.SITUACAO.toString(), filtro.getIndSituacao()));
		}

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	public List<SceReqMaterial> pesquisaRequisicoesMateriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PesquisaRequisicaoMaterialVO filtro){
		DetachedCriteria criteria = basePesquisaRequisicoesMateriais(filtro);

		criteria.add(Restrictions.ne(SceReqMaterial.Fields.SITUACAO.toString(), DominioSituacaoRequisicaoMaterial.A));
		criteria.add(Restrictions.ne(SceReqMaterial.Fields.SITUACAO.toString(), DominioSituacaoRequisicaoMaterial.E));

		if(filtro.getIndSituacao() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.SITUACAO.toString(), filtro.getIndSituacao()));
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	


	public List<SceReqMaterial> pesquisaRequisicoesMateriaisEstornar(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceReqMaterial sceReqMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		
		basePesquisaRequisicoesMateriaisEstornar(sceReqMateriais, criteria);
		
		criteria.setFetchMode(SceReqMaterial.Fields.ALMOXARIFADO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICACAO.toString(), FetchMode.JOIN);
		
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString(), "servConf", JoinType.LEFT_OUTER_JOIN );
		criteria.createAlias("servConf."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesFis", JoinType.LEFT_OUTER_JOIN );
		
		criteria.addOrder(Order.desc(SceReqMaterial.Fields.SEQ.toString()) );
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	


	public Long pesquisaRequisicoesMateriaisEstornarCount(
			SceReqMaterial sceReqMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		basePesquisaRequisicoesMateriaisEstornar(sceReqMateriais,criteria);
		
		return executeCriteriaCount(criteria);
	}

	private void basePesquisaRequisicoesMateriaisEstornar(
			SceReqMaterial sceReqMateriais, DetachedCriteria criteria) {
		if(sceReqMateriais.getSeq() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.SEQ.toString(), sceReqMateriais.getSeq()));
		}

		if(sceReqMateriais.getAlmoxarifado() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.ALMOXARIFADO.toString(), sceReqMateriais.getAlmoxarifado()));
		}

		if(sceReqMateriais.getCentroCusto() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), sceReqMateriais.getCentroCusto()));
		}

		if(sceReqMateriais.getCentroCustoAplica() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), sceReqMateriais.getCentroCustoAplica()));
		}
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		if(sceReqMateriais.getDtGeracao() != null){
			criteria.add(Restrictions.sqlRestriction("To_char(" + "DT_GERACAO" + ",'dd/mm/yyyy') = ?",formatador.format(sceReqMateriais.getDtGeracao()),
					StringType.INSTANCE));
		}
		
		if(sceReqMateriais.getDtConfirmacao() != null){
			criteria.add(Restrictions.sqlRestriction("To_char(" + "DT_CONFIRMACAO" + ",'dd/mm/yyyy') = ?",formatador.format(sceReqMateriais.getDtConfirmacao()),
					StringType.INSTANCE));
		}
		
		criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_SITUACAO.toString(), DominioSituacaoRequisicaoMaterial.E));
		criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
	}
	
	
	/**
	 * 
	 * @return
	 */
	public DetachedCriteria obterCriteriagetMateriaisefetivar(PesquisaRequisicaoMaterialVO filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		criteria.createCriteria(SceReqMaterial.Fields.ALMOXARIFADO.toString(), "alm", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(SceReqMaterial.Fields.CENTRO_CUSTOS_REQUISICAO.toString(), "ccr", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICACAO.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), "grp", JoinType.LEFT_OUTER_JOIN);

		if(filtro.getNumRM() != null){
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.SEQ.toString(), filtro.getNumRM()));
		}

		if(filtro.getAlmoxarifado() != null){
			criteria.add(Restrictions.eq("alm."+SceAlmoxarifado.Fields.SEQ.toString(), filtro.getAlmoxarifado().getSeq()));
		}

		// Quando uma Hierarquia de Centros de Custo é informada...
		if (filtro.getCentroCustosReq() != null 
				&& filtro.getCentrosCustosRequisicaoHierarquia() != null 
				&& !filtro.getCentrosCustosRequisicaoHierarquia().isEmpty()){
			
			List<Integer> listaCentroCustosReq = new LinkedList<Integer>();
			
			// Percorre todos os Centro de Custos de Requisição através da hierarquia
			for (FccCentroCustos centroCustos : filtro.getCentrosCustosRequisicaoHierarquia() ) {
				listaCentroCustosReq.add(centroCustos.getCodigo());
			}
			
			// Acrescenta o critério de pesquisa para todos Centros de Custo de Requisição da Hierarquia
			criteria.add(Restrictions.in("ccr."+FccCentroCustos.Fields.CODIGO.toString(), listaCentroCustosReq));
			
		} else if (filtro.getCentroCustosReq() != null){ 
			// Quando o Centro de Custo Requisição é informado individualmente a pesquisa ocorre através do mesmo
			criteria.add(Restrictions.eq("ccr."+FccCentroCustos.Fields.CODIGO.toString(), filtro.getCentroCustosReq().getCodigo()));
		}

		if(filtro.getCentroCustosApl() != null){
			criteria.add(Restrictions.eq("cca."+FccCentroCustos.Fields.CODIGO.toString(), filtro.getCentroCustosApl().getCodigo()));
		}

		if(filtro.getGrupoMaterial() != null){
			criteria.add(Restrictions.eq("grp."+ScoGrupoMaterial.Fields.CODIGO.toString(), filtro.getGrupoMaterial().getCodigo()));
		}
		
		this.preencherFiltrosEfetivar(criteria, filtro);
		
		return criteria;
	}
	
	private void preencherFiltrosEfetivar(DetachedCriteria criteria, PesquisaRequisicaoMaterialVO filtro) {
		if(filtro.getIndAutomatica() != null) {
            if (DominioSimNao.S.equals(filtro.getIndAutomatica())) {
                    criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_AUTOMATICA.toString(), Boolean.TRUE));                                
            } else {
                    criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_AUTOMATICA.toString(), Boolean.FALSE));
            }
		}

		if (filtro.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(SceReqMaterial.Fields.SITUACAO.toString(), filtro.getIndSituacao()));
		} else {
			criteria.add(Restrictions.in(SceReqMaterial.Fields.IND_SITUACAO.toString(), new DominioSituacaoRequisicaoMaterial[]{DominioSituacaoRequisicaoMaterial.E,DominioSituacaoRequisicaoMaterial.C}));
		}
		
		if (filtro.getIndEstorno() != null) {
			if (filtro.getIndEstorno().equals(DominioSimNao.S)) {
				criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_ESTORNO.toString(), Boolean.TRUE));
			} else {
				criteria.add(Restrictions.eq(SceReqMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
			}
		}
	}
	
	/**
	 * 
	 * @param filtro
	 * @return
	 */
	public Long pesquisaRequisicoesMateriaisEfetivarCount(PesquisaRequisicaoMaterialVO filtro){
		return executeCriteriaCount(this.obterCriteriagetMateriaisefetivar(filtro));
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	public List<SceReqMaterial> pesquisaRequisicoesMateriaisefetivar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PesquisaRequisicaoMaterialVO filtro){
		DetachedCriteria criteria = this.obterCriteriagetMateriaisefetivar(filtro);
		criteria.addOrder(Order.desc(SceReqMaterial.Fields.SEQ.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * 
	 * @param sceReqMateriais
	 * @return
	 */
	public List<SceReqMaterial> pesquisaRequisicoesMateriaisPorRequisicaoMaterial(SceReqMaterial sceReqMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		criteria.add(Restrictions.eq(SceReqMaterial.Fields.SCE_REQ_MATERIAIS.toString(), sceReqMateriais));
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca SceReqMateriais através do id e almoxarifado
	 * @param seqReqMaterial
	 * @param seqSceAlmoxarifado
	 * @return
	 */
	public SceReqMaterial buscarSceReqMateriaisPorAlmoxarifado(Integer seq, Short seqAlmoxarifado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);
		criteria.createAlias(SceReqMaterial.Fields.ALMOXARIFADO.toString(), "alm");
		
		criteria.add(Restrictions.eq(SceReqMaterial.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("alm."+SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifado));
		
		return (SceReqMaterial) executeCriteriaUniqueResult(criteria);
		
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public RequisicaoMaterialVO buscaMateriaisItensImprimir(Integer reqMat) throws BaseException{

		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);

		criteria.createAlias(SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), "cc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.ALMOXARIFADO.toString(), "alm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.TIPO_MOVIMENTO.toString(), "tmv", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR.toString(), "rap", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rap."+RapServidores.Fields.RAMAL_TELEFONICO.toString(), "ram", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rap."+RapServidores.Fields.PESSOA_FISICA.toString(), "pfi", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createCriteria(SceReqMaterial.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
			.add(Projections.property(SceReqMaterial.Fields.SEQ.toString()))
			//.add(Projections.property(SceReqMateriais.Fields.ALMOXARIFADO.toString()))
			.add(Projections.property(SceReqMaterial.Fields.IND_SITUACAO.toString()))
			.add(Projections.property(SceReqMaterial.Fields.DATA_GERACAO.toString()))
			.add(Projections.property(SceReqMaterial.Fields.DATA_CONFIRMACAO.toString()))
			.add(Projections.property(SceReqMaterial.Fields.DATA_EFETIVACAO.toString()))

			.add(Projections.property("cc."+FccCentroCustos.Fields.CODIGO.toString()))
			.add(Projections.property("cc."+FccCentroCustos.Fields.NOME_REDUZIDO.toString()))
			.add(Projections.property("cc."+FccCentroCustos.Fields.DESCRICAO.toString()))

			.add(Projections.property("cca."+FccCentroCustos.Fields.CODIGO.toString()))
			.add(Projections.property("cca."+FccCentroCustos.Fields.NOME_REDUZIDO.toString()))
			.add(Projections.property("cca."+FccCentroCustos.Fields.DESCRICAO.toString()))//10

			.add(Projections.property("tmv."+SceTipoMovimento.Fields.SEQ.toString()))
			.add(Projections.property("tmv."+SceTipoMovimento.Fields.COMPLEMENTO.toString()))
			.add(Projections.property("pfi."+RapPessoasFisicas.Fields.NOME.toString()))
			.add(Projections.property("ram."+RapRamalTelefonico.Fields.NUMERORAMAL.toString()))

			.add(Projections.property("alm."+SceAlmoxarifado.Fields.SEQ.toString()))
			.add(Projections.property("alm."+SceAlmoxarifado.Fields.DESCRICAO.toString()))
			.add(Projections.property(SceReqMaterial.Fields.OBSERVACAO.toString()))
			
			.add(Projections.property("pac."+AipPacientes.Fields.NOME.toString()))
			.add(Projections.property("pac."+AipPacientes.Fields.PRONTUARIO.toString()))
		));

		Criterion cri1 = Restrictions.eq(SceReqMaterial.Fields.SEQ.toString(), reqMat);
		Criterion cri2 = Restrictions.eq(SceReqMaterial.Fields.SCE_REQ_MATERIAIS_SEQ.toString(), reqMat);

		criteria.add(Restrictions.or(cri1, cri2));

		List<Object[]> resultList = executeCriteria(criteria);
		if(resultList==null || resultList.size()==0){
			return null;
		}

		/*Results*/
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Object[] objects = resultList.get(0);

		RequisicaoMaterialVO objReturn = new RequisicaoMaterialVO();

		objReturn.setReqMaterial(Integer.parseInt(objects[0].toString()));
		objReturn.setSituacao(((DominioSituacaoRequisicaoMaterial)objects[1]).getDescricao());
		objReturn.setDataGeracao(sdf.format((Date)objects[2]));

		if(objects[3] != null){
			objReturn.setDataConfirmacao(sdf.format((Date)objects[3]));
		}

		if(objects[4] != null){
			objReturn.setDataEfetivacao(sdf.format((Date)objects[4]));
		}

		if(objects[5] != null){
			objReturn.setCentroCustoCodigo(Integer.parseInt(objects[5].toString()));
		}
		
		if(objects[7]==null || objects[7].toString().isEmpty()){
			objReturn.setCentroCustoDescricao(objects[6].toString());	
		}else{
			objReturn.setCentroCustoDescricao(objects[7].toString());
		}					

		if(objects[8] != null){
			objReturn.setCentroCustoAplicacaoCodigo(Integer.parseInt(objects[8].toString()));
		}
		
		if(objects[10]==null || objects[10].toString().isEmpty()){
			objReturn.setCentroCustoAplicacaoDescricao(objects[9].toString());	
		}else{
			objReturn.setCentroCustoAplicacaoDescricao(objects[10].toString());
		}				

		if(objects[11] != null){
			objReturn.setTipoMovimentoSeq(Short.parseShort(objects[11].toString()));
		}
		
		objReturn.setTipoMovimentoComplemento(objects[12].toString());

		objReturn.setNomePessoa(objects[13].toString());
		
		if(objects[14] != null){
			objReturn.setNumeroRamal(Integer.parseInt(objects[14].toString()));
		}

		if(objects[15] != null){
			objReturn.setAlmoxSeq(Short.parseShort(objects[15].toString()));
		}

		objReturn.setAlmoxDesc(objects[16].toString());
		
		if(objects[17] != null){
			objReturn.setObservacao(objects[17].toString());
		}
		
		if(objects[18] != null){
			objReturn.setPaciente(objects[18].toString());
		}
		
		if(objects[19] != null){
			objReturn.setProntuario(objects[19].toString());
		}


		return objReturn;
	}

	/**
	 * Retorna a quantidade de Requisição de Materiais dependentes do pacote de materiais
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 */
	public Long obterQuantidadeRequisicaoMateriaisPorCentrosCustosNumeroPacoteMaterial(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class, "REQ");
		
		criteria.add(Restrictions.eq("REQ." + SceReqMaterial.Fields.PACOTE_MATERIAL.toString() + "." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO,
				codigoCentroCustoProprietario));
		
		criteria.add(Restrictions.eq("REQ." + SceReqMaterial.Fields.PACOTE_MATERIAL.toString() + "." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO,
				codigoCentroCustoAplicacao));
		
		criteria.add(Restrictions.eq("REQ." + SceReqMaterial.Fields.PACOTE_MATERIAL.toString() + "." + ScePacoteMateriais.Fields.NUMERO,
				numeroPacote));
		
		return executeCriteriaCount(criteria);
		
	}

	/**
	 * 
	 * Realiza a pesquisa de requisições de materiais da Consulta Geral
	 *
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nroRM
	 * @param dtGeracao
	 * @param dtConfirmacao
	 * @param dtEfetivacao
	 * @param indSituacao
	 * @param indEstorno
	 * @param automatica
	 * @param nomeImpressora
	 * @param almSeq
	 * @param ccReq
	 * @param ccApli
	 * @param impresso
	 * @return
	 */
	public List<SceReqMaterial> pesquisarRequisicaoMaterialConsultaGeral(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer nroRM, Date dtGeracao,
			Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao,
			Boolean indEstorno, Boolean automatica, String nomeImpressora,
			Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso) {

		DetachedCriteria criteria = pesquisarCriteriaPesquisarRequisicaoMaterialGeral(nroRM, dtGeracao,
				dtConfirmacao, dtEfetivacao, indSituacao, indEstorno, automatica,
				nomeImpressora, almSeq, ccReq, ccApli,
				impresso, Boolean.TRUE);    

		criteria.addOrder(Order.asc(SceReqMaterial.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	/**
	 * Realiza a pesquisa de requisições de materiais da Consulta de RM
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nroRM
	 * @param dtGeracao
	 * @param dtConfirmacao
	 * @param dtEfetivacao
	 * @param indSituacao
	 * @param indEstorno
	 * @param nomeImpressora
	 * @param almSeq
	 * @param ccReq
	 * @param ccApli
	 * @param impresso
	 * @return
	 */
	public List<SceReqMaterial> pesquisarRequisicaoMaterialConsultaRM(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer nroRM, Date dtGeracao,
			Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao,
			Boolean indEstorno, String nomeImpressora,
			Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso,
			List<FccCentroCustos> hierarquiaCc, List<SceAlmoxarifado> listaAlmox,
			List<DominioSituacaoRequisicaoMaterial> listaSituacao) {

		DetachedCriteria criteria = pesquisarCriteriaPesquisarRequisicaoMaterialGeral(nroRM, dtGeracao,
				dtConfirmacao, dtEfetivacao, indSituacao, indEstorno, null,
				nomeImpressora, almSeq, ccReq, ccApli,
				impresso, Boolean.FALSE);    
		
		if (hierarquiaCc != null && !hierarquiaCc.isEmpty()) {
			criteria.add(Restrictions.in("REQ." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(),
					hierarquiaCc));			
		}

		if (listaAlmox != null && !listaAlmox.isEmpty()) {
			criteria.add(Restrictions.in("REQ." + SceReqMaterial.Fields.ALMOXARIFADO.toString(),
					listaAlmox));
		}
		
	/*	if (listaSituacao != null && !listaSituacao.isEmpty()) {
			criteria.add(Restrictions.in("REQ." + SceReqMaterial.Fields.SITUACAO.toString(),
					listaSituacao));
		}*/

		if(DominioSituacaoRequisicaoMaterial.G.equals(indSituacao)){
			criteria.addOrder(Order.asc(SceReqMaterial.Fields.DATA_GERACAO.toString()));
		}else if(DominioSituacaoRequisicaoMaterial.C.equals(indSituacao)){
			criteria.addOrder(Order.asc(SceReqMaterial.Fields.DATA_CONFIRMACAO.toString()));
		}
		criteria.addOrder(Order.asc(SceReqMaterial.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	

	/**
	 * 
	 * Retorna a quantidade de requisições encontradas na Consulta Geral
	 *
	 * @param nroRM
	 * @param dtGeracao
	 * @param dtConfirmacao
	 * @param dtEfetivacao
	 * @param indSituacao
	 * @param indEstorno
	 * @param automatica
	 * @param nomeImpressora
	 * @param almSeq
	 * @param ccReq
	 * @param ccApli
	 * @param impresso
	 * @return
	 */
	public Long pesquisarRequisicaoMaterialConsultaGeralCount(Integer nroRM, Date dtGeracao,
			Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao,
			Boolean indEstorno, Boolean automatica, String nomeImpressora,
			Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso) {

		DetachedCriteria criteria = pesquisarCriteriaPesquisarRequisicaoMaterialGeral(
				nroRM, dtGeracao, dtConfirmacao, dtEfetivacao, indSituacao, indEstorno, automatica,
				nomeImpressora, almSeq, ccReq, ccApli, impresso, Boolean.TRUE);

		return this.executeCriteriaCount(criteria);
	}
	
	
	/**
	 * 
	 * Retorna a quantidade de requisições encontradas na Consulta de RM
	 *
	 * @param nroRM
	 * @param dtGeracao
	 * @param dtConfirmacao
	 * @param dtEfetivacao
	 * @param indSituacao
	 * @param indEstorno
	 * @param nomeImpressora
	 * @param almSeq
	 * @param ccReq
	 * @param ccApli
	 * @param impresso
	 * @return
	 */
	public Long pesquisarRequisicaoMaterialConsultaRMCount(Integer nroRM, Date dtGeracao,
			Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao,
			Boolean indEstorno, String nomeImpressora,
			Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso,
			List<FccCentroCustos> hierarquiaCc, List<SceAlmoxarifado> listaAlmox,
			List<DominioSituacaoRequisicaoMaterial> listaSituacao) {

		DetachedCriteria criteria = pesquisarCriteriaPesquisarRequisicaoMaterialGeral(
				nroRM, dtGeracao, dtConfirmacao, dtEfetivacao, indSituacao, indEstorno, null,
				nomeImpressora, almSeq, ccReq, ccApli, impresso, Boolean.FALSE);

		if (hierarquiaCc != null && !hierarquiaCc.isEmpty()) {
			criteria.add(Restrictions.in("REQ." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(),
					hierarquiaCc));			
		}
		
		if (listaAlmox != null && !listaAlmox.isEmpty()) {
			criteria.add(Restrictions.in("REQ." + SceReqMaterial.Fields.ALMOXARIFADO.toString(),
					listaAlmox));
		}
		
		if (listaSituacao != null && !listaSituacao.isEmpty()) {
			criteria.add(Restrictions.in("REQ." + SceReqMaterial.Fields.SITUACAO.toString(),
					listaSituacao));
		}
		
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna a critéria para a consulta geral de requisições de materiais
	 * 
	 * @param nroRM
	 * @param dtGeracao
	 * @param dtConfirmacao
	 * @param dtEfetivacao
	 * @param indSituacao
	 * @param indEstorno
	 * @param automatica
	 * @param nomeImpressora
	 * @param almSeq
	 * @param ccReq
	 * @param ccApli
	 * @param impresso
	 * @param isDataConsultaGeral
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private DetachedCriteria pesquisarCriteriaPesquisarRequisicaoMaterialGeral(
			Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, Boolean automatica,
			String nomeImpressora, Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso, Boolean isDataConsultaGeral) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				SceReqMaterial.class, "REQ");
		criteria.createAlias("REQ." + SceReqMaterial.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.INNER_JOIN);
		criteria.createAlias("REQ." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), "CCR", JoinType.INNER_JOIN);
		criteria.createAlias("REQ." + SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), "CCA", JoinType.INNER_JOIN);
		criteria.createAlias("REQ." + SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), "GRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("REQ." + SceReqMaterial.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("REQ." + SceReqMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);
		criteria.createAlias("REQ." + SceReqMaterial.Fields.ATENDIMENTO.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.SEQ.toString()),
				SceReqMaterial.Fields.SEQ.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.SITUACAO.toString()),
				SceReqMaterial.Fields.SITUACAO.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.DATA_GERACAO.toString()),
				SceReqMaterial.Fields.DATA_GERACAO.toString());
		p.add(Projections.property("ALM."
				+ SceAlmoxarifado.Fields.SEQ.toString()),
				SceReqMaterial.Fields.SEQ_ALMOXARIFADO.toString());
		p.add(Projections.property("ALM."
				+ SceAlmoxarifado.Fields.DESCRICAO.toString()),
				SceReqMaterial.Fields.DESCRICAO_ALMOXARIFADO.toString());
		p.add(Projections.property("CCR."
				+ FccCentroCustos.Fields.CODIGO.toString()),
				SceReqMaterial.Fields.CODIGO_CENTRO_CUSTO_REQUISITANTE.toString());
		p.add(Projections.property("CCR."
				+ FccCentroCustos.Fields.DESCRICAO.toString()),
				SceReqMaterial.Fields.DESCRICAO_CENTRO_CUSTO_REQUISITANTE.toString());
		p.add(Projections.property("CCA."
				+ FccCentroCustos.Fields.CODIGO.toString()),
				SceReqMaterial.Fields.CODIGO_CENTRO_CUSTO_APLICACAO.toString());
		p.add(Projections.property("CCA."
				+ FccCentroCustos.Fields.DESCRICAO.toString()),
				SceReqMaterial.Fields.DESCRICAO_CENTRO_CUSTO_APLICACAO.toString());
		p.add(Projections.property("GRP."
				+ ScoGrupoMaterial.Fields.CODIGO.toString()),
				SceReqMaterial.Fields.CODIGO_GRUPO_MATERIAL.toString());
		p.add(Projections.property("GRP."
				+ ScoGrupoMaterial.Fields.DESCRICAO.toString()),
				SceReqMaterial.Fields.DESCRICAO_GRUPO_MATERIAL.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.IND_ESTORNO.toString()),
				SceReqMaterial.Fields.IND_ESTORNO.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.NOME_IMPRESSORA.toString()),
				SceReqMaterial.Fields.NOME_IMPRESSORA.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.OBSERVACAO.toString()),
				SceReqMaterial.Fields.OBSERVACAO.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.DATA_CONFIRMACAO.toString()),
				SceReqMaterial.Fields.DATA_CONFIRMACAO.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.DATA_EFETIVACAO.toString()),
				SceReqMaterial.Fields.DATA_EFETIVACAO.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.IND_IMPRESSO.toString()),
				SceReqMaterial.Fields.IND_IMPRESSO.toString());
		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.IND_AUTOMATICA.toString()),
				SceReqMaterial.Fields.IND_AUTOMATICA.toString());
		p.add(Projections.property("TMV."
				+ SceTipoMovimento.Fields.SEQ.toString()),
				SceReqMaterial.Fields.TMV_SEQ.toString());
		p.add(Projections.property("TMV."
				+ SceTipoMovimento.Fields.COMPLEMENTO.toString()),
				SceReqMaterial.Fields.TMV_COMPLEMENTO.toString());
		p.add(Projections.property("ATD."
				+ AghAtendimentos.Fields.SEQ.toString()),
				SceReqMaterial.Fields.SEQ_ATENDIMENTO.toString());
		p.add(Projections.property("SER."
				+ RapServidores.Fields.MATRICULA.toString()),
				SceReqMaterial.Fields.MATRICULA.toString());
		p.add(Projections.property("SER."
				+ RapServidores.Fields.VIN_CODIGO.toString()),
				SceReqMaterial.Fields.VIN_CODIGO.toString());
		
		criteria.setProjection(p);
		
		if (nroRM != null) {
			criteria.add(Restrictions.eq("REQ."
					+ SceReqMaterial.Fields.SEQ.toString(), nroRM));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq("REQ."
					+ SceReqMaterial.Fields.SITUACAO.toString(), indSituacao));
		}

		if (almSeq != null) {
			criteria.add(Restrictions.eq("ALM."
					+ SceAlmoxarifado.Fields.SEQ.toString(),
					almSeq));
		}
		
		if (ccReq != null) {
			criteria.add(Restrictions.eq("CCR."
					+ FccCentroCustos.Fields.CODIGO.toString(),
					ccReq));
		}
		
		if (ccApli != null) {
			criteria.add(Restrictions.eq("REQ."+SceReqMaterial.Fields.COD_CC_APLIC.toString(), ccApli));
		}
		
		if (indEstorno != null) {
			criteria.add(Restrictions.eq(
					"REQ." + SceReqMaterial.Fields.IND_ESTORNO.toString(),
					indEstorno));
		}
		
		if (automatica != null) {
			criteria.add(Restrictions.eq(
					"REQ." + SceReqMaterial.Fields.IND_AUTOMATICA.toString(),
					automatica));
		}
		if (nomeImpressora != null && !"".equals(nomeImpressora)) {
			criteria.add(Restrictions.like(
					"REQ." + SceReqMaterial.Fields.NOME_IMPRESSORA.toString(),
					nomeImpressora.replaceAll("\\\\","%"), MatchMode.ANYWHERE));
		}

		if (dtGeracao != null) {

			Date dataAtual = new Date();
			Criterion regraA = Restrictions.between("REQ."
					+ SceReqMaterial.Fields.DATA_GERACAO.toString(),
					DateUtil.truncaData(dtGeracao), dataAtual);
			SimpleDateFormat in= new SimpleDateFormat("yyyy-MM-dd");  
			   
		    String dataA = in.format(dtGeracao); 
		    String sqlRestriction = "";
			
			if (!isOracle()) {
				sqlRestriction = ("DATE(this_.dt_geracao) >= '"+ dataA + "'");
			} else {
				sqlRestriction = ("TRUNC(this_.dt_geracao) >= TO_DATE('"+ dataA + "', 'YYYY-MM-DD')");
			}
			
			if(isDataConsultaGeral){
				criteria.add(regraA);
			}else{
				criteria.add(Restrictions.sqlRestriction(sqlRestriction));
			}
		}
		if (dtConfirmacao != null) {

			Criterion regraA = Restrictions.between("REQ." + SceReqMaterial.Fields.DATA_CONFIRMACAO.toString(),
												    DateUtil.truncaData(dtConfirmacao), 
												    DateUtil.adicionaDias(DateUtil.obterDataComHoraFinal(dtConfirmacao),1));
			
			Criterion regraB = Restrictions.between("REQ." + SceReqMaterial.Fields.DATA_CONFIRMACAO.toString(),
				    							    DateUtil.truncaData(dtConfirmacao), DateUtil.obterDataComHoraFinal(dtConfirmacao));
			
			if(isDataConsultaGeral){
				criteria.add(regraA);
			}else{
				criteria.add(regraB);
			}

		}
		if (dtEfetivacao != null) {
			Criterion regraA = Restrictions.between("REQ." + SceReqMaterial.Fields.DATA_EFETIVACAO.toString(),
												    DateUtil.truncaData(dtEfetivacao), 
												    DateUtil.adicionaDias(DateUtil.obterDataComHoraFinal(dtEfetivacao),1));
			
			Criterion regraB = Restrictions.between("REQ." + SceReqMaterial.Fields.DATA_EFETIVACAO.toString(),
				    								DateUtil.truncaData(dtEfetivacao), DateUtil.obterDataComHoraFinal(dtEfetivacao));
			
			if(isDataConsultaGeral){
				criteria.add(regraA);
			}else{
				criteria.add(regraB);
			}
			
		}
		if (impresso != null) {
			criteria.add(Restrictions.eq("REQ."	+ SceReqMaterial.Fields.IND_IMPRESSO.toString(), impresso));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(SceReqMaterial.class));
		return criteria;
	}
	
	/**
	 * Pesquisa nomes das impressoras das requisições de materiais
	 * @param parametro
	 * @return
	 */
	public List<LinhaReportVO> pesquisarNomesImpressorasRequisicaoMaterial(String parametro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(
				SceReqMaterial.class, "REQ");

		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("REQ."
				+ SceReqMaterial.Fields.NOME_IMPRESSORA.toString()),
				LinhaReportVO.Fields.TEXTO1.toString());
		
		criteria.setProjection(Projections.distinct(p));
		
		if(parametro!=null && !"".equals(parametro)){
			criteria.add(Restrictions.ilike("REQ." + SceReqMaterial.Fields.NOME_IMPRESSORA.toString(), parametro, MatchMode.ANYWHERE));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		criteria.addOrder(Order.asc(SceReqMaterial.Fields.NOME_IMPRESSORA.toString()));		
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public SceReqMaterial obterRequicaoMaterialUnidadeFuncional(Short unidadeFuncionalSeq, Integer atendimentoSeq, FccCentroCustos centroCustoAtendimento){
		Date data = new Date();
		
		Date dataInicioFormatada = DateUtil.truncaData(data);
		Date dataFimFormatada = DateUtil.truncaData(DateUtil.adicionaDias(data, 1));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class, "RMS");
		
		criteria.createAlias("RMS." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), "CC");
		criteria.createAlias("CC." + FccCentroCustos.Fields.UNIDADES_FUNCIONAIS.toString(), "UNF");
		
		criteria.add(Restrictions.eq("UNF."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeFuncionalSeq));
		criteria.add(Restrictions.eq("RMS."+SceReqMaterial.Fields.ATENDIMENTO_SEQ.toString(), atendimentoSeq));
		
		if(centroCustoAtendimento!=null) {
			criteria.add(Restrictions.eq("RMS."+SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), centroCustoAtendimento));
		}
		
		criteria.add(Restrictions.eq("RMS."+SceReqMaterial.Fields.IND_SITUACAO.toString(), DominioSituacaoRequisicaoMaterial.G));
		criteria.add(Restrictions.between("RMS."+SceReqMaterial.Fields.DATA_GERACAO.toString(), dataInicioFormatada, dataFimFormatada));
		criteria.add(Restrictions.eq("RMS."+SceReqMaterial.Fields.IND_AUTOMATICA.toString(), Boolean.TRUE));
		
		return (SceReqMaterial)executeCriteriaUniqueResult(criteria);
	}
	
	public List<SceReqMaterial> buscaRequisicaoMaterialEfetivacaoAutomatica(){
		DominioSituacaoRequisicaoMaterial[] listaDominioNaoAutomatico = 
			new DominioSituacaoRequisicaoMaterial[] {DominioSituacaoRequisicaoMaterial.A, DominioSituacaoRequisicaoMaterial.E};
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class, "RMS");
		
		criteria.add(Restrictions.not(Restrictions.in("RMS."+SceReqMaterial.Fields.IND_SITUACAO.toString(), listaDominioNaoAutomatico)));
		criteria.add(Restrictions.and(
				Restrictions.isNotNull("RMS."+SceReqMaterial.Fields.IND_AUTOMATICA.toString()),
				Restrictions.eq("RMS."+SceReqMaterial.Fields.IND_AUTOMATICA.toString(), Boolean.TRUE))
				);
		
		return this.executeCriteria(criteria);
	}

	@Override
	public SceReqMaterial obterOriginal(SceReqMaterial elemento) {
		SceReqMaterial returnValue = null;
		
		if (elemento != null && elemento.getSeq() != null) {
			returnValue = this.obterOriginal(elemento.getSeq());
		}
		
		return returnValue;
	}
	
	public void unirTransacao(){
		joinTransaction();
	}
	
	public List<SceReqMaterial> pesquisarRequisicaoCompleta(SceReqMaterial reqMaterial, int firstResult, int maxResults, String orderProperty, boolean asc, Date dataLimite, Boolean dtLimiteInferior,ScoMaterial material){
	
		DetachedCriteria criteria = montarCriteriaCompleta(reqMaterial, dataLimite, dtLimiteInferior,material);
				
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarRequisicaoCompletaCount(SceReqMaterial reqMaterial,Date dataLimite, Boolean dtLimiteInferior,ScoMaterial material){
		
		DetachedCriteria criteria = montarCriteriaCompleta(reqMaterial, dataLimite, dtLimiteInferior,material);
		
		return this.executeCriteriaCount(criteria);
	}

	
	/**
	 * Monta criteria para pesquisa por todos os campos de requisição de material. <br> 
	 * Os campos de datas são pesquisados levando em consideração os parametros dataLimite e dtLimiteInferior.<BR>
	 * Se dataLimite estiver preenchido, dtLimiteInferior também deve ser. Se dtLimiteInferior = true, então a pesquisa será realizada entre
	 * a dataLimite informada e o valor no campo do objeto, se dtLimiteInferior = false, então a pesquisa será realizada entre o valor do campo
	 * no objeto e a dataLimite informada.<br>
	 * Se dataLimite não for informada (nula), então a pesquisa buscará somente no dia informado no campo de cada data.
	 * @param reqMaterial
	 * @param dataLimite
	 * @param dtLimiteInferior
	 * @return
	 * @author bruno.mourao
	 * @since 24/04/2012
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria montarCriteriaCompleta(SceReqMaterial reqMaterial, Date dataLimite, Boolean dtLimiteInferior,ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class, "RMS");
		
		criteria.createAlias("RMS." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RMS." + SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RMS." + SceReqMaterial.Fields.ALMOXARIFADO.toString(), SceReqMaterial.Fields.ALMOXARIFADO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("RMS." + SceReqMaterial.Fields.ITENS_REQUISICAO_MATERIAL.toString(), "ITM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ITM." + SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString(), "ETM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ETM." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		
		
		
		if(material != null && material.getCodigo() != null){
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		}
		if(reqMaterial.getAutomatica() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.IND_AUTOMATICA.toString(), reqMaterial.getAutomatica()));
		}
		if(reqMaterial.getEstorno() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.IND_ESTORNO.toString(), reqMaterial.getEstorno()));
		}
		if(reqMaterial.getAlmoxarifado() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.ALMOXARIFADO.toString(), reqMaterial.getAlmoxarifado()));
		}
		if(reqMaterial.getAtendimento() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.ATENDIMENTO.toString(), reqMaterial.getAtendimento()));
		}
		if(reqMaterial.getCentroCusto() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), reqMaterial.getCentroCusto()));
		}
		if(reqMaterial.getCentroCustoAplica() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), reqMaterial.getCentroCustoAplica()));
		}
		if(reqMaterial.getDtCancelamento() != null){
			String propertyName = "RMS." + SceReqMaterial.Fields.DATA_CANCELAMENTO.toString();
			criteria.add(obterRestrictionData(propertyName,reqMaterial.getDtCancelamento() , dataLimite, dtLimiteInferior));
		}
		if(reqMaterial.getDtConfirmacao() != null){
			String propertyName = "RMS." + SceReqMaterial.Fields.DATA_CONFIRMACAO.toString();
			criteria.add(obterRestrictionData(propertyName,reqMaterial.getDtConfirmacao() , dataLimite, dtLimiteInferior));
		}
		if(reqMaterial.getDtEfetivacao() != null){
			String propertyName = "RMS." + SceReqMaterial.Fields.DATA_EFETIVACAO.toString();
			criteria.add(obterRestrictionData(propertyName,reqMaterial.getDtEfetivacao() , dataLimite, dtLimiteInferior));
		}
		
		if(reqMaterial.getDtEstorno() != null){
			String propertyName = "RMS." + SceReqMaterial.Fields.DATA_ESTORNO.toString();
			criteria.add(obterRestrictionData(propertyName,reqMaterial.getDtEstorno() , dataLimite, dtLimiteInferior));
		}
		
		if(reqMaterial.getDtGeracao() != null){
			String propertyName = "RMS." + SceReqMaterial.Fields.DATA_GERACAO.toString();
			criteria.add(obterRestrictionData(propertyName,reqMaterial.getDtGeracao() , dataLimite, dtLimiteInferior));
		}
		
		if(reqMaterial.getGrupoMaterial() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), reqMaterial.getGrupoMaterial()));
		}
		if(reqMaterial.getIndImpresso() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.IND_IMPRESSO.toString(), reqMaterial.getIndImpresso()));
		}
		if(reqMaterial.getIndSitFaturamento() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.IND_SIT_FATURAMENTO.toString(), reqMaterial.getIndSitFaturamento()));
		}
		if(reqMaterial.getIndSituacao() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.IND_SITUACAO.toString(), reqMaterial.getIndSituacao()));
		}
		if(reqMaterial.getNomeImpressora() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.NOME_IMPRESSORA.toString(), reqMaterial.getNomeImpressora()));
		}
		if(reqMaterial.getObservacao() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.OBSERVACAO.toString(), reqMaterial.getObservacao()));
		}
		if(reqMaterial.getPacoteMaterial() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.PACOTE_MATERIAL.toString(), reqMaterial.getPacoteMaterial()));
		}
		if(reqMaterial.getPdmNumero() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.PDM_NUMERO.toString(), reqMaterial.getPdmNumero()));
		}
		if(reqMaterial.getReqMaterial() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.SCE_REQ_MATERIAIS.toString(), reqMaterial.getReqMaterial()));
		}
		if(reqMaterial.getRmrPaciente() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.RMR_PACIENTE.toString(), reqMaterial.getRmrPaciente()));
		}
		if(reqMaterial.getRmrPacienteNrs() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.RMR_PACIENTE_NRS.toString(), reqMaterial.getRmrPacienteNrs()));
		}
		if(reqMaterial.getSeq() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.SEQ.toString(), reqMaterial.getSeq()));
		}
		if(reqMaterial.getServidor() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.SERVIDOR.toString(), reqMaterial.getServidor()));
		}
		if(reqMaterial.getServidorCancelado() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.SERVIDOR_CANCELADO.toString(), reqMaterial.getServidorCancelado()));
		}
		if(reqMaterial.getServidorConfirmado() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString(), reqMaterial.getServidorConfirmado()));
		}
		if(reqMaterial.getServidorEfetivado() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.SERVIDOR_EFETIVADO.toString(), reqMaterial.getServidorEfetivado()));
		}
		if(reqMaterial.getServidorEstornado() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.SERVIDOR_ESTORNADO.toString(), reqMaterial.getServidorEstornado()));
		}
		if(reqMaterial.getTipoMovimento() != null){
			criteria.add(Restrictions.eq("RMS." + SceReqMaterial.Fields.TIPO_MOVIMENTO.toString(), reqMaterial.getTipoMovimento()));
		}
		return criteria;
	}

	private Criterion obterRestrictionData(String propertyName, Date dataPsq, Date dataLimite,Boolean dtLimiteInferior) {
		if(dataLimite != null){
			if(dtLimiteInferior){ //Busca entre a dtLimiteInferior e a data fornecida
				return Restrictions.between(propertyName, dataLimite, dataPsq);
			}
			else{//Busca entre a data fornecida e a dtLimiteInferior 
				return Restrictions.between(propertyName, dataPsq,dataLimite);
			}
		}
		else{
			//Busca somente no dia da data informada
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataPsq);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date dtInicio = cal.getTime();
			
			cal.set(Calendar.HOUR, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Date dtFim = cal.getTime();
			return Restrictions.between(propertyName, dtInicio,dtFim);
		}
	}
	
	public SceReqMaterial obterMaterialPorId(Integer seqReq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterial.class);

		criteria.createAlias(SceReqMaterial.Fields.PACOTE_MATERIAL.toString(), "pctm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), SceReqMaterial.Fields.CENTRO_CUSTOS.toString(),JoinType.INNER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(),SceReqMaterial.Fields.CENTRO_CUSTOS_APLICA.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.ALMOXARIFADO.toString(), SceReqMaterial.Fields.ALMOXARIFADO.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.CCT_CODIGO.toString(), "almCct" , JoinType.INNER_JOIN);
		
		criteria.createAlias(SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), SceReqMaterial.Fields.GRUPO_MATERIAL.toString(),JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR.toString(), SceReqMaterial.Fields.SERVIDOR.toString(),JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				SceReqMaterial.Fields.SERVIDOR.toString() + "_" + RapServidores.Fields.PESSOA_FISICA.toString(),JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_CANCELADO.toString(),SceReqMaterial.Fields.SERVIDOR_CANCELADO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_CANCELADO.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				SceReqMaterial.Fields.SERVIDOR_CANCELADO.toString() + "_" + RapServidores.Fields.PESSOA_FISICA.toString(),
				JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString(),SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				SceReqMaterial.Fields.SERVIDOR_CONFIRMADO.toString() + "_" + RapServidores.Fields.PESSOA_FISICA.toString(),
				JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_EFETIVADO.toString(),SceReqMaterial.Fields.SERVIDOR_EFETIVADO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_EFETIVADO.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				SceReqMaterial.Fields.SERVIDOR_EFETIVADO.toString() + "_" + RapServidores.Fields.PESSOA_FISICA.toString(),
				JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_ESTORNADO.toString(),SceReqMaterial.Fields.SERVIDOR_ESTORNADO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.SERVIDOR_ESTORNADO.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				SceReqMaterial.Fields.SERVIDOR_ESTORNADO.toString() + "_" + RapServidores.Fields.PESSOA_FISICA.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.TIPO_MOVIMENTO.toString(), SceReqMaterial.Fields.TIPO_MOVIMENTO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.ATENDIMENTO.toString(), SceReqMaterial.Fields.ATENDIMENTO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceReqMaterial.Fields.ATENDIMENTO.toString() + "." + AghAtendimentos.Fields.PACIENTE.toString(),
				AghAtendimentos.Fields.PACIENTE.toString(), JoinType.LEFT_OUTER_JOIN);
		

		criteria.add(Restrictions.eq(SceReqMaterial.Fields.SEQ.toString(), seqReq));

		return (SceReqMaterial) executeCriteriaUniqueResult(criteria);

	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}
}
