package br.gov.mec.aghu.internacao.leitos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicTransfPacientesDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class SolicTransfPacienteRN extends BaseBusiness {


@EJB
private ExtratoLeitoON extratoLeitoON;

private static final Log LOG = LogFactory.getLog(SolicTransfPacienteRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

@Inject
private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;

@Inject
private AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1177278822167513197L;

	private enum SolicTransfPacienteRNExceptionCode implements BusinessExceptionCode {
		 AIN_00716, AIN_00342, AIN_00717, AIN_00718, AIN_00719, AIN_00720, AIN_00721;
		}
	
	
	//ORADB: AINP_ENFORCE_STP_RULES implementação da procedure.
	public void alteraLeitoSolicTransfPaciente(AinSolicTransfPacientes solicitacao,
					AinLeitos leito) throws ApplicationBusinessException{
		
		if (solicitacao.getLeito()==null && leito!=null){
			solicitacao.setLeito(leito);

			String justificativa = null;
			Short tipoMovimentoLeito = null;
			
			AghParametros aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_JUSTIF_RESERVA_LEITO_SOLIC_TRANSF);
			if (aghParametros == null || aghParametros.getVlrTexto() == null) {
				throw new ApplicationBusinessException(
						SolicTransfPacienteRNExceptionCode.AIN_00716);
			}
			justificativa = aghParametros.getVlrTexto();
			
			aghParametros = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_RESERVADO);
			if (aghParametros == null || aghParametros.getVlrNumerico() == null) {
				throw new ApplicationBusinessException(
						SolicTransfPacienteRNExceptionCode.AIN_00342);
			}		
			tipoMovimentoLeito = aghParametros.getVlrNumerico().shortValue();
			
			if (solicitacao.getInternacao()==null){
				throw new ApplicationBusinessException(
						SolicTransfPacienteRNExceptionCode.AIN_00717);
			}
			
			if (solicitacao.getLeito() == null) {
				throw new ApplicationBusinessException(
						SolicTransfPacienteRNExceptionCode.AIN_00718);
			}
	
			AinTiposMovimentoLeito tpMvtLeito = getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(tipoMovimentoLeito);
			getExtratoLeitoON().inserirExtrato(solicitacao.getLeito(), tpMvtLeito, null, null,
					justificativa, new Date(), null, solicitacao
							.getInternacao().getPaciente(), null, null, null,
					null);
	
			if (solicitacao.getLeito().getQuarto() == null) {
				throw new ApplicationBusinessException(
						SolicTransfPacienteRNExceptionCode.AIN_00719);
			}
			
			if (solicitacao.getLeito().getQuarto().isConsSexo() &&
					solicitacao.getLeito().getQuarto().getSexoOcupacao() == null){
				if (solicitacao.getInternacao().getPaciente() == null || 
						solicitacao.getInternacao().getPaciente().getSexo() == null) {
					throw new ApplicationBusinessException(
							SolicTransfPacienteRNExceptionCode.AIN_00720);
				}
				AinQuartos quarto = solicitacao.getLeito().getQuarto();
				
				quarto.setSexoOcupacao(solicitacao.getInternacao().getPaciente().getSexo());
				
				getCadastrosBasicosInternacaoFacade().persistirQuarto(quarto,null);
			}			
			
			AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();
			ainSolicTransfPacientesDAO.persistir(solicitacao);
			ainSolicTransfPacientesDAO.flush();
		}
	}

	/**
	 * ORADB AINC_RET_UNF_INT_SEQ
	 * 
	 * @param seqInternacao
	 * @return
	 */
	public String recuperaUnidadeFuncionalInternacao(Integer seqInternacao) {
		String retValue = null;
		Short seqUnidadeFuncional = null;
		
		AinInternacao internacao = this.getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao);
		
		if (internacao != null) {
			if (DominioLocalPaciente.Q.equals(internacao.getIndLocalPaciente())) {
				AinQuartos quarto = internacao.getQuarto();
				if (quarto != null) {
					seqUnidadeFuncional = quarto.getUnidadeFuncional().getSeq();
				}
			} else if (DominioLocalPaciente.L.equals(internacao.getIndLocalPaciente())) {
				AinLeitos leito = internacao.getLeito();
				if (leito != null) {
					seqUnidadeFuncional = leito.getUnidadeFuncional().getSeq();
				}
			}
			
			if (seqUnidadeFuncional != null) {
				AghUnidadesFuncionais unidadeFuncional = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(
						seqUnidadeFuncional);
				retValue = unidadeFuncional.getLPADAndarAlaDescricao();
			}
		}
		
		return retValue;
	}

	protected ExtratoLeitoON getExtratoLeitoON(){
		return extratoLeitoON;
	}
	
	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO(){
		return ainTiposMovimentoLeitoDAO;
	}
	
	protected AinSolicTransfPacientesDAO getAinSolicTransfPacientesDAO(){
		return ainSolicTransfPacientesDAO;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}
