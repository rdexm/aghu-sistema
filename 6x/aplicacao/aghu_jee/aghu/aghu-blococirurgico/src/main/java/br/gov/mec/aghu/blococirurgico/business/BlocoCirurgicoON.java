package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaComPacEmTransOperatorioVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaMateriaisConsumidosVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.EspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.RequerenteVO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.blococirurgico.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@Stateless
public class BlocoCirurgicoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(BlocoCirurgicoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;

	@Inject 
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IAdministracaoFacade iAdministracaoFacade;

	@EJB
	private IBlocoCirurgicoOpmesFacade iBlocoCirurgicoOpmesFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1134948931921935499L;
	
	private enum BlocoCirurgicoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VALIDACAO_ENTRE_DATAS_DT_INI, 
		MENSAGEM_VALIDACAO_ENTRE_DATAS_DT_FIM, 
		MENSAGEM_VALIDACAO_ENTRE_DATAS_PARAM_DATAS, 
		MENSAGEM_VALIDACAO_ENTRE_DATAS_INTERVALO_DT_INVALIDO
	}

	private MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	private MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO(){
		return mbcExtratoCirurgiaDAO;
	}
	
	private IFaturamentoFacade getFaturamentoFacade(){
		return this.iFaturamentoFacade;
	}
	

	public void atualizarCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// TODO verificar triggers (que não estão implementadas) quando ocorrer
		// o deschaveamento da tabela
		getMbcCirurgiasDAO().flush();
	}

	/**
	 * Método pár buscar cirurgias por ID de atendimento.
	 * 
	 * @param seqAtendimento
	 * @param dataFimCirurgia
	 * @return
	 */
	public List<MbcCirurgias> pesquisarCirurgiasPorAtendimento(Integer seqAtendimento,
			Date dataFimCirurgia) {
		return getMbcCirurgiasDAO().pesquisarCirurgiasPorAtendimento(seqAtendimento,
				dataFimCirurgia);
	}
	
	/**
	 * ORADB: MAMC_INT_HAB_EVO
	 */
	@SuppressWarnings("ucd")
	public boolean habilitaUsuarioConectadoAhUnidade(final Integer pIdentificao, final Integer atdSeq) {
		final IAghuFacade aghuFacade = getAghuFacade();
		
		// Migrou-se apenas  ELSIF p_identificacao = 1, pois demais código considerou-se utiliza-vel apenas para teste
		final AghAtendimentos atd = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
		
		if(atd != null && atd.getUnidadeFuncional() != null){
			return aghuFacade.verificarCaracteristicaUnidadeFuncional(atd.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.ANAMNESE_EVOLUCAO_ELETRONICA);
			
		} else {
			return false;
		}
	}
	
	/**
	 * Segue as regras:
	 * 1) Verificar se a unidade possui a caracteristica "Unidade Executora de Cirurgias".
	 * 1.1) Se sim, trazer as cirurgias desta unidade.
	 * 1.2) Se não, verificar se esta unidade tem unidade Pai e se o pai tem caracteristica "Unidade Executora de Cirurgias". 
	 * 1.1.1) Se sim, trazer as cirurgias da unidade Pai.
	 * 1.1.2) Se não, não trazer nada.
	 * @param nomeMicrocomputador
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AghUnidadesFuncionais obterUnidadeFuncionalCirurgia(final String nomeMicrocomputador) throws ApplicationBusinessException {
		final AghMicrocomputador micro = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIP(nomeMicrocomputador, null);
		
		//1.1.2
		if(micro == null || micro.getAghUnidadesFuncionais() == null ){
			return null;
		}
		
		//1.1
		if (getAghuFacade().verificarCaracteristicaUnidadeFuncional(micro.getAghUnidadesFuncionais().getSeq(),
						ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS)) {
			return micro.getAghUnidadesFuncionais();
		} 
		
		//1.2
		List<AghUnidadesFuncionais> listUnidadesFuncionaisPai = getAghuFacade().pesquisarUnidadesPaiPorDescricao(micro.getAghUnidadesFuncionais().getSeq());
		if(listUnidadesFuncionaisPai != null && listUnidadesFuncionaisPai.size() > 0){
			AghUnidadesFuncionais unidadePai = listUnidadesFuncionaisPai.get(0).getUnfSeq();
			if(unidadePai != null && unidadePai.getSeq() != null && 
					getAghuFacade().verificarCaracteristicaUnidadeFuncional(unidadePai.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS)){
				return getAghuFacade().obterUnidadeFuncional(unidadePai.getSeq());
				
			}
		}
		
		//1.1.2
		return null;
	}
	
	
	private IAghuFacade getAghuFacade() {
		return  iAghuFacade;
	}
	
	private IAdministracaoFacade getAdministracaoFacade(){
		return iAdministracaoFacade;
	}

	public List<CirurgiaComPacEmTransOperatorioVO> pesquisarCirurgiasComPacientesEmTransOperatorio(
			DominioSituacaoCirurgia situacaoCirurgia,
			DominioOrigemPacienteCirurgia origemPacienteCirurgia,
			Boolean atendimentoIsNull,
			List<CirurgiaComPacEmTransOperatorioVO> cirurgiasComPacientesEmTransOperatorio,
			AghUnidadesFuncionais unidadeFuncional) {
		Short seqUnfSeq = unidadeFuncional != null ? unidadeFuncional.getSeq() : null;
		List<MbcCirurgias> cirurgias = getMbcCirurgiasDAO()
		.pesquisarMbcCirurgiasComSalaCirurgia(null, seqUnfSeq, null,
				situacaoCirurgia, Boolean.TRUE, origemPacienteCirurgia,
				atendimentoIsNull, null, Boolean.TRUE, null, MbcCirurgias.Fields.NOME.toString());
		if(cirurgias.isEmpty()){
			return null;
		}
		
		List<CirurgiaComPacEmTransOperatorioVO> listaCirgVo = new ArrayList<CirurgiaComPacEmTransOperatorioVO>();
		for(MbcCirurgias cirg : cirurgias){
			CirurgiaComPacEmTransOperatorioVO cirgVo = new CirurgiaComPacEmTransOperatorioVO();
			cirgVo.setCodPaciente(cirg.getPaciente().getCodigo());
			cirgVo.setCrgSeq(cirg.getSeq());
			cirgVo.setNomePaciente(cirg.getPaciente().getNome());
			cirgVo.setProntuario(cirg.getPaciente().getProntuario());
			if(cirurgiasComPacientesEmTransOperatorio != null && cirurgiasComPacientesEmTransOperatorio.contains(cirgVo)){
				Boolean sitPanel = cirurgiasComPacientesEmTransOperatorio.get(cirurgiasComPacientesEmTransOperatorio.indexOf(cirgVo)).getPanelAberto();
				cirgVo.setPanelAberto(sitPanel);
			}else{
				cirgVo.setPanelAberto(Boolean.FALSE);
			}
			
			List<MbcExtratoCirurgia> list = this.getMbcExtratoCirurgiaDAO().pesquisarMbcExtratoCirurgiaPorCirurgiaSituacao(cirg.getSeq(), DominioSituacaoCirurgia.TRAN);
			if(list!= null && list.size() > 0){
				cirgVo.setDataExtratoTransOperatorio(list.get(list.size() -1 ).getCriadoEm());
			}
			
			listaCirgVo.add(cirgVo);
		}
		
		return listaCirgVo;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	/**
	 * Método responsável por validar datas, 
	 * data inicial tem que ser menor ou igual a data atual,
	 * e data final tem que ser igual ou maior que a data inicial,
	 * o período entre as datas deverá ser menor igual ao parâmetro periodoEntreDatas
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param periodoEntreDatas
	 * @param negocioExceptionCode
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void validarIntervaldoEntreDatas(final Date dataInicial, final Date dataFinal, AghuParametrosEnum paramPeriodoEntreDatas) throws ApplicationBusinessException {
		
		if (DateValidator.validaDataMaiorQueAtual(dataInicial)) {
			throw new ApplicationBusinessException(BlocoCirurgicoONExceptionCode.MENSAGEM_VALIDACAO_ENTRE_DATAS_DT_INI);
		} else if (!DateUtil.validaDataMaiorIgual(dataFinal, dataInicial)) {
			throw new ApplicationBusinessException(BlocoCirurgicoONExceptionCode.MENSAGEM_VALIDACAO_ENTRE_DATAS_DT_FIM);
		} else {
			AghParametros parametroIntervaloEntreDatas = getParametroFacade().buscarAghParametro(paramPeriodoEntreDatas);
			
			if(parametroIntervaloEntreDatas != null &&  parametroIntervaloEntreDatas.getVlrNumerico().intValue() > 0){
				
				if(!(DateUtil.calcularDiasEntreDatasComPrecisao(dataInicial, dataFinal, 10).compareTo(parametroIntervaloEntreDatas.getVlrNumerico()) <= 0)){	
					throw new ApplicationBusinessException(BlocoCirurgicoONExceptionCode.MENSAGEM_VALIDACAO_ENTRE_DATAS_INTERVALO_DT_INVALIDO,
							parametroIntervaloEntreDatas.getVlrNumerico().intValue());
				}
			}else{
				throw new ApplicationBusinessException(
						BlocoCirurgicoONExceptionCode.MENSAGEM_VALIDACAO_ENTRE_DATAS_PARAM_DATAS);
			}
		}
	}
	
	
	public List<AghCid> pesquisarCidsPorPciSeq(Integer pciSeq, String descricao, DominioTipoPlano plano,
			String filtro) {
		Set<FatProcedHospIntCid> listaCids = new HashSet<FatProcedHospIntCid>();
		Set<AghCid> cids = new HashSet<AghCid>();
		
		List<FatProcedHospInternos> listaProcedHospitalares = getFaturamentoFacade()
					.listarFatProcedHospInternosPorProcedimentoCirurgicos(pciSeq, descricao);
		for (FatProcedHospInternos fatProcedHospInternos : listaProcedHospitalares) {
			Integer phiSeq = fatProcedHospInternos.getSeq();
			listaCids.addAll(getFaturamentoFacade().listarFatProcedHospIntCidPorPhiSeqValidade(phiSeq, plano, filtro));
		}

		for (FatProcedHospIntCid procedHospIntCid : listaCids) {
			cids.add(procedHospIntCid.getCid());
		}
		List<AghCid> listRetorno = new ArrayList<AghCid>();
		listRetorno.addAll(cids);
		return listRetorno;

	}
	
	public List<DescricaoCirurgicaMateriaisConsumidosVO> pesquisarMateriaisConsumidos(Integer cirurgiaSeq){
		
		List<Object[]> result = getMbcCirurgiasDAO().pesquisarMateriaisConsumidos(cirurgiaSeq);
		List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos = new ArrayList<DescricaoCirurgicaMateriaisConsumidosVO>(); 
		
		
		for (Object[] item : result) {
			
			DescricaoCirurgicaMateriaisConsumidosVO vo = new DescricaoCirurgicaMateriaisConsumidosVO();
			if (item[0] != null) {
				vo.setSeqItemReq(Short.parseShort(item[0].toString()));
			}
			if (item[1] != null) {
				vo.setSeqRequisicaoOpme(Short.parseShort(item[1].toString()));
			}
			if (item[2] != null) {
				vo.setItemSus(item[2].toString());
			}
			if (item[3] != null) {
				vo.setQtdeAutorizada(Integer.parseInt(item[3].toString()));
			}
			if (item[4] != null) {
				vo.setQtdeDispensada(Integer.parseInt(item[4].toString()));
			}
			if (item[5] != null) {
				vo.setMaterial(item[5].toString());
			}
			vo.setQtdeUtilizada(0);
			vo.setIncompativel(Boolean.FALSE);
			
			listaMateriaisConsumidos.add(vo);
		}
		
		return listaMateriaisConsumidos;
	}
	
	public String montarJustificativaMateriaisConsumidos(Short seqRequisicaoOpme){
		
		if(seqRequisicaoOpme != null){
			StringBuilder justificativa = new StringBuilder();
			
			MbcRequisicaoOpmes requisicao = this.iBlocoCirurgicoOpmesFacade.obterDetalhesRequisicao(seqRequisicaoOpme);
			List<MbcItensRequisicaoOpmes> itensRequisicao = requisicao.getItensRequisicao();
			
			for (MbcItensRequisicaoOpmes itemReq : itensRequisicao) {
				MbcItensRequisicaoOpmes itemRequisicao = this.iBlocoCirurgicoOpmesFacade.validarItensRequisicao(itemReq, itemReq);
				
				if(itemRequisicao.getDescricaoIncompativel() != null){
					justificativa.append(itemRequisicao.getDescricaoIncompativel()).append('\n');
				}
			}
			
			return justificativa.toString();
		} else {
			return null;
		}
		
	}
	
	public void validarConcluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos){
		for (DescricaoCirurgicaMateriaisConsumidosVO vo : listaMateriaisConsumidos) {
			this.validaQtdeUtilizada(vo);			
		}
	}

	public void validaQtdeUtilizada(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido) {
		
		Integer autorizado = itemMaterialConsumido.getQtdeAutorizada();
		Integer utilizado = itemMaterialConsumido.getQtdeUtilizada();
		
		if(utilizado > autorizado){
			itemMaterialConsumido.setIncompativel(Boolean.TRUE);
			itemMaterialConsumido.setMotivoIncompatibilidade("Quantidade maior que autorizada");
		} else {
			itemMaterialConsumido.setIncompativel(Boolean.FALSE);
			itemMaterialConsumido.setMotivoIncompatibilidade(null);
		}
	}
		
	public List<AghCid> pesquisarCidsPorPhiSeq(Integer phiSeq,DominioTipoPlano plano, String filtro) throws ApplicationBusinessException	{

		Set<FatProcedHospIntCid> listaCids = new HashSet<FatProcedHospIntCid>();
		Set<AghCid> cids = new HashSet<AghCid>();

		if (phiSeq!=null) {
			listaCids.addAll(getFaturamentoFacade().listarFatProcedHospIntCidPorPhiSeqValidade(phiSeq, plano, filtro));
		}	

		for (FatProcedHospIntCid procedHospIntCid : listaCids) {
			cids.add(procedHospIntCid.getCid());
		}

		List<AghCid> listRetorno = new ArrayList<AghCid>();
		listRetorno.addAll(cids);
		return listRetorno;
	}
	
	public List<RequisicoesOPMEsProcedimentosVinculadosVO> pesquisarRequisicaoOpmes(Short seqRequisicao, Date dataRequisicao, 
			RequerenteVO requerente,
			DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,
			ExecutorEtapaAtualVO executorEtapaAtual, 
			Date dataProcedimento,
			UnidadeFuncionalVO unidadeFuncional,
			EspecialidadeVO especialidade, 
			EquipeVO equipe,
			Integer prontuario, 
			Boolean pesquisarRequisicao, 
			Integer nrDias,Integer executorSeq, Integer etapaSeq){

		List<RequisicoesOPMEsProcedimentosVinculadosVO> result = mbcRequisicaoOpmesDAO.pesquisarRequisicoesOpmes( seqRequisicao, dataRequisicao, 
																												  requerente,
																												  etapaAtualRequisicaoSelecionada,
																												  executorEtapaAtual, 
																												  dataProcedimento,
																												  unidadeFuncional,
																												  especialidade, 
																												  equipe,
																												  prontuario, 
																												  pesquisarRequisicao, 
																												  nrDias,executorSeq, etapaSeq);
		
		//Pega o resultado e agrupo os executores apenas em um registro
		Map<Short, RequisicoesOPMEsProcedimentosVinculadosVO> resultAgrupado = new HashMap<Short, RequisicoesOPMEsProcedimentosVinculadosVO>();
		
		for(RequisicoesOPMEsProcedimentosVinculadosVO vo : result){
			ExecutorEtapaAtualVO isExecutor = null;
			if(vo.getExecutorMatricula() != null && vo.getExecutorVinCodigo() != null){
				RapServidores servidor = new RapServidores();
				RapServidoresId id = new RapServidoresId();
				id.setMatricula(vo.getExecutorMatricula());
				id.setVinCodigo(vo.getExecutorVinCodigo());
				servidor.setId(id);
				isExecutor = this.iBlocoCirurgicoOpmesFacade.pesquisarExecutorEtapaAtualProcesso(vo.getRequisicaoSeq(), servidor);
			}
			if(isExecutor != null){
				if(isExecutor.getAutorizadoExecutar()){
					if(resultAgrupado.containsKey(vo.getRequisicaoSeq())){
						resultAgrupado.get(vo.getRequisicaoSeq()).setPes3Nome(resultAgrupado.get(vo.getRequisicaoSeq()).getPes3Nome() + ", "  + vo.getPes3Nome());
					}else{
						resultAgrupado.put(vo.getRequisicaoSeq(), vo);
					}
				}
			}else if(vo.getExecutorMatricula() == null && vo.getExecutorVinCodigo() == null){
				if(!resultAgrupado.containsKey(vo.getRequisicaoSeq())){
					if("Requisição Não Autorizada".equals(vo.getEtapaDescricao())){
						vo.setPes3Nome(vo.getPes1Nome());
					}
					resultAgrupado.put(vo.getRequisicaoSeq(), vo);
				}
			}
		}
		
		// requisições sem fluxo
		if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.TODAS.equals(etapaAtualRequisicaoSelecionada) || DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.CANCELADA.equals(etapaAtualRequisicaoSelecionada) || DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.INCOMPATIVEL.equals(etapaAtualRequisicaoSelecionada)){
			List<RequisicoesOPMEsProcedimentosVinculadosVO> result2 = mbcRequisicaoOpmesDAO.pesquisarRequisicoesOpmesCompativeis( seqRequisicao, dataRequisicao, 
					  requerente,
					  etapaAtualRequisicaoSelecionada,
					  executorEtapaAtual, 
					  dataProcedimento,
					  unidadeFuncional,
					  especialidade, 
					  equipe,
					  prontuario, 
					  pesquisarRequisicao, 
					  nrDias,executorSeq, etapaSeq);
			for(RequisicoesOPMEsProcedimentosVinculadosVO vo : result2){
				if(vo.getEtapaDescricao() == null){
					/*if(DominioSituacaoRequisicao.COMPATIVEL.equals(vo.getSituacao())){
						if(DominioWorkflowOPMEsCodigoTemplateEtapa.TODAS.equals(etapaAtualRequisicaoSelecionada) || DominioWorkflowOPMEsCodigoTemplateEtapa.COMPATIVEL.equals(etapaAtualRequisicaoSelecionada)){
							vo.setEtapaDescricao("Procedimento Compatível");
							resultAgrupado.put(vo.getRequisicaoSeq(), vo);
						}
					}else*/ if(DominioSituacaoRequisicao.CANCELADA.equals(vo.getSituacao())){
						if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.TODAS.equals(etapaAtualRequisicaoSelecionada) || DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.CANCELADA.equals(etapaAtualRequisicaoSelecionada)){
							vo.setEtapaDescricao("Requisição Cancelada");
							resultAgrupado.put(vo.getRequisicaoSeq(), vo);
						}
					}
					else if(DominioSituacaoRequisicao.INCOMPATIVEL.equals(vo.getSituacao())){
						if(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.TODAS.equals(etapaAtualRequisicaoSelecionada) || DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.INCOMPATIVEL.equals(etapaAtualRequisicaoSelecionada)){
							vo.setEtapaDescricao("Procedimento Incompatível");
							resultAgrupado.put(vo.getRequisicaoSeq(), vo);
						}
					}
				}
				
			}
		}
		
		
		// TODO:  SORT
		
		return new ArrayList<RequisicoesOPMEsProcedimentosVinculadosVO>(resultAgrupado.values());
	}

}
