package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncionario;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AtualizarConsultaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AtualizarConsultaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IExamesFacade examesFacade;

		/**
	 * 
	 */
	private static final long serialVersionUID = -1008535672362764178L;

		public enum AtualizarConsultaONExceptionCode implements
		BusinessExceptionCode {
	
		AAC_00274, AAC_00735, AAC_00693, AAC_00694, AAC_00104, AAC_00261;
		
	}

	
	public Boolean existeSolicitacaoExame(AacConsultas consulta) {
		boolean existeSolicitacoesExame = getExamesFacade().verificarExistenciaSolicitacoesExameComRetornoPeloNumConsulta(consulta.getNumero());
		if(existeSolicitacoesExame) {
			return true;
		}
		return false;
	}

	public Boolean existeAtendimentoEmAndamento(AacConsultas consulta) {
		List<AghAtendimentos> atendimentos = getAghuFacade().listarAtendimentoEmAnadamentoConsulta(consulta.getNumero());
		if(atendimentos!=null && !atendimentos.isEmpty()){
			return true;
		}		
		return false;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void preAtualizar(AacConsultas consulta) throws BaseException {
		if(consulta.getAtendimento() !=null) {
			throw new ApplicationBusinessException(AtualizarConsultaONExceptionCode.AAC_00274);
		}
		
		if(consulta.getServidorConsultado() == null && (DominioFuncionario.F.equals(consulta.getIndFuncionario()) || DominioFuncionario.D.equals(consulta.getIndFuncionario()))) {
			throw new ApplicationBusinessException(AtualizarConsultaONExceptionCode.AAC_00735);
		}
		
		if(consulta.getServidorConsultado() != null) {
			RapDependentes dependente = getRegistroColaboradorFacade().obterDependentePeloPacCodigoPeloVinculoEMatricula(consulta.getPaciente().getCodigo(), consulta.getServidorConsultado().getId().getVinCodigo(), consulta.getServidorConsultado().getId().getMatricula());
			RapServidores servidor = getRegistroColaboradorFacade().obterServidorPeloProntuarioPeloVinculoEMatricula(consulta.getPaciente().getProntuario(), consulta.getServidorConsultado().getId().getVinCodigo(), consulta.getServidorConsultado().getId().getMatricula());
			if(DominioFuncionario.D.equals(consulta.getIndFuncionario()) && dependente == null) {
				throw new ApplicationBusinessException(AtualizarConsultaONExceptionCode.AAC_00693);
			}
			if(DominioFuncionario.F.equals(consulta.getIndFuncionario()) && servidor == null) {
				throw new ApplicationBusinessException(AtualizarConsultaONExceptionCode.AAC_00694);
			}
		}
		
		List<AghAtendimentos> atendimentos = getAghuFacade().listarAtendimentosPorNumeroConsulta(consulta.getNumero());
		if(atendimentos == null || atendimentos.isEmpty()) {
			throw new ApplicationBusinessException(AtualizarConsultaONExceptionCode.AAC_00104);
		}
		
		AghParametros unimedFunConv = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIMED_FUNC_CNV);
		AghParametros unimedFuncPl = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIMED_FUNC_PL);
		
		Boolean existeAtendimentoUrgencia = false;
		for (AghAtendimentos atendimento : atendimentos){
			if (atendimento.getAtendimentoUrgencia()!=null){
				existeAtendimentoUrgencia = true;
			}
		}
		if(existeAtendimentoUrgencia && 
				(consulta.getConvenioSaudePlano() != null && 
						!consulta.getConvenioSaudePlano().getId().equals(new FatConvenioSaudePlanoId(unimedFunConv.getVlrNumerico().shortValue(), unimedFuncPl.getVlrNumerico().byteValue())))) {
			throw new ApplicationBusinessException(AtualizarConsultaONExceptionCode.AAC_00261);
		}
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}	

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
}
