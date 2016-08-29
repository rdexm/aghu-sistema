package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaProcedProfissionalVO;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcProfDescricoesId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.core.utils.DateUtil;

import org.hibernate.Query;

public class MbcProfDescricoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProfDescricoes> {

	private static final long serialVersionUID = 398086745623847616L;
	
	/**
	 * Implementa o cursor <code>c_get_cbo_anest_desc</code>
	 * 
	 * @param crgSeq
	 * @param tipoAtuacao
	 * @return
	 */
	public RapServidores buscaServidorProfPorCrgSeqETipoAtuacao(Integer crgSeq, DominioTipoAtuacao tipoAtuacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class);
		criteria.createAlias(MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		criteria.addOrder(Order.asc("ser." + RapServidores.Fields.MATRICULA.toString()));
	
		List<MbcProfDescricoes> result = executeCriteria(criteria);
		
		if(result != null && !result.isEmpty()){
			return result.get(0).getServidorProf();
		}
		return null;
	}
	
	public List<MbcProfDescricoes> obterMbcProfDescricoesPorCrgSeqDcgSeqpETipoAtuacao(final Integer crgSeq, final Short dcgSeq, final DominioTipoAtuacao tipoAtuacao){

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, "pdf");
		criteria.add(Restrictions.eq("pdf."+MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("pdf."+MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeq));
		criteria.add(Restrictions.eq("pdf."+MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Efetua busca de MbcProfDescricoes
	 * Consulta C3 #18527
	 * @param dcgCrgSeq
	 * @param dcgSeq
	 * @return
	 */
	public List<MbcProfDescricoes> buscarProfDescricoes(Integer dcgCrgSeq, Short dcgSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, "pdf");
		criteria.add(Restrictions.eq("pdf."+MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("pdf."+MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeq));
		criteria.addOrder(Order.asc(MbcProfDescricoes.Fields.TIPO_ATUACAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, DominioTipoAtuacao tipoAtuacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, "pdf");
		
		criteria.createAlias("pdf." + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), "rap");
		
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpEListaTipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, 
			List<DominioTipoAtuacao> listaTipoAtuacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, "pdf");
		
		criteria.createAlias("pdf." + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), "rap");
		
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.in("pdf." + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), listaTipoAtuacao));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProfDescricoes> pesquisarProfDescricoesOutrosPorDcgCrgSeqDcgSeqpETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, DominioTipoAtuacao tipoAtuacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, "pdf");
		
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq("pdf." + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpServidorProfETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, 
			Integer servidorMatricula, Short servidorVinCodigo, DominioTipoAtuacao tipoAtuacao) {
		String aliasPdf = "pdf";
		String aliasRap = "rap";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, aliasPdf);
		
		criteria.createAlias(aliasPdf + separador + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), aliasRap);
		
		criteria.add(Restrictions.eq(aliasPdf + separador + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq(aliasPdf + separador + MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq(aliasPdf + separador + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		criteria.add(Restrictions.eq(aliasRap + separador + RapServidores.Fields.MATRICULA, servidorMatricula));
		criteria.add(Restrictions.eq(aliasRap + separador + RapServidores.Fields.VIN_CODIGO.toString(), servidorVinCodigo));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProfDescricoes> pesquisarProfDescricoesPorCrgSeqEServidor(Integer crgSeq, Integer servidorMatricula, Short servidorVinCodigo) {
		String aliasPdf = "pdf";
		String aliasRap = "rap";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, aliasPdf);
		
		criteria.createAlias(aliasPdf + separador + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), aliasRap);
		
		criteria.add(Restrictions.eq(aliasPdf + separador + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(aliasRap + separador + RapServidores.Fields.MATRICULA, servidorMatricula));
		criteria.add(Restrictions.eq(aliasRap + separador + RapServidores.Fields.VIN_CODIGO.toString(), servidorVinCodigo));
		
		return executeCriteria(criteria);
	}
	
	public List<RelatorioCirurgiaProcedProfissionalVO> pesquisarProfPorCodigoPessoaFisicaDthrInicioEDthrFim(
			Integer codigoPessoaFisica, Date dthrInicio, Date dthrFim) {
		String aliasPfd = "pfd";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String aliasDcg = "dcg";
		String aliasCrg = "crg";
		String aliasPpc = "ppc";
		String aliasPac = "pac";
		String aliasPci = "pci";
		String aliasEsp = "esp";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class, aliasPfd);
		
		Projection p = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.DATA.toString())
				.add(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.SER_MATRICULA_PROF.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SER_MATRICULA_PROF.toString())
				.add(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SER_VIN_CODIGO_PROF.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.NOME.toString())
				.add(Projections.property(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.IND_PRINCIPAL.toString())
				.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.PROCEDIMENTO.toString())
				.add(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.TIPO_ATUACAO.toString())
				.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SIGLA_ESP.toString());
		
		criteria.setProjection(p);
		
		criteria.createAlias(aliasPfd + ponto + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		criteria.createAlias(aliasPfd + ponto + MbcProfDescricoes.Fields.MBC_DESCRICAO_CIRURGICA.toString(), aliasDcg);
		criteria.createAlias(aliasDcg + ponto + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), aliasCrg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPpc);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasEsp);
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), aliasPci);
		
		criteria.add(Restrictions.eq(aliasPes + ponto + RapPessoasFisicas.Fields.CODIGO.toString(), codigoPessoaFisica));
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA, 
				DateUtil.obterDataComHoraInical(dthrInicio), DateUtil.obterDataComHoraFinal(dthrFim)));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC, DominioIndRespProc.NOTA));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO, DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioCirurgiaProcedProfissionalVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Integer obterMaiorSeqpProfDescricoesPorDcgCrgSeqEDcgSeqp(Integer dcgCrgSeq, Short dcgSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class);
		
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		
		criteria.setProjection(Projections.max(MbcProfDescricoes.Fields.SEQP.toString()));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public ProfDescricaoCirurgicaVO obterProfissionalDescricaoAnestesistaPorDescricaoCirurgica(Integer dcgCrgSeq, Short dcgSeqp) {
		StringBuilder hql = montarHqlObterProfissionalDescricaoAnestesistaPorDescricaoCirurgica();
		hql.append(" and puc." + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString() + " = :tipoAtuacao");
		hql.append(" and puc." + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString() + " = :dcgCrgSeq");
		hql.append(" and puc." + MbcProfDescricoes.Fields.DCG_SEQP.toString() + " = :dcgSeqp");
		
		Query q = createHibernateQuery(hql.toString());

		q.setParameter("dcgCrgSeq", dcgCrgSeq);
		q.setParameter("dcgSeqp", dcgSeqp);
		q.setParameter("tipoAtuacao", DominioTipoAtuacao.ESE);

		q.setResultTransformer(Transformers.aliasToBean(ProfDescricaoCirurgicaVO.class));
		
		List<ProfDescricaoCirurgicaVO> lista = q.list();
		
		if (lista!= null && lista.size() > 0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	private StringBuilder montarHqlObterProfissionalDescricaoAnestesistaPorDescricaoCirurgica() {
		StringBuilder hql = new StringBuilder(200);

		hql.append("select ")
			.append(" puc.").append(MbcProfDescricoes.Fields.SER_MATRICULA_PROF.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.SER_MATRICULA.toString())
			.append(", puc.").append(MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.SER_VIN_CODIGO.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.SIGLA.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.SIGLA.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.NRO_REG_CONSELHO.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.NOME.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.NOME.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.CONSELHO_SIGLA.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.CONSELHO_SIGLA.toString());
		
		hql.append(" from ")
			.append(MbcProfDescricoes.class.getName()).append(" puc, ")
			.append(VRapServidorConselho.class.getName()).append(" vcs")
	
			.append(" where ")
			.append(" puc.").append(MbcProfDescricoes.Fields.SER_MATRICULA_PROF.toString())
			.append(" = vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString())
			.append(" and")
			.append(" puc.").append(MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.toString())
			.append(" = vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString());
		
		return hql;
	}
	
	public MbcProfDescricoes obterUltimoProfDescricaoPorDescCigExecAnestesia(Integer dcgCrgSeq, Short dcgSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class);
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), DominioTipoAtuacao.ESE));
		
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.addOrder(Order.desc(MbcProfDescricoes.Fields.SEQP.toString()));

		List<MbcProfDescricoes> lista = executeCriteria(criteria);
		
		if (lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public MbcProfDescricoes obterMbcProfDescricoesPorId(MbcProfDescricoesId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfDescricoes.class);
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), id.getDcgCrgSeq()));
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.DCG_SEQP.toString(), id.getDcgSeqp()));
		criteria.add(Restrictions.eq(MbcProfDescricoes.Fields.SEQP.toString(), id.getSeqp()));
		
		return (MbcProfDescricoes) executeCriteriaUniqueResult(criteria);
	}
	
}
