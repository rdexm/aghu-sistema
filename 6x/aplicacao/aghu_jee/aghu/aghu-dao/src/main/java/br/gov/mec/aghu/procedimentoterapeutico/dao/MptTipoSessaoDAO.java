package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaEsperaRetirarPacienteVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MptTipoSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptTipoSessao> {

	private static final long serialVersionUID = 9069743268660153768L;

	public List<MptTipoSessao> listarMptTipoSessao(String descricao, Short unfSeq, DominioSituacao situacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = obterCriteriaListarMptTipoSessao(descricao, unfSeq, situacao);
		criteria.addOrder(Order.asc("TS." + MptTipoSessao.Fields.IND_SITUACAO.toString()))
			.addOrder(Order.asc("TS." + MptTipoSessao.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long listarMptTipoSessaoCount(String descricao, Short unfSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteriaListarMptTipoSessao(descricao, unfSeq, situacao);
		return executeCriteriaCount(criteria);
	}
	//C1
	private DetachedCriteria obterCriteriaListarMptTipoSessao(String descricao, Short unfSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class, "TS");
		criteria.createAlias("TS." + MptTipoSessao.Fields.UNIDADE_FUNCIONAL.toString(), "UF");
		
		if (descricao != null) {
			criteria.add(Restrictions.ilike("TS." + MptTipoSessao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("TS." + MptTipoSessao.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq("TS." + MptTipoSessao.Fields.IND_SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	public MptTipoSessao obterMptTipoSessaoPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class, "TS");
		criteria.createAlias("TS." + MptTipoSessao.Fields.UNIDADE_FUNCIONAL.toString(), "UF");
		criteria.add(Restrictions.eq("TS." + MptTipoSessao.Fields.SEQ.toString(), seq));
		
		return (MptTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
	public MptTipoSessao obterMptTipoSessaoPorSeqAtivo(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class);
		
		criteria.add(Restrictions.eq(MptTipoSessao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MptTipoSessao.Fields.SEQ.toString(), seq));
		
		return (MptTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<MptTipoSessao> listarTiposSessaoPorUnfSeq(Short unfSeq) {
		DetachedCriteria criteria = obterCriteriaListarMptTipoSessao(null, unfSeq, DominioSituacao.A);
		return executeCriteria(criteria);
	}
	//c2
	public List<MptTipoSessao> listarTiposSessao(String param) {
		DetachedCriteria criteria = obterCriteriaListarMptTipoSessao(null, null, DominioSituacao.A);
		if (StringUtils.isNumeric(param)) {
			criteria.add(Restrictions.eq("TS." + MptTipoSessao.Fields.SEQ.toString(), Short.valueOf(param)));
		} else if (!StringUtils.isEmpty(param)) {
			criteria.add(Restrictions.ilike("TS." + MptTipoSessao.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc("TS." + MptTipoSessao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long listarTiposSessaoCount(String param) {
		DetachedCriteria criteria = obterCriteriaListarMptTipoSessao(null, null, DominioSituacao.A);
		if (StringUtils.isNumeric(param)) {
			criteria.add(Restrictions.eq("TS." + MptTipoSessao.Fields.SEQ.toString(), Short.valueOf(param)));
		} else if (!StringUtils.isEmpty(param)) {
			criteria.add(Restrictions.ilike("TS." + MptTipoSessao.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
		}
		return executeCriteriaCount(criteria);
	}

	public List<MptTipoSessao> listarMptTiposSessao() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class, "MTS");
			
		criteria.createAlias("MTS." + MptTipoSessao.Fields.UNIDADE_FUNCIONAL.toString(), "UF");
		criteria.add(Restrictions.eq("MTS." + MptTipoSessao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("MTS." + MptTipoSessao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	//C1 #46468 - obtem descrição e seq das tipoSessoes ativas 
	public List<MptTipoSessao> pesquisarTipoSessoesAtivas(){
			DetachedCriteria criteria = obterTipoSessoesAtivos();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptTipoSessao.Fields.DESCRICAO.toString())
						.as(MptTipoSessao.Fields.DESCRICAO.toString()))
				.add(Projections.property(MptTipoSessao.Fields.SEQ.toString())
						.as(MptTipoSessao.Fields.SEQ.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptTipoSessao.class));
		
		criteria.addOrder(Order.asc(MptTipoSessao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	
	public List<MptTipoSessao> listarTipoSessaoCombo(){
		DetachedCriteria criteria = obterTipoSessoesAtivos();
		criteria.addOrder(Order.asc(MptTipoSessao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterTipoSessoesAtivos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class);
		criteria.add(Restrictions.eq(MptTipoSessao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	/**
	 * 	Carrega o SuggestionBox Tipo de Sessão. (C1)
	 * @param parametro
	 * @return List<MptTipoSessao>
	 */
	public  List<MptTipoSessao> buscarTipoSessao(){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class, "TPS");
		
		criteria.addOrder(Order.asc("TPS."+MptTipoSessao.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	//C1 #44466 - Monta o criteria
	private DetachedCriteria criteriaObterTipoSessaoDescricao(){
		DetachedCriteria criteria = obterTipoSessoesAtivos();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptTipoSessao.Fields.DESCRICAO.toString())
						.as(MptTipoSessao.Fields.DESCRICAO.toString()))
				.add(Projections.property(MptTipoSessao.Fields.SEQ.toString())
						.as(MptTipoSessao.Fields.SEQ.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptTipoSessao.class));
		
		return criteria;
	}
	/**C1 #44466 - Retorna lista com os tipos de sessão ordenados pela descricao **/
	public List<MptTipoSessao> obterTipoSessaoDescricao(){
		DetachedCriteria criteria = criteriaObterTipoSessaoDescricao();
		criteria.addOrder(Order.asc(MptTipoSessao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public String obterDescricaoPorSeq(Short sessaoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class);
		criteria.add(Restrictions.eq(MptTipoSessao.Fields.SEQ.toString(), sessaoSeq));
		criteria.setProjection(Projections.projectionList().add(Projections.property(MptTipoSessao.Fields.DESCRICAO.toString())));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MptTipoSessao> pesquisarMptTipoSessaoAtivos() {
		DetachedCriteria criteria = obterTipoSessoesAtivos();
		criteria.addOrder(Order.asc(MptTipoSessao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarQueryTipoSessaoManutencaoAgendamentoSessaoTerapeutica(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class);
		criteria.createAlias(MptTipoSessao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		if (StringUtils.isNotBlank(filtro)) {
			criteria.add(Restrictions.ilike(MptTipoSessao.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(MptTipoSessao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public List<MptTipoSessao> pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeutica(String filtro) {
		DetachedCriteria criteria = montarQueryTipoSessaoManutencaoAgendamentoSessaoTerapeutica(filtro);
		criteria.addOrder(Order.asc(MptTipoSessao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeuticaCount(String filtro) {
		DetachedCriteria criteria = montarQueryTipoSessaoManutencaoAgendamentoSessaoTerapeutica(filtro);
		return executeCriteriaCount(criteria);

	}

	/**
	 * #44249 C2
	 * Consulta para a Tipos de Sessão
	 * @return
	 */
	public List<MptTipoSessao> obterListaTipoSessaoPorIndSituacaoAtiva(){
		DetachedCriteria criteria = obterTipoSessoesAtivos();
		criteria.addOrder(Order.asc(MptTipoSessao.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	public List<ListaEsperaRetirarPacienteVO> pesquisarListaEsperaPacientes(AghParametros aghParametros, MptTipoSessao tipoSessao, Date dataPrescricao, Date dataSugerida,	AghEspecialidades especialidade, AipPacientes paciente) {
		Integer valor = 0;
		BigDecimal parametro = BigDecimal.ZERO;
		if(aghParametros != null){
			parametro = aghParametros.getVlrNumerico();			
		}
		valor = (int) (parametro.intValue() * -1);	
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		
		Projection proj = Projections.projectionList()
				.add(Projections.min("PTE." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()), "dtSugerida")				
				.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.CICLO.toString()),"ciclo")
				.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.SER_MATRICULA.toString()),"serMatricula")
				.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.SER_VIN_CODIGO.toString()),"serVinCodigo")
				.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()), "serMatriculaValida")
				.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()), "serVinCodigoValida")
				.add(Projections.groupProperty("PES1." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel1")
				.add(Projections.groupProperty("PES2." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel2")
				.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString()), "lote")
				.add(Projections.groupProperty("SES." + MptSessao.Fields.CLO_SEQ.toString()), "cloSeq")	
				.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.CRIADO_EM.toString()), "dtPrescricao")
				.add(Projections.groupProperty("ATE." + AghAtendimentos.Fields.PAC_CODIGO.toString()), "pacCodigo")
				.add(Projections.groupProperty("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.groupProperty("PAC." + AipPacientes.Fields.NOME.toString()), "nomePaciente")				
				.add(Projections.groupProperty("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "nomeEspecialidade");
		
		criteria.setProjection(Projections.distinct(proj));				
		
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATE", JoinType.INNER_JOIN);
		criteria.createAlias("ATE." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("ATE." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString(), "SER1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES2", JoinType.LEFT_OUTER_JOIN);
				
		if(paciente != null){
			criteria.add(Restrictions.eq("ATE." + AghAtendimentos.Fields.PAC_CODIGO.toString(), paciente.getCodigo()));			
		}
		
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SSO));
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.TIPO_ATENDIMENTO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.sqlRestriction("TO_CHAR(pte1_.CRIADO_EM, 'yyyyMMdd') >= '"+DateUtil.obterDataFormatada(DateUtil.adicionaDias(new Date(), valor), "yyyyMMdd") + "'"));
		
		if(dataPrescricao != null){			
			criteria.add(Restrictions.sqlRestriction("TO_CHAR(pte1_.CRIADO_EM, 'yyyyMMdd') = '"+DateUtil.obterDataFormatada(dataPrescricao, "yyyyMMdd") + "'"));
		}
		if(dataSugerida != null){
			criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString(), dataSugerida));			
		}
		if(especialidade != null){
			criteria.add(Restrictions.eq("ATE." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), especialidade.getSeq()));			
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ListaEsperaRetirarPacienteVO.class));
		
		return executeCriteria(criteria);
	}

	public List<CadIntervaloTempoVO> consultarDiasAgendamento(Integer lote) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.CICLO.toString()),"ciclo")
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.DIA.toString()),"diaReferencia")
				.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.TEMPO_ADMINISTRACAO.toString()),"tempoAdministracao")
				.add(Projections.property("SES." + MptSessao.Fields.SEQ.toString()), "sesSeq"));
		
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString(), lote));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CadIntervaloTempoVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * @author thiago.cortes #44393 C3
	 * @param tpsSeq
	 * @return
	 */
	public MptTipoSessao obterTipoSessaoPorProtocolo(Short tpsSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessao.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptTipoSessao.Fields.DESCRICAO.toString()))
				.add(Projections.property(MptTipoSessao.Fields.SEQ.toString())));
		
		criteria.add(Restrictions.eq(MptTipoSessao.Fields.SEQ.toString(), tpsSeq));
		
		return (MptTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
	public Short pesquisarSeqTipoSessao(Short seqTipoSessao){
		DetachedCriteria criteria = obterTipoSessoesAtivos();
		criteria.add(Restrictions.eq(MptTipoSessao.Fields.SEQ.toString(), seqTipoSessao));
		
		criteria.setProjection(Projections.property(MptTipoSessao.Fields.UNF_SEQ.toString()));
		
		return (Short)executeCriteriaUniqueResult(criteria);
	}
}