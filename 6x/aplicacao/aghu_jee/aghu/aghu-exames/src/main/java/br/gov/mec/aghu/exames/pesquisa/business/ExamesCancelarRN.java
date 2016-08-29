package br.gov.mec.aghu.exames.pesquisa.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAmostra;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCartaColeta;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicCartasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMotivoCancelaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.PesquisaExameDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicCartas;
import br.gov.mec.aghu.model.AelItemSolicCartasId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ExamesCancelarRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ExamesCancelarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	@Inject
	private PesquisaExameDAO pesquisaExameDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

	@Inject
	private AelItemSolicCartasDAO aelItemSolicCartasDAO;

	@Inject
	private AelMotivoCancelaExamesDAO aelMotivoCancelaExamesDAO;

	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@Inject
	private AelAmostrasDAO aelAmostrasDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5709735025566163400L;

	/**
	 * ORADB aelc_ver_gera_carta
	 * @param aelItemSolicitacaoExames
	 * @return
	 */
	public Boolean verGeraCarta(
			AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		AelSolicitacaoExames aelSolicitacaoExames =  aelItemSolicitacaoExames.getSolicitacaoExame();
		if(aelSolicitacaoExames.getAtendimento() != null){
			
			AghAtendimentos atendimento = aelSolicitacaoExames.getAtendimento();
			if(atendimento.getOrigem().equals(DominioOrigemAtendimento.I) || atendimento.getOrigem().equals(DominioOrigemAtendimento.N) || atendimento.getOrigem().equals(DominioOrigemAtendimento.U)){
				if(atendimento.getIndPacAtendimento().equals(DominioPacAtendimento.N)){
					AipPacientes paciente = atendimento.getPaciente();
					if(paciente.getDtObito() == null){
						return Boolean.TRUE;
					}
				}
			}else{
				return Boolean.TRUE; 
			}
			
		}else if(aelSolicitacaoExames.getAtendimentoDiverso() != null){
			AelAtendimentoDiversos atendimentoDiverso = aelSolicitacaoExames.getAtendimentoDiverso();
			if(atendimentoDiverso != null){
				if(atendimentoDiverso.getOrigemAmostra().equals(DominioOrigemAmostra.H)){
					if(atendimentoDiverso.getAipPaciente() != null || atendimentoDiverso.getAelLaboratorioExternos() != null || 
							(atendimentoDiverso.getAbsCandidatosDoadores() != null && atendimentoDiverso.getAbsCandidatosDoadores().getSeq() != null)){
						return Boolean.TRUE; 
					}
				}
			}
			
		}
		
		return Boolean.FALSE;
	}
	
	/**
	 * ORADB aelp_gera_carta_canc
	 * @param aelItemSolicitacaoExames
	 *  
	 * @throws ApplicationBusinessException 
	 */
	public void geraCartaCanc(AelItemSolicitacaoExames aelItemSolicitacaoExames) throws ApplicationBusinessException {
		AelExamesMaterialAnalise examesMaterial = aelItemSolicitacaoExames.getAelUnfExecutaExames().getAelExamesMaterialAnalise();
		if(examesMaterial.getAelModeloCartas() != null){
			AelItemSolicCartas aelItemSolicCartas = new AelItemSolicCartas();
			aelItemSolicCartas.setId(new AelItemSolicCartasId());
			
			aelItemSolicCartas.getId().setIseSoeSeq(aelItemSolicitacaoExames.getId().getSoeSeq());
			aelItemSolicCartas.getId().setIseSeqp(aelItemSolicitacaoExames.getId().getSeqp());
			
			List<AelItemSolicCartas> itensCartas = getAelItemSolicCartasDAO().buscarAelItemSolicCartasPorAelItemSolicitacaoExames(aelItemSolicitacaoExames);
			Short seqp = 0;
			for(AelItemSolicCartas itens:itensCartas){
				if(itens.getId().getSeqp() > seqp){
					seqp = itens.getId().getSeqp();
				}
			}
			seqp++;
			
			aelItemSolicCartas.getId().setSeqp(seqp);
			aelItemSolicCartas.setSituacao(DominioSituacaoCartaColeta.AE);
			aelItemSolicCartas.setAelModeloCartas(examesMaterial.getAelModeloCartas());
			getExamesFacade().persistirItemSolicCartas(aelItemSolicCartas, true);
		}
	}
	

	
	protected AelItemSolicCartasDAO getAelItemSolicCartasDAO(){
		return aelItemSolicCartasDAO;
	}
	
	
	
	public void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException {
		cancelarInterfaceamento(aelItemSolicitacaoExames, nomeMicrocomputador);
		getSolicitacaoExameFacade().atualizar(aelItemSolicitacaoExames, nomeMicrocomputador);	
	
	}
	
	public PesquisaExamesPacientesResultsVO buscaDadosSolicitacaoPorSoeSeq(Integer soeSeq) throws BaseException {
		return getPesquisaExameDAO().buscaDadosSolicitacaoPorSoeSeq(soeSeq);
	}
	
	public List<PesquisaExamesPacientesResultsVO> buscaDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short unfSeq) throws BaseException {
		
		//parametro P_SITUACAO_EXECUTANDO
		AghParametrosVO pSituacaoExecutando = new AghParametrosVO();
		pSituacaoExecutando.setNome(AghuParametrosEnum.P_SITUACAO_EXECUTANDO.toString());
		getParametroFacade().getAghpParametro(pSituacaoExecutando);

		//parametro P_SITUACAO_EXECUTADO
		AghParametrosVO pSituacaoExecutado = new AghParametrosVO();
		pSituacaoExecutado.setNome(AghuParametrosEnum.P_SITUACAO_EXECUTADO.toString());
		getParametroFacade().getAghpParametro(pSituacaoExecutado);		
		
		//P_SITUACAO_NA_AREA_EXECUTORA
		AghParametrosVO pSituacaoNaAreaExecutora = new AghParametrosVO();
		pSituacaoNaAreaExecutora.setNome(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA.toString());
		getParametroFacade().getAghpParametro(pSituacaoNaAreaExecutora);

		List<PesquisaExamesPacientesResultsVO> itens = getPesquisaExameDAO().buscaDadosItensSolicitacaoPorSoeSeq(soeSeq, unfSeq);
		
		if(itens!=null){
			for (PesquisaExamesPacientesResultsVO item : itens) {

				//testa se pode cancelar
				if(item.getSituacaoCodigo().equals(pSituacaoExecutando.getVlrTexto()) 
						|| item.getSituacaoCodigo().equals(pSituacaoExecutado.getVlrTexto())
						|| item.getSituacaoCodigo().equals(pSituacaoNaAreaExecutora.getVlrTexto())){
					item.setPodeCancelar(true);
				}
				
				//testa se pode estornar
				if(item.getSituacaoCodigo().equals("CA")){
					item.setPodeEstornar(true);
				}
			}
		}
		
		return itens;
	}

	public List<PesquisaExamesPacientesResultsVO> buscaDadosItensSolicitacaoCancelarColetaPorSoeSeq(Integer soeSeq, Short unfSeq) throws BaseException {
		
		//parametro P_SITUACAO_COLETADO
		AghParametrosVO pSituacaoColetado = new AghParametrosVO();
		 pSituacaoColetado.setNome(AghuParametrosEnum.P_SITUACAO_COLETADO.toString());
		getParametroFacade().getAghpParametro(pSituacaoColetado);
	
		//parametro P_SITUACAO_EM_COLETA
		AghParametrosVO pSituacaoColetando = new AghParametrosVO();
		pSituacaoColetando.setNome(AghuParametrosEnum.P_SITUACAO_EM_COLETA.toString());
		getParametroFacade().getAghpParametro(pSituacaoColetando);		
	
		List<PesquisaExamesPacientesResultsVO> itens = getPesquisaExameDAO().buscaDadosItensSolicitacaoPorSoeSeq(soeSeq, unfSeq);
		
		if(itens!=null){
			for (PesquisaExamesPacientesResultsVO item : itens) {
	
				//testa se pode cancelar
				if(item.getSituacaoCodigo().equals(pSituacaoColetado.getVlrTexto()) 
						|| item.getSituacaoCodigo().equals(pSituacaoColetando.getVlrTexto())){
					item.setPodeCancelar(true);
				}
				
				//testa se pode estornar
				if(item.getSituacaoCodigo().equals("CA")){
					item.setPodeEstornar(true);
				}
			}
		}
		
		return itens;
	}
	
	private void cancelarInterfaceamento(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException{		
		List<AelAmostraItemExames> amostrasItemExame = aelAmostraItemExamesDAO.buscarAelAmostraItemExamesPorItemSolicitacaoExameIds(aelItemSolicitacaoExames.getId().getSoeSeq(),aelItemSolicitacaoExames.getId().getSeqp());
		if(amostrasItemExame != null && !amostrasItemExame.isEmpty()){
			for (AelAmostraItemExames itemAmostra: amostrasItemExame) {

				AelAmostras amostra = this.getAelAmostrasDAO()
						.obterPorChavePrimaria(
								itemAmostra.getAelAmostras().getId(), true,
								AelAmostras.Fields.SOLICITACAO_EXAME);

				//Cancela o interfaceamento
				getExamesFacade().verificarModoInterfaceamento(amostra, true, nomeMicrocomputador);
			}
		}
	}
	
	/**
	 * ORADB PROCEDURE AGHK_ATD_RN.RN_ATDP_CANC_EXME
	 * @param atendimento
	 * @param tipoAltaMedica
	 * @throws BaseException 
	 */
	public void cancelarExamesNaAlta(AghAtendimentos atendimento, AinTiposAltaMedica tipoAltaMedica, String nomeMicrocomputador) throws BaseException {

		BigDecimal seqMotivoCancelamento = null;
		Boolean urgenciaNaAlta = false;

		//parametro P_COD_TIPO_ALTA_OBITO
		AghParametrosVO tipoAltaObito = new AghParametrosVO();
		tipoAltaObito.setNome(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO.toString());
		getParametroFacade().getAghpParametro(tipoAltaObito);

		//parametro P_COD_TIPO_ALTA_OBITO_MAIS_48H
		AghParametrosVO tipoAltaObitoMaisHoras = new AghParametrosVO();
		tipoAltaObitoMaisHoras.setNome(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H.toString());
		getParametroFacade().getAghpParametro(tipoAltaObitoMaisHoras);		

		//P_COD_TIPO_ALTA_OBITO_GENERICO
		AghParametrosVO tipoAltaObitoGenerico = new AghParametrosVO();
		tipoAltaObitoGenerico.setNome(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_GENERICO.toString());
		getParametroFacade().getAghpParametro(tipoAltaObitoGenerico);
		
		AghParametrosVO parametroMotivoCancelamento = new AghParametrosVO();

		if (tipoAltaMedica.getCodigo().equals(tipoAltaObito.getVlrTexto())
				|| tipoAltaMedica.getCodigo().equals(tipoAltaObitoMaisHoras.getVlrTexto()) 
				|| tipoAltaMedica.getCodigo().equals(tipoAltaObitoGenerico.getVlrTexto())) {

			parametroMotivoCancelamento.setNome(AghuParametrosEnum.P_MOC_CANCELA_OBITO.toString());
			getParametroFacade().getAghpParametro(parametroMotivoCancelamento);
			seqMotivoCancelamento = parametroMotivoCancelamento.getVlrNumerico();
			
		} else {
			
			parametroMotivoCancelamento.setNome(AghuParametrosEnum.P_MOC_CANCELA_ALTA.toString());
			getParametroFacade().getAghpParametro(parametroMotivoCancelamento);
			seqMotivoCancelamento = parametroMotivoCancelamento.getVlrNumerico();
			
			if (atendimento.getOrigem().equals(DominioOrigemAtendimento.U)) {
				
				urgenciaNaAlta = true;
				
			}
			
		}
		
		if (seqMotivoCancelamento != null) {
			
			atualizarSituacaoItemSolicitacao(atendimento, seqMotivoCancelamento.shortValue(), urgenciaNaAlta, nomeMicrocomputador);
			
		}

	}
	
	/**
	 * Atualiza situação do item
	 * @param urgenciaNaAlta
	 * @throws BaseException 
	 */
	public void atualizarSituacaoItemSolicitacao(AghAtendimentos atendimento, Short seqMotivoCancelamento, Boolean urgenciaNaAlta, String nomeMicrocomputador) throws BaseException {
		
		List<String> codigos = new ArrayList<String>();
		List<AelItemSolicitacaoExames> itensSolicitacoes = new ArrayList<AelItemSolicitacaoExames>();			
		Short unfSeqAnatomia = null;

		//parametro P_SITUACAO_A_COLETAR
		AghParametrosVO pSituacaoColetar = new AghParametrosVO();
		pSituacaoColetar.setNome(AghuParametrosEnum.P_SITUACAO_A_COLETAR.toString());
		getParametroFacade().getAghpParametro(pSituacaoColetar);
		codigos.add(pSituacaoColetar.getVlrTexto());

		//parametro P_SITUACAO_A_EXECUTAR
		AghParametrosVO pSituacaoExecutar = new AghParametrosVO();
		pSituacaoExecutar.setNome(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR.toString());
		getParametroFacade().getAghpParametro(pSituacaoExecutar);
		codigos.add(pSituacaoExecutar.getVlrTexto());
		
		//P_SITUACAO_COLETADO_SOLIC
		AghParametrosVO pSituacaoColetado = new AghParametrosVO();
		pSituacaoColetado.setNome(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC.toString());
		getParametroFacade().getAghpParametro(pSituacaoColetado);
		codigos.add(pSituacaoColetado.getVlrTexto());
		
		//P_SITUACAO_AGENDADO
		AghParametrosVO pSituacaoAgendado = new AghParametrosVO();
		pSituacaoAgendado.setNome(AghuParametrosEnum.P_SITUACAO_AGENDADO.toString());
		getParametroFacade().getAghpParametro(pSituacaoAgendado);
		codigos.add(pSituacaoAgendado.getVlrTexto());
		
		//P_SITUACAO_EM_COLETA
		AghParametrosVO pSituacaoEmColeta = new AghParametrosVO();
		pSituacaoEmColeta.setNome(AghuParametrosEnum.P_SITUACAO_EM_COLETA.toString());
		getParametroFacade().getAghpParametro(pSituacaoEmColeta);
		codigos.add(pSituacaoEmColeta.getVlrTexto());
		
		//P_UNID_ANATOMIA
		AghParametrosVO undAnatomia = new AghParametrosVO();
		undAnatomia.setNome(AghuParametrosEnum.P_UNID_ANATOMIA.toString());
		getParametroFacade().getAghpParametro(undAnatomia);
		
		if (undAnatomia.getVlrNumerico() != null) {
			
			unfSeqAnatomia = undAnatomia.getVlrNumerico().shortValue();
			
		}
		
		itensSolicitacoes = getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorAtendimento(atendimento.getSeq(), unfSeqAnatomia, codigos);
		
		for (AelItemSolicitacaoExames aelItemSolicitacaoExames : itensSolicitacoes) {
			AelItemSolicitacaoExames original = new AelItemSolicitacaoExames();
			try {
				BeanUtils.copyProperties(original, aelItemSolicitacaoExames);
			} catch (IllegalAccessException | InvocationTargetException e) {
				LOG.error("Erro ao copiar itemSolicitacaoExame.", e);
			}
			
			AelSolicitacaoExames solicitacao = aelItemSolicitacaoExames.getSolicitacaoExame();
			Short unfSeq = solicitacao.getUnidadeFuncional().getSeq();
			
			if (!(urgenciaNaAlta && getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO).equals(DominioSimNao.S))) {
				
				//P_SITUACAO_CANCELADO
				AghParametrosVO pSituacaoCancelado = new AghParametrosVO();
				pSituacaoCancelado.setNome(AghuParametrosEnum.P_SITUACAO_CANCELADO.toString());
				getParametroFacade().getAghpParametro(pSituacaoCancelado);
				
				AelSitItemSolicitacoes situacaoCancelado = this.getAelSitItemSolicitacoesDAO().obterPeloId(pSituacaoCancelado.getVlrTexto());
				AelMotivoCancelaExames motivo = this.getAelMotivoCancelaExamesDAO().obterPeloId(seqMotivoCancelamento);
				aelItemSolicitacaoExames.setAelMotivoCancelaExames(motivo);
				aelItemSolicitacaoExames.setSituacaoItemSolicitacao(situacaoCancelado);
				
				getSolicitacaoExameFacade().atualizar(aelItemSolicitacaoExames, original, nomeMicrocomputador);
				
			}
			
		}
		
	}
	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExames(Object param) {
		return getAelMotivoCancelaExamesDAO().pesquisarMotivoCancelaExames(param);
	}

	public Long pesquisarMotivoCancelaExamesCount(Object param) {
		return getAelMotivoCancelaExamesDAO().pesquisarMotivoCancelaExamesCount(param);
	}

	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExamesColeta(Object param) {
		return getAelMotivoCancelaExamesDAO().pesquisarMotivoCancelaExamesColeta(param);
	}

	protected PesquisaExameDAO getPesquisaExameDAO(){
		return pesquisaExameDAO;
	}

	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelMotivoCancelaExamesDAO getAelMotivoCancelaExamesDAO() {
		return aelMotivoCancelaExamesDAO;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}
}