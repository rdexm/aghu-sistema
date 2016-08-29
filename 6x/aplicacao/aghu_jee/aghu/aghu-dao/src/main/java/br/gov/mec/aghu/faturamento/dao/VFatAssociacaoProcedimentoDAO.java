package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.vo.CursorVaprVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.sig.custos.vo.AssociacaoProcedimentoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class VFatAssociacaoProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VFatAssociacaoProcedimento>{
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = 4343420363915032506L;

	private DetachedCriteria obterDetachedCriteriaVFatAssociacaoProcedimento(){
		return DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);
	}
	
	public Long consultaQuantidadeProcedimentosConsultaPorPhiSeq(Integer phiSeq) throws BaseException{
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		AghParametros convenioHU = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_HU);
		AghParametros planoPadaro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PLANO_PADRAO);
		AghParametros tabela = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), planoPadaro.getVlrNumerico().shortValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), convenioHU.getVlrNumerico().byteValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_IND_CONSULTA.toString(), true));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), tabela.getVlrNumerico().shortValue()));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<Integer> getPHIs(Long codTabela, Short grupo,Short convenio, Short tabela, Byte[] planosSus) {

		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), codTabela));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), convenio));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), tabela));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupo));
		criteria.add(Restrictions.in(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), planosSus));
		
		criteria.setProjection(Projections.distinct(Projections.property(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())));

		List<Integer> results = executeCriteria(criteria);
		return results;
	}
	
	public Boolean consultarPorCodigoConvenioSeqConvenioSaudePlanoProcHospInterno(Short cnvCodigo, Byte cspSeq, Integer phiSeq){
		Boolean retorno = false;
		
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString(), phiSeq));
		
		List<VFatAssociacaoProcedimento> lista = executeCriteria(criteria, true);
		
		if(lista != null && lista.size() > 0){
			retorno = true;
		}
		
		return retorno;
	}

	protected Short obterAghuParamTipoGrpContaSus()
			throws ApplicationBusinessException {
	
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		Short result = Short.valueOf(parametro.getVlrNumerico().shortValue());
		
		return result;
	}

	protected DetachedCriteria obterCriteriaVFatAssociacaoProcedimentoPorConvenioConvenioSaudePlano(Short cnvCodigo, Byte cspSeq, String alias)
			throws ApplicationBusinessException {
	
		DetachedCriteria criteria = null;
		Short tipoGrpContaSus = null;
		
		tipoGrpContaSus = this.obterAghuParamTipoGrpContaSus();
		criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, alias);
		criteria.createAlias(alias + "." + VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString(), VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString());
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString() + "." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());
	
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString() + "." +FatConvenioSaudePlano.Fields.CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString() + "." +FatConvenioSaudePlano.Fields.SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString() + "." +FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		criteria.add(Restrictions.eq(alias + "." + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString() , tipoGrpContaSus));
		
		return criteria;
	}

	protected DetachedCriteria obterCriteriaVFatAssociacaoProcedimentoPorConvenioConvenioSaudePlanoEProcedHospInt(Short cnvCodigo, Byte cspSeq, Integer phiSeq)
			throws ApplicationBusinessException {
		
		DetachedCriteria criteria = obterCriteriaVFatAssociacaoProcedimentoPorConvenioConvenioSaudePlano(cnvCodigo, cspSeq, "ASS");
		criteria.add(Restrictions.eq("ASS." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString() , phiSeq));
		
		return criteria;
	}

	public VFatAssociacaoProcedimento buscarVFatAssociacaoProcedimentoPorConvenioConvenioSaudePlanoEProcedHospInt(Short cnvCodigo, Byte cspSeq, Integer phiSeq) throws ApplicationBusinessException{
		/**
		 * Esta consulta nao foi migrada exatamente igual ao que foi implementado no AGH. 
		 * Verificar se o resultado da execucao esta correto.
		 */
		DetachedCriteria criteria = obterCriteriaVFatAssociacaoProcedimentoPorConvenioConvenioSaudePlanoEProcedHospInt(cnvCodigo, cspSeq, phiSeq);
		List<VFatAssociacaoProcedimento> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	protected DetachedCriteria obterCriteriaPorConvenioConvenioSaudePlanoEListaProcedHospInt(Short cnvCodigo, Byte cspSeq, String caracteristica, Integer... listaPhiSeq)
			throws ApplicationBusinessException {
		
		DetachedCriteria result = null;
		DetachedCriteria sub = null;
		ProjectionList projLst = null;
		String ass = null;
		String tci = null;
		String cip = null;
		String iph = null;		
		
		ass = "ASS";
		tci = "TCI";
		cip = "CIP";
		iph = "IPH";
		// ASS
		result = this.obterCriteriaVFatAssociacaoProcedimentoPorConvenioConvenioSaudePlano(cnvCodigo, cspSeq, ass);
		result.createAlias(ass + "." + VFatAssociacaoProcedimento.Fields.IPH.toString(), iph);
		result.add(Restrictions.in(ass + "." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString() , listaPhiSeq));		
		// TCT
		sub = DetachedCriteria.forClass(FatCaractItemProcHosp.class, cip);
		sub.createAlias(cip + "." + FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(), tci);
		sub.add(Restrictions.eqProperty(cip + "." + FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(), ass + "." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString()));
		sub.add(Restrictions.eqProperty(cip + "." + FatCaractItemProcHosp.Fields.IPH_SEQ.toString(), ass + "." + VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString()));
		sub.add(Restrictions.eqProperty(cip + "." + FatCaractItemProcHosp.Fields.TCT_SEQ.toString(), tci + "." + FatTipoCaractItens.Fields.SEQ.toString()));
		sub.add(Restrictions.ilike(tci + "." + FatTipoCaractItens.Fields.CARACTERISTICA.toString(), caracteristica, MatchMode.EXACT));
		// sub
		sub.setProjection(Projections.property(cip + "." + FatCaractItemProcHosp.Fields.TCT_SEQ.toString()));
		result.add(Subqueries.exists(sub));
		// proj
		projLst = Projections.projectionList();
		projLst.add(Projections.property(ass + "." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())); 	
		result.setProjection(projLst);
		
		return result;
	}

	public List<Integer> listarPorConvenioConvenioSaudePlanoEListaProcedHospInt(Short cnvCodigo, Byte cspSeq, String caracteristica, Integer... phiSeq) throws ApplicationBusinessException{
		
		List<Integer> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorConvenioConvenioSaudePlanoEListaProcedHospInt(cnvCodigo, cspSeq, caracteristica, phiSeq);
		result = this.executeCriteria(criteria);
		
		return result; 
	}
	
	/**
	 * Primeiro VFatAssociacaoProcedimento filtrando por ConvenioSaudePlano = PLANO INTERNACAO,  
	 * também por ProcedHospInterno (phiSeq) que esteja Ativo e por ItemProcedHosp Ativo.
	 * @param phiSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public VFatAssociacaoProcedimento buscarPrimeiroPorConvSaudePlanoInternacaoEProcedHospInternoAtivoEIphAtivo(Integer phiSeq) throws ApplicationBusinessException{
		
		IParametroFacade aghuFacade = getParametroFacade();
		AghParametros paramCvnSus = aghuFacade.buscarAghParametro(AghuParametrosEnum.P_CODIGO_CONVENIO_SUS);
		AghParametros paramCvnSaudePlano = aghuFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SAUDE_PLANO_INTERNACAO);
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), paramCvnSus.getVlrNumerico().shortValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), paramCvnSaudePlano.getVlrNumerico().byteValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), parametro.getVlrNumerico().shortValue()));
		
		
		List<VFatAssociacaoProcedimento> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null; 
	}
	
	/** Busca Iph
	 * 
	 * @param phiSeq
	 * @param iphPhoSeq
	 * @param cnvCodigo
	 * @param plano
	 * @param iphIndInternacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public VFatAssociacaoProcedimento buscaIph(Integer phiSeq, Short iphPhoSeq, Short cnvCodigo, 
			Byte plano, Boolean iphIndInternacao) throws ApplicationBusinessException{
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), plano));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_IND_INTERNACAO.toString(), iphIndInternacao));
		
		return (VFatAssociacaoProcedimento) executeCriteriaUniqueResult(criteria); 
	}
	
	public VFatAssociacaoProcedimento buscaIphSms(Integer phiSeq, Short iphPhoSeq) throws ApplicationBusinessException{
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		
		List<VFatAssociacaoProcedimento> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		
		return null; 
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private DetachedCriteria obterProcedimentoSUSCriteria(Object objPesquisa, Integer phiSeq, Integer cthSeq, Boolean isProcHospSolictado, 
			Boolean isInternacao, Boolean isSituacao, DominioSituacaoItenConta situacaoConta[], Boolean filtroCodTabela) throws BaseException {
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		AghParametros convenioSUS = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
		AghParametros planoSUS = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		AghParametros grupoSUS = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), 
				VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), convenioSUS.getVlrNumerico().shortValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), planoSUS.getVlrNumerico().byteValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupoSUS.getVlrNumerico().shortValue()));

		if(phiSeq != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		}
		
		if(isSituacao) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_SITUACAO.toString(), DominioSituacao.A));
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SITUACAO.toString(), DominioSituacao.A));
		}
		
		if(isProcHospSolictado) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_SITUACAO.toString(), DominioSituacao.A));
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SITUACAO.toString(), DominioSituacao.A));
			
			if(filtroCodTabela) {
				if(CoreUtil.isNumeroLong(objPesquisa)) {
					if(objPesquisa instanceof Long) {
						criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), objPesquisa));
					}
					else {
						criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf((String)objPesquisa)));
					}
					
				} else {
					if(!StringUtils.isEmpty((String)objPesquisa)) {
						criteria.add(Restrictions.ilike(VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
					}
				}				
			} else {
				if(CoreUtil.isNumeroInteger(objPesquisa) || CoreUtil.isNumeroLong(objPesquisa)) {
					if(objPesquisa instanceof Integer) {
						criteria.add(Restrictions.or(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), objPesquisa),  Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf((Integer)objPesquisa))));
					}
					else if(objPesquisa instanceof Long){
						criteria.add(Restrictions.or(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), ((Long)objPesquisa).intValue()),  Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), objPesquisa)));
					}
					else {
						criteria.add(Restrictions.or(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), Integer.valueOf((String)objPesquisa)),  Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf((String)objPesquisa))));
					}
				} else {
					if(!StringUtils.isEmpty((String)objPesquisa)) {
						criteria.add(Restrictions.ilike(VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
					}
				}
			}
			
		} else {
			DetachedCriteria subquery = DetachedCriteria.forClass(FatItemContaHospitalar.class);
			subquery.setProjection(Projections.projectionList()
					.add(Projections.property(FatItemContaHospitalar.Fields.PHI_SEQ.toString())));
			subquery.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
			if(situacaoConta == null) {
				subquery.add(Restrictions.not(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), 
						new DominioSituacaoItenConta[] {DominioSituacaoItenConta.C, DominioSituacaoItenConta.R, DominioSituacaoItenConta.D})
						));
			} else {
				subquery.add(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), 
						situacaoConta));				
			}
			if(isInternacao != null) {
				criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_IND_INTERNACAO.toString(), isInternacao));
			}
			criteria.add(Subqueries.propertyIn(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), subquery));
			
			if(filtroCodTabela) {
				if(CoreUtil.isNumeroLong(objPesquisa)) {
					criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf((String)objPesquisa)));
				
				} else {
					if(!StringUtils.isEmpty((String)objPesquisa)) {
						criteria.add(Restrictions.ilike(VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
					}
				}
				
			} else {
				if(CoreUtil.isNumeroShort(objPesquisa) || CoreUtil.isNumeroLong(objPesquisa)) {
					if(objPesquisa instanceof Integer) {
						criteria.add(Restrictions.or(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), objPesquisa),  Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf((Integer)objPesquisa))));
						
					} else if(objPesquisa instanceof Long){
						criteria.add(Restrictions.or(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), ((Long)objPesquisa).intValue()),  Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), objPesquisa)));
						
					} else {
						criteria.add(Restrictions.or(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), Integer.valueOf((String)objPesquisa)),  Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf((String)objPesquisa))));
					}
					
				} else {
					if(!StringUtils.isEmpty((String)objPesquisa)) {
						criteria.add(Restrictions.ilike(VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
					}
				}
			}
		}
		
		return criteria;
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSItem(Object objPesquisa, Integer phiSeq, Integer cthSeq, Boolean isProcHospSolictado) throws BaseException {
		
		DetachedCriteria criteria = obterProcedimentoSUSCriteria(objPesquisa, phiSeq, cthSeq, isProcHospSolictado, null, true, 
												new DominioSituacaoItenConta[] { DominioSituacaoItenConta.A, 
																				 DominioSituacaoItenConta.C, 
																				 DominioSituacaoItenConta.P, 
																				 DominioSituacaoItenConta.V, 
																				 DominioSituacaoItenConta.R, 
																				 DominioSituacaoItenConta.N, 
																				 DominioSituacaoItenConta.D, 
																				 DominioSituacaoItenConta.T
																			    }, false);
				
		List<VFatAssociacaoProcedimento> lista = executeCriteria(criteria,0 , 100, null, false);
		
		return lista;
	}


	public Long listarAssociacaoProcedimentoSUSItemCount(Object objPesquisa, Integer phiSeq, Integer cthSeq, Boolean isProcHospSolictado) throws BaseException {
		
		DetachedCriteria criteria = obterProcedimentoSUSCriteria(objPesquisa, phiSeq, cthSeq, isProcHospSolictado, null, true, new DominioSituacaoItenConta[] {DominioSituacaoItenConta.A, DominioSituacaoItenConta.C}, false);
		
		return executeCriteriaCount(criteria);
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUS(Object objPesquisa, Integer phiSeq, Integer cthSeq, Boolean isProcHospSolictado) throws BaseException {
		
		DetachedCriteria criteria = obterProcedimentoSUSCriteria(objPesquisa, phiSeq, cthSeq, isProcHospSolictado, true, false, null, true);
				
		List<VFatAssociacaoProcedimento> lista = executeCriteria(criteria,0 , 100, null, false);
		
		if(lista == null || lista.isEmpty()) {
			lista = new ArrayList<VFatAssociacaoProcedimento>();
		}
		
		return lista;
	}

	public Long listarAssociacaoProcedimentoSUSCount(Object objPesquisa, Integer phiSeq, Integer cthSeq, Boolean isProcHospSolictado) throws BaseException {
		
		DetachedCriteria criteria = obterProcedimentoSUSCriteria(objPesquisa, phiSeq, cthSeq, isProcHospSolictado, true, false, null, true);
		
		return executeCriteriaCount(criteria);
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSPorPHI(Object objPesquisa, Integer phiSeq, Integer cthSeq, Boolean isProcHospSolictado) throws BaseException {
		
		DetachedCriteria criteria = obterProcedimentoSUSCriteria(objPesquisa, phiSeq, cthSeq, isProcHospSolictado, true, false, null, false);
				
		List<VFatAssociacaoProcedimento> lista = executeCriteria(criteria,0 , 100, null, false);
		
		if(lista == null || lista.isEmpty()) {
			lista = new ArrayList<VFatAssociacaoProcedimento>();
		}
		
		return lista;
	}

	public Long listarAssociacaoProcedimentoSUSPorPHICount(Object objPesquisa, Integer phiSeq, Integer cthSeq, Boolean isProcHospSolictado) throws BaseException {
		
		DetachedCriteria criteria = obterProcedimentoSUSCriteria(objPesquisa, phiSeq, cthSeq, isProcHospSolictado, true, false, null, false);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoParaExameMaterial(final Short cnvCodigo, final Byte cspSeq, final String sigla,
			final Integer manSeq, final Short tipoContaSus) {
		return executeCriteria(criarCriteriaListarAssociacaoProcedimentoParaExameMaterial(cnvCodigo, cspSeq, sigla, manSeq, tipoContaSus));
	}

	private DetachedCriteria criarCriteriaListarAssociacaoProcedimentoParaExameMaterial(final Short cnvCodigo, final Byte cspSeq, final String sigla,
			final Integer manSeq, final Short tipoContaSus) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "VAPR");
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP");
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString() + "." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CNV");
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI");
		
		criteria.add(Restrictions.eq("CNV."+ FatConvenioSaude.Fields.CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq("CSP."+ FatConvenioSaudePlano.Fields.SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq("PHI."+ FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq("PHI."+ FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString(), manSeq));
		if(tipoContaSus != null){
			criteria.add(Restrictions.eq("VAPR."+ VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), tipoContaSus));
		}
		criteria.add(Restrictions.eq("CNV."+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		criteria.add(Restrictions.eq("CSP."+ FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.A));
		
		return criteria;
	}
	
	public VFatAssociacaoProcedimento obterFatProcedHospIntPorExameMaterialConvCspIphPhoSeq(final String sigla,
			final Integer manSeq, final Short iphPhoSeq, final Short cnvCodigo, final Byte cspSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "V");
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI");
		criteria.add(Restrictions.eq("PHI."+ FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq("PHI."+ FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("PHI."+ FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("V."+ VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("V."+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq("V."+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		
		return (VFatAssociacaoProcedimento) executeCriteriaUniqueResult(criteria);
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoParaCaracteristica(Short cnvCodigo, Byte cspSeq, Integer phiSeq, Short grcSeq) {
		
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString(), VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString());
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString()+"."+FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString()+"."+FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString()+"."+FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString()+"."+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CONVENIO_SAUDE_PLANO.toString()+"."+FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(),DominioTipoPlano.A));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		return executeCriteria(criteria);
	}

	protected IParametroFacade getParametroFacade() {
		//return (ParametroFacade)Component.getInstance(ParametroFacade.class, true);
		return aIParametroFacade;
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(Object objPesquisa, final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq, final Byte cpgCphCspSeq) throws ApplicationBusinessException {
		DetachedCriteria criteria = obterCriteriaListarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(objPesquisa, cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq);
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "procedimentoHospitalarInterno",JoinType.LEFT_OUTER_JOIN);
		criteria.addOrder(Order.asc(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		return executeCriteria(criteria, 0, 100, new HashMap<String, Boolean>());
	}

	public Long listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(Object objPesquisa, final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq, final Byte cpgCphCspSeq) throws ApplicationBusinessException {
		final DetachedCriteria criteria = obterCriteriaListarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(objPesquisa, cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaListarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(final Object objPesquisa, final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq, final Byte cpgCphCspSeq) throws ApplicationBusinessException {
		final DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), cpgGrcSeq));
			
		if(objPesquisa != null){
			if(CoreUtil.isNumeroLong(objPesquisa) || CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add( Restrictions.or(
												Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf(objPesquisa.toString())),
												Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), Integer.valueOf(objPesquisa.toString()))
											 )
							);
				
			} else if(!StringUtils.isEmpty(objPesquisa.toString())) {
				criteria.add(Restrictions.ilike(VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	public List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimento(Integer phiSeq, Short cpgCphCspCnvCodigo,
			Byte cpgCphCspSeq, Short cpgGrcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), cpgGrcSeq));

		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "procedimentoHospitalarInterno",JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria);
	}
	
	public List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimento(Integer phiSeq, Short cpgCphCspCnvCodigo,
			Byte cpgCphCspSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));

		return executeCriteria(criteria);
	}
	
	public List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimentoOutrosProcedimentos(Integer conNumero, Integer phiSeq, 
			Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Short cpgGrcSeq) {
		final String vFatAlias = "VAS";
		final String aacConAlias = "PRH";
		final String sqlSeparador = ".";

		DetachedCriteria subquery = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class, aacConAlias);
		subquery.setProjection(Projections.property(aacConAlias + sqlSeparador + AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString()));
		subquery.add(Restrictions.eq(aacConAlias + sqlSeparador + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), conNumero));
		subquery.add(Restrictions.ne(aacConAlias + sqlSeparador + AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString(), phiSeq));

		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, vFatAlias);
		criteria.add(Restrictions.eq(vFatAlias + sqlSeparador + VFatAssociacaoProcedimento.Fields.IPH_IND_CONSULTA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(vFatAlias + sqlSeparador + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), cpgGrcSeq));
		criteria.add(Restrictions.eq(vFatAlias + sqlSeparador + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), Short.valueOf("1")));
		criteria.add(Subqueries.propertyIn(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), subquery));

		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "procedimentoHospitalarInterno",JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria);
	}

	public List<VFatAssociacaoProcedimento> pesquisarAssociacoesProcedimentos(String filtro, Integer phiSeq, Boolean indInternacaoIph,
			Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, DominioSituacao indSituacaoPhi, DominioSituacao indSituacaoIph,
			Short tipoGrupoContaSUS, Short iphPhoSeq) {
		DetachedCriteria criteria = this.createCriteria(filtro, phiSeq, indInternacaoIph, cpgCphCspCnvCodigo,
				cpgCphCspSeq, indSituacaoPhi, indSituacaoIph, tipoGrupoContaSUS, iphPhoSeq);
		
		criteria.addOrder(Order.asc(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		return executeCriteria(criteria, 0, 25, null, false);
	}

	public Long pesquisarAssociacoesProcedimentosCount(String filtro, Integer phiSeq, Boolean indInternacaoIph, Short cpgCphCspCnvCodigo,
			Byte cpgCphCspSeq, DominioSituacao indSituacaoPhi, DominioSituacao indSituacaoIph, Short tipoGrupoContaSUS, Short iphPhoSeq) {
		DetachedCriteria criteria = this.createCriteria(filtro, phiSeq, indInternacaoIph, cpgCphCspCnvCodigo,
				cpgCphCspSeq, indSituacaoPhi, indSituacaoIph, tipoGrupoContaSUS, iphPhoSeq);
		return executeCriteriaCount(criteria);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria createCriteria(String filtro, Integer phiSeq, Boolean indInternacaoIph, Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq,
			DominioSituacao indSituacaoPhi, DominioSituacao indSituacaoIph, Short tipoGrupoContaSUS, Short iphPhoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);
		
		if (indInternacaoIph != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_IND_INTERNACAO.toString(), indInternacaoIph));
		}
		
		if (cpgCphCspCnvCodigo != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		}
		
		if (cpgCphCspSeq != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		}
		
		if (indSituacaoPhi != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_IND_SITUACAO.toString(), indSituacaoPhi));
		}
		
		if (indSituacaoIph != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString(), indSituacaoIph));
		}
		
		if (tipoGrupoContaSUS != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), tipoGrupoContaSUS));
		}
		
		if (phiSeq != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		}
		
		if (iphPhoSeq != null) {
			criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString() , iphPhoSeq));
		}
		
		if (StringUtils.isNotBlank(filtro)) {
			Criterion phiDescricaoCriterion = Restrictions.ilike(VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString(), filtro,
					MatchMode.ANYWHERE);
			Criterion iphDescricaoCriterion = Restrictions.ilike(VFatAssociacaoProcedimento.Fields.IPH_DESCRICAO.toString(), filtro,
					MatchMode.ANYWHERE);

			Criterion criterionOrDescricoes = Restrictions.or(phiDescricaoCriterion, iphDescricaoCriterion);

			Criterion phiSeqCriterion = null;
			Criterion iphSeqCriterion = null;
			Criterion codTabelaCriterion = null;
			if (CoreUtil.isNumeroInteger(filtro)) {
				phiSeqCriterion = Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), Integer.valueOf(filtro));
				iphSeqCriterion = Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString(), Integer.valueOf(filtro));
				codTabelaCriterion = Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf(filtro));

				criteria.add(Restrictions.or(phiSeqCriterion,
						Restrictions.or(iphSeqCriterion, Restrictions.or(codTabelaCriterion, criterionOrDescricoes))));
			} else if (CoreUtil.isNumeroLong(filtro)) {
				codTabelaCriterion = Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), Long.valueOf(filtro));
				criteria.add(Restrictions.or(codTabelaCriterion, criterionOrDescricoes));
			} else {
				criteria.add(criterionOrDescricoes);
			}
		}
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "procedimentoHospitalarInterno",JoinType.LEFT_OUTER_JOIN);
		return criteria;
	}
	

	
	/**
	 * Metodo para filtrar VFatAssociacaoProcedimento pelo PHI, conforme os seguintes parametros:
	 * P_CONVENIO_SUS,
	 * P_SUS_PLANO_INTERNACAO,
	 * P_TABELA_FATUR_PADRAO,
	 * P_TIPO_GRUPO_CONTA_SUS
	 * 
	 * @param phiSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimentoPorProcedHospInt(Integer phiSeq) throws ApplicationBusinessException{
		
		
		AghParametros pConvenioSus = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
		AghParametros pSusPlanoInternacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		AghParametros pTabelaFaturPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		AghParametros pTipoGrupoContaSus = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString() , phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString() , pConvenioSus.getVlrNumerico().shortValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString() , pSusPlanoInternacao.getVlrNumerico().byteValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString() , pTabelaFaturPadrao.getVlrNumerico().shortValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString() , pTipoGrupoContaSus.getVlrNumerico().shortValue()));
		
		return executeCriteria(criteria); 
	}
	
	/**
	 * Busca o cod_tabela para um IPH - Report laudos
	 * 
	 * @param phiSeq
	 * @param cnvCodigo
	 * @param valorGrupoContaSUS
	 * @return
	 */
	public Map<String, Object> obterCodTabelaParaIPH(Integer phiSeq, Short cnvCodigo, Short valorGrupoContaSUS) {
		StringBuilder hql = new StringBuilder(220);
		hql.append("select VAPR.")
		.append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString())
		.append(",VAPR.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ)
		.append(",VAPR.").append(VFatAssociacaoProcedimento.Fields.IPH_IND_COBRANCA_DIARIA.toString())
		.append(" from ")
		.append(VFatAssociacaoProcedimento.class.getSimpleName()).append(" VAPR ")
		.append(',').append(FatProcedHospInternos.class.getSimpleName()).append(" PHI ")
		
		.append(" where ")
		.append(" PHI.")
		.append(FatProcedHospInternos.Fields.SEQ.toString())
		.append(" = :phiSeq")
		.append(" and ")
		.append(" VAPR.")
		.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())
		.append(" = CASE WHEN PHI.").append(FatProcedHospInternos.Fields.PHI_SEQ.toString()).append(" IS NULL ")
		.append(" THEN ")
		.append(" PHI.").append(FatProcedHospInternos.Fields.SEQ.toString())
		.append(" ELSE ")
		.append(" PHI.").append(FatProcedHospInternos.Fields.PHI_SEQ.toString())
		.append(" END ")
		.append(" and ")
		.append(" VAPR.")
		.append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString())
		.append(" = :cnvCodigo")
		.append(" and ")
		.append(" VAPR.").append(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString())
		.append(" = :vlrGrpCtaSus");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("vlrGrpCtaSus", valorGrupoContaSUS);
		
		List<Object[]> result = query.getResultList();

		if(result.isEmpty()) {
			return null;
		} else {
			Object[] obj = result.get(0);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("iphPhoSeq", obj[0]);
			map.put("iphSeq", obj[1]);
			map.put("indCobrancaDiaria", obj[2]);

			return map;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CursorVaprVO> listarAssociacaoProcedimento(Short cnvCodigo, Byte cspSeq, Integer phiSeq, Short tipoGrupoContaSus) {
		StringBuilder hql = new StringBuilder(350);

		hql.append(" select vapr.");
		hql.append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString());
		hql.append(" as iphPhoSeq");

		hql.append(", vapr.");
		hql.append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString());
		hql.append(" as iphSeq");


		// from
		hql.append(" from ");
		hql.append(VFatAssociacaoProcedimento.class.getName());
		hql.append(" as vapr");
		hql.append(", ").append(FatProcedHospInternos.class.getName());
		hql.append(" as phi ");
		hql.append(", ").append(FatConvenioSaudePlano.class.getName());
		hql.append(" as csp ");
		hql.append(", ").append(FatConvenioSaude.class.getName());
		hql.append(" as cnv ");

		// where
		hql.append(" where cnv.");
		hql.append(FatConvenioSaude.Fields.CODIGO.toString());
		hql.append(" = :cnvCodigo");

		hql.append(" and cnv.");
		hql.append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString());
		hql.append(" = :grupoConvenio");

		hql.append(" and csp.");
		hql.append(FatConvenioSaudePlano.Fields.CODIGO.toString());
		hql.append(" = cnv.");
		hql.append(FatConvenioSaude.Fields.CODIGO.toString());

		hql.append(" and csp.");
		hql.append(FatConvenioSaudePlano.Fields.SEQ.toString());
		hql.append(" = :cspSeq");

		hql.append(" and csp.");
		hql.append(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString());
		hql.append(" = :indTipoPlano");

		hql.append(" and phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" = :phiSeq");

		hql.append(" and vapr.");
		hql.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString());
		hql.append(" = phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());

		hql.append(" and vapr.");
		hql.append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = cnv.");
		hql.append(FatConvenioSaude.Fields.CODIGO.toString());

		hql.append(" and vapr.");
		hql.append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = csp.");
		hql.append(FatConvenioSaudePlano.Fields.SEQ.toString());

		hql.append(" and vapr.");
		hql.append(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :tipoGrupoContaSus");


		// query
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("grupoConvenio", DominioGrupoConvenio.S);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("indTipoPlano", DominioTipoPlano.A);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("tipoGrupoContaSus", tipoGrupoContaSus);
		
		query.setResultTransformer(Transformers.aliasToBean(CursorVaprVO.class));

		return query.list();
	}
	
	/**
	 * ORADB: RN_CTHP_VER_INS_CID.C_ICH_REALIZADOS
	 * DAOTest: VFatAssociacaoProcedimentoDAOTest.obterMenorIchRealizados (Retestar qd alterado) eSchweigert 17/09/2012|
	 */
	public List<Integer> obterMenorIchRealizados( final Integer cthSeq, 		  final Short cpgGrcSeq, 
												  final Boolean cirurgiaMultipla, final DominioSituacaoItenConta indSituacao,
												  final String  codRegistro){

		final StringBuilder sql = new StringBuilder(1250);
		
		sql.append("SELECT ICH.PHI_SEQ AS PHI_SEQ")
		   .append(" FROM ")
		   .append("  AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VAP_R, ")
		   .append("  AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append("     ICH, ")
		   .append("  AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append("   IPH_R, ")
		   .append("  AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VAPR, ")
		   .append("  AGH.").append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append("      CTH ")
		   .append(" WHERE ")
		   .append( "      CTH.SEQ = :P_CTH_SEQ ")
		   .append( "  AND VAPR.PHI_SEQ = CTH.PHI_SEQ_REALIZADO ")
		   .append( "  AND VAPR.CPG_CPH_CSP_CNV_CODIGO =  CTH.CSP_CNV_CODIGO ")
		   .append( "  AND VAPR.CPG_CPH_CSP_SEQ =  CTH.CSP_SEQ ")
		   .append( "  AND VAPR.CPG_GRC_SEQ  = :P_TIPO_GRUPO_CONTA_SUS ")
		   .append( "  AND IPH_R.PHO_SEQ = VAPR.IPH_PHO_SEQ ")
		   .append( "  AND IPH_R.SEQ  = VAPR.IPH_SEQ ")
		   .append( "  AND IPH_R.IND_CIRURGIA_MULTIPLA = :P_CIRURGIA_MULTIPLA")
		   .append( "  AND ICH.CTH_SEQ = CTH.SEQ ")
		   .append( "  AND ICH.IND_SITUACAO = :P_ICH_IND_SITUACAO ")
		   .append( "  AND VAP_R.PHI_SEQ = ICH.PHI_SEQ ")
		   .append( "  AND VAP_R.CPG_CPH_CSP_CNV_CODIGO =  CTH.CSP_CNV_CODIGO ")
		   .append( "  AND VAP_R.CPG_CPH_CSP_SEQ = CTH.CSP_SEQ ")
		   .append( "  AND VAP_R.CPG_GRC_SEQ  = :P_TIPO_GRUPO_CONTA_SUS ")
		   
		   // and  fatc_busca_instr_reg(vap_r.iph_SEQ,vap_r.IPH_PHO_SEQ,'03') = 'S'
		   .append("   AND ( " )
		   .append(" 		 CASE WHEN (" )
           .append("    		SELECT FPRIN.COD_REGISTRO " ) // FPRIN.COD_REGISTRO 
           .append("      		 FROM 	AGH.FAT_PROCEDIMENTOS_REGISTRO FPRIN" )
           .append("      		 WHERE  FPRIN.IPH_PHO_SEQ     = VAP_R.IPH_PHO_SEQ" )
           .append("           		AND FPRIN.IPH_SEQ      = VAP_R.IPH_SEQ  " )
           .append("           		AND FPRIN.COD_REGISTRO = :P_FPRIN_COD_REGISTRO " )
           .append("   					) IS NOT NULL THEN 'S'" )
           .append("             				  ELSE 'N' " )
           .append("  		  END ")
           .append("       ) = 'S' " );
					 
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setParameter("P_CTH_SEQ", cthSeq);
		query.setParameter("P_TIPO_GRUPO_CONTA_SUS", cpgGrcSeq);
		query.setParameter("P_CIRURGIA_MULTIPLA", Boolean.TRUE.equals(cirurgiaMultipla) ? "S" : "N");
		query.setParameter("P_ICH_IND_SITUACAO", indSituacao.toString());
		query.setParameter("P_FPRIN_COD_REGISTRO", codRegistro);
		
		return query.addScalar("PHI_SEQ", IntegerType.INSTANCE).list();
	}
	
	public List<VFatAssociacaoProcedimento> pesquisarVFatAssociacaoProcedimentoAtivosPorPhi(Integer phiSeq) throws ApplicationBusinessException{
		
		AghParametros pTabelaFaturPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString() , phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString() , pTabelaFaturPadrao.getVlrNumerico().shortValue()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString() , DominioSituacao.A));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SITUACAO.toString() , DominioSituacao.A));

		criteria.addOrder(Order.asc(VFatAssociacaoProcedimento.Fields.IPH_DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	

	/**
	 * Pesquisa 
	 * @param cnvSeq
	 * @param cspSeq
	 * @param phiSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<VFatAssociacaoProcedimento> pesquisarVFatTipoGrupoContaSusPorCnvCspPhi(Short cnvSeq, Byte cspSeq, Integer phiSeq) throws ApplicationBusinessException {

		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();

		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));

		AghParametros pTabelaFaturPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), pTabelaFaturPadrao.getVlrNumerico().shortValue()));

		return executeCriteria(criteria);
	}
	
	public Integer obterClinica(final Integer phiRealizado) {
		DetachedCriteria criteria = obterDetachedCriteriaVFatAssociacaoProcedimento();
		criteria.createAlias(VFatAssociacaoProcedimento.Fields.IPH.toString(), "IPH", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.property("IPH." + FatItensProcedHospitalar.Fields.CLINICA_CODIGO.toString()));
		
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiRealizado));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), Short.valueOf("12")));
		
		List<Integer> result = executeCriteria(criteria);
		
		if(result != null && result.size() > 0) {
			return result.get(0);
		}

		return null;
	}
	
	
	/**
	 * 42803
	 * C9 
	 * @return
	 */
	public VFatAssociacaoProcedimento obterVFatAssociacaoProcedimento(Integer pacCodigo, String candidato, Long fatProcedAmbRealizadosSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "v");
		criteria.createAlias("v." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), "caot");
		
		criteria.add(Restrictions.eq("caot." + FatCandidatosApacOtorrino.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eqProperty("caot." + FatCandidatosApacOtorrino.Fields.PHI_SEQ_SUGERIDO.toString(), "v." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		criteria.add(Restrictions.eq("v." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), 12));
		criteria.add(Restrictions.eq("caot." + FatCandidatosApacOtorrino.Fields.CANDIDATO.toString(), candidato));
		criteria.add(Restrictions.eq("v." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), fatProcedAmbRealizadosSeq));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("v." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()));
		projList.add(Projections.property("v." + VFatAssociacaoProcedimento.Fields.IPH_DESCRICAO.toString()));
		criteria.setProjection(Projections.distinct(projList));
		
		return (VFatAssociacaoProcedimento) executeCriteriaUniqueResult(criteria);
	}

	public List<AssociacaoProcedimentoVO> obterValoresProcedimentosAtravesRespectivosCodigosSus(Set<Integer> listaPhi, Set<Long> listaCodTabela) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);
		
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.distinct(Projections.property(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())), AssociacaoProcedimentoVO.Fields.PHI_SEQ.toString())
				.add(Projections.property(VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString()), AssociacaoProcedimentoVO.Fields.PHI_DESCRICAO.toString())
				.add(Projections.property(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()), AssociacaoProcedimentoVO.Fields.COD_TABELA.toString());
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.in(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), listaPhi));
		criteria.add(Restrictions.in(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), listaCodTabela));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AssociacaoProcedimentoVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * C7/C21 Melhoria da 42801/42803
	 * Consulta usada para popular os campos 10 e 11 quando aparelho
	 * @param phiSeq - Obtido na C6/C20
	 * @param iphPhoSeq - Obtido pelo PS10
	 */
	public VFatAssociacaoProcedimento obterCodigoTabelaEDescricao(Integer phiSeq, Short iphPhoSeq, Short convenioSus, Byte planoSus, Short cpgGrcSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class,"VFAT");
		criteria.add(Restrictions.eq("VFAT." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq("VFAT." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(),  convenioSus));
		criteria.add(Restrictions.eq("VFAT." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(),  planoSus));
		criteria.add(Restrictions.eq("VFAT." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("VFAT." + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), cpgGrcSeq));
		return (VFatAssociacaoProcedimento) executeCriteriaUniqueResult(criteria);
		
	}
	
	public VFatAssociacaoProcedimento obterDescricaoPorCodTabela(Long codTabela){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class,"VFAT");
		criteria.add(Restrictions.eq("VFAT." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), codTabela));
		List<VFatAssociacaoProcedimento> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()){
			return lista.get(0);
		}
		return null;
	}

}
