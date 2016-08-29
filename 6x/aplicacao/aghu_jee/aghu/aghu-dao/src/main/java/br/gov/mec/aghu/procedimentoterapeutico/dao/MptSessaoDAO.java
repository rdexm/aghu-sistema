package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoManipulacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiaPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacientePTVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacienteVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MptSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptSessao> {

	private static final long serialVersionUID = -457731610706644208L;
	
	@EJB
	private IParametroFacade parametrofacade;

	public List<PrescricaoPacienteVO> obterListaPrescricoesPorPaciente(Integer pacCodigo, Date dataCalculada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString(), "SER1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES2", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SEQ.toString())
						, PrescricaoPacienteVO.Fields.PTE_SEQ.toString())
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.CICLO.toString())
						, PrescricaoPacienteVO.Fields.CICLO.toString())
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString())
						, PrescricaoPacienteVO.Fields.DATA_SUGERIDA.toString())
				.add(Projections.property("PES1." + RapPessoasFisicas.Fields.NOME.toString())
						, PrescricaoPacienteVO.Fields.RESPONSAVEL_1.toString())
				.add(Projections.property("PES2." + RapPessoasFisicas.Fields.NOME.toString())
						, PrescricaoPacienteVO.Fields.RESPONSAVEL_2.toString())
				.add(Projections.property("SES." + MptSessao.Fields.CICLO.toString())
						, PrescricaoPacienteVO.Fields.CLO_SEQ.toString())
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString())
						, PrescricaoPacienteVO.Fields.LOTE.toString()));
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SSO));
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.TIPO_ATENDIMENTO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ge("PTE." + MptPrescricaoPaciente.Fields.CRIADO_EM.toString(), dataCalculada));
		
		criteria.addOrder(Order.asc("PTE." + MptPrescricaoPaciente.Fields.CRIADO_EM.toString()));
		//criteria.addOrder(Order.asc("PTE." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PrescricaoPacienteVO.class));
		
		return executeCriteria(criteria);
	}
	
	//C3 41689
	public List<CadIntervaloTempoVO> listarIntervalosTempoPrescricao(Integer lote, Integer cloSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("SES." + MptSessao.Fields.HORARIO_SESSAO.toString(), "HRS", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.CICLO.toString())
						, CadIntervaloTempoVO.Fields.CICLO.toString())
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.DIA.toString())
						, CadIntervaloTempoVO.Fields.DIA_REFERENCIA.toString())
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.TEMPO_ADMINISTRACAO.toString())
						, CadIntervaloTempoVO.Fields.TEMPO_ADMINISTRACAO.toString())
				.add(Projections.property("SES." + MptSessao.Fields.SEQ.toString())
						, CadIntervaloTempoVO.Fields.SES_SEQ.toString())
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SEQ.toString())
						, CadIntervaloTempoVO.Fields.PTE_SEQ.toString()));
		
		criteria.add(Restrictions.or(Restrictions.eq("HRS."+MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.C),
				Restrictions.isNull("HRS."+MptHorarioSessao.Fields.IND_SITUACAO.toString())));
		criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString(), lote));
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.CICLO.toString(), cloSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(CadIntervaloTempoVO.class));
		
		return executeCriteria(criteria);
	}
	
	 /**
	 * #44249 C7 
	 * Consulta os dias da prescrição
	 * @param lote
	 * @return
	 */
	public List<DiaPrescricaoVO> obterDiaPrescricaoVO(Integer lote, Integer cloSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		criteria.createAlias("SES."+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "MPT");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MPT."+MptPrescricaoPaciente.Fields.CICLO.toString()),DiaPrescricaoVO.Fields.CICLO.toString())
				.add(Projections.property("MPT."+MptPrescricaoPaciente.Fields.DIA.toString()),DiaPrescricaoVO.Fields.DIA.toString())
				.add(Projections.property("MPT."+MptPrescricaoPaciente.Fields.TEMPO_ADMINISTRACAO.toString()),DiaPrescricaoVO.Fields.TEMPO_ADMINISTRATIVO.toString())
				.add(Projections.property("SES."+MptSessao.Fields.SEQ.toString()),DiaPrescricaoVO.Fields.SEQ.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(DiaPrescricaoVO.class));
		criteria.add(Restrictions.eq("MPT."+MptPrescricaoPaciente.Fields.LOTE.toString(), lote));
		criteria.add(Restrictions.eq("SES."+MptSessao.Fields.CLO_SEQ.toString(), cloSeq));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #44249 C1 - Consulta as prescrições do paciente
	 * @param pacCodigo
	 * @return
	 */
	public List<PrescricaoPacientePTVO> obterListaPrescricaoPacientePTVO(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		
		criteria.createAlias("SES."+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE");
		criteria.createAlias("PTE."+MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("PTE."+MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString(), "SER1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER1."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PTE."+MptPrescricaoPaciente.Fields.SERVIDOR.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER2."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES2", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("PTE."+MptPrescricaoPaciente.Fields.CICLO.toString())), PrescricaoPacientePTVO.Fields.CICLO.toString())
				.add(Projections.property("PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA.toString()), PrescricaoPacientePTVO.Fields.SER_MATRICULA.toString())
				.add(Projections.property("PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO.toString()), PrescricaoPacientePTVO.Fields.SER_VIN_CODIGO.toString())
				.add(Projections.property("PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()), PrescricaoPacientePTVO.Fields.SER_MATRICULA_VALIDA.toString())
				.add(Projections.property("PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()), PrescricaoPacientePTVO.Fields.SER_VIN_CODIGO_VALIDA.toString())
				.add(Projections.property("PES1."+RapPessoasFisicas.Fields.NOME.toString()), PrescricaoPacientePTVO.Fields.NOME_RESPONSAVEL1.toString())
				.add(Projections.property("PES2."+RapPessoasFisicas.Fields.NOME.toString()), PrescricaoPacientePTVO.Fields.NOME_RESPONSAVEL2.toString())
				.add(Projections.property("SES."+MptSessao.Fields.CLO_SEQ.toString()), PrescricaoPacientePTVO.Fields.CLO_SEQ.toString())
				.add(Projections.property("PTE."+MptPrescricaoPaciente.Fields.LOTE.toString()), PrescricaoPacientePTVO.Fields.LOTE.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(PrescricaoPacientePTVO.class));
		
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("SES."+MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SSO));
		criteria.add(Restrictions.eq("SES."+MptSessao.Fields.TIPO_ATENDIMENTO.toString(), DominioSituacao.A));
		
		BigDecimal paramSistema = parametrofacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_AGHU_VALIDADE_PRECRICAO_QUIMIO.toString());
		Date dataParametro = DateUtil.adicionaDias(new Date(), -paramSistema.intValue());
		criteria.add(Restrictions.ge("PTE."+MptPrescricaoPaciente.Fields.CRIADO_EM.toString(), dataParametro));
		criteria.addOrder(Order.asc("PTE."+MptPrescricaoPaciente.Fields.LOTE.toString()));
		
		return executeCriteria(criteria);
	}	
	
	
	/**
	 * #41708 C4 
	 * Consultar manipulação.
	 * @param seqSessao
	 * @return DominioSituacaoManipulacao
	 */
	public DominioSituacaoManipulacao obterManipulacao(Integer seqSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		
		criteria.setProjection(Projections.property(MptSessao.Fields.IND_SITUACAOMANIPULACAO.toString()));
		criteria.add(Restrictions.eq("SES."+MptSessao.Fields.SEQ.toString(), seqSessao));
		
		return (DominioSituacaoManipulacao) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Consultar sessão.
	 * C1 da estória #41703
	 * @param atdSeq
	 * @param seqTipoSessao
	 * @return MptSessao
	 */
	public Integer pesquisarSeqSessao(Integer seqSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.AGENDA_PRESCRICAO.toString(), "AGP", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.SEQ.toString(), seqSessao));
		criteria.add(Restrictions.eq("AGP." + MptAgendaPrescricao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "CUF");
		subquery.setProjection(Projections.property("CUF." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subquery.add(Restrictions.ne("CUF." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.APLICACAO_QUIMIO_INTRATECAL));
		subquery.add(Restrictions.eqProperty("CUF." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), "AGP." + MptAgendaPrescricao.Fields.UNF_SEQ_SEQ.toString()));
		criteria.add(Subqueries.exists(subquery));
		
		criteria.setProjection(Projections.property("AGP." + MptAgendaPrescricao.Fields.PTE_SEQ.toString()));
		
		return (Integer)executeCriteriaUniqueResult(criteria);
	}
}