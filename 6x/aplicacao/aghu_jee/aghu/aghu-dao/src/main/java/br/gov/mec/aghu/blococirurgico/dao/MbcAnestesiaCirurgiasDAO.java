package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProdutividadePorAnestesistaConsultaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;

public class MbcAnestesiaCirurgiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAnestesiaCirurgias> {

	private static final long serialVersionUID = 3089567205523552653L;

	public List<String> pesquisaTiposAnestesia(Integer seq, String stringSeparator) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class);
		criteria.createAlias(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(),
				MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString());
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString() + stringSeparator
						+ MbcTipoAnestesias.Fields.DESCRICAO.toString())));
		criteria.add(Restrictions.eq(MbcAnestesiaCirurgias.Fields.SEQ.toString(), seq));

		return executeCriteria(criteria);
	}
	
	/**
	 * Lista Tipos de anestesia que necessitam anestesista
	 * 
	 * @param crgSeq
	 * @param necessitaAnestesista
	 * @return
	 */
	public List<MbcTipoAnestesias> listarTipoAnestesias(Integer crgSeq, Boolean necessitaAnestesista){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class);		
		criteria.add(Restrictions.eq(MbcAnestesiaCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
		criteria.createAlias(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(), MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString());		
		criteria.add(Restrictions.eq(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA_NESS_ANEST.toString(), necessitaAnestesista));
		criteria.setProjection(Projections.property(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString()));
		return executeCriteria(criteria);
	}
	
	public List< MbcAnestesiaCirurgias> pesquisarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(Integer crgSeq, DominioSituacao situacao) {
		String aliasTan = "tan";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class);
		
		criteria.createAlias(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(), aliasTan);
		
		criteria.add(Restrictions.eq(MbcAnestesiaCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
		
		if(situacao != null){
			criteria.add(Restrictions.eq(aliasTan + separador + MbcTipoAnestesias.Fields.SITUACAO.toString(), situacao));
		}
		
		return executeCriteria(criteria);
	}
	
	public List< MbcAnestesiaCirurgias> pesquisarAnestesiaCirurgiaTipoAnestesiaAtivoPorCrgSeq(Integer crgSeq) {
		return pesquisarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(crgSeq, DominioSituacao.A);
	}
	
	public List<MbcAnestesiaCirurgias> listarTipoAnestesiasPorCrgSeq(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class);		
		criteria.add(Restrictions.eq(MbcAnestesiaCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
		return executeCriteria(criteria);
	}
	
	public List<MbcAnestesiaCirurgias> listarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class);		
		
		criteria.createAlias(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(), "tan");
		criteria.add(Restrictions.eq(MbcAnestesiaCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
	
		return executeCriteria(criteria);
	}
	
	public List<MbcAnestesiaCirurgias> pesquisarAnestesiaNecessidadeAnestesista(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class);		
		criteria.createAlias(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(), "tan");
		criteria.add(Restrictions.eq(MbcAnestesiaCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
		criteria.add(Restrictions.eq("tan." + MbcTipoAnestesias.Fields.IND_NESS_ANES.toString(), Boolean.TRUE));		
		return executeCriteria(criteria);
	}

	/**
	 * Lista tipos de anestesia das unidades executoras de um período, com exceção das cirurgias canceladas 
	 * 
	 * @param dataInicial, dataFinal
	 * @param unidadeExecutora
	 * @return
	 */
	public List<RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO> listarTipoAnestesiasCirurgiasNaoCanceladasPorPeriodo(
			Date dataInicial, Date dataFinal, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.count(MbcAnestesiaCirurgias.Fields.SEQ.toString()), RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO.Fields.QTDE_TIPO_ANESTESIA.toString())
				.add(Projections.groupProperty("tan."+MbcTipoAnestesias.Fields.DESCRICAO.toString()), RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO.Fields.TIPO_ANESTESIA.toString())
				.add(Projections.groupProperty("agh."+AghUnidadesFuncionais.Fields.SIGLA.toString()), RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO.Fields.UNIDADE.toString())
				);
		
		criteria.createAlias(MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(), "tan");
		criteria.createAlias(MbcAnestesiaCirurgias.Fields.CIRURGIA.toString(), "crg");
		criteria.createAlias("crg." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "agh");
		criteria.createAlias("agh." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "car");
		
		criteria.add(Restrictions.ne("crg." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.between("crg." + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		
		if(constanteAghCaractUnidFuncionais != null){
			criteria.add(Restrictions.eq("car." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),
					constanteAghCaractUnidFuncionais));
		}
		
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO.class));
		
		return executeCriteria(criteria);
	}
	
 	public List<RelatorioProdutividadePorAnestesistaConsultaVO> listarCirurgiasProdutividadeAnestesiaTipo(Short unfSeq, Date dataInicio, Date dataFim, DominioFuncaoProfissional dominioFuncaoProfissional, Integer matricula, Short vinCodigo) {
 		Projection projection;
 		if(vinCodigo != null && matricula != null){
			projection = Projections.projectionList()
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.IND_FUNCAO_PROF.toString())
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.VIN_CODIGO.toString())
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.MATRICULA.toString())
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.PUC_UNF_SEQ.toString())
			.add(Projections.groupProperty("tam."+MbcTipoAnestesias.Fields.SEQ.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.TAM_SEQ.toString())
			.add(Projections.groupProperty("tam."+MbcTipoAnestesias.Fields.DESCRICAO.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.DESCRICAO.toString())
			.add(Projections.rowCount(), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QUANTIDADE.toString());
 		}else{
			projection = Projections.projectionList()
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.IND_FUNCAO_PROF.toString())
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.PUC_UNF_SEQ.toString())
			.add(Projections.groupProperty("tam."+MbcTipoAnestesias.Fields.SEQ.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.TAM_SEQ.toString())
			.add(Projections.groupProperty("tam."+MbcTipoAnestesias.Fields.DESCRICAO.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.DESCRICAO.toString())
			.add(Projections.rowCount(), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QUANTIDADE.toString());
 		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaCirurgias.class, "anc");
		criteria.createAlias("anc."+MbcAnestesiaCirurgias.Fields.CIRURGIA.toString(), "crg");
		criteria.createAlias("crg."+MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "pcg");
		criteria.createAlias("anc."+MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(), "tam");
		
 		criteria.add(Restrictions.ne("crg."+MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
 		criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), dominioFuncaoProfissional));
 		criteria.add(Restrictions.eq("crg."+MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
 		criteria.add(Restrictions.between("crg." + MbcCirurgias.Fields.DATA.toString(), dataInicio, dataFim));
 		if(vinCodigo != null && matricula != null){
 			criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString(), matricula));
 			criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigo));
 		}
 		
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioProdutividadePorAnestesistaConsultaVO.class));
 		
 		return executeCriteria(criteria);
 	}
}
