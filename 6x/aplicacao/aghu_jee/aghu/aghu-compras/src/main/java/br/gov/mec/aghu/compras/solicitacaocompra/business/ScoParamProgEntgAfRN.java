package br.gov.mec.aghu.compras.solicitacaocompra.business;


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParamAutorizacaoScDAO;
import br.gov.mec.aghu.compras.dao.ScoParamProgEntgAfDAO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioIndFrequenciaEntrega;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.model.ScoParamProgEntgAf;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ScoParamProgEntgAfRN extends BaseBusiness {

	private static final long serialVersionUID = -8259974572462521419L;

	public enum ScoParamProgEntgAfRNExceptionCode implements BusinessExceptionCode {
		ERRO_MANTER_SUG_PROG_ENTREGA_M3, ERRO_MANTER_SUG_PROG_ENTREGA_M4, ERRO_MANTER_SUG_PROG_ENTREGA_M5,
		ERRO_MANTER_SUG_PROG_ENTREGA_M6, ERRO_MANTER_SUG_PROG_ENTREGA_M7, ERRO_MANTER_SUG_PROG_ENTREGA_M8,
		ERRO_MANTER_SUG_PROG_ENTREGA_M9, ERRO_MANTER_SUG_PROG_ENTREGA_M12, ERRO_MANTER_SUG_PROG_ENTREGA_M10, ERRO_MANTER_SUG_PROG_ENTREGA_M11, ERRO_MANTER_SUG_PROG_ENTREGA_M13;
		
	}
	
	private static final Log LOG = LogFactory.getLog(ScoParamProgEntgAfRN.class);
	
	@Inject
	private ScoParamAutorizacaoScDAO scoParamAutorizacaoScDAO;
	
	@Inject
	private ScoParamProgEntgAfDAO scoParamProgEntgAfDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Método de pesquisa para a página de lista.
	 * @param cCSolicitante, cCAplicacao, solicitante, situacao, firstResult, maxResult, orderProperty, asc
	 * @author dilceia.alves
	 * @since 16/11/2012
	 */
	public List<ScoParamAutorizacaoSc> pesquisarParamAutorizacaoSc(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		return this.getScoParamAutorizacaoScDAO().pesquisarParamAutorizacaoSc(
				firstResult, maxResult, orderProperty, asc, scoParamAutorizacaoSc);
	}
	
	/**
	 * Método count de pesquisa para a página de lista. 
	 * @param cCSolicitante, cCAplicacao, solicitante, situacao
	 * @author dilceia.alves
	 * @since 16/11/2012
	 */
	public Long pesquisarParamAutorizacaoScCount(ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		return this.getScoParamAutorizacaoScDAO().pesquisarParamAutorizacaoScCount(
				scoParamAutorizacaoSc);
	}
	
	public ScoParamProgEntgAf obterScoParamProgEntgAfPorSolicitacaoDeCompra(Integer numero){
		return getScoParamProgEntgAfDAO().obterPorSolicitacaoDeCompra(numero);
	}
	
	public void excluirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega){
		getScoParamProgEntgAfDAO().excluirScoParamProgEntgAf(programacaoEntrega);
	}
	
	public void persistirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega, Boolean inserir) throws ApplicationBusinessException{
		validaCampos(programacaoEntrega);
		getScoParamProgEntgAfDAO().persistirScoParamProgEntgAf(programacaoEntrega,inserir);
	}

	private void validaCampos(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {
		ScoParamProgEntgAf programacaoEntregaValidar = new ScoParamProgEntgAf();
		programacaoEntregaValidar.setDiaEntrega(programacaoEntrega.getDiaEntrega());
		programacaoEntregaValidar.setFreqDias(programacaoEntrega.getFreqDias());
		programacaoEntregaValidar.setQtdeParcela(programacaoEntrega.getQtdeParcela());
		programacaoEntregaValidar.setDtInicioEntrega(programacaoEntrega.getDtInicioEntrega());
		programacaoEntregaValidar.setNroParcelas(programacaoEntrega.getNroParcelas());
		programacaoEntregaValidar.setObservacao(programacaoEntrega.getObservacao());
		programacaoEntregaValidar.setIndFreqEntrega(programacaoEntrega.getIndFreqEntrega());
		programacaoEntregaValidar.setScoSolicitacaoDeCompra(programacaoEntrega.getScoSolicitacaoDeCompra());
		
		validarFrequenciaDias(programacaoEntregaValidar);
		validarQuantidadeParcelas(programacaoEntregaValidar);
		if(programacaoEntregaValidar.getNroParcelas() == null){
			programacaoEntregaValidar.setNroParcelas((short)0);
		}
		if(programacaoEntregaValidar.getDiaEntrega() == null){
			programacaoEntregaValidar.setDiaEntrega((short)0);
		}
		validaFrequenciaEntrega(programacaoEntregaValidar);
		validaParcelas(programacaoEntregaValidar);
		validaAPartirDe(programacaoEntregaValidar);
		validaQuantidadeParcelas(programacaoEntregaValidar);
		validarDatasEntrega(programacaoEntregaValidar);
		validaQuantidadeAprovada(programacaoEntregaValidar);
	}

	private void validarDatasEntrega(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {
		//RN05
		if(programacaoEntrega.getDiaEntrega() != null){
			if(programacaoEntrega.getIndFreqEntrega() != null && programacaoEntrega.getIndFreqEntrega().getDescricao().equals(DominioIndFrequenciaEntrega.S.getDescricao()) && programacaoEntrega.getDiaEntrega() > 0 && programacaoEntrega.getDiaEntrega().intValue() != DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(programacaoEntrega.getDtInicioEntrega())).getNumeroDiaDaSemana()){
				throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M10);
			}
			Calendar data = DateUtil.getCalendarBy(programacaoEntrega.getDtInicioEntrega());
			if(programacaoEntrega.getIndFreqEntrega() != null && programacaoEntrega.getIndFreqEntrega().getDescricao().equals(DominioIndFrequenciaEntrega.M.getDescricao()) && programacaoEntrega.getDiaEntrega() > 0 && programacaoEntrega.getDiaEntrega().intValue() != data.get(Calendar.DAY_OF_MONTH)){
				throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M11);
			}
		}
	}

	private void validaQuantidadeAprovada(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {
		if((programacaoEntrega.getQtdeParcela() * programacaoEntrega.getNroParcelas().intValue() - programacaoEntrega.getScoSolicitacaoDeCompra().getQtdeAprovada().intValue()) > programacaoEntrega.getQtdeParcela()){
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M12);
		}
		if((programacaoEntrega.getScoSolicitacaoDeCompra().getQtdeAprovada().intValue() - (programacaoEntrega.getQtdeParcela() * programacaoEntrega.getNroParcelas().intValue())) >= programacaoEntrega.getQtdeParcela()){
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M12);
		}
		if((programacaoEntrega.getScoSolicitacaoDeCompra().getQtdeAprovada().intValue() / programacaoEntrega.getQtdeParcela()) - Math.abs(programacaoEntrega.getScoSolicitacaoDeCompra().getQtdeAprovada().intValue() / programacaoEntrega.getQtdeParcela()) > 0){
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M12);
		}
		if(((programacaoEntrega.getQtdeParcela() * programacaoEntrega.getNroParcelas().intValue()) - programacaoEntrega.getScoSolicitacaoDeCompra().getQtdeAprovada().intValue()) == programacaoEntrega.getQtdeParcela()){
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M12);
		}
		if(((programacaoEntrega.getQtdeParcela() * programacaoEntrega.getNroParcelas().intValue()) - programacaoEntrega.getScoSolicitacaoDeCompra().getQtdeAprovada().intValue()) == programacaoEntrega.getNroParcelas().intValue()){
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M12);
		}
	}

	private void validaQuantidadeParcelas(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {
		// RN 04
		if(programacaoEntrega.getQtdeParcela() == 0){
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M9);
		}
	}

	private void validaAPartirDe(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {
		// RN 03
		if(programacaoEntrega.getDtInicioEntrega() == null){
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M8);
		}
	}

	private void validaParcelas(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {
		// RN02
		if(	programacaoEntrega.getQtdeParcela() 	> 0 	&& 
			programacaoEntrega.getNroParcelas().intValue() 	> 0 	&& 
			programacaoEntrega.getFreqDias().intValue() 		== 0 	&& 
			null ==	programacaoEntrega.getIndFreqEntrega()){
			
			throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M7);
		}
		
		this.validarScoParamProgEntgAfCamposNulosParaZero(programacaoEntrega);
	}

	private void validaFrequenciaEntrega(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {
		if(programacaoEntrega.getIndFreqEntrega() != null){
			avaliaFrequencias(programacaoEntrega);
		}
	}

	private void avaliaFrequencias(ScoParamProgEntgAf programacaoEntrega) throws ApplicationBusinessException {

			if(programacaoEntrega.getFreqDias().intValue() > 0){
				throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M3);
			}

		if(programacaoEntrega.getDiaEntrega() != null){
			if(DominioIndFrequenciaEntrega.D.equals(programacaoEntrega.getIndFreqEntrega()) && programacaoEntrega.getDiaEntrega().intValue() > 0){
				throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M4);
			}else if(DominioIndFrequenciaEntrega.S.equals(programacaoEntrega.getIndFreqEntrega()) && programacaoEntrega.getDiaEntrega().intValue() == 0){
				throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M5);
			}else if(DominioIndFrequenciaEntrega.M.equals(programacaoEntrega.getIndFreqEntrega()) && programacaoEntrega.getDiaEntrega().intValue() == 0){
				throw new ApplicationBusinessException(ScoParamProgEntgAfRNExceptionCode.ERRO_MANTER_SUG_PROG_ENTREGA_M6);
			}
		}
	}
	
	public void validarScoParamProgEntgAfCamposNulosParaZero(ScoParamProgEntgAf scoParamProgEntgAf){
		if(scoParamProgEntgAf != null){
			
			validarFrequenciaDias(scoParamProgEntgAf);
			validarQuantidadeParcelas(scoParamProgEntgAf);
			validarNumeroParcelas(scoParamProgEntgAf);
			preencherValores(scoParamProgEntgAf);
		}
	}

	private void preencherValores(ScoParamProgEntgAf scoParamProgEntgAf) {
		if(scoParamProgEntgAf.getQtdeParcela() == 0 && scoParamProgEntgAf.getNroParcelas() == 0 && scoParamProgEntgAf.getFreqDias() > 0){
			scoParamProgEntgAf.setNroParcelas(new BigDecimal(scoParamProgEntgAf.getScoSolicitacaoDeCompra().getQtdeAprovada()).divide(new BigDecimal(scoParamProgEntgAf.getFreqDias())).shortValue());
		} else if(scoParamProgEntgAf.getQtdeParcela() == 0 && scoParamProgEntgAf.getNroParcelas() > 0){
			scoParamProgEntgAf.setQtdeParcela(new BigDecimal(scoParamProgEntgAf.getScoSolicitacaoDeCompra().getQtdeAprovada()).divide(new BigDecimal(scoParamProgEntgAf.getNroParcelas())).intValue());
		}else if(scoParamProgEntgAf.getNroParcelas() == 0 && scoParamProgEntgAf.getQtdeParcela() > 0){
			scoParamProgEntgAf.setNroParcelas(new BigDecimal(scoParamProgEntgAf.getScoSolicitacaoDeCompra().getQtdeAprovada()).divide(new BigDecimal(scoParamProgEntgAf.getQtdeParcela())).shortValue());
		}
	}

	private void validarNumeroParcelas(ScoParamProgEntgAf scoParamProgEntgAf) {
		if(scoParamProgEntgAf.getNroParcelas() == null){
			scoParamProgEntgAf.setNroParcelas((short)0);
			
			if(scoParamProgEntgAf.getFreqDias() > 0){
				scoParamProgEntgAf.setNroParcelas(new BigDecimal(scoParamProgEntgAf.getScoSolicitacaoDeCompra().getQtdeAprovada()).divide(new BigDecimal(scoParamProgEntgAf.getFreqDias())).shortValue());
			}
		}
	}

	private void validarQuantidadeParcelas(ScoParamProgEntgAf scoParamProgEntgAf) {
		if(scoParamProgEntgAf.getQtdeParcela() == null){
			scoParamProgEntgAf.setQtdeParcela(0);
		}
	}

	private void validarFrequenciaDias(ScoParamProgEntgAf scoParamProgEntgAf) {
		if(scoParamProgEntgAf.getFreqDias() == null){
			scoParamProgEntgAf.setFreqDias((short)0);
		}
	}

	public ScoParamAutorizacaoSc obterParamAutorizacaoSc(Integer seq){
		return getScoParamAutorizacaoScDAO().obterPorChavePrimaria(seq);
	}
	
	
	public ScoParamAutorizacaoSc obterParametrosAutorizacaoSCPrioridade(FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicacao,
			RapServidores servidor){
		return getScoParamAutorizacaoScDAO().obterParametrosAutorizacaoSCPrioridade(centroCusto, centroCustoAplicacao, servidor);
	}
	
	protected ScoParamAutorizacaoScDAO getScoParamAutorizacaoScDAO(){
		return scoParamAutorizacaoScDAO;
	}
	
	protected ScoParamProgEntgAfDAO getScoParamProgEntgAfDAO() {
		return scoParamProgEntgAfDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
}