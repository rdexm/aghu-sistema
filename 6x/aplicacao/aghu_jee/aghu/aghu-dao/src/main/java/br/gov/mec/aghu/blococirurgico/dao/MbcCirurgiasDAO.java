package br.gov.mec.aghu.blococirurgico.dao;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Distinct;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiasExposicaoRadiacaoIonizanteVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgiaPdtSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcRelatCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcTotalCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaConcluidaHojeVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaRecuperacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesCirurgiaUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.ProtocoloEntregaNotasDeConsumoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaComRetornoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasPendenteRetornoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesComCirurgiaPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedAgendPorUnidCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.TelaListaCirurgiaVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioConvenio;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProtocoloEntregaNotasConsumo;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.exames.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcMaterialPorCirurgia;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.view.VListaMbcCirurgias;

@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects", "PMD.NcssTypeCount" })
public class MbcCirurgiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcCirurgias> {
	
	private static final long serialVersionUID = -6520779999200201260L;
	
	private static final Log LOG = LogFactory.getLog(MbcCirurgiasDAO.class);
	

	public boolean procedimentoCirurgicoExigeInternacao(AghAtendimentos atendimento) {
		if (atendimento == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.ATENDIMENTO.toString(), "atendimento");
		criteria.createAlias(MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "procEspPorCirurgias");
		criteria.createAlias(MbcCirurgias.Fields.PROC_CIRURGICO.toString(), "procedimentoCirurgico");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq("procEspPorCirurgias." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.DESC));
		criteria.add(Restrictions.eq("procEspPorCirurgias." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("procedimentoCirurgico." + MbcProcedimentoCirurgicos.Fields.REGIME_PROCED_SUS.toString(), "I"));
		return executeCriteriaCount(criteria) > 0;
	}

	/** Consulta equivalente ao cursor cur_acr da procedure MPMP_LISTA_CIRG_REALIZADAS, contida no arquivo MPMF_SUMARIO_ALTA.pll. */
	public List<Object[]> pesquisarRealizadasPorProcedimentoEspecial(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias(MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "PPC");
		criteria.createAlias("PPC." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "PCI", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		ProjectionList pList = Projections.projectionList();
		pList.add(Property.forName("PPC." + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString()));
		pList.add(Property.forName("PPC." + MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()));
		pList.add(Property.forName("PPC." + MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString()));
		pList.add(Property.forName("PPC." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()));
		pList.add(Property.forName("CRG." + MbcCirurgias.Fields.DATA.toString()));
		pList.add(Property.forName("PCI." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		Distinct distinct = new Distinct(pList);
		criteria.setProjection(distinct);
		return executeCriteria(criteria);
	}

	/** Método para buscar cirurgias através do seq de um atendimento */
	public List<MbcCirurgias> pesquisarCirurgiaPorAtendimento(Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.ATENDIMENTO.toString(), "atendimento");
		criteria.add(Restrictions.eq("atendimento." + AghAtendimentos.Fields.SEQUENCE.toString(), seqAtendimento));
		return this.executeCriteria(criteria);
	}

	/** Método pár buscar cirurgias por ID de atendimento. */
	public List<MbcCirurgias> pesquisarCirurgiasPorAtendimento(Integer seqAtendimento, Date dataFimCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.ge(MbcCirurgias.Fields.DTHR_FIM_CIRG.toString(), dataFimCirurgia));
		if(isOracle()) {
		criteria.setLockMode(LockMode.UPGRADE);
		}
		return executeCriteria(criteria);
	}
	
	public String montaSQLEvolucao(final Date vDataValida, final String strSeqTipoEvolucao){
        final StringBuilder sqlEvolucao = new StringBuilder(200);
		
		if ( strSeqTipoEvolucao != null  && StringUtils.isNotEmpty(strSeqTipoEvolucao) && StringUtils.isNotBlank(strSeqTipoEvolucao)){
			sqlEvolucao.append(" SELECT COUNT(*) ")
		            .append("    FROM AGH.mam_item_evolucoes IEO,")
		            .append("         AGH.mam_evolucoes EVO")
		            .append("    WHERE evo.atd_seq = this_.atd_seq and")
		            .append("         evo.ind_pendente = 'V' and")
		            .append("         evo.dthr_valida_mvto is null");			
			//se existe evolução no dia
			if (isOracle()) {
				sqlEvolucao.append("  AND TRUNC(evo.dthr_valida) = to_date('" + DateUtil.obterDataFormatada(vDataValida, "dd/MM/yyyy") + "','dd/MM/yyyy')" );   
			} else {
				sqlEvolucao.append("  AND DATE_TRUNC('day', evo.dthr_valida) = to_date('" + DateUtil.obterDataFormatada(vDataValida, "dd/MM/yyyy") + "','dd/MM/yyyy')" );  
			}			
			sqlEvolucao.append(" and evo.seq = IEO.EVO_SEQ ");
			sqlEvolucao.append(" and IEO.TIE_SEQ IN(" + strSeqTipoEvolucao + ")");
		}
		else {
			sqlEvolucao.append(" 0 ");
		}
		
		return sqlEvolucao.toString();
		
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public List<CirurgiaVO> pesquisarCirurgias(final TelaListaCirurgiaVO tela, String orderProperty, final Date dataInicioFiltro,			
			final Integer crgSeq,  final List<Integer> crgsProceds, final Date vDataValida, final String strSeqTipoEvolucao, Integer diasNotaAdicional) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(VListaMbcCirurgias.class, "VMBC");		
		final StringBuilder filtrosPesquisa = new StringBuilder(200);
		
		if (crgSeq != null) {
			criteria.add(Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.SEQ.toString(), crgSeq));
		} else if (dataInicioFiltro != null) {			
				    criteria.add(Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), tela.getUnidade()));
					filtrosPesquisa.append(" | Unidade Funcional: ").append(tela.getUnidade().getSeq()).append(" - ").append(tela.getUnidade().getDescricao());
					filtrosPesquisa.append(" | Data entre: ").append(DateUtil.obterDataFormatada(dataInicioFiltro, "dd/MM/yyyy")).append(" e ")
					.append(DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy"));
					filtrosPesquisa.append(" | Pacientes em Sala de Recuperação");
					criteria.add(Restrictions.between("VMBC." + VListaMbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(dataInicioFiltro), DateUtil.truncaData(new Date())));
					criteria.add(Restrictions.isNotNull("VMBC." + VListaMbcCirurgias.Fields.DTHR_ENTRADA_SR.toString()));
					criteria.add(Restrictions.isNull("VMBC." + VListaMbcCirurgias.Fields.DTHR_SAIDA_SR.toString()));
		} else {
				filtrosPesquisa.append(" | Data: ").append(DateUtil.obterDataFormatada(tela.getDtProcedimento(), "dd/MM/yyyy"));
				
				if (tela.getEquipe() != null && tela.getEquipe().isEquipeDoUsuario()) {
					// Buscando todas as cirurgias com data menor a data da pesquisa vinculadas na equipe e que não tenha sido feita a descrição cirúrgica					
					Calendar inicio = Calendar.getInstance();
					inicio.add(Calendar.DAY_OF_MONTH, -diasNotaAdicional);
					
					Calendar fim = Calendar.getInstance();
					fim.add(Calendar.DAY_OF_MONTH, -1);
					fim.set(Calendar.HOUR, 23);
					fim.set(Calendar.HOUR_OF_DAY, 23);
					fim.set(Calendar.MINUTE, 59);
					fim.set(Calendar.SECOND, 59);
					fim.set(Calendar.MILLISECOND, 999);

					Date dataInicio = DateUtil.truncaData(inicio.getTime());
					Date dataFim = fim.getTime();
					
					LOG.debug("Data inicio: " + DateUtil.obterDataFormatada(dataInicio, "dd/MM/yyyy HH:mm:ss") );
					LOG.debug("Data fim : " + DateUtil.obterDataFormatada(dataFim, "dd/MM/yyyy HH:mm:ss") );
					
					criteria.add(Restrictions.or(
							Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.DATA.toString(), tela.getDtProcedimento()), 
							Restrictions.and(
									Restrictions.and(
											Restrictions.between("VMBC." + VListaMbcCirurgias.Fields.DATA.toString(), dataInicio, dataFim),
											Restrictions.ne("VMBC." + VListaMbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC)
									), Restrictions.or(
											Restrictions.gt("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESC_CIR_PENDENTE.toString(), 0),
											Restrictions.and(
													Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESCR_CIR.toString(), 0),
													Restrictions.or(
															Restrictions.gt("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESCR_PDT_PENDENTE.toString(), 0),
															 Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESCR_PDT_SIMPLES.toString(), 0)
															)
													)
											)
									)
							));
					if (dataInicio.before(tela.getDtProcedimento())){
					    criteria.add(Restrictions.ge("VMBC." + VListaMbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(dataInicio)));
					} else {
					    criteria.add(Restrictions.ge("VMBC." + VListaMbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(tela.getDtProcedimento())));
					}
				} else {
					criteria.add(Restrictions.sqlRestriction(" {alias}.DATA = to_date('" + DateUtil.obterDataFormatada(tela.getDtProcedimento(), "dd/MM/yyyy") + "','dd/MM/yyyy')" ));
				}
				
				criteria.add(Restrictions.sqlRestriction(" COALESCE({alias}." + VListaMbcCirurgias.Fields.MTC_SEQ.name() + ",0) NOT IN (" + tela.getDesmarcar() + ","
				+ tela.getDesmarcarAdm() + ")"));
				criteria.add(Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), tela.getUnidade()));
				filtrosPesquisa.append(" | Unidade Funcional: ").append(tela.getUnidade().getSeq()).append(" - ").append(tela.getUnidade().getDescricao());
				if (tela.getSala() != null) {
					criteria.add(Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.SALA_CIRURGICA.toString(), tela.getSala()));
					filtrosPesquisa.append(" | Sala").append(": ").append(tela.getSala().getId().getSeqp());
				}
				if (tela.getEspecialidade() != null) {
				   criteria.add(Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.ESP_SEQ.toString(), tela.getEspecialidade().getSeq()));
				   filtrosPesquisa.append(" | Especialidade: ").append(tela.getEspecialidade().getSeq());
				}
				
				if (tela.getEquipe() != null && !tela.getEquipe().isEquipeDoUsuario()) {
					filtrosPesquisa.append(" | Equipe: ").append(tela.getEquipe().getNome());
					final DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCG");
					subCriteria.setProjection(Projections.property("PCG." + MbcProfCirurgias.Fields.CRG_SEQ.toString()));
					subCriteria.add(Restrictions.eqProperty("PCG." + MbcProfCirurgias.Fields.CRG_SEQ.toString(), "VMBC." + VListaMbcCirurgias.Fields.SEQ.toString()));
					subCriteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
					subCriteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString(), tela.getEquipe().getMatricula()));
					subCriteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString(), tela.getEquipe().getVinCodigo()));
					criteria.add(Subqueries.exists(subCriteria));
				} else if (tela.getEquipe() != null && tela.getEquipe().isEquipeDoUsuario()) {
					
					final String sql = " {alias}.SEQ IN (SELECT DISTINCT PCG." + MbcProfCirurgias.Fields.CRG_SEQ.name() + "				 FROM "
					+ MbcProfCirurgias.class.getAnnotation(Table.class).schema() + "." + MbcProfCirurgias.class.getAnnotation(Table.class).name() + " PCG, " +
	
					AghEquipes.class.getAnnotation(Table.class).schema() + "." + AghEquipes.class.getAnnotation(Table.class).name() + " EQP, " +	
				
			    	MpmListaServEquipe.class.getAnnotation(Table.class).schema() + "." + MpmListaServEquipe.class.getAnnotation(Table.class).name() + " LSQ " +
					
					" WHERE LSQ." + MpmListaServEquipe.Fields.SER_MATRICULA.name() + " = ? " + "  AND  LSQ."
					+ MpmListaServEquipe.Fields.SER_VIN_CODIGO.name() + " = ? " + "  AND  EQP." + AghEquipes.Fields.SEQ.name() + " = LSQ."
					+ MpmListaServEquipe.Fields.EQP_SEQ.name() + "    AND  EQP." + AghEquipes.Fields.SER_MATRICULA.name() + " = PCG."
					+ MbcProfCirurgias.Fields.PUC_SER_MATRICULA.name() + "   AND  EQP." + AghEquipes.Fields.SER_VIN_CODIGO.name() + " = PCG."
					+ MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.name() + "  AND  PCG." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.name() + " = ? "
					+ " ) ";
							
					final Object[] values = { tela.getMatricula(), tela.getVinculo(), DominioSimNao.S.toString() };
					final Type[] types = { IntegerType.INSTANCE, ShortType.INSTANCE, StringType.INSTANCE };					
					criteria.add(Restrictions.sqlRestriction(sql, values, types));
			    }
				if (tela.getNatureza() != null) {
					criteria.add(Restrictions.eq("VMBC." + VListaMbcCirurgias.Fields.NATUREZA_AGEND.toString(), tela.getNatureza()));
					filtrosPesquisa.append(" | Natureza: ").append(tela.getNatureza().getDescricao());
				}
					
				if(crgsProceds != null && !crgsProceds.isEmpty()){
					criteria.add(Restrictions.in("VMBC." + VListaMbcCirurgias.Fields.SEQ.toString(), crgsProceds));
				}
		}			
				
		final StringBuilder sqlEvolucao = new StringBuilder(200);
		sqlEvolucao.append(this.montaSQLEvolucao(vDataValida,strSeqTipoEvolucao));		
		
		criteria.setProjection(Projections.projectionList().add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.SEQ.toString()), CirurgiaVO.Fields.CRG_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.DATA.toString()), CirurgiaVO.Fields.CRG_DATA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.SALA_NOME.toString()), CirurgiaVO.Fields.CRG_SALA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.PAC_CODIGO.toString()), CirurgiaVO.Fields.PAC_CODIGO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.PRONTUARIO.toString()), CirurgiaVO.Fields.PRONTUARIO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.NOME.toString()), CirurgiaVO.Fields.NOME_PACIENTE.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.ESP_SEQ.toString()), CirurgiaVO.Fields.ESP_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.ESP_SIGLA.toString()), CirurgiaVO.Fields.SIGLA_ESPECIALIDADE.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.ESP_NOME.toString()), CirurgiaVO.Fields.NOME_ESPECIALIDADE.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.NATUREZA_AGEND.toString()), CirurgiaVO.Fields.NATUREZA_AGENDA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.DTHR_FIM_CIRG.toString()), CirurgiaVO.Fields.DATA_FIM_CIRURGIA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()), CirurgiaVO.Fields.ORIGEM_PAC_CIRG.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.CSP_CNV_CODIGO.toString()), CirurgiaVO.Fields.CSP_CNV_CODIGO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.CSP_SEQ.toString()), CirurgiaVO.Fields.CSP_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), CirurgiaVO.Fields.DTHR_INICIO_CIRG.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.AGENDA_LADO_CIRG.toString()), CirurgiaVO.Fields.LADO_CIRURGIA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.SITUACAO.toString()), CirurgiaVO.Fields.SITUACAO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()), CirurgiaVO.Fields.ATD_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.QES_MTC_SEQ.toString()), CirurgiaVO.Fields.QES_MTC_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.QES_SEQP.toString()), CirurgiaVO.Fields.QES_SEQP.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.VVC_QES_MTC_SEQ.toString()), CirurgiaVO.Fields.VVC_QES_MTC_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.VVC_QES_SEQP.toString()), CirurgiaVO.Fields.VVC_QES_SEQP.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.VVC_SEQP.toString()), CirurgiaVO.Fields.VVC_SEQP.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.MTC_SEQ.toString()), CirurgiaVO.Fields.MTC_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.COMPLEMENTO_CANC.toString()), CirurgiaVO.Fields.COMPLEMENTO_CANC.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.CRIADO_EM.toString()), CirurgiaVO.Fields.CRG_CRIADO_EM.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString()), CirurgiaVO.Fields.IND_DIGT_NOTA_SALA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.SERVIDOR_MATRICULA.toString()), CirurgiaVO.Fields.SERVIDOR_MATRICULA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.SERVIDOR_VIN_CODIGO.toString()), CirurgiaVO.Fields.SERVIDOR_VIN_CODIGO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.PROC_CIR_DESCRICAO.toString()), CirurgiaVO.Fields.PROC_CIR_DESCRICAO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.NOME_EQUIPE.toString()), CirurgiaVO.Fields.NOME_EQUIPE.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.PROJ_PESQ_PAC.toString()), CirurgiaVO.Fields.PROJ_PESQ_PAC.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESC_CIR_PENDENTE.toString()), CirurgiaVO.Fields.TEMP_DESC_CIR_PENDENTE.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESCR_CIR.toString()), CirurgiaVO.Fields.TEMP_DESCR_CIR.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESCR_PDT_PENDENTE.toString()), CirurgiaVO.Fields.TEMP_DESCR_PDT_PENDENTE.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.TEMP_DESCR_PDT_SIMPLES.toString()), CirurgiaVO.Fields.TEMP_DESCR_PDT_SIMPLES.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.FICHA_SEQ.toString()), CirurgiaVO.Fields.FICHA_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.FICHA_PENDENTE.toString()), CirurgiaVO.Fields.FICHA_PENDENTE.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.TEM_CERTIF_DIGITAL.toString()), CirurgiaVO.Fields.TEM_CERTIF_DIGITAL.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.TEM_GERME_MULTI.toString()), CirurgiaVO.Fields.TEM_GERME_MULTI.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.EXIGE_CERIH.toString()), CirurgiaVO.Fields.EXIGE_CERIH.toString())		
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.LTO_LTO_ID.toString()), CirurgiaVO.Fields.LTO_LTO_ID.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.PAC_UNF_SEQ.toString()), CirurgiaVO.Fields.PAC_UNF_SEQ.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.DT_ULT_INTERNACAO.toString()), CirurgiaVO.Fields.DT_ULT_INTERNACAO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.DT_ULT_ALTA.toString()), CirurgiaVO.Fields.DT_ULT_ALTA.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.QRT_DESCRICAO.toString()), CirurgiaVO.Fields.QRT_DESCRICAO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.PAC_UNF_ANDAR.toString()), CirurgiaVO.Fields.PAC_UNF_ANDAR.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.PAC_UNF_ALA.toString()), CirurgiaVO.Fields.PAC_UNF_ALA.toString())
		.add(Projections.sqlProjection("("+	sqlEvolucao.toString() + ") as evolucao",	new String[] {"evolucao"}, new Type[] { IntegerType.INSTANCE}), CirurgiaVO.Fields.TEM_EVOLUCAO.toString())
		.add(Projections.property("VMBC." + VListaMbcCirurgias.Fields.TEM_PAC_INTERNACAO.toString()), CirurgiaVO.Fields.TEM_PAC_INTERNACAO.toString()));
		
		
		//.add(Projections.sqlProjection(sqlEvolucao.toString(), new String[] {"evolucao"},new Type[] {IntegerType.INSTANCE},CirurgiaVO.Fields.TEM_EVOLUCAO.toString())));
		
		if (orderProperty == null) {
			criteria.addOrder(Order.asc("VMBC." + VListaMbcCirurgias.Fields.DATA.toString()));
			criteria.addOrder(Order.asc("VMBC." + VListaMbcCirurgias.Fields.SCI_SEQP.toString()));
			criteria.addOrder(OrderBySql.sql(" COALESCE(" + VListaMbcCirurgias.Fields.DTHR_INICIO_CIRG.name() + ", " + VListaMbcCirurgias.Fields.DTHR_INICIO_ORDEM.name() + ") ASC "));
		} else {
			criteria.addOrder(Order.asc(orderProperty));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiaVO.class));
				
		if (filtrosPesquisa.length() > 0) {
			tela.setFiltrosPesquisa(filtrosPesquisa.toString());
		}
				
		return executeCriteria(criteria);
	}


	/*public List<CirurgiaVO> pesquisarCirurgias(final TelaListaCirurgiaVO tela, String orderProperty, final Date dataInicioFiltro, 
												final Integer crgSeq,  final List<Integer> crgsProceds) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "MBC");
		criteria.createAlias("MBC."+ MbcCirurgias.Fields.AGENDA.toString(), "agd", CriteriaSpecification.LEFT_JOIN);
		final StringBuilder filtrosPesquisa = new StringBuilder(200);
		if (crgSeq != null) {
			criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		} else if (dataInicioFiltro != null) {
			criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), tela.getUnidade()));
			filtrosPesquisa.append(" | Unidade Funcional: ").append(tela.getUnidade().getSeq()).append(" - ").append(tela.getUnidade().getDescricao());
			filtrosPesquisa.append(" | Data entre: ").append(DateUtil.obterDataFormatada(dataInicioFiltro, "dd/MM/yyyy")).append(" e ")
			.append(DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy"));
			filtrosPesquisa.append(" | Pacientes em Sala de Recuperação");
			criteria.add(Restrictions.between("MBC." + MbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(dataInicioFiltro), DateUtil.truncaData(new Date())));
			criteria.add(Restrictions.isNotNull("MBC." + MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString()));
			criteria.add(Restrictions.isNull("MBC." + MbcCirurgias.Fields.DTHR_SAIDA_SR.toString()));
		} else {
			filtrosPesquisa.append(" | Data: ").append(DateUtil.obterDataFormatada(tela.getDtProcedimento(), "dd/MM/yyyy"));
			criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.DATA.toString(), tela.getDtProcedimento()));
			criteria.add(Restrictions.sqlRestriction(" COALESCE({alias}." + MbcCirurgias.Fields.MTC_SEQ.name() + ",0) NOT IN (" + tela.getDesmarcar() + ","
					+ tela.getDesmarcarAdm() + ")"));
			criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), tela.getUnidade()));
			filtrosPesquisa.append(" | Unidade Funcional: ").append(tela.getUnidade().getSeq()).append(" - ").append(tela.getUnidade().getDescricao());
			if (tela.getSala() != null) {
				criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), tela.getSala()));
				filtrosPesquisa.append(" | Sala").append(": ").append(tela.getSala().getId().getSeqp());
			}
			if (tela.getEspecialidade() != null) {
				criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.ESP_SEQ.toString(), tela.getEspecialidade().getSeq()));
				filtrosPesquisa.append(" | Especialidade: ").append(tela.getEspecialidade().getSeq());
			}
			if (tela.getEquipe() != null && !tela.getEquipe().isEquipeDoUsuario()) {
				filtrosPesquisa.append(" | Equipe: ").append(tela.getEquipe().getNome());
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCG");
				subCriteria.setProjection(Projections.property("PCG." + MbcProfCirurgias.Fields.CRG_SEQ.toString()));
				subCriteria.add(Restrictions.eqProperty("PCG." + MbcProfCirurgias.Fields.CRG_SEQ.toString(), "MBC." + MbcCirurgias.Fields.SEQ.toString()));
				subCriteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
				subCriteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString(), tela.getEquipe().getMatricula()));
				subCriteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString(), tela.getEquipe().getVinCodigo()));
				criteria.add(Subqueries.exists(subCriteria));
			} else if (tela.getEquipe() != null && tela.getEquipe().isEquipeDoUsuario()) {
				filtrosPesquisa.append(" | Equipe: ").append(tela.getEquipe().getNome());
				final String sql = " {alias}.SEQ IN (SELECT DISTINCT PCG." + MbcProfCirurgias.Fields.CRG_SEQ.name() + "				 FROM "
				+ MbcProfCirurgias.class.getAnnotation(Table.class).schema() + "." + MbcProfCirurgias.class.getAnnotation(Table.class).name() + " PCG, " +

				AghEquipes.class.getAnnotation(Table.class).schema() + "." + AghEquipes.class.getAnnotation(Table.class).name() + " EQP, " +

				MpmListaServEquipe.class.getAnnotation(Table.class).schema() + "." + MpmListaServEquipe.class.getAnnotation(Table.class).name() + " LSQ " +

				"                  WHERE LSQ." + MpmListaServEquipe.Fields.SER_MATRICULA.name() + " = ? " + "            	   AND  LSQ."
				+ MpmListaServEquipe.Fields.SER_VIN_CODIGO.name() + " = ? " + "           	       AND  EQP." + AghEquipes.Fields.SEQ.name() + " = LSQ."
				+ MpmListaServEquipe.Fields.EQP_SEQ.name() + "           	       AND  EQP." + AghEquipes.Fields.SER_MATRICULA.name() + " = PCG."
				+ MbcProfCirurgias.Fields.PUC_SER_MATRICULA.name() + "           	       AND  EQP." + AghEquipes.Fields.SER_VIN_CODIGO.name() + " = PCG."
				+ MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.name() + "            	   AND  PCG." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.name() + " = ? "
				+ "             	) ";

				final Object[] values = { tela.getMatricula(), tela.getVinculo(), DominioSimNao.S.toString() };
				final Type[] types = { IntegerType.INSTANCE, ShortType.INSTANCE, StringType.INSTANCE };

				criteria.add(Restrictions.sqlRestriction(sql, values, types));
			}
			if (tela.getNatureza() != null) {
				criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.NATUREZA_AGEND.toString(), tela.getNatureza()));
				filtrosPesquisa.append(" | Natureza: ").append(tela.getNatureza().getDescricao());
			}
			
			if(crgsProceds != null){
				criteria.add(Restrictions.in("MBC." + MbcCirurgias.Fields.SEQ.toString(), crgsProceds));
			}
		}
		criteria.createAlias("MBC." + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "sala");
		criteria.createAlias("MBC." + MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("MBC." + MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.setProjection(Projections.projectionList().add(Projections.property("MBC." + MbcCirurgias.Fields.SEQ.toString()), CirurgiaVO.Fields.CRG_SEQ.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.DATA.toString()), CirurgiaVO.Fields.CRG_DATA.toString())
				.add(Projections.property("sala." + MbcSalaCirurgica.Fields.NOME.toString()), CirurgiaVO.Fields.CRG_SALA.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.CODIGO.toString()), CirurgiaVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()), CirurgiaVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), CirurgiaVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()), CirurgiaVO.Fields.ESP_SEQ.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString()), CirurgiaVO.Fields.SIGLA_ESPECIALIDADE.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.NOME.toString()), CirurgiaVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.NATUREZA_AGEND.toString()), CirurgiaVO.Fields.NATUREZA_AGENDA.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()), CirurgiaVO.Fields.DATA_FIM_CIRURGIA.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()), CirurgiaVO.Fields.ORIGEM_PAC_CIRG.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.CSP_CNV_CODIGO.toString()), CirurgiaVO.Fields.CSP_CNV_CODIGO.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.CSP_SEQ.toString()), CirurgiaVO.Fields.CSP_SEQ.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), CirurgiaVO.Fields.DTHR_INICIO_CIRG.toString())
				.add(Projections.property("agd." + MbcAgendas.Fields.LADO_CIRURGIA.toString()), CirurgiaVO.Fields.LADO_CIRURGIA.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.SITUACAO.toString()), CirurgiaVO.Fields.SITUACAO.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()), CirurgiaVO.Fields.ATD_SEQ.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.QES_MTC_SEQ.toString()), CirurgiaVO.Fields.QES_MTC_SEQ.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.QES_SEQP.toString()), CirurgiaVO.Fields.QES_SEQP.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.VVC_QES_MTC_SEQ.toString()), CirurgiaVO.Fields.VVC_QES_MTC_SEQ.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.VVC_QES_SEQP.toString()), CirurgiaVO.Fields.VVC_QES_SEQP.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.VVC_SEQP.toString()), CirurgiaVO.Fields.VVC_SEQP.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.MTC_SEQ.toString()), CirurgiaVO.Fields.MTC_SEQ.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.COMPLEMENTO_CANC.toString()), CirurgiaVO.Fields.COMPLEMENTO_CANC.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.CRIADO_EM.toString()), CirurgiaVO.Fields.CRG_CRIADO_EM.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString()), CirurgiaVO.Fields.IND_DIGT_NOTA_SALA.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.SERVIDOR_MATRICULA.toString()), CirurgiaVO.Fields.SERVIDOR_MATRICULA.toString())
				.add(Projections.property("MBC." + MbcCirurgias.Fields.SERVIDOR_VIN_CODIGO.toString()), CirurgiaVO.Fields.SERVIDOR_VIN_CODIGO.toString()));
		if (orderProperty == null) {
			criteria.addOrder(Order.asc("MBC." + MbcCirurgias.Fields.DATA.toString()));
			criteria.addOrder(Order.asc("MBC." + MbcCirurgias.Fields.SCI_SEQP.toString()));
			criteria.addOrder(OrderBySql.sql(" COALESCE(" + MbcCirurgias.Fields.DTHR_INICIO_CIRG.name() + ", " + MbcCirurgias.Fields.DTHR_INICIO_ORDEM.name() + ") ASC "));
		} else {
			criteria.addOrder(Order.asc(orderProperty));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiaVO.class));

		if (filtrosPesquisa.length() > 0) {
		tela.setFiltrosPesquisa(filtrosPesquisa.toString());
		}

		return executeCriteria(criteria);
	}*/

	/** verifica se o paciente tem cirurgia sem nota digitada */
	public Boolean pacienteTemCirurgiaSemNota(Integer pacCodigo, Date dataInternacao, Date dataAlta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.FALSE));
		if (dataAlta == null) {
			dataAlta = new Date();
		}
		criteria.add(Restrictions.between(MbcCirurgias.Fields.DTHR_FIM_CIRG.toString(), dataInternacao, dataAlta));
		return executeCriteriaCount(criteria) > 0;
	}

	@SuppressWarnings("unchecked")
	public List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> listarCirurgiasAgendadasCadastroSugestaoDesdobramento(Date dthrInicioCirurgiaIni,
			Date dthrInicioCirurgiaFim, ConstanteAghCaractUnidFuncionais[] caracteristicas, DominioGrupoConvenio grupoConvenio, DominioTipoPlano indTipoPlano,
			DominioSituacaoCirurgia situacaoCirurgia) {
		StringBuffer hql = new StringBuffer(410);

		hql.append(" select distinct new br.gov.mec.aghu.faturamento.vo.CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO (");
		hql.append(" 	crg.").append(MbcCirurgias.Fields.PAC_CODIGO.toString());
		hql.append("	, crg.").append(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString());
		hql.append("	, crg.").append(MbcCirurgias.Fields.CSP_CNV_CODIGO.toString());
		hql.append("	, crg.").append(MbcCirurgias.Fields.CSP_SEQ.toString());
		hql.append("	, crg.").append(MbcCirurgias.Fields.UNF_SEQ.toString());
		hql.append(" ) ");
		hql.append(" from ").append(MbcCirurgias.class.getSimpleName()).append(" crg ");
		hql.append(" 	join crg.").append(MbcCirurgias.Fields.CONVENIO_SAUDE.toString()).append(" fcs ");
		hql.append(" 	join crg.").append(MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString()).append(" csp ");
		hql.append(" 	join crg.").append(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString()).append(" unf ");
		hql.append(" 	join unf.").append(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString()).append(" cuf ");
		hql.append(" where cuf.").append(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).append(" in (:caracteristicas) ");
		hql.append(" 	and crg.").append(MbcCirurgias.Fields.SITUACAO.toString()).append(" = :situacaoCirurgia ");
		hql.append(" 	and crg.").append(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()).append(" between :dthrInicioCirurgiaIni and :dthrInicioCirurgiaFim ");
		hql.append(" 	and fcs.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio ");
		hql.append(" 	and csp.").append(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString()).append(" = :indTipoPlano ");
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrInicioCirurgiaIni", dthrInicioCirurgiaIni);
		query.setParameter("dthrInicioCirurgiaFim", dthrInicioCirurgiaFim);
		query.setParameter("grupoConvenio", grupoConvenio);
		query.setParameter("indTipoPlano", indTipoPlano);
		query.setParameter("situacaoCirurgia", situacaoCirurgia);
		query.setParameterList("caracteristicas", caracteristicas);
		return query.list();
	}

	public List<MbcCirurgias> listarCirurgiasPorHistoricoPacienteVO(HistoricoPacienteVO historicoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.setFetchMode(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PACIENTE.toString(), historicoVO.getAipPaciente()));
		criteria.add(Restrictions.le(MbcCirurgias.Fields.DATA.toString(), new Date()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.DATA.toString()));
		return executeCriteria(criteria);
	}

	public Long obterNumeroCirurgiasAgendadasPorPacienteDia(Integer codigoPaciente, Date dataCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		criteria.add(Restrictions.eq("PAC.".concat(AipPacientes.Fields.CODIGO.toString()), codigoPaciente));
		return executeCriteriaCount(criteria);
	}

	/** Cria a pesquisa principal. */
	public List<Object[]> pesquisaInformacoesEscalaCirurgias(Short unidade, Date dataCirurgia, Date dataIni, Date dataSup) {
		DetachedCriteria criteria = obterCriteriaPrincipalRelatorios(unidade, dataCirurgia);

		DetachedCriteria cp = DetachedCriteria.forClass(AinInternacao.class, "int");
		criteria.add(Restrictions.ge(MbcCirurgias.Fields.PRONTUARIO.toString(), 0));
		criteria.add(Restrictions.le(MbcCirurgias.Fields.PRONTUARIO.toString(), VALOR_MAXIMO_PRONTUARIO));

		if (dataIni != null) {
			criteria.add(Restrictions.ge(MbcCirurgias.Fields.CRIADO_EM.toString(), dataIni));
		}
		criteria.add(Restrictions.le(MbcCirurgias.Fields.CRIADO_EM.toString(), dataSup));
		// AND NOT EXISTS (select seq from ain_internacoes where pac_Codigo =
		// crg.pac_Codigo and tam_codigo is null)
		criteria.add(Subqueries.notExists(cp.setProjection(Property.forName("int." + AinInternacao.Fields.SEQ.toString()))
				.add(Restrictions.eqProperty("int." + AinInternacao.Fields.PAC_CODIGO.toString(), "CIR." + MbcCirurgias.Fields.PAC_CODIGO.toString()))
				.add(Restrictions.isNull("int." + AinInternacao.Fields.TAM_CODIGO.toString()))));

		obterProjectionRelatorios(criteria);

		return executeCriteria(criteria);
	}

	private void obterProjectionRelatorios(DetachedCriteria criteria) {
		criteria.setProjection(Projections.projectionList().add(Projections.property(MbcCirurgias.Fields.SCI_SEQP.toString())) // 0 - Short
				.add(Projections.property(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString())) // 1 - Date
				.add(Projections.property(MbcCirurgias.Fields.CSP_CNV_CODIGO.toString())) // 2 - Short
				.add(Projections.property(MbcCirurgias.Fields.NOME.toString())) // 3 - String
				.add(Projections.property(MbcCirurgias.Fields.DATA_NASCIMENTO.toString())) // 4 - Date
				.add(Projections.property(MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString())) // 5 - String
				.add(Projections.property(MbcCirurgias.Fields.PRONTUARIO.toString())) // 6 - Integer
				.add(Projections.property(MbcCirurgias.Fields.PRNT_ATIVO.toString())) // 7 - DominioTipoProntuario
				.add(Projections.property(MbcCirurgias.Fields.IND_PRECAUCAO_ESPECIAL.toString())) // 8 - String
				.add(Projections.property(MbcCirurgias.Fields.SEQ.toString())) // 9 - Integer
				.add(Projections.property(MbcCirurgias.Fields.PAC_CODIGO.toString())) // 10 - Integer
				.add(Projections.property(MbcCirurgias.Fields.VOLUMES.toString())) // 11 - Short
				.add(Projections.property(MbcCirurgias.Fields.CRIADO_EM.toString())));// 12 - Date
	}

	private DetachedCriteria obterCriteriaPrincipalRelatorios(Short unidade, Date dataCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CIR");
		addJoinPacienteEUnidadeFuncional(criteria);
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNF_SEQ.toString(), unidade));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		return criteria;
	}

	/**
	 * #22484 PESQUISA RELATÓRIO ESCALA SIMPLES
	 * @param unidade, dataCirurgia
	 * @return
	 */
	public List<MbcCirurgias> pesquisarEscalaSimples(Short unidade, Date dataCirurgia) {
		DetachedCriteria criteria = obterCriteriaPrincipalRelatorios(unidade, dataCirurgia);
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.SCI_SEQP.toString()));
		return executeCriteria(criteria);
	}

	public List<MbcCirurgias> pesquisarEscalaCirurgica(Short unfSeq, Date dataCirurgia, MbcSalaCirurgica sala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);

		criteria.createAlias(MbcCirurgias.Fields.AGENDA.toString(), "AGD", JoinType.INNER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.INNER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "CNV", JoinType.INNER_JOIN);
		
		if(sala != null){
			criteria.createAlias(MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "SLC", JoinType.INNER_JOIN);	
		}
		
		
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC)); // Diferente de cancelamento
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq)); // Unidade
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dataCirurgia)); // Data da cirurgia

		if(sala != null){
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.SALA_CIRURGICA.toString(), sala)); // Data da cirurgia
		}

		criteria.addOrder(Order.asc(MbcCirurgias.Fields.SCI_SEQP.toString()));
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.DTHR_INICIO_ORDEM.toString()));
		
		return executeCriteria(criteria);

	}

	/**
	 * #22483 PESQUISA RELATÓRIO ESCALA CIRURGICA
	 * @param unfSeq, dataCirurgia
	 * @return
	 */
	public List<MbcCirurgias> pesquisarEscalaCirurgica(Short unfSeq, Date dataCirurgia) {
		return this.pesquisarEscalaCirurgica(unfSeq, dataCirurgia, null);
	}

	public List<MbcCirurgias> listarCirurgiasPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.AGENDA.toString(), "AGE");
		criteria.createAlias(MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "CONV_CIG");
		criteria.createAlias("AGE.".concat(MbcAgendas.Fields.ESPECIALIDADE.toString()), "ESP");
		criteria.createAlias("AGE.".concat(MbcAgendas.Fields.CONVENIO_SAUDE_PLANO.toString()), "CONV_AGE");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		return executeCriteria(criteria);
	}

	public MbcCirurgias obterProcedimentoCirurgicoInternacaoUltimaSemana(Integer pacCodigo, Date dtInternacao, DominioSituacaoCirurgia situacaoCirurgia) {
		MbcCirurgias cirurgia = null;
		Calendar calendarData = Calendar.getInstance();
		calendarData.setTime(dtInternacao);
		calendarData.add(Calendar.DAY_OF_MONTH, -7);
		Date dataMenosSete = calendarData.getTime();

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), situacaoCirurgia));
		
		//Desenvolvido trecho abaixo, pois quando estava vindo do alerta da lista de cirurgias não mostrava a modal, pois os segundos não ficavam corretos.
		//Para resolver, foi truncada a data.
//		criteria.add(Restrictions.le(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString(), dtInternacao));
//		criteria.add(Restrictions.ge(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString(), dataMenosSete));
		StringBuffer sql = new StringBuffer(140);
		if (isOracle()) {
			sql.append(" trunc(DTHR_INICIO_CIRG) between to_date('" + DateUtil.obterDataFormatada(dataMenosSete, "dd/MM/yyyy") + "','dd/MM/yyyy') and to_date('" + DateUtil.obterDataFormatada(dtInternacao, "dd/MM/yyyy") + "','dd/MM/yyyy') ");
		} else {
			sql.append(" date_trunc('day', DTHR_INICIO_CIRG::timestamp) between to_date('" + DateUtil.obterDataFormatada(dataMenosSete, "dd/MM/yyyy") + "','dd/MM/yyyy') and to_date('" + DateUtil.obterDataFormatada(dtInternacao, "dd/MM/yyyy") + "','dd/MM/yyyy') ");
		}
		criteria.add(Restrictions.sqlRestriction(sql.toString()));
		/*
		 * criteria.add(Restrictions.isNull(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString())); Melhoria #44845, Ajuste #
		 */
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));

		List<MbcCirurgias> listaCirurgias = this.executeCriteria(criteria);
		if (!listaCirurgias.isEmpty()) {
			cirurgia = listaCirurgias.get(0);
		}

		return cirurgia;
	}

	public List<MbcCirurgias> pesquisarCirurgiasPorPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = getCriteriaPesquisarCirurgiasPorPaciente(paciente);
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.DATA.toString()));

		List<MbcCirurgias> listaCirurgiasVolta = executeCriteria(criteria);

		return listaCirurgiasVolta;
	}
	
	public Long pesquisarCirurgiasPorPacienteCount(AipPacientes paciente) {
		DetachedCriteria criteria = getCriteriaPesquisarCirurgiasPorPaciente(paciente);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarCirurgiasPorPaciente(
			AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MbcCirurgias.class)
		.add(Restrictions.eq(MbcCirurgias.Fields.PACIENTE.toString(), paciente))
		.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(),
				DominioSituacaoCirurgia.CANC));
		return criteria;
	}

	public List<MbcCirurgias> listarCirurgias(AipPacientes paciente, AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.ATENDIMENTO.toString(), atendimento));
		return executeCriteria(criteria);
	}

	/*
	 * CURSOR c_cirurgia (c_pac_codigo mbc_cirurgias.pac_codigo%type, c_dt_realiz fat_proced_amb_realizados.dthr_realizado%type, c_dias NUMBER) IS
	 */
	public List<Date> buscarDataCirurgias(final Integer codPaciente, final Date dtRealizado, final Date dtRealizadoFimMes, final DominioSituacaoCirurgia situacaoCirurgia,
			final Boolean indDigtNotaSala, final Short cnvCodigo, final DominioIndRespProc dominioIndRespProc, final DominioSituacao situacao, final Integer phiSeq) {

		return executeCriteria(obterCriteriaBuscarDataCirurgias(codPaciente, dtRealizado, dtRealizadoFimMes, situacaoCirurgia, indDigtNotaSala, cnvCodigo, dominioIndRespProc,
				situacao, phiSeq));
	}

	private DetachedCriteria obterCriteriaBuscarDataCirurgias(final Integer codPaciente, final Date dtRealizado, final Date dtRealizadoFimMes,
			final DominioSituacaoCirurgia situacaoCirurgia, final Boolean indDigtNotaSala, final Short cnvCodigo, final DominioIndRespProc dominioIndRespProc,
			final DominioSituacao situacao, final Integer phiSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		criteria.createAlias(MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "ppc");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		criteria.createAlias("pci." + MbcProcedimentoCirurgicos.Fields.PROCEDIMENTOS_HOSPITALARES_INTERNOS.toString(), "phi");
		criteria.setProjection(Projections.property("crg." + MbcCirurgias.Fields.DATA.toString()));
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.PAC_CODIGO.toString(), codPaciente));
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.SITUACAO.toString(), situacaoCirurgia));
		criteria.add(Restrictions.le("crg." + MbcCirurgias.Fields.DATA.toString(), dtRealizado));
		criteria.add(Restrictions.ge("crg." + MbcCirurgias.Fields.DATA.toString(), dtRealizadoFimMes));
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), indDigtNotaSala));
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), dominioIndRespProc));
		criteria.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq("phi." + FatProcedHospInternos.Fields.SEQ.toString(), phiSeq));
		return criteria;
	}

	public List<Object[]> listarAtendimentosPacienteCirurgiaInternacao(
			Integer pacCodigo, DominioSituacaoCirurgia situacaoCirurgia, DominioOrigemPacienteCirurgia origemPacienteCirurgia, Boolean situacaoCirurgiaDiferente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		getCriteriaListarAtendimentosPacienteCirurgiaInternacao(pacCodigo, situacaoCirurgia, origemPacienteCirurgia, situacaoCirurgiaDiferente, criteria);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MbcCirurgias.Fields.DATA.toString()));
		p.add(Projections.property(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString()));

		criteria.setProjection(p);

		return executeCriteria(criteria);
	}

	private void getCriteriaListarAtendimentosPacienteCirurgiaInternacao(
			Integer pacCodigo, DominioSituacaoCirurgia situacaoCirurgia, DominioOrigemPacienteCirurgia origemPacienteCirurgia, 
			Boolean situacaoCirurgiaDiferente, DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()));
		
		if(situacaoCirurgia != null){
			if(situacaoCirurgiaDiferente){
				criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), situacaoCirurgia));
			}else{
				criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), situacaoCirurgia));
			}
		}
		
		if(origemPacienteCirurgia != null){
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString(), origemPacienteCirurgia));
		}
	}

	public List<Object[]> listarAtendimentosPacienteCirurgiaAmbulatorioPorCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString(), DominioOrigemPacienteCirurgia.A));
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()));
		p.add(Projections.property(MbcCirurgias.Fields.DATA.toString()));
		p.add(Projections.property(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString()));

		criteria.setProjection(p);

		return executeCriteria(criteria);
	}

	/**
	 * Migração dos 3 primeiros selects da VIEW V_AIP_POL_CIRURG
	 * 
	 * @param codigo
	 * @return
	 */
	public List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOL(Integer codigo) {
		List<CirurgiasInternacaoPOLVO> resultados = pesquisarCirurgiasInternacaoPOLParte1(codigo);
		resultados.addAll(pesquisarCirurgiasInternacaoPOLParte2(codigo));
		resultados.addAll(pesquisarCirurgiasInternacaoPOLParte3(codigo));

		return resultados;
	}
	
	private List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOLParte1(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		criteria.createAlias(MbcCirurgias.Fields.DESCRICOES_CIRURGIAS.toString(), "dcg");
		criteria.createAlias("dcg." + MbcDescricaoCirurgica.Fields.MBC_PROC_DESCRICOES.toString(), "pod");
		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias(MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "prof");
		criteria.createAlias("prof." + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), "rap");
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.createAlias("pod." + MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci");

		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(MbcCirurgias.Fields.SEQ.toString()), CirurgiasInternacaoPOLVO.Fields.SEQ.toString())
		.add(Projections.property(MbcCirurgias.Fields.TEM_DESCRICAO.toString()), CirurgiasInternacaoPOLVO.Fields.TEM_DESCRICAO.toString())
		.add(Projections.property(MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), CirurgiasInternacaoPOLVO.Fields.DIGITA_NOTA_SALA.toString())
		.add(Projections.property(MbcCirurgias.Fields.SITUACAO.toString()), CirurgiasInternacaoPOLVO.Fields.SITUACAO.toString())
		.add(Projections.property(MbcCirurgias.Fields.DATA.toString()), CirurgiasInternacaoPOLVO.Fields.DATA.toString())
		.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasInternacaoPOLVO.Fields.DESCRICAO.toString())
		.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), CirurgiasInternacaoPOLVO.Fields.ESPECIALIDADE.toString())
		.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()), CirurgiasInternacaoPOLVO.Fields.EQUIPE.toString())
		.add(Projections.property(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()), CirurgiasInternacaoPOLVO.Fields.ATD_SEQ.toString());
		
		criteria.setProjection(projection);

		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.IND_TEM_DESCRICAO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("dcg." + MbcDescricaoCirurgica.Fields.SITUACAO.toString(), DominioSituacaoDescricaoCirurgia.CON));
		criteria.add(Restrictions.eq("prof." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.COD_PACIENTE.toString(), codigo)); //Restrição adicionada originalmente no select do forms
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), DominioTipoProcedimentoCirurgico.CIRURGIA)); //Restrição adicionada originalmente no select do forms

		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasInternacaoPOLVO.class));

		return executeCriteria(criteria);
	}

	private List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOLParte2(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		
		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias(MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "prof");
		criteria.createAlias("prof." + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), "rap");
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.createAlias(MbcCirurgias.Fields.PDT_DESCRICAO.toString(), "ddt");
		criteria.createAlias("ddt." + PdtDescricao.Fields.PDT_PROCS.toString(), "dpc");
		criteria.createAlias("dpc." + PdtProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "dpt");
		criteria.createAlias("dpt." + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO, "pci");
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(MbcCirurgias.Fields.SEQ.toString()), CirurgiasInternacaoPOLVO.Fields.SEQ.toString())
		.add(Projections.property(MbcCirurgias.Fields.TEM_DESCRICAO.toString()), CirurgiasInternacaoPOLVO.Fields.TEM_DESCRICAO.toString())
		.add(Projections.property(MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), CirurgiasInternacaoPOLVO.Fields.DIGITA_NOTA_SALA.toString())
		.add(Projections.property(MbcCirurgias.Fields.SITUACAO.toString()), CirurgiasInternacaoPOLVO.Fields.SITUACAO.toString())
		.add(Projections.property(MbcCirurgias.Fields.DATA.toString()), CirurgiasInternacaoPOLVO.Fields.DATA.toString())
		.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasInternacaoPOLVO.Fields.DESCRICAO.toString())
		.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), CirurgiasInternacaoPOLVO.Fields.ESPECIALIDADE.toString())
		.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()), CirurgiasInternacaoPOLVO.Fields.EQUIPE.toString())
		.add(Projections.property(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()), CirurgiasInternacaoPOLVO.Fields.ATD_SEQ.toString());
		
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.IND_TEM_DESCRICAO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("prof." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.in("ddt." + PdtDescricao.Fields.SITUACAO.toString(), new DominioSituacaoDescricao[]{DominioSituacaoDescricao.DEF, DominioSituacaoDescricao.PRE}));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.COD_PACIENTE.toString(), codigo)); //Restrição adicionada originalmente no select do forms
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), DominioTipoProcedimentoCirurgico.CIRURGIA)); //Restrição adicionada originalmente no select do forms
		
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasInternacaoPOLVO.class));
		
		return executeCriteria(criteria);
	}
	
	private List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOLParte3(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		
		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias(MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "prof");
		criteria.createAlias("prof." + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), "rap");
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.createAlias(MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "ppc");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO, "pci");
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(MbcCirurgias.Fields.SEQ.toString()), CirurgiasInternacaoPOLVO.Fields.SEQ.toString())
		.add(Projections.property(MbcCirurgias.Fields.TEM_DESCRICAO.toString()), CirurgiasInternacaoPOLVO.Fields.TEM_DESCRICAO.toString())
		.add(Projections.property(MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), CirurgiasInternacaoPOLVO.Fields.DIGITA_NOTA_SALA.toString())
		.add(Projections.property(MbcCirurgias.Fields.SITUACAO.toString()), CirurgiasInternacaoPOLVO.Fields.SITUACAO.toString())
		.add(Projections.property(MbcCirurgias.Fields.DATA.toString()), CirurgiasInternacaoPOLVO.Fields.DATA.toString())
		.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasInternacaoPOLVO.Fields.DESCRICAO.toString())
		.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), CirurgiasInternacaoPOLVO.Fields.ESPECIALIDADE.toString())
		.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()), CirurgiasInternacaoPOLVO.Fields.EQUIPE.toString())
		.add(Projections.property(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()), CirurgiasInternacaoPOLVO.Fields.ATD_SEQ.toString());
		
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.or(
				Restrictions.eq("crg." + MbcCirurgias.Fields.IND_TEM_DESCRICAO.toString(), Boolean.FALSE),
				Restrictions.isNull("crg." + MbcCirurgias.Fields.IND_TEM_DESCRICAO.toString())
				)); //equivalente ao nvl(crg.ind_tem_descricao,'N') = 'N'
		criteria.add(Restrictions.or(
				Restrictions.or(
						Restrictions.and(Restrictions.eq("crg." + MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString(), Boolean.TRUE), Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA)),
						Restrictions.and(Restrictions.eq("crg." + MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString(), Boolean.FALSE), Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND))),
						Restrictions.and(Restrictions.isNull("crg." + MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND))
				)); //equivalente ao DECODE (crg.ind_digit_nota_sala, 'S', 'NOTA', 'AGND')
		criteria.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("prof." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.COD_PACIENTE.toString(), codigo)); //Restrição adicionada originalmente no select do forms
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), DominioTipoProcedimentoCirurgico.CIRURGIA)); //Restrição adicionada originalmente no select do forms
		
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasInternacaoPOLVO.class));
		
		return executeCriteria(criteria);
	}

	/** verifica se escala do portal de agendamento tem cirurgia */
	public Long verificarSeEscalaPortalAgendamentoTemCirurgia(Integer agdSeq, Date dtAgenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dtAgenda));
		criteria.add(Restrictions.isNull(MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString()));

		return executeCriteriaCount(criteria);
	}

	/**
	 * se paciente tem alguma cirurgia x dias para trás ou x dias para frente P_PAC_EM_ATENDIMENTO CURSOR c_cirurgia_amb
	 */
	public List<MbcCirurgias> verificarSePacienteTemCirurgia(Integer pacCodigo, Integer numDiasPassado, Integer numDiasFuturo) {
		if (numDiasFuturo == null && numDiasPassado == null) {
			numDiasFuturo = 0;
			numDiasPassado = 0;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.ge(MbcCirurgias.Fields.DATA.toString(), DateUtil.adicionaDias(DateUtil.obterDataComHoraInical(new Date()), (-1) * numDiasPassado)));
		criteria.add(Restrictions.le(MbcCirurgias.Fields.DATA.toString(), DateUtil.adicionaDias(DateUtil.obterDataComHoraFinal(new Date()), numDiasFuturo)));

		return executeCriteria(criteria);
	}

	public MbcCirurgias obterCirurgiaPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.ATENDIMENTO.toString(), "atendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "paciente");
		criteria.createAlias(MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "salaCirurgica");
		criteria.createAlias("salaCirurgica." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");
		criteria.createAlias(MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "convenioSaudePlano");
		criteria.createAlias("convenioSaudePlano." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "convenioSaude");
		criteria.createAlias(MbcCirurgias.Fields.CENTRO_CUSTO.toString(), "cc", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), seq));

		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}

	public MbcCirurgias obterCirurgiaProjetpPesquisa(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PROJETO_PESQUISAS.toString(), "projetoPesquisa");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), seq));
		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}

	/** Pesquisa as cirurgias de um pacientes agendadas para o dia atual ou dia seguinte */
	public List<MbcCirurgias> pesquisarCirurgiasPacienteDataAtualEDiaSeguinte(AipPacientes paciente) {
		Calendar calendarDataAtual = Calendar.getInstance();
		calendarDataAtual.set(Calendar.HOUR_OF_DAY, 0);
		calendarDataAtual.set(Calendar.MINUTE, 0);
		calendarDataAtual.set(Calendar.SECOND, 0);
		calendarDataAtual.set(Calendar.MILLISECOND, 0);

		Calendar calendarDiaSeguinte = Calendar.getInstance();
		calendarDiaSeguinte.add(Calendar.DAY_OF_MONTH, 1);
		calendarDiaSeguinte.set(Calendar.HOUR_OF_DAY, 23);
		calendarDiaSeguinte.set(Calendar.MINUTE, 59);
		calendarDiaSeguinte.set(Calendar.SECOND, 59);

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.ge(MbcCirurgias.Fields.DATA.toString(), calendarDataAtual.getTime()));
		criteria.add(Restrictions.le(MbcCirurgias.Fields.DATA.toString(), calendarDiaSeguinte.getTime()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), false));

		List<MbcCirurgias> listaCirurgias = executeCriteria(criteria);
		return listaCirurgias;
	}
	
	/** Pesquisa as cirurgias de um pacientes agendadas entre dias */
	public List<MbcCirurgias> pesquisarCirurgiasPacienteDataEntreDias(AipPacientes paciente) {
	
		Calendar calendarDia2 = Calendar.getInstance();
		calendarDia2.add(Calendar.DAY_OF_MONTH, 1);
		calendarDia2.set(Calendar.HOUR_OF_DAY, 23);
		calendarDia2.set(Calendar.MINUTE, 59);
		calendarDia2.set(Calendar.SECOND, 59);
		
		Calendar calendarDia4 = Calendar.getInstance();
		calendarDia4.add(Calendar.DAY_OF_MONTH, 3);
		calendarDia4.set(Calendar.HOUR_OF_DAY, 23);
		calendarDia4.set(Calendar.MINUTE, 59);
		calendarDia4.set(Calendar.SECOND, 59);

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.ge(MbcCirurgias.Fields.DATA.toString(), calendarDia2.getTime()));
		criteria.add(Restrictions.le(MbcCirurgias.Fields.DATA.toString(), calendarDia4.getTime()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), false));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));

		List<MbcCirurgias> listaCirurgias = executeCriteria(criteria);
		return listaCirurgias;
	}

	/** verifica se agendamento tem cirurgia realizada */
	public MbcCirurgias verificarSeAgendamentoTemCirurgiaRealizada(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		Object cir = executeCriteriaUniqueResult(criteria);
		if (cir != null) {
			return (MbcCirurgias) cir;
		}
		return null;
	}

	public List<MbcCirurgias> buscarCirurgiaPorAgendamentoSemMotivoCancelamento(Integer agdSeq, Date dtAgenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.isNull(MbcCirurgias.Fields.MTC_SEQ.toString()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dtAgenda));
		return executeCriteria(criteria);
	}

	public List<CirurgiasCanceladasVO> pesquisarCirurgiasCanceladas(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		DetachedCriteria criteria = view(portalPesquisaCirurgiasParametrosVO);

		if (portalPesquisaCirurgiasParametrosVO.getUnfSeq() != null && portalPesquisaCirurgiasParametrosVO.getUnfSeq() != 0) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNF_SEQ.toString(), portalPesquisaCirurgiasParametrosVO.getUnfSeq()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getEspSeq() != null && portalPesquisaCirurgiasParametrosVO.getEspSeq() != 0) {
			criteria.add(Restrictions.eq("especialidade." + AghEspecialidades.Fields.SEQ.toString(), portalPesquisaCirurgiasParametrosVO.getEspSeq()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucSerMatricula() != null && portalPesquisaCirurgiasParametrosVO.getPucSerMatricula() != 0) {
			criteria.add(Restrictions.eq("servidor." + RapServidores.Fields.MATRICULA.toString(), portalPesquisaCirurgiasParametrosVO.getPucSerMatricula()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo() != null && portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo() != 0) {
			criteria.add(Restrictions.eq("servidor." + RapServidores.Fields.CODIGO_VINCULO.toString(), portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo()));
		}

		criteria.add(Restrictions.sqlRestriction(" COALESCE("+MbcCirurgias.Fields.MTC_SEQ.name() + ",0) NOT IN (" + portalPesquisaCirurgiasParametrosVO.getDesmarcar() + ","
				+ portalPesquisaCirurgiasParametrosVO.getDesmarcarAdm() + ")"));
		
		restricoes(portalPesquisaCirurgiasParametrosVO, criteria);

		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatConvenioSaude.class, "niv");
		ProjectionList subProjection = Projections.projectionList().add(Projections.property(FatConvenioSaude.Fields.CODIGO.toString()));
		subCriteria.setProjection(subProjection);
		subCriteria.add(Restrictions.eqProperty("niv." + FatConvenioSaude.Fields.CODIGO.toString(), "cirurgia." + MbcCirurgias.Fields.CSP_CNV_CODIGO.toString()));

		if (portalPesquisaCirurgiasParametrosVO.getConvenio() != null) {
			Boolean equalsT = "T".equals(portalPesquisaCirurgiasParametrosVO.getConvenio().name());
			if (!equalsT) {// Se for T, não precisa verificar a outra parte
				if (DominioConvenio.N.name().equals(portalPesquisaCirurgiasParametrosVO.getConvenio().name())) {// Se selecionado "NÃO SUS"

					subCriteria.add(Restrictions.or(Restrictions.eq("niv." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.C),
							Restrictions.eq("niv." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.P)));
				} else {
					if (DominioConvenio.S.name().equals(portalPesquisaCirurgiasParametrosVO.getConvenio().name())) {// Se selecionado "SUS"
						subCriteria.add(Restrictions.eq("niv." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
					}
				}
			}
			/**
			 * EXISTS (select 1 from fat_convenios_saude cnv where cnv.CODIGO = V_MBC_AGD_CIRG_CAN.CSP_CNV_CODIGO and (P_CONVENIO = 'T' or P_CONVENIO =
			 * decode(cnv.grupo_convenio,'C','N','P','N','S','S')))
			 */
		}
		criteria.add(Subqueries.exists(subCriteria));
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.DATA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasCanceladasVO.class));

		return executeCriteria(criteria);
	}

	private void restricoes(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO, DetachedCriteria criteria) {
		if (portalPesquisaCirurgiasParametrosVO.getPucUnfSeq() != null && portalPesquisaCirurgiasParametrosVO.getPucUnfSeq() != 0) {
			criteria.add(Restrictions.eq("profCirurgias." + MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString(), portalPesquisaCirurgiasParametrosVO.getPucUnfSeq()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf() != null) {
			criteria.add(Restrictions.eq("profCirurgias." + MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf() != null) {
			criteria.add(Restrictions.eq("profCirurgias." + MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPacCodigo() != null && portalPesquisaCirurgiasParametrosVO.getPacCodigo() != 0) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), portalPesquisaCirurgiasParametrosVO.getPacCodigo()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getDataInicio() != null || portalPesquisaCirurgiasParametrosVO.getDataFim() != null) {
			criteria.add(Restrictions.ge(MbcCirurgias.Fields.DATA.toString(), portalPesquisaCirurgiasParametrosVO.getDataInicio()));
			criteria.add(Restrictions.le(MbcCirurgias.Fields.DATA.toString(), portalPesquisaCirurgiasParametrosVO.getDataFim()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getSala() != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.SALA_CIRURGICA.toString()+"." + MbcSalaCirurgica.Fields.SEQP, portalPesquisaCirurgiasParametrosVO.getSala()));
		}
	}

	private DetachedCriteria view(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "cirurgia");

		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "paciente");
		criteria.createAlias("profCirurgias." + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), "servidor");
		criteria.createAlias(MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "profCirurgias");
		criteria.createAlias(MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString(), "motivoCancelamento");

		ProjectionList projection = Projections.projectionList().add(Projections.property(MbcCirurgias.Fields.SEQ.toString()), CirurgiasCanceladasVO.Fields.CIR_SEQ.toString())
		.add(Projections.property(MbcCirurgias.Fields.AGD_SEQ.toString()), CirurgiasCanceladasVO.Fields.AGD_SEQ.toString())
		.add(Projections.property(MbcCirurgias.Fields.PACIENTE.toString()), CirurgiasCanceladasVO.Fields.PACIENTE.toString())
		.add(Projections.property(MbcCirurgias.Fields.UNF_SEQ.toString()), CirurgiasCanceladasVO.Fields.UNF_SEQ.toString())
		.add(Projections.property("especialidade."+AghEspecialidades.Fields.SEQ.toString()), CirurgiasCanceladasVO.Fields.SEQ_ESPECIALIDADE.toString())
		.add(Projections.property("especialidade."+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), CirurgiasCanceladasVO.Fields.NOME_ESPECIALIDADE.toString())
		.add(Projections.property("especialidade."+AghEspecialidades.Fields.SIGLA.toString()), CirurgiasCanceladasVO.Fields.SIGLA_ESPECIALIDADE.toString())
		.add(Projections.property("paciente."+AipPacientes.Fields.PRONTUARIO.toString()), CirurgiasCanceladasVO.Fields.PRONTUARIO.toString())
		.add(Projections.property("paciente."+AipPacientes.Fields.NOME.toString()), CirurgiasCanceladasVO.Fields.NOME_PACIENTE.toString())
		.add(Projections.property("servidor." + RapServidores.Fields.MATRICULA.toString()), CirurgiasCanceladasVO.Fields.PUC_SER_MATRICULA.toString())
		.add(Projections.property("servidor." + RapServidores.Fields.CODIGO_VINCULO.toString()), CirurgiasCanceladasVO.Fields.PUC_SER_VIN_CODIGO.toString())
		.add(Projections.property("profCirurgias." + MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString()), CirurgiasCanceladasVO.Fields.PUC_UNF_SEQ.toString())
		.add(Projections.property("profCirurgias." + MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString()), CirurgiasCanceladasVO.Fields.PUC_IND_FUNCAO_PROF.toString())
		.add(Projections.property(MbcCirurgias.Fields.DATA.toString()), CirurgiasCanceladasVO.Fields.DT_AGENDA.toString())
		.add(Projections.property(MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()), CirurgiasCanceladasVO.Fields.ORIGEM_PAC_CIRG.toString())
		.add(Projections.property(MbcCirurgias.Fields.ORIGEM_INT_LOCAL.toString()), CirurgiasCanceladasVO.Fields.ORIGEM_INT_LOCAL.toString())
		.add(Projections.property(MbcCirurgias.Fields.SCI_UNFSEQ.toString()), CirurgiasCanceladasVO.Fields.SCI_UNF_SEQ.toString())
		.add(Projections.property(MbcCirurgias.Fields.SCI_SEQP.toString()), CirurgiasCanceladasVO.Fields.SCI_SEQP.toString())
		.add(Projections.property(MbcCirurgias.Fields.CSP_CNV_CODIGO.toString()), CirurgiasCanceladasVO.Fields.CSP_CNV_CODIGO.toString())
		.add(Projections.property(MbcCirurgias.Fields.CSP_SEQ.toString()), CirurgiasCanceladasVO.Fields.CSP_SEQ.toString())
		.add(Projections.property(MbcCirurgias.Fields.DATA.toString()), CirurgiasCanceladasVO.Fields.DT_CANCELAMENTO.toString())
		.add(Projections.property("motivoCancelamento." + MbcMotivoCancelamento.Fields.DESCRICAO.toString()), CirurgiasCanceladasVO.Fields.MOTIVO_CANCELAMENTO.toString())
		.add(Projections.property("motivoCancelamento." + MbcMotivoCancelamento.Fields.SEQ.toString()), CirurgiasCanceladasVO.Fields.MTC_SEQ.toString());

		criteria.setProjection(projection);

		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq("profCirurgias."+MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), 
				Boolean.TRUE ));

		return criteria;
	}

	public List<MbcCirurgias> pesquisarMbcCirurgiasComSalaCirurgia(
			Date dataCirurgia, Short unfSeq, Short sciSeqp,
			DominioSituacaoCirurgia situacaoCirurgia, Boolean situacaoEquals,
			Short sciUnfSeq,	Boolean asc, Boolean dtHrPrevInicioNotNull, String... orders) {
		return pesquisarMbcCirurgiasComSalaCirurgia(dataCirurgia, unfSeq,
				sciSeqp, situacaoCirurgia, situacaoEquals, null, null,
				sciUnfSeq, asc, dtHrPrevInicioNotNull, orders);
	}
	
	public List<MbcCirurgias> pesquisarMbcCirurgiasComSalaCirurgia(
			Date dataCirurgia, Short unfSeq, Short sciSeqp,
			DominioSituacaoCirurgia situacaoCirurgia, Boolean situacaoEquals,
			DominioOrigemPacienteCirurgia origemPacienteCirurgia, Boolean atendimentoIsNull, 
			Short sciUnfSeq, Boolean asc, Boolean dtHrPrevinicioNotNull, String... orders) {
		DetachedCriteria criteria = getCriteriaPesquisarMbcCirurgiasComSalaCirurgia(
				dataCirurgia, unfSeq, sciSeqp, situacaoCirurgia, origemPacienteCirurgia, atendimentoIsNull,
				situacaoEquals, sciUnfSeq, asc, dtHrPrevinicioNotNull, orders);

		// Adicionado a pedido de Liziane Allegretti no dia 09/05/2014, pois uma
		// cirurgia com situação 'TRANS' não deverá durar mais do que 24 horas.
		// Foi dado um prazo maior por segurança.
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.DAY_OF_MONTH, -5);
		
		Calendar dataFim = Calendar.getInstance();
		dataFim.add(Calendar.DAY_OF_MONTH, 1);
		
		criteria.add(Restrictions.between(MbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(dataInicio.getTime()), DateUtil.truncaDataFim(dataFim.getTime())));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> pesquisarMbcCirurgiasByPrevisaoInicioEFim(
			Date dataPrevisaoInicio,Date dataPrevisaoFim, Short unfSeq, Short sciSeqp,
			DominioSituacaoCirurgia situacaoCirurgia, Boolean situacaoEquals,
			Boolean asc, String... orders) {
		DetachedCriteria criteria = getCriteriaPesquisarMbcCirurgiasComSalaCirurgia(
				null, unfSeq, sciSeqp, situacaoCirurgia, null, null,
				situacaoEquals, null, asc, Boolean.FALSE, orders);
		criteria.add(Restrictions.or(
				Restrictions.between(MbcCirurgias.Fields.DTHR_PREV_INICIO.toString(), DateUtil.truncaData(dataPrevisaoInicio), DateUtil.truncaDataFim(dataPrevisaoInicio))
				, 
				Restrictions.between(MbcCirurgias.Fields.DTHR_PREVISAO_FIM.toString(), DateUtil.truncaData(dataPrevisaoFim), DateUtil.truncaDataFim(dataPrevisaoFim))
				));
		
		
		return executeCriteria(criteria);
	}

	public DetachedCriteria getCriteriaPesquisarMbcCirurgiasComSalaCirurgia(Date dataCirurgia, Short unfSeq, Short sciSeqp, 
			DominioSituacaoCirurgia situacaoCirurgia,
			DominioOrigemPacienteCirurgia origemPacienteCirurgia, Boolean atendimentoIsNull,
			Boolean situacaoEquals, Short sciUnfSeq, Boolean asc, Boolean dtHrPrevinicioNotNull, String... orders) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "MBC");
		criteria.createAlias("MBC."+MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		
		//Não interfere no resultado pq column é notNull
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "paciente");
		if (dataCirurgia != null) {
			criteria.add(Restrictions.eq("MBC."+MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		}
		
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("MBC."+MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		}
		
		if(sciUnfSeq != null){
			criteria.add(Restrictions.eq("MBC."+MbcCirurgias.Fields.SCI_UNFSEQ.toString(), unfSeq));
		}
		
		if (situacaoCirurgia != null && situacaoEquals != null) {
			if (situacaoEquals) {
				criteria.add(Restrictions.eq("MBC."+MbcCirurgias.Fields.SITUACAO.toString(), situacaoCirurgia));
			} else {//not equals
				criteria.add(Restrictions.ne("MBC."+MbcCirurgias.Fields.SITUACAO.toString(), situacaoCirurgia));
			}
		}
		getCriteriaPesquisarMbcCirurgiasComSalaCirurgia(sciSeqp,
				origemPacienteCirurgia, atendimentoIsNull, asc, criteria,
				dtHrPrevinicioNotNull, orders);
		return criteria;
	}

	public void getCriteriaPesquisarMbcCirurgiasComSalaCirurgia(Short sciSeqp,
			DominioOrigemPacienteCirurgia origemPacienteCirurgia,
			Boolean atendimentoIsNull, Boolean asc, DetachedCriteria criteria,
			Boolean dtHrPrevinicioNotNull, String... orders) {
		if (sciSeqp != null) {
			criteria.add(Restrictions.eq("MBC."+MbcCirurgias.Fields.SCI_SEQP.toString(), sciSeqp));
		}
		if(origemPacienteCirurgia != null){
			criteria.add(Restrictions.eq("MBC."+MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString(), origemPacienteCirurgia));
		}
		if (atendimentoIsNull != null) {
			if(Boolean.TRUE.equals(atendimentoIsNull)){
				criteria.add(Restrictions.isNull("MBC."+MbcCirurgias.Fields.ATENDIMENTO.toString()));
			} else {
			criteria.add(Restrictions.or(Restrictions.isNull("MBC."+MbcCirurgias.Fields.ATENDIMENTO.toString()), 
					Restrictions.and(Restrictions.isNotNull("MBC."+MbcCirurgias.Fields.ATENDIMENTO.toString()), Restrictions.eq("ATD."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.C))));
			}
		}
		if(Boolean.TRUE.equals(dtHrPrevinicioNotNull)){
			criteria.add(Restrictions.isNotNull("MBC."+MbcCirurgias.Fields.DTHR_PREV_INICIO.toString()));
		}
		if (asc != null && orders != null) {
			if (asc) {
				for (String order : orders) {
					criteria.addOrder(Order.asc(order));
				}
			} else {
				for (String order : orders) {
					criteria.addOrder(Order.desc(order));
				}
			}
		}
	}

	public Boolean verificarSeCirurgiaAgendadaParaEspecialidadeProc(Integer pciSeq, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "PROC_ESP", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("PROC_ESP." + MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq("PROC_ESP." + MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.AGND));
		Long count = executeCriteriaCount(criteria);
		return (count != null && count > 0) ? true : false;
	}

	public List<MbcCirurgias> pesquisarMbcCirurgiaControleEscala(Date data, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "CONV");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), data));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.NATUREZA_AGEND.toString(), DominioNaturezaFichaAnestesia.ELE));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		return executeCriteria(criteria);
	}

	public List<MbcCirurgias> pesquisarCirurgiasDataMarcada(Short unfSeq, Date data) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CUR");
		criteria.add(Restrictions.eq("CUR.".concat(MbcCirurgias.Fields.DATA.toString()), data));
		criteria.add(Restrictions.eq("CUR.".concat(MbcCirurgias.Fields.UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.eq("CUR.".concat(MbcCirurgias.Fields.SITUACAO.toString()), DominioSituacaoCirurgia.AGND));
		criteria.addOrder(Order.asc("CUR." + MbcCirurgias.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}

	public MbcCirurgias obterMbcCirurgiaPorSituacaoRealizada(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		return (MbcCirurgias) this.executeCriteriaUniqueResult(criteria);
	}

	public List<MbcCirurgiaVO> listarItensOrteseProtese(Integer ppcCrgSeq) {
		final StringBuilder hql = new StringBuilder(300);

		hql.append("SELECT ").append(" IPS.").append(SceItemRmps.Fields.DATA.toString()).append(" as data, ").append(" IPS.").append(SceItemRmps.Fields.QUANTIDADE.toString())
		.append(" as quantidadeIps, ").append(" IPS.").append(SceItemRmps.Fields.RMP_SEQ.toString()).append(" as rmpSeq, ").append(" IPS.")
		.append(SceItemRmps.Fields.NUMERO.toString()).append(" as numero, ").append(" PHI.").append(FatProcedHospInternos.Fields.SEQ.toString()).append(" as phiSeq, ")
		.append(" CRG.").append(MbcCirurgias.Fields.UNF_SEQ.toString()).append(" as unfSeq ")

		.append(" FROM ").append(FatProcedHospInternos.class.getSimpleName()).append(" PHI, ").append(SceItemRmps.class.getSimpleName()).append(" IPS ")
		.append(" JOIN  IPS.").append(SceItemRmps.Fields.SCE_RMR_PACIENTE.toString()).append(" RMP ").append(" LEFT JOIN  IPS.")
		.append(SceItemRmps.Fields.ITEM_RMR.toString()).append(" IRR ").append(" LEFT JOIN  IPS.").append(SceItemRmps.Fields.SCE_ESTQ_ALMOX.toString()).append(" EAL ")
		.append(" JOIN  RMP.").append(SceRmrPaciente.Fields.CIRURGIA.toString()).append(" CRG ")

		.append(" WHERE ").append(" EAL.").append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString()).append(" = ").append("PHI.")
		.append(FatProcedHospInternos.Fields.MAT_CODIGO.toString())

		.append(" AND  CRG.").append(MbcCirurgias.Fields.SITUACAO.toString()).append(" = :situacaoCrg").append(" AND  CRG.").append(MbcCirurgias.Fields.SEQ.toString())
		.append(" = :ppcPrgseq");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacaoCrg", DominioSituacaoCirurgia.RZDA);
		query.setParameter("ppcPrgseq", ppcCrgSeq);
		query.setResultTransformer(Transformers.aliasToBean(MbcCirurgiaVO.class));

		return query.list();
	}

	/**Verifica se dthr prev inicio e dthr prev fim da cirurgia não o está entre outra da mesma sala cirurgica e unidade. 
	 * @param seqAtendimento
	 * @param dataFimCirurgia
	 * @return
	 */
	public List<MbcCirurgias> listarDataPrvInicioFim(MbcCirurgias cirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), cirurgia.getData()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNF_SEQ.toString(), cirurgia.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SALA_CIRURGICA.toString(), cirurgia.getSalaCirurgica()));
		if (cirurgia.getSeq() != null){
			criteria.add(Restrictions.ne(MbcCirurgias.Fields.SEQ.toString(), cirurgia.getSeq()));	
		}
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_PREV_INICIO.toString()));
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_PREVISAO_FIM.toString()));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		
		criteria.add(Restrictions.sqlRestriction(montaSqlOverlaps(cirurgia.getDataPrevisaoInicio(), cirurgia.getDataPrevisaoFim())));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Utilizado o comando SQL ANSI 'OVERLAPS' para verificar a colisão das datas.
	 * @param dataPrevInicio
	 * @param dataPrevFim
	 * @return
	 */
	private String montaSqlOverlaps(Date dataPrevInicio, Date dataPrevFim) {
		StringBuilder sql = new StringBuilder(300);
		
		String dataInicioFormatada = DateUtil.obterDataFormatada(dataPrevInicio, "dd/MM/yyyy HH:mm");
		String dataFimFormatada = DateUtil.obterDataFormatada(dataPrevFim, "dd/MM/yyyy HH:mm");
		
		sql.append(" ( ") ;
		sql.append(" TO_TIMESTAMP('").append(dataInicioFormatada).append("', 'DD/MM/YYYY HH24:MI') ").append(", ");
		sql.append(" TO_TIMESTAMP('").append(dataFimFormatada).append("', 'DD/MM/YYYY HH24:MI')");
		sql.append(" ) ") ;
		sql.append(" overlaps ");
		sql.append(" ( ") ;
		sql.append(" TO_TIMESTAMP(TO_CHAR(DTHR_PREV_INICIO, 'DD/MM/YYYY HH24:MI'), 'DD/MM/YYYY HH24:MI')").append(", ");
		sql.append(" TO_TIMESTAMP(TO_CHAR(DTHR_PREV_FIM, 'DD/MM/YYYY HH24:MI'), 'DD/MM/YYYY HH24:MI') ");
		sql.append(" ) ");
		
		return sql.toString();
	}

	public List<RelatorioPacientesComCirurgiaPorUnidadeVO> listarCirurgiasPorSeqUnidadeFuncionalData(Short seqUnidadeCirurgica, Short seqUnidadeInternacao, Date dataCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("atd." + AghAtendimentos.Fields.UNF_SEQ), 	RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.ATD_UNF_SEQ.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.CODIGO), 	RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO), RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.PAC_PRONTUARIO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.NOME), 		RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.PAC_NOME.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_INICIO_CIRG), RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.DTHR_CIRURGIA.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SCI_SEQP), 	RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.SCI_SEQP.toString())
				.add(Projections.property("csp." + FatConvenioSaude.Fields.CODIGO), RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.CONVENIO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SEQ), 		RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.SEQ_CIRURGIA.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.NUMERO_AGENDA), RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.NRO_AGENDA.toString())
		);
		criteria.createAlias("crg." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL	.toString(), "unf").createAlias("crg." + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "csp");
		criteria.createAlias("crg." + MbcCirurgias.Fields.PACIENTE			.toString(), "pac").createAlias("pac." + AipPacientes.Fields.ATENDIMENTOS.toString(), "atd");
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.ne("crg." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		if (seqUnidadeCirurgica != null) {
			criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnidadeCirurgica));	}
		if (seqUnidadeInternacao != null) {
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.UNF_SEQ.toString(), seqUnidadeInternacao));}
		if (dataCirurgia != null) {
			criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.DATA.toString(), dataCirurgia));	}
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioPacientesComCirurgiaPorUnidadeVO.class));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioPacientesComCirurgiaPorUnidadeVO.class));
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(Short seqUnidadeCirurgica, Date dataCirurgia, Short numeroAgenda, Boolean nsDigitada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		criteria.createAlias("crg." + MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("crg." + MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "csp");
		criteria.createAlias("crg." + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "cnv");
	
		criteria.add(Restrictions.ne("crg." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		if (seqUnidadeCirurgica != null) {
			criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.UNF_SEQ.toString(), seqUnidadeCirurgica));
		}
		if (dataCirurgia != null) {
			criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		}
		if (numeroAgenda != null) {
			criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.NUMERO_AGENDA.toString(), numeroAgenda));
		}
		if (nsDigitada != null && nsDigitada.equals(Boolean.TRUE)) {
			criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), nsDigitada));
		}
		return executeCriteria(criteria);
	}

	public MbcCirurgias obterCirurgiaPacientePorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}

	public DominioSituacaoCirurgia obterSituacaoCirurgia(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.setProjection(Property.forName(MbcCirurgias.Fields.SITUACAO.toString()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), seq));
		return (DominioSituacaoCirurgia) executeCriteriaUniqueResult(criteria);
	}

	public Short obterUltimoNumeroAgenda(MbcCirurgias cirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), cirurgia.getData()));
		criteria.setProjection(Projections.max(MbcCirurgias.Fields.NUMERO_AGENDA.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public MbcCirurgias obterCirurgiaAgendadaGeradaSistema(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.AGENDA.toString(), "agd", DetachedCriteria.INNER_JOIN);
		criteria.add(Restrictions.eq("agd.".concat(MbcAgendas.Fields.SEQ.toString()), seq));
		criteria.add(Restrictions.eq("agd.".concat(MbcAgendas.Fields.IND_GERADO_SISTEMA.toString()), Boolean.TRUE));
		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcCirurgias> listarPacientesSalaRecuperacaoPorUnidade(Short unidade, Byte destinoPaciente, BigDecimal diasRetroativos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		Date sysdate = DateUtil.adicionaDias(new GregorianCalendar().getTime(), (diasRetroativos.intValue()*(-1)));
		addJoinPacienteEUnidadeFuncional(criteria);
		// #27603 - define join com AghAtendimentos 
		criteria.createAlias("CRG." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD",  DetachedCriteria.LEFT_JOIN);		
		
		criteria.add(Restrictions.gt(MbcCirurgias.Fields.DATA.toString(), sysdate));
		criteria.add(Restrictions.isNotNull((MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString())));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNF_SEQ.toString(), unidade));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DPA_SEQ.toString(), destinoPaciente));
		return executeCriteria(criteria);
	}
	
	public MbcCirurgias obterCirurgiaComUnidadePacienteDestinoPorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		criteria.createAlias(MbcCirurgias.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias(MbcCirurgias.Fields.DESTINO_PACIENTE.toString(), "dpa", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "proc");
		criteria.createAlias(MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "conv");
		criteria.createAlias("proc." + MbcProcEspPorCirurgias.Fields.CIRURGIA.toString(), "cirProc");
		
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		
		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}
	
	private void addJoinPacienteEUnidadeFuncional(DetachedCriteria criteria) {
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), 			MbcCirurgias.Fields.PACIENTE.toString());
		criteria.createAlias(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), 	MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString());
	}
	
	public MbcCirurgias obterCirurgiaAgendadaPacienteMesmoDia(Date dataCirurgia, Integer pacCodigo, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		criteria.add(Restrictions.eq("pac.".concat(AipPacientes.Fields.CODIGO.toString()), pacCodigo));
		criteria.add(Restrictions.eq("unf.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), unfSeq));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		List<MbcCirurgias> listaCirurgias = executeCriteria(criteria);
		return listaCirurgias.isEmpty() ? null : listaCirurgias.get(0);
	}
	
	public MbcCirurgias obterCirurgiaAgendadaPacienteMesmoDia(Date dataCirurgia, Integer pacCodigo, Short unfSeq, Short numeroAgenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.add(Restrictions.eq("pac.".concat(AipPacientes.Fields.CODIGO.toString()), pacCodigo));
		criteria.add(Restrictions.eq("unf.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), unfSeq));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.NUMERO_AGENDA.toString(), numeroAgenda));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		List<MbcCirurgias> listaCirurgias = executeCriteria(criteria);
		for (MbcCirurgias cirurgia : listaCirurgias) {			
			if (DateValidator.validarMesmoDia(dataCirurgia, cirurgia.getData())) { // Comparação de data no mesmo dia. Vide: CRG.DATA = C_DATA no CURSOR CUR_CRG_UPD
				return cirurgia;
			}
		}
		return null;
	}
	
	public List<MbcCirurgias> pesquisarCirurgiasAgendaDataCentroCusto(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, Short agenda, Date data, Integer centroCusto) {
		DetachedCriteria criteria =  montaCriteriaPesquisarCirurgiasAgendaDataCentroCusto(agenda, data, centroCusto);
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.DATA.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCirurgiasAgendaDataCentroCustoCount(Short agenda, Date data, Integer centroCusto) {
		DetachedCriteria criteria =  montaCriteriaPesquisarCirurgiasAgendaDataCentroCusto(agenda, data, centroCusto);
		return executeCriteriaCount(criteria);
	}

	public DetachedCriteria montaCriteriaPesquisarCirurgiasAgendaDataCentroCusto(Short agenda, Date data, Integer centroCusto) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class,"MBC");
		criteria.createAlias("MBC." + MbcCirurgias.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MBC." + MbcCirurgias.Fields.CENTRO_CUSTO.toString(), "CCT", JoinType.LEFT_OUTER_JOIN);
		
	
		if (agenda != null) {
			criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.NUMERO_AGENDA.toString(), agenda));
		}
		if (data != null) {
			criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.DATA.toString(), data));
		}
		if (centroCusto != null) {		
			criteria.add(Restrictions.eq("CCT.".concat(FccCentroCustos.Fields.CODIGO.toString()), centroCusto));
		}
	
		return criteria;
	}
	
	public List<MbcCirurgias> pesquisarColisaoHorariosAnestesista(Date dataCirurgia, Integer crgSeq, Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias(MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "pcg");
		criteria.createAlias(MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "sci");
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()));
		criteria.add(Restrictions.eq("pcg.".concat(MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()), matricula));
		criteria.add(Restrictions.eq("pcg.".concat(MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()), vinCodigo));
		return executeCriteria(criteria);
	}
	
	public List<DescricaoCirurgiaPdtSalaVO> pesquisarCirurgiaComDescricaoMesmaSala(Short unfSeq, Date dataCirurgia, 
			Integer crgSeq, Short sciUnfSeq, Short sciSeqp) {
		String aliasCrg = "crg";
		String aliasDcg = "dcg";
		String aliasSci = "sci";
		String aliasDti = "dti";
		String aliasPfd = "pfd";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), DescricaoCirurgiaPdtSalaVO.Fields.CRG_SEQ.toString())
				.add(Projections.property(aliasDti + ponto + MbcDescricaoItens.Fields.DCG_SEQP.toString()), DescricaoCirurgiaPdtSalaVO.Fields.DCG_SEQP.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), DescricaoCirurgiaPdtSalaVO.Fields.DATA_CRG.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString()), DescricaoCirurgiaPdtSalaVO.Fields.SCI_SEQP.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.PAC_CODIGO.toString()), DescricaoCirurgiaPdtSalaVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property(aliasSci + ponto + MbcSalaCirurgica.Fields.NOME.toString()), DescricaoCirurgiaPdtSalaVO.Fields.SALA.toString())
				.add(Projections.property(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG.toString()), DescricaoCirurgiaPdtSalaVO.Fields.DTHR_INICIO_CIRG.toString())
				.add(Projections.property(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_FIM_CIRG.toString()), DescricaoCirurgiaPdtSalaVO.Fields.DTHR_FIM_CIRG.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), DescricaoCirurgiaPdtSalaVO.Fields.NOME_USUAL.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), DescricaoCirurgiaPdtSalaVO.Fields.NOME.toString())
				.add(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.SER_MATRICULA_PROF.toString()), DescricaoCirurgiaPdtSalaVO.Fields.SER_MATRICULA_PROF.toString())
				.add(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.toString()), DescricaoCirurgiaPdtSalaVO.Fields.SER_VIN_CODIGO_PROF.toString());
		
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), aliasSci);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.DESCRICOES_CIRURGIAS.toString(), aliasDcg);
		criteria.createAlias(aliasDcg + ponto + MbcDescricaoCirurgica.Fields.MBC_DESCRICAO_ITENSES.toString(), aliasDti);
		criteria.createAlias(aliasDcg + ponto + MbcDescricaoCirurgica.Fields.MBC_PROF_DESCRICOES.toString(), aliasPfd);
		criteria.createAlias(aliasPfd + ponto + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA, aliasPes);
		
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SCI_UNF_SEQ.toString(), sciUnfSeq));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString(), sciSeqp));
		criteria.add(Restrictions.isNotNull(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_INICIO_CIRG.toString()));
		criteria.add(Restrictions.isNotNull(aliasDti + ponto + MbcDescricaoItens.Fields.DTHR_FIM_CIRG.toString()));
		criteria.add(Restrictions.eq(aliasPfd + ponto + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString(), DominioTipoAtuacao.RESP));
		
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(aliasDti + ponto + MbcDescricaoItens.Fields.DCG_SEQP.toString()));
		
		criteria.setProjection(proj);
		
		criteria.setResultTransformer(Transformers.aliasToBean(DescricaoCirurgiaPdtSalaVO.class));
		
		return executeCriteria(criteria);
	}
	
	public DominioOrigemPacienteCirurgia obterOrigemPacienteCirurgia(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.setProjection(Projections.property(MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		return (DominioOrigemPacienteCirurgia) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MbcCirurgias> pesquisarMotivoCirurgiasCanceladas(Integer agdSeq, Short vlrNumerico) {
		DetachedCriteria criteria = montarCriteriaMotivoCirurgiasCanceladas(agdSeq, vlrNumerico);
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> pesquisarMotivoCirurgiasCanceladasOrdenaPorData(Integer agdSeq, Short vlrNumerico) {
		DetachedCriteria criteria = montarCriteriaMotivoCirurgiasCanceladas(agdSeq, vlrNumerico);
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.DATA.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarMotivoCirurgiasCanceladasCount(Integer agdSeq, Short vlrNumerico) {
		DetachedCriteria criteria = montarCriteriaMotivoCirurgiasCanceladas(agdSeq, vlrNumerico);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria montarCriteriaMotivoCirurgiasCanceladas(Integer agdSeq, Short vlrNumerico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		
		criteria.createAlias(MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString(), "mtc");
		criteria.createAlias(MbcCirurgias.Fields.QUESTAO.toString(), "qes", DetachedCriteria.LEFT_JOIN);
				
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.MTC_SEQ.toString(), vlrNumerico));
		
		return criteria;
	}
	
	public List<ProtocoloEntregaNotasDeConsumoVO> relatorioProtocoloEntregaNotaConsumo(Short unfSeq, Date data, 
			DominioOrdenacaoProtocoloEntregaNotasConsumo ordenacao) {
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(MbcMotivoCancelamento.class);
		subQuery.setProjection(Projections.property(MbcMotivoCancelamento.Fields.SEQ.toString()));
		subQuery.add(Restrictions.eq(MbcMotivoCancelamento.Fields.ERRO_AGENDAMENTO.toString(), false));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG.".concat(MbcCirurgias.Fields.PACIENTE.toString()), "PAC");

		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("CRG." + MbcCirurgias.Fields.SEQ.toString()), 
						ProtocoloEntregaNotasDeConsumoVO.Fields.CRG_SEQ.toString())
			.add(Projections.property("CRG." + MbcCirurgias.Fields.NUMERO_AGENDA.toString()), 
					ProtocoloEntregaNotasDeConsumoVO.Fields.NUMERO_AGENDA.toString())
			.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), 
					ProtocoloEntregaNotasDeConsumoVO.Fields.NOME.toString())
			.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), 
					ProtocoloEntregaNotasDeConsumoVO.Fields.PRONTUARIO.toString())
			.add(Projections.property("CRG." + MbcCirurgias.Fields.SITUACAO.toString()), 
					ProtocoloEntregaNotasDeConsumoVO.Fields.SITUACAO.toString())
			.add(Projections.property("CRG." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString()),
					ProtocoloEntregaNotasDeConsumoVO.Fields.IND_DIGT_NOTA_SALA.toString()));
		
		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.DATA.toString()), data));
		
		criteria.add(Restrictions.or(Restrictions.isNull("CRG." + MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString()), 
									 Subqueries.propertyIn("CRG." + MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString(), subQuery)));
		
		
		criteria.addOrder(Order.asc(StringUtils.equals(ordenacao.toString(), DominioOrdenacaoProtocoloEntregaNotasConsumo.AGENDA.toString())
				? "CRG.".concat(MbcCirurgias.Fields.NUMERO_AGENDA.toString()) : "PAC.".concat(AipPacientes.Fields.NOME.toString())));
				
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloEntregaNotasDeConsumoVO.class));

		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> verificarCirurgiasCanceladas(Integer agdSeq, Short motvCancId){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(),DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.MTC_SEQ.toString(),motvCancId));
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.DATA.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Monta criteria da pesquisa paginada de cirurgias
	 * 
	 * @param elemento
	 * @return
	 */
	private DetachedCriteria getCriteriaPesquisarCirurgiasRealizadasNotaConsumo(MbcCirurgias elemento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(),"PAC");
		criteria.createAlias(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(),"UND", JoinType.LEFT_OUTER_JOIN);
		
		if (elemento.getUnidadeFuncional() != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), elemento.getUnidadeFuncional()));
		}
		
		if (elemento.getData() != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), elemento.getData()));
		}
		
		if (elemento.getNumeroAgenda() != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.NUMERO_AGENDA.toString(), elemento.getNumeroAgenda()));
		}
		
		if (elemento.getDigitaNotaSala() != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), elemento.getDigitaNotaSala()));
		}
		
		if (elemento.getPaciente() != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.PACIENTE.toString(), elemento.getPaciente()));
		}
		
		if(elemento.getAtendimento() != null && elemento.getAtendimento().getSeq() != null){
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString(), elemento.getAtendimento().getSeq()));
		}
		
//		criteria.add(Restrictions.le(MbcCirurgias.Fields.DATA.toString(), new Date()));
		
		
		return criteria;
	}
	
	/**
	 * Pesquisa paginada de cirurgias
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	public List<MbcCirurgias> pesquisarCirurgiasRealizadasNotaConsumo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcCirurgias elemento) {
		
		DetachedCriteria criteria = getCriteriaPesquisarCirurgiasRealizadasNotaConsumo(elemento);
		
		criteria.addOrder(Order.desc(MbcCirurgias.Fields.DATA.toString()));
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.NUMERO_AGENDA.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Contabiliza resultados da pesquisa paginada de cirurgias
	 * 
	 * @param elemento
	 * @return
	 */
	public Long pesquisarCirurgiasRealizadasNotaConsumoCount(MbcCirurgias elemento) {
		DetachedCriteria criteria = getCriteriaPesquisarCirurgiasRealizadasNotaConsumo(elemento);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaCirurRealizPorEspecEProf(
			Short unidadeFuncional, Date dataInicial, Date dataFinal,
			Short especialidade) {
		String ponto = ".";
		String aliasCrg = "crg"; // MBC_CIRURGIAS CRG
		String aliasPuc = "puc"; // MBC_PROF_ATUA_UNID_CIRGS PUC
		String aliasSer = "ser"; // RAP_SERVIDORES SER
		String aliasPcg = "pcg"; // MBC_PROF_CIRURGIAS PCG
		String aliasEsp = "esp"; // AGH_ESPECIALIDADES ESP
		String aliasPes = "pes"; // RAP_PESSOAS_FISICAS PES

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasEsp);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), aliasPcg);
		criteria.createAlias(aliasPcg + ponto + MbcProfCirurgias.Fields.UNID_CIRG.toString(), aliasPuc);
		criteria.createAlias(aliasPuc + ponto + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);

		criteria.add(Restrictions.eq(aliasPcg + ponto + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unidadeFuncional));
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		if (especialidade != null) {
			criteria.add(Restrictions.eq(aliasEsp + ponto + AghEspecialidades.Fields.SEQ.toString(), especialidade));
		}

		return criteria;
	}
	

	public List<MbcRelatCirurRealizPorEspecEProfVO> obterCirurRealizPorEspecEProf(
			final Short unidadeFuncional, final Date dataInicial,
			final Date dataFinal, final Short especialidade) {
		
		String ponto	= ".";
		String aliasCrg = "crg"; // MBC_CIRURGIAS 					CRG
		String aliasCsp = "csp"; // FAT_CONV_SAUDE_PLANOS 	 		CSP
		String aliasEsp = "esp"; // AGH_ESPECIALIDADES 				ESP
		String aliasPes = "pes"; // RAP_PESSOAS_FISICAS 			PES
		String aliasPac = "pac"; // AIP_PACIENTES 					PAC
		String aliasCnv = "cnv"; // FAT_CONVENIOS_SAUDE 			CNV
		String aliasClc = "clc"; // AGH_CLINICAS 					CLC
		
		String aliasHibPes= "pes5_"; // este alias foi criado, pois não tive como passar dinamicamente.
		
		DetachedCriteria criteria = obterCriteriaCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);
		
		Projection projection = Projections.projectionList()
		.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SEQ.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.ESP_SEQ.toString())
		.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.NOME.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.ESP_NOME.toString())
		.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.PES_NOME.toString())
		.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.PES_NOME_USUAL.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.CRG_SEQ.toString())
		.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.PAC_NOME.toString())
		.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.PAC_PRONTUARIO.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.CRG_DTHR_INICIO_CIRG.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.NUMERO_AGENDA.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.CRG_NRO_AGENDA.toString())
		.add(Projections.property(aliasCnv + ponto + FatConvenioSaude.Fields.DESCRICAO.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.CNV_CONVENIO.toString())
		.add(Projections.property(aliasClc + ponto + AghClinicas.Fields.DESCRICAO.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.CLC_DESCRICAO.toString());
		
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), aliasCsp);
		criteria.createAlias(aliasCsp + ponto + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), aliasCnv);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		
		// clinica
		criteria.createAlias(aliasEsp + ponto + AghEspecialidades.Fields.CLINICA.toString(), aliasClc, DetachedCriteria.LEFT_JOIN);
		
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.ESP_SEQ.toString()));
		//,COALESCE(PES.NOME_USUAL,NULL,SUBSTR(PES.NOME,1,15),PES.NOME_USUAL) 
		criteria.addOrder(OrderBySql.sql(" COALESCE(" + aliasHibPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.name() 
				+ ", NULL, SUBSTR( " + aliasHibPes + ponto + RapPessoasFisicas.Fields.NOME.name()+ ", 1, 15)," 
				+ aliasHibPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.name() + ")"));
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));

		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(MbcRelatCirurRealizPorEspecEProfVO.class));
		return executeCriteria(criteria);
	}

	public List<MbcCirurgias> listarLocalPacMbc(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(MbcCirurgias.Fields.DTHR_PREV_INICIO.toString(), DateUtil.adicionaDias(new Date(), -2)));
		
		criteria.add(Restrictions.or(
				Restrictions.in(MbcCirurgias.Fields.SITUACAO.toString(), new DominioSituacaoCirurgia[] { DominioSituacaoCirurgia.PREP, DominioSituacaoCirurgia.TRAN}),
					Restrictions.and(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString()), 
							Restrictions.isNull(MbcCirurgias.Fields.DTHR_SAIDA_SR.toString()))
				));
		
		return executeCriteria(criteria, 0, 1, null, false);
	}

	public List<String> obterProcedimentosDaCirurgia(final Integer seqCirurgia, final Boolean procedimentoPrincipal){
		
		String ponto	= ".";
		String aliasCrg = "crg"; // MBC_CIRURGIAS 					CRG
		String aliasPpc = "ppc"; // MBC_PROC_ESP_POR_CIRURGIAS 		PPC
		String aliasEpr = "epr"; // MBC_ESPECIALIDADE_PROC_CIRGS 	EPR
		String aliasPci = "pci"; // MBC_PROCEDIMENTO_CIRURGICOS 	PCI
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);
		
		Projection projection = Projections.projectionList()
		.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), MbcRelatCirurRealizPorEspecEProfVO.Fields.PCI_DESCRICAO.toString());
		
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPpc);
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), aliasEpr);
		criteria.createAlias(aliasEpr + ponto + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), aliasPci);
		
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString(), seqCirurgia));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(procedimentoPrincipal){
			criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		}
		
		criteria.setProjection(projection);
		return executeCriteria(criteria);
				
	}

	public List<MbcTotalCirurRealizPorEspecEProfVO> obterTotalizadorCirurRealizPorEspecEProf(
			Short unidadeFuncional, Date dataInicial, Date dataFinal,
			Short seqEspecialidade) {
			
			String ponto	= ".";
			String aliasEsp = "esp"; // AGH_ESPECIALIDADES 			ESP
			String aliasPes = "pes"; // RAP_PESSOAS_FISICAS 		PES
																					
			DetachedCriteria criteria = obterCriteriaCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, seqEspecialidade);
			
			Projection projection = Projections.projectionList()
			.add(Projections.groupProperty(aliasEsp + ponto + AghEspecialidades.Fields.SEQ.toString()), MbcTotalCirurRealizPorEspecEProfVO.Fields.ESP_SEQ.toString())
			.add(Projections.groupProperty(aliasEsp + ponto + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), MbcTotalCirurRealizPorEspecEProfVO.Fields.ESP_NOME_ESPECIALIDADE.toString())
			.add(Projections.groupProperty(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), MbcTotalCirurRealizPorEspecEProfVO.Fields.PES_NOME.toString())
			.add(Projections.groupProperty(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), MbcTotalCirurRealizPorEspecEProfVO.Fields.PES_NOME_USUAL.toString())
			.add(Projections.rowCount(), MbcTotalCirurRealizPorEspecEProfVO.Fields.NUMERO_CIRURGIAS.toString());
			
			criteria.addOrder(Order.asc(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()));
			criteria.addOrder(Order.asc(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		
			criteria.setProjection(projection);
			criteria.setResultTransformer(Transformers.aliasToBean(MbcTotalCirurRealizPorEspecEProfVO.class));
			return executeCriteria(criteria);
	}
	
	
	public Long quantidadeCirurgiasSemRetorno(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "PPC");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "CNV");

		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), false));
		criteria.add(Restrictions.ne("CRG."+MbcCirurgias.Fields.SITUACAO.toString(),DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.between("CRG."+MbcCirurgias.Fields.DATA.toString(), dataInicio, dataFim));
		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));

		criteria.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), true));
		
		if(codigoConvenio != null) {
			criteria.add(Restrictions.eq("CNV."+FatConvenioSaude.Fields.CODIGO.toString(), codigoConvenio));
		}

		if(seqProcedimento != null) {
			criteria.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_SEQ.toString(),seqProcedimento));
		}
		
		Projection projection = Projections.projectionList()
				.add(Projections.rowCount())
				.add(Projections.groupProperty("CRG."+MbcCirurgias.Fields.UNF_SEQ.toString()));

		criteria.setProjection(projection);
		Object[] resultado = ((Object[])executeCriteriaUniqueResult(criteria));
		
		return (resultado != null)?(Long)resultado[0] : 0;
	}
	
	public List<RelatorioCirurgiaComRetornoVO> listarCirurgiasComRetorno(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "PCG");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "CNV");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.createAlias("CRG."+MbcCirurgias.Fields.DESTINO_PACIENTE.toString(), "DPA", Criteria.LEFT_JOIN);
		criteria.createAlias("PCG." + MbcProfCirurgias.Fields.UNID_CIRG.toString(), "PUC");
		criteria.createAlias("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		
		Projection projection = Projections.projectionList()
				.add(Projections.property("CRG."+MbcCirurgias.Fields.SEQ.toString()), RelatorioCirurgiaComRetornoVO.Fields.CRG_SEQ.toString())
				.add(Projections.property("CRG."+MbcCirurgias.Fields.DATA.toString()), RelatorioCirurgiaComRetornoVO.Fields.DATA.toString())
				.add(Projections.property("CRG."+MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), RelatorioCirurgiaComRetornoVO.Fields.DATA_INCIO.toString())
				.add(Projections.property("CRG."+MbcCirurgias.Fields.SCI_SEQP.toString()), RelatorioCirurgiaComRetornoVO.Fields.SALA.toString())
				.add(Projections.property("CRG."+MbcCirurgias.Fields.NUMERO_AGENDA.toString()), RelatorioCirurgiaComRetornoVO.Fields.NRO_AGENDA.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO.toString()), RelatorioCirurgiaComRetornoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()), RelatorioCirurgiaComRetornoVO.Fields.NOME.toString())
				.add(Projections.property("CNV."+FatConvenioSaude.Fields.CODIGO.toString()), RelatorioCirurgiaComRetornoVO.Fields.CODIGO_CONVENIO.toString())
				.add(Projections.property("CSP."+FatConvenioSaudePlano.Fields.SEQ.toString()), RelatorioCirurgiaComRetornoVO.Fields.CODIGO_PLANO.toString())
				.add(Projections.property("CNV."+FatConvenioSaude.Fields.DESCRICAO.toString()), RelatorioCirurgiaComRetornoVO.Fields.CONVENIO.toString())
				.add(Projections.property("CSP."+FatConvenioSaudePlano.Fields.DESCRICAO.toString()), RelatorioCirurgiaComRetornoVO.Fields.PLANO.toString())
				.add(Projections.property("CRG."+MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()), RelatorioCirurgiaComRetornoVO.Fields.ORIGEM.toString())
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME.toString()), RelatorioCirurgiaComRetornoVO.Fields.NOME_MEDICO.toString())
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME_USUAL.toString()), RelatorioCirurgiaComRetornoVO.Fields.NOME_USUAL_MEDICO.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.SIGLA.toString()), RelatorioCirurgiaComRetornoVO.Fields.ESPECIALIDADE.toString())
				.add(Projections.property("DPA."+MbcDestinoPaciente.Fields.SEQ.toString()), RelatorioCirurgiaComRetornoVO.Fields.CODIGO_DESTINO.toString())
				.add(Projections.property("DPA."+MbcDestinoPaciente.Fields.DESCRICAO.toString()), RelatorioCirurgiaComRetornoVO.Fields.DESTINO.toString());

		criteria.setProjection(projection);
		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), true));
		criteria.add(Restrictions.eq("PCG." + MbcProfCirurgias.Fields.IND_REALIZOU.toString(), true));
		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.SITUACAO.toString(),DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.between("CRG."+MbcCirurgias.Fields.DATA.toString(), dataInicio, dataFim));
		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		if(codigoConvenio != null) {
			criteria.add(Restrictions.eq("CNV."+FatConvenioSaude.Fields.CODIGO.toString(), codigoConvenio));
		}
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(MbcCirurgias.class, "CRG2");
		subQuery.createAlias("CRG2."+MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "PPC");
		subQuery.setProjection(Projections.property("PPC." + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString()));
		subQuery.add(Restrictions.eq("CRG2."+MbcCirurgias.Fields.SITUACAO.toString(),DominioSituacaoCirurgia.RZDA));
		subQuery.add(Restrictions.between("CRG2."+MbcCirurgias.Fields.DATA.toString(), dataInicio, dataFim));
		subQuery.add(Restrictions.eq("CRG2."+MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		subQuery.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(),DominioIndRespProc.NOTA));
		subQuery.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.SITUACAO.toString(),DominioSituacao.A));
		if(seqProcedimento != null) {
			subQuery.add(Restrictions.eq("PPC."+MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_SEQ.toString(),seqProcedimento));
		}
		
		criteria.add(Subqueries.propertyIn("CRG."+MbcCirurgias.Fields.SEQ.toString(), subQuery));
		
		criteria.addOrder(Order.asc("CRG."+MbcCirurgias.Fields.DATA.toString()));
		criteria.addOrder(Order.asc("CRG."+MbcCirurgias.Fields.SCI_SEQP.toString()));
		criteria.addOrder(Order.asc("CRG."+MbcCirurgias.Fields.NUMERO_AGENDA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioCirurgiaComRetornoVO.class));
		
		return executeCriteria(criteria);
	}

	public List<MbcCirurgias> pesquisarCirurgiasPorUnidadeDataSolicitacao(Short unfSeq,Short unfExecExames, Date dataCirurgia, Boolean cirgComSolicitacao, Boolean relIndicExames, Boolean joinConvenio, MbcCirurgias.Fields orderProperty ) {
		// define join de Cirurgia com Paciente e com Unidade Funcional e critérios de pesquisa para situacao, unf.seq e data
		DetachedCriteria criteria = obterCriteriaPrincipalRelatorios(unfSeq, dataCirurgia);		
		
		if (joinConvenio){
			// define join de Cirurgia com ConvenioSaudePlano e ConvenioSaude
			criteria.createAlias("CIR." + MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP");		
			criteria.createAlias("CSP." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CNV");  
		}
		
		if (cirgComSolicitacao && !relIndicExames) {
			
			DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcSolicHemoCirgAgendada.class, "SHA");
			// define join de SolicHemoCirgAgendada com ComponenteSanguineo
			subCriteria.createAlias("SHA." + MbcSolicHemoCirgAgendada.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "CSA"); 	
			subCriteria.setProjection(Projections.property("SHA." + MbcSolicHemoCirgAgendada.Fields.CRIADO_EM.toString()));
			// define critérios de pesquisa 			
			subCriteria.add(Restrictions.eqProperty("CIR." + MbcCirurgias.Fields.SEQ.toString(), "SHA." + MbcSolicHemoCirgAgendada.Fields.ID_CRG_SEQ.toString()));
			//subCriteria.add(Restrictions.eqProperty("CSA." + AbsComponenteSanguineo.Fields.CODIGO.toString(), "SHA." + MbcSolicHemoCirgAgendada.Fields.ID_CSA_CODIGO.toString()));
			
			criteria.add(Subqueries.exists(subCriteria)); 
		}else if(cirgComSolicitacao){

			DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
			subCriteria.createAlias("SEC." + MbcSolicitacaoEspExecCirg.Fields.MBC_NECESSIDADE_CIRURGICAS.toString(), "NCI"); 	
			subCriteria.setProjection(Projections.property("SEC." + MbcSolicitacaoEspExecCirg.Fields.CRIADO_EM.toString()));
			// define critérios de pesquisa 			
			subCriteria.add(Restrictions.eqProperty("CIR." + MbcCirurgias.Fields.SEQ.toString(), "SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString()));
			subCriteria.add(Restrictions.eq("NCI."+MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString()+"."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfExecExames));
			criteria.add(Subqueries.exists(subCriteria)); 
		}
		
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));
		
		
		if(orderProperty != null){
			criteria.addOrder(Order.asc(orderProperty.toString()));
		}	
		return executeCriteria(criteria);
	}
	
	public List<Object[]> listarCirurgiasComProcedimentosAtivos(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "PPC");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CRG." + MbcCirurgias.Fields.CSP_CNV_CODIGO.toString()))
				.add(Projections.property("CRG." + MbcCirurgias.Fields.CSP_SEQ.toString()))
				.add(Projections.property("PPC." + MbcProcEspPorCirurgias.Fields.PHI_SEQ.toString())));
		
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString() , Boolean.TRUE));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString() , DominioSituacao.A));
		criteria.add(Restrictions.isNotNull("PPC." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_HOSP_INTERNO.toString()));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString() , crgSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<Integer> pesquisarDiariaAutorizadaComSenha(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.setProjection(Projections.property("CRG." + MbcCirurgias.Fields.SEQ.toString()));
		
		DetachedCriteria subCriteriaAtd = DetachedCriteria.forClass(AghAtendimentos.class, "ATD2");
		subCriteriaAtd.createAlias("ATD2." + AghAtendimentos.Fields.INTERNACAO.toString(), "INT");
		subCriteriaAtd.setProjection(Projections.property("ATD2." + AghAtendimentos.Fields.INT_SEQ.toString()));
		subCriteriaAtd.add(Restrictions.eqProperty("INT." + AinInternacao.Fields.SEQ.toString(), 
				"ATD2." + AghAtendimentos.Fields.INT_SEQ.toString()));
		
		DetachedCriteria subCriteriaDau = DetachedCriteria.forClass(AinDiariasAutorizadas.class, "DAU");
		subCriteriaDau.createAlias("DAU." + AinDiariasAutorizadas.Fields.INTERNACAO.toString() , "INT2");
		subCriteriaDau.setProjection(Projections.property("DAU." + AinDiariasAutorizadas.Fields.INT_SEQ.toString()));
		subCriteriaDau.add(Restrictions.eqProperty("INT2." + AinInternacao.Fields.SEQ.toString(), 
				"DAU." + AinDiariasAutorizadas.Fields.INT_SEQ.toString()));
		subCriteriaDau.add(Restrictions.isNotNull("DAU." + AinDiariasAutorizadas.Fields.SENHA.toString()));
		
		criteria.add(Subqueries.exists(subCriteriaAtd));
		criteria.add(Subqueries.exists(subCriteriaDau));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		
		return executeCriteria(criteria);
	}
	
	public String obterNomePacienteCirurgia(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		criteria.setProjection(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		return (String) executeCriteriaUniqueResult(criteria);
	}

	public List<MonitorCirurgiaSalaRecuperacaoVO> pesquisarMonitorCirurgiaSalaRecuperacao(final Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		ProjectionList projecoesCriteria = Projections.projectionList();
		projecoesCriteria.add(Projections.property("CRG." +	MbcCirurgias.Fields.SEQ.toString()), "crgSeq");
		projecoesCriteria.add(Projections.property("CRG." +	MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString()), "entradaSalaRecuperacao");
		criteria.setProjection(projecoesCriteria);
		Date hoje = DateUtil.truncaData(new Date());
		Date noveDiasAntes = DateUtil.adicionaDias(hoje, -9);
		criteria.add(Restrictions.between("CRG." + MbcCirurgias.Fields.DATA.toString(), noveDiasAntes, hoje));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.isNotNull("CRG." + MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString()));
		criteria.add(Restrictions.isNull("CRG." + MbcCirurgias.Fields.DTHR_SAIDA_SR.toString()));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "SLC");
		subCriteria.setProjection(Projections.property("SLC." + MbcSalaCirurgica.Fields.SEQP.toString()));
		subCriteria.add(Restrictions.eqProperty("CRG." + MbcCirurgias.Fields.SCI_SEQP.toString(), "SLC." + MbcSalaCirurgica.Fields.SEQP.toString()));
		subCriteria.add(Restrictions.eq("SLC." + MbcSalaCirurgica.Fields.VISIVEL_MONITOR.toString(), Boolean.TRUE));
		criteria.add(Subqueries.exists(subCriteria));

		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(MonitorCirurgiaSalaRecuperacaoVO.class));
		return executeCriteria(criteria);
	}

	public List<MonitorCirurgiaConcluidaHojeVO> pesquisarMonitorCirurgiaConcluidaHoje(final Short unfSeq, Byte... destinosPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.DESTINO_PACIENTE.toString(), "DPA");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		
		ProjectionList projecoesCriteria = Projections.projectionList();
		projecoesCriteria.add(Projections.property("CRG." +	MbcCirurgias.Fields.SEQ.toString()), "crgSeq");
		projecoesCriteria.add(Projections.property("DPA." +	MbcDestinoPaciente.Fields.DESCRICAO.toString()), "destino");
		projecoesCriteria.add(Projections.property("PAC." +	AipPacientes.Fields.CODIGO.toString()), "codigoPaciente");
		criteria.setProjection(projecoesCriteria);

		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(new Date())));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "SLC");
		subCriteria.setProjection(Projections.property("SLC." + MbcSalaCirurgica.Fields.SEQP.toString()));
		subCriteria.add(Restrictions.eqProperty("CRG." + MbcCirurgias.Fields.SCI_SEQP.toString(), "SLC." + MbcSalaCirurgica.Fields.SEQP.toString()));
		subCriteria.add(Restrictions.eq("SLC." + MbcSalaCirurgica.Fields.VISIVEL_MONITOR.toString(), Boolean.TRUE));
		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		criteria.add(Restrictions.not(Restrictions.in("DPA." + MbcDestinoPaciente.Fields.SEQ.toString(), destinosPaciente)));
		
		criteria.addOrder(Order.asc("PAC." + AipPacientes.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MonitorCirurgiaConcluidaHojeVO.class));
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> pesquisarCirurgiasPorPacienteDataCrg(Integer crgSeq, Integer pacCodigo, Date dataCrg) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		criteria.add(Restrictions.sqlRestriction("TO_CHAR(this_.DATA,'dd/MM/yyyy') = ?", formatador.format(dataCrg), StringType.INSTANCE));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<CirurgiasExposicaoRadiacaoIonizanteVO> criteriaUnion1CirurgiasExposicaoRadiacaoIonizante(
			final Date dataInicial, final Date dataFinal,
			final List<Short> unidadesFuncionais, final List<Short> equipamentos) {
		
		final String ponto	  = ".";
		final String aliasCRG = "crg";	// MBC_CIRURGIAS 			 	CRG
		final String aliasPCC = "ppc";	// MBC_PROC_ESP_POR_CIRURGIAS	PPC
		final String aliasPCI = "pci";	// MBC_PROCEDIMENTO_CIRURGICOS	PCI
		final String aliasESP = "esp"; 	// AGH_ESPECIALIDADES			ESP
		final String aliasPAC = "pac";	// AIP_PACIENTES 				PACa
		final String aliasSCI = "sci";	// MBC_SALA_CIRURGICAS 			SCI
		final String aliasEQC = "eqc";	// MBC_EQUIPAMENTO_UTIL_CIRGS 	EQC
		final String aliasMDC = "mdc";	// MBC_DESCRICAO_CIRURGICAS		MDC
		final String aliasSER = "ser";	// RAP_SERVIDORES 				SER
		final String aliasPES = "pes";  // RAP_PESSOAS_FISICAS			PES
		
		final String aliasPFD = "pfd";	// MBC_PROF_DESCRICOES 			PFD
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCRG);
		Projection projection = Projections.projectionList()
		//SUBSTR(TO_CHAR(crg.data,'dd/mm/yyyy'),1,10) data
		.add(Projections.sqlProjection("TO_CHAR({alias}" + ponto + MbcCirurgias.Fields.DATA.toString() + ", 'DD/MM/YYYY') as " + CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString(),  
				new String[] { CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString() }, new Type[] { StringType.INSTANCE }), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString())		
		//SUBSTR(TO_CHAR(crg.DTHR_INICIO_CIRG,'hh24:mi'),1,5) hora_inicio_cirg
		.add(Projections.property(aliasCRG + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DTHR_INICIO_CIRG.toString())
		//SUBSTR(SCI.NOME,1,60) SALA
		.add(Projections.property(aliasSCI + ponto + MbcSalaCirurgica.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.SCI_NOME.toString())
		//SUBSTR(pac.prontuario,1,8) prontuario
		.add(Projections.property(aliasPAC + ponto + AipPacientes.Fields.PRONTUARIO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.PAC_PRONTUARIO.toString())
		//SUBSTR(esp.sigla,1,3) esp
		.add(Projections.property(aliasESP + ponto + AghEspecialidades.Fields.SIGLA.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.ESP_SIGLA.toString())
		//SUBSTR(ESP.NOME_ESPECIALIDADE,1,50) ESPECIALIDADE
		.add(Projections.property(aliasESP + ponto + AghEspecialidades.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.ESP_NOME.toString())
		//SUBSTR(pfd.tipo_atuacao,1,3) atuacao
		.add(Projections.property(aliasPFD + ponto + MbcProfDescricoes.Fields.TIPO_ATUACAO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.TIPO_ATUACAO.toString())
		//SUBSTR(pfd.ser_matricula_prof,1,7) matricula
		.add(Projections.property(aliasPFD + ponto + MbcProfDescricoes.Fields.SER_MATRICULA_PROF.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.MATRICULA.toString())
		//SUBSTR(pfd.ser_vin_codigo_prof,1,3) vinculo
		.add(Projections.property(aliasPFD + ponto + MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.VINCODIGO.toString())
		//SUBSTR(RAPC_BUSCA_NOME(pfd.ser_vin_codigo_prof,pfd.ser_matricula_prof),1,50) NOME_PROF,
		.add(Projections.property(aliasPES + ponto + RapPessoasFisicas.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.NOME_PROF.toString())
		//SUBSTR(pci.descricao,1,30) procedimento
		.add(Projections.property(aliasPCI + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.PROCEDIMENTO.toString())
		//SUBSTR(DECODE(eqc.euu_seq,109,'Fluroscopia',7,'Intensificador Imagem',105,'Gama Probi', 111,'Intensificador Imagem(fluroscopia)'),1,30)equipamento
		.add(Projections.property(aliasEQC + ponto + MbcEquipamentoUtilCirg.Fields.EUU_SEQ.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.EQUIPAMENTO.toString());
		
		// AND ppc.crg_seq 				= crg.seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPCC);
		// AND pci.seq 					= ppc.epr_pci_seq
		criteria.createAlias(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), aliasPCI);
		// AND esp.seq					= crg.esp_seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasESP);
		// AND pac.codigo				= crg.pac_codigo
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPAC);
		// AND crg.seq					= pfd.dcg_crg_seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.DESCRICOES_CIRURGIAS.toString(), aliasMDC);
		criteria.createAlias(aliasMDC + ponto + MbcDescricaoCirurgica.Fields.MBC_PROF_DESCRICOES.toString(), aliasPFD);
		// AND SCI.UNF_SEQ				= CRG.SCI_UNF_SEQ
		// AND SCI.SEQP					= CRG.SCI_SEQP
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), aliasSCI);
		// AND eqc.crg_seq				= crg.seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.EQUIPAMENTOS_UTIL_CIRGS.toString(), aliasEQC);
		// AND pfd.ser_matricula_prof	= ser.matricula
		// AND pfd.ser_vin_codigo_prof	= ser.vin_codigo
		criteria.createAlias(aliasPFD + ponto + MbcProfDescricoes.Fields.SERVIDOR_PROF.toString(), aliasSER);
		// AND ser.pes_codigo			= pes.codigo
		criteria.createAlias(aliasSER + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPES);
		
		// AND crg.data BETWEEN p_data_ini AND p_data_final
		criteria.add(Restrictions.between(aliasCRG + ponto + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		// AND ppc.ind_resp_proc 		= 'NOTA'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		// AND ppc.situacao				= 'A'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		// AND ppc.ind_principal                                     = 'S'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), true));
		// AND DECODE(crg.unf_seq,126,'S',131,'S',130,'S','N')       = 'S'
		criteria.add(Restrictions.in(aliasCRG + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unidadesFuncionais));
		// AND DECODE(eqc.euu_seq,109,'S',7,'S',111,'S',105,'S','N') = 'S'
		criteria.add(Restrictions.in(aliasEQC + ponto + MbcEquipamentoUtilCirg.Fields.EUU_SEQ.toString(), equipamentos));
		
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasExposicaoRadiacaoIonizanteVO.class));
		return executeCriteria(criteria);
	}
	
	public List<CirurgiasExposicaoRadiacaoIonizanteVO> criteriaUnion2CirurgiasExposicaoRadiacaoIonizante(
			final Date dataInicial, final Date dataFinal,
			final List<Short> unidadesFuncionais, final List<Short> equipamentos) {
		
		final String ponto	  = ".";
		final String aliasCRG = "crg";	// MBC_CIRURGIAS 			 	CRG
		final String aliasPCC = "ppc";	// MBC_PROC_ESP_POR_CIRURGIAS	PPC
		final String aliasPCI = "pci";	// MBC_PROCEDIMENTO_CIRURGICOS	PCI
		final String aliasESP = "esp"; 	// AGH_ESPECIALIDADES			ESP
		final String aliasPAC = "pac";	// AIP_PACIENTES 				PAC
		final String aliasSCI = "sci";	// MBC_SALA_CIRURGICAS 			SCI
		final String aliasEQC = "eqc";	// MBC_EQUIPAMENTO_UTIL_CIRGS 	EQC
		final String aliasSER = "ser";	// RAP_SERVIDORES 				SER
		final String aliasPES = "pes";  // RAP_PESSOAS_FISICAS			PES
		
		final String aliasDDT = "ddt";	// PDT_DESCRICOES 				DDT
		final String aliasDPF = "dpf";	// PDT_PROFS 					DPF
	
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCRG);
		Projection projection = Projections.projectionList()
		//SUBSTR(TO_CHAR(crg.data,'dd/mm/yyyy'),1,10) data
		.add(Projections.sqlProjection("TO_CHAR({alias}" + ponto + MbcCirurgias.Fields.DATA.toString() + ", 'DD/MM/YYYY') as " + CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString(),  
				new String[] { CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString() }, new Type[] { StringType.INSTANCE }), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString())	
		//SUBSTR(TO_CHAR(crg.DTHR_INICIO_CIRG,'hh24:mi'),1,5) hora_inicio_cirg
		.add(Projections.property(aliasCRG + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DTHR_INICIO_CIRG.toString())
		//SUBSTR(SCI.NOME,1,60) SALA
		.add(Projections.property(aliasSCI + ponto + MbcSalaCirurgica.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.SCI_NOME.toString())
		//SUBSTR(pac.prontuario,1,8) prontuario
		.add(Projections.property(aliasPAC + ponto + AipPacientes.Fields.PRONTUARIO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.PAC_PRONTUARIO.toString())
		//SUBSTR(esp.sigla,1,3) esp
		.add(Projections.property(aliasESP + ponto + AghEspecialidades.Fields.SIGLA.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.ESP_SIGLA.toString())
		//SUBSTR(ESP.NOME_ESPECIALIDADE,1,50) ESPECIALIDADE
		.add(Projections.property(aliasESP + ponto + AghEspecialidades.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.ESP_NOME.toString())
		//SUBSTR(dpf.tipo_atuacao,1,3) atuacao
		.add(Projections.property(aliasDPF + ponto + PdtProf.Fields.TIPO_ATUACAO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.TIPO_ATUACAO.toString())
		//SUBSTR(dpf.ser_matricula_prf,1,7) matricula
		.add(Projections.property(aliasDPF + ponto + PdtProf.Fields.SERVIDOR_PRF_MATRICULA.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.MATRICULA.toString())
		//SUBSTR(dpf.ser_vin_codigo_prf,1,3) vinculo
		.add(Projections.property(aliasDPF + ponto + PdtProf.Fields.SERVIDOR_PRF_VIN_CODIGO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.VINCODIGO.toString())
		//SUBSTR(RAPC_BUSCA_NOME(DPF.ser_vin_codigo_prf,DPF.ser_matricula_prf),1,50) NOME_PROF,
		.add(Projections.property(aliasPES + ponto + RapPessoasFisicas.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.NOME_PROF.toString())
		//SUBSTR(pci.descricao,1,30) procedimento
		.add(Projections.property(aliasPCI + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.PROCEDIMENTO.toString())
		//SUBSTR(DECODE(eqc.euu_seq,109,'Fluroscopia',7,'Intensificador Imagem',105,'Gama Probi', 111,'Intensificador Imagem(fluroscopia)'),1,30)equipamento
		.add(Projections.property(aliasEQC + ponto + MbcEquipamentoUtilCirg.Fields.EUU_SEQ.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.EQUIPAMENTO.toString());
		
		// AND ppc.crg_seq 		= crg.seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPCC);
		// AND pci.seq 			= ppc.epr_pci_seq
		criteria.createAlias(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), aliasPCI);
		// AND esp.seq			= crg.esp_seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasESP);
		// AND pac.codigo		= crg.pac_codigo
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPAC);
		// AND SCI.UNF_SEQ		= CRG.SCI_UNF_SEQ
		// AND SCI.SEQP			= CRG.SCI_SEQP
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), aliasSCI);
		// AND eqc.crg_seq		= crg.seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.EQUIPAMENTOS_UTIL_CIRGS.toString(), aliasEQC);
		// AND ser.pes_codigo	= pes.codigo
		criteria.createAlias(aliasSER + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPES);

		// AND crg.seq          = ddt.crg_seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PDT_DESCRICAO.toString(), aliasDDT);
		// AND ddt.seq          		= dpf.ddt_seq
		criteria.createAlias(aliasDDT + ponto + PdtDescricao.Fields.PDT_PROFS.toString(), aliasDPF);
		// AND dpf.ser_matricula_prf	= ser.matricula
		// AND dpf.ser_vin_codigo_prf	= ser.vin_codigo
		criteria.createAlias(aliasDPF + ponto + PdtProf.Fields.SERVIDOR_PRF.toString(), aliasSER);

		// AND crg.data BETWEEN p_data_ini AND p_data_final
		criteria.add(Restrictions.between(aliasCRG + ponto + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		// AND ppc.ind_resp_proc 	= 'NOTA'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		// AND ppc.situacao			= 'A'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		// AND ppc.ind_principal                                     = 'S'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), true));
		// AND DECODE(crg.unf_seq,126,'S',131,'S',130,'S','N')       = 'S'
		criteria.add(Restrictions.in(aliasCRG + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unidadesFuncionais));
		// AND DECODE(eqc.euu_seq,109,'S',7,'S',111,'S',105,'S','N') = 'S'
		criteria.add(Restrictions.in(aliasEQC + ponto + MbcEquipamentoUtilCirg.Fields.EUU_SEQ.toString(), equipamentos));

		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasExposicaoRadiacaoIonizanteVO.class));
		return executeCriteria(criteria);
	}
	
	public List<CirurgiasExposicaoRadiacaoIonizanteVO> criteriaUnion3CirurgiasExposicaoRadiacaoIonizante(
			final Date dataInicial, final Date dataFinal,
			final List<Short> unidadesFuncionais, final List<Short> equipamentos) {

		final String ponto	  = ".";
		final String aliasCRG = "crg";	// MBC_CIRURGIAS 			 	CRG
		final String aliasPCC = "ppc";	// MBC_PROC_ESP_POR_CIRURGIAS	PPC
		final String aliasPCI = "pci";	// MBC_PROCEDIMENTO_CIRURGICOS	PCI
		final String aliasESP = "esp"; 	// AGH_ESPECIALIDADES			ESP
		final String aliasPAC = "pac";	// AIP_PACIENTES 				PAC
		final String aliasSCI = "sci";	// MBC_SALA_CIRURGICAS 			SCI
		final String aliasEQC = "eqc";	// MBC_EQUIPAMENTO_UTIL_CIRGS 	EQC
		final String aliasSER = "ser";	// RAP_SERVIDORES 				SER
		final String aliasPES = "pes";  // RAP_PESSOAS_FISICAS			PES
		
		final String aliasPCG = "pcg";  // MBC_PROF_CIRURGIAS 			PCG
		final String aliasEPC = "epc";  // MBC_ESPECIALIDADE_PROC_CIRGS EPC
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCRG);
		Projection projection = Projections.projectionList()
		//SUBSTR(TO_CHAR(crg.data,'dd/mm/yyyy'),1,10) data
		.add(Projections.sqlProjection("TO_CHAR({alias}" + ponto + MbcCirurgias.Fields.DATA.toString() + ", 'DD/MM/YYYY') as " + CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString(),  
				new String[] { CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString() }, new Type[] { StringType.INSTANCE }), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DATA.toString())	
		//SUBSTR(TO_CHAR(crg.DTHR_INICIO_CIRG,'hh24:mi'),1,5) hora_inicio_cirg
		.add(Projections.property(aliasCRG + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.CRG_DTHR_INICIO_CIRG.toString())
		//SUBSTR(SCI.NOME,1,60) SALA
		.add(Projections.property(aliasSCI + ponto + MbcSalaCirurgica.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.SCI_NOME.toString())
		//SUBSTR(pac.prontuario,1,8) prontuario
		.add(Projections.property(aliasPAC + ponto + AipPacientes.Fields.PRONTUARIO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.PAC_PRONTUARIO.toString())
		//SUBSTR(esp.sigla,1,3) esp
		.add(Projections.property(aliasESP + ponto + AghEspecialidades.Fields.SIGLA.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.ESP_SIGLA.toString())
		//SUBSTR(ESP.NOME_ESPECIALIDADE,1,50) ESPECIALIDADE
		.add(Projections.property(aliasESP + ponto + AghEspecialidades.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.ESP_NOME.toString())
		//SUBSTR(pcg.puc_ind_funcao_prof,1,3) atuacao
		.add(Projections.property(aliasPCG + ponto + MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.PCG_FUNCAO_PROFISSIONAL.toString())
		//SUBSTR(pcg.puc_ser_matricula,1,7) matricula
		.add(Projections.property(aliasPCG + ponto + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.MATRICULA.toString())
		//SUBSTR(pcg.puc_ser_vin_codigo,1,3) vinculo,
		.add(Projections.property(aliasPCG + ponto + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.VINCODIGO.toString())
		//SUBSTR(RAPC_BUSCA_NOME(pcg.puc_ser_vin_codigo,pcg.puc_ser_matricula),1,50) NOME_PROF
		.add(Projections.property(aliasPES + ponto + RapPessoasFisicas.Fields.NOME.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.NOME_PROF.toString())
		//SUBSTR(pci.descricao,1,30) procedimento
		.add(Projections.property(aliasPCI + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.PROCEDIMENTO.toString())
		//SUBSTR(DECODE(eqc.euu_seq,109,'Fluroscopia',7,'Intensificador Imagem',105,'Gama Probi', 111,'Intensificador Imagem(fluroscopia)'),1,30)equipamento
		.add(Projections.property(aliasEQC + ponto + MbcEquipamentoUtilCirg.Fields.EUU_SEQ.toString()), CirurgiasExposicaoRadiacaoIonizanteVO.Fields.EQUIPAMENTO.toString());

		// AND ppc.crg_seq 		= crg.seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPCC);
		// AND pci.seq 			= ppc.epr_pci_seq
		criteria.createAlias(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), aliasPCI);
		
		// AND esp.seq          = ppc.epr_esp_seq
		criteria.createAlias(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), aliasEPC);
		criteria.createAlias(aliasEPC + ponto + MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), aliasESP);
		
		// AND pac.codigo		= crg.pac_codigo
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPAC);
		// AND SCI.UNF_SEQ		= CRG.SCI_UNF_SEQ
		// AND SCI.SEQP			= CRG.SCI_SEQP
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), aliasSCI);
		// AND eqc.crg_seq		= crg.seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.EQUIPAMENTOS_UTIL_CIRGS.toString(), aliasEQC);
		// AND ser.pes_codigo	= pes.codigo
		criteria.createAlias(aliasSER + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPES);
		// AND pcg.crg_seq				= crg.seq
		criteria.createAlias(aliasCRG + ponto + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), aliasPCG);
		// AND pcg.puc_ser_matricula	= ser.matricula
		// AND pcg.puc_ser_vin_codigo	= ser.vin_codigo
		criteria.createAlias(aliasPCG + ponto + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), aliasSER);

		// AND crg.data BETWEEN p_data_ini AND p_data_final
		criteria.add(Restrictions.between(aliasCRG + ponto + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		// AND ppc.ind_resp_proc 									 = 'NOTA'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		// AND ppc.situacao											 = 'A'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		// AND ppc.ind_principal                                     = 'S'
		criteria.add(Restrictions.eq(aliasPCC + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), true));
		// AND DECODE(crg.unf_seq,126,'S',131,'S',130,'S','N')       = 'S'
		criteria.add(Restrictions.in(aliasCRG + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unidadesFuncionais));
		// AND DECODE(eqc.euu_seq,109,'S',7,'S',111,'S',105,'S','N') = 'S'
		criteria.add(Restrictions.in(aliasEQC + ponto + MbcEquipamentoUtilCirg.Fields.EUU_SEQ.toString(), equipamentos));
		//AND crg.situacao                                           = 'RZDA'
		criteria.add(Restrictions.eq(aliasCRG + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		//AND SUBSTR(crg.ind_digt_nota_sala,1,1)                     = 'S'
		criteria.add(Restrictions.eq(aliasCRG + ponto + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), true));
		

		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiasExposicaoRadiacaoIonizanteVO.class));
		return executeCriteria(criteria);
		
	}
	
	public List<RelatorioProcedAgendPorUnidCirurgicaVO> pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(
			Short unfSeq, Integer pciSeq, Date dtInicio, Date dtFim) {
		
		String aliasCrg = "crg";
		String aliasPpc = "ppc";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);
		
		Projection projection = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioProcedAgendPorUnidCirurgicaVO.Fields.DATA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString()), RelatorioProcedAgendPorUnidCirurgicaVO.Fields.SCI_SEQP.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), RelatorioProcedAgendPorUnidCirurgicaVO.Fields.DTHR_INICIO.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()), RelatorioProcedAgendPorUnidCirurgicaVO.Fields.DTHR_FIM.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), RelatorioProcedAgendPorUnidCirurgicaVO.Fields.CRG_SEQ.toString());
		
		criteria.setProjection(projection);
		
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPpc);
		
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.AGND));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.ge(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dtInicio));
		criteria.add(Restrictions.le(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dtFim));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));

		
		if (pciSeq != null) {
			criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ, pciSeq));
		}
		
		if (isOracle()) {
			criteria.addOrder(OrderBySql.sql("TRUNC(this_" + ponto + MbcCirurgias.Fields.DATA.name() + ") asc"));
		} else {
			criteria.addOrder(OrderBySql.sql("DATE_TRUNC('day', this_" + ponto + MbcCirurgias.Fields.DATA.name() + ") asc"));
		}
		
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString()));
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioProcedAgendPorUnidCirurgicaVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RelatorioCirurgiasPendenteRetornoVO> pesquisarCirurgiaNaoCanceladaPorUnfSeqDtInicioDtFim(
			Short unfSeq, Date dtInicio, Date dtFim) {
		
		String aliasCrg = "crg";
		String aliasEsp = "esp";
		String aliasPcg = "pcg";
		String aliasPac = "pac";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);
		
		Projection projection = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.DATA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.NUMERO_AGENDA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.NUMERO_AGENDA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SEQ.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.ORIGEM_PAC_CIRG.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.NOME.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.ESP_SIGLA.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PES_NOME.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PES_NOME_USUAL.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SERVIDOR_MATRICULA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SER_MATRICULA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SERVIDOR_VIN_CODIGO.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SER_VIN_CODIGO.toString());
		
		criteria.setProjection(projection);
		
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasEsp);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), aliasPcg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		criteria.createAlias(aliasPcg + ponto + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dtInicio, dtFim));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasPcg + ponto + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));

		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioCirurgiasPendenteRetornoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RelatorioCirurgiasPendenteRetornoVO> pesquisarCirurgiaNaoCanceladaProcedPrincipalPorUnfSeqDtInicioDtFimEPciSeq(
			Short unfSeq, Date dtInicio, Date dtFim, Integer pciSeq) {
		
		String aliasCrg = "crg";
		String aliasEsp = "esp";
		String aliasPcg = "pcg";
		String aliasPac = "pac";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String aliasPpc = "ppc";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);
		
		Projection projection = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.DATA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.NUMERO_AGENDA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.NUMERO_AGENDA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SEQ.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.ORIGEM_PAC_CIRG.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.NOME.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.ESP_SIGLA.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PES_NOME.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PES_NOME_USUAL.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SERVIDOR_MATRICULA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SER_MATRICULA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SERVIDOR_VIN_CODIGO.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SER_VIN_CODIGO.toString());
		
		criteria.setProjection(projection);
		
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasEsp);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), aliasPcg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPpc);
		criteria.createAlias(aliasPcg + ponto + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dtInicio, dtFim));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasPcg + ponto + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		
		if (pciSeq != null) {
			criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString(), pciSeq));
		}
		
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));

		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioCirurgiasPendenteRetornoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RelatorioCirurgiasPendenteRetornoVO> pesquisarCirurgiaNaoCanceladaDigitaNotaSalaPorUnfSeqDtInicioDtFim(
			Short unfSeq, Date dtInicio, Date dtFim, Integer gmtCodigoOrteseProtese) {
		
		String aliasCrg = "crg";
		String aliasEsp = "esp";
		String aliasPcg = "pcg";
		String aliasPac = "pac";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String aliasPpc = "ppc";
		String aliasCrg2 = "crg2";
		String aliasMci = "mci";
		String aliasMat = "mat";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);
		
		Projection projection = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.DATA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.NUMERO_AGENDA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.NUMERO_AGENDA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SEQ.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.ORIGEM_PAC_CIRG.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.NOME.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.ESP_SIGLA.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PES_NOME.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.PES_NOME_USUAL.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SERVIDOR_MATRICULA.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SER_MATRICULA.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SERVIDOR_VIN_CODIGO.toString()), RelatorioCirurgiasPendenteRetornoVO.Fields.CRG_SER_VIN_CODIGO.toString());
		
		criteria.setProjection(projection);
		
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasEsp);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), aliasPcg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPpc);
		criteria.createAlias(aliasPcg + ponto + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		
		// Cirurgias realizadas
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dtInicio, dtFim));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(aliasPcg + ponto + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		
		// Subquery para fazer o MINUS (cirurgias realizadas com materiais lançados na nota de sala)  
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg2);
		subCriteria.setProjection(Projections.property(aliasCrg2 + ponto + MbcCirurgias.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.between(aliasCrg2 + ponto + MbcCirurgias.Fields.DATA.toString(), dtInicio, dtFim));
		subCriteria.add(Restrictions.eq(aliasCrg2 + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		subCriteria.add(Restrictions.eq(aliasCrg2 + ponto + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.TRUE));
		subCriteria.add(Restrictions.isNotNull(aliasCrg2 + ponto + MbcCirurgias.Fields.CENTRO_CUSTO.toString()));
		subCriteria.add(Restrictions.eq(aliasCrg2 + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		subCriteria.add(Restrictions.eqProperty(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), 
				aliasCrg2 + ponto + MbcCirurgias.Fields.DATA.toString()));
		subCriteria.add(Restrictions.eqProperty(aliasCrg + ponto + MbcCirurgias.Fields.NUMERO_AGENDA.toString(), 
				aliasCrg2 + ponto + MbcCirurgias.Fields.NUMERO_AGENDA.toString()));
		
		// Subquery para buscar os materiais da cirurgia
		DetachedCriteria subCriteriaMat = DetachedCriteria.forClass(MbcMaterialPorCirurgia.class, aliasMci);
		subCriteriaMat.setProjection(Projections.property(aliasMci + ponto + MbcMaterialPorCirurgia.Fields.MBC_CIRURGIAS_SEQ.toString()));
		subCriteriaMat.createAlias(aliasMci + ponto + MbcMaterialPorCirurgia.Fields.SCO_MATERIAL.toString(), aliasMat);
		subCriteriaMat.add(Restrictions.eqProperty(aliasCrg2 + ponto + MbcCirurgias.Fields.SEQ.toString(), 
				aliasMci + ponto + MbcMaterialPorCirurgia.Fields.CRG_SEQ.toString()));
		subCriteriaMat.add(Restrictions.ne(aliasMat + ponto + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), gmtCodigoOrteseProtese));
		
		if (isOracle()) {
			subCriteriaMat.add(Restrictions.sqlRestriction("ROWNUM = 1"));
		} else {
			// Uso de OrderBySql para concatenar o "LIMIT 1" no final da query
			subCriteriaMat.addOrder(OrderBySql.sql("mci_" + ponto + MbcMaterialPorCirurgia.Fields.CRG_SEQ.name() + " asc LIMIT 1"));
		}
		
		subCriteria.add(Subqueries.exists(subCriteriaMat));
		criteria.add(Subqueries.notExists(subCriteria));
		
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()));
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.NUMERO_AGENDA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioCirurgiasPendenteRetornoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> pesquisarRetornoCirurgiasEmLote(
			Short sciUnfSeq /*sujestion de unidade funcional*/, Date dataCirurgia, Short sciSeqp /*sujestion de unidade sala*/,
			Integer prontuario) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.PACIENTE.toString(), "PACIENTE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CRG." + MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "CONVENIO");
		if(dataCirurgia != null){
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.DATA.toString(), DateUtil.obterDataComHoraInical(dataCirurgia)));
		} else {
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.DATA.toString(), DateUtil.obterDataComHoraInical(new Date())));
		}
		
		if(sciUnfSeq != null){
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SCI_UNF_SEQ, sciUnfSeq));
		}	
		
		//SUBQUERY (IN) 
		DetachedCriteria subQueryIN = DetachedCriteria.forClass(AipPacientes.class, "PAC");

		if(prontuario != null && prontuario != 0){
			subQueryIN.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}	
			
		subQueryIN.setProjection(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()));
		subQueryIN.add(Property.forName("PAC." + AipPacientes.Fields.CODIGO.toString()).eqProperty("CRG." + MbcCirurgias.Fields.PAC_CODIGO.toString()));
			
		criteria.add(Subqueries.exists(subQueryIN));
		//SUBQUERY (IN) [FIM]
		
		if(sciSeqp != null){
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SCI_SEQP.toString(), sciSeqp));
		} 
		
		criteria.add(Restrictions.ne("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.FALSE));
		
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.SCI_SEQP.toString()));
		
		return executeCriteria(criteria);
	}
	
	//24951
	public List<MbcCirurgias> listarCirurgiasPacientesEmSR(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr, Date dataAtual, Date dataAnterior) {
		DetachedCriteria criteria = getCriteriaListarPacientesEmSR(unidadeFuncional, dataEntradaSr, dataAtual, dataAnterior);
		if(StringUtils.isNotBlank(orderProperty)) {
			if(asc) {
				criteria.addOrder(Order.asc(orderProperty));
			} else {
				criteria.addOrder(Order.desc(orderProperty));
			}
		}
		return executeCriteria(criteria, firstResult, maxResult, null);
	}

	public Long listarCirurgiasPacientesEmSRCount(AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr, Date dataAtual, Date dataAnterior) {
		DetachedCriteria criteria = getCriteriaListarPacientesEmSR(unidadeFuncional, dataEntradaSr, dataAtual, dataAnterior);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria getCriteriaListarPacientesEmSR(AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr, 
			Date dataAtual, Date dataAnterior) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		
		criteria.createAlias("CRG." + MbcCirurgias.Fields.PACIENTE.toString(), "AIP");
		
		if (unidadeFuncional != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		}
		
		if (dataEntradaSr != null) {
			criteria.add(Restrictions.between(MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString(), DateUtil.truncaData(dataEntradaSr), DateUtil.truncaDataFim(dataEntradaSr)));
		}
		
		criteria.add(Restrictions.between(MbcCirurgias.Fields.DATA.toString(), dataAnterior, dataAtual));
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString()));
		criteria.add(Restrictions.isNull(MbcCirurgias.Fields.DTHR_SAIDA_SR.toString()));
		
		return criteria;
	}
	// Fim 24951
	
	public List<MbcCirurgias> pesquisarCirurgiasEletivasParaAgenda(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.isNull(MbcCirurgias.Fields.MTC_SEQ.toString()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.NATUREZA_AGEND.toString(), DominioNaturezaFichaAnestesia.ELE));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.AGND));
		return executeCriteria(criteria);	
	}
	
	public List<MbcCirurgias> pesquisarCirurgiasDeReserva(Date dataAgendamento, Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dataAgendamento));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.isNull(MbcCirurgias.Fields.MTC_SEQ.toString()));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.AGND));
		return executeCriteria(criteria);	
	}
	
	public MbcCirurgias obterCirurgiaPorSeqInnerJoinAtendimento(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.ATENDIMENTO.toString(), "atendimento", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), seq));

		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<PacientesCirurgiaUnidadeVO> obterPacientesCirurgiaUnidade(Short unfSeq, 
				Date crgDataInicio, Date crgDataFim, Integer serMatricula, Short serVinCodigo) {
 		Date dataAtualTrunc = DateUtil.truncaData(new Date());
		StringBuffer hql = new StringBuffer(2058);
		hql.append("select")
			.append("	unf."+AghUnidadesFuncionais.Fields.SIGLA+" as unid, \n ")
		   .append("	case when pes."+RapPessoasFisicas.Fields.NOME_USUAL+" is not null \n ")
		   .append("		 then pes."+RapPessoasFisicas.Fields.NOME_USUAL+" \n ")
		   .append("		 else pes."+RapPessoasFisicas.Fields.NOME+" \n ")
		   .append("	end as equipe, \n ")
		   .append("	case when crg."+MbcCirurgias.Fields.SITUACAO+" = '" + DominioSituacaoCirurgia.RZDA.name() + "' \n ")
		   .append("		 then crg."+MbcCirurgias.Fields.DTHR_INICIO_CIRG+" \n ")
		   .append("		 else crg."+MbcCirurgias.Fields.DTHR_PREV_INICIO+" \n ")
		   .append("	end as dataini, \n ")
		   .append("	case when crg."+MbcCirurgias.Fields.SITUACAO+" = '" + DominioSituacaoCirurgia.RZDA.name() + "' \n ")
		   .append("		then crg."+MbcCirurgias.Fields.DTHR_FIM_CIRG+" \n ")
		   .append("		else crg."+MbcCirurgias.Fields.DTHR_PREVISAO_FIM+" \n ")
		   .append("	end as datafim, \n ")
		   .append("	crg."+MbcCirurgias.Fields.SITUACAO+" as sit, \n ")
		   .append("	pac."+AipPacientes.Fields.PRONTUARIO+" as prnt, \n ")
		   .append("	pac."+AipPacientes.Fields.NOME+" as nome, \n ")
		   .append("	cnv."+FatConvenioSaude.Fields.DESCRICAO+" as cnvdesc, \n ")
		   .append("	pci."+MbcProcedimentoCirurgicos.Fields.DESCRICAO+" as proc, \n ")
		   .append("	crg."+MbcCirurgias.Fields.CRIADO_EM+" as dataagenda, \n ")
		   .append("	mtc."+MbcMotivoCancelamento.Fields.DESCRICAO+" as motivo, \n ")
		   .append("	crg."+MbcCirurgias.Fields.ORIGEM_PAC_CIRG+" as origem, \n ")
		   .append("	crg."+MbcCirurgias.Fields.NATUREZA_AGEND+" as natureza, \n ")
		   .append("	esp."+AghEspecialidades.Fields.SIGLA+" as esp, \n ")
		   .append("	(select distinct atd.dthrInicio \n ")
		   .append("	from \n ")
		   .append("		AghAtendimentos atd \n ")
		   .append("	where \n ")
		   .append("		atd.paciente.codigo = pac."+AipPacientes.Fields.CODIGO+" \n ")
		   .append("		and crg."+MbcCirurgias.Fields.DATA+" between atd.dthrInicio and \n ")
		   .append("	( \n ")
		   .append("		CASE WHEN atd.dthrFim IS NOT NULL \n")
		   .append("		THEN  atd.dthrFim \n")
		   .append("		ELSE  :dataAtualTrunc \n")
		   .append("		END \n")
		   .append("	) \n ")
		   .append("		and atd.origem in ( '")
		   	.append(DominioOrigemAtendimento.I.name())
		   .append("' , '")
		   .append(DominioOrigemAtendimento.U.name())
		   .append("' ) \n ")
		   .append("	) as dtatend \n ")
		   .append("From \n ")
		   .append("	"+FatConvenioSaude.class.getSimpleName() +" cnv, \n ")
		   .append("	MbcCirurgias crg \n ")
		   .append("	inner join crg.paciente pac \n ")
		   .append("	inner join crg.especialidade esp \n ")
		   .append("	inner join crg.profCirurgias pcg \n ")
		   .append("	inner join pcg.servidor ser \n ")
		   .append(" 	inner join ser.pessoaFisica pes \n ")
		   .append("	inner join crg.procEspPorCirurgias ppc \n ")
		   .append("	inner join ppc.procedimentoCirurgico pci \n ")
		   .append("	inner join crg.unidadeFuncional unf \n ")
		   .append("	left join crg.motivoCancelamento mtc \n ")
		   .append("where \n ")
		   .append("	 crg."+MbcCirurgias.Fields.CONVENIO_SAUDE+".codigo = cnv."+FatConvenioSaude.Fields.CODIGO+" \n ")
		   
			.append("	and ((crg."+MbcCirurgias.Fields.SITUACAO+" = '" 	+ DominioSituacaoCirurgia.RZDA.name() +"'   \n ")
			.append("	and ppc.id.indRespProc = '"	+ DominioIndRespProc.NOTA.name() 	  +"' ) \n ")
			.append("	or (  \n ")
			.append("	(crg."+MbcCirurgias.Fields.SITUACAO+" = 			'" 	+ DominioSituacaoCirurgia.AGND.name() 	+"'  \n ")
			.append("	or crg."+MbcCirurgias.Fields.SITUACAO+" = 			'"	+ DominioSituacaoCirurgia.CANC.name() 	+"') \n ")
			.append("	and ppc.id.indRespProc = 	'" 	+ DominioSituacaoCirurgia.AGND.name() 	+"')) \n ")
		   
		   .append("	and ppc."+MbcProcEspPorCirurgias.Fields.SITUACAO+" = 			'" 	+ DominioSituacao.A.name() 	+"' \n ")
		   .append("	and pcg."+MbcProfCirurgias.Fields.IND_RESPONSAVEL+" = 	'" 	+ "S" 	+"' \n ")
		   .append("	and crg."+MbcCirurgias.Fields.UNIDADE_FUNCIONAL+".seq = :unfSeq \n ") 
		   .append("	and crg."+MbcCirurgias.Fields.DATA+" between :crgDataInicio and :crgDataFim \n ");
		   
		   if(serMatricula != null && serVinCodigo != null){
			   hql.append("	and ser.id.matricula = :serMatricula \n ");
			   hql.append("	and ser.id.vinCodigo = :serVinCodigo \n ");
		   }
		   hql.append("order by \n ");
		   hql.append("	1,2,3,5,4,6,7,8,9,10,11,12,13,14,15 \n ");
		
		Query q = createHibernateQuery(hql.toString());

		q.setShort("unfSeq",unfSeq);
		q.setDate("crgDataInicio",crgDataInicio);
		q.setDate("crgDataFim",crgDataFim);
		q.setDate("dataAtualTrunc",dataAtualTrunc);
		
		
		if(serMatricula != null && serVinCodigo != null){
			q.setInteger("serMatricula",serMatricula);
			q.setShort("serVinCodigo",serVinCodigo);
		}
		
		q.setResultTransformer(Transformers.aliasToBean(PacientesCirurgiaUnidadeVO.class));
		return q.list();
 	}
 	
	public List<MbcCirurgias> buscarCirurgias(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		
		criteria.createAlias("CRG." + MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString(), "MTC", DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("CRG." + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "SCR", DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("SCR." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", DetachedCriteria.LEFT_JOIN);
		
		if(unidadeCirurgica != null){
			criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeCirurgica.getSeq()));
		}
		
		if(dataInicial != null && dataFinal != null){
			criteria.add(Restrictions.between("CRG." + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		}
		
		criteria.add(Restrictions.eqProperty("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), "CRG." + MbcCirurgias.Fields.UNF_SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> pesquisarColisaoHorariosOutraNSDigitada(final Short unfSeq, final Date dataCirurgia, final Integer crgSeq, final Short sciUnfSeq, final Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString(), Boolean.TRUE));
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), sciUnfSeq));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_ENTRADA_SALA.toString()));
		criteria.add(Restrictions.isNotNull(MbcCirurgias.Fields.DTHR_SAIDA_SALA.toString()));
		
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> buscarCirurgiasDetalhe(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		
		criteria.createAlias("CRG." + MbcCirurgias.Fields.ESPECIALIDADE.toString(), "ESP", DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("CRG." + MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString(), "MTC", DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("CRG." + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "SCR", DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("SCR." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", DetachedCriteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eqProperty("CRG." + MbcCirurgias.Fields.ESP_SEQ.toString(), "ESP." + AghEspecialidades.Fields.SEQ));
		criteria.add(Restrictions.eqProperty("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), "CRG." + MbcCirurgias.Fields.UNF_SEQ.toString()));
		
		if(unidadeCirurgica != null){
			criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeCirurgica.getSeq()));
		}
		
		if(dataInicial != null && dataFinal != null){
			criteria.add(Restrictions.between("CRG." + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		}
		
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.SIGLA.toString()));
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));

		return executeCriteria(criteria);
		
	}
	
	public List<MbcCirurgias> pesquisaCirurgiasPorCriterios(Date data, Boolean digitouNotaSala, Integer pacCodigo, Integer atendimentoSeq, DominioOrigemPacienteCirurgia origemPacienteCirurgia){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		
		if (data != null) {
			
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date)data.clone());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			criteria.add(Restrictions.ge(MbcCirurgias.Fields.DATA.toString(), data))
			.add(Restrictions.le(MbcCirurgias.Fields.DATA.toString(), cal.getTime()));
		}
		
		
		if (digitouNotaSala != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), digitouNotaSala));
		}
		
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO_CODIGO.toString(), pacCodigo));
		}
		
		if(atendimentoSeq != null){
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString(), atendimentoSeq));
		} else {
			criteria.add(Restrictions.isNull(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()));
		}
		
		if(origemPacienteCirurgia != null){
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString(), origemPacienteCirurgia));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgias> pesquisarCirurgiaPorCaractUnidFuncionais(Integer crgSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "cir");
		criteria.createAlias("cir."+MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias("unf."+AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "cuf");
		
		criteria.add(Restrictions.eq("cir."+MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("cuf."+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristica));
		
		return executeCriteria(criteria);
	}

	/**
	 * #31998
	 * @param Código da Cirurgia
	 * @return List<Object[]>
	 */
	public List<Object[]> pesquisarMateriaisConsumidos(Integer cirurgiaSeq) {
		
		StringBuilder sql = new StringBuilder(5000);
		
		sql.append(" SELECT iro.SEQ       SEQ_ITEM_REQUISICAO, "
				+ "         rop.SEQ       SEQ_REQUISICAO, "
				+ "         CASE iro.IND_REQUERIDO "
				+ "             when 'NOV' then '(Nova Solicitação de Material)' "
				+ "             when 'ADC' then '(Material Adicionado pelo Usuário)' "
				+ "             else       iph.COD_TABELA || ' - ' || iph.DESCRICAO "
				+ "         END ITEM_SUS, "
				+ "         COALESCE(mio.QTD_SOLC, iro.QTD_SOLC)     QTD_AUTORIZADA, "
				+ "         ( SELECT  COALESCE(SUM(idp2.QTDE_DISPENSA), 0) "
				+ "         FROM agh.MBC_CIRURGIAS                  crg2  "
				+ " 		JOIN agh.MBC_AGENDAS                    agd2  "
				+ "                 ON agd2.SEQ = crg2.AGD_SEQ            "
				+ "			JOIN agh.SCE_DISPENSA_MAT_OPS           dmo2  "
				+ "                 ON dmo2.CRG_SEQ = crg2.SEQ            "
				+ "			JOIN agh.SCE_ITEM_DISP_MAT_OPS          idp2  "
				+ "                 ON idp2.DMO_SEQ = dmo2.SEQ            "
				+ "			JOIN agh.SCE_ESTQ_ALMOXS                eal2  "
				+ "                 ON eal2.SEQ = idp2.EAL_SEQ            "
				+ "         JOIN agh.SCO_MATERIAIS                  mat2  "
				+ "                 ON mat2.CODIGO  = eal2.MAT_CODIGO     "
				+ "         WHERE crg2.SEQ    = crg.SEQ                   "
				+ "             AND    mat2.CODIGO = mat.CODIGO           "
				+ "             AND    dmo2.IND_SITUACAO = 'D'            "
				+ "             AND    idp2.IND_SITUACAO = 'D') QTD_DISPENSADA, "
				+ "         CASE iro.IND_REQUERIDO                        "
				+ "             when 'NOV' then case                      "
				+ "                 when COALESCE(mat.CODIGO, -1) = -1    "
				+ "					then COALESCE(iro.ESPEC_NOVO_MAT,     "
				+ "                 iro.SOLC_NOVO_MAT)                    "
				+ "                 else mat.CODIGO||' - '||mat.NOME      "
				+ "             end               "
				+ "             when 'ADC' then mat.CODIGO||' - '||mat.NOME "
				+ "             else            mat.CODIGO||' - '||mat.NOME "
				+ "         END MATERIAL,                                   "
				+ "         CASE iro.IND_REQUERIDO                          "
				+ "             WHEN 'NOV' then 1                           "
				+ "             WHEN 'ADC' then 2  "
				+ "             else 3             "
				+ "         END as reqOrder        "
				+ "     FROM  agh.MBC_CIRURGIAS                  crg  "
				+ "     JOIN agh.MBC_AGENDAS                    agd   "
				+ "             ON agd.SEQ     = crg.AGD_SEQ          "
				+ "     JOIN agh.MBC_REQUISICAO_OPMES           rop   "
				+ "             ON rop.AGD_SEQ = agd.SEQ              "
				+ "     JOIN agh.MBC_ITENS_REQUISICAO_OPMES     iro   "
				+ "             ON iro.ROP_SEQ = rop.SEQ              "
				+ "     LEFT JOIN agh.FAT_ITENS_PROCED_HOSPITALAR iph "
				+ "             ON iph.PHO_SEQ = iro.IPH_PHO_SEQ      "
				+ "				and iph.SEQ = iro.IPH_SEQ             "
				+ "     LEFT JOIN agh.MBC_MATERIAIS_ITEM_OPMES    mio "
				+ "             ON mio.IRO_SEQ = iro.SEQ  "
				+ "             and mio.QTD_SOLC > 0   "
				+ "     LEFT JOIN "
				+ "         agh.SCO_MATERIAIS               mat  "
				+ "             ON mat.CODIGO  = mio.MAT_CODIGO   "
				+ "     WHERE crg.SEQ = :cirurgiaSeq "
				+ "         AND    iro.IND_REQUERIDO  <> 'NRQ' "
				+ "         AND    iro.IND_AUTORIZADO = 'S' "
				+ "     UNION "
				+ "   SELECT NULL  SEQ_ITEM_REQUISICAO,"
				+ "			SEQ_REQUISICAO, "
				+ "         ITEM_SUS        , "
				+ "         QTD_AUTORIZADA        , "
				+ "         SUM(QTD_DISPENSADA_0) QTD_DISPENSADA        , "
				+ "         MATERIAL        , "
				+ "         reqOrder   "
				+ "     FROM  (  SELECT rop.SEQ SEQ_REQUISICAO        , "
				+ "             COALESCE(null, '(Materia Dispensado na Cirurgia)') ITEM_SUS, "
				+ "             0 QTD_AUTORIZADA, "
				+ "             COALESCE(idp.QTDE_DISPENSA, "
				+ "             0)                     QTD_DISPENSADA_0        , "
				+ "             mat.CODIGO || ' - ' || mat.NOME                   MATERIAL        , "
				+ "             4                                                 reqOrder   "
				+ "         FROM agh.MBC_CIRURGIAS         crg   "
				+ "         JOIN agh.SCE_DISPENSA_MAT_OPS  dmo  "
				+ "                 ON crg.SEQ = dmo.CRG_SEQ   "
				+ "         JOIN agh.SCE_ITEM_DISP_MAT_OPS idp  "
				+ "                 ON dmo.SEQ = idp.DMO_SEQ  "
				+ "         JOIN agh.SCE_ESTQ_ALMOXS       eal  "
				+ "                 ON eal.SEQ = idp.EAL_SEQ   "
				+ "         JOIN agh.SCO_MATERIAIS         mat  "
				+ "                 ON mat.CODIGO = eal.MAT_CODIGO   "
				+ "         JOIN agh.MBC_AGENDAS           agd  "
				+ "                 ON crg.AGD_SEQ = agd.SEQ   "
				+ "         JOIN agh.MBC_REQUISICAO_OPMES  rop  "
				+ "                 ON rop.AGD_SEQ = agd.SEQ   "
				+ "         WHERE crg.SEQ = :cirurgiaSeq    "
				+ "             AND    mat.CODIGO not in ( "
				+ "                 select  mio.MAT_CODIGO "
				+ "                 from agh.MBC_ITENS_REQUISICAO_OPMES iro "
				+ "					join agh.MBC_MATERIAIS_ITEM_OPMES   mio  "
				+ "                         ON mio.IRO_SEQ = iro.SEQ  "
				+ "                         and mio.QTD_SOLC > 0  "
				+ "                 where iro.ROP_SEQ = rop.SEQ "
				+ "             ) ) subq   "
				+ "     GROUP BY "
				+ "         SEQ_REQUISICAO, "
				+ "         ITEM_SUS,       "
				+ "         QTD_AUTORIZADA, "
				+ "         MATERIAL,       "
				+ "         reqOrder        "
				+ "     ORDER BY            "
				+ "         reqOrder,       "
				+ "         ITEM_SUS,       "
				+ "         MATERIAL  ");		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("cirurgiaSeq", cirurgiaSeq);
		return query.list();
		
	}

	/**
	 * Verifica a existência de Cirurgia para a Consulta informada.
	 * 
	 * @param numeroConsulta - Número da Consulta
	 * @return Flag indicando a existência do registro
	 */
	public boolean verificarExistenciaCirurgiaPorConsulta(Integer numeroConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CIR");

		criteria.createAlias("CIR." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		return executeCriteriaExists(criteria);
	}

	/**
	 * Realiza uma busca por Cirurgias futuras agendadas, relacionadas ao Paciente informado.
	 * 
	 * @param codigoPaciente - Código do Paciente
	 * @return Lista de Cirurgias relacionadas
	 */
	public List<MbcCirurgias> pesquisarCirurgiasFuturasAgendadasPorPaciente(Integer codigoPaciente) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CIR");
		
		criteria.createAlias("CIR." + MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.gt("CIR." + MbcCirurgias.Fields.DATA.toString(), new Date()));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("CIR." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.AGND));
		
		return executeCriteria(criteria);
	}
	
	public MbcCirurgias obterCirurgiaPorChavePrimaria(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "mbc");
		criteria.createAlias("mbc." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias("mbc." + MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("pac." + AipPacientes.Fields.ENDERECOS.toString(), "end", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), crgSeq));

		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}
	
	public MbcCirurgias obterCirurgiaPorSeqServidor(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.ATENDIMENTO.toString(), "atendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.PACIENTE.toString(), "paciente");
		criteria.createAlias(MbcCirurgias.Fields.SERVIDOR.toString(), "servidor");
		criteria.createAlias(MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "salaCirurgica");
		criteria.createAlias("salaCirurgica." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");
		criteria.createAlias(MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO.toString(), "convenioSaudePlano");
		criteria.createAlias("convenioSaudePlano." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "convenioSaude");
		criteria.createAlias(MbcCirurgias.Fields.CENTRO_CUSTO.toString(), "cc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "especialidade");
		
		
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.SEQ.toString(), seq));

		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}
	
	public MbcCirurgias obterCirurgiaAtendimentoCancelada(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "MBC");
		criteria.createAlias("MBC." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.C));
		criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		
		return (MbcCirurgias) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcCirurgias> obterCirurgiasPorPacienteEDatas(List<Date> datas, Integer pacCodigo, List<Short> listaSeqp) {
//		SELECT CRG.SEQ
//		FROM MBC_CIRURGIAS CRG 
//		WHERE (CRG.DATA BETWEEN (Datas obtidas na RN03 na consulta C11) AND DATA ATUAL))
//		AND CRG.SITUACAO <> <'CANC'  -- Ver domínio DominioSituacaoCirurgia.java>
//		AND CRG.UNF_SEQ IN  <UNF_SEQ’s obtidos na consulta C11>
//		AND CRG.PAC_CODIGO = <Código do paciente em questão>
//		ORDER BY 1;
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "cir");
		if(datas != null){
			Date dataAtual = new Date(); 
			for(Date data : datas){
				criteria.add(Restrictions.between(MbcCirurgias.Fields.DATA.toString(), data, dataAtual));
			}
		}
		
		criteria.add(Restrictions.ne(MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.in(MbcCirurgias.Fields.UNF_SEQ.toString(), listaSeqp));
		criteria.add(Restrictions.eq(MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.asc(MbcCirurgias.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesCirurgia(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("AGD." + MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString(), "PUC");
		criteria.createAlias("AGD." + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), "EPR");
		criteria.createAlias("EPR." + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString())
						, NotificacoesGeraisVO.Fields.SEQ.toString())
				.add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString())
						, NotificacoesGeraisVO.Fields.DESCRICAO.toString())
				.add(Projections.property("CRG." + MbcCirurgias.Fields.DTHR_FIM_CIRG.toString())
						, NotificacoesGeraisVO.Fields.DT_FIM_CIRURGIA.toString()));
		
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.addOrder(Order.desc("CRG." + MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * 42010
	 * @param codigoPaciente
	 * @param valorNumericoParametro
	 * C_BUSCA_IMPLANTE
	 * @return
	 */
	
	public List<MbcCirurgias> obterImplante(final Integer codigoPaciente, Integer valorNumericoParametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "CNV")
		.createAlias("CRG." + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "PPC")
		.createAlias("PPC." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_HOSP_INTERNO.toString(), "PHI")
		
		.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.PAC_CODIGO.toString(), codigoPaciente))
		.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA))
		.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), true));
		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "especialidade")
		
		
		.add(Restrictions.eqProperty("CNV." + FatConvenioSaude.Fields.CODIGO.toString(), "CRG." + MbcCirurgias.Fields.CSP_CNV_CODIGO.toString()))
		.add(Restrictions.eq("CNV." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S))

		.add(Restrictions.eqProperty("PPC." + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), "CRG." + MbcCirurgias.Fields.SEQ.toString()))
		.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA))
		.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A))
		
		.add(Restrictions.eqProperty("PHI." + FatProcedHospInternos.Fields.PCI_SEQ.toString(), "PPC." + MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString()))
		.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.SEQ.toString(), valorNumericoParametro));
				
		return executeCriteria(criteria);
	}
	
	/** #42803
	 *  verifica se paciente fez implante antes do atendimento no ambulatório
	 *  busca_cirurgia
	 */
	public MbcCirurgias buscarCirurgia(Integer pacCodigo, Date dtRealizado){
		StringBuilder hql = new StringBuilder("Select crg from ").append(FatProcedHospInternosPai.class.getName()).append(" phi, ")
					.append(MbcProcEspPorCirurgias.class.getName()).append(" ppc, ")
					.append(MbcCirurgias.class.getName()).append(" crg ")
					.append(" WHERE crg.")
					.append(MbcCirurgias.Fields.PAC_CODIGO.toString()).append(" = :pacCodigo and ")
					.append("crg.").append(MbcCirurgias.Fields.SITUACAO.toString()).append(" =:situacao and ")
					.append("crg.").append(MbcCirurgias.Fields.DATA.toString()).append(" < :dtRealizado and ")
					.append(" substr(crg.").append(MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString()).append(",1,1) = 'S' and ")
					.append(" crg.").append(MbcCirurgias.Fields.CSP_CNV_CODIGO.toString()).append(" = 1 and ")
					.append(" ppc.").append(MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString()).append(" = ")
					.append(" crg.").append(MbcCirurgias.Fields.SEQ.toString())
					.append(" and ppc.").append(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()).append(" = 'NOTA' and ")
					.append(" ppc.").append(MbcProcEspPorCirurgias.Fields.SITUACAO.toString()).append(" = :situacaoA and ")
					.append(" phi.").append(FatProcedHospInternosPai.Fields.PCI_SEQ.toString()).append(" = ").append(" ppc.").append(MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString())
					.append(" and phi.").append(FatProcedHospInternosPai.Fields.SEQ.toString()).append(" = ").append(" ( select ").append(AghParametros.Fields.VLR_NUMERICO.toString()).append(" from ")
					.append(AghParametros.class.getName()).append(" WHERE nome = 'P_PHI_IMPLANTE_COCLEAR' )");
			
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("situacao", DominioSituacaoCirurgia.RZDA);
		query.setParameter("dtRealizado", dtRealizado);
		query.setParameter("situacaoA", DominioSituacao.A);
		
		return (MbcCirurgias) query.uniqueResult();
	}	
	
	public MbcCirurgias obterCirurgiaPorDtInternacaoEOrigem(Integer pacCodigo, Date dtInternacao, DominioOrigemAtendimento origem) {
		MbcCirurgias cirurgia = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "MBC");
		criteria.createAlias("MBC." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MBC." + MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), origem));
		
		StringBuffer sql = new StringBuffer(140);
		if (isOracle()) {
			sql.append(" trunc({alias}.DTHR_INICIO_CIRG) = to_date('" + DateUtil.obterDataFormatada(dtInternacao, "dd/MM/yyyy") + "','dd/MM/yyyy') ");
		} else {
			sql.append(" date_trunc('day', {alias}.DTHR_INICIO_CIRG::timestamp) = to_date('" + DateUtil.obterDataFormatada(dtInternacao, "dd/MM/yyyy") + "','dd/MM/yyyy') ");
		}
		criteria.add(Restrictions.sqlRestriction(sql.toString()));
		criteria.addOrder(Order.desc("MBC." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));
		
		List<MbcCirurgias> listaCirurgias = this.executeCriteria(criteria);
		if (!listaCirurgias.isEmpty()) {
			cirurgia = listaCirurgias.get(0);
		}
		return cirurgia;
	}
}