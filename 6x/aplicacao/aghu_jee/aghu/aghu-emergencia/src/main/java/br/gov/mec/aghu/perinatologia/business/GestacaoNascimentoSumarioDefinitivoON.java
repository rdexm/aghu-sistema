package br.gov.mec.aghu.perinatologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.casca.service.ICascaService;
import br.gov.mec.aghu.configuracao.vo.EquipeVO;
import br.gov.mec.aghu.dominio.DominioGravidez;
import br.gov.mec.aghu.dominio.DominioTipoNascimento;
import br.gov.mec.aghu.dominio.DominioTipoParto;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.perinatologia.dao.McoAtendTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoCesarianasDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoProfNascsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.vo.DadosGestacaoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class GestacaoNascimentoSumarioDefinitivoON  extends BaseBusiness {

	@EJB
	private IAmbulatorioService ambulatorioService;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoTrabPartosDAO mcoTrabPartosDAO;
	
	@Inject
	private McoAtendTrabPartosDAO mcoAtendTrabPartosDAO;
	
	@Inject
	private McoCesarianasDAO mcoCesarianasDAO;
	
	@Inject
	private McoProfNascsDAO mcoProfNascsDAO;
	
	@EJB
	private ICascaService cascaService;
	
	@EJB
	private IExamesService examesService;
	
	@EJB
	private IRegistroColaboradorService registroColaboradorService;
	
	private enum RegistrarGestacaoAbaNascimentoONExceptionCode implements BusinessExceptionCode {
		MCO_00792
		, MCO_00794
		, MCO_00795
		, MCO_00796
		, MCO_00797
		, MCO_00806
		, MCO_00790
		, MCO_00805
		, ERRO_DECISAO_TOMADA_NAO_INFORMADA
		, MENSAGEM_ERRO_TRABALHO_PARTO
		, MCO_00741
		, MENSAGEM_ERRO_EQUIPE_NAO_INFORMADA
		, MENSAGEM_ERRO_CONTAMINACAO_NAO_INFORMADA
		, MENSAGEM_ERRO_GESTACAO_GEMELAR_NASC_INCOMPLETO
		, MENSAGEM_ERRO_ANESTESISTA
		, MCO_00737, MENSAGEM_ERRO_PARAMETRO
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3983984798735421178L;;

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void validarDados(Boolean indAcompanhante
			,Integer pacCodigo
			,Short gsoSeqp
			,DadosGestacaoVO gestacacao
			,EquipeVO equipeVO
			,DadosNascimentoVO nascimentoSelecionado) throws ApplicationBusinessException, ServiceException{
		
		boolean blocoCirugicoAtivo = this.validarBlocoCirurgicoAtivo();
		
		
		validarTrabalhoParto(pacCodigo, gsoSeqp);
		
		validarGravidez(gestacacao);
		
		if(blocoCirugicoAtivo){
			validarAcompanhante(indAcompanhante);
			validarMensagemEquipeNaoInformada(equipeVO);
			validarContaminacao(pacCodigo, gsoSeqp, nascimentoSelecionado);
		}	
		
		validarGemelar(pacCodigo, gsoSeqp, gestacacao);
		
		validarAnestesiologista(pacCodigo, gsoSeqp, nascimentoSelecionado);
		
	}

	private void validarAnestesiologista(Integer pacCodigo, Short gsoSeqp,
			DadosNascimentoVO nascimentoSelecionado)
			throws ApplicationBusinessException, ServiceException {
		List<McoProfNascs> listMcoProfNascs = this.mcoProfNascsDAO.listarProfNascs(gsoSeqp, pacCodigo, nascimentoSelecionado.getSeqp());
		
		String sigla = (String) this.parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_CONSELHO_PROFISSIONAL_MED_SOLIC.toString(), "vlrTexto");
		String cbo = (String) this.parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_CBO_ANESTESIOLOGISTA.toString(), "vlrTexto");
		
		if(listMcoProfNascs == null || listMcoProfNascs.isEmpty()){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_ANESTESISTA);
		} else {
			//Chamar o serviço 44878 e finalizar o item 14 e 15 da RN04 do caso de uso 26325
			Boolean possuiCbo = Boolean.FALSE;
			for(McoProfNascs anestesistas : listMcoProfNascs){
				possuiCbo = registroColaboradorService.verificarProfissionalPossuiCBOAnestesista(anestesistas.getId().getSerVinCodigoNasc(), anestesistas.getId().getSerMatriculaNasc(), new String[]{sigla}, new String[]{cbo});
				if(possuiCbo){
					break;
				}
			}
			
			if(!possuiCbo){
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_ANESTESISTA);
			}
		}
	}

	private void validarGemelar(Integer pacCodigo, Short gsoSeqp,
			DadosGestacaoVO gestacacao) throws ApplicationBusinessException {
		if(gestacacao.getGemelar() != null && !"N".equals(gestacacao.getGemelar())){
			List<McoNascimentos> listaNascimentos = this.mcoNascimentosDAO.listarNascimentos(pacCodigo, gsoSeqp);
			if(listaNascimentos == null 
					|| listaNascimentos.isEmpty() 
					|| (listaNascimentos.size() < Integer.parseInt(gestacacao.getGemelar()))){
				
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_GESTACAO_GEMELAR_NASC_INCOMPLETO);
			}
		}
	}

	private void validarContaminacao(Integer pacCodigo, Short gsoSeqp,
			DadosNascimentoVO nascimentoSelecionado)
			throws ApplicationBusinessException {
		if(DominioTipoNascimento.C == nascimentoSelecionado.getTipoNascimento()){
			McoCesarianas cesariana = this.mcoCesarianasDAO.obterMcoCesarianas(pacCodigo, gsoSeqp, nascimentoSelecionado.getSeqp());
			if(cesariana == null || cesariana.getContaminacao() == null){
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_CONTAMINACAO_NAO_INFORMADA);
			}
		}
	}

	private void validarMensagemEquipeNaoInformada(EquipeVO equipeVO)
			throws ApplicationBusinessException {
		if(equipeVO == null || equipeVO.getSeq() == null){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_EQUIPE_NAO_INFORMADA);
		}
	}

	private void validarGravidez(DadosGestacaoVO gestacacao)
			throws ApplicationBusinessException {
		if(DominioGravidez.GCO != gestacacao.getGravidez()){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00741);
		}
	}

	private void validarAcompanhante(Boolean indAcompanhante)
			throws ApplicationBusinessException {
		
		if(indAcompanhante == null) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00790);
		}
	}

	private boolean validarBlocoCirurgicoAtivo() {
		return this.cascaService.verificarSeModuloEstaAtivo("blococirurgico");
	}

	private void validarTrabalhoParto(Integer pacCodigo, Short gsoSeqp)
			throws ApplicationBusinessException {
		
		McoTrabPartos mcoTrabPartos = mcoTrabPartosDAO.obterMcoTrabPartosPorId(pacCodigo, gsoSeqp);
		if(DominioTipoParto.C == mcoTrabPartos.getTipoParto() && mcoTrabPartos.getIndicacaoNascimento() == null){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00805);
		} else if(mcoTrabPartos.getTipoParto() == null){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_DECISAO_TOMADA_NAO_INFORMADA);
		}
		
		List<McoAtendTrabPartos> mcoAtendTrabPartos = mcoAtendTrabPartosDAO.buscarListaMcoAtendTrabPartos(pacCodigo, gsoSeqp);
		if(mcoAtendTrabPartos == null || mcoAtendTrabPartos.size() == 0 && mcoTrabPartos.getJustificativa() == null){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_TRABALHO_PARTO);
		}
	}
	
	public Boolean verificarChamarModalParaExamesVDRL(Integer numeroConsulta, String sigla) throws ApplicationBusinessException{
		if(this.cascaService.verificarSeModuloEstaAtivo("agendamentoExames")){
			return !this.validarExameVDRL(numeroConsulta);
		} 
		return Boolean.FALSE;
	} 
	
	public Boolean validarExameVDRL(Integer conNumero) throws ApplicationBusinessException{
		
		String url = (String) this.parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_EXAME_VDRL.toString(), "vlrTexto");

		if(url != null && !url.isEmpty()){
			Integer atdSeq = this.ambulatorioService.obterAtdSeqPorNumeroConsulta(conNumero);
			
			return this.examesService.verificarExameVDRLnaoSolicitado(atdSeq);
			
		} else {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00737);
		}
	}

}
