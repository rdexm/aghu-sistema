package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.prescricaomedica.vo.DadosDialiseVO;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


/**
 * Classe responsável por verificar o destino do botão Diálise na Lista de Pacientes
 * 
 * @author israel.haas
 * 
 */
@Stateless
public class ListaPacientesInternadosDialiseON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ListaPacientesInternadosDialiseON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private static final long serialVersionUID = -3971874923772129801L;
	
	public enum ListaPacientesInternadosDialiseONExceptionCode implements BusinessExceptionCode {
		MPM_04011;
	}

	/**
	 * @ORADB BUT_TRAT_DIALISE
	 * @param atdSeq
	 * @param pacCodigo
	 * @return
	 * @throws BaseException 
	 */
	public DadosDialiseVO obterCaminhoDialise(Integer atdSeq, String nomeMicrocomputador) throws BaseException {
		DadosDialiseVO dadosDialise = new DadosDialiseVO();
		dadosDialise.setAtdSeq(atdSeq);
		
		Short cspCnvCodigo = null;
		Byte cspSeq = null;
		AghAtendimentos atendimento = this.aghuFacade.obterAtendimentoPeloSeq(atdSeq);
		
		if (atendimento.getInternacao() != null) {
			AinInternacao internacao = this.internacaoFacade
					.obterAinInternacaoPorChavePrimaria(atendimento.getInternacao().getSeq(), AinInternacao.Fields.CONVENIO_SAUDE_PLANO);
			
			cspCnvCodigo = internacao.getConvenioSaudePlano().getId().getCnvCodigo();
			cspSeq = internacao.getConvenioSaudePlano().getId().getSeq();
			
		} else if (atendimento.getAtendimentoUrgencia() != null) {
			AinAtendimentosUrgencia atendUrgencia = this.internacaoFacade
					.obterAinAtendimentosUrgenciaPorChavePrimaria(atendimento.getAtendimentoUrgencia().getSeq());
			
			cspCnvCodigo = atendUrgencia.getConvenioSaudePlano().getId().getCnvCodigo();
			cspSeq = atendUrgencia.getConvenioSaudePlano().getId().getSeq();
			
		} else {
			throw new ApplicationBusinessException(ListaPacientesInternadosDialiseONExceptionCode.MPM_04011);
		}
		AghParametros paramUnfSeqDialise = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNF_SEQ_DIALISE);
		AghParametros paramEspSeqDialise = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ESP_SEQ_DIALISE);
		
		List<AghProfEspecialidades> profEspecialidades = this.aghuFacade.listarProfEspecialidadesPorEspSeq((short) 510);
		Integer matriculaResp = !profEspecialidades.isEmpty() ? profEspecialidades.get(0).getRapServidor().getId().getMatricula() : null;
		Short vinCodigoResp = !profEspecialidades.isEmpty() ? profEspecialidades.get(0).getRapServidor().getId().getVinCodigo() : null;
		
		dadosDialise.setSeqTratamento(this.buscarTratamentoDialise(atendimento.getPaciente().getCodigo(), matriculaResp,
				vinCodigoResp, paramUnfSeqDialise.getVlrNumerico().shortValue(), paramEspSeqDialise.getVlrNumerico().shortValue(),
				cspCnvCodigo, cspSeq, nomeMicrocomputador));
		
		return dadosDialise;
	}
	
	/**
	 * @ORADB MPTC_BUSCA_TRP_DIALI
	 * @param pacCodigo
	 * @param matriculaResp
	 * @param vinCodigoResp
	 * @param unfSeqDialise
	 * @param espSeqDialise
	 * @param cspCnvCodigo
	 * @param cspSeq
	 * @return
	 * @throws BaseException  
	 */
	private Integer buscarTratamentoDialise(Integer pacCodigo, Integer matriculaResp, Short vinCodigoResp, Short unfSeqDialise,
			Short espSeqDialise, Short cspCnvCodigo, Byte cspSeq, String nomeMicrocomputador) throws BaseException {
		
		Integer atdSeqRetorno = null;
		
		AghParametros paramtipoTratDialise = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_TRAT_DIALISE);
		
		List<Object[]> listaAtendimentosPaciente = this.aghuFacade
				.listarAtendimentosPacienteTratamentoPorCodigo(pacCodigo, paramtipoTratDialise.getVlrNumerico().intValue());
		
		if (!listaAtendimentosPaciente.isEmpty()) {
			Object[] obj = listaAtendimentosPaciente.get(0);
			atdSeqRetorno = (Integer) obj[0];
			
		} else {
			this.procedimentoTerapeuticoFacade.inserirMptTratamentoTerapeuticoDialise(unfSeqDialise, espSeqDialise, pacCodigo,
					paramtipoTratDialise.getVlrNumerico().intValue(), cspCnvCodigo, cspSeq, matriculaResp, vinCodigoResp,
					nomeMicrocomputador);
			
			List<Object[]> listaAtendimentosPacienteAux = this.aghuFacade
					.listarAtendimentosPacienteTratamentoPorCodigo(pacCodigo, paramtipoTratDialise.getVlrNumerico().intValue());
			
			if (!listaAtendimentosPacienteAux.isEmpty()) {
				Object[] obj = listaAtendimentosPacienteAux.get(0);
				atdSeqRetorno = (Integer) obj[0];
			}
		}
		return atdSeqRetorno;
	}
	
}
