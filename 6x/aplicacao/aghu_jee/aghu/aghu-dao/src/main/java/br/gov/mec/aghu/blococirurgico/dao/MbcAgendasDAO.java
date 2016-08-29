package br.gov.mec.aghu.blococirurgico.dao;
import static br.gov.mec.aghu.dominio.DominioSituacaoAgendas.LE;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.AgendamentosExcluidosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasAgendaMedicoVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasTurnoVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ResumoAgendaCirurgiaVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioConvenio;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.paciente.vo.SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class MbcAgendasDAO extends BaseDao<MbcAgendas> {
	private static final long serialVersionUID = 2378257069301336456L;
	private static final String T1 = "t1.";
	private static final String T2 = "t2.";
	private static final String PAC_TABLE = "pac";
	private static final String PAC = "pac.";
	private static final String EPR_TABLE = "epr";
	private static final String EPR = "epr.";
	private static final String FAT_CONVENIO_SAUDE = "fatConvenioSaude.";
	private static final String AGD_TABLE = "agd";
	private static final String AGD = "agd.";
	private static final String PCI_TABLE = "pci";
	private static final String PCI = "pci.";
	private static final String AND_AGD = " and agd.";
	private static final String NIV = "niv.";
	private static final String AGENDA = "agenda.";
	private static final String CIR = "cir.";
	private static final String AG = "ag.";
	private static final String RQC_OPME_TABLE = " rqc_opme";
	private static final String ESP = " esp";
	private static final String CRG = "CRG.";
	private static final String ESP_MAX = "ESP";
	private static final String UNF = "UNF";
	public List<MbcAgendas> listarAgendarPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class);
		criteria.add(Restrictions.eq(MbcAgendas.Fields.COD_PACIENTE.toString(), pacCodigo));
		return executeCriteria(criteria);
	}
	public List<CirurgiasInternacaoPOLVO> pesquisarAgendasProcCirurgicosInternacaoPOL(Integer codigo) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class,"t1");
			criteria.createAlias(T1 + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "espProcCirurgias");
			criteria.createAlias("espProcCirurgias." + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "procCirurgico");
			criteria.createAlias(T1 + MbcAgendas.Fields.SERVIDOR.toString(), "servidor");
			criteria.createAlias("servidor." + RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
			criteria.createAlias(T1 + MbcAgendas.Fields.ESPECIALIDADE.toString(), "especialidade");				
			ProjectionList projection = Projections.projectionList()
			.add(Projections.property(T1 + MbcAgendas.Fields.DT_AGENDA.toString()), CirurgiasInternacaoPOLVO.Fields.DATA.toString())
			.add(Projections.property(T1 + MbcAgendas.Fields.IND_SITUACAO.toString()), CirurgiasInternacaoPOLVO.Fields.IND_SITUACAO.toString())
			.add(Projections.property("procCirurgico."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasInternacaoPOLVO.Fields.DESCRICAO.toString())
			.add(Projections.property("especialidade."+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), CirurgiasInternacaoPOLVO.Fields.ESPECIALIDADE.toString())
			.add(Projections.property("pessoaFisica."+RapPessoasFisicas.Fields.NOME.toString()), CirurgiasInternacaoPOLVO.Fields.EQUIPE.toString());
			criteria.setProjection(projection);	
			criteria.add(Restrictions.eq(T1 + MbcAgendas.Fields.COD_PACIENTE.toString(), codigo));
			criteria.add(Restrictions.eq("procCirurgico."+MbcProcedimentoCirurgicos.Fields.TIPO.toString(), DominioTipoProcedimentoCirurgico.CIRURGIA));
			criteria.add(Restrictions.or(Restrictions.eq(T1 + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.LE)
					, Restrictions.eq(T1 + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.AG)));
			criteria.add(Restrictions.eq(T1 + MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			criteria.add(Restrictions.eq(T1 + MbcAgendas.Fields.IND_GERADO_SISTEMA.toString(), Boolean.FALSE));				
			criteria.add(Restrictions.or(Restrictions.gt(T1 + MbcAgendas.Fields.DT_AGENDA.toString(), new Date())
					, Restrictions.isNull(T1 + MbcAgendas.Fields.DT_AGENDA.toString())));
			criteria.add(Subqueries.notExists(subCriteriaCirurgias()));				
			criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasInternacaoPOLVO.class));
			return executeCriteria(criteria);
	}
	private DetachedCriteria subCriteriaCirurgias() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class,"t2");
		criteria.setProjection(Projections.projectionList().add(Projections.property(T2 + MbcCirurgias.Fields.AGD_SEQ.toString())));
		criteria.add(Restrictions.eqProperty(T2 + MbcCirurgias.Fields.AGD_SEQ.toString(), "t1.seq"));
		criteria.add(Restrictions.eqProperty(T2 + MbcCirurgias.Fields.DATA.toString(), "t1.dtAgenda"));
		criteria.add(Restrictions.isNull(T2 + MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString()));		
		return criteria;
	}	
	public List<ProcedimentosPOLVO> pesquisarProcedimentosPortalPOL(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(AGD+ MbcAgendas.Fields.COD_PACIENTE.toString()),ProcedimentosPOLVO.Fields.PAC_CODIGO.toString())
		.add(Projections.property(AGD+ MbcAgendas.Fields.DT_AGENDA.toString()), ProcedimentosPOLVO.Fields.DATA.toString())
		.add(Projections.property(AGD+ MbcAgendas.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.AGD_SEQ.toString())
		.add(Projections.property(PCI+ MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ProcedimentosPOLVO.Fields.DESCRICAO.toString())
		.add(Projections.property(PCI+ MbcProcedimentoCirurgicos.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.EPR_PCI_SEQ.toString());
		
		criteria.setProjection(projection);	
		criteria.createAlias(AGD+ MbcAgendas.Fields.PROCEDIMENTO.toString(), PCI_TABLE);
		criteria.add(Restrictions.eq(AGD+ MbcAgendas.Fields.COD_PACIENTE.toString(),codigo));
		criteria.add(Restrictions.eq(PCI + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO));	
		criteria.add(Restrictions.or(Restrictions.gt(AGD+ MbcAgendas.Fields.DT_AGENDA.toString(),new Date()), Restrictions.isNull(AGD+ MbcAgendas.Fields.DT_AGENDA.toString())));
		List<DominioSituacaoAgendas> situacoes = new ArrayList<DominioSituacaoAgendas>();
		situacoes.add(DominioSituacaoAgendas.AG);
		situacoes.add(DominioSituacaoAgendas.LE);
		criteria.add(Restrictions.in(AGD+ MbcAgendas.Fields.IND_SITUACAO.toString(), situacoes));		
		criteria.add(Restrictions.eq(AGD+ MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(AGD+ MbcAgendas.Fields.IND_GERADO_SISTEMA.toString(),false));
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosPOLVO.class));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> pesquisarCirurgiaAgendadaPorPaciente(Integer pacCodigo, Integer nroDiasPraFrente, Integer nroDiasPraTras){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.add(Restrictions.eq(MbcAgendas.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.AG));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		Date dataPassado = DateUtil.obterDataComHoraInical(new Date());
		Date dataFuturo = DateUtil.obterDataComHoraFinal(new Date());
		if(nroDiasPraFrente != null && nroDiasPraTras != null){
			dataPassado = DateUtil.adicionaDias(dataPassado, (-1) * nroDiasPraTras);			
			dataFuturo = DateUtil.adicionaDias(dataFuturo, nroDiasPraFrente);
		}
		criteria.add(Restrictions.ge(MbcAgendas.Fields.DT_AGENDA.toString(), dataPassado));
		criteria.add(Restrictions.le(MbcAgendas.Fields.DT_AGENDA.toString(), dataFuturo));
		return this.executeCriteria(criteria);
	}	
	public MbcAgendas pesquisarAgendaComControleEscalaCirurgicaDefinitiva(Integer seq) {		
		final StringBuilder hql = new StringBuilder(150);
		hql.append("select agd ")
		.append(" from ").append(MbcAgendas.class.getName()).append(" agd, ")
		.append(MbcControleEscalaCirurgica.class.getName()).append(" cec ")
		.append(" where agd.").append(MbcAgendas.Fields.SEQ.toString()).append(" = :pSeq ")
		.append(AND_AGD).append(MbcAgendas.Fields.IND_SITUACAO.toString()).append(" = :pIndSituacao ")
		.append(AND_AGD).append(MbcAgendas.Fields.UNF_SEQ.toString()).append(" = cec.").append(MbcControleEscalaCirurgica.Fields.UNF_SEQ.toString())
		.append(AND_AGD).append(MbcAgendas.Fields.DT_AGENDA.toString()).append(" = cec.").append(MbcControleEscalaCirurgica.Fields.DT_ESCALA.toString())
		.append(" and cec.").append(MbcControleEscalaCirurgica.Fields.TIPO_ESCALA.toString()).append(" = :pTipoEscala ");
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("pSeq", seq);
		query.setParameter("pIndSituacao", DominioSituacaoAgendas.ES);
		query.setParameter("pTipoEscala", DominioTipoEscala.D);
		return (MbcAgendas) query.uniqueResult();
	}
	public List<MbcAgendas> pesquisarCirurgiasListaDeEspera(Integer pacCodigo, DominioSituacaoAgendas situacao) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class);
		if(pacCodigo != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.COD_PACIENTE.toString(), pacCodigo));
		}
		if(situacao != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_SITUACAO.toString(), situacao));
		}
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		return this.executeCriteria(criteria);
	}
	public List<MbcAgendas> pesquisarCirurgiasAgendadasPorResponsavel(MbcCirurgias cirurgia, MbcProfCirurgias profCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class);
		criteria.add(Restrictions.eq(MbcAgendas.Fields.DT_AGENDA.toString(), cirurgia.getData()));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_SERVIDOR.toString(), profCirurgia.getServidorPuc()));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_UNF_SEQ.toString(), profCirurgia.getId().getPucUnfSeq()));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), cirurgia.getEspecialidade().getSeq()));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), profCirurgia.getId().getPucIndFuncaoProf()));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.COD_PACIENTE.toString(), cirurgia.getPaciente().getCodigo()));
		List<DominioSituacaoAgendas> situacoes = new ArrayList<DominioSituacaoAgendas>();
		situacoes.add(DominioSituacaoAgendas.AG);
		situacoes.add(DominioSituacaoAgendas.ES);
		criteria.add(Restrictions.in(MbcAgendas.Fields.IND_SITUACAO.toString(), situacoes));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.addOrder(Order.asc(MbcAgendas.Fields.ALTERADO_EM.toString()));
		return this.executeCriteria(criteria);
	}
	public List<ResumoAgendaCirurgiaVO> pesquisarResumoAgendamentos(Date dtAgenda, MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short unfSeq, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD + MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE);						
		criteria.createAlias(AGD + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), EPR_TABLE);
		criteria.createAlias(EPR + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), PCI_TABLE);
		ProjectionList projection = Projections.projectionList().add(Projections.property(AGD + MbcAgendas.Fields.SEQ.toString()), ResumoAgendaCirurgiaVO.Fields.AGENDA_SEQ.toString())
			.add(Projections.property(AGD + MbcAgendas.Fields.DTHR_INCLUSAO.toString()), ResumoAgendaCirurgiaVO.Fields.DTHR_INCLUSAO.toString())
			.add(Projections.property(PAC + AipPacientes.Fields.NOME.toString()), ResumoAgendaCirurgiaVO.Fields.PAC_NOME.toString())
			.add(Projections.property(PAC + AipPacientes.Fields.PRONTUARIO.toString()), ResumoAgendaCirurgiaVO.Fields.PRONTUARIO.toString())
			.add(Projections.property(PCI + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ResumoAgendaCirurgiaVO.Fields.DESCRICAO_PROCED.toString())
			.add(Projections.property(AGD + MbcAgendas.Fields.TEMPO_SALA.toString()), ResumoAgendaCirurgiaVO.Fields.TEMPO_SALA.toString());
		criteria.setProjection(projection);
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.DT_AGENDA.toString(), DateUtil.truncaData(dtAgenda)));
		criteria.add(Restrictions.or(Restrictions.eq(AGD + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.AG), 
				Restrictions.eq(AGD + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.ES)));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidCirg));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.addOrder(Order.asc(AGD + MbcAgendas.Fields.DTHR_INCLUSAO.toString()));
		criteria.addOrder(Order.asc(PAC + AipPacientes.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ResumoAgendaCirurgiaVO.class));
		return executeCriteria(criteria);
	}
	public Long pesquisarPlanejamentoCirurgiaAgendadaCount(Date dtIniAgenda, Date dtFimAgenda, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq, Short unfSeq) {
		DetachedCriteria criteria = getCriteriaPesquisarPlanejamentoCirurgiaAgendada(
				dtIniAgenda, dtFimAgenda, pucSerMatricula, pucSerVinCodigo,	pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq);
		return executeCriteriaCount(criteria);
	}
	public List<MbcAgendas> pesquisarPlanejamentoCirurgiaAgendada(Date dtIniAgenda, Date dtFimAgenda, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq, Short unfSeq) {
		DetachedCriteria criteria = getCriteriaPesquisarPlanejamentoCirurgiaAgendada(
				dtIniAgenda, dtFimAgenda, pucSerMatricula, pucSerVinCodigo,	pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq);
		return executeCriteria(criteria);
	}
	private DetachedCriteria getCriteriaPesquisarPlanejamentoCirurgiaAgendada(Date dtIniAgenda, Date dtFimAgenda, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq,	DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq,Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);		
		criteria.createAlias(AGD+ MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE);
		criteria.createAlias(AGD+ MbcAgendas.Fields.PROCEDIMENTO.toString(), PCI_TABLE);		
		if(dtIniAgenda != null && dtFimAgenda != null){ 
			Date dtIni2 = DateUtil.truncaData(dtIniAgenda);
			Date dtFim2= DateUtil.truncaDataFim(dtFimAgenda);			
			criteria.add(Restrictions.between(AGD+MbcAgendas.Fields.DT_AGENDA.toString(), dtIni2, dtFim2));
		}
		if(pucSerMatricula != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_MATRICULA.toString(),pucSerMatricula));
		}
		if(pucSerVinCodigo != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), pucSerVinCodigo));
		}
		if(pucUnfSeq != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_UNF_SEQ.toString(), pucUnfSeq));
		}
		if(pucIndFuncaoProf != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), pucIndFuncaoProf));
		}
		if(espSeq != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		}
		if(unfSeq != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		}		
		List<DominioSituacaoAgendas> situacoes = new ArrayList<DominioSituacaoAgendas>();
		situacoes.add(DominioSituacaoAgendas.ES);
		situacoes.add(DominioSituacaoAgendas.AG);
		criteria.add(Restrictions.in(AGD+ MbcAgendas.Fields.IND_SITUACAO.toString(), situacoes));		
		criteria.add(Restrictions.eq(AGD+ MbcAgendas.Fields.IND_EXCLUSAO.toString(),Boolean.FALSE));
		return criteria;
	}
	public DetachedCriteria obterCriteriaAgendasListaEspera(PortalPesquisaCirurgiasParametrosVO parametros) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class,AGD_TABLE);
		criteria.createAlias(AGD+MbcAgendas.Fields.ESPECIALIDADE.toString(), ESP_MAX);
		criteria.createAlias(AGD+MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE);
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.LE));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if (parametros.getUnfSeq() != null) {
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.UNF_SEQ.toString(), parametros.getUnfSeq()));		
		}
		if(parametros.getEspSeq() != null) {
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.ESP_SEQ.toString(), parametros.getEspSeq()));
		}
		if(parametros.getPucSerMatricula() != null && parametros.getPucSerVinCodigo() != null && parametros.getPucIndFuncaoProf() != null) {
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), parametros.getPucSerMatricula()));
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), parametros.getPucSerVinCodigo()));
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), parametros.getPucIndFuncaoProf()));
		}
		if(parametros.getPciSeqPortal() != null) {
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.EPR_PCI_SEQ.toString(), parametros.getPciSeqPortal()));
		}
		if(parametros.getPacCodigo() != null) {
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.PAC_CODIGO.toString(), parametros.getPacCodigo()));
		}
		if(parametros.getDataInicio() != null || parametros.getDataFim() != null) {
			criteria.add(Restrictions.ge(AGD + MbcAgendas.Fields.DTHR_INCLUSAO.toString(), parametros.getDataInicio()));
			Calendar cdate = Calendar.getInstance();
			cdate.setTime(parametros.getDataFim());
			cdate.add(Calendar.DAY_OF_MONTH, +1);
			criteria.add(Restrictions.le(AGD + MbcAgendas.Fields.DTHR_INCLUSAO.toString(), cdate.getTime()));
		}
		criteria.createAlias(AGD + MbcAgendas.Fields.FAT_CONVENIO_SAUDE.toString(), "fatConvenioSaude");
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatConvenioSaude.class,"cnv");
		subCriteria.setProjection(Projections.property("cnv." + FatConvenioSaude.Fields.CODIGO.toString()));
		subCriteria.add(Restrictions.eqProperty("cnv." + FatConvenioSaude.Fields.CODIGO.toString(), FAT_CONVENIO_SAUDE + FatConvenioSaude.Fields.CODIGO));
		addRestrictionConvenioSaude(parametros, subCriteria);
		criteria.add(Subqueries.exists(subCriteria));
		return criteria;
	}
	private void addRestrictionConvenioSaude(PortalPesquisaCirurgiasParametrosVO parametros,DetachedCriteria subCriteria) {
		if (parametros.getConvenio() != null) {			
			Boolean equalsT = "T".equals(parametros.getConvenio().name());
			if(!equalsT){//Se for T, não precisa verificar a outra parte				
				if(DominioConvenio.N.name().equals(parametros.getConvenio().name())){//Se selecionado "NÃO SUS"					
						subCriteria.add(Restrictions.or(Restrictions.eq(FAT_CONVENIO_SAUDE+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.C),
										Restrictions.eq(FAT_CONVENIO_SAUDE+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.P)));
				}else{
					if(DominioConvenio.S.name().equals(parametros.getConvenio().name())){//Se selecionado "SUS"
						subCriteria.add(Restrictions.eq(FAT_CONVENIO_SAUDE+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
					}
				}			
			}			
		}
	}
	public List<MbcAgendas> listarAgendasListaEspera(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PortalPesquisaCirurgiasParametrosVO parametros) {
		DetachedCriteria criteria = obterCriteriaAgendasListaEspera(parametros);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	public List<MbcAgendas> listarAgendasListaEspera(PortalPesquisaCirurgiasParametrosVO parametros) {
		DetachedCriteria criteria = obterCriteriaAgendasListaEspera(parametros);
		return executeCriteria(criteria);
	}
	public Long listarAgendasListaEsperaCount(PortalPesquisaCirurgiasParametrosVO parametros) {
		DetachedCriteria criteria = obterCriteriaAgendasListaEspera(parametros);
		return executeCriteriaCount(criteria);
	}
	@SuppressWarnings("unchecked")
	public List<MbcAgendas> buscarAgendasComProcEquipColisaoHorario(Date dtAgenda, Short unfSeq, Integer agdSeq, Short gpcSeq) {
		final StringBuilder hql = new StringBuilder(300);
		hql.append(" select agd ").append(" from ").append(MbcProcEspPorCirurgias.class.getSimpleName()).append(" ppc ")
		.append(" 	inner join ppc.").append(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString()).append(" crg ")
		.append(" 	inner join crg.").append(MbcCirurgias.Fields.AGENDA.toString()).append(" agd, ")
		.append(MbcProcedimentoPorGrupo.class.getName()).append(" pgr ")
		.append(" where ppc.").append(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()).append(" = pgr.").append(MbcProcedimentoPorGrupo.Fields.PCI_SEQ)
		.append("    and pgr.").append(MbcProcedimentoPorGrupo.Fields.GPC_SEQ).append(" = :gpcSeq ")
		.append(AND_AGD).append(MbcAgendas.Fields.SEQ.toString()).append(" <> :agdSeq ")
		.append(AND_AGD).append(MbcAgendas.Fields.UNF_SEQ.toString()).append(" = :unfSeq ")
		.append(AND_AGD).append(MbcAgendas.Fields.DTHR_PREV_INICIO.toString()).append(" is not null ")
		.append(AND_AGD).append(MbcAgendas.Fields.DTHR_PREV_FIM.toString()).append(" is not null ")
		.append(AND_AGD).append(MbcAgendas.Fields.IND_SITUACAO.toString()).append(" = :indSituacao ")
		.append(AND_AGD).append(MbcAgendas.Fields.DT_AGENDA.toString()).append(" = :dtAgenda ");
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("gpcSeq", gpcSeq);
		query.setParameter("agdSeq", agdSeq);
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("dtAgenda", dtAgenda);
		query.setParameter("indSituacao", DominioSituacaoAgendas.ES);
		return query.list();
	}
	public List<MbcAgendas> pesquisarAgendasPorPacienteEquipe(DominioSituacaoAgendas[] dominioSituacaoAgendas, Integer pacCodigo, Integer matriculaEquipe, 
			Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProfEquipe, Short seqEspecialidade,	Short seqUnidFuncionalCirugica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, "t1");
		criteria.createAlias(T1 + MbcAgendas.Fields.AGENDAS_DIAGNOSTICOS.toString(), "t2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE, JoinType.INNER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.ESPECIALIDADE.toString(), ESP_MAX, JoinType.INNER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.UNF.toString(), UNF, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.SALA_CIRURGICA.toString(), "SAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "ESP_PROC", JoinType.INNER_JOIN);
		criteria.createAlias("ESP_PROC." + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "PROC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString(), "PROF_ATUA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.PROCEDIMENTO.toString(), "PROCED", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(T1 + MbcAgendas.Fields.AGENDAS_HEMOTERAPIAS.toString(), "HEMO", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.in(T1 + MbcAgendas.Fields.IND_SITUACAO.toString(), dominioSituacaoAgendas));
		criteria.add(Restrictions.eq(T1 + MbcAgendas.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(T1+MbcAgendas.Fields.PUC_SER_MATRICULA.toString(),matriculaEquipe));
		criteria.add(Restrictions.eq(T1+MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigoEquipe));
		criteria.add(Restrictions.eq(T1+MbcAgendas.Fields.PUC_UNF_SEQ.toString(), unfSeqEquipe));
		criteria.add(Restrictions.eq(T1+MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), indFuncaoProfEquipe));
		criteria.add(Restrictions.eq(T1+MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), seqEspecialidade));
		criteria.add(Restrictions.eq(T1+MbcAgendas.Fields.UNF_SEQ.toString(), seqUnidFuncionalCirugica));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarHorariosOcupacaoSalaCirurgica(Date dtAgenda, Short unfSeq, Short sciUnfSeq, Short sciSeqp, Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class);
		criteria.add(Restrictions.eq(MbcAgendas.Fields.DT_AGENDA.toString(), dtAgenda));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.SALA_CIRURGICA_UNF_SEQ.toString(), sciUnfSeq));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString(), sciSeqp));
		criteria.add(Restrictions.ne(MbcAgendas.Fields.SEQ.toString(), agdSeq));
		criteria.add(Restrictions.isNotNull(MbcAgendas.Fields.DTHR_PREV_INICIO.toString()));
		criteria.add(Restrictions.isNotNull(MbcAgendas.Fields.DTHR_PREV_FIM.toString()));
		criteria.add(Restrictions.ne(MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.CA));
		criteria.add(Restrictions.ne(MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.ne(MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.AG));//Adicionado restricao de itens planejados pois no AGHU estamos gerando horarios na inclusao do paciente em agenda e iremos reorganizar os horarios após persistir a agenda atual (17/09/2013)
		return executeCriteria(criteria);
	}
	public Boolean verificarExistenciaPacienteAgendadoNaData(Integer pacCodigo, Date dtAgenda, Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class);		
		criteria.add(Restrictions.eq(MbcAgendas.Fields.DT_AGENDA.toString(), dtAgenda));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.PAC_CODIGO.toString(), pacCodigo));
		if(agdSeq != null){
			criteria.add(Restrictions.ne(MbcAgendas.Fields.SEQ.toString(), agdSeq));
		}
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		Long agendados = executeCriteriaCount(criteria);
		
		if(agendados != null && agendados > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	public List<AgendamentosExcluidosVO> pesquisarAgendamentosExcluidos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PortalPesquisaCirurgiasParametrosVO parametros) {
		DetachedCriteria criteria = createCriteriaPesquisarAgendamentosExcluidos(parametros);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	public Long pesquisarAgendamentosExcluidosCount(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		DetachedCriteria criteria = createCriteriaPesquisarAgendamentosExcluidos(portalPesquisaCirurgiasParametrosVO);
		return executeCriteriaCount(criteria);
	}
	public DetachedCriteria createCriteriaPesquisarAgendamentosExcluidos(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		DetachedCriteria criteria = createViewsAgendamentosExcluidos(portalPesquisaCirurgiasParametrosVO);
		criteria = this.createFiltersEquipeAgendamentos(criteria, portalPesquisaCirurgiasParametrosVO);
		criteria = this.createFiltersAgendamentos(criteria, portalPesquisaCirurgiasParametrosVO);
		if(portalPesquisaCirurgiasParametrosVO.getDataInicio()!=null &&	portalPesquisaCirurgiasParametrosVO.getDataFim()!=null){
			criteria.add(Restrictions.between(MbcAgendas.Fields.ALTERADO_EM.toString(), DateUtil.truncaData(portalPesquisaCirurgiasParametrosVO.getDataInicio()), DateUtil.truncaDataFim(portalPesquisaCirurgiasParametrosVO.getDataFim())));	
		}		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatConvenioSaude.class, "niv");
		ProjectionList subProjection = Projections.projectionList().add(Projections.property(FatConvenioSaude.Fields.CODIGO.toString()));
		subCriteria.setProjection(subProjection);
		subCriteria.add(Restrictions.eqProperty(NIV+FatConvenioSaude.Fields.CODIGO.toString(), 
				AGENDA + MbcAgendas.Fields.FAT_CONVENIO_SAUDE.toString()+ "."+FatConvenioSaude.Fields.CODIGO.toString()));
		if (portalPesquisaCirurgiasParametrosVO.getConvenio() != null) {
			if(DominioConvenio.N.name().equals(portalPesquisaCirurgiasParametrosVO.getConvenio().name())){//Se selecionado "NÃO SUS"
					subCriteria.add(Restrictions.or(Restrictions.eq(NIV+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.C),
									Restrictions.eq(NIV+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.P)));
			}else{
				if(DominioConvenio.S.name().equals(portalPesquisaCirurgiasParametrosVO.getConvenio().name())){//Se selecionado "SUS"
					subCriteria.add(Restrictions.eq(NIV+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
				}
			}
		}	
		criteria.add(Subqueries.exists(subCriteria));
		criteria.setResultTransformer(Transformers.aliasToBean(AgendamentosExcluidosVO.class));
		return criteria;
	}
	public DetachedCriteria createFiltersEquipeAgendamentos(DetachedCriteria criteria, PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO ){
		if (portalPesquisaCirurgiasParametrosVO.getUnfSeq() != null && portalPesquisaCirurgiasParametrosVO.getUnfSeq() != 0	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.UNF_SEQ.toString(), portalPesquisaCirurgiasParametrosVO.getUnfSeq()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucSerMatricula() != null && portalPesquisaCirurgiasParametrosVO.getPucSerMatricula() != 0	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), portalPesquisaCirurgiasParametrosVO.getPucSerMatricula()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo() != null && portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo() != 0	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucUnfSeq() != null && portalPesquisaCirurgiasParametrosVO.getPucUnfSeq() != 0	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_UNF_SEQ.toString(), portalPesquisaCirurgiasParametrosVO.getPucUnfSeq()));
		}
		return criteria;
	}	
	public DetachedCriteria createFiltersAgendamentos(DetachedCriteria criteria, PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO ){
		if (portalPesquisaCirurgiasParametrosVO.getEspSeq() != null && portalPesquisaCirurgiasParametrosVO.getEspSeq() != 0	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), portalPesquisaCirurgiasParametrosVO.getEspSeq()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf() != null && portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf().getCodigo() != null	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPciSeqPortal() != null && portalPesquisaCirurgiasParametrosVO.getPciSeqPortal() != 0	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.EPR_PCI_SEQ.toString(), portalPesquisaCirurgiasParametrosVO.getPciSeqPortal()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPacCodigo() != null &&portalPesquisaCirurgiasParametrosVO.getPacCodigo() != 0	){
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PAC_CODIGO.toString(), portalPesquisaCirurgiasParametrosVO.getPacCodigo()));
		}
		return criteria;
	}	
	private DetachedCriteria createViewsAgendamentosExcluidos(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, "agenda");
		criteria.createAlias(MbcAgendas.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias(MbcAgendas.Fields.PUC_SERVIDOR.toString(),"pucServidor");
		criteria.createAlias(MbcAgendas.Fields.PUC_SERVIDOR_PESSOA_FISICA.toString(),"pessoaFisica");
		criteria.createAlias(MbcAgendas.Fields.PACIENTE.toString(), "paciente");
		criteria.createAlias(MbcAgendas.Fields.PROCEDIMENTO.toString(), "procedimentoCirurgico");
		criteria.createAlias(MbcAgendas.Fields.UNF.toString(), "unidadeFuncional");
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(AGENDA+MbcAgendas.Fields.SEQ.toString()),AgendamentosExcluidosVO.Fields.AGENDA_SEQ.toString())
		.add(Projections.property(MbcAgendas.Fields.ESPECIALIDADE_SIGLA.toString()),AgendamentosExcluidosVO.Fields.SIGLA_ESPECIALIDADE.toString())
		.add(Projections.property(MbcAgendas.Fields.ESPECIALIDADE_NOME.toString()),AgendamentosExcluidosVO.Fields.NOME_ESPECIALIDADE.toString())
		.add(Projections.property(RapServidores.Fields.NOME_PESSOA_FISICA.toString()),AgendamentosExcluidosVO.Fields.EQUIPE.toString())
		.add(Projections.property(MbcAgendas.Fields.PACIENTE_NOME.toString()),AgendamentosExcluidosVO.Fields.PACIENTE.toString())
		.add(Projections.property(MbcAgendas.Fields.PACIENTE_PRONTUARIO.toString()),AgendamentosExcluidosVO.Fields.PRONTUARIO.toString())
		.add(Projections.property(AGENDA+MbcAgendas.Fields.ALTERADO_EM.toString()),AgendamentosExcluidosVO.Fields.DT_EXCLUSAO.toString())
		.add(Projections.property(AGENDA+MbcAgendas.Fields.IND_SITUACAO.toString()),AgendamentosExcluidosVO.Fields.SITUACAO.toString())
		.add(Projections.property(AGENDA+MbcAgendas.Fields.REGIME.toString()),AgendamentosExcluidosVO.Fields.REGIME.toString())
		.add(Projections.property(MbcAgendas.Fields.PROCEDIMENTO_DESCRICAO.toString()),AgendamentosExcluidosVO.Fields.VPE_DESCRICAO.toString())
		.add(Projections.property(MbcAgendas.Fields.UNF_SIGLA.toString()),AgendamentosExcluidosVO.Fields.UNIDADE_FUNCIONAL.toString());
		criteria.setProjection(projection);	
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_EXCLUSAO.toString(),Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_GERADO_SISTEMA.toString(),Boolean.FALSE));
		
		return criteria;
	}	
	public List<MbcAgendas> getPesquisarPacientesCirurgiaUnion1(Integer pacCodigo) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class,AGD_TABLE);
		if(pacCodigo != null) {
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.COD_PACIENTE.toString(), pacCodigo));
		}
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		Criterion c1 = Restrictions.in(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), new DominioSituacaoAgendas[]{DominioSituacaoAgendas.AG,DominioSituacaoAgendas.LE});
		Criterion c2 = Restrictions.eq(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.ES);
		criteria.add(Restrictions.or(c1, c2));
		List<MbcAgendas> listAux = new ArrayList<MbcAgendas>();
		List<MbcAgendas> list = this.executeCriteria(criteria);
		for(MbcAgendas agenda : list){
			if(agenda.getIndSituacao().equals(DominioSituacaoAgendas.ES)){
				DetachedCriteria criteria2 = DetachedCriteria.forClass(MbcCirurgias.class,"cir");
				criteria2.add(Restrictions.eq(CIR+MbcCirurgias.Fields.AGD_SEQ.toString(), agenda.getSeq()));
				criteria2.add(Restrictions.eq(CIR+MbcCirurgias.Fields.DATA.toString(), agenda.getDtAgenda()));
				criteria2.add(Restrictions.isNull(CIR+MbcCirurgias.Fields.MTC_SEQ.toString()));
				if(! executeCriteriaExists(criteria2)){
					listAux.add(agenda);
				}
			}else{
				listAux.add(agenda);
			}
		}
		return listAux;
	}
	public List<MbcProfCirurgias> getPesquisarPacientesCirurgiaUnion2(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class,"pcg");
		criteria.createAlias("pcg."+MbcProfCirurgias.Fields.CIRURGIA.toString(), "cir", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(CIR+MbcCirurgias.Fields.PAC_CODIGO.toString(), codigo));
		criteria.add(Restrictions.isNotNull(CIR+MbcCirurgias.Fields.SITUACAO.toString()));
		return this.executeCriteria(criteria);
	}
	public MbcAgendas obterMbcAgendaGeradaPeloSistemaporCirurgia(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class,"cir");
		criteria.createAlias(CIR+MbcCirurgias.Fields.AGENDA.toString(), AGD_TABLE, Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(CIR+MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_GERADO_SISTEMA.toString(), Boolean.TRUE));
		Object objCirg= executeCriteriaUniqueResult(criteria);
		if(objCirg != null){
			return ((MbcCirurgias) objCirg).getAgenda();
		}else{
			return null;
		}
	}
	public List<MbcAgendas> buscarAgendasPorUnfSeqSalaData(PortalPlanejamentoCirurgiasTurnoVO voTurno, Short unfSeq, Date data, MbcControleEscalaCirurgica mbcControleEscalaCirurgica) {
		final StringBuilder sbQuery = new StringBuilder(1000);
		sbQuery.append(" SELECT DISTINCT agd.* FROM agh.mbc_agendas	agd "
		  	+ " join agh.mbc_caracteristica_sala_cirgs cas on (agd.sci_seqp = cas.sci_seqp) "
		  	+ " join agh.mbc_horario_turno_cirgs htc on (cas.htc_turno = htc.turno) "
		  	+ " WHERE 1=1 AND agd.dt_agenda = ")
		  	.append(getSqlData(data)).append(" and ((agd.dthr_prev_inicio is null and agd.ordem_overbooking is not null) ");
		if (isOracle()) {
			sbQuery.append(" or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) >= to_number(to_char(htc.horario_inicial,'hh24mi')) and to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) < to_number(to_char(htc.horario_final,'hh24mi'))) "
				+ "or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) <= to_number(to_char(htc.horario_inicial,'hh24mi')) and to_number(to_char(agd.dthr_prev_fim,'hh24mi')) > to_number(to_char(htc.horario_inicial,'hh24mi'))))  ");
		} else {
			sbQuery.append(" or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') >= to_number(to_char(htc.horario_inicial,'hh24mi'),'9999') and to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') < to_number(to_char(htc.horario_final,'hh24mi'),'9999')) "
				+ " or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') <= to_number(to_char(htc.horario_inicial,'hh24mi'),'9999') and to_number(to_char(agd.dthr_prev_fim,'hh24mi'),'9999') > to_number(to_char(htc.horario_inicial,'hh24mi'),'9999')))  ");
		}
		sbQuery.append(" AND agd.ind_exclusao 	= 'N' ");
		if(mbcControleEscalaCirurgica == null){
			sbQuery.append(" AND agd.ind_situacao in ('AG','ES') ");
		}else{
			sbQuery.append(" AND agd.ind_situacao = 'ES' ");
		}
		sbQuery.append(" AND agd.unf_seq = ")
		.append(unfSeq).append(" AND agd.sci_seqp = ").append(Short.valueOf(voTurno.getSala()))
		.append(" AND htc.turno = '").append(voTurno.getTurno()).append("' AND htc.unf_seq = ").append(unfSeq);
		javax.persistence.Query query = createNativeQuery(sbQuery.toString(), MbcAgendas.class);
		List<MbcAgendas> agendas = query.getResultList();
		return agendas;
	}
	public List<CirurgiasCanceladasAgendaMedicoVO> pesquisarCirgsCanceladasByMedicoEquipe(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer serMatricula, Short serVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		DetachedCriteria criteria = this.montarCriteriaParaPesquisarCirgsCanceladasByMedicoEquipe(serMatricula, serVinCodigo, pucUnfSeq, indFuncaoProf, espSeq, unfSeq, pacCodigo);	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MbcAgendas.Fields.SEQ.toString()), CirurgiasCanceladasAgendaMedicoVO.Fields.AGD_SEQ.toString())
				.add(Projections.property(PAC+AipPacientes.Fields.NOME.toString()), CirurgiasCanceladasAgendaMedicoVO.Fields.NOME.toString())		
				.add(Projections.property(PAC+AipPacientes.Fields.PRONTUARIO.toString()), CirurgiasCanceladasAgendaMedicoVO.Fields.PRONTUARIO.toString())	
				.add(Projections.property(PCI+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasCanceladasAgendaMedicoVO.Fields.PCI_DESCRICAO.toString())		 
				.add(Projections.property(MbcAgendas.Fields.DTHR_INCLUSAO.toString()), CirurgiasCanceladasAgendaMedicoVO.Fields.DT_INCLUSAO.toString())	 
		);
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasCanceladasAgendaMedicoVO.class));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	private DetachedCriteria montarCriteriaParaPesquisarCirgsCanceladasByMedicoEquipe(Integer serMatricula, Short serVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class);
		criteria.createAlias(MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE);						
		criteria.createAlias(MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), EPR_TABLE);
		criteria.createAlias(EPR+MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), PCI_TABLE);
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.CA));
		criteria.add(Restrictions.eq(MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if(serMatricula != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), serMatricula));
		}	
		if(serVinCodigo != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), serVinCodigo));
		}	
		if(pucUnfSeq != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_UNF_SEQ.toString(), pucUnfSeq));
		}	
		if(indFuncaoProf != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), indFuncaoProf));
		}	
		if(espSeq != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		}	
		if(unfSeq != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if(pacCodigo != null) {
			criteria.add(Restrictions.eq(MbcAgendas.Fields.COD_PACIENTE.toString(), pacCodigo));
		}
		return criteria;
	}
	private static String getSqlData(Date data){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final String dataFormatada = dateFormat.format(data);
		return "TO_DATE('" + dataFormatada + "', 'dd/mm/yyyy hh24:mi:ss')";
	}
	public Long pesquisarCirgsCanceladasByMedicoEquipeCount(Integer serMatricula, Short serVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		DetachedCriteria criteria = this.montarCriteriaParaPesquisarCirgsCanceladasByMedicoEquipe(serMatricula, serVinCodigo, pucUnfSeq, indFuncaoProf, espSeq, unfSeq, pacCodigo);	
		return executeCriteriaCount(criteria);
	}
	public List<MbcAgendas> listarAgendaPorUnidadeEspecialidadeEquipePaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		DetachedCriteria criteria = criarCriteriaAgendaPorUnidadeEspecialidadeEquipePaciente(pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo, LE);
		if ("dthrInclusao".equals(orderProperty)) {
			criteria.addOrder(asc ? Order.asc(AGD.concat(MbcAgendas.Fields.DTHR_INCLUSAO.toString())) : Order.desc(AGD.concat(MbcAgendas.Fields.DTHR_INCLUSAO.toString())));
		} else {
			criteria.addOrder(asc ? Order.asc(PAC.concat(AipPacientes.Fields.NOME.toString())) : Order.desc(PAC.concat(AipPacientes.Fields.NOME.toString())));
		}
		if (firstResult == null && maxResult == null) {
			return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, maxResult, null, asc);
		}		
	}
	public List<MbcAgendas> listarAgendaPacientesEmListaProcedimentosCancelados(String orderProperty, boolean asc, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		DetachedCriteria criteria = criarCriteriaAgendaPorUnidadeEspecialidadeEquipePaciente(pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo, DominioSituacaoAgendas.CA);
		if ("dthrInclusao".equals(orderProperty)) {
			criteria.addOrder(asc ? Order.asc(AGD.concat(MbcAgendas.Fields.DTHR_INCLUSAO.toString())) : Order.desc(AGD.concat(MbcAgendas.Fields.DTHR_INCLUSAO.toString())));
		} else {
			criteria.addOrder(asc ? Order.asc(PAC.concat(AipPacientes.Fields.NOME.toString())) : Order.desc(PAC.concat(AipPacientes.Fields.NOME.toString())));
		}
		return executeCriteria(criteria);
	}
	public Long listarAgendaPorUnidadeEspecialidadeEquipePacienteCount(Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		DetachedCriteria criteria = criarCriteriaAgendaPorUnidadeEspecialidadeEquipePaciente(
				pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf,
				espSeq, unfSeq, pacCodigo, DominioSituacaoAgendas.LE);
		return executeCriteriaCount(criteria);
	}
	private DetachedCriteria criarCriteriaAgendaPorUnidadeEspecialidadeEquipePaciente(
			Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq,
			Short unfSeq, Integer pacCodigo, DominioSituacaoAgendas situacaoAgendas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD.concat(MbcAgendas.Fields.PACIENTE.toString()), PAC_TABLE);
		criteria.createAlias(AGD.concat(MbcAgendas.Fields.PROCEDIMENTO.toString()), PCI_TABLE);
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), situacaoAgendas));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), pucSerMatricula));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), pucSerVinCodigo));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_UNF_SEQ.toString(), pucUnfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), pucIndFuncaoProf));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		if(pacCodigo != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		return criteria;
	}
	public List<MbcAgendas> pesquisarAgendamentosPorData(Date dataBase,	MbcProfAtuaUnidCirgs atuaUnidCirgs,
			Short espSeq, Short unfSeq, Short sciSeqp, Boolean ordenacaoEscala) {
		DetachedCriteria criteria = this.obterCriteriaAgendamentosPorData(dataBase, atuaUnidCirgs, espSeq, unfSeq, sciSeqp, ordenacaoEscala);
		criteria.addOrder(Order.desc(MbcAgendas.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc(MbcAgendas.Fields.DTHR_PREV_INICIO.toString()));
		criteria.addOrder(Order.asc(MbcAgendas.Fields.ORDEM_OVERBOOKING.toString()));
		criteria.addOrder(Order.asc(MbcAgendas.Fields.DTHR_INCLUSAO.toString()));
		return executeCriteria(criteria);
	}
	public Boolean existeAgendamentosPorData(Date dataBase,	MbcProfAtuaUnidCirgs atuaUnidCirgs,	Short espSeq, Short unfSeq, Short sciSeqp, Boolean ordenacaoEscala) {
		DetachedCriteria criteria = this.obterCriteriaAgendamentosPorData(dataBase, atuaUnidCirgs, espSeq, unfSeq, sciSeqp, ordenacaoEscala);
		Long count = this.executeCriteriaCount(criteria);
		return count != null && count > 0;
	}
	private DetachedCriteria obterCriteriaAgendamentosPorData(Date dataBase, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq, Short unfSeq, Short sciSeqp, Boolean ordenacaoEscala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD.concat(MbcAgendas.Fields.PROCEDIMENTO.toString()), PCI_TABLE);
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.DT_AGENDA.toString(), dataBase));
		if(ordenacaoEscala != null){
			if(!ordenacaoEscala){
				criteria.add(Restrictions.in(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), new DominioSituacaoAgendas[]{DominioSituacaoAgendas.ES,DominioSituacaoAgendas.AG}));
			} else if(ordenacaoEscala){
				criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.AG));
			}
		}
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if(atuaUnidCirgs != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
		}
		if(espSeq != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		}
		if(sciSeqp != null){
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString(), sciSeqp));
		}
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		return criteria;
	}
	public Date getHoraFinal(Date date) {
		if(date != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if(cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
				return DateUtil.obterDataComHoraFinal(date);
			}
		}
		return date;
	}
	public List<Byte> buscarOrdemOverbook(Date dataAgenda, Short salaSeqp, Short salaUnfSeq, Short unfSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.DT_AGENDA.toString(), dataAgenda));
		criteria.add(Restrictions.isNotNull(AGD+MbcAgendas.Fields.ORDEM_OVERBOOKING.toString()));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString(), salaSeqp));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SALA_CIRURGICA_UNF_SEQ.toString(), salaUnfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(),Boolean.FALSE));
		criteria.addOrder(Order.desc(MbcAgendas.Fields.ORDEM_OVERBOOKING.toString()));
		Projection projection = Projections.property(AGD+MbcAgendas.Fields.ORDEM_OVERBOOKING.toString());
		criteria.setProjection(projection);
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarEscalaAgendas(MbcAgendas agenda, Date dataInicio, Date dataFim, Boolean insereAtualizaAgendaEscala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD + MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE);
		criteria.createAlias(AGD + MbcAgendas.Fields.PROCEDIMENTO.toString(), PCI_TABLE);
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.DT_AGENDA.toString(), DateUtil.truncaData(agenda.getDtAgenda())));		
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString(), agenda.getSalaCirurgica().getId().getSeqp()));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.SALA_CIRURGICA_UNF_SEQ.toString(), agenda.getSalaCirurgica().getId().getUnfSeq()));
		criteria.add(Restrictions.and(Restrictions.ge(AGD + MbcAgendas.Fields.DTHR_PREV_INICIO.toString(), dataInicio), 
				Restrictions.lt(AGD + MbcAgendas.Fields.DTHR_PREV_INICIO.toString(), dataFim)));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.UNF_SEQ.toString(), agenda.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if(!insereAtualizaAgendaEscala){
			criteria.add(Restrictions.in(AGD + MbcAgendas.Fields.IND_SITUACAO.toString(), new DominioSituacaoAgendas[]{DominioSituacaoAgendas.ES, DominioSituacaoAgendas.AG}));
		} else {
			criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.ES));
		}
		criteria.addOrder(Order.desc(MbcAgendas.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc(MbcAgendas.Fields.DTHR_PREV_INICIO.toString()));
		criteria.addOrder(Order.asc(MbcAgendas.Fields.ORDEM_OVERBOOKING.toString()));
		criteria.addOrder(Order.asc(MbcAgendas.Fields.DTHR_INCLUSAO.toString()));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarAgendasPlanejadasParaEscala(Date dtAgenda, Integer pucSerMatricula, Short pucSerVinCodigo,
			Short pucUnfSeq, DominioFuncaoProfissional funProf, Short espSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD.concat(MbcAgendas.Fields.PACIENTE.toString()), PAC_TABLE);
		criteria.createAlias(AGD.concat(MbcAgendas.Fields.PROCEDIMENTO.toString()), PCI_TABLE);
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.DT_AGENDA.toString(), dtAgenda));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.AG));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), pucSerMatricula));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), pucSerVinCodigo));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_UNF_SEQ.toString(), pucUnfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString(), funProf));
		if(espSeq != null) {
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		}
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		final StringBuilder orderBy = new StringBuilder(150);
		orderBy.append(" this_.ind_situacao, "
			+ " case this_.IND_PRIORIDADE when 'S' then 1 "
			+ "	when 'N' then 2 end, " 
			+ " this_.dthr_prev_inicio, "
			+ " this_.dthr_inclusao");
		criteria.addOrder(OrderBySql.sql(orderBy.toString()));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarAgendasEmEscala(Short unfSeq, Date dtAgenda, Short sciUnfSeq, Short sciSeqp,
			Short espSeq, Boolean indGeradoSistema, Integer pucSerMatricula, Short pucSerVinCodigo, Boolean orderInicio,
			Boolean apenasOverbookings) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.DT_AGENDA.toString(), dtAgenda));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SALA_CIRURGICA_UNF_SEQ.toString(), sciUnfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString(), sciSeqp));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.ES));
		if(espSeq != null) {
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.ESP_SEQ.toString(), espSeq));
		}
		if(indGeradoSistema != null) {
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_GERADO_SISTEMA.toString(), indGeradoSistema));
		}
		if(pucSerMatricula != null && pucSerVinCodigo != null) {
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), pucSerMatricula));
			criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), pucSerVinCodigo));
		}
		if(apenasOverbookings) {
			criteria.add(Restrictions.isNull(AGD+MbcAgendas.Fields.DTHR_PREV_INICIO.toString()));
		}
		if(orderInicio) {
			criteria.addOrder(Order.asc(AGD+MbcAgendas.Fields.DTHR_PREV_INICIO.toString()));
		} else {
			criteria.addOrder(Order.asc(AGD+MbcAgendas.Fields.DTHR_PREV_FIM.toString()));
		}
		criteria.addOrder(Order.asc(AGD+MbcAgendas.Fields.ORDEM_OVERBOOKING.toString()));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarAgendasEmEscalaGeradoSistema(Short unfSeq, Date dtAgenda, Short sciUnfSeq, Short sciSeqp,
			Integer pucSerMatricula, Short pucSerVinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.DT_AGENDA.toString(), dtAgenda));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SALA_CIRURGICA_UNF_SEQ.toString(), sciUnfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString(), sciSeqp));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.ES));
		criteria.add(Restrictions.or(Restrictions.eq(AGD+MbcAgendas.Fields.IND_GERADO_SISTEMA.toString(), Boolean.TRUE),
				Restrictions.or(Restrictions.ne(AGD+MbcAgendas.Fields.PUC_SER_MATRICULA.toString(), pucSerMatricula),
						Restrictions.ne(AGD+MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString(), pucSerVinCodigo))));
		criteria.addOrder(Order.asc(AGD+MbcAgendas.Fields.DTHR_PREV_INICIO.toString()));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarAgendasPorUnfSeqSalaData(	MbcHorarioTurnoCirg turno, MbcSalaCirurgica sala, Short unidadeFiltro,Date data) {
		 final StringBuilder sbQuery = new StringBuilder(1000);
		sbQuery.append("	SELECT DISTINCT agd.* FROM agh.mbc_agendas agd "  
			+ " join agh.mbc_horario_turno_cirgs htc on (agd.unf_seq = htc.unf_seq) " 
			+ " WHERE 1=1 AND agd.dt_agenda	= ")
			.append(getSqlData(data))
			.append(" and ((agd.dthr_prev_inicio is null and agd.ordem_overbooking is not null) ");
		if (isOracle()) {
			final StringBuilder horarioTurnoFinal = new StringBuilder("case to_number(to_char(htc.horario_final,'hh24mi')) when 0 then to_number('2359') else to_number(to_char(htc.horario_final,'hh24mi')) end");
			sbQuery.append(" or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) >= to_number(to_char(htc.horario_inicial,'hh24mi')) and to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) < ")
				.append(horarioTurnoFinal).append(") or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) <= to_number(to_char(htc.horario_inicial,'hh24mi')) and to_number(to_char(agd.dthr_prev_fim,'hh24mi')) > to_number(to_char(htc.horario_inicial,'hh24mi'))))  ");
		} else {
			final StringBuilder horarioTurnoFinalPg = new StringBuilder("case to_number(to_char(htc.horario_final,'hh24mi'), '9999') when 0 then to_number('2359', '9999') else to_number(to_char(htc.horario_final,'hh24mi'), '9999') end");
			sbQuery.append(" or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') >= to_number(to_char(htc.horario_inicial,'hh24mi'),'9999') "
				+ " and to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') < ")
				.append(horarioTurnoFinalPg).append(") or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') <= to_number(to_char(htc.horario_inicial,'hh24mi'),'9999') "
				+ " and to_number(to_char(agd.dthr_prev_fim,'hh24mi'),'9999') > to_number(to_char(htc.horario_inicial,'hh24mi'),'9999'))) ");
		}
		sbQuery.append(" AND agd.ind_exclusao = 'N' " + "AND agd.ind_situacao in ('AG','ES') "
			+ " AND agd.unf_seq = ").append(unidadeFiltro).append(" AND agd.sci_seqp = ")
			.append(Short.valueOf(sala.getId().getSeqp())).append(" AND htc.turno = '")
			.append(turno.getId().getTurno()).append("' AND htc.unf_seq = ").append(unidadeFiltro).append(" order by agd.dthr_prev_inicio ");
		javax.persistence.Query query = createNativeQuery(sbQuery.toString(), MbcAgendas.class);
		List<MbcAgendas> agendas = query.getResultList();
		return agendas;
	}
	public List<DominioRegimeProcedimentoCirurgicoSus> buscarRegimeSusPorId(Integer seq) {
		List<DominioRegimeProcedimentoCirurgicoSus> regimes = new ArrayList<DominioRegimeProcedimentoCirurgicoSus>();
		regimes.addAll(buscarRegimeSusUnion1(seq));
		regimes.addAll(buscarRegimeSusUnion2(seq));
		Collections.sort(regimes, Collections.reverseOrder(new Comparator<DominioRegimeProcedimentoCirurgicoSus>() {
            @Override
            public int compare(DominioRegimeProcedimentoCirurgicoSus o1, DominioRegimeProcedimentoCirurgicoSus o2) {
            	if(o1 == null) {
            		return -1;
            	}
            	if(o2 == null) {
            		return 1;
            	}
            	return o1.getOrdem().compareTo(o2.getOrdem());
            }
		}));
		return regimes;
	}
	private List<DominioRegimeProcedimentoCirurgicoSus> buscarRegimeSusUnion1(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), EPR_TABLE);
		criteria.createAlias(EPR + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), PCI_TABLE);
		criteria.setProjection(Projections.property(PCI+MbcProcedimentoCirurgicos.Fields.REGIME_PROCED_SUS.toString()));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
	private List<DominioRegimeProcedimentoCirurgicoSus> buscarRegimeSusUnion2(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD + MbcAgendas.Fields.AGENDA_PROCEDIMENTO, "agt");
		criteria.createAlias("agt." + MbcAgendaProcedimento.Fields.MBC_ESPECIALIDADE_PROC_CIRGS.toString(), EPR_TABLE);
		criteria.createAlias(EPR + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), PCI_TABLE);
		criteria.setProjection(Projections.property(PCI+MbcProcedimentoCirurgicos.Fields.REGIME_PROCED_SUS.toString()));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarRegimeSusPacientePorId(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD + MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE);
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
	public List<MbcAgendas> buscarDatasAgendaEscala(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createAlias(AGD + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), EPR_TABLE);
		criteria.createAlias(EPR + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), PCI_TABLE);
		criteria = this.createFiltersEquipeAgendamentos(criteria, portalPesquisaCirurgiasParametrosVO);
		criteria = this.createFiltersAgendamentos(criteria, portalPesquisaCirurgiasParametrosVO);
		criteria.add(Restrictions.ne(AGD+MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.LE));
		criteria.add(Restrictions.eq(AGD+MbcAgendas.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		return executeCriteria(criteria);
	}
	
	public MbcAgendas consultarInformacoesAgenda(Integer seqAgenda) {
		ConsultarInformacaoAgendaBuilder builder = new ConsultarInformacaoAgendaBuilder();
		builder.setSeq(seqAgenda);
		DetachedCriteria criteria = builder.build();
		return (MbcAgendas) executeCriteriaUniqueResult(criteria);
	}
	public List<SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO> listarMateriaisOrteseProteseOPME(Integer agendasSeq, Integer grupoMat){
		StringBuilder hql = new StringBuilder(1500);
		hql.append("SELECT CASE IRO.IND_REQUERIDO WHEN 'NOV' then '(Nova Solicitação de Material) '||iro.SOLC_NOVO_MAT WHEN 'ADC' "
			+ " then '(Material Adicionado pelo Usuário) '||mat.CODIGO||' - '||mat.NOME ELSE '(ROMP '||iph.COD_TABELA||')'|| "
			+ " CASE WHEN COALESCE(MAT.CODIGO,-1) = -1 THEN ' '||IPH.DESCRICAO else ' | '||MAT.CODIGO ||' - '||MAT.NOME "
			+ " END END orteseProt, COALESCE(mio.QTD_SOLC, iro.QTD_SOLC) qtdSolic, "
			+ " CASE IRO.IND_REQUERIDO WHEN 'NOV' THEN 1 WHEN 'ADC' THEN 2 ELSE 3 END AS reqOrder "
			+ " FROM AGH.MBC_AGENDAS AGD  JOIN AGH.MBC_REQUISICAO_OPMES ROP ON ROP.AGD_SEQ = AGD.SEQ "
			+ " JOIN AGH.MBC_ITENS_REQUISICAO_OPMES IRO ON ROP.SEQ = IRO.ROP_SEQ " + " LEFT JOIN AGH.MBC_MATERIAIS_ITEM_OPMES MIO ON IRO.SEQ = MIO.IRO_SEQ AND MIO.QTD_SOLC > 0 "
			+ " LEFT JOIN AGH.SCO_MATERIAIS MAT ON MIO.MAT_CODIGO = MAT.CODIGO " + " LEFT JOIN AGH.FAT_ITENS_PROCED_HOSPITALAR IPH ON IRO.IPH_PHO_SEQ = IPH.PHO_SEQ AND IRO.IPH_SEQ = IPH.SEQ "
			+ " WHERE  AGD.SEQ = :agendasSeq" + " AND IRO.QTD_SOLC > 0 "
			+ " AND IRO.IND_AUTORIZADO = 'S' " + " ORDER BY reqOrder, IPH.COD_TABELA, MAT.CODIGO ");
	    SQLQuery query = createSQLQuery(hql.toString());
	    query.setInteger("agendasSeq", agendasSeq);	    
		query.addScalar("qtdSolic", IntegerType.INSTANCE);
		query.addScalar("orteseProt", StringType.INSTANCE);		
		query.setResultTransformer(Transformers.aliasToBean(SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO.class));
		return query.list();
	}
	public MbcAgendas obterAgendaPorSeq(Integer agdSeq) {
		MbcAgendas agenda = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, "ag");
		criteria.createAlias(AG+MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE, JoinType.INNER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.ESPECIALIDADE.toString(), ESP_MAX, JoinType.INNER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.UNF.toString(), UNF, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.SALA_CIRURGICA.toString(), "SAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.AGENDAS_DIAGNOSTICOS.toString(), AGD_TABLE, JoinType.LEFT_OUTER_JOIN); 
		criteria.createAlias(AG+MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "ESP_PROC", JoinType.INNER_JOIN);
		criteria.createAlias("ESP_PROC."+MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "PROC");
		criteria.createAlias(AG+MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString(), "PROF_ATUA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.PROCEDIMENTO.toString(), "PROCED", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.AGENDAS_HEMOTERAPIAS.toString(), "HEMO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.CONVENIO_SAUDE_PLANO.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(AG+MbcAgendas.Fields.REQUISICAO_OPME.toString(), "OPME", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AG+MbcAgendas.Fields.SEQ.toString(), agdSeq));		
		List<MbcAgendas> listaRetorno = executeCriteria(criteria);
		if (listaRetorno != null && !listaRetorno.isEmpty()) {
			agenda = listaRetorno.get(0);
			agenda.getItensProcedHospitalar();
			//agenda.getRequisicoesOpmes();
			if(agenda.getItensProcedHospitalar() != null){
				agenda.getItensProcedHospitalar().getCodTabela();
				agenda.getItensProcedHospitalar().getDescricao();
			}
		}		
		return agenda;
	}
	public MbcAgendas obterAgendaPorSeqRemarcar(Integer agdSeq) {
		MbcAgendas agenda = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, "ag");
		criteria.createAlias(AG+MbcAgendas.Fields.PACIENTE.toString(), PAC_TABLE, JoinType.INNER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.ESPECIALIDADE.toString(), ESP_MAX, JoinType.INNER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.UNF.toString(), UNF, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.SALA_CIRURGICA.toString(), "SAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.AGENDAS_DIAGNOSTICOS.toString(), AGD_TABLE, JoinType.LEFT_OUTER_JOIN); 
		criteria.createAlias(AG+MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "ESP_PROC", JoinType.INNER_JOIN);
		criteria.createAlias("ESP_PROC."+MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "PROC");
		criteria.createAlias(AG+MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString(), "PROF_ATUA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.PROCEDIMENTO.toString(), "PROCED", JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(AG+MbcAgendas.Fields.AGENDAS_HEMOTERAPIAS.toString(), "HEMO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AG+MbcAgendas.Fields.CONVENIO_SAUDE_PLANO.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(AG+MbcAgendas.Fields.REQUISICAO_OPME.toString(), "OPME", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AG+MbcAgendas.Fields.SEQ.toString(), agdSeq));		
		List<MbcAgendas> listaRetorno = executeCriteria(criteria);
		if (listaRetorno != null && !listaRetorno.isEmpty()) {
			agenda = listaRetorno.get(0);
			agenda.getItensProcedHospitalar();
			//agenda.getRequisicoesOpmes();
			if(agenda.getItensProcedHospitalar() != null){
				agenda.getItensProcedHospitalar().getCodTabela();
				agenda.getItensProcedHospitalar().getDescricao();
			}
		}		
		return agenda;
	}
	@SuppressWarnings("unchecked")
	public List<Integer> buscaDadosProcessoAutorizacaoOpme(BigDecimal prazoPlanCirg) throws ApplicationBusinessException {
		final StringBuffer sql = new StringBuffer(1000);		
		sql.append(
				"SELECT AGH.MBC_REQUISICAO_OPMES.SEQ FROM AGH.MBC_REQUISICAO_OPMES JOIN AGH.MBC_AGENDAS ON AGH.MBC_REQUISICAO_OPMES.AGD_SEQ = AGH.MBC_AGENDAS.SEQ  WHERE AGH.MBC_REQUISICAO_OPMES.DT_FIM IS NULL AND AGH.MBC_REQUISICAO_OPMES.IND_SITUACAO  = 'INCOMPATIVEL' AND ( AGH.MBC_REQUISICAO_OPMES.WFL_SEQ    IS NULL OR AGH.MBC_REQUISICAO_OPMES.IND_AUTORIZADO = 'N') AND AGH.MBC_AGENDAS.IND_EXCLUSAO = 'N'  AND AGH.MBC_AGENDAS.DT_AGENDA >= DATE_TRUNC('DAY', CURRENT_DATE) AND AGH.MBC_AGENDAS.DT_AGENDA <= CURRENT_DATE + interval '"
						+ prazoPlanCirg + " DAYS' ORDER BY 1");
		SQLQuery query = createSQLQuery(sql.toString());
		List<Object> result = query.list();
		List<Integer> requisicoes = new ArrayList<Integer>();
		for (Object object : result) {
			requisicoes.add(Integer.valueOf(object.toString()));
		}
		return requisicoes;
	}
	public MbcAgendas retornarAgendaPorAgdSeq(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, AGD_TABLE);
		criteria.createCriteria(AGD + MbcAgendas.Fields.REQUISICAO_OPME.toString(), RQC_OPME_TABLE, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AGD + MbcAgendas.Fields.ESPECIALIDADE.toString(), ESP, JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AGD + MbcAgendas.Fields.SEQ.toString(), agdSeq));
		MbcAgendas agenda = (MbcAgendas) executeCriteriaUniqueResult(criteria);
		super.initialize(agenda.getEspecialidade().getSeq());
		return agenda;
	}
	public List<NotificacoesGeraisVO> listarNotificacoesCirurgiaParaBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias(CRG + MbcCirurgias.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias(CRG + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias(CRG + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), UNF);
		criteria.createAlias("AGD." + MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString(), "PUC");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "EPR");
		criteria.createAlias("EPR." + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		criteria.createAlias("PUC." + MbcProfAtuaUnidCirgs.Fields.PROF_CIRURGIAS.toString(), "PCG");
		criteria.createAlias("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias(CRG + MbcCirurgias.Fields.ANEST_CIRURGIAS.toString(), "ANT");
		criteria.createAlias("ANT." + MbcAnestesiaCirurgias.Fields.TIPO_ANESTESIA.toString(), "TAN");
		criteria.setProjection(Projections.projectionList().add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()), NotificacoesGeraisVO.Fields.SEQ.toString())
				.add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), NotificacoesGeraisVO.Fields.DESCRICAO.toString())
				.add(Projections.property(CRG + MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()), NotificacoesGeraisVO.Fields.DT_FIM_CIRURGIA.toString()));
		criteria.add(Restrictions.eq(CRG + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(CRG + MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()));
		criteria.addOrder(Order.asc("CRG." + MbcCirurgias.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		return executeCriteria(criteria);
	}
}