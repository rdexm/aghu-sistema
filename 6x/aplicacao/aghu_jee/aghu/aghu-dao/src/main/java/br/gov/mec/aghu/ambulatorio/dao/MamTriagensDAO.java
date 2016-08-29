package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.emergencia.vo.GravidadesVO;
import br.gov.mec.aghu.emergencia.vo.PacientesAguardandoAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.PacientesAtendidosVO;
import br.gov.mec.aghu.emergencia.vo.TriagemVO;
import br.gov.mec.aghu.model.MamAgrupGravidade;
import br.gov.mec.aghu.model.MamAgrupXGravidade;
import br.gov.mec.aghu.model.MamCaractSitEmerg;
import br.gov.mec.aghu.model.MamDestinos;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTrgAlergias;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTrgPrevAtend;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MamTriagensDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTriagens> {

	private static final long serialVersionUID = 5749331210834902942L;
	
	public List<Object[]> listarAtendimentosPacienteTriagemPorCodigo(Integer pacCodigo) { 
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);

		criteria.add(Restrictions.eq(MamTriagens.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MamTriagens.Fields.DTHR_INICIO.toString()));
		p.add(Projections.property(MamTriagens.Fields.UNF_SEQ.toString()));

		criteria.setProjection(p);
				
		return executeCriteria(criteria);
	}
	
	public List<MamTriagens> listarTriagensPorSeq(Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);

		criteria.add(Restrictions.eq(MamTriagens.Fields.SEQ.toString(), trgSeq));

		return executeCriteria(criteria);
	}
	
	public Boolean pacienteEmAtendimentoEmergenciaTerreo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);
		criteria.add(Restrictions.eq(MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(),DominioPacAtendimento.S));
		criteria.add(Restrictions.in(MamTriagens.Fields.ULT_TIPO_MVTO.toString(), Arrays.asList(DominioTipoMovimento.E,DominioTipoMovimento.S)));
		criteria.add(Restrictions.eq(MamTriagens.Fields.PAC_CODIGO.toString(),pacCodigo));
		return executeCriteriaCount(criteria) > 0 ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public Boolean pacienteEmAtendimentoEmergenciaUltimosDias(Integer pacCodigo, Integer dias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);
		criteria.add(Restrictions.eq(MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(),DominioPacAtendimento.N));
		criteria.add(Restrictions.eq(MamTriagens.Fields.ULT_TIPO_MVTO.toString(), DominioTipoMovimento.C));
		criteria.add(Restrictions.isNotNull(MamTriagens.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.between(MamTriagens.Fields.DTHR_FIM.toString(), DateUtil.adicionaDias(new Date(),-dias), (new Date())));
		criteria.add(Restrictions.eq(MamTriagens.Fields.PAC_CODIGO.toString(),pacCodigo));
		return executeCriteriaCount(criteria) > 0 ? Boolean.TRUE : Boolean.FALSE;
	}

	/**


	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamTriagens buscarTriagensPorPacCodigoSeq(Integer pacCodigo, Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);

		criteria.add(Restrictions.ne(MamTriagens.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MamTriagens.Fields.SEQ.toString(), trgSeq));
		List<MamTriagens> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * Executa o count da pesquisa de Triagem da situa��o de emerg�ncia pelo c�digo da situacao
	 * 
	 * C7 de #12167  - Manter cadastro de situa��es da emergencia
	 * 
	 * @param codigoSit
	 * @return
	 */
	public Long pesquisarTriagemSituacaoEmergenciaCount(Short codigoSit) {

		final DetachedCriteria criteria = this.montarPesquisaTriagemSituacaoEmergencia(codigoSit);

		return this.executeCriteriaCount(criteria);
	}
	
	public Short buscaSegSeqDaTriagem(Long trgSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(
				MamTriagens.class, "MamTriagens");

		if (trgSeq != null) {
			criteria.add(Restrictions.eq("MamTriagens."
					+ MamTriagens.Fields.SEQ.toString(), trgSeq));
		}

		return (Short) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Monta a pesquisa de Triagem da situacao de emergencia pelo  codigo da situacao
	 * 
	 * C7 de #12167 Manter cadastro de situacoes da emergencia
	 * 
	 * @param codigoSit
	 * @return
	 */
	private DetachedCriteria montarPesquisaTriagemSituacaoEmergencia(Short codigoSit) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "MamTriagens");

		if (codigoSit != null) {
			criteria.createAlias("MamTriagens." + MamTriagens.Fields.SITUACAO_EMERGENCIA.toString(), "MamSituacaoEmergencia");
			criteria.add(Restrictions.eq("MamSituacaoEmergencia." + MamSituacaoEmergencia.Fields.SEQ.toString(), codigoSit));
		}

		return criteria;
	}

	/** C 19 **/
	public Boolean verificaExisteCaracteriscaListaTriagem(		
			Long seqTriagem){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "MamTriagem");

		criteria.createAlias("MamTriagem." + MamTriagens.Fields.SITUACAO_EMERGENCIA.toString(), "SITUACAO_EMERGENCIA");
		criteria.createAlias("SITUACAO_EMERGENCIA." + MamSituacaoEmergencia.Fields.MAM_CARACT_SIT_EMERGES.toString(), "MAM_CARACT_SIT_EMERGES");
		
		criteria.add(Restrictions.eq("MamTriagem." + MamTriagens.Fields.SEQ.toString(), seqTriagem));
		criteria.add(Restrictions.eq("MAM_CARACT_SIT_EMERGES." + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), DominioCaracteristicaEmergencia.LISTA_TRIAGEM));
		criteria.add(Restrictions.eq("MAM_CARACT_SIT_EMERGES." + MamCaractSitEmerg.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}
	
	/***
     * C14
     */
	public List<Integer> verificaAtendimentoPrevioGrade(Integer tempoMinutos){
    	
    	final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "MamTriagem");
    	
    	criteria.createAlias("MamTriagem." + MamTriagens.Fields.MAM_TRG_ENC_INTERNO.toString(), "MamTrgEncInterno");
    	criteria.createAlias("MamTriagem." + MamTriagens.Fields.MAM_TRG_PREV_ATEND.toString(), "MamTrgprevAtend");
    	criteria.createAlias("MamTriagem." + MamTriagens.Fields.MAM_REGISTROS.toString(), "MamRegistros");
    	criteria.createAlias("MamTriagem." + MamTriagens.Fields.MAM_DESTINOS.toString(), "MamDestinos");
    	
    	criteria.add(Restrictions.eq("MamTriagem." + MamTriagens.Fields.IND_PAC_EMERGENCIA.toString(), Boolean.TRUE));
    	criteria.add(Restrictions.eq("MamRegistros." + MamRegistro.Fields.IND_NO_CONSULTORIO.toString(), Boolean.TRUE));
    	criteria.add(Restrictions.eq("MamRegistros." + MamRegistro.Fields.IND_SITUACAO.toString(), DominioSituacaoRegistro.VA));
    	criteria.add(Restrictions.eq("MamDestinos." + MamDestinos.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
    	criteria.add(Restrictions.isNull("MamTrgEncInterno." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
    	
    	DetachedCriteria subQuerySeq = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEIS");
		subQuerySeq.add(Restrictions.eqProperty("TEIS." + MamTrgEncInterno.Fields.TRG_SEQ.toString(), "MamTrgEncInterno." + MamTrgEncInterno.Fields.TRG_SEQ.toString()));
		subQuerySeq.add(Restrictions.isNull("TEIS." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		subQuerySeq.setProjection(Projections.max("TEIS."+ MamTrgEncInterno.Fields.SEQ_P.toString()));
		criteria.add(Subqueries.propertyEq("MamTrgEncInterno." + MamTrgEncInterno.Fields.SEQ_P.toString(), subQuerySeq));
		
		criteria.add(Restrictions.eq("MamTrgprevAtend." + MamTrgPrevAtend.Fields.IND_IMEDIATO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull("MamTrgprevAtend." + MamTrgPrevAtend.Fields.DTHR_PREV_ATEND.toString()));
		criteria.add(Restrictions.gtProperty("MamTrgprevAtend." + MamTrgPrevAtend.Fields.DTHR_PREV_ATEND.toString(), "MamDestinos." + MamDestinos.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.gt("MamTrgprevAtend." + MamTrgPrevAtend.Fields.DTHR_PREV_ATEND.toString(), new Date()));
		
		if(isOracle()) {
			criteria.add(Restrictions.sqlRestriction(" DTHR_PREV_ATEND - DTHR_VALIDA_MVTO <= " + tempoMinutos/60/24));
		} else {
			criteria.add(Restrictions.sqlRestriction(" DTHR_PREV_ATEND - DTHR_VALIDA_MVTO <= INTERVAL '" + tempoMinutos/60/24 + " minutes'"));
		}
		
    	criteria.setProjection(Projections.distinct(Projections.property("MamTrgEncInterno." + MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString())));
    	
    	return executeCriteria(criteria);
    }
    
    /***
     * C15
     */
	public List<GravidadesVO> pesquisarPacientesAtendimento(){
    	
    	DominioTipoMovimento[] tipoDominio = new DominioTipoMovimento[]{DominioTipoMovimento.E, DominioTipoMovimento.S};
    	
    	final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "MamTriagem");
    	
    	criteria.createAlias("MamTriagem." + MamTriagens.Fields.MAM_TRG_ENC_INTERNO.toString(), "MamTrgEncInterno");
    	criteria.createAlias("MamTriagem." + MamTriagens.Fields.MAM_TRG_GRAVIDADE.toString(), "MamTrgGravidade");
    	criteria.createAlias("MamTriagem." + MamTriagens.Fields.SITUACAO_EMERGENCIA.toString(), "SITUACAO_EMERGENCIA");
       	criteria.createAlias("SITUACAO_EMERGENCIA." + MamSituacaoEmergencia.Fields.MAM_CARACT_SIT_EMERGES.toString(), "MamCaracSitEmerg");       	
    	criteria.createAlias("MamTrgGravidade." + MamTrgGravidade.Fields.MAM_GRAVIDADES.toString(), "MamGravidade");
    	criteria.createAlias("MamGravidade." + MamGravidade.Fields.AGRUP_X_GRAVIDADE.toString(), "MamAgrupXGravidade");
    	criteria.createAlias("MamAgrupXGravidade." + MamAgrupXGravidade.Fields.AGRUPAMENTO_GRAVIDADE.toString() , "MamAgrupGravidade");
    	
    	criteria.add(Restrictions.eq("MamTriagem." + MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString() , DominioPacAtendimento.S));
		criteria.add(Restrictions.in("MamTriagem." + MamTriagens.Fields.ULT_TIPO_MVTO.toString(), tipoDominio));
		criteria.add(Restrictions.eq("MamCaracSitEmerg." + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(),DominioCaracteristicaEmergencia.LISTA_AGUARDANDO));
		criteria.add(Restrictions.isNull("MamTrgEncInterno." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
    	
		DetachedCriteria subQuerySeq = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEIS");
		subQuerySeq.add(Restrictions.eqProperty("TEIS." + MamTrgEncInterno.Fields.TRG_SEQ.toString(), "MamTrgEncInterno." + MamTrgEncInterno.Fields.TRG_SEQ.toString()));
		subQuerySeq.add(Restrictions.isNull("TEIS." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		subQuerySeq.setProjection(Projections.max("TEIS."+ MamTrgEncInterno.Fields.SEQ_P.toString()));
		criteria.add(Subqueries.propertyEq("MamTrgEncInterno." + MamTrgEncInterno.Fields.SEQ_P.toString(), subQuerySeq));
    	
		DetachedCriteria subQuerySeqGravidade = DetachedCriteria.forClass(MamTrgGravidade.class, "TGGS");
		subQuerySeqGravidade.add(Restrictions.eqProperty("TGGS." + MamTrgGravidade.Fields.TRG_SEQ.toString(), "MamTrgGravidade." + MamTrgGravidade.Fields.TRG_SEQ.toString()));
		    subQuerySeqGravidade.setProjection(Projections.max("TGGS."+ MamTrgGravidade.Fields.SEQP.toString()));
		criteria.add(Subqueries.propertyEq("MamTrgGravidade."+ MamTrgGravidade.Fields.SEQP.toString(), subQuerySeqGravidade));
		
		criteria.add(Restrictions.eq("MamAgrupXGravidade." + MamAgrupXGravidade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("MamAgrupGravidade." + MamAgrupGravidade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc("MamAgrupGravidade." + MamAgrupGravidade.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc("MamTriagem." + MamTriagens.Fields.SEQ.toString()));
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("MamAgrupGravidade."+ MamAgrupGravidade.Fields.SEQ.toString()))
				.add(Projections.property("MamTrgGravidade."+ MamTrgGravidade.Fields.GRV_SEQ.toString()))
				.add(Projections.property("MamTriagem."+ MamTriagens.Fields.SEQ.toString()))
				.add(Projections.property("MamTrgEncInterno."+ MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()));
		
		criteria.setProjection(projection);
		
		List<GravidadesVO> gravidades = new ArrayList<GravidadesVO>();
		List<Object[]> lstObj = executeCriteria(criteria);
		for(Object[] obj : lstObj) {
			GravidadesVO grv = new GravidadesVO((Short) obj[0], (Short) obj[1], (Long) obj[2], (Integer) obj[3]);
			gravidades.add(grv);
		}
    	return gravidades;
    }
	
	public List<MamTriagens> verificarExisteTriagemPorPaciente(Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);
		
		criteria.add(Restrictions.eq(MamTriagens.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(MamTriagens.Fields.DTHR_FIM.toString()));
		
		criteria.addOrder(Order.desc(MamTriagens.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	public List<MamTriagens> listarPacientesTriagemPorUnfSeq(Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamUnidAtendem.class, "UAN");
		
		subCriteria.setProjection(Projections.projectionList()
				.add(Projections.property("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString())));
		Criterion eqUnfSeq1 = Restrictions.eq("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString(), unfSeq);
		Criterion eqUnfSeq2 = Restrictions.eq("UAN." + MamUnidAtendem.Fields.UAN_UNF_SEQ.toString(), unfSeq);
		subCriteria.add(Restrictions.or(eqUnfSeq1, eqUnfSeq2));
		
		criteria.add(Subqueries.propertyIn("TRG." + MamTriagens.Fields.UNF_SEQ.toString(), subCriteria));
		
		final DetachedCriteria subCriteria2 = DetachedCriteria.forClass(MamCaractSitEmerg.class, "MCE");
		subCriteria2.setProjection(Projections.projectionList()
				.add(Projections.property("MCE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString())));
		subCriteria2.add(Restrictions.eqProperty("MCE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString()
				, "TRG." + MamTriagens.Fields.SEG_SEQ.toString()));
		subCriteria2.add(Restrictions.eq("MCE." + MamCaractSitEmerg.Fields.CARACTERISTICA.toString()
				, DominioCaracteristicaEmergencia.LISTA_TRIAGEM));
		
		criteria.add(Subqueries.propertyIn("TRG." + MamTriagens.Fields.SEG_SEQ.toString(), subCriteria2));
		
		return executeCriteria(criteria);
	}
	
	public TriagemVO obterTriagemVOPorSeq(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "MTR");
		criteria.createAlias("MTR." + MamTriagens.Fields.MAM_TRG_ALERGIAS.toString(), "MTA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MTR." + MamTriagens.Fields.MAM_TRG_GRAVIDADE.toString(), "GRV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRV." + MamTrgGravidade.Fields.MAM_GRAVIDADES.toString(), "GRAV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MTR." + MamTriagens.Fields.ORIGEM_PACIENTE.toString(), "ORP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MTR." + MamTriagens.Fields.SEQ.toString())
						, TriagemVO.Fields.TRG_SEQ.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.QUEIXA_PRINCIPAL.toString())
						, TriagemVO.Fields.QUEIXA_PRINCIPAL.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.DATA_QUEIXA.toString())
						, TriagemVO.Fields.DATA_QUEIXA.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.HORA_QUEIXA.toString())
						, TriagemVO.Fields.HORA_QUEIXA.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.IND_INTERNADO.toString())
						, TriagemVO.Fields.IND_INTERNADO.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.INFORMACOES_COMPLEMENTARES.toString())
						, TriagemVO.Fields.INFO_COMPLEMENTARES.toString())
				.add(Projections.property("MTA." + MamTrgAlergias.Fields.DESCRICAO.toString())
						, TriagemVO.Fields.ALERGIAS.toString())
				.add(Projections.property("MTA." + MamTrgAlergias.Fields.SEQP.toString())
						, TriagemVO.Fields.SEQP_ALERGIA.toString())
				.add(Projections.property("GRAV." + MamGravidade.Fields.DESCRICAO.toString())
						, TriagemVO.Fields.DESCRICAO_GRAVIDADE.toString())
				.add(Projections.property("GRAV." + MamGravidade.Fields.COD_COR.toString())
						, TriagemVO.Fields.COD_COR.toString())
				.add(Projections.property("ORP." + MamOrigemPaciente.Fields.SEQ.toString())
						, TriagemVO.Fields.SEQ_ORIGEM.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.HOUVE_CONTATO.toString())
						, TriagemVO.Fields.HOUVE_CONTATO.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.CONTATO.toString())
						, TriagemVO.Fields.NOME_CONTATO.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.UNF_SEQ.toString())
						, TriagemVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("MTR." + MamTriagens.Fields.HOSPITAL_INTERNADO_SEQ.toString())
						, TriagemVO.Fields.HOSPITAL_INTERNADO.toString()));
		
		criteria.add(Restrictions.eq("MTR." + MamTriagens.Fields.SEQ.toString(), trgSeq));
		
		criteria.addOrder(Order.desc("GRV." + MamTrgGravidade.Fields.SEQP.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(TriagemVO.class));
		
		List<TriagemVO> result = executeCriteria(criteria, 0, 1, null, false);
		
		if(result != null && !result.isEmpty()){
			return result.get(0);
		}
		
		return null;
	}
	
	
	
	/**
	 * Listar pacientes da emergencia da aba Aguardando
	 * @param unfSeq
	 * @return
	 */
	public List<PacientesAguardandoAtendimentoVO> listarPacientesAguardandoPorUnfSeq(Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		criteria.createAlias("TRG."+ MamTriagens.Fields.MAM_TRG_PREV_ATEND, "PREV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TRG."+ MamTriagens.Fields.MAM_TRG_ENC_INTERNO,"INT");
		

		final DetachedCriteria subCriteria2 = DetachedCriteria.forClass(MamCaractSitEmerg.class, "CSE");
		subCriteria2.setProjection(Projections.projectionList().add(Projections.property("CSE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString())));
		subCriteria2.add(Restrictions.eq("CSE." + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), DominioCaracteristicaEmergencia.LISTA_AGUARDANDO));
		subCriteria2.add(Restrictions.eqProperty("CSE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString(), "TRG." + MamTriagens.Fields.SEG_SEQ.toString()));
		
		criteria.add(Subqueries.exists(subCriteria2));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamUnidAtendem.class, "UAN");
		subCriteria.setProjection(Projections.projectionList()
				.add(Projections.property("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString())));
		Criterion eqUnfSeq1 = Restrictions.eq("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString(), unfSeq);
		Criterion eqUnfSeq2 = Restrictions.eq("UAN." + MamUnidAtendem.Fields.UAN_UNF_SEQ.toString(), unfSeq);
		subCriteria.add(Restrictions.or(eqUnfSeq1, eqUnfSeq2));
		criteria.add(Subqueries.propertyIn("TRG." + MamTriagens.Fields.UNF_SEQ.toString(), subCriteria));
		
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.IND_PAC_EMERGENCIA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.ULT_TIPO_MVTO.toString(), DominioTipoMovimento.E));
		criteria.add(Restrictions.isNull("INT."+ MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		
		criteria.addOrder(Order.asc("PREV."+ MamTrgPrevAtend.Fields.DTHR_PREV_ATEND.toString()));
		criteria.addOrder(Order.asc("INT."+ MamTrgEncInterno.Fields.SEQP.toString()));
		
		ProjectionList projection =	Projections.projectionList()
											.add(Projections.property("TRG."+ MamTriagens.Fields.SEQ.toString()), PacientesAguardandoAtendimentoVO.Fields.TRG_SEQ.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.PAC_CODIGO.toString()), PacientesAguardandoAtendimentoVO.Fields.PAC_CODIGO.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.SEG_SEQ.toString()), PacientesAguardandoAtendimentoVO.Fields.SEG_SEQ.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString()), PacientesAguardandoAtendimentoVO.Fields.IND_PAC_ATENDIMENTO.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.IND_PAC_EMERGENCIA.toString()), PacientesAguardandoAtendimentoVO.Fields.IND_PAC_EMERGENCIA.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.UNF_SEQ.toString()), PacientesAguardandoAtendimentoVO.Fields.UNF_SEQ.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.ULT_TIPO_MVTO.toString()), PacientesAguardandoAtendimentoVO.Fields.ULT_TIPO_MVT.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.DTHR_ULT_MVTO.toString()), PacientesAguardandoAtendimentoVO.Fields.DTHR_ULT_MVTO.toString())
											.add(Projections.property("PREV."+ MamTrgPrevAtend.Fields.IND_IMEDIATO.toString()), PacientesAguardandoAtendimentoVO.Fields.IND_IMEDIATO.toString())
											.add(Projections.property("PREV."+ MamTrgPrevAtend.Fields.DTHR_PREV_ATEND.toString()), PacientesAguardandoAtendimentoVO.Fields.DTHR_PREV_ATEND.toString())
											.add(Projections.property("INT."+ MamTrgEncInterno.Fields.SEQP.toString()), PacientesAguardandoAtendimentoVO.Fields.SEQP.toString())
											.add(Projections.property("INT."+ MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()), PacientesAguardandoAtendimentoVO.Fields.CON_NUMERO.toString())
											.add(Projections.property("INT."+ MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()), PacientesAguardandoAtendimentoVO.Fields.DTHR_ESTORNO.toString());
		criteria.setProjection(Projections.distinct(projection));	
		criteria.setResultTransformer(Transformers.aliasToBean(PacientesAguardandoAtendimentoVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * #28986  C1 - Listar pacientes da emergencia da aba Atendidos
	 * @param unfSeq
	 * @return
	 */
	public List<PacientesAtendidosVO> listarPacientesAtendidoPorUnfSeq(Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		criteria.createAlias("TRG."+ MamTriagens.Fields.MAM_TRG_ENC_INTERNO,"INT");
		criteria.createAlias("TRG."+ MamTriagens.Fields.MAM_UNID_ATENDIMENTO,"UND");
		
		final DetachedCriteria subCriteria2 = DetachedCriteria.forClass(MamCaractSitEmerg.class, "CSE");
		subCriteria2.setProjection(Projections.projectionList().add(Projections.property("CSE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString())));
		subCriteria2.add(Restrictions.eq("CSE." + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), DominioCaracteristicaEmergencia.LISTA_ATENDIDO));
		subCriteria2.add(Restrictions.eqProperty("CSE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString(), "TRG." + MamTriagens.Fields.SEG_SEQ.toString()));
		
		criteria.add(Subqueries.exists(subCriteria2));
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.ULT_TIPO_MVTO.toString(), DominioTipoMovimento.E));
		criteria.add(Restrictions.isNull("INT."+ MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		criteria.addOrder(Order.desc("INT."+ MamTrgEncInterno.Fields.SEQP.toString()));
		
		ProjectionList projection =	Projections.projectionList()
											.add(Projections.property("TRG."+ MamTriagens.Fields.SEQ.toString()), PacientesAtendidosVO.Fields.TRG_SEQ.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.PAC_CODIGO.toString()), PacientesAtendidosVO.Fields.PAC_CODIGO.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.SEG_SEQ.toString()), PacientesAtendidosVO.Fields.SEG_SEQ.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString()), PacientesAtendidosVO.Fields.IND_PAC_ATENDIMENTO.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.UNF_SEQ.toString()), PacientesAtendidosVO.Fields.UNF_SEQ.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.ULT_TIPO_MVTO.toString()), PacientesAtendidosVO.Fields.ULT_TIPO_MVT.toString())
											.add(Projections.property("TRG."+ MamTriagens.Fields.DTHR_ULT_MVTO.toString()), PacientesAtendidosVO.Fields.DTHR_ULT_MVTO.toString())
											.add(Projections.property("INT."+ MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()), PacientesAtendidosVO.Fields.CON_NUMERO.toString())
											.add(Projections.property("INT."+ MamTrgEncInterno.Fields.SEQP.toString()), PacientesAtendidosVO.Fields.SEQP.toString());
							
		criteria.setProjection(projection);	
		criteria.setResultTransformer(Transformers.aliasToBean(PacientesAtendidosVO.class));
		return executeCriteria(criteria);
	}
	
	
	/**
	 *  #28986  C9 - Verificar se paciente ainda esta na emergencia
	 * @param pacCodigo
	 * @return
	 */
	public MamTriagens verificarPacienteEmergencia(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);
		criteria.add(Restrictions.eq(MamTriagens.Fields.SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq(MamTriagens.Fields.ULT_TIPO_MVTO.toString(), DominioTipoMovimento.E));
		return (MamTriagens) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44179 - CURSOR cur_trg
	 * @author thiago.cortes
	 * return segSeq
	 */
	public Short obterSegSeqPorSeq(Long seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);
		criteria.setProjection(Projections.property(MamTriagens.Fields.SEG_SEQ.toString()));
		criteria.add(Restrictions.eq(MamTriagens.Fields.SEQ.toString(), seq));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
}
