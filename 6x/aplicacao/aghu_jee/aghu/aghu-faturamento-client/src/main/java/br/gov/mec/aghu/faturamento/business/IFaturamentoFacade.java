package br.gov.mec.aghu.faturamento.business;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.blococirurgico.vo.CursoPopulaProcedimentoHospitalarInternoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemPendencia;
import br.gov.mec.aghu.dominio.DominioMensagemEstornoAIH;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioOpcaoEncerramentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioOrigemSugestoesDesdobramento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.dominio.TipoArquivoRelatorio;
import br.gov.mec.aghu.exames.questionario.vo.VFatProcedSusPhiVO;
import br.gov.mec.aghu.faturamento.vo.AIHFaturadaPorPacienteVO;
import br.gov.mec.aghu.faturamento.vo.AihFaturadaVO;
import br.gov.mec.aghu.faturamento.vo.AihPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.AihsFaturadasPorClinicaVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.CaracteristicaPhiVO;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.ConsultaRateioProfissionalVO;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.ContaHospitalarInformarSolicitadoVO;
import br.gov.mec.aghu.faturamento.vo.ContasPreenchimentoLaudosVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.DadosCaracteristicaTratamentoApacVO;
import br.gov.mec.aghu.faturamento.vo.DemonstrativoFaturamentoInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.FatArqEspelhoProcedAmbVO;
import br.gov.mec.aghu.faturamento.vo.FatBancoCapacidadeVO;
import br.gov.mec.aghu.faturamento.vo.FatCnesVO;
import br.gov.mec.aghu.faturamento.vo.FatDadosContaSemIntVO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoEncerramentoPreviaVO;
import br.gov.mec.aghu.faturamento.vo.FatLogErrorVO;
import br.gov.mec.aghu.faturamento.vo.FatMotivoRejeicaoContasVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadosVO;
import br.gov.mec.aghu.faturamento.vo.FatSaldoUTIVO;
import br.gov.mec.aghu.faturamento.vo.FatUnidadeFuncionalCnesVO;
import br.gov.mec.aghu.faturamento.vo.FatcVerCarPhiCnvVO;
import br.gov.mec.aghu.faturamento.vo.FaturaAmbulatorioVO;
import br.gov.mec.aghu.faturamento.vo.FaturamentoPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.FtLogErrorVO;
import br.gov.mec.aghu.faturamento.vo.ImprimirVencimentoTributosVO;
import br.gov.mec.aghu.faturamento.vo.ItemProcedimentoHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.ItensRealizadosIndividuaisVO;
import br.gov.mec.aghu.faturamento.vo.ListarContasHospPacientesPorPacCodigoVO;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciaBPAVO;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciasInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.ParametrosGeracaoLaudoOtorrinoVO;
import br.gov.mec.aghu.faturamento.vo.PendenciaEncerramentoVO;
import br.gov.mec.aghu.faturamento.vo.PendenciasEncerramentoVO;
import br.gov.mec.aghu.faturamento.vo.PreviaDiariaFaturamentoVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoNaoFaturadoVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoRealizadoDadosOPMVO;
import br.gov.mec.aghu.faturamento.vo.ProtocoloAihVO;
import br.gov.mec.aghu.faturamento.vo.ProtocolosAihsVO;
import br.gov.mec.aghu.faturamento.vo.RateioValoresSadtPorPontosVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOPMNaoFaturadaVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOrtesesProtesesVO;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteServicosVO;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihServicosVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihVO;
import br.gov.mec.aghu.faturamento.vo.RnCapcCboProcResVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDatasVO;
import br.gov.mec.aghu.faturamento.vo.SugestoesDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.TotaisPorDCIHVO;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.faturamento.vo.ValoresAIHPorDCIHVO;
import br.gov.mec.aghu.faturamento.vo.ValoresPreviaVO;
import br.gov.mec.aghu.internacao.business.vo.AtualizarContaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioIntermediarioLancamentosContaVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.model.FatProcedHospInternosPai.Fields;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioMudancaProcedimentosVO;
import br.gov.mec.aghu.sig.custos.vo.AssociacaoProcedimentoVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoCirurgiaVO;
import br.gov.mec.aghu.sig.custos.vo.SigValorReceitaVO;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface IFaturamentoFacade extends Serializable {

	String NOME_JOB_ENCERRAMENTO_AMBULATORIO = "faturamentoFacade.rnFatpExecFatNew";
	String ARQUIVOS_IMPORTACAO_SUS = "arquivos";
	String LOG_FILE_IMPORTACAO_SUS = "log";
	String NOME_ARQUIVO_LOG_PROCEDIMENTOS = "log_imp_proc_";
	String NOME_ARQUIVO_LOG_CID = "log_imp_cid_";
	String NOME_ARQUIVO_LOG_COMPATIBILIDADE = "log_proc_x_compat_";
	String NOME_ARQUIVO_LOG_CBO = "log_imp_cbo_";
	String NOME_ARQUIVO_LOG_GERAL = "log_imp_geral_";
	String EXTENCAO_ARQUIVO_LOG = ".txt";
	String NOME_ARQUIVO_LOG_SERVICO_CLASSIFICACAO = "log_servico_classificacao_";
	String NOME_ARQUIVO_LOG_INSTRUMENTO_REGISTRO = "log_instrumento_registro_";
	
	void persistirMotivoDesdobramento(FatMotivoDesdobramento fatMotivoDesdobramento);

	List<AghClinicas> pesquisarClinicasPorCodigoOuDescricao(Object parametro);

	Long pesquisarClinicasPorCodigoOuDescricaoCount(Object parametro);
	
	List<FatItensProcedHospitalar> pesquisarProcedimentosPorTabelaOuItemOuProcedimentoOuDescricao(Object parametro);

	Long pesquisarProcedimentosPorTabelaOuItemOuProcedimentoOuDescricaoCount(Object parametro);

	void persistirMotivoDesdobramentoClinica(FatMotivoDesdobrClinica fatMotivoDesdobrClinica) throws ApplicationBusinessException;

	void persistirMotivoDesdobramentoProcedimento(FatMotivoDesdobrSsm fatMotivoDesdobrSsm) throws ApplicationBusinessException;
	
	void alterarMotivoDesdobramentoSSM(FatMotivoDesdobrSsm fatMotivoDesdobrSsm);
	
	List<AghClinicas> pesquisarClinicasPorMotivoDesdobramento(Short codigoMotivoDesdobramento);
	
	public List<FatMotivoDesdobrSsm> pesquisarMotivosDesdobramentosSSM(Short seqMotivoDesdobramento);
	
	public void excluirClinicaMotivoDesdobramento(FatMotivoDesdobrClinicaId fatMotivoDesdobrClinicaId);

	void removerCidContaHospitalar(final Integer cthSeq, final Integer cidSeq,
			final DominioPrioridadeCid prioridadeCid,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	VFatContaHospitalarPac obterContaHospitalarPaciente(final Integer seq,
			final Enum[] fetchArgsLeftJoin);

	VFatContaHospitalarPac obterContaHospitalarPaciente(Integer seq);

	FatContasHospitalares obterContaHospitalar(final Integer seq,
			Enum... fetchArgs);

	FatContasHospitalares buscarCthGerada(final Integer cthSeq);

	List<FatItemContaHospitalar> listarIchGerada(final Integer cthSeq);

	List<FatItemContaHospitalar> listarIchDesdobrada(final Integer cthSeq);

	FatItemContaHospitalar obterItemContaHospitalar(
			final FatItemContaHospitalarId id);
	
	List<FatItemContaHospitalar> listarItensContaHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer cthSeq, final Integer procedimentoSeq,
			final Short unfSeq, final Date dtRealizado,
			final DominioSituacaoItenConta situacao,
			final DominioIndOrigemItemContaHospitalar origem,
			final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException;

	//Método criado após a uma solicitação do item #49345
	List<FatItemContaHospitalar> listarItensContaHospitalar(final Integer cthSeq, final Integer procedimentoSeq, final Short unfSeq, final Date dtRealizado,
			final DominioSituacaoItenConta situacao,
			final DominioIndOrigemItemContaHospitalar origem,
			final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException;

	Long listarItensContaHospitalarCount(final Integer cthSeq,
			final Integer procedimentoSeq, final Short unfSeq,
			final Date dtRealizado, final DominioSituacaoItenConta situacao,
			final DominioIndOrigemItemContaHospitalar origem,
			final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException;

	List<FatProcedHospInternos> pesquisarFatProcedHospInternos(
			final Integer codigoMaterial,
			final Integer seqProcedimentoCirurgico,
			final Short seqProcedimentoEspecialDiverso);

	List<SubRelatorioLaudosProcSusVO> buscarJustificativasLaudoProcedimentoSUS(
			final Integer seqAtendimento) throws ApplicationBusinessException;
	
	List<SubRelatorioMudancaProcedimentosVO> buscarMudancaProcedimentos(Integer seqAtendimento, Integer apaSeq, Short seqp);

	void removerContaHospitalar(final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp,
			final Date dataFimVinculoServidor) throws BaseException;

	FatContasHospitalares persistirContaHospitalar(
			final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	void persistirContaHospitalar(final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, final Boolean flush,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void persistirContaHospitalar(final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, final Boolean flush,
			final Boolean alterarEspecialidade, String nomeMicrocomputador, RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException;

	FatProcedHospInternos obterProcedimentoHospitalarInterno(final Integer seq);

	void buscaCountQtdCids(final Integer cthSeq, final Integer cidSeq)
			throws ApplicationBusinessException;

	void inserirFatProcedHospInternos(
			final FatProcedHospInternos fatProcedHospInternos)
			throws ApplicationBusinessException;

	List<FatProcedHospInternos> listarFatProcedHospInternosPorMaterial(
			final ScoMaterial material);

	List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedimentoCirurgicos(
			final MbcProcedimentoCirurgicos procedimentoCirurgico);

	List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedEspecialDiversos(
			final MpmProcedEspecialDiversos procedimentoEspecialDiverso);

	List<String> buscaProcedimentoHospitalarInternoAgrupa(final Integer phiSeq,
			final Short cnvCodigo, final Byte cspSeq, final Short phoSeq,
			final Short tipoGrupoContaSUS);

	List<Long> buscaDescricaoProcedimentoHospitalarInterno(
			final Integer phiSeq, final Short cnvCodigo, final Byte cspSeq,
			final Short phoSeq, final Short tipoGrupoContaSUS);

	Boolean verificarFatConvGrupoItensProcedExigeJustificativa(
			final FatProcedHospInternos fatProcedHospInternos,
			final FatConvenioSaudePlano convenioSaudePlano);

	List<FatConvGrupoItemProced> listarFatConvGrupoItensProced(
			final Short pIphPhoSeq, final Integer pIphSeq,
			final Short pCnvCodigo, final Byte pCnvCspSeq,
			final Short pCpgCphPhoSeq, final Short pCpgGrcSeq,
			final Integer phiSeq);

	FatTipoAih obterTipoAih(final Byte seq);

	List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitado(
			final Integer phiSeq) throws BaseException;

	List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSRealizado(
			final Integer phiSeq, final Integer cthSeq) throws BaseException;

	List<FatConvenioSaudePlano> pesquisarPlanoPorConvenioSaude(
			final Short cnvCodigo);

	String buscaSitSms(final Integer pCthSeq);

	List<FatCidContaHospitalar> pesquisarCidContaHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer cthSeq);

	FatConvenioSaudePlano obterConvenioSaudePlanoPeloAtendimentoDaConsulta(
			Integer atdSeq);

	List<FatCidContaHospitalar> pesquisarCidContaHospitalar(final Integer cthSeq);

	FatConvenioSaude obterConvenioSaudeComPagador(Short codigoConvenio);

	List<FatPeriodosEmissao> pesquisarPeriodosEmissaoConvenioSaudePlano(
			FatConvenioSaudePlano convenioSaudePlano);

	List<FatConvTipoDocumentos> pesquisarConvTipoDocumentoConvenioSaudePlano(
			FatConvenioSaudePlano convenioSaudePlano);

	List<FatConvPlanoAcomodacoes> pesquisarConvPlanoAcomodocaoConvenioSaudePlano(
			FatConvenioSaudePlano convenioSaudePlano);

	Long pesquisarCidContaHospitalarCount(final Integer cthSeq);

	void validarConvenioPlanoAcomodacaoAntesInserir(
			final FatConvPlanoAcomodacoes convPlanoAcomodacao)
			throws ApplicationBusinessException;

	void validarConvenioTipoDocumentoAntesInserir(
			final FatConvTipoDocumentos convTipoDocumento)
			throws ApplicationBusinessException;

	List<FatConvGrupoItemProced> pesquisarConvenioGrupoItemProcedimento(
			final Integer iphSeq, final Short iphPhoSeq)
			throws ApplicationBusinessException;

	void persistirCidContaHospitalar(
			final FatCidContaHospitalar cidContaHospitalar,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void inserirCidContaHospitalar(
			final FatCidContaHospitalar cidContaHospitalar,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	List<FatProcedHospIntCid> listarFatProcedHospIntCidPorPhiSeqValidade(
			Integer phiSeq, DominioTipoPlano validade, String filtro);

	void inserirContaInternacao(final FatContasInternacao contaInternacao,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;
	
	void inserirContaInternacaoConvenio(
			final FatContasInternacao contaInternacao,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;
			
	void buscarSangue(final Integer seqContaInternacao)
			throws ApplicationBusinessException;

	void incluiMateriais(final Integer cthSeq, Date dataFimVinculoServidor)
			throws BaseException;

	void agendarEncerramentoAutomaticoContaHospitalar(
			Date date, final String cron, final String nomeMicrocomputador, final RapServidores servidorLogado, final String nomeProcessoQuartz);
	
	FatTipoAih obterItemAihPorCodigoSus(final Short codigoSus);

	List<FatContasInternacao> obterContaInternacaoNaoManuseada(
			final Integer seqInternacao);

	List<FatContasInternacao> pesquisarContaInternacaoPorInternacao(
			final Integer seqInternacao);

	FatItensProcedHospitalar obterItemProcedHospitalar(final Short phoSeq,
			final Integer seq);

	void atualizarFatProcedHospInternos(
			final FatProcedHospInternos fatProcedHospInternos)
			throws ApplicationBusinessException;

	void deleteFatProcedHospInternos(final ScoMaterial matCodigo,
			final MbcProcedimentoCirurgicos pciSeq,
			final MpmProcedEspecialDiversos pedSeq, final String csaCodigo,
			final String pheCodigo, final Short euuSeq, final Integer cduSeq,
			final Short cuiSeq, final Integer tidSeq);

	/**
	 * Busca uma lista de VFatContaHospitalarPac pelo filtro e situação A, F ou
	 * E
	 */
	List<VFatContaHospitalarPac> pesquisarAbertaFechadaEncerrada(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq);

	/**
	 * Busca uma lista de VFatContaHospitalarPac pelo filtro e situação A e F
	 * ordenados pela data de alta administrativa
	 */
	List<VFatContaHospitalarPac> pesquisarAbertaOuFechadaOrdenadaPorDtIntAdm(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer pacProntuario, final Integer cthSeq,
			final Integer codigo, final DominioSituacaoConta situacao);

	List<FatContasHospitalares> pesquisaContaHospitalarParaNotaConsumoDaCirurgia(
			Integer seqAtendimento);

	/**
	 * Faz um count em VFatContaHospitalarPac pelo filtro e
	 */
	Long pesquisarAbertaOuFechadaCount(final Integer pacProntuario,
			final Integer cthSeq, final Integer codigo,
			final DominioSituacaoConta situacao);

	List<VFatContaHospitalarPac> pesquisarPorPacCodigo(final Integer pacCodigo);

	Long pesquisarPorPacCodigoCount(final Integer pacCodigo);

	/**
	 * Busca o número de elementos da lista de VFatContaHospitalarPac pelo
	 * filtro e situação A, F ou E
	 */
	Long pesquisarAbertaFechadaEncerradaCount(final Integer pacProntuario,
			final Long cthNroAih, final Integer pacCodigo, final Integer cthSeq);

	/**
	 * Busca o paciente da lista de VFatContaHospitalarPac pelo filtro e
	 * situação A, F ou E
	 */
	AipPacientes pesquisarAbertaFechadaEncerradaPaciente(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq);

	/**
	 * Busca situação do gestor
	 * 
	 * @return
	 */
	String situacaoSMS(final FatContasHospitalares contaHospitalar);

	/**
	 * Busca leito da ultima internação
	 * 
	 * @param contaHospitalar
	 * @return
	 */
	String buscaLeito(final FatContasHospitalares contaHospitalar);

	/**
	 * Busca o Procedimento SSM
	 */
	String buscaSSM(final Integer pCthSeq, final Short pCspCnvCodigo,
			final Byte pCspSeq, final DominioSituacaoSSM situacaoSSM);

	String obterMensagemResourceBundle(String key);

	/**
	 * Busca a complexidade do procedimento SSM
	 * 
	 */
	String buscaSsmComplexidade(final Integer pCthSeq,
			final Short pCspCnvCodigo, final Byte pCspSeq,
			final Integer pCthPhiSeq, final Integer pCthPhiSeqRealizado,
			final DominioSituacaoSSM situacaoSSM);

	/**
	 * Pesquisa a conta hospitalar
	 */
	List<VFatContaHospitalarPac> pesquisarContaHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer prontuario, final Integer contaHospitalar,
			final String codigoDcih, final Long numeroAih,
			final Date competencia, final Integer codigo,
			final DominioSituacaoConta[] situacoes);

	/**
	 * Pesquisa conta hospitalar count (retorna nro registros)
	 */
	Long pesquisarContaHospitalarCount(final Integer prontuario,
			final Integer contaHospitalar, final String codigoDcih,
			final Long numeroAih, final Date competencia, final Integer codigo);

	void removerItemContaHospitalar(final FatItemContaHospitalar itemCtaHosp)
			throws BaseException;

	void persistirItemContaHospitalar(
			final FatItemContaHospitalar newItemCtaHosp,
			final FatItemContaHospitalar oldItemCtaHosp, RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException;

	void persistirItemContaHospitalar(
			final FatItemContaHospitalar newItemCtaHosp,
			final FatItemContaHospitalar oldItemCtaHosp, final boolean flush,
			RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException;

	/**
	 * Método para buscar Cids através de sua descrição ou código
	 */
	List<AghCid> pesquisarCidsPorDescricaoOuCodigo(final String descricao,
			final Integer limiteRegistros);

	List<AghCid> pesquisarCidsPorDescricaoOuCodigo(final String descricao,
			final Integer limiteRegistros, final String order);

	/**
	 * Método para buscar número de registros do Cid através de sua descrição ou
	 * código
	 */
	Long pesquisarCidsPorDescricaoOuCodigoCount(final String descricao);

	List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitado(
			final Object objPesquisa, final Integer phiSeq)
			throws BaseException;

	Long listarAssociacaoProcedimentoSUSSolicitadoCount(final Object objPesquisa)
			throws BaseException;

	List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitadoPorPHI(
			final Object objPesquisa, final Integer phiSeq)
			throws BaseException;

	Long listarAssociacaoProcedimentoSUSSolicitadoPorPHICount(
			final Object objPesquisa) throws BaseException;

	List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSRealizado(
			final Object objPesquisa, final Integer phiSeq, final Integer cthSeq)
			throws BaseException;

	Long listarAssociacaoProcedimentoSUSRealizadoCount(
			final Object objPesquisa, final Integer phiSeq, final Integer cthSeq)
			throws BaseException;

	List<FatSituacaoSaidaPaciente> listarSituacaoSaidaPaciente(
			final Object objPesquisa, final Short mspSeq);

	Long listarSituacaoSaidaPacienteCount(final Object objPesquisa,
			final Short mspSeq);

	List<FatMotivoSaidaPaciente> listarMotivoSaidaPaciente(
			final Object objPesquisa);

	Long listarMotivoSaidaPacienteCount(final Object objPesquisa);

	FatMotivoSaidaPaciente obterMotivoSaidaPacientePorChavePrimaria(Short seq);

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	FatContasHospitalares clonarContaHospitalar(
			final FatContasHospitalares contaHospitalar) throws Exception;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	FatItemContaHospitalar clonarItemContaHospitalar(
			final FatItemContaHospitalar itemContaHospitalar) throws Exception;

	boolean verificaDiariaAcompanhante(
			final FatItensProcedHospitalar itemProcedHospitalar);

	void persistirItemProcedimentoHospitalarComFlush(
			final FatItensProcedHospitalar newIph,
			final FatItensProcedHospitalar oldIph) throws BaseException;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	FatItensProcedHospitalar clonarItemProcedimentoHospitalar(
			final FatItensProcedHospitalar iph) throws Exception;

	List<FatCaractItemProcHosp> listarCaractItemProcHospPorSeqEPhoSeq(
			final Short iphPhoSeq, final Integer iphSeq);

	List<FatCaractItemProcHosp> listarCaractItemProcHospPorPhoSeqECodTabela(
			final Short iphPhoSeq, final Long codTabela);

	List<FatTipoCaractItens> listarTiposCaracteristicasParaItens(
			final Object objPesquisa);

	Long listarTiposCaracteristicasParaItensCount(final Object objPesquisa);

	FatTipoCaractItens obterTipoCaracteristicaItemPorChavePrimaria(
			final Integer tctSeq);

	List<FatTipoCaractItens> listarTipoCaractItensPorCaracteristica(
			final String caracteristica);

	FatItensProcedHospitalar obterItemProcedHospitalarPorChavePrimaria(
			final FatItensProcedHospitalarId id);

	void persistirCaractItemProcedimentoHospitalar(
			final FatCaractItemProcHosp caractItemProcHosp,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException;

	void removerCaractItemProcedimentoHospitalar(
			final FatCaractItemProcHosp caractItemProcHosp);

	List<FatItensProcedHospitalar> listarFatItensProcedHospitalarTabFatPadrao(
			final Object obj) throws BaseException;

	Long listarFatItensProcedHospitalarTabFatPadraoCount(final Object obj)
			throws BaseException;

	List<FatItensProcedHospitalar> listarIPHPorConvenioSaudePlanoConvProcedHosp(
			final Object objPesquisa) throws BaseException;

	Long listarIPHPorConvenioSaudePlanoConvProcedHospCount(
			final Object objPesquisa) throws BaseException;

	List<ItemProcedimentoHospitalarVO> obterProcedimentosMedicoAudtAIH(
			final Integer cthSeq);

	List<ItemProcedimentoHospitalarVO> obterProcedimentosAtosMedicosAIH(
			final Integer cthSeq) throws ApplicationBusinessException;

	List<FatItensProcedHospitalar> listarItensProcedHospTabPadraoPlanoInt(
			final Object objPesquisa) throws BaseException;

	Long listarItensProcedHospTabPadraoPlanoIntCount(final Object objPesquisa)
			throws BaseException;

	List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final String descricao, final Short seq, final Long codTabela)
			throws BaseException;

	List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final String descricao, final Integer seq, final Long codTabela,
			final Short phoSeq) throws BaseException;

	List<FatItensProcedHospitalar> listarFatItensProcedHospitalarPorPhoSeqECodTabela(
			final Short phoSeq, final Object codTabela,
			final Integer limiteRegistros);

	Long listarFatItensProcedHospitalarPorPhoSeqESeqCount(final Short phoSeq,
			final Object seq);

	FatProcedimentosHospitalares obterProcedimentoHospitalar(final Short seq);

	Long listarFatItensProcedHospitalarCount(final String descricao,
			final Short seq, final Long codTabela);

	Long listarFatItensProcedHospitalarCount(final String descricao,
			final Integer seq, final Long codTabela, final Short phoSeq);

	List<FatProcedimentosHospitalares> listarProcedimentosHospitalar(
			final Object param) throws BaseException;

	Long listarProcedimentosHospitalarCount(final Object param)
			throws BaseException;

	List<FatProcedimentosHospitalares> listarProcedimentosHospitalaresPorSeqEDescricao(
			final Object objPesquisa);

	Long listarProcedimentosHospitalaresPorSeqEDescricaoCount(
			final Object objPesquisa);

	List<FatMotivoCobrancaApac> pesquisarMotivoCobrancaApac(
			final Object objPesquisa);

	Long pesquisarMotivoCobrancaApacCount(final Object objPesquisa);

	List<DemonstrativoFaturamentoInternacaoVO> pesquisarEspelhoAih(
			final Integer prontuario, final Date dtInternacaoAdm)
			throws ApplicationBusinessException;

	List<AihFaturadaVO> listarEspelhoAihFaturada(final Integer cthSeq,
			final Integer prontuario, final Integer mes, final Integer ano,
			final Date dthrInicio, final Long codTabelaIni,
			final Long codTabelaFim, final String iniciais,
			final Boolean reapresentada);

	List<ProcedimentoNaoFaturadoVO> pesquisarEspelhoAihProcedimentosNaoFaturados(
			final Long iphCodTabela, final Long iphCodSusRealiz,
			final Short espSeq, final Integer mes, final Integer ano,
			final Date dthrInicio,
			final DominioGrupoProcedimento grupoProcedimento,
			final String iniciais, final Boolean reapresentada)
			throws ApplicationBusinessException;

	List<PendenciaEncerramentoVO> pesquisarMensagensErro(final Date dtInicial,
			final Date dtFinal, final DominioSituacaoMensagemLog grupo,
			final String erro, final String nome, final Boolean reapresentada);

	FatProcedimentosHospitalares obterFatProcedimentosHospitalaresPadrao();

	List<FatTipoAto> pesquisarTipoAto(final Object objPesquisa);

	Long pesquisarTipoAtoCount(final Object objPesquisa);

	List<FatTiposVinculo> pesquisarTipoVinculo(final Object objPesquisa);

	Long pesquisarTipoVinculoCount(final Object objPesquisa);

	/**
	 * Pesquisa FatDocumentoCobrancaAihs por código
	 * 
	 * @param codigoDcih
	 * @return
	 */
	FatDocumentoCobrancaAihs pesquisarFatDocumentoCobrancaAihsPorCodigoDcih(
			final String codigoDcih);

	Boolean existeFaturamentoComPagador(AacPagador aacPagador);

	List<FatFormaOrganizacao> listarFormasOrganizacaoPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq, final Byte subGrupo);

	Long listarFormasOrganizacaoPorCodigoOuDescricaoCount(
			final Object objPesquisa, final Short grpSeq, final Byte subGrupo);

	List<FatSubGrupo> listarSubGruposPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq);

	Long listarSubGruposPorCodigoOuDescricaoCount(final Object objPesquisa,
			final Short grpSeq);

	List<FatGrupo> listarGruposPorCodigoOuDescricao(final Object objPesquisa);

	Long listarGruposPorCodigoOuDescricaoCount(final Object objPesquisa);

	Long listarFatAtoMedicoEspelhoCount(final Integer cthSeq);

	List<FatLogError> pesquisarFatLogError(final Integer cthSeq,
			final Integer pacCodigo, final Short ichSeqp, final String erro,
			final Integer phiSeqItem1, final Long codItemSus1,
			final DominioModuloCompetencia modulo, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc);

	FatLogError obterFatLogError(final Integer seq);

	FatLogErrorVO obterFatLogErrorVo(final Short iphPhoSeqItem1,
			final Integer iphSeqItem1, final Short iphPhoSeqItem2,
			final Integer iphSeqItem2, final Short iphPhoSeqRealizado,
			final Integer iphSeqRealizado, final Short iphPhoSeq,
			final Integer iphSeq);

	Long pesquisarFatLogErrorCount(final Integer cthSeq,
			final Integer pacCodigo, final Short ichSeqp, final String erro,
			final Integer phiSeq1, final Long itemSMM1,
			final DominioModuloCompetencia modulo);

	FatCbos obterFatCboPorCodigoVigente(final String codigo)
			throws ApplicationBusinessException;

	Map<String, Object> atualizarItensProcedHosp(
			final List<String> lista) throws BaseException;

	Map<String, Object> atualizarCboProcedimento(final List<String> lista)
			throws BaseException;

	Map<String, Object> atualizarGeral(final List<String> lista)
			throws BaseException;

	void verificaNomeArquivoZip(final String arquivo)
			throws ApplicationBusinessException;

	void converterContaEmSemCobertura(final Integer cthSeqSelected,
			final DominioSituacaoConta situacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	void faturarContaUmDia(final Integer cthSeqSelected,
			final DominioSituacaoConta situacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	Boolean fatcVerCaractPhiQrInt(final Short cnvCodigo, final Byte cspSeq,
			final Integer phiSeq, final String caracteristica)
			throws BaseException;

	List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSItem(
			final Object objPesquisa, final Integer phiSeq,
			final Integer cthSeq, final Boolean isProcHospSolictado)
			throws BaseException;

	Long listarAssociacaoProcedimentoSUSItemCount(final Object objPesquisa,
			final Integer phiSeq, final Integer cthSeq,
			final Boolean isProcHospSolictado) throws BaseException;

	Boolean rnCthcAtuGeraEsp(final Integer pCthSeq, final Boolean pPrevia,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, boolean refresh)
			throws BaseException;

	RnCthcVerDatasVO rnCthcVerDatas(final Integer pIntSeq,
			final Date pDataNova, final Date pDataAnterior,
			final String pTipoData, final Date dataFimVinculoServidor)
			throws BaseException;

	void rnCthpAtuDiarUti(final Integer pCthSeq, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	void rnCthpTrocaCnv(final Integer pIntSeq, final Date pDtInt,
			final Short pCnvOld, final Byte pCspOld, final Short pCnvNew,
			final Byte pCspNew, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	Boolean validarReaberturaContaHospitalar(final Integer cthSeqSelected,
			final DominioSituacaoConta situacao) throws BaseException;

	Boolean reabrirContaHospitalar(final Integer cthSeqSelected,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	List<Integer> getContasParaReaberturaEmLote(
			final FatCompetencia competencia, final Date dtInicial,
			final Date dtFinal, final Long procedimentoSUS)
			throws BaseException;

	boolean reabrirContasHospitalares(final Integer cthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void gerarCompetenciaEmManutencao(final FatCompetencia competencia,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	Boolean encerrarCompetenciaAtualEAbreNovaCompetencia(
			final FatCompetencia fatCompetencia, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	Long listarFatEspelhoAihCount(final Integer cthSeq);

	FatVlrItemProcedHospComps persistirFatVlrItemProcedHospComps(
			final FatVlrItemProcedHospComps fatVlrItemProcedHospComps,
			final Integer seq, final Short phoSeq) throws BaseException;

	List<FatVlrItemProcedHospComps> obterListaValorItemProcHospComp(
			final Short phoSeq, final Integer iphSeq);

	void setTimeout(final Integer timeout) throws ApplicationBusinessException;

	void commit(final Integer timeout) throws ApplicationBusinessException;

	void commit() throws ApplicationBusinessException;

	void clear();

	void clearSemFlush();

	void evict(BaseEntity entity);

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	FatCompetencia clonarFatCompetencia(final FatCompetencia competencia)
			throws Exception;

	List<FatCompetencia> obterCompetenciasPorModuloESituacoes(
			final DominioModuloCompetencia modulo,
			final DominioSituacaoCompetencia... situacoes);

	List<FatProcedimentoCbo> listarProcedimentoCboPorIphSeqEPhoSeq(
			final Integer iphSeq, final Short iphPhoSeq);

	List<FatProcedimentoCbo> listarProcedimentoCboPorCbo(final String codigoCbo);

	FatProcedimentoCbo obterProcedimentoCboPorChavePrimaria(final Integer seq);

	void inserirProcedimentoCbo(final FatProcedimentoCbo procCbo);

	void removerProcedimentoCbo(final FatProcedimentoCbo procCbo);

	List<FatCbos> listarCbos(final Object objPesquisa)
			throws ApplicationBusinessException;

	Long listarCbosCount(final Object objPesquisa);

	List<FatCbos> listarCbosAtivos(final Object objPesquisa)
			throws ApplicationBusinessException;

	Long listarCbosAtivosCount(final Object objPesquisa);	
	
	List<FatProcedimentosHospitalares> listarProcedimentosHospitalaresPorSeqEDescricaoOrdenado(
			final Object param, final String ordem);

	void inserirAtoMedicoAih(final FatAtoMedicoAih atoMedAih,
			final boolean comFlush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	void atualizarAtoMedicoAih(final FatAtoMedicoAih atoMedAih,
			final boolean comFlush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	FatValorContaHospitalar inserirFatValorContaHospitalar(
			final FatValorContaHospitalar fatValorContaHospitalar,
			final boolean flush);

	FatValorContaHospitalar atualizarFatValorContaHospitalar(
			final FatValorContaHospitalar fatValorContaHospitalar,
			final boolean flush);

	FatEspelhoItemContaHosp atualizarFatEspelhoItemContaHosp(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp);

	FatEspelhoItemContaHosp inserirFatEspelhoItemContaHospSemFlush(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp);

	/**
	 * Atualiza um FatDocumentoCobrancaAihs executando suas respectivas triggers
	 */
	FatDocumentoCobrancaAihs atualizarFatDocumentoCobrancaAihs(
			final FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs,
			final FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs)
			throws ApplicationBusinessException;

	/**
	 * Atualiza um AIH executando suas respectivas triggers
	 */
	void atualizarFatAih(final FatAih fatAih, final FatAih oldFatAih)
			throws ApplicationBusinessException;

	void atualizarSituacaoFatAih(final FatAih fatAih,
			final DominioSituacaoAih indSituacao) throws BaseException;

	void inserirFatEspelhoAih(final FatEspelhoAih fatEspelhoAih,
			final boolean flush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	void atualizarFatEspelhoAih(final FatEspelhoAih fatEspelhoAih,
			final boolean flush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;
		
	void atualizarFatEspelhoAih(final FatEspelhoAih fatEspelhoAih) throws BaseException;

	/**
	 * Persiste um objeto logError
	 */
	void persistirLogError(final FatLogError fle);

	void persistirLogError(final FatLogError fle, final boolean flush);

	void atualizarFaturamentoSolicitacaoExames(
			final AelSolicitacaoExames modificada,
			final AelSolicitacaoExames original, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException;

	/**
	 * Insere um PerdaItemConta executando suas respectivas triggers
	 */
	void inserirFatPerdaItemConta(final FatPerdaItemConta fatPerdaItemConta,
			final boolean flush) throws BaseException;

	void validarCamposFatVlrItemProcedHospComps(
			final FatVlrItemProcedHospComps vlrItemProcedHospComps,
			final FatVlrItemProcedHospComps vigente)
			throws ApplicationBusinessException;

	void inserirFatCampoMedicoAuditAih(
			final FatCampoMedicoAuditAih campoMedicoAuditAih,
			final boolean comFlush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	List<FatCompatExclusItem> listarFatCompatExclusItem(final Short phoSeq,
			final Integer seq, final DominioSituacao indSituacao);

	void persistirFatCompatExclusItem(
			final List<FatCompatExclusItem> newListaFatCompatExclusItem,
			final List<FatCompatExclusItem> oldListaFatCompatExclusItem,
			final List<FatCompatExclusItem> excluidosListaFatCompatExclusItem)
			throws BaseException;

	void persistirFatProcedHospIntCid(
			final FatProcedHospIntCid fatProcedHospIntCid, final Boolean flush)
			throws BaseException;

	void removerFatProcedHospIntCid(final Integer phiSeq, final Integer cidSeq,
			final Boolean flush) throws BaseException;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	List<FatCompatExclusItem> clonarFatCompatExclusItem(
			final List<FatCompatExclusItem> listaOriginal) throws Exception;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	FatConvGrupoItemProced clonarGrupoItemConvenio(
			final FatConvGrupoItemProced elemento) throws Exception;

	List<FatProcedHospInternos> listarPhisAtivosPorSeqEDescricao(
			final Object objPesquisa);

	Long listarPhisAtivosPorSeqEDescricaoCount(final Object objPesquisa);

	List<VFatConvPlanoGrupoProcedVO> listarTabelas(final Object objPesquisa);

	Long listarTabelasCount(final Object objPesquisa);

	List<VFatConvPlanoGrupoProcedVO> listarConvenios(final Object objPesquisa,
			final Short grcSeq, final Short cphPhoSeq);

	Long listarConveniosCount(final Object objPesquisa, final Short grcSeq,
			final Short cphPhoSeq);

	List<VFatConvPlanoGrupoProcedVO> listarPlanos(final Object objPesquisa,
			final Short grcSeq, final Short cphPhoSeq,
			final Short cphCspCnvCodigo);

	Long listarPlanosCount(final Object objPesquisa, final Short grcSeq,
			final Short cphPhoSeq, final Short cphCspCnvCodigo);

	List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(
			final Object objPesquisa, final Short phoSeq, final String order);

	List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(
			final Object objPesquisa, final Short phoSeq);

	Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(
			final Object objPesquisa, final Short phoSeq);

	List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeq(
			final Object objPesquisa, final Short phoSeq);

	Long listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqCount(
			final Object objPesquisa, final Short phoSeq);

	List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(
			final Object objPesquisa, final Short phoSeq);

	Long listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProteseCount(
			final Object objPesquisa, final Short phoSeq);

	List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(
			final Object objPesquisa);

	Long listarFatItensProcedHospitalarCount(final Object objPesquisa);

	List<FatTipoTransplante> listarTodosOsTiposTransplante();

	void persistirExcCnvGrpItemProc(
			final FatExcCnvGrpItemProc excCnvGrpItemProcNew);

	void removerExcCnvGrpItemProc(final FatExcCnvGrpItemProc excCnvGrpItemProc);

	List<FatExcCnvGrpItemProc> listarFatExcCnvGrpItemProcPorPlanoConvenioProcedInternoETabela(
			final Integer phiSeq, final Short phoSeq,
			final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq);

	Boolean buscaInstrRegistro(final Integer iphSeq, final Short iphPhoSeq,
			final String codRegistro);

	String buscaProcedimentoPrConta(final Integer seq, final Short phoSeq,
			final Integer eaiCthSeq, final Long codTabela);

	List<ResumoCobrancaAihServicosVO> listarAtosMedicos(final Integer seq,
			final Integer cthSeq);

	List<ResumoCobrancaAihServicosVO> listarAtosMedicosPrevia(
			final Integer seq, final Integer cthSeq);

	List<ResumoCobrancaAihVO> gerarRelatorioResumoCobrancaAih(
			final Integer cthSeq, final Boolean previa);

	List<ResumoCobrancaAihVO> pesquisarEspelhoAih(final Integer cthSeq,
			final Boolean previa);

	FatCaractFinanciamento obterCaractFinanciamentoPorSeqEPhoSeqECodTabela(
			final Short iphPhoSeq, final Integer iphSeq, final Long codTabela);

	List<FatCaractFinanciamento> listarFinanciamentosAtivosPorCodigoOuDescricao(
			Object objPesquisa);

	FatCaractComplexidade obterCaractComplexidadePorSeqEPhoSeqECodTabela(
			final Short iphPhoSeq, final Integer iphSeq, final Long codTabela);

	List<FatCaractComplexidade> listarComplexidadesAtivasPorCodigoOuDescricao(
			Object objPesquisa);

	/**
	 * Exclui os procedimentos ambulatoriais realizados para o atendimento.
	 */
	void excluirProcedimentosAmbulatoriaisRealizadosPorAtendimento(
			final AghAtendimentos atendimento);

	void atualizarProcedimentoAmbulatorialRealizado(
			final FatProcedAmbRealizado procedAmbRealizado,
			final FatProcedAmbRealizado oldProcedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void excluirProcedimentoAmbulatorialRealizado(
			final FatProcedAmbRealizado procedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	FatProcedAmbRealizado clonarFatProcedAmbRealizado(
			final FatProcedAmbRealizado procedAmbRealizado)
			throws BaseException;

	void persistirProcedimentoAmbulatorialRealizado(
			final FatProcedAmbRealizado procedAmbRealizado,
			final FatProcedAmbRealizado oldProcedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	FatProcedHospInternos obterFatProcedHospInternosPorExamesMaterialAnalise(
			final AelExamesMaterialAnaliseId exaManId);

	List<FatContasInternacao> obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
			final Integer intSeq, final Integer pacCodigo,
			final Date dthrSolicitacao, final Short cspCnvCodigo);

	List<FatContasInternacao> obterContasHospitalaresPorInternacaoPacienteDataSolicitacaoComExamesEspeciais(
			final Integer intSeq, final Integer pacCodigo,
			final Date dthrSolicitacao, final Short cspCnvCodigo);

	List<FatItemContaHospitalar> obterItensContaHospitalarPorContaHospitalarItemSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp, final Integer chtSeq);

	List<FatItemContaHospitalar> obterItensContaHospitalarPorSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp);

	Short obterProximoSeqFatItensContaHospitalar(final Integer cthSeq);

	FatCompetencia buscarCompetenciasDataHoraFimNula(
			final DominioModuloCompetencia modulo,
			final DominioSituacaoCompetencia situacao);

	List<FatProcedAmbRealizado> buscarFatProcedAmbRealizadoPorProcedHospInternosEItemSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp, final Integer phiSeq);

	boolean existeFatProcedAmbRealizadoTransferida(final Short iseSeqp,
			final Integer iseSoeSeqp, final Integer phiSeq);

	List<FatProcedAmbRealizado> buscarFatProcedAmbRealizadoPorItemSolicitacaoExameNaoFaturados(
			final Short iseSeqp, final Integer iseSoeSeqp);

	List<FatProcedAmbRealizado> buscarFatProcedAmbRealizadoPorItemSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp);

	FatConvenioSaudePlano obterFatConvenioSaudePlano(final Short cnvCodigo,
			final Byte seq);

	List<VFatContaHospitalarPac> pesquisarVFatContaHospitalarPac(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc);

	Long pesquisarVFatContaHospitalarPacCount(final Integer pacProntuario,
			final Long cthNroAih, final Integer pacCodigo,
			final Integer cthSeq, final DominioSituacaoConta[] situacoes);

	/**
	 * pesquisarContaHospitalarParaCobrancaSemInternacao
	 */
	List<FatDadosContaSemIntVO> pesquisarContaHospitalarParaCobrancaSemInternacao(
			final Integer pacProntuario, final Integer pacCodigo,
			final Integer cthSeq, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc);

	/**
	 * pesquisarContaHospitalarParaCobrancaSemInternacaoCount
	 */
	Long pesquisarContaHospitalarParaCobrancaSemInternacaoCount(
			final Integer pacProntuario, final Integer pacCodigo,
			final Integer cthSeq);

	List<ContaHospitalarInformarSolicitadoVO> pesquisarContaHospitalarInformarSolicitadoVO(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final Byte seqTipoAih,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc);

	Long pesquisarVFatContaHospitalarPacCount(final Integer pacProntuario,
			final Long cthNroAih, final Integer pacCodigo,
			final Integer cthSeq, final DominioSituacaoConta[] situacoes,
			final String codigoDcih, final Date dataInternacaoAdm,
			final Date dataAltaAdm,
			final VFatAssociacaoProcedimento procedimentoSolicitado,
			final VFatAssociacaoProcedimento procedimentoRealizado);

	List<VFatContaHospitalarPac> pesquisarVFatContaHospitalarPac(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final String codigoDcih,
			final Date dataInternacaoAdm, final Date dataAltaAdm,
			final VFatAssociacaoProcedimento procedimentoSolicitado,
			final VFatAssociacaoProcedimento procedimentoRealizado,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc);

	Long pesquisarContaHospitalarInformarSolicitadoVOCount(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final Byte seqTipoAih);

	void atualizarProcedimentosAmbulatoriaisRealizados(
			final FatProcedAmbRealizado entidade,
			final FatProcedAmbRealizado oldProcedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	void inserirProcedimentosAmbulatoriaisRealizados(
			final FatProcedAmbRealizado entidade, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException;

	void removerProcedimentosAmbulatoriaisRealizados(
			final FatProcedAmbRealizado entidade, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException;

	ArquivoURINomeQtdVO gerarArquivoContasPeriodo() throws BaseException;

	List<FatMotivoDesdobramento> listarMotivosDesdobramentos(
			final String filtro, final Byte seqTipoAih);

	Long listarMotivosDesdobramentosCount(final String filtro,
			final Byte seqTipoAih);

	void desdobrarContaHospitalar(final Integer cthSeq,
			final FatMotivoDesdobramento motivoDesdobramento,
			final Date dataHoraDesdobramento,
			final Boolean contaConsideradaReapresentada,
			final String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void estornarDesdobramento(final Integer cthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	Long consultaQuantidadeProcedimentosConsultaPorPhiSeq(final Integer phiSeq)
			throws BaseException;

	List<FatProcedHospInternos> listarProcedHospInternoPorSeqOuDescricao(
			final Object param, final Integer maxResults, final String order);

	Long listarProcedHospInternoPorSeqOuDescricaoCount(final Object param);

	List<FatMensagemLog> listarMensagensErro(final Object objPesquisa);

	Long listarMensagensErroCount(final Object objPesquisa);

	List<FatMensagemLogId> listarMensagensErro(final Object objPesquisa,
			final DominioModuloMensagem modulo);

	Long listarMensagensErroCount(final Object objPesquisa,
			final DominioModuloMensagem modulo);

	List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidPorPhiSeq(
			final Integer phiSeq);

	FatProcedHospIntCid obterFatProcedHospIntCidPorChavePrimaria(
			final FatProcedHospIntCidId id);

	VFatContasHospPacientes obterVFatContasHospPacientes(final Integer cthSeq,
			final BigDecimal intSeq);

	VFatContaHospitalarPac obterVFatContaHospitalarPac(final Integer cthSeq);

	List<ListarContasHospPacientesPorPacCodigoVO> listarContasHospPacientesPorPacCodigo(
			final Integer pacCodigo, final Short tipoGrupoContaSUS,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) throws BaseException;

	Long listarContasHospPacientesPorPacCodigoCount(final Integer pacCodigo)
			throws BaseException;

	List<RapServidores> pesquisarAuditores(final String filtro)
			throws ApplicationBusinessException;

	Long pesquisarAuditoresCount(final String filtro)
			throws ApplicationBusinessException;

	List<VFatAssociacaoProcedimento> pesquisarAssociacoesProcedimentos(
			final String filtro, final Integer phiSeq,
			final Boolean indInternacaoIph, final Short cpgCphCspCnvCodigo,
			final Byte cpgCphCspSeq, final DominioSituacao indSituacaoPhi,
			final DominioSituacao indSituacaoIph,
			final Short tipoGrupoContaSUS, final Short iphPhoSeq);

	Long pesquisarAssociacoesProcedimentosCount(final String filtro,
			final Integer phiSeq, final Boolean indInternacaoIph,
			final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq,
			final DominioSituacao indSituacaoPhi,
			final DominioSituacao indSituacaoIph,
			final Short tipoGrupoContaSUS, final Short iphPhoSeq);

	List<Date> listarDatasTransplantes(final Integer pacCodigo);

	void informarSolicitado(final VFatContasHospPacientes contaHospitalar,
			final VFatAssociacaoProcedimento procedimentoSolicitado,
			final Long numeroAIH, final Date dataHoraEmissao,
			final RapServidores medicoAuditor,
			final String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	DominioMensagemEstornoAIH validaEstornoAIH(final Integer seq,
			final Long nrAIH) throws ApplicationBusinessException;

	void estornarAIH(final Integer seq, final Long nrAIH,
			final Boolean reaproveitarAIH, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	RapServidores obterMedicoAuditor(final RapServidores servidor)
			throws ApplicationBusinessException;

	FatContasInternacao obterFatContasInternacaoPorChavePrimaria(
			final Integer seq, Enum... fields);

	List<FatContasInternacao> listarContasInternacao(final Integer cthSeq);

	Long validarInformarSolicitado(final DominioGrupoConvenio grupoConvenio,
			final Integer cthSeq, final Integer prontuario,
			final Integer intSeq, final DominioSituacaoConta[] situacoes,
			final Date dtInicio, final Date dtFim);

	FatAih obterFatAihPorChavePrimaria(final Long numeroAIH);

	List<FatMotivoPendencia> listarMotivosPendenciaPorSeqOuDescricao(
			final Object filtro);

	Long listarMotivosPendenciaPorSeqOuDescricaoCount(final Object filtro);

	void inserirFatPendenciaContaHospitalar(final Integer cthSeq,
			final FatMotivoPendencia motivoPendencia, final Short unfSeq,
			final DominioSituacao indSituacao)
			throws ApplicationBusinessException;

	List<FatMotivoPendencia> pesquisarMotivosPendencia(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FatMotivoPendencia motivoPendencia);

	Long pesquisarMotivosPendenciaCount(FatMotivoPendencia motivoPendencia);

	void persistirFatMotivoPendencia(final FatMotivoPendencia motivoPendencia)
			throws ApplicationBusinessException;

	void removerFatMotivoPendencia(final Short seq) throws BaseException;

	void alterarFatPendenciaContaHospitalar(final FatPendenciaContaHosp pojo)
			throws ApplicationBusinessException;

	FatPendenciaContaHosp obterFatPendenciaContaHosp(
			final FatPendenciaContaHosp fatPCH);

	List<FatPendenciaContaHosp> listarFatPendenciaContaHospPorCthSeq(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer cthSeq);

	Long listarFatPendenciaContaHospPorCthSeqCount(final Integer cthSeq);

	List<FatProcedHospInternos> listarFatProcedHospInternos(
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc,
			final DominioSituacao situacao, final Boolean indOrigemPresc,
			final DominioTipoNutricaoParenteral tipoNutricaoEnteral,
			final Integer pciSeq, final Integer phiSeq,
			final Integer phiSeqAgrupado);

	Long listarFatProcedHospInternosCount(final DominioSituacao situacao,
			final Boolean indOrigemPresc,
			final DominioTipoNutricaoParenteral tipoNutricaoEnteral,
			final Integer pciSeq, final Integer phiSeq,
			final Integer phiSeqAgrupado);

	List<FatProcedHospInternos> listarPhis(final Object objPesquisa);

	Long listarPhisCount(final Object objPesquisa);

	List<FatProcedHospInternos> listarPhis(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final Integer seq,
			final DominioSituacao situacao, final Integer matCodigo,
			final Integer pciSeq, final Short pedSeq, final String csaCodigo,
			final String pheCodigo, final String emaExaSigla,
			final Integer emaManSeq);

	Long listarPhisCount(final Integer seq, final DominioSituacao situacao,
			final Integer matCodigo, final Integer pciSeq, final Short pedSeq,
			final String csaCodigo, final String pheCodigo,
			final String emaExaSigla, final Integer emaManSeq);

	Long pesquisarAihsLiberadasCount(final Long nroAih, final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula);

	List<FatAih> pesquisarAihsLiberadas(final Integer firstResult,
			final Integer maxResult, final String order, final boolean asc,
			final Long nroAih, final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula);

	List<FatAih> pesquisarAihs(final Integer firstResult,
			final Integer maxResult, final String order, final boolean asc,
			final Long nroAih, final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula, final Date dtCriadoEm);

	Long pesquisarAihsCount(final Long nroAih, final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula, final Date dtCriadoEm);

	List<FatCompetencia> listarFatCompetencia(final FatCompetencia competencia,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc);

	Long listarFatCompetenciaCount(final FatCompetencia competencia);

	FatCompetencia obterCompetenciaModuloMesAnoDtHoraInicioSemHora(
			final DominioModuloCompetencia modulo, final Integer mes,
			final Integer ano, final Date dtHoraInicio);

	void atualizarFatCompetencia(final FatCompetencia competencia)
			throws ApplicationBusinessException;

	List<FatProcedHospInternos> listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricao(
			final Object param, final String ordem,
			final DominioSituacao situacao);

	Long listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricaoCount(
			final Object param, final DominioSituacao situacao);

	void persistirProcedimentoHospitalarInterno(
			final FatProcedHospInternos procedHospInterno) throws BaseException;

	void reinternarContaHospitalar(final Integer cthSeq,
			final Integer pacCodigo, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	List<Long> gerarFatAih(final Long nroAihInicial, final Long nroAihFinal);

	List<FatAih> pesquisarAihsIntervalo(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final Long nroAihInicial, final Long nroAihFinal);

	Long pesquisarAihsIntervaloCount(final Long nroAihInicial,
			final Long nroAihFinal);

	Integer gravarFatAihLote(final List<Long> novasAihs) throws BaseException;

	void persistirGrupoItemConvenio(final FatConvGrupoItemProced newFat,
			final FatConvGrupoItemProced oldFat,
			final DominioOperacoesJournal operacao)
			throws ApplicationBusinessException;

	void excluirGrupoItemConvenio(final FatConvGrupoItemProced newFat)
			throws ApplicationBusinessException;

	List<LogInconsistenciasInternacaoVO> getLogsInconsistenciasInternacaoVO(
			final Date dtCriacaoIni, final Date dtCriacaoFim,
			final Date dtPrevia, final Integer prontuario,
			final Integer cthSeq, final String inconsistencia,
			final String iniciaisPaciente, final Boolean reapresentada,
			final DominioGrupoProcedimento grupoProcedimento)
			throws ApplicationBusinessException;

	List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimentoPorProcedHospInt(
			final Integer phiSeq) throws ApplicationBusinessException;

	List<FatProcedAmbRealizado> listarProcedimentosAmbulatoriaisRealizados(
			final Integer numeroConsulta);

	List<FatProcedAmbRealizado> listarProcedimentosAmbulatoriaisRealizados(
			final Integer numeroConsulta,
			final DominioSituacaoProcedimentoAmbulatorio[] situacoesIgnoradas);

	List<FatCompetencia> listarCompetenciaModuloMesAnoDtHoraInicioSemHora(
			final FatCompetenciaId id);

	Long listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(
			final FatCompetenciaId id);

	List<AIHFaturadaPorPacienteVO> obterAIHsFaturadasPorPaciente(
			final Date dtHrInicio, final Integer ano, final Integer mes,
			final String iniciaisPaciente, final Boolean reapresentada,
			final Integer clinica);

	List<FatProcedHospInternos> obterProcedimentoLimitadoPeloMaterial(
			final Object objPesquisa) throws BaseException;

	Long obterProcedimentoLimitadoPeloMaterialCount(final Object objPesquisa)
			throws BaseException;

	List<TotaisPorDCIHVO> obterTotaisPorDCIH(final Date dtHrInicio,
			final Integer ano, final Integer mes);

	List<FaturamentoPorProcedimentoVO> obterFaturamentoPorProcedimento(
			final Date dtHrInicio, final Integer ano, final Integer mes);

	FaturamentoPorProcedimentoVO obterFaturamentoPorProcedimentoUTIEspelho(
			final Date dtHrInicio, final Integer ano, final Integer mes);

	String geraCSVRelatorioTotaisDCIH(final FatCompetencia competencia)
			throws IOException;

	String geraCSVRelatorioValoresAIHPorDCIH(final Integer ano,
			final Integer mes) throws IOException;

	String gerarCSVRelatorioAihsFaturadas(final Integer cthSeq,
			final Integer prontuario, final Integer mes, final Integer ano,
			final Date dthrInicio, final Long codTabelaIni,
			final Long codTabelaFim, final String iniciais,
			final Boolean reapresentada) throws IOException, BaseException;

	List<AghCid> listarPorItemProcedimentoHospitalarEConvenio(
			final Short iphPhoSeq, final Integer iphSeq,
			final Short cpgCphCspCnvCodigo, final String order);

	List<FatMotivoRejeicaoConta> pesquisarMotivosRejeicaoConta(
			final String filtro, final DominioSituacao situacao,
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc);

	List<FatItensProcedHospitalar> listarPorCidEConvenio(
			final String cidCodigo, final Short cpgCphCspCnvCodigo,
			final String campoOrder, final Boolean order);

	Long pesquisarMotivosRejeicaoContaCount(final String filtro,
			final DominioSituacao situacao);

	List<FatCompatExclusItem> listarFatCompatExclusItemPorIphCompatibilizaEIndSituacaoEIndComparacao(
			final Short iphPhoSeqCompatibiliza,
			final Integer iphSeqCompatibiliza,
			final DominioIndComparacao indComparacao,
			final DominioSituacao iphIndSituacao, final String colunaOrder,
			final Boolean order);

	List<FatVlrItemProcedHospComps> obterListaValorItemProcHospCompPorPhoIphAbertos(
			final Short phoSeq, final Integer iphSeq);

	List<ValoresAIHPorDCIHVO> obterValoresAIHPorDCIH(final Integer ano,
			final Integer mes);

	void reapresentarContaHospitalar(final Integer cthSeq,
			final FatMotivoRejeicaoConta motivoRejeicaoConta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void desfazerReapresentacaoContaHospitalar(final Integer cthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void rejeitarContaHospitalar(final Integer cthSeq,
			final FatMotivoRejeicaoConta motivoRejeicaoConta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	List<FatAtoMedicoAih> buscarAtosMedicosEspelho(final Integer cthSeq,
			final int firstResult, final int maxResults,
			final String orderProperty, final boolean asc);

	Boolean verificarCaracteristicaExame(final Integer seqIph,
			final Short seqIphPho, final DominioFatTipoCaractItem caracteristica);

	Boolean encerrarContasHospitalares(final boolean isScheduled,
			final Integer cth, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final AghJobDetail job)
			throws BaseException;

	List<Integer> getContasEncerramentoEmLote();

	List<RelacaoDeOPMNaoFaturadaVO> obterRelacaoDeOPMNaoFaturada(
			final Long procedimento, final Integer ano, final Integer mes,
			final Date dtHrInicio, final Long SSM,
			final String iniciaisPaciente, final Boolean reapresentada)
			throws ApplicationBusinessException;

	String geraCSVRelatorioRelacaoDeOPMNaoFaturada(final Long procedimento,
			final Integer ano, final Integer mes, final Date dtHrInicio,
			final Long SSM, final String iniciaisPaciente,
			final Boolean reapresentada) throws IOException,
			ApplicationBusinessException;

	List<FatContaSugestaoDesdobr> pesquisarSugestoesDesdobramento(
			final Date dataHoraSugestao, final String origem,
			final String leito, final Integer prontuario,
			final Boolean considera,
			final DominioSituacaoConta[] situacoesContaHospitalar,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc);

	Long pesquisarSugestoesDesdobramentoCount(final Date dataHoraSugestao,
			final String origem, final String leito, final Integer prontuario,
			final Boolean considera,
			final DominioSituacaoConta[] situacoesContaHospitalar);

	FatDadosContaSemInt obterFatDadosContaSemInt(final Integer seq);

	FatConvenioSaude obterFatConvenioSaudePorId(final Short convCodigo);

	List<VFatContasHospPacientes> listarContasPorPacCodigoDthrRealizadoESituacaoCth(
			final Integer pacCodigo, final Date dthrRealizado,
			final DominioSituacaoConta[] cthIndSituacao,
			final String colunaOrder, final Boolean order);

	List<Integer> gerarSugestoesDesdobramento() throws BaseException;

	Boolean geraSugestaoDesdobramentoContaHospitalar(final Integer cthSeq)
			throws BaseException;

	Boolean geraSugestaoDesdobramentoContaHospitalar(Integer pCthSeq,
			Byte pMdsSeq) throws BaseException;

	void geraSugestaoDesdobramentoParaCirAgendada() throws BaseException;

	List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> obterCirurgiasAgendadas();

	void removerFatContaSugestaoDesdobr(
			final FatContaSugestaoDesdobr contaSugestaoDesdobr,
			final boolean flush);

	void removerFatContaSugestaoDesdobrPorId(
			final FatContaSugestaoDesdobrId id, final boolean flush);

	FatContaSugestaoDesdobr obterFatContaSugestaoDesdobrPorChavePrimaria(
			final FatContaSugestaoDesdobrId id);

	void atualizarFatContaSugestaoDesdobr(
			final FatContaSugestaoDesdobr contaSugestaoDesdobr,
			final boolean flush) throws BaseException;

	List<FatCompatExclusItem> listaCompatExclusItem(final Short iphPhoSeq,
			final Integer iphSeq, final DominioIndComparacao indComparacao,
			final DominioIndCompatExclus indCompatExclus,
			final DominioSituacao indSituacao, final String order);

	String verFatCompatItem(final Short phoSeqCont, final Integer iphSeqCont,
			final Short phoSeqComp, final Integer iphSeqComp);

	List<FatCompatExclusItem> pesquisarExcludencia(final Short phoComp,
			final Integer iphComp, final DominioIndComparacao indComparacao,
			final DominioSituacao indSituacao);

	void inserirFatDadosContaSemInt(final FatDadosContaSemInt entidade,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void atualizarFatDadosContaSemInt(final FatDadosContaSemInt fdsi,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	FatDadosContaSemInt fatcGeraContaSemInt(
			final AghEspecialidades especialidade, final Date dtRealizado,
			final AipPacientes paciente, final AghUnidadesFuncionais un,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	FatContasInternacao obterContaInternacaoEmFatDadosContaSemInt(
			final Integer dcsSeq, final Integer cthSeq);

	List<FatItemContaHospitalar> obterItensContaHospitalarPorItemRmps(
			final Integer rmpSeq, final Short numero);

	FatItemContaHospitalar obterItensContaHospitalarPorContaHospitalarePHI(
			final Integer cthSeq, final Integer phiSeq);

	boolean validaNumeroAIHInformadoManualmente(final Long numeroAIH)
			throws ApplicationBusinessException;

	void inserirFatLogInterface(final FatLogInterface fli);

	String geraCSVRelatorioLogInconsistenciasInternacao(
			final Date dtCriacaoIni, final Date dtCriacaoFim,
			final Date dtPrevia, final Integer pacProntuario,
			final Integer cthSeq, final String inconsistencia,
			final String iniciaisPaciente, final Boolean reapresentada,
			final DominioGrupoProcedimento grupoProcedimento)
			throws IOException, ApplicationBusinessException;

	String geraCSVRelatorioAIHFaturadaPorPaciente(final Date dtHrInicio,
			final Integer ano, final Integer mes,
			final String iniciaisPaciente, final Boolean reapresentada,
			final Integer clinica) throws IOException,
			ApplicationBusinessException;

	String geraCSVRelatorioContasPreenchimentoLaudos(final Date dtPrevia,
			final Short unfSeq, final String iniciaisPaciente)
			throws IOException, ApplicationBusinessException;

	void atualizaContaHospitalar(final Integer atdSeq,
			final Integer prescricaoMedicaSeq, final Date dataInicio,
			final Date dataFim, final Date dataInicioMovimentoPendente,
			final DominioOperacaoBanco operacao,
			final Date dataFimVinculoServidor) throws BaseException;

	Map<String, Integer> buscarDadosInicias(final Integer cthSeq);

	Long geracaoSugestoesDesdobramentoCount();

	List<RelatorioIntermediarioLancamentosContaVO> obterItensContaParaRelatorioIntermediarioLancamentos(
			final Integer cthSeq,
			Map<AghuParametrosEnum, AghParametros> parametros)
			throws ApplicationBusinessException;

	String geraCSVRelatorioIntermediarioLancamentosConta(final Integer cthSeq)
			throws IOException, ApplicationBusinessException;

	String geraCSVRelatorioFaturamentoPorProcedimento(
			final FatCompetencia competencia) throws IOException,
			ApplicationBusinessException;

	String gerarCSVRelatorioArquivoProcedimento() throws IOException,
			ApplicationBusinessException;

	List<FatEspelhoAih> obterFatEspelhoAihPorCth(final Integer cthSeq);

	List<ContasPreenchimentoLaudosVO> obterContasPreenchimentoLaudos(
			final Date dtUltimaPrevia, final Short unfSeq,
			final String iniciaisPaciente);

	List<AihPorProcedimentoVO> obterAihsPorProcedimentoVO(
			final Long procedimentoInicial, final Long procedimentoFinal,
			final Date dtHrInicio, final Integer mes, final Integer ano,
			final String iniciaisPaciente, final boolean reapresentada);

	String geraCSVRelatorioAIHPorProcedimento(final Long procedimentoInicial,
			final Long procedimentoFinal, final Date dtHrInicio,
			final Integer mes, final Integer ano,
			final String iniciaisPaciente, final boolean reapresentada)
			throws IOException, ApplicationBusinessException;

	String geraCSVRelatorioPreviaDiariaFaturamento(
			final FatCompetencia competencia) throws IOException,
			ApplicationBusinessException;

	String geraCSVRelatorioLogInconsistenciaBPA(
			final DominioModuloMensagem modulo, final DominioSituacao situacao)
			throws IOException, ApplicationBusinessException;

	List<FatContaSugestaoDesdobr> pesquisarSugestoesDesdobramento(
			final Date dataHoraSugestao, final Integer mdsSeq,
			final String descricao, final Integer cthSeq,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc);

	Long pesquisarSugestoesDesdobramentoCount(final Date dataHoraSugestao,
			final Integer mdsSeq, final String descricao, final Integer cthSeq);

	List<LogInconsistenciaBPAVO> obterLogInconsistenciaBPAVO(
			final DominioModuloMensagem modulo, final String erro,
			final DominioSituacaoMensagemLog situacao, final Short pIphPhoSeq,
			final Short pTipoGrupoContaSUS, final Short pCpgCphCspSeq,
			final Short pCpgCphCspCnvCodigo)
			throws ApplicationBusinessException;

	List<String> obterLogInconsistenciaBPACSV(
			final DominioModuloCompetencia[] modulo, final String[] erros,
			final String erro, final DominioSituacao situacao,
			final Short pCpgCphCspSeq, final Short pCpgCphCspCnvCodigo,
			final Short pIphPhoSeq) throws ApplicationBusinessException;

	/**
	 * Obtém o convênio e o plano que permite internação
	 */
	FatConvenioSaudePlano obterConvenioPlanoInternacao(final Short cnvCodigo);

	List<FatConvenioSaudePlano> pesquisarConvenioPlano(
			final Integer firstResult, final Integer maxResult,
			final String strPesquisa);

	/**
	 * Este método implementa a view descrita acima, porém trazendo apenas o
	 * campo CGI.CPG_CPH_CSP_CNV_CODIGO, necessário na pesquisa de procedimentos
	 * de internação quando um CID já tiver sido informado.
	 */

	List<Long> pesquisarFatAssociacaoProcedimentos(final Integer cidSeq);

	List<FatVlrItemProcedHospComps> pesquisarProcedimentos(
			final Object strPesquisa, final Short parametroProcedimento,
			final AipPacientes paciente, final Integer cidSeq)
			throws ApplicationBusinessException;

	List<ConvenioPlanoVO> pesquisarConvenioPlanoVOPorCodigoDescricao(
			final Object strPesquisa);

	Map<Integer, List<ResumoAIHEmLoteServicosVO>> listarAtosMedicosResumoAihEmLote(
			final List<Integer> seqs);

	List<ResumoAIHEmLoteVO> pesquisarResumoAIHEmLote(final Integer cthSeq,
			final Date dtInicial, final Date dtFinal,
			final Boolean indAutorizadoSSM, final String iniciaisPaciente,
			final Boolean reapresentada);

	Long listarFatProcedAmbRealizadoCount(final FatCompetencia competencia,
			final AipPacientes paciente,
			final FatProcedHospInternos procedimento,
			final AacConsultaProcedHospitalar consulta,
			final AelItemSolicitacaoExames itemSolicitacaoExame,
			final MbcProcEspPorCirurgias procCirurgia,
			final DominioSituacaoProcedimentoAmbulatorio situacao,
			final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq,
			final Byte cpgCphCspSeq, final Short unfSeq,
			final Long procedimentoAmbSeq,
			final DominioOrigemProcedimentoAmbulatorialRealizado origem,
			final Short convenioId, final Byte planoId);

	List<FatProcedAmbRealizadosVO> listarFatProcedAmbRealizado(
			final FatCompetencia competencia, final AipPacientes paciente,
			final FatProcedHospInternos procedimento,
			final AacConsultaProcedHospitalar consulta,
			final AelItemSolicitacaoExames itemSolicitacaoExame,
			final MbcProcEspPorCirurgias procCirurgia,
			final DominioSituacaoProcedimentoAmbulatorio situacao,
			final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq,
			final Byte cpgCphCspSeq, final Short unfSeq,
			final Long procedimentoAmbSeq,
			final DominioOrigemProcedimentoAmbulatorialRealizado origem,
			final Short convenioId, final Byte planoId,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc);

	List<FatProcedAmbRealizado> buscarProcedimentosCirurgicosParaSeremFaturados(
			final AipPacientes paciente, final MbcCirurgias cirurgia,
			final Short unfSeq, final Short convenioId, final Byte planoId);

	FatProcedAmbRealizado obterFatProcedAmbRealizadoPorSeq(final Long seq);

	List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(
			final Object objPesquisa, final Short cpgCphCspCnvCodigo,
			final Short cpgGrcSeq, final Byte cpgCphCspSeq)
			throws ApplicationBusinessException;

	Long listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(
			final Object objPesquisa, final Short cpgCphCspCnvCodigo,
			final Short cpgGrcSeq, final Byte cpgCphCspSeq)
			throws ApplicationBusinessException;

	List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimento(
			final Integer phiSeq, final Short cpgCphCspCnvCodigo,
			final Byte cpgCphCspSeq, final Short cpgGrcSeq);

	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA (CURSOR CONTAS - PARTE 2) Obtém a conta
	 * hospitalar para encerramento
	 */
	FatContasHospitalares obterContaHospitalarPorInternacao(final Integer intSeq);

	List<FatContasInternacao> pesquisarContasInternacaoParaUpdate(
			final Integer intSeq);

	void removerFatContasInternacao(
			final FatContasInternacao fatContasInternacao, final boolean flush);

	void removerFatItemContaHospitalar(
			final FatItemContaHospitalar fatItemContaHospitalar,
			final boolean flush);

	void removerFatCidContaHospitalar(
			final FatCidContaHospitalar fatCidContaHospitalar,
			final boolean flush);

	/**
	 * Método que pesquisa os ítens de conta hospitalar
	 * 
	 * @param cthSeq
	 * @return
	 */
	List<FatItemContaHospitalar> pesquisarItensContaHospitalarPorCthSituacao(
			final Integer cthSeq, final DominioSituacaoItenConta situacaoConta);

	List<FatContaSugestaoDesdobr> pesquisarFatContaSugestaoDesdobrPorCthNaoConsidera(
			final Integer cthSeq);

	/**
	 * Lista as contas de uma internação
	 */

	List<FatContasInternacao> pesquisarContasInternacaoOrderDtInternacaoDesc(
			final Integer intSeq);

	Long acomodacaoInternacaoConvenioCount(final AinInternacao internacao);

	FatConvenioSaudePlano obterFatConvenioSaudePlanoPorChavePrimaria(
			final FatConvenioSaudePlanoId id);

	@Deprecated
	FatConvenioSaudePlano atualizarFatConvenioSaudePlano(
			final FatConvenioSaudePlano fatConvenioSaudePlano);

	@Deprecated
	FatContasHospitalares atualizarFatContasHospitalares(
			final FatContasHospitalares fatContasHospitalares);

	List<FatTipoAih> pesquisarTipoAihPorSituacaoCodSus(
			final DominioSituacao situacao, final Short codSus);

	FatContasHospitalares obterContaHospitalarAbertaOuFechada(
			final Integer seqContaHospitalarOld);

	boolean verificaProcedimentoHospitalarInterno(final Integer matCodigo);

	List<FatConvTipoDocumentos> pesquisarObrigatoriosPorFatConvenioSaudePlano(
			final Short novoCspCnvCodigo, final Byte novoCspSeq);

	List<FatConvenioSaude> pesquisarConveniosSaudeGrupoSUS(
			final Short codigoConvenio);

	FatItensProcedHospitalar obterItemProcedimentoHospitalar(
			final Integer iphSeq, final Short iphPhoSeq);

	FatContasInternacao obtePrimeiraContaHospitalar(
			final Integer seqInternacao, final Short cspCnvCodigo);

	void validarCamposProgramarEncerramento(final String nomeJob,
			final DominioOpcaoEncerramentoAmbulatorio opcao,
			final Date dtExecucao, final Date dtFimCompetencia,
			final Date dtFimProximaCompetencia, final Boolean previa)
			throws ApplicationBusinessException;

	void atualizarFaturamentoProcedimentoConsulta(final Integer conNumero,
			final Integer phiSeq, final Short quantidade,
			final AacRetornos retorno, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	void inserirFaturamentoProcedimentoConsulta(
			final AacConsultaProcedHospitalar consultaProcedHospitalar,
			final Date dthrRealizado, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	void rnFatpExecFatNew(final DominioOpcaoEncerramentoAmbulatorio modulo,
			final Boolean previa, final Date cpeDtFim, final AghJobDetail job,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	String gerarArquivoBPADataSus(final FatCompetencia competencia,
			final Long procedimento, final Integer tctSeq) throws IOException;

	List<FatArqEspelhoProcedAmbVO> listarFatArqEspelhoProcedAmbVO(
			final FatCompetencia competencia, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc);

	Integer listarFatArqEspelhoProcedAmbVOCount(final FatCompetencia competencia);

	void fatpAgruBpaBpi(final boolean previa, final FatCompetencia competencia,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	void enviaEmailResultadoEncerramentoAmbulatorio(final String msg)
			throws ApplicationBusinessException;

	void enviaEmailResultadoEncerramentoAmbulatorio(final String msg,
			String remetente, List<String> destinatarios)
			throws ApplicationBusinessException;

	void enviaEmailResultadoEncerramentoCTHs(final List<String> resultOK,
			final List<String> resultNOK, final Date dataInicioEncerramento)
			throws ApplicationBusinessException;

	void enviaEmailInicioEncerramentoCTHs(final Integer quantidadeContas,
			final Date dataInicioEncerramento)
			throws ApplicationBusinessException;

	Map<String, Object> atualizarCidProcedimentoNovo(final List<String> lista)
			throws ApplicationBusinessException;

	Map<String, Object> atualizarCompatibilidade(final List<String> lista)
			throws BaseException;

	Map<String, Object> atualizarServicoClassificacao(final List<String> lista)
			throws BaseException;
	
	Map<String, Object> atualizarInstrumentoRegistro(final List<String> lista) 
			throws BaseException;
	
	String gerarCSVRelatorioConsultaRateioServicosProfissionais(
			final FatCompetencia competencia)
			throws ApplicationBusinessException, IOException;

	List<ConsultaRateioProfissionalVO> listarConsultaRateioServicosProfissionais(
			final FatCompetencia competencia)
			throws ApplicationBusinessException, IOException;

	void gerarArquivoRelatorioRealizadosIndividuaisFolhaRosto(
			final FatCompetenciaId id, final Integer phiSeqInicial,
			final Integer phiSeqFinal, final TipoArquivoRelatorio tipoArquivo);

	String gerarArquivoProducaoPHI(final List<Integer> phiSeqs,
			final Date dtInicio, final Date dtFinal) throws BaseException;

	/**
	 * Esse método não pode ter , pois é usado ao dar alta para o paciente, se
	 * tivesse bypass poderia não encontrar a conta de internação o que geraria
	 * uma exceção de que a conta já foi faturada impedindo a alta do paciente
	 * 
	 * @param seqInternacao
	 * @return
	 */
	FatContasInternacao obterContaHospitalarePorInternacao(
			final Integer seqInternacao);

	List<FatProcedAmbRealizado> buscarPorNumeroConsultaEProcedHospInternos(
			final Integer conNumero, final Integer phiSeq);

	void rnPmrpTrcEspAtd(final Integer atdSeq, final Short espSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	/**
	 * Esse método não pode ter , pois a rotina que usa essa consulta é
	 * executada pelo banco de dados
	 * 
	 * @param seqInternacao
	 * @param indSituacaoContaHospitalar
	 * @return
	 */
	List<AtualizarContaInternacaoVO> listarAtualizarContaInternacaoVO(
			Integer seqInternacao,
			DominioSituacaoConta indSituacaoContaHospitalar);

	FatConvenioSaudePlano obterConvenioSaudePlanoPorChavePrimaria(
			FatConvenioSaudePlanoId fatConvenioSaudePlanoId);

	Long pesquisarFatCompetenciaProdCount(
			final FatCompetenciaProd competenciaProducao);

	List<FatCompetenciaProd> pesquisarFatCompetenciaProd(
			final FatCompetenciaProd competenciaProducao,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc);

	FatCompetenciaId getCompetenciaId(Object objPesquisa)
			throws ApplicationBusinessException;

	FatCompetenciaId getCompetenciaId(final Object objPesquisa,
			final DominioModuloCompetencia modulo)
			throws ApplicationBusinessException;

	FatProcedHospInternos pesquisarPorChaveGenericaFatProcedHospInternos(
			Object seq, Fields field);

	FatProcedHospInternos obterFatProcedHospInternosPorMaterial(
			AelExamesMaterialAnaliseId exaManId);

	FatProcedHospInternos obterFatProcedHospInternosPorMaterial(
			Integer matCodigo);

	FatConvenioSaudePlano obterConvenioSaudePlano(Short cspCnvCodigo,
			Byte cspSeq);

	List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidAtivosPorPhiSeqCidSeq(
			Integer phiSeq, Integer cidSeq);

	List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidAtivosPorPhiSeq(
			Integer phiSeq);

	FatProcedHospIntCid pesquisarFatProcedHospIntCidPorPhiSeqValidade(
			Integer phiSeq, DominioTipoPlano validade);

	List<FatProcedAmbRealizado> buscarPorNumeroConsultaEProcedHospInternosApresentadosExcluidos(
			Integer conNumero, Integer phiSeq);

	List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimentoOutrosProcedimentos(
			Integer conNumero, Integer phiSeq, Short cpgCphCspCnvCodigo,
			Byte cpgCphCspSeq, Short cpgGrcSeq);

	List<FatCompetencia> pesquisarCompetenciasPorModulo(
			DominioModuloCompetencia amb);

	FatCompetencia obterCompetenciaModuloMesAno(DominioModuloCompetencia amb,
			int i, int j);

	List<FatItemContaApac> listarItemContaApacPorPrhConsultaSituacao(
			Integer consultaNumero);

	List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorPrhConsultaSituacao(
			Integer consultaNumero);

	List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorConsulta(
			Integer consultaNumero);

	void atualizarProcedAmbRealizado(FatProcedAmbRealizado procedAmbRealizado);

	FatProcedAmbRealizado obterFatProcedAmbRealizadoPorPrhConNumero(
			Integer numero);

	List<FatResumoApacs> listarResumosApacsPorCodigoPaciente(Integer pacCodigo);

	void persistirFatResumoApacs(FatResumoApacs fatResumoApacs);

	List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorCodigoPacientePrhConNumeroNulo(
			Integer pacCodigo);

	List<FatPacienteTratamentos> listarPacientesTratamentosPorCodigoPaciente(
			Integer pacCodigo);

	void persistirFatPacienteTratamentos(
			FatPacienteTratamentos fatPacienteTratamentos);

	List<FatPacienteTransplantes> listarPacientesTransplantesPorPacCodigo(
			Integer pacCodigo);

	void persistirFatPacienteTransplantes(
			FatPacienteTransplantes fatPacienteTransplantes);

	void removerFatPacienteTransplantes(
			FatPacienteTransplantes fatPacienteTransplantes);

	List<FatDadosContaSemInt> listarDadosContaSemIntPorCodigoPaciente(
			Integer pacCodigo);

	void persistirFatContaApac(FatContaApac fatContaApac);

	List<FatContaApac> listarContasApacsPorCodigoPaciente(Integer pacCodigo);

	void persistirFatCandidatosApacOtorrino(
			FatCandidatosApacOtorrino fatCandidatosApacOtorrino);

	List<FatCandidatosApacOtorrino> listarCandidatosApacOtorrinoPorCodigoPaciente(
			Integer pacCodigo);

	CntaConv obterCntaConv(Integer intdCodPrnt, Date intdDataInt, Short convCod);

	List<CntaConv> listarCntaConvPorIntdCodPrnt(Integer prontuario);

	void persistirCntaConv(CntaConv cntaConv);

	void atualizarCntaConv(CntaConv cntaConv, boolean flush);

	boolean temContaConv(Integer nroConta);

	boolean temBackupContaConv(Integer nroConta);

	List<FatConvenioSaude> obterConveniosSaudeAtivos();

	Long obterCountConvenioSaudeAtivoPorPgdSeq(Short pgdSeq);
	
	List<FatConvenioSaude> listarConveniosSaudeAtivosPorPgdSeq(Short pgdSeq);

	List<FatConvenioSaudePlano> listarConvenioSaudePlanos(String parametro);

	FatConvenioSaudePlano obterConvenioSaudePlanoAtivo(Short codigoConvenio,
			Byte seqPlano);

	List<FatConvGrupoItemProced> obterListaFatConvGrupoItensProcedPorExame(
			Byte planoConvenio, Short convenio, short parseShort,
			String siglaExame, int parseInt, short parseShort2);

	FatConvGrupoItemProced obterFatConvGrupoItensProcedPeloId(
			Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Integer phiSeq);

	FatTiposDocumento obterTipoDoc(Short seqTipoDoc);

	List<FatTiposDocumento> obterTiposDocs(String seqDesc);

	List<FatContasInternacao> pesquisarNumeroAihContaHospitalarAtendimento(
			Integer seqInternacao);

	VFatAssociacaoProcedimento obterAssociacaoProcedimento(Short seqPho,
			Integer seqIph);

	void refreshItensProcedimentoHospitalar(
			FatItensProcedHospitalar itemProcedimentoHospitalar);

	List<FatProcedAmbRealizado> listarProcedAmbRealizadosPrhConNumeroNulo(
			Integer codigo, AghAtendimentos atendimento);

	List<FatProcedAmbRealizado> listarProcedAmbRealizados(
			Integer seq,
			DominioSituacaoProcedimentoAmbulatorio[] dominioSituacaoProcedimentoAmbulatorios);

	FatProcedHospInternos obterFatProcedHospInternosPorCodigoProcedimentoHemoterapico(
			String codigo);

	boolean existeProcedimentohospitalarInterno(
			FatProcedHospInternos fatProcedHospInternos,
			Class<FatConvGrupoItemProced> class1, Enum procedHospInterno);

	void removerFatProcedHospInternosPorMaterial(AelExamesMaterialAnaliseId id);

	List<FatProcedHospInternos> buscaProcedimentosComLaudoJustificativaParaImpressao(
			AghAtendimentos atendimento);

	List<FatProcedHospInternos> buscaProcedimentoHospitalarInterno(
			Integer matCodigo, Integer pciSeq, Short pedSeq);

	List<FatProcedHospInternos> pesquisarPorProcedimentoEspecialDiverso(
			MpmProcedEspecialDiversos procedimento);

	void removerProcedimetoHospitalarInterno(FatProcedHospInternos proced);

	void insereFaturamentoHospitalInternoParaMpmCuidadoUsual(
			MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException;

	void atualizaFaturamentoInternoParaMpmCuidadoUsual(
			MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException;

	FatProcedHospInternos obterProcedimentoHospitalarInternoPorCuidadoUsual(
			Integer seqCuidado);

	List<FatProcedHospInternos> pesquisarPhis(Object paramPesquisa,
			String ordem, DominioSituacao situacao);

	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA (CURSOR CONTAS - PARTE 1) Obtém a
	 * CntaConv
	 * 
	 * @param intSeq
	 * @return
	 */

	CntaConv obterCntaConv(Integer intSeq);

	List<CntaConv> pesquisarContaNotEcrtPorInternacao(Integer seqInternacao);

	CntaConv obterCntaConvPorChavePrimaria(Integer nroCntaCont);

	void removerCntaConv(CntaConv cntaConv, boolean flush);

	List<CntaConv> listarCntaConvPorSeqAtendimento(Integer seqAtendimento);

	VFatContaHospitalarPac buscarPrimeiraContaHospitalarPaciente(Integer cthSeq);

	VFatContaHospitalarPac buscarPrimeiraAihPaciente(
			Long numeroAih);
	
	CaracteristicaPhiVO fatcVerCaractPhi(Short cnvCodigo, Byte cspSeq,
			Integer phiSeq, String caracteristica)
			throws ApplicationBusinessException;

	List<PreviaDiariaFaturamentoVO> obterPreviaDiariaFaturamento(
			final FatCompetencia competencia, final boolean isPDF)
			throws ApplicationBusinessException;

	List<Date> obterDataPreviaModuloSIS(FatCompetencia competencia);

	List<FaturaAmbulatorioVO> listarFaturamentoAmbulatorioPorCompetencia(
			Integer mes, Integer ano, Date dtHoraInicio);

	List<FatProcedHospInternos> listaPhiAtivosExame(DominioSituacao situacao,
			String exameSigla);

	String geraCSVRelatorioFaturaAmbulatorio(FatCompetencia competencia)
			throws ApplicationBusinessException, IOException;

	String gerarPDFRelatorioItensRealzIndv(byte[] bytes)
			throws ApplicationBusinessException, IOException;

	List<ItensRealizadosIndividuaisVO> listarItensRealizadosIndividuaisPorCompetencia(
			Date dtHoraInicio, Integer ano, Integer mes, Long procedInicial,
			Long procedFinal) throws BaseException;

	List<ItensRealizadosIndividuaisVO> listarItensRealizadosIndividuaisHistPorCompetencia(
			Date dtHoraInicio, Integer ano, Integer mes, Long procedInicial,
			Long procedFinal) throws BaseException;

	String geraCSVRelatorioItensRealzIndv(Date dtHoraInicio, Integer ano,
			Integer mes, Long procedInicial, Long procedFinal)
			throws IOException, BaseException;

	String geraCSVRelatorioItensRealzIndvHist(Date dtHoraInicio, Integer ano,
			Integer mes, Long procedInicial, Long procedFinal)
			throws IOException, BaseException;

	FatConvenioSaude obterConvenioSaude(Short codigo);

	public DadosCaracteristicaTratamentoApacVO verificarCaracteristicaTratamento(
			final Short phoSeqTrat, final Integer iphSeqTrat,
			final Short phoSeqItem, final Integer iphSeqItem,
			final Integer tciSeq);

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoParaExameMaterial(
			final Short cnvCodigo, final Byte cspSeq, final String sigla,
			final Integer manSeq, final Short tipoContaSus);

	public List<VFatProcedSusPhiVO> pesquisarViewFatProcedSusPhiVO(
			VFatProcedSusPhiVO filtro, Short pSusPadrao, Byte pSusAmbulatorio,
			Short pTipoGrupoContaSus);

	StringBuilder buscarPhiSus(AelItemSolicitacaoExames item);

	List<FatVlrItemProcedHospComps> pesquisarVlrItemProcedHospCompsPorDthrLiberadaExaSiglaManSeq(
			Date dthrLiberada, Date dataFinalMaisUm, String exaSigla,
			Integer manSeq, AghParametros pTipoGrupoContaSus);

	List<Byte> obterListaConvenioSaudeAtivoComPlanoAmbulatorialAtivo(
			Short codigoConvenio);

	FatCaractItemProcHosp obterQtdeFatCaractItemProcHosp(
			final FatItensProcedHospitalar proced, final Integer... tctSeq);

	Long obterQtdeFatProcedHospIntCid(final Integer phiSeq);

	Long pesquisarContaHospitalarCount(Integer prontuario,
			Integer contaHospitalar, String codigoDcih, Long numeroAih,
			Date competencia, Integer codigo, DominioSituacaoConta[] situacoes);

	List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloSeqProcCirg(
			Integer pciSeq);

	List<VFatAssociacaoProcedimento> pesquisarVFatAssociacaoProcedimentoAtivosPorPhi(
			Integer phiSeq) throws ApplicationBusinessException;

	void inserirProcedimentoHospitalarInterno(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedEspecialDiverso, String csaCodigo,
			String pheCodigo, String descricao, DominioSituacao indSituacao,
			Short euuSeq, Integer cduSeq, Short cuiSeq, Integer tidSeq)
			throws ApplicationBusinessException;

	void atualizarProcedimentoHospitalarInternoSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedEspecialDiverso, String csaCodigo,
			String pheCodigo, DominioSituacao indSituacao, Short euuSeq,
			Integer cduSeq, Short cuiSeq, Integer tidSeq)
			throws ApplicationBusinessException;

	void atualizarProcedimentoHospitalarInternoDescricao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedEspecialDiverso, String csaCodigo,
			String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException;

	List<FatConvenioSaudePlano> sbObterConvenio(final Object parametro);

	Long sbObterConvenioCount(final Object parametro);

	VFatAssociacaoProcedimento obterFatProcedHospIntPorExameMaterialConvCspIphPhoSeq(
			final String sigla, final Integer manSeq, final Short iphPhoSeq,
			final Short cnvCodigo, final Byte cspSeq);

	ArquivoURINomeQtdVO gerarArquivoFaturamentoParcialSUSNew(
			final FatCompetencia competencia, final Date dataEncIni,
			final Date dataEncFinal) throws IOException, BaseException;

	FatcVerCarPhiCnvVO fatcVerCarPhiCnv(final Short cspCnvCodigo,
			final Byte cspSeq, final Integer phi, final String pCaracteristica,
			final Short cpgGrcSeq);

	void removerFatItemContaApac(final FatItemContaApac elemento)
			throws ApplicationBusinessException;

	FatProcedAmbRealizado obterFatProcedAmbRealizadoPorCrgSeq(
			Integer ppcCrgSeq, Short cnvCodigo, Byte cspSeq);

	List<FatItemContaApac> listarFatItemContaApacPorPpcCrgSeq(Integer ppcCrgSeq);

	List<FatItemContaApac> listarFatItemContaApac(Integer ppcCrgSeq);

	void atualizarFatItemContaApac(final FatItemContaApac elemento,
			final FatItemContaApac oldElemento, final Boolean flush) throws BaseException;

	List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorCrgSeq(
			Integer ppcCrgSeq);

	void removerFatProcedAmbRealizado(FatProcedAmbRealizado elemento)
			throws BaseException;

	List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorCrgSeqSituacao(
			Integer ppcCrgSeq);

	void atualizarFatProcedAmbRealizado(FatProcedAmbRealizado elemento)
			throws BaseException;

	List<FatItemContaHospitalar> listarContaHospitalarPorCirurgia(
			Integer ppcCrgSeq);

	List<FatItemContaHospitalar> listarContaHospitalarPorSceRmrPacientes(
			Integer ppcCrgSeq);

	Integer obterMaxSeqConta(Date pcDthr, Integer atdSeq, Short cnvCodigo,
			Byte cnvSeq);

	FatProcedHospInternos obterFatProcedHospInternosPorChavePrimaria(Integer seq);

	/*
	 * QuartzTriggerHandle agendarEncerramentoAutomaticoContaHospitalar(
	 * 
	 * @Expiration final Date date, @IntervalCron final String cronn, final
	 * String nomeMicrocomputador, final RapServidores servidorLogado, final
	 * String nomeProcessoQuartz);
	 */

	void atualizarFaturamentoBlocoCirurgico(
			MbcProcEspPorCirurgias procEspPorCirurgia,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	/**
	 * Efetua a busca de todos os equipamentos utilizados numa determinada
	 * cirurgia.
	 * 
	 * @param crgSeq
	 *            - Seq da cirurgia
	 * @return List<{@link EquipamentoCirurgiaVO}>
	 * @author jgugel
	 */
	List<EquipamentoCirurgiaVO> buscarEquipamentos(Integer crgSeq);

	Date obterDataUltimoTransplante(final Integer pacCodigo);

	Boolean verificarCirurgiaFaturada(final Integer crgSeq,
			final DominioOrigemPacienteCirurgia origem);

	List<FatProcedHospIntCid> pesquisarProcedimentoHospitalarInternoCidCompativel(
			final Integer phiSeq, final Integer cidSeq,
			DominioOrigemPacienteCirurgia origem);

	List<FatVlrItemProcedHospComps> pesquisarGruposPopularProcedimentoHospitalarInterno(
			final Integer pciSeq, final Date cpeComp, final Short cnvCodigo,
			final Byte cspSeq, final Short tipoGrupoContaSus);

	Integer obterProcedimentoCirurgicoPopularProcedimentoHospitalarInterno(
			final Short iphPhoSeq, final Integer iphSeq, final Integer pciSeq,
			final Date cpeComp, final Short cnvCodigo, final Byte cspSeq,
			final Short tipoGrupoContaSus);

	List<FatCompetencia> pesquisarCompetenciaProcedimentoHospitalarInternoPorModulo(
			DominioModuloCompetencia modulo);

	List<FatVlrItemProcedHospComps> pesquisarProcedimentosPopularProcedimentoHospitalarInterno(
			final Integer pciSeq, final Date cpeComp, final Integer phiSeq,
			final Short tipoGrupoContaSus);

	public List<FatVlrItemProcedHospComps> pesquisarProcedimentosConvenioPopularProcedimentoHospitalarInterno(
			final Integer pciSeq, final Date cpeComp, final Short cnvCodigo,
			final Byte cspSeq, final Integer phiSeq,
			final Short tipoGrupoContaSus);

	List<Integer> buscarPhiSeqPorPciSeqOrigemPacienteCodigo(Integer pciSeq,
			Byte origemCodigo, Short grupoSUS);

	List<CirurgiaCodigoProcedimentoSusVO> obterListaCirurgiaCodigoProcedimentoPorPhiSeqOrigemGrupo(
			Integer phiSeq, Short grupoSUS, Byte origemCodigo);

	List<FatVlrItemProcedHospComps> pesquisarVlrItemProcedHospCompsPorIphSeqDtComp(
			Integer iphSeq, Short iphPhoSeq, Date valorDtComp);

	List<CirurgiaCodigoProcedimentoSusVO> getCursorFatAssocProcdporPhiSeqOrigemGrupo(
			final Integer phiSeq, final Short grupoSUS, final Byte origemCodigo);


	FatConvenioSaudePlano obterPorCspCnvCodigoECnvCodigo(Short codigo, Byte seq);

	/**
	 * Esse método executa o cursor c_sus das funções.
	 * 
	 * @ORADB MBCC_TAB_FAT_SUS_AMB / MBCC_TAB_FAT_SUS_INT
	 * @param phiSeq
	 *            Integer
	 * @return String
	 */

	List<Long> obterListaDeCodigoTabela(final Integer phiSeq,
			final DominioSituacao phiIndSituacao,
			final DominioSituacao iphIndSituacao,
			final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq,
			final Short iphPhoSeq);

	List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedCirPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, final Short especialidade,
			final Short iphPhoSeq, final Integer procedimento);

	List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, final Short especialidade,
			final Short iphPhoSeq, final Integer procedimento);

	void deleteFatProcedHospInternos(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq, Boolean flush);

	List<FatMotivoDesdobrClinica> pesquisarMotivoDesdobrClinicaPorClinica(
			AghClinicas clinica);

	List<VFatSsmInternacaoVO> obterListaVFatSssInternacao(Object strPesquisa,
			Integer idade, DominioSexoDeterminante sexo,
			Short paramTabelaFaturPadrao, Integer cidSeq, Integer caracteristica);

	Long obterListaVFatSssInternacaoCount(Object strPesquisa, Integer idade,
			DominioSexoDeterminante sexo, Short paramTabelaFaturPadrao,
			Integer cidSeq, Integer caracteristica);

	List<VFatSsmInternacaoVO> buscarVFatSsmInternacaoPorIphPho(Short phoSeq,
			Integer iphSeq);

	List<FatProcedHospInternos> pesquisarProcedimentosInternosPorTipoEtiqueta(
			String tipoEtiqueta);

	void atualizarItemProcedimentoHospitalar(FatItensProcedHospitalar newIph,
			FatItensProcedHospitalar oldIph, RapServidores servidorLogado)
			throws BaseException;

	List<CursoPopulaProcedimentoHospitalarInternoVO> obterCursorSSM(
			final Integer pciSeq, final Date cpeComp, final Short cnvCodigo,
			final Byte cspSeq, final Short tipoGrupoContaSus);

	List<CursoPopulaProcedimentoHospitalarInternoVO> obterCursorPHI(
			final Integer pciSeq, final Date cpeComp, final Integer phiSeq,
			final Short tipoGrupoContaSus);

	List<VFatConvPlanoGrupoProcedVO> listarConveniosPlanos(Short grcSeq,
			Short cphPhoSeq, Short cphCspCnvCodigo);

	public List<FatProcedHospInternos> listarPhi(Integer seq,
			DominioSituacao situacao, Integer matCodigo, Integer pciSeq,
			Short pedSeq, String csaCodigo, String pheCodigo,
			String emaExaSigla, Integer emaManSeq, Integer maxResult);

	FatCompetenciaCompatibilid obterFatCompetenciaCompatibilidPorSeq(
			Long vCpxSeq);

	List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloMatCodigo(
			Integer matCod);

	List<FatConvGrupoItemProced> listarFatConvGrupoItensProcedPorPhi(
			Short pIphPhoSeq, Integer pIphSeq, Short pCnvCodigo,
			Byte pCnvCspSeq, Short pCpgCphPhoSeq, Short pCpgGrcSeq,
			Integer phiSeq);

	List<RapServidores> buscaUsuariosPorCCusto(Integer cCusto);

	List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloCodProcHem(
			String codigo);

	void estornarCompetenciaInternacao(FatCompetencia competencia)
			throws BaseException;

	void extornaCompetenciaAmbulatorio(String usuarioLogado)
			throws BaseException;

	FatCompetencia obterUltimaCompetenciaModInternacao();

	String nameHeaderDownloadPDFRelatorioItensRealzIndv(String fileName,
			boolean hist) throws IOException;

	List<Long> obterListaCodTabelaFatSsm(Long codTabela, Integer idade,
			DominioSexoDeterminante sexo, Short paramTabelaFaturPadrao);

	void verificaCompatibilidadeConvenioComProcedimento(final Integer phiSeq,
			final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq)
			throws ApplicationBusinessException;

	public List<FatMotivoDesdobramento> listarMotivoDesdobramento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatMotivoDesdobramento motivoDesdobramento);

	public Long listarMotivoDesdobramentoCount(
			FatMotivoDesdobramento motivoDesdobramento);

	List<FatTipoAih> pesquisarTipoAih(Object parametro);

	Long pesquisarTipoAihCount(Object parametro);

	ImprimirVencimentoTributosVO recuperaTributosVencidos(
			DominioTipoTributo tipoTributo, Date dataApuracao) throws BaseException;

	List<FatCaractFinanciamento> pesquisarCaracteristicasFinanciamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final FatCaractFinanciamento filtro);

	Long pesquisarCaractFinanciamentosCount(final FatCaractFinanciamento filtro);

	void persistirCaracteristicasFinanciamento(
			final FatCaractFinanciamento entidade)
			throws ApplicationBusinessException, BaseException;

	void alterarSituacaoCaracteristicaFinanciamento(
			final FatCaractFinanciamento entidade)
			throws ApplicationBusinessException;	
	

	ArquivoURINomeQtdVO gerarCSVContasProcedProfissionalVinculoIncorreto() throws ApplicationBusinessException;

	ArquivoURINomeQtdVO gerarCSVContasNaoReapresentadasCPF(
			FatCompetencia competencia) throws ApplicationBusinessException;

	ArquivoURINomeQtdVO gerarCSVDadosContaNutricaoEnteral()
			throws ApplicationBusinessException;
	ArquivoURINomeQtdVO gerarCSVDadosContasComNPT(FatCompetencia competencia) throws ApplicationBusinessException;
	void inserirFatTipoCaractItens(FatTipoCaractItens entity);
	void atualizarFatTipoCaractItens(FatTipoCaractItens entity) throws ApplicationBusinessException;

	List<FatTipoCaractItens> pesquisarTiposCaractItensPorSeqCaracteristica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer seq, String caracteristica);

	Long pesquisarTiposCaractItensPorSeqCaracteristicaCount(Integer seq, String caracteristica);

	void excluirFatTipoCaractItensPorSeq(Integer seq) throws ApplicationBusinessException;		

	/**
	 * Realiza a atualização dos procedimentos relacionados a uma consulta, retornando uma flag indicando o sucesso da operação.
	 * 
	 * ORADB Procedure FATP_ATU_APAC_CON.
	 * 
	 * @param seqAtendimentoAntigo - Código do atendimento antigo
	 * @param seqAtendimentoNovo - Código do novo atendimento
	 * @param numeroConsulta - Número da consulta
	 * @param nomeMicrocomputador - Nome do micro que está realizando a operação
	 * @throws BaseException 
	 */
	void atualizarProcedimentosConsulta(Integer seqAtendimentoAntigo, Integer seqAtendimentoNovo, Integer numeroConsulta, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * Realiza a busca por Itens de Procedimentos transferidos, relacionados à Consulta informada.
	 * 
	 * @param numeroConsulta - Número da Consulta
	 * @return Lista de Itens de Conta
	 */
	List<FatItemContaApac> pesquisarItensProcedimentoTransferidoPorConsulta(Integer numeroConsulta);

	/**
	 * Realiza a busca pelos dados originais do FatProcedAmbRealizado alterado informado.
	 * 
	 * @param fatProcedAmbRealizado - Entidade alterada
	 * @return Entidade com dados antigos
	 */
	FatProcedAmbRealizado obterFatProcedAmbRealizadoOriginal (FatProcedAmbRealizado fatProcedAmbRealizado);

	/**
	 * Verifica se existe ao menos um registro de FatAtendimentoApacProcHosp com os códigos informados.
	 * 
	 * @param phiSeq - Código do Procedimento Interno
	 * @param codigoPaciente - Código do Paciente
	 * @return Flag indicando a existência do registro solicitado
	 */
	DominioSimNao verificarExistenciaProcedimentoAtendimento(Integer phiSeq, Integer codigoPaciente);
	
	List<AghClinicas> pesquisarClinicas(Object param);
	Long pesquisarClinicasCount(Object param) ;
	List<FatBancoCapacidadeVO> pesquisarBancosCapacidade(Integer mes, Integer ano, Integer numeroLeitos, Integer capacidade, Integer utilizacao, AghClinicas clinica, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	Long pesquisarBancosCapacidadeCount(Integer mes, Integer ano, Integer numeroLeitos, Integer capacidade, Integer utilizacao, AghClinicas clinica);
	FatBancoCapacidadeVO atualizarBancoCapacidade(final FatBancoCapacidadeVO vo) throws ApplicationBusinessException;
	
	List<FatCnesVO> pesquisarFatCnesPorSeqDescricao(Object param);
	
	List<FatUnidadeFuncionalCnesVO> pesquisarFatUnidadeFuncionalCnes(Short unfSeq);
	
	void inserirFatCnesUf(FatCnesUf entity);
	void deletarFatCnesUf(Short unfSeq, Integer fcsSeq, Short cnesSeq);
	FatServClassificacoes obterServClassificacoesPorChavePrimaria(Integer codigo);

	String geraCSVRelatorioRelacaoOrtesesProteses(Long procedimento,
			Integer ano, Integer mes, Date dtHrInicio, String iniciaisPaciente,
			Date dtIni, Date dtFim) throws IOException,
			ApplicationBusinessException;

	List<RelacaoDeOrtesesProtesesVO> obterRelacaoDeOrtesesProteses(
			Long procedimento, Integer ano, Integer mes, Date dtHrInicio,
			String iniciaisPaciente, Date dtIni, Date dtFim)
			throws ApplicationBusinessException;

	
	List<FatSaldoUTIVO> pesquisarBancosUTI(Integer mes, Integer ano, DominioTipoIdadeUTI tipoUTI, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	Long pesquisarBancosUTICount(Integer mes, Integer ano, DominioTipoIdadeUTI tipoUTI);
	FatSaldoUTIVO persistirBancoUTI(final FatSaldoUTIVO vo, final Boolean emEdicao) throws ApplicationBusinessException;
	
	ArquivoURINomeQtdVO obterDadosContaRepresentada(FatCompetencia competencia) throws ApplicationBusinessException;
	
	List<FatCompetencia> listarCompetenciaModuloMesAnoDtHoraInicioComHora(final FatCompetenciaId id);

	Long listarCompetenciaModuloMesAnoDtHoraInicioComHoraCount(FatCompetenciaId competenciaId);

	List<ContaApresentadaPacienteProcedimentoVO> obterContaApresentadaEspecialidade(Short codigoEspecialidade, FatCompetencia competencia) throws ApplicationBusinessException;
	
	String gerarCSVContaApresentadaEspMes(Short seqEspecialidade, FatCompetencia competencia) throws ApplicationBusinessException;
	
	String gerarCSVClinicaPorProcedimento(FatCompetencia competencia) throws BaseException;

	String geraCSVRelatorioAihsFaturadasPorClinica(Integer ano, Integer mes,
			Date dtHrInicio, String iniciaisPaciente) throws IOException,
			ApplicationBusinessException;

	List<AihsFaturadasPorClinicaVO> obterAihsFaturadasPorClinica(Integer ano, Integer mes, Date dtHrInicio,
			String iniciaisPaciente)
			throws ApplicationBusinessException;
			
	Long pesquisarFatCnesPorSeqDescricaoCount(String param);
	
	List<FatCaractComplexidade> pesquisarCaracteristicasDeComplexidade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FatCaractComplexidade caractComplexidade);

	Long pesquisarCaracteristicasDeComplexidadeCount(FatCaractComplexidade caractComplexidade);

	void persistirCaracteristicasDeComplexidade(final FatCaractComplexidade caractComplexidade, Boolean alteracao) throws BaseException;
	
	List<FatCaractComplexidade> listaCaracteristicasDeComplexidade(Object caractComplexidade);

	Long listaCaracteristicasDeComplexidadeCount(Object caractComplexidade);	
	/**
	 * Método que insere a possibilidade no banco de dados.
	 * @param possibilidadeRealizado
	 * @throws ApplicationBusinessException
	 */
	void persistirPossibilidadeRealizado(final FatPossibilidadeRealizado possibilidadeRealizado) throws ApplicationBusinessException;
	
	List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrder(
			final Object objPesquisa, final Short phoSeq);
	
	List<FatExcecaoPercentual> listarExcecaoPercentual(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FatExcecaoPercentual excecaoPercentual);
	
	Long listarExcecaoPercentualCount(FatExcecaoPercentual excecaoPercentual);
	
	void excluirExcecaoPercentual(FatExcecaoPercentual excecaoPercentual);
	
	void alterarExcecaoPercentual(FatExcecaoPercentual excecaoPercentual);
	
	void adicionarExcecaoPercentual(FatExcecaoPercentual excecaoPercentual) throws ApplicationBusinessException;
	
	public List<FtLogErrorVO> pesquisaFatLogErrorFatMensagensLog(Integer contaHospitalar,DominioSituacaoMensagemLog situacao,boolean administrarUnidadeFuncionalInternacao, boolean leituraCadastrosBasicosFaturamento
			,boolean  manterCadastrosBasicosFaturamento, final Short ichSeqp, final String erro,final Integer phiSeqItem1, final Long codItemSus1);
	
	public Long pesquisaFatLogErrorFatMensagensLogCount(Integer contaHospitalar,DominioSituacaoMensagemLog situacao,boolean administrarUnidadeFuncionalInternacao, boolean leituraCadastrosBasicosFaturamento
			,boolean  manterCadastrosBasicosFaturamento, final Short ichSeqp, final String erro,final Integer phiSeqItem1, final Long codItemSus1);
	
	/*
	 * thiago.cortes
	 Mensagem de Erro #2152
	*/
	public List<FatMensagemLog> listarMensagemErro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final FatMensagemLog fatMensagemLog);
	
	public void alterarMensagemErro(final FatMensagemLog fatMensagemErro) throws ApplicationBusinessException, BaseException;
	
	Long listarMensagensLogCount(FatMensagemLog fatMensagemLog);
	
	ArquivoURINomeQtdVO obterDadosGeracaoArquivoProdutividadeFisiatria(FatCompetencia competencia) throws ApplicationBusinessException;
	
	// ###### 41082 ######
	/**
	 * #41082 - consulta principal
	 * lista os procedimentos por tabela, item, codigo sus, 
	 * descricao e transplante
	 *  
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return List<FatItensProcedHospitalar>
	 * 
	 */
	List<FatItensProcedHospitalar> pesquisarProcedimentosHospitalaresTransplante(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final FatItensProcedHospitalar filtro);

	/**
	 * count do  pesquisarProcedimentosHospitalaresTransplante
	 */
	Long pesquisarProcedimentosHospitalaresTransplanteCount(final FatItensProcedHospitalar filtro);
	
	/**
	 * #41082 suggestionbox  transplante
	 */
	List<FatTipoTransplante> pesquisarProcedimentosTransplante(Object filtro);
	
	/**
	 * #41082 count do  pesquisarProcedimentosTransplante
	 */
	Long pesquisarProcedimentosTransplanteCount(Object filtro);

	/**
	 * #41082 persistência da inclusão de Procedimento Hospitalar com Transplante 
	 * @param FatItemProcHospTransp
	 */
	void persistirProcedimentoHospitalarComTransplante (
			FatItemProcHospTransp cadastro) throws BaseException;

	/**
	 * #41082 consulta suggestionBox Procedimentos Hospitalares
	 */
	List<FatItensProcedHospitalar> pesquisarProcedimentosHospitalaresComInternacao(
			Short seqTabela, String filtro);

	/**
	 * #41082 count do pesquisarProcedimentosHospitalaresComInternacao
	 */
	Long pesquisarProcedimentosHospitalaresComInternacaoCount(Short seqTabela, String filtro);

	/**
	 * #41082 exclusão de relacionamento de procedimento hospitalar com transplante
	 * @param FatItemProcHospTransp  
	 */
	void excluirFatItemHospTransp(FatItemProcHospTransp itemProcHospTransp);
	
	FatItemProcHospTransp obterDescricaoTransplantePorProcedHospitalar(
			FatItemProcHospTranspId id);
	
	public void atualizarItemProcedimentoHospitalarTransplante(
			FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph) throws BaseException;

	FatItemProcHospTransp pesquisarItemProcHospTranspPorId(
			FatItemProcHospTranspId id);

	void persistirItemProcedHospTransp(FatItemProcHospTransp cadastro, 
			FatItensProcedHospitalar procedRecuperado,
			FatItensProcedHospitalar entidade) throws BaseException;

	void atualizarItemProcedHospTransp(FatItemProcHospTransp cadastro) throws BaseException;
	
	List<SugestoesDesdobramentoVO> pesquisarSugestoesDesdobramento(DominioOrigemSugestoesDesdobramento origem, String incialPac);
	
	String gerarCSV(DominioOrigemSugestoesDesdobramento origem, String incialPac)throws IOException, BaseException;
	
	/**
	 * Obtem lista de {@link RateioValoresSadtPorPontosVO} para construção de relatorio.
	 * 
	 * @param dataHoraInicio {@link Date}
	 * @param ano {@link Integer}
	 * @param mes {@link Integer}
	 * @return {@link List} de {@link RateioValoresSadtPorPontosVO}
	 * @throws ApplicationBusinessException 
	 */
	List<RateioValoresSadtPorPontosVO> obterRateioValoresSadtPorPontos(final Date dataHoraInicio, final Integer ano, final Integer mes) throws ApplicationBusinessException;

	/**
	 * Gera CSV do relatorio de {@link RateioValoresSadtPorPontosVO}
	 * 
	 * @param competencia {@link FatCompetencia}
	 * @return {@link String}
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	String gerarCSVRelatorioRateioValoresSadtPorPontos(final FatCompetencia competencia) throws IOException, ApplicationBusinessException;
	
	/**
	 * Obtem indice que será multiplicado com os Pontos SADT da unidade e dividido pelo somatorio da coluna Pontos SADT
	 * 
	 * @param dataHoraInicio {@link Date}
	 * @param ano {@link Integer}
	 * @param mes {@link Integer}
	 * @return indice {@link BigDecimal}
	 */
	BigDecimal obterFatorMultiplicacaoParaValorRateado(final Date dataHoraInicio, final Integer ano, final Integer mes);

	/**
	 * Obtem lista de {@link FatCompetencia} para Suggestion Box.
	 * 
	 * @param id {@link FatCompetencia}
	 * @return {@link List} de {@link FatCompetencia}
	 */
	List<FatCompetencia> listarCompetenciaModuloParaSuggestionBox(final FatCompetenciaId id);

	/**
	 * Obtem count de lista de {@link FatCompetencia} para Suggestion Box.
	 * 
	 * @param id {@link FatCompetencia}
	 * @return {@link Long}
	 */
	Long listarCompetenciaModuloParaSuggestionBoxCount(final FatCompetenciaId id);

	List<ProtocolosAihsVO> getProtocolosAihs(Integer prontuario,
			String nomePaciente, Integer codpaciente, String leito,
			Integer conta, Date dtInternacao, Date dtAlta, Date dtEnvio,
			String envio) throws ApplicationBusinessException;

	void atualizarContaHospitalar(FatContasHospitalares newCtaHosp,
			FatContasHospitalares oldCtaHosp, boolean flush,
			String nomeMicrocomputador, Date dataFimVinculoServidor)
			throws BaseException;
	
	void atualizarContaHospitalarProtocolosAih(ProtocolosAihsVO item, boolean flush, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) throws BaseException;

	FatContasHospitalares obterProtocoloAihPorId(Integer seq);

	File geraCSVContas(String data1, String data2, FatCompetenciaId id)
			throws IOException, ApplicationBusinessException;

	File geraCSVRelatorioNutricaoEnteralDigitada(String data1, String data2,
			FatCompetenciaId id) throws IOException,
			ApplicationBusinessException;
	/*
	 * thiago.cortes 
	 * Diarias internação autorizadas #2146
	 */
	List<FatDiariaInternacao> listarDiariasInternacao(final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc, final FatDiariaInternacao fatDiariaInternacao);

	Long listarDiariasInternacaoCount(FatDiariaInternacao fatDiariaInternacao);
	void persistirDiariaInternacaoAutorizada(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException;
	void alterarDiariaInternacaoAutorizada(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException;
	void removerDiariaInternacaoAutorizada(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException;
	public List<FatValorDiariaInternacao> validarDiariaInternacao(final Integer DinSeq) throws ApplicationBusinessException;
	public List<FatValorDiariaInternacao> listarValorDiariaInternacao(final String orderProperty,
			final FatValorDiariaInternacao fatValorDiariaInternacao, FatDiariaInternacao fatDiariaInternacao);
	void adicionarValorDiariaInternacao(FatValorDiariaInternacao fatValorDIariaInternacao) throws ApplicationBusinessException;
	void alterarValorDiariaInternacao(FatValorDiariaInternacao fatValorDIariaInternacao) throws ApplicationBusinessException;
	
	void removerValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException;

	Collection<ClinicaPorProcedimentoVO> recupearColecaoRelatorioClinicaPorProcedimento(FatCompetencia competencia) throws BaseException;

	TotalGeralClinicaPorProcedimentoVO obterTotalGeralClinicaPorProcedimento(FatCompetencia competencia)  throws BaseException;

	List<PendenciasEncerramentoVO> getPendenciasEncerramento(Integer conta,
			Short itemConta, String erro, Integer prontuario, Date dtOperacao,
			String programa, DominioItemPendencia item, Short tabItem,
			Integer seqItem, Long sus, Integer hcpa)
			throws ApplicationBusinessException;

	List<PendenciaEncerramentoVO> obterDadosRelatorioLogInconsistenciaCarga(Date dataImpl);

	List<ProtocoloAihVO> listaProtocolosAih(Date data);

	ByteArrayOutputStream gerarArquivoSms(Date data) throws ApplicationBusinessException;

	Date obterUltimaDataCriacaoFatLogError();

	void processarRetornoSms(InputStream inputStream, String nomeMicrocomputador) throws BaseException;

	FatEspelhoEncerramentoPreviaVO obterFatEspelhoEncerramentoPreviaVO(Integer cthSeq);

	List<ProcedimentoRealizadoDadosOPMVO> obterListaDadosOPM(Integer cthSeq);
	
	List<ValoresPreviaVO> obterValoresPreviaVO(Integer cthSeq, AghParametros faturamentoPadrao);

	public List<AghCid> pesquisarCidsPorSSMDescricaoOuCodigo(Integer phiSeq,
			String descricao, Integer limiteRegistros);

	public Long pesquisarCidsPorSSMDescricaoOuCodigoCount(Integer phiSeq,String descricao);

	List<SigValorReceitaVO> obterValorEspelhoPelaInternacao(Integer intSeq,
			DominioSituacaoConta situacao, Integer eaiSeqp);

	List<SigValorReceitaVO> obterValoresReceitaAtosMedicos(
			List<Integer> listaCthSeq, DominioModoCobranca modoCobranca);

	List<AssociacaoProcedimentoVO> obterValoresProcedimentosAtravesRespectivosCodigosSus(
			Set<Integer> listaPhi, Set<Long> listaCodTabela); 
	
	public FatModalidadeAtendimento obterModalidadeAtivaPorCodigo(Short codigo);

	Map<String, Object> atualizarFinanciamento(List<String> lista) throws BaseException;

	FatMotivoCobrancaApac obterMotivoCobrancaPorCodSus(Byte codigoSus);

	List<ProcedimentoRealizadoDadosOPMVO> obterListaProcedimentoRealizados(Integer cthSeq);

	List<FatMotivoRejeicaoContasVO> obterDescricaoMotivosRejeicaoContaPorSeq(Integer seq);

	Short aghcModuloOnzeCompl(FatEspelhoEncerramentoPreviaVO objetoVO);

	String obterDescricaoNacionalidadePorCodigo(Integer codigo);

	String obterDescricaoEtniaPorId(Integer id);

	String obterDescricaoCidPorCodigo(String codigo);

	AinTiposCaraterInternacao obterTiposCaraterInternacaoPorSeq(Byte seq);

	Short fatcBuscaModalidade(Integer p_iph_seq, Short p_pho_seq, Date p_data_int, Date p_data_alta);

	boolean asuPosAtualizacaoStatement(FatDadosContaSemInt original, FatDadosContaSemInt modificada, String nomeComputador,
			Date dataFimVinculoServidor, Boolean substituirProntuario, RapServidores servidorLogado) throws BaseException;
	
	public Conv obterConvenioPorCodigo(Short codigo);
	
	public void atualizarConvenio(Conv convenio);

	public FatProcedAmbRealizado obterProcedAmbPorCirurgiaCancelada(Integer crgSeq);
	
	List<FatProcedAmbRealizado> pesquisarProcedAmbPorCirurgiaCancelada(Integer crgSeq);

	List<FatItemContaHospitalar> listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(final Integer cthSeq, DominioIndOrigemItemContaHospitalar origem);

	Short buscarFatItemContaHospitalarMaxSeq(Integer seq);
	
	public List<FatItensProcedHospitalar> listarItensProcedimentosHospitalares(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, FatItensProcedHospitalar itensProcedimentosHospitalares);
	
	public Long listarItensProcedimentosHospitalaresCount(FatItensProcedHospitalar itensProcedimentosHospitalares);
	
	public Long listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrderCount(
			final Object objPesquisa, final Short phoSeq);
	
	/**
	 * Metodo que lista os Faturamentos de Procedimentos e Possibilidades Associados.
	 * @param itemProcedimentoHospitalar
	 * @return
	 */
	public List<FatPossibilidadeRealizado> listarProcedimentosPossibilidadesAssociados(FatItensProcedHospitalar itemProcedimentoHospitalar);
	
	/**
	 * Metodo que remove a possibilidade no banco de dados.
	 * @param possibilidadeRealizado
	 * @throws BaseException
	 */
	void removerPossibilidadeRealizado(final FatPossibilidadeRealizado possibilidadeRealizado) throws BaseException;

	
	List<FatPacienteTransplantes> buscarPacientes(Integer pacienteCodigo);
		FatLaudoPacApac obterLaudoRelacionadoConsulta(Integer conNumero);

	LaudoSolicitacaoAutorizacaoProcedAmbVO obterProcedimentoSolicitado(Integer seq);

	ParametrosGeracaoLaudoOtorrinoVO obterParametrosGeracaoLaudoOtorrino(Integer conNumero);
	
	FatAutorizaApac obterFatAutorizaApacPorCpf(Long cpf);

	String getCnsResp(Long cpf) throws ApplicationBusinessException;
	
	String gerarCodigoBarras(String tipoCodigoBarras, String prontuario);

	List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorPrhConNumero(Integer numero);
	
	public Long obterCodTabelaPorPhi(Integer phiSeq, Short convenioSus,
			Byte planoSusAmbulatorio,
			Short tabelaFaturamentoUnificada);

	LaudoSolicitacaoAutorizacaoProcedAmbVO obterCandidatosApacOtorrino(ParametrosGeracaoLaudoOtorrinoVO parametrosGeracaoLaudoOtorrinoVO);

	List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedimentoCirurgicos(Integer pciSeq, String desc);

	FatItemContaHospitalar obterItemContaHospitalarLazyPorId(
			FatItemContaHospitalarId id);

	String pesquisarCodigoFormulaPaciente(Integer nroProntuario);
	
	public FatResumoApacs buscaResumo(Integer pacCodigo, Integer diagnostico);
	
	public FatCandidatosApacOtorrino buscarCandidato(Integer codigo, String candidato);

	public FatCandidatosApacOtorrino buscarCandidatoSemReavaliacao(Integer codigo);
	
	public String fatpCandidatoApacDesc(Integer consulta,Date dataRealizado) throws ApplicationBusinessException;

	List<FatResumoApacs> buscarDatasResumosApacsAtivos(Integer codigo, Date dthrRealizado);

	void verificaApacAutorizacao(Integer pacCodigo,Date data,Byte tipoTratamento) throws ApplicationBusinessException;

	Boolean encerrarContasHospitalares(Integer cth, String nomeMicrocomputador,
			Date dataFimVinculoServidor, AghJobDetail job) throws BaseException;

	void inserirFatProcedAmbRealizado(FatProcedAmbRealizado fatProcedAmbRealizado, String nomeMicrocomputador, Date dataFimVinculoServidor)
			throws BaseException;

	void inserirFatProcedAmbRealizadoAposVerificarEvolucaoEAnamnese(FatProcedAmbRealizado fatProcedAmbRealizado, String nomeMicrocomputador,
			Date dataFimVinculoServidor) throws BaseException;

	FatProcedHospInternos obterFatProcedHospInternosPorProcedEspecialDiversos(Short pedSeq);
	
	List<FatConvGrupoItemProced> pesquisarConvenioVerificarDuracaoTratamento(Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Integer phiSeq);
	
	FatSituacaoSaidaPaciente obterFatSituacaoSaidaPacientePorChavePrimaria(Object pk);
	
	Integer persistirContasCobradasEmLote(List<String> listCodigoDCHI, String servidorLogado, RapServidores servidorManuseado);
	
	List<VFatAssociacaoProcedimento> pesquisarAssociacaoProcedimentos(Integer phiSeq, Short convenio,Byte plano,Long codTabela);
	
	FatPacienteTransplantes obterFatPacienteTransplante(FatPacienteTransplantesId id);
	
	public boolean isPacienteTransplantado(AipPacientes paciente);
	
	//50635 - @ORADB Procedure AGH.FAT_DESFAZ_REINTERNACAO
	public void desfazerReintegracao(Integer prontuario, String nomeMicrocomputador) throws BaseException, ApplicationBusinessException;
	
	Integer removerPorCthModulo(final Integer cthSeq, final DominioModuloCompetencia modulo);

	FatContasInternacao buscaContaInternacao(Integer seqContaInternacao);

	RnCapcCboProcResVO rnCapcCboProcRes(Integer pSerMatricula,
			Short pSerVinCodigo, Integer pSoeSeq, Short pIseSeqp,
			Integer pConNumero, Integer pCrgSeq, Short pIphPhoSeq,
			Integer pIphSeq, Date pDtRealizacao,
			List<Short> resultSeqTipoInformacaoShort, Date ultimoDiaMes)
			throws ApplicationBusinessException;

	String recuperarProntuarioFormatado(Integer prontuario);

	FatConvGrupoItemProced obterFatConvGrupoItensProcedId(
			FatConvGrupoItemProcedId id);
	
	public AghParametros buscarParametroEditarDescPHI() throws BaseException;
	public List<FatRegistro> listarFatRegistroPorItensProcedimentoHospitalar(final Short iphPhoSeq, final Integer iphSeq);
}
