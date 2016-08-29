package br.gov.mec.aghu.controleinfeccao.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.ListaPacientesCCIHVO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.VAghUnidFuncionalDAO;
import br.gov.mec.aghu.controleinfeccao.MciBacteriasAssociadasVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciAntimicrobianosDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciBacteriaMultirDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciCriterioGmrDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciDuracaoMedidaPreventivaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciDuracaoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciExportacaoDadoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciFatorPredisponentesDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciGrupoReportRotinaCciDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMicroorganismoPatologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoFatorPredisponentesDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoProcedimentoRiscosDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotasCCIHDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciParamReportGrupoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciParamReportUsuarioDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPatologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciProcedimentoRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTipoGrupoProcedRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaMultirresistenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaPacienteVO;
import br.gov.mec.aghu.controleinfeccao.vo.CriteriosBacteriaAntimicrobianoVO;
import br.gov.mec.aghu.controleinfeccao.vo.DoencaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.ExportacaoDadoVO;
import br.gov.mec.aghu.controleinfeccao.vo.FatorPredisponenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.GMRPacienteVO;
import br.gov.mec.aghu.controleinfeccao.vo.GrupoReportRotinaCciVO;
import br.gov.mec.aghu.controleinfeccao.vo.LocaisOrigemInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoFatorPredisponenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoMedidasPreventivasVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoProcedimentoRiscoVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoTopografiasVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.controleinfeccao.vo.ParamReportGrupoVO;
import br.gov.mec.aghu.controleinfeccao.vo.ParamReportUsuarioVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioBuscaAtivaPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioNotificGermeMultirresistenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.TipoGrupoRiscoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.ResultadoCodificadoExameVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.model.MciCriterioGmr;
import br.gov.mec.aghu.model.MciCriterioGmrId;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.model.MciExportacaoDado;
import br.gov.mec.aghu.model.MciFatorPredisponentes;
import br.gov.mec.aghu.model.MciGrupoReportRotinaCci;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
import br.gov.mec.aghu.model.MciNotasCCIH;
import br.gov.mec.aghu.model.MciNotificacaoGmr;
import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.model.MciParamReportGrupo;
import br.gov.mec.aghu.model.MciParamReportGrupoId;
import br.gov.mec.aghu.model.MciParamReportUsuario;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciProcedimentoRisco;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.model.RapServidores;

@Modulo(ModuloEnum.CONTROLE_INFECCAO)
@Stateless
@SuppressWarnings("PMD.ExcessiveClassLength")
public class ControleInfeccaoFacade extends BaseFacade implements IControleInfeccaoFacade {
	
    private static final long serialVersionUID = -1780402776819605250L;
	
	@EJB
	private MciPatologiaInfeccaoRN mciPatologiaInfeccaoRN;
	
	@EJB
	private MciFatorPredisponentesRN mciFatorPredisponentesRN;
	@EJB
	private MciGrupoReportRotinaCciRN mciGrupoReportRotinaCciRN;
	@EJB
	private MciParamReportGrupoRN mciParamReportGrupoRN;
	@EJB
	private ControleInfeccaoON controleInfeccaoON;
	
	@EJB
	private OrigemInfeccaoON origemInfeccaoON;
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	private MciDuracaoMedidasPreventivasRN mciDuracaoMedidasPreventivasRN;
	
	@EJB
	private MciBacteriasMultirRN mciBacteriasMultirRN;	
	
	@EJB
	private MciTipoGrupoProcedRiscoON mciTipoGrupoProcedRiscoON;
	
	@EJB
	private TopografiaInfeccaoON topografiaInfeccaoON;
	
	@EJB
	private TopografiaPorProcedimentoON topografiaPorProcedimentoON;
	
	@EJB
	private CadastroPalavrasChavePatologiaON cadastroPalavrasChavePatologiaON;
	
	@EJB
	private MciAntimicrobianosRN mciAntimicrobianosRN;
	
	@EJB
	private NotificacoesFatorPredisponenteRN notificacaoFatorPredisponenteRN;
	
	@EJB
	private NotificacoesProcedimentoRiscoRN notificacaoProcedimentoRiscoRN;
	
	@EJB
	private MciCriteriosGmrRN mciCriterioGmrRN;

	@Inject
	private MciMicroorganismoPatologiaDAO mciMicroorganismoPatologiaDAO;
	
	@EJB
	private MciMicroorganismoPatologiaRN mciMicroorganismoPatologiaRN;
	
	@EJB
	private NotificacaoTopografiaON notificacaoTopografiaON;
	
	@EJB
	private NotificacaoMedidasPreventivasON notificacaoMedidasPreventivasON;
	
	@EJB
	private ListaPacientesCCIHRN listaPacientesCCIHRN; 
	@EJB
	private NotificacaoGmrPacienteRN notificacaoGmrPacienteRN;
	@EJB
	private MciNotasCCIHRN mciNotasCCIHRN;

	@EJB
	private RelatorioNotificGermeMultirresistenteRN relatorioNotificGermeMultirresistenteRN;
	@Inject
	private MciMvtoProcedimentoRiscosDAO mciMvtoProcedimentoRiscosDAO;
	
	
	@Inject
	private MciMvtoFatorPredisponentesDAO mciMvtoFatorPredisponentesDAO;
	
	@Inject
	private MciNotasCCIHDAO mciNotasCCIHDAO;
	@Inject
	private MciMvtoInfeccaoTopografiaDAO mciMvtoInfeccaoTopografiaDAO;
	
	@Inject
	private MciMvtoInfeccaoTopografiasDAO mciMvtoInfeccaoTopografiasDAO;
	
	@Inject
	private MciFatorPredisponentesDAO mciFatorPredisponentesDAO;
	
	@Inject
	private MciGrupoReportRotinaCciDAO mciGrupoReportRotinaCciDAO;
	@Inject
	private MciMvtoMedidaPreventivasDAO mciMvtoMedidaPreventivasDAO;
	
	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;
	
	@Inject
	private MciTipoGrupoProcedRiscoDAO mciTipoGrupoProcedRiscoDAO;
	
	@Inject
	private MciProcedimentoRiscoDAO mciProcedRiscoDAO;
	
	@Inject
	private MciPatologiaInfeccaoDAO mciPatologiaInfeccaoDAO; 
	
	
	@Inject
	private MciDuracaoMedidaPreventivasDAO mciDuracaoMedidaPreventivasDAO;

	@Inject
	private MciBacteriaMultirDAO mciBacteriasMultirDAO;

	@Inject
	private MciTopografiaInfeccaoDAO mciTopografiaInfeccaoDAO;

	@Inject
	private MciDuracaoMedidaPreventivaDAO mciDuracaoMedidaPreventivaDAO;
	
	@Inject
	private MciAntimicrobianosDAO mciAntimicrobianosDAO;
	
	@Inject
	private MciEtiologiaInfeccaoDAO etiologiaInfeccaoDAO;
	
	@Inject
	private AghAtendimentoDAO atendimentoDAO;
	
	@Inject
	private MciTopografiaProcedimentoDAO mciTopografiaProcedimentoDAO;
	@Inject
	private MciParamReportGrupoDAO mciParamReportGrupoDAO;
	@Inject
	private MciExportacaoDadoDAO mciExportacaoDadoDAO;
	@Inject
	private MciParamReportUsuarioDAO mciParamReportUsuarioDAO;
	@Inject
	private RelatorioBuscaAtivaPacientesON relatorioBuscaAtivaPacientesON;	@Inject
	private MciCriterioGmrDAO mciCriteriosGmrDAO;
	@Inject
	private VAghUnidFuncionalDAO vAghUnidFuncionalDAO;
	
	@Override
	public void alterarLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) {
		origemInfeccaoON.alterarLocalOrigemInfeccao(localOrigemInfeccao);
	}
	
	@Override
	public void alterarBacteriaAssociada(MciBacteriasAssociadasVO bacteriaAssociada) throws ApplicationBusinessException {
		mciBacteriasMultirRN.alterarBacteriaAssociada(bacteriaAssociada);
	}	

	@Override
	public void associarGrupoProced(Short porSeq, Short tgpSeq) throws ApplicationBusinessException {
		controleInfeccaoRN.associarGrupoProced(porSeq, tgpSeq);
	}
	
	@Override
	public void atualizaProcedimentoRisco(MciProcedimentoRisco entity, DominioSituacao situacao)throws ApplicationBusinessException{
		controleInfeccaoRN.atualizaProcedimentoRisco(entity,situacao);
	}
	
	// #36265
	@Override	
	public void atualizarDuracaoMedidaPreventiva(MciDuracaoMedidaPreventiva entity) throws ApplicationBusinessException{
		mciDuracaoMedidasPreventivasRN.atualizarDuracaoMedidaPreventiva(entity);
	}	
	
	// #37923
	@Override	
	public void atualizarBacteriaMultir(MciBacteriaMultir entity) throws ApplicationBusinessException{
		mciBacteriasMultirRN.atualizarBacteriaMultir(entity);
	}		
	
	@Override
	public void atualizarMciMicroorganismoPatologia(
			MciMicroorganismoPatologia entidade, RapServidores usuario)
			throws ApplicationBusinessException {
		mciMicroorganismoPatologiaRN.atualizarMciMicroorganismoPatologia(entidade, usuario);
		
	}
	
	@Override
	public void atualizarMciMicroorganismoPatologiaExame(
			ResultadoCodificadoExameVO entidade, RapServidores usuario)
			throws ApplicationBusinessException {
		mciMicroorganismoPatologiaRN.atualizarMciMicroorganismoPatologiaExame(entidade, usuario);
		
	}

	@BypassInactiveModule
	@Secure("#{s:hasPermission('manterProcedimentosRisco','executar')}")
	@Override
	public void atualizarMciTipoGrupoProcedRisco(
			MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado)
			throws ApplicationBusinessException {
		mciTipoGrupoProcedRiscoON.alterarMciTipoGrupoProcedRisco(entidade, usuarioLogado);
	}

	@Override
	public void atualizarOrigemInfeccao(OrigemInfeccoesVO origemInfeccao) {
		origemInfeccaoON.atualizarOrigemInfeccao(origemInfeccao);
	}
	
	@Override
	public void atualizarPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia) throws ApplicationBusinessException {
		this.cadastroPalavrasChavePatologiaON.atualizarPalavraChavePatologia(palavraChavePatologia);
	}

	@Override
	public List<MciMicroorganismoPatologia> buscarMicroorganismoPorSeqInfeccao(
			Integer seq) {
		return mciMicroorganismoPatologiaDAO.buscarMicroorganismoPorSeqInfeccao(seq);
	}

	@Override
	public void deletarMciMicroorganismoPatologia(
			MciMicroorganismoPatologia entidade, RapServidores usuario)
			throws ApplicationBusinessException {
		mciMicroorganismoPatologiaRN.deletarMicroorganismoPatologia(entidade, usuario);
	}

	@Override
	public void deletarMciMicroorganismoPatologiaExame(
			ResultadoCodificadoExameVO entidade, RapServidores usuario)
			throws ApplicationBusinessException {
		mciMicroorganismoPatologiaRN.deletarMicroorganismoPatologiaExame(entidade, usuario);
	}
	
	@BypassInactiveModule
	@Secure("#{s:hasPermission('manterProcedimentosRisco','executar')}")
	@Override
	public void deletarMciTipoGrupoProcedRisco(
			MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado)
			throws ApplicationBusinessException {
		mciTipoGrupoProcedRiscoON.excluirMciTipoGrupoProcedRisco(entidade, usuarioLogado);
	}
	
	@Override
	public String excluirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) throws ApplicationBusinessException {
		return origemInfeccaoON.excluirLocalOrigemInfeccao(localOrigemInfeccao);
	}

	@Override
	public void excluirPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia) throws ApplicationBusinessException {
		this.cadastroPalavrasChavePatologiaON.excluirPalavraChavePatologia(palavraChavePatologia);
	}

	@Override
	public void excluirProcedimentoRisco(Short tgpSeq, Short porSeq) throws ApplicationBusinessException {
		controleInfeccaoRN.excluirProcedimentoRisco(tgpSeq,porSeq);
	}

	@Override
	public void excluirTopografiaInfeccao(TopografiaInfeccaoVO vo) throws BaseException {
		topografiaInfeccaoON.excluirTopografiaInfeccao(vo);
	}

	@Override
	public void excluirTopografiaProcedimentoVO(TopografiaProcedimentoVO vo) throws BaseException {
		topografiaPorProcedimentoON.excluirTopografiaProcedimentoVO(vo);
		
	}

	// #36265
	@Override	
	public void inserirDuracaoMedidaPreventiva(MciDuracaoMedidaPreventiva entity) throws ApplicationBusinessException{
		mciDuracaoMedidasPreventivasRN.validaeInsereDescricaoDuracaoMedidas(entity);
	}
	
	// #37923
	@Override	
	public void inserirMciBacteriaMultir(MciBacteriaMultir entity) throws ApplicationBusinessException{
		mciBacteriasMultirRN.validarInsereBacteriaMultir(entity);
	}	
	
	@Override	
	public void inserirMciBacteriaMultir(MciBacteriasAssociadasVO entity) throws ApplicationBusinessException{		
		mciBacteriasMultirRN.validarInsereBacteriaMultir(entity);
	}		

	@Override
	public void inserirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) {
		origemInfeccaoON.inserirLocalOrigemInfeccao(localOrigemInfeccao);
	}

	@Override
	public void inserirMciMicroorganismoPatologia(
			MciPatologiaInfeccao mciPatologiaInfeccao,
			MciMicroorganismoPatologia entidade, RapServidores usuario) {
		mciMicroorganismoPatologiaRN.inserirMciMicroorganismoPatologia(mciPatologiaInfeccao, entidade, usuario);
		
	}

	@Override
	public void inserirMciMicroorganismoPatologiaExame(
			MciMicroorganismoPatologia patologia,
			ResultadoCodificadoExameVO entidade, RapServidores usuario)
			throws ApplicationBusinessException {
		mciMicroorganismoPatologiaRN.inserirMciMicroorganismoPatologiaExame(patologia, entidade, usuario);
	}

	@Override
	public void inserirMciMvtoFatorPredisponentes(
			MciMvtoFatorPredisponentes mciMvtoFatorPredisponentes, boolean flush) {
		mciMvtoFatorPredisponentesDAO.persistir(mciMvtoFatorPredisponentes);
		if (flush){
			mciMvtoFatorPredisponentesDAO.flush();
		}
	}

	@BypassInactiveModule
	@Secure("#{s:hasPermission('manterProcedimentosRisco','executar')}")
	@Override
	public void inserirMciTipoGrupoProcedRisco(
			MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado)
			throws ApplicationBusinessException {
		mciTipoGrupoProcedRiscoON.inserirMciTipoGrupoProcedRisco(entidade, usuarioLogado);
	}

	@Override
	public void inserirPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia, Integer codigoPatologia) {
		this.cadastroPalavrasChavePatologiaON.inserirPalavraChavePatologia(palavraChavePatologia, codigoPatologia);
	}

	@Override
	public List<LocaisOrigemInfeccaoVO> listarLocaisOrigemInfeccoes(String codigoOrigem) {
		return origemInfeccaoON.listarLocaisOrigemInfeccoes(codigoOrigem);
	}
	
	// #37923
	@Override
	public List<MciBacteriasAssociadasVO> listarBacteriasAssociadas(Integer seq) {
		return this.mciBacteriasMultirRN.listarBacteriasAssociadas(seq);
	}	
	
	@Override
	public List<BacteriaMultirresistenteVO> listarBacteriasMultir(Integer firstResult,Integer maxResults, String orderProperty, boolean asc,String codigo, String descricao, DominioSituacao situacao) {
		return mciBacteriasMultirDAO.listarBacteriasMultir(firstResult, maxResults, orderProperty, asc, codigo, descricao, situacao);
	}

	@Override
	public Long listarBacteriasMultirCount(String codigo, String descricao, DominioSituacao situacao) {
		return mciBacteriasMultirDAO.listarBacteriasMultirCount(codigo, descricao, situacao);
	}

	@Override
	public List<TopografiaInfeccaoVO> listarMciTopografiaInfeccaoPorDescricaoESituacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String descricao, DominioSituacao situacao) {

		return topografiaInfeccaoON
				.listarMciTopografiaInfeccaoPorDescricaoESituacao(firstResult,
						maxResult, orderProperty, asc, descricao, situacao);
	}

	@Override
	public Long listarMciTopografiaInfeccaoPorDescricaoESituacaoCount(
			String descricao, DominioSituacao situacao) {
		return topografiaInfeccaoON
				.listarMciTopografiaInfeccaoPorDescricaoESituacaoCount(
						descricao, situacao);
	}
	
	@Override
	public List<TopografiaProcedimentoVO> listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seq, String descricao, DominioSituacao situacao,
			Short toiSeq) {

		return topografiaPorProcedimentoON
				.listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(
						firstResult, maxResult, orderProperty, asc, seq,
						descricao, situacao, toiSeq);
	}
	
	@Override
	public Long listarMciTopografiaProcedimentoPorSeqDescSitSeqTopCount(
			Short seq, String descricao, DominioSituacao situacao, Short toiSeq) {
		return topografiaPorProcedimentoON
				.listarMciTopografiaProcedimentoPorSeqDescSitSeqTopCount(seq,
						descricao, situacao, toiSeq);
	}
	
	@Override
	public List<MciMvtoFatorPredisponentes> listarMvtoFatorPredisponentes(
			Integer pacCodigo, AghAtendimentos atendimento) {
		return mciMvtoFatorPredisponentesDAO
				.listarMvtoFatorPredisponentes(pacCodigo, atendimento);
	}
	
	@Override
	public List<MciMvtoFatorPredisponentes> listarMvtosFatorPredisponentesPorCodigoPaciente(
			Integer pacCodigo) {
		return mciMvtoFatorPredisponentesDAO
				.listarMvtosFatorPredisponentesPorCodigoPaciente(pacCodigo);
	}
	
	@Override
	public List<MciMvtoInfeccaoTopografias> listarMvtosInfeccoesTopografias(
			Integer pacCodigo, AghAtendimentos atendimento) {
		return mciMvtoInfeccaoTopografiasDAO
				.listarMvtosInfeccoesTopografias(pacCodigo, atendimento);
	}
	
	@Override
	public List<MciMvtoInfeccaoTopografias> listarMvtosInfeccoesTopologiasPorCodigoPaciente(
			Integer pacCodigo) {
		return mciMvtoInfeccaoTopografiasDAO
				.listarMvtosInfeccoesTopologiasPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<MciMvtoMedidaPreventivas> listarMvtosMedidasPreventivas(
			Integer pacCodigo, AghAtendimentos atendimento) {
		return mciMvtoMedidaPreventivasDAO
				.listarMvtosMedidasPreventivas(pacCodigo, atendimento);
	}
	
	@Override
	public List<MciMvtoMedidaPreventivas> listarMvtosMedidasPreventivasPorCodigoPaciente(
			Integer pacCodigo) {
		return mciMvtoMedidaPreventivasDAO
				.listarMvtosMedidasPreventivasPorCodigoPaciente(pacCodigo);
	}
	
	@Override
	public List<MciMvtoProcedimentoRiscos> listarMvtosProcedimentosRiscos(
			Integer pacCodigo, AghAtendimentos atendimento) {
		return mciMvtoProcedimentoRiscosDAO
				.listarMvtosProcedimentosRiscos(pacCodigo, atendimento);
	}
	
	@Override
	public List<MciMvtoProcedimentoRiscos> listarMvtosProcedimentosRiscosPorCodigoPaciente(
			Integer pacCodigo) {
		return mciMvtoProcedimentoRiscosDAO
				.listarMvtosProcedimentosRiscosPorCodigoPaciente(pacCodigo);
	}
	
	@Override
	public List<OrigemInfeccoesVO> listarOrigemInfeccoes(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String codigoOrigem, String descricao, DominioSituacao situacao) {
		return origemInfeccaoON.listarOrigemInfeccoes(firstResult, maxResults, orderProperty, asc, codigoOrigem, descricao, situacao);
	}
	
	@Override
	public Long listarOrigemInfeccoesCount(String codigoOrigem, String descricao, DominioSituacao situacao) {
		return origemInfeccaoON.listarOrigemInfeccoesCount(codigoOrigem, descricao, situacao);
	}
	
	@Override
	public List<MciPalavraChavePatologia> listarPalavraChavePatologia(Integer codigoPatologia) {
		return this.cadastroPalavrasChavePatologiaON.listarPalavraChavePatologia(codigoPatologia);
	}
	
	// #36265
	@Override	
	public MciDuracaoMedidaPreventiva obterDuracaoMedidaPreventiva(Short seq){
		return mciDuracaoMedidaPreventivasDAO.obterPorChavePrimaria(seq);
	}
	
	// #37923
	@Override	
	public MciBacteriaMultir obterMciBacteriaMultir(Integer seq){
		return mciBacteriasMultirDAO.obterPorChavePrimaria(seq);
	}	
	
	@Override
	public List<MciDuracaoMedidaPreventiva> obterDuracaoMedidaPreventivaPorDescricaoSituacao(String descricao, DominioSituacao indSituacao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return mciDuracaoMedidaPreventivasDAO.obterDuracaoMedidaPreventivaPorDescricaoSituacao(descricao, indSituacao, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public List<MciBacteriaMultir> obterBacteriaMultirPorSeqDescricaoSituacao(Integer codigo, String descricao, DominioSituacao indSituacao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return mciBacteriasMultirDAO.obterBacteriaMultirPorDescricaoSituacao(codigo, descricao, indSituacao, firstResult, maxResults, orderProperty, asc);
	}	
	
	@Override
	public Long obterDuracaoMedidaPreventivaPorDescricaoSituacaoCount(String descricao, DominioSituacao indSituacao) {
		return mciDuracaoMedidaPreventivasDAO.obterDuracaoMedidaPreventivaPorDescricaoSituacaoCount(descricao, indSituacao);
	}
	
	@Override
	public Long obterBacteriasMultirPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao indSituacao) {
		return mciBacteriasMultirDAO.obterBacteriaMultirPorSeqDescricaoSituacaoCount(seq, descricao, indSituacao);
	}	
	
	@Override
	public MciFatorPredisponentes obterFatorPredisponentesPorPeso(BigDecimal valorPeso) {
		return mciFatorPredisponentesDAO.obterFatorPredisponentesPorPeso(valorPeso);
	}
	
	@Override
	public MciFatorPredisponentes obterFatorPredisponentesPorSeq(Short seq) {
		return mciFatorPredisponentesDAO.obterFatorPredisponentesPorSeq(seq);
	}
	
	@Override
	public MciProcedimentoRisco obterProcedimentoRiscoPorSeq(Short seq) {
		return mciProcedRiscoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public MciMvtoFatorPredisponentes obterMciMvtoFatorPredisponentesPorPaciente(Integer codigoPaciente) {
		return mciMvtoFatorPredisponentesDAO.obterMciMvtoFatorPredisponentesPorPaciente(codigoPaciente);
	}
	
	@BypassInactiveModule
	@Secure("#{s:hasPermission('consultarProcedimentosRisco','pesquisar')}")
	@Override
	public MciTipoGrupoProcedRisco obterMciTipoGrupoProcedRiscoPorSeq(Short seq) {
		return mciTipoGrupoProcedRiscoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public MciPatologiaInfeccao obterPorChavePrimaria(int i) {
		return mciPatologiaInfeccaoDAO.obterPorChavePrimaria(i);
	}
	
	@Override
	public MciProcedimentoRisco obterProcedimentoRisco(Short seq){
		return mciProcedRiscoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public MciTipoGrupoProcedRisco obterTipoGrupo(Short tgpSeq) {
		return mciTipoGrupoProcedRiscoDAO.obterPorChavePrimaria(tgpSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean obterVerificacaoMvtoInfeccaoDeAtendimento(Integer atdSeq) throws ApplicationBusinessException {
		return mciMvtoInfeccaoTopografiaDAO.obterVerificacaoMvtoInfeccaoDeAtendimento(atdSeq);
	}

	@Override
	public void persistirMciMvtoFatorPredisponentes(
			MciMvtoFatorPredisponentes mciMvtoFatorPredisponentes) {
		mciMvtoFatorPredisponentesDAO.persistir(mciMvtoFatorPredisponentes);
	}
	
	@Override
	public void persistirMciMvtoInfeccaoTopografias(
			MciMvtoInfeccaoTopografias mciMvtoInfeccaoTopografias) {
		mciMvtoInfeccaoTopografiasDAO.persistir(
				mciMvtoInfeccaoTopografias);
	}

	@Override
	public void persistirMciMvtoMedidaPreventivas(
			MciMvtoMedidaPreventivas mciMvtoMedidaPreventivas) {
		mciMvtoMedidaPreventivasDAO.persistir(
				mciMvtoMedidaPreventivas);
	}

	@Override
	public void persistirMciMvtoProcedimentoRiscos(
			MciMvtoProcedimentoRiscos mciMvtoProcedimentoRiscos) {
		mciMvtoProcedimentoRiscosDAO.persistir(
				mciMvtoProcedimentoRiscos);
	}
	
	@Override
	public void persistirMciAntimicrobiano(MciAntimicrobianos mciAntimicrobiano)	throws ApplicationBusinessException {
		this.mciAntimicrobianosRN.persistirMciAntimicrobiano(mciAntimicrobiano);
	}
	
	@Override
	public void excluirMciAntimicrobiano(Integer seq) throws ApplicationBusinessException, BaseListException {
		this.mciAntimicrobianosRN.excluirMciAntimicrobiano(seq);
	}
	
	@Override	
	public MciAntimicrobianos obterAntimicrobianoPorChavePrimaria(Integer seq) {
		return this.mciAntimicrobianosDAO.obterPorChavePrimaria(seq);
	}
	
	@Override	
	public MciBacteriaMultir obterBacteriaPorChavePrimaria(Integer seq) {
		return this.mciBacteriasMultirDAO.obterPorChavePrimaria(seq);
	}	
	
	@Override
	public List<MciAntimicrobianos> pesquisarAntimicrobianosPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao indSituacao, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return this.mciAntimicrobianosDAO.pesquisarAntimicrobianosPorSeqDescricaoSituacao(seq, descricao, indSituacao, firstResult, 
				maxResults, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarAntimicrobianosPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao indSituacao) {
		return this.mciAntimicrobianosDAO.pesquisarAntimicrobianosPorSeqDescricaoSituacaoCount(seq, descricao, indSituacao);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterPatologiaInfeccao','executar')}")
	public void persistirMciPatologiaInfeccao(MciPatologiaInfeccao patologiaInfeccao) throws ApplicationBusinessException {
		this.mciPatologiaInfeccaoRN.persistirMciPatologiaInfeccao(patologiaInfeccao);
	}
	
	@Override
	public void persistirTopografiaInfeccao(TopografiaInfeccaoVO vo) throws BaseException {
		topografiaInfeccaoON.persistirTopografiaInfeccao(vo);
	}
	
	@Override
	public void persistirTopografiaProcedimento(TopografiaProcedimentoVO vo) throws BaseException {
		topografiaPorProcedimentoON.persistirTopografiaProcedimento(vo);
	}

	@Override
	public List<MciDuracaoMedidaPreventiva> pesquisarDuracaoMedidaPreventivaPatologiaInfeccao(Object parametro) {
		return this.mciDuracaoMedidaPreventivaDAO.pesquisarDuracaoMedidaPreventivaPatologiaInfeccao(parametro);
	}
	
	@Override
	public Long pesquisarDuracaoMedidaPreventivaPatologiaInfeccaoCount(Object parametro) {
		return this.mciDuracaoMedidaPreventivaDAO.pesquisarDuracaoMedidaPreventivaPatologiaInfeccaoCount(parametro);
	}

	@Override
	public List<TipoGrupoRiscoVO> pesquisarMciGrupoProcedRiscoPorSeqeSeqTipoGrupo(Short porSeq, Short tgpSeq) {
		return mciProcedRiscoDAO.pesquisarMciGrupoProcedRiscoPorSeqeSeqTipoGrupo(porSeq, tgpSeq);
	}
	
	@Override
	public List<MciProcedimentoRisco> pesquisarMciProcedRiscoPorSeqDescricaoSituacao(Short seq, String descricao, DominioSituacao indSituacao,Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return mciProcedRiscoDAO.pesquisarMciProcedRiscoPorSeqDescricaoSituacao(seq, descricao, indSituacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarMciProcedRiscoPorSeqDescricaoSituacaoCount(Short seq, String descricao, DominioSituacao indSituacao) {
		return mciProcedRiscoDAO.pesquisarMciProcedRiscoPorSeqDescricaoSituacaoCount(seq, descricao, indSituacao);
	}
	
	@BypassInactiveModule
	@Secure("#{s:hasPermission('consultarProcedimentosRisco','pesquisar')}")
	@Override
	public List<MciTipoGrupoProcedRisco> pesquisarMciTipoGrupoProcedRisco(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short seq, String descricao, DominioSituacao indSituacao) {
		return mciTipoGrupoProcedRiscoDAO.pesquisarMciTipoGrupoProcedRisco(firstResult, maxResults,  orderProperty,  asc,seq, descricao, indSituacao) ;
	}
	
	@BypassInactiveModule
	@Secure("#{s:hasPermission('consultarProcedimentosRisco','pesquisar')}")
	@Override
	public Long pesquisarMciTipoGrupoProcedRiscoCount(Short codigo,	String descricao, DominioSituacao situacao) {
		return mciTipoGrupoProcedRiscoDAO.pesquisarMciTipoGrupoProcedRiscoCount(codigo, descricao, situacao) ;
	}
	
	@Override
	public List<MciPatologiaInfeccao> pesquisarPatologiaInfeccao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MciPatologiaInfeccao patologiaInfeccao) {
		return mciPatologiaInfeccaoDAO.pesquisarPatologiaInfeccao(firstResult, maxResult, orderProperty, asc, patologiaInfeccao);
	}
	
	@Override
	public Long pesquisarPatologiaInfeccaoCount(MciPatologiaInfeccao patologiaInfeccao) {
		return mciPatologiaInfeccaoDAO.pesquisarPatologiaInfeccaoCount(patologiaInfeccao);
	}
	
	@BypassInactiveModule
	@Override
	public List<Integer> pesquisarSequencialFatorPredisponentePorCodigoPaciente(
			Integer codigoPaciente) {
		return mciMvtoFatorPredisponentesDAO
				.pesquisarSequencialFatorPredisponentePorCodigoPaciente(
						codigoPaciente);
	}



	@Override
	public List<MciTipoGrupoProcedRisco> pesquisarSuggestionGrupos(String strPesquisa, Short seqProcedimento) {
		return mciProcedRiscoDAO.pesquisarMciTipoGrupo(strPesquisa, seqProcedimento);
	}

	@Override
	public List<MciTopografiaInfeccao> pesquisarTopografiaInfeccaoPatologiaInfeccao(Object parametro) {
		return this.mciTopografiaInfeccaoDAO.pesquisarTopografiaInfeccaoPatologiaInfeccao(parametro);

	}

	@Override
	public Long pesquisarTopografiaInfeccaoPatologiaInfeccaoCount(Object parametro) {
		return this.mciTopografiaInfeccaoDAO.pesquisarTopografiaInfeccaoPatologiaInfeccaoCount(parametro);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesAtivas(String strPesquisa, boolean semEtiologia, String tipo) {
		return origemInfeccaoON.pesquisarUnidadesAtivas(strPesquisa, semEtiologia, tipo);
	}

	// #36265
	@Override		
	public void removerDuracaoMedidaPreventiva(Short seq, Date criadoEm) throws ApplicationBusinessException{
		mciDuracaoMedidasPreventivasRN.validarRemoverDuracaoMedidas(seq,criadoEm);	
	}
	
	// #37923
	@Override		
	public void removerBacteriaMultir(Integer seq) throws ApplicationBusinessException, BaseListException{
		mciBacteriasMultirRN.excluirMciBacteriaMultir(seq);
	}
	
	@Override
	public void removerMciMvtoFatorPredisponentes(
			MciMvtoFatorPredisponentes mciMvtoFatorPredisponentes, boolean flush) {
		mciMvtoFatorPredisponentesDAO.remover(mciMvtoFatorPredisponentes);
		if (flush){
			mciMvtoFatorPredisponentesDAO.flush();
		}
	}
	
	@Override
	public List<MciFatorPredisponentes> obterSuggestionFatorPredisponentes(String strPesquisa){
		return mciFatorPredisponentesDAO.obterFatorPredisponentesPorCodigoDescricao(strPesquisa);
	}
	
	@Override
	public Long obterSuggestionFatorPredisponentesCount(String strPesquisa) {		
		return mciFatorPredisponentesDAO.obterFatorPredisponentesPorCodigoDescricaoCount(strPesquisa);
	}
	
	@Override
	public List<MciProcedimentoRisco> pesquisarProcedimentoRisco(String strPesquisa){
		return mciProcedRiscoDAO.obterProcedimentoRiscoPorCodigoDescricao(strPesquisa);
	}
	
	@Override
	public Long pesquisarProcedimentoRiscoCount(String strPesquisa){
		return mciProcedRiscoDAO.obterProcedimentoRiscoPorCodigoDescricaoCount(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('manterPatologiaInfeccao','executar')}")
	public void removerMciPatologiaInfeccao(final Integer seq) throws BaseException {
		this.mciPatologiaInfeccaoRN.removerMciPatologiaInfeccao(seq);
	}
	
	@Override
	public List<TopografiaInfeccaoVO> suggestionBoxTopografiaInfeccaoPorSeqOuDescricao(String strPesquisa) {
		return topografiaPorProcedimentoON.suggestionBoxTopografiaInfeccaoPorSeqOuDescricao(strPesquisa);
	}
	
	@Override
	public void validaeInsereProcedimentoRisco(MciProcedimentoRisco entity) throws ApplicationBusinessException {
		controleInfeccaoRN.validaeInsereProcedimentoRisco(entity);
	}
	
	@Override
	public void validarRemoverProcedimentoRisco(Short seq, Date criadoEm) throws ApplicationBusinessException, BaseListException{
		controleInfeccaoRN.validarRemoverProcedimentoRisco(seq,criadoEm);
	}
	
	@Override
	public List<MciAntimicrobianos> pesquisarAntiMicrobianosAtivosPorSeqDescricao(Object param) {
		return this.mciAntimicrobianosDAO.pesquisarAntiMicrobianosAtivosPorSeqDescricao(param);
	}
	
	@Override
	public Long pesquisarAntiMicrobianosAtivosPorSeqDescricaoCount(Object param) {
		return this.mciAntimicrobianosDAO.pesquisarAntiMicrobianosAtivosPorSeqDescricaoCount(param);
	}
	
	@Override
	@Secure("#{s:hasPermission('controleInfeccao','verificarLeito')}")
	@BypassInactiveModule
	public boolean verificaLeitoControladoCCIH(String leitoID) {
		return controleInfeccaoRN.verificaLeitoControladoCCIH(leitoID);
	}
	
	@Override
	@BypassInactiveModule
	public boolean verificaLeitoExclusivoControleInfeccao(AinLeitos leito) {
		return controleInfeccaoON.verificaLeitoExclusivoControleInfeccao(leito);
	}
	
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('controleInfeccao','verificarLeito')}")
	public boolean verificarNecessidadeMedidaPreventivaInfeccao(Integer codigoPaciente) {
		return controleInfeccaoON.verificarNecessidadeMedidaPreventivaInfeccao(codigoPaciente);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarNotificacaoGmrPorCodigo(Integer pacCodigo) {
		return mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(pacCodigo);
	}
	
	@Override
	public List<NotificacaoFatorPredisponenteVO> listarNotificacoesPorPaciente(Integer pacCodigo){
		return mciMvtoFatorPredisponentesDAO.listarNotificacoesPorPaciente(pacCodigo);
	}
	
	@Override
	public List<NotificacaoProcedimentoRiscoVO> listarNotificacoesProcedientoRiscoPorPaciente(Integer pacCodigo){
		return mciMvtoProcedimentoRiscosDAO.listarNotificacoesPorPaciente(pacCodigo);
	}
	
	public List<MciMvtoInfeccaoTopografias> pesquisarMovimentosInfeccoesInstituicaoHospitalar(final Integer ihoSeq){
		return mciMvtoInfeccaoTopografiaDAO.pesquisarMovimentosInfeccoesInstituicaoHospitalar(ihoSeq);
	}
	
	public List<MciMvtoMedidaPreventivas> pesquisarMovimentosMedidasPreventivasInstituicaoHospitalar(final Integer ihoSeq){
		return mciMvtoMedidaPreventivasDAO.pesquisarMovimentosMedidasPreventivasInstituicaoHospitalar(ihoSeq);
	}
	
	@Override
	public void inserirNotificacaoFatorPredisponente(MciMvtoFatorPredisponentes entity) throws BaseException, ApplicationBusinessException{
		notificacaoFatorPredisponenteRN.inserirNotificacaoFatorPredisponente(entity);
	}
	
	@Override
	public void inserirNotificacaoProcedimentoRisco(MciMvtoProcedimentoRiscos entity) throws BaseException, ApplicationBusinessException{
		notificacaoProcedimentoRiscoRN.inserirNotificacaoProcedimentoRisco(entity);
	}
	
	@Override
	public MciMvtoFatorPredisponentes obterNotificacaoFatorPredisponente(Integer seq){
		return mciMvtoFatorPredisponentesDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public void atualizarNotificacaoFatorPredisponente(MciMvtoFatorPredisponentes entity, Integer seq) throws ApplicationBusinessException, BaseException{
		notificacaoFatorPredisponenteRN.atualizarNotificacaoFatorPredisponente(entity,seq);
	}
	
	@Override
	public void atualizarProcedimentoRisco(MciMvtoProcedimentoRiscos entity, Integer seq) throws ApplicationBusinessException, BaseException{
		notificacaoProcedimentoRiscoRN.atualizarNotificacaoProcedimentoRisco(entity,seq);
	}

	@Override
	public List<CriteriosBacteriaAntimicrobianoVO> pesquisarCriterioGrmPorBmrSeq(Integer bmrSeq) {
		return mciCriteriosGmrDAO.pesquisarCriterioGrmPorBmrSeq(bmrSeq);
	}

	@Override
	public void persistirCriterioGmr(MciCriterioGmr criterio, Integer ambSeq, Integer bmrSeq) throws ApplicationBusinessException {
		mciCriterioGmrRN.persistirMciCriteriosGMR(criterio, ambSeq, bmrSeq);		
	}
	
	@Override
	public void excluirCriterioGmr(Integer bmrSeq, Integer ambSeq)	throws ApplicationBusinessException, BaseListException {
		mciCriterioGmrRN.excluir(bmrSeq, ambSeq);
	}

	@Override
	public MciCriterioGmr obterCriterioGmr(MciCriterioGmrId criterio) {
		return mciCriteriosGmrDAO.obterCriterioGmrPeloId(criterio);
	}


	@Override
	public List<NotificacaoMedidasPreventivasVO> pesquisarNotificacoesMedidaPreventiva(Integer codigoPaciente) throws ApplicationBusinessException {
		return notificacaoMedidasPreventivasON.buscarNotificacoesMedidasPreventivasPorCodigoPaciente(codigoPaciente);
	}

	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesDoencasCondicoes(final Integer codigoPaciente) {
		return mciMvtoMedidaPreventivasDAO.listarNotificacoesDoencasCondicoes(codigoPaciente);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesTopografias(final Integer codigoPaciente) {
		return mciMvtoInfeccaoTopografiasDAO.listarNotificacoesTopografias(codigoPaciente);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesProcedimentosRisco(final Integer codigoPaciente) {
		return mciMvtoProcedimentoRiscosDAO.listarNotificacoesProcedimentosRisco(codigoPaciente);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesFatoresPredisponentes(final Integer codigoPaciente) {
		return mciMvtoFatorPredisponentesDAO.listarNotificacoesFatoresPredisponentes(codigoPaciente);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotasCCIH(final Integer codigoPaciente) {
		return mciNotasCCIHDAO.listarNotasCCIH(codigoPaciente);
	}

	@Override
	public List<NotificacoesGeraisVO> listarNotasCCIHNaoEncerradas(final Integer codigoPaciente) {
		return mciNotasCCIHDAO.listarNotasCCIHNaoEncerradas(codigoPaciente);
	}
	
	@Override
	public List<DoencaInfeccaoVO> listarDoencaInfeccaoVO(String param) {		
		return notificacaoMedidasPreventivasON.listarInfeccoes(param);
	}	
	
	@Override
	public Long listarDoencaInfeccaoVOCount(String param) {		
		return notificacaoMedidasPreventivasON.listarInfeccoesCount(param);
	}
	
	@Override
	public void validarCadastroEdicaoNotificacao(NotificacaoMedidasPreventivasVO vo) throws BaseListException {
		notificacaoMedidasPreventivasON.validarCadastroEdicaoNotificacao(vo);
	}

	
	@Override
	public List<TopografiaProcedimentoVO> listarTopografiasAtivas(String param) {	
		return mciTopografiaProcedimentoDAO.listarTopografiasAtivas(param);
	}
	
	@Override
	public Long listarTopografiasAtivasCount(String param) {	
		return mciTopografiaProcedimentoDAO.listarTopografiasAtivasCount(param);
	}
	
	@Override
	public void persistirNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO notificacao) throws BaseException, BaseListException {
		notificacaoMedidasPreventivasON.inserirNotificacaoMedidaPreventiva(notificacao);
	}
	
	@Override
	public void alterarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO notificacao) throws BaseException, BaseListException {
		notificacaoMedidasPreventivasON.atualizarNotificacaoMedidaPreventiva(notificacao);
	}


	
	@Override
	public void deletarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO notificacao) throws BaseException {
		notificacaoMedidasPreventivasON.deletarNotificacaoMedidaPreventiva(notificacao);
	}
	
	
	@Override
	public void removerNotificacaoFatorPredisponente(Integer seq) throws ApplicationBusinessException, BaseException {
		notificacaoFatorPredisponenteRN.removerNotificacaoFatorPredisponente(seq);
	}
	
	@Override
	public void removerNotificacaoProcedimentoRisco(Integer seq) throws ApplicationBusinessException, BaseException {
		notificacaoProcedimentoRiscoRN.removerNotificacaoProcedimentoRisco(seq);
	}
	
	@Override
	public List<OrigemInfeccoesVO> suggestionBoxTopografiaOrigemInfeccoes(String strPesquisa) {
		return etiologiaInfeccaoDAO.suggestionBoxTopografiaOrigemInfeccoes(strPesquisa);
	}

	@Override
	public List<ProcedRealizadoVO> obterProcedimentosPorPaciente(Integer pacCodigo, String strPesquisa) throws ApplicationBusinessException {
		return notificacaoTopografiaON.obterProcedimentosPorPaciente(pacCodigo, strPesquisa);
	}
	
	@Override
	public ProcedRealizadoVO obterProcedimentoVOPorId(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		return this.notificacaoTopografiaON.obterProcedimentoVOPorId(dcgCrgSeq, dcgSeqp, seqp);
	}

	@Override
	public List<NotificacaoTopografiasVO> obterNotificacoesTopografia(Integer codigo) throws BaseException {
		return notificacaoTopografiaON.obterNotificacoesTopografia(codigo);
	}

	@Override
	public List<TopografiaProcedimentoVO> suggestionBoxTopografiaProcedimentoPorSeqOuDescricao(String strPesquisa) {
		return mciTopografiaProcedimentoDAO.suggestionBoxTopografiaProcedimentoPorSeqOuDescricaoSituacao(strPesquisa);
	}

	@Override
	public TopografiaProcedimentoVO obterTopografiaProcedimento(Short seqTopografiaProcedimento) throws BaseException {
		return notificacaoTopografiaON.obterTopografiaProcedimento(seqTopografiaProcedimento);
	}

	@Override
	public void persistirNotificacaoTopografia(NotificacaoTopografiasVO vo) throws BaseException {
		notificacaoTopografiaON.persistirNotificacaoTopografia(vo);
	}

	@Override
	public void excluirNotificacaoTopografia(NotificacaoTopografiasVO notificacaoTopografiaVO) throws BaseException {
		notificacaoTopografiaON.excluirNotificacaoTopografia(notificacaoTopografiaVO);
	}
	
	@Override
	public NotificacaoFatorPredisponenteVO listarNotificacoesPorSeq(Integer seq){
		return mciMvtoFatorPredisponentesDAO.listarNotificacoesPorSeq(seq);
	}
	
	@Override
	public NotificacaoProcedimentoRiscoVO listarNotificacoesProcedimentoRiscoPorSeq(Integer seq){
		return mciMvtoProcedimentoRiscosDAO.listarNotificacoesPorSeq(seq);
	}

	@Override
	public OrigemInfeccoesVO obterTopografiaOrigemInfeccoes(String codigoEtiologiaInfeccao) throws BaseException {
		return notificacaoTopografiaON.obterTopografiaOrigemInfeccoes(codigoEtiologiaInfeccao);
	}

	@Override
	public AghAtendimentos obterAghAtendimentoObterPorChavePrimaria(Integer seq) {
		return atendimentoDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public List<OrigemInfeccoesVO> listarOrigemInfeccoes(String strPesquisa) {
		return notificacaoMedidasPreventivasON.listarOrigemInfeccoes(strPesquisa);
	}

	@Override
	public Long listarOrigemInfeccoesCount(String strPesquisa) {
		return notificacaoMedidasPreventivasON.listarOrigemInfeccoesCount(strPesquisa);
	}

	@Override
	public List<DoencaInfeccaoVO> buscarDoencaInfeccaoPaiChaveAtivos(String param) {
		return mciPatologiaInfeccaoDAO.buscarDoencaInfeccaoPaiChaveAtivos(param);
	}

	@Override
	public Long buscarDoencaInfeccaoPaiChaveAtivosCount(Object param) {
		return mciPatologiaInfeccaoDAO.buscarDoencaInfeccaoPaiChaveAtivosCount(param);
	}

	@Override
	public List<TopografiaProcedimentoVO> listarTopografiaProcedimentoAtivas(String param) {
		return mciTopografiaProcedimentoDAO.listarTopografiaProcedimentoAtivas(param);
	}

	@Override
	public Long listarTopografiaProcedimentoAtivasCount(Object param) {
		return mciTopografiaProcedimentoDAO.listarTopografiaProcedimentoAtivasCount(param);
	}

	@Override
	public List<ListaPacientesCCIHVO> pesquisarPacientesCCIH(
			FiltroListaPacienteCCIHVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		return this.listaPacientesCCIHRN.pesquisarPacientesCCIH(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarPacientesCCIHCount(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException {
		return this.listaPacientesCCIHRN.pesquisarPacientesCCIHCount(filtro);
	}
	
	public void validarPeriodoPesquisaAtendimento(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException {
		this.listaPacientesCCIHRN.validarPeriodoPesquisaAtendimento(filtro);
	}

	@Override
	public void validarNotificacaoSelecionada(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException {
		this.listaPacientesCCIHRN.validarNotificacaoSelecionada(filtro);
	}
	
	@Override
	public List<FatorPredisponenteVO> pesquisarFatorPredisponente(Short codigo, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return mciFatorPredisponentesDAO.pesquisarFatorPredisponente(codigo, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long pesquisarFatorPredisponenteCount(Short codigo, String descricao, DominioSituacao situacao) {
		return mciFatorPredisponentesDAO.pesquisarFatorPredisponenteCount(codigo, descricao, situacao);
	}
	
	@Secure("#{s:hasPermission('manterFatoresPredisponentes','manter')}")
	@Override
	public void removerFatorPredisponente(Short codigo) throws ApplicationBusinessException {
		mciFatorPredisponentesRN.excluirFatorPredisponente(codigo);
	}
	@Secure("#{s:hasPermission('manterFatoresPredisponentes','manter')}")
	@Override
	public void gravarFatorPredisponente(MciFatorPredisponentes fatorPredisponente) throws ApplicationBusinessException {
		mciFatorPredisponentesRN.gravarFatorPredisponente(fatorPredisponente);
	}
	@Override
	public List<GrupoReportRotinaCciVO> pesquisarGrupoReportRotinaCci(Short codigo, String descricao, DominioSituacao situacao, DominioPeriodicidade periodicidade) {
		return mciGrupoReportRotinaCciDAO.pesquisarGrupoReportRotinaCci(codigo, descricao, situacao, periodicidade);
	}
	
	@Override
	public void removerGrupoReportRotinaCci(Short codigo)  throws ApplicationBusinessException {
		mciGrupoReportRotinaCciRN.excluirGrupoReportRotinaCci(codigo);
	}
	
	@Override
	public void gravarGrupoReportRotinaCci(MciGrupoReportRotinaCci mciGrupoReportRotinaCci) throws ApplicationBusinessException {
		mciGrupoReportRotinaCciRN.gravarGrupoReportRotinaCci(mciGrupoReportRotinaCci);
	}
	
	@Override
	public MciGrupoReportRotinaCci obterGrupoReportRotinaCciPorSeq(Short seq) {
		return mciGrupoReportRotinaCciDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<ParamReportGrupoVO> pesquisarParamReportGrupoPorSeqGrupo(Short seqGrupo) {
		return mciParamReportGrupoDAO.pesquisarParamReportGrupoPorSeqGrupo(seqGrupo);
	}
	
	@Override
	public MciParamReportGrupo obterParamReportGrupoPorId(MciParamReportGrupoId id) {
		return mciParamReportGrupoDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public MciParamReportUsuario obterParamReportUsuarioPorId(Integer id) {
		return mciParamReportUsuarioDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public MciExportacaoDado obterExportacaoDadoPorId(Short id) {
		return mciExportacaoDadoDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public void removerMciParamReportGrupo(Integer pruSeq, Short grrCodigo)  throws ApplicationBusinessException {
		mciParamReportGrupoRN.excluirParamReportGrupo(pruSeq, grrCodigo);
	}
	
	@Override
	public void gravarParamReportGrupo(MciParamReportGrupo mciParamReportGrupo)  throws ApplicationBusinessException {
		mciParamReportGrupoRN.gravarParamReportGrupo(mciParamReportGrupo);
	}
	
	@Override
	public List<ParamReportUsuarioVO> pesquisarParamsReportUsuario(final String pesquisa, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return mciParamReportUsuarioDAO.pesquisarParamsReportUsuario(pesquisa, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long pesquisarParamsReportUsuarioCount(final String strPesquisa) {
		return mciParamReportUsuarioDAO.pesquisarParamsReportUsuarioCount(strPesquisa);
	}
	
	@Override
	public List<ExportacaoDadoVO> pesquisarExpDados(final String pesquisa, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return mciExportacaoDadoDAO.pesquisarExpDados(pesquisa, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long pesquisarExpDadosCount(final String strPesquisa) {
		return mciExportacaoDadoDAO.pesquisarExpDadosCount(strPesquisa);
	}
	@Override
	public List<BacteriaPacienteVO> pesquisarGermesDisponiveisListaGMRPaciente(String parametro) {
		return this.mciBacteriasMultirDAO.pesquisarGermesDisponiveisListaGMRPaciente(parametro);
	}

	@Override
	public Long pesquisarGermesDisponiveisListaGMRPacienteCount(String parametro) {
		return this.mciBacteriasMultirDAO.pesquisarGermesDisponiveisListaGMRPacienteCount(parametro);
	}
	
	
	@Override
	public List<MciCriterioGmr> pesquisarMciCriterioGrmAtivoPorBmrSeq(Integer brmSeq){
		return this.mciCriteriosGmrDAO.pesquisarMciCriterioGrmAtivoPorBmrSeq(brmSeq);
	}
    
	@Override
	public List<RelatorioBuscaAtivaPacientesCCIHVO> gerarRelatorioBuscaAtivaPacientes(FiltroListaPacienteCCIHVO filtro) {
		return relatorioBuscaAtivaPacientesON.gerarRelatorioBuscaAtivaPacientes(filtro);
	
	}


	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesDoencasCondicoesBuscaAtiva(Integer codigoPaciente, Integer atdSeq) {
		return mciMvtoMedidaPreventivasDAO.listarNotificacoesDoencasCondicoesBuscaAtiva(codigoPaciente, atdSeq);
	}

	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesTopografiasBuscaAtiva(Integer codigoPaciente, Integer atdSeq) {
		return mciMvtoInfeccaoTopografiasDAO.listarNotificacoesTopografiasBuscaAtiva(codigoPaciente, atdSeq);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesFatoresPredisponentesBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq) {
		return mciMvtoFatorPredisponentesDAO.listarNotificacoesFatoresPredisponentesBuscaAtiva(codigoPaciente, atdSeq);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesProcedimentosRiscoBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq) {
		return mciMvtoProcedimentoRiscosDAO.listarNotificacoesProcedimentosRiscoBuscaAtiva(codigoPaciente, atdSeq);
	}
    
	
	@Override
	public List<GMRPacienteVO> pesquisarGermesMultirresistentesPaciente(final Integer prontuario) {
		return mciNotificacaoGmrDAO.pesquisarGermesMultirresistentesPaciente(prontuario);
	}
	
	@Override
	public String buscarLocalizacaoPacienteCCIH(Integer pacCodigo) throws ApplicationBusinessException {
		return this.notificacaoGmrPacienteRN.buscarLocalizacaoPacienteCCIH(pacCodigo);
	}
	
	@Override
	public Date obterDataUltimaInternacaoPaciente(Integer pacCodigo) {
		return this.notificacaoGmrPacienteRN.obterDataUltimaInternacaoPaciente(pacCodigo);
	}
	
	@Override
	public void criarNotificacaoGMR(Integer pacCodigo, Integer ambSeq, Integer bmrSeq) {
		this.notificacaoGmrPacienteRN.criarNotificacaoGMR(pacCodigo, ambSeq, bmrSeq);
	}
	
	@Override
	public void desativarNotificacao(Integer seq) {
		this.notificacaoGmrPacienteRN.desativarNotificacao(seq);
	}

	@Override
	public List<MciNotasCCIH> bucarNotasCCIHPorPacCodigo(Integer pacCodigo) {
		return mciNotasCCIHDAO.bucarNotasCCIHPorPacCodigo(pacCodigo);
	}

	@Override
	public void excluirMciNotasCCIH(Integer seq) {
		mciNotasCCIHRN.excluir(seq);		
	}

	@Override
	public void persistirMciNotasCCIH(MciNotasCCIH notaCCIH) throws ApplicationBusinessException {
		mciNotasCCIHRN.persistirNotaCCIH(notaCCIH);		
	}
	
	@Override
	public Long listarSuggestionBoxBacteriaMultirCount(String filtro){
		return mciBacteriasMultirDAO.listarSuggestionBoxBacteriaMultirCount(filtro);
	}
	
	@Override
	public List<MciBacteriaMultir> listarSuggestionBoxBacteriaMultir(String filtro){
		return mciBacteriasMultirDAO.listarSuggestionBoxBacteriaMultir(filtro, false);
	}
	
	@Override
	public List<RelatorioNotificGermeMultirresistenteVO> listarRelatorioPacientesPortadoresGermeMultiResistente(String paramFibrose, Integer bacteriaSeq, Short unidadeSeq, Boolean indNotificao){
		return mciNotificacaoGmrDAO.listarRelatorioPacientesPortadoresGermeMultiResistente(paramFibrose, bacteriaSeq, unidadeSeq, indNotificao);
	}
	
	@Override
	public String obterLocalPaciente(Integer codigoPaciente) {
		return this.notificacaoGmrPacienteRN.obterLocalPaciente(codigoPaciente);
	}
	
	@Override
	public List<RelatorioNotificGermeMultirresistenteVO> obterDadosRelatorioGermesMultirresistente(String paramFibrose, Integer bacteriaSeq, Short unidadeSeq, Boolean indNotificao){
		return relatorioNotificGermeMultirresistenteRN.obterDadosRelatorio(paramFibrose, bacteriaSeq, unidadeSeq, indNotificao);
	}
	
	@Override
	public List<MciBacteriaMultir> pesquisarDescricaoBacteria(Integer pacCodigo){
		return mciNotificacaoGmrDAO.pesquisarDescricaoBacteria(pacCodigo);
	}
	
	@Override
	public String obterUnidFuncionalDescAndar(Short unfSeq){
		return vAghUnidFuncionalDAO.obterUnidFuncionalDescAndar(unfSeq);
	}
	
	@Override
	public List<MciNotificacaoGmr> pesquisarNotificacaoAtiva(Integer pacCodigo){
		return mciNotificacaoGmrDAO.pesquisarNotificacaoAtiva(pacCodigo);
	}
		
	@Override
	public void enviaEmailGmr(DominioOrigemAtendimento origem, Integer pacCodigo, Integer prontuario, String leitoID, Short unfSeq) throws ApplicationBusinessException {
		 this.notificacaoGmrPacienteRN.enviaEmailGmr(origem, pacCodigo, prontuario, leitoID, unfSeq);
	}	
	

}
