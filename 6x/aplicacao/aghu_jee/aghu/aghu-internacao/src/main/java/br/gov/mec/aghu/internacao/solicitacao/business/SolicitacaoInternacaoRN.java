package br.gov.mec.aghu.internacao.solicitacao.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class SolicitacaoInternacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SolicitacaoInternacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4119427003623690463L;

	private enum SolicitacaoInternacaoRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_ATENDIDA, MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_LIBERADA, MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_PENDENTE, MENSAGEM_ERRO_HABILITAR_INTERNACAO, MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_CANCELADA, MENSAGEM_ERRO_ALTERACAO_SOLICITACAO_INTERNACAO, MENSAGEM_ERRO_PERSISTIR_SOLICITACAO_INTERNACAO, MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO, MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO
	}

	public ConvenioPlanoVO obterConvenioPlanoVO(Short cnvCodigo, Byte seq) {
		FatConvenioSaudePlano fatConvenioSaudePlano = getFaturamentoFacade().obterFatConvenioSaudePlano(cnvCodigo, seq);
		return popularConvenioPlanoVO(fatConvenioSaudePlano);
	}
	
	/**
	 * Obtém o convênio e o plano que permite internação
	 * 
	 * @param cnvCodigo
	 * @return
	 */
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(Short cnvCodigo) {
		return getFaturamentoFacade().obterConvenioPlanoInternacao(cnvCodigo);
	}
	
	/**
	 * ORADB View V_AIN_CONVENIO_PLANO
	 * 
	 */
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVO(Integer firstResult, Integer maxResult, String strPesquisa) {
		List<FatConvenioSaudePlano> resultList = getFaturamentoFacade().pesquisarConvenioPlano(firstResult, maxResult, strPesquisa);
		List<ConvenioPlanoVO> listaConvenioPlanoVO = new ArrayList<ConvenioPlanoVO>();
		if (resultList != null && !resultList.isEmpty()) {
			for (FatConvenioSaudePlano convenioSaudePlano : resultList) {
				ConvenioPlanoVO convenioPlanoVO = popularConvenioPlanoVO(convenioSaudePlano);
				listaConvenioPlanoVO.add(convenioPlanoVO);
			}
		}
		return listaConvenioPlanoVO;
	}

	private ConvenioPlanoVO popularConvenioPlanoVO(
			FatConvenioSaudePlano convenioSaudePlano) {
		ConvenioPlanoVO convenioPlanoVO = new ConvenioPlanoVO();
		convenioPlanoVO.setCnvCodigo(convenioSaudePlano.getConvenioSaude()
				.getCodigo());
		convenioPlanoVO.setPlano(convenioSaudePlano.getId().getSeq());
		convenioPlanoVO.setDescConv(convenioSaudePlano.getConvenioSaude()
				.getDescricao());
		convenioPlanoVO.setDescPlan(convenioSaudePlano.getDescricao());
		convenioPlanoVO.setIndPermissaoInternacao(convenioSaudePlano
				.getConvenioSaude().getPermissaoInternacao());
		convenioPlanoVO.setIndVerfEscalaProfInt(convenioSaudePlano
				.getConvenioSaude().getVerificaEscalaProfInt());
		convenioPlanoVO.setIndExigeNumMatr(convenioSaudePlano
				.getConvenioSaude().getExigeNumeroMatricula());
		convenioPlanoVO.setIndSelAutomProf(convenioSaudePlano
				.getConvenioSaude().getSelecaoAutomaticaProf());
		convenioPlanoVO.setIndSituacao(convenioSaudePlano.getConvenioSaude()
				.getSituacao());
		convenioPlanoVO.setIndRestringeProf(convenioSaudePlano
				.getConvenioSaude().getRestringeProf());
		convenioPlanoVO.setGrupoConvenio(convenioSaudePlano.getConvenioSaude()
				.getGrupoConvenio());
		convenioPlanoVO.setIndTipoPlano(convenioSaudePlano.getIndTipoPlano());
		convenioPlanoVO
				.setPlanoIndSituacao(convenioSaudePlano.getIndSituacao());

		if (convenioSaudePlano.getConvenioSaude().getPagador() == null) {
			convenioPlanoVO.setPgdSeq(null);
		} else {
			convenioPlanoVO.setPgdSeq(convenioSaudePlano.getConvenioSaude()
					.getPagador().getSeq());
		}

		return convenioPlanoVO;
	}

	/**
	 * ORADB VIEW V_AGH_ESP_CRM
	 *  
	 * 
	 */
	public List<EspCrmVO> pesquisarEspCrmVO(Object strPesquisa,	AghEspecialidades especialidade) throws ApplicationBusinessException {
		 return pesquisarEspCrmVO(strPesquisa, especialidade, null);
	}
		
	public List<EspCrmVO> pesquisarEspCrmVO(Object strPesquisa, AghEspecialidades especialidade, DominioSimNao ambulatorio)
		throws ApplicationBusinessException {
		AghParametros qualificacaoMedicina = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_MEDICINA);
		if (qualificacaoMedicina == null || qualificacaoMedicina.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(SolicitacaoInternacaoRNExceptionCode.MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO);
		}
		AghParametros qualificacaoOdonto = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_ODONTO);
		if (qualificacaoOdonto == null
				|| qualificacaoOdonto.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoInternacaoRNExceptionCode.MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO);
		}
		Integer[] tipoQualificacao = {
				qualificacaoOdonto.getVlrNumerico().intValue(),
				qualificacaoMedicina.getVlrNumerico().intValue() };
		return getAghuFacade().pesquisarEspCrmVO(strPesquisa, especialidade, ambulatorio, tipoQualificacao);
	}

	public EspCrmVO obterEspCrmVO(Object strPesquisa, AghEspecialidades especialidade, DominioSimNao ambulatorio,
			RapServidores servidor) throws ApplicationBusinessException {
		AghParametros qualificacaoMedicina = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_MEDICINA);
		if (qualificacaoMedicina == null || qualificacaoMedicina.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(SolicitacaoInternacaoRNExceptionCode.MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO);
		}
		AghParametros qualificacaoOdonto = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_ODONTO);
		if (qualificacaoOdonto == null
				|| qualificacaoOdonto.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoInternacaoRNExceptionCode.MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO);
		}
		Integer[] tipoQualificacao = {
				qualificacaoOdonto.getVlrNumerico().intValue(),
				qualificacaoMedicina.getVlrNumerico().intValue() };
		return getAghuFacade().obterEspCrmVO(strPesquisa, especialidade, ambulatorio, servidor, tipoQualificacao);
	}
	
	public List<FatVlrItemProcedHospComps> pesquisarProcedimentos(Object strPesquisa, Short parametroProcedimento,
			AipPacientes paciente, Integer cidSeq) throws ApplicationBusinessException {
		return getFaturamentoFacade().pesquisarProcedimentos(strPesquisa, parametroProcedimento, paciente, cidSeq);
	}
	
	
	/**
	 * ORADB VIEW V_FAT_ASSOCIACAO_PROCEDIMENTOS Este método implementa a view
	 * descrita acima, porém trazendo apenas o campo CGI.CPG_CPH_CSP_CNV_CODIGO,
	 * necessário na pesquisa de procedimentos de internação quando um CID já
	 * tiver sido informado.
	 * 
	 * @param cidSeq
	 * @return listaCodTabelas
	 */
	public List<Long> pesquisarFatAssociacaoProcedimentos(Integer cidSeq) {
		return getFaturamentoFacade().pesquisarFatAssociacaoProcedimentos(cidSeq);
	}
	
	/**
	 * ORADB Trigger AINT_SIN_BRU
	 * 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void validarDados(
			AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		if (solicitacaoInternacao.getIndSitSolicInternacao().equals(
				DominioSituacaoSolicitacaoInternacao.C)) {
			solicitacaoInternacao.setDthrAtendimentoSolicitacao(Calendar
					.getInstance().getTime());
		}
		if ((solicitacaoInternacaoTemp.getLeito() == null && solicitacaoInternacao
				.getLeito() != null)
				|| (solicitacaoInternacaoTemp.getQuarto() == null && solicitacaoInternacao
						.getQuarto() != null)
				|| (solicitacaoInternacaoTemp.getUnidadeFuncional() == null && solicitacaoInternacao
						.getUnidadeFuncional() != null)) {
			solicitacaoInternacao.setDthrAtendimentoSolicitacao(Calendar
					.getInstance().getTime());
		}
		//Verifica validações de Solicitação de Internação SOMENTE SE
		//não estiver substituindo prontuário
		if (!substituirProntuario){

			if (solicitacaoInternacaoTemp.getIndSitSolicInternacao().equals(
					DominioSituacaoSolicitacaoInternacao.E)
					&& !solicitacaoInternacao.getIndSitSolicInternacao().equals(
							DominioSituacaoSolicitacaoInternacao.E)) {
				throw new ApplicationBusinessException(
						SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_ATENDIDA);
				
			}
			if (solicitacaoInternacaoTemp.getIndSitSolicInternacao().equals(
					DominioSituacaoSolicitacaoInternacao.L)
					&& !solicitacaoInternacao.getIndSitSolicInternacao().equals(
							DominioSituacaoSolicitacaoInternacao.A) && !solicitacaoInternacao
							.getIndSitSolicInternacao().equals(
									DominioSituacaoSolicitacaoInternacao.C)) {
				throw new ApplicationBusinessException(
						SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_LIBERADA);
			}
			if (solicitacaoInternacaoTemp.getIndSitSolicInternacao().equals(
					DominioSituacaoSolicitacaoInternacao.P)
					&& !solicitacaoInternacao.getIndSitSolicInternacao().equals(
							DominioSituacaoSolicitacaoInternacao.L) && !solicitacaoInternacao
							.getIndSitSolicInternacao().equals(
									DominioSituacaoSolicitacaoInternacao.C)) {
				throw new ApplicationBusinessException(
						SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_PENDENTE);
				
			}
			if (solicitacaoInternacaoTemp.getIndSitSolicInternacao().equals(
					DominioSituacaoSolicitacaoInternacao.A)) {
				if (!solicitacaoInternacao.getIndSitSolicInternacao().equals(
						DominioSituacaoSolicitacaoInternacao.E)) {
					throw new ApplicationBusinessException(
							SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_ATENDIDA);
				} else if (solicitacaoInternacao.getLeito() == null
						&& solicitacaoInternacao.getQuarto() == null
						&& solicitacaoInternacao.getUnidadeFuncional() == null) {
					throw new ApplicationBusinessException(
							SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_HABILITAR_INTERNACAO);
					
				}
			}
			
			if (solicitacaoInternacaoTemp.getIndSitSolicInternacao().equals(
					DominioSituacaoSolicitacaoInternacao.C)
					&& !solicitacaoInternacao.getIndSitSolicInternacao().equals(
							DominioSituacaoSolicitacaoInternacao.C)) {
				throw new ApplicationBusinessException(
						SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_SOLICITACAO_INTERNACAO_CANCELADA);
			}
			
			if((solicitacaoInternacaoTemp.getLeito()!=null && solicitacaoInternacaoTemp.getLeito().getLeitoID()!=null
					&& solicitacaoInternacao.getLeito()!=null && solicitacaoInternacao.getLeito().getLeitoID()!=null
					&& !solicitacaoInternacao.getLeito().getLeitoID().equals(solicitacaoInternacaoTemp.getLeito().getLeitoID()))||
					(solicitacaoInternacaoTemp.getQuarto()!=null && solicitacaoInternacaoTemp.getQuarto().getNumero()!=null
					&& solicitacaoInternacao.getQuarto()!=null && solicitacaoInternacao.getQuarto().getNumero()!=null
					&& !solicitacaoInternacao.getQuarto().getNumero().equals(solicitacaoInternacaoTemp.getQuarto().getNumero()))||
					(solicitacaoInternacaoTemp.getUnidadeFuncional()!=null && solicitacaoInternacaoTemp.getUnidadeFuncional().getSeq()!=null
					&& solicitacaoInternacao.getUnidadeFuncional()!=null && solicitacaoInternacao.getUnidadeFuncional().getSeq()!=null
					&& !solicitacaoInternacao.getUnidadeFuncional().getSeq().equals(solicitacaoInternacaoTemp.getUnidadeFuncional().getSeq()))){
				
				throw new ApplicationBusinessException(
						SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_ALTERACAO_SOLICITACAO_INTERNACAO);
				
			}
		}
	}

	/**
	 * ORADB Procedure AINP_ENFORCE_SIN_RULES
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void validarDadosAtualizacao(
			AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp)
			throws ApplicationBusinessException {
		AghParametros parametroJustificativaReserva = null;
		AghParametros parametroMotivoLeitoReservado = null;
		String justificativaReservaLeito = null;
		Integer codigoMovimento = null;
		if (solicitacaoInternacaoTemp.getLeito() == null
				&& solicitacaoInternacao.getLeito() != null) {
			parametroJustificativaReserva = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_JUSTIF_RESERVA_LEITO_SOLIC_INT);
			justificativaReservaLeito = parametroJustificativaReserva
					.getVlrTexto();
			parametroMotivoLeitoReservado = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_RESERVADO);
			codigoMovimento = parametroMotivoLeitoReservado.getVlrNumerico()
					.intValue();
		}
		this.insereRegistroReserva(solicitacaoInternacao,
				justificativaReservaLeito, codigoMovimento);
	}

	/**
	 * ORADB Procedure AINP_INS_EXTR_LTO
	 * 
	 */
	public void insereRegistroReserva(AinSolicitacoesInternacao solicitacaoInternacao, String justificativa, Integer codigoMovimento)
			throws ApplicationBusinessException {
		AinLeitos leito = null;
		AinQuartos quarto = null;
		AipPacientes paciente = null;
		if (solicitacaoInternacao.getLeito() != null) {
			leito = this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(solicitacaoInternacao.getLeito().getLeitoID());
		}
		List<AinQuartos> listaQuartos = null;
		if (leito != null && leito.getQuarto() != null && leito.getQuarto().getNumero() != null) {
			listaQuartos = this.getCadastrosBasicosInternacaoFacade().pesquisarQuartos(leito.getQuarto().getNumero().toString());
		}
		if (listaQuartos != null) {
			quarto = listaQuartos.get(0);
		}

		if (quarto != null && quarto.getIndConsSexo().equals(DominioSimNao.S) && quarto.getSexoOcupacao() == null) {
			paciente = this.getPacienteFacade().obterPaciente(solicitacaoInternacao.getPaciente().getCodigo());
			quarto.setSexoOcupacao(paciente.getSexo());
			try {
				getCadastrosBasicosInternacaoFacade().atualizarQuarto(quarto, true);
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new ApplicationBusinessException(
						SolicitacaoInternacaoRNExceptionCode.MENSAGEM_ERRO_PERSISTIR_SOLICITACAO_INTERNACAO);
			}
		}		
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
}
