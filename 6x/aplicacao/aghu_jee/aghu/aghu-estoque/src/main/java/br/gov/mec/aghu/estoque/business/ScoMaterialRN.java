package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.estoque.business.ScoMaterialRN.ScoMaterialRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEntradaSaidaSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.DadosEntradaMateriasDiaVO;
import br.gov.mec.aghu.estoque.vo.GrupoEntradaMateriasDiaVO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceEstoqueGeralId;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoesId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class ScoMaterialRN extends BaseBusiness{
	
	@EJB
	private ScoMateriaisClassificacaoRN scoMateriaisClassificacaoRN;
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	@EJB
	private SceEstoqueGeralRN sceEstoqueGeralRN;
	
	private static final Log LOG = LogFactory.getLog(ScoMaterialRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceEntradaSaidaSemLicitacaoDAO sceEntradaSaidaSemLicitacaoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	
	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7387617058066731361L;

	public enum ScoMaterialRNExceptionCode implements BusinessExceptionCode {
		SCO_00298,MENSAGEM_GRUPO_NAO_CLASSIFICADO,MENSAGEM_MATERIAL_SALDO_ESTOQUE_DESATIVADO,
		MENSAGEM_ALMOXARIFADO_NAO_CADASTRADO, MENSAGEM_ALMOXARIFADO_NAO_INFORMADO,
		MENSAGEM_MATERIAL_SALDO_ALMOXARIFADO,MENSAGEM_MATERIAL_SALDO_BLOQUEADO_PROBLEMA_ALMOXARIFADO, 
		MENSAGEM_DOCUMENTO_UNIDADE_ANTIGA_NAO_ENCERRADO, MENSAGEM_AF_UNIDADE_ANTIGA_NAO_ENCERRADA;
	}
	
	
	public List<GrupoEntradaMateriasDiaVO> gerarListaGrupoEntradamateriaisDia(
			Date dataGeracao,
			List<DadosEntradaMateriasDiaVO> dadosEntradaMateriasDiaVOs) throws ApplicationBusinessException{

		Map<Integer, String> lMapGrupos = new HashMap<Integer, String>();
		for (DadosEntradaMateriasDiaVO lDadosEntradaMateriasDiaVO : dadosEntradaMateriasDiaVOs) {
			lMapGrupos.put(lDadosEntradaMateriasDiaVO.getGrupo(),
					lDadosEntradaMateriasDiaVO.getDescricaoGrupo());
		}
		List<GrupoEntradaMateriasDiaVO> listaGrupos = new ArrayList<GrupoEntradaMateriasDiaVO>();

		for (Entry<Integer, String> grupo : lMapGrupos.entrySet()) {
			Integer lGrupo = grupo.getKey();
			String descGrupo = grupo.getValue();
			GrupoEntradaMateriasDiaVO grupoVO = new GrupoEntradaMateriasDiaVO(
					gmtEntradaMateriaisEstocaveisDiaFormula(dataGeracao, lGrupo),
					gmtEntradaMaterialDiretoDiaFormula(dataGeracao, lGrupo),
					gmtEntradaAcumuladoMaterialEstocavelMesFormula(dataGeracao,
							lGrupo),
					gmtEntradaAcumuladoMaterialDiretoMesFormula(dataGeracao,
							lGrupo), cfEntradaAcumuladoMaterialMesFormula(
							dataGeracao, lGrupo),
					gmtConsumoMaterialEstocavelDiaFormula(dataGeracao, lGrupo),
					gmtConsumoMaterialDiretoDiaFormula(dataGeracao, lGrupo),
					gmtConsumoAcumuladoMaterialEstocavelMesFormu(dataGeracao,
							lGrupo), gmtConsumoAcumuladoMaterialDiretoMesFormu(
							dataGeracao, lGrupo),
					cfConsumoAcumuladoMaterialMesFormula(dataGeracao, lGrupo),
					lGrupo, descGrupo);
			listaGrupos.add(grupoVO);
		}

		return listaGrupos;
	}
	
	
	/*
	 * Métodos para Inserir ScoMaterial
	 */
	
	/**
	 * ORADB TRIGGER SCOT_MAT_BRI (INSERT)
	 * @param material
	 * @throws BaseException
	 */
	private void preInserir(ScoMaterial material) throws BaseException{
		this.atualizarMaterialServidorLogadoDataDigitacao(material); // RN1
		this.atualizarMaterialCartaoPonto(material); // RN2
		this.validarMaterialEstocavelGenerico(material); // RN3
		this.validarMaterialControleValidade(material); // RN4 e RN5
		this.validarAlmoxarifadoInformado(material);

		this.setaValoresDefaulsSCOMaterialCasoNulos(material);
	}

	private void setaValoresDefaulsSCOMaterialCasoNulos(ScoMaterial material) {
		// Seta valores defauls caso estejam nulos.
		if(material != null){
			if(material.getIndEstocavel() == null){
				material.setEstocavel(false);
			}
			
			if(material.getIndMenorPreco() == null){
				material.setIndMenorPreco(DominioSimNao.N);
			}
			
			if(material.getIndAtuQtdeDisponivel() == null ){
				material.setIndAtuQtdeDisponivel(DominioSimNao.N);
			}
			
			if(material.getIndGenerico() == null){
				material.setIndGenerico(DominioSimNao.N);
			}
			
			if(material.getIndCcih() == null ){
				material.setIndCcih(DominioSimNao.N);
			}
			
			if(material.getIndFaturavel() == null){
				material.setIndFaturavel(DominioSimNao.N);
			}
			
			if(material.getIndControleValidade() == null){
				material.setIndControleValidade(DominioSimNao.S);
			}

			if(material.getIndSituacao() == null){
				material.setIndSituacao(DominioSituacao.A);
			}
		}
	}
	
	/**
	 * Inserir ScoMaterial
	 * @param material
	 * @throws BaseException
	 */
	public void inserir(ScoMaterial material, String nomeMicrocomputador) throws BaseException{
		this.preInserir(material);
		this.getComprasFacade().persistirScoMaterial(material);
		
		this.posInserir(material, nomeMicrocomputador);
		this.inserirScoMaterialJN(material);
	}

	/**
	 * ORADB PROCEDURE SCOP_ENFORCE_MAT_RULES (INSERT) 
	 * @param material
	 * @throws BaseException
	 */
	private void posInserir(ScoMaterial material, String nomeMicrocomputador) throws BaseException{
		
		this.gerarClassificacaoGrupoMaterial(material, nomeMicrocomputador); // RN1
		this.gerarEstoqueGeral(material); // RN2
		this.gerarEstoqueAlmoxarifadoCentralMaterial(material); // RN3
		this.gerarEstoqueAlmoxarifadoMaterial(material); // RN4
		this.validarPosInserirMaterialFaturamentoProcedimentosHospitalaresInternos(material); // RN5 e RN5.1
		this.gerarMedicamentoMaterial(material, nomeMicrocomputador); // RN6

	}
	
	/**
	 * ORADB PROCEDURE SCOT_MAT_ARI (INSERT)
	 * @param material
	 * @throws BaseException
	 */
	public void inserirScoMaterialJN(ScoMaterial material) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//ScoMaterialJN materialJN = new ScoMaterialJN(material);
		ScoMaterialJN materialJN = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.INS, ScoMaterialJN.class, servidorLogado.getUsuario());
		materialJN.doSetPropriedades(material);
		
		// TODO REVISAR REGRA: Na inserção de Journal alguns atributos antigos são considerados
		/*if (DominioOperacoesJournal.INS.equals(tipoOperacaoJournal)){
			
			materialJN.setIndCcih(old.getIndCcih());
			materialJN.setIndFotosensivel(old.getIndFotosensivel());
			materialJN.setIndTipoUso(old.getIndTipoUso());
			materialJN.setNumero(old.getNumero());
			materialJN.setIndCadSapiens(old.getIndCadSapiens());
			materialJN.setDtCadSapiens(old.getDtCadSapiens());
			materialJN.setServidorCadSapiens(old.getServidorCadSapiens());
			materialJN.setCodRetencao(old.getCodRetencao());
			materialJN.setCodTransacaoSapiens(old.getCodTransacaoSapiens());
			materialJN.setCodSiasg(old.getCodSiasg());
			materialJN.setCodSitTribSapiens(old.getCodSitTribSapiens());
			materialJN.setNcmCodigo(old.getNcmCodigo());
			
		}*/
		
		//materialJN.setOperacao(DominioOperacoesJournal.INS);
		//materialJN.setJnUser(servidorLogado.getUsuario());
		//materialJN.setJnDateTime(new Date());
		materialJN.setNomeEstacao("");
		
		this.getComprasFacade().inserirScoMaterialJN(materialJN);	
	}

	/*
	 * Métodos para Atualizar ScoMaterial
	 */
	
	/**
	 * ORADB TRIGGER SCOT_MAT_BRU (UPDATE)
	 * @param material
	 * @throws BaseException
	 */
	private void preAtualizar(ScoMaterial material, ScoMaterial old, String nomeMicrocomputador) throws BaseException{
		this.atualizarMaterialServidorLogadoSituacaoModificada(material, old); // RN1
		this.atualizarMaterialDataDesativacao(material, old); // RN2 e RN3
		this.validarMaterialEstocavelGenerico(material); // RN4
		this.atualizarRevisaoCadastroMedicamentos(material, old, nomeMicrocomputador); // RN5
		this.validarSaldoEstoqueMaterialDesativado(material, old); // RN6
		this.atualizarAlmoxarifadoLocalEstoqueModificado(material, old);// RN7
		this.validarNovoLocalAlmoxarifadoEstoqueModificado(material, old); // RN8
		this.validarUnidadeMedidaModificada(material, old);// RN9
		this.atualizarMaterialServidorLogadoAlteracao(material, old); // RN 10
		
		this.setaValoresDefaulsSCOMaterialCasoNulos(material);
	}

	/**
	 * Atualizar ScoMaterial
	 * @param material
	 * @throws BaseException
	 */
	public void atualizar(ScoMaterial material, String nomeMicrocomputador) throws BaseException{
		
		// Obtem o registro original/atual do material
		final ScoMaterial old = this.getComprasFacade().obterScoMaterialDetalhadoPorChavePrimaria(material.getCodigo());
		this.scoMaterialDAO.desatachar(old);
		this.preAtualizar(material, old, nomeMicrocomputador);
		this.getComprasFacade().atualizarScoMaterial(material);
		this.posAtualizar(material, old, nomeMicrocomputador);
		this.inserirScoMaterialJN(material,old);

	}

	/**
	 * ORADB PROCEDURE SCOP_ENFORCE_MAT_RULES (UPDATE)
	 * @param material
	 * @throws BaseException
	 */
	private void posAtualizar(ScoMaterial material, ScoMaterial old, String nomeMicrocomputador) throws BaseException{

		this.atualizarClassificacaoMaterialGrupoScoMaterial(material, old,  nomeMicrocomputador); // RN1
		this.validarPosAlterarMaterialFaturamentoProcedimentosHospitalaresInternos(material, old); // RN2
		
		// Verifica alterações no código grupo de material
		if(CoreUtil.modificados(material.getGrupoMaterial(), old.getGrupoMaterial())){  
			this.gerarMedicamentoMaterial(material, nomeMicrocomputador); // RN3
		}
		
		this.atualizarEstoqueAlmoxarifadoMaterialEstocavel(material, old, nomeMicrocomputador); // RN4
		this.validarSituacaoMaterial(material, old, nomeMicrocomputador); // RN5
		
		// Atualiza unidade de medida do material nos almoxarifados e estoque gerais
		//if(CoreUtil.modificados(material.getUnidadeMedida(), old.getUnidadeMedida())){
			this.atualizarUnidadeMedidaMaterialEstoqueAlmoxarifadoGeral(material, nomeMicrocomputador); // RN6
		//}
		
		// Verifica se o material está em contrato
		if(CoreUtil.modificados(material.getAlmoxarifado(), old.getAlmoxarifado())){
			this.validarMaterialEmContrato(material,old, nomeMicrocomputador); // RN7
		}

		this.validarMaterialCadastradoAlmoxarifado(material, old); // RN8

	}
	
	/**
	 * ORADB PROCEDURE ORADB SCOT_MAT_ARU (UPDATE)
	 * @param material
	 * @throws BaseException
	 */
	protected void inserirScoMaterialJN(ScoMaterial material, ScoMaterial old) throws BaseException{

		// TODO Testar de forma sucinta de comparação
		if(CoreUtil.modificados(material.getCodigo(), old.getCodigo())
				|| CoreUtil.modificados(material.getUnidadeMedida(), old.getUnidadeMedida())
				|| CoreUtil.modificados(material.getServidor(), old.getServidor())
				|| CoreUtil.modificados(material.getServidorDesativado(), old.getServidorDesativado())
				|| CoreUtil.modificados(material.getDescricao(), old.getDescricao())
				|| CoreUtil.modificados(material.getDtDigitacao(), old.getDtDigitacao())
				|| CoreUtil.modificados(material.getIndSituacao(), old.getIndSituacao())
				|| CoreUtil.modificados(material.getIndGenerico(), old.getIndGenerico())
				|| CoreUtil.modificados(material.getIndMenorPreco(), old.getIndMenorPreco())
				|| CoreUtil.modificados(material.getIndProducaoInterna(), old.getIndProducaoInterna())
				|| CoreUtil.modificados(material.getIndAtuQtdeDisponivel(), old.getIndAtuQtdeDisponivel())
				|| CoreUtil.modificados(material.getClassifXyz(), old.getClassifXyz())
				|| CoreUtil.modificados(material.getSazonalidade(), old.getSazonalidade())
				|| CoreUtil.modificados(material.getNome(), old.getNome())
				|| CoreUtil.modificados(material.getObservacao(), old.getObservacao())
				|| CoreUtil.modificados(material.getDtAlteracao(), old.getDtAlteracao())
				|| CoreUtil.modificados(material.getDtDesativacao(), old.getDtDesativacao())
				|| CoreUtil.modificados(material.getIndControleValidade(), old.getIndControleValidade())
				|| CoreUtil.modificados(material.getIndFaturavel(), old.getIndFaturavel())
				|| CoreUtil.modificados(material.getIndEnvioProdInterna(), old.getIndEnvioProdInterna())
				|| CoreUtil.modificados(material.getAlmLocalEstq(), old.getAlmLocalEstq())
				|| CoreUtil.modificados(material.getServidorAlteracao(), old.getServidorAlteracao())
				|| CoreUtil.modificados(material.getIndPadronizado(), old.getIndPadronizado())
				|| CoreUtil.modificados(material.getIndCcih(), old.getIndCcih())
				|| CoreUtil.modificados(material.getIndControleLote(), old.getIndControleLote())
				|| CoreUtil.modificados(material.getIndFotosensivel(), old.getIndFotosensivel())
				|| CoreUtil.modificados(material.getIndTipoUso(), old.getIndTipoUso())
				|| CoreUtil.modificados(material.getNumero(), old.getNumero())
				|| CoreUtil.modificados(material.getIndCadSapiens(), old.getIndCadSapiens())
				|| CoreUtil.modificados(material.getDtCadSapiens(), old.getDtCadSapiens())
				|| CoreUtil.modificados(material.getServidorCadSapiens(), old.getServidorCadSapiens())
				|| CoreUtil.modificados(material.getCodRetencao(), old.getCodRetencao())
				|| CoreUtil.modificados(material.getCodTransacaoSapiens(), old.getCodTransacaoSapiens())
				|| CoreUtil.modificados(material.getCodSiasg(), old.getCodSiasg())
				|| CoreUtil.modificados(material.getCodSitTribSapiens(), old.getCodSitTribSapiens())
				|| CoreUtil.modificados(material.getNcmCodigo(), old.getNcmCodigo())
				|| CoreUtil.modificados(material.getAfaMedicamento(), old.getAfaMedicamento())
				|| CoreUtil.modificados(material.getGrupoMaterial(), old.getGrupoMaterial())
				|| CoreUtil.modificados(material.getAlmoxarifado(), old.getAlmoxarifado())
				|| CoreUtil.modificados(material.getOrigemParecerTecnico(), old.getOrigemParecerTecnico())
				|| CoreUtil.modificados(material.getMarcasModelos(), old.getMarcasModelos())
				|| CoreUtil.modificados(material.getIndTipoResiduo(), old.getIndTipoResiduo())//#26669
				|| CoreUtil.modificados(material.getIndTermolabil(), old.getIndTermolabil())
				|| CoreUtil.modificados(material.getTemperatura(), old.getTemperatura())
				|| CoreUtil.modificados(material.getIndVinculado(), old.getIndVinculado())
				|| CoreUtil.modificados(material.getIndSustentavel(), old.getIndSustentavel())
				|| CoreUtil.modificados(material.getIndCapCmed(), old.getIndCapCmed())
				|| CoreUtil.modificados(material.getIndConfaz(), old.getIndConfaz())
				|| CoreUtil.modificados(material.getLegislacao(), old.getLegislacao())
				|| CoreUtil.modificados(material.getCodCatmat(), old.getCodCatmat())
				|| CoreUtil.modificados(material.getCodMatAntigo(), old.getCodMatAntigo())
				|| CoreUtil.modificados(material.getEstocavel(), old.getEstocavel())){
				
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
	
//			ScoMaterialJN materialJN = new ScoMaterialJN(old);
			ScoMaterialJN materialJN = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, ScoMaterialJN.class, servidorLogado.getUsuario());
			materialJN.doSetPropriedades(old);
			
			//materialJN.setOperacao(DominioOperacoesJournal.UPD);
			//materialJN.setJnUser(servidorLogado.getUsuario());
			//materialJN.setJnDateTime(new Date());
			materialJN.setNomeEstacao("");
			
			this.getComprasFacade().inserirScoMaterialJN(materialJN);
		}
		
	}

	/*
	 * RNs Inserir
	 */
	
	/**
	 * Atualiza servidor com o usuário logado e data de digitação com a data atual
	 * @param material
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarMaterialServidorLogadoDataDigitacao(ScoMaterial material) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		material.setServidor(servidorLogado);
		material.setDtDigitacao(new Date());
	}

	
	/**
	 * Atualiza cartão ponto do servidor quando o material é inativo
	 * @param material
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarMaterialCartaoPonto(ScoMaterial material) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(DominioSituacao.I.equals(material.getIndSituacao())){
			material.setServidorDesativado(servidorLogado);
			material.setDtDesativacao(new Date());
		}
	}
	
	/**
	 * Valida material estocável e genérico
	 * @param material
	 * @throws ApplicationBusinessException
	 */
	protected void validarMaterialEstocavelGenerico(ScoMaterial material) throws ApplicationBusinessException{
		if(DominioSimNao.S.equals(material.getIndEstocavel()) && DominioSimNao.S.equals(material.getIndGenerico())){
			throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.SCO_00298);
		}
	}
	
	/**
	 * Valida e ativa o controle de validade do material
	 * @param material
	 * @throws ApplicationBusinessException
	 */
	protected void validarMaterialControleValidade(ScoMaterial material) throws ApplicationBusinessException{

		final ScoGrupoMaterial grupoMaterial = material.getGrupoMaterial();
		if(grupoMaterial != null){
			material.setIndControleValidade(DominioSimNao.getInstance(grupoMaterial.getControleValidade()));
		}
		// Destativa o indicador de envio para produção interna
		material.setIndEnvioProdInterna(DominioSimNao.N); 
		
	}
	
	private void validarAlmoxarifadoInformado(ScoMaterial material) throws ApplicationBusinessException{
		if(material.getAlmoxarifado()==null){
			throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_INFORMADO);
		}
	}
	
	/**
	 * Gera uma classificação para o material e grupo correspondente
	 * @param material
	 * @throws BaseException
	 */
	protected void gerarClassificacaoGrupoMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException{
		
		final Integer codigoMaterial = material.getCodigo();
		final Integer codigoGrupoMaterial = material.getGrupoMaterial().getCodigo();
		List<Long> listaNumerosClassificacao = this.getComprasFacade().pesquisarNumerosClassificacaoGrupoMaterial(codigoGrupoMaterial);
		
		Long cn5Numero = null;
		if(listaNumerosClassificacao != null && !listaNumerosClassificacao.isEmpty()){
			cn5Numero = listaNumerosClassificacao.get(0);
		} else{
			throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_GRUPO_NAO_CLASSIFICADO);
		}
		
		ScoMateriaisClassificacoes materiaisClassificacoes = new ScoMateriaisClassificacoes();
		
		ScoMateriaisClassificacoesId id = new ScoMateriaisClassificacoesId();
		id.setCn5Numero(cn5Numero);
		id.setMatCodigo(codigoMaterial);
		
		materiaisClassificacoes.setId(id);
		
		materiaisClassificacoes.setMaterial(material);

		this.getScoMateriaisClassificacaoRN().inserir(materiaisClassificacoes, nomeMicrocomputador);
	}
	
	
	/**
	 * Gera um estoque geral para o material informado
	 * @param material
	 * @throws BaseException
	 */
	protected void gerarEstoqueGeral(ScoMaterial material) throws BaseException{
		
		SceEstoqueGeral estoqueGeral = new SceEstoqueGeral();
		
		estoqueGeral.setId(new SceEstoqueGeralId());
		
		estoqueGeral.setMaterial(material);
		estoqueGeral.setCustoMedioPonderado(BigDecimal.ZERO);
		estoqueGeral.setResiduo(new Double(0));
		estoqueGeral.setValor(new Double(0));
		estoqueGeral.setUnidadeMedida(material.getUnidadeMedida());
		estoqueGeral.setClassificacaoAbc(null);
		estoqueGeral.setSubClassificacaoAbc(null);
		estoqueGeral.setQtde(0);
		
		estoqueGeral.setFornecedor(this.getScoFornecedorPadrao());
		
		estoqueGeral.setValorConsignado(new Double(0));
		estoqueGeral.setQtdeConsignada(0);

		this.getSceEstoqueGeralRN().inserir(estoqueGeral);
		
	}
	
	/**
	 * Gera um registro de estoque almoxarifado CENTRAL para o material informado
	 * Obs. Para cada material criado é incluído um registro na tabela almoxarifado central com fornecedor do HU
	 * @param material
	 * @throws BaseException
	 */
	protected void gerarEstoqueAlmoxarifadoCentralMaterial(ScoMaterial material) throws BaseException{
		this.getSceEstoqueAlmoxarifadoRN().gerarEstoqueAlmoxarifadoCentralMaterial(material);
		this.getSceEstoqueAlmoxarifadoDAO().flush();
	}
	
	/**
	 *  Gera um registro de estoque almoxarifado para o material informado
	 * @param material
	 * @throws BaseException
	 */
	protected void gerarEstoqueAlmoxarifadoMaterial(ScoMaterial material) throws BaseException{
	
		final AghParametros parametroAlmoxCentral = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);

		// Verifica se o almoxarifado é diferente central
		if(!material.getAlmoxarifado().getSeq().equals(parametroAlmoxCentral.getVlrNumerico().shortValue())){
			
			this.getSceEstoqueAlmoxarifadoRN().gerarEstoqueAlmoxarifadoMaterial(material, material.getAlmoxarifado());
		} 
		
	}
	
	/**
	 * Valida inserção do material procedimentos hospitalares internos no faturamento
	 * @param material
	 * @throws BaseException
	 */
	protected void validarPosInserirMaterialFaturamentoProcedimentosHospitalaresInternos(ScoMaterial material) throws BaseException{
	
		final AghParametros parametroGrpoMatOrdProt = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
		
		final boolean isMaterialFaturavel = material.getIndFaturavel().isSim();
		final Integer grupoMaterialCodigo = material.getGrupoMaterial().getCodigo();
		
		if(isMaterialFaturavel || grupoMaterialCodigo.equals(parametroGrpoMatOrdProt.getVlrNumerico().intValue())){
			
			// Insere/Altera material nos procedimentos hospitalares internos do faturamento 
			this.inserirAlterarMaterialFaturamentoProcedimentosHospitalaresInternos(material);

		}
		
	}
	
	
	/**
	 * Gera um registro de medicamentos para o material informado
	 * @param material
	 * @throws BaseException
	 */
	protected void gerarMedicamentoMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException{
		
		final AghParametros parametroGrMatMedic = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_MEDIC);
		final Integer grupoMaterialCodigo = material.getGrupoMaterial().getCodigo();
		
		if(grupoMaterialCodigo.equals(parametroGrMatMedic.getVlrNumerico().intValue())){
			
			this.gerarAtualizarMedicamentoMaterial(material, nomeMicrocomputador);
			
		}

	}
	
	/**
	 * ORADB PROCEDURE AFAK_MED_RN.RN_MEDP_ATU_INCL_MAT
	 * @param material
	 * @throws BaseException
	 */
	protected void gerarAtualizarMedicamentoMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException{
		
		CoreUtil.validaParametrosObrigatorios(material);
		
		final AfaMedicamento medicamento = this.getFarmaciaFacade().obterMedicamento(material.getCodigo());
		
		// Atualiza a situação da revisão do cadastro caso o medicamento exista
		if(medicamento != null){
			
			medicamento.setIndRevisaoCadastro(Boolean.TRUE);
			this.getFarmaciaApoioFacade().efetuarAlteracao(medicamento, nomeMicrocomputador, new Date());
			
		} else{
			
			AfaMedicamento novoMedicamento = new AfaMedicamento();
			
			novoMedicamento.setScoMaterial(material);
			novoMedicamento.setMatCodigo(material.getCodigo());
			novoMedicamento.setDescricao(material.getNome());
			
			novoMedicamento.setIndSituacao(DominioSituacaoMedicamento.P);
	        
	        novoMedicamento.setIndCalcDispensacaoFracionad(false);
	        novoMedicamento.setIndPadronizacao(false);
	        novoMedicamento.setIndPermiteDoseFracionada(false);
	        novoMedicamento.setIndSobraReaproveitavel(false);
	        novoMedicamento.setIndExigeObservacao(false);
	        novoMedicamento.setIndRevisaoCadastro(false);
	        novoMedicamento.setIndInteresseCcih(false);
	        
	    	novoMedicamento.setIndGeladeira(false);
			novoMedicamento.setIndUnitariza(false);
			
	        novoMedicamento.setIndDiluente(false);
	        novoMedicamento.setIndGeraDispensacao(true);
	        novoMedicamento.setIndControlado(false);
	        
	        // #44033 - Erro ao efetuar cadastro de materiais (default true)
	        novoMedicamento.setPermitePrescricaoAmbulatorial(true);
	        
			// Insere um novo registro de medicamento relacionado ao material
			this.getFarmaciaApoioFacade().efetuarInclusao(novoMedicamento, nomeMicrocomputador, new Date());
		}

	}
	
	/*
	 * RNs Atualizar
	 */
	
	/**
	 * Se a situacao foi modificada atualiza o servidor desativado de acordo com o servidor logado
	 * @param material
	 * @param old
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarMaterialServidorLogadoSituacaoModificada(ScoMaterial material, ScoMaterial old) throws ApplicationBusinessException{

		if(CoreUtil.modificados(material.getIndSituacao(), old.getIndSituacao())){
	
			// Atualiza o servidor desativado quando a situação do material passar de ativa para inativa
			if(DominioSituacao.A.equals(old.getIndSituacao()) && DominioSituacao.I.equals(material.getIndSituacao())){
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				material.setServidorDesativado(servidorLogado);
			}
			
			// Limpa o servidor desativado quando a situação do material passar de inativa para ativa
			if(DominioSituacao.I.equals(old.getIndSituacao()) && DominioSituacao.A.equals(material.getIndSituacao())){
				material.setServidorDesativado(null);
			}	

		}
		
	}
	
	/**
	 * Atualiza a data de desativação do material considerando a situação anterior e a atual do mesmo
	 * @param material
	 * @param old
	 */
	protected void atualizarMaterialDataDesativacao(ScoMaterial material, ScoMaterial old){
	
		// Atualiza a data de desativação quando a situação do material passar de ativa para inativa
		if(DominioSituacao.A.equals(old.getIndSituacao()) && DominioSituacao.I.equals(material.getIndSituacao())){
			material.setDtDesativacao(new Date());
		}
		
		// Limpa a data de desativação quando a situação do material passar de inativa para ativa
		if(DominioSituacao.I.equals(old.getIndSituacao()) && DominioSituacao.A.equals(material.getIndSituacao())){
			material.setDtDesativacao(null);
		}	
		
	}
	
	/**
	 * Atualiza a situação da revisão de cadastro nos medicamentos
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void atualizarRevisaoCadastroMedicamentos(ScoMaterial material, ScoMaterial old, String nomeMicrocomputador) throws BaseException{

		if(CoreUtil.modificados(material.getNome(), old.getNome()) ||
				CoreUtil.modificados(material.getUnidadeMedida(), old.getUnidadeMedida()) || 
				CoreUtil.modificados(material.getGrupoMaterial(), old.getGrupoMaterial()) ||
				CoreUtil.modificados(material.getIndSituacao(), old.getIndSituacao())){

			final AghParametros parametroGrMatMedic = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_MEDIC);

			final boolean isGrupMaterialCodigoAntigoIgualParametro = old.getGrupoMaterial().getCodigo().equals(parametroGrMatMedic.getVlrNumerico().intValue());
			
			if (isGrupMaterialCodigoAntigoIgualParametro){
				
				this.gerarAtualizarMedicamentoMaterial(material, nomeMicrocomputador);
			}
			
		}
		
	}

	/**
	 * Verifica se o material tem saldo em estoque e sua situação é válida
	 * Material com saldo em estoque não deve ser desativado!
	 * @param material
	 * @param old 
	 * @throws BaseException
	 */
	protected void validarSaldoEstoqueMaterialDesativado(ScoMaterial material, ScoMaterial old)  throws BaseException{
		
		if(DominioSituacao.A.equals(old.getIndSituacao()) && DominioSituacao.I.equals(material.getIndSituacao())){
			
			final Boolean isExisteSaldo = this.getSceEstoqueAlmoxarifadoRN().existeSaldoEstoqueAlmoxarifado(material.getCodigo(), null, null);
			
			if (isExisteSaldo) {
				throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_MATERIAL_SALDO_ESTOQUE_DESATIVADO);
			}
			
		}		
	}
	
	/**
	 * Atualiza o almoxarifado de acordo com o novo local de estoque
	 * @param material
	 * @param old 
	 * @throws BaseException
	 */
	protected void atualizarAlmoxarifadoLocalEstoqueModificado(ScoMaterial material, ScoMaterial old)  throws BaseException{
		
		if(CoreUtil.modificados(material.getAlmoxarifado(), old.getAlmoxarifado())){
			material.setAlmLocalEstq(material.getAlmoxarifado());
		}
	}
	
	/**
	 * Valida um novo local de estoque do material
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void validarNovoLocalAlmoxarifadoEstoqueModificado(ScoMaterial material, ScoMaterial old)  throws BaseException{
		
		if(CoreUtil.modificados(material.getAlmLocalEstq(), old.getAlmLocalEstq())){
			Short materialId = null;
			
			if (material.getAlmLocalEstq() != null) {
				materialId = material.getAlmLocalEstq().getSeq().shortValue();
			}
			
			final Boolean isExisteAlmoxarifadoCadastrado = this.getSceAlmoxarifadoDAO().existeAlmoxarifadoCadastrado(materialId);
			if (!isExisteAlmoxarifadoCadastrado) {
				throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_CADASTRADO);
			}

			/* TODO Verificar o uso!
			List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisaEstoqueAlmoxarifadoPorMaterialAlmoxarifadoFornecedor(material.getCodigo(), material.getAlmSeqLocalEstq(), 1);
			if(listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty() && !listaEstoqueAlmoxarifado.get(0).getIndEstocavel()){
				throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_ANTES_ALTERAR_LOCAL_ESTOQUE_TRANSFORME);
			}*/
			
		}
		validarAlmoxarifadoInformado(material);
	}
	
	/**
	 * Valida uma nova unidade de medida do material
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void validarUnidadeMedidaModificada(ScoMaterial material, ScoMaterial old)  throws BaseException{
		
		if(CoreUtil.modificados(material.getUnidadeMedida(), old.getUnidadeMedida())){
			this.validarCondicoesTrocaUnidadeMedidaMaterial(material, old.getUnidadeMedida().getCodigo());
		}
	}
	
	/**
	 * Se a situação do indicador de envio de produção interna e o CCIH não for modificado,
	 * o servidor alterado é atualizado de acordo com o servidor logado
	 * @param material
	 * @param old
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarMaterialServidorLogadoAlteracao(ScoMaterial material, ScoMaterial old) throws ApplicationBusinessException{
	
		if(!CoreUtil.modificados(material.getIndEnvioProdInterna(), old.getIndEnvioProdInterna()) 
				&& !CoreUtil.modificados(material.getIndCcih(), old.getIndCcih())){

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			material.setServidorAlteracao(servidorLogado);
			material.setDtAlteracao(new Date());
		}
		
	}

	
	/**
	 * Remove classificações de grupo material antigas e insere/gera uma nova para o material atual
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void atualizarClassificacaoMaterialGrupoScoMaterial(ScoMaterial material, ScoMaterial old, String nomeMicrocomputador) throws BaseException{
	
		// Verifica alterações no código grupo de material
		if(CoreUtil.modificados(material.getGrupoMaterial(), old.getGrupoMaterial())){
			
			// Obtem todas classificações de grupo de material através do material
			List<ScoMateriaisClassificacoes> listaMateriaisClassificacoes = this.getComprasFacade().pesquisarScoMateriaisClassificacoesPorMaterial(material.getCodigo());
			
			// Remove as classificaões de grupo de material antigas
			for (ScoMateriaisClassificacoes materiaisClassificacoes : listaMateriaisClassificacoes) {
				this.getScoMateriaisClassificacaoRN().remover(materiaisClassificacoes);
			}
			
			// Insere (Gera) uma nova classificação de grupo de material
			this.gerarClassificacaoGrupoMaterial(material, nomeMicrocomputador);

		}
		
	}
	
	/**
	 * Valida alteração do material procedimentos hospitalares internos no faturamento.
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void validarPosAlterarMaterialFaturamentoProcedimentosHospitalaresInternos(ScoMaterial material, ScoMaterial old) throws BaseException{

		final AghParametros parametroGrpoMatOrdProt = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);

		// Verifica alterações no indicador da situação faturável
		if(CoreUtil.modificados(material.getIndFaturavel(), old.getIndFaturavel())){
			
			// Caso a situação do material seja faturável
			if(DominioSimNao.S.equals(material.getIndFaturavel())){
				
				// Insere/Altera material nos procedimentos hospitalares internos do faturamento 
				this.inserirAlterarMaterialFaturamentoProcedimentosHospitalaresInternos(material);
				
			} 

		} else{
			
			if(DominioSimNao.S.equals(material.getIndFaturavel()) || material.getGrupoMaterial().getCodigo().equals(parametroGrpoMatOrdProt.getVlrNumerico().intValue())){

				// Verifica alterações na situação do material
				if(CoreUtil.modificados(material.getIndSituacao(), old.getIndSituacao())){
					
					// Alterar material nos procedimentos hospitalares internos do faturamento 
					this.atualizarMaterialFaturamentoProcedimentosHospitalaresInternos(material);
					
				}
				
				// Verifica alterações no nome do material
				if(CoreUtil.modificados(material.getNome(), old.getNome())){
					
					// Inserir material nos procedimentos hospitalares internos do faturamento
					this.atualizarMaterialFaturamentoProcedimentosHospitalaresInternos(material);
					
				}

			}
			
		}
		
	}	
	
	/**
	 * Atualiza o estoque almoxarifado de acordo com a situação estocável do material
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void atualizarEstoqueAlmoxarifadoMaterialEstocavel(ScoMaterial material, ScoMaterial old, String nomeMicrocomputador) throws BaseException{
	
		// Verifica alterações no indicador da situação de estoque
		if(CoreUtil.modificados(material.getIndEstocavel(), old.getIndEstocavel())){
			
			// #40780
//			final AghParametros parametroAlmoxCentral = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);
//			final Short seqAlmoxarifadoCentral  = parametroAlmoxCentral.getVlrNumerico().shortValue();
			
			 List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorMaterialAlmoxarifadoCentral(material.getCodigo(), material.getAlmoxarifado().getSeq());
			
			 if(listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()){
				 
				 for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifado) {
					 
					 // Atualiza o estoque almoxarifado de acordo com a situação estocável do material
					 estoqueAlmoxarifado.setIndEstocavel(material.getIndEstocavelBoolean());
					 this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
					
				}
				 
			 } else{
				 throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_CADASTRADO); 
			 }
		}
			
	}		
	
	/**
	 * Valida a situaçãp do material
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void validarSituacaoMaterial(ScoMaterial material, ScoMaterial old, String nomeMicrocomputador) throws BaseException{
		
		// Verifica alterações na situação do material
		if(CoreUtil.modificados(material.getIndSituacao(), old.getIndSituacao())){
			
			// Para situação inativa
			if(DominioSituacao.I.equals(material.getIndSituacao())){
				// SCOK_MAT_RN.RN_MATP_VER_SIT_MAT
				this.validarCondicoesSituacaoMaterial(material); 
				// SCEK_EAL_RN.RN_EALP_ATU_SIT_ESTQ
				this.getSceEstoqueAlmoxarifadoRN().desativarEstoqueAlmoxarifadoPorMaterialSemSaldo(material, nomeMicrocomputador);
			}
			
			// Para situação ativa
			if(DominioSituacao.A.equals(material.getIndSituacao())){
				// SCEK_EAL_RN.RN_EALP_ATU_SIT_ATIV
				this.getSceEstoqueAlmoxarifadoRN().ativarEstoqueAlmoxarifadoPorMaterial(material, nomeMicrocomputador);
			}
			
			
		}

	}
	
	/**
	 * ORADB PROCEDURE SCOK_MAT_RN.RN_MATP_VER_TROCA_UN
	 * @param material
	 * @param codigoUnidadeMedidaAnterior
	 * @throws BaseException
	 */
	protected void validarCondicoesTrocaUnidadeMedidaMaterial(ScoMaterial material, final String codigoUnidadeMedidaAnterior) throws BaseException{
		CoreUtil.validaParametrosObrigatorios(material);
		this.validarCondicoesMaterial(material, codigoUnidadeMedidaAnterior);
	}
	
	/**
	 * ORADB PROCEDURE SCOK_MAT_RN.RN_MATP_VER_SIT_MAT
	 * @param material
	 * @throws BaseException
	 */
	protected void validarCondicoesSituacaoMaterial(ScoMaterial material) throws BaseException{
		CoreUtil.validaParametrosObrigatorios(material);
		this.validarCondicoesMaterial(material, null);
	}
	
	/**
	 * Este método reutiliza as operações das PROCEDURES SCOK_MAT_RN.RN_MATP_VER_TROCA_UN e SCOK_MAT_RN.RN_MATP_VER_SIT_MAT
	 * @param material
	 * @param codigoUnidadeMedidaAnterior considera a unidade de medida anterior nas consultas das validações, quando a chamada deste método ocorre no pós-atualizar
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validarCondicoesMaterial(ScoMaterial material, final String codigoUnidadeMedidaAnterior) throws BaseException{
		
		// Pesquisa estoque almoxarifado através do material e que contenha QUANTIDADE DISPONÍVEL OU BLOQUEADA
		List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado =  this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoQuantidadeDisponivelBloqueadaPorMaterial(material.getCodigo());
		if(listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()){
			
			final SceEstoqueAlmoxarifado estoqueAlmoxarifado = listaEstoqueAlmoxarifado.get(0);
			final Short seqAlmoxarifado = estoqueAlmoxarifado.getAlmoxarifado().getSeq();
			final Integer numerofornecedor = estoqueAlmoxarifado.getFornecedor().getNumero();
			
			throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_MATERIAL_SALDO_ALMOXARIFADO, seqAlmoxarifado, numerofornecedor);
		}

		// Verifica se há material com SALDO BLOQUEADO POR PROBLEMA no almoxarifado
		List<SceHistoricoProblemaMaterial> listaHistoricoProblemaMaterial = this.getSceHistoricoProblemaMaterialDAO().pesquisarMaterialSaldoBloqueadoProblemaAlmoxarifado(material.getCodigo());
		if (listaHistoricoProblemaMaterial != null && !listaHistoricoProblemaMaterial.isEmpty()) {
			
			final SceHistoricoProblemaMaterial historicoProblemaMaterial = listaHistoricoProblemaMaterial.get(0);
			final Short seqAlmoxarifado = historicoProblemaMaterial.getSceEstqAlmox().getAlmoxarifado().getSeq();
			final Integer numerofornecedor = historicoProblemaMaterial.getSceEstqAlmox().getFornecedor()!=null?historicoProblemaMaterial.getSceEstqAlmox().getFornecedor().getNumero():0;
			
			throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_MATERIAL_SALDO_BLOQUEADO_PROBLEMA_ALMOXARIFADO, seqAlmoxarifado, numerofornecedor);
		}

		// Pesquisa DOCUMENTOS com UNIDADE ANTERIOR não encerrados
		List<SceEntradaSaidaSemLicitacao> listaEntradaSaidaSemLicitacao = this.getSceEntradaSaidaSemLicitacaoDAO().pesquisarDocumentoUnidadeAntigaNaoEncerrado(material.getCodigo(), codigoUnidadeMedidaAnterior);
		if (listaEntradaSaidaSemLicitacao != null && !listaEntradaSaidaSemLicitacao.isEmpty()) {
			
			StringBuilder listEntradaSaidaSemLicitacao = new StringBuilder();
			
			
			for(SceEntradaSaidaSemLicitacao entradaSaidaSemLicitacao:listaEntradaSaidaSemLicitacao){
				
				if(listEntradaSaidaSemLicitacao.length()==0){
					listEntradaSaidaSemLicitacao.append(entradaSaidaSemLicitacao.getSeq());
//					listEntradaSaidaSemLicitacao.append(" ("+entradaSaidaSemLicitacao.getSceTipoMovimento().getSigla()+")");
							
				}else{
					listEntradaSaidaSemLicitacao.append(", ");
					listEntradaSaidaSemLicitacao.append(entradaSaidaSemLicitacao.getSeq());
//					listEntradaSaidaSemLicitacao.append(" ("+entradaSaidaSemLicitacao.getSceTipoMovimento().getSigla()+")");
					
				}
				
			}
			
			throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_DOCUMENTO_UNIDADE_ANTIGA_NAO_ENCERRADO,listEntradaSaidaSemLicitacao );
		}
		
		// Pesquisa AFs (AUTORIZAÇÕES DE FORNECIMENTO) com UNIDADE ANTERIOR não encerradas
		List<ScoFaseSolicitacao> listaFaseSolicitacao = this.getComprasFacade().pesquisarAutorizacaoFormAntigaNaoEncerrada(material.getCodigo(), codigoUnidadeMedidaAnterior);
		if (listaFaseSolicitacao != null && !listaFaseSolicitacao.isEmpty()) {
			
			 final ScoFaseSolicitacao faseSolicitacao = listaFaseSolicitacao.get(0); 
			 final Integer numeroLicitacao = faseSolicitacao.getItemAutorizacaoForn() != null ? faseSolicitacao.getItemAutorizacaoForn().getAutorizacoesForn().getPropostaFornecedor().getLicitacao().getNumero() : null;
			 final Short numeroComplemento = faseSolicitacao.getItemAutorizacaoForn() != null ? faseSolicitacao.getItemAutorizacaoForn().getAutorizacoesForn().getNroComplemento() : null;

			 throw new ApplicationBusinessException(ScoMaterialRNExceptionCode.MENSAGEM_AF_UNIDADE_ANTIGA_NAO_ENCERRADA, numeroLicitacao + "/" + numeroComplemento);
		 }

	}	

	/**
	 * ORADB PROCEDURE SCOK_MAT_RN.RN_MATP_ATU_ALMOX
	 * Atualiza unidade de medida do material nos almoxarifados e estoque geral
	 * @param material
	 * @throws BaseException
	 */
	protected void atualizarUnidadeMedidaMaterialEstoqueAlmoxarifadoGeral(ScoMaterial material, String nomeMicrocomputador) throws BaseException{
		
		//  Atualiza unidade de medida do material nos almoxarifados
		List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorMaterial(material.getCodigo());
		if(listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()){
			for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifado) {
				estoqueAlmoxarifado.setUnidadeMedida(material.getUnidadeMedida());
				estoqueAlmoxarifado.setMaterial(material);
				this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
			}
		 }
		
		//  Atualiza unidade de medida do material no estoque geral
		List<SceEstoqueGeral> listaEstoqueGeral = this.getSceEstoqueGeralDAO().pesquisarEstoqueGeralPorMaterial(material);
		if(listaEstoqueGeral != null && !listaEstoqueGeral.isEmpty()){
			AghParametros paramCompetencia =  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
			if (paramCompetencia != null && paramCompetencia.getVlrData() != null) {
				for (SceEstoqueGeral estoqueGeral : listaEstoqueGeral) {
					if (estoqueGeral.getId().getDtCompetencia().equals(paramCompetencia.getVlrData())) {
						if(CoreUtil.modificados(material.getUnidadeMedida(), estoqueGeral.getUnidadeMedida())){
							estoqueGeral.setUnidadeMedida(material.getUnidadeMedida());
							estoqueGeral.setMaterial(material);
							this.getSceEstoqueGeralRN().atualizar(estoqueGeral);
						}				
					}
				}
			}
		}
	}
	
	/**
	 * ORADB PROCEDURE SCOK_MAT_RN.RN_MATP_VER_CONTRATO
	 * Verifica se o material está em contrato
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void validarMaterialEmContrato(ScoMaterial material, ScoMaterial old, String nomeMicrocomputador) throws BaseException{

		// Pesquisa material cadastrado no almoxarifado com solicitação de compra
		final List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifadoSolicitacaoDeCompra = this.getSceEstoqueAlmoxarifadoDAO().pesquisarMaterialCadastradoAlmoxarifadoComSolicitacaoCompra(material.getCodigo(), material.getAlmoxarifado().getSeq(), this.getScoFornecedorPadrao().getNumero());

		if(listaEstoqueAlmoxarifadoSolicitacaoDeCompra != null && !listaEstoqueAlmoxarifadoSolicitacaoDeCompra.isEmpty()){
			
			// Pesquisa material cadastrado no almoxarifado 
			List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarMaterialCadastradoAlmoxarifado(material.getCodigo(), material.getAlmoxarifado().getSeq(), getScoFornecedorPadrao().getNumero());
			if(listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()){
				for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifado) {
					// Atualiza solicitação de compra
					if(CoreUtil.modificados(material.getUnidadeMedida(), estoqueAlmoxarifado.getUnidadeMedida())){
						estoqueAlmoxarifado.setUnidadeMedida(material.getUnidadeMedida());
						estoqueAlmoxarifado.setMaterial(material);

						this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
					}
				}
			}

			// Pesquisa material cadastrado no almoxarifado com solicitação de compra
			List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifadoNovaSolicitacaoDeCompra = this.getSceEstoqueAlmoxarifadoDAO().pesquisarMaterialCadastradoAlmoxarifadoComSolicitacaoCompra(material.getCodigo(), old.getAlmoxarifado().getSeq(),  this.getScoFornecedorPadrao().getNumero());
			if(listaEstoqueAlmoxarifadoNovaSolicitacaoDeCompra != null && !listaEstoqueAlmoxarifadoNovaSolicitacaoDeCompra.isEmpty()){
				for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifadoNovaSolicitacaoDeCompra) {
					// Atualiza almoxarifado
					estoqueAlmoxarifado.setAlmoxarifado(old.getAlmoxarifado());
					this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
				}
			}
			
		}
		
	}
	
	/**
	 * Valida se o material está cadastrado no almoxarifado
	 * @param material
	 * @param old
	 * @throws BaseException
	 */
	protected void validarMaterialCadastradoAlmoxarifado(ScoMaterial material, ScoMaterial old)  throws BaseException{
		
		// Verifica alterações no almoxarifado
		if(CoreUtil.modificados(material.getAlmoxarifado().getSeq(), old.getAlmoxarifado().getSeq())){
			
			List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorMaterialAlmoxarifadoFornecedor(material.getCodigo(), material.getAlmoxarifado().getSeq(),  this.getScoFornecedorPadrao().getNumero());
			if(listaEstoqueAlmoxarifado != null && listaEstoqueAlmoxarifado.isEmpty()){
				
				// Gera um registro de SceEstoqueAlmoxarifado
				this.gerarEstoqueAlmoxarifadoMaterial(material);

			}
		}
	
	}
	
	/**
	 * Os métodos a seguir dizem respeito aos procedimentos hospitalares internos no faturamento do material
	 * Ambos foram divididos para o reuso nas operações de inserir e atualizar material
	 */
	
	/**
	 * Insere/Altera material nos procedimentos hospitalares internos do faturamento 
	 * @param material
	 * @throws BaseException
	 */
	protected void inserirAlterarMaterialFaturamentoProcedimentosHospitalaresInternos(ScoMaterial material) throws BaseException{
		
		// Verifica existência do procedimento hospitalar (scok_mat_rn.rn_matc_ver_phi)
		final boolean isExistenciaProcedimentoHospitalar = this.getFaturamentoFacade().verificaProcedimentoHospitalarInterno(material.getCodigo());
		if (isExistenciaProcedimentoHospitalar){
			
			// Alterar material nos procedimentos hospitalares internos do faturamento 
			this.atualizarMaterialFaturamentoProcedimentosHospitalaresInternos(material);
			
		} else{
			
			// Inserir material nos procedimentos hospitalares internos do faturamento
			this.inserirMaterialFaturamentoProcedimentosHospitalaresInternos(material);
			
		}
	}
	
	/**
	 * Alterar material nos procedimentos hospitalares internos do faturamento 
	 * @param material
	 * @throws BaseException
	 */
	private void atualizarMaterialFaturamentoProcedimentosHospitalaresInternos(ScoMaterial material) throws BaseException{
		
		FatProcedHospInternos fatProcedHospInternos = this.getFaturamentoFacade().obterFatProcedHospInternosPorMaterial(material.getCodigo());
		
		if (fatProcedHospInternos != null) {
			
			fatProcedHospInternos.setMaterial(material);
			fatProcedHospInternos.setDescricao(material.getNome());
			fatProcedHospInternos.setSituacao(material.getIndSituacao());

			getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);
			
		}
		
	}	
	
	/**
	 * Inserir material nos procedimentos hospitalares internos do faturamento
	 * @param material
	 * @throws BaseException
	 */
	private void inserirMaterialFaturamentoProcedimentosHospitalaresInternos(ScoMaterial material) throws BaseException{
		
		FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();
		fatProcedHospInternos.setMaterial(material);
		fatProcedHospInternos.setDescricao(material.getNome());
		fatProcedHospInternos.setSituacao(material.getIndSituacao());

		getFaturamentoFacade().inserirFatProcedHospInternos(fatProcedHospInternos);
	}
	
	
	/**
	 * Retorna o fornecedor padrão HCPA/HU
	 * @return
	 * @throws BaseException
	 */
	private ScoFornecedor getScoFornecedorPadrao() throws BaseException{
		AghParametros parametroFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		return this.getComprasFacade().obterFornecedorPorNumero(parametroFornecedor.getVlrNumerico().intValue());
	}
	
	
	public Double getUltimoValorCompra(ScoMaterial scoMaterial)
			throws ApplicationBusinessException {
		
		scoMaterial = this.comprasFacade.obterMaterialPorId(scoMaterial.getCodigo());
		scoMaterial.setGrupoMaterial(this.comprasFacade.obterGrupoMaterialPorId(scoMaterial.getGrupoMaterial().getCodigo()));
				
		if (scoMaterial.getIndGenericoBoolean() == false
				&& scoMaterial.getGrupoMaterial().getPatrimonio() == false) {			
			AghParametros parametroTipoMovimento = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);		     
			return getSceMovimentoMaterialDAO().buscarUltimoCustoEntradaPorMaterialTipoMov(
					scoMaterial, parametroTipoMovimento.getVlrNumerico()
							.shortValue());
		} else {
			return null;
		}
	}
	
	private BigDecimal nvl(BigDecimal valor){
		return (valor != null) ? valor : BigDecimal.ZERO;
	}
	
	private BigDecimal somaPorTipoMovimento(BigDecimal valor, MovimentoMaterialVO item){
		if(item.getTipoMovimento() == 05){
			valor = valor.add(nvl(item.getValor()));
		} else {
			valor = valor.add(nvl(item.getValor()).negate());
		}
		return valor;
	}
	
	/**
     * Método que calcula o total de cada function.
     * @param listaSceMovimentoMaterial
     * @return
     */
	private BigDecimal somarTotalGeral(
			List<MovimentoMaterialVO> listaSceMovimentoMaterial,
			Boolean somaTipoMovimento) {
		BigDecimal totalGeral = BigDecimal.ZERO;
		if (listaSceMovimentoMaterial != null
				&& !listaSceMovimentoMaterial.isEmpty()) {
			BigDecimal somaValoresN = BigDecimal.ZERO;
			BigDecimal somaValoresS = BigDecimal.ZERO;
			for (MovimentoMaterialVO sceMovimentoMaterial : listaSceMovimentoMaterial) {
				if (!sceMovimentoMaterial.getEstornado()) {
					if (somaTipoMovimento) {
						somaValoresN = somaPorTipoMovimento(somaValoresN,
								sceMovimentoMaterial);
					} else {
						somaValoresN = somaValoresN
								.add(nvl(sceMovimentoMaterial.getValor()));
					}
				} else {
					if (somaTipoMovimento) {
						somaValoresS = somaPorTipoMovimento(somaValoresS,
								sceMovimentoMaterial);
					} else {
						somaValoresS = somaValoresS
								.add(nvl(sceMovimentoMaterial.getValor()));
					}
				}
			}
			totalGeral = somaValoresN.subtract(somaValoresS);
		}
		return totalGeral;
	}
	
	/**
     * Function F1 da estória #6635 
      * ORADB FUNCTION TOT_GERAL_COMPRAS
     * @param dataGeracao
     * @return BigDecimal
     */
     public BigDecimal obterTotalGeralComprasDia(Date dataGeracao) throws ApplicationBusinessException{
           List<MovimentoMaterialVO> listaSceMovimentoMaterial = getScoMaterialDAO()
                         .consultarMovimentosMateriais(dataGeracao, new String[]{AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.FALSE,
                                      Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
           BigDecimal totalGeralComprasDia = BigDecimal.ZERO;
           totalGeralComprasDia = somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
           return totalGeralComprasDia;
     }
     
     /**
     * Function F2 da estória #6635
     * ORADB_TOT_GERAL_DEVOLUCOES
     * @param dataGeracao
     * @return BigDecimal
     */
     public BigDecimal obterTotalGeralDevolucoesDia(Date dataGeracao) throws ApplicationBusinessException{
           BigDecimal totalGeralDevolucoesDia = BigDecimal.ZERO;
           List<MovimentoMaterialVO> listaSceMovimentoMaterial = getScoMaterialDAO()
                         .consultarMovimentosMateriais(dataGeracao, new String[]{"P_TMV_DOC_DF"}, Boolean.FALSE,
                                      Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
           totalGeralDevolucoesDia = somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
           return totalGeralDevolucoesDia;
           
     }
     
     /**
     * Function F3 da estória #6635
     * ORADB_TOT_GERAL_DIFERENCA
     * @param dataGeracao
     * @return BigDecimal
     */
     public BigDecimal obterTotalDiferencaFormula(Date dataGeracao) throws ApplicationBusinessException{
           return obterTotalGeralComprasDia(dataGeracao).subtract(obterTotalGeralDevolucoesDia(dataGeracao));
     }
     
     /**
     * Function F4 da estória #6635
     * ORADB_ENTR_CONSUMO_DIA
     * @param dataGeracao
     * @param tmvDocNr
     * @return BigDecimal
     */
     public BigDecimal obterQuantidadeMateriaisConsumidosDia(Date dataGeracao) throws ApplicationBusinessException{
           BigDecimal totalQtdMateriais = BigDecimal.ZERO;
           List<MovimentoMaterialVO> listaSceMovimentoMaterial = getScoMaterialDAO()
                         .consultarMovimentosMateriais(dataGeracao, new String[]{AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
           totalQtdMateriais = somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
           return totalQtdMateriais;
     }
     
     /**
     * Function F6 da estória #6635
     * ORADB_ENTR_PATR_DIA
     * @param dataGeracao
     * @param tmvDocNr
     * @return BigDecimal
     */
     public BigDecimal obterQuantidadePatrimonioEntradaDia(Date dataGeracao) throws ApplicationBusinessException{
           BigDecimal totalQtdPatrimonios = BigDecimal.ZERO;
           List<MovimentoMaterialVO> listaSceMovimentoMaterial = getScoMaterialDAO()
                         .consultarMovimentosMateriais(dataGeracao, new String[]{AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
           totalQtdPatrimonios = somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
           return totalQtdPatrimonios;
     }
     
     /**
     * Function F7 da estória #6635
     * ORADB_ENTR_ACUM_CONSUMO_MES
     * @param dataGeracao
     * @param tmvDocNr
     * @return BigDecimal
     */
     public BigDecimal obterQuantidadeMateriaisConsumoEntradaDia(Date dataGeracao) throws ApplicationBusinessException{
           BigDecimal totalQtdMateriaisConsumoEntrada = BigDecimal.ZERO;
           List<MovimentoMaterialVO> listaSceMovimentoMaterial = getScoMaterialDAO()
                         .consultarMovimentosMateriais(dataGeracao, new String[]{AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
           totalQtdMateriaisConsumoEntrada = somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
           return totalQtdMateriaisConsumoEntrada;
     }
     
     /**
     * Function F9 da estória #6635
     * ORADB_ENTR_ACUM_PATR_MES
     * @param dataGeracao
     * @param tmvDocNr
     * @return BigDecimal
     */
     public BigDecimal obterQuantidadeServicosEntradaMes(Date dataGeracao) throws ApplicationBusinessException{
           BigDecimal totalQtdServicos = BigDecimal.ZERO;
           List<MovimentoMaterialVO> listaSceMovimentoMaterial = getScoMaterialDAO()
                         .consultarMovimentosMateriais(dataGeracao, new String[]{AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
           totalQtdServicos = somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
           return totalQtdServicos;
     }
	
	/**
	 * #6635 F10
	 * ORADB_CONSUMO_MAT_DIA
	 * @param dataGeracao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal consumoMatDiaFormula(Date dataGeracao) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = getScoMaterialDAO().consultarMovimentosMateriais(dataGeracao, new String[] {"P_TMV_DOC_DA", "P_TMV_DOC_RM"}, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.TRUE).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * #6635 F11
	 * ORADB_CONSUMO_MAT_MES
	 * @param dataGeracao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal consumoMatMesFormula(Date dataGeracao) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().consultarMovimentosMateriais(dataGeracao, new String[] {AghuParametrosEnum.P_TMV_DOC_DA.toString(), AghuParametrosEnum.P_TMV_DOC_RM.toString()}, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.TRUE).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * #6635 F12
	 * ORADB_GMT_ENTR_MAT_EST_DIA
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtEntradaMateriaisEstocaveisDiaFormula (Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.TRUE, Boolean.FALSE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
	}
	
	/**
	 * #6635 F13
	 * ORADB_GMT_ENTR_MAT_DIR_DIA
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtEntradaMaterialDiretoDiaFormula (Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.FALSE, Boolean.FALSE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
	}
	
	/**
	 * #6635 F14
	 * ORADB_GMT_ENTR_ACUM_MAT_EST_MES
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtEntradaAcumuladoMaterialEstocavelMesFormula (Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.TRUE, Boolean.TRUE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
	}
	
	/**
	 * #6635 F15
	 * ORADB_GMT_CONS_ACUM_MAT_MES
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtEntradaAcumuladoMaterialDiretoMesFormula (Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_NR.toString()}, Boolean.FALSE, Boolean.TRUE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.FALSE);
	}
	
	/**
	 * #6635 F16
	 * ORADB_CF_ENTR_ACUM_MAT_MES
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal cfEntradaAcumuladoMaterialMesFormula(Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		return gmtEntradaAcumuladoMaterialEstocavelMesFormula(dataGeracao, codigoGrupoMaterial)
				.add(gmtEntradaAcumuladoMaterialDiretoMesFormula(dataGeracao, codigoGrupoMaterial));
	}
	
	/**
	 * #6635 F17
	 * ORADB_GMT_CONS_MAT_EST_DIA
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtConsumoMaterialEstocavelDiaFormula(Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_DA.toString(), AghuParametrosEnum.P_TMV_DOC_RM.toString()}, Boolean.TRUE, Boolean.FALSE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.TRUE).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * #6635 F18
	 * ORADB_GMT_CONS_MAT_DIR_DIA
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtConsumoMaterialDiretoDiaFormula(Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_DA.toString(), AghuParametrosEnum.P_TMV_DOC_RM.toString()}, Boolean.FALSE, Boolean.FALSE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.TRUE).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * #6635 F19
	 * ORADB_GMT_CONS_ACUM_MAT_EST_MES
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtConsumoAcumuladoMaterialEstocavelMesFormu(Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_DA.toString(), AghuParametrosEnum.P_TMV_DOC_RM.toString()}, Boolean.TRUE, Boolean.TRUE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.TRUE).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * #6635 F20
	 * ORADB_GMT_CONS_ACUM_MAT_DIR_MES
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal gmtConsumoAcumuladoMaterialDiretoMesFormu(Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		List<MovimentoMaterialVO> listaSceMovimentoMaterial = 
				getScoMaterialDAO().gmtEntradaMateriaisFormula(dataGeracao, codigoGrupoMaterial, new String[] {AghuParametrosEnum.P_TMV_DOC_DA.toString(), AghuParametrosEnum.P_TMV_DOC_RM.toString()}, Boolean.FALSE, Boolean.TRUE);
		return somarTotalGeral(listaSceMovimentoMaterial, Boolean.TRUE).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * #6635 F21
	 * ORADB_CF_CONS_ACUM_MAT_MES
	 * @param dataGeracao
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal cfConsumoAcumuladoMaterialMesFormula(Date dataGeracao, Integer codigoGrupoMaterial) throws ApplicationBusinessException{
		return gmtConsumoAcumuladoMaterialEstocavelMesFormu(dataGeracao, codigoGrupoMaterial)
				.add(gmtConsumoAcumuladoMaterialDiretoMesFormu(dataGeracao, codigoGrupoMaterial));
	}
	

	/**
	 * Getters para RNs e DAOs
	 */
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return farmaciaApoioFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}
	
	protected SceEntradaSaidaSemLicitacaoDAO getSceEntradaSaidaSemLicitacaoDAO() {
		return sceEntradaSaidaSemLicitacaoDAO;
	}
	
	protected SceEstoqueGeralRN getSceEstoqueGeralRN() {
		return sceEstoqueGeralRN;
	}

	public ScoMateriaisClassificacaoRN getScoMateriaisClassificacaoRN(){
		return scoMateriaisClassificacaoRN;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
	
	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO() {
		return sceEstoqueGeralDAO;
	}
	
	protected SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN(){
		return sceEstoqueAlmoxarifadoRN;
	}
	
	private SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}
	
	public void setScoMaterialDAO(ScoMaterialDAO scoMaterialDAO) {
		this.scoMaterialDAO = scoMaterialDAO;
	}

}