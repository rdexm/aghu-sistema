package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.BuscaPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author dansantos
 *
 */
@Stateless
public class ManutencaoPrescricaoCuidadoRN extends BaseBusiness {


private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

@EJB
private ElaboracaoPrescricaoEnfermagemON elaboracaoPrescricaoEnfermagemON;

private static final Log LOG = LogFactory.getLog(ManutencaoPrescricaoCuidadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;

@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;

@EJB
private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

@Inject
private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -704039700038881405L;
	

	public static enum ManutencaoPrescricaoCuidadoRNExceptionCode implements BusinessExceptionCode {
		EPE_00117, EPE_00118, EPE_00119, EPE_00232, EPE_00243, EPE_00244, EPE_00237, EPE_00238, 
		EPE_00281, EPE_00282, EPE_00291, EPE_00293, EPE_00292, EPE_00294, EPE_00296, EPE_00297, EPE_00299
	}
	
//	/**
//	 * ORADB
//	 * Trigger EPET_PRC_BRU
//	 * @throws ApplicationBusinessException 
//	 *  
//	 */
//	public void atualizarPrescricaoCuidado(EpePrescricoesCuidados prescricaoCuidado, EpePrescricoesCuidados prescricaoCuidadoOld) throws BaseException {
//	
//		 /*
//		 ESTA PROCEDURE ESTÁ SENDO AVALIADO PELO FRED SE SERÁ NECESSÁRIA
//		 
//		 IF aghk_util.modificados (:old.dthr_inicio, :new.dthr_inicio)
//		 OR aghk_util.modificados (:old.dthr_fim, :new.dthr_fim)
//		 OR  aghk_util.modificados (:old.atd_seq, :new.atd_seq)
//		 THEN
//		       epek_rn.rn_epep_ver_atend
//		                                 (:new.dthr_inicio,
//		                                  :new.dthr_fim,
//		                                  :new.atd_seq);
//		 END IF;
//		 */
//		 if(!prescricaoCuidado.getTipoFrequenciaAprazamento().equals(prescricaoCuidadoOld.getTipoFrequenciaAprazamento()) || !prescricaoCuidado.getFrequencia().equals(prescricaoCuidadoOld.getFrequencia())){
//			 Short tipoFrequenciaAprazamentoSeq = null;
//			 if(prescricaoCuidado.getTipoFrequenciaAprazamento()!=null){
//				tipoFrequenciaAprazamentoSeq = prescricaoCuidado.getTipoFrequenciaAprazamento().getSeq();
//			}
//			 this.verificarDigitacaoFrequencia(tipoFrequenciaAprazamentoSeq, prescricaoCuidado.getFrequencia());
//		 }
//		 if(!prescricaoCuidado.getCuidado().equals(prescricaoCuidadoOld.getCuidado()) || 
//			!prescricaoCuidado.getCriadoEm().equals(prescricaoCuidadoOld.getCriadoEm()) ||
//			!prescricaoCuidado.getDthrFim().equals(prescricaoCuidadoOld.getDthrFim())||
//			!prescricaoCuidado.getIndPendente().equals(prescricaoCuidadoOld.getIndPendente())){
//			 Integer seqAtendimento = null;
//			 if(prescricaoCuidado.getPrescricaoEnfermagem()!=null && prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento()!=null){
//				 seqAtendimento = prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento().getSeq();
//			 }
//			 this.verificarPrescricaoEnfermagemUpdate(seqAtendimento, prescricaoCuidado.getDthrInicio(), prescricaoCuidado.getDthrFim(), prescricaoCuidado.getCriadoEm(), prescricaoCuidado.getIndPendente(), DominioOperacaoBanco.UPD);
//		 }
//		 
//		 /*
//		 
//		 ESTA PROCEDURE NAO PRECISA SER IMPLEMENTADA
//
//		 IF aghk_util.modificados (:old.dthr_fim, :new.dthr_fim) AND
//		    :new.dthr_fim IS NOT NULL
//		 THEN
//		       epek_prc_rn.rn_prcp_atu_servidor
//			(:new.ser_matricula_movimentado,
//			:new.ser_vin_codigo_movimentado);
//		 END IF;
//		 */
//
//		 if(!prescricaoCuidado.getCuidado().equals(prescricaoCuidadoOld.getCuidado()) || !prescricaoCuidado.getDescricao().equals(prescricaoCuidadoOld.getDescricao())){
//			 Short cuiSeq = null;
//			 if(prescricaoCuidado.getCuidado()!=null){
//				 cuiSeq = prescricaoCuidado.getCuidado().getSeq(); 
//			 }
//			 this.verificarDescricaoCuidado(cuiSeq, prescricaoCuidado.getDescricao());
//		 }
//
//
//		 /*
//		 
//		 ESTA PROCEDURE NAO PRECISA SER IMPLEMENTADA
//		 
//		 
//		 IF  aghk_util.modificados(:old.dthr_inicio, :new.dthr_inicio)
//		 OR  aghk_util.modificados(:old.dthr_fim,    :new.dthr_fim)
//		 THEN
//		   IF :new.dthr_fim IS NOT NULL
//		   THEN
//		      epek_prc_rn.rn_prcp_ver_dthr
//							(:new.dthr_inicio,
//							 :new.dthr_fim,
//							 'A');
//		   END IF;
//		 END IF;
//		 */
//		 
//
//		  /*
//		    ESTA PROCEDURE NAO PRECISA SER IMPLEMENTADA
//		 
//		 IF aghk_util.modificados(:old.dthr_fim, :new.dthr_fim)  AND
//		    :new.dthr_fim IS NOT NULL
//		 THEN
//		    IF :new.ind_pendente = 'P'
//		       AND :old.ind_pendente = 'P'
//		    THEN
//		         NULL;
//		    ELSE
//		       BEGIN
//		         OPEN c_dual;
//		         FETCH c_dual
//		         INTO  l_sysdate
//		         ,     l_user;
//		         CLOSE c_dual;
//		         :new.ALTERADO_EM := l_sysdate;
//		       END;
//		    END IF;
//		 END IF;
//		END;
//		*/
//		 this.getEpePrescricoesCuidados().atualizar(prescricaoCuidado, true);
//	}
	
//	/**
//	 * 	Triggger
//	 * ORADB EPET_PRC_BRI
//	 * @param prescricaoCuidado
//	 * @throws ApplicationBusinessException
//	 */
//	public void inserirPrescricaoCuidado(EpePrescricoesCuidados prescricaoCuidado) throws BaseException {
//		/*	 ESTAS PROCEDURES ESTAO SENDO AVALIADAS PELO FRED SE SERÁ NECESSÁRIA
//	     
//		   epek_rn.rn_epep_ver_atend   (:new.dthr_inicio,
//		                                  :new.dthr_fim,
//		                                  :new.atd_seq);
//		   epek_prc_rn.rn_prcp_ver_autorel
//		                                 (:new.prc_atd_seq,
//		                                  :new.prc_seq,
//		                                  :new.ind_pendente);*/
//		Short tfqSeq = null;
//		if(prescricaoCuidado.getTipoFrequenciaAprazamento()!=null){
//			tfqSeq = prescricaoCuidado.getTipoFrequenciaAprazamento().getSeq();
//		}
//		this.verificarDigitacaoFrequencia(tfqSeq, prescricaoCuidado.getFrequencia());
//		
//		Short cuiSeq = null;
//		 if(prescricaoCuidado.getCuidado()!=null){
//			 cuiSeq = prescricaoCuidado.getCuidado().getSeq(); 
//		 }
//		this.verificarDescricaoCuidado(cuiSeq, prescricaoCuidado.getDescricao());
//		Integer atdSeq = null;
//		if(prescricaoCuidado.getPrescricaoEnfermagem()!=null && prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento()!=null){
//			atdSeq = prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento().getSeq();
//		}
//		this.verificarPrescricaoEnfermagemInsert(atdSeq, prescricaoCuidado.getDthrInicio(), prescricaoCuidado.getDthrFim(), prescricaoCuidado.getCriadoEm(), prescricaoCuidado.getIndPendente(), DominioOperacaoBanco.INS);
//		this.getEpePrescricoesCuidados().inserir(prescricaoCuidado, true);
//	}
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PRC_RN.RN_PRCP_VER_FREQUENC
	 */
	@SuppressWarnings("ucd")
	public void verificarDigitacaoFrequencia(Short seqTipoFrequenciaAprazamento,
			Short frequencia) throws ApplicationBusinessException {
		if (seqTipoFrequenciaAprazamento != null) {
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = getPrescricaoMedicaFacade().obterTipoFrequenciaAprazamentoId(seqTipoFrequenciaAprazamento);
			if (tipoFrequenciaAprazamento != null) {
				if(!tipoFrequenciaAprazamento.getIndSituacao().equals(DominioSituacao.A)){
					throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00117);
				}
				if (tipoFrequenciaAprazamento.getIndDigitaFrequencia() && (frequencia == null || frequencia == 0)) {
					throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00118);
				} else if (!tipoFrequenciaAprazamento.getIndDigitaFrequencia()
						&& (frequencia != null && frequencia > 0)) {
					throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00119);
				}
			}
		}
	}
	
	
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PRC_RN.RN_PRCP_VER_PR_ENF_U
	 *  
	 */
	public void verificarPrescricaoEnfermagemUpdate(Integer seqAtendimento,
			Date dthrInicio, Date dthrFim, Date dthrMotivoPendente,
			DominioIndPendentePrescricoesCuidados pendente,
			DominioOperacaoBanco operacao)
			throws ApplicationBusinessException {
		BuscaPrescricaoEnfermagemVO buscaPrescricaoEnfermagemVO = obterPrescricaoEnfermagem(
				seqAtendimento, dthrInicio, dthrFim, operacao);

		if (buscaPrescricaoEnfermagemVO.getDataHoraInicio() == null
				&& buscaPrescricaoEnfermagemVO.getDataHoraFim() == null
				&& buscaPrescricaoEnfermagemVO.getSituacao() == null) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00243);
		}

		if (buscaPrescricaoEnfermagemVO.getDataHoraMovimentoPendente() == null
				&& !DominioIndPendentePrescricoesCuidados.N.equals(pendente)) {
			try {
				EpePrescricaoEnfermagemId id = new EpePrescricaoEnfermagemId();
				id.setSeq(buscaPrescricaoEnfermagemVO.getSeqPrescricaoEnfermagem());
				id.setAtdSeq(seqAtendimento);

				EpePrescricaoEnfermagem prescricaoEnfermagem = getEpePrescricaoEnfermagemDAO()
				.obterPorChavePrimaria(id);

				if (prescricaoEnfermagem != null) {
					EpePrescricaoEnfermagem prescricaoEnfermagemOld = getPrescricaoEnfermagemFacade().clonarPrescricaoEnfermagem(prescricaoEnfermagem);

					prescricaoEnfermagem
					.setDthrInicioMvtoPendente(buscaPrescricaoEnfermagemVO
							.getDataHoraMovimento());
					
					this.getElaboracaoPrescricaoEnfermagemON().atualizarPrescricaoEnfermagem(prescricaoEnfermagemOld, prescricaoEnfermagem, true);
				}
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00244);
			}
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PRC_RN.RN_PRCP_VER_PR_ENF_I
	 *  
	 */
	public void verificarPrescricaoEnfermagemInsert(Integer seqAtendimento,
			Date dthrInicio, Date dthrFim, Date dthrMotivoPendente,
			DominioIndPendentePrescricoesCuidados pendente,
			DominioOperacaoBanco operacao)
			throws ApplicationBusinessException {
		BuscaPrescricaoEnfermagemVO buscaPrescricaoEnfermagemVO = obterPrescricaoEnfermagem(
				seqAtendimento, dthrInicio, dthrFim, operacao);

		if(DateUtil.validaDataMaior(dthrFim, buscaPrescricaoEnfermagemVO.getDataHoraFim()) && dthrFim!=null 
				&& buscaPrescricaoEnfermagemVO.getDataHoraInicio() == null
				&& buscaPrescricaoEnfermagemVO.getDataHoraFim() == null
				&& buscaPrescricaoEnfermagemVO.getSituacao() == null){
			throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00237);
		}

		if (buscaPrescricaoEnfermagemVO.getDataHoraMovimentoPendente() == null
				&& !DominioIndPendentePrescricoesCuidados.N.equals(pendente)) {
			try {
				EpePrescricaoEnfermagemId id = new EpePrescricaoEnfermagemId();
				id.setSeq(buscaPrescricaoEnfermagemVO.getSeqPrescricaoEnfermagem());
				id.setAtdSeq(seqAtendimento);

				EpePrescricaoEnfermagem prescricaoEnfermagem = getEpePrescricaoEnfermagemDAO()
				.obterPorChavePrimaria(id);
				
				if (prescricaoEnfermagem != null) {
					EpePrescricaoEnfermagem prescricaoEnfermagemOld = getPrescricaoEnfermagemFacade().clonarPrescricaoEnfermagem(prescricaoEnfermagem);
					
					prescricaoEnfermagem
					.setDthrInicioMvtoPendente(buscaPrescricaoEnfermagemVO
							.getDataHoraMovimento());
					
					this.getElaboracaoPrescricaoEnfermagemON().atualizarPrescricaoEnfermagem(prescricaoEnfermagemOld, prescricaoEnfermagem, true);
				}
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00238);
			}
		}
	}
	
	/**
	 * ORADB Procedure EPEP_GET_PRCR_ENF
	 * 
	 * Busca a prescrição enfermagem conforme atendimento
	 * e data informado, retornando os valores dos campos
	 * dthr_inicio, dthr_fim, ind_situacao, seq e dthr_inicio_mvto_pendente.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public BuscaPrescricaoEnfermagemVO obterPrescricaoEnfermagem(
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim,
			DominioOperacaoBanco operacao)
	throws ApplicationBusinessException {
		BuscaPrescricaoEnfermagemVO buscaPrescricaoEnfermagemVO = new BuscaPrescricaoEnfermagemVO();

		if (dataHoraFim == null || dataHoraInicio.equals(dataHoraFim)) {
			List<EpePrescricaoEnfermagem> listaPrescricoes = getEpePrescricaoEnfermagemDAO()
			.listarPrescricoesEnfermagemInicio(seqAtendimento,
					dataHoraInicio);

			if (listaPrescricoes != null
					&& !listaPrescricoes.isEmpty()) {
				// Pega o primeiro elemento, conforme lógica do cursor do oracle
				EpePrescricaoEnfermagem prescricaoEnfermagem = listaPrescricoes.get(0);
				
				buscaPrescricaoEnfermagemVO.setDataHoraInicio(prescricaoEnfermagem
						.getDthrInicio());
				buscaPrescricaoEnfermagemVO.setDataHoraFim(prescricaoEnfermagem
						.getDthrFim());
				buscaPrescricaoEnfermagemVO.setSituacao(prescricaoEnfermagem
						.getSituacao());
				buscaPrescricaoEnfermagemVO.setSeqPrescricaoEnfermagem(prescricaoEnfermagem
						.getId().getSeq());
				buscaPrescricaoEnfermagemVO
				.setDataHoraMovimentoPendente(prescricaoEnfermagem
						.getDthrInicioMvtoPendente());
				buscaPrescricaoEnfermagemVO.setDataHoraMovimento(prescricaoEnfermagem
						.getDthrMovimento());
				buscaPrescricaoEnfermagemVO.setDataReferencia(prescricaoEnfermagem
						.getDtReferencia());
			}
		} else {
			Date auxDataFim = null;
			if (DominioOperacaoBanco.INS.equals(operacao)) {
				List<EpePrescricaoEnfermagem> listaPrescricoes = getEpePrescricaoEnfermagemDAO()
				.listarPrescricoesEnfermagemInicio(seqAtendimento,
						dataHoraInicio);

				if (listaPrescricoes != null
						&& !listaPrescricoes.isEmpty()) {
					// Pega o primeiro elemento, conforme lógica do cursor do
					// oracle
					EpePrescricaoEnfermagem prescricaoEnfermagem = listaPrescricoes
					.get(0);
					auxDataFim = prescricaoEnfermagem.getDthrFim();

					buscaPrescricaoEnfermagemVO.setDataHoraInicio(prescricaoEnfermagem
							.getDthrInicio());
					buscaPrescricaoEnfermagemVO.setDataHoraFim(prescricaoEnfermagem
							.getDthrFim());
					buscaPrescricaoEnfermagemVO.setSituacao(prescricaoEnfermagem
							.getSituacao());
					buscaPrescricaoEnfermagemVO
					.setSeqPrescricaoEnfermagem(prescricaoEnfermagem.getId()
							.getSeq());
					buscaPrescricaoEnfermagemVO
					.setDataHoraMovimentoPendente(prescricaoEnfermagem
							.getDthrInicioMvtoPendente());
					buscaPrescricaoEnfermagemVO
					.setDataHoraMovimento(prescricaoEnfermagem
							.getDthrMovimento());
					buscaPrescricaoEnfermagemVO.setDataReferencia(prescricaoEnfermagem
							.getDtReferencia());
				}
			}

			if (DominioOperacaoBanco.UPD.equals(operacao)
					|| (auxDataFim != null && dataHoraFim.after(auxDataFim))) {
				List<EpePrescricaoEnfermagem> listaPrescricoes = getEpePrescricaoEnfermagemDAO()
				.listarPrescricoesEnfermagemFim(seqAtendimento,
						dataHoraFim);

				if (listaPrescricoes != null
						&& !listaPrescricoes.isEmpty()) {
					// Pega o primeiro elemento, conforme lógica do cursor do
					// oracle
					EpePrescricaoEnfermagem prescricaoEnfermagem = listaPrescricoes
					.get(0);

					buscaPrescricaoEnfermagemVO.setDataHoraInicio(prescricaoEnfermagem
							.getDthrInicio());
					buscaPrescricaoEnfermagemVO.setDataHoraFim(prescricaoEnfermagem
							.getDthrFim());
					buscaPrescricaoEnfermagemVO.setSituacao(prescricaoEnfermagem
							.getSituacao());
					buscaPrescricaoEnfermagemVO
					.setSeqPrescricaoEnfermagem(prescricaoEnfermagem.getId()
							.getSeq());
					buscaPrescricaoEnfermagemVO
					.setDataHoraMovimentoPendente(prescricaoEnfermagem
							.getDthrInicioMvtoPendente());
					buscaPrescricaoEnfermagemVO
					.setDataHoraMovimento(prescricaoEnfermagem
							.getDthrMovimento());
					buscaPrescricaoEnfermagemVO.setDataReferencia(prescricaoEnfermagem
							.getDtReferencia());
				}
			}
		}

		return buscaPrescricaoEnfermagemVO;
	}
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PRC_RN.RN_PRCP_VER_DIG_COMP
	 *  
	 */
	public void verificarDescricaoCuidado(EpePrescricoesCuidados prescricaoCuidado) throws ApplicationBusinessException {
		EpeCuidados cuidado = prescricaoCuidado.getCuidado();
		
		if(cuidado!=null && cuidado.getIndDigitaComplemento() && StringUtils.isBlank(cuidado.getDescricao())) {
			throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00232);
		}
	}
	
	/**
	 * Executa a rotina de cancelamento de Prescrição de Enfermagem.
	 * 
	 * PROCEDURE
	 * ORADB EPEK_CANCELA.EPEP_CANCELA
	 * @throws ApplicationBusinessException
	 */
	public void cancelaPrescricaoEnfermagem(Integer penSeqAtendimento, Integer penSeq) throws ApplicationBusinessException {
		EpePrescricaoEnfermagemId prescricaoId;
		EpePrescricaoEnfermagem prescricaoEnfermagem;
		
		try {
			prescricaoId = new EpePrescricaoEnfermagemId(penSeqAtendimento, penSeq);
			prescricaoEnfermagem = getEpePrescricaoEnfermagemDAO().obterPorChavePrimaria(prescricaoId);
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00281);
		}
		
		if(prescricaoEnfermagem.getSituacao()!=DominioSituacaoPrescricao.U) {
			throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00282);
		}
		if(prescricaoEnfermagem.getDthrMovimento()==null) {
			throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00291);
		}
		
		 /*	penSeqAtendimento contem o atd_seq.
		  	DthrMovimento contem a data e hora a partir dos quais se deve fazer o cancelamento.
		  	DthrInicio contem a data e hora de inicio da prescricao de enfermagem.
		  	DthrFim contem a data e hora de fim da prescricao de enfermagem.
		 */
		
		this.cancelaCuidadosPrescricaoEnfermagem(penSeqAtendimento, penSeq, prescricaoEnfermagem.getDthrMovimento(), 
				prescricaoEnfermagem.getDthrInicio(), prescricaoEnfermagem.getDthrFim());
		
		this.cancelaUsoPrescricaoEnfermagem(penSeqAtendimento, penSeq, prescricaoEnfermagem.getServidorValida());	
		
		super.flush();
		
	}
	
	/**
	 * Atualiza os dados dos cuidados do paciente após o cancelamento.
	 * 
	 * PROCEDURE
	 * ORADB EPEK_CANCELA.EPEP_CANCELA_CUID (Modificada)
	 *  
	 */
	public void cancelaCuidadosPrescricaoEnfermagem(Integer penSeqAtendimento, Integer penSeq, Date dthrMovimento, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {
		
		List<EpePrescricoesCuidados> listaCuidados = getEpePrescricoesCuidadosDAO().pesquisarPrescricoesCuidadosPorPrescricaoEnfermagemMovimentoDataInicioFim(penSeqAtendimento, penSeq, dthrMovimento, dthrInicio, dthrFim);
		for(EpePrescricoesCuidados cuidado : listaCuidados) {
			if(cuidado.getPendente()==DominioIndPendentePrescricoesCuidados.A) {
				cuidado.setPendente(DominioIndPendentePrescricoesCuidados.N);
				cuidado.setDthrFim(dthrFim);
				cuidado.setServidorMovimentado(null);
				cuidado.setAlteradoEm(null);
				try {
					getEpePrescricoesCuidadosDAO().atualizar(cuidado);
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00296);
				}
			} else if(cuidado.getPendente()==DominioIndPendentePrescricoesCuidados.E) {
				cuidado.setPendente(DominioIndPendentePrescricoesCuidados.N);
				cuidado.setDthrFim(dthrFim);
				cuidado.setServidorMovimentado(null);
				cuidado.setAlteradoEm(null);
				try {
					getEpePrescricoesCuidadosDAO().atualizar(cuidado);
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00297);
				}
			} else if(cuidado.getPendente()==DominioIndPendentePrescricoesCuidados.P) {
				try {
					List<EpePrescCuidDiagnostico> listaCuidadosDiagnostico = getEpePrescCuidDiagnosticoDAO().listarPrescCuidDiagnosticoPorPrescricaoCuidado(cuidado);
					for(EpePrescCuidDiagnostico cuidadoDiagnostico : listaCuidadosDiagnostico) {
						getEpePrescCuidDiagnosticoDAO().remover(cuidadoDiagnostico);
						getEpePrescCuidDiagnosticoDAO().flush();
					}
					getEpePrescricoesCuidadosDAO().remover(cuidado);
					getEpePrescricoesCuidadosDAO().flush();
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00299);
				}
			}

		}	
	}
	
	/**
	 * Tira uso da Prescrição de Enfermagem.
	 * 
	 * PROCEDURE
	 * ORADB EPEK_CANCELA.EPEP_CANC_TIRA_USO
	 *  
	 */
	public void cancelaUsoPrescricaoEnfermagem(Integer penSeqAtendimento, Integer penSeq, RapServidores servidorValida) throws ApplicationBusinessException {
		Boolean cancelarAtualizando = false;
		
		EpePrescricaoEnfermagemId prescricaoId = new EpePrescricaoEnfermagemId(penSeqAtendimento, penSeq);
		EpePrescricaoEnfermagem prescricaoEnfermagem = this.getEpePrescricaoEnfermagemDAO().obterPorChavePrimaria(prescricaoId);
		EpePrescricaoEnfermagem prescricaoEnfermagemOld = getPrescricaoEnfermagemFacade().clonarPrescricaoEnfermagem(prescricaoEnfermagem);
		
		Date dthrInicioMvtoPendente = prescricaoEnfermagem.getDthrInicioMvtoPendente();
		Date dthrMovimento = prescricaoEnfermagem.getDthrMovimento();
		
		if(dthrInicioMvtoPendente==null && dthrMovimento==null) {
			throw new ApplicationBusinessException(ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00293); 
		}
		
		
		if (servidorValida != null ||
				(dthrInicioMvtoPendente != null &&
						dthrMovimento.compareTo(dthrInicioMvtoPendente) > 0)){
			cancelarAtualizando = true;
		}
		
		
		if(!cancelarAtualizando) {
			try {
				// Remove Prescrição de Enfermagem
				this.getEpePrescricaoEnfermagemDAO().remover(prescricaoEnfermagem);
				this.getEpePrescricaoEnfermagemDAO().flush();
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(
						ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00292);
			}
		}
		else {
			Date novaDthrInicioMvtoPendente;
			if(dthrInicioMvtoPendente==dthrMovimento) {
				novaDthrInicioMvtoPendente = null;
			}
			else {
				novaDthrInicioMvtoPendente = dthrInicioMvtoPendente;
			}
			// Atualiza Prescrição de Enfermagem
			prescricaoEnfermagem.setDthrMovimento(null);
			prescricaoEnfermagem.setDthrInicioMvtoPendente(novaDthrInicioMvtoPendente);
			prescricaoEnfermagem.setSituacao(DominioSituacaoPrescricao.L);
			try {
				this.getElaboracaoPrescricaoEnfermagemON().atualizarPrescricaoEnfermagem(prescricaoEnfermagemOld, prescricaoEnfermagem, true);
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(
						ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00294);
			}
		}
	}
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO() {
		return epePrescricaoEnfermagemDAO;
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO() {
		return epePrescricoesCuidadosDAO;
	}
	
	protected EpePrescCuidDiagnosticoDAO getEpePrescCuidDiagnosticoDAO() {
		return epePrescCuidDiagnosticoDAO;
	}
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}		
	
	protected ElaboracaoPrescricaoEnfermagemON getElaboracaoPrescricaoEnfermagemON() {
		return elaboracaoPrescricaoEnfermagemON;
	}
	
}
