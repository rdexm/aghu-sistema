package br.gov.mec.aghu.controleinfeccao.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.ListaPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.MciBacteriasAssociadasVO;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaMultirresistenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaPacienteVO;
import br.gov.mec.aghu.controleinfeccao.vo.CriteriosBacteriaAntimicrobianoVO;
import br.gov.mec.aghu.controleinfeccao.vo.DoencaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.ExportacaoDadoVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioBuscaAtivaPacientesCCIHVO;
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
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioNotificGermeMultirresistenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.TipoGrupoRiscoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
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
import br.gov.mec.aghu.model.MciNotificacaoGmr;import br.gov.mec.aghu.model.MciNotasCCIH;
import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.model.MciParamReportGrupo;
import br.gov.mec.aghu.model.MciParamReportGrupoId;
import br.gov.mec.aghu.model.MciParamReportUsuario;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciProcedimentoRisco;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;

public interface IControleInfeccaoFacade extends Serializable {

	public void associarGrupoProced(Short porSeq, Short tgpSeq) throws ApplicationBusinessException;

	public void atualizaProcedimentoRisco(MciProcedimentoRisco entity,DominioSituacao situacao) throws ApplicationBusinessException;

	public void atualizarDuracaoMedidaPreventiva(MciDuracaoMedidaPreventiva entity) throws ApplicationBusinessException;
	
	public void atualizarBacteriaMultir(MciBacteriaMultir entity) throws ApplicationBusinessException;	

	public void excluirProcedimentoRisco(Short tgpSeq, Short porSeq) throws ApplicationBusinessException;

	public void excluirTopografiaInfeccao(TopografiaInfeccaoVO vo) throws BaseException;

	public void excluirTopografiaProcedimentoVO(TopografiaProcedimentoVO vo)throws BaseException;

	public void inserirDuracaoMedidaPreventiva(MciDuracaoMedidaPreventiva entity) throws ApplicationBusinessException;
	
	public void inserirMciBacteriaMultir(MciBacteriaMultir entity) throws ApplicationBusinessException;
	
	public void inserirMciBacteriaMultir(MciBacteriasAssociadasVO entity) throws ApplicationBusinessException;

	public void inserirMciMvtoFatorPredisponentes(
			MciMvtoFatorPredisponentes mciMvtoFatorPredisponentes, boolean flush);

	public List<TopografiaInfeccaoVO> listarMciTopografiaInfeccaoPorDescricaoESituacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String descricao, DominioSituacao situacao);

	public Long listarMciTopografiaInfeccaoPorDescricaoESituacaoCount(
			String descricao, DominioSituacao situacao);

	public List<TopografiaProcedimentoVO> listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seq, String descricao,
			DominioSituacao situacao, Short toiSeq);

	public Long listarMciTopografiaProcedimentoPorSeqDescSitSeqTopCount(
			Short seq, String descricao,
			DominioSituacao situacao, Short toiSeq);

	public List<MciMvtoFatorPredisponentes> listarMvtoFatorPredisponentes(
			Integer pacCodigo, AghAtendimentos atendimento);

	public List<MciMvtoFatorPredisponentes> listarMvtosFatorPredisponentesPorCodigoPaciente(
			Integer pacCodigo);

	public List<MciMvtoInfeccaoTopografias> listarMvtosInfeccoesTopografias(
			Integer pacCodigo, AghAtendimentos atendimento);

	public List<MciMvtoInfeccaoTopografias> listarMvtosInfeccoesTopologiasPorCodigoPaciente(
			Integer pacCodigo);

	public List<MciMvtoMedidaPreventivas> listarMvtosMedidasPreventivas(
			Integer pacCodigo, AghAtendimentos atendimento);

	public List<MciMvtoMedidaPreventivas> listarMvtosMedidasPreventivasPorCodigoPaciente(
			Integer pacCodigo);

	public List<MciMvtoProcedimentoRiscos> listarMvtosProcedimentosRiscos(
			Integer pacCodigo, AghAtendimentos atendimento);

	public List<MciMvtoProcedimentoRiscos> listarMvtosProcedimentosRiscosPorCodigoPaciente(
			Integer pacCodigo);

	public MciDuracaoMedidaPreventiva obterDuracaoMedidaPreventiva(Short seq);
	
	public MciBacteriaMultir obterMciBacteriaMultir(Integer seq);

	public List<MciDuracaoMedidaPreventiva> obterDuracaoMedidaPreventivaPorDescricaoSituacao(
			String descricao, DominioSituacao indSituacao, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc);
	
	public List<MciBacteriaMultir> obterBacteriaMultirPorSeqDescricaoSituacao(
			Integer seq, String descricao, DominioSituacao indSituacao, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc);	

	public Long obterDuracaoMedidaPreventivaPorDescricaoSituacaoCount(
			String descricao, DominioSituacao indSituacao);
	public Long obterBacteriasMultirPorSeqDescricaoSituacaoCount(
			Integer seq, String descricao, DominioSituacao indSituacao);	
	public MciFatorPredisponentes obterFatorPredisponentesPorPeso(
			BigDecimal valorPeso);
	public MciMvtoFatorPredisponentes obterMciMvtoFatorPredisponentesPorPaciente(
			Integer codigoPaciente);
	public MciProcedimentoRisco obterProcedimentoRisco(Short seq);
	public MciTipoGrupoProcedRisco obterTipoGrupo(Short tgpSeq);
	/**
	 * 
	 * @param atdSeq
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	public Boolean obterVerificacaoMvtoInfeccaoDeAtendimento(Integer atdSeq)
			throws ApplicationBusinessException;
	public void persistirMciMvtoFatorPredisponentes(
			MciMvtoFatorPredisponentes mciMvtoFatorPredisponentes);

	public void persistirMciMvtoInfeccaoTopografias(
			MciMvtoInfeccaoTopografias mciMvtoInfeccaoTopografias);

	public void persistirMciMvtoMedidaPreventivas(
			MciMvtoMedidaPreventivas mciMvtoMedidaPreventivas);

	public void persistirMciMvtoProcedimentoRiscos(
			MciMvtoProcedimentoRiscos mciMvtoProcedimentoRiscos);

	public void persistirTopografiaInfeccao(TopografiaInfeccaoVO vo) throws BaseException;

	public void persistirTopografiaProcedimento(TopografiaProcedimentoVO vo) throws BaseException;

	public List<TipoGrupoRiscoVO> pesquisarMciGrupoProcedRiscoPorSeqeSeqTipoGrupo(Short porSeq,Short tgpSeq);

	public List<MciProcedimentoRisco> pesquisarMciProcedRiscoPorSeqDescricaoSituacao(
			Short seq, String descricao, DominioSituacao indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	public Long pesquisarMciProcedRiscoPorSeqDescricaoSituacaoCount(Short seq,
			String descricao, DominioSituacao indSituacao);

	public List<Integer> pesquisarSequencialFatorPredisponentePorCodigoPaciente(
			Integer codigoPaciente);

	public List<MciTipoGrupoProcedRisco> pesquisarSuggestionGrupos(String strPesquisa,Short seqProcedimento);

	public void removerDuracaoMedidaPreventiva(Short seq, Date criadoEm)
			throws ApplicationBusinessException;
	
	public void removerBacteriaMultir(Integer seq)
			throws ApplicationBusinessException, BaseListException;

	public void removerMciMvtoFatorPredisponentes(
			MciMvtoFatorPredisponentes mciMvtoFatorPredisponentes, boolean flush);

	public List<TopografiaInfeccaoVO> suggestionBoxTopografiaInfeccaoPorSeqOuDescricao(String strPesquisa);

	public void validaeInsereProcedimentoRisco(MciProcedimentoRisco entity)throws ApplicationBusinessException;

	public void validarRemoverProcedimentoRisco(Short seq, Date criadoEm)throws ApplicationBusinessException, BaseListException;

	/**
	 * Verifica se o leito é controlado pela CCIH.
	 */


	public boolean verificaLeitoControladoCCIH(String leitoID);

	public boolean verificaLeitoExclusivoControleInfeccao(AinLeitos leitoId);

	/**
	 * Método para verificar se para o leito do paciente é necessário fazer
	 * solicitação de medida preventiva de infecção a enfermagem.
	 * 
	 * @param codigoPaciente
	 * @return
	 */


	public boolean verificarNecessidadeMedidaPreventivaInfeccao(
			Integer codigoPaciente);

	void alterarLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao);
	
	void alterarBacteriaAssociada(MciBacteriasAssociadasVO bacteriaAssociada) throws ApplicationBusinessException;

	//#1326
	void atualizarMciMicroorganismoPatologia(MciMicroorganismoPatologia entidade, RapServidores usuario) throws ApplicationBusinessException;

	void atualizarMciMicroorganismoPatologiaExame(ResultadoCodificadoExameVO entidade, RapServidores usuario) throws ApplicationBusinessException;

	void atualizarMciTipoGrupoProcedRisco(MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado) throws ApplicationBusinessException;

	void atualizarOrigemInfeccao(OrigemInfeccoesVO origemInfeccao);

	void atualizarPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia) throws ApplicationBusinessException;

	List<MciMicroorganismoPatologia> buscarMicroorganismoPorSeqInfeccao(
			Integer seq);

	void deletarMciMicroorganismoPatologia(MciMicroorganismoPatologia entidade, RapServidores usuario) throws ApplicationBusinessException;

	void deletarMciMicroorganismoPatologiaExame(ResultadoCodificadoExameVO entidade, RapServidores usuario) throws ApplicationBusinessException;

	void deletarMciTipoGrupoProcedRisco(MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado) throws ApplicationBusinessException;

	String excluirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) throws ApplicationBusinessException;

	void excluirPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia) throws ApplicationBusinessException;

	void inserirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao);

	void inserirMciMicroorganismoPatologia(MciPatologiaInfeccao mciPatologiaInfeccao, MciMicroorganismoPatologia entidade, RapServidores usuario) ;

	void inserirMciMicroorganismoPatologiaExame(MciMicroorganismoPatologia patologia, ResultadoCodificadoExameVO entidade, RapServidores usuario) throws ApplicationBusinessException;

	//#37965
	void inserirMciTipoGrupoProcedRisco(MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado) throws ApplicationBusinessException;

	void inserirPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia, Integer codigoPatologia);

	List<LocaisOrigemInfeccaoVO> listarLocaisOrigemInfeccoes(String codigoOrigem);
	
	List<MciBacteriasAssociadasVO> listarBacteriasAssociadas(Integer seqEditar);

	List<OrigemInfeccoesVO> listarOrigemInfeccoes(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String codigoOrigem, String descricao, DominioSituacao situacao);

	Long listarOrigemInfeccoesCount(String codigoOrigem, String descricao, DominioSituacao situacao);

	List<MciPalavraChavePatologia> listarPalavraChavePatologia(Integer codigoPatologia);

	MciTipoGrupoProcedRisco obterMciTipoGrupoProcedRiscoPorSeq(Short seq);

	MciPatologiaInfeccao obterPorChavePrimaria(int i);

	void persistirMciPatologiaInfeccao(final MciPatologiaInfeccao patologiaInfeccao) throws ApplicationBusinessException;

	List<MciDuracaoMedidaPreventiva> pesquisarDuracaoMedidaPreventivaPatologiaInfeccao(Object parametro);

	Long pesquisarDuracaoMedidaPreventivaPatologiaInfeccaoCount(Object parametro);

	List<MciTipoGrupoProcedRisco> pesquisarMciTipoGrupoProcedRisco(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short seq, String descricao, DominioSituacao situacao);

	Long pesquisarMciTipoGrupoProcedRiscoCount(Short codigo, String descricao, DominioSituacao situacao);

	List<MciPatologiaInfeccao> pesquisarPatologiaInfeccao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MciPatologiaInfeccao patologiaInfeccao);

	Long pesquisarPatologiaInfeccaoCount(MciPatologiaInfeccao patologiaInfeccao);

	List<MciTopografiaInfeccao> pesquisarTopografiaInfeccaoPatologiaInfeccao(
			Object parametro);

	Long pesquisarTopografiaInfeccaoPatologiaInfeccaoCount(Object parametro);

	List<AghUnidadesFuncionais> pesquisarUnidadesAtivas(String strPesquisa,
			boolean semEtiologia, String tipo);

	void removerMciPatologiaInfeccao(final Integer seq) throws BaseException;

	Boolean verificarNotificacaoGmrPorCodigo(Integer pacCodigo);
	
	void persistirMciAntimicrobiano(MciAntimicrobianos mciAntimicrobiano) throws ApplicationBusinessException;
	
	void excluirMciAntimicrobiano(Integer seq) throws ApplicationBusinessException, BaseListException;
	
	MciAntimicrobianos obterAntimicrobianoPorChavePrimaria(Integer seq);
	
	MciBacteriaMultir obterBacteriaPorChavePrimaria(Integer seq);
	
	List<MciAntimicrobianos> pesquisarAntimicrobianosPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao indSituacao, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc);
	
	Long pesquisarAntimicrobianosPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao indSituacao);
	
	public List<MciMvtoInfeccaoTopografias> pesquisarMovimentosInfeccoesInstituicaoHospitalar(final Integer ihoSeq);
	
	public List<MciMvtoMedidaPreventivas> pesquisarMovimentosMedidasPreventivasInstituicaoHospitalar(final Integer ihoSeq);

	List<MciFatorPredisponentes> obterSuggestionFatorPredisponentes(String strPesquisa);

	Long obterSuggestionFatorPredisponentesCount(String strPesquisa);

	List<BacteriaMultirresistenteVO> listarBacteriasMultir(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String codigo, String descricao, DominioSituacao situacao);

	Long listarBacteriasMultirCount(String codigo, String descricao, DominioSituacao situacao);

	List<NotificacaoFatorPredisponenteVO> listarNotificacoesPorPaciente(Integer pacCodigo);

	void inserirNotificacaoFatorPredisponente(MciMvtoFatorPredisponentes entity) throws BaseException;

	MciMvtoFatorPredisponentes obterNotificacaoFatorPredisponente(Integer seq);

	void atualizarNotificacaoFatorPredisponente(MciMvtoFatorPredisponentes entity, Integer seq) throws ApplicationBusinessException, BaseException;

	List<NotificacaoMedidasPreventivasVO> pesquisarNotificacoesMedidaPreventiva(Integer codigoPaciente) throws ApplicationBusinessException;
	
	List<NotificacoesGeraisVO> listarNotificacoesTopografias(final Integer codigoPaciente);
	
	List<NotificacoesGeraisVO> listarNotificacoesProcedimentosRisco(final Integer codigoPaciente);
	
	List<NotificacoesGeraisVO> listarNotificacoesFatoresPredisponentes(final Integer codigoPaciente);
	
	List<NotificacoesGeraisVO> listarNotasCCIH(final Integer codigoPaciente);
	
	List<NotificacoesGeraisVO> listarNotasCCIHNaoEncerradas(final Integer codigoPaciente);
	
	List<NotificacoesGeraisVO> listarNotificacoesDoencasCondicoes(final Integer codigoPaciente);

	List<DoencaInfeccaoVO> listarDoencaInfeccaoVO(String param);

	Long listarDoencaInfeccaoVOCount(String param);

	List<TopografiaProcedimentoVO> listarTopografiasAtivas(String param);

	Long listarTopografiasAtivasCount(String param);

	void persistirNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO notificacao) throws BaseException, BaseListException;

	void alterarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO notificacao) throws BaseException, BaseListException;

	void deletarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO notificacao) throws BaseException;
	
	void validarCadastroEdicaoNotificacao(NotificacaoMedidasPreventivasVO vo) throws BaseListException;
	
	List<MciAntimicrobianos> pesquisarAntiMicrobianosAtivosPorSeqDescricao(Object param);
	
	Long pesquisarAntiMicrobianosAtivosPorSeqDescricaoCount(Object param);
	
	List<CriteriosBacteriaAntimicrobianoVO> pesquisarCriterioGrmPorBmrSeq(Integer bmrSeq);
	
	void persistirCriterioGmr(MciCriterioGmr criterio, Integer ambSeq, Integer bmrSeq) throws ApplicationBusinessException;
	
	public void excluirCriterioGmr(Integer bmrSeq, Integer ambSeq) throws ApplicationBusinessException, BaseListException;
	
	MciCriterioGmr obterCriterioGmr(MciCriterioGmrId criterio);
	
	void removerNotificacaoFatorPredisponente(Integer seq) throws ApplicationBusinessException, BaseException;
	
	public List<OrigemInfeccoesVO> suggestionBoxTopografiaOrigemInfeccoes(String strPesquisa);

	List<ProcedRealizadoVO> obterProcedimentosPorPaciente(Integer pacCodigo, String strPesquisa) throws ApplicationBusinessException;
	
	ProcedRealizadoVO obterProcedimentoVOPorId(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp);

	public List<NotificacaoTopografiasVO> obterNotificacoesTopografia(Integer codigo) throws BaseException;

	public List<TopografiaProcedimentoVO> suggestionBoxTopografiaProcedimentoPorSeqOuDescricao(String strPesquisa);

	public TopografiaProcedimentoVO obterTopografiaProcedimento(Short seqTopografiaProcedimento) throws BaseException;

	public void persistirNotificacaoTopografia(NotificacaoTopografiasVO vo) throws BaseException;

	public void excluirNotificacaoTopografia(NotificacaoTopografiasVO vo) throws BaseException;

	NotificacaoFatorPredisponenteVO listarNotificacoesPorSeq(Integer seq);

	MciFatorPredisponentes obterFatorPredisponentesPorSeq(Short seq);

	public OrigemInfeccoesVO obterTopografiaOrigemInfeccoes(String codigoEtiologiaInfeccao) throws BaseException;

	List<MciProcedimentoRisco> pesquisarProcedimentoRisco(String strPesquisa);
	
	Long pesquisarProcedimentoRiscoCount(String strPesquisa);
	
	AghAtendimentos obterAghAtendimentoObterPorChavePrimaria(Integer seq);

	List<NotificacaoProcedimentoRiscoVO> listarNotificacoesProcedientoRiscoPorPaciente(Integer pacCodigo);

	void inserirNotificacaoProcedimentoRisco(MciMvtoProcedimentoRiscos entity) throws BaseException, ApplicationBusinessException;

	NotificacaoProcedimentoRiscoVO listarNotificacoesProcedimentoRiscoPorSeq(Integer seq);

	void removerNotificacaoProcedimentoRisco(Integer seq) throws ApplicationBusinessException, BaseException;

	MciProcedimentoRisco obterProcedimentoRiscoPorSeq(Short seq);

	void atualizarProcedimentoRisco(MciMvtoProcedimentoRiscos entity, Integer seq) throws ApplicationBusinessException, BaseException;
	
	List<OrigemInfeccoesVO> listarOrigemInfeccoes(String strPesquisa);
	
	Long listarOrigemInfeccoesCount(String strPesquisa);
	
	public List<DoencaInfeccaoVO> buscarDoencaInfeccaoPaiChaveAtivos(String param);
		
	public Long buscarDoencaInfeccaoPaiChaveAtivosCount(Object param);
	
	public List<TopografiaProcedimentoVO> listarTopografiaProcedimentoAtivas(String param);
	
	public Long listarTopografiaProcedimentoAtivasCount(Object param);
	
	List<ListaPacientesCCIHVO> pesquisarPacientesCCIH(FiltroListaPacienteCCIHVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException;
	
	Long pesquisarPacientesCCIHCount(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException;
	
	void validarPeriodoPesquisaAtendimento(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException;

	void validarNotificacaoSelecionada(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException;
	
	List<FatorPredisponenteVO> pesquisarFatorPredisponente(Short codigo, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);
	Long pesquisarFatorPredisponenteCount(Short codigo, String descricao, DominioSituacao situacao);
	
	void removerFatorPredisponente(Short codigo) throws ApplicationBusinessException;
	void gravarFatorPredisponente(MciFatorPredisponentes fatorPredisponentes) throws ApplicationBusinessException;
	
	List<GrupoReportRotinaCciVO> pesquisarGrupoReportRotinaCci(Short codigo, String descricao, DominioSituacao situacao, DominioPeriodicidade periodicidade);
	void removerGrupoReportRotinaCci(Short codigo)  throws ApplicationBusinessException;
	void gravarGrupoReportRotinaCci(MciGrupoReportRotinaCci mciGrupoReportRotinaCci) throws ApplicationBusinessException;
	MciGrupoReportRotinaCci obterGrupoReportRotinaCciPorSeq(Short seq);
	List<ParamReportGrupoVO> pesquisarParamReportGrupoPorSeqGrupo(Short seqGrupo);
	MciParamReportGrupo obterParamReportGrupoPorId(MciParamReportGrupoId id);
	MciParamReportUsuario obterParamReportUsuarioPorId(Integer seq);
	MciExportacaoDado obterExportacaoDadoPorId(Short seq);
	void removerMciParamReportGrupo(Integer pruSeq, Short grrCodigo)  throws ApplicationBusinessException;
	void gravarParamReportGrupo(MciParamReportGrupo mciParamReportGrupo)  throws ApplicationBusinessException;
	List<ExportacaoDadoVO> pesquisarExpDados(final String pesquisa, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);
	Long pesquisarExpDadosCount(final String strPesquisa);
	
	
	
	List<ParamReportUsuarioVO> pesquisarParamsReportUsuario(final String pesquisa, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) ;
	Long pesquisarParamsReportUsuarioCount(final String strPesquisa);

	List<BacteriaPacienteVO> pesquisarGermesDisponiveisListaGMRPaciente(final String parametro);
	
	Long pesquisarGermesDisponiveisListaGMRPacienteCount(final String parametro);
	
	List<MciCriterioGmr> pesquisarMciCriterioGrmAtivoPorBmrSeq(Integer brmSeq);
	
	public List<RelatorioBuscaAtivaPacientesCCIHVO> gerarRelatorioBuscaAtivaPacientes(FiltroListaPacienteCCIHVO filtro);

	List<NotificacoesGeraisVO> listarNotificacoesDoencasCondicoesBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq);
	
	List<NotificacoesGeraisVO> listarNotificacoesTopografiasBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq);
	
	List<NotificacoesGeraisVO> listarNotificacoesFatoresPredisponentesBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq);
	
	List<NotificacoesGeraisVO> listarNotificacoesProcedimentosRiscoBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq);



	List<GMRPacienteVO> pesquisarGermesMultirresistentesPaciente(final Integer prontuario);
	
	String buscarLocalizacaoPacienteCCIH(Integer pacCodigo) throws ApplicationBusinessException;
	
	Date obterDataUltimaInternacaoPaciente(Integer pacCodigo);
	
	void criarNotificacaoGMR(Integer pacCodigo, Integer ambSeq, Integer bmrSeq);
	
	void desativarNotificacao(Integer seq);

	String obterLocalPaciente(final Integer codigoPaciente);
	List<MciNotasCCIH> bucarNotasCCIHPorPacCodigo(final Integer pacCodigo);

	void excluirMciNotasCCIH(Integer seq);

	void persistirMciNotasCCIH(MciNotasCCIH notaCCIH) throws ApplicationBusinessException;


	List<MciBacteriaMultir> listarSuggestionBoxBacteriaMultir(String filtro);

	List<RelatorioNotificGermeMultirresistenteVO> listarRelatorioPacientesPortadoresGermeMultiResistente(String paramFibrose, Integer bacteriaSeq, Short unidadeSeq,Boolean indNotificao);

	Long listarSuggestionBoxBacteriaMultirCount(String filtro);
	
	List<RelatorioNotificGermeMultirresistenteVO> obterDadosRelatorioGermesMultirresistente(
			String paramFibrose, Integer bacteriaSeq, Short unidadeSeq,
			Boolean indNotificao);
	
	List<MciBacteriaMultir> pesquisarDescricaoBacteria(Integer pacCodigo);
	
	String obterUnidFuncionalDescAndar(Short unfSeq);
	
	List<MciNotificacaoGmr> pesquisarNotificacaoAtiva(Integer pacCodigo);
	
	void enviaEmailGmr(DominioOrigemAtendimento origem, Integer pacCodigo, Integer prontuario, String leitoID, Short unfSeq) throws ApplicationBusinessException;	

	
}

