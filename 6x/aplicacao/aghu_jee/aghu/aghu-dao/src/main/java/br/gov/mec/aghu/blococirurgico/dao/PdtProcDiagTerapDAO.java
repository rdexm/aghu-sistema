package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PdtProcDiagTerapDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtProcDiagTerap> {
	

	private static final long serialVersionUID = 6698786129066467798L;

	public Long obterCountPdtProcDiagTerapPorPciSeq(Integer pciSeq) {
		String aliasPdt = "pdt";
		String aliasPci = "pci";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class, aliasPdt);
		criteria.createAlias(aliasPdt + separador + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO, aliasPci, Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(aliasPci + separador + MbcProcedimentoCirurgicos.Fields.SEQ, pciSeq));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<PdtProcDiagTerap> pesquisarProcDiagTerapPorPciSeq(Integer pciSeq) {
		String aliasPdt = "pdt";
		String aliasPci = "pci";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class, aliasPdt);
		criteria.createAlias(aliasPdt + separador + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO, aliasPci, Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(aliasPci + separador + MbcProcedimentoCirurgicos.Fields.SEQ, pciSeq));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteria);
	}

	/**
	 * #24711 C2 
	 */
	public Long pesquisarProcDiagTerapCount(Integer seq, String descricao, Short especialidade, DominioIndContaminacao contaminacao) {
		
		DetachedCriteria criteria = this.montarCriteriaParapesquisarProcDiagTerap(seq, descricao, especialidade, contaminacao);					
		
		return executeCriteriaCountDistinct(criteria,
				PdtProcDiagTerap.Fields.SEQ.toString(), true);	
	}

	public List<LinhaReportVO> pesquisarProcDiagTerap(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer seq, String descricao, Short especialidade, DominioIndContaminacao contaminacao) {
		
		DetachedCriteria criteria = this.montarCriteriaParapesquisarProcDiagTerap(seq, descricao, especialidade, contaminacao);		
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(
						Projections.property(PdtProcDiagTerap.Fields.SEQ.toString())), LinhaReportVO.Fields.NUMERO10.toString())
				.add(	Projections.property(PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO_SEQ.toString()), LinhaReportVO.Fields.NUMERO7.toString())		
				.add(	Projections.property(PdtProcDiagTerap.Fields.DESCRICAO.toString()), LinhaReportVO.Fields.TEXTO1.toString())		
				.add(	Projections.property(PdtProcDiagTerap.Fields.CONTAMINACAO.toString()), LinhaReportVO.Fields.DOMINIO1.toString())	 
				.add(	Projections.property(PdtProcDiagTerap.Fields.TEMPO_MINIMO.toString()), LinhaReportVO.Fields.NUMERO4.toString())	 
				.add(	Projections.property(PdtProcDiagTerap.Fields.EXAME_SIGLA.toString()), LinhaReportVO.Fields.TEXTO2.toString())	
				.add(	Projections.property(PdtProcDiagTerap.Fields.SITUACAO.toString()), LinhaReportVO.Fields.DOMINIO2.toString())
				.add(	Projections.property(PdtProcDiagTerap.Fields.CRIADO_EM.toString()), LinhaReportVO.Fields.DATA.toString())
				.add(	Projections.property(PdtProcDiagTerap.Fields.SERVIDOR_ID_MATRICULA.toString()), LinhaReportVO.Fields.NUMERO8.toString())
				.add(	Projections.property(PdtProcDiagTerap.Fields.SERVIDOR_ID_VINCODIGO.toString()), LinhaReportVO.Fields.NUMERO5.toString())
		);	
		
		criteria.addOrder(Order.asc(PdtProcDiagTerap.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);	
	}

	private DetachedCriteria montarCriteriaParapesquisarProcDiagTerap(Integer seq, String descricao, Short especialidade, DominioIndContaminacao contaminacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class);
		
		criteria.createAlias(PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), "procCirur");
		criteria.createAlias
			("procCirur."+MbcProcedimentoCirurgicos.Fields.ESPECIALIDADES_PROCS_CIRGS.toString(), "especProcsCirgs", Criteria.LEFT_JOIN);
		criteria.createAlias("especProcsCirgs."+MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "especialidade");		
		
		if(seq != null) {
			criteria.add(Restrictions.eq(PdtProcDiagTerap.Fields.SEQ.toString(), seq));
		}		
		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(PdtProcDiagTerap.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}		
		if(especialidade != null) {
			criteria.add(Restrictions.eq("especialidade."+AghEspecialidades.Fields.SEQ.toString(), especialidade));
		}		
		if(contaminacao != null) {
			criteria.add(Restrictions.eq(PdtProcDiagTerap.Fields.CONTAMINACAO.toString(), contaminacao));
		}	
		
		criteria.add(Restrictions.eq("especProcsCirgs."+MbcEspecialidadeProcCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	public List<PdtProcDiagTerap> listarProcDiagTerapAtivaPorDescricao(Object strPesquisa) {
		
		DetachedCriteria criteria = criarCriteriaListarProcDiagTerapAtivaPorDescricacao(strPesquisa);
		//criteria.addOrder(Order.asc());
		return executeCriteria(criteria, 0, 100, PdtProcDiagTerap.Fields.DESCRICAO.toString(), true);
	}
	
	public Long listarProcDiagTerapAtivaPorDescricaoCount(Object strPesquisa) {
		
		DetachedCriteria criteria = criarCriteriaListarProcDiagTerapAtivaPorDescricacao(strPesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListarProcDiagTerapAtivaPorDescricacao(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class);
		criteria.add(Restrictions.eq(PdtProcDiagTerap.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(PdtProcDiagTerap.Fields.SEQ.toString(), Integer.valueOf(strPesquisa.toString())));
		}else if(strPesquisa != null && !strPesquisa.toString().isEmpty()){
			criteria.add(Restrictions.ilike(PdtProcDiagTerap.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE));
		}
		return criteria;
	}

	/**
	 * #24713 C1 
	 */
	public List<PdtProcDiagTerap> pesquisarPdtProcDiagTerap(String strPesquisa) {
		DetachedCriteria criteria = this.montarCriteriaParapesquisarPdtProcDiagTerap(strPesquisa);
		//criteria.addOrder(Order.asc(PdtProcDiagTerap.Fields.SEQ.toString()));
		return executeCriteria(criteria, 0, 100, PdtProcDiagTerap.Fields.SEQ.toString(), true);
	}

	private DetachedCriteria montarCriteriaParapesquisarPdtProcDiagTerap(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				
				criteria.add(Restrictions.eq(PdtProcDiagTerap.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
				
			} else {
				criteria.add(Restrictions.ilike(PdtProcDiagTerap.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}			
		}		
		return criteria;
	}

	public Long pesquisarPdtGrupoPorIdDescricaoSituacaoCount(String strPesquisa) {
		DetachedCriteria criteria = this.montarCriteriaParapesquisarPdtProcDiagTerap(strPesquisa);
		return executeCriteriaCount(criteria);			
	}	
	
	public List<PdtProcDiagTerap> listarProcDiagTerap(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class);
		
		if (CoreUtil.isNumeroInteger(objPesquisa)) {
			criteria.add(Restrictions.eq(PdtProcDiagTerap.Fields.SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
		}else if(objPesquisa != null && !objPesquisa.toString().isEmpty()){
			criteria.add(Restrictions.ilike(PdtProcDiagTerap.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
		}

		return executeCriteria(criteria, 0, 100, PdtProcDiagTerap.Fields.DESCRICAO.toString(), true);	
	}
	
	public Long listarProcDiagTerapCount(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class);
		
		if (CoreUtil.isNumeroInteger(objPesquisa)) {
			criteria.add(Restrictions.eq(PdtProcDiagTerap.Fields.SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
		}else if(objPesquisa != null && !objPesquisa.toString().isEmpty()){
			criteria.add(Restrictions.ilike(PdtProcDiagTerap.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
		}
				
		return executeCriteriaCount(criteria);	
	}
	
	//Buscar os procedimentos realizados da PDT quando existir descrição
	public List<ProcedimentosPOLVO> pesquisarProcedimentosPDTComDescricaoPOL(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProcDiagTerap.class, "pdt");	

		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("crg." + MbcCirurgias.Fields.COD_PACIENTE.toString()),ProcedimentosPOLVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.DATA.toString()), ProcedimentosPOLVO.Fields.DATA.toString())
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ProcedimentosPOLVO.Fields.DESCRICAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SITUACAO.toString()), ProcedimentosPOLVO.Fields.SITUACAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.SEQ.toString())
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.EPR_PCI_SEQ.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString()), ProcedimentosPOLVO.Fields.TEM_DESCRICAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), ProcedimentosPOLVO.Fields.DIGITA_NOTA_SALA.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()),ProcedimentosPOLVO.Fields.ATD_SEQ.toString());

		criteria.setProjection(projection);	

		criteria.createAlias("pdt." + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci", Criteria.INNER_JOIN);
		criteria.createAlias("pdt." + PdtProcDiagTerap.Fields.PDT_PROCES.toString(), "pdc", Criteria.INNER_JOIN);
		criteria.createAlias("pdc." + PdtProc.Fields.PDT_DESCRICAO.toString(), "ddt", Criteria.INNER_JOIN);
		criteria.createAlias("ddt." + PdtDescricao.Fields.MBC_CIRURGIAS.toString(), "crg", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.and(Restrictions.eq("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString(), Boolean.TRUE),
				Restrictions.isNotNull("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString())));
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.COD_PACIENTE.toString(), codigo));	

		List<DominioSituacaoDescricao> situacoes = new ArrayList<DominioSituacaoDescricao>();
		situacoes.add(DominioSituacaoDescricao.DEF);
		situacoes.add(DominioSituacaoDescricao.PRE);	

		criteria.add(Restrictions.in("ddt." + PdtDescricao.Fields.SITUACAO.toString(), situacoes));
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), 
				DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO));	

		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosPOLVO.class));	

		return executeCriteria(criteria);
	}
}
