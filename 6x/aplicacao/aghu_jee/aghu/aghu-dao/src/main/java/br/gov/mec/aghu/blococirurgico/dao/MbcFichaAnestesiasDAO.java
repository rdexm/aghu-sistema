package br.gov.mec.aghu.blococirurgico.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaEquipeAnestesia;
import br.gov.mec.aghu.model.MbcFichaMedicPreAnest;
import br.gov.mec.aghu.model.MbcFichaProcedimento;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemMonitorizacaoDefinidoFichaAnestVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class MbcFichaAnestesiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaAnestesias> {

	private static final long serialVersionUID = 3002146859800326098L;
	
	private static final Log LOG = LogFactory.getLog(MbcFichaAnestesiasDAO.class);

	public List<MbcFichaAnestesias> listarFichasAnestesiasPorCirurgias(final Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);

		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.isNull(MbcFichaAnestesias.Fields.DTHR_MVTO.toString()));

		final DominioIndPendenteAmbulatorio[] dominios = {DominioIndPendenteAmbulatorio.R, 
														  DominioIndPendenteAmbulatorio.P, 
														  DominioIndPendenteAmbulatorio.V};
		
		criteria.add(Restrictions.in(MbcFichaAnestesias.Fields.PENDENTE.toString(), dominios));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcFichaAnestesias> listarFichasAnestesias(Integer pacCodigo, Integer gsoPacCodigo, Short gsoSequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);

		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.GSO_SEQUENCE.toString(), gsoSequence));

		return executeCriteria(criteria);
	}

	public List<MbcFichaAnestesias> listarFichasAnestesiasPorCodigoPacienteComGsoPacCodigoNulo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);

		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(MbcFichaAnestesias.Fields.GSO_PAC_CODIGO.toString()));

		return executeCriteria(criteria);

	}
	
	public List<ItemMonitorizacaoDefinidoFichaAnestVO> pesquisarItensMonitorizacaoDefinidosFicha(
			Long seqMbcFichaAnest) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaMedicPreAnest.class);
		criteria.createAlias(MbcFichaMedicPreAnest.Fields.FICHAS_ANESTESIAS.toString(), "fic");
		criteria.createAlias(MbcFichaMedicPreAnest.Fields.MEDICAMENTO.toString(), "med", Criteria.LEFT_JOIN);
		criteria.createAlias(MbcFichaMedicPreAnest.Fields.FORMA_DOSAGEM.toString(), "fds", Criteria.LEFT_JOIN);
		criteria.createAlias("med." + AfaMedicamento.Fields.TPR.toString(), "tpr", Criteria.LEFT_JOIN);
		criteria.createAlias("fds." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "umm", Criteria.LEFT_JOIN);

		ProjectionList projection = Projections.projectionList()
			.add(Projections.property("fic." + MbcFichaAnestesias.Fields.SEQ.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.SEQ_FICHA_ANESTESIA.toString())
			.add(Projections.property("med." + AfaMedicamento.Fields.MAT_CODIGO.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.MAT_CODIGO_MEDICAMENTO.toString())
			.add(Projections.property("med." + AfaMedicamento.Fields.DESCRICAO.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.DESCRICAO_MEDICAMENTO.toString())
			.add(Projections.property(MbcFichaMedicPreAnest.Fields.VIA_ADMINISTRACAO_VAD_SIGLA.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.SIGLA_VIA_ADMINISTRACAO.toString())
			.add(Projections.property(MbcFichaMedicPreAnest.Fields.DOSE.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.DOSE_FICHA_MEDIC_ANEST.toString())
			.add(Projections.property("umm." + MpmUnidadeMedidaMedica.Fields.SEQ.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.SEQ_UNIDADE_MEDIDA_MEDICA.toString())
			.add(Projections.property("tpr." + AfaTipoApresentacaoMedicamento.Fields.DESCRICAO.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.DESCRICAO_TIPO_APRES_MDTO.toString())
			.add(Projections.property("umm." + MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.DESCRICAO_UNIDADE_MEDIDA_MEDICA.toString())
		;
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnest));
		criteria.add(Restrictions.eq(MbcFichaMedicPreAnest.Fields.SELECIONADO.toString(), Boolean.TRUE));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemMonitorizacaoDefinidoFichaAnestVO.class));
		
		List<ItemMonitorizacaoDefinidoFichaAnestVO> list1 = executeCriteria(criteria);
		
		DetachedCriteria criteria1 = DetachedCriteria.forClass(MbcFichaAnestesias.class);
		criteria1.add(Restrictions.eq(MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnest));
		ProjectionList projection1 = Projections.projectionList()
			.add(Projections.property(MbcFichaAnestesias.Fields.SEQ.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.SEQ_FICHA_ANESTESIA.toString())
			.add(Projections.property(MbcFichaAnestesias.Fields.NENHUMA_MEDICACAO_PRE_ANEST.toString()), ItemMonitorizacaoDefinidoFichaAnestVO.Fields.MEDICACAO_PRE_ANESTESICA.toString())
		;
		criteria1.setProjection(projection1);
		criteria1.setResultTransformer(Transformers.aliasToBean(ItemMonitorizacaoDefinidoFichaAnestVO.class));
		
		List<ItemMonitorizacaoDefinidoFichaAnestVO> list2 = executeCriteria(criteria1);
		
		for(ItemMonitorizacaoDefinidoFichaAnestVO vo : list2){
			vo.setDescricaoMedicamento(DominioSimNao.S.equals(vo.getMedicacaoPreAnestesica()) ? "Nenhuma medicação pré-anestésica." : null);
		}
		list1.addAll(list2);
		
		CoreUtil.ordenarLista(list1, ItemMonitorizacaoDefinidoFichaAnestVO.Fields.DESCRICAO_MEDICAMENTO.toString(), Boolean.TRUE);
		
		return list1;
		
		
		
	}
	
	public List<MbcFichaAnestesias> listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(Integer crgSeq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);

		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.CRG_SEQ.toString(), crgSeq));
		addDtHrMvtoIsNullAndIndPendente(criteria, pendente);

		return executeCriteria(criteria);

	}

	public MbcFichaAnestesias obterMbcFichaAnestesiaByConsulta(
			Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));
		addDtHrMvtoIsNullAndIndPendente(criteria, DominioIndPendenteAmbulatorio.V);
		
		return (MbcFichaAnestesias) executeCriteriaUniqueResult(criteria);
	}

	private void addDtHrMvtoIsNullAndIndPendente(DetachedCriteria criteria, DominioIndPendenteAmbulatorio indPendente) {
		criteria.add(Restrictions.isNull(MbcFichaAnestesias.Fields.DTHR_MVTO.toString()));
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.PENDENTE.toString(), indPendente));
	}

	public List<MbcFichaAnestesias> pesquisarMbcFichaAnestesiasAtdGso(Integer atdSeq, Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.ATD_SEQ.toString(), atdSeq)); 
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.GSO_SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.isNull(MbcFichaAnestesias.Fields.DTHR_MVTO.toString())); 
		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V)); 
		return executeCriteria(criteria);
	}

	public MbcFichaAnestesias obterMbcFichaAnestesiaByMcoGestacao(
			McoGestacoes gestacao, Integer atdSeq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);
		
		//criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.GESTACAO.toString(), gestacao)); 
		
		if(pendente != null){
			criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.PENDENTE.toString(), pendente)); 
		}
		if(atdSeq != null){
			criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.ATD_SEQ.toString(), atdSeq));
		}
		
		criteria.add(Restrictions.isNull(MbcFichaAnestesias.Fields.DTHR_MVTO.toString())); 
		
		return (MbcFichaAnestesias) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MbcFichaAnestesias> listarFichasAnestesiasPorItemSolicExame(Integer soeSeq, Short seqp, DominioIndPendenteAmbulatorio indPendAmbulat, Boolean dthrMvtoNull) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);

		getCriteriaListarFichasAnestesiasPorItemSolicExame(soeSeq, seqp,
				indPendAmbulat, dthrMvtoNull, criteria);

		return executeCriteria(criteria);

	}

	private void getCriteriaListarFichasAnestesiasPorItemSolicExame(
			Integer soeSeq, Short seqp,
			DominioIndPendenteAmbulatorio indPendAmbulat, Boolean dthrMvtoNull,
			DetachedCriteria criteria) {
		if(soeSeq != null){
			criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		}
		if(seqp != null){
			criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.ISE_SEQP.toString(), seqp));
		}
		if(dthrMvtoNull != null){
			if(dthrMvtoNull){
				criteria.add(Restrictions.isNull(MbcFichaAnestesias.Fields.DTHR_MVTO.toString()));
			}
		}
		if(indPendAmbulat != null){
			criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.PENDENTE.toString(), indPendAmbulat));
		}
	}


	
	// @mbcp_carrg_ficha - Só a chamada, deverá ser migrado no futuro.
	public Integer obterFichaAnestesica(final CirurgiaVO crgSelecionada,
			final MbcFichaAnestesias fichaAnestesia,
			final RapServidores servidorLogado) throws ApplicationBusinessException {
		final ArrayList<Integer> retornoLista = new ArrayList<Integer>();
		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.MBCP_CARRG_FICHA;
		
		if (isOracle()) {
			
			AghWork work = new AghWork(servidorLogado != null ?
					servidorLogado.getUsuario() : null) {
				
				@Override
				public void executeAghProcedure(final Connection connection)
						throws SQLException {
					CallableStatement statement = null;
					try {
						final StringBuilder call = new StringBuilder(100).append("{call ");
						call.append(nomeObjeto);
						call.append("(?,?,?,?,?,?,?,?,?,?,?)}");
						
						statement = connection.prepareCall(call.toString());

						CoreUtil.configurarParametroCallableStatement(
								statement, 1, Types.INTEGER, crgSelecionada
										.getPacCodigo() );
						CoreUtil.configurarParametroCallableStatement(
								statement, 2, Types.INTEGER, crgSelecionada
										.getCrgSeq() );
						CoreUtil.configurarParametroCallableStatement(
								statement, 3, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 4, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 5, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 6, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 7, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 8, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 9, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 10, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(
								statement, 11, Types.INTEGER, fichaAnestesia
										.getSeq() == null ? null
										: fichaAnestesia.getSeq().intValue());
						
						// Registro de parametro OUT
						statement.registerOutParameter(11, Types.INTEGER);
						statement.execute();
						retornoLista.add(statement.getInt(11));
						
					}finally {
						if (statement != null) {
							statement.close();
						}
					}
				}
			};
			try {
				this.doWork(work);
			} catch (final Exception e) {
				
				throwExceptionStoredProcedure(nomeObjeto, e,
						crgSelecionada.getPacCodigo(),
						crgSelecionada.getCrgSeq());
				
			}
		}
		if (retornoLista.isEmpty()) {
			return fichaAnestesia.getSeq() == null ? null : fichaAnestesia
					.getSeq().intValue();
		}
		return retornoLista.get(0);
	}

	

	
	/**
	 * Trata exceptions de chamadas a stored procedures.
	 * 
	 * @param storedProcedureName
	 *            nome da stored procedure
	 * @param e
	 *            exception
	 * @param parametros
	 *            parâmetros fornecidos na chamada
	 * @throws ApplicationBusinessException
	 */
	private void throwExceptionStoredProcedure(
			final String storedProcedureName, final Exception e,
			final Object... parametros) throws ApplicationBusinessException {
		final String string = CoreUtil
				.configurarValoresParametrosCallableStatement(parametros);
		LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(
				storedProcedureName, e, true, string));
		throw new ApplicationBusinessException(
				AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
				storedProcedureName, string, CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								storedProcedureName, e, false, string));
	}
	
	
	/**
	 * Web Service #39252 utilizado na estória #864
	 * @param atdSeq
	 * @return MbcFichaAnestesias
	 */
	public String obterPendenciaFichaAnestesia(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class);

		criteria.add(Restrictions.eq(MbcFichaAnestesias.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNull(MbcFichaAnestesias.Fields.DTHR_MVTO.toString()));

		final DominioIndPendenteAmbulatorio[] dominios = {DominioIndPendenteAmbulatorio.R, 
														  DominioIndPendenteAmbulatorio.P, 
														  DominioIndPendenteAmbulatorio.V};
		
		criteria.add(Restrictions.in(MbcFichaAnestesias.Fields.PENDENTE.toString(), dominios));
		
		MbcFichaAnestesias ficha = (MbcFichaAnestesias) executeCriteriaUniqueResult(criteria);
		
		if(ficha != null){
			return ficha.getPendente().toString();
		}
		
		return null;
	}
	
	public List<RapServidores> listarFichasAnestesias(Integer atedimentoSeq, Integer gsoPacCodigo, Short gsoSequence,DominioIndPendenteAmbulatorio pendente, Integer pciSeq, DominioSituacaoExame situacaoProcedimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesias.class, "anestesia");
		criteria.createAlias("anestesia."+MbcFichaAnestesias.Fields.FICHA_PROCEDIMENTOS.toString(), "procedimento");
		criteria.createAlias("anestesia."+MbcFichaAnestesias.Fields.FICHA_EQUIPE_ANESTESIA.toString(), "equipe");
		
		if(atedimentoSeq != null){
			criteria.add(Restrictions.eq("anestesia."+MbcFichaAnestesias.Fields.ATD_SEQ.toString(), atedimentoSeq));
}

		if(gsoPacCodigo != null){
			criteria.add(Restrictions.eq("anestesia."+MbcFichaAnestesias.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		}

		if(gsoSequence != null){
			criteria.add(Restrictions.eq("anestesia."+MbcFichaAnestesias.Fields.GSO_SEQUENCE.toString(), gsoSequence));
		}
		
		
		if(pendente != null){	
			criteria.add(Restrictions.eq("anestesia."+MbcFichaAnestesias.Fields.PENDENTE.toString(),pendente));
		}
		
		if(pciSeq != null){
			criteria.add(Restrictions.eq("procedimento."+MbcFichaProcedimento.Fields.PCI_SEQ.toString(), pciSeq));
		}
		
		if(situacaoProcedimento != null){
			criteria.add(Restrictions.eq("procedimento."+MbcFichaProcedimento.Fields.SITUACAO_PROCEDIMENTO.toString(), situacaoProcedimento));
		}	

		criteria.addOrder(Order.desc("equipe."+MbcFichaEquipeAnestesia.Fields.ANO_RESIDENCIA.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST_VIN_CODIGO.toString()), RapServidores.Fields.VIN_CODIGO.toString())
				.add(Projections.property(MbcFichaEquipeAnestesia.Fields.SERVIDOR_ANEST_MATRICULA.toString()), RapServidores.Fields.MATRICULA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidores.class));
		
		return executeCriteria(criteria);
	}
	
}


