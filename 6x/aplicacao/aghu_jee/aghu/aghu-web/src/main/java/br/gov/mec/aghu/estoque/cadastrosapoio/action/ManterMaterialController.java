package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioIndFotoSensibilidade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ScoMaterialVO;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.prescricaomedica.action.ProcedimentoRelacionadoController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManterMaterialController extends ActionController {
	private static final String ESTOQUE_PESQUISAR_ESTOQUE_ALMOXARIFADO = "estoque-pesquisarEstoqueAlmoxarifado";
	private static final String ESTOQUE_CONSULTAR_HISTORICO_MATERIAL = "estoque-consultarHistoricoMaterial";
	private static final String PRESCRICAOMEDICA_PROCEDIMENTOS_RELACIONADOS = "prescricaomedica-procedimentosRelacionados";
	private static final String COMPRAS_CADASTRO_DESCRICAO_TECNICA_CRUD = "compras-cadastroDescricaoTecnicaCRUD";
	private static final String ESTOQUE_MATERIAL_VINCULO_CRUD = "estoque-materialVinculoCRUD";
	private static final String ESTOQUE_PESQUISAR_VALIDADE_MATERIAL = "estoque-pesquisarValidadeMaterial";
	private static final String ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";
	private static final String CONSULTAR_CATALOGO_MATERIAL = "estoque-consultarCatalogoMaterial";
	private static final String PESQUISA_MANTER_MATERIAL = "estoque-pesquisaManterMaterial";
	private static final String MANTER_ESTOQUE_ALMOXARIFADO = "estoque-manterEstoqueAlmoxarifado";
	private static final String PESQUISAR_ESTOQUE_ALMOXARIFADO = ESTOQUE_PESQUISAR_ESTOQUE_ALMOXARIFADO;
	private static final Log LOG = LogFactory.getLog(ManterMaterialController.class);
	private static final long serialVersionUID = -539894874954388678L;
	@EJB
	private IEstoqueFacade estoqueFacade;
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	@EJB
	private IComprasFacade comprasFacade;
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	@EJB
	protected IParametroFacade parametroFacade;
	@EJB
	protected ICentralPendenciaFacade centralPendenciaFacade;
    @EJB
    private ICascaFacade cascaFacade;
	private List<VFatConvPlanoGrupoProcedVO> processosRelacionados;
	private List<FatConvGrupoItemProced> listaClones;
	@Inject
	private RelacionarPHISSMController relacionarPHISSMController;
	private Integer codigo;
	private String voltarPara = "estoque-pesquisaManterMaterial"; // O padrão é voltar para interface de pesquisa
	private ScoMaterialVO materialVO;
	private Boolean indEstocavel;
	private Boolean indPadronizado;
	private Boolean indGenerico;
	private Boolean indControleValidade;
	private Boolean indMenorPreco;
	private Boolean indAtuQtdeDisponivel;
	private Boolean indFaturavel;
	private Boolean indCatMat;
	private Boolean indConfaz;
	private Boolean indCapCmed;
    private Boolean existeJustificativaAnterior = false;
    private Boolean justificativaConfirmada = false;
	private boolean emEdicao;
	private String origem;
	private Boolean habilitarBotaoHistorico;
	private Boolean sliderInfoFiscaisAberto;
	private Boolean exibirConfaz;
	private Boolean exibirSliderInfoFiscais;
	private Boolean integracaoDescricaoTecnica = Boolean.FALSE;
	private FatProcedHospInternos procedimentoInterno;
	private boolean criadoNovoMaterial;
	private Integer codigoMaterialPrincipal;
	private Boolean justificado  = Boolean.FALSE;
	private Integer serMatriculaJusProcRel;
	private String justificativaProcRel;
	private Date dataJusProcRel;
	private Short serVinCodigoJusProcRel;
	private String senhaUser;
    private String textoJustificativaCatMat;
    private String usuarioJustificativaCatMat;
    private String senhaJustificativaCatMat;
	private List<FatConvGrupoItemProced> procedimentoRelacionadoCadastrado;
	private Boolean retornouProcedimentos = false;
	@Inject
	private ProcedimentoRelacionadoController procedimentoRelacionadoController;
	@Inject
	private SecurityController securityController;
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

    public enum ManterMaterialControllerExceptionCode implements BusinessExceptionCode {
        MENSAGEM_USUARIO_NAO_AUTENTICADO,
        MENSAGEM_USUARIO_SEM_PERMISSAO,
        MENSAGEM_JUSTIFICATIVA_MENOR_QUE_MINIMO;
    }

	public void inicio() throws ApplicationBusinessException {
        verificarObrigatoriedadeCatMat();
		if(!retornouProcedimentos){
			getLimpaVariaveisJustificativa();
			this.materialVO = new ScoMaterialVO();
			if (this.emEdicao || (this.codigo != null && !this.criadoNovoMaterial)) {
				final ScoMaterial material = this.comprasFacade.obterScoMaterialDetalhadoPorChavePrimaria(this.codigo);
                getDadosFromMaterial(material);
                Short cpgCphPhoSeq = null;
                Short cpgGrcSeq = null;
				try{
					AghParametros pTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
					cpgCphPhoSeq = pTabela.getVlrNumerico().shortValue();
					List<VFatConvPlanoGrupoProcedVO> listaTabela = this.faturamentoFacade.listarTabelas(cpgCphPhoSeq.toString());
					
					if (listaTabela != null && !listaTabela.isEmpty()) {
						VFatConvPlanoGrupoProcedVO tabela = listaTabela.get(0);
						cpgGrcSeq = tabela.getGrcSeq();
					}
				} catch(BaseException e){
					apresentarExcecaoNegocio(e);
				}
				setProcedimentoInterno(obterProcedimentosInternos(this.codigo));
				if(procedimentoInterno != null && procedimentoInterno.getSeq() != null){
					procedimentoRelacionadoCadastrado = faturamentoFacade.listarFatConvGrupoItensProcedPorPhi(null, null, null, null, cpgCphPhoSeq, cpgGrcSeq, procedimentoInterno.getSeq());
				}


                this.existeJustificativaAnterior =  material.getJustificativaCatmat() == null || material.getJustificativaCatmat().equals("") ? false : true ;
				textoJustificativaCatMat = material.getJustificativaCatmat();
			} else {
					this.materialVO = new ScoMaterialVO();
					this.materialVO.setIndSituacao(DominioSituacao.A); // O valor padrão da situação é ativo
					this.indEstocavel = false;
					this.indPadronizado = false;
					this.indGenerico = false;
					this.indControleValidade = true; // O valor padrão para indicação de controle de validade é ativo
					this.indMenorPreco = false;
					this.indAtuQtdeDisponivel = false;
					this.indFaturavel = false;
					this.habilitarBotaoHistorico = Boolean.FALSE;
					this.indCapCmed = Boolean.FALSE;
					this.indConfaz = Boolean.FALSE;
					this.materialVO.setIndTermolabil(Boolean.FALSE);
					this.materialVO.setIndVinculado(Boolean.FALSE);
					this.materialVO.setIndSustentavel(Boolean.FALSE);	
					procedimentoInterno = null;
					processosRelacionados = null;
					listaClones = null;
			}
			validaObrigatoriedadeCatMat();
			this.exibirSliderInfoFiscais = securityController.usuarioTemPermissao("inserirInformacoesFiscais", "gravar");
			if(this.exibirSliderInfoFiscais) {
				this.sliderInfoFiscaisAberto = Boolean.FALSE;
				this.exibirConfaz = this.estoqueFacade.habilitarConfaz();
			}
		}else{
			confirmar();
		}
	}


    private void verificarObrigatoriedadeCatMat() {
        try {

            AghParametros paramCatmat = this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_HABILITA_CATMAT);

            if(paramCatmat != null && paramCatmat.getVlrTexto().equalsIgnoreCase("S")){
                this.setIndCatMat(true);
            } else {
                this.setIndCatMat(false);
            }
        } catch (BaseException e) {
            apresentarExcecaoNegocio(e);
        }

    }
    private void getDadosFromMaterial(ScoMaterial material) {
        this.materialVO.setCodigo(this.codigo);
        this.materialVO.setNome(material.getNome());
        this.materialVO.setIndSituacao(material.getIndSituacao());
        this.materialVO.setDescricao(material.getDescricao());
        this.materialVO.setScoUnidadeMedida(material.getUnidadeMedida());
        this.materialVO.setOrigemParecerTecnico(material.getOrigemParecerTecnico());
        this.materialVO.setNumero(material.getNumero());
        this.materialVO.setScoGrupoMaterial(material.getGrupoMaterial());
        this.materialVO.setSceAlmoxarifado(material.getAlmoxarifado());
        this.materialVO.setClassifXyz(material.getClassifXyz());
        this.materialVO.setSazonalidade(material.getSazonalidade());
        this.materialVO.setIndProducaoInterna(material.getIndProducaoInterna());
        this.materialVO.setIndFotosensivel(material.getIndFotosensivel());
        this.materialVO.setIndTipoUso(material.getIndTipoUso());
        if(material.getCodCatmat() != null){
            Object codCatMat = StringUtil.adicionaZerosAEsquerda(material.getCodCatmat(), 7);
            List<ScoSiasgMaterialMestre> catmat = this.estoqueFacade.obterCatMat(codCatMat.toString());
            if(catmat != null && !catmat.isEmpty()){
            	this.materialVO.setCatMat(catmat.get(0));
            }
        }
        this.materialVO.setCodMatAntigo(material.getCodMatAntigo());
        this.materialVO.setIndCorrosivo(material.getIndCorrosivo());
        this.materialVO.setIndInflamavel(material.getIndInflamavel());
        this.materialVO.setIndRadioativo(material.getIndRadioativo());
        this.materialVO.setIndReativo(material.getIndReativo());
        this.materialVO.setIndToxico(material.getIndToxico());
        this.materialVO.setIndUtilizaEspacoFisico(material.getIndUtilizaEspacoFisico());
        this.indEstocavel = material.getIndEstocavel().isSim();
        this.indPadronizado = material.getIndPadronizado() != null ? material.getIndPadronizado().isSim() : false;
        this.indGenerico = material.getIndGenerico().isSim();
        this.indControleValidade = material.getIndControleValidade().isSim();
        this.indMenorPreco = material.getIndMenorPreco().isSim();
        this.indAtuQtdeDisponivel = material.getIndAtuQtdeDisponivel().isSim();
        this.indFaturavel = material.getIndFaturavel().isSim();
        this.materialVO.setObservacao(material.getObservacao());
        this.materialVO.setDtDigitacao(material.getDtDigitacao());
        this.materialVO.setServidor(material.getServidor());
        this.materialVO.setDtAlteracao(material.getDtAlteracao());
        this.materialVO.setServidorAlteracao(material.getServidorAlteracao());
        this.materialVO.setDtDesativacao(material.getDtDesativacao());
        this.materialVO.setServidorDesativado(material.getServidorDesativado());
        this.materialVO.setIndTipoResiduo(material.getIndTipoResiduo());
        this.materialVO.setIndTermolabil(material.getIndTermolabil());
        this.materialVO.setIndVinculado(material.getIndVinculado());
        this.materialVO.setLegislacao(material.getLegislacao());
        this.materialVO.setTemperatura(material.getTemperatura());
        this.materialVO.setIndSustentavel(material.getIndSustentavel());
        this.indConfaz = material.getIndConfaz().isSim();
        this.indCapCmed = material.getIndCapCmed().isSim();
        this.habilitarBotaoHistorico = this.comprasFacade.validarVisualizacaoHistoricoMaterial(this.codigo);
        if(material.getSerMatriculaJusProcRel() != null){
            serMatriculaJusProcRel = material.getSerMatriculaJusProcRel();
            justificativaProcRel = material.getJustificativaProcRel();
            dataJusProcRel = material.getDataJusProcRel();
            serVinCodigoJusProcRel = material.getSerVinCodigoJusProcRel();
        }
        processosRelacionados = null;
    }

    private void validaObrigatoriedadeCatMat() {
		try {
			AghParametros paramCatmat = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HABILITA_CATMAT);
			if(paramCatmat.getVlrTexto().equalsIgnoreCase("S")){
				this.setIndCatMat(true);
			} else {
				this.setIndCatMat(false);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

    public void solicitarJustificativaCatMat(){
            openDialog("panelJustificativaCatMatWG");
    }

    public void confirmaJustificativa(Boolean existeJutificava){
        if (existeJutificava) {
            justificativaConfirmada = existeJutificava;
            confirmar();
        }else {
            openDialog("panelJustificativaCatMatWG");
        }
    }

    public String getTextoJustificativaCatMat() {
        return textoJustificativaCatMat;
    }

    public void setTextoJustificativaCatMat(String textoJustificativaCatMat) {
        this.textoJustificativaCatMat = textoJustificativaCatMat;
    }

    public String validarUsuario(){
        String retorno = "";
        try {
            boolean usuarioAutenticado = cascaFacade.verificarSenha(usuarioJustificativaCatMat, senhaJustificativaCatMat);
            boolean usuarioPossuiPermnissao = cascaFacade.usuarioTemPermissao(usuarioJustificativaCatMat, "autorizadorCATMAT", "gravar");
            if (usuarioAutenticado && usuarioPossuiPermnissao){
                justificativaConfirmada = true;
                retorno = confirmar();
            } else if (!usuarioAutenticado){
                throw new BaseException(ManterMaterialControllerExceptionCode.MENSAGEM_USUARIO_NAO_AUTENTICADO);
            }else{
                throw new BaseException(ManterMaterialControllerExceptionCode.MENSAGEM_USUARIO_SEM_PERMISSAO);
            }
        } catch (BaseException e) {
            apresentarExcecaoNegocio(e);
        }
        return retorno;
    }

	public String confirmar() {
        if (this.getIndCatMat()) {
            if (this.getMaterialVO().getCatMat() == null && !justificativaConfirmada) {
                if (this.emEdicao) {
                    openDialog("modalConfirmacaoJustificativaWG");
                    return null;
                } else {
                    openDialog("panelJustificativaCatMatWG");
                    return null;
                }
            }
        }
        String nomeMicrocomputador = getNomeMicroComputador();
		try {
            ScoMaterial material = getInstanciaMaterial();
            validaJustificativa();
            getPopulaMaterial(material);
			AghParametros pObrigatoriedade = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_OBRIGATORIEDADE_SIGTAP);
			if(verificaProcedimentoRelacionado(pObrigatoriedade)){
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_PROCEDIMENTO_OBRIGATORIO_MATERIAL");
				retornouProcedimentos = false;
				return null;
			}
            material = persisteMaterial(nomeMicrocomputador, material);
            getMensagensElimpaVariaveis(material);
            if (this.integracaoDescricaoTecnica) {
				return manterMaterialDescTecnica();
			}
		} catch (BaseException e) {
			retornouProcedimentos = false;
			apresentarExcecaoNegocio(e);
		}
		if(this.criadoNovoMaterial){
			return manterVinculoMaterial();
		}
		processosRelacionados = null;
		listaClones = null;
		return null;
	}

    private String getNomeMicroComputador() {
        String nomeMicrocomputador = null;
        try {
            nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
        } catch (UnknownHostException e) {
            LOG.error("Exceção caputada:", e);
        }
        return nomeMicrocomputador;
    }

    private void getMensagensElimpaVariaveis(ScoMaterial material) throws ApplicationBusinessException {
        String mensagem = null;
        if (this.emEdicao) {
            mensagem = "MENSAGEM_SUCESSO_ALTERAR_MANTER_MATERIAL";
        } else {
            mensagem = "MENSAGEM_SUCESSO_INSERIR_MANTER_MATERIAL";
        }
        if(!this.emEdicao){
            this.codigo = material.getCodigo();
            this.materialVO.setCodigo(material.getCodigo());
            this.emEdicao = true;
        }
        justificativaConfirmada = false;
        this.apresentarMsgNegocio(Severity.INFO, mensagem, material.getNome());
        getConfirmaProcedimentosRelacionados();
        retornouProcedimentos = false;
        processosRelacionados = null;
        listaClones = null;
    }

    private ScoMaterial persisteMaterial(String nomeMicrocomputador, ScoMaterial material) throws BaseException {
        if(getProcessosRelacionados() != null && !getProcessosRelacionados().isEmpty()){
            material = getLimpaJustificativa(material);
        }
        this.estoqueBeanFacade.manterMaterialEsperanto(material, nomeMicrocomputador);
        return material;
    }

    private void validaJustificativa() throws BaseException {
        if (textoJustificativaCatMat != null) {
            if (textoJustificativaCatMat.length() < 15) {
                justificativaConfirmada = false;
                throw new BaseException(ManterMaterialControllerExceptionCode.MENSAGEM_JUSTIFICATIVA_MENOR_QUE_MINIMO);
            }
        }
    }

    private ScoMaterial getInstanciaMaterial() {
        ScoMaterial material = null;
        if(this.emEdicao){
            material = this.comprasFacade.obterScoMaterialDetalhadoPorChavePrimaria(this.codigo);
        } else{
            material = new ScoMaterial();
        }
        return material;
    }

    private void getConfirmaProcedimentosRelacionados() throws ApplicationBusinessException, ApplicationBusinessException {
		procedimentoInterno = obterProcedimentosInternos(this.codigo);
		if (procedimentoInterno != null && procedimentoInterno.getSeq() != null){
			if(procedimentoInterno == null || procedimentoInterno.getSeq() == null){
				procedimentoInterno = obterProcedimentosInternos(this.codigo);
			}
			if(getProcessosRelacionados() != null && getProcessosRelacionados().size() > 0){
				List<FatConvGrupoItemProced> listaProcedimentos = getPreparaListaProcedimentos();
				getPersistirProcedimentos(listaProcedimentos);
				relacionarPHISSMController.criarNotificacoesUsuarios(procedimentoInterno.getSeq(), listaProcedimentos);
			}else if(justificativaProcRel != null && getJustificado()){
				Integer codProcedimento = procedimentoInterno != null && procedimentoInterno.getSeq() != null ? procedimentoInterno.getSeq(): null;
				relacionarPHISSMController.criarNotificacoesUsuarios(codProcedimento, null);
			}
		}else if (getProcessosRelacionados() != null && getProcessosRelacionados().size() > 0){
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_PHI_INEXISTENTE");
		}
	}

	private List<FatConvGrupoItemProced> getPreparaListaProcedimentos() throws ApplicationBusinessException {
		AghParametros pTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		Short cpgCphPhoSeq = pTabela.getVlrNumerico().shortValue();
		List<VFatConvPlanoGrupoProcedVO> listaTabela = this.faturamentoFacade.listarTabelas(cpgCphPhoSeq.toString());
		Short cpgGrcSeq = null;
		if (listaTabela != null && !listaTabela.isEmpty()) {
			VFatConvPlanoGrupoProcedVO tabela = listaTabela.get(0);
			cpgGrcSeq = tabela.getGrcSeq();
		}
		setProcedimentoInterno(obterProcedimentosInternos(this.codigo));
		List<FatConvGrupoItemProced> listaProcRel = new ArrayList<FatConvGrupoItemProced>(0);
		for (VFatConvPlanoGrupoProcedVO vo : getProcessosRelacionados()) {
			FatConvGrupoItemProced fatConvGrupo = new FatConvGrupoItemProced();
			FatConvGrupoItemProcedId fatConvGrupoId = new FatConvGrupoItemProcedId(
					vo.getCphCspSeq(),vo.getCphCspCnvCodigo(), 
					vo.getCphPhoSeq(), vo.getGrcSeq(), vo.getIphSeq(), vo.getIphPhoSeq(), vo.getPhiSeq());
			
			if(vo.getOperacao().equals(DominioOperacoesJournal.UPD)){
				fatConvGrupo = faturamentoFacade.obterFatConvGrupoItensProcedId(fatConvGrupoId);
			}else{
				FatConvGrupoItemProcedId id = new FatConvGrupoItemProcedId();
				id.setIphPhoSeq(vo.getIphPhoSeq());
				id.setIphSeq(vo.getFatItensProcedHospitalar().getSeq());
				id.setCpgCphCspCnvCodigo(vo.getCphCspCnvCodigo());
				id.setCpgCphCspSeq(vo.getCphCspSeq());
				id.setCpgCphPhoSeq(cpgCphPhoSeq);
				id.setCpgGrcSeq(cpgGrcSeq);
				id.setPhiSeq(procedimentoInterno.getSeq());
				fatConvGrupo.setId(id);
			}
			fatConvGrupo.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(vo.getCphCspCnvCodigo(), vo.getCphCspSeq()));
			fatConvGrupo.setItemProcedHospitalar(vo.getFatItensProcedHospitalar());
			fatConvGrupo.setIndCobrancaFracionada(Boolean.FALSE);
			fatConvGrupo.setIndExigeJustificativa(Boolean.FALSE);
			fatConvGrupo.setIndImprimeLaudo(Boolean.FALSE);
			fatConvGrupo.setProcedimentoHospitalarInterno(procedimentoInterno);
			fatConvGrupo.setOperacao(vo.getOperacao());
			listaProcRel.add(fatConvGrupo);
		}
		return listaProcRel;
	}

	private Boolean verificaProcedimentoRelacionado(AghParametros pObrigatoriedade){
		boolean obrigatoriedade;
		if (securityController.usuarioTemPermissao("ordenadorDespesas", "gravar") || !"S".equalsIgnoreCase(pObrigatoriedade.getVlrTexto())) {
			obrigatoriedade = false;
		}else if (getIndFaturavel()) {
			obrigatoriedade = true;
		}else {
			obrigatoriedade = false;
		}
		if(obrigatoriedade && (procedimentoRelacionadoCadastrado != null
				|| (getProcessosRelacionados() != null && !getProcessosRelacionados().isEmpty()) 
				|| (getJustificado() && justificativaProcRel != null))){
			obrigatoriedade = false;
		}
		return obrigatoriedade;
	}

	private void getPopulaMaterial(ScoMaterial material) {
		material.setNome(materialVO.getNome());
		material.setIndSituacao(materialVO.getIndSituacao());
		material.setDescricao(materialVO.getDescricao());
		material.setUnidadeMedida(materialVO.getScoUnidadeMedida());
		material.setOrigemParecerTecnico(materialVO.getOrigemParecerTecnico());
		material.setNumero(materialVO.getNumero());
		material.setGrupoMaterial(materialVO.getScoGrupoMaterial());
		material.setAlmoxarifado(materialVO.getSceAlmoxarifado());
		material.setClassifXyz(materialVO.getClassifXyz());
		material.setSazonalidade(materialVO.getSazonalidade());
		material.setIndProducaoInterna(materialVO.getIndProducaoInterna());
		material.setIndFotosensivel(materialVO.getIndFotosensivel());
		material.setIndTipoUso(materialVO.getIndTipoUso());
		material.setEstocavel(this.indEstocavel);
		material.setIndPadronizado(DominioSimNao.getInstance(this.indPadronizado));
		material.setIndGenerico(DominioSimNao.getInstance(this.indGenerico));
		material.setIndControleValidade(DominioSimNao.getInstance(this.indControleValidade));
		material.setIndMenorPreco(DominioSimNao.getInstance(this.indMenorPreco));
		material.setIndAtuQtdeDisponivel(DominioSimNao.getInstance(this.indAtuQtdeDisponivel));
		material.setIndFaturavel(DominioSimNao.getInstance(this.indFaturavel));
		if(this.materialVO.getCatMat() != null){
			String catmat = this.materialVO.getCatMat().getCodigo().replaceAll( "\\D*", "" );
			material.setCodCatmat(Integer.parseInt(catmat));
		} else {
			material.setCodCatmat(null);
		}
		material.setCodMatAntigo(materialVO.getCodMatAntigo());
		material.setIndCorrosivo(materialVO.getIndCorrosivo());
		material.setIndInflamavel(materialVO.getIndInflamavel());
		material.setIndRadioativo(materialVO.getIndRadioativo());
		material.setIndReativo(materialVO.getIndReativo());
		material.setIndToxico(materialVO.getIndToxico());
		material.setIndUtilizaEspacoFisico(materialVO.getIndUtilizaEspacoFisico());
		material.setIndTipoResiduo(materialVO.getIndTipoResiduo());
		material.setIndTermolabil(materialVO.getIndTermolabil());
		material.setIndVinculado(materialVO.getIndVinculado());
		material.setLegislacao(materialVO.getLegislacao());
		material.setTemperatura(materialVO.getTemperatura());
		material.setIndSustentavel(materialVO.getIndSustentavel());
		material.setIndConfaz(DominioSimNao.getInstance(this.indConfaz));
		material.setIndCapCmed(DominioSimNao.getInstance(this.indCapCmed));
		if(material.getJustificativaProcRel() == null && justificativaProcRel != null){
			material.setSerMatriculaJusProcRel(serMatriculaJusProcRel);
			material.setJustificativaProcRel(justificativaProcRel);
			material.setDataJusProcRel(dataJusProcRel);
			material.setSerVinCodigoJusProcRel(serVinCodigoJusProcRel);
		}
        material.setJustificativaCatmat(textoJustificativaCatMat);
		material.setIndCcih(DominioSimNao.N);
		material.setObservacao(materialVO.getObservacao());
	}
	
	public String confirmarIntegracao() {
        if (this.getMaterialVO().getCatMat() == null) {
            openDialog("modalConfirmacaoJustificativaWG");
            return null;
        }
        String nomeMicrocomputador = getString();
		try {
			ScoMaterial material = new ScoMaterial();
			getPopulaMaterial(material);
			this.estoqueBeanFacade.manterMaterial(material, nomeMicrocomputador);
			String mensagem = null;
			mensagem = "MENSAGEM_SUCESSO_INSERIR_MANTER_MATERIAL";
			this.apresentarMsgNegocio(Severity.INFO, mensagem, material.getNome());
			this.codigo = material.getCodigo();
			this.materialVO.setCodigo(material.getCodigo());
			this.emEdicao = true;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return manterMaterialDescTecnica();
	}

    private String getString() {
        String nomeMicrocomputador = null;
        try {
            nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
        } catch (UnknownHostException e) {
            LOG.error("Exceção caputada:", e);
        }
        return nomeMicrocomputador;
    }

    public FatProcedHospInternos obterProcedimentosInternos(Integer seq) throws ApplicationBusinessException{
		List<FatProcedHospInternos> lista = new ArrayList<FatProcedHospInternos>();
		lista = this.faturamentoFacade.listarPhi(null, null, seq, null, null, null, null, null, null, 1);
		if(!lista.isEmpty()){
			return lista.get(0);	
		}
		return null;
	}
	
	public String voltar() {
		this.codigo = null;
		if(voltarPara != null){
			if(voltarPara.equalsIgnoreCase("manterEstoqueAlmoxarifado")){
				return MANTER_ESTOQUE_ALMOXARIFADO;
			}
			else if(voltarPara.equalsIgnoreCase("pesquisaManterMaterial")){
				return PESQUISA_MANTER_MATERIAL;
			}
			else if(voltarPara.equalsIgnoreCase("PESQUISAR_ESTOQUE_ALMOXARIFADO")){
				return PESQUISAR_ESTOQUE_ALMOXARIFADO;
			}
			else if(voltarPara.equalsIgnoreCase("CATALOGO_MATERIAL")){
				return CONSULTAR_CATALOGO_MATERIAL;
			}
			else if(voltarPara.equalsIgnoreCase("ESTATISTICA_CONSUMO")){
				return ESTATISTICA_CONSUMO;
			}
			else if(voltarPara.equalsIgnoreCase("CONSULTAR_ESTOQUE_ALMOXARIFADO")){
				return ESTOQUE_PESQUISAR_ESTOQUE_ALMOXARIFADO;
			}
			return voltarPara;
		}
		return null;
	}

	public List<ScoUnidadeMedida> obterUnidades(String objPesquisa) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorSigla(objPesquisa, true);
	}
	
	public List<ScoOrigemParecerTecnico> obterOrigens(String objPesquisa) {
		return this.comprasFacade.obterOrigemParecerTecnico(objPesquisa);
	}
	
	public List<ScoGrupoMaterial> obterGrupos(String objPesquisa) {
		return this.comprasFacade.obterGrupoMaterialPorSeqDescricao(objPesquisa);
	}
	
	public List<SceAlmoxarifado> obterLocaisEstoque(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}
	
	public List<ScoSiasgMaterialMestre> obterCatMat(String objCatMat) {
		return this.estoqueFacade.obterCatMat(objCatMat);
	}
	
	public String cancelar(){
		return origem;
	}
	
	public String manterEstoqueAlmoxarifado(){
		return ESTOQUE_PESQUISAR_ESTOQUE_ALMOXARIFADO;
	}
	
	public String manterValidadeMaterial(){
		return ESTOQUE_PESQUISAR_VALIDADE_MATERIAL;
	}
	
	public String consultarHistoricoMaterial(){
		return ESTOQUE_CONSULTAR_HISTORICO_MATERIAL;
	}
	
	public String manterProcedimentoRelacionado(){
		return PRESCRICAOMEDICA_PROCEDIMENTOS_RELACIONADOS;
	}
	
	public DominioIndFotoSensibilidade[] listarIndFotoSensibilidade(){
		return new DominioIndFotoSensibilidade[]{DominioIndFotoSensibilidade.S, DominioIndFotoSensibilidade.N};
	}
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public ScoMaterialVO getMaterialVO() {
		return materialVO;
	}
	
	public void setMaterialVO(ScoMaterialVO materialVO) {
		this.materialVO = materialVO;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
	
	public Boolean getIndEstocavel() {
		return indEstocavel;
	}

	public void setIndEstocavel(Boolean indEstocavel) {
		this.indEstocavel = indEstocavel;
	}


	public Boolean getIndPadronizado() {
		return indPadronizado;
	}


	public void setIndPadronizado(Boolean indPadronizado) {
		this.indPadronizado = indPadronizado;
	}

	public Boolean getIndGenerico() {
		return indGenerico;
	}

	public void setIndGenerico(Boolean indGenerico) {
		this.indGenerico = indGenerico;
	}

	public Boolean getIndControleValidade() {
		return indControleValidade;
	}

	public void setIndControleValidade(Boolean indControleValidade) {
		this.indControleValidade = indControleValidade;
	}

	public Boolean getIndMenorPreco() {
		return indMenorPreco;
	}

	public void setIndMenorPreco(Boolean indMenorPreco) {
		this.indMenorPreco = indMenorPreco;
	}

	public Boolean getIndAtuQtdeDisponivel() {
		return indAtuQtdeDisponivel;
	}

	public void setIndAtuQtdeDisponivel(Boolean indAtuQtdeDisponivel) {
		this.indAtuQtdeDisponivel = indAtuQtdeDisponivel;
	}

	public Boolean getIndFaturavel() {
		if(indFaturavel != null && indFaturavel == true){
			return indFaturavel;
		}
		if(materialVO != null && materialVO.getScoGrupoMaterial() != null){
			if(materialVO.getScoGrupoMaterial().getCodigo().equals(13)){
				indFaturavel = true;
			}else{
				indFaturavel = false;
			}
		}else{
			indFaturavel = false;
		}
		return indFaturavel;
	}
	
	public Boolean verificaGrupo(){
		if(materialVO!= null && materialVO.getScoGrupoMaterial() != null){
			if(materialVO.getScoGrupoMaterial().getCodigo().equals(13)){
				return true;
			}
		}
		return false;
	}

	public void setIndFaturavel(Boolean indFaturavel) {
		this.indFaturavel = indFaturavel;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public void setIndCatMat(Boolean indCatMat) {
		this.indCatMat = indCatMat;
	}

	public Boolean getIndCatMat() {
		return indCatMat;
	}

	public void setHabilitarBotaoHistorico(Boolean habilitarBotaoHistorico) {
		this.habilitarBotaoHistorico = habilitarBotaoHistorico;
	}

	public Boolean getHabilitarBotaoHistorico() {
		return habilitarBotaoHistorico;
	}

	public void setSliderInfoFiscaisAberto(Boolean sliderInfoFiscaisAberto) {
		this.sliderInfoFiscaisAberto = sliderInfoFiscaisAberto;
	}

	public Boolean isSliderInfoFiscaisAberto() {
		return sliderInfoFiscaisAberto;
	}
	
	public String manterVinculoMaterial(){
		return ESTOQUE_MATERIAL_VINCULO_CRUD;
	}

	public String voltarVinculoMaterial(){
		return ESTOQUE_MATERIAL_VINCULO_CRUD;
	}
	
	public String manterMaterialDescTecnica(){
		this.integracaoDescricaoTecnica = Boolean.FALSE;
		return COMPRAS_CADASTRO_DESCRICAO_TECNICA_CRUD;
	}
	
	public String voltarIntegracao() {
		return COMPRAS_CADASTRO_DESCRICAO_TECNICA_CRUD;
	}

	public void setIndConfaz(Boolean indConfaz) {
		this.indConfaz = indConfaz;
	}

	public Boolean getIndConfaz() {
		return indConfaz;
	}

	public void setIndCapCmed(Boolean indCapCmed) {
		this.indCapCmed = indCapCmed;
	}

	public Boolean getIndCapCmed() {
		return indCapCmed;
	}

	public void setExibirConfaz(Boolean exibirConfaz) {
		this.exibirConfaz = exibirConfaz;
	}

	public Boolean getExibirConfaz() {
		return exibirConfaz;
	}

	public void setExibirSliderInfoFiscais(Boolean exibirSliderInfoFiscais) {
		this.exibirSliderInfoFiscais = exibirSliderInfoFiscais;
	}

	public Boolean getExibirSliderInfoFiscais() {
		return exibirSliderInfoFiscais;
	}

	public Boolean getIntegracaoDescricaoTecnica() {
		return integracaoDescricaoTecnica;
	}

	public void setIntegracaoDescricaoTecnica(Boolean integracaoDescricaoTecnica) {
		this.integracaoDescricaoTecnica = integracaoDescricaoTecnica;
	}

	public void setCriadoNovoMaterial(Boolean criadoNovoMaterial) {
		this.criadoNovoMaterial = criadoNovoMaterial;
	}

	public Boolean getCriadoNovoMaterial() {
		return criadoNovoMaterial;
	}

	public void setCodigoMaterialPrincipal(Integer codigoMaterialPrincipal) {
		this.codigoMaterialPrincipal = codigoMaterialPrincipal;
	}

	public Integer getCodigoMaterialPrincipal() {
		return codigoMaterialPrincipal;
	}
	
	public boolean isExibirVoltarComum() {
		if(integracaoDescricaoTecnica.equals(true) || criadoNovoMaterial == true){
			return false;
		}
		return true;
	}
	
	public FatProcedHospInternos getProcedimentoInterno() {
		return procedimentoInterno;
	}

	public void setProcedimentoInterno(FatProcedHospInternos procedimentoInterno) {
		this.procedimentoInterno = procedimentoInterno;
	}
	
	public List<VFatConvPlanoGrupoProcedVO> getProcessosRelacionados() {
		return processosRelacionados;
	}

	public void setProcessosRelacionados(List<VFatConvPlanoGrupoProcedVO> processosRelacionados) {
		this.processosRelacionados = processosRelacionados;
	}
	
	public Boolean getJustificado() {
		return justificado;
	}

	public void setJustificado(Boolean justificado) {
		this.justificado = justificado;
	}
	
	public Integer getSerMatriculaJusProcRel() {
		return serMatriculaJusProcRel;
	}

	public void setSerMatriculaJusProcRel(Integer serMatriculaJusProcRel) {
		this.serMatriculaJusProcRel = serMatriculaJusProcRel;
	}

	public String getJustificativaProcRel() {
		return justificativaProcRel;
	}

	public void setJustificativaProcRel(String justificativaProcRel) {
		this.justificativaProcRel = justificativaProcRel;
	}

	public Date getDataJusProcRel() {
		return dataJusProcRel;
	}

	public void setDataJusProcRel(Date dataJusProcRel) {
		this.dataJusProcRel = dataJusProcRel;
	}

	public Short getSerVinCodigoJusProcRel() {
		return serVinCodigoJusProcRel;
	}

	public void setSerVinCodigoJusProcRel(Short serVinCodigoJusProcRel) {
		this.serVinCodigoJusProcRel = serVinCodigoJusProcRel;
	}

	public String getSenhaUser() {
		return senhaUser;
	}

	public void setSenhaUser(String senhaUser) {
		this.senhaUser = senhaUser;
	}

	public List<FatConvGrupoItemProced> getListaClones() {
		return listaClones;
	}

	public void setListaClones(List<FatConvGrupoItemProced> listaClones) {
		this.listaClones = listaClones;
	}
	
	public Boolean verificaProcedimentoCadastrado(){
		if(procedimentoRelacionadoCadastrado != null){
			if (procedimentoRelacionadoCadastrado.size() > 0){
				return true;
			}
		}
		return false;
	}
	
	public Boolean getRetornouProcedimentos() {
		return retornouProcedimentos;
	}

	public void setRetornouProcedimentos(Boolean retornouProcedimentos) {
		this.retornouProcedimentos = retornouProcedimentos;
	}
	
	private void getPersistirProcedimentos(List<FatConvGrupoItemProced> listaProcedimentos) throws ApplicationBusinessException {
		for (FatConvGrupoItemProced proced : listaProcedimentos) {
			if(DominioOperacoesJournal.DEL.equals(proced.getOperacao())) {
				FatConvGrupoItemProced procedDel = faturamentoFacade.obterFatConvGrupoItensProcedPeloId(proced.getId().getCpgCphCspCnvCodigo(), proced.getId().getCpgCphCspSeq(), proced.getId().getPhiSeq());
				faturamentoFacade.excluirGrupoItemConvenio(procedDel);
			} else if(DominioOperacoesJournal.UPD.equals(proced.getOperacao())) {
				if(listaClones.contains(proced)) {
					faturamentoFacade.persistirGrupoItemConvenio(proced, listaClones.get(listaClones.indexOf(proced)), DominioOperacoesJournal.UPD);
				} else {
					faturamentoFacade.persistirGrupoItemConvenio(proced, null, DominioOperacoesJournal.INS);
				}
			} else if(DominioOperacoesJournal.INS.equals(proced.getOperacao())) {
				faturamentoFacade.persistirGrupoItemConvenio(proced, null, DominioOperacoesJournal.INS);
				
			}				
		}
	}

	private ScoMaterial getLimpaJustificativa(ScoMaterial material) {
		material.setSerMatriculaJusProcRel(null);
		material.setJustificativaProcRel(null);
		material.setDataJusProcRel(null);
		material.setSerVinCodigoJusProcRel(null);
		getLimpaVariaveisJustificativa();
		return material;
	}

	private void getLimpaVariaveisJustificativa() {
		serMatriculaJusProcRel = null;
		justificativaProcRel = null;
		dataJusProcRel = null;
		serVinCodigoJusProcRel = null;
		procedimentoRelacionadoController.setSerMatriculaJusProcRel(null);
		procedimentoRelacionadoController.setJustificativaProcRel(null);
		procedimentoRelacionadoController.setPhiSeq(null);
        textoJustificativaCatMat = null;
        justificativaConfirmada = false;
	}

    public String getUsuarioJustificativaCatMat() {
        return usuarioJustificativaCatMat;
    }

    public void setUsuarioJustificativaCatMat(String usuarioJustificativaCatMat) {
        this.usuarioJustificativaCatMat = usuarioJustificativaCatMat;
    }

    public String getSenhaJustificativaCatMat() {
        return senhaJustificativaCatMat;
    }

    public void setSenhaJustificativaCatMat(String senhaJustificativaCatMat) {
        this.senhaJustificativaCatMat = senhaJustificativaCatMat;
    }

    public Boolean getExisteJustificativaAnterior() {
        return existeJustificativaAnterior;
    }

    public void setExisteJustificativaAnterior(Boolean existeJustificativaAnterior) {
        this.existeJustificativaAnterior = existeJustificativaAnterior;
    }
}