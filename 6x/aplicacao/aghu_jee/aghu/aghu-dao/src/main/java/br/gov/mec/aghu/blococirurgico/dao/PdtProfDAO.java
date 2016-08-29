package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaProcedProfissionalVO;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PdtProfDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtProf> {
	

	private static final long serialVersionUID = 7278240216078361552L;

	/**
	 * Implementa o cursor <code>c_get_cbo_anest_pdt</code>
	 * 
	 * @param crgSeq
	 * @param tipoAtuacao
	 * @return
	 */
	public RapServidores buscaRapServidor(Integer crgSeq, DominioTipoAtuacao tipoAtuacao){

		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		criteria.createAlias(PdtProf.Fields.PDT_DESCRICAO.toString(), PdtProf.Fields.PDT_DESCRICAO.toString());
		criteria.createAlias(PdtProf.Fields.SERVIDOR_PRF.toString(), PdtProf.Fields.SERVIDOR_PRF.toString(), Criteria.LEFT_JOIN);

		
		criteria.add(Restrictions.eq(PdtProf.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		criteria.add(Restrictions.eq(PdtProf.Fields.CRG_SEQ_PDT_DESCRICAO.toString(), crgSeq));
		
		criteria.addOrder(Order.asc(PdtProf.Fields.SERVIDOR_PRF_MATRICULA.toString()));
			
		List<PdtProf> result = executeCriteria(criteria);
		
		if(result != null && !result.isEmpty()){
			return result.get(0).getServidorPrf();
		}
		return null;
	}
	
	public List<PdtProf> pesquisarProfPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		
		criteria.createAlias(PdtProf.Fields.SERVIDOR_PRF.toString(), PdtProf.Fields.SERVIDOR_PRF.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(PdtProf.Fields.DDT_SEQ.toString(), ddtSeq));		
		
		return executeCriteria(criteria);
	}
	
	public List<PdtProf> pesquisarProfPorDdtSeqETipoAtuacao(Integer ddtSeq, DominioTipoAtuacao tipoAtuacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		
		criteria.add(Restrictions.eq(PdtProf.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq(PdtProf.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		
		return executeCriteria(criteria);
	}
	
	public List<PdtProf> pesquisarProfPorDdtSeqServidorETipoAtuacao(Integer ddtSeq, Integer serMatricula, Short serVinCodigo, DominioTipoAtuacao tipoAtuacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		
		criteria.add(Restrictions.eq(PdtProf.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq(PdtProf.Fields.SERVIDOR_PRF_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(PdtProf.Fields.SERVIDOR_PRF_VIN_CODIGO.toString(), serVinCodigo));
		criteria.add(Restrictions.eq(PdtProf.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		
		return executeCriteria(criteria);
	}
	
	public List<PdtProf> pesquisarProfPorDdtSeqEListaTipoAtuacao(Integer ddtSeq, List<DominioTipoAtuacao> listaTipoAtuacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		
		criteria.add(Restrictions.eq(PdtProf.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.in(PdtProf.Fields.TIPO_ATUACAO.toString(), listaTipoAtuacao));
		
		return executeCriteria(criteria);
	}
	
	public List<PdtProf> buscaPdtProfissaoPorDdtSeqETipoAtuacao(Integer ddtSeq, DominioTipoAtuacao tipoAtuacao, Integer serMatricula){

		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		
		criteria.add(Restrictions.eq(PdtProf.Fields.TIPO_ATUACAO.toString(), tipoAtuacao));
		criteria.add(Restrictions.ne(PdtProf.Fields.SERVIDOR_PRF_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(PdtProf.Fields.DDT_SEQ.toString(), ddtSeq));
	
		return executeCriteria(criteria);
	}
	
	public List<PdtProf> pesquisarProfResponsavelPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		
		criteria.add(Restrictions.eq(PdtProf.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq(PdtProf.Fields.TIPO_ATUACAO.toString(), DominioTipoAtuacao.RESP));
		
		return executeCriteria(criteria);
	}
	
	public Short obterMaiorSeqpProfPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class);
		
		criteria.add(Restrictions.eq(PdtProf.Fields.DDT_SEQ.toString(), ddtSeq));
		
		criteria.setProjection(Projections.max(PdtProf.Fields.SEQP.toString()));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public List<RelatorioCirurgiaProcedProfissionalVO> pesquisarProfPorCodigoPessoaFisicaDthrInicioEDthrFim(
			Integer codigoPessoaFisica, Date dthrInicio, Date dthrFim) {
		String aliasDpf = "dpf";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String aliasDdt = "ddt";
		String aliasCrg = "crg";
		String aliasPpc = "ppc";
		String aliasPac = "pac";
		String aliasPci = "pci";
		String aliasEsp = "esp";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class, aliasDpf);
		
		Projection p = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.DATA.toString())
				.add(Projections.property(aliasDpf + ponto + PdtProf.Fields.SERVIDOR_PRF_MATRICULA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SER_MATRICULA_PROF.toString())
				.add(Projections.property(aliasDpf + ponto + PdtProf.Fields.SERVIDOR_PRF_VIN_CODIGO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SER_VIN_CODIGO_PROF.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.NOME.toString())
				.add(Projections.property(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.IND_PRINCIPAL.toString())
				.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.PROCEDIMENTO.toString())
				.add(Projections.property(aliasDpf + ponto + PdtProf.Fields.TIPO_ATUACAO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.TIPO_ATUACAO.toString())
				.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SIGLA_ESP.toString());
		
		criteria.setProjection(p);
		
		criteria.createAlias(aliasDpf + ponto + PdtProf.Fields.SERVIDOR_PRF.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		criteria.createAlias(aliasDpf + ponto + PdtProf.Fields.PDT_DESCRICAO.toString(), aliasDdt);
		criteria.createAlias(aliasDdt + ponto + PdtDescricao.Fields.MBC_CIRURGIAS.toString(), aliasCrg);
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

	public String obterNomePessoaPdtProfByPdtDescricao(
			Integer seqPdtDescricao, Integer seqMbcCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProf.class, "pdtProf");
		criteria.setProjection(Projections.distinct(Projections.property("pesFisica." + RapPessoasFisicas.Fields.NOME)));
		
		criteria.createAlias("pdtProf."+PdtProf.Fields.PDT_DESCRICAO.toString(), "pdtDesc");
		criteria.createAlias("pdtDesc."+PdtDescricao.Fields.SERVIDOR.toString(), "serv");
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesFisica");
		
		
		criteria.add(Restrictions.eq("pdtDesc." + PdtDescricao.Fields.SEQ, seqPdtDescricao));
		criteria.add(Restrictions.eq("pdtDesc." + PdtDescricao.Fields.CRG_SEQ, seqMbcCirurgia));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	

}
