package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTipoAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.VMbcProcEspDAO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProcEspPorCirurgiasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class RetornoCirurgiaEmLoteON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RetornoCirurgiaEmLoteON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private VMbcProcEspDAO vMbcProcEspDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcTipoAnestesiasDAO mbcTipoAnestesiasDAO;


	@EJB
	private MbcAnestesiaCirurgiasRN mbcAnestesiaCirurgiasRN;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IInternacaoFacade iInternacaoFacade;

	@EJB
	private IFaturamentoApoioFacade iFaturamentoApoioFacade;

	@EJB
	private PopulaCodigoSsmRN populaCodigoSsmRN;

	@EJB
	private MbcCirurgiasVerificacoes2RN mbcCirurgiasVerificacoes2RN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN;

	@EJB
	private VerificaEspecialidadeNotaRN verificaEspecialidadeNotaRN;

	@EJB
	private MbcCirurgiasVerificacoesRN mbcCirurgiasVerificacoesRN;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;
	
	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3140565224607557881L;

	private enum RetornoCirurgiaEmLoteONExceptonCode implements BusinessExceptionCode{
		ACAO_CANCELAR_NAO_PERMITIDA, MBC_00516, MBC_00537, MBC_00478, MBC_00340, MBC_00477, MBC_00506, MBC_00507, SEM_PARAMETRO_TIPO_ANESTESIA
	}

	/*
	 * Botao pesquisar do documento de analise 24949
	 */
	public List<MbcCirurgias> pesquisarRetornoCirurgiasEmLote(
			Short unidadeFuncionalSeq, Date dataCirurgia, Short sala,
			Integer prontuario) {

		return this.getMbcCirurgiasDAO().pesquisarRetornoCirurgiasEmLote(unidadeFuncionalSeq, dataCirurgia, sala, prontuario);
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}

	public void mudarSituacao(DominioSituacaoCirurgia situacao) throws ApplicationBusinessException {
		if(situacao == DominioSituacaoCirurgia.CANC){
			throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.ACAO_CANCELAR_NAO_PERMITIDA);
		}		
	}
	
	public void atualizarComPreVerificacoes(MbcCirurgias cirurgia) throws ApplicationBusinessException, BaseException{
		this.getMbcCirurgiasVerificacoesRN().verificarRestricoesMbcCirurgias(cirurgia);
		/*RN1*/this.validarIndDigtNotaSalaEMtcSeq(cirurgia);
		this.validarDthrDigitNotaSalaEDdthrUltAtlzNotaSala(cirurgia);
		this.verificaConvenioCirurgia(cirurgia);
		/*RN8*/this.verificarPacienteProntuarioEIndDigitNotaSala(cirurgia, /*RN7*/this.verificaExisteExamePrincipal(cirurgia));
		/*RN9*/this.verificaRN9(cirurgia);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, null, servidorLogado.getDtFimVinculo());
	}
	
	public void validarPosUpdate(MbcCirurgias cirurgia) throws BaseException{
		/*RN1*/this.verificarDataNotaSala(cirurgia);
		/*RN2*/this.verificaUnicoResponsavelCirurgiaProfessorOrContratado(cirurgia);
		/*RN3*/this.verificarProfissionalExecucaoCirugia(cirurgia);
		/*RN4*/this.verificarCaracteristicaUnidadeFuncionalEDigitaNotaSala(cirurgia);
		/*RN5*/this.verificaDigitoNotaSalaENaoEncontraNaConsulta(cirurgia);
	}

	public void validarPosComitForm(MbcCirurgias cirurgia) throws BaseException{
		this.verificarEspecialidadeNota(cirurgia);
	}

	private void verificarEspecialidadeNota(MbcCirurgias cirurgia) throws BaseException {
		this.getVerificaEspecialidadeNotaRN().verificarEspecialidadeNota(cirurgia.getDigitaNotaSala(), cirurgia.getSeq(), cirurgia.getSituacao(), cirurgia.getEspecialidade().getSeq());
	}

	private void verificaDigitoNotaSalaENaoEncontraNaConsulta(MbcCirurgias cirurgia) throws BaseException {
		if(cirurgia.getDigitaNotaSala()){
			List<MbcTipoAnestesias> listaTipoAnestesia = this.getMbcTipoAnestesiasDAO().obterPorCirurgia(cirurgia.getSeq());			
			if(listaTipoAnestesia.isEmpty()){
				MbcAnestesiaCirurgias mbcAnestesiaCirurgias = new MbcAnestesiaCirurgias();
				AghParametros tipoAnestesia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_TIPO_SEM_ANESTESIA);
				
				if (tipoAnestesia.getVlrNumerico() != null) {
					
					MbcAnestesiaCirurgiasId id = new MbcAnestesiaCirurgiasId();
					id.setCrgSeq(cirurgia.getSeq());
					id.setTanSeq(tipoAnestesia.getVlrNumerico().shortValue());
					mbcAnestesiaCirurgias.setId(id);
					
					mbcAnestesiaCirurgias.setMbcTipoAnestesias(getMbcTipoAnestesiasDAO().obterPorChavePrimaria(tipoAnestesia.getVlrNumerico().shortValue()));
					mbcAnestesiaCirurgias.setCirurgia(cirurgia);
					getMbcAnestesiaCirurgiasRN().inserirMbcAnestesiaCirurgias(mbcAnestesiaCirurgias);					
				} else {					
					throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.SEM_PARAMETRO_TIPO_ANESTESIA);					
				}			
			}
		}
	}

	private void verificarCaracteristicaUnidadeFuncionalEDigitaNotaSala(MbcCirurgias cirurgia) throws ApplicationBusinessException {
	
		for(AghCaractUnidFuncionais caracteristica : iAghuFacade.listarCaractUnidFuncionaisEUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq().toString(), ConstanteAghCaractUnidFuncionais.BLOCO)){
			if(caracteristica.getId().getCaracteristica() == ConstanteAghCaractUnidFuncionais.BLOCO){
				if(cirurgia.getDigitaNotaSala()){
					this.getMbcCirurgiasVerificacoes2RN().verificarProfissionalEnfermagem(cirurgia.getSeq());
					this.getMbcCirurgiasVerificacoesRN().verificarNecessidadeAnestesista(cirurgia.getSeq());
				}

			}
		}
	}

	private void verificarProfissionalExecucaoCirugia(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if(cirurgia.getDigitaNotaSala()){
			getMbcCirurgiasVerificacoes2RN().verificarProfissionalExecucaoCirugia(cirurgia);
		}
	}

	private void verificaUnicoResponsavelCirurgiaProfessorOrContratado(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		this.getMbcProfCirurgiasRN().verificarResponsavel(cirurgia.getSeq());
	}

	private void verificarDataNotaSala(MbcCirurgias cirurgia)throws BaseException {
		if(cirurgia.getDataDigitacaoNotaSala() != null){
			this.getMbcProcEspPorCirurgiasRN().verificarProcedimentoEspecialidade(cirurgia.getSeq(), DominioIndRespProc.NOTA);
		} else {
			this.getMbcProcEspPorCirurgiasRN().verificarProcedimentoEspecialidade(cirurgia.getSeq(), DominioIndRespProc.AGND);
		}
	}

	private void verificaRN9(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		FatConvenioSaudePlano fatConvenioSaudePlano = this.getFaturamentoFacade().obterPorCspCnvCodigoECnvCodigo(cirurgia.getConvenioSaude().getCodigo(), cirurgia.getConvenioSaudePlano().getId().getSeq());
		if(fatConvenioSaudePlano.getIndTipoPlano() != null && fatConvenioSaudePlano.getIndTipoPlano() == DominioTipoPlano.A){
			if(cirurgia.getOrigemPacienteCirurgia() != DominioOrigemPacienteCirurgia.A){
				throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.MBC_00506);
			}
		}

		if(fatConvenioSaudePlano.getIndTipoPlano() != null && fatConvenioSaudePlano.getIndTipoPlano() == DominioTipoPlano.I){
			if(cirurgia.getOrigemPacienteCirurgia() != DominioOrigemPacienteCirurgia.I){
				throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.MBC_00507);
			}
		}
	}

	private void verificarPacienteProntuarioEIndDigitNotaSala(MbcCirurgias cirurgia, MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) throws ApplicationBusinessException {

		if(cirurgia.getPaciente().getProntuario() == null){
			if((!mbcProcedimentoCirurgicos.getIndNsSemPront() || cirurgia.getOrigemPacienteCirurgia() == DominioOrigemPacienteCirurgia.I)){
				if(cirurgia.getDigitaNotaSala()){
					throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.MBC_00477);
				}
			}
		}
	}

	private MbcProcedimentoCirurgicos verificaExisteExamePrincipal(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos = this.getMbcProcedimentoCirurgicoDAO().obterPorCirurgiaIndPrincipalSituacaoIndRespProcJoinComMbcProcEspPorCirurgias(cirurgia.getSeq(), Boolean.TRUE, DominioSituacao.A, DominioIndRespProc.AGND);
		if(mbcProcedimentoCirurgicos == null || mbcProcedimentoCirurgicos.getSeq() == null){
			throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.MBC_00340);
		}

		return mbcProcedimentoCirurgicos;
	}


	private void verificaConvenioCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException {

		if(cirurgia.getAtendimento() != null){
			AinInternacao internacao = this.getInternacaoFacade().obterAinInternacaoPorOrigemEAtdSeq(DominioOrigemAtendimento.I, cirurgia.getAtendimento().getSeq());
			if(internacao != null && !internacao.getConvenioSaude().equals(cirurgia.getConvenioSaude())){
				throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.MBC_00478);
			}
		}	
	}

	private void validarDthrDigitNotaSalaEDdthrUltAtlzNotaSala(MbcCirurgias cirurgia) throws ApplicationBusinessException, BaseException {
		MbcCirurgias cirurgiaOriginal = getMbcCirurgiasRN().getMbcCirurgiasDAO().obterOriginal(cirurgia);
		if(cirurgia != null){
			cirurgia.setDigitaNotaSala(cirurgia.getDigitaNotaSala() != null ? cirurgia.getDigitaNotaSala() : Boolean.FALSE);
			if(cirurgia.getDigitaNotaSala()){
				if(cirurgia.getDataDigitacaoNotaSala() == null){
					cirurgia.setDataDigitacaoNotaSala(new Date());
					cirurgia.setDataUltimaAtualizacaoNotaSala(new Date());
				} else {
					/*RN3*/
					if(!cirurgia.getDigitaNotaSala()){
						cirurgia.setDataDigitacaoNotaSala(null);
						cirurgia.setDataUltimaAtualizacaoNotaSala(null);
					}
				}

				// Chamada da PROCEDURE MBCK_CRG_RN.RN_CRGP_VER_DIGT_NOT
				/*RN4*/this.getMbcCirurgiasVerificacoes2RN().verificarDigitoNotaSala(cirurgia);
			} 
		}

		//Se foi modificado o valor de ind_digt_nota_sala então
		if(!cirurgiaOriginal.getDigitaNotaSala().equals(cirurgia.getDigitaNotaSala())){
			List<MbcProcEspPorCirurgias> listaProcEspCir = getMbcProcEspPorCirurgiasDAO().pesquisarProcEspCirurgicoPrincipalAgendamentoPorCrgSeq(cirurgia.getSeq(), DominioSituacao.A, null);
			for(MbcProcEspPorCirurgias procEspCir : listaProcEspCir){

				MbcProcEspPorCirurgias novoMbcProcEspPorCirurgias = new MbcProcEspPorCirurgias();
				try {
					BeanUtils.copyProperties(novoMbcProcEspPorCirurgias, procEspCir);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					this.logError(e.getMessage());
				}
				novoMbcProcEspPorCirurgias.getId().setIndRespProc(DominioIndRespProc.NOTA);
				novoMbcProcEspPorCirurgias.getServidor().getId().setMatricula(null);
				novoMbcProcEspPorCirurgias.getServidor().getId().setVinCodigo(null);
				novoMbcProcEspPorCirurgias.setCriadoEm(null);

				this.getMbcProcEspPorCirurgiasRN().inserirMbcProcEspPorCirurgias(novoMbcProcEspPorCirurgias);
				this.getMbcProcEspPorCirurgiasDAO().flush();

			}

			//2) se NAO encontrar registro em (ALTERAR o documento de analise):
			List<MbcProfCirurgias> listaMbcProfCirurgias = this.getMbcProfCirurgiasDAO().pesquisarResponsavelRealizacaoCirurgia(cirurgia.getSeq());
			if(listaMbcProfCirurgias.size() == 0){

				MbcProfCirurgias mbcProfCirurgias = getMbcProfCirurgiasDAO().retornaResponsavelCirurgia(cirurgia);
				if (mbcProfCirurgias != null) {
					mbcProfCirurgias.setIndRealizou(Boolean.TRUE);
					getMbcProfCirurgiasRN().atualizarMbcProfCirurgias(mbcProfCirurgias, false);
					RapServidores servidor = mbcProfCirurgias.getServidorPuc();
					// Seta o centro de custo na cirurgia do profissional responsável
					cirurgia.setCentroCustos(servidor.getCentroCustoLotacao());
				}				
				
				
			}
		}
	}

	private void validarIndDigtNotaSalaEMtcSeq(MbcCirurgias cirurgias) throws ApplicationBusinessException {
		Boolean digitouNotaSala = cirurgias.getDigitaNotaSala() != null ? cirurgias.getDigitaNotaSala() : Boolean.FALSE;
		if(digitouNotaSala && cirurgias.getMotivoCancelamento() != null){
			throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.MBC_00516);
		}
	}

	public void obterDataUltimoTransplante(MbcCirurgias cirurgias) throws ApplicationBusinessException {
		Date date = this.getFaturamentoFacade().obterDataUltimoTransplante(cirurgias.getPaciente().getCodigo());
		if(date != null){
			throw new ApplicationBusinessException(RetornoCirurgiaEmLoteONExceptonCode.MBC_00537, cirurgias.getPaciente().getNome());
		}
	}

	protected MbcCirurgiasVerificacoes2RN getMbcCirurgiasVerificacoes2RN() {
		return mbcCirurgiasVerificacoes2RN;
	}

	protected MbcCirurgiasRN getMbcCirurgiasRN(){
		return mbcCirurgiasRN;
	} 

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO(){
		return mbcProcEspPorCirurgiasDAO;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO(){
		return mbcProfCirurgiasDAO;
	}

	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO(){
		return mbcProcedimentoCirurgicoDAO;
	}

	protected MbcProcEspPorCirurgiasRN getMbcProcEspPorCirurgiasRN(){
		return mbcProcEspPorCirurgiasRN;
	}

	protected MbcProfCirurgiasRN getMbcProfCirurgiasRN(){
		return mbcProfCirurgiasRN;
	}

	protected MbcCirurgiasVerificacoesRN getMbcCirurgiasVerificacoesRN(){
		return mbcCirurgiasVerificacoesRN;
	}

	protected MbcTipoAnestesiasDAO getMbcTipoAnestesiasDAO(){
		return mbcTipoAnestesiasDAO;
	}
	
	protected MbcAnestesiaCirurgiasRN getMbcAnestesiaCirurgiasRN(){
		return mbcAnestesiaCirurgiasRN;
	}

	protected VerificaEspecialidadeNotaRN getVerificaEspecialidadeNotaRN(){
		return verificaEspecialidadeNotaRN;
	}	

	public FatConvenioSaudePlano obterPlanoPorId(Byte planoId, Short convenioId) {
		return this.getFaturamentoApoioFacade().obterPlanoPorId(planoId, convenioId);
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(
			String objPesquisa) {
		return this.getFaturamentoApoioFacade().pesquisarConvenioSaudePlanos(objPesquisa);
	}

	public Long pesquisarCountConvenioSaudePlanos(String strPesquisa) {
		return this.getFaturamentoApoioFacade().pesquisarCountConvenioSaudePlanos(strPesquisa);
	}

	public List<MbcProcEspPorCirurgiasVO> buscarListaMbcProcEspPorCirurgiasVO(MbcCirurgias cirurgia) throws BaseException {
		List<MbcProcEspPorCirurgiasVO> listaMbcProcEspPorCirurgiasVO = new ArrayList<MbcProcEspPorCirurgiasVO>();
		List<MbcProcEspPorCirurgias> listaProc = this.getMbcProcEspPorCirurgiasDAO().pesquisarPorMbcCirurgiaSituacaoIndResponsavel(cirurgia, DominioSituacao.A, cirurgia.getDigitaNotaSala() ? DominioIndRespProc.NOTA : DominioIndRespProc.AGND);
		for(MbcProcEspPorCirurgias pojo : listaProc){
			MbcProcEspPorCirurgiasVO vo = new MbcProcEspPorCirurgiasVO();
			vo.setIndPrincipal(pojo.getIndPrincipal());
			vo.setCodigo(pojo.getId().getEprPciSeq());

			VMbcProcEsp vMbcProcEsp = this.getVMbcProcEspDAO().obterProcedimentosAgendadosPorId(pojo.getId().getEprEspSeq(), pojo.getId().getEprPciSeq(), null);
			if(vMbcProcEsp != null){
				vo.setDescricao(vMbcProcEsp.getDescricao());
				vo.setEspecialidade(vMbcProcEsp.getSigla());
			}	
			vo.setProcedimentoCirurgicoSeq(pojo.getProcedimentoCirurgico().getSeq());

			listaMbcProcEspPorCirurgiasVO.add(vo);
		}

		return listaMbcProcEspPorCirurgiasVO;
	}

	public PopulaCodigoSsmRN getPopulaCodigoSsmRN(){
		return populaCodigoSsmRN;
	}

	public VMbcProcEspDAO getVMbcProcEspDAO(){
		return vMbcProcEspDAO;
	}

	protected IInternacaoFacade getInternacaoFacade() { 
		return  iInternacaoFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() { 
		return  iFaturamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() { 
		return  iFaturamentoApoioFacade;
	}

	public void gravar(MbcCirurgias cirurgia, String nomeComputador) throws ApplicationBusinessException, BaseException {
		if(cirurgia != null){
			this.atualizarComPreVerificacoes(cirurgia);
			this.validarPosUpdate(cirurgia);
			this.validarPosComitForm(cirurgia);
		}	

	}
}
