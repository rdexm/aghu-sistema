package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioOpcoesFormulaParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.AelResultadoCodificadoRN;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelCadCtrlQualidadesDAO;
import br.gov.mec.aghu.exames.dao.AelCampoCodifRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoVinculadoDAO;
import br.gov.mec.aghu.exames.dao.AelDadosCadaveresDAO;
import br.gov.mec.aghu.exames.dao.AelEquipamentosDAO;
import br.gov.mec.aghu.exames.dao.AelExameReflexoDAO;
import br.gov.mec.aghu.exames.dao.AelExameResuNotificacaoDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelExamesNotificacaoDAO;
import br.gov.mec.aghu.exames.dao.AelExecExamesMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoXMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelModeloCartasDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelRetornoCartaDAO;
import br.gov.mec.aghu.exames.dao.AelServidorUnidAssinaEletDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmoExameConvDAO;
import br.gov.mec.aghu.exames.dao.AelVersaoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AghResponsavelDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.vo.AelAgrpPesquisaXExameVO;
import br.gov.mec.aghu.exames.vo.AelExameGrupoCaracteristicaVO;
import br.gov.mec.aghu.exames.vo.AelGrpTecnicaUnfExamesVO;
import br.gov.mec.aghu.exames.vo.ProtocoloEntregaExamesVO;
import br.gov.mec.aghu.exames.vo.ResponsavelVO;
import br.gov.mec.aghu.exames.vo.ResultadoCodificadoExameVO;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelCampoCodifRelacionado;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionado;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExameDeptConvenio;
import br.gov.mec.aghu.model.AelExameDeptConvenioId;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristicaId;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelExameReflexo;
import br.gov.mec.aghu.model.AelExameReflexoId;
import br.gov.mec.aghu.model.AelExameResuNotificacao;
import br.gov.mec.aghu.model.AelExameResuNotificacaoId;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesDependentesId;
import br.gov.mec.aghu.model.AelExamesEspecialidade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelExamesNotificacaoId;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AelExecExamesMatAnaliseId;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExames;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExamesId;
import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampo;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampoId;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnaliseId;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelHorarioRotinaColetasId;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelLoteExame;
import br.gov.mec.aghu.model.AelLoteExameId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMetodoUnfExame;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelRetornoCarta;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExamesId;
import br.gov.mec.aghu.model.AelServidorUnidAssinaElet;
import br.gov.mec.aghu.model.AelServidorUnidAssinaEletId;
import br.gov.mec.aghu.model.AelServidoresExameUnid;
import br.gov.mec.aghu.model.AelSinonimoCampoLaudo;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelTipoAmoExameConv;
import br.gov.mec.aghu.model.AelTipoAmoExameConvId;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudoId;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghHorariosUnidFuncionalId;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

/**
 * Porta de entrada do sub módulo de cadastros de apoio do módulo exames.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.EXAMES_LAUDOS)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class CadastrosApoioExamesFacade extends BaseFacade implements ICadastrosApoioExamesFacade {

	@EJB
	private UnidExecUsuarioON unidExecUsuarioON;

	@EJB
	private AelAgrpPesquisasRN aelAgrpPesquisasRN;
	
	@EJB
	private AelProtocoloEntregaExamesRN aelProtocoloEntregaExamesRN;

	@EJB
	private AnticoagulanteON anticoagulanteON;

	@EJB
	private ModeloCartaRN modeloCartaRN;

	@EJB
	private ManterMaterialAnaliseON manterMaterialAnaliseON;

	@EJB
	private ManterPermissoesUnidadesSolicitantesCRUD manterPermissoesUnidadesSolicitantesCRUD;

	@EJB
	private AelAgrpPesquisaXExameRN aelAgrpPesquisaXExameRN;

	@EJB
	private ExameNotificacaoON exameNotificacaoON;

	@EJB
	private AelCampoLaudoRelacionadoRN aelCampoLaudoRelacionadoRN;

	@EJB
	private AelCampoVinculadoRN aelCampoVinculadoRN;

	@EJB
	private AelEquipamentoRN aelEquipamentoRN;

	@EJB
	private AgruparCaracteristicaExameON agruparCaracteristicaExameON;

	@EJB
	private ManterProvaExameRN manterProvaExameRN;

	@EJB
	private ManterGrupoExamesCRUD manterGrupoExamesCRUD;

	@EJB
	private LaboratorioHemocentroRN laboratorioHemocentroRN;

	@EJB
	private AelDadosCadaveresRN aelDadosCadaveresRN;

	@EJB
	private ManterSalaExecutoraExameON manterSalaExecutoraExameON;

	@EJB
	private ManterHorarioFuncionamentoUnidadeON manterHorarioFuncionamentoUnidadeON;

	@EJB
	private AnticoagulanteRN anticoagulanteRN;

	@EJB
	private GrupoResultadoCaracteristicaON grupoResultadoCaracteristicaON;

	@EJB
	private ManterRecipienteColetaON manterRecipienteColetaON;

	@EJB
	private AelExamesCRUD aelExamesCRUD;

	@EJB
	private ManterSituacaoExameON manterSituacaoExameON;

	@EJB
	private ManterRestringirPedidoEspecialidadeCRUD manterRestringirPedidoEspecialidadeCRUD;

	@EJB
	private AelCampoCodifRelacionadoRN aelCampoCodifRelacionadoRN;

	@EJB
	private MedicoAtendimentoExternoON medicoAtendimentoExternoON;

	@EJB
	private ModeloCartaON modeloCartaON;

	@EJB
	private ManterExamesMaterialDependenteRN manterExamesMaterialDependenteRN;

	@EJB
	private ManterDadosBasicosExamesCRUD manterDadosBasicosExamesCRUD;

	@EJB
	private GrupoMaterialAnaliseRN grupoMaterialAnaliseRN;

	@EJB
	private AelLoteExameON aelLoteExameON;

	@EJB
	private AelResultadoCaracteristicaRN aelResultadoCaracteristicaRN;

	@EJB
	private AelServidorUnidAssinaEletON aelServidorUnidAssinaEletON;

	@EJB
	private RetornoCartaRN retornoCartaRN;

	@EJB
	private AelCampoLaudoRN aelCampoLaudoRN;

	@EJB
	private ManterExamesMaterialAnaliseON manterExamesMaterialAnaliseON;

	@EJB
	private ManterRecipienteColetaRN manterRecipienteColetaRN;

	@EJB
	private ManterTipoAmostraExameRN manterTipoAmostraExameRN;

	@EJB
	private AelResultadosPadraoRN aelResultadosPadraoRN;

	@EJB
	private AelResultadoPadraoCampoRN aelResultadoPadraoCampoRN;

	@EJB
	private AelDescricoesResulPadraoRN aelDescricoesResulPadraoRN;

	@EJB
	private MedicoAtendimentoExternoRN medicoAtendimentoExternoRN;

	@EJB
	private AelGrupoResultadoCaracteristicaRN aelGrupoResultadoCaracteristicaRN;

	@EJB
	private AelCadCtrlQualidadesRN aelCadCtrlQualidadesRN;

	@EJB
	private AelParametroCamposLaudoRN aelParametroCamposLaudoRN;

	@EJB
	private GrupoMaterialAnaliseON grupoMaterialAnaliseON;

	@EJB
	private AelGrupoTecnicaCampoRN aelGrupoTecnicaCampoRN;

	@EJB
	private AelExameReflexoON aelExameReflexoON;

	@EJB
	private AelSinonimoCampoLaudoRN aelSinonimoCampoLaudoRN;

	@EJB
	private ManterUnidadesExecutorasExamesCRUD manterUnidadesExecutorasExamesCRUD;

	@EJB
	private GrupoRecomendacaoON grupoRecomendacaoON;

	@EJB
	private ManterExamesMaterialAnaliseRN manterExamesMaterialAnaliseRN;

	@EJB
	private ManterIntervaloColetaON manterIntervaloColetaON;

	@EJB
	private ManterHorarioColetaExameON manterHorarioColetaExameON;

	@EJB
	private ManterRegiaoAnatomicaON manterRegiaoAnatomicaON;

	@EJB
	private AelMotivoCancelaExamesCRUD aelMotivoCancelaExamesCRUD;

	@EJB
	private AelParametroCamposLaudoON aelParametroCamposLaudoON;

	@EJB
	private ManterRecomendacaoRealizacaoExameRN manterRecomendacaoRealizacaoExameRN;

	@EJB
	private ManterPedidoExamesServidoresCRUD manterPedidoExamesServidoresCRUD;

	@EJB
	private ResultadoPadraoON resultadoPadraoON;

	@EJB
	private GrupoXMaterialAnaliseRN grupoXMaterialAnaliseRN;

	@EJB
	private ExameNotificacaoRN exameNotificacaoRN;

	@EJB
	private AelVersaoLaudoRN aelVersaoLaudoRN;

	@EJB
	private AelAtendimentoDiversosRN aelAtendimentoDiversosRN;

	@EJB
	private AelSinonimosExamesCRUD aelSinonimosExamesCRUD;

	@EJB
	private AelTipoAmoExameConvRN aelTipoAmoExameConvRN;

	@EJB
	private UnidadeMedidaON unidadeMedidaON;

	@EJB
	private AelExameGrupoCaracteristicaRN aelExameGrupoCaracteristicaRN;

	@EJB
	private AelExecExamesMatAnaliseRN aelExecExamesMatAnaliseRN;

	@EJB
	private LaboratorioHemocentroON laboratorioHemocentroON;

	@EJB
	private ManterMetodoUnfExameRN manterMetodoUnfExameRN;

	@EJB
	private ExameResultadoNotificacaoRN exameResultadoNotificacaoRN;

	@EJB
	private ManterHorarioRotinaColetaCRUD manterHorarioRotinaColetaCRUD;
	
	@EJB
	private AelResultadoCodificadoRN aelResultadoCodificadoRN;	
	
	@EJB 
	private AghResponsavelON aghResponsavelON;

	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;

	@Inject
	private AelExameReflexoDAO aelExameReflexoDAO;

	@Inject
	private AelEquipamentosDAO aelEquipamentosDAO;

	@Inject
	private AelServidorUnidAssinaEletDAO aelServidorUnidAssinaEletDAO;

	@Inject
	private AelGrupoMaterialAnaliseDAO aelGrupoMaterialAnaliseDAO;

	@Inject
	private AelVersaoLaudoDAO aelVersaoLaudoDAO;

	@Inject
	private AelExameResuNotificacaoDAO aelExameResuNotificacaoDAO;

	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;

	@Inject
	private AelCampoVinculadoDAO aelCampoVinculadoDAO;

	@Inject
	private AelResultadosPadraoDAO aelResultadosPadraoDAO;

	@Inject
	private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;

	@Inject
	private AelDadosCadaveresDAO aelDadosCadaveresDAO;

	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	@Inject
	private AelExamesNotificacaoDAO aelExamesNotificacaoDAO;

	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;

	@Inject
	private AelTipoAmoExameConvDAO aelTipoAmoExameConvDAO;

	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;
	
	@Inject
	private AelGrupoXMaterialAnaliseDAO aelGrupoXMaterialAnaliseDAO;

	@Inject
	private AelAgrpPesquisasDAO aelAgrpPesquisasDAO;

	@Inject
	private AelRetornoCartaDAO aelRetornoCartaDAO;

	@Inject
	private AelModeloCartasDAO aelModeloCartasDAO;

	@Inject
	private AelExecExamesMatAnaliseDAO aelExecExamesMatAnaliseDAO;

	@Inject
	private AelCampoLaudoRelacionadoDAO aelCampoLaudoRelacionadoDAO;

	@Inject
	private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;

	@Inject
	private AelCadCtrlQualidadesDAO aelCadCtrlQualidadesDAO;

	@Inject
	private AelCampoCodifRelacionadoDAO aelCampoCodifRelacionadoDAO;
	
	@Inject
	private AghResponsavelDAO	aghResponsaveldao;

	@Inject
	private AelExamesDAO aelExamesDAO;
	
	private static final long serialVersionUID = -8664965253135723182L;
	@Override
	public void adicionarProcedimentosRelacionados(List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO,
			VFatConvPlanoGrupoProcedVO convenio, FatItensProcedHospitalar fatItensProcedHospitalar, Short grcSeq)
			throws ApplicationBusinessException {
		this.getManterExamesMaterialAnaliseON().adicionarProcedimentosRelacionados(listaFatProcedimentoRelacionadosVO, convenio,
				fatItensProcedHospitalar, grcSeq);
	}

	@Override
	public AelSitItemSolicitacoes ativarDesativarSituacaoExame(AelSitItemSolicitacoes situacao, boolean ativar)
			throws ApplicationBusinessException {
		return getManterSituacaoExameON().ativarDesativarSituacaoExame(situacao, ativar);
	}

	@Override
	@Secure("#{s:hasPermission('manterUnidadeMedida','persistir')}")
	public AelUnidMedValorNormal ativarInativarAelUnidMedValorNormal(AelUnidMedValorNormal aumvn) throws ApplicationBusinessException {
		return getUnidadeMedidaON().ativarInativarAelUnidMedValorNormal(aumvn);
	}

	@Override
	@Secure("#{s:hasPermission('manterAnticoagulantes','executar')}")
	public AelAnticoagulante ativarInativarAnticoagulante(AelAnticoagulante aelAnticoagulante) throws ApplicationBusinessException {
		return getAelAnticoagulanteON().ativarInativarAnticoagulante(aelAnticoagulante);
	}

	@Override
	public void atualizarAelDescricoesResulPadrao(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		this.getAelDescricoesResulPadraoRN().atualizar(descResultaPadrao);
	}

	@Override
	public void atualizarAelExameGrupoCaracteristica(AelExameGrupoCaracteristica aelExameGrupoCaracteristica) throws BaseException {
		this.getAelExameGrupoCaracteristicaRN().atualizar(aelExameGrupoCaracteristica);
	}

	@Override
	public void atualizarAelExamesDependentes(AelExamesDependentes exameDependente, AelExamesDependentes exaDepAux)
			throws ApplicationBusinessException {
		getManterExamesMaterialDependenteRN().atualizarAelExamesDependente(exameDependente, exaDepAux);
	}

	@Override
	public void atualizarAelExamesMaterialAnaliseTiposAmostraExame(AelExamesMaterialAnalise aelExamesMaterialAnalise,
			AelExamesMaterialAnalise aelExamesMaterialAnaliseAux, List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {
		getManterExamesMaterialAnaliseRN().atualizarAelExamesMaterialAnaliseTiposAmostraExame(aelExamesMaterialAnalise,
				aelExamesMaterialAnaliseAux, listaTiposAmostraExame);
	}

	@Override
	public void atualizarAelExecExamesMatAnalise(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException {
		getAelExecExamesMatAnaliseRN().atualizar(execExamesMatAnalise);
	}

	@Override
	public void atualizarAelGrupoResultadoCaracteristica(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		this.getAelGrupoResultadoCaracteristicaRN().atualizar(elemento);
	}

	@Override
	public void atualizarAelRecomendacaoExame(AelRecomendacaoExame novoRecomendacaoExame) throws ApplicationBusinessException {
		getManterRecomendacaoRealizacaoExameRN().atualizarAelRecomendacaoExame(novoRecomendacaoExame);
	}

	@Override
	public void atualizarAelResultadoCaracteristica(AelResultadoCaracteristica resultadoCaracteristica) throws BaseException {
		getAelResultadoCaracteristicaRN().atualizar(resultadoCaracteristica);
	}

	@Override
	public void atualizarAelResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		this.getAelResultadoPadraoCampoRN().atualizar(resultadoPadraoCampo);
	}

	@Override
	public void atualizarAelServidorUnidAssinaElet(AelServidorUnidAssinaElet servidorUnidAssinaElet) throws ApplicationBusinessException {
		getAelServidorUnidAssinaEletON().atualizarAelServidorUnidAssinaElet(servidorUnidAssinaElet);
	}

	@Override
	public void atualizarAelSinonimoExame(AelSinonimoExame aelSinonimoExame, boolean validaSeqp) throws ApplicationBusinessException,
			ApplicationBusinessException {
		getAelSinonimosExamesCRUD().atualizarAelSinonimosExames(aelSinonimoExame, validaSeqp);
	}

	@Override
	public void atualizarAelTipoAmoExameConv(AelTipoAmoExameConv elemento) throws BaseException {
		this.getAelTipoAmoExameConvRN().atualizar(elemento);
	}

	@Override
	@Secure("#{s:hasPermission('manterEquipamentos','executar')}")
	public void atualizarEquipamentos(AelEquipamentos equipamentos) throws BaseException {
		getAelEquipamentoRN().atualizar(equipamentos);
	}

	@Override
	@Secure("#{s:hasPermission('notificacaoResultadoExame','persistir')}")
	public void atualizarExameResultadoNotificacao(final AelExameResuNotificacao exameResultadoNotificacao)
			throws ApplicationBusinessException {
		this.getExameResultadoNotificacaoRN().atualizar(exameResultadoNotificacao);
	}

	@Override
	public void atualizarExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {
		getManterProvaExameRN().atualizarExamesProva(examesProva);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','executar')}")
	public void atualizarGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException {
		this.getGrupoMaterialAnaliseRN().atualizar(grupoMaterialAnalise);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','executar')}")
	public void atualizarGrupoXMaterialAnalise(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException {
		this.getGrupoXMaterialAnaliseRN().atualizar(grupoXMaterialAnalise);
	}

	@Override
	public void atualizarHorarioFuncionamentoUnidade(AghHorariosUnidFuncional horariosUnidFuncional) throws ApplicationBusinessException {
		getManterHorarioFuncionamentoUnidadeON().atualizarHorarioFuncionamentoUnidade(horariosUnidFuncional);
	}

	@Override
	public void atualizarHorarioRotina(AelHorarioRotinaColetas horarioRotina) throws ApplicationBusinessException {
		getManterHorarioRotinaColetaCRUD().atualizar(horarioRotina);
	}

	@Override
	public void atualizarMetodoUnfExame(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {
		getManterMetodoUnfExameRN().atualizarMetodoUnfExame(metodoUnfExame);
	}

	@Override
	@Secure("#{s:hasPermission('notificacaoResultadoExame','persistir')}")
	public void atualizarSituacaoExameNotificacao(final AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException {
		this.getExameNotificacaoON().atualizarSituacaoExameNotificacao(exameNotificacao);
	}

	@Override
	public List<VAelExameMatAnalise> buscaEXADEPVAelExameMatAnalisePelaSigla(String siglaDescViewExaMatAnalise)
			throws ApplicationBusinessException {
		return this.getGrupoRecomendacaoON().buscaEXADEPVAelExameMatAnalisePelaSigla(siglaDescViewExaMatAnalise);
	}

	@Override
	public List<RapServidores> buscaListRapServidoresVAelPessoaServidor(Object pesquisa, String emaExaSigla, Integer emaManSeq)
			throws ApplicationBusinessException {
		return getManterPedidoExamesServidoresCRUD().buscaListRapServidoresVAelPessoaServidor(pesquisa, emaExaSigla, emaManSeq);
	}

	@Override
	public List<AelCampoLaudo> buscarAelCampoLaudoPorVAelCampoLaudoExme(final String valPesquisa, final String velEmaExaSigla,
			final Integer velEmaManSeq, final boolean limitarEm100) {
		return getAelCampoLaudoDAO().buscarAelCampoLaudoPorVAelCampoLaudoExme(valPesquisa, velEmaExaSigla, velEmaManSeq, limitarEm100);
	}

	@Override
	public AelExamesDependentes buscarAelExamesDependenteById(AelExamesDependentesId id) throws ApplicationBusinessException {
		return getManterExamesMaterialDependenteRN().buscarAelExamesDependenteById(id);
	}

	@Override
	public List<AelExameReflexo> buscarAelExamesReflexo(final String exaSigla, final Integer manSeq) {
		return this.getAelExameReflexoDAO().buscarAelExamesReflexo(exaSigla, manSeq);
	}

	@Override
	public List<AelGrpTecnicaUnfExamesVO> buscarAelGrpTecnicaUnfExamesVOPorAelGrupoExameTecnica(AelGrupoExameTecnicas grupoExameTecnicas) {
		return getManterGrupoExamesCRUD().buscarAelGrpTecnicaUnfExamesVOPorAelGrupoExameTecnica(grupoExameTecnicas);
	}

	@Override
	public List<AelResultadoCodificado> buscarAelResultadoCodificado(final String string, final Integer calSeq) {
		return this.getResultadoPadraoON().buscarAelResultadoCodificadoPorCampoLaudo(string, calSeq);
	}

	@Override
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificados(String param) {		
		return aelResultadoCodificadoDAO.buscarResultadosCodificados(param);
	}
	
	@Override
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosBacteriaMultir(String param) throws ApplicationBusinessException {		
		return aelResultadoCodificadoRN.buscarResultadosCodificadosBacteriaMultir(param);
	}	
	
	@Override
	public Long buscarResultadosCodificadosBacteriaMultirCount(String param) throws ApplicationBusinessException {
		return aelResultadoCodificadoRN.buscarResultadosCodificadosBacteriaMultirCount(param);
	}	
	
	@Override
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosPorDescricao(String descricao) throws ApplicationBusinessException {		
		return aelResultadoCodificadoRN.buscarResultadosCodificadosPorDescricao(descricao);
	}	

	@Override
	public Long buscarResultadosCodificadosCount(String param) {
		// TODO Auto-generated method stub
		return aelResultadoCodificadoDAO.buscarResultadosCodificadosCount(param);
	}

	@Override
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosPorMicroorgPatologia(
			MciMicroorganismoPatologia patologia) {
		
		return aelResultadoCodificadoDAO.buscarResultadosCodificadosPorMicroorgPatologia(patologia);
	}

	@Override
	public List<VAelExameMatAnalise> buscarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(String siglaDescViewExaMatAnalise)
			throws ApplicationBusinessException {
		return this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePorSiglaDescViewExaMatAnalise(siglaDescViewExaMatAnalise, null);
	}

	@Override
	public List<VAelExameMatAnalise> buscarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(String valPesquisa, String string)
			throws ApplicationBusinessException {
		return this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePorSiglaDescViewExaMatAnalise(valPesquisa, string);
	}

	@Override
	public VAelExameMatAnalise buscarVAelExameMatAnalisePelaSiglaESeq(String exaSigla, Integer manSeq) {
		return this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePelaSiglaESeq(exaSigla, manSeq);
	}

	@Override
	public VAelExameMatAnalise buscarVAelExameMatAnalisePorExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise) {
		return this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePorExameMaterialAnalise(exameMaterialAnalise);
	}

	@Override
	public List<VAelExameMatAnalise> buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(String siglaDescViewExaMatAnalise) {
		return this.getVAelExameMatAnaliseDAO().buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(siglaDescViewExaMatAnalise);
	}

	@Override
	public Long buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnaliseCount(String siglaDescViewExaMatAnalise) {
		return this.getVAelExameMatAnaliseDAO().buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnaliseCount(siglaDescViewExaMatAnalise);
	}

	@Override
	public Long contarAelCampoLaudoPorVAelCampoLaudoExme(String valPesquisa, String velEmaExaSigla, Integer velEmaManSeq) {
		return getAelCampoLaudoDAO().contarAelCampoLaudoPorVAelCampoLaudoExme(valPesquisa, velEmaExaSigla, velEmaManSeq);
	}

	@Override
	public Long contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(final String valPesquisa) {
		return this.getVAelExameMatAnaliseDAO().contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(valPesquisa, null);
	}

	@Override
	public Long contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(String valPesquisa, String indDependente) {
		return this.getVAelExameMatAnaliseDAO()
				.contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(valPesquisa, indDependente);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarLaboratorio/Hemocentro','pesquisar')}")
	public Long countLaboratorioHemocentro(final AelLaboratorioExternos filtros) {
		return this.getLaboratorioHemocentroON().countLaboratoioHemocentro(filtros);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarMedicoAtendimentoExterno','pesquisar')}")
	public Long countMedicoAtendimentoExterno(Map<Object, Object> filtersMap) {
		return this.getMedicoAtendimentoExternoON().countMedicoAtendimentoExterno(filtersMap);
	}

	@Override
	public void desatacharVersaoLaudo(AelVersaoLaudo elemento) {
		this.getAelVersaoLaudoDAO().desatachar(elemento);
	}

	@Override
	public void excluirAelAtendimentoDiversos(AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException {
		getAelAtendimentoDiversosRN().remover(aelAtendimentoDiversos);
	}

	@Override
	public void excluirAelCadCtrlQualidades(Integer seq) throws BaseException {
		getAelCadCtrlQualidadesRN().remover(seq);
	}

	@Override
	public void excluirAelDadosCadaveres(final Integer seq) throws BaseException {
		getAelDadosCadaveresRN().remover(seq);
	}

	@Override
	public void excluirAelExameReflexo(final AelExameReflexo aelExameReflexoExcluir) throws ApplicationBusinessException {
		this.getAelExameReflexoON().excluirAelExameReflexo(aelExameReflexoExcluir);
	}

	@Override
	public void excluirAelTipoAmoExameConv(AelTipoAmoExameConv elemento) throws BaseException {
		this.getAelTipoAmoExameConvRN().excluir(elemento);
	}

	@Override
	public void excluirCampoLaudoPendencia(AelGrupoTecnicaCampoId id) throws BaseException {
		this.getAelGrupoTecnicaCampoRN().remover(id);
	}

	@Override
	@Secure("#{s:hasPermission('notificacaoResultadoExame','excluir')}")
	public void excluirExameNotificacao(final AelExamesNotificacaoId exameNotificacao) throws ApplicationBusinessException {
		this.getExameNotificacaoON().remover(exameNotificacao);
	}

	@Override
	@Secure("#{s:hasPermission('notificacaoResultadoExame','excluir')}")
	public void excluirExameResultadoNotificacao(final AelExameResuNotificacaoId id) throws ApplicationBusinessException {
		this.getExameResultadoNotificacaoRN().remover(id);
	}

	@Override
	public void excluirHorarioRotina(AelHorarioRotinaColetasId horarioRotinaId) throws ApplicationBusinessException {
		getManterHorarioRotinaColetaCRUD().excluirHorarioRotina(horarioRotinaId);
	}

	@Override
	public void excluirModeloCarta(Short seq) throws ApplicationBusinessException {
		getModeloCartaON().excluirModeloCarta(seq);
	}

	/** FIM 5391 **/

	@Override
	public void excluirParametroCamposLaudo(final AelParametroCamposLaudo parametro) throws BaseException {
		getAelParametroCamposLaudoON().remover(parametro);
	}

	@Override
	@Secure("#{s:hasPermission('manterRecipienteColeta','excluir')}")
	public void excluirRecipienteColeta(Integer seqAelRecipienteColeta) throws ApplicationBusinessException {
		getManterRecipienteColetaRN().removerRecipienteColeta(seqAelRecipienteColeta);
	}

	public AelGrupoMaterialAnaliseDAO getAelGrupoMaterialAnaliseDAO() {
		return aelGrupoMaterialAnaliseDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoRecomendacao','persistir')}")
	public void gravarAelGrupoRecomendacao(AelGrupoRecomendacao grupoRecomendacao, List<AelGrupoRecomendacaoExame> lista)
			throws BaseException {
		this.getGrupoRecomendacaoON().gravar(grupoRecomendacao, lista);
	}

	@Override
	public void inserirAelCampoCodifRelacionado(AelCampoCodifRelacionado elemento) throws BaseException {
		getAelCampoCodifRelacionadoRN().inserir(elemento);
	}

	@Override
	public void inserirAelCampoLaudoRelacionado(AelCampoLaudoRelacionado elemento) throws BaseException {
		this.getAelCampoLaudoRelacionadoRN().inserir(elemento);
	}

	@Override
	public void inserirAelCampoVinculado(AelCampoVinculado campoVinculado) throws IllegalArgumentException, BaseException {
		this.getAelCampoVinculadoRN().inserir(campoVinculado);
	}

	@Override
	public void inserirAelExamesDependentes(AelExamesDependentes exameDependente, List<AelExameDeptConvenio> listaConveniosPlanosDependentes)
			throws ApplicationBusinessException {
		getManterExamesMaterialDependenteRN().inserirAelExamesDependente(exameDependente, listaConveniosPlanosDependentes);
	}

	@Override
	public void inserirAelExamesMaterialAnaliseTipoAmostraExame(AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame) throws ApplicationBusinessException {
		getManterExamesMaterialAnaliseRN()
				.inserirAelExamesMaterialAnaliseTipoAmostraExame(aelExamesMaterialAnalise, listaTiposAmostraExame);
	}

	@Override
	public void inserirAelExecExamesMatAnalise(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException {
		getAelExecExamesMatAnaliseRN().inserir(execExamesMatAnalise);
	}

	@Override
	public void inserirAelGrupoResultadoCaracteristica(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		this.getAelGrupoResultadoCaracteristicaRN().persistir(elemento);
	}

	@Override
	public void inserirAelRecomendacaoExame(AelRecomendacaoExame recomendacaoExame) throws ApplicationBusinessException {
		getManterRecomendacaoRealizacaoExameRN().inserirAelRecomendacaoExame(recomendacaoExame);
	}

	@Override
	public void inserirAelResultadoCaracteristica(AelResultadoCaracteristica resultadoCaracteristica) throws BaseException {
		getAelResultadoCaracteristicaRN().inserir(resultadoCaracteristica);
	}

	@Override
	public void inserirAelResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		this.getAelResultadoPadraoCampoRN().inserir(resultadoPadraoCampo);
	}

	@Override
	public void inserirAelServidorUnidAssinaElet(AghUnidadesFuncionais unidadeFuncional, RapServidores servidor, Boolean ativo)
			throws ApplicationBusinessException {
		getAelServidorUnidAssinaEletON().inserirAelServidorUnidAssinaElet(unidadeFuncional, servidor, ativo);
	}

	@Override
	public void inserirAelSinonimoExame(AelSinonimoExame aelSinonimoExame) throws ApplicationBusinessException {
		getAelSinonimosExamesCRUD().inserirAelSinonimosExames(aelSinonimoExame);
	}

	@Override
	public void inserirAelTipoAmoExameConv(AelTipoAmoExameConv elemento) throws BaseException {
		this.getAelTipoAmoExameConvRN().inserir(elemento);
	}

	/**
	 * #2211 - Manter Cadastro de Equipamentos
	 */

	@Override
	@Secure("#{s:hasPermission('manterEquipamentos','executar')}")
	public void inserirEquipamentos(AelEquipamentos equipamentos) throws BaseException {
		getAelEquipamentoRN().inserir(equipamentos);
	}

	@Override
	public void inserirExameDeptConvenioEmLote(AelExamesDependentes exameDependente,
			List<AelExameDeptConvenio> listaConveniosPlanosDependentes) throws ApplicationBusinessException {
		getManterExamesMaterialDependenteRN().inserirExameDeptConvenioEmLote(exameDependente, listaConveniosPlanosDependentes);
	}

	@Override
	public void inserirExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {
		getManterProvaExameRN().inserirExamesProva(examesProva);
	}

	@Override
	public void inserirHorarioFuncionamentoUnidade(AghHorariosUnidFuncional horariosUnidFuncional) throws ApplicationBusinessException {
		getManterHorarioFuncionamentoUnidadeON().inserirHorarioFuncionamentoUnidade(horariosUnidFuncional);
	}

	@Override
	public void inserirMetodoUnfExame(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {
		getManterMetodoUnfExameRN().inserirMetodoUnfExame(metodoUnfExame);
	}

	@Override
	public AelParametroCamposLaudo inserirNovoCampoTela(AelVersaoLaudo versaoLaudo, DominioObjetoVisual objetoVisual) {
		return getAelParametroCamposLaudoON().inserirNovoCampoTela(versaoLaudo, objetoVisual);
	}

	@Override
	public List<AelTipoAmoExameConv> listarAelTipoAmoExameConvPorTipoAmostraExame(String emaExaSigla, Integer emaManSeq, Integer manSeq,
			DominioOrigemAtendimento origemAtendimento) {
		return this.getAelTipoAmoExameConvDAO().listarAelTipoAmoExameConvPorTipoAmostraExame(emaExaSigla, emaManSeq, manSeq,
				origemAtendimento);
	}

	@Override
	public List<AelResultadoCaracteristica> listarResultadoCaracteristica(String param, Integer calSeq) throws ApplicationBusinessException {
		return this.getResultadoPadraoON().listarResultadoCaracteristica(param, calSeq);
	}

	@Override
	public List<AelResultadosPadrao> listarResultadoPadraoCampoPorExameMaterial(String emaExaSigla, Integer emaManSeq) {
		return this.getAelResultadosPadraoDAO().listarResultadoPadraoCampoPorExameMaterial(emaExaSigla, emaManSeq);
	}

	@Override
	public List<AelSitItemSolicitacoes> listarSituacoesExame(String texto) {
		return getManterSituacaoExameON().listarSituacoesExame(texto);
	}

	@Override
	public void montaFormulaParametroCampoLaudo(AelParametroCamposLaudo parametroCampoLaudo,
			AelParametroCamposLaudo parametroCampoLaudoAdicionado, DominioOpcoesFormulaParametroCamposLaudo opcao) {
		getAelParametroCamposLaudoON().montaFormulaParametroCampoLaudo(parametroCampoLaudo, parametroCampoLaudoAdicionado, opcao);
	}

	@Override
	public List<AelAgrpPesquisas> obterAelAgrpPesquisasList(AelAgrpPesquisas filtros, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getAelAgrpPesquisasDAO().obterAelAgrpPesquisasList(filtros, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long obterAelAgrpPesquisasListCount(AelAgrpPesquisas filtros) {
		return getAelAgrpPesquisasDAO().obterAelAgrpPesquisasListCount(filtros);
	}

	@Override
	public AelAgrpPesquisas obterAelAgrpPesquisasPorId(final Short seq) {
		return getAelAgrpPesquisasDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<AelAgrpPesquisaXExameVO> obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(final AelAgrpPesquisas aelAgrpPesquisas,
			final String filtro, final boolean limitarRegsPai) {
		return getAelAgrpPesquisaXExameDAO().obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(aelAgrpPesquisas, filtro, limitarRegsPai);
	}

	@Override
	public AelAgrpPesquisaXExame obterAelAgrpPesquisaXExamePorId(final Integer seq) {
		return getAelAgrpPesquisaXExameDAO().obterPorChavePrimaria(seq, true, AelAgrpPesquisaXExame.Fields.AGRP_PESQUISA, 
				AelAgrpPesquisaXExame.Fields.SERVIDOR, AelAgrpPesquisaXExame.Fields.UNF_EXECUTA_EXAME);
	}

	@Override
	public AelCadCtrlQualidades obterAelCadCtrlQualidadesPorId(Integer seq) {
		return aelCadCtrlQualidadesDAO.obterAelCadCtrlQualidadesPorId(seq);
	}

	@Override
	public List<AelDadosCadaveres> obterAelDadosCadaveresList(AelDadosCadaveres filtros, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getAelDadosCadaveresDAO().obterAelDadosCadaveresList(filtros, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long obterAelDadosCadaveresListCount(AelDadosCadaveres filtros) {
		return getAelDadosCadaveresDAO().obterAelDadosCadaveresListCount(filtros);
	}

	@Override
	public AelDadosCadaveres obterAelDadosCadaveresPorId(Integer seq) {
		return aelDadosCadaveresDAO.obterAelDadosCadaveresPorId(seq);
	}

	@Override
	public AelDescricoesResulPadrao obterAelDescricoesResulPadrao(Integer rpcRpaSeq, Integer seqP) {
		return this.getResultadoPadraoON().obterAelDescricoesResulPadrao(rpcRpaSeq, seqP);
	}

	@Override
	public AelEquipamentos obterAelEquipamentosId(Short seq) {
		return this.getAelEquipamentosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AelEquipamentos obterAelEquipamentosId(Short seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.getAelEquipamentosDAO().obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	public AelExameReflexo obterAelExameReflexoPorId(final AelExameReflexoId id) {
		return this.getAelExameReflexoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public AelExamesMaterialAnalise obterAelExamesMaterialAnaliseById(AelExamesMaterialAnaliseId id) {
		return this.getAelExamesMaterialAnaliseDAO().obterPorChavePrimaria(id, true, AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE,
				AelExamesMaterialAnalise.Fields.EXAME);
	}

	@Override
	public AelExecExamesMatAnalise obterAelExecExamesMatAnalisePorId(AelExecExamesMatAnaliseId id) {
		return this.getAelExecExamesMatAnaliseDAO().obterPorChavePrimaria(id, true, AelExecExamesMatAnalise.Fields.EQUIPAMENTOS);
	}

	@Override
	public AelGrupoResultadoCaracteristica obterAelGrupoResultadoCaracteristica(Integer seq) {
		return this.getGrupoResultadoCaracteristicaON().obterAelGrupoResultadoCaracteristica(seq);
	}

	@Override
	public AelGrupoResultadoCaracteristica obterAelGrupoResultadoCaracteristica(Integer seq, Enum[] fetchArgsInnerJoin,
			Enum[] fetchArgsLeftJoin) {
		return this.getGrupoResultadoCaracteristicaON().obterAelGrupoResultadoCaracteristica(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	public AelHorarioRotinaColetas obterAelHorarioRotinaColetaPorId(AelHorarioRotinaColetasId horarioId) {
		return getManterHorarioRotinaColetaCRUD().obterAelHorarioRotinaColetaPorId(horarioId);
	}

	/* Manter horário rotina coleta */
	@Override
	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasPorParametros(Short unidadeColeta, Short unidadeSolicitante,
			Date dataHora, String dia, DominioSituacao situacaoHorario) {
		return getManterHorarioRotinaColetaCRUD().obterAelHorarioRotinaColetasPorParametros(unidadeColeta, unidadeSolicitante, dataHora,
				dia, situacaoHorario);
	}

	@Override
	public AelResultadoCodificado obterAelResultadoCodificadoPorId(AelResultadoCodificadoId id) {
		// TODO Auto-generated method stub
		return aelResultadoCodificadoDAO.obterPorChavePrimaria(id);
	}

	@Override
	public AelServidorUnidAssinaElet obterAelServidorUnidAssinaEletPorId(AelServidorUnidAssinaEletId servidorUnidAssinaEletId) {
		return this.getAelServidorUnidAssinaEletDAO().obterPorChavePrimaria(servidorUnidAssinaEletId);
	}

	@Override
	public AelTipoAmoExameConv obterAelTipoAmoExameConvPorId(AelTipoAmoExameConvId id) {
		return this.getAelTipoAmoExameConvDAO().obterPorChavePrimaria(id);
	}

	@Override
	public List<AelCadCtrlQualidades> obterCadCtrlQualidadesList(final AelCadCtrlQualidades filtros, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final boolean asc) {
		return getAelCadCtrlQualidadesDao().obterCadCtrlQualidadesList(filtros, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long obterCadCtrlQualidadesListCount(final AelCadCtrlQualidades filtros) {
		return getAelCadCtrlQualidadesDao().obterCadCtrlQualidadesListCount(filtros);
	}

	@Override
	public List<AelExameDeptConvenio> obterConvenioPlanoDependentes(AelExamesDependentesId id) {
		return this.getManterExamesMaterialDependenteRN().obterConvenioPlanoDependentes(id);
	}

	@Override
	public List<VAelExameMatAnalise> obterExameMaterialAnalise(String siglaDescViewExaMatAnalise) throws ApplicationBusinessException {
		return this.getGrupoRecomendacaoON().obterExameMaterialAnalise(siglaDescViewExaMatAnalise);
	}

    @Override
    public Long obterExameMaterialAnaliseCount(String siglaDescViewExaMatAnalise) throws ApplicationBusinessException {
        return this.getGrupoRecomendacaoON().obterExameMaterialAnaliseCount(siglaDescViewExaMatAnalise);
    }

	@Override
	public AelExamesNotificacao obterExameNotificacao(final AelExamesNotificacaoId exameNotificacaoId) {
		return this.getAelExamesNotificacaoDAO().obterPorChavePrimaria(exameNotificacaoId);
	}

	@Override
	public Short obterExameResultadoNotificacaoNextSeqp(AelExameResuNotificacaoId id) {
		return this.getAelExameResuNotificacaoDAO().obterExameResultadoNotificacaoNextSeqp(id, false);
	}

	@Override
	public List<AelExamesDependentes> obterExamesDependentes(String sigla, Integer manSeq) {
		return this.getManterExamesMaterialDependenteRN().obterExamesDependentes(sigla, manSeq);
	}

	@Override
	public AelGrupoMaterialAnalise obterGrupoMaterialAnalisePorSeq(Integer seq) {
		return this.getAelGrupoMaterialAnaliseDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarLaboratorio/Hemocentro','pesquisar')}")
	public AelLaboratorioExternos obterLaboratorioExternoPorId(Integer seq) {
		return this.getLaboratorioHemocentroON().obterLaboratorioExternoPorId(seq);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarLaboratorio/Hemocentro','pesquisar')}")
	public AelLaboratorioExternos obterLaboratorioExternoPorId(Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.getLaboratorioHemocentroON().obterLaboratorioExternoPorId(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	public Long obterModeloCartaCount(AelModeloCartas modeloCarta) {
		return getAelModeloCartasDAO().pesquisarCount(modeloCarta);
	}

	@Override
	public AelModeloCartas obterModeloCartaPorId(Short seq) {
		return getAelModeloCartasDAO().obterPorChavePrimaria(seq);
	}

	/* final */

	@Override
	public FatConvenioSaudePlano obterPlanoPorId(Byte seqConvenioSaudePlano, Short codConvenioSaude) {
		return this.getManterExamesMaterialDependenteRN().obterPlanoPorId(seqConvenioSaudePlano, codConvenioSaude);
	}

	@Override
	public AelResultadoPadraoCampo obterResultadoPadraoCampoPorParametro(Integer calSeq, Integer seqP, Integer rpaSeq) {
		return this.getResultadoPadraoON().obterResultadoPadraoCampoPorParametro(calSeq, seqP, rpaSeq);
	}

	@Override
	public void persistirAelAgrpPesquisas(AelAgrpPesquisas aelAgrpPesquisas) throws BaseException {
		getAelAgrpPesquisasRN().persistirAelAgrpPesquisas(aelAgrpPesquisas);
	}

	@Override
	public void persistirAelAgrpPesquisaXExame(AelAgrpPesquisaXExame aelAgrpPesquisaXExame) throws BaseException {
		getAelAgrpPesquisaXExameRN().persistirAelAgrpPesquisaXExame(aelAgrpPesquisaXExame);
	}

	@Override
	public void persistirAelAtendimentoDiversos(AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException {
		getAelAtendimentoDiversosRN().persistirAelAtendimentoDiversos(aelAtendimentoDiversos);
	}

	@Override
	public void persistirAelCadCtrlQualidades(AelCadCtrlQualidades aelCadCtrlQualidades) throws BaseException {
		getAelCadCtrlQualidadesRN().persistirAelCadCtrlQualidades(aelCadCtrlQualidades);
	}

	@Override
	public void persistirAelDadosCadaveres(AelDadosCadaveres aelDadosCadaveres) throws BaseException {
		getAelDadosCadaveresRN().persistirAelDadosCadaveres(aelDadosCadaveres);
	}

	@Override
	public void persistirAelDescricoesResulPadrao(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		this.getAelDescricoesResulPadraoRN().persistir(descResultaPadrao);
	}

	@Override
	public void persistirAelExameGrupoCaracteristica(AelExameGrupoCaracteristica aelExameGrupoCaracteristica) throws BaseException {
		this.getAelExameGrupoCaracteristicaRN().persistir(aelExameGrupoCaracteristica);
	}

	@Override
	public void persistirAelExameReflexo(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		this.getAelExameReflexoON().persistirAelExameReflexo(aelExameReflexo);
	}

	@Override
	public void persistirAelExames(AelExames aelExames, String sigla) throws BaseException {
		getManterDadosBasicosExamesCRUD().persistirAelExames(aelExames, sigla);
	}

	@Override
	public void persistirAelGrpTecnicaUnfExames(AelGrpTecnicaUnfExames grpTecnicaUnfExames) throws BaseException {
		this.getManterGrupoExamesCRUD().persistirAelGrpTecnicaUnfExames(grpTecnicaUnfExames);
	}

	@Override
	public void persistirAelGrupoExameTecnicas(AelGrupoExameTecnicas grupoExameTecnicas) throws BaseException {
		getManterGrupoExamesCRUD().persistirAelGrupoExameTecnicas(grupoExameTecnicas);
	}

	@Override
	public void persistirAelLoteExame(AelLoteExame loteExame, Boolean flush) throws BaseException {
		getAelLoteExameON().persistirAelLoteExame(loteExame, flush);
	}

	@Override
	public void persistirAelResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		this.getAelResultadoPadraoCampoRN().persistir(resultadoPadraoCampo);
	}

	@Override
	public void persistirAelServidoresExameUnid(AelServidoresExameUnid aelServidoresExameUnid) throws ApplicationBusinessException {
		getManterPedidoExamesServidoresCRUD().persistirAelServidoresExameUnid(aelServidoresExameUnid);
	}

	@Override
	@Secure("#{s:hasPermission('manterAnticoagulantes','executar')}")
	public AelAnticoagulante persistirAnticoagulante(AelAnticoagulante elemento) throws ApplicationBusinessException {
		return getAelAnticoagulanteON().persistirAnticoagulante(elemento);
	}

	@Override
	public AelCampoLaudo persistirCampoLaudo(AelCampoLaudo campoLaudo) throws BaseException {
		return getAelCampoLaudoRN().persistirCampoLaudo(campoLaudo);
	}

	@Override
	public void persistirCampoLaudoPendencia(AelGrupoTecnicaCampo elemento) throws BaseException {
		this.getAelGrupoTecnicaCampoRN().persistir(elemento);
	}

	@Override
	public void persistirExameEspecialidade(AelExamesEspecialidade exameEspecialidade) throws ApplicationBusinessException {
		getManterRestringirPedidoEspecialidadeCRUD().persistirExameEspecialidade(exameEspecialidade);
	}

	/**
	 * Método para insert e update na tabela AEL_EXAMES_NOTIFICACAO
	 * 
	 * @throws ApplicationBusinessException
	 */

	@Override
	@Secure("#{s:hasPermission('notificacaoResultadoExame','persistir')}")
	public void persistirExameNotificacao(final AelExamesNotificacaoId id, final DominioSituacao situacao)
			throws ApplicationBusinessException {
		this.getExameNotificacaoON().persistirExameNotificacao(id, situacao);
	}

	@Override
	@Secure("#{s:hasPermission('notificacaoResultadoExame','persistir')}")
	public void persistirExameResultadoNotificacao(final AelExameResuNotificacao exameResultadoNotificacao)
			throws ApplicationBusinessException {
		this.getExameResultadoNotificacaoRN().persistir(exameResultadoNotificacao);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','executar')}")
	public void persistirGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException {
		this.getGrupoMaterialAnaliseRN().persistir(grupoMaterialAnalise);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','executar')}")
	public void persistirGrupoXMaterialAnalise(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException {
		this.getGrupoXMaterialAnaliseRN().persistir(grupoXMaterialAnalise);
	}

	/*
	 * Horário de Coleta do Exame
	 */
	@Override
	public void persistirHorarioColetaExame(AelExameHorarioColeta exameHorarioColeta) throws ApplicationBusinessException {
		getManterHorarioColetaExameON().persistirHorarioColetaExame(exameHorarioColeta);
	}

	@Override
	public void persistirHorarioRotina(AelHorarioRotinaColetas horarioRotina) throws ApplicationBusinessException {
		getManterHorarioRotinaColetaCRUD().persistir(horarioRotina);
	}

	@Override
	@Secure("#{s:hasPermission('identificarUnidadeExecutora','executar')}")
	public AelUnidExecUsuario persistirIdentificacaoUnidadeExecutora(AelUnidExecUsuario elemento, AghUnidadesFuncionais unidFuncional)
			throws ApplicationBusinessException {
		return getUnidExecUsuarioON().atualizar(elemento, unidFuncional);
	}

	@Override
	@Secure("#{s:hasPermission('manterIntervaloColeta','persistir')}")
	public AelIntervaloColeta persistirIntervaloColeta(AelIntervaloColeta intervalo) throws ApplicationBusinessException {
		return getManterIntervaloColetaON().persistirIntervaloColeta(intervalo);
	}

	@Override
	@Secure("#{s:hasPermission('manterMaterialAnalise','executar')}")
	public AelMateriaisAnalises persistirMaterialAnalise(AelMateriaisAnalises material) throws ApplicationBusinessException {
		return getManterMaterialAnaliseON().persistirMaterialAnalise(material);
	}

	@Override
	public void persistirModeloCarta(AelModeloCartas modeloCarta) throws ApplicationBusinessException {
		getModeloCartaRN().persistirModeloCarta(modeloCarta);
	}

	@Override
	public void persistirParametroCamposLaudo(final AelParametroCamposLaudo aelParametroCamposLaudos) throws BaseException {
		getAelParametroCamposLaudoON().persistir(aelParametroCamposLaudos);
	}

	@Override
	public void persistirParametroCamposLaudo(final List<AelParametroCamposLaudo> aelParametroCamposLaudos) throws BaseException {
		getAelParametroCamposLaudoON().persistir(aelParametroCamposLaudos);
	}

	/*
	 * #5364 Manter Campo Laudo
	 */

	@Override
	public void persistirParametroCamposLaudo(final List<AelParametroCamposLaudo> aelParametroCamposLaudos, final boolean novaVersao)
			throws BaseException {
		getAelParametroCamposLaudoON().persistir(aelParametroCamposLaudos, novaVersao);
	}

	@Override
	public void recarregaValoresPerdidos(AelParametroCamposLaudo parametro) {
		getAelParametroCamposLaudoON().recarregaValoresPerdidos(parametro);
	}
	
	@Override
	public void persistirPermissaoUnidadeSolicitante(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {
		getManterPermissoesUnidadesSolicitantesCRUD().persistirPermissaoUnidadeSolicitante(aelPermissaoUnidSolic);
	}

	@Override
	@Secure("#{s:hasPermission('manterRegiaoAnatomica','persistir')}")
	public AelRegiaoAnatomica persistirRegiaoAnatomica(AelRegiaoAnatomica elemento) throws ApplicationBusinessException {
		return getManterRegiaoAnatomicaON().persistirRegiaoAnatomica(elemento);
	}

	/*
	 * #5354 Manter Sinônimos do Campo Laudo
	 */

	@Override
	public void persistirResultadoPadrao(AelResultadosPadrao resultadoPadrao) throws BaseException {
		this.getAelResultadosPadraoRN().persistirResultadoPadrao(resultadoPadrao);
	}

	@Override
	public void persistirRetornoCarta(AelRetornoCarta retornoCartaNew) throws ApplicationBusinessException {
		getRetornoCartaRN().persistirRetornoCarta(retornoCartaNew);
	}

	/*
	 * #5367 - Manter Título do Resultado Padrão
	 */

	@Override
	@Secure("#{s:hasPermission('manterSalaExecutoraExames','persistir')}")
	public void persistirSalaExecutoraExame(final AelSalasExecutorasExames salaExecutora) throws ApplicationBusinessException {
		this.getManterSalaExecutoraExameON().persistirSalaExecutoraExame(salaExecutora);
	}

	@Override
	public void persistirSinonimoCampoLaudo(AelSinonimoCampoLaudo sinonimoCampoLaudo) throws BaseException {
		this.getAelSinonimoCampoLaudoRN().persistirSinonimoCampoLaudo(sinonimoCampoLaudo);
	}

	@Override
	public AelSitItemSolicitacoes persistirSituacaoExame(AelSitItemSolicitacoes situacao) throws ApplicationBusinessException {
		return getManterSituacaoExameON().persistirSituacaoExame(situacao);
	}

	@Override
	public void persistirUnidadeExecutoraExames(AelUnfExecutaExames aelUnfExecutaExames, AelExamesMaterialAnalise examesMaterialAnalise)
			throws BaseException {
		getManterUnidadesExecutorasExamesCRUD().persistirUnidadeExecutoraExames(aelUnfExecutaExames, examesMaterialAnalise);

	}

	@Override
	@Secure("#{s:hasPermission('manterUnidadeMedida','persistir')}")
	public AelUnidMedValorNormal persistirUnidadeMedida(AelUnidMedValorNormal elemento) throws ApplicationBusinessException {
		return getUnidadeMedidaON().persistirUnidadeMedida(elemento);
	}

	@Override
	public void persistirVersaoLaudo(AelVersaoLaudo versaoLaudo) throws BaseException {
		this.getAelVersaoLaudoRN().persistir(versaoLaudo);
	}

	@Override
	public void persistMotivoCancelamento(AelMotivoCancelaExames motivoCancelamento) throws BaseException {
		getAelMotivoCancelaExamesCRUD().persistMotivoCancelamento(motivoCancelamento);

	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoRecomendacao','pesquisar')}")
	public List<AelGrupoRecomendacao> pesquisaGrupoRecomendacaoPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelGrupoRecomendacao grupoRecomendacao) {
		return this.getGrupoRecomendacaoON().pesquisaGrupoRecomendacaoPaginada(firstResult, maxResult, orderProperty, asc,
				grupoRecomendacao);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoRecomendacao','pesquisar')}")
	public Long pesquisaGrupoRecomendacaoPaginadaCount(AelGrupoRecomendacao grupoRecomendacao) {
		return this.getGrupoRecomendacaoON().pesquisaGrupoRecomendacaoPaginadaCount(grupoRecomendacao);
	}

	@Override
	public List<AelCampoCodifRelacionado> pesquisarCampoCodificadoPorParametroCampoLaudoECampoLaudo(
			AelParametroCamposLaudo aelParametroCamposLaudo) {
		return this.getAelCampoCodifRelacionadoDAO().pesquisarCampoCodificadoPorParametroCampoLaudoECampoLaudo(aelParametroCamposLaudo);
	}

	@Override
	public List<AelCampoLaudo> pesquisarCampoLaudoExameMaterial(Object param, String emaExaSigla, Integer emaManSeq) {
		return this.getAelCampoLaudoDAO().pesquisarCampoLaudoExameMaterial(param, emaExaSigla, emaManSeq);
	}

	@Override
	public List<AelCampoLaudoRelacionado> pesquisarCampoLaudoPorParametroCampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudo) {
		return getAelCampoLaudoRelacionadoDAO().pesquisarCampoLaudoPorParametroCampoLaudo(aelParametroCamposLaudo);
	}

	@Override
	public List<AelCampoVinculado> pesquisarCampoVinculadoPorParametroCampoLaudo(
			AelParametroCamposLaudo aelParametroCamposLaudoByAelCvcPclFk1) {
		return this.getAelCampoVinculadoDAO().pesquisarCampoVinculadoPorParametroCampoLaudo(aelParametroCamposLaudoByAelCvcPclFk1);
	}

	@Override
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return this.getManterExamesMaterialDependenteRN().pesquisarConvenioSaudePlanos(filtro);
	}

	@Override
	public Long pesquisarConvenioSaudePlanosCount(String filtro) {
		return this.getManterExamesMaterialDependenteRN().pesquisarConvenioSaudePlanosCount(filtro);
	}

	@Override
	public List<AelDadosCadaveres> pesquisarDadosCadaveresInstituicaoHospitalarProcedencia(final Integer ihoSeq){
		return getAelDadosCadaveresDAO().pesquisarDadosCadaveresInstituicaoHospitalarProcedencia(ihoSeq);
	}

	@Override
	public List<AelDadosCadaveres> pesquisarDadosCadaveresInstituicaoHospitalarRetirada(final Integer ihoSeq){
		return getAelDadosCadaveresDAO().pesquisarDadosCadaveresInstituicaoHospitalarRetirada(ihoSeq);
	}

	@Override
	public List<AelExecExamesMatAnalise> pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(AelExamesMaterialAnaliseId id) {
		return this.getAelExecExamesMatAnaliseDAO().pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(id, null);
	}

	@Override
	public List<AelEquipamentos> pesquisarEquipamentosPorSiglaOuDescricao(Object parametro, Short unfSeq)
			throws ApplicationBusinessException {
		return this.getAelEquipamentosDAO().pesquisarEquipamentosPorSiglaOuDescricao(parametro, unfSeq);
	}

	/**
	 * 5361
	 */
	@Override
	public List<AelEquipamentos> pesquisarEquipamentosSeqDescricao(Object parametro) throws ApplicationBusinessException {
		// Utilizado no suggestionbox
		return getAelEquipamentosDAO().pesquisaEquipamentosPorSeqDescricao(parametro);
	}

	@Override
	public Long pesquisarExameGrupoCaracteristicaCount(AelExameGrupoCaracteristica exameGrupoCaracteristica) {
		return this.getAgruparCaracteristicaExameON().pesquisarExameGrupoCarateristicaCount(exameGrupoCaracteristica);
	}

	/** 5375 **/
	@Override
	public List<AelExameGrupoCaracteristicaVO> pesquisarExameGrupoCarateristica(AelExameGrupoCaracteristica exameGrupoCaracteristica,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.getAgruparCaracteristicaExameON().pesquisarExameGrupoCarateristica(exameGrupoCaracteristica, firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	public List<AelExameResuNotificacao> pesquisarExameResultadoNotificacao(String sigla, Integer manSeq, Integer calSeq) {
		return this.getAelExameResuNotificacaoDAO().pesquisarExameResultadoNotificacao(sigla, manSeq, calSeq);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','pesquisar')}")
	public List<AelGrupoMaterialAnalise> pesquisarGrupoMaterialAnalise(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelGrupoMaterialAnalise grupoMaterialAnalise) {
		return this.getAelGrupoMaterialAnaliseDAO().pesquisarGrupoMaterialAnalise(firstResult, maxResult, orderProperty, asc,
				grupoMaterialAnalise);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','pesquisar')}")
	public Long pesquisarGrupoMaterialAnaliseCount(AelGrupoMaterialAnalise grupoMaterialAnalise) {
		return this.getAelGrupoMaterialAnaliseDAO().pesquisarGrupoMaterialAnaliseCount(grupoMaterialAnalise);
	}

	/** 5374 **/
	@Override
	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.getGrupoResultadoCaracteristicaON().pesquisarGrupoResultadoCaracteristica(grupoResultadoCaracteristica, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarGrupoResultadoCaracteristicaCount(AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {

		return this.getGrupoResultadoCaracteristicaON().pesquisarGrupoResultadoCaracteristicaCount(grupoResultadoCaracteristica);
	}

	@Override
	public List<AelGrupoXMaterialAnalise> pesquisarGrupoXMaterialAnalisePorGrupo(Integer gmaSeq) {
		return this.getAelGrupoXMaterialAnaliseDAO().pesquisarGrupoXMateriaisAnalisesPorGrupo(gmaSeq);
	}

	/** FIM 5375 **/

	/** #5927 **/
	@Override
	@Secure("#{s:hasPermission('cadastrarLaboratorio/Hemocentro','pesquisar')}")
	public List<AelLaboratorioExternos> pesquisarLaboratorioHemocentro(final AelLaboratorioExternos filtros, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		return this.getLaboratorioHemocentroON().pesquisaLaboratoioHemocentroPaginado(filtros, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarMedicoAtendimentoExterno','pesquisar')}")
	public List<AghMedicoExterno> pesquisarMedicoExternoPaginado(Map<Object, Object> filtersMap, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		return this.getMedicoAtendimentoExternoON().pesquisaMedicoExternoPaginado(filtersMap, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public List<AelModeloCartas> pesquisarModeloCarta(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AelModeloCartas modeloCarta) {
		return getAelModeloCartasDAO().pesquisar(firstResult, maxResult, orderProperty, asc, modeloCarta);
	}

	@Override
	public List<AelRetornoCarta> pesquisarRetornoCarta(Integer filtroSeq, String filtroDescricaoRetorno, DominioSituacao filtroIndSituacao) {
		return getAelRetornoCartaDAO().pesquisarRetornoCarta(filtroSeq, filtroDescricaoRetorno, filtroIndSituacao);
	}

	@Override
	public List<AelServidorUnidAssinaElet> pesquisarServidorUnidAssinaEletPorUnfSeq(Short seq) {
		return getAelServidorUnidAssinaEletDAO().pesquisarServidorUnidAssinaEletPorUnfSeq(seq);
	}

	@Override
	public List<AelSinonimoExame> pesquisarSinonimosExames(AelExames exame, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getAelSinonimosExamesCRUD().pesquisarSinonimosExames(exame, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarSinonimosExamesCount(AelExames exame) {
		return getAelSinonimosExamesCRUD().pesquisarSinonimosExamesCount(exame);
	}

	@Override
	public List<AelVersaoLaudo> pesquisarVersaoLaudo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncional, AelVersaoLaudo versaoLaudo) {
		return this.getAelVersaoLaudoDAO().pesquisarVersaoLaudo(firstResult, maxResult, orderProperty, asc, unidadeFuncional, versaoLaudo);
	}

	/** 5391 **/
	@Override
	public Long pesquisarVersaoLaudoCount(AghUnidadesFuncionais unidadeFuncional, AelVersaoLaudo versaoLaudo) {
		return this.getAelVersaoLaudoDAO().pesquisarVersaoLaudoCount(unidadeFuncional, versaoLaudo);
	}

	/** FIM 5374 **/

	@Override
	public void removerAelDescricoesResulPadrao(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		this.getAelDescricoesResulPadraoRN().remover(descResultaPadrao);
	}

	@Override
	public void removerAelExameGrupoCaracteristica(AelExameGrupoCaracteristicaId id) throws BaseException {
		this.getAelExameGrupoCaracteristicaRN().remover(id);
	}

	@Override
	public void removerAelExames(AelExames aelExames) throws BaseException {
		getManterDadosBasicosExamesCRUD().removerAelExames(aelExames);
	}

	@Override
	public void removerAelExamesDependentes(AelExamesDependentes exameDependente) throws BaseException {
		getManterExamesMaterialDependenteRN().removerExameDependente(exameDependente);
	}

	@Override
	public void removerAelExamesMaterialAnalise(AelExamesMaterialAnalise aelExamesMaterialAnalise) throws BaseException {
		getManterExamesMaterialAnaliseRN().removerAelExamesMaterialAnalise(aelExamesMaterialAnalise);
	}

	@Override
	public void removerAelGrpTecnicaUnfExames(AelGrpTecnicaUnfExamesId id) throws BaseException {
		this.getManterGrupoExamesCRUD().removerAelGrpTecnicaUnfExames(id);
	}

	@Override
	public void removerAelGrupoExameTecnicas(Integer seq) throws BaseException {
		getManterGrupoExamesCRUD().removerAelGrupoExameTecnicas(seq);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoRecomendacao','excluir')}")
	public void removerAelGrupoRecomendacao(Integer seqExclusao) throws BaseException {
		getGrupoRecomendacaoON().remover(seqExclusao);
	}

	@Override
	public void removerAelLoteExame(AelLoteExameId loteExameId) throws ApplicationBusinessException {
		getAelLoteExameON().removerAelLoteExame(loteExameId);
	}

	@Override
	public void removerAelPermissaoUnidSolic(String emaExaSigla, Integer emaManSeq, Short unfSeq, Short unfSeqSolicitante)
			throws BaseException {
		getManterPermissoesUnidadesSolicitantesCRUD().removerAelPermissaoUnidSolic(emaExaSigla, emaManSeq, unfSeq, unfSeqSolicitante);
	}

	@Override
	public void removerAelRecomendacaoExame(AelRecomendacaoExame novoRecomendacaoExame) {
		getManterRecomendacaoRealizacaoExameRN().removerAelRecomendacaoExame(novoRecomendacaoExame);
	}

	@Override
	public void removerAelResultadoCaracteristica(Integer seq) throws BaseException {
		getAelResultadoCaracteristicaRN().remover(seq);
	}

	@Override
	public void removerAelResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		this.getAelResultadoPadraoCampoRN().remover(resultadoPadraoCampo);
	}

	@Override
	public void removerAelServidoresExameUnid(String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq, Integer serMatricula,
			Short serVinCodigo) throws ApplicationBusinessException {
		getManterPedidoExamesServidoresCRUD().removerAelServidoresExameUnid(ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq, serMatricula,
				serVinCodigo);
	}

	@Override
	public void removerAelSinonimosExames(AelSinonimoExame aelSinonimoExame) throws ApplicationBusinessException {
		getAelSinonimosExamesCRUD().removerAelSinonimosExames(aelSinonimoExame);
	}

	@Override
	public void removerAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws BaseException {
		getManterTipoAmostraExameRN().removerAelTipoAmostraExame(tipoAmostraExame);
	}

	@Override
	@Secure("#{s:hasPermission('manterAnticoagulantes','executar')}")
	public void removerAnticoagulante(Integer seqAnticoagulante) throws ApplicationBusinessException {
		getAelAnticoagulanteRN().remover(seqAnticoagulante);
	}

	@Override
	public void removerCampoLaudo(Integer seq) throws BaseException {
		this.getAelCampoLaudoRN().remover(seq);
	}

	@Override
	@Secure("#{s:hasPermission('manterEquipamentos','executar')}")
	public void removerEquipamento(AelEquipamentos equipamentos) throws BaseException {
		getAelEquipamentoRN().remover(equipamentos);
	}

	@Override
	public void removerExameEspecialidade(String emaExaSigla, Integer emaManSeq, Short unfSeq, Short espSeq)
			throws ApplicationBusinessException {
		getManterRestringirPedidoEspecialidadeCRUD().removerExameEspecialidade(emaExaSigla, emaManSeq, unfSeq, espSeq);
	}

	@Override
	public void removerExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {
		getManterProvaExameRN().removerExamesProva(examesProva);
	}

	@Override
	public void removerExecExamesMatAnalise(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException {
		getAelExecExamesMatAnaliseRN().remover(execExamesMatAnalise);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','executar')}")
	public void removerGrupoMaterialAnalise(Integer seqExclusao) throws ApplicationBusinessException {
		this.getGrupoMaterialAnaliseRN().remover(seqExclusao);
	}

	@Override
	public AelGrupoResultadoCaracteristica removerGrupoResultadoCaracteristica(Integer seqExclusao) throws BaseException {
		return this.getGrupoResultadoCaracteristicaON().removerGrupoResultadoCaracteristica(seqExclusao);
	}

	@Override
	@Secure("#{s:hasPermission('manterGrupoMaterialAnalise','executar')}")
	public void removerGrupoXMaterialAnalise(AelGrupoXMaterialAnaliseId id) {
		this.getGrupoXMaterialAnaliseRN().remover(id);
	}

	@Override
	public void removerHorarioColetaExame(AelExameHorarioColeta horarioColetaExame) throws ApplicationBusinessException {
		getManterHorarioColetaExameON().removerHorarioColetaExame(horarioColetaExame);
	}

	@Override
	public void removerHorarioFuncionamentoUnidade(AghHorariosUnidFuncionalId id) throws ApplicationBusinessException {
		getManterHorarioFuncionamentoUnidadeON().removerHorarioFuncionamentoUnidade(id);
	}

	@Override
	@Secure("#{s:hasPermission('manterIntervaloColeta','excluir')}")
	public void removerIntervaloColeta(Short codigo) throws ApplicationBusinessException {
		getManterIntervaloColetaON().removerIntervaloColeta(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarLaboratorio/Hemocentro','executar')}")
	public void removerLaboratorioExterno(Integer seqExclusao) throws ApplicationBusinessException, BaseException {
		getLaboratorioHemocentroRN().removerLaboratorioExterno(seqExclusao);
	}

	@Override
	public void removerListaDependentes(AelExamesDependentes exameDependente) throws BaseException {
		getManterExamesMaterialDependenteRN().removerListaDependentes(exameDependente);
	}

	@Override
	@Secure("#{s:hasPermission('manterMaterialAnalise','executar')}")
	public void removerMaterialAnalise(Integer codigo) throws ApplicationBusinessException {
		getManterMaterialAnaliseON().removerMaterialAnalise(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarMedicoAtendimentoExterno','executar')}")
	public void removerMedicoAtendimentoExterno(Integer seqExclusao) throws ApplicationBusinessException, BaseException {
		getMedicoAtendimentoExternoRN().remover(seqExclusao);
	}

	@Override
	public void removerMotivoCancelamento(Short seqAelMotivoCancelaExames) throws BaseException {
		getAelMotivoCancelaExamesCRUD().removerMotivoCancelamento(seqAelMotivoCancelaExames);
	}

	@Override
	@Secure("#{s:hasPermission('manterRegiaoAnatomica','excluir')}")
	public void removerRegiaoAnatomica(Integer seqRegiaoAnatomica) throws ApplicationBusinessException {
		getManterRegiaoAnatomicaON().removerRegiaoAnatomica(seqRegiaoAnatomica);
	}

	@Override
	@Secure("#{s:hasPermission('manterSalaExecutoraExames','persistir')}")
	public void removerSalaExecutoraExame(final AelSalasExecutorasExamesId id) throws ApplicationBusinessException {
		this.getManterSalaExecutoraExameON().excluirSalaExecutoraExame(id);
	}

	@Override
	public void removerUnidadeExecutoraExames(String emaExaSigla, Integer emaManSeq, Short unfSeq) throws BaseException {
		getManterUnidadesExecutorasExamesCRUD().removerUnidadeExecutoraExames(emaExaSigla, emaManSeq, unfSeq);
	}
	
	@Override
	public void persistirProtocolo(Map<Integer, Vector<Short>> listaItens, AelProtocoloEntregaExames protocolo) throws ApplicationBusinessException {
		getAelProtocoloEntregaExamesRN().persistirProtocolo(listaItens, protocolo);
	}
	
	@Override
	public void persistirNovoProtocolo(AelProtocoloEntregaExames protocolo) throws ApplicationBusinessException {
		getAelProtocoloEntregaExamesRN().persistirNovoProtocolo(protocolo);
	}
	
	@Override
	public ProtocoloEntregaExamesVO recuperarRelatorioEntregaExames(Map<Integer, Vector<Short>> listaItens, AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes) {
		return getAelProtocoloEntregaExamesRN().recuperarRelatorioEntregaExames(listaItens, protocolo, listaPacientes);
	}
	
	@Override
	public ProtocoloEntregaExamesVO recuperarNovoRelatorioEntregaExames(AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes) {
		return getAelProtocoloEntregaExamesRN().recuperarNovoRelatorioEntregaExames(protocolo, listaPacientes);
	}

	@Override
	public void removerVersaoLaudo(AelVersaoLaudoId id) throws BaseException {
		this.getAelVersaoLaudoRN().remover(id);
	}

	@Override
	public void salvarProcedimentosRelacionados(FatProcedHospInternos fatProcedHospInternos,
			List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO, Short cpgGrcSeq, Short cpgCphPhoSeq)
			throws ApplicationBusinessException {
		this.getManterExamesMaterialAnaliseON().salvarProcedimentosRelacionados(fatProcedHospInternos, listaFatProcedimentoRelacionadosVO,
				cpgGrcSeq, cpgCphPhoSeq);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarLaboratorio/Hemocentro','executar')}")
	public void saveOrUpdateLaboratorioHemocentro(AelLaboratorioExternos laboratorioExternos) throws BaseException {
		this.getLaboratorioHemocentroON().saveOrLaboratoioHemocentro(laboratorioExternos);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarMedicoAtendimentoExterno','executar') or s:hasPermission('gerarExamesIntegracao','inserir')}")
	public void saveOrUpdateMedicoExterno(AghMedicoExterno medicoExterno) throws BaseException {
		this.getMedicoAtendimentoExternoON().saveOrUpdateMedicoExterno(medicoExterno);
	}

	@Override
	@Secure("#{s:hasPermission('manterRecipienteColeta','persistir')}")
	public void saveOrUpdateRecipienteColeta(AelRecipienteColeta recipienteColeta) throws BaseException {
		getManterRecipienteColetaON().saveOrUpdateRecipienteColeta(recipienteColeta);
	}

	@Override
	public List<AelResultadoCodificado> sbListarResultadoCodificado(String param, Integer calSeq) throws ApplicationBusinessException {
		return this.getResultadoPadraoON().sbListarResultadoCodificado(param, calSeq);
	}

	@Override
	public List<AelResultadosPadrao> sbListarResultadosPadrao(String param) throws ApplicationBusinessException {
		return this.getResultadoPadraoON().listarResultadosPadraoPorSeqOuDescricao(param);
	}

	@Override
	public void validaCampoLaudo(AelParametroCamposLaudo parametroCamposLaudo) throws ApplicationBusinessException {
		getAelParametroCamposLaudoRN().validaCampoLaudo(parametroCamposLaudo);
	}

	@Override
	public void validaConvenioPlanoJaInserido(List<AelExameDeptConvenio> listaConveniosPlanosDependentes,
			AelExameDeptConvenioId exaConvIdToCompare) throws ApplicationBusinessException {
		getManterExamesMaterialDependenteRN().validaConvenioPlanoJaInserido(listaConveniosPlanosDependentes, exaConvIdToCompare);
	}

	@Override
	public boolean validarAdicionarItemTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame,
			AelExamesMaterialAnalise examesMaterialAnalise, List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {
		return getManterExamesMaterialAnaliseON().validarAdicionarItemTipoAmostraExame(tipoAmostraExame, examesMaterialAnalise,
				listaTiposAmostraExame);
	}

	@Override
	public AelGrupoMaterialAnalise validarCamposGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise)
			throws ApplicationBusinessException {
		return this.getGrupoMaterialAnaliseON().validarCampos(grupoMaterialAnalise);
	}
	
	@Override
	public void criarAghResponsavelJn(AghResponsavel responsavel, DominioOperacoesJournal operacao){
		getAghResponsavelON().criarAghResponsavelJn(responsavel, operacao);
	}

	@Override
	public void validarExpressaoFormula(String formula) throws ApplicationBusinessException {
		getAelParametroCamposLaudoON().validarExpressaoFormula(formula);
	}
	
	@Override
	public AelResultadosPadrao obterAelResultadoPadraoPorId(Integer seq) {
		return getAelResultadosPadraoDAO().obterPorChavePrimaria(seq, true, AelResultadosPadrao.Fields.EXAME_MATERIAL_ANALISE, AelResultadosPadrao.Fields.SERVIDOR);
	}

	/**
	 * oradb aelk_exa_rn.rn_exap_ver_delecao
	 * 
	 * @param data
	 * @param aghuParametrosEnum
	 * @param exceptionForaPeriodoPermitido
	 * @param erroRecuperacaoAghuParametro
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void verificaDataCriacao(final Date data, final AghuParametrosEnum aghuParametrosEnum,
			BusinessExceptionCode exceptionForaPeriodoPermitido, BusinessExceptionCode erroRecuperacaoAghuParametro) throws BaseException {
		getAelExamesCRUD().verificaDataCriacao(data, aghuParametrosEnum, exceptionForaPeriodoPermitido, erroRecuperacaoAghuParametro);
	}

	@Override
	public void verificarSituacaoDuplicada(AelVersaoLaudo versaoLaudo) throws BaseException {
		this.getAelVersaoLaudoRN().verificarSituacaoDuplicada(versaoLaudo);
	}

	@Override
	public void verificarTiposAmostraExame(AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame) throws ApplicationBusinessException {
		this.getManterExamesMaterialAnaliseRN().verificarTiposAmostraExame(aelExamesMaterialAnalise, listaTiposAmostraExame);
	}

		
	@Override
	public void salvarResponsavel(AghResponsavel responsavel, Integer seqAinRep) throws ApplicationBusinessException {
		this.getAghResponsavelON().salvarResponsavel(responsavel, seqAinRep);
	}
	
	@Override 
	public AghResponsavel obterResponsavelPorPaciente(AipPacientes paciente) {
		return this.aghResponsaveldao.obterResponsavelPorPaciente(paciente);
	}
	
	@Override
	public AghResponsavel obterResponsavelPorSeq(Integer seq){
		return this.aghResponsaveldao.obterResponsavelPorSeq(seq);
	}
	
	@Override
	public List<ResponsavelVO> listarResponsavel(String parametro) {
		return this.getAghResponsavelON().listarResponsavel(parametro);
	}
	
	@Override
	public ResponsavelVO obterResponsavelVo(AghResponsavel resp){
		return this.getAghResponsavelON().obterResponsavelVo(resp);
	}
	/**
	 * ORADB aelk_ael_rn.rn_aelp_ver_unf_ativ
	 * 
	 * @param aghUnidadesFuncionais
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void verUnfAtiv(AghUnidadesFuncionais aghUnidadesFuncionais) throws ApplicationBusinessException {
		getManterUnidadesExecutorasExamesCRUD().verUnfAtiv(aghUnidadesFuncionais);
	}

	@Override
	public AelParametroCamposLaudo obterAelParametroCamposLaudoPorId(AelParametroCampoLaudoId id){
		return getAelParametroCamposLaudoDAO().obterCampoLaudoPorChavePrimaria(id);
	}
	
	private AelAgrpPesquisasDAO getAelAgrpPesquisasDAO() {
		return aelAgrpPesquisasDAO;
	}

	private AelAgrpPesquisasRN getAelAgrpPesquisasRN() {
		return aelAgrpPesquisasRN;
	}
	
	private AelProtocoloEntregaExamesRN getAelProtocoloEntregaExamesRN() {
		return aelProtocoloEntregaExamesRN ;
	}

	private AelAgrpPesquisaXExameDAO getAelAgrpPesquisaXExameDAO() {
		return aelAgrpPesquisaXExameDAO;
	}

	private AelAgrpPesquisaXExameRN getAelAgrpPesquisaXExameRN() {
		return aelAgrpPesquisaXExameRN;
	}

	private AelAtendimentoDiversosRN getAelAtendimentoDiversosRN() {
		return aelAtendimentoDiversosRN;
	}

	private AelCadCtrlQualidadesDAO getAelCadCtrlQualidadesDao() {
		return aelCadCtrlQualidadesDAO;
	}

	private AelCadCtrlQualidadesRN getAelCadCtrlQualidadesRN() {
		return aelCadCtrlQualidadesRN;
	}

	private AelCampoLaudoRN getAelCampoLaudoRN() {
		return aelCampoLaudoRN;
	}

	private AelDadosCadaveresDAO getAelDadosCadaveresDAO() {
		return aelDadosCadaveresDAO;
	}

	private AelDadosCadaveresRN getAelDadosCadaveresRN() {
		return aelDadosCadaveresRN;
	}

	private AelResultadosPadraoRN getAelResultadosPadraoRN() {
		return aelResultadosPadraoRN;
	}

	private AelSinonimoCampoLaudoRN getAelSinonimoCampoLaudoRN() {
		return aelSinonimoCampoLaudoRN;
	}

	protected AnticoagulanteON getAelAnticoagulanteON() {
		return anticoagulanteON;
	}

	protected AnticoagulanteRN getAelAnticoagulanteRN() {
		return anticoagulanteRN;
	}

	protected AelCampoCodifRelacionadoDAO getAelCampoCodifRelacionadoDAO() {
		return aelCampoCodifRelacionadoDAO;
	}

	protected AelCampoCodifRelacionadoRN getAelCampoCodifRelacionadoRN() {
		return aelCampoCodifRelacionadoRN;
	}

	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}

	protected AelCampoLaudoRelacionadoDAO getAelCampoLaudoRelacionadoDAO() {
		return aelCampoLaudoRelacionadoDAO;
	}

	protected AelCampoLaudoRelacionadoRN getAelCampoLaudoRelacionadoRN() {
		return aelCampoLaudoRelacionadoRN;
	}

	protected AelCampoVinculadoDAO getAelCampoVinculadoDAO() {
		return aelCampoVinculadoDAO;
	}

	protected AelCampoVinculadoRN getAelCampoVinculadoRN() {
		return aelCampoVinculadoRN;
	}

	protected AelDescricoesResulPadraoRN getAelDescricoesResulPadraoRN() {
		return aelDescricoesResulPadraoRN;
	}

	protected AelEquipamentoRN getAelEquipamentoRN() {
		return aelEquipamentoRN;
	}

	protected AelEquipamentosDAO getAelEquipamentosDAO() {
		return aelEquipamentosDAO;
	}

	protected AelExameGrupoCaracteristicaRN getAelExameGrupoCaracteristicaRN() {
		return aelExameGrupoCaracteristicaRN;
	}

	protected AelExameReflexoDAO getAelExameReflexoDAO() {
		return aelExameReflexoDAO;
	}

	protected AelExameReflexoON getAelExameReflexoON() {
		return aelExameReflexoON;
	}

	protected AelExameResuNotificacaoDAO getAelExameResuNotificacaoDAO() {
		return aelExameResuNotificacaoDAO;
	}

	protected AelExamesCRUD getAelExamesCRUD() {
		return aelExamesCRUD;
	}

	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

	protected AelExamesNotificacaoDAO getAelExamesNotificacaoDAO() {
		return aelExamesNotificacaoDAO;
	}

	protected AelExecExamesMatAnaliseDAO getAelExecExamesMatAnaliseDAO() {
		return aelExecExamesMatAnaliseDAO;
	}

	protected AelExecExamesMatAnaliseRN getAelExecExamesMatAnaliseRN() {
		return aelExecExamesMatAnaliseRN;
	}

	protected AelGrupoResultadoCaracteristicaRN getAelGrupoResultadoCaracteristicaRN() {
		return aelGrupoResultadoCaracteristicaRN;
	}

	protected AelGrupoTecnicaCampoRN getAelGrupoTecnicaCampoRN() {
		return aelGrupoTecnicaCampoRN;
	}

	protected AelGrupoXMaterialAnaliseDAO getAelGrupoXMaterialAnaliseDAO() {
		return aelGrupoXMaterialAnaliseDAO;
	}

	protected AelLoteExameON getAelLoteExameON() {
		return aelLoteExameON;
	}

	protected AelModeloCartasDAO getAelModeloCartasDAO() {
		return aelModeloCartasDAO;
	}

	protected AelMotivoCancelaExamesCRUD getAelMotivoCancelaExamesCRUD() {
		return aelMotivoCancelaExamesCRUD;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}

	protected AelParametroCamposLaudoON getAelParametroCamposLaudoON() {
		return aelParametroCamposLaudoON;
	}

	protected AelParametroCamposLaudoRN getAelParametroCamposLaudoRN() {
		return aelParametroCamposLaudoRN;
	}

	protected AelResultadoCaracteristicaRN getAelResultadoCaracteristicaRN() {
		return aelResultadoCaracteristicaRN;
	}

	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}

	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
		return aelResultadoPadraoCampoDAO;
	}

	protected AelResultadoPadraoCampoRN getAelResultadoPadraoCampoRN() {
		return aelResultadoPadraoCampoRN;
	}

	protected AelResultadosPadraoDAO getAelResultadosPadraoDAO() {
		return aelResultadosPadraoDAO;
	}

	protected AelRetornoCartaDAO getAelRetornoCartaDAO() {
		return aelRetornoCartaDAO;
	}

	protected AelServidorUnidAssinaEletDAO getAelServidorUnidAssinaEletDAO() {
		return aelServidorUnidAssinaEletDAO;
	}

	protected AelServidorUnidAssinaEletON getAelServidorUnidAssinaEletON() {
		return aelServidorUnidAssinaEletON;
	}

	protected AelSinonimosExamesCRUD getAelSinonimosExamesCRUD() {
		return aelSinonimosExamesCRUD;
	}

	protected AelTipoAmoExameConvDAO getAelTipoAmoExameConvDAO() {
		return aelTipoAmoExameConvDAO;
	}

	protected AelTipoAmoExameConvRN getAelTipoAmoExameConvRN() {
		return aelTipoAmoExameConvRN;
	}

	protected AelVersaoLaudoDAO getAelVersaoLaudoDAO() {
		return aelVersaoLaudoDAO;
	}

	protected AelVersaoLaudoRN getAelVersaoLaudoRN() {
		return aelVersaoLaudoRN;
	}

	protected AgruparCaracteristicaExameON getAgruparCaracteristicaExameON() {
		return agruparCaracteristicaExameON;
	}

	protected ExameNotificacaoON getExameNotificacaoON() {
		return exameNotificacaoON;
	}

	protected ExameNotificacaoRN getExameNotificacaoRN() {
		return exameNotificacaoRN;
	}

	protected ExameResultadoNotificacaoRN getExameResultadoNotificacaoRN() {
		return exameResultadoNotificacaoRN;
	}

	protected GrupoMaterialAnaliseON getGrupoMaterialAnaliseON() {
		return grupoMaterialAnaliseON;
	}

	protected GrupoMaterialAnaliseRN getGrupoMaterialAnaliseRN() {
		return grupoMaterialAnaliseRN;
	}

	protected GrupoRecomendacaoON getGrupoRecomendacaoON() {
		return grupoRecomendacaoON;
	}

	protected GrupoResultadoCaracteristicaON getGrupoResultadoCaracteristicaON() {
		return grupoResultadoCaracteristicaON;
	}

	protected GrupoXMaterialAnaliseRN getGrupoXMaterialAnaliseRN() {
		return grupoXMaterialAnaliseRN;
	}

	/** FIM #5927 **/

	protected LaboratorioHemocentroON getLaboratorioHemocentroON() {
		return laboratorioHemocentroON;
	}

	protected LaboratorioHemocentroRN getLaboratorioHemocentroRN() {
		return laboratorioHemocentroRN;
	}

	protected ManterDadosBasicosExamesCRUD getManterDadosBasicosExamesCRUD() {
		return manterDadosBasicosExamesCRUD;
	}

	protected ManterExamesMaterialAnaliseON getManterExamesMaterialAnaliseON() {
		return manterExamesMaterialAnaliseON;
	}

	protected ManterExamesMaterialAnaliseRN getManterExamesMaterialAnaliseRN() {
		return manterExamesMaterialAnaliseRN;
	}

	protected ManterExamesMaterialDependenteRN getManterExamesMaterialDependenteRN() {
		return manterExamesMaterialDependenteRN;
	}

	protected ManterGrupoExamesCRUD getManterGrupoExamesCRUD() {
		return manterGrupoExamesCRUD;
	}

	protected ManterHorarioColetaExameON getManterHorarioColetaExameON() {
		return manterHorarioColetaExameON;
	}

	protected ManterHorarioFuncionamentoUnidadeON getManterHorarioFuncionamentoUnidadeON() {
		return manterHorarioFuncionamentoUnidadeON;
	}

	protected ManterHorarioRotinaColetaCRUD getManterHorarioRotinaColetaCRUD() {
		return manterHorarioRotinaColetaCRUD;
	}

	protected ManterIntervaloColetaON getManterIntervaloColetaON() {
		return manterIntervaloColetaON;
	}

	protected ManterMaterialAnaliseON getManterMaterialAnaliseON() {
		return manterMaterialAnaliseON;
	}

	protected ManterMetodoUnfExameRN getManterMetodoUnfExameRN() {
		return manterMetodoUnfExameRN;
	}

	protected ManterPedidoExamesServidoresCRUD getManterPedidoExamesServidoresCRUD() {
		return manterPedidoExamesServidoresCRUD;
	}

	protected ManterPermissoesUnidadesSolicitantesCRUD getManterPermissoesUnidadesSolicitantesCRUD() {
		return manterPermissoesUnidadesSolicitantesCRUD;
	}

	protected ManterProvaExameRN getManterProvaExameRN() {
		return manterProvaExameRN;
	}

	protected ManterRecipienteColetaON getManterRecipienteColetaON() {
		return manterRecipienteColetaON;
	}

	protected ManterRecipienteColetaRN getManterRecipienteColetaRN() {
		return manterRecipienteColetaRN;
	}

	protected ManterRecomendacaoRealizacaoExameRN getManterRecomendacaoRealizacaoExameRN() {
		return manterRecomendacaoRealizacaoExameRN;
	}

	protected ManterRegiaoAnatomicaON getManterRegiaoAnatomicaON() {
		return manterRegiaoAnatomicaON;
	}

	protected ManterRestringirPedidoEspecialidadeCRUD getManterRestringirPedidoEspecialidadeCRUD() {
		return manterRestringirPedidoEspecialidadeCRUD;
	}

	protected ManterSalaExecutoraExameON getManterSalaExecutoraExameON() {
		return manterSalaExecutoraExameON;
	}

	protected ManterSituacaoExameON getManterSituacaoExameON() {
		return manterSituacaoExameON;
	}

	protected ManterTipoAmostraExameRN getManterTipoAmostraExameRN() {
		return manterTipoAmostraExameRN;
	}

	protected ManterUnidadesExecutorasExamesCRUD getManterUnidadesExecutorasExamesCRUD() {
		return manterUnidadesExecutorasExamesCRUD;
	}

	protected MedicoAtendimentoExternoON getMedicoAtendimentoExternoON() {
		return medicoAtendimentoExternoON;
	}

	protected MedicoAtendimentoExternoRN getMedicoAtendimentoExternoRN() {
		return medicoAtendimentoExternoRN;
	}

	protected ModeloCartaON getModeloCartaON() {
		return modeloCartaON;
	}
	
	protected ModeloCartaRN getModeloCartaRN() {
		return modeloCartaRN;
	}

	protected ResultadoPadraoON getResultadoPadraoON() {
		return resultadoPadraoON;
	}

	protected RetornoCartaRN getRetornoCartaRN() {
		return retornoCartaRN;
	}

	protected UnidadeMedidaON getUnidadeMedidaON() {
		return unidadeMedidaON;
	}

	protected UnidExecUsuarioON getUnidExecUsuarioON() {
		return unidExecUsuarioON;
	}

	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}

	public AghResponsavelON getAghResponsavelON() {
		return aghResponsavelON;
	}
	@Override
	public FatProcedHospInternos insereProcedimentoHospitalarInterno(AelExamesMaterialAnalise materialExameSelecionado) throws ApplicationBusinessException {
		return manterExamesMaterialAnaliseRN.inserirFatProcedHospInternos(materialExameSelecionado);
	}

	@Override
	public Boolean phiJaCriado(AelExames exame, AelExamesMaterialAnalise materialAnalise) {
		FatProcedHospInternos phiRetornado = aelExamesDAO.phiJaCadastrado(exame, materialAnalise);
		if (phiRetornado != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override
	public FatProcedHospInternos pesquisaPHI(AelExames exame, AelExamesMaterialAnalise materialAnalise) {
		return aelExamesDAO.phiJaCadastrado(exame, materialAnalise);
	}

}
