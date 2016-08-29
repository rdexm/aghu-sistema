package br.gov.mec.aghu.blococirurgico.dao;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.vo.CancelamentoOpmeVO;
import br.gov.mec.aghu.blococirurgico.vo.ConsultaMaterialRequisicaoVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.EspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.InfoProcdCirgRequisicaoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoHospitalarMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcItensRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.RequerenteVO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.blococirurgico.vo.SolicitarReceberOrcMatNaoLicitadoVO;
import br.gov.mec.aghu.blococirurgico.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.blococirurgico.vo.VisualizarAutorizacaoOpmeVO;
import br.gov.mec.aghu.dominio.DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.AghWFTemplateFluxo;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
public class MbcRequisicaoOpmesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcRequisicaoOpmes> {
    @Inject
    private MbcMateriaisItemOpmesDAO aMbcMateriaisItemOpmesDAO;
    @Inject
    private MbcItensRequisicaoOpmesDAO aMbcItensRequisicaoOpmesDAO;    
	private static final long serialVersionUID = -506628165399209099L;
	@Inject
    private IParametroFacade parametroFacade;
	public List<Object[]> consultaRequerentes(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.RAP_SERVIDORES.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.setProjection(	Projections.projectionList().add(Projections.distinct(Projections.property("PES." + RapPessoasFisicas.Fields.CODIGO.toString()))).add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString())).add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString())).add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString())));
		if(!objPesquisa.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(objPesquisa)){
				criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString(), Integer.valueOf(objPesquisa.toString())));
			} else if(CoreUtil.isNumeroShort(objPesquisa)){
				criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.SER_VIN_CODIGO_CRIACAO.toString(), Short.valueOf(objPesquisa.toString())));
			} else {
				criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq("SER." + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("PES." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
	public List<ExecutorEtapaAtualVO> consultarExecutoresEtapaAtual(Object valor) {
		StringBuilder hql = this.createQueryExecutorEtapaAtual();
		if(!valor.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(valor.toString())){
				hql.append(" AND SER." ).append( RapServidores.Fields.MATRICULA.toString() ).append( " = :matriculaExecutor");
			} else if(CoreUtil.isNumeroShort(valor.toString())){
				hql.append(" AND SER." ).append( RapServidores.Fields.VIN_CODIGO.toString()).append( " = :vinculoExecutor");
			} else {
				hql.append(" AND PES." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " = :nomePessoa");
			}
		}
		hql.append(" ORDER BY PES." ).append( RapPessoasFisicas.Fields.NOME.toString());
		Query query = createHibernateQuery(hql.toString());
		if(!valor.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(valor.toString())){
				query.setInteger("matriculaExecutor", Integer.parseInt(valor.toString()));
			} else if(CoreUtil.isNumeroShort(valor.toString())){
				query.setShort("vinculoExecutor", Short.parseShort(valor.toString()));
			} else {
				query.setString("nomePessoa", "%" + valor.toString() +"%");
			}
		}
		query.setResultTransformer(Transformers.aliasToBean(ExecutorEtapaAtualVO.class));
		return query.list();
	}
	public List<MbcRequisicaoOpmes> consultarListaRequisicoesPorAgenda(final Integer agendaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class);
		criteria.add(Restrictions.eq(MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString(), agendaSeq));
		List<MbcRequisicaoOpmes> requisicoes = executeCriteria(criteria);
		for (MbcRequisicaoOpmes mbcRequisicaoOpmes : requisicoes) {
			super.initialize(mbcRequisicaoOpmes.getItensRequisicao());super.initialize(mbcRequisicaoOpmes.getAgendas());
			super.initialize(mbcRequisicaoOpmes.getCirurgia());super.initialize(mbcRequisicaoOpmes.getGrupoAlcadaAvalOpms());
			for (MbcItensRequisicaoOpmes mbcItensRequisicaoOpmes : mbcRequisicaoOpmes.getItensRequisicao()) {
				super.initialize(mbcItensRequisicaoOpmes.getMateriaisItemOpmes());super.initialize(mbcItensRequisicaoOpmes.getRequisicaoOpmes());
				super.initialize(mbcItensRequisicaoOpmes.getFatItensProcedHospitalar());
				for (MbcMateriaisItemOpmes material : mbcItensRequisicaoOpmes.getMateriaisItemOpmes()) {
					super.initialize(material.getMaterial());super.initialize(material.getProcedHospInternos());
					super.initialize(material.getScoMarcaComercial());super.initialize(material.getItensRequisicaoOpmes());
				}
			}			
		}
		return requisicoes;
	}
	public List<MbcAgendas> consultarUnidadesFuncionaisProcedimentosVinculadosRequisicoesOPMEs() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		return executeCriteria(criteria);
	}
	public List<FatItensProcedHospitalar> consultarProcedimentoSUSVinculadoProcedimentoInterno(Integer pciSeq, Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString(), "CGI");
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "PHI");
		criteria.createAlias("PHI." + FatProcedHospInternosPai.Fields.PROCEDIMENTO_CIRURGICO.toString(), "PCI");
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternosPai.Fields.SITUACAO.toString(), DominioSituacao.A));
		BigDecimal phoSeq = this.getParametroFacade().obterValorNumericoAghParametros(AghuParametrosEnum.P_TABELA_FATUR_PADRAO.toString());
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq.shortValue()));		
		BigDecimal cspCnvCodigo = this.getParametroFacade().obterValorNumericoAghParametros(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO.toString());
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cspCnvCodigo.shortValue()));		
		BigDecimal cspSeq = this.getParametroFacade().obterValorNumericoAghParametros(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO.toString());
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cspSeq.shortValue()));		
		String strPesquisa = (String) param;
		if (CoreUtil.isNumeroLong(strPesquisa) || CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString(),Integer.valueOf(strPesquisa)), Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), Long.valueOf(strPesquisa))));
			} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq));
		criteria.addOrder(Order.asc("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		return executeCriteria(criteria);
	}
	protected IParametroFacade getParametroFacade() {return parametroFacade;}
	public List<MbcAgendas> consultarUnidadesFuncionaisProcedimentosVinculadosRequisicoesOPMEs(Object valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString() , "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.UNF.toString(), "UNF");
		criteria.addOrder(Order.asc("AGD." + MbcAgendas.Fields.UNF_SEQ.toString()));
		return executeCriteria(criteria);
	}
	public Long consultarUnidadesFuncionaisProcedimentosVinculadosRequisicoesOPMEsCount(Object valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString() , "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.UNF.toString(), "UNF");
		return executeCriteriaCount(criteria);
	}
	public List<MbcAgendas> consultarEspecialidadesProcedimentosVinculadosRequisicoesOPMEs(Object valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString() , "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ESPECIALIDADE.toString() , "ESP");
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	public Long consultarEspecialidadesProcedimentosVinculadosRequisicoesOPMEsCount(Object valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString() , "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ESPECIALIDADE.toString() , "ESP");
		return executeCriteriaCount(criteria);
	}
	public FatItensProcedHospitalar getFatItensProcedHospitalar(Integer seq, Short phoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "CMP");
		criteria.add(Restrictions.eq("CMP." + FatItensProcedHospitalar.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("CMP." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		return (FatItensProcedHospitalar)executeCriteriaUniqueResult(criteria);
	}
	public List<ItemProcedimentoHospitalarMaterialVO> consultarItensProcedimentoHospitalarMateriais(final Integer pciSeq, final Integer iphSeq, final Short phoSeq) {
		DetachedCriteria criteria = obterCriteriaConsultarItensProcedimentoHospitalarMateriais(pciSeq, iphSeq, phoSeq);		
		List<ItemProcedimentoHospitalarMaterialVO> itens = executeCriteria(criteria);
		for (ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO : itens) {
			if (!DominioSituacao.A.equals(itemProcedimentoHospitalarMaterialVO.getCmpPhiSituacao())) {
				itemProcedimentoHospitalarMaterialVO.setCmpPhi(null);
				itemProcedimentoHospitalarMaterialVO.setCodigoMat(null);
				itemProcedimentoHospitalarMaterialVO.setNomeMat(null);
				itemProcedimentoHospitalarMaterialVO.setMaterial(null);}
			if (!DominioSituacao.A.equals(itemProcedimentoHospitalarMaterialVO.getMatSituacao())) {
				itemProcedimentoHospitalarMaterialVO.setCodigoMat(null);
				itemProcedimentoHospitalarMaterialVO.setNomeMat(null);
				itemProcedimentoHospitalarMaterialVO.setMaterial(null);}
		}return itens;
			}
	private DetachedCriteria obterCriteriaConsultarItensProcedimentoHospitalarMateriais(final Integer pciSeq, final Integer iphSeq, final Short phoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "CMP");
		criteria.createAlias("PHI." + FatProcedHospInternosPai.Fields.PROCEDIMENTO_CIRURGICO.toString(), "PCI");
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "PHI");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString(), "CGI");
		criteria.createAlias("CEI." + FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), "IPH");
		criteria.createAlias("CMP." + FatItensProcedHospitalar.Fields.FAT_COMPAT_EXCLUS_ITEMS_COMPATIBILIZA.toString(), "CEI");
		criteria.createAlias("CMP." + FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "VLR");
		criteria.add(Restrictions.le("VLR." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), new Date()));
		criteria.add(Restrictions.isNull("VLR." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));
		criteria.createAlias("CEI." + FatCompatExclusItem.Fields.COMPETENCIA_COMPATIBILIDADE.toString(), "CEC");
		criteria.add(Restrictions.le("CEC." + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.toString(), new Date()));
		criteria.add(Restrictions.isNull("CEC." + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.toString()));
		criteria.createAlias("CMP." + FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString(), "CMP_CGI", Criteria.LEFT_JOIN);
		criteria.createAlias("CMP_CGI." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "CMP_PHI", Criteria.LEFT_JOIN);
		criteria.createAlias("CMP_PHI." + FatProcedHospInternosPai.Fields.MATERIAIS.toString(), "MAT", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternosPai.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("CMP." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Subqueries.propertyEq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), obterCriteriaParaValorParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO)));		
		criteria.add(Restrictions.or(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), 13), Restrictions.isNull("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())));
		criteria.add(Subqueries.propertyEq("CMP." + FatItensProcedHospitalar.Fields.FOG_SGR_GRP_SEQ.toString(), obterCriteriaParaValorParametro(AghuParametrosEnum.P_GRUPO_OPM)));
		criteria.add(Subqueries.propertyEq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), obterCriteriaParaValorParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO)));
		criteria.add(Subqueries.propertyEq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), obterCriteriaParaValorParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO)));
		criteria.addOrder(Order.asc("CMP." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.SITUACAO.toString()));
		criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.CODIGO.toString()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.IPH_COMP_PHO.toString()).add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.SEQ.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.IPH_COMP_SEQ.toString()).add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.IPH_COMP_DSCR.toString()).add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.IPH_COMP_COD.toString()).add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.SEQ.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.IPH_COMP_SEQ.toString())
					.add(Projections.property("CEI." + FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.QTD_MAXIMA.toString()).add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.MAX_QTD_CONTA.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.MAX_QTD_CONTA.toString()).add(Projections.property("VLR." + FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.VLR_SERV_HOSPITALAR.toString()).add(Projections.property("VLR." + FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.VLR_SERV_PROFISSIONAL.toString()).add(Projections.property("VLR." + FatVlrItemProcedHospComps.Fields.VLR_SADT.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.VLR_SADT.toString())
					.add(Projections.property("VLR." + FatVlrItemProcedHospComps.Fields.VLR_PROCEDIMENTO.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.VLR_PROCEDIMENTO.toString()).add(Projections.property("VLR." + FatVlrItemProcedHospComps.Fields.VLR_ANESTESIA.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.VLR_ANESTESIA.toString()).add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.CODIGO_MAT.toString()).add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.NOME_MAT.toString()).add(Projections.property("CMP_CGI." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.CMP_PHI.toString())
					.add(Projections.property("CMP_PHI." + FatProcedHospInternosPai.Fields.MATERIAIS.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.MAT.toString()).add(Projections.property("CMP_PHI." + FatProcedHospInternosPai.Fields.SITUACAO.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.CMP_PHI_SITUACAO.toString()).add(Projections.property("MAT." + ScoMaterial.Fields.SITUACAO.toString()), ItemProcedimentoHospitalarMaterialVO.Fields.MAT_SITUACAO.toString())
					);criteria.setResultTransformer(Transformers.aliasToBean(ItemProcedimentoHospitalarMaterialVO.class));
		return criteria;
	}
	public void consultar7(MbcAgendas agenda) {
		DetachedCriteria subq1 = DetachedCriteria.forClass(MbcRequisicaoOpmes.class);
		subq1.add(Restrictions.eq(MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString(), agenda.getSeq()));
		DetachedCriteria criteriac5 = obterCriteriaConsultarItensProcedimentoHospitalarMateriais(null, null, null);
		criteriac5.add(Restrictions.or(Restrictions.eq("MAT." + ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A), Restrictions.isNull("MAT." + ScoMaterial.Fields.SITUACAO.toString())));
		criteriac5.add(Restrictions.or(Restrictions.eq("CMP_PHI." + FatProcedHospInternosPai.Fields.SITUACAO.toString(), DominioSituacao.A), Restrictions.isNull("CMP_PHI." + FatProcedHospInternosPai.Fields.SITUACAO.toString())));
		criteriac5.add(Subqueries.notExists(subq1));
		List<FatItensProcedHospitalar> listaC5 = executeCriteria(criteriac5);
		DetachedCriteria criteriac6 = obterCriteriaConsultarMateriaisInseridosRequisicaoOpmes(null);
		List<FatItensProcedHospitalar> listaC6 = executeCriteria(criteriac6);
		Set<FatItensProcedHospitalar> listaAll = new HashSet<FatItensProcedHospitalar>();
		listaAll.addAll(listaC5);
		listaAll.addAll(listaC6);
	}
	private DetachedCriteria obterCriteriaConsultarMateriaisInseridosRequisicaoOpmes(final Integer ropAgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		criteria.createAlias("IRO." + MbcItensRequisicaoOpmes.Fields.REQUISICAO_OPMES.toString(), "ROP");
		criteria.createAlias("IRO." + MbcItensRequisicaoOpmes.Fields.MATERIAIS_ITEM_OPME.toString(), "MIO");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.ITENS_REQUISICAO_OPMES.toString(), "IRO", Criteria.LEFT_JOIN);
		criteria.createAlias("MIO." + MbcMateriaisItemOpmes.Fields.PROCED_HOSP_INTERNOS.toString(), "PHI", Criteria.LEFT_JOIN);
		criteria.createAlias("MIO." + MbcMateriaisItemOpmes.Fields.MATERIAL.toString(), "MAT", Criteria.LEFT_JOIN);		
		criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString(), ropAgdSeq));
		criteria.addOrder(Order.asc("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.CODIGO.toString()));
		return criteria;
	}
	private DetachedCriteria obterCriteriaParaValorParametro(AghuParametrosEnum parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);
		criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), parametro.name()));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString())));
		return criteria;
	}
	public List<FatItensProcedHospitalar> consultarMateriaisInseridosRequisaoOpmes(final Integer ropAgdSeq) {
		DetachedCriteria criteria = obterCriteriaConsultarMateriaisInseridosRequisicaoOpmes(ropAgdSeq);
		return executeCriteria(criteria);
	}
	public Object consultarEquipesProcedimentosVinculadosRequisicoesOPMEs() {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		DetachedCriteria subcriteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		subcriteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString() , "AGD");
		subcriteria.add(Restrictions.eqProperty("ROP." + MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString(), "AGD." + MbcAgendas.Fields.SEQ.toString()));
		subcriteria.add(Restrictions.eqProperty("AGD." + MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), "SER." + RapServidores.Fields.MATRICULA.toString()));
		subcriteria.add(Restrictions.eqProperty("AGD." + MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(),"SER." + RapServidores.Fields.VIN_CODIGO.toString()));
		subcriteria.add(Restrictions.eqProperty("SER." + RapServidores.Fields.PES_CODIGO.toString(), "PES." + RapPessoasFisicas.Fields.CODIGO.toString()));
		ProjectionList projectionSub = Projections.projectionList();
		projectionSub.add(Projections.property("ROP." + MbcRequisicaoOpmes.Fields.ID.toString()));
		subcriteria.setProjection(projectionSub);
		criteria.add(Subqueries.exists(subcriteria));
		return executeCriteria(criteria);
	}
	public Long consultarEquipesProcedimentosVinculadosRequisicoesOPMEsCount() {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		DetachedCriteria subcriteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		subcriteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString() , "AGD");
		subcriteria.add(Restrictions.eqProperty("ROP." + MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString(), "AGD." + MbcAgendas.Fields.SEQ.toString()));
		subcriteria.add(Restrictions.eqProperty("AGD." + MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), "SER." + RapServidores.Fields.MATRICULA.toString()));
		subcriteria.add(Restrictions.eqProperty("AGD." + MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(),"SER." + RapServidores.Fields.VIN_CODIGO.toString()));
		subcriteria.add(Restrictions.eqProperty("SER." + RapServidores.Fields.PES_CODIGO.toString(), "PES." + RapPessoasFisicas.Fields.CODIGO.toString()));
		ProjectionList projectionSub = Projections.projectionList();
		projectionSub.add(Projections.property("ROP." + MbcRequisicaoOpmes.Fields.ID.toString()));
		criteria.add(Subqueries.exists(subcriteria));
		return executeCriteriaCount(criteria);
	}
	public MbcRequisicaoOpmes obterMbcRequisicaoOpmesPorFluxo(Integer seqFluxo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.FLUXO_ID.toString(), seqFluxo));
		return (MbcRequisicaoOpmes) executeCriteriaUniqueResult(criteria);
	}
	public SolicitarReceberOrcMatNaoLicitadoVO pesquisarMatNaoLicitado(Short seqRequisicaoOpme){
		StringBuilder hql = new StringBuilder(300);
		hql.append("SELECT distinct ")
		.append("ROP." ).append( MbcRequisicaoOpmes.Fields.CRIADO_EM.toString() ).append( " as " ).append( SolicitarReceberOrcMatNaoLicitadoVO.Fields.CRIADO_EM.toString())
		.append(", RPE." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( SolicitarReceberOrcMatNaoLicitadoVO.Fields.SOLICITANTE.toString())
		.append(", TET." ).append( AghWFTemplateEtapa.Fields.DESCRICAO.toString() ).append( " as " ).append( SolicitarReceberOrcMatNaoLicitadoVO.Fields.DESCRICAO_ETAPA.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.JUST_REQUISICAO_OPME.toString() ).append( " as " ).append( SolicitarReceberOrcMatNaoLicitadoVO.Fields.JUSTIFICATIVA.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.OBSERVACAO_OPME.toString() ).append( " as " ).append( SolicitarReceberOrcMatNaoLicitadoVO.Fields.OBSERVACAO.toString())
		.append(" FROM ")
		.append(MbcRequisicaoOpmes.class.getSimpleName() ).append( " ROP, ")
		.append(AghWFFluxo.class.getSimpleName() ).append( " FLU, ")
		.append(AghWFEtapa.class.getSimpleName() ).append( " ETA, ")
		.append(AghWFTemplateEtapa.class.getSimpleName() ).append( " TET, ")
		.append(RapServidores.class.getSimpleName() ).append( " RAP, ")
		.append(RapPessoasFisicas.class.getSimpleName() ).append( " RPE")
		.append(" WHERE ")
		.append("ROP." ).append( MbcRequisicaoOpmes.Fields.FLUXO_ID.toString() ).append( " = FLU." ).append( AghWFFluxo.Fields.SEQ.toString())
		.append(" AND ETA." ).append( AghWFEtapa.Fields.WFL_SEQ.toString() ).append( " = FLU." ).append( AghWFFluxo.Fields.SEQ.toString())
		.append(" AND ETA." ).append( AghWFEtapa.Fields.TEMPLATE_ETAPA.toString() ).append( " = TET." ).append( AghWFTemplateEtapa.Fields.SEQ.toString())
		.append(" AND RAP." ).append( RapServidores.Fields.MATRICULA.toString() ).append( " = ROP." ).append( MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString())
		.append(" AND RAP." ).append( RapServidores.Fields.VIN_CODIGO.toString() ).append( " = ROP." ).append( MbcRequisicaoOpmes.Fields.SER_VIN_CODIGO_CRIACAO.toString())
		.append(" AND RAP." ).append( RapServidores.Fields.PES_CODIGO.toString() ).append( " = RPE." ).append( RapPessoasFisicas.Fields.CODIGO.toString())
		.append(" AND ETA." ).append( AghWFEtapa.Fields.DT_FIM.toString() ).append( " is null");
		if(seqRequisicaoOpme != null){hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.ID.toString() + " = :seqRequisicaoOpme ");}
		Query query = createHibernateQuery(hql.toString());
		if(seqRequisicaoOpme != null){query.setShort("seqRequisicaoOpme", seqRequisicaoOpme);}
		query.setResultTransformer(Transformers.aliasToBean(SolicitarReceberOrcMatNaoLicitadoVO.class));
		return (SolicitarReceberOrcMatNaoLicitadoVO) query.uniqueResult(); 
	}
	public List<Object[]> pesquisarUnidadeFuncional(Object unidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.UNF.toString(), "UNF");
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()))).add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString())).add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SIGLA.toString())));
		if(!unidade.toString().isEmpty()){
			if(CoreUtil.isNumeroShort(unidade)){
				criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(unidade.toString())));
			} else {
				criteria.add(Restrictions.ilike("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString(), unidade.toString(), MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc("UNF." + AghUnidadesFuncionais.Fields.SIGLA.toString()));
		return executeCriteria(criteria);
	}
	public List<Object[]> pesquisarEspecialidade(Object especialidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("ESP." + AghEspecialidades.Fields.SEQ.toString()))).add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString())).add(Projections.property("ESP." + AghEspecialidades.Fields.SIGLA.toString())));
		if(!especialidade.toString().isEmpty()){
			if(CoreUtil.isNumeroShort(especialidade)){
				criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(especialidade.toString())));
			} else {
				if(especialidade.toString().length() <= 3){
					criteria.add(Restrictions.ilike("ESP." + AghEspecialidades.Fields.SIGLA.toString(), especialidade.toString(), MatchMode.EXACT));
				} else {
					criteria.add(Restrictions.ilike("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), especialidade.toString(), MatchMode.ANYWHERE));
				}
			}
		}
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.SIGLA.toString()));
		return executeCriteria(criteria);
	}
	public List<Object[]> pesquisarEquipe(Object equipe){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.PUC_SERVIDOR .toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()))).add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString())).add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString())).add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME_USUAL.toString())).add(Projections.property("AGD." + MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString())));
		if(!equipe.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(equipe)){
				criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), Integer.parseInt(equipe.toString())));
			} else {
				criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), equipe.toString(), MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc("PES." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	public List<RequisicoesOPMEsProcedimentosVinculadosVO> pesquisarRequisicoesOpmes(Short seqRequisicao, Date dataRequisicao, RequerenteVO requerente,	DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada, ExecutorEtapaAtualVO executorEtapaAtual, Date dataProcedimento, UnidadeFuncionalVO unidadeFuncional, EspecialidadeVO especialidade, EquipeVO equipe, Integer prontuario, Boolean pesquisarRequisicao, Integer nrDias,Integer executorSeq, Integer etapaSeq) {
		Query query = this.createQueryPesquisarRequisicoesOpmes(seqRequisicao,dataRequisicao, requerente, etapaAtualRequisicaoSelecionada, executorEtapaAtual, dataProcedimento, unidadeFuncional, especialidade, equipe, prontuario, pesquisarRequisicao, nrDias, executorSeq, etapaSeq);
		setarFiltros(seqRequisicao, dataRequisicao, requerente,	etapaAtualRequisicaoSelecionada, executorEtapaAtual, dataProcedimento, unidadeFuncional, especialidade, equipe,	prontuario, pesquisarRequisicao, nrDias, query, executorSeq, etapaSeq);
		return query.setResultTransformer(Transformers.aliasToBean(RequisicoesOPMEsProcedimentosVinculadosVO.class)).list();
	}
	public List<RequisicoesOPMEsProcedimentosVinculadosVO> pesquisarRequisicoesOpmesCompativeis(Short seqRequisicao, Date dataRequisicao, RequerenteVO requerente,	DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada, ExecutorEtapaAtualVO executorEtapaAtual, Date dataProcedimento, UnidadeFuncionalVO unidadeFuncional, EspecialidadeVO especialidade, EquipeVO equipe, Integer prontuario, Boolean pesquisarRequisicao, Integer nrDias,Integer executorSeq, Integer etapaSeq) {
		Query query = this.createQueryPrimeiraPesquisarReqOpmes(seqRequisicao,dataRequisicao, requerente, etapaAtualRequisicaoSelecionada, executorEtapaAtual, dataProcedimento, unidadeFuncional, especialidade, equipe, prontuario, pesquisarRequisicao, nrDias, executorSeq, etapaSeq);
		if(seqRequisicao != null) {
			query.setShort("seqRequisicao", seqRequisicao);
		}
		if(dataRequisicao != null) {
			Date dataRequisicaoInicial = DateUtil.obterDataComHoraInical(dataRequisicao);
			Date dataRequisicaoFinal = DateUtil.adicionaDias(dataRequisicaoInicial, 1);
			query.setDate("dataRequisicaoInicial", dataRequisicaoInicial);
			query.setDate("dataRequisicaoFinal", dataRequisicaoFinal);
		}
		if(requerente != null){
			query.setInteger("matriculaRequerente", requerente.getMatricula());
			query.setShort("vinCodigoRequerente", requerente.getVinculo());
		}
		if(executorEtapaAtual != null){
			query.setInteger("matriculaExecutor", executorEtapaAtual.getMatricula());
			query.setShort("codigoExecutor", executorEtapaAtual.getVinculo());
		}
		if(unidadeFuncional != null){
			query.setShort("unidadeFuncional", unidadeFuncional.getSeq());
		}
		if(dataProcedimento != null){
			query.setDate("dataProcedimento", dataProcedimento);
		}
		if(especialidade != null){
			query.setShort("especialidade", especialidade.getSeq());
		}
		if(equipe != null){
			query.setInteger("matriculaEquipe", equipe.getMatricula());
			query.setShort("vinCodigoEquipe", equipe.getVinculo());
		}
		if(prontuario != null){
			query.setInteger("prontuario", prontuario);
		}
		query = filtrosDias(nrDias,pesquisarRequisicao,query);
		return query.setResultTransformer(Transformers.aliasToBean(RequisicoesOPMEsProcedimentosVinculadosVO.class)).list();
	}
	private Query filtrosDias(Integer nrDias, Boolean pesquisarRequisicao, Query query){
		if(pesquisarRequisicao.equals(Boolean.TRUE)){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, - nrDias);
			query.setDate("numeroDias", calendar.getTime());
		}return query;
	}
	private Query createQueryPesquisarRequisicoesOpmes(Short seqRequisicao, Date dataRequisicao, RequerenteVO requerente,DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,ExecutorEtapaAtualVO executorEtapaAtual, Date dataProcedimento, UnidadeFuncionalVO unidadeFuncional, EspecialidadeVO especialidade, EquipeVO equipe, Integer prontuario, Boolean pesquisarRequisicao, Integer nrDias, Integer executorSeq, Integer etapaSeq){
		StringBuilder hql = new StringBuilder(1200);
		hql = this.createQueryPesquisarReqOpmes(hql);
		setFiltroReq(seqRequisicao, hql);
		setFiltroDataReq(dataRequisicao, hql);
		setFiltroRequerente(requerente, hql);
		setFiltroEtapaAtual(executorEtapaAtual, hql);
		setFiltroUnidadeFuncional(unidadeFuncional, hql);
		setFiltroDataProcedimento(dataProcedimento, hql);
		setFiltroEspecialidade(especialidade, hql);
		setFiltroEquipe(equipe, hql);
		setFiltroProntuario(prontuario, hql);
		setEtapaAtual(etapaAtualRequisicaoSelecionada, pesquisarRequisicao, hql);
		this.setExecutorEEtapa(executorSeq, etapaSeq, hql);
		criarOrderBy(hql);
		Query query = (Query) this.createHibernateQuery(hql.toString());
		return query;
	}
	private void setExecutorEEtapa(Integer executorSeq, Integer etapaSeq,StringBuilder hql) {
		if(executorSeq != null && etapaSeq != null){
			hql.append(" AND WEX." + AghWFExecutor.Fields.SEQ.toString() + " = :executorSeq");
			hql.append(" AND WET." + AghWFEtapa.Fields.SEQ.toString() + " = :etapaSeq");
		}
	}
	private void setarFiltros( Short seqRequisicao,Date dataRequisicao, RequerenteVO requerente, DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada, ExecutorEtapaAtualVO executorEtapaAtual, Date dataProcedimento, UnidadeFuncionalVO unidadeFuncional, EspecialidadeVO especialidade, EquipeVO equipe, Integer prontuario, Boolean pesquisarRequisicao, Integer nrDias, Query query, Integer executorSeq, Integer etapaSeq) {
		if(seqRequisicao != null) {
			query.setShort("seqRequisicao", seqRequisicao);
		}
		if(dataRequisicao != null) {
			Date dataRequisicaoInicial = DateUtil.obterDataComHoraInical(dataRequisicao);
			Date dataRequisicaoFinal = DateUtil.adicionaDias(dataRequisicaoInicial, 1);
			query.setDate("dataRequisicaoInicial", dataRequisicaoInicial);
			query.setDate("dataRequisicaoFinal", dataRequisicaoFinal);
		}
		if(requerente != null){
			query.setInteger("matriculaRequerente", requerente.getMatricula());
			query.setShort("vinCodigoRequerente", requerente.getVinculo());
		}
		if(executorEtapaAtual != null){
			query.setInteger("matriculaExecutor", executorEtapaAtual.getMatricula());
			query.setShort("vinCodigoExecutor", executorEtapaAtual.getVinculo());
		}
		if(unidadeFuncional != null){
			query.setShort("unidadeFuncional", unidadeFuncional.getSeq());
		}
		if(dataProcedimento != null){
			query.setDate("dataProcedimento", dataProcedimento);
		}
		if(especialidade != null){
			query.setShort("especialidade", especialidade.getSeq());
		}
		if(equipe != null){
			query.setInteger("matriculaEquipe", equipe.getMatricula());
			query.setShort("vinCodigoEquipe", equipe.getVinculo());
		}
		if(prontuario != null){
			query.setInteger("prontuario", prontuario);
		}
		checarExecutorEtapa(query, executorSeq, etapaSeq);
		setarFiltroEtapa(etapaAtualRequisicaoSelecionada, pesquisarRequisicao, nrDias, query);
	}
	private void checarExecutorEtapa(Query query, Integer executorSeq, Integer etapaSeq) {
		if(executorSeq != null && etapaSeq != null){
			query.setInteger("executorSeq", executorSeq);
			query.setInteger("etapaSeq", etapaSeq);
		}
	}
	private void setarFiltroEtapa(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,Boolean pesquisarRequisicao, Integer nrDias, Query query) {
		if(etapaAtualRequisicaoSelecionada != null){
			this.ajustaParametrosEtapaAtualRequisicaoSelecionadaQuery(etapaAtualRequisicaoSelecionada, pesquisarRequisicao,	nrDias, query);
		}
	}
	private void setEtapaAtual(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,Boolean pesquisarRequisicao, StringBuilder hql) {
		if(etapaAtualRequisicaoSelecionada != null){
			this.adicionaRestricaoEtapaAtualRequisicaoSelecionada(etapaAtualRequisicaoSelecionada, pesquisarRequisicao, hql);
		}
	}
	private void setFiltroProntuario(Integer prontuario, StringBuilder hql) {
		if(prontuario != null){hql.append(" AND PAC." + AipPacientes.Fields.PRONTUARIO.toString() + " = :prontuario");}
	}
	private void setFiltroEquipe(EquipeVO equipe, StringBuilder hql) {
		if(equipe != null){
			hql.append(" AND AGD." + MbcAgendas.Fields.PUC_SER_MATRICULA.toString() + " = :matriculaEquipe");
			hql.append(" AND AGD." + MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString() + " = :vinCodigoEquipe");}
		}
	private void setFiltroEspecialidade(EspecialidadeVO especialidade,StringBuilder hql) {
		if(especialidade != null){hql.append(" AND AGD." + MbcAgendas.Fields.ESP_SEQ.toString() + " = :especialidade");}
	}
	private void setFiltroDataProcedimento(Date dataProcedimento,StringBuilder hql) {
		if(dataProcedimento != null){hql.append(" AND AGD." + MbcAgendas.Fields.DT_AGENDA.toString() + " = :dataProcedimento");}
		}
	private void setFiltroUnidadeFuncional(UnidadeFuncionalVO unidadeFuncional,StringBuilder hql) {
		if(unidadeFuncional != null){
			hql.append(" AND AGD." + MbcAgendas.Fields.UNF_SEQ.toString() + " = :unidadeFuncional");
		}
	}
	private void setFiltroEtapaAtual(ExecutorEtapaAtualVO executorEtapaAtual,StringBuilder hql) {
		if(executorEtapaAtual != null){
			hql.append(" AND WEX." + AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString() + " = :matriculaExecutor");
			hql.append(" AND WEX." + AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString() + " = :vinCodigoExecutor");
			hql.append(" AND WEX." + AghWFExecutor.Fields.AUTORIZADO_EXECUTAR.toString() + " = 'S'");
		}
	}
	private void setFiltroRequerente(RequerenteVO requerente, StringBuilder hql) {
		if(requerente != null){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString() + " = :matriculaRequerente");
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.SER_VIN_CODIGO_CRIACAO.toString() + " = :vinCodigoRequerente");
		}
	}
	private void setFiltroDataReq(Date dataRequisicao, StringBuilder hql) {		//WHERE dataDoBanco between dataDaTela (09/07/2014 00:00) and dataDatTela +1 (10/07/2014 00:00)
		if(dataRequisicao != null){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.CRIADO_EM.toString() + " > :dataRequisicaoInicial ");
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.CRIADO_EM.toString() + " < :dataRequisicaoFinal  ");
		}
	}
	private void setFiltroReq(Short seqRequisicao, StringBuilder hql) {
		if(seqRequisicao != null){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.ID.toString() + " = :seqRequisicao");
		}
	}
	private void criarOrderBy(StringBuilder hql) {
		hql.append(" ORDER BY ROP." ).append( MbcRequisicaoOpmes.Fields.CRIADO_EM.toString())
		.append(" , AGD." ).append( MbcAgendas.Fields.DT_AGENDA.toString())
		.append(" , ESP." ).append( AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString());
	}
	private void adicionaRestricaoEtapaAtualRequisicaoSelecionada(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,Boolean pesquisarRequisicao, StringBuilder hql) {
		if(!DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.TODAS.equals(etapaAtualRequisicaoSelecionada) && 
		   !DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.ANDAMENTO.equals(etapaAtualRequisicaoSelecionada) &&
		   !DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.CONCLUIDA.equals(etapaAtualRequisicaoSelecionada)){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.SITUACAO.toString() + " = :situacaoEtapa");
		} else if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.CONCLUIDA.equals(etapaAtualRequisicaoSelecionada)){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " is not null");
		} else if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.ANDAMENTO.equals(etapaAtualRequisicaoSelecionada) && Boolean.FALSE.equals(pesquisarRequisicao)){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " is null");
		} else if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.ANDAMENTO.equals(etapaAtualRequisicaoSelecionada) && Boolean.TRUE.equals(pesquisarRequisicao)){
			hql.append(" AND ( ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " is null");
			hql.append(" OR ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " >= :numeroDias )");
		} else if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.TODAS.equals(etapaAtualRequisicaoSelecionada) && Boolean.TRUE.equals(pesquisarRequisicao)){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " >= :numeroDias");
		}
	}
	private void ajustaParametrosEtapaAtualRequisicaoSelecionadaQuery(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,Boolean pesquisarRequisicao, Integer nrDias, Query query) {
		if(!etapaAtualRequisicaoSelecionada.toString().equalsIgnoreCase("TODAS") &&  !etapaAtualRequisicaoSelecionada.toString().equalsIgnoreCase("ANDAMENTO") && !etapaAtualRequisicaoSelecionada.toString().equalsIgnoreCase("CONCLUIDA")){
			query.setString("situacaoEtapa", etapaAtualRequisicaoSelecionada.toString());
		}
		if(pesquisarRequisicao.equals(Boolean.TRUE)){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, - nrDias);
			query.setDate("numeroDias", calendar.getTime());
		}
	}
	private Query createQueryPrimeiraPesquisarReqOpmes(Short seqRequisicao, Date dataRequisicao, RequerenteVO requerente,	DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada, ExecutorEtapaAtualVO executorEtapaAtual, Date dataProcedimento, UnidadeFuncionalVO unidadeFuncional, EspecialidadeVO especialidade, EquipeVO equipe, Integer prontuario, Boolean pesquisarRequisicao, Integer nrDias,Integer executorSeq, Integer etapaSeq){
		StringBuilder hql = new StringBuilder(2100);
		hql.append(" SELECT")
		.append(" ROP." ).append( MbcRequisicaoOpmes.Fields.ID.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.REQUISICAO_SEQ.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_SEQ.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.SITUACAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.SITUACAO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.IND_AUTORIZADO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.IND_AUTORIZADO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.CRIADO_EM.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.DATA_REQUISICAO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.REQUERENTE_MATRICULA.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.SER_VIN_CODIGO_CRIACAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.REQUERENTE_VIN_CODIGO.toString())
		.append(", PES1." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PES1_NOME.toString())
		.append(", PES1." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PES3_NOME.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.UNF_SEQ.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_UNIDADE_SEQ.toString())
		.append(", UNF." ).append( AghUnidadesFuncionais.Fields.DESCRICAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.UNIDADE_FUNCIONAL_DESCRICAO.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.DT_AGENDA.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.DATA_PROCEDIMENTO.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.ESP_SEQ.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.ESPECIALIDADE_SEQ.toString())
		.append(", ESP." ).append( AghEspecialidades.Fields.SIGLA.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.SIGLA.toString())
		.append(", ESP." ).append( AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.NOME_ESPECIALIDADE.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.PUC_SER_MATRICULA.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_PUC_MATRICULA.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_PUC_VINCODIGO.toString())
		.append(", PES2." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PES2_NOME.toString())
		.append(", PAC." ).append( AipPacientes.Fields.PRONTUARIO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PRONTUARIO.toString())
		.append(", PAC." ).append( AipPacientes.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.NOME_PACIENTE.toString())
		.append(" FROM ").append(MbcRequisicaoOpmes.class.getSimpleName() ).append( " ROP ")
		.append(" INNER JOIN ").append(" ROP." ).append( MbcRequisicaoOpmes.Fields.AGENDA.toString() ).append( " AGD ")
		.append(" INNER JOIN ").append(" AGD." ).append( MbcAgendas.Fields.UNF.toString() ).append( " UNF ")
		.append(" INNER JOIN ").append(" AGD." ).append( MbcAgendas.Fields.ESPECIALIDADE.toString() ).append( " ESP ")
		.append(" INNER JOIN ").append(" AGD." ).append( MbcAgendas.Fields.PACIENTE.toString() ).append( " PAC ")
		.append(" INNER JOIN ").append(" AGD." ).append( MbcAgendas.Fields.PUC_SERVIDOR.toString() ).append( " SER2 ")
		.append(" INNER JOIN ").append(" SER2." ).append( RapServidores.Fields.PESSOA_FISICA.toString() ).append( " PES2 ")
		.append(" INNER JOIN ").append(" ROP." ).append( MbcRequisicaoOpmes.Fields.RAP_SERVIDORES.toString() ).append( " SER1 " )
		.append(" INNER JOIN ").append(" SER1." ).append( RapServidores.Fields.PESSOA_FISICA.toString() ).append( " PES1 ")
		.append(" WHERE ").append("  ROP." ).append( MbcRequisicaoOpmes.Fields.FLUXO_ID.toString()).append(" is null ")
		.append(" AND AGD." ).append( MbcAgendas.Fields.IND_EXCLUSAO.toString() ).append( " = 'N' ");
		setFiltroReq(seqRequisicao, hql);
		setFiltroDataReq(dataRequisicao, hql);
		setFiltroRequerente(requerente, hql);
		setFiltroUnidadeFuncional(unidadeFuncional, hql);
		setFiltroDataProcedimento(dataProcedimento, hql);
		setFiltroEspecialidade(especialidade, hql);
		setFiltroEquipe(equipe, hql);
		setFiltroProntuario(prontuario, hql);
		hql = filtros(etapaAtualRequisicaoSelecionada,hql,pesquisarRequisicao);
		if(executorEtapaAtual != null){
			hql.append(" AND ").append("  ROP." ).append( MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString()).append(" = :matriculaExecutor ");
			hql.append(" AND ").append("  ROP." ).append( MbcRequisicaoOpmes.Fields.SER_VIN_CODIGO_CRIACAO.toString()).append(" = :codigoExecutor ");}
		Query query = (Query) this.createHibernateQuery(hql.toString());
		return query;
	}
	private StringBuilder filtros(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,StringBuilder hql, Boolean pesquisarRequisicao){
		if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.CONCLUIDA.equals(etapaAtualRequisicaoSelecionada)){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " is not null");
		} else if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.ANDAMENTO.equals(etapaAtualRequisicaoSelecionada) && Boolean.FALSE.equals(pesquisarRequisicao)){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " is null");
		} else if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.ANDAMENTO.equals(etapaAtualRequisicaoSelecionada) && Boolean.TRUE.equals(pesquisarRequisicao)){
			hql.append(" AND ( ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " is null");
			hql.append(" OR ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " >= :numeroDias )");
		} else if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.TODAS.equals(etapaAtualRequisicaoSelecionada) && Boolean.TRUE.equals(pesquisarRequisicao)){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.DT_FIM.toString() + " >= :numeroDias");
		}return hql;
	}
	private StringBuilder createQueryPesquisarReqOpmes(StringBuilder hql){
		hql.append(" SELECT")
		.append(" ROP." ).append( MbcRequisicaoOpmes.Fields.ID.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.REQUISICAO_SEQ.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_SEQ.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.SITUACAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.SITUACAO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.IND_AUTORIZADO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.IND_AUTORIZADO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.CRIADO_EM.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.DATA_REQUISICAO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.REQUERENTE_MATRICULA.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.SER_VIN_CODIGO_CRIACAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.REQUERENTE_VIN_CODIGO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.FLUXO_ID.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.FLUXO_SEQ.toString())
		.append(", PES1." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PES1_NOME.toString())
		.append(", WET." ).append( AghWFEtapa.Fields.SEQ.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.ETAPA_SEQ.toString())
		.append(", WTE." ).append( AghWFTemplateEtapa.Fields.DESCRICAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.ETAPA_DESCRICAO.toString())
		.append(", WEX." ).append( AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.EXECUTOR_MATRICULA.toString())
		.append(", WEX." ).append( AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.EXECUTOR_VINCODIGO.toString())
		.append(", PES3." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PES3_NOME.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.UNF_SEQ.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_UNIDADE_SEQ.toString())
		.append(", UNF." ).append( AghUnidadesFuncionais.Fields.DESCRICAO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.UNIDADE_FUNCIONAL_DESCRICAO.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.DT_AGENDA.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.DATA_PROCEDIMENTO.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.ESP_SEQ.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.ESPECIALIDADE_SEQ.toString())
		.append(", ESP." ).append( AghEspecialidades.Fields.SIGLA.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.SIGLA.toString())
		.append(", ESP." ).append( AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.NOME_ESPECIALIDADE.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.PUC_SER_MATRICULA.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_PUC_MATRICULA.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.AGENDA_PUC_VINCODIGO.toString())
		.append(", PES2." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PES2_NOME.toString())
		.append(", PAC." ).append( AipPacientes.Fields.PRONTUARIO.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.PRONTUARIO.toString())
		.append(", PAC." ).append( AipPacientes.Fields.NOME.toString() ).append( " as " ).append( RequisicoesOPMEsProcedimentosVinculadosVO.Fields.NOME_PACIENTE.toString())
		.append(" FROM ").append(MbcRequisicaoOpmes.class.getSimpleName() ).append( " ROP ")
		.append(" INNER JOIN ").append(" ROP." ).append( MbcRequisicaoOpmes.Fields.AGENDA.toString() ).append( " AGD ")
		.append(" INNER JOIN ").append(" ROP." ).append( MbcRequisicaoOpmes.Fields.FLUXO.toString() ).append( " WFL ")
		.append(" INNER JOIN ").append(" WFL." ).append( AghWFFluxo.Fields.ETAPAS.toString() ).append( " WET ")
		.append(" LEFT JOIN ").append(" WET." ).append( AghWFEtapa.Fields.EXECUTORES.toString() ).append( " WEX ")
		.append(" LEFT JOIN ").append(" WEX." ).append( AghWFExecutor.Fields.SERVIDOR.toString() ).append( " SER3 ")
		.append(" LEFT JOIN ").append(" SER3." ).append( RapServidores.Fields.PESSOA_FISICA.toString() ).append( " PES3 ")
		.append(" INNER JOIN ").append(" AGD." ).append( MbcAgendas.Fields.UNF.toString() ).append( " UNF ")
		.append(" INNER JOIN ").append(" AGD." ).append( MbcAgendas.Fields.ESPECIALIDADE.toString() ).append( " ESP ")
		.append(" INNER JOIN ").append("  AGD." ).append( MbcAgendas.Fields.PACIENTE.toString() ).append( " PAC ")
		.append(" INNER JOIN ").append(" AGD." ).append( MbcAgendas.Fields.PUC_SERVIDOR.toString() ).append( " SER2 ")
		.append(" INNER JOIN ").append(" SER2." ).append( RapServidores.Fields.PESSOA_FISICA.toString() ).append( " PES2 ")
		.append(" INNER JOIN ").append(" ROP." ).append( MbcRequisicaoOpmes.Fields.RAP_SERVIDORES.toString() ).append( " SER1 " )
		.append(" INNER JOIN ").append(" SER1." ).append( RapServidores.Fields.PESSOA_FISICA.toString() ).append( " PES1 ")
		.append(" INNER JOIN ").append(" WET." ).append( AghWFEtapa.Fields.TEMPLATE_ETAPA.toString() ).append( " WTE ")
		.append(" INNER JOIN ").append(" WTE." ).append( AghWFTemplateEtapa.Fields.WTF_SEQ.toString() ).append( " WTF ")
		.append(" WHERE ").append("  WET." ).append( AghWFEtapa.Fields.SEQUENCIA.toString() )
		.append( " = ( SELECT max( WET2." ).append( AghWFEtapa.Fields.SEQUENCIA.toString() ).append( ") ").append( "FROM " ).append( AghWFEtapa.class.getSimpleName() ).append( " WET2 ").append( "WHERE WET2." ).append( AghWFEtapa.Fields.WFL_SEQ ).append( " = WET." ).append( AghWFEtapa.Fields.WFL_SEQ.toString() ).append( ')' )		
		.append(" AND WTF." ).append( AghWFTemplateFluxo.Fields.SEQ.toString() ).append( " = " ).append( "WFL." ).append( AghWFFluxo.Fields.WTF_SEQ.toString())
		.append(" AND WTE." ).append( AghWFTemplateEtapa.Fields.CODIGO.toString() ).append( " = " ).append( "ROP." ).append( MbcRequisicaoOpmes.Fields.SITUACAO.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.IND_EXCLUSAO.toString() ).append( " = 'N' ");
		return hql;
	}	
	public MbcRequisicaoOpmes obterDetalhesRequisicao(Short requisicaoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ITENS_PROCED_HOSPITALAR.toString(), "IPH");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "EPR");
		criteria.createAlias("EPR." + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		if(requisicaoSeq != null){
			criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), requisicaoSeq));
		}
		MbcRequisicaoOpmes item = (MbcRequisicaoOpmes) executeCriteriaUniqueResult(criteria);
		if(item != null){super.initialize(item.getFluxo());super.initialize(item.getAgendas().getPaciente());}
		return item;
	}
	public List<MbcItensRequisicaoOpmesVO> pesquisarMateriaisRequisicao(Short requisicaoSeq, DominioSimNao compativel, DominioSimNao licitado) {
		StringBuilder hql = new StringBuilder(1400);
		hql.append("SELECT DISTINCT ");
		hql.append(" IRO.SEQ           seq ");
	    hql.append(",IPH.PHO_SEQ           phoSeq ");
	    hql.append(",IPH.SEQ               iphSeq ");
	    hql.append(", CASE IRO.IND_REQUERIDO ");
	    hql.append("WHEN 'NOV' then '(Nova Solicitação de Material) '||iro.SOLC_NOVO_MAT ");
	    hql.append("WHEN 'ADC' then '(Material Adicionado pelo Usuário) '||mat.CODIGO||' - '||mat.NOME ");
	    hql.append("ELSE IPH.COD_TABELA||' - '||IPH.DESCRICAO ");
	    hql.append("END itemMatDescricao ");
	    hql.append(",IRO.IND_COMPATIVEL    indCompativel ");
	    hql.append(",CASE IRO.IND_REQUERIDO WHEN 'NOV' THEN 'N' ELSE 'S' END requerido ");
	    hql.append(",IRO.QTD_SOLC          quantidadeSolicitada ");
	    hql.append(",IRO.QTD_AUTR_SUS      quantidadeAutorizadaHospital ");
	    hql.append(",IRO.VLR_UNIT          valorUnitarioIph ");
	    hql.append(",IRO.DESC_INCOMPAT     descricaoIncompativel ");
	    hql.append(",CASE IRO.IND_REQUERIDO WHEN 'NOV' THEN 1 WHEN 'ADC' THEN 2 ELSE 3 END AS reqOrder ");
	    hql.append("FROM  AGH.MBC_REQUISICAO_OPMES            ROP ");
	    hql.append("JOIN  AGH.MBC_ITENS_REQUISICAO_OPMES      IRO ON ROP.SEQ = IRO.ROP_SEQ ");
	    hql.append("LEFT JOIN AGH.MBC_MATERIAIS_ITEM_OPMES    MIO ON IRO.SEQ = MIO.IRO_SEQ ");
	    hql.append("LEFT JOIN AGH.FAT_ITENS_PROCED_HOSPITALAR IPH ON IRO.IPH_PHO_SEQ = IPH.PHO_SEQ AND IRO.IPH_SEQ = IPH.SEQ ");
	    hql.append("LEFT JOIN AGH.FAT_PROCED_HOSP_INTERNOS    PHI ON MIO.PHI_SEQ = PHI.SEQ ");
	    hql.append("LEFT JOIN AGH.SCO_MATERIAIS               MAT ON MIO.MAT_CODIGO = MAT.CODIGO ");
	    hql.append(" WHERE IRO.IND_REQUERIDO <> 'NRQ' ");
		if(requisicaoSeq != null){
			hql.append(" AND    ROP.SEQ           = "+requisicaoSeq.toString()+" ");}
		if(compativel != null){
			hql.append(" AND    IRO.IND_COMPATIVEL = '" + compativel.name() + "'");}
		if(licitado != null){
			if(licitado.isSim()){				
				hql.append(" AND    IRO.IND_REQUERIDO  IN ('REQ', 'ADC') "); } 
			else {
				hql.append(" AND    IRO.IND_REQUERIDO  = 'NOV' ");}
			}
		hql.append(" ORDER BY reqOrder, ");
	    hql.append(" itemMatDescricao ");
	    SQLQuery query = createSQLQuery(hql.toString());
	    query.addScalar("seq", ShortType.INSTANCE);
		query.addScalar("phoSeq", ShortType.INSTANCE);
		query.addScalar("iphSeq", IntegerType.INSTANCE);
		query.addScalar("itemMatDescricao", StringType.INSTANCE);
		query.addScalar("indCompativel", StringType.INSTANCE);
		query.addScalar("requerido", StringType.INSTANCE);
		query.addScalar("quantidadeSolicitada", IntegerType.INSTANCE);
		query.addScalar("quantidadeAutorizadaHospital", IntegerType.INSTANCE);
		query.addScalar("valorUnitarioIph", BigDecimalType.INSTANCE);
		query.addScalar("descricaoIncompativel", StringType.INSTANCE);
		query.addScalar("reqOrder", IntegerType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(MbcItensRequisicaoOpmesVO.class));
		return query.list();
	}
	@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
	public ExecutorEtapaAtualVO pesquisarExecutorEtapaAtualProcesso(Short requisicaoSeq, RapServidores servidorLogado){
		StringBuilder hql = this.createQueryExecutorEtapaAtual();
		if(requisicaoSeq != null){
			hql.append(" AND ROP." + MbcRequisicaoOpmes.Fields.ID.toString() + " = :requisicaoSeq");
		}
		if(servidorLogado != null){
			hql.append(" AND WEX." + AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString() + " = :matricula");
			hql.append(" AND WEX." + AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString() + " = :vinCodigo");
		}
		Query query = createHibernateQuery(hql.toString());		
		if(requisicaoSeq != null){
			query.setShort("requisicaoSeq", requisicaoSeq);
		}		
		if(servidorLogado != null){
			query.setInteger("matricula", servidorLogado.getId().getMatricula());
			query.setShort("vinCodigo", servidorLogado.getId().getVinCodigo());
		}			
		query.setResultTransformer(Transformers.aliasToBean(ExecutorEtapaAtualVO.class));
		List<ExecutorEtapaAtualVO> lista = query.list();
		if(lista != null){
			if(lista.size() > 0){
				return lista.get(0);
			}
		}
		return null;  
	}	
	private StringBuilder createQueryExecutorEtapaAtual(){		
		StringBuilder hql = new StringBuilder(300);		
		hql.append("SELECT DISTINCT")
		.append(" SER." ).append( RapServidores.Fields.MATRICULA.toString() ).append( " as " ).append( ExecutorEtapaAtualVO.Fields.MATRICULA.toString())
		.append(", SER." ).append( RapServidores.Fields.VIN_CODIGO.toString() ).append( " as " ).append( ExecutorEtapaAtualVO.Fields.VINCULO.toString())
		.append(", PES." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( ExecutorEtapaAtualVO.Fields.NOME.toString())
		.append(", PES." ).append( RapPessoasFisicas.Fields.CODIGO.toString() ).append( " as " ).append( ExecutorEtapaAtualVO.Fields.CODIGO_PESSOA.toString())
		.append(", WEX." + AghWFExecutor.Fields.AUTORIZADO_EXECUTAR.toString() + " as " + ExecutorEtapaAtualVO.Fields.AUTORIZADO_EXECUTAR.toString())
		.append(" FROM ")
		.append(MbcRequisicaoOpmes.class.getSimpleName() ).append( " ROP, ")
		.append(AghWFFluxo.class.getSimpleName() ).append( " WFL, ")
		.append(AghWFEtapa.class.getSimpleName() ).append( " WET, ")
		.append(AghWFExecutor.class.getSimpleName() ).append( " WEX, ")
		.append(RapServidores.class.getSimpleName() ).append( " SER, ")
		.append(RapPessoasFisicas.class.getSimpleName() ).append( " PES")		
		.append(" WHERE ROP." ).append( MbcRequisicaoOpmes.Fields.FLUXO_ID.toString() ).append( " = WFL." ).append( AghWFFluxo.Fields.SEQ.toString())
		.append(" AND WFL." ).append( AghWFFluxo.Fields.SEQ.toString() ).append( " = WET." ).append( AghWFEtapa.Fields.WFL_SEQ_SEQ.toString())
		.append(" AND WET." ).append( AghWFEtapa.Fields.SEQ.toString() ).append( " = WEX." ).append( AghWFExecutor.Fields.WET_SEQ_ID.toString())
		.append(" AND WET." ).append( AghWFEtapa.Fields.SEQUENCIA.toString() ).append( " = (SELECT MAX(WET2." ).append( AghWFEtapa.Fields.SEQUENCIA.toString() ).append( ") ").append( "FROM " ).append( AghWFEtapa.class.getSimpleName() ).append( " WET2 ").append( "WHERE WET2." ).append( AghWFEtapa.Fields.WFL_SEQ_SEQ.toString() ).append( " = WET." ).append( AghWFEtapa.Fields.WFL_SEQ_SEQ.toString() ).append(')')
		.append(" AND WEX." ).append( AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString() ).append( " = SER." ).append( RapServidores.Fields.MATRICULA.toString())
		.append(" AND WEX." ).append( AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString() ).append( " = SER." ).append( RapServidores.Fields.VIN_CODIGO.toString())
		.append(" AND SER." ).append( RapServidores.Fields.PES_CODIGO.toString() ).append( " = PES." ).append( RapPessoasFisicas.Fields.CODIGO.toString());		
		return hql;
	}	
	public MbcAgendas verificaTelaMedicoAdm(Short requisicaoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, "AGD");
		criteria.createAlias("AGD." + MbcAgendas.Fields.REQUISICAO_OPME.toString(), "ROP");		
		if(requisicaoSeq != null){criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), requisicaoSeq));}		
		return (MbcAgendas) executeCriteriaUniqueResult(criteria);
	}
	public InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPME(final Short requisicaoSelecionada) {
		ConsultarInfoProcCirgReqOPMEQueryBuilder queryBuilder = new ConsultarInfoProcCirgReqOPMEQueryBuilder();
		return (InfoProcdCirgRequisicaoOPMEVO) executeCriteriaUniqueResult(queryBuilder.build(requisicaoSelecionada));
	}
	public InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPMESemSituacao(final Short requisicaoSelecionada) {
		ConsultarInfoProcCirgReqOPMEsQueryBuilder queryBuilder = new ConsultarInfoProcCirgReqOPMEsQueryBuilder();
		return (InfoProcdCirgRequisicaoOPMEVO) executeCriteriaUniqueResult(queryBuilder.build(requisicaoSelecionada));
	}
	public List<MateriaisProcedimentoOPMEVO> consultarMaterialProcedimentoOPME(final Short requisicaoSelecionada, List<DominioRequeridoItemRequisicao>  requisicoes) {
		ConsultaMaterialProcedimentoOPMEVOQueryBuilder queryBuilder = new ConsultaMaterialProcedimentoOPMEVOQueryBuilder();
		return executeCriteria(queryBuilder.build(requisicaoSelecionada, requisicoes));
	}		
	public List<ItemProcedimentoVO> consultarProcedimentoSUSMaterialOPMEExcludenciaMaterial(final Integer seqProcedimento, final Short phoSeq, final Integer iphSeq) {
		ConsultarProcedimentoMaterialBuilder builder = new ConsultarProcedimentoMaterialBuilder();
		StringBuilder hql = builder.createProduct();		
		final SQLQuery query = createSQLQuery(hql.toString());		
		query.setDate("dataInicioCompt", new Date());
		query.setDate("dataInicioVal", new Date());
		query.setString("indSituacaoIPH", DominioSituacao.A.name());
		query.setString("indSituacaoPHI", DominioSituacao.A.name());
		query.setString("indSituacaoCMP", DominioSituacao.A.name());
		query.setString("indSituacaoIPX", DominioSituacao.A.name());
		query.setInteger("seqProcedimento", seqProcedimento);
		query.setShort("phoSeq", phoSeq);
		query.setInteger("iphSeq", iphSeq);		
		query.setResultTransformer(Transformers.aliasToBean(ItemProcedimentoVO.class));		
		query.addScalar(ItemProcedimentoVO.Fields.PCI_SEQ.toString(), IntegerType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.PCI_DESCRICAO.toString(), StringType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.PHI_SEQ.toString(), IntegerType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.IPH_PHO_SEQ.toString(), ShortType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.IPH_SEQ.toString(), IntegerType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.IPH_COD_TABELA.toString(), LongType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.IPH_DESCRICAO.toString(), StringType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.CEI_IND_COMPARACAO.toString(), StringType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.CEI_IND_COMPAT_EXCLUS.toString(), StringType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.CMP_PHO_SEQ.toString(), ShortType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.CMP_SEQ.toString(), IntegerType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.CMP_COD_TABELA.toString(), LongType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.CMP_DESCRICAO.toString(), StringType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.EXC_IND_COMPARACAO.toString(), StringType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.EXC_IND_COMPAT_EXCLUS.toString(), StringType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.IPX_SEQ.toString(), IntegerType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.IPX_COD_TABELA.toString(), LongType.INSTANCE);
		query.addScalar(ItemProcedimentoVO.Fields.IPX_DESCRICAO.toString(), StringType.INSTANCE);		
		return query.list();
	}
	public List<ItemProcedimentoVO> consultarExcludenciaMaterial(final Short cmpPhoSeq, final Integer cmpSeq) {
		ConsultarExcludenciaMaterialBuilder builder = new ConsultarExcludenciaMaterialBuilder();
		builder.setIphSeq(cmpSeq);
		builder.setPhoSeq(cmpPhoSeq);
		DetachedCriteria criteria = builder.build();
		return executeCriteria(criteria);
	}	
	public FatCompatExclusItem consultarRelacionamentoProcedimentoSUSMaterialOpme(final Short iphPhoSeq, final Integer iphSeq, final Short iphPhoSeqCompatibiliza, final Integer iphSeqCompatibiliza) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class, "ICT");
		criteria.createAlias("ICT." + FatCompatExclusItem.Fields.COMPETENCIA_COMPATIBILIDADE.toString(), "CPX");
		criteria.add(Restrictions.eqProperty("ICT." + FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), "CPX." + FatCompetenciaCompatibilid.Fields.IPH_PHO_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("ICT." + FatCompatExclusItem.Fields.IPH_SEQ.toString(), "CPX." + FatCompetenciaCompatibilid.Fields.IPH_SEQ.toString()));
		criteria.add(Restrictions.eq("ICT." + FatCompatExclusItem.Fields.IND_INTERNACAO.toString(), true));
		criteria.add(Restrictions.le("CPX." + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.toString(), new Date()));
		criteria.add(Restrictions.isNull("CPX." + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.toString()));
		criteria.add(Restrictions.eq("ICT." + FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("ICT." + FatCompatExclusItem.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("ICT." + FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), iphPhoSeqCompatibiliza));
		criteria.add(Restrictions.eq("ICT." + FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), iphSeqCompatibiliza));
		return (FatCompatExclusItem) executeCriteriaUniqueResult(criteria);
	}
	public MbcRequisicaoOpmes obterRequisicaoOriginal(Short seq) {
		MbcRequisicaoOpmes req = obterPorChavePrimaria(seq);
		for (MbcItensRequisicaoOpmes item : req.getItensRequisicao()) {			
			if (item.getMateriaisItemOpmes() != null) {
				for (MbcMateriaisItemOpmes material : item.getMateriaisItemOpmes()) {
					getMateriaisItemOpmesDAO().desatachar(material);
				}
			}
			getItensRequisicaoOpmesDAO().desatachar(item);
		}desatachar(req);
		return req;
	}
	public MbcItensRequisicaoOpmesDAO getItensRequisicaoOpmesDAO() {return aMbcItensRequisicaoOpmesDAO;}
	public MbcMateriaisItemOpmesDAO getMateriaisItemOpmesDAO() {return aMbcMateriaisItemOpmesDAO;}
	public List<ConsultaMaterialRequisicaoVO> consultarMateriaisRequisicao(final Integer ropSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.ITENS_REQUISICAO.toString(), "IRO");
		criteria.createAlias("IRO." + MbcItensRequisicaoOpmes.Fields.ITENS_PROCED_HOSPITALAR.toString(), "IPH", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.in("IRO." + MbcItensRequisicaoOpmes.Fields.IND_REQUERIDO.toString(), new DominioRequeridoItemRequisicao[] {DominioRequeridoItemRequisicao.REQ, DominioRequeridoItemRequisicao.NOV}));
		criteria.add(Restrictions.eq("IRO." + MbcItensRequisicaoOpmes.Fields.IND_COMPATIVEL.toString(), DominioSimNao.N));
		criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), ropSeq));
		List<ConsultaMaterialRequisicaoVO> result1 = executeCriteria(criteria);		
		DetachedCriteria criteriad = DetachedCriteria.forClass(MbcItensRequisicaoOpmes.class, "ROP");
		criteriad.createAlias("ROP." + MbcRequisicaoOpmes.Fields.ITENS_REQUISICAO.toString(), "IRO");
		criteriad.createAlias("IRO." + MbcItensRequisicaoOpmes.Fields.MATERIAIS_ITEM_OPME.toString(), "MIO");
		criteriad.createAlias("MIO." + MbcMateriaisItemOpmes.Fields.MATERIAL.toString(), "MAT");
		criteriad.add(Restrictions.in("IRO." + MbcItensRequisicaoOpmes.Fields.IND_REQUERIDO.toString(), new DominioRequeridoItemRequisicao[] {DominioRequeridoItemRequisicao.ADC}));
		criteriad.add(Restrictions.eq("IRO." + MbcItensRequisicaoOpmes.Fields.IND_COMPATIVEL.toString(), DominioSimNao.N));
		criteriad.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), ropSeq));
		List<ConsultaMaterialRequisicaoVO> result2 = executeCriteria(criteriad);		
		List<ConsultaMaterialRequisicaoVO> listaUnion = new ArrayList<ConsultaMaterialRequisicaoVO>();
		listaUnion.addAll(result1);
		listaUnion.addAll(result2);
		return listaUnion;
	}
	public Object[] consultarInfPacienteProntuario(Short seqRequisicao) {		
		StringBuilder hql = new StringBuilder(500);		
		hql.append(" SELECT")
		.append(" PAC." ).append( AipPacientes.Fields.NOME.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.NOME_PACIENTE.toString())
		.append(", CNV." ).append( FatConvenioSaude.Fields.DESCRICAO.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.CONVENIO.toString())
		.append(", CSP." ).append( FatConvenioSaudePlano.Fields.DESCRICAO.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.PLANO_SAUDE.toString())
		.append(", PCI." ).append( MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.PROCEDIMENTO.toString())
		.append(", PAC." ).append( AipPacientes.Fields.PRONTUARIO.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.PRONTUARIO.toString())
		.append(", ATD." ).append( AghAtendimentos.Fields.LTO_LTO_ID.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.LEITO.toString())
		.append(", AGD." ).append( MbcAgendas.Fields.DT_AGENDA.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.DATA_PROCEDIMENTO.toString())
		.append(", IPH." ).append( FatItensProcedHospitalar.Fields.COD_TABELA.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.COD_PROCEDIMENTO_SUS.toString())
		.append(", IPH." ).append( FatItensProcedHospitalar.Fields.DESCRICAO.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.DESC_PROCEDIMENTO_SUS.toString())
		.append(", PES." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.MEDICO_REQUERENTE.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.CRIADO_EM.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.DATA_REQUISICAO.toString())
		.append(", ROP." ).append( MbcRequisicaoOpmes.Fields.JUST_REQUISICAO_OPME.toString() ).append( " as " ).append( VisualizarAutorizacaoOpmeVO.Fields.JUSTIFICATIVA.toString())		
		.append(" FROM ")
		.append(RapPessoasFisicas.class.getSimpleName() ).append( " PES, ")
		.append(RapServidores.class.getSimpleName() ).append( " SER, ")
		.append(FatConvenioSaude.class.getSimpleName() ).append( " CNV, ")
		.append(FatConvenioSaudePlano.class.getSimpleName() ).append( " CSP, ")
		.append(AipPacientes.class.getSimpleName() ).append( " PAC, ")
		.append(FatItensProcedHospitalar.class.getSimpleName() ).append( " IPH, ")
		.append(MbcProcedimentoCirurgicos.class.getSimpleName() ).append( " PCI, ")
		.append(MbcEspecialidadeProcCirgs.class.getSimpleName() ).append( " EPR, ")		
		.append(MbcAgendas.class.getSimpleName() ).append( " AGD, ")
		.append(MbcRequisicaoOpmes.class.getSimpleName() ).append( " ROP")		
		.append(" LEFT JOIN AGD." ).append( MbcAgendas.Fields.CIRURGIAS.toString() ).append( " as CRG")
		.append(" LEFT JOIN CRG." ).append( MbcCirurgias.Fields.ATENDIMENTO.toString() ).append( " as ATD")
		.append(" WHERE")
		.append(" ROP." ).append( MbcRequisicaoOpmes.Fields.AGENDA_SEQ.toString() ).append( " = AGD." ).append( MbcAgendas.Fields.SEQ.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.EPR_PCI_SEQ.toString() ).append( " = EPR." ).append( MbcEspecialidadeProcCirgs.Fields.PCI_SEQ.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.ESP_SEQ.toString() ).append( " = EPR." ).append( MbcEspecialidadeProcCirgs.Fields.ESP_SEQ.toString())
		.append(" AND EPR." ).append( MbcEspecialidadeProcCirgs.Fields.PCI_SEQ.toString() ).append( " = PCI." ).append( MbcProcedimentoCirurgicos.Fields.SEQ.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.IPH_PHO_SEQ.toString() ).append( " = IPH." ).append( FatItensProcedHospitalar.Fields.PHO_SEQ.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.IPH_SEQ.toString() ).append( " = IPH." ).append( FatItensProcedHospitalar.Fields.SEQ.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.PAC_CODIGO.toString() ).append( " = PAC." ).append( AipPacientes.Fields.CODIGO.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.CONVENIO_SAUDE_PLANO_CSP_CONV_SEQ.toString() ).append( " = CSP." ).append( FatConvenioSaudePlano.Fields.CNV_CODIGO.toString())
		.append(" AND AGD." ).append( MbcAgendas.Fields.CONVENIO_SAUDE_PLANO_CSP_SEQ.toString() ).append( " = CSP." ).append( FatConvenioSaudePlano.Fields.SEQ.toString())
		.append(" AND CSP." ).append( FatConvenioSaudePlano.Fields.CNV_CODIGO.toString() ).append( " = CNV." ).append( FatConvenioSaude.Fields.CODIGO.toString())
		.append(" AND ROP." ).append( MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString() ).append( " = SER." ).append( RapServidores.Fields.MATRICULA.toString())
		.append(" AND ROP." ).append( MbcRequisicaoOpmes.Fields.SER_VIN_CODIGO_CRIACAO.toString() ).append( " = SER." ).append( RapServidores.Fields.VIN_CODIGO.toString())
		.append(" AND SER." ).append( RapServidores.Fields.PES_CODIGO.toString() ).append( " = PES." ).append( RapPessoasFisicas.Fields.CODIGO.toString())
		.append(" AND ROP." ).append( MbcRequisicaoOpmes.Fields.ID.toString() ).append( " = :seqRequisicao");
		Query query = createHibernateQuery(hql.toString());
		query.setShort("seqRequisicao", seqRequisicao);		
		return (Object[]) query.uniqueResult();
	}
	public CancelamentoOpmeVO obterInformacoesEmail(Short requisicaoSeq) {
		StringBuffer sql = new StringBuffer(650);
		sql.append("SELECT ")
		.append("RAP_PESSOAS_FISICAS.NOME requerente, ")
		.append("AGH_WF_HIST_EXECUCOES.JUSTIFICATIVA justificativa ")
		.append("FROM AGH.AGH_WF_FLUXOS, AGH.MBC_REQUISICAO_OPMES , AGH.RAP_SERVIDORES , AGH.RAP_PESSOAS_FISICAS, AGH.AGH_WF_HIST_EXECUCOES ")
		.append("WHERE MBC_REQUISICAO_OPMES.SER_MATRICULA_CRIACAO = RAP_SERVIDORES.MATRICULA ")
		.append("AND MBC_REQUISICAO_OPMES.SER_VIN_CODIGO_CRIACAO = RAP_SERVIDORES.VIN_CODIGO ")
		.append("AND RAP_PESSOAS_FISICAS.CODIGO = RAP_SERVIDORES.PES_CODIGO ")
		.append("AND MBC_REQUISICAO_OPMES.WFL_SEQ = AGH_WF_FLUXOS.SEQ ")
		.append("AND AGH_WF_HIST_EXECUCOES.WFL_SEQ = AGH_WF_FLUXOS.SEQ ")
		.append("AND AGH_WF_HIST_EXECUCOES.ACAO = 'CANCELAMENTO' ")
		.append("AND MBC_REQUISICAO_OPMES.SEQ =  :requisicaoSeq ");
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("requisicaoSeq", requisicaoSeq);
		query.addScalar("requerente", StringType.INSTANCE);
		query.addScalar("justificativa",StringType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(CancelamentoOpmeVO.class));
		List<CancelamentoOpmeVO> lista = query.list();
		if(lista != null){if(lista.size() > 0){
				return (CancelamentoOpmeVO) lista.get(0);}
			}
		return null;
	}	
	public List<MbcRequisicaoOpmes> obterRequisicaoPorGrupoAlcadaSeq(Short grupoAlcadaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class);
		criteria.add(Restrictions.eq(MbcRequisicaoOpmes.Fields.GRUPO_ALCADA_SEQ.toString(), grupoAlcadaSeq));
		return executeCriteria(criteria);
	}
	@SuppressWarnings("unchecked")
	public List<Object[]> consultarInfAutorizadores(Short seqRequisicao) {
		StringBuilder sql = new StringBuilder(1000);
		sql.append(" SELECT pes.nome PES_NOME, oca.descricao OCA_DSCR, Max(wex.dt_execucao)    DTA_AUTR  "+ " FROM   agh.mbc_requisicao_opmes rop  "
				+ " JOIN agh.agh_wf_fluxos wfl ON rop.wfl_seq = wfl.seq  "+ " JOIN agh.agh_wf_etapas wet ON wfl.seq = wet.wfl_seq   "
				+ " JOIN agh.agh_wf_executores wex  ON wet.seq = wex.wet_seq "+ " JOIN agh.agh_wf_template_etapas wte  ON wet.wte_seq = wte.seq  "
				+ " JOIN agh.rap_servidores ser  ON wex.ser_matricula = ser.matricula  "+ " AND wex.ser_vin_codigo = ser.vin_codigo  "
				+ " JOIN agh.rap_pessoas_fisicas pes  ON ser.pes_codigo = pes.codigo  "+ " JOIN agh.rap_ocupacoes_cargo oca  ON ser.oca_car_codigo = oca.car_codigo "
				+ " AND ser.oca_codigo = oca.codigo "+ " WHERE  wte.codigo LIKE 'EM_AUTORIZACAO%' AND wex.dt_execucao IS NOT NULL AND rop.seq = :seqRequisicao"
				+ " GROUP  BY ser.matricula, ser.vin_codigo, pes.nome, oca.descricao "+ " ORDER  BY Max(wte.sequencia_base)  ");
		Query query = createSQLQuery(sql.toString());
		query.setShort("seqRequisicao", seqRequisicao);
		return query.list();
	}
	public Object[] obterObservacoesFluxoOpme(Integer agendaSeq) {
		StringBuilder sql = new StringBuilder(1500);
		sql.append(" SELECT agh.agh_unidades_funcionais.descricao Unidade, agh.mbc_agendas.dt_agenda data_agenda,  agh.agh_especialidades.sigla Especialidade, agh.rap_pessoas_fisicas.nome Equipe, agh.mbc_sala_cirurgicas.nome Sala, agh.aip_pacientes.prontuario prontuario "+ " FROM   agh.mbc_agendas  JOIN agh.agh_unidades_funcionais  ON agh.mbc_agendas.unf_seq = agh.agh_unidades_funcionais.seq  JOIN agh.agh_especialidades  ON agh.mbc_agendas.esp_seq = agh.agh_especialidades.seq "
				+ "        JOIN agh.rap_servidores  ON agh.mbc_agendas.puc_ser_vin_codigo = agh.rap_servidores.vin_codigo  AND agh.mbc_agendas.puc_ser_matricula = agh.rap_servidores.matricula "+ "        JOIN agh.rap_pessoas_fisicas  ON agh.rap_servidores.pes_codigo = agh.rap_pessoas_fisicas.codigo JOIN agh.mbc_sala_cirurgicas  ON agh.mbc_agendas.sci_seqp = agh.mbc_sala_cirurgicas.seqp "
				+ "             AND agh.mbc_agendas.unf_seq = agh.mbc_sala_cirurgicas.unf_seq  JOIN agh.aip_pacientes  ON agh.mbc_agendas.pac_codigo = agh.aip_pacientes.codigo "+ " WHERE  agh.mbc_agendas.seq = :agendaSeq ");
		Query query = createSQLQuery(sql.toString());
		query.setInteger("agendaSeq", agendaSeq);
		return (Object[]) query.uniqueResult();	
	}
}