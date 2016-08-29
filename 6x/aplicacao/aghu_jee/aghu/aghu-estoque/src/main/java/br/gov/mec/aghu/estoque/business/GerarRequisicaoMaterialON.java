package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoGruposDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.estoque.vo.PesquisaRequisicaoMaterialVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifadoGrupos;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.vo.AghAtendimentosVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #595 - Gerar requisição de material
 * @author aghu
 *
 */
@Stateless
public class GerarRequisicaoMaterialON extends BaseBusiness{

	@EJB
	private SceItemRmsRN sceItemRmsRN;
	
	@EJB
	private SceReqMateriaisRN sceReqMateriaisRN;
	
	private static final Log LOG = LogFactory.getLog(GerarRequisicaoMaterialON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	@Inject
	private SceTipoMovimentosDAO sceTipoMovimentosDAO;
	
	@Inject
	private SceAlmoxarifadoGruposDAO sceAlmoxarifadoGruposDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4335487448853231289L;

	public enum GerarRequisicaoMaterialONExceptionCode implements BusinessExceptionCode  {
		FORM_RN1, MENSAGEM_PARAMETRO_TRATAMENTO_QUIMIO_NAO_ENCONTRADO, 
		MENSAGEM_PARAMETRO_TRATAMENTO_FISIOTERAPIA_NAO_ENCONTRADO,
		MENSAGEM_PARAMETRO_TRATAMENTO_DIALISE_NAO_ENCONTRADO, 
		ERRO_GERAR_REQUISICAO_MATERIAL_ALTERAR_AUTOMATICA,
		MENSAGEM_PARAMETROS_OBRIGATORIOS_NAO_PREENCHIDOS,
		MENSAGEM_ALMOXARIFADO_NAO_PERMITE_MISTURAR_MATERIAIS,
		MENSAGEM_ALMOXARIFADO_NAO_PREENCHIDO,
		MENSAGEM_ALMOXARIFADO_NAO_PERMITE_GRUPO,
		MENSAGEM_ALMOXARIFADO_NAO_PERMITE_REQUISITAR_MATERIAL_INATIVO,
		MENSAGEM_ALMOXARIFADO_NAO_PERMITE_REQUISITAR_MATERIAL_SEMALMOX,
		MENSAGEM_MATERIAL_NAO_ENCONTRADO_PARA_ALMOXARIFADO_SELECIONADO;		
	}

	public void persistirRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException{
		
		prePersistirRequisicaoMaterial(sceReqMateriais);
		
		if(sceReqMateriais.getSeq() == null){
			// Inserir
			sceReqMateriais.setDtGeracao(new Date());
			sceReqMateriais.setIndImpresso(DominioImpresso.N);
			
			inserirRequisicaoMaterial(sceReqMateriais);
			
		}else{
			// Atualizar
			atualizarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
		}
	}
	
	public void validarMaterialRM(ScoMaterial mat, SceReqMaterial reqMaterial, List<SceItemRms> listaItens,
			Boolean isAlmoxarife) throws ApplicationBusinessException {
		
		if (mat == null || reqMaterial == null) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_PARAMETROS_OBRIGATORIOS_NAO_PREENCHIDOS);
		}
		
		if (reqMaterial.getAlmoxarifado() == null) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_PREENCHIDO);
		}
		
		if (!reqMaterial.getAlmoxarifado().getIndMultiplosGrupos()) {
			List<Integer> listaGrupos = this.montarListaGrupos(reqMaterial, listaItens);
			if (listaGrupos.size() > 0 && !listaGrupos.contains(mat.getGmtCodigo())) {
				throw new ApplicationBusinessException(
						GerarRequisicaoMaterialONExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_PERMITE_GRUPO);
			}
		}
		
		// validar estocavel/direto
		if (!isAlmoxarife && reqMaterial != null && listaItens != null && !listaItens.isEmpty() 
				&& !reqMaterial.getAlmoxarifado().getIndMaterialDireto()) {
			if (!Objects.equals(mat.getIndEstocavel().isSim(), listaItens.get(0).getEstoqueAlmoxarifado().getMaterial().getEstocavel())) {
				throw new ApplicationBusinessException(
						GerarRequisicaoMaterialONExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_PERMITE_MISTURAR_MATERIAIS);
			}
		}
		
		// #42085 - Não é possível requisitar um material inativo.
		if(!mat.getIndAtivo()) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_PERMITE_REQUISITAR_MATERIAL_INATIVO);
		}
		
		// #42085 - Não é possível requisitar um material sem almoxarifado associado.
		if(mat.getAlmoxarifado()==null) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_PERMITE_REQUISITAR_MATERIAL_SEMALMOX);
		}
	}
	
	private List<Integer> montarListaGrupos(SceReqMaterial reqMaterial, List<SceItemRms> listaItens) {
		List<Integer> listaGrupos = new ArrayList<Integer>();
		// se o almoxarifado permite multiplos grupos passa reto
		// se o almoxarifado nao permite multiplos grupos
		if (reqMaterial != null && !reqMaterial.getAlmoxarifado().getIndMultiplosGrupos()) {
			List<SceAlmoxarifadoGrupos> listaAlmoxGrupos = sceAlmoxarifadoGruposDAO.pesquisarGruposPorAlmoxarifado(reqMaterial.getAlmoxarifado());
			
			// se nao tem lista de excecoes, cai na regra anterior
			if (listaAlmoxGrupos == null || listaAlmoxGrupos.isEmpty()) {
				if(listaItens != null  && !listaItens.isEmpty()){
					listaGrupos.add(listaItens.get(0).getEstoqueAlmoxarifado().getMaterial().getGrupoMaterial().getCodigo());
				} else{
					if (reqMaterial.getGrupoMaterial() != null) {
						listaGrupos.add(reqMaterial.getGrupoMaterial().getCodigo());
					}
				}
			} else {
				// se tem lista de excecoes, tem que mandar a lista para a query				
				// se a RM ja possui um item...
				if(listaItens != null  && !listaItens.isEmpty()){
					ScoGrupoMaterial grupoReq = listaItens.get(0).getEstoqueAlmoxarifado().getMaterial().getGrupoMaterial();

					//	verifica se esta na lista de grupos que pode misturar
					Integer alcSeq = null;
					for (SceAlmoxarifadoGrupos grp : listaAlmoxGrupos) {
						if (grp.getGrupoMaterial().equals(grupoReq)) {
							alcSeq = grp.getComposicao().getSeq();
						}
					}
					if (alcSeq != null) {
						// se estiver na lista, pega o alcSeq e insere na listaGrupos somente
						// os grupos do mesmo alcSeq
						for (SceAlmoxarifadoGrupos grp : listaAlmoxGrupos) {
							if (grp.getComposicao().getSeq().equals(alcSeq)) {
								listaGrupos.add(grp.getGrupoMaterial().getCodigo());
							}
						}	
					} else {
						// se nao estiver na lista, insere na listaGrupos somente o grupo do item ja existente
						listaGrupos.add(grupoReq.getCodigo());
					}
				}
			}
		}
		return listaGrupos;
	}	
	
	public void persistirItensRequisicaoMaterial(SceItemRms sceItemRms, Boolean estorno, String nomeMicrocomputador) throws BaseException{
		
		if(sceItemRms.getId() != null){
			if(sceItemRms.getId()!=null && sceItemRms.getId().getEalSeq()!=null){
				if (Boolean.FALSE.equals(estorno)) {
					validarRequisicaoMaterialAutomatica(sceItemRms.getSceReqMateriais());
				}
				sceItemRms.setScoUnidadeMedida(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getUnidadeMedida());
				if(getSceItemRmsRN().verificaExistencia(sceItemRms)){
					getSceItemRmsRN().atualizar(sceItemRms);
				}else{
					getSceItemRmsRN().inserir(sceItemRms, nomeMicrocomputador);	
				}
			}
		}
	}

	/**
	 * Regras de FORMS relacionadas ao insert e ao update
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	private void prePersistirRequisicaoMaterial(SceReqMaterial sceReqMateriais)
			throws ApplicationBusinessException {
		validarPacoteGrupoMaterial(sceReqMateriais);
		gravarNomeImpressora(sceReqMateriais);
	}

	public void inserirRequisicaoMaterial(SceReqMaterial sceReqMateriais) throws BaseException{
		// Lógica de FORMS e montagem de VOs
		// Chamada para RNs com regras de banco
		getSceReqMateriaisRN().inserir(sceReqMateriais);
	}
	
	public void atualizarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException{
		// Lógica de FORMS e montagem de VOs
		// Chamada para RNs com regras de banco
		validarRequisicaoMaterialAutomatica(sceReqMateriais);
		getSceReqMateriaisRN().atualizar(sceReqMateriais, nomeMicrocomputador);
	}
	
	public void excluirItemRequisicaoMaterial(SceItemRms sceItemRms, Integer countItensLista, Boolean estorno) throws BaseException{
		if (Boolean.FALSE.equals(estorno)) {
			validarRequisicaoMaterialAutomatica(sceItemRms.getSceReqMateriais());	
		}
		getSceItemRmsRN().remover(sceItemRms, countItensLista);
	}

	/**
	 * Método valida se requisicao de materiais possui pacote mas não possui grupo.
	 * @param sceReqMateriais
	 */
	private void validarPacoteGrupoMaterial(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException{
		if(sceReqMateriais.getPacoteMaterial() != null && sceReqMateriais.getGrupoMaterial() == null) {
			throw new ApplicationBusinessException(GerarRequisicaoMaterialONExceptionCode.FORM_RN1);
		}
	}
	
	private void validarRequisicaoMaterialAutomatica(SceReqMaterial reqMaterial) throws ApplicationBusinessException {
		if (reqMaterial.getAutomatica() != null && reqMaterial.getAutomatica()) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.ERRO_GERAR_REQUISICAO_MATERIAL_ALTERAR_AUTOMATICA);
		}
	}

	/**
	 * TODO: Método de impressão das requisicoes
	 * @param sceReqMateriais
	 */
	private void gravarNomeImpressora(SceReqMaterial sceReqMateriais) {
		// TODO Código temporario
		if(sceReqMateriais!=null && sceReqMateriais.getIndImpresso()==null){
			sceReqMateriais.setIndImpresso(DominioImpresso.N);		
		}
	}
	
	public SceReqMaterial retornarReqMaterial(AghUnidadesFuncionais unidadeFuncional, AghAtendimentos atendimento) throws BaseException {
		SceReqMaterial sceReqMaterial;
		FccCentroCustos centroCustoAtendimento = null;
		
		if(atendimento.getUnidadeFuncional().getCentroCusto()!=null) {
			centroCustoAtendimento = atendimento.getUnidadeFuncional().getCentroCusto();
		}
		
		sceReqMaterial = this.getSceReqMateriaisDAO().obterRequicaoMaterialUnidadeFuncional(unidadeFuncional.getSeq(),atendimento.getSeq(),centroCustoAtendimento);
		
		if(sceReqMaterial!=null) {
			return sceReqMaterial;
		}
		else {
			sceReqMaterial = new SceReqMaterial();
			sceReqMaterial.setAtendimento(atendimento);
			sceReqMaterial.setAutomatica(true);
			sceReqMaterial.setCentroCustoAplica(atendimento.getUnidadeFuncional().getCentroCusto());
			
			sceReqMaterial.setCentroCusto(unidadeFuncional.getCentroCusto());
			
			// Grupo do Material - 2(Medicamento)
			ScoGrupoMaterial grupoMaterial = this.getComprasFacade().obterScoGrupoMaterialPorChavePrimaria(2);
			sceReqMaterial.setGrupoMaterial(grupoMaterial);
			
			sceReqMaterial.setAlmoxarifado(unidadeFuncional.getAlmoxarifado());
			sceReqMaterial.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
			sceReqMaterial.setEstorno(Boolean.FALSE);
			sceReqMaterial.setIndImpresso(DominioImpresso.N);
			
			// Movimento requisição material ativo (5,2)
			Short seqTipoMovimento = 5;
			Byte complementoTipoMovimento = 2;
			SceTipoMovimento tipoMovimento = this.getSceTipoMovimentosDAO().obterSceTipoMovimentosSeqComplemento(seqTipoMovimento,complementoTipoMovimento);
			sceReqMaterial.setTipoMovimento(tipoMovimento);
			
			sceReqMaterial.setDtGeracao(new Date());

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			sceReqMaterial.setServidor(servidorLogado);
			
			getSceReqMateriaisRN().inserir(sceReqMaterial);
			
			return sceReqMaterial;
		}
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<AghAtendimentosVO> listarAtendimentosPaciente(Integer pacCodigo) throws ApplicationBusinessException {
		List<AghAtendimentosVO> listaAtendimentos = new ArrayList<AghAtendimentosVO>();
		
		List<Object[]> listaAtendimentosEmAndamento = getAghuFacade().listarAtendimentosPacienteEmAndamentoPorCodigo(pacCodigo);
		for (Object[] atendimento : listaAtendimentosEmAndamento) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setSeq((Integer) atendimento[0]);
			atend.setDthrInicio((Date) atendimento[1]);
			atend.setUnidadeFuncional((AghUnidadesFuncionais) atendimento[2]);
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			atend.setOrigemAtendimento(((DominioOrigemAtendimento) atendimento[3]).getDescricao());
			listaAtendimentos.add(atend);
		}
		
		Date data = new Date();
		Date dataInicio = DateUtil.obterDataComHoraInical(data);
		Date dataFim = DateUtil.obterDataComHoraFinal(data);
		List<Object[]> listaAtendimentosAmbulatorio = getAmbulatorioFacade().listarAtendimentosPacienteAmbulatorioPorCodigo(pacCodigo, dataInicio, dataFim);
		for (Object[] atendimento : listaAtendimentosAmbulatorio) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setSeq((Integer) atendimento[0]);
			atend.setDthrInicio((Date) atendimento[1]);
			Short unfSeq = (Short) atendimento[2];
			AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
			atend.setUnidadeFuncional(unidadeFuncional);
			atend.setOrigemAtendimento(DominioOrigemAtendimento.A.getDescricao());
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			listaAtendimentos.add(atend);
		}
		
		AghParametros tipoTratamentoQuimio = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_TRAT_QUIMIO);
		if (tipoTratamentoQuimio == null || tipoTratamentoQuimio.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_PARAMETRO_TRATAMENTO_QUIMIO_NAO_ENCONTRADO);
		}
		List<Object[]> listaAtendimentosQuimioterapia = getAghuFacade().listarAtendimentosPacienteTratamentoPorCodigo(pacCodigo, tipoTratamentoQuimio.getVlrNumerico().intValue());
		for (Object[] atendimento : listaAtendimentosQuimioterapia) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setSeq((Integer) atendimento[0]);
			atend.setDthrInicio((Date) atendimento[1]);
			atend.setUnidadeFuncional((AghUnidadesFuncionais) atendimento[2]);
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			atend.setOrigemAtendimento("Quimioterapia");
			listaAtendimentos.add(atend);
		}
		
		AghParametros tipoTratamentoFisioterapia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_TRAT_FISIOTERAPIA);
		if (tipoTratamentoFisioterapia == null || tipoTratamentoFisioterapia.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_PARAMETRO_TRATAMENTO_FISIOTERAPIA_NAO_ENCONTRADO);
		}
		List<Object[]> listaAtendimentosFisioterapia = getAghuFacade().listarAtendimentosPacienteTratamentoPorCodigo(pacCodigo, tipoTratamentoFisioterapia.getVlrNumerico().intValue());
		for (Object[] atendimento : listaAtendimentosFisioterapia) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setSeq((Integer) atendimento[0]);
			atend.setDthrInicio((Date) atendimento[1]);
			atend.setUnidadeFuncional((AghUnidadesFuncionais) atendimento[2]);
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			atend.setOrigemAtendimento("Fisioterapia");
			listaAtendimentos.add(atend);
		}
		
		AghParametros tipoTratamentoHemodialise = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_TRAT_DIALISE);
		if (tipoTratamentoHemodialise == null || tipoTratamentoHemodialise.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_PARAMETRO_TRATAMENTO_DIALISE_NAO_ENCONTRADO);
		}
		List<Object[]> listaAtendimentosHemodialise = getAghuFacade().listarAtendimentosPacienteTratamentoPorCodigo(pacCodigo, tipoTratamentoHemodialise.getVlrNumerico().intValue());
		for (Object[] atendimento : listaAtendimentosHemodialise) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setSeq((Integer) atendimento[0]);
			atend.setDthrInicio((Date) atendimento[1]);
			atend.setUnidadeFuncional((AghUnidadesFuncionais) atendimento[2]);
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			atend.setOrigemAtendimento("Hemodiálise");
			listaAtendimentos.add(atend);
		}
		DominioSituacaoCirurgia situacaoCirurgia = DominioSituacaoCirurgia.CANC;
		DominioOrigemPacienteCirurgia origemPacienteCirurgia = DominioOrigemPacienteCirurgia.I;
		
		List<Object[]> listaAtendimentosCirurgiaInternacao = getBlocoCirurgicoFacade().listarAtendimentosPacienteCirurgiaInternacaoPorCodigo(pacCodigo, situacaoCirurgia, origemPacienteCirurgia, true);
		for (Object[] atendimento : listaAtendimentosCirurgiaInternacao) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setDthrInicio((Date) atendimento[0]);
			atend.setUnidadeFuncional((AghUnidadesFuncionais) atendimento[1]);
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			atend.setOrigemAtendimento(DominioOrigemAtendimento.C.getDescricao());
			listaAtendimentos.add(atend);
		}
		
		List<Object[]> listaAtendimentosCirurgiaAmbulatorio = getBlocoCirurgicoFacade().listarAtendimentosPacienteCirurgiaAmbulatorioPorCodigo(pacCodigo);
		for (Object[] atendimento : listaAtendimentosCirurgiaAmbulatorio) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setSeq((Integer) atendimento[0]);
			atend.setDthrInicio((Date) atendimento[1]);
			atend.setUnidadeFuncional((AghUnidadesFuncionais) atendimento[2]);
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			atend.setOrigemAtendimento(DominioOrigemAtendimento.C.getDescricao());
			listaAtendimentos.add(atend);
		}
		
		List<Object[]> listaAtendimentosEmergencia = getAmbulatorioFacade().listarAtendimentosPacienteTriagemPorCodigo(pacCodigo);
		for (Object[] atendimento : listaAtendimentosEmergencia) {
			AghAtendimentosVO atend = new AghAtendimentosVO();
			atend.setDthrInicio((Date) atendimento[0]);
			Short unfSeq = (Short) atendimento[1];
			AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
			atend.setUnidadeFuncional(unidadeFuncional);
			atend.getUnidadeFuncional().getAndarAlaDescricao();
			atend.setOrigemAtendimento("Emergência");
			listaAtendimentos.add(atend);
		}
		
		return listaAtendimentos;
	}
	
	
	/**
	 * Pesquisa Estoque Almoxarifado através do Almoxarifado e Material
	 * Obs. Esta pesquisa considera somente o Fornecedor Padrão/HU
	 * @param seqAlmoxarifado
	 * @param numeroFornecedor
	 * @param codigoMaterial
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public SceEstoqueAlmoxarifado pesquisarEstoqueAlmoxarifadoFornecedorPadrao(Short seqAlmoxarifado, Integer codigoMaterial) throws ApplicationBusinessException {
		

		AghParametros parametroFornecedorPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		// Obtém o número do fornecedor Padrão/HU
		final Integer numeroFornecedor = parametroFornecedorPadrao.getVlrNumerico().intValue();
		
		// Pesquisa Estoque Almoxarifado através do Fornecedor Padrão
		List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(seqAlmoxarifado, codigoMaterial, numeroFornecedor);
		
		// Obtém o resultado único
		if(listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()){
			return listaEstoqueAlmoxarifado.get(0);
		} else {
			throw new ApplicationBusinessException(
					GerarRequisicaoMaterialONExceptionCode.MENSAGEM_MATERIAL_NAO_ENCONTRADO_PARA_ALMOXARIFADO_SELECIONADO
					,codigoMaterial
					, seqAlmoxarifado
					, numeroFornecedor);
			}

	}
	
	public Long pesquisaRequisicoesMateriaisCount(PesquisaRequisicaoMaterialVO filtro){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		filtro.setServidorLogado(servidorLogado);
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriaisCount(filtro);
	}
	
	public List<SceReqMaterial> pesquisaRequisicoesMateriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PesquisaRequisicaoMaterialVO filtro){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		filtro.setServidorLogado(servidorLogado);
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriais(firstResult, maxResult, orderProperty, asc, filtro);
	}
	
	
	
	/**
	 * get de RNs e DAOs
	 */
	protected SceReqMateriaisRN getSceReqMateriaisRN(){
		return sceReqMateriaisRN;
	}
	
	protected SceItemRmsRN getSceItemRmsRN(){
		return sceItemRmsRN;
	}
	
	protected SceReqMateriaisDAO getSceReqMateriaisDAO(){
		return sceReqMateriaisDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}	
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
