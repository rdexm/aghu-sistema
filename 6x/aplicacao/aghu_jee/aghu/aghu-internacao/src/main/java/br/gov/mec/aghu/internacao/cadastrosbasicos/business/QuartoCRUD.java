package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinCaracteristicaLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosJnDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinTipoCaracteristicaLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinCaracteristicaLeito;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinLeitosJn;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class QuartoCRUD extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	private static final Log LOG = LogFactory.getLog(QuartoCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;

	@Inject
	private AinCaracteristicaLeitoDAO ainCaracteristicaLeitoDAO;
	
	@EJB
	private IControleInfeccaoFacade iControleInfeccaoFacade;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@Inject
	private AinLeitosJnDAO ainLeitosJnDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	
	@EJB
	private IPacienteFacade iPacienteFacade;
	
	@Inject
	private AinTipoCaracteristicaLeitoDAO ainTipoCaracteristicaLeitoDAO;
	
	@EJB
	private IParametroFacade iParametroFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;
	
	@EJB
	private ILeitosInternacaoFacade iLeitosInternacaoFacade;
	
	@EJB
	private IInternacaoFacade iInternacaoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6207573367062984029L;

	private enum QuartoCRUDExceptionCode implements BusinessExceptionCode {
		NUMERO_QUARTO_INVALIDO,ERRO_PERSISTIR_QUARTO, DESCRICAO_QUARTO_EXISTENTE, 
		CARACTERISTICA_PRINCIPAL_DUPLICADA, LEITO_EXISTENTE, QUARTO_SEM_LEITO,
		ERRO_VALIDAR_LEITO, ERRO_OBRIGATORIO_INFORMAR_CARACTERISTICA_PRINCIPAL,
		CONFIGURACAO_DOS_ANDARES_NAO_DEFINIDA,AIN00815,AIN00817
	}

	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected ILeitosInternacaoFacade getLeitosInternacaoFacade() {
		return this.iLeitosInternacaoFacade;
	}

	/**
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param parametro
	 * @return
	 */
	// Nao pode ser chamado diretamente da view! 
	// #{quartoController.quartoCRUD.pesquisarTiposCaracteristicasPorCodigoOuDescricao}
	// Nao encotrei a chamada acima em nenhum xhtml.
	public List<AinTipoCaracteristicaLeito> pesquisarTiposCaracteristicasPorCodigoOuDescricao(Object parametro) {		
		//return cadastrosBasicosInternacaoFacade.pesquisarTipoCaracteristicaPorCodigoOuDescricao(parametro == null ? null : parametro.toString());
		String p = parametro == null ? null : parametro.toString();
		return  getAinTipoCaracteristicaLeitoDAO().pesquisarTipoCaracteristicaPorCodigoOuDescricao(p);
	}

	/**
	 * 
	 * @dbtables AghEspecialidades select
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadeSiglaEDescricao(Object parametro){
		return this.getAghuFacade().listarEspecialidadeAtivasNaoInternas(parametro == null ? null
				: parametro.toString());
	}
	
	@Secure("#{s:hasPermission('quarto','alterar')}")
	public void persistir(AinQuartos quarto,  List<AinLeitosVO> leitosQuarto) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {			
			// Issue #15349 do redmine
			if(quarto.getUnidadeFuncional() != null) {
				AghUnidadesFuncionais unidade = getAghuFacade().obterUnidadeFuncional(quarto.getUnidadeFuncional().getSeq());
				quarto.setAla(unidade.getIndAla());
			}
			validarPossuiLeito(quarto, leitosQuarto);

			List<AinLeitos> leitosExtrato = new ArrayList<AinLeitos>(0);
			
			boolean atualizarUnidadeFuncional = false;
			AghUnidadesFuncionais unidadeFuncionalAtualQuarto = obterUnidadeFuncionalQuarto(quarto.getNumero());
			if(quarto.getUnidadeFuncional() != null) {
				if(unidadeFuncionalAtualQuarto == null || !unidadeFuncionalAtualQuarto.getSeq().equals(quarto.getUnidadeFuncional().getSeq())) {
					atualizarUnidadeFuncional = true;
				}
			}
			if(quarto.getNumero() == null){
				ainQuartosDAO.persistir(quarto);
			} else {
				ainQuartosDAO.atualizar(quarto);
			}
			ainQuartosDAO.flush();
			
			ainQuartosDAO.desatachar(quarto);
			
			if(leitosQuarto != null && !leitosQuarto.isEmpty()){
				persistirLeitos(quarto, leitosQuarto, servidorLogado, leitosExtrato, atualizarUnidadeFuncional);
			}
			
			quarto = getAinQuartosDAO().obterPorChavePrimaria(quarto.getNumero());
			for(AinLeitos leito : leitosExtrato) {
				int index = quarto.getAinLeitos().indexOf(leito);
				if(index != -1) {
					AinLeitos leitoExtrato = quarto.getAinLeitos().get(index);
					getLeitosInternacaoFacade().inserirExtrato(leitoExtrato, leitoExtrato.getTipoMovimentoLeito(), null, null, null, null, null, null, null, null, null, null);
				}
			}
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw e;
			
		} catch (BaseRuntimeException e) {
			LOG.error("Exceção BaseRuntimeException capturada, lançada para cima.");
			throw e;
		} catch (Exception e) {
			LOG.error("Erro ao incluir/atualizar o quarto.", e);
			throw new ApplicationBusinessException(QuartoCRUDExceptionCode.ERRO_PERSISTIR_QUARTO);
		}
	}

	private void persistirLeitos(AinQuartos quarto, List<AinLeitosVO> leitosQuarto,
			RapServidores servidorLogado, List<AinLeitos> leitosExtrato,
			boolean atualizarUnidadeFuncional) throws ApplicationBusinessException {
		
		for(AinLeitosVO leitoVO : leitosQuarto) {
			AinLeitos leito = leitoVO.getAinLeito();
			
			// Não esta no VO, não foi alterado/inserido.
			if(leito == null){
				continue;
			}
			
			leito.setServidor(servidorLogado);
			
			if(leito.getLeitoID() == null) {
				leito.setLeitoID(quarto.getDescricao() + leito.getLeito());
				leito.setQuarto(quarto);
				//AINT_LTO_BRI
				if(leito.isSituacao()) {
					Short parametro = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
					leito.setTipoMovimentoLeito(obterTipoMvtLeito(parametro));
				} else { 
					Short parametro = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_MVTO_LTO_DESATIVADO);
					leito.setTipoMovimentoLeito(obterTipoMvtLeito(parametro));					
				}
				
				leito.setUnidadeFuncional(quarto.getUnidadeFuncional());
	
				leitosExtrato.add(leito);
				
				ainLeitosDAO.persistir(leito);
				
			} else {
				AinLeitos leitoOriginal = ainLeitosDAO.obterOriginal(leito);
				validaLeitoMedidaPreventiva(quarto.getNumero(), leito.getTipoMovimentoLeito().getCodigo());
				
				if(atualizarUnidadeFuncional) {
					leito.setUnidadeFuncional(quarto.getUnidadeFuncional());
				}
				if(leito.isSituacao()) {
					if(!leito.getIndSituacao().isAtivo()) {
						Short vl = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
						leito.setTipoMovimentoLeito(obterTipoMvtLeito(vl));
						if(!Short.valueOf("0").equals(vl)){
							leitosExtrato.add(leito);
						}
					}
				}
				
				ainLeitosDAO.atualizar(leito);
				posAtualizarLeito(leito, leitoOriginal);
			}
			
			for(AinCaracteristicaLeito crt : leitoVO.getCaracteristicasExcluidas()) {
				AinCaracteristicaLeito caracteristica = ainCaracteristicaLeitoDAO.obterPorChavePrimaria(crt.getId());
				
				if(caracteristica != null){
					ainCaracteristicaLeitoDAO.remover(caracteristica);
				}
			}
			
			ainCaracteristicaLeitoDAO.flush();
			
			for(AinCaracteristicaLeito caracteristica : leitoVO.getCaracteristicasInseridas()) {
				caracteristica.getId().setLtoLtoId(leito.getLeitoID());
				ainCaracteristicaLeitoDAO.persistir(caracteristica);
			}
		}
	}
	
	private void posAtualizarLeito(AinLeitos leito, AinLeitos leitoOriginal) throws ApplicationBusinessException {
			if( CoreUtil.modificados(leito.getLeito(), leitoOriginal.getLeito())
					|| CoreUtil.modificados(leito.getEspecialidade(), leitoOriginal.getEspecialidade())
					|| CoreUtil.modificados(leito.getQuarto(), leitoOriginal.getQuarto())
					|| CoreUtil.modificados(leito.getIndConsEsp(), leitoOriginal.getIndConsEsp())
					|| CoreUtil.modificados(leito.getIndConsClinUnidade(), leitoOriginal.getIndConsClinUnidade())
					|| CoreUtil.modificados(leito.getIndBloqLeitoLimpeza(), leitoOriginal.getIndBloqLeitoLimpeza())
					|| CoreUtil.modificados(leito.getUnidadeFuncional(), leitoOriginal.getUnidadeFuncional())
					|| CoreUtil.modificados(leito.getIndAcompanhamentoCcih(), leitoOriginal.getIndAcompanhamentoCcih())
					|| CoreUtil.modificados(leito.getIndSituacao(), leitoOriginal.getIndSituacao())) {
				
				AinLeitosJn ainLeitosJn = criarJournalLeito(leitoOriginal, DominioOperacoesJournal.UPD);
				getAinLeitosJnDAO().persistir(ainLeitosJn);
		}
	}
	
	private AinLeitosJn criarJournalLeito(AinLeitos leito, DominioOperacoesJournal dominio) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AinLeitosJn AinLeitosJn = BaseJournalFactory.getBaseJournal(dominio, AinLeitosJn.class, servidorLogado.getUsuario());

		AinLeitosJn.setLeitoId(leito.getLeitoID());
		AinLeitosJn.setNomeUsuario(servidorLogado.getUsuario());
		AinLeitosJn.setSerMatricula(servidorLogado.getId().getMatricula());
		AinLeitosJn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		AinLeitosJn.setLeito(leito.getLeito());
		validaAinLeitosJnEspecialidade(leito, AinLeitosJn);
		validaAinLeitosJnNumeroQuarto(leito, AinLeitosJn);  
		AinLeitosJn.setIndConsEsp(leito.getIndConsEsp());
		AinLeitosJn.setIndPertenceRefl(leito.getIndConsEsp());
		AinLeitosJn.setIndConsClinUnidade(leito.getIndConsClinUnidade());
		AinLeitosJn.setIndBloqLeitoLimpeza(leito.getIndBloqLeitoLimpeza());
		AinLeitosJn.setTipoMovimentoLeito(leito.getTipoMovimentoLeito().getCodigo());
		AinLeitosJn.setUnidadeFuncional(leito.getUnidadeFuncional().getSeq());
		AinLeitosJn.setIndAcompanhamentoCcih(leito.getIndAcompanhamentoCcih());
		AinLeitosJn.setIndSituacao(leito.getIndSituacao());
		return AinLeitosJn;
	}
	
	private void validaAinLeitosJnEspecialidade(AinLeitos leito,AinLeitosJn AinLeitosJn) throws ApplicationBusinessException {
        if (leito.getEspecialidade() != null) {
            AinLeitosJn.setEspSeq(leito.getEspecialidade().getSeq());    
        }
    }
	
	private void validaAinLeitosJnNumeroQuarto(AinLeitos leito,AinLeitosJn AinLeitosJn) throws ApplicationBusinessException {
        if (leito.getQuarto() != null) {
            AinLeitosJn.setNumeroQuarto(leito.getQuarto().getNumero());    
        }
    }

	//@ORADB : aint_lto_bru
	/**
	 * 
	 * @dbtables AghParametros select 
	 * @dbtables AinLeitos select,insert,update
	 * @dbtables AinQuartos select,insert,update
	 * @dbtables AinExtratoLeitos select,insert,update
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinExtratoLeitos select
	 * @dbtables RapServidores select
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('quarto','validarAtualizacaoLeito')}")
	public void validarAtualizacaoLeito(AinLeitos leito) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			validaLeitoMedidaPreventiva(leito.getQuarto().getNumero(), leito.getTipoMovimentoLeito().getCodigo());
			
			leito.setServidor(servidorLogado);
			
			DominioSituacao situacaoLeito = obterSituacaoLeito(leito.getLeitoID());
			if(leito.isSituacao()) {
				if(!situacaoLeito.isAtivo()) {
					AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
					leito.setTipoMovimentoLeito(obterTipoMvtLeito(parametro.getVlrNumerico().shortValue()));
				}
			}
	
			//AINP_ENFORCE_LTO_RULES
			// if p_event = 'UPDATE' then
//		    if (l_lto_saved_row.ind_situacao = 'I' and l_lto_row_new.ind_situacao = 'A'
//		    	and	l_lto_row_new.tml_codigo != 0)
			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_OCUPADO);
			if ((situacaoLeito == DominioSituacao.I && leito.getIndSituacao() == DominioSituacao.I)
					&&	!leito.getTipoMovimentoLeito().equals(obterTipoMvtLeito(parametro.getVlrNumerico().shortValue()))) {
			getLeitosInternacaoFacade().inserirExtrato(leito, leito.getTipoMovimentoLeito(), null, null, null, null, null, null, null, null, null, null);
			}
		} catch(Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					QuartoCRUDExceptionCode.ERRO_VALIDAR_LEITO);			
		}
	}
	
	/**
	 * @dbtables AinQuartos select
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param numero
	 * @return
	 */
	//@ORADB: RN_LTOP_VER_MED_PREV 
	public void validaLeitoMedidaPreventiva(Short numero, Short codigoMvtLeito) throws ApplicationBusinessException {
		Short parametro = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_MVTO_LTO_INFECCAO);
		
		if (codigoMvtLeito.equals(parametro)) {
			
			String paramValidaBloqLeitoInfeccao = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_VALIDAR_BLOQUEIO_LEITO_INFECCAO);
			if("S".equalsIgnoreCase(paramValidaBloqLeitoInfeccao)){			
				List<Integer> resultadoPesquisa = getPacienteFacade().pesquisarCodigoPacientesPorNumeroQuarto(numero);
				Integer codigoPaciente = null;
				
				if (resultadoPesquisa.size() > 0) {
					codigoPaciente = resultadoPesquisa.get(0);
				}
				
				if (codigoPaciente == null) {
					throw new ApplicationBusinessException(QuartoCRUDExceptionCode.AIN00817);			
				}
				
				List<Integer> resultadoPesquisaMco = 
					getControleInfeccaoFacade().pesquisarSequencialFatorPredisponentePorCodigoPaciente(codigoPaciente);
				
				List<Integer> resultado = 
						getControleInfeccaoFacade().pesquisarSequencialFatorPredisponentePorCodigoPaciente(codigoPaciente);			
		
				resultadoPesquisaMco.addAll(resultado);
				
				if(resultadoPesquisaMco.size() == 0) {
					throw new ApplicationBusinessException(QuartoCRUDExceptionCode.AIN00815);								
				}
			}
		}
	}

	
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public AghUnidadesFuncionais obterUnidadeFuncionalQuarto(Short numero) {
		return getAghuFacade().obterUnidadeFuncionalQuarto(numero);
	}

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param leitoID
	 * @return
	 */
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public DominioSituacao obterSituacaoLeito(String leitoID) {
		return getAinLeitosDAO().obterSituacaoLeito(leitoID);
	} 
	
	public void validaCaracteristicaPrincipal(List<AinCaracteristicaLeito> caracteristicas) throws ApplicationBusinessException {
		boolean principal = false;
		for(AinCaracteristicaLeito carac : caracteristicas) {
			if(carac.isPrincipal()) {
				if(principal) {
					throw new ApplicationBusinessException(QuartoCRUDExceptionCode.CARACTERISTICA_PRINCIPAL_DUPLICADA);
				}
				principal = true;				
			}
		}

		if (caracteristicas.size() > 0 && !principal) {
			throw new ApplicationBusinessException(QuartoCRUDExceptionCode.ERRO_OBRIGATORIO_INFORMAR_CARACTERISTICA_PRINCIPAL);
		}
	}

	/**
	 * 
	 * @dbtables AinLeitos select 
	 * 
	 * @param quarto
	 * @throws ApplicationBusinessException
	 */
	public void validarPossuiLeito(AinQuartos quarto,  List<AinLeitosVO> leitosQuarto) throws ApplicationBusinessException {
		
		// Quando vier da tela de edição de Quartos/Leitos
		if(leitosQuarto != null){
			if(leitosQuarto.isEmpty()){
				throw new ApplicationBusinessException(QuartoCRUDExceptionCode.QUARTO_SEM_LEITO);
				
			// Quando outras ON/RN's chamarem
			} else {
				return;
			}
		}
		
		if(quarto.getAinLeitos() == null || quarto.getAinLeitos().isEmpty()) {
			throw new ApplicationBusinessException(QuartoCRUDExceptionCode.QUARTO_SEM_LEITO);
		}		
	}
	
	/**
	 * 
	 * @dbtables AinLeitos select 
	 * 
	 * @param idLeito
	 * @param quarto
	 * @throws ApplicationBusinessException
	 */
	public void validarLeitoExistente(String idLeito, AinQuartos quarto) throws ApplicationBusinessException {
		List<AinLeitos> listaLeitos = this.getAinLeitosDAO().pesquisaAinLeitosPorNroQuarto(quarto.getNumero());
		for(AinLeitos leito : listaLeitos){
			if(leito.getLeitoID().equalsIgnoreCase(idLeito)) {
				throw new ApplicationBusinessException(QuartoCRUDExceptionCode.LEITO_EXISTENTE);
			}
		}
	}

	/**
	 * 
	 * @dbtables AinQuartos select
	 * 
	 * @param ainQuartosCodigo
	 * @throws ApplicationBusinessException
	 */
	public void validarQuartoExistente(String descricao) throws ApplicationBusinessException {
		AinQuartos quarto = obterQuartoDescricao(descricao);
		if(quarto != null) {
			throw new ApplicationBusinessException(QuartoCRUDExceptionCode.DESCRICAO_QUARTO_EXISTENTE);
		}
	}

	public void desatacharQuarto(AinQuartos quarto) {
	    this.getAinQuartosDAO().desatachar(quarto);
	}
	
	/**
	 * 
	 * @dbtable AinQuartos select
	 * 
	 * @param ainQuartosCodigo
	 * @return
	 */

	public AinQuartos obterQuarto(Short ainQuartosCodigo) {
		return getAinQuartosDAO().obterPorChavePrimaria(ainQuartosCodigo,AinQuartos.Fields.UNIDADES_FUNCIONAIS);
	}
	
	
	public AinQuartos obterQuartoDescricao(String descricao){
		return getAinQuartosDAO().obterQuartoDescricao(descricao);
	}
	
	/**
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param ainTiposMovimentoLeitoCodigo
	 * @return
	 */
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public AinTiposMovimentoLeito obterTipoMvtLeito(Short ainTiposMovimentoLeitoCodigo) {
		return getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(ainTiposMovimentoLeitoCodigo);
	}
	

	
	
	/**
	 * Método para obter a descrição completa do quarto (numero, andar, ala,
	 * descrição da unidade funcional)
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * @dbtables AinQuartos select
	 * 
	 * @param numeroQuarto
	 * @return Descrição completa do quarto
	 */
	public String obterDescricaoCompletaQuarto(Short numeroQuarto) {
		
		if (numeroQuarto == null) {
			return "";
		} else {
			AinQuartos quarto = this.obterQuarto(numeroQuarto);
			if (quarto.getNumero() == null) {
				return null;
			} else {
				StringBuffer descricao = new StringBuffer();
				descricao.append(quarto.getDescricao()).append("  /  ")
				.append(quarto.getUnidadeFuncional().getAndar());

				if (quarto.getUnidadeFuncional().getIndAla() != null) {
					descricao.append(' ').append(quarto.getUnidadeFuncional().getIndAla());
				}

				descricao.append(_HIFEN_).append(quarto.getUnidadeFuncional().getDescricao());

				return descricao.toString();
			}
		}
	}

	
	public List<AinQuartos> pesquisarQuartoSolicitacaoInternacao(String strPesquisa, DominioSexo sexoPaciente) {
		return getAinQuartosDAO().pesquisarQuartoSolicitacaoInternacao(strPesquisa, sexoPaciente);
	}

	@Secure("#{s:hasPermission('quarto','alterar')}")
	public AinQuartos mergeAinQuartos(AinQuartos quarto) {
		AinQuartosDAO ainQuartosDAO = this.getAinQuartosDAO();
		return ainQuartosDAO.atualizar(quarto);
	}
	
	protected AinTipoCaracteristicaLeitoDAO getAinTipoCaracteristicaLeitoDAO() {
		return ainTipoCaracteristicaLeitoDAO;
	}

	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}
	
	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}
	
	protected AinLeitosJnDAO getAinLeitosJnDAO() {
		return ainLeitosJnDAO;
	}
	
	protected AghUnidadesFuncionaisDAO getAghUnidadesFuncionaisDAO() {
		return aghUnidadesFuncionaisDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO() {
		return ainTiposMovimentoLeitoDAO;
	}
	
	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return this.iControleInfeccaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	protected IInternacaoFacade getIInternacaoFacade() {
		return this.iInternacaoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AinCaracteristicaLeitoDAO getAinCaracteristicaLeitoDAO() {
		return ainCaracteristicaLeitoDAO;
	}
}
