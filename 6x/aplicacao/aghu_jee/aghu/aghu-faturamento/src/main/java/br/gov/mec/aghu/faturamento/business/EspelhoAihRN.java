package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Triggers de:<br/>
 * ORADB: <code>FAT_ESPELHOS_AIH</code>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class EspelhoAihRN extends AbstractAGHUCrudRn<FatEspelhoAih> {

	private static final long serialVersionUID = 1044847923618564298L;
	private static final Log LOG = LogFactory.getLog(EspelhoAihRN.class);
	
	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum EspelhoAihRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_FAT_ESPELHO_AIH;
	}

	protected int getAghuParamMaxContAih() throws ApplicationBusinessException {

		int result = 0;
		AghParametros maxContAihParam = null;

		maxContAihParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_MAX_AIH_CONTINUACAO);
		
		if(maxContAihParam.getVlrNumerico() == null){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.PARAMETRO_INVALIDO, AghuParametrosEnum.P_MAX_ATO_MEDICO_AIH);
		}
		
		result = maxContAihParam.getVlrNumerico().intValue();

		return result;
	}

	protected void ajustarDadosAlteracaoEntidade(final FatEspelhoAih entidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		entidade.setAlteradoEm(this.getDataCriacao());
		entidade.setAlteradoPor(servidorLogado.getUsuario());
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_EAI.PROCESS_EAI_ROWS</code> ORADB:
	 * <code>FATP_ENFORCE_EAI_RULES</code> ORADB:
	 * <code>FATK_EAI_RN.RN_EAIP_VER_QUANT</code>
	 * </p>
	 * 
	 * @param op
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @see FaturamentoExceptionCode#FAT_00404
	 */
	protected void verificarQtdMaxAIHPorEspelho(final DominioOperacoesJournal op, final FatEspelhoAih entidade) throws ApplicationBusinessException {
		
		int maxContAih = 0;
		Long qtdFatEspAih = 0l;

		maxContAih = this.getAghuParamMaxContAih();
		qtdFatEspAih = fatEspelhoAihDAO.countPorCthSeqpDataPreviaNotNull(entidade.getId().getCthSeq(), entidade.getId().getSeqp());
		qtdFatEspAih = (qtdFatEspAih == null ? 0 : qtdFatEspAih); 
		
		if (DominioOperacoesJournal.UPD.equals(op)) {
			qtdFatEspAih--;
		}
		
		if (qtdFatEspAih > maxContAih) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00404);
		}
	}

	/**
	 * <p>
	 * ORADB: <code>FATT_EAI_BRI</code> ORADB: <code>FATT_EAI_ASI</code>
	 * </p>
	 */
	@Override
	public boolean briPreInsercaoRow(final FatEspelhoAih entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;

		//BRI
		this.ajustarDadosAlteracaoEntidade(entidade);
		entidade.setCriadoEm(entidade.getAlteradoEm());
		entidade.setCriadoPor(entidade.getAlteradoPor());
		//ASI
		this.verificarQtdMaxAIHPorEspelho(DominioOperacoesJournal.INS, entidade);
		result = true;

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATT_EAI_BRU</code> ORADB: <code>FATT_EAI_ASU</code>
	 * </p>
	 */
	@Override
	public boolean bruPreAtualizacaoRow(final FatEspelhoAih original, final FatEspelhoAih modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;

		//BRU
		this.ajustarDadosAlteracaoEntidade(modificada);
		//ASU
		this.verificarQtdMaxAIHPorEspelho(DominioOperacoesJournal.UPD, modificada);
		result = true;

		return result;
	}
	

	public void atualizarFatEspelhoAih(final FatEspelhoAih fatEspelhoAih) throws BaseException {
		if (bruPreAtualizacaoRow(null, fatEspelhoAih, null, null)) {
			FatEspelhoAih  entidade = fatEspelhoAihDAO.obterPorChavePrimaria(fatEspelhoAih.getId());
			atualizarEntidade(entidade, fatEspelhoAih);
		} else {
			throw new ApplicationBusinessException(EspelhoAihRNExceptionCode.ERRO_PERSISTENCIA_FAT_ESPELHO_AIH);
		}
	}
	
	private void atualizarEntidade(FatEspelhoAih entidade, FatEspelhoAih fatEspelhoAih) throws BaseException {

		entidade.setContaHospitalar(fatEspelhoAih.getContaHospitalar());
		entidade.setNroDiasMesInicialUti(fatEspelhoAih.getNroDiasMesInicialUti());
		entidade.setNroDiasMesAnteriorUti(fatEspelhoAih.getNroDiasMesAnteriorUti());
		entidade.setNroDiasMesAltaUti(fatEspelhoAih.getNroDiasMesAltaUti());
		entidade.setNroDiariasAcompanhante(fatEspelhoAih.getNroDiariasAcompanhante());
		entidade.setTahSeq(fatEspelhoAih.getTahSeq());
		entidade.setAlteradoPor(fatEspelhoAih.getAlteradoPor());
		entidade.setCriadoPor(fatEspelhoAih.getCriadoPor());
		entidade.setAlteradoEm(fatEspelhoAih.getAlteradoEm());
		entidade.setCriadoEm(fatEspelhoAih.getCriadoEm());
		entidade.setAihDthrEmissao(fatEspelhoAih.getAihDthrEmissao());
		entidade.setCidPrimario(fatEspelhoAih.getCidPrimario());
		entidade.setCidSecundario(fatEspelhoAih.getCidSecundario());
		entidade.setCodIbgeCidadePac(fatEspelhoAih.getCodIbgeCidadePac());
		entidade.setCpfMedicoAuditor(fatEspelhoAih.getCpfMedicoAuditor());
		entidade.setCpfMedicoSolicRespons(fatEspelhoAih.getCpfMedicoSolicRespons());
		entidade.setDataInternacao(fatEspelhoAih.getDataInternacao());
		entidade.setDataPrevia(fatEspelhoAih.getDataPrevia());
		entidade.setDataSaida(fatEspelhoAih.getDataSaida());
		entidade.setDciCodigoDcih(fatEspelhoAih.getDciCodigoDcih());
		entidade.setDciCpeAno(fatEspelhoAih.getDciCpeAno());
		entidade.setDciCpeDtHrInicio(fatEspelhoAih.getDciCpeDtHrInicio());
		entidade.setDciCpeMes(fatEspelhoAih.getDciCpeMes());
		entidade.setDciCpeModulo(fatEspelhoAih.getDciCpeModulo());
		entidade.setEndCepPac(fatEspelhoAih.getEndCepPac());
		entidade.setEndCidadePac(fatEspelhoAih.getEndCidadePac());
		entidade.setEndCmplLogradouroPac(fatEspelhoAih.getEndCmplLogradouroPac());
		entidade.setEndLogradouroPac(fatEspelhoAih.getEndLogradouroPac());
		entidade.setEndNroLogradouroPac(fatEspelhoAih.getEndNroLogradouroPac());
		entidade.setEndUfPac(fatEspelhoAih.getEndUfPac());
		entidade.setEnfermaria(fatEspelhoAih.getEnfermaria());
		entidade.setEspecialidadeAih(fatEspelhoAih.getEspecialidadeAih());
		entidade.setEspecialidadeDcih(fatEspelhoAih.getEspecialidadeDcih());
		entidade.setExclusaoCritica(fatEspelhoAih.getExclusaoCritica());
		entidade.setGrauInstrucaoPac(fatEspelhoAih.getGrauInstrucaoPac());
		entidade.setIndConsistente(fatEspelhoAih.getIndConsistente());
		entidade.setIndDocPac(fatEspelhoAih.getIndDocPac());
		entidade.setInfeccaoHospitalar(fatEspelhoAih.getInfeccaoHospitalar());
		entidade.setIphCodSusRealiz(fatEspelhoAih.getIphCodSusRealiz());
		entidade.setIphCodSusSolic(fatEspelhoAih.getIphCodSusSolic());
		entidade.setIphPhoSeqRealiz(fatEspelhoAih.getIphPhoSeqRealiz());
		entidade.setIphPhoSeqSolic(fatEspelhoAih.getIphPhoSeqSolic());
		entidade.setIphSeqRealiz(fatEspelhoAih.getIphSeqRealiz());
		entidade.setIphSeqSolic(fatEspelhoAih.getIphSeqSolic());
		entidade.setLeito(fatEspelhoAih.getLeito());
		entidade.setMotivoCobranca(fatEspelhoAih.getMotivoCobranca());
		entidade.setNacionalidadePac(fatEspelhoAih.getNacionalidadePac());
		entidade.setNascidosMortos(fatEspelhoAih.getNascidosMortos());
		entidade.setNascidosVivos(fatEspelhoAih.getNascidosVivos());
		entidade.setNomeResponsavelPac(fatEspelhoAih.getNomeResponsavelPac());
		entidade.setNumeroAih(fatEspelhoAih.getNumeroAih());
		entidade.setNumeroAihAnterior(fatEspelhoAih.getNumeroAihAnterior());
		entidade.setNumeroAihPosterior(fatEspelhoAih.getNumeroAihPosterior());
		entidade.setPacCpf(fatEspelhoAih.getPacCpf());
		entidade.setPacDtNascimento(fatEspelhoAih.getPacDtNascimento());
		entidade.setPacNome(fatEspelhoAih.getPacNome());
		entidade.setPacRG(fatEspelhoAih.getPacRG());
		entidade.setPacNroCartaoSaude(fatEspelhoAih.getPacNroCartaoSaude());
		entidade.setPacProntuario(fatEspelhoAih.getPacProntuario());
		entidade.setPacSexo(fatEspelhoAih.getPacSexo());
		entidade.setSaidasAlta(fatEspelhoAih.getSaidasAlta());
		entidade.setSaidasObito(fatEspelhoAih.getSaidasObito());
		entidade.setSaidasTransferencia(fatEspelhoAih.getSaidasTransferencia());
		entidade.setTciCodSus(fatEspelhoAih.getTciCodSus());
		entidade.setValorAnestRealiz(fatEspelhoAih.getValorAnestRealiz());
		entidade.setValorProcedRealiz(fatEspelhoAih.getValorProcedRealiz());
		entidade.setValorSadtRealiz(fatEspelhoAih.getValorSadtRealiz());
		entidade.setValorShRealiz(fatEspelhoAih.getValorShRealiz());
		entidade.setValorSpRealiz(fatEspelhoAih.getValorSpRealiz());
		entidade.setNroSeqaih5(fatEspelhoAih.getNroSeqaih5());
		entidade.setNroSisprenatal(fatEspelhoAih.getNroSisprenatal());
		entidade.setPacNomeMae(fatEspelhoAih.getPacNomeMae());
		entidade.setPacCor(fatEspelhoAih.getPacCor());
		entidade.setEndTipCodigo(fatEspelhoAih.getEndTipCodigo());
		entidade.setEndBairroPac(fatEspelhoAih.getEndBairroPac());
		entidade.setDadosRn(fatEspelhoAih.getDadosRn());
		entidade.setIndBcoCapac(fatEspelhoAih.getIndBcoCapac());
		entidade.setConCodCentral(fatEspelhoAih.getConCodCentral());
		entidade.setDauSenha(fatEspelhoAih.getDauSenha());
		entidade.setFatAtoMedicoAihs(fatEspelhoAih.getFatAtoMedicoAihs());
		entidade.setFatCampoMedicoAuditAihs(fatEspelhoAih.getFatCampoMedicoAuditAihs());
		entidade.setFatItemProcedimentoHospitalarRealizado(fatEspelhoAih.getFatItemProcedimentoHospitalarRealizado());
		entidade.setFatItemProcedimentoHospitalarSolicitado(fatEspelhoAih.getFatItemProcedimentoHospitalarSolicitado());
		entidade.setCnsMedicoAuditor(fatEspelhoAih.getCnsMedicoAuditor());
		entidade.setAghClinicas(fatEspelhoAih.getAghClinicas());
		entidade.setAipPacientes(fatEspelhoAih.getAipPacientes());
		entidade.setFatCompetencia(fatEspelhoAih.getFatCompetencia());
		
	}

}
