package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuHabitoAlimAtendimento;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CuidadosEnfermagemVO;
import br.gov.mec.aghu.sig.custos.vo.DietaPrescritaVO;
import br.gov.mec.aghu.sig.custos.vo.ExamesInternacaoVO;
import br.gov.mec.aghu.sig.custos.vo.SigObjetoCustoPhisVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SigObjetoCustoPhisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoPhis> {

	private static final long serialVersionUID = 8810968457442273876L;

	public List<SigObjetoCustoPhis> pesquisarPhiPorObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoPhis.class);
		criteria.setFetchMode(SigObjetoCustoPhis.Fields.FAT_PHI.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), seqObjetoCustoVersao));
		return executeCriteria(criteria);
	}

	public List<ExamesInternacaoVO> buscarExamesInternacao(AghAtendimentos atendimento, SigProcessamentoCusto sigProcessamentoCusto) {

		Date inicioCompetencia = DateUtil.obterDataInicioCompetencia(sigProcessamentoCusto.getCompetencia());
		Date fimCompetencia = DateUtil.obterDataFimCompetencia(sigProcessamentoCusto.getCompetencia());

		StringBuilder hql = new StringBuilder(500);
		hql.append("SELECT ")
		.append("soe." ).append( AelSolicitacaoExames.Fields.SEQ ).append( ", ")
		.append("soe." ).append( AelSolicitacaoExames.Fields.CRIADO_EM ).append( ", ")
		.append("ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ ).append( ", ")
		.append("phi." ).append( FatProcedHospInternos.Fields.SEQ ).append( ", ")
		.append("unf." ).append( AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO ).append( ", ")
		.append("count(*) as qtde_exames ")
		.append("FROM ")
		.append(AghAtendimentos.class.getSimpleName() ).append( " atd, ")
		.append(AelSolicitacaoExames.class.getSimpleName() ).append( " soe, ")
		.append(AelItemSolicitacaoExames.class.getSimpleName() ).append( " ise, ")
		.append(AghUnidadesFuncionais.class.getSimpleName() ).append( " unf, ")
		.append(AelExamesMaterialAnalise.class.getSimpleName() ).append( " ema, ")
		.append(FatProcedHospInternos.class.getSimpleName() ).append( " phi ")
		.append("LEFT JOIN phi." ).append( FatProcedHospInternos.Fields.OBJETO_CUSTO_PHI ).append( " ocp ")
		.append("LEFT JOIN ocp." ).append( SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO ).append( " ocv ")
		.append("WHERE ")
		.append("soe." ).append( AelSolicitacaoExames.Fields.ATD_SEQ ).append( " = :seqAtendimento ")
		.append("and ise." ).append( AelItemSolicitacaoExames.Fields.SIT_CODIGO ).append( " in ('LI', 'AE') ")
		.append("and soe." ).append( AelSolicitacaoExames.Fields.CRIADO_EM ).append( " between :inicioCompetencia and :fimCompetencia ")
		.append("and atd." ).append( AghAtendimentos.Fields.ORIGEM ).append( " = '" ).append( DominioOrigemAtendimento.I ).append( "' ")
		.append("and ise." ).append( AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ ).append( " = soe." ).append( AelSolicitacaoExames.Fields.SEQ ).append( ' ')
		.append("and soe." ).append( AelSolicitacaoExames.Fields.ATD_SEQ ).append( " = atd." ).append( AghAtendimentos.Fields.SEQ ).append( ' ')
		.append("and ise." ).append( AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA ).append( " = ema." ).append( AelExamesMaterialAnalise.Fields.EXA_SIGLA ).append( ' ')
		.append("and ise." ).append( AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ ).append( " = ema." ).append( AelExamesMaterialAnalise.Fields.MAN_SEQ ).append( ' ')
		.append("and phi." ).append( FatProcedHospInternos.Fields.EMA_EXA_SIGLA ).append( " = ema." ).append( AelExamesMaterialAnalise.Fields.EXA_SIGLA ).append( ' ')
		.append("and phi." ).append( FatProcedHospInternos.Fields.EMA_MAN_SEQ ).append( " = ema." ).append( AelExamesMaterialAnalise.Fields.MAN_SEQ ).append( ' ')
		.append("and ise." ).append( AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString() ).append( '.' ).append( AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()
				).append( " = unf." ).append( AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() ).append( ' ')
		.append("GROUP BY ")
		.append("soe." ).append( AelSolicitacaoExames.Fields.SEQ ).append( ", ")
		.append("soe." ).append( AelSolicitacaoExames.Fields.CRIADO_EM ).append( ", ")
		.append("phi." ).append( FatProcedHospInternos.Fields.SEQ ).append( ", ")
		.append("unf." ).append( AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO ).append( ", ")
		.append("ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ ).append( ' ')
		.append("ORDER BY ")
		.append("soe." ).append( AelSolicitacaoExames.Fields.SEQ ).append( ' ');

		Query query = this.createQuery(hql.toString());
		query.setParameter("seqAtendimento", atendimento.getSeq());
		query.setParameter("inicioCompetencia", inicioCompetencia);
		query.setParameter("fimCompetencia", fimCompetencia);

		@SuppressWarnings("unchecked")
		List<Object[]> listResult = query.getResultList();
		List<ExamesInternacaoVO> listRetorno = new ArrayList<ExamesInternacaoVO>();

		if (listResult != null && listResult.size() > 0) {
			for (Object[] object : listResult) {
				listRetorno.add(ExamesInternacaoVO.create(object));
			}
		}

		return listRetorno;
	}
	
	/**
	 * Consulta para obter todos os cuidados de enfermagem prescritos naquele atendimento de internação
	 * @author rogeriovieira
	 * @param seqAtendimento 
	 * @param dataInicioProcessamento 
	 * @param dataFimProcessamento 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CuidadosEnfermagemVO>   buscarCuidadosEnfermagem(Integer seqAtendimento, Date dataInicioProcessamento, Date dataFimProcessamento){
		
		StringBuilder hql = new StringBuilder(460);
		
		hql.append("SELECT ")
			.append("phi.").append(FatProcedHospInternos.Fields.SEQ).append(" as phi_seq, ")
			.append("ocv.").append(SigObjetoCustoVersoes.Fields.SEQ).append(" as ocv_seq, ")
			.append("prc.").append(EpePrescricoesCuidados.Fields.TFQ_SEQ).append(" as tfq_seq, ")
			.append("prc.").append(EpePrescricoesCuidados.Fields.FREQUENCIA).append(" as frequencia, ")
			.append("pen.").append(EpePrescricaoEnfermagem.Fields.DTHR_INICIO).append(" as dthr_inicio,")
			.append("pen.").append(EpePrescricaoEnfermagem.Fields.DTHR_FIM).append(" as dthr_fim,")
			//A função vai ser chamada no java, mas como vou aplicar esse SUM ? Devo percorrer a lista antes
		    // sum(round(mpmc_num_vezes_apraz(prc.tfq_seq, prc.frequencia) * (pen.dthr_fim - pen.dthr_inicio))) qtde_kit, 
			.append("count(*) as qtd_cuidados ")
		.append("FROM ")
			.append(EpePrescricaoEnfermagem.class.getSimpleName() ).append( " pen, ")
			.append(EpePrescricoesCuidados.class.getSimpleName() ).append( " prc, ")
			.append(FatProcedHospInternos.class.getSimpleName() ).append( " phi  ")
			// agh.sig_objeto_custo_phis ocp, (como tem o operador  (+) no where, deverá ser left join)
			.append("LEFT JOIN phi." ).append( FatProcedHospInternos.Fields.OBJETO_CUSTO_PHI ).append( " ocp ")
			// agh.sig_objeto_custo_versoes ocv  (como tem o operador (+) no where, deverá ser left join)
			.append("LEFT JOIN ocp." ).append( SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO ).append( " ocv with ( ocv.").append(SigObjetoCustoVersoes.Fields.IND_SITUACAO).append(" = :ocvSituacao ) ")
		.append("WHERE ")
			.append("pen.").append(EpePrescricaoEnfermagem.Fields.ATD_SEQ).append( " = :seqAtendimento ")
			.append("and pen.").append(EpePrescricaoEnfermagem.Fields.DT_REFERENCIA).append( " between :inicioCompetencia and  :fimCompetencia ")
			.append("and prc.").append(EpePrescricoesCuidados.Fields.ATD_SEQ).append(" = pen.").append(EpePrescricaoEnfermagem.Fields.ATD_SEQ).append(' ')
			.append("and phi.").append(FatProcedHospInternos.Fields.CUI_SEQ).append(" = prc.").append(EpePrescricoesCuidados.Fields.CUI_SEQ).append(' ')
			// and phi.seq =  ocp.phi_seq ().append()  (não precisa aparecer no where ser criado pois representa o left join)
			// and ocp.ocv_seq = ocv.seq().append()  (não precisa aparecer no where ser criado pois representa o left join)
			.append("and prc.").append(EpePrescricoesCuidados.Fields.SERVIDOR_VALIDACAO).append('.').append(RapServidores.Fields.MATRICULA).append(" is not null ")
			// and ocv.ind_situacao(+) = 'A' (teve que ser adicionado no with para respeitar o left join)
			// -- ver com o Vacaro
			//.append("and pen.").append(EpePrescricaoEnfermagem.Fields.ATD_SEQ).append(" = prc.").append(EpePrescricoesCuidados.Fields.PEN_ATD_SEQ).append(' ')
			//.append("and pen.").append(EpePrescricaoEnfermagem.Fields.ATD_SEQ).append(" = prc.").append(EpePrescricoesCuidados.Fields.PEN_SEQ).append(' ')
		.append("GROUP BY ")
			.append("phi.").append(FatProcedHospInternos.Fields.SEQ).append(", ")
			.append("ocv.").append(SigObjetoCustoVersoes.Fields.SEQ).append(", ")
			.append("prc.").append(EpePrescricoesCuidados.Fields.TFQ_SEQ).append(", ")
			.append("prc.").append(EpePrescricoesCuidados.Fields.FREQUENCIA).append(", ")
			.append("pen.").append(EpePrescricaoEnfermagem.Fields.DTHR_INICIO).append(", ")
			.append("pen.").append(EpePrescricaoEnfermagem.Fields.DTHR_FIM).append(' ')
		.append("ORDER BY ")
			.append("pen.").append(EpePrescricaoEnfermagem.Fields.DTHR_INICIO).append(", ")//Alterado a ordenação pra facilitar o processamento
			.append("phi.").append(FatProcedHospInternos.Fields.SEQ).append(", ")
			.append("ocv.").append(SigObjetoCustoVersoes.Fields.SEQ).append(", ")
			.append("prc.").append(EpePrescricoesCuidados.Fields.TFQ_SEQ).append(", ")
			.append("prc.").append(EpePrescricoesCuidados.Fields.FREQUENCIA).append(", ")
			.append("pen.").append(EpePrescricaoEnfermagem.Fields.DTHR_FIM);
		
		org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("inicioCompetencia", dataInicioProcessamento);
		query.setParameter("fimCompetencia", dataFimProcessamento);
		query.setParameter("ocvSituacao", DominioSituacaoVersoesCustos.A);
		
		query.setResultTransformer(new CuidadosEnfermagemVO());
		
		return (List<CuidadosEnfermagemVO>)query.list();
	}
	
	/**
	 * Busca as dietas prescritas durante um periodo para um determinado atendimento.
	 * @param atendimento Codigo identificador do atendimento.
	 * @param sigProcessamentoCusto - Intervalo de tempo do atendimento
	 * @return List<DietaPrescritaVO>  dietas existentes 
	 * @author jgugel
	 */
	@SuppressWarnings("unchecked")
	public List<DietaPrescritaVO> buscaDietasDuranteValidadePrescricao(AghAtendimentos atendimento, SigProcessamentoCusto sigProcessamentoCusto) {
		
		StringBuilder hql = new StringBuilder(400);
		hql.append("SELECT ")
		.append("atd.").append(AghAtendimentos.Fields.INT_SEQ).append(", ")
		.append("atd.").append(AghAtendimentos.Fields.UNF_SEQ).append(", ")
		.append("haa.").append(AnuHabitoAlimAtendimento.Fields.DTHR_INICIO).append(", ")
		.append("case when haa.").append(AnuHabitoAlimAtendimento.Fields.DTHR_FIM).append(" is null ")
		.append("	then coalesce(atd.").append(AghAtendimentos.Fields.DTHR_FIM).append(", pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM).append(" ) ")
		.append("	else haa.").append(AnuHabitoAlimAtendimento.Fields.DTHR_FIM).append(" end, ")
		.append("haa.").append(AnuHabitoAlimAtendimento.Fields.HAU_SEQ).append(", ")
		.append("haa.").append(AnuHabitoAlimAtendimento.Fields.SEQ_REFEICAO).append(' ')
		.append("FROM ")
		.append(AghAtendimentos.class.getSimpleName() ).append( " atd, ")
		.append(AnuHabitoAlimAtendimento.class.getSimpleName()).append( " haa, ")
		.append(SigProcessamentoCusto.class.getSimpleName()).append( " pmu ")
		.append("WHERE ")
		.append("haa.").append(AnuHabitoAlimAtendimento.Fields.ID_ATD_SEQ).append(" = :seqAtendimento ")
		.append("AND pmu.").append(SigProcessamentoCusto.Fields.SEQ).append(" = :seqProcessamentoCusto ")
		.append("AND atd.").append(AghAtendimentos.Fields.SEQ).append(" = haa.").append(AnuHabitoAlimAtendimento.Fields.ID_ATD_SEQ).append(' ')
		.append("AND haa.").append(AnuHabitoAlimAtendimento.Fields.DTHR_INICIO).append(" <= pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM).append(' ' )
		.append("AND case when haa.").append(AnuHabitoAlimAtendimento.Fields.DTHR_FIM).append(" is null ")
		.append("THEN coalesce(atd.").append(AghAtendimentos.Fields.DTHR_FIM).append(",  pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM).append(") ")
		.append("ELSE haa.").append(AnuHabitoAlimAtendimento.Fields.DTHR_FIM).append(" end >= pmu.").append(SigProcessamentoCusto.Fields.DATA_INICIO).append(' ')
		.append("ORDER BY ")
		.append("haa.").append(AnuHabitoAlimAtendimento.Fields.HAU_SEQ).append(", ") 
		.append("haa.").append(AnuHabitoAlimAtendimento.Fields.SEQ_REFEICAO).append(' ');
		org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", atendimento.getSeq());
		query.setParameter("seqProcessamentoCusto", sigProcessamentoCusto.getSeq());
		query.setResultTransformer(new DietaPrescritaVO());
		
		return (List<DietaPrescritaVO>) query.list();

	}

	public Integer buscarPHIPorNomeUsal(String nomeUsual, Short unfSeq) {

		StringBuilder hql = new StringBuilder(350);
		hql.append("SELECT ")
		.append(" phi.seq ")
		.append("FROM ")
		.append(AelMateriaisAnalises.class.getSimpleName() ).append( " man, ")
		.append(AghUnidadesFuncionais.class.getSimpleName() ).append( " unf, ")
		.append(AelExames.class.getSimpleName() ).append( " exa, ")
		.append(AelExamesMaterialAnalise.class.getSimpleName() ).append( " ema, ")
		.append(AelUnfExecutaExames.class.getSimpleName() ).append( " ufe, ")
		.append(FatProcedHospInternos.class.getSimpleName() ).append( " phi ")
		.append("WHERE ")
		.append("ufe." ).append( AelUnfExecutaExames.Fields.UNF_SEQ ).append( " = unf." ).append( AghUnidadesFuncionais.Fields.SEQUENCIAL ).append( ' ')
		.append("and ufe." ).append( AelUnfExecutaExames.Fields.SITUACAO ).append( " = :ufeSituacao ")
		.append("and ema." ).append( AelExamesMaterialAnalise.Fields.EXA_SIGLA ).append( " = ufe." ).append( AelUnfExecutaExames.Fields.EMA_EXA_SIGLA ).append( ' ')
		.append("and ema." ).append( AelExamesMaterialAnalise.Fields.MAN_SEQ ).append( " = ufe." ).append( AelUnfExecutaExames.Fields.EMA_MAN_SEQ ).append( ' ')
		.append("and ema." ).append( AelExamesMaterialAnalise.Fields.IND_SITUACAO ).append( " = :emaSituacao ")
		.append("and exa." ).append( AelExames.Fields.SIGLA ).append( " = ema." ).append( AelExamesMaterialAnalise.Fields.EXA_SIGLA ).append( ' ')
		.append("and exa." ).append( AelExames.Fields.IND_SITUACAO ).append( " = :exaSituacao ")
		.append("and unf." ).append( AghUnidadesFuncionais.Fields.SEQUENCIAL ).append( " = ufe." ).append( AelUnfExecutaExames.Fields.UNF_SEQ ).append( ' ')
		.append("and man." ).append( AelMateriaisAnalises.Fields.SEQ ).append( " = ema." ).append( AelExamesMaterialAnalise.Fields.MAN_SEQ ).append( ' ')
		.append("and ema." ).append( AelExamesMaterialAnalise.Fields.EXA_SIGLA ).append( " = phi." ).append( FatProcedHospInternos.Fields.EMA_EXA_SIGLA ).append( ' ')
		.append("and ema." ).append( AelExamesMaterialAnalise.Fields.MAN_SEQ ).append( " = phi." ).append( FatProcedHospInternos.Fields.EMA_MAN_SEQ ).append( ' ')
		.append("and unf." ).append( AghUnidadesFuncionais.Fields.SEQUENCIAL ).append( " = :unfSeq ")
		.append("and exa." ).append( AelExames.Fields.DESCRICAO_USUAL ).append( " = :nomeUsual ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("ufeSituacao", DominioSituacao.A);
		query.setParameter("emaSituacao", DominioSituacao.A);
		query.setParameter("exaSituacao", DominioSituacao.A);
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("nomeUsual", nomeUsual);

		@SuppressWarnings("rawtypes")
		List listResult = query.getResultList();

		if (listResult == null || listResult.isEmpty()) {
			return 0;
		}
		return (Integer) listResult.get(0);
	}
	
	public List<SigObjetoCustoPhisVO> pesquisarObjectoCustoPorTipoEtiqueta(String tipoEtiqueta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoPhis.class, "OPHI");
		
		criteria.createAlias(SigObjetoCustoPhis.Fields.FAT_PHI.toString(), "PHI");
		
		criteria.createAlias("OPHI."+SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO.toString(), "OCV");
		criteria.createAlias("OCV."+SigObjetoCustoVersoes.Fields.COMPOSICOES.toString(), "OCC");
		criteria.createAlias("OCC."+SigObjetoCustoComposicoes.Fields.ATIVIDADE, "ATV", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PHI."+FatProcedHospInternosPai.Fields.TIPO_ETIQUETA, tipoEtiqueta));
		criteria.add(Restrictions.eq("OPHI."+SigObjetoCustoPhis.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		ProjectionList pList = Projections.projectionList();
		
		pList.add(
				Projections.property("OPHI."
						+SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()), "ocvSeq");
		
		pList.add(
				Projections.property("OCC."
						+SigObjetoCustoComposicoes.Fields.ATIVIDADE_SEQ.toString()), "tvdSeq");
		
		criteria.setProjection(pList);
		
		criteria.setResultTransformer(Transformers
				.aliasToBean(SigObjetoCustoPhisVO.class));
			
		return executeCriteria(criteria);
	}	

}
