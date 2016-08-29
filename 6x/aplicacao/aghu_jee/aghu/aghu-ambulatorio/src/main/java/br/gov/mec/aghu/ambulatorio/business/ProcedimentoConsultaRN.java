package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.MarcacaoConsultaRN.MarcacaoConsultaRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPagadorDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacProcedHospEspecialidadesDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaGrade;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.CaracteristicaPhiVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeProcedHospitalar;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe de negócio com implementação das triggers, procedures
 * e functions migradas da package AACK_PRH_RN.
 *
 */
@Stateless
public class ProcedimentoConsultaRN extends BaseBusiness {

@EJB
private AmbulatorioConsultaRN ambulatorioConsultaRN;

private static final Log LOG = LogFactory.getLog(ProcedimentoConsultaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacProcedHospEspecialidadesDAO aacProcedHospEspecialidadesDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AacPagadorDAO aacPagadorDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AacGradeProcedHospitalarDAO aacGradeProcedHospitalarDAO;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9034673963979439135L;

	public enum ProcedimentoConsultaRNExceptionCode implements BusinessExceptionCode {
		AAC_00251, FAT_00512, AAC_00705, FAT_00942
	}
	
	/**
	 * Atualiza AacConsultaProcedHospitalar 
	 *  
	 * @param consultaProcedHospitalar
	 */
	public AacConsultaProcedHospitalar atualizarConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar oldConsultaProcedHospitalar, 
			AacConsultaProcedHospitalar newConsultaProcedHospitalar, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException  {
		newConsultaProcedHospitalar = preAtualizarConsultaProcedimentoHospitalar(oldConsultaProcedHospitalar, newConsultaProcedHospitalar);
		newConsultaProcedHospitalar = posAtualizarConsultaProcedimentoHospitalar(oldConsultaProcedHospitalar, newConsultaProcedHospitalar);
		executarPosStatementAtualizarConsultaProcedimentoHospitalar(oldConsultaProcedHospitalar, newConsultaProcedHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
		return newConsultaProcedHospitalar;
	}
	
	/**
	 * ORADB: Trigger AACT_PRH_ASU
	 * 
	 * @param oldConsultaProcedHospitalar
	 * @param newConsultaProcedHospitalar
	 * @throws BaseException
	 */
	public void executarPosStatementAtualizarConsultaProcedimentoHospitalar(
			AacConsultaProcedHospitalar oldConsultaProcedHospitalar, 
			AacConsultaProcedHospitalar newConsultaProcedHospitalar, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		processarConsultaProcedimentoHospitalar(oldConsultaProcedHospitalar, newConsultaProcedHospitalar, 
				DominioOperacaoBanco.UPD, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * ORADB Trigger AACT_PRH_BRU 
	 * @param consultaProcedHospitalar
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public AacConsultaProcedHospitalar preAtualizarConsultaProcedimentoHospitalar(
			AacConsultaProcedHospitalar oldConsultaProcedHospitalar,
			AacConsultaProcedHospitalar newConsultaProcedHospitalar)
				throws ApplicationBusinessException {
		this.verificarRetornoConsulta(newConsultaProcedHospitalar.getConsultas().getNumero());
		
		Byte quantidade = newConsultaProcedHospitalar.getQuantidade();
		Byte quantidadeAnterior = oldConsultaProcedHospitalar.getQuantidade();
		
		if(CoreUtil.modificados(quantidade, quantidadeAnterior)) {
			if(newConsultaProcedHospitalar.getConsulta()){
				throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.AAC_00187);
			}
		}
		
		AacConsultaProcedHospitalar retorno =  this.getAacConsultaProcedHospitalarDAO().atualizar(newConsultaProcedHospitalar);
		this.getAacConsultaProcedHospitalarDAO().flush();
		return retorno;
	}
	
	/**
	 * ORADB Trigger AACT_PRH_ARU	
	 * @param consultaProcedHospitalar
	 * @throws ApplicationBusinessException 
	 */
	public AacConsultaProcedHospitalar posAtualizarConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar oldConsultaProcedHospitalar, 
			AacConsultaProcedHospitalar newConsultaProcedHospitalar) throws ApplicationBusinessException {
		Integer conNumero = newConsultaProcedHospitalar.getConsultas().getNumero();
		Integer seqProcedHospInterno = newConsultaProcedHospitalar.getProcedHospInterno().getSeq();
		Integer cidSeq = null;
		if(newConsultaProcedHospitalar.getCid()!=null){
			cidSeq = newConsultaProcedHospitalar.getCid().getSeq();	
		}
		
		Integer cidSeqAnterior = null; 
		if (oldConsultaProcedHospitalar.getCid() != null) {
			cidSeqAnterior = oldConsultaProcedHospitalar.getCid().getSeq();
		}
		
		if (CoreUtil.modificados(cidSeq, cidSeqAnterior))
		{
			List<FatProcedAmbRealizado> lista = getFaturamentoFacade().buscarPorNumeroConsultaEProcedHospInternos(conNumero, seqProcedHospInterno);
			for (FatProcedAmbRealizado procedimento : lista){
				final AghCid cid = getAghuFacade().obterAghCidsPorChavePrimaria(cidSeq);
				procedimento.setCid(cid);
			}
		}
	
		AacConsultaProcedHospitalar retorno = this.getAacConsultaProcedHospitalarDAO().atualizar(newConsultaProcedHospitalar);
		this.getAacConsultaProcedHospitalarDAO().flush();
		return retorno;
	}		

	@Deprecated
	public void inserirProcedimentoConsulta(AacConsultaProcedHospitalar newConsultaProcedHospitalar, boolean flush, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		executarPreInserirConsultaProcedimentoHospitalar(newConsultaProcedHospitalar);
		this.getAacConsultaProcedHospitalarDAO().persistir(newConsultaProcedHospitalar);
		if (flush){
			this.getAacConsultaProcedHospitalarDAO().flush();
		}
		executarPosStatementInserirConsultaProcedimentoHospitalar(newConsultaProcedHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	public void inserirProcedimentoConsulta(AacConsultaProcedHospitalar newConsultaProcedHospitalar, boolean flush, String nomeMicrocomputador, final Date dataFimVinculoServidor, 
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) throws BaseException {
		executarPreInserirConsultaProcedimentoHospitalar(newConsultaProcedHospitalar, aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
		this.getAacConsultaProcedHospitalarDAO().persistir(newConsultaProcedHospitalar);
		if (flush){
			this.getAacConsultaProcedHospitalarDAO().flush();
		}
		executarPosStatementInserirConsultaProcedimentoHospitalar(newConsultaProcedHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	
	public void executarPosStatementInserirConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar newConsultaProcedHospitalar, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) 
			throws BaseException {
		processarConsultaProcedimentoHospitalar(null, newConsultaProcedHospitalar, DominioOperacaoBanco.INS, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * ORADB Trigger AACT_PRH_BRI
	 * 
	 * @param consultaProcedHospitalar
	 */
	@Deprecated
	public void executarPreInserirConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar consultaProcedHospitalar) 
			throws ApplicationBusinessException {
		//Boolean vProtese = Boolean.FALSE; não usado => apac
		
		/*
		 * TODO: AGHU MIGRAÇÃO 6.0 - É NECESSÁRIO RESGATAR OS ATRIBUTOS DO CONTEXTO DA SESSÃO ATRAVÉS DAS CONTROLLERS.
		 * VIDE: CONTROLLER PesquisarPacientesAgendadosController MÉTODO adicionarProcedimentoConsulta.
		 * OBS. AO CHAMAR ESTE MÉTODO OCORRERÁ UMA UnsupportedOperationException
		 * 
		 */
		
		logError("Erro metodo deve ser atualizado porque está @Deprecated - AmbulatorioConsultaRN.executarPreInserirConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar consultaProcedHospitalar) ");
		
		
		verificarRetornoConsulta(consultaProcedHospitalar.getId().getConNumero());

		//--fim desvio proteses auditivas 15/04/2009
		//-- verifica procedimento para especialidade (exceto emergência)
		//-- testa variável para não cancelar inclusão na rotina diária apacs
		//-- Milena 02/2003
		if (Boolean.FALSE.equals(obterContextoSessao("AACK_PRH_RN_V_APAC_DIARIA"))) {
			verificarProcedimentoEspecialidade(consultaProcedHospitalar.getId().getConNumero(), 
				consultaProcedHospitalar.getId().getPhiSeq());		
		}
		
		//-- Milena 05/2005
		//-- verificar se os aparelhos auditivos lançados são compatíveis com as
		//-- solicitações de compra
		//-- Milena 09/2008 - exceto para os aparelhos de doação da receita federal. --
		//  --IF aacc_ver_prot_audit(:new.con_numero)= 'S--
		//   --AGHP_ENVIA_EMAIL('T',1,'regra phi_bri '||aack_aaa_rn.v_protese_auditiva
		//  --     ,'MPONS@HCPA.UFRGS.BR','correio;');--		
		if (Boolean.TRUE.equals(obterContextoSessao("AACK_AAA_RN_V_PROTESE_AUDITIVA"))) {
			//vProtese = Boolean.TRUE; não usado => apac
			atribuirContextoSessao("AACK_AAA_RN_V_PROTESE_AUDITIVA.toString()", Boolean.FALSE);
		}
		else {
			Integer vlrNumerico = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PHI_APA_SEM_SOLIC).getVlrNumerico().intValue();
			if (!vlrNumerico.equals(consultaProcedHospitalar.getId().getPhiSeq())) { 
				if (!rnPrhcVerApaSlc(consultaProcedHospitalar.getId().getConNumero(), consultaProcedHospitalar.getId().getPhiSeq())) { //AACK_PRH_RN.RN_PRHC_VER_APA_SLC
					throw new ApplicationBusinessException(ProcedimentoConsultaRNExceptionCode.AAC_00705);
				}
			}
		}
		//--fim desvio proteses auditivas 15/04/2009
		//-- verifica procedimento para especialidade (exceto emergência)
		//-- testa variável para não cancelar inclusão na rotina diária apacs
		//-- Milena 02/2003
		
	      
		//-- verfica proced consulta com proced grad para grade procedimentos
		//-- Milena incluindo variável package para não cancelar encerramento - 130807
		//-- para garantir as inclusões feitas pelo sistema
		if (Boolean.FALSE.equals(obterContextoSessao("FATK_CAP_RN_V_CAP_ENCERRAMENTO"))){
			verificarProcedimentoGrade(consultaProcedHospitalar.getId().getConNumero(), 
					consultaProcedHospitalar.getId().getPhiSeq());
		}
		//-- Milena 27/10/2005  ---> Enforce PRH
		if (verificarIndicadorProcedimentoConsultaLancado(
				consultaProcedHospitalar.getId().getConNumero(),
				consultaProcedHospitalar.getId().getPhiSeq())) {
			throw new ApplicationBusinessException(
					MarcacaoConsultaRNExceptionCode.AAC_00733);
		}
	}
	
	/**
	 * ORADB Trigger AACT_PRH_BRI
	 * 
	 * @param consultaProcedHospitalar
	 */
	public void executarPreInserirConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar consultaProcedHospitalar, 
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) 
			throws ApplicationBusinessException {
		//Boolean vProtese = Boolean.FALSE; não usado => apac
		
		verificarRetornoConsulta(consultaProcedHospitalar.getId().getConNumero());

		//--fim desvio proteses auditivas 15/04/2009
		//-- verifica procedimento para especialidade (exceto emergência)
		//-- testa variável para não cancelar inclusão na rotina diária apacs
		//-- Milena 02/2003
		if (Boolean.FALSE.equals(aack_prh_rn_v_apac_diaria)) {
			verificarProcedimentoEspecialidade(consultaProcedHospitalar.getId().getConNumero(), 
				consultaProcedHospitalar.getId().getPhiSeq());		
		}
		
		//-- Milena 05/2005
		//-- verificar se os aparelhos auditivos lançados são compatíveis com as
		//-- solicitações de compra
		//-- Milena 09/2008 - exceto para os aparelhos de doação da receita federal. --
		//  --IF aacc_ver_prot_audit(:new.con_numero)= 'S--
		//   --AGHP_ENVIA_EMAIL('T',1,'regra phi_bri '||aack_aaa_rn.v_protese_auditiva
		//  --     ,'MPONS@HCPA.UFRGS.BR','correio;');--		

// 		Validação retirada pela issue #82256
//		if (Boolean.FALSE.equals(aack_aaa_rn_v_protese_auditiva)) {
//			Integer vlrNumerico = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PHI_APA_SEM_SOLIC).getVlrNumerico().intValue();
//			if (!vlrNumerico.equals(consultaProcedHospitalar.getId().getPhiSeq())) { 
//				if (!rnPrhcVerApaSlc(consultaProcedHospitalar.getId().getConNumero(), consultaProcedHospitalar.getId().getPhiSeq())) { //AACK_PRH_RN.RN_PRHC_VER_APA_SLC
//					throw new ApplicationBusinessException(ProcedimentoConsultaRNExceptionCode.AAC_00705);
//				}
//			}
//		}
		//--fim desvio proteses auditivas 15/04/2009
		//-- verifica procedimento para especialidade (exceto emergência)
		//-- testa variável para não cancelar inclusão na rotina diária apacs
		//-- Milena 02/2003
		
	      
		//-- verfica proced consulta com proced grad para grade procedimentos
		//-- Milena incluindo variável package para não cancelar encerramento - 130807
		//-- para garantir as inclusões feitas pelo sistema
		if (Boolean.FALSE.equals(fatk_cap_rn_v_cap_encerramento)){
			verificarProcedimentoGrade(consultaProcedHospitalar.getId().getConNumero(), 
					consultaProcedHospitalar.getId().getPhiSeq());
		}
		//-- Milena 27/10/2005  ---> Enforce PRH
		if (verificarIndicadorProcedimentoConsultaLancado(
				consultaProcedHospitalar.getId().getConNumero(),
				consultaProcedHospitalar.getId().getPhiSeq())) {
			throw new ApplicationBusinessException(
					MarcacaoConsultaRNExceptionCode.AAC_00733);
		}
	}

	/**
	 * Realiza a remoção dos procedimentos relacionados à consulta informada, executando a validação da trigger AACT_PRH_BRD.
	 * 
	 * @param numeroConsulta - Número da Consulta
	 * @throws BaseException 
	 */
	public void removerProcedimentosConsultaPorCodigoConsulta (Integer numeroConsulta, String nomeMicrocomputador) throws BaseException {

		// Consulta C05.1 da estória #37942
		List<AacConsultaProcedHospitalar> procedimentos = getAacConsultaProcedHospitalarDAO().buscarConsultaProcedHospPorNumeroConsulta(numeroConsulta);
		for (AacConsultaProcedHospitalar aacConsultaProcedHospitalar : procedimentos) {
			DominioSimNao resultado = validarExcluirProcedimentoConsulta(numeroConsulta, aacConsultaProcedHospitalar.getProcedHospInterno().getSeq());
			
			if (DominioSimNao.N.equals(resultado)) {
				throw new ApplicationBusinessException(ProcedimentoConsultaRNExceptionCode.FAT_00942, aacConsultaProcedHospitalar.getId().getPhiSeq());
			}
		}
		
		for (AacConsultaProcedHospitalar aacConsultaProcedHospitalar : procedimentos) {
			removerProcedimentoConsulta(aacConsultaProcedHospitalar, false, nomeMicrocomputador);
		}
	}

	/**
	 * Realiza a validação da exclusão dos Procedimentos de uma Consulta, conforme a regra RN_PRHC_VER_PRC_APAC.
	 * 
	 * @param numeroConsulta - Número da Consulta
	 * @param phiSeq - Código do Procedimento Interno
	 * @return Flag indicando o resultado da validação
	 * @throws ApplicationBusinessException 
	 */
	public DominioSimNao validarExcluirProcedimentoConsulta(Integer numeroConsulta, Integer phiSeq) throws ApplicationBusinessException {

		// Consulta C27 da estória #37942
		AacConsultas consulta = getAacConsultasDAO().obterPorChavePrimaria(numeroConsulta);

		CaracteristicaPhiVO princ = null;
		if (consulta.getConvenioSaudePlano() == null) {
			princ = getFaturamentoFacade().fatcVerCaractPhi(null, null, phiSeq, "Procedimento Principal APAC");
		} else {
			princ = getFaturamentoFacade().fatcVerCaractPhi(consulta.getConvenioSaudePlano().getId().getCnvCodigo(),
					consulta.getConvenioSaudePlano().getId().getSeq(), phiSeq, "Procedimento Principal APAC");
		}
		
		if (DominioSimNao.S.toString().equalsIgnoreCase(princ.getValorChar())) {
			AghParametros paramPagadorSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS);
			
			// Consulta C28 da estória #37942
			DominioSimNao pgd = getAacPagadorDAO().verificarExistenciaPagadorSus(consulta.getPagador().getSeq(), paramPagadorSus.getVlrNumerico().shortValue());
			if (DominioSimNao.N.equals(pgd)) {
				return DominioSimNao.S;
			}
			
			return getFaturamentoFacade().verificarExistenciaProcedimentoAtendimento(phiSeq, consulta.getPaciente().getCodigo());
		}
		
		return null;
	}
	
	/** ORADB: Trigger AACT_PRH_ASD <br/>
	 * Remove um procedimento de consulta
	 */
	public void removerProcedimentoConsulta(AacConsultaProcedHospitalar consultaProcedHospitalar, boolean flush, String nomeMicrocomputador) throws BaseException {
		getAacConsultaProcedHospitalarDAO().remover(consultaProcedHospitalar);
		if (flush){
			getAacConsultaProcedHospitalarDAO().flush();
		}
		/*  Trigger de pos-remover não será mais chamada, pois
		 *  o delete no faturamento é sempre chamado antes de executar este método. 
		executarEnforceConsultaProcedimentoHospitalar(consultaProcedHospitalar, 
				null, 
				DominioOperacaoBanco.DEL, 
				nomeMicrocomputador, 
				new Date());
		
		 */
	}
	
	/**
	 * Verifica se o procedimento é consulta e se já existe outro lançado
	 * 
	 * ORADB Function AACK_PRH_RN.RN_PRHC_VER_IND_CONS
	 * 
	 * @param conNumero
	 * @param phiSeq 
	 */
	public Boolean verificarIndicadorProcedimentoConsultaLancado(Integer conNumero, Integer phiSeq) throws ApplicationBusinessException {
		AacConsultas consulta = getAacConsultasDAO().obterConsulta(conNumero);
		AghParametros paramTipoGrupoContaSus = null;
		
		paramTipoGrupoContaSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		List<VFatAssociacaoProcedimento> listaVFatAssociacaoProcedimentos = 
				getFaturamentoFacade().listarVFatAssociacaoProcedimento(phiSeq, 
					consulta.getConvenioSaudePlano().getId().getCnvCodigo(), 
					consulta.getConvenioSaudePlano().getId().getSeq(), 
					paramTipoGrupoContaSus.getVlrNumerico().shortValue());
		
		if ((listaVFatAssociacaoProcedimentos.isEmpty() || !listaVFatAssociacaoProcedimentos.get(0).getIphIndConsulta())) {
			return false;
		}
		else {
			List<VFatAssociacaoProcedimento> listaVFatAssociacaoProcedimentosOutros = 
					getFaturamentoFacade().listarVFatAssociacaoProcedimentoOutrosProcedimentos(conNumero, 
					phiSeq, consulta.getConvenioSaudePlano().getId().getCnvCodigo(), 
					consulta.getConvenioSaudePlano().getId().getSeq(), 
					paramTipoGrupoContaSus.getVlrNumerico().shortValue());
			
			if (listaVFatAssociacaoProcedimentosOutros.isEmpty() || !listaVFatAssociacaoProcedimentosOutros.get(0).getIphIndConsulta()) {
				return false;
			}
			else {
				return true;
			}
		}
	}	
	
    /**
     * ORADB Procedure AACK_PRH_RN.RN_PRHP_VER_RET_CONS
	 *  
     * @param conNumero
     */
    public void verificarRetornoConsulta(Integer conNumero) throws ApplicationBusinessException {
          if (getAacConsultasDAO().pesquisarRetornosAbsenteismoCount(conNumero) == 0) {
                throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.AAC_00263);
          }
    }	
    
	/**
	 * ORADB Procedure AACK_PRH_RN.RN_PRHP_VER_PROC_ESP
	 * 
	 * @param conNumero
	 * @param phiSeq
	 */
	public void verificarProcedimentoEspecialidade(Integer conNumero, Integer phiSeq) throws ApplicationBusinessException {
		AacConsultas consulta = getAacConsultasDAO().pesquisarConsultaGradeEspecialidade(conNumero).get(0);
		AghParametros paramGrdEspEmergencia = null;
		
		paramGrdEspEmergencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_ESP_EMERGENCIA);
		
		if (consulta != null) {
			// espSeq e espSeqGen vem da consulta
			Short espSeqGen = null;
			if (consulta.getGradeAgendamenConsulta().getEspecialidade().getEspecialidade() != null) {
				espSeqGen = consulta.getGradeAgendamenConsulta().getEspecialidade().getEspecialidade().getSeq();
			}
			if (espSeqGen == null) {
				espSeqGen = consulta.getGradeAgendamenConsulta().getEspecialidade().getSeq();
			}

			  
			if (paramGrdEspEmergencia != null && consulta.getGradeAgendamenConsulta().getEspecialidade().getGreSeq() !=null && 
					paramGrdEspEmergencia.getVlrNumerico().byteValue() != consulta.getGradeAgendamenConsulta().getEspecialidade().getGreSeq()) {
				
				Long countProcedimentos = getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidadeEspGenericaCount(phiSeq, 
						consulta.getGradeAgendamenConsulta().getEspecialidade().getSeq(), espSeqGen);
	    	  
				if (countProcedimentos == 0) {
					throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.AAC_00273);
				}
			}
		}
	}
	
	/**
	 * ORADB Procedure AACK_PRH_RN.RN_PRHP_VER_FAT_AMB
	 * 
	 * @param conNumero
	 * @param phiSeq
	 */
	public void verificarFaturamentoAmbulatorio(Integer conNumero, Integer phiSeq) throws ApplicationBusinessException {
		List<FatProcedAmbRealizado> listaProcedAmbRealizado = 
				getFaturamentoFacade().buscarPorNumeroConsultaEProcedHospInternosApresentadosExcluidos(conNumero, phiSeq);
		
		if (!listaProcedAmbRealizado.isEmpty()) {
			// Procedimento já faturado não pode ser excluido
			throw new ApplicationBusinessException(ProcedimentoConsultaRNExceptionCode.FAT_00512);
		}
	}
	
	/**
	 * ORADB Procedure AACK_PRH_RN.RN_PRHP_VER_PROC_GRD
	 * 
	 * @param conNumero
	 * @param phiSeq 
	 */
	public void verificarProcedimentoGrade(Integer conNumero, Integer phiSeq) throws ApplicationBusinessException {
		Integer grdSeq = getAacGradeAgendamenConsultasDAO().obterGradeAgendamentoConsultaSeqPorConNumeroEIndProcedimento(conNumero, true);
		
		if (grdSeq != null) {
			if (!getAmbulatorioConsultaRN().verificarCaracteristicaGrade(grdSeq, DominioCaracteristicaGrade.NAO_CONSISTE_PROCS_DEFINIDOS)) {
				Long qtde = getAacGradeProcedHospitalarDAO().obterCountListaProcedimentosGradePorGrdSeq(grdSeq);
				if (qtde > 0) {
					List<AacGradeProcedHospitalar> listaGradeProcedHosp = getAacGradeProcedHospitalarDAO().listarProcedimentosGradeTodasSituacoes(grdSeq, phiSeq);
					if (!listaGradeProcedHosp.isEmpty()){
						throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.AAC_00245);
					}
				}
			}
		}
	}
	
	/**
	 * ORADB Function AACK_PRH_RN.RN_PRHC_VER_APA_SLC
	 * 
	 * @param conNumero
	 * @param phiSeq
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws NumberFormatException 
	 */
	public Boolean rnPrhcVerApaSlc(Integer conNumero, Integer phiSeq) throws NumberFormatException, ApplicationBusinessException {
		logDebug("inicio");
		final String caracteristica = "Aparelho Auditivo";
		Boolean vOk = Boolean.FALSE;
		
		//FATC_VER_CARACT_PHI
		CaracteristicaPhiVO caracteristicaPhiVO = getFaturamentoFacade().fatcVerCaractPhi(Short.valueOf("1"), Byte.valueOf("2"), phiSeq, caracteristica);
		
		if (caracteristicaPhiVO.getResultado()) {
			List<AacConsultas> listaConsultas = getAacConsultasDAO().pesquisarConsultacPorConsultaNumeroAnterior(conNumero);
			
			if (!listaConsultas.isEmpty() && listaConsultas.get(0).getPaciente() != null) {
				
				Long cn5Numero = Long.valueOf(this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CLASS_AP_AUDITIVOS).getVlrTexto());
				List<ScoSolicitacaoDeCompra> listaSolicitacaoCompra = getComprasFacade().listarSolicitacoesAparelho(listaConsultas.get(0).getPaciente().getCodigo(), phiSeq, Boolean.TRUE, cn5Numero);
				
				if (listaSolicitacaoCompra.isEmpty()) {
					vOk = Boolean.FALSE;
				}
				else {
					vOk = Boolean.TRUE;
				}
			}
		}
		else {
			vOk = Boolean.TRUE;
		}
		
		return vOk;
	}
	
	
	/**
	 * ORADB Procedure AACK_PRH_RN.RN_PRHP_VER_IND_CONS
	 * 
	 * @param conNumero
	 * @param phiSeq 
	 */
	public void verificarIndicadorProcedimentoConsulta(Boolean indConsulta, Integer conNumero) throws ApplicationBusinessException {
		if (indConsulta) {
			List<AacConsultaProcedHospitalar> listaConsultaProcedHospitalar =
				getAacConsultaProcedHospitalarDAO().buscarConsultaProcedHospPorNumeroConsultaIndicadorConsultaSim(conNumero);
			
			if (listaConsultaProcedHospitalar.size() > 1) {
				// Somente um Item de consulta procedimento pode  ter o
				// indicador  Consulta			
				throw new ApplicationBusinessException(ProcedimentoConsultaRNExceptionCode.AAC_00251);	
			}
		}
	}
	
	/**
	 * ORADB: Procedure AACK_PRH.PROCESS_PRH_ROWS
	 * 
	 * @param consultaProcedHospitalar
	 * @param operacao
	 */
	public void processarConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar oldConsultaProcedHospitalar,
			AacConsultaProcedHospitalar newConsultaProcedHospitalar, DominioOperacaoBanco operacao, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) 
			throws BaseException {
		executarEnforceConsultaProcedimentoHospitalar(oldConsultaProcedHospitalar, newConsultaProcedHospitalar, operacao, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * ORADB: Procedure AACP_ENFORCE_PRH_RULES
	 * 
	 * @param consultaProcedHospitalar
	 * @param operacao
	 */
	public void executarEnforceConsultaProcedimentoHospitalar(AacConsultaProcedHospitalar oldConsultaProcedHospitalar,
			AacConsultaProcedHospitalar newConsultaProcedHospitalar, DominioOperacaoBanco operacao, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) 
			throws BaseException {

		if (operacao.equals(DominioOperacaoBanco.UPD)) {
			verificarFaturamentoAmbulatorio(oldConsultaProcedHospitalar.getConsultas().getNumero(),
					oldConsultaProcedHospitalar.getProcedHospInterno().getSeq());
				  
			// verifica se existe mais de um item com
			// indicador marcado como Consulta			
			verificarIndicadorProcedimentoConsulta(newConsultaProcedHospitalar.getConsulta(),
					newConsultaProcedHospitalar.getConsultas().getNumero());
				
			// interface faturamento
			getFaturamentoFacade().atualizarFaturamentoProcedimentoConsulta(
					newConsultaProcedHospitalar.getConsultas().getNumero(),
					newConsultaProcedHospitalar.getProcedHospInterno().getSeq(),
					newConsultaProcedHospitalar.getQuantidade().shortValue(), 
					newConsultaProcedHospitalar.getConsultas().getRetorno(), 
					nomeMicrocomputador, dataFimVinculoServidor);
		}
		else if (operacao.equals(DominioOperacaoBanco.DEL)) {
			// interface faturamento
			verificarFaturamentoAmbulatorio(oldConsultaProcedHospitalar.getConsultas().getNumero(),
					oldConsultaProcedHospitalar.getProcedHospInterno().getSeq());
			
			// interface faturamento (altera situacao para cancelado nesse caso)
			getFaturamentoFacade().atualizarFaturamentoProcedimentoConsulta(
					oldConsultaProcedHospitalar.getConsultas().getNumero(),
					oldConsultaProcedHospitalar.getProcedHospInterno().getSeq(),
					oldConsultaProcedHospitalar.getQuantidade().shortValue(), 
					oldConsultaProcedHospitalar.getConsultas().getRetorno(), nomeMicrocomputador, dataFimVinculoServidor);
		}
		else if (operacao.equals(DominioOperacaoBanco.INS)) {
			// interface faturamento
			getFaturamentoFacade().inserirFaturamentoProcedimentoConsulta(
					newConsultaProcedHospitalar, 
					newConsultaProcedHospitalar.getConsultas().getDtConsulta(), nomeMicrocomputador, dataFimVinculoServidor);
		}
		
	}
	
	protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
		return aacConsultaProcedHospitalarDAO;
	}
	
	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}
	
	protected AacProcedHospEspecialidadesDAO getAacProcedHospEspecialidadesDAO() {
		return aacProcedHospEspecialidadesDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}

	protected AacGradeProcedHospitalarDAO getAacGradeProcedHospitalarDAO() {
		return aacGradeProcedHospitalarDAO;
	}

	protected AacPagadorDAO getAacPagadorDAO() {
		return aacPagadorDAO;
	}
}
