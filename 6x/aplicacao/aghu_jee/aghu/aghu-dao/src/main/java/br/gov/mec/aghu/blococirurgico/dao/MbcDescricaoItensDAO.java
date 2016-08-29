package br.gov.mec.aghu.blococirurgico.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiasCursorCirVO;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

public class MbcDescricaoItensDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcDescricaoItens> {

	private static final long serialVersionUID = -3225974742807082303L;
	
	private static final Log LOG = LogFactory.getLog(MbcDescricaoItensDAO.class);

	/**
	 * Efetua busca de List<MbcDescricaoItens>
	 * Consulta C4 #18527
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @return
	 */
	public MbcDescricaoItens buscarDescricaoItens(Integer dcgCrgSeq, Short dcgSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoItens.class, "dti");
		criteria.add(Restrictions.eq("dti."+MbcDescricaoItens.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("dti."+MbcDescricaoItens.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.addOrder(Order.asc("dti."+MbcDescricaoItens.Fields.ASA.toString()));
		
		return (MbcDescricaoItens) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MbcDescricaoItens> buscarMbcDescricaoItens(Integer dcgCrgSeq, Short dcgSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoItens.class, "dti");
		criteria.add(Restrictions.eq("dti."+MbcDescricaoItens.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("dti."+MbcDescricaoItens.Fields.DCG_SEQP.toString(), dcgSeqp));
		
		criteria.addOrder(Order.asc("dti."+MbcDescricaoItens.Fields.ASA.toString()));
		
		return executeCriteria(criteria);
	}
	
	public LinhaReportVO obterDataInicioFimCirurgia(Integer crgSeq, Date dthrInicioCirg){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoItens.class, "dti");
		criteria.createAlias(MbcDescricaoItens.Fields.MBC_DESCRICAO_CIRURGICA.toString(), "dcg");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.min("dti." + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG)	, LinhaReportVO.Fields.DATA.toString())
				.add(Projections.max("dti." + MbcDescricaoItens.Fields.DTHR_FIM_CIRG)		, LinhaReportVO.Fields.DATA1.toString()));
		
		criteria.add(Restrictions.eq("dcg." + MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("dcg." + MbcDescricaoCirurgica.Fields.SITUACAO.toString(), DominioSituacaoDescricaoCirurgia.CON));
		criteria.add(Restrictions.ge("dti." + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG.toString(), dthrInicioCirg));
		criteria.add(Restrictions.isNotNull("dti." + MbcDescricaoItens.Fields.DTHR_FIM_CIRG.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		
		return (LinhaReportVO) executeCriteriaUniqueResult(criteria);
	}
	

	public List<MbcCirurgiasCursorCirVO> pesquisarOutrasDescricoesCirurgicasPorSala( final Integer dcgCrgSeq, 
																			 final Date dataCrg, 
																			 final Short unfSeq,
																			 final Short sciUnfSeq,
																			 final Short sciSeqp) {
		String aliasDti = "dti";
		String aliasDcg = "dcg";
		String aliasCrg = "crg";
		String aliasSci = "sci";
		String aliasPfd = "pfd";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String ponto = ".";

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoItens.class, aliasDti);
		
		Projection projection = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), MbcCirurgiasCursorCirVO.Fields.CRG_SEQ.toString())
				.add(Projections.property(aliasDti + ponto + MbcDescricaoItens.Fields.DCG_SEQP.toString()), MbcCirurgiasCursorCirVO.Fields.DCG_SEQP.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), MbcCirurgiasCursorCirVO.Fields.DATA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString()), MbcCirurgiasCursorCirVO.Fields.SCI_SEQP.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.PAC_CODIGO.toString()), MbcCirurgiasCursorCirVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property(aliasSci + ponto + MbcSalaCirurgica.Fields.NOME.toString()), MbcCirurgiasCursorCirVO.Fields.SALA.toString())
				.add(Projections.property(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG.toString()), MbcCirurgiasCursorCirVO.Fields.DTHR_INICIO_CIRG.toString())
				.add(Projections.property(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_FIM_CIRG.toString()), MbcCirurgiasCursorCirVO.Fields.DTHR_FIM_CIRG.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), MbcCirurgiasCursorCirVO.Fields.NOME_USUAL.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), MbcCirurgiasCursorCirVO.Fields.NOME.toString())
				.add(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.SER_MATRICULA_PROF.toString()), MbcCirurgiasCursorCirVO.Fields.SER_MATRICULA_PROF.toString())
				.add(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.toString()), MbcCirurgiasCursorCirVO.Fields.SER_VIN_CODIGO_PROF.toString());
		
		criteria.setProjection(projection);
		
		criteria.createAlias(aliasDti + ponto + MbcDescricaoItens.Fields.MBC_DESCRICAO_CIRURGICA.toString(), aliasDcg);
		criteria.createAlias(aliasDcg + ponto + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), aliasCrg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), aliasSci);
		criteria.createAlias(aliasDcg + ponto + MbcDescricaoCirurgica.Fields.MBC_PROF_DESCRICOES.toString(), aliasPfd);
		criteria.createAlias(aliasPfd + ponto + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dataCrg));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SCI_UNFSEQ.toString(), sciUnfSeq));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString(), sciSeqp));
		criteria.add(Restrictions.isNotNull(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG.toString()));
		criteria.add(Restrictions.isNotNull(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_FIM_CIRG.toString()));
		criteria.add(Restrictions.eq(aliasPfd + ponto + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), DominioTipoAtuacao.RESP));
		
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(aliasDti + ponto + MbcDescricaoItens.Fields.DCG_SEQP.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MbcCirurgiasCursorCirVO.class));
		
		return executeCriteria(criteria);
	}
	
	public MbcDescricaoItens buscarMbcDescricaoItensMaxMinHoraCirg(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoItens.class, "dti");
		criteria.createAlias(MbcDescricaoItens.Fields.MBC_DESCRICAO_CIRURGICA.toString(), "dcg");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.max("dti." + MbcDescricaoItens.Fields.DTHR_FIM_CIRG), MbcDescricaoItens.Fields.DTHR_FIM_CIRG.toString())
				.add(Projections.min("dti." + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG), MbcDescricaoItens.Fields.DTHR_INICIO_CIRG.toString()));
		
		criteria.add(Restrictions.eq("dcg." + MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.eq("dcg." + MbcDescricaoCirurgica.Fields.SITUACAO.toString(), DominioSituacaoDescricaoCirurgia.CON));
		Date d = null;
		
		try {
			d = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2004");
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}  
		
		criteria.add(Restrictions.ge("dti." + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG.toString(), d));
		criteria.add(Restrictions.isNotNull("dti." + MbcDescricaoItens.Fields.DTHR_FIM_CIRG.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MbcDescricaoItens.class));
		
		return (MbcDescricaoItens) executeCriteriaUniqueResult(criteria);
	}
}
