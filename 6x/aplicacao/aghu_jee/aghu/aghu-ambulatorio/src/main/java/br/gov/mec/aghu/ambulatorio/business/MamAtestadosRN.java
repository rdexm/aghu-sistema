package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.internal.util.SerializationHelper;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.MarcacaoConsultaRN.MarcacaoConsultaRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.MamAtestadosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoAtestadoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

/**
 * Classe responsável por manter as regras de negócio da entidade MamAtestados.
 *
 */
@Stateless
public class MamAtestadosRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1625217259148400364L;

	private static final Log LOG = LogFactory.getLog(MamAtestadosRN.class);
	
	@Inject
	private MamAtestadosDAO mamAtestadosDAO;
	
	@Inject
	private MamTipoAtestadoDAO mamTipoAtestadosDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MamAtestadosRNExceptionCode implements BusinessExceptionCode {
		MAM_00648, MAM_00649, MENSAGEM_VALIDA_DATAS_ATESTADO, MSG_VALIDACAO_HORA, PREENCHA_CAMPOS_OBRIGATORIOS,
		MSG_VALOR_MINIMO_1, MSG_VALOR_MINIMO_MESES;
	}
	
	public void persistir(MamAtestados atestado, Boolean imprimeAtestado) throws ApplicationBusinessException  {
		if (atestado.getSeq() == null) {
			preInserir(atestado);
			mamAtestadosDAO.persistir(atestado);
		} else {
			MamAtestados atestadoOld = mamAtestadosDAO.obterOriginal(atestado.getSeq());
			DominioIndPendenteAmbulatorio atestadoIndPendente = atestado.getIndPendente();
			preAtualizar(atestado, atestadoOld);
			mamAtestadosDAO.atualizar(atestado);
			posAtualizar(atestado, atestadoIndPendente, imprimeAtestado);
		}
	}

	private void posAtualizar(MamAtestados atestado, DominioIndPendenteAmbulatorio atestadoIndPendente, Boolean imprimeAtestado) {
		if (atestado.getMamTipoAtestado() != null && 
			atestado.getMamTipoAtestado().getSeq() == Short.valueOf("15") && 
			DominioIndPendenteAmbulatorio.V.equals(atestadoIndPendente) && imprimeAtestado) {

			MamAtestados novoAtestado = mamAtestadosDAO.obterPorChavePrimaria(atestado.getSeq());
			
			MamAtestados novoAtestadoPersistir = new MamAtestados();
			novoAtestadoPersistir = (MamAtestados) SerializationHelper.clone(novoAtestado);
			novoAtestadoPersistir.setSeq(null);
			novoAtestadoPersistir.setIndPendente(DominioIndPendenteAmbulatorio.P);
			novoAtestadoPersistir.setMamAtestados(novoAtestado);
			mamAtestadosDAO.persistir(novoAtestadoPersistir);
		
			atestado.setIndPendente(DominioIndPendenteAmbulatorio.A);
			mamAtestadosDAO.persistir(novoAtestadoPersistir);
		}
	}
	
	/**
	 * ORADB TRIGGER MAMT_ATE_BRI
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(MamAtestados atestado) throws ApplicationBusinessException {
		if (DominioIndPendenteAmbulatorio.V.equals(atestado.getIndPendente())) {
			TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.INSERT;
			verificarRegistroValidado(tipoOperacao);
		}
		
		verificaCidAtivo(atestado.getAghCid() != null ? atestado.getAghCid().getSeq() : null);
		verificaTipoAtestado(atestado.getMamTipoAtestado().getSeq());
		
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		atestado.setServidor(servidorLogado);
		atestado.setServidorValida(servidorLogado);
		atestado.setDthrCriacao(new Date());
		
		if (atestado.getAipPacientes() == null) {
			if (atestado.getConsulta() != null) {
				AacConsultas consulta = ambulatorioFacade.obterConsulta(atestado.getConsulta().getNumero());
				if (consulta.getPaciente() != null && consulta.getPaciente().getCodigo() > 0) {
					atestado.setAipPacientes(consulta.getPaciente());
				}
			}
		}
	}
	
	/**
	 * ORADB TRIGGER MAMT_ATE_BRU
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizar(MamAtestados atestado, MamAtestados atestadoOld) throws ApplicationBusinessException {
		if (atestadoOld != null && DominioIndPendenteAmbulatorio.V.equals(atestadoOld.getIndPendente())) {

			if (CoreUtil.modificados(atestadoOld.getDataInicial(), atestado.getDataInicial())
				|| CoreUtil.modificados(atestadoOld.getDataFinal(), atestado.getDataFinal())
				|| CoreUtil.modificados(atestadoOld.getObservacao(), atestado.getObservacao())
				|| CoreUtil.modificados(atestadoOld.getMamTipoAtestado().getSeq(), atestado.getMamTipoAtestado().getSeq())
				|| CoreUtil.modificados(atestadoOld.getNomeAcompanhante(), atestado.getNomeAcompanhante())
				|| CoreUtil.modificados(atestadoOld.getAghCid(), atestado.getAghCid())
				|| CoreUtil.modificados(atestadoOld.getNroVias(), atestado.getNroVias())) {
					
				TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.UPDATE;
				verificarRegistroValidado(tipoOperacao);
			}
		}
	}
	
	/**
	 * ORADB TRIGGER MAMT_ATE_BRD
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	public void excluir(Long atsSeq) throws ApplicationBusinessException {
		MamAtestados atestado = mamAtestadosDAO.obterPorChavePrimaria(atsSeq);
		preRemover(atestado);
		mamAtestadosDAO.remover(atestado);
	}

	/**
	 * ORADB PROCEDURE MAMK_ATE_RN.RN_ATEP_VER_VALIDADO
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	private void verificarRegistroValidado(TipoOperacaoEnum tipoOperacao) throws ApplicationBusinessException {
		if (TipoOperacaoEnum.INSERT.equals(tipoOperacao)) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00650);
		} else if (TipoOperacaoEnum.UPDATE.equals(tipoOperacao)) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00651);
		} else if (TipoOperacaoEnum.DELETE.equals(tipoOperacao)){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00652);
		}
	}
	
	/**
	 * ORADB TRIGGER MAMT_ATE_BRD
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	private void preRemover(MamAtestados atestado) throws ApplicationBusinessException {
		if (DominioIndPendenteAmbulatorio.V.equals(atestado.getIndPendente())) {
			TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.DELETE;
			verificarRegistroValidado(tipoOperacao);
		}
	}
	
	/**
	 * ORADB PROCEDURE MAMK_ATE_RN.RN_ATEP_VER_CID_ATIV
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	private void verificaCidAtivo(Integer cidSeq) throws ApplicationBusinessException {
		AghCid cid = null;
		if (cidSeq != null) {
		
			cid = aghuFacade.obterAghCidsPorChavePrimaria(cidSeq);
			if (DominioSituacao.I.equals(cid.getSituacao())) {
				throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.MAM_00649);
			}
		}
	}
	
	/**
	 * ORADB PROCEDURE MAMK_ATE_RN.RN_ATEP_VER_TAS_ATIV
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	private void verificaTipoAtestado(Short tasSeq) throws ApplicationBusinessException {
		MamTipoAtestado tipoAtestado = mamTipoAtestadosDAO.obterPorChavePrimaria(tasSeq);
			
		if (tipoAtestado != null) {
			if (DominioSituacao.I.equals(tipoAtestado.getIndSituacao())){
				throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.MAM_00648);
			}
		}
	}
	
	public void validarPreAtualizarAtestado(MamAtestados atestado, MamAtestados atestadoOld) throws ApplicationBusinessException {
		preAtualizar(atestado, atestadoOld);
	}
	
	/**
	 * #46225 - Deleção de acompanhante, se IND_PENDENTE = V, atualiza para E
	 * se IND_PENDENTE = P ou IND_PENDENTE = R, deletar
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void excluirAtestadoFgts(MamAtestados atestado) throws ApplicationBusinessException {
		atestado = mamAtestadosDAO.obterPorChavePrimaria(atestado.getSeq());
		if (DominioIndPendenteAmbulatorio.V.equals(atestado.getIndPendente())) {
			atestado.setIndPendente(DominioIndPendenteAmbulatorio.E);
			this.persistir(atestado, false);
		} else if (DominioIndPendenteAmbulatorio.P.equals(atestado.getIndPendente()) || DominioIndPendenteAmbulatorio.R.equals(atestado.getIndPendente())){
			this.excluir(atestado.getSeq());
		}
	}
	
	/**
	 * #46206 - Deleção de acompanhante, se IND_PENDENTE = V, atualiza para E
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void excluirAtestadoAcompanhamento(MamAtestados atestado) throws ApplicationBusinessException {
		atestado = mamAtestadosDAO.obterPorChavePrimaria(atestado.getSeq());
		if (DominioIndPendenteAmbulatorio.V.equals(atestado.getIndPendente())) {
			atestado.setIndPendente(DominioIndPendenteAmbulatorio.E);
			this.persistir(atestado, false);
		} else {
			this.excluir(atestado.getSeq());
		}
	}	
	
	/**
	 * #46206 - Data inicial deve ser menor que a data final
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ApplicationBusinessException
	 */
	public void validarDatasAtestado(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		if (DateUtil.validaDataMaior(dataInicial, dataFinal)){
			throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.MENSAGEM_VALIDA_DATAS_ATESTADO);
		}
	}
	
	/**
	 * #46218 ORADB MAMP_CANC_ATE_ALTA
	 * @param apaAtdSeq
	 * @param apaSeq
	 * @param seqp
	 * @param seq
	 * @throws ApplicationBusinessException 
	 */
	public void cancelarAtestadosAltaSumario(Integer apaAtdSeq, Integer apaSeq, Short seqp, Long seq) throws ApplicationBusinessException {
		List<MamAtestados> atestadosList = mamAtestadosDAO.obterAtestadosCancelamentoAlta(apaAtdSeq, apaSeq, seqp, null, seq);
		
		for (MamAtestados mamAtestados : atestadosList) {
			if (DominioIndPendenteAmbulatorio.R.equals(mamAtestados.getIndPendente())) {
				this.cancelarAtestadoRascunhoPendente(mamAtestados, false);
				
			} else if (DominioIndPendenteAmbulatorio.P.equals(mamAtestados.getIndPendente())) {
				this.cancelarAtestadoRascunhoPendente(mamAtestados, true);
				
			} else if (DominioIndPendenteAmbulatorio.E.equals(mamAtestados.getIndPendente())) {
				cancelarAtestadoExcluido(mamAtestados);
			}
		}
	}
	
	private void cancelarAtestadoRascunhoPendente(MamAtestados atestado, boolean pendente) throws ApplicationBusinessException {
		Long ateSeq = atestado.getMamAtestados() != null ? atestado.getMamAtestados().getSeq() : null;
	
		this.excluir(atestado.getSeq());
		
		if (ateSeq != null) {
			List<MamAtestados> lista = mamAtestadosDAO.obterAtestadosPorAteSeq(ateSeq);
			
			if (lista != null && !lista.isEmpty()) {
				for (MamAtestados mamAtestados : lista) {
					mamAtestados.setDthrMvto(null);
					mamAtestados.setServidorMvto(null);
					if (pendente) {
						mamAtestados.setIndPendente(DominioIndPendenteAmbulatorio.V);
				}
					this.persistir(mamAtestados, false);
				}
			}
		}		
	}
	
	private void cancelarAtestadoExcluido(MamAtestados atestado) throws ApplicationBusinessException {
		atestado.setDthrMvto(null);
		atestado.setServidorMvto(null);
		atestado.setIndPendente(DominioIndPendenteAmbulatorio.V);
		this.persistir(atestado, false);
	}
	
	/**
	 * #11942
	 * @param atestado
	 */
	public void gravarAtestado(MamAtestados atestado) throws ApplicationBusinessException{
		if(atestado.getSeq() == null){
			preInserir(atestado);
			mamAtestadosDAO.persistir(atestado);
		}else{
			mamAtestadosDAO.atualizar(atestado);
		}
		mamAtestadosDAO.flush();
	}
	
	/**
	 * #11942
	 * @param atestado
	 */
	public void excluirAtestadoComparecimento(MamAtestados atestado){
		MamAtestados itemExcluir = mamAtestadosDAO.obterPorChavePrimaria(atestado.getSeq());
		mamAtestadosDAO.remover(itemExcluir);
		mamAtestadosDAO.flush();
	}
	
	/**
	 * #11942
	 * @param horaInicio
	 * @param horaFim
	 * @throws ApplicationBusinessException
	 */
	public void validarHoraInicioFimAtestado(Date horaInicio, Date horaFim) throws ApplicationBusinessException {
		if (DateUtil.validaDataMaior(horaInicio, horaFim)){
			throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.MSG_VALIDACAO_HORA);
		}
	}
	
	/**
	 * #11942
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void validarCamposPreenchidosAtestadoComparecimento(MamAtestados atestado) throws ApplicationBusinessException{
		if(atestado.getDthrCons() == null || atestado.getDataInicial() == null|| atestado.getDataFinal() == null || atestado.getNroVias() == null){
			throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.PREENCHA_CAMPOS_OBRIGATORIOS);
		}
	}
	
	/**
	 * #11942
	 * @param consulta
	 */
	public void acaoFinalizarAtendimento(AacConsultas consulta){
		List <MamAtestados> atestados = mamAtestadosDAO.obterAtestadosConsultaEmAndamento(consulta);
		for (MamAtestados mamAtestados : atestados) {
			mamAtestados.setIndPendente(DominioIndPendenteAmbulatorio.V);
			mamAtestadosDAO.atualizar(mamAtestados);
		}
		mamAtestadosDAO.flush();
	}
	
	/**
	 * #11942
	 * @param consulta
	 */	
	public void acaoCancelarAtendimento(AacConsultas consulta){
		List <MamAtestados> atestados = mamAtestadosDAO.obterAtestadosConsultaEmAndamento(consulta);
		for (MamAtestados mamAtestados : atestados) {
			mamAtestadosDAO.remover(mamAtestados);
		}
		mamAtestadosDAO.flush();
	}
	
	/**
	 * #11946 e #11947
	 * Adiciona e atualiza atestados.
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void persistirMamAtestados(MamAtestados atestado) throws ApplicationBusinessException  {
		if (atestado.getSeq() == null) {
			preInserir(atestado);
			mamAtestadosDAO.persistir(atestado);
		} else {
			mamAtestadosDAO.atualizar(atestado);
		}
	}
	
	/**
	 * #11946
	 * Exclui os atestados logicamente ou do banco de dados.
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void excluirAtestadoFgtsPis(MamAtestados atestado) throws ApplicationBusinessException {
		atestado = mamAtestadosDAO.obterPorChavePrimaria(atestado.getSeq());
		if (DominioIndPendenteAmbulatorio.V.equals(atestado.getIndPendente())) {
			atestado.setIndPendente(DominioIndPendenteAmbulatorio.E);
			this.persistirMamAtestados(atestado);
		} else if (DominioIndPendenteAmbulatorio.P.equals(atestado.getIndPendente()) || DominioIndPendenteAmbulatorio.R.equals(atestado.getIndPendente())){
			mamAtestadosDAO.remover(atestado);
		}
	}
	
	/**
	 * #11942
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal obterParametroAtestadoComparecimento() throws ApplicationBusinessException{
		return parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_ATEST_COMPARECIMENTO);
	}
	
	/**
	 * #11943
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal obterParametroAtestadoAcompanhamento() throws ApplicationBusinessException{
		return parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_ATEST_ACOMPANHAMENTO);
	}
	
	/**
	 * #11944
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal obterParametroAtestadoMedico() throws ApplicationBusinessException{
		return parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_ATEST_MEDICO);
	}
	
	/**
	 * #11943
	 * @param atestado
	 * @return
	 */
	public List<String> validarCamposAtestadoAcompanhamento(MamAtestados atestado){
		List<String> retorno = new ArrayList<String>();
		if(atestado.getNomeAcompanhante() == null || atestado.getNomeAcompanhante().isEmpty()){
			retorno.add("Declaro que : Campo obrigatório. Digite um valor.");
		}
		if(atestado.getDthrCons() == null){
			retorno.add("Data da consulta : Campo obrigatório. Digite um valor.");
		}
		if(atestado.getDataInicial() == null){
			retorno.add("Hora de início : Campo obrigatório. Digite um valor.");
		}
		if(atestado.getDataFinal() == null){
			retorno.add("Hora de fim : Campo obrigatório. Digite um valor.");
		}
		if(atestado.getNroVias() == null){
			retorno.add("Número de vias : Campo obrigatório. Digite um valor.");
		}
		return retorno;
	}

	/**
	 * #11944
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void validarCamposPreenchidosAtestadoMedico(MamAtestados atestado) throws ApplicationBusinessException{
		if(atestado.getDataInicial() == null|| atestado.getDataFinal() == null || atestado.getNroVias() == null){
			throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.PREENCHA_CAMPOS_OBRIGATORIOS);
		}
	}
	
	
	/**
	 * #45902
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal obterParametroRenovacaoReceita() throws ApplicationBusinessException{
		return parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_ATEST_RENOV);
	}
	
	/**
	 * #45902
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void validarCamposPreenchidosRenovacaoReceita(MamAtestados atestado) throws ApplicationBusinessException{
		if( (atestado.getObservacao() == null || atestado.getObservacao().isEmpty()) || atestado.getNroVias() == null){
			throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.PREENCHA_CAMPOS_OBRIGATORIOS);
		}
	}
	
	/**
	 * #45902
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	public Boolean validarValorMinimo(MamAtestados atestado) throws ApplicationBusinessException{
		if(atestado != null && atestado.getNroVias() != null && atestado.getNroVias() <= 0){
			return false;
		}
		return true;
	}
	
	/**
	 * #45902
	 * @param atestado
	 * @throws ApplicationBusinessException 
	 */
	public Boolean validarValorMinimoPeriodo(MamAtestados atestado) throws ApplicationBusinessException{
		if(atestado != null && atestado.getPeriodo() != null && atestado.getPeriodo() <= 0){
			return false;
		}
		return true;
	}
	
	/**
	 * #11945
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal obterParametroAtestadoMarcacao() throws ApplicationBusinessException{
		return parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_ATEST_MARCACAO);
	}
	
	/**
	 * #11945
	 * @param atestado
	 * @throws ApplicationBusinessException
	 */
	public void validarValorMinimoNumeroVias(MamAtestados atestado) throws ApplicationBusinessException{
		if(atestado.getNroVias() < 1){
			throw new ApplicationBusinessException(MamAtestadosRNExceptionCode.MSG_VALOR_MINIMO_1);
		}
	}
}