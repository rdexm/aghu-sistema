package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioOpcoesFormulaParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
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

public interface ICadastrosApoioExamesFacade extends Serializable {


	public void adicionarProcedimentosRelacionados(List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO, VFatConvPlanoGrupoProcedVO convenio, FatItensProcedHospitalar fatItensProcedHospitalar, Short grcSeq)  throws ApplicationBusinessException;


	public void atualizarAelDescricoesResulPadrao(
			AelDescricoesResulPadrao descResultaPadrao) throws BaseException;


	public void atualizarAelExameGrupoCaracteristica(
			AelExameGrupoCaracteristica aelExameGrupoCaracteristica)
					throws BaseException;

	public void atualizarAelExamesDependentes(
			AelExamesDependentes exameDependente, AelExamesDependentes exaDepAux)
					throws ApplicationBusinessException;
	public void atualizarAelExamesMaterialAnaliseTiposAmostraExame(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			AelExamesMaterialAnalise aelExamesMaterialAnaliseAux,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
					throws ApplicationBusinessException;

	public void atualizarAelExecExamesMatAnalise(
			AelExecExamesMatAnalise execExamesMatAnalise)
					throws BaseException;

	public void atualizarAelGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica elemento) throws BaseException;

	public void atualizarAelRecomendacaoExame(
			AelRecomendacaoExame novoRecomendacaoExame)
					throws ApplicationBusinessException;

	public void atualizarAelResultadoCaracteristica(
			AelResultadoCaracteristica resultadoCaracteristica)
					throws BaseException;

	public void atualizarAelResultadoPadraoCampo(
			AelResultadoPadraoCampo resultadoPadraoCampo)
					throws BaseException;

	public void atualizarAelSinonimoExame(AelSinonimoExame aelSinonimoExame,
			boolean validaSeqp) throws ApplicationBusinessException,
			ApplicationBusinessException;

	public void atualizarAelTipoAmoExameConv(AelTipoAmoExameConv elemento) throws BaseException;

	public void atualizarEquipamentos(AelEquipamentos equipamentos)
			throws BaseException;

	public void atualizarExameResultadoNotificacao(final AelExameResuNotificacao exameResultadoNotificacao) throws ApplicationBusinessException;

	public void atualizarExamesProva(AelExamesProva examesProva)
			throws ApplicationBusinessException;

	public void atualizarGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException;


	public void atualizarGrupoXMaterialAnalise(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException;


	public void atualizarHorarioFuncionamentoUnidade(
			AghHorariosUnidFuncional horariosUnidFuncional)
					throws ApplicationBusinessException;


	public void atualizarHorarioRotina(AelHorarioRotinaColetas horarioRotina)
			throws ApplicationBusinessException;


	public void atualizarMetodoUnfExame(AelMetodoUnfExame metodoUnfExame)
			throws ApplicationBusinessException;



	public void atualizarSituacaoExameNotificacao(final AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException;


	public List<VAelExameMatAnalise> buscaEXADEPVAelExameMatAnalisePelaSigla(
			String siglaDescViewExaMatAnalise)
					throws ApplicationBusinessException;


	public List<RapServidores> buscaListRapServidoresVAelPessoaServidor(
			Object pesquisa, String emaExaSigla, Integer emaManSeq)
					throws ApplicationBusinessException;


	public AelExamesDependentes buscarAelExamesDependenteById(
			AelExamesDependentesId id) throws ApplicationBusinessException;


	public List<AelGrpTecnicaUnfExamesVO> buscarAelGrpTecnicaUnfExamesVOPorAelGrupoExameTecnica(
			AelGrupoExameTecnicas grupoExameTecnicas);


	public List<VAelExameMatAnalise> buscarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(String valPesquisa, String string)throws ApplicationBusinessException;


	public VAelExameMatAnalise buscarVAelExameMatAnalisePelaSiglaESeq(String exaSigla, Integer manSeq);


	public VAelExameMatAnalise buscarVAelExameMatAnalisePorExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise);


	public List<VAelExameMatAnalise> buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(String siglaDescViewExaMatAnalise);


	public Long buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnaliseCount(String siglaDescViewExaMatAnalise);



	public Long contarAelCampoLaudoPorVAelCampoLaudoExme(String string, String exaSigla, Integer manSeq);



	public Long contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(String valPesquisa);


	public Long contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(String valPesquisa, String indDependente);


	public Long countLaboratorioHemocentro(final AelLaboratorioExternos filtros);


	public Long countMedicoAtendimentoExterno(Map<Object, Object> filtersMap);


	public void desatacharVersaoLaudo(AelVersaoLaudo elemento);


	public void excluirAelTipoAmoExameConv(AelTipoAmoExameConv elemento) throws BaseException;


	public void excluirCampoLaudoPendencia(AelGrupoTecnicaCampoId id) throws BaseException;
	public void excluirExameNotificacao(final AelExamesNotificacaoId exameNotificacao) throws ApplicationBusinessException;

	public void excluirExameResultadoNotificacao(final AelExameResuNotificacaoId id) throws ApplicationBusinessException;
	public void excluirHorarioRotina(AelHorarioRotinaColetasId horarioRotinaId)
			throws ApplicationBusinessException;


	public void excluirModeloCarta(Short seq) throws ApplicationBusinessException;


	public void excluirRecipienteColeta(Integer seqAelRecipienteColeta) throws ApplicationBusinessException;


	public void inserirAelExamesDependentes(
			AelExamesDependentes exameDependente,
			List<AelExameDeptConvenio> listaConveniosPlanosDependentes)
					throws ApplicationBusinessException;

	public void inserirAelExamesMaterialAnaliseTipoAmostraExame(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
					throws ApplicationBusinessException;


	public void inserirAelExecExamesMatAnalise(
			AelExecExamesMatAnalise execExamesMatAnalise)
					throws BaseException;

	public void inserirAelGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica elemento) throws BaseException;

	public void inserirAelRecomendacaoExame(
			AelRecomendacaoExame recomendacaoExame) throws ApplicationBusinessException;


	public void inserirAelResultadoCaracteristica(
			AelResultadoCaracteristica resultadoCaracteristica)
					throws BaseException;


	public void inserirAelResultadoPadraoCampo(
			AelResultadoPadraoCampo resultadoPadraoCampo)
					throws BaseException;


	public void inserirAelSinonimoExame(AelSinonimoExame aelSinonimoExame)
			throws ApplicationBusinessException;


	public void inserirAelTipoAmoExameConv(AelTipoAmoExameConv elemento) throws BaseException;


	/**
	 * #2211 - Manter Cadastro de Equipamentos
	 */

	public void inserirEquipamentos(AelEquipamentos equipamentos)
			throws BaseException;

	public void inserirExameDeptConvenioEmLote(
			AelExamesDependentes exameDependente,
			List<AelExameDeptConvenio> listaConveniosPlanosDependentes)
					throws ApplicationBusinessException;

	public void inserirExamesProva(AelExamesProva examesProva)
			throws ApplicationBusinessException;

	public void inserirHorarioFuncionamentoUnidade(
			AghHorariosUnidFuncional horariosUnidFuncional)
					throws ApplicationBusinessException;

	public void inserirMetodoUnfExame(AelMetodoUnfExame metodoUnfExame)
			throws ApplicationBusinessException;

	public AelParametroCamposLaudo inserirNovoCampoTela(AelVersaoLaudo versaoLaudo, DominioObjetoVisual objetoVisual);

	/*
	 * Horário de Coleta do Exame
	 */

	public List<AelTipoAmoExameConv> listarAelTipoAmoExameConvPorTipoAmostraExame(
			String emaExaSigla, Integer emaManSeq,Integer manSeq, DominioOrigemAtendimento origemAtendimento);


	public List<AelResultadoCaracteristica> listarResultadoCaracteristica(
			String param, Integer calSeq)
					throws ApplicationBusinessException;


	public List<AelResultadosPadrao> listarResultadoPadraoCampoPorExameMaterial(String emaExaSigla, Integer emaManSeq);


	public List<AelSitItemSolicitacoes> listarSituacoesExame(String texto);


	public void montaFormulaParametroCampoLaudo(AelParametroCamposLaudo parametroCampoLaudo, AelParametroCamposLaudo parametroCampoLaudoAdicionado, DominioOpcoesFormulaParametroCamposLaudo opcao);


	public AelDescricoesResulPadrao obterAelDescricoesResulPadrao(
			Integer rpcRpaSeq, Integer seqP);


	public AelExecExamesMatAnalise obterAelExecExamesMatAnalisePorId(
			AelExecExamesMatAnaliseId id);


	public AelHorarioRotinaColetas obterAelHorarioRotinaColetaPorId(
			AelHorarioRotinaColetasId horarioId);

	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasPorParametros(
			Short unidadeColeta, Short unidadeSolicitante, Date dataHora,
			String dia, DominioSituacao situacaoHorario);

	public AelServidorUnidAssinaElet obterAelServidorUnidAssinaEletPorId(AelServidorUnidAssinaEletId servidorUnidAssinaEletId);

	public List<AelExameDeptConvenio> obterConvenioPlanoDependentes(
			AelExamesDependentesId id);

	public List<VAelExameMatAnalise> obterExameMaterialAnalise(
			String siglaDescViewExaMatAnalise)
					throws ApplicationBusinessException;

    public Long obterExameMaterialAnaliseCount(
            String siglaDescViewExaMatAnalise)
            throws ApplicationBusinessException;

	public AelExamesNotificacao obterExameNotificacao(final AelExamesNotificacaoId exameNotificacaoId);

	public Short obterExameResultadoNotificacaoNextSeqp(AelExameResuNotificacaoId id);

	public List<AelExamesDependentes> obterExamesDependentes(String sigla,
			Integer manSeq);

	public AelGrupoMaterialAnalise obterGrupoMaterialAnalisePorSeq(Integer seq);


	public AelLaboratorioExternos obterLaboratorioExternoPorId(Integer seq);
	public Long obterModeloCartaCount(AelModeloCartas modeloCarta);
	public AelModeloCartas obterModeloCartaPorId(Short seq);


	public FatConvenioSaudePlano obterPlanoPorId(Byte seqConvenioSaudePlano,
			Short codConvenioSaude);

	public AelResultadoPadraoCampo obterResultadoPadraoCampoPorParametro(
			Integer calSeq, Integer seqP, Integer rpaSeq);


	public void persistirAelDescricoesResulPadrao(
			AelDescricoesResulPadrao descResultaPadrao) throws BaseException;


	public void persistirAelExameGrupoCaracteristica(
			AelExameGrupoCaracteristica aelExameGrupoCaracteristica)
					throws BaseException;


	public void persistirAelExames(AelExames aelExames, String sigla)
			throws BaseException;


	public void persistirAelGrpTecnicaUnfExames(
			AelGrpTecnicaUnfExames grpTecnicaUnfExames) throws BaseException;


	public void persistirAelGrupoExameTecnicas(
			AelGrupoExameTecnicas grupoExameTecnicas) throws BaseException;


	public void persistirAelLoteExame(AelLoteExame loteExame, Boolean flush)
			throws BaseException;
	public void persistirAelResultadoPadraoCampo(
			AelResultadoPadraoCampo resultadoPadraoCampo)
					throws BaseException;

	public void persistirAelServidoresExameUnid(
			AelServidoresExameUnid aelServidoresExameUnid)
					throws ApplicationBusinessException;


	public AelCampoLaudo persistirCampoLaudo(AelCampoLaudo campoLaudo) throws BaseException;


	public void persistirCampoLaudoPendencia(AelGrupoTecnicaCampo elemento) throws BaseException;

	public void persistirExameEspecialidade(
			AelExamesEspecialidade exameEspecialidade)
					throws ApplicationBusinessException;

	public void persistirExameNotificacao(final AelExamesNotificacaoId id, final DominioSituacao situacao) throws ApplicationBusinessException;

	public void persistirExameResultadoNotificacao(final AelExameResuNotificacao exameResultadoNotificacao) throws ApplicationBusinessException;


	public void persistirGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException;

	public void persistirGrupoXMaterialAnalise(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException;


	public void persistirHorarioColetaExame(
			AelExameHorarioColeta exameHorarioColeta)
					throws ApplicationBusinessException;


	public void persistirHorarioRotina(AelHorarioRotinaColetas horarioRotina)
			throws ApplicationBusinessException;

	public AelUnidExecUsuario persistirIdentificacaoUnidadeExecutora(
			AelUnidExecUsuario elemento, AghUnidadesFuncionais unidFuncional)
					throws ApplicationBusinessException;

	public AelIntervaloColeta persistirIntervaloColeta(
			AelIntervaloColeta intervalo)
					throws ApplicationBusinessException;

	public AelMateriaisAnalises persistirMaterialAnalise(
			AelMateriaisAnalises material)
					throws ApplicationBusinessException;

	public void persistirModeloCarta(AelModeloCartas modeloCarta) throws ApplicationBusinessException;

	public void persistirPermissaoUnidadeSolicitante(
			AelPermissaoUnidSolic aelPermissaoUnidSolic)
					throws BaseException;

	/* Manter horário rotina coleta */

	public void persistirResultadoPadrao(AelResultadosPadrao resultadoPadrao)
			throws BaseException;

	public void persistirRetornoCarta(AelRetornoCarta retornoCartaNew) throws ApplicationBusinessException;

	public void persistirSinonimoCampoLaudo(
			AelSinonimoCampoLaudo sinonimoCampoLaudo) throws BaseException;

	public void persistirUnidadeExecutoraExames(
			AelUnfExecutaExames aelUnfExecutaExames,
			AelExamesMaterialAnalise examesMaterialAnalise)
					throws BaseException;

	public void persistirVersaoLaudo(AelVersaoLaudo versaoLaudo) throws BaseException;

	public void persistMotivoCancelamento(
			AelMotivoCancelaExames motivoCancelamento) throws BaseException;

	public List<AelGrupoRecomendacao> pesquisaGrupoRecomendacaoPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelGrupoRecomendacao grupoRecomendacao);

	public List<AelCampoCodifRelacionado> pesquisarCampoCodificadoPorParametroCampoLaudoECampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudo);


	public List<AelCampoLaudo> pesquisarCampoLaudoExameMaterial(Object param, String emaExaSigla, Integer emaManSeq);

	public List<AelCampoLaudoRelacionado> pesquisarCampoLaudoPorParametroCampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudo);

	public List<AelCampoVinculado> pesquisarCampoVinculadoPorParametroCampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudoByAelCvcPclFk1);

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(
			String filtro);

	public Long pesquisarConvenioSaudePlanosCount(String filtro);

	public List<AelExecExamesMatAnalise> pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(
			AelExamesMaterialAnaliseId id);

	public List<AelEquipamentos> pesquisarEquipamentosPorSiglaOuDescricao(Object parametro, Short unfSeq) throws ApplicationBusinessException;

	/**
	 * 5361
	 */

	public List<AelEquipamentos> pesquisarEquipamentosSeqDescricao(
			Object parametro) throws ApplicationBusinessException;

	public Long pesquisarExameGrupoCaracteristicaCount(
			AelExameGrupoCaracteristica exameGrupoCaracteristica);

	/** 5375 **/

	public List<AelExameGrupoCaracteristicaVO> pesquisarExameGrupoCarateristica(
			AelExameGrupoCaracteristica exameGrupoCaracteristica,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	public List<AelExameResuNotificacao> pesquisarExameResultadoNotificacao(String sigla, Integer manSeq, Integer calSeq);


	public List<AelGrupoMaterialAnalise> pesquisarGrupoMaterialAnalise(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelGrupoMaterialAnalise grupoMaterialAnalise);


	public Long pesquisarGrupoMaterialAnaliseCount(AelGrupoMaterialAnalise grupoMaterialAnalise);

	/** 5374 **/
	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	public Long pesquisarGrupoResultadoCaracteristicaCount(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica);

	public List<AelGrupoXMaterialAnalise> pesquisarGrupoXMaterialAnalisePorGrupo(Integer gmaSeq);

	/** #5927 **/

	public List<AelLaboratorioExternos> pesquisarLaboratorioHemocentro(
			final AelLaboratorioExternos filtros, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	public List<AghMedicoExterno> pesquisarMedicoExternoPaginado(
			Map<Object, Object> filtersMap, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	public List<AelModeloCartas> pesquisarModeloCarta(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelModeloCartas modeloCarta);

	public List<AelRetornoCarta> pesquisarRetornoCarta(Integer filtroSeq, String filtroDescricaoRetorno, DominioSituacao filtroIndSituacao);

	public List<AelSinonimoExame> pesquisarSinonimosExames(AelExames exame,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);


	public Long pesquisarSinonimosExamesCount(AelExames exame);

	public List<AelVersaoLaudo> pesquisarVersaoLaudo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AghUnidadesFuncionais unidadeFuncional,
			AelVersaoLaudo versaoLaudo);

	/** 5391 **/

	public Long pesquisarVersaoLaudoCount(AghUnidadesFuncionais unidadeFuncional, AelVersaoLaudo versaoLaudo);

	public void removerAelDescricoesResulPadrao(
			AelDescricoesResulPadrao descResultaPadrao) throws BaseException;

	public void removerAelExameGrupoCaracteristica(AelExameGrupoCaracteristicaId id) throws BaseException;

	public void removerAelExames(AelExames aelExames) throws BaseException;

	public void removerAelExamesDependentes(AelExamesDependentes exameDependente)
			throws BaseException;

	public void removerAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
					throws BaseException;

	public void removerAelGrpTecnicaUnfExames(AelGrpTecnicaUnfExamesId id) throws BaseException;

	public void removerAelGrupoExameTecnicas(Integer seq) throws BaseException;


	public void removerAelLoteExame(AelLoteExameId loteExameId)
			throws ApplicationBusinessException;


	public void removerAelPermissaoUnidSolic(String emaExaSigla,
			Integer emaManSeq, Short unfSeq, Short unfSeqSolicitante)
					throws BaseException;


	public void removerAelRecomendacaoExame(
			AelRecomendacaoExame novoRecomendacaoExame);

	public void removerAelResultadoCaracteristica(Integer seq) throws BaseException;


	public void removerAelResultadoPadraoCampo(
			AelResultadoPadraoCampo resultadoPadraoCampo)
					throws BaseException;


	public void removerAelServidoresExameUnid(String ufeEmaExaSigla,
			Integer ufeEmaManSeq, Short ufeUnfSeq, Integer serMatricula,
			Short serVinCodigo) throws ApplicationBusinessException;


	public void removerAelSinonimosExames(AelSinonimoExame aelSinonimoExame)
			throws ApplicationBusinessException;


	public void removerCampoLaudo(Integer seq) throws BaseException;

	public void removerExameEspecialidade(String emaExaSigla,
			Integer emaManSeq, Short unfSeq, Short espSeq)
					throws ApplicationBusinessException;


	public void removerExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException;


	public void removerExecExamesMatAnalise(
			AelExecExamesMatAnalise execExamesMatAnalise)
					throws BaseException;


	public void removerGrupoMaterialAnalise(Integer seqExclusao) throws ApplicationBusinessException;


	public AelGrupoResultadoCaracteristica removerGrupoResultadoCaracteristica(
			Integer seqExclusao) throws BaseException;

	public void removerGrupoXMaterialAnalise(AelGrupoXMaterialAnaliseId id);

	public void removerHorarioColetaExame(
			AelExameHorarioColeta horarioColetaExame) throws ApplicationBusinessException;

	public void removerIntervaloColeta(Short codigo)
			throws ApplicationBusinessException;

	public void removerListaDependentes(AelExamesDependentes exameDependente)
			throws BaseException;

	public void removerMaterialAnalise(Integer codigo)
			throws ApplicationBusinessException;

	public void removerMotivoCancelamento(Short seqAelMotivoCancelaExames) throws BaseException;

	public void removerUnidadeExecutoraExames(String emaExaSigla,
			Integer emaManSeq, Short unfSeq) throws BaseException;

	public void removerVersaoLaudo(AelVersaoLaudoId id) throws BaseException;

	public void saveOrUpdateMedicoExterno(AghMedicoExterno medicoExterno)
			throws BaseException;

	public void saveOrUpdateRecipienteColeta(
			AelRecipienteColeta recipienteColeta) throws BaseException;

	public List<AelResultadoCodificado> sbListarResultadoCodificado(
			String param, Integer calSeq)
					throws ApplicationBusinessException;

	public List<AelResultadosPadrao> sbListarResultadosPadrao(String param)
			throws ApplicationBusinessException;


	public void validaConvenioPlanoJaInserido(
			List<AelExameDeptConvenio> listaConveniosPlanosDependentes,
			AelExameDeptConvenioId exaConvIdToCompare)
					throws ApplicationBusinessException;


	public boolean validarAdicionarItemTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame, AelExamesMaterialAnalise examesMaterialAnalise, List<AelTipoAmostraExame> listaTiposAmostraExame) throws ApplicationBusinessException;

	public AelGrupoMaterialAnalise validarCamposGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException;

	public void validarExpressaoFormula(String formula) throws ApplicationBusinessException;

	/**
	 * oradb aelk_exa_rn.rn_exap_ver_delecao
	 * 
	 * @param data
	 * @param aghuParametrosEnum
	 * @param exceptionForaPeriodoPermitido
	 * @param erroRecuperacaoAghuParametro
	 * @throws ApplicationBusinessException
	 */
	public void verificaDataCriacao(final Date data,
			final AghuParametrosEnum aghuParametrosEnum,
			BusinessExceptionCode exceptionForaPeriodoPermitido,
			BusinessExceptionCode erroRecuperacaoAghuParametro)
					throws BaseException;

	public void verificarTiposAmostraExame(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
					throws ApplicationBusinessException;

	/**
	 * ORADB aelk_ael_rn.rn_aelp_ver_unf_ativ
	 * 
	 * @param aghUnidadesFuncionais
	 * @throws ApplicationBusinessException
	 */
	public void verUnfAtiv(AghUnidadesFuncionais aghUnidadesFuncionais)
			throws ApplicationBusinessException;

	AelSitItemSolicitacoes ativarDesativarSituacaoExame(AelSitItemSolicitacoes situacao,  boolean ativar) throws ApplicationBusinessException;

	AelUnidMedValorNormal ativarInativarAelUnidMedValorNormal(AelUnidMedValorNormal aumvn) throws ApplicationBusinessException;

	AelAnticoagulante ativarInativarAnticoagulante(AelAnticoagulante aelAnticoagulante) throws ApplicationBusinessException;

	void atualizarAelServidorUnidAssinaElet(AelServidorUnidAssinaElet servidorUnidAssinaElet) throws ApplicationBusinessException;

	List<AelCampoLaudo> buscarAelCampoLaudoPorVAelCampoLaudoExme(final String valPesquisa, final String velEmaExaSigla, final Integer velEmaManSeq,
			final boolean limitarEm100);

	List<AelExameReflexo> buscarAelExamesReflexo(final String exaSigla, final Integer manSeq);

	List<AelResultadoCodificado> buscarAelResultadoCodificado(final String string, final Integer seq);

	List<ResultadoCodificadoExameVO> buscarResultadosCodificados(String param);
	
	List<ResultadoCodificadoExameVO> buscarResultadosCodificadosPorDescricao(String descricao) throws ApplicationBusinessException;

	List<ResultadoCodificadoExameVO> buscarResultadosCodificadosPorMicroorgPatologia(MciMicroorganismoPatologia patologia);

	List<VAelExameMatAnalise> buscarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(final String siglaDescViewExaMatAnalise)
			throws ApplicationBusinessException;

	void excluirAelAtendimentoDiversos(AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException;

	void excluirAelCadCtrlQualidades(Integer seq) throws BaseException;

	void criarAghResponsavelJn(AghResponsavel responsavel, DominioOperacoesJournal operacao);

	void excluirAelDadosCadaveres(final Integer seq) throws BaseException;

	void excluirAelExameReflexo(final AelExameReflexo aelExameReflexoExcluir) throws ApplicationBusinessException;

	void excluirParametroCamposLaudo(final AelParametroCamposLaudo parametro) throws BaseException;

	void gravarAelGrupoRecomendacao(AelGrupoRecomendacao grupoRecomendacao, List<AelGrupoRecomendacaoExame> lista) throws BaseException;

	void inserirAelCampoCodifRelacionado(AelCampoCodifRelacionado elemento) throws BaseException;

	void inserirAelCampoLaudoRelacionado(AelCampoLaudoRelacionado elemento) throws BaseException;

	void inserirAelCampoVinculado(AelCampoVinculado campoVinculado) throws BaseException;

	void inserirAelServidorUnidAssinaElet(AghUnidadesFuncionais unidadeFuncional, RapServidores servidor, Boolean ativo)throws ApplicationBusinessException;

	List<AelAgrpPesquisas> obterAelAgrpPesquisasList(
			AelAgrpPesquisas filtros, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc);

	Long obterAelAgrpPesquisasListCount(AelAgrpPesquisas filtros);

	AelAgrpPesquisas obterAelAgrpPesquisasPorId(final Short seq);

	List<AelAgrpPesquisaXExameVO> obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(final AelAgrpPesquisas aelAgrpPesquisas, final String filtro, final boolean limitarRegsPai);

	AelAgrpPesquisaXExame obterAelAgrpPesquisaXExamePorId(final Integer seq);

	AelCadCtrlQualidades obterAelCadCtrlQualidadesPorId(Integer seq);

	List<AelDadosCadaveres> obterAelDadosCadaveresList(final AelDadosCadaveres filtros,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc);

	Long obterAelDadosCadaveresListCount(final AelDadosCadaveres filtros);

	AelDadosCadaveres obterAelDadosCadaveresPorId(Integer seq);

	AelEquipamentos obterAelEquipamentosId(Short seq);

	AelEquipamentos obterAelEquipamentosId(Short seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	AelExameReflexo obterAelExameReflexoPorId(final AelExameReflexoId id);

	AelExamesMaterialAnalise obterAelExamesMaterialAnaliseById(final AelExamesMaterialAnaliseId id);

	AelGrupoResultadoCaracteristica obterAelGrupoResultadoCaracteristica(Integer seq);


	AelGrupoResultadoCaracteristica obterAelGrupoResultadoCaracteristica(Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	AelResultadoCodificado obterAelResultadoCodificadoPorId(AelResultadoCodificadoId id);

	AelTipoAmoExameConv obterAelTipoAmoExameConvPorId(AelTipoAmoExameConvId id);

	List<AelCadCtrlQualidades> obterCadCtrlQualidadesList(final AelCadCtrlQualidades filtros,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc);

	Long obterCadCtrlQualidadesListCount(final AelCadCtrlQualidades filtros);

	AelLaboratorioExternos obterLaboratorioExternoPorId(Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	void persistirAelAgrpPesquisas(AelAgrpPesquisas aelAgrpPesquisas) throws BaseException;

	void persistirAelAgrpPesquisaXExame(AelAgrpPesquisaXExame aelAgrpPesquisaXExame) throws BaseException;

	void persistirAelAtendimentoDiversos(AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException;

	void persistirAelCadCtrlQualidades(AelCadCtrlQualidades aelCadCtrlQualidades) throws BaseException;

	void persistirAelDadosCadaveres(AelDadosCadaveres aelDadosCadaveres) throws BaseException;

	void persistirAelExameReflexo(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException;

	AelAnticoagulante persistirAnticoagulante(AelAnticoagulante elemento) throws ApplicationBusinessException;

	void persistirParametroCamposLaudo(final AelParametroCamposLaudo aelParametroCamposLaudos) throws BaseException;

	void persistirParametroCamposLaudo(final List<AelParametroCamposLaudo> aelParametroCamposLaudos) throws BaseException;

	void persistirParametroCamposLaudo(final List<AelParametroCamposLaudo> aelParametroCamposLaudos, final boolean novaVersao) throws BaseException;

	void recarregaValoresPerdidos(AelParametroCamposLaudo parametro);

	AelRegiaoAnatomica persistirRegiaoAnatomica(AelRegiaoAnatomica elemento) throws ApplicationBusinessException;

	void persistirSalaExecutoraExame(
			final AelSalasExecutorasExames salaExecutora)
					throws ApplicationBusinessException;

	AelSitItemSolicitacoes persistirSituacaoExame(AelSitItemSolicitacoes situacao) throws ApplicationBusinessException;

	AelUnidMedValorNormal persistirUnidadeMedida(AelUnidMedValorNormal elemento) throws ApplicationBusinessException;

	Long pesquisaGrupoRecomendacaoPaginadaCount(	AelGrupoRecomendacao grupoRecomendacao);

	List<AelDadosCadaveres> pesquisarDadosCadaveresInstituicaoHospitalarProcedencia(Integer ihoSeq);

	List<AelDadosCadaveres> pesquisarDadosCadaveresInstituicaoHospitalarRetirada(Integer ihoSeq);



	List<AelServidorUnidAssinaElet> pesquisarServidorUnidAssinaEletPorUnfSeq(Short seq);

	void removerAelGrupoRecomendacao(Integer seqExclusao) throws BaseException;

	void removerAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws BaseException;

	void removerAnticoagulante(Integer seqAnticoagulante) throws ApplicationBusinessException;

	void removerEquipamento(AelEquipamentos equipamentos) throws BaseException;

	void removerHorarioFuncionamentoUnidade(AghHorariosUnidFuncionalId id) throws ApplicationBusinessException;

	void removerLaboratorioExterno(Integer seqExclusao) throws ApplicationBusinessException, BaseException;


	void removerMedicoAtendimentoExterno(Integer seqExclusao) throws ApplicationBusinessException, BaseException;




	void removerRegiaoAnatomica(Integer seqRegiaoAnatomica) throws ApplicationBusinessException;

	void removerSalaExecutoraExame(final AelSalasExecutorasExamesId id) throws ApplicationBusinessException;
	void salvarProcedimentosRelacionados(FatProcedHospInternos fatProcedHospInternos,
			List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO, Short cpgGrcSeq, Short cpgCphPhoSeq)
					throws ApplicationBusinessException;



	void saveOrUpdateLaboratorioHemocentro(AelLaboratorioExternos laboratorioExternos) throws BaseException;


	void validaCampoLaudo(AelParametroCamposLaudo parametroCamposLaudo) throws ApplicationBusinessException;


	void verificarSituacaoDuplicada(AelVersaoLaudo versaoLaudo)
			throws BaseException;


	Long buscarResultadosCodificadosCount(String param);

	public ProtocoloEntregaExamesVO recuperarRelatorioEntregaExames(Map<Integer, Vector<Short>> listaItens, AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes);

	public ProtocoloEntregaExamesVO recuperarNovoRelatorioEntregaExames(AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes);
	
	List<ResultadoCodificadoExameVO> buscarResultadosCodificadosBacteriaMultir(
			String param) throws ApplicationBusinessException;


	Long buscarResultadosCodificadosBacteriaMultirCount(String param) throws ApplicationBusinessException;
	
		
	void salvarResponsavel(AghResponsavel responsavel, Integer seqAinRep) throws ApplicationBusinessException;
	
		
	public AghResponsavel obterResponsavelPorPaciente(AipPacientes paciente);
	
	public AghResponsavel obterResponsavelPorSeq(Integer seq);
	
	public List<ResponsavelVO> listarResponsavel(String parametro);
	
	public ResponsavelVO obterResponsavelVo(AghResponsavel resp);
	
	public void persistirProtocolo(Map<Integer, Vector<Short>> listaItens, AelProtocoloEntregaExames protocolo) throws ApplicationBusinessException;
	
	public void persistirNovoProtocolo(AelProtocoloEntregaExames protocolo) throws ApplicationBusinessException;

	public AelParametroCamposLaudo obterAelParametroCamposLaudoPorId(AelParametroCampoLaudoId id);


	FatProcedHospInternos insereProcedimentoHospitalarInterno(
			AelExamesMaterialAnalise materialExameSelecionado)
			throws ApplicationBusinessException;


	Boolean phiJaCriado(AelExames exame,
			AelExamesMaterialAnalise materialAnalise);


	FatProcedHospInternos pesquisaPHI(AelExames exame,
			AelExamesMaterialAnalise materialAnalise);

	public AelResultadosPadrao obterAelResultadoPadraoPorId(Integer seq);
}