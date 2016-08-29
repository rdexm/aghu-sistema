package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelacionadoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ElaboracaoPrescricaoEnfermagemRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ElaboracaoPrescricaoEnfermagemRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private EpeDiagnosticoDAO epeDiagnosticoDAO;
	
	@Inject
	private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private EpeFatRelacionadoDAO epeFatRelacionadoDAO;
	
	@Inject
	private EpeFatRelDiagnosticoDAO epeFatRelDiagnosticoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	private static final long serialVersionUID = -3990547940789469106L;
	
	public enum PrescricaoEnfermagemRNExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_DIAGNOSTICO, EPE_00307, EPE_00104, EPE_00106, EPE_00110, EPE_00111, EPE_00198, EPE_00199, EPE_00200, EPE_00226, EPE_00227, ERRO_PRESCRICAO_ADIANTADA_DIAS,
		ERRO_PRESCRICAO_ADIANTADA_HORAS, EPE_00190;
	}

	/**
	 * PROCEDURE
	 * 
	 * ORADB EPEP_ENFORCE_PEN_RULES
	 * @param prescricaoEnfermagem
	 * @param prescricaoEnfermagemOld
	 * @param operacao
	 */
	public void validarPrescricaoEnfermagemEnforce(EpePrescricaoEnfermagem prescricaoEnfermagem, EpePrescricaoEnfermagem prescricaoEnfermagemOld, DominioOperacaoBanco operacao) throws BaseException {
		if(operacao.equals(DominioOperacaoBanco.UPD)){
			if(prescricaoEnfermagemOld.getSituacao().equals(DominioSituacaoPrescricao.U) && 
				prescricaoEnfermagem.getSituacao().equals(DominioSituacaoPrescricao.L) &&
				prescricaoEnfermagemOld.getServidorValida()==null && prescricaoEnfermagem.getServidorValida()!=null) {
					/*epep_gera_pista_mci (l_pen_row_new.atd_seq,
                            l_pen_row_new.seq,
		                   l_pen_row_new.dthr_inicio,
   		             l_pen_row_new.dthr_fim);*/
					this.inserirMvtoProcedimentoRiscos(prescricaoEnfermagem.getId().getAtdSeq(), prescricaoEnfermagem.getId().getSeq(),
							prescricaoEnfermagem.getDthrInicio(), 	prescricaoEnfermagem.getDthrFim());	
					
					/*IF v_unidade_checagem THEN */
					/*if (this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(prescricaoEnfermagem.getAtendimento().getUnidadeFuncional().getSeq(),
															ConstanteAghCaractUnidFuncionais.CHECAGEM_ELETRONICA_ENFERMAGEM)
						||	prescricaoEnfermagem.getAtendimento().getInternacao().getConvenioSaude().getCodigo()>1){
						
						/ * epep_gera_ordem_ece (l_pen_row_new.atd_seq, --mar  l_pen_row_new.seq,l_pen_row_new.dthr_inicio,
				        l_pen_row_new.dthr_fim,  l_pen_row_new.dt_referencia); * /
						this.inserirEceOrdemLocalizacao(prescricaoEnfermagem.getId().getAtdSeq(), prescricaoEnfermagem.getId().getSeq(),
							prescricaoEnfermagem.getDthrInicio() , prescricaoEnfermagem.getDthrFim(),prescricaoEnfermagem.getDtReferencia()); 
						
						
						/ * epep_gera_item_ece  (l_pen_row_new.atd_seq, --mar    l_pen_row_new.seq,
								             l_pen_row_new.dthr_inicio,   l_pen_row_new.dthr_fim,l_pen_row_new.dt_referencia); * /
						
						//		this.inserirEceItemAdministrados(new Integer(12748231), new Integer(3807815),
						//				dtInicio ,dtFim, new Date(), servidorLogado); 

						this.inserirEceItemAdministrados(prescricaoEnfermagem.getId().getAtdSeq(), prescricaoEnfermagem.getId().getSeq(),
								prescricaoEnfermagem.getDthrInicio() , 	prescricaoEnfermagem.getDthrFim(), prescricaoEnfermagem.getDtReferencia()); 
					}*/
				
					this.gerarDiagnosticosEnfermagem(prescricaoEnfermagem.getAtendimento().getSeq());
				}

				if (prescricaoEnfermagemOld.getServidorValida()!=null && prescricaoEnfermagem.getServidorValida()!=null
						&& prescricaoEnfermagemOld.getDthrInicioMvtoPendente()!=null && 
						prescricaoEnfermagem.getDthrInicioMvtoPendente()==null) {
					
					/*epep_gera_pista_mci (l_pen_row_new.atd_seq, l_pen_row_new.seq, l_pen_row_new.dthr_inicio,
			             l_pen_row_new.dthr_fim);*/
					this.inserirMvtoProcedimentoRiscos(prescricaoEnfermagem.getAtendimento().getSeq(), prescricaoEnfermagem.getId().getSeq(),
						prescricaoEnfermagem.getDthrInicio() , 	prescricaoEnfermagem.getDthrFim());	
				
					
					/*IF v_unidade_checagem THEN */
					if (this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(prescricaoEnfermagem.getAtendimento().getUnidadeFuncional().getSeq(),
															ConstanteAghCaractUnidFuncionais.CHECAGEM_ELETRONICA_ENFERMAGEM)
						|| 	prescricaoEnfermagem.getAtendimento().getInternacao().getConvenioSaude().getCodigo()>1){
							
						/*	 epep_gera_mvto_ece (l_pen_row_new.atd_seq, --mar  l_pen_row_new.seq, l_pen_saved_row.dthr_inicio_mvto_pendente,
			 			  l_pen_saved_row.dthr_movimento,  l_pen_row_new.dthr_inicio,  l_pen_row_new.dthr_fim,
						  l_pen_row_new.dt_referencia); */
						this.gerarMvtoEnfermagemChecagemEletronica(prescricaoEnfermagem.getId().getAtdSeq(),
																	prescricaoEnfermagem.getId().getSeq(),
																	prescricaoEnfermagem.getDthrInicioMvtoPendente(),
																	prescricaoEnfermagem.getDthrMovimento(),
																	prescricaoEnfermagem.getDthrInicio(),
																	prescricaoEnfermagem.getDthrFim(),
																	prescricaoEnfermagem.getDtReferencia());
					}
					
		          this.gerarDiagnosticosEnfermagem(prescricaoEnfermagem.getAtendimento().getSeq());
				}
			} //Fim do Update
	
		if (operacao.equals(DominioOperacaoBanco.INS) &&
				prescricaoEnfermagem.getSituacao().equals(DominioSituacaoPrescricao.L)){
				/*epep_gera_pista_mci (l_pen_row_new.atd_seq, l_pen_row_new.seq, l_pen_row_new.dthr_inicio,
		             l_pen_row_new.dthr_fim);*/
				this.inserirMvtoProcedimentoRiscos(prescricaoEnfermagem.getId().getAtdSeq(), prescricaoEnfermagem.getId().getSeq(),
					prescricaoEnfermagem.getDthrInicio() , 	prescricaoEnfermagem.getDthrFim());	
				/*IF v_unidade_checagem THEN */
				if (this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(prescricaoEnfermagem.getAtendimento().getUnidadeFuncional().getSeq(),
														ConstanteAghCaractUnidFuncionais.CHECAGEM_ELETRONICA_ENFERMAGEM)
					&& 	prescricaoEnfermagem.getAtendimento().getInternacao().getConvenioSaude().getCodigo()>1){
					
					/* epep_gera_ordem_ece (l_pen_row_new.atd_seq, --mar  l_pen_row_new.seq,l_pen_row_new.dthr_inicio,
			        l_pen_row_new.dthr_fim,  l_pen_row_new.dt_referencia); */
					this.inserirEceOrdemLocalizacao(prescricaoEnfermagem.getId().getAtdSeq(), prescricaoEnfermagem.getId().getSeq(),
						prescricaoEnfermagem.getDthrInicio() , 	prescricaoEnfermagem.getDthrFim(), prescricaoEnfermagem.getDtReferencia()); 
					
					/* epep_gera_item_ece  (l_pen_row_new.atd_seq, --mar    l_pen_row_new.seq,
							             l_pen_row_new.dthr_inicio,   l_pen_row_new.dthr_fim,l_pen_row_new.dt_referencia); */
					
					this.inserirEceItemAdministrados(prescricaoEnfermagem.getId().getAtdSeq(), prescricaoEnfermagem.getId().getSeq(),
							prescricaoEnfermagem.getDthrInicio() , 	prescricaoEnfermagem.getDthrFim(), prescricaoEnfermagem.getDtReferencia()); 
				}
				
				this.gerarDiagnosticosEnfermagem(prescricaoEnfermagem.getAtendimento().getSeq());
			}
			
		}

	/**
	 * @ORADB chamada nativa EPEP_GERA_ITEM_ECE
	 * 
	 *   insert ECE_ITEM_ADMINISTRADOS
	 *   Chama EPEP_INCLUI_ITEM_ECE
	 * 
	 * @param atdSeq
	 * @param seq (EpePrescEnfermagem)
	 * @param dthr_inicio, dthr_fim, dt_referencia
	 * @param servidorLogado
	 * @throws AGHUNegocioException
	 */
	public void  inserirEceItemAdministrados(Integer atdSeq, Integer seq, Date dthInicio, Date dthFim, 
			Date dtReferencia) 	throws ApplicationBusinessException	{
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		this.getObjetosOracleDAO().inserirEceItemAdministrados(atdSeq, seq, dthInicio, 	dthFim, 
																	dtReferencia, servidorLogado.getUsuario()); 						
				
	}
	
	/**
	 * @ORADB chamada nativa EPEP_GERA_MVTO_ECE
	 * 
	 *        update ECE_ITEM_ADMINISTRADOS, ECE_HORARIO_ADMINISTRADOS,
	 *        ECE_TURNO_ITEM_ADMINISTRADOS, ECE_ITEM_ADMINISTRADOS Chama
	 *        EPEP_INCLUI_ITEM_ECE
	 * 
	 * @param atdSeq
	 * @param seq
	 *            (EpePrescEnfermagem)
	 * @param dthr_inicio_mvto_pendente
	 * @param dthr_movimento
	 * @param dthr_inicio
	 *            , dthr_fim, dt_referencia
	 * @param servidorLogado
	 * @throws AGHUNegocioException
	 */
	public void gerarMvtoEnfermagemChecagemEletronica(Integer atdSeq,
			Integer seq, Date dthIniMvtoPendente, Date dthMovimento,
			Date dthInicio, Date dthFim, Date dtReferencia)
			throws ApplicationBusinessException {

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		this.getObjetosOracleDAO().gerarMvtoEnfermagemChecagemEletronica(
				atdSeq, seq, dthIniMvtoPendente, dthMovimento, dthInicio,
				dthFim, dtReferencia, servidorLogado.getUsuario());

	}

	/**
	 * @ORADB chamada nativa EPEP_GERA_PISTA_MCI
	 * 
	 *        insere na tabela MCI_MVTO_PROCEDIMENTO_RISCOS, MCI_PISTAS
	 * 
	 * @param atdSeq
	 * @param seq
	 *            (EpePrescEnfermagem)
	 * @param dthr_inicio
	 *            , dthr_fim
	 * @param servidorLogado
	 * @throws AGHUNegocioException
	 */

	public void inserirMvtoProcedimentoRiscos(Integer atdSeq, Integer seq,
			Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		this.getObjetosOracleDAO().inserirMvtoProcedimentoRiscos(atdSeq, seq,
				dthrInicio, dthrFim, servidorLogado.getUsuario());

	}

	/**
	 * @ORADB chamada nativa EPEP_GERA_ORDEM_ECE
	 * 
	 *        insere na tabela ECE_ORDEM_DE_ADMINISTRACOES,
	 *        ECE_ORDEM_X_LOCALIZACAO,
	 * 
	 * @param atdSeq
	 * @param seq
	 *            (EpePrescEnfermagem)
	 * @param dthr_inicio
	 *            , dthr_fim
	 * @param servidorLogado
	 * @throws AGHUNegocioException
	 */

	public void inserirEceOrdemLocalizacao(Integer atdSeq, Integer seq,
			Date dthrInicio, Date dthrFim, Date dtReferencia) throws ApplicationBusinessException {

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		this.getObjetosOracleDAO().inserirEceOrdemLocalizacao(atdSeq, seq,
				dthrInicio, dthrFim, dtReferencia, servidorLogado.getUsuario());

	}

	/**
	 * PROCEDURE
	 * 
	 * EPEP_GERA_MAM_DIAG
	 * 
	 * @param atdSeq
	 *  
	 */
	public void gerarDiagnosticosEnfermagem(Integer atdSeq) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<DiagnosticoEtiologiaVO> listaPrescCuidDiagnostico = this.getEpePrescCuidDiagnosticoDAO().listarGerarMamDignosticos(atdSeq);
		for(DiagnosticoEtiologiaVO prescCuidDiagnostico: listaPrescCuidDiagnostico){
			Short fdgDgnSnbGnbSeq = prescCuidDiagnostico.getCdgFdgDgnSnbGnbSeq();
			Short fdgDgnSnbSequencia = prescCuidDiagnostico.getCdgFdgDgnSnbSequencia();
			Short fdgDgnSequencia = prescCuidDiagnostico.getCdgFdgDgnSequencia();
			EpeDiagnosticoId diagnosticoId = new EpeDiagnosticoId();
			diagnosticoId.setSnbGnbSeq(fdgDgnSnbGnbSeq);
			diagnosticoId.setSnbSequencia(fdgDgnSnbSequencia);
			diagnosticoId.setSequencia(fdgDgnSequencia);
			EpeDiagnostico diagnostico = this.getEpeDiagnosticoDAO().obterPorChavePrimaria(diagnosticoId);
			String descricaoDiagnostico = diagnostico.getDescricao();
			descricaoDiagnostico = descricaoDiagnostico.toLowerCase();
			EpeFatRelacionado fatRelacionado = this.getEpeFatRelacionadoDAO().obterPorChavePrimaria(prescCuidDiagnostico.getCdgFdgFreSeq());
			String descricaoFatRelacionado = fatRelacionado.getDescricao();
			descricaoFatRelacionado = descricaoFatRelacionado.toLowerCase();
			descricaoFatRelacionado = CoreUtil.capitalizaTextoFormatoAghu(descricaoFatRelacionado); 
			String descricao = descricaoDiagnostico + " relacionado a " + descricaoFatRelacionado;
			MamDiagnostico diagnosticoNew = new MamDiagnostico();
			Date data = DateUtil.truncaData(new Date());
			diagnosticoNew.setData(data);
			diagnosticoNew.setDthrCriacao(new Date());
			diagnosticoNew.setDthrValida(new Date());
			AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentosPorSeq(atdSeq);
			diagnosticoNew.setAtendimento(atendimento);
			diagnosticoNew.setPaciente(atendimento.getPaciente());
			diagnosticoNew.setIndSituacao(DominioSituacao.A);
			diagnosticoNew.setIndPendente(DominioIndPendenteDiagnosticos.V);
			diagnosticoNew.setServidor(servidorLogado);
			diagnosticoNew.setServidorValida(servidorLogado);
			diagnosticoNew.setDescricao(descricao);
			EpeFatRelDiagnosticoId fatRelDiagnosticoId = new EpeFatRelDiagnosticoId();
			fatRelDiagnosticoId.setDgnSequencia(fdgDgnSequencia);
			fatRelDiagnosticoId.setDgnSnbGnbSeq(fdgDgnSnbGnbSeq);
			fatRelDiagnosticoId.setDgnSnbSequencia(fdgDgnSnbSequencia);
			fatRelDiagnosticoId.setFreSeq(prescCuidDiagnostico.getCdgFdgFreSeq());
			EpeFatRelDiagnostico fatRelDiagnostico = this.getFatRelDiagnosticoDAO().obterPorChavePrimaria(fatRelDiagnosticoId);
			diagnosticoNew.setFatRelDiagnostico(fatRelDiagnostico);
			this.getAmbulatorioFacade().inserirDiagnostico(diagnosticoNew);
		}
	
		
		List<DiagnosticoEtiologiaVO> listaDiagnostico = this.getAmbulatorioFacade().listarAtualizarMamDignosticos(atdSeq);
		for(DiagnosticoEtiologiaVO diagnostico: listaDiagnostico){
			Short fdgDgnSnbGnbSeq = diagnostico.getCdgFdgDgnSnbGnbSeq();
			Short fdgDgnSnbSequencia = diagnostico.getCdgFdgDgnSnbSequencia();
			Short fdgDgnSequencia = diagnostico.getCdgFdgDgnSequencia();

			EpeFatRelDiagnosticoId fatRelDiagnosticoId = new EpeFatRelDiagnosticoId();
			
			fatRelDiagnosticoId.setDgnSnbGnbSeq(fdgDgnSnbGnbSeq);
			fatRelDiagnosticoId.setDgnSequencia(fdgDgnSequencia);
			fatRelDiagnosticoId.setDgnSnbSequencia(fdgDgnSnbSequencia);
			fatRelDiagnosticoId.setFreSeq(diagnostico.getCdgFdgFreSeq());

			AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentosPorSeq(atdSeq);

			List<MamDiagnostico> listaDiagnosticoAux = this.getAmbulatorioFacade()
				.listarDiagnosticosPorFatRelDiagnosticoEPaciente(fatRelDiagnosticoId, atendimento.getPaciente().getCodigo());
			for(MamDiagnostico diagnosticoAux: listaDiagnosticoAux){
				MamDiagnostico diagnosticoAuxOld = null;
				try {
					diagnosticoAuxOld = (MamDiagnostico) BeanUtils.cloneBean(diagnosticoAux);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.ERRO_CLONE_DIAGNOSTICO);
				} 
				diagnosticoAux.setDthrMvto(new Date());
				diagnosticoAux.setDthrValidaMvto(new Date());
				diagnosticoAux.setIndSituacao(DominioSituacao.I);
				diagnosticoAux.setServidorMovimento(servidorLogado);
				diagnosticoAux.setServidorValidaMovimento(servidorLogado);
				Date dataTruncada = DateUtil.truncaData(new Date());
				diagnosticoAux.setDataFim(dataTruncada);
				this.getAmbulatorioFacade().atualizarDiagnostico(diagnosticoAux, diagnosticoAuxOld);
			}
		}
	}
	
	public void inserirPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagem,
			boolean flush) throws BaseException {
		EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO = this.getEpePrescricaoEnfermagemDAO();

		if (prescricaoEnfermagem.getServidorValida() != null) {
			validarPrescricaoEnfermagem(prescricaoEnfermagem);
		}

		epePrescricaoEnfermagemDAO.persistir(prescricaoEnfermagem);
		
		if (Boolean.TRUE.equals(flush)) {
			epePrescricaoEnfermagemDAO.flush();
		}

	}
	
	/**
	 * Atualiza uma prescricao de enfermagem.
	 * 
	 * @param prescricaoEnfermagemOld
	 * @param prescricaoEnfermagemNew 
	 * @return
	 * @throws BaseException
	 */
	public EpePrescricaoEnfermagem atualizarPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagemOld,
			EpePrescricaoEnfermagem prescricaoEnfermagemNew, boolean flush) throws BaseException {
		EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO = this
				.getEpePrescricaoEnfermagemDAO();

		if (!(CoreUtil.modificados(prescricaoEnfermagemOld.getDataImpSumario(),
				prescricaoEnfermagemNew.getDataImpSumario())
				|| CoreUtil.modificados(
						prescricaoEnfermagemOld.getTipoEmissaoSumario(),
						prescricaoEnfermagemNew.getTipoEmissaoSumario())
				|| (prescricaoEnfermagemOld.getDataImpSumario() == null && prescricaoEnfermagemNew
						.getDataImpSumario() != null)
				|| (prescricaoEnfermagemOld.getDataImpSumario() != null && prescricaoEnfermagemNew
						.getDataImpSumario() == null)
				|| (prescricaoEnfermagemOld.getTipoEmissaoSumario() == null && prescricaoEnfermagemNew
						.getTipoEmissaoSumario() != null) || (prescricaoEnfermagemOld
				.getTipoEmissaoSumario() != null && prescricaoEnfermagemNew
				.getTipoEmissaoSumario() == null))
				&& ((prescricaoEnfermagemNew.getDthrInicioMvtoPendente() == null && prescricaoEnfermagemOld
						.getDthrInicioMvtoPendente() != null) || (prescricaoEnfermagemNew
						.getServidorValida() != null && prescricaoEnfermagemOld
						.getServidorValida() == null))) {
			validarPrescricaoEnfermagem(prescricaoEnfermagemNew);
		}
		
		prescricaoEnfermagemNew = epePrescricaoEnfermagemDAO
				.atualizar(prescricaoEnfermagemNew);
		
		epePrescricaoEnfermagemDAO.flush();
		
		validarPrescricaoEnfermagemEnforce(prescricaoEnfermagemNew,
				prescricaoEnfermagemOld, DominioOperacaoBanco.UPD);

		if (Boolean.TRUE.equals(flush)) {
			epePrescricaoEnfermagemDAO.flush();
		}

		return prescricaoEnfermagemNew;
	}
	
	/**
	 * ORADB EPEK_PEN_RN.RN_PENP_VER_SERV_VAL
	 * 
	 * @param prescricaoEnfermagemNew
	 * @throws ApplicationBusinessException
	 */
	private void validarPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagemNew) throws ApplicationBusinessException {
		Boolean existeQualificacoesUsuarioSemNumeroRegistroConselho = this
				.getRegistroColaboradorFacade()
				.existeQualificacoesUsuarioSemNumeroRegistroConselho(DominioTipoQualificacao.CCC);
		if (Boolean.FALSE
				.equals(existeQualificacoesUsuarioSemNumeroRegistroConselho)) {
			throw new ApplicationBusinessException(
					PrescricaoEnfermagemRNExceptionCode.EPE_00190);
		}
	}

	/**
	 * ORADB 
	 * PROCEDURE EPEK_PEN_RN.RN_PENP_VER_ATENDIM
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	public void verificarAtendimentoVigente(Integer atdSeq, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException{
		this.verificarAtendimento(atdSeq, dthrInicio, dthrFim);
	}
	
	/**
	 * ORADB 
	 * PROCEDURE EPEK_RN.RN_EPEP_VER_ATEND
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void verificarAtendimento(Integer atdSeq, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException{
		if(atdSeq != null){
			AghAtendimentos atendimento = getAghuFacade().obterPorAtendimentoVigente(atdSeq);
			if(atendimento == null){
				throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00106);
			} else {
				if(dthrInicio!=null){
					if(!atendimento.getOrigem().equals(DominioOrigemAtendimento.I) && !atendimento.getOrigem().equals(DominioOrigemAtendimento.H) &&
					   !atendimento.getOrigem().equals(DominioOrigemAtendimento.U) && !atendimento.getOrigem().equals(DominioOrigemAtendimento.N)){
						throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00226);		
					}
					if((DateValidator.validaDataMenor(dthrInicio,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrInicio,atendimento.getDthrFim()))){
						throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
					}
					
					if(dthrFim!=null && (DateValidator.validaDataMenor(dthrFim,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrFim,atendimento.getDthrFim()))){
							throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
					}
				}
			}
			Integer hodSeq = atendimento.getHospitalDia().getSeq();
			Integer intSeq = atendimento.getInternacao().getSeq();
			Integer atuSeq = atendimento.getAtendimentoUrgencia().getSeq();
			
			if(hodSeq!=null){
				AghAtendimentos atendimentoHospitalDia = getAghuFacade().obterAtendimentoPorHospDiaVigente(hodSeq);
				if(atendimentoHospitalDia==null){
					throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00106);
				} else {
					if(dthrInicio!=null){
						if((DateValidator.validaDataMenor(dthrInicio,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrInicio,atendimento.getDthrFim()))){
							throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
						}
						if(dthrFim!=null && (DateValidator.validaDataMenor(dthrInicio,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrInicio,atendimento.getDthrFim()))){
							throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
						}
					}
				}
			}
			
			if(intSeq!=null){
				AghAtendimentos atendimentoInternacao = getAghuFacade().obterAtendimentoPorInternacaoVigente(intSeq);
				if(atendimentoInternacao == null){
					throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00106);
				} else {
					if(dthrInicio!=null){
						if((DateValidator.validaDataMenor(dthrInicio,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrInicio,atendimento.getDthrFim()))){
							throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
						}
						if(dthrFim!=null && (DateValidator.validaDataMenor(dthrInicio,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrInicio,atendimento.getDthrFim()))){
							throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
						}
					}
				}
			}
			if(atuSeq!=null){
				AghAtendimentos atendimentoAtendimentoUrgencia = getAghuFacade().obterAtendimentoPorAtendimentoUrgenciaVigente(atuSeq);
				if(atendimentoAtendimentoUrgencia == null){
					throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00106);
				} else {
					if(dthrInicio!=null){
						if((DateValidator.validaDataMenor(dthrInicio,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrInicio,atendimento.getDthrFim()))){
							throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
						}
						if(dthrFim!=null && (DateValidator.validaDataMenor(dthrInicio,atendimento.getDthrInicio())&& atendimento.getDthrFim()!=null && DateUtil.validaDataMaior(dthrInicio,atendimento.getDthrFim()))){
							throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00227);
						}
					}
				}
			}
		}
	}
	
	/**
	 * ORADB 
	 * PROCEDURE EPEK_PEN_RN.RN_PENP_VER_NUM_CUID
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	public void verificarNumeroCuidados(Integer atdSeq, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException{
		List<EpePrescricoesCuidados> listaPrescricoesCuidados = this.getEpePrescricoesCuidadosDAO().pesquisarPrescricoesCuidadosPorAtendimentoDataInicioFim(atdSeq, dthrInicio, dthrFim);
		if(listaPrescricoesCuidados==null){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00307);
		}
	}
	
	
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PEN_RN.RN_PENP_VER_DTHR_FIM 
	 * @param atdSeq
	 * @param dthrInicio
	 * @param dthrFim
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void verificarDataHoraFim(Date dthrFim) throws ApplicationBusinessException{
		if(DateValidator.validaDataMenorQueAtual(dthrFim)){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00198);
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PEN_RN.RN_PENP_VER_DTHR_INI
	 * @param dthrInicio
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void verificarDataHoraInicio(EpePrescricaoEnfermagem prescricaoEnfermagem) throws ApplicationBusinessException{
		if(prescricaoEnfermagem.getDthrInicio()==null || DateValidator.validaDataMenorQueAtual(prescricaoEnfermagem.getDthrInicio()) ){
			prescricaoEnfermagem.setDthrInicio(new Date());
			if(prescricaoEnfermagem.getSituacao().equals(DominioSituacaoPrescricao.U)){
				prescricaoEnfermagem.setDthrMovimento(prescricaoEnfermagem.getDthrInicio());
			}
		} else if(DateUtil.isDatasIguais(prescricaoEnfermagem.getDthrInicio(), new Date())){
			if(prescricaoEnfermagem.getSituacao().equals(DominioSituacaoPrescricao.U)){
				prescricaoEnfermagem.setDthrMovimento(prescricaoEnfermagem.getDthrInicio());
			}
		} else {
			if(prescricaoEnfermagem.getSituacao().equals(DominioSituacaoPrescricao.U)){
				prescricaoEnfermagem.setDthrMovimento(new Date());
			}
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PEN_RN.RN_PENP_VER_DT_REF
	 * @return
	 */
	@SuppressWarnings("ucd")
	public void verificarDataHoraReferencia(Date dataReferencia) throws ApplicationBusinessException{
		Date dataOntem = DateUtil.adicionaDias(new Date(), -1);
		if(DateUtil.validaHoraMenorIgual(dataReferencia, dataOntem)){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00104);
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PEN_RN.RN_PENP_VER_SOBREP
	 */
	@SuppressWarnings("ucd")
	public void verificarSobreposicaoDatasMesmoAtendimento(Integer atdSeq, Integer seq, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException{
		List<EpePrescricaoEnfermagem> listaPrescricoes = this.getEpePrescricaoEnfermagemDAO().pesquisarPrescricaoDiferentePorAtendimento(atdSeq, seq);
		for(EpePrescricaoEnfermagem prescricaoEnfermagem: listaPrescricoes){
			if(!this.verificarDatas(prescricaoEnfermagem.getDthrInicio(), prescricaoEnfermagem.getDthrFim(), dthrInicio, dthrFim)){
				listaPrescricoes.remove(prescricaoEnfermagem);
			}
		}
		if(listaPrescricoes!=null && listaPrescricoes.size()>0){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00199);
		}
	}
	
	/**
	 * FUNCTION
	 * ORADB MPMC_VERIFICA_DATAS
	 * @param dthrInicioTab
	 * @param dthrFimTab
	 * @param dthInicio
	 * @param dthrFim
	 */
	public Boolean verificarDatas(Date dthrInicioTab, Date dthrFimTab, Date dthrInicio, Date dthrFim){
		if((DateValidator.validaDataMenor(dthrFimTab, dthrInicio)||DateValidator.validaDataMenor(dthrFimTab, dthrFim))&&dthrFimTab==null){
			return true;
		} else if((dthrInicioTab!=null && dthrFimTab!=null && dthrInicio!=null && !DateUtil.isDatasIguais(dthrInicioTab, dthrFimTab))&&
				(DateUtil.validaDataMaiorIgual(dthrInicioTab,dthrInicio) && DateValidator.validaDataMenor(dthrInicioTab, dthrFim)) || 
				(DateUtil.validaDataMaior(dthrFimTab, dthrInicio) && DateValidator.validaDataMenorIgual(dthrFimTab, dthrFim))){
			return true;
		} else if(dthrInicioTab!=null && dthrFimTab!=null && dthrInicio!=null && dthrFim!=null && DateValidator.validaDataMenorIgual(dthrFimTab, dthrInicio)&&DateUtil.validaDataMaior(dthrFimTab, dthrFim)){
			return true;
		} else if(dthrFim==null && DateValidator.validaDataMenorIgual(dthrInicioTab, dthrInicio) && DateUtil.validaDataMaior(dthrFimTab, dthrInicio)){
			return true;
		} else if(dthrFim==null && dthrFimTab==null){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB EPEK_PEN_RN.RN_PENP_VER_UNID_FUN
	 */
	@SuppressWarnings("ucd")
	public void verificarPacienteUnidFuncional(Integer atdSeq) throws ApplicationBusinessException{
		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPorIntSeq(atdSeq);
		if(atendimento==null){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00106);
		} else if(!this.getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.PEN_INFORMATIZADA)){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00111);
		}
	}

	/**
	 * PROCEDURE
	 * ORADB EPEK_PEN_RN.RN_PENP_VER_DIAS_PEN
	 */
	@SuppressWarnings("ucd")
	public void verificarPrescricaoAdiantada(Integer atdSeq, Date dthrInicio, Integer seq, Date dtReferencia) throws ApplicationBusinessException{
		Date data = new Date();
		String parametro = null;
		String parametro2 = null;
		Date dataParametro = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPorIntSeq(atdSeq);
		if(atendimento==null){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00106);
		} else if(atendimento.getUnidadeFuncional()==null){
			throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00110);
		} else if(atendimento.getUnidadeFuncional().getIndUnidTempoPenAdiantada().equals(DominioUnidTempo.D)){
			if(atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas()!=null){
				data = DateUtil.adicionaDias(data, atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas().intValue());
			}
			if(DateValidator.validaDataMenor(data, dthrInicio)){
				if(atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas()==null){
					parametro = "0";
				} else {
					parametro = atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas().toString();
					dataParametro = DateUtil.adicionaDias(dataParametro, atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas().intValue());
				}
				parametro2 = sdf.format(dataParametro);
				throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.ERRO_PRESCRICAO_ADIANTADA_DIAS, parametro, parametro2);
			}
	
		} else if(atendimento.getUnidadeFuncional().getIndUnidTempoPenAdiantada().equals(DominioUnidTempo.H)){
			
			if(atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas()!=null){
				data = DateUtil.adicionaHoras(data, atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas().intValue());
			}
			if(DateValidator.validaDataMenor(data, dthrInicio)){
				if(atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas()==null){
					parametro = "0";
				} else {
					parametro = atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas().toString();
					dataParametro = DateUtil.adicionaHoras(dataParametro, atendimento.getUnidadeFuncional().getNroUnidTempoPenAdiantadas().intValue());
				}
				parametro2 = sdf.format(dataParametro);
				throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.ERRO_PRESCRICAO_ADIANTADA_HORAS, parametro, parametro2);
			}
		}
		if(DateUtil.validaDataMaior(dthrInicio, new Date())){
			if(this.getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.PEN_CONSECUTIVA)){
				dtReferencia = DateUtil.adicionaDias(dtReferencia, -1);
				List<EpePrescricaoEnfermagem> listaPrescricao = this.getEpePrescricaoEnfermagemDAO().pesquisarPrescricaoDiferentePorAtendimentoEDataReferencia(atdSeq, seq, dtReferencia);
				if(listaPrescricao==null || listaPrescricao.size()==0){
					throw new ApplicationBusinessException(PrescricaoEnfermagemRNExceptionCode.EPE_00200);
				}
			}
		}
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO() {
		return epePrescricoesCuidadosDAO;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO() {
		return epePrescricaoEnfermagemDAO;
	}	
	
	protected EpePrescCuidDiagnosticoDAO getEpePrescCuidDiagnosticoDAO() {
		return epePrescCuidDiagnosticoDAO;
	}
	
	protected EpeDiagnosticoDAO getEpeDiagnosticoDAO() {
		return epeDiagnosticoDAO;
	}
	
	protected EpeFatRelacionadoDAO getEpeFatRelacionadoDAO() {
		return epeFatRelacionadoDAO;
	}
	
	protected EpeFatRelDiagnosticoDAO getFatRelDiagnosticoDAO() {
		return epeFatRelDiagnosticoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected EpeFatRelDiagnosticoDAO getEpeFatRelDiagnosticoDAO() {
		return epeFatRelDiagnosticoDAO;
	}
	
}
