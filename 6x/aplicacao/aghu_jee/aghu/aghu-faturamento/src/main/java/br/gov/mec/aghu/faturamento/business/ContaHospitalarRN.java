package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoNascimento;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaHospitalarJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcAtuReintVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDatasVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.internacao.vo.FatItemContaHospitalarVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContaHospitalarJn;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.AghAtendimentosPacienteCnsVO;
import br.gov.mec.aghu.vo.AtendimentoNascimentoVO;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity","PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ContaHospitalarRN extends AbstractFatDebugLogEnableRN {
	@EJB
	private FatkCthRN fatkCthRN;
	@EJB
	private FatkCth5RN fatkCth5RN;
	@EJB
	private FatkCth4RN fatkCth4RN;
	@EJB
	private FatkCth2RN fatkCth2RN;
	private static final Log LOG = LogFactory.getLog(ContaHospitalarRN.class);
	@Override
	@Deprecated
	protected Log getLogger() {return LOG;}
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private ICascaFacade cascaFacade;
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	@EJB
	private ItemContaHospitalarRN itemContaHospitalarRN;
	@Inject
	private IPerinatologiaFacade perinatologiaFacade;
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	@EJB
	private IAghuFacade aghuFacade;
	private static final long serialVersionUID = 717111286572838887L;
	public Boolean rnCthcAtuReabre(final Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		Boolean result = getFatkCthRN().rnCthcAtuReabre(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor, null);
		if(result) {
			//this.commitTransaction();
			//this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
			logWarn("Métodos para controle de transação comentados, pois geravam erro de compilação!");
		}
		return result;
	}	
	public void rnCthcAtuReabreEmLote(String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		getFatkCthRN().rnCthcAtuReabreEmLote(nomeMicrocomputador,dataFimVinculoServidor);
	}
	public Boolean rnCthcAtuGeraEsp(final Integer pCthSeq, final Boolean pPrevia, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return getFatkCth2RN().rnCthcAtuGeraEsp(pCthSeq, pPrevia, nomeMicrocomputador, dataFimVinculoServidor, false);
	}
	public RnCthcAtuReintVO rnCthcAtuReint(final Integer pCthSeq, final Integer pPacCodigo, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return getFatkCthRN().rnCthcAtuReint(pCthSeq, pPacCodigo, nomeMicrocomputador, dataFimVinculoServidor);
	}
	public RnCthcVerItemSusVO rnCthcVerItemSus(final DominioOrigemProcedimento pModulo, final Short pCnvCodigo, final Byte pCspSeq, final Short pQuantidade, final Integer pPhiSeq) throws BaseException {
		return getFatkCthRN().rnCthcVerItemSus(pModulo, pCnvCodigo, pCspSeq, pQuantidade, pPhiSeq, null);
	}
	public void rnCthpAtuDiaUti2(final Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		getFatkCth4RN().rnCthpAtuDiaUti2(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor);
	}
	public Boolean rnCthcVerDesdobr(final Integer pSeq) throws BaseException {
		return getFatkCthRN().rnCthcVerDesdobr(pSeq);
	}	
	public Boolean rnCthcVerReapres(final Integer pSeq) throws BaseException {
		return getFatkCthRN().rnCthcVerReapres(pSeq);
	}	
	public List<Integer> getContasParaReaberturaEmLote(final FatCompetencia competencia, final Date dtInicial, final Date dtFinal, final Long procedimentoSUS) throws BaseException{
		return getFatkCth4RN().getContasParaReaberturaEmLote(competencia, dtInicial, dtFinal, procedimentoSUS);
	}

	public void rnCthpTrocaCnv(final Integer pIntSeq, final Date pDtInt, final Short pCnvOld, final Byte pCspOld, final Short pCnvNew, final Byte pCspNew, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		getFatkCthRN().rnCthpTrocaCnv(pIntSeq, pDtInt, pCnvOld, pCspOld, pCnvNew, pCspNew, nomeMicrocomputador, dataFimVinculoServidor);	
	}
	public RnCthcVerDatasVO rnCthcVerDatas(final Integer pIntSeq,final Date pDataNova,final Date pDataAnterior,final String pTipoData, final Date dataFimVinculoServidor) throws BaseException {
		return getFatkCthRN().rnCthcVerDatas(pIntSeq, pDataNova, pDataAnterior, pTipoData, dataFimVinculoServidor);
	}
	public void rnCthpAtuDiarUti(final Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		getFatkCthRN().rnCthpAtuDiarUti(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	//ORADB FATT_CTH_BRI
	@SuppressWarnings("PMD.NPathComplexity")
	public void executarAntesDeInserirContaHospitalar(final FatContasHospitalares contaHospitalar, final Boolean alterarEspecialidade, final Date dataFimVinculoServidor) throws BaseException, ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final Date dataAtual = new Date();
		this.getFatkCthRN().rnCthpVerAihSolic(contaHospitalar.getAih() != null ? contaHospitalar.getAih().getNroAih() : null, contaHospitalar.getProcedimentoHospitalarInterno() != null ? contaHospitalar.getProcedimentoHospitalarInterno().getSeq() : null);
		
		contaHospitalar.setAlteradoEm(dataAtual);
		contaHospitalar.setCriadoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
		contaHospitalar.setCriadoEm(dataAtual);
		contaHospitalar.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);

		final Integer serMatricula = servidorLogado.getId().getMatricula();
		
		//if :new.ser_matricula  is null then raise_application_error(-20000, 'Usuário não cadastrado como servidor');
		if(serMatricula == null){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.USUARIO_NAO_SERVIDOR);
		}
		
		/* ATUALIZA CARTAO PONTO DO SERVIDOR */
		contaHospitalar.setServidor(servidorLogado);
		contaHospitalar.setServidorManuseado(servidorLogado);
		
		/* INDICADOR DE SITUACAO DA CONTA */
		//if(contaHospitalar.getIndSituacao() == null){Linha nao migrada pois, conforme mapeamento, indSituacao nao pode ser nulo.
		if(contaHospitalar.getDtAltaAdministrativa() == null){
			contaHospitalar.setIndSituacao(DominioSituacaoConta.A);
		}else{
			contaHospitalar.setIndSituacao(DominioSituacaoConta.F);
			
			//#7040 RN1,RN3
			final String parametroAutorizacaoManual = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_INTEGRACAO_SISTEMA_SMS_PARA_AUTORIZACAO_ITENS_DA_CONTA);
			
			if(DominioSimNao.N.toString().equalsIgnoreCase(parametroAutorizacaoManual)){
				contaHospitalar.setIndEnviadoSms(DominioSimNao.S.toString());
				contaHospitalar.setIndAutorizadoSms(DominioSimNao.S.toString());
			}
			
			//Atualiza especialidade da conta quando atualizar dt alta cfv(27/05/2002)
			if(alterarEspecialidade){
				contaHospitalar.setEspecialidade(this.getFatkCth4RN().rnCthcVerEspCta(contaHospitalar.getSeq(), contaHospitalar.getDtAltaAdministrativa()));
			}
		}
		
		/* VERIFICA SE O CONVENIO E PLANO SAO VALIDOS PARA ESSE PROCEDIMENTO*/
		  this.verificarFiltroConvPlano(contaHospitalar, null, null, 
			contaHospitalar.getConvenioSaude() != null ? contaHospitalar.getConvenioSaude().getCodigo() : null,
			contaHospitalar.getConvenioSaudePlano() != null ? contaHospitalar.getConvenioSaudePlano().getId().getSeq() : null,
			contaHospitalar.getProcedimentoHospitalarInterno() != null ? contaHospitalar.getProcedimentoHospitalarInterno().getSeq() : null,
			contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null ? contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
			null, null);
	}
	//ORADB FATP_VER_FILTRO_CONV_PLANO 
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void verificarFiltroConvPlano(final FatContasHospitalares contaHosp,
			final Long capAtmNumero, final Long pmrSeq, final Short cnvCodigo,
			final Byte cspSeq, final Integer phiSeq, final Integer phiSeqRealizado, final Integer atdSeq,
			final String origem) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final FatConvenioSaudePlanoDAO convenioSaudePlanoDAO = this.getFatConvenioSaudePlanoDAO();
		final FatConvenioSaudeDAO fatConvenioSaudeDAO = getFatConvenioSaudeDAO();
		final VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		final FatLogErrorON logErrorON = this.getFatLogErrorON();
		
		String vModulo = "";
		
		if(cnvCodigo != null){
			final FatConvenioSaude convenioSaude = fatConvenioSaudeDAO.obterPorChavePrimaria(cnvCodigo);
			if(convenioSaude != null && (convenioSaude.getGrupoConvenio() != null && !CoreUtil.igual(DominioGrupoConvenio.S,convenioSaude.getGrupoConvenio()))){
				return;
			}
		}
		final FatConvenioSaudePlanoId convenioSaudePlanoId = new FatConvenioSaudePlanoId();
		convenioSaudePlanoId.setCnvCodigo(cnvCodigo);
		convenioSaudePlanoId.setSeq(cspSeq);
		final FatConvenioSaudePlano convenioSaudePlano = convenioSaudePlanoDAO.obterPorChavePrimaria(convenioSaudePlanoId);
		
		Boolean vErro = false;
		Boolean vInsere = false;
		DominioTipoPlano vPlano = (convenioSaudePlano!=null)?convenioSaudePlano.getIndTipoPlano():null;
		Short vCnvCodigo = cnvCodigo;
		Byte vCspSeq = cspSeq;
		if(contaHosp != null){
			vModulo = "INTERNAÇÃO - CONTA HOSPITALAR " + contaHosp.getSeq();
			if(convenioSaudePlano == null || convenioSaudePlano.getIndTipoPlano() == null){
				final FatConvenioSaudePlano convSaudePlano = contaHosp.getConvenioSaudePlano();
				vPlano = convSaudePlano.getIndTipoPlano();
				vCnvCodigo = convSaudePlano.getConvenioSaude().getCodigo();
				vCspSeq = convSaudePlano.getId().getSeq();				
			}
		}else if(capAtmNumero != null){
			vModulo = "APAC - ATENDIMENTO " + capAtmNumero;
			if(vPlano == null){
				if(atdSeq != null){
					final FatConvenioSaudePlano csp = convenioSaudePlanoDAO.obterConvenioSaudePlanoPeloAtendimentoDaConsulta(atdSeq);
					if(csp != null){
						vPlano = csp.getIndTipoPlano();
						vCnvCodigo = csp.getConvenioSaude().getCodigo();
						vCspSeq = csp.getId().getSeq();
					}
				}else{
					final FatConvenioSaudePlano convSPlano = convenioSaudePlanoDAO.obterConvenioSaudePlanoPeloNumeroDoAtendimentoPaciente(capAtmNumero);
					if(convSPlano != null){
						vPlano = convSPlano.getIndTipoPlano();
						vCnvCodigo = convSPlano.getConvenioSaude().getCodigo();
						vCspSeq = convSPlano.getId().getSeq();
					}
				}
				if(vPlano == null && pmrSeq != null){
					final FatConvenioSaudePlano convSaudePlano = convenioSaudePlanoDAO.obterConvenioSaudePlanoPeloProcedimentoAmbRealizado(pmrSeq);
					if(convSaudePlano != null){
						vPlano = convSaudePlano.getIndTipoPlano();
						vCnvCodigo = convSaudePlano.getConvenioSaude().getCodigo();
						vCspSeq = convSaudePlano.getId().getSeq();
					}
				}
				if(vPlano == null){
					vPlano = DominioTipoPlano.A;
					vCnvCodigo = 1;
					vCspSeq = 2;
				}
			}
		} else {
			vModulo = "AMBULATÓRIO - PROCED AMB ";
		}
		
		if(vPlano == null || (vPlano != null && StringUtils.isEmpty(vModulo)) || ( !("").equals(vModulo) && !vPlano.toString().equalsIgnoreCase(vModulo.substring(0, 1)) )
		   ){
			vErro = true;
		}
		
		Boolean vAux = false;
		if(!vErro && phiSeq != null){
			vAux = vFatAssociacaoProcedimentoDAO.consultarPorCodigoConvenioSeqConvenioSaudePlanoProcHospInterno(vCnvCodigo, vCspSeq, phiSeq);
			if(CoreUtil.igual(Boolean.FALSE,vAux)){
				vInsere = true;
			}
		}
		vAux = null;
		if(!vErro && phiSeqRealizado != null){
			vAux = vFatAssociacaoProcedimentoDAO.consultarPorCodigoConvenioSeqConvenioSaudePlanoProcHospInterno(vCnvCodigo, vCspSeq, phiSeqRealizado);
			if(CoreUtil.igual(Boolean.FALSE,vAux)){
				vInsere = true;
			}
		}
		
		DominioGrupoConvenio vAux2 = null;
		FatConvenioSaude convenioSaudeAux = null;
		if(vCnvCodigo != null) {
			convenioSaudeAux = fatConvenioSaudeDAO.obterPorChavePrimaria(vCnvCodigo);
		}
		if(convenioSaudeAux != null){
			vAux2 = convenioSaudeAux.getGrupoConvenio();
		}
		
		if(vErro && CoreUtil.igual(DominioGrupoConvenio.S,vAux2)){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.CONVENIO_PLANO_SAUDE_INCONSISTENTE_MODULO, vModulo, phiSeq);
		}else if(vInsere && CoreUtil.igual(DominioGrupoConvenio.S,vAux2)){
			final StringBuilder erro = new StringBuilder();
			erro.append("CONVENIO ")
			.append(vCnvCodigo)
			.append(" PLANO SAUDE ")
			.append(vCspSeq)
			.append(" INCONSISTENTE NO MÓDULO ")
			.append(vModulo.substring(0, 11))
			.append(" ORIGEM ")
			.append(StringUtils.isNotBlank(origem) ? origem : " ");
			
			final FatLogError fle = new FatLogError();
			fle.setModulo(vModulo.substring(0, 3) + "F");
			fle.setErro(erro.toString());
			if (contaHosp != null) {
				fle.setCthSeq(contaHosp.getSeq());
			}
			fle.setCapAtmNumero(capAtmNumero);
			fle.setPhiSeq(phiSeq);
			fle.setPhiSeqRealizado(phiSeqRealizado);
			fle.setPmrSeq(pmrSeq);
			fle.setCriadoEm(new Date());
			fle.setCriadoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
			fle.setPrograma("FATP_VER_FILTRO_CONV_PLANO");
			
			logErrorON.persistirLogError(fle);
		}
	} 
	
	//ORADB FATT_CTH_ARD
	public void executarAposDeletarContaHospitalar(final FatContasHospitalares contaHospitalar){
		this.inserirJournalContaHospitalar(contaHospitalar,DominioOperacoesJournal.DEL);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void inserirJournalContaHospitalar(final FatContasHospitalares contaHospitalar, final DominioOperacoesJournal operacao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final FatContaHospitalarJn contaHospitalarJn = new FatContaHospitalarJn();
		contaHospitalarJn.setNomeUsuario(servidorLogado.getUsuario());
		contaHospitalarJn.setCriadoPor(servidorLogado.getUsuario());
		contaHospitalarJn.setCriadoEm(new Date());
		contaHospitalarJn.setOperacao(operacao);
		contaHospitalarJn.setSeq(contaHospitalar.getSeq());
		contaHospitalarJn.setSerMatriculaManuseada(contaHospitalar.getServidorManuseado().getId().getMatricula());
		contaHospitalarJn.setSerVinCodigoManuseada(contaHospitalar.getServidorManuseado().getId().getVinCodigo());
		if(contaHospitalar.getServidorTemProfResponsavel()!= null){
			contaHospitalarJn.setSerMatriculaTemProfRespons(contaHospitalar.getServidorTemProfResponsavel().getId().getMatricula());
			contaHospitalarJn.setSerVinCodigoTemProfRespon(contaHospitalar.getServidorTemProfResponsavel().getId().getVinCodigo());
		}
		contaHospitalarJn.setDtIntAdministrativa(contaHospitalar.getDataInternacaoAdministrativa());
		contaHospitalarJn.setIndContaManuseada(CoreUtil.igual(Boolean.TRUE,contaHospitalar.getContaManuseada())? "S" : "N");
		contaHospitalarJn.setAcmSeq(contaHospitalar.getAcomodacao() != null ? contaHospitalar.getAcomodacao().getSeq().byteValue() : null);
		contaHospitalarJn.setCspCnvCodigo(contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo());
		contaHospitalarJn.setCspSeq(contaHospitalar.getConvenioSaudePlano().getId().getSeq());
		if(contaHospitalar.getServidor() != null){
			contaHospitalarJn.setSerMatricula(contaHospitalar.getServidor().getId().getMatricula());
			contaHospitalarJn.setSerVinCodigo(contaHospitalar.getServidor().getId().getVinCodigo());
		}
		contaHospitalarJn.setPhiSeq(contaHospitalar.getProcedimentoHospitalarInterno() != null ? contaHospitalar.getProcedimentoHospitalarInterno().getSeq() : null);
		
		contaHospitalarJn.setTacSeq(contaHospitalar.getTacSeq());
		contaHospitalarJn.setTacCnvCodigo(contaHospitalar.getTacCnvCodigo());
		contaHospitalarJn.setPhiSeqRealizado(contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null ? contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq() : null);
		contaHospitalarJn.setCthSeq(contaHospitalar.getSeq());
		contaHospitalarJn.setDtAltaAdministrativa(contaHospitalar.getDtAltaAdministrativa());
		contaHospitalarJn.setNroAih(contaHospitalar.getAih() != null ? contaHospitalar.getAih().getNroAih() : null);
		contaHospitalarJn.setDtEmisConta(contaHospitalar.getDtEmisConta());
		contaHospitalarJn.setIndContaReapresentada(CoreUtil.igual(Boolean.TRUE,contaHospitalar.getIndContaReapresentada()) ? "S" : "N");
		contaHospitalarJn.setIndImpAih(CoreUtil.igual(Boolean.TRUE,contaHospitalar.getIndImpAih()) ? "S" : "N");
		contaHospitalarJn.setNumDiariasAutorizadas(contaHospitalar.getNumDiariasAutorizadas());
		if(contaHospitalar.getSituacaoSaidaPaciente()!= null){
			contaHospitalarJn.setSiaMspSeq(contaHospitalar.getSituacaoSaidaPaciente().getId().getMspSeq());
			contaHospitalarJn.setSiaSeq(contaHospitalar.getSituacaoSaidaPaciente().getId().getSeq());
		}
		contaHospitalarJn.setCriadoPor(contaHospitalar.getCriadoPor());
		contaHospitalarJn.setAlteradoPor(contaHospitalar.getAlteradoPor());
		contaHospitalarJn.setCriadoEm(contaHospitalar.getCriadoEm());
		contaHospitalarJn.setAlteradoEm(contaHospitalar.getAlteradoEm());
		contaHospitalarJn.setRnNascidoVivo(contaHospitalar.getRnNascidoVivo());
		contaHospitalarJn.setRnNascidoMorto(contaHospitalar.getRnNascidoMorto());
		contaHospitalarJn.setRnSaidaAlta(contaHospitalar.getRnSaidaAlta());
		contaHospitalarJn.setRnSaidaTransferencia(contaHospitalar.getRnSaidaTransferencia());
		contaHospitalarJn.setRnSaidaObito(contaHospitalar.getRnSaidaObito());
		contaHospitalarJn.setIndCodigoExclusaoCritica(contaHospitalar.getExclusaoCritica() != null ? contaHospitalar.getExclusaoCritica().getCodigo(): "");
		contaHospitalarJn.setValorRealizado(contaHospitalar.getValorRealizado());
		contaHospitalarJn.setValor(contaHospitalar.getValor());
		contaHospitalarJn.setIndInfeccao(contaHospitalar.getIndInfeccao());
		contaHospitalarJn.setIndSituacao(contaHospitalar.getIndSituacao().toString());//TODO revisar!
		contaHospitalarJn.setValorSh(contaHospitalar.getValorSh());
		contaHospitalarJn.setValorUti(contaHospitalar.getValorUti());
		contaHospitalarJn.setValorUtie(contaHospitalar.getValorUtie());
		contaHospitalarJn.setValorSp(contaHospitalar.getValorSp());
		contaHospitalarJn.setValorAcomp(contaHospitalar.getValorAcomp());
		contaHospitalarJn.setValorRn(contaHospitalar.getValorRn());
		contaHospitalarJn.setValorSadt(contaHospitalar.getValorSadt());
		contaHospitalarJn.setValorHemat(contaHospitalar.getValorHemat());
		contaHospitalarJn.setValorTransp(contaHospitalar.getValorTransp());
		contaHospitalarJn.setValorOpm(contaHospitalar.getValorOpm());
		contaHospitalarJn.setDciCodigoDcih(contaHospitalar.getDocumentoCobrancaAih() != null ? contaHospitalar.getDocumentoCobrancaAih().getCodigoDcih() : null);
		contaHospitalarJn.setMdsSeq(contaHospitalar.getMotivoDesdobramento() != null ? contaHospitalar.getMotivoDesdobramento().getSeq() : null);
		contaHospitalarJn.setTcsSeq(contaHospitalar.getTipoClassifSecSaude() != null ? contaHospitalar.getTipoClassifSecSaude().getSeq() : null);
		contaHospitalarJn.setTahSeq(contaHospitalar.getTipoAih() != null ? contaHospitalar.getTipoAih().getSeq() : null );
		contaHospitalarJn.setCthSeqReapresentada(contaHospitalar.getContaHospitalarReapresentada() != null ? contaHospitalar.getContaHospitalarReapresentada().getSeq() : null );
		contaHospitalarJn.setValorAnestesista(contaHospitalar.getValorAnestesista());
		contaHospitalarJn.setPontosSadt(contaHospitalar.getPontosSadt());
		contaHospitalarJn.setPontosAnestesista(contaHospitalar.getPontosAnestesista());
		contaHospitalarJn.setPontosCirurgiao(contaHospitalar.getPontosCirurgiao());
		contaHospitalarJn.setValorProcedimento(contaHospitalar.getValorProcedimento());
		contaHospitalarJn.setDiasUtiMesInicial(contaHospitalar.getDiasUtiMesInicial());
		contaHospitalarJn.setDiasUtiMesAnterior(contaHospitalar.getDiasUtiMesAnterior());
		contaHospitalarJn.setDiasUtiMesAlta(contaHospitalar.getDiasUtiMesAlta());
		contaHospitalarJn.setDiariasAcompanhante(contaHospitalar.getDiariasAcompanhante());
		contaHospitalarJn.setDiasPermanenciaMaior(contaHospitalar.getDiasPermanenciaMaior());
		contaHospitalarJn.setIndTipoUti(contaHospitalar.getIndTipoUti() != null?String.valueOf(contaHospitalar.getIndTipoUti().getCodigo()):null);
		contaHospitalarJn.setDtEncerramento(contaHospitalar.getDtEncerramento());
		contaHospitalarJn.setIndImpressaoEspelho(contaHospitalar.getIndImpressaoEspelho());
		contaHospitalarJn.setTipoAltaUti(contaHospitalar.getTipoAltaUti() != null?String.valueOf(contaHospitalar.getTipoAltaUti().getCodigo()):null);
		contaHospitalarJn.setPesoRn(contaHospitalar.getPesoRn());
		contaHospitalarJn.setMesesGestacao(contaHospitalar.getMesesGestacao());
		contaHospitalarJn.setUsuarioPrevia(contaHospitalar.getUsuarioPrevia());
		contaHospitalarJn.setDataPrevia(contaHospitalar.getDataPrevia());
		contaHospitalarJn.setIndIdadeUti(contaHospitalar.getIndIdadeUti());
		contaHospitalarJn.setRjcSeq((contaHospitalar.getMotivoRejeicao()!=null)?contaHospitalar.getMotivoRejeicao().getSeq():null);
		contaHospitalarJn.setEspSeq(contaHospitalar.getEspecialidade() != null ? contaHospitalar.getEspecialidade().getSeq(): null);
		contaHospitalarJn.setNroSeqaih5(contaHospitalar.getNroSeqaih5());
		contaHospitalarJn.setNroSisprenatal(contaHospitalar.getNroSisprenatal());
		contaHospitalarJn.setIndAutorizaFat(contaHospitalar.getIndAutorizaFat());
		contaHospitalarJn.setIndAutorizadoSms(contaHospitalar.getIndAutorizadoSms());
		contaHospitalarJn.setDtEnvioSms(contaHospitalar.getDtEnvioSms());
		contaHospitalarJn.setIndEnviadoSms(contaHospitalar.getIndEnviadoSms());
		
		final FatContaHospitalarJnDAO fatContaHospitalarJnDAO = getFatContaHospitalarJnDAO();
		fatContaHospitalarJnDAO.persistir(contaHospitalarJn);
	}
	
	//ORADB FATT_CTH_ARU
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void executarAposAtualizarContaHospitalar(final FatContasHospitalares newCtaHosp, FatContasHospitalares oldCtaHosp, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		
		if(CoreUtil.modificados(newCtaHosp.getIndSituacao(), oldCtaHosp.getIndSituacao())){
			//fatk_cth4_rn_un.RN_CTHP_ATU_TRANSP(:new.seq, :new.dt_int_administrativa, :new.phi_seq, :new.nro_aih, :new.csp_cnv_codigo, :new.csp_seq, :new.ind_situacao);
			this.getFatkCth4RN().rnCthpAtuTransp(newCtaHosp.getSeq(), 
					newCtaHosp.getDataInternacaoAdministrativa(), 
					newCtaHosp.getProcedimentoHospitalarInterno() != null ? newCtaHosp.getProcedimentoHospitalarInterno().getSeq() : null,
					newCtaHosp.getAih() != null ? newCtaHosp.getAih().getNroAih() : null, 
					newCtaHosp.getConvenioSaudePlano() != null ? newCtaHosp.getConvenioSaudePlano().getId().getCnvCodigo() : null,
					newCtaHosp.getConvenioSaudePlano() != null ? newCtaHosp.getConvenioSaudePlano().getId().getSeq() : null,
					newCtaHosp.getIndSituacao());
		}
		if(CoreUtil.modificados(newCtaHosp.getProcedimentoHospitalarInterno(), oldCtaHosp.getProcedimentoHospitalarInterno()) && newCtaHosp.getSeq() == null){
			/* Se a necessidade do uso da variavel FATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL é garantir que o 
			 * metodo getFatkCth4RN().rnCthpAtuSsmSol Nao seja executado
			 * ao mesmo tempo (concorrentemente) ou nao seja executado mais de uma vez por acao do usuário
			 * precisamos fazer de outra forma, pois com EJB não podemos colocar coisas na sessão e recuperar-la
			 * posteriormente. Não temos garantia que serah na mesma acao do usuario e nem se será na acao 
			 * do mesmo usuario.
			 * E NAO teremos acesso a sessao web de dentro de um EJB, pelo menos nao eh um boa pratica. 
			 */
			//IF NOT fatk_cth4_rn.v_atu_cth_ssm_sol THEN
			//if(!((Boolean)obterContextoSessao("FATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL"))){
				//fatk_cth4_rn.v_atu_cth_ssm_sol := TRUE;
				//atribuirContextoSessao(VariaveisSessaoEnum.FATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL, Boolean.TRUE);
           		//fatk_cth4_rn_un.rn_cthp_atu_ssm_sol(:new.seq, :new.phi_seq);
           		this.getFatkCth4RN().rnCthpAtuSsmSol(newCtaHosp.getSeq()
           				, newCtaHosp.getProcedimentoHospitalarInterno() != null ? newCtaHosp.getProcedimentoHospitalarInterno().getSeq() : null
           				, nomeMicrocomputador
           				, dataFimVinculoServidor
           				, true);
           		//fatk_cth4_rn.v_atu_cth_ssm_sol := FALSE;
           		//atribuirContextoSessao(VariaveisSessaoEnum.FATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL, Boolean.FALSE);
			//}//END IF;
		}

		oldCtaHosp = this.getFatContasHospitalaresDAO().obterFatContaHospitalarCompleto(oldCtaHosp.getSeq());
		
		if(oldCtaHosp != null && ((CoreUtil.modificados(oldCtaHosp.getSeq(), newCtaHosp.getSeq()))
			|| (CoreUtil.modificados(oldCtaHosp.getServidorManuseado(), newCtaHosp.getServidorManuseado())) 
			|| (CoreUtil.modificados(oldCtaHosp.getServidorTemProfResponsavel(), newCtaHosp.getServidorTemProfResponsavel())) 
			|| (CoreUtil.modificados(oldCtaHosp.getDataInternacaoAdministrativa(), newCtaHosp.getDataInternacaoAdministrativa()))
			|| (CoreUtil.modificados(oldCtaHosp.getContaManuseada(), newCtaHosp.getContaManuseada()))
			|| (CoreUtil.modificados(oldCtaHosp.getAcomodacao(), newCtaHosp.getAcomodacao()))
			|| (CoreUtil.modificados(oldCtaHosp.getConvenioSaudePlano(), newCtaHosp.getConvenioSaudePlano()))
			|| (CoreUtil.modificados(oldCtaHosp.getServidor(), newCtaHosp.getServidor()))
			|| (CoreUtil.modificados(oldCtaHosp.getProcedimentoHospitalarInterno(), newCtaHosp.getProcedimentoHospitalarInterno()))
			|| (CoreUtil.modificados(oldCtaHosp.getTacCnvCodigo(), newCtaHosp.getTacCnvCodigo()))
			|| (CoreUtil.modificados(oldCtaHosp.getTacSeq(), newCtaHosp.getTacSeq()))
			|| (CoreUtil.modificados(oldCtaHosp.getProcedimentoHospitalarInternoRealizado(), newCtaHosp.getProcedimentoHospitalarInternoRealizado()))
			|| (CoreUtil.modificados(oldCtaHosp.getContaHospitalar(), newCtaHosp.getContaHospitalar()))
			|| (CoreUtil.modificados(oldCtaHosp.getDtAltaAdministrativa(), newCtaHosp.getDtAltaAdministrativa()))
			|| (CoreUtil.modificados(oldCtaHosp.getAih(), newCtaHosp.getAih()))
			|| (CoreUtil.modificados(oldCtaHosp.getDtEmisConta(), newCtaHosp.getDtEmisConta()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndContaReapresentada(), newCtaHosp.getIndContaReapresentada()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndImpAih(), newCtaHosp.getIndImpAih()))
			|| (CoreUtil.modificados(oldCtaHosp.getNumDiariasAutorizadas(), newCtaHosp.getNumDiariasAutorizadas()))
			|| (CoreUtil.modificados(oldCtaHosp.getSituacaoSaidaPaciente(), newCtaHosp.getSituacaoSaidaPaciente()))
			|| (CoreUtil.modificados(oldCtaHosp.getCriadoPor(), newCtaHosp.getCriadoPor()))
			|| (CoreUtil.modificados(oldCtaHosp.getCriadoEm(), newCtaHosp.getCriadoEm()))
			|| (CoreUtil.modificados(oldCtaHosp.getAlteradoPor(), newCtaHosp.getAlteradoPor()))
			|| (CoreUtil.modificados(oldCtaHosp.getAlteradoEm(), newCtaHosp.getAlteradoEm()))
			|| (CoreUtil.modificados(oldCtaHosp.getRnNascidoVivo(), newCtaHosp.getRnNascidoVivo()))
			|| (CoreUtil.modificados(oldCtaHosp.getRnNascidoMorto(), newCtaHosp.getRnNascidoMorto()))
			|| (CoreUtil.modificados(oldCtaHosp.getRnSaidaAlta(), newCtaHosp.getRnSaidaAlta()))
			|| (CoreUtil.modificados(oldCtaHosp.getRnSaidaTransferencia(), newCtaHosp.getRnSaidaTransferencia()))
			|| (CoreUtil.modificados(oldCtaHosp.getRnSaidaObito(), newCtaHosp.getRnSaidaObito()))
			|| (CoreUtil.modificados(oldCtaHosp.getExclusaoCritica(), newCtaHosp.getExclusaoCritica()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorRealizado(), newCtaHosp.getValorRealizado()))
			|| (CoreUtil.modificados(oldCtaHosp.getValor(), newCtaHosp.getValor()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndInfeccao(), newCtaHosp.getIndInfeccao()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndSituacao(), newCtaHosp.getIndSituacao()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorSh(), newCtaHosp.getValorSh()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorUti(), newCtaHosp.getValorUti()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorUtie(), newCtaHosp.getValorUtie()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorSp(), newCtaHosp.getValorSp()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorAcomp(), newCtaHosp.getValorAcomp()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorRn(), newCtaHosp.getValorRn()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorSadt(), newCtaHosp.getValorSadt()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorHemat(), newCtaHosp.getValorHemat()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorTransp(), newCtaHosp.getValorTransp()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorOpm(), newCtaHosp.getValorOpm()))
			|| (CoreUtil.modificados(oldCtaHosp.getDocumentoCobrancaAih(), newCtaHosp.getDocumentoCobrancaAih()))
			|| (CoreUtil.modificados(oldCtaHosp.getMotivoDesdobramento(), newCtaHosp.getMotivoDesdobramento()))
			|| (CoreUtil.modificados(oldCtaHosp.getTipoClassifSecSaude(), newCtaHosp.getTipoClassifSecSaude()))
			|| (CoreUtil.modificados(oldCtaHosp.getTipoAih(), newCtaHosp.getTipoAih()))
			|| (CoreUtil.modificados(oldCtaHosp.getContaHospitalarReapresentada(), newCtaHosp.getContaHospitalarReapresentada()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorAnestesista(), newCtaHosp.getValorAnestesista()))
			|| (CoreUtil.modificados(oldCtaHosp.getPontosSadt(), newCtaHosp.getPontosSadt()))
			|| (CoreUtil.modificados(oldCtaHosp.getPontosAnestesista(), newCtaHosp.getPontosAnestesista()))
			|| (CoreUtil.modificados(oldCtaHosp.getPontosCirurgiao(), newCtaHosp.getPontosCirurgiao()))
			|| (CoreUtil.modificados(oldCtaHosp.getValorProcedimento(), newCtaHosp.getValorProcedimento()))
			|| (CoreUtil.modificados(oldCtaHosp.getDiasUtiMesInicial(), newCtaHosp.getDiasUtiMesInicial()))
			|| (CoreUtil.modificados(oldCtaHosp.getDiasUtiMesAnterior(), newCtaHosp.getDiasUtiMesAnterior()))
			|| (CoreUtil.modificados(oldCtaHosp.getDiasUtiMesAlta(), newCtaHosp.getDiasUtiMesAlta()))
			|| (CoreUtil.modificados(oldCtaHosp.getDiariasAcompanhante(), newCtaHosp.getDiariasAcompanhante()))
			|| (CoreUtil.modificados(oldCtaHosp.getDiasPermanenciaMaior(), newCtaHosp.getDiasPermanenciaMaior()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndTipoUti(), newCtaHosp.getIndTipoUti()))
			|| (CoreUtil.modificados(oldCtaHosp.getDtEncerramento(), newCtaHosp.getDtEncerramento()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndImpressaoEspelho(), newCtaHosp.getIndImpressaoEspelho()))
			|| (CoreUtil.modificados(oldCtaHosp.getTipoAltaUti(), newCtaHosp.getTipoAltaUti()))
			|| (CoreUtil.modificados(oldCtaHosp.getPesoRn(), newCtaHosp.getPesoRn()))
			|| (CoreUtil.modificados(oldCtaHosp.getMesesGestacao(), newCtaHosp.getMesesGestacao()))
			|| (CoreUtil.modificados(oldCtaHosp.getUsuarioPrevia(), newCtaHosp.getUsuarioPrevia()))
			|| (CoreUtil.modificados(oldCtaHosp.getDataPrevia(), newCtaHosp.getDataPrevia()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndIdadeUti(), newCtaHosp.getIndIdadeUti()))
			|| (CoreUtil.modificados(oldCtaHosp.getEspecialidade(), newCtaHosp.getEspecialidade()))
			|| (CoreUtil.modificados(oldCtaHosp.getMotivoRejeicao(), newCtaHosp.getMotivoRejeicao()))
			|| (CoreUtil.modificados(oldCtaHosp.getNroSeqaih5(), newCtaHosp.getNroSeqaih5()))
			|| (CoreUtil.modificados(oldCtaHosp.getNroSisprenatal(), newCtaHosp.getNroSisprenatal()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndAutorizaFat(), newCtaHosp.getIndAutorizaFat()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndAutorizadoSms(), newCtaHosp.getIndAutorizadoSms()))
			|| (CoreUtil.modificados(oldCtaHosp.getDtEnvioSms(), newCtaHosp.getDtEnvioSms()))
			|| (CoreUtil.modificados(oldCtaHosp.getIndEnviadoSms(), newCtaHosp.getIndEnviadoSms())))
		){
			this.inserirJournalContaHospitalar(oldCtaHosp,DominioOperacoesJournal.UPD);
		}
	}
	
	//ORADB FATP_ENFORCE_CTH_RULES
	public void executarEnforceContaHospitalar(final FatContasHospitalares newCtaHosp, final FatContasHospitalares oldCtaHosp, final TipoOperacaoEnum tipoOperacao, final Date dataFimVinculoServidor) throws BaseException{
		final IAghuFacade aghuFacade = this.getAghuFacade();
		final DominioGrupoConvenio grupoConvenio = (newCtaHosp.getConvenioSaudePlano() != null ) ? (newCtaHosp.getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio()) : (newCtaHosp.getConvenioSaude() != null ? newCtaHosp.getConvenioSaude().getGrupoConvenio() : null);
		
		if(CoreUtil.igual(TipoOperacaoEnum.UPDATE,tipoOperacao) && CoreUtil.igual(DominioGrupoConvenio.S,grupoConvenio) && (CoreUtil.modificados(newCtaHosp.getDtAltaAdministrativa(), oldCtaHosp.getDtAltaAdministrativa()))){
			final List<AghAtendimentos> listaAtendimentos = aghuFacade.obterAtendimentosDeContasInternacaoPorContaHospitalar(newCtaHosp.getSeq());
			Integer opcao = 1;
			for (final AghAtendimentos atendimento : listaAtendimentos) {

				/*Integração AFA X FAT*/
				//TODO Descomentar quando a tabela AfaDispensacaoMdtos for carregada (nao faz parte do escopo da primeira versao do faturamento)
				//FATK_INTERFACE_AFA.RN_FATP_INS_DISP(v_atd_seq, l_cth_saved_row.dt_int_administrativa, l_cth_row_new.dt_alta_administrativa, l_cth_row_new.seq, v_opcao);
				getFaturamentoFatkInterfaceAfaRN().rnFatpInsDisp(atendimento.getSeq(), oldCtaHosp.getDataInternacaoAdministrativa(), newCtaHosp.getDtAltaAdministrativa(), newCtaHosp.getSeq(), opcao, dataFimVinculoServidor, null);

	           /*Integração ANU X FAT*/              
	           //FATK_INTERFACE_ANU.RN_FATP_INS_NUTR_ENT (v_atd_seq, l_cth_saved_row.dt_int_administrativa, l_cth_row_new.dt_alta_administrativa, l_cth_row_new.seq, v_opcao);                   
				getFaturamentoFatkInterfaceAnuRN().rnFatpInsNutrEnt(atendimento.getSeq(), oldCtaHosp.getDataInternacaoAdministrativa(), newCtaHosp.getDtAltaAdministrativa(), newCtaHosp.getSeq(), opcao);    

				/*Integração entre MCO x FAT*/                                          
		        //TODO Descomentar quando o metodo rnFatpInsCentroObstetrico for implementado.
				//FATK_INTERFACE_MCO.RN_FATP_INS_CENTRO_OBSTETRICO(v_atd_seq, l_cth_row_new.seq, l_cth_saved_row.dt_int_administrativa, l_cth_row_new.dt_alta_administrativa, v_opcao);   
				getFaturamentoFatkInterfaceMcoRN().rnFatpInsCentroObstetrico(atendimento.getSeq(), newCtaHosp.getSeq(), oldCtaHosp.getDataInternacaoAdministrativa(), newCtaHosp.getDtAltaAdministrativa(), opcao);
//				validarInternacaoPerinato(newCtaHosp, oldCtaHosp, dataFimVinculoServidor, aghuFacade, opcao, atendimento);
				opcao++;
			}
			
			//ETB 270606 Cancela NPT'S duplas
			this.executarCancelamentoDeNtpDupla(newCtaHosp.getSeq(), dataFimVinculoServidor);
		}
	}
	
	protected String validarInternacaoPerinato( final FatContasHospitalares newCtaHosp, final FatContasHospitalares oldCtaHosp, final Date dataFimVinculoServidor, final IAghuFacade aghuFacade, Integer opcao, final AghAtendimentos atendimento) throws ApplicationBusinessException, BaseException {
		Boolean moduloPerinatoAtivo = cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.PERINATOLOGIA.getDescricao());
		String mensagem = null;
		if(!moduloPerinatoAtivo){
			//Enviar mensagem pra controller sem throws
			mensagem = "";
		} else {
			//3. Se o valor do campo OPCAO recebido por parâmetro for igual a 1 executar a RN03.
			validarOpcao(newCtaHosp, opcao);
			
			if(atendimento.getInternacao() != null) {
				
				McoNascimentos nasc39319 = perinatologiaFacade.obterMcoNascimento(atendimento.getGsoPacCodigo(), atendimento.getGsoSeqp(), atendimento.getInternacao().getDthrInternacao(), newCtaHosp.getDataInternacaoAdministrativa());
				
				if(nasc39319 != null){
					
					List<AtendimentoNascimentoVO> listaAtendimentoNascimentoVO = aghuFacade.pesquisarAtendimentosPorPacienteGestacaoEDataDeInternacao(atendimento.getInternacao().getSeq(), 
							nasc39319.getId().getGsoPacCodigo(), 
							nasc39319.getId().getGsoSeqp(), 
							oldCtaHosp.getDataInternacaoAdministrativa(), 
							oldCtaHosp.getDtAltaAdministrativa());
					
					if(listaAtendimentoNascimentoVO != null){
						for(AtendimentoNascimentoVO vo : listaAtendimentoNascimentoVO){
							if(DominioTipoNascimento.P.toString().equals(vo.getTipo())){
								executarRegrasParaParto(newCtaHosp, dataFimVinculoServidor, aghuFacade, atendimento, vo);
							} else {
								executarRegrasParaCesariana(newCtaHosp, dataFimVinculoServidor, aghuFacade, atendimento);
							}
						}		
					}	
				}
			}
		}
		
		return mensagem;
	}
	
	private String executarRegrasParaCesariana(final FatContasHospitalares newCtaHosp, final Date dataFimVinculoServidor, final IAghuFacade aghuFacade, final AghAtendimentos atendimento) throws ApplicationBusinessException, BaseException {
		McoNascimentos nasc39320 = perinatologiaFacade.obterMcoNascimento(atendimento.getGsoPacCodigo(), atendimento.getGsoSeqp(), null, null);
		String mensagem = null;
		List<AtendimentoNascimentoVO> listaAtdNasc = aghuFacade.pesquisarAtendimentosPorPacienteGestacao(atendimento.getInternacao().getSeq(), nasc39320.getId().getGsoPacCodigo(), nasc39320.getId().getGsoSeqp());
		this.executarClassificacaoNascimento(listaAtdNasc, newCtaHosp);
		
		AghParametros incentivoRegistroParam = this.buscarAghParametro(AghuParametrosEnum.P_COD_PHI_INCENTIVO_REGISTRO_CIVIL_NASCIMENTO);
		
		if(incentivoRegistroParam == null || incentivoRegistroParam.getVlrNumerico() == null){
			//Enviar mensagem
			mensagem = "";
		} else {
			List<AghAtendimentosPacienteCnsVO> listaAghAtendimentosPacienteCnsVO = this.getAghuFacade().buscarAtendimentosFetchAipPacientesDadosCns(atendimento.getSeq());
			if(listaAghAtendimentosPacienteCnsVO != null){
				inserirItemContaHospitalar(newCtaHosp, dataFimVinculoServidor, incentivoRegistroParam, listaAghAtendimentosPacienteCnsVO);
			}
		}
		
		return mensagem;
	}
	private void inserirItemContaHospitalar(final FatContasHospitalares newCtaHosp, final Date dataFimVinculoServidor, AghParametros incentivoRegistroParam, List<AghAtendimentosPacienteCnsVO> listaAghAtendimentosPacienteCnsVO) throws BaseException {
		for(AghAtendimentosPacienteCnsVO voAghAtendimentosPacienteCns : listaAghAtendimentosPacienteCnsVO){
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(voAghAtendimentosPacienteCns.getMatricula(), voAghAtendimentosPacienteCns.getVinCodigo());
			this.inserirItemContaHospitalar( newCtaHosp.getSeq(), incentivoRegistroParam.getVlrNumerico().intValue(), dataFimVinculoServidor, voAghAtendimentosPacienteCns.getCriadoEm(), servidor);  
		}
	}
	private String executarRegrasParaParto( final FatContasHospitalares newCtaHosp, final Date dataFimVinculoServidor, final IAghuFacade aghuFacade, final AghAtendimentos atendimento, AtendimentoNascimentoVO vo) throws ApplicationBusinessException, BaseException {
		AghParametros parametro = this.buscarAghParametro(AghuParametrosEnum.P_COD_PHI_PARTO_NORMAL);
		String mensagem = null;
		if(parametro == null || parametro.getVlrNumerico() == null){
			mensagem = "";//Adicionar mensagem
		} else {
			AghEquipes equipes = aghuFacade.obterEquipe(vo.getEquipeSeq());
			this.inserirItemContaHospitalar(newCtaHosp.getSeq(), newCtaHosp.getProcedimentoHospitalarInterno().getSeq(), dataFimVinculoServidor, vo.getDataNascimento(), equipes.getProfissionalResponsavel()); 
			AghParametros analgesiaParam = this.buscarAghParametro(AghuParametrosEnum.P_ANALGESIA_PARTO);
			
			if(analgesiaParam == null || analgesiaParam.getVlrNumerico() == null){
				mensagem = "";//Adicionar mensagem
			} else {
				List<RapServidores> anestesistas = blocoCirurgicoFacade.listarFichasAnestesias(atendimento.getSeq(), vo.getGsoPacCodigo(), vo.getGsoSeqp().shortValue(), DominioIndPendenteAmbulatorio.V, analgesiaParam.getVlrNumerico().intValue(), DominioSituacaoExame.R);
				validarAnalgesia(newCtaHosp, dataFimVinculoServidor, vo, anestesistas);
			}
		}
		return mensagem;
	}
	
	private String validarAnalgesia(final FatContasHospitalares newCtaHosp, final Date dataFimVinculoServidor, AtendimentoNascimentoVO vo, List<RapServidores> anestesistas) throws ApplicationBusinessException, BaseException {
		String mensagem = null;
		AghParametros obstetriciaParam = this.buscarAghParametro(AghuParametrosEnum.P_COD_PHI_ANALGESIA_OBSTETRICIA);
		if(obstetriciaParam == null || obstetriciaParam.getVlrNumerico() == null){
			mensagem = "";//Adicionar mensagem
		} else {
			if(!anestesistas.isEmpty()){
				this.inserirItemContaHospitalar(newCtaHosp.getSeq(), obstetriciaParam.getVlrNumerico().intValue(), dataFimVinculoServidor, vo.getDataNascimento(),anestesistas.get(0)); 
			}
		}
		return mensagem;
	}
	
	private void validarOpcao(final FatContasHospitalares newCtaHosp,Integer opcao) throws ApplicationBusinessException {
		if(opcao == 1){
			List<FatItemContaHospitalar> listaFatItemContaHospitalar = this.faturamentoFacade.listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(newCtaHosp.getSeq(), DominioIndOrigemItemContaHospitalar.MCO);
			if(!listaFatItemContaHospitalar.isEmpty()){
				for(FatItemContaHospitalar entidadeFatItemContaHospitalar : listaFatItemContaHospitalar){
					this.itemContaHospitalarRN.executarAntesExcluirItemContaHospitalar(entidadeFatItemContaHospitalar.getIndSituacao());
					this.faturamentoFacade.removerFatItemContaHospitalar(entidadeFatItemContaHospitalar, Boolean.TRUE);
					this.itemContaHospitalarRN.executarAposExcluirItemContaHospitalar(entidadeFatItemContaHospitalar);
				}
			}
		}
	}
	
	private void executarClassificacaoNascimento(List<AtendimentoNascimentoVO> listaAtdNasc, 
			FatContasHospitalares fatContasHospitalares) {
		
		Byte contagemNascidosVivos = 0;
		Byte contagemObitos = 0;
		
		if(listaAtdNasc != null && !listaAtdNasc.isEmpty()){
			for(AtendimentoNascimentoVO atdNascVO :  listaAtdNasc){
				if(atdNascVO.getRnClassificacao().equals(DominioRNClassificacaoNascimento.NAV.toString())){
					contagemNascidosVivos++;
				} else if(atdNascVO.getRnClassificacao().equals(DominioRNClassificacaoNascimento.ABO.toString())||
						atdNascVO.getRnClassificacao().equals(DominioRNClassificacaoNascimento.NAM.toString())){
					contagemObitos++;
				}
			}
		}
		
		this.atualizarClassificacoes(fatContasHospitalares, contagemNascidosVivos, contagemObitos);
		
		this.getFatContasHospitalaresDAO().atualizar(fatContasHospitalares);
		
		DominioVivoMorto vivo = validarCidVido(contagemNascidosVivos);
		DominioVivoMorto morto = validarCidVido(contagemObitos);
		
		List<CidVO> listaCidVO = aghuFacade.obterCidPorDominioVivoMorto(vivo, morto);
		
		validarCIds(fatContasHospitalares, listaCidVO);
	}
	
	private void validarCIds(FatContasHospitalares fatContasHospitalares,List<CidVO> listaCidVO) {
		
		if(listaCidVO != null && !listaCidVO.isEmpty()){
			
			List<FatCidContaHospitalar> listaFatCidContaHospitalar = getFatCidContaHospitalarDAO().buscaFatCidContaHospitalar(fatContasHospitalares.getSeq(), listaCidVO.get(0).getSeq());
			
			validarPersistirFatCidContaHospitalar(fatContasHospitalares, listaCidVO, listaFatCidContaHospitalar);
		}
	}
	private void validarPersistirFatCidContaHospitalar(
			FatContasHospitalares fatContasHospitalares,
			List<CidVO> listaCidVO,
			List<FatCidContaHospitalar> listaFatCidContaHospitalar) {
		
		if(listaFatCidContaHospitalar.isEmpty()){
			persistirFatCidContaHospitalar(fatContasHospitalares, listaCidVO);
		}
	}
	private void persistirFatCidContaHospitalar(
			FatContasHospitalares fatContasHospitalares, List<CidVO> listaCidVO) {
		FatCidContaHospitalar fatCidContaHospitalar = new FatCidContaHospitalar();
		FatCidContaHospitalarId id = new FatCidContaHospitalarId();
		id.setCidSeq(listaCidVO.get(0).getSeq());
		id.setCthSeq(fatContasHospitalares.getSeq());
		id.setPrioridadeCid(DominioPrioridadeCid.S);
		fatCidContaHospitalar.setId(id);
		
		getFatCidContaHospitalarDAO().persistir(fatCidContaHospitalar);
	}
	
	private DominioVivoMorto validarCidVido(Byte quantidade){
		if(quantidade == 0){
			return DominioVivoMorto.Z;
		} else if(quantidade == 1){
			return DominioVivoMorto.U;
		} else if(quantidade == 2){
			return DominioVivoMorto.D;
		} else {
			return DominioVivoMorto.M;
		}
	}
	private void atualizarClassificacoes(
			FatContasHospitalares fatContasHospitalares,
			Byte contagemNascidosVivos, Byte contagemObitos) {
		
		fatContasHospitalares.setRnNascidoVivo(contagemNascidosVivos);
		fatContasHospitalares.setRnNascidoMorto(contagemObitos);
		fatContasHospitalares.setRnSaidaAlta(contagemNascidosVivos);
	}
	private void inserirItemContaHospitalar(
			Integer cthSeq,
			Integer phiSeq,
			Date dataFimVinculoServidor,
			Date dataNascimento,
			RapServidores responsavel) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		Short seq = faturamentoFacade.buscarFatItemContaHospitalarMaxSeq(cthSeq);
		
		FatItemContaHospitalar fatItemContaHospitalar = new FatItemContaHospitalar();
		FatItemContaHospitalarId id = new FatItemContaHospitalarId();
		id.setCthSeq(cthSeq);
		id.setSeq(seq);
		fatItemContaHospitalar.setId(id);
		fatItemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
		
		FatProcedHospInternos phi = faturamentoFacade.obterFatProcedHospInternosPorChavePrimaria(phiSeq);
		fatItemContaHospitalar.setProcedimentoHospitalarInterno(phi);
		
		fatItemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.MCO);
		fatItemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
		fatItemContaHospitalar.setQuantidadeRealizada((short)1);
		fatItemContaHospitalar.setValor(BigDecimal.ZERO);
		fatItemContaHospitalar.setDthrRealizado(dataNascimento);
		
		fatItemContaHospitalar.setServidor(responsavel);
		getItemContaHospitalarON().inserirItemContaHospitalarSemValidacoesForms(fatItemContaHospitalar, servidorLogado, dataFimVinculoServidor, false);
	}
	
	//ORADB FATP_CANC_NPT_DUPLA
	public void executarCancelamentoDeNtpDupla(final Integer cthSeq, final Date dataFimVinculoServidor) throws BaseException{
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		final ItemContaHospitalarON itemContaHospitalarON = getItemContaHospitalarON();
		Integer qtdVezes = 0;
		Integer qtdUpdate = 0;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final List<FatItemContaHospitalarVO> listaItemContaHospitalarVO = fatItemContaHospitalarDAO.listarFatItemContaHospitalarPorContaHospitalar(cthSeq);
		if(listaItemContaHospitalarVO != null && !listaItemContaHospitalarVO.isEmpty()){
			/*Cancela as NPT'S duplas, triplas ... (-1), para o mesmo dia*/
			for (final FatItemContaHospitalarVO itemContaHospitalarVO : listaItemContaHospitalarVO) {
				qtdVezes = itemContaHospitalarVO.getCount() - 1;
				qtdUpdate = 0;
				
				final List<FatItemContaHospitalar> listaItensContaHospitalar = fatItemContaHospitalarDAO.listarItensContaHospAtivaPorProcedHospInternoDthrRealizadoEContaHosp(itemContaHospitalarVO.getPhiSeq(), itemContaHospitalarVO.getDthrRealizado(), cthSeq);
				if(listaItensContaHospitalar!= null && !listaItensContaHospitalar.isEmpty()){
					while(!CoreUtil.igual(qtdUpdate,qtdVezes)){
					
						final FatItemContaHospitalar ich = listaItensContaHospitalar.get(qtdUpdate);
						FatItemContaHospitalar ichOld = null;
						try{
							ichOld = faturamentoFacade.clonarItemContaHospitalar(ich);
						}catch (final Exception e) {
							logError("Exceção capturada: ", e);
							throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
						}
						ich.setIndSituacao(DominioSituacaoItenConta.C);
						ich.setNumConta("CANCELADO");//TODO Alterar para domínio?
						itemContaHospitalarON.atualizarItemContaHospitalarSemValidacoesForms(ich, servidorLogado, ichOld, dataFimVinculoServidor);
						qtdUpdate++;
					}
				}
			}
		}
	}
	
	 //ORADB FATT_CTH_ASU
	public void executarStatementAposAtualizarContaHospitalar(final FatContasHospitalares newCtaHosp, final FatContasHospitalares oldCtaHosp, final Date dataFimVinculoServidor) throws BaseException{
		
		this.executarProcessContaHospitalar(newCtaHosp, oldCtaHosp, TipoOperacaoEnum.UPDATE, dataFimVinculoServidor);
	}
	
	//ORADB FATT_CTH_ASD
	public void executarStatementAposDeletarContaHospitalar(final FatContasHospitalares newCtaHosp, final FatContasHospitalares oldCtaHosp, final Date dataFimVinculoServidor) throws BaseException{
		
		this.executarProcessContaHospitalar(newCtaHosp, oldCtaHosp, TipoOperacaoEnum.DELETE, dataFimVinculoServidor);
	}
	
	//ORADB PROCESS_CTH_ROWS
	public void executarProcessContaHospitalar(final FatContasHospitalares newCtaHosp, final FatContasHospitalares oldCtaHosp, final TipoOperacaoEnum tipoOperacao, final Date dataFimVinculoServidor) throws BaseException{
		this.executarEnforceContaHospitalar(newCtaHosp, oldCtaHosp, tipoOperacao, dataFimVinculoServidor);
	}
	
	//ORADB FATT_CTH_BRU
	public void executarAntesDeAtualizarContaHospitalar(final FatContasHospitalares newCtaHosp, final FatContasHospitalares oldCtaHosp, final Boolean alterarEspecialidade, final Date dataFimVinculoServidor) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IAghuFacade aghuFacade = this.getAghuFacade();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		
		/*VERIFICA SE NRO AIH PREENCHIDO E SSM SOLICITADO NAO PREENCHIDO*/
		this.getFatkCthRN().rnCthpVerAihSolic(newCtaHosp.getAih() != null ? newCtaHosp.getAih().getNroAih() : null , newCtaHosp.getProcedimentoHospitalarInterno() != null ? newCtaHosp.getProcedimentoHospitalarInterno().getSeq() : null ); 

		/* VERIFICA SE O CONVENIO E PLANO SAO VALIDOS PARA ESSE PROCEDIMENTO*/
		this.verificarFiltroConvPlano(newCtaHosp, null, null, 
			newCtaHosp.getConvenioSaude() != null ? newCtaHosp.getConvenioSaude().getCodigo() : null,
			newCtaHosp.getConvenioSaudePlano() != null ? newCtaHosp.getConvenioSaudePlano().getId().getSeq() : null,
			newCtaHosp.getProcedimentoHospitalarInterno() != null ? newCtaHosp.getProcedimentoHospitalarInterno().getSeq() : null,
			newCtaHosp.getProcedimentoHospitalarInternoRealizado() != null ? newCtaHosp.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
			null, null);
		
		newCtaHosp.setAlteradoEm(new Date());
		newCtaHosp.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
		newCtaHosp.setServidorManuseado(servidorLogado);
		
		if(newCtaHosp.getContaManuseada() == null){
			newCtaHosp.setContaManuseada(false);
		}
		
		if(alterarEspecialidade){
			if(CoreUtil.modificados(newCtaHosp.getDtAltaAdministrativa(), oldCtaHosp.getDtAltaAdministrativa())){
				newCtaHosp.setEspecialidade(this.getFatkCth4RN().rnCthcVerEspCta(newCtaHosp.getSeq(), newCtaHosp.getDtAltaAdministrativa()));
			}
		}

		final String parametroAutorizacaoManual = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_INTEGRACAO_SISTEMA_SMS_PARA_AUTORIZACAO_ITENS_DA_CONTA);
		
		/* FECHA A CONTA QUANDO PACIENTE RECEBER ALTA */
		if(newCtaHosp.getDtAltaAdministrativa() != null && oldCtaHosp.getDtAltaAdministrativa() == null){
			newCtaHosp.setIndSituacao(DominioSituacaoConta.F);
			//#7040 RN1
			if(DominioSimNao.N.toString().equalsIgnoreCase(parametroAutorizacaoManual)){
				newCtaHosp.setIndEnviadoSms(DominioSimNao.S.toString());
				newCtaHosp.setIndAutorizadoSms(DominioSimNao.S.toString());
			}
		}
		/* ABRE A CONTA QUANDO PACIENTE ESTORNAR ALTA */
		if(newCtaHosp.getDtAltaAdministrativa() == null && oldCtaHosp.getDtAltaAdministrativa() != null){
			newCtaHosp.setIndSituacao(DominioSituacaoConta.A);

			//#7040 RN4
			if(DominioSimNao.N.toString().equalsIgnoreCase(parametroAutorizacaoManual)){
				newCtaHosp.setIndEnviadoSms(DominioSimNao.N.toString());
				newCtaHosp.setIndAutorizadoSms(DominioSimNao.N.toString());
			}
		}
		/* ATUALIZA INDICADOR DE INFECCAO A CADA MUDANCA NA DT_ALTA*/
		if(CoreUtil.modificados(newCtaHosp.getDtAltaAdministrativa(), oldCtaHosp.getDtAltaAdministrativa()) || newCtaHosp.getDtAltaAdministrativa() != null){
			final List<AghAtendimentos> listaAtendimentos = aghuFacade.obterAtendimentosDeContasInternacaoPorContaHospitalar(newCtaHosp.getSeq());
			if(listaAtendimentos != null && listaAtendimentos.size() > 0 && this.getFatkCthRN().rnCthcVerInfeccao(listaAtendimentos.get(0).getSeq())){
				newCtaHosp.setIndInfeccao(true);
			}else{
				newCtaHosp.setIndInfeccao(false);
			}
		}
		/* VERIFICA MODIFICAÇÕES NO MOTIVO E/OU SITUAÇÃO */
		if((CoreUtil.modificados(newCtaHosp.getMotivoRejeicao(), oldCtaHosp.getMotivoRejeicao()))
			||(CoreUtil.modificados(newCtaHosp.getIndSituacao(), oldCtaHosp.getIndSituacao()))){
			logar("verifica motivo/situacao ");
			this.getFatkCth5RN().rnCthpVerRjcSit((oldCtaHosp.getMotivoRejeicao()!=null)?oldCtaHosp.getMotivoRejeicao().getSeq():null, (newCtaHosp.getMotivoRejeicao()!=null)?newCtaHosp.getMotivoRejeicao().getSeq():null, newCtaHosp.getIndSituacao());
		}
		
		/*ATUALIZA ESPECIALIDADE DA CONTA SE FOR CIRURGICA*/
		if(CoreUtil.igual(DominioSituacaoConta.E,newCtaHosp.getIndSituacao()) && !CoreUtil.igual(DominioSituacaoConta.E,oldCtaHosp.getIndSituacao())){
			final FatItemContaHospitalar itemContaHosp = fatItemContaHospitalarDAO.buscarItensContaHospitalarPorCtaHospEProcedHospInternoRealizado(newCtaHosp.getSeq(), newCtaHosp.getProcedimentoHospitalarInternoRealizado() != null ? newCtaHosp.getProcedimentoHospitalarInternoRealizado().getSeq() : null);
			if(itemContaHosp != null){
				final Short espSeq = (itemContaHosp.getProcEspPorCirurgias() != null ? (itemContaHosp.getProcEspPorCirurgias().getMbcEspecialidadeProcCirgs() != null ? itemContaHosp.getProcEspPorCirurgias().getMbcEspecialidadeProcCirgs().getId().getEspSeq() : null) : null);
				if(espSeq != null){//IMPORTANTE: IF diferente do AGH, conforme definicao do analista, juntamente com a CGTI.
					final AghEspecialidades esp = aghuFacade.obterAghEspecialidadesPorChavePrimaria(espSeq);
					newCtaHosp.setEspecialidade(esp);
				}
			}
		}
		/*ATUALIZA PARA NEFRO SE FOR TRANSPLANTE -- ETB 210706*/
		final String parametroNefro = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CARACT_FATURA_ESPECIALIDADE_NEFRO);
		final Short parametroEspecialidadeNefro = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_SEQ_ESPECIALIDADE_NEFROLOGIA);
		if(this.fatcVerCaractPhiQrInt(newCtaHosp.getConvenioSaudePlano() != null ? newCtaHosp.getConvenioSaudePlano().getId().getCnvCodigo() : null,
				newCtaHosp.getConvenioSaudePlano() != null ? newCtaHosp.getConvenioSaudePlano().getId().getSeq() : null,
				newCtaHosp.getProcedimentoHospitalarInternoRealizado() != null ? newCtaHosp.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
						parametroNefro)){
			
			final AghEspecialidades esp = aghuFacade.obterAghEspecialidadesPorChavePrimaria(parametroEspecialidadeNefro);
			newCtaHosp.setEspecialidade(esp);
		}
		
	}
	
	//ORADB FATC_VER_CARACT_PHI_QR_INT
	public List<Integer> fatcVerCaractListaPhiQrInt(final Short cnvCodigo, final Byte cspSeq, final String caracteristica, final Integer... phiSeq) throws BaseException {
		
		List<Integer> result = null;
		VFatAssociacaoProcedimentoDAO daoAssocProced = null;
		
		daoAssocProced = this.getVFatAssociacaoProcedimentoDAO();
		result = daoAssocProced.listarPorConvenioConvenioSaudePlanoEListaProcedHospInt(cnvCodigo, cspSeq, caracteristica, phiSeq);
		
		return result;
	}

	//ORADB FATC_VER_CARACT_PHI_QR_INT
	public Boolean fatcVerCaractPhiQrInt(final Short cnvCodigo, final Byte cspSeq, final Integer phiSeq, final String caracteristica) throws BaseException{
		Boolean resultado = null;
		final VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		final FatCaractItemProcHospDAO fatCaractItemProcHospDAO = getFatCaractItemProcHospDAO();
		final FatTipoCaractItensDAO fatTipoCaractItensDAO = getFatTipoCaractItensDAO();
		
		final VFatAssociacaoProcedimento vFatAssociacaoProcedimento = vFatAssociacaoProcedimentoDAO.buscarVFatAssociacaoProcedimentoPorConvenioConvenioSaudePlanoEProcedHospInt(cnvCodigo, cspSeq, phiSeq);
		if(vFatAssociacaoProcedimento == null){
			resultado = false;
		}else{
			final List<FatTipoCaractItens> listaFatTipoCaractItens = fatTipoCaractItensDAO.listarTipoCaractItensPorCaracteristica(caracteristica);
			
			Integer seqTct = null;
			FatCaractItemProcHosp caractItemProcHosp = null;
			if(listaFatTipoCaractItens != null && !listaFatTipoCaractItens.isEmpty()){
				seqTct = listaFatTipoCaractItens.get(0).getSeq();
			
				final FatCaractItemProcHospId caractItemProcHospId = new FatCaractItemProcHospId();
				caractItemProcHospId.setIphPhoSeq(vFatAssociacaoProcedimento.getId().getIphPhoSeq());
				caractItemProcHospId.setIphSeq(vFatAssociacaoProcedimento.getId().getIphSeq());
				caractItemProcHospId.setTctSeq(seqTct);
				
				caractItemProcHosp = fatCaractItemProcHospDAO.obterPorChavePrimaria(caractItemProcHospId);
			}
			if(caractItemProcHosp == null){
				resultado = false;
			}else{
				resultado = true;
			}
		}
		return resultado;
	}
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUS(final Object objPesquisa, final Integer phiSeq, final Integer cthSeq, final Boolean isProcHospSolictado) throws BaseException {
		final VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		return vFatAssociacaoProcedimentoDAO.listarAssociacaoProcedimentoSUS(objPesquisa, phiSeq, cthSeq, isProcHospSolictado);
	}

	public Long listarAssociacaoProcedimentoSUSCount(final Object objPesquisa, final Integer phiSeq, final Integer cthSeq, final Boolean isProcHospSolictado) throws BaseException {
		final VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		return vFatAssociacaoProcedimentoDAO.listarAssociacaoProcedimentoSUSCount(objPesquisa, phiSeq, cthSeq, isProcHospSolictado);
	}
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSPorPHI(final Object objPesquisa, final Integer phiSeq, final Integer cthSeq, final Boolean isProcHospSolictado) throws BaseException {
		final VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		return vFatAssociacaoProcedimentoDAO.listarAssociacaoProcedimentoSUSPorPHI(objPesquisa, phiSeq, cthSeq, isProcHospSolictado);
	}
	public Long listarAssociacaoProcedimentoSUSPorPHICount(final Object objPesquisa, final Integer phiSeq, final Integer cthSeq, final Boolean isProcHospSolictado) throws BaseException {
		final VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		return vFatAssociacaoProcedimentoDAO.listarAssociacaoProcedimentoSUSPorPHICount(objPesquisa, phiSeq, cthSeq, isProcHospSolictado);
	}
	private FatkCthRN getFatkCthRN(){
		return fatkCthRN;
	}
	private FatkCth2RN getFatkCth2RN(){
		return fatkCth2RN;
	}
	private FatkCth4RN getFatkCth4RN(){
		return fatkCth4RN;
	}
	private FatkCth5RN getFatkCth5RN(){
		return fatkCth5RN;
	}
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}